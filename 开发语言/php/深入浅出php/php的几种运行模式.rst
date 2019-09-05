****************
php的几种运行模式
****************

CLI
===
CLI：就是命令行，例如可以在控制台或者是 shell 中键入命令:

.. code-block:: shell

    php -f index.php

CGI
===
CGI：以下是不同的说法与理解

公共网关接口”(Common Gateway Interface)，HTTP服务器与你的或其它机器上的程序进行 “交谈”的一种工具 ，其程序须运行在网络服务器上。在服务器环境中，为“程序 ”提供标准 的接口，通过这个接口，“程序 ”可以对服务器与客户端交换的信息做一些事情 。“程序 ”的语 言并没有要求。程序对接口进行操作。服务器要支持 CGI 就要提供 CGI 中要求的环境变量，或者还有别的。

HTTP Server 和一个独立的进程之间的协议，把 HTTP Request 的 Header 设置成进程的环境变量，HTTP Request 的正文设置成进程的标准输入，而进程的标准输出就是 HTTP Response 包括 Header 和正文。

这个 Web 服务器使用了 UNIX shell 环境变量来保存从 Web 服务器传递出去的参数，然后生成一个运行 CGI (php-cgi.exe)的独立进程。

不同类型语言写的程序只要符合 cgi 标准，就能作为一个 cgi 程序与 web 服务器交互

**个人理解:CGI规定了php与web server交流的规则，相当于执行了response = exec("php -f index.php -url=xxx -cookie=xxx -xxx=xxx")。**

以 CGI 方式运行 PHP ，意味着 apache 需要知道 PHP 执行文件的位置，然后 apache 才能运行 PHP ，当我们浏览一个页面时， apache 会去调用 php 可执行文件来解析，这时 PHP 需要读取配置文件(php.ini)，加载配置文件中启用的所有扩展模块，然后开始解析 php 脚本，每次浏览页面都要重复上面的动作（读取配置文件->加载扩展模块->解析脚本）。

mod_php
=======
即 apache 的 php 模块，将 PHP 做为 web-server 的子进程控制，两者之间有从属关系。最明显的例子就是在 CGI 模式下，如果修改了 PHP 。 INI 的配置文件，不用重启 web 服务便可生效，而模块模式下则需要重启 web 服务。以 mod_php 模式运行 PHP ，意味着 php 是作为 apache 的一个模块来启动的，因此只有在 apache 启动的时候会读取 php.ini 配置文件并加载扩展模块，在 apache 运行期间是不会再去读取和加载扩展模块的。（这也是为什么当我们以这种模式运行 PHP 时每次修改 php.ini 的配置信息都需要重启 apache 来使配置生效）

**个人理解:这种模式把php嵌入到apache中，相当于给apache加入了解析php文件的功能。以模块加载的方式运行，其实就是将PHP集成到Apache服务器，以同一个进程运行。**

FastCGI
=======
CGI 有很多缺点，每接收一个请求就要 fork 一个进程处理，只能接收一个请求作出一个响应。请求结束后该进程就会结束。而 FastCGI 会事先启动起来，作为一个 cgi 的管理服务器存在，预先启动一系列的子进程来等待处理，然后等待 web 服务器发过来的请求，一旦接受到请求就交由子进程处理，这样由于不需要在接受到请求后启动 cgi ，会快很多。 FastCGI 使用进程/线程池来处理一连串的请求。这些进程/线程由 FastCGI 服务器管理，而不是 Web 服务器。 当进来一个请求时， Web 服务器把环境变量和这个页面请求通过一个 Socket 长连接传递给 FastCGI 进程。 FastCGI 像是一个常驻型的 CGI ，它可以一直执行，在请求到达时不会花费时间去 fork 一个进程来处理(这是 CGI 对位人诟病的 fork-and-execute 模式)。正是因为它只是一个通信协议，它还支持分布式的运算，即 FastCGI 程序可以在网站服务器以外的主机上执行并且接受来自其他网站服务器的请求。

FastCGI整个流程
---------------

1. Web server启动时载入 FastCGI 进程管理器；
2. FastCGI 自身初始化，启动多个 CGI 解释器进程(可见多个php-cgi)并等待来自Web server的请求；
3. 当请求 Web server 时，Web server 通过 socket 请求 FastCGI 进程管理器， FastCGI 进程管理器选择并连接到一个 CGI 解释器， Web server 将 CGI 环境变量和标准输入发送到 FastCGI 子进程 php-cgi ；
4. FastCGI 子进程处理请求完成后将标准输出和错误从同一连接返回给 Web server ，当 FastCGI 子进程结束后请求便结束。 FastCGI 子进程接着等待处理来自 FastCGI 进程管理器的下一个连接，在 CGI 模式中， php-cgi 在此便退出了；

在上述情况中，你可以想象CGI通常有多慢。每一个Web请求PHP都必须重新解析php.ini、重新载入全部扩展并重初始化全部数据结构。使用FastCGI，所有这些都只在进程启动时发生一次。一个额外的好处是，持续数据库连接(Persistent database connection)可以工作。

FastCGI与CGI特点
----------------

1. 如CGI，FastCGI也具有语言无关性。
2. 如CGI， FastCGI在进程中的应用程序，独立于核心web服务器运行，提供了一个比API更安全的环境。(APIs把应用程序的代码与核心的web服务器链接在一起，这意味着在一个错误的API的应用程序可能会损坏其他应用程序或核心服务器; 恶意的API的应用程序代码甚至可以窃取另一个应用程序或核心服务器的密钥。)
3. FastCGI技术目前支持语言有：C/C++、Java、Perl、Tcl、Python、SmallTalk、Ruby等。相关模块在Apache， ISS， Lighttpd等流行的服务器上也是可用的。
4. 如CGI，FastCGI的不依赖于任何Web服务器的内部架构，因此即使服务器技术的变化， FastCGI依然稳定不变。

.. note:: **因为当使用 Zend Studio 调试程序时，由于 FastCGI 会认为 PHP 进程超时，从而在页面返回 500 错误。**

PHP-CGI
=======
PHP-CGI是PHP自带的FastCGI管理器。

启动PHP-CGI，使用如下命令：

.. code-block:: shell

    php-cgi -b 127.0.0.1:9000

PHP-CGI的不足
-------------

1. php-cgi 变更 php.ini 配置后需重启 php-cgi 才能让新的 php-ini 生效，不可以平滑重启；
2. 直接杀死 php-cgi 进程， php 就不能运行了。(PHP-FPM 和 Spawn-FCGI 就没有这个问题，守护进程会平滑从新生成新的子进程。）

PHP-FPM
=======
PHP-FPM 是一个 PHP FastCGI 管理器，是只用于 PHP 的，可以在 http://php-fpm.org/download 下载得到。

PHP-FPM 其实是 PHP 源代码的一个补丁，旨在将 FastCGI 进程管理整合进 PHP 包中。必须将它 patch 到你的 PHP 源代码中，在编译安装 PHP 后才可以使用。

现在我们可以在最新的PHP 5.3.2的源码树里下载得到直接整合了 PHP-FPM 的分支，据说下个版本会融合进 PHP 的主分支去。相对 Spawn-FCGI ， PHP-FPM 在 CPU 和内存方面的控制都更胜一筹，而且前者很容易崩溃，必须用 crontab 进行监控，而 PHP-FPM 则没有这种烦恼。

PHP5.3.3 已经集成 php-fpm 了，不再是第三方的包了。 PHP-FPM 提供了更好的 PHP 进程管理方式，可以有效控制内存和进程、可以平滑重载 PHP 配置，比 spawn-fcgi 具有更多有点，所以被 PHP 官方收录了。在 ./configure 的时候带 --enable-fpm 参数即可开启 PHP-FPM 。

使用 PHP-FPM 来控制 PHP-CGI 的 FastCGI 进程

.. code-block:: shell

	/usr/local/php/sbin/php-fpm{start|stop|quit|restart|reload|logrotate}
    --start 启动php的fastcgi进程
    --stop 强制终止php的fastcgi进程
    --quit 平滑终止php的fastcgi进程
    --restart 重启php的fastcgi进程
    --reload 重新平滑加载php的php.ini
    --logrotate 重新启用log文件

Spawn-FCGI
==========
Spawn-FCGI是一个通用的FastCGI管理服务器，它是lighttpd中的一部份，很多人都用Lighttpd的Spawn-FCGI进行FastCGI模式下的管理工作，不过有不少缺点。而PHP-FPM的出现多少缓解了一些问题，但PHP-FPM有个缺点就是要重新编译，这对于一些已经运行的环境可能有不小的风险(refer)，在php 5.3.3中可以直接使用PHP-FPM了。

Spawn-FCGI目前已经独成为一个项目，更加稳定一些，也给很多Web 站点的配置带来便利。已经有不少站点将它与nginx搭配来解决动态网页。

下面我们就可以使用Spawn-FCGI来控制php-CGI的FastCGI进程了

.. code-block:: shell

    /usr/local/bin/spawn-fcgi -a 127.0.0.1 -p 9000 -C 5 -u www-data -g www-data -f /usr/bin/php-CGI

	参数含义如下:

	-f 指定调用FastCGI的进程的执行程序位置，根据系统上所装的PHP的情况具体设置
	-a 绑定到地址addr
	-p 绑定到端口port
	-s 绑定到unix socket的路径path
	-C 指定产生的FastCGI的进程数，默认为5(仅用于PHP)
	-P 指定产生的进程的PID文件路径
	-u和-g FastCGI使用什么身份(-u 用户 -g 用户组)运行，Ubuntu下可以使用www-data，其他的根据情况配置，如nobody、apache等

PHP-FPM、Spawn-FCGI都是守护php-cgi的进程管理器。

参考：

- https://www.cnblogs.com/52php/p/5668823.html
- https://www.cnblogs.com/52php/p/5668824.html