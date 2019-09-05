PHP数组
=====

声明数组
----
在PHP中声明数组的方式主要有两种：

- 一种是直接通过为数组元素赋值的方式声明数组；

  .. code-block:: php

     <?php
	//索引数组
	 $user[0]=1;//用户序号
	 $user[1]="zhangsan";//用户名
	 $user[2]=10;//年龄
	 $user[3]="nan";//性别

	 //关联数组
	 $user["id"]=1;
	 $user["name"]="zhangsan";
	 $user["age"]=10;
	 $user["sex"];
	 $user["age"]=90;//赋值
     ?>

- 另一种是应用array()函数声明数组；

  .. code-block:: php

      <?php
	//使用array()声明数组
	$user=array(1,"zhangsan",10,"nan");
	//使用array()声明关联数组
	$user=array("id"=>1,"name"=>"zhangsan","age"=>10,"sex"=>"nan");
	//声明多维数组(多条记录)，来保存一个表中的多条用户信息记录
	$user=array(
	        //用$user[0]调用这一行，比如调用这条记录中的姓名,$user[0][1]
	        array(1,"zhangsan",10,"nan"),
	        //用$user[1]调用这一行，比如调用这条记录中的姓名,$user[1][1]
	        array(2,"lisi",20,"nv")
	);
	//数组保存多个表，每个表有多条记录
	$info=array(
	        "user"=>array(
	                array(1,"zhangsan",10,"nan"),
	                array(2,"lisi",20,"nv")
	        ),
	        "score"=>array(
	                array(1,90,80,70),
	                array(2,60,40,70)
	        )
	);
      ?>

- 自 5.4 起可以使用短数组定义语法，用 [] 替代 array();

  .. code-block:: php

      <?php
		//使用[]声明数组
		$user=[1,"zhangsan",10,"nan"];
		//使用[]声明关联数组
		$user=["id"=>1,"name"=>"zhangsan","age"=>10,"sex"=>"nan"];
		//声明多维数组(多条记录]，来保存一个表中的多条用户信息记录
		$user=[
		        //用$user[0]调用这一行，比如调用这条记录中的姓名,$user[0][1]
		        [1,"zhangsan",10,"nan"],
		        //用$user[1]调用这一行，比如调用这条记录中的姓名,$user[1][1]
		        [2,"lisi",20,"nv"]
		];
		//数组保存多个表，每个表有多条记录
		$info=[
		        "user"=>[
		                [1,"zhangsan",10,"nan"],
		                [2,"lisi",20,"nv"]
		        ],
		        "score"=>[
		                [1,90,80,70],
		                [2,60,40,70]
		        ]
		];
      ?>

数组的类型
-----
PHP支持两种数组：

- 数字索引数组，使用数字作为键；
- 关联数组，使用字符串作为键；

 技巧：关联数组的键名可以是任何一个整数或字符串。如果键名是一个字符串，则不要忘了给这个键名或索引加上定界修饰符——单引号(')或者双引号(")。对于数字索引数组，为了避免不必要的麻烦，最好也加上定界符。

向数组中添加元素
--------------
- 通过给数组下标赋值来添加元素；
- 通过 ``$array[]=10;`` 在索引数组末尾追加元素；
- 通过 ``array_push($array,23,14,...);`` 在索引数组末尾追加多个元素；返回处理之后数组的元素个数。

  .. code-block:: php

      <?php
		$stack = array("orange", "banana");
		array_push($stack, "apple", "raspberry");
		print_r($stack);
		/* 运行结果
		Array
		(
		    [0] => orange
		    [1] => banana
		    [2] => apple
		    [3] => raspberry
		)
		 */
       ?>

- 通过array_splice()向数组中插入单个或多个元素；

  .. code-block:: php

      <?php
	$colors = array("red", "green", "blue", "yellow");
	//设置第三个参数长度为0，在索引3前面插入元素和数组
	array_splice($colors, 3, 0, "purple");
	print_r($colors);

	$capitals = array("USA" => "Washington", "Great Britain" => "London", "New Zealand" => "Wellington", "Australia" => "Canberra", "Italy" => "Rome");

	$down_under = array_splice($capitals, 2, 2);  // Remove New Zealand and Australia
	$france = array("France" => "Paris");
	array_splice($capitals, 1, 0, $france);     // 在USA and G.B.之间插入France，但是索引不会保留，重新索引为0
	print_r($capitals);
      ?>

- 通过array_pad(array $input, int $pad_size , mixed $pad_value)向数组中添加多个指定值。

  .. code-block:: php

      <?php
	$input = array(12, 10, 9);

	// pad_size为正值则从数组右边添加
	$result = array_pad($input, 5, 0); // result is array(12, 10, 9, 0, 0)
	// pad_size为负值则从数组左边添加
	$result = array_pad($input, -7, -1); // result is array(-1, -1, -1, -1, 12, 10, 9)
	// pad_size的值小于或等于数组的长度，则不添加值
	$result = array_pad($input, 2, "noop"); // array(12, 10, 9)
      ?>

- 通过array_fill (int $start_index, int $num, mixed $value)创建指定值的数组；

  .. code-block:: php

      <?php
	$a = array_fill(5, 6, 'banana');
	$b = array_fill(-2, 2, 'pear');
	print_r($a);
	print_r($b);
      ?>

从数组中删除元素
---------------
删除数组中指定元素
^^^^^^^^^^^^^^^^
- 在某个数组中删除一个元素，可以直接用unset，但是数组的索引不会重排；

  .. code-block:: php

      <?php
		$arr = array('a','b','c','d');
		unset($arr[1]);
		print_r($arr);
		/* 运行结果
		Array ( [0] => a [2] => c [3] => d )
		 */

		// 删除数组中特定值的元素
		$arr2 = array(1,3,5,7,8);
		foreach ($arr2 as $key=>$value)
		{
		  if ($value === 3)
		    unset($arr2[$key]);
		}
      ?>

- array_splice删除数组中的元素，数组的索引会重排；

  .. code-block:: php

      <?php
		$arr = array('a','b','c','d');
		array_splice($arr,1,1);
		print_r($arr);
		/* 运行结果
		Array ( [0] => a [1] => c [2] => d )
		 */

		// 删除数组中特定值的元素
		$arr1 = array(1,3, 5,7,8);
		$key = array_search(3, $arr1);
		if ($key !== false)
		    array_splice($arr1, $key, 1);
      ?>

 总结：可以看到使用array_splice()删除特定值和使用unset删除特定值是有区别的。

 - array_splice()函数删除的话，数组的索引值也变化了。
 - unset()函数删除的话，数组的索引值没有变化。

删除数组中的值为空的元素
^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
	  $array = ('a' => "abc", 'b' => "bcd",'c' =>"cde",'d' =>"def",'e'=>"");
	  array_filter($array);
	  print_r($array);
	  /* 运行结果
	  Array (
	     [a] => abc
	     [b] => bcd
	     [c] => cde
	     [d] => def
	  )
	   */
    ?>

删除数组中重复元素
^^^^^^^^^^^^^^^^
- php有内置函数array_unique可以用来删除数组中的重复值，array_unique() 接受 array 作为输入并返回没有重复值的新数组；

  注意键名保留不变。array_unique() 先将值作为字符串排序，然后对每个值只保留第一个遇到的键名，接着忽略所有后面的键名。这并不意味着在未排序的 array 中同一个值的第一个出现的键名会被保留。

  注: 当且仅当 (string) $elem1 === (string) $elem2 时两个单元被认为相同。就是说，当字符串的表达一样时。第一个单元将被保留。

  .. code-block:: php

      <?php
		$input = array("a" => "green", "red", "b" => "green", "blue", "red");
		$result = array_unique($input);
		print_r($result);
		/* 运行结果
		Array
		(
		 [a] => green
		 [0] => red
		 [1] => blue
		)
		 */
      ?>

- 使用php的array_flip函数来间接的实现去重效果

  array_flip是反转数组键和值的函数，它有个特性就是如果数组中有二个值是一样的，那么反转后会保留最后一个键和值，利用这个特性我们用他来间接的实现数组的去重。

  .. code-block:: php

      <?php
		$arr = array("a"=>"a1","b"=>'b1',"c"=>"a2","d"=>"a1");
		$arr1 = array_flip($arr);
		print_r($arr1);//先反转一次,去掉重复值,输出Array ( [a1] => d [b1] => b [a2] => c )
		$arr2 = array_flip($arr1);
		print_r($arr2);//再反转回来,得到去重后的数组,输出Array (  [d] => a1 [b] => b1 [c] => a2 )
		$arr3 = array_unique($arr);
		print_r($arr3);//利用php的array_unique函数去重,输出Array ( [a] => a1 [b] => b1 [c] => a2 )
      ?>

 二种方法不同的：
  - 用array_flip得到的是重复元素最后的键和值；
  - 用array_unique得到的是重复元素第一个键和值；

获取数组中的元素
---------------
获取子数组
^^^^^^^^^
- 使用array_slice()从索引数组中获取子索引数组；

  .. code-block:: php

      <?php
	$person = array('name' => 'Fred', 'age' => 35, 'wife' => 'Betty');
	$subset = array_slice($person, 1, 2);  // $subset is array(0 => 35, 1 => 'Betty')
	//array_slice() can be combined with list() to extract only some values to variables:
	$order = array('Tom', 'Dick', 'Harriet', 'Brenda', 'Jo');
	list($second, $third) = array_slice($order, 1, 2); // $second is 'Dick', $third is 'Harriet'
      ?>

- 使用array_chunk()拆分为多维；

  .. code-block:: php

      <?php
	$nums = range(1, 7);
	// 第一个参数是数组，第二个参数是每个数组的长度，第三个参数是否保留原始的键
	$rows = array_chunk($nums, 3, true);
	print_r($rows);
      ?>

获取数组的键和值
^^^^^^^^^^^^^^^
- 使用array_keys()获取数组的所有键
- 使用array_values()获取数组的所有键值

获取数组中最后一个元素
^^^^^^^^^^^^^^^^^^^^
- 通过 ``$array[count($array)-1]`` 来获取数组最后一个元素；
- 通过 ``array_pop`` 函数获取返回数组的最后一个元素，并将数组的长度减1，如果数组为空(或者不是数组)将返回null。

  .. code-block:: php

      <?php
		$stack = array("orange", "banana", "apple", "raspberry");
		$fruit = array_pop($stack);
		print_r($stack);
		/* 运行结果
		Array
		(
		    [0] => orange
		    [1] => banana
		    [2] => apple
		)
		 */
      ?>

数组和变量之间转换
-----------------
从一个数组创建变量
^^^^^^^^^^^^^^^^
- list()结构让你复制数组指定元素为变量；

.. code-block:: php

    <?php
	$person = array('name' => 'Fred', 'age' => 35, 'wife' => 'Betty');
	list($n, $a, $w) = $person;  // $n is 'Fred', $a is 35, $w is 'Betty'
	list($n, $a) = $person;     // $n is 'Fred', $a is 35
	$values = array('hello', 'world');

	/* Extra variables are fill with NULL */
	list($a, $b, $c) = $values;   // $a is 'hello', $b is 'world', $c is NULL
	$values = range('a', 'e');

	/* Use two or more commas to skip values */
	list($m,,$n,,$o) = $values;   // $m is 'a', $n is 'c', $o is 'e'
    ?>

- extract()自动从一个数组创建变量，变量名称为索引；

  .. code-block:: php

      <?php
	$var_array = array("color" => "blue", "size"  => "medium", "shape" => "sphere");
	extract($var_array);

	$shape = "round";
	$array = array("cover" => "bird", "shape" => "rectangular");
	// 第二个参数规定变量名称如何解决命名冲突，此处为有冲突，则加上前缀
	extract($array, EXTR_PREFIX_SAME, "book");
	// 不管是否有冲突，都加上前缀
	extract($array, EXTR_PREFIX_ALL, "book");
	echo "Cover: $cover, Book Shape: $book_shape, Shape: $shape";
      ?>

从变量创建一个数组
^^^^^^^^^^^^^^^^^
compact()使用变量名称和值创建一个关联数组。

.. code-block:: php

    <?php
	$color = 'indigo';
	$shape = 'curvy';
	$floppy = 'none';

	$a = compact('color', 'shape', 'floppy');
	// or
	$names = array('color', 'shape', 'floppy');
	$a = compact($names);
	print_r($a);
    ?>

输出数组
-------
在PHP中对数组元素进行输出，可以通过print_r()或者var_dump()函数来打印数组中的所有元素的内容。

``bool print_r(mixed $expression [, bool $return ])``

如果该函数的参数expression为普通的整型、字符型或浮点型，则输出该变量本身。如果该参数为数组，则按键值和元素的顺序输出该数组中的所有元素。

遍历数组
----

- 使用for语句循环遍历数组

  不是首选方式，数组必须是索引数组，而且下标必须是连续的。

  .. code-block:: php

      <?php
		$a=array('s','d','f','g');
		$num = count($a);
		for($i=0;$i<$num;$i++){
		    echo $a[$i];
		}
      ?>

- 使用foreach语句循环遍历数组（首选）

  使用foreach语句遍历数组时与数组的下标无关，不管是否是连续的数字索引数组，还是以字符串为下标的关联数组，都统一使用foreach语句遍历。foreach只能用于数组，自PHP5起，还可以遍历对象。

  .. code-block:: php

      <?php
		foreach(数组变量 as 自定义变量){
			//循环语句
		}
		foreach(数组变量 as 下标变量 => 值变量){
			//循环语句
		}
		// 引用数组元素而不是复制
		foreach(数组变量 as &自定义变量){
		    //循环语句
		}
		unset(自定义变量); // 删除最后一个元素的引用
		foreach(数组变量 as 下标变量 => &值变量){
		    //循环语句
		}
		unset(值变量); // 删除最后一个元素的引用
      ?>

  + 循环次数由数组的元素个数决定
  + 每次循环都将数组中的元素赋给后面的变量

  .. code-block:: php

      <?php
		$user=array(1,"zhangsan",40,"nan");
		foreach($user as $val)//$val是自定义变量
		{
		  echo $val."<br>";//输出与下标无关
		}
		foreach($user as $key=>$val)//$val $key 都是自定义变量
		{
		  echo $key."=====>".$val."<br>";
		}
      ?>

- 联合使用list()、each()和while循环遍历数组

  遍历数组的另外一个简便方法就是使用list()、each()和while语句联合，也是忽略数组元素下标就可以遍历数组的方法。

  + each()函数

    each()函数需要传递一个数组作为参数，返回数组中当前元素的键值对，并向后移动数组指针到下一个元素的位置。键值对返回为带有四个元素和索引混合的数组，键名为0，1，key和 value。单元 0 和 key 包含有数组元素的键名，1 和 value 包含有数组元素的值。如果内部指针越过了数组的末端，则 each() 返回 FALSE。

    .. code-block:: php

        <?php
		//each()的使用
		 $user=array("id"=>1,"name"=>"zhangsan","age"=>10,"sex"=>"nan");
		 $a=each($user);//Array ( [1] => 1 [value] => 1 [0] => id [key] => id )  默认是第一个元素的值
		 print_r($a);
		 $b=each($user);
		 print_r($b);//Array ( [1] => zhangsan [value] => zhangsan [0] => name [key] => name ) 每执行一次，向后遍历一个

		 $c=each($user);
		 print_r($c);//Array ( [1] => 10 [value] => 10 [0] => age [key] => age )
		 $d=each($user);
		 print_r($d);//Array ( [1] => nan [value] => nan [0] => sex [key] => sex )
		 $e=each($user);
		 var_dump($e);//bool(false)  当没有元素时，返回的值
        ?>

  + list函数

    像 array() 一样，这不是真正的函数，而是语言结构。 list() 可以在单次操作内就为一组变量赋值。即把数组中的值赋给一些变量。list()仅能用于数字索引的数组并假定数字索引从0开始。

    .. code-block:: php

        <?php
		list (mixed $var1 [, mixed $... ]) = array_expression
        ?>

    通过赋值运算，将数组中每个元素的值，赋给对应的list()函数中的每个参数。然后可以在脚本中直接使用这些变量。

    .. code-block:: php

        <?php
		//list()函数的使用
		 list($name,$age,$sex)=array("zhangsan",10,"nnnnn");
		 echo $name."<br>";
		 echo $age."<br>";
		 echo $sex."<br>";
		 //另一种使用方法
		 list(,,$sex)=array("zhangsan",10,"nnnnn");
		 echo $sex."<br>";//只把性别转换为变量
		 //ip判断
		 $ip="192.168.1.128";
		 list(,,,$d)=explode(".",$ip);//explode表示用 . 来分隔，并返回一个数组
		 echo $d;//取出128
		 //list()只能接收索引数组的例子
		 $user=array("id"=>1,"name"=>"zhangsan","age"=>10,"sex"=>"nan");
		 list($key,$value)=each($user);//Array( [1]=>1 [0]=>id) 按照索引下标的顺序给list中的参数赋值，所以先是 0键  然后是 1值
		 echo $key."--->".$value;
        ?>

  + while循环遍历数组

    语法格式如下：

    .. code-block:: php

  	    <?php
		while(list($key,$value) = each(array_expression)) {
			// 循环体
		}

		//while list() each()  组合使用
		 $user=array("id"=>1,"name"=>"zhangsan","age"=>10,"sex"=>"nan");
		 while(list($key,$value)=each($user))
		 {
		    echo $key."--->".$value."<br>";
		 }
  	    ?>

    虽然while遍历数组的结果和foreach语句相同，但这两种方法是有区别的。在使用while语句遍历数组之后，each()语句已经将传入的数组参数内部指针指向了数组的末端。当再次使用while语句遍历同一个数组时，数组指针已经在数组的末端，each()语句直接返回FALSE，while语句不会执行循环。只有在while语句执行之前先调用一下reset()函数，重新将数组指针指向第一个元素。而foreach语句会自动重置数组的指针位置，当foreach开始执行时，数组内部的指针会自动指向第一个单元。这意味着不需要在foreach循环之前调用reset()函数。

- 使用数组的内部指针控制遍历数组

  数组的内部指针是数组内部的组织机制，指向一个数组中的某个元素。默认是指向数组中第一个元素通过移动或改变指针的位置，可以访问数组中的任意元素。对于数组指针的控制PHP提供了以下几个内建函数可以利用。

  - current():取得目前指针位置的内容资料；
  - key():读取目前指针所指向资料的索引值（键值）；
  - next():将数组中的内部指针移动到下一个单元；
  - prev():将数组的内部指针倒回一位；
  - end():将数组的内部指针指向最后一个元素；
  - reset():将目前指针无条件移至第一个索引位置；

  这些函数的参数都是只有，就是要操作的数组本身。

  .. code-block:: php

        <?php
		$contact = array(
		"ID" => 1,
		"姓名" => "高某",
		"公司" => "A公司",
		"地址" => "北京市",
		"电话" => "(010)98765432",
		"EMAIL" => "gao@brophp.com",
		);

		//数组刚声明时，数组指针在数组中第一个元素位置
		echo '第一个元素：'.key($contact).' => '.current($contact).'<br>'; //第一个元素
		echo '第一个元素：'.key($contact).' => '.current($contact).'<br>'; //数组指针没动

		next($contact);
		next($contact);
		echo '第三个元素：'.key($contact).' => '.current($contact).'<br>'; //第三个元素

		end($contact);
		echo '最后一个元素：'.key($contact).' => '.current($contact).'<br>';

		prev($contact);
		echo '倒数第二个元素：'.key($contact).' => '.current($contact).'<br>';

		reset($contact);
		echo '又回到了第一个元素：'.key($contact).' => '.current($contact).'<br>';
        ?>

字符串与数组的转换
---------
字符串与数组的转换在程序开发过程中经常使用，PHP主要使用explode()函数和implode()函数实现。

- 使用explode()函数将字符串转换成数组

  explode()函数将字符串依指定的字符串或字符切开，语法格式如下：

  ``array explode (string $delimiter , string $string [, int $limit ])``

  + 本函数返回由字符串组成的数组，其中的每个元素都是由 separator 作为边界点分割出来的子字符串。
  + separator 参数不能是空字符串。如果 separator 为空字符串（""），explode() 将返回 FALSE。如果 separator 所包含的值在 string 中找不到，那么 explode() 将返回包含 string 中单个元素的数组。
  + 如果设置了limit参数，则返回的数组包含最多limit 个元素，而最后那个元素将包含string的剩余部分。
  + 如果limit参数是负数，则返回除了最后的 -limit 个元素外的所有元素。

  .. code-block:: php

      <?php
		$str = "Hello world. It's a beautiful day.";
		print_r (explode(" ",$str));
		/* 运行结果
		Array
		(
			[0] => Hello
			[1] => world.
			[2] => It's
			[3] => a
			[4] => beautiful
			[5] => day.
		)
		 */
      ?>

- 使用implode()函数将数组转换成一个新字符串

  implode()函数将数组的内容组合成一个新的字符串。

  ``string implode(string $glue , array $pieces)``

  参数glue是字符串类型，指要传入的分隔符；参数pieces是数组类型，指被传入的要合并元素的数组变量名称。

  .. code-block:: php

      <?php
		$array = array('lastname', 'email', 'phone');
		$comma_separated = implode(",", $array);

		echo $comma_separated; // lastname,email,phone
      ?>

数组查找函数
----------
php内置的三个数组函数来查找指定值是否存在于数组中，这三个数组分别是 in_array(),array_search(),array_key_exists()。

查找键值
^^^^^^^^
函数in_array()
""""""""""""""
in_array()函数的作用是检查数组中是否存在某个值，即在数组中搜索给定的值。语法格式如下：

``bool in_array(mixed $needle , array $haystack [, bool $strict = FALSE ])``

参数：

- needle：待搜索的值。 **如果 needle 是字符串，则比较是区分大小写的。**
- haystack：待搜索的数组。
- strict：如果第三个参数 strict 的值为 TRUE 则 in_array() 函数还会检查 needle 的类型是否和 haystack 中的相同。

返回值：

- 如果找到 needle 则返回 TRUE，否则返回 FALSE。

例子：

.. code-block:: php

    <?php
	$people = array("Peter", "Joe", "Glenn", "Cleveland");
	if(in_array("Glenn",$people)){
	  echo "Match found";
	}else{
	  echo "Match not found";
	}
	// 输出 Match found
    ?>

函数array_search()
"""""""""""""""""
array_search() 函数与 in_array() 一样，在数组中查找一个键值。如果找到了该值，则返回匹配该元素所对应的键名。如果没找到，则返回 false。注意在 PHP 4.2.0 之前，函数在失败时返回 null 而不是 false。同样如果第三个参数 strict 被指定为 true，则只有在数据类型和值都一致时才返回相应元素的键名。语法格式如下：

``mixed array_search(mixed $needle , array $haystack [, bool $strict = false ])``

参数：

- needle：搜索的值。 **如果 needle 是字符串，则比较以区分大小写的方式进行。**
- haystack：被搜索的数组。
- strict：如果可选的第三个参数 strict 为 TRUE，则 array_search() 将在 haystack 中检查完全相同的元素。 这意味着同样严格比较 haystack 里 needle 的 类型，并且对象需是同一个实例。

返回值：

- 如果找到了 needle 则返回它的键，否则返回 FALSE。
- 如果 needle 在 haystack 中出现不止一次，则返回第一个匹配的键。要返回所有匹配值的键，应该用 array_keys() 加上可选参数 search_value 来代替。

例子：

.. code-block:: php

    <?php
	$a=array("a"=>"Dog","b"=>"Cat","c"=>5,"d"=>"5");
	echo array_search("Dog",$a); //输出 a
	echo array_search("5",$a); // 输出 c
    ?>

查找键
^^^^^^
函数array_key_exists()
""""""""""""""""""""""
该函数是判断某个数组array中是否存在指定的key，如果该 key 存在，则返回 true，否则返回 false。语法格式如下：

``bool array_key_exists(mixed $key , array $array)``

参数：

- key：要检查的键。
- array：一个数组，包含待检查的键。

返回值：

- 成功时返回 TRUE， 或者在失败时返回 FALSE。

 array_key_exists() 仅仅搜索第一维的键。 多维数组里嵌套的键不会被搜索到。

例子：

isset() 对于数组中为 NULL 的值不会返回 TRUE，而 array_key_exists() 会。

.. code-block:: php

    <?php
	$a = array(0, NULL, '');

	function trueORfalse ($v) {
	    return $v ? "T" : "F";
	}

	for ($i = 0; $i < 4; $i++) {
	    if($a[$i]) { // 对于0，NULL，‘’都是假
	        echo $i.'：T';
	    }else {
	        echo $i.'：F';
	    }
	    // isset对于NULL值为假，而array_key_exists为真
	   printf(" %s %s\n", trueORfalse(isset($a[$i])), trueORfalse(array_key_exists($i, $a)));
	}
	/*
	 0：F T T
	1：F F T
	2：F T T
	3：F F F
	*/
    ?>

数组的键值操作函数
---------

函数array_values()
^^^^^^^^^^^^^^
array_values()函数的作用是返回数组中所有元素的值。使用非常容易，只有一个必选参数，规定传入给定的数组，返回一个包含给定数组中所有值得数组。但不保留键名，被返回的数组将使用顺序的数值键重新建立索引，从0开始并且以1递增。适合用于数组中元素下表混乱的数组，或者可以将关联数组转化为索引数组。

例如：

.. code-block:: php

    <?php
	$contact = array( "ID" => 1, "姓名" => "高某", "公司" => "A公司", "地址" => "北京市", "电话" => "(010)98765432" );
	//array_values()函数传入数组$contact 重新索引返回一个新数组
	print_r(array_values($contact));
	print_r($contact); //原数组$contact内容元素不变
	/*
	该程序运行后的结果如下所示：
	Array([0]=>1 [1]=>高某 [2]=>A公司 [3]=>北京市 [4]=>(010)98765432)
	Array([ID]=>1 [姓名]=>高某 [公司]=>A公司 [地址]=> 北京市 [电话]=>(010)98765432)
	 */
    ?>
函数array_keys()
^^^^^^^^^^^^
array_keys()函数的作用是返回数组中所有的键名。本函数中有一个必须参数和两个可选参数，其函数的原型如下：

``array array_keys(array input[,mixed search_value[,bool strict]])``

如果指定了可选参数search_value，则只返回该值的键名，否则input数组中的所有键名都会被返回。自PHP5起，可以用strict参数来进行全等比较。需要传入一个布尔型的值，FALSE为默认值不依赖类型。如果传入TRUE值则根据类型返回带有指定值得键名。

例子：

.. code-block:: php

    <?php
	$lamp = array("a"=>"Linux","b"=>"Apache","c"=>"MySQL","d"=>"php");
	print_r(array_keys($lamp)); //输出Array([0]=>a [1]=>b [2]=>c)
	print_r(array_keys($lamp,"Apache")); //使用第二个可选参数输出：Array([0]=>b)
	$a = array(10,20,30,"10"); //声明一个数组，其中元素的值有整数10和字符串"10"
	print_r(array_keys($a,"10",false)); //使用第三个参数(false)输出：Array([0]=>0 [1]=>3)
	$a = array(10,20,30,"10"); //声明一个数组，其中元素的值有整数10和字符串"10"
	print_r(array_keys($a,"10",true)); //使用第三个参数(true)输出：Array([0]=>3)
    ?>

函数in_array()
^^^^^^^^^^^^^^
in_array()函数的作用是检查数组中是否存在某个值，即在数组中搜索给定的值。本函数中有三个参数，前两个参数为必须的，最后一个参数为可选的。其函数的原型如下：

``bool in_array(mixed needle,array haystack[,bool strict])``

第一个参数needle为规定要在数组中搜索的值，第二个参数haystack是规定要被搜索的数组，如果给定的值needle存在于数组haystack中则返回TRUE。如果第三个参数设置为TRUE，函数只有在元素存在于数组中且数据类型于给定值相同时才返回TRUE。如果没有在数组中找到参数，函数返回FALSE。要注意如果needle参数是字符串，且strict参数设置为TRUE，则搜索区分大小写。

例子：

.. code-block:: php

    <?php
	//in_array()函数的简单使用形式
	$os = array("Mac", "NT", "Trix", "Linux");
	if (in_array("Trix", $os)) {  //这个条件成立，字符串Trix在数组$os中
	        echo "Got Trix";
	}
	if (in_array("mac", $os)) {   //这个条件失败，因为in_array()是区分大小写的
	        echo "Got mac";
	}
	//in_array严格类型检查例子
	$a = array('1.10', 12.4, 1.13);
	//第三个参数为true，所以字符串'12.4'和浮点数12.4类型不同
	if (in_array('12.4', $a, true)) {
	        echo "'12.4' found with strict check\n";
	}
	if (in_array(1.13, $a, true)) {  //这个条件成立，执行下面的语句
	        echo "1.13 found with strict check\n";
	}
	//in_array()中还可以用数组当做第一个参数作为查询条件
	$a = array(array('p', 'h'), array('p', 'r'), 'o');
	if (in_array(array('p', 'h'), $a)) {  //数组array('p','h')在数组$a中存在
	        echo "'ph'was found\n";
	}
	if (in_array(array('h', 'p'), $a)) {  //数组array('h','p')在数组$a中不存在
	        echo "'hp'was found\n";
	}
    ?>

函数array_flip()
^^^^^^^^^^^^
array_flip()的作用是交换数组中的键和值。返回一个反转后的数组，如果同一个值出现了多次，则最后一个键名将作为它的值，覆盖前面出现的元素。如果原数组中的值数据类型不是字符串或整数，函数将报错。该函数只有一个参数，其函数原型如下：

``array array_flip(array trans)``

参数是必须的，要求输入一个要处理的数组，返回该数组中每个元素的键和值交换后的数组。

例子：

.. code-block:: php

    <?php
	$lamp = array("os"=>"linux","WebServer"=>"Apache","Database"=>"MySQL","Language"=>"PHP");
	//输出：Array([linux]=>os [Apache]=>WebServer [MySQL]=>Database [PHP]=Language);
	print_r(array_flip($lamp)); //使用array_flip()函数交换数组中的键和值
	//在数组中如果元素的值相同，则使用array_flip()会发生冲突
	$trans = array("a"=>1,"b"=>1,"c"=>2);
	print_r(array_flip($trans)); //现在$trans变成了：Array([1]=> b [2]=> c)
    ?>

函数array_reverse()
^^^^^^^^^^^^^^^
array_reverse()作用是原数组中的元素顺序翻转，创建新的数组并返回。该函数有两个参数，其函数的原型如下：

``array array_reverse(array array[,bool preserve_keys])``

第一个参数是必选项，接收一个数组作为输入。第二个参数是可选项，如果指定为TRUE，则元素的键名保持不变，否则键名将会丢失。

例子：

.. code-block:: php

    <?php
	$lamp = array("OS"=>"Linux","WebServer"=>"Apache","Database"=>"MySQL","Language"=>"PHP");
	//使用array_reverse()函数将数组$lamp中的元素的顺序翻转
	print_r(array_reverse($lamp));//输出的结果Array([Language]=>PHP [Database]=>MySQL [WebServer]=>Apache  [OS]=>Linux)
    ?>

统计数组元素的个数和唯一性
-------------
有些函数可以用来确定数组中的值总数以及唯一值的个数。sizeof()函数是count()的别名。

函数count()
^^^^^^^
函数count()的作用是计算数组中的元素数目或对象中的属性个数。对于数组，返回其元素的个数，对于其他值则返回1。如果参数是变量而变量没有定义或是变量包含一个空的数组，该函数会返回0。该函数有两个参数，其函数原型如下：

``int count(mixed var [,int mode])``

其中第一个参数是必需的，传入要计数的数组或对象。第二个参数是可选的，规定函数的模式是否递归地计算多维数组中的数组的元素个数。可能的值是0和1,0为默认值，不检测多维数组，为1则检测多维数组。

例子：

.. code-block:: php

    <?php
	$lamp = array("Linux","Apache","MySQL","PHP");
	echo count($lamp);   //输出数组的个数为：4

	//声明一个二维数组，统计数组中元素的个数
	$web = array(
	        'lanmp' =>array("Linux","Apache","MySQL","PHP"),
	        'j2ee' =>array("Unix","Tomcat","Oracle","JSP")
	);

	echo count($web,1);  //第二个参数的模式为1则计算多维数组的个数，输出10
	echo count($web);    //默认模式为0，不计算数组的个数，输出2
    ?>

函数array_count_values()
^^^^^^^^^^^^^^^^^^^^
array_count_values()函数用于统计数组中所有值出现的次数。该函数只有一个参数，其函数的原型如下：

``array array_count_values(array input)``

参数规定输入一个数组，返回一个数组，其元素的键名是原数组的值，键值是该值在元素组中出现的次数。

例子：

.. code-block:: php

    <?php
	$array =array(1,"php",1,"mysql","php"); //晟敏跟一个带有重复值得数组
	$newArray = array_count_values($array); //统计数组$array中所有值出现的次数
	print_r($newArray); //输出：Array([1]=>2  [php]=>2  [mysql]=>1)
    ?>

函数array_unique()
^^^^^^^^^^^^^^
array_unique()函数用于删除数组中重复的值，并返回没有重复值得新数组。该函数的原型如下：

``array array_unique(array array)``

参数需要接收一个数组，当数组中几个元素的值相等时，只保留一个元素，其他的元素被删除，并且返回的新数组中键名不变。array_unique()先将值作为字符串排序，然后对每个值只保留第一个遇到的键名，接着忽略所有后面的键名。这并不意味着在未排序的array中同一个值的第一个出现的键名会被保留。

例子：

.. code-block:: php

    <?php
	$a = array("a"=>"php","b"=>"mysql","c"=>"php");   //声明一个带有重复值的数组
	print_r(array_unique($a));   //删除重复值后输出：Array([a]=>php  [b]=>mysql)
    ?>

使用回调函数处理数组的函数
-----------------------
函数的回调是PHP中的一种特殊机制，这种机制允许在函数的参数列表中，传入用户自定义的函数地址作为参数处理或完成一定的操作。使用回调函数可以很容易地实现一些所需的功能。

函数array_filter()
^^^^^^^^^^^^^^
用回调函数过滤数组中的单元。语法格式为：

``array array_filter(array $array [, callable $callback [, int $flag = 0 ]])``

函数用回调函数过滤数组中的元素，返回按用户自定义函数过滤后的新数组。该函数的第一个参数是必选项，要求输入一个过滤的数组。第二个参数是可选项，将用户自定义的函数名以字符串形式传入。如果自定义过滤函数返回true，则被操作的数组的当前值就会被含在内返回的结果数组，并将结果组成一个数组。如果原数组是一个关联数组，则键名保持不变。

参数：

- array：要循环的数组
- callback：使用的回调函数

  如果没有提供 callback 函数， 将删除 array 中所有等值为 FALSE 的条目。更多信息见转换为布尔值。
- flag：决定callback接收的参数形式:

    + ARRAY_FILTER_USE_KEY - callback接受键名作为的唯一参数
    + ARRAY_FILTER_USE_BOTH - callback同时接受键名和键值

返回值：

- 返回过滤后的数组。

例子：

.. code-block:: php

    <?php
	function myfunction($v)
	{
	        if ($v==="Horse")
	        {
	                return true;
	        }
	        return false;
	}
	$a=array(0=>"Dog",1=>"Cat",2=>"Horse");
	print_r(array_filter($a,"myfunction"));  // 输出：Array ( [2] => Horse )
    ?>

函数array_walk()
^^^^^^^^^^^^
函数对数组中的每一个元素应用回调函数处理。如果成功返回 ``true`` ，否则返回 ``false`` 。语法格式如下：

``bool array_walk(array &$array , callable $callback [, mixed $userdata = NULL ])``

第一个参数必选项，输入被指定的回调函数处理的数组。第二个参数也是必选，输入用户定义的回调函数，用于操作传入第一个参数的数组。自定义的回调函数接受两个参数，依次传入进来元素的值作为第一个参数，键名作为第二个参数。函数中提供可选的第三个参数，也将被作为回调函数的第三个参数接收。

如果自定义函数需要的参数比给出的多，则每次 ``array_walk()`` 调用回调函数时都会产生一个 ``E_WARNING`` 级的错误。这些警告可以通过在 ``array_walk()`` 调用前加上 ``PHP`` 的错误操作符 ``@`` 来抑制，或者用 ``error_reporting()`` 。

如果回调函数需要直接作用于数组中的值，可以将回调函数第一个参数指定为引用 ``&$value`` 。但是你不能将回调函数第三个参数指定为引用 ``&$userdata`` 。因为 ``array_walk`` 函数中对该参数是复制，而不是引用。

只有 ``array`` 的值才可以被改变，用户不应在回调函数中改变该数组本身的结构。例如增加/删除单元，unset 单元等等。如果 ``array_walk()`` 作用的数组改变了，则此函数的的行为未经定义，且不可预期。

例子：

.. code-block:: php

    <?php
	$a=array("a"=>"Cat","b"=>"Dog","c"=>"Horse");
	function myfunction1($value,$key)
	{
	        echo "The key $key has the value $value<br />";
	}
	array_walk($a,"myfunction1");
	/*输出：
	        The key a has the value Cat
	        The key b has the value Dog
	        The key c has the value Horse
	*/

	//  带有一个参数：
	function myfunction2($value,$key,$p)
	{
	        echo "$key $p $value<br />";
	}
	array_walk($a,"myfunction2","has the value");

	/*  输出：
	        a has the value Cat
	        b has the value Dog
	        c has the value Horse
	*/

	// 改变数组元素的值（请注意 &$value）
	function myfunction3(&$value,$key)
	{
	        $value="Bird";
	}
	array_walk($a,"myfunction3");
	print_r($a);

	/* 输出：
	        Array ( [a] => Bird [b] => Bird [c] => Bird )
	*/

	function myfunction4($value, $key, &$userdata) {
		$userdata = 'afterRun';
	}
	$data = 'beforeRun';
	array_walk($a,"myfunction3", $data);
	echo $data; // 输出 beforeRun

	// 对于上面的问题可以通过匿名函数use来传入参数

	$callback = function($value, $key) use(&$data) {
		$userdata = 'afterRun';
	}
	array_walk($a,"myfunction3");
	echo $data; // 输出 afterRun
    ?>

函数array_map()
^^^^^^^^^^^
此函数跟 ``array_walk()`` 比更加灵活，并且可以处理多个数组。将回调函数作用到给定数组的元素上，返回用户自定义函数作用后的数组。 ``array_map()`` 是任意参数列表函数， **回调函数接受的参数数目应该和传递给array_map()函数的数组数目一致。** 函数格式如下：

``array array_map(callable $callback , array $array1 [, array $... ])``

第一个参数必选，用户自定义的函数名称，或者是null。第二个参数也是必选，输入要处理的数组，也可以输入多个数组作为可选参数。

- 如果第一个参数是自定义函数，则返回由函数返回值组成的一维数组；
- 如果第一个参数为null，且传入多个数组，则返回由多个数组组成的二维数组；

例子：

.. code-block:: php

    <?php
	function myfunction1($v)
	{
	        if ($v==="Dog")
	        {
	                return "Fido";
	        }
	        return $v;
	}
	$a=array("Horse","Dog","Cat");
	print_r(array_map("myfunction1",$a));
	// 输出：Array ( [0] => Horse [1] => Fido [2] => Cat )

	// 使用多个参数：
	function myfunction2($v1,$v2)
	{
	        if ($v1===$v2)
	        {
	                return "same";
	        }
	        return "different";
	}
	$a1=array("Horse","Dog","Cat");
	$a2=array("Cow","Dog","Rat");
	print_r(array_map("myfunction2",$a1,$a2));
	// 输出：Array ( [0] => different [1] => same [2] => different )

	// 请看当自定义函数名设置为 null 时的情况：
	$a1=array("Dog","Cat");
	$a2=array("Puppy","Kitten");
	print_r(array_map(null,$a1,$a2));
	/* 输出：
	Array (
	        [0] => Array ( [0] => Dog [1] => Puppy )
	        [1] => Array ( [0] => Cat [1] => Kitten )
	)
	*/
    ?>

数组的排序函数
-------------
在PHP中提供了很多函数可以对数组进行排序，这些函数提供了多种排序的方法。可以通过元素的值或键以及自定义排序。

+-------------------+----------------------------------------------------------------+
| 排序函数          | 说明                                                           |
+===================+================================================================+
| sort()            | 按由小到大的升序对给定数组的值排序                             |
+-------------------+----------------------------------------------------------------+
| rsort()           | 按由大到小的降序对给定数组的值排序                             |
+-------------------+----------------------------------------------------------------+
| usort()           | 使用用户自定义的比较函数对数组中的值进行排序                   |
+-------------------+----------------------------------------------------------------+
| asort()           | 对数组单元从低到高进行排序并保持索引关系                       |
+-------------------+----------------------------------------------------------------+
| arsort()          | 对数组单元从高到低进行排序并保持索引关系                       |
+-------------------+----------------------------------------------------------------+
| uasort()          | 使用用户自定义的比较函数对数组中的值进行排序并保持索引关系     |
+-------------------+----------------------------------------------------------------+
| ksort()           | 对数组单元按照键名从低到高进行排序                             |
+-------------------+----------------------------------------------------------------+
| krsort()          | 对数组单元按照键名从高到低进行排序                             |
+-------------------+----------------------------------------------------------------+
| uksort()          | 使用用户自定义的比较函数对数组中的键名进行排序                 |
+-------------------+----------------------------------------------------------------+
| natsort()         | 用“自然排序”算法对数组排序并保持索引关系                       |
+-------------------+----------------------------------------------------------------+
| natcasesort()     | 用“自然排序”算法对数组进行不区分大小写字母的排序并保持索引关系 |
+-------------------+----------------------------------------------------------------+
| array_multisort() | 对多个数组或多维数组进行排序                                   |
+-------------------+----------------------------------------------------------------+

简单的数组排序函数
^^^^^^^^^
简单的数组排序，是对一个数组元素的值进行排序，PHP的sort()函数和rsort()函数实现了这个功能。这两个函数既可以按数字大小排列也可以按照字母顺序排列，并具有相同的参数列表。语法格式如下：

.. code-block:: php

    <?php
	bool sort(array &array[,int sort_flags])
	bool rsort(array &array[,int sort_flags])
    ?>

第一个参数是必须的。后一个参数是可选的，给出了排序的方式，可以用以下值改变排序的行为。

- SORT_REGULAR:是默认值，将自动识别数组元素的类型进行排序。
- SORT_NUMERIC:用于数字元素的排序。
- SORT_STRING:用于字符串元素的排序。
- SORT_LOCALE_STRING:根据当前的locale设置来把元素当做字符串比较。

sort()函数对数组中的元素值按照由小到大顺序进行排序，rsort()函数则按照由大到小的顺序对元素的值进行排序。

例子：

.. code-block:: php

    <?php
	$data = array(5,8,1,7,2);

	sort($data);
	print_r($data); //输出：Array([0]=>1 [1]=>2 [2]=>5 [3]=>7 [4]=>8)

	rsort($data);
	print_r($data); //输出：Array([0]=>8 [1]=>7 [2]=>5 [3]=>2 [4]=>1)
    ?>

根据键名对数组排序
^^^^^^^^^^^^^^^^
当我们使用数组时，经常会根据键名对数组重新排序，ksort()函数和krsort()函数实现了这个功能。ksort函数对数组按照键名进行由小到大的排序，krsort()函数和ksort()函数相反，排序后为数组值保留原来的键。

例子：

.. code-block:: php

    <?php
	$fruits = array("d"=>"lemon", "a"=>"orange", "b"=>"banana", "c"=>"apple");
	ksort($fruits);
	// Array ( [a] => orange [b] => banana [c] => apple [d] => lemon )
	print_r($fruits);
    ?>

根据元素的值对数组排序
^^^^^^^^^^^^^^^^^^^^
如果你想使用数组中元素的值进行排序来取代键值排序，PHP也能满足你的要求。你只要使用asort()函数来代替先前提到的ksort()函数就可以了，如果按照从大到小排序，可以使用arsort()函数。前面介绍过简单的排序函数sort()函数和rsort()函数，也是根据元素的值对数组进行排序，但原始键名将被忽略，而依序使用数字重新索引数组的下标。而asort()函数和arsort()函数将保留原有键名和值得关系。

例子：

.. code-block:: php

    <?php
	$fruits = array("d" => "lemon", "a" => "orange", "b" => "banana", "c" => "apple");
	asort($fruits);
	print_r($fruits);
	// 输出：Array([c] => apple [b] => banana [d] => lemon [a] => orange)
    ?>

根据"自然排序"法对数组排序
^^^^^^^^^^^^^^^^^^^^^^^^
PHP有一个非常独特的排序方式，这种方式使用认知而不是使用计算规则，这种特性成为“自然排序法”，即数字从1到9的排序方法，字母从a到z的排序方法，短者优先。当创建模糊逻辑应用软件时这种排序方式非常有用。可以使用natsort()进行“自然排序”法的数组排序，该函数的排序结果时忽略键名的。函数natcasesort()是用“自然排序”算法对数组进行不区分大小写字母的排序。

例子：

.. code-block:: php

    <?php
	$data = array("file1.txt","file11.txt","File2.txt","FILE12.txt","file.txt");

	natsort($data); //普通的自然排序
	print_r($data); //输出排序后的结果，数组中包括大小写，输出不是正确的排序结果
	// 输出结果：Array([3] => FILE12.txt [2] => File2.txt [4] => file.txt [0] => file1.txt [1] => file11.txt)
	natcasesort($data); //忽略大小写的“自然排序”
	print_r($data); //输出“自然排序”后的结果，正常结果
	// 输出结果：Array([4] => file.txt [0] => file1.txt [2] => File2.txt [1] => file11.txt [3] => FILE12.txt)
    ?>

根据用户自定义的规则对数组排序
^^^^^^^^^^^^^^^^^^^^^^^^^^^
PHP提供了可以通过创建你自己的比较函数作为回调函数的数组排序函数，包括usort()、uasort()和uksort等函数。他们的使用格式一样，并具有相同的参数列表，区别在于对键还是值进行排序。其函数原型分别如下：

.. code-block:: php

    <?php
	bool usort(array &array,callback cmp_function)
	bool uasort(array &array,callback cmp_function)
	bool ursort(array &array,callback cmp_function)
    ?>

这三个函数将用用户自定义的比较函数对一个数组中的值进行排序。如果要排序的数组需要用一种不寻常的标准进行排序，那么应该使用这几个函数。在自定义的回调函数中，需要两个参数，分别依次传入数组中连续的两个元素。比较函数必须在第一个参数被认为小于、等于或大于第二个参数时分别返回一个小于，等于或大于零的整数。在下面的例子中就根据数组中元素的长度对数组进行排序，最短项放在最前面。

例子：

.. code-block:: php

    <?php
	//声明一个数组，其中元素值得长度不相同
	$lamp = array("Linux","Apache","MySQL","php");

	//使用usort()函数传入用户自定义的回调函数进行数组排序
	usort($lamp,"sortByLen");
	print_r($lamp);

	//自定义的函数作为回调函数提供给usort()函数使用，声明排序规则
	function sortByLen($one,$two){
	//如果两个参数长度相等返回0，在数组中的位置不变
	    if(strlen($one) == strlen($two))
	            return 0;
	    else
	//第一个参数大于第二个参数返回大于0的数，否则返回小于0的数
	            return (strlen($one)>strlen($two)) ? 1 : -1;
	}
	//运行结果 Array([0]=>php [1]=MySQL [2]=>Linux [3]=>Apache)
    ?>

多维数组的排序
^^^^^^^^^^^^^
PHP也允许在多维数组上执行一些比较复杂的排序。例如，首先对一个嵌套数组使用一个普通的键值进行排序，然后再根据另一个键值进行排序。这与使用SQL的ORDER BY语句对多个字段进行排序非常相似。可以使用array_multisort()函数对 **多个数组或多维数组进行排序** ，或者根据某一维或多维对多维数组进行排序。

``bool array_multisort(array &$array1 [, mixed $array1_sort_order = SORT_ASC [, mixed $array1_sort_flags = SORT_REGULAR [, mixed $... ]]])``

 关联（string）键名保持不变，但数字键名会被重新索引。

参数：

http://php.net/manual/zh/function.array-multisort.php

例子：

.. code-block:: php

    <?php
	//声明一个$data数组，模拟了一个行和列数组
	$data = array(
	        array("id"=>1,"soft"=>"Linux","rating"=>3),
	        array("id"=>2,"soft"=>"Apache","rating"=>1),
	        array("id"=>3,"soft"=>"MySQL","rating"=>4),
	        array("id"=>4,"soft"=>"PHP","rating"=>2),
	);

	//foreach遍历创建两个数组$soft和rating，作为array_multisort的参数
	foreach($data as $key =>$value){
	        $soft[$key] = $value ["soft"]; //将$data中的每个数组元素中键值为soft的值形成数组$soft
	        $rating[$key] = $value["rating"]; //将每个数组元素中键值为rating的值形成数组$rating
	}

	array_multisort($rating,$soft,$data); //使用array_multisort()函数传入三个数组进行排序
	print_r($data); //输出排序后的二维数组
	/*运行结果
	array(
	[0]=>Array([id]=>2 [soft]=>Apache [rating]=>1)
	[1]=>Array([id]=>4 [soft]=>PHP [rating]=>2)
	[2]=>Array([id]=>1 [soft]=>Linux [rating]=>3)
	[3]=>Array([id]=>3 [soft]=>MySQL [rating]=>4)
	)
	*/
    ?>

拆分、合并、分解和接合数组
------------------------
数组处理函数能够完成一些更复杂的数组处理任务，可以把数组作为一个集合处理。例如，对两个货多个数组进行合并，计算数组间的差集或交集，从数组元素中提取一部分，以及完成数组的比较。

函数array_slice()
^^^^^^^^^^^^^^
``array_slice()`` 函数的作用是在数组中根据条件取出一段值并返回。如果数组有字符串键，所返回的数组将保留键名。该函数可以设置4个参数，其函数的原型如下：

``array array_slice ( array $array , int $offset [, int $length = NULL [, bool $preserve_keys = false ]] )``


参数:

- array：输入的数组。
- offset：如果 offset 非负，则序列将从 array 中的此偏移量开始。如果 offset 为负，则序列将从 array 中距离末端这么远的地方开始。
- length：如果给出了 length 并且为正，则序列中将具有这么多的单元。如果给出了 length 并且为负，则序列将终止在距离数组末端这么远的地方。(这时的length表示一个位置)如果省略，则序列将从 offset 开始一直到 array 的末端。
- preserve_keys：注意 array_slice() 默认会重新排序并重置数组的数字索引。你可以通过将 preserve_keys 设为 TRUE 来改变此行为。如果为TRUE值则所返回的数组将保留键名。

返回值：

- 返回其中一段。 如果 offset 参数大于 array 尺寸，就会返回空的 array。

.. code-block:: php

    <?php
	$lamp=array("Linux", "Apache", "MySQL", "PHP");
	//使用array_slice()从第二个开始取(0是第一个，1是第二个)，取两个元素从数组$lamp中返回
	print_r(array_slice($lamp, 1,2));  //输出Array([0]=>Apache [1]=>MySQL)

	//第二个参数使用负数-2，从后面第二个开始取，返回一个元素
	print_r(array_slice($lamp, -2,1));        //输出Array([0]=>MySQL)

	//最后一个参数设置为true，保留原有的键值返回
	print_r(array_slice($lamp, 1,2, true));      //输出Array([1]=Apache  [2]=>MySQL)

	$lamp=array("a"=>"Linux", "b"=>"Apache", "c"=>"MySQL", "d"=>"PHP");

	//如果数组中有字符串键，默认返回的数组将保留键名
	print_r(array_slice($lamp, 1,2));       //输出Array([b]=>Apache [c]=>MySQL)

	//第三个参数使用负数-2，表示截取到从倒数第2个(但不包括)
	print_r(array_slice($lamp, 1,-2));       //输出Array([b]=>Apache
    ?>

函数array_splice()
^^^^^^^^^^^^^^
``array_splice()`` 函数与 ``array_slice()`` 函数类似，选择数组中的一系列元素，但不返回，而是删除它们并用其他值代替。如果提供了第四个参数，则之前选中的那些元素将备第四个参数指定的数组取代。最后生成的数组将会返回。其函数原型如下：

``array array_splice(array &$input , int $offset [, int $length = count($input) [, mixed $replacement = array() ]])``

参数：

- input：输入的数组。
- offset：如果 offset 为正，则从 input 数组中该值指定的偏移量开始移除。如果 offset 为负，则从 input 末尾倒数该值指定的偏移量开始移除。
- length：如果省略 length，则移除数组中从 offset 到结尾的所有部分。如果指定了 length 并且为正值，则移除这么多单元。如果指定了 length 并且为负值，则移除从 offset 到数组末尾倒数 length 为止中间所有的单元。 如果设置了 length 为零，不会移除单元。 小窍门：当给出了 replacement 时要移除从 offset 到数组末尾所有单元时，用 count($input) 作为 length。
- replacement：如果给出了 replacement 数组，则被移除的单元被此数组中的单元替代。

如果 offset 和 length 的组合结果是不会移除任何值，则 replacement 数组中的单元将被插入到 offset 指定的位置。 注意替换数组中的键名不保留。

如果用来替换 replacement 只有一个单元，那么不需要给它加上 array()，除非该单元本身就是一个数组、一个对象或者 NULL。

返回值：

- 返回一个包含有被移除单元的数组。

.. code-block:: php

    <?php
	$input = array("Linux", "Apache", "MySQL", "PHP");
	//原数组中的第二个元素后到数组结尾都被删除
	array_splice($input,2);
	print_r($input); //输出Array([0]=>Linux [1]=>Apache)

	$input = array("Linux", "Apache", "MySQL", "PHP");
	//从第二个元素开始移除直到数组末尾倒数第一个为止中间所有的元素
	array_splice($input,1,-1);
	print_r($input); //输出Array([0]=>Linux [1]=>php)

	$input = array("Linux", "Apache", "MySQL", "PHP");
	//从第二个元素到数组结尾都被第四个参数替代
	array_splice($input,1,count($input),"web");
	print_r($input); //输出Array([0]=>Linux [1]=>web)

	$input = array("Linux", "Apache", "MySQL", "PHP");
	//最后一个元素被第四个参数数组替代
	array_splice($input,-1,1,array("web","www"));
	print_r($input); //输出Array([0]=>Linux [1]=>Apache [2]=>MySQL [3]=>web [4]=>www)
    ?>

函数array_combine()
^^^^^^^^^^^^^^^^^^
array_combine()函数的作用是通过合并两个数组来创建一个新数组。其中一个数组是键名，另一个数组的值为键值。如果其中一个数组为空，或者两个数组的元素个数不同，则函数返回false。其函数原型如下：

``array array_combine(array $keys , array $values)``

该函数有两个参数且都是必选项，两个参数必须有相同数目的元素。

例子：

.. code-block:: php

    <?php
	$a1 = array("OS","WebServer","DataBase","Language");  //声明第一个数组作为参数1
	$a2 = array("Linux","Apache","MySQL","PHP");  //声明第二个数组作为参数2

	print_r(array_combine($a1,$a2));   //使用array_combine()将两个数组合并
	//输出Array([OS]=>Linux [WebServer]=>Apache [DataBase]=>MySQL [Language]=>PHP)
    ?>

函数array_merge()
^^^^^^^^^^^^^^^^
array_merge() 函数的作用是把一个或多个数组合并为一个数组。如果键名有重复，该键的值为最后一个数组键名对应的值（后面的覆盖前面的）。如果数组是数字索引的，则键名会以连续方式重新索引，也就是说不会合并数字键相同的元素，而是按数字顺序附加。这里要注意如果仅仅向 array_merge() 函数输入一个数组，且键名是整数，则该函数将返回带有整数键名的新数组，其键名以 0 开始进行重新索引。其函数的原型如下：

``array array_merge(array $array1 [, array $... ])``

该函数第一个参数是必选项，需要传入一个数组。可以有多个可选参数，但必须都是数组类型的数据。返回将多个数组合并后的新数组。

例子：

.. code-block:: php

    <?php
	$a1 = array("a"=>"Linux","b"=>"Apache");
	$a2 = array("c"=>"MySQL","b"=>"PHP");

	print_r(array_merge($a1,$a2));   //输出array([a]=>Linux [b]=>php [c]=>MySQL)

	//仅使用一个数组参数则键名以0开始进行重新索引
	$a = array(3=>"php",4=>"MySQL");
	print_r(array_merge($a));    //输出array([0]=>PHP [1]=>MySQL)
    ?>

区分 + 和 array_merge()区别
""""""""""""""""""""""""""
- ``array_merge()`` ：如果数组包含数字键名，后面的值将不会覆盖原来的值，而是附加到后面。如果只给了一个数组并且该数组是数字索引的，则键名会以连续方式重新索引。
- ``+`` ：在两个数组中存在相同的键名时，第一个数组中的同键名的元素将会被保留，第二个数组中的元素将会被忽略。

.. code-block:: php

	$a1 = [
	    0 => 'a',
	    1 => 'b',
	    2 => 'c',
	    'key1' => 'value1',
	    'key2' => 'value2',
	];
	$a2 = [
	    0 => 'e',
	    1 => 'f',
	    2 => 'g',
	    3 => 'h',
	    'key1' => 'value3',
	    'key2' => 'value4',
	    'key3' =>'value5'
	];
	print_r(array_merge($a1, $a2));
	print_r($a1 + $a2);

输出结果：

.. code-block:: shell

	Array
	(
	    [0] => a
	    [1] => b
	    [2] => c
	    [key1] => value3
	    [key2] => value4
	    [3] => e
	    [4] => f
	    [5] => g
	    [6] => h
	    [key3] => value5
	)
	Array
	(
	    [0] => a
	    [1] => b
	    [2] => c
	    [key1] => value1
	    [key2] => value2
	    [3] => h
	    [key3] => value5
	)

函数array_intersect()
^^^^^^^^^^^^^^^^^^^^^
array_intersect()函数的作用是计算数组的交集。返回的结果数组中包含了所有在被比较数组中，也同时出现在所有其他参数数组中的值，键名保留不变，仅有值用于比较。其函数的原型如下：

``array array_intersect(array $array1 , array $array2 [, array $... ])``

该函数第一个参数是必选项，与其他数组进行比较的第一个数组。第二个参数也是必选项，与第一个数组进行比较的数组。可以有多个可选参数作为以后的参数，与第一个数组进行比较的数组。

例子：

.. code-block:: php

    <?php
	$a1 = array("Linux", "Apache", "MySQL", "PHP");
	$a2 = array("Linux", "Tomcat", "MySQL", "JSP");

	print_r(array_intersect($a1,$a2));   //输出array([0]=>Linux [2]=>MySQL)
    ?>

函数array_diff()
^^^^^^^^^^^^^^^
array_diff()函数的作用是返回两个数组的差集数组(值不相同的元素)。该数组包括了所有在被比较的数组中，但是不在任何其他参数数组中的元素值。在返回的数组中，键名保持不变。其函数的原型如下：

``array array_diff(array $array1 , array $array2 [, array $... ])``

第一个参数是必选项，传入与其他数组进行比较的数组。第二个参数也是必选项，传入与第一个数组进行比较的数组。第三个参数以后都是可选项，可用一个或任意多个数组与第一个数组进行比较。本函数仅有值用于比较。

例子：

.. code-block:: php

    <?php
	$a1 = array("Linux", "Apache", "MySQL", "PHP");
	$a2 = array("Linux", "Tomcat", "MySQL", "JSP");

	print_r(array_diff($a1,$a2));  //输出array([1]=>Apache [3]=>PHP)
    ?>

其它常用的数组函数
----------------

函数array_rand()
^^^^^^^^^^^^
array_rand()函数从数组随机选出一个或多个元素，并返回。该函数有两个参数，其函数原型如下：

``mixed array_rand(array $array [, int $num = 1 ])``

第二个参数用来确定要选出几个元素。如果只取出一个，array_rand() 返回随机单元的键名。 否则就返回包含随机键名的数组。 完成后，就可以根据随机的键获取数组的随机值。 取出数量如果超过 array 的长度，就会导致 E_WARNING 错误，并返回 NULL。

例子：

.. code-block:: php

    <?php
	$a=array("a"=>"Dog","b"=>"Cat","c"=>"Horse");
	print_r(array_rand($a,1));  // 输出 b

	$a=array("a"=>"Dog","b"=>"Cat","c"=>"Horse");
	print_r(array_rand($a,2));  // 输出 Array ( [0] => c [1] => b )
    ?>

函数shuffle()
^^^^^^^^^^^
shuffle()函数把数组中的元素按随机顺序重新排列，即将数组中的顺序打乱。若成功则返回TRUE，否则返回FALSE。

 本函数为数组中的单元赋予新的键名。这将删除原有的键名而不仅是重新排序。

例子：

.. code-block:: php

    <?php
	$my_array = array("a" => "Dog", "b" => "Cat", "c" => "Horse");
	shuffle($my_array);
	print_r($my_array); // 输出：Array ( [0] => Cat [1] => Horse [2] => Dog )
    ?>

函数array_sum()
^^^^^^^^^^^
array_sum()函数返回数组中所有值的总和。该函数也非常容易使用，只需要传入一个数组作为必选参数即可。如果所有值都是整数，则返回一个整数值，如果其中有一个或多个值是浮点数，则返回浮点数。

.. code-block:: php

    <?php
	$a = array(0 => "5", 1 => "15", 2 => "25");
	echo array_sum($a); // 输出45
    ?>

函数range()
^^^^^^^
range()函数创建并返回一个包含指定范围的元素的数组。函数格式如下：

``array range(mixed $start , mixed $end [, number $step = 1 ])``

参数：

- start：序列的第一个值。
- end：序列结束于 end 的值。
- step：如果设置了步长 step，会被作为单元之间的步进值。step 应该为正值。不设置step 则默认为 1。

返回值：

- 返回的数组中从 start 到 end （含 start 和 end）的单元。

例子：

.. code-block:: php

    <?php
	$number = range(0,5); //使用range()函数声明一个元素值为0-5的数组
	print_r($number); //输出Array([0]=>0 ,[1]=>1 ,[2]=>2 ,[3]=>3 ,[4]=>4 ,[5]=>5 )

	$number = range(0,50,10); //使用range()函数声明元素值为0-50的数组，每个元素之间的步长为10

	$letter =range ("a","d"); //还可以使用range()函数声明元素的字母数组，声明字母a-d的数组
    ?>