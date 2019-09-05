***********
PHP函数解析
***********

php中的set_magic_quotes_runtime()作用和用法
===========================================
在 ``magic_quotes_gpc=On`` 的情况下，如果输入的数据有单引号（’）、双引号（”）、反斜线（）与 NUL（NULL 字符）等字符都会被加上反斜线。这些转义是必须的，如果这个选项为 ``off`` ，那么我们就必须调用 ``addslashes`` 这个函数来为字符串增加转义。
在 php5.4 以后就废除了此特性。所以我们在以后就不要依靠这个特性了。当然如果重复给溢出字符加反斜线，那么字符串中就会有多个反斜线，为了使自己的程序不管服务器是什么设置都能正常执行。所以这时就要用 ``set_magic_quotes_runtime()`` 与 ``get_magic_quotes_runtime()`` 设置和检测 ``php.ini`` 文件中 ``magic_quotes_runtime`` 状态。
判断 php 版本，小于 5.4 的就手动关掉，定义常量。大于 5.4 直接定义常量为 ``false`` 。


为了使自己的程序不管服务器是什么设置都能正常执行。可以在程序开始用 ``get_magic_quotes_runtime`` 检测该设置的状态决定是否要手工处理，或者在开始（或不需要自动转义的时候）用 ``set_magic_quotes_runtime(0)`` 关掉该设置。 ``magic_quotes_gpc`` 设置是否自动为 ``GPC(get,post,cookie)`` 传来的数据中的 ``\'\"\\`` 加上反斜线。

可以用 ``get_magic_quotes_gpc()`` 检测系统设置。如果没有打开这项设置，可以使用 ``addslashes()`` 函数添加，它的功能就是给数据库查询语句等的需要在某些字符前加上了反斜线。这些字符是单引号（ ``\'`` ）、双引号（ ``\"`` ）、反斜线（ ``\\`` ）与 NUL（ NULL 字符）。

一般用法如下：

.. code-block:: php

    <?php
    if(!get_magic_quotes_gpc())
    {
        addslashes($prot);
    }

``set_magic_quotes_runtime`` 用来设置 ``php.ini`` 文件中的 ``magic_quotes_runtime`` 值，当遇到反斜杆（ ``\`` ）、单引号（'）、双引号（"）这样一些的字符定入到数据库里，又不想被过滤掉，使用这个函数，将会自动加上一个反斜杆（ ``\`` ），保护系统和数据库的安全。

``magic_quotes_runtime`` 是 ``php.ini`` 里的环境配置变量， ``0`` 和 ``false`` 表示关闭本功能， ``1`` 和 ``true`` 表示打开本功能。当 ``magic_quotes_runtime`` 打开时，所有外部引入的数据库资料或者文件等都会自动转为含有反斜线溢出的资料，
设置方法如下：
1、可以直接在 ``php.ini`` 里面设置为 ``ON`` ；
2、``set_magic_quotes_runtime(true)`` 或者 ``set_magic_quotes_runtime(1)`` ；
3、用 ``ini_set`` 函数， ``ini_set(magic_quotes_runtime,1)`` ；
