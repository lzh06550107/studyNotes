****
join
****

``JOIN`` 方法用于根据两个或多个表中的列之间的关系，从这些表中查询数据。 ``join`` 通常有下面几种类型，不同类型的 ``join`` 操作会影响返回的数据结果。

- ``INNER JOIN`` : 等同于 ``JOIN`` （默认的 ``JOIN`` 类型）,如果表中有至少一个匹配，则返回行；
- ``LEFT JOIN`` : 即使右表中没有匹配，也从左表返回所有的行；
- ``RIGHT JOIN`` : 即使左表中没有匹配，也从右表返回所有的行；
- ``FULL JOIN`` : 只要其中一个表中存在匹配，就返回行；

函数
====

.. code-block:: php

	join ( mixed join [, mixed $condition = null [, string $type = 'INNER']] )
	leftJoin ( mixed join [, mixed $condition = null ] )
	rightJoin ( mixed join [, mixed $condition = null ] )
	fullJoin ( mixed join [, mixed $condition = null ] )

参数
----

join
^^^^^
要关联的（完整）表名以及别名

支持的写法：

- 写法1：[ '完整表名或者子查询'=>'别名' ]
- 写法2：'不带数据表前缀的表名'（自动作为别名）
- 写法2：'不带数据表前缀的表名 别名'

condition
^^^^^^^^^
关联条件。可以为字符串或数组， 为数组时每一个元素都是一个关联条件。

type
^^^^^
关联类型。可以为: INNER 、 LEFT 、 RIGHT 、 FULL ，不区分大小写，默认为 INNER 。

返回值
------
模型对象，？？

举例
====

.. code-block:: php

	Db::table('think_artist')
	->alias('a')
	->join('work w','a.id = w.artist_id')
	->join('card c','a.card_id = c.id')
	->select();


	Db::table('think_user')
	->alias('a')
	->join(['think_work'=>'w'],'a.id=w.artist_id')
	->join(['think_card'=>'c'],'a.card_id=c.id')
	->select();

默认采用 ``INNER JOIN`` 方式，如果需要用其他的 ``JOIN`` 方式，可以改成

.. code-block:: php

	Db::table('think_user')
	->alias('a')
	->leftJoin('word w','a.id = w.artist_id')
	->select();

表名也可以是一个子查询

.. code-block:: php

	$subsql = Db::table('think_work')
	->where('status',1)
	->field('artist_id,count(id) count')
	->group('artist_id')
	->buildSql();

	Db::table('think_user')
	->alias('a')
	->join([$subsql=> 'w'], 'a.artist_id = w.artist_id')
	->select();































