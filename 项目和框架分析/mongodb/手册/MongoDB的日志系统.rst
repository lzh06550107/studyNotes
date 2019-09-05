*****************
MongoDB的日志系统
*****************

MongoDB 中主要有四种日志。分别是 系统日志 、 Journal日志 、 oplog主从日志 、 慢查询日志 等。这些日志记录着 Mongodb 数据库不同方便的踪迹。下面分别介绍这四种日志：

系统日志
========
系统日志在 Mongdb 数据中很中重要，它记录 mongodb 启动和停止的操作，以及服务器在运行过程中发生的任何异常信息；配置系统日志也非常简单，在运行 mongod 时候增加一个参数 logpath ，就可以设置；

例如： ``mongod -logpath='/data/db/log/server.log' -logappend`` .

Journal[ˈdʒɜ:nl]日志
===================
``Jouranl`` 日志通过预写入的 ``redo`` 日志为 ``mongodb`` 增加了额外的可靠性保障。开启该功能时候，数据的更新就先写入 ``Journal`` 日志，定期集中提交（目前是每 100ms 提交一次） ，然后在正式数据执行更改。启动数据库的 ``Journal`` 功能非常简单，只需在 ``mongod`` 后面指定 ``journal`` 参数即可；

开启方式： ``mongod -journal``

Oplog主从日志
=============
Mongodb 的高可用复制策略有一个叫做 ReplicaSets 。ReplicaSet 复制过程中有一个服务器充当主服务器，而一个或多个充当从服务器，主服务将更新写入一个本地的 collection 中，这个 collection 记录着发生在主服务器的更新操作。并将这些操作分发到从服务器上。这个日志是 Capped Collection 。利用如下命令可以配置。

例如： ``mongod -oplogSize=1024`` 单位是 M 。

慢查询日志
==========
慢查询记录了执行时间超过了所设定时间阀值的操作语句。慢查询日志对于发现性能有问题的语句很有帮助，建议开启此功能并经常分析该日志的内容。

要配置这个功能只需要在 ``mongod`` 启动时候设置 ``profile`` 参数即可。例如想要将超过 ``5s`` 的操作都记录，可以使用如下语句：

例如： ``mongod --profile=1 --slowms=5`` 。


MongoDB配置按天存储日志文件
===========================

mongodb 默认不提供直接按天来输出日志文件的配置，但是提供一个日志清理的命令： ``logRotate`` 。如果日志不及时清理会导致 ``mongo`` 访问越来越慢，甚至卡死。

要使用 ``logRotate`` 命令需要进入到 ``mongo shell`` ，然后执行：

.. code-block:: shell

    use admin
    #db.auth('username','password');
    db.runCommand({logRotate:1});

必须要进到 ``admin`` 库中才生效，如果配置 ``auth`` 需要填写用户名和密码。

执行完毕后会在 ``logpath`` 目录中生成一个当前时间节点的备份文件，并且原来的日志文件内容会被清除掉。

在生产环境上不可能每天定时由人工来执行日志文件清理，所以可以写一个脚本，由 ``crontab`` 定时在每天定时执行转储工作，具体脚本内容如下：

.. code-block:: shell

    mongo 127.0.0.1:27021/admin --eval "db.auth('admin','admin@123');db.runCommand({logRotate:1});"
    mongo 127.0.0.1:27022/admin --eval "db.auth('admin','admin@123');db.runCommand({logRotate:1});"
    mongo 127.0.0.1:27023/admin --eval "db.auth('admin','admin@123');db.runCommand({logRotate:1});"

其中需要配置 ``IP`` 和端口，如果开启 ``auth`` ，需要填写访问 ``admin`` 库的用户名和密码。

crontab的配置如下：

.. code-block:: ini

    05 00 * * * root /usr/soft/mongodb/logrotate.sh >> logrotate.log