************
MongoDB - 连接
************

启动 MongoDB 服务
==================
在前面的教程中，我们已经讨论了如何启动 MongoDB 服务，你只需要在 MongoDB 安装目录的 ``bin`` 目录下执行 ``mongodb`` 即可。

执行启动操作后， ``mongodb`` 在输出一些必要信息后不会输出任何信息，之后就等待连接的建立，当连接被建立后，就会开始打印日志信息。

你可以使用 MongoDB shell 来连接 MongoDB 服务器。你也可以使用 PHP 来连接 MongoDB 。本教程我们会使用 MongoDB shell 来连接 Mongodb 服务，之后的章节我们将会介绍如何通过 php 来连接 MongoDB 服务。

标准 URI 连接语法：

.. code-block:: shell

    mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]

- ``mongodb://`` 这是固定的格式，必须要指定。
- ``username:password@`` 可选项，如果设置，在连接数据库服务器之后，驱动都会尝试登陆这个数据库
- ``host1`` 必须的指定至少一个 ``host`` 它可以是主机名，IP地址或者UNIX域socket,  ``host1`` 是这个 ``URI`` 唯一要填写的。它指定了要连接服务器的地址。如果要连接复制集，请指定复制集配置中列出的 ``mongod`` 实例的主机名。如果是分片集群，请指定 ``mongos`` 实例的主机名。
- ``portX`` 可选的指定端口，如果不填，默认为 27017
- ``/database`` 如果指定 ``username:password@`` ，连接并验证登陆指定数据库。如果提供了认证凭证却没有提供该参数，则默认为 ``admin`` 。若没有提供认证凭证且不指定该参数，默认打开 ``test`` 数据库。
- ``?options`` 是连接选项。如果不使用 ``/database`` ，则前面需要加上 ``/``。所有连接选项都是键值对 ``name=value`` ，键值对之间通过 ``&`` 或 ``;`` （分号）隔开。

标准的连接格式包含了多个选项(options)，如下所示：

复制集选项：

- replicaSet=name：如果 mongod 是副本集的成员，则指定 replica set 的名称。连接到副本集时，必须提供至少两个 ``mongod`` 实例的种子列表。如果仅提供单个 mongod 实例的连接点且省略 replicaSet ，则客户端将创建独立连接。

连接选项：

- ssl：默认值为false。

  - true：使用 ``TLS/SSL`` 启动连接。
  - false：在没有 ``TLS/SSL`` 的情况下启动连接。

- connectTimeoutMS=ms：打开连接的时间。
- socketTimeoutMS=ms：发送和接受sockets的时间。

连接池选项：

大多数驱动程序实现某种连接池处理。某些驱动程序不支持连接池。有关连接池实现的更多信息，请参阅驱动程序文档。这些选项允许应用程序在连接到 MongoDB 部署时配置连接池。

- maxPoolSize：连接池中的最大连接数。默认值为100。
- minPoolSize：连接池中的最小连接数。默认值为0。
- maxIdleTimeMS：在删除和关闭之前，连接在池中保持空闲的最大毫秒数。
- waitQueueMultiple：驱动程序将 maxPoolSize 值乘以的数字，以提供允许等待连接从池中获得的最大线程数。有关默认值，请参阅MongoDB驱动程序和客户端库文档。驱动强行限制线程同时等待连接的个数。
- waitQueueTimeoutMS：线程可以等待连接变为可用的最长时间（以毫秒为单位）。

写关注的选项：

写入关注描述了 mongod 和驱动程序为应用程序提供的关于写入操作的成功和持久性的保证。

.. note:: 您可以在连接字符串中指定写入关注点，也可以指定插入或更新等方法调用的参数。如果在两个位置都指定了写入关注点，则 ``method`` 参数将覆盖连接字符串设置。

- w：对应写作关注 w 选项。 w 选项请求确认写操作已传播到指定数量的 ``mongod`` 实例或具有指定标签的 ``mongod`` 实例。您可以指定数字，字符串 ``majority`` 或标记集。
- wtimeoutMS：对应写作关注 ``wtimeout`` 。 ``wtimeoutMS`` 指定写入关注的时间限制（以毫秒为单位）。当 ``wtimeoutMS`` 为 ``0`` 时，写操作永远不会超时。
- journal：对应于写入关注 ``j`` 选项。 ``journal`` 选项请求 MongoDB 确认写操作已写入日志。如果将 ``journal`` 设置为 ``true`` ，并且 ``mongod`` 没有启用日记功能 ``storage.journal.enabled`` 一样，那么 MongoDB 将会出错。

读关注的选项：

3.2版中的新功能：对于 ``WiredTiger`` 存储引擎， MongoDB 3.2 为副本集和副本集分片引入了 ``readConcern`` 选项。Read Concern允许客户端为副本集中的读取选择隔离级别。

- readConcernLevel：隔离程度。可以接受以下值之一：local，majority，linearizable，available 。

读首选项：

读首选项描述了与副本集有关的读取操作的行为。这些参数允许您在连接字符串中基于每个连接指定读取首选项：

- readPreference：指定副本集中读取首选项
- maxStalenessSeconds：
- readPreferenceTags：将标签集指定为以逗号分隔的冒号分隔的键值对列表。

认证选项：

- authSource：指定与用户凭据关联的数据库名称。 ``authSource`` 默认为连接字符串中指定的数据库。
- authMechanism：指定MongoDB用于验证连接的身份验证机制。
- gssapiServiceName：


实例
====
使用默认端口来连接 MongoDB 的服务。

.. code-block:: shell

    mongodb://localhost

通过 shell 连接 MongoDB 服务：

.. code-block:: shell

    $ ./mongo
    MongoDB shell version: 3.0.6
    connecting to: test
    ...

这时候你返回查看运行 ``./mongod`` 命令的窗口，可以看到是从哪里连接到 MongoDB 的服务器，您可以看到如下信息：

.. code-block:: shell

    ……省略信息……
    2015-09-25T17:22:27.336+0800 I CONTROL  [initandlisten] allocator: tcmalloc
    2015-09-25T17:22:27.336+0800 I CONTROL  [initandlisten] options: { storage: { dbPath: "/data/db" } }
    2015-09-25T17:22:27.350+0800 I NETWORK  [initandlisten] waiting for connections on port 27017
    2015-09-25T17:22:36.012+0800 I NETWORK  [initandlisten] connection accepted from 127.0.0.1:37310 #1 (1 connection now open)  # 该行表明一个来自本机的连接

    ……省略信息……

MongoDB 连接命令格式
====================
使用用户名和密码连接到 MongoDB 服务器，你必须使用 ``username:password@hostname/dbname`` 格式， ``username`` 为用户名， ``password`` 为密码。

使用用户名和密码连接登陆到指定数据库，格式如下：

.. code-block:: shell

    mongodb://admin:123456@localhost/test

更多连接实例
============
连接本地数据库服务器，端口是默认的。

.. code-block:: shell

    mongodb://localhost

使用用户名 ``fred`` ，密码 ``foobar`` 登录 ``localhost`` 的 ``admin`` 数据库。

.. code-block:: shell

    mongodb://fred:foobar@localhost

使用用户名 ``fred`` ，密码 ``foobar`` 登录 ``localhost`` 的 ``baz`` 数据库。

.. code-block:: shell

    mongodb://fred:foobar@localhost/baz

连接 replica pair, 服务器1为 example1.com 服务器2为 example2 。

.. code-block:: shell

    mongodb://example1.com:27017,example2.com:27017

连接 replica set 三台服务器 (端口 27017, 27018, 和27019):

.. code-block:: shell

    mongodb://localhost,localhost:27018,localhost:27019

连接 replica set 三台服务器, 写入操作应用在主服务器 并且分布查询到从服务器。

.. code-block:: shell

    mongodb://host1,host2,host3/?slaveOk=true

直接连接第一个服务器，无论是 replica set 一部分或者主服务器或者从服务器。

.. code-block:: shell

    mongodb://host1,host2,host3/?connect=direct;slaveOk=true

当你的连接服务器有优先级，还需要列出所有服务器，你可以使用上述连接方式。

安全模式连接到localhost:

.. code-block:: shell

    mongodb://localhost/?safe=true

以安全模式连接到 replica set ，并且等待至少两个复制服务器成功写入，超时时间设置为2秒。

.. code-block:: shell

    mongodb://host1,host2,host3/?safe=true;w=2;wtimeoutMS=2000

参考 https://docs.mongodb.com/manual/reference/connection-string/