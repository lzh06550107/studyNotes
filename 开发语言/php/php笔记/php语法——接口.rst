*********
预定义和SPL接口
*********

接口
====

预定义接口
---------
Traversable（遍历）接口
^^^^^^^^^^^^^^^^^^^^^^
这是一个无法在 PHP 脚本中实现的内部引擎接口。IteratorAggregate 或Iterator 接口可以用来代替它。实现此接口的内建类可以使用 foreach 进行遍历而无需实现 IteratorAggregate 或 Iterator 接口。没有实现该接口的类要使用foreach，则必须实现 IteratorAggregate 或者 Iterator接口。

接口摘要：

.. code-block:: php

    <?php
	Traversable {
	}
    ?>

Traversable 重要的一个用处就是判断一个类是否可以遍历，下面是官方例子：

.. code-block:: php

    <?php
	if( !is_array( $items ) && !$items instanceof Traversable )
        //Throw exception here
    // PHP7提供is_iterable()方法来检测
    if ( !is_iterable( $items ))
        //Throw exception here
    ?>

需要注意的是，数组和对象可以通过foreach遍历，但它们没有实现Traversable接口，所以不是Traversable的示例：

.. code-block:: php

    <?php
	$array=[1,2,3];
	$obj = (object) $array;
	var_dump($array instanceof \Traversable); //输出 boolean false
	var_dump($obj instanceof \Traversable); //输出 boolean false
    ?>

.. note:: 类未实现Iterator接口或者IteratorAggregate接口时，执行foreach遍历将输出所有其能够访问的可见属性。

Iterator（迭代器）接口
^^^^^^^^^^^^^^^^^^^^^
PHP Iterator接口的作用是允许对象以自己的方式迭代内部的数据，从而使它可以被循环访问，Iterator接口摘要如下：

.. code-block:: php

    <?php
	Iterator extends Traversable {
	    //返回当前索引游标指向的元素
	    abstract public mixed current ( void )
	    //返回当前索引游标指向的键
	    abstract public scalar key ( void )
	    //移动当前索引游标到下一元素
	    abstract public void next ( void )
	    //重置索引游标
	    abstract public void rewind ( void )
	    //判断当前索引游标指向的元素是否有效
	    abstract public boolean valid ( void )
	}
    ?>

foreach循环中各个方法的调用顺序：

1. 在循环第一次迭代前，调用 ``Iterator::rewind()`` 方法；
2. 在循环每一次迭代前，调用 ``Iterator::valid()`` 方法；
3. 如果 ``Iterator::valid()`` 返回false，循环结束；如果返回true， ``Iterator::current()``  和 ``Iterator::key()`` 被调用,注意key()方法只有在需要的时候才调用；
4. 循环主体被执行；
5. 在循环每一次迭代后， ``Iterator::next()`` 被调用，然后重复前面的步骤2；

等价于：

.. code-block:: php

    <?php
	$it->rewind();
	while ($it->valid()) {
	    $key = $it->key();
	    $value = $it->current();

	    // ...

	    $it->next();
	}
    ?>



例子：

.. code-block:: php


	<?php
	/**
	 * 该类允许外部迭代自己内部私有属性$_test，并演示迭代过程
	 *
	 * @author 疯狂老司机
	 */
	class TestIterator implements Iterator {

	    /*
	     * 定义要进行迭代的数组
	     */
	    private $_test = array('dog', 'cat', 'pig');

	    /*
	     * 索引游标
	     */
	    private $_key = 0;

	    /*
	     * 执行步骤
	     */
	    private $_step = 0;

	    /**
	     * 将索引游标指向初始位置
	     *
	     * @see TestIterator::rewind()
	     */
	    public function rewind() {
	        echo '第'.++$this->_step.'步：执行 '.__METHOD__.'<br>';
	        $this->_key = 0;
	    }

	    /**
	     * 判断当前索引游标指向的元素是否设置
	     *
	     * @see TestIterator::valid()
	     * @return bool
	     */
	    public function valid() {
	        echo '第'.++$this->_step.'步：执行 '.__METHOD__.'<br>';
	        return isset($this->_test[$this->_key]);
	    }

	    /**
	     * 将当前索引指向下一位置
	     *
	     * @see TestIterator::next()
	     */
	    public function next() {
	        echo '第'.++$this->_step.'步：执行 '.__METHOD__.'<br>';
	        $this->_key++;
	    }
	    /**
	     * 返回当前索引游标指向的元素的值
	     *
	     * @see TestIterator::current()
	     * @return value
	     */
	    public function current() {
	        echo '第'.++$this->_step.'步：执行 '.__METHOD__.'<br>';
	        return $this->_test[$this->_key];
	    }

	    /**
	     * 返回当前索引值
	     *
	     * @return key
	     * @see TestIterator::key()
	     */
	    public function key() {
	        echo '第'.++$this->_step.'步：执行 '.__METHOD__.'<br>';
	        return $this->_key;
	    }
	}

	$iterator = new TestIterator();
	foreach($iterator as $key => $value){
	    echo "输出索引为{$key}的元素".":$value".'<br><br>';
	}
	/*
	以上例子将输出：
	第1步：执行 TestIterator::rewind
	第2步：执行 TestIterator::valid
	第3步：执行 TestIterator::current
	第4步：执行 TestIterator::key
	输出索引为0的元素:dog

	第5步：执行 TestIterator::next
	第6步：执行 TestIterator::valid
	第7步：执行 TestIterator::current
	第8步：执行 TestIterator::key
	输出索引为1的元素:cat

	第9步：执行 TestIterator::next
	第10步：执行 TestIterator::valid
	第11步：执行 TestIterator::current
	第12步：执行 TestIterator::key
	输出索引为2的元素:pig

	第13步：执行 TestIterator::next
	第14步：执行 TestIterator::valid
	从以上例子可以看出，如果执行valid返回false，则循环就此结束。
	 */
	?>

.. code-block:: php

    <?php
	/**
	 *  本地数组的迭代器
	 */
	class ArrayReloaded implements Iterator {

	    /**
	     *  需要被迭代的数组
	     */
	    private $array = array();

	    /**
	     *  跟踪是否到达数组末尾的开关
	     */
	    private $valid = FALSE;

	    function __construct($array) {
	        $this->array = $array;
	    }

	    /**
	     * Return the array "pointer" to the first element
	     * PHP's reset() returns false if the array has no elements
	     */
	    function rewind(){
	        $this->valid = (FALSE !== reset($this->array));
	    }

	    /**
	     * Return the current array element
	     */
	    function current(){
	        return current($this->array);
	    }

	    /**
	     * Return the key of the current array element
	     */
	    function key(){
	        return key($this->array);
	    }

	    /**
	     * Move forward by one
	     * PHP's next() returns false if there are no more elements
	     */
	    function next(){
	        $this->valid = (FALSE !== next($this->array));
	    }

	    /**
	     * Is the current element valid?
	     */
	    function valid(){
	        return $this->valid;
	    }
	}
    ?>

使用方法如下：

.. code-block:: php

    <?php
	// Create iterator object
	$colors = new ArrayReloaded(array ('red','green','blue',));

	// Iterate away!
	foreach ( $colors as $color ) {
	 echo $color."<br>";
	}
	//////////////同样可以使用这种形式/////////////
	// Display the keys as well
	foreach ( $colors as $key => $color ) {
	 echo "$key: $color<br>";
	}
	/////////////除了foreach循环外，也可以使用while循环///////////////////
	// Reset the iterator - foreach does this automatically
	$colors->rewind();

	// Loop while valid
	while ( $colors->valid() ) {

	   echo $colors->key().": ".$colors->current()."";
	   $colors->next();

	}
    ?>

根据测试，while循环要稍快于foreach循环，因为运行时少了一层中间调用。

ArrayAccess（数组式访问）接口
^^^^^^^^^^^^^^^^^^^^^^^^^^^^
ArrayAccess接口又叫数组式访问接口，该接口的作用是提供像访问数组一样访问对象的能力。

在ArrayAccess对象中使用的索引并不局限于像数组字符串和整数，只要您编写实现来处理索引，就可以使用任何类型的索引。这个事实被SplObjectStorage类所利用。

接口摘要如下：

.. code-block:: php

    <?php
    // 获取一个偏移位置的值
    abstract public mixed offsetGet ( mixed $offset )
    // 设置一个偏移位置的值
    abstract public void offsetSet ( mixed $offset , mixed $value )
    // 检查一个偏移位置是否存在，如果在实现ArrayAccess的类的对象上调用array_key_exists（），则不会调用ArrayAccess :: offsetExists（）
    abstract public boolean offsetExists ( mixed $offset )
    // 删除一个偏移位置的值
    abstract public void offsetUnset ( mixed $offset )
    ?>

例子：

.. code-block:: php


	<?php
	/**
	* ArrayAndObjectAccess
	* 该类允许以数组或对象的方式进行访问
	*/
	class ArrayAndObjectAccess implements ArrayAccess {

	    /**
	     * 定义一个数组用于保存数据
	     *
	     * @access private
	     * @var array
	     */
	    private $data = [];

	    /**
	     * 以对象方式访问数组中的数据
	     *
	     * @access public
	     * @param string 数组元素键名
	     */
	    public function __get($key) {
	        return $this->data[$key];
	    }

	    /**
	     * 以对象方式添加一个数组元素
	     *
	     * @access public
	     * @param string 数组元素键名
	     * @param mixed  数组元素值
	     * @return mixed
	     */
	    public function __set($key,$value) {
	        $this->data[$key] = $value;
	    }

	    /**
	     * 以对象方式判断数组元素是否设置
	     *
	     * @access public
	     * @param 数组元素键名
	     * @return boolean
	     */
	    public function __isset($key) {
	        return isset($this->data[$key]);
	    }

	    /**
	     * 以对象方式删除一个数组元素
	     *
	     * @access public
	     * @param 数组元素键名
	     */
	    public function __unset($key) {
	        unset($this->data[$key]);
	    }

	    /**
	     * 以数组方式向data数组添加一个元素
	     *
	     * @access public
	     * @abstracting ArrayAccess
	     * @param string 偏移位置
	     * @param mixed  元素值
	     */
	    public function offsetSet($offset,$value) {
	        if (is_null($offset)) {
	            $this->data[] = $value;
	        } else {
	            $this->data[$offset] = $value;
	        }
	    }

	    /**
	     * 以数组方式获取data数组指定位置元素
	     *
	     * @access public
	     * @abstracting ArrayAccess
	     * @param 偏移位置
	     * @return mixed
	     */
	    public function offsetGet($offset) {
	        return $this->offsetExists($offset) ? $this->data[$offset] : null;
	    }

	    /**
	     * 以数组方式判断偏移位置元素是否设置
	     *
	     * @access public
	     * @abstracting ArrayAccess
	     * @param 偏移位置
	     * @return boolean
	     */
	    public function offsetExists($offset) {
	        return isset($this->data[$offset]);
	    }

	    /**
	     * 以数组方式删除data数组指定位置元素
	     *
	     * @access public
	     * @abstracting ArrayAccess
	     * @param 偏移位置
	     */
	    public function offsetUnset($offset) {
	        if ($this->offsetExists($offset)) {
	            unset($this->data[$offset]);
	        }
	    }

	}

	$animal = new ArrayAndObjectAccess();

	$animal->dog = 'dog'; // 调用ArrayAndObjectAccess::__set
	$animal['pig'] = 'pig'; // 调用ArrayAndObjectAccess::offsetSet
	var_dump(isset($animal->dog)); // 调用ArrayAndObjectAccess::__isset
	var_dump(isset($animal['pig'])); // 调用ArrayAndObjectAccess::offsetExists
	var_dump($animal->pig); // 调用ArrayAndObjectAccess::__get
	var_dump($animal['dog']); // 调用ArrayAndObjectAccess::offsetGet
	unset($animal['dog']); // 调用ArrayAndObjectAccess::offsetUnset
	unset($animal->pig); // 调用ArrayAndObjectAccess::__unset
	var_dump($animal['pig']); // 调用ArrayAndObjectAccess::offsetGet
	var_dump($animal->dog); // 调用ArrayAndObjectAccess::__get
	/*
	以上输出：
	boolean true
	boolean true
	string 'pig' (length=3)
	string 'dog' (length=3)
	null
	null
	 */
	?>

.. code-block:: php

    <?php
	/**
	 * A class that can be used like an array
	 */
	class Article implements ArrayAccess {

	    public $title;

	    public $author;

	    public $category;

	    function __construct($title,$author,$category) {
	        $this->title = $title;
	        $this->author = $author;
	        $this->category = $category;
	    }

	    /**
	     * Defined by ArrayAccess interface
	     * Set a value given it's key e.g. $A['title'] = 'foo';
	     * @param mixed key (string or integer)
	     * @param mixed value
	     * @return void
	     */
	    function offsetSet($key, $value) {
	        if ( array_key_exists($key,get_object_vars($this)) ) {
	            $this->{$key} = $value;
	        }
	    }

	    /**
	     * Defined by ArrayAccess interface
	     * Return a value given it's key e.g. echo $A['title'];
	     * @param mixed key (string or integer)
	     * @return mixed value
	     */
	    function offsetGet($key) {
	        if ( array_key_exists($key,get_object_vars($this)) ) {
	            return $this->{$key};
	        }
	    }

	    /**
	     * Defined by ArrayAccess interface
	     * Unset a value by it's key e.g. unset($A['title']);
	     * @param mixed key (string or integer)
	     * @return void
	     */
	    function offsetUnset($key) {
	        if ( array_key_exists($key,get_object_vars($this)) ) {
	            unset($this->{$key});
	        }
	    }

	    /**
	     * Defined by ArrayAccess interface
	     * Check value exists, given it's key e.g. isset($A['title'])
	     * @param mixed key (string or integer)
	     * @return boolean
	     */
	    function offsetExists($offset) {
	        return array_key_exists($offset,get_object_vars($this));
	    }

	}

	// Create the object
	$A = new Article('SPL Rocks','Joe Bloggs', 'PHP');

	// Check what it looks like
	echo 'Initial State:<div>';
	print_r($A);
	echo '</div>';

	// Change the title using array syntax
	$A['title'] = 'SPL _really_ rocks';

	// Try setting a non existent property (ignored)，会被忽略
	$A['not found'] = 1;

	// Unset the author field
	unset($A['author']);

	// Check what it looks like again
	echo 'Final State:<div>';
	print_r($A);
	echo '</div>';
	/* 输出结果
	Initial State:

	Article Object
	(
	   [title] => SPL Rocks
	   [author] => Joe Bloggs
	   [category] => PHP
	)

	Final State:

	Article Object
	(
	   [title] => SPL _really_ rocks
	   [category] => PHP
	)
	*/
    ?>

IteratorAggregate（聚合式迭代器）接口
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
但是，虽然$A可以像数组那样操作，却无法使用foreach遍历，除非实现了前面提到的Iterator接口。

另一个解决方法是，有时会需要将数据和遍历部分分开，这时就可以实现IteratorAggregate接口。它规定了一个getIterator()方法，返回一个实现Iterator接口的对象。

IteratorAggregate 接口是用将Iterator接口要求实现的5个迭代器方法委托给其他类的。这让你可以在类的外部实现迭代功能.并允许重新使用常用的迭代器方法，而不是在编写的每个可迭代类中重复使用这些方法。

实现getIterator方法时，必须返回一个实现了Iterator接口的类的实例。通常在getIterator()方法内部，你会把类的信息传递给一个特殊的迭代器类的构造函数。这个数据可能是一个基础数组或者任何能够想到的其他数据,只要它足够控制5个Iterator方法即可。

SPL提供了些专门用来与IteratorAggregate接口一起使用的内置迭代器。使用这些迭代器意味着只需要实现一个方法并实例化一个类就可以使对象可迭代访问了。

创建外部迭代器的接口,接口摘要如下：

.. code-block:: php

    <?php
	IteratorAggregate extends Traversable {
		// 在foreach结构中该方法被自动调用
		abstract public Traversable getIterator ( void )
	}
    ?>

实现getIterator方法时必须返回一个实现了Iterator接口的类的实例。

还是以上一节的Article类为例：

.. code-block:: php

    <?php
	class Article implements ArrayAccess, IteratorAggregate {

	    /**
	     * Defined by IteratorAggregate interface
	     * Returns an iterator for for this object, for use with foreach
	     * @return ArrayIterator
	     */
	    function getIterator ()
	    {
	        return new ArrayIterator($this);
	    }
	}

	$A = new Article('SPL Rocks','Joe Bloggs', 'PHP');

	// Loop (getIterator will be called automatically)
	echo 'Looping with foreach:<div>';
	foreach ( $A as $field => $value ) {
	    echo "$field : $value<br>";
	}
	echo '</div>';

	// Get the size of the iterator (see how many properties are left)
	echo "Object has ".sizeof($A->getIterator())." elements";

	/* 输出结果
	Looping with foreach:

	title : SPL Rocks
	author : Joe Bloggs
	category : PHP

	Object has 3 elements
	*/
    ?>

.. code-block:: php

    <?php
	/**
	 * 利用聚合式迭代器，并返回一个实现了Iterator接口的类的实例
	 */
	class myData implements IteratorAggregate {
	    public $one = "Public property one";
	    public $two = "Public property two";
	    public $three = "Public property three";

	    public function __construct() {
	        $this->last = "last property";
	    }

	    public function getIterator() {
	        return new ArrayIterator($this); //对象也可以传入？？对象可以作为数组访问
	    }
	}

	$obj = new myData;

	foreach($obj as $key => $value) {
	    var_dump($key, $value);
	    echo '<br>';// Linux：echo "\n";
	}
	/*
	以上例子输出：
	string 'one' (length=3)
	string 'Public property one' (length=19)

	string 'two' (length=3)
	string 'Public property two' (length=19)

	string 'three' (length=5)
	string 'Public property three' (length=21)

	string 'last' (length=4)
	string 'last property' (length=13)

	ArrayIterator迭代器会把对象或数组封装为一个可以通过foreach来操作的类，具体SPL 迭代器后面会具体介绍。
	 */
    ?>

Serializable 序列化接口
^^^^^^^^^^^^^^^^^^^^^^^
PHP Serializable是自定义序列化的接口。实现此接口的类将不再支持__sleep()和__wakeup()，当类的实例被序列化时将自动调用serialize方法，并且不会调用 __destruct()或有其他影响。当类的实例被反序列化时，将调用unserialize()方法，并且不执行__construct()。接口摘要如下：

.. code-block:: php

	<?php
	Serializable {
	    abstract public string serialize ( void )
	    abstract public mixed unserialize ( string $serialized )
	}
	?>

例子：

.. code-block:: php


	<?php
	/**
	 * 类自定义序列化相关操作
	 *
	 * @author 疯狂老司机
	 */
	class obj implements Serializable {

	    private $data;
	    private $step = 0;

	    /*
	     * 构造函数
	     */
	    public function __construct() {
	        $this->data = "这是一段测试文字<br>";
	        echo '调用构造函数<br>';
	    }

	    public function serialize() {
	        return serialize($this->data);
	    }

	    public function unserialize($data) {
	        $this->step++;
	        $this->data = unserialize($data);
	    }

	    /*
	     * 析构函数
	     */
	    public function __destruct() {
	        echo 'step:'.$this->step.' 调用析构函数<br>';
	    }
	    public function getData(){
	        return $this->data;
	    }
	}

	$obj = new obj;// 调用obj::__construct
	$ser = serialize($obj);// 调用obj::serialize
	$newobj = unserialize($ser);// 调用obj::unserialize
	echo $newobj->getData();// 调用obj::getData
	// 执行结束，调用析构函数，先执行newobj对象的析构函数在执行obj对象的析构函数

	/*
	以上例子输出：
	调用构造函数
	这是一段测试文字
	step:1 调用析构函数
	step:0 调用析构函数
	 */
	?>

Closure 类
^^^^^^^^^^^
PHP Closure 类是用于代表匿名函数的类，匿名函数（在 PHP 5.3 中被引入）会产生这个类型的对象，Closure类摘要如下：

.. code-block:: php

	<?php
	Closure {
	    __construct ( void )
	    public static Closure bind (Closure $closure , object $newthis [, mixed $newscope = 'static' ])
	    public Closure bindTo (object $newthis [, mixed $newscope = 'static' ])
	}
	?>

方法说明：

- Closure::__construct — 用于禁止实例化的构造函数
- Closure::bind — 复制一个闭包，绑定指定的$this对象和类作用域。
- Closure::bindTo — 复制当前闭包对象，绑定指定的$this对象和类作用域。

除了此处列出的方法，还有一个 __invoke 方法。这是为了与其他实现了 __invoke()魔术方法的对象保持一致性，但调用闭包对象的过程与它无关。
下面将介绍Closure::bind和Closure::bindTo。

Closure::bind是Closure::bindTo的静态版本，其说明如下：

``public static Closure bind (Closure $closure , object $newthis [, mixed $newscope = 'static' ])``

- closure表示需要绑定的闭包对象。
- newthis表示需要绑定到闭包对象的对象，或者NULL创建未绑定的闭包。
- newscope表示想要绑定给闭包的类作用域，可以传入类名或类的示例，默认值是 'static'， 表示不改变。
- 该方法成功时返回一个新的 Closure 对象，失败时返回FALSE。

例子说明：

.. code-block:: php

	<?php
	/**
	 * 复制一个闭包，绑定指定的$this对象和类作用域。
	 */
	class Animal {
	    private static $cat = "cat";
	    private $dog = "dog";
	    public $pig = "pig";
	}

	/*
	 * 获取Animal类静态私有成员属性
	 */
	$cat = static function() {
	    return Animal::$cat;
	};

	/*
	 * 获取Animal实例私有成员属性
	 */
	$dog = function() {
	    return $this->dog;
	};

	/*
	 * 获取Animal实例公有成员属性
	 */
	$pig = function() {
	    return $this->pig;
	};

	$bindCat = Closure::bind($cat, null, new Animal());// 给闭包绑定了Animal实例的作用域，但未给闭包绑定$this对象
	$bindDog = Closure::bind($dog, new Animal(), 'Animal');// 给闭包绑定了Animal类的作用域，同时将Animal实例对象作为$this对象绑定给闭包
	$bindPig = Closure::bind($pig, new Animal());// 将Animal实例对象作为$this对象绑定给闭包,保留闭包原有作用域
	echo $bindCat(),'<br>';// 根据绑定规则，允许闭包通过作用域限定操作符获取Animal类静态私有成员属性
	echo $bindDog(),'<br>';// 根据绑定规则，允许闭包通过绑定的$this对象(Animal实例对象)获取Animal实例私有成员属性
	echo $bindPig(),'<br>';// 根据绑定规则，允许闭包通过绑定的$this对象获取Animal实例公有成员属性
	/*
	输出：
	cat
	dog
	pig
	 */
	?>

Closure::bindTo — 复制当前闭包对象，绑定指定的$this对象和类作用域，其说明如下：

``public Closure Closure::bindTo (object $newthis [, mixed $newscope = 'static' ])``

- newthis表示绑定给闭包对象的一个对象，或者NULL来取消绑定。
- newscope表示关联到闭包对象的类作用域，可以传入类名或类的实例，默认值是 'static'， 表示不改变。
- 该方法创建并返回一个闭包对象，它与当前对象绑定了同样变量，但可以绑定不同的对象，也可以绑定新的类作用域。绑定的对象决定了返回的闭包对象中的$this的取值，类作用域决定返回的闭包对象能够调用哪些方法，也就是说，此时$this可以调用的方法，与newscope类作用域相同。

.. code-block:: php

	<?php
	/**
	 * 一个基本的购物车，包括一些已经添加的商品和每种商品的数量
	 *
	 * @author 疯狂老司机
	 */
	class Cart {
	    // 定义商品价格
	    const PRICE_BUTTER  = 1.00;
	    const PRICE_MILK    = 3.33;
	    const PRICE_EGGS    = 8.88;

	    protected   $products = array();

	    /**
	     * 添加商品和数量
	     *
	     * @access public
	     * @param string 商品名称
	     * @param string 商品数量
	     */
	    public function add($item, $quantity) {
	        $this->products[$item] = $quantity;
	    }

	    /**
	     * 获取单项商品数量
	     *
	     * @access public
	     * @param string 商品名称
	     */
	    public function getQuantity($item) {
	        return isset($this->products[$item]) ? $this->products[$item] : FALSE;
	    }

	    /**
	     * 获取总价
	     *
	     * @access public
	     * @param string 税率
	     */
	    public function getTotal($tax) {
	        $total = 0.00;

	        $callback = function ($quantity, $item) use ($tax, &$total) {
	            $pricePerItem = constant(__CLASS__ . "::PRICE_" . strtoupper($item));
	            $total += ($pricePerItem * $quantity) * ($tax + 1.0);
	        };

	        array_walk($this->products, $callback);
	        return round($total, 2);;
	    }
	}

	$my_cart = new Cart;

	// 往购物车里添加商品及对应数量
	$my_cart->add('butter', 10);
	$my_cart->add('milk', 3);
	$my_cart->add('eggs', 12);

	// 打出出总价格，其中有 5% 的销售税.
	echo $my_cart->getTotal(0.05);
	?>

补充说明：闭包可以使用USE关键连接外部变量。总结：合理使用闭包能使代码更加简洁和精炼。

Generator类
------------
由生成器返回的生成器对象，生成器对象实现本接口。

.. code-block:: php

    <?php
	Generator implements Iterator {
	/* Methods */
	public mixed current ( void )
	// 获取生成器的返回值
	public mixed getReturn ( void )
	public mixed key ( void )
	public void next ( void )
	public void rewind ( void )
	// 发送一个值给生成器
	public mixed send ( mixed $value )
	public mixed throw ( Throwable $exception )
	public bool valid ( void )
	public void __wakeup ( void )
	}
    ?>


SPL接口
-------

Countable
^^^^^^^^^
类实现 Countable 可被用于 count() 函数。

类声明如下：

.. code-block:: php

    <?php
	Countable {
		/* 方法 */
		abstract public int count ( void )
	}
    ?>

例子：

.. code-block:: php

    <?php
	class CountMe
	{

	    protected $_myCount = 3;

	    public function count()
	    {
	        return $this->_myCount;
	    }

	}

	$countable = new CountMe();
	echo count($countable); //result is "1", not as expected

	class CountMe implements Countable
	{

	    protected $_myCount = 3;

	    public function count()
	    {
	        return $this->_myCount;
	    }

	}

	$countable = new CountMe();
	echo count($countable); //result is "3" as expected
	echo $countable->count(); // 3
    ?>

OuterIterator
^^^^^^^^^^^^^
该接口实现在多个迭代器上顺序迭代功能。

有时，将一个或多个迭代器包裹在另外一个迭代器中是很有用的，例如，在你希望按顺序迭代访问几个不同的迭代器时。对于这一用途而言，可以使用OuterIterator接口。

OuterIterator接口的定义如下：

.. code-block:: php

    <?php
	Interface OuterIterator extends Iterator{
		//返回当前迭代实体的内部迭代器
	    public Iterator getInnerIterator ( void )
	}
    ?>

这个接口与IteratorAggregate接口的区别在于它扩展了Iterator接口，因此所有实现它的类都必领实现Iterator接口定义的所有方法。

getInnerIterator()方法应该返回当前正在迭代访问的迭代器。例如，当两个或者更多的迭代器被添加在一起并且一个接一个地迭代访问时，随着数组指针通过next()方法不断增加，getInnerIterator()方法必须返回第一个迭代器，然后是第二个，以此类推。

RecursiveIterator
^^^^^^^^^^^^^^^^^
该接口实现在多个迭代器上递归迭代功能。

这个界面用于遍历多层数据，它继承了Iterator界面，因而也具有标准的current()、key()、next()、 rewind()和valid()方法。同时，它自己还规定了getChildren()和hasChildren()方法。getChildren() 方法必须返回一个实现了RecursiveIterator接口的对象。

RecursiveIterator接口的作用在于提供了递归迭代访问功能。这种类型的迭代器接口可以表达一个树形的数据结构，其中包含了节点元素和叶子元素或者父元素和子元素。目录就是一个递归结构的例子。

RecursiveIterator接口的定义如下。

.. code-block:: php

    <?php
	RecursiveIterator extends Iterator {
		//返回当前迭代实体的迭代器
	    public RecursiveIterator getChildren ( void )
	    //返回是否可以为当前条目创建迭代器
	    public bool hasChildren ( void )

	}
    ?>

所有的递归函数(调用自身的函数)都必须具有这样的功能，即决定是要继续递归操作，还是停止递归并返回到调用栈的顶部。hasChildren()方法提供了实现这一判断条件的功能。如果迭代器拥有子元索getChildren()方法将会被调用，并且它应该返回子元素的一个迭代器实例。

SeekableIterator
^^^^^^^^^^^^^^^^
该接口实现可查找的迭代器。

SeekableIterator界面也是Iterator界面的延伸，除了Iterator的5个方法以外，还规定了seek()方法，参数是元素的位置，返回该元素。如果该位置不存在，则抛出OutOfBoundsException。

例子：

.. code-block:: php

    <?php
	class PartyMemberIterator implements SeekableIterator
	{
	    public function __construct(PartyMember $member)
	    {
	        // Store $member locally for iteration
	    }

	    public function seek($index)
	    {
	        $this->rewind();
	        $position = 0;

	        while ($position < $index && $this->valid()) {
	            $this->next();
	            $position++;
	        }

	        if (!$this->valid()) {
	            throw new OutOfBoundsException('Invalid position');
	        }
	    }

	    // Implement current(), key(), next(), rewind()
	    // and valid() to iterate over data in $member
	}
    ?>

SplObserver
^^^^^^^^^^^

SplSubject
^^^^^^^^^^