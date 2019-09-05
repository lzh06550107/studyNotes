MySQL数据库
===========

MySQL数据库管理
--------------

MySQL数据库中表的设计
--------------------

数据库的设计步骤
---------------

SQL语言设计
----------

使用PHP脚本向MySQL服务器发送SQL
-----------------------------
MySQL采用的是“客户机/服务器”体系结构，如果使用PHP脚本处理数据库中的数据，则PHP充当了MySQL“客户机”的角色。但mysql功能模块并不是PHP的一个集成组件，要想使用这个功能扩展模块，PHP的Linux版本必须在编译时加上一个 ``--with-mysql`` 选项。PHP的Windows版本则通过一个DLL文件提供了相应的扩展。无论使用的是哪一种操作系统，都必须在php.ini文件里启用这个扩展以确保PHP能够找到所有必要的DLL。

在PHP脚本中连接MySQL服务器
^^^^^^^^^^^^^^^^^^^^^^^^
通过PHP脚本程序去管理MySQL服务器中的数据，也必须先建立连接，然后才能通过PHP中的函数向服务器中发送SQL查询语句。PHP可以通过MySQL功能模块去连接MySQL服务器，办法是调用mysql_connect()函数，和使用MySQL客户机程序连接MySQL服务器类似。该函数的原型如下所示：

``resource mysql_connect([string server [, string username [, string password [, bool new_link [, int client_flags]]]]])``

通常只要提供前三个参数即可，包括MySQL服务器的主机名、MySQL用户名和密码。如果MySQL服务器与PHP脚本运行在同一台计算机上，可以使用localhost作为它的主机名。如果还是通过MySQL管理员用户名root，密码mysql_pass连接本机的MySQL服务器。代码如下所示：

``mysql_connect("localhost","root","mysql_pass"); //使用PHP程序连接MySQL服务器``

如果连接成功，这个函数将返回一个资源类型的标识符号。如果与MySQL服务器建立了不只一条的连接，在以后的操作中就必须使用它们的标识符号来区分它们。而如果只与MySQL服务器建立了一条连接，这条连接就会成为与MySQL服务器之间的默认连接，也就无须在调用各种与MySQL操作相关的函数中给出这个标识符号了。如果连接失败，这个函数将返回FALSE，并向Web服务器发送一条出错消息。可以通过下面的代码检查与MySQL服务器建立的连接是否成功，并输出与当前连接有关的详细信息。

判断是否连接正确
"""""""""""""""
我们也可以使用两个函数来判断， ``mysql_errno()`` 和 ``mysql_error()`` ，这两个函数分别返回上次MySQL发生的错误号和错误信息，如果未发生任何错误, ``mysql_errno()`` 函数将返回0。所以，我们可以使用如下判断来进行处理：

.. code-block:: php

    <?php
	//int mysql_errno ([ resource $link_identifier ] )
	//string mysql_error ([ resource $link_identifier ] )

	  if(mysql_errno()){
	    exit(‘数据库连接错误！’.mysql_error());
	  }
    ?>

获取连接信息
"""""""""""

.. code-block:: php

    <?php
	$link = mysql_connect("localhost", "root", "root") or die("连接失败：".mysql_error());
	echo "与MySQL服务器建立的连接成功"; // 连接成功则会输出这条提示信息
	echo PHP_EOL;
	echo mysql_get_client_info(); // 客户端API函数库的版本信息 mysqlnd 5.0.10 - 20111026 - $Id: c85105d7c6f7d70d609bb4c000257868a40840ab $
	echo PHP_EOL;
	echo mysql_get_host_info(); // 与MySQL服务器的连接类型 localhost via TCP/IP
	echo PHP_EOL;
	echo mysqli_get_proto_info(); // 通信协议的版本信息
	echo PHP_EOL;
	echo mysql_get_server_info(); // MySQL服务器的版本信息 5.5.53
	echo PHP_EOL;
	echo mysql_client_encoding(); // 客户端使用的默认字符集 utf8
	echo PHP_EOL;
	echo mysql_stat(); // MySQL服务器的当前工作状态 Uptime: 185  Threads: 1  Questions: 8  Slow queries: 0  Opens: 33  Flush tables: 1  Open tables: 26  Queries per second avg: 0.043
	echo PHP_EOL;
	mysql_close($link); // 关闭与MySQL服务器建立的连接
    ?>

完成数据库访问工作之后，可以通过调用mysql_close()函数断开与MySQL服务器的连接，则通过mysql_connect()函数成功返回的连接标志符号就不能再继续使用了。

在PHP程序中选择已创建的数据库
^^^^^^^^^^^^^^^^^^^^^^^^^^^
通常数据库的创建工作都是先由数据库管理员（DBA）建立，再由PHP程序员在脚本中使用。在使用PHP脚本建立起与MySQL服务器的连接之后，为了避免每次调用PHP的mysql扩展函数时都指定目标数据库，最好先用 ``mysql_select_db()`` 函数为后续操作选定一个默认数据库，这个函数和SQL命令 ``USE bookstore`` 功能相似。例如：

``bool mysql_select_db ( string $database_name [, resource $ link_identifier ] )``

.. code-block:: php

    <?php
	$link = mysql_connect("localhost", "mysql_user", "mysql_password") or die("连接失败：".mysql_error());
	// 为后续的mysql扩展函数的操作选定一个默认的数据库，它相当于SQL命令use bookstore
	mysql_select_db('bookstore', $link) or die('不能选定数据库 bookstore:'.mysql_error());
	mysql_close($link); // 关闭与MySQL服务器建立的连接
    ?>

可以将数据库连接和选定默认数据库的过程，写在一个独立的PHP脚本文件中。例如，将上面的PHP程序保存在一个名为connect.php的文件中。这样，在其它需要对数据库操作的PHP文件中，只要使用require()或include()等函数将该文件包含进来，就不需要再重复连接了。这样做不仅可以提高开发效率，更重要的是当数据库的用户名和密码需要变化时，只需要更改这一个文件，则所有使用该文件的PHP脚本都是使用新用户与数据库服务器建立的连接。

设置字符集
^^^^^^^^^
为了避免读取和写入数据时发生数据乱码，除了要将文件格式设置为utf-8无bom头格式，还要将数据库客户端字读集设置为utf8，所以我们需要在发送sql语句之前，使用 ``mysql_set_charset()`` 函数来完成数据库字符集的设定！

``bool mysql_set_charset ( string $charset [, resource $link_identifier = NULL ] )``

执行SQL命令
^^^^^^^^^^
在PHP脚本中，只要把SQL命令作为一个字符串传递给mysql_query()函数，就会将其发送到MySQL服务器中并执行。如果想访问的不是当前数据库，就需要调用mysql_db_query()函数来执行SQL命令并明确地给出数据库的名字。

mysql_query()函数可以用来执行DDL、DML、DQL及DCL等任何一种SQL命令，如果想执行一条以上的SQL命令，就需要为他们分别调用一次mysql_query()函数。如果SQL命令执行成功，mysql_query()函数将返回一个非0值。如果没有执行成功，该函数将返回FALSE，并会生成一条出错消息，出错原因可以利用mysql_errno()和mysql_error()函数来确定。

此外，该函数执行完INSERT、UPDATE和DELETE等DML命令后，可以调用 ``mysql_affected_rows()`` 函数去查看它们到底修改了多少条数据记录。如果在执行完INSERT命令之后，还可以调用 ``mysql_insert_id()`` 函数查看插入的最后一条新纪录的 ``AUTO_INCREMENT`` 值是多少。代码如下：

.. code-block:: php

    <?php
	$link = mysql_connect("localhost", "mysql_user", "mysql_pass") or die("连接失败：".mysql_error());
	// 为后续的mysql扩展函数的操作选定一个默认的数据库，它相当于SQL命令use bookstore
	mysql_select_db('bookstore') or die('不能选定数据库 bookstore:'.mysql_error());

	// 将插入3条的INSERT语句声明为一个字符串
	$insert = "INSERT INTO books(bookName,publisher,author,price,detail) VALUES
	('PHP','电子工业','高某某','80.00','与PHP相关的图书'),
	('JSP','人民邮电','洛某某','50.00','与JSP相关的图书'),
	('ASP','电子工业','峰某某','30.00','与ASP相关的图书')";

	// 使用mysql_query()函数发送INSERT语句，如果成功返回TRUE，失败则返回FALSE
	$result = mysql_query($insert);
	if($result && mysql_affected_rows() > 0) {
	    echo "数据记录插入成功，最后一条插入的数据记录ID为：".mysqli_insert_id()."<br>";
	}else{
	    echo "插入记录失败，错误号：".mysql_errno()."，错误原因：".mysql_error()."<br>";
	}

	// 执行UPDATE命令修改表books中的一条记录，将图书名为PHP的记录价格修改为79.90
	$result1 = mysql_query("UPDATE books SET price='79.9' WHERE bookName='PHP'");
	if($result1 && mysql_affected_rows() > 0) {
	    echo "数据记录修改成功<br>";
	}else {
	    echo "修改数据失败，错误号：".mysql_errno(). "，错误原因：".mysql_error()."<br>";
	}

	// 执行DELETE命令删除表books中图书名为JSP的记录
	$result2 = mysql_query("DELETE FROM books WHERE bookName='JSP'");
	if($result2 && mysql_affected_rows() > 0) {
	    echo "数据记录删除成功<br>";
	}else {
	    echo "删除数据失败，错误号：".mysql_errno()."，错误原因：".mysql_error()."<br>";
	}
	mysql_close($link); // 关闭与MySQL服务器建立的连接
    ?>

在PHP脚本中处理SELECT查询结果集
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
在PHP脚本中执行SELECT查询命令，也是调用mysql_query()函数，但和执行DML不同的是，执行SELECT命令之后mysql_query()函数的返回值是一个PHp资源的引用指针(结果集)。这个返回值可以用在各种结果集处理函数中，对结果数据表的各个字段进行处理。例如，可以用下面两个函数获得结果数据表的数据行个数和数据列个数。代码如下：

.. code-block:: php

    <?php
	$result = mysql_query("SELECT * FROM books"); //执行SELECT语句返回结果集资源$result
	$rows = mysql_num_rows($result); //从结果集中获得数据记录行的个数
	$cols = mysql_num_fields($result); //从结果集中获得记录列的个数
    ?>

将SQL语句发送到MySQL服务器之后，MySQL服务器会将执行SQL语句之后的结果返回给PHP端，这里的结果分为两种：

- 布尔型
- 结果集资源类型

当我们执行的SQL语句是DML语句，也就是增，删，改三种语句，这时mysql_query()返回的类型是布尔类型，执行成功返回真，失败返回假，但有时SQL语句执行成功，但不一定有受影响行数，所以我们可以使用mysql_affected_rows()函数返回受影响行数；

当我们执行的是DQL语句，也就是查询语句，mysql_query()函数将返回的是结果集资源类型，我们可以使用mysql_num_rows($result)函数来获取结果集当中的记录条数，但是我们无法将结果集资源当中的数据得到，因此PHP为我们提供了4个函数，来解析此结果集资源：

- mysql_fetch_array()：该函数可以将结果数据表中的每一行获取为一个关联数组或索引数组，或者同时获取为关联和索引数组。可以通过为该函数传递MYSQL_ASSOC、MYSQL_NUM或MYSQL_BOTH中的一个常量返回不同的数组形态，默认使用MYSQL_BOTH常量将两种数组一起返回。
- mysql_fetch_assoc()：以关联数组形式返回一条结果记录
- mysql_fetch_row()：以索引数组形式返回一条结果记录
- mysql_fetch_object()：以一个对象的形式返回一条结果记录

如果没有特殊要求，尽量不要去使用 ``mysql_fetch_array()`` 方法。使用 ``mysql_fetch_row()`` 或 ``mysql_fetch_assoc()`` 函数实现相同的功能，效率会更高一些。上述四个函数每执行一次，结果集资源的指针都将向后移动一位，直到最后一位，将返回布尔类型的FALSE，因此，我们可以使用条件型循环while配合上述四个函数来使用，以 ``mysql_fetch_assoc()`` 函数为例，如下所示：

.. code-block:: php

    <?php
	while($row=mysql_fetch_assoc($result)){
	    //$row为一条记录的数组
	}
    ?>
