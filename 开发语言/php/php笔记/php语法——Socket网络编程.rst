**************
PHP的Socket网络编程
**************

.. contents:: 目录
   :depth: 4

什么是TCP/IP、UDP？
==================
- TCP/IP（Transmission Control Protocol/Internet Protocol）即传输控制协议/网间协议，是一个工业标准的协议集，它是为广域网（WANs）设计的。
- UDP（User Data Protocol，用户数据报协议）是与TCP相对应的协议。它是属于TCP/IP协议族中的一种。

这里有一张图，表明了这些协议的关系。

.. image:: ./_static/images/TcpUdp.jpg

TCP/IP协议族包括运输层、网络层、链路层。现在你知道TCP/IP与UDP的关系了吧。Socket在哪里呢？在图1中，我们没有看到Socket的影子，那么它到底在哪里呢？还是用图来说话，一目了然。

.. image:: ./_static/images/socket.jpg

Socket是什么呢？
===============
Socket是应用层与TCP/IP协议族通信的中间软件抽象层，它是一组接口。在设计模式中，Socket其实就是一个门面模式，它把复杂的TCP/IP协议族隐藏在Socket接口后面，对用户来说，一组简单的接口就是全部，让Socket去组织数据，以符合指定的协议。

前人已经给我们做了好多的事了，网络间的通信也就简单了许多，但毕竟还是有挺多工作要做的。以前听到Socket编程，觉得它是比较高深的编程知识，但是只要弄清Socket编程的工作原理，神秘的面纱也就揭开了。

一个生活中的场景。你要打电话给一个朋友，先拨号，朋友听到电话铃声后提起电话，这时你和你的朋友就建立起了连接，就可以讲话了。等交流结束，挂断电话结束此次交谈。 生活中的场景就解释了这工作原理，也许TCP/IP协议族就是诞生于生活中，这也不一定。

.. image:: ./_static/images/socket1.jpg

先从服务器端说起。服务器端先初始化Socket，然后与端口绑定(bind)，对端口进行监听(listen)，调用accept阻塞，等待客户端连接。在这时如果有个客户端初始化一个Socket，然后连接服务器(connect)，如果连接成功，这时客户端与服务器端的连接就建立了。客户端发送数据请求，服务器端接收请求并处理请求，然后把回应数据发送给客户端，客户端读取数据，最后关闭连接，一次交互结束。

在PHP中存在两种方式进行socket编程：

- Socket扩展：底层Socket API。类C风格API。所有函数以 ``socket_`` 开头；
- 流Sockets：从PHP5.0.0开始，流扩展(提供所有PHP IO的抽象)被内置来连接网络sockets。使用流扩展创建的Socket资源可以使用几乎所有的流相关的函数，如 ``fgets`` , ``fread`` 或者 ``stream_get_contents`` ,提供了访问流简单方便的方式，这就像处理文件句柄一样。这些函数以 ``stream_socket_`` 开头


socket相关函数：

注意使用如下函数之前，你需要确保你的socket已打开，如果你没有打开，请编辑你的php.ini文件，去掉下面这行前面的注释（分号）：

``extension=php_sockets.dll``

如果你无法去掉注释，那么请使用下面的代码来加载扩展库：

.. code-block:: php

    <?php
	if(!extension_loaded('sockets')){
	    if(strtoupper(substr(PHP_OS,3))=="WIN"){
	        dl('php_sockets.dll');
	    }else{
	        dl('sockets.so');
	    }
	}
    ?>

+------------------------+---------------------------------------------------------+
| 函数名称               | 描述                                                    |
+========================+=========================================================+
| socket_accept()        | 接受一个Socket连接                                      |
+------------------------+---------------------------------------------------------+
| socket_bind()          | 把socket绑定在一个IP地址和端口上                        |
+------------------------+---------------------------------------------------------+
| socket_clear_error()   | 清除socket的错误或者最后的错误代码                      |
+------------------------+---------------------------------------------------------+
| socket_close()         | 关闭一个socket资源                                      |
+------------------------+---------------------------------------------------------+
| socket_cmsg_space()    | 计算消息缓冲区大小                                      |
+------------------------+---------------------------------------------------------+
| socket_connect()       | 开始一个socket连接                                      |
+------------------------+---------------------------------------------------------+
| socket_create_listen() | 在指定端口打开一个socket监听                            |
+------------------------+---------------------------------------------------------+
| socket_create_pair()   | ？？？产生一对没有区别的socket到一个数组里              |
+------------------------+---------------------------------------------------------+
| socket_create()        | 创建一个socket，相当于产生一个socket的数据结构          |
+------------------------+---------------------------------------------------------+
| socket_get_option()    | 获取socket选项                                          |
+------------------------+---------------------------------------------------------+
| socket_getopt()        | socket_get_option的别名                                 |
+------------------------+---------------------------------------------------------+
| socket_getpeername()   | 获取远程端socket，可能是主机/端口，也可能是Unix文件路径 |
+------------------------+---------------------------------------------------------+
| socket_getsockname()   | 获取本地socket，可能是主机/端口，也可能是Unix文件路径   |
+------------------------+---------------------------------------------------------+
| socket_import_stream() | 导入一个流                                              |
+------------------------+---------------------------------------------------------+
| socket_last_error()    | 获取当前socket的最后错误代码                            |
+------------------------+---------------------------------------------------------+
| socket_listen()        | 监听由指定socket的所有连接                              |
+------------------------+---------------------------------------------------------+
| socket_read()          | 读取指定长度的数据                                      |
+------------------------+---------------------------------------------------------+
| socket_recv()          | 从一个socket连接接收数据到缓存                          |
+------------------------+---------------------------------------------------------+
| socket_recvfrom()      | 从套接字接收数据，不管它是否连接                        |
+------------------------+---------------------------------------------------------+
| socket_recvmsg()       | 从iovec里接受消息                                       |
+------------------------+---------------------------------------------------------+
| socket_select()        | 使用指定的超时对给定的套接字阵列运行select（）系统调用  |
+------------------------+---------------------------------------------------------+
| socket_send()          | 这个函数发送数据到已连接的socket                        |
+------------------------+---------------------------------------------------------+
| socket_sendmsg()       | 发送消息到socket                                        |
+------------------------+---------------------------------------------------------+
| socket_sendto()        | 发送消息到套接字，无论是否连接                          |
+------------------------+---------------------------------------------------------+
| socket_set_block()     | 设置socket为阻塞模式                                    |
+------------------------+---------------------------------------------------------+
| socket_set_nonblock()  | 设置socket为非阻塞模式                                  |
+------------------------+---------------------------------------------------------+
| socket_set_option()    | 设置socket选项                                          |
+------------------------+---------------------------------------------------------+
| socket_shutdown()      | 这个函数允许你关闭读、写又读又写的socket                |
+------------------------+---------------------------------------------------------+
| socket_strerror()      | 返回描述套接字错误的字符串                              |
+------------------------+---------------------------------------------------------+
| socket_write()         | 写数据到socket缓存                                      |
+------------------------+---------------------------------------------------------+

socket_create()
===============
``resource socket_create ( int $domain , int $type , int $protocol )``

创建和返回一个socket资源。

参数：

- domain：该参数指定由套接字使用的协议族。

==========  ========================================
协议族         描述
==========  ========================================
AF_INET     基于IPv4的Internet协议。TCP和UDP是这个协议族的通用协议。
AF_INET6    于IPv6的Internet协议。TCP和UDP是这个协议族的通用协议。
AF_UNIX     本地通信协议族。高效率和低开销使其成为IPC（进程间通信）的一种很好的形式。
==========  ========================================

- type：参数选择套接字使用的通信类型。

================  ======================================================================
类型                描述
================  ======================================================================
SOCK_STREAM       提供，可靠有序，全双工，面向连接的字节流。可以支持带外数据传输机制。 TCP协议基于此套接字类型。
SOCK_DGRAM        支持数据报（固定最大长度的无连接，不可靠的消息）。 UDP协议基于这种套接字类型。
SOCK_SEQPACKET    为固定最大长度的数据报提供有序的，可靠的基于双向连接的数据传输路径;消费者需要在每次读取呼叫时读取整个数据包。
SOCK_RAW          提供原始的网络协议访问。这种特殊类型的套接字可以用来手动构建任何类型的协议。此套接字类型的一个常见用途是执行ICMP请求（如ping）。
SOCK_RDM          提供不保证排序的可靠数据报层。这很可能不会在您的操作系统上实现。
================  ======================================================================

- protocol：协议参数设置在返回的套接字上进行通信时要使用的指定域内的特定协议。可以使用getprotobyname()通过名称检索正确的值。如果所需的协议是TCP或UDP，则也可以使用相应的常量SOL_TCP和SOL_UDP、IPPROTO_IP(0)。

======  ================================================================================================================================================================
名称      描述
======  ================================================================================================================================================================
icmp    Internet控制消息协议主要由网关和主机用于报告数据报通信中的错误。 “ping”命令（目前在大多数现代操作系统中）是ICMP的一个示例应用程序。
udp     用户数据报协议是一个无连接的，不可靠的，具有固定记录长度的协议。由于这些方面，UDP需要最少量的协议开销。
tcp     传输控制协议是一种可靠的基于连接的面向流的全双工协议。 TCP保证所有数据包将按照发送顺序被接收。如果在通信过程中丢失了某个数据包，则TCP将自动重新发送数据包，直到目标主机确认该数据包。出于可靠性和性能原因，TCP实现本身决定了底层数据报通信层的适当的八位组边界。因此，TCP应用程序必须允许部分记录传输的可能性。
======  ================================================================================================================================================================

返回值：

成功返回socket资源，失败返回False。实际的错误代码可以通过调用socket_last_error()来获取。此错误代码可能会传递给socket_strerror()以获取错误的文本说明。

.. code-block:: php

    <?php
	// 会创建一个本地文件，socket_close被调用时不会删除该文件，需要使用unlink()手动删除
	$socket = socket_create(AF_UNIX, SOCK_STREAM, 0); //unix tcp socket
	$socket = socket_create(AF_UNIX, SOCK_DGRAM, 0); //unix udp socket
	$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP); // tcp socket
	$socket = socket_create(AF_INET, SOCK_DGRAM, SOL_UDP); // udp socket
    ?>

socket_create_pair()
====================
``bool socket_create_pair ( int $domain , int $type , int $protocol , array &$fd )``

创建两个连接和不可区分的套接字，并将它们存储在fd中。这个功能通常在IPC（进程间通信）中使用。

参数：

- domain：domain参数指定由套接字使用的协议族。
- type：type参数选择套接字使用的通信类型。
- protocol：协议参数设置在返回的套接字上进行通信时要使用的指定域内的特定协议。
- fd：引用两个套接字资源将被插入的数组。传入空数组，返回一对socket。

返回值：

成功返回TRUE，失败则返回FALSE。

.. code-block:: php

    <?php
	$sockets = array();
	/* Setup socket pair */
	if (socket_create_pair(AF_UNIX, SOCK_STREAM, 0, $sockets) === false) {
	    echo "socket_create_pair failed. Reason: ".socket_strerror(socket_last_error());
	}
	/* Send and Recieve Data */
	if (socket_write($sockets[0], "ABCdef123\n", strlen("ABCdef123\n")) === false) {
	    echo "socket_write() failed. Reason: ".socket_strerror(socket_last_error($sockets[0]));
	}
	if (($data = socket_read($sockets[1], strlen("ABCdef123\n"), PHP_BINARY_READ)) === false) {
	    echo "socket_read() failed. Reason: ".socket_strerror(socket_last_error($sockets[1]));
	}
	var_dump($data);

	/* Close sockets */
	socket_close($sockets[0]);
	socket_close($sockets[1]);
    ?>

socket_create_listen()
=====================
``resource socket_create_listen ( int $port [, int $backlog = 128 ] )``

创建一个类型为AF_INET的新套接字资源，监听给定端口上的所有本地网卡，等待新的连接。这个函数是为了简化创建一个只监听接受新连接的新套接字的任务。

参数：

port：在所有网卡上侦听的端口；
backlog：等待连接队列最大长度，可以使用SOMAXCONN作为参数；

返回值：

socket_create_listen()成功返回一个新的套接字资源，错误时返回FALSE。

.. note:: 如果想要创建在某一个网卡上监听socket，你需要使用socket_create(), socket_bind() 和 socket_listen()。

如果指定端口为0，则会随机分配一个端口，如果需要获取该信息，需要使用socket_getsockname($sock, $addr, $port)获取地址和端口信息。

socket_bind
============
``bool socket_bind ( resource $socket , string $address [, int $port = 0 ] )``

将地址绑定到由socket描述的套接字。这必须在使用socket_connect()或socket_listen()建立连接之前完成。

参数：

- socket：使用socket_create()创建的有效套接字资源；
- address：如果socket是AF_INET，则为IP地址，如果是AF_UNIX，则是unix socket(/tmp/my.sock，需要修改文件权限)chmod($sockpath, 0702)；
   + 127.0.0.1：仅仅接收本地主机连接；
   + w.x.y.z：仅仅接收来自这个网络的连接；
   + 0.0.0.0：接收来自任何网络的连接；
- port(可选)：port参数仅在绑定AF_INET套接字时使用，并指定侦听连接的端口；参数0表示随机绑定一个端口。

返回值：

成功返回TRUE，失败则返回FALSE。

.. note:: 如果端口绑定失败，则需要socket_set_option($sock, SOL_SOCKET, SO_REUSEADDR, 1)来设置端口重用。

socket_listen()
==============
``bool socket_listen ( resource $socket [, int $backlog = 0 ] )``

在使用socket_create()创建套接字套接字并使用socket_bind()绑定名称之后，可能会要求侦听套接字上的传入连接。

socket_listen()仅适用于SOCK_STREAM或SOCK_SEQPACKET类型的套接字。

参数：

- socket：使用socket_create()创建的有效套接字资源。
- backlog：等待连接队列最大长度，可以使用SOMAXCONN作为参数；

可以修改系统最大队列长度：

1. sudo sysctl -a | grep somaxconn；
2. sudo sysctl -w net.core.somaxconn=1024

返回值：

成功返回TRUE，失败则返回FALSE。

socket_accept()
===============
``resource socket_accept ( resource $socket )``

在使用socket_create()创建套接字套接字后，使用socket_bind()绑定到一个名称，并使用socket_listen()监听连接，此函数将接受该套接字上的传入连接。一旦连接成功，返回一个新的套接字资源，可用于通信。如果套接字上有多个连接排队，则会使用第一个连接。如果没有挂起的连接，socket_accept()将会阻塞，直到出现连接。如果使用socket_set_blocking()或socket_set_nonblock()将套接字设置为非阻塞，则将返回FALSE。

参数：

socket：使用socket_create()创建的有效套接字资源。

返回值：

成功返回新的套接字资源，错误时返回FALSE。

socket_read()
=============
``string socket_read ( resource $socket , int $length [, int $type = PHP_BINARY_READ ] )``

该函数从socket读取数据。read()与flags参数为0的recv()等价。

参数：

- socket：使用socket_create()或socket_accept()创建的有效套接字资源。
- length：读取的最大字节数由长度参数指定。否则，您可以使用\\r，\\ n或\\ 0结束读取（取决于下面的type参数）。
- type：可选的类型参数是一个命名常量：

    + PHP_BINARY_READ(默认)：使用系统recv（）函数。二进制数据读取安全。连接断开，返回空字符串。
    + PHP_NORMAL_READ：在\\ n或\\ r停止读。连接断开，返回false。

返回值：

成功返回读取的字符串，失败返回False。

.. note:: 当没有更多的数据要读取时，socket_read()返回一个零长度的字符串("")。

socket_recv()
=============
``int socket_recv ( resource $socket , string &$buf , int $len , int $flags )``

socket_recv()函数从socket接收len个字节的数据到buf中。 socket_recv()可用于从连接的套接字收集数据。此外，可以指定一个或多个标志来修改函数的行为。

buf是通过引用传递的，所以它必须在参数列表中被指定为一个变量。通过socket_recv()从套接字读取的数据将在buf中返回。

参数：

- socket：套接字必须是以前由socket_create()创建的套接字资源。
- buf：接收到的数据将被读取到buf中。如果发生错误，如果连接重置，或者没有数据可用，则buf将被设置为NULL。
- len：将从远程主机获取最多len字节。
- flags：标志的值可以是以下标志的任意组合，用二进制OR(|)运算符连接。

==============  =============================================
标志              描述
==============  =============================================
MSG_OOB         处理带外数据
MSG_PEEK        从接收队列的开始处接收数据，而不将其从队列中移除。
MSG_WAITALL     阻塞直到接收到len个数据。但是，如果捕获信号或者远程主机断开，该函数返回读到的数据。
MSG_DONTWAIT    设置了该标志后，即使socket被设置为阻塞，该函数也会立即返回。
==============  =============================================

返回值：

socket_recv()返回接收到的字节数，如果有错误则返回FALSE。如果客户端没有数据，则返回false；如果客户端断开连接，则返回0。

socket_recvfrom()
==================
``int socket_recvfrom ( resource $socket , string &$buf , int $len , int $flags , string &$name [, int &$port ] )``

socket_recvfrom()函数使用套接字从指定name和端口上（如果套接字不是AF_UNIX类型）接收len个字节的数据到buf中。 socket_recvfrom()可用于从已连接和未连接的套接字收集数据。此外，可以指定一个或多个标志来修改函数的行为。

名称和端口必须通过引用传递。如果套接字不是面向连接的，则将名称设置为远程主机的Internet协议地址或UNIX套接字的路径。如果套接字是面向连接的，则name为NULL。此外，在未连接AF_INET或AF_INET6套接字的情况下，端口将包含远程主机的端口。

参数：

- socket：套接字必须是以前由socket_create()创建的套接字资源。
- buf：接收到的数据将被读取到由buf指定的变量中。
- len：将从远程主机获取最多len字节。
- flags：参考socket_recv()中的flags。
- name：如果套接字类型是AF_UNIX类型，则name是文件的路径。否则，对于未连接的套接字，name是远程主机的IP地址，如果套接字是面向连接的，则为NULL。
- port：此参数仅适用于AF_INET和AF_INET6套接字，并指定从中接收数据的远程端口。如果套接字是面向连接的，那么端口将是NULL。

返回值：

socket_recvfrom()返回接收到的字节数，如果发生错误则返回FALSE。

socket_recvmsg()
=================
``int socket_recvmsg ( resource $socket , string $message [, int $flags ] )``


socket_write()
==============
``int socket_write ( resource $socket , string $buffer [, int $length = 0 ] )``

函数socket_write()从给定的缓冲区写入套接字。write()等价于标志为0的send()。

参数：
 - socket：
 - buffer：要写入的缓冲区。
 - length：可选参数长度可以指定写入套接字的字节长度。如果这个长度大于缓冲区长度，它将被自动截断为缓冲区的长度。

返回值：

返回成功写入套接字的字节数或失败时返回FALSE。

.. note:: socket_write()返回0意味着没有写入字节，这是完全有效的。如果发生错误，请确保使用===运算符来检查FALSE。

socket_wirte()可能不会一次写入buffer中的所有数据，我们需要判断来决定是否需要再次写入剩余的数据。

socket_send()
=============
``int socket_send ( resource $socket , string $buf , int $len , int $flags )``

函数socket_send()从buf发送len字节到socket套接字。

参数：

- socket：使用socket_create()或socket_accept()创建的有效套接字资源。
- buf：包含将发送到远程主机的数据的缓冲区。
- len：将从buf发送到远程主机的字节数。
- flags：标志的值可以是以下标志的任意组合，用二进制OR(|)运算符连接。

===============  ===========================================
标志               描述
===============  ===========================================
MSG_OOB          发送OOB（带外）数据。
MSG_EOR          指示一个记录标记。发送的数据完成记录??。
MSG_EOF          关闭套接字的发送端，并在发送数据的末尾包含一个适当的通知。发送的数据完成交易??。
MSG_DONTROUTE    绕过路由，使用直接网卡接口??。
===============  ===========================================

返回值：

socket_send()返回发送的字节数，错误时返回FALSE。

socket_sendto()
===============
``int socket_sendto ( resource $socket , string $buf , int $len , int $flags , string $addr [, int $port = 0 ] )``

函数socket_sendto()通过socket套接字从buf发送len字节到地址addr的端口。

参数：

- socket：使用socket_create()或socket_accept()创建的有效套接字资源。
- buf：包含将发送到远程主机的数据的缓冲区。
- len：将从buf发送到远程主机的字节数。
- flags：同socket_send()的flags参数。
- addr：远程主机的IP地址。
- port：端口是数据将被发送到的远程端口号。

返回值：

socket_sendto()返回发送到远程主机的字节数，如果发生错误则返回FALSE。

socket_sendmsg()
=================
``int socket_sendmsg ( resource $socket , array $message , int $flags )``

socket_connect()
=================
``bool socket_connect ( resource $socket , string $address [, int $port = 0 ] )``

使用套接字资源启动连接，该套接字资源socket必须是使用socket_create()创建的有效套接字资源。

参数：

- socket：
- address：如果socket为AF_INET，则地址参数为点分隔符号形式的IPv4地址（例如127.0.0.1），如果启用IPv6支持且套接字为AF_INET6，则为有效的IPv6地址（例如:: 1）套接字，或如果套接字系列是AF_UNIX，则为Unix域的路径名。
- port：该参数仅用于连接到AF_INET或AF_INET6套接字时是必需的，它指定要建立连接的远程主机上的端口。

返回值：

成功返回TRUE，失败则返回FALSE。

.. note:: 如果套接字是非阻塞的，那么这个函数返回FALSE。

socket_set_block()
==================
``bool socket_set_block ( resource $socket )``

socket_set_block()函数删除由socket参数指定的套接字上的O_NONBLOCK标志。

当一个操作（例如receive，send，connect，accept，...）在阻塞socket上执行时，脚本会暂停执行直到它接收到一个信号或者它可以执行操作。

参数：

- socket：使用socket_create()或socket_accept()创建的有效套接字资源。

返回值：

成功返回TRUE，失败则返回FALSE。

socket_set_nonblock()
=====================
``bool socket_set_nonblock ( resource $socket )``

socket_set_nonblock()函数在由socket参数指定的套接字上设置O_NONBLOCK标志。

当一个操作（例如receive，send，connect，accept，...）在非阻塞socket上执行时，脚本不会暂停执行。如果执行失败，则返回false。

参数：

- socket：使用socket_create()或socket_accept()创建的有效套接字资源。

返回值：

成功返回TRUE，失败则返回FALSE。

socket_shutdown()
==================
``bool socket_shutdown ( resource $socket [, int $how = 2 ] )``

socket_shutdown()函数允许您停止通过套接字读取，写入或读写数据。

参数：

- socket：使用socket_create()创建的有效套接字资源。
- how：可以是下列之一：

    + 0：关闭socket读；
    + 1：关闭socket写；
    + 2：关闭socket读写；

返回值：

成功返回TRUE，失败则返回FALSE。

socket_close()
==============
``void socket_close ( resource $socket )``

socket_close()关闭由socket给定的套接字资源。此函数特定于套接字，不能用于任何其他类型的资源。

socket_set_option()
===================
``bool socket_set_option ( resource $socket , int $level , int $optname , mixed $optval )``

socket_set_option()函数将指定协议的optname参数指定的选项设置为socket的optval参数所指向的值。

参数：

- socket：使用socket_create()或socket_accept()创建的有效套接字资源。
- level：level参数指定选项所在的协议级别。例如，要在套接字级别检索选项，将使用SOL_SOCKET的级别参数。通过指定该级别的协议号可以使用其他级别，如TCP。协议编号可以通过使用getprotobyname（）函数找到。
- optname：可用的套接字选项与socket_get_option()函数的选项相同。
- optval：选项值。

返回值：

成功返回TRUE，失败则返回FALSE。

socket_get_option()
===================
``mixed socket_get_option ( resource $socket , int $level , int $optname )``

socket_get_option()函数检索由指定套接字的optname参数指定的选项的值。

socket_getpeername()
=====================
``bool socket_getpeername ( resource $socket , string &$address [, int &$port ] )``

查询给定套接字的远程端，这可能导致主机/端口或Unix文件系统路径，这取决于其类型。

参数：

- socket：使用socket_create()或socket_accept()创建的有效套接字资源。
- address：如果给定的套接字类型为AF_INET或AF_INET6，则socket_getpeername()将在地址参数中以适当的表示形式（例如127.0.0.1或fe80 :: 1）返回对等体（远程）IP地址，并且如果存在可选的端口参数，也返回相关的端口。如果给定的套接字类型为AF_UNIX，则socket_getpeername()将返回地址参数中的Unix文件系统路径（例如/var/run/daemon.sock）。
- port：如果给出，这将保存地址相关的端口。

返回值：

成功返回TRUE，失败则返回FALSE。如果套接字类型不是AF_INET，AF_INET6或AF_UNIX中的任何一个，则socket_getpeername()也可能返回FALSE，在这种情况下，最后一个socket错误代码不会更新。

.. note:: socket_getpeername()不应用于使用socket_accept()创建的AF_UNIX套接字。只有主服务器socket调用socket_bind()之后，或者使用socket_connect()之后的套接字才会返回有意义的值。

socket_getsockname()
=====================
``bool socket_getsockname ( resource $socket , string &$addr [, int &$port ] )``

参数同上个方法。

.. note:: socket_getsockname()不应用于使用socket_connect()创建的AF_UNIX套接字。只有使用socket_accept()创建的套接字或者调用socket_bind()之后的主服务器套接字才返回有意义的值。

socket_import_stream()
=======================
``resource socket_import_stream ( resource $stream )``

将封装socket的流导入套接字扩展资源。失败时返回FALSE或NULL。

socket_select
=============
``int socket_select ( array &$read , array &$write , array &$except , int $tv_sec [, int $tv_usec = 0 ] )``

使用指定的超时对给定的套接字数组运行select()系统调用。socket_select()接受套接字数组，并等待它们改变状态。这些套接字资源数组实际上是所谓的文件描述符集。观察三个独立的套接字资源数组。这是一个同步方法，必须得到响应之后才会继续下一步,常用在同步非阻塞IO。

参数：

- read：读取数组中的套接字将被监视以查看字符是否可用于读取（更确切地说，查看读取是否不会被阻塞 - 特别是套接字资源在文件末尾，在这种情况下一个socket_read()将返回一个零长度的字符串）。
- write：写数组中的套接字将被监视，看是否写不会被阻塞。
- except：该数组中的套接字将被监视异常。
- tv_sec：tv_sec和tv_usec一起构成超时参数。超时是socket_select()返回之前经过的时间量的上限。 tv_sec可能为零，导致socket_select()立即返回。这对于轮询很有用。如果tv_sec是NULL（没有超时），socket_select()可以无限期地阻塞。
- tv_usec：

.. note:: 在退出时，数组被修改以指示哪个套接字资源实际上改变了状态。

您不需要将每个参数数组传递给socket_select()。你可以把它放在外面，用一个空的数组或NULL来代替。另外不要忘记那些数组是通过引用传递的，并且会在socket_select()返回后被修改。

返回值：

成功时，socket_select()将返回修改数组中包含的套接字资源的数量(所有修改的socket包含在传入参数read、write、except中)，如果在发生任何有趣的事情之前超时到期，则可能为零(如果socket_select返回0，则表示套接字是非阻塞的，并且没有任何数据可用。)。返回错误FALSE。错误代码可以通过socket_last_error（）来获取。

.. note:: 检查错误时一定要使用===操作符。由于socket_select()可能返回0，所以与==的比较将计算为TRUE：

1. 新连接到来时,被监听的端口是活跃的,如果是新数据到来或者客户端关闭链接时,活跃的是对应的客户端socket而不是服务器上被监听的端口。
2. 如果客户端发来数据没有被读走,则socket_select将会始终显示客户端是活跃状态并将其保存在readfds数组中。
3. 如果客户端先关闭了,则必须手动关闭服务器上相对应的客户端socket,否则socket_select也始终显示该客户端活跃(这个道理跟"有新连接到来然后没有用socket_access把它读出来,导致监听的端口一直活跃"是一样的)。

.. code-block:: php

    <?php
    $e = NULL;
    if (false === socket_select($r, $w, $e, 0)) {
        echo "socket_select() failed, reason: " .
            socket_strerror(socket_last_error()) . "\n";
    }
    ?>

.. note:: 请注意，某些套接字实现需要非常小心地处理。一些基本规则：
- 你应该总是尝试使用不带有超时参数的socket_select()。如果没有可用的数据，你的程序应该不干什么。依赖超时的代码通常不便携，难于调试。
- 如果你不打算在socket_select()调用之后检查它的结果并且适当地作出响应，那么不要把套接字资源添加到任何集合中。
- 在socket_select()返回之后，必须检查所有数组中的所有套接字资源。任何可用于写入的套接字资源都必须写入，并且必须读取任何可用于读取的套接字资源。
- 如果读取/写入数组中的套接字，请注意，它们不必读取/写入所请求的全部数据量。可能只想读/写一个字节。
- 对于大多数套接字实现来说是很常见的，唯一例外的情况是在套接字上接收到超出边界的数据。

阻塞和非阻塞对读写的影响
========================
读
----
读阻塞与非阻塞读的区别:  //阻塞和非阻塞的区别在于没有数据到达的时候是否立刻返回．

读(read/recv/msgrcv)：读的本质来说其实不能是读,在实际中, 具体的接收数据不是由这些调用来进行,是由于系统底层自动完成的。read 也好,recv 也好只负责把数据从底层缓冲copy 到我们指定的位置.

对于读来说(read, 或者recv) ::

阻塞情况下::

在阻塞条件下，read/recv/msgrcv的行为::

1. 如果没有发现数据在网络缓冲中会一直等待；
2. 当发现有数据的时候会把数据读到用户指定的缓冲区，但是如果这个时候读到的数据量比较少，比参数中指定的长度要小，read 并不会一直等待下去，而是立刻返回。

**read 的原则::是数据在不超过指定的长度的时候有多少读多少，没有数据就会一直等待。**

所以一般情况下::我们读取数据都需要采用循环读的方式读取数据，因为一次read 完毕不能保证读到我们需要长度的数据，read 完一次需要判断读到的数据长度再决定是否还需要再次读取。

非阻塞情况下::

在非阻塞的情况下，read 的行为::

1. 如果发现没有数据就直接返回；
2. 如果发现有数据那么也是采用有多少读多少的进行处理；

**所以::read 完一次需要判断读到的数据长度再决定是否还需要再次读取。**

**对于读而言::   阻塞和非阻塞的区别在于没有数据到达的时候是否立刻返回。**

recv 中有一个MSG_WAITALL 的参数::

recv(sockfd, buff, buff_size, MSG_WAITALL)：在正常情况下recv 是会等待直到读取到buff_size 长度的数据，但是这里的WAITALL 也只是尽量读全，在有中断的情况下recv 还是可能会被打断，造成没有读完指定的buff_size的长度。

所以即使是采用recv + WAITALL 参数还是要考虑是否需要循环读取的问题，在实验中对于多数情况下recv (使用了MSG_WAITALL)还是可以读完buff_size，所以相应的性能会比直接read 进行循环读要好一些。

.. note:: //使用MSG_WAITALL时，sockfd必须处于阻塞模式下，否则不起作用。所以MSG_WAITALL不能和MSG_NONBLOCK同时使用。

写
----
写阻塞与非阻塞写的区别:

写(send/write/msgsnd)：写的本质也不是进行发送操作,而是把用户态的数据copy 到系统底层去,然后再由系统进行发送操作,send、write返回成功，只表示数据已经copy 到底层缓冲,而不表示数据已经发出,更不能表示对方端口已经接收到数据.

对于write(或者send)而言：

阻塞情况下::  //阻塞情况下，write会将数据发送完。(不过可能被中断)

在阻塞的情况下，是会一直等待，直到write 完，全部的数据再返回．这点行为上与读操作有所不同。

原因：

1. 读，究其原因主要是读数据的时候我们并不知道对端到底有没有数据，数据是在什么时候结束发送的，如果一直等待就可能会造成死循环，所以并没有去进行这方面的处理；
2. 写，而对于write, 由于需要写的长度是已知的，所以可以一直再写，直到写完。不过问题是write 是可能被打断的，造成write 一次只write 一部分数据, 所以write 的过程还是需要考虑循环write，只不过多数情况下一次write 调用就可能成功。

非阻塞写的情况下：

非阻塞写的情况下，是采用可以写多少就写多少的策略。与读不一样的地方在于，有多少读多少是由网络发送的那一端是否有数据传输到为标准，但是对于可以写多少是由本地的网络堵塞情况为标准的，在网络阻塞严重的时候，网络层没有足够的内存来进行写操作，这时候就会出现写不成功的情况，阻塞情况下会尽可能(有可能被中断)等待到数据全部发送完毕， 对于非阻塞的情况就是一次写多少算多少，没有中断的情况下也还是会出现write 到一部分的情况。


函数使用
========
TCP客户端编程：
--------------
创建一个socket
^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    // 创建一个socket
    $sock = socket_create(AF_INET, SOCK_STREAM, IPPROTO_IP);
    // 如果创建失败，则显示错误信息
    if(!($sock = socket_create(AF_INET, SOCK_STREAM, 0)))
    {
        $errorcode = socket_last_error();
        $errormsg = socket_strerror($errorcode);

        die("Couldn't create socket: [$errorcode] $errormsg \n");
    }
    echo "Socket created";
    ?>

连接服务器
^^^^^^^^^
我们连接到某个端口号的远程服务器。所以我们需要2件东西，IP地址和端口号来连接。所以你需要知道你连接的远程服务器的IP地址。在这里，我们使用google.com的ip地址作为示例。稍后我们将看到如何找出给定域名的IP地址。

.. code-block:: php

    <?php
    // 连接到google主页
    if(!socket_connect($sock , '74.125.235.20' , 80))
    {
        $errorcode = socket_last_error();
        $errormsg = socket_strerror($errorcode);

        die("Could not connect: [$errorcode] $errormsg \n");
    }
    echo "Connection established \n";
    ?>

“连接”的概念适用于SOCK_STREAM / TCP类型的套接字。连接意味着数据的可靠的“流”，使得可以有多个这样的流，每个具有其自己的通信。把它看作是一个不受其他数据干扰的管道。

其他插座，如UDP，ICMP，ARP没有“连接”的概念。这些是基于非连接的通信。这意味着你不断地发送或接收来自任何人和每个人的数据包。

发送数据
^^^^^^^^
模拟http协议发送一个get请求。

.. code-block:: php

    <?php
    $message = "GET / HTTP/1.1\r\n\r\n";
    //Send the message to the server
    if( ! socket_send ( $sock , $message , strlen($message) , 0))
    {
        $errorcode = socket_last_error();
        $errormsg = socket_strerror($errorcode);

        die("Could not send data: [$errorcode] $errormsg \n");
    }
    echo "Message send successfully \n";
    ?>

接收数据
^^^^^^^^
获取服务器响应的数据。

.. code-block:: php

    <?php
    //Now receive reply from server
    if(socket_recv ( $sock , $buf , 2045 , MSG_WAITALL ) === FALSE)
    {
        $errorcode = socket_last_error();
        $errormsg = socket_strerror($errorcode);

        die("Could not receive data: [$errorcode] $errormsg \n");
    }
    //print the received message
    echo $buf;
    ?>

关闭socket
^^^^^^^^^

``socket_close($sock);``

TCP服务器端编程：
----------------

创建并绑定一个socket
^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    if(!($sock = socket_create(AF_INET, SOCK_STREAM, 0)))
    {
        $errorcode = socket_last_error();
        $errormsg = socket_strerror($errorcode);

        die("Couldn't create socket: [$errorcode] $errormsg \n");
    }

    echo "Socket created \n";

    // Bind the source address
    if( !socket_bind($sock, "127.0.0.1" , 5000) )
    {
        $errorcode = socket_last_error();
        $errormsg = socket_strerror($errorcode);

        die("Could not bind socket : [$errorcode] $errormsg \n");
    }

    echo "Socket bind OK \n";
    ?>

监听连接
^^^^^^^^

.. code-block:: php

    <?php
    if(!socket_listen ($sock , 10))
    {
        $errorcode = socket_last_error();
        $errormsg = socket_strerror($errorcode);

        die("Could not listen on socket : [$errorcode] $errormsg \n");
    }

    echo "Socket listen OK \n";
    ?>

接收连接
^^^^^^^

.. code-block:: php

    <?php
    echo "Waiting for incoming connections... \n";

    //Accept incoming connection - This is a blocking call
    $client = socket_accept($sock);

    //display information about the client who is connected
    if(socket_getpeername($client , $address , $port))
    {
        echo "Client $address : $port is now connected to us.";
    }
    ?>

我们可以通过 ``telnet localhost 5000`` 来连接服务器。

读写数据
^^^^^^^

.. code-block:: php

    <?php
    //read data from the incoming socket
    $input = socket_read($client, 1024000);
    $response = "OK .. $input";
    // Display output  back to client
    socket_write($client, $response);
    // Display output  back to client
    socket_write($client, $response);
    socket_close($client);
    ?>

上面的服务器只能接收一个连接。我们需要保持我们的服务器运行不间断。最简单的方法是把accept放在一个循环中，这样它就可以一直接收到连接。

.. code-block:: php

    <?php
    //start loop to listen for incoming connections
    while (true)
    {
        //Accept incoming connection - This is a blocking call
        $client =  socket_accept($sock);

        //display information about the client who is connected
        if(socket_getpeername($client , $address , $port))
        {
            echo "Client $address : $port is now connected to us. \n";
        }

        //read data from the incoming socket
        $input = socket_read($client, 1024000);

        $response = "OK .. $input";

        // Display output  back to client
        socket_write($client, $response);
    }
    ?>

上面的代码可以连接多个客户端，但是只能交互一次。为了处理每个连接，我们需要一个单独的处理代码来与接受连接的主服务器一起运行。一种方法是使用线程。主服务器程序接受一个连接并创建一个新的线程来处理连接的通信，然后服务器返回来接受更多的连接。不过，php不直接支持线程。

另一种方法是使用select函数。select函数基本上是“轮询”或观察某些事件的套接字，如可读，可写或有异常等。所以select函数可以用来监视多个客户端，并检查哪个客户端发送了一个消息。

.. code-block:: php

    <?php
    error_reporting(~E_NOTICE);
    set_time_limit(0);

    $address = "0.0.0.0";
    $port = 5000;
    $max_clients = 10;

    if (!($sock = socket_create(AF_INET, SOCK_STREAM, 0))) {
        $errorcode = socket_last_error();
        $errormsg = socket_strerror($errorcode);

        die("Couldn't create socket: [$errorcode] $errormsg \n");
    }

    echo "Socket created \n";

    // Bind the source address
    if (!socket_bind($sock, $address, 5000)) {
        $errorcode = socket_last_error();
        $errormsg = socket_strerror($errorcode);

        die("Could not bind socket : [$errorcode] $errormsg \n");
    }

    echo "Socket bind OK \n";

    if (!socket_listen($sock, 10)) {
        $errorcode = socket_last_error();
        $errormsg = socket_strerror($errorcode);

        die("Could not listen on socket : [$errorcode] $errormsg \n");
    }

    echo "Socket listen OK \n";

    echo "Waiting for incoming connections... \n";

    //array of client sockets
    $client_socks = array();

    //array of sockets to read
    $read = array();

    //start loop to listen for incoming connections and process existing connections
    while (true) {
        //prepare array of readable client sockets
        $read = array();

        //first socket is the master socket
        $read[0] = $sock; // 每次循环都需要把需要监控的主socket加入

        //now add the existing client sockets
        // 每次循环，都需要把需要监控的客户端socket加入
        for ($i = 0; $i < $max_clients; $i++) {
            if ($client_socks[$i] != null) {
                $read[$i + 1] = $client_socks[$i];
            }
        }

        $write = NULL;
        $except = NULL;
        //now call select - blocking call
        // 阻塞方法，获取改变状态的socket数组
        if (socket_select($read, $write, $except, null) === false) {
            $errorcode = socket_last_error();
            $errormsg = socket_strerror($errorcode);

            die("Could not listen on socket : [$errorcode] $errormsg \n");
        }

        //if ready contains the master socket, then a new connection has come in
        if (in_array($sock, $read)) {
            for ($i = 0; $i < $max_clients; $i++) {
                if ($client_socks[$i] == null) {
                    $client_socks[$i] = socket_accept($sock);

                    //display information about the client who is connected
                    if (socket_getpeername($client_socks[$i], $address, $port)) {
                        echo "Client $address : $port is now connected to us. \n";
                    }

                    //Send Welcome message to client
                    $message = "Welcome to php socket server version 1.0 \n";
                    $message .= "Enter a message and press enter, and i shall reply back \n";
                    socket_write($client_socks[$i], $message);
                    break;
                }
            }
        }

        //check each client if they send any data
        for ($i = 0; $i < $max_clients; $i++) {
            if (in_array($client_socks[$i], $read)) {
                $input = socket_read($client_socks[$i], 1024);

                if ($input == null) {
                    //zero length string meaning disconnected, remove and close the socket
                    unset($client_socks[$i]);
                    socket_close($client_socks[$i]);
                }

                $n = trim($input);

                $output = "OK ... $input";

                echo "Sending output to client \n";

                //send response to client
                socket_write($client_socks[$i], $output);
            }
        }
    }
    ?>

UDP服务端编程：
-------------------
该服务器可以连接多个客户端。

.. code-block:: php

    <?php
    //Reduce errors
    error_reporting(~E_WARNING);

    //Create a UDP socket
    if (!($sock = socket_create(AF_INET, SOCK_DGRAM, 0))) {
        $errorcode = socket_last_error();
        $errormsg = socket_strerror($errorcode);

        die("Couldn't create socket: [$errorcode] $errormsg \n");
    }

    echo "Socket created \n";

    // Bind the source address
    if (!socket_bind($sock, "0.0.0.0", 9999)) {
        $errorcode = socket_last_error();
        $errormsg = socket_strerror($errorcode);

        die("Could not bind socket : [$errorcode] $errormsg \n");
    }

    echo "Socket bind OK \n";

    //Do some communication, this loop can handle multiple clients
    while (1) {
        echo "Waiting for data ... \n";

        //Receive some data
        $r = socket_recvfrom($sock, $buf, 512, 0, $remote_ip, $remote_port);
        echo "$remote_ip : $remote_port -- " . $buf;

        //Send back the data to the client
        socket_sendto($sock, "OK " . $buf, 100, 0, $remote_ip, $remote_port);
    }

    socket_close($sock);
    ?>

UDP客户端编程：
-------------------

.. code-block:: php

    <?php
    //Reduce errors
    error_reporting(~E_WARNING);

    $server = '127.0.0.1';
    $port = 9999;

    if (!($sock = socket_create(AF_INET, SOCK_DGRAM, 0))) {
        $errorcode = socket_last_error();
        $errormsg = socket_strerror($errorcode);

        die("Couldn't create socket: [$errorcode] $errormsg \n");
    }

    echo "Socket created \n";

    //Communication loop
    while (1) {
        //Take some input to send
        echo 'Enter a message to send : ';
        $input = fgets(STDIN);

        //Send the message to the server
        if (!socket_sendto($sock, $input, strlen($input), 0, $server, $port)) {
            $errorcode = socket_last_error();
            $errormsg = socket_strerror($errorcode);

            die("Could not send data: [$errorcode] $errormsg \n");
        }

        //Now receive reply from server and print it
        if (socket_recv($sock, $reply, 2045, MSG_WAITALL) === FALSE) {
            $errorcode = socket_last_error();
            $errormsg = socket_strerror($errorcode);

            die("Could not receive data: [$errorcode] $errormsg \n");
        }

        echo "Reply : $reply";
    }
    ?>

TCP通讯方式：
============

服务器端：

.. literalinclude:: ./src/networkdemo/server.php
   :language: php

客户端：

.. literalinclude:: ./src/networkdemo/client.php
   :language: php

下面是其每一步骤的详细说明:

1. 第一步是建立两个变量来保存Socket运行的服务器的IP地址和端口.你可以设置为你自己的服务器和端口(这个端口可以是1到65535之间的数字),前提是这个端口未被使用。
2. 在服务器端可以使用set_time_out()函数来确保PHP在等待客户端连接时不会超时。
3. 在前面的基础上,现在该使用socket_creat()函数创建一个Socket了—这个函数返回一个Socket句柄,这个句柄将用在以后所有的函数中。

   .. code-block:: php

       <?php
	// 创建Socket
	$socket = socket_create(AF_INET, SOCK_STREAM, 0) or die("Could not create
	socket\n");
       ?>
   - 第一个参数”AF_INET”用来指定域名;
   - 第二个参数”SOCK_STREM”告诉函数将创建一个什么类型的Socket(在这个例子中是TCP类型);

   因此,如果你想创建一个UDP Socket的话,你可以使用如下的代码:

   .. code-block:: php

       <?php
	// 创建 socket
	$socket = socket_create(AF_INET, SOCK_DGRAM, 0) or die("Could not create
	socket\n");
       ?>

4. 一旦创建了一个Socket句柄,下一步就是指定或者绑定它到指定的地址和端口.这可以通过socket_bind()函数来完成。
5. 当Socket被创建好并绑定到一个端口后,就可以开始监听外部的连接了.PHP允许你由socket_listen()函数来开始一个监听,同时你可以指定一个数字(在这个例子中就是第二个参数:3)。
6. 到现在,你的服务器除了等待来自客户端的连接请求外基本上什么也没有做.一旦一个客户端的连接被收到,socket_accept()函数便开始起作用了,它接收连接请求并调用另一个子Socket来处理客户端–服务器间的信息。
7. 当一个连接被建立后,服务器就会等待客户端发送一些输入信息,这写信息可以由socket_read()函数来获得,并把它赋值给PHP的$input变量。socker_read的第而个参数用以指定读入的字节数,你可以通过它来限制从客户端获取数据的大小。

   注意:socket_read函数会一直读取壳户端数据,直到遇见\n,\t或者\0字符.PHP脚本把这写字符看做是输入的结束符.

8. 现在服务器必须处理这些由客户端发来是数据(在这个例子中的处理仅仅包含数据的输入和回传到客户端).这部分可以由socket_write()函数来完成(使得由通信socket发回一个数据流到客户端成为可能)。
9. 一旦输出被返回到客户端,父/子socket都应通过socket_close()函数来终止。

.. note:: 这里注意socket_read函数： 可选的类型参数是一个命名的常数：

 - PHP_BINARY_READ - 使用系统recv()函数。用于读取二进制数据的安全。 （在PHP>“默认= 4.1.0）
 - PHP_NORMAL_READ - 读停在\\n或\\r（在PHP <= 4.0.6默认）
 针对参数PHP_NORMAL_READ ，如果服务器的响应结果没有\\n。造成socket_read(): unable to read from socket。

udp通讯方式
===========

服务器端：

.. literalinclude:: ./src/networkdemo/udpserver.php
   :language: php

客户端：

.. literalinclude:: ./src/networkdemo/udpclient.php
   :language: php

PHP的Socket多进程编程
===================

守护进程
----------
php5.3自带了socket模块，使得php具有socket通信能力，具体api可以参考官方手册：http://php.net/manual/zh/book.sockets.php, 具体实现跟c非常类似，只是少了内存分配和网络字节序转换这种底层操作
同时，php的pcntl模块和posix模块配合可以实现基本的进程管理、信号处理等操作系统级别的功能。这里有两个非常关键的函数，pcntl_fork()和posix_setsid()。fork()一个进程，则表示创建了一个运行进程的副本，副本被认为是子进程，而原始进程被认为是父进程。当fork()运行之后，则可以脱离启动它的进程和终端控制等，也意味着父进程可以自由退出。pcntl_fork()返回值，-1表示执行失败，0表示在子进程中，大于0表示在父进程中。setsit()，它首先使新进程成为一个新会话的“领导者”，最后使进程不再控制终端。这也是成为守护进程最关键一步，这意味着，不会随着终端关闭而强制退出进程。对于一个不会被中断的常驻进程来说，这是很关键的一步。进行最后一次fork(),这一步不是必须的，但通常都这么做，它最大的意义是防止获得控制终端。

什么是守护进程？一个守护进程通常被认为是一个不对终端进行控制的后台任务。它有三个很明显的特征：

- 在后台运行
- 与启动他的进程脱离
- 无须终端控制

最常见的实现方法：fork() -> setsid() -> fork(), 代码里run_server()方法实现了守护进程。

.. code-block:: php

    <?php
    // 接受客户端请求，回复固定的响应内容
    function server_listen_socket ($address, $port) {
        $buffer = "Welcome to My First Server...";
        $len = strlen($buffer);

        // create, bind and listen to socket
        $socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
        if (!$socket) {
            echo "failed to create socket:" . socket_strerror($socket) . "\n";
            exit();
        }

        $bind_flag = socket_bind($socket, $address, $port);
        if (!$bind_flag) {
            echo "failed to bind socket:" . socket_strerror($bind_flag) . "\n";
            exit();
        }

        $backlog = 20;
        $listen_flag = socket_listen($socket, $backlog);
        if (!$listen_flag) {
            echo "failed to listen to socket:" . socket_strerror($listen_flag) . "\n";
            exit();
        }

        echo "waiting for clients to connect\n";

        while (1) {
            // 阻塞等待客户端连接
            if (($accept_socket = socket_accept($socket)) == FALSE) {
                continue;
            } else { // 如果连接成功，则立即响应客户端连接
                socket_write($accept_socket, $buffer, $len);
                // 关闭连接
                socket_close($accept_socket);
            }
        }
    }

    function run_server () {
        $pid1 = pcntl_fork();
        if ($pid1 == 0) {
            // first child process

            // 守护进程的一般流程：fork()->setsid()->fork()
            posix_setsid(); // 设置本子线程为会话组长

            // 再次创建子进程，该子进程为守护进程
            if (($pid2 = pcntl_fork()) == 0) {
                $address = "0.0.0.0";
                $port = "8080";
                // 守护进程负责接收客户端连接
                server_listen_socket($address, $port);
            } else {
                // 防止获得控制终端，该进程退出
                exit();
            }
        } else {
            // 父进程等待第一个子进程退出
            pcntl_wait($status);
        }
    }

    // server守护进程
    run_server();
    ?>

有限子进程的echo服务器
---------------------------

limited_forking_echo_server.php

.. code-block:: php

    <?php
    $server = stream_socket_server('tcp://0.0.0.0:8080', $errno, $errstr);
    if (false === $server) {
        fwrite(STDERR, "Failed creating socket server: $errstr\n");
        exit(1);
    }
    echo "Waiting…\n";
    const MAX_PROCS = 2;
    $children = [];
    for (;;) {
        $read = [$server];
        $write = null;
        $except = null;
        // 设置500毫秒的阻塞等待，防止频繁的轮询
        stream_select($read, $write, $except, 0, 500);
        // 处理读socket
        foreach ($read as $stream) {
            // 如果是服务器socket读事件，表示有连接，则开启新的子进程来处理连接
            if ($stream === $server && count($children) < MAX_PROCS) {
                // 会阻塞，放到这里应该是为了控制有限子进程来处理连接的客户端
                $conn = @stream_socket_accept($server, -1, $peer);
                if (!is_resource($conn)) {
                    continue;
                }
                echo "Starting a new child process for $peer\n";
                $pid = pcntl_fork();
                if ($pid > 0) {
                    $children[] = $pid; // 子进程pid加入数组中
                } elseif ($pid === 0) {
                    // Child process, implement our echo server
                    $childPid = posix_getpid();
                    fwrite($conn, "You are connected to process $childPid\n");
                    // 阻塞，直到有数据可以读取，继续运行，当客户端关闭时返回false
                    while ($buf = fread($conn, 4096)) {
                        fwrite($conn, $buf);
                    }
                    fclose($conn);
                    // We are done, quit.
                    exit(0);
                }
            }
        }
        // Do housekeeping on exited childs
        foreach ($children as $i => $child) {
            $result = pcntl_waitpid($child, $status, WNOHANG); // 无阻塞的等待子进程结束
            if ($result > 0 && pcntl_wifexited($status)) { // 如果是正常退出，
                unset($children[$i]); // 则清空数组中的子进程pid
            }
        }
        echo "\t".count($children)." connected\r";
    }
    ?>

使用有限的子进程来处理客户端连接，父进程负责循环接收客户端连接，如果小于指定的子进程，则创建子进程来处理客户端交互，否则，等待子进程结束，然后再创建子进程来处理客户端连接。这里处理客户端交互的子进程只处理了一次，就关闭了。

实现双向进程间通信管道(socketpair)
-----------------------------------------

.. code-block:: php

    <?php
    $server = stream_socket_server('tcp://0.0.0.0:' . (getenv('PORT') ?: 8080), $errno, $errstr);
    if (false === $server) {
        fwrite(STDERR, "Failed creating socket server: $errstr\n");
        exit(1);
    }
    echo "Waiting…\n";
    $children = [];
    // UNIX socket进行父子进程双向通信
    $pipe = stream_socket_pair(STREAM_PF_UNIX, STREAM_SOCK_STREAM, STREAM_IPPROTO_IP);
    for (; ;) {
        // 检查信号，执行信号处理函数
        //pcntl_signal_dispatch();
        $read = [$server, $pipe[0]]; // 监听server socket和pipe socket是否有数据
        $write = null;
        $except = null;
        // 阻塞
        stream_select($read, $write, $except,NULL);
        foreach ($read as $stream) {
            if ($stream === $server && count($children) < 2) {
                $conn = @stream_socket_accept($server, -1, $peer);
                if (!is_resource($conn)) {
                    continue;
                }
                echo "Starting a new child process for $peer\n";
                $pid = pcntl_fork();
                if ($pid > 0) {
                    $children[] = $pid;
                } elseif ($pid === 0) {
                    fclose($pipe[0]); // 子进程关闭pipe[0]管道
                    // Child process, implement our echo server
                    $childPid = posix_getpid();
                    fwrite($conn, "You are connected to process $childPid\n");
                    while ($line = fgets($conn, 4096)) {
                        if(trim($line) == 'quit')
                            break;
                        fwrite($conn, $line);
                    }
                    //  shutdown( socket, SHUT_WR)发送FIN
                    stream_socket_shutdown($conn,STREAM_SHUT_RDWR);
                    fclose($conn);
                    // Tell the parent that we are done
                    fwrite($pipe[1], "$childPid\n"); //通过pipe管道与父进程通信
                    // We are done, quit.
                    exit(0);
                }
            } elseif ($stream === $pipe[0]) { // 子进程发送消息给父进程
                $result = fgets($pipe[0]);
                if ($result === '0') { // 这个怎么来的？？
                    foreach ($children as $i => $pid) {
                        $wait = pcntl_waitpid($pid, $status, WNOHANG);
                        if ($wait > 0 && pcntl_wifexited($status)) {
                            unset($chilren[$i]);
                        }
                    }
                } else { // 获取子进程号
                    foreach ($children as $i => $pid) {
                        if ((string)$pid == (string)$result) {
                            unset($children[$i]);
                        }
                    }
                }
            }
        }
        echo count($children) . " connected\n";
    }
    ?>

上面存在一个问题：多个子进程共享同一个管道socket。

工作线程池来处理socket
---------------------------

.. code-block:: php

    <?php
    $server = stream_socket_server('tcp://0.0.0.0:' . (getenv('PORT') ?: 8080), $errno, $errstr);
    if (false === $server) {
        fwrite(STDERR, "Failed creating socket server: $errstr\n");
        exit(1);
    }
    $maxProcs = getenv('MAX_PROCS') ?: 2;
    $workers = [];
    function create_worker ($server) {
        $pid = pcntl_fork(); // 创建子进程
        if ($pid > 0) {
            return $pid;
        } elseif ($pid < 0) {
            return -1;
        } else {
            for (; ;) {
                // 阻塞，每个子进程都从$server socket获取连接，然后处理客户端交互
                $conn = @stream_socket_accept($server, -1, $peer);
                if (!is_resource($conn)) {
                    continue;
                }
                $childPid = posix_getpid();
                fwrite($conn, "You are connected to process $childPid\n");
                while ($buf = fread($conn, 4096)) {
                    fwrite($conn, $buf);
                }
                fclose($conn);
            }
            exit(0);
        }
    }

    for (; ;) {
        // Check if workers are alive
        foreach ($workers as $i => $worker) {
            $result = pcntl_waitpid($worker, $status, WNOHANG);
            if ($result > 0 && (pcntl_wifexited($status) || pcntl_wifsignaled($status))) {
                echo "Worker $worker has died :(\n";
                unset($workers[$i]);
            }
        }
        // Respawn lost workers
        while (($maxProcs - count($workers)) > 0) {
            echo "Creating worker process\n";
            $workers[] = create_worker($server);
        }
        usleep(500);
    }
    ?>

https://github.com/phpsphb/book-examples