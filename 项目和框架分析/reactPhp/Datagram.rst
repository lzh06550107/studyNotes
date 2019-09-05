********
Datagram
********

事件驱动的 ``UDP`` 数据报套接字客户端和 ``ReactPHP`` 服务器。

快速例子
========
安装后，您可以使用以下代码连接到侦听 ``localhost:1234`` 的 ``UDP`` 服务器并发送和接收 ``UDP`` 数据报：

.. code-block:: php

	$loop = React\EventLoop\Factory::create();
	$factory = new React\Datagram\Factory($loop);

	$factory->createClient('localhost:1234')->then(function (React\Datagram\Socket $client) {
	    $client->send('first');

	    $client->on('message', function($message, $serverAddress, $client) {
	        echo 'received "' . $message . '" from ' . $serverAddress. PHP_EOL;
	    });
	});

	$loop->run();

`更多的例子 <https://github.com/reactphp/datagram/blob/v1.4.0/examples>`_

使用
====
该库的 API 以 node.js 的 `UDP/数据报套接字 API（dgram.Socket） <https://nodejs.org/api/dgram.html>`_ 为模型。

安装
====

.. code-block:: shell

    $ composer require react/datagram:^1.4

测试
====

要运行测试套件，首先需要克隆此 ``repo`` ，然后通过 ``Composer`` 安装所有依赖项：

.. code-block:: shell

    $ composer install

要运行测试套件，请转到项目根目录并运行：

.. code-block:: shell

	$ php vendor/bin/phpunit




