=================
辅助函数-Tap函数
=================

Tap 帮助函数
============

旧的实现方式
------------
Laravel 提出了一个 ``tap`` 功能。这是一个非常奇怪的功能，受 ``Ruby`` 的启发。这是 ``tap`` 助手功能的基本实现。

.. code-block:: php

    <?php
    function tap($value, $callback)
    {
        $callback($value);

        return $value;
    }

上面的代码将接受一个参数，它将使用该参数调用一个匿名函数。在调用回调函数后，它将返回参数。
让我们看看我们如何以有意义的方式使用它。例如：

.. code-block:: php

    <?php

    return tap(App\Photo::find(1), function($photo) {
        $photo->validated = true;
        $photo->save();
    });

在上面的例子中，我们传递一个参数（照片模型）和一个回调函数，该函数简单地将 ``validated`` 设置为 ``true`` 并保存模型。这个函数然后将照片模型实例返回给调用者。

新的实现方式
------------
在最新版本的 Laravel 5.4 和 Laravel 5.5 中，更高级的 ``tap`` 来了。它引入了更短的使用方式。这里是 ``tap`` 函数的新实现。

.. code-block:: php

    <?php
    function tap($value, $callback = null)
    {
        if (is_null($callback)) {
            return new HigherOrderTapProxy($value); // 可以保持对象方法链式调用
        }

        $callback($value);

        return $value;
    }

回调函数现在是可选的。你还可以链式使用参数中的多个方法，这里其实也就是照片 ``Model`` 中支持的方法。例如

.. code-block:: php

    <?php

    $photo = App\Photo::find(1);

    return tap($photo)->update([
        'validated' => 'true',
    ])

我们能够将任何模型的方法通过 ``tap`` 链式调用。此更新方法通常返回 ``true`` 或 ``false`` ，但是这里使用了 ``tap`` 函数。在这种情况下，它将返回照片模型。 ``tap`` 可以帮助你返回作为参数传递的对象。

它是如何工作的
--------------
``tap`` 是一个非常有用的功能，但有时它很难理解它是如何工作的。 这里来解释它是如何工作的。

如果没有给出回调函数，因为它是可选的，Laravel将返回 ``HigherOrderTapProxy`` 的新实例。 在 ``HigherOrderTapProxy`` 类中定义了调用魔术方法。 调用魔术方法是由语言动态调用的(所谓的方法在类中没有定义)。 因为除了调用魔术方法， ``HigherOrderTapProxy`` 类中没有定义方法，所以每次使用 ``tap`` 函数任何方法调用时都会调用它。 在调用魔术方法中，我们的更新方法或任何我们调用的方法将通过对参数方法调用来完成，并且它将返回我们最初传递给 ``tap`` 函数的参数。

这里是 ``HigherOrderTapProxy`` 类中调用魔术方法的实际内容。

.. code-block:: php

    <?php
    // vendor/laravel/framework/src/Illuminate/Support/HigherOrderTapProxy.php
    public function __call($method, $parameters)
    {
        $this->target->{$method}(...$parameters);

        return $this->target;
    }

在上面的代码中， ``target`` 属性是我们在 ``tap`` 中传递的参数。

Laravel collection 中的 tap 方法
================================
Laravel 还在 ``collection`` 类中有一个 ``tap`` 方法，可让你在特定的地方传入参数到 ``tap`` 中，并对这些结果进行处理。 ``tap`` 不会影响主要 ``collection`` 的结果。 这对调试代码和查找在处理集合时出现错误的地方很有帮助。我们用一个例子来解释这个方法。初始化以下数组。

.. code-block:: php

    <?php
    $photos = [
        ['file_name' => 'wallpaper', 'validated' => true, 'extension' => 'jpg'],
        ['file_name' => 'spring', 'validated' => true, 'extension' => 'png'],
        ['file_name' => 'flowers', 'validated' => false, 'extension' => 'jpg'],
        ['file_name' => 'mac', 'validated' => true, 'extension' => 'png'],
        ['file_name' => 'books', 'validated' => false, 'extension' => 'jpg'],
        ['file_name' => 'mobiles', 'validated' => false, 'extension' => 'jpg'],
        ['file_name' => 'glass', 'validated' => false, 'extension' => 'png'],
        ['file_name' => 'fruit', 'validated' => true, 'extension' => 'jpg'],
    ];

现在让我们尝试在这个数组上使用 ``tap`` 方法。首先，我们必须将这个数组转换为一个集合，然后在特定点处 ``tap`` 这个集合。

.. code-block:: php

    <?php
    return collect($photos)
        ->where('validated', true)
        ->tap(function ($validated) {
            return var_dump($validated->pluck('file_name'));
        });
    });

上面的代码将会输出以下结果：

.. code-block:: shell

    wallpaper
    spring
    mac
    fruit

tap VS Pipe(管道)
=================
在 Laravel 中，也有类似的方法叫管道。它们在某种意义上是相似的，因为它们都在集合管道中使用。 ``tap`` 和 ``pipe`` 之间有一个区别。 ``tap`` 允许你使用数据，但不会修改原始返回值。 另一方面， ``pipe`` 根据返回值修改数据。

例如：

.. code-block:: php

    <?php
    return collect($photos)
        ->where('validated', true)
        ->pipe(function ($validated) {
            return $validated->where('extension', 'jpg')->pluck('file_name');
        });
    });

输出结果为：

.. code-block:: shell

    wallpaper
    fruit

另一方面，如果我们像这样使用上面的代码：

.. code-block:: php

    <?php
    return collect($photos)
        ->where('validated', true)
        ->tap(function ($validated) {
            return $validated->where('extension', 'jpg')->pluck('file_name');
        });
    });

它将返回验证设置为 ``true`` 的所有照片数组。

结果为：

.. code-block:: shell

    0: {
        file_name: "wallpaper",
        validated: true,
        extension: "jpg"
    },
    1: {
        file_name: "spring",
        validated: true,
        extension: "png"
    },
    3: {
        file_name: "mac",
        validated: true,
        extension: "png"
    },
    7: {
        file_name: "fruit",
        validated: true,
        extension: "jpg"
    }









