=======
控制器
=======


简介
====
为了代替在路由文件中以闭包的形式定义所有的请求处理逻辑，你也许想使用控制类来组织这些行为。控制器能够将相关的请求处理逻辑组成一个单独的类，控制器被存放在 ``app/Http/Controllers`` 目录下。


控制器基类
==========
定义控制器
----------
下面是一个控制器类的例子。需要注意的是，该控制器继承了一个 Laravel 内置的控制器基类。该控制器基类提供了一些编辑的方法，比如 ``middleware`` 方法，该方法可以为控制器行为添加中间件：

.. code-block:: php

    <?php

    namespace App\Http\Controllers;

    use App\User;
    use App\Http\Controllers\Controller;

    class UserController extends Controller
    {
        /**
         * Show the profile for the given user.
         *
         * @param  int  $id
         * @return Response
         */
        public function show($id)
        {
            return view('user.profile', ['user' => User::findOrFail($id)]);
        }
    }

你可以这样定义一个指向控制器行为的路由:

.. code-block:: php

    <?php
    Route::get('user/{id}', 'UserController@show');

现在, 当一个请求与此指定的路由的 ``URI`` 匹配时， ``UserController`` 类的 ``show`` 方法就会被执行。当然，路由参数也会传递至该方法。

.. tip:: 控制器并不是强制要求 继承基础类。但是，如果控制器没有继承基础类，你将无法使用一些便捷的功能，比如 ``middleware`` ,  ``validate`` 和 ``dispatch`` 。

控制器和命名空间
----------------
需要注意的是，在定义控制器路由时，我们不需要指定完整的控制器命名空间。因为 ``RouteServiceProvider`` 会在一个包含命名空间的路由器组中加载路由文件，所以我们只需指定类名中 ``App\Http\Controllers`` 命名空间之后的部分就可以了。

如果你选择将控制器存放在 ``App\Http\Controllers`` 目录下，只需要简单的使用相对于 ``App\Http\Controllers`` 根命名空间的特定类名。 也就是说，如果完整的控制器类是 ``App\Http\Controllers\Photos\AdminController`` ，那你应该用以下这种方式向控制器注册路由：

.. code-block:: php

    <?php
    Route::get('foo', 'Photos\AdminController@method');

单个行为控制器
--------------
如果你想定义一个只处理单个行为的控制器，你可以在这个控制器中放置一个 ``__invoke`` 方法：

.. code-block:: php

    <?php
    namespace App\Http\Controllers;

    use App\User;
    use App\Http\Controllers\Controller;

    class ShowProfile extends Controller
    {
        /**
         * Show the profile for the given user.
         *
         * @param  int  $id
         * @return Response
         */
        public function __invoke($id)
        {
            return view('user.profile', ['user' => User::findOrFail($id)]);
        }
    }

注册单个行为控制器的路由时，不需要指定方法：

.. code-block:: php

    <?php
    Route::get('user/{id}', 'ShowProfile');

控制器中间件
============
``Middleware`` 可以在路由文件中被分配给控制器路由：

.. code-block:: php

    <?php
    Route::get('profile', 'UserController@show')->middleware('auth');

但是，在控制器的构造方法中指定中间件会更方便。使用控制器构造函数中的 ``middleware`` 方法， 你可以很容易地将中间件分配给控制器的动作。你甚至可以约束中间件只对控制器类中的某些特定方法生效：

.. code-block:: php

    <?php
    class UserController extends Controller
    {
        /**
         * Instantiate a new controller instance.
         *
         * @return void
         */
        public function __construct()
        {
            $this->middleware('auth');

            $this->middleware('log')->only('index');

            $this->middleware('subscribed')->except('store');
        }
    }

还能使用闭包来为控制器注册中间件。闭包的方便之处在于，你无需特地创建一个中间件类来为某一个特殊的控制器注册中间件：

.. code-block:: php

    <?php
    $this->middleware(function ($request, $next) {
        // ...

        return $next($request);
    });

.. tip:: 你可以将中间件分配给控制器的部分行为上；然而这样可能意味着你的控制器正在变得很大。这里建议你将控制器分成多个更小的控制器。

资源控制器
==========
Laravel 资源路由将典型的「CRUD」路由分配给具有单行代码的控制器。比如，你希望创建一个控制器来处理应用保存的「照片」的所有 ``HTTP`` 请求。使用 ``Artisan`` 命令 ``make:controller`` ，我们可以快速创建这样一个控制器：

.. code-block:: shell

    php artisan make:controller PhotoController --resource

这个命令会生成一个控制器 ``app/Http/Controllers/PhotoController.php`` 。其中包含了每个可用资源的操作方法。

接下来，你可以给控制器注册一个资源路由：

.. code-block:: php

    <?php
    Route::resource('photos', 'PhotoController');

这个单一路由声明创建多个路由来处理资源上的各种行为。生成的控制器为每个行为保留了方法，包括了关于处理 ``HTTP`` 动作和 ``URIs`` 的声明注释。

你可以通过将一个数组传入到 ``resources`` 方法中的方式来一次性的创建多个资源控制器：

.. code-block:: php

    <?php
    Route::resources([
        'photos' => 'PhotoController',
        'posts' => 'PostController'
    ]);

资源控制器操作处理

+-----------+----------------------+---------+----------------+
| 动作      | URI                  | 行为    | 路由名称       |
+===========+======================+=========+================+
| GET       | /photos              | index   | photos.index   |
+-----------+----------------------+---------+----------------+
| GET       | /photos/create       | create  | photos.create  |
+-----------+----------------------+---------+----------------+
| POST      | /photos              | store   | photos.store   |
+-----------+----------------------+---------+----------------+
| GET       | /photos/{photo}      | show    | photos.show    |
+-----------+----------------------+---------+----------------+
| GET       | /photos/{photo}/edit | edit    | photos.edit    |
+-----------+----------------------+---------+----------------+
| PUT/PATCH | /photos/{photo}      | update  | photos.update  |
+-----------+----------------------+---------+----------------+
| DELETE    | /photos/{photo}      | destroy | photos.destroy |
+-----------+----------------------+---------+----------------+

指定资源模型

如果你使用了路由模型绑定，并且想在资源控制器的方法中使用类型提示，你可以在生成控制器的时候使用 ``--model`` 选项：

.. code-block:: shell

    php artisan make:controller PhotoController --resource --model=Photo

伪造表单方法

因为 ``HTML`` 表单不能生成 ``PUT`` ， ``PATCH`` 和 ``DELETE`` 请求，所以你需要添加一个隐藏的 ``_method`` 字段来伪造这些 ``HTTP`` 动作。 这个 Blade 指令 ``@method`` 可以为你创建这个字段：

.. code-block:: php

    <?php
    <form action="/foo/bar" method="POST">
        @method('PUT')
    </form>

部分资源路由
------------
声明资源路由时，你可以指定控制器应该处理的部分行为，而不是所有默认的行为：

.. code-block:: php

    <?php
    Route::resource('photos', 'PhotoController', ['only' => [
        'index', 'show'
    ]]);

    Route::resource('photos', 'PhotoController', ['except' => [
        'create', 'store', 'update', 'destroy'
    ]]);

API 资源路由
^^^^^^^^^^^^
当声明用于 ``APIs`` 的资源路由时，通常需要排除显示 ``HTML`` 模板的路由， 如 ``create`` 和 ``edit`` 。 为了方便起见，你可以使用 ``apiResource`` 方法自动排除这两个路由：

.. code-block:: php

    <?php
    Route::apiResource('photos', 'PhotoController');

你可以通过传递一个数组给 ``apiResources`` 方法的方式来一次性注册多个 ``API`` 资源控制器：

.. code-block:: php

    <?php
    Route::apiResources([
        'photos' => 'PhotoController',
        'posts' => 'PostController'
    ]);

为了快速生成一个不包含 ``create`` 和 ``edit`` 方法的API资源控制器，可以在执行 ``make:controller`` 命令时加上 ``--api`` 选项：

.. code-block:: shell

    php artisan make:controller API/PhotoController --api

命名资源路由
------------
默认情况下，所有的资源控制器行为都有一个路由名称。 但是，你可以传入一个 ``names`` 数组来覆盖这些名称：

.. code-block:: php

    <?php
    Route::resource('photos', 'PhotoController', ['names' => [
        'create' => 'photos.build' // 默认photos.create被替换为photos.build
    ]]);

命名资源路由参数
----------------
默认情况下， ``Route::resource`` 会根据资源名称的「单数」形式创建资源路由的路由参数。 你可以在选项数组中传入 ``parameters`` 参数来轻松地覆盖每个资源。 ``parameters`` 数组应当是一个资源名称和参数名称的关联数组：

.. code-block:: php

    <?php
    Route::resource('user', 'AdminUserController', ['parameters' => [
        'user' => 'admin_user'
    ]]);

上例将会为资源的 ``show`` 路由生成如下的 ``URI`` ：

.. code-block:: shell

    /user/{admin_user}

本地化资源 URIs
---------------
默认情况下， ``Route::resource`` 将会使用英文动词来创建资源 ``URI`` 。如果你需要本地化 ``create`` 和 ``edit`` 行为动作名，你可以在 ``AppServiceProvider`` 的 ``boot`` 方法中使用 ``Route::resourceVerbs`` 方法实现 ：

.. code-block:: php

    <?php
    use Illuminate\Support\Facades\Route;

    /**
     * Bootstrap any application services.
     *
     * @return void
     */
    public function boot()
    {
        Route::resourceVerbs([
            'create' => 'crear',
            'edit' => 'editar',
        ]);
    }

一旦动作被自定义后，像 ``Route::resource('fotos', 'PhotoController')`` 这样注册的资源路由将会产生如下的 ``URI`` ：

.. code-block:: shell

    /fotos/crear

    /fotos/{foto}/editar

补充资源控制器
--------------
如果你想为一个资源控制器在默认的资源路由之外增加额外的路由，你应该在调用 ``Route::resource`` 之前定义这些路由。否则由 ``resource`` 方法定义的路由可能会无意中优先于你补充的路由：

.. code-block:: php

    <?php
    Route::get('photos/popular', 'PhotoController@method');

    Route::resource('photos', 'PhotoController');

.. tip:: 记住保持控制器的专一性。如果你发现需要典型的资源操作之外的方法，可以考虑将你的控制器分成两个更小的控制器。

依赖注入 & 控制器
=================
构造函数注入
------------
Laravel 使用 服务容器 来解析所有的控制器。因此，你可以在控制器的构造函数中使用类型提示需要的依赖项，而声明的依赖项会自动解析并注入控制器实例中：

.. code-block:: php

    <?php

    namespace App\Http\Controllers;

    use App\Repositories\UserRepository;

    class UserController extends Controller
    {
        /**
         * 用户 repository 实例。
         */
        protected $users;

        /**
         * 创建一个新的控制器实例。
         *
         * @param  UserRepository  $users
         * @return void
         */
        public function __construct(UserRepository $users)
        {
            $this->users = $users;
        }
    }

当然，你也可以类型提示任何 Laravel 契约，只要它能被解析。根据你的应用，将你的依赖项注入控制器能提供更好的可测试性。

方法注入
--------
除了构造函数注入之外，你还可以在控制器方法中类型提示依赖项。 最常见的用法就是将 ``Illuminate\Http\Request`` 实例注入到控制器方法中：

.. code-block:: php

    <?php
    namespace App\Http\Controllers;

    use Illuminate\Http\Request;

    class UserController extends Controller
    {
        /**
         * 保存一个新用户
         *
         * @param  Request  $request
         * @return Response
         */
        public function store(Request $request)
        {
            $name = $request->name;

            //
        }
    }

如果你的控制器方法还需要从路由参数中获取输入内容，只需要在其他依赖项后列出路由参数即可。比如，如果你的路由是这样定义的：

.. code-block:: php

    <?php
    Route::put('user/{id}', 'UserController@update');

你还可以类型提示 ``Illuminate\Http\Request`` 并通过定义控制器方法获取 ``id`` 参数，如下所示：

.. code-block:: php

    <?php

    namespace App\Http\Controllers;

    use Illuminate\Http\Request;

    class UserController extends Controller
    {
        /**
         * 更新给定用户的信息。
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

__call方法
==========
和普通类一样，若控制器中没有对应 ``classname@method`` 中的 ``method`` ,则会调用类的 ``__call`` 函数。





