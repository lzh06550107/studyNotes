****
闭包
****

PHP回调函数与匿名函数
====================
回调函数和匿名函数
-----------------
回调函数、闭包在JS中并不陌生，JS使用它可以完成事件机制，进行许多复杂的操作。PHP中却不常使用，今天来说一说PHP中中的回调函数和匿名函数。

回调函数
^^^^^^^^
回调函数：Callback （即call then back 被主函数调用运算后会返回主函数），是指通过函数参数传递到其它代码的，某一块可执行代码的引用。

通俗的解释就是把函数作为参数传入进另一个函数中使用；PHP中有许多 “需求参数为函数” 的函数，像 ``array_map`` , ``usort`` , ``call_user_func_array`` 之类，他们执行传入的函数，然后直接将结果返回主函数。好处是函数作为值使用起来方便，而且代码简洁，可读性强。

匿名函数
^^^^^^^^
匿名函数，顾名思义，是没有一个确定函数名的函数，PHP将匿名函数和闭包视作相同的概念（匿名函数在PHP中也叫作闭包函数）。它的用法，当然只能被当作变量来使用了。

PHP中将一个函数赋值给一个变量的方式有四种：

- 我们经常会用到的：函数在外部定义/或PHP内置，直接将函数名作为字符串参数传入。注意：如果是类静态函数的话以 ``CLASS::FUNC_NAME`` 的方式传入。
- 使用 ``create_function($args, $func_code);`` 创建函数，会返回一个函数名。 $func_code为代码体，$args为参数字符串，以','分隔；
- 直接赋值: ``$func_name = function($arg){statement}``
- 直接使用匿名函数，在参数处直接定义函数，不赋给具体的变量值；

第一种方式因为是平常所用，不再多提；第二种类似eval()方法的用法，也被PHP官方列为不推荐使用的方式，而且其定义方式太不直观，我除了测试外，也没有在其他地方使用过，也略过不提。在这里重点说一下第三种和第四种用法；

后两种创建的函数就被称为匿名函数，也就是闭包函数， 第三种赋值法方式创建的函数非常灵活，可以通过变量引用。可以用 ``is_callable($func_name)`` 来测试此函数是否可以被调用， 也可以通过 ``$func_name($var)`` 来直接调用；而第四种方式创建的函数比较类似于JS中的回调函数，不需要变量赋值，直接使用；

另外要特别介绍的是 ``use`` 关键词，它可以在定义函数时，用来引用父作用域中的变量；用法为 ``function($arg) use($outside_arg) {function_statement}``  。其中$outside_arg 为父作用域中的变量，可以在function_statement使用。
这种用法用在回调函数“参数值数量确定”的函数中。 如usort需求$callback的参数值为两项，可是我们需要引入别的参数来影响排序怎么办呢？使用use()关键词就很方便地把一个新的变量引入$callback内部使用了。

array_map/array_filter/array_walk
---------------------------------
把这三个函数放在一块是因为这三个函数在执行逻辑上比较类似，类似于下面的代码：

.. code-block:: php

	<?php
	$result = [];
	foreach($vars as $key=>$val){
	  $item = callback();
	  $result[] = $item;
	}
	return $result;
	//典型情况下 callback 接受两个参数。array 参数的值作为第一个，键名作为第二个
	//如果 callback 需要直接作用于数组中的值，则给 callback 的第一个参数指定为引用。这样任何对这些单元的改变也将会改变原始数组本身。
	array_walk($vars, $callback)
	?>

其callback应如下：

.. code-block:: php

	<?php
	$callback = function(&$val, $key[, $arg]){
	  doSomething($val);
	}
	?>

array_walk返回执行是否成功，是一个布尔值。对$value添加引用符号可以在函数内改变$value值，以达到改变$vars数组的效果。由于其$callback对参数数量要求为两项，array_walk不能传入strtolower/array_filter之类的$callback,若想实现类似功能，可以使用接下来要说的array_map()。

array_walk_recursive($arr, $callback)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
返回值和执行机制类似于array_walk;

其callback同array_walk，不同的是，如果$val是数组，函数会递归地向下处理$val；需要注意的是这样的话$val为数组的$key就会被忽略掉了。

array_filter($vars, $callback, $flag)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
其$callback类似于：

.. code-block:: php

	<?php
	$callback = function($var){
	  return true or false;
	}
	?>

array_filter会过滤掉$callback执行时返回为false的项目，array_filter返回过滤完成后的数组。

第三个参数 $flag决定其callback形参$var的值，不过这个可能是PHP高版本的特性，我的PHP5.5.3不支持，大家可以自行测试。默认传入数组每项的value,当flag为ARRAY_FILTER_USE_KEY传入数组每项的key，ARRAY_FILTER_USE_BOTH传入键和值;

array_map($callback, &$var_as [,$var_bs...])
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
其$callback类似于：

.. code-block:: php

	<?php
	$callback = function($var_a[, $var_b...]){
	  doSomething($var_a, $var_b);
	}
	?>

返回$var_as经过callback处理后的数组（会改变原数组）；如果有多个数组的时候将两个数组同样顺序的项目传入处理，执行次数为参数数组中项目最多的个数；

usort/array_reduce
------------------
把这两个函数放在一块，因为他们的执行机制都有些特殊。

usort(&$vars, $callback)
^^^^^^^^^^^^^^^^^^^^^^^^^

$callback应该如下：

.. code-block:: php

	<?php
	callback = function($left, $right){
	    $res = compare($left, $right);
	    return $res;
	}
	?>

usort返回执行成功与否，bool值。用户自定义方法 比较$left 和 $right，其中$left和$right是$vars中的任意两项；

$left > $right时返回正整数， $left < $right时返回负整数， $left = $right时返回0；

$vars中的元素会被取出会被由小到大升序排序。 想实现降序排列，将$callback的返回值反一下就行了。

array_reduce($vars ,$callable [, mixed $initial = NULL])
-------------------------------------------------------
$callback应该如下：

.. code-block:: php

	<?php
	$callback = function($initial, $var){
	    $initial = calculate($initail, $var);
	    return $initial;
	}
	?>

初始值$initial默认为null，返回经过迭代后的initial；一定要将$initial返回，这样才能不停地改变$initial的值，实现迭代的效果。

这里顺便说一下map和reduce的不同：

- map：将数组中的成员遍历处理，每次返回处理后的一个值，最后结果值为所有处理后值组成的多项数组；
- reduce：遍历数组成员，每次使用数组成员结合初始值处理，并将初始值返回，即使用上一次执行的结果，配合下一次的输入继续产生结果，结果值为一项；

call_user_func/call_user_func_array
------------------------------------
call_user_func[_array]($callback, $param)

$callback形如：

.. code-block:: php

	<?php
	$callback = function($param){
	    $result = statement();
	    return $result;
	}
	?>

返回值多种，具体看$callback。

可用此函数实现PHP的事件机制，其实并不高深，在判断条件达成，或程序执行到某一步后 ``call_user_func()`` 就OK了。

其实以上$callback不用单独定义并使用变量引用，使用上面说过的第四种函数定义方式，直接在函数内定义，使用‘完全'匿名函数就行了。 如：

.. code-block:: php

	<?php
	usort($records, function mySortFunc($arg) use ($order){
	  func_statement;
	});
	?>

php的闭包（Closure）匿名函数初探
==============================
提到闭包就不得不想起匿名函数，也叫闭包函数（closures），貌似PHP闭包实现主要就是靠它。声明一个匿名函数是这样：

.. code-block:: php

	<?php
	$func = function() {

	}; //带结束符
	?>

可以看到，匿名函数因为没有名字，如果要使用它，需要将其返回给一个变量。匿名函数也像普通函数一样可以声明参数，调用方法也相同：

.. code-block:: php

	<?php
	$func = function( $param ) {
	  echo $param;
	};

	$func( 'some string' );

	//输出：
	//some string
	?>

顺便提一下，PHP在引入闭包之前，也有一个可以创建匿名函数的函数： ``create function`` ，但是代码逻辑只能写成字符串，这样看起来很晦涩并且不好维护，所以很少有人用。

实现闭包
--------
将匿名函数在普通函数中当做参数传入，也可以被返回。这就实现了一个简单的闭包。

.. code-block:: php

	<?php
	//例一
	//在函数里定义一个匿名函数，并且调用它
	function printStr() {
	    $func = function( $str ) {
	        echo $str;
	    };
	    $func( 'some string' );
	}
	printStr();

	//例二
	//在函数中把匿名函数返回，并且调用它
	function getPrintStrFunc() {
	    $func = function( $str ) {
	        echo $str;
	    };
	    return $func;
	}
	$printStrFunc = getPrintStrFunc();
	$printStrFunc( 'some string' );

	//例三
	//把匿名函数当做参数传递，并且调用它
	function callFunc( $func ) {
	    $func( 'some string' );
	}

	$printStrFunc = function( $str ) {
	    echo $str;
	};
	callFunc( $printStrFunc );

	//也可以直接将匿名函数进行传递。如果你了解js，这种写法可能会很熟悉
	callFunc( function( $str ) {
	    echo $str;
	} );
	?>

连接闭包和外界变量的关键字：USE
-----------------------------
闭包可以保存所在代码块上下文的一些变量和值。PHP在默认情况下，匿名函数不能调用所在代码块的上下文变量，而需要通过使用use关键字。

例如：

.. code-block:: php

	<?php
	function getMoney() {
	  $rmb = 1;
	  $dollar = 6;
	  $func = function() use ( $rmb ) {
	    echo $rmb;
	    echo $dollar;
	  };
	  $func();
	}

	getMoney();

	//输出：
	//1
	//报错，找不到dorllar变量
	?>


可以看到，dollar没有在use关键字中声明，在这个匿名函数里也就不能获取到它，所以开发中要注意这个问题。

有人可能会想到，是否可以在匿名函数中改变上下文的变量，但我发现是不可以的：

.. code-block:: php

	<?php
	function getMoney() {
	  $rmb = 1;
	  $func = function() use ( $rmb ) {
	    echo $rmb;
	    //把$rmb的值加1
	    $rmb++;
	  };
	  $func();
	  echo $rmb;
	}

	getMoney();

	//输出：
	//1
	//1
	?>

啊，原来use所引用的也只不过是变量的一个副本而已。但是我想要完全引用变量，而不是复制。

要达到这种效果，其实在变量前加一个 & 符号就可以了：

.. code-block:: php

	<?php
	function getMoney() {
	  $rmb = 1;
	  $func = function() use ( &$rmb ) {
	    echo $rmb;
	    //把$rmb的值加1
	    $rmb++;
	  };
	  $func();
	  echo $rmb;
	}

	getMoney();

	//输出：
	//1
	//2
	?>

好，这样匿名函数就可以引用上下文的变量了。如果将匿名函数返回给外界，匿名函数会保存use所引用的变量，而外界则不能得到这些变量，这样形成‘闭包'这个概念可能会更清晰一些。

根据描述改变一下上面的例子：

.. code-block:: php

	<?php
	function getMoneyFunc() {
	  $rmb = 1;
	  $func = function() use ( &$rmb ) {
	    echo $rmb;
	    //把$rmb的值加1
	    $rmb++;
	  };
	  return $func;
	}

	$getMoney = getMoneyFunc();
	$getMoney();
	$getMoney();
	$getMoney();

	//输出：
	//1
	//2
	//3
	?>

.. note:: PHP闭包的特性并没有太大惊喜，其实用CLASS就可以实现类似甚至强大得多的功能，更不能和js的闭包相提并论，只能期待PHP以后对闭包支持的改进。不过匿名函数还是挺有用的，比如在使用preg_replace_callback等之类的函数可以不用在外部声明回调函数了。

.. code-block:: php

	<?php
	/**
	 * 一个利用闭包的计数器产生器
	 * 这里其实借鉴的是python中介绍闭包时的例子...
	 * 我们可以这样考虑:
	 *      1. counter函数每次调用, 创建一个局部变量$counter, 初始化为1.
	 *      2. 然后创建一个闭包, 闭包产生了对局部变量$counter的引用.
	 *      3. 函数counter返回创建的闭包, 并销毁局部变量, 但此时有闭包对$counter的引用,
	 *          它并不会被回收, 因此, 我们可以这样理解, 被函数counter返回的闭包, 携带了一个游离态的
	 *          变量.
	 *      4. 由于每次调用counter都会创建独立的$counter和闭包, 因此返回的闭包相互之间是独立的.
	 *      5. 执行被返回的闭包, 对其携带的游离态变量自增并返回, 得到的就是一个计数器.
	 * 结论: 此函数可以用来生成相互独立的计数器.
	 */
	function counter() {
	    $counter = 1;
	    return function() use(&$counter) {return $counter ++;};
	}
	$counter1 = counter();
	$counter2 = counter();
	echo "counter1: " . $counter1() . "<br />/n";
	echo "counter1: " . $counter1() . "<br />/n";
	echo "counter1: " . $counter1() . "<br />/n";
	echo "counter1: " . $counter1() . "<br />/n";
	echo "counter2: " . $counter2() . "<br />/n";
	echo "counter2: " . $counter2() . "<br />/n";
	echo "counter2: " . $counter2() . "<br />/n";
	echo "counter2: " . $counter2() . "<br />/n";
	?>

闭包的作用
---------
减少foreach的循环的代码
^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

	<?php
	// 一个基本的购物车，包括一些已经添加的商品和每种商品的数量。
	// 其中有一个方法用来计算购物车中所有商品的总价格。该方法使用了一个closure作为回调函数。
	class Cart
	{
	    const PRICE_BUTTER  = 1.00;
	    const PRICE_MILK    = 3.00;
	    const PRICE_EGGS    = 6.95;
	    protected   $products = array();
	    public function add($product, $quantity)
	    {
	        $this->products[$product] = $quantity;
	    }
	    public function getQuantity($product)
	    {
	        return isset($this->products[$product]) ? $this->products[$product] :
	               FALSE;
	    }
	    public function getTotal($tax)
	    {
	        $total = 0.00;
	        $callback =
	            function ($quantity, $product) use ($tax, &$total)
	            {
	                $pricePerItem = constant(__CLASS__ . "::PRICE_" .
	                    strtoupper($product));
	                $total += ($pricePerItem * $quantity) * ($tax + 1.0);
	            };
	        //使用用户自定义函数对数组中的每个元素做回调处理
	        array_walk($this->products, $callback);
	        return round($total, 2);;
	    }
	}
	$my_cart = new Cart;
	// 往购物车里添加条目
	$my_cart->add('butter', 1);
	$my_cart->add('milk', 3);
	$my_cart->add('eggs', 6);
	// 打出出总价格，其中有 5% 的销售税.
	print $my_cart->getTotal(0.05) . "\n";
	// The result is 54.29
	?>

这里如果我们改造getTotal函数必然要使用到foreach。

减少函数的参数
^^^^^^^^^^^^^^

.. code-block:: php

	<?php
	function html ($code, $id = "", $class = "") {
	    if ($id !== "")
	        $id = " id = \"$id\"";
	    $class = ($class !== "") ? " class =\"$class\">" : ">";
	    $open = "<$code$id$class";
	    $close = "</$code>";
	    return function ($inner = "") use ($open, $close) {
	        return "$open$inner$close";
	    };
	}
	?>

如果是使用平时的方法，我们会把inner放到html函数参数中，这样不管是代码阅读还是使用都不如使用闭包。

解除递归函数
^^^^^^^^^^^^

.. code-block:: php

	<?php
	$fib = function($n) use(&$fib) {
	    if($n == 0 || $n == 1) return 1;
	    return $fib($n - 1) + $fib($n - 2);
	};
	echo $fib(2) . "\n"; // 2
	$lie = $fib;
	$fib = function(){die('error');};//rewrite $fib variable
	echo $lie(5); // error   because $fib is referenced by closure
	?>

注意上题中的use使用了&，这里不使用&会出现错误fib(n-1)是找不到function的（前面没有定义fib的类型）所以想使用闭包解除循环函数的时候就需要使用。

.. code-block:: php

	<?php
	$recursive = function () use (&$recursive){
	// The function is now available as $recursive
	}
	?>

延迟绑定
^^^^^^^^
如果你需要延迟绑定use里面的变量，你就需要使用引用，否则在定义的时候就会做一份拷贝放到use中。

.. code-block:: php

	<?php
	$result = 0;
	$one = function()
	{
	    var_dump($result);
	};
	$two = function() use ($result)
	{
	    var_dump($result);
	};
	$three = function() use (&$result)
	{
	    var_dump($result);
	};
	$result++;
	$one();    // outputs NULL: $result is not in scope
	$two();    // outputs int(0): $result was copied
	$three();    // outputs int(1)
	?>

使用引用和不使用引用就代表了是调用时赋值，还是申明时候赋值。