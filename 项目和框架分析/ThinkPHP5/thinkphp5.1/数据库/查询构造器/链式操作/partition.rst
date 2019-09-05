*********
partition
*********

``partition`` 方法用于是数据库水平分表

.. code-block:: php

	partition($data, $field, $rule);
	// $data 分表字段的数据
	// $field 分表字段的名称
	// $rule 分表规则

.. note:: 注意：不要使用任何 ``SQL`` 语句中会出现的关键字当表名、字段名，例如 ``order`` 等。会导致数据模型拼装 ``SQL`` 语句语法错误。

``partition`` 方法用法如下：

.. code-block:: php

	// 用于写入
	$data = [
	    'user_id'   => 110,
	    'user_name' => 'think'
	];

	$rule = [
	    'type' => 'mod', // 分表方式
	    'num'  => 10     // 分表数量
	];

	Db::name('log')
	    ->partition(['user_id' => 110], "user_id", $rule)
	    ->insert($data);
	    
	// 用于查询
	Db::name('log')
	    ->partition(['user_id' => 110], "user_id", $rule)
	    ->where(['user_id' => 110])
	    ->select();

.. note:: 如果你的分表后缀是从 0 开始，而不是 1 的话，需要自定义分表类型，并自己写一个自定义函数

此时你的规则应该是：

.. code-block:: php

	$rule = [
	    'type' => 'myMod', // 自定义分表方式，应该是一个函数
	    'num'  => 10     // 分表数量
	];

同时在 ``common.php`` 中需要定义这个函数

.. code-block:: php

	/**
	 * 自定义分表规则
	 *
	 * @param int $num
	 * @return string
	 */
	function myMod($num)
	{
	    return chr($num % 10 - 1);
	}

.. note:: 注意，两边的规则参数，在这里是 10 是需要对应的，而不能只写在 ``rule`` 中。



