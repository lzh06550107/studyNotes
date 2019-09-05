******
复合数据类型
******

复合类型是由多个基本数据类型（也可以包括复合类型）组成的数据体。 ``JavaScript`` 中的复合类型大致上有如下3种。

- ``Object`` ：对象。
- ``Array`` ：数组。
- ``Function`` ：函数。

下面依次介绍这3种复合类型。

对象
====
对象是一系列命名变量、函数的集合。其中命名变量的类型既可以是基本数据类型，也可以是复合类型。对象中的命名变量称为属性，而对象中的函数称为方法。对象访问属性和函数的方法都是通过 ``.`` 实现的，例如如下代码用于判断浏览器的版本：

.. code-block:: js

    alert("浏览器的版本为：" + navigator.appVersion);//获得浏览器版本

正如前文提到的， ``JavaScript`` 是基于对象的脚本语言，它提供了大量的内置对象供用户使用。除 ``Object`` 之外， ``JavaScript`` 还提供了如下常用的内置类。

- ``Array`` ：数组类
- ``Date`` ：日期类。
- ``Error`` ：错误类。
- ``Function`` ：函数类。
- ``Math`` ：数学类，该对象包含相当多的执行数学运算的方法。
- ``Number`` ：数值类。
- ``Object`` ：对象类。
- ``String`` ：字符串类。

数组
====
数组是一系列的变量。与其他强类型语言不同的是， ``JavaScript`` 中数组元素的类型可以不相同。定义一个数组有如下3种语法：

.. code-block:: js

	var a = [3, 5, 23];
	var b = [];
	var c = new Array();

第一种在定义数组时已为数组完成了数组元素的初始化，第二种和第三种都只创建一个空数组。

.. code-block:: js

	// 定义一个数组，定义时直接给数组元素赋值。
    var a = [ 3, 5, 23 ];
    // 定义一个空数组
    var b = [];
    // 定义一个空数组。
    var c = new Array();
    // 直接为数组元素赋值
    b[0] = 'hello';
    // 直接为数组元素赋值
    b[1] = 6;
    // 直接为数组元素赋值
    c[5] = true;
    // 直接为数组元素赋值
    c[7] = null;
    // 输出三个数组值和数组长度
    console.log(a + "\n" + b + "\n" + c + "\na数组的长度：" + a.length + "\nb数组的长度：" + b.length + "\nc数组的长度：" + c.length);

正如从代码中看到的， ``JavaScript`` 中数组的元素并不要求相同，同一个数组中的元素类型可以互不相同。

``JavaScript`` 为数组提供了一个 ``length`` 属性，该属性可得到数组的长度， ``JavaScript`` 的数组长度可以随意变化，它总等于所有元素 **索引最大值+1** 。 **JavaScript的数组索引从0开始。**

``JavaScript`` 作为动态、弱类型语言，归纳起来，其数组有如下3个特征。

- ``JavaScript`` 的数组长度可变。数组长度总等于所有元素 索引最大值+1 。
- 同一个数组中的元素类型可以互不相同。
- 访问数组元素时不会产生数组越界，访问并未赋值的数组元素时，该元素的值为 ``undefined`` 。

函数
====

函数是 ``JavaScript`` 中另一个复合类型。函数可以包含一段可执行性代码，也可以接收调用者传入参数。正如所有的弱类型语言一样， ``JavaScript`` 的函数声明中，参数列表不需要数据类型声明，函数的返回值也不需要数据类型声明。函数定义的语法格式如下：

下面代码定义了一个简单的函数。

.. code-block:: js

	// 定义一个函数，定义函数时无需声明返回值类型，也无需声明变量类型
    function judgeAge(age) {
        // 如果参数值大于60
        if (age > 60) {
            alert("老人");
        }
        // 如果参数值大于40
        else if (age > 40) {
            alert("中年人");
        }
        // 如果参数值大于15
        else if (age > 15) {
            alert("青年人");
        }
        // 否则
        else {
            alert("儿童");
        }
    }
    // 调用函数
    judgeAge(46);

上面代码定义了一个简单的函数，然后通过 ``judgeAge(46)`` 调用函数。代码的执行结果是中年人。

调用函数的语法如下：

.. code-block:: js

    functionName(value1,value2,...);

上面函数存在一个小小的问题：如果传入的参数不是数值会怎样呢？为了让程序更加严谨，应该先判断参数的数据类型，判断变量的数据类型使用 ``typeof`` 运算符，该函数用于返回变量的数据类型。

为了让上面函数更加严谨，可以将函数修改为如下：

.. code-block:: js

	function judgeAge(age) {
        // 要求age参数必须是数值
        if (typeof age === "number") {
            // 如果参数值大于60
            if (age > 60) {
                alert("老人");
            }
            // 如果参数值大于40
            else if (age > 40) {
                alert("中年人");
            }
            // 如果参数值大于15
            else if (age > 15) {
                alert("青年人");
            }
            // 否则
            else {
                alert("儿童");
            }
        } else {
            alert("参数必须为数值");
        }
    }
    // 调用函数
    judgeAge(46);
    judgeAge('dd');

从语法定义的角度来看， ``JavaScript`` 函数与 ``Java`` 方法有些相似。但实际上它们的差别很大，归纳起来，主要存在如下4点区别。

- JavaScript函数无须声明返回值类型。
- JavaScript函数无须声明形参类型。
- JavaScript函数可以独立存在，无须属于任何类。
- JavaScript函数必须使用 ``function`` 关键字定义。

函数中有一个数组在对传入的参数进行存储。这个数组就是 ``arguments`` 。

.. code-block:: js

	function show(x, y) {
		//  alert(arguments.length);
		//  alert(x+"："+y);
		for (var a = 0; a < arguments.length; a++) {
			document.write(arguments[a]);
		}
	}
