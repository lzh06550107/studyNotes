*************
failException
*************

``failException`` 设置查询数据为空时是否需要抛出异常，如果不传入任何参数，默认为开启，用于 ``select`` 和 ``find`` 方法，例如：

.. code-block:: php

	// 数据不存在的话直接抛出异常
	Db::name('blog')
		->where('status',1)
	    ->failException()
	    ->select();
	// 数据不存在返回空数组 不抛异常
	Db::name('blog')
		->where('status',1)
	    ->failException(false)
	    ->select();

或者可以使用更方便的查空报错

.. code-block:: php

	// 查询多条
	Db::name('blog')
		->where('status', 1)
	    ->selectOrFail();

	// 查询单条
	Db::name('blog')
		->where('status', 1)
	    ->findOrFail();








