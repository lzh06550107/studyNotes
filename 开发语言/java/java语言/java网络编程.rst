************
java网络编程
************

网络基础
========



Socket用法详解
==============


构造Socket
----------
``Socket`` 的构造方法有以下几种重载方式：

1. Socket();
2. Socket(InetAddress address, int port) throws UnknownHostException, IOException
3. Socket(InetAddress address, int port, InetAddress localAddr, int localPort) throws IOException
4. Socket(String host, int port) throws UnknownHostException, IOException
5. Socket(String host, int port, InetAddress localAddr, int localPort) throws IOException

除了第一个不带参数的构造方法之外，其他的构造方法都会试图建立与服务器的连接，如果连接成功，就返回 ``Scoket`` 对象；如果因为某些原因连接失败，就会抛出 ``IOException`` 。

设定等待建立连接的超时时间
^^^^^^^^^^^^^^^^^^^^^^^^
默认情况下， ``Scoket`` 构造方法与服务器连接时会一直等待下去，直到连接成功，或者出现异常。设置超时后，可能就会抛出 ``SocketTimeoutException`` 。

.. code-block:: java

Socket socket = new Socket();
SocketAddress remoteAddress = new InetSocketAddress("localhost", 8000);
socket.connect(remoteAddress, 6000);//等待建立连接的超时时长为1分钟

``Socket`` 类的 ``connect(SocketAddress endpoint, int timeout)`` 方法负责连接服务器，参数 ``endpoint`` 指定服务器的地址，参数 ``timeout`` 设定超时时间，以毫秒为单位。如果参数 ``timeout`` 设为 ``0`` ，表示永远不会超时。

设定服务器的地址
^^^^^^^^^^^^^^^
除了第一个不带参数的构造方法，其他构造方法都需要在参数中设定服务器的地址，包括服务器的IP地址或主机名，以及端口：　

.. code-block:: java

	Socket(InetAddress address, int port) // 第一个参数address表示主机的IP地址
	Socket(String host, int port) // 第一个参数host表示主机的名称

InetAddress类表示服务器的IP地址，InetAddress类提供了一系列静态工厂方法，用于构造自身的实例，例如：

.. code-block:: java

	//返回本地主机的IP地址
	InetAddress addr1=InetAddress.getLocalHost();
	//返回代表"222.34.5.7"的IP地址
	InetAddress addr2=InetAddress.getByName("222.34.5.7");
	//返回域名为"www.cnblogs.com"的IP地址
	InetAddress addr3=InetAddress.GetByName("www.cnblogs.com");

设定客户端的地址
^^^^^^^^^^^^^^^
设定服务端地址没什么可说的，要连接什么地方要说清楚的。默认情况下，客户端的IP地址来自于客户端程序所在的主机，客户端的端口则由系统随机分配，但是 ``socket`` 中有两个构造方法是显示的设置客户端的 ``IP`` 地址和端口，这样是有些主机同时属于两个以上的网络，可能拥有两个以上的 ``IP`` 地址，这样就就可以区分出来了。

.. code-block:: java

	//参数localAddress和localPort用来设置客户端的IP地址和端口。
	Socket(InetAddress address,int port,InetAddress localAddr,int localPort)throws IOException
	Socket(String host,int port,InetAddress localAddr,intlocalPort)throws IOException

如果一个主机同时属于两个以上的网络，他就可能拥有两个以上的 ``IP`` 地址。例如，一个主机在 ``Internet`` 网络中的 ``IP`` 地址为 ``222.67.1.34`` ，在一个局域网中的 ``IP`` 为 ``112.5.4.3`` 。假设这个主机上的，客户端程序希望和同一个局域网上的一个服务器程序通信，客户可按照如下方式构造 ``Socket`` 对象：

.. code-block:: java

	InetAddress remoteAddr=InetAddress.getByName("112.5.4.45")
	InetAddress localAddr=InetAddress.getByName("112.5.4.3")
	Socketsocket=new Socket(remoteAddr,8000,localAddr,2345)//客户端使用端口2345

客户连接服务器时可能抛出的异常
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
当Socket的构造方法请求连接服务器是，可能会抛出下面的异常。

- ``UnknownHostException`` ：如果无法识别主机的名字或者 ``IP`` 地址，就会抛出这种异常。
- ``ConnectException`` ：如果没有服务器进程监听指定的端口，或者服务器进程拒绝连接，就会抛出这种异常。
- ``SocketException`` ：如果等待连接超时就会抛出这种异常。
- ``BindException`` ：如果无法把 ``Socket`` 对象与指定的本机 ``IP`` 地址或端口绑定，就会抛出这种异常。

以上都是 ``IOException`` 的直接或者间接子类。

获取Socket的信息
----------------
在一个 ``Socket`` 对象中同时包含了远程服务器的 ``IP`` 地址和端口信息，以及客户端本地的 ``IP`` 地址和端口信息。想要通信，必然还要有输入输出流，``getInputStream()`` 和 ``getOutputStream()`` ，这两个方法在 ``Socket`` 还没要连接，或者已经关闭，或者已经通过 ``shutdownInput()`` 或者 ``shutdownOutput()`` ，就会抛出 ``IOException`` 。

以下方法用于获取 ``Socket`` 的有关信息。

- ``getInetAddress()`` ：获得远程服务器的 ``IP`` 地址。
- ``getPort()`` ：获得远程服务器的端口。
- ``getLocalAddress()`` ：获得客户本地的 ``IP`` 地址。
- ``getInputStream()`` ：获得输入流，如果 ``Socket`` 还没有连接，或者已经关闭，或者已经通过 ``shutdownInput()`` 方法关闭输入流，那么此方法会抛出 ``IOException`` 。
- ``getOutputStream()`` ：获得输出流。如果 ``Socket`` 还没有连接，或者已经关闭，或者已经通过 ``shutdownOutput()`` 方法关闭输出流，那么此方法会抛出 ``IOException`` 。

关闭Socket
----------
当客户与服务器的通信结束，应当及时关闭 ``Socket`` ，以释放 ``Socket`` 占用的包括端口在内的各种资源。 ``Socket`` 的 ``close()`` 方法负责关闭 ``Socket`` 。当一个 ``Socket`` 对象被关闭。就不能再通过它的输入流和输出流进行 ``I/O`` 操作，否则会导致 ``IOException`` 。

为了确保关闭 ``Socket`` 的操作总是被执行，强烈建议吧这个操作放在 ``finally`` 代码块中。

``Socket`` 类提供了3个状态测试的方法：

- ``isClose()`` ：如果 ``Socket`` 已经连接到了远程主机，并且还没关闭，则返回 ``true`` ，否则返回 ``false`` 。
- ``isConnected()`` ：如果 ``Socket`` 曾经连接到远程主机，则返回 ``true`` ，否则返回 ``false`` 。
- ``isBound()`` ：如果 ``Socket`` 已经与一个本地端口绑定，则返回 ``true`` ，否则返回 ``false`` 。

如果要判断一个 ``Socket`` 对象当前是否处于连接状态，可以采用以下方式：

.. code-block:: java

    boolean isConnected=socket.isConnected()&&!socket.isClosed();　

半关闭Socket
-----------
进程A与进程B通过 ``socket`` 通讯，假定进程A输出数据，进程B读入数据，进程A如何告诉进程B所有数据已经输出完毕。

1. 当进程A与进程B交换的是字节流，并且都是一行一行读写数据，可以事先约定以一个特殊标志作为结束标志。
  如以字符串"bye"作为结束标志，当进程A向进程B发送一行字符串"bye",进程B读到这行数据后就停止读取数据。
2. 进程A先发送一个消息，告诉线程B所发送的正文长度，然后再发送正文。进程B先获知进程A将发送的正文长度，接下来只需要读完改长度的字符串就停止读取数据。
3. 进程A发送完数据后，关闭 ``socket`` 。当进程B读入了进程A发送的所有数据后，再次执行输入流的read()方法时，该方法返回-1。如果执行BufferedReader的readLine()方法，则返回null。
4. 当调用Socket的close()方法关闭Socket时，它的输出流和输入流也都被关闭。有的时候，可能仅仅希望关闭输出流或输入流之一。此时可以采用Socket类提供的半关闭方法。

-　shutdownInput():关闭输入流，但输出流还可以读取。
-　shutdownOuput():关闭输出流，但输入流还可以发送

当进程A发送数据后关闭输出流，进程B读入所有数据后，就会读到输入流的末尾。

值得注意的是，先后调用Socket的shutdownInput()和shutdownOutput()方法，仅仅关闭了输入流和输出流，并不等价于调用Socket的close()方法。在通信结束后，仍然要调用Socket的close()方法，因为只有该方法才会释放Socket占用的资源，如端口。

Socket还提供了两种方法来测试输入流和输出流是否关闭：

- ``public boolean isInputShutdown()`` :输入流关闭成功返回 ``true`` ，否则 ``false`` ；
- ``public　boolean　isOutputShutdown()`` :输出流关闭成功返回 ``true`` ，否则 ``false`` ；

设置Socket的选项
---------------

TCP_NODELAY选项
^^^^^^^^^^^^^^^

设置该选项： public void setTcpNoDelay(boolean on) throws SocketException
读取该选项： public boolean getTcpNoDelay() throws SocketException

默认情况下，发送数据采用 ``Negale`` 算法, ``Negale`` 算法是值发送方发送数据不会立刻发出,而是先放在缓存里，等缓冲区满了再发出。 ``Negale`` 算法适用于发送方需要发送大批数据，并且接收方会及时作出回应的场合，这种算法通过减少传输数据的次数来提高通信效率。
如果发送小批量数据，并且接收方不一定会立即发送响应数据，那么 ``Negale`` 算法反而会使发送方运行慢，对于GUI程序，如网络游戏(服务端需要实时跟踪客户端鼠标的移动)，客户端鼠标位置移动信息需要实时发送到服务端，由 ``Negale`` 采用缓存，大大降低实时响应速度，导致客户端程序运行很慢。

``TCP_NODELAY`` 的默认值为 ``false`` ,表示采用 ``Negale`` 算法，调用 ``setTcpNoDelay(true)`` 方法，会关闭 ``socket`` 的缓存,确保数据及时发送:

.. code-block:: java

    if(!socket.getTcpNoDelay()) socket.setTcpNoDelay(true);


如果 ``socket`` 底层不支持 ``TCP_NODELAY`` 选项，调用该方法是回抛出 ``SocketException``

SO_REUSEADDR选项
^^^^^^^^^^^^^^^^
SO_REUSEADDR  是否允许重用socket所绑定的本地地址

设置该选项： public void setResuseAddress(boolean on) throws SocketException
读取该选项： public boolean getResuseAddress() throws SocketException

当接收方通过socket close方法关闭socket时，如果网络上还有发送到这个socket数据，底层socket不会立即释放本地端口，而是等待一段时间，确保接收到了网络上发送过来的延迟数据，然后在释放端口。socket接收到延迟数据后，不会对这些数据作任何处理。socket接收延迟数据目的是确保这些数据不会被其他碰巧绑定到同样的端口的新进程收到。
客户端一般采用随机端口，因此出现两个客户端绑定到同样的端口可能性不大，而服务器端都是使用固定端口，当服务器端程序关闭后，有可能他的端口还会被占用一段时间，如果此时立刻在此主机上重启服务器程序，由于服务器端口被占用，使得服务器程序无法绑定改端口，启动失败。
为了确保一个进程关闭socket后，即使它还没释放端口，同一主机上的其他进程可以立刻重用该端口，可以调用socket的setReuseAddress(true) 
需要注意的是setReuseAddress(boolean on)方法必须在socket还未绑定到一个本地端口之前调用，否则无效 


TCP_NODELAY选项
^^^^^^^^^^^^^^^


SO_RESUSEADDR选项
^^^^^^^^^^^^^^^^^


SO_TIMEOUT选项
^^^^^^^^^^^^


SO_LINGER选项
^^^^^^^^^^^


SO_RCVBUF选项
^^^^^^^^^^^


SO_SNDBUF选项
^^^^^^^^^^^^^


SO_KEEPALIVE选项
^^^^^^^^^^^^^^


OOBINLINE选项
^^^^^^^^^^^


服务类型选项
^^^^^^^^^^^


设定连接时间、延迟和带宽的相对重要性
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

发送邮件的SMTP客户程序
---------------------



ServerSocket用法详解
====================


构造ServerSocket
----------------


接收和关闭与客户的连接
--------------------


关闭ServerSocket
----------------


获取ServerSocket的信息
---------------------


ServerSocket选项
----------------


创建多线程的服务器
-----------------


关闭服务器
---------



非阻塞通信
==========


线程阻塞的概念
-------------


Java.nio包中的主要类
-------------------


服务器编程范例
-------------


客户端编程范例
-------------
