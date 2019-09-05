*********
配置MySQL
*********
在本章中，我们将会覆盖如下主题：

- 使用配置文件；
- 使用全局和会话变量；
- 在启动脚本中使用参数；
- 配置参数；
- 改变 data directory；

简介
====
MySQL具有两种类型的参数：

- 静态：在重新启动MySQL服务端之后生效的；
- 动态：不用重新启动MySQL服务端就会立即生效的；

变量可以通过如下方式设置：

- 配置文件：MySQL具有一个配置文件，可以用来指定数据位置，MySQL可以使用的内存以及各种其它参数；
- 启动脚本：你可以直接传递参数到 ``mysqld`` 进程。它仅对该服务进程保持有效。
- 使用SET命令：(只能是动态变量)，持续到服务重新启动。使更改持久化的另一种方法是在变量名前加上 ``PERSIST`` 关键字或 ``@@persist`` 。

使用配置文件
============
默认的配置文件是 ``/etc/my.cnf`` (在 ``Red Hat`` 和 ``CentOS`` 系统中)和 ``/etc/mysql/my.cnf`` (在 ``Debian`` 系统中)。在您喜欢的编辑器中打开文件，并根据需要修改参数。 本章将讨论主要参数。


配置文件包含 ``section_name`` 指定的部分。与该部分相关的所有参数都可以放在它们下面，例如：

.. code-block:: ini

    [mysqld] <---section name
    <parameter_name> = <value> <---parameter values
    [client]
    <parameter_name> = <value>
    [mysqldump]
    <parameter_name> = <value>
    [mysqld_safe]
    <parameter_name> = <value>
    [server]
    <parameter_name> = <value>

- [mysql] ： 该部分被 ``mysql`` 命令行客户端读取；
- [client] ： 该部分被所有连接客户端读取(包括 ``mysql cli`` )；
- [mysqld] ： 该部分被 ``mysql`` 服务端读取；
- [mysqldump] ： 该部分被 ``mysqldump`` 备份工具读取；
- [mysqld_safe] ： 该部分被 ``mysqld_safe`` 进程(它是 ``MySQL`` 服务端启动脚本)读取；

除了 ``mysqld_safe`` 进程读取 ``[mysqld]`` 和 ``[server]`` 中所有选项。其它的只读取自己的部分。

例如， ``mysqld_safe`` 进程从 ``mysqld`` 部分读取 ``pid-file`` 选项。

.. code-block:: shell

    shell> sudo vi /etc/my.cnf
    [mysqld]
    pid-file = /var/lib/mysql/mysqld.pid

在使用 ``systemd`` 的系统中，不会安装 ``mysqld_safe`` 。要配置启动脚本，需要在 ``/etc/systemd/system/mysqld.service.d/override.conf`` 中设置值。

例如：

.. code-block:: ini

    [Service]
    LimitNOFILE=max_open_files
    PIDFile=/path/to/pid/file
    LimitCore=core_file_limit
    Environment="LD_PRELOAD=/path/to/malloc/library"
    Environment="TZ=time_zone_setting

使用全局和会话变量
==================
正如您在前面的章节中所看到的，您可以通过连接到 ``MySQL`` 并执行 ``SET`` 命令来设置参数。

根据变量的范围有两种类型的变量：

- 全局：应用到所有新的连接；
- 会话：只应用到当前连接(会话)；

例如，如果你想记录查询时间超过1秒的所有查询，你可以执行：

.. code-block:: shell

    mysql> SET GLOBAL long_query_time = 1;

为了让改变跨重启使用：

.. code-block:: shell

    mysql> SET PERSIST long_query_time = 1;

或者

.. code-block:: shell

    mysql> SET @@persist.long_query_time = 1;

持久化的全局系统变量设置存储在 ``mysqld-auto.cnf`` 中，该文件位于数据目录(data directory)中。

假设您只想记录此会话的查询，而不是所有连接的记录。您可以使用以下命令：

.. code-block:: shell

    mysql> SET SESSION long_query_time = 1;

在启动命令中使用参数
====================
假设您希望使用启动命令中而不是通过 ``systemd`` 启动 ``MySQL`` ，尤其是用于测试或进行一些临时更改。您可以将变量传递给命令，而不是在配置文件中更改它。

.. code-block:: shell

    shell> /usr/local/mysql/bin/mysqld
    --basedir=/usr/local/mysql
    --datadir=/usr/local/mysql/data
    --plugin-dir=/usr/local/mysql/lib/plugin
    --user=mysql
    --log-error=/usr/local/mysql/data/centos7.err
    --pid-file=/usr/local/mysql/data/centos7.pid
    --init-file=/tmp/mysql-init &

您可以看到 ``--init-file`` 参数传递给服务器。服务器在启动之前执行该文件中的 ``SQL`` 语句。

配置参数
========
安装后，本节将介绍您需要配置的基本内容。其余的都可以保留为默认值或稍后根据负载调整。

data directory
--------------
MySQL服务器管理的数据存储在称为数据目录(data directory)的目录下。数据目录的每个子目录都是一个数据库目录，对应于服务器管理的数据库。 默认情况下 data directory 目录中存在三个子目录：

- ``mysql`` ： MySQL系统数据库；
- ``performance_schema`` ： 提供用于在运行时检查服务器内部执行的信息；
- ``sys`` ： 提供一组对象，以帮助更轻松地说明性能信息；

除了这些， data directory 包含日志文件， ``InnoDB`` 表空间和 ``InnoDB`` 日志文件， ``SSL`` 和 ``RSA key`` 文件， ``mysqld`` 的 ``pid`` ，和存储持久的全局系统变量设置的 ``mysqld-auto.cnf`` 文件。

为了设置 data directory 的值，在配置文件中增加或修改 ``datadir`` 值。默认为 ``/var/lib/MySQL`` ：

.. code-block:: shell

    shell> sudo vi /etc/my.cnf
    [mysqld]
    datadir = /data/mysql

您可以将其设置为要存储数据的位置，但应将数据目录(data directory)的拥有者更改为 ``mysql`` 。

.. note:: 确保包含数据目录的磁盘卷有足够的空间来容纳所有数据。

innodb_buffer_pool_size
------------------------
这是最重要的调整参数，它决定 ``InnoDB`` 存储引擎可以使用多少内存来缓存数据和索引到内存中。将其设置得太低会降低 ``MySQL`` 服务器的性能，将其设置得太高会增加 ``MySQL`` 进程的内存消耗。在 MySQL 8 中它是 ``innodb_buffer_pool_size`` 是动态的，这意味着你可以在不重启服务器的情况下改变 ``innodb_buffer_pool_size`` 。

下面是一个关于如何调整它的简单指南：

1. 找出数据集的大小。不要将 ``innodb_buffer_pool_size`` 的值设置为高于数据集的值。假设你有一台 12GB 的 RAM 机器，你的数据集是 3GB ；然后你可以将 ``innodb_buffer_pool_size`` 设置为 3GB 。 如果您预期数据增长，可以根据需要增加数据，而无需重新启动 MySQL 。
2. 通常，数据集的大小远大于可用的 RAM 。在总 RAM 中，您可以设置一些用于操作系统，一些用于其他进程，一些用于 MySQL 内部的每线程缓冲区，一些用于除 ``InnoDB`` 之外的 MySQL 服务器。其余的可以分配给 ``InnoDB`` 缓冲池大小。这是一个非常通用的表，并为您提供了一个很好的值，假设它是一个专用的 MySQL 服务器，所有表都是 ``InnoDB`` ，每个线程缓冲区保留为默认值。如果系统内存不足，则可以动态减少缓冲池。

+-------+----------------+
| RAM   | 缓冲池大小范围 |
+=======+================+
| 4GB   | 1GB - 2GB      |
+-------+----------------+
| 8GB   | 4GB - 6GB      |
+-------+----------------+
| 12GB  | 6GB - 10GB     |
+-------+----------------+
| 16GB  | 10GB - 12GB    |
+-------+----------------+
| 32GB  | 24GB - 28GB    |
+-------+----------------+
| 64GB  | 45GB - 56GB    |
+-------+----------------+
| 128GB | 108GB - 116GB  |
+-------+----------------+
| 256GB | 220GB - 245GB  |
+-------+----------------+

innodb_buffer_pool_instances
----------------------------
您可以将 ``InnoDB`` 缓冲池划分为单独的区域，以通过减少不同线程读取和写入缓存页面时的争用来提高并发性。 例如，如果缓冲池大小为 64GB 且 ``innodb_buffer_pool_instances`` 为 32 ，则缓冲区将拆分为 32 个区域，每个区域为 2GB 。

如果缓冲池大小超过 16GB ，则可以设置实例，以便每个区域至少获得 1GB 的空间。

innodb_log_file_size
--------------------
这是重做日志空间的大小，用于在数据库崩溃时重放已提交的事务。默认值为 48MB ，这可能不足以用于生产工作负载。首先，您可以设置 1GB 或 2GB 。此更改需要重新启动。停止 MySQL 服务器并确保它关闭而没有错误。在 ``my.cnf`` 中进行更改并启动服务器。在早期版本中，您需要停止服务器，删除日志文件，然后启动服务器。 在 MySQL 8 中，它是自动的。第11章“管理表空间”中的“更改 ``InnoDB`` 重做日志文件的数量或大小”部分介绍了如何修改重做日志文件。

改变 data directory
===================
您的数据会随着时间的推移而增长，当它超出文件系统时，您需要添加磁盘或将数据目录(data directory)移动到更大的卷。

1. 查看 data directory 目录位置。默认为 /var/lib/mysql ：

    .. code-block:: shell

    mysql> show variables like '%datadir%';
    +---------------+-----------------+
    | Variable_name | Value           |
    +---------------+-----------------+
    | datadir       | /var/lib/mysql/ |
    +---------------+-----------------+
    1 row in set (0.04 sec)

2. 停止 mysql 和确保它已经成功停止：

.. code-block:: shell

    shell> sudo systemctl stop mysql

3. 检查进程状态：

.. code-block:: shell

    shell> sudo systemctl status mysql

4. 在新位置创建目录，然后改变目录的所有者为 ``mysql`` ：

.. code-block:: shell

    shell> sudo mkdir -pv /data
    shell> sudo chown -R mysql:mysql /data/

5. 将文件移动到新数据目录：

.. code-block:: shell

    shell> sudo rsync -av /var/lib/mysql /data

6. 在Ubuntu中，如果你开启了 AppArmor ，你需要配置访问控制：

.. code-block:: shell

    shell> vi /etc/apparmor.d/tunables/alias
    alias /var/lib/mysql/ -> /data/mysql/,
    shell> sudo systemctl restart apparmor

7. 启动 MySQL 服务端然后验证数据目录是否改变：

.. code-block:: shell

    shell> sudo systemctl start mysql
    mysql> show variables like '%datadir%';
    +---------------+--------------+
    | Variable_name | Value        |
    +---------------+--------------+
    | datadir       | /data/mysql/ |
    +---------------+--------------+
    1 row in set (0.00 sec)

8. 验证数据是否完整并删除旧数据目录：

.. code-block:: shell

    shell> sudo rm -rf /var/lib/mysql

如果MySQL因为 MySQL data dir not found at /var/lib/mysql, please create one: 启动失败。则可以执行 ``sudo mkdir /var/lib/mysql/mysql -p`` ；

如果它说 ``MySQL system database not found`` 运行 ``mysql_install_db`` 工具来创建必须的目录。

