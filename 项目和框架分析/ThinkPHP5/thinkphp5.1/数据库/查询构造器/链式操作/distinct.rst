********
distinct
********

``DISTINCT`` 方法用于返回唯一不同的值 。

例如数据库表中有以下数据

以下代码会返回 ``user_login`` 字段不同的数据

.. code-block:: php

    Db::table('think_user')->distinct(true)->field('user_login')->select();

生成的 ``SQL`` 语句是：  ``SELECT DISTINCT user_login FROM think_user``

返回以下数组

.. code-block:: shell

	array(2) {
	  [0] => array(1) {
	    ["user_login"] => string(7) "chunice"
	  }
	  [1] => array(1) {
	    ["user_login"] => string(5) "admin"
	  }
	}

``distinct`` 方法的参数是一个布尔值。

