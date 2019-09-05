****
请求
****

获取请求
========
要通过依赖注入的方式来获取当前 ``HTTP`` 请求的实例，你应该在控制器方法中引入 ``Illuminate\Http\Request`` 类。传入的请求实例将通过 服务容器 自动注入:

.. code-block:: php

    <?php

    namespace App\Http\Controllers;

    use Illuminate\Http\Request;

    class UserController extends Controller
    {
        /**
         * 存储一个新的用户。
         *
         * @param  Request  $request
         * @return Response
         */
        public function store(Request $request)
        {
            $name = $request->input('name');

            //
        }
    }

依赖注入
---------

控制器依赖注入
^^^^^^^^^^^^^^
如下所示使用提示类 ``Illuminate\Http\Request`` ，就可以在控制器方法中获得请求对象。

.. code-block:: php

    <?php
    namespace App\Http\Controllers;

    use Illuminate\Http\Request;

    class UserController extends Controller
    {
        /**
         * 更新某个用户
         *
         * @param  Request  $request
         * @param  string  $id
         * @return Response
         */
        public function update(Request $request, $id)
        {
            //
        }
    }


闭包依赖注入
^^^^^^^^^^^^
你也同样可以在路由闭包中使用 ``Illuminate\Http\Request`` 类。程序在执行的时候，服务容器会自动的将请求注入到路由闭包中：


请求路径和方法
==============
``Illuminate\Http\Request`` 实例为你的应用程序提供了一系列方法检查路由，并且继承了 ``Symfony\Component\HttpFoundation\Request`` 类。 以下是该类的一些重要用法：

获取请求路径
------------
``path`` 方法返回请求的路径信息。也就是说，如果传入的请求的目标地址是 ``http://domain.com/foo/bar`` ，那么 ``path`` 将会返回 ``foo/bar`` ：

.. code-block:: php

    <?php
    $uri = $request->path();

``is`` 方法可以验证传入的请求路径和指定规则是否匹配。使用这个方法的时，你也可以传递一个 ``*`` 字符作为通配符：

.. code-block:: php

    <?php
    if ($request->is('admin/*')) {
        //
    }

获取请求的 URL
--------------
你可以使用 ``url`` 或 ``fullUrl`` 方法去获取传入请求的完整 ``URL`` 。 ``url`` 方法返回不带有查询字符串的 ``URL`` ，而 ``fullUrl`` 方法的返回值包含查询字符串：

.. code-block:: php

    <?php
    // 没有查询字符串...
    $url = $request->url();

    // 使用查询字符串...
    $url = $request->fullUrl();

获取请求方法
------------
对于传入的请求 ``method`` 方法将返回 ``HTTP`` 的请求方式。你也可以使用 ``isMethod`` 方法去验证 ``HTTP`` 的请求方式与指定规则是否相配：

.. code-block:: php

    <?php
    $method = $request->method();

    if ($request->isMethod('post')) {
        //
    }

PSR-7 请求
==========
`PSR-7 标准 <http://www.php-fig.org/psr/psr-7/>`_ 标准 规定的 ``HTTP`` 消息接口包含了请求和响应。如果你想使用一个 ``PSR-7`` 请求来代替一个 Laravel 请求实例，那么你首先要安装几个函数库。 Laravel 使用 ``Symfony`` 的 ``HTTP`` 消息桥接组件将典型的 Laravel 请求和响应转换为 ``PSR-7`` 兼容实现：

.. code-block:: shell

    composer require symfony/psr-http-message-bridge
    composer require zendframework/zend-diactoros

安装完这些库后， 就可以在路由闭包或控制器中类型提示请求的接口来获取 ``PSR-7`` 请求：

.. code-block:: php

    <?php
    use Psr\Http\Message\ServerRequestInterface;

    Route::get('/', function (ServerRequestInterface $request) {
        //
    });

.. tip:: 如果从路由或者控制器返回 ``PSR-7`` 响应实例，它会自动转换回 Laravel 响应实例，并由框架显示。

输入预处理 & 规范化
===================
默认情况下，Laravel 在应用程序的全局中间件堆栈中包含了 ``TrimStrings`` 和 ``ConvertEmptyStringsToNull`` 两个中间件。这些中间件由 ``App\Http\Kernel`` 类列在堆栈中。它们会自动处理请求上所有传入的字符串字段，并将空的字符串字段转变成 ``null`` 值。这样你就不用再担心路由和控制器中数据规范化的问题。

如果你想停用这些功能，可以从应用程序的中间件堆栈中删除这两个中间件，只需在 ``App\Http\Kernel`` 类的 ``$middleware`` 属性中移除它们。

获取输入
=========
获取所有输入数据
----------------
你可以使用 ``all`` 方法以 ``array`` 形式获取到所有输入数据:

.. code-block:: php

    <?php
    $input = $request->all();

获取指定输入值
--------------
使用几种简单的方法（不需要特别指定哪个 ``HTTP`` 动作），就可以访问 ``Illuminate\Http\Request`` 实例中所有的用户输入。也就是说无论是什么样的 ``HTTP`` 动作， ``input`` 方法都可以被用来获取用户输入数据：

.. code-block:: php

    <?php
    $name = $request->input('name');

你可以给 ``input`` 方法的第二个参数传入一个默认值。如果请求的输入值不存在请求上，就返回默认值：

.. code-block:: php

    <?php
    $name = $request->input('name', 'Sally');

如果传输表单数据中包含「数组」形式的数据，那么可以使用「点」式语法来获取数组：

.. code-block:: php

    <?php
    $name = $request->input('products.0.name');

    $names = $request->input('products.*.name');

从查询字符串获取输入
---------------------
使用 ``input`` 方法可以从整个请求中获取输入数据（包括查询字符串），而 ``query`` 方法可以只从查询字符串中获取输入数据：

.. code-block:: php

    <?php
    $name = $request->query('name');

如果请求的查询字符串数据不存在，则将返回这个方法的第二个参数：

.. code-block:: php

    <?php
    $name = $request->query('name', 'Helen');

你可以不提供参数调用 ``query`` 方法来以关联数组的形式检索所有查询字符串值：

.. code-block:: php

    <?php
    $query = $request->query();

通过动态属性获取输入
---------------------
你也可以通过 ``Illuminate\Http\Request`` 实例的动态属性来获取用户输入。例如，如果你应用的表单中包含 ``name`` 字段，那么可以像这样访问该字段的值：

.. code-block:: php

    <?php
    $name = $request->name;

Laravel 在处理动态属性的优先级是，先在请求的数据中查找，如果没有，再到路由参数中查找。

获取 JSON 输入信息
------------------
如果发送到应用程序的请求数据是 ``JSON`` ，只要请求的 ``Content-Type`` 标头正确设置为 ``application/json`` ，就可以通过 ``input`` 方法访问 ``JSON`` 数据。你甚至可以使用 「点」式语法来读取 ``JSON`` 数组：

.. code-block:: php

    <?php
    $name = $request->input('user.name');

获取部分输入数据
----------------
如果你需要获取输入数据的子集，则可以用 ``only`` 和 ``except`` 方法。这两个方法都接收 ``array`` 或动态列表作为参数：

.. code-block:: php

    <?php
    $input = $request->only(['username', 'password']);

    $input = $request->only('username', 'password');

    $input = $request->except(['credit_card']);

    $input = $request->except('credit_card');

.. tip:: ``only`` 方法会返回所有你指定的键值对，但不会返回请求中不存在的键值对。

确定是否存在输入值
------------------
要判断请求是否存在某个值，可以使用 ``has`` 方法。如果请求中存在该值， ``has`` 方法就会返回 ``true`` ：

.. code-block:: php

    <?php
    if ($request->has('name')) {
        //
    }

当提供一个数组作为参数时， ``has`` 方法将确定是否存在数组中所有给定的值：

.. code-block:: php

    <?php
    if ($request->has(['name', 'email'])) {
        //
    }

如果你想确定请求中是否存在值并且不为空，可以使用 ``filled`` 方法：

.. code-block:: php

    <?php
    if ($request->filled('name')) {
        //
    }

旧输入
======
Laravel 允许你将本次请求的数据保留到下一次请求发送前。如果第一次请求的表单不能通过验证，就可以使用这个功能重新填充表单。但是，如果你使用了 Laravel 的 验证功能，你就不需要在手动实现这些方法，因为 Laravel 内置的验证工具会自动调用他们。

将输入闪存至 Session
--------------------
``Illuminate\Http\Request`` 的 ``flash`` 方法会将当前输入的数据存进 ``session`` 中，以便在用户下次发送请求到应用程序之前可以使用它们：

.. code-block:: php

    <?php
    $request->flash();

你也可以使用 ``flashOnly`` 和 ``flashExcept`` 方法将请求数据的一部分闪存到 ``session`` 。这些方法对敏感信息（例如密码）的保护非常有用：

.. code-block:: php

    <?php
    $request->flashOnly(['username', 'email']);

    $request->flashExcept('password');

闪存输入后重定向
----------------
你可能需要把输入闪存到 ``session`` 然后重定向到上一页，这时只需要在重定向方法后加上 ``withInput`` 即可：

.. code-block:: php

    <?php
    return redirect('form')->withInput();

    return redirect('form')->withInput(
        $request->except('password')
    );

获取旧输入
----------
若要获取上一次请求中闪存的输入，则可以使用 ``Request`` 实例中的 ``old`` 方法。 ``old`` 方法会从 ``Session`` 取出之前被闪存的输入数据：

.. code-block:: php

    <?php
    $username = $request->old('username');

Laravel 也提供了全局辅助函数 ``old`` 。如果你要在 ``Blade`` 模板 中显示旧的输入，使用 ``old`` 会更加方便。如果给定字段没有旧的输入，则返回 ``null`` ：

.. code-block:: html

    <input type="text" name="username" value="{{ old('username') }}">

Cookies
=======
从请求中获取 ``Cookie``
-----------------------
Laravel 框架创建的每个 ``cookie`` 都会被加密并使用验证码进行签名，这意味着如果客户端更改了它们，便视为无效。若要从请求中获取 ``cookie`` 值，你可以在 ``Illuminate\Http\Request`` 实例上使用 ``cookie`` 方法：

.. code-block:: php

    <?php
    $value = $request->cookie('name');

或者，您可以使用 ``Cookie Facade`` 来访问 ``cookie`` 的值：

.. code-block:: php

    <?php
    $value = Cookie::get('name');

将 Cookies 附加到响应
---------------------
你可以使用 ``cookie`` 方法将 ``cookie`` 附加到传出的 ``Illuminate\Http\Response`` 实例。你需要传递 ``Cookie`` 名称、值、以及有效期（分钟）到这个方法：

.. code-block:: php

    <?php
    return response('Hello World')->cookie(
        'name', 'value', $minutes
    );

``cookie`` 方法还接受一些不太频繁使用的参数。通常，这些参数与 ``PHP`` 原生 ``setcookie`` 方法的参数具有相同的目的和意义：

.. code-block:: php

    <?php
    return response('Hello World')->cookie(
        'name', 'value', $minutes, $path, $domain, $secure, $httpOnly
    );

或者，您也可以使用 ``Cookie Facade`` 来 ``queue Cookie`` ，以将其与应用程序的传出响应连接起来。 ``queue`` 方法接受 ``Cookie`` 实例或创建 ``Cookie`` 实例所需的参数。在发送到浏览器之前，这些 ``cookie`` 将被附加到输出响应：

.. code-block:: php

    <?php
    Cookie::queue(Cookie::make('name', 'value', $minutes));

    Cookie::queue('name', 'value', $minutes);

生成 Cookie 实例
----------------
如果你想要在一段时间以后生成一个可以给定 ``Symfony\Component\HttpFoundation\Cookie`` 的响应实例，你可以使用全局辅助函数 ``cookie`` 。除非此 ``cookie`` 被附加到响应实例，否则不会发送回客户端：

.. code-block:: php

    <?php
    $cookie = cookie('name', 'value', $minutes);

    return response('Hello World')->cookie($cookie);

文件
====

获取上传文件
------------
你可以使用 ``file`` 方法或使用动态属性从 ``Illuminate\Http\Request`` 实例中访问上传的文件。该 ``file`` 方法返回一个 ``Illuminate\Http\UploadedFile`` 类的实例，该类继承了 ``PHP`` 的 ``SplFileInfo`` 类的同时也提供了各种与文件交互的方法：

.. code-block:: php

    <?php
    $file = $request->file('photo');

    $file = $request->photo;

你可以使用 ``hasFile`` 方法确认请求中是否存在文件：

.. code-block:: php

    <?php
    if ($request->hasFile('photo')) {
        //
    }

验证成功上传
------------
除了检查上传的文件是否存在外，你也可以通过 ``isValid`` 方法验证上传的文件是否有效：

.. code-block:: php

    <?php
    if ($request->file('photo')->isValid()) {
        //
    }

文件路径 & 扩展名
-----------------
``UploadedFile`` 类还包含访问文件的完整路径及其扩展名方法。 ``extension`` 方法会根据文件内容判断文件的扩展名。该扩展名可能会和客户端提供的扩展名不同：

.. code-block:: php

    <?php
    $path = $request->photo->path();

    $extension = $request->photo->extension();

其它文件方法
------------
``UploadedFile`` 实例上还有许多可用的方法，可以查看该类的 `API 文档 <http://api.symfony.com/3.0/Symfony/Component/HttpFoundation/File/UploadedFile.html>`_ 了解这些方法的详细信息。

存储上传文件
------------
要存储上传的文件，先配置好 文件系统。你可以使用 ``UploadedFile`` 的 ``store`` 方法把上传文件移动到你的某个磁盘上，该文件可能是本地文件系统中的一个位置，甚至像 ``Amazon S3`` 这样的云存储位置。

``store`` 方法接受相对于文件系统配置的存储文件根目录的路径。这个路径不能包含文件名，因为系统会自动生成唯一的 ``ID`` 作为文件名。

``store`` 方法还接受可选的第二个参数，用于存储文件的磁盘名称。这个方法会返回相对于磁盘根目录的文件路径：

.. code-block:: php

    <?php
    $path = $request->photo->store('images');

    $path = $request->photo->store('images', 's3');

如果你不想自动生成文件名，那么可以使用 ``storeAs`` 方法，它接受路径、文件名和磁盘名作为其参数：

.. code-block:: php

    <?php
    $path = $request->photo->storeAs('images', 'filename.jpg');

    $path = $request->photo->storeAs('images', 'filename.jpg', 's3');

配置可信代理
=============
如果你的应用程序运行在失效的 ``TLS/SSL`` 证书的负载均衡器后，你可能会注意到你的应用程序有时不能生成 ``HTTPS`` 链接。通常这是因为你的应用程序正在从端口 ``80`` 上的负载平衡器转发流量，却不知道是否应该生成安全链接。

部署应用程序时，您可能会部署在负载均衡器（例如 ``AWS Elastic Load Balancing`` ）或反向代理（例如用于缓存的 ``Varnish`` ）之后。在大多数情况下，这不会出现任何问题。但是，当请求通过代理时，使用标准 ``Forwarded`` 标头或 ``X-Forwarded-*``标头发送某些请求信息。例如，用户的真实 ``IP`` 将存储在标准的 ``Forwarded: for="..."`` 标题或 ``X-Forwarded-For`` 中，而不是读取 ``REMOTE_ADDR`` 标题头（现在将是反向代理的IP地址）。如果您不配置应用以查找这些标头，无论客户端是通过 ``HTTPS`` 连接，还是其它，您将获得有关客户端 ``IP`` 地址，客户端端口以及请求的主机名的错误信息。

要解决此问题，您需要告诉 Laravel 要信任的反向代理 ``IP`` 地址以及反向代理用于发送信息的标题头。

解决这个问题需要在 Laravel 应用程序中包含 ``App\Http\Middleware\TrustProxies`` 中间件，这使得你可以快速自定义应用程序信任的负载均衡器或代理。你的可信代理应该作为这个中间件的 ``$proxies`` 属性的数组列出。除了配置受信任的代理之外，还可以配置应该信任的代理 ``$header`` :

.. code-block:: php

    <?php

    namespace App\Http\Middleware;

    use Illuminate\Http\Request;
    use Fideloper\Proxy\TrustProxies as Middleware;

    class TrustProxies extends Middleware
    {
        /**
         * 这个应用程序的可信代理列表
         *
         * @var array
         */
        protected $proxies = [
            '192.168.1.1',
            '192.168.1.2',
        ];

        /**
         * 应该用来检测代理的头信息。
         *
         * @var string
         */
        protected $headers = Request::HEADER_X_FORWARDED_ALL;
    }

.. tip:: 如果您使用 ``AWS`` 弹性负载平衡，您的 ``$header`` 值应该是 ``Request::HEADER_X_FORWARDED_AWS_ELB`` 。常量的更多信息,可用于 ``$headers`` 属性，看看 ``Symfony`` 的 文档 `信任代理 <http://symfony.com/doc/current/deployment/proxies.html>`_ 。

信任所有代理
------------
如果你使用 ``Amazon AWS`` 或其他的「云」负载均衡器提供程序，你可能不知道负载均衡器的实际 ``IP`` 地址。在这种情况下，你可以使用 ``*`` 来信任所有代理：

.. code-block:: php

    <?php
    /**
     * 这个应用程序的可信代理列表
     *
     * @var array
     */
    protected $proxies = '*';
