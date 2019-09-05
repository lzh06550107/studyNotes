********
sequence
********

``sequence`` 方法用于 ``pgsql`` 数据库指定自增序列名，其它数据库不必使用，用法为：

.. code-block:: php

	Db::name('user')
	->sequence('user_id_seq')
	->insert(['name'=>'thinkphp']);

