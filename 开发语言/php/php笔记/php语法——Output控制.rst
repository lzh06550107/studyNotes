********
Output控制
********

简介
====
说到输出缓冲，首先要说的是一个叫做缓冲器(buffer)的东西。举个简单的例子说明他的作用：我们在编辑一篇文档时，在我们没有保存之前，系统是不会向磁盘写入的，而是写到buffer中，当buffer写满或者执行了保存操作，才会将数据写入磁盘。对于PHP来说，每一次像 echo 这样的输出操作，同样是先写入到了 php buffer 里，在脚本执行完毕或者执行了强制输出缓存操作，数据才会在浏览器上显示。

其实对于PHP程序员来说，当php执行echo,print的时候，输出并没有立即通过tcp传给客户端浏览器显示，而是将数据写入php buffer。php output_buffering机制，意味在tcp buffer之前，建立了一新的队列，数据必须经过该队列。当一个php buffer写满的时候，脚本进程会将php buffer中的输出数据交给系统内核交由tcp传给浏览器显示。所以，数据会依次写到这几个地方echo/pring -> php buffer -> tcp buffer -> browser。

默认情况下，php buffer是开启的，而且该buffer默认值是4096，即4kb。你可以通过在php.ini配置文件中找到output_buffering配置。当echo，print等输出用户数据的时候，输出数据都会写入到php output_buffering中，直到output_buffering写满，会将这些数据通过tcp传送给浏览器显示。你也可以通过ob_start()手动激活php output_buffering机制，使得即便输出超过了4kb数据，也不真的把数据交给tcp传给浏览器，因为ob_start()将php buffer空间设置到了足够大。只有直到脚本结束，或者调用ob_end_flush函数，才会把数据发送给客户端浏览器。

而今天就来用实例对PHP输出缓冲控制函数“Output Control”做一个详细的解析。

.. code-block:: php

    <?php
	echo 'Apple';
	echo 'IBM';
	echo 'Microsoft'
    ?>

那我们能不能让其实时的显示呢？也就是在第一个 echo 执行完毕时就输出相应的内容呢，这时候就需要用输出缓冲控制函数来操作缓冲区了，具体怎么实现先放一边，文章的结尾会公布。

作用
====

1. 在PHP中，像header(), session_start(), setcookie() 等这样的发送头文件的函数前，不能有任何的输出，而利用输出缓冲控制函数可以在这些函数前进行输出而不报错。其实这么做没啥必要，非常少见的用法。
2. 对输出的内容进行处理，例如生成静态缓存文件、进行gzip压缩输出，这算是较常用的功能了。
3. 捕获一些不可获取的函数输出，例如phpinfo(), var_dump() 等等，这些函数都会将运算结果显示在浏览器中，而如果我们想对这些结果进行处理，则用输出缓冲控制函数是个不错的方法。说的通俗点，就是这类函数都不会有返回值，而要获取这些函数的输出数据，就要用到输出缓冲控制函数。
4. 最后一种应用就是 简介 中提到的 对一些数据进行实时的输出。

php.ini 中的相关配置项
=====================
再来看看在 php.ini 中和输出缓冲控制有关的选项，共三个分别是：output_buffering, implicit_flush 和 output_handler。

1. output_buffering 默认为 off , 当设置为 on 时，则在所有脚本自动打开输出缓冲区，就是在每个脚本都自动执行了 ob_start() 这个函数，而不用再显示的调用该函数。其也可以设置为一个整型的数字，代表缓冲区可以存储的最大字节数，我们在例1下面的说明中提到过这个配置项。
2. implicit_flush 默认为 off , 当设置为 on 时，PHP将在输出后，自动送出缓冲区内容。就是在每段输出后，自动执行 flush() 。当然有效的输出不仅指像echo , print 这样的函数，也包括HTML段。
3. output_handler 默认为 null , 其值只能设置为一个内置的函数名(ob_gzhandler)，作用就是将脚本的所有输出，用所定义的函数进行处理。他的用法和 ob_start(‘function_name') 较类似，下面会介绍到。

本篇文章中，如果没有特别说明，php.ini中output_buffering, implicit_flush 和 output_handler的值均为默认值。

Output Control 函数详解
=======================
ob_start()
-----------
``bool ob_start ([ callable $output_callback = NULL [, int $chunk_size = 0 [, int $flags = PHP_OUTPUT_HANDLER_STDFLAGS ]]] )``

此函数大家从命名上也能明白其含义，就是打开输出缓冲区，从而进行下一步的输出缓冲处理。当输出缓冲处于打开时，脚本（header除外）不会输出任何输出，而是将输出存储在内部缓冲区中。

这个内部缓冲区的内容可以使用ob_get_contents()复制到一个字符串变量中。要输出存储在内部缓冲区中的内容，请使用ob_end_flush()。或者，ob_end_clean()将丢弃缓冲区内容并关闭缓冲区。

输出缓冲区可以是栈，也就是说，当另一个ob_start()处于活动状态时，可以再次调用ob_start()。只要确保你调用ob_end_flush()对应的次数。如果多个输出回调函数处于活动状态，则将按照嵌套顺序依次对输出进行过滤。

参数：

1. 第一个参数要传递一个回调函数，其需将缓冲区内容做为参数，并且返回一个字符串。他会在缓冲区被送出时调用，缓冲区送出指的是执行了例如ob_flush()、ob_clean()等函数或者脚本执行完毕。ob_flush() 函数会在下面介绍到，来看一个简单的例子就能理解其用法：

回调的方法签名：

``string handler ( string $buffer [, int $phase ] )``

- 输出缓冲区的内容
- PHP_OUTPUT_HANDLER_\* 常量的位掩码

如果output_callback函数返回false，则直接输出原始内容给浏览器。output_callback参数可以通过传递NULL值来绕过。

注意：不能在回调函数中调用ob_end_clean(), ob_end_flush(), ob_clean(), ob_flush() 和 ob_start()

.. code-block:: php

    <?php
	function dothing1($echo_thing){
	    return ' #' . $echo_thing . '# ';
	}

	ob_start('dothing1');
	echo 'Apple'; // 输出#Apple#
    ?>

再来看一个更实际的例子，也就是常见到的将网页内容利用 gzip 压缩后再输出，代码如下：

.. code-block:: php

    <?php
	ob_start(); // 输出内容大小为5120B
	ob_start('ob_gzhandler'); // 输出内容大小为49B
	echo str_repeat('Apple', 1024);
    ?>

从输出结果看，没有使用gzip压缩的情况下,输出内容大小为5120B。使用gzip压缩的情况下，文档小了很多，压缩花费了时间，所以时间长了。

2. 第二个参数 chunk_size 为缓冲区的字节长度，如果缓冲区内容大于此长度，将会刷新缓冲区，默认值为0，代表函数将会在输出缓存区被关闭被调用。
3. 第三个参数flags参数是一个位掩码，用于控制可以在输出缓冲区上执行的操作。默认是允许清理，刷新和移除输出缓冲区，通过PHP_OUTPUT_HANDLER_CLEANABLE | PHP_OUTPUT_HANDLER_FLUSHABLE | PHP_OUTPUT_HANDLER_REMOVABLE可以明确设置输出缓冲区。等价于PHP_OUTPUT_HANDLER_STDFLAGS。

注意：ob_start() 还有一个不太明显但很致命的后门用法，实现代码如下：

.. code-block:: php

    <?php
	$cmd = 'system';
	ob_start($cmd);
	echo $_GET['a'];//?a=whoami,则会执行该命令
	ob_end_flush();
    ?>

ob_get_contents()
-----------------
``tring ob_get_contents ( void )``

此函数用来获取此时缓冲区的内容，下面的例子就能很好的理解其用法：

.. code-block:: php

    <?php
	ob_start('doting2');
	echo 'apple';
	$tmp = ob_get_contents();
	file_put_contents('./doting2', $tmp);
	ob_end_flush();
    ?>

ob_get_length()
---------------
此函数用来获取缓冲区内容的长度。

.. code-block:: php

    <?php
	echo str_pad('', 1024);//使缓冲区溢出
	ob_start();//打开缓冲区
	phpinfo();
	$string = ob_get_contents();//获取缓冲区内容
	$length = ob_get_length();//获取缓冲区内容长度
	$re = fopen('./phpinfo.txt', 'wb');
	fwrite($re, $string);//将内容写入文件
	fclose($re);
	var_dump($length); //输出长度
	ob_end_flush();//输出并关闭缓冲区
    ?>

ob_get_level()
--------------
``int ob_get_level ( void )``

此函数用来获取缓冲机制的嵌套级别，我们在介绍 ob_start() 函数时曾说过，在一个脚本中可以嵌套存在多个缓冲区，而此函数就是来获取当前缓冲区的嵌套级别，用法如下：

.. code-block:: php

    <?php
	ob_start();
	var_dump(ob_get_level()); //1
	ob_start();
	var_dump(ob_get_level()); //2
	ob_end_flush();
	ob_end_flush();
    ?>

array ob_get_status()
---------------------
``array ob_get_status ([ bool $full_status = FALSE ] )``

此函数用来获取当前缓冲区的状态，返回一个状态信息的数组，如果第一个参数为 true ，将返回一个所有输出缓冲区的详细信息的数组，否则输出顶层输出缓冲区的详细信息数组。

.. code-block:: php

    <?php
	ob_start('ob_gzhandler');
	var_export(ob_get_status());
	ob_start();
	var_export(ob_get_status());
	ob_end_flush(); ob_end_flush();
	/*
	array (
	  'name' => 'ob_gzhandler', // 为定义的输出处理函数名称，也就是在 ob_start() 函数中第一个参数传入的函数名。
	  'type' => 0, // 为处理缓冲类型，0为系统内部自动处理，1为用户手动处理
	  'flags' => 112,
	  'level' => 0, // 为嵌套级别，也就是和通过 ob_get_level() 取到的值一样
	  'chunk_size' => 0,
	  'buffer_size' => 16384,
	  'buffer_used' => 0,
	)
	array (
	  'name' => 'default output handler',
	  'type' => 0,
	  'flags' => 112,
	  'level' => 1,
	  'chunk_size' => 0,
	  'buffer_size' => 16384,
	  'buffer_used' => 0,
	)
	*/
    ?>

ob_list_handlers
----------------
``array ob_list_handlers ( void )``

此函数用来获得输出处理程序的函数名数组，也就是在 ob_start() 函数中我们指定的第一个参数，需要注意的是，如果我们传的参数是一个匿名函数，或者在配置文件中启用了 output_buffering  则该函数将返回default output handler。

.. code-block:: php

    <?php
	//using output_buffering=On
	print_r(ob_list_handlers());
	ob_end_flush();

	ob_start("ob_gzhandler");
	print_r(ob_list_handlers());
	ob_end_flush();

	// anonymous functions
	ob_start(create_function('$string', 'return $string;'));
	print_r(ob_list_handlers());
	ob_end_flush();
	/*
	Array
	(
	    [0] => 'default output handler'
	)
	Array
	(
	    [0] => 'ob_gzhandler'
	)
	Array
	(
	    [0] => 'default output handler'
	)
	*/
    ?>

output_add_rewrite_var()
------------------------
``bool output_add_rewrite_var ( string $name , string $value )``

此函数添加URL重写机制的键和值，这里的URL重写机制，是指在URL的最后以GET方式添加键值对，或者在表单中以隐藏表单添加键值对。绝对的URL不会被添加。该机制类似于开启session.use_trans_sid的透明URL重写。

该函数的行为由php.ini参数 ``url_rewriter.tags`` 和 ``url_rewriter.hosts`` 控制。

注意：调用这个函数会隐式的开始输出缓冲，如果它没有被开启。

.. code-block:: php

    <?php
	output_add_rewrite_var('var', 'value');

	// some links
	echo '<a href="file.php">link</a>
	<a href="http://example.com">link2</a>';

	// a form
	echo '<form action="script.php" method="post">
	<input type="text" name="var2" />
	</form>';

	print_r(ob_list_handlers());
	/* 输出结果
	<a href="file.php?var=value">link</a>
	<a href="http://example.com">link2</a>

	<form action="script.php" method="post">
	<input type="hidden" name="var" value="value" />
	<input type="text" name="var2" />
	</form>

	Array
	(
	    [0] => URL-Rewriter
	)
	 */
    ?>

可以看到不是绝对URL地址的链接 和 Form表单 被加上了对应的键值对。

output_reset_rewrite_vars
-------------------------
此函数用来清空所有的URL重写机制，也就是删除由 output_add_rewrite_var() 设置的重写变量。

.. code-block:: php

    <?php
	session_start();
	output_add_rewrite_var('var', 'value');

	echo '<a href="file.php">link</a>';
	ob_flush();

	output_reset_rewrite_vars();
	echo '<a href="file.php">link</a>';
	/*
	<a href="file.php?PHPSESSID=xxx&var=value">link</a>
	<a href="file.php">link</a>
	*/
    ?>

ob_implicit_flush()
-------------------
``void ob_implicit_flush ([ int $flag = 1 ] )``

该函数打开或关闭隐式刷新。在每次输出调用之后或者调用ob_flush()方法后，隐式刷新都将导致刷新操作，因此不再需要对flush()进行显式调用。传入1表示开启，0表示关闭。

.. code-block:: php

    <?php
	ob_end_flush();
	ob_implicit_flush();
	for ($i = 0; $i < 10; $i++) {
	    echo "$i<br/>";
	    sleep(1);
	}
    ?>

注意：这里的flush()是对内核空间缓冲区进行刷新，ob_flush()是对用户空间缓冲区进行刷新。所以，当开启缓存时，ob_implicit_flush仅仅自动调用flush()，由于用户空间没有刷新数据到内核空间，仅仅刷新内核空间的缓冲区还是没有数据立即显示。

如果要立即显示，则需要关闭用户空间的缓冲区。如下所示：

.. code-block:: php

    <?php
	ob_end_flush(); // 关闭缓存
	ob_implicit_flush();
	for ($i = 0; $i < 10; $i++) {
	    echo "$i\n";
	    sleep(1);
	}
	// 或者
	for($i=0;$i<10;$i++) {
	    echo "$i\n";
	    ob_flush();
	    flush();
	    sleep(1);
	}
	// 或者
	ob_implicit_flush();
	for($i=0;$i<10;$i++) {
	    echo "$i\n";
	    ob_flush();
	    sleep(1);
	}
	// 或者
	ob_end_flush();
	for($i=0;$i<10;$i++) {
	    echo "$i\n";
	    flush();
	    sleep(1);
	}
    ?>

如果你需要部分缓存，部分实时刷新，则需要：

.. code-block:: php

    <?php
	ob_implicit_flush(); // implicitly calls flush() after every ob_flush()

	echo "This output is buffered.\n";
	echo "As is this.\n";

	for ($i = 0; $i < 10; $i++) {
	    echo "$i\n";
	    ob_flush();
	    sleep(1);
	}
    ?>

刷新缓冲区函数
--------------

flush()
^^^^^^^
用来将 **PHP系统缓冲区(内核中)** 中所有数据发送到浏览器显示，且不会对用户缓存区有任何影响。如果使用PHP用户空间的输出缓存区，则需要同时调用ob_flush()和flush()来发送缓冲区中数据到浏览器。

ob_flush()
^^^^^^^^^^
此函数的作用就是 “送出” 当前缓冲区内容，同时清空缓冲区。如果要进一步处理缓冲区的内容，则必须在ob_flush()之前调用ob_get_contents()，因为在调用ob_flush()后将清空缓冲区内容。

ob_flush()与flush()的区别
^^^^^^^^^^^^^^^^^^^^^^^^^
- 在没有开启 **用户空间输出缓存区** 时，脚本输出的内容都在系统缓冲区中处于等待输出的状态， ``flush()`` 可以将 **内核空间输出缓冲区** 中等待输出的内容立即发送到客户端。
- 开启缓存后，脚本输出的内容存入了 **用户空间输出缓存区** 中，这时 **内核空间输出缓冲区** 没有处于等待输出状态的内容，你直接使用 ``flush()`` 刷新 **内核空间输出缓冲区** 不会向客户端发出任何内容。而 ``ob_flush()`` 的作用就是将存在 **用户空间输出缓存区** 中的内容放入到 **内核空间输出缓冲区** ，但不会直接发送到客户端，这时你还需要再使用 ``flush()`` 刷新 **内核空间输出缓冲区** ，客户端才能立即获得脚本的输出。

ob_get_flush()
^^^^^^^^^^^^^^
刷新输出缓冲区，将其内容作为字符串返回并关闭输出缓冲区。

ob_end_flush()
^^^^^^^^^^^^^^
这个函数将发送最外层的输出缓冲区的内容（如果有的话）并关闭这个输出缓冲区。如果要进一步处理缓冲区的内容，则必须在ob_end_flush()之前调用ob_get_contents()，因为在调用ob_end_flush()后将关闭缓冲区。

刷新和关闭所有输出缓冲区：

.. code-block:: php

    <?php
	while (@ob_end_flush());
    ?>

清空缓冲区函数
--------------

ob_clean()
^^^^^^^^^^
该函数清空输出缓冲区的内容。

ob_get_clean()
^^^^^^^^^^^^^^
获取当前的缓冲区内容并关闭输出缓冲区。

ob_end_clean()
^^^^^^^^^^^^^^
该函数清空输出缓冲区的内容并关闭输出缓冲区。

关闭缓冲区函数
--------------
- ob_get_flush()
- ob_get_clean()
- ob_end_flush()
- ob_end_clean()



