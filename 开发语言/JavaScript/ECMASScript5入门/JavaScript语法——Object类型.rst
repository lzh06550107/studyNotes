********
Object类型
********

Object对象方法
=============

``JavaScript`` 在 ``Object`` 对象上面，提供了很多相关方法，处理面向对象编程的相关操作。本章介绍这些方法。

Object.getPrototypeOf()
-----------------------
``Object.getPrototypeOf`` 方法返回参数对象的原型。这是获取原型对象的标准方法。

.. code-block:: js

    var F = function () {};
    var f = new F();
    Object.getPrototypeOf(f) === F.prototype // true

上面代码中，实例对象 ``f`` 的原型是 ``F.prototype`` 。

下面是几种特殊对象的原型。

.. code-block:: js

    // 空对象的原型是 Object.prototype
    Object.getPrototypeOf({}) === Object.prototype // true

    // Object.prototype 的原型是 null
    Object.getPrototypeOf(Object.prototype) === null // true

    // 函数的原型是 Function.prototype
    function f() {}
    Object.getPrototypeOf(f) === Function.prototype // true


Object.setPrototypeOf()
------------------------
``Object.setPrototypeOf`` 方法为参数对象设置原型，返回该参数对象。它接受两个参数，第一个是现有对象，第二个是原型对象。

.. code-block:: js

    var a = {};
    var b = {x: 1};
    Object.setPrototypeOf(a, b);

    Object.getPrototypeOf(a) === b // true
    a.x // 1

上面代码中， ``Object.setPrototypeOf`` 方法将对象a的原型，设置为对象 ``b`` ，因此 ``a`` 可以共享 ``b`` 的属性。

``new`` 命令可以使用 ``Object.setPrototypeOf`` 方法模拟。

.. code-block:: js

    var F = function () {
      this.foo = 'bar';
    };

    var f = new F();
    // 等同于
    var f = Object.setPrototypeOf({}, F.prototype);
    F.call(f);

上面代码中， ``new`` 命令新建实例对象，其实可以分成两步。第一步，将一个空对象的原型设为构造函数的 ``prototype`` 属性（上例是 ``F.prototype`` ）；第二步，将构造函数内部的 ``this`` 绑定这个空对象，然后执行构造函数，使得定义在 ``this`` 上面的方法和属性（上例是 ``this.foo`` ），都转移到这个空对象上。


Object.create()
---------------
生成实例对象的常用方法是，使用 ``new`` 命令让构造函数返回一个实例。但是很多时候，只能拿到一个实例对象，它可能根本不是由构建函数生成的，那么能不能从一个实例对象，生成另一个实例对象呢？

``JavaScript`` 提供了 ``Object.create`` 方法，用来满足这种需求。该方法接受一个对象作为参数，然后以它为原型，返回一个实例对象。该实例完全继承原型对象的属性。

.. code-block:: js

    // 原型对象
    var A = {
      print: function () {
        console.log('hello');
      }
    };

    // 实例对象
    var B = Object.create(A);

    Object.getPrototypeOf(B) === A // true
    B.print() // hello
    B.print === A.print // true

上面代码中， ``Object.create`` 方法以 ``A`` 对象为原型，生成了 ``B`` 对象。 ``B`` 继承了 ``A`` 的所有属性和方法。

实际上， ``Object.create`` 方法可以用下面的代码代替。

.. code-block:: js

    if (typeof Object.create !== 'function') {
      Object.create = function (obj) {
        function F() {}
        F.prototype = obj;
        return new F();
      };
    }

上面代码表明， ``Object.create`` 方法的实质是新建一个空的构造函数 ``F`` ，然后让 ``F.prototype`` 属性指向参数对象 ``obj`` ，最后返回一个 ``F`` 的实例，从而实现让该实例继承 ``obj`` 的属性。

下面三种方式生成的新对象是等价的。

.. code-block:: js

    var obj1 = Object.create({});
    var obj2 = Object.create(Object.prototype);
    var obj3 = new Object();

如果想要生成一个不继承任何属性（比如没有 ``toString`` 和 ``valueOf`` 方法）的对象，可以将 ``Object.create`` 的参数设为 ``null`` 。

.. code-block:: js

    var obj = Object.create(null);

    obj.valueOf() // TypeError: Object [object Object] has no method 'valueOf'

上面代码中，对象 ``obj`` 的原型是 ``null`` ，它就不具备一些定义在 ``Object.prototype`` 对象上面的属性，比如 ``valueOf`` 方法。

使用 ``Object.create`` 方法的时候，必须提供对象原型，即参数不能为空，或者不是对象，否则会报错。

.. code-block:: js

    Object.create()
    // TypeError: Object prototype may only be an Object or null
    Object.create(123)
    // TypeError: Object prototype may only be an Object or null

``Object.create`` 方法生成的新对象，动态继承了原型。在原型上添加或修改任何方法，会立刻反映在新对象之上。

.. code-block:: js

    var obj1 = { p: 1 };
    var obj2 = Object.create(obj1);

    obj1.p = 2;
    obj2.p // 2

上面代码中，修改对象原型 ``obj1`` 会影响到实例对象 ``obj2`` 。

除了对象的原型， ``Object.create`` 方法还可以接受第二个参数。该参数是一个属性描述对象，它所描述的对象属性，会添加到实例对象，作为该对象自身的属性。

.. code-block:: js

    var obj = Object.create({}, {
      p1: {
        value: 123,
        enumerable: true,
        configurable: true,
        writable: true,
      },
      p2: {
        value: 'abc',
        enumerable: true,
        configurable: true,
        writable: true,
      }
    });

    // 等同于
    var obj = Object.create({});
    obj.p1 = 123;
    obj.p2 = 'abc';

``Object.create`` 方法生成的对象，继承了它的原型对象的构造函数。

.. code-block:: js

    function A() {}
    var a = new A();
    var b = Object.create(a);

    b.constructor === A // true
    b instanceof A // true

上面代码中， ``b`` 对象的原型是 ``a`` 对象，因此继承了 ``a`` 对象的构造函数 ``A`` 。

Object.prototype.isPrototypeOf()
--------------------------------
实例对象的 ``isPrototypeOf`` 方法，用来判断该对象是否为参数对象的原型。

.. code-block:: js

    var o1 = {};
    var o2 = Object.create(o1);
    var o3 = Object.create(o2);

    o2.isPrototypeOf(o3) // true
    o1.isPrototypeOf(o3) // true

上面代码中， ``o1`` 和 ``o2`` 都是 ``o3`` 的原型。这表明只要实例对象处在参数对象的原型链上， ``isPrototypeOf`` 方法都返回 ``true`` 。

.. code-block:: js

    Object.prototype.isPrototypeOf({}) // true
    Object.prototype.isPrototypeOf([]) // true
    Object.prototype.isPrototypeOf(/xyz/) // true
    Object.prototype.isPrototypeOf(Object.create(null)) // false

上面代码中，由于 ``Object.prototype`` 处于原型链的最顶端，所以对各种实例都返回 ``true`` ，只有直接继承自 ``null`` 的对象除外。

Object.prototype.__proto__
---------------------------
实例对象的 ``__proto__`` 属性（前后各两个下划线），返回该对象的原型。该属性可读写。

.. code-block:: js

    var obj = {};
    var p = {};

    obj.__proto__ = p;
    Object.getPrototypeOf(obj) === p // true

上面代码通过 ``__proto__`` 属性，将 ``p`` 对象设为 ``obj`` 对象的原型。

根据语言标准， ``__proto__`` 属性只有浏览器才需要部署，其他环境可以没有这个属性。它前后的两根下划线，表明它本质是一个内部属性，不应该对使用者暴露。因此，应该尽量少用这个属性，而是用 ``Object.getPrototypeof()`` 和 ``Object.setPrototypeOf()`` ，进行原型对象的读写操作。

原型链可以用 ``__proto__`` 很直观地表示。

.. code-block:: js

    var A = {
      name: '张三'
    };
    var B = {
      name: '李四'
    };

    var proto = {
      print: function () {
        console.log(this.name);
      }
    };

    A.__proto__ = proto;
    B.__proto__ = proto;

    A.print() // 张三
    B.print() // 李四

    A.print === B.print // true
    A.print === proto.print // true
    B.print === proto.print // true

上面代码中， ``A`` 对象和 ``B`` 对象的原型都是 ``proto`` 对象，它们都共享 ``proto`` 对象的 ``print`` 方法。也就是说， ``A`` 和 ``B`` 的 ``print`` 方法，都是在调用 ``proto`` 对象的 ``print`` 方法。


获取原型对象方法的比较
--------------------
如前所述， ``__proto__`` 属性指向当前对象的原型对象，即构造函数的 ``prototype`` 属性。

.. code-block:: js

    var obj = new Object();

    obj.__proto__ === Object.prototype
    // true
    obj.__proto__ === obj.constructor.prototype
    // true

上面代码首先新建了一个对象 ``obj`` ，它的 ``__proto__`` 属性，指向构造函数（ ``Object`` 或 ``obj.constructor`` ）的 ``prototype`` 属性。

因此，获取实例对象 ``obj`` 的原型对象，有三种方法。

- obj.__proto__
- obj.constructor.prototype
- Object.getPrototypeOf(obj)

上面三种方法之中，前两种都不是很可靠。 ``__proto__`` 属性只有浏览器才需要部署，其他环境可以不部署。而 ``obj.constructor.prototype`` 在手动改变原型对象时，可能会失效。

.. code-block:: js

    var P = function () {};
    var p = new P();

    var C = function () {};
    C.prototype = p;
    var c = new C();

    c.constructor.prototype === p // false

上面代码中，构造函数C的原型对象被改成了 ``p`` ，但是实例对象的 ``c.constructor.prototype`` 却没有指向 ``p`` 。所以，在改变原型对象时，一般要同时设置 ``constructor`` 属性。

.. code-block:: js

    C.prototype = p;
    C.prototype.constructor = C;

    var c = new C();
    c.constructor.prototype === p // true

因此，推荐使用第三种 ``Object.getPrototypeOf`` 方法，获取原型对象。

Object.getOwnPropertyNames()
-----------------------------
``Object.getOwnPropertyNames`` 方法返回一个数组，成员是参数对象本身的所有属性的键名，不包含继承的属性键名。

.. code-block:: js

    Object.getOwnPropertyNames(Date) // ["parse", "arguments", "UTC", "caller", "name", "prototype", "now", "length"]

上面代码中， ``Object.getOwnPropertyNames`` 方法返回 ``Date`` 所有自身的属性名。

对象本身的属性之中，有的是可以遍历的（enumerable），有的是不可以遍历的。 ``Object.getOwnPropertyNames`` 方法返回所有键名，不管是否可以遍历。只获取那些可以遍历的属性，使用 ``Object.keys`` 方法。

.. code-block:: js

    Object.keys(Date) // []

上面代码表明， ``Date`` 对象所有自身的属性，都是不可以遍历的。

Object.prototype.hasOwnProperty()
---------------------------------
对象实例的 ``hasOwnProperty`` 方法返回一个布尔值，用于判断某个属性定义在对象自身，还是定义在原型链上。

.. code-block:: js

    Date.hasOwnProperty('length') // true
    Date.hasOwnProperty('toString') // false

上面代码表明， ``Date.length`` （构造函数 ``Date`` 可以接受多少个参数）是 ``Date`` 自身的属性， ``Date.toString`` 是继承的属性。

另外， ``hasOwnProperty`` 方法是 ``JavaScript`` 之中唯一一个处理对象属性时，不会遍历原型链的方法。


in 运算符和 for…in 循环
----------------------
``in`` 运算符返回一个布尔值，表示一个对象是否具有某个属性。它不区分该属性是对象自身的属性，还是继承的属性。

.. code-block:: js

    'length' in Date // true
    'toString' in Date // true

``in`` 运算符常用于检查一个属性是否存在。

获得对象的所有可遍历属性（不管是自身的还是继承的），可以使用 ``for...in`` 循环。

.. code-block:: js

    var o1 = { p1: 123 };

    var o2 = Object.create(o1, {
      p2: { value: "abc", enumerable: true }
    });

    for (p in o2) {
      console.info(p);
    }
    // p2
    // p1

上面对象中，对象 ``o2`` 的 ``p2`` 属性是自身的，p1属性是继承的。这两个属性都会被 ``for...in`` 循环遍历。

为了在 ``for...in`` 循环中获得对象自身的属性，可以采用 ``hasOwnProperty`` 方法判断一下。

.. code-block:: js

    for ( var name in object ) {
      if ( object.hasOwnProperty(name) ) {
        /* loop code */
      }
    }

获得对象的所有属性（不管是自身的还是继承的，也不管是否可枚举），可以使用下面的函数。

.. code-block:: js

    function inheritedPropertyNames(obj) {
      var props = {};
      while(obj) {
        Object.getOwnPropertyNames(obj).forEach(function(p) {
          props[p] = true;
        });
        obj = Object.getPrototypeOf(obj);
      }
      return Object.getOwnPropertyNames(props);
    }

上面代码依次获取 ``obj`` 对象的每一级原型对象“自身”的属性，从而获取 ``obj`` 对象的“所有”属性，不管是否可遍历。

下面是一个例子，列出 ``Date`` 对象的所有属性。

.. code-block:: js

    inheritedPropertyNames(Date)
    // [
    //  "caller",
    //  "constructor",
    //  "toString",
    //  "UTC",
    //  ...
    // ]


对象的拷贝
---------
如果要拷贝一个对象，需要做到下面两件事情。

- 确保拷贝后的对象，与原对象具有同样的原型。
- 确保拷贝后的对象，与原对象具有同样的实例属性。

下面就是根据上面两点，实现的对象拷贝函数。

.. code-block:: js

    function copyObject(orig) {
      var copy = Object.create(Object.getPrototypeOf(orig));
      copyOwnPropertiesFrom(copy, orig);
      return copy;
    }

    function copyOwnPropertiesFrom(target, source) {
      Object
        .getOwnPropertyNames(source)
        .forEach(function (propKey) {
          var desc = Object.getOwnPropertyDescriptor(source, propKey);
          Object.defineProperty(target, propKey, desc);
        });
      return target;
    }

另一种更简单的写法，是利用 ES2017 才引入标准的 ``Object.getOwnPropertyDescriptors`` 方法。

.. code-block:: js

    function copyObject(orig) {
      return Object.create(
        Object.getPrototypeOf(orig),
        Object.getOwnPropertyDescriptors(orig)
      );
    }


面向对象的概念
=============

``JavaScript`` 并不严格地要求使用对象，甚至可以不使用函数，将代码堆积成简单的顺序代码流。但随着代码的增加，为了提供更好的软件复用，建议使用对象和函数。

``JavaScript`` 并不是面向对象的程序设计语言，面向对象设计的基本特征：继承、多态等没有得到很好的实现。在纯粹的面向对象语言里，最基本的程序单位是类，类与类之间提供严格的继承关系。比如 ``Java`` 中的类，所有的类都可以通过 ``extends`` 显式继承父类，或者默认继承系统的 ``Object`` 类。而 ``JavaScript`` 并没有提供规范的语法让开发者定义类。

在纯粹的面向对象程序设计语言里，严格使用 ``new`` 关键字创建对象，而 ``new`` 关键字调用该类的构造器，通过这种方式可以返回该类的实例。例如，在 ``Java`` 中可以通过如下代码创建 ``Person`` 实例：

.. code-block:: js

    Person p = new Person();

假设 ``Person`` 类已有了 ``Person`` 的构造器，通过构造器即可返回 ``Person`` 实例。但 ``JavaScript`` 则没有这样严格的语法， ``JavaScript`` 中的每个函数都可用于创建对象，返回的对象既是该类的实例，也是 ``Object`` 类的实例。看如下代码。

.. code-block:: js

	// 定义简单函数
    function Person(name) {
        this.name = name;
    }
    // 使用new关键字，简单创建Person类的实例
    var p = new Person('yeeku');
    // 如果p是Person实例，则输出静态文本
    if (p instanceof Person)
        document.writeln("p是Person的实例<br />");
    // 如果p是Object 实例，则输出静态文本
    if (p instanceof Object)
        document.writeln("p是Object的实例");

上面的 ``JavaScript`` 在定义 ``Person`` 函数的同时，也得到了一个 ``Person`` 类，因此程序通过 ``Person`` 创建的对象既是 ``Person`` 类的实例，也是 ``Object`` 类的实例。

由于 ``JavaScript`` 的函数定义不支持继承语法，因此 ``JavaScript`` 没有完善的继承机制。所以我们习惯上称 ``JavaScript`` 是基于对象的脚本语言。

``JavaScript`` 不允许开发者指定类与类之间的继承关系， ``JavaScript`` 并没有提供完善的继承语法，因此开发者定义的类没有父子关系，但这些类都是 ``Object`` 类的子类。

对象和关联数组
=============
``JavaScript`` 对象与纯粹的面向对象语言的对象存在一定的区别： ``JavaScript`` 中的对象本质上是一个关联数组，或者说更像 ``Java`` 里的 ``Map`` 数据结构，由一组 ``key-value`` 对组成。与 ``Java`` 中 ``Map`` 对象存在区别的是， ``JavaScript`` 对象的 ``value`` ，不仅可以是值（包括基本类型的值和复合类型的值），也可以是函数，此时的函数就是该对象的方法。当 ``value`` 是基本类型的值或者复合类型的值时，此时的 ``value`` 就是该对象的属性值。

因此，当需要访问某个 ``JavaScript`` 对象的属性时，不仅可以使用 ``obj.propName`` 的形式，也可以采用 ``obj[propName]`` 的形式，有些时候甚至必须使用这种形式。例如下面代码。

.. code-block:: js

	function Person(name, age) {
        // 将name、age形参的值分别赋给name、age实例属性。
        this.name = name;
        this.age = age;
        this.info = function() {
            alert('info method!');
        }
    }
    var p = new Person('yeeku', 30);
    for (propName in p) {
        // 遍历Person对象的属性
        document.writeln('p对象的' + propName + "属性值为：" + p[propName] + "<br />");
    }

上面程序中粗体字代码遍历了 ``Person`` 对象的每个属性，因为遍历每个属性时循环计数器是 ``Person`` 对象的属性名，因此程序必须根据属性名来访问 ``Person`` 对象的属性，此时不能采用 ``p.propName`` 的形式，如果采用 ``p.propName`` 的形式， ``JavaScript`` 不会把 ``propName`` 当成变量处理，它试图直接访问该对象的 ``propName`` 属性——但该属性实际并不存在。

``JavaScript`` 的函数没有提供显式的继承语法，因而 ``JavaScript`` 中的对象全部是 ``Object`` 的子类。这在前面已经介绍过了，因而各对象之间完全平等，各对象之间并不存在直接的父子关系。 ``JavaScript`` 提供了一些内建类，通过这些内建类可以方便地创建各自的对象。

继承和prototype
===============
在面向对象的程序设计语言里，类与类之间有显式的继承关系，一个类可以显式地指定继承自哪个类，子类将具有父类的所有属性和方法。 ``JavaScript`` 虽然也支持类、对象的概念，但没有继承的概念，只能通过一种特殊的手段来扩展原有的 ``JavaScript`` 类。

事实上，每个 ``JavaScript`` 对象都是相同基类（ ``Object`` 类）的实例，因此所有的 ``JavaScript`` 对象之间并没有明显的继承关系。而且 ``JavaScript`` 是一种动态语言，它允许自由地为对象增加属性和方法，当程序为对象的某个不存在的属性赋值时，即可认为是为该对象增加属性。

.. code-block:: js

	//定义一个对象，该对象没有任何属性和方法
	var p = {};
	//为p对象增加age属性
	p.age = 30;
	//为p对象增加info属性，该属性值是函数，也就是增加了info方法
	p.info = function(){
	    alert("info method!");
	}

前面已经介绍过，定义 ``JavaScript`` 函数时，也就得到了一个同名的类，而且该函数就是该类的构造器。因此，我们认为定义一个函数的同时，实质上也是定义了一个构造器。

当定义函数时，函数中以 ``this`` 修饰的变量是实例属性，如果某个属性值是函数时，即可认为该属性变成了方法。例如如下代码。

.. code-block:: js

	// 创建Person函数
    function Person(name, age) {
        this.name = name;
        this.age = age;
        // 为Person对象指定info方法
        this.info = function() {//每次创建新的对象
            //输 出Person实例的name和age属性
            document.writeln("姓名：" + this.name);
            document.writeln("年龄：" + this.age);
        }
    }
    // 创建Person实例p1
    var p1 = new Person('yeeku', 29);
    // 执行p1的info方法
    p1.info();
    document.writeln("<hr />");
    // 创建Person实例p2
    var p2 = new Person('wawa', 20);
    // 执行p2的info方法
    p2.info();

代码中在定义 ``Person`` 函数的同时，也定义了一个 ``Person`` 类，而且该 ``Person`` 函数就是该 ``Person`` 类的构造器，该构造器不仅为 ``Person`` 实例完成了属性的初始化，还为 ``Person`` 实例提供了一个 ``info`` 方法。

但使用上面方法为 ``Person`` 类定义增加 ``info`` 方法相当不好，主要有如下两个原因。

- 性能低下：因为每次创建 ``Person`` 实例时，程序依次向下执行，每次执行程序中粗体字代码时都将创建一个新的 ``info`` 函数——当创建多个 ``Person`` 对象时，系统就会有很多个 ``info`` 函数——这就会造成系统内存泄漏，从而引起性能下降。实际上， ``info`` 函数只需要一个就够了。
- 使得 ``info`` 函数中的局部变量产生闭包：闭包会扩大局部变量的作用域，使得局部变量一直存活到函数之外的地方。看如下代码。

.. code-block:: js

 	// 创建Person函数
    function Person() {
        // locVal是个局部变量，原本应该该函数结束后立即失效
        var locVal = '疯狂Java联盟';
        this.info = function() {
            // 此处会形成闭包
            document.writeln("locVal的值为：" + locVal);
            return locVal;
        }
    }
    var p = new Person();
    // 调用p对象的info()方法
    var val = p.info();
    // 输出val返回值，该返回值就是局部变量locVal。
    alert(val);

从上面代码中可以看出，由于在 ``info`` 函数里访问了局部变量 ``locVal`` ，所以形成了闭包，从而导致 ``locVal`` 变量的作用域被扩大，在最后一行粗体字代码处可以看到，即使离开了 ``info`` 函数，程序依然可以访问到局部变量的值。

为了避免这两种情况，通常不建议直接在函数定义（也就是类定义）中直接为该函数定义方法，而是建议使用 ``prototype`` 属性。

``JavaScript`` 的所有类（也就是函数）都有一个 ``prototype`` 属性，当我们为 ``JavaScript`` 类的 ``prototype`` 属性增加函数、属性时，则可视为对原有类的扩展。我们可理解为：增加了 ``prototype`` 属性的类继承了原有的类——这就是 ``JavaScript`` 所提供的伪继承机制。看如下程序。

.. code-block:: js

	// 定义一个Person函数，同时也定义了Person类
    function Person(name, age) {
        // 将局部变量name、age赋值给实例属性name、age
        this.name = name;
        this.age = age;
        // 使用内嵌的函数定义了Person类的方法
        this.info = function() {
            document.writeln("姓名：" + this.name + "<br />");
            document.writeln("年龄：" + this.age + "<br />");
        }
    }
    // 创建Person的实例p1
    var p1 = new Person('yeeku', 29);
    // 执行Person的info方法
    p1.info();
    // 此处不可调用walk方法，变量p还没有walk方法
    // 将walk方法增加到Person的prototype属性上
    Person.prototype.walk = function() {
        document.writeln(this.name + '正在慢慢溜达...<br />');
    }
    document.writeln('<hr />');
    // 创建Person的实例p2
    var p2 = new Person('leegang', 30);
    // 执行p2的info方法
    p2.info();
    document.writeln('<hr />');
    // 执行p2的walk方法
    p2.walk();
    // 此时p1也具有了walk方法——JavaScript允许为类动态增加方法和属性
    // 执行p1的walk方法
    p1.walk();

上面程序中粗体字代码为 ``Person`` 类的 ``prototype`` 属性增加了 ``walk`` 函数，即可认为程序为 ``Person`` 类动态地增加了 ``walk`` 实例方法—一实际上， ``JavaScript`` 是一门动态语言，它不仅可以为对象动态地增加属性和方法，也可以动态地为类增加属性和方法。

在为 ``Person`` 类增加 ``walk`` 实例方法之前， ``pl`` 对象不能调用 ``walk`` 方法；当为 ``Person`` 类增加了 ``walk`` 实例方法之后，新创建的 ``p2`` 对象以及前面创建的 ``pl`` 对象都拥有了 ``walk`` 方法，所以可调用 ``walk`` 方法。

上面程序采用 ``prototype`` 为 ``Person`` 类增加了一个 ``walk`` 方法，这样会让所有的 ``Person`` 实例共享一个 ``walk`` 方法，而且该 ``walk`` 方法不在 ``Person`` 函数之内，因此不会产生闭包。

与 ``Java`` 等真正面向对象的继承不同，虽然使用 ``prototype`` 属性可以为一个类动态地增加属性和方法，这可被当成一种“伪继承”；但这种“伪继承”的实质是修改了原有的类，并不是产生了一个新的子类，这一点尤其需要注意。因此原有的那个没有 ``walk`` 方法的 ``Person`` 类将不再存在！

``JavaScript`` 并没有提供真正的继承，当通过某个类的 ``prototype`` 属性动态地增加属性或方法时，其实质是对原有类的修改，并不是真正产生一个新的子类，所以这种机制依然只是一种伪继承。

通过使用 ``prototype`` 属性，可以对 ``JavaScript`` 的内建类进行扩展。下面的代码为 ``JavaScript`` 内建类 ``Array`` 增加了 ``indexof`` 方法，该方法用于判断数组中是否包含了某元素。

.. code-block:: js

	// 为Array增加indexof方法，将该函数增加到prototype属性上
    Array.prototype.indexof = function(obj) {
        // 定义result的值为-1
        var result = -1;
        // 遍历数组的每个元素
        for (var i = 0; i < this.length; i++) {
            // 当数组的第i个元素值等于obj时
            if (this[i] == obj) {
                // 将result的值赋为i，并结束循环。
                result = i;
                break;
            }
        }
        // 返回元素所在的位置。
        return result;
    }
    var arr = [ 4, 5, 7, -2 ];
    // 测试为arr新增的indexof方法
    alert(arr.indexof(-2));

上面程序中第一段粗体字代码为 ``Array`` 类动态地增加了 ``indexof`` 方法，使得其后的所有数组对象都可以直接使用 ``indexof`` 方法，程序中最后一行粗体字代码就直接测试使用了数组对象的 ``indexof`` 方法。

如果将上面代码放在 ``JavaScript`` 代码最上面，则代码中的所有数组都会增加 ``indexof`` 方法。一定要将这段代码放在 ``JavaScript`` 脚本的开头，因为只有将 ``indexof`` 方法增加到 ``prototype`` 函数之后，创建的 ``Array`` 实例才有 ``indexof`` 方法。

虽然可以在任何时候为一个类增加属性和方法，但通常建议在类定义结束后立即增加该类所需的方法，这样可避免造成不必要的混乱。同时，对于需要在类定义中定义方法的情形，尽量避免直接在类定义中定义方法，这样可能造成内存泄漏和产生闭包。比较安全的方式是通过 ``prototype`` 属性为该类增加属性和方法。

尽量避免使用内嵌函数为类定义方法，而应该使用增加 ``prototype`` 属性的方式来增加方法。通过 ``prototype`` 属性来为类动态地增加属性和方法会让程序更加安全，性能更加稳定。

创建对象
=======
正如前文介绍的， ``JavaScript`` 对象是一个特殊的数据结构， ``JavaScript`` 对象只是一种特殊的关联数组。创建对象并不是总需要先创建类，与纯粹面向对象语言不同的是， ``JavaScript`` 中创建对象可以不使用任何类。 ``JavaScript`` 中创建对象大致有3种方式：

- 使用 ``new`` 关键字调用构造器创建对象。
- 使用 ``Object`` 直接创建对象。
- 使用 ``JSON`` 语法创建对象。

使用new关键字调用构造器创建对象
------------------------------
使用 ``new`` 关键字调用构造器创建对象，这是最接近面向对象语言创建对象的方式， ``new`` 关键字后紧跟函数的方式非常类似于 ``Java`` 中 ``new`` 后紧跟构造器的方式，通过这种方式创建对象简单、直观。 ``JavaScript`` 中所有的函数都可以作为构造器使用，使用 ``new`` 调用函数后总可以返回一个对象。看如下代码。

.. code-block:: js

	// 定义一个函数，同时也定义了一个Person类
    function Person(name, age) {
        //将name、age形参赋值给name、age实例属性
        this.name = name;
        this.age = age;
    }
    // 分别以两种方式创建Person实例
    var p1 = new Person();
    var p2 = new Person('yeeku', 29);
    // 输出p1的属性
    document.writeln("p1的属性如下：" + p1.name + p1.age + "<br />");
    // 输出p2的属性
    document.writeln("p2的属性如下：" + p2.name + p2.age);

在上面代码中，以两种不同的方式创建了 ``Person`` 对象。因为 ``JavaScript`` 支持空参数特性，所以调用函数时，依然可以不传入参数，如果没有传入参数，则对应的参数值是 ``undefined`` 。

前面已经介绍过，在函数中使用 ``this`` 修饰的变量是该函数的实例属性，以函数名修饰的变量则是该函数的类属性。实例属性以实例访问，类属性则以函数名访问。以这种方式创建的对象是 ``Person`` 的实例，也是 ``Object`` 的实例。上面代码的执行结果是， ``pl`` 的两个属性都是 ``undefned;`` 而 ``p2`` 的 ``name`` 属性为 ``yeeku`` ， ``age`` 属性为 ``29`` 。

使用Object直接创建对象
---------------------
``JavaScript`` 的对象都是 ``Object`` 类的子类，因此可以采用如下方法创建对象。

.. code-block:: js

    var myObj =  new Object();//创建一个默认对象

这是空对象，该对象不包含任何的属性和方法。与 ``Java`` 不同的是， ``JavaScript`` 是动态语言，因此可以动态地为该对象增加属性和方法。在静态语言（如Java、C#）中，一个对象一旦创建成功，它所包含的属性值可以变化，但属性的类型、属性的个数都不可改变，也不可增加方法。

``JavaScript`` 既可以为对象动态地增加方法，也可以动态地增加属性。看如下代码。

.. code-block:: js

	// 创建空对象
    var myObj = new Object();
    // 增加属性
    myObj.name = 'yeeku';
    // 增加属性
    myObj.age = 29;
    // 输出对象的两个属性
    document.writeln(myObj.name  + myObj.age);

上面代码直接为对象增加两个属性，这种语法从某个侧面反映了 ``JavaScript`` 对象的本质：它是一个特殊的关联数组。事实上， ``JavaScript`` 完全允许使用数组语法来访问属性。

也可以使用匿名函数为对象增加方法。此处没有必要使用有名字的函数，当然也可以使用有名字的函数。例如：

.. code-block:: js

	myObj.info = function abc(){
	  	statements;
	}

正如前面提到的， ``JavaScript`` 还可以通过 ``new Function()`` 的方法来定义匿名函数，因此完全可以通过这种方式来为 ``JavaScript`` 对象增加方法，如下代码所示。

.. code-block:: js

	var myObj = new Object();
    myObj.name = 'yeeku';
    myObj.age = 29;
    // 为对象增加方法
    myObj.info = new Function("document.writeln('对象的name属性：' + this.name);"
            + "document.writeln('<br />');"
            + "document.writeln('对象的age属性：' + this.age)");
    document.writeln("<hr / >");
    myObj.info();

除此之外， ``JavaScript`` 也允许将一个已有的函数添加为对象的方法，看如下代码。

.. code-block:: js

	//创建空对象
    var myObj = new Object();
    //为空对象增加属性
    myObj.name = 'yeeku';
    myObj.age = 29;
    //创建一个函数
    function abc() {
        document.writeln("对象的name属性：" + this.name);
        document.writeln("<br />");
        document.writeln("对象的age属性：" + this.age);
    };
    //将已有的函数添加为对象的方法
    myObj.info = abc;
    document.writeln("<hr />");
    //调用方法
    myObj.info();

上面程序中第一段粗体字代码定义了一个普通函数，程序的最后一行粗体字代码将 ``abc`` 函数直接赋值给 ``myObj`` 对象的 ``info`` 属性，这样就为 ``myObj`` 对象添加了一个 ``info`` 方法。

值得指出的是，将已有的函数添加为对象方法时，不能在函数名后添加括号。一旦添加了括号，将表示调用函数，不再是将函数本身赋给对象的方法，而是将函数的返回值赋给对象的属性。

为对象添加方法时，不要在函数后添加括号。一旦添加了括号，将表示要把函数的返回值赋给对象的属性。

使用JSON语法创建对象
-------------------
JSON( JavaScript Object Notation)语法提供了一种更简单的方式来创建对象，使用 ``JSON`` 语法可避免书写函数，也可避免使用 ``new`` 关键字，可以直接创建一个 ``JavaScript`` 对象。为了创建 ``JavaScript`` 对象，可以使用花括号，然后将每个属性写成 ``key:value`` 对的形式。

对于早期的 ``JavaScript`` 版本，如果要使用 ``JavaScript`` 创建一个对象，在通常情况下可能会这样写：

.. code-block:: js

	function Person(name,sex){//定义一个函数，作为构造器
	    this.name = name;
	    this.sex = sex;
	}
	var p = new Person('yeeku','male');//创建一个Person实例

从Javascript l.2开始，创建对象有了一种更快捷的语法，语法如下：

.. code-block:: js

	var p = {
	    "name"：'yeeku',//名称可以没有引号
	    "gender"：'male'
	};
	alert(p);

这种语法就是一种 ``JSON`` 语法。显然，使用 ``JSON`` 语法创建对象更加简捷、方便。

创建对象时，总以 ``{`` 开始，以 ``}`` 结束，对象的每个属性名和属性值之间以英文冒号( ``:`` )隔开，多个属性定义之间以英文逗号( ``,`` )隔开。语法格式如下：

.. code-block:: js

	object = {
	    propertyName1：propertyValue,
	    proPertyName2：propertyValue,
	    ...
	}

必须注意的是，并不是每个属性定义后面都有英文逗号(，)，必须后面还有属性定义时才需要逗号(，)，也就是最后一个属性定义后不再有英文逗号(，)。

使用 ``JSON`` 语法创建 ``JavaScript`` 对象时，属性值不仅可以是普通字符串，也可以是任何基本数据类型，还可以是函数、数组，甚至可以是另外一个 ``JSON`` 语法创建的对象。例如：

.. code-block:: js

	person = {
	    name：'yeeku',
	    gender：'male'
	    //使用JSON对象为其指定一个属性
	    son：{
	        name：'nono',
	        grade：1
	    },
	    //使用JSON语法为person直接分配一个方法
	    info：function(){
	        document.writeln("姓名："+ this.name +"性别："+this.gender);
	    }
	}


