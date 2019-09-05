****
语句
****

语句
====

语句是 ``JavaScript`` 的基本执行单位。 ``JavaScript`` 要求所有的语句都以分号(；)结束。语句既可以是简单的赋值语句，也可以是算术运算语句，还可以是逻辑运算语句等。除此之外，还有一些特殊的语句，下面具体介绍这些特殊的语句。


语句块
------
所谓语句块就是使用花括号包含的多个语句，语句块是一个整体的执行体，类似于一个单独的语句。下面是语句块示例：

.. code-block:: js

	{
	    x = Math.PI;
	    cx = Math.cos(x);
	    alert("Hello JavaScript");
	}

虽然语句块类似于一个单独的语句，但语句块后不需要以分号结束。但语句块中的每个语句都需要以分号结束。

.. note:: 虽然 ``JavaScript`` 支持使用语句块，但 ``JavaScript`` 的语句块不能作为变量的作用域。

空语句
------
最简单的空语句仅有一个分号(;)，如下是一个空语句：

上面的空语句没有丝毫用处，因此实际中几乎不会使用这种空语句。但空语句主要用于没有循环体的循环。看如下代码。

.. code-block:: js

	// 声明一个数组
    var a = [];
    // 使用空语句，完成数组的初始化
    for (var i = 0; i < 10; a[i++] = i + 20)
        ;
    // 遍历数组元素
    for (index in a) {
        document.writeln(a[index] + "<br />");
    }

上面的粗体字代码使用空语句完成数组的初始化，这种初始化更加简洁。

异常抛出语句
-----------
``JavaScript`` 支持异常处理，支持手动抛出异常。与 ``Java`` 不同的是， ``JavaScript`` 的异常没有 ``Java`` 那么丰富， ``JavaScript`` 的所有异常都是 ``Error`` 对象。当 ``JavaScript`` 需要抛出异常时，总是通过 ``throw`` 语句抛出 ``Error`` 对象。抛出 ``Error`` 对象的语法如下：

.. code-block:: js

    throw new Error(errorString);

``JavaScript`` 既允许在代码执行过程中抛出异常，也允许在函数定义中抛出异常。在代码执行过程中，一旦遇到异常，立即寻找对应的异常捕捉块（ ``catch`` 块），如果没有对应的异常捕捉块，异常将传播给浏览器，程序非正常中止。看如下代码。

.. code-block:: js

	// 对计数器i循环
    for (var i = 0; i < 10; i++) {
        // 在页面输出i
        document.writeln(i + '<br />');
        // 当i > 4时，抛出用户自定义异常
        if (i > 4)
            throw new Error('用户自定义错误');
    }

当 ``i=5`` 时，手动抛出异常，但没有得到处理，因而传播到浏览器，引起程序非正常中止，浏览器也有关于错误的提示。

``Chrome`` 浏览器提供了强大的 ``JavaScript`` 调试工具， ``JavaScript`` 开发人员可以通过选择主菜单“工具一开发人员工具”，或者直接按 ``Ctrl+Shifi+I`` 快捷键打开调试控制台。实际上， ``Opera`` 浏览器也提供了功能强大的调试工具： ``Dragonfly`` ，同样可通过按 ``Ctrl+Shift+I`` 快捷键来打开 ``Dragonfly`` 工具。 ``Opera`` 打开 ``Dragonfly`` 调试工具后的界面。

异常捕捉语句
-----------
当程序出现异常时，这种异常不管是用户手动抛出的异常，还是系统本身的异常，都可使用 ``catch`` 捕捉异常。 ``JavaScript`` 代码运行中一旦出现异常，程序就跳转到对应的 ``catch`` 块。异常捕捉语句的语法格式如下：(同Java)

这种异常捕捉语句大致上类似于 ``Java`` 的异常捕捉语句，但有一些差别：因为 ``JavaScript`` 的异常体系远不如 ``Java`` 丰富，因此无须使用多个 ``catch`` 块。与 ``Java`` 异常机制类似的是， ``finally`` 块是可以省略的，但一旦指定了 ``finally`` 块， ``finally`` 代码块就总会获得执行的机会。看如下代码。

.. code-block:: js

	try {
        for (var i = 0; i < 10; i++) {
            // 在页面输出i值
            document.writeln(i + '<br />');
            // 当i大于4时，抛出异常
            if (i > 4)
                throw new Error('用户自定义错误');
        }
    }
    // 如果try块中的代码出现异常，自动跳转到catch块执行
    catch (e) {
        document.writeln('系统出现异常' + e.message + '<br/>');
    }
    // finally块的代码总可以获得执行的机会
    finally {
        document.writeln('系统的finally块');
    }

从上面粗体字代码可以看出， ``JavaScript`` 同样可以获取异常的描述信息，通过异常对象的 ``message`` 属性即可访问异常对象的描述信息。

归纳起来， ``JavaScript`` 异常机制与 ``Java`` 异常机制存在如下区别。

- ``JavaScript`` 只有一个异常类： ``Error`` ，无须在定义函数时声明抛出该异常，所以没有 ``throws`` 关键字。
- ``JavaScript`` 是弱类型语言，所以 ``catch`` 语句后括号里的异常实例无须声明类型。
- ``JavaScript`` 只有一个异常类，所以 ``try`` 块后最多只能有一个 ``catch`` 块。
- 获取异常的描述信息是通过异常对象的 ``message`` 属性，而不是通过 ``getMessage()`` 方法实现的。

with语句
--------
``with`` 语句是一种更简洁的写法，使用 ``with`` 语句可以避免重复书写对象。 ``with`` 语句的语法格式如下：

.. code-block:: js

	with(object){
    	statements;
	}

如果 ``with`` 后的代码块只有一行语句，则可以省略花括号。但在只有一行语句的情况下，使用 ``with`` 语句意义不大。关于 ``with`` 语句的作用，看如下代码：

.. code-block:: js

	document.writeln("Hello<br/>");
	document.writeln("World<br/>");
	document.writeln("JavaScript<br/>");

在上面的代码中，多次使用 ``document`` 的 ``writeln`` 方法重复输出静态字符串。使用 ``with`` 语句可以避免重复书写 ``document`` 对象。将上面代码该为如下形式，效果完全相同。

.. code-block:: js

	with(document){
	    writeln("Hello<br/>");
	    writeln("World<br/>");
	    writeln("JavaScript<br/>");
	}

``with`` 语句的主要作用是避免重复书写同一个对象。

流程控制
========

``JavaScript`` 支持的流程控制也很丰富， ``JavaScript`` 支持基本的分支语句，如 ``if`` 、 ``if ...else`` 等；也支持基本的循环语句，如 ``while`` 、 ``for`` 等；还支持 ``for in`` 循环等；循环相关的 ``break`` 、 ``continue`` ，以及带标签的 ``break`` 、 ``continue`` 语句也是支持的。

if语句
------
``if`` 语句的语法：

.. code-block:: js

	if (condition) {
	    statement1
	}else {
		statement2
	}



do-while语句
------------
``do-while`` 语句的语法：

.. code-block:: js

    do {
    	statement
    } while (expression);


while语句
---------
``while`` 语句的语法：

.. code-block:: js

    while (expression) {
    	statement
    }



for语句
-------
``for`` 语句的语法：

.. code-block:: js

	for(initialization; expression; post-loop-expression) {
		statement
	}



for-in语句
----------
for in 循环的本质是一种 ``foreach`` 循环，它主要有两个作用：

- 遍历数组里的所有数组元素。
- 遍历 ``JavaScript`` 对象的所有属性。

for-in语句的语法：

.. code-block:: js

    for (property in object) {
    	statement
    }

    for (index in array) {
    	statement
    }

当遍历数组时， ``for in`` 循环的循环计数器是数组元素的索引值。看如下代码。

.. code-block:: js

 	// 定义数组
    var array = ['hello' , 'javascript' , 'world'];
    // 遍历数组的每个元素
    for (index in array)
        document.writeln('索引' + index + '的值是：' + array[index] + "<br />" );

除此之外， ``for in`` 循环还可遍历对象的所有属性。此时，循环计数器是该对象的属性名。看如下代码。

.. code-block:: js

	// 在页面输出静态文本
    document.write("<h1>Navigator对象的全部属性如下：</h1>");
    // 遍历navigator对象的所有属性
    for (propName in navigator) {
        // 输出navigator对象的所有属性名，以及对应的属性值
        document.write('属性' + propName + '的值是：' + navigator[propName]);
        document.write("<br />");
    }


label语句
---------
label语句的语法：

.. code-block:: js

    label: statement

例如：

.. code-block:: js

    start: for(var i=0; i<count; i++) {
    	alert(i);
    }

``start`` 标签可以在将来由 ``break`` 或 ``continue`` 语句引用。加标签的语句一般都要与 ``for`` 语句等循环语句配合使用。

break和continue语句
-------------------
``break`` 和 ``continue`` 都可用于中止循环，区别是 ``continue`` 只是中止本次循环，接着开始下一次循环（我们也可以视 ``continue`` 为忽略本次循环后面的执行语句）；而 ``break`` 则是完全中止整个循环，开始执行循环后面的代码。

如果在 ``break`` 或 ``continue`` 后使用标签，则可以直接跳到标签所在的循环。至于使用 ``break`` 和 ``continue`` 的区别与前面类似， ``break`` 是完全中止标签所在的循环，而 ``continue`` 则是中止标签所在的本次循环。

所谓标签，就是在一个合法的标识符后紧跟一个英文冒号( ``：`` )，标签只有放在循环之前才有效，标签放在其他地方将没有意义。

with语句
--------
with语句的语法：

.. code-block:: js

    with (expression) {
    	statement
    }



switch语句
----------
switch语句的语法：

.. code-block:: js

    switch (expression) {
    	case value: statement
    		break;
    	case value: statement
    		break;
    	case value: statement
    		break;
    	case value: statement
    		break;
    	default: statement
    }

``javascript`` 中的 ``switch`` 自己的特色：

- 可以在 ``switch`` 语句使用任何数据类型，无论是字符串，还是对象都没有问题；
- 每个 ``case`` 的值不一定是常量，可以是变量，甚至是表达式；
- ``switch`` 语句在比较值时使用的全等操作符，因此不会发生类型转换，如，字符串 ``"10"`` 不等于 ``10`` ;

