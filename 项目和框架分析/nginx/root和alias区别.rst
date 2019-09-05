*************************
nginx 中location和root区别
*************************
nginx 指定文件路径有两种方式 root 和 alias ，这两者的用法区别，使用方法总结了下，方便大家在应用过程中，快速响应。 root 与 alias 主要区别在于 nginx 如何解释 location 后面的 uri ，这会使两者分别以不同的方式将请求映射到服务器文件上。

[root]
语法：root path
默认值：root html
配置段：http、server、location、if

[alias]
语法：alias path
配置段：location

root实例
========

.. code-block:: conf

	location ^~ /t/ {
	    root /www/root/html/;
	}

如果一个请求的 ``URI`` 是 ``/t/a.html`` 时，web服务器将会返回服务器上的 ``/www/root/html/t/a.html`` 的文件。

alias实例
=========

.. code-block:: conf

	location ^~ /t/ {
		alias /www/root/html/new_t/;
	}

如果一个请求的 ``URI`` 是 ``/t/a.html`` 时，web服务器将会返回服务器上的 ``/www/root/html/new_t/a.html`` 的文件。注意这里是 ``new_t`` ，因为 ``alias`` 会把 ``location`` 后面配置的路径丢弃掉，把当前匹配到的目录指向到指定的目录。

注意：

1. 使用 ``alias`` 时，目录名后面一定要加 ``/`` 。
3. ``alias`` 在使用正则匹配时，必须捕捉要匹配的内容并在指定的内容处使用。
4. ``alias`` 只能位于 ``location`` 块中。（ ``root`` 可以不放在 ``location`` 中）