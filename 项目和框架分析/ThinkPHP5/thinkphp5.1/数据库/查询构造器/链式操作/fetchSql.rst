********
fetchSql
********

``fetchSql`` 用于直接返回 ``SQL`` 而不是执行查询，适用于任何的 ``CURD`` 操作方法。 例如：

.. code-block:: php

	echo Db::table('think_user')->fetchSql(true)->find(1);

输出结果为：

.. code-block:: shell

    SELECT * FROM think_user where `id` = 1
