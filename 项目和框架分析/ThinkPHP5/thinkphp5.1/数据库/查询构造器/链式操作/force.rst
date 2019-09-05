*****
force
*****

``force`` 方法用于数据集的强制索引操作，例如：

.. code-block:: php

    Db::table('think_user')->force('user')->select();

对查询强制使用 ``user`` 索引， ``user`` 必须是数据表实际创建的索引名称。