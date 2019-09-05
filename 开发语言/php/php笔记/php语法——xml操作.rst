*********
PHP与XML操作
*********

PHP与XML操作
===========

XML的概述
========
XML(eXtensible Markup Language,扩展性标记语言)，它是用来描述其它语言的语言，它允许用户设计自己的标记。XML是由W3C于1998年2月发布的一种标准，它的前身是SGML。XML产生的原因是为了补充HTML语言的不足，使网络语言更加规范化、多样化。

HTML语言被称为第一代Web语言，现在的版本为4.0，以后将不再更新，取而代之的是XHMTL。而XHMTL正是根据XML来制定的。XML的特点有以下几个方面。

- 易用性：XML可以使用多种编辑器来进行编写，包括记事本等所有的纯文本编辑器；
- 结果性：XML是具有层次结构的标记语言，包括多层的嵌套；
- 开发性：XML语言允许开发人员自定义标记，这使得不同的领域都可以有自己的特色方案。
- 分离性：XML语言将数据样式或数据内容分开保存、各自处理，使得基于XML的应用程序可以在XML文件中准确高效地搜索相关的数据内容，忽略其他不相关部分；

XML语法
=======

XML文档结构
-------

XML声明
-----

处理指令
----

注释
----

XML元素
-----

XML属性
-----

使用CDATA标记
---------

XML命名空间
----------

在PHP中创建XML文档
=================
PHP不仅可以生成动态网页，同样也可以生成XML文件。XML处理是开发过程中经常遇到的，PHP对其也有很丰富的支持，本文只是对其中某几种解析技术做简要说明，包括：Xml parser, SimpleXML, XMLReader, DOMDocument。

PHP中有两种主要的XML解析器:

- 基于树的解析器。它是把整个文档存储为树的数据结构中，即需要把整个文档都加载到内存中才能工作。所以，当处理大型XML文档时候，性能剧减。SimpleXML和DOMDocument扩展属于此类型解析器。
- 基于流的解析器。它不会一次把整个文档加载到内存中，而是每次分别读取其中的一个节点并允许实时与之交互（当移向下一个节点时，上一个节点是被丢弃，但也设置为保留）。很明显，其效率要高且占内存少，不便之处代码量大点。Xml parser(推解析器)和XMLReader(拉解析器)属于这种。

XML Expat Parser
================
XML Parser使用Expat XML解析器。Expat是一种基于事件的解析器，它把XML文档视为一系列事件。当某个事件发生时，它调用一个指定的函数处理它。Expat是无验证的解析器，忽略任何链接到文档的DTD。但是，如果文档的形式不好，则会以一个错误消息结束。由于它基于事件，且无验证，Expat具有快速并适合web应用程序的特性。

expat是基于sax来进行xml解析而不是dom解析。因此，在expat中设置了很多的回调来处理。
XML 解析器函数允许我们创建 XML 解析器，并为 XML 事件定义句柄。

XML Parser的优势是性能好，因为它不是将整个xml文档载入内存后再处理，而是边解析边处理。但也正因为如此，它不适合那些要对xml结构做动态调整、或基于xml上下文结构做复杂操作的需求。如果你只是要解析处理一个结构良好的xml文档，那么它可以很好的完成任务。需要注意的是XML Parser只支持三种编码格式：US-ASCII, ISO-8859-1和UTF-8，如果你的xml数据是其他编码，需要先转换成以上三个之一。

SAX 解析器的优点是，它是真正轻量级的。解析器不会在内存中长期保持内容，所以可以用于非常巨大的文件。缺点是编写 SAX 解析器回调是件非常麻烦的事。

XML Parser常用的解析方式大体有两种（其实就是两个函数）： ``xml_parse_into_struct`` 和 ``xml_set_element_handler`` 。

xml_parse_into_struct
---------------------
此方法是将xml数据解析到两个数组中：

- index数组：包含指向Value数组中值的位置的指针
- value数组：包含来自被解析的 XML 的数据

这俩数组文字描述起来有点麻烦，还是看个例子吧（来自php官方文档）

.. code-block:: php

	<?php
		$simple = "<para><note>simple note</note></para>";
		$parser = xml_parser_create();
		// 元素名称是否转换为大写
		xml_parser_set_option($parser, XML_OPTION_CASE_FOLDING, 0);
		// 是否略过由白空字符组成的值
		xml_parser_set_option($parser, XML_OPTION_SKIP_WHITE, 1);
		xml_parse_into_struct($parser, $simple, $vals, $index);
		xml_parser_free($parser);
		echo "Index array\n";
		print_r($index);
		echo "\nVals array\n";
		print_r($vals);

		/* 输出
		Index array
		Array
		(
		    [PARA] => Array
		        (
		            [0] => 0
		            [1] => 2
		        )

		    [NOTE] => Array
		        (
		            [0] => 1
		        )
		)

		Vals array
		Array
		(
		    [0] => Array
		        (
		            [tag] => PARA
		            [type] => open
		            [level] => 1
		        )

		    [1] => Array
		        (
		            [tag] => NOTE
		            [type] => complete
		            [level] => 2
		            [value] => simple note
		        )

		    [2] => Array
		        (
		            [tag] => PARA
		            [type] => close
		            [level] => 1
		        )
		)
		 */
	?>

其中index数组以标签名为key，对应的值是一个数组，里面包括所有此标签在value数组中的位置。然后通过这个位置，找到此标签对应的值。

如果xml中每组数据格式有出入，不能做到完全统一，那么在写代码时要注意，说不定就得到了错误的结果。比如下面这个例子：

.. code-block:: php

	<?php
		$xml = '
		<infos>
		<para><note>note1</note><extra>extra1</extra></para>
		<para><note>note2</note></para>
		<para><note>note3</note><extra>extra3</extra></para>
		</infos>
		';

		$p = xml_parser_create();
		xml_parse_into_struct($p, $xml, $values, $tags);
		xml_parser_free($p);
		$result = array();
		//下面的遍历方式有bug隐患
		for ($i=0; $i<3; $i++) {
		  $result[$i] = array();
		  $result[$i]["note"] = $values[$tags["NOTE"][$i]]["value"];
		  $result[$i]["extra"] = $values[$tags["EXTRA"][$i]]["value"];
		}
		print_r($result);
	?>

要是按照上面那种方式遍历，看似代码简单，但是暗藏危机，最致命的是得到错误的结果（extra3跑到第二个para里了）。所以要以一种比较严谨的方式遍历：

.. code-block:: php

	<?php
		$result = array();
		$paraTagIndexes = $tags['PARA'];
		$paraCount = count($paraTagIndexes);
		for($i = 0; $i < $paraCount; $i += 2) {
		  $para = array();
		  //遍历para标签对之间的所有值
		  for($j = $paraTagIndexes[$i]; $j < $paraTagIndexes[$i+1]; $j++) {
		    $value = $values[$j]['value'];
		    if(empty($value)) continue;

		    $tagname = strtolower($values[$j]['tag']);
		    if(in_array($tagname, array('note','extra'))) {
		      $para[$tagname] = $value;
		    }
		  }
		  $result[] = $para;
		}
	?>

.. code-block:: php

    <?php
	$xml = <<<XML
	<?xml version="1.0"?>
	<moldb>

	    <molecule>
	        <name>Alanine</name>
	        <symbol>ala</symbol>
	        <code>A</code>
	        <type>hydrophobic</type>
	    </molecule>

	    <molecule>
	        <name>Lysine</name>
	        <symbol>lys</symbol>
	        <code>K</code>
	        <type>charged</type>
	    </molecule>

	</moldb>
	XML;

	class AminoAcid {
	    var $name;  // aa 姓名
	    var $symbol;    // 三字母符号
	    var $code;  // 单字母代码
	    var $type;  // hydrophobic, charged 或 neutral
	    // 构造函数
	    function AminoAcid ($aa)
	    {
	        foreach ($aa as $k=>$v)
	            $this->$k = $v;
	    }
	}

	function readDatabase($filename)
	{
	    // 读取 aminoacids 的 XML 数据
	    //$data = implode("",file($filename));
	    $data = $filename;
	    $parser = xml_parser_create();
	    // 元素名称是否转换为大写
	    xml_parser_set_option($parser, XML_OPTION_CASE_FOLDING, 0);
	    // 是否忽略空白
	    xml_parser_set_option($parser, XML_OPTION_SKIP_WHITE, 1);
	    xml_parse_into_struct($parser, $data, $values, $tags);
	    xml_parser_free($parser);

	    // 遍历 XML 结构
	    foreach ($tags as $key=>$val) {
	        if ($key == "molecule") {
	            $molranges = $val;
	            // each contiguous pair of array entries are the
	            // lower and upper range for each molecule definition
	            for ($i=0; $i < count($molranges); $i+=2) {
	                $offset = $molranges[$i] + 1;
	                $len = $molranges[$i + 1] - $offset;
	                $tdb[] = parseMol(array_slice($values, $offset, $len));
	            }
	        } else {
	            continue;
	        }
	    }
	    return $tdb;
	}

	function parseMol($mvalues)
	{
	    for ($i=0; $i < count($mvalues); $i++) {
	        $mol[$mvalues[$i]["tag"]] = $mvalues[$i]["value"];
	    }
	    return new AminoAcid($mol);
	}

	//$db = readDatabase("moldb.xml");
	$db = readDatabase($xml);
	echo "** Database of AminoAcid objects:\n";
	print_r($db);

	/* 输出结果
	** Database of AminoAcid objects:
	Array
	(
	    [0] => AminoAcid Object
	        (
	            [name] => Alanine
	            [symbol] => ala
	            [code] => A
	            [type] => hydrophobic
	        )

	    [1] => AminoAcid Object
	        (
	            [name] => Lysine
	            [symbol] => lys
	            [code] => K
	            [type] => charged
	        )

	)
	 */
    ?>

其实我很少用xml_parse_into_struct函数，所以上面所谓“严谨”的代码保不齐还会有其他情况下的bug。为此，我们需要使用另一种方式来操作xml文档。

xml_set_element_handler
-----------------------

操作步骤：

1. 使用xml_parser_create()函数初始化XML解析器；
2. 创建不同的事件处理器函数；
3. 通过添加xml_set_element_handler()函数来指定当遇到元素开始或结束标签时需要执行的函数；
4. 通过添加xml_set_element_handler()函数来指定当解析器遇到字符时需要执行的函数；
5. 通过xml_parse()函数来解析传入文件；
6. 当出现错误情况时，通过xml_error_string()函数将一个XML的错误代码转换成一个文本描述；
7. 通过调用xml_parser_free()函数来释放xml_parser_create()函数分配的内存；

SAX 解析器运行在回调模型上。每次打开或关闭一个标记时，或者每次解析器看到文本时，就用节点或文本的信息回调用户定义的函数。这种方式写的代码比较清晰，利于维护。

.. code-block:: php

	<?php
		$xml = <<<XML
		<infos>
		<para><note>note1</note><extra>extra1</extra></para>
		<para><note>note2</note></para>
		<para><note>note3</note><extra>extra3</extra></para>
		</infos>
		XML;

		$result = array();
		$index = -1;
		$currData;

		function charactor($parser, $data) {
		    global $currData;
		    $currData = $data;
		}

		function startElement($parser, $name, $attribs) {
		    global $result, $index;
		    $name = strtolower($name);
		    if($name == 'para') {
		        $index++;
		        $result[$index] = array();
		    }
		}

		function endElement($parser, $name) {
		    global $result, $index, $currData;
		    $name = strtolower($name);
		    if($name == 'note' || $name == 'extra') {
		        $result[$index][$name] = $currData;
		    }
		}
		// 1、创建解析器
		$xml_parser = xml_parser_create();
		// 注册字符处理函数
		xml_set_character_data_handler($xml_parser, "charactor");
		// 注册元素开始和结束处理函数
		xml_set_element_handler($xml_parser, "startElement", "endElement");
		// 解析xml文件
		if (!xml_parse($xml_parser, $xml)) {
		    echo "Error when parse xml: ";
		    // 转换错误码为字符串
		    echo xml_error_string(xml_get_error_code($xml_parser));
		}
		// 释放内存
		xml_parser_free($xml_parser);

		print_r($result);

		// 如果是大的文件，则需要如下代码
		$f = fopen( 'books.xml', 'r' );

		while( $data = fread( $f, 4096 ) )
		{
			xml_parse( $xml_parser, $data );
		}
	?>

可见，set handler方式虽然代码行数多，但思路清晰，可读性更好，不过性能上略慢于第一种方式，而且灵活性不强。XML Parser支持PHP4，适用于于使用老版本的系统。对于PHP5环境，还是优先考虑下面的方法吧。

XMLReader
=========
XMLReader也是PHP5之后的扩展（5.1后默认安装），它就像游标一样在文档流中移动，并在每个节点处停下来，操作起来很灵活。它提供了对输入的快速和非缓存的流式访问，可以读取流或文档，使用户从中提取数据，并跳过对应用程序没有意义的记录。XMLReader是Iterator设计模式的实现，而不是Observer设计模式。

类摘要：

.. code-block:: php

    <?php
	XMLReader {
	    /* 常量 */
	    // 非节点类型
	    const int NONE = 0 ;
	    // 元素的开始
	    const int ELEMENT = 1 ;
	    // 属性节点
	    const int ATTRIBUTE = 2 ;
	    // 文本节点
	    const int TEXT = 3 ;
	    // CDATA节点
	    const int CDATA = 4 ;
	    // 实体引用节点
	    const int ENTITY_REF = 5 ;
	    // 实体声明节点
	    const int ENTITY = 6 ;
	    // 处理指令节点
	    const int PI = 7 ;
	    // 注释节点
	    const int COMMENT = 8 ;
	    // 文档节点
	    const int DOC = 9 ;
	    // 文档类型节点
	    const int DOC_TYPE = 10 ;
	    // 文档片段节点
	    const int DOC_FRAGMENT = 11 ;
	    // 符号节点
	    const int NOTATION = 12 ;
	    // 空白符节点
	    const int WHITESPACE = 13 ;
	    // 重要的空白节点
	    const int SIGNIFICANT_WHITESPACE = 14 ;
	    // 元素结束
	    const int END_ELEMENT = 15 ;
	    // 实体结束
	    const int END_ENTITY = 16 ;
	    // XML声明节点
	    const int XML_DECLARATION = 17 ;
	    //////////////解析器选项/////////////////
	    // 加载DTD但不验证
	    const int LOADDTD = 1 ;
	    // 加载DTD和默认属性但不验证
	    const int DEFAULTATTRS = 2 ;
	    // 加载DTD且在解析时验证
	    const int VALIDATE = 3 ;
	    // 替换实体并扩展引用
	    const int SUBST_ENTITIES = 4 ;

	    /* 属性 */
	    // 当前节点属性的个数
	    public readonly int $attributeCount ;
	    // 节点的基本URI
	    public readonly string $baseURI ;
	    // 树中节点的深度，从0开始
	    public readonly int $depth ;
	    // 指示节点是否具有属性
	    public readonly bool $hasAttributes ;
	    // 指示节点是否具有文本值
	    public readonly bool $hasValue ;
	    // 指示节点的值是否来自DTD的默认值
	    public readonly bool $isDefault ;
	    // 指示节点是否是一个空元素标签
	    public readonly bool $isEmptyElement ;
	    // 节点的本地名称
	    public readonly string $localName ;
	    // 节点的完整名称
	    public readonly string $name ;
	    // 与节点关联的名称空间的URI
	    public readonly string $namespaceURI ;
	    // 节点的类型
	    public readonly int $nodeType ;
	    // 与节点关联的名称空间的前缀
	    public readonly string $prefix ;
	    // 节点的文本值
	    public readonly string $value ;
	    // 节点所在的xml：lang作用域
	    public readonly string $xmlLang ;

	    /* 方法 */
	    // 关闭XMLReader输入
	    public bool close ( void )
	    // 以DOM对象的形式返回当前节点的副本
	    public DOMNode expand ([ DOMNode $basenode ] )
	    // 获取一个命名属性的值
	    public string getAttribute ( string $name )
	    // 通过索引获取属性的值
	    public string getAttributeNo ( int $index )
	    // 通过localname和URI获取属性的值
	    public string getAttributeNs ( string $localName , string $namespaceURI )
	    // 指示是否设置了指定的属性
	    public bool getParserProperty ( int $property )
	    // 指示分析的文档是否有效
	    public bool isValid ( void )
	    // 查找名称空间的前缀
	    public string lookupNamespace ( string $prefix )
	    // 将光标移动到指定的属性
	    public bool moveToAttribute ( string $name )
	    // 通过索引将光标移动到一个属性
	    public bool moveToAttributeNo ( int $index )
	    // 将光标移动到指定的属性
	    public bool moveToAttributeNs ( string $localName , string $namespaceURI )
	    // 将光标定位在当前属性的父元素上
	    public bool moveToElement ( void )
	    // 将光标置于第一个属性上
	    public bool moveToFirstAttribute ( void )
	    // 将光标置于下一个属性上
	    public bool moveToNextAttribute ( void )
	    // 将光标移动到跳过所有子树的下一个节点
	    public bool next ([ string $localname ] )
	    // 设置包含要解析的XML的URI
	    public bool open ( string $URI [, string $encoding [, int $options = 0 ]] )
	    // 这个方法从文件的第一个节点开始，然后读取所有余下的节点，但每次调用只读取一个节点，如果存在一个节点可被读取 则返回True,当到达文件最后时返回False.
	    public bool read ( void )
	    // 从当前节点检索XML
	    public string readInnerXML ( void )
	    // 从当前节点（包括自己）中检索XML
	    public string readOuterXML ( void )
	    // 以字符串形式读取当前节点的内容
	    public string readString ( void )
	    // 设置解析器选项
	    public bool setParserProperty ( int $property , bool $value )
	    // 设置RelaxNG模式的文件名或URI
	    public bool setRelaxNGSchema ( string $filename )
	    // 设置包含RelaxNG模式的数据
	    public bool setRelaxNGSchemaSource ( string $source )
	    // 根据XSD验证文档
	    public bool setSchema ( string $filename )
	    // 设置包含要解析的XML的数据
	    public bool xml ( string $source [, string $encoding [, int $options = 0 ]] )
	}
    ?>

操作步骤
--------

1. 初始化解析器并载入文档

   第一步是创建新的解析器对象。创建操作很简单：

   ``$reader = new xmlReader();``

   接着，需要为它提供一些用于解析的数据。对于 xml-RPC，这是超文本传输协议（Hypertext Transfer Protocol，HTTP）请求的原始主体。然后可以将该字符串传递到读取器的 xml() 函数：

2. 填充原始发送数据

   ``$request = $HTTP_RAW_POST_DATA;``

   ``$reader->xml($request);``

   如果发现 $HTTP_RAW_POST_DATA 是空的，则将以下代码行添加到 php(做为现在的主流开发语言).ini 文件：

   ``always_populate_raw_post_data = On``

   可以解析任何字符串，无论它是从何处获取的。例如，可以是程序中的一串文字或从本地文件读取。还可以使用 open() 函数从外部 URL 载入数据。例如，下面的语句准备解析其中一个 Atom 提要：

   ``$reader->xml('http://www.cafeaulait.org/today.atom');``
　　
   无论是从何处获取原始数据，现在已建立了阅读器并为解析做好准备。

   **解析文件(包括本地和远程的)使用open()；解析字符串使用xml()。**

3. 读取文档

   read()函数使解析器前进到下一个标记。最简单的方法是在 while 循环中遍历整个文档：

   .. code-block:: php

       <?php
		while ($reader->read()) {
		　　// processing code goes here...
		}
       ?>
　　
   在这个循环中将检查XmlReader对象的属性和方法，以获得当前节点的信息（类型、名称、数据等等），不断地执行该循环直到Read()返回False。

4. 完成遍历后，关闭解析器以释放它所持有的任何资源，并且重置解析器以便用于下一个文档：

   ``$reader->close();``

循环移动如下所示：

.. image:: ./_static/images/xmlReader.jpg



读取元素
--------

Read(), ReadString(),ReadInnerXML(),ReadOuterXML()：

- 都能读取Element节点。
- 每个方法都跳到文档的下一个节点。
- MovetoElement()只移动到下一个节点而不读取它。

.. code-block:: php

    <?php
	$xmlData = <<<XML
	<?xml version="1.0" encoding="UTF-8"?>
	<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
	    <url>
	        <loc>http://www.php230.com/</loc>
	        <lastmod>2013-06-13 01:20:01</lastmod>
	        <changefreq>always</changefreq>
	        <priority>1.0</priority>
	    </url>
	    <url>
	        <loc>http://www.php230.com/category/</loc>
	        <lastmod>2013-06-13 01:20:01</lastmod>
	        <changefreq>always</changefreq>
	        <priority>0.8</priority>
	    </url>
	</urlset>
	XML;

	$xml = new XMLReader();
	// $url = 'http://www.php230.com/baidu_sitemap1.xml';
	// $xml->open($url);
	$xml->XML($xmlData);
	$assoc = xml2assoc($xml);
	$xml->close();

	print_r($assoc);

	function xml2assoc ($xml) {
	    $tree = null;
	    while ($xml->read())
	        switch ($xml->nodeType) {
	            case XMLReader::END_ELEMENT:
	                return $tree;
	            case XMLReader::ELEMENT:
	                $node = array('tag' => $xml->name, 'value' => $xml->isEmptyElement ? '' : xml2assoc($xml));
	                if ($xml->hasAttributes)
	                    while ($xml->moveToNextAttribute())
	                        $node['attributes'][$xml->name] = $xml->value;
	                $tree[] = $node;
	                break;
	            // 如果是文本和CDATA节点，则获取值
	            case XMLReader::TEXT:
	            case XMLReader::CDATA:
	                $tree .= $xml->value;
	        }
	    return $tree;
	}

	/*
	Array
	(
	    [0] => Array
	        (
	            [tag] => urlset
	            [value] => Array
	                (
	                    [0] => Array
	                        (
	                            [tag] => url
	                            [value] => Array
	                                (
	                                    [0] => Array
	                                        (
	                                            [tag] => loc
	                                            [value] => http://www.php230.com/
	                                        )

	                                    [1] => Array
	                                        (
	                                            [tag] => lastmod
	                                            [value] => 2013-06-13 01:20:01
	                                        )

	                                    [2] => Array
	                                        (
	                                            [tag] => changefreq
	                                            [value] => always
	                                        )

	                                    [3] => Array
	                                        (
	                                            [tag] => priority
	                                            [value] => 1.0
	                                        )

	                                )

	                        )

	                    [1] => Array
	                        (
	                            [tag] => url
	                            [value] => Array
	                                (
	                                    [0] => Array
	                                        (
	                                            [tag] => loc
	                                            [value] => http://www.php230.com/category/
	                                        )

	                                    [1] => Array
	                                        (
	                                            [tag] => lastmod
	                                            [value] => 2013-06-13 01:20:01
	                                        )

	                                    [2] => Array
	                                        (
	                                            [tag] => changefreq
	                                            [value] => always
	                                        )

	                                    [3] => Array
	                                        (
	                                            [tag] => priority
	                                            [value] => 0.8
	                                        )

	                                )

	                        )

	                )

	            [attributes] => Array
	                (
	                    [xmlns] => http://www.sitemaps.org/schemas/sitemap/0.9
	                )

	        )

	) 
	*/
    ?>

.. code-block:: php

    <?php
	class XmlTest{

	    /**
	     * 解析返回的XML文档
	     */
	    public function parseXmlFile($xmlFile){
	        $reader = new \XMLReader();
	        $reader->open($xmlFile, 'UTF-8');
	        $nodeName = '';
	        $dataList = array();
	        $data = array();
	        while ($reader->read()){
	            if($reader->nodeType == \XMLReader::ELEMENT){
	                $nodeName = $reader->name;
	                if($nodeName=='persons'){
	                    $count = $reader->getAttribute('count');
	                    if(!($count>0)){
	                        break;
	                    }
	                }
	                elseif($nodeName=='person'){
	                    $data = array(
	                        'username'=>$reader->getAttribute('username'),
	                        'age'=>$reader->getAttribute('age'),
	                    );
	                }
	            }
	            if($reader->nodeType == \XMLReader::TEXT && !empty($nodeName)){
	                if($nodeName=='person'){
	                    $data['description'] = strtolower($reader->value);
	                    $dataList[] = $data;
	                }
	            }
	        }
	        $reader->close();
	        return $dataList;
	    }

	    /**
	     * 解析返回的XML文档
	     */
	    public function parseXmlStr($xmlString){
	        $reader = new \XMLReader();
	        $reader->xml($xmlString,'UTF-8');
	        $nodeName = '';
	        $dataList = array();
	        $data = array();
	        while ($reader->read()){
	            if($reader->nodeType == \XMLReader::ELEMENT){
	                $nodeName = $reader->name;
	                if($nodeName=='persons'){
	                    $count = $reader->getAttribute('count');
	                    if(!($count>0)){
	                        break;
	                    }
	                }
	                elseif($nodeName=='person'){
	                    $data = array(
	                        'username'=>$reader->getAttribute('username'),
	                        'age'=>$reader->getAttribute('age'),
	                    );
	                }
	            }
	            if($reader->nodeType == \XMLReader::TEXT && !empty($nodeName)){
	                if($nodeName=='person'){
	                    $data['description'] = strtolower($reader->value);
	                    $dataList[] = $data;
	                }
	            }
	        }
	        return $dataList;
	    }
	}


	$xmlString = <<<XML
	<xml>
	    <persons count="10">
	        <person username="username1" age="20">this is username1 description</person>
	        <person username="username2" age="20">this is username2 description</person>
	    </persons>
	</xml>
	XML;

	$mXmlTest = new XmlTest();
	$dataList = $mXmlTest->parseXmlStr($xmlString);
	print_r($dataList);

	$xmlFile = './test.xml';
	$dataList = $mXmlTest->parseXmlFile($xmlFile);
	print_r($dataList);
    ?>

以一个利用google天气api获取信息的例子展示下XMLReader的使用，这里也只涉及到一小部分函数，更多还请参考官方文档。

.. code-block:: php

	<?php
		$xml_uri = 'http://www.google.com/ig/api?weather=Beijing&hl=zh-cn';
		$current = array();
		$forecast = array();

		$reader = new XMLReader();
		$reader->open($xml_uri, 'gbk');
		while ($reader->read()) {
		  //get current data
		  if ($reader->name == "current_conditions" && $reader->nodeType == XMLReader::ELEMENT) {
		    while($reader->read() && $reader->name != "current_conditions") {
		      $name = $reader->name;
		      $value = $reader->getAttribute('data');
		      $current[$name] = $value;
		    }
		  }

		  //get forecast data
		  if ($reader->name == "forecast_conditions" && $reader->nodeType == XMLReader::ELEMENT) {
		    $sub_forecast = array();
		    while($reader->read() && $reader->name != "forecast_conditions") {
		      $name = $reader->name;
		      $value = $reader->getAttribute('data');
		      $sub_forecast[$name] = $value;
		    }
		    $forecast[] = $sub_forecast;
		  }
		}
		$reader->close();
	?>

XMLReader和XML Parser类似，都是边读边操作，较大的差异在于SAX模型是一个“推送”模型，其中分析器将事件推到应用程序，在每次读取新节点时通知应用程序，而使用XmlReader的应用程序可以随意从读取器提取节点，可控性更好。

由于XMLReader基于libxml，所以有些函数要参考文档看看是否适用于你的libxml版本。

XMLWriter
=========
需要总结




DOMDocument
===========
DOMDocument还是PHP5后推出的DOM扩展的一部分，可用来建立或解析html/xml，目前只支持utf-8编码。

.. code-block:: php

	<?php
		$xmlstring = <<<XML
		<?xml version='1.0'?>
		<document>
		  <cmd attr='default'>login</cmd>
		  <login>imdonkey</login>
		</document>
		XML;

		$dom = new DOMDocument();
		$dom->loadXML($xmlstring);
		print_r(getArray($dom->documentElement));

		function getArray($node) {
		    $array = false;

		    if ($node->hasAttributes()) {
		        foreach ($node->attributes as $attr) {
		            $array[$attr->nodeName] = $attr->nodeValue;
		        }
		    }

		    if ($node->hasChildNodes()) {
		        if ($node->childNodes->length == 1) {
		            $array[$node->firstChild->nodeName] = getArray($node->firstChild);
		        } else {
		            foreach ($node->childNodes as $childNode) {
		                if ($childNode->nodeType != XML_TEXT_NODE) {
		                    $array[$childNode->nodeName][] = getArray($childNode);
		                }
		            }
		        }
		    } else {
		        return $node->nodeValue;
		    }
		    return $array;
		}
	?>

从函数名上看感觉跟JavaScript很像，应该是借鉴了一些吧。DOMDocument也是一次性将xml载入内存，所以内存问题同样需要注意。PHP提供了这么多的xml处理方式，开发人员在选择上就要花些时间了解，选择适合项目需求及系统环境、又便于维护的方法。



动态创建XML文档
--------------
使用SimpleXML对象可以十分方便地读取和修改XML文档，但却无法动态创建XML，这时就需要使用DOM来实现。DOM通过树形结构模式来遍历XML文档。使用DOM遍历文档的好处是不需要标记即可显示全部内容，但缺点同样明显，就是十分消耗内存。

.. code-block:: php

    <?php
	$dom = new DOMDocument('1.0','utf-8');
	$object = $dom->createElement('object'); // 创建根节点object
	$dom->appendChild($object); // 将创建的根节点添加到DOM对象中
	$book = $dom->createElement('book'); // 创建节点book
	$object->appendChild($book); // 将节点book追加到DOM对象中
	$computerbook = $dom->createElement('computerbook'); // 创建节点computerbook
	$book->appendChild($computerbook); // 将computerbook追加到DOM对象中
	$type = $dom->createAttribute('type'); // 创建一个节点属性type
	$computerbook->appendChild($type); // 将属性追加到computerbook元素后
	$type_value = $dom->createTextNode('computer'); // 创建一个属性值
	$type->appendChild($type_value); // 将属性值赋给type
	$bookname = $dom->createElement('bookname'); // 创建节点bookname
	$computerbook->appendChild($bookname); // 将节点追加到DOM对象中
	$bookname_value = $dom->createTextNode('PHP从入门到精通'); // 创建元素值
	$bookname->appendChild($bookname_value); // 将值赋给节点bookname
	echo $dom->saveXML(); // 输出XML文件
    ?>

.. include:: ./xml——DOM操作.rst

.. include:: ./xml——SimpleXML操作.rst










