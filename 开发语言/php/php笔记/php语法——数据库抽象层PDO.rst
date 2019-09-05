数据库抽象层PDO
=========
PHP与流行的开放源代码的MySQL数据库服务器之间总是很有默契。它们的合作使他们各自都取得了广受推崇的地位。很多PHP应用程序开发人员都习惯于PHP与MySQL这对组合，以至于PHP对其它数据库的支持常常模仿处理MySQL的函数库。然而，并不是所有的数据库处理函数库都是一样的，也不是所有的数据库都提供相同的特性。虽然存在模仿，但不同的PHP数据库扩展都有它们各自的怪癖和不同之处，所以从一种数据库迁移到另一种数据库时会有一些困难。

虽然PHP一直都拥有很好的数据库连接，但PDO的出现让PHP达到一个新的高度。PDO扩展类库为PHP访问数据库定义了一个轻量级、一致性的接口，它提供了一个数据访问抽象层，这样，无论你是用什么数据库，你都可以通过一致的函数执行查询和获取数据。大大简化了数据库的操作，并能够屏蔽不同数据库之间的差异。

POD所支持的数据库
----------------
使用PHP可以处理各种数据库系统，包括MySQL、PostgreSQL、Oracle、MsSQL等，但使用不同的数据库系统时，其所使用的函数是不同的。例如，前面两个章节中介绍的使用mysql或mysqli扩展的函数，只能用来处理MySQL数据库服务器。而如果需要处理Oracle数据库服务器，就必须重新学习PHP中处理Oracle的函数库。应用每种数据库时都需要学习特定的函数库，这样是比较麻烦的，更重要的这使得数据库间的移植难以实现。

为了解决这样的难题，就需要一种“数据库抽象层”。它能解除应用程序逻辑与数据库通信逻辑之间的耦合，通过这个通用接口传递所有与数据库相关的命令，应用程序就能使用多种数据库解决方案中的某一种，只要该数据库支持应用程序所需要的特性，而且抽象层提供了与该数据库兼容的驱动程序。

PDO就是一个“数据库访问抽象层”，作用是统一各种数据库的访问接口，能够轻松地在不同数据库之间进行切换，使得数据库间的移植容易实现。与mysql和mysqli的函数库相比，PDO让跨数据库的使用更具有亲和力；与ADODB和MDB2等同类数据库访问抽象层相比，PDO更高效。另外，PDO与PHP支持的所有数据库扩展都非常相似，因为PDO借鉴了以往数据库扩展的最好特性。

对任何数据库的操作。并不是使用PDo扩展本身执行的，必须针对不同的数据库服务器使用特定的PDO驱动程序访问。驱动程序扩展则为PDO和本地RDBMS客户机API库架起一座架起一座桥梁，用来访问指定的数据库系统。这能大大提高PDO的灵活性，因为PDO的运行时才加载必须的数据库驱动程序，所以不需要在每次使用不同的数据库时重新配置和重新编译PHP。例如，如果数据库服务器需要从MySQL切换到Oracle，只要重新加载PDO_OCI驱动程序就可以了。PDO对其它数据库的支持以及对应使用的驱动名称如表所示：

+--------------+-------------------------------------+
| 驱动名       | 对应访问的数据库                    |
+==============+=====================================+
| PDO_DBLIB    | FreeTDS/Microsoft SQL Server/Sybase |
+--------------+-------------------------------------+
| PDO_FIREBIRD | Firebird/Interbase 6                |
+--------------+-------------------------------------+
| PDO_MYSQL    | MySQL 3.x/4.x/5.x                   |
+--------------+-------------------------------------+
| PDO_OCI      | Oracle                              |
+--------------+-------------------------------------+
| PDO_ODBC     | ODBC v3                             |
+--------------+-------------------------------------+
| PDO_PGSQL    | PostgreSQL                          |
+--------------+-------------------------------------+
| PDO_SQLITE   | SQLite 2.x/3.x                      |
+--------------+-------------------------------------+

要确定所处的环境中可用的PDO驱动程序，可以在浏览器中通过加载phpinfo()函数，查看PDO部分的列表，或者通过查看pdo_drivers()函数返回的数组。

PDO的安装
---------
POD随PHP5.1发行，在PHP5.0的PECL扩展中也可以使用。PDO需要PHP5核心面向对象特性的支持，所以之前的版本无法运行。无论如何，在配置PHP时，仍需要显式地指定所要包括的驱动程序，驱动程序除PDO_SQLITE之外，都需要手动打开。

Linux环境下启动对MySQL的PDO驱动程序支持，需要在安装PHP5.1以上版本的源代码包环境时，向configure命令中添加如下标志：

``--with-pdo-mysql=/usr/local/mysql //其中"/usr/local/mysql"为MySQL服务器安装目录``

如果在安装PHP环境时，要开启其他各个特定PDO驱动程序的更多信息，请参考执行configure --help命令所获得的帮助结果。

在Windows环境下PHP5.1以上版本中，PDO和主要数据库的驱动同PHP一起作为扩展发布，要激活它们只需要简单的编辑php.ini文件。

.. code-block:: ini

    extension=php_pdo.dll //所有PDO驱动程序共享的扩展，必须有
    extension=php_pdo_mysql.dll //如果使用MySQL，那么就添加这一行
    extension=php_pdo_mssql.dll //如果使用SQL Server,那么添加这一行
    extension=php_pdo_odbc.ll  //如果使用ODBC驱动程序，那么添加这一行
    extension=php_pdo_oci.ll  //如果使用Oracle驱动程序，那么添加这一行

保存修改的php.ini文件变化，重启Apache服务器，查看phpinfo()函数，

创建PDO对象
----------
使用PDO在与不同数据库管理系统之间交互时，PDO对象中的成员方法时统一各种数据库的访问接口，所以在使用PDO与数据库交互之前，首先要创建一个PDO对象。在通过构造方法创建对象的同时，需要建立一个与数据库服务器的连接，并选择一个数据库。PDO的构造方法原型如下：

``__construct(string dsn [ , string username [ , string password [ , array driver_options]]])  //PDO的构造方法``

构造方法参数说明：

- 第一个参数：dsn (data source name)数据源名 必选参数 用来定义一个确定的数据库和必须用到的驱动程序。DSN的PDO命名惯例为PDO驱动程序的名称，后面一个冒号，再后面是可选的驱动程序的数据库连接变量信息，如主机名、端口和数据库名。例如，连接Oracle服务器和连接MySQL服务器的DSN格式分别如下所示：

  ``oci:dbname=//localhost:1521/mydb //连接到Oracle服务器的DSN, oci:作为驱动前缀， 主机 localhost, 端口 1521, 数据库 mydb``

  ``mysql:host=localhost;dbname=testdb //连接到MySQL服务器的DSN,mysql:作为驱动前缀，主机 localhost, 数据库 testdb``

- 构造方法中的第二个参数username和第三个参数password 分别用于指定连接数据库的用户名和密码，是可选参数。
- 最后一个参数 ``driver_option`` 需要一个数组，用来指定连接所需要的所有额外选项，传递附加的调优参数到PDO或底层驱动程序。

以多种方式调用构造方法
^^^^^^^^^^^^^^^^^^^^
可以以多种方式调用构造方法创建PDO对象，下面以连接MySQL和Oracle服务器为例，分别介绍构造方法的多种调用方式。

将参数嵌入到构造函数
""""""""""""""""""
在下面的连接Oracle服务器的示例中，在DSN字符串中加载OCI驱动程序并指定里两个可选参数：第一个是数据库名称，第二个是字符集。使用了特定的字符集连接一个特定的数据库，如果不指定任何信息就会使用默认的数据库。代码如下：

.. code-block:: php

    <?php
	try{
	    $dbh = new PDO("OCI:dbname = accounts;charset=UTF8","scott","tiger");
	}catch (PDOException $e){
	    echo "数据库连接失败：".$e->getMessage();
	}
    ?>

``OCI:dbname=accounts`` 告诉PDO它应该使用OCI驱动程序，并且应该使用"accounts"数据库。对于MySQL驱动程序，第一个冒号后面的所有内容都将会被用作MySQL的DSN。连接MySQL 服务器的显示如下：

.. code-block:: php

    <?php
	$dbms = "mysql"; // 数据库的类型
	$dbName ="php_cn"; // 使用的数据库名称
	$user = "root"; // 使用的数据库用户名
	$pwd = "root"; // 使用的数据库密码
	$host = "localhost"; // 使用的主机名称
	$dsn  = "$dbms:host=$host;dbname=$dbName";
	try {
	    $pdo = new PDO($dsn, $user, $pwd);//初始化一个PDO对象，就是创建了数据库连接对象$pdo
	}catch (PDOException $e){
	   echo "数据库连接失败：".$e->getMessage();
	}
    ?>

其他的驱动程序会同样以不同的方式解析它的DSN。如果无法加载驱动程序，或者发生了连接失败，则会抛出一个 ``PDOException`` ，以便您可以决定如何最好的处理该故障。省略 ``try...catch`` 控制结构并无裨益，如果在应用程序的较高级别没有定义异常处理，那么在无法建立数据库连接的情况下，该脚本会终止。

将参数存放在文件中
""""""""""""""""
在创建PDO对象时，可以把DSN字符串放在另一个本地或者远程文件中，并在构造函数中引用这个文件，如下所示：

.. code-block:: php

    <?php
	try{
	    $dbh = new PDO('uri:file:///usr/localhost/dbconnect','webuser','password');
	}catch(PDOException $e){
	    echo '连接失败：'.$e->getMessage();
	}
    ?>

只要将文件 ``/usr/localhost/dbconnect`` 中的DSN驱动改变，就可以在多种数据库系统之间切换，但要确保该文件由负责执行PHP脚本的用户所拥有，而且此用户拥有必要的权限。

引用php.ini文件
""""""""""""""
也可以在PHP服务器的配置文件中维护DSN信息，只要在php.ini文件中吧DSN信息付给一个名为 ``pdo.dsn.aliasname`` 的配置参数，这里 ``aliasname`` 是后面将提供给构造函数的DSN别名。如下所示连接Oracle 服务器，在php.ini中为DSN指定的别名为oraclepdo：

.. code-block:: ini

    [PDO]
    pdo.dsn.oraclepdo = "OCI:dbname=//localhost:1521/mydb;chaset=UTF-8"；

重新启动 Apaceh服务器后，就可以在php程序中，调用PDO构造方法时，在第一个参数中使用这个别名，如下所示：

.. code-block:: php

    <?php
	try{
	    $dbh = new PDO('oraclepdo','scott','tiger');//使用php.ini文件中的oraclepdo别名
	}catch(PDOException $e){
	    echo '连接失败：'.$e->getMessage();
	}
    ?>

PDO与连接有关的选项
""""""""""""""""""
在创建PDO对象时，有一些与数据库连接有关选项，可以将必要的几个选项组成数组传递给构造方法的第四个参数 ``driver_opts`` 中，用来传递附加的调优参数到PDO或底层驱动程序。一些常用的使用选项如表：

=============================  ==============================
选项名                            描述
=============================  ==============================
PDO::ATTR_AUTOCOMMIT           确定PDO是否关闭自定提交功能，设置FALSE值时关闭
PDO::ATTR_CASE                 强制PDO获取的表字段字符的大小转换，或远原样使用列信息
PDO::ATTR_ERRMODE              设置错误处理的模式
PDO::ATTR_PERSISTENT           确定连接是否为持久连接，默认值为FALSE
PDO::ATTR_ORACCLE_NULLS        将返回的空字符串转换为SQL的NULL
PDO::ATTR_PREFETCH             设置应用程序提前获取的数据大小，以K字节单位
PDO::ATTR_TIMEOUT              设置超市之前等待的时间（秒数）
PDO::ATTR_SERVER_INFO          包含与数据库特有的服务器信息
PDO::ATTR_SERVER_VERSION       包含与数据库服务器版本号有关的信息
PDO::ATTR_CLIENT_VERSION       包含与数据库客户端版本号有关的信息
PDO::ATTR_CONNECTION_STATUS    包含数据库特有的与连接状态有关的信息
=============================  ==============================

设置选项名为下表组成的关联数组，作为驱动程序特定的连接选项，传递给PDO构造方法的第四个参数中，在下面的实例中使用连接选项创建持久连接，持久连接的好处是能够避免在每个页面执行到打开和关闭数据库服务器连接，速度更快，如MySQL数据库的一个进程创建了两个连接，PHP则会把原有连接与新的连接合并共享为一个连接，代码如下：

.. code-block:: php

    <?php
	$opt = array(PDO::ATTR_PERSISTENT =>true);
	try{
	    $dbh = new PDO('mysql:host=localhost;dbname=test','dbuser','password',$opt); //使用$opt参数
	}catch(PDOException $e){
	    echo '连接失败：'.$e->getMessage();
	}
    ?>

PDO对象中的成员方法
^^^^^^^^^^^^^^^^^^
当PDO对象创建成功以后，与数据库的连接已经建立，就 可以使用该对象了。PHP与数据库服务之间的交互都是通过PDO对象中的成员方法实现的，该对象中的全部成员方法如下所示：

======================  ==========================================
方法名                     描述
======================  ==========================================
getAttribute()          获取一个"数据库连接对象"的属性
setAttribute()          为一个"数据库连接对象"设定属性
errorCode()             从数据库返回一个错误代号，如果有的话
errorInfo()             从数据库返回一个含有错误信息的数组，如果有的话
exec()                  执行一条SQL语句并返回影响的行数
query()                 执行一条SQL语句并返回一个结果集(PDOStatement对象)
quote()                 返回添加了引号的字符串，以使其可用于SQL语句中
lastInsertId()          返回最新插入到数据库的行（的ID）
prepare()               为执行准备一条SQL语句，返回语句执行后的联合结果集(PDOStatement)
getAvailableDriver()    获取有效的PDO驱动器名称
beginTransaction()      开始一个事务，标明回滚起始点
commit()                提交一个事务,并执行SQL
rollBack()              回滚一个事务
======================  ==========================================

在上表中，从PDO对象中提供的成员方法可以看出，使用PDO对象可以完成与数据库服务器之间的连接管理、存取属性、错误处理、查询执行、预处理语句，以及事务等操作。

使用PDO对象
-----------
PDO扩展类库为PHP访问数据库定义了一个轻量级的、一致性的接口，它提供了一个数据访问抽象层，这样，无论你使用什么数据库，你都可以通过一致的函数执行查询和获取数据。大大简化了数据库的操作，并能够屏蔽不同数据库之间的差异。

调整PDO的行为属性
^^^^^^^^^^^^^^^^
在PDO对象中有很多属性可以用来调整PDO的行为或获取底层驱动程序状态，可以通过查看PHP帮助文档，获得详细的PDO属性列表信息。如果在创建PDO对象时，没有在构造方法中最后一个参数设置过的属性选项，也可以在创建完对象以后，通过PDO对象中的 ``setAttribute()`` 和 ``getAttribute()`` 方法设置和获取这些属性的值。

getAttribute()
""""""""""""""
该方法只需要提供一个参数，传递一个特定的属性名称，如果执行成功，则返回该属性所指定的值，否则返回NULL。示例如下：

.. code-block:: php

    <?php
	$opt = array(PDO::ATTR_PERSISTENT => TRUE);
	try {
	    $dbh = new PDO('mysql:dbname=testdb;host=localhost', 'mysql_user', 'mysql_pwd', $opt);
	} catch (PDOException $e) {
	    echo '数据库连接失败:' . $e->getMessage();
	    exit;
	}
	//如果有异常发生则退出程序}
	echo "\nPDO是否关闭自动提交功能:" . $dbh->getAttribute(PDO::ATTR_AUTOCOMMIT);
	echo "\n当前PDO的错误处理模式:" . $dbh->getAttribute(PDO::ATTR_ERRMODE);
	echo "\n表字段字符的大小写转换:" . $dbh->getAttribute(PDO::ATTR_CASE);
	echo "\n与连接状态相关特有信息:" . $dbh->getAttribute(PDO::CONNECTION_STATUS);
	echo "\n空字符串转换为SQL的null" . $dbh->getAttribute(PDO::ORACLE_NULLS);
	echo "\n应用程序提前获取数据大小:" . $dbh->getAttribute(PDO::ATTR_PERSISTENT);
	echo "\n与数据库特有的服务器信息:". $dbh->getAttribute(PDO::ATTR_SERVER_INFO);
	echo "\n数据库服务器版本号信息:". $dbh->getAttribute(PDO::ATTR_SERVER_VERSION);
	echo "\n数据库客户端版本号信息:". $dbh->getAttribute(PDO::ATTR_CLIENT_VERSION);
    ?>

setAttribute()
""""""""""""""
这个方法需要两个参数，第一个参数提供PDO对象的特定的属性名，第二个参数则是为这个指定的属性赋一个值。例如设置PDO的错误模式，需要如下设置PDO对象中ATR_ERROMODE属性的值：

``$dbh->setAttribute(PDO::ATTR_ERRMODE,PDO::ERRMODE_EXCEPTION); //设置抛出异常处理错误``

PDO处理PHP程序和数据库之间的数据类型转换
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
PDO在某种程度上是对类型不可知的，因此它将任何类型数据都表示为字符串。而不是将其转换为整数或双精度类型。因为字符串类型是最精确的类型，在PHP中具有最广泛的应用范围，过早地将数据转换为整数或者双精度类型可能会导致截断或舍入错误。通过将数据以字符串抽出，PDO为你提供了一些脚本控制，使用普通的PHP类型转换方式就可以控制如何进行转换以及何时进行转换。

如果结果集中的某列包含一个NULL值，PDO则会将其映射为PHP的NULL值。Oracle在将数据返回PDO时会将空字符串转换为NULL，但是PHP支持的任何其他数据库都不会这样处理，从而导致了可移植性问题。PDO提供了一个驱动程序级属性PDO_ATTR_ORACLE_NULLS,该属性会为其它数据驱动程序模拟此行为。此属性设置为TRUE，在获取时会把空字符串转换为NULL，默认情况下该属性值为FALSE。如下：

``$dbh->setAttribute(PDO::ATTR_ORACLE_NULLS,true);``

该属性设置以后，通过$dbh对象打开的任何语句中的空字符串都将被转换为NULL。

PDO的错误处理模式
^^^^^^^^^^^^^^^^
PDO提供了三种不同的错误处理模式，不仅可以满足不同风格的编程，也可以调整扩展处理错误的方式。

PDO::ERRMODE_SILENT
"""""""""""""""""""""
这是默认模式，在错误发生时不进行任何操作，PDO将只设置PDOStatement对象的errorCode属性。开发人员可以通过对象中的errorCode和errorInfo()方法对语句和数据库对象进行检查。如果错误是由于对语句对象的调用而产生的，那么可以在那个语句对象上调用errorCode()或errorInfo()方法。如果错误是由于调用数据库对象而产生的，那么可以在哪个数据库对象上调用上述两个方法。

PDO::ERRMODE_WARNING
""""""""""""""""""""
除了设置错误代码以为，PDO还将发出一条PHP传统E_WARNING消息，可以使用常规的PHP错误处理程序捕获该警告。如果你只是想看看发生了什么问题，而无意中断应用程序的流程，那么在调试或测试当中这种设置很有用。该模式的设置方式如下：

``$pdo->setAttribute(PDO::ATTR_ERRMODE,PDO::ERRMODE_WARNING);``

PDO::ERRMODE_EXCEPTION
""""""""""""""""""""""
除了设置错误代码以外，PDO还将抛出一个PDOException,并设置其属性，以反映错误代码和错误消息。这种设置在调试中
很有用，因为它会放大脚本中产生错误的地方，该错误方式配合try{}catch(){}使用最好。

``$pdo->setAttribute(PDO::ATTR_ERRMODE,PDO::ERRMODE_EXCEPTION);//设置抛出异常处理错误``

SQL标准提供了一组用于指示SQL查询结果的诊断代码，称为SQLSTATE代码。PDO定制了使用SQL-92 SQLSTATE错误代码字符串的标准，不同PDO驱动程序负责将它们本地代码映射为适当的SQLSTATE代码。例如，可以在MySQL安装目录下的include/sql_state.h文件中找到MySQL的SQLSTATE代码列表。可以使用PDO对象或是PDOStatement对象中的errorCode()方法返回一个SQLSTATE代码。如果需要关于一个错误的更多特定的信息，在这两个对象中还提供了一个errorInfo()方法，该方法将返回一个数组，其中包含SQLSTATE代码、特定于驱动程序的错误代码，以及特定于驱动程序的错误字符串。

使用PDO执行SQL语句
^^^^^^^^^^^^^^^^^
在使用PDO执行查询数据之前，先提供一组相关的数据。创建PDO对象并通过mysql驱动连接localhost的MySQL数据库服务器，MySQL服务器的登录名为“mysql_user”，密码为“mysql_pwd”。创建一个以“testdb”命名的数据库，并在该数据库中创建一个联系人信息表contactInfo。建立数据表的SQL语句如下所示：

.. code-block:: sql

    CREATE TABLE contactInfo (    //创建表contact
	uid mediumint(8) unsigned NOT NULL AUTO_INCREMENT,  //联系人ID
	name varchar(50) NOT NULL,   //姓名
	departmentId char(3) NOT NULL,  //部门编号
	address varchar(80) NOT NULL,  //联系地址
	phone varchar(20),  //联系电话
	email varchar(100), //联系人的电子邮件
	PRIMARY KEY(uid)   //设置用户ID为主键
    );

数据表contactInfo建立以后，向表中插入多行记录，本例中插入的数据如表14-4所示。

.. image:: ./_static/images/record.jpg

在PHP脚本中，通过PDO执行SQL查询与数据库进行交互，可以分为三种不同的策略，使用哪一种方法取决于你要做什么操作。

使用PDO::exec()方法
"""""""""""""""""""
当执行INSERT、UPDATE和DELETE等没有结果集的查询时，使用PDO对象中的exec()方法去执行。该方法成功执行后，将返回受影响的行数。注意，该方法不能用于SELECT查询。示例如下所示：

.. code-block:: php

    <?php
	try{
	    $dbh=new PDO('mysql:dbname=testdb;host=localhost','mysql_user','mysql_pwd');
	}catch(PDOException $e){
	    echo '数据库连接失败：'.$e->getMessage();
	    exit;
	}
	$query="UPDATE contactInfo SET phone='15801680168' where name='高某某'"; //声明UPDATE查询
	$affected=$dbh->exec($query);  //使用exec()方法可以执行INSERT、UPDATE和DELETE等
	if($affected){ //如果执行成功将返回受影响的行数
	    echo '数据表contactInfo中受影响的行数为：'.$affected; //$affected的值为1
	}else{
	    print_r($dbh->errorInfo());  //如果执行查询时出错，可以使用errorInfo()查看
	}
    ?>

使用PDO::query()方法
""""""""""""""""""""
当执行返回结果集的SELECT查询时，或者所影响的行数无关紧要时，应当使用PDO对象中的query()方法。如果该方法成功执行指定的查询，返回一个PDOStatement对象。如果使用了query()方法，并想了解影响的行总数，可以使用PDO对象中的rowCount()方法获取。示例代码如下所示：

.. code-block:: php

    <?php
	try {
	    $dbh = new PDO('mysql:dbname=testdb;host=localhost', 'mysql_user', 'mysql_pwd');
	    $dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION); //设置捕获异常
	} catch (PDOException $e) {
	    echo '数据库连接失败：' . $e->getMessage();
	    exit; //执行则退出程序
	}
	$query = "SELECT name, phone, email FROM contactInfo WHERE departmentId='D01'";
	try { //使用异常处理试着执行下面的代码
	    $pdostatement = $dbh->query($query); //执行SELECT查询，并返回PDOstatement对象
	    echo "一共从表中获取到" . $pdostatement->rowCount() . "条记录:\n";
	    foreach ($pdostatement as $row) { //从PDOstatement对象中遍历结果
	        echo $row['name'] . "\t";  //输出从表中获取到的联系人的名字
	        echo $row['phone'] . "\t";  //输出从表中获取到的联系人的电话
	        echo $row['email'] . "\n"; //输出从表中获取到的联系人的电子邮件
	    }
	} catch (PDOException $e) { //如果出现异常进入下面代码块执行
	    echo $e->getMessage(); //使用PDOException对象中的getMessage()方法输出提示
	    print_r($dbh->errorInfo()); //使用PDO对象中的errorInfo()方法输出获取到的错误数组
	}
    ?>

使用PDO:prepare()和PDOStatement::execute()方法
"""""""""""""""""""""""""""""""""""""""""""""
当同一个查询需要多次执行时(有时需要迭代传入不同的列值)，使用预处理语句的方式来实现效率会更高。使用预处理语句就需要使用PDO对象中的prepare()方法去准备一个将要执行的查询，再使用PDOStatement对象中的execute()方法来执行。

PDO对预处理语句的支持
-------------------
PDO支持使用占位符（?）语法将变量绑定到SQL中的预处理语句，与前面介绍过的MySQLi扩展中对预算理语句的支持类似。PDO几乎为所支持的所有数据库提供了命名占位符模拟，甚至可以为生来就不支持该概念的数据库模拟预处理语句和绑定参数。这是PHP向前迈进的积极一步，因为这样可以使开发人员能够用PHP编写“企业级”的数据库应用程序，而不必特别关注数据库平台的能力。

预处理语句的作用是，编译一次，可以多次执行，会在服务器上缓存查询的语法和执行过程，而只在服务器和客户端之间传输有变化的列值，以此来消除这些额外的开销。可以有效防止SQL注入，在执行单个查询时快于直接使用query()/exec()的方法，速度快而且安全，推荐使用。

了解PDOStatement对象
^^^^^^^^^^^^^^^^^^^^
通过PDO使用预处理语句功能之前，我们先来了解一下PDO扩展中的PDOStatement类。如果通过执行PDO对象中的query()方法返回的PDOStatement类对象，就代表的是一个结果集对象。如果通过执行PDO对象中的prepare()方法产生的PDOStatement类对象，则为一个查询对象。PDOStatement类中的全部成员方法如表14-5所示。

===============================  =========================================================
方法名                              描述                                                       
===============================  =========================================================
PDOStatement::bindColumn         绑定一列到一个PHP变量，用来匹配列名和一个指定的变量名，这样每次获取各行记录时，会自动将相应的列值赋给该变量  
PDOStatement::bindParam          绑定一个参数到相应的查询占位符上                                         
PDOStatement::bindValue          把一个值绑定到对应的一个参数中                                          
PDOStatement::closeCursor        关闭游标，使语句能再次被执行。                                          
PDOStatement::columnCount        返回结果集中的列数                                                
PDOStatement::debugDumpParams    打印一条 SQL 预处理命令                                           
PDOStatement::errorCode          获取跟上一次语句句柄操作相关的 SQLSTATE                                 
PDOStatement::errorInfo          获取跟上一次语句句柄操作相关的扩展错误信息                                    
PDOStatement::execute            负责执行一个准备好的预处理查询                                          
PDOStatement::fetch              从结果集中获取下一行，当到达结果集末尾时返回FALSE                              
PDOStatement::fetchAll           返回一个包含结果集中所有行的数组                                         
PDOStatement::fetchColumn        返回结果集中下一行某个列的值。(某行某列的单个值)                                          
PDOStatement::fetchObject        获取下一行记录并返回它作为一个对象。                                       
PDOStatement::getAttribute       检索一个语句属性                                                 
PDOStatement::getColumnMeta      返回结果集中一列的元数据                                             
PDOStatement::nextRowset         在一个多行集语句句柄中推进到下一个行集                                      
PDOStatement::rowCount           返回受上一个SQL语句影响的行数                                         
PDOStatement::setAttribute       为一个预处理语句设置属性                                             
PDOStatement::setFetchMode       设置需要结果集合的类型                                              
===============================  =========================================================

准备语句
^^^^^^^
重复执行一个SQL查询，通过每次迭代使用不同的参数，这种情况使用预处理语句运行效率最高。使用预处理语句，首先要先准备好一个SQL查询语句，但并不需要马上执行。对于一个准备好的SQL语句，如果在每次执行时都要改变一些列值，这种情况必须使用占位符号而不是具体的列值。而在PDO中有两种使用占位符的语法：“命名参数”和“问号参数”，使用哪一种语法要看个人的喜好。

- 使用命名参数作为占位符的INSERT查询如下所示：

  ``$dbh->prepare("INSERT INTO contactInfo (name, address, phone) VALUES (:name, :address, :phone)");``

- 使用问号（?）参数作为占位符的INSERT查询如下所示：

  ``$dbh->prepare("INSERT INTO contactInfo (name, address, phone) VALUES (?, ?, ?)");``

不管使用哪一种参数作为占位符构成的查询，都需要使用PDO对象中的prepare()方法，去准备这个将要用于迭代执行的查询，并返回PDOStatement类对象。

绑定参数
^^^^^^^
当查询准备好之后，需要在每次执行时替换输入的参数。可以通过PDOStatement对象中的bindParam()方法，把参数绑定到准备好的查询中相应的占位符上。方法bindParame()的原型如下所示：

``bindParam ( mixed parameter, mixed &variable [, int data_type [, int length [, mixed driver_options]]] )``

- 第一个参数parameter是必选项，如果在准备好的查询中，占位符语法使用名字参数时，将名字参数字符串作为bindParam()方法的第一个参数提供。如果占位符语法使用问号参数时，将准备好的查询中列值占位符的索引偏移量，作为该方法的第一个参数提供。
- 第二个参数variable也是必选项，提供赋给第一个参数所指定占位符的值。它需要按引用传递，在结合准备存储过程使用此方时，可以根据存储过程的某个动作修改这个值。因为该参数是按引用传递，所以只能提供变量作为参数，不能直接提供数值。
- 第三个参数data_type是可选项，显示地为当前被绑定的参数设置数据类型。可以为以下值。

  + PDO::PARAM_BOOL：代表boolean数据类型。
  + PDO::PARAM_NULL：代表SQL中NULL类型。
  + PDO::PARAM_INT：代表SQL中INTEGER数据类型。
  + PDO::PARAM_STR：代表SQL中CHAR、VARCHAR和其他字符串数据类型。
  + PDO::PARAM_LOB：代表SQL中大对象数据类型。
  + PDO::PARAM_STMT：代表PDOStatement对象类型。
  + PDO::PARAM_INPUT_OUTPUT：专为存储过程使用的数据类型，可以在过程执行后修改。

- 第四个参数length是可选项，用于指定数据类型的长度，当在第三个参数中使用 ``PDO_PARAM _INPUT_OUTPUT`` 数据类型时必须使用这个参数。
- 第五个参数 ``driver_options`` 是可选项，通过该参数提供任何数据库驱动程序特定的选项。

将上一节中使用两种占位符语法准备的SQL查询，使用bindParam()方法分别绑定上对应的参数。查询中使用名字参数的绑定示例如下所示：

.. code-block:: php

    <?php
	$query="INSERT INTO contactInfo (name, address, phone) VALUES (:name, :address, :phone)";
	$stmt=$dbh->prepare($query);   //调用PDO对象中的prepare()方法
	$name="张某某";  //声明一个参数变量$name
	$address="北京海淀区中关村";  //声明一个参数变量$address
	$phone="15801688988";  //声明一个参数变量$phone
	//第二个参数需要按引用传递，所以需要变量作为参数
	$stmt->bindParam(':name', $name);  //将变量$name的引用绑定到准备好的查询名字参数’:name’中
	$stmt->bindParam(':address', $address);  //将变量address的引用绑定到查询的名字参数’:address’中
	$stmt->bindParam(':phone', $phone); //将变量phone的引用绑定到查询的名字参数’:phone’中
    ?>

查询中使用问号（?）参数的绑定示例如下所示，并在绑定时通过第三个参数显示指定数据类型：

.. code-block:: php

    <?php
	$query="INSERT INTO contactInfo (name, address, phone) VALUES (?, ?, ?)";
	$stmt=$dbh->prepare($query);   //调用PDO对象中的prepare()方法
	$name="张某某";  //声明一个参数变量$name
	$address="北京海淀区中关村";   //声明一个参数变量$address
	$phone="15801688988";   //声明一个参数变量$phone
	//第二个参数需要按引用传递，所以需要变量作为参数
	$stmt->bindParam(1, $name, PDO::PARAM_STR);   //将变量$name绑定到查询中的第一个问号参数中
	$stmt->bindParam(2, $address,PDO::PARAM_STR);  //将变量$address绑定到查询的第二个问号参数中
	$stmt->bindParam(3, $phone,PDO::PARAM_STR,20);  //将变量$phone绑定到查询的第三个问号参数中
    ?>

执行准备好的查询
^^^^^^^^^^^^^^
当准备好查询并绑定了相应的参数，就可以通过调用 ``PDOStatement`` 类对象中的 ``execute()`` 方法反复执行了。在下面的示例中，向前面提供的contactInfo表中，使用预处理方式连续执行同一个查询，通过改变不同的参数添加两条记录。如下所示：

.. code-block:: php

    <?php
	try{
	    $dbh=new PDO('mysql:dbname=testdb;host=localhost','mysql_user','mysql_pwd');
	}catch(PDOException $e){
	    echo '数据库连接失败：'.$e->getMessage();
	    exit;
	}
	$query="INSERT INTO contactInfo (name, address, phone) VALUES (?, ?, ?)";
	$stmt=$dbh->prepare($query);    //调用PDO对象中的prepare()方法准备查询
	$name="赵某某";                            //声明一个参数变量$name
	$address="海淀区中关村";             //声明一个参数变量$address
	$phone="15801688348";              //声明一个参数变量$phone
	$stmt->bindParam(1, $name);    //将变量$name绑定到查询中的第一个问号参数中
	$stmt->bindParam(2, $address);   //将变量$address绑定到查询的第二个问号参数中
	$stmt->bindParam(3, $phone);    //将变量$phone绑定到查询的第三个问号参数中
	$stmt->execute();                    //执行参数被绑定值后的准备语句
	$name="孙某某";                       //为变量$name重新赋值
	$address="宣武区";                  //为变量$address重新赋值
	$phone="15801688698";         //为变量$phone重新赋值
	$stmt->execute();                   //再次执行参数被绑定值后的准备语句，插入第二条语句
    ?>

如果你只是要传递输入参数，并且有许多这样的参数要传递，那么你会觉得下面所示的快捷方式语法非常有帮助。是通过在 ``execute()`` 方法中提供一个可选参数，该参数由准备查询中的命名参数占位符组成的数组，这是第二种为预处理查询在执行中替换输入参数的方式。此语法使你能够省去对 ``$stmt->bindParam()`` 的调用。将上面的示例做如下修改：

.. code-block:: php

    <?php
	try{
	    $dbh=new PDO('mysql:dbname=testdb;host=localhost','mysql_user','mysql_pwd');
	}catch(PDOException $e){
	    echo '数据库连接失败：'.$e->getMessage();
	    exit;
	}
	$query="INSERT INTO contactInfo (name, address, phone) VALUES (:name, :address, :phone)";
	$stmt=$dbh->prepare($query);   //调用PDO对象中的prepare()方法准备查询，使用命名参数
	//传递一个数组为预处理查询中的命名参数绑定值，并执行一次。
	$stmt->execute(array(":name"=>"赵某某",":address"=>"海淀区", ":phone"=>"15801688348"));
	//再次传递一个数组为预处理查询中的命名参数绑定值，并执行第二次插入数据。
	$stmt->execute(array(":name"=>"孙某某",":address"=>"宣武区", ":phone"=>"15801688698"));
    ?>

获取数据
^^^^^^^
PDO的数据获取方法与其他数据库扩展都非常类似，只要成功执行SELECT查询，都会有结果集对象生成。不管是使用PDO对象中的query()方法，还是使用prepare()和execute()等方法结合的预处理语句，执行SELECT查询都会得到相同的结果集对象PDOStatement。都需要通过PDOStatement类对象中的方法将数据遍历出来，以下介绍PDOStatement类中常见的几个获取结果集数据的方法。

fetch()方法
"""""""""""
``PDOStatement`` 类中的 ``fetch()`` 方法可以将结果集中当前行的记录以某种方式返回，并将结果集指针移至下一行，当到达结果集末尾时返回 ``FALSE`` 。该方法的原型如下：

``fetch ( [int fetch_style [, int cursor_orientation [, int cursor_offset]]] ) //返回结果集的下一行``

- 第一个参数fetch_style是必选项。获取的一行数据记录中，各列的引用方式取决于这个参数如何设置。可以使用的设置有以下六种。

  + PDO::FETCH_ASSOC：从结果集中获取按列名为索引的关联数组。
  + PDO::FETCH_NUM：从结果集中获取一个按列在行中的数值偏移为索引的值数组。
  + PDO::FETCH_BOTH：这是默认值，包含上面两种数组。
  + PDO::FETCH_OBJ：从结果集当前行的记录中获取其属性对应各个列名的一个对象。
  + PDO::FETCH_BOUND：使用fetch()以布尔值的形式返回结果，并将获取的列值赋给通过bindParm()方法中指定的相应变量。
  + PDO::FETCH_LAZY：以关联数组、数字索引和对象3种形式返回结果。

- 第二个参数cursor_orientation是可选项，PDOStatement对象的一个滚动游标，可用于获取指定的一行。
- 第三个参数cursor_offset也是可选项，需要提供一个整数值，表示要获取的行相对于当前游标位置的偏移。

在下面的示例中，使用PDO对象中的query()方法执行SELECT查询，获取联系人信息表contactInfo中的信息，并返回PDOStatement类对象作为结果集。然后通过fetch()方法结合while循环遍历数据，并以HTML表格的形式输出。代码如下所示：

.. code-block:: php

    <?php
	try {
	    $dbh = new PDO('mysql:dbname=testdb;host=localhost', 'mysql_user', 'mysql_pwd'); // 创建PDO对象
	} catch (PDOException $e) {  // 捕获异常
	    echo '数据库连接失败：' . $e->getMessage();       // 输出异常信息
	    exit;  // 退出程序
	}
	echo '<table border="1" align="center" width="90%">';  // 输出表格开始标记
	echo '<caption><h1>联系人信息表</h1></caption>';  // 输出表格标题
	echo '<tr bgcolor="#cccccc">';   // 输出列名的行标记
	echo '<th>UID</th><th>姓名</th><th>联系地址</th><th>联系电话</th><th>电子邮件</th></tr>';
	$stmt = $dbh->query("select uid,name,address,phone,email from contactInfo");  //执行SELECT语句
	while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) { // 以ASSOC方式获取数据并遍历
	echo '<tr>';    //输出每行的开始标记
	echo '<td>'.$row["uid"].'</td>';    //从结果行数组中获取uid
	echo '<td>'.$row["name"].'</td>';   //从结果行数组中获取name
	echo '<td>'.$row["address"].'</td>';   //从结果行数组中获取address
	echo '<td>'.$row["phone"].'</td>';    //从结果行数组中获取phone
	echo '<td>'.$row["email"].'</td>';    //从结果行数组中获取email
	echo '</tr>';   //输出每行的结束标记
	}
	echo '</table>'; // 输出表格的结束标记
    ?>

fetchAll()方法
""""""""""""""
fetchAll()方法与上一个方法fetch()类似，但是该方法只需要调用一次就可以获取结果集中的所有行，并赋给返回的数组。该方法的原型如下：

``fetchAll([int fetch_style [, int column_index]]) //一次调用返回结果集中所有行``

- 第一个参数fetch_style是必选项，以何种方式引用所获取的列取决于该参数。默认值为 ``PDO::FETCH _BOTH`` ，所有可用的值可以参考在fetch()方法中介绍的第一个参数的列表，还可以指定 ``PDO::FETCH_COLUMN`` 值，从结果集中返回一个包含单列的所有值。
- 第二个参数column_index是可选项，需要提供一个整数索引，当在 ``fetchAll()`` 方法的第一个参数中指定 ``PDO::FETCH_COLUMN`` 值时，从结果集中返回通过该参数提供的索引所指定列的所有值。

.. code-block:: php

    <?php
	try {
	    $dbh = new PDO('mysql:dbname=testdb;host=localhost', 'mysql_user', 'mysql_pwd'); // 创建PDO对象
	} catch (PDOException $e) {  // 捕获异常
	    echo '数据库连接失败：' . $e->getMessage();       // 输出异常信息
	    exit;  // 退出程序
	}
	echo '<table border="1" align="center" width="90%">';  // 输出表格开始标记
	echo '<caption><h1>联系人信息表</h1></caption>';  // 输出表格标题
	echo '<tr bgcolor="#cccccc">';   // 输出列名的行标记
	echo '<th>UID</th><th>姓名</th><th>联系地址</th><th>联系电话</th><th>电子邮件</th></tr>';
	$stmt = $dbh->query("select uid,name,address,phone,email from contactInfo");  //执行SELECT语句
	$allRows = $stmt->fetchAll(PDO::FETCH_NUM)); // 以索引下标从结果集中获取所有数据
	foreach($allRows as $row) {
	    echo '<tr>';    //输出每行的开始标记
	    echo '<td>'.$row[0].'</td>';    //从结果行数组中获取uid
	    echo '<td>'.$row[1].'</td>';   //从结果行数组中获取name
	    echo '<td>'.$row[2].'</td>';   //从结果行数组中获取address
	    echo '<td>'.$row[3].'</td>';    //从结果行数组中获取phone
	    echo '<td>'.$row[4].'</td>';    //从结果行数组中获取email
	    echo '</tr>';   //输出每行的结束标记
	}
	echo '</table>'; // 输出表格的结束标记

	/*   以下是在fetchAll()方法中使用两个特别参数的演示示例  */
	$stmt->execute();   //再次执行一个准备好的SELECT语句
	$row=$stmt->fetchAll(PDO::FETCH_COLUMN, 1);   //从结果集中获取第二列的所有值
	echo '所有联系人的姓名：';   //输出提示
	print_r($row);   //输出获取到的第二列所有姓名数组
    ?>

该程序的输出结果和前一个示例相似，只是多输出一个包含所有联系人姓名的数组。在很大程度上是为了出于方便考虑，选择使用fetchAll()方法代替fetch()方法。但使用fetchAll()处理特别大的结果集时，会给数据库服务器资源和网络带宽带来很大的负担。

setFetchMode()方法
""""""""""""""""""
PDOStatement对象中的fetch()和fetchAll()两个方法，获取结果数据的引用方式默认都是一样的，既按列名索引又按列在行中的数值偏移（从0开始）索引的值数组，因为它们的默认模式都被设置为PDO::FETCH_BOTH值。如果计划使用其他模式来改变这个默认设置，可以在fetch()或fetchAll()方法中提供需要的模式参数。但如果多次使用这两个方法，在每次调用时都需要设置新的模式来改变默认的模式。这时就可以使用PDOStatement类对象中的setFetchMode()方法，在脚本页面的顶部设置一次模式，以后所有fetch()和fetchAll()方法的调用都将生成相应引用的结果集，减少了多次在调用fetch()方法时的参数录入。

bindColumn()方法
""""""""""""""""
使用该方法可以将一个列和一个指定的变量名绑定，这样在每次使用fetch()方法获取各行记录时，会自动将相应的列值赋给该变量，但必须是在fetch()方法的第一个参数设置为 ``PDO::FETCH_BOUND`` 值时。bindColumn()方法的原型如下所示：

``bindColumn(mixed column, mixed &param [, int type]) //设置绑定列值到变量上``

- 第一个参数column为必选项，可以使用整数的列偏移位置索引（索引值从1开始），或是列的名称字符串。
- 第二个参数param也是必选项，需要传递一个引用，所以必须提供一个相应的变量名。
- 第三个参数type是可选项，通过设置变量的类型来限制变量值，该参数支持的值和介绍bindParam()方法时提供的一样。该方法的应用示例如下所示：

.. code-block:: php

    <?php
	try {
	    $dbh = new PDO('mysql:dbname=testdb;host=localhost', 'mysql_user', 'mysql_pwd');
	    $dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	} catch (PDOException $e) {
	    echo '数据库连接失败：' . $e->getMessage();
	    exit;
	}
	//声明一个SELECT查询，从表contactInfo中获取D01部门的四个字段的信息
	$query = "SELECT uid, name, phone, email FROM contactInfo WHERE departmentId='D01'";
	try {
	    $stmt = $dbh->prepare($query); //准备声明好的一个查询
	    $stmt->execute(); //执行准备好的查询
	    $stmt->bindColumn(1, $uid); //通过列位置偏移数绑定变量$uid
	    $stmt->bindColumn(2, $name);  //通过列位置偏移数绑定变量$name
	    $stmt->bindColumn('phone', $phone); //绑定列名称到变量$phone上
	    $stmt->bindColumn('email', $email); //绑定列名称到变量$email上
	    while ($row = $stmt->fetch(PDO::FETCH_BOUND)) {      //fetch()方法传入特定的参数遍历
	        echo $uid . "\t" . $name . "\t" . $phone . "\t" . $email . "\n";//输出自动将列值赋给对应变量的值
	    }
	} catch (PDOException $e) {
	    echo $e->getMessage();
	}
	/*
	根据前面给出的数据样本，有三条符合条件的数据记录，输出的结果如下：
	1       高某某     15801680168     gmm@lampbrother.net
	4       王某某     15801681357     wmm@lampbrother.net
	5       陈某某     15801682468     cmm@lampbrother.net
	 */
    ?>

大数据对象的存取
^^^^^^^^^^^^^^
在项目开发时，有时会需要在数据库中存储“大型”数据。大型对象可以是文本的数据，也可以是二进制的图片、电影等。PDO允许在bindParam()或bindColumn()调用中通过使用PDO::PARAM_LOB类型代码来使用大型数据类型。PDO::PARAM_LOB告诉PDO将数据映射为流，所以可以使用PHP中文件处理函数来操纵这样的数据。下面是将上传的图像插入到一个数据库中的示例：

.. code-block:: php

    <?php
	$dbh=new PDO('mysql:dbname=testdb;host=localhost','mysql_user','mysql_pwd');       //连接数据库
	$stmt = $dbh->prepare("insert into images(contenttype, imagedata) values (?, ?)"); //准备插入查询
	$fp = fopen($_FILES['file']['tmp_name'], 'rb'); //使用fopen()函数打开上传的文件
	$stmt->bindParam(1, $_FILES['file']['type']);   //将上传文件的MIME类型绑定到第一个参数中
	$stmt->bindParam(2, $fp, PDO_PARAM_LOB);     //将上传文件的二进制数据和第二个参数绑定
	$stmt->execute();    //执行准备好的并绑定了参数的查询
    ?>

上面的介绍很简明扼要，现在让我们试试另一面，从数据库取一幅图像，并使用fpassthru()函数将给定的文件指针，从当前的位置读取到EOF并把结果写到输出缓冲区。如下所示：

.. code-block:: php

    <?php
	$dbh=new PDO('mysql:dbname=testdb;host=localhost','mysql_user','mysql_pwd');  //连接数据库
	$stmt = $dbh->prepare("select contenttype, imagedata from images where id=?");   //准备好的查询
	$stmt->execute(array($_GET['id']));     //通过表单中输入的ID值和参数绑定，并执行查询
	list($type, $lob) = $stmt->fetch();     //获取结果集中的大数据类型和文件指针
	header("Content-Type: $type");    //将从表中读取的大文件类型作为合适的报头发送
	// 将给定的文件指针从当前的位置读取到 EOF 并把结果写到输出缓冲区。
	fpassthru($lob);   //发送图片并终止脚本
    ?>

这两个例子都是宏观层次的，被选取的大型对象是一个文件流，可以通过所有常规的流函数来使用它。例如fgets()、fread()或stream_get_contents()等文件处理函数。

PDO的事务处理
------------
在前面介绍的mysqli扩展中接触过事务，它有4个重要特征：原子性（Atomicity）、一致性（Consistency）、独立性（Isolation）和持久性（Durability），即ACID。对于在一个事务中执行的任何工作，即使它是分阶段执行的，也一定可以保证该工作会安全地应用于数据库，并且在工作被提交时，不会受到来自其他连接的影响。

在PDO中事务方法如下：

- 开启事务beginTransaction()方法

  beginTransaction()方法将关闭自动提交模式，知道事务提交或者回滚以后才恢复。

- 提交事务commit()方法

  commit()方法完成事务的提交操作，成功则返回true，否则返回false。

- 事务回滚rollBack()方法

  rollBack()方法执行事务的回滚操作。

并不是每种数据库都支持事务，PDO只为能够执行事务的数据库提供事务支持，所以当第一次打开连接时，PDO需要在“自动提交（auto-commit）”模式下运行。如果需要一个事务，那么必须使用PDO对象中的 ``beginTransaction()`` 方法来启动一个事务。如果底层驱动程序不支持事务，那么将会抛出一个 ``PDOException`` 异常。在一个事务中，可以使用PDO对象中的commit()方法或rollBack()方法来结束该事务，这取决于事务中运行的代码是否成功。一个简单的事务处理示例如下所示：

.. code-block:: php

    <?php
	try {
	    $dbh=new PDO('mysql:dbname=testdb;host=localhost','mysql_user','mysql_pwd');
	    $dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	    $dbh->beginTransaction();   //启动一个事务
	    $dbh->exec("insert into staff (id, name, Positions) values (23, 'Tom', 'programmer')");
	    $dbh->exec("insert into salarychange (id, amount, changedate) values (23, 5000, NOW())");
	    $dbh->commit();     //如果上面两条执行成功，该方法执行则提交事务
	} catch (Exception $e) {
	    $dbh->rollBack();   //执行失败，则回滚事务开始以来发生的所有更改
	    echo '失败：'.$e->getMessage();   //打印出一条错误消息
	}
    ?>

在上面的示例中，假设我们为一个新雇员创建一组条目，这个雇员有的ID号设定为23。除了输入这个人的基本数据外，我们还需要记录雇员的薪水。两个更新分别完成起来很简单，但通过将这两个更新包括在beginTransaction()和commit()调用中，就可以保证在更改完成之前，其他人无法看到更改。如果发生了错误，catch块可以回滚事务开始以来发生的所有更改，并打印出一条错误消息。

PDO中存储过程
------------
DO中存储过程允许在更接近于数据的位置操作数据，从而减少带宽的使用，它们使数据独立于脚本逻辑，允许使用不同语言的多个系统以相同的方式访问数据，从而节省了花费在编码和调试上的宝贵时间，同时他使用预定义的方案执行操作，提高查询速度，并且能阻止与数据的直接相互作用，从而起到保护数据的作用！

首先来讲解如何在PDO中调用存储过程，这里先创建一个存储过程，其SQL语句如下：

.. code-block:: sql

    drop procedure if exists pro_reg;
    delimiter//
    create procedure pro_reg(in nc varchar(80),in pwd varchar(80), in email varchar(80),in address varchar(50))
    begin
    insert into tb_reg(name,pwd,email,address)values(nc,pwd,email,address);
    end;
    //

- “drop”语句删除MySQL服务器中已经存在的存储过程pro_reg。
- “delimiter//”的作用是将语句结束符更以为“//”。
- “in nc varchar(80).......in address varchar(50)”表示要向存储过程中传入的参数。
- “begin......end”表示存储过程中的语句块，它的作用类似与PHP语言中的“{.......}”。

存储过程创建成功后，下面调用存储过程，实现用户注册信息的添加操作，具体步骤如下。

创建index.php文件。首先，创建form表单，将用户信息通过POST方法提交到本页。然后，在本页中编写PHP脚本，通过PDO连接MySQL数据库，并且设置数据库编码格式为UTF-8，获取表单中提交的用户注册信息。接着，通过call语句调用存储过程pro_reg，将用户注册信息添加到数据表中。最后，通过try...catch...语句块返回错误信息。关键代码如下：

.. code-block:: php

	<form name="form1" action="4.php" method="post">
	    用户昵称：<input type="text" name="nc"><br>
	    密   码：<input type="password" name="password"><br>
	    邮   箱：<input type="text" name="email"><br>
	    地  址：<input type="text" name="address"><br>
	    <input type="submit" name="Submit" value="注册">
	    <input type="submit" name="Submit" value="重写">
	</form>
    <?php
	if($_POST["Submit"]){
	    header("Content-Type:text/html; charset=utf-8");    //设置页面的编码格式
	    $dbms = "mysql";                                  // 数据库的类型
	    $dbName ="php_cn";                                //使用的数据库名称
	    $user = "root";                                   //使用的数据库用户名
	    $pwd = "root";                                    //使用的数据库密码
	    $host = "localhost";                              //使用的主机名称
	    $dsn  = "$dbms:host=$host;dbname=$dbName";
	    try {
	        $pdo = new PDO($dsn, $user, $pwd);//初始化一个PDO对象，就是创建了数据库连接对象$pdo
	        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	        $nc = $_POST['nc'];
	        $password = md5($_POST['password']);
	        $email = $_POST['email'];
	        $address = $_POST['address'];
	        $query = "call pro_reg('$nc','$password','$email','$address)";
	        $res = $pdo->prepare($query);//准备查询语句
	        if ($res->execute()) {
	            echo "添加数据库成功";
	        } else {
	            echo "添加数据库失败";
	        }
	    }catch (PDOException $e){
	        echo "PDO Exception Caught";
	        echo 'Error with the database:<br>';
	        echo 'SQL Query;'.$query;
	        echo '<pre>';
	        echo "Error:".$e -> getMessage()."<br>";
	        echo "Code:".$e ->getCode()."<br>";
	        echo "File:".$e ->getFile()."<br>";
	        echo "Line:".$e ->getLine()."<br>";
	        echo "Trace:".$e ->getTraceAsString()."<br>";
	        echo  "</pre>";
	    }
	}
    ?>