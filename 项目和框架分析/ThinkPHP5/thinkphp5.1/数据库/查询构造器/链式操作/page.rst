****
page
****

``page`` 方法主要用于分页查询。

我们在前面已经了解了关于 ``limit`` 方法用于分页查询的情况，而 ``page`` 方法则是更人性化的进行分页查询的方法，例如还是以文章列表分页为例来说，如果使用 ``limit`` 方法，我们要查询第一页和第二页（假设我们每页输出10条数据）写法如下：

.. code-block:: php

	// 查询第一页数据
	Db::table('think_article')->limit(0,10)->select(); 
	// 查询第二页数据
	Db::table('think_article')->limit(10,10)->select(); 

虽然利用扩展类库中的分页类 ``Page`` 可以自动计算出每个分页的 ``limit`` 参数，但是如果要自己写就比较费力了，如果用 ``page`` 方法来写则简单多了，例如：

.. code-block:: php

	// 查询第一页数据
	Db::table('think_article')->page(1,10)->select(); 
	// 查询第二页数据
	Db::table('think_article')->page(2,10)->select(); 

显而易见的是，使用 ``page`` 方法你不需要计算每个分页数据的起始位置， ``page`` 方法内部会自动计算。

``page`` 方法还可以和 ``limit`` 方法配合使用，例如：

.. code-block:: php

    Db::table('think_article')->limit(25)->page(3)->select();

当 ``page`` 方法只有一个值传入的时候，表示第几页，而 ``limit`` 方法则用于设置每页显示的数量，也就是说上面的写法等同于：

.. code-block:: php

    Db::table('think_article')->page(3,25)->select(); 











