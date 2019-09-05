*******
SPL数据结构
*******

数据结构
========
SPL提供了一套标准的数据结构，这些都是在应用开发过程中的常用数据结构，如双向链表、堆、栈等。 双向链表的数据结构由一个双向链表类（spl_dllist_object）、一个双向链表（spl_ptr_llist） 和一个标准的双向链表元素结构体（spl_ptr_llist_element）组成 这三者存在着包含关系，双向链表是类的组成之一，双向链表元素是双向链表头尾的结构。 双向链表类实现了迭代器接口，我们可以直接用foreach遍历整个链表。 其实现了 Countable 接口，即实现了count方法，我们可以直接对spl_dllist_object使用count函数获取链表中的元素个数。 关于 Countable 接口，关键实现在于count函数，当存在SPL时，如果count的是一个对象，则会判断是否实现了Countable接口， 如果实现了此接口，则会调用count方法并返回，否则返回对象的属性个数。

队列（SplQueue）和栈（SplStack）都是双向链表的子类， 栈操作的pop和push方法都是直接继承自父类，队列操作除了父类的操作外，增加了属于自己的enqueue和dequeue操作， 不过它们只不过是父类的push方法和shift方法的别名。

堆、大头堆、小头堆和优先队列是同一类数据结构，都是基于堆的实现。 堆是一颗完全二叉树，常用于管理算法执行过程中的信息，应用场景包括堆排序，优先队列等。 堆分为大头堆和小头堆，在定义上的区别是父节点的值是大于还是小于子节点的值， 在SPL中，它们的区别以比较函数的不同体现，而比较函数的不同仅仅体现在比较时交换了下位置和函数名的不同。

PHP的堆以数组的形式存储数据，默认初始化分配64个元素的内存空间， 新元素插入时，如果当前元素的个数总和超过分配的值，则会将其空间扩大一倍，即\*2。 SplMaxHeap和SplMinHeap都是SplHeap类的子类，直接继承了SplHeap的所有方法和属性，各自实现了自己的compare方法。

优先级队列是不同于先进先出队列的另一种队列，它每次从队列中取出的是具有最高优先权的元素， 这里的优先是指元素的某一属性优先，以比较为例，可能是较大的优先，也可能是较小的优先。 PHP实现的优先级队列默认是以大头堆实现，即较大的优先，如果要较小的优先，则需要继承SplPriorityQueue类，并重载compare方法。

SPL的堆实现了 Countable 接口和 Iterator 接口，我们可以通过count函数获取堆的元素个数， 以及直接foreach以访问数组的方式遍历堆中的元素，遍历顺序与比较函数相关。

SplFixedArray类提供了数组的主要功能。 一个SplFixedArray和一个常规的PHP数组之间的主要区别是：SplFixedArray所代表的数组长度是固定的，并只允许为整数的下标。 与普通的数组相比，它的优点是实现了更快的数组操作。 但这里的操作并不包括创建， 与原生的 array 相比，在创建对象的性能上大概有 1/3 的损耗。 之所有SplFixedArray类拥有更快的数组操作性能，是因为它舍弃了PHP自带数组的HashTable数据结构，直接以C数组存储数据。 由于只允许整数作为下标，并且数组长度是固定的，当获取一个元素时，只需要读取下标索引，返回C数组对应的元素即可。 这与HashTable的实现相比，少了hash key的计算，对于hash冲突的处理等。 换一句话说：SplFixedArray类基本就是C数组，只是作为PHP语法展现。

和双向链表一样， SplFixedArray类实现了Iterator，ArrayAccess和Countable接口。 从而可以直接用foreach遍历整个链表，可以以数组的方式访问对象，调用count方法获取数组的长度。 在获取数组元素值时，如果所传递的不是整数的下标，则抛出RuntimeException: Index invalid or out of range异常。 与获取元素末端，在设置数组元素时，如果所传递的不是整数的下标，会抛出RuntimeExceptione异常。 如果所设置的下标已经存在的值，则会先释放旧值的空间，然后将新的值指向旧值的空间。 当通过unset函数释放数组中的元素时，如果参数指定的下标存在值，则释放值所占的空间，并设置为NULL。

SplObjectStorage类实现了对象存储映射表，应用于需要唯一标识多个对象的存储场景。 在PHP5.3.0之前仅能存储对象，之后可以针对每个对象添加一条对应的数据。 SplObjectStorage类的数据存储依赖于PHP的HashTable实现，与传统的使用数组和spl_object_hash函数生成数组key相比， 其直接使用HashTable的实现在性能上有较大的优势。 有一些奇怪的是，在PHP手册中，SplObjectStorage类放在数据结构目录下。 但是他的实现和观察者模式的接口放在同一个文件（ext/spl/spl_observer.c）。 实际上他们并没有直接的关系。

双向链表
--------

- SplDoublyLinkedList
	+ SplStack
	+ SplQueue

双链表是一种重要的线性存储结构，对于双链表中的每个节点，不仅仅存储自己的信息，还要保存前驱和后继节点的地址。

PHP SPL中的 ``SplDoublyLinkedList`` 类提供了对双链表的操作。

SplDoublyLinkedList类摘要如下：

.. code-block:: php

	<?php
	class SplDoublyLinkedList implements Iterator  , ArrayAccess  , Countable  {
	  //构造一个双链表
	  public __construct ( void )
	  //在指定的索引位置插入或修改值
	  public void add ( mixed $index , mixed $newval )
	  //双链表的尾部节点(不删除)
	  public mixed top ( void )
	  //双链表的头部节点(不删除)
	  public mixed bottom ( void )
	  //双链表元素的个数
	  public int count ( void )
	  //检测双链表是否为空
	  public bool isEmpty ( void )


	  //当前节点索引
	  public mixed key ( void )
	  //移到上条记录
	  public void prev ( void )
	  //移到下条记录
	  public void next ( void )
	  //当前记录值
	  public mixed current ( void )
	  //将指针指向迭代开始处
	  public void rewind ( void )
	  //检查双链表是否还有节点
	  public bool valid ( void )

	  //指定index处节点是否存在
	  public bool offsetExists ( mixed $index )
	  //获取指定index处节点值
	  public mixed offsetGet ( mixed $index )
	  //设置指定index处值
	  public void offsetSet ( mixed $index , mixed $newval )
	  //删除指定index处节点
	  public void offsetUnset ( mixed $index )

	  //从双链表的尾部弹出元素(删除)
	  public mixed pop ( void )
	  //添加元素到双链表的尾部
	  public void push ( mixed $value )

	  //序列化存储
	  public string serialize ( void )
	  //反序列化
	  public void unserialize ( string $serialized )

	  //设置迭代模式
	  public void setIteratorMode ( int $mode )
	  //获取迭代模式SplDoublyLinkedList::IT_MODE_LIFO (Stack style) SplDoublyLinkedList::IT_MODE_FIFO (Queue style)
	  public int getIteratorMode ( void )

	  //双链表的头部移除元素(删除)
	  public mixed shift ( void )
	  //双链表的头部添加元素
	  public void unshift ( mixed $value )

	}
	?>

实现代码：

.. code-block:: php

	<?php
	/**
	 * PS：关于预定义接口Iterator, ArrayAccess, Countable的文章已经介绍过了，不认识的可以往前翻翻
	 */
	class SplDoublyLinkedList implements Iterator, ArrayAccess, Countable
	{
	    /**
	     * @var _llist 定义一个数组用于存放数据
	     */
	    protected $_llist   = array();

	    /**
	     * @var _it_mode 链表的迭代模式
	     */
	    protected $_it_mode = 0;

	    /**
	     * @var _it_pos 链表指针
	     */
	    protected $_it_pos  = 0;
	    /**
	     * 迭代模式
	     * @see setIteratorMode
	     */
	    const IT_MODE_LIFO     = 0x00000002;
	    const IT_MODE_FIFO     = 0x00000000;
	    const IT_MODE_KEEP     = 0x00000000;
	    const IT_MODE_DELETE   = 0x00000001;

	    /**
	     * @return 返回被移出尾部节点元素
	     * @throw RuntimeException 如果链表为空则抛出异常
	     */
	    public function pop()
	    {
	        if (count($this->_llist) == 0) {
	            throw new RuntimeException("Can't pop from an empty datastructure");
	        }
	        return array_pop($this->_llist);
	    }

	    /**
	     * @return 返回被移出头部节点元素
	     * @throw RuntimeException 如果链表为空则抛出异常
	     */
	    public function shift()
	    {
	        if (count($this->_llist) == 0) {
	            throw new RuntimeException("Can't shift from an empty datastructure");
	        }
	        return array_shift($this->_llist);
	    }

	    /**
	     * 往链表尾部添加一个节点元素
	     * @param $data 要添加的节点元素
	     */
	    public function push($data)
	    {
	        array_push($this->_llist, $data);
	        return true;
	    }

	    /**
	     * 往链表头部添加一个节点元素
	     * @param $data 要添加的节点元素
	     */
	    public function unshift($data)
	    {
	        array_unshift($this->_llist, $data);
	        return true;
	    }

	    /**
	     * @return 返回尾部节点元素，并把指针指向尾部节点元素
	     */
	    public function top()
	    {
	        return end($this->_llist);
	    }

	    /**
	     * @return 返回头部节点元素，并把指针指向头部节点元素
	     */
	    public function bottom()
	    {
	        return reset($this->_llist);
	    }

	    /**
	     * @return 返回链表节点数
	     */
	    public function count()
	    {
	        return count($this->_llist);
	    }

	    /**
	     * @return 判断链表是否为空
	     */
	    public function isEmpty()
	    {
	        return ($this->count() == 0);
	    }
	    /**
	     * 设置迭代模式
	     * - 迭代的顺序 (先进先出、后进先出)
	     *  - SplDoublyLnkedList::IT_MODE_LIFO (堆栈)
	     *  - SplDoublyLnkedList::IT_MODE_FIFO (队列)
	     *
	     * - 迭代过程中迭代器的行为
	     *  - SplDoublyLnkedList::IT_MODE_DELETE (删除已迭代的节点元素)
	     *  - SplDoublyLnkedList::IT_MODE_KEEP   (保留已迭代的节点元素)
	     *
	     * 默认的模式是 0 : SplDoublyLnkedList::IT_MODE_FIFO | SplDoublyLnkedList::IT_MODE_KEEP
	     *
	     * @param $mode 新的迭代模式
	     */
	    public function setIteratorMode($mode)
	    {
	        $this->_it_mode = $mode;
	    }

	    /**
	     * @return 返回当前的迭代模式
	     * @see setIteratorMode
	     */
	    public function getIteratorMode()
	    {
	        return $this->_it_mode;
	    }

	    /**
	     * 重置节点指针
	     */
	    public function rewind()
	    {
	        if ($this->_it_mode & self::IT_MODE_LIFO) {
	            $this->_it_pos = count($this->_llist)-1;
	        } else {
	            $this->_it_pos = 0;
	        }
	    }

	    /**
	     * @return 判断指针对应的节点元素是否存在
	     */
	    public function valid()
	    {
	        return array_key_exists($this->_it_pos, $this->_llist);
	    }

	    /**
	     * @return 返回当前指针的偏移位置
	     */
	    public function key()
	    {
	        return $this->_it_pos;
	    }

	    /**
	     * @return 返回当前指针对应的节点元素
	     */
	    public function current()
	    {
	        return $this->_llist[$this->_it_pos];
	    }

	    /**
	     * 将指针向前移动一个偏移位置
	     */
	    public function next()
	    {
	        if ($this->_it_mode & self::IT_MODE_LIFO) {
	            if ($this->_it_mode & self::IT_MODE_DELETE) {
	                $this->pop();
	            }
	            $this->_it_pos--;
	        } else {
	            if ($this->_it_mode & self::IT_MODE_DELETE) {
	                $this->shift();
	            } else {
	                $this->_it_pos++;
	            }
	        }
	    }
	    /**
	     * @return 偏移位置是否存在
	     *
	     * @param $offset             偏移位置
	     * @throw OutOfRangeException 如果偏移位置超出范围或者无效则抛出异常
	     */
	    public function offsetExists($offset)
	    {
	        if (!is_numeric($offset)) {
	            throw new OutOfRangeException("Offset invalid or out of range");
	        } else {
	            return array_key_exists($offset, $this->_llist);
	        }
	    }

	    /**
	     * @return 获取偏移位置对应的值
	     *
	     * @param $offset             偏移位置
	     * @throw OutOfRangeException 如果偏移位置超出范围或者无效则抛出异常
	     */
	    public function offsetGet($offset)
	    {
	        if ($this->_it_mode & self::IT_MODE_LIFO) {
	            $realOffset = count($this->_llist)-$offset;
	        } else {
	            $realOffset = $offset;
	        }
	        if (!is_numeric($offset) || !array_key_exists($realOffset, $this->_llist)) {
	            throw new OutOfRangeException("Offset invalid or out of range");
	        } else {
	            return $this->_llist[$realOffset];
	        }
	    }

	    /**
	     * @return 设置偏移位置对应的值
	     *
	     * @param $offset             偏移位置
	     * @throw OutOfRangeException 如果偏移位置超出范围或者无效则抛出异常
	     */
	    public function offsetSet($offset, $value)
	    {
	        if ($offset === null) {
	            return $this->push($value);
	        }
	        if ($this->_it_mode & self::IT_MODE_LIFO) {
	            $realOffset = count($this->_llist)-$offset;
	        } else {
	            $realOffset = $offset;
	        }
	        if (!is_numeric($offset) || !array_key_exists($realOffset, $this->_llist)) {
	            throw new OutOfRangeException("Offset invalid or out of range");
	        } else {
	            $this->_llist[$realOffset] = $value;
	        }
	    }

	    /**
	     * @return 删除偏移位置对应的值
	     *
	     * @param $offset             偏移位置
	     * @throw OutOfRangeException 如果偏移位置超出范围或者无效则抛出异常
	     */
	    public function offsetUnset($offset)
	    {
	        if ($this->_it_mode & self::IT_MODE_LIFO) {
	            $realOffset = count($this->_llist)-$offset;
	        } else {
	            $realOffset = $offset;
	        }
	        if (!is_numeric($offset) || !array_key_exists($realOffset, $this->_llist)) {
	            throw new OutOfRangeException("Offset invalid or out of range");
	        } else {
	            array_splice($this->_llist, $realOffset, 1);
	        }
	    }
	}
	?>

使用样例：

.. code-block:: php

    <?php
	$dlist=new SplDoublyLinkedList();

	//在列表的末尾插入数据
	$dlist->push('hiramariam');
	$dlist->push('maaz');
	$dlist->push('zafar');

	/* the list contains
	hiramariam
	maaz
	zafar
	*/

	//在列表的头部插入数据
	$dlist->unshift(1);
	$dlist->unshift(2);
	$dlist->unshift(3);

	/* the list now contains
	3
	2
	1
	hiramariam
	maaz
	zafar
	*/

	//从列表的尾部删除数据
	$dlist->pop();

	/* the list now contains
	3
	2
	1
	hiramariam
	maaz

	*/
	//从列表的头部删除数据
	$dlist->shift();

	/* the list now contains

	2
	1
	hiramariam
	maaz

	*/

	/*
	 如果需要取代指定索引位置的值，使用add方法，如果索引不存在，则抛出异常
	*/

	//$dlist->add(3 , 2.24);

	/*
	使用简单地循环遍历列表，rewind()重置迭代器；valid()检查是否到达链表的末尾；next()指向下一个元素
	*/
	for($dlist->rewind();$dlist->valid();$dlist->next()){

	    echo $dlist->current()."<br/>";
	}
	echo "<br/>";
	/*
	逆向迭代链表
	*/
	$dlist->setIteratorMode(SplDoublyLinkedList::IT_MODE_LIFO);
	for($dlist->rewind();$dlist->valid();$dlist->next()){

	    echo $dlist->current()."<br/>";;
	}

	// 另一种迭代方法
	while ($dlist->valid()){
	    //打印当前值
	    echo $dlist->current()."\n";
	    //移动到下一个值
	    $dlist->next();
	}
    ?>

.. code-block:: php

	<?php
	$doubly=new SplDoublyLinkedList();
	$doubly->push('a');
	$doubly->push('b');
	$doubly->push('c');
	$doubly->push('d');

	echo '初始链表结构：';
	var_dump($doubly);

	echo '<br/> 先进先出Keep模式迭代输出： <br/>';
	$doubly->setIteratorMode(SplDoublyLinkedList::IT_MODE_FIFO | SplDoublyLinkedList::IT_MODE_KEEP);
	$doubly->rewind();
	foreach($doubly as $key=>$value)
	{
	    echo $key.' '.$value."<br/>";
	}

	echo '<br/>后进先出Keep模式迭代输出：<br/>';
	$doubly->setIteratorMode(SplDoublyLinkedList::IT_MODE_LIFO | SplDoublyLinkedList::IT_MODE_KEEP);
	$doubly->rewind();
	foreach($doubly as $key=>$value)
	{
	    echo $key.' '.$value."<br/>";
	}

	echo '<br/>后进先出Delete模式迭代输出：<br/>';
	$doubly->setIteratorMode(SplDoublyLinkedList::IT_MODE_LIFO | SplDoublyLinkedList::IT_MODE_DELETE);
	$doubly->rewind();
	foreach($doubly as $key=>$value)
	{
	    if($key == 1) break;
	    echo $key.' '.$value."<br/>";
	}
	echo '<br/>Delete模式迭代之后的链表：';
	var_dump($doubly);
	?>

栈
^^
SplStack类通过使用一个双向链表来提供栈的主要功能。SplStack是一个带有 ``IT_MODE_LIFO`` 和 ``IT_MODE_KEEP`` 模式的SplDoublyLinkedList。

栈(Stack)是一种特殊的线性表，因为它只能在线性表的一端进行插入或删除元素(即进栈和出栈)，SplStack继承自SplDoublyLinkedList，并且mode被限制为LIFO，即后进先出模式。

实现代码：

.. code-block:: php

	<?php
	/**
	 * @since PHP 5.3
	 *
	 * SplStack继承自SplDoublyLinkedList，关于SplDoublyLinkedList请查看之前的文章
	 */
	class SplStack extends SplDoublyLinkedList
	{
	    protected $_it_mode = parent::IT_MODE_LIFO;
	    /**
	     * - SplStack允许使用两种迭代模式
	     *  - SplDoublyLnkedList::IT_MODE_LIFO | SplDoublyLnkedList::IT_MODE_KEEP
	     *  - SplDoublyLnkedList::IT_MODE_LIFO | SplDoublyLnkedList::IT_MODE_DELETE
	     *
	     * 默认的模式是 : SplDoublyLnkedList::IT_MODE_LIFO | SplDoublyLnkedList::IT_MODE_KEEP
	     * @param $mode
	     */
	    public function setIteratorMode($mode)
	    {
	        if ($mode & parent::IT_MODE_LIFO !== parent::IT_MODE_LIFO) {
	            throw new RuntimeException("Iterators' LIFO/FIFO modes for SplStack/SplQueue objects are frozen");
	        }
	        $this->_it_mode = $mode;
	    }
	}
	?>

.. code-block:: php

    <?php
	$q = new SplStack();

	$q[] = 1;
	$q[] = 2;
	$q[] = 3;
	$q->push(4);
	$q->add(4,5);

	$q->rewind();
	while($q->valid()){
	    echo $q->current(),"\n";
	    $q->next();
	}
    ?>

队列
^^^^
队列是一种特殊的线性表，遵循先进先出原则，特殊之处在于它只允许在表的前端进行删除操作，而在表的后端进行插入操作和栈一样，队列是一种操作受限制的线性表。进行插入操作的端称为队尾，进行删除操作的端称为队头。队列中没有元素时，称为空队列。

实现代码：

.. code-block:: php

	<?php
	/**
	 * SplQueue继承自SplDoublyLinkedList，关于SplDoublyLinkedList请查看之前的文章
	 *
	 * @since PHP 5.3
	 * @link http://blog.csdn.net/wuxing26jiayou/article/details/51862707
	 */
	class SplQueue extends SplDoublyLinkedList
	{
	    protected $_it_mode = parent::IT_MODE_FIFO;
	    /**
	     * - SplQueue允许使用两种迭代模式
	     *  - SplDoublyLnkedList::IT_MODE_FIFO | SplDoublyLnkedList::IT_MODE_KEEP
	     *  - SplDoublyLnkedList::IT_MODE_FIFO | SplDoublyLnkedList::IT_MODE_DELETE
	     *
	     * 默认的模式是 : SplDoublyLnkedList::IT_MODE_FIFO | SplDoublyLnkedList::IT_MODE_KEEP
	     * @param $mode
	     */
	    public function setIteratorMode($mode)
	    {
	        if ($mode & parent::IT_MODE_LIFO === parent::IT_MODE_LIFO) {
	            throw new RuntimeException("Iterators' LIFO/FIFO modes for SplStack/SplQueue objects are frozen");
	        }
	        $this->_it_mode = $mode;
	    }
	    /**
	     * 取出队列头部的成员
	     *
	     * @note dequeue方法等效于父类shift方法
	     * @see splDoublyLinkedList::shift()
	     */
	    public function dequeue()
	    {
	        return parent::shift();
	    }
	    /**
	     * 往队列尾部添加成员
	     *
	     * @note dequeue方法等效于父类push方法
	     * @see splDoublyLinkedList::push()
	     */
	    public function enqueue($data)
	    {
	        return parent::push($data);
	    }
	}
	?>

例子：

.. code-block:: php

    <?php
	$queue = new SplQueue();
	// 队列尾部增加元素
	$queue->enqueue('A');
	$queue->enqueue('B');
	$queue->enqueue('C');

	$queue->rewind();
	while($queue->valid()){
	    echo $queue->current(),"\n";
	    $queue->next();
	}

	print_r($queue);
	// 队列头部删除元素
	$queue->dequeue(); //remove first one
	print_r($queue);
    ?>

堆
---

- SplHeap
	+ SplMaxHeap
	+ SplMinHeap

就是为了实现优先队列而设计的一种数据结构，它是通过构造二叉堆(二叉树的一种)实现。根节点最大的堆叫做最大堆或大根堆（SplMaxHeap），根节点最小的堆叫做最小堆或小根堆（SplMinHeap）。二叉堆还常用于排序(堆排序)。

SplHeap类摘要如下：

.. code-block:: php

    <?php
	abstract SplHeap implements Iterator , Countable {
	　　/* 方法 */
	　　public __construct ( void )
		// 比较元素，以便在筛选时将它们正确放置在堆中。
	　　abstract protected int compare ( mixed $value1 , mixed $value2 )
		// 计算堆中元素个数
	　　public int count ( void )
		// 返回当前迭代器指向的节点
	　　public mixed current ( void )
		// 从堆顶部删除一个节点然后筛选
	　　public mixed extract ( void )
		// 通过筛选将元素插入堆中。
	　　public void insert ( mixed $value )
	 	// 检查堆是否为空
	　　public bool isEmpty ( void )
		// 返回当前节点的索引
	　　public mixed key ( void )
		// 移到下一个节点
	　　public void next ( void )
		// 从损坏的状态中恢复，并允许在堆上进一步的操作
	　　public void recoverFromCorruption ( void )
		// 重置迭代器
	　　public void rewind ( void )
		// 从堆的顶部获取值，不删除
	　　public mixed top ( void )
		// 检查堆是否还包含元素
	　　public bool valid ( void )
	}
    ?>

显然它是一个抽象类，最大堆(SplMaxHeap)和最小堆(SplMinHeap)就是继承它实现的。最大堆和最小堆并没有额外的方法。

SplHeap简单使用：

.. code-block:: php

    <?php
	//堆
	class MySplHeap extends SplHeap{
	    //compare()方法用来比较两个元素的大小，绝对他们在堆中的位置
	    public function compare( $value1, $value2 ) {
	        return ( $value1 - $value2 );
	    }
	}

	$obj = new MySplHeap();

	$obj->insert(0);
	$obj->insert(1);
	$obj->insert(2);
	$obj->insert(3);
	$obj->insert(4);

	echo $obj->top();//4
	echo $obj->count();//5

	foreach ($obj as $item) {
	    echo $item."<br />";
	}
    ?>

SplPriorityQueue
^^^^^^^^^^^^^^^^^
优先队列也是非常实用的一种数据结构，可以通过加权对值进行排序，由于排序在php内部实现，业务代码中将精简不少而且更高效。通过SplPriorityQueue::setExtractFlags(int  $flag)设置提取方式可以提取数据（等同最大堆）、优先级、和两者都提取的方式。

SplPriorityQueue类摘要如下：

.. code-block:: php

    <?php
	$pq = new SplPriorityQueue();

	$pq->insert('a', 10);
	$pq->insert('b', 1);
	$pq->insert('c', 8);

	echo $pq->count() .PHP_EOL; //3
	echo $pq->current() . PHP_EOL; //a

	/**
	 * 设置元素出队模式
	 * SplPriorityQueue::EXTR_DATA 仅提取值
	 * SplPriorityQueue::EXTR_PRIORITY 仅提取优先级
	 * SplPriorityQueue::EXTR_BOTH 提取数组包含值和优先级
	 */
	$pq->setExtractFlags(SplPriorityQueue::EXTR_DATA);

	while($pq->valid()) {
	    print_r($pq->current());  //a  c  b
	    $pq->next();
	}
    ?>

阵列(SplFixedArray)
----
SplFixedArray主要是处理数组相关的主要功能，与普通php array不同的是，它是固定长度的，且以数字为键名的数组，优势就是比普通的数组处理更快。通常情况下SplFixedArray要比php array快上20%~30%，所以如果你是处理巨大数量的固定长度数组，还是强烈建议使用。

SplFixedArray与普通的PHP Array不同，它是以数字为键名的固定长度的数组，它没有使用散列(Hash)存储方式，更接近于C语言的数组，因此效率更高。

SplFixedArray类摘要如下：

.. code-block:: php

    <?php
	SplFixedArray implements Iterator , ArrayAccess , Countable {
	　　/* 方法 */
	　　public __construct ([ int $size = 0 ] )
		// 返回数组的大小
	　　public int count ( void )
		// 返回当前值
	　　public mixed current ( void )
		// 导入PHP数组到SplFixedArray实例中
	　　public static SplFixedArray fromArray ( array $array [, bool $save_indexes = true ] )
		// 获取数组的大小
	　　public int getSize ( void )
		// 返回当前数组的索引
	　　public int key ( void )
		// 移动到下一个值
	　　public void next ( void )
		// 返回是否请求索引存在
	　　public bool offsetExists ( int $index )
		// 返回指定索引的值
	　　public mixed offsetGet ( int $index )
		// 设置指定索引的值
	　　public void offsetSet ( int $index , mixed $newval )
	　　// 删除指定索引的值
	    public void offsetUnset ( int $index )
		// 重置指针
	　　public void rewind ( void )
		// 改变数组的大小
	　　public int setSize ( int $size )
		// 转换为PHP数组
	　　public array toArray ( void )
		// 检查是否数组包含更多元素
	　　public bool valid ( void )
		// 重新初始化反序列化后的数组
	　　public void __wakeup ( void )
	}
    ?>

简单使用：

.. code-block:: php

    <?php
	$arr = new SplFixedArray(4);
	$arr[0] = 'php';
	$arr[1] = 1;
	$arr[3] = 'python';

	//遍历， $arr[2] 为null
	foreach($arr as $v) {
	    echo $v . PHP_EOL;
	}

	//获取数组长度
	echo $arr->getSize(); //4

	//增加数组长度
	$arr->setSize(5);
	$arr[4] = 'new one';

	//捕获异常
	try{
	    echo $arr[10];
	} catch (RuntimeException $e) {
	    echo $e->getMessage();
	}
    ?>

.. code-block:: php

	<?php
	$splArray = new SplFixedArray(5);
	$splArray[1] = 'cat';
	$splArray[4] = 'dog';
	var_dump($splArray);
	unset($splArray);

	$splArray = SplFixedArray::fromArray(array('cat','pig','dog'));
	var_dump($splArray);
	var_dump($splArray->getSize());
	var_dump($splArray->toArray());

	/*
	以上输出：
	object(SplFixedArray)#1 (5) { [0]=> NULL [1]=> string(3) "cat" [2]=> NULL [3]=> NULL [4]=> string(3) "dog" }
	object(SplFixedArray)#1 (3) { [0]=> string(3) "cat" [1]=> string(3) "pig" [2]=> string(3) "dog" }
	int(3)
	array(3) { [0]=> string(3) "cat" [1]=> string(3) "pig" [2]=> string(3) "dog" }
	 */
	?>

映射
-----
用来存储一组对象的，特别是当你需要唯一标识对象的时候。

PHP SPL SplObjectStorage类实现了 ``Countable`` , ``Iterator`` , ``Serializable`` , ``ArrayAccess`` 四个接口。可实现统计、迭代、序列化、数组式访问等功能。

SplObjectStorage类提供从对象到数据的映射，或者通过忽略数据来提供对象集功能。

SplObjectStorage类摘要如下：

.. code-block:: php

    <?php
	SplObjectStorage implements Countable , Iterator , Serializable , ArrayAccess {
	　　/* 方法 */
		// 增加另一个容器的所有对象
	　　public void addAll ( SplObjectStorage $storage )
		// 增加一个对象到容器中
	　　public void attach ( object $object [, mixed $data = NULL ] )
		// 检查容器中是否包含指定对象
	　　public bool contains ( object $object )
		// 返回在容器中的对象个数
	　　public int count ( void )
		// 返回当前值
	　　public object current ( void )
		// 从容器中移除一个对象
	　　public void detach ( object $object )
		// 计算包含对象的唯一标识符。
	　　public string getHash ( object $object )
		// 获取关联当前迭代器key的值信息
	　　public mixed getInfo ( void )
		// 返回当前索引
	　　public int key ( void )
		// 移动到下一个实体
	　　public void next ( void )
		// 检查对象在容器中是否存在
	　　public bool offsetExists ( object $object )
		// 返回一个对象的关联数据
	　　public mixed offsetGet ( object $object )
		// 设置一个对象的关联数据
	　　public void offsetSet ( object $object [, mixed $data = NULL ] )
		// 从容器中移除一个对象
	　　public void offsetUnset ( object $object )
		// 差集
	　　public void removeAll ( SplObjectStorage $storage )
		// 从当前容器中移除不在指定容器中的对象
	　　public void removeAllExcept ( SplObjectStorage $storage )
	　　public void rewind ( void )
	　　public string serialize ( void )
		// 设当前迭代的实体对象置关联数据
	　　public void setInfo ( mixed $data )
	　　public void unserialize ( string $serialized )
	　　public bool valid ( void )
	}
    ?>

简单使用：

- SplObjectStorage作为集合
实现了以对象为键的映射（map）或对象的集合（如果忽略作为键的对象所对应的数据）这种数据结构。这个类的实例很像一个数组，但是它所存放的对象都是唯一的。

  .. code-block:: php

		<?php
		// As an object set
		$s = new SplObjectStorage();

		$o1 = new StdClass;
		$o2 = new StdClass;
		$o3 = new StdClass;

		$s->attach($o1);
		$s->attach($o2);

		var_dump($s->contains($o1));
		var_dump($s->contains($o2));
		var_dump($s->contains($o3));

		$s->detach($o2);

		var_dump($s->contains($o1));
		var_dump($s->contains($o2));
		var_dump($s->contains($o3));
		/* 输出结果
		bool(true)
		bool(true)
		bool(false)
		bool(true)
		bool(false)
		bool(false)
		 */
		?>

- SplObjectStorage作为一个map

  .. code-block:: php

		<?php
		// As a map from objects to data
		$s = new SplObjectStorage();

		$o1 = new StdClass;
		$o2 = new StdClass;
		$o3 = new StdClass;

		$s[$o1] = "data for object 1";
		$s[$o2] = array(1,2,3);

		if (isset($s[$o2])) {
		    var_dump($s[$o2]);
		}
		/* 输出结果
		array(3) {
		  [0]=>
		  int(1)
		  [1]=>
		  int(2)
		  [2]=>
		  int(3)
		}
		 */
		?>

.. code-block:: php

    <?php
	class A {
	    public $i;
	    public function __construct($i) {
	        $this->i = $i;
	    }
	}

	$a1 = new A(1);
	$a2 = new A(2);
	$a3 = new A(3);
	$a4 = new A(4);

	$container = new SplObjectStorage();

	//SplObjectStorage::attach 添加对象到Storage中
	$container->attach($a1);
	$container->attach($a2);
	$container->attach($a3);

	//SplObjectStorage::detach 将对象从Storage中移除
	$container->detach($a2);

	//SplObjectStorage::contains用于检查对象是否存在Storage中
	var_dump($container->contains($a1)); //true
	var_dump($container->contains($a4)); //false

	//遍历
	$container->rewind();
	while($container->valid()) {
	    var_dump($container->current());
	    $container->next();
	}
    ?>