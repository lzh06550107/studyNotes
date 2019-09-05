*****
group
*****

``GROUP`` 方法通常用于结合合计函数，根据一个或多个列对结果集进行分组 。

``group`` 方法只有一个参数，并且只能使用字符串。

例如，我们都查询结果按照用户 ``id`` 进行分组统计：

.. code-block:: php

	Db::table('think_user')
	    ->field('user_id,username,max(score)')
	    ->group('user_id')
	    ->select();


生成的 ``SQL`` 语句是：

.. code-block:: shell

	SELECT user_id,username,max(score) FROM think_score GROUP BY user_id

也支持对多个字段进行分组，例如：


.. code-block:: php

	Db::table('think_user')
	    ->field('user_id,test_time,username,max(score)')
	    ->group('user_id,test_time')
	    ->select();

生成的 ``SQL`` 语句是：

.. code-block:: shell

    SELECT user_id,test_time,username,max(score) FROM think_user GROUP BY user_id,test_time



















