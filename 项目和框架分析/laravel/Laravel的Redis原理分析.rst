=============
Redis原理分析
=============


Laravel中的Redis使用流程

首先通过Redis门面来初始化RedisManger对象。

文件 ``routes\web.php``

.. code-block:: php

    <?php
    Route::get('cats/{cat}', function(App\Cat $cat) {
        Redis::set('name', 'Taylor'); // 这里存储数据到Redis服务器中
        return view('cats.show')->with('cat', $cat);
    })->where('cat', '[0-9]+');

文件 ``\Illuminate\Support\Facades\Redis``

.. code-block:: php

    <?php
    protected static function getFacadeAccessor()
    {
        return 'redis'; // 获取容器中的RedisManager对象实例
    }

文件 ``\Illuminate\Foundation\Application.php``

.. code-block:: php

    <?php
    public function registerCoreContainerAliases()
    {
        foreach ([
                     'app'                  => [\Illuminate\Foundation\Application::class, \Illuminate\Contracts\Container\Container::class, \Illuminate\Contracts\Foundation\Application::class,  \Psr\Container\ContainerInterface::class],
                     'auth'                 => [\Illuminate\Auth\AuthManager::class, \Illuminate\Contracts\Auth\Factory::class],
                     'auth.driver'          => [\Illuminate\Contracts\Auth\Guard::class],
                     'blade.compiler'       => [\Illuminate\View\Compilers\BladeCompiler::class],
                     'cache'                => [\Illuminate\Cache\CacheManager::class, \Illuminate\Contracts\Cache\Factory::class],
                     'cache.store'          => [\Illuminate\Cache\Repository::class, \Illuminate\Contracts\Cache\Repository::class],
                     'config'               => [\Illuminate\Config\Repository::class, \Illuminate\Contracts\Config\Repository::class],
                     'cookie'               => [\Illuminate\Cookie\CookieJar::class, \Illuminate\Contracts\Cookie\Factory::class, \Illuminate\Contracts\Cookie\QueueingFactory::class],
                     'encrypter'            => [\Illuminate\Encryption\Encrypter::class, \Illuminate\Contracts\Encryption\Encrypter::class],
                     'db'                   => [\Illuminate\Database\DatabaseManager::class],
                     'db.connection'        => [\Illuminate\Database\Connection::class, \Illuminate\Database\ConnectionInterface::class],
                     'events'               => [\Illuminate\Events\Dispatcher::class, \Illuminate\Contracts\Events\Dispatcher::class],
                     'files'                => [\Illuminate\Filesystem\Filesystem::class],
                     'filesystem'           => [\Illuminate\Filesystem\FilesystemManager::class, \Illuminate\Contracts\Filesystem\Factory::class],
                     'filesystem.disk'      => [\Illuminate\Contracts\Filesystem\Filesystem::class],
                     'filesystem.cloud'     => [\Illuminate\Contracts\Filesystem\Cloud::class],
                     'hash'                 => [\Illuminate\Contracts\Hashing\Hasher::class],
                     'translator'           => [\Illuminate\Translation\Translator::class, \Illuminate\Contracts\Translation\Translator::class],
                     'log'                  => [\Illuminate\Log\Writer::class, \Illuminate\Contracts\Logging\Log::class, \Psr\Log\LoggerInterface::class],
                     'mailer'               => [\Illuminate\Mail\Mailer::class, \Illuminate\Contracts\Mail\Mailer::class, \Illuminate\Contracts\Mail\MailQueue::class],
                     'auth.password'        => [\Illuminate\Auth\Passwords\PasswordBrokerManager::class, \Illuminate\Contracts\Auth\PasswordBrokerFactory::class],
                     'auth.password.broker' => [\Illuminate\Auth\Passwords\PasswordBroker::class, \Illuminate\Contracts\Auth\PasswordBroker::class],
                     'queue'                => [\Illuminate\Queue\QueueManager::class, \Illuminate\Contracts\Queue\Factory::class, \Illuminate\Contracts\Queue\Monitor::class],
                     'queue.connection'     => [\Illuminate\Contracts\Queue\Queue::class],
                     'queue.failer'         => [\Illuminate\Queue\Failed\FailedJobProviderInterface::class],
                     'redirect'             => [\Illuminate\Routing\Redirector::class],
                     'redis'                => [\Illuminate\Redis\RedisManager::class, \Illuminate\Contracts\Redis\Factory::class],
                     'request'              => [\Illuminate\Http\Request::class, \Symfony\Component\HttpFoundation\Request::class],
                     'router'               => [\Illuminate\Routing\Router::class, \Illuminate\Contracts\Routing\Registrar::class, \Illuminate\Contracts\Routing\BindingRegistrar::class],
                     'session'              => [\Illuminate\Session\SessionManager::class],
                     'session.store'        => [\Illuminate\Session\Store::class, \Illuminate\Contracts\Session\Session::class],
                     'url'                  => [\Illuminate\Routing\UrlGenerator::class, \Illuminate\Contracts\Routing\UrlGenerator::class],
                     'validator'            => [\Illuminate\Validation\Factory::class, \Illuminate\Contracts\Validation\Factory::class],
                     'view'                 => [\Illuminate\View\Factory::class, \Illuminate\Contracts\View\Factory::class],
                 ] as $key => $aliases) {
            foreach ($aliases as $alias) {
                $this->alias($key, $alias);
            }
        }
    }

该 ``redis`` 别名对应容器中的 ``\Illuminate\Redis\RedisManager::class`` 类型。

文件 ``\Illuminate\Support\Facades\Facade.php``

.. code-block:: php

    <?php
    protected static function resolveFacadeInstance($name)
    {
        if (is_object($name)) {
            return $name;
        }

        if (isset(static::$resolvedInstance[$name])) {  // 如果该名称对象已经解析，则返回以前的
            return static::$resolvedInstance[$name];
        }

        return static::$resolvedInstance[$name] = static::$app[$name]; // 否则利用容器获取，然后保存
    }

上面的 ``Redis`` 类继承 ``Facade`` 类。

但此时，在容器中不存在该实例对象，则需要查询延迟提供器中是否存在该实例对象，这里的 ``\Illuminate\Redis\RedisServiceProvider.php`` 延迟服务提供器提供了 "redis" 初始化工厂。

文件 ``\Illuminate\Redis\RedisServiceProvider.php``

.. code-block:: php

    <?php
    public function register()
    {
        $this->app->singleton('redis', function ($app) { // 绑定创建RedisManager实例闭包
            $config = $app->make('config')->get('database.redis');

            return new RedisManager(Arr::pull($config, 'client', 'predis'), $config);
        });

        $this->app->bind('redis.connection', function ($app) { // 绑定返回Redis默认连接的实例闭包
            return $app['redis']->connection();
        });
    }

通过调用上面注册的闭包工厂，生成 ``RedisManager`` 实例对象，这里传入了在 ``database.php`` 配置文件中的 ``redis`` 配置来初始化该实例。

当我们调用 ``Redis::Set()`` 方法时，会调用底层的 ``RedisManager`` 实例对象的方法，因为该对象不存在该方法，只能调用魔术方法 ``__call()`` 。

文件 ``\Illuminate\Support\Facades\Facade.php``

.. code-block:: php

    <?php
    public static function __callStatic($method, $args)
    {
        $instance = static::getFacadeRoot(); // 获取容器中的RedisManager对象实例

        if (! $instance) {
            throw new RuntimeException('A facade root has not been set.');
        }

        return $instance->$method(...$args); // 这里调用的是RedisManager对象实例方法
    }

由于 ``RedisManager`` 对象实例没有 ``set()`` 方法，只能调用魔术方法 ``__call()`` 。

文件 ``\Illuminate\Redis\RedisManager.php``

.. code-block:: php

    <?php
    public function __call($method, $parameters)
    {
        return $this->connection()->{$method}(...$parameters); // 只会调用默认的连接实例方法
    }

    //通过一个客户端连接实例来连接后端redis，connections中可存在多个redis连接实例(即连接多个后端redis服务器)，
    //这里可通过名称(该名称为配置中定义的名称)来获取对应的连接实例
    public function connection($name = null)
    {
        $name = $name ?: 'default';

        if (isset($this->connections[$name])) {
            return $this->connections[$name]; // 已经存在，则直接返回连接实例对象
        }
        // 如果不存在，则需要配置中来初始化该连接实例
        return $this->connections[$name] = $this->resolve($name);
    }

    // 根据配置来创建连接实例对象
    public function resolve($name = null)
    {
        $name = $name ?: 'default';

        $options = $this->config['options'] ?? []; // 获取配置选项

        if (isset($this->config[$name])) { // 通过不同的连接器来初始化指定的连接实例
            return $this->connector()->connect($this->config[$name], $options);
        }

        if (isset($this->config['clusters'][$name])) {
            return $this->resolveCluster($name);
        }

        throw new InvalidArgumentException(
            "Redis connection [{$name}] not configured."
        );
    }

    // 使用指定的客户端连接器来建立连接
    protected function connector()
    {
        switch ($this->driver) {
            case 'predis':
                return new Connectors\PredisConnector; // 此时的connections属性值为PredisConnection对象实例
            case 'phpredis':
                return new Connectors\PhpRedisConnector;
        }
    }

文件 ``\Illuminate\Redis\Connectors\PredisConnector``

.. code-block:: php

    <?php
    // 建立连接，返回连接实例对象
    public function connect(array $config, array $options)
    {
        $formattedOptions = array_merge(
            ['timeout' => 10.0], $options, Arr::pull($config, 'options', [])
        );
        // 使用连接Redis服务器的\Predis\Client实例来初始化Predis连接
        return new PredisConnection(new Client($config, $formattedOptions));
    }

当连接初始化完成之后，就是调用它的 ``set()`` 方法来访问 ``Redis`` 服务器。由于 ``\Illuminate\Redis\Connections\PredisConnection`` 继承了 ``\Illuminate\Redis\Connections\Connection`` 类所以，调用了Connection类的魔术方法 ``__call()`` 。

文件 ``Illuminate\Redis\Connections\Connection``

.. code-block:: php

    <?php
    public function __call($method, $parameters)
    {
        return $this->command($method, $parameters);
    }

    public function command($method, array $parameters = [])
    {
        // 直接调用Client对象的对应方法来执行
        return $this->client->{$method}(...$parameters);
    }

至此为止， ``Redis`` 访问流程完成。该过程主要是实例化 ``RedisManager`` 对象，同时延迟到访问 ``Redis`` 服务器的时候，才建立连接实例对象来访问 ``Redis`` 服务器。





