******
流程控制语句
******

条件控制语句
======
不论是PHP还是别的语法，程序总是由若干条语句组成。

从执行方式上看，语句的控制结构分为以下三种：

- 顺序结构：从第一条语句到最后一条语句完全顺序执行；
- 选择结构：根据用户输入或语句的中间结果去执行若干任务；
- 循环结构：根据某条条件重复地执行某项任务若干次，或直到达成目标即可。


PHP中 有三种控制语句用以实现选择结构与循环结构：

- 条件控制语句：if、else、elseif和switch；
- 循环控制语句：foreach、while、do while和for；
- 转移控制语句：break、continue和return。

if语句
----

.. code-block:: php

    <?php
	if（condition）{
	    Statement1；
	}
    ?>

if...else语句
-----------


.. code-block:: php

    <?php
	if（condition）{
	    Statement1；
	}
	else {
	  　Statement2；
	}
    ?>

elseif语句
--------


.. code-block:: php

    <?php
	if（condition1）{
	    Statement1；
	}
	elseif(condition2) {
	    Statement2；
	}
	else
	    Statement3;
    ?>

switch...case多重判断语句
-------------------
虽然elseif语句 可以进行多重选择，但使用时时分繁琐。为了避免if语句过于冗长，提高程序的可读性，可以使用switch多重判断语句。

.. code-block:: php

    <?php
	Switch(variable)
	{
	　Case val1:
		Statement1;
		Break;
	　Case val2:
		Statement2;
		Break;
	　Default:
		Statement3;
	　}
    ?>

和if语句不通的是，switch语句后面的控制表达式的数据类型只能是整型或是字符串，不能是boolean型。虽然PHP是弱类型语言，在switch后面控制表达式的变量可以是任意类型数据，但为了保证匹配执行的准确性，最好只使用 **整型或是字符串** 中的一种类型

循环控制语句
======

while循环语句
---------

.. code-block:: php

    <?php
	while(condition){
		Statement;
	}
    ?>

do...while循环语句
--------------

.. code-block:: php

    <?php
	do {
		Statements;
	}while(condition)
    ?>

for循环语句
-------

.. code-block:: php

    <?php
	for(初始化；条件表达式；增量) {
		Statement;
	}
    ?>

foreach循环语句
-----------
foreach循环是PHP4引进来的，只能用于数组。在PHP5中，又增加了对对象的支持。该语句的语法格式为：

.. code-block:: php

    <?php
	foreach(array_expression as $value){
		Statement;
	}
	foreach(array_expression as $key => $value){
		Statement;
	}
    ?>

foreach循环语句将遍历数组array_expression。每次循环时，将当前数组中的值赋给$value(或$key和$value)，同时，数组指针向后移动直到遍历结束。当使用foreach循环语句时，数组指针自动被重置，所以不需要手动设置指针位置。

.. note:: 当试图使用foreach语句用于其它数据类型或者未初始化的变量时会产生错误。为了避免这个问题，最好使用is_array()函数先来判断变量是否为数组类型。如果是，再进行其他操作。

.. code-block:: php

	<?php
		//Traversable 重要的一个用处就是判断一个类是否可以遍历
	    if( !is_array( $items ) && !$items instanceof Traversable )
	        //Throw exception here

    	if ( !is_iterable( $items )) // PHP7代替方法
        	//Throw exception here
	?>


流程控制的另一种书写格式
------------
在一个复杂的PHP页面中，可能包含多个条件控制语句、循环控制语句和函数，仅查找匹配的大括号“{}”就非常麻烦。为此，PHP提供了另一种书写格式，包括if、while、for、foreach和switch都可以使用。书写格式的基本形式是：使用冒号”:”来代替左边的大括号“{”；使用endif;、endwhile、endfor、endforeach;和endswitch;来的代替右边的大括号“}”。

.. code-block:: php

    <?php
	$ss=2;
	$max=1000;
	$arr=array();
	echo $max."以内的素数为：";
	while($ss < $max):
	     $boo=false;
	     foreach($arr as $value):
	          if($ss % $value ==0):
	               $boo=true;
	               break;
	          endif;
	     endforeach;
	     if(!$boo):
	          echo $ss." ";
	          $arr[count($arr)]=$ss;
	     endif;
	     $ss++;
	endwhile;
    ?>

使用break/continue语句跳出循环
----------------------

break
^^^^^
break关键字可以终止当前的循环，包括while、do…while、for、foreach和switch 在内的所有控制语句。 
break语句不仅可以跳出当前的循环，还可以指定跳出几层循环，格式为：

``break $num;``

参数$num 指定要跳出几层循环。

continue
^^^^^^^^
continue关键字的作用没有break强大，continue只能终止本次循环而进入到下一次循环中，continue也可以指定跳出几重循环。