=====
日志
=====

简介
====
为了帮助你了解你的应用的运行状况，Laravel 提供了强大的日志记录服务，以便你将信息、系统错误记录到文件中，甚至还可以用 ``Slack`` 通知你的团队。

Laravel 的日志系统基于 ``Monolog`` 库， ``Monolog`` 提供了多种强力的日志处理程序。在 Laravel 中可以简单地配置这些处理程序，你可以通过混合使用配置项来自定义应用日志处理程序。

配置
====
所有日志系统相关的配置都在 ``config/logging.php`` 文件中。你可以在这个文件中配置应用日志记录通道，通过查看文件可以了解每个可用通道及其选项。接下来我们会查看一些常见的配置。

.. code-block:: php

    <?php
    use Monolog\Handler\StreamHandler;
    return [
        /*
        |--------------------------------------------------------------------------
        | Default Log Channel
        |--------------------------------------------------------------------------
        |
        | This option defines the default log channel that gets used when writing
        | messages to the logs. The name specified in this option should match
        | one of the channels defined in the "channels" configuration array.
        |
        */
        'default' => env('LOG_CHANNEL', 'stack'),
        /*
        |--------------------------------------------------------------------------
        | Log Channels
        |--------------------------------------------------------------------------
        |
        | Here you may configure the log channels for your application. Out of
        | the box, Laravel uses the Monolog PHP logging library. This gives
        | you a variety of powerful log handlers / formatters to utilize.
        |
        | Available Drivers: "single", "daily", "slack", "syslog",
        |                    "errorlog", "monolog",
        |                    "custom", "stack"
        |
        */
        'channels' => [
            'stack' => [
                'driver' => 'stack',
                'channels' => ['single'],
            ],
            'single' => [
                'driver' => 'single',
                'path' => storage_path('logs/laravel.log'),
                'level' => 'debug',
            ],
            'daily' => [
                'driver' => 'daily',
                'path' => storage_path('logs/laravel.log'),
                'level' => 'debug',
                'days' => 7,
            ],
            'slack' => [
                'driver' => 'slack',
                'url' => env('LOG_SLACK_WEBHOOK_URL'),
                'username' => 'Laravel Log',
                'emoji' => ':boom:',
                'level' => 'critical',
            ],
            'stderr' => [
                'driver' => 'monolog',
                'handler' => StreamHandler::class,
                'with' => [
                    'stream' => 'php://stderr',
                ],
            ],
            'syslog' => [
                'driver' => 'syslog',
                'level' => 'debug',
            ],
            'errorlog' => [
                'driver' => 'errorlog',
                'level' => 'debug',
            ],
        ],
    ];

默认情况下 Laravel 使用 ``stack`` 通道记录信息。 ``stack`` 通道会将多个日志通道集合到单个通道中，到 下面的文档 了解更多有关构建栈的信息。

但是 laravel 5.5 中该日志配置文件已经移除，配置日志信息只需要到 config/app.php 中进行配置即可。

.. code-block:: php

    <?php
    /*
    |--------------------------------------------------------------------------
    | Logging Configuration
    |--------------------------------------------------------------------------
    |
    | Here you may configure the log settings for your application. Out of
    | the box, Laravel uses the Monolog PHP logging library. This gives
    | you a variety of powerful log handlers / formatters to utilize.
    |
    | Available Settings: "single", "daily", "syslog", "errorlog"
    |
    */

    'log' => env('APP_LOG', 'single'),

    'log_level' => env('APP_LOG_LEVEL', 'debug'),



配置通道名称
------------
``Monolog`` 在默认情况下会以当前的环境作为「通道名称」来实例化，例如 ``production`` 或者 ``local`` 。可以给你的通道配置添加一个 ``name`` 选项来更改这个值：

.. code-block:: php

    <?php
    'stack' => [
        'driver' => 'stack',
        'name' => 'channel-name',
        'channels' => ['single', 'slack'],
    ],

配置 Slack 通道
---------------
``slack`` 通道必须要有 ``url`` 配置项。这个 ``URL`` 要和你在 ``Slack`` 上配置的 ``incoming webhook`` 保持一致。

构建日志栈
----------
如之前所提， ``stack`` 驱动可以把多个通道整合到单个通道中。我们可以通过来看一个上线应用示例配置，来展示如何使用日志栈：

.. code-block:: php

    <?php
    'channels' => [
        'stack' => [
            'driver' => 'stack',
            'channels' => ['syslog', 'slack'],
        ],

        'syslog' => [
            'driver' => 'syslog',
            'level' => 'debug',
        ],

        'slack' => [
            'driver' => 'slack',
            'url' => env('LOG_SLACK_WEBHOOK_URL'),
            'username' => 'Laravel Log',
            'emoji' => ':boom:',
            'level' => 'critical',
        ],
    ],

我们来分析一下这份配置。首先，我们注意到 ``stack`` 通道整合了另外两个通道，他们的 ``channels`` 选项分别是： ``syslog`` 和 ``slack`` 。那么在记录信息时，这两个通道都会记录到该信息。

日志等级
--------
注意到 ``syslog`` 和 ``slack`` 通道都有 ``level`` 这个配置项。该选项用来判断可以被记录的信息的最低 「等级」。Laravel 日志服务的基础库 ``Monolog`` 提供了所有在 RFC 5424 specification 中定义的日志等级： ``emergency`` 、  ``alert`` 、 ``critical`` 、 ``error`` 、 ``warning`` 、 ``notice`` 、 ``info`` 和 ``debug`` 。

我们想象一下用 ``debug`` 方法来记录一条信息：

.. code-block:: php

    <?php
    Log::debug('An informational message.');

那么鉴于我们的配置， ``syslog`` 通道会将这条信息记录到系统日志上；然而在 ``critical`` 等级之下的错误信息并不会发送到 ``Slack`` ；不过如果我们记录了一条 ``emergency`` 等级的信息，它将会被同时发送到 ``syslog`` 通道和 ``slack`` 通道，因为 ``emergency`` 等级高于这两个通道的最低日志等级：

.. code-block:: php

    <?php
    Log::emergency('The system is down!');

记录日志信息
============
你可以用 Log facade 记录信息到日志。就如前面提到的，日志处理程序提供了八种在 RFC 5424 specification 里定义的日志等级： ``emergency`` 、 ``alert`` 、 ``critical`` 、 ``error`` 、 ``warning`` 、 ``notice`` 、 ``info`` 和 ``debug`` 。

.. code-block:: php

    <?php
    Log::emergency($message);
    Log::alert($message);
    Log::critical($message);
    Log::error($message);
    Log::warning($message);
    Log::notice($message);
    Log::info($message);
    Log::debug($message);

所以，你可以调用这些方法来记录相应等级的信息。信息会被写入 ``config/logging.php`` 文件中配置的默认的日志通道：

.. code-block:: php

    <?php

    namespace App\Http\Controllers;

    use App\User;
    use Illuminate\Support\Facades\Log;
    use App\Http\Controllers\Controller;

    class UserController extends Controller
    {
        /**
         * Show the profile for the given user.
         *
         * @param  int  $id
         * @return Response
         */
        public function showProfile($id)
        {
            Log::info('Showing user profile for user: '.$id);

            return view('user.profile', ['user' => User::findOrFail($id)]);
        }
    }

上下文信息
----------
上下文数据也可以用数组的形式传递给日志方法。此上下文数据将被格式化并与日志消息一起显示：

.. code-block:: php

    <?php
    Log::info('User failed to login.', ['id' => $user->id]); // [2018-08-09 10:58:34] local.INFO: User failed to login. {"id":"1"}

记录日志到指定通道(5.5不支持)
-----------------------------
有时候你可能希望将日志记录到非默认通道。你可以使用 Log Facade 中的 ``channel`` 方法，将日志记录到应用配置中存在的任何渠道：

.. code-block:: php

    <?php
    Log::channel('slack')->info('Something happened!');

如果你想按需创建多个渠道的日志堆栈，你可以使用 ``stack`` 方法：

.. code-block:: php

    <?php
    Log::stack(['single', 'slack'])->info('Something happened!');

自定义 Monolog 日志通道(5.5不支持)
==================================
有时你可能需要完全配置 ``Monolog`` 现有的通道。例如：你想要为现有通道自定义一个 ``Monolog FormatterInterface`` 实现。

首先，在频道配置文件中定义一个 ``tap`` 数组。 ``tap`` 数组应该包含所需的类列表，这些类就是 ``Monolog`` 实例创建后需要自定义（或开发）的类：

.. code-block:: php

    <?php
    'single' => [
        'driver' => 'single',
        'tap' => [App\Logging\CustomizeFormatter::class],
        'path' => storage_path('logs/laravel.log'),
        'level' => 'debug',
    ],

当你完成了通道中 ``tap`` 选项的配置，你就可以开始写 ``Monolog`` 实例自定义类了。这个类只需要一个方法： ``__invoke`` ，这个方法可以接收一个 ``Illuminate\Log\Logger`` 实例。 ``Illuminate\Log\Logger`` 实例代理了所有 ``Monolog`` 实例底层方法调用：

.. code-block:: php

    <?php

    namespace App\Logging;

    class CustomizeFormatter
    {
        /**
         * 自定义日志实例
         *
         * @param  \Illuminate\Log\Logger  $logger
         * @return void
         */
        public function __invoke($logger)
        {
            foreach ($logger->getHandlers() as $handler) {
                $handler->setFormatter(...);
            }
        }
    }

创建自定义通道(5.5不支持)
=========================
如果你想定义一个完全自定义的通道以使你可以通过 ``Monolog`` 的实例和配置控制它，你可以在你的 ``config/logging.php`` 配置文件中定义一个 ``custom`` 驱动类型。此外，你的配置文件应该包含一个 ``via`` 选项来指定用于创建 ``Monolog`` 实例的类：

.. code-block:: php

    <?php
    'channels' => [
        'custom' => [
            'driver' => 'custom',
            'via' => App\Logging\CreateCustomLogger::class,
        ],
    ],

当你配置了 ``custom`` 通道，准备好定义创建 ``Monolog`` 实例的类。这个类只需要一个方法： ``__invoke`` ，它应该返回一个 ``Monolog`` 实例。

.. code-block:: php

    <?php

    namespace App\Logging;

    use Monolog\Logger;

    class CreateCustomLogger
    {
        /**
         * 创建一个 Monolog 实例。
         *
         * @param  array  $config
         * @return \Monolog\Logger
         */
        public function __invoke(array $config)
        {
            return new Logger(...);
        }
    }

laravel 5.5 版本的改变
======================

文件 ``\Illuminate\Log\LogServiceProvider``

.. code-block:: php

    <?php
    public function register()
    {
        $this->app->singleton('log', function () {
            return $this->createLogger();
        });
    }

    public function createLogger()
    {
        $log = new Writer(
            new Monolog($this->channel()), $this->app['events']
        );
        // 是否有自定义的monolog配置器
        if ($this->app->hasMonologConfigurator()) { // 传入底层的monolog实例
            call_user_func($this->app->getMonologConfigurator(), $log->getMonolog());
        } else {
            $this->configureHandler($log); // 根据app配置文件中的参数来初始化日志处理器和处理层级
        }

        return $log;
    }

    protected function channel()
    {
        // 通过app配置文件的log_channel来获取频道配置，用来区分日志来自哪个频道
        if ($this->app->bound('config') &&
            $channel = $this->app->make('config')->get('app.log_channel')) {
            return $channel;
        }
        // 否则把当前应用的环境名称当作频道
        return $this->app->bound('env') ? $this->app->environment() : 'production';
    }

    protected function configureHandler(Writer $log)
    {
        $this->{'configure'.ucfirst($this->handler()).'Handler'}($log);
    }

    protected function configureSingleHandler(Writer $log)
    {
        $log->useFiles(
            $this->app->storagePath().'/logs/laravel.log',
            $this->logLevel()
        );
    }

上面日志服务提供器注册了日志管理类 ``Writer`` ，通过 app.php 配置文件中的日志配置来初始化日志管理类中的日志处理器和处理日志等级。

文件 ``\Illuminate\Log\Writer``

.. code-block:: php

    <?php
    public function useFiles($path, $level = 'debug')
    {
        $this->monolog->pushHandler($handler = new StreamHandler($path, $this->parseLevel($level)));

        $handler->setFormatter($this->getDefaultFormatter());
    }

    protected function getDefaultFormatter()
    {
        // 把初始化实例传入后面的函数，然后返回该实例
        return tap(new LineFormatter(null, null, true, true), function ($formatter) {
            $formatter->includeStacktraces();
        });
    }

日志事件
--------
在发送日志的时候会触发 ``\Illuminate\Log\Events\MessageLogged`` 事件。

文件 ``\Illuminate\Log\Writer``

.. code-block:: php

    <?php
    public function debug($message, array $context = [])
    {
        $this->writeLog(__FUNCTION__, $message, $context);
    }

    protected function writeLog($level, $message, $context)
    {
        // 触发事件
        $this->fireLogEvent($level, $message = $this->formatMessage($message), $context);
        // 记录日志
        $this->monolog->{$level}($message, $context);
    }

    protected function fireLogEvent($level, $message, array $context = [])
    {
        /**
         * 如果设置了事件分发器，我们将把参数传递给日志监听器。
         * 这些对于构建分析器或其他工具非常有用，这些工具可聚合给定“请求”周期的所有日志消息。
         */
        if (isset($this->dispatcher)) {
            $this->dispatcher->dispatch(new MessageLogged($level, $message, $context));
        }
    }

    // 可以通过Log::listen()方法来注册事件监听器
    public function listen(Closure $callback)
    {
        if (! isset($this->dispatcher)) {
            throw new RuntimeException('Events dispatcher has not been set.');
        }

        $this->dispatcher->listen(MessageLogged::class, $callback);
    }

