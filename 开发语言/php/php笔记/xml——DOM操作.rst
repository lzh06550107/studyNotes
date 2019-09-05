*****
DOM操作
*****

DOM扩展
=======
DOM扩展允许你通过PHP的DOM API来操作XML文档。

DOM全称为The Document Object Model，应该理解为是一个规范，定义了HTML和XML文档的逻辑结构和文档操作的编程接口。

文档逻辑结构

DOM实际上是以面向对象方式描述的对象模型，它将文档建模为一个个对象，以树状的结构组织（本文称之为“文档树”，树中的对象称为“节点”）。

每个文档包含1个document节点，0个或1个doctype节点以及0个或多个元素节点等。document节点是文档树的根节点。
如对于HTML文档，DOM 是这样规定的：

- 整个文档是一个文档节点(DOMDocument)
- 每个 HTML 标签是一个元素节点(DOMElement)
- 包含在 HTML 元素中的文本是文本节点(DOMText)
- 每一个 HTML 属性是一个属性节点(DOMAttr)
- 注释属于注释节点(DOMComment)

节点与文档内容是一一对应的关系，节点之间有层次关系。

DOMNode抽象类
---------
表示一个DOM节点。

.. code-block:: php

    <?php
	DOMNode {
	    // 节点名称
	    public readonly string $nodeName ;
	    // 节点值，取决于它的类型
	    public string $nodeValue ;
	    // 节点类型
	    public readonly int $nodeType ;
	    // 本节点父节点
	    public readonly DOMNode $parentNode ;
	    // 本节点所有子节点
	    public readonly DOMNodeList $childNodes ;
	    // 第一个孩子节点
	    public readonly DOMNode $firstChild ;
	    // 最后一个孩子节点
	    public readonly DOMNode $lastChild ;
	    // 前一个兄弟节点
	    public readonly DOMNode $previousSibling ;
	    // 后一个兄弟节点
	    public readonly DOMNode $nextSibling ;
	    // 节点的属性
	    public readonly DOMNamedNodeMap $attributes ;
	    // 拥有该节点的DOMDocument对象
	    public readonly DOMDocument $ownerDocument ;
	    // 该节点的命名空间URI
	    public readonly string $namespaceURI ;
	    // 该节点的命名空间前缀
	    public string $prefix ;
	    // 该节点全名本地部分
	    public readonly string $localName ;
	    // 该节点的绝对基础URI
	    public readonly string $baseURI ;
	    // 该节点即后代的文本内容
	    public string $textContent ;

	    // 在末尾增加孩子节点
	    public DOMNode appendChild ( DOMNode $newnode )
	    // 转换该节点为字符串表示
	    public string C14N ([ bool $exclusive [, bool $with_comments [, array $xpath [, array $ns_prefixes ]]]] )
	    // 存储该节点到文件中
	    public int C14NFile ( string $uri [, bool $exclusive [, bool $with_comments [, array $xpath [, array $ns_prefixes ]]]] )
	    // 克隆节点
	    public DOMNode cloneNode ([ bool $deep ] )
	    // 获取节点的行号
	    public int getLineNo ( void )
	    // 获取一个节点的XPath
	    public string getNodePath ( void )
	    // 检查节点是否有属性
	    public bool hasAttributes ( void )
	    // 检查是否具有孩子节点
	    public bool hasChildNodes ( void )
	    // 在参考节点前面添加一个新的孩子
	    public DOMNode insertBefore ( DOMNode $newnode [, DOMNode $refnode ] )
	    // 检查指定的namespaceURI是否是默认的命名空间
	    public bool isDefaultNamespace ( string $namespaceURI )
	    // 检查两个节点是否是相同节点
	    public bool isSameNode ( DOMNode $node )
	    // 检查是否支持具体版本功能
	    public bool isSupported ( string $feature , string $version )
	    // 根据前缀获取节点的名称空间URI
	    public string lookupNamespaceURI ( string $prefix )
	    // 根据名称空间URI获取节点的名称空间前缀
	    public string lookupPrefix ( string $namespaceURI )
	    // 规范化节点
	    public void normalize ( void )
	    // 移除孩子节点
	    public DOMNode removeChild ( DOMNode $oldnode )
	    // 替换孩子节点
	    public DOMNode replaceChild ( DOMNode $newnode , DOMNode $oldnode )
	}
    ?>

DOMNodeList类
-------------
我们可通过节点列表中的节点索引号来访问列表中的节点（索引号由0开始）。

节点列表可保持其自身的更新。如果节点列表或 XML 文档中的某个元素被删除或添加，列表也会被自动更新。

.. note:: 在一个节点列表中，节点被返回的顺序与它们在 XML 被规定的顺序相同。

.. code-block:: php

    <?php
	DOMNodeList implements Traversable {

	    // 列表中的节点数量
	    readonly public int $length ;

	    // 通过指定的索引来检索节点
	    DOMNode DOMNodelist::item ( int $index )
	}
    ?>

关于item($index)

item(index)是DOMNodeList类中的一个方法，它的做用是返回一个由索引指明的节点。而DOMDocument类中的getElementsByTagName(name）方法返回的正是一个DOMNodeList对象的实例，所以可以直接调用item(index)方法。

DOMDocument类
-------------
代表整个HTML或XML文档;作为文档树的根。

.. code-block:: php

    <?php
	DOMDocument extends DOMNode {

	    // 文档的实际编码(过时)
	    readonly public string $actualEncoding ;
	    // 配置(过时)
	    readonly public DOMConfiguration $config ;
	    // 与此文档关联的文档类型声明
	    readonly public DOMDocumentType $doctype ;
	    // 这是一个便利的属性，允许直接访问文档根元素
	    readonly public DOMElement $documentElement ;
	    // 文档的位置
	    public string $documentURI ;
	    // 根据XML声明指定的文档编码
	    public string $encoding ;
	    // 带有缩进和空白漂亮格式
	    public bool $formatOutput ;
	    // 处理本文档的DOMImplementation
	    readonly public DOMImplementation $implementation ;
	    // 不删除多余的空格,分析时，最好设置为false，这样孩子节点就不包含空白
	    public bool $preserveWhiteSpace = true ;
	    // 启用恢复模式,试图解析不完整的文件
	    public bool $recover ;
	    // 从doctype声明加载外部实体
	    public bool $resolveExternals ;
	    // 文档是否独立(过时)
	    public bool $standalone ;
	    //
	    public bool $strictErrorChecking = true ;
	    // 是否替代实体？？
	    public bool $substituteEntities ;
	    // 加载并验证DTD
	    public bool $validateOnParse = false ;
	    // XML版本(过时)
	    public string $version ;
	    // 作为XML声明一部分的一个属性，指定了这个文档的编码。
	    readonly public string $xmlEncoding ;
	    // 作为XML声明的一部分，指定该文档是否是独立的。
	    public bool $xmlStandalone ;
	    // 作为XML声明一部分的属性，指定此文档的版本号。
	    public string $xmlVersion ;

	    // 创建一个新的DOMDocument对象
	    public __construct ([ string $version [, string $encoding ]] )
	    // 创建一个新的属性
	    public DOMAttr createAttribute ( string $name )
	    // 使用关联的名称空间创建新的属性节点？？
	    public DOMAttr createAttributeNS ( string $namespaceURI , string $qualifiedName )
	    // 创建一个新的CDATA节点
	    public DOMCDATASection createCDATASection ( string $data )
	    // 创建一个新注释节点
	    public DOMComment createComment ( string $data )
	    // 创建一个新的文档片段
	    public DOMDocumentFragment createDocumentFragment ( void )
	    // 创建一个新的元素节点
	    public DOMElement createElement ( string $name [, string $value ] )
	    // 使用关联的名称空间创建新的元素节点创建
	    public DOMElement createElementNS ( string $namespaceURI , string $qualifiedName [, string $value ] )
	    // 创建新的实体引用节点
	    public DOMEntityReference createEntityReference ( string $name )
	    // 创建新的PI节点
	    public DOMProcessingInstruction createProcessingInstruction ( string $target [, string $data ] )
	    // 创建新的文本节点
	    public DOMText createTextNode ( string $content )
	    // 搜索具有特定ID的元素
	    public DOMElement getElementById ( string $elementId )
	    // 使用给定的本地标签名称搜索所有元素
	    public DOMNodeList getElementsByTagName ( string $name )
	    // 在指定的名称空间中搜索具有给定标签名称的所有元素
	    public DOMNodeList getElementsByTagNameNS ( string $namespaceURI , string $localName )
	    // 将节点导入当前文档
	    public DOMNode importNode ( DOMNode $importedNode [, bool $deep ] )
	    // 从文件加载XML
	    public mixed load ( string $filename [, int $options = 0 ] )
	    // 从字符串加载HTML
	    public bool loadHTML ( string $source [, int $options = 0 ] )
	    // 从文件加载HTML
	    public bool loadHTMLFile ( string $filename [, int $options = 0 ] )
	    // 从字符串加载XML
	    public mixed loadXML ( string $source [, int $options = 0 ] )
	    // 规范化文档
	    public void normalizeDocument ( void )
	    // 注册扩展类用于创建基本节点类型
	    public bool registerNodeClass ( string $baseclass , string $extendedclass )
	    // 对文档执行relaxNG验证
	    public bool relaxNGValidate ( string $filename )
	    // 对文档执行relaxNG验证
	    public bool relaxNGValidateSource ( string $source )
	    // 将内部XML树转储回文件
	    public int save ( string $filename [, int $options ] )
	    // 使用HTML格式将内部文档转储为字符串
	    public string saveHTML ([ DOMNode $node = NULL ] )
	    // 使用HTML格式将内部文档转储到文件中
	    public int saveHTMLFile ( string $filename )
	    // 将内部XML树转储回字符串
	    public string saveXML ([ DOMNode $node [, int $options ]] )
	    // 基于模式验证文档
	    public bool schemaValidate ( string $filename [, int $flags ] )
	    // 基于模式验证文档
	    public bool schemaValidateSource ( string $source [, int $flags ] )
	    // 基于DTD验证文档
	    public bool validate ( void )
	    // 在DOMDocument对象中替换XIncludes
	    public int xinclude ([ int $options ] )

	    /* 省略继承的方法 */

	}
    ?>


DOMElement类
------------


.. code-block:: php

    <?php
	DOMElement extends DOMNode {

	    // 没有实现
	    readonly public bool $schemaTypeInfo ;
	    // 元素名称
	    readonly public string $tagName ;

	    // 创建一个新的DOMElement对象
	    public __construct ( string $name [, string $value [, string $namespaceURI ]] )
	    // 返回属性值
	    public string getAttribute ( string $name )
	    // 返回属性节点
	    public DOMAttr getAttributeNode ( string $name )
	    // 返回属性节点
	    public DOMAttr getAttributeNodeNS ( string $namespaceURI , string $localName )
	   // 返回属性值
	    public string getAttributeNS ( string $namespaceURI , string $localName )
	    // 通过标记名获取元素节点
	    public DOMNodeList getElementsByTagName ( string $name )
	    // 通过namespaceURI和localName获取元素
	    public DOMNodeList getElementsByTagNameNS ( string $namespaceURI , string $localName )
	    // 检查是否存在该属性
	    public bool hasAttribute ( string $name )
	    // 检查是否存在该属性
	    public bool hasAttributeNS ( string $namespaceURI , string $localName )
	    // 移除属性
	    public bool removeAttribute ( string $name )
	    // 移除属性
	    public bool removeAttributeNode ( DOMAttr $oldnode )
	    // 移除属性
	    public bool removeAttributeNS ( string $namespaceURI , string $localName )
	    // 增加新属性
	    public DOMAttr setAttribute ( string $name , string $value )
	    // 增加新属性节点
	    public DOMAttr setAttributeNode ( DOMAttr $attr )
	    // 增加新属性节点
	    public DOMAttr setAttributeNodeNS ( DOMAttr $attr )
	    // 增加新属性
	    public void setAttributeNS ( string $namespaceURI , string $qualifiedName , string $value )
	    // 声明由name指定的属性是类型ID
	    public void setIdAttribute ( string $name , bool $isId )
	    // 声明由属性节点指定的属性是类型ID
	    public void setIdAttributeNode ( DOMAttr $attr , bool $isId )
	    // 声明本地名称和名称空间URI指定的属性是类型ID
	    public void setIdAttributeNS ( string $namespaceURI , string $localName , bool $isId )

	/* 省略继承的方法 */

	}
    ?>

DOMAttr类
---------
DOMAttr表示DOMElement对象中的一个属性。

.. code-block:: php

    <?php
	DOMAttr extends DOMNode {

	    // 属性名称
	    public readonly string $name ;
	    // 属性所附属的元素节点
	    public readonly DOMElement $ownerElement ;
	    // 属性相关联的类型信息???
	    public readonly bool $schemaTypeInfo ;
	    // 如果属性值被设置在文档中，则返回 true，如果其默认值被设置在 DTD/Schema 中，则返回 false。(没有实现)
	    public readonly bool $specified ;
	    // 属性的值
	    public string $value ;

	    // 创建一个新的DOMAttr对象
	    public __construct ( string $name [, string $value ] )
	    // 检查属性是否是已定义的ID
	    public bool isId ( void )

	    /* 省略继承的方法 */

	}
    ?>

DOMCharacterData
----------------
表示包含字符数据的节点。没有节点直接对应这个类，但是其他节点继承它。CharacterData 接口提供了 Text 和 Comment 节点的常用功能。以便 Text 和 Comment 可以继承它。

.. code-block:: php

    <?php
	DOMCharacterData extends DOMNode {

	    // 节点的内容
	    public string $data ;
	    // 内容的长度
	    readonly public int $length ;

	    // 将字符串追加到节点的字符数据的末尾
	    void appendData ( string $data )
	    // 从节点中删除指定范围的字符
	    void deleteData ( int $offset , int $count )
	    // 在指定的位置插入字符串
	    void insertData ( int $offset , string $data )
	    // 替换字符串
	    void replaceData ( int $offset , int $count , string $data )
	    // 截取子字符串
	    string substringData ( int $offset , int $count )

	    /* 省略继承的方法 */

	}
    ?>

DOMText
-------
``DOMText`` 类从 ``DOMCharacterData`` 继承并表示 ``DOMElement`` 或 ``DOMAttr`` 的文本内容。

.. code-block:: php

    <?php
	DOMText extends DOMCharacterData {

	    // 保存逻辑上相邻的所有文本节点的文本（不由元素，注释或处理指令分隔）。
	    readonly public string $wholeText ;

	    // 创建一个新的DOMText对象
	    public __construct ([ string $value ] )
	    // 指示此文本节点是否包含空格
	    public bool isWhitespaceInElementContent ( void )
	    // 按指定的偏移量将此节点分成两个节点
	    public DOMText splitText ( int $offset )

	    /* 省略继承的方法 */

	}
    ?>

DOMCdataSection
----------------
CDATASection 对象表示文档中的 CDATA Section。

CDATASection 接口是 Text 接口的子接口，没有定义任何自己的属性和方法。通过从 Node 接口继承 nodeValue 属性，或通过从 CharacterData 接口继承 data 属性，可以访问 CDATA Section 的文本内容。

虽然通常可以把 CDATASection 节点作为 Text 节点处理，但要注意，Node.normalize() 方法不并入相邻的 CDATA 部分。

使用 Document.createCDATASection() 来创建一个 CDATASection。

CDATA 区段包含了不会被解析器解析的文本。CDATA 区段中的标签不会被视为标记，同时实体也不会被展开。主要的目的是为了包含诸如 XML 片段之类的材料，而无需转义所有的分隔符。

在一个 CDATA 中唯一被识别的分隔符是 "]]>"，它可标示 CDATA 区段的结束。CDATA 区段不能进行嵌套。

.. code-block:: php

    <?php
	DOMCdataSection extends DOMText {
	    //创建一个DOMCdataSection对象
	    public __construct ( string $value )
	    /* Inherited methods */
	    public bool DOMText::isWhitespaceInElementContent ( void )
	    public DOMText DOMText::splitText ( int $offset )
	}
    ?>

DOMComment类
------------
表示注释节点，由<!-- 和 -->分隔的字符。使用由 CharacterData 接口继承的各种方法可以操作注释的内容。

使用 Document.createComment() 来创建一个注释对象。

.. code-block:: php

    <?php
	DOMComment extends DOMCharacterData {

	    // 创建一个注释对象
	    public __construct ([ string $value ] )
	    /* 省略继承的方法 */

	}
    ?>

DOMDocumentFragment
-------------------

.. code-block:: php

    <?php
	DOMDocumentFragment extends DOMNode {

	    //附加原始XML数据
	    public bool appendXML ( string $data )
	    /* 省略继承的方法 */

	}
    ?>

DOMDocumentType
---------------
每个 ``DOMDocument`` 都有一个 ``doctype`` 属性，其值为 ``NULL`` 或 ``DOMDocumentType`` 对象。

DocumentType 对象可向为 XML 所定义的实体提供接口。

.. code-block:: php

    <?php
	DOMDocumentType extends DOMNode {

	    // 外部子集的公共标识符
	    readonly public string $publicId ;
	    // 外部 DTD 的系统识别符
	    readonly public string $systemId ;
	    // DTD 的名称
	    readonly public string $name ;
	    // 含有在 DTD 中所声明的实体的 NamedNodeMap
	    readonly public DOMNamedNodeMap $entities ;
	    // 含有在 DTD 中所声明的符号（notation）的 NamedNodeMap
	    readonly public DOMNamedNodeMap $notations ;
	    // 以字符串返回内部 DTD
	    readonly public string $internalSubset ;

	    /* 省略继承的方法 */

	}
    ?>

DOMEntity类
-----------


DOMEntityReference类
---------------------


DOMException类
--------------
在特定情况下，DOM操作会引发异常，例如，由于合乎逻辑的原因而无法执行操作时。

DOMImplementation类
-------------------
DomImplementation 对象可执行与文档对象模型的任何实例无关的任何操作。

DomImplementation 接口是一个占位符，存放不专属任何特定 Document 对象，而对 DOM 实现来说是“全局性”的方法。可以通过任何 Document 对象的 implementation 属性获得对 DomImplementation 对象的引用。

.. code-block:: php

    <?php
	DOMImplementation {

	    // 创建爱你一个新的DOMImplementation对象
	    __construct ( void )
	    // 使用指定的根元素创建一个新 Document 对象
	    public DOMDocument createDocument ([ string $namespaceURI = NULL [, string $qualifiedName = NULL [, DOMDocumentType $doctype = NULL ]]] )
	    // 创建空的 DocumentType 节点
	    public DOMDocumentType createDocumentType ([ string $qualifiedName = NULL [, string $publicId = NULL [, string $systemId = NULL ]]] )
	    // 检查 DOM implementation 是否可执行指定的特性和版本
	    public bool hasFeature ( string $feature , string $version )
	}
    ?>

DOMNamedNodeMap类
-----------------
我们可通过节点名称来访问 NamedNodeMap 中的节点。

NamedNodeMap 可保持其自身的更新。假如节点列表或 XML 文档中的某元素被删除或添加，节点也会被自动更新。

注释：注释：在一个指定的节点地图中，节点不会以任何次序返回。

.. code-block:: php

    <?php
	DOMNamedNodeMap implements Traversable {

	    // map中的节点数量。
	    readonly public int $length ;

	    // 可返回指定的节点（通过名称）
	    DOMNode getNamedItem ( string $name )
	    //可返回指定的节点（通过名称和命名空间）
	    DOMNode getNamedItemNS ( string $namespaceURI , string $localName )
	    // 可返回处于指定索引号的节点
	    DOMNode item ( int $index )
	}
    ?>

DOMNotation类
-------------

.. code-block:: php

    <?php
	DOMNotation extends DOMNode {

	    //
	    readonly public string $publicId ;
	    readonly public string $systemId ;

	    /* 省略继承的方法 */

	}
    ?>

DOMProcessingInstruction类
--------------------------
ProcessingInstruction 对象可表示处理指令。

这个不常用的接口表示 XML 文档中的一个处理指令（或 PI）。使用 HTML 文档的程序设计者不会遇到 ProcessingInstruction 节点。

处理指令可作为在 XML文档的文本中保留处理器定制信息的方法来使用。

.. code-block:: php

    <?php
	DOMProcessingInstruction extends DOMNode {

	    // 返回此处理指令的目标。它是“<?”后的第一个标识符，指定了处理指令的处理器。
	    readonly public string $target ;
	    // 设置或返回此处理指令的内容。（即从目标开始后的第一个非空格字符到结束字符“?>”之间的字符，但不包括“?>”）
	    public string $data ;

	    public __construct ( string $name [, string $value ] )
	    /* 省略继承的方法 */

	}
    ?>

DOMXPath类
----------
支持XPath 1.0。

.. code-block:: php

    <?php
	DOMXPath {

	    public DOMDocument $document ;

	    public __construct ( DOMDocument $doc )
	    // 计算给定的XPath表达式，并在可能的情况下返回结果
	    public mixed evaluate ( string $expression [, DOMNode $contextnode [, bool $registerNodeNS = true ]] )
	    // 计算给定的XPath表达式
	    public DOMNodeList query ( string $expression [, DOMNode $contextnode [, bool $registerNodeNS = true ]] )
	    // 用DOMXPath对象注册名称空间
	    public bool registerNamespace ( string $prefix , string $namespaceURI )
	    // 将PHP函数注册为XPath函数
	    public void registerPhpFunctions ([ mixed $restrict ] )
	}
    ?>

DOM使用例子
==========
DOM树定义了文档的逻辑结构，以及控制你访问和操作这些文档的方法。使用DOM，开发人员可以创建XML或HTML文档，操作它们的结果，增加、修改和删除文档 元素及内容。可以从任何编程语言访问DOM，本文使用PHP 5 DOM扩展，它是PHP核心的一部分，因此除了PHP外，不需要安装其它软件。

操作的样例文件Book.xml：

.. code-block:: xml

	<?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
	<book>
	    <!--XML Processing [part I] -->
	    <name>XML Processing I</name>
	    <author>John Smith Jr.</author>
	    <publisher>HisOwnTM</publisher>
	    <ISBN>111-222-333-4441</ISBN>
	    <contents>
	        <chapter_I>
	            <title>What is XML about ?</title>
	            <content>XML (Extensible Markup Language) is a ...</content>
	        </chapter_I>
	        <chapter_II>
	            <title>SAX</title>
	            <content>SAX is a simple API for ...</content>
	        </chapter_II>
	        <chapter_III>
	            <title>StAX</title>
	            <content>Much powerful and flexible, StAX, is very...</content>
	        </chapter_III>
	        <chapter_IV>
	            <title>DOM
	                <subtitle>DOM concept
	                    <continut>Starting to use DOM...</continut>
	                </subtitle>
	                <subchapter_IV_I>
	                    <title>First DOM application...</title>
	                    <content>Here it is your first DOM application...</content>
	                </subchapter_IV_I>
	            </title>
	        </chapter_IV>
	        <end>The end...</end>
	    </contents>
	    <!-- See you in XML Processing [part II] -->
	</book>


创建DOMNode对象
-----------------
要使用DOM首先创建DOMNode对象。共有3中方法来创建对象，分别是：

- 通过new来创建DOMDocument对象并通过它的createXXXX()方法来创建节点；
- 通过导入文件来创建,如load,loadHTMLFile；
- 通过导入字符串来创建,如loadXML,loadHTML；
- 通过dom_import_simplexml转换一个SimpleXMLElement对象为DOMElement对象；

.. code-block:: php

    <?php
	// 创建文档节点
	$doc = new DOMDocument('1.0','utf-8');
	// 格式化输出
	$doc->formatOutput = true;

	// 创建元素节点
	$root = $doc->createElement('book');
	$root = $doc->appendChild($root);

	// 创建属性节点
	$attr = $doc->createAttribute('name');
	$attr->value = 'php xml操作';
	$root->appendChild($attr);

	$title = $doc->createElement('title');
	$root->appendChild($title);
	// 另一种创建属性方式
	$title->setAttribute('name', '标题名称');

	// 创建文本节点
	$text = $doc->createTextNode('This is the title');
	$title->appendChild($text);


	// 创建CDATA节点
	$cdata = $doc->createCDATASection("function someJsText() {
	   document.write('Some js with <a href=\"#\">HTML</a> content');
	}");
	$title->appendChild($cdata);

	// 创建注释节点
	$comment = $doc->createComment('这是一个注释');
	$title->appendChild($comment);

	// 另一种创建文本节点方式
	$content = $doc->createElement('content','书的内容');
	$root->appendChild($content);

	echo "Saving all the document:\n";
	echo $doc->saveXML() . "\n";

	echo "Saving only the title part:\n";
	echo $doc->saveXML($title);
    ?>

.. code-block:: php

    <?php
	$doc = new DOMDocument();

	///////////////////////从文件获取文档////////////////////////

	// 通过本地获取xml文件
	$doc->load('demo.xml');
	print $doc->saveXML();

	//通过远程获取xml文件
	$doc->load('http://www.w3school.com.cn/example/xmle/note.xml');
	print $doc->saveXML();

	// 通过本地获取html文件
	@$doc->loadHTMLFile('index.html');
	print $doc->saveHTML();

	// 通过远程获取html文件
	$opts = array(
	    'http' => array(
	        'user_agent' => 'PHP libxml agent',
	    )
	);

	$context = stream_context_create($opts);
	libxml_set_streams_context($context);

	@$doc->loadHTMLFile('http://www.baidu.com');
	print $doc->saveHTML();

	/////////////////从字符串获取文档///////////////////////

	$xml = <<<XML
	<?xml version="1.0" encoding="ISO-8859-1"?>
	<!--  Copyright w3school.com.cn -->
	<note>
		<to>George</to>
		<from>John</from>
		<heading>Reminder</heading>
		<body>Don't forget the meeting!</body>
	</note>
	XML;

	$html = <<<HTML
	<html><body>Test<br><div>Text</div></body></html>
	HTML;

	// 从字符串导入xml文档
	$doc->loadXML($xml);
	print $doc->saveXML();

	// 从字符串导入html文档
	$doc->loadHTML($html);
	print $doc->saveHTML();

	////////////////////////dom_import_simplexml///////////////////////

	$sxe = simplexml_load_string('<books><book><title>blah</title></book></books>');

	if ($sxe === false) {
	    echo 'Error while parsing the document';
	    exit;
	}

	$dom_sxe = dom_import_simplexml($sxe);
	if (!$dom_sxe) {
	    echo 'Error while converting XML';
	    exit;
	}
	// 导入该节点以及子节点
	$dom_sxe = $doc->importNode($dom_sxe, true);
	$dom_sxe = $doc->appendChild($dom_sxe);

	echo $doc->saveXML();
    ?>

提取元素
--------
使用Book.xml文档，提取出关联的树，然后使用DOMElement接口的getElementsByTagName方法显示第一个子节点实例。

``DOMNodeList DOMElement::getElementsByTagName(string $name)`` ：这个方法返回所有$name参数指定的标签名的子元素。下面的例子查找<book>根节点 ，然后查找它的子节点 <author>，<publisher>和 <name>元素，选择每个子节点的第一个，最后打印这些节点的值。

.. code-block:: php

    <?php
	// 创建一个文档实例
	$doc = new DOMDocument();
	//载入Book.xml文件
	$doc->load( 'Book.xml' );
	//使用book标签名搜索所有元素
	$books = $doc->getElementsByTagName( "book" );
	//使用author标签名搜索所有元素
	$authors = $doc->getElementsByTagName( "author" );
	//返回第一个标签名为author的元素
	$author = $authors->item(0)->nodeValue;
	//以publisher标签名搜索所有元素
	$publishers = $doc->getElementsByTagName( "publisher" );
	//返回第一个找到的标签名为publisher的元素
	$publisher = $publishers->item(0)->nodeValue;
	//搜索标签名为name的所有元素
	$titles = $doc->getElementsByTagName( "name" );
	//返回标签名为name的第一个找到的元素
	$title = $titles->item(0)->nodeValue;
	//打印找到的值
	echo "$title - $author - $publisher \n";
	/* 输出结果
	XML Processing I - John Smith Jr. - HisOwnTM
	 */
    ?>

使用DOMXPath检索节点
-------------------
这里涉及到2个类 DOMDocument 和 DOMXPath。

其实思路比较明确，就是通过DOMDocument将一个html file转换成DOM树的数据结构，再用DOMXPath的实例去搜索这个DOM树，拿到想要特定节点，接下来就可以对当前节点的子树进行遍历，得到想要的结果。

.. code-block:: php

    <?php
	//将html/xml文件转换成DOM树
	$dom = new DOMDocument();
	$dom->loadHTMLFile("hao.html");

	//得到所有class为fix的dl标签

	// example 1: for everything with an id
	//$elements = $xpath->query("//*[@id]");

	// example 2: for node data in a selected id
	//$elements = $xpath->query("/html/body/div[@id='yourTagIdHere']");

	// example 3: same as above with wildcard
	//$elements = $xpath->query("*/div[@id='yourTagIdHere']");
	$xpath = new DOMXPath($dom);
	$dls = $xpath->query('//dl[@class="fix"]');

	foreach ($dls as $dl) {
	    $spans = $dl->childNodes;
	    foreach ($spans as $span) {
	        echo trim($span->textContent)."\t";
	    }
	    echo "\n";
	}
    ?>

递归浏览DOM树
------------
因为XML文档结构中一个标签可以包括另一个标签（分支树），剩下就是叶子节点，因此你可以浏览完整的树或从任何节点开始递归浏览子树 。下面的例子是从任何开始节点（$node）浏览下面的XML子树，并列出节点的名字和值。

.. code-block:: php

    <?php
	//创建一个文档实例
	$doc = new DOMDocument();
	//载入Book.xml文件
	$doc->load('Book.xml');
	//设置对象树根
	$root = $doc->firstChild;
	// 递归函数列出子树的所有节点
	function getNodesInfo ($node) {
	    if ($node->hasChildNodes()) {
	        $subNodes = $node->childNodes;
	        foreach ($subNodes as $subNode) {
	            // 去除了所有空文本节点
	            if (($subNode->nodeType != XML_TEXT_NODE) || (($subNode->nodeType == XML_TEXT_NODE) && (strlen(trim($subNode->wholeText)) >= 1))) {
	                echo "Node name: " . $subNode->nodeName . "\n";
	                echo "Node value: " . $subNode->nodeValue . "\n";
	            }
	            getNodesInfo($subNode);
	        }
	    }
	}
	//调用getNodesInfo函数
	getNodesInfo($root);

	/* 输出结果
	Node name: #comment
	Node value: XML Processing [part I]
	Node name: name
	Node value: XML Processing I
	Node name: #text
	Node value: XML Processing I
	Node name: author
	Node value: John Smith Jr.
	Node name: #text
	Node value: John Smith Jr.
	Node name: publisher
	Node value: HisOwnTM
	Node name: #text
	Node value: HisOwnTM
	Node name: ISBN
	Node value: 111-222-333-4441
	Node name: #text
	Node value: 111-222-333-4441
	Node name: contents
	Node value:

	            What is XML about ?
	            XML (Extensible Markup Language) is a ...


	            SAX
	            SAX is a simple API for ...


	            StAX
	            Much powerful and flexible, StAX, is very...


	            DOM
	                DOM concept
	                    Starting to use DOM...


	                    First DOM application...
	                    Here it is your first DOM application...



	        The end...

	Node name: chapter_I
	Node value:
	            What is XML about ?
	            XML (Extensible Markup Language) is a ...

	Node name: title
	Node value: What is XML about ?
	Node name: #text
	Node value: What is XML about ?
	Node name: content
	Node value: XML (Extensible Markup Language) is a ...
	Node name: #text
	Node value: XML (Extensible Markup Language) is a ...
	Node name: chapter_II
	Node value:
	            SAX
	            SAX is a simple API for ...

	Node name: title
	Node value: SAX
	Node name: #text
	Node value: SAX
	Node name: content
	Node value: SAX is a simple API for ...
	Node name: #text
	Node value: SAX is a simple API for ...
	Node name: chapter_III
	Node value:
	            StAX
	            Much powerful and flexible, StAX, is very...

	Node name: title
	Node value: StAX
	Node name: #text
	Node value: StAX
	Node name: content
	Node value: Much powerful and flexible, StAX, is very...
	Node name: #text
	Node value: Much powerful and flexible, StAX, is very...
	Node name: chapter_IV
	Node value:
	            DOM
	                DOM concept
	                    Starting to use DOM...


	                    First DOM application...
	                    Here it is your first DOM application...



	Node name: title
	Node value: DOM
	                DOM concept
	                    Starting to use DOM...


	                    First DOM application...
	                    Here it is your first DOM application...


	Node name: #text
	Node value: DOM

	Node name: subtitle
	Node value: DOM concept
	                    Starting to use DOM...

	Node name: #text
	Node value: DOM concept

	Node name: continut
	Node value: Starting to use DOM...
	Node name: #text
	Node value: Starting to use DOM...
	Node name: subchapter_IV_I
	Node value:
	                    First DOM application...
	                    Here it is your first DOM application...

	Node name: title
	Node value: First DOM application...
	Node name: #text
	Node value: First DOM application...
	Node name: content
	Node value: Here it is your first DOM application...
	Node name: #text
	Node value: Here it is your first DOM application...
	Node name: end
	Node value: The end...
	Node name: #text
	Node value: The end...
	Node name: #comment
	Node value:  See you in XML Processing [part II]
	 */
    ?>

增加新节点
---------
DOMNode接口包括多个创建新节点和在DOM树中插入节点的方法，如果要创建一个新节点，可以使用createElement或createTextNode方法，然后 ，为了增加一个新节点到DOM树上，可以调用appendChild或insertBefore方法，appendChild方法增加一个新的子节点到特定节点的子节点列表的后面，而 insertBefore方法是在特定节点的前面插入一个节点。

下面是这些方法的原型：

- DOMElement createElement(string $name [, string $value ]) ：这个方法创建了一个DOMElement类的实例，$name参数表示新元素的标签名，$value参数 表示元素的值，你也可以稍后使用DOMElement->nodeValue属性其值。
- DOMText createTextNode(string $content)：这个方法创建了一个DOMText类的实例，$content参数表示新的文本节点的文本内容。
- DOMNode DOMNode::appendChild(DOMNode $newnode)：这个函数扩展了现有子节点末尾$newnode参数，或创建一个新的包括指定节点的子节点列表。
- DOMNode DOMNode::insertBefore(DOMNode $newnode [,DOMNode $refnode])：这个方法在$refnode节点前插入$newnode参数，如果$refnode节点丢失，新的 节点就添加到节点的子节点列表前。

下面的例子创建了一个<bibliography>节点，并将其追加到节点的末尾：

.. code-block:: php

    <?php
	//创建一个文档实例
	$doc = new DOMDocument();
	//载入Book.xml文件
	$doc->load( 'Book.xml' );
	//设置对象树根
	$root = $doc->firstChild;

	//创建一个新元素
	$newElement = $doc->createElement('bibliography','Martin Didier, Professional XML');

	//使用appendChild函数将其追加到根节点孩子节点后面
	$root->appendChild($newElement);

	//使用insertBefore函数将其插入到根节点第一个孩子节点位置
	//注意，需要克隆前面创建的节点
	$root->insertBefore($newElement->cloneNode(true),$root->firstChild);
	print $doc->saveXML();
    ?>

移除子节点
---------
使用removeChild方法从DOM树中移除子节点。

``DOMNode DOMNode::removeChild(DOMNode $oldnode)`` ：这个函数移除一个子节点，$oldnode参数指出要移除的子节点。下面的示例代码从Book.xml文档中移除子节点。

.. code-block:: php

    <?php
	//创建一个文档实例
	$doc = new DOMDocument();
	//去除文档中的空白
	$doc->preserveWhiteSpace = false;
	//载入Book.xml文件
	$doc->load( 'Book.xml' );

	//设置对象树根
	$root = $doc->firstChild;
	//第一个孩子是注释节点
	print $doc->saveXML($root->firstChild);
	//移除根节点第一个孩子
	$root->removeChild($root->firstChild);
	print PHP_EOL;
	print $doc->saveXML($root->firstChild);
    ?>

替换节点
--------
为了用一个新节点替换已有的节点，使用replaceChild方法。
``DOMNode DOMNode::replaceChild(DOMNode $newnode, DOMNode $oldnode)`` ：这个函数使用$newnode子节点替换$oldnode节点。
例如，假设你想用新的code子节点替换ISBN子节点：

.. code-block:: php

    <?php
	//创建一个文档实例
	$doc = new DOMDocument();
	//去除空白
	$doc->preserveWhiteSpace = false;
	//载入Book.xml文件
	$doc->load('Book.xml');

	//获取ISBN节点
	$element = $doc->getElementsByTagName('ISBN')->item(0);
	//创建新的<code>元素
	$code = $doc->createElement('code', '909090');

	//使用$code替换$element
	$element->parentNode->replaceChild($code, $element);
	//格式化输出
	$doc->formatOutput = true;

	print $doc->saveXML();
    ?>

导入节点
--------
使用importNode方法从另一个树拷贝一个节点到当前的树。

``DOMNode DOMDocument::importNode(DOMNode $importedNode [,bool $deep])`` ：这个方法从另一个XML文档导入一个节点，然后插入当前文档的DOM树中，$importedNode参数指出了要导入的节点，导入的节点表示原始节点的一份拷贝，因此导入操作不会修改外部树，$deep参数控制是否导入被导入节点的深度，值为 TRUE时，导入完整的节点子树，为FALSE时，只导入节点本身。

下面的示例从Book_continue.xml文件导入<continue>节点到Book.xml，下面是Book_continue.xml文档的内容：

.. code-block:: php

    <?php
	$xml = <<<XML
	<?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
	<!--chapter V-->
	<continue>
	  <chapter_V>
	   <title>XPath</title>
	   <content>XPath is language for...</content>
	  </chapter_V>
	  <![CDATA[
	     This chaper is a bonus to...
	     ]]>
	  <printing cap_I="click_here_for_chapter_I"
	    cap_II="click_here_for_chapter_II"
	    cap_III="click_here_for_chapter_III"
	    cap_IV="click_here_for_chapter_IV"
	    cap_V="click_here_for_chapter_V" />
	</continue>
	XML;

	$olddoc = new DOMDocument;
	$olddoc->loadXML($xml);
	$node = $olddoc->getElementsByTagName("continue")->item(0);
	$newdoc = new DOMDocument;
	$newdoc->preserveWhiteSpace = false;
	$newdoc->formatOutput = true;
	$newdoc->load("Book.xml");
	//导入节点及其所有子节点到文档
	$node = $newdoc->importNode($node, true);
	//然后追加到根节点
	$newdoc->documentElement->appendChild($node);

	print $newdoc->saveXML();
    ?>

检查节点的等同性
---------------
检查两个节点是否相同使用isSameNode方法。

``bool DOMNode::isSameNode(DOMNode $node)`` ：当节点是相等的时候，这个函数返回一个布尔值TRUE，否则返回FALSE，$node参数表示你要和当前节点进行比较 的节点。

注意比较不是基于节点的内容进行的：

.. code-block:: php

    <?php
	//创建一个文档实例
	$doc = new DOMDocument();
	//载入Book.xml文件
	$doc->load('Book.xml');
	//设置对象树根
	$root = $doc->firstChild;
	//检查两个节点是否相同
	$title1 = $root->getElementsByTagName('title')->item(0);
	$title2 = $root->getElementsByTagName('title')->item(1);
	print_r($title1);
	print PHP_EOL;
	print_r($title2);
	//调用verifyNodes函数
	verifyNodes($title1,$title2);
	function verifyNodes($currentNode, $node)
	{
	    if (($currentNode->isSameNode($node))==true)
	    {
	        echo "These two nodes are the same";
	    }else{
	        echo "These two nodes are not the same";
	    }
	}
    ?>

创建新的DOM文档
--------------
PHP 5 DOM扩展可以让你从零开始构建DOM树，下面的示例创建了一个全新的XML文档，使用了两个新函数创建注释和CDATA节点。

- DOMComment DOMDocument::createComment(string $data)：创建一个新的注释节点，$data参数表示节点的内容。
- DOMCDATASection DOMDocument::createCDATASection(string $data)：创建一个新的CDATA节点，$data参数表示节点的内容。

.. code-block:: php

    <?php
	//创建一个文档实例
	$document = new DOMDocument('1.0', 'utf-8');
	//使用缩进格式化输出
	$document->formatOutput = true;
	//创建一个注释
	$comment = $document->createComment('Beautiful flowers!!!');
	$document->appendChild( $comment );
	//创建<flowers>根元素
	$root = $document->createElement( 'flowers' );
	$document->appendChild( $root );
	//创建<tulips>子节点
	$tulips = $document->createElement( 'tulips' );
	//创建<tulips>元素的第一个子节点<bulbs>，并设置其属性
	$bulbs_1 = $document->createElement( 'bulbs' );
	$bulbs_1->setAttribute('price','€ 7.65');
	$bulbs_1->appendChild($document->createTextNode( 'Parrot'));
	$tulips->appendChild( $bulbs_1 );
	//创建<tulips>元素的第二个子节点<bulbs>，并设置其属性
	$bulbs_2 = $document->createElement( 'bulbs' );
	$bulbs_2->setAttribute('color','magenta');
	$bulbs_2->appendChild($document->createTextNode( 'Lily flowering' ));
	$tulips->appendChild( $bulbs_2 );
	//追加<tulips>节点到根节点后
	$root->appendChild( $tulips );
	//创建CDATA小节
	$cdata = $document->createCDATASection(
	    '<gladiolus><species>Sword Lily</species>'.
	    '<species>Starface</species></gladiolus>');
	$document->appendChild( $cdata );
	//保存对象树到Flowers.xml
	echo $document->saveXML();
	$document->save('Flowers.xml');

	/* 新的Flower.xml文档内容如下
	<!--Beautiful flowers!!!-->
	<flowers>
	  <tulips>
	    <bulbs price="€ 7.65">Parrot</bulbs>
	    <bulbs color="magenta">Lily flowering</bulbs>
	  </tulips>
	</flowers>
	<![CDATA
	[<gladiolus>
	    <species>Sword Lily</species>
	    <species>Starface</species>
	  </gladiolus>
	]]>
	*/
    ?>