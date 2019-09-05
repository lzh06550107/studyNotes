================
广播系统原理分析
================

基础概念
========
广播系统的目的是用于实现当服务端完成某种特定功能后向客户端推送消息的功能。先让我们了解下消息推送的大致流程：

1. 首先，服务器需要支持 ``WebSocket`` 协议，并且允许客户端建立 ``WebSocket`` 连接；
2. 您可以实现自己的 ``WebSocket`` 服务,或者使用第三方服务如 ``Pusher`` ，后文会用到 ``Pusher`` 库；
3. 客户端创建一个服务器的 ``Web Socket`` 连接，连接成功后客户端会获取唯一标识符(socketID)；
4. 一旦客户端连接成功，表示该客户端订阅了指定频道，将接收这个频道的消息；
5. 最后，客户端还会注册其所订阅的频道的监听事件；
6. 当服务端完成指定功能后，我们以指定频道名称和事件名称的信息通知到 ``WebSocket`` 服务器；
7. 最终， ``WebSocket`` 服务器将这个指定事件以广播的形式推送到所有注册这个频道监听的客户端。

我们先用一个电子商务网站作为例子来概览一下事件广播。当用户在查看自己的订单时，我们不希望他们必须通过刷新页面才能看到状态更新。我们希望一旦有更新时就主动将更新信息广播到客户端。

laravel 的广播系统和队列系统类似，需要两个进程协作，一个是 laravel 的 ``web`` 后台系统，另一个是使用 ``Socket.IO`` ( ``Laravel Echo Server`` 使用 ``Socket.IO`` 来监听websocket协议连接)服务器系统。具体的流程是页面加载时，网页 ``js`` 程序 ``Laravel Echo`` 与 ``Laravel Echo Server`` 服务器建立连接， laravel 发起通过驱动发布广播， ``Laravel Echo Server`` 服务器接受广播内容，对连接的客户端网页推送信息，以达到网页实时更新的目的。

laravel 发起广播的方式有两种， ``redis`` 与 ``pusher`` 。对于 ``redis`` 来说，需要支持 ``Socket.IO`` 服务器系统，官方推荐 ``nodejs`` 为底层的 `tlaverdure/laravel-echo-server <https://github.com/tlaverdure/laravel-echo-server>`_ 。对于 ``pusher`` 来说，该第三方服务包含了驱动与 ``Socket.IO`` 服务器。

广播架构
========

- ``laravel-echo-server`` ：使用 ``socket.io`` 机制实现的 ``broadcasting`` 服务端；
- ``laravel-echo`` ： ``laravel-echo`` 是 ``laravel broadcasting`` 的客户端。注意， ``laravel-echo`` 并不是 ``laravel-echo-server`` 专属的客户端， ``laravel-echo`` 有两种连接机制可以选： ``pusher`` 和 ``socket.io`` 。 而 ``laravel-echo-server`` 是开发出来专门用于 ``socket.io`` 连接的服务端。如果你使用的是 ``pusher`` ，那么不需要使用 ``laravel-echo-server`` ，但是你依然要使用 ``laravel-echo`` ；
- ``Socket.IO``： ``websocket`` 协议的一种 ``nodejs`` 实现。 ``laravel-echo`` 如果要使用 ``socket.io`` 则需要先安装 ``socket.io-client`` 。
- ``Predis`` ： ``redis`` 客户端的 php 实现，如果要使用 ``redis`` 作为广播机制的实现，则需要先安装 ``predis`` ；
- ``Laravel Event`` ：广播事件类；
- ``Laravel Queue`` ：广播机制是基于 ``queue`` 机制来实现的；
- ``Redis Sub/Pub`` ： ``Redis`` 的订阅机制。 ``laravel-echo-server`` 本质上只是一个 ``Redis`` 订阅服务的订阅者。

.. image:: ./images/broadcast.png

根据这幅图我们可以知道事件的广播机制流程：

- ``Laravel`` 通过 ``broadcasting`` 机制发布一个 ``Event`` 对象到 ``Redis`` ；
- ``Laravel Queue Worker`` 读取该 ``Event`` 对象，并使用 ``Redis`` 的 ``Sub/Pub`` 机制将该 ``Event`` 对象在对应的频道发布出去；
- ``laravel-echo`` 通过调用 ``channel()`` 、 ``join()`` 或 ``private()`` 方法来调用订阅对应的公开、私有和存在频道，通过调用公开和私有频道的 ``listen()`` 方法在 ``socket.io`` 上监听对应事件的发生；这里的私有和存在频道需要通过 ``laravel-echo-server`` 访问 ``web`` 应用后台进行身份验证；
- ``laravel-echo-server``  通过 ``Redis`` 的 ``Sub/Pub`` 机制收听到该所有频道的 ``Event`` ；
- 由于 ``laravel-echo`` 使用 ``socket.io`` 跟 ``laravel-echo-server`` 相连接。所以 ``laravel-echo-server`` 会通过 ``socket.io`` 将对应频道的 ``Event`` 对象发送给 ``laravel-echo`` ；
- ``laravel-echo`` 解析通过 ``socket.io`` 接收到的 ``Event`` 对象；


laravel端广播事件分析
=====================
广播系统服务的启动
------------------
和其他服务类似，广播系统服务的注册实质上就是对 Ioc 容器注册门面类，广播系统的门面类是 BroadcastManager:

文件 ``\Illuminate\Broadcasting\BroadcastServiceProvider``

.. code-block:: php

    <?php
    public function register()
    {
        $this->app->singleton(BroadcastManager::class, function ($app) {
            return new BroadcastManager($app);
        });

        $this->app->singleton(BroadcasterContract::class, function ($app) {
            // 返回LogBroadcaster、NullBroadcaster、PusherBroadcaster和RedisBroadCaster对象实例
            return $app->make(BroadcastManager::class)->connection();
        });
        // 建立别名映射
        $this->app->alias(
            BroadcastManager::class, BroadcastingFactory::class
        );
    }

除了注册 ``BroadcastManager`` ， ``BroadcastServiceProvider`` 还进行了广播驱动的启动：

文件 ``\Illuminate\Broadcasting\BroadcastManager``

.. code-block:: php

    <?php
    public function connection($driver = null)
    {
        return $this->driver($driver);
    }

    public function driver($name = null)
    {
        $name = $name ?: $this->getDefaultDriver();

        return $this->drivers[$name] = $this->get($name);
    }

    protected function get($name)
    {
        return isset($this->drivers[$name]) ? $this->drivers[$name] : $this->resolve($name);
    }

    protected function resolve($name)
    {
        $config = $this->getConfig($name);

        if (is_null($config)) {
            throw new InvalidArgumentException("Broadcaster [{$name}] is not defined.");
        }

        if (isset($this->customCreators[$config['driver']])) {
            return $this->callCustomCreator($config);
        }

        $driverMethod = 'create'.ucfirst($config['driver']).'Driver';

        if (! method_exists($this, $driverMethod)) {
            throw new InvalidArgumentException("Driver [{$config['driver']}] is not supported.");
        }

        return $this->{$driverMethod}($config);
    }

    protected function createRedisDriver(array $config)
    {
        return new RedisBroadcaster(
            $this->app->make('redis'), Arr::get($config, 'connection')
        );
    }

广播信息的发布
--------------

广播信息的发布与事件的发布大致相同，要告知 Laravel 一个给定的事件是广播类型，只需在事件类中实现 ``Illuminate\Contracts\Broadcasting\ShouldBroadcast`` 接口即可。该接口已经被导入到所有由框架生成的事件类中，所以可以很方便地将它添加到自己的事件中。

``ShouldBroadcast`` 接口要求你实现一个方法： ``broadcastOn`` 。 ``broadcastOn`` 方法返回一个频道或一个频道数组，事件会被广播到这些频道。频道必须是 ``Channel`` 、 ``PrivateChannel`` 或 ``PresenceChannel`` 的实例。 ``Channel`` 实例表示任何用户都可以订阅的公开频道，而 ``PrivateChannels`` 和 ``PresenceChannels`` 则表示需要 频道授权 的私有频道：

.. code-block:: php

    <?php
    class ServerCreated implements ShouldBroadcast
    {
        use SerializesModels;

        public $user;

        //默认情况下，每一个广播事件都被添加到默认的队列上，默认的队列连接在 queue.php 配置文件中指定。可以通过在事件类中定义一个 broadcastQueue 属性来自定义广播器使用的队列。该属性用于指定广播使用的队列名称：
        public $broadcastQueue = 'your-queue-name';

        public function __construct(User $user)
        {
            $this->user = $user;
        }
        // 指定事件发送的频道
        public function broadcastOn()
        {
            return new PrivateChannel('user.'.$this->user->id);
        }

        //Laravel 默认会使用事件的类名作为广播事件名称来广播事件，可以自定义事件：
        public function broadcastAs()
        {
            return 'server.created';
        }

        //想更细粒度地控制广播数据:
        public function broadcastWith()
        {
            return ['id' => $this->user->id];
        }

        //有时，想在给定条件为 true ，才广播事件:
        public function broadcastWhen()
        {
            return $this->value > 100;
        }
    }

然后，只需要像平时那样触发事件。一旦事件被触发，一个队列任务会自动广播事件到你指定的广播驱动器上。

当一个事件被广播时，它所有的 ``public`` 属性会自动被序列化为广播数据，这允许你在你的 JavaScript 应用中访问事件的公有数据。因此，举个例子，如果你的事件有一个公有的 ``$user`` 属性，它包含了一个 ``Elouqent`` 模型，那么事件的广播数据会是：

.. code-block:: json

    {
        "user": {
            "id": 1,
            "name": "Patrick Stewart"
            ...
        }
    }

广播发布的源码
--------------
广播的发布与事件的触发是一体的，具体的流程我们已经在 ``event`` 的源码中介绍清楚了，现在我们来看唯一的不同：

文件 ``\Illuminate\Events\Dispatcher``

.. code-block:: php

    <?php
    public function dispatch($event, $payload = [], $halt = false)
    {
        list($event, $payload) = $this->parseEventAndPayload(
            $event, $payload
        );

        if ($this->shouldBroadcast($payload)) {
            $this->broadcastEvent($payload[0]);
        }

        ...
    }

    protected function shouldBroadcast(array $payload)
    {
        return isset($payload[0]) && $payload[0] instanceof ShouldBroadcast;
    }

    protected function broadcastEvent($event)
    {
        $this->container->make(BroadcastFactory::class)->queue($event);
    }

从上面代码可知 laravel 端通过调用 ``broadcast()`` 方法将事件发送到 ``Redis`` 服务器队列中。这里的 ``broadcast`` 方法实际上调用的就是 ``BroadcastManager`` 对象中的方法:

文件 ``\Illuminate\Broadcasting\BroadcastManager``

.. code-block:: php

    <?php
    public function queue($event)
    {
        // 要使用 sync 队列而不是默认队列驱动程序广播你的事件
        $connection = $event instanceof ShouldBroadcastNow ? 'sync' : null;
        // 这里应该是通过属性定义队列连接驱动
        if (is_null($connection) && isset($event->connection)) {
            $connection = $event->connection;
        }

        $queue = null;
        // 通过broadcastQueue()自定义广播器使用的队列
        if (method_exists($event, 'broadcastQueue')) {
            $queue = $event->broadcastQueue();
        } elseif (isset($event->broadcastQueue)) { // 通过broadcastQueue属性自定义广播器使用的队列
            $queue = $event->broadcastQueue;
        } elseif (isset($event->queue)) { // 通过queue属性自定义广播器使用的队列
            $queue = $event->queue;
        }
        // 这里如果$connection为null则使用默认队列驱动，推送事件到指定的队列中
        // 从这里可知，需要队列配置
        $this->app->make('queue')->connection($connection)->pushOn(
            $queue, new BroadcastEvent(clone $event)
        );
    }

可见， ``quene`` 方法将广播事件包装为事件类，并且通过队列发布，我们接下来看这个事件类的处理：

文件 ``\Illuminate\Broadcasting\BroadcastEvent``

.. code-block:: php

    <?php
    public function handle(Broadcaster $broadcaster) // 由该方法可以，需要消费Worker进程执行该函数
    {
        // 确定广播名称
        $name = method_exists($this->event, 'broadcastAs')
            ? $this->event->broadcastAs() : get_class($this->event);

        $broadcaster->broadcast(
            Arr::wrap($this->event->broadcastOn()), $name, // 指定事件被广播到哪些频道
            $this->getPayloadFromEvent($this->event)
        );
    }

    protected function getPayloadFromEvent($event)
    {
        // 这个方法应该返回一个数组，该数组中的数据会被添加到广播数据中
        if (method_exists($event, 'broadcastWith')) {
            return array_merge(
                $event->broadcastWith(), ['socket' => data_get($event, 'socket')] // 这个socket属性？？？
            );
        }

        // 如果没有指定broadcastWith方法则收集所有public属性值
        $payload = [];

        foreach ((new ReflectionClass($event))->getProperties(ReflectionProperty::IS_PUBLIC) as $property) {
            $payload[$property->getName()] = $this->formatProperty($property->getValue($event));
        }

        unset($payload['broadcastQueue']);

        return $payload;
    }

    protected function formatProperty($value)
    {
        if ($value instanceof Arrayable) {
            return $value->toArray();
        }

        return $value;
    }

可见该事件主要调用 ``broadcaster`` 的 ``broadcast`` 方法，我们这里讲 ``redis`` 的发布：

文件 ``\Illuminate\Broadcasting\Broadcasters\RedisBroadcaster``

.. code-block:: php

    <?php
    public function broadcast(array $channels, $event, array $payload = [])
    {
        // \Illuminate\Redis\Connections\PredisConnection实例对象
        $connection = $this->redis->connection($this->connection);

        $payload = json_encode([
            'event' => $event,
            'data' => $payload,
            'socket' => Arr::pull($payload, 'socket'), //???
        ]);
        // 此处利用Redis的发布和订阅功能
        foreach ($this->formatChannels($channels) as $channel) {
            $connection->publish($channel, $payload);
        }
    }

``broadcast`` 方法运用了 ``redis`` 的 ``publish`` 方法，对 ``redis`` 进行了频道的信息发布。

频道授权
--------
对于私有频道，用户只有被授权后才能监听。实现过程是 ``laravel-echo-server`` 向 Laravel 应用程序发起一个携带频道名称的 ``HTTP`` 请求，应用程序判断该用户是否能够监听该频道。在使用 ``Laravel Echo`` 时，上述 ``HTTP`` 请求会被 ``laravel-echo-server`` 自动发送；尽管如此，仍然需要定义适当的路由来响应这些请求。

定义授权路由
^^^^^^^^^^^^
我们可以在 Laravel 里很容易地定义路由来响应频道授权请求。

文件 ``\App\Providers\BroadcastServiceProvider``

.. code-block:: php

    <?php
    public function boot()
    {
        Broadcast::routes();

        require base_path('routes/channels.php');
    }

``Broadcast::routes`` 方法会自动把它的路由放进 ``web`` 中间件组中；另外，如果你想对一些属性自定义，可以向该方法传递一个包含路由属性的数组。

文件 ``\Illuminate\Broadcasting\BroadcastManager``

.. code-block:: php

    <?php
    public function routes(array $attributes = null)
    {
        if ($this->app->routesAreCached()) { // 如果路由被缓存，则直接返回
            return;
        }

        // 如果没有指定属性，则默认为该路由添加web中间件
        $attributes = $attributes ?: ['middleware' => ['web']];
        // 闭包形式注册该路由
        $this->app['router']->group($attributes, function ($router) {
            $router->post('/broadcasting/auth', '\\'.BroadcastController::class.'@authenticate');
        });
    }

文件 ``\Illuminate\Broadcasting\BroadcastController``

.. code-block:: php

    <?php
    public function authenticate(Request $request)
    {
        return Broadcast::auth($request);
    }

``BroadcastManager`` 中没有 ``auth`` 方法，它调用的是 ``Broadcaster`` 实现类中的 ``auth`` 方法。

文件 ``\Illuminate\Broadcasting\Broadcasters\RedisBroadcaster``

.. code-block:: php

    <?php
    public function auth($request)
    {
        if (Str::startsWith($request->channel_name, ['private-', 'presence-']) &&
            ! $request->user()) { // 如果是私有和存在频道，且没有授权，则返回异常
            throw new AccessDeniedHttpException;
        }
        // 去除前缀
        $channelName = Str::startsWith($request->channel_name, 'private-')
            ? Str::replaceFirst('private-', '', $request->channel_name)
            : Str::replaceFirst('presence-', '', $request->channel_name);

        return parent::verifyUserCanAccessChannel(
            $request, $channelName
        );
    }

    protected function verifyUserCanAccessChannel($request, $channel)
    {
        //由于频道的命名经常带有 userid 等参数，因此判断频道之前首先要把 channels 中的频道名转为通配符 *，例如 order.{userid} 转为 order.*，之后进行正则匹配。
        foreach ($this->channels as $pattern => $callback) {
            // 查找频道对应的认证器
            if (! Str::is(preg_replace('/\{(.*?)\}/', '*', $pattern), $channel)) {
                continue;
            }

            // 获取请求的所有参数，如果 userid 是 User 的主键，resolveBinding 还可以为其自动进行路由模型绑定。
            $parameters = $this->extractAuthParameters($pattern, $channel, $callback);
            // 通过调用注册频道回调验证来检测
            if ($result = $callback($request->user(), ...$parameters)) {// 如果验证通过，则
                return $this->validAuthenticationResponse($request, $result);
            }
        }

        throw new AccessDeniedHttpException;
    }

    public function validAuthenticationResponse($request, $result)
    {
        if (is_bool($result)) {
            return json_encode($result);
        }
        /**
         * 与私有频道不同的是，在给 presence 频道定义授权回调函数时，
         * 如果一个用户已经加入了该频道，那么不应该返回 true，而应该返回一个关于该用户信息的数组。
         */
        return json_encode(['channel_data' => [
            'user_id' => $request->user()->getAuthIdentifier(),
            'user_info' => $result,
        ]]);
    }

由上面的代码可知，认证被注入到 ``web`` 中间件中，所以，当调用授权回调时，用户其实已经认证了，还需进行授权回调来验证当前用户是否具备该频道的权限。这里还有一个问题就是该验证请求是 ``laravel-echo-server`` 发出的，那么如何确保用户身份认证通过呢？原理是 ``laravel-echo`` 向 ``laravel-echo-server`` 发送请求时会自带 ``cookie`` 和 ``csrfToken`` 。当 ``laravel-echo-server`` 向laravel应用发送验证请求时会在请求头部带上前面 ``cookie`` 和 ``csrfToken`` 字段。

定义授权回调
^^^^^^^^^^^^
接下来，我们需要定义真正用于处理频道授权的逻辑。这是在 ``routes/channels.php`` 文件中完成。在该文件中，你可以用 ``Broadcast::channel`` 方法来注册频道授权回调函数：

.. code-block:: php

    <?php
    Broadcast::channel('order.{orderId}', function ($user, $orderId) {
        return $user->id === Order::findOrNew($orderId)->user_id;
    });

``channel`` 方法接收两个参数：频道名称和一个回调函数，该回调通过返回 ``true`` 或 ``false`` 来表示用户是否被授权监听该频道。

所有的授权回调接收当前被认证的用户作为第一个参数，任何额外的通配符参数作为后续参数。在本例中，我们使用 ``{orderId}`` 占位符来表示频道名称的「ID」部分是通配符。

授权回调模型绑定
^^^^^^^^^^^^^^^^
就像 ``HTTP`` 路由一样，频道路由也可以利用显式或隐式 路由模型绑定。例如，相比于接收一个字符串或数字类型的 ``order ID`` ，你也可以请求一个真正的 ``Order`` 模型实例:

.. code-block:: php

    <?php
    Broadcast::channel('order.{order}', function ($user, Order $order) {
        return $user->id === $order->user_id;
    });



接下来，让我们打开 Laravel 默认广播系统配置文件 ``config/broadcasting.php`` 看看里面的配置选项：

.. code-block:: php

    <?php
    return [

        /*
        |--------------------------------------------------------------------------
        | 默认广播驱动
        |--------------------------------------------------------------------------
        |
        | 该配置选项用于配置项目需要提供广播服务时的默认驱动器。配置连接器可以使任何一个
        | 在 "connections" 节点配置的驱动名称。
        | 支持: "pusher", "redis", "log", "null"
        |
        */

        'default' => env('BROADCAST_DRIVER', 'null'),

        /*
        |--------------------------------------------------------------------------
        | 广播连接
        |--------------------------------------------------------------------------
        |
        | 在这里，您可以定义将用于向其他系统或websockets广播事件的所有广播连接。
        | 在该数组内提供每种可用连接类型的样本。
        |
        */

        'connections' => [

            'pusher' => [
                'driver' => 'pusher',
                'key' => env('PUSHER_APP_KEY'),
                'secret' => env('PUSHER_APP_SECRET'),
                'app_id' => env('PUSHER_APP_ID'),
                'options' => [
                    //
                ],
            ],

            'redis' => [
                'driver' => 'redis',
                'connection' => 'default',
            ],

            'log' => [
                'driver' => 'log',
            ],

            'null' => [
                'driver' => 'null',
            ],

        ],

    ];

在调试阶段，我们可以选择使用 ``log`` 作为广播驱动。同时如果选用 ``log`` 驱动，也就表示客户端将不会接收任何消息，而只是将需要广播的消息写入到 ``laravel.log`` 日志文件内。

Laravel 广播系统支持 3 中不同频道类型 - ``public(公共)`` ， ``private(私有)`` 和 ``presence(存在)`` 。当系统需要向所用用户推送信息时，可以使用 ``public(公共)`` 类型的频道。相反，如果仅需要将消息推送给指定的频道，则需要使用 ``private(私有)`` 类型的频道。




