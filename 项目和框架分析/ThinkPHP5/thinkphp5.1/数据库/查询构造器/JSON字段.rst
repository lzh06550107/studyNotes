******
JSON字段
******


JSON字段
========

从 ``V5.1.4+`` 版本开始，强化了 ``JSON`` 字段的操作支持。

如果你的 ``user`` 表有一个 ``info`` 字段是 ``JSON`` 类型的（或者说你存储的是 ``JSON`` 格式，但并非是要 ``JSON`` 字段类型），你可以使用下面的方式操作数据。


JSON数据写入
------------

.. code-block:: php

	$user['name'] = 'thinkphp';
	$user['info'] = [
		'email'    => 'thinkphp@qq.com',
		'nickname' => '流年',
	];
	Db::name('user')
		->json(['info'])
		->insert($user);



JSON数据查询
------------
查询整个 ``JSON`` 数据
^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

	$user = Db::name('user')
		->json(['info'])
		->find(1);
	dump($user);    

查询条件为JSON数据
^^^^^^^^^^^^^^^^^^

.. code-block:: php

	$user = Db::name('user')
		->json(['info'])
	    ->where('info->nickname','ThinkPHP')
		->find();
	dump($user);    

由于 ``JSON`` 字段的属性类型并不会自动获取，所以，如果是整型数据查询的话，可以设置 ``JSON`` 字段类型，例如：

.. code-block:: php

	$user = Db::name('user')
		->json(['info'])
	    ->where('info->user_id', 10)
		->setJsonFieldType(['info->user_id' => 'int'])
		->find();
	dump($user);    

JSON数据更新
------------

完整 ``JSON`` 数据更新
^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

	$data['info'] = [
		'email'    => 'kancloud@qq.com',
		'nickname' => 'kancloud',
	];
	Db::name('user')
		->json(['info'])
	    ->where('id',1)
		->update($data);

单个JSON数据更新
^^^^^^^^^^^^^^^^

.. code-block:: php

	$data['info->nickname'] = 'ThinkPHP';
	Db::name('user')
		->json(['info'])
	    ->where('id',1)
		->update($data);








