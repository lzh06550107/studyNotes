*************
SimpleXML操作文档
*************

SimpleXML
=========
PHP对XML格式的文档进行操作有很多方法。如XML语法解析函数、DOMXML函数和SimpleXML函数等。其中，PHP5新加入的SimpleXML函数操作更简单。

SimpleXML是PHP5后提供的一套简单易用的xml工具集，可以把xml转换成方便处理的对象，也可以组织生成xml数据。不过它不适用于包含namespace的xml，而且要保证xml格式完整(well-formed)。它提供了三个方法：simplexml_import_dom、simplexml_load_file、simplexml_load_string，函数名很直观地说明了函数的作用。三个函数都返回SimpleXMLElement对象，数据的读取/添加都是通过SimpleXMLElement操作。

SimpleXML的优点是开发简单，缺点是它会将整个xml载入内存后再进行处理，所以在解析超多内容的xml文档时可能会力不从心。如果是读取小文件，而且xml中也不包含namespace，那SimpleXML是很好的选择。

SimpleXMLElement对象
--------------------
它表示XML文档中的元素。

.. code-block:: php

    <?php
	SimpleXMLElement implements Traversable {
	    // 创建一个新的SimpleXMLElement对象
	    final public __construct ( string $data [, int $options = 0 [, bool $data_is_url = false [, string $ns = "" [, bool $is_prefix = false ]]]] )
	    // 给SimpleXMLElement对象添加一个属性
	    public void addAttribute ( string $name [, string $value [, string $namespace ]] )
	    // 给SimpleXMLElement对象添加一个孩子对象
	    public SimpleXMLElement addChild ( string $name [, string $value [, string $namespace ]] )
	    // 返回一个格式良好的XML字符串
	    public mixed asXML ([ string $filename ] )
	    // 返回SimpleXMLElement对象所有属性
	    public SimpleXMLElement attributes ([ string $ns = NULL [, bool $is_prefix = false ]] )
	    // 返回SimpleXMLElement对象所有孩子对象
	    public SimpleXMLElement children ([ string $ns [, bool $is_prefix = false ]] )
	    // 统计本元素的孩子对象个数
	    public int count ( void )
	     // 返回文档声明的所有命名空间
	    public array getDocNamespaces ([ bool $recursive = false [, bool $from_root = true ]] )
	    // 获取元素的名称
	    public string getName ( void )
	    // 返回所有使用的命名空间
	    public array getNamespaces ([ bool $recursive = false ] )
	    // 为下一次 XPath 查询创建命名空间
	    public bool registerXPathNamespace ( string $prefix , string $ns )
	    public string __toString ( void )
	    // 运行对 XML 文档的 XPath 查询
	    public array xpath ( string $path )
	}
    ?>

创建SimpleXMLElement对象
------------------------
使用SimpleXMLElement首先创建对象。共有3中方法来创建对象，分别是：

- Simplexml_load_file()函数，将指定的文件解析为SimpleXMLElement对象；
- Simplexml_load_string()函数，将创建的字符串解析为SimpleXMLElement对象；
- Simplexml_import_dom()函数，根据DOM节点创建SimpleXMLElement对象；
- 通过new SimpleXMLElement来创建对象；

.. code-block:: php

	<?php
		header("Content-Type:text/html;charset=utf-8"); // 设置编码

		// 第一种方法
		$xml_1 = simplexml_load_file("5.xml");
		print_r($xml_1);

		// 第二种方法
		$str = <<<XML
		<?xml version="1.0" encoding="utf-8" ?>
		<Object>
		    <ComputerBook>
		        <title>PHP从入门到精通</title>
		    </ComputerBook>
		</Object>
		XML;

		$xml_2 = simplexml_load_string($str);
		echo PHP_EOL;
		print_r($xml_2);

		// 第三种方法
		$dom = new DOMDocument();
		$dom->loadXML($str);
		$xml_3 = simplexml_import_dom($dom);
		echo PHP_EOL;
		print_r($xml_3);

		// 第四种方法
		$dom = new SimpleXMLElement($str);
	?>

遍历所有子元素
-------------
创建对象后，就可以使用SimpleXML的其他函数来读取数据了。使用SimpleXML对象中的children()方法和foreach循环语句可以遍历所有子节点元素。

.. code-block:: php

    <?php
	//header('Content-Type:text/html;charset=utf-8'); // 设置编码
	// 创建XML格式的字符串

	$str = <<<XML
	<?xml version="1.0" encoding="utf-8" ?>
	<object>
	    <book>
	        <computerbook>PHP从入门到精通</computerbook>
	    </book>
	    <book>
	        <computerbook>PHP项目开发全程实录</computerbook>
	     </book>
	</object>
	XML;

	$xml = simplexml_load_string($str); // 创建一个SimpleXML对象
	foreach($xml->children() as $layer_one) {  // 循环输出根节点
	    print_r($layer_one); // 查看节点结构
	    echo PHP_EOL;
	    foreach ($layer_one->children() as $layer_two) { // 循环输出第二层根节点
	        print_r($layer_two);
	        echo PHP_EOL;
	    }
	}
    ?>

遍历所有属性
-----------
SimpleXML不仅可以遍历子元素，还可以遍历元素中的属性。其使用的是SimpleXML对象中的attributes()方法，在使用上和children()方法相似。

.. code-block:: php

	<?php
		header("Content-Type:text/html;charset=utf-8");  // 设置编码
		// 创建XML格式的字符串

		$str = <<<XML
		<?xml version="1.0" encoding="UTF-8" ?>
		<object name="'commodity">
		    <book name="computerbook">
		        <bookname name="PHP从入门到精通"/>
		    </book>
		    <book>
		        <bookname name="上下五千年" />
		    </book>
		</object>
		XML;

		$xml = simplexml_load_string($str); // 创建一个SimpleXML对象
		foreach($xml->children() as $layer_one) { // 循环子节点元素
		    foreach($layer_one->attributes() as $name => $vl) { // 循环子节点元素
		        echo $name."::".$vl;
		    }
		    echo PHP_EOL;
		    foreach ($layer_one->children() as $layer_two) { // 输出第二层节点元素
		        foreach($layer_two->attributes() as $nm => $vl) { // 输出各个节点的属性和值
		            echo $nm."::".$vl;
		        }
		        echo PHP_EOL;
		    }
		}
	?>

递归遍历所有节点元素和属性
------------------------

.. code-block:: php

    <?php
	$str = <<<XML
	<?xml version="1.0" encoding="utf-8" ?>
	<object>
	    <book id="1">
	        <computerbook author="diaomao">PHP从入门到精通</computerbook>
	    </book>
	    <book>
	        <computerbook author="lzh">PHP项目开发全程实录</computerbook>
	     </book>
	</object>
	XML;

	$xml = new SimpleXMLElement($str);

	function getNodesInfo (SimpleXMLElement $node) {
	    // 输出元素的名称
	    print $node->getName();

	    // 输出元素包含的属性名和值
	    foreach ($node->attributes() as $key => $value) {
	        print '('.$key.":".$value.')';
	    }

	    // 输出元素的值
	    if($node->count() == 0) {
	        print ':'.(string)$node.PHP_EOL;
	    }else {
	        print PHP_EOL;
	        // 递归
	        foreach ($node->children() as $childNode) {
	            getNodesInfo($childNode);
	        }

	    }
	}

	getNodesInfo($xml);
    ?>

访问特定节点元素和属性
--------------------
SimpleXML对象除了可以使用上面两种方法来遍历所有的子节点元素和属性，还可以访问特定的数据元素。SimpleXML对象可以通过子元素的名称对该子元素赋值，或者使用子元素的名称数组来对该子元素的属性赋值。

.. code-block:: php

	<?php
		header('Content-Type:text/html;charset=utf-8'); // 设置编码
		// 创建XML格式的字符串
		$str = <<<XML
		<?xml version="1.0" encoding="UTF-8" ?>
		<object name="商品">
		    <book>
		        <computerbook>PHP从入门到精通</computerbook>
		    </book>
		    <book>
		        <computerbook name="PHP项目开发全程实录" />
		    </book>
		</object>
		XML;

		$xml = simplexml_load_string($str); // 创建SimpleXML对象
		echo $xml['name']; // 输出根元素的属性name
		echo PHP_EOL;
		echo $xml->book[0]->computerbook; // 输出子元素中computerbook的值
		echo PHP_EOL;
		echo $xml->book[1]->computerbook['name']; // 输出computerbook的属性值
	?>

插入元素和属性
-------------
给指定的节点插入属性或者插入子节点。

.. code-block:: php

    <?php
	// 从字符串中获取xml文档
	$xml = <<<XML
	<?xml version="1.0" encoding="utf-8" ?>
	<Object>
	    <ComputerBook>
	        <title>PHP从入门到精通</title>
	    </ComputerBook>
	    <ComputerBook>
	        <title>PHP高级编程</title>
	    </ComputerBook>
	</Object>
	XML;

	$doc = new SimpleXMLElement($xml);

	// 添加元素
	$doc->ComputerBook[0]->addChild('author','lzh');
	// 添加属性
	$doc->ComputerBook[1]->addAttribute('author', 'lzh');

	print $doc->asXML();
    ?>

修改XML数据
-----------
修改XML数据同读取XML数据类似。在访问特定节点元素或属性时，也可以对其进行修改操作。

.. code-block:: php

	<?php
		header('Content-Type:text/html;charset=utf-8'); // 设置编码
		// 创建XML格式的字符串
		$str = <<<XML
		<?xml version="1.0" encoding="UTF-8" ?>
		<object name="商品">
		    <book>
		        <computerbook>PHP从入门到精通</computerbook>
		    </book>
		</object>
		XML;

		$xml = simplexml_load_string($str); // 创建SimpleXML对象
		echo $xml['name']; // 输出根元素的属性name
		echo PHP_EOL;
		// 修改子元素computerbook的属性值type
		$xml->book->computerbook['type'] = 'PHP程序员必备工具';
		// 修改子元素computerbook的值
		$xml->book->computerbook = 'PHP函数参考大全';
		// 输出修改后的属性和元素值
		echo $xml->book->computerbook['type'];
		echo PHP_EOL;
		echo $xml->book->computerbook;
	?>

保存XML文档
-----------
数据在SimpleXML对象中所做的修改，其实是在系统内存中做的改动，而原文档根本没有变化。当关掉网页或清空内存时，数据又会恢复。要保存一个修改过的SimpleXML对象，可以使用asXML()方法来实现，该访问可以将SimpleXML对象中的数据格式化为XML格式。然后再使用file()函数中的写入函数将数据保存到XML文件中。

demo.xml文档内容

.. code-block:: xml

	<?xml version="1.0" encoding="UTF-8"?>
	<object name="商品">
	    <book>
	        <computerbook type="PHP程序员必备工具">PHP函数参考大全</computerbook>
	    </book>
	</object>

.. code-block:: php

	<?php
		// 创建SimpleXML对象
		$xml = simplexml_load_file('demo.xml');
		// 修改XML文档内容
		$xml->book->computerbook['type'] = 'PHP程序员必备工具';
		$xml->book->computerbook = 'PHP函数参考大全';
		// 格式化对象$xml
		$modi = $xml->asXML();
		// 将对象保存到demo.xml文档中
		file_put_contents("demo.xml", $modi);
		// 重新读取xml文档
		$str = file_get_contents("demo.xml");
		// 输出修改后的文档内容
		echo $str;
	?>

SimpleXMLIterator
------------------
递归遍历SimpleXMLElement节点对象的迭代器。该迭代器继承了SimpleXMLElement。

.. code-block:: php

    <?php
	/*** a simple xml tree ***/
	$xmlstring = <<<XML
	<?xml version = "1.0" encoding="UTF-8" standalone="yes"?>
	<document>
	  <animal>
	    <category id="26">
	      <species>Phascolarctidae</species>
	      <type>koala</type>
	      <name>Bruce</name>
	    </category>
	  </animal>
	  <animal>
	    <category id="27">
	      <species>macropod</species>
	      <type>kangaroo</type>
	      <name>Bruce</name>
	    </category>
	  </animal>
	  <animal>
	    <category id="28" author="lzh">
	      <species>diprotodon</species>
	      <type>wombat</type>
	      <name>Bruce</name>
	    </category>
	  </animal>
	  <animal>
	    <category id="31">
	      <species>macropod</species>
	      <type>wallaby</type>
	      <name>Bruce</name>
	    </category>
	  </animal>
	  <animal>
	    <category id="21">
	      <species>dromaius</species>
	      <type>emu</type>
	      <name>Bruce</name>
	    </category>
	  </animal>
	  <animal>
	    <category id="22">
	      <species>Apteryx</species>
	      <type>kiwi</type>
	      <name>Troy</name>
	    </category>
	  </animal>
	  <animal>
	    <category id="23">
	      <species>kingfisher</species>
	      <type>kookaburra</type>
	      <name>Bruce</name>
	    </category>
	  </animal>
	  <animal>
	    <category id="48">
	      <species>monotremes</species>
	      <type>platypus</type>
	      <name>Bruce</name>
	    </category>
	  </animal>
	  <animal>
	    <category id="4">
	      <species>arachnid</species>
	      <type>funnel web</type>
	      <name>Bruce</name>
	      <legs>8</legs>
	    </category>
	  </animal>
	</document>
	XML;

	/*** a new simpleXML iterator object ***/
	try {
	    /*** a new simple xml iterator ***/
	    $it = new SimpleXMLIterator($xmlstring);
	    /*** a new limitIterator object ***/
	    foreach (new RecursiveIteratorIterator($it, 1) as $name => $data) {

	       if($data->count() != 0) { // 还有子节点，则输出名称
	            echo $name;
	        } else { //没有子节点，则输出名称和值
	            echo $name . ' -- ' . $data;
	        }
	        foreach($data->attributes() as $key => $value) {
	            echo '('.$key.':'.$value.')';
	        }
	        echo PHP_EOL;
	    }
	} catch (Exception $e) {
	    echo $e->getMessage();
	}
    ?>

new RecursiveIteratorIterator($it,1)表示显示所有包括父元素在内的子元素。

显示某一个特定的元素值，可以这样写：

.. code-block:: php

    <?php
	// 这个方法有局限性，循环层级和上面的层级需要对应
	try {
	    /*** a new simpleXML iterator object ***/
	    $sxi = new SimpleXMLIterator($xmlstring);

	    foreach ($sxi as $node) {
	        foreach ($node as $k => $v) {
	            echo $v->species . PHP_EOL; // 获取指定元素的值
	        }
	    }
	} catch (Exception $e) {
	    echo $e->getMessage();
	}

	// 相对应的while循环写法为：
	try {
	    $sxe = simplexml_load_string($xmlstring, 'SimpleXMLIterator');

	    for ($sxe->rewind(); $sxe->valid(); $sxe->next()) {
	        if ($sxe->hasChildren()) {
	            foreach ($sxe->getChildren() as $element => $value) {
	                echo $value->species . PHP_EOL;
	            }
	        }
	    }
	} catch (Exception $e) {
	    echo $e->getMessage();
	}
    ?>

最方便的写法，还是使用xpath：

.. code-block:: php

    <?php
	try {
	    /*** a new simpleXML iterator object ***/
	    $sxi = new SimpleXMLIterator($xmlstring);

	    /*** set the xpath ***/
	    $foo = $sxi->xpath('animal/category/species');

	    /*** iterate over the xpath ***/
	    foreach ($foo as $k => $v) {
	        echo $v . PHP_EOL;
	    }
	} catch (Exception $e) {
	    echo $e->getMessage();
	}
    ?>

下面的例子，显示有namespace的情况：

.. code-block:: php

    <?php
	/*** a simple xml tree ***/
	$xmlstring = <<<XML
	<?xml version = "1.0" encoding="UTF-8" standalone="yes"?>
	<document xmlns:spec="http://example.org/animal-species">
	  <animal>
	    <category id="26">
	      <species>Phascolarctidae</species>
	      <spec:name>Speed Hump</spec:name>
	      <type>koala</type>
	      <name>Bruce</name>
	    </category>
	  </animal>
	  <animal>
	    <category id="27">
	      <species>macropod</species>
	      <spec:name>Boonga</spec:name>
	      <type>kangaroo</type>
	      <name>Bruce</name>
	    </category>
	  </animal>
	  <animal>
	    <category id="28">
	      <species>diprotodon</species>
	      <spec:name>pot holer</spec:name>
	      <type>wombat</type>
	      <name>Bruce</name>
	    </category>
	  </animal>
	  <animal>
	    <category id="31">
	      <species>macropod</species>
	      <spec:name>Target</spec:name>
	      <type>wallaby</type>
	      <name>Bruce</name>
	    </category>
	  </animal>
	  <animal>
	    <category id="21">
	      <species>dromaius</species>
	      <spec:name>Road Runner</spec:name>
	      <type>emu</type>
	      <name>Bruce</name>
	    </category>
	  </animal>
	  <animal>
	    <category id="22">
	      <species>Apteryx</species>
	      <spec:name>Football</spec:name>
	      <type>kiwi</type>
	      <name>Troy</name>
	    </category>
	  </animal>
	  <animal>
	    <category id="23">
	      <species>kingfisher</species>
	      <spec:name>snaker</spec:name>
	      <type>kookaburra</type>
	      <name>Bruce</name>
	    </category>
	  </animal>
	  <animal>
	    <category id="48">
	      <species>monotremes</species>
	      <spec:name>Swamp Rat</spec:name>
	      <type>platypus</type>
	      <name>Bruce</name>
	    </category>
	  </animal>
	  <animal>
	    <category id="4">
	      <species>arachnid</species>
	      <spec:name>Killer</spec:name>
	      <type>funnel web</type>
	      <name>Bruce</name>
	      <legs>8</legs>
	    </category>
	  </animal>
	</document>
	XML;

	/*** a new simpleXML iterator object ***/
	try {
	    /*** a new simpleXML iterator object ***/
	    $sxi = new SimpleXMLIterator($xmlstring);

	   $sxi->registerXPathNamespace('spec1', 'http://example.org/animal-species');

	    /*** set the xpath ***/
	    $result = $sxi->xpath('//spec1:name');

	    /*** get all declared namespaces ***/
	    foreach ($sxi->getDocNamespaces('animal') as $ns) {
	        echo $ns . PHP_EOL;
	    }

	    /*** iterate over the xpath ***/
	    foreach ($result as $k => $v) {
	        echo $v . PHP_EOL;
	    }
	} catch (Exception $e) {
	    echo $e->getMessage();
	}
    ?>

.. code-block:: php

    <?php
	/**
	 * This example demonstrates recursively iterating over an XML file
	 * to any particular path.
	 */
	$xmlstring = <<<XML
	<?xml version = "1.0" encoding="UTF-8" standalone="yes"?>
	<document>
	    <animal>
	        <category id="26">
	            <species>Phascolarctidae</species>
	            <type>koala</type>
	            <name>Bruce</name>
	        </category>
	    </animal>
	    <animal>
	        <category id="27">
	            <species>macropod</species>
	            <type>kangaroo</type>
	            <name>Bruce</name>
	        </category>
	    </animal>
	    <animal>
	        <category id="28">
	            <species>diprotodon</species>
	            <type>wombat</type>
	            <name>Bruce</name>
	        </category>
	    </animal>
	    <animal>
	        <category id="31">
	            <species>macropod</species>
	            <type>wallaby</type>
	            <name>Bruce</name>
	        </category>
	    </animal>
	    <animal>
	        <category id="21">
	            <species>dromaius</species>
	            <type>emu</type>
	            <name>Bruce</name>
	        </category>
	    </animal>
	    <animal>
	        <category id="22">
	            <species>Apteryx</species>
	            <type>kiwi</type>
	            <name>Troy</name>
	        </category>
	    </animal>
	    <animal>
	        <category id="23">
	            <species>kingfisher</species>
	            <type>kookaburra</type>
	            <name>Bruce</name>
	        </category>
	    </animal>
	    <animal>
	        <category id="48">
	            <species>monotremes</species>
	            <type>platypus</type>
	            <name>Bruce</name>
	        </category>
	    </animal>
	    <animal>
	        <category id="4">
	            <species>arachnid</species>
	            <type>funnel web</type>
	            <name>Bruce</name>
	            <legs>8</legs>
	        </category>
	    </animal>
	</document>
	XML;
	try {

	    // load the XML Iterator and iterate over it
	    $sxi = new SimpleXMLIterator($xmlstring);

	    // iterate over animals
	    foreach ($sxi as $animal) {
	        // iterate over category nodes
	        foreach ($animal as $key => $category) {
	            echo $category->species . PHP_EOL;
	        }
	    }
	} catch (Exception $e) {
	    die($e->getMessage());
	}
	echo '===================================' . PHP_EOL;
	echo 'Finding all species with xpath' . PHP_EOL;
	echo '===================================' . PHP_EOL;
	// which can also be re-written for optimization
	try {

	    // load the XML Iterator and iterate over it
	    $sxi = new SimpleXMLIterator($xmlstring);
	    // use xpath
	    $foo = $sxi->xpath('animal/category/species');
	    foreach ($foo as $k => $v) {
	        echo $v . PHP_EOL;
	    }
	} catch (Exception $e) {
	    die($e->getMessage());
	}
    ?>