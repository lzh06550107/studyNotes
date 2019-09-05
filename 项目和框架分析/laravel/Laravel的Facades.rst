*******
Facades
*******

简介
====
``Facades`` （读音： ``/fəˈsäd/`` ）为应用程序的 服务容器 中可用的类提供了一个「静态」接口。 Laravel 自带了很多 ``Facades`` ，可以访问绝大部分 Laravel 的功能。 Laravel Facades 实际上是服务容器中底层类的「静态代理」，它提供了简洁而富有表现力的语法，甚至比传统的静态方法更具可测试性和扩展性。

所有的 Laravel Facades 都在 ``Illuminate\Support\Facades`` 命名空间中定义。所以，我们可以轻松地使用 ``Facade``  :

.. code-block:: php

    <?php
    use Illuminate\Support\Facades\Cache;

    Route::get('/cache', function () {
        return Cache::get('key');
    });

在 Laravel 的文档中，很多示例代码都会使用 ``Facades`` 来演示框架的各种功能。

何时使用 Facades
================
``Facades`` 有很多好处， 它为我们使用 Laravel 的功能提供了简单、易记的语法，而无需记住必须手动注入或配置的长长的类名。此外，由于他们对 PHP 动态方法的独特用法，使得测试起来非常容易。

然而，在使用 ``Facades`` 时，有些地方需要特别注意。使用 ``Facades`` 最主要的风险就是会引起类作用范围的膨胀。因为 ``Facades`` 使用起来非常简单且不需要注入，就会使得我们不经意间在单个类中使用许多 ``Facades`` ，从而导致类变的越来越大。而使用依赖注入的时候，使用的类越多，构造方法就会越长，在视觉上就会引起注意，提醒你这个类有些庞大了。 因此在使用 ``Facades`` 的时候，要特别注意控制类的大小，让类的作用范围保持短小。

.. note:: 在开发与 Laravel 进行交互的第三方扩展包时， 建立最好选择注入 Laravel 契约 ，而不是使用 ``Facades`` 的方法来使用类。因为扩展包是在 Laravel 本身之外构建，所以你无法使用 Laravel Facades 测试辅助函数。

Facades Vs. 依赖注入
--------------------
依赖注入的主要优点之一是切换注入类的实现的能力。 这在测试的时候很有用，因为你可以注入一个 mock 或者 stub ，并断言在 stub 上调用的各种方法。

通常，真正的静态方法是不可能被 mock 或者 stub 。但是，因为 Facades 使用动态方法来代理从服务容器解析的对象的方法调用，我们可以像测试注入的类实例一样来测试 Facades。例如，像下面的路由：



