=====
Redis
=====

简介
====
``Redis`` 是一个开源的，高级键值对存储数据库。由于它包含 字符串、哈希、列表、集合 和 有序集合 这些数据类型，所以它通常被称为数据结构服务器。

在使用 Laravel 的 ``Redis`` 之前，你需要通过 ``Composer`` 安装 ``predis/predis`` 扩展包：

.. code-block:: shell

    composer require predis/predis

或者，你可以通过 ``PECL`` 安装 `PhpRedis <https://github.com/phpredis/phpredis>`_ PHP 扩展。这个扩展安装起来比较复杂，但对于大量使用 ``Redis`` 的应用程序来说可能会产生更好的性能。


配置
====
Laravel 应用的 ``Redis`` 配置都在配置文件 ``config/database.php`` 中。在这个文件里，你可以看到 ``redis`` 数组里面包含了应用程序使用的 ``Redis`` 服务器：

.. code-block:: php

    <?php
    'redis' => [

        'client' => 'predis',

        'default' => [
            'host' => env('REDIS_HOST', 'localhost'),
            'password' => env('REDIS_PASSWORD', null),
            'port' => env('REDIS_PORT', 6379),
            'database' => 0,
        ],

    ],

默认的服务器配置应该足以进行开发。当然，你也可以根据使用的环境来随意更改这个数组。只需在配置文件中给每个 ``Redis`` 服务器指定名称、 ``host`` 和 ``port`` 即可。

集群配置
--------
如果你的程序使用 ``redis`` 服务器集群，你应该在 ``redis`` 配置文件中使用 ``clusters`` 键来定义这些集群：

.. code-block:: php

    <?php
    'redis' => [

        'client' => 'predis',

        'clusters' => [
            'default' => [
                [
                    'host' => env('REDIS_HOST', 'localhost'),
                    'password' => env('REDIS_PASSWORD', null),
                    'port' => env('REDIS_PORT', 6379),
                    'database' => 0,
                ],
            ],
        ],

    ],

默认情况下，集群可以实现跨节点间客户端共享，允许你实现节点池以及创建大量可用内存。这里要注意，客户端共享不会处理失败的情况；因此，这个功能主要适用于从另一个主数据库获取的缓存数据。如果要使用 ``redis`` 原生集群，要在配置文件的 ``options`` 键中如下指定：

.. code-block:: php

    <?php
    'redis' => [

        'client' => 'predis',

        'options' => [
            'cluster' => 'redis',
        ],

        'clusters' => [
            // ...
        ],

    ],

Predis
======
除了默认的 ``Host`` 、 ``port`` 、 ``database`` 和 ``password`` 这些服务配置选项之外， ``Predis`` 还支持为每个 ``redis`` 服务器定义其它的 `连接参数 <https://github.com/nrk/predis/wiki/Connection-Parameters>`_ 。如果要使用这些额外的配置选项，就将它们添加到配置文件 ``config/database.php`` 的 ``Redis`` 服务器配置中：

.. code-block:: php

    <?php
    'default' => [
        'host' => env('REDIS_HOST', 'localhost'),
        'password' => env('REDIS_PASSWORD', null),
        'port' => env('REDIS_PORT', 6379),
        'database' => 0,
        'read_write_timeout' => 60,
    ],

PhpRedis
========
.. note:: 如果你是通过 PECL 安装 Redis PHP 扩展，就需要重命名 ``config/app.php`` 文件里 ``Redis`` 的别名。

如果要使用 ``Phpredis`` 扩展，就需要将配置文件 ``config/database.php`` 中 ``Redis`` 配置的 ``client`` 选项更改为 ``phpredis`` ：

.. code-block:: php

    <?php
    'redis' => [

        'client' => 'phpredis',

        // 其余的 Redis 配置...
    ],

除了默认的 ``Host`` 、 ``port`` 、 ``database`` 和 ``password`` 这些服务配置项之外， ``Phpredis`` 还支持以下几个额外的连接参数： ``persistent`` 、 ``prefix`` 、 ``read_timeout`` 和 ``timeout`` 。你可以将这些选项加到配置文件 ``config/database.php`` 中 ``redis`` 服务器配置项下：

.. code-block:: php

    <?php
    'default' => [
        'host' => env('REDIS_HOST', 'localhost'),
        'password' => env('REDIS_PASSWORD', null),
        'port' => env('REDIS_PORT', 6379),
        'database' => 0,
        'read_timeout' => 60,
    ],

Redis 交互
==========
你可以调用 ``Redis facade`` 上的各种方法来与 ``Redis`` 进行交互。 ``Redis facade`` 支持动态方法，这意味着你可以在 ``facade`` 上调用任何 ``Redis`` 命令 ，还能将该命令直接传递给 ``Redis`` 。在本例中，通过调用 ``Redis facade`` 上的 ``get`` 方法来调用 ``Redis`` 的 ``GET`` 命令：

.. code-block:: php

    <?php
    namespace App\Http\Controllers;

    use App\Http\Controllers\Controller;
    use Illuminate\Support\Facades\Redis;

    class UserController extends Controller
    {
        /**
         * 显示给定用户的配置文件。
         *
         * @param  int  $id
         * @return Response
         */
        public function showProfile($id)
        {
            $user = Redis::get('user:profile:'.$id); // 获取默认连接的字段值

            return view('user.profile', ['user' => $user]);
        }
    }

也就是说，你可以在 ``Redis facade`` 上调用任何的 ``Redis`` 命令。 Laravel 使用魔术方法将传递命令给 ``Redis`` 服务器，因此只需传递 ``Redis`` 命令所需的参数即可：

.. code-block:: php

    <?php
    Redis::set('name', 'Taylor');

    $values = Redis::lrange('names', 5, 10);

或者，你也可以使用 ``command`` 方法将命令传递给服务器，它接受命令的名称作为其第一个参数，并将值的数组作为其第二个参数：

.. code-block:: php

    <?php
    $values = Redis::command('lrange', ['name', 5, 10]);

使用多个 Redis 连接
--------------------
你可以通过 ``Redis::connection`` 方法来获取 ``Redis`` 实例：

.. code-block:: php

    <?php
    $redis = Redis::connection();

这会返回一个默认的 ``redis`` 服务器的实例。前面所有示例都是使用 ``Redis`` 默认连接，你也可以将连接或者集群的名称传递给 ``connection`` 方法，来获取在 ``Redis`` 配置文件中定义的特定的服务器或者集群：

.. code-block:: php

    <?php
    $redis = Redis::connection('my-connection');

管道命令
--------
如果你需要在一个操作中向服务器发送很多命令，推荐你使用管道命令。 ``pipeline`` 方法接收一个带有 ``Redis`` 实例的 闭包 。你可以将所有的命令发送给这个 ``Redis`` 实例，它们都会一次过执行完：

.. code-block:: php

    <?php
    Redis::pipeline(function ($pipe) {
        for ($i = 0; $i < 1000; $i++) {
            $pipe->set("key:$i", $i);
        }
    });

发布与订阅
==========
Laravel 为 ``Redis`` 的 ``publish`` 及 ``subscribe`` 提供了方便的接口。这些 ``Redis`` 命令让你可以监听指定「频道」上的消息。你可以从另一个应用程序发布消息给另一个应用程序，甚至使用其它编程语言，让应用程序和进程之间能够轻松进行通信。

首先，我们使用 ``subscribe`` 方法设置频道监听器。我们将这个方法调用放在 ``Artisan`` 命令 中，因为调用 ``subscribe`` 方法会启动一个长时间运行的进程：

.. code-block:: php

    <?php
    namespace App\Console\Commands;

    use Illuminate\Console\Command;
    use Illuminate\Support\Facades\Redis;

    class RedisSubscribe extends Command
    {
        /**
         * 控制台命令的名称和签名。
         *
         * @var string
         */
        protected $signature = 'redis:subscribe';

        /**
         * 控制台命令说明。
         *
         * @var string
         */
        protected $description = 'Subscribe to a Redis channel';

        /**
         * 执行控制台命令。
         *
         * @return mixed
         */
        public function handle()
        {
            Redis::subscribe(['test-channel'], function ($message) {
                echo $message;
            });
        }
    }

现在我们可以使用 ``publish`` 方法将消息发布到频道：

.. code-block:: php

    <?php
    Route::get('publish', function () {
        // Route logic...

        Redis::publish('test-channel', json_encode(['foo' => 'bar']));
    });

通配符订阅
----------
使用 ``psubscribe`` 方法可以订阅通配符频道，可以用来在所有频道上获取所有消息。 ``$channel`` 名称将作为第二个参数传递给提供的回调 闭包 ：

.. code-block:: php

    <?php
    Redis::psubscribe(['*'], function ($message, $channel) {
        echo $message;
    });

    Redis::psubscribe(['users.*'], function ($message, $channel) {
        echo $message;
    });




