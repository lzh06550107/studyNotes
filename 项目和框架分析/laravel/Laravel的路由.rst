*****
路由
*****


路由
====

.. contents:: 目录
   :depth: 4

路由基础
--------
构建最基本的路由只需要一个 ``URI`` 与一个 闭包，这里提供了一个非常简单优雅的定义路由的方法：

.. code-block:: php

    <?php
    Route::get('foo', function () {
        return 'Hello World';
    });

默认路由文件
^^^^^^^^^^^^
所有的 Laravel 路由都在 ``routes`` 目录中的路由文件中定义，这些文件都由框架自动加载。 ``routes/web.php`` 文件用于定义 ``web`` 界面的路由。这里面的路由都会被分配给 ``web`` 中间件组，它提供了会话状态和 ``CSRF`` 保护等功能。定义在 ``routes/api.php`` 中的路由都是无状态的，并且被分配了 ``api`` 中间件组。

大多数的应用构建，都是以在 ``routes/web.php`` 文件定义路由开始的。可以通过在浏览器中输入定义的路由 ``URL`` 来访问 ``routes/web.php`` 中定义的路由。例如，你可以在浏览器中输入 ``http://your-app.dev/user`` 来访问以下路由：

.. code-block:: php

    <?php
    Route::get('/user', 'UserController@index');

``routes/api.php`` 文件中定义的路由通过 ``RouteServiceProvider`` 被嵌套到一个路由组里面。在这个路由组中，会自动添加 ``URL`` 前缀 ``/api`` 到此文件中的每个路由，这样你就无需再手动添加了。你可以在 ``RouteServiceProvider`` 类中修改此前缀以及其他路由组选项。

可用的路由方法
^^^^^^^^^^^^^^
路由器允许你注册能响应任何 ``HTTP`` 请求的路由：

.. code-block:: php

    <?php
    Route::get($uri, $callback);
    Route::post($uri, $callback);
    Route::put($uri, $callback);
    Route::patch($uri, $callback);
    Route::delete($uri, $callback);
    Route::options($uri, $callback);

有的时候你可能需要注册一个可响应多个 ``HTTP`` 请求的路由，这时你可以使用 ``match`` 方法，也可以使用 ``any`` 方法注册一个实现响应所有 ``HTTP`` 请求的路由：

.. code-block:: php

    <?php
    Route::match(['get', 'post'], '/', function () {
        //
    });

    Route::any('foo', function () {
        //
    });

CSRF 保护
^^^^^^^^^
指向 ``web`` 路由文件中定义的 ``POST`` 、 ``PUT`` 或 ``DELETE`` 路由的任何 ``HTML`` 表单都应该包含一个 ``CSRF`` 令牌字段，否则，这个请求将会被拒绝。可以在 ``CSRF`` 文档 中阅读有关 ``CSRF`` 更多的信息:

.. code-block:: php

    <?php
    <form method="POST" action="/profile">
        @csrf
        ...
    </form>
    //或者
    <form method="POST" action="/profile">
        {{ csrf_field() }}
        ...
    </form>


重定向路由
----------
如果要定义重定向到另一个 ``URI`` 的路由，可以使用 ``Route::redirect`` 方法。这个方法可以快速的实现重定向，而不再需要去定义完整的路由或者控制器:

.. code-block:: php

    <?php
    Route::redirect('/here', '/there', 301);

视图路由
--------
如果你的路由只需要返回一个视图，可以使用 ``Route::view`` 方法。它和 ``redirect`` 一样方便，不需要定义完整的路由或控制器。 ``view`` 方法有三个参数，其中前两个是必填参数，分别是 ``URI`` 和视图名称。第三个参数选填，可以传入一个数组，数组中的数据会被传递给视图:

.. code-block:: php

    <?php
    Route::view('/welcome', 'welcome');

    Route::view('/welcome', 'welcome', ['name' => 'Taylor']);

路由参数
========
laravel 允许在注册定义路由的时候设定路由参数，以供控制器或者闭包所用。路由参数可以设定在 ``URI`` 中，也可以设定在 ``domain`` 中。

对于已编码的请求 ``URI`` ，框架会自动进行解码然后进行匹配:

必填参数
--------
当然，有时需要在路由中捕获一些 ``URL`` 片段。例如，从 ``URL`` 中捕获用户的 ``ID`` ，可以通过定义路由参数来执行此操作：

.. code-block:: php

    <?php
    Route::get('user/{id}', function ($id) {
        return 'User '.$id;
    });

也可以根据需要在路由中定义多个参数：

.. code-block:: php

    <?php
    Route::get('posts/{post}/comments/{comment}', function ($postId, $commentId) {
        //
    });

路由的参数通常都会被放在 ``{}`` 内，并且参数名只能为字母，同时路由参数不能包含 ``-`` 符号，如果需要可以用下划线 ( ``_`` ) 代替。 **路由参数会按顺序依次被注入到路由回调或者控制器中，而不受回调或者控制器的参数名称的影响。**

可选参数
--------
有时，你可能需要指定一个路由参数，但你希望这个参数是可选的。你可以在参数后面加上 ``?`` 标记来实现，但前提是要确保路由的相应变量有默认值：

.. code-block:: php

    <?php
    Route::get('user/{name?}', function ($name = null) {
        return $name;
    });

    Route::get('user/{name?}', function ($name = 'John') {
        return $name;
    });

正则表达式约束
--------------
你可以使用路由实例上的 ``where`` 方法约束路由参数的格式。 ``where`` 方法接受参数名称和定义参数应如何约束的正则表达式：

.. code-block:: php

    <?php
    Route::get('user/{name}', function ($name) {
        //
    })->where('name', '[A-Za-z]+');

    Route::get('user/{id}', function ($id) {
        //
    })->where('id', '[0-9]+');

    Route::get('user/{id}/{name}', function ($id, $name) {
        //
    })->where(['id' => '[0-9]+', 'name' => '[a-z]+']);
    //或者
    Route::get('{one}', ['where' => ['one' => '(.+)'], function () {
    }]);

全局约束
^^^^^^^^
如果你希望某个具体的路由参数都遵循同一个正则表达式的约束，就使用 ``pattern`` 方法在 ``RouteServiceProvider`` 的 ``boot`` 方法中定义这些模式：

.. code-block:: php

    <?php
    /**
     * 定义你的路由模型绑定, pattern 过滤器等。
     *
     * @return void
     */
    public function boot()
    {
        Route::pattern('id', '[0-9]+');

        parent::boot();
    }

一旦定义好之后，便会自动应用这些规则到所有使用该参数名称的路由上：

.. code-block:: php

    <?php
    Route::get('user/{id}', function ($id) {
        // Only executed if {id} is numeric...
    });

路由属性
========
在调用方法如 ``get`` ``post`` 或者 ``group`` 等前，可以定义路由的属性( ``as`` ,  ``domain`` ,  ``middleware`` ,  ``name`` ,  ``namespace`` ,  ``prefix`` )，如果你在 ``get`` ``post`` 等方法之后可调用 ``name`` ， ``domain`` ， ``middleware`` ， ``prefix`` 。而在 ``group`` 方法之后不能调用任何函数。

路由属性的合并：

- ``prefix`` ``、name`` 这几个属性会连接在一起，例如 prefix1/prefix2/prefix3，
- ``where`` 属性数组相同的会被替换，不同的会被合并。
- ``domain`` 属性会被替换。
- 其他属性，例如 ``middleware`` 数组会直接被合并，即使存在相同的元素。


路由名称
--------
路由命名可以方便地为指定路由生成 ``URL`` 或者重定向。通过在路由定义上链式调用 ``name`` 方法可以指定路由名称：

.. code-block:: php

    <?php
    Route::get('user/profile', function () {
        //
    })->name('profile');

你还可以指定控制器行为的路由名称：

.. code-block:: php

    <?php
    Route::get('user/profile', 'UserController@showProfile')->name('profile');

路由 as 别名
^^^^^^^^^^^^

可以为路由指定别名，通过路由属性的 ``as`` 来指定：

.. code-block:: php

    <?php
    Route::as('Foo')
         ->get('foo/bar', function () {
        });

    Route::name('Foo')
         ->get('foo/bar', function () {
        });

    Route::get('foo/bar', ['as' => 'Foo', function () {
        }]);

    Route::get('foo/bar', function () {
        })->name('Foo');

``name`` 是 ``as`` 的别名，所以两者是等同的。底层使用 ``as`` 属性存储路由名称。

生成指定路由的 URL
^^^^^^^^^^^^^^^^^^
为路由指定了名称后，就可以使用全局辅助函数 ``route`` 来生成链接或者重定向到该路由：

.. code-block:: php

    <?php
    // 生成 URL...
    $url = route('profile');

    // 生成重定向...
    return redirect()->route('profile');

如果是有定义参数的命名路由，可以把参数作为 ``route`` 函数的第二个参数传入，指定的参数将会自动插入到 ``URL`` 中对应的位置：

.. code-block:: php

    <?php
    Route::get('user/{id}/profile', function ($id) {
        //
    })->name('profile');

    $url = route('profile', ['id' => 1]);

检查当前路由
^^^^^^^^^^^^
如果你想判断当前请求是否指向了某个路由，你可以调用路由实例上的 ``named`` 方法。例如，你可以在路由中间件中检查当前路由名称：

.. code-block:: php

    <?php
    /**
     * 处理一次请求。
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    public function handle($request, Closure $next)
    {
        if ($request->route()->named('profile')) {
            //
        }

        return $next($request);
    }

路由组
======
路由组允许你在大量路由之间共享路由属性，例如中间件或命名空间，而不需要为每个路由单独定义这些属性。共享属性应该以数组的形式传入 ``Route::group`` 方法的第一个参数中。

中间件
------
要给路由组中所有的路由分配中间件，可以在 ``group`` 之前调用 ``middleware`` 方法，中间件会依照它们在数组中列出的顺序来运行：

.. code-block:: php

    <?php
    Route::middleware(['first', 'second'])->group(function () {
        Route::get('/', function () {
            // 使用 first 和 second 中间件
        });

        Route::get('user/profile', function () {
            // 使用 first 和 second 中间件
        });
    });
    //或者
    Route::get('foo/bar', ['middleware' => 'web', function () {
    }]);

命名空间
--------
另一个常见用例是使用 ``namespace`` 方法将相同的 ``PHP`` 命名空间分配给路由组的中所有的控制器：

.. code-block:: php

    <?php
    Route::namespace('Admin')->group(function () {
        // 在 "App\Http\Controllers\Admin" 命名空间下的控制器
    });
    // 或者
    Route::get('foo/bar', ['namespace' => 'Namespace\\Example\\', function () {
    }]);

.. note:: 默认情况下， ``RouteServiceProvider`` 会在命名空间组中引入你的路由文件，让你不用指定完整的 ``App\Http\Controllers`` 命名空间前缀就能注册控制器路由。因此，你只需要指定命名空间 ``App\Http\Controllers`` 之后的部分。

子域名路由
----------
路由组也可以用来处理子域名。子域名可以像路由 ``URI`` 一样被分配路由参数，允许你获取一部分子域名作为参数给路由或控制器使用。可以在 ``group`` 之前调用 ``domain`` 方法来指定子域名：

.. code-block:: php

    <?php
    Route::domain('{account}.myapp.com')->group(function () {
        Route::get('user/{id}', function ($account, $id) {
            //
        });
    });
    //或者
    Route::get('foo/bar', ['domain' => 'api.name.bar', function ($name) {
        return $name;
    }]);

路由前缀
--------
可以用 ``prefix`` 方法为路由组中给定的 ``URL`` 增加前缀。例如，你可以为组中所有路由的 ``URI`` 加上 ``admin`` 前缀：

.. code-block:: php

    <?php
    Route::prefix('admin')->group(function () {
        Route::get('users', function () {
            // 匹配包含 "/admin/users" 的 URL
        });
    });
    //或者
    Route::get('foo/bar', ['prefix' => 'pre', function () {
    }]);

路由名称前缀
------------
``name`` 方法可以用来给路由组中的每个路由名称添加一个给定的字符串。例如，您可能希望以 「admin」为所有分组路由的名称加前缀。给定的字符串与指定的路由名称前缀完全相同，因此我们将确保在前缀中提供尾部的 ``.`` 字符：

.. code-block:: php

    <?php
    Route::name('admin.')->group(function () {
        Route::get('users', function () {
            // 路由分配名称“admin.users”...
        })->name('users');
    });

路由 scheme 协议
----------------
对于 ``web`` 后台框架来说，路由的 ``scheme`` 底层协议一般使用 ``http`` 、 ``https`` :

.. code-block:: php

    <?php
    Route::get('foo/{bar}', ['http', function () {
        }]);
    Route::get('foo/{bar}', ['https', function () {
        }]);

路由 uses 属性
--------------
可以为路由添加 ``URI`` 对应的执行逻辑，例如闭包或者控制器：

.. code-block:: php

    <?php
    Route::get('foo/bar', ['uses' => function () {
        }]);

    Route::get('foo/bar', ['uses' => ‘Illuminate\Tests\Routing\RouteTestControllerStub@index’]);

    Route::get('foo/bar')->uses(function () {
        });

    Route::get('foo/bar')->uses(‘Illuminate\Tests\Routing\RouteTestControllerStub@index’);

路由 group 群组属性
-------------------
可以为一系列具有类似属性的路由归为同一组，利用 ``group`` 将这些路由归并到一起：

.. code-block:: php

    <?php
    Route::group(['domain'     => 'group.domain.name',
                  'prefix'     => 'grouppre',
                  'where'      => ['one' => '(.+)'],
                  'middleware' => 'groupMiddleware',
                  'namespace'  => 'Namespace\\Group\\',
                  'as'         => 'Group::',]
                  function () {
                      Route::get('/replace',‘domain’ => 'route.domain.name'，
                                            'uses'   => function () {
                                            return 'replace';
                      });

                     Route::get('additional/{one}/{two}', 'prefix'     => 'routepre',
                                                          'where'      => '['one' => '([0-9]+)','two' => '(.+)']',
                                                          'middleware' => 'routeMiddleware',
                                                          'namespace'  => 'Namespace\\Group\\',
                                                          'as'         => 'Route',
                                                          'use         => 'function () {
                                                          return 'additional';
                     });
    });

``group`` 群组的属性分为两类：替换型、递增型。当群组属性与路由属性重复的时候，替换型属性会用路由的属性替换群组的属性，递增型的属性会综合路由和群组的属性。

在上面的例子可以看出：

- ``domain`` 这个属性是替换型属性，路由的属性会覆盖和替换群组的这几个属性；
- ``prefix`` 、 ``middleware`` 、 ``namespace`` 、 ``as`` 、 ``where`` 这几个属性是递增型属性，路由的属性和群组属性会相互结合。

另外值得注意的是：

- 路由的 ``prefix`` 属性具有优先级,因此上面第二个路由的 ``uri`` 是 ``routepre/grouppre/additional/111/add`` ,而不是 ``grouppre/routepre/additional/111/add`` ；
- ``where`` 属性对于相同的路由参数会替换，不同的路由参数会结合，因此上面 ``where`` 中 ``one`` 被替换， ``two`` 被结合进来；

路由模型绑定
============
当向路由或控制器行为注入模型 ``ID`` 时，就需要查询这个 ``ID`` 对应的模型。 Laravel 为路由模型绑定提供了一个直接自动将模型实例注入到路由中的方法。例如，你可以注入与给定 ``ID`` 匹配的整个 ``User`` 模型实例，而不是注入用户的 ``ID`` 。

关于路由绑定的实现参考 ``Laravel的中间件-SubstituteBindings`` 。



隐式绑定
--------
Laravel 会自动解析定义在路由或控制器行为中与类型提示的变量名匹配的路由段名称的 ``Eloquent`` 模型。例如：

.. code-block:: php

    <?php
    Route::get('api/users/{user}', function (App\User $user) {
        return $user->email;
    });

在这个例子中，由于 ``$user`` 变量被类型提示为 ``Eloquent`` 模型 ``App\User`` ，变量名称又与 ``URI`` 中的 ``{user}`` 匹配，因此，Laravel 会自动注入与请求 ``URI`` 中传入的 ``ID`` 匹配的用户模型实例。如果在数据库中找不到对应的模型实例，将会自动生成 ``404`` 异常。

.. note:: 需要注意的是，在隐式模型绑定中 ``route`` 定义路由的路由参数必须和控制器内的变量名相同，例如下例中路由参数 ``userid`` 和控制器参数 ``userid`` 。

自定义键名
^^^^^^^^^^
如果你想要模型绑定在检索给定的模型类时使用除 ``id`` 之外的数据库字段，你可以在 ``Eloquent`` 模型上重写 ``getRouteKeyName`` 方法：

.. code-block:: php

    <?php
    /**
     * 为路由模型获取键名。
     *
     * @return string
     */
    public function getRouteKeyName()
    {
        return 'slug';
    }

显式绑定
--------

使用model绑定
^^^^^^^^^^^^^

要注册显式绑定，使用路由器的 ``model`` 方法来为给定参数指定类。在 ``RouteServiceProvider`` 类中的 ``boot`` 方法内定义这些显式模型绑定：

.. code-block:: php

    <?php
    public function boot()
    {
        parent::boot();

        Route::model('user', App\User::class);
    }

接着，定义一个包含 ``{user}`` 参数的路由:

.. code-block:: php

    <?php
    Route::get('profile/{user}', function (App\User $user) {
        //
    });

因为我们已经将所有 ``{user}`` 参数绑定至 ``App\User`` 模型，所以 ``User`` 实例将被注入该路由。例如， ``profile/1`` 的请求会注入数据库中 ``ID`` 为 ``1`` 的 ``User`` 实例。

如果在数据库不存在对应 ``ID`` 的数据，就会自动抛出一个 ``404`` 异常。

.. note:: 通过 ``model`` 为参数绑定数据库模型，路由的参数就不需要和控制器方法中的变量名相同，但路由参数必须和 ``model`` 方法的第一个参数名称一致。


若绑定的 ``model`` 并没有找到对应路由参数的记录，可以在 ``model`` 中定义一个闭包函数，路由参数会调用闭包函数：

.. code-block:: php

    <?php
    public function testModelBindingWithCustomNullReturn()
    {
        $router = $this->getRouter();
        $router->get('foo/{bar}', ['middleware' => SubstituteBindings::class, 'uses' => function ($name) {
            return $name;
        }]);
        $router->model('bar', 'Illuminate\Tests\Routing\RouteModelBindingNullStub', function () {
            return 'missing';
        });
        $this->assertEquals('missing', $router->dispatch(Request::create('foo/taylor', 'GET'))->getContent());
    }

    public function testModelBindingWithBindingClosure()
    {
        $router = $this->getRouter();
        $router->get('foo/{bar}', ['middleware' => SubstituteBindings::class, 'uses' => function ($name) {
            return $name;
        }]);
        $router->model('bar', 'Illuminate\Tests\Routing\RouteModelBindingNullStub', function ($value) {
            return (new RouteModelBindingClosureStub())->findAlternate($value);
        });
        $this->assertEquals('tayloralt', $router->dispatch(Request::create('foo/TAYLOR', 'GET'))->getContent());
    }

    class RouteModelBindingNullStub
    {
        public function getRouteKeyName()
        {
            return 'id';
        }

        public function where($key, $value)
        {
            return $this;
        }

        public function first()
        {
        }
    }

    class RouteModelBindingClosureStub
    {
        public function findAlternate($value)
        {
            return strtolower($value).'alt';
        }
    }


使用bind绑定
^^^^^^^^^^^^
- 通过 bind 为参数绑定闭包函数：如果你想要使用自定义的解析逻辑，就使用 ``Route::bind`` 方法。传递到 ``bind`` 方法的 闭包 会接受 ``URI`` 中大括号对应的值，并且返回你想要在该路由中注入的类的实例：

  .. code-block:: php

    <?php
    public function boot()
    {
        parent::boot();

        Route::bind('user', function ($value) {
            return App\User::where('name', $value)->first() ?? abort(404);
        });
    }

  ``bind()`` 方法第一个参数必须和路由路径中的参数一致；第二个参数只支持传入闭包，该闭包函数接受路由中当前键对应的值和当前路由项。

- 通过 ``bind`` 为参数绑定类方法，可以指定 ``classname@method`` ，也可以直接使用类名，默认会调用类的 ``bind`` 函数：

  .. code-block:: php

    <?php
    public function testRouteClassBinding()
    {
        $router = $this->getRouter();
        $router->get('foo/{bar}', ['middleware' => SubstituteBindings::class, 'uses' => function ($name) {
            return $name;
        }]);
        $router->bind('bar', 'Illuminate\Tests\Routing\RouteBindingStub');
        $this->assertEquals('TAYLOR', $router->dispatch(Request::create('foo/taylor', 'GET'))->getContent());
    }

    public function testRouteClassMethodBinding()
    {
        $router = $this->getRouter();
        $router->get('foo/{bar}', ['middleware' => SubstituteBindings::class, 'uses' => function ($name) {
            return $name;
        }]);
        $router->bind('bar', 'Illuminate\Tests\Routing\RouteBindingStub@find');
        $this->assertEquals('dragon', $router->dispatch(Request::create('foo/Dragon', 'GET'))->getContent());
    }

    class RouteBindingStub
    {
        public function bind($value, $route)
        {
            return strtoupper($value);
        }

        public function find($value, $route)
        {
            return strtolower($value);
        }
    }

访问控制
========
Laravel 包含了一个 中间件 用于控制应用程序对路由的访问。如果想要使用，请将 ``throttle`` 中间件分配给一个路由或一个路由组。 ``throttle`` 中间件会接收两个参数，这两个参数决定了在给定的分钟数内可以进行的最大请求数。 例如，让我们指定一个经过身份验证并且用户每分钟访问频率不超过 60 次的路由：

.. code-block:: php

    <?php
    Route::middleware('auth:api', 'throttle:60,1')->group(function () {
        Route::get('/user', function () {
            //
        });
    });

动态访问控制
------------
您可以根据已验证的 ``User`` 模型的属性指定动态请求的最大值。 例如，如果您的 ``User`` 模型包含 ``rate_limit`` 属性，则可以将属性名称传递给 ``throttle`` 中间件，以便它用于计算最大请求计数：

.. code-block:: php

    <?php
    Route::middleware('auth:api', 'throttle:rate_limit,1')->group(function () {
        Route::get('/user', function () {
            //
        });
    });

表单方法伪造
============
``HTML`` 表单不支持 ``PUT`` 、 ``PATCH`` 或 ``DELETE`` 行为。所以当你要从 ``HTML`` 表单中调用定义了 ``PUT`` 、 ``PATCH`` 或 ``DELETE`` 路由时，你将需要在表单中增加隐藏的 ``_method`` 输入标签。使用 ``_method`` 字段的值作为 ``HTTP`` 的请求方法：

.. code-block:: php

    <?php
    <form action="/foo/bar" method="POST">
        <input type="hidden" name="_method" value="PUT">
        <input type="hidden" name="_token" value="{{ csrf_token() }}">
    </form>

你也可以使用 ``@method`` Blade 指令生成 ``_method`` 输入：

.. code-block:: php

    <?php
    <form action="/foo/bar" method="POST">
        @method('PUT')
        @csrf
    </form>

访问当前路由
============
你可以使用 ``Route`` Facade 上的 ``current`` 、 ``currentRouteName`` 和 ``currentRouteAction`` 方法来访问处理传入请求的路由的信息：

.. code-block:: php

    <?php
    $route = Route::current();

    $name = Route::currentRouteName();

    $action = Route::currentRouteAction();


路由命令
========
.. note:: 基于闭包的路由不能被缓存。如果要使用路由缓存，你必须将所有的闭包路由转换成控制器类路由。

如果你的应用只使用了基于控制器的路由，那么你应该充分利用 Laravel 的路由缓存的优势。使用路由缓存将极大地减少注册所有应用路由所需的时间。某些情况下，路由注册的速度甚至可以快一百倍。要生成路由缓存，只需执行 ``Artisan`` 命令 ``route:cache`` ：

路由缓存
--------
Artisan 命令 ``route:cache`` 可以把 ``routes.php`` 文件中的所有路由定义序列化 -- 会对所有的路由进行一次解析然后把解析结果缓存起来。

路由缓存清理
------------
执行完 ``route:cache`` 这个命令以后，所有的路由都是从缓存文件而不是路由文件进行读取。之后你可以对 ``routes.php`` 的随意进行修改, 但应用的路由不会发生变化, 一直到再次执行 ``route:cache`` 重新缓存。

路由列表
---------
Artisan 命令 ``route:list`` 列出所有注册的路由。

router扩展方法
==============
``router`` 支持添加自定义的方法，只需要利用 ``macro`` 函数来注册对应的函数名和函数实现：

.. code-block:: php

    <?php
    public function testMacro()
    {
        $router = $this->getRouter();
        $router->macro('webhook', function () use ($router) {
            $router->match(['GET', 'POST'], 'webhook', function () {
                return 'OK';
            });
        });
        $router->webhook();
        $this->assertEquals('OK', $router->dispatch(Request::create('webhook', 'GET'))->getContent());
        $this->assertEquals('OK', $router->dispatch(Request::create('webhook', 'POST'))->getContent());
    }

