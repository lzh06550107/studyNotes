*******
生成器
*******

PHP5.5之前是通过定义类实现Iterator接口的方式来构造迭代器，现在通过yield构造迭代器将更加提升性能节省系统开销。

这种方法的优点是显而易见的.它可以让你在处理大数据集合的时候不用一次性的加载到内存中，甚至你可以处理无限大的数据流。

如上面例子所示，这个迭代器的功能是生成从1到1000000的数字，循环输出，那么使用以往的方式是生成好这1到1000000的数字到数组中，将会十分占用内存，因为是事先就要生成好所有结果，而不是用的时候按需生成，也就是说调用xrange这个迭代器的时候，里面的函数还没有真正的运行，直到你每一次的迭代。
再看看PHP官网的例子：

.. code-block:: php

    <?php
    // 原始迭代器实现
    foreach (range(0, 1000000) as $number) {
        echo "$number ";
    }

    echo PHP_EOL;

    // 使用生成器实现
    function xrange($start, $limit, $step = 1) {
        for ($i = $start; $i <= $limit; $i += $step) {
            yield $i;
        }
    }
    echo 'Single digit odd numbers: ';
    /*
     * Note that an array is never created or returned,
     * which saves memory.
     */
    foreach (xrange(1, 1000000, 1) as $number) {
        echo "$number ";
    }

    echo PHP_EOL;
    ?>

PHP5.5引入了迭代生成器的概念，迭代的概念早就在PHP有了，但是迭代生成器是PHP的一个新特性，这跟python3中的迭代生成器类似，看看PHP5.5的迭代生成器如何定义。

生成器提供了一种简单的方法来实现简单的迭代器，而无需实现Iterator接口的类的开销或复杂性。

一个生成器允许你编写使用foreach遍历一组数据的代码，而不需要在内存中建立一个数组，这可能会导致你超出内存限制，或者需要大量的处理时间来生成。相反，您可以编写一个生成器函数，它与普通函数相同，除了不是一次返回，生成器可以根据需要产生多次，以提供要迭代的值。

生成器对象
==========
当第一次调用生成器函数时，返回内部返回Generator类的一个对象。这个对象实现Iterator接口的方式与只向前迭代器对象的方式大致相同，并且提供了可以被调用来操纵生成器状态的方法，包括向它发送值和从中返回值。

.. code-block:: php

    <?php
    function getLines($file) {
        $f = fopen($file, 'r');
        try {
            while ($line = fgets($f)) {
                yield $line;
            }
        } finally {
            fclose($f);
        }
    }

    foreach (getLines("file.txt") as $n => $line) {
        if ($n > 5) break;
        echo $line;
    }
    ?>

请记住，生成器函数的执行被推迟，直到迭代结果（Generator对象）开始。注意区分将一个生成器的结果分配给一个变量而不是立即迭代。

.. code-block:: php

    <?php
    $some_state = 'initial';

    function gen () {
        global $some_state;

        echo "gen() execution start\n";
        $some_state = "changed";

        yield 1;
        yield 2;
    }

    function peek_state () {
        global $some_state;
        echo "\$some_state = $some_state\n";
    }

    echo "calling gen()...\n";
    $result = gen(); // 结果是一个生成器对象，不会立即执行函数
    echo "gen() was called\n";

    peek_state();

    echo "iterating...\n";
    foreach ($result as $val) { // 开始执行函数
        echo "iteration: $val\n";
        peek_state();
    }
    /*
    calling gen()...
    gen() was called
    $some_state = initial
    iterating...
    gen() execution start
    iteration: 1
    $some_state = changed
    iteration: 2
    $some_state = changed
    */
    ?>

在使用结果之前，如果你需要在函数被调用的时候执行一些操作，你必须把你的生成器包装到另一个函数中。

.. code-block:: php

    <?php
    function some_generator() {
        global $some_state;

        $some_state = "changed";
        return gen();
    }
    ?>

您也可以在函数范围内声明生成器函数。

.. code-block:: php

    <?php
    function gen_n($n){
        function gen_t($len){
            for($i = 1; $i < $len; $i++)
                yield $i;
        }

        foreach(gen_t($n) as $out)
            printf("%d, ", $out);

        printf("%d", ++$out); // 输出最后一个值
    }

    gen_n(15);
    ?>

生成器语法
==========
生成器函数看起来就像一个正常的函数，除了取代一个返回值，而是返回一个生成器(yield)来产生尽可能多的值。

当一个生成器函数被调用时，它返回一个可以被迭代的对象。当你遍历这个对象（例如，通过foreach循环）时，PHP会在每次需要一个值时调用generator函数，然后在生成器产生一个值时保存生成器的状态，以便当下一个需要时恢复。

一旦没有更多的值被生成，那么生成器函数可以简单地退出，调用代码继续，就好像一个数组已经用完了值一样。

.. note:: 在PHP 5中，生成器无法返回值：这样做会导致编译错误。一个空的return语句是一个生成器中的有效语法，它会终止生成器。从PHP 7.0开始，一个生成器可以返回值，可以使用Generator :: getReturn()来检索。

yield关键字
------------
生成器函数的核心是yield关键字。以最简单的形式，yield语句看起来非常像return语句，不同之处在于，yield并不是停止函数的执行和返回，而是为循环遍历生成器的代码提供一个值，并暂停生成器函数的执行。

yield 只能在函数中使用，否则会报PHP Fatal error:The "yield" expression can only be used inside a function，凡是使用了yield关键字的函数都会返回一个Generator对象。

每次代码执行到yield语句都会中止执行，返回yield语句中表达式的值给Generator对象，继续迭代Generator对象时，yield后面的代码会接着执行，直到所有yield语句全部执行完毕或者有return语句，这个renturn语句只能返回nullreturn;，否则会编译错误。

.. code-block:: php

    <?php
    function gen_one_to_three() {
        echo '函数被调用'.PHP_EOL;
        for ($i = 1; $i <= 3; $i++) {
            // Note that $i is preserved between yields.
            yield $i;
        }
    }

    echo '开始调用函数'.PHP_EOL;
    $generator = gen_one_to_three();
    echo '函数调用完成'.PHP_EOL;
    foreach ($generator as $value) {
        echo "$value\n";
    }
    ?>

.. note:: 在内部，连续的整数键将与赋值相匹配，就像使用非关联数组一样。

.. caution:: 如果在表达式上下文中使用yield（例如，在赋值的右侧），则必须在PHP 5中用括号括住yield语句。例如，这是有效的：

``$data = (yield $value);``

但是下面代码会导致PHP 5中的分析错误：

``$data = yield $value;``

但是在PHP 7中不存在这样的问题。这个语法可以和 ``Generator :: send()`` 方法一起使用。

生成带键的值
---------------
PHP也支持关联数组，而生成器也不例外。除了产生简单的值，如上所示，您也可以同时产生一个键。

产生键/值对的语法与用于定义关联数组非常相似，如下所示。

.. code-block:: php

    <?php
    /*
     * The input is semi-colon separated fields, with the first
     * field being an ID to use as a key.
     */

    $input = <<<'EOF'
    1;PHP;Likes dollar signs
    2;Python;Likes whitespace
    3;Ruby;Likes blocks
    EOF;

    function input_parser($input) {
        foreach (explode("\n", $input) as $line) {
            $fields = explode(';', $line);
            $id = array_shift($fields);

            yield $id => $fields;
        }
    }

    foreach (input_parser($input) as $id => $fields) {
        echo "$id:\n";
        echo "    $fields[0]\n";
        echo "    $fields[1]\n";
    }
    ?>

.. caution:: 与前面显示的简单值一样，在表达式上下文中产生键/值对需要将yield语句括起来：

``$data = (yield $key => $value);``

生成null值
------------
可以不带参数地调用Yield来用自动键产生NULL值。

.. code-block:: php

    <?php
    function gen_three_nulls() {
        foreach (range(1, 3) as $i) {
            yield;
        }
    }

    var_dump(iterator_to_array(gen_three_nulls()));
    ?>

生成引用值
-----------
生成器函数能够通过引用值来产生值。这与从函数返回引用的方式完成相同：通过在函数名称前加一个＆符号。

.. code-block:: php

    <?php
    function &gen_reference() {
        $value = 3;

        while ($value > 0) {
            yield $value;
        }
    }

    /*
     * 注意，在循环中引用$number的值，又因为生成器生成引用值
     * 所以，该值在循环中可以改变。
     */
    foreach (gen_reference() as &$number) {
        echo (--$number).'... ';
    }
    // 输出结果： 2... 1... 0...
    ?>

通过yield from来委派生成器
-------------------------------
在PHP 7中，生成器代理允许您使用yield from关键字从另一个生成器，Traversable对象或数组来生成值。然后外部生成器将产生来自内部生成器，对象或数组的所有值，直到该值不再有效，然后在外部生成器中继续执行。

如果一个生成器与yield from一起使用，则yield from表达式将返回由inner generator返回的任何值。

.. caution:: 使用 ``iterator_to_array()`` 存储为数组时，yield from不会重置键。它保留由Traversable对象或数组返回的键。因此使用另一个yield或者yield from一些值可能共享一个共同的key。一旦插入到数组中，将用该键覆盖以前的值。

常见情况是iterator_to_array()默认返回一个键值对数组，导致可能意外的结果。 iterator_to_array()有第二个参数use_keys，可以设置为FALSE来收集所有的值，同时忽略发生器返回的键。

.. code-block:: php

    <?php
    function from() {
        yield 1; // key 0
        yield 2; // key 1
        yield 3; // key 2
    }
    function gen() {
        yield 0; // key 0
        yield from from(); // keys 0-2
        yield 4; // key 1
    }
    // pass false as second parameter to get an array [0, 1, 2, 3, 4]
    var_dump(iterator_to_array(gen()));

    /* 不传入第二个参数为false的输出结果
    array(3) {
      [0]=>
      int(1)
      [1]=>
      int(4)
      [2]=>
      int(3)
    }
    */
    ?>

比较生成器和迭代器对象
======================
生成器的主要优点是简单。与实现Iterator类相比，更少的样板代码必须被编写，并且代码通常更具可读性。例如，下面的函数和类是等价的：

.. code-block:: php

    <?php
    function getLinesFromFile ($fileName) {
        if (!$fileHandle = fopen($fileName, 'r')) {
            return;
        }

        while (false !== $line = fgets($fileHandle)) {
            yield $line;
        }

        fclose($fileHandle);
    }

    // 迭代器实现

    class LineIterator implements Iterator {
        protected $fileHandle;

        protected $line;
        protected $i;

        public function __construct ($fileName) {
            if (!$this->fileHandle = fopen($fileName, 'r')) {
                throw new RuntimeException('Couldn\'t open file "' . $fileName . '"');
            }
        }

        public function rewind () {
            fseek($this->fileHandle, 0);
            $this->line = fgets($this->fileHandle);
            $this->i = 0;
        }

        public function valid () {
            return false !== $this->line;
        }

        public function current () {
            return $this->line;
        }

        public function key () {
            return $this->i;
        }

        public function next () {
            if (false !== $this->line) {
                $this->line = fgets($this->fileHandle);
                $this->i++;
            }
        }

        public function __destruct () {
            fclose($this->fileHandle);
        }
    }
    ?>

然而，这种灵活性的代价是：生成器是只向前迭代器，一旦迭代开始就不能倒回。这也意味着同一个生成器不能迭代多次：生成器将需要通过再次调用生成器函数来重建。

生成器对象函数
==============

.. code-block:: php

    <?php
    Generator implements Iterator {

        // 获取生成的值
        public mixed current ( void )
        // 获取生成器的返回值
        public mixed getReturn ( void )
        // 获取生成器的键
        public mixed key ( void )
        // 恢复生成器的执行
        public void next ( void )
        // 重置迭代器
        public void rewind ( void )
        // 给生成器发送一个值
        public mixed send ( mixed $value )
        // 给生成器抛一个异常
        public mixed throw ( Throwable $exception )
        // 检查迭代器是否已经关闭
        public bool valid ( void )
        // 系列化回调
        public void __wakeup ( void )
    }
    ?>

mixed getReturn ( void )
----------------------------
一旦执行完成，返回生成器的返回值。

.. code-block:: php

    <?php
    $gen = (function() {
        yield 1;
        yield 2;

        return 3;
    })();

    foreach ($gen as $val) {
        echo $val, PHP_EOL;
    }
     //必须迭代完成生产器，才能获取返回值
    echo $gen->getReturn(), PHP_EOL;
    // 输出1 2 3
    ?>

void next ( void )
--------------------
在yield处，恢复生成器的执行。next本质上调用send(null)方法。

应该注意的是，next()向生成器发送一个隐式的null，以确保如果生成器期望一个值被发送到它并且代码不执行任何send()时，不会被阻塞。

void Generator::rewind ( void )
----------------------------------
如果迭代已经开始，这将抛出一个异常。

Generator::send(mixed $value)
-----------------------------------
将当前yield表达式的结果发送给生成器，并恢复生成器的执行。

向生成器中传入一个值，并且当做yield表达式的结果，然后继续执行生成器。如果当这个方法被调用时，生成器不在 yield 表达式处，那么在传入值之前，它会先运行到第一个 yield 表达式。(注意，如果不调用current()获取第一个yield表达式的参数值，可能会被忽略)

参数：

- value：发送到发生器的值。该值将是生成器当前所处的yield表达式的返回值。

返回值：

返回发送消息之后下一个yield表达式的参数值。

.. code-block:: php

    <?php
     function test () {
        $a = (yield 111); //1
        var_dump('test()->$a:' . $a);
        $b = (yield 222); //2
        var_dump('test()->$b:' . $b);
        $c = (yield 444); //3
        var_dump('test()->$c:' . $c);

        return 666;
    }

    $gen = test();
    echo "第一次输出：\n";
    var_dump($gen->current()); //执行到第1步中断，返回111
    echo "第二次输出：\n";
    var_dump($gen->send(333)); //从第1步恢复执行并传入表达式值333，执行到第2步中断，返回222
    echo "第三次输出：\n";
    var_dump($gen->next()); //从第2步恢复执行并传入表达式值null，执行到第3步中断，这时表达式有444值，但next()不返回值
    echo "第四次输出：\n";
    var_dump($gen->send(555)); //从第3步恢复并传入表达式值555，生成器执行结束，由于没有yield表达式，返回null
    echo "生成器返回值：\n";
    var_dump($gen->getReturn()); // 生成器返回值666
    ?>

mixed Generator::throw ( Throwable $exception )
-------------------------------------------------------
向生成器中抛入异常并恢复生成器的执行。行为将与当前的yield表达式被throw $ exception语句替换相同。

如果在调用此方法时生成器已经关闭，则异常将被抛入调用者的上下文中。

参数：

- exception：抛入生成器的异常

返回值：

返回生成器的值。经测试只会返回NULL。

.. code-block:: php

    <?php
    $gen = (function () {
        try {
            yield 1;
        } catch (Exception $e) {
            echo $e->getMessage();
        }
    })();

    var_dump($gen->throw(new Exception('gen throw exception')));
    ?>

协程
====
我们要知道，对于单核处理器，多任务的执行原理是让每一个任务执行一段时间，然后中断、让另一个任务执行然后在中断后执行下一个，如此反复。由于其执行切换速度很快，让外部认为多个任务实际上是 “并行” 的。

.. note:: 多任务协作这个术语中的 “协作” 很好的说明了如何进行这种切换的：它要求当前正在运行的任务自动把控制传回给调度器，这样就可以运行其他任务了。这与 “抢占” 多任务相反, 抢占多任务是这样的：调度器可以中断运行了一段时间的任务, 不管它喜欢还是不喜欢。协作多任务在 Windows 的早期版本 (windows95) 和 Mac OS 中有使用, 不过它们后来都切换到使用抢先多任务了。理由相当明确：如果你依靠程序自动交出控制的话，那么一些恶意的程序将很容易占用整个CPU，不与其他任务共享。

协程，又称微线程，纤程。英文名Coroutine。协程的概念很早就提出来了，但直到最近几年才在某些语言（如Lua）中得到广泛应用。

子程序，或者称为函数，在所有语言中都是层级调用，比如A调用B，B在执行过程中又调用了C，C执行完毕返回，B执行完毕返回，最后是A执行完毕。所以子程序调用是通过栈实现的，一个线程就是执行一个子程序。子程序调用总是一个入口，一次返回，调用顺序是明确的。而协程的调用和子程序不同。

协程看上去也是子程序，但执行过程中，在子程序内部可中断，然后转而执行别的子程序，在适当的时候再返回来接着执行。

那和多线程比，协程有何优势？

最大的优势就是协程极高的执行效率。因为子程序切换不是线程切换，而是由程序自身控制，因此，没有线程切换的开销，和多线程比，线程数量越多，协程的性能优势就越明显。

第二大优势就是不需要多线程的锁机制，因为只有一个线程，也不存在同时写变量冲突，在协程中控制共享资源不加锁，只需要判断状态就好了，所以执行效率比多线程高很多。

因为协程是一个线程执行，那怎么利用多核CPU呢？最简单的方法是多进程+协程，既充分利用多核，又充分发挥协程的高效率，可获得极高的性能。

我们结合之前的例子，可以发现，yield 作为可以让一段任务自身中断，然后回到外部继续执行。利用这个特性可以实现多任务调度的功能，配合 yield 的双向通讯功能，以实现任务和调度器之间进行通信。

这样的功能对于读写和操作 Stream 资源时尤为重要，我们可以极大的提高程序对于并发流资源的处理能力，比如实现 tcp server。

生成器为可中断的函数
------------------------
要从生成器认识协程, 理解它内部是如何工作是非常重要的: 生成器是一种可中断的函数, 在它里面的yield构成了中断点。

还是看例一个子, 调用xrange(1,1000000)的时候, xrange()函数里代码其实并没有真正地运行. 它只是返回了一个迭代器：

.. code-block:: php

    <?php
    $range = xrange(1, 1000000);
    var_dump($range); // object(Generator)#1
    var_dump($range instanceof Iterator); // bool(true)
    ?>

这也解释了为什么xrange叫做迭代生成器, 因为它返回一个迭代器, 而这个迭代器实现了Iterator接口。

调用迭代器的方法一次, 其中的代码运行一次。例如, 如果你调用$range->next(), 那么xrange()里的代码就会运行到控制流第一次出现yield的地方。而函数内传递给yield语句的返回值可以通过$range->current()获取。

为了继续执行生成器中yield后的代码, 你就需要调用$range->next()方法。这将再次启动生成器, 直到下一次yield语句出现。因此,连续调用next()和current()方法, 你就能从生成器里获得所有的值, 直到再没有yield语句出现。

对xrange()来说, 这种情形出现在$i超过$end时. 在这中情况下, 控制流将到达函数的终点,因此将不执行任何代码。一旦这种情况发生,vaild()方法将返回假, 这时迭代结束。

协程的支持是在迭代生成器的基础上, 增加了可以回送数据给生成器的功能(调用者发送数据给被调用的生成器函数). 这就把生成器到调用者的单向通信转变为两者之间的双向通信.

传递数据的功能是通过迭代器的send()方法实现的. 下面的logger()协程是这种通信如何运行的例子：

.. code-block:: php

    <?php
    function logger($fileName) {
        $fileHandle = fopen($fileName, 'a');
        while (true) {
            fwrite($fileHandle, yield . "\n");
        }
    }

    $logger = logger(__DIR__ . '/log');
    $logger->send('Foo');
    $logger->send('Bar');
    ?>

正如你能看到,这儿yield没有作为一个语句来使用, 而是用作一个表达式, 即它能被演化成一个值. 这个值就是调用者传递给send()方法的值. 在这个例子里, yield表达式将首先被”Foo”替代写入Log, 然后被”Bar”替代写入Log.

上面的例子里演示了yield作为接受者, 接下来我们看如何同时进行接收和发送的例子：

.. code-block:: php

    <?php
    function gen() {
        $ret = (yield 'yield1');
        var_dump($ret);
        $ret = (yield 'yield2');
        var_dump($ret);
    }

    $gen = gen();
    // 执行该语句时，进入第一个yield表达式时，先执行表达式参数(如果参数是函数或者表达式时)
    // 通过该生成器的current()方法中断生成器并获取yield表达式值。
    var_dump($gen->current());    // string(6) "yield1"
    // 执行该语句时，第一个yield语句中断恢复
    // 并修改yield表达式的值，继续执行进入第二个yield语句中断返回生成器，
    var_dump($gen->send('ret1')); // string(4) "ret1"   (the first var_dump in gen)
    // string(6) "yield2" (the var_dump of the ->send() return value)
    var_dump($gen->send('ret2')); // string(4) "ret2"   (again from within gen)
    // NULL (the return value of ->send())
    ?>

多任务协作
------------
如果阅读了上面的logger()例子, 你也许会疑惑“为了双向通信我为什么要使用协程呢？我完全可以使用其他非协程方法实现同样的功能啊?”, 是的, 你是对的, 但上面的例子只是为了演示了基本用法, 这个例子其实并没有真正的展示出使用协程的优点.

正如上面介绍里提到的,协程是非常强大的概念,不过却应用的很稀少而且常常十分复杂.要给出一些简单而真实的例子很难。

在这篇文章里,我决定去做的是使用协程实现多任务协作.我们要解决的问题是你想并发地运行多任务(或者“程序”）.不过我们都知道CPU在一个时刻只能运行一个任务（不考虑多核的情况）.因此处理器需要在不同的任务之间进行切换,而且总是让每个任务运行 “一小会儿”。

多任务协作这个术语中的“协作”很好的说明了如何进行这种切换的：它要求当前正在运行的任务自动把控制传回给调度器,这样就可以运行其他任务了. 这与“抢占”多任务相反, 抢占多任务是这样的：调度器可以中断运行了一段时间的任务, 不管它喜欢还是不喜欢. 协作多任务在Windows的早期版本(windows95)和Mac OS中有使用, 不过它们后来都切换到使用抢先多任务了. 理由相当明确：如果你依靠程序自动交出控制的话, 那么一些恶意的程序将很容易占用整个CPU, 不与其他任务共享。

现在你应当明白协程和任务调度之间的关系：yield指令提供了任务中断自身的一种方法, 然后把控制交回给任务调度器。因此协程可以运行多个其他任务. 更进一步来说, yield还可以用来在任务和调度器之间进行通信。

为了实现我们的多任务调度, 首先实现“任务” — 一个用轻量级的包装的协程函数：

.. code-block:: php

    <?php
    class Task {
        protected $taskId; // 一个任务就是用任务ID标记的一个协程(函数)
        protected $coroutine; // 一个生成器
        protected $sendValue = null; // 使用setSendValue()方法, 你可以指定哪些值将被发送到下次的恢复
        protected $beforeFirstYield = true; //用来解决send()发送消息跳过第一个yield值的情况

        public function __construct($taskId, Generator $coroutine) {
            $this->taskId = $taskId;
            $this->coroutine = $coroutine;
        }

        public function getTaskId() {
            return $this->taskId;
        }

        public function setSendValue($sendValue) {
            $this->sendValue = $sendValue;
        }

        public function run() {
            if ($this->beforeFirstYield) {
                $this->beforeFirstYield = false;
                return $this->coroutine->current(); //返回第一个yield表达式参数值，并中断执行
            } else {
                // 向本任务的生成器发送值，并恢复执行
                // 当再次遇到yield表达式时，则
                $retval = $this->coroutine->send($this->sendValue);
                $this->sendValue = null;
                return $retval;
            }
        }

        public function isFinished() {
            return !$this->coroutine->valid();
        }
    }
    ?>

调度器现在不得不比多任务循环要做稍微多点了, 然后才运行多任务：

.. code-block:: php

    <?php
    class Scheduler {
        protected $maxTaskId = 0;
        protected $taskMap = []; // taskId => task，任务映射集合
        protected $taskQueue; // 任务执行队列

        public function __construct () {
            $this->taskQueue = new SplQueue();
        }

        public function newTask (Generator $coroutine) {
            $tid = ++$this->maxTaskId; // 任务id
            $task = new Task($tid, $coroutine); //创建任务
            $this->taskMap[$tid] = $task; // 任务加入映射表中
            $this->schedule($task); //任务加入执行队列
            return $tid;
        }

        public function schedule (Task $task) {
            $this->taskQueue->enqueue($task); // 加入任务到队列
        }

        public function run () {
            while (!$this->taskQueue->isEmpty()) {
                // 从任务队列中获取任务并执行
                $task = $this->taskQueue->dequeue();
                $task->run();

                // 任务yield中断让出进程后
                // 如果任务已经执行完成，则清除该任务，包含从映射表和队列中删除
                if ($task->isFinished()) {
                    unset($this->taskMap[$task->getTaskId()]);
                } else {
                    // 如果任务没有执行完成，则加入任务执行队列，继续等待执行
                    $this->schedule($task);
                }
            }
        }
    }
    ?>

newTask()方法（使用下一个空闲的任务id）创建一个新任务,然后把这个任务放入任务map数组里. 接着它通过把任务放入任务队列里来实现对任务的调度. 接着run()方法扫描任务队列, 运行任务.如果一个任务结束了, 那么它将从队列里删除, 否则它将在队列的末尾再次被调度.

让我们看看下面具有两个简单（没有什么意义）任务的调度器：

.. code-block:: php

    <?php
    // 生成器对象的任务
    function task1() {
        for ($i = 1; $i <= 10; ++$i) {
            echo "This is task 1 iteration $i.\n";
            yield;
        }
    }
    // 生成器对象的任务
    function task2() {
        for ($i = 1; $i <= 5; ++$i) {
            echo "This is task 2 iteration $i.\n";
            yield;
        }
    }
    // 初始化一个调度器
    $scheduler = new Scheduler;
    // 增加任务
    $scheduler->newTask(task1());
    $scheduler->newTask(task2());
    // 开始调度执行任务
    $scheduler->run();
    ?>

与调度器之间通信
^^^^^^^^^^^^^^
么我们来看下一个问题：任务和调度器之间的通信。

我们将使用 ”进程用来和操作系统会话” 的同样的方式来通信：系统调用。

我们需要系统调用的理由是操作系统与进程相比它处在不同的权限级别上。因此为了执行特权级别的操作（如杀死另一个进程), 就不得不以某种方式把控制传回给内核, 这样内核就可以执行所说的操作了。再说一遍, 这种行为在内部是通过使用中断指令来实现的。 过去使用的是通用的int指令, 如今使用的是更特殊并且更快速的syscall/sysenter指令。

我们的任务调度系统将反映这种设计：不是简单地把调度器传递给任务（这样就允许它做它想做的任何事), 我们将通过给yield表达式传递信息来与系统调用通信。这儿yield即是中断, 也是传递信息给调度器（和从调度器传递出信息）的方法。

为了说明系统调用, 我们对可调用的系统调用做一个小小的封装：

.. code-block:: php

    <?php
    class SystemCall {
        // 回调函数
        protected $callback;

        public function __construct(callable $callback) {
            $this->callback = $callback;
        }

        public function __invoke(Task $task, Scheduler $scheduler) {
            $callback = $this->callback;
            // 传入任务和调度器给回调函数，这样，可以给任务发送消息
            // 然后再次把该任务加入任务队列
            return $callback($task, $scheduler);
        }
    }
    ?>

它和其他任何可调用的对象(使用_invoke)一样的运行, 不过它要求调度器把正在调用的任务和自身传递给这个函数。

为了解决这个问题我们不得不稍微的修改调度器的run方法：

.. code-block:: php

    <?php
    public function run () {
            while (!$this->taskQueue->isEmpty()) {
                // 从任务队列中获取任务并执行
                $task = $this->taskQueue->dequeue();
                $retval = $task->run();

                // 如果需要给任务传递消息，则需要调用传入的函数
                if ($retval instanceof SystemCall) {
                    $retval($task, $this);
                    continue;
                }

                // 任务yield中断让出进程后
                // 如果任务已经执行完成，则清除该任务，包含从映射表和队列中删除
                if ($task->isFinished()) {
                    unset($this->taskMap[$task->getTaskId()]);
                } else {
                    // 如果任务没有执行完成，则加入任务执行队列，继续等待执行
                    $this->schedule($task);
                }
            }
    }
    ?>

第一个系统调用除了返回任务ID外什么都没有做：

.. code-block:: php

    <?php
    // 向任务传递消息
    function getTaskId() {
        // 初始化一个系统调用对象
        return new SystemCall(function(Task $task, Scheduler $scheduler) {
            // 向任务传递当前任务号的消息
            $task->setSendValue($task->getTaskId());
            // 把任务加入调度队列
            $scheduler->schedule($task);
        });
    }
    ?>

这个函数设置任务id为下一次发送的值, 并再次调度了这个任务 .由于使用了系统调用, 所以调度器不能自动调用任务, 我们需要手工调度任务（稍后你将明白为什么这么做). 要使用这个新的系统调用的话, 我们要重新编写以前的例子：

.. code-block:: php

    <?php
    // 生成器对象的任务
    function task($max) {
        // 传入给yield表达式的是系统调用对象
        $tid = (yield getTaskId()); // <-- here's the syscall!
        // 输出传入的消息
        for ($i = 1; $i <= $max; ++$i) {
            echo "This is task $tid iteration $i.\n";
            yield;
        }
    }

    $scheduler = new Scheduler;

    $scheduler->newTask(task(10));
    $scheduler->newTask(task(5));

    $scheduler->run();
    ?>

这段代码将给出与前一个例子相同的输出。请注意系统调用如何同其他任何调用一样正常地运行, 只不过预先增加了yield。

要创建新的任务, 然后再杀死它们的话, 需要两个以上的系统调用：

.. code-block:: php

    <?php
    // 给任务传递一个创建新任务的消息
    function newTask(Generator $coroutine) {
        return new SystemCall(
            function(Task $task, Scheduler $scheduler) use ($coroutine) {
                //向任务传递创建新任务的消息
                $task->setSendValue($scheduler->newTask($coroutine));
                $scheduler->schedule($task);
            }
        );
    }
    // 给任务传递一个杀死指定任务的消息
    function killTask($tid) {
        return new SystemCall(
            function(Task $task, Scheduler $scheduler) use ($tid) {
                //向任务传递杀死指定任务的消息
                $task->setSendValue($scheduler->killTask($tid));
                $scheduler->schedule($task);
            }
        );
    }
    ?>

killTask函数需要在调度器里增加一个方法：

.. code-block:: php

    <?php
    public function killTask($tid) {
        if (!isset($this->taskMap[$tid])) {
            return false;
        }

        unset($this->taskMap[$tid]);

        // This is a bit ugly and could be optimized so it does not have to walk the queue,
        // but assuming that killing tasks is rather rare I won't bother with it now
        foreach ($this->taskQueue as $i => $task) {
            if ($task->getTaskId() === $tid) {
                unset($this->taskQueue[$i]);
                break;
            }
        }

        return true;
    }
    ?>

用来测试新功能的微脚本：

.. code-block:: php

    <?php
    // 获取当前任务号
    function childTask() {
        $tid = (yield getTaskId());
        while (true) {
            echo "Child task $tid still alive!\n";
            yield;
        }
    }

    // 获取当前任务号并创建新的任务，新的任务也是获取当前任务号
    // 然后杀死这些新创建的任务
    function task() {
        $tid = (yield getTaskId());
        // 创建新任务
        $childTid = (yield newTask(childTask()));

        for ($i = 1; $i <= 6; ++$i) {
            echo "Parent task $tid iteration $i.\n";
            yield;
            // 杀死新的任务
            if ($i == 3) yield killTask($childTid);
        }
    }

    $scheduler = new Scheduler;
    $scheduler->newTask(task());
    $scheduler->run();
    ?>

现在你可以实现许多进程管理调用. 例如 wait（它一直等待到任务结束运行时), exec（它替代当前任务)和fork（它创建一个当前任务的克隆)。fork非常酷,而 且你可以使用PHP的协程真正地实现它, 因为它们都支持克隆。

非阻塞IO
------------
很明显, 我们的任务管理系统的真正很酷的应用应该是web服务器. 它有一个任务是在套接字上侦听是否有新连接, 当有新连接要建立的时候, 它创建一个新任务来处理新连接。

Web服务器最难的部分通常是像读数据这样的套接字操作是阻塞的。例如PHP将等待到客户端完成发送为止. 对一个Web服务器来说, 这有点不太高效。因为服务器在一个时间点上只能处理一个连接。

解决方案是确保在真正对套接字读写之前该套接字已经“准备就绪”。为了查找哪个套接字已经准备好读或者写了, 可以使用 流选择函数。

首先,让我们添加两个新的 syscall, 它们将等待直到指定socket 准备好：

.. code-block:: php

    <?php
    function waitForRead($socket) {
        return new SystemCall(
            function(Task $task, Scheduler $scheduler) use ($socket) {
                $scheduler->waitForRead($socket, $task);
            }
        );
    }

    function waitForWrite($socket) {
        return new SystemCall(
            function(Task $task, Scheduler $scheduler) use ($socket) {
                $scheduler->waitForWrite($socket, $task);
            }
        );
    }
    ?>

这些 syscall 只是在调度器中代理其各自的方法：

.. code-block:: php

    <?php
    // resourceID => [socket, tasks]
    protected $waitingForRead = [];
    protected $waitingForWrite = [];

    public function waitForRead($socket, Task $task) {
        if (isset($this->waitingForRead[(int) $socket])) {
            $this->waitingForRead[(int) $socket][1][] = $task;
        } else {
            $this->waitingForRead[(int) $socket] = [$socket, [$task]];
        }
    }

    public function waitForWrite($socket, Task $task) {
        if (isset($this->waitingForWrite[(int) $socket])) {
            $this->waitingForWrite[(int) $socket][1][] = $task;
        } else {
            $this->waitingForWrite[(int) $socket] = [$socket, [$task]];
        }
    }
    ?>

waitingForRead 及 waitingForWrite 属性是两个承载等待的socket 及等待它们的任务的数组. 有趣的部分在于下面的方法,它将检查 socket 是否可用, 并重新安排各自任务：

.. code-block:: php

    <?php
    protected function ioPoll($timeout) {
        $rSocks = [];
        foreach ($this->waitingForRead as list($socket)) {
            $rSocks[] = $socket;
        }

        $wSocks = [];
        foreach ($this->waitingForWrite as list($socket)) {
            $wSocks[] = $socket;
        }

        $eSocks = []; // dummy

        if (!stream_select($rSocks, $wSocks, $eSocks, $timeout)) {
            return;
        }

        foreach ($rSocks as $socket) {
            list(, $tasks) = $this->waitingForRead[(int) $socket];
            unset($this->waitingForRead[(int) $socket]);

            foreach ($tasks as $task) {
                $this->schedule($task);
            }
        }

        foreach ($wSocks as $socket) {
            list(, $tasks) = $this->waitingForWrite[(int) $socket];
            unset($this->waitingForWrite[(int) $socket]);

            foreach ($tasks as $task) {
                $this->schedule($task);
            }
        }
    }
    ?>

stream_select 函数接受承载读取、写入以及待检查的socket的数组（我们无需考虑最后一类). 数组将按引用传递, 函数只会保留那些状态改变了的数组元素. 我们可以遍历这些数组, 并重新安排与之相关的任务.

为了正常地执行上面的轮询动作, 我们将在调度器里增加一个特殊的任务：

.. code-block:: php

    <?php
    protected function ioPollTask() {
        while (true) {
            if ($this->taskQueue->isEmpty()) {
                $this->ioPoll(null);
            } else {
                $this->ioPoll(0);
            }
            yield;
        }
    }
    ?>

需要在某个地方注册这个任务, 例如, 你可以在run()方法的开始增加$this->newTask($this->ioPollTask()). 然后就像其他任务一样每执行完整任务循环一次就执行轮询操作一次（这么做一定不是最好的方法), ioPollTask将使用0秒的超时来调用ioPoll, 也就是stream_select将立即返回（而不是等待）.

只有任务队列为空时,我们才使用null超时,这意味着它一直等到某个套接口准备就绪.如果我们没有这么做,那么轮询任务将一而再, 再而三的循环运行, 直到有新的连接建立. 这将导致100%的CPU利用率. 相反, 让操作系统做这种等待会更有效.

现在编写服务器就相对容易了：

.. code-block:: php

    <?php
    function server($port) {
        echo "Starting server at port $port...\n";

        $socket = @stream_socket_server("tcp://localhost:$port", $errNo, $errStr);
        if (!$socket) throw new Exception($errStr, $errNo);

        stream_set_blocking($socket, 0);

        while (true) {
            yield waitForRead($socket);
            $clientSocket = stream_socket_accept($socket, 0);
            yield newTask(handleClient($clientSocket));
        }
    }

    function handleClient($socket) {
        yield waitForRead($socket);
        $data = fread($socket, 8192);

        $msg = "Received following request:\n\n$data";
        $msgLength = strlen($msg);

        $response = <<<RES
    HTTP/1.1 200 OK\r
    Content-Type: text/plain\r
    Content-Length: $msgLength\r
    Connection: close\r
    \r
    $msg
    RES;

        yield waitForWrite($socket);
        fwrite($socket, $response);

        fclose($socket);
    }

    $scheduler = new Scheduler;
    $scheduler->newTask(server(8000));
    $scheduler->run();
    ?>

这段代码实现了接收localhost:8000上的连接, 然后返回发送来的内容作为HTTP响应. 当然它还能处理真正的复杂HTTP请求, 上面的代码片段只是演示了一般性的概念.

你可以使用类似于ab -n 10000 -c 100 localhost:8000/这样命令来测试服务器. 这条命令将向服务器发送10000个请求, 并且其中100个请求将同时到达. 使用这样的数目, 我得到了处于中间的10毫秒的响应时间. 不过还有一个问题：有少数几个请求真正处理的很慢（如5秒), 这就是为什么总吞吐量只有2000请求/秒（如果是10毫秒的响应时间的话, 总的吞吐量应该更像是10000请求/秒)。

协程堆栈
----------
如果你试图用我们的调度系统建立更大的系统的话, 你将很快遇到问题：我们习惯了把代码分解为更小的函数, 然后调用它们。然而, 如果使用了协程的话, 就不能这么做了。例如,看下面代码：

.. code-block:: php

    <?php
    function echoTimes($msg, $max) {
        for ($i = 1; $i <= $max; ++$i) {
            echo "$msg iteration $i\n";
            yield;
        }
    }

    function task() {
        echoTimes('foo', 10); // print foo ten times
        echo "---\n";
        echoTimes('bar', 5); // print bar five times
        yield; // force it to be a coroutine
    }

    $scheduler = new Scheduler;
    $scheduler->newTask(task());
    $scheduler->run();
    ?>

这段代码试图把重复循环“输出n次“的代码嵌入到一个独立的协程里，然后从主任务里调用它。然而它无法运行。正如在这篇文章的开始所提到的, 调用生成器（或者协程）将没有真正地做任何事情, 它仅仅返回一个对象。这 也出现在上面的例子里:echoTimes调用除了返回一个（无用的）协程对象外不做任何事情。

为了仍然允许这么做，我们需要在这个裸协程上写一个小小的封装。我们将称呼它为：“协程堆栈”。 因为它将管理嵌套的协程调用堆栈。这将使得通过生成协程来调用子协程成为可能：

``$retval = (yield someCoroutine($foo, $bar));``

使用yield,子协程也能再次返回值：

``yield retval("I'm a return value!");``

retval函数除了返回一个值的封装外没有做任何其他事情。这个封装将表示它是一个返回值。

.. code-block:: php

    <?php
    class CoroutineReturnValue {
        protected $value;

        public function __construct($value) {
            $this->value = $value;
        }

        public function getValue() {
            return $this->value;
        }
    }

    function retval($value) {
        return new CoroutineReturnValue($value);
    }
    ?>

为了把协程转变为协程堆栈（它支持子调用）,我们将不得不编写另外一个函数（很明显,它是另一个协程）：

.. code-block:: php

    <?php
    function stackedCoroutine(Generator $gen) {
        $stack = new SplStack;

        for (;;) {
            // 执行当前协程并中断返回yield的值
            $value = $gen->current();

            // 如果是子协程，则把当前协程压入栈中，对子协程执行
            if ($value instanceof Generator) {
                $stack->push($gen);
                $gen = $value;
                continue;
            }

            // 如果是正常的值
            $isReturnValue = $value instanceof CoroutineReturnValue;
            if (!$gen->valid() || $isReturnValue) {
                if ($stack->isEmpty()) { // 如果栈为空，则退出
                    return;
                }

                // 如果协程执行完成，或者yield返回正常的值，则弹出父协程
                $gen = $stack->pop();
                // 恢复父协程执行并传入返回值给yield表达式
                $gen->send($isReturnValue ? $value->getValue() : NULL);
                continue;
            }

            // ？？？
            $gen->send(yield $gen->key() => $value);
        }
    }
    ?>

http://www.laruence.com/2015/05/28/3038.html

