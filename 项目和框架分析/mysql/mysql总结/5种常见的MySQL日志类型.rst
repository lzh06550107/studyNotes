**************
5种常见的MySQL日志类型
**************

MySQL日志是我们需要掌握的知识，下面就为您介绍几个最常见的MySQL日志类型。

错误日志
========
记录启动、运行或停止 ``mysqld`` 时出现的问题。

``My.ini`` 配置信息：

.. code-block:: shell

	#Enter a name for the error log file.   Otherwise a default name will be used.
	#log-error=d:/mysql_log_err.txt

查询日志
========
记录建立的客户端连接和执行的语句。

``My.ini`` 配置信息：

.. code-block:: shell

	#Enter a name for the query log file. Otherwise a default name will be used.
	#log=d:/mysql_log.txt

更新日志
========
记录更改数据的语句。不赞成使用该日志。

``My.ini`` 配置信息：

.. code-block:: shell

	#Enter a name for the update log file. Otherwise a default name will be used.
	#log-update=d:/mysql_log_update.txt

二进制日志
==========
记录所有更改数据的语句。还用于复制。

``My.ini`` 配置信息：

.. code-block:: shell

	#Enter a name for the binary log. Otherwise a default name will be used.
	#log-bin=d:/mysql_log_bin

二进制日志存储修改数据库中表数据的所有动作，包含了所有更新了数据或者已经潜在更新了数据的所有语句。潜在跟新了数据的 SQL 语句例如：无法匹配行的 DELETE 语句；设置列为当前值的 UPDATE 语句。除此之外，该日志还存储了语句执行期间耗时的相关信息。二进制日志文件以一种更有效并且是事务安全的方式包含更新日志中可用的所有信息。 MySQL 在执行语句之后，但在释放锁之前，马上将修改写入二进制日志中，

使用 log-bin[=file_name] 选项启动该日志类型， mysqld 写入包含所有更新数据的 SQL 命令的日志文件。如果未给出 file_name 值，默认名为 "HOSTNAME-bin.nnnnn"； 如果给出了文件名，但没有包含路径，则文件被写入数据目录。如果在日志名中提供了扩展名(例如， log-bin=file_name.extension )，则扩展名被悄悄除掉并忽略。二进制日志文件名的 .nnnn 表示， mysqld 在每个二进制日志名后面添加一个数字扩展名。每次启动服务器或刷新日志（flush logs）时该数字增加 1 。如果当前的日志大小达到设定的 max_binlog_size ，还会自动创建新的二进制日志。如果在该文件的末尾正使用大的事务，二进制日志还有可能个会超过 max_binlog_size ：同一个事务全写入一个二进制日志中，绝对不要写入不同的二进制日志中。

binlog-do-db=db_name
---------------------
告诉主服务器，如果当前的数据库(即USE选定的数据库)是 ``db_name`` ，应将更新记录到二进制日志中。其它所有没有明显指定的数据库被忽略。

如果数据库启动时使用选项 ``binlog-do-db=DB_A`` (定义的是当前使用数据库是否在此配置)，使用语句 ``use DB_B`` 置 ``DB_B`` 为当前数据库，此时使用 ``update`` 语句修改 ``DB_A`` 的表数据时出现如下情况：

- 数据库 ``DB_B`` 不在允许 ``binlog`` 的列表内，该语句不写入二进制日志文件
- 数据库 ``DB_B`` 在允许 ``binlog`` 的列表内，该语句写入二进制日志文件

binlog-ignore-db=db_name
-------------------------
告诉主服务器，如果当前的数据库(即USE选定的数据库)是 ``db_name`` ，不将更新保存到二进制日志中。
综上所述，使用这个两个选项时决定是否将该语句写入日志文件还有参考当前数据库的属性，如果指定了这两个选项尽量使指定的数据库为当前数据库，才能按照逻辑来记录日志。但 CREATE DATABASE 、 ALTER DATABASE 和 DROP DATABASE 等语句，有一个例外，即通过操作的数据库来决定是否应记录语句。

慢日志
======
记录所有执行时间超过 ``long_query_time`` 秒的所有查询或不使用索引的查询。

``My.ini`` 配置信息：

.. code-block:: shell

	#Enter a name for the slow query log file. Otherwise a default name will be used.
	#long_query_time =1
	#log-slow-queries= d:/mysql_log_slow.txt
