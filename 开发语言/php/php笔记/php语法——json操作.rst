******
Json操作
******

.. contents:: 目录
   :depth: 3

json_encode()
=============
该函数主要用来将数组和对象，转换为json格式。

索引数组和关联数组
-----------------
PHP支持两种数组，一种是只保存"值"（value）的索引数组（indexed array），另一种是保存"名值对"（name/value）的关联数组（associative array）。

由于javascript不支持关联数组，所以json_encode()只将索引数组（indexed array）转为数组格式，而将关联数组（associative array）转为对象格式。

比如，现在有一个索引数组

.. code-block:: php

    <?php
	// 索引数组
	$arr = Array('one', 'two', 'three');
	// 结果为 ["one","two","three"]
	echo json_encode($arr);

	// 如果你需要将"索引数组"强制转化成"对象"，可以这样写
	echo json_encode( (object)$arr );
	// 或者
	echo json_encode ( $arr, JSON_FORCE_OBJECT );

	// 关联数组
	$arr = Array('1'=>'one', '2'=>'two', '3'=>'three');
	// 结果为 {"1":"one","2":"two","3":"three"}
	echo json_encode($arr);
    ?>

类（class）的转换
-----------------

.. code-block:: php

    <?php
    $obj = new stdClass();
    $obj->body = 'another post';
    $obj->id = 21;
    $obj->approved = true;
    $obj->favorite_count = 1;
    $obj->status = NULL;
    echo json_encode($obj);
    ?>

下面是一个PHP的类：

.. code-block:: php

    <?php
	class Foo {
	    const     ERROR_CODE = '404';
	    public    $public_ex = 'this is public';
	    private   $private_ex = 'this is private!';
	    protected $protected_ex = 'this should be protected';
	    public function getErrorCode() {
	        return self::ERROR_CODE;
	    }
	}

	$foo = new Foo;
	$foo_json = json_encode($foo);
	// 输出结果是 {"public_ex":"this is public"}
	echo $foo_json;
    ?>

可以看到，除了公开变量（public），其他东西（常量、私有变量、方法等等）都遗失了。

json_decode()
=============
该函数用于将json文本转换为相应的PHP数据结构。下面是一个例子：

.. code-block:: php

    <?php
	$json = '{"foo": 12345}';
	$obj = json_decode($json);
	print $obj->{'foo'}; // 12345
	print $obj->foo; // 12345，注意这两种访问属性的方式
    ?>

通常情况下，json_decode()总是返回一个PHP对象，而不是数组。如果想要强制生成PHP关联数组，json_decode()需要加一个参数true：

.. code-block:: php

    <?php
	$json = '{"a":1,"b":2,"c":3,"d":4,"e":5}';
	var_dump(json_decode($json));
	var_dump(json_decode($json,true));
    ?>

json_decode()的常见错误
======================

.. code-block:: php

    <?php
	$bad_json = "{ 'bar': 'baz' }";
	$bad_json = '{ bar: "baz" }';
	$bad_json = '{ "bar": "baz", }';
    ?>

对这三个字符串执行json_decode()都将返回null，并且报错。

- 第一个的错误是，json的分隔符（delimiter）只允许使用双引号，不能使用单引号。
- 第二个的错误是，json名值对的"名"（冒号左边的部分），任何情况下都必须使用双引号。
- 第三个的错误是，最后一个值之后不能添加逗号（trailing comma）。

另外，json只能用来表示对象（object）和数组（array），如果对一个字符串或数值使用json_decode()，将会返回null。

编码问题
========
相信很多人在使用Ajax与后台php页面进行交互的时候都碰到过中文乱码的问题。JSON作为一种轻量级的数据交换格式，备受亲睐，但是用PHP作为后台交互，容易出现中文乱码的问题。JSON和js一样，对于客户端的字符都是以UTF8的形式进行处理的，也就是说，使用JSON作为提交和接收的数据格式时字符都采用UTF8编码处理，当我们的页面编码和数据库编码不是采用UTF8的时候，就极容易出现中文乱码的问题。解决办法自然是在用js或者PHP处理JSON数据的时候都采用UTF8的形式。

  PHP5.2或以上的版本把json_encode作为内置函数来用，给网站制作者带来了很大的方便，但是我们必须注意到json_encode只支持UTF8编码的字符，否则，中文乱码或者空值就出现了。

解决办法分为以下两个步骤。

1. 保证在使用JSON处理的时候字符是以UTF8编码的。具体我们可以把数据库编码和页面编码都改为UTF8。当然喜欢用gbk编码的话，可以在进行JSON处理前，把字符转为UTF8形式。在PHP中有如下方法：

.. code-block:: php

	<?php
	  $data="JSON中文";
	  $newData=iconv("GB2312","UTF-8//IGNORE",$data);
	  echo $newData;
	  //ignore的意思是忽略转换时的错误，如果没有ignore参数，所有该字符后面的字符都不会被保存。
	  //或是("GB2312","UTF-8",$data);
	?>

2. 后台PHP页面（页面编码为UTF-8或者已经把字符转为UTF-8）使用json_encode将PHP中的array数组转为JSON字符串。例如：

.. code-block:: php

	<?php
	 $testJSON=array('name'=>'中文字符串','value'=>'test');
	 echo json_encode($testJSON);
	?>

查看输出结果为： ``{"name":"\u4e2d\u6587\u5b57\u7b26\u4e32","value":"test"}`` 

可见即使用UTF8编码的字符，使用json_encode也出现了中文乱码。解决办法是在使用json_encode之前把字符用函数urlencode()处理一下，然后再json_encode，输出结果的时候在用函数urldecode()转回来。具体如下：

.. code-block:: php

	<?php
	 $testJSON=array('name'=>'中文字符串','value'=>'test');
	 //echo json_encode($testJSON);
	 foreach ( $testJSON as $key => $value ) {
	  $testJSON[$key] = urlencode ( $value );
	 }
	 echo urldecode ( json_encode ( $testJSON ) );
	?>

查看输出结果为： ``{"name":"中文字符串","value":"test"}``

对于多维数组的urlencode转换，则需要下面函数：

.. code-block:: php

    <?php
	/**************************************************************
	 *
	 *	使用特定function对数组中所有元素做处理
	 *	@param	string	&$array		要处理的字符串
	 *	@param	string	$function	要执行的函数
	 *	@return boolean	$apply_to_keys_also	是否也应用到key上
	 *	@access public
	 *
	 *************************************************************/
	function arrayRecursive(&$array, $function, $apply_to_keys_also = false)
	{
	    static $recursive_counter = 0;
	    if (++$recursive_counter > 1000) {
	        die('possible deep recursion attack');
	    }
	    foreach ($array as $key => $value) {
	        if (is_array($value)) {
	            arrayRecursive($array[$key], $function, $apply_to_keys_also);
	        } else {
	            $array[$key] = $function($value);
	        }

	        if ($apply_to_keys_also && is_string($key)) {
	            $new_key = $function($key);
	            if ($new_key != $key) {
	                $array[$new_key] = $array[$key];
	                unset($array[$key]);
	            }
	        }
	    }
	    $recursive_counter--;
	}

	/**************************************************************
	 *
	 *	将数组转换为JSON字符串（兼容中文）
	 *	@param	array	$array		要转换的数组
	 *	@return string		转换得到的json字符串
	 *	@access public
	 *
	 *************************************************************/
	function JSON($array) {
	    arrayRecursive($array, 'urlencode', true);
	    $json = json_encode($array);
	    return urldecode($json);
	}

	$array = array
	(
	    'Name'=>'希亚',
	    'Age'=>20,
	    'child' => array (
	        'Name'=>'叼毛',
	        'Age'=>30,
	    )
	);

	echo JSON($array);
    ?>

到此，成功地输出了中文字符。随意使用json_encode吧。这样子在PHP后台输出的JSON字符串在前台javascript中Ajax接收后eval出来也不会出现中文乱码，因为js在处理JSON格式数据是也是以UTF8的形式进行的，与PHP类似，故接收PHP页面的JSON字符串不会出现问题。