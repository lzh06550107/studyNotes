*********
process对象
*********

``process`` 对象是 ``Node`` 的一个全局对象，提供当前 ``Node`` 进程的信息。它可以在脚本的任意位置使用，不必通过 ``require`` 命令加载。该对象部署了 ``EventEmitter`` 接口。

属性
====
``process`` 对象提供一系列属性，用于返回系统信息。

- ``process.argv`` ：返回一个数组，成员是当前进程的所有命令行参数。
- ``process.env`` ：返回一个对象，成员为当前 ``Shell`` 的环境变量，比如 ``process.env.HOME`` 。
- ``process.installPrefix`` ：返回一个字符串，表示 ``Node`` 安装路径的前缀，比如 ``/usr/local`` 。相应地， ``Node`` 的执行文件目录为 ``/usr/local/bin/node`` 。
- ``process.pid`` ：返回一个数字，表示当前进程的进程号。
- ``process.platform`` ：返回一个字符串，表示当前的操作系统，比如 ``Linux`` 。
- ``process.title`` ：返回一个字符串，默认值为 ``node`` ，可以自定义该值。
- ``process.version`` ：返回一个字符串，表示当前使用的 ``Node`` 版本，比如 ``v7.10.0`` 。

``process`` 对象还有一些属性，用来指向 ``Shell`` 提供的接口。

process.stdout
--------------
``process.stdout`` 属性返回一个对象，表示标准输出。该对象的 ``write`` 方法等同于 ``console.log`` ，可用在标准输出向用户显示内容。

.. code-block:: js

	console.log = function(d) {
	  process.stdout.write(d + '\n');
	};

下面代码表示将一个文件导向标准输出。

.. code-block:: js

	var fs = require('fs');

	fs.createReadStream('wow.txt')
	  .pipe(process.stdout);

上面代码中，由于 ``process.stdout`` 和 ``process.stdin`` 与其他进程的通信，都是流（ ``stream`` ）形式，所以必须通过 ``pipe`` 管道命令中介。

.. code-block:: js

	var fs = require('fs');
	var zlib = require('zlib');

	fs.createReadStream('wow.txt')
	  .pipe(zlib.createGzip())
	  .pipe(process.stdout);

上面代码通过 ``pipe`` 方法，先将文件数据压缩，然后再导向标准输出。

process.stdin
-------------
``process.stdin`` 返回一个对象，表示标准输入。

.. code-block:: js

	process.stdin.pipe(process.stdout)

上面代码表示将标准输入导向标准输出。

由于 ``stdin`` 和 ``stdout`` 都部署了 ``stream`` 接口，所以可以使用 ``stream`` 接口的方法。

.. code-block:: js

	process.stdin.setEncoding('utf8');

	process.stdin.on('readable', function() {
	  var chunk = process.stdin.read();
	  if (chunk !== null) {
	    process.stdout.write('data: ' + chunk);
	  }
	});

	process.stdin.on('end', function() {
	  process.stdout.write('end');
	});

stderr
------
``process.stderr`` 属性指向标准错误。

process.argv，process.execPath，process.execArgv
------------------------------------------------
``process.argv`` 属性返回一个数组，由命令行执行脚本时的各个参数组成。它的第一个成员总是 ``node`` ，第二个成员是脚本文件名，其余成员是脚本文件的参数。

请看下面的例子，新建一个脚本文件 ``argv.js`` 。

.. code-block:: js

	// argv.js
	console.log("argv: ", process.argv);

命令行下调用这个脚本，会得到以下结果。

.. code-block:: shell

	$ node argv.js a b c
	[ 'node', '/path/to/argv.js', 'a', 'b', 'c' ]

上面代码表示， ``argv`` 返回数组的成员依次是命令行的各个部分，真正的参数实际上是从 ``process.argv[2]`` 开始。要得到真正的参数部分，可以把 ``argv.js`` 改写成下面这样。

.. code-block:: js

	// argv.js
	var myArgs = process.argv.slice(2);
	console.log(myArgs);

``process.execPath`` 属性返回执行当前脚本的 ``Node`` 二进制文件的绝对路径。

.. code-block:: shell

	> process.execPath
	'/usr/local/bin/node'
	>

``process.execArgv`` 属性返回一个数组，成员是命令行下执行脚本时，在 ``Node`` 可执行文件与脚本文件之间的命令行参数。

.. code-block:: shell

	# script.js的代码为
	# console.log(process.execArgv);
	$ node --harmony script.js --version

process.env
-----------
``process.env`` 属性返回一个对象，包含了当前 ``Shell`` 的所有环境变量。比如， ``process.env.HOME`` 返回用户的主目录。

通常的做法是，新建一个环境变量 ``NODE_ENV`` ，用它确定当前所处的开发阶段，生产阶段设为 ``production`` ，开发阶段设为 ``develop`` 或 ``staging`` ，然后在脚本中读取 ``process.env.NODE_ENV`` 即可。

运行脚本时，改变环境变量，可以采用下面的写法。

.. code-block:: shell

	$ export NODE_ENV=production && node app.js
	# 或者
	$ NODE_ENV=production node app.js

方法
====
``process`` 对象提供以下方法：

- ``process.chdir()`` ：切换工作目录到指定目录。
- ``process.cwd()`` ：返回运行当前脚本的工作目录的路径。
- ``process.exit()`` ：退出当前进程。
- ``process.getgid()`` ：返回当前进程的组 ``ID`` （数值）。
- ``process.getuid()`` ：返回当前进程的用户 ``ID`` （数值）。
- ``process.nextTick()`` ：指定回调函数在当前执行栈的尾部、下一次 ``Event Loop`` 之前执行。
- ``process.on()`` ：监听事件。
- ``process.setgid()`` ：指定当前进程的组，可以使用数字 ``ID`` ，也可以使用字符串 ``ID`` 。
- ``process.setuid()`` ：指定当前进程的用户，可以使用数字 ``ID`` ，也可以使用字符串 ``ID`` 。


process.cwd()，process.chdir()
------------------------------
``cwd`` 方法返回进程的当前目录（绝对路径）， ``chdir`` 方法用来切换目录。

.. code-block:: shell

	> process.cwd()
	'/home/aaa'

	> process.chdir('/home/bbb')
	> process.cwd()
	'/home/bbb'

.. note::  ``process.cwd()`` 与 ``__dirname`` 的区别。前者进程发起时的位置，后者是脚本的位置，两者可能是不一致的。比如， ``node ./code/program.js`` ，对于 ``process.cwd()`` 来说，返回的是当前目录（ ``.`` ）；对于 ``__dirname`` 来说，返回是脚本所在目录，即 ``./code/program.js`` 。

process.nextTick()
==================
``process.nextTick`` 将任务放到当前一轮事件循环（ ``Event Loop`` ）的尾部。

.. code-block:: js

	process.nextTick(function () {
	  console.log('下一次Event Loop即将开始!');
	});

上面代码可以用 ``setTimeout(f,0)`` 改写，效果接近，但是原理不同。

.. code-block:: js

	setTimeout(function () {
	  console.log('已经到了下一轮Event Loop！');
	}, 0)

``setTimeout(f,0)`` 是将任务放到下一轮事件循环的头部，因此 ``nextTick`` 会比它先执行。另外， ``nextTick`` 的效率更高，因为不用检查是否到了指定时间。

根据 ``Node`` 的事件循环的实现，基本上，进入下一轮事件循环后的执行顺序如下。

- setTimeout(f,0)
- 各种到期的回调函数
- process.nextTick push(), sort(), reverse(), and splice()

process.exit()
---------------
``process.exit`` 方法用来退出当前进程。它可以接受一个数值参数，如果参数大于 ``0`` ，表示执行失败；如果等于 ``0`` 表示执行成功。

.. code-block:: js

	if (err) {
	  process.exit(1);
	} else {
	  process.exit(0);
	}

如果不带有参数， ``exit`` 方法的参数默认为 ``0`` 。

.. note:: ``process.exit()`` 很多时候是不需要的。因为如果没有错误，一旦事件循环之中没有待完成的任务， ``Node`` 本来就会退出进程，不需要调用 ``process.exit(0)`` 。这时如果调用了，进程会立刻退出，不管有没有异步任务还在执行，所以不如等 ``Node`` 自然退出。另一方面，如果发生错误， ``Node`` 往往也会退出进程，也不一定要调用 ``process.exit(1)`` 。

.. code-block:: js

	function printUsageStdout() {
	  process.stdout.write('...some long text ...');
	}

	if (true) {
	  printUsageToStdout();
	  process.exit(1);
	}

上面的代码可能不会达到预期效果。因为 ``process.stdout`` 有时会变成异步，不能保证一定会在当前事件循环之中输出所有内容，而 ``process.exit`` 会使当前进程立刻退出。

更安全的方法是使用 ``exitcode`` 属性，指定退出状态，然后再抛出一个错误。??

.. code-block:: js

	if (true) {
	  printUsageToStdout();
	  process.exitCode = 1;
	  throw new Error("xx condition failed");
	}

``process.exit()`` 执行时，会触发 ``exit`` 事件。

process.on()
-------------
``process`` 对象部署了 ``EventEmitter`` 接口，可以使用 ``on`` 方法监听各种事件，并指定回调函数。

.. code-block:: js

	process.on('uncaughtException', function(err){
	  console.error('got an error: %s', err.message);
	  process.exit(1);
	});

	setTimeout(function(){
	  throw new Error('fail');
	}, 100);

上面代码是 ``process`` 监听 ``Node`` 的一个全局性事件 ``uncaughtException`` ，只要有错误没有捕获，就会触发这个事件。

``process`` 支持的事件还有下面这些。

- ``data`` 事件：数据输出输入时触发
- ``SIGINT`` 事件：接收到系统信号 ``SIGINT`` 时触发，主要是用户按 ``Ctrl + c`` 时触发。
- ``SIGTERM`` 事件：系统发出进程终止信号 ``SIGTERM`` 时触发
- ``exit`` 事件：进程退出前触发

.. code-block:: js

	process.on('SIGINT', function () {
	  console.log('Got a SIGINT. Goodbye cruel world');
	  process.exit(0);
	});

	// 也可以忽略这个信号
	process.on('SIGINT', function() {
	  console.log("Ignored Ctrl-C");
	});

使用时，向该进程发出系统信号，就会导致进程退出。

.. code-block:: shell

    $ kill -s SIGINT [process_id]

``SIGTERM`` 信号表示内核要求当前进程停止，进程可以自行停止，也可以忽略这个信号。

.. code-block:: js

	var http = require('http');

	var server = http.createServer(function (req, res) {
	  // ...
	});

	process.on('SIGTERM', function () {
	  server.close(function () {
	    process.exit(0);
	  });
	});

上面代码表示，进程接到 ``SIGTERM`` 信号之后，关闭服务器，然后退出进程。需要注意的是，这时进程不会马上退出，而是要回应完最后一个请求，处理完所有回调函数，然后再退出。

``exit`` 事件在 ``Node`` 进程退出前触发。

.. code-block:: js

	process.on('exit', function() {
	  console.log('Goodbye');
	});

process.kill()
--------------
``process.kill`` 方法用来对指定 ``ID`` 的线程发送信号，默认为 ``SIGINT`` 信号。

.. code-block:: js

    process.kill(process.pid, 'SIGTERM');

上面代码用于杀死当前进程。

.. code-block:: js

	process.on('SIGTERM', function(){
	  console.log('terminating');
	  process.exit(1);
	});

	setTimeout(function(){
	    console.log('sending SIGTERM to process %d', process.pid);
	    process.kill(process.pid, 'SIGTERM');
	}, 500);

	setTimeout(function(){
	    console.log('never called');
	}, 1000);

上面代码中， ``500`` 毫秒后向当前进程发送 ``SIGTERM`` 信号（终结进程），因此 ``1000`` 毫秒后的指定事件不会被触发。

事件
====

exit事件
--------
当前进程退出时，会触发 ``exit`` 事件，可以对该事件指定回调函数。

.. code-block:: js

	process.on('exit', function () {
	  fs.writeFileSync('/tmp/myfile', '需要保存到硬盘的信息');
	});

下面是一个例子，进程退出时，显示一段日志。

.. code-block:: js

	process.on("exit", code =>
	  console.log("exiting with code: " + code))

.. note:: 此时回调函数只能执行同步操作，不能包含异步操作，因为执行完回调函数，进程就会退出，无法监听到回调函数的操作结果。

.. code-block:: js

	process.on('exit', function(code) {
	  // 不会执行
	  setTimeout(function() {
	    console.log('This will not run');
	  }, 0);
	});

上面代码在 ``exit`` 事件的回调函数里面，指定了一个下一轮事件循环，所要执行的操作。这是无效的，不会得到执行。

beforeExit事件
--------------
``beforeExit`` 事件在 ``Node`` 清空了 ``Event Loop`` 以后，再没有任何待处理的任务时触发。正常情况下，如果没有任何待处理的任务， ``Node`` 进程会自动退出，设置 ``beforeExit`` 事件的监听函数以后，就可以提供一个机会，再部署一些任务，使得 ``Node`` 进程不退出。

``beforeExit`` 事件与 ``exit`` 事件的主要区别是， ``beforeExit`` 的监听函数可以部署异步任务，而 ``exit`` 不行。

此外，如果是显式终止程序（比如调用 ``process.exit()`` ），或者因为发生未捕获的错误，而导致进程退出，这些场合不会触发 ``beforeExit`` 事件。因此，不能使用该事件替代 ``exit`` 事件。

uncaughtException事件
---------------------
当前进程抛出一个没有被捕捉的错误时，会触发 ``uncaughtException`` 事件。

.. code-block:: js

	process.on('uncaughtException', function (err) {
	  console.error('An uncaught error occurred!');
	  console.error(err.stack);
	  throw new Error('未捕获错误');
	});

部署 ``uncaughtException`` 事件的监听函数，是免于 ``Node`` 进程终止的最后措施，否则 ``Node`` 就要执行 ``process.exit()`` 。出于除错的目的，并不建议发生错误后，还保持进程运行。

抛出错误之前部署的异步操作，还是会继续执行。只有完成以后， ``Node`` 进程才会退出。

.. code-block:: js

	process.on('uncaughtException', function(err) {
	  console.log('Caught exception: ' + err);
	});

	setTimeout(function() {
	  console.log('本行依然执行');
	}, 500);

	// 下面的表达式抛出错误
	nonexistentFunc();

上面代码中，抛出错误之后，此前 ``setTimeout`` 指定的回调函数亦然会执行。

信号事件
--------
操作系统内核向 ``Node`` 进程发出信号，会触发信号事件。实际开发中，主要对 ``SIGTERM`` 和 ``SIGINT`` 信号部署监听函数，这两个信号在非 ``Windows`` 平台会导致进程退出，但是只要部署了监听函数， ``Node`` 进程收到信号后就不会退出。

.. code-block:: js

	// 读取标准输入，这主要是为了不让当前进程退出
	process.stdin.resume();

	process.on('SIGINT', function() {
	  console.log('SIGINT信号，按Control-D退出');
	});

上面代码部署了 ``SIGINT`` 信号的监听函数，当用户按下 ``Ctrl-C`` 后，会显示提示文字。

进程的退出码
===========
进程退出时，会返回一个整数值，表示退出时的状态。这个整数值就叫做退出码。下面是常见的 ``Node`` 进程退出码。

- 0，正常退出
- 1，发生未捕获错误
- 5，V8执行错误
- 8，不正确的参数
- 128 + 信号值，如果 ``Node`` 接受到退出信号（比如 ``SIGKILL`` 或 ``SIGHUP`` ），它的退出码就是 ``128`` 加上信号值。由于 ``128`` 的二进制形式是 ``10000000`` , 所以退出码的后七位就是信号值。

``Bash`` 可以使用环境变量 ``$?`` ，获取上一步操作的退出码。

.. code-block:: shell

	$ node nonexist.js
	Error: Cannot find 'nonexist.js'

	$ echo $?
	1

上面代码中， ``Node`` 执行一个不存在的脚本文件，结果报错，退出码就是 ``1`` 。