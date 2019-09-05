****
HTTP
****

基于 ``ReactPHP`` 的事件驱动的流式普通 ``HTTP`` 和安全 ``HTTPS`` 。

简单例子
========
这是一个对每一个请求响应 ``Hello World`` 的 ``HTTP`` 服务器。

.. code-block:: php

$loop = React\EventLoop\Factory::create();

$server = new Server(function (ServerRequestInterface $request) {
    return new Response(
        200,
        array(
            'Content-Type' => 'text/plain'
        ),
        "Hello World!\n"
    );
});

$socket = new React\Socket\Server(8080, $loop);
$server->listen($socket);

$loop->run();

更多例子 `查看 <https://github.com/reactphp/http/tree/v0.8.4/examples>`_ 。

使用
====

服务器
------
``Server`` 类负责处理传入连接，然后处理每个传入的 ``HTTP`` 请求。

它在内存中缓冲并解析的完整传入 ``HTTP`` 请求。一旦收到完整的请求，它将调用请求处理程序。

对于每个请求，它执行传递给构造函数的回调函数并传入相应请求对象，期望返回相应的响应对象。

.. code-block:: php

	$server = new Server(function (ServerRequestInterface $request) {
	    return new Response(
	        200,
	        array(
	            'Content-Type' => 'text/plain'
	        ),
	        "Hello World!\n"
	    );
	});

对于大多数用户而言，在将请求作为 ``PSR-7`` 请求处理之前缓冲和解析请求的服务器是他们想要的。 ``Server`` 门面处理这一点，并采取更高级的配置。在底层它使用 ``StreamingServer`` 和三个使用php.ini的默认设置的自带中间件。

``LimitConcurrentRequestsMiddleware`` 需要一个限制，因此 ``Server`` 门面使用 ``memory_limit`` 和 ``post_max_size`` ``ini`` 设置来计算合理的限制。它假定最大四分之一的 ``memory_limit`` 用于缓冲，另外四分之三用于解析和处理请求。限制是将 ``memory_limit`` 的一半四舍五入。

.. note:: 请注意，封装的 ``StreamingServer`` 发出的任何错误都由 ``Server`` 转发。


StreamingServer
---------------
高级 ``StreamingServer`` 类负责处理传入连接，然后处理每个传入的 ``HTTP`` 请求。

与 ``Server`` 类不同，它默认不缓冲和解析传入的 ``HTTP`` 请求体。这意味着将使用流请求主体调用请求处理程序。

对于每个请求，它执行传递给构造函数的回调函数并传入相应请求对象，期望返回相应的响应对象。

.. code-block:: php

	$server = new StreamingServer(function (ServerRequestInterface $request) {
	    return new Response(
	        200,
	        array(
	            'Content-Type' => 'text/plain'
	        ),
	        "Hello World!\n"
	    );
	});

为了处理任何连接，服务器需要连接到 ``React\Socket\ServerInterface`` 的实例，该实例发出底层流连接，然后将传入数据解析为 ``HTTP`` 。

您可以将它附加到 ``React\Socket\Server`` ，以便启动明文 ``HTTP`` 服务器，如下所示：

.. code-block:: php

	$server = new StreamingServer($handler);

	$socket = new React\Socket\Server(8080, $loop);
	$server->listen($socket);

有关更多详细信息，另请参阅 ``listen()`` 方法和第一个示例。

同样，您也可以将其附加到 ``React\Socket\SecureServer`` 以启动安全的 ``HTTPS`` 服务器，如下所示：

.. code-block:: php

	$server = new StreamingServer($handler);

	$socket = new React\Socket\Server(8080, $loop);
	$socket = new React\Socket\SecureServer($socket, $loop, array(
	    'local_cert' => __DIR__ . '/localhost.pem'
	));

	$server->listen($socket);

有关更多详细信息，另请参见 `示例＃11 <https://github.com/reactphp/http/blob/v0.8.4/examples>`_ 。

当 ``HTTP/1.1`` 客户端想要发送更大的请求主体时，它们可能只发送带有额外 ``Expect:100-continue`` 标头的请求标头，并在发送实际（大）消息体之前等待。在这种情况下，服务器将自动向客户端发送中间 ``HTTP/1.1 100 Continue`` 响应。这可确保您按预期方式无延迟地收到请求正文。仍然需要按照上面的示例中的描述创建响应。

``StreamingServer`` 支持 ``HTTP/1.1`` 和 ``HTTP/1.0`` 请求消息。如果客户端发送无效的请求消息，使用无效的 ``HTTP`` 协议版本或在请求标头中发送无效的 ``Transfer-Encoding`` ，它将发出错误事件，向客户端发送 ``HTTP`` 错误响应并关闭连接：

.. code-block:: php

	$server->on('error', function (Exception $e) {
	    echo 'Error: ' . $e->getMessage() . PHP_EOL;
	});

如果在回调函数中返回无效类型或具有未处理的 ``Exception`` 或 ``Throwable`` ，则服务器也将发出错误事件。如果您的回调函数抛出 ``Exception`` 或 ``Throwable`` ， ``StreamingServer`` 将发出 ``RuntimeException`` 并添加先前抛出的异常：

.. code-block:: php

	$server->on('error', function (Exception $e) {
	    echo 'Error: ' . $e->getMessage() . PHP_EOL;
	    if ($e->getPrevious() !== null) {
	        $previousException = $e->getPrevious();
	        echo $previousException->getMessage() . PHP_EOL;
	    }
	});

请注意，请求对象也可以发出错误。查看请求以获取更多详细信息。


请求
----
如上所示， ``Server`` 和 ``StreamingServer`` 类负责处理传入连接，然后处理每个传入的 ``HTTP`` 请求。

一旦客户端收到请求，将处理请求对象。此请求对象实现 `PSR-7 ServerRequestInterface <https://github.com/php-fig/fig-standards/blob/master/accepted/PSR-7-http-message.md#32-psrhttpmessagerequestinterface>`_ ，后者又扩展了 ``PSR-7 RequestInterface`` ，并将像这样传递给回调函数。

.. code-block:: php

	$server = new Server(function (ServerRequestInterface $request) {
	   $body = "The method of the request is: " . $request->getMethod();
	   $body .= "The requested path is: " . $request->getUri()->getPath();

	   return new Response(
	       200,
	       array(
	           'Content-Type' => 'text/plain'
	       ),
	       $body
	   );
	});

请求参数
^^^^^^^^
``getServerParams():mixed[]`` 方法可用于获取类似于 ``$_SERVER`` 变量的服务器端参数。目前提供以下参数：

REMOTE_ADDR ：请求发送方IP地址；
REMOTE_PORT ：请求发送方端口；
SERVER_ADDR ：服务器IP地址；
SERVER_PORT ：服务器端口；
REQUEST_TIME ：收到完整请求标头时的 ``Unix`` 时间戳，类似于 ``time()`` 的整数；
REQUEST_TIME_FLOAT ：收到完整请求标头时的 ``Unix`` 时间戳，类似于 ``microtime`` 的浮点数（true）；
HTTPS ：如果请求使用 ``HTTPS`` ，则设置为 ``on`` ，否则将不会设置；

.. code-block:: php

	$server = new Server(function (ServerRequestInterface $request) {
	    $body = "Your IP is: " . $request->getServerParams()['REMOTE_ADDR'];

	    return new Response(
	        200,
	        array(
	            'Content-Type' => 'text/plain'
	        ),
	        $body
	    );
	});

查看 `示例3 <https://github.com/reactphp/http/blob/v0.8.4/examples>`_

.. note:: 高级：请注意，如果您正在侦听 ``Unix`` 域套接字（UDS）路径，则不会设置地址参数，因为此协议缺少主机/端口的概念。

查询参数
^^^^^^^^
``getQueryParams():array`` 方法可用于获取类似于 ``$_GET`` 变量的查询参数。

.. code-block:: php

	$server = new Server(function (ServerRequestInterface $request) {
	    $queryParams = $request->getQueryParams();

	    $body = 'The query parameter "foo" is not set. Click the following link ';
	    $body .= '<a href="/?foo=bar">to use query parameter in your request</a>';

	    if (isset($queryParams['foo'])) {
	        $body = 'The value of "foo" is: ' . htmlspecialchars($queryParams['foo']);
	    }

	    return new Response(
	        200,
	        array(
	            'Content-Type' => 'text/html'
	        ),
	        $body
	    );
	});

上例中的响应将返回带有链接的响应正文。 ``URL`` 包含值为 ``bar`` 的查询参数 ``foo`` 。使用此示例中的 ``htmlentities`` 来防止 `跨站点脚本（缩写为XSS） <https://en.wikipedia.org/wiki/Cross-site_scripting>`_ 。

查看 `示例#4 <https://github.com/reactphp/http/blob/v0.8.4/examples>`_

流请求
^^^^^^
如果您正在使用 ``Server`` ，那么请求对象将在内存中进行缓冲和解析，并包含完整的请求正文。这包括已解析的请求正文和任何上传的文件。

如果您使用的是高级 ``StreamingServer`` ，则会在收到请求标头后处理请求对象。这意味着无论（即在之前）接收（可能更大的）请求主体大小，都会发生这种情况。

虽然这在 ``PHP`` 生态系统中可能并不常见，但这实际上是一种非常强大的方法，它为您提供了一些其他方面无法实现的优势：

- 在接收大型请求主体之前对请求作出反应，例如拒绝未经身份验证的请求或超出允许的消息长度的请求（文件上传）。
- 在请求主体的剩余部分到达之前或者如果发送者正在缓慢地流式传输数据，则开始处理请求主体的部分。
- 处理大型请求主体而不必在内存中缓冲任何内容，例如接受大量文件上传或可能无限制的请求正文流。

``getBody()`` 方法可用于访问请求正文流。在默认流模式下，此方法返回实现 `PSR-7 StreamInterface <http://www.php-fig.org/psr/psr-7/#psrhttpmessagestreaminterface>`_ 和 `ReactPHP ReadableStreamInterface <https://reactphp.org/stream/#readablestreaminterface>`_ 的流实例。但是，大多数 ``PSR-7 StreamInterface`` 方法都是在控制请求体的假设下设计的。鉴于这不适用于此服务器，不使用以下 ``PSR-7 StreamInterface`` 方法，并且不应该调用： ``tell()`` ， ``eof()`` ， ``seek()`` ， ``rewind()`` ， ``write()`` 和 ``read()`` 。如果这是您的用例的问题或您想要访问上传的文件，强烈建议使用 ``RequestBodyBufferMiddleware`` 。 ``ReactPHP ReadableStreamInterface`` 使您可以在各个块到达时访问传入的请求主体：

.. code-block:: php

	$server = new StreamingServer(function (ServerRequestInterface $request) {
	    return new Promise(function ($resolve, $reject) use ($request) {
	        $contentLength = 0;
	        $request->getBody()->on('data', function ($data) use (&$contentLength) {
	            $contentLength += strlen($data);
	        });

	        $request->getBody()->on('end', function () use ($resolve, &$contentLength){
	            $response = new Response(
	                200,
	                array(
	                    'Content-Type' => 'text/plain'
	                ),
	                "The length of the submitted request body is: " . $contentLength
	            );
	            $resolve($response);
	        });

	        // an error occures e.g. on invalid chunked encoded data or an unexpected 'end' event
	        $request->getBody()->on('error', function (\Exception $exception) use ($resolve, &$contentLength) {
	            $response = new Response(
	                400,
	                array(
	                    'Content-Type' => 'text/plain'
	                ),
	                "An error occured while reading at length: " . $contentLength
	            );
	            $resolve($response);
	        });
	    });
	});

以上示例仅计算请求正文中接收的字节数。这可以用作缓存或处理请求主体的框架。

更多例子查看 `示例#9 <https://github.com/reactphp/http/blob/v0.8.4/examples>`_

只要请求正文流上有新数据，就会发出 ``data`` 事件。服务器还使用 ``Transfer-Encoding:chunked``  自动处理任何传入请求的解码并将实际有效负载作为数据发出。

当请求主体流成功终止时，将发出 ``end`` 事件，即它被读取直到其预期结束。

如果请求流包含 ``Transfer-Encoding: chunked`` 的无效数据或在收到完整请求流之前连接关闭的情况，则会发出 ``error`` 事件。服务器将自动停止从连接读取并丢弃所有传入数据而不是关闭它。仍然可以发送响应消息（除非连接已经关闭）。

``error`` 或 ``end`` 事件后将发出 ``close`` 事件。

有关请求正文流的更多详细信息，请查看 `ReactPHP ReadableStreamInterface <https://reactphp.org/stream/#readablestreaminterface>`_ 的文档。

如果您只想知道请求体大小，可以使用 ``getSize():?int`` 方法。此方法返回消息边界定义的请求正文的完整大小。如果请求消息不包含请求主体（例如简单的 ``GET`` 请求），则此值可以为 ``0`` 。请注意，如果请求正文大小事先未知，则此值可能为 ``null`` ，因为请求消息使用 ``Transfer-Encoding: chunked`` 。

.. code-block:: php

	$server = new StreamingServer(function (ServerRequestInterface $request) {
	    $size = $request->getBody()->getSize();
	    if ($size === null) {
	        $body = 'The request does not contain an explicit length.';
	        $body .= 'This example does not accept chunked transfer encoding.';

	        return new Response(
	            411,
	            array(
	                'Content-Type' => 'text/plain'
	            ),
	            $body
	        );
	    }

	    return new Response(
	        200,
	        array(
	            'Content-Type' => 'text/plain'
	        ),
	        "Request body size: " . $size . " bytes\n"
	    );
	});

请求方法
^^^^^^^^
请注意，服务器支持任何请求方法（包括自定义和非标准方法）以及 ``HTTP`` 规范中为每个相应方法定义的所有请求目标格式，包括正常的 ``origin-form`` 请求以及 ``absolute-form`` 和 ``authority-form`` 格式的代理请求。 ``getUri():UriInterface`` 方法可用于获取有效的请求 ``URI`` ，该 ``URI`` 允许您访问单个 ``URI`` 组件。请注意（取决于给定的 ``request-target`` ）某些 ``URI`` 组件可能存在也可能不存在，例如， ``getPath():string`` 方法将为 ``asterisk-form`` 或 ``authority-form`` 形式的请求返回空字符串。它的 ``getHost():string`` 方法将返回由有效请求 ``URI`` 确定的主机，如果 ``HTTP/1.0`` 客户端没有指定一个（即没有主机头），则默认为本地套接字地址。其 ``getScheme():string`` 方法将返回 ``http`` 或 ``https`` ，具体取决于请求是否通过与目标主机的安全 ``TLS`` 连接进行。

只有在此 ``URI`` 协议不标准的情况下，才会消毒 ``Host`` 头值以匹配此主机组件和端口组件。

您可以使用 ``getMethod():string`` 和 ``getRequestTarget():string`` 来检查这是一个已接受的请求，并且可能希望拒绝其他请求并带有相应错误代码，例如 ``400`` （错误请求）或 ``405`` （不允许方法）。

.. note:: ``CONNECT`` 方法在隧道设置（ ``HTTPS`` 代理）中很有用，而不是大多数 ``HTTP`` 服务器想要关注的内容。请注意，如果要处理此方法，客户端可以发送与 ``Host`` 头值不同的请求目标（例如删除默认端口），并且请求目标必须在转发时优先。

Cookie参数
^^^^^^^^^^
``getCookieParams():string[]`` 方法可用于获取当前请求发送的所有 ``cookie`` 。

.. code-block:: php

	$server = new Server(function (ServerRequestInterface $request) {
	    $key = 'react\php';

	    if (isset($request->getCookieParams()[$key])) {
	        $body = "Your cookie value is: " . $request->getCookieParams()[$key];

	        return new Response(
	            200,
	            array(
	                'Content-Type' => 'text/plain'
	            ),
	            $body
	        );
	    }

	    return new Response(
	        200,
	        array(
	            'Content-Type' => 'text/plain',
	            'Set-Cookie' => urlencode($key) . '=' . urlencode('test;more')
	        ),
	        "Your cookie has been set."
	    );
	});

上面的示例将尝试在首次访问时设置 ``cookie`` ，并尝试在所有后续尝试中打印 ``cookie`` 值。请注意该示例如何使用 ``urlencode()`` 函数对非字母数字字符进行编码。在解码 ``cookie`` 的名称和值时，这种编码也在内部使用（与其他实现一致，例如 ``PHP`` 的 ``cookie`` 函数）。

有关更多详细信息，另请参见 `示例＃5 <https://github.com/reactphp/http/blob/v0.8.4/examples>`_ 。

响应
----
传递给 ``Server`` 或高级 ``StreamingServer`` 的构造函数的回调函数负责处理请求并返回响应，该响应将传递给客户端。该函数必须返回一个实现 ``PSR-7 ResponseInterface`` 对象的实例或一个将解析 ``PSR-7 ResponseInterface`` 对象的 ``ReactPHP Promise`` 。

您将在此项目中找到一个实现 ``PSR-7 ResponseInterface`` 的 ``Response`` 类。我们在项目中使用此类的实例化，但您可以随意使用您喜欢的 ``PSR-7 ResponseInterface`` 的任何实现。

.. code-block:: php

	$server = new Server(function (ServerRequestInterface $request) {
	    return new Response(
	        200,
	        array(
	            'Content-Type' => 'text/plain'
	        ),
	        "Hello World!\n"
	    );
	});

延迟响应
^^^^^^^^
上面的示例直接返回响应，因为它不需要时间来处理。使用数据库，文件系统或长计算（实际上每个操作需要> = 1ms）来创建响应，将减慢服务器的速度。为了防止这种情况，你应该使用 ``ReactPHP Promise`` 。此示例显示了这样的长期操作如何编码：

.. code-block:: php

	$server = new Server(function (ServerRequestInterface $request) use ($loop) {
	    return new Promise(function ($resolve, $reject) use ($loop) {
	        $loop->addTimer(1.5, function() use ($resolve) {
	            $response = new Response(
	                200,
	                array(
	                    'Content-Type' => 'text/plain'
	                ),
	                "Hello world"
	            );
	            $resolve($response);
	        });
	    });
	});

以上示例将在 1.5 秒后创建响应。如果您的响应需要时间创建，此示例显示您需要一个承诺。当请求主体结束时， ``ReactPHP`` ``Promise`` 将在 ``Response`` 对象中解析。如果客户端在承诺仍处于挂起状态时关闭连接，则承诺将自动取消。 ``promise`` 取消处理程序可用于清除在这种情况下分配的任何待处理资源（如果适用）。如果在客户端关闭后解析了承诺，则只会忽略它。

流响应
^^^^^^
此项目中的 ``Response`` 类支持添加实现响应主体的 ``ReactPHP ReadableStreamInterface`` 的实例。因此，您可以将数据直接传输到响应正文中。请注意， ``PSR-7 ResponseInterface`` 的其它实现可能只支持字符串。

.. code-block:: php

	$server = new Server(function (ServerRequestInterface $request) use ($loop) {
	    $stream = new ThroughStream();

	    $timer = $loop->addPeriodicTimer(0.5, function () use ($stream) {
	        $stream->write(microtime(true) . PHP_EOL);
	    });

	    $loop->addTimer(5, function() use ($loop, $timer, $stream) {
	        $loop->cancelTimer($timer);
	        $stream->end();
	    });

	    return new Response(
	        200,
	        array(
	            'Content-Type' => 'text/plain'
	        ),
	        $stream
	    );
	});

上面的例子将每 ``0.5`` 秒发出当前 ``Unix`` 时间戳，微秒作为浮点数发送到客户端，并将在 ``5`` 秒后结束。这只是您可以使用流媒体的一个示例，您还可以通过小块发送大量数据或将其用于需要计算的正文数据。

如果请求处理程序使用已关闭的响应流解析，它将只发送一个空的响应主体。如果客户端在流仍处于打开状态时关闭连接，则响应流将自动关闭。如果在客户端关闭后使用流体体解析了承诺，则响应流将自动关闭。 ``close`` 事件可用于清除在这种情况下分配的任何待处理资源（如果适用）。

请注意，如果您使用实现 ``ReactPHP`` 的 ``DuplexStreamInterface`` 的体流实例（例如上例中的 ``ThroughStream`` ），则必须特别小心。

对于大多数情况，这将仅消耗其可读侧并转发（发送）流发出的任何数据，从而完全忽略流的可写侧。但是，如果这是对 ``CONNECT`` 方法的 ``101`` （切换协议）响应或 ``2xx`` （成功）响应，它也会将数据写入流的可写端。这可以通过使用 ``CONNECT`` 方法拒绝所有请求（这是大多数普通原始HTTP服务器可能会执行的操作）或者确保仅使用 ``ReadableStreamInterface`` 的实例来避免。

``101`` （切换协议）响应代码对于更高级的 ``Upgrade`` 请求非常有用，例如升级到 ``WebSocket`` 协议或实现超出 ``HTTP`` 规范和此 ``HTTP`` 库范围的自定义协议逻辑。如果你想处理 ``Upgrade:WebSocket`` 头，你可能会想要使用 `Ratchet <http://socketo.me/>`_ 。如果您想处理自定义协议，您可能需要查看 `HTTP规范 <https://tools.ietf.org/html/rfc7230#section-6.7>`_ ，还可以查看 `示例＃31 和 ＃32 <https://github.com/reactphp/http/blob/v0.8.4/examples>`_ 以获取更多详细信息。特别是，除非您发送同样存在于相应 ``HTTP/1.1`` 升级请求标头值中的升级响应标头值，否则不得使用 ``101`` （交换协议）响应代码。在这种情况下，服务器会自动负责发送 ``Connection:upgrade`` 标头值，因此您不必这样做。

<未完成>


响应长度
^^^^^^^^
如果响应正文大小已知，则会自动添加 ``Content-Length`` 响应标头。这是最常见的用例，例如当使用这样的字符串响应体时：

.. code-block:: php

	$server = new Server(function (ServerRequestInterface $request) {
	    return new Response(
	        200,
	        array(
	            'Content-Type' => 'text/plain'
	        ),
	        "Hello World!\n"
	    );
	});

如果响应正文大小未知，则无法自动添加 ``Content-Length`` 响应标头。当使用没有显式 ``Content-Length`` 响应头的流式响应时，传出的 ``HTTP/1.1`` 响应消息将自动使用 ``Transfer-Encoding:chunked`` ，而旧的 ``HTTP/1.0`` 响应消息将包含普通的响应主体。如果您知道流式响应正文的长度，您可能希望明确指定它：

.. code-block:: php

	$server = new Server(function (ServerRequestInterface $request) use ($loop) {
	    $stream = new ThroughStream();

	    $loop->addTimer(2.0, function () use ($stream) {
	        $stream->end("Hello World!\n");
	    });

	    return new Response(
	        200,
	        array(
	            'Content-Length' => '13',
	            'Content-Type' => 'text/plain',
	        ),
	        $stream
	    );
	});

对 ``HEAD`` 请求的任何响应以及具有 ``1xx`` （信息）， ``204`` （无内容）或 ``304`` （未修改）状态代码的任何响应将不包括根据 ``HTTP`` 规范的消息正文。这意味着您的回调不必特别注意这一点，任何响应体都将被忽略。

类似地，对 ``CONNECT`` 请求的任何 ``2xx`` （成功）响应和具有 ``1xx`` （信息）或 ``204`` （无内容）状态代码的任何响应将不包括 ``Content-Length`` 或 ``Transfer-Encoding`` 标头，因为这些不适用于这些消息。请注意，对 ``HEAD`` 请求和具有 ``304`` （未修改）状态代码的任何响应的响应可能包括这些标头，即使该消息不包含响应主体，因为如果相同的请求使用（无条件）GET，则这些标头将应用于该消息。

无效响应
^^^^^^^^
回调函数的代码中的无效返回值或未处理的 ``Exception`` 或 ``Throwable`` 将导致 ``500 Internal Server Error`` 消息。确保捕获 ``Exceptions`` 或 ``Throwables`` 以创建自己的响应消息。

默认响应头
^^^^^^^^^^
在回调函数中返回后，响应将分别由 ``Server`` 或 ``StreamingServer`` 处理。他们将添加请求的协议版本，因此您不必这样做。

如果没有给出 ``Date`` 标题，系统日期和时间将自动添加。您可以像这样自己添加自定义 ``Date`` 标题：

.. code-block:: php

	$server = new Server(function (ServerRequestInterface $request) {
	    return new Response(
	        200,
	        array(
	            'Date' => date('D, d M Y H:i:s T')
	        )
	    );
	});

如果您没有合适的时钟可供使用，则应使用空字符串取消设置此标头：

.. code-block:: php

	$server = new Server(function (ServerRequestInterface $request) {
	    return new Response(
	        200,
	        array(
	            'Date' => ''
	        )
	    );
	});

请注意，除非您自己指定自定义 ``X-Powered-By`` 标头，否则它将自动采用 ``X-Powered-By:react/alpha`` 标头：

.. code-block:: php

	$server = new Server(function (ServerRequestInterface $request) {
	    return new Response(
	        200,
	        array(
	            'X-Powered-By' => 'PHP 3'
	        )
	    );
	});

如果您根本不想发送此标头，可以使用空字符串作为值，如下所示：

.. code-block:: php

	$server = new Server(function (ServerRequestInterface $request) {
	    return new Response(
	        200,
	        array(
	            'X-Powered-By' => ''
	        )
	    );
	});

请注意，目前不支持持久连接( ``Connection：keep-alive`` ) 。因此， ``HTTP/1.1`` 响应消息将自动包含 ``Connection:close`` 标头，而不管显式传递哪些标头值。

中间件
------
如上所述， ``Server`` 和高级 ``StreamingServer`` 接受单个请求处理程序参数，该参数负责处理传入的 ``HTTP`` 请求，然后创建并返回传出的 ``HTTP`` 响应。

许多常见用例涉及在将传入的 ``HTTP`` 请求传递给最终的业务逻辑请求处理程序之前对其进行验证，处理和操作。因此，该项目支持中间件请求处理程序的概念。

中间件请求处理程序应遵循以下规则：

- 这是一个有效的 ``callable`` ；
- 它接受 ``ServerRequestInterface`` 作为第一个参数，并接受可选的 ``callable`` 作为第二个参数；
- 它返回：

  + 实现 ``ResponseInterface`` 以供直接使用的实例；
  + 可以被 ``Promise\resolve()`` 消耗并解析为 ``ResponseInterface`` 延迟消耗的任何承诺；
  + 它可以抛出 ``Exception`` （或返回被拒绝的承诺）以发出错误信号并中止链；

- 它调用 ``$next($request)`` 继续处理下一个中间件请求处理程序或显式返回而不调用 ``$next`` 来中止链。

  + ``$next`` 请求处理程序（递归地）使用与上面相同的逻辑从链中调用下一个请求处理程序，并返回（或抛出），如上所述；
  + 可以在调用 ``$next($request)`` 之前修改 ``$request`` 以更改下一个中间件操作的传入请求；
  + 可以消耗 ``$next`` 返回值来修改传出响应；
  + 如果要实现自定义“重试”逻辑等，可以多次调用 ``$next`` 请求处理程序；

请注意，这个非常简单的定义允许您使用匿名函数或使用具有 ``__invoke()`` 魔术方法的任何类。这使您可以轻松地动态创建自定义中间件请求处理程序，或使用基于类的方法来轻松使用现有的中间件实现。

虽然该项目确实提供了使用中间件实现的方法，但它并不旨在定义中间件实现应该如何。我们意识到有一个生动的中间件实现生态系统，并且正在努力使这些与PSR-15（HTTP服务器请求处理程序）之间的接口标准化并支持这一目标。因此，该项目仅捆绑了一些中间件实现，这些实现与PHP的请求行为相匹配（见下文），并积极鼓励第三方中间件实现。

为了使用中间件请求处理程序，只需将具有上述定义的所有可调用项的数组分别传递给 ``Server`` 或 ``StreamingServer`` 。下面的示例添加了一个中间件请求处理程序，它将当前时间作为标题（Request-Time）添加到请求中，并且最终请求处理程序始终返回不带正文的 ``200`` 代码：

.. code-block:: php

	$server = new Server(array(
	    function (ServerRequestInterface $request, callable $next) {
	        $request = $request->withHeader('Request-Time', time());
	        return $next($request);
	    },
	    function (ServerRequestInterface $request) {
	        return new Response(200);
	    }
	));



限制并发请求中间件
^^^^^^^^^^^^^^^^^


请求主体缓存中间件
^^^^^^^^^^^^^^^^^


请求主体解析中间件
^^^^^^^^^^^^^^^^^


第三方中间件
^^^^^^^^^^^


安装
====




测试
====
































