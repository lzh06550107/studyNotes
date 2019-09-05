**********
服务提供器
**********

简介
====
服务提供器是所有 Laravel 应用程序引导中心。你的应用程序以及 Laravel 的所有核心服务都是通过服务提供器进行引导。

在这里，我们说的「引导」其实是指注册，比如注册服务容器绑定、事件监听器、中间件，甚至是路由的注册。服务提供器是配置你的应用程序的中心。

Laravel 的 ``config/app.php`` 文件中有一个 ``providers`` 数组。数组中的内容是应用程序要加载的所有服务提供器类。这其中有许多提供器并不会在每次请求时都被加载，只有当它们提供的服务实际需要时才会加载。这种我们称之为「延迟」提供器。

本篇将带你学习如何编写自己的服务提供器，并将其注册到 Laravel 应用程序中。

编写服务提供器
==============
所有服务提供器都会继承 ``Illuminate\Support\ServiceProvider`` 类。大多数服务提供器都包含 ``register`` 和 ``boot`` 方法。在 ``register`` 方法中，你只需要绑定类到服务容器 。而不需要尝试在 ``register`` 方法中注册任何事件监听器、路由或任何其他功能。

使用 ``Artisan`` 命令行界面，通过 ``make:provider`` 命令生成一个新的提供器：

.. code-block:: shell

    php artisan make:provider RiakServiceProvider

注册方法
--------
在 ``register`` 方法中，你只需要将类绑定到服务容器中。而不需要尝试在 ``register`` 方法中注册任何事件监听器、路由或者任何其他功能。否则，你可能会意外使用到尚未加载的服务提供器提供的服务。

让我们来看一个基本的服务提供器。在你的任何服务提供器方法中，你可以通过 ``$app`` 属性来访问服务容器：

.. code-block:: php

    <?php

    namespace App\Providers;

    use Riak\Connection;
    use Illuminate\Support\ServiceProvider;

    class RiakServiceProvider extends ServiceProvider
    {
        /**
         * 在服务容器里注册
         *
         * @return void
         */
        public function register()
        {
            $this->app->singleton(Connection::class, function ($app) {
                return new Connection(config('riak'));
            });
        }
    }

这个服务提供器只定义了一个 ``register`` 方法，并使用该方法在服务容器中定义了一个 ``Riak\Connection`` 实现。 如果你不了解服务容器的工作原理，请查看其 文档。

bindings 和 singletons 属性
---------------------------
如果你的服务提供商注册许多简单的绑定，你可能想使用 bindings 和 singletons 属性而不是手动调用方法注册每个容器绑定。当服务提供者被框架加载时，它将自动检查这些属性并注册它们的绑定：

.. code-block:: php

    <?php
    class AppServiceProvider extends ServiceProvider
    {
        /**
         * 设定容器绑定的对应关系
         *
         * @var array
         */
        public $bindings = [
            ServerProvider::class => DigitalOceanServerProvider::class,
        ];

        /**
         * 设定单例模式的容器绑定对应关系
         *
         * @var array
         */
        public $singletons = [
            DowntimeNotifier::class => PingdomDowntimeNotifier::class,
        ];
    }

上面没有找到实现代码，还需要进一步研究。

引导方法
--------
那么，如果我们需要在我们的服务提供器中注册一个视图组件呢？这应该在 ``boot`` 方法中完成。 此方法在所有其他服务提供器都注册之后才能调用，这意味着你可以访问已经被框架注册的所有服务：

.. code-block:: php

    <?php

    namespace App\Providers;

    use Illuminate\Support\ServiceProvider;

    class ComposerServiceProvider extends ServiceProvider
    {
        /**
         * 启动所有的应用服务
         *
         * @return void
         */
        public function boot()
        {
            view()->composer('view', function () {
                //
            });
        }
    }

引导方法依赖注入
^^^^^^^^^^^^^^^^
你可以为服务提供器的 ``boot`` 方法设置类型提示。服务容器 会自动注入你需要的任何依赖项：

.. code-block:: php

    <?php
    use Illuminate\Contracts\Routing\ResponseFactory;

    public function boot(ResponseFactory $response)
    {
        $response->macro('caps', function ($value) {
            //
        });
    }

注册提供器
==========
所有服务提供器都在 ``config/app.php`` 配置文件中注册。该文件中有一个 ``providers`` 数组，用于存放服务提供器的类名 。默认情况下，这个数组列出了一系列 Laravel 核心服务提供器。这些服务提供器引导 Laravel 核心组件，例如邮件、队列、缓存等。

要注册提供器，只需要将其添加到数组：

.. code-block:: php

    <?php
    'providers' => [
        // 其他服务提供器

        App\Providers\ComposerServiceProvider::class,
    ],

服务提供器分类
==============

立即服务提供器
--------------
在启动容器的时候就已经调用该服务提供器的 ``register()`` 方法和 ``boot()`` 方法。用于每次请求都需要的服务注册。


延迟服务提供器
--------------
如果你的提供器不需要容器启动时就注册，就可以选择推迟其注册，直到当它真正需要时再注册绑定。推迟加载这种提供器会提高应用程序的性能，因为它不会在每次请求时都从文件系统中加载。

``Laravel`` 编译并保存延迟服务提供器提供的所有服务的列表，以及其服务提供器类的名称。因此，只有当你在尝试解析其中一项服务时， ``Laravel`` 才会加载服务提供器。

要延迟提供器的加载，请将 ``defer`` 属性设置为 ``true`` ，并定义 ``provides`` 方法。 ``provides`` 方法应该返回由提供器注册的服务容器绑定：

.. code-block:: php

    <?php

    namespace App\Providers;

    use Riak\Connection;
    use Illuminate\Support\ServiceProvider;

    class RiakServiceProvider extends ServiceProvider
    {
        /**
         * 标记着提供器是延迟加载的
         *
         * @var bool
         */
        protected $defer = true;

        /**
         * 注册服务提供者提供的服务(先绑定)
         *
         * @return void
         */
        public function register()
        {
            $this->app->singleton(Connection::class, function ($app) {
                return new Connection($app['config']['riak']);
            });
        }

        /**
         * 取得提供者提供的服务别名(当需要时通过别名来注册服务)
         *
         * @return array
         */
        public function provides()
        {
            return [Connection::class];
        }

    }

事件服务提供器
--------------
当需要时通过触发特定的事件来提供注册服务时，可以使用事件服务提供器。事件服务提供器必定是延迟服务提供器，但延迟服务提供器不一定是事件服务提供器。

文件 ``Illuminate\Foundation\ProviderRepository.php``

.. code-block:: php

    <?php
    protected function registerLoadEvents($provider, array $events)
    {
        if (count($events) < 1) {
            return;
        }
        // 注册事件监听器，当事件发生时，调用回调函数来注册服务提供者，即调用服务提供器的register()方法
        $this->app->make('events')->listen($events, function () use ($provider) {
            $this->app->register($provider);
        });
    }

由上面源码可知，需要在继承 ``Illuminate\Support\ServiceProvider.php`` 延迟服务提供器中实现 ``when()`` 方法。

.. code-block:: php

    <?php
    public function when()
    {
        return []; // 此处为事件名称数组
    }
