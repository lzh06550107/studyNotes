*******
php配置文件
*******

配置文件的加载顺序
=================
``PHP`` 和 ``PHP-FPM`` 都可以用参数 ``-c`` 指定 ``php.ini`` 配置文件。但他的默认执行顺序如下：

.. code-block:: shell

	strace -f -o strace.log \
	/png/php/5.4.45/bin/php -v && \
	cat strace.log|egrep 'open|read'|grep 'ini'
	3080  open("/png/php/5.4.45/bin/php-cli.ini", O_RDONLY) = -1 ENOENT (No such file or directory)
	3080  open("/png/php/5.4.45/lib/php-cli.ini", O_RDONLY) = -1 ENOENT (No such file or directory)
	3080  open("/png/php/5.4.45/bin/php.ini", O_RDONLY) = -1 ENOENT (No such file or directory)
	3080  open("/png/php/5.4.45/lib/php.ini", O_RDONLY) = 3

``PHP`` 会优先读取 ``php`` 程序所在目录下的 ``php-cli.ini`` ，访问到则不再读取其他 ``ini`` 文件。

PHP-FPM 情况如下:

.. code-block:: shell

	strace -f -o strace.log \
	/png/php/5.4.45/sbin/php-fpm -v && \
	cat strace.log|egrep 'open|read'|grep 'ini'
	3537  open("/png/php/5.4.45/lib/php-fpm-fcgi.ini", O_RDONLY) = -1 ENOENT (No such file or directory)
	3537  open("/png/php/5.4.45/lib/php.ini", O_RDONLY) = 3

PHP-CGI 情况如下:

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


https://www.jianshu.com/p/53a9064cb4e7 调试
