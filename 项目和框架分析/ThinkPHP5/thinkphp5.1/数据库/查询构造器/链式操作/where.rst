*****
where
*****

``where`` 方法的用法是 ``ThinkPHP`` 查询语言的精髓，也是 ``ThinkPHP ORM`` 的重要组成部分和亮点所在，可以完成包括普通查询、表达式查询、快捷查询、区间查询、组合查询在内的查询操作。 ``where`` 方法的参数支持的变量类型包括字符串、数组和闭包。

.. note:: 和 ``where`` 方法相同用法的方法还包括 ``whereOr`` 、 ``whereIn`` 等一系列快捷查询方法，下面仅以 ``where`` 为例说明用法。

表达式查询
==========

.. note:: 表达式查询是官方推荐使用的查询方式

查询表达式的使用格式：

.. code-block:: php

	Db::table('think_user')
	    ->where('id','>',1)
	    ->where('name','thinkphp')
	    ->select(); 

更多的表达式查询语法，可以参考前面的查询表达式部分。


数组条件
========
数组方式有两种查询条件类型：关联数组和索引数组。

关联数组
--------
主要用于等值 ``AND`` 条件，例如：

.. code-block:: php

	// 传入数组作为查询条件
	Db::table('think_user')->where([
		'name'	=>	'thinkphp',
	    'status'=>	1
	])->select(); 

最后生成的 ``SQL`` 语句是

.. code-block:: shell

    SELECT * FROM think_user WHERE `name`='thinkphp' AND status = 1

``V5.1.13+`` 版本开始增加了关联数组的 ``IN`` 查询支持，例如可以使用：

.. code-block:: php

	// 传入数组作为查询条件
	Db::table('think_user')->where([
		'name'	=>	'thinkphp',
	    'status'=>	[1, 2]
	])->select(); 

最后生成的 ``SQL`` 语句是

.. code-block:: shell

    SELECT * FROM think_user WHERE `name`='thinkphp' AND status IN (1, 2)

索引数组
--------
索引数组方式批量设置查询条件，使用方式如下：

.. code-block:: php

	// 传入数组作为查询条件
	Db::table('think_user')->where([
		['name','=','thinkphp'],
	    ['status','=',1]
	])->select(); 

最后生成的 ``SQL`` 语句是

.. code-block:: shell

    SELECT * FROM think_user WHERE `name`='thinkphp' AND status = 1

.. note:: 5.1的数组查询方式有所调整，是为了尽量避免数组方式的条件查询注入。如何确保不会注入？？？

如果需要事先组装数组查询条件，可以使用：

.. code-block:: php

	$map[] = ['name','like','think'];
	$map[] = ['status','=',1];

.. note:: 数组方式查询还有一些额外的复杂用法，我们会在后面的高级查询章节提及。

字符串条件
=========
使用字符串条件直接查询和操作，例如：

.. code-block:: php

    Db::table('think_user')->where('type=1 AND status=1')->select(); 

最后生成的 ``SQL`` 语句是

.. code-block:: shell

    SELECT * FROM think_user WHERE type=1 AND status=1

.. note:: 注意使用字符串查询条件和表达式查询的一个区别在于，不会对查询字段进行避免关键词冲突处理。

使用字符串条件的时候，如果需要传入变量，建议配合预处理机制，确保更加安全，例如：

.. code-block:: php

	Db::table('think_user')
	->where("id=:id and username=:name", ['id' => [1, \PDO::PARAM_INT] , 'name' => 'thinkphp'])
	->select();

或者使用

.. code-block:: php

	Db::table('think_user')
	->where("id=:id and username=:name")
	->bind(['id' => [1, \PDO::PARAM_INT] , 'name' => 'thinkphp'])
	->select();

在 ``V5.1.7+`` 版本以后，你可以使用更安全的

.. code-block:: php

	Db::table('think_user')
	->whereRaw("id=:id and username=:name", ['id' => [1, \PDO::PARAM_INT] , 'name' => 'thinkphp'])
	->select();
