**********
从源码构建nginx
**********

此构建是使用 ``configure`` 命令来进行配置的。它定义了系统的各个层面，包含了 ``nginx`` 在连接处理中允许使用的方法。最后它创建了一个 ``Makefile`` 文件。

``configure`` 命令支持以下参数:

- ``--help`` 打印帮助信息。

- ``--prefix=path`` 指定保存服务器文件的目录。该目录也被用在所有被 ``configure`` （除了源码库的路径）和 ``nginx.conf`` 配置文件中设置的相对路径。它默认被设置为 ``/usr/local/nginx`` 目录。

- ``--sbin-path=path`` 设置 ``nginx`` 可执行文件的名称。这个路径只在安装过程中使用。该文件默认被命名为 ``/prefix/sbin/nginx`` 。

- ``--conf-path=path`` 设置 ``nginx.conf`` 配置文件的名称。如果需要的话，通过在命令行参数 ``-c file`` 中指定它， ``nginx`` 就可以以不同的配置文件启动。该文件默认被命名为 ``prefix/conf/nginx.conf`` 。

- ``--pid-path=path`` 设置保存主进程的进程ID的一个 ``nginx.pid`` 文件的名称。安装后，该文件名可以在 ``nginx.conf`` 配置文件使用 ``pid`` 指令来修改。该文件默认被命名为 ``prefix/log/nginx.pid`` 。

- ``--error-log-path=path`` 设置主要错误、警告和诊断文件的名称。安装后，该文件名可以在 ``nginx.conf`` 配置文件中使用 ``error_log`` 指令来修改。该文件默认被命名为 ``prefix/logs/error.log`` 。

- ``--lock-path=path`` 设置锁文件名称的前缀。安装之后，可以在 ``nginx.conf`` 配置文件中使用 ``lock_file`` 指令来修改。默认值为 ``prefix/logs/nginx.lock`` 。

- ``--user=name`` 设置一个其凭证将被工作进程使用的无特权的用户的名称。安装后，该名称可以 ``nginx.conf`` 配置文件中使用 ``user`` 指令来修改。默认的用户名是 ``nobody`` 。

- ``--group=name`` 设置一个其凭证将被工作进程使用的组的名称。安装后，该名称可以在 ``nginx.conf`` 配置文件中使用 ``user`` 指令来修改。默认的组名设置为一个无特权的用户的名称。

- ``--build=name`` 设置一个可选的 ``nginx`` 构建名。

- ``builddir=path`` 设置一个构建目录。


- --with-select_module
- --without-select_module

  允许或者禁止在构建时允许服务器使用 ``select()`` 方法一起工作的模块。如果平台没有像 ``kqueue`` 、 ``epoll`` 、或者 ``/dev/poll`` 方法，该模块将会自动构建。

- --with-poll_module
- --without-poll_module

  允许或者禁止在构建时允许服务器使用 ``poll()`` 方法一起工作的模块。如果平台没有像 ``kqueue`` 、 ``epoll`` 、或者 ``/dev/poll`` 方法，该模块将会自动构建。

- ``--with-threads`` 开启使用线程池。
- ``--with-file-aio`` 在 FreeBSD 和 linux 异步文件I/O (AIO)。
- ``--with-http_ssl_module`` 允许构建一个模块，将 ``HTTPS`` 协议支持添加到 ``HTTP`` 服务器。默认情况下不构建此模块。需要 ``OpenSSL`` 库来构建和运行此模块。
- ``--with-http_v2_module`` 可以构建一个支持 ``HTTP/2`` 的模块。默认情况下不构建此模块。
- ``--with-http_realip_module`` 允许构建 ``ngx_http_realip_module`` 模块，该模块将客户端地址更改为在头字段中发送的指定的地址。默认情况下不构建此模块。
- ``--with-http_addition_module`` 允许构建 ``ngx_http_addition_module`` 模块，该模块在响应之前和之后添加文本。默认情况下不构建此模块。

- ``--with-http_xslt_module``
- ``--with-http_xslt_module=dynamic``

  允许构建使用一个或多个 ``XSLT`` 样式表转换 ``XML`` 响应的 ``ngx_http_xslt_module`` 模块。默认情况下不构建此模块。需要 ``libxml2`` 和 ``libxslt`` 库来构建和运行此模块。

- ``--with-http_image_filter_module``
- ``--with-http_image_filter_module=dynamic``

  允许构建 ``ngx_http_image_filter_module`` 模块，该模块可以转换 ``JPEG`` ， ``GIF`` ， ``PNG`` 和 ``WebP`` 格式的图像。默认情况下不构建此模块。

- ``--with-http_geoip_module``
- ``--with-http_geoip_module=dynamic``

  允许构建 ``ngx_http_geoip_module`` 模块，该模块根据客户端IP地址和预编译的 ``MaxMind`` 数据库创建变量。默认情况下不构建此模块。

- ``--with-http_sub_module`` 允许构建 ``ngx_http_sub_module`` 模块，该模块通过将一个指定的字符串替换为另一个来修改响应。默认情况下不构建此模块。

- ``--with-http_dav_module`` 支持构建 ``ngx_http_dav_module`` 模块，该模块通过 ``WebDAV`` 协议提供文件管理自动化。默认情况下不构建此模块。
- ``--with-http_flv_module`` 支持构建 ``ngx_http_flv_module`` 模块，该模块为 Flash Video（FLV） 文件提供伪流服务器端支持。默认情况下不构建此模块。
- ``--with-http_mp4_module`` 支持构建 ``ngx_http_mp4_module`` 模块，该模块为 ``MP4`` 文件提供伪流服务器端支持。默认情况下不构建此模块。
- ``--with-http_gunzip_module`` 对于不支持 ``gzip`` 编码方法的客户端，可以构建解压缩使用 ``Content-Encoding：gzip`` 响应的 ``ngx_http_gunzip_module`` 模块。默认情况下不构建此模块。
- ``--with-http_gzip_static_module`` 允许构建 ``ngx_http_gzip_static_module`` 模块，该模块允许使用“.gz”文件扩展名而不是常规文件发送预压缩文件。默认情况下不构建此模块。
- ``--with-http_auth_request_module`` 允许构建 ``ngx_http_auth_request_module`` 模块，该模块基于子请求的结果实现客户端授权。默认情况下不构建此模块。
- ``--with-http_random_index_module`` 允许构建 ``ngx_http_random_index_module`` 模块，该模块处理以斜杠字符（'/'）结尾的请求，并选择目录中的随机文件作为 ``index`` 文件。默认情况下不构建此模块。
- ``--with-http_secure_link_module`` 可以构建 ``ngx_http_secure_link_module`` 模块。默认情况下不构建此模块。
- ``--with-http_degradation_module`` 可以构建 ``ngx_http_degradation_module`` 模块。默认情况下不构建此模块。
- ``--with-http_slice_module`` 允许构建将请求拆分为子请求的 ``ngx_http_slice_module`` 模块，每个模块都返回一定范围的响应。该模块提供了更有效的大响应缓存。默认情况下不构建此模块。
- ``--with-http_stub_status_module`` 可以构建 ``ngx_http_stub_status_module`` 模块，该模块提供对基本状态信息的访问。默认情况下不构建此模块。
- ``--without-http_charset_module`` 禁用构建 ``ngx_http_charset_module`` 模块，该模块将指定的字符集添加到“Content-Type”响应头字段，并且还可以将数据从一个字符集转换为另一个字符集。
- ``--without-http_gzip_module`` 禁止构建压缩响应的 ``HTTP`` 服务器模块。构建和运行该模块需要 ``zlib`` 库。
- ``--without-http_ssi_module`` 禁用构建 ``ngx_http_ssi_module`` 模块，该模块在通过它的响应中处理 ``SSI`` （服务器端包含）命令。
- ``--without-http_userid_module`` 禁用构建 ``ngx_http_userid_module`` 模块，该模块设置适合客户端识别的 ``cookie`` 。
- ``--without-http_access_module`` 禁用构建 ``ngx_http_access_module`` 模块，该模块允许限制对某些客户端地址的访问。
- ``--without-http_auth_basic_module`` 禁用构建 ``ngx_http_auth_basic_module`` 模块，该模块允许通过使用“HTTP基本身份验证”协议验证用户名和密码来限制对资源的访问。
- ``--without-http_mirror_module`` 禁用构建 ``ngx_http_mirror_module`` 模块，该模块通过创建后台子请求来实现原始请求。
- ``--without-http_autoindex_module`` 禁用构建 ``ngx_http_autoindex_module`` 模块，该模块处理以斜杠字符（'/'）结尾的请求，并在 ``ngx_http_index_module`` 模块找不到索引文件的情况下生成目录列表。
- ``--without-http_geo_module`` 禁用构建 ``ngx_http_geo_module`` 模块，该模块使用取决于客户端IP地址的值创建变量。
- ``--without-http_map_module`` 禁用构建 ``ngx_http_map_module`` 模块，该模块使用取决于其他变量值的值创建变量。
- ``--without-http_split_clients_module`` 禁用构建 ``ngx_http_split_clients_module`` 模块，该模块为 ``A/B`` 测试创建变量。
- ``--without-http_referer_module`` 禁用构建 ``ngx_http_referer_module`` 模块，该模块可以阻止对“Referer”头字段中具有无效值的请求访问站点。
- ``--without-http_rewrite_module`` 禁止构建允许服务器重定向请求和修改请求的URI的模块。构建和运行该模块需要 ``PCRE`` 库。
- ``--without-http_proxy_module`` 禁止构建 ``HTTP`` 服务器的代理模块。
- ``--without-http_fastcgi_module`` 禁用构建将请求传递给 ``FastCGI`` 服务器的 ``ngx_http_fastcgi_module`` 模块。
- ``--without-http_uwsgi_module`` 禁用构建将请求传递给 ``uwsgi`` 服务器的 ``ngx_http_uwsgi_module`` 模块。
- ``--without-http_scgi_module`` 禁用构建将请求传递给 ``SCGI`` 服务器的 ``ngx_http_scgi_module`` 模块。
- ``--without-http_grpc_module`` 禁用构建将请求传递给 ``gRPC`` 服务器的 ``ngx_http_grpc_module`` 模块。
- ``--without-http_memcached_module`` 禁用构建 ``ngx_http_memcached_module`` 模块，该模块从 ``memcached`` 服务器获取响应。
- ``--without-http_limit_conn_module`` 禁用构建 ``ngx_http_limit_conn_module`` 模块，该模块限制每个key的连接数，例如，来自单个 ``IP`` 地址的连接数。
- ``--without-http_limit_req_module`` 禁用构建 ``ngx_http_limit_req_module`` 模块，该模块限制每个key的请求处理速率，例如，来自单个 ``IP`` 地址的请求的处理速率。
- ``--without-http_empty_gif_module`` 禁用构建发出单像素透明GIF的模块。
- ``--without-http_browser_module`` 禁用构建 ``ngx_http_browser_module`` 模块，该模块创建的值的变量值取决于“User-Agent”请求标头字段的值。
- ``--without-http_upstream_hash_module`` 禁用构建实现散列负载均衡方法的模块。
- ``--without-http_upstream_ip_hash_module`` 禁用构建实现 ``ip_hash`` 负载均衡方法的模块。
- ``--without-http_upstream_least_conn_module`` 禁用构建实现 ``least_conn`` 负载平衡方法的模块。
- ``--without-http_upstream_keepalive_module`` 禁用构建一个模块，该模块提供到上游服务器的连接缓存。
- ``--without-http_upstream_zone_module`` 禁用构建模块，可以将上游组的运行时状态存储在共享内存区域中。

- ``--with-http_perl_module``
- ``--with-http_perl_module=dynamic``

  可以构建嵌入式 ``Perl`` 模块。默认情况下不构建此模块。

- ``--with-perl_modules_path=path`` 定义一个将保存 ``Perl`` 模块的目录。
- ``--with-perl=path`` 设置 ``Perl`` 二进制文件的名称。设置 ``PCRE`` 库的源码路径。该库的分发包（版本号4.4-8.40）需要从 ``PCRE`` 网站下载和提取。其余的由 ``nginx`` 的 ``./configure`` 和 ``make`` 完成。 ``location`` 指令的正则表达式支持和 ``ngx_http_rewrite_module`` 模块都需要改库。
- ``--http-log-path=path`` 设置 ``HTTP`` 服务器主要的请求日志文件的名称。安装后，该文件名可以在 ``nginx.conf`` 配置文件中使用 ``access_log`` 指令来修改。该文件默认被命名为 ``prefix/logs/access.log`` 。
- ``--http-client-body-temp-path=path`` 定义用于存储保存客户端请求主体的临时文件的目录。安装后，可以使用 ``client_body_temp_path`` 指令在 ``nginx.conf`` 配置文件中更改目录。默认情况下，该目录名为 ``prefix/client_body_temp`` 。
- ``--http-proxy-temp-path=path`` 定义一个目录，用于存储临时文件和从代理服务器接收的数据。安装后，可以使用 ``proxy_temp_path`` 指令在 ``nginx.conf`` 配置文件中更改目录。默认情况下，该目录名为 ``prefix/proxy_temp`` 。
- ``--http-fastcgi-temp-path=path`` 定义一个目录，用于存储从 ``FastCGI`` 服务器接收的数据的临时文件。安装后，可以使用 ``fastcgi_temp_path`` 指令在 ``nginx.conf`` 配置文件中始终更改目录。默认情况下，该目录名为 ``prefix/fastcgi_temp`` 。
- ``--http-uwsgi-temp-path=path`` 定义一个目录，用于存储从 ``uwsgi`` 服务器接收的数据的临时文件。安装后，可以使用 ``uwsgi_temp_path`` 指令在 ``nginx.conf`` 配置文件中始终更改目录。默认情况下，该目录名为 ``prefix/uwsgi_temp`` 。
- ``--http-scgi-temp-path=path`` 定义用于存储临时文件的目录，其中包含从 ``SCGI`` 服务器接收的数据。安装后，可以使用 ``scgi_temp_path`` 指令在 ``nginx.conf`` 配置文件中更改目录。默认情况下，该目录名为 ``prefix/scgi_temp`` 。
- ``--without-http`` 禁用 ``HTTP`` 服务器。
- ``--without-http-cache`` 禁用 ``HTTP`` 缓存。

- ``--with-mail``
- ``--with-mail=dynamic``

  启用 ``POP3/IMAP4/SMTP`` 邮件代理服务器。

- ``--with-mail_ssl_module`` 允许构建一个模块，将 ``SSL/TLS`` 协议支持添加到邮件代理服务器。默认情况下不构建此模块。需要 ``OpenSSL`` 库来构建和运行此模块。
- ``--without-mail_pop3_module`` 禁用邮件代理服务器中的 ``POP3`` 协议。
- ``--without-mail_imap_module`` 禁用邮件代理服务器中的 ``IMAP`` 协议。
- ``--without-mail_smtp_module`` 禁用邮件代理服务器中的 ``SMTP`` 协议。

- ``--with-stream``
- ``--with-stream=dynamic``

  允许构建流模块以进行通用 ``TCP/UDP`` 代理和负载平衡。默认情况下不构建此模块。

- ``--with-stream_ssl_module`` 可以构建一个模块，为流模块添加 ``SSL/TLS`` 协议支持。默认情况下不构建此模块。需要 ``OpenSSL`` 库来构建和运行此模块。
- ``--with-stream_realip_module`` 启用构建 ``ngx_stream_realip_module`` 模块，该模块将客户端地址更改为 ``PROXY`` 协议头中发送的地址。默认情况下不构建此模块。

- ``--with-stream_geoip_module``
- ``--with-stream_geoip_module=dynamic``

  允许构建 ``ngx_stream_geoip_module`` 模块，该模块根据客户端 ``IP`` 地址和预编译的 ``MaxMind`` 数据库创建变量。默认情况下不构建此模块。

- ``--with-stream_ssl_preread_module`` 允许构建 ``ngx_stream_ssl_preread_module`` 模块，该模块允许从 ``ClientHello`` 消息中提取信息而不终止 ``SSL/TLS`` 。默认情况下不构建此模块。
- ``--without-stream_limit_conn_module`` 禁用构建 ``ngx_stream_limit_conn_module`` 模块，该模块限制每个key的连接数，例如，来自单个 ``IP`` 地址的连接数。
- ``--without-stream_access_module`` 禁用构建 ``ngx_stream_core_module`` 模块，允许限制对某些客户端地址的访问。
- ``--without-stream_geo_module`` 禁用构建 ``ngx_stream_core_module`` 模块，该模块使用取决于客户端IP地址的值创建变量。
- ``--without-stream_map_module`` 禁用构建 ``ngx_stream_core_module`` 模块，该模块使用取决于其他变量值的值创建变量。
- ``--without-stream_split_clients_module`` 禁用构建 ``ngx_stream_split_clients`` 模块模块，该模块为 ``A/B`` 测试创建变量。
- ``--without-stream_return_module`` 禁用构建 ``ngx_stream_core_module`` 模块，该模块将一些特定值发送到客户端，然后关闭连接。
- ``--without-stream_upstream_hash_module`` 禁用构建实现散列负载平衡方法的模块。
- ``--without-stream_upstream_least_conn_module`` 禁用构建实现 ``least_conn`` 负载平衡方法的模块。
- ``--without-stream_upstream_zone_module`` 禁用构建模块，可以将上游组的运行时状态存储在共享内存区域中。
- ``--with-google_perftools_module`` 可以构建 ``ngx_google_perftools_module`` 模块，该模块可以使用 Google Performance Tools 分析 ``nginx`` 工作进程。该模块适用于 ``nginx`` 开发人员，默认情况下不构建。
- ``--with-cpp_test_module`` 用于构建 ``ngx_cpp_test_module`` 模块。
- ``--add-module=path`` 启用外部模块。
- ``--add-dynamic-module=path`` 启用外部动态模块。
- ``--with-compat`` 实现动态模块兼容性。
- ``--with-cc=path`` 设置C编译器的名称。
- ``--with-cc-opt=parameters`` 设置将添加到 ``CFLAGS`` 变量的其他参数。在 ``FreeBSD`` 下使用系统 ``PCRE`` 库时，应指定 ``--with-cc-opt="-I /usr/local/include"`` 。如果需要增加 ``select()`` 支持的文件数，也可以在此处指定，例如： ``-with-cc-opt="-D FD_SETSIZE = 2048"`` 。
- ``--with-ld-opt=parameters`` 设置将在链接期间使用的其他参数。在 ``FreeBSD`` 下使用系统 ``PCRE`` 库时，应指定 ``--with-ld-opt="-L /usr/local/lib"`` 。
- ``--with-cpu-opt=cpu`` 支持按指定 ``CPU`` 构建：pentium，pentiumpro，pentium3，pentium4，athlon，opteron，sparc32，sparc64，ppc64。
- ``--without-pcre`` 禁用 ``PCRE`` 库的使用。
- ``--with-pcre`` 强制使用 ``PCRE`` 库。
- ``--with-pcre=path`` 设置 PCRE 库源的路径。需要从 PCRE 站点下载库分发（版本4.4-8.42）并将其解压缩。剩下的工作由 ``nginx`` 的 ``./configure`` 和 ``make`` 完成。该位置指令和 ``ngx_http_rewrite_module`` 模块中的正则表达式支持需要该库。
- ``--with-cc-opt=parameters`` 设置将被添加到 ``CFLAGS`` 变量的额外参数。当在 ``FreeBSD`` 下使用系统 ``PCRE`` 库时，应该指定 ``--with-cc-opt="-I /usr/local/include"`` 。如果需要增加被 ``select()`` 支持的文件的数量，也可以像这样指定: ``--with-cc-opt="-D FD_SETSIZE=2048"`` 。
- ``--with-pcre-jit`` 使用“即时编译”支持来构建 ``PCRE`` 库（1.1.12，pcre_jit指令）
- ``--with-zlib=path`` 设置 ``zlib`` 库的源码路径。该库的发布包（版本号1.1.3-1.2.11）需要从 ``zlib`` 网站下载和提取。其余的由 ``nginx`` 的 ``./configure`` 和 ``make`` 完成。 ``ngx_http_gzip_module`` 模块需要该库。
- ``--with-zlib-opt=parameters`` 为 ``zlib`` 设置其他构建选项。
- ``--with-zlib-asm=cpu`` 允许使用针对其中一个指定 ``CPU`` 优化的 ``zlib`` 汇编程序源：pentium，pentiumpro。
- ``--with-libatomic`` 强制 ``libatomic_ops`` 库使用。
- ``--with-libatomic=path`` 设置 ``libatomic_ops`` 库源的路径。
- ``--with-openssl=path`` 设置 ``OpenSSL`` 库源的路径。
- ``--with-openssl-opt=parameters`` 为 ``OpenSSL`` 设置其他构建选项。
- ``--with-debug`` 启用调试日志。

参数使用范例（所有这些需要在一行输入）:

.. code-block:: shell

	./configure
	    --sbin-path=/usr/local/nginx/nginx
	    --conf-path=/usr/local/nginx/nginx.conf
	    --pid-path=/usr/local/nginx/nginx.pid
	    --with-http_ssl_module
	    --with-pcre=../pcre-8.42
	    --with-zlib=../zlib-1.2.11

配置完成后，使用 ``make`` 编译和安装 ``nginx`` 。