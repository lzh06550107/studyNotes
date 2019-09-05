******
Socket
******

.. contents:: 目录
   :depth: 4

``ReactPHP`` 的异步，流明文 ``TCP/IP`` 和安全 ``TLS`` 套接字服务器和客户端连接。

套接字库基于 ``EventLoop`` 和 ``Stream`` 组件为套接字层服务器和客户端提供可重用的接口。其服务器组件允许您构建接受来自网络客户端（例如 ``HTTP`` 服务器）的传入连接的网络服务器。其客户端组件允许您构建与网络服务器（例如 ``HTTP`` 或数据库客户端）建立传出连接的网络客户端。此库为所有这些提供了异步，流式传输方式，因此您可以无阻塞地处理多个并发连接。

快速例子
=======
如果您连接该服务器并发送任何内容，它会关闭连接：

.. code-block:: php

	$loop = React\EventLoop\Factory::create();
	$socket = new React\Socket\Server('127.0.0.1:8080', $loop);

	$socket->on('connection', function (React\Socket\ConnectionInterface $connection) {
	    $connection->write("Hello " . $connection->getRemoteAddress() . "!\n");
	    $connection->write("Welcome to this amazing server!\n");
	    $connection->write("Here's a tip: don't say anything.\n");

	    $connection->on('data', function ($data) use ($connection) {
	        $connection->close();
	    });
	});

	$loop->run();

更多 `例子 <https://github.com/reactphp/socket/blob/v1.2.0/examples>`_

这是一个客户端，输出服务器的输出到控制台，然后尝试发送一个字符串：

.. code-block:: php

	$loop = React\EventLoop\Factory::create();
	$connector = new React\Socket\Connector($loop);

	$connector->connect('127.0.0.1:8080')->then(function (React\Socket\ConnectionInterface $connection) use ($loop) {
	    $connection->pipe(new React\Stream\WritableResourceStream(STDOUT, $loop));
	    $connection->write("Hello World!\n");
	});

	$loop->run();

连接的使用
========

ConnectionInterface
--------------------
``ConnectionInterface`` 用于表示任何传入和传出连接，例如普通的 ``TCP/IP`` 连接。

传入或传出连接是实现 ``React`` 的 ``DuplexStreamInterface`` 的双工流（可读和可写）。它包含已建立此连接的本地和远程地址（客户端 ``IP`` ）的额外属性。

最常见的是，实现此 ``ConnectionInterface`` 的实例由实现 ``ServerInterface`` 的所有类发出，并由实现 ``ConnectorInterface`` 的所有类使用。

因为 ``ConnectionInterface`` 实现了底层的 ``DuplexStreamInterface`` ，所以您可以像往常一样使用它的任何事件和方法：

.. code-block:: php

	$connection->on('data', function ($chunk) {
	    echo $chunk;
	});

	$connection->on('end', function () {
	    echo 'ended';
	});

	$connection->on('error', function (Exception $e) {
	    echo 'error: ' . $e->getMessage();
	});

	$connection->on('close', function () {
	    echo 'closed';
	});

	$connection->write($data);
	$connection->end($data = null);
	$connection->close();
	// …

getRemoteAddress()
^^^^^^^^^^^^^^^^^^^
``getRemoteAddress():?string`` 方法返回已建立此连接的完整远程地址（URI）。

.. code-block:: php

	$address = $connection->getRemoteAddress();
	echo 'Connection with ' . $address . PHP_EOL;

如果此时无法确定远程地址或未知（例如在连接关闭后），则可能会返回 ``NULL`` 值。

否则，它将返回完整地址（URI）作为字符串值，例如 ``tcp://127.0.0.1:8080`` ， ``tcp://[:: 1]:80`` ， ``tls://127.0.0.1:443`` ， ``unix://example.sock`` 或 ``unix:///path/to/example.sock`` 。请注意，各个 ``URI`` 组件是特定于应用程序的，并且取决于底层传输协议。

如果这是基于 ``TCP/IP`` 的连接而您只需要远程 ``IP`` ，则可以使用以下代码：

.. code-block:: php

	$address = $connection->getRemoteAddress();
	$ip = trim(parse_url($address, PHP_URL_HOST), '[]');
	echo 'Connection with ' . $ip . PHP_EOL;

getLocalAddress()
^^^^^^^^^^^^^^^^^
``getLocalAddress():?string`` 方法返回已建立此连接的完整本地地址（URI）。

.. code-block:: php

	$address = $connection->getLocalAddress();
	echo 'Connection with ' . $address . PHP_EOL;

如果此时无法确定本地地址或未知（例如在连接关闭之后），则可能会返回 ``NULL`` 值。

否则，它将返回完整地址（URI）作为字符串值，例如 ``tcp://127.0.0.1:8080`` ， ``tcp://[:: 1]:80`` ， ``tls://127.0.0.1:443`` ， ``unix://example.sock`` 或 ``unix:///path/to/example.sock`` 。请注意，各个 ``URI`` 组件是特定于应用程序的，并且取决于底层传输协议。

此方法补充了 ``getRemoteAddress()`` 方法，因此不应混淆它们。

如果您的 ``TcpServer`` 实例正在侦听多个接口（例如，使用地址 ``0.0.0.0`` ），则可以使用此方法找出实际接受此连接的接口（例如公开接口或本地接口）。

如果您的系统有多个接口（例如 ``WAN`` 和 ``LAN`` 接口），则可以使用此方法找出实际用于此连接的接口。

服务端使用
=========

ServerInterface
----------------
``ServerInterface`` 负责提供接受传入流连接的接口，例如普通的 ``TCP/IP`` 连接。

大多数更高级别的组件（例如 ``HTTP`` 服务器）接受实现此接口的实例以接受传入的流连接。这通常是通过依赖注入完成的，因此实际将此实现与此接口的任何其他实现交换相当简单。这意味着您应该对此接口进行类型提示，而不是此接口的具体实现。

除了定义一些方法之外，该接口还实现了 ``EventEmitterInterface`` ，它允许您对某些事件做出反应。

连接事件
^^^^^^^^
只要新连接建立，即新客户端连接到此服务器套接字，就会发出 ``connection`` 事件：

.. code-block:: php

	$server->on('connection', function (React\Socket\ConnectionInterface $connection) {
	    echo 'new connection' . PHP_EOL;
	});


错误事件
^^^^^^^^
只要从客户端接受新连接时出错，就会发出 ``error`` 事件。

.. code-block:: php

	$server->on('error', function (Exception $e) {
	    echo 'error: ' . $e->getMessage() . PHP_EOL;
	});

请注意，这不是致命错误事件，即服务器即使在此事件之后也会继续侦听新连接。

getAddress()
^^^^^^^^^^^^^
``getAddress():?string`` 方法可用于返回此服务器当前正在侦听的完整地址（URI）。

.. code-block:: php

	$address = $server->getAddress();
	echo 'Server listening on ' . $address . PHP_EOL;

如果此时无法确定或未知地址（例如在套接字关闭后），则可能会返回 ``NULL`` 值。

否则，它将返回完整地址（URI）作为字符串值，例如 ``tcp://127.0.0.1:8080`` ， ``tcp://[:: 1]:80`` ， ``tls://127.0.0.1:443`` ， ``unix://example.sock`` 或 ``unix:///path/to/example.sock`` 。请注意，各个 ``URI`` 组件是特定于应用程序的，并且取决于底层传输协议。

如果这是基于 ``TCP/IP`` 的服务器而您只需要本地端口，则可以使用以下代码：

.. code-block:: php

	$address = $server->getAddress();
	$port = parse_url($address, PHP_URL_PORT);
	echo 'Server listening on port ' . $port . PHP_EOL;

pause()
^^^^^^^^
``pause():void`` 方法可用于暂停接受新的传入连接。

从 ``EventLoop`` 中删除套接字资源，从而停止接受新连接。请注意，侦听套接字保持活动状态且未关闭。

这意味着新的传入连接将在操作系统请求队列中保持挂起状态，直到其可配置的请求队列被填满。一旦请求队列充满，操作系统可以拒绝进一步的传入连接，直到通过恢复接受新连接再次耗尽请求队列。

服务器暂停后，不应再发出任何 ``connection`` 事件。

.. code-block:: php

	$server->pause();

	$server->on('connection', assertShouldNeverCalled());

此方法仅供参考，但通常不推荐，服务器可以继续发送连接事件。

除非另有说明，否则成功打开的服务器不应该在暂停状态下启动。

您可以再次调用 ``resume()`` 继续处理事件。

请注意，这两种方法都可以被调用任意次，特别是多次调用 ``pause()`` 不应该有任何影响。同样，在 ``close()`` 之后调用它是无效操作。

resume()
^^^^^^^^
``resume():void`` 方法可用于恢复接受新的传入连接。

在上一次 ``pause()`` 之后将套接字资源重新附加到 ``EventLoop`` 。

.. code-block:: php

	$server->pause();

	$loop->addTimer(1.0, function () use ($server) {
	    $server->resume();
	});

请注意，这两种方法都可以被调用任意次，特别是在没有事先暂停的情况下调用 ``resume()`` 该方法不应该有任何影响。同样，在 ``close()`` 之后调用它是无效操作。

close()
^^^^^^^^
``close():void`` 方法可用于关闭此侦听套接字。这将停止侦听此套接字上的新传入连接。

这将停止侦听此套接字上的新传入连接。

.. code-block:: php

	echo 'Shutting down server socket' . PHP_EOL;
	$server->close();

在同一个实例上多次调用此方法是无效操作。

Server
-------
``Server`` 类是此程序包中实现 ``ServerInterface`` 的主类，允许您接受传入的流连接，例如纯文本 ``TCP/IP`` 或安全 ``TLS`` 连接流。在 ``Unix`` 域套接字上也可以接受连接。

.. code-block:: php

    $server = new React\Socket\Server(8080, $loop);

如上所述， ``$uri`` 参数只能由一个端口组成，在这种情况下，服务器将默认监听 ``localhost`` 地址 ``127.0.0.1`` ，这意味着它不能从该系统外部访问。

要使用随机端口分配，您可以使用端口 ``0`` ：

.. code-block:: php

	$server = new React\Socket\Server(0, $loop);
	$address = $server->getAddress();

为了更改套接字正在侦听的主机，您可以通过给构造函数的第一个参数提供 ``IP`` 地址，可选地在 ``tcp://`` 协议之后：

.. code-block:: php

    $server = new React\Socket\Server('192.168.0.1:8080', $loop);

如果要监听 ``IPv6`` 地址，必须将主机括在方括号中：

.. code-block:: php

    $server = new React\Socket\Server('[::1]:8080', $loop);

要侦听 ``Unix`` 域套接字（UDS）路径，必须在 ``URI`` 前面添加 ``unix://`` 方案：

.. code-block:: php

    $server = new React\Socket\Server('unix:///tmp/server.sock', $loop);

如果给定的 ``URI`` 无效，不包含端口，任何其它协议或者如果它包含主机名，则会抛出 ``InvalidArgumentException`` ：

.. code-block:: php

	// throws InvalidArgumentException due to missing port
	$server = new React\Socket\Server('127.0.0.1', $loop);

如果给定的 ``URI`` 看起来有效，但是监听它失败（例如，如果端口已经在使用或 ``1024`` 以下的端口可能需要 ``root`` 访问等），它将抛出 ``RuntimeException`` ：

.. code-block:: php

	$first = new React\Socket\Server(8080, $loop);

	// throws RuntimeException because port is already in use
	$second = new React\Socket\Server(8080, $loop);

.. note:: 请注意，这些错误情况可能因系统和/或配置而异。有关实际错误情况的更多详细信息，请参阅异常消息和代码。

（可选）您可以为基础流套接字资源指定 ``TCP`` 套接字上下文选项，如下所示：

.. code-block:: php

	$server = new React\Socket\Server('[::1]:8080', $loop, array(
	    'tcp' => array(
	        'backlog' => 200,
	        'so_reuseport' => true,
	        'ipv6_v6only' => true
	    )
	));

.. note:: 请注意，可用的套接字上下文选项，它们的默认值以及更改这些选项的效果可能因系统和/或PHP版本而异。传递未知的上下文选项无效。出于BC原因，您还可以将TCP套接字上下文选项作为简单数组传递，而不将其包装在tcp键下的另一个数组中。

您可以通过简单地添加 ``tls://URI`` 协议来启动安全TLS（以前称为SSL）服务器。在内部，它将等待明文 ``TCP/IP`` 连接，然后为每个连接执行 ``TLS`` 握手。因此，它需要有效的 ``TLS`` 上下文选项，如果您使用 ``PEM`` 编码的证书文件，则其最基本的形式可能看起来像这样：

.. code-block:: php

	$server = new React\Socket\Server('tls://127.0.0.1:8080', $loop, array(
	    'tls' => array(
	        'local_cert' => 'server.pem'
	    )
	));

.. note:: 请注意，证书文件不会在实例化时加载，但是当传入连接初始化其TLS上下文时。这意味着任何无效的证书文件路径或内容只会在以后导致错误事件。

如果您的私钥使用密码加密，则必须像下面这样指定：

.. code-block:: php

	$server = new React\Socket\Server('tls://127.0.0.1:8000', $loop, array(
	    'tls' => array(
	        'local_cert' => 'server.pem',
	        'passphrase' => 'secret'
	    )
	));

默认情况下，此服务器支持 ``TLSv1.0+`` ，并且不包括对旧版 ``SSLv2/SSLv3`` 的支持。从 ``PHP 5.6+`` 开始，您还可以明确选择要与远程端协商的 ``TLS`` 版本：

.. code-block:: php

	$server = new React\Socket\Server('tls://127.0.0.1:8000', $loop, array(
	    'tls' => array(
	        'local_cert' => 'server.pem',
	        'crypto_method' => STREAM_CRYPTO_METHOD_TLSv1_2_SERVER
	    )
	));

.. note:: 请注意， `可用的TLS上下文选项 <http://php.net/manual/en/context.ssl.php>`_ ，其默认值以及更改这些选项的效果可能因系统和/或PHP版本而异。外部上下文数组允许您同时使用tcp（可能还有更多）上下文选项。传递未知的上下文选项无效。如果不使用 ``tls://`` 协议，则传递tls上下文选项无效。

每当客户端连接时，它将使用实现 ``ConnectionInterface`` 的连接实例发出连接事件：

.. code-block:: php

	$server->on('connection', function (React\Socket\ConnectionInterface $connection) {
	    echo 'Plaintext connection from ' . $connection->getRemoteAddress() . PHP_EOL;

	    $connection->write('hello there!' . PHP_EOL);
	    …
	});

.. note:: 请注意， ``Server`` 类是 ``TCP/IP`` 套接字的具体实现。如果要在更高级别的协议实现中键入提示，则应该使用通用 ``ServerInterface`` 。


服务端高级使用
-------------
TcpServer
^^^^^^^^^^
``TcpServer`` 类实现 ``ServerInterface`` 并负责接受纯文本 ``TCP/IP`` 连接。

.. code-block:: php

    $server = new React\Socket\TcpServer(8080, $loop);

如上所述， ``$uri`` 参数只能由一个端口组成，在这种情况下，服务器将默认监听 ``localhost`` 地址 ``127.0.0.1`` ，这意味着它不能从该系统外部访问。

要使用随机端口分配，您可以使用端口 ``0`` ：

.. code-block:: php

	$server = new React\Socket\TcpServer(0, $loop);
	$address = $server->getAddress();

为了更改套接字正在侦听的主机，您可以通过给构造函数的第一个参数提供 ``IP`` 地址，可选地在 ``tcp://`` 协议之后：

.. code-block:: php

	$server = new React\Socket\TcpServer('192.168.0.1:8080', $loop);

如果要监听 ``IPv6`` 地址，必须将主机括在方括号中：

.. code-block:: php

    $server = new React\Socket\TcpServer('[::1]:8080', $loop);

如果给定的 ``URI`` 无效，不包含端口，任何其它协议或者如果它包含主机名，则会抛出 ``InvalidArgumentException`` ：

.. code-block:: php

	// throws InvalidArgumentException due to missing port
	$server = new React\Socket\TcpServer('127.0.0.1', $loop);

如果给定的 ``URI`` 看起来有效，但是监听它失败（例如，如果端口已经在使用或 ``1024`` 以下的端口可能需要 ``root`` 访问等），它将抛出 ``RuntimeException`` ：

.. code-block:: php

	$first = new React\Socket\TcpServer(8080, $loop);

	// throws RuntimeException because port is already in use
	$second = new React\Socket\TcpServer(8080, $loop);

.. note:: 请注意，这些错误情况可能因系统和/或配置而异。有关实际错误情况的更多详细信息，请参阅异常消息和代码。

（可选）您可以为基础流套接字资源指定 `套接字上下文选项 <http://php.net/manual/en/context.socket.php>`_ ，如下所示：

.. code-block:: php

	$server = new React\Socket\TcpServer('[::1]:8080', $loop, array(
	    'backlog' => 200,
	    'so_reuseport' => true,
	    'ipv6_v6only' => true
	));

.. note:: 请注意，可用的套接字上下文选项，它们的默认值以及更改这些选项的效果可能因系统和/或PHP版本而异。传递未知的上下文选项无效。

每当客户端连接时，它将使用实现 ``ConnectionInterface`` 的连接实例发出连接事件：

.. code-block:: php

	$server->on('connection', function (React\Socket\ConnectionInterface $connection) {
	    echo 'Plaintext connection from ' . $connection->getRemoteAddress() . PHP_EOL;

	    $connection->write('hello there!' . PHP_EOL);
	    …
	});

SecureServer
^^^^^^^^^^^^^
``SecureServer`` 类实现 ``ServerInterface`` ，负责提供安全的 TLS（以前称为SSL）服务器。

它通过包装等待明文 ``TCP/IP`` 连接的 ``TcpServer`` 实例，然后为每个连接执行 ``TLS`` 握手来实现。因此，它需要有效的 `TLS 上下文选项 <http://php.net/manual/en/context.ssl.php>`_，如果您使用 ``PEM`` 编码的证书文件，则其最基本的形式可能看起来像这样：

.. code-block:: php

	$server = new React\Socket\TcpServer(8000, $loop);
	$server = new React\Socket\SecureServer($server, $loop, array(
	    'local_cert' => 'server.pem'
	));

.. note:: 请注意，证书文件不会在实例化时加载，而是在当传入连接初始化其TLS上下文时。这意味着任何无效的证书文件路径或内容只会在以后导致错误事件。

如果您的私钥使用密码加密，则必须像下面这样指定：

.. code-block:: php

	$server = new React\Socket\TcpServer(8000, $loop);
	$server = new React\Socket\SecureServer($server, $loop, array(
	    'local_cert' => 'server.pem',
	    'passphrase' => 'secret'
	));

默认情况下，此服务器支持 ``TLSv1.0+`` ，并且不包括对旧版 ``SSLv2/SSLv3`` 的支持。从 ``PHP 5.6+`` 开始，您还可以明确选择要与远程端协商的 ``TLS`` 版本：

.. code-block:: php

	$server = new React\Socket\TcpServer(8000, $loop);
	$server = new React\Socket\SecureServer($server, $loop, array(
	    'local_cert' => 'server.pem',
	    'crypto_method' => STREAM_CRYPTO_METHOD_TLSv1_2_SERVER
	));

.. note:: 请注意，可用的 `TLS上下文选项 <http://php.net/manual/en/context.ssl.php>`_ ，其默认值以及更改这些选项的效果可能因系统或PHP版本而异。传递未知的上下文选项无效。

每当客户端完成 ``TLS`` 握手时，它将使用实现 ``ConnectionInterface`` 的连接实例发出连接事件：

.. code-block:: php

	$server->on('connection', function (React\Socket\ConnectionInterface $connection) {
	    echo 'Secure connection from' . $connection->getRemoteAddress() . PHP_EOL;

	    $connection->write('hello there!' . PHP_EOL);
	    …
	});

每当客户端无法执行成功的 ``TLS`` 握手时，它将发出错误事件，然后关闭基础 ``TCP/IP`` 连接：

.. code-block:: php

	$server->on('error', function (Exception $e) {
	    echo 'Error' . $e->getMessage() . PHP_EOL;
	});

请注意， ``SecureServer`` 类是 ``TLS`` 套接字的具体实现。如果要在更高级别的协议实现中键入提示，则应该使用通用 ``ServerInterface`` 。

.. note:: 高级用法：尽管允许任何 ``ServerInterface`` 作为第一个参数，但您应该将 ``TcpServer`` 实例作为第一个参数传递，除非您知道自己在做什么。在内部， ``SecureServer`` 必须在底层流资源上设置所需的 ``TLS`` 上下文选项。这些资源不通过此程序包中定义的任何接口公开，而只通过内部 ``Connection`` 类公开。 ``TcpServer`` 类保证发出实现 ``ConnectionInterface`` 的连接，并使用内部 ``Connection`` 类来公开这些底层资源。如果使用自定义 ``ServerInterface`` 且其连接事件不满足此要求， ``SecureServer`` 将发出错误事件，然后关闭底层连接。

UnixServer
^^^^^^^^^^
``UnixServer`` 类实现 ``ServerInterface`` ，负责接受 ``Unix`` 域套接字（UDS）上的连接。

.. code-block:: php

    $server = new React\Socket\UnixServer('/tmp/server.sock', $loop);

如上所述， ``$uri`` 参数只能由以 ``unix://`` 协议为前缀的套接字路径或套接字路径组成。

如果给定的 ``URI`` 看起来有效，但是监听它失败（例如，如果套接字已经在使用或文件不可访问等），它将抛出 ``RuntimeException`` ：

.. code-block:: php

	$first = new React\Socket\UnixServer('/tmp/same.sock', $loop);

	// throws RuntimeException because socket is already in use
	$second = new React\Socket\UnixServer('/tmp/same.sock', $loop);

.. note:: 请注意，这些错误情况可能因系统或配置而异。特别是，当 UDS 路径已经存在且无法绑定时， Zend PHP 仅报告“未知错误”。在这种情况下，您可能需要检查给定 UDS 路径上的 ``is_file()`` 以报告更加用户友好的错误消息。有关实际错误情况的更多详细信息，请参阅异常消息和代码。

每当客户端连接时，它将使用实现 ``ConnectionInterface`` 的连接实例发出连接事件：

.. code-block:: php

	$server->on('connection', function (React\Socket\ConnectionInterface $connection) {
	    echo 'New connection' . PHP_EOL;

	    $connection->write('hello there!' . PHP_EOL);
	    …
	});

LimitingServer
^^^^^^^^^^^^^^
``LimitingServer`` 装饰器包装给定的 ``ServerInterface`` ，负责限制和跟踪与此服务器实例的打开连接。

每当底层服务器发出 ``connection`` 事件时，它将检查其限制然后

- 通过将此连接添加到打开的连接列表来跟踪此连接，然后转发连接事件
- 或超过其限制时拒绝（关闭）连接，并转发错误事件。

每当连接关闭时，它将从打开的连接列表中删除此连接。

.. code-block:: php

	$server = new React\Socket\LimitingServer($server, 100);
	$server->on('connection', function (React\Socket\ConnectionInterface $connection) {
	    $connection->write('hello there!' . PHP_EOL);
	    …
	});

`更多例子查看 <https://github.com/reactphp/socket/blob/v1.2.0/examples>`_

您必须传递最大数量的打开连接，以确保服务器在超过此限制时自动拒绝（关闭）连接。在这种情况下，它将发出 ``error`` 事件以通知此事件，并且不会发出任何 ``connection`` 事件。

.. code-block:: php

	$server = new React\Socket\LimitingServer($server, 100);
	$server->on('connection', function (React\Socket\ConnectionInterface $connection) {
	    $connection->write('hello there!' . PHP_EOL);
	    …
	});

您可以传递空值( ``null`` )限制，以便对打开的连接数量没有限制，并继续接受新连接，直到您的操作系统资源（例如打开文件句柄）用完为止。如果您不想限制连接但仍想使用 ``getConnections()`` 方法，这可能很有用。

您可以选择将服务器配置为在达到连接限制后暂停接受新连接。在这种情况下，它将暂停底层服务器，并且根本不再处理任何新连接，因此也不再关闭任何过多的连接。底层操作系统负责保持待处理连接的请求队列，直到达到其限制，此时它将开始拒绝进一步的连接。一旦服务器低于连接限制，它将继续消耗来自请求队列的连接，并将处理每个连接上的任何未完成数据。此模式可能对某些旨在等待响应消息（例如 ``HTTP`` ）的协议有用，但对于需要立即响应的其他协议（例如交互式聊天中的“欢迎”消息）可能不太有用。

.. code-block:: php

	$server = new React\Socket\LimitingServer($server, 100, true);
	$server->on('connection', function (React\Socket\ConnectionInterface $connection) {
	    $connection->write('hello there!' . PHP_EOL);
	    …
	});

getConnections()
""""""""""""""""
``getConnections():ConnectionInterface[]`` 方法可用于返回具有所有当前活动连接的数组。

.. code-block:: php

	foreach ($server->getConnection() as $connection) {
	    $connection->write('Hi!');
	}

客户端使用
==========

ConnectorInterface
------------------
``ConnectorInterface`` 负责提供用于建立流连接的接口，例如普通的 ``TCP/IP`` 连接。

这是此软件包中定义的主要接口，它在整个 ``React`` 的庞大生态系统中使用。

大多数更高级别的组件（例如 ``HTTP`` ，数据库或其他网络服务客户端）接受实现此接口的实例，以创建与底层网络服务的 ``TCP/IP`` 连接。这通常是通过依赖注入完成的，因此实际将此实现与此接口的任何其他实现交换相当简单。

接口只提供一种方法：

connect()
^^^^^^^^^^
``connect(string $ uri):PromiseInterface<ConnectionInterface，Exception>`` 方法可用于创建到给定远程地址的流连接。

它返回一个 ``Promise`` ，它在成功时返回一个实现 ``ConnectionInterface`` 的流的参数，或者如果连接不成功则返回一个 ``Exception`` 参数：

.. code-block:: php

	$connector->connect('google.com:443')->then(
	    function (React\Socket\ConnectionInterface $connection) {
	        // connection successfully established
	    },
	    function (Exception $error) {
	        // failed to connect due to $error
	    }
	);

返回的 ``Promise`` 必须以这样的方式实现，即当它仍处于未决状态时可以取消它。取消挂起的承诺必须使用异常作为拒绝的值。它应该在适用的情况下清理任何底层资源和引用：

.. code-block:: php

	$promise = $connector->connect($uri);

	$promise->cancel();

Connector
---------
``Connector`` 类是此包中的主类，它实现 ``ConnectorInterface`` 并允许您创建流连接。

您可以使用此连接器创建任何类型的流连接，例如纯文本 ``TCP/IP`` ，安全 ``TLS`` 或本地 ``Unix`` 连接流。

它绑定到主事件循环，可以像这样使用：

.. code-block:: php

	$loop = React\EventLoop\Factory::create();
	$connector = new React\Socket\Connector($loop);

	$connector->connect($uri)->then(function (React\Socket\ConnectionInterface $connection) {
	    $connection->write('...');
	    $connection->end();
	});

	$loop->run();

要创建纯文本 ``TCP/IP`` 连接，您只需传递一个主机和端口组合，如下所示：

.. code-block:: php

	$connector->connect('www.google.com:80')->then(function (React\Socket\ConnectionInterface $connection) {
	    $connection->write('...');
	    $connection->end();
	});

.. note:: 如果未在目标 ``URI`` 中指定 ``URI`` 协议，则它将假定 ``tcp://`` 作为默认值并建立纯文本 ``TCP/IP`` 连接。请注意， ``TCP/IP`` 连接需要目标 ``URI`` 中的主机和端口部分，如上所述，所有其他 ``URI`` 组件都是可选的。

为了创建安全的 ``TLS`` 连接，您可以使用 ``tls://`` URI 协议，如下所示：

.. code-block:: php

	$connector->connect('tls://www.google.com:443')->then(function (React\Socket\ConnectionInterface $connection) {
	    $connection->write('...');
	    $connection->end();
	});

为了创建本地 ``Unix`` 域套接字连接，您可以使用如下的 ``unix://`` URI 协议：

.. code-block:: php

	$connector->connect('unix:///tmp/demo.sock')->then(function (React\Socket\ConnectionInterface $connection) {
	    $connection->write('...');
	    $connection->end();
	});

.. note:: ``getRemoteAddress()`` 方法将返回给 ``connect()`` 方法的目标 ``Unix`` 域套接字（UDS）路径，包括 ``unix://`` 协议，例如 ``unix:///tmp/demo.sock`` 。对于 ``UDS`` 连接 ``getLocalAddress()`` 方法很可能返回 ``null`` 值。

在底层下， Connector 实现为此包中实现的较低级别连接器的更高级别的外观。这意味着它还共享所有功能和实现细节。如果要在更高级别的协议实现中键入提示，则应该使用通用的 ``ConnectorInterface`` 。

``Connector`` 类将尝试检测您的系统 ``DNS`` 设置（如果无法确定您的系统设置，则使用 ``Google`` 的公共 ``DNS`` 服务器 ``8.8.8.8`` 作为后备），以便默认情况下将所有公共主机名解析为基础 ``IP`` 地址。如果您明确要使用自定义 ``DNS`` 服务器（例如本地 ``DNS`` 中继或公司范围的 ``DNS`` 服务器），则可以像这样设置连接器：

.. code-block:: php

	$connector = new React\Socket\Connector($loop, array(
	    'dns' => '127.0.1.1'
	));

	$connector->connect('localhost:80')->then(function (React\Socket\ConnectionInterface $connection) {
	    $connection->write('...');
	    $connection->end();
	});

如果您根本不想使用 ``DNS`` 解析器并且只想连接到 ``IP`` 地址，您也可以像这样设置连接器：

.. code-block:: php

	$connector = new React\Socket\Connector($loop, array(
	    'dns' => false
	));

	$connector->connect('127.0.0.1:80')->then(function (React\Socket\ConnectionInterface $connection) {
	    $connection->write('...');
	    $connection->end();
	});

高级：如果您需要自定义 ``DNS`` 解析器实例，您还可以像这样设置连接器：

.. code-block:: php

	$dnsResolverFactory = new React\Dns\Resolver\Factory();
	$resolver = $dnsResolverFactory->createCached('127.0.1.1', $loop);

	$connector = new React\Socket\Connector($loop, array(
	    'dns' => $resolver
	));

	$connector->connect('localhost:80')->then(function (React\Socket\ConnectionInterface $connection) {
	    $connection->write('...');
	    $connection->end();
	});

默认情况下， ``tcp://`` 和 ``tls://`` URI协议将使用超时值来覆盖 ``default_socket_timeout`` ``ini`` 设置（默认为 ``60`` 秒）。如果你想要一个自定义超时值，你可以像这样传递：

.. code-block:: php

	$connector = new React\Socket\Connector($loop, array(
	    'timeout' => 10.0
	));

同样，如果你根本不想应用超时并让操作系统处理这个，你可以传递一个像这样的布尔标志：

.. code-block:: php

	$connector = new React\Socket\Connector($loop, array(
	    'timeout' => false
	));

默认情况下， ``Connector`` 支持 ``tcp://`` ， ``tls://`` 和 ``unix://`` URI协议。如果你想明确禁止任何这些，你可以简单地传递像这样的布尔标志：

.. code-block:: php

	// only allow secure TLS connections
	$connector = new React\Socket\Connector($loop, array(
	    'tcp' => false,
	    'tls' => true,
	    'unix' => false,
	));

	$connector->connect('tls://google.com:443')->then(function (React\Socket\ConnectionInterface $connection) {
	    $connection->write('...');
	    $connection->end();
	});

``tcp://`` 和 ``tls://`` 也接受传递给底层连接器的其他上下文选项。如果要显式传递其他上下文选项，可以简单地传递上下文选项数组，如下所示：

.. code-block:: php

	// allow insecure TLS connections
	$connector = new React\Socket\Connector($loop, array(
	    'tcp' => array(
	        'bindto' => '192.168.0.1:0'
	    ),
	    'tls' => array(
	        'verify_peer' => false,
	        'verify_peer_name' => false
	    ),
	));

	$connector->connect('tls://localhost:443')->then(function (React\Socket\ConnectionInterface $connection) {
	    $connection->write('...');
	    $connection->end();
	});

默认情况下，此连接器支持 ``TLSv1.0+`` ，并且不包括对旧版 ``SSLv2/SSLv3`` 的支持。从 ``PHP 5.6+`` 开始，您还可以明确选择要与远程端协商的 ``TLS`` 版本：

.. code-block:: php

	$connector = new React\Socket\Connector($loop, array(
	    'tls' => array(
	        'crypto_method' => STREAM_CRYPTO_METHOD_TLSv1_2_CLIENT
	    )
	));

.. note:: 有关上下文选项的更多详细信息，请参阅有关 `套接字上下文选项 <http://php.net/manual/en/context.socket.php>`_ 和 `SSL上下文选项 <http://php.net/manual/en/context.ssl.php>`_ 的PHP文档。

高级：默认情况下， ``Connector`` 支持 ``tcp://`` ， ``tls://`` 和 ``unix://`` URI协议。为此，它会自动设置所需的连接器类。如果要为其中任何一个显式传递自定义连接器，您只需传递实现 ``ConnectorInterface`` 的实例，如下所示：

.. code-block:: php

	$dnsResolverFactory = new React\Dns\Resolver\Factory();
	$resolver = $dnsResolverFactory->createCached('127.0.1.1', $loop);
	$tcp = new React\Socket\DnsConnector(new React\Socket\TcpConnector($loop), $resolver);

	$tls = new React\Socket\SecureConnector($tcp, $loop);

	$unix = new React\Socket\UnixConnector($loop);

	$connector = new React\Socket\Connector($loop, array(
	    'tcp' => $tcp,
	    'tls' => $tls,
	    'unix' => $unix,

	    'dns' => false,
	    'timeout' => false,
	));

	$connector->connect('google.com:80')->then(function (React\Socket\ConnectionInterface $connection) {
	    $connection->write('...');
	    $connection->end();
	});

.. note:: 在内部，``tcp://`` 连接器将始终由 ``DNS`` 解析器包装，除非您在上面的示例中禁用 ``DNS`` 。在这种情况下， ``tcp://`` 连接器接收实际的主机名而不是仅解析的 ``IP`` 地址，因此负责执行查找。在内部，自动创建的 ``tls://`` 连接器将始终包装底层 ``tcp://`` 连接器，以便在启用安全 ``TLS`` 模式之前建立基础明文 ``TCP/IP`` 连接。如果您只想使用自定义底层 ``tcp://`` 连接器进行安全 ``TLS`` 连接，则可以显式传递上面的 ``tls://`` 连接器。在内部， ``tcp://`` 和 ``tls://`` 连接器将始终由 ``TimeoutConnector`` 包装，除非您禁用超时，如上例所示。


客户端高级使用
--------------

TcpConnector
^^^^^^^^^^^^^
``TcpConnector`` 类实现 ``ConnectorInterface`` ，并允许您创建到任何 ``IP`` 端口组合的纯文本 ``TCP/IP`` 连接：

.. code-block:: php

	$tcpConnector = new React\Socket\TcpConnector($loop);

	$tcpConnector->connect('127.0.0.1:80')->then(function (React\Socket\ConnectionInterface $connection) {
	    $connection->write('...');
	    $connection->end();
	});

	$loop->run();

可以通过取消其挂起的 ``promise`` 来取消挂起的连接尝试，如下所示：

.. code-block:: php

	$promise = $tcpConnector->connect('127.0.0.1:80');

	$promise->cancel();

在挂起的 ``promise`` 上调用 ``cancel()`` 将关闭底层套接字资源，从而取消挂起的 ``TCP/IP`` 连接，并拒绝生成的 ``promise`` 。您可以选择将其他套接字上下文选项传递给构造函数，如下所示：

.. code-block:: php

	$tcpConnector = new React\Socket\TcpConnector($loop, array(
	    'bindto' => '192.168.0.1:0'
	));

请注意，此类仅允许您连接到 ``IP`` 端口组合。如果给定的 ``URI`` 无效，不包含有效的 ``IP`` 地址和端口或包含任何其他协议，它将拒绝 ``InvalidArgumentException`` ：

如果给定的 ``URI`` 看起来有效，但连接到它失败（例如远程主机拒绝连接等），它将拒绝 ``RuntimeException`` 。

如果要连接到 ``hostname-port-combinations`` ，请参阅以下章节。

.. note:: 高级用法：在内部，TcpConnector为每个流资源分配一个空的上下文资源。如果目标URI包含hostname查询参数，则其值将用于设置TLS对等名称。SecureConnector和DnsConnector使用它来验证对等名称，如果需要自定义TLS对等名称，也可以使用它。


DnsConnector
^^^^^^^^^^^^
``DnsConnector`` 类实现 ``ConnectorInterface`` ，并允许您创建到任何主机名 - 端口组合的纯文本 ``TCP/IP`` 连接。

它通过修饰给定的 ``TcpConnector`` 实例来实现，以便它首先通过 ``DNS`` 查找给定的域名（如果适用），然后建立底层解析的目标为 ``IP`` 地址的 ``TCP/IP`` 连接。

确保设置 ``DNS`` 解析器和底层 ``TCP`` 连接器，如下所示：

.. code-block:: php

	$dnsResolverFactory = new React\Dns\Resolver\Factory();
	$dns = $dnsResolverFactory->createCached('8.8.8.8', $loop);

	$dnsConnector = new React\Socket\DnsConnector($tcpConnector, $dns);

	$dnsConnector->connect('www.google.com:80')->then(function (React\Socket\ConnectionInterface $connection) {
	    $connection->write('...');
	    $connection->end();
	});

	$loop->run();

可以通过取消其挂起的 ``promise`` 来取消挂起的连接尝试，如下所示：

.. code-block:: php

	$promise = $dnsConnector->connect('www.google.com:80');

	$promise->cancel();

在挂起的 ``promise`` 上调用 ``cancel()`` 将取消底层 ``DNS`` 查找或底层 ``TCP/IP`` 连接，并生成拒绝的 ``promise`` 。

.. note:: 高级用法：在内部， ``DnsConnector`` 依赖于解析器来查找给定主机名的 IP 地址。然后，它将使用此 IP 替换目标 URI 中的主机名，并附加主机名查询参数，并将此更新的 URI 传递给基础连接器。因此，底层连接器负责创建与目标 IP 地址的连接，而此查询参数可用于检查原始主机名，并由 ``TcpConnector`` 用于设置 ``TLS`` 对等端名称。如果显式指定了主机名，则不会修改此查询参数，如果您需要自定义 ``TLS`` 对等名称，这将非常有用。


SecureConnector
^^^^^^^^^^^^^^^^
``SecureConnector`` 类实现 ``ConnectorInterface`` ，并允许您创建到任何主机名 - 端口组合的安全 ``TLS`` （以前称为SSL）连接。

它通过修饰给定的 ``DnsConnector`` 实例来实现，以便它首先创建纯文本 ``TCP/IP`` 连接，然后在此流上启用 ``TLS`` 加密。

.. code-block:: php

	$secureConnector = new React\Socket\SecureConnector($dnsConnector, $loop);

	$secureConnector->connect('www.google.com:443')->then(function (React\Socket\ConnectionInterface $connection) {
	    $connection->write("GET / HTTP/1.0\r\nHost: www.google.com\r\n\r\n");
	    ...
	});

	$loop->run();

可以通过取消其挂起的 ``promise`` 来取消挂起的连接尝试，如下所示：

.. code-block:: php

	$promise = $secureConnector->connect('www.google.com:443');

	$promise->cancel();

在挂起的 ``promise`` 上调用 ``cancel()`` 将取消基础 ``TCP/IP`` 连接或 ``SSL/TLS`` 协商，并生成拒绝的 ``promise`` 。

您可以选择将其他 ``SSL`` 上下文选项传递给构造函数，如下所示：

.. code-block:: php

	$secureConnector = new React\Socket\SecureConnector($dnsConnector, $loop, array(
	    'verify_peer' => false,
	    'verify_peer_name' => false
	));

默认情况下，此连接器支持 ``TLSv1.0+`` ，并且不包括对旧版 ``SSLv2/SSLv3`` 的支持。从 ``PHP 5.6+`` 开始，您还可以明确选择要与远程端协商的 ``TLS`` 版本：

.. code-block:: php

	$secureConnector = new React\Socket\SecureConnector($dnsConnector, $loop, array(
	    'crypto_method' => STREAM_CRYPTO_METHOD_TLSv1_2_CLIENT
	));

.. note:: 高级用法：在内部， ``SecureConnector`` 依赖于在底层流资源上设置必须的上下文选项。因此，它应该与连接器堆栈中某处的 ``TcpConnector`` 一起使用，以便它可以为每个流资源分配一个空的上下文资源并验证对等端名称。如果不这样做可能会导致TLS对等名称不匹配错误或某些难以跟踪的竞争条件，因为否则所有流资源都将使用单个共享默认上下文资源。

TimeoutConnector
^^^^^^^^^^^^^^^^
``TimeoutConnector`` 类实现 ``ConnectorInterface`` ，并允许您将超时处理添加到任何现有连接器实例。

它通过装饰任何给定的 ``ConnectorInterface`` 实例并启动一个定时器来实现，该定时器将自动拒绝并中止任何底层连接尝试太长的连接。

.. code-block:: php

	$timeoutConnector = new React\Socket\TimeoutConnector($connector, 3.0, $loop);

	$timeoutConnector->connect('google.com:80')->then(function (React\Socket\ConnectionInterface $connection) {
	    // connection succeeded within 3.0 seconds
	});

可以通过取消其挂起的 ``promise`` 来取消挂起的连接尝试，如下所示：

.. code-block:: php

	$promise = $timeoutConnector->connect('google.com:80');

	$promise->cancel();

在挂起的 ``promise`` 上调用 ``cancel()`` 将取消底层连接尝试，中止计时器并生成拒绝的 ``promise`` 。

UnixConnector
^^^^^^^^^^^^^
``UnixConnector`` 类实现 ``ConnectorInterface`` ，并允许您连接到 ``Unix`` 域套接字（UDS）路径，如下所示：

.. code-block:: php

	$connector = new React\Socket\UnixConnector($loop);

	$connector->connect('/tmp/demo.sock')->then(function (React\Socket\ConnectionInterface $connection) {
	    $connection->write("HELLO\n");
	});

	$loop->run();

连接到 ``Unix`` 域套接字是一种原子操作，即它的承诺将立即解决（解决或拒绝）。因此，对生成的 ``promise`` 调用 ``cancel()`` 无效。

.. note:: ``getRemoteAddress()`` 方法将返回给予 ``connect()`` 方法的目标 ``Unix`` 域套接字（UDS）路径，前缀为 ``unix://`` 协议，例如 ``unix:///tmp/demo.sock`` 。 ``getLocalAddress()`` 方法很可能返回 ``null`` 值，因为此值不适用于此处的 ``UDS`` 连接。

FixUriConnector
^^^^^^^^^^^^^^^
``FixedUriConnector`` 类实现 ``ConnectorInterface`` 并装饰现有的 ``Connector`` 以始终使用固定的预配置 ``URI`` 。

这对于不支持某些 ``URI`` 的消费者非常有用，例如当您希望显式连接到 ``Unix`` 域套接字（UDS）路径而不是连接到更高级别 ``API`` 所假定的默认地址时：

.. code-block:: php

	$connector = new React\Socket\FixedUriConnector(
	    'unix:///var/run/docker.sock',
	    new React\Socket\UnixConnector($loop)
	);

	// destination will be ignored, actually connects to Unix domain socket
	$promise = $connector->connect('localhost:80');

安装
====

.. code-block:: shell

    $ composer require react/socket:^1.2

测试
====

要运行测试套件，首先需要克隆此 ``repo`` ，然后通过 ``Composer`` 安装所有依赖项：

.. code-block:: shell

    $ composer install

要运行测试套件，请转到项目根目录并运行：

.. code-block:: shell

    $ php vendor/bin/phpunit

测试套件还包含许多依赖稳定互联网连接的功能集成测试。如果你不想运行它们，可以像这样简单地跳过它们：

.. code-block:: shell

    $ php vendor/bin/phpunit --exclude-group internet
