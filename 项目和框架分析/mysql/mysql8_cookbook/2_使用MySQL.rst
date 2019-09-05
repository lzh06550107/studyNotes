*********
使用MySQL
*********

在本章中，我们将会覆盖如下问题：

- 使用命令行客户端连接MySQL；
- 创建数据库；
- 创建表；
- 插入，更新和删除行；
- 加载样本数据；
- 选择数据；
- 排序结果；
- 对结果分组(聚合函数)；
- 创建用户；
- 对用户授权和撤销；
- 将数据选择到文件和表中；
- 导入数据到表中；
- 连接表；
- 存储过程；
- 函数；
- 触发器；
- 视图；
- 事件；
- 获取数据库和表的元信息；

使用命令行客户端连接MySQL
-------------------------
安装完成后，就可以使用命令行客户端工具 ``mysql`` 来连接 MySQL 服务端。要想连接服务端，需要在客户端电脑上安装 ``mysql-client`` 包，或者通过 ``SSH`` 连接到服务器中，使用服务器安装的客户端。

.. code-block:: shell

    shell> mysql -h localhost -P 3306 -u <username> -p<password>
    shell> mysql --host=localhost --port=3306 --user=root --password=<password>
    shell> mysql --host localhost --port 3306 --user root --password=<password

.. note:: 不要在命令行中直接提供密码，应该让客户端提示你输入密码的时候，输入密码。

.. code-block:: shell

    shell> mysql --host=localhost --port=3306 --user=root --password
    Enter Password:

1. ``-P`` (大写)参数指定端口；
2. ``-p`` (小写)参数指定密码；
3. 在 ``-p`` 参数与密码之间不能存在空格；
4. 对于 ``password`` ，在 ``=`` 后面不存在空白；

当连接服务端后，你可以使用 ``；、\g、\G`` 符号分隔命令。 ``;或者\g`` 水平输出； ``\G`` 垂直输出；为了取消命令，输入 ``Ctrl + C`` 命令：

创建数据库
----------

.. code-block:: shell

    shell> mysql -u root -p
    Enter Password:
    mysql> CREATE DATABASE company;
    mysql> CREATE DATABASE `my.contacts`;

后面的反引用符号（`）用于引用标识符，如数据库和表名称。当数据库包含特殊字符，如(.)号时。

切换数据库：

.. code-block:: shell

    mysql> USE company
    mysql> USE `my.contacts`

或者，我们可以直接连接数据库：

.. code-block:: shell

    shell> mysql -u root -p company

查找你当前连接的数据，使用如下命令：

.. code-block:: shell

    mysql> SELECT DATABASE();
    +------------+
    | DATABASE() |
    +------------+
    | company    |
    +------------+
    1 row in set (0.00 sec)

为了查找所有数据库，可以使用如下命令：

.. code-block:: shell

    mysql> SHOW DATABASES;
    +--------------------+
    | Database           |
    +--------------------+
    | company            |
    | my.contacts        |
    | information_schema |
    | mysql              |
    | performance_schema |
    | sys                |
    +--------------------+
    6 rows in set (0.00 sec)

数据库创建为 ``data directory`` 中的目录。 默认数据目录是 ``/var/lib/mysql`` 用于基于存储库的包安装，而 /usr/local/mysql/data/ 用于通过二进制文件安装。要了解当前的 ``data directory`` ，可以执行如下命令：

.. code-block:: shell

    mysql> SHOW VARIABLES LIKE 'datadir';
    +---------------+------------------------+
    | Variable_name | Value                  |
    +---------------+------------------------+
    | datadir       | /usr/local/mysql/data/ |
    +---------------+------------------------+
    1 row in set (0.00 sec)

查看 ``data directory`` 内文件：

.. code-block:: shell

    shell> sudo ls -lhtr /usr/local/mysql/data/

创建表
------
当在一个表中定义列时，你应该提供列名称，数据类型和默认值。MySQL 数据类型文档( https://dev.mysql.com/doc/refman/8.0/en/data-types.html )：

1. Numeric： TINYINT, SMALLINT, MEDIUMINT, INT, BIGINT, BIT ；
2. 浮点数字： DECIMAL, FLOAT, DOUBLE ；
3. 字符串：CHAR, VARCHAR, BINARY, VARBINARY, BLOB, TEXT, ENUM, SET ；
4. 还支持空间数据类型。参考 https://dev.mysql.com/doc/refman/8.0/en/spatial-extensions.html
5. JSON数据类型；

.. code-block:: shell

    mysql> CREATE TABLE IF NOT EXISTS
    `company`.`customers` (
    `id` int unsigned AUTO_INCREMENT PRIMARY KEY,
    `first_name` varchar(20),
    `last_name` varchar(20),
    `country` varchar(20)
    ) ENGINE=InnoDB;

- AUTO_INCREMENT ：自动生成线性增量序列，因此您无需担心为每行分配 ``ID`` 值。
- PRIMARY KEY ：等价于 ``UNIQUE`` 列且 ``NOT NULL`` 。如果一个表包含一个 ``AUTO_INCREMENT`` 列，它被认为是 ``PRIMARY KEY`` 。
- Engine ：存在 InnoDB, MyISAM,FEDERATED, BLACKHOLE, CSV, MEMORY 等存储引擎。

为了列出所有存储引擎，执行如下命令：

.. code-block:: shell

    mysql> SHOW ENGINES\G

为了列出所有的表，使用如下命令：

.. code-block:: shell

    mysql> SHOW TABLES;
    +-------------------+
    | Tables_in_company |
    +-------------------+
    | customers         |
    | payments          |
    +-------------------+
    2 rows in set (0.00 sec)

为了查看表的结构，执行如下命令：

.. code-block:: shell

    mysql> SHOW CREATE TABLE customers\G
    或者
    mysql> DESC customers;

当创建表成功后，MySQL会在 data directory 目录创建对应表名称的 ``.ibd`` 文件：

.. code-block:: shell

    shell> sudo ls -lhtr /usr/local/mysql/data/company
    total 256K
    -rw-r----- 1 mysql mysql 128K Jun 4 07:36 customers.ibd
    -rw-r----- 1 mysql mysql 128K Jun 4 08:24 payments.ibd

克隆表结构
^^^^^^^^^^
你可以克隆一个表的结构为一个新表：

.. code-block:: shell

    mysql> CREATE TABLE new_customers LIKE customers;

你可以验证新表的结构：

.. code-block:: shell

    mysql> SHOW CREATE TABLE new_customers\G

参考 https://dev.mysql.com/doc/refman/8.0/en/create-table.html 页面获取创建表的更多选项。

插入，更新和删除行
------------------

插入
^^^^^
``INSERT`` 语句用来插入新记录到表中：

.. code-block:: shell

    mysql> INSERT IGNORE INTO
    `company`.`customers`(first_name, last_name,country)
    VALUES
    ('Mike', 'Christensen', 'USA'),
    ('Andy', 'Hollands', 'Australia'),
    ('Ravi', 'Vedantam', 'India'),
    ('Rajiv', 'Perera', 'Sri Lanka');

或者你可以直接提供 ``id`` 来插入指定的 ``id`` 值：

.. code-block:: shell

    mysql> INSERT IGNORE INTO `company`.`customers`(id,first_name, last_name,country)
    VALUES
    (1, 'Mike', 'Christensen', 'USA'),
    (2, 'Andy', 'Hollands', 'Australia'),
    (3, 'Ravi', 'Vedantam', 'India'),
    (4, 'Rajiv', 'Perera', 'Sri Lanka');

- IGNORE: 如果存在该语句，如果该行记录已经存在，则忽略该插入语句。

更新
^^^^^
``UPDATE`` 语句被用来修改表中存在的记录：

.. code-block:: shell

    mysql> UPDATE customers SET first_name='Rajiv', country='UK' WHERE id=4;

.. note:: 如果不给where 子句，则会更新整个表的记录。最好在事务中修改数据。

删除
^^^^^

.. code-block:: shell

    mysql> DELETE FROM customers WHERE id=4 AND first_name='Rajiv';

.. note:: 如果不给where 子句，则会删除整个表的记录。最好在事务中删除数据。

REPLACE和INSERT ON DUPLICATE KEY UPDATE
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
在许多情况下，您需要处理重复行。行的唯一性由主键标识。如果某行已存在，则 ``REPLACE`` 只删除该行并插入新行。 如果不存在行，则 ``REPLACE`` 表现为 ``INSERT`` 。

``ON DUPLICATE KEY UPDATE`` 用于在行已存在时要执行操作的情况。 如果指定 ``ON DUPLICATE KEY UPDATE`` 选项并且 ``INSERT`` 语句在 ``PRIMARY KEY`` 中导致重复值，则 ``MySQL`` 将根据新值对旧行执行更新。

假设您希望在从同一客户收到付款时更新之前的金额，并在客户第一次付款时同时插入新记录。 为此，您将定义金额列，并在新付款到来时更新它：

.. code-block:: shell

    mysql> REPLACE INTO customers VALUES (1,'Mike','Christensen','America');
    Query OK, 2 rows affected (0.03 sec)

您可以看到两行受到影响，删除了一个重复行并插入了一个新行，而 ``ON DUPLICATE KEY UPDATE`` 则不同：

.. code-block:: shell

    mysql> INSERT INTO payments VALUES('Mike Christensen', 200) ON DUPLICATE KEY UPDATE payment=payment+VALUES(payment);
    Query OK, 1 row affected (0.00 sec)

- VALUES(payment)：指 ``INSERT`` 语句中给出的值。 ``payment`` 是指表格的列。

截断表格
^^^^^^^^
使用 ``TRUNCATE TABLE`` 语句删除整张表的数据，仅保留表的结构。

.. code-block:: shell

    mysql> TRUNCATE TABLE customers;

加载样本数据
------------
为接下来的演示提供样本数据 ``employee`` 。

1. 下载打包文件

.. code-block:: shell

    shell> wget 'https://codeload.github.com/datacharmer/test_db /zip/master' -O master.zip

2. 解压文件

.. code-block:: shell

    shell> unzip master.zip

3. 加载数据

.. code-block:: shell

    shell> cd test_db-master
    shell> mysql -u root -p < employees.sql

4. 验证数据

.. code-block:: shell

    shell> mysql -u root -p employees -A

选择数据
--------
演示怎么从样本 ``employee`` 数据库检索数据。SELECT 语句详情请参考 https://dev.mysql.com/doc/refman/8.0/en/select.html

从 ``employee`` 数据库的 ``departments`` 表中选择所有数据。 您可以使用星号（ ``*`` ）从表中选择所有列。不建议使用它，您应该始终只选择您需要的数据：

.. code-block:: shell

    mysql> SELECT * FROM departments;
    +---------+--------------------+
    | dept_no | dept_name          |
    +---------+--------------------+
    | d009    | Customer Service   |
    | d005    | Development        |
    | d002    | Finance            |
    | d003    | Human Resources    |
    | d001    | Marketing          |
    | d004    | Production         |
    | d006    | Quality Management |
    | d008    | Research           |
    | d007    | Sales              |
    +---------+--------------------+
    9 rows in set (0.00 sec)

选择列
^^^^^^
假设你需要来自 ``dept_manager`` 表的 ``emp_no`` 和 ``dept_no`` 字段：

.. code-block:: shell

    mysql> SELECT emp_no, dept_no FROM dept_manager;

count
^^^^^
查找 ``employee`` 表中雇员的个数：

.. code-block:: shell

    mysql> SELECT COUNT(*) FROM employees;
    +----------+
    | COUNT(*) |
    +----------+
    | 300024   |
    +----------+
    1 row in set (0.03 sec)

基于条件的过滤
^^^^^^^^^^^^^^
使用 ``first_name`` 为 ``Georgi`` 和 ``last_name`` 为 ``Facello`` 查找 ``employees`` 表中的 ``emp_no`` 字段：

.. code-block:: shell

    mysql> SELECT emp_no FROM employees WHERE first_name='Georgi' AND last_name='Facello';
    +--------+
    | emp_no |
    +--------+
    | 10001  |
    | 55649  |
    +--------+
    2 rows in set (0.08 sec)

所有的过滤条件通过 ``WHERE`` 语法。除了整数和浮点数据，其它所有值应该保护在引号中。

操作符
^^^^^^
MySQL 支持很多操作符来过滤结果。请参考 https://dev.mysql.com/doc/refman/8.0/en/comparison-operators.html 来查看操作符列表。

- = ；
- IN ：检查值是否在值集合中

.. code-block:: shell

    mysql> SELECT COUNT(*) FROM employees WHERE last_name IN ('Christ', 'Lamba', 'Baba');
    +----------+
    | COUNT(*) |
    +----------+
    | 626      |
    +----------+
    1 row in set (0.08 sec)

- BETWEEN...AND : 检查值在指定范围内

.. code-block:: shell

    mysql> SELECT COUNT(*) FROM employees WHERE hire_date BETWEEN '1986-12-01' AND '1986-12-31';
    +----------+
    | COUNT(*) |
    +----------+
    | 3081     |
    +----------+
    1 row in set (0.06 sec)

- NOT : 您可以通过使用 ``NOT`` 运算符前置来简单地否定结果。

.. code-block:: shell

    mysql> SELECT COUNT(*) FROM employees WHERE hire_date NOT BETWEEN '1986-12-01' AND '1986-12-31';
    +----------+
    | COUNT(*) |
    +----------+
    | 296943   |
    +----------+
    1 row in set (0.08 sec)

简单模式匹配
^^^^^^^^^^^^
您可以使用 ``LIKE`` 运算符。使用下划线( ``_`` )来匹配一个字符。使用 ``％`` 来匹配任意数量的字符。

- 查找 ``first_name`` 以 ``christ`` 开头的所有雇员：

.. code-block:: shell

    mysql> SELECT COUNT(*) FROM employees WHERE
    first_name LIKE 'christ%';
    +----------+
    | COUNT(*) |
    +----------+
    | 1157     |
    +----------+
    1 row in set (0.06 sec)

- 查找 ``first_name`` 以 ``christ`` 开头 ``ed`` 结尾的所有雇员：

.. code-block:: shell

    mysql> SELECT COUNT(*) FROM employees WHERE first_name LIKE 'christ%ed';
    +----------+
    | COUNT(*) |
    +----------+
    | 228      |
    +----------+
    1 row in set (0.06 sec)

- 查找 ``first_name`` 包含 ``sri`` 的所有雇员：

.. code-block:: shell

    mysql> SELECT COUNT(*) FROM employees WHERE first_name LIKE '%sri%';
    +----------+
    | COUNT(*) |
    +----------+
    | 253      |
    +----------+
    1 row in set (0.08 sec)

- 查找 ``first_name`` 以 ``er`` 结尾的所有雇员：

.. code-block:: shell

    mysql> SELECT COUNT(*) FROM employees WHERE first_name LIKE '%er';
    +----------+
    | COUNT(*) |
    +----------+
    | 5388     |
    +----------+
    1 row in set (0.08 sec)

- 查找名字以任意两个字符开头然后是 ``ka`` ，然后是任意数量的字符的所有员工的计数：

.. code-block:: shell

    mysql> SELECT COUNT(*) FROM employees WHERE first_name LIKE '__ka%';
    +----------+
    | COUNT(*) |
    +----------+
    | 1918     |
    +----------+
    1 row in set (0.06 sec)

正则表达式
^^^^^^^^^^
您可以使用 ``RLIKE`` 或 ``REGEXP`` 运算符在 ``WHERE`` 子句中使用正则表达式。存在很多方式使用 ``REGEXP`` 请参考 https://dev.mysql.com/doc/refman/8.0/en/regexp.html

+--------+----------------------------+
| 表达式 | 描述                       |
+========+============================+
| *      | 0个或多个匹配              |
+--------+----------------------------+
| +      | 1个或多个匹配(等于{1, })   |
+--------+----------------------------+
| ?      | 0个或1个匹配(等于{0,1})    |
+--------+----------------------------+
| .      | 表示任意字符               |
+--------+----------------------------+
| ^      | 以什么开始                 |
+--------+----------------------------+
| $      | 以什么结束                 |
+--------+----------------------------+
| [abc]  | 只是a,b 或 c               |
+--------+----------------------------+
| [^abc] | 不是a,b 也不是 c           |
+--------+----------------------------+
| [a-z]  | 字符a到z                   |
+--------+----------------------------+
| [0-9]  | 数字0到9                   |
+--------+----------------------------+
| ^...$  | 以什么开始和结束           |
+--------+----------------------------+
| \d     | 任意数字任意               |
+--------+----------------------------+
| \D     | 任意非数字                 |
+--------+----------------------------+
| \s     | 任意空白                   |
+--------+----------------------------+
| \S     | 任意非空白                 |
+--------+----------------------------+
| \w     | 任何字母字符               |
+--------+----------------------------+
| \W     | 任意非字母字符             |
+--------+----------------------------+
| {n}    | 指定数目的匹配             |
+--------+----------------------------+
| {n,}   | 不少于指定数目的匹配       |
+--------+----------------------------+
| {n,m}  | 匹配数目的范围(m不超过255) |
+--------+----------------------------+

- 查找 ``first_name`` 以 ``christ`` 开头的所有雇员个数：

.. code-block:: shell

    mysql> SELECT COUNT(*) FROM employees WHERE first_name RLIKE '^christ';
    +----------+
    | COUNT(*) |
    +----------+
    | 1157     |
    +----------+
    1 row in set (0.18 sec)

- 查找 ``last_name`` 以 ``ba`` 结尾的所有雇员个数：

.. code-block:: shell

    mysql> SELECT COUNT(*) FROM employees WHERE last_name REGEXP 'ba$';
    +----------+
    | COUNT(*) |
    +----------+
    | 1008     |
    +----------+

- 查找 last_name 不包含 a,e,i,o,u 的所有雇员的个数：

.. code-block:: shell

    mysql> SELECT COUNT(*) FROM employees WHERE last_name NOT REGEXP '[aeiou]';
    +----------+
    | COUNT(*) |
    +----------+
    | 148      |
    +----------+
    1 row in set (0.11 sec)

限制结果
^^^^^^^^

.. code-block:: shell

    mysql> SELECT first_name, last_name FROM employees WHERE hire_date < '1986-01-01' LIMIT 10;

使用表别名
^^^^^^^^^^

.. code-block:: shell

mysql> SELECT COUNT(*) AS count FROM employees WHERE hire_date BETWEEN '1986-12-01' AND '1986-12-31';

排序结果
--------
您可以根据列或别名列来排序结果。您可以指定降序的 ``DESC`` 或升序的 ``ASC`` 。默认情况下，排序将是升序。您可以将 ``LIMIT`` 子句与 ``ORDER BY`` 组合以限制结果。

.. code-block:: shell

    mysql> SELECT emp_no,salary FROM salaries ORDER BY salary DESC LIMIT 5;
    +--------+--------+
    | emp_no | salary |
    +--------+--------+
    | 43624  | 158220 |
    | 43624  | 157821 |
    | 254466 | 156286 |
    | 47978  | 155709 |
    | 253939 | 155513 |
    +--------+--------+
    5 rows in set (0.74 sec)

您也可以在 ``SELECT`` 语句中提及列的位置，而不是指定列名。例如，您正在 ``SELECT`` 语句中的第二个位置选择薪水。 因此，您可以指定 ``ORDER BY 2`` ：

.. code-block:: shell

mysql> SELECT emp_no,salary FROM salaries ORDER BY 2 DESC LIMIT 5;

对结果分组(聚合函数)
--------------------
您可以使用列上的 ``GROUP BY`` 子句对结果进行分组，然后使用 ``AGGREGATE`` 函数，例如 COUNT，MAX，MIN 和 AVERAGE 。 您还可以在 ``group by`` 子句中的列上使用函数。请参见 ``SUM`` 示例，您将使用 ``YEAR()`` 函数。


COUNT
^^^^^

- 查找男性和女性雇员的个数：

.. code-block:: shell

    mysql> SELECT gender, COUNT(*) AS count FROM employees GROUP BY gender;
    +--------+--------+
    | gender | count |
    +--------+--------+
    | M      | 179973 |
    | F      | 120051 |
    +--------+--------+
    2 rows in set (0.14 sec)

- 您想要找到 10 个最常见的员工名字。 您可以使用 ``GROUP BY first_name`` 对所有名字进行分组，然后使用 ``COUNT(first_name)`` 查找组内的计数，最后使用 ``ORDER BY`` 计对数结果进行排序。将这些结果限制在前 10 名：

.. code-block:: shell

    mysql> SELECT first_name, COUNT(first_name) AS count FROM employees GROUP BY first_name ORDER BY count DESC LIMIT 10;

SUM
^^^
找出每年给员工的工资总和，并按工资对结果进行排序。 ``YEAR()`` 函数返回给定日期的 ``YEAR`` ：

.. code-block:: shell

    mysql> SELECT '2017-06-12', YEAR('2017-06-12');
    +------------+--------------------+
    | 2017-06-12 | YEAR('2017-06-12') |
    +------------+--------------------+
    | 2017-06-12 | 2017               |
    +------------+--------------------+
    1 row in set (0.00 sec)

    mysql> SELECT YEAR(from_date), SUM(salary) AS sum FROM salaries GROUP BY YEAR(from_date) ORDER BY sum DESC;


AVERAGE
^^^^^^^
找到平均薪资最高的 10 名员工：

.. code-block:: shell

mysql> SELECT emp_no, AVG(salary) AS avg FROM salaries GROUP BY emp_no ORDER BY avg DESC LIMIT 10;

DISTINCT
^^^^^^^^
您可以使用 ``DISTINCT`` 子句过滤表中的不同条目：

.. code-block:: shell

    mysql> SELECT DISTINCT title FROM titles;
    +--------------------+
    | title              |
    +--------------------+
    | Senior Engineer    |
    | Staff              |
    | Engineer           |
    | Senior Staff       |
    | Assistant Engineer |
    | Technique Leader   |
    | Manager            |
    +--------------------+
    7 rows in set (0.30 sec)

使用HAVING过滤
^^^^^^^^^^^^^^
您可以通过添加 ``HAVING`` 子句来过滤 ``GROUP BY`` 子句的结果。

.. code-block:: shell

    mysql> SELECT emp_no, AVG(salary) AS avg FROM salaries GROUP BY emp_no HAVING avg > 140000 ORDER BY avg DESC;
    +--------+-------------+
    | emp_no | avg         |
    +--------+-------------+
    | 109334 | 141835.3333 |
    | 205000 | 141064.6364 |
    +--------+-------------+
    2 rows in set (0.80 sec)

可以在  https://dev.mysql.com/doc/refman/8.0/en/group-by-functions.html 中查看其它更多的信息。

创建用户
--------
到目前为止，您只使用 ``root`` 用户连接到 ``MySQL`` 并执行语句。除了来自 ``localhost`` 的管理任务之外，访问 ``MySQL`` 时永远不应该使用 ``root`` 用户。 您应该创建用户，限制访问，限制资源使用等。要创建新用户，您应具有 ``CREATE USER`` 权限，将在下一节中讨论。 在初始设置期间，您可以使用 ``root`` 用户创建其他用户。

.. code-block:: shell

    mysql> CREATE USER IF NOT EXISTS
    'company_read_only'@'localhost'
    IDENTIFIED WITH mysql_native_password
    BY 'company_pass'
    WITH MAX_QUERIES_PER_HOUR 500
    MAX_UPDATES_PER_HOUR 100;

前面语句将会创建用户：

- Username：company_read_only
- access：localhost
- 你可以限制访问IP的范围。例如 ``10.148.%.%``
- password：company_pass
- using mysql_native_password 默认认证
- 你同样可以指定任何可插拔认证，如 sha256_password, LDAP 或者Kerberos
- maximum number of queries 用户一小时内可以执行500次查询
- maximum number of updates 用户一小时内可以执行100次更新

当客户端连接到MySQL服务器，它的底层进行两个阶段：

1. 访问控制——连接验证；
2. 访问控制——请求验证；

在连接验证期间，服务器通过用户名和连接它的主机名来标识连接。服务器为用户调用身份验证插件并验证密码。它还会检查用户是否被锁定。

在请求验证阶段，服务器检查用户是否对每个操作具有足够的权限。

在前面的语句中，您必须以明文形式提供密码，该密码可以记录在命令历史文件 ``$HOME/.mysql_history`` 中。为避免这种情况，您可以在本地服务器上计算哈希并直接指定哈希字符串。它的语法是相同的，除了 ``mysql_native_password BY 'company_pass'`` 更改为 ``mysql_native_password AS 'hashed_string'`` ：

.. code-block:: shell

    mysql> SELECT PASSWORD('company_pass');
    +-------------------------------------------+
    |PASSWORD('company_pass')                   |
    +-------------------------------------------+
    | *EBD9E3BFD1489CA1EB0D2B4F29F6665F321E8C18 |
    +-------------------------------------------+
    1 row in set, 1 warning (0.00 sec)

    mysql> CREATE USER IF NOT EXISTS
    'company_read_only'@'localhost'
    IDENTIFIED WITH mysql_native_password
    AS '*EBD9E3BFD1489CA1EB0D2B4F29F6665F321E8C18'
    WITH MAX_QUERIES_PER_HOUR 500
    MAX_UPDATES_PER_HOUR 100;

.. note:: 您可以通过授予权限直接创建用户。请参阅下一节有关如何授予权限的部分。 但是，MySQL将在下一版本中弃用此功能。关于创建用户请参考 https://dev.mysql.com/doc/refman/8.0/en/create-user.html

对用户授权和撤销
----------------
您可以限制用户访问特定数据库或表，也可以只限制特定操作，例如 ``SELECT`` ， ``INSERT`` 和 ``UPDATE`` 。要向其他用户授予权限，您应具有 ``GRANT`` 权限。

授权
^^^^

- 授予 ``company_read_only`` 用户只读( ``SELECT`` )权限：

.. code-block:: shell

    mysql> GRANT SELECT ON company.* TO 'company_read_only'@'localhost';
    Query OK, 0 rows affected (0.06 sec)

星号表示数据库内的所有表。

- 授予 ``company_insert_only`` 用户插入( ``INSERT`` )权限：

.. code-block:: shell

    mysql> GRANT INSERT ON company.* TO 'company_insert_only'@'localhost' IDENTIFIED BY 'xxxx';
    Query OK, 0 rows affected, 1 warning (0.05 sec)

- 授予 ``WRITE`` 权限给新 ``company_write`` 用户：

.. code-block:: shell

    mysql> GRANT INSERT, DELETE, UPDATE ON company.*
    TO 'company_write'@'%' IDENTIFIED WITH
    mysql_native_password AS
    '*EBD9E3BFD1489CA1EB0D2B4F29F6665F321E8C18';
    Query OK, 0 rows affected, 1 warning (0.04 sec)

- 限制为指定的表。将 ``employees_read_only`` 用户限制为仅从 ``employees`` 表中选择 ``SELECT`` ：

.. code-block:: shell

    mysql> GRANT SELECT ON employees.employees TO
    'employees_read_only'@'%' IDENTIFIED WITH
    mysql_native_password AS
    '*EBD9E3BFD1489CA1EB0D2B4F29F6665F321E8C18';
    Query OK, 0 rows affected, 1 warning (0.03 sec)

- 可以进一步限制指定的列。将 ``employees_ro`` 用户限制为 ``employees`` 表的 ``first_name`` 和 ``last_name`` 列：

.. code-block:: shell

    mysql> GRANT SELECT(first_name,last_name) ON
    employees.employees TO 'employees_ro'@'%'
    IDENTIFIED WITH mysql_native_password AS
    '*EBD9E3BFD1489CA1EB0D2B4F29F6665F321E8C18';
    Query OK, 0 rows affected, 1 warning (0.06 sec)

- 扩展授权。你可以通过执行新的授权来扩展授权。如，扩展 ``employees_col_ro`` 用户能够访问 ``salaries`` 表：

.. code-block:: shell

    mysql> GRANT SELECT(salary) ON employees.salaries TO 'employees_ro'@'%';
    Query OK, 0 rows affected (0.00 sec)

- 创建 ``SUPER`` 用户。你需要一个管理员账号来管理服务器。 ``ALL`` 表示除了 ``GRANT`` 权限的所有权限：

.. code-block:: shell

    mysql> CREATE USER 'dbadmin'@'%' IDENTIFIED WITH mysql_native_password BY 'DB@dm1n';
    Query OK, 0 rows affected (0.01 sec)

    mysql> GRANT ALL ON *.* TO 'dbadmin'@'%';
    Query OK, 0 rows affected (0.01 sec)

- 赋予 ``GRANT`` 权限。用户应具有 ``GRANT OPTION`` 权限，以便为其他用户授予权限。扩展 ``GRANT`` 权限给 ``dbadmin`` 超级用户：

.. code-block:: shell

    mysql> GRANT GRANT OPTION ON *.* TO 'dbadmin'@'%';
    Query OK, 0 rows affected (0.03 sec)

请参考 https://dev.mysql.com/doc/refman/8.0/en/grant.html 获取更多的权限类型。

检查授权
^^^^^^^^
你可以检查所有的用户授权。检查 ``employee_ro`` 用户的授权：

.. code-block:: shell

    mysql> SHOW GRANTS FOR 'employees_ro'@'%'\G

撤销授权
^^^^^^^^
撤销授权和授权具有同样的语法。你授予一个权限给用户然后从用户撤销一个权限。

- 从 'company_write'@'%' 用户撤销 DELETE 访问权限

.. code-block:: shell

    mysql> REVOKE DELETE ON company.* FROM 'company_write'@'%';
    Query OK, 0 rows affected (0.04 sec)

- 从 ``employee_ro`` 用户撤销访问 ``salary`` 列：

.. code-block:: shell

    mysql> REVOKE SELECT(salary) ON employees.salaries FROM 'employees_ro'@'%';
    Query OK, 0 rows affected (0.03 sec)

修改mysql.user表
^^^^^^^^^^^^^^^^
所有用户信息以及权限都存储在 ``mysql.user`` 表中。如果您有权访问 ``mysql.user`` 表，则可以直接修改 ``mysql.user`` 表以创建用户和授予权限。

如果使用帐户管理语句(如 ``GRANT`` ， ``REVOKE`` ， ``SET PASSWORD`` 或 ``RENAME USER`` )间接修改授权表，服务器会注意到这些更改并立即再次将授权表加载到内存中。

如果使用 ``INSERT`` ， ``UPDATE`` 或 ``DELETE`` 等语句直接修改授权表，则在重新启动服务器或指示重新装入表之前，更改对权限检查没有影响。如果直接更改授权表但忘记重新加载它们，则在重新启动服务器之前，更改无效。

可以通过发出 ``FLUSH PRIVILEGES`` 语句来重新加载 ``GRANT`` 表。

查询 ``mysql.user`` 表以查找 ``dbadmin`` 用户的所有条目：

.. code-block:: shell

    mysql> SELECT * FROM mysql.user WHERE user='dbadmin'\G

您可以看到 ``dbadmin`` 用户可以从任何主机( ``％`` )访问数据库。您可以通过更新 ``mysql.user`` 表并重新加载授权表来将它们限制为 ``localhost`` ：

.. code-block:: shell

    mysql> UPDATE mysql.user SET host='localhost' WHERE user='dbadmin';
    Query OK, 1 row affected (0.02 sec)
    Rows matched: 1 Changed: 1 Warnings: 0

    mysql> FLUSH PRIVILEGES;
    Query OK, 0 rows affected (0.00 sec)

设置用户密码期限
^^^^^^^^^^^^^^^^
您可以在特定时间间隔内使用户的密码到期; 在此之后，他们需要更改密码。当应用程序开发人员请求数据库访问时，您可以使用默认密码创建帐户，然后将其设置过期时间。 您可以与开发人员共享密码，然后他们必须更改密码才能继续使用 ``MySQL`` 。

创建的所有帐户的密码到期时间等于 ``default_password_lifetime`` 变量，默认情况下禁用该变量：

- 当创建一个带有过期密码的用户时。当开发者第一次登陆然后执行任何语句时，会抛出 1802 错误。在执行其它语句之前，必须使用 ``ALTER USER`` 语句重置密码：

.. code-block:: shell

    mysql> CREATE USER 'developer'@'%' IDENTIFIED
    WITH mysql_native_password AS
    '*EBD9E3BFD1489CA1EB0D2B4F29F6665F321E8C18'
    PASSWORD EXPIRE;
    Query OK, 0 rows affected (0.04 sec)

    shell> mysql -u developer -pcompany_pass
    mysql: [Warning] Using a password on the command
    line interface can be insecure.
    Welcome to the MySQL monitor. Commands end with
    ; or \g.
    Your MySQL connection id is 31
    Server version: 8.0.3-rc-log
    Copyright (c) 2000, 2017, Oracle and/or its
    affiliates. All rights reserved.
    Oracle is a registered trademark of Oracle
    Corporation and/or its
    affiliates. Other names may be trademarks of
    their respective
    owners.
    Type 'help;' or '\h' for help. Type '\c' to
    clear the current input statement.
    mysql> SHOW DATABASES;
    ERROR 1820 (HY000): You must reset your password
    using ALTER USER statement before executing this
    statement.

开发者必须使用如下命令来修改密码：

.. code-block:: shell

    mysql> ALTER USER 'developer'@'%' IDENTIFIED WITH
    mysql_native_password BY 'new_company_pass';
    Query OK, 0 rows affected (0.03 sec)

- 手动地过期存在的用户：

.. code-block:: shell

    mysql> ALTER USER 'developer'@'%' PASSWORD EXPIRE;
    Query OK, 0 rows affected (0.06 sec)

- 需要每隔180天修改密码：

.. code-block:: shell

    mysql> ALTER USER 'developer'@'%' PASSWORD EXPIRE INTERVAL 90 DAY;
    Query OK, 0 rows affected (0.04 sec)

锁定用户
^^^^^^^^
如果您发现该帐户存在任何问题，可以将其锁定。 MySQL 在使用 ``CREATE USER`` 或 ``ALTER USER`` 时支持锁定。

通过将 ``ACCOUNT LOCK`` 子句添加到 ``ALTER USER`` 语句来锁定帐户：

.. code-block:: shell

    mysql> ALTER USER 'developer'@'%' ACCOUNT LOCK;
    Query OK, 0 rows affected (0.05 sec)

开发人员将收到错误消息，指出该帐户已被锁定：

.. code-block:: shell

    shell> mysql -u developer -pnew_company_pass
    mysql: [Warning] Using a password on the command line
    interface can be insecure.
    ERROR 3118 (HY000): Access denied for user
    'developer'@'localhost'. Account is locked.

确认后，您可以解锁帐户：

.. code-block:: shell

    mysql> ALTER USER 'developer'@'%' ACCOUNT UNLOCK;
    Query OK, 0 rows affected (0.00 sec)

为用户创建角色
^^^^^^^^^^^^^^
MySQL 角色是一组命名的权限集合。与用户帐户一样，角色可以拥有授予和撤消的权限。可以为用户帐户授予角色，从而向角色授予角色权限。之前，您为读取，写入和管理创建了单独的用户。 对于写入权限，您已向用户授予 ``INSERT`` ， ``DELETE`` 和 ``UPDATE`` 。相反，您可以将这些权限授予角色，然后将该角色分配给用户。通过这种方式，您可以避免单独授予许多权限给很多用户帐户。

- 创建角色：

.. code-block:: shell

    mysql> CREATE ROLE 'app_read_only', 'app_writes', 'app_developer';
    Query OK, 0 rows affected (0.01 sec)

- 使用 GRANT 语句赋予权限给角色：

.. code-block:: shell

    mysql> GRANT SELECT ON employees.* TO 'app_read_only';
    Query OK, 0 rows affected (0.00 sec)

    mysql> GRANT INSERT, UPDATE, DELETE ON employees.* TO 'app_writes';
    Query OK, 0 rows affected (0.00 sec)

    mysql> GRANT ALL ON employees.* TO 'app_developer';
    Query OK, 0 rows affected (0.04 sec)

- 创建用户。如果你没有指定任何主机， ``%`` 将会采用：

.. code-block:: shell

    mysql> CREATE user emp_read_only IDENTIFIED BY 'emp_pass';
    Query OK, 0 rows affected (0.06 sec)

    mysql> CREATE user emp_writes IDENTIFIED BY 'emp_pass';
    Query OK, 0 rows affected (0.04 sec)

    mysql> CREATE user emp_developer IDENTIFIED BY 'emp_pass';
    Query OK, 0 rows affected (0.01 sec)

    mysql> CREATE user emp_read_write IDENTIFIED BY 'emp_pass';
    Query OK, 0 rows affected (0.00 sec)

- 使用 ``GRANT`` 语句将角色分配给用户。您可以为用户分配多个角色。例如，您可以为 ``emp_read_write`` 用户分配读写访问权限：

.. code-block:: shell

    mysql> GRANT 'app_read_only' TO 'emp_read_only'@'%';
    Query OK, 0 rows affected (0.04 sec)

    mysql> GRANT 'app_writes' TO 'emp_writes'@'%';
    Query OK, 0 rows affected (0.00 sec)

    mysql> GRANT 'app_developer' TO 'emp_developer'@'%';
    Query OK, 0 rows affected (0.00 sec)

    mysql> GRANT 'app_read_only', 'app_writes' TO 'emp_read_write'@'%';
    Query OK, 0 rows affected (0.05 sec)

作为安全措施，请避免使用 ``％`` 并限制对部署应用程序的 ``IP`` 的访问。


将选择数据存储到文件和表中
--------------------------
您可以使用 ``SELECT INTO OUTFILE`` 语句将输出保存到文件中。您可以指定列和行分隔符，稍后您可以将数据导入其他数据平台。


保存到文件中
^^^^^^^^^^^^

- 要将输出保存到文件中，您需要 ``FILE`` 权限。 ``FILE`` 是一个全局特权，这意味着您不能为特定数据库限制它。但是，您可以限制用户选择的内容：

.. code-block:: shell

    mysql> GRANT SELECT ON employees.* TO 'user_ro_file'@'%' IDENTIFIED WITH
    mysql_native_password AS '*EBD9E3BFD1489CA1EB0D2B4F29F6665F321E8C18';
    Query OK, 0 rows affected, 1 warning (0.00 sec)
    mysql> GRANT FILE ON *.* TO 'user_ro_file'@'%' IDENTIFIED WITH mysql_native_password AS '*EBD9E3BFD1489CA1EB0D2B4F29F6665F321E8C18';
    Query OK, 0 rows affected, 1 warning (0.00 sec)

- 在 ``Ubuntu`` 上，默认 ``MySQL`` 将不允许你写入到文件。你应该在配置文件中设置 secure_file_priv 然后重启。在CentOS，Red Hat中， ``secure_file_priv`` 设置为 ``/var/lib/mysql-files`` 。这表示所有文件将会存储在 ``mysql-files`` 目录中。

- 现在，打开配置文件。增加 ``secure_file_priv = /var/lib/mysql`` ：

.. code-block:: shell

    shell> sudo vi
    /etc/mysql/mysql.conf.d/mysqld.cnf

- 重新启动MySQL服务器：

.. code-block:: shell

    shell> sudo systemctl restart mysql

以下语句将输出保存为 ``CSV`` 格式：

.. code-block:: shell

    mysql> SELECT first_name, last_name INTO OUTFILE 'result.csv'
    FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED
    BY '"'
    LINES TERMINATED BY '\n'
    FROM employees WHERE hire_date<'1986-01-01'
    LIMIT 10;
    Query OK, 10 rows affected (0.00 sec)

您可以检查文件的输出，该输出将在指定的路径中创建 ``{secure_file_priv}/{database_name}`` ，在这种情况下是 ``/var/lib/mysql/employees/`` 。 如果文件已存在，则语句将失败，因此每次执行文件或将文件移动到其他位置时都需要提供唯一的名称：

.. code-block:: shell

    shell> sudo cat /var/lib/mysql/employees/result.csv
    "Bezalel","Simmel"
    "Sumant","Peac"
    "Eberhardt","Terkki"
    "Otmar","Herbst"
    "Florian","Syrotiuk"
    "Tse","Herber"
    "Udi","Jansch"
    "Reuven","Garigliano"
    "Erez","Ritzmann"
    "Premal","Baek"

保存到表中
^^^^^^^^^^
您可以将 ``SELECT`` 语句的结果保存到表中。即使表不存在，也可以使用 ``CREATE`` 和 ``SELECT`` 创建表并加载数据。如果表已存在，则可以使用 ``INSERT`` 和 ``SELECT`` 加载数据。

您可以将标题保存到新的 ``titles_only`` 表中：

.. code-block:: shell

    mysql> CREATE TABLE titles_only AS SELECT DISTINCT title FROM titles;
    Query OK, 7 rows affected (0.50 sec)
    Records: 7 Duplicates: 0 Warnings: 0

如果表已存在，则可以使用 ``INSERT INTO SELECT`` 语句：

.. code-block:: shell

    mysql> INSERT INTO titles_only SELECT DISTINCT title FROM titles;
    Query OK, 7 rows affected (0.46 sec)
    Records: 7 Duplicates: 0 Warnings: 0

为了避免重复，可以使用 ``INSERT IGNORE`` 。但是，在这种情况下， ``titles_only`` 表上没有 ``PRIMARY KEY`` 。 所以 ``IGNORE`` 条款没有任何区别。

导入数据到表中
--------------
您可以将表数据转储到文件中，反之亦然，即将文件中的数据加载到表中。 这广泛用于加载批量数据，是将数据加载到表中的超快速方法。 您可以指定列分隔符以将数据加载到相应的列中。您应该具有表的 ``FILE`` 权限和 ``INSERT`` 权限。

之前，您已将 ``first_name`` 和 ``last_name`` 保存到文件中。您可以使用同一文件将数据加载到另一个表中。在加载之前，您应该先创建表。如果表已存在，则可以直接加载。 **表的列应与文件的字段匹配。**

创建一个保存数据的表：

.. code-block:: shell

    mysql> CREATE TABLE employee_names (
    `first_name` varchar(14) NOT NULL,
    `last_name` varchar(16) NOT NULL
    ) ENGINE=InnoDB;
    Query OK, 0 rows affected (0.07 sec)

确保该文件存在：

.. code-block:: shell

    shell> sudo ls -lhtr
    /var/lib/mysql/employees/result.csv
    -rw-rw-rw- 1 mysql mysql 180 Jun 10 14:53
    /var/lib/mysql/employees/result.csv

使用 ``LOAD DATA INFILE`` 语句加载数据：

.. code-block:: shell

    mysql> LOAD DATA INFILE 'result.csv' INTO TABLE
    employee_names
    FIELDS TERMINATED BY ','
    OPTIONALLY ENCLOSED BY '"'
    LINES TERMINATED BY '\n';
    Query OK, 10 rows affected (0.01 sec)
    Records: 10 Deleted: 0 Skipped: 0 Warnings: 0

该文件可以作为完整路径名提供，以指定其确切位置。如果以相对路径名的形式给出，则相对于启动客户端程序的目录解释名称。

- 如果文件包含您要忽略的任何标头，请指定 ``IGNORE n LINES`` ：

.. code-block:: shell

    mysql> LOAD DATA INFILE 'result.csv' INTO TABLE
    employee_names
    FIELDS TERMINATED BY ','
    OPTIONALLY ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 LINES;

- 您可以指定 ``REPLACE`` 或 ``IGNORE`` 来处理重复项：

.. code-block:: shell

    mysql> LOAD DATA INFILE 'result.csv' REPLACE
    INTO TABLE employee_names FIELDS TERMINATED BY
    ','OPTIONALLY ENCLOSED BY '"' LINES TERMINATED
    BY '\n';
    Query OK, 10 rows affected (0.01 sec)
    Records: 10 Deleted: 0 Skipped: 0 Warnings: 0
    mysql> LOAD DATA INFILE 'result.csv' IGNORE INTO
    TABLE employee_names FIELDS TERMINATED BY
    ','OPTIONALLY ENCLOSED BY '"' LINES TERMINATED
    BY '\n';
    Query OK, 10 rows affected (0.06 sec)
    Records: 10 Deleted: 0 Skipped: 0 Warnings: 0

- MySQL 假定您要加载的文件在服务器上可用。如果从远程客户端计算机连接到服务器，则可以指定 ``LOCAL`` 以加载位于客户端上的文件。本地文件将从客户端复制到服务器。该文件保存在服务器的标准临时位置。 在 Linux 机器中，它是 ``/tmp`` ：

.. code-block:: shell

    mysql> LOAD DATA LOCAL INFILE 'result.csv'
    IGNORE INTO TABLE employee_names FIELDS
    TERMINATED BY ','OPTIONALLY ENCLOSED BY '"'
    LINES TERMINATED BY '\n';

连接表
------
到目前为止，您已经了解了从单个表中插入和检索数据。在本节中，我们将讨论如何连接两个或多个表来检索结果。

一个完美的例子是您想要使用员工姓名和部门编号 ``emp_no:110022`` 来查找员工：

- 部门号和名称存储在 ``departments`` 表中；
- 员工号和其它详情 如 ``first_name`` 和 ``last_name`` 存储在 ``employees`` 表中；
- 员工和部门的映射存储在 ``dept_manager`` 表中；

如果不使用 ``JOIN`` ，你可以这样做：

1. 使用 ``emp_no=110022`` 从 ``employee`` 表中查找雇员名称：

.. code-block:: shell

    mysql> SELECT emp.emp_no, emp.first_name, emp.last_name
    FROM employees AS emp
    WHERE emp.emp_no=110022;
    +--------+------------+------------+
    | emp_no | first_name | last_name  |
    +--------+------------+------------+
    | 110022 | Margareta  | Markovitch |
    +--------+------------+------------+
    1 row in set (0.00 sec)

2. 从 ``departments`` 表中查找部门号；

.. code-block:: shell

    mysql> SELECT dept_no FROM dept_manager AS dept_mgr WHERE dept_mgr.emp_no=110022;
    +---------+
    | dept_no |
    +---------+
    | d001    |
    +---------+
    1 row in set (0.00 sec)

3. 从 ``departments`` 表中查找部门名称；

.. code-block:: shell

    mysql> SELECT dept_name FROM departments dept WHERE dept.dept_no='d001';
    +-----------+
    | dept_name |
    +-----------+
    | Marketing |
    +-----------+
    1 row in set (0.00 sec)

为避免使用三个语句在三个不同的表上查找，您可以使用 ``JOIN`` 来连接它们。 这里要注意的重要一点是要连接两个表，您应该有一个或多个公共列来连接。 您可以根据 ``emp_no`` 连接 ``employees`` 和 ``dept_manager`` ，它们都有 ``emp_no`` 列。 虽然名称不需要匹配，但您应该找出可以连接的列。同样， ``dept_mgr`` 和部门将 ``dept_no`` 作为公共列。

与列别名一样，您可以为表提供别名，并使用别名引用该表的列。例如，您可以使用 ``FROM employees AS emp`` 为员工提供别名，并使用点表示法引用 ``employees`` 表的列，例如 ``emp.emp_no`` ：

.. code-block:: shell

    mysql> SELECT
        emp.emp_no,
        emp.first_name,
        emp.last_name,
        dept.dept_name
    FROM
        employees AS emp
    JOIN dept_manager AS dept_mgr
        ON emp.emp_no=dept_mgr.emp_no AND
    emp.emp_no=110022
    JOIN departments AS dept
        ON dept_mgr.dept_no=dept.dept_no;
    +--------+------------+------------+-----------+
    | emp_no | first_name | last_name  | dept_name |
    +--------+------------+------------+-----------+
    | 110022 | Margareta  | Markovitch | Marketing |
    +--------+------------+------------+-----------+
    1 row in set (0.00 sec)

让我们看另一个例子 - 你想找出每个部门的平均工资。为此，您可以使用 ``AVG`` 函数和 ``dept_no`` 的分组。要查找部门名称，您可以将结果与 ``departments`` 表上的 ``dept_no`` 一起连接：

.. code-block:: shell

    mysql> SELECT
        dept_name,
        AVG(salary) AS avg_salary
    FROM
        salaries
    JOIN dept_emp
        ON salaries.emp_no=dept_emp.emp_no
    JOIN departments
        ON dept_emp.dept_no=departments.dept_no
    GROUP BY
        dept_emp.dept_no
    ORDER BY
        avg_salary
    DESC;

    +--------------------+------------+
    | dept_name          | avg_salary |
    +--------------------+------------+
    | Sales              | 80667.6058 |
    | Marketing          | 71913.2000 |
    | Finance            | 70489.3649 |
    | Research           | 59665.1817 |
    | Production         | 59605.4825 |
    | Development        | 59478.9012 |
    | Customer Service   | 58770.3665 |
    | Quality Management | 57251.2719 |
    | Human Resources    | 55574.8794 |
    +--------------------+------------+
    9 rows in set (8.29 sec)

使用自我连接来识别重复行
^^^^^^^^^^^^^^^^^^^^^^^^
您希望在表中找到特定列的重复行。例如，您想要找出哪些员工具有相同的 ``first_name`` ，相同的 ``last_name`` ，相同的 ``gender`` 以及相同的 ``hire_date`` 。在这种情况下，您可以将 ``employees`` 表连接到自身，同时指定要在 ``JOIN`` 子句中查找重复项的列。您需要为每个表使用不同的别名。

您需要在要连接的列上添加索引。你可以执行如下命令来添加索引：

.. code-block:: shell

    mysql> ALTER TABLE employees ADD INDEX name(first_name, last_name);
    Query OK, 0 rows affected (1.95 sec)
    Records: 0 Duplicates: 0 Warnings: 0

    mysql> SELECT
        emp1.*
    FROM
        employees emp1
    JOIN employees emp2
        ON emp1.first_name=emp2.first_name
        AND emp1.last_name=emp2.last_name
        AND emp1.gender=emp2.gender
        AND emp1.hire_date=emp2.hire_date
        AND emp1.emp_no!=emp2.emp_no
    ORDER BY
        first_name, last_name;

你必须使用 ``emp1.emp_no != emp2.emp_no`` ，因为员工将有不同的 ``emp_no`` 。 否则，将出现相同的员工。

使用子查询
^^^^^^^^^^
子查询是另一个语句中的 ``SELECT`` 语句。假设你想找到从 ``1986-06-26`` 开始以 ``Senior Engineer`` 开头的员工的姓名。

你可以从 ``titles`` 表中获取 ``emp_no`` ，并从 ``employees`` 表中获取名称。 您还可以使用 ``JOIN`` 查找结果。

为了从 ``titles`` 表中获取 ``emp_no`` :

.. code-block:: shell

    mysql> SELECT emp_no FROM titles WHERE title="Senior
    Engineer" AND from_date="1986-06-26";
    +--------+
    | emp_no |
    +--------+
    | 10001  |
    | 84305  |
    | 228917 |
    | 426700 |
    | 458304 |
    +--------+
    5 rows in set (0.14 sec)

为了查找名称：

.. code-block:: shell

    mysql> SELECT first_name, last_name FROM employees
    WHERE emp_no IN (< output from preceding query>)

    mysql> SELECT first_name, last_name FROM employees
    WHERE emp_no IN (10001,84305,228917,426700,458304);

    +------------+-----------+
    | first_name | last_name |
    +------------+-----------+
    | Georgi     | Facello   |
    | Minghong   | Kalloufi  |
    | Nechama    | Bennet    |
    | Nagui      | Restivo   |
    | Shuzo      | Kirkerud  |
    +------------+-----------+
    5 rows in set (0.00 sec

MySQL 也支持其它子句，如 ``EXISTS`` 和 ``EQUAL`` 。请参阅参考手册 https://dev.mysql.com/doc/refman/8.0/en/subqueries.html

.. code-block:: shell

mysql> SELECT
    first_name,
    last_name
FROM
    employees
WHERE
    emp_no
IN (SELECT emp_no FROM titles WHERE title="Senior Engineer" AND from_date="1986-06-26");

+------------+-----------+
| first_name | last_name |
+------------+-----------+
| Georgi     | Facello   |
| Minghong   | Kalloufi  |
| Nagui      | Restivo   |
| Nechama    | Bennet    |
| Shuzo      | Kirkerud  |
+------------+-----------+
5 rows in set (0.91 sec)

找到赚取最高薪水的员工：

.. code-block:: shell

    mysql> SELECT emp_no FROM salaries WHERE salary= (SELECT MAX(salary) FROM salaries);
    +--------+
    | emp_no |
    +--------+
    | 43624  |
    +--------+
    1 row in set (1.54 sec)

在表间查找不匹配的行
^^^^^^^^^^^^^^^^^^^^
假设您要在表中查找不在其他表中的行。您可以通过两种方式实现这一目标。使用 ``NOT IN`` 子句或使用 ``OUTER JOIN`` 。

要查找匹配的行，可以使用普通 ``JOIN`` ，如果要查找不匹配的行，可以使用 ``OUTER JOIN`` 。 正常 ``JOIN`` 表示 ``A`` 与 ``B`` 的交集。 ``OUTER JOIN`` 给出 ``A`` 和 ``B`` 的匹配记录，并且还提供 ``A`` 的连接字段为 ``NULL`` 的所有记录。如果需要 ``A-B`` 的输出，可以使用 ``WHERE <JOIN COLUMN IN B> IS NULL`` 子句。

为了理解 ``OUTER JOIN`` 的使用，创建两个 ``employee`` 表然后插入相同的值：

.. code-block:: shell

    mysql> CREATE TABLE employees_list1 AS SELECT * FROM
    employees WHERE first_name LIKE 'aa%';
    Query OK, 444 rows affected (0.22 sec)
    Records: 444 Duplicates: 0 Warnings: 0

    mysql> CREATE TABLE employees_list2 AS SELECT * FROM
    employees WHERE emp_no BETWEEN 400000 AND 500000 AND
    gender='F';
    Query OK, 39892 rows affected (0.59 sec)
    Records: 39892 Duplicates: 0 Warnings: 0

您已经知道如何查找两个列表中都存在的员工：

.. code-block:: shell

    mysql> SELECT * FROM employees_list1 WHERE emp_no IN (SELECT emp_no FROM employees_list2);

或者，你可以使用 ``JOIN`` ：

.. code-block:: shell

    mysql> SELECT l1.* FROM employees_list1 l1 JOIN employees_list2 l2 ON l1.emp_no=l2.emp_no;

为了查找存在于 ``employees_list1`` 而不存在于 ``employees_list2`` 的雇员：

.. code-block:: shell

    mysql> SELECT * FROM employees_list1 WHERE emp_no NOT
    IN (SELECT emp_no FROM employees_list2);

或者，你可以使用 OUTER JOIN :

.. code-block:: shell

    mysql> SELECT l1.* FROM employees_list1 l1 LEFT OUTER
    JOIN employees_list2 l2 ON l1.emp_no=l2.emp_no WHERE
    l2.emp_no IS NULL;

外连接为每个不匹配的行在连接列表中创建第二个表的 ``NULL`` 列。 如果使用 ``RIGHT JOIN`` ，则第一个表中所有不匹配的行设置 ``NULL`` 值。

您还可以使用 ``OUTER JOIN`` 查找匹配的行。而不是 ``WHERE l2.emp_no IS NULL`` ，使用 ``WHERE emp_no IS NOT NULL`` ：

.. code-block:: shell

    mysql> SELECT l1.* FROM employees_list1 l1 LEFT OUTER
    JOIN employees_list2 l2 ON l1.emp_no=l2.emp_no WHERE
    l2.emp_no IS NOT NULL;

存储过程
--------
假设您需要在 ``MySQL`` 中执行一系列语句，而不是每次都发送所有 ``SQL`` 语句，您可以将所有语句封装在一个程序中并在需要时调用它。存储过程是一组 ``SQL`` 语句，不需要返回值。

除了 ``SQL`` 语句之外，您还可以使用变量来存储结果，并在存储过程中执行编程工作。 例如，您可以编写 ``IF`` ， ``CASE`` 子句，逻辑运算和 ``WHILE`` 循环。

- 存储的函数和过程也称为存储例程。
- 要创建存储过程，您应该具有 ``CREATE ROUTINE`` 权限。
- 存储的函数具有返回值。
- 存储过程没有返回值。
- 所有代码都写在 ``BEGIN`` 和 ``END`` 块中。
- 可以在 ``SELECT`` 语句中直接调用存储函数。
- 可以使用 ``CALL`` 语句调用存储过程。
- 由于存储例程中的语句应以分隔符( ``;`` )结尾，因此您必须更改 ``MySQL`` 的分隔符，以便 MySQL 不会使用正常语句解释存储例程中的 SQL 语句。创建过程后，您可以将分隔符更改回默认值。


函数
----
例如，您想要添加新员工。您应该更新三个表，即 ``employees`` ， ``salaries`` 和 ``titles`` (职称)。您可以开发存储过程并调用它来创建新 ``employee`` ，而不是执行三个语句。

您必须传递员工的 ``first_name`` ， ``last_name`` ， ``gender`` 和 ``birth_date`` ，以及员工加入的部门。您可以使用这些作为输入变量，您应该将员工编号作为输出。 **存储过程不返回值，但它可以更新变量，您可以使用它。**

下面是一个简单的存储过程用来创建一个新的雇员然后更新 ``salary`` 和 ``department`` 表：

.. code-block:: shell

    /* 在创建之前，如果任何具有相同名称的过程，则删除现有过程 */
    DROP PROCEDURE IF EXISTS create_employee;

    /* 改变分隔符为 $$ */
    DELIMITER $$

    /* IN指定作为参数的变量，INOUT指定输出变量 */
    CREATE PROCEDURE create_employee (OUT new_emp_no INT,
    IN first_name varchar(20), IN last_name varchar(20),
    IN gender enum('M','F'), IN birth_date date, IN
    emp_dept_name varchar(40), IN title varchar(50))
    BEGIN

    /* 为emp_dept_no和salary声明变量 */
    DECLARE emp_dept_no char(4);
    DECLARE salary int DEFAULT 60000;

    /* 在变量new_emp_no中选择最大员工编号 */
    SELECT max(emp_no) INTO new_emp_no FROM employees;

    /* 增加new_emp_no */
    SET new_emp_no = new_emp_no + 1;

    /* 插入数据到employees表中 */
    /* The function CURDATE() gives the current date) */
    INSERT INTO employees VALUES(new_emp_no,
    birth_date, first_name, last_name, gender,
    CURDATE());

    /* 根据部门名称查找部门编号 */
    SELECT emp_dept_name;
    SELECT dept_no INTO emp_dept_no FROM departments
    WHERE dept_name=emp_dept_name;
    SELECT emp_dept_no;

    /* 插入到部门和员工中间表中 */
    INSERT INTO dept_emp VALUES(new_emp_no, emp_dept_no, CURDATE(), '9999-01-01');

    /* 插入到职称表中 */
    INSERT INTO titles VALUES(new_emp_no, title, CURDATE(), '9999-01-01');

    /* 基于职称查找奖金 */
    IF title = 'Staff'
    THEN SET salary = 100000;
    ELSEIF title = 'Senior Staff'
    THEN SET salary = 120000;
    END IF;

    /* 插入到奖金表中 */
    INSERT INTO salaries VALUES(new_emp_no, salary,
    CURDATE(), '9999-01-01');
    END
    $$

    /* 恢复分隔符为分号 */
    DELIMITER ;

为了创建一个存储过程，你可以：

- 粘贴它到命令行工具；
- 保存到文件然后在命令行中使用 ``mysql -u <user> -p employees < stored_procedure.sql`` 导入；

- 如果在mysql命令行中，则通过 ``mysql> SOURCE stored_procedure.sql`` 加载文件；

为了能够使用存储过程，赋予执行权限给 ``emp_read_only`` 用户：

.. code-block:: shell

    mysql> GRANT EXECUTE ON employees.* TO 'emp_read_only'@'%';
    Query OK, 0 rows affected (0.05 sec)

使用 ``CALL stored_procedure(OUT variable，IN values)`` 语句和例程名称调用存储过程。

使用 ``emp_read_only`` 账户连接 ``MySQL`` ：

.. code-block:: shell

    shell> mysql -u emp_read_only -pemp_pass employees -A

将变量传递到要存储 ``@new_emp_no`` 输出的位置，并传递所需的输入值：

.. code-block:: shell

    mysql> CALL create_employee(@new_emp_no, 'John',
    'Smith', 'M', '1984-06-19', 'Research', 'Staff');
    Query OK, 1 row affected (0.01 sec)

选择 ``emp_no`` 的值，该值存储在 ``@new_emp_no`` 变量中：

.. code-block:: shell

    mysql> SELECT @new_emp_no;
    +-------------+
    | @new_emp_no |
    +-------------+
    | 500000      |
    +-------------+
    1 row in set (0.00 sec)

检查是否在 ``employees`` ， ``salaries`` 和 ``titles`` 表中创建了行：

.. code-block:: shell

    mysql> SELECT * FROM employees WHERE emp_no=500000;
    mysql> SELECT * FROM salaries WHERE emp_no=500000;
    mysql> SELECT * FROM titles WHERE emp_no=500000;

您可以看到，即使 ``emp_read_only`` 对表没有写访问权限，它也可以通过调用存储过程来编写。如果 ``SQL SECURITY`` 存储
过程创建为 ``INVOKER`` ， ``emp_read_only`` 无法修改数据。 请注意，如果使用 ``localhost`` 进行连接，请为 ``localhost`` 用户创建权限。

要列出数据库中的所有存储过程，请执行 ``SHOW PROCEDURE STATUS\G`` 。 要检查现有存储例程的定义，可以执行 ``SHOW CREATE PROCEDURE <procedure_name>\G`` 。

存储过程也用于增强安全性。用户需要存储过程的 ``EXECUTE`` 特权才能执行它。

通过存储例程的定义：

- ``DEFINER`` 子句指定存储例程的创建者。如果未指定任何内容，则采用当前用户。
- ``SQL SECURITY`` 子句指定存储例程的执行上下文。 它可以是 ``DEFINER`` 或 ``INVOKER`` 。

DEFINER：即使只有例程的 ``EXECUTE`` 权限，用户也可以调用并获取存储例程的输出，无论该用户是否具有基础表的权限。如果 ``DEFINER`` 有权限就足够了。
INVOKER：安全上下文切换到调用存储例程的用户。在这种情况下，调用者应该可以访问基础表。

更多的例子，请参考 https://dev.mysql.com/doc/refman/8.0/en/create-procedure.html

函数
----
就像存储过程一样，您可以创建存储的函数。主要区别在于函数应该具有返回值，并且可以在 ``SELECT`` 中调用它们。通常，创建存储函数以简化复杂计算。

这是一个如何编写函数以及如何调用它的示例。假设银行想要根据收入水平给出信用卡，而不是暴露实际工资，你可以公开这个函数来找出收入水平：

.. code-block:: shell

    shell> vi function.sql;
    DROP FUNCTION IF EXISTS get_sal_level;
    DELIMITER $$
    CREATE FUNCTION get_sal_level(emp int) RETURNS
    VARCHAR(10)
        DETERMINISTIC
    BEGIN
        DECLARE sal_level varchar(10);
        DECLARE avg_sal FLOAT;

        SELECT AVG(salary) INTO avg_sal FROM salaries WHERE emp_no=emp;

        IF avg_sal < 50000 THEN
        SET sal_level = 'BRONZE';
        ELSEIF (avg_sal >= 50000 AND avg_sal < 70000) THEN
        SET sal_level = 'SILVER';
        ELSEIF (avg_sal >= 70000 AND avg_sal < 90000) THEN
        SET sal_level = 'GOLD';
        ELSEIF (avg_sal >= 90000) THEN
        SET sal_level = 'PLATINUM';
        ELSE
        SET sal_level = 'NOT FOUND';
        END IF;
        RETURN (sal_level);
    END
    $$
    DELIMITER ;

为了创建函数：

.. code-block:: shell

    mysql> SOURCE function.sql;

    mysql> SELECT get_sal_level(10002);
    +----------------------+
    | get_sal_level(10002) |
    +----------------------+
    | SILVER |
    +----------------------+
    1 row in set (0.00 sec)
    mysql> SELECT get_sal_level(10001);
    +----------------------+
    | get_sal_level(10001) |
    +----------------------+
    | GOLD |
    +----------------------+
    1 row in set (0.00 sec)
    mysql> SELECT get_sal_level(1);
    +------------------+
    | get_sal_level(1) |
    +------------------+
    | NOT FOUND |
    +------------------+
    1 row in set (0.00 sec)

要列出数据库中的所有存储函数，请执行 ``SHOW FUNCTION STATUS\G`` 。要检查现有存储函数的定义，可以执行 ``SHOW CREATE FUNCTION <function_name>\G`` 。

.. note:: 在函数创建中提供 ``DETERMINISTIC`` 关键字非常重要。如果例程对于相同的输入参数总是产生相同的结果，则该例程被认为是 ``DETERMINISTIC`` ，否则不是 ``DETERMINISTIC`` 。 如果在例程定义中既未给出 ``DETERMINISTIC`` 也未给出 ``DETERMINISTIC`` ，则默认值为 ``NOT DETERMINISTIC`` 。 要声明函数是确定性的，必须明确指定 ``DETERMINISTIC`` 。将 ``NON DETERMINISTIC`` 例程声明为 ``DETERMINISTIC`` 可能会导致意外结果，导致优化程序选择错误的执行计划。 将 ``DETERMINISTIC`` 例程声明为 NON ``DETERMINISTIC`` 可能会降低性能，导致不使用可用的优化。


内置函数
^^^^^^^^
MySQL 提供了许多内置函数。 您已经使用 ``CURDATE()`` 函数来获取当前日期。

您可以在 ``WHERE`` 子句中使用函数：

.. code-block:: shell

    mysql> SELECT * FROM employees WHERE hire_date = CURDATE();

- 例如，以下函数给出了一周前的日期：

.. code-block:: shell

    mysql> SELECT DATE_ADD(CURDATE(), INTERVAL -7 DAY) AS '7 Days Ago';

- 连接两个字符串：

.. code-block:: shell

    mysql> SELECT CONCAT(first_name, ' ', last_name) FROM employees LIMIT 1;
    +------------------------------------+
    | CONCAT(first_name, ' ', last_name) |
    +------------------------------------+
    | Aamer Anger                        |
    +------------------------------------+
    1 row in set (0.00 sec)

更多的内置函数，请参考 https://dev.mysql.com/doc/refman/8.0/en/func-op-summary-ref.html

触发器
------
触发器用于在触发事件之前或之后激活某些东西。例如，您可以在插入表中的每一行之前或更新的每一行之后激活触发器。

在不停机的情况下更改表格时，触发器非常有用（请参阅第10章，表格维护，在 ``Alter`` 表中使用在线 ``schema`` 更改工具）以及审计目的。假设您想要找出行的先前值，您可以编写一个触发器，在更新之前将这些行保存在另一个表中。另一个表用作具有先前记录的审计表。

触发动作时间可以是 ``BEFORE`` 或 ``AFTER`` ，它指示触发器是在要修改的每一行之前还是之后激活。

触发事件可以是 ``INSERT`` , ``DELETE`` , 或者 ``UPDATE`` ：

- ``INSERT`` ：当一个新行通过 ``INSERT`` ， ``REPLACE`` 或者 ``LOAD DATA`` 插入时，该触发器被激活；
- ``UPDATE`` ：当通过 ``UPDATE`` 语句更新时；
- ``DELETE`` ：当通过 ``DELETE`` 或者 ``REPLACE`` 语句时；

从 MySQL 5.7 开始，表可以同时具有多个触发器。例如，一个表可以有两个 ``BEFORE INSERT`` 触发器。 您必须使用 ``FOLLOWS`` 或 ``PRECEDES`` 指定首先应该使用哪个触发器。

例如，您希望在将薪水插入工资(salaries)表之前将工资四舍五入。 ``NEW`` 指的是要插入的新值：

.. code-block:: shell

    shell> vi before_insert_trigger.sql
    DROP TRIGGER IF EXISTS salary_round;
    DELIMITER $$
    CREATE TRIGGER salary_round BEFORE INSERT ON salaries
    FOR EACH ROW
    BEGIN
        SET NEW.salary=ROUND(NEW.salary);
    END
    $$
    DELIMITER ;

导入触发器源文件：

.. code-block:: shell

    mysql> SOURCE before_insert_trigger.sql;

通过在薪水中插入浮动数来测试触发器：

.. code-block:: shell

    mysql> INSERT INTO salaries VALUES(10002, 100000.79, CURDATE(), '9999-01-01');

你可以看到工资四舍五入：

.. code-block:: shell

    mysql> SELECT * FROM salaries WHERE emp_no=10002 AND
    from_date=CURDATE();
    +--------+--------+------------+------------+
    | emp_no | salary | from_date  | to_date    |
    +--------+--------+------------+------------+
    | 10002  | 100001 | 2017-06-18 | 9999-01-01 |
    +--------+--------+------------+------------+
    1 row in set (0.00 sec)

同样，您可以创建一个 ``BEFORE UPDATE`` 触发器来舍入薪水。另一个例子：您想要记录哪个用户已插入工资表。创建审计表：

.. code-block:: shell

    mysql> CREATE TABLE salary_audit (emp_no int, user varchar(50), date_modified date);

请注意，以下触发器位于 ``salary_round`` 触发器之前，由 ``PRECEDES salary_round`` 指定：

.. code-block:: shell

    shell> vi before_insert_trigger.sql
    DELIMITER $$
    CREATE TRIGGER salary_audit
    BEFORE INSERT
        ON salaries FOR EACH ROW PRECEDES salary_round
    BEGIN
        INSERT INTO salary_audit VALUES(NEW.emp_no,
    USER(), CURDATE());
    END; $$
    DELIMITER ;

在 ``salaries`` 表中插入：

.. code-block:: shell

    mysql> INSERT INTO salaries VALUES(10003, 100000.79, CURDATE(), '9999-01-01');
    Query OK, 1 row affected (0.06 sec)

通过查询 ``salary_audit`` 表找出谁插入了工资：

.. code-block:: shell

    mysql> SELECT * FROM salary_audit WHERE emp_no=10003;
    +--------+----------------+---------------+
    | emp_no | user           | date_modified |
    +--------+----------------+---------------+
    | 10003  | root@localhost | 2017-06-18    |
    +--------+----------------+---------------+
    1 row in set (0.00 sec)

如果 ``salary_audit`` 表被删除或不可用，则工资表上的所有插入都将被阻止。如果您不想进行审核，则应首先删除触发器，然后再删除表。触发器可以根据其复杂性在写入速度上产生开销。 要检查所有触发器，请执行 ``SHOW TRIGGERS\G`` 。 要检查现有触发器的定义，请执行 ``SHOW CREATE TRIGGER <trigger_name>`` 。

更多详情，请参考 https://dev.mysql.com/doc/refman/8.0/en/trigger-syntax.html

视图
----
``View`` 是基于 ``SQL`` 语句的结果集的虚拟表。它也会像真正的表一样有行和列，但很少有限制，稍后将对此进行讨论。 视图隐藏了 ``SQL`` 复杂性，更重要的是，提供了额外的安全性。


假设您只想访问工资表的 ``emp_no`` 和 ``salary`` 列，而 ``from_date`` 满足是在 ``2002-01-01`` 之后。 为此，您可以使用提供所需结果的 ``SQL`` 创建视图。

.. code-block:: shell

    mysql> CREATE ALGORITHM=UNDEFINED
    DEFINER=`root`@`localhost`
    SQL SECURITY DEFINER VIEW salary_view
    AS
    SELECT emp_no, salary FROM salaries WHERE from_date >
    '2002-01-01';

现在 ``salary_view`` 视图被创建且你可以像其它表一样查询它：

.. code-block:: shell

    mysql> SELECT emp_no, AVG(salary) as avg FROM salary_view GROUP BY emp_no ORDER BY avg DESC LIMIT 5;

您可以看到该视图可以访问特定的行(即 ``from_date>'2002-01-01'`` )而不是所有行。您可以使用该视图来限制用户对特定行的访问。

为了列出所有视图，执行：

.. code-block:: shell

    mysql> SHOW FULL TABLES WHERE TABLE_TYPE LIKE 'VIEW';

为了检查视图定义，执行：

.. code-block:: shell

    mysql> SHOW CREATE VIEW salary_view\G

简单视图可以没有子查询， ``JOINS`` ， ``GROUP BY`` 子句， ``union`` 等的情况下更新。 ``salary_view`` 是一个简单的视图，如果底层表具有默认值，则可以更新或插入：

.. code-block:: shell

    mysql> UPDATE salary_view SET salary=100000 WHERE
    emp_no=10001;
    Query OK, 1 row affected (0.01 sec)
    Rows matched: 2 Changed: 1 Warnings: 0

    mysql> INSERT INTO salary_view VALUES(10001,100001);
    ERROR 1423 (HY000): Field of view
    'employees.salary_view' underlying table doesn't have
    a default value

如果表具有默认值，即使它与视图中的筛选条件不匹配，也可以插入一行。要避免这种情况，并插入满足视图条件的行，必须在定义中提供 ``WITH CHECK OPTION`` 。

视图算法：

- merge：合并算法；系统应该先将视图对应的 ``select`` 语句与外部查询视图的 ``select`` 语句进行合并，然后执行(效率高)，系统默认值。
- temptable：临时表算法；系统应该先执行视图的 select 语句生成临时表，然后在临时表上执行外部查询的语句。
- undefined：未定义(默认的)，这不是一种实际使用的算法，是一种推卸责任的算法—-告诉系统，视图没有定义算法，你看着办。MySQL会自动选择合并或者临时表算法。


事件(计划任务)
--------------
就像 ``Linux`` 服务器上的 ``cron`` 一样， ``MySQL`` 有 ``EVENTS`` 来处理预定的任务。 ``MySQL`` 使用一个称为事件调度线程的特殊线程来执行所有调度事件。默认情况下，未启用事件调度程序线程（版本<8.0.3），要启用它，请执行：

.. code-block:: shell

    mysql> SET GLOBAL event_scheduler = ON;

假设您不再需要保留超过一个月的工资审计记录，您可以安排每天运行的事件，并从 ``salary_audit`` 表中删除一个月前的记录。

.. code-block:: shell

    mysql> DROP EVENT IF EXISTS purge_salary_audit;
    DELIMITER $$
    CREATE EVENT IF NOT EXISTS purge_salary_audit
    ON SCHEDULE
        EVERY 1 WEEK
        STARTS CURRENT_DATE
            DO BEGIN
                DELETE FROM salary_audit WHERE date_modified < DATE_ADD(CURDATE(), INTERVAL -7 day);
            END $$
    DELIMITER ;

创建事件后，它将自动执行清除工资审计记录的工作。

- 为了检测事件，执行：

.. code-block:: shell

    mysql> SHOW EVENTS\G

- 为了检测具体的事件，执行：

.. code-block:: shell

    mysql> SHOW CREATE EVENT purge_salary_audit\G

- 要开启或关闭事件，执行如下命令：

.. code-block:: shell

    mysql> ALTER EVENT purge_salary_audit DISABLE;
    mysql> ALTER EVENT purge_salary_audit ENABLE;

更多信息，参考 https://dev.mysql.com/doc/refman/8.0/en/event-scheduler.html

访问控制
^^^^^^^^
所有存储的程序（过程，函数，触发器和事件）和视图都有一个 ``DEFINER`` 。如果未指定 ``DEFINER`` ，则创建对象的用户将被选为 ``DEFINER`` 。

存储的例程（过程和函数）和视图具有 ``SQL SECURITY`` 特性，其值为 ``DEFINER`` 或 ``INVOKER`` ，用于指定对象是在定义者还是调用者上下文中执行。 触发器和事件没有 ``SQL SECURITY`` 特性，并始终在定义器上下文中执行。服务器根据需要自动调用这些对象，因此没有调用用户。

**DEFINER**

如：创建 procedure

.. code-block:: shell

    CREATE DEFINER = 'admin'@'localhost' PROCEDURE account_count()
    BEGIN
        SELECT 'Number of accounts:'， COUNT(*) FROM mysql.user;
    END;

上面示例指定 ``definer`` 为用户 'admin'@'localhost' ，所以任意用户A访问该 ``PROCEDURE`` 时，能否成功取决于 A 是否有调用该 ``PROCEDURE`` 的权限，以及 ``definer`` 是否有 ``procedure`` 中的 ``SELECT`` 的权限。

**SQL SECURITY**

``DEFINER`` 默认为当前用户，也可指定其他用户。如果想通过访问者来判断是否具有访问该 ``PROCEDURE`` 的权限，则可用 ``SQL SECURITY`` 指定。

.. code-block:: shell

    CREATE DEFINER = 'admin'@'localhost' PROCEDURE account_count()
    SQL SECURITY INVOKER
    BEGIN
        SELECT 'Number of accounts:'， COUNT(*) FROM mysql.user;
    END;

该示例虽然指定了 ``DEFINER`` ，但同时也指定了 ``SQL SECURITY`` 类型为 ``INVOKER`` ， ``SQL SECURITY`` 优先级高，所以安全类型为 ``INVOKER`` ，用户能否访问取决于用户是否有执行该 ``PROCEDURE`` 的权限及该 ``PROCEDURE`` 中的 ``SELECT`` 权限（与 ``select`` 操作的表有关）。

当然，也可用 ``SQL SECURITY`` 指定 ``DEFINER:SQL SECURITY DEFINER`` 。

获取数据库和表的元信息
----------------------
您可能已经注意到数据库列表中的 ``information_schema`` 数据库。 ``information_schema`` 是一组视图，由关于所有数据库对象的元数据组成。您可以连接到 ``information_schema`` 并浏览所有表。本章将介绍最常用的表格。 您可以查询 ``information_schema`` 表或使用 ``SHOW`` 命令，它们本质上是一样的。 ``INFORMATION_SCHEMA`` 查询实现为 ``data dictionary`` 表的视图。 ``INFORMATION_SCHEMA`` 表中有两种类型的元数据：

- 静态表元素： ``TABLE_SCHEMA`` ,  ``TABLE_NAME`` , ``TABLE_TYPE`` 和 ``ENGINE`` 。静态表将直接从 data dictionary 直接读取。
- 动态表元素： ``AUTO_INCREMENT`` , ``AVG_ROW_LENGTH`` 和 ``DATA_FREE`` 。动态元数据频繁改变(例如，每次插入字后 ``AUTO_INCREMENT`` 值会增加)。在许多情况下，动态元数据也会产生一些成本，由于准确计算的需要，并且准确性可能对典型查询不利。考虑 ``DATA_FREE`` 统计信息的情况，该统计信息显示表中的空闲字节数 - 缓存值通常就足够了。

在MySQL 8.0中，动态表元数据将默认为缓存。这可以通过 ``information_schema_stats`` 设置（默认缓存）进行配置，并且可以更改为 ``SET @@ GLOBAL.information_schema_stats ='LATEST'`` ，以便始终直接从存储引擎检索动态信息（代价略高于查询执行）。作为替代方案，用户还可以在表上执行 ``ANALYZE TABLE`` ，以更新缓存的动态统计信息。大多数表都有 ``TABLE_SCHEMA`` 列（引用数据库名称）和 ``TABLE_NAME`` 列（引用表名称）。

详情，请参考 https://mysqlserverteam.com/mysql-8-0-improvements-to-information_schema/ 。

查看数据库的所有表

.. code-block:: shell

    mysql> USE INFORMATION_SCHEMA;
    mysql> SHOW TABLES;

TABLES
^^^^^^
``TABLES`` 表包含有关表的所有信息，例如该表属于哪个数据库的 ``TABLE_SCHEMA`` ，行数（ ``TABLE_ROWS`` ）， ``ENGINE`` ， ``DATA_LENGTH`` ， ``INDEX_LENGTH`` 和 ``DATA_FREE`` ：

.. code-block:: shell

    mysql> DESC INFORMATION_SCHEMA.TABLES;

例如，您想知道 ``employees`` 数据库中的 ``DATA_LENGTH`` ， ``INDEX_LENGTH`` 和 ``DATE_FREE`` ：

.. code-block:: shell

    mysql> SELECT SUM(DATA_LENGTH)/1024/1024 AS
    DATA_SIZE_MB, SUM(INDEX_LENGTH)/1024/1024 AS
    INDEX_SIZE_MB, SUM(DATA_FREE)/1024/1024 AS
    DATA_FREE_MB FROM INFORMATION_SCHEMA.TABLES WHERE
    TABLE_SCHEMA='employees';
    +--------------+---------------+--------------+
    | DATA_SIZE_MB | INDEX_SIZE_MB | DATA_FREE_MB |
    +--------------+---------------+--------------+
    | 17.39062500 | 14.62500000 | 11.00000000 |
    +--------------+---------------+--------------+
    1 row in set (0.01 sec)

COLUMNS
^^^^^^^
此表列出了每个表的所有列及其定义：

.. code-block:: shell

    mysql> SELECT * FROM COLUMNS WHERE TABLE_NAME='employees'\G

FILES
^^^^^
您已经看到 ``MySQL`` 将 ``InnoDB`` 数据存储在 ``data directory`` 中的目录（与数据库名称相同）内的 ``.ibd`` 文件中。要获取有关文件的更多信息，可以查询 ``FILES`` 表：

.. code-block:: shell

    mysql> SELECT * FROM FILES WHERE FILE_NAME LIKE './employees/employees.ibd'\G
    ~~~
    EXTENT_SIZE: 1048576
    AUTOEXTEND_SIZE: 4194304
    DATA_FREE: 13631488
    ~~~

您应该热衷于 ``DATA_FREE`` ，它表示未分配的段加上由于碎片而在段内可用的数据。重建表时，可以释放 ``DATA_FREE`` 中显示的字节。

INNODB_SYS_TABLESPACES
^^^^^^^^^^^^^^^^^^^^^^
``INNODB_TABLESPACES`` 表中也提供了可用文件的大小：

.. code-block:: shell

    mysql> SELECT * FROM INNODB_TABLESPACES WHERE NAME='employees/employees'\G
    *************************** 1. row ***************************
    SPACE: 118
    NAME: employees/employees
    FLAG: 16417
    ROW_FORMAT: Dynamic
    PAGE_SIZE: 16384
    ZIP_PAGE_SIZE: 0
    SPACE_TYPE: Single
    FS_BLOCK_SIZE: 4096
    FILE_SIZE: 32505856
    ALLOCATED_SIZE: 32509952
    1 row in set (0.00 sec)

您可以在文件系统中验证同一个文件：

.. code-block:: shell

    shell> sudo ls -ltr
    /var/lib/mysql/employees/employees.ibd
    -rw-r----- 1 mysql mysql 32505856 Jun 20 16:50
    /var/lib/mysql/employees/employees.ibd


INNODB_TABLESTATS
^^^^^^^^^^^^^^^^^
``INNODB_TABLESTATS`` 表中提供了索引的大小和大致的行数：

.. code-block:: shell

    mysql> SELECT * FROM INNODB_TABLESTATS WHERE NAME='employees/employees'\G
    *************************** 1. row ***************************
    TABLE_ID: 128
    NAME: employees/employees
    STATS_INITIALIZED: Initialized
    NUM_ROWS: 299468
    CLUST_INDEX_SIZE: 1057
    OTHER_INDEX_SIZE: 545
    MODIFIED_COUNTER: 0
    AUTOINC: 0
    REF_COUNT: 1
    1 row in set (0.00 sec)


PROCCESSLIST
^^^^^^^^^^^^
最常用的视图之一是进程列表。它列出了服务器上运行的所有查询：

.. code-block:: shell

    mysql> SELECT * FROM PROCESSLIST\G
    *************************** 1. row ***************************
    ID: 85
    USER: event_scheduler
    HOST: localhost
    DB: NULL
    COMMAND: Daemon
    TIME: 44
    STATE: Waiting for next activation
    INFO: NULL
    *************************** 2. row ***************************
    ID: 26231
    USER: root
    HOST: localhost
    DB: information_schema
    COMMAND: Query
    TIME: 0
    STATE: executing
    INFO: SELECT * FROM PROCESSLIST
    2 rows in set (0.00 sec)

或者你执行 ``SHOW PROCESSLIST`` 获取相同的输出。

其它表： ``ROUTINES`` 包含函数和存储例程的定义。 ``TRIGGERS`` 包含触发器的定义。 ``VIEWS`` 包含视图的定义。

详情，请参考 http://mysqlserverteam.com/mysql-8-0-improvements-to-information_schema/


