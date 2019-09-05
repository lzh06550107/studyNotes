******
JSON字段
******

本章内容为 ``V5.1.4+`` 版本开始支持，可以更为方便的操作模型的 ``JSON`` 数据字段。

.. note:: 这里指的 ``JSON`` 数据包括 ``JSON`` 类型以及 ``JSON`` 格式（但并不是 ``JSON`` 类型字段）的数据，?? 难道不需要数据库字段为JSON类型？


我们修改下 ``User`` 模型类

.. code-block:: php

	<?php
	namespace app\index\model;

	use think\Model;
	class User extends Model
	{
		// 设置json类型字段
		protected $json = ['info'];
	}

定义后，可以进行如下 ``JSON`` 数据操作。

写入JSON数据
============
使用数组方式写入 ``JSON`` 数据：

.. code-block:: php

	$user = new User;
	$user->name = 'thinkphp';
	$user->info = [
		'email'    => 'thinkphp@qq.com',
	    'nickname '=> '流年',
	];
	$user->save();

使用对象方式写入 ``JSON`` 数据

.. code-block:: php

	$user = new User;
	$user->name = 'thinkphp';
	$info = new \StdClass();
	$info->email = 'thinkphp@qq.com';
	$info->nickname = '流年';
	$user->info = $info;
	$user->save();


查询JSON数据
============

.. code-block:: php

	$user = User::get(1);
	echo $user->name; // thinkphp
	echo $user->info->email; // thinkphp@qq.com
	echo $user->info->nickname; // 流年

查询条件为 ``JSON`` 数据

.. code-block:: php

	$user = User::where('info->nickname','流年')->find();
	echo $user->name; // thinkphp
	echo $user->info->email; // thinkphp@qq.com
	echo $user->info->nickname; // 流年

如果你需要查询的 ``JSON`` 属性是整型类型的话，可以在模型类里面定义 ``JSON`` 字段的属性类型，就会自动进行相应类型的参数绑定查询。

.. code-block:: php

	<?php
	namespace app\index\model;

	use think\Model;

	class User extends Model
	{
		// 设置json类型字段
		protected $json = ['info'];
	    
	    // 设置JSON字段的类型
	    protected $jsonType = [
	    	'info->user_id'	=>	'int'
	    ];
	}

没有定义类型的属性默认为字符串类型，因此字符串类型的属性可以无需定义。

``V5.1.17+`` 版本开始，可以设置模型的 ``JSON`` 数据返回数组，只需要在模型设置 ``jsonAssoc`` 属性为 ``true`` 。

.. code-block:: php

	<?php
	namespace app\index\model;

	use think\Model;

	class User extends Model
	{
		// 设置json类型字段
		protected $json = ['info'];
	    
	    // 设置JSON数据返回数组
	    protected $jsonAssoc = true;
	}

设置后，查询代码调整为：

.. code-block:: php

	$user = User::get(1);
	echo $user->name; // thinkphp
	echo $user->info['email']; // thinkphp@qq.com
	echo $user->info['nickname']; // 流年

更新JSON数据
============

.. code-block:: php

	$user = User::get(1);
	$user->name = 'kancloud';
	$user->info->email = 'kancloud@qq.com';
	$user->info->nickname = 'kancloud';
	$user->save();

如果在 ``V5.1.17+`` 版本以后，并且设置模型的 ``JSON`` 数据返回数组，那么更新操作需要调整如下。

.. code-block:: php

	$user = User::get(1);
	$user->name = 'kancloud';
	$info['email'] = 'kancloud@qq.com';
	$info['nickname'] = 'kancloud';
	$user->info = $info;
	$user->save();


