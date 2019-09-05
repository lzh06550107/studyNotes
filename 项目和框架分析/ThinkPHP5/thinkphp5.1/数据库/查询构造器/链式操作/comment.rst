*******
comment
*******

``COMMENT`` 方法 用于在生成的 ``SQL`` 语句中添加注释内容，例如：

.. code-block:: php

	Db::table('think_score')->comment('查询考试前十名分数')
	    ->field('username,score')
	    ->limit(10)
	    ->order('score desc')
	    ->select();

最终生成的 ``SQL`` 语句是：

.. code-block:: shell

    SELECT username,score FROM think_score ORDER BY score desc LIMIT 10 /* 查询考试前十名分数 */



