*************
nginx如何处理一个请求
*************

基于域名的虚拟服务器
===================
``Nginx`` 会决定使用哪一个服务器接收请求。下面让我们看一个简单的配置示例， 3 台虚拟服务器都监听着 80 端口。

.. code-block:: shell

	server {
	    listen      80;
	    server_name example.org www.example.org;
	    ...
	}

	server {
	    listen      80;
	    server_name example.net www.example.net;
	    ...
	}

	server {
	    listen      80;
	    server_name example.com www.example.com;
	    ...
	}

在上文的配置中， ``Nginx`` 根据请求头信息中的 ``Host`` 字段决定请求应该由哪一台服务器处理，如果 ``Host`` 字段的值没有相匹配的服务器或者请求中没有 ``Host`` 字段，那么 ``Nginx`` 会将请求路由至这个端口的默认服务器。在上文的配置中，默认的服务器是第一台，这是 ``Nginx`` 的默认策略。默认服务器是可以显式配置的，在 ``listen`` 指令使用 ``default_server`` 参数即可指定服务器为该端口的默认服务器。

.. code-block:: shell

	server {
	    listen      80 default_server;
	    server_name example.net www.example.net;
	    ...
	}

.. note:: default_server 参数自从0.8.21版本开始启用，在更早期的版本中，使用 default 参数替代。

值得注意的是，默认服务器是属于监听端口的，而不是服务器名称。更多关于这点的信息之后会提及。

如何避免处理未定义域名的请求
==========================
如果不允许出现未携带 ``Host`` 字段的请求，希望服务器直接将其丢弃，可以使用如下配置：

.. code-block:: shell

	server {
	    listen      80;
	    server_name "";
	    return      444;
	}

上述配置中， ``server_name`` 设置为空字符串，将匹配没有携带 ``Host`` 信息的请求，并且会返回 ``nginx`` 的非标准返回码 ``444`` 关闭连接。

.. note:: 自从0.8.48版本后，server_name "" 是默认配置，可以被省略了。在更早期的版本中，机器的域名被用作默认服务器名。


基于域名和基于IP地址的虚拟服务器
==============================
接下来看一个更复杂的配置示例，一些虚拟服务器监听着不同的地址：

.. code-block:: shell

	server {
	    listen      192.168.1.1:80;
	    server_name example.org www.example.org;
	    ...
	}

	server {
	    listen      192.168.1.1:80;
	    server_name example.net www.example.net;
	    ...
	}

	server {
	    listen      192.168.1.2:80;
	    server_name example.com www.example.com;
	    ...
	}

在此配置中， ``nginx`` 首先使用 ``server`` 块的 ``listen`` 指令的配置去测试请求的 ``ip`` 地址以及端口，然后使用 ``server_name`` 去测试 ``Host`` 字段如果请求匹配了 ``Ip`` 地址以及端口。如果域名没有找到，请求将由默认服务器处理。举个例子，在 ``192.168.1.1:80`` 端口收到了对于 ``www.example.com`` 的请求，因为没有为该端口定义 ``www.example.com`` 的 ``server_name`` ，请求将由 ``192.168.1.1:80`` 的默认服务器处理，即配置中的第一台服务器。

如之前所述，默认服务器是监听端口的属性之一，并且可以为不同的监听端口匹配不同的默认服务器。


简单php站点的配置
================
接下来了解 ``nginx`` 如何处理简单 ``php`` 站点的请求，访问到请求所选择的 ``location`` 。

.. code-block:: shell

	server {
	    listen      80;
	    server_name example.org www.example.org;
	    root        /data/www;

	    location / {
	        index   index.html index.php;
	    }

	    location ~* \.(gif|jpg|png)$ {
	        expires 30d;
	    }

	    location ~ \.php$ {
	        fastcgi_pass  localhost:9000;
	        fastcgi_param SCRIPT_FILENAME
	                      $document_root$fastcgi_script_name;
	        include       fastcgi_params;
	    }
	}

首先 ``nginx`` 会查找最明确的 ``location`` 前缀，并非根据配置中所定义的顺序。在上述配置中， "/" 是最通用的，能够匹配任何请求，会被作为最后的手段使用。然后 ``nginx`` 会按顺序使用配置中的 ``location`` 的正则表达式匹配地址。当匹配成功时， ``nginx`` 会停止查找并且使用该 ``location`` 。如果请求没有和任何正则表达式匹配成功，那么 ``nginx`` 会使用之前查找到的最明确的 ``location`` 前缀。

值得注意的是，所有类型的 ``location`` 配置只会和请求的 ``URI`` 部分进行匹配，不包括变量。这是因为请求中的变量可能会以好多种方式出现，如下所示:

.. code-block:: shell

	/index.php?user=john&page=1
	/index.php?page=1&user=john

任何人都可以在请求字符串中请求任意事物。

.. code-block:: shell

    /index.php?page=1&something+else&user=john

接下来了解上述配置是如何处理请求的:

- 请求 ``/logo.gif`` 首先由前缀 ``/`` 匹配，然后由正则表达式 ``\.(gif | jpg | png)$`` 匹配，因此它将由后一个 ``location`` 处理。使用指令 ``root /data/www`` 将请求映射到文件 ``/data/www/logo.gif`` ，并将文件发送到客户端。
- 请求 ``/index.php`` 首先由前缀 ``/`` 匹配，然后由正则表达式 ``\.(php)$`` 匹配。因此它将由后一个 ``location`` 处理，并且被转发到监听着 ``location:9000`` 端口的 ``FastCGI`` 服务器。 ``fastcgi_param`` 指令将 ``FastCGI`` 变量 ``SCRIPT_FILENAME`` 设置为 ``/data/www/index.php`` 然后 ``FastCGI`` 执行该文件。变量 ``$document_root`` 变量等于 ``root`` 指令的值，变量 ``$fastcgi_script_name`` 等于请求的 ``URI`` ，即 ``/index.php`` 。
- 请求 ``/about.html`` 只由前缀 ``/`` 匹配，因此将由该 ``location`` 处理。使用指令 ``root /data/www`` 将请求映射到文件 ``/data/www/about.html`` 然后发送到客户端。
- 处理请求 ``/`` 比较复杂。它只匹配了前缀 ``/`` ，因此由该 ``location`` 处理。然后 ``index`` 指令根据设置的变量以及 ``root /data/www`` 指令检查 ``index`` 文件是否存在。如果 ``/data/www/index.html`` 不存在， ``/data/www/index.php`` 存在，会内部重定向到 ``index.php`` ,然后 ``nginx`` 再次使用这个地址搜索合适的 ``location`` 。正如我们之前所看到的，重定向的请求最终会被 ``FastCGI`` 服务器处理。



