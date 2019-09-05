*************
Facade的原理
*************

首先，我们要知道 laravel 框架的核心就是个 ``Ioc`` 容器即 服务容器，功能类似于一个工厂模式，是个高级版的工厂。 laravel 的其他功能例如路由、缓存、日志、数据库其实都是类似于插件或者零件一样，叫做 服务。 ``Ioc`` 容器主要的作用就是生产各种零件，就是提供各个服务。在 laravel 中，如果我们想要用某个服务，该怎么办呢？最简单的办法就是调用服务容器的 ``make`` 函数，或者利用依赖注入，或者就是今天要讲的门面 ``Facade`` 。门面相对于其他方法来说，最大的特点就是简洁，例如我们经常使用的 ``Router`` ，如果利用服务容器的 ``make`` ：

.. code-block:: php

    <?php
    App::make('router')->get('/', function () {
      return view('welcome');
    });

如果利用门面：

.. code-block:: php

    <?php
    Route::get('/', function () {
      return view('welcome');
    });

可以看出代码更加简洁。其实，下面我们就会介绍门面最后调用的函数也是服务容器的 ``make`` 函数。

Facade 的原理
=============
我们以 ``Route`` 为例，来讲解一下门面 ``Facade`` 的原理与实现。我们先来看 ``Route`` 的门面类：

文件 ``Illuminate\Support\Facades\Route.php``

.. code-block:: php

    <?php
    class Route extends Facade
    {
        // 获取注册组件的注册名称
        protected static function getFacadeAccessor()
        {
            return 'router';
        }
    }

每个门面类也就是重定义一下 ``getFacadeAccessor`` 函数就行了，这个函数返回服务的唯一名称： ``router`` 。需要注意的是要确保这个名称可以用服务容器的 ``make`` 函数创建成功( ``App::make('router')`` )，原因我们马上就会讲到。
那么当我们写出 ``Route::get()`` 这样的语句时，到底发生了什么呢？奥秘就在基类 ``Facade`` 中。

文件 ``Illuminate\Support\Facades\Facade.php``

.. code-block:: php

    <?php
    public static function __callStatic($method, $args)
    {
        $instance = static::getFacadeRoot();

        if (! $instance) {
            throw new RuntimeException('A facade root has not been set.');
        }

        return $instance->$method(...$args);
    }

    public static function getFacadeRoot()
    {
        return static::resolveFacadeInstance(static::getFacadeAccessor());
    }

    protected static function getFacadeAccessor()
    {
        throw new RuntimeException('Facade does not implement getFacadeAccessor method.');
    }

    protected static function resolveFacadeInstance($name)
    {
         if (is_object($name)) {
             return $name;
         }

         if (isset(static::$resolvedInstance[$name])) {// 如果该名称对象已经解析，则返回以前的
             return static::$resolvedInstance[$name];
         }

         return static::$resolvedInstance[$name] = static::$app[$name];// 否则利用容器获取，然后保存
    }

我们看到基类 ``getFacadeRoot()`` 调用了 ``getFacadeAccessor()`` ，也就是我们的服务重载的函数，如果调用了基类的 ``getFacadeAccessor`` ，就会抛出异常。在我们的例子里 ``getFacadeAccessor()`` 返回了 “router” ，接下来 ``getFacadeRoot()`` 又调用了 ``resolveFacadeInstance()`` 。在这个函数里重点就是

.. code-block:: php

    <?php
    return static::$resolvedInstance[$name] = static::$app[$name];

我们看到，在这里利用了 ``app`` 也就是服务容器创建了 “router”，创建成功后放入 ``resolvedInstance`` 作为缓存，以便以后快速加载。
好了， ``Facade`` 的原理到这里就讲完了，但是到这里我们有个疑惑，为什么代码中写 ``Route`` 就可以调用 ``Illuminate\Support\Facades\Route`` 呢？这个就是别名的用途了，很多门面都有自己的别名，这样我们就不必在代码里面写 ``use Illuminate\Support\Facades\Route`` ，而是可以直接用 ``Route`` 了。

别名 Aliases
============
为什么我们可以在 larval 中全局用 ``Route`` ，而不需要使用 ``use IlluminateSupportFacadesRoute`` ?其实奥秘在于一个 PHP 函数： ``class_alias`` ，它可以为任何类创建别名。larval 在启动的时候为各个门面类调用了 ``class_alias`` 函数，因此不必直接用类名，直接用别名即可。在 ``config`` 文件夹的 ``app`` 文件里面存放着门面与类名的映射：

.. code-block:: php

    <?php
    'aliases' => [

        'App' => Illuminate\Support\Facades\App::class,
        'Artisan' => Illuminate\Support\Facades\Artisan::class,
        'Auth' => Illuminate\Support\Facades\Auth::class,
        ...
       ]

下面我们来看看 ``laravel`` 是如何为门面类创建别名的。

启动别名Aliases服务
-------------------
Laravel 使用启动组件 ``\Illuminate\Foundation\Bootstrap\LoadConfiguration::class`` 来加载定义的别名数组配置。然后使用启动组件 ``\Illuminate\Foundation\Bootstrap\RegisterFacades::class`` 来配置 ``Facade`` 类属性

文件 ``\Illuminate\Foundation\Bootstrap\RegisterFacades::class``

.. code-block:: php

    <?php
    class RegisterFacades
    {
        public function bootstrap(Application $app)
        {
            Facade::clearResolvedInstances();

            Facade::setFacadeApplication($app);

            AliasLoader::getInstance($app->make('config')->get('app.aliases', []))->register();
        }
    }

可以看出来，bootstrap 做了一下几件事：

1. 清除了 ``Facade`` 中的缓存
2. 设置 ``Facade`` 的 ``Ioc`` 容器
3. 获得我们前面讲的 ``config`` 文件夹里面 ``app`` 文件 ``aliases`` 别名映射数组
4. 使用 ``aliases`` 实例化初始化 ``AliasLoader``
5. 调用 ``AliasLoader->register()``

文件 ``Illuminate\Foundation\AliasLoader.php``

.. code-block:: php

    <?php
    public function register()
    {
        if (! $this->registered) {
             $this->prependToLoaderStack();

             $this->registered = true;
        }
    }

    protected function prependToLoaderStack()
    {
        spl_autoload_register([$this, 'load'], true, true);
    }

    public function load($alias)
    {
        // 这个是 laravel5.4 版本新出的功能叫做实时门面服务
        if (static::$facadeNamespace && strpos($alias, static::$facadeNamespace) === 0) {
           $this->loadFacade($alias);

           return true;
        }
        // class_alias 利用别名映射数组将别名映射到真正的门面类中去
        if (isset($this->aliases[$alias])) {
             return class_alias($this->aliases[$alias], $alias);
        }
    }

我们可以看出，别名服务的启动关键就是这个 ``spl_autoload_register`` ，这个函数我们应该很熟悉了，在自动加载中这个函数用于解析命名空间，在这里用于解析别名的真正类名。所以，我们可以在任何类中直接使用别名配置的键( ``Route`` )作为值( ``Illuminate\Support\Facades\Route`` )的别名。

实时门面服务
------------
其实门面功能已经很简单了，我们只需要定义一个类继承 ``Facade`` 即可，但是 laravel5.4 打算更近一步——自动生成门面子类，这就是实时门面。
实时门面怎么用？看下面的为应用服务类生成门面的例子：

.. code-block:: php

    <?php
    namespace App\Services;

    class PaymentGateway
    {
        protected $tax;

        public function __construct(TaxCalculator $tax)
        {
            $this->tax = $tax;
        }
    }

这是一个自定义的类，如果我们想要为这个类定义一个门面，在 laravel5.4 我们可以这么做：

.. code-block:: php

    <?php
    use Facades\ {
        App\Services\PaymentGateway
    };

    Route::get('/pay/{amount}', function ($amount) {
        PaymentGateway::pay($amount);
    });

那么这么做的原理是什么呢？我们接着看源码：

文件 ``Illuminate\Foundation\AliasLoader.php``

.. code-block:: php

    <?php
    protected static $facadeNamespace = 'Facades\\';
    // 如果命名空间是以 Facades\ 开头的，那么就会调用实时门面的功能，调用 loadFacade 函数：
    if (static::$facadeNamespace && strpos($alias, static::$facadeNamespace) === 0) {
         $this->loadFacade($alias);

         return true;
    }

    //loadFacade 负责加载门面类
    protected function loadFacade($alias)
    {
        require $this->ensureFacadeExists($alias);
    }

    // ensureFacadeExists 函数负责自动生成门面类
    protected function ensureFacadeExists($alias)
    {
        if (file_exists($path = storage_path('framework/cache/facade-'.sha1($alias).'.php'))) {
            return $path;
        }

        file_put_contents($path, $this->formatFacadeStub(
            $alias, file_get_contents(__DIR__.'/stubs/facade.stub')
        ));

        return $path;
    }

    protected function formatFacadeStub($alias, $stub)
    {
        $replacements = [
            str_replace('/', '\\', dirname(str_replace('\\', '/', $alias))),
            class_basename($alias),
            substr($alias, strlen(static::$facadeNamespace)),
        ];

        return str_replace(
            ['DummyNamespace', 'DummyClass', 'DummyTarget'], $replacements, $stub
                );
    }

可以看出来， ``laravel`` 框架生成的门面类会放到 ``stroge/framework/cache/`` 文件夹下，名字以 ``facade`` 开头，以命名空间的哈希结尾。如果存在这个文件就会返回，否则就要利用 ``file_put_contents`` 生成这个文件。

门面模板文件 ``Illuminate\Foundation\stubs\facade.stub``

.. code-block:: php

    <?php
    namespace DummyNamespace;

    use Illuminate\Support\Facades\Facade;

    /**
    * @see \DummyTarget
    */
    class DummyClass extends Facade
    {
        /**
        * Get the registered name of the component.
        *
        * @return string
        */
        protected static function getFacadeAccessor()
        {
            return 'DummyTarget';
        }
    }

替换后的文件是：

.. code-block:: php

    <?php
    namespace Facades\App\Services\;

    use Illuminate\Support\Facades\Facade;

    /**
    * @see \DummyTarget
    */
    class PaymentGateway extends Facade
    {
        /**
         * Get the registered name of the component.
         *
         * @return string
         */
        protected static function getFacadeAccessor()
        {
            return 'App\Services\PaymentGateway'; // 这里不是别名，直接完全类定义文件
        }
    }

