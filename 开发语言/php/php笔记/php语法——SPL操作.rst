*****
SPL操作
*****
SPL，PHP标准库（Standard PHP Library），从 PHP 5.0 开始内置的接口和类的集合，从 PHP5.3 开始逐渐成熟并成为内核组件的一部分。 但是由于其文档的稀少以及推行的力度不够，导致较多的PHP开发人员对其不了解，甚至闻所未闻。

SPL是为了解决典型问题而存在，为了实现一些有效的数据访问接口和类。 现在它包括对常规数据结构的访问，迭代器，异常处理，文件处理，数组处理和一些设计模式的实现。 这些在程序设计的世界中都是一些典型的问题，以这样一种标准库的方式实现可以在很大程度上减少代码的冗余和提高开发的效率。

SPL在PHP内核中以一个扩展的形式存在，在ext目录下有一个spl目录，这里存放了SPL的所有代码实现。 从文件名我们可以看出其基本的操作实体：异常、数组、文件……。与其它扩展相比，SPL扩展没有太多不同的地方， 它和json、date等扩展一起作为内置的模块（php_builtin_extensions）加载。

.. include:: ./php语法——接口.rst

.. include:: ./php语法——SPL数据结构.rst

.. include:: ./php语法——SPL异常类.rst

.. include:: ./php语法——SPL迭代器.rst

.. include:: ./php语法——SPL迭代器demo.rst

观察者模式
=========
观察者模式定义对象间的一种一对多的依赖关系，当一个对象的状态发生改变时，所有依赖于它的对象都得到通知并被自动更新 观察者模式又称为发布-订阅（Publish-Subscribe）模式、模型-视图（Model-View）模式、源-监听（Source-Listener）模式、 或从属者(Dependents)模式。 一般来说，观察者模式包括如下四个角色：

- 抽象主题（Subject）角色：主题角色将所有对观察者对象的引用保存在一个集合中，每个主题可以有任意多个观察者。 抽象主题提供了增加和删除观察者对象的接口。
- 抽象观察者（Observer）角色：为所有的具体观察者定义一个接口，在观察的主题发生改变时更新自己。
- 具体主题（ConcreteSubject）角色：存储相关状态到具体观察者对象，当具体主题的内部状态改变时，给所有登记过的观察者发出通知。 具体主题角色通常用一个具体子类实现。
- 具体观察者（ConcretedObserver）角色：存储一个具体主题对象，存储相关状态，实现抽象观察者角色所要求的更新接口， 以使得其自身状态和主题的状态保持一致。

SPL实现了其中两个抽象角色：SplObserver接口和SplSubject接口。 如果我们需要实现观察者模式，仅需要实现这两个接口即可。并且这两个接口定义在模块初始化的方法中。 除了声明标准的接口函数，他们什么也没有做。 这只是观察者模式的标准，具体的更新操作，添加操作等还是需要我们去实现。

使用 SPL 提供的 SplSubject和 SplObserver接口以及 SplObjectStorage类，快速实现 Observer 设计模式。

Observer 设计模式定义了对象间的一种一对多的依赖关系，当被观察的对象发生改变时，所有依赖于它的对象都会得到通知并被自动更新，而且被观察的对象和观察者之间是松耦合的。在该模式中，有目标（Subject）和观察者（Observer）两种角色。目标角色是被观察的对象，持有并控制着某种状态，可以被任意多个观察者作为观察的目标，SPL 中使用 SplSubject接口规范了该角色的行为：

表 1. SplSubject 接口中的方法

+-------------------------------------------------------+----------------------------------+
| 方法声明                                              | 描述                             |
+=======================================================+==================================+
| abstract public void attach ( SplObserver $observer ) | 添加（注册）一个观察者           |
+-------------------------------------------------------+----------------------------------+
| abstract public void detach ( SplObserver $observer ) | 删除一个观察者                   |
+-------------------------------------------------------+----------------------------------+
| abstract public void notify ( void )                  | 当状态发生改变时，通知所有观察者 |
+-------------------------------------------------------+----------------------------------+

观察者角色是在目标发生改变时，需要得到通知的对象。SPL 中用 SplObserver接口规范了该角色的行为：

表 2. SplObserver 中的方法

+-----------------------------------------------------+-------------------------------------------------------------------------+
| 方法声明                                            | 描述                                                                    |
+=====================================================+=========================================================================+
| abstract public void update ( SplSubject $subject ) | 在目标发生改变时接收目标发送的通知；当关注的目标调用其 notify()时被调用 |
+-----------------------------------------------------+-------------------------------------------------------------------------+

该设计模式的核心思想是，SplSubject对象会在其状态改变时调用 notify()方法，一旦这个方法被调用，任何先前通过 attach()方法注册上来的 SplObserver对象都会以调用其 update()方法的方式被更新。

为什么使用 SplObjectStorage 类

SplObjectStorage类实现了以对象为键的映射（map）或对象的集合（如果忽略作为键的对象所对应的数据）这种数据结构。这个类的实例很像一个数组，但是它所存放的对象都是唯一的。这个特点就为快速实现 Observer 设计模式贡献了不少力量，因为我们不希望同一个观察者被注册多次。该类的另一个特点是，可以直接从中删除指定的对象，而不需要遍历或搜索整个集合。

SplObjectStorage类的实例之所以能够只存储唯一的对象，是因为其 SplObjectStorage::attach()方法的实现中先判断了指定的对象是否已经被存储：

.. code-block:: php

    <?php
	function attach ($obj, $inf = NULL) {
	    if (is_object($obj) && !$this->contains($obj)) {
	        $this->storage[] = array($obj, $inf);
	    }
	}
    ?>

模拟案例

下面我们通过一个模拟案例来演示 SPL 在实现 Observer 设计模式上的威力。该案例模拟了一个网站的用户管理模块，该模块包括 3 个主要功能：

- 新增 1 个用户
- 把指定用户的密码变更为他所指定的新密码
- 在用户忘记密码时重置其密码

每当这些功能完成后，都需要将密码告知用户。除了传统的向用户发送 Email 这种手段外，我们还需要向用户的手机发送短信，让他们更加方便地知道密码是什么。假设我们的网站还有一套站内的消息系统，我们称之为小纸条，在用户变更或重置密码后，向他们发送小纸条会令他们高兴的。

经过分析，该案例适合使用 Observer 设计模式解决，因为将密码告知用户的多种手段与用户密码的改变——无论是从无到有，用户主动变更，还是系统重置——形成了多对一的关系。

我们决定定义一个 User 类表示用户，实现需求中的 3 个功能。该类就是 Observer 设计模式中的目标（Subject）角色。我们还需要一组类，实现利用各种手段向用户发送新密码的功能，这些类就充当了 Observer 设计模式中的观察者（Observer）角色。

经过简单地分析后，我们画出 UML 类图：

.. image:: ./_static/images/observer.jpg

根据 UML 类图，首先，定义 1 个名为 User 的类模拟案例中的用户。尽管实际网站中的用户要有更多的属性，特别是通常需要用 ID 来标识每个用户，但是我们为了突出本文的主题，只保留了案例所需的属性。

清单 2. User 类的源代码

.. code-block:: php

    <?php
	class User implements SplSubject {

	   private $email;
	   private $username;
	   private $mobile;
	   private $password;
	   /**
	    * @var SplObjectStorage
	    */
	   private $observers = NULL;

	   public function __construct($email, $username, $mobile, $password) {
	       $this->email = $email;
	       $this->username = $username;
	       $this->mobile = $mobile;
	       $this->password = $password;

	       $this->observers = new SplObjectStorage();
	   }

	   public function attach(SplObserver $observer) {
	       $this->observers->attach($observer);
	   }

	   public function detach(SplObserver $observer) {
	       $this->observers->detach($observer);
	   }

	   public function notify() {
	       $userInfo = array(
	           'username' => $this->username,
	           'password' => $this->password,
	           'email' => $this->email,
	           'mobile' => $this->mobile,
	       );
	       foreach ($this->observers as $observer) {
	           $observer->update($this, $userInfo);
	       }
	   }

	   public function create() {
	       echo __METHOD__, PHP_EOL;
	       $this->notify();
	   }

	   public function changePassword($newPassword) {
	       echo __METHOD__, PHP_EOL;
	       $this->password = $newPassword;
	       $this->notify();
	   }

	   public function resetPassword() {
	       echo __METHOD__, PHP_EOL;
	       $this->password = mt_rand(100000, 999999);
	       $this->notify();
	   }

	}
    ?>

User 类要想充当目标角色，就需要实现 SplSubject接口，而按照实现接口的法则，attach()、detach()和 notify()就必须被实现。请注意，由于在 SplSubject接口中，attach() 和detach() 的参数都使用了类型提示（type hinting），在实现这两个方法时，也不能省略参数前面的类型。我们还使用了 $observers实例属性保存一个 SplObjectStorage对象，用来存放所有注册上来的观察者。

的确，一个数组就能解决问题，但是很快就可以发现，使用了 SplObjectStorage之后删除一个观察者实现起来是多么简单，直接委托给 SplObjectStorage对象！是的，不需要再使用最原始的 for语句遍历观察者数组或者使用 array_search函数，1 行搞定。

接下来分别定义充当观察者角色的 3 个信息发送类。为了简单，我们只是通过输出文本来假装发送信息。可即使是假装，依然需要知道用户的信息。可看看 SplObserver接口 update()方法的签名，多么令人沮丧，它无法接受目标角色通过调用其 notify() 方法发送通告时给出的参数。如果你试图在重写 update()方法时加上第 2 个参数，会得到一个类似

``Fatal error: Declaration of EmailSender::update() must be compatible with that of SplObserver::update() 的错误而使代码执行终止。``

其实，当目标所持有的状态（在本例中是用户的密码）更新时，如何通知观察者有两种方法。“拉”的方法和“推”的方法。SPL 使用的是“拉”的方法，观察者需要通过目标的引用（作为 update()方法的参数传入）来访问其属性。“拉”的方法需要让观察者更了解目标都拥有哪些属性，这增加了它们耦合度。而且主题也要对观察者门户大开，违背了封装性。解决的方法是在目标中提供一系列 getter 方法，如 getPassword()来让观察者获得用户的密码。

虽然“拉”的方法可能被认为更加正确，但是我们觉得让主题把用户的信息“推”过来更加方便。既然通过在重写 update()方法时加上第 2 个参数是行不通的，那么就从别的方向上着手。好在 PHP 在方法调用上有这样的特性，只要给定的参数（实参）不少于定义时指定的必选参数（没有默认值的参数），PHP 就不会报错。传入一个方法的参数个数，可以通过 func_num_args() 函数获取；多余的参数可以使用 func_get_arg()函数读取。注意该函数是从 0 开始计数的，即 0 表示第 1 个实参。利用这个小技巧，update()方法可以通过 func_get_arg(1)接收一个用户信息的数组，有了这个数组，就能知道邮件该发给谁，新密码是什么了。为了节约篇幅，而且三个信息发送类非常相像，下面只给出其中一个的源代码，完整的源代码可以下载本文的附件得到。

清单 3. Email_Sender 类的源代码

.. code-block:: php

    <?php
	class EmailSender implements SplObserver {

	   public function update(SplSubject $subject) {
	       if (func_num_args() === 2) {
	           $userInfo = func_get_arg(1);
	           echo "向 {$userInfo['email']} 发送电子邮件成功。内容是：你好 {$userInfo['username']}" .
	           "你的新密码是 {$userInfo['password']}，请妥善保管", PHP_EOL;
	       }
	   }

	}
    ?>

最后我们写一个测试脚本 test.php。建议使用 CLI 的方式 php – f test.php来执行该脚本，但由于设置了 Content-Type响应头部字段为 text/plain，在浏览器中应该也能看到一行一行显示的结果（因为没有用 <br />做换行符而是使用常量 PHP_EOL，所以不设置 Content-Type的话，就不能正确分行显示了）。

清单 4. 用于测试的脚本

.. code-block:: php

    <?php
	header('Content-Type: text/plain');

	function __autoload($class_name) {
	   require_once "$class_name.php";
	}

	$email_sender = new EmailSender();
	$mobile_sender = new MobileSender();
	$web_sender = new WebsiteSender();

	$user = new User('user1@domain.com', '张三', '13610002000', '123456');

	// 创建用户时通过 Email 和手机短信通知用户
	$user->attach($email_sender);
	$user->attach($mobile_sender);
	$user->create($user);
	echo PHP_EOL;

	// 用户忘记密码后重置密码，还需要通过站内小纸条通知用户
	$user->attach($web_sender);
	$user->resetPassword();
	echo PHP_EOL;

	// 用户变更了密码，但是不要给他的手机发短信
	$user->detach($mobile_sender);
	$user->changePassword('654321');
	echo PHP_EOL;
    ?>

SPL函数
=======


iterator_apply
--------------
为迭代器中每个元素调用一个用户自定义函数

.. code-block:: php

    <?php
	/**
	 *  首字母大写
	 *
	 * @param Iterator $it
	 *
	 * @return bool
	 *
	 */
	function addCaps (Iterator $it) {
	    echo ucfirst($it->current()) . PHP_EOL;
	    // 为了遍历 iterator 这个函数必须返回 TRUE
	    return true;
	}

	/*** an array of aussies ***/
	$array = array('dingo', 'wombat', 'wallaby');

	try {
	    $it = new ArrayIterator($array);
	    iterator_apply($it, 'addCaps', array($it));
	} catch (Exception $e) {
	    /*** echo the error message ***/
	    echo $e->getMessage();
	}
    ?>

各种类及接口
===========

ArrayObject
-----------
这个类允许对象当做数组工作。这将统一数组和对象，允许您迭代它们并使用数组语法来访问内容。允许通过数组语法访问属性和使用foreach迭代。从关联数组创建ArrayObject时，对象属性将采用数组的键的名称。

.. code-block:: php

    <?php
	ArrayObject implements IteratorAggregate , ArrayAccess , Serializable , Countable {
	/* 常量 */
	//实体可以以属性的方式访问
	const integer STD_PROP_LIST = 1 ;
	//实体可以以数组的方式访问
	const integer ARRAY_AS_PROPS = 2 ;
	/* 方法 */
	public __construct ([ mixed $input = [] [, int $flags = 0 [, string $iterator_class = "ArrayIterator" ]]] )
	//添加新元素
	public void append ( mixed $value )
	//根据值来排序实体
	public void asort ( void )
	//返回迭代器中元素个数
	public int count ( void )
	//用输入数组或对象替换对象中的数组
	public array exchangeArray ( mixed $input )
	//复制当前对象中数组
	public array getArrayCopy ( void )
	//获取行为标志
	public int getFlags ( void )
	//从一个数组对象构造一个新迭代器
	public ArrayIterator getIterator ( void )
	//获取对象中迭代器的类名
	public string getIteratorClass ( void )
	//通过关键字排序
	public void ksort ( void )
	//使用大小写不敏感的自然排序
	public void natcasesort ( void )
	//使用自然排序
	public void natsort ( void )
	//判断提交的值是否存在
	public bool offsetExists ( mixed $index )
	//指定 name 获取值
	public mixed offsetGet ( mixed $index )
	//修改指定 name 的值
	public void offsetSet ( mixed $index , mixed $newval )
	//删除数据
	public void offsetUnset ( mixed $index )
	public string serialize ( void )
	//改变数组对象的行为标志
	public void setFlags ( int $flags )
	//设置数组迭代器类名称
	public void setIteratorClass ( string $iterator_class )
	//实体值使用用户定义比较函数排序
	public void uasort ( callable $cmp_function )
	//实体键值使用用户定义比较函数排序
	public void uksort ( callable $cmp_function )
	public void unserialize ( string $serialized )
	}
    ?>

ArrayObject::getIterator()将返回一个ArrayIterator。如果扩展ArrayObject，则可以返回自己的迭代器类型，但它必须从ArrayIterator派生。

输出结果：

.. code-block:: php

    <?php
	$a = new ArrayObject();
	//$a->setFlags(ArrayObject::STD_PROP_LIST); //默认标志
	$a['arr'] = 'array data';
	$a->prop = 'prop data';
	echo $a['arr']; // array data
	echo PHP_EOL;
	echo $a->prop; // prop data
	echo PHP_EOL;

	$b = new ArrayObject();
	$b->setFlags(ArrayObject::ARRAY_AS_PROPS); //增加对象属性功能
	$b['arr'] = 'array data';
	$b->prop = 'prop data';
	echo $b['arr']; // array data
	echo PHP_EOL;
	echo $b->prop; // prop data
	echo PHP_EOL;

	$c = new ArrayObject();
	$c ->setFlags(ArrayObject::STD_PROP_LIST|ArrayObject::ARRAY_AS_PROPS);
	$c['arr'] = 'array data';
	$c->prop = 'prop data';

	/*
	ArrayObject Object
	(
	    [prop] => prop data
	    [storage:ArrayObject:private] => Array
	        (
	            [arr] => array data
	        )
	)
	*/
	print_r($a);
	/*
	 ArrayObject Object
	(
	    [storage:ArrayObject:private] => Array
	        (
	            [arr] => array data
	            [prop] => prop data
	        )
	)
	*/
	print_r($b);
	/*
	 ArrayObject Object
	(
	    [storage:ArrayObject:private] => Array
	        (
	            [arr] => array data
	            [prop] => prop data
	        )
	)
	*/
	print_r($c);
    ?>

- STD_PROP_LIST告诉怎么读；

  STD_PROP_LIST决定了在某些情况的返回值，比如，如果设置了该标志，var_dump()方法将会返回设置在数组对象(ArrayObject)上的属性，如果没有设置，只能返回内部元素列表；当使用foreach()时，它总是返回内部元素的而不会返回实际的属性。
- ARRAY_AS_PROPS告诉怎么写；

  首先，对于通过标准数组形式赋值的这两个标志都是一样的结果。当使用对象属性形式赋值时，当设置ARRAY_AS_PROPS标志，该属性将被存储为内部元素；如果没有设置，只存储在对象数组(ArrayObject)的属性中。

.. code-block:: php

    <?php
	/*** a simple array ***/
	$array = array('koala', 'kangaroo', 'wombat', 'wallaby', 'emu', 'kiwi', 'kookaburra', 'platypus');

	/*** create the array object ***/
	$arrayObj = new ArrayObject($array);

	/*** iterate over the array ***/
	for($iterator = $arrayObj->getIterator();
	    /*** check if valid ***/
	    $iterator->valid();
	    /*** move to the next array member ***/
	    $iterator->next())  {
	    /*** output the key and current array value ***/
	    echo $iterator->key() . ' => ' . $iterator->current() . PHP_EOL;
	}

	//另一种迭代方法，只对一维数组有效
	foreach ($arrayObj as $key => $item) {
	    echo $key. ' => ' . $item . PHP_EOL;
	}

	$arrayObj->append('dingo'); // 增加一个元素
	$arrayObj->natcasesort(); // 对数组元素排序
	echo $arrayObj->count(); // 显示元素的数量
	$arrayObj->offsetUnset(5); // 删除一个元素

	// 某一个元素是否存在
	if ($arrayObj->offsetExists(3))
	{
	    echo 'Offset Exists'.PHP_EOL;
	}

	$arrayObj->offsetSet(5, "galah"); // 更改某个位置的元素值
	echo $arrayObj->offsetGet(4); // 显示某个位置的元素值

	$arrayCopy = $arrayObj->getArrayCopy(); //复制数组
	echo '====================================='.PHP_EOL;
	// 针对多维数组
	$company = array(
	    'name' => 'Chuck Norris', 'position' => 'Account Manager',
	    'manages' => array(
	        'name' => 'Jane Doe', 'position' => 'Project Manager',
	        'manages' => array(array(
	            'name' => 'Cinderella', 'position' => 'Developer',
	            'manages' => array()
	        ),
	            array(
	                'name' => 'Shrek', 'position' => 'Graphical Designer',
	                'manages' => array()
	            )
	        ),
	        array(
	            'name' => 'John Doe', 'position' => 'Project Manager',
	            'manages' => array()
	        )
	    )
	);

	$iterator = new RecursiveArrayIterator(new ArrayObject($company));

	$riteriter = new RecursiveIteratorIterator($iterator);

	foreach ($riteriter as $key => $value) {

	    echo $key . " = " . $value . PHP_EOL;
	}
    ?>

.. code-block:: php

    <?php
	class EmailUser {
	     public $name;
	     public $email;
	     public function __construct($name, $email) { $this->name = $name; $this->email = $email; }
	}

	$user = new EmailUser("John Doe", "john@domain.com");

	// create an instance of the ArrayObject class
	$arrayObj = new ArrayObject($user);

	// access the object properties using an array notation
	// displays the following: Full name: John Doe Email: john@domain.com
	echo "Full name: " . $arrayObj['name'] . " Email: " . $arrayObj['email'];

	// count the number of properties in the object
	echo "total properties is " . count($arrayObj);
    ?>