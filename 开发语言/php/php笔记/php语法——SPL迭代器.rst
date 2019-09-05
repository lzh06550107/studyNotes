**********
SPL迭代器
**********

.. contents:: 目录
   :depth: 6

迭代器
======
SPL提供了大量的迭代器，适用于各种应用场景，遍历不同的对象。 包括可以遍历时删除或修改元素的值或key（ArrayIterator）、空迭代器（EmptyIterator）、可以实现多迭代器遍历的MultipleIterator、文件目录迭代器等等。

通俗地说，Iterator能够使许多不同的数据结构，都能有统一的操作界面，比如一个数据库的结果集、同一个目录中的文件集、或者一个文本中每一行构成的集合。

如果按照普通情况，遍历一个MySQL的结果集，程序需要这样写：

.. code-block:: php

    <?php
	// Fetch the "aggregate structure"
	$result = mysql_query("SELECT * FROM users");

	// Iterate over the structure
	while ( $row = mysql_fetch_array($result) ) {
	   // do stuff with the row here
	}
    ?>

读出一个目录中的内容，需要这样写：

.. code-block:: php

    <?php
	// Fetch the "aggregate structure"
	$dh = opendir('/home/harryf/files');

	// Iterate over the structure
	while ( $file = readdir($dh) ) {
	   // do stuff with the file here
	}
    ?>

读出一个文本文件的内容，需要这样写：

.. code-block:: php

    <?php
	// Fetch the "aggregate structure"
	$fh = fopen("/home/hfuecks/files/results.txt", "r");

	// Iterate over the structure
	while (!feof($fh)) {

	   $line = fgets($fh);
	   // do stuff with the line here

	}
    ?>

上面三段代码，虽然处理的是不同的resource（资源），但是功能都是遍历结果集（loop over contents），因此Iterator的基本思想，就是将这三种不同的操作统一起来，用同样的命令接口，处理不同的资源。

.. code-block:: php

    <?php
	/*** class definition to extend Directory Iterator class ***/
	class DirectoryReader extends DirectoryIterator {
	    // constructor.. duh!
	    function __construct ($path) {
	        /*** pass the $path off to the parent class constructor ***/
	        parent::__construct($path);
	    }

	    /*** return the current filename ***/
	    function current () {
	        return parent::getFileName();
	    }

	    /*** members are only valid if they are a directory ***/
	    function valid () {
	        if (parent::valid()) {
	            if (!parent::isDir()) { // 如果不是目录，则迭代下一个，再次调用本方法验证有效性
	                parent::next();
	                return $this->valid();
	            }
	            return TRUE; // 如果父类有效且是目录，则有效
	        }
	        return FALSE; // 如果父类无效，则无效
	    }
	} // end class

	try {
	    /*** a new iterator object ***/
	    $it = new DirectoryReader('./');
	    /*** loop over the object if valid ***/
	    while ($it->valid()) {
	        /*** echo the current object member ***/
	        echo $it->current() . PHP_EOL;
	        /*** advance the internal pointer ***/
	        $it->next();
	    }
	} catch (Exception $e) {
	    echo 'No files Found!'.PHP_EOL;
	}
    ?>

这为创建可重用的可移植类创造了很多机会，从而减少了用户代码，加快了开发时间。

ArrayIterator
-------------
这个类实际上是对ArrayObject类的补充，为后者提供遍历功能。这个迭代器允许在遍历数组和对象时删除和更新值与键。

当你想多次遍历相同数组时你需要实例化ArrayObject，然后让这个实例创建一个ArrayIteratror实例，然后使用foreach或者 手动调用getIterator()方法。

类定义如下：

.. literalinclude:: ./src/appenditerator.inc
   :language: php

示例如下：

.. code-block:: php

    <?php
	/*** a simple array ***/
	$array = array('koala', 'kangaroo', 'wombat', 'wallaby', 'emu', 'kiwi', 'kookaburra', 'platypus');

	try {
	    $object = new ArrayIterator($array);
	    foreach($object as $key=>$value) {
	        echo $key.' => '.$value.PHP_EOL;
	    }

	    /*** check for the existence of the offset 2 ***/
	    if($object->offSetExists(2)) {
	        /*** set the offset of 2 to a new value ***/
	        $object->offSetSet(2, 'Goanna');
	    }
	    /*** unset the kiwi ***/
	    foreach($object as $key=>$value) {
	        /*** check the value of the key ***/
	        if($object->offSetGet($key) === 'kiwi')
	        {
	            /*** unset the current key ***/
	            $object->offSetUnset($key);
	        }
	        echo '<li>'.$key.' - '.$value.'</li>'."\n";
	    }
	} catch (Exception $e)  {
	    echo $e->getMessage();
	}
    ?>

.. code-block:: php

    <?php
	$fruits = array(
	    "apple" => 'apple value',//position =0
	    "orange" => 'orange value',//position =1
	    "grape" => 'grape value',
	    "plum" => 'plum value',
	);

	echo '------------------普通数组遍历-----------------'."<br/>";

	foreach ($fruits as $key => $value) {
	    echo $key.":".$value."<br/>";
	}

	echo '------------------使用ArrayIterator迭代器遍历数组(foreach)-----------------'."<br/>";
	$obj = new ArrayObject($fruits);//创建数组对象
	$it = $obj->getIterator();//获取迭代器

	foreach ($it as $key => $value) {
	    echo $key.":".$value."<br/>";
	}

	echo '------------------(while)-----------------'."<br/>";

	$it->rewind();//如果要使用current必须使用rewind
	while ($it -> valid()) {
	    echo $it->key().":".$it->current()."<br/>";
	    $it -> next();
	}

	echo '------------------跳过某些元素进行打印(while)-----------------'."<br/>";

	$it->rewind();
	if($it->valid()){
	    $it -> seek(1);//当前指针指向position=1
	    while ($it -> valid()) {
	        echo $it->key().":".$it->current()."<br/>";
	        $it -> next();
	    }
	}

	echo '------------------用迭代器的key进行排序(ksort)-----------------'."<br/>";
	$it->ksort();
	foreach ($it as $key => $value) {
	    echo $key.":".$value."<br/>";
	}

	echo '------------------用迭代器的value进行排序(asort)-----------------'."<br/>";
	$it->asort();
	foreach ($it as $key => $value) {
	    echo $key.":".$value."<br/>";
	}
    ?>

在迭代时调用一个回调函数：

.. code-block:: php

    <?php
	class ArrayCallbackIterator extends ArrayIterator {
	    private $callback;

	    public function __construct ($value, $callback) {
	        parent::__construct($value);
	        $this->callback = $callback;
	    }

	    public function current () {
	        $value = parent::current();
	        return call_user_func($this->callback, $value);
	    }
	}

	$iterator1 = new ArrayCallbackIterator($valueList, "callback_function");
	$iterator2 = new ArrayCallbackIterator($valueList, array($object, "callback_class_method"));
    ?>

.. code-block:: php

    <?php
	$b = array('name'=>'mengzhi','age'=>'22','city'=>'shanghai');
	$a = new ArrayIterator($b);
	// 通过append添加和构造函数传入的是不同的
	$a->append(array('home'=>'china','work'=>'developer'));
	/*
	 ArrayIterator Object
	(
	    [storage:ArrayIterator:private] => Array
	        (
	            [name] => mengzhi
	            [age] => 22
	            [city] => shanghai
	            [0] => Array
	                (
	                    [home] => china
	                    [work] => developer
	                )
	        )
	)
	 */
	print_r($a); // 注意输出结构

	// 多维数组遍历
	$it = new RecursiveIteratorIterator( new RecursiveArrayIterator($a));

	foreach ($it as $key => $item) {
	    echo $key.':'.$item.PHP_EOL;
	}
    ?>

RecursiveArrayIterator
^^^^^^^^^^^^^^^^^^^^^^
ArrayIterator类和ArrayObject类，只支持遍历一维数组。如果要遍历多维数组，必须先用RecursiveIteratorIterator生成一个Iterator，然后再对这个Iterator使用RecursiveIteratorIterator。

RecursiveArrayIterator扩展了ArrayIterator并实现了RecursiveIterator，实现了getChildren（）和hasChildren（）两个RecursiveIterator接口方法。

这个迭代器允许您以与ArrayIterator相同的方式遍历数组和对象时删除值和修改值和键，此外还可以迭代当前迭代器条目。ArrayIterator的使用很简单，但仅限于单维数组。有时你会有一个多维数组，你会想递归迭代嵌套数组。

类定义如下：

.. literalinclude:: ./src/recursivearrayiterator.inc
   :language: php

.. code-block:: php

    <?php
	$myArray = array(0 => 'a', 1 => array('subA', 'subB', array(0 => 'subsubA', 1 => 'subsubB', 2 => array(0 => 'deepA', 1 => 'deepB'))), 2 => 'b', 3 => array('subA', 'subB', 'subC'), 4 => 'c');

	$iterator = new RecursiveArrayIterator($myArray);
	// 为迭代器中每个元素调用一个用户自定义函数
	iterator_apply($iterator, 'traverseStructure', array($iterator));

	function traverseStructure ($iterator) {

	    while ($iterator->valid()) {
	        if ($iterator->hasChildren()) {
	            traverseStructure($iterator->getChildren());
	        } else {
	            echo $iterator->key() . ' : ' . $iterator->current() . PHP_EOL;
	        }
	        $iterator->next();
	    }
	}
	// 另一种调用方法
	foreach(new RecursiveIteratorIterator(new RecursiveArrayIterator($myArray)) as $key=>$value) {
	    echo $key.' -- '.$value.PHP_EOL;
	}
    ?>

默认地，RecursiveArrayIterator将所有对象视为有子对象，并尝试递归到它们中。如果你只想让你的RecursiveIteratorIterator返回多维数组中的对象。我们需要设置 ``RecursiveArrayIterator::CHILD_ARRAYS_ONLY`` 的值。该设置类似如下代码：

.. code-block:: php

    <?php
	class RecursiveArrayOnlyIterator extends RecursiveArrayIterator {
	  public function hasChildren() {
	    return is_array($this->current());
	  }
	}
    ?>


EmptyIterator
-------------
空迭代器对象。

类定义如下：

.. literalinclude:: ./src/emptyiterator.inc
   :language: php

IteratorIterator
----------------
这个迭代器包装器允许将任何实现Traversable接口的对象转换成迭代器。而数组和对象没有实现该接口，所以不能直接作为参数传入。而对于实现 ``Iterator`` 接口和 ``IteratorAggregate`` 接口的类都可以作为参数传入。

类定义如下：

.. literalinclude:: ./src/iteratoriterator.inc
   :language: php

.. code-block:: php

    <?php
	/*
	 * 内部迭代器的指针移动不会影响IteratorIterator迭代器指针，
	 * 而IteratorIterator迭代器的rewind()和next()会同步到内部迭代器的指针
	 * */
	$ii = new IteratorIterator(new ArrayIterator(range(1,6)));
	$i1 = $ii->getInnerIterator(); // gets the real thing
	$i2 = $ii->getInnerIterator(); // ditto: $i2 === $i1 and the two are therefore in sync.
	echo $i1->current(); // 1
	echo $i1->key(); // 0
	var_dump($ii->valid()); // FALSE
	$i1->next(); // 能够影响$i2,它们是同一个引用对象
	echo $i1->key(); // 1
	var_dump($ii->valid()); // still FALSE
	$ii->rewind(); // 该操作同步重置$i1
	echo $ii->key(); // 0, as is $i1->key()
	$i1->next(); // next操作，脱离同步
	echo $ii->key(); // still 0
	echo $i1->key(); // 1
	$ii->next(); // 该操作同步内部迭代器
	echo $ii->key(); // 2
	echo $i1->key(); // 2
    ?>

.. code-block:: php

    <?php
	// make it or break it
	error_reporting(E_ALL);

	try {
	    $dsn = new PDO("sqlite2:/home/kevin/html/periodic.sdb");

	// the result only implements Traversable
	    $stmt = $dsn->prepare("SELECT * FROM periodic ORDER BY atomicnumber");

	// exceute the query
	    $stmt->execute();



	// by setting the FETCH mode we can set the resulting arrays to numerical or associative
	    $result = $stmt->setFetchMode(PDO::FETCH_ASSOC);

	// the result should be an instance of PDOStatement
	// IteratorIterator converts it and after that you can do any iterator operation with it
	// The iterator will fetch the results for us.
	    $it = new IteratorIterator($stmt);

	// Each array contains a result set from the db query
	    foreach ($it as $row) {
	        echo '<table style="border: solid 1px black; width: 300px;">';
	// iterate over the array with the ArrayIterator
	        foreach (new ArrayIterator($row) as $key => $val) {
	            echo '<tr><td style="width: 150px">' . $key . '</td><td>' . $val . '</td></tr>';
	        }
	        echo '</table>';
	    }

	// reset dsn
	    $dsn = null;
	} catch (PDOException $e) {
	    print "Error!: " . $e->getMessage() . "<br />";
	}
    ?>

重构上面的代码：

.. code-block:: php


	<tablestyle = "border: solid 1px black; width: 400px;" >
	<tr ><td > Atomic Number </td ><td > Latin</td ><td > English</td ><td > Abbr</td ></tr >

	<?php
	// make sure its broken
	error_reporting(E_ALL);

	// extend the RecursiveIteratorIterator
	class TableRows extends RecursiveIteratorIterator {
	    function __construct ($it) {
	// here we use the parent class and use LEAVES_ONLY to
	        parent::__construct($it, self::LEAVES_ONLY);
	    }

	    function beginChildren () {
	        echo '<tr>';
	    }

	    function endChildren () {
	        echo '</tr>' . "\n";
	    }

	} // end class

	 try {
	     $dsn = new PDO("sqlite2:/home/kevin/html/periodic.sdb");

	// the result only implements Traversable
	     $stmt = $dsn->prepare('SELECT * FROM periodic');

	// exceute the query
	     $stmt->execute();

	// by setting the FETCH mode we can set the resulting arrays to numerical or associative
	     $result = $stmt->setFetchMode(PDO::FETCH_ASSOC);

	// the result should be an instance of PDOStatement
	// IteratorIterator converts it and after that you can do any iterator operation with it
	// The iterator will fetch the results for us.

	     foreach (new TableRows(new RecursiveArrayIterator($stmt->fetchAll())) as $k => $v) {
	         echo '<td style="width: 150px; border: 1px solid black;">' . $v . '</td>';
	     }

	     $dsn = null;
	 } catch (PDOException $e) {
	     print "Error!: " . $e->getMessage() . '<br />';
	 }
	?>
	</table>

AppendIterator
^^^^^^^^^^^^^^^
对迭代器进行迭代。AppendIterator能包含多个迭代器， **按顺序迭代访问几个不同的迭代器** ，例如，希望在以此循环中迭代访问两个或者更多的组合，把两个或者更多的数组连接起来。注意是连接起来。

类的定义：

.. code-block:: php

    <?php
	AppendIterator extends IteratorIterator implements OuterIterator {

	    public __construct ( void )
	    // 增加一个迭代器
	    public void append ( Iterator $iterator )
	    public mixed current ( void )
	    // 此方法获取用于存储迭代器的ArrayIterator。
	    public ArrayIterator getArrayIterator ( void )
	    // 获取内部迭代器
	    public Iterator getInnerIterator ( void )
	    // 获取迭代器的索引
	    public int getIteratorIndex ( void )
	    public scalar key ( void )
	    public void next ( void )
	    public void rewind ( void )
	    public bool valid ( void )

	    /* 省略继承的方法 */
	}
    ?>

类定义如下：

.. literalinclude:: ./src/appenditerator.inc
   :language: php

.. code-block:: php

    <?php
	$array_a = new ArrayIterator(array('a' => 'a1', 'b' => 'b2', 'c' => 'c3'));
	$array_b = new ArrayIterator(array('d' => 'd4', 'e' => 'e5', 'f' => 'f6'));
	$iterator = new AppendIterator();
	// Appends an iterator
	$iterator->append($array_a);
	$iterator->append($array_b);
	for ($iterator->rewind(); $iterator->valid(); $iterator->next()) {
	    echo $iterator->key() . '=>' . $iterator->current() . PHP_EOL;
	}
    ?>

CachingIterator
^^^^^^^^^^^^^^^
对迭代器进行迭代，这个类支持缓存，且有一个hasNext()方法，用来判断是否还有下一个元素。

其中常量标志影响__toString()方法的返回值。

类的定义：

.. code-block:: php

    <?php
	CachingIterator extends IteratorIterator implements OuterIterator , ArrayAccess , Countable {
	    /* 常量 */
	    // 调用每个内部元素的__toString()作为__toString()方法的返回值
	    const integer CALL_TOSTRING = 1 ;
	    // 不要在访问孩子时抛出异常。
	    const integer CATCH_GET_CHILD = 16 ;
	    // 使用迭代的key值作为__toString()方法的返回值
	    const integer TOSTRING_USE_KEY = 2 ;
	    // 使用迭代的current值作为__toString()方法的返回值
	    const integer TOSTRING_USE_CURRENT = 4 ;
	    // 使用迭代器的__toString()值作为__toString()方法的返回值
	    const integer TOSTRING_USE_INNER = 8 ;
	    // 缓存所有读的数据
	    const integer FULL_CACHE = 256 ;
	    /* 方法 */
	    public __construct ( Iterator $iterator [, int $flags = self::CALL_TOSTRING ] )
	    // 迭代器中元素的数量
	    public int count ( void )
	    public void current ( void )
	    // 检索缓存的内容，当CachingIterator::FULL_CACHE 没有设置时，抛出异常
	    public array getCache ( void )
	    public int getFlags ( void )
	    public Iterator getInnerIterator ( void )
	    // 检查内部迭代器是否有有效的下一个元素
	    public void hasNext ( void )
	    public scalar key ( void )
	    public void next ( void )
	    public void offsetExists ( string $index )
	    public void offsetGet ( string $index )
	    public void offsetSet ( string $index , string $newval )
	    public void offsetUnset ( string $index )
	    public void rewind ( void )
	    public void setFlags ( int $flags )
	    public void __toString ( void )
	    public void valid ( void )
	}
    ?>

类定义如下：

.. literalinclude:: ./src/cachingiterator.inc
   :language: php

示例如下：

.. code-block:: php

    <?php
	/*** a simple array ***/
	$array = array('koala', 'kangaroo', 'wombat', 'wallaby', 'emu', 'kiwi', 'kookaburra', 'platypus');

	try {
	    /*** create a new object ***/
	    $object = new CachingIterator(new ArrayIterator($array));
	    foreach ($object as $value) {
	        echo $value;
	        if ($object->hasNext()) {
	            echo ',';
	        }
	    }
	} catch (Exception $e) {
	    echo $e->getMessage();
	}
    ?>

.. code-block:: php

    <?php
	$array = array('koala', 'kangaroo', 'wombat', 'wallaby', 'emu', 'kiwi', 'kookaburra', 'platypus');

	$cache  = new CachingIterator(new ArrayIterator(range(1,100)), CachingIterator::FULL_CACHE);

	foreach ($cache as $c) {

	}

	print_r($cache->getCache());
    ?>

.. code-block:: php

    <?php
	/*** a new caching object ***/
	$cacheObj = new CachingIterator(new MyArrayIterator($array), 0);
	try
	{
	    /*** set our flags ***/
	    //$cacheObj->setFlags(CachingIterator::CALL_TOSTRING);
	    //$cacheObj->setFlags(CachingIterator::TOSTRING_USE_KEY);
	    //$cacheObj->setFlags(CachingIterator::TOSTRING_USE_CURRENT);
	    $cacheObj->setFlags(CachingIterator::TOSTRING_USE_INNER);
	    /*** travers the iterator ***/
	    $i = 0;
	    foreach($cacheObj as $v)
	    {
	        /*** objects are now strings ***/
	        $i++;
	        echo $i.":".$cacheObj.PHP_EOL;
	    }
	}
	catch (Exception $e)
	{
	    echo 'Exception: ' . $e->getMessage() . "\n";
	}

	class MyArrayIterator extends ArrayIterator
	{
	    function __toString(){
	        return $this->key() . '===>' . $this->current();
	    }

	}/*** end of class ***/
    ?>

.. code-block:: php

    <?php
	/**
	 * 使用缓存迭代器在一维navigation数组中预先查看最后一个元素，以便我们可以精确设置最后的class样式。
	 * 注意：没有采取安全措施来消毒产量。
	 */

	$nav = array(
	    'Home' => '/home',
	    'Products' => '/products',
	    'Company' => '/company',
	    'Privacy Policy' => '/privacy-policy'
	);
	// storage of output
	$output = new ArrayIterator();
	try {

	    // create the caching iterator of the nav array
	    $it = new CachingIterator(new ArrayIterator($nav));
	    foreach ($it as $name => $url) {
	        if ($it->hasNext()) {
	            $output->append('<li><a href="' . $url . '">' . $name . '</a></li>');
	        } else {
	            $output->append('<li class="last"><a href="' . $url . '">' . $name . '</a></li>');
	        }
	    }

	    // if we have values, output the unordered list
	    if ($output->count()) {
	        echo '<ul id="nav">' . "\n" . implode("\n", (array) $output) . "\n" . '</ul>';
	    }

	} catch (Exception $e) {
	    die($e->getMessage());
	}
	/**
	 * 下面是同样的例子，但是是一个可以重用的扩展类
	 */
	class NavBuilder extends CachingIterator {

	    /**
	     * Override the current() method to modify the return value
	     * for the given index.
	     *
	     * @access  public
	     * @return  string
	     */
	    public function current()
	    {
	        // get the name and url of the nav item
	        $name = parent::key();
	        $url = parent::current();

	        // determine if we're on the last element
	        if ($this->hasNext()) {
	            return '<li><a href="' . $url . '">' . $name . '</a></li>';
	        } else {
	            return '<li class="last"><a href="' . $url . '">' . $name . '</a></li>';
	        }
	    }

	    /**
	     * Outputs the navigation.
	     */
	    public function generate()
	    {
	        $inner = $this->getInnerIterator();
	        var_dump(get_class_methods($inner));
	    }

	}
	try {
	    $it = new NavBuilder(new ArrayIterator($nav));
	    echo $it->generate();
	} catch (Exception $e) {
	    var_dump($e); die;
	}
    ?>

RecursiveCachingIterator
""""""""""""""""""""""""
在CachingIterator类的基础上实现了RecursiveIterator接口。实现对多维数组或容器进行迭代。

类定义如下：

.. literalinclude:: ./src/recursivecachingiterator.inc
   :language: php

例子：

.. literalinclude:: ./src/demo/recursive-caching-iterator.php
   :language: php

FilterIterator
^^^^^^^^^^^^^^^
对实现Iterator接口的迭代器进行过滤。需要继承并重写accept()方法。

这个抽象类遍历并过滤出不想要的值。这个类应该被实现了迭代过滤器的类继承 FilterIterator::accept()方法必须被子类实现。

FilterIterator类可以对元素进行过滤，只要在accept()方法中设置过滤条件就可以了。这个抽象类的遍历并过滤出不想要的值。这个类应该被实现了迭代过滤器的类继承 ``FilterIterator::accept()`` 方法必须被子类实现。

类定义如下：

.. literalinclude:: ./src/filteriterator.inc
   :language: php

.. code-block:: php

    <?php
	/*** a simple array ***/
	$animals = array('koala', 'kangaroo', 'wombat', 'wallaby', 'emu', 'NZ'=>'kiwi', 'kookaburra', 'platypus');

	class CullingIterator extends FilterIterator{

	    /*** The filteriterator takes  a iterator as param: ***/
	    public function __construct( Iterator $it ){
	        parent::__construct( $it );
	    }

	    /*** check if key is numeric ***/
	    function accept(){
	        return is_numeric($this->key());
	    }

	}/*** end of class ***/
	$cull = new CullingIterator(new ArrayIterator($animals));

	foreach($cull as $key=>$value)
	{
	    echo $key.' == '.$value.PHP_EOL;
	}
    ?>

下面是另一个返回质数的例子：

.. code-block:: php

    <?php
	class PrimeFilter extends FilterIterator {

	    /*** The filteriterator takes  a iterator as param: ***/
	    public function __construct (Iterator $it) {
	        parent::__construct($it);
	    }

	    /*** check if current value is prime ***/
	    function accept () {
	        if ($this->current() % 2 != 1) {
	            return false;
	        }
	        $d = 3;
	        $x = sqrt($this->current());
	        while ($this->current() % $d != 0 && $d < $x) {
	            $d += 2;
	        }
	        return (($this->current() % $d == 0 && $this->current() != $d) * 1) == 0 ? true : false;
	    }

	}

	/*** end of class ***/

	/*** an array of numbers ***/
	$numbers = range(212345, 212456);

	/*** create a new FilterIterator object ***/
	$primes = new primeFilter(new ArrayIterator($numbers));

	foreach ($primes as $value) {
	    echo $value . ' is prime.<br />';
	}
    ?>

CallbackFilterIterator
""""""""""""""""""""""
对实现Iterator接口的迭代器进行过滤。需要传入过滤函数。

CallbackFilterIterator继承并实现了FilterIterator类。只需要传入过滤函数即可。实现过滤功能不用继承FilterIterator类。

回调函数可以是函数名称，类方法和匿名函数，它们接受三个参数：分别为current，key，iterator

类定义如下：

.. literalinclude:: ./src/#
   :language: php

.. code-block:: php

    <?php
	/**
	 * Callback for CallbackFilterIterator
	 *
	 * @param $current   Current item's value
	 * @param $key       Current item's key
	 * @param $iterator  Iterator being filtered
	 * @return boolean   TRUE to accept the current item, FALSE otherwise
	 */
	function my_callback($current, $key, $iterator) {
	    // Your filtering code here
	}
    ?>

.. code-block:: php

    <?php
    // 单层目录遍历
	$dir = new FilesystemIterator(__DIR__);

	// Filter large files ( > 100MB)
	function is_large_file($current) {
	    return $current->isFile() && $current->getSize() > 104857600;
	}
	$large_files = new CallbackFilterIterator($dir, 'is_large_file');

	// Filter directories
	$files = new CallbackFilterIterator($dir, function ($current, $key, $iterator) {
	    return $current->isDir() && ! $iterator->isDot();
	});
    ?>

RecursiveCallbackFilterIterator
+++++++++++++++++++++++++++++++
对实现RecursiveIterator接口的迭代器进行过滤。需要传入过滤函数。

对于多层数组、目录树或者包含容器，需要使用该过滤迭代器。无须继承该类，只需要传入过滤函数即可。

在RecursiveIterator迭代器上进行递归操作，同时执行过滤和回调操作，在找到一个匹配的元素之后会调用回调函数。

类定义如下：

.. literalinclude:: ./src/#
   :language: php

例子：

.. code-block:: php

    <?php
	function doesntStartWithLetterT ($current) {
	    $rs = $current->getFileName();
	    return $rs[0] !== 'T';
	}

	$rdi = new RecursiveDirectoryIterator(__DIR__);
	$files = new RecursiveCallbackFilterIterator($rdi, 'doesntStartWithLetterT');
	foreach (new RecursiveIteratorIterator($files) as $file) {
	    echo $file->getPathname() . PHP_EOL;
	}
    ?>

RecursiveFilterIterator
""""""""""""""""""""""""
对实现RecursiveIterator接口的迭代器进行过滤。

对于多层数组、目录树或者包含容器，需要使用该过滤迭代器。需要继承该类并重写accept()方法。

类定义如下：

.. literalinclude:: ./src/recursivefilteriterator.inc
   :language: php

例子：

.. code-block:: php

    <?php
	class TestsOnlyFilter extends RecursiveFilterIterator {
	    public function accept () {
	        // 找出含有“叶”的元素
	        return $this->hasChildren() || (mb_strpos($this->current(), "叶") !== FALSE);
	    }
	}

	$array = array("叶1", array("李2", "叶3", "叶4"), "叶5");
	$iterator = new RecursiveArrayIterator($array);
	$filter = new TestsOnlyFilter($iterator);
	$filter = new RecursiveIteratorIterator($filter);
	//print_r(iterator_to_array($filter));
	foreach ($filter as $key => $value)
	    echo $key.":".$value.PHP_EOL;
    ?>

ParentIterator
+++++++++++++++
对实现RecursiveIterator接口的迭代器进行过滤。不需要继承。

ParentIterator只是一个RecursiveFilterIterator，它的accept（）方法调用RecursiveFilterIterator-> hasChildren（）方法来过滤自己。它滤除了叶节点。

类定义如下：

.. literalinclude:: ./src/parentiterator.inc
   :language: php

.. code-block:: php

    <?php
	$rdi = new RecursiveDirectoryIterator(__DIR__);
	$iter = new RecursiveIteratorIterator($rdi, RecursiveIteratorIterator::CHILD_FIRST);
	// 返回所有文件和目录
	foreach($iter as $item)
	    echo $item.PHP_EOL;

	echo "\n=======================\n";

	$dirsOnly = new ParentIterator($rdi);
	$iter = new RecursiveIteratorIterator($dirsOnly, RecursiveIteratorIterator::CHILD_FIRST);
	// 只返回目录过滤器
	foreach($dirsOnly  as $item)
	    echo $item.PHP_EOL;


	$hey = array("a" => "lemon", "b" => "orange", array("a" => "apple", "p" => "pear"));
	$arrayIterator = new RecursiveArrayIterator($hey);
	$it = new ParentIterator($arrayIterator);
	print_r(iterator_to_array($it));
    ?>

RegexIterator
"""""""""""""
继承FilterIterator，支持使用正则表达式模式匹配和修改迭代器中的元素。经常用于将字符串匹配。

模型常量列表：

- RegexIterator::ALL_MATCHES：返回所有匹配的实体(see preg_match_all())；
- RegexIterator::GET_MATCH：返回第一个匹配的实体(see preg_match())；
- RegexIterator::MATCH：仅仅是对当前实体执行匹配(see preg_match())；
- RegexIterator::REPLACE：替换当前匹配实体(see preg_replace(); 未完全实现)
- RegexIterator::SPLIT：返回对当前实体的切分值(see preg_split())；

标志常量列表：

- RegexIterator::USE_KEY：匹配key而不是实体值；

类定义如下：

.. literalinclude:: ./src/regexiterator.inc
   :language: php

例子：

.. code-block:: php

    <?php
	$a = new ArrayIterator(array('test1', 'test2', 'test3'));
	$i = new RegexIterator($a, '/^(test)(\d+)/', RegexIterator::REPLACE);
	$i->replacement = '$2:$1';
	print_r(iterator_to_array($i));

	/**output
	Array
	(
	[0] => 1:test
	[1] => 2:test
	[2] => 3:test
	)
	 **/
    ?>

RecursiveRegexIterator
++++++++++++++++++++++
是RegexIterator迭代器的递归形式，只接受RecursiveIterator迭代器作为迭代对象。

类定义如下：

.. literalinclude:: ./src/recursiveregexiterator.inc
   :language: php

例子：

.. code-block:: php

    <?php
	$rArrayIterator = new RecursiveArrayIterator(array('叶1', array('tet3', '叶4', '叶5')));
	$rRegexIterator = new RecursiveRegexIterator($rArrayIterator, '/^叶/',
	    RecursiveRegexIterator::ALL_MATCHES);

	foreach ($rRegexIterator as $key1 => $value1) {
	    if ($rRegexIterator->hasChildren()) {
	        // print all children
	        echo "Children: ";
	        foreach ($rRegexIterator->getChildren() as $key => $value) {
	            echo $value . " ";
	        }
	        echo "\n";
	    } else {
	        echo "No children\n";
	    }
	}
	/**output
	No children
	Children: 叶4 叶5
	 **/
    ?>

InfiniteIterator
^^^^^^^^^^^^^^^^^
InfiniteIterator允许无限地遍历一个迭代器，而不必在到达结束时手动重置迭代器。

类定义如下：

.. literalinclude:: ./src/infiniteiterator.inc
   :language: php

例子：

.. code-block:: php

    <?php
	$obj = new stdClass();
	$obj->Mon = "Monday";
	$obj->Tue = "Tuesday";
	$obj->Wed = "Wednesday";
	$obj->Thu = "Thursday";
	$obj->Fri = "Friday";
	$obj->Sat = "Saturday";
	$obj->Sun = "Sunday";

	$infinate = new InfiniteIterator(new ArrayIterator($obj));
	foreach ( new LimitIterator($infinate, 0, 14) as $value ) {
	    print($value . PHP_EOL);
	}
    ?>

LimitIterator
^^^^^^^^^^^^^^
这个类用来限定返回结果集的数量和位置，必须提供offset和limit两个参数，与SQL命令中limit语句类似。

类定义如下：

.. literalinclude:: ./src/limititerator.inc
   :language: php

示例如下：

.. code-block:: php

    <?php
	/*** the offset value ***/
	$offset = 3;

	/*** the limit of records to show ***/
	$limit = 2;

	$array = array('koala', 'kangaroo', 'wombat', 'wallaby', 'emu', 'kiwi', 'kookaburra', 'platypus');

	$it = new LimitIterator(new ArrayIterator($array), $offset, $limit);

	foreach ($it as $k => $v) {
	    echo $it->getPosition() . ' ' . $v . PHP_EOL;
	}

	// Loop from third item until the end
	// Note: offset starts from zero for apple
	foreach (new LimitIterator($array, 2) as $item) {
	    echo $item.PHP_EOL;
	}
    ?>

.. code-block:: php

    <?php
	/*** a simple array ***/
	$array = array('koala', 'kangaroo', 'wombat', 'wallaby', 'emu', 'kiwi', 'kookaburra', 'platypus');

	$it = new LimitIterator(new ArrayIterator($array));

	try {
	    $it->seek(5);

	     echo $it->current().PHP_EOL;

	    // 调用foreach会重置指针
	    foreach ($it as $item){
	        echo $it->getPosition() . ' ' . $item. PHP_EOL;
	    }

	} catch (OutOfBoundsException $e) {
	    echo $e->getMessage() . "<br />";
	}
    ?>

NoRewindIterator
^^^^^^^^^^^^^^^^^
不能重置的迭代器，用于不能多次迭代的集合，适用于在迭代过程中执行一次性操作。

类定义如下：

.. literalinclude:: ./src/norewinditerator.inc
   :language: php

例子：

.. code-block:: php

    <?php
	$fruit = array('apple', 'banana', 'cranberry');
	$arr = new ArrayObject($fruit);
	$it = new NoRewindIterator($arr->getIterator());
	echo "Fruit A:\n";
	foreach ($it as $item) {
	    echo $item . "\n";
	}

	echo "Fruit B:\n";
	foreach ($it as $item) {
	    echo $item . "\n";
	}
	/**output
	Fruit A:
	apple
	banana
	cranberry
	Fruit B:
	 **/
    ?>

MultipleIterator
-----------------
一个遍历所有附加迭代器的迭代器。它实际上是一个并行迭代器：你可以用一个键，整数或NULL来附加一个或多个迭代器，当迭代MultipleIterator时，将所有附加迭代器的所有结果以一个数组作为current（）的结果，key（）调用也是如此。

- MultipleIterator::MIT_NEED_ANY：如果$flags设置为ANY，只要有一个迭代器有效，则valid（）将是有效的，在最后一个有效迭代器结束之前，其它无效的迭代器将返回NULL值；
- MultipleIterator::MIT_NEED_ALL：如果$flags设置为ALL，当第一个迭代器停止传递结果时，迭代停止；
- MultipleIterator::MIT_KEYS_NUMERIC：使用子迭代器的位置创建的最终的key；
- MultipleIterator::MIT_KEYS_ASSOC：使用子迭代器相关信息创建的最终的key；

类定义如下：

.. literalinclude:: ./src/multipleiterator.inc
   :language: php

.. code-block:: php

    <?php
	$array_a = new ArrayIterator(array('01','02','03'));
	$array_b = new ArrayIterator(array('张三','李四','王五'));
	$mit = new MultipleIterator(MultipleIterator::MIT_NEED_ALL|MultipleIterator::MIT_KEYS_ASSOC);//MIT_KEYS_ASSOC:使用迭代器关联信息作为key
	$mit -> attachIterator($array_a,"ID");
	$mit -> attachIterator($array_b,"NAME");
	foreach ($mit as $value) {
	    print_r($value);
	}
    ?>

RecursiveIteratorIterator
--------------------------
RecursiveIteratorIterator实现了OuterIterator，并且是一个包装器，它将启用实现RecursiveIterator接口的迭代器。

RecursiveIterator是统一递归的接口。PHP提供了具体的RecursiveIteratorIterator类，在调用foreach时，它在内部使用RecursiveIterator为我们做实际的递归。这消除了使用RecursiveIterator编写我们自己的递归函数的需要。

RecursiveIteratorIterator允许您用foreach迭代多维数组或树结构。它的构造函数需要一个RecursiveIterator实例（构造函数实际上需要一个Iterator或者IteratorAggregate，但它的目的是与RecursiveIterator一起使用）。

类定义如下：

.. literalinclude:: ./src/recursiveiteratoriterator.inc
   :language: php

RecursiveIteratorIterator操作模式:

- LEAVES_ONLY：仅仅显示叶子节点；
- SELF_FIRST：优先显示父节点,如包含是数组，则先显示整个数组，然后迭代内部的值；
- CHILD_FIRST：优先显示孩子节点，如包含是数组，则先迭代内部的值，然后显示整个数组；

RecursiveIteratorIterator操作标志：

- CATCH_GET_CHILD：在getChildren()调用时捕获异常，然后跳转到下一个元素

RecursiveTreeIterator
^^^^^^^^^^^^^^^^^^^^^^
允许遍历RecursiveIterator来生成一个ASCII图形树。

- RecursiveTreeIterator::BYPASS_CURRENT
- RecursiveTreeIterator::BYPASS_KEY
- RecursiveTreeIterator::PREFIX_LEFT
- RecursiveTreeIterator::PREFIX_MID_HAS_NEXT
- RecursiveTreeIterator::PREFIX_MID_LAST
- RecursiveTreeIterator::PREFIX_END_HAS_NEXT
- RecursiveTreeIterator::PREFIX_END_LAST
- RecursiveTreeIterator::PREFIX_RIGHT

类定义如下：

.. literalinclude:: ./src/recursivetreeiterator.inc
   :language: php

例子：

.. code-block:: php

    <?php
	$hey = array("a" => "lemon", "b" => "orange", array("a" => "apple", "p" => "pear"));
	$awesome = new RecursiveTreeIterator(
	    new RecursiveArrayIterator($hey),
	    null, null, RecursiveIteratorIterator::LEAVES_ONLY
	);
	foreach ($awesome as $line)
	    echo $line . PHP_EOL;
    ?>