******
php7编译
******

安装编译php7时需要的依赖包
==========================

.. code-block:: shell

    yum -y install libxml2 libxml2-devel openssl openssl-devel curl curl-devel libjpeg libjpeg-devel libpng libpng-devel freetype freetype-devel pcre pcre-devel libxslt libxslt-devel bzip2 bzip2-devel systemd systemd-devel

创建php-fpm运行账号
===================

.. code-block:: shell

    groupadd www-data
    useradd -g www-data www-data

配置编译选项
============

.. code-block:: shell

    ./configure --prefix=/usr/local/php7.2.5 --with-config-file-path=/usr/local/php7.2.5/etc --with-config-file-scan-dir=/usr/local/php7.2.5/etc/php.d --with-curl --with-freetype-dir --with-gd --with-gettext --with-iconv-dir --with-kerberos --with-libdir=lib64 --with-libxml-dir --with-mysqli --with-openssl --with-pcre-regex --with-pdo-mysql --with-pdo-sqlite --with-pear --with-png-dir --with-jpeg-dir --with-xmlrpc --with-xsl --with-zlib --with-bz2 --with-mhash --enable-fpm --with-fpm-user=www-data  --with-fpm-group=www-data  --with-fpm-systemd --enable-bcmath --enable-libxml --enable-inline-optimization --enable-gd-native-ttf --enable-mbregex --enable-mbstring --enable-opcache --enable-pcntl --enable-shmop --enable-soap --enable-sockets --enable-sysvsem --enable-sysvshm --enable-xml --enable-zip

编译并安装
==========

.. code-block:: shell

    make  -j  2 (开启 2 个线程编译)  &&  make  install

创建配置文件
============

.. code-block:: shell

    cp  php.ini-production    /usr/local/php7.2.5/etc/php.ini
    cp  /usr/local/php7.2.5/etc/php-fpm.conf.default  /usr/local/php7.2.5/etc/php-fpm.conf
    cp  /usr/local/php7.2.5/etc/php-fpm.d/www.conf.default /usr/local/php7.2.5/etc/php-fpm.d/www.conf

增加到 Systemd 服务并启动
=========================

.. code-block:: shell

    cp  sapi/fpm/php-fpm.service /lib/systemd/system/php-fpm.service
    systemctl start php-fpm.service

安装xdebug插件
==============
下载解压源码
------------

.. code-block:: shell

    wget https://xdebug.org/files/xdebug-2.6.0.tgz
    tar-zxvf xdebug-2.6.0.tgz

进入xdebug源代码目录
--------------------

.. code-block:: shell

    cd xdebug-2.6.0

执行 phpize 配置
----------------

.. code-block:: shell

    /usr/local/php7.2.5/bin/phpize

开始初始化配置
--------------

.. code-block:: shell

    ./configure --with-php-config=/usr/local/php7.2.5/bin/php-config

编译并安装
----------

.. code-block:: shell

    make  &&  make  install

配置php.ini，以启用xdebug
-------------------------

.. code-block:: shell

    vim /usr/local/php7.2.5/etc/php.ini

键入如下配置信息:



.. code-block:: shell

    [xdebug]
    zend_extension="/usr/local/php7.2.5/lib/php/extensions/no-debug-non-zts-20170718/xdebug.so"

    xdebug.profiler_enable_trigger = 1
    xdebug.profiler_output_dir="/data/xdebug/profiler"
    xdebug.profiler_output_name = "cache.out.%t-%s"

    xdebug.trace_enable_trigger=1
    xdebug.trace_output_dir="/data/xdebug/trace"
    xdebug.trace_output_name=trace.%c.%p

    xdebug.remote_enable = 1
    xdebug.remote_handler = "dbgp"
    xdebug.remote_host = "127.0.0.1"

重启php-fpm
-----------

.. code-block:: shell

    systemctl restart php-fpm.service

通过 ``phpinfo();`` 看一下结果。


php cli和php-fpm配置加载顺序
============================

``PHP`` 和 ``PHP-FPM`` 都可以用参数 ``-c`` 指定 ``php.ini`` 配置文件。

执行下列命令可见:

.. code-block:: shell

    strace -f -o strace.log \
    /png/php/5.4.45/bin/php -v && \
    cat strace.log|egrep 'open|read'|grep 'ini'
    3080  open("/png/php/5.4.45/bin/php-cli.ini", O_RDONLY) = -1 ENOENT (No such file or directory)
    3080  open("/png/php/5.4.45/lib/php-cli.ini", O_RDONLY) = -1 ENOENT (No such file or directory)
    3080  open("/png/php/5.4.45/bin/php.ini", O_RDONLY) = -1 ENOENT (No such file or directory)
    3080  open("/png/php/5.4.45/lib/php.ini", O_RDONLY) = 3

``PHP`` 会优先读取 ``php`` 程序所在目录下的 ``php-cli.ini`` ,访问到则不再读取其他 ``ini`` 文件。

PHP-FPM情况如下:

.. code-block:: shell

    strace -f -o strace.log \
    /png/php/5.4.45/sbin/php-fpm -v && \
    cat strace.log|egrep 'open|read'|grep 'ini'
    3537  open("/png/php/5.4.45/lib/php-fpm-fcgi.ini", O_RDONLY) = -1 ENOENT (No such file or directory)
    3537  open("/png/php/5.4.45/lib/php.ini", O_RDONLY) = 3

PHP-CGI情况如下:

.. code-block:: shell

    strace -f -o strace.log \
    /png/php/5.4.45/bin/php-cgi -v && \
    cat strace.log|egrep 'open|read'|grep 'ini'
    3568  open("./php-cgi-fcgi.ini", O_RDONLY) = -1 ENOENT (No such file or directory)
    3568  open("/png/php/5.4.45/bin/php-cgi-fcgi.ini", O_RDONLY) = -1 ENOENT (No such file or directory)
    3568  open("/png/php/5.4.45/lib/php-cgi-fcgi.ini", O_RDONLY) = -1 ENOENT (No such file or directory)
    3568  open("./php.ini", O_RDONLY)       = -1 ENOENT (No such file or directory)
    3568  open("/png/php/5.4.45/bin/php.ini", O_RDONLY) = -1 ENOENT (No such file or directory)
    3568  open("/png/php/5.4.45/lib/php.ini", O_RDONLY) = 3