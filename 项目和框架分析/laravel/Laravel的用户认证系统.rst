**********************
Laravel 的用户认证系统
**********************

简介
====
``Laravel`` 中实现用户认证非常简单。实际上，几乎所有东西都已经为你配置好了。其配置文件位于 ``config/auth.php`` ，其中包含了用于调整认证服务行为的注释清晰的选项配置。

其核心是由 ``Laravel`` 的认证组件的 **「看守器」** 和 **「提供器」** 组成。 **看守器定义了该如何认证每个请求中用户。** 例如， ``Laravel``  自带的 ``session`` 看守器会使用 ``session`` 存储和 ``cookies`` 来维护状态。

**提供器中定义了该如何从持久化的存储数据中检索用户。** ``Laravel`` 自带支持使用 ``Eloquent`` 和数据库查询构造器来检索用户。当然，你可以根据需要自定义其他提供器。

不过对大多数应用而言，可能永远都不需要修改默认身份认证配置。

数据库注意事项
---------------
默认情况下， ``Laravel`` 在 ``app`` 目录中包含了一个 ``Eloquent`` 模型 ``App\User`` 。这个模型和默认的 ``Eloquent`` 认证驱动一起使用。如果你的应用不使用 ``Eloquent`` ，也可以使用 ``Laravel`` 查询构造器的 ``database`` 认证驱动。

为 ``App\User`` 模型创建数据库表结构时，确保密码字段长度至少为 60 个字符以及默认字符串列长度为 255 个字符。

此外，你要验证的用户（或等效的）表要包含一个可空的、长度为 100 的字符串 ``remember_token`` 。这个字段将用于存储当用户登录应用并勾选「记住我」时的令牌。

快速构建认证系统
================
想要快点开始？ 只需在新的 ``Laravel`` 应用上运行 ``php artisan make:auth`` (实际调用 ``\Illuminate\Auth\Console\AuthMakeCommand`` )和 ``php artisan migrate`` 命令。然后可以用浏览器访问 ``http://your-app.dev/register`` 或者你在程序中定义的其它 ``URL`` 。这两个命令就可以构建好整个认证系统。

``Laravel`` 自带几个预构建的认证控制器，它们被放置在 ``App\Http\Controllers\Auth`` 命名空间内。 ``RegisterController`` 处理新用户注册， ``LoginController`` 处理用户登陆， ``ForgotPasswordController`` 处理用于重置密码的邮件链接，而 ``ResetPasswordController`` 包含重置密码的逻辑。这些控制器都使用 ``trait`` 来引入所必要的方法。对于大多数应用而言，你根本不需要修改这些控制器。

路由
-----
``Laravel`` 提供了一个简单的命令来快速生成身份验证所需的路由和视图：

.. code-block:: shell

    php artisan make:auth

该命令最好在新的应用下使用， **它会生成布局、注册和登录视图以及所有的认证接口的路由。同时它还会生成 HomeController 来处理应用的登录请求。（不会生成auth目录下的其它控制器）**

视图
----
``php artisan make:auth`` 命令会在 ``resources/views/auth`` 目录创建所有认证需要的视图。

同时， ``make:auth`` 命令还创建了 ``resources/views/layouts`` 目录，该目录包含了应用的基本布局视图。所有这些视图都是用 ``Bootstrap CSS`` 框架，你也可以根据需要对其自定义。

注册和登陆
----------
现在你已经为自带的认证控制器设置好了路由和视图，可以为应用注册和验证新用户了。你可以简单地在浏览器中访问应用，因为身份验证控制器已包含了（通过 traits）验证现有用户并将新用户存储在数据库中的逻辑了。

自定义路径
^^^^^^^^^^^
当用户成功通过身份认证后，他们会被重定向到 ``/home URI`` 。你可以通过在 ``LoginController、RegisterController`` 和 ``ResetPasswordController`` 中设置 ``redirectTo`` 属性来自定义重定向的位置：

.. code-block:: php

    <?php
    protected $redirectTo = '/';

如果重定向路径需要自定义生成逻辑，你可以定义 ``redirectTo`` 方法来代替 ``redirectTo`` 属性：

.. code-block:: php

    <?php
    protected function redirectTo()
    {
        return '/path';
    }

.. note:: ``redirectTo`` 方法优先于 ``redirectTo`` 属性。

该方法提供给 ``Illuminate\Foundation\Auth\RedirectsUsers`` 类中的 ``redirectPath()`` 方法调用。

自定义用户名
^^^^^^^^^^^^
``Laravel`` 默认使用 ``email`` 字段来作为用户名进行认证。如果你想用其他字段认证，可以在 ``LoginController`` 里面定义一个 ``username`` 方法：

.. code-block:: php

    <?php
    public function username()
    {
        return 'username';
    }

该方法覆盖 ``Illuminate\Foundation\Auth\AuthenticatesUsers`` 类中的 ``username()`` 方法。

自定义看守器
^^^^^^^^^^^^
你还可以自定义用于认证和注册用户的「看守器」。要实现这一功能，需要在 ``LoginController、RegisterController`` 和 ``ResetPasswordController`` 中定义 ``guard`` 方法。该方法需要返回一个看守器实例：

.. code-block:: php

    <?php
    use Illuminate\Support\Facades\Auth;

    protected function guard()
    {
        return Auth::guard('guard-name'); // 调用\Illuminate\Auth\AuthManager中的guard()方法
    }

该方法覆盖 ``Illuminate\Foundation\Auth\AuthenticatesUsers`` 类中的 ``guard()`` 方法。这里的 ``Auth`` 门面底层调用的是 ``\Illuminate\Auth\AuthManager`` 类。

自定义验证／存储
^^^^^^^^^^^^^^^^
要修改新用户在应用注册时所需的表单字段，或者自定义如何将新用户存储到数据库中，你可以修改 ``RegisterController`` 类。该类负责验证和创建应用的新用户。

``RegisterController`` 的 ``validator`` 方法包含了应用验证新用户字段的规则，你可以按需要自定义该方法。

``RegisterController`` 的 ``create`` 方法负责使用 ``Eloquent ORM`` 在数据库中创建新的 ``App\User`` 记录。你可以根据数据库的需要自定义该方法。

检索认证用户
------------
你可以通过 ``Auth`` 面板来访问认证的用户：

.. code-block:: php

    <?php
    use Illuminate\Support\Facades\Auth;

    // 获取当前已认证的用户...
    $user = Auth::user(); // 底层实际调用的是 \Illuminate\Contracts\Auth\Guard 接口的 user() 方法，即 \Illuminate\Auth\SessionGuard 对象的 user() 方法

    // 获取当前已认证的用户 ID...
    $id = Auth::id(); // 底层实际调用的是 \Illuminate\Contracts\Auth\Guard 接口的 id()方法，即 \Illuminate\Auth\SessionGuard 对象的 id() 方法

或者，你还可以通过 ``Illuminate\Http\Request`` 实例来访问已认证的用户。请记住，类型提示的类会被自动注入到您的控制器方法中：

.. code-block:: php

    <?php
    namespace App\Http\Controllers;

    use Illuminate\Http\Request;

    class ProfileController extends Controller
    {
        /**
         * 更新用户的简介。
         *
         * @param  Request  $request
         * @return Response
         */
        public function update(Request $request)
        {
            $request->user() //返回已认证的用户的实例。这在\Illuminate\Auth\AuthServiceProvider的registerRequestRebindHandler方法中被传入
        }
    }

确定当前用户是否认证
^^^^^^^^^^^^^^^^^^^^^
你可以使用 ``Auth`` 门面的 ``check`` 方法来检查用户是否登录，如果已经认证，将会返回 ``true`` ：

.. code-block:: php

    <?php
    use Illuminate\Support\Facades\Auth;

    if (Auth::check()) { // 底层实际调用的是 \Illuminate\Contracts\Auth\Guard 接口的 check() 方法，即 \Illuminate\Auth\SessionGuard 对象的 check() 方法
        // 用户已登录...
    }

.. note:: 除了可以使用 ``check`` 方法确定用户是否被认证，在允许用户访问某些路由／控制器之前，通常还是会使用中间件来验证用户是否进行身份验证。

保护路由
--------
路由中间件 用于只允许通过认证的用户访问指定的路由。 ``Laravel`` 自带了在 ``Illuminate\Auth\Middleware\Authenticate`` 中定义的 ``auth`` 中间件。由于这个中间件已经在 ``HTTP`` 内核中注册，所以只需要将中间件附加到路由定义中：

.. code-block:: php

    <?php
    Route::get('profile', function () {
        // 只有认证过的用户可以...
    })->middleware('auth');

当然，如果使用 控制器，则可以在构造器中调用 ``middleware`` 方法来代替在路由中直接定义：

.. code-block:: php

    <?php
    public function __construct()
    {
        $this->middleware('auth'); // 当前控制器所有方法都需要认证
    }

该中间件位于 ``\Illuminate\Auth\Middleware\Authenticate`` 文件中。

指定看守器
^^^^^^^^^^^
将 ``auth`` 中间件添加到路由时，还需要指定使用哪个看守器来认证用户。指定的看守器对应配置文件 ``auth.php`` 中 ``guards`` 数组的某个键：

.. code-block:: php

    <?php
    Route::get('profile', function () {
        // 只有认证过的用户可以...
    })->middleware('auth:api');

    public function __construct()
    {
        $this->middleware('auth:api');
    }

登录限制
--------
``Laravel`` 内置的控制器 ``LoginController`` 已经包含了 ``Illuminate\Foundation\Auth\ThrottlesLogins trait`` 。默认情况下，如果用户在进行几次尝试后仍不能提供正确的凭证，该用户将在一分钟内无法进行登录。这个限制基于用户的用户名／邮件地址和 ``IP`` 地址。

手动认证用户
============
自定义控制器
-------------
当然，不一定要使用 ``Laravel`` 内置的认证控制器。如果选择删除这些控制器，你可以直接调用 ``Laravel`` 的认证类来管理用户认证。别担心，这简单得很。

我们可以通过 ``Auth`` 门面来访问 ``Laravel`` 的认证服务，因此需要确认类的顶部引入了 ``Auth`` 门面。接下来让我们看一下 ``attempt`` 方法：

.. code-block:: php

    <?php
    namespace App\Http\Controllers;

    use Illuminate\Support\Facades\Auth;

    class LoginController extends Controller
    {
        /**
         * 处理身份认证尝试.
         *
         * @return Response
         */
        public function authenticate()
        {
            if (Auth::attempt(['email' => $email, 'password' => $password])) {
                // 认证通过...
                return redirect()->intended('dashboard');
            }
        }
    }

``attempt`` 方法会接受一个键值对数组作为其第一个参数。这个数组的值将用来在数据库表中查找用户。所以在上面的例子中，用户将被 ``email`` 字段的值检索。如果用户被找到了，数据库中存储的散列密码将与通过数组传递给方法的散列 ``password`` 进行比较。 如果两个散列密码匹配，就会为用户启动一个已认证的会话。

如果认证成功， ``attempt`` 方法将返回 ``true`` ，反之则返回 ``false`` 。

在身份验证中间件拦截之前，重定向器上的 ``intended`` 方法将重定向到用户尝试访问的 ``URL`` 。如果该 ``URL`` 无效，会给该方法传递回退 ``URI`` 。

指定额外条件
^^^^^^^^^^^^
除了用户的电子邮件和密码之外，你还可以向身份验证查询添加额外的条件。例如，我们可能会验证用户是否被标记为「活动」状态：

.. code-block:: php

    <?php
    if (Auth::attempt(['email' => $email, 'password' => $password, 'active' => 1])) {
        // 用户处于活动状态，不被暂停，并且存在。
    }

底层实际调用的是 ``\Illuminate\Contracts\Auth\UserProvider`` 接口的 ``retrieveByCredentials()`` 方法，即 ``\Illuminate\Auth\DatabaseUserProvider`` 对象的 ``retrieveByCredentials()`` 方法。

.. note:: 在这些例子中， ``email`` 不是必需的选项，仅作为示例使用。你应该使用与数据库中的「用户名」对应的任何字段的名称。

访问指定的看守器实例
^^^^^^^^^^^^^^^^^^^^
可以通过 ``Auth`` 门面的 ``guard`` 方法来指定要使用哪个看守器实例。这允许你使用完全独立的可认证模型或用户表来管理应用的抽离出来的身份验证。

传递给 ``guard`` 方法的看守器名称应该与 ``auth.php`` 配置文件中 ``guards`` 中的其中一个值相对应：

.. code-block:: php

    <?php
    if (Auth::guard('admin')->attempt($credentials)) {
        //
    }

注销用户
^^^^^^^^
要让用户从应用中注销，可以在 ``Auth`` 门面上使用 ``logout`` 方法。这会清除用户会话中的身份验证信息：

.. code-block:: php

    <?php
    Auth::logout();

记住用户
--------
如果你想要在应用中提供「记住我」的功能 ， 则可以传递一个布尔值作为 ``attempt``  方法的第二个参数，这会使在用户手动注销前一直保持已验证状态。当然， ``users``  数据表必须包含 ``remember_token`` 字段，这是用来保存「记住我」的令牌。本质就是通过该字段来检索用户信息。

.. code-block:: php

    <?php
    if ( Auth::attempt([ 'email' => $email, 'password' => $password], $remember)) {
        // 这个用户被记住了...
    }

.. note:: 如果你使用 ``Laravel`` 内置的 ``LoginController`` ，则「记住」用户的逻辑已经由控制器使用的 ``traits`` 来实现。

如果你「记住」用户，可以使用 ``viaRemember`` 方法来检查这个用户是否使用「记住我」`` cookie`` 进行认证：

.. code-block:: php

    <?php
    if (Auth::viaRemember()) {
        //
    }

其它认证方法
------------
验证用户实例
^^^^^^^^^^^^
如果需要将现有用户实例记录到应用，可以使用用户实例调用 ``login`` 方法。给定的对象必须实现了 ``Illuminate\Contracts\Auth\Authenticatable`` 契约 。当然， ``Laravel`` 自带的 ``App\User`` 模型已经实现了这个接口：

.. code-block:: php

    <?php
    Auth::login($user); // 这里是切换用户

    // 登录并且「记住」给定用户...
    Auth::login($user, true);

该方法调用的是 ``Illuminate\Auth\SessionGuard`` 类中的 ``login()`` 方法。

当然，你也可以指定要使用的看守器实例：

.. code-block:: php

    <?php
    Auth::guard('admin')->login($user);

通过 ID 验证用户
^^^^^^^^^^^^^^^^^
你可以使用 ``loginUsingId`` 方法通过其 ``ID`` 将用户记录到应用中。这个方法只接受要验证的用户的主键：

.. code-block:: php

    <?php
    Auth::loginUsingId(1);

    //登录并且「记住」给定的用户...
    Auth::loginUsingId(1, true);

``loginUsingId`` 函数内部调用 ``login()`` 函数来登陆。

仅验证用户一次
^^^^^^^^^^^^^^^
你可以使用 ``once`` 方法将用户记录到单个请求的应用中。不会使用任何会话或 ``cookies`` ， 这个对于构建无状态的 ``API`` 非常的有用：

.. code-block:: php

    <?php
    if (Auth::once($credentials)) {
        //
    }

HTTP 基础认证
=============
HTTP 基础认证 提供一种快速方式来认证应用的用户，而且不需要设置专用的「登录」页面。开始之前，先把 ``auth.basic`` 中间件 添加到你的路由。 ``auth.basic`` 中间件已经被包含在 ``Laravel`` 框架中，所以你不需要定义它：

.. code-block:: php

    <?php
    Route::get('profile', function () {
        // 只有认证过的用户可进入...
    })->middleware('auth.basic'); // 通过basic中间件来认证用户

中间件被增加到路由后，在浏览器中访问路由时，将自动提示你输入凭证。默认情况下， ``auth.basic`` 中间件将会使用用户记录上的 ``email`` 字段作为「用户名」。

FastCGI 的注意事项
------------------
如果使用了 ``PHP FastCGI`` ， ``HTTP`` 基础认证可能无法正常工作。你需要将下面这几行加入你 ``.htaccess`` 文件中:

.. code-block:: php

    <?php
    RewriteCond %{HTTP:Authorization} ^(.+)$
    RewriteRule .* - [E=HTTP_AUTHORIZATION:%{HTTP:Authorization}]

无状态 HTTP 基础认证
---------------------
你可以使用 ``HTTP`` 基础认证，而不在会话中设置用户标识符 ``cookie`` ，这对于 ``API`` 认证来说特别有用。为此，请定义一个中间件并调用 ``onceBasic`` 方法。如果 ``onceBasic`` 方法没有返回任何响应的话，这个请求可以进一步传递到应用程序中：

.. code-block:: php

    <?php
    namespace Illuminate\Auth\Middleware;

    use Illuminate\Support\Facades\Auth;

    class AuthenticateOnceWithBasicAuth
    {
        /**
         * 处理传入的请求。
         *
         * @param  \Illuminate\Http\Request  $request
         * @param  \Closure  $next
         * @return mixed
         */
        public function handle($request, $next)
        {
            return Auth::onceBasic() ?: $next($request);
        }
    }

接着，注册路由中间件 ，然后将它附加到路由：

.. code-block:: language

    protected $routeMiddleware = [
        'auth' => \Illuminate\Auth\Middleware\Authenticate::class,
        'auth.basic' => \Illuminate\Auth\Middleware\AuthenticateWithBasicAuth::class,
        'bindings' => \Illuminate\Routing\Middleware\SubstituteBindings::class,
        'can' => \Illuminate\Auth\Middleware\Authorize::class,
        'guest' => \App\Http\Middleware\RedirectIfAuthenticated::class,
        'throttle' => \Illuminate\Routing\Middleware\ThrottleRequests::class,
        'auth.basic.once' => Illuminate\Auth\Middleware\AuthenticateOnceWithBasicAuth::class
    ];

    Route::get('api/user', function () {
        // 只有认证过的用户可以进入...
    })->middleware('auth.basic.once');


社交认证
========


增加自定义看守器
================

Guard 接口
----------
.. code-block:: php

    <?php
    Illuminate\Contracts\Auth\Guard

``Guard`` 接口定义了某个实现了 ``Authenticatable``  (可认证的) 模型或类的认证方法以及一些常用的接口。

.. code-block:: php

    <?php
    // 判断当前用户是否登录
    public function check();
    // 判断当前用户是否是游客（未登录）
    public function guest();
    // 获取当前认证的用户
    public function user();
    // 获取当前认证用户的 id，严格来说不一定是 id，应该是上个模型中定义的唯一的字段值
    public function id();
    // 根据提供的凭证认证用户
    public function validate(array $credentials = []);
    // 设置当前用户
    public function setUser(Authenticatable $user);

StatefulGuard 接口
------------------
.. code-block:: php

    <?php
    Illuminate\Contracts\Auth\StatefulGuard

``StatefulGuard`` 接口继承自 ``Guard`` 接口，除了 ``Guard`` 里面定义的一些基本接口外，还增加了更进一步、有状态的 ``Guard`` .

新添加的接口有这些：

.. code-block:: php

    <?php
    // 尝试根据提供的凭证验证用户是否合法
    public function attempt(array $credentials = [], $remember = false);
    // 一次性登录，不记录session or cookie
    public function once(array $credentials = []);
    // 登录用户，通常在验证成功后记录 session 和 cookie
    public function login(Authenticatable $user, $remember = false);
    // 使用用户 id 登录，内部调用上面的login()
    public function loginUsingId($id, $remember = false);
    // 使用用户 ID 登录，但是不记录 session 和 cookie，内部调用前面的once()
    public function onceUsingId($id);
    // 通过 cookie 中的 remember token 自动登录
    public function viaRemember();
    // 登出
    public function logout();

**Laravel 中默认提供了 3 中 guard：RequestGuard，TokenGuard，SessionGuard** ：

- ``Illuminate\Auth\RequestGuard`` RequestGuard 是一个非常简单的 guard。RequestGuard 是通过传入一个闭包来认证的。可以通过调用 ``Auth::viaRequest`` 添加一个自定义的 RequestGuard 。
- ``Illuminate\Auth\SessionGuard`` SessionGuard 是 Laravel web 认证默认的 guard 。
- ``Illuminate\Auth\TokenGuard`` TokenGuard 适用于无状态 api 认证，通过 token 认证。

你可以使用 ``Auth`` 门面的 ``extend`` 方法来定义自己的身份验证提供器(守护器)。 你需要在 服务提供器 中调用这个提供器。由于 ``Laravel`` 已经配备了 ``AuthServiceProvider`` ，我们可以把代码放在这个提供器中：

.. code-block:: php

    <?php
    namespace App\Providers;

    use App\Services\Auth\JwtGuard;
    use Illuminate\Support\Facades\Auth;
    use Illuminate\Foundation\Support\Providers\AuthServiceProvider as ServiceProvider;

    class AuthServiceProvider extends ServiceProvider
    {
        /**
         * 注册任意应用认证／授权服务。
         *
         * @return void
         */
        public function boot()
        {
            $this->registerPolicies();

            Auth::extend('jwt', function ($app, $name, array $config) {
                // 返回一个 Illuminate\Contracts\Auth\Guard 实例...

                return new JwtGuard(Auth::createUserProvider($config['provider']));
            });
        }
    }

正如上面的代码所示，传递给 ``extend`` 方法的回调应该返回 ``Illuminate\Contracts\Auth\Guard`` 的实现的实例。 这个接口包含你需要实现的方法来定义一个自定义看守器。定义完之后，你可以在 ``auth.php`` 配置文件的 ``guards`` 配置中使用这个看守器：

.. code-block:: php

    <?php
    'guards' => [
        'api' => [
            'driver' => 'jwt',
            'provider' => 'users',
        ],
    ],

请求闭包看守器
^^^^^^^^^^^^^^^
一个最简的，基于 HTTP 请求的认证方案是使用 ``Auth::viaRequest`` 方法，此方法允许你使用闭包来快速定义一个看守器。

首先你需要在 ``AuthServiceProvider`` 的 ``boot`` 方法里调用 ``Auth::viaRequest`` ，这个 ``viaRequest`` 方法接受一个看守器名称为第一参数，看守器名称是有字符串组成的自定义名称。第二个参数是一个闭包函数，此函数接受一个 HTTP 请求实例，成功授权后返回一个用户实例，授权失败后返回 ``null`` :

.. code-block:: php

    <?php
    use App\User;
    use Illuminate\Http\Request;
    use Illuminate\Support\Facades\Auth;

    /**
     * 注册所有的应用授权服务
     *
     * @return void
     */
    public function boot()
    {
        $this->registerPolicies();

        Auth::viaRequest('custom-token', function ($request) {
            return User::where('token', $request->token)->first();
        });
    }

成功定义看守器以后，你可以在 ``auth.php`` 里的 ``guards`` 选项中使用：

.. code-block:: php

    <?php
    'guards' => [
        'api' => [
            'driver' => 'custom-token',
        ],
    ],


增加自定义用户提供器
====================
如果你没有使用传统的关系型数据库来存储用户信息，则需要使用自己的用户认证提供器来扩展 ``Laravel`` 。我们使用 ``Auth`` 门面上的 ``provider`` 方法定义自定义用户提供器：

.. code-block:: php

    <?php
    namespace App\Providers;

    use Illuminate\Support\Facades\Auth;
    use App\Extensions\RiakUserProvider;
    use Illuminate\Foundation\Support\Providers\AuthServiceProvider as ServiceProvider;

    class AuthServiceProvider extends ServiceProvider
    {
        /**
         * 注册任何应用认证／授权服务。
         *
         * @return void
         */
        public function boot()
        {
            $this->registerPolicies();

            Auth::provider('riak', function ($app, array $config) {
                // 返回 Illuminate\Contracts\Auth\UserProvider 实例...

                return new RiakUserProvider($app->make('riak.connection'));
            });
        }
    }

使用 ``provider`` 方法注册用户提供器后，你可以在配置文件 ``auth.php`` 中切换到新的用户提供器。首先，定义一个使用新驱动的 ``provider`` ：

.. code-block:: php

    <?php
    'providers' => [
        'users' => [
            'driver' => 'riak',
        ],
    ],

最后，你可以在 ``guards`` 配置中使用这个提供器：

.. code-block:: php

    <?php
    'guards' => [
        'web' => [
            'driver' => 'session',
            'provider' => 'users',
        ],
    ],

用户提供器契约
--------------
``Illuminate\Contracts\Auth\UserProvider`` 的实现只负责从永久存储系统（如 ``MySQL、Riak`` 等）中获取 ``Illuminate\Contracts\Auth\Authenticatable`` 的实现实例。这两个接口允许 ``Laravel`` 认证机制继续运行，无论用户数据如何被存储或使用什么类型的类实现它。

``UserProvider`` 接口定义了获取认证模型的方法，比如根据 ``id`` 获取模型，根据 ``email`` 获取模型等等。

让我们来看看 ``Illuminate\Contracts\Auth\UserProvider`` 契约：

.. code-block:: php

    <?php
    interface UserProvider {

        // 通过唯一标示符获取认证模型
        public function retrieveById($identifier);
        // 通过唯一标示符和 remember token 获取模型
        public function retrieveByToken($identifier, $token);
        // 通过给定的认证模型更新 remember token
        public function updateRememberToken(Authenticatable $user, $token);
        // 通过给定的凭证获取用户，比如 email 或用户名等等
        public function retrieveByCredentials(array $credentials);
        // 认证给定的用户和给定的凭证是否符合
        public function validateCredentials(Authenticatable $user, array $credentials);

    }

- ``retrieveById`` 函数通常接收代表用户的键，例如 ``MySQL`` 数据库中自增的 ``ID`` 。应该通过该方法检索和返回与 ``ID`` 匹配的 ``Authenticatable`` 的实现实例。
- ``retrieveByToken`` 函数通过其唯一的 ``$identifier`` 和存储在 remember_token 字段中的「记住我」 ``$token`` 来检索一个用户 。与以前的方法一样，应该返回 ``Authenticatable`` 实现的实例。
- ``updateRememberToken`` 方法使用新的 ``$token`` 更新了 ``$user`` 的 ``remember_token`` 字段。当使用「记住我」尝试登录成功时，或用户登出时，可以更新该令牌。
- 在尝试登录应用程序时，``retrieveByCredentials`` 方法接收传递给 ``Auth::attempt`` 方法的凭据数组。然后该方法将「查询」底层持久存储匹配用户的这些凭据。通常，此方法会在 ``$credentials['username']`` 上运行「where」条件的查询。这个方法应该需要返回 ``Authenticatable`` 的实现的实例。此方法不应该尝试任何密码验证或认证。
- ``validateCredentials`` 方法将给定的 ``$user`` 和 ``$credentials`` 进行匹配，以此来验证用户。例如，这个方法可以使用 ``Hash::check`` 比较 ``$user->getAuthPassword()`` 和 ``$credentials['password']`` 的值。此方法通过返回 ``true`` 或 ``false`` 来显示密码是否有效。

``Laravel`` 中默认有两个 ``user provider: DatabaseUserProvider & EloquentUserProvider`` ：

- ``Illuminate\Auth\DatabaseUserProvider`` 直接通过数据库表来获取认证模型；
- ``Illuminate\Auth\EloquentUserProvider`` 通过 ``eloquent`` 模型来获取认证模型；

认证契约
--------
现在我们已经探讨了 ``UserProvider`` 中的每个方法，让我们来看看 ``Authenticatable`` 契约。记住，提供器需要从 ``retrieveById`` 和 ``retrieveByCredentials`` 方法来返回这个接口的实现：

``Authenticatable`` 定义了一个可以被用来认证的模型或类需要实现的接口，也就是说，如果需要用一个自定义的类来做认证，需要实现这个接口定义的方法。

.. code-block:: php

    <?php
    interface Authenticatable {

        // 获取唯一标识的，可以用来认证的字段名，比如 id，uuid
        public function getAuthIdentifierName();
        // 获取该标示符对应的值
        public function getAuthIdentifier();
        // 获取认证的密码
        public function getAuthPassword();
        // 获取remember token
        public function getRememberToken();
        // 设置 remember token
        public function setRememberToken($value);
        // 获取 remember token 对应的字段名，比如默认的 'remember_token'
        public function getRememberTokenName();

    }

这个接口很简单。 ``getAuthIdentifierName`` 方法返回用户的「主键」字段的名称，而 ``getAuthIdentifier`` 方法返回用户的「主键」值。重申一次，在 ``MySQL`` 后台，这个主键是指自增的主键。 ``getAuthPassword`` 应该要返回用户的散列密码。这个接口允许认证系统和任何用户类一起工作，不用管你在使用什么 ``ORM`` 或存储抽象层。默认情况下， ``Laravel`` 的 ``app`` 目录中包含一个 ``User`` 类来实现此接口，因此你可以参考这个类来实现一个实例。

Authenticatable trait
^^^^^^^^^^^^^^^^^^^^^^

该 ``Authenticatable trait`` 实现了 ``Authenticatable`` 接口的所有方法，并提供一些成员变量。

.. code-block:: php

    <?php
    Illuminate\Auth\Authenticatable

``Laravel`` 中定义的 ``Authenticatable trait`` ，也是 ``Laravel auth`` 默认的 ``User`` 模型使用的 ``trait`` ，这个 ``trait`` 定义了 ``User`` 模型默认认证标识符为 ``id`` ，密码字段为 ``password`` ， ``remember token`` 对应的字段为 ``remember_token`` 等等。

通过重写 ``User`` 模型的这些方法可以修改一些设置。

AuthManager
===========
.. code-block:: php

    Illuminate\Auth\AuthManager

``Guard`` 用来认证一个用户是否认证成功， ``UserProvider`` 用来提供认证模型的来源，而根据项目的 ``config`` 管理 ``guard`` 以及自定义 ``guard`` 等等功能，则是通过 ``AuthManager`` 来实现。

``AuthManager`` 应该是有点像策略模式里面的 ``Context`` 类以及工厂方法里面的工厂，一方面管理 ``Guard`` ，另外一方面通过 ``__call`` 魔术方法调用具体的策略( ``Guard`` )方法。

``Auth`` 门面对应的实现类就是 ``AuthManager`` ， ``AuthManager`` 在容器中注册为单例，用来管理所有的 ``guard`` 和 ``user provider`` 以及 ``guard`` 的代理工作。

自定义认证
==========
根据上面的知识，可以知道要自定义一个认证很简单。

1. 创建认证模型。创建一个自定义的认证模型，实现 ``Authenticatable`` 接口；
2. 创建自定义的 ``UserProvider`` 。创建一个自定义的 ``UserProvider`` ，实现 ``UserProvider`` 接口，可以返回上面自定义的认证模型；
3. 创建自定义的 ``Guard`` 。创建一个自定义的 ``Guard`` ，实现 ``Guard`` 或 ``StatefulGuard`` 接口；
4. 添加 ``guard creator`` 和 ``user provider creator`` 到 ``AuthManager`` 中。

   在 ``AuthServiceProvider`` 的 ``boot`` 方法添加如下代码：

   .. code-block:: php

    <?php
    Auth::extend('myguard', function(){
        ...
        return new MyGuard();   //返回自定义 guard 实例
        ...
    });

    Auth::provider('myuserprovider', function(){
        return new MyUserProvider();    // 返回自定义的 user provider
    });

5. 在 ``config\auth.php`` 的 ``guards`` 数组中添加自定义 ``guard`` ，一个自定义 ``guard`` 包括两部分： ``driver`` 和 ``provider`` 。

   .. code-block:: php

    <?php
    'oustn' => [
        'driver' => 'myguard',
        'provider' => 'myusers',
    ],

6. 在 ``config\auth.php`` 的 ``providers`` 数组中添加自定义 ``user provider`` 。

   .. code-block:: php

    <?php
    'myusers' => [
        // 里面具体的字段可以根据你创建 user provider 需要的信息自由添加，
        // 可以通过 Auth::createUserProvider('myuserprovider') 创建
        'driver' => 'myuserprovider'
    ],

7. 设置 ``config\auth.php`` 的 ``defaults.guard`` 为 ``oustn``

事件
====
``Laravel`` 在认证过程中引发了各种各样的事件。你可以在 ``EventServiceProvider`` 中对这些事件做监听：

.. code-block:: php

    <?php
    /**
     * 应用程序的事件监听器映射。
     *
     * @var array
     */
    protected $listen = [
        'Illuminate\Auth\Events\Registered' => [
            'App\Listeners\LogRegisteredUser',
        ],

        'Illuminate\Auth\Events\Attempting' => [
            'App\Listeners\LogAuthenticationAttempt',
        ],

        'Illuminate\Auth\Events\Authenticated' => [
            'App\Listeners\LogAuthenticated',
        ],

        'Illuminate\Auth\Events\Login' => [
            'App\Listeners\LogSuccessfulLogin',
        ],

        'Illuminate\Auth\Events\Failed' => [
            'App\Listeners\LogFailedLogin',
        ],

        'Illuminate\Auth\Events\Logout' => [
            'App\Listeners\LogSuccessfulLogout',
        ],

        'Illuminate\Auth\Events\Lockout' => [
            'App\Listeners\LogLockout',
        ],
    ];