************
ChildProcess
************

事件驱动的库，用于使用 ``ReactPHP`` 执行子进程。

该库将 `Program Execution <http://php.net/manual/en/book.exec.php>`_ 与 ``EventLoop`` 集成在一起。启动的子进程可能会发出信号，并在终止时发出 ``exit`` 事件。另外，进程 ``I/O`` 流（即 ``STDIN`` ， ``STDOUT`` ， ``STDERR`` ）作为 ``Streams`` 暴露。

快速例子
========

.. code-block:: php

	$loop = React\EventLoop\Factory::create();

	$process = new React\ChildProcess\Process('echo foo');
	$process->start($loop);

	$process->stdout->on('data', function ($chunk) {
	    echo $chunk;
	});

	$process->on('exit', function($exitCode, $termSignal) {
	    echo 'Process exited with code ' . $exitCode . PHP_EOL;
	});

	$loop->run();

`更多例子 <https://github.com/reactphp/child-process/blob/v0.6.1/examples>`_

进程
====

流属性
------
一旦进程启动，其 ``I/O`` 流将被构造为 ``React\Stream\ReadableStreamInterface`` 和 ``React\Stream\WritableStreamInterface`` 的实例。在调用 ``start()`` 之前，不会设置这些属性。一旦进程终止，流将关闭但不会被 ``unset`` 。

遵循 ``Unix`` 的一般约定，此库将使用与默认情况下给出的标准 ``I/O`` 流匹配的三个管道启动每个子进程。您可以将命名引用用于常见用例，或者将这些引用作为包含所有三个管道的数组进行访问。

- ``$stdin`` 或者 ``$pipes[0]`` 是一个 ``WritableStreamInterface``
- ``$stdout`` 或者 ``$pipes[1]`` 是一个 ``ReadableStreamInterface``
- ``$stderr`` 或者 ``$pipes[2]`` 是一个 ``ReadableStreamInterface``

请注意，可以通过显式传递自定义管道来覆盖此默认配置，在这种情况下，它们可能不会设置或为其分配不同的值。特别要注意的是， ``Windows`` 支持受到限制，因为它不支持非阻塞 ``STDIO`` 管道。 ``$pipes`` 数组将始终包含对已配置的所有管道的引用，并且标准 ``I/O`` 引用将始终设置为引用与上述约定匹配的管道。有关详细信息，请参阅自定义管道。

因为每个都实现了底层的 ``ReadableStreamInterface`` 或 ``WritableStreamInterface`` ，所以您可以像往常一样使用它们的任何事件和方法：

.. code-block:: php

	$process = new Process($command);
	$process->start($loop);

	$process->stdout->on('data', function ($chunk) {
	    echo $chunk;
	});

	$process->stdout->on('end', function () {
	    echo 'ended';
	});

	$process->stdout->on('error', function (Exception $e) {
	    echo 'error: ' . $e->getMessage();
	});

	$process->stdout->on('close', function () {
	    echo 'closed';
	});

	$process->stdin->write($data);
	$process->stdin->end($data = null);
	// …


命令
----
``Process`` 类允许您传递任何类型的命令行字符串：

.. code-block:: php

	$process = new Process('echo test');
	$process->start($loop);

命令行字符串通常由一个以空格分隔的列表组成，其中包含主可执行 ``bin`` 和任意数量的参数。应特别注意转义或引号包含任何参数，特别是如果您传递任何用户输入。同样，请记住，特别是在 ``Windows`` 上，路径名包含空格和其他特殊字符是很常见的。如果你想运行这样的二进制文件，你必须确保使用 ``escapeshellarg()`` 将它作为单个参数引用，如下所示：

.. code-block:: php

	$bin = 'C:\\Program files (x86)\\PHP\\php.exe';
	$file = 'C:\\Users\\me\\Desktop\\Application\\main.php';

	$process = new Process(escapeshellarg($bin) . ' ' . escapeshellarg($file));
	$process->start($loop);

默认情况下， ``PHP`` 将通过在 ``Unix`` 上的 ``sh`` 命令中包装给定的命令行字符串来启动进程，因此第一个示例将在 ``Unix`` 下实际执行 ``sh -c echo test`` 。在 ``Windows`` 上，它不会通过将它们包装在 ``shell`` 中来启动进程。

这是一个非常有用的功能，因为它不仅允许您传递单个命令，而且实际上允许您传递任何类型的 ``shell`` 命令行并使用命令链（使用 ``&&`` ， ``||``  ， ``;`` 等）启动多个子命令并允许您重定向 ``STDIO`` 流（使用 ``2>&1`` 和 ``family`` ）。这可用于传递完整的命令行并从包装 ``shell`` 命令接收生成的 ``STDIO`` 流，如下所示：

.. code-block:: php

	$process = new Process('echo run && demo || echo failed');
	$process->start($loop);

.. note:: 请注意， `Windows 支持 <https://reactphp.org/child-process/#windows-compatibility>`_ 受到限制，因为它根本不支持 ``STDIO`` 流，并且默认情况下，这些进程不会在包装 ``shell`` 中运行。如果要运行 ``shell`` 内置函数（如 ``echo hello`` 或 ``sleep 10`` ），则可能必须在命令行前添加一个显式 ``shell`` ，如 ``cmd / c echo hello`` 。

换句话说，底层 ``shell`` 负责管理此命令行并启动各个子命令并根据需要连接其 ``STDIO`` 流。这意味着 ``Process`` 类只接收来自包装 ``shell`` 的结果 ``STDIO`` 流，因此它将包含完整的输入/输出，无法识别单个子命令的输入/输出。

如果要识别单个子命令的输出，您可能希望实现一些更高级别的协议逻辑，例如在每个子命令之间打印显式边界，如下所示：

.. code-block:: php

	$process = new Process('cat first && echo --- && cat second');
	$process->start($loop);

作为替代方案，考虑一次启动一个进程并监听其 ``exit`` 事件以有条件地方式启动链中的下一个进程。这将为您提供配置后续进程 ``I/O`` 流的机会：

.. code-block:: php

	$first = new Process('cat first');
	$first->start($loop);

	$first->on('exit', function () use ($loop) {
	    $second = new Process('cat second');
	    $second->start($loop);
	});

请记住， ``PHP`` 在 ``Unix`` 上为所有命令行使用 ``shell`` 包装器。虽然这对于更复杂的命令行似乎是合理的，但这实际上也适用于运行最简单的单个命令：

.. code-block:: php

	$process = new Process('yes');
	$process->start($loop);

这实际上会在 ``Unix`` 上产生类似于此的命令层次结构：

.. code-block:: shell

	5480 … \_ php example.php
	5481 …    \_ sh -c yes
	5482 …        \_ yes

这意味着尝试获取底层进程 PID 或发送信号实际上将以包装 ``shell`` 为目标，即获取包装 ``shell PID`` ，这在许多情况下可能不是所需的结果。

如果您不希望显示此包装 ``shell`` 进程，则可以在 ``Unix`` 平台上使用 ``exec`` 前置命令字符串，这将导致包装 ``shell`` 进程被我们的进程替换：

.. code-block:: php

	$process = new Process('exec yes');
	$process->start($loop);

这将显示与此类似的结果命令层次结构：

.. code-block:: shell

	5480 … \_ php example.php
	5481 …    \_ yes

这意味着尝试获取底层进程 ``PID`` 并发送信号现在将按预期目标指向实际命令。

请注意，在这种情况下，命令行不会在包装 ``shell`` 中运行。这意味着当使用 ``exec`` 时，无法传递包含命令链或重定向 ``STDIO`` 流的命令行。

根据经验，大多数命令很可能与包装外壳一起运行。如果你传递一个完整的命令行（或不确定），你应该最有可能保留包装 ``shell`` 。如果您在 ``Unix`` 上运行并且只想传递单个命令，那么您可能需要考虑在命令字符串之前添加 ``exec`` 以避免包装 ``shell`` 。

结束
----
只要进程不再运行，就会发出 ``exit`` 事件。事件侦听器将接收退出代码和终止信号作为两个参数：

.. code-block:: php

	$process = new Process('sleep 10');
	$process->start($loop);

	$process->on('exit', function ($code, $term) {
	    if ($term === null) {
	        echo 'exit with code ' . $code . PHP_EOL;
	    } else {
	        echo 'terminated with signal ' . $term . PHP_EOL;
	    }
	});

请注意，如果进程已终止， ``$code`` 为 ``null`` ，但无法确定退出代码（例如，禁用了 ``sigchild`` 兼容性）。类似地， ``$term`` 为 ``null`` ，除非进程已终止以响应发送给它的未捕获信号。这不是这个项目的限制，而是实际如何在 ``POSIX`` 系统上公开退出代码和信号，更多细节也请参见此处。




自定义管道
----------

Sigchld兼容性
-------------


Windows兼容性
-------------


安装
====


测试
====



