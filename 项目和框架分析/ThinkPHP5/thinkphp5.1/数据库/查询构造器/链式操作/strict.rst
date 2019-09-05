******
strict
******

``strict`` 方法用于设置是否严格检查字段名，用法如下：

.. code-block:: php

	// 关闭字段严格检查
	Db::name('user')
	    ->strict(false)
	    ->insert($data);

.. note:: 注意，系统默认值是由数据库配置参数 ``fields_strict`` 决定，因此修改数据库配置参数可以进行全局的严格检查配置，如下：

.. code-block:: php

	// 关闭严格检查字段是否存在
	'fields_strict'  => false,

.. note:: 如果开启字段严格检查的话，在更新和写入数据库的时候，一旦存在非数据表字段的值，则会抛出异常。


