*********
SQL语句执行顺序
*********

.. image:: ./images/sql语句执行图.png

.. note:: 在mysql中，group by中可以使用别名；where中不能使用别名；order by中可以使用别名。其余像oracle，hive中别名的使用都是严格遵循sql执行顺序的，groupby后面不能用别名。mysql特殊是因为mysql中对查询做了加强。