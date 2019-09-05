PHP的mysqli扩展
===============
从PHP 5.0开始，不仅可以使用早期的mysql数据库扩展函数，而且还可以使用新的扩展mysqli技术实现与MySQL数据库的信息交流。PHP的 mysqli扩展被封装到一个类中，它是一种面向对象的技术，只能在PHP 5和MySQL 4.1（或更高的版本）环境中使用，（i）表示改进，其执行速度更快。使用mysqli扩展和传统的过程化方法相比更方便也更高效。利用mysqli扩展 技术不仅可以调用MySQL的存储过程、处理MySQL事务，而且还可以使访问数据库工作变得更加稳定。

喜欢过程化编程的用户也不用担心，mysqli也有过程式的方式，提供了一个传统的函数式接口，只不过开始贯以mysqli的前缀，其他都差不多。 如果mysqli以过程式的方式操作的话，有些函数必须指定资源，比如说 mysqli_query（资源标识，SQL语句）。并且资源标识的参数是放在前面的，而mysql_query（SQL语句，'可选'）的资源标识是放 在后面的，并且可以不指定，它默认是上一个打开的连接或资源。本书将重点介绍他的面向对象的用法，如果更喜欢以过程化方式编写程序，使用前面介绍的 mysql功能扩展模块就可以了。这里，希望读者使用面向对象的方式编程，这样可以编写出更容易阅读和理解的代码。

启用mysqli扩展模块
-----------------
与mysql功能扩展模块类似，mysqli接口也不是PHP的一个集成组件，如果想使用这个功能扩展模块，需要显示配置PHP才能使用此扩展。在不同平台下的配置有所不同，如果在Linux平台中启用mysqli扩展，必须在编译PHP时加上 ``--with-mysqli`` 选项。如果在Windows平台中启用mysqli扩展，需要通过一个DLL文件提供相应的扩展。不管使用的是哪一个操作系统平台，都必须在php.ini文件里启用这个扩展，以确保PHP能够找到所有必要的DLL。可以在php.ini文件中找到下面一行，取消前面的注释，如果没有找到就添加这样一行：

``extension=php_mysqli.dll //在php.ini文件中启用这一行``

mysqli扩展不仅提供了mysql模块的所有功能，也相应地增加了一些新特性。mysqli扩展模块包括 ``mysqli`` 、 ``mysqli_result`` 和 ``mysqli_stmt`` 三个类，通过这三个类的搭配使用，就可以连接MySQL数据库服务器和选择数据库、查询和获取数据，以及使用预处理语句简化了重复 执行的查询语句。

使用mysqli类
------------
mysqli类的对象主要控制PHP和MySQL数据库服务器之间的连接、选择数据库、向MySQL服务器发送SQL语句，以及设置字符集等，这些任务都是通过该类中声明的构造方法、成员方法和成员属性完成的。在表13-1和表13-2两个表格中，分别列出了mysqli类中声明的成员方法和成员属性。

======================  ==============================================
成员方法名                   描述
======================  ==============================================
__construct()           构造方法，用于创建一个新的mysqli对象，也可以建立一个连接
autocommit()            开启或关闭数据库修改自动提交
change_user()           改变了数据库连接所指定的用户
character_set_name()    返回数据库连接默认的字符集
close()                 关闭先前打开连接
commit()                提交当前的事务
connect()               打开一个新的连接到MySQL数据库服务器
debug()                 执行调试操作
dump_debug_info()       转储调试信息
get_client_info()       返回客户端版本
get_host_info()         返回一个字符串代表的连接使用类型，如：Localhost via UNIX socket
get_server_info()       返回MySQL服务器的版本，如：4.1.2-alpha-debug
get_server_version()    返回整数形式的MySQL服务器版本，如40102
init()                  初始化MySQLi并返回一个资源
info()                  检索有关最近执行的查询
kill()                  要求服务器去杀死一个MySQL线程
multi_query()           执行多个查询语句
more_results()          从多查询语句中检查是否有任何更多的查询结果
Next_result()           从当前执行的多查询中读取下一个结果
options()               设置选项
ping()                  如果没有连接，ping一台服务器连接或重新连接
prepare()               准备一个SQL语句的执行，返回mysqli_stmt对象
query()                 与数据库的任何交互都是通过查询进行的，该方法向数据库发送查询来执行
real_connect()          试图打开一个连接到MySQL数据库服务器
escape_string()         转义特殊字符的字符串，用于在一个SQL语句，并考虑到当前的字符集的连接
rollback()              回滚当前的事务
select_db()             为数据库查询选择默认的数据库
set_charset()           设置默认客户端字符集
ssl_set()               使用SSL用于建立安全连接
stat()                  获取当前的系统状态
stmt_init()             初始化一个声明，并返回一个mysqli_stmt对象
store_result()          从最后查询中转让结果集
thread_safe()           是否考虑返回安全的线程
======================  ==============================================

===================  ============================
成员属性名                描述
===================  ============================
$affected_rows       在前一个MySQL操作中获取影响的行数
$client_info         MySQL客户端版本为一个字符串返回
$client_version      MySQL客户端版本为一个整数返回
$errno               返回最近函数调用的错误代码
$error               返回最近函数调用的错误信息字符串
$field_count         传回最近查询获取的列数
$host_info           返回一个字符串的连接类型使用
$info                检索有关最近执行的查询
$insert_id           返回使用最后查询自动生成的编号
$protocol_version    返回MySQL协议使用的版本
$sqlstate            返回一个字符串包含SQLSTATE错误码的最后一个错
$thread_id           为当前连接返回线程ID
$warning_count       返回前一个SQL语句执行过程中产生的警告数量
===================  ============================

上面两个表格列出的mysqli类全部的成员属性和成员方法，当成功创建该类对象以后，就可以调用对象中的成员完成上面两个表格所列出来的功能。下面介绍mysqli类中常见的成员应用。

使用mysqli函数库连接MySQL，支持面向对象和面向过程两种方式：

面向对象的使用方式
^^^^^^^^^^^^^^^^^
1. 建立一个连接

   ``$db = new mysqli('localhost', 'root', '123456', 'dbname');``

2. 如果建立连接时未指定数据库则选择使用的数据库，切换使用的数据库

   ``$db->select_db('dbname');``

3. 查询数据库

   ``$query = "SELECT * FROM user WHERE uid=4";``

   ``$result = $db->query($query);``

4. 统计返回记录的行数

   ``$result_num = $result->num_rows;``

5. 返回一行结果

   .. code-block:: php

       <?php
		$row = $result->fetch_assoc();    //返回一个关联数组，可以通过$row['uid']的方式取得值
		$row = $result->fetch_row();    //返回一个列举数组，可以通过$row[0]的方式取得值
		$row = $result->fetch_array();    //返回一个混合数组，可以通过$row['uid']和$row[0]两种方式取得值
		$row = $result->fetch_object();    //返回一个对象，可以通过$row->uid的方式取得值
       ?>

6. 断开数据库连接

   ``$result->free(); //释放结果集``

   ``$db->close(); //关闭一个数据库连接，这不是必要的，因为脚本执行完毕时会自动关闭连接``

 另外：当进行INSERT、UPDATE、DELETE操作时，使用 ``$db->affected_rows`` 查看影响行数

面向过程的使用方式
^^^^^^^^^^^^^^^^
1. 建立一个连接

   ``$db = mysqli_connect('localhost', 'root', '123456', 'dbname');``

2. 如果建立连接时未指定数据库则选择使用的数据库，切换使用的数据库

   ``mysqli_select_db($db, 'dbname');``

3. 查询数据库

   ``$query = "SELECT * FROM user WHERE uid=4";``

   ``$result = mysqli_query($db, $query);``

4. 统计返回记录的行数

   ``$result_num = mysqli_num_rows($result);``

5. 返回一行结果

   .. code-block:: php

       <?php
		$row = mysqli_fetch_assoc($result);    //返回一个关联数组，可以通过$row['uid']的方式取得值
		$row = mysqli_fetch_row($result);    //返回一个列举数组，可以通过$row[0]的方式取得值
		$row = mysqli_fetch_array($result);    //返回一个混合数组，可以通过$row['uid']和$row[0]两种方式取得值
		$row = mysqli_fetch_object($result);    //返回一个对象，可以通过$row->uid的方式取得值
       ?>

6. 断开数据库连接

   ``mysqli_free_result($result);//释放结果集``

   ``mysqli_close($db);//关闭一个数据库连接，这不是必要的，因为脚本执行完毕时会自动关闭连接``

 另外：当进行INSERT、UPDATE、DELETE操作时，使用mysqli_affected_rows()查看影响行数

连接MySQL服务器
^^^^^^^^^^^^^^
PHP程序在与MySQL服务器交互之前，需要成功地连接。如果选择使用面向对象接口与MySQL服务器连接，第一种方式是通过mysqli类的构造方法实例化对象，其构造方法的原型如下所示：

``class mysqli {__construct([string host[,string username[,string passwd[,string dbname[,int port[,socket]]]]]])}``

通过mysqli类的构造方法原型可以看到，和前面介绍过的mysql_connect()函数相似，都可以接收同样的参数，也都是可选的参数。为了方便起见，如果向它提供前四项信息：MySQL服务器的主机名、MySQL用户名、MySQL用户密码和使用的数据库的名称，就不用再去调用mysqli对象中的connect()和select_db()方法为当前的连接改变它的默认数据库。

这个构造方法还有两个可选的参数：MySQL服务器的端口号、一个套接字文件或命名管道。但这两个可选参数很少使用，例如，MySQL服务器的端口号如果没用改动过，第五个参数默认设置为3306号端口。如果连接成功，该构造方法将返回一个mysqli对象。如下代码所示：

``$mysqli = new mysqli("localhost", "mysql_user", "mysql_pwd", "mylib"); //连接MySQL数据库服务器``

如果在创建mysqli对象时没有向构造方法传入连接参数，就需要多写几行代码，调用mysqli对象中的connect()方法连接MySQL数据库服务器，还可以使用select_db()方法指定数据库。如下代码所示：

.. code-block:: php

    <?php
	$mysqli = new mysqli(); // 创建mysqli对象
	$mysqli->connect("localhost", "mysql_user", "mysql_pwd"); // 连接指定的MySQL数据库服务器
	$mysqli->select_db("mylib"); //选择特定的数据库
    ?>

虽然使用mysqli构造方法建立连接是最常见也是最方便的方法，但也有一个缺点：无法设置任何MySQL特有的连接选项。例如，设置连接倒计时，在连接成功之后立刻执行一个SQL命令等，所以还可以像下面这样去创建一个连接。如下代码所示：

.. code-block:: php

    <?php
	    /*如果没有连接则使用mysqli_init()函数创建一个连接对象 */
	    $mysqli = mysqli_init();

	    /* 下面两行设置连接选项 */
	    $mysqli->options(MYSQLI_INIT_COMMAND, "SET AUTOCOMMIT=0"); //连接成功则执行
	    $mysqli->options(MYSQLI_OPT_CONNECT_TIMEOUT, 5); //设置倒计时

	    /* 通过mysqli对象中的real_connect()方法连接MySQL服务器 */
	    $mysqli->real_connect('localhost', 'mysql_user', 'mysql_pwd', 'mylib');
    ?>

处理连接错误报告
^^^^^^^^^^^^^^^
在连接过程中难免会出现错误，应该及时让用户得到通知。在连接出错时mysqli对象并没有创建成功，所以不能调用mysqli对象中的成员获取这些错误信息，要通过mysqli扩展中的过程方法获取。使用 ``mysqli_connect_errno()`` 函数测试在建立连接的过程中是否发生错误，相关的出错消息由 ``mysqli_connect_error()`` 函数负责返回。如下代码所示：

.. code-block:: php

    <?php
	    $mysqli = new mysqli("localhost", "mysql_user", "mysql_pwd", "dbname");

	    /* 检查连接，如果连接出错输出错误信息并退出程序 */
	    if (mysqli_connect_errno()) {
	        printf("连接失败: %s\n", mysqli_connect_error());
	        exit();
	    }
    ?>



关闭与MySQL服务器连接
^^^^^^^^^^^^^^^^^^^
完成数据库访问工作，如果不再需要连接到数据库，应该明确地释放有关的mysqli对象。虽然脚本执行结束后，所有打开的数据库连接都将自动关闭，资源被回收。但是，在执行过程中，有可能页面需要多个数据库连接，各个连接要在适当的时候将其关闭。mysqli对象中的close()方法负责关闭打开的数据库连接，成功时返回TRUE，否则返回FALSE。

在下面的示例中，连接MySQL数据库服务器、检查连接、通过mysqli对象中的一些成员方法和属性获取连接的详细信息，最后将打开的数据库连接关闭。如下代码所示：

.. code-block:: php

    <?php
	/* 连接MySQL数据库并，成功则返回mysqli 对象*/
	$mysqli = new mysqli("localhost", "mysql_user", "mysql_pwd", "mylib");
	/* 检查连接，如果连接出错输出错误信息并退出程序 */
	if (mysqli_connect_errno()) {
	    printf("连接失败: %s\n", mysqli_connect_error());
	    exit();
	}

	/* 打印当前数据库使用字符集字符串 */
	printf("当前数据库的字符集：%s\n", $mysqli->character_set_name());
	/* 打印客户端版本 */
	printf("客户端版本：%s\n", $mysqli->get_client_info());
	/* 打印服务器主机信息 */
	printf("主机信息：%s\n", $mysqli->host_info);
	/* 打印字符串形式 MySQL 服务器版本 */
	printf("服务器版本：%s\n", $mysqli->server_info);
	/* 打印整数形式MySQL服务器版本 */
	printf("服务器版本：%d\n", $mysqli->server_version);

	/* 关闭打开的数据库连接 */
	$mysqli->close();

	/* 运行结果：
	当前数据库的字符集：utf8
	客户端版本：mysqlnd 5.0.10 - 20111026 - $Id: c85105d7c6f7d70d609bb4c000257868a40840ab $
	主机信息：localhost via TCP/IP
	服务器版本：5.5.53
	服务器版本：50553
	*/
    ?>

执行SQL命令
^^^^^^^^^^^
无论如何通过PHP脚本与MySQL数据库交互，过程都是一样的，创建一个SQL语句，再传递给执行查询的函数。在mysqli类中提供了几种执行SQL命令的方法，其中最常用的是query()方法。对于 ``insert、update、delete`` 等不会返回数据的SQL命令，query()方法在SQL命令成功执行时返回true。在此基础上,还可以通过mysqli对象中的 ``affected_rows`` 属性获取有多少条记录发生变化。而且使用mysqli对象中的 ``insert_id()`` 属性可以返回最后一条insert命令生成的 ``AUTO_INCREMENT`` 编号值。

.. code-block:: php

    <?php
	$mysqli=new mysqli("localhost","mysql_user","mysql_pwd","my_db_name");
	if(mysqli_connect_errno()){
	    printf("连接失败：%s<br>",mysqli_connect_error());
	    exit();
	}

	/* 执行SQL命令向表中插入一条记录，并获取改变的记录和新ID值 */
	if($mysqli->query("insert into 表名(列1,列2) value (‘值1,值2‘)")) {
	    echo "改变的记录数：".$mysqli->affected_rows."<br>";
	    echo "新插入的ID值：".$mysqli->insert_id."<br>";
	}
	$mysqli->close();
    ?>

如果在执行SQL命令发生错误，query()方法将返回false,此时可以通过mysqli对象中的errno、error属性获取错误编号和错误原因。如果执行有返回数据的SQL命令SELECT，执行成功后则返回一个mysqli_result对象。

 mysqli对象中的query()方法每次调用只能执行一条SQL命令，如果想一次执行多条命令，就必须使用mysqli对象中的multi_query()方法。如果想以不同的参数多次执行一条SQL命令，最有效率的办法是先对那条命令做一些预处理然后再执行。

使用mysqli_result类
------------------
这个类的对象包含SELECT查询的结果，和获取结果集中数据的成员方法，以及和查询的结果有关的成员属性。mysqli_result类中包含的全部成员属性和成员方法如下所示：

======================  ===============================
成员方法名                   描述
======================  ===============================
close()                 释放内存并关闭结果集。
data_seek()             明确改变当前结果记录顺序。
fetch_field()           从结果集中获取某一个字段的信息。
fetch_fields()          从结果集中获取所有字段的信息。
fetch_field_direct()    从一个指定的列中获取列详细信息，返回一个包含列信息的对象。
fetch_array()           将以一个普通索引数组和关联数组两种形式返回一条结果记录。
fetch_assoc()           将以一个普通的关联数组的形式返回一条结果记录。
fetch_object()          将以一个对象的形式返回一条结果记录。
fetch_row()             将以一个普通的索引数组的形式返回一条结果记录。
field_seek()            设置结果集中字段的偏移位置。
======================  ===============================

================  ==============================
成员属性名             描述
================  ==============================
$current_field    获取当前结果中指向的字段偏移位置，是一个整数。
$field_count      从查询结果中获取列的个数。
$lengths          返回一个数组，保存在结果集中获取当前行的每一个列的长度。
$num_rows         返回结果集中包含记录的行数。
================  ==============================

创建结果集对象
^^^^^^^^^^^^^
mysqli_result类的对象，默认是通过mysqli对象中的query()方法执行SELECT语句返回的，并把所有的结果数据从MySQL服务器取回到客户端，保存在该对象中。如果希望把结果暂时留在MySQL服务器上，在有需要时才一条条地读取记录过来，就需要在调用query()方法时，在第二个参数中提供一个MYSQL_USE_RESULT值。在处理的数据集合尺寸比较大或不适合一次全部取回到客户端的时候，使用这个参数比较有用。但是，要想知道本次查询到底找到了多少条记录，只能在所有的结果记录被全部读取完毕之后。使用mysqli对象中的query()方法获取结果集的代码，如下所示：

.. code-block:: php

    <?php
	$result = $mysqli->query("SELECT * FROM table1 LIMIT 10"); // 将数据取回到客户端
	$result = $mysqli->query("SELECT * FROM table1", MYSQLI_USE_RESULT); // 留在MySQL服务器上
    ?>

除了使用上面的方法声明所需的结果类型，也可以将mysqli对象中的real_query()方法与mysqli对象中的store_result()或use_result()方法一起使用获取结果集。real_query()方法与query()方法相同，只是无法确定所返回结果集的类型，可以使用store_result()方法获取整个结果集。将所有记录存储在一个对象中，在合适的时候加以解析，这称为缓冲结果集。可以在缓冲结果集的记录中向前和向后导航，甚至直接跳到任意一条记录上。代码如下所示：

.. code-block:: php

    <?php
	$mysqli->real_query("SELECT * FROM TABLE1 LIMIT 10"); //无法确定所返回结果集的类型
	$result = $mysqli->store_result(); //获取一个缓冲结果集
    ?>

由于这种缓冲结果集是获取整个结果集，可能占用非常多的内存，所以一旦结果集操作结束，就要及时回收内存。而使用mysqli对象中的real_query()方法和use_result方法结合，也是从服务器获取结果集，但并不是获取整个集合，而是可以在适当的时候获取各条记录。因为这种方式只是开始结果集的获取，所以不仅无法确定集合中的记录总数，也无法向后导航或跳到某条记录。

回收查询内存
^^^^^^^^^^^
在对结果集结束操作时，则有必要回收集合所需的内存，可以使用mysqli_result对象中的close()方法回收结果集占用的内存。注意，一旦执行了这个方法，结果集就不再可用。

从结果集中获取数据列的信息
^^^^^^^^^^^^^^^^^^^^^^^^
执行查询并准备了结果集之后，就可以开始解析了。解析的内容包括：从结果集中获取需要的记录、字段信息以及整个表的属性等。 解析数据之前先提供一组相关的示例，在MySQL数据库服务器中，创建一个名称为demo的数据库。并在该数据库中创建一个联系人信息表contactInfo，建立数据表的SQL语句如下所示：

.. code-block:: sql

    CREATE TABLE contactInfo ( #创建表contact
	    uid mediumint(8) unsigned NOT NULL AUTO_INCREMENT, #联系人ID
	    name varchar(50) NOT NULL, #姓名
	    departmentId char(3) NOT NULL, #部门编号
	    address varchar(80) NOT NULL, #联系地址
	    phone varchar(20), #联系电话
	    email varchar(100), #联系人的电子邮件
	    PRIMARY KEY(uid) #设置用户ID为主键
    );

数据表contactInfo建立以后，向表中插入多行记录，本例中插入的数据如表13-5所示。


+-----+--------+----------+----------+-------------+--------------------+
| UID | 姓名   | 部门编号 | 联系地址 | 联系电话    | 电子邮件           |
+=====+========+==========+==========+=============+====================+
| 1   | 高某某 | D01      | 海淀区   | 15801688338 | gm@lampbrother.net |
+-----+--------+----------+----------+-------------+--------------------+
| 2   | 洛某某 | D02      | 朝阳区   | 15801681234 | lm@lampbrother.net |
+-----+--------+----------+----------+-------------+--------------------+
| 3   | 峰某某 | D03      | 东城区   | 15801689876 | fm@lampbrother.net |
+-----+--------+----------+----------+-------------+--------------------+
| 4   | 李某某 | D04      | 西城区   | 15801681357 | lm@lampbrother.net |
+-----+--------+----------+----------+-------------+--------------------+
| 5   | 陈某某 | D01      | 昌平区   | 15801682468 | cm@lampbrother.net |
+-----+--------+----------+----------+-------------+--------------------+

与mysql功能扩展模块类似，mysqli接口在结果集对象中也提供了 ``fetch_row()`` 、 ``fetch_array()`` 、 ``fetch_assoc()`` 和 ``fetch_object()`` 四个彼此很相似的方法来依次读取结果数据行。这四个方法只在引用字段的方式上有差别，它们的共同点是：每次调用将自动返回下一条结果记录，如果已经到达结果数据表的末尾，则返回FALSE。以下将对这四个方法进行对比介绍。

$result->fetch_row()
""""""""""""""""""""
该方法从结果集中获取一条结果记录，将值存放在一个索引数组中，与其他三个方法相比是最方便的方法。它的各个字段需要以 ``$row[$n]`` 的方式访问，其中$row是从结果集中获取的一行记录返回的数组，$n为连续的整数下标。因为返回的是索引数组，所以还可以和list()函数结合在一起使用。代码如下所示：

.. code-block:: php

    <?php
	$mysqli = new mysqli("localhost", "mysql_user", "mysql_pwd", "demo");//连接本地demo数据库
	if (mysqli_connect_errno()) {
	    printf("连接失败: %s<br>", mysqli_connect_error());
	    exit();
	}
	$mysqli->query("set names gb2312");//设置字符集为国标2312码
	/* 将部门编号为D01的联系人姓名和电子邮件全部取出存入到结果集中 */
	$result = $mysqli->query("SELECT name, email
	FROM contactInfo WHERE departmentId='D01'");
	echo 'D01部门的联系人姓名和电子邮件：';
	echo '<ol>';
	while(list($name, $email)=$result->fetch_row()){
	    //从结果集中遍历每条数据
	    echo '<li>'.$name.' : '.$email.'</li>';
	    //以列表形式输出每条记录
	}
	echo '</ol>';
	$result->close(); //关闭结果集
	$mysqli->close(); //关闭与数据库的连接
	/* 输出结果如下所示：
	    D01部门的联系人姓名和电子邮件：
	    1.高某某 : gm@lampbrother.net
	    2.陈某某 : cm@lampbrother.net
	 */
    ?>

在上面示例中，也可以通过遍历数组获取同样的输出结果。但通过list()函数和while循环结合使用，遇到每条记录时将字段赋给一个变量，可以简化一些步骤。

$result->fetch_assoc()
""""""""""""""""""""""
该方法将以一个关联数组的形式返回一条结果记录，数据表的字段名表示键，字段内容表示值。使用代码如下所示：

.. code-block:: php

    <?php
	$mysqli = new mysqli("localhost", "mysql_user", "mysql_pwd", "demo");   //连接MySQL数据库
	if (mysqli_connect_errno()) {   //检查连接错误
	    printf("连接失败: %s<br>", mysqli_connect_error());
	    exit();
	}
	$mysqli->query("set names gb2312");   //设置查询字符集
	$result = $mysqli->query("SELECT * FROM contactInfo");
	//执行查询语句获取结果集

	echo '<table width="90%" border="1" align="center">';
	//打印HTML表格
	echo '<caption><h1>联系人信息表</h1></caption>';
	//输出表名
	echo '<th>用户ID</th><th>姓名</th><th>部门编号</th>';
	//输出字段名
	echo '<th>联系地址</th><th>联系电话</th><th>电子邮件</th>';
	while($row=$result->fetch_assoc()){  //循环从结果集中遍历记录
	    echo '<tr align="center">';  //输出行标记
	    echo '<td>'.$row["uid"].'</td>';  //输出用户ID
	    echo '<td>'.$row["name"].'</td>'; //输出用户姓名
	    echo '<td>'.$row["departmentId"].'</td>';  //输出部门编号
	    echo '<td>'.$row["address"].'</td>';  //输出联系地址
	    echo '<td>'.$row["phone"].'</td>';  //输出联系电话
	    echo '<td>'.$row["email"].'</td>';  //输出电子邮件
	    echo '</tr>';
	}
	echo '</table>';
	$result->close();  //关闭结果集释放内存
	$mysqli->close();  //关闭与数据库服务器的连接
    ?>

$result->fetch_array()
""""""""""""""""""""""
该方法可以说是fetch_row()和fetch_assoc()两个方法的结合版本，可以将结果集的各条记录获取为一个关联数组或数值索引数组，或者同时获取为关联数组和索引数组。默认情况下，会同时获取这两种数组。可以通过在该方法的参数中传入如下不同的值来修改这种默认行为。

- MYSQLI_ASSOC：记录被作为关联数组返回，字段名为键，字段内容为值。
- MYSQLI_NUM：记录被作为索引数组返回，按查询中指定的字段名顺序排序。
- MYSQLI_BOTH：这是默认值，记录即作为关联数组又作为索引数组返回。因此，每个字段可以根据其索引偏移引用，也可以根据字段名来引用。

如果没有特殊要求，尽量不要去使用 ``fetch_array()`` 方法。使用前面介绍的 ``fetch_row()`` 或 ``fetch_assoc()`` 方法实现相同的功能，效率会更高一些。

$result->fetch_object()
"""""""""""""""""""""""
该方法与前面三个方法不同，它将以一个对象的形式返回一条结果记录，而不是数组。它的各个字段需要以对象的方式进行访问，数据列的名字区分字母大小写情况。修改上面的示例，假设使用相同的数据，这个示例与前面介绍的fetch_assoc()方法提供的结果相同。代码如下所示：

.. code-block:: php

    <?php
	$mysqli = new mysqli("localhost", "mysql_user",  "mysql_pwd", "demo");   //连接MySQL数据库
	if (mysqli_connect_errno()) {   //检查连接错误
	    printf("连接失败: %s<br>", mysqli_connect_error());
	    exit();
	}
	$mysqli->query("set names gb2312"); //设置查询字符集
	$result = $mysqli->query("SELECT * FROM contactInfo");
	//执行查询语句获取结果集

	echo '<table width="90%" border="1" align="center">';
	//打印HTML表格
	echo '<caption><h1>联系人信息表</h1></caption>'; //输出表名
	echo '<th>用户ID</th><th>姓名</th><th>部门编号</th>';
	//输出字段名
	echo '<th>联系地址</th><th>联系电话</th><th>电子邮件</th>';
	while($rowObj=$result->fetch_object()){
	//循环从结果集中遍历记录
	    echo '<tr align="center">';             //输出行标记
	    echo '<td>'.$rowObj->uid.'</td>';       //输出用户ID
	    echo '<td>'.$rowObj->name.'</td>';      //输出用户姓名
	    echo '<td>'.$rowObj->departmentId.'</td>';  //输出部门编号
	    echo '<td>'.$rowObj->address.'</td>';       //输出联系地址
	    echo '<td>'.$rowObj->phone.'</td>';         //输出联系电话
	    echo '<td>'.$rowObj->email.'</td>';         //输出电子邮件
	    echo '</tr>';
	}
	echo '</table>';
	$result->close();  //关闭结果集释放内存
	$mysqli->close();  //关闭与数据库服务器的连接
    ?>

以上四个结果集中遍历数据的方法，每次调用都将自动返回下一条结果记录。如果想改变这个读取的顺序，可以使用结果集对象中的data_seek()方法明确地改变当前记录位置。还可以使用结果集对象中的num_rows属性，给出结果数据表里的记录个数。还可以使用结果对象中的lengths属性返回一个组，该数组的各个元素是使用以上四个方法最后读取的结果记录中各字段里的字符个数。

从结果集中获取数据列的信息
""""""""""""""""""""""""
在解析结果集时，不仅需要从中遍历数据，有时也需要获取数据表的属性和各个字段的信息。可以通过结果集对象中的 ``field_count`` 属性给出结果数据表里的数据列的个数、使用 ``current_field`` 属性获取指向当前列的位置、使用 ``field_seek()`` 方法改变指向当前列的偏移位置，以及通过 ``fetch_field()`` 方法返回的对象中获取当前列的信息。示例如下所示：

.. code-block:: php

    <?php
	$mysqli = new mysqli("localhost", "mysql_user", "mysql_pwd", "demo");   //连接MySQL数据库
	if (mysqli_connect_errno()) { //检查连接错误
	    printf("连接失败: %s<br>", mysqli_connect_error());
	    exit();
	}
	$mysqli->query("set names gb2312"); //设置查询字符集
	$result = $mysqli->query("SELECT * FROM contactInfo"); //执行查询语句获取结果集

	echo "结果数据表里数据列个数为：" . $result->field_count . "列<br>";  //从查询结果中获取列数
	echo "默认当前列的指针位置为第：" . $result->current_field . "列<br>";    //打印默认列的指针位置
	echo "将指向当前列的指针移动到第二列;<br>";
	$result->field_seek(1); //将当前列指针移至第二列（默认0代表第一列）
	echo "指向当前列的指针位置为第：" . $result->current_field . "列<br>";    //打印当前列的指针位置
	 echo "第二列的信息如下所示：<br>";
	$finfo = $result->fetch_field(); //获取当前列的对象
	echo "列的名称：" . $finfo->name . "<br>"; //打印列的名称
	echo "数据列来自数据表：" . $finfo->table . "<br>"; //打印本列来自哪个数据表
	echo "本列最长字符串的长度" . $finfo->max_length . "<br>"; //打印本列中最长字符串长度

	$result->close(); //关闭结果集释放内存
	$mysqli->close(); //关闭与数据库服务器的连接
	/*
	结果数据表里数据列个数为6列
	默认当前列的指针位置为第0列
	将指向当前列的指针移动到第二列；
	指向当前列的指针位置为第1列
	第二列的信息如下所示：
	    列的名称：name
	    数据列来自数据表：contactInfo
	    本列最长字符串的长度6
	 */
    ?>

使用结果集对象中的 ``fetch_field()`` 方法，只能获取当前的列信息。有关查询结果更详细的数据信息，可以通过对 ``fetch_fields()`` 方法调用的结果进行分析获得。这个方法从查询结果中返回所有列的信息，保存在一个对象数组中，其中每一个对象对应一个数据列的信息。

一次执行多条SQL命令
^^^^^^^^^^^^^^^^^
使用mysqi对象中的query()方法每次调用只能执行一条SQL命令。如果需要一次执行多条SQL命令，就必须使用mysqli对象中的 ``multi_query()`` 方法。具体做法是把多条SQL命令写在同一个字符串里作为参数传递给 ``multi_query()`` 方法，多条SQL之间使用分号（;）分隔。如果第一条SQL命令在执行时没有出错，这个方法就会返回TRUE，否则将返回FALSE。

因为 ``multi_query()`` 方法能够连接执行一个或多个查询，而每条SQL命令都可能返回一个结果，在必要时需要获取每一个结果集。所以对该方法返回结果的处理也有了一些变化，第一条查询命令的结果要用mysqli对象中的 ``use_result()`` 或 ``store_result()`` 方法来读取，当然，使用 ``store_result()`` 方法将全部结果立刻取回到客户端，这种做法效率更高。另外，可以用mysqli对象中的 ``more_results()`` 方法检查是否还有其他结果集。如果想对下一个结果集进行处理，应该调用mysqli对象中的 ``next_result()`` 方法，获取下一个结果集。这个方法返回TRUE（有下一个结果）或FALSE。如果有下一个结果集，也需要使用 ``use_result()`` 或 ``store_result()`` 方法来读取。执行多条SQL命令代码如下所示：

.. code-block:: php

    <?php
	$mysqli = new mysqli("localhost", "mysql_user", "mysql_pwd", "demo");   //连接MySQL数据库
	if (mysqli_connect_errno()) { //检查连接错误
	    printf("连接失败: %s<br>", mysqli_connect_error());
	    exit();
	}
	/* 将三条SQL命令使用分号（;）分隔, 连接成一个字符串 */
	$query = "SET NAMES GB2312;"; //设置查询字符集为GB2312
	$query .= "SELECT CURRENT_USER();"; //从MySQL服务器获取当前用户
	$query .= "SELECT name,phone FROM contactinfo LIMIT 0,2"; //从contactinfo表中读取数据

	if ($mysqli->multi_query($query)) { //执行多条SQL命令
	    do {
	        if ($result = $mysqli->store_result()) { //获取第一个结果集
	            while ($row = $result->fetch_row()) { //遍历结果集中每条记录
	                foreach($row as $data){ //从一行记录数组中获取每列数据
	                    echo $data."&nbsp;&nbsp;"; //输出每列数据
	                }
	                echo "<br>"; //输出换行符号
	            }
	            $result->close(); //关闭一个打开的结果集
	        }
	        if ($mysqli->more_results()) { //判断是否还有更多的结果集
	            echo "-----------------<br>"; //输出一行分隔线
	        }
	    } while ($mysqli->next_result()); //获取下一个结果集，并继续执行循环
	}
	$mysqli->close(); //关闭mysqli连接
	/* 输出结果如下所示：
	mysql_user@localhost
	-----------------
	高某某  15801688338
	洛某某  15801681234
	 */
    ?>

在上面的示例程序中，使用mysqli对象中的 ``multi_query()`` 方法一次执行三条SQL命令，获取多个结果集并从中遍历数据。如果在命令的处理过程中发生了错误， ``multi_query()`` 和 ``next_result()`` 方法就会出现问题。 ``multi_query()`` 方法的返回值，以及mysqli的属性errno、error、info等只与第一条SQL命令有关，无法判断第二条及以后的命令是否在执行时发生了错误。所以在执行 ``multi_query()`` 方法的返回值是TRUE时，并不意味着后续命令在执行时没有出错。

使用mysqli_stmt类
----------------
在生成网页时，许多PHP脚本通常都会执行除参数以外，其他部分完全相同的查询语句，针对这种重复执行一个查询，每次迭代使用不同的参数情况，MySQL从4.1版本开始提供了一种名为预处理语句（prepared statement）的机制。它可以将整个命令向MySQL服务器发送一次，以后只有参数发生变化，MySQL服务器只需对命令的结构做一次分析就够了。这不仅大大减少了需要传输的数据量，还提高了命令的处理效率。可以用mysqli扩展模式中提供的mysqli_stmt类的对象，去定义和执行参数化的SQL命令，mysqli_result类中包含的全部成员属性和成员方法如表13-6和表13-7所示。
表13-6  mysqli_stmt类中的成员方法（共12个）

===================  ===================================
成员方法名                描述
===================  ===================================
bind_param()         该方法把预处理语句各有关参数绑定到一些PHP变量上，注意参数的先后顺序
bind_result()        预处理语句执行查询之后，利用该方法将变量绑定到所获取的字段
close()              一旦预处理语句使用结果之后，它所占用的资源可以通过该方法回收
data_seek()          在预处理语句中移动内部结果的指针
execute()            执行准备好的预处理语句
fetch()              获取预处理语句结果的每条记录，并将相应的字段赋给绑定结果
free_result()        回收由该对象指定的语句占用的内存
result_metadata()    从预处理中返回结果集原数据
prepare()            无论是绑定参数还是绑定结果，都需要使用该方法准备要执行的预处理语句
send_long_data()     发送数据块
reset()              重新设置预处理语句
store_result()       从预处理语句中获取结果集
===================  ===================================

表13-7  mysqli_stmt类中的成员属性（共6个）

================  ===============================================
成员属性名             描述
================  ===============================================
$affected_rows    返回该对象指定的最后一条语句所影响的记录数。注意，该方法只与插入、修改和删除三种查询句有关
$errno            返回该对象指定最近所执行语句的错误代码
$error            返回该对象指定最近所执行语句的错误描述字符串
$param_count      返回给定的预处理语句中需要绑定的参数个数
$sqlstate         从先前的预处理语句中返回SQL状态错误代码
$num_rows         返回stmt对象指定的SELECT语句获取的记录数
================  ===============================================

获取预处理语句对象
^^^^^^^^^^^^^^^^^
在设计PHP程序时，使用预处理语句的最大好处是有关代码可以编写得更精巧、更易于理解，不必为各组参数分别构造一条SQL命令。可以使用mysqli对象中的prepare()方法准备要执行的SQL语句，获得一个 ``mysqli_stmt`` 对象。但要将准备的SQL语句中，各有关参数替换为占位符号，通常使用问号（?）作为占位符号。这条准备执行的SQL语句就被允许存储在MySQL服务器上，但还没有执行。 ``mysqli_stmt`` 对象是后面操作的基础，获取该对象的代码如下所示：

``$stmt = $mysqli->prepare("INSERT INTO tableName
VALUES (?, ?, ?, ?)"); //返回mysqli_stmt对象``

另外，还可以通过mysqli对象中的 ``stmt_init()`` 方法获取一个 ``mysqli_stmt`` 对象。但获取mysqli_stmt对象之后，还要通过该对象中的 ``prepare()`` 方法去准备一个要执行的SQL语句，代码如下所示：

.. code-block:: php

    <?php
	$stmt =  $mysqli->stmt_init();
	//获取一个mysqli_stmt对象
	$stmt->prepare ("INSERT INTO tableName VALUES (?, ?, ?, ?)"); //返回mysqli_stmt对象
    ?>

绑定参数
^^^^^^^^
创建完 ``mysqli_stmt`` 对象并准备了一个要执行的SQL语句之后，接下来，需要使用该对象中的 ``bind_param()`` 方法，把在预处理语句中使用占位符号问号（?）表示的各有关参数，绑定到一些PHP变量上，一定要注意它们的先后顺序是否正确。在 ``bind_param()`` 方法中，第一个参数是必需的，表示该方法中其后多个可选参数变量的数据类型。每个参数的数据类型必须用相应的字符明确给出，表示绑定变量的数据类型字符如表13-8所示。

表13-8  绑定变量的数据类型字符

====  ======================
字符    含义
====  ======================
i     所有INTEGER类型
d     DOUBLE和FLOAT类型
s     所有其他类型（包括字符串）
b     二进制数据类型（BLOB、二进制字节串）
====  ======================

通过 ``bind_param()`` 方法将变量绑定到相应的字段之后，为了实际执行的那条SQL命令，还需要把参数值存入绑定的PHP变量。绑定变量并存入数值的代码如下所示：

.. code-block:: php

    <?php
	$stmt = $mysqli->prepare("INSERT INTO 表名 VALUES
	(?, ?, ?, ?)"); //获取一个mysqli_stmt对象
	$stmt->bind_param('issd', $var1, $var2, $var3, $var4); //绑定参数，其中'issd'表示4个变量类型
	$var1 = 整数值; //给第一个变量赋上整型数值
	$var2 = '字符串1'; //给第二个变量赋上字符串值
	$var3 = "字符串2"; //给第三个变量赋上字符串值
	$var4 = 浮点数值;  //给第四个变量赋上浮点数值
    ?>

执行准备好的语句
^^^^^^^^^^^^^^^
准备好SQL语句并绑定参数，把那些参数放入几个简单的PHP变量之后，就可以调用 ``mysqli_stmt`` 对象中的 ``execute()`` 执行了。因为绑定参数的预处理语句并没有执行过，只是存储在MySQL服务器上，将迭代数据重复地发送给服务器，再将这些迭代数据集成到查询中来执行。

回收资源
^^^^^^^
等不再需要 ``mysqli_stmt`` 对象时，应该立刻明确地释放它占用的资源，可以通过该对象中的 ``close()`` 方法回收。这么做不仅从本地内存释放了这个对象，还通知MySQL服务器后面不会再有这样的命令了，删除它的预处理语句。

简单的实例分析
^^^^^^^^^^^^^
以上一节中介绍的联系人信息表contactInfo为例，向demo数据库的contactinfo表中连接插入多条联系记录。使用预处理语句只需要声明一条SQL命令，并向MySQL服务器送一次，以后插入的记录时，只有参数发生变化即可。代码如下所示：

.. code-block:: php

    <?php
	$mysqli = new mysqli("localhost", "mysql_user", "mysql_pwd", "demo");     //连接MySQL数据库
	if (mysqli_connect_errno()) { //检查连接错误
	    printf("连接失败: %s<br>", mysqli_connect_error());
	    exit();
	}
	//声明一个INSERT语句，并使用$mysqli->prepare()方法对算执行的这个SQL命令进行处理
	$query = "INSERT INTO contactInfo(name, departmentId, address, phone, email) VALUES (?, ?, ?, ?,?)";
	$stmt = $mysqli->prepare($query); //处理打算执行的SQL命令

	//将5个占位符号（?）对应的参数绑定到5个PHP变量中
	$stmt->bind_param('sssss', $name, $departmentId, $address, $phone, $email);

	$name = "张某某"; //为第一个绑定的参数赋上字符串的值
	$departmentId = "D03"; //为第二个绑定的参数赋上字符串的值
	$address = "中关村"; //为第三个绑定的参数赋上字符串的值
	$phone = "15801683721"; //为第四个绑定的参数赋上字符串的值
	$email = "zm@lampbrother.net"; //为第五个绑定的参数赋上字符串的值

	$stmt->execute(); //执行预处理的SQL命令，向服务器发送数据

	echo "插入的行数：" . $stmt->affected_rows . "<br>"; //返回插入的行数
	echo "自动增长的UID：" . $mysqli->insert_id . "<br>"; //返回最后生成的AUTO_INCREMENT值

	//以下几条代码重新为参数赋值，可以随时重复这个过程继续插入记录。
	$name = "白某某";
	$departmentId = "D01";
	$address = "海淀区";
	$phone = "15801689675";
	$email = "bm@lampbrother.net";
	$stmt->execute(); //重新给参数赋值后，再次向服务器发送数据

	echo "插入的行数：" . $stmt->affected_rows . "<br>"; //返回插入的行数
	echo "自动增长的UID：" . $mysqli->insert_id . "<br>";//返回最后生成的AUTO_INCREMENT值

	$stmt->close(); //释放mysqli_stmt对象占用的资源
	$mysqli->close(); //关闭与MySQL数据库的连接
    ?>

在上面的示例中，连续向contate表添加两条记录，也可以重复执行插入更多的记录。采用这个办法执行完 ``INSERT、UPDATE`` 和 ``DELETE`` 命令之后，可以通过 ``mysqli_stmt`` 对象中的 ``affected_rows`` 属性返回被修改的记录个数。如果是 ``INSERT`` 语句，也可以使用mysqli对象中的 ``insert_id`` 属性返回最后生成的 ``AUTO_INCREMENT`` 值。

使用预处理语句处理SELECT查询结果
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
SELECT语句和其他的SQL查询命令不同，它需要处理查询结果。SQL语句的执行也需要使用 ``mysqli_stmt`` 对象中的 ``execute()`` 方法，但与mysqli对象中的 ``query()`` 方法不同， ``execute()`` 方法的返回值并不是一个 ``mysqli_result`` 对象。 ``mysqli_stmt`` 对象提供了一种更为精巧的办法来处理SELECT语句查询结果：在使用 ``execute()`` 方法执行SQL语句完成查询之后，使用 ``mysqli_stmt`` 对象中的 ``bind_result()`` 方法，把查询结果的各个数据列绑定到一些PHP变量上；然后使用 ``mysqli_stmt`` 对象中的 ``fetch()`` 方法把下一条结果记录读取到这些变量里。如果成功地读入下一条记录 ``fetch()`` 方法返回TRUE，否则返回FALSE，或者已经读完所有的结果记录返回FALSE。

默认情况下，SELECT查询结果将留在MySQL服务器上，等待 ``fetch()`` 方法把记录逐条取回到PHP程序中，赋给使用 ``bind_result()`` 方法绑定的PHP变量上。如果需要对所有记录而不只是一小部分进行处理，可以调用 ``mysqli_stmt`` 对象中的 ``store_result()`` 方法，把所有结果一次全部传回到PHP程序中。这样做不仅更有效率，而且能减轻服务器的负担。 ``store_result()`` 方法是可选的，除了读取数据不改变任何东西。以联系人信息表contactinfo为例，使用预处理语句处理SELECT查询结果的代码如下所示：

.. code-block:: php

    <?php
	$mysqli = new mysqli("localhost", "mysql_user", "mysql_pwd", "demo");   //连接MySQL数据库
	if (mysqli_connect_errno()) { //检查连接错误
	    printf("连接失败: %s<br>", mysqli_connect_error());
	    exit();
	}

	$query = "SELECT name, address, phone FROM contactinfo LIMIT 0,3";  //声明SELECT语句
	if ($stmt = $mysqli->prepare($query)) { //处理打算执行的SQL命令
	    $stmt->execute(); //执行SQL语句
	    $stmt->store_result(); //取回全部查询结果
	    echo "记录个数：".$stmt->num_rows."行<br>"; //输出查询的记录个数
	    $stmt->bind_result($name, $address, $phone); //当查询结果绑定到变量中
	    while ($stmt->fetch()) { //逐条从MySQL服务取数据
	        printf ("%s (%s,%s)<br>", $name, $address,  $phone);  //格式化结果输出
	    }
	    $stmt->close(); //释放mysqli_stmt对象占用的资源
	}
	$mysqli->close(); //关闭与MySQL数据库的连接
	/* 输出结果如下所示：
	记录个数：3行
	高某某 (海淀区,15801688338)
	洛某某 (朝阳区,15801681234)
	峰某某 (东城区,15801689876)
	 */
    ?>

如果要获取SELECT语句查找到了多少条记录，可以从mysqli_stmt对象中的num_rows属性中检索出来。但是，这个属性只有在提前执行过store_result()方法，将全部查询结果传回到PHP程序中的情况下才可以使用。
如果在SELECT语句中也使用占位符号（?），并需要多次执行这一条语句时，也可以将mysqli_stmt对象中的bind_param()和bind_result()方法结合起来使用。代码如下所示：

.. code-block:: php

    <?php
	$mysqli = new mysqli("localhost", "mysql_user", "mysql_pwd", "demo");   //连接MySQL数据库
	if (mysqli_connect_errno()) { //检查连接错误
	    printf("连接失败: %s<br>", mysqli_connect_error());
	    exit();
	}
	//声明SELECT语句，按部门编号查找，使用占位符号（?）表示将要查找的部门
	$query = "SELECT name, address, phone FROM  contactinfo WHERE departmentId=? LIMIT 0,3";
	if ($stmt = $mysqli->prepare($query)) { //处理打算执行的SQL命令
	    $stmt->bind_param('s', $departmentId); //绑定参数部门编号
	    $departmentId = "D01"; //给绑定的变量赋上值
	    $stmt->execute(); //执行SQL语句
	    $stmt->store_result(); //取回全部查询结果
	    $stmt->bind_result($name, $address, $phone); //当查询结果绑定到变量中
	    echo "D01部门的联系人信息列表如下：<br>"; //打印提示信息
	    while ($stmt->fetch()) { //逐条从MySQL服务取数据
	        printf("%s (%s,%s)<br>", $name, $address, $phone);     //格式化结果输出
	    }

	    echo "D02部门的联系人信息列表如下：<br>"; //打印提示信息
	    $departmentId = "D02"; //给绑定的变量赋上新值
	    $stmt->execute(); //执行SQL语句
	    $stmt->store_result(); //取回全部查询结果
	    while ($stmt->fetch()) { //逐条从MySQL服务取数据
	        printf("%s (%s,%s)<br>", $name, $address, $phone);     //格式化结果输出
	    }
	    $stmt->close(); //释放mysqli_stmt对象占用的资源
	}
	$mysqli->close(); //关闭与MySQL数据库的连接
	/* 输出结果如下所示：
	D01部门的联系人信息列表如下：
	    高某某 (海淀区,15801688338)
	    陈某某 (昌平区,15801682468)
	    白某某 (海淀区,15801689675)
	D02部门的联系人信息列表如下：
	    洛某某 (朝阳区,15801681234)
	 */
    ?>

在上面的示例中，根据提供的部门参数不同，从数据库中分别取出两个部门的联系人信息。只要使用一次 ``bind_result()`` 方法绑定结果就可以了，并不需要每次执行都把查询结果的各个数据列绑定到一些PHP变量上。

数据库事务
---------
事务是确保数据库一致的机制，是一个或一系列的查询，作为一个单元的一组有序的数据库操作。如果组中的所有SQL语句都操作成功，则认为事务成功，事务则被提交，其修改将作用于所有其他数据库进程。即使在事务的组中只有一个环节操作失败，事务也不成功，则整个事务将被回滚，该事务中所有操作都将被取消。事务功能是企业级数据库的一个重要部分，因为很多业务过程都包括多个步骤。如果任何一个步骤失败，则所有步骤都不应发生。

事务处理
^^^^^^^
在MySQL4.0及以上版本中均默认地启用事务，但MySQL目前只有InnoDB和BDB两个数据表类型才支持事务，两个表类型具有相同的特性，InnoDB表类型具有比BDB还丰富的特性，速度更快，因此建议使用InnoDB表类型。创建InnoDB类型的表实际上与创建任何其他类型表的过程没有区别，如果数据库没有设置为默认的表类型，只要在创建时显式指定要将表创建为InnoDB类型。创建InnoDB类型一个雇员表如下所示：

.. code-block:: sql

    CREATE TABLE employees(
	    userID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
	    name VARCHAR(45) NOT NULL,
	    department VARCHAR(60) NOT NULL,
	    PRIMARY KEY(userID)
    )TYPE=InnoDB;

在默认的情况下，MySQL是以自动提交（autocommit）模式运行的，这就意味着所执行的每一个语句都将立即写入数据库中。但如果使用事务安全的表格类型，是不希望有自动提交的行为的。要在当前的会话中关闭自动提交，执行如下所示的MySQL命令：

``mysql>SET AUTOCOMMIT=0;  //在当前的会话中关闭自动提交``

如果自动提交被打开了，必须使用如下所示语句开始一个事务，如果自动提交是关闭的，不需要使用这条命令，因为当输入一个SQL语句时，一个事务将自动启动。如下所示：

``mysql>START TRANSACTION;  //开始一个事务``

在完成了一组事务的语句输入后，可以使用如下所示语句将其提交给数据库。只有提交了一个事务，该事务才能在其他会话中被其他用户所见，如下所示：

``mysql>COMMIT;  //提交一个事务给数据库``

如果改变主意，可以使用如下所示语句回到数据库以前的状态，如下所示：

``mysql>ROOLBACK;  //事务将被回滚，所有操作都将被取消``

mysqli扩展模块中目前没有提供与SQL命令 ``START TRANSACTION`` 相对应的方法，如果想使用事务，必须执行mysqli对象中的 ``autocommit(0)`` 方法关闭MySQL事务机制的自动提交模式。关闭自动提交模式后，后续执行的所有SQL命令将构成一个事务，直到调用mysqli类对象的 ``commit()`` 方法提交它们或者是调用 ``rollback()`` 方法撤销它们为止。接下来执行的SQL命令又构成了另一个事务，直到再次遇到``commit()`` 或 ``rollback()`` 方法调用。如果忘记了调用mysqli类对象中的 ``commit()`` 方法，或在执行 ``commit()`` 方法之前，一旦有SQL命令执行出错或是失去与MySQL服务器的连接，当前事务里的所有SQL命令都将被撤销。

构建事务应用程序
^^^^^^^^^^^^^^
例如，顾客要进行一次在线购物，选好一款产品，价格为RMB8000.00元，采用网上银行转账方式付款。假设用户userA向用户userB的账户转账，需要从userA账户中减去8000元人民币，并向userB账户加上8000人民币。首先，在demo数据库中准备一个InnoDB类型的数据表（account）。用于保存两个用户的账户信息，包括其姓名和可用现金数据。并向表中插入userA和userB的数据记录。如下所示：

.. code-block:: sql

    CREATE TABLE account( #创建表account
	    userID SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT, #用户ID字段，自动增长
	    name VARCHAR(45) NOT NULL, #用户名字段
	    cash DECIMAL(9,2) NOT NULL, #账户余额字段
	    PRIMARY KEY(userID) #用户ID为主键
    )TYPE=InnoDB; #指为InnoDB表类型

    INSERT INTO account(name, cash) values('userA', '100000'); #插入userA用户数据记录
    INSERT INTO account(name, cash) values('userB', '900000'); #插入userB用户数据记录

在下面示例中，这个转账过程需要执行两条SQL命令完成。真实场景中还会有其他步骤，要把此过程变为一个事务，确保数据不会由于某个步骤的失败而遭到破坏。示例代码如下所示：

.. code-block:: php

    <?php
	$mysqli = new mysqli("localhost", "mysql_user", "mysql_pwd", "demo");   //连接MySQL数据库
	if (mysqli_connect_errno()) { //检查连接错误
	    printf("连接失败: %s<br>", mysqli_connect_error());
	    exit();
	}

	$success = TRUE; //设置事务执行状态
	$price = 8000; //转账的数目

	$mysqli->autocommit(0);   //暂时关闭MySQL事务机制的自动提交模式
	//执行从userA记录中减少cash的值，返回1表示成功，否则执行失败
	$result = $mysqli->query("UPDATE account SET  cash=cash-$price WHERE name='userA'");
	//如果SQL语句执行失败或没有改变记录中的值，将$sucess的值设置为FALSE
	if (!$result or $mysqli->affected_rows != 1) {
	    $success = FALSE;   //设置$sucess的值为FALSE
	}
	//执行向userB记录中添加cash的值，返回1表示成功，否则执行失败
	$result = $mysqli->query("UPDATE account SET  cash=cash+$price WHERE name='userB'");
	//如果SQL语句执行失败或没有改变记录中的值，将$sucess的值设置为FALSE
	if (!$result or $mysqli->affected_rows != 1) {
	    $success = FALSE;   //设置$sucess的值为FALSE
	}

	if ($success) {   //如果$success的值为TRUE
	    $mysqli->commit();    //事务提交给数据库
	    echo "转账成功!";       //输出成功的提示信息
	} else {     //如果$success的值为FLASE，事务中有错误
	    $mysqli->rollback();   //回滚当前的事务，所有SQL命令都被撤销
	    echo "转账失败!";       //输出不成功的提示信息
	}
	$mysqli->autocommit(1);  //开启MySQL事务机制的自动提交模式
	$mysqli->close();  //关闭与MySQL数据库的连接
    ?>

可以看出，在事务的每个步骤执行之后都会检查查询状态和受影响的记录。如果成功$success值为TRUE，调用mysqli对象中的 ``commit()`` 方法提交数据。如果在任何时刻失败，$success都将被设置为FALSE，所有步骤都会在脚本结束时回滚，当前事务里的所有SQL命令都将被撤销。
