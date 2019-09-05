*****
table
*****

``table`` 方法主要用于指定操作的数据表。

用法
====

一般情况下，操作模型的时候系统能够自动识别当前对应的数据表，所以，使用 ``table`` 方法的情况通常是为了：

- 切换操作的数据表；
- 对多表进行操作；

例如：

.. code-block:: php

	Db::table('think_user')->where('status>1')->select();

也可以在 ``table`` 方法中指定数据库，例如：

.. code-block:: php

    Db::table('db_name.think_user')->where('status>1')->select();

``table`` 方法指定的数据表需要完整的表名，但可以采用 ``name`` 方式简化数据表前缀的传入，例如：

.. code-block:: php

    Db::name('user')->where('status>1')->select();

会自动获取当前模型对应的数据表前缀来生成 ``think_user`` 数据表名称。

.. note:: 需要注意的是 ``table`` 方法不会改变数据库的连接，所以你要确保当前连接的用户有权限操作相应的数据库和数据表。

如果需要对多表进行操作，可以这样使用：

.. code-block:: php

	Db::field('user.name,role.title')
	->table('think_user user,think_role role')
	->limit(10)->select();

为了尽量避免和 ``mysql`` 的关键字冲突，可以建议使用数组方式定义(为何数组定义就会使用反引号包含字段)，例如：

.. code-block:: php

	Db::field('user.name,role.title')
	->table(['think_user'=>'user','think_role'=>'role'])
	->limit(10)->select();

使用数组方式定义的优势是可以避免因为表名和关键字冲突而出错的情况。



