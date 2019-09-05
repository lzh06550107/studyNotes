=================
辅助函数-其它杂项
=================

其它函数
========

.. contents:: 目录
   :depth: 4

分类
----

abort：函数抛出 异常处理 程序呈现的 HTTP 异常
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
如果传入 404 则抛出 ``NotFoundHttpException`` 异常，其它参数，抛出 ``HttpException`` 异常。

第一个参数是响应码；第二个参数是消息内容；第三个参数是响应头；

.. code-block:: php

    <?php
    abort(403);

你也可以提供额外的响应文本和自定义响应标头：

.. code-block:: php

    <?php
    abort(403, 'Unauthorized.', $headers);

abort_if：如果给定的布尔表达式计算结果为 true， abort_if 函数将抛出一个 HTTP 异常
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    abort_if(! Auth::user()->isAdmin(), 403);

和 ``abort`` 方法一样，你也可以提供异常的响应文本作为第三个参数，并提供一个自定义响应头数组作为第四个参数。该方法底层调用 ``abort()`` 函数。

abort_unless：如果给定的布尔表达式计算结果为 false，abort_unless 函数将抛出一个 HTTP 异常
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    abort_unless(Auth::user()->isAdmin(), 403);

和 ``abort`` 方法一样，你也可以提供异常的响应文本作为第三个参数，并提供一个自定义响应头数组作为第四个参数。


app：函数返回 服务容器 实例
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $container = app();

你可以传递一个类或接口名称来从容器中解析它：

.. code-block:: php

    <?php
    $api = app('HelpSpot\API');

auth：函数返回一个 认证 实例
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
为了方便起见，你可以使用它来替代 ``Auth Facade`` ：

.. code-block:: php

    <?php
    $user = auth()->user();

如果需要，你可以指定你想要访问的认证实例：

.. code-block:: php

    <?php
    $user = auth('admin')->user();

back：函数生成一个重定向 HTTP 响应到用户之前的位置
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
.. code-block:: php

    <?php
    return back($status = 302, $headers = [], $fallback = false);

    return back();

bcrypt 哈希 使用 Bcrypt 对给定的值进行散列
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
你可以使用它替代 Hash facade：

.. code-block:: php

    <?php
    $password = bcrypt('my-secret-password');

broadcast 函数将广播给定的事件到它的监听器
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    broadcast(new UserRegistered($user));

blank：函数判断给定的值是否为「空」
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    blank('');
    blank('   ');
    blank(null);
    blank(collect());

    // true

    blank(0);
    blank(true);
    blank(false);

    // false

要使用与 ``blank`` 相反的功能，请看 ``filled`` 方法。

cache：函数可以用来从缓存中获取值
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
如果缓存中不存在给定的健，则返回一个可选的默认值。

.. code-block:: php

    <?php
    $value = cache('key');

    $value = cache('key', 'default');

你可以通过将一组键/值对传递给函数来将其添加到缓存中。与此同时，你还应该传递有效的分钟数或持续时间作为缓存过期时间：

.. code-block:: php

    <?php
    cache(['key' => 'value'], 5);

    cache(['key' => 'value'], now()->addSeconds(10));

``class_uses_recursive`` 函数返回一个类使用的所有 ``traits`` ，包括任何子类使用的 ``traits`` ：

.. code-block:: php

    <?php
    $traits = class_uses_recursive(App\User::class);

collect：函数根据给定的数组创建一个集合实例
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $collection = collect(['taylor', 'abigail']);

config：函数获取配置变量的值
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
可以使用「点」语法访问配置值，其中包括文件的名称和希望访问的选项。如果配置选项不存在，则可以指定一个默认值并返回：

.. code-block:: php

    <?php
    $value = config('app.timezone');

    $value = config('app.timezone', $default);

可以在运行时通过传递一组键/值对来设置配置变量：

.. code-block:: php

    <?php
    config(['app.debug' => true]);

cookie：函数创建一个新的 cookie 实例
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $cookie = cookie('name', 'value', $minutes);

csrf_field：函数生成包含 CSRF 令牌值的 HTMLhidden 表单字段
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
例如，使用 ``Blade`` 语法：

.. code-block:: php

    <?php
    {{ csrf_field() }}

csrf_token：函数获取当前 CSRF 令牌的值
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $token = csrf_token();

dd：函数输出给定的值并结束脚本运行
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    dd($value);

    dd($value1, $value2, $value3, ...);

如果你不想终止脚本运行，请改用 ``dump`` 函数。

decrypt：函数使用 Laravel 的加密器来解密给定的值
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $decrypted = decrypt($encrypted_value);

dispatch：函数将给定的任务推送到 Laravel 任务列队中
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    dispatch(new App\Jobs\SendEmails);

dispatch_now：函数立即运行给定的任务，并从其 handle 方法返回值
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $result = dispatch_now(new App\Jobs\SendEmails);

dump：函数打印给定的变量
^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    dump($value);

    dump($value1, $value2, $value3, ...);

如果要在打印变量后停止执行脚本，请改用 ``dd`` 函数。

encrypt：函数使用 Laravel 的加密器对给定的值进行加密
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $encrypted = encrypt($unencrypted_value);

env：函数获取环境变量的值或者返回默认值
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $env = env('APP_ENV');

    // 如果环境变量不存在则返回默认值...
    $env = env('APP_ENV', 'production');

.. note:: 如果在你在部署过程中执行 ``config:cache`` 命令，则应该保证只在配置中调用 ``env`` 函数。一旦配置被缓存， ``.env`` 文件则不会再被加载，所有对 ``env`` 函数的调用都将返回 ``null`` 。

event：函数将给定的事件分派给它的监听器
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    event(new UserRegistered($user));

factory：函数根据给定的类、名称和数量创建一个模型工厂构建器
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
可以在测试或数据填充中使用：

.. code-block:: php

    <?php
    $user = factory(App\User::class)->make();

filled：函数判断给定的值是否不为「空」
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    filled(0);
    filled(true);
    filled(false);

    // true

    filled('');
    filled('   ');
    filled(null);
    filled(collect());

    // false

要使用与 ``filled`` 相反的功能，请看 ``blank`` 方法。

info：函数将信息写入日志

.. code-block:: php

    <?php
    info('一些有用的信息！');

有上下文关系的数组也可以传递给函数：

.. code-block:: php

    <?php
    info('用户登录尝试失败。', ['id' => $user->id]);


logger：函数可以将一个 debug 级别的消息写入到日志中
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    logger('Debug 消息');

有上下文关系的数组也可以传递给函数：

.. code-block:: php

    <?php
    logger('User has logged in.', ['id' => $user->id]);

如果没有传值给函数则返回日志的实例：

.. code-block:: php

    <?php
    logger()->error('You are not allowed here.');

method_field：函数生成一个 HTML hidden 表单字段，其中包含表单的 HTTP 动作的欺骗值
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
例如，使用 ``Blade`` 语法：

.. code-block:: php

    <?php
    <form method="POST">
        {{ method_field('DELETE') }}
    </form>

now：函数为当前时间创建一个新的 ``Illuminate\Support\Carbon`` 实例
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $now = now(); // 返回日期和时间

old：函数 获取 会话中闪存的 旧输入 值
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $value = old('value');

    $value = old('value', 'default');

optional：函数可以接受任何参数，并且允许你访问该对象的属性或者调用方法
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
如果给定的对象是 ``null`` ， 那么属性和方法会简单地返回 ``null`` 而不是产生一个错误：

.. code-block:: php

    <?php
    return optional($user->address)->street;

    {!! old('name', optional($user)->name) !!}

policy：方法为给定的类获取一个策略实例
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $policy = policy(App\User::class);

redirect：函数返回一个重定向 HTTP 响应，如果没有没有传入参数，则返回重定向实例
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    return redirect($to = null, $status = 302, $headers = [], $secure = null);

    return redirect('/home');

    return redirect()->route('route.name');

report：函数将使用异常处理程序的 report 方法抛出异常
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    report($e);

request：函数返回当前请求实例或者获取输入项
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $request = request();

    $value = request('key', $default);

rescue：函数执行给定的闭包并捕获执行期间发生的任何异常
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
所有被捕获的异常将被发送到你的异常处理程序的 ``report`` 方法。要注意的是，该请求将继续处理：

.. code-block:: php

    <?php
    return rescue(function () {
        return $this->method();
    });

你也可以将第二个参数传递给 ``rescue`` 方法。如果在执行闭包时发生异常，这个参数将是应该返回的默认值：

.. code-block:: php

    <?php
    return rescue(function () {
        return $this->method();
    }, false);

    return rescue(function () {
        return $this->method();
    }, function () {
        return $this->failure();
    });


resolve：函数使用服务容器将给定的类或接口名称解析为其实例
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $api = resolve('HelpSpot\API');

response：函数创建响应实例或者获取响应工厂实例
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
.. code-block:: php

    <?php
    return response('Hello World', 200, $headers);

    return response()->json(['foo' => 'bar'], 200, $headers);

retry：函数尝试执行给定的回调，直到到达给定的最大尝试次数
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
如果回调没有抛出异常，则返回值将被返回。如果回调抛出异常，它将自动重试。如果超过最大尝试次数，则会抛出异常：

.. code-block:: php

    <?php
    return retry(5, function () {
        // 在 100ms 左右尝试 5 次...
    }, 100);

session：函数可以用来获取或者设置 Session 值
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $value = session('key');

你可以通过将一组键/值对传递给该函数来设置值：

.. code-block:: php

    <?php
    session(['chairs' => 7, 'instruments' => 3]);

如果没有传递值给函数，则返回 ``Session`` 实例：

.. code-block:: php

    <?php
    $value = session()->get('key');

    session()->put('key', $value);

tap：函数接受两个参数：一个任意的 $value 和一个闭包
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``$value`` 将被传递给闭包，然后由 ``tap`` 函数返回。不需要在闭包中使用 ``return`` 返回值。

.. code-block:: php

    <?php
    $user = tap(User::first(), function ($user) {
        $user->name = 'taylor';

        $user->save();
    });

如果没有闭包被传递给 ``tap`` 函数，你可以调用给定 ``$value`` 的任何方法。而你调用的方法的返回值始终为 ``$value`` ，无论方法在其定义中实际返回的是什么。例如， ``Eloquent`` 的 ``update`` 方法通常会返回一个整数。但是，我们可以强制通过 ``tap`` 函数链式调用 ``update`` 方法来返回模型本身：

.. code-block:: php

    <?php
    $user = tap($user)->update([
        'name' => $name,
        'email' => $email,
    ]);

today：函数为当前日期创建一个新的 ``Illuminate\Support\Carbon`` 实例
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $today = today(); //仅返回日期，不包括时间

throw_if：如果给定的布尔表达式计算结果为 true，throw_if 函数抛出给定的异常
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    throw_if(! Auth::user()->isAdmin(), AuthorizationException::class);

    throw_if(
        ! Auth::user()->isAdmin(),
        AuthorizationException::class,
        'You are not allowed to access this page'
    );

throw_unless：如果给定的布尔表达式计算结果为 false，则 throw_unless 函数会抛出给定的异常
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    throw_unless(Auth::user()->isAdmin(), AuthorizationException::class);

    throw_unless(
        Auth::user()->isAdmin(),
        AuthorizationException::class,
        'You are not allowed to access this page'
    );

trait_uses_recursive：函数返回一个类使用的所有 trait
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $traits = trait_uses_recursive(\Illuminate\Notifications\Notifiable::class);

transform：如果给定的值不为 blank 并且传入 Closure，那么 transform 函数对给定的值执行 Closure 并返回其结果
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $callback = function ($value) {
        return $value * 2;
    };

    $result = transform(5, $callback);

    // 10

默认值或 ``Closure`` 也可以作为方法的第三个参数传递。如果给定值为空白，则返回该值：

.. code-block:: php

    <?php
    $result = transform(null, $callback, 'The value is blank');

    // The value is blank

validator：函数用给定的参数创建一个新的验证器实例
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

为方便起见，你可以使用它来代替 ``Validator facade`` ：

.. code-block:: php

    <?php
    $validator = validator($data, $rules, $messages);

value：函数返回给定的值
^^^^^^^^^^^^^^^^^^^^^^^
但是，如果将一个 ``Closure`` 传递给该函数，则将执行该 ``Closure`` 并返回其结果：

.. code-block:: php

    <?php
    $result = value(true);

    // true

    $result = value(function () {
        return false;
    });

    // false

view：函数获取一个视图实例
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    return view('auth.login');

with：函数会返回给定的值
^^^^^^^^^^^^^^^^^^^^^^^^
如果传入一个 ``Closure`` 作为该函数的第二个参数，会返回 ``Closure`` 执行的结果：

.. code-block:: php

    <?php
    $callback = function ($value) {
        return (is_numeric($value)) ? $value * 2 : 0;
    };

    $result = with(5, $callback);

    // 10

    $result = with(null, $callback);

    // 0

    $result = with(5, null);

    // 5

