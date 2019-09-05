字符串操作
=========
如果字符串处理函数和正则表达式都可以实现字符串操作，建议使用字符串处理函数来完成，因为字符串的处理函数要比正则表达式处理字符串的效率高。但对于很多复杂的字符串操作，只有通过正则表达式才能完成。

字符串的连接
-----------
1. 使用连接符；
   半角句号“.”是字符串连接符，可以把两个或两个以上的字符串连接成一个字符串。

   .. code-block:: php

       <?php
		$name = "明日编程词典：";
		$url = "www.mrbccd";
		echo $name.$url.".com";
       ?>

2. 直接写入字符串变量到双引号字符串中；应用字符串连接符号无法实现大量字符串的连接，PHP允许程序员在双引号中直接包含字符串变量。

   .. code-block:: php

	    <?php
		$name = "明日编程词典：";
		$url = "www.mrbccd";
		echo "$name$url.com";
	    ?>

去除字符串首尾空格和特殊字符
-------------------------
在PHP中提供了trim()函数去除字符串左右两边的空格和特殊字符、ltrim()函数去除字符串左边的空格和特殊字符、rtrim()函数去除字符串右边的空格和特殊字符。

trim()函数
^^^^
trim()函数用于去除字符串首尾空格和特殊字符，并返回去掉空格和特殊字符后的字符串。

``string trim(string str[,string charlist]);``

- str是要操作的字符串对象；
- 参数charlist为可选参数，指定需要从字符串中删除那些字符，如果不设置该参数，则所有的可选字符(空格、制表符、换行符、回车符、垂直制表符等)都将被删除。

trim()函数的参数charlist的可选值如下：

- ""：(ASCII 32 (0x20)), 即空格
- "\t"：(ASCII 9 (0x09)), 即制表符Tab
- "\n"：(ASCII 10 (0x0A)), 即新行
- "\r"：(ASCII 13 (0x0D)), 即回车
- "\0"：(ASCII 0 (0x00)), 即NULL
- "\x0B"：(ASCII 11 (0x0B)), 即垂直制表符

此外还可以使用“..”符号指定需要去除的一个范围，例如“0..9”或“a..z”表示去掉ASCII码值中的数字和小写字母。

.. code-block:: php

    <?php
	$str = "   lamp  "; // 声明一个字符串，其中左侧有三个空格，右侧两个空格，总长度为九个字符
	echo strlen($str); // 输出字符串的总长度9
	echo strlen(ltrim($str)); // 去掉左侧空格后长度输出6
	echo strlen(rtrim($str)); // 去掉右侧空格后长度输出7
	echo strlen(trim($str)); // 去掉两侧空格后的长度输出4

	$str = "123This is a test...";
	echo ltrim($str, "0..9"); // 过滤掉字符串左侧的数字，输出 This is a test...
	echo rtrim($str, "."); //过滤掉字符串右侧的所有“.”，输出123This is a test
	echo trim($str, "0..9A..Z."); // 过滤掉字符串两端的数字和大写字母还有“.”，输出 his is a test
    ?>

不仅可以按需求过滤掉字符串中的内容，还可以使用 ``str_pad()`` 函数按需求对字符串进行填补。可以用于对一些敏感信息的保护，例如数据的对并排列等。其函数的原型如下所示：

``string str_pad(string input,int pad_length[,string pad_string[,int pad_type]])``

该函数有4个参数，第一个参数指明要处理的字符串。第二个参数给定处理后字符串的长度，如果该值小于原始字符串的长度，则不进行任何操作。第三个参数指定填补时所用的字符串，它为可选参数，如果没有指定则默认使用空格填补。最后一个参数指定填补的方向，它有三个可选值：STR_PAD_BOTH、STR_PAD_LEFT和STR_PAD_RIGHT，分别代表在字符串两端、左和右进行填补。也是一个可选参数，如果没有指定，则默认值是STR_PAD_RIGHT。函数str_pad()的使用代码如下所示：

.. code-block:: php

    <?php
	$str = "LAMP";
	echo str_pad($str,10); //指定长度为10，默认使用空格在右边填补“LAMP”
	echo str_pad($str,10,"-=",STR_PAD_LEFT); //指定长度为10，指定在左边填补“-=-=-=LAMP”
	echo str_pad($str,10,"_",STR_PAD_BOTH); //指定长度为10，指定在左边填补“___LAMP___”
    ?>

转义、还原字符串数据
------------------
字符串转义、还原的方法有两种：

- 一种是手动转义、还原字符串数据；
- 另一种是自动转义、还原字符串数据。

手动转义、还原字符串数据
^^^^^^^^^^^^
字符串可以用单引号（'）、双引号（""）、定界符（<<<）3种方法定义。而指定一个简单字符串的最简单的方法是用单引号（'）括起来。当使用字符串时，很可能在该串中存在这几种符号与PHP脚本混淆的字符，因此必须要做转义语句。这就要在它的前面使用转义符号“\\”。
“\\”是一个转义符，紧跟在“\\”后面的第一个字符将变得没有意义或有特殊意义。如(')是字符串的定界符，写为 ``\'`` 时就失去了定界符的意义，变为了普通的单引号(')。读者可以通过 ``echo '\'';`` 输出一个单引号(')，同时转义字符"\\"也不会显示。

对于简单的字符串建议采用手动方法进行字符串转义，而对于数据量较大的字符串，建议采用自动转义函数实现字符串的转义。说明：手动转义字符串可应用addcslashes()函数进行字符串还原，其具体的实现方法将在下面进行介绍。

自动转义、还原字符串数据
^^^^^^^^^^^^
自动转义、还原字符串数据可以应用PHP提供的 ``addslashes()`` 函数和 ``stripslashes()`` 函数实现。

- addslashes()函数

  addslashes()函数用来为字符串str加入斜线“\\”。
  语法格式如下：

  ``string addslashes (string str)``
- stripslashes()函数

  stripslashes()函数用来将使用addslashes()函数转义后的字符串str返回原样。
  语法格式如下：

  ``string stripslashes(string str)``

 技巧：所有数据在插入数据库之前，有必要应用addslashes()函数进行字符串转义，以免特殊字符未经转义在插入数据库时出现错误。另外，对于使用addslashes()函数实现的自动转义字符串可以使用stripcslashes()函数进行还原，但数据在插入数据库之前必须再次进行转义。

以上两个函数实现了对指定字符串进行自动转义和还原。除了上面介绍的方法外，还可以对要转义、还原的字符串进行一定范围的限制，通过使用addcslashes()函数和stripcslashes()函数实现对指定范围内的字符串进行自动转义、还原。下面分别对两个函数进行详细介绍。

- addcslashes()函数

  实现转义字符串中的字符，即在指定的字符charlist前加上反斜线。
  语法格式如下：

  ``string addcslashes (string str, string charlist)``
  
  参数说明：
   参数str为将要被操作的字符串，参数charlist指定在字符串中的哪些字符前加上反斜线“\\”，如果参数charlist中包含\\n、\\r等字符，将以C语言风格转换，而其他非字母数字且ASCII码低于32以及高于126的字符均转换成八进制表示。

    注意：在定义参数charlist的范围时，需要明确在开始和结束的范围内的字符。
- stripcslashes()函数
  
  stripcslashes()函数用来将应用addcslashes()函数转义的字符串str还原。语法格式如下：
　　
  ``string stripcslashes (string str)``

.. code-block:: php

    <?php
	$a = "编程体验网";
	echo $a;
	print PHP_EOL;
	$b = addcslashes($a, "编程体验网");
	echo $b;
	print PHP_EOL;
	$c = stripcslashes($b);
	echo $c;
    ?>

PHP EOF(heredoc)
----------------
PHP EOF定义一个字串的方法。

使用概述：

1. 必须后接分号，否则编译通不过。
2. ``EOF`` 可以用任意其它字符代替，只需保证结束标识与开始标识一致。
3. 结束标识必须顶格独自占一行(即必须从行首开始，前后不能衔接任何空白和字符)。
4. 开始标识可以不带引号或带单双引号，不带引号与带双引号效果一致，解释内嵌的变量和转义符号，带单引号则不解释内嵌的变量和转义符号。
5. 当内容需要内嵌引号（单引号或双引号）时，不需要加转义符，本身对单双引号转义。

.. code-block:: php

	<?php
	echo <<<EOF
	    <h1>我的第一个标题</h1>
	    <p>我的第一个段落。</p>
	EOF;
	// 结束需要独立一行且前后不能空格
	?>

1. 以 ``<<<EOF`` 开始标记开始，以 ``EOF`` 结束标记结束，结束标记必须顶头写，不能有缩进和空格，且在结束标记末尾要有分号 。
2. 开始标记和结束标记相同，比如常用大写的 ``EOT`` 、 ``EOD`` 、 ``EOF`` 来表示，但是不只限于那几个(也可以用： ``JSON`` 、 ``HTML`` 等)，只要保证开始标记和结束标记不在正文中出现即可。
3. 位于开始标记和结束标记之间的变量可以被正常解析，但是函数则不可以。在 ``heredoc`` 中，变量不需要用连接符 ``.`` 或 ``,`` 来拼接，如下：

.. code-block:: php

	<?php
	$name="runoob";
	$a= <<<EOF
	    "abc"$name
	    "123"
	EOF;
	// 结束需要独立一行且前后不能空格
	echo $a;

获取字符串的长度
---------------
获取字符串的长度使用的是strlen()函数，下面重点讲解strlen()函数的语法及其应用。

strlen()函数主要用于获取指定字符串str的长度。语法格式如下：

``int strlen(string str)``

strlen()函数在获取字符串长度的同时，也可以用来检测字符串的长度。

 说明：汉字占两个字符，数字、英文、小数点、下划线和空格占一个字符。

字符串大小写转换函数
----------
在PHP中提供了四个字符串大小写的转换函数：

- 函数strtoupper()用于将给定的字符串全部转换为大写字母；
- 函数strtolower()用于将给定的字符串全部转换为小写字母；
- 函数ucfirst()用于将给定的字符串中首字母转换为大写，其余字符不变；
- 函数ucwords()用于将给定的字符串中全部以空格分隔的单词首字母转换为大写；

.. code-block:: php

    <?php
	$lamp = "lamp is composed of Linux、Apache、MySQL and PHP";
	echo strtolower($lamp); // 输出lamp is composed of linux、apache、mysql and php
	echo strtoupper($lamp); // 输出LAMP IS COMPOSED OF LINUX、APACHE、MYSQL AND PHP
	echo ucfirst($lamp); // 输出Lamp is composed of Linux、Apache、MySQL and PHP
	echo ucwords($lamp); // 输出Lamp Is Composed Of Linux、Apache、MySQL And PHP
    ?>

截取字符串
---------
PHP对字符串截取可以采用PHP预定义函数substr()实现。语法格式如下：

``string substr(string str, int start[,int length])``

- 参数str为要操作的字符串
- 参数start为你要截取的字符串的开始位置，若start为负数时，则表示从倒数第start开始截取length个字符
- 可选参数length为你要截取的字符串长度，若在使用时不指定则默认取到字符串结尾。若length为负数时，则表示从start开始向右截取到末尾倒数第length个字符的位置(当length为负数时，不再表示长度而是位置了)

.. code-block:: php

    <?php
	// 输出abcdefg
	echo substr("abcdefg",0);              //从第0个字符开始截取
	// 输出cdef
	echo substr("abcdefg",2,4);             //从第2个字符开始连续截取4个字符
	// 输出de
	echo substr("abcdefg",-4,2);             //从倒数第4个字符开始截取2个字符
	// 输出abc
	echo substr("abcdefg",0,-4);             //从第0个字符开始截取,截取到倒数第4个字符，当length为负数时，不再表示长度而是位置了
    ?>

在开发一些web程序时，为了保持整个页面的合理布局，经常需要对于一些超长的文本，只要求显示其中的部分信息。下面来通过具体的实例讲解一下其实现的方法。

使用substr()函数截取超长文本的部分字符串信息，剩余的部分使用"....."来代替，示例代码如下：

.. code-block:: php

    <?php
	$str="沪深证券交易所已发布“高送转”信息披露指引，实施“刨根问底”问询，开展对“高送转”内幕交易核查联动，集中查办了一批借“高送转”之名从事内幕交易或信息披露违规案件。";
	if(strlen($str)>30){  //如果文本的字符串长度大于30
	        echo substr($str,0,30). "....";   //输出文本的前30个字节，然后输出省略号
	}else{
	        echo $str;    // 如果文本的字符串长度小于30，直接输出原文本
	}
    ?>

从指定的字符串中按照指定的位置截取一定长度的字符。通过substr()函数可以获取某个固定格式字符串中的一部分。

 注意： 使用substr()函数在截取字符串时，如果截取的字符串个数是奇数(偶数有时也不行)，那么就会导致截取的中文字符串出现乱码，所以substr()函数适用于对英文字符串的截取，如果想要对中文字符串进行截取，而且要避免出现乱码，最好的方法就是应用substr()函数编写一个自定义函数。

比较字符串
---------
在PHP中，对字符串之间进行比较的方法有很多种：

- 第一种是使用strcmp()函数和strcasecmp()函数按照字节进行比较；
- 第二种是使用strnatcmp()函数按照自然排序法进行比较；
- 第三种是使用strncmp()函数指定比较字符串开头的若干个字符；

按字节进行字符串的比较
^^^^^^^^^^^^^^^^^^^^
按字节进行字符串比较的方法有两种，分别是strcmp()和strcasecmp()函数，通过这两个函数即可实现对字符串进行按字节的比较。这两种函数的区别是strcmp()函数区分字符的大小写，而strcasecmp()函数不区分字符的大小写。

strcmp()函数的语法格式如下：

``int strcmp ( string str1, string str2)``

参数str1和参数str2指定要比较的两个字符串。如果相等则返回0；如果参数str1大于参数str2则返回值大于0；如果参数str1小于参数str2则返回值小于0。

按自然排序法进行字符串的比较
^^^^^^^^^^^^^^^^^^^^^^^^^^
在PHP中，按照自然排序法进行字符串的比较是通过strnatcmp()函数来实现的。自然排序法比较的是字符串中的ASCII数字部分，将字符串中的数字按照大小进行排序。语法格式如下：

``int strnatcmp ( string str1, string str2)``

如果字符串相等则返回0，如果参数str1大于参数str2则返回值大于0；如果参数str1小于参数str2则返回值小于0。本函数区分字母大小写。

 注意：在自然运算法则中(自然排序)，2比10小，而在计算机序列中(字节比较)，10比2小，因为“10”中的第一个数字是“1”，它小于2。

指定从源字符串的位置开始比较
^^^^^^^^^^^^^^^^^^^^^^^^^^
strncmp()函数用来比较字符串中的前n个字符。语法格式如下：

``int strncmp ( string $str1 , string $str2 , int $len )``

- str1：指定参与比较的第一个字符串对象；
- str2：指定参与比较的第二个字符串对象；
- len：必要参数，指定每个字符串中参与比较字符的数量；

检索字符串
---------

使用strstr()函数查找指定的关键字(函数别名为strchr)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
获取一个指定字符串在另一个字符串中首次出现的位置到后者末尾的子字符串。如果执行成功，则返回剩余字符串（包含相匹配的字符）；如果没有找到相匹配的字符，则返回false。语法格式如下：

``string strstr ( string haystack, string needle)``

参数说明：

- haystack必要参数，指定从哪个字符串中进行搜索needle必要参数;
- 指定搜索的对象。如果该参数是一个数值，那么将搜索与这个数值的ASCII值相匹配的字符

使用substr_count()
^^^^^^^^^^^^^^^^^^
函数检索子串出现的次数获取指定字符在字符串中出现的次数。语法格式如下：

``int substr_count(string haystack,string needle)``

参数haystack是指定的字符串，参数needle为指定的字符。

strstr(strchr)、strrchr、substr、stristr四个函数的区别
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

strstr和strcchr的区别
""""""""""""""""""""
- strstr：显示第一次找到，要查找的字符串，以及后面的字符串。
- strrchr：显示最后一次找到，要查找的字符串，以及后面的字符串。

.. code-block:: php

    <?php
	$email = 'test@test.com@jb51.net';
	$domain = strstr($email, '@');
	echo "strstr 测试结果 $domain"; // 输出结果：strstr 测试结果 @test.com@jb51.net
	$domain = strrchr($email, '@');
	echo "strrchr 测试结果 $domain"; // 输出结果：strrchr 测试结果 @jb51.net
    ?>

strstr和stristr的区别
""""""""""""""""""""
- strstr是大小写敏感的。
- stristr是大小写不敏感的。

.. code-block:: php

    <?php
	$email = 'zhangYing@jb51.net';
	$domain = strstr($email, 'y');
	echo "strstr 测试结果 $domain";  // 输出结果 strstr 测试结果
	$domain = stristr($email, 'y');
	echo "stristr 测试结果 $domain"; // 输出结果 stristr 测试结果 Ying@jb51.net
    ?>

strstr和substr的区别
"""""""""""""""""""
- strsr是匹配后截取。
- substr是不匹配，根据起始位置，进行截取。

.. code-block:: php

    <?php
	$email = 'zhangYing@jb51.net';
	$domain = strstr($email, 'j');
	echo "strstr 测试结果 $domain"; // 输出strstr 测试结果 jb51.net
	$domain = substr($email,-8);
	echo "substr 测试结果 $domain"; // substr 测试结果 jb51.net
    ?>

替换字符串
---------
通过字符串的替换技术可以实现对指定字符串中的指定字符进行替换。字符串的替换技术可以通过以下两个函数实现：

- str_ireplace()和str_replace()函数
- substr_replace()函数

str_replace()函数
^^^^^^^^^^^^^^^^^^
使用新的子字符串（子串）替换原始字符串中被指定要替换的字符串语法格式如下：

``mixed str_replace ( mixed search, mixed replace, mixed
　　subject [, int &count])``

将所有在参数subject中出现的参数search以参数replace取代，参数&count表示取代字符串执行的次数。本函数区分大小写。参数说明：

- search必要参数，指定需要查找的字符串；
- replace必要参数，指定替换的值；
- subject必要参数，指定查找的范围；
- count可选参数，获取执行替换的次数；

substr_replace()函数
^^^^^^^^^^^^^^^^^^^^
对指定字符串中的部分字符串进行替换。语法格式如下：

``substr_replace(string,replacement,start,length)``

作用：substr_replace() 函数把字符串的一部分替换为另一个字符串。

- string 必需。指定要操作的原始字符串。
- replacement 必需。指定替换后的新字符串。
- start 必需。规定在字符串的何处开始替换。

  + 正数 - 在第 start 个偏移量开始替换
  + 负数 - 在从字符串结尾开始的第 start 个偏移量开始替换
  + 0 - 在字符串中的第一个字符处开始替换

- length 可选，指定要替换string长度。

  + 如果设定了这个参数并且为正数，表示 string 中被替换的子字符串的长度。
  + 如果设定为负数，它表示待替换的子字符串结尾处距离 string 末端的字符个数。
  + 如果没有提供此参数，那么它默认为 strlen( string ) （字符串的长度）。
  + 当然，如果 length 为 0，那么这个函数的功能为将 replacement 插入到 string 的 start 位置处。


格式化字符串
-----------
在PHP中，字符串的格式化方式有多种，按照格式化的类型可以分为字符串的格式化和数字的格式化，数字的格式化最为常用，本节将重点讲解数字格式化number_format()函数。

number_format()函数用来将数字字符串格式化。语法格式如下：

``string number_format(float number,[int
　　num_decimal_places],[string dec_seperator],string
　　thousands_ seperator)``

number_format()函数可以有一个、两个或是4个参数，但不能是3个参数。

- 如果只有一个参数number，number格式化后会舍去小数点后的值，且每一千就会以逗号（，）来隔开；
- 如果有两个参数，number格式化后会到小数点第num_decimal_places位，且每一千就会以逗号来隔开；
- 如果有4个参数number格式化后会到小数点第num_decimal_places位，dec_seperator用来替代小数点（.），thousands_seperator用来替代每一千隔开的逗号（，）

.. code-block:: php

    <?php
	$number = 123456789;
	echo number_format($number); //输出：123,456,789千位分隔的字符串
	echo number_format($number,2); //输出：123,456,789.00小数点后保留两位小数
	echo number_format($number,2,",","."); //输出123.456.789,00千位使用(.)分隔了，并保留两位小数
    ?>

分割字符串
---------
字符串的分割是通过explode()函数实现的。explode()函数按照指定的规则对一个字符串进行分割，返回值为数组。语法格式如下：

``array explode(string separator,string str,[int limit])``

参数说明：

- separator	必需。指定的分割的标识符。如果separator为空字符串，则将返回false;如果separator所包含的值在str中找不到，那么explode()函数将返回包含str单个元素的数组；
- string 必需。指定要被分割的字符串。
- limit	可选。如果设置了limit参数，则返回的数组包含最多limit个元素，而最后的元素将最多包含limit个元素，而最后的元素将包含str的剩余部分；如果limit是负数，则返回除了最后的-limit个元素外的所有元素。

合成字符串
---------
implode()函数可以将数组的内容组合成一个新字符串。语法格式如下：

``string implode(string glue, array pieces)``
　
参数说明：

- 参数glue是字符串类型，指定分隔符；
- 参数pieces是数组类型，指定要被合并的数组；

字符串输出函数
-------
常用的输出字符串函数：

- echo()：输出字符串；
- print()：输出一个或多个字符串；
- die()：输出一条消息，并退出当前脚本；
- printf()：输出格式化字符串；
- sprintf()：把格式化的字符串写入一个变量中；

函数echo()
^^^^^^
该函数用于输出一个或多个字符串，它的效率比其它字符串输出函数高。它其实不是一个函数，而是一个语言结构，因此你无需对其使用括号。语法格式如下：

``void echo(string arg1[,string...])``

该函数的参数可以为一个或多个要发送到输出的字符串，如果要传递一个以上的参数到此函数时，不能使用括弧来将参数围在里面。

.. code-block:: php

    <?php
	$str = "What`s LAMP?";
	echo $str;
	echo "<br>";
	echo $str."<br>Linux+Apache+MySQL+PHP<br>";

	echo "This
	        text
	        spans
	        multiple
	        lines<br>"; // 可以将一行文本换成多行输出，多行格式将会保留

	echo 'This','string','was','made','with multiple parameters<br>'; // 可以输出用逗号隔开的多个参数，不能使用括号括起来
    ?>

函数print()
^^^^^^^
该函数的功能和echo()一样，但它有返回值，若成功则返回1，失败则返回0。

函数die()
^^^^^
该函数是exit()函数的别名。如果参数是一个字符串，则该函数会在退出前输出它。如果参数是一个整数，这个值会被用做退出状态。退出状态的值在0到254之间。状态0用于成功地终止程序；状态255由PHP保留，不会被使用。

.. code-block:: php

    <?php
	$uri = "http://www.baidu.com";
	fopen($uri,"r") or die("Unable to connect to $url");
    ?>

函数printf()
^^^^^^^^
该函数用于格式化输出字符串。函数语法格式如下：

``int printf ( string $format [, mixed $args [, mixed $... ]] )``

- 返回输出字符串的长度;
- 第一个参数是使用的转换格式；
    + 格式如下: ``%['padding_character][-][width][.precision]type`` 
    + 所有的转换说明都是以%开始,如果想打印一个%符号,必须用%%。
    + 参数'padding_character是可选.它将被用来填充变量直至所指定的宽度。该参数的作用就在变量前面填充。默认的填充字符是一个空格，如果指定0或者空格，就不需要'单引号作为前缀,其他字符就必须指定'作为前缀。
    + 参数 - 是可选。它指左对齐,默认是右对齐。
    + 参数width是指被替换的变量的长度。
    + 参数precision表示以小数点开始。它指明小数点后要显示的位数。
    + 参数type 是类型码,请看下表:
	+------+-------------------------------------------------+
	| 类型 | 含义                                            |
	+======+=================================================+
	| b    | 解释为整数并作为二进制输出.                     |
	+------+-------------------------------------------------+
	| c    | 解释为整数并作为字符表示输出(ASCII码).          |
	+------+-------------------------------------------------+
	| d    | 解释为整数并作为整数输出.                       |
	+------+-------------------------------------------------+
	| f    | 解释为双精度并作为浮点数输出.                   |
	+------+-------------------------------------------------+
	| o    | 解释为整数并作为八进制数输出.                   |
	+------+-------------------------------------------------+
	| s    | 解释为字符串并为字符串输出.                     |
	+------+-------------------------------------------------+
	| u    | 解释为整数并作为非指定小数输出.                 |
	+------+-------------------------------------------------+
	| x    | 解释为整数并作为带有小写字母a-f的十六进制数输出 |
	+------+-------------------------------------------------+
	| X    | 解释为整数并作为带有大写字母A-F的十六进制数输出 |
	+------+-------------------------------------------------+

 注释：如果 % 符号多于 arg 参数，则您必须使用占位符。占位符被插入到 % 符号之后，由数字和 "\$" 组成。

.. code-block:: php

    <?php
	$str = "0758 jian";
	$strA = "A";
	$strB = "B";
	$num1 = 5;
	$num2 = 5;
	$num3 = 0.25;
	$num4 = 3.2567;
	$num5 = 8;
	$num6 = 1.735;
	$num7 = 16777215;
	$num8 = 16777215;
	printf("%2\$s %1\$s", $strA, $strB); // 2\$是指定参数位置
	echo '<br />';
	printf("填充: %'%10s", $str); //指定填充符为%字符串宽度为10
	echo '<br />';
	printf("二制制: %b", $num1);
	echo '<br />';
	printf("ASCII码: %c", $num2);
	echo '<br />';
	printf("整数: %d", $num3);
	echo '<br />';
	printf("浮点数: %.2f", $num4);
	echo '<br />';
	printf("八进制: %o", $num5);
	echo '<br />';
	printf("字符串: %s", $str);
	echo '<br />';
	printf("非小数: %u", $num6);
	echo '<br />';
	printf("十六进制: %x", $num7);
	echo '<br />';
	printf("十六进制: %X", $num8);
    ?>

函数sprintf()
^^^^^^^^^
该函数的用法和printf()的格式相似，但它并不是输出字符串，而是把格式化的字符串以返回值的形式写入到一个变量中。这样就可以将格式化后的字符串在需要时使用。

.. code-block:: php

    <?php
	$num = 12345;
	$text = sprintf("%0.2f", $num);
	echo $text;
    ?>

和HTML标签相关的字符串格式化
----------------

函数nl2br()
^^^^^^^
nl2br()函数就是在字符串中的每个新行“\n”之前插入(不是替换)HTML换行符“<br/>”。

函数htmlspecialchars()
^^^^^^^^^^^^^^^^^^
如果不希望浏览器直接解析HTML标记，就需要将HTML标记中的特殊字符转换成HTML实体。例如，将“<”转换为“<”,将“>”转换为“>”。这样HTML标记浏览器就不会去解析，而是将HTML文本在浏览器中原样输出。PHP中提供的 ``htmlspecialchars()`` 函数就可以将一些预定义的字符串转换为HTML实体。此函数用在预防使用者提供的文字中包含了HTML的标记，像是布告栏或是访客留言板这方面的应用。以下是该函数可以转换的字符：

- "&"(和号)转换为"&amp;"。
- """(双引号)转换为"&quot;"。
- "'"(单引号)转换为"&#039;"。
- "<"(小于)转换为"&lt;"。
- ">"(大于)转换为"&gt;"。

该函数的原型如下：

``string htmlspecialchars(string string [,int quote_style[,string charset]])``

该函数中第一个参数是带有HTML标记待处理的字符串。第二个参数用来决定引号的转换方式。默认值为ENT_COMPAT将只转换双引号，而保留单引号；ENT_QUOTES将同时转换这两种引号；而ENT_NOQUOTES将不对引号进行转换。第三个参数用于指定所处理字符串的字符集，默认的字符集是“ISO88511-1”。

.. code-block:: php

	<html>
		<body>

			<?php
				$str = "<B>WebServer:</B> & 'Linux' & 'Apache'"; //将有HTML标记和单引号的字符串
				echo htmlspecialchars($str,ENT_COMPAT); //转换HTML标记和转换双引号
				echo "<br>";
				echo htmlspecialchars($str,ENT_QUOTES); //转换HTML标记和转换两种引号
				echo "<br>";
				echo htmlspecialchars($str,ENT_NOQUOTES); //转换HTML标记和不对引号转换
				echo "<br>";
			?>

		</body>
	</html>

在PHP中还提供了 ``htmlentities()`` 函数，可以将所有的非ASCII码字符转换为对应的实体代码。该函数与htmlspecialchars()函数的使用语法格式一致，该函数可以转义更多的HTML字符。下面的代码为htmlentities()函数的使用范例：

.. code-block:: php

    <?php
	$str = "一个'quote'是<b>bold</b>";
	//输出：一个'quote'是&lt;b&gt;bold&lt;/b&gt;
	echo htmlentities($str);
	//输出：一个&#039;quote&#039;是&lt;b&gt;bold&lt;/b&gt;
	echo htmlentities($str,ENT_QUOTES,"UTF-8");
    ?>

提示：要把 HTML 实体转换回字符，请使用 ``html_entity_decode()`` 函数。

提示：请使用 ``get_html_translation_table()`` 函数来返回 ``htmlentities()`` 使用的翻译表。

在处理表单中提交的数据时，不仅要通过前面介绍的函数将HTML的标记符号和一些特殊字符转换为HTML实体，还需要对引号进行处理。因为被提交的表单数据中的"'"、"""和"\\"等字符前将自动加上一个斜线"\\"。这是由于PHP配置文件php.ini中的选项 ``magic_quotes_gpc`` 在起作用，默认是打开的，如果不关闭它则要使用函数 ``stripslashes()`` 删除反斜线。如果不处理，将数据保存到数据库中时，有可能会被数据库误当成控制符号而引起错误。函数 ``stripslashes()`` 只有一个被处理字符串作为参数，返回处理后的字符串。通常使用 ``htmlspecialchars()`` 函数与 ``stripslashes()`` 函数复合的方式，联合处理表单中提交的数据。

函数 ``stripslashes()`` 的功能是去掉反斜线"\\"，如果有连续两个反斜线，则只去掉一个。与之对应的是另一个函数 ``addslashes()`` ，正如函数名所暗示的，它将在"'"、"""、"\\"和NULL字符等前增加必要的反斜线。

函数 ``htmlspecialchars()`` 是将函数HTML中的标记符号转换为对应的HTML实体，有时直接删除用户输入的HTML标签，也是非常有必要的。PHP中提供的 ``strip_tags()`` 函数默认就可以删除字符串中所有的HTML标签，也可以有选择性地删除一些HTML标记。如布告栏或是访客留言板，有这方面的应用是相当必要的。例如用户在论坛中发布文章时，可以预留一些可以改变字体大小、颜色、粗体和斜体等的HTML标记，而删除一些对页面布局有影响的HTML标记。函数 ``strip_tags()`` 的原型如下所示：

``string strip_tags(string str[,string allowable_tags]); //删除HTML的标签函数``

该函数有两个参数，第一个参数提供了要处理的字符串，第二个参数是一个可选的HTML标签列表，放入该列表中的HTML标签将被保留，其他的则全部被删除。默认将所有HTML标签都删除。下面的程序为该函数的使用范围，如下所示：

.. code-block:: php

    <?php
	$str = "<font color='red' size=7>Linux</font> <i>Apache</i> <u>Mysql</u> <b>PHP</b>";
	echo strip_tags($str); //删除了全部HTML标签，输出：Linux Apache Mysql PHP
	echo strip_tags($str,"<font>"); //输出<font color='red' size=7>Linux</font>Apache Mysql PHP
	echo strip_tags($str,"<b><u><i>"); //输出Linux <i>Apache</i> <u>Mysql</u> <b>PHP</b>
    ?>

其他字符串格式化函数
------------------
字符串的格式化处理函数还有很多，只要是想得到所需要格式化的字符串，都可以调用PHP中提供的系统函数处理，很少需要自己定义字符串格式化函数。

函数strrev()
^^^^^^^^^^^^
该函数的作用是将输入的字符串反转，只提供一个要处理的字符串作为参数，返回翻转后的字符串。如下所示：

.. code-block:: php

    <?php
	echo strrev("http://www.lampbrother.net"); //反转后输出：ten.rehtorbpmal.www//:ptth
    ?>

函数md5()
^^^^^^^^^
随着互联网的普及，黑客攻击已成为网络管理者的心病。有统计数据表明70%的攻击来自内部，因此必须采取相应的防范措施来扼制系统内部的攻击。防止内部攻击的重要性还在于内部人员对数据的存储位置、信息重要性非常了解，这使得内部攻击更容易奏效。攻击者盗用合法用户的身份信息，以仿冒的身份与他人进行通信。所以在用户注册时应该先将密码加密后再添加到数据库中，这样就可以防止内部攻击者直接查询数据库中的授权表，盗用合法用户的身份信息。

md5()函数的作用就是将一个字符串进行MD5算法加密，默认返回一个32位的十六进制字符串。

``string md5 ( string $str [, bool $raw_output = false ] )``

.. code-block:: php

    <?php
	$password = "lampbrother";
	echo md5($password)."<br>";
	//将输入的密码和数据库保存的匹配
	if(md5($password) == '5f1ba7d4b4bf96fb8e7ae52fc6297aee') {
	        echo "密码一致，登录成功";
	}
    ?>


