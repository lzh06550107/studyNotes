
特殊情况总结
============
动态地创建属性和方法
------------------------
事实上，PHP并没有强制所以的属性都要在类中声明。我们可以动态地添加属性到对象，如下所示：

.. code-block:: php

    <?php
    error_reporting(-1);
    ini_set('display_errors','on');

    class A {
        public $a;
        public static $b = 'World';
        public function add() {
            $this->a = 'Hello';
        }
        public static function p() {
            echo self::$b.PHP_EOL;
        }
    }

    $a = new A;
    $a->add();
    $a->c = 'test';
    $a->p(); //对象可以调用对象所属类的静态方法

    var_dump($a);
    /*
    world
    class A#1 (2) {
      public $a =>
      string(5) "Hello"
      public $c =>
      string(4) "test"
    }
    */
    ?>

.. code-block:: php

    <?php
    /**
     * 类的相关知识点 3（动态地创建属性和方法）
     */
    // 用于演示如何动态地创建属性（这就是 php 中所谓的重载）
    class Class1
    {
    // __set 魔术方法，当设置的属性不存在或者不可访问（private）时就会调用此函数
        public function __set($name, $value)
        {
            echo "__set \$name: {$name}, \$value: {$value}";
            echo "\n";
        }
    // __get 魔术方法，当获取的属性不存在或者不可访问（private）时就会调用此函数
        public function __get($name)
        {
            echo "__get \$name: {$name}";
            echo "\n";
            return 999;
        }
    }
    $objClass1 = new Class1();
    // 当你设置的属性不存在或者不可访问（private）时，就会调用对应的 __set 魔术方法
    $objClass1->property1 = 'wanglei'; // 不可访问的如 private ，或者不存在的
    // 当你获取的属性不存在或者不可访问（private）时，就会调用对应的 __get 魔术方法
    echo $objClass1->property2;
    echo "\n";
    // 用于演示如何动态地创建方法（这就是 php 中所谓的重载）
    class Class2
    {
    // __call 魔术方法，当调用的实例方法不存在或者不可访问（private）时就会调用此函数
        public function __call($name, $arguments)
        {
            echo "__call \$name: {$name}, \$arguments: " . implode(', ', $arguments);
            echo "\n";
        }
    // __callStatic 魔术方法，当调用的类方法不存在或者不可访问（private）时就会调用此函数
        public static function __callStatic($name, $arguments)
        {
            echo "__callStatic \$name: {$name}, \$arguments: " . implode(', ', $arguments);
            echo "\n";
        }
    }
    $objClass2 = new Class2();
    // 当你调用的实例方法不存在或者不可访问（private）时，就会调用对应的 __call 魔术方法
    echo $objClass2->method1("aaa", "bbb");
    // 当你调用的类方法不存在或者不可访问（private）时，就会调用对应的 __callStatic 魔术方法
    echo Class2::method2("aaa", "bbb");
    ?>

对象的复制和比较
-------------

.. code-block:: php

    <?php
    /**
     * 类的相关知识点 4（对象的复制，对象的比较）
     */

    // 用于演示如何复制对象
    class Class1 {
        public $field1 = "field1";
        public $field2 = "field2";

    // 通过 clone 复制对象时，会调用此魔术方法
        function __clone () {
            echo "__clone";
            echo "\n";
        }
    }

    class Class4 {
        public $field1 = "field1";
        public $field2 = "field2";

    // 通过 clone 复制对象时，会调用此魔术方法
        function __clone () {
            echo "__clone";
            echo "\n";
        }
    }

    $objClass1 = new Class1();
    // 通过 clone 复制对象，会调用 __clone 魔术方法
    $objClass2 = clone $objClass1;
    // 通过 clone 复制的对象为浅拷贝（shallow copy），即成员数据之间的一一赋值, 而所有的引用属性仍然会是一个指向原来的变量的引用（如果要做 deep copy 则需要自己写）
    echo $objClass2->field1; // output: field1
    echo "\n";
    echo $objClass2->field2; // output: field2
    echo "\n";

    // 如果两个对象的属性和属性值都相等，则他们“==”相等，
    if ($objClass1 == $objClass2) {
        echo '$objClass1 == $objClass2';
        echo "\n";
    }
    // 如果两个对象的属性和属性值都相等，但不是同一个类的实例，则它们“==”不相等
    $objClass4 = new Class4();
    if ($objClass1 != $objClass4) {
        echo '$objClass1 != $objClass4';
        echo "\n";
    }

    // 如果两个对象的属性和属性值都相等，且是同一个类的实例，则他们“===”不相等
    if ($objClass1 !== $objClass2) {
        echo '$objClass1 !== $objClass2';
        echo "\n";
    }
    // 如果两个对象的属性和属性值都相等，但不是同一个类的实例，则他们“===”不相等
    if ($objClass1 !== $objClass4) {
        echo '$objClass1 !== $objClass4';
        echo "\n";
    }

    $objClass5 = new Class1();
    // 如果两个对象是同一个类的不同实例，则他们“===”不相等
    if ($objClass1 !== $objClass5) {
        echo '$objClass1 !== $objClass5';
        echo "\n";
    }
    // 如果两个对象是同一个实例，则他们“===”相等
    $objClass3 = &$objClass1;
    if ($objClass1 === $objClass3) {
        echo '$objClass1 === $objClass3';
        echo "\n";
    }
    ?>

总结：

- 一个类的不同实例的属性和值相等时，则 ``==`` 为真；
- 一个类的同一个实例，则 ``===`` 为真；

加载指定的文件和自动加载类文件
------------------------------------

.. code-block:: php

    <?php
    /**
     * 类的相关知识点 5（加载指定的文件，自动加载类文件）
     */
    /*
    * 包含并运行指定文件，可以是绝对路径也可以是相对路径
    * include 找不到的话则警告，然后继续运行（include_once: 在当前文件中只 include 指定文件一次）
    * require 找不到的话则错误，然后终止运行（require_once: 在当前文件中只 require 指定文件一次）
    * include '';
    * require '';
    * include_once '';
    * require_once '';
    */
    // 演示如何通过 __autoload 魔术方法，来实现类的自动加载
    function __autoload($class_name)
    {
    // 加载指定的文件
        require_once $class_name . '.class.php';
    }
    // 如果在当前文件中找不到 MyClass 类，那么就会去调用 __autoload 魔术方法
    $obj = new MyClass();
    echo $obj->name;
    echo "<br />";
    class MyClass {
        public $name = "webabcd";
    }
    ?>

PHP include作用域1：变量的作用域
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

     <?php
         $color = 'green';
         $fruit = 'apple';
     ?>

     <?php
     function foo()
     {
         global $color;
         include 'vars.php';
         echo "A $color $fruit";
     }
     foo();
     // A green apple
     echo "A $color $fruit";
     // A green
     ?>

由此例可看出：

- 被包含文件的变量的 ``PHP include`` 作用域遵从包含文件所在处的作用域。即在函数里使用 ``include`` 将其他文件的变量包含进来，这些变量的作用域为该函数内。
- ``foo()`` 函数外能打印出 ``$color`` 的值，并没有违反前面的规定。那是因为函数开始已经声明 ``$color`` 为 ``global``  (尽管 ``foo()`` 函数外并没有 ``$color`` 变量，此时的 ``$color`` 变量并不是 ``vars.php`` 里面的 ``$color`` 变量，而是一个强制声明为“全局”的新变量，这时它还没有被赋值，当下面包含进 ``vars.php`` 后，根据上面的原则， ``vars.php`` 中的 ``$color`` 变量自动享有函数内的作用域，所以它的值就是全局变量 ``$color`` 的值)。

PHP include作用域2：函数、类的作用域
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    class ClassB {
        /**
        * constructor
        */
        public function __construct(){}
        /**
        * destructor
        */
        public function __destruct() {}
        public function printit() {
            echo 'print it in ClassB.<br />';
        }
    }

    function show_func_included() {
        echo 'show_func_included<br/>';
    }
    ?>

    <?php
    function include_class() {
        include('classb.php');
    }
    include_class();
    $objB = new ClassB();
    $objB->printit();
    // print it in ClassB.
    show_func_included()
    // show_func_included
    ?>

所有在被包含文件中定义的函数和类在被包含后，在包含文件里都具有全局作用域(相当于把“函数”、“类”的代码附加到包含文件的头部)。

结论：

1. 被包含文件的变量的 ``PHP include`` 作用域遵从(不改变)包含文件所在处的作用域。
2. 所有在被包含文件中定义的函数和类在被包含后，在包含文件里都具有全局作用域

命名空间
----------

.. code-block:: php

    <?php
    /**
     * 类的相关知识点 6（命名空间）
     */
    // 以下代码仅用于演示，实际项目中不建议在一个文件中定义多个 namespace
    // 如果当前文件中只有一个命名空间，那么下面的这段可以省略掉命名空间的大括号，直接 namespace MyNamespace1; 即可
    namespace MyNamespace1 {
        const MyConst = "MyNamespace1 MyConst";
        function myFunction () {
            echo "MyNamespace1 myFunction";
            echo "<br />";
        }

        class MyClass {
            public function myMethod () {
                echo "MyNamespace1 MyClass myMethod";
                echo "<br />";
            }
        }
    }
    // 定义命名空间时，可以指定路径
    namespace Sub1\Sub2\MyNamespace2 {
        const MyConst = "MyNamespace2 MyConst";
        function myFunction () {
            echo "MyNamespace2 myFunction";
            echo "<br />";
        }

        class MyClass {
            public function myMethod () {
                echo "MyNamespace2 MyClass myMethod";
                echo "<br />";
            }
        }
    }

    namespace MyNamespace3 {
    // 调用指定命名空间中的指定常量
        echo \MyNamespace1\MyConst;
        echo "<br />";
    // 调用指定命名空间中的指定函数
        \MyNamespace1\myFunction();
    // 实例化指定命名空间中的类
        $obj1 = new \MyNamespace1\MyClass();
        $obj1->myMethod();
    }

    namespace MyNamespace4 {

    // use 指定的命名空间
        use \Sub1\Sub2\MyNamespace2;

    // 之后不用再写全命名空间的路径了，因为之前 use 过了
        echo MyNamespace2\MyConst;
        echo "<br />";
        MyNamespace2\myFunction();
        $obj1 = new MyNamespace2\MyClass();
        $obj1->myMethod();
    }

    namespace MyNamespace5 {

    // use 指定的命名空间，并为其设置别名
        use \Sub1\Sub2\MyNamespace2 as xxx;

    // 之后再调用命名空间时，可以使用其别名
        echo xxx\MyConst;
        echo "<br />";
        xxx\myFunction();
        $obj1 = new xxx\MyClass();
        $obj1->myMethod();
    }
    ?>


