**************************
MongoDB 安全和访问权限控制
**************************
MongoDB 的访问控制能够有效保证数据库的安全，访问控制是指绑定 Application 监听的 IP 地址，设置监听端口，使用账户和密码登录。

访问控制的参数
==============

绑定IP地址
----------

``mongod`` 参数： ``--bind_ip <ip address>``

默认值是所有的 ``IP`` 地址都能访问，该参数指定 ``MongoDB`` 对外提供服务的绑定 ``IP`` 地址，用于监听客户端 ``Application`` 的连接，客户端只能使用绑定的 ``IP`` 地址才能访问 ``mongod`` ，其他 ``IP`` 地址是无法访问的。

设置监听端口
------------

``mongod`` 参数： ``--port <port>``

``MongoDB`` 默认监听的端口是 ``27017`` ，该参数显式指定 ``MongoDB`` 实例监听的 ``TCP`` 端口，只有当客户端 Application 连接的端口和 MongoDB 实例监听的端口一致时，才能连接到 MongoDB 实例。

启用用户验证
------------

``mongod`` 参数： ``--auth``

默认值是不需要验证，即 ``--noauth`` ，该参数启用用户访问权限控制；当 ``mongod`` 使用该参数启动时， ``MongoDB`` 会验证客户端连接的账户和密码，以确定其是否有访问的权限。如果认证不通过，那么客户端不能访问 MongoDB 的数据库。

权限认证
--------

- mongo 参数：--username <username>, -u <username>
- mongo 参数：--password <password>, -p <password>
- mongo 参数：--authenticationDatabase <dbname>  指定创建 User 的数据库；在特定的数据库中创建 User ，该 DB 就是 User 的 authentication database 。

在使用 ``mongo`` 连接时，使用参数 ``--authenticationDatabase`` ，会认证 ``-u`` 和 ``-p`` 参数指定的账户和密码。如果没有指定验证数据库， ``mongo`` 使用连接字符串中指定的 ``DB`` 作为验证数据块。

基于角色的访问控制（Role-Based Access Control）
==============================================
角色是授予 ``User`` 在指定资源上执行指定操作的权限， MongoDB官方手册对角色的定义是：

A role grants privileges to perform the specified actions on resource.

MongoDB 为了方便管理员管理权限，在 DB 级别上预先定义了内置角色；如果用户需要对权限进行更为细致的管理， MongoDB 允许用户创建自定义的角色，能够在集合级别上控制 User 能够执行的操作。

MongoDB 使用角色( ``Role`` )授予 ``User`` 访问资源的权限， ``Role`` 决定 ``User`` 能够访问的数据库资源和执行的操作。一个 ``User`` 能够被授予一个或多个 ``Role`` ，如果 ``User`` 没有被授予 ``Role`` ，那么就没有访问 MongoDB 系统的权限。

内置角色（Built-In Roles）
-------------------------

内置角色是 MongoDB 预定义的角色，操作的资源是在 DB 级别上。 MongoDB 拥有一个 SuperUser 的角色： ``root`` ，拥有最大权限，能够在系统的所有资源上执行任意操作。

数据库用户角色（Database User Roles）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

- read：授予 User 只读数据的权限
- readWrite：授予 User 读写数据的权限

数据库管理角色(Database Administration Roles)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

- dbAdmin：在当前 DB 中执行管理操作
- dbOwner：在当前 DB 中执行任意操作
- userAdmin：在当前 DB 中管理 User

备份和还原角色（Backup and Restoration Roles）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

- backup
- restore

跨库角色（All-Database Roles）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

- readAnyDatabase：授予在所有数据库上读取数据的权限
- readWriteAnyDatabase：授予在所有数据库上读写数据的权限
- userAdminAnyDatabase：授予在所有数据库上管理User的权限
- dbAdminAnyDatabase：授予管理所有数据库的权限

集群管理角色（Cluster Administration Roles）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

- clusterAdmin：授予管理集群的最高权限
- clusterManager：授予管理和监控集群的权限，A user with this role can access the config and local databases, which are used in sharding and replication, respectively.
- clusterMonitor：授予监控集群的权限，对监控工具具有 readonly 的权限
- hostManager：管理Server

用户自定义的角色（User-Defined Roles）
-------------------------------------
内置角色只能控制 ``User`` 在 ``DB`` 级别上执行的操作，管理员可以创建自定义角色，控制用户在集合级别( Collection-Level )上执行的操作，即，控制 ``User`` 在当前 ``DB`` 的特定集合上执行特定的操作。

在创建角色时，必须明确 ``Role`` 的四个特性：

- ``Scope`` ：角色作用的范围，创建在 ``Admin`` 数据库中的角色，能够在其他 ``DB`` 中使用；在其他 ``DB`` 中创建的角色，只能在当前 ``DB`` 中使用；
- ``Resource`` ：角色控制的资源，表示授予在该资源上执行特定操作的权限；
- ``Privilege Actions`` ：定义了 ``User`` 能够在资源上执行的操作，系统定义 ``Action`` 是： ``Privilege Actions`` ；
- ``Inherit`` ：角色能够继承其他角色权限；

角色作用的范围（Scope）
^^^^^^^^^^^^^^^^^^^^^^
在 ``admin`` 数据库中创建的角色， ``Scope`` 是全局的，能够在 ``admin`` ，其他 ``DB`` 和集群中使用，并且能够继承其他 ``DB`` 的 ``Role`` ；而在非 ``admin`` 中创建的角色， ``Scope`` 是当前数据库，只能在当前 ``DB`` 中使用，只能继承当前数据库的角色。

权限的操作（Privilege actions）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
MongoDB 的权限包由：资源( ``Resource`` )和操作( ``Action`` )两部分组成，Privilege Actions 定义 User 能够在资源上执行的操作，例如： MongoDB 在文档级别( ``Document-Level`` )上执行的读写操作（Query and Write Actions）列表是：

- find；
- insert；
- remove；
- update；

创建角色
^^^^^^^^
使用 ``db.CreateRole()`` 在当前 ``DB`` 中创建角色，创建的语法示例如下：

.. code-block:: shell

    use admin
    db.createRole(
       {
         role: "new_role",
         privileges: [
           { resource: { cluster: true }, actions: [ "addShard" ] },
           { resource: { db: "config", collection: "" }, actions: [ "find", "update", "insert", "remove" ] },
           { resource: { db: "users", collection: "usersCollection" }, actions: [ "update", "insert", "remove" ] },
           { resource: { db: "", collection: "" }, actions: [ "find" ] }
         ],
         roles: [
           { role: "read", db: "admin" }
         ]
       },
       { w: "majority" , wtimeout: 5000 }
    )

在 ``roles`` 数组中，指定被继承的 ``role`` ，即，新建的 ``new_role`` 从 ``roles`` 数组中继承权限：

- 如果被继承的 ``role`` 在当前 ``DB`` 中，定义的格式是： ``roles:["role"]`` ；
- 如果被继承的 ``role`` 不在当前 ``DB`` 中，需要使用 ``doc`` ，指定该 ``role`` 所在的 ``DB`` ，定义的格式是： ``roles:[{role:"role_name", db:"db_name"}]`` ；

自定义角色管理函数
^^^^^^^^^^^^^^^^^^

- db.createRole() ：Creates a role and specifies its privileges.
- db.updateRole() ：Updates a user-defined role.
- db.dropRole() ：Deletes a user-defined role.
- db.dropAllRoles() ：Deletes all user-defined roles associated with a database.
- db.grantPrivilegesToRole() ：Assigns privileges to a user-defined role.
- db.revokePrivilegesFromRole() ：Removes the specified privileges from a user-defined role.
- db.grantRolesToRole() ：Specifies roles from which a user-defined role inherits privileges.
- db.revokeRolesFromRole() ：Removes inherited roles from a role.
- db.getRole() ：Returns information for the specified role.
- db.getRoles() ：Returns information for all the user-defined roles in a database.

管理用户和权限
==============

创建用户
--------

.. code-block:: shell

    use db_name
    db.createUser(
    {
        user: "user_name",
        pwd: "user_pwd",
        roles: [   { role: "clusterAdmin", db: "admin" },
                   { role: "readAnyDatabase", db: "admin" },
                   "readWrite"
               ]
    }
    )

为新建的 ``User`` ，授予一个或多个角色，通过 ``roles`` 数组来实现：

- 如果 ``role`` 存在于当前 ``DB`` 中， ``roles`` 的格式： ``roles:["role"]`` ；
- 如果 ``role`` 不存在于当前 ``DB`` 中， ``roles`` 的格式： ``roles:[Role:"role_name", db:"db_name"]`` ；

权限认证（Authenticate）
-----------------------
``mongo`` 连接到 ``mongod`` ，有两种权限认证的方式：

- 在连接时认证用户访问的权限， ``mongo`` 使用参数 ``--authenticationDatabase <dbname>`` 指定认证数据库；
- 在连接后，认证用户访问的权限， ``mongo`` 没有使用参数 ``--authenticationDatabase <dbname>`` ，在连接到 ``mongod`` 之后，切换到验证数据库（ ``authentication database`` ）中，使用 ``db.auth()`` 验证 ``User`` 是否有权限访问当前数据库；

.. code-block:: shell

    use db_name
    db.auth("user_name", "user_pwd" )

用户管理函数
------------

- db.auth() ：Authenticates a user to a database.
- db.createUser() ：Creates a new user.
- db.updateUser() ：Updates user data.
- db.changeUserPassword() ：Changes an existing user’s password.
- db.dropAllUsers() ：Deletes all users associated with a database.
- db.dropUser() ：Removes a single user.
- db.grantRolesToUser() ：Grants a role and its privileges to a user.
- db.revokeRolesFromUser() ：Removes a role from a user.
- db.getUser() ：Returns information about the specified user.
- db.getUsers() ：Returns information about all users associated with a database.

Mongodb 忘记密码
================

.. code-block:: shell

    vim /etc/mongodb.conf          # 修改 mongodb 配置，将 auth = true 注释掉，或者改成 false
    service mongodb restart        # 重启 mongodb 服务

    mongo                          # 运行客户端（也可以去mongodb安装目录下运行这个）
    use admin                      # 切换到系统帐户表
    db.system.users.find()         # 查看当前帐户（密码有加密过）
    db.system.users.remove({})     # 删除所有帐户
    db.createUser({user:"root",pwd:"root",roles:["root"]}) #增加用户账户

    db.createUser(
      {
        user: "admin",
        pwd: "password",
        roles: [{role: "userAdminAnyDatabase", db: "admin"}]
      }
    )

    vim /etc/mongodb.conf          # 恢复 auth = true
    service mongodb restart        # 重启 mongodb 服务

