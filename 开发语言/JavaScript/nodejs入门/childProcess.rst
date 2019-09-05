***************
Child Process模块
***************

``child_process`` 模块用于新建子进程。子进程的运行结果储存在系统缓存之中（最大 ``200KB`` ），等到子进程运行结束以后，主进程再用回调函数读取子进程的运行结果。

exec()
======
``exec`` 方法用于执行 ``bash`` 命令，它的参数是一个命令字符串。

.. code-block:: js

	var exec = require('child_process').exec;

	var ls = exec('ls -l', function (error, stdout, stderr) {
	  if (error) {
	    console.log(error.stack);
	    console.log('Error code: ' + error.code);
	  }
	  console.log('Child Process STDOUT: ' + stdout);
	});

上面代码的 ``exec`` 方法用于新建一个子进程，然后缓存它的运行结果，运行结束后调用回调函数。

``exec`` 方法最多可以接受两个参数，第一个参数是所要执行的 ``shell`` 命令，第二个参数是回调函数，该函数接受三个参数，分别是发生的错误、标准输出的显示结果、标准错误的显示结果。

由于标准输出和标准错误都是流对象（ ``stream`` ），可以监听 ``data`` 事件，因此上面的代码也可以写成下面这样。

.. code-block:: js

	var exec = require('child_process').exec;
	var child = exec('ls -l');

	child.stdout.on('data', function(data) {
	  console.log('stdout: ' + data);
	});
	child.stderr.on('data', function(data) {
	  console.log('stdout: ' + data);
	});
	child.on('close', function(code) {
	  console.log('closing code: ' + code);
	});

上面的代码还表明，子进程本身有 ``close`` 事件，可以设置回调函数。

上面的代码还有一个好处。监听 ``data`` 事件以后，可以实时输出结果，否则只有等到子进程结束，才会输出结果。所以，如果子进程运行时间较长，或者是持续运行，第二种写法更好。

下面是另一个例子，假定有一个 ``child.js`` 文件。

.. code-block:: js

	// child.js

	var exec = require('child_process').exec;
	exec('node -v', function(error, stdout, stderr) {
	  console.log('stdout: ' + stdout);
	  console.log('stderr: ' + stderr);
	  if (error !== null) {
	    console.log('exec error: ' + error);
	  }
	});

运行后，该文件的输出结果如下。

.. code-block:: shell

	$ node child.js

	stdout: v0.11.14

	stderr:

``exec`` 方法会直接调用 ``bash`` （ ``/bin/sh`` 程序）来解释命令，所以如果有用户输入的参数， ``exec`` 方法是不安全的。

.. code-block:: js

	var path = ";user input";
	child_process.exec('ls -l ' + path, function (err, data) {
	  console.log(data);
	});

上面代码表示，在 ``bash`` 环境下， ``ls -l; user input`` 会直接运行。如果用户输入恶意代码，将会带来安全风险。因此，在有用户输入的情况下，最好不使用 ``exec`` 方法，而是使用 ``execFile`` 方法。

execSync()
==========
``execSync`` 是 ``exec`` 的同步执行版本。

它可以接受两个参数，第一个参数是所要执行的命令，第二个参数用来配置执行环境。

.. code-block:: js

	var execSync = require("child_process").execSync;

	var SEPARATOR = process.platform === 'win32' ? ';' : ':';
	var env = Object.assign({}, process.env);

	env.PATH = path.resolve('./node_modules/.bin') + SEPARATOR + env.PATH;

	function myExecSync(cmd) {
	  var output = execSync(cmd, {
	    cwd: process.cwd(),
	    env: env
	  });

	  console.log(output);
	}

	myExecSync('eslint .');

上面代码中， ``execSync`` 方法的第二个参数是一个对象。该对象的 ``cwd`` 属性指定脚本的当前目录， ``env`` 属性指定环境变量。上面代码将 ``./node_modules/.bin`` 目录，存入 ``$PATH`` 变量。这样就可以不加路径，引用项目内部的模块命令了，比如 ``eslint`` 命令实际执行的是 ``./node_modules/.bin/eslint`` 。

execFile()
==========
``execFile`` 方法直接执行特定的程序，参数作为数组传入，不会被 ``bash`` 解释，因此具有较高的安全性。

.. code-block:: js

	var child_process = require('child_process');

	var path = ".";
	child_process.execFile('/bin/ls', ['-l', path], function (err, result) {
	    console.log(result)
	});

上面代码中，假定 ``path`` 来自用户输入，如果其中包含了分号或反引号， ``ls`` 程序不理解它们的含义，因此也就得不到运行结果，安全性就得到了提高。

spawn()
=======
``spawn`` 方法创建一个子进程来执行特定命令，用法与 ``execFile`` 方法类似，但是没有回调函数，只能通过监听事件，来获取运行结果。它属于异步执行，适用于子进程长时间运行的情况。

.. code-block:: js

	var child_process = require('child_process');

	var path = '.';
	var ls = child_process.spawn('/bin/ls', ['-l', path]);
	ls.stdout.on('data', function (data) {
	  console.log('stdout: ' + data);
	});

	ls.stderr.on('data', function (data) {
	  console.log('stderr: ' + data);
	});

	ls.on('close', function (code) {
	  console.log('child process exited with code ' + code);
	});

``spawn`` 方法接受两个参数，第一个是可执行文件，第二个是参数数组。

``spawn`` 对象返回一个对象，代表子进程。该对象部署了 ``EventEmitter`` 接口，它的 ``data`` 事件可以监听，从而得到子进程的输出结果。

``spawn`` 方法与 ``exec`` 方法非常类似，只是使用格式略有区别。

.. code-block:: js

	child_process.exec(command, [options], callback)
	child_process.spawn(command, [args], [options])

fork()
======
``fork`` 方法直接创建一个子进程，执行 ``Node`` 脚本， ``fork('./child.js')`` 相当于 ``spawn('node', ['./child.js'])`` 。与 ``spawn`` 方法不同的是， ``fork`` 会在父进程与子进程之间，建立一个通信管道，用于进程之间的通信。

.. code-block:: js

	var n = child_process.fork('./child.js');
	n.on('message', function(m) {
	  console.log('PARENT got message:', m);
	});
	n.send({ hello: 'world' });

上面代码中， ``fork`` 方法返回一个代表进程间通信管道的对象，对该对象可以监听 ``message`` 事件，用来获取子进程返回的信息，也可以向子进程发送信息。

``child.js`` 脚本的内容如下。

.. code-block:: js

	process.on('message', function(m) {
	  console.log('CHILD got message:', m);
	});
	process.send({ foo: 'bar' });

上面代码中，子进程监听 ``message`` 事件，并向父进程发送信息。

send()
======
使用 ``child_process.fork()`` 生成新进程之后，就可以用 ``child.send(message, [sendHandle])`` 向新进程发送消息。新进程中通过监听 ``message`` 事件，来获取消息。

下面的例子是主进程的代码。

.. code-block:: js

	var cp = require('child_process');

	var n = cp.fork(__dirname + '/sub.js');

	n.on('message', function(m) {
	  console.log('PARENT got message:', m);
	});

	n.send({ hello: 'world' });

下面是子进程 ``sub.js`` 代码。

.. code-block:: js

	process.on('message', function(m) {
	  console.log('CHILD got message:', m);
	});

	process.send({ foo: 'bar' });

