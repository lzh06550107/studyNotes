*****
cache
*****

``cache`` 方法用于查询缓存操作，也是连贯操作方法之一。

``cache`` 可以用于 ``select`` 、 ``find`` 、 ``value`` 和 ``column`` 方法，以及其衍生方法，使用 ``cache`` 方法后，在缓存有效期之内不会再次进行数据库查询操作，而是直接获取缓存中的数据，关于数据缓存的类型和设置可以参考缓存部分。

下面举例说明，例如，我们对 find 方法使用 ``cache`` 方法如下：

.. code-block:: php

    Db::table('think_user')->where('id',5)->cache(true)->find();

第一次查询结果会被缓存，第二次查询相同的数据的时候就会直接返回缓存中的内容，而不需要再次进行数据库查询操作。

默认情况下， 缓存有效期是由默认的缓存配置参数决定的，但 ``cache`` 方法可以单独指定，例如：


.. code-block:: php

	Db::table('think_user')->cache(true,60)->find();
	// 或者使用下面的方式 是等效的
	Db::table('think_user')->cache(60)->find();

表示对查询结果的缓存有效期 ``60`` 秒。

``cache`` 方法可以指定缓存标识：

.. code-block:: php

    Db::table('think_user')->cache('key',60)->find();

.. note:: 指定查询缓存的标识可以使得查询缓存更有效率。

这样，在外部就可以通过 ``\think\Cache`` 类直接获取查询缓存的数据，例如：

.. code-block:: php

	$result = Db::table('think_user')->cache('key',60)->find();
	$data = \think\Cache::get('key');

``cache`` 方法支持设置缓存标签，例如：

.. code-block:: php

    Db::table('think_user')->cache('key',60,'tagName')->find();

缓存自动更新
===========
这里的缓存自动更新是指一旦数据更新或者删除后会自动清理缓存（下次获取的时候会自动重新缓存）。

当你删除或者更新数据的时候，可以调用相同 ``key`` 的 ``cache`` 方法，会自动更新（清除）缓存，例如：

.. code-block:: php

	Db::table('think_user')->cache('user_data')->select([1,3,5]);
	Db::table('think_user')->cache('user_data')->update(['id'=>1,'name'=>'thinkphp']);
	Db::table('think_user')->cache('user_data')->select([1,5]);

最后查询的数据不会受第一条查询缓存的影响，确保查询和更新或者删除使用相同的缓存标识才能自动清除缓存。

如果使用 ``find`` 方法并且使用主键查询的情况，不需要指定缓存标识，会自动清理缓存，例如：

.. code-block:: php

	Db::table('think_user')->cache(true)->find(1);
	Db::table('think_user')->update(['id'=>1,'name'=>'thinkphp']);
	Db::table('think_user')->cache(true)->find(1);

最后查询的数据会是更新后的数据。









