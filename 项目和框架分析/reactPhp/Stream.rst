******
Stream
******

.. contents:: 目录
   :depth: 4

介绍
====
事件驱动的可读写流，用于 ``ReactPHP`` 中的非阻塞 ``I/O`` 。为了使 ``EventLoop`` 更易于使用，该组件引入了强大的“流”概念。 ``Streams`` 允许您以小块方式有效地处理大量数据（例如多千兆字节文件下载），而无需一次将所有内容存储在内存中。它们与 ``PHP`` 本身的流非常相似，但是一个更适合异步，非阻塞 ``I/O`` 的接口。

示例
====
以下示例可用于将源文件的内容传递到目标文件，而无需将整个文件读入内存：

.. code-block:: php

	$loop = new React\EventLoop\StreamSelectLoop;

	$source = new React\Stream\ReadableResourceStream(fopen('source.txt', 'r'), $loop);
	$dest = new React\Stream\WritableResourceStream(fopen('destination.txt', 'w'), $loop);

	$source->pipe($dest);

	$loop->run();

.. note:: 请注意，此示例仅使用 ``fopen()`` 进行演示。这不应该用在真正的异步程序中，因为文件系统本身就是阻塞的，每次调用可能需要几秒钟。另请参阅为更复杂的 `示例 <https://reactphp.org/stream/#creating-streams>`_ 创建流。

使用
====
``ReactPHP`` 在其整个生态系统中使用“流”的概念，为处理任意数据内容和大小的流提供一致的高级抽象。虽然流本身是一个非常低级的概念，但它可以用作强大的抽象来构建更高级别的组件和协议。

如果您对这个概念不熟悉，可以将它们视为水管：您可以从源头消耗水，或者您可以生产水并将其转移（管道）到任何目的地（水槽）。

同样，流也可以：

- 可读（如 ``STDIN`` 终端输入）或
- 可写（如 ``STDOUT`` 端子输出）或
- 双工（可读写，如 ``TCP/IP`` 连接）

因此，该包定义了以下三个接口：

- ``ReadableStreamInterface``
- ``WritableStreamInterface``
- ``DuplexStreamInterface``

ReadableStreamInterface
-----------------------
``ReadableStreamInterface`` 负责为只读流和双工流的可读侧提供接口。

除了定义一些方法之外，该接口还实现了 ``EventEmitterInterface`` ，它允许您对某些事件做出反应。

事件回调函数必须是一个有效的可调用( ``callable`` )函数，它遵循严格的参数定义，并且必须完全按照文档记录接受事件参数。事件回调函数绝不能抛出异常 ( ``Exception`` )。事件回调函数的返回值将被忽略且无效，因此出于性能原因，建议您不要返回任何值。

此接口的每个实现必须遵循这些事件语义，以便被视为行为良好的流。

.. note:: 请注意，此接口的更高级别实现可以选择使用此低级别流规范的一部分未定义的专用语义来定义其他事件。这些事件语义超出了此接口的范围，因此您可能还需要参考此类更高级别实现的文档。

data事件
^^^^^^^^
只要从此源流读取/接收某些数据，就会发出 ``data`` 事件。该事件接收传入数据的单个混合参数。

.. code-block:: php

	$stream->on('data', function ($data) {
	    echo $data;
	});

此事件可以触发任意次，如果此流不发送任何数据，则可能不会触发。 ``end`` 或 ``close`` 事件后不应该触发该事件。

给定的 ``$data`` 参数可以是混合类型，但通常建议它应该是一个字符串值，或者使用一种允许表示为字符串(string)类型以实现最大兼容性。

许多公共流（如 ``TCP/IP`` 连接或基于文件的流）将发出原始（二进制）有效负载数据，这些数据作为字符串值的块通过线路接收。

由于基于流的性质，发送方可以发送具有不同大小的任何数量的块。无法保证这些接收块与将发送方打算发送的的帧完全相同。换句话说，许多较低级别的协议（例如 ``TCP/IP`` ）以块的形式传输数据，这些块可以是单字节值到几十千字节之间的任何值。您可能希望将更高级别的协议应用于这些低级别数据块，以达到正确的消息帧。

end事件
^^^^^^^
一旦源流成功到达流的末尾（EOF），就会发出 ``end`` 事件。

.. code-block:: php

	$stream->on('end', function () {
	    echo 'END';
	});

此事件应该发出一次或根本不发出，具体取决于是否检测到成功结束。它不应该在上一次 ``end`` 或 ``close`` 事件后发出。如果流由于非成功结束而关闭，则不得发出它，例如它前面的 ``error`` 事件之后。

流结束后，它必须切换到不可读模式，另请参阅 ``isReadable()`` 。

只有在成功结束时才会发出此事件，而不是在流被不可恢复的错误中断或显式关闭时发出。并非所有流都知道这种“成功结束”的概念。许多用例涉及检测流何时关闭（终止），在这种情况下，您应该使用 ``close`` 事件。在流发出 ``end`` 事件后，通常应该跟一个 ``close`` 事件。

如果远程端关闭连接或文件句柄被成功读取直到到达其结尾（EOF），则许多公共流（例如 ``TCP/IP`` 连接或基于文件的流）将发出此事件。

请注意，不应将此事件与 ``end()`` 方法混淆。此事件定义从源流读取成功结束，而 ``end()`` 方法定义将成功结束写入目标流。

error事件
^^^^^^^^^
通常在尝试从此流中读取时一旦发生致命错误，将发出 ``error`` 事件。该事件接收单个 ``Exception`` 类型的错误实例参数。

.. code-block:: php

	$server->on('error', function (Exception $e) {
	    echo 'Error: ' . $e->getMessage() . PHP_EOL;
	});

一旦流检测到致命错误（例如致命传输错误或在意外 ``data`` 或过早 ``end`` 事件之后），应该发出此事件。它不应该在上一个错误，结束或关闭事件之后发出。如果这不是致命的错误条件，例如临时网络问题不会导致任何数据丢失，则不得发出它。

在流错误之后，它必须关闭流，然后应该紧跟一个 ``close`` 事件，然后切换到不可读模式，另请参见 ``close()`` 和 ``isReadable()`` 。

许多公共流（例如 ``TCP/IP`` 连接或基于文件的流）仅处理数据传输，并且不对数据边界（例如意外 ``data`` 或过早 ``end`` 事件）进行考虑。换句话说，许多较低级协议（例如 TCP/IP ）可能选择在出现一次致命传输错误后发出该事件，然后关闭（终止）流作为响应。

如果此流是 ``DuplexStreamInterface`` ，您还应该注意流的可写端如何实现 ``error`` 事件。换句话说，当读取或写入应该导致相同错误处理的流时可能发生错误。

close事件
^^^^^^^^^
一旦流关闭（终止），将发出 ``close`` 事件。

.. code-block:: php

	$stream->on('close', function () {
	    echo 'CLOSED';
	});

这个事件应该发出一次或者根本不发出，这取决于流是否会终止。它不应该在前一个 ``close`` 事件后发出。

关闭流后，必须切换到不可读模式，另请参阅 ``isReadable()`` 。

与 ``end`` 事件不同，不管这是由于不可恢复的错误导致隐式发生还是在任何一方关闭流时明确发生，该事件应该在流关闭时发出。如果您只想检测成功结束，则应使用 ``end`` 事件。

许多常见的流（例如 ``TCP/IP`` 连接或基于文件的流）可能会在读取成功的 ``end`` 事件之后或在致命的传输 ``error`` 事件之后选择发出此事件。

如果此流是 ``DuplexStreamInterface`` ，您还应该注意流的可写端如何实现 ``close`` 事件。换句话说，在收到此事件后，流必须切换到不可写和不可读模式，另请参阅 ``isWritable()`` 。请注意，此事件不应与 ``end`` 事件混淆。

isReadable()
^^^^^^^^^^^^
``isReadable():bool`` 方法可用于检查此流是否处于可读状态（尚未关闭）。

此方法可用于检查流是否仍接受传入数据事件，或者它是否已经结束或关闭。一旦流不可读，就不应该发出更多的 ``data`` 或 ``end`` 事件。

.. code-block:: php

	assert($stream->isReadable() === false);

	$stream->on('data', assertNeverCalled());
	$stream->on('end', assertNeverCalled());

成功打开的流始终必须以可读模式启动。

一旦流结束或关闭，它必须切换到不可读模式。这可以在任何时候发生，如明确地通过 ``close()`` 或隐式地由于远程关闭或不可恢复的传输错误而发生。一旦流切换到不可读模式，它就不能转换回可读模式。

如果此流是 ``DuplexStreamInterface`` ，您还应该注意流的可写端如何实现 ``isWritable()`` 方法。除非这是半开双工流，否则它们通常应该具有相同的返回值。

pause()
^^^^^^^
``pause():void`` 方法可用于暂停读取传入的数据事件。

从事件循环中删除数据源文件描述符。这允许您限制传入的数据。

除非另有说明，否则成功打开的流不应该在暂停状态下启动。

暂停流后，不应发出任何进一步的 ``data`` 或 ``end`` 事件。

.. code-block:: php

	$stream->pause();

	$stream->on('data', assertShouldNeverCalled());
	$stream->on('end', assertShouldNeverCalled());

此方法仅供参考，但通常不推荐，流可能继续发出 ``data`` 事件。

您可以再次调用 ``resume()`` 继续处理事件。

请注意，这两种方法都可以被调用任意次，特别是多次调用 ``pause()`` 不应该有任何影响。


resume()
^^^^^^^^
``resume():void`` 方法可用于继续读取传入的 ``data`` 事件。

在上一次 ``pause()`` 之后重新附加数据源。

.. code-block:: php

	$stream->pause();

	$loop->addTimer(1.0, function () use ($stream) {
	    $stream->resume();
	});

请注意，这两种方法都可以被调用任意次，特别是在没有事先暂停的情况下调用 ``resume()`` 不应该有任何影响。

pipe()
^^^^^^
``pipe(WritableStreamInterface $ dest，array $ options = [])`` 方法可用于将来自此可读源的所有数据传输到给定的可写目标。

自动将所有传入数据发送到目标。根据目标可以处理的内容能力自动限制源读取。

.. code-block:: php

    $source->pipe($dest);

同样，您也可以将实现 ``DuplexStreamInterface`` 的实例通过管道传输到自身，以便回写所有收到的数据。这可能是 ``TCP/IP`` 回送服务的有用功能：

.. code-block:: php

    $connection->pipe($connection);

此方法按原样返回目标流，可用于设置管道流链：

.. code-block:: php

    $source->pipe($decodeGzip)->pipe($filterBadWords)->pipe($dest);

默认情况下，一旦源流发出 ``end`` 事件，这将调用目标流上的 ``end()`` 。这可以像下面这样禁用该功能：

.. code-block:: php

    $source->pipe($dest, array('end' => false));

请注意，这仅适用于 ``end`` 事件。如果源流上发生 ``error`` 或显式 ``close`` 事件，则必须手动关闭目标流：

.. code-block:: php

	$source->pipe($dest);
	$source->on('close', function () use ($dest) {
	    $dest->end('BYE!');
	});

如果源流不可读（关闭状态），则这是不可操作。

.. code-block:: php

	$source->close();
	$source->pipe($dest); // NO-OP

如果目标流不可写（关闭状态），那么这将简单地限制（暂停）源流：

.. code-block:: php

	$dest->close();
	$source->pipe($dest); // calls $source->pause()

同样，如果在管道仍处于活动状态时关闭目标流，它也会限制（暂停）源流：

.. code-block:: php

	$source->pipe($dest);
	$dest->close(); // calls $source->pause()

管道设置成功后，目标流必须发出使用此源流作为事件参数的 ``pipe`` 事件。

close()
^^^^^^^
``close():void`` 方法可用于关闭流（强制）。

此方法可用于（强制）关闭流。

.. code-block:: php

    $stream->close();

关闭流后，它应该发出一个 ``close`` 事件。请注意，此事件不应多次发出，特别是如果多次调用此方法。

调用此方法后，流必须切换到不可读模式，另请参阅 ``isReadable()`` 。这意味着不应该发出更多的 ``data`` 或 ``end`` 事件。

.. code-block:: php

	$stream->close();
	assert($stream->isReadable() === false);

	$stream->on('data', assertNeverCalled());
	$stream->on('end', assertNeverCalled());

如果此流是 ``DuplexStreamInterface`` ，您还应该注意流的可写端如何实现 ``close()`` 方法。换句话说，在调用此方法之后，流必须切换到不可写且不可读的模式，另请参阅 ``isWritable()`` 。请注意，不应将此方法与 ``end()`` 方法混淆。

WritableStreamInterface
------------------------
``WritableStreamInterface`` 负责为只写流和双工流的可写端提供接口。

除了定义一些方法之外，该接口还实现了 ``EventEmitterInterface`` ，它允许您对某些事件做出反应。

事件回调函数必须是一个有效的可调用函数(callable)，它遵循严格的参数定义，并且必须完全按照文档记录接受事件参数。事件回调函数绝不能抛出异常。事件回调函数的返回值将被忽略且无效，因此出于性能原因，建议您不要返回任何值。

此接口的每个实现必须遵循这些事件语义，以便被视为行为良好的流。

.. note:: 请注意，此接口的更高级别实现可以选择使用此低级别流规范的未定义一部分的专用语义来定义其他事件。与这些事件语义超出了此接口的范围，因此您可能还需要参考此类更高级别实现的文档。

drain事件
^^^^^^^^^
只要写缓冲区先前已满且现在准备接受更多数据，就会发出 ``drain`` 事件。

.. code-block:: php

	$stream->on('drain', function () use ($stream) {
	    echo 'Stream is now ready to accept more data';
	});

只要写缓冲区先前已满且现在准备接受更多数据，此事件应该发出一次。换句话说，此事件可以发出任意次数，如果缓冲区从未变满，则可能为零次。如果缓冲区未满，则不应发出此事件。

此事件主要在内部使用，另请参阅 ``write()`` 以获取更多详细信息。

pipe事件
^^^^^^^^
只要可读流是 ``pipe()`` 到此流中，就会发出 ``pipe`` 事件。该事件接收单个 ``ReadableStreamInterface`` 类型参数，即源流。

.. code-block:: php

	$stream->on('pipe', function (ReadableStreamInterface $source) use ($stream) {
	    echo 'Now receiving piped data';

	    // explicitly close target if source emits an error
	    $source->on('error', function () use ($stream) {
	        $stream->close();
	    });
	});

	$source->pipe($stream);

对于成功传送到此目标流的每个可读流，必须发出一次此事件。换句话说，这个事件可以被发送任意次，如果没有流被传输到这个流中，这可能是零次。如果源不可读（已关闭）或此目标不可写（已关闭），则不得发出此事件。

此事件主要在内部使用，另请参阅 ``pipe()`` 以获取更多详细信息。

error事件
^^^^^^^^^
通常在尝试写入此流时一旦发生致命错误，将发出错误事件。该事件接收错误实例的单个 ``Exception`` 参数。

.. code-block:: php

	$stream->on('error', function (Exception $e) {
	    echo 'Error: ' . $e->getMessage() . PHP_EOL;
	});

一旦流检测到致命错误（例如致命传输错误），应该发出此事件。它不应该在上一个 ``error`` 或 ``close`` 事件后发出。如果这不是致命的错误条件，例如临时网络问题不会导致任何数据丢失，则不得发出它。

在流错误之后，它必须关闭流，然后应该跟随一个 ``close`` 事件，然后切换到不可写模式，另请参见 ``close()`` 和 ``isWritable()`` 。

许多公共流（例如 ``TCP/IP`` 连接或基于文件的流）仅处理数据传输，并且一旦出现致命传输错误，则发送该事件，然后关闭（终止）流作为响应。

如果此流是 ``DuplexStreamInterface`` ，您还应该注意流的可读端也如何实现 ``error`` 事件。换句话说，在读取或写入导致相同错误处理的流时可能发生错误。

close事件
^^^^^^^^^
一旦流关闭（终止），将发出 ``close`` 事件。

.. code-block:: php

	$stream->on('close', function () {
	    echo 'CLOSED';
	});

取决于流是否会终止，这个事件应该发出一次或者根本不发出。它不应该在前一个 ``close`` 事件后发出。

流关闭后，它必须切换到不可写模式，另请参阅 ``isWritable()`` 。

每当流关闭时都应该发出此事件，不管这是由于不可恢复的错误是隐式发生还是在任何一方关闭流时明确发生。

许多公共流（例如 ``TCP/IP`` 连接或基于文件的流）可能会在 ``end()`` 方法刷新缓冲区后，在收到成功的 ``end`` 事件之后或在致命的传输 ``error`` 事件之后选择发出此事件。

如果此流是 ``DuplexStreamInterface`` ，您还应该注意流的可读端也如何实现 ``close`` 事件。换句话说，在收到此事件后，流必须切换到不可写和不可读模式，另请参阅 ``isReadable()`` 。请注意，此事件不应与 ``end`` 事件混淆。

isWritable()
^^^^^^^^^^^^
``isWritable():bool`` 方法可用于检查此流是否处于可写状态（尚未关闭）。

此方法可用于检查流是否仍接受写入任何数据，或者它是否已经结束或关闭。将任何数据写入不可写的流是无效操作：

.. code-block:: php

	assert($stream->isWritable() === false);

	$stream->write('end'); // NO-OP
	$stream->end('end'); // NO-OP

成功打开的流始终必须以可写模式启动。

一旦流结束或关闭，它必须切换到不可写模式。这可以在任何时候发生，明确地通过 ``end()`` 或 ``close()`` 或隐式地由于远程关闭或不可恢复的传输错误而发生。一旦流切换到不可写模式，它就不能转换回可写模式。

如果此流是 ``DuplexStreamInterface`` ，您还应该注意流的可读方面如何实现 ``isReadable()`` 方法。除非这是半开双工流，否则它们通常应该具有相同的返回值。

write()
^^^^^^^^
``write(mixed $data):bool`` 方法可用于将一些数据写入流中。

必须返回布尔值 ``true`` 确认成功写入，这意味着要么立即写入（刷新）数据，要么缓冲并安排将来写入。请注意，此接口无法控制显式刷新缓冲数据，因为找到适当的时间超出了此接口的范围，并留给此接口的实现。

许多公共流(例如 ``TCP/IP`` 连接或基于文件的流)可以选择缓冲所有给定数据，并通过使用基于 ``EventLoop`` 来检查资源实际可写时的未来刷新。

如果一个流不能处理写入(或刷新)数据，它应该发出一个 ``error`` 事件，如果它无法从这个错误中恢复，可以 ``close()`` 。

如果在添加 ``$data`` 后内部缓冲区已满，则 ``write()`` 应该返回 ``false`` ，表示调用者应该停止发送数据，直到缓冲区耗尽。一旦缓冲区准备好接受更多数据，流应该发送一个 ``drain`` 事件。

类似地，如果流不可写（已经处于关闭状态），它必须不处理给定的 ``$data`` 并且返回 ``false`` ，表示调用者应该停止发送数据。

给定的 ``$data`` 参数可以是混合类型，但通常建议它应该是一个字符串值，或者使用一种允许表示为字符串类型以获得最大兼容性。

许多公共流（例如 ``TCP/IP`` 连接或基于文件的流）将仅接受通过线路传输的原始（二进制）有效负载数据作为字符串值的块。

由于基于流的性质，发送方可以发送具有不同大小的任何数量的块。无法保证这些接收块与将发送方打算发送的帧完全相同。换句话说，许多较低级别的协议（例如 ``TCP/IP`` ）以块的形式传输数据，这些块可以是单字节值到几十千字节之间的任何值。您可能希望将更高级别的协议应用于这些低级别数据块，以实现正确的消息帧。

end()
^^^^^
``end(mixed $data = null):void`` 方法可用于成功结束流（在可选地发送一些最终数据之后）。

该方法可用于成功结束流，即在发出当前缓冲的所有数据之后关闭流。

.. code-block:: php

	$stream->write('hello');
	$stream->write('world');
	$stream->end();

如果当前没有数据缓冲并且没有要刷新的内容，则此方法可以立即 ``close()`` 流。

如果缓冲区中仍有数据需要先刷新，那么这个方法应该尝试写出这些数据，然后才 ``close()`` 流。关闭流后，它应该发出一个 ``close`` 事件。

请注意，此接口无法控制显式刷新缓冲数据，因为找到适当的时间超出了此接口的范围，并留给此接口的实现。

许多公共流（例如 TCP/IP 连接或基于文件的流）可以选择缓冲所有给定数据，并通过使用基础 ``EventLoop`` 来检查资源实际可写时的未来刷新。

您可以选择在结束流之前传递一些写入流的最终数据。如果将非空值作为 ``$data`` 给出，则此方法的行为就像在没有数据结束之前调用 ``write($data)`` 一样。

.. code-block:: php

	// shorter version
	$stream->end('bye');

	// same as longer version
	$stream->write('bye');
	$stream->end();

调用此方法后，流必须切换到不可写模式，另请参阅 ``isWritable()`` 。这意味着无法进一步写入，因此任何其他 ``write()`` 或 ``end()`` 调用都不起作用。

.. code-block:: php

	$stream->end();
	assert($stream->isWritable() === false);

	$stream->write('nope'); // NO-OP
	$stream->end(); // NO-OP

如果此流是 ``DuplexStreamInterface`` ，则调用此方法也应该结束其可读端，除非流支持半开模式。换句话说，在调用此方法之后，这些流应该切换到不可写和不可读的模式，另请参见 ``isReadable()`` 。这意味着在这种情况下，流应该不再发出任何 ``data`` 或 ``end`` 事件。 流可以选择使用 ``pause()`` 方法逻辑达到该目的，但是可能必须特别小心以确保对 ``resume()`` 方法的后续调用不应该继续发出可读事件。

请注意，不应将此方法与 ``close()`` 方法混淆。

close()
^^^^^^^
``close():void`` 方法可用于关闭流（强制）。

该方法可用于强制关闭流，即关闭流而不等待刷新任何缓冲的数据。如果缓冲区中仍有数据，则应该丢弃该数据。

.. code-block:: php

    $stream->close();

关闭流后，它应该发出一个 ``close`` 事件。请注意，此事件不应多次发出，特别是如果多次调用此方法。

调用此方法后，流必须切换到不可写模式，另请参阅 ``isWritable()`` 。这意味着无法进一步写入，因此任何其它 ``write()`` 或 ``end()`` 调用都不起作用。

.. code-block:: php

	$stream->close();
	assert($stream->isWritable() === false);

	$stream->write('nope'); // NO-OP
	$stream->end(); // NO-OP

请注意，不应将此方法与 ``end()`` 方法混淆。与 ``end()`` 方法不同，此方法不处理任何现有缓冲区，只是丢弃任何缓冲区内容。同样，也可以在调用流上的 ``end()`` 之后调用此方法，以便停止等待流刷新其最终数据。

.. code-block:: php

	$stream->end();
	$loop->addTimer(1.0, function () use ($stream) {
	    $stream->close();
	});

如果此流是 ``DuplexStreamInterface`` ，您还应该注意流的可读边如何实现 ``close()`` 方法。换句话说，在调用此方法之后，流必须切换到不可写且不可读的模式，另请参阅 ``isReadable()`` 。

DuplexStreamInterface
---------------------
``DuplexStreamInterface`` 负责为双工流（可读和可写）提供接口。

它构建在可读写流的现有接口之上，并遵循完全相同的方法和事件语义。如果您对此概念不熟悉，则应首先查看 ``ReadableStreamInterface`` 和 ``WritableStreamInterface`` 。

除了定义一些方法之外，该接口还实现了 ``EventEmitterInterface`` ，它允许您对 ``ReadbleStreamInterface`` 和 ``WritableStreamInterface`` 上定义的相同事件做出反应。

事件回调函数必须是一个有效的可调用函数(callable)，它遵循严格的参数定义，并且必须完全按照文档记录接受事件参数。事件回调函数绝不能抛出异常。事件回调函数的返回值将被忽略且无效，因此出于性能原因，建议您不要返回值。

此接口的每个实现必须遵循这些事件语义，以便被视为行为良好的流。

.. note:: 请注意，此接口的更高级别实现可以选择使用未定义为此低级别流规范的一部分的专用语义来定义其他事件。与这些事件语义超出了此接口的范围，因此您可能还需要参考此类更高级别实现的文档。

创建流
======
``ReactPHP`` 在其整个生态系统中使用“流”的概念，因此该软件包的许多更高级别的消费者只处理流使用。这意味着流实例通常在一些更高级别的组件中创建，并且许多消费者实际上不必处理创建流实例。

- 如果要接收传入或建立传出的纯文本 ``TCP/IP`` 或安全 ``TLS`` 套接字连接流，请使用 ``react/socket`` 。
- 如果要接收传入的 ``HTTP`` 请求正文流，请使用 ``react/http`` 。
- 如果要通过 ``STDIN`` ， ``STDOUT`` ， ``STDERR`` 等进程管道与子进程通信，请使用 ``react/child-process`` 。
- 如果要读取/写入文件系统，请使用实验性的 ``react/filesystem`` 。
- 有关更多实际应用程序的信息，请参见 `最后一章 <https://reactphp.org/stream/#more>`_ 。

但是，如果您正在编写较低级别的组件或想要从流资源创建流实例，那么以下章节适合您。

.. note:: 请注意，以下示例仅使用 ``fopen()`` 和 ``stream_socket_client()`` 进行演示。这些函数不应该用在真正的异步程序中，因为每次调用可能需要几秒钟才能完成，会阻塞 ``EventLoop`` 。此外， ``fopen()`` 调用将在某些平台上返回文件句柄，所有 ``EventLoop`` 实现都可能支持也可能不支持。或者，您可能希望使用上面列出的更高级别的库。

ReadableResourceStream
----------------------
``ReadableResourceStream`` 是 ``PHP`` 的流资源的 ``ReadableStreamInterface`` 的具体实现。

这可用于表示只读资源，如以可读模式打开的文件流或 ``STDIN`` 等流：

.. code-block:: php

	$stream = new ReadableResourceStream(STDIN, $loop);
	$stream->on('data', function ($chunk) {
	    echo $chunk;
	});
	$stream->on('end', function () {
	    echo 'END';
	});

赋予构造函数的第一个参数必须是在读取模式下打开的有效流资源（例如 ``fopen()`` 模式 ``r`` ）。否则，它将抛出 ``InvalidArgumentException`` ：

.. code-block:: php

	// throws InvalidArgumentException
	$stream = new ReadableResourceStream(false, $loop);

在内部，此类尝试在流资源上启用非阻塞模式，这可能不支持所有流资源。最值得注意的是， ``Windows`` 上的管道（ ``STDIN`` 等）不支持此功能。如果失败，它将抛出 ``RuntimeException`` ：

.. code-block:: php

	// throws RuntimeException on Windows
	$stream = new ReadableResourceStream(STDIN, $loop);

使用有效的流资源调用构造函数后，此类将操作底层流资源。您应该只使用其公共 ``API`` ，并且不应手动干扰底层流资源。

此类采用可选的 ``int|null $readChunkSize`` 参数，该参数控制从流中一次读取的最大缓冲区大小（以字节为单位）。您可以在此处使用空值以应用其默认值。除非您知道自己在做什么，否则不应更改此值。这可以是正数，这意味着将从底层流资源一次读取多达 ``X`` 个字节。请注意，如果流资源的当前可用字节数少于 ``X`` 个字节，则读取的实际字节数可能会更低。这可以是 ``-1`` ，这意味着从底层流资源“读取所有可用内容”。这应该读取，直到流资源不再可读（即底层缓冲区耗尽），请注意，这并不一定意味着它达到了 ``EOF`` 。

.. code-block:: php

    $stream = new ReadableResourceStream(STDIN, $loop, 8192);

.. note:: PHP错误警告：如果在没有STDIN流的情况下显式启动了PHP进程，则尝试从STDIN读取可能会返回从另一个流资源来的数据。如果你使用像 ``php test.php < /dev/null`` 而不是 ``php test.php<&-`` 这样的空流来启动它，就不会发生这种情况。有关详细信息，请参阅 `＃81 <https://github.com/reactphp/stream/issues/81>`_ 。

WritableResourceStream
-----------------------
``WritableResourceStream`` 是 ``PHP`` 的流资源的 ``WritableStreamInterface`` 的具体实现。

这可用于表示只写资源，如以可写模式打开的文件流或 ``STDOUT`` 或 ``STDERR`` 等流：

.. code-block:: php

	$stream = new WritableResourceStream(STDOUT, $loop);
	$stream->write('hello!');
	$stream->end();

赋予构造函数的第一个参数必须是为写入而打开的有效流资源。否则，它将抛出 ``InvalidArgumentException`` ：

.. code-block:: php

	// throws InvalidArgumentException
	$stream = new WritableResourceStream(false, $loop);

在内部，此类尝试在流资源上启用非阻塞模式，这可能不支持所有流资源。最值得注意的是， ``Windows`` 上的管道（STDOUT，STDERR等）不支持此功能。如果失败，它将抛出 ``RuntimeException`` ：

.. code-block:: php

	// throws RuntimeException on Windows
	$stream = new WritableResourceStream(STDOUT, $loop);

使用有效的流资源调用构造函数后，此类将操作底层流资源。您应该只使用其公共API，并且不应手动干扰底层流资源。

任何对此类的 ``write()`` 调用都不会立即执行，而是异步执行，直到 ``eventLoop`` 报告流资源准备好接受数据。为此，它使用内存缓冲区字符串来收集所有未完成的写入。此缓冲区应用了一个软限制，它定义了在调用者应该停止发送更多数据之前愿意接受多少数据。

此类采用可选的 ``int|null $writeBufferSoftLimit`` 参数来控制此最大缓冲区大小（以字节为单位）。您可以在此处使用空( ``null`` )值以应用其默认值。除非您知道自己在做什么，否则不应更改此值。

.. code-block:: php

    $stream = new WritableResourceStream(STDOUT, $loop, 8192);

此类采用可选的 ``int|null $writeChunkSize`` 参数，该参数控制此一次写入流的最大缓冲区大小（以字节为单位）。您可以在此处使用空(null)值以应用其默认值。除非您知道自己在做什么，否则不应更改此值。这可以是正数，这意味着最多 ``X`` 个字节将立即写入底层流资源。请注意，如果流资源的当前可用字节数少于 ``X`` 个字节，则写入的实际字节数可能会更低。这可以是 ``-1`` ，这意味着“将所有可用内容写入”基础流资源。

.. code-block:: php

    $stream = new WritableResourceStream(STDOUT, $loop, null, 8192);

DuplexResourceStream
---------------------
``DuplexResourceStream`` 是 ``PHP`` 的流资源的 ``DuplexStreamInterface`` 的具体实现。

这可用于表示读写资源，如以读写模式打开的文件流或 ``TCP/IP`` 连接等流：

.. code-block:: php

	$conn = stream_socket_client('tcp://google.com:80');
	$stream = new DuplexResourceStream($conn, $loop);
	$stream->write('hello!');
	$stream->end();

赋予构造函数的第一个参数必须是为读取和写入而打开的有效流资源。否则，它将抛出 ``InvalidArgumentException`` ：

.. code-block:: php

	// throws InvalidArgumentException
	$stream = new DuplexResourceStream(false, $loop);

在内部，此类尝试在流资源上启用非阻塞模式，这可能不支持所有流资源。最值得注意的是， ``Windows`` 上的管道（STDOUT，STDERR等）不支持此功能。如果失败，它将抛出 ``RuntimeException`` ：

.. code-block:: php

	// throws RuntimeException on Windows
	$stream = new DuplexResourceStream(STDOUT, $loop);

使用有效的流资源调用构造函数后，此类将操作底层流资源。您应该只使用其公共 ``API`` ，并且不应手动干扰底层流资源。

此类采用可选的 ``int|null $readChunkSize`` 参数，该参数控制从流中一次读取的最大缓冲区大小（以字节为单位）。您可以在此处使用空值以应用其默认值。除非您知道自己在做什么，否则不应更改此值。这可以是正数，这意味着将从底层流资源一次读取多达 ``X`` 个字节。请注意，如果流资源的当前可用字节数少于 ``X`` 个字节，则读取的实际字节数可能会更低。这可以是 ``-1`` ，这意味着从基础流资源“读取所有可用内容”。这应该读取，直到流资源不再可读（即底层缓冲区耗尽），请注意，这并不一定意味着它达到了 ``EOF`` 。

.. code-block:: php

	$conn = stream_socket_client('tcp://google.com:80');
	$stream = new DuplexResourceStream($conn, $loop, 8192);

任何对此类的 write() 调用都不会立即执行，而是异步执行，直到 ``eventLoop`` 报告流资源准备好接受数据，。为此，它使用内存缓冲区字符串来收集所有未完成的写入。此缓冲区应用了一个软限制，它定义了在调用者应该停止发送更多数据之前愿意接受多少数据。此类采用另一个可选的 ``WritableStreamInterface|null $buffer`` 参数来控制此流的写入行为。您可以在此处使用空值(null)以应用其默认值。除非您知道自己在做什么，否则不应更改此值。

如果要更改写缓冲区软限制，可以像这样传递 ``WritableResourceStream`` 的实例：

.. code-block:: php

	$conn = stream_socket_client('tcp://google.com:80');
	$buffer = new WritableResourceStream($conn, $loop, 8192);
	$stream = new DuplexResourceStream($conn, $loop, null, $buffer);

ThroughStream
-------------
``ThroughStream`` 实现了 ``DuplexStreamInterface`` ，只是将您写入的任何数据传递给它的可读端。

.. code-block:: php

	$through = new ThroughStream();
	$through->on('data', $this->expectCallableOnceWith('hello'));

	$through->write('hello');

类似地， ``end()`` 方法将结束流并发出 ``end`` 事件，然后 ``close()`` 流。 ``close()`` 方法将关闭流并发出 ``close`` 事件。因此，这也可以在 ``pipe()`` 上下文中使用，如下所示：

.. code-block:: php

	$through = new ThroughStream();
	$source->pipe($through)->pipe($dest);

可选地，它的构造函数接受任何可调用的函数，然后该函数将用于过滤写入它的任何数据。此函数接收传递给可写端的单个数据参数，并且必须返回数据，因为它将传递给可读端：

.. code-block:: php

	$through = new ThroughStream('strtoupper');
	$source->pipe($through)->pipe($dest);

请注意，此类不对任何数据类型进行任何假设。这可用于转换数据，例如将任何结构化数据转换为换行符分隔的 ``JSON`` （NDJSON）流，如下所示：

.. code-block:: php

	$through = new ThroughStream(function ($data) {
	    return json_encode($data) . PHP_EOL;
	});
	$through->on('data', $this->expectCallableOnceWith("[2, true]\n")); // 监听读事件

	$through->write(array(2, true));

允许回调函数抛出异常。在这种情况下，流将发出 ``error`` 事件，然后 ``close()`` 流。

.. code-block:: php

	$through = new ThroughStream(function ($data) {
	    if (!is_string($data)) {
	        throw new \UnexpectedValueException('Only strings allowed');
	    }
	    return $data;
	});
	$through->on('error', $this->expectCallableOnce()));
	$through->on('close', $this->expectCallableOnce()));
	$through->on('data', $this->expectCallableNever()));

	$through->write(2);

CompositeStream
----------------
``CompositeStream`` 实现 ``DuplexStreamInterface`` ，可用于分别从实现 ``ReadableStreamInterface`` 和 ``WritableStreamInterface`` 的两个单独的流创建单个双工流。

这对于某些可能需要单个 ``DuplexStreamInterface`` 的API非常有用，或者仅仅因为使用这样的单个流实例通常更方便：

.. code-block:: php

	$stdin = new ReadableResourceStream(STDIN, $loop);
	$stdout = new WritableResourceStream(STDOUT, $loop);

	$stdio = new CompositeStream($stdin, $stdout);

	$stdio->on('data', function ($chunk) use ($stdio) {
	    $stdio->write('You said: ' . $chunk);
	});

这是一个表现良好的流，它转发来自底层流所有流事件，并将所有流调用转发到底层流。

如果你 ``write()`` 到双工流，它只会 ``write()`` 到可写端并返回其状态。

如果你 ``end()`` 双工流，它将 ``end()`` 可写侧并 ``pause()`` 可读侧。

如果 ``close()`` 双工流，则两个输入流都将关闭。如果两个输入流中的任何一个发出 ``close`` 事件，则双工流也将关闭。如果两个输入流中的任何一个在构造双工流时已经关闭，它将 ``close()`` 另一侧并返回一个封闭的流。

安装
====

.. code-block:: php

    $ composer require react/stream:^1.0

测试
====
要运行测试套件，首先需要克隆此repo，然后通过 `Composer <https://getcomposer.org/>`_ 安装所有依赖项：

.. code-block:: shell

    $ composer install

要运行测试套件，请转到项目根目录并运行：

.. code-block:: shell

    $ php vendor/bin/phpunit

测试套件还包含许多依赖稳定互联网连接的功能集成测试。如果你不想运行它们，可以像这样简单地跳过它们：

.. code-block:: shell

    $ php vendor/bin/phpunit --exclude-group internet


更多
====

- 有关如何在实际应用程序中创建流的更多信息，请参阅 `创建流 <https://reactphp.org/stream/#creating-streams>`_ 。
- 有关在实际应用程序中使用流的软件包列表，请参阅我们的 `用户wiki <https://github.com/reactphp/react/wiki/Users>`_ 和 `Packagist上的依赖项 <https://packagist.org/packages/react/stream/dependents>`_ 。


