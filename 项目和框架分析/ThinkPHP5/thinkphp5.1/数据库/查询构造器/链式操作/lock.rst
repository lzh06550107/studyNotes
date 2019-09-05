****
lock
****

``Lock`` 方法是用于数据库的锁机制，如果在查询或者执行操作的时候使用：

.. code-block:: php

    Db::name('user')->where('id',1)->lock(true)->find();

就会自动在生成的 ``SQL`` 语句最后加上 ``FOR UPDATE`` 或者 ``FOR UPDATE NOWAIT`` （Oracle数据库）。

``lock`` 方法支持传入字符串用于一些特殊的锁定要求，例如：

.. code-block:: php

    Db::name('user')->where('id',1)->lock('lock in share mode')->find();

