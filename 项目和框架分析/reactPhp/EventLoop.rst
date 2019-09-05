*********
EventLoop
*********

.. contents:: 目录
   :depth: 4

介绍
====

``ReactPHP`` 的核心事件循环库，可用于事件型 ``I/O`` 。

为了使基于异步的库可以互相调用，它们需要使用相同的事件循环。该组件提供了任何库可以使用的通用 ``LoopInterface`` 。 这允许它们在同一个循环中使用，由控制的用户调用一次 ``run()`` 方法。

入门示例
========
下面是一个仅使用事件循环构建的异步 ``HTTP`` 服务器的例子。

.. code-block:: php

	$loop = React\EventLoop\Factory::create(); // 创建一个循环实例

	$server = stream_socket_server('tcp://127.0.0.1:8080'); // 创建一个服务器socket，返回创建的流
	stream_set_blocking($server, false); // 设置该流为非阻塞

	// 注册流的读事件到事件循环中
	$loop->addReadStream($server, function ($server) use ($loop) {
	    $conn = stream_socket_accept($server); // 接收一个连接，返回一个连接流
	    $data = "HTTP/1.1 200 OK\r\nContent-Length: 3\r\n\r\nHi\n"; // 响应数据
	    // 注册流的写事件到事件循环中
	    $loop->addWriteStream($conn, function ($conn) use (&$data, $loop) {
	        $written = fwrite($conn, $data); // 写入数据到流中，因为非阻塞，所以不能保证一次能够写入所有的数据
	        if ($written === strlen($data)) { // 如果数据已经完全写入，则关闭连接流
	            fclose($conn);
	            $loop->removeWriteStream($conn); // 从事件循环中删除连接流写事件
	        } else { // 清除已经写入的数据
	            $data = substr($data, $written);
	        }
	    });
	});
	// 添加周期定时器来统计当前脚本使用的内存
	$loop->addPeriodicTimer(5, function () {
	    $memory = memory_get_usage() / 1024; // 统计分配给脚本的内存
	    $formatted = number_format($memory, 3).'K';
	    echo "Current memory usage: {$formatted}\n";
	});

	$loop->run(); //开始事件循环

查看更多的 `示例 <https://github.com/reactphp/event-loop/tree/v1.0.0/examples>`_

使用
====
典型的程序是使用单个事件循环，该事件循环在程序开始时创建并在程序结束时运行。

.. code-block:: php

	// [1]
	$loop = React\EventLoop\Factory::create();

	// [2]
	$loop->addPeriodicTimer(1, function () {
	    echo "Tick\n";
	});

	$stream = new React\Stream\ReadableResourceStream(
	    fopen('file.txt', 'r'),
	    $loop
	);

	// [3]
	$loop->run();

1. 在程序开始时创建循环实例。 本库提供了一个简单易于使用的工厂 ``React\EventLoop\Factory::create()`` ，它选择并实现了最好的事件循环。

2. 直接使用循环实例或者传递给库和应用程序代码。在此示例中，周期性定时器向事件循环注册，该事件循环每秒仅输出 ``Tick`` ，并且使用 ``ReactPHP`` 的流组件创建可读流以用于演示目的。

3. 在程序结束时使用单个 ``$loop->run()`` 调用运行循环。

Factory
-------
``Factory`` 类作为选择最佳可用事件循环实现的便捷方式而存在。

create()
^^^^^^^^
``create(): LoopInterface`` 方法用于创建一个新的事件循环实例

.. code-block:: php

    $loop = React\EventLoop\Factory::create();

此方法始终返回实现 ``LoopInterface`` 的实例，实际的实现细节参见下面事件循环实现 。

通常只应在程序开始时调用此方法一次。

事件循环实现
-----------

除了 ``LoopInterface`` 之外，还提供了许多的事件循环实现。

所有事件循环都支持以下功能：

- File descriptor polling（文件描述符轮询）
- One-off timers（一次性定时器）
- Periodic timers（周期定时器）
- Deferred execution on future loop tick（延迟到将来的某个 ``loop tick`` 时执行）

对于大多数使用者来说，底层的事件循环实现只是实现细节而已。你只需要使用 ``Factory`` 来自动创建新实例即可。

高级用法！如果你明确需要某个事件循环实现，则可以手动实例化以下类之一。请注意，你可能必须首先为对应的事件循环实现安装所需的 ``PHP`` 扩展，否则将在创建时抛出 ``BadMethodCallException`` 异常。

StreamSelectLoop
^^^^^^^^^^^^^^^^
基于 ``stream_select`` 的事件循环

这使用了 ``stream_select()`` 函数，是唯一可以与 ``PHP`` 一起开箱即用的实现。

这个事件循环在 ``PHP 5.3`` 到 ``PHP 7`` 和 ``HHVM`` 上开箱即用。 这意味着无需安装，此库适用于所有平台和受支持的 ``PHP`` 版本。 因此，如果你不安装下面列出的任何事件循环扩展， ``Factory`` 将默认使用此事件循环。

在底层，做了简单的额 ``select`` 系统调用。此系统调用限于以 ``O(m)`` （ ``m`` 是传递的最大文件描述符编号）和 ``FD_SETSIZE`` 的最大文件描述符编号（平台相关，通常为 ``1024`` ）之间。这意味着在并发处理数千个流时可能会遇到问题，您可能希望在这种情况下使用下面列出的备用事件循环实现之一。如果您的用例是涉及一次只处理几十或几百个流的许多常见用例之一，那么此事件循环实现执行得非常好。

如果你想使用信号处理（参见下面的 ``addSignal()`` ），这个事件循环实现需要 ``ext-pcntl`` 。此扩展仅适用于类 ``Unix`` 平台，不支持 ``Windows`` 。它通常作为许多 ``PHP`` 发行版的一部分安装。如果缺少此扩展（或者您在 ``Windows`` 上运行），则不支持信号处理，而是抛出 ``BadMethodCallException`` 。

在使用PHP 7.3之前任何版本时此事件循环依赖于挂钟时间来调度未来的定时器，因为单调时间源仅在PHP 7.3（ ``hrtime()`` ）中可用。虽然这不会影响许多常见用例，但这对于依赖高时间精度的程序或受不连续时间调整（时间跳跃）的系统来说是一个重要的区别。这意味着如果您计划在 PHP <7.3 时在30秒内触发计时器，然后将系统时间向前调整20秒，则计时器可能会在10秒内触发。有关更多详细信息，另请参见 ``addTimer()`` 。

ExtEventLoop
^^^^^^^^^^^^
基于 ``ext-event`` 的事件循环。

使用 `PECL event <https://pecl.php.net/package/event>`_ 扩展，它支持像 ``libevent`` 一样的后端。

从 ``PHP 5.4`` 到 ``PHP 7+`` 都能和此循环一起使用。

ExtEvLoop
^^^^^^^^^^
基于 ``ext-ev`` 的事件循环。

使用 `PECL ev <https://pecl.php.net/package/ev>`_ 扩展，它为 ``libev`` 库提供了一个接口。

从 ``PHP 5.4`` 到 ``PHP 7+`` 都能和此循环一起使用。

ExtUvLoop
^^^^^^^^^^
基于 ``ext-uv`` 的事件循环。

使用 `PECL uv <https://pecl.php.net/package/uv>`_ 扩展，它为 ``libuv`` 库提供一个接口。

在 ``PHP 7+`` 中可以一起使用。

ExtLibeventLoop
^^^^^^^^^^^^^^^
基于 ``ext-libevent`` 的事件循环。

使用 `PECL libevent <https://pecl.php.net/package/libevent>`_ 扩展， ``libevent`` 本身支持许多特定于系统的后端（ ``epoll`` ， ``kqueue`` ）。

此事件循环仅适用于 ``PHP 5`` 。 ``PHP 7`` 的 `非官方更新 <https://github.com/php/pecl-event-libevent/pull/2>`_ 确实存在，但已知由于 ``SEGFAULT`` 导致定期崩溃。重申：不建议在 ``PHP 7`` 上使用此事件循环。因此， ``Factory`` 不会尝试在 ``PHP 7`` 上使用此事件循环。

已知此事件循环仅在流变为可读（边缘触发）时触发可读侦听器，并且如果流开始就可读，则可能不触发。这也意味着当数据仍留在 ``PHP`` 的内部流缓冲区中时，流可能无法识别为可读。因此，建议使用 ``stream_set_read_buffer($stream，0);`` 在这种情况下禁用 ``PHP`` 的内部读缓冲区。有关更多详细信息，另请参见 ``addReadStream()`` 。

ExtLibevLoop
^^^^^^^^^^^^
基于 ``ext-libev`` 的事件循环。

这使用 `非官方的 libev 扩展 <https://github.com/m4rw3r/php-libev>`_ ，它支持像 ``libevent`` 一样的后端。

此循环仅适用于 ``PHP 5`` 。不太可能会很快支持 ``PHP7`` 。

LoopInterface
-------------

run()
^^^^^
``run(): void`` 方法用于运行事件循环，直到没有任务执行。

对于许多应用程序，此方法是事件循环上唯一直接可见的调用。 根据经验，通常建议将所有内容附加到同一个循环实例上，然后在程序的底端运行一次循环。

.. code-block:: php

    $loop->run();

此方法将会保持循环一直运行下去，直到没任务执行。换句话说，，此方法将阻塞直到最后一个定时器、流或 信号被删除。

同样，必须确保应用程序实际只调用此方法一次。 将侦听器添加到循环中但是没有运行它时，将导致应用程序不等待任何其他附加的侦听器就退出了。

循环已经运行时，不得调用此方法。但在显示调用 ``stop()`` 之后或者在它自动停止之后，则可以多次调用此方法，因为它已经不再有任何操作了。

stop()
^^^^^^
``stop(): void`` 方法用于停止正在运行的事件循环。

此方法被视为高级用法，应谨慎使用。根据经验，通常建议在不再有任何操作时让循环自动停止。

此方法可用于显式停止事件循环：

.. code-block:: php

	$loop->addTimer(3.0, function () use ($loop) {
	    $loop->stop();
	});

在当前未运行的循环实例或已停止的循环实例上调用此方法时是无效的。

addTimer()
^^^^^^^^^^^
该 ``addTimer(float $interval, callable $callback):TimerInterface`` 方法用于将回调函数入队，以便在给定的时间间隔之后被调用一次。

此方法会返回定时器实例，定时器回调函数必须能够接受单个参数或者无参的函数。

定时器回调函数绝不能抛出 ``Exception`` 且没有返回值。出于性能原因，建议不要返回任何值，否则定时器回调函数的返回值将被忽略且无效。

与 ``addPeriodicTimer()`` 不同，此方法将确保在给定间隔后仅调用一次回调函数。你可以调用 ``cancelTimer`` 取消一个处于 ``pending`` 的定时器 。

.. code-block:: php

	$loop->addTimer(0.8, function () {
	    echo 'world!' . PHP_EOL;
	});

	$loop->addTimer(0.3, function () {
	    echo 'hello ';
	});

另见 `example #1 <https://github.com/reactphp/event-loop/blob/v1.0.0/examples>`_ 。

如果你想要在回调函数中要访问任何变量，可以将任意数据绑定到回调闭包中，如下所示：

.. code-block:: php

	function hello($name, LoopInterface $loop)
	{
	    $loop->addTimer(1.0, function () use ($name) {
	        echo "hello $name\n";
	    });
	}

	hello('Tester', $loop);

此接口不会强制任何特定级别的定时器精度，因此如果你依赖于毫秒或更低的高级别精度，则可能需要特别小心。事件循环实现应该尽力工作，除非另有说明，否则应该提供至少毫秒级别的精度。已知现有的很多事件循环实现提供微秒级别的精度，但通常不建议依赖这种高精度。

同样的，不保证在同一时间（在其可能的精度内）定时器的执行顺序。

此接口建议事件循环实现应该使用单调时间源（如果可用）。鉴于默认情况下 ``PHP7.3`` 之前版本上没有单调时间源，事件循环实现可能会回退到使用系统时间。虽然许多常见用例不会受影响，但这对于依赖高精度时间的程序或者时间调整不连续（time jumps）的系统来说是一个重要的区别。这意味着如果你计划在30秒内触发定时器，然后将系统时间向前调整20秒，则定时器应该仍然在30秒内触发。

addPeriodicTimer()
^^^^^^^^^^^^^^^^^^
该 ``addPeriodicTimer(float $interval, callable $callback):TimerInterface`` 方法用于将回调函数入队，以在给定的时间间隔之后被重复调用。

此方法会返回定时器实例，定时器回调函数必须能够接受单个参数，或者你可以使用无参的函数。

定时器回调函数绝不能抛出 ``Exception`` 。出于性能原因，建议不要返回任何值，否则定时器回调函数的返回值将被忽略且无效。

与 ``addTimer()`` 不同，此方法将确保在给定的时间间隔之后无限地调用回调函数，或者直到你调用 ``cancelTimer`` 为止。

.. code-block:: php

	$timer = $loop->addPeriodicTimer(0.1, function () {
	    echo 'tick!' . PHP_EOL;
	});

	$loop->addTimer(1.0, function () use ($loop, $timer) {
	    $loop->cancelTimer($timer);
	    echo 'Done' . PHP_EOL;
	});

另见 `示例＃2 <https://github.com/reactphp/event-loop/blob/v1.0.0/examples>`_ 。

如果要限制执行次数，可以将任意数据绑定到回调闭包中，如下所示：

.. code-block:: php

	function hello($name, LoopInterface $loop)
	{
	    $n = 3;
	    $loop->addPeriodicTimer(1.0, function ($timer) use ($name, $loop, &$n) {
	        if ($n > 0) {
	            --$n;
	            echo "hello $name\n";
	        } else {
	            $loop->cancelTimer($timer);
	        }
	    });
	}

	hello('Tester', $loop);

此接口不会强制任何特定级别的定时器精度，因此如果你依赖于毫秒或更低的高级别精度，则可能需要特别小心。事件循环实现应该尽力工作，除非另有说明，否则应该提供至少毫秒级别的精度。已知现有的很多事件循环实现提供微秒级别的精度，但通常不建议依赖这种高精度。

同样的，不保证在同一时间（在其可能的精度内）定时器的执行顺序。

此接口建议事件循环实现应该使用单调时间源（如果可用）。鉴于默认情况下 ``PHP7.3`` 之前版本上没有单调时间源，事件循环实现可能会回退到使用系统时间。虽然许多常见用例不会受影响，但这对于依赖高精度时间的程序或者时间调整不连续（time jumps）的系统来说是一个重要的区别。这意味着如果你计划在30秒内触发定时器，然后将系统时间向前调整20秒，则定时器应该仍然在30秒内触发。

另外，由于在每次调用之后重新调度，周期性定时器可能经历定时器漂移（timer drift）。因此，通常不建议将其依赖于毫秒或更小的高精度时间间隔。

cancelTimer()
^^^^^^^^^^^^^
该 ``cancelTimer(TimerInterface $timer):void`` 方法可用于取消一个处于 ``pending`` 的定时器。

另见 ``addPeriodicTimer()`` 和 `例子＃2 <https://github.com/reactphp/event-loop/blob/v1.0.0/examples>`_ 。

在尚未添加到此循环实例中或已被取消过的定时器实例上调用此方法无效。

futureTick()
^^^^^^^^^^^^
该 ``futureTick(callable $listener):void`` 方法用于在事件循环的将来某个 ``tick`` 上调用回调函数。

这与时间间隔为 ``0`` 秒的定时器非常相似，但这个不需要调度定时器队列的开销。

``tick`` 回调函数必须能够接受无参。

``tick`` 回调函数绝对不能抛出 ``Exception`` 。出于性能原因，建议不要返回任何值，否则 ``tick`` 回调函数的返回值将被忽略且无效。

如果你想要在回调函数中要访问任何变量，可以将任意数据绑定到回调闭包中，如下所示：

.. code-block:: php

	function hello($name, LoopInterface $loop)
	{
	    $loop->futureTick(function () use ($name) {
	        echo "hello $name\n";
	    });
	}

	hello('Tester', $loop);

与定时器不同， ``tick`` 回调保证按入队顺序执行。此外，一旦回调入函数队，就无法取消此操作。

这通常用于将较大的任务分解为更小的步骤（一种协作式多任务处理）。

.. code-block:: php

	$loop->futureTick(function () {
	    echo 'b';
	});
	$loop->futureTick(function () {
	    echo 'c';
	});
	echo 'a';

另见 `示例＃3 <https://github.com/reactphp/event-loop/blob/v1.0.0/examples>`_ 。

addSignal()
^^^^^^^^^^^^
该 ``addSignal(int $signal, callable $listener):void`` 方法用于注册当捕获到信号时要通知的侦听器。

这对于捕获来自 ``supervisor`` 或 ``systemd`` 等工具的用户中断信号或关闭信号非常有用 。

通过此方法添加的信号，监听器回调函数必须能够接受单个参数，或者你可以使用无参的函数。

监听器回调函数绝不能抛出 ``Exception`` 。出于性能原因，建议你不要返回任何值，否则监听器回调函数的返回值将被忽略且无效。

.. code-block:: php

	$loop->addSignal(SIGINT, function (int $signal) {
	    echo 'Caught user interrupt signal' . PHP_EOL;
	});

另见 `示例＃4 <https://github.com/reactphp/event-loop/blob/v1.0.0/examples>`_ 。

``Signaling`` 仅在类 ``Unix`` 平台上可用，由于操作系统限制，不支持 ``Windows`` 。如果此平台不支持信号，例如，当缺少必要的扩展时，此方法可能会抛出 ``BadMethodCallException`` 。

注意：一个侦听器只能在同一个信号上添加一次，任何多次的添加操作都将被忽略。

removeSignal()
^^^^^^^^^^^^^^
该 ``removeSignal(int $signal, callable $listener):void`` 方法用于移除先前添加的信号监听器。

.. code-block:: php

    $loop->removeSignal(SIGINT, $listener);

任何删除未注册的侦听器的操作都将被忽略。

addReadStream()
^^^^^^^^^^^^^^^^
.. note:: 高级！请注意，此低级API被视为高级用法。大多数用例应该使用更高级别的 readable Stream API 。

该 ``addReadStream(resource $stream, callable $callback):void`` 方法用于注册在 ``stream`` 流准备好读取时要通知的监听器。

第一个参数必须是一个有效且支持循环实现检查它是否已准备好读取的流资源。单个流资源不得添加多次。相反，要么 ``removeReadStream()`` 先调用，要么使用单个侦听器对此事件做出反应，然后从这个侦听器调度。如果此循环实现不支持给定的资源类型，则此方法可以会抛出一个 ``Exception`` 。

通过此方法添加的流资源，监听器回调函数必须能够接受单个参数，或者你可以使用无参的函数。

监听器回调函数绝不能抛出 ``Exception`` 。出于性能原因，建议你不要返回任何值，否则监听器回调函数的返回值将被忽略且无效。

如果你想要在回调函数中要访问任何变量，可以将任意数据绑定到回调闭包中，如下所示：

.. code-block:: php

	$loop->addReadStream($stream, function ($stream) use ($name) {
	    echo $name . ' said: ' . fread($stream);
	});

另见 `示例＃11 <https://github.com/reactphp/event-loop/blob/v1.0.0/examples>`_ 。

你可以调用 ``removeReadStream()`` 以删除此流的读取事件侦听器。

当多个流同时准备好时，不能保证监听器的执行顺序。

一些众所周知的事件循环的实现，如果流变为可读的（边沿触发），并且如果流一开始就已经是可读的则可能不会触发。这也意味着当数据仍留在 ``PHP`` 的内部流缓冲区中时，流可能无法识别为可读。因此，在这种情况下，建议使用 ``stream_set_read_buffer($stream, 0);`` 禁用 ``PHP`` 的内部读缓冲区。

addWriteStream()
^^^^^^^^^^^^^^^^
.. note:: 高级用法！请注意，此底层API被视为高级用法。大多数用例应该使用更高级别的 writable Stream API 。

该 ``addWriteStream(resource $stream, callable $callback):void`` 方法用于注册在 ``stream`` 流准备好写入时要通知的侦听器。

第一个参数必须是一个有效且支持循环实现检查它是否已准备好写的流资源。单个流资源不得添加多次。相反，要么 ``removeWriteStream()`` 先调用，要么使用单个侦听器对此事件做出反应，然后从这个侦听器调度。如果此循环实现不支持给定的资源类型，则此方法可以会抛出一个 ``Exception`` 。

通过此方法添加的流资源，监听器回调函数必须能够接受单个参数，或者你可以使用无参的函数。

监听器回调函数绝不能抛出 ``Exception`` 。出于性能原因，建议你不要返回任何过多的数据结构，否则监听器回调函数的返回值将被忽略且无效。

如果你想要在回调函数中要访问任何变量，可以将任意数据绑定到回调闭包中，如下所示：

.. code-block:: php

	$loop->addWriteStream($stream, function ($stream) use ($name) {
	    fwrite($stream, 'Hello ' . $name);
	});

另见 `示例＃12 <https://github.com/reactphp/event-loop/blob/v1.0.0/examples>`_ 。

你可以调用 ``removeWriteStream()`` 以删除此流的写事件侦听器。

当多个流同时准备好时，不能保证监听器的执行顺序。

removeReadStream()
^^^^^^^^^^^^^^^^^^
该 ``removeReadStream(resource $stream):void`` 方法可用于删除给定流的 ``Read`` 事件侦听器。

从已删除的循环中删除流或尝试删除从未添加或无效的流无效。

removeWriteStream()
^^^^^^^^^^^^^^^^^^^^
该 ``removeWriteStream(resource $stream):void`` 方法可用于删除给定流的 ``write`` 事件的侦听器。

从已删除的循环中删除流或尝试删除从未添加或无效的流无效。

安装
====
安装本库的推荐方法是通过 ``Composer`` 安装。

该项目遵循 ``SemVer`` ，这将安装支持的最新版本：

.. code-block:: shell

    $ composer require react/event-loop:^1.0

有关版本升级的详细信息，另请参阅 CHANGELOG 。

该项目旨在在任何平台上运行，因此不需要任何 ``PHP`` 扩展，并支持通过当前的 ``PHP 7+`` 和 ``HHVM`` 上在旧版 ``PHP 5.3`` 上运行。强烈建议在此项目中使用 ``PHP 7+`` 。

建议安装所有事件循环扩展，虽然完全是可选的。有关更多详细信息，请参阅事件循环实现 。

测试
====
要运行测试套件，首先需要 ``clone`` 此仓库，然后通过 ``Composer`` 安装所有的依赖项：

.. code-block:: shell

    $ composer install

要运行测试套件，请切换到项目根目录并运行：

.. code-block:: shell

    $ php vendor/bin/phpunit

更多
====

- 有关如何在实际应用中使用 ``streams`` 流，请参阅我们的 `Stream组件 <https://reactphp.org/stream/>`_ 。
- 有关在实际应用中使用 ``EventLoop`` 的软件包列表，请参阅我们的 `用户wiki <https://github.com/reactphp/react/wiki/Users>`_ 和 `dependents on Packagist <https://packagist.org/packages/react/event-loop/dependents>`_ 。
