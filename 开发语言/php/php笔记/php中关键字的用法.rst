*********************************************************
php中$this、static、final、const、self 等几个关键字的用法
*********************************************************

- this是指向对象实例的一个指针，self是对类本身的一个引用，parent是对父类的引用。
- this不能引用静态变量，但可以调用静态方法，this是指向当前实例化对象；self是指向类本身，也就是self是不指向任何已经实例化的对象，一般self使用来指向类中的静态变量和静态方法(但凡self通过域运算符::调用的静态变量或者静态方法使用的静态变量都必须经过初始化，否则会报变量未初始化。 **注意调用静态变量要带$** )；parent是指向父类的指针，一般我们使用parent来调用父类的构造函数和静态方法、静态属性，也是通过域运算符::调用。

const
=====
在类的内部方法访问已经声明为const及static的属性时，需要使用self::$name的形式调用。举例如下：

.. code-block:: php

    <?php
    class Demo{
        private static $name = "static Demo";
        const PI = 3.14;
        public $value;
        public static function getName(){
            return self::$name; // 静态变量需要带$符号
        }
        //这种写法有误，静态方法不能访问非静态属性
        public static function getName2(){
            return self::$value;
        }
        public function getPI(){
            return self::PI; // 常量不用带$符号
        }
    }

范围解析操作符 （::）
=====================

- 可以用于访问静态成员，类常量，还可以用于覆盖类中的属性和方法。
- self，parent 和 static 这三个特殊的关键字是用于在类定义的内部对其属性或方法进行访问的。
- parent用于调用父类中被覆盖的属性或方法（出现在哪里，就将解析为相应类的父类）。
- self用于调用本类中的方法或属性（出现在哪里，就将解析为相应的类；注意与$this区别，$this指向当前实例化的对象）。
- 当一个子类覆盖其父类中的方法时，PHP 不会调用父类中已被覆盖的方法。是否调用父类的方法取决于子类。

PHP内核将类的继承实现放在了"编译阶段"
=====================================

.. code-block:: php

    <?php
    class A{
        const H = 'A';

        const J = 'A';

        static function testSelf(){
            echo self::H;  //在编译阶段就确定了 self解析为 A
        }
    }

    class B extends A{
        const H = "B";

        const J = 'B';

        static function testParent(){
            echo parent::J;  //在编译阶段就确定了 parent解析为A
        }

        /* 若重写testSelf则能输出“B”, 且C::testSelf()也是输出“B”
        static function testSelf(){
            echo self::H;
        }
        */

    }

    class C extends B{
        const H = "C";

        const J = 'C';
    }

    B::testParent();
    B::testSelf();

    echo "\n";

    C::testParent();
    C::testSelf();

运行结果：

.. code-block:: shell

    AA
    AA

结论：

**self::和parent::出现在某个类X的定义中，则将被解析为相应的类X，除非在子类中覆盖父类的方法。**

Static（静态）关键字
====================
作用：

- 在函数体内的修饰变量的static关键字用于定义静态局部变量。
- 用于修饰类成员函数和成员变量时用于声明静态成员。
- (PHP5.3之后)在作用域解析符(::)前又表示静态延迟绑定的特殊类。

定义静态局部变量（出现位置：局部函数中）
---------------------------------------
特征：静态变量仅在局部函数域中存在，但当程序执行离开此作用域时，其值并不丢失。

.. code-block:: php

    <?php
    function test()
    {
        static $count = 0;

        $count++;
        echo $count;
        if ($count < 10) {
            test();
        }
        $count--;
    }

定义静态方法，静态属性
-----------------------
a) 声明类属性或方法为静态，就可以不实例化类而直接访问。
b) 静态属性不能通过一个类已实例化的对象来访问（但静态方法可以）
c) 如果没有指定访问控制，属性和方法默认为公有。
d) 由于静态方法不需要通过对象即可调用，所以伪变量 $this 在静态方法中不可用。
e) 静态属性不可以由对象通过 -> 操作符来访问。
f) 用静态方法调用一个非静态方法会导致一个 E_STRICT 级别的错误。
g) 就像其它所有的 PHP 静态变量一样，静态属性只能被初始化为文字或常量，不能使用表达式。所以可以把静态属性初始化为整数或数组，但不能初始化为另一个变量或函数返回值，也不能指向一个对象。

静态方法例子（出现位置： 类的方法定义）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
.. code-block:: php

    <?php
        class Foo {
            public static function aStaticMethod() {
                // ...
            }
        }

        Foo::aStaticMethod();
        $classname = 'Foo';
        $classname::aStaticMethod(); // 自PHP 5.3.0后，可以通过变量引用类
    ?>

静态属性例子（出现位置：类的属性定义）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
        class Foo
        {
            public static $my_static = 'foo';

            public function staticValue() {
                return self::$my_static;    //self 即 FOO类
            }
        }

        class Bar extends Foo
        {
            public function fooStatic() {
                return parent::$my_static; //parent 即 FOO类
            }
        }

        print Foo::$my_static . "\n";

        $foo = new Foo();
        print $foo->staticValue() . "\n";
        print $foo->my_static . "\n";      // Undefined "Property" my_static

        print $foo::$my_static . "\n";
        $classname = 'Foo';
        print $classname::$my_static . "\n"; // As of PHP 5.3.0

        print Bar::$my_static . "\n";
        $bar = new Bar();
        print $bar->fooStatic() . "\n";
    ?>

用于后期静态绑定（出现位置： 类的方法中，用于修饰变量或方法）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
自 PHP 5.3.0 起，PHP 增加了一个叫做后期静态绑定的功能，用于在继承范围内引用静态调用的类。

转发调用与非转发调用
""""""""""""""""""""
转发调用 :

- 指的是通过以下几种方式进行的静态调用：self::，parent::，static:: 以及 forward_static_call()。
非转发调用 ：

- 明确指定类名的静态调用（例如Foo::foo()）
- 非静态调用（例如$foo->foo()）

后期静态绑定工作原理
""""""""""""""""""""
原理：存储了在上一个“非转发调用”（non-forwarding call）中的类名。意思是当我们调用一个转发调用的静态调用时，实际调用的类是上一个非转发调用的类。

例子分析：

.. code-block:: php

    <?php
    class A {
        public static function foo() {
            echo __CLASS__."\n";
            static::who();
        }

        public static function who() {
            echo __CLASS__."\n";
        }
    }

    class B extends A {
        public static function test() {
            echo "A::foo()\n";
            A::foo();
            echo "parent::foo()\n";
            parent::foo();
            echo "self::foo()\n";
            self::foo();
        }

        public static function who() {
            echo __CLASS__."\n";
        }
    }

    class C extends B {
        public static function who() {
            echo __CLASS__."\n";
        }
    }

    C::test();

    /*
     * C::test();   //非转发调用 ，进入test()调用后，“上一次非转发调用”存储的类名为C
     *
     * //当前的“上一次非转发调用”存储的类名为C
     * public static function test() {
     *      A::foo();  //非转发调用， 进入foo()调用后，“上一次非转发调用”存储的类名为A，然后实际执行代码A::foo(),  转 0-0
     *      parent::foo();  //转发调用， 进入foo()调用后，“上一次非转发调用”存储的类名为C， 此处的parent解析为A ,转1-0
     *      self::foo(); //转发调用， 进入foo()调用后，“上一次非转发调用”存储的类名为C， 此处self解析为B, 转2-0
     *  }
     *
     *
     * 0-0
     * //当前的“上一次非转发调用”存储的类名为A
     * public static function foo() {
     *      static::who(); //转发调用， 因为当前的“上一次非转发调用”存储的类名为A， 故实际执行代码A::who(),即static代表A，进入who()调用后，“上一次非转发调用”存储的类名依然为A，因此打印 “A”
     *  }
     *
     * 1-0
     * //当前的“上一次非转发调用”存储的类名为C
     * public static function foo() {
     *      static::who(); //转发调用， 因为当前的“上一次非转发调用”存储的类名为C， 故实际执行代码C::who(),即static代表C，进入who()调用后，“上一次非转发调用”存储的类名依然为C，因此打印 “C”

     *  }
     *
     * 2-0
     * //当前的“上一次非转发调用”存储的类名为C
     * public static function foo() {
     *      static::who(); //转发调用， 因为当前的“上一次非转发调用”存储的类名为C， 故实际执行代码C::who(),即static代表C，进入who()调用后，“上一次非转发调用”存储的类名依然为C，因此打印 “C”
     *  }
     */


    故最终结果为：
    A::foo()
    A
    A
    parent::foo()
    A
    C
    self::foo()
    A
    C

更多静态后期静态绑定的例子
""""""""""""""""""""""""""

- Self, Parent 和 Static的对比

  .. code-block:: php

    <?php
    class Mango {
        function classname(){
            return __CLASS__;
        }

        function selfname(){
            return self::classname();
        }

        function staticname(){
            return static::classname();
        }
    }

    class Orange extends Mango {
        function parentname(){
            return parent::classname();
        }

        function classname(){
            return __CLASS__;
        }
    }

    class Apple extends Orange {
        function parentname(){
            return parent::classname();
        }

        function classname(){
            return __CLASS__;
        }
    }

    $apple = new Apple();
    echo $apple->selfname() . "\n";
    echo $apple->parentname() . "\n";
    echo $apple->staticname();

    ?>

    运行结果：
    Mango
    Orange
    Apple

- 使用forward_static_call()

  .. code-block:: php

    <?php
    class Mango
    {
        const NAME = 'Mango is';
        public static function fruit() {
            $args = func_get_args();
            echo static::NAME, " " . join(' ', $args) . "\n";
        }
    }

    class Orange extends Mango
    {
        const NAME = 'Orange is';

        public static function fruit() {
            echo self::NAME, "\n";

            forward_static_call(array('Mango', 'fruit'), 'my', 'favorite', 'fruit');
            forward_static_call('fruit', 'my', 'father\'s', 'favorite', 'fruit');
        }
    }

    Orange::fruit('NO');

    function fruit() {
        $args = func_get_args();
        echo "Apple is " . join(' ', $args). "\n";
    }

    ?>

    运行结果：
    Orange is
    Orange is my favorite fruit
    Apple is my father's favorite fruit

- 使用get_called_class()

  .. code-block:: php

    <?php
    class Mango {
        static public function fruit() {
            echo get_called_class() . "\n";
        }
    }

    class Orange extends Mango {
        //
    }

    Mango::fruit();
    Orange::fruit();
    ?>

    运行结果：
    Mango
    Orange

应用
""""
前面已经提到过了，引入后期静态绑定的目的是：用于在继承范围内引用静态调用的类。
所以， 可以用后期静态绑定的办法解决单例继承问题。

先看一下使用self是一个什么样的情况：

.. code-block:: php

    <?php
    // new self 得到的单例都为A。
    class A
    {
        protected static $_instance = null;

        protected function __construct()
        {
            //disallow new instance
        }

        protected function __clone(){
            //disallow clone
        }

        static public function getInstance()
        {
            if (self::$_instance === null) {
                self::$_instance = new self();
            }
            return self::$_instance;
        }
    }

    class B extends A
    {
        protected static $_instance = null;
    }

    class C extends A{
        protected static $_instance = null;
    }

    $a = A::getInstance();
    $b = B::getInstance();
    $c = C::getInstance();

    var_dump($a);
    var_dump($b);
    var_dump($c);

    运行结果：
    E:\code\php_test\apply\self.php:37:
    class A#1 (0) {
    }
    E:\code\php_test\apply\self.php:38:
    class A#1 (0) {
    }
    E:\code\php_test\apply\self.php:39:
    class A#1 (0) {
    }

通过上面的例子可以看到，使用self，实例化得到的都是类A的同一个对象。

再来看看使用static会得到什么样的结果。

.. code-block:: php

    <?php
    // new static 得到的单例分别为D，E和F。
    class D
    {
        protected static $_instance = null;

        protected function __construct(){}
        protected function __clone()
        {
            //disallow clone
        }

        static public function getInstance()
        {
            if (static::$_instance === null) {
                static::$_instance = new static();
            }
            return static::$_instance;
        }
    }

    class E extends D
    {
        protected static $_instance = null;
    }

    class F extends D{
        protected static $_instance = null;
    }

    $d = D::getInstance();
    $e = E::getInstance();
    $f = F::getInstance();

    var_dump($d);
    var_dump($e);
    var_dump($f);

    运行结果：
    E:\code\php_test\apply\static.php:35:
    class D#1 (0) {
    }
    E:\code\php_test\apply\static.php:36:
    class E#2 (0) {
    }
    E:\code\php_test\apply\static.php:37:
    class F#3 (0) {
    }

可以看到，使用static可以解决self时出现的单例继承问题。