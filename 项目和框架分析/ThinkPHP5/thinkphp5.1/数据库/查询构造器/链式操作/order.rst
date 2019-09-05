*****
order
*****

``order`` 方法用于对操作的结果排序或者优先级限制。

用法如下：

.. code-block:: php

	Db::table('think_user')
	->where('status', 1)
	->order('id', 'desc')
	->limit(5)
	->select();

.. code-block:: shell

     SELECT * FROM `think_user` WHERE `status` = 1 ORDER BY `id` desc LIMIT 5


.. note:: 如果没有指定 ``desc`` 或者 ``asc`` 排序规则的话，默认为 ``asc`` 。


支持使用数组对多个字段的排序，例如：

.. code-block:: php

	Db::table('think_user')
	->where('status', 1)
	->order(['order','id'=>'desc'])
	->limit(5)
	->select(); 

最终的查询 ``SQL`` 可能是

.. code-block:: shell

    SELECT * FROM `think_user` WHERE `status` = 1 ORDER BY `order`,`id` desc LIMIT 5

对于更新数据或者删除数据的时候可以用于优先级限制

.. code-block:: php

	Db::table('think_user')
	->where('status', 1)
	->order('id', 'desc')
	->limit(5)
	->delete(); 

生成的 ``SQL``

.. code-block:: shell

    DELETE FROM `think_user` WHERE `status` = 1 ORDER BY `id` desc LIMIT 5

在 ``V5.1.7+`` 版本开始，如果你需要在 ``order`` 方法中使用 ``mysql`` 函数的话，必须使用下面的方式：

.. code-block:: php

	Db::table('think_user')
	->where('status', 1)
	->orderRaw("field(name,'thinkphp','onethink','kancloud')")
	->limit(5)
	->select();


