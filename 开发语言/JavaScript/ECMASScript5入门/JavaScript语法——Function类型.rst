****
函数
****

``JavaScript`` 是一种基于对象的脚本语言， ``JavaScript`` 代码复用的单位是函数，但它的函数比结构化程序设计语言的函数功能更丰富。 ``JavaScript`` 语言中的函数就是“一等公民”，它可以独立存在：而且 ``JavaScript`` 的函数完全可以作为一个类使用（而且它还是该类唯一的构造器）；与此同时，函数本身也是一个对象，函数本身是 ``Function`` 实例。 ``JavaScrip`` 函数的功能非常丰富，如果想深入学习 ``JavaScript`` ，那就必须认真学习 ``JavaScript`` 函数。

函数内部属性
============
在函数内部，有两个特殊的对象： ``arguments`` 和 ``this`` 。其中， ``arguments`` 是一个类数组对象，包含着传入函数中的所有参数。虽然 ``arguments`` 的主要用途是保存函数参数，但这个对象还有一个名叫 ``callee`` 的属性，该属性是一个指针，指向拥有这个 ``arguments`` 对象的函数。

ECMAScript5 也规范了另一个函数对象的属性： ``caller`` 。这个属性中保存着调用当前函数的函数的引用，如果是在全局作用域中调用当前函数，它的值时 ``null`` 。

.. code-block:: js

	function outer() {
		inner();
	}

	function inner() {
		alert(inner.caller);
	}

	outer();

``inner.caller`` 就指向 ``outer()`` 。为了实现更松散的耦合，也可以通过 ``arguments.callee.caller`` 来访问相同的信息。

.. code-block:: js

	function outer() {
		inner();
	}

	function inner() {
		alert(arguments.callee.caller);
	}

	outer();

当函数在严格模式下运行时，访问 ``arguments.callee`` 会导致错误。

函数内部的另一个特殊对象是 ``this`` ，其行为与 Java 和 C# 中的 ``this`` 大致类似。换句话说， ``this`` 引用的是函数据以执行的环境对象。

由于在调用函数之前， ``this`` 的值并不确定，因此 ``this`` 可能会在代码执行过程中引用不同的对象。


函数声明与函数表达式
===================
解析器在向执行环境中加载数据时，对函数声明和函数表达式并非一视同仁。解析器会最先读取函数声明，并使其在执行任何代码之前可用；至于函数表达式，则必须等到解析器执行到它所在的代码行，才会真正被解释执行。

.. code-block:: js

    alert(sum(10, 10));
    function sum(num1, num2) {
    	return num1 + num2;
    }

以上代码完全可以正常运行。因为在代码开始执行之前，解析器就已经通过一个名为函数声明提升的过程，读取并将函数声明添加到执行环境中。对代码求值时，Javascript 引擎在第一遍会声明函数并将它们放到源代码树的顶部。所以，即使声明函数的代码在调用它的代码后面，JavaScript 引擎也能把函数声明提升到顶部。如果改为下面例子，则可能出错：

.. code-block:: js

    alert(sum(10, 10));
    var sum = function(num1, num2) {
    	return num1 + num2;
    };

错误的原因在于函数位于一个初始化语句中，而不是一个函数声明。即，在执行到函数所在的语句之前，变量sum中不会保存有对函数的引用；而且，由于第一行代码就会导致错误。

定义函数的3种方式
================
正如前面所介绍的， ``JavaScript`` 是弱类型语言，因此定义函数时，既不需要声明函数的返回值类型，也不需要声明函数的参数类型。 ``JavaScript`` 目前支持3种函数定义方式。

定义命名函数
-----------
定义命名函数的语法格式如下：

.. code-block:: js

	function functionName(parameter-list){
	  	statements;
	}

下面代码定义了一个简单的函数，并调用函数。

.. code-block:: js

	hello('yeeku');
    // 定义函数hello，该函数需要一个参数
    function hello(name) {
        alert(name + "，你好");
    }

函数的最大作用是提供代码复用，将需要重复使用的代码块定义成函数，提供更好的代码复用。

.. note:: 从上面程序中可以看出，在同一个 ``<script.../>`` 元素中时， ``JavaScript`` 允许先调用函数，然后再定义该函数；但在不同的 ``<script.../>`` 元素中时，必须先定义函数，再调用该函数。也就是说，在后面的 ``<script.../>`` 元素中可以调用前面 ``<script.../>`` 里定义的函数。

函数可以有返回值，也可以没有返回值。函数的返回值使用 ``return`` 语句返回，在函数的运行过程中，一旦遇到第一条 ``return`` 语句，函数就返回返回值，函数运行结束。看下面代码。

.. code-block:: js

 	// 定义函数hello
    function hello(name) {
        // 如果参数类型为字符串，则返回静态字符串
        if (typeof name == 'string') {
            return name + "，你好";
        }
        // 当参数类型不是字符串时，执行此处的返回语句
        return '名字只能为字符串'
    }
    alert(hello('yeeku'));

定义命名函数的语法简单易用，因此对很多 ``JavaScript`` 初级用户来说，这也是见得最多的一种函数定义语法。但实际上采用这种语法定义函数的可读性并不好——因为在定义 ``JavaScript`` 函数时也得到了一个对象。

定义匿名函数
------------
``JavaScript`` 提供了定义匿名函数的方式，这种创建匿名函数的语法格式如下：

.. code-block:: js

	function(parameter list){
	   	statements;
	};

这种函数定义语法无须指定函数名，而是将参数列表紧跟 ``function`` 关键字。在函数定义语法的最后不要忘记紧跟分号( ``;`` )。

当通过这种语法格式定义了函数之后，实际上就是定义了一个函数对象（即 ``Function`` 实例），接下来可以将这个对象赋给另一个变量。例如如下代码。

.. code-block:: js

	var f = function(name) {
        document.writeln('匿名函数<br />');
        document.writeln('你好' + name);
    };
    f('yeeku');

上面代码中的粗体字代码就定义了一个匿名函数，也就是定义了一个 ``Function`` 对象。接下来我们直接将这个函数赋值给另一个变量 ``f`` ，后面就可以通过 ``f`` 来调用这个匿名函数了。

对于这种匿名函数的语法，可读性非常好：程序使用 ``function`` 关键字定义一个函数对象（ ``Function`` 类的实例），然后把这个对象赋值给 ``f`` 变量，以后程序即可通过 ``f`` 来调用这个函数。

如果你是一个有经验的 ``JavaScript`` 开发者，或者阅读过大量优秀的 ``JavaScript`` 源代码（比如 ``Prototype.js`` 、 ``jQuery`` 等），将会在这些 ``JavaScript`` 源代码中看到它们基本都是采用这种方式来定义函数的。使用匿名函数的另一个好处是更加方便，当需要为类、对象定义方法时，使用匿名函数的语法能提供更好的可读性。

``JavaScript`` 的函数非常特殊，它既是可重复调用的“程序块”，也是一个 ``Function`` 实例。

使用Function类匿名函数
----------------------
``JavaScript`` 提供了一个 ``Function`` 类，该类也可以用于定义函数， ``Function`` 类的构造器的参数个数可以不受限制， ``Function`` 可以接受一系列的字符串参数，其中最后一个字符串参数是函数的执行体，执行体的各语句以分号( ``；`` )隔开，而前面的各字符串参数则是函数的参数。

看下面定义函数的方式。

.. code-block:: js

	// 定义匿名函数，并将函数赋给变量f
    var f = new Function('name', "document.writeln('Function定义的函数<br />');"
            + "document.writeln('你好' + name);");
    // 通过变量调用匿名函数
    f('yeeku');

上面代码使用 ``new Function()`` 语法定义了一个匿名函数，并将该匿名函数赋给 ``f`` 变量，从而允许通过 ``f`` 来访问匿名函数。

调用 ``Function`` 类的构造器来创建函数虽然能明确地表示创建了一个 ``Function`` 对象，但由于 ``Function()`` 构造器的最后一个字符串代表函数执行体——当函数执行体的语句很多时， ``Function`` 的最后一个参数将变得十分臃肿，因此这种方式定义函数的语法可读性也不好。


递归函数
========
递归函数是一种特殊的函数，递归函数允许在函数定义中调用函数本身。考虑对于如下计算：

.. code-block:: shell

    n! = n * (n-1) * (n-2) * ..... * 1

注意到等式左边需要求 ``n`` 的阶乘，而等式右边则是求 ``n-1`` 的阶乘。实质都是一个函数，因此可将求阶乘的函数定义如下。

.. code-block:: js

	// 定义求阶乘的函数
    function factorial(n) {
        // 如果n的类型是数值，才执行函数
        if (typeof (n) == "number" && n > 0) {
            // 当n等于1时，直接返回1
            if (n == 1) {
                return 1;
            }
            // 当n不等于1时，通过递归返回值。
            else {
                return n * factorial(n - 1);
            }
        }
        // 当参数不是数值时，直接返回
        else {
            alert("参数类型不对！");
        }
    }
    // 调用阶乘函数
    alert(factorial(5));

上面程序中粗体字代码再次调用了 ``factorial()`` 函数，这就是在函数里调用函数本身，也就是所谓的递归。上面程序执行的结果是120，可以正常求出5的阶乘。注意到程序中判断参数时，先判断参数以是否为数值，而且要求 ``n`` 大于0才会继续运算。事实上，这个函数不仅要求 ``n`` 为数值，而且必须是大于0的整数，否则函数不仅不能得到正确结果，而且将产生内存溢出。

可见，递归的方向很重要，一定要向已知的方向递归。对于上例而言，因为1的阶乘是已知的，因此递归一定要追溯到l的阶乘，递归一定要给定中止条件，这一点与循环类似。没有中止条件的循环是死循环，不向中止点追溯的递归是无穷递归。递归一定要向已知点追溯。

上面存在一个问题，函数名称并不一定和函数对象绑定在一起的，为了消除这种问题，可以定义如下函数：

.. code-block:: js

    function factorial(num) {
    	if (num <=1 ) {
    		return 1;
    	}else {
    		// arguments对象有一个callee属性，该属性是一个指针，指向拥有这个arguments对象的函数
    		return num * arguments.callee(num-1);
    	}
    }

局部变量和局部函数
=================
前面已经介绍了局部变量的概念，在函数里定义的变量称为局部变量，在函数外定义的变量则称为全局变量，如果局部变量和全局变量的变量名相同，则局部变量会覆盖全局变量。局部变量只能在函数里访问，而全局变量可以在所有的函数里访问。

与此类似的概念是局部函数，局部变量在函数里定义，而局部函数也在函数里定义。

下面代码在函数 ``outer`` 中定义了两个局部函数。

.. code-block:: js

	// 定义全局函数
    function outer() {
        // 定义第一个局部函数
        function inner1() {
            document.write("局部函数11111<br />");
        }
        // 定义第二个局部函数
        function inner2() {
            document.write("局部函数22222<br />");
        }
        document.write("开始测试局部函数...<br />");
        // 在函数中调用第一个局部函数
        inner1();
        // 在函数中调用第二个局部函数
        inner2();
        document.write("结束测试局部函数...<br />");
    }
    document.write("调用outer之前...<br />");
    // 调用全局函数
    outer();
    document.write("调用outer之后...<br />");

在上面代码中，在 ``outer`` 函数中定义了两个局部函数： ``innerl`` 和 ``inner2`` ，并在 ``outer`` 函数内调用了这两个局部函数。因为这两个函数是在 ``outer`` 内定义的，因此可以在 ``outer`` 内访问它们。在 ``outer`` 外，则无法访问它们——也就是说， ``innerl`` 、 ``inner2`` 两个函数仅在 ``outer`` 函数内有效。

.. note:: 在外部函数里调用局部函数并不能让局部函数获得执行的机会，只有当外部函数被调用时，外部函数里调用的局部函数才会被执行。

函数、方法、对象和类
=================
函数是 ``JavaScript`` 语言中的“一等公民”，函数是 ``JavaScript`` 编程里非常重要的一个概念。当使用 ``JavaScript`` 定义一个函数后，实际上可以得到如下4项。

- 函数：就像 ``Java`` 的方法一样，这个函数可以被调用。
- 对象：定义一个函数时，系统也会创建一个对象，该对象是 ``Function`` 类的实例。
- 方法：定义一个函数时，该函数通常都会附加给某个对象，作为该对象的方法。
- 类：在定义函数的同时，也得到了一个与函数同名的类。

函数可作为函数被调用，这在前面已经见到过很多例子，此处不再赘述。函数不仅可作为函数使用，函数本身也是一个对象，是 ``Function`` 类的实例。例如如下代码。

.. code-block:: js

	// 定义一个函数，并将它赋给hello变量
    var hello = function(name) {
        return name + "，您好";
    }
    // 判断函数是否为Function的实例、是否为Object的实例
    alert("hello是否为Function对象：" + (hello instanceof Function)
            + "\nhello是否为Object对象：" + (hello instanceof Object));
    alert(hello);

上面程序定义了一个函数，接着程序调用 ``instanceof`` 运算符判断函数是否为 ``Function`` 的实例，是否为 ``Object`` 的实例。

如果直接输出函数本身，例如上面程序中的 ``alert(hello);`` ，将会输出函数的源代码。

``JavaScript`` 的函数不仅是一个函数，更是一个类，在我们定义一个 ``JavaScript`` 函数的同时，也得到了一个与该函数同名的类，该函数也是该类唯一的构造器。

因此，当我们定义一个函数后，有如下两种方式来调用函数。

- 直接调用函数：直接调用函数总是返回该函数体内最后一条 ``return`` 语句的返回值；如果该函数体内不包含 ``return`` 语句，则直接调用函数没有任何返回值。
- 使用 ``new`` 关键字调用函数：通过这种方式调用总有一个返回值，返回值可能是新建的一个 ``JavaScript`` 对象也可能是函数 ``return`` 返回值。

看如下代码。

.. code-block:: js

    // 定义一个函数
    var test = function(name) {
        return "你好，" + name;
    }
    // 直接调用函数
    var rval = test('leegang');
    // 将函数作为类的构造器,返回空对象
    var obj = new test('leegang');
    alert(rval + "\n" + obj);

上面程序中两行粗体字代码示范了两种调用函数的方式，第一种是直接调用该函数，因此得到的返回值是该函数的返回值；第二种是使用 ``new`` 关键字来调用该函数，也就是将该函数当成类使用，所以得到一个空对象。

下面程序定义了一个 ``Person`` 函数，也就是定义了一个 ``Person`` 类，该 ``Person`` 函数也会作为 ``Person`` 类唯一的构造器。定义 ``Person`` 函数时希望为该函数定义一个方法，程序如下。

.. code-block:: js

	// 定义了一个函数，该函数也是一个类
    function Person(name, age) {
        // 将参数name的值赋给name属性
        this.name = name;
        // 将参数age的值赋给age属性
        this.age = age;
        // 为函数分配info方法，使用匿名函数来定义方法
        this.info = function() {
            document.writeln("我的名字是：" + this.name + "<br />");
            document.writeln("我的年纪是：" + this.age + "<br />");
        };
    }
    // 创建p对象
    var p = new Person('yeeku', 29);
    // 执行info方法
    p.info();

上面代码为 ``Person`` 类定义了一个方法，通过使用匿名函数，代码更加简洁。

上面程序中使用了 ``this`` 关键字，被 ``this`` 关键字修饰的变量不再是局部变量，它是该函数的实例属性。关于函数的实例属性参看下一节介绍。

正如从上面代码中看到的， ``JavaScript`` 定义的函数可以“附加”到某个对象上，作为该对象的方法。实际上，如果没有明确指定将函数“附加”到哪个对象上，该函数将“附加”到 ``window`` 对象上，作为 ``window`` 对象的方法。

例如如下代码。

.. code-block:: js

	// 直接定义一个函数，并未指定该函数属于哪个对象。
    // 该对象默认属于window对象
    var hello = function(name) {
        document.write(name + ", 您好<br />");
    }
    // 以window作为调用者，调用hello函数
    window.hello("孙悟空");
    // 定义一个对象
    var p = {
        // 定义一个函数，该函数属于p对象。
        walk ： function() {
            for (var i = 0; i < 2; i++) {
                document.write("慢慢地走...");
            }
        }
    }
    p.walk();

从上面代码可以看出，如果直接定义一个函数，没有指定将该函数“附加”给哪个对象，那么这个函数将会被“附加”给 ``window`` 对象，作为 ``window`` 对象的方法。比如上面代码中第二行粗体字代码，以 ``window`` 作为调用者来调用 ``hello()`` 方法。

函数的实例属性和类属性
====================
由于 ``JavaScript`` 函数不仅仅是一个函数，而且是一个类，该函数还是此类唯一的构造器，只要在调用函数时使用 ``new`` 关键字，就可返回一个 ``Object`` 。因此在 ``JavaScript`` 中定义的变量不仅有局部变量，还有实例属性和类属性两种。根据函数中声明变量的方式，函数中的变量有3种。

- 局部变量：在函数中以普通方式声明的变量，包括以 ``var`` 或不加任何前缀声明的变量。
- 实例属性：在函数中以 ``this`` 前缀修饰的变量。
- 类属性：在函数中以函数名前缀修饰的变量。其实就是定义函数对象的属性。

前面已经对局部变量作了介绍，局部变量是只能在函数里访问的变量。实例属性和类属性则是面向对象的概念：实例属性是属于单个对象的，因此必须通过对象来访问；类属性是属于整个类（也就是函数）本身的，因此必须通过类（也就是函数）来访问。

同一个类（也就是函数）只占用一块内存，因此每个类属性将只占用一块内存；同一个类（也就是函数）每创建一个对象，系统将会为该对象的实例属性分配一块内存。看如下代码。

.. code-block:: js

	// 定义函数Person
    function Person(national, age) {
        // this修饰的变量为实例属性
        this.age = age;
        // Person修饰的变量为类属性
        Person.national = national;
        // 以var定义的变量为局部变量
        var bb = 0;
    }
    // 创建Person的第一个对象p1。国籍为中国，年纪为29
    var p1 = new Person('中国', 29);
    document.writeln("创建第一个Person对象<br />");
    // 输出第一个对象p1的年纪和国籍
    document.writeln("p1的age属性为" + p1.age + "<br />");
    document.writeln("p1的national属性为" + p1.national + "<br />");
    document.writeln("通过Person访问静态national属性为" + Person.national + "<br />");
    // 输出bb属性
    document.writeln("p1的bb属性为" + p1.bb + "<br /><hr />");
    document.writeln("Person的bb属性为" + Person.bb + "<br /><hr />");

    // 输出结果
	创建第一个Person对象
	p1的age属性为29
	p1的national属性为undefined
	通过Person访问静态national属性为中国
	p1的bb属性为undefined
	Person的bb属性为undefined

``Person`` 函数的 ``age`` 属性为实例属性，因而每个实例的 ``age`` 属性都可以完全不同，程序应通过 ``Person`` 对象来访问 ``age`` 属性； ``national`` 属性为类属性，该属性完全属于 ``Person`` 类，因此必须通过 ``Person`` 类来访问 ``national`` 属性， ``Person`` 对象并没有 ``national`` 属性，所以通过 ``Person`` 对象访问该属性将返回 ``undefined`` ;而 ``bb`` 则是 ``Person`` 的局部变量，在 ``Person`` 函数以外无法访问该变量。

调用函数的3种方式
================
定义一个函数之后， ``JavaScript`` 提供了3种调用函数的方式。

直接调用函数
-----------
直接调用函数是最常见、最普通的方式。这种方式直接以函数附加的对象作为调用者，在函数后括号内传入参数来调用函数。这种方式是前面最常见的调用方式。例如如下代码：

.. code-block:: js

	window.alert("测试代码");//调用window对象的alert方法

以call()方法调用函数
--------------------
直接调用函数的方式简单、易用，但这种调用方式不够灵活。有些时候调用函数时需要动态地传入一个函数引用，此时为了动态地调用函数，就需要使用 ``call`` 方法来调用函数了。

假如我们需要定义一个形如 ``each(array，fn)`` 的函数，这个函数可以自动迭代处理 ``array`` 数组元素，而 ``fn`` 函数则负责对数组元素进行处理——此时需要在 ``each`` 函数中调用 ``fn`` 函数，但目前 ``fn`` 函数并未确定，因此无法采用直接调用的方式来调用 ``fn`` 函数，需要通过 ``call()`` 方法来调用函数。

如下代码实现了通过 ``call()`` 方法来调用 ``each()`` 函数。

.. code-block:: js

	// 定义一个each函数
    var each = function(array, fn) {
        for ( var index in array) {
            // 以window为调用者来调用fn函数，
            // index、array[index]是传给fn函数的参数
            fn.call(null, index, array[index]);
        }
    }
    // 调用each函数，第一个参数是数组，第二个参数是函数
    each([ 4, 20, 3 ], function(index, ele) {
        document.write("第" + index + "个元素是：" + ele + "<br />");
    });

上面程序中粗体字代码示范了通过 ``call()`` 动态地调用函数，从调用语法来看，不难发现通过 ``call()`` 调用函数的语法格式为：

.. code-block:: js

    函数引用.call(调用者,参数1,参数2,...);

由此可以得到直接调用函数与通过 ``call()`` 调用函数的关系如下：

.. code-block:: js

    调用者.函数(参数1,参数2,...) = 函数.call(调用者,参数1,参数2,...);

以apply()方法调用函数
--------------------
``apply()`` 方法与 ``call()`` 方法的功能基本相似，它们都可以动态地调用函数。 ``apply()`` 与 ``call()`` 的区别如下：

- 通过call()调用函数时，必须在括号中详细地列出每个参数。
- 通过apply()动态地调用函数时，可以在括号中以arguments或参数数组来代表所有参数。

如下代码示范了call()与apply()的关系。

.. code-block:: js

 	// 定义一个函数
    var myfun = function(a, b) {
        alert("a的值是：" + a + "\nb的值是：" + b);
    }
    // 以call()方法动态地调用函数
    myfun.call(window, 12, 23);
    var example = function(num1, num2) {
        // 直接用arguments代表调用example函数时传入的所有参数
        myfun.apply(this, arguments);
    }
    example(20, 40);
    // 为apply()动态调用传入数组
    myfun.apply(window, [ 12, 23 ]);//1

对比上面两行粗体字代码不难发现，当通过 ``call()`` 动态地调用方法时，需要为被调用方法逐个地传入参数；当通过 ``apply()`` 动态地调用方法时，能以 ``arguments`` 一次性地传入多个参数。

需要指出的是， ``arguments`` 可代表调用当前函数时传入的所有参数，因此 ``arguments`` 相当于一个数组，所以上面程序在 ① 号代码处使用数组代替了 ``arguments`` 。

函数的独立性
============
虽然定义函数时可以将函数定义成某个类的方法，或定义成某个对象的方法。但 ``JavaScript`` 的函数是“一等公民”，它永远是独立的，函数永远不会从属于其他类、对象。

下面代码示范了函数的独立性。

.. code-block:: js

	function Person(name) {
        this.name = name;
        // 定义一个info方法
        this.info = function() {
            alert("我的name是：" + this.name);
        }
    }
    var p = new Person("yeeku");
    // 调用p对象的info方法
    p.info();
    var name = "测试名称";
    // 以window对象作为调用者来调用p对象的info方法
    p.info.call(window);

上面程序为 ``Person`` 类定义了一个 ``info()`` 方法， ``info()`` 方法只有一行代码，这行代码用于输出 ``this.name`` 实例属性值。程序在第一行粗体字代码处直接通过 ``p`` 对象来调用 ``info()`` 方法，此时p对象的 ``name`` 实例属性为 ``"yeeku"`` ，因此程序将会输出 ``"yeeku"`` 。

需要指出的是， ``JavaScript`` 函数永远是独立的。虽然程序的确是在 ``Person`` 类中定义了 ``info()`` 方法，但这个 ``info()`` 方法依然是独立的，程序只要通过 ``p.info()`` 即可引用这个函数。因此程序在第二行粗体字代码处以 ``call()`` 方法来调用 ``p.info()`` 方法，此时 ``window`` 对象是调用者，因此 ``info()`` 方法中的 ``this`` 代表的就是 ``window`` 对象了，访问 ``this.name`` 将返回”测试名称”。

当使用匿名内嵌函数定义某个类的方法时，该内嵌函数一样是独立存在的，该函数也不是完全作为该类实例的附庸存在，这些内嵌函数也可以被分离出来独立使用，包括成为另一个对象的函数。如下代码再次证明了函数的独立性。

.. code-block:: js

	// 定义Dog函数，等同于定义了Dog类
    function Dog(name, age, bark) {
        // 将name、age、bark形参赋值给name、age、bark实例属性
        this.name = name;
        this.age = age;
        this.bark = bark;
        // 使用内嵌函数为Dog实例定义方法
        this.info = function() {
            return this.name + "的年纪为：" + this.age + ",它的叫声：" + this.bark;
        }
    }
    // 创建Dog的实例
    var dog = new Dog("旺财", 3, '汪汪,汪汪...');
    // 创建Cat函数，对应Cat类
    function Cat(name, age) {
        this.name = name;
        this.age = age;
    }
    // 创建Cat实例。
    var cat = new Cat("kitty", 2);
    // 将dog实例的info方法分离出来，再通过call方法完调用info方法，
    // 此时以cat为调用者
    alert(dog.info.call(cat));

上面程序中第一段粗体字代码使用内嵌函数为 ``Dog`` 定义了名为 ``info()`` 的实例方法，但这个 ``info()`` 方法并不完全属于 ``Dog`` 实例，它依然是一个独立函数，所以程序在最后一行粗体字代码处将该函数分离出来，并让 ``Cat`` 实例来调用这个 ``info()`` 方法。

函数的参数处理
=============
大部分时候，函数都需要接受参数传递。与 ``Java`` 完全类似， ``JavaScript`` 的参数传递也全部是采用值传递方式。

在函数体内可以通过 ``arguments`` 对象来访问这个参数数组，从而获取传递给函数的每一个参数。

通过访问 ``arguments`` 对象的 ``length`` 属性可以获知有多少个参数传递给了函数。

基本类型和复合类型的参数传递
--------------------------
对于基本类型参数， ``JavaScript`` 采用值传递方式，当通过实参调用函数时，传入函数里的并不是实参本身，而是实参的副本，因此在函数中修改参数值并不会对实参有任何影响。看下面程序。

.. code-block:: js

	// 定义一个函数，该函数接受一个参数
    function change(arg1) {
        //对参数值赋值，对实参不会有任何影响
        arg1 = 10;
        document.write("函数执行中arg1的值为：" + arg1 + "<br/>");
    }
    // 定义变量x的值为5
    var x = 5;
    // 输出函数调用之前x的值
    document.write("函数调用之前x的值为：" + x + "<br />");
    change(x);
    document.write("函数调用之后x的值为：" + x + "<br />");

当使用 ``x`` 变量作为参数调用 ``change()`` 函数时， ``x`` 并未真正传入 ``change()`` 函数中，传入的仅仅是 ``x`` 的副本，因此在 ``change()`` 中对参数赋值不会影响 ``x`` 的值。

从结果看到，在函数调用之前， ``x`` 的值为 ``5`` ；在函数调用之后， ``x`` 的值依然为 ``5`` 。虽然在函数体内修改了 ``x`` 的值，但实际上 ``x`` 的值根本没有改变。这是因为 ``JavaScript`` 基本类型的参数传递采用值传递方式，实际传入函数的只是 ``x`` 的副本，所以 ``x`` 本身是不会有任何改变的。

但对于复合类型的参数，实际上采用的依然是值传递方式，只是很容易混淆，看如下程序。

.. code-block:: js

	// 定义函数，该函数接受一个参数
    function changeAge(person) {
        // 改变person的age属性
        person.age = 10;
        // 输出person的age属性
        document.write("函数执行中person的age值为：" + person.age + "<br />");
        // 将person变量直接赋为null
        person = null;
    }
    // 使用JSON语法定义person对象
    var person = {
        age ： 5
    };
    // 输出person的age属性
    document.write("函数调用之前person的age的值为：" + person.age + "<br />");
    // 调用函数
    changeAge(person);
    // 输出函数调用后person实例的age属性值
    document.write("函数调用之后person的age的值为：" + person.age + "<br />");
    document.write("person对象为：" + person);

	// 结果
	函数调用之前person的age的值为：5
	函数执行中person的age值为：10
	函数调用之后person的age的值为：10
	person对象为：[object Object]

上面代码中使用了 ``JSON`` 语法创建 ``person`` 对象。在上面程序中，传入 ``changeAge()`` 函数中的不再是基本类型变量，而是一个复合类型变量。

如果仅从 ``person`` 对象的 ``age`` 属性值被改变来看，很多资料、书籍非常容易得到一个结论：复合类型的参数采用了引用传递方式，不再是采用值传递方式。

我们看到 ``changeAge()`` 函数中最后一条粗体字代码，将 ``person`` 对象直接赋值为 ``null`` ，但 ``changeAge()`` 函数执行结束后，后面的 ``person`` 对象依然是一个对象，并不是 ``null`` ，这表明 ``person`` 本身并未传入 ``changeAge()`` 函数中，传入 ``changeAge()`` 函数的依然是副本。

上面程序的关键是，复合类型的变量本身并未持有对象本身，复合类型的变量只是一个引用（类似于 ``Java`` 的引用变量），该引用指向实际的 ``JavaScript`` 对象。当把 ``person`` 复合类型的变量传入 ``changeAge()`` 函数时，传入的依然是 ``person`` 变量的副本——只是该副本和原 ``person`` 变量指向同一个 ``JavaScript`` 对象。因此不管是修改该副本所引用的 ``JavaScript`` 对象，还是修改 person 变量所引用的 ``JavaScript`` 对象，实际上修改的是同一个对象。 ``JavaScript`` 的复合类型包括对象、数组等。

空参数
------
看如下程序代码。

.. code-block:: js

	function changeAge(person) {
        if (typeof person == 'object') {
            // 改变参数的age属性
            person.age = 10;
            // 输出参数的age属性
            document.write("函数执行中person的Age值为：" + person.age + "<br />");
        } else {
            alert("参数类型不符合：" + typeof person);
        }
    }
    changeAge();

上面代码的函数声明中包含了一个参数，但调用函数时并没有传入任何参数。这种形式对于强类型语言，如 ``Java`` 或 ``C`` 都是不允许的；但对于 ``JavaScript`` 却没有任何语法问题，因为 ``JavaScript`` 会将没有传入实参的参数值自动设置为 ``undefrned`` 值。

使用空参数完全没有任何程序问题，程序可以正常执行，只是没有传入实参的参数值将作为 ``undefined`` 处理。

由于 ``JavaScript`` 调用函数时对传入的实参并没有要求，即使定义函数时声明了多个形参，调用函数时也并不强制要求传入相匹配的实参。因此 ``JavaScript`` 没有所谓的函数“重载”，对于 ``JavaScript`` 来说，函数名就是函数的唯一标识。

如果先后定义两个同名的函数，它们的形参列表并不相同，这也不是函数重载，这种方式会导致后面定义的函数覆盖前面定义的函数。例如如下代码。

.. code-block:: js

	function test() {
        alert("第一个无参数的test函数");
    }
    // 后面定义的函数将会覆盖前面定义的函数
    function test(name) {
        alert("第二个带name参数的test函数：" + name);
    }
    // 即使不传入参数，程序依然调用带一个参数的test函数。
    test();

上面程序中定义了两个名为 ``test()`` 的函数，虽然两个 ``test()`` 函数声明的形参个数不同，但第二个 ``test()`` 函数会覆盖第一个 ``test()`` 函数。因此程序中粗体字代码调用 ``test()`` 函数时。无论是否传入参数，程序始终都是调用第二个 ``test()`` 函数。

参数类型
--------
``JavaScript`` 函数声明的参数列表无须类型声明，这是它作为弱类型语言的一个特征。但 ``JavaScript`` 语言又是基于对象的编程语言，这一点往往非常矛盾。例如，对于如下的 ``Java`` 方法定义：

.. code-block:: js

	public void changeAge(Person p){
	    p.setAge(34);
	}

这个程序没有任何问题，因为 ``Java`` 要求参数列表具有类型声明，因而参数 ``p`` 属于 ``Person`` 实例，而 ``Person`` 实例具有 ``setAge()`` 方法。如果 ``Person`` 类没有 ``setAge()`` 方法，程序将在编译时出现错误。调用该方法时，如果没有传入参数，或者传入参数的类型不是 ``Person`` 对象，都将在编译时出现错误。

将上面程序简单转换成 ``JavaScript`` 写法，即变成如下形式：

.. code-block:: js

	function changeAge(p){
	    p.setAge(34);
	}

值得注意的是， ``JavaScript`` 无须类型声明，因此调用函数时，传入的 ``p`` 完全可以是整型变量，或者是布尔型变量，这些类型的数据都没有 ``setAge()`` 方法，但程序强制调用该方法，肯定导致程序出现错误，程序非正常中止。

``JavaScript`` 函数定义的参数列表无须类型声明，这一点为函数调用埋下了隐患，这也是 ``JavaScript`` 语言程序不如 ``Java`` 、 ``C`` 语言程序健壮的一个重要原因。

实际上这个问题并不是 ``JavaScript`` 所独有的，而是所有弱类型语言所共同的问题。由于声明函数时形参无须定义数据类型，所以导致调用这些函数时可能出现问题。

为了解决弱类型语言所存在的问题，弱类型语言方面的专家提出了“鸭子类型( Duck Type)”的概念，他们认为：当你需要一个“鸭子类型”的参数时，由于编程语言本身是弱类型的，所以无法保证传入的参数一定是“鸭子类型”，这时你可以先判断这个对象是否能发出“嘎嘎”声，并具有走路左右摇摆的特征，也就是具有“鸭子类型”的特征——一旦该参数具有“鸭子类型”的特征，即使它不是“鸭子”，程序也可以将它当成“鸭子”使用。

简单地说，“鸭子类型”的理论认为：如果弱类型语言的函数需要接受参数，则应先判断参数类型，并判断参数是否包含了需要访问的属性、方法。只有当这些条件都满足时，程序才开始真正处理调用参数的属性、方法。看如下代码。

.. code-block:: js

	// 定义函数changeAge,函数需要一个参数
    function changeAge(person) {
        // 首先要求person必须是对象，而且person的age属性为number
        if (typeof person == 'object' && typeof person.age == 'number') {
            //执行函数所需的逻辑操作
            document.write("函数执行前person的Age值为：" + person.age + "<br />");
            person.age = 10;
            document.write("函数执行中person的Age值为：" + person.age + "<br />");
        }
        // 否则将输出提示，参数类型不符合
        else {
            document.writeln("参数类型不符合" + typeof person + "<br />");
        }
    }
    // 分别采用不同方式调用函数
    changeAge();
    changeAge('xxx');
    changeAge(true);
    // 采用JSON语法创建第一个对象
    p = {
        abc ： 34
    };
    changeAge(p);
    // 采用JSON语法创建第二个对象
    person = {
        age ： 25
    };
    changeAge(person);

这种语法要求：函数对参数执行逻辑操作之前，首先判断参数的数据类型，并检查参数的属性是否符合要求，当所有的要求满足后才执行逻辑操作；否则弹出警告。