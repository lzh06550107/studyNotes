****
数组
****
数组与其他语言具有以下特点：

- 数组的每一项可以保存任何类型的数据；
- 数组的大小是可以动态调整的，即可以随着数据的添加自动增长以容纳新增数据；

创建数组
========
创建数组的两种基本方式：

- 使用Array构造函数；
- 使用数组字面量表示法；

使用Array构造函数
-----------------

.. code-block:: js

    var colors = new Array();

如果预先知道数组要保存的项目数量，也可以给构造函数传递该数量，而该数量会自动变成 length 属性的值。例如：

.. code-block:: js

    var colors = new Array(20);

也可以向 Array 构造函数传递数组中应该包含的项。例如：

.. code-block:: js

    var colors = new Array("red","blue","green");

当然，给构造函数传递一个值也可以创建数组。但这时候问题就复杂一点了，因为如果传递的是数值，则会按照该数值创建包含给定项数的数组；而如果传递的是其他类型的参数，则会创建包含那个值得只有一项的数组。

.. code-block:: js

    var colors = new Array(3); // 创建一个包含3项的数组
    var names = new Array("Greg"); // 创建一个包含1项，即字符串"Greg"的数组

    // 也可以省略new操作符
    var colors = Array(3);
    var names = Array("Greg");

使用数组字面量表示法
-------------------
数组字面量由一对包含数组项的方括号表示，多个数组项之间以逗号隔开，如下所示：

.. code-block:: js

    var colors = ["red", "blue", "green"]; // 创建一个包含3个字符串的数组
    var names = []; // 创建一个空数组

操作数组
========
在读取和设置数组的值时，要使用方括号并提供相应值的基于0的数字索引，如下所示：

.. code-block:: js

    var colors = ["red", "blue", "green"];
    alert(colors[0]);
    colors[2] = "black";
    colors[3] = "brown";

如果设置某个值得索引超过了数组现有项数，数组就会自动增长到该索引值加1的长度。

数组的项数保存在其 ``length`` 属性中，这个属性始终会返回0或更大的值，如：

.. code-block:: js

    var colors = ["red", "blue", "green"];
    var names = [];

    alert(colors.length); // 3
    alert(names.length); //0

数组的 ``length`` 属性很有特点——它不是只读的。因此，通过设置这个属性，可以从数组的末尾移除项或向数组中添加新项。例如：

.. code-block:: js

    var colors = ["red", "blue", "green"]; // 创建一个包含3个字符串的数组
    colors.length = 2;
    alert(colors[2]);  // undefined

利用 ``length`` 属性也可以方便地在数组末尾添加新项，如下所示：

.. code-block:: js

    var colors = ["red", "blue", "green"]; // 创建一个包含3个字符串的数组
    colors[colors.length] = "black";
    colors[colors.length] = "brown";

由于数组最后一项的索引始终是 ``length-1`` ，因此下一个新项的位置就是 ``length`` 。当把一个值放在超出当前数组大小的位置上时，数组就会重新计算其长度。

检测数组
========
如何确定某个对象是不是数组？对于一个网页，或者一个全局作用域而言，使用 instanceof 操作符就能得到满意的结果：

.. code-block:: js

    if(value instanceof Array) {
    	// 对数组执行某些操作
    }

``instanceof`` 操作符的问题在于，它假定单一的全局执行环境。如果在网页中包含多个框架，那实际上就存在两个以上不同的全局执行环境，从存在两个以上不同版本的 ``Array`` 构造函数。如果从一个框架向另一个框架传入一个数组，那么传入的数组与在第二个框架中原生创建的数组分别具有各自不同的构造函数。

为了解决这个问题， ``ECMAScript5`` 新增了 ``Array.isArray()`` 方法。这个方法的目的是最终确定某个值到底是不是数组，而不管它是在哪个全局执行环境中创建的。

.. code-block:: js

    if(Array.isArray(value)) {
    	// 对数组执行某些操作
    }

转换方法
========
所有对象都具有 ``toLocalString()`` 、 ``toString()`` 和 ``valueOf()`` 方法。其中，调用数组的 ``toString()`` 方法会返回由数组中每个值的字符串形式拼接而成的一个以逗号分隔的字符串。而调用 ``valueOf()`` 返回的还是数组。实际上，为了创建这个字符串会调用数组每一项的 ``toString()`` 方法。如：

.. code-block:: js

    var colors = ["red","blue","green"];//创建一个包含3个字符串的数组
    alert(colors.toString());//red,blue,green
    alert(colors.valueOf());//red,blue,green
    alert(colors);//red,blue,green

在这里，首先显式地调用了 ``toString()`` 和 ``valueOf()`` 方法，以便返回数组的字符串表示，每个值的字符串表示拼接了一个字符串，中间以逗号分隔。最后一行代码直接将数组传递给了 ``alert()`` 。由于 ``alert()`` 要接收字符串参数，所以它在后台会在后台调用 ``toString()`` 方法，由此会得到与直接调用 ``toString()`` 方法相同的结果。

另外 ``toLocaleString()`` 方法经常也会返回与 ``toString()`` 和 ``valueOf()`` 方法相同的值，但也不总是如此。当调用数组的 ``toLocaleString()`` 方法时，它也会创建一个数组值的以逗号分隔的字符串。而与前两个方法唯一的不同之处在于，这一次为了取得每一项的值，调用的是每一项的 ``toLocaleString()`` 方法，而不是 ``toString()`` 方法。如：

.. code-block:: js

   var person1 = {
        toLocaleString:function(){
            return "Nikolaos";
        },
        toString:function(){
            return "Nicholas";
        }
    };

    var person2 = {
        toLocaleString:function(){
            return "Grigorios";
        },
        toString:function(){
            return "Greg";
        }
    };
    var people = [person1,person2];
    alert(people);//Nicholas,Greg
    alert(people.toString());//Nicholas,Greg
    alert(people.toLocaleString());//Nikolaos,Grigorios

在这里定义了两个对象： ``person1`` 和 ``person2`` 。而且还分别为每一个对象定义了一个 ``toString()`` 方法和一个 ``toLocaleString()`` 方法，这两个方法返回不同的值。然后，创建一个包含前面定义的两个对象的数组。在数组传递给 ``alert()`` 时，输出结果是 ``Nicholas,Greg`` ，因为调用了数组每一项的 ``toString()`` 方法。而当调用数组的 ``toLocaleString()`` 方法是，输出的结果是 ``Nikolaos,Grigorios`` ，原因是调用了数组每一项的 ``toLocaleString()`` 方法。

数组继承的 ``toLocaleString()`` 、 ``toString()`` 和 ``valueOf()`` 方法，在默认情况下都会以逗号分隔的字符串的形式返回数组项。而如果使用 ``join()`` 方法，则可以使用不同的分隔符来构建这个字符串。 ``join()`` 方法只接收一个参数，即用作分隔符的字符串，然后返回包含所有数组项的字符串。如：

.. code-block:: js

    var colors = ["red","blue","green"];
    alert(colors.join("-"));//red-blue-green
    alert(colors.join("**"));//red**blue**green

如果不给 ``join()`` 方法传入任何值，或者给它传入 ``undefined`` ，则使用逗号作为分隔符。IE7及更早版本会错误的使用字符串 ``undefined`` 作为分隔符。如果给 ``join()`` 方法传入 ``null`` ，则会使用字符串 ``null`` 作为分隔符（不知是不是一个bug，其行为应该同undefined及不传递任何参数时的情形——使用默认的逗号进行分隔，可能会在新版本中更新）。

如果数组中的某一项的值是 ``null`` 或 ``undefined`` ，那么该值在 ``join()`` 、 ``toLocaleString()`` 、 ``toString()`` 和 ``valueOf()`` 方法返回的结果中以空字符串表示。

push/pop栈方法
==============
``ECMAScript`` 数组也提供了一种让数组的行为类似于其他数据结构的方法。具体来说，数组可以表现的就像栈一样，后者是一种可以限制插入和删除项的数据结构。栈是一种 ``LIFO`` （Last-In-First-Out，后进先出）的数据结构，也就是最新添加的项最早被移除。而栈中项的插入（叫做推入）和移除（叫做弹出），值发生在一个位置——栈的顶部。 ``ECMAScript`` 为数组专门提供了 ``push()`` 和 ``pop()`` 方法，以便实现类似栈的行为。

``push()`` 方法可以接收任意数量的参数，把它们逐个添加到数组末尾，并返回修改后数组的长度。而 ``pop()`` 方法则从数组末尾移除最后一项，减少数组的 ``length`` 值，然后返回移除的项。

.. code-block:: js

	var colors = new Array(); // 创建一个数组
	var count = colors.push("red", "green"); // 推入两项
	alert(count); // 2

	count = colors.push("black"); // 推入另一项
	alert(count); // 3

	var item = colors.pop(); // 取得最后一项
	alert(item) // "black"
	alert(colors.length); // 2

push/shift/unshift/pop队列方法
==============================
栈数据结构的访问规则是 ``LIFO`` （后进先出），而队列数据结构的访问规则是 ``FIFO`` （First-In-First-Out，先进先出）。队列在列表的末端添加项，从列表的前端移除项。由于 ``push()`` 是向数组末端添加项的方法，因此要模拟队列只需一个从数组前端取得项的方法，实现这一操作的数组方法就是 ``shift()`` ，它能够移除数组中的第一个项并返回该项，同时将数组长度减 ``1`` 。结合使用 ``shift()`` 和 ``push()`` 方法，可以像使用队列一样使用数组。

.. code-block:: js

	var colors = new Array(); // 创建一个数组
	var count = colors.push("red", "green"); // 推入两项
	alert(count); // 2

	count = colors.push("black"); // 推入另一项
	alert(count); // 3

	var item = colors.shift(); // 取得第一项
	alert(item) // "red"
	alert(colors.length); //2

``ECMAScript`` 还为数组提供了一个 ``unshift()`` 方法。顾名思义， ``unshift()`` 与 ``shift()`` 的用途相反：它能在数组前端添加任意个项并返回新数组的长度。因此，同时使用 ``unshift()`` 和 ``pop()`` 方法，可以从相反的方向来模拟队列，即在数组的前端添加项，从数组末端移除项。

.. code-block:: js

	var colors = new Array(); // 创建一个数组
	var count = colors.unshift("red", "green"); // 推入两项
	alert(count); // 2

	count = colors.unshift("black"); // 推入另一项
	alert(count); // 3

	var item = colors.pop(); // 取得最后一项
	alert(item); // "green"
	alert(colors.length); // 2

reverse/sort重排序方法
======================
数组中已经存在两个可以直接用来重排序的方法： ``reverse()`` 和 ``sort()`` 。 ``reverse()`` 方法会反转数组。 ``sort()`` 方法默认情况下按升序排列——即最小的值位于最前面，最大的值排在最后面。为了实现排序， ``sort()`` 方法会调用每个数组项的 ``toString()`` 转型方法，然后比较得到的字符串，以确定如何排序。即使数组中的每一项都是数值， ``sort()`` 方法比较的也是字符串，如：

.. code-block:: js

    var numbers = [0,1,5,10,15];
    numbers.sort();
    alert(numbers);//0,1,10,15,5

可见，即使例子中值的顺序没有问题，但 ``sort()`` 方法也会根据测试字符串的结果改变原来的顺序。因为数值 ``5`` 虽然小于 ``10`` ，但在进行字符串比较时， ``10`` 则位于 ``5`` 的前面，于是数组的顺序就被修改了。这种排序方式在很多情况下都不是最佳方案。因此 ``sort()`` 方法可以接收一个比较函数作为参数，以便指定哪个值位于哪个值的前面。

比较函数接收两个参数，如果第一个参数应该位于第二个参数之前则返回一个负数，如果两个参数相等则返回 ``0`` ，如果第一个参数应该位于第二个之后则返回一个正数。如：

.. code-block:: js

	function compare(value1, value2) {
		if(value1 < value2) {
			return -1;
		}else if (value1 > value2) {
			return 1;
		}else {
			return 0;
		}
	}

	var values = [0,1,5,10,15];
	values.sort(compare);
	alert(values); // 0,1,5,10,15

对于数值类型或者其 ``valueOf()`` 方法会返回数值类型的对象类型，可以使用一个更简单的比较函数。这个函数只要用第二个值减第一个值即可。如：

.. code-block:: js

    function compare(value1 , value2){
        return value2-value1;
    }
    var person1 = {
            name:"Nicholas",
            age:29,
            valueOf:function(){
                return this.age;
            },
            toString:function(){
                return "[" + this.name + "; " + this.age + "]";
            }
    };

    var person2 = {
            name:"Greg",
            age:21,
            valueOf:function(){
                return this.age;
            },
            toString:function(){
                return "[" + this.name + "; " + this.age + "]";
            }
    };

    var person3 = {
            name:"Gos",
            age:25,
            valueOf:function(){
                return this.age;
            },
            toString:function(){
                return "[" + this.name + "; " + this.age + "]";
            }
    };
    var people = [person1 , person2 , person3];
    people.sort(compare);
    alert(people);//[Nicholas; 29],[Gos; 25],[Greg; 21]
    var numbers = [0,1,5,10,15];
    numbers.sort(compare);
    alert(numbers);//15,10,5,1,0

concat/slice/splice操作方法
===========================
``ECMAScript`` 为操作已经包含在数组中的项提供了很多方法。其中， ``concat()`` 方法可以基于当前数组中的所有项创建一个新数组。具体来说，这个方法会先创建当前数组一个副本，然后将接收到的参数添加到这个副本的末尾，最后返回新构建的数组。在没有给 ``concat()`` 方法传递参数的情况下，它只是复制当前数组并返回副本。如果传递给 ``concat()`` 方法的是一或多个数组，则该方法会将这些数组中的每一项都添加到结果数组中。如果传递的值不是数组，这些值就会被简单地添加到结果数组的末尾。

.. code-block:: js

    var arr0 = [1,2];
    var arr1 = [4,5,[6,[7,8,9,"10"]]];
    var arr2 = arr0.concat(3 , arr1 , "11");
    alert(arr0);//1,2
    alert(arr2);//1,2,3,4,5,6,7,8,9,10,11

以上代码开始定义了一个包含两个值的数组 ``arr0`` ，然后，定义了一个比较复杂的数组 ``arr1`` ——数组的项中包含数组（该数组的项中仍然包含数组），接下来基于 ``arr0`` 调用 ``concat()`` 方法，并传入数值 3 和 ``arr1`` ，以及一个字符串 ``"11"`` 。最终结果数组 ``arr2`` 包含了 ``1,2,3,4,5,6,7,8,9,10,11`` 。也就是 ``concat()`` 方法会将传入的所有数组一层一层的解开直至每一项，然后将这些项添加到数组的末尾。至于原来的数组 ``arr0`` ，其值保持不变。

下一个方法是 ``slice()`` ，它能够基于当前数组中的一或多个项创建一个 **新数组** 。 ``slice()`` 方法可以接受一或两个参数，即要返回项的其实和结束位置。在只有一个参数的情况下， ``slice()`` 方法返回从该参数指定位置开始到当前数组末尾的所有项。如果有两个参数，该方法返回其实和结束位置之间的项——但不包含结束位置的项。 ``slice()`` 方法仍然不会影响原始数组。如果传入的参数大于原始数组 ``length`` 属性值，则会将大于原始数组 ``length`` 属性值的参数替换为原始数组的 ``length`` 属性值，如：

.. code-block:: js

   	var arr0 = [1,2,3];
    var arr1 = arr0.slice(5);
    var arr2 = arr0.slice(1,5);
    alert(arr1.length);//0
    alert(arr2);//2,3

在上面的代码中，先定义了一个长度为 ``3`` 的数组 ``arr0`` ，接下来基于 ``arr0`` 调用 ``slice()`` 方法。第一次调用时传入了一个参数 ``5`` ，此时 ``5>3`` ，则创建新数组 ``arr1`` 时，等同于语句：

.. code-block:: js

    var arr1 = arr0.slice(3);

第二次调用 ``slice()`` 方法时，传入两个参数 ``1`` 和 ``5`` ， ``5>3`` ，同上， ``arr2`` 的创建语句等同于：

.. code-block:: js

    var arr2 = arr0.slice(1,3);

如果不传入参数，或是传入参数调用 ``valueOf()`` 方法后返回的不是整数值的参数，则等同于：

.. code-block:: js

	arr0.slice(0);

即所有的不合规范的参数都被默认值 ``0`` 替换。

而如果传入的参数是一个负数，则返回从数组的末尾开始，向前参数绝对值个项，如：

.. code-block:: js

    var arr0 = [1,2,3];
    var arr1 = arr0.slice(-2);
    alert(arr1);//2,3

接下来是 ``splice()`` 方法，这个方法恐怕要算是最强大的数组方法了，它有多种用法。 ``splice()`` 的主要用途是向数组的中部插入项，但使用这种方法的方式有如下3种。

1. 删除：可以删除任意数量的项，只需指定两个参数：要删除的第一项的位置和要删除的项数。例如： ``splice(0,2)`` 会删除数组中的前两项。
2. 插入：可以向指定位置插入任意数量的项，只需提供3个参数：起始位置、0（要删除的项数）和要插入的项。如果插入多个项，可以再传入第四、第五，以至任意多个项。例如： ``splice(2, 0,"red" , "green")`` ,会从当前数组的位置2开始插入字符串 ``"red"`` 和 ``"green"`` 。
3. 替换：可以项指定位置插入任意数量的项，同时删除任意数量的项，只需指定3个参数：起始位置、要删除的项数和要插入的任意数量的项。插入的项数不必与删除的项数相等。例如， ``splice(2, 1, "red", "green")`` 会删除当前数组位置2的项，然后再从位置2开始插入字符串 ``"red"`` 和 ``"green"`` 。

``splice()`` 方法始终会返回一个数组，该数组中包含从原始数组中删除的项（如果没有删除任何项，则返回一个空数组）。如:

.. code-block:: js

    var numbers = [0,1,2,3];
    var removed = numbers.splice(0,1);//删除第一项
    alert(numbers);//1,2,3
    alert(removed);//0
    removed = numbers.splice(1,0,8,9);
    alert(removed);//返回的是一个空数组
    removed = numbers.splice(1,1,5,6);
    alert(removed);//8

indexOf/lastIndexOf位置方法
===========================
``ECMAScript5`` 为数组实例添加了两个位置方法： ``indexOf()`` 和 ``lastIndexOf()`` 。这两个函数都接收两个参数：要查找的项和（可选的）表示查找起点位置的索引。其中， ``indexOf()`` 方法从数组的开头（位置0）开始向后查找， ``lastIndexOf()`` 方法则从数组的末尾开始向前查找。

这两个方法都返回要查找的项在数组中的位置，或者在没找到的情况下返回 ``-1`` 。在比较第一个参数与数组中的每一项时，会使用全等操作符；也就是说，要求查找的项必须严格相等。

迭代方法
========
``ECMAScript5`` 为数组定义了 ``5`` 个迭代方法。每个方法都接收两个参数：要在每一项上运行的函数和（可选的）运行该函数的作用域对象——影响 ``this`` 的值。传入这些方法中的函数会接收三个参数：数组项的值、该项在数组中的位置和数组对象本身。根据使用的方法不同，这个函数执行后的返回值可能会也可能不会影响访问的返回值。以下是这5个迭代方法的作用。

- ``every()`` :对数组中的每一项运行给定函数，如果该函数对每一项都返回 ``true`` ，则返回 ``true`` 。
- ``filter()`` :对数组中的每一项运行给定函数，返回该函数会返回 ``true`` 的项组成的数组。
- ``forEach()`` :对数组中的每一项运行给定函数，这个方法没有返回值。
- ``map()`` :对数组中的每一项运行给定函数，返回每次函数调用的结果组成的数组。
- ``some()`` :对数组中的每一项运行给定函数，如果该函数对任一项返回 ``true`` ，则返回 ``true`` 。

以上方法都不会修改数组中的包含的值。

在这些方法中，最相似的是 ``every()`` 和 ``some()`` ，它们都用于查询数组中的项是否满足某个条件。对于 ``every()`` 来说，传入的函数必须对每一项都返回 ``true`` ，这个方法菜返回 ``true`` ；否则，它就返回 ``false`` 而 ``some()`` 方法则只要传入的函数对数组中某一项返回 ``true`` ，就会返回 ``true`` 。如：

.. code-block:: js

    var numbers = [0,1,2,3];
    var everyResult = numbers.every(function(item , index , array){
        return (item > 2);
    });
    alert(everyResult);//false
    var someResult = numbers.some(function(item , index , array){
        return (item > 2);
    });
    alert(someResult);//true

以上代码调用了 ``every()`` 和 ``some()`` ，传入的函数只要给定项大于 ``2`` 就会返回 ``true`` 。对于 ``every()`` ,它返回的是 ``false`` ，因为只有部分数组项符合条件。对于 ``some()`` ，结果就是 ``true`` ，因为至少有一项是大于 ``2`` 的。

``filter()`` 方法的作用是检索出数组中满足给定条件的项，如下面的代码中，查询出年龄大于24周岁的人员信息：

.. code-block:: js

    var numbers = [1, 2, 3, 4, 5, 4, 3, 2, 1];
    var filterResult = numbers.filter(function(item, index, array) {
    	return (item > 2);
    });
    alert(filterResult); // [3,4,5,4,3]

.. code-block:: js

   function compare(value1 , value2){
        return value2-value1;
    }
    var person1 = {
            name:"Nicholas",
            age:29,
            valueOf:function(){
                return this.age;
            },
            toString:function(){
                return "[" + this.name + "; " + this.age + "]";
            }
    };

    var person2 = {
            name:"Greg",
            age:21,
            valueOf:function(){
                return this.age;
            },
            toString:function(){
                return "[" + this.name + "; " + this.age + "]";
            }
    };

    var person3 = {
            name:"Gos",
            age:25,
            valueOf:function(){
                return this.age;
            },
            toString:function(){
                return "[" + this.name + "; " + this.age + "]";
            }
    };
    var people = [person1 , person2 , person3];
    var oldPeople = people.filter(function(item , index , array){
        return item>24;
    });
    alert(oldPeople);//[Nicholas; 29],[Gos; 25]

在上面的例子中一个 ``person`` 对象能够与数值 ``24`` 进行比较是因为，在对象的定义中定义了 ``valueOf()`` 方法，而在比较时会调用对象的 ``valueOf()`` 方法，返回的是 ``person`` 的 ``age`` 属性值，进而过滤出符合条件（>24）的 ``person`` 。

``map()`` 方法也返回一个数组，则这个数组的每一项都是在原始数组中的对应项上运行传入函数的结果。例如，给数组中的每一项乘以 2（所有人的工资翻倍，嗯），然后返回这些乘积组成的数组，如：

.. code-block:: js

    var salary = [7800,6000,2300];
    var doubledSalary = salary.map(function(item , index , array){
        return item*2;
    });
    alert(doubledSalary);//15600,12000,4600

最后一个方法是 ``forEach()`` ,它只是对数组中的每一项运行传入的函数，这个方法没有返回值，本质上与使用 ``for`` 循环迭代数组一样。如：

.. code-block:: js

    var nums = [1,2,3,4,5,6];
    nums.forEach(function(item,index,array){
        nums[index] = item*2;
        //或是进行其他操作
    });
    alert(nums);//2,4,6,8,10,12

得到这样的结果与前面说的这5个方法不会改变数组的值是否矛盾，不矛盾，因为上面的例子改变数组值的不是 ``forEach()`` 方法，而是代码 ``nums[index] = item*2`` 。

reduce/reduceRight缩小方法
==========================
``ECMAScript5`` 还增加了两个缩小数组的方法： ``reduce()`` 和 ``reduceRight()`` 。这两个方法都会迭代数组的所有项，然后构建一个最终返回的值。其中， ``reduce()`` 方法从数组的第一项开始，逐个遍历到最后。而 ``reduceRight()`` 则从数组的最后一项开始，项前遍历到第一项。

这两个方法都接收两个参数：一个在每一项上调用的函数和（可选的）作为缩小基础的初始值。传给 ``reduce()`` 和 ``reduceRight()`` 的函数接收4个参数：前一个值、当前值、项的索引和数组对象。这个函数返回的任何值都会作为第一个参数自动传给下一项。第一次迭代发生在数组的第二项上，因此第一个参数是数组的第一项，第二个参数就是数组的第二项。

使用 ``reduce()`` 方法可以执行求数组中所有值之和的操作，如：

.. code-block:: js

   var nums = [1,2,3,4,5,6];
    var sum = nums.reduce(function(prev, cur , index , array){
        return prev + cur;
    });
    alert(sum);//21

``reduceRight()`` 的作用类似，只不过方向相反而已。

链式使用
========
上面这些数组方法之中，有不少返回的还是数组，所以可以链式使用。

.. code-block:: js

    var users = [
      {name: 'tom', email: 'tom@example.com'},
      {name: 'peter', email: 'peter@example.com'}
    ];

    users
    .map(function (user) {
      return user.email;
    })
    .filter(function (email) {
      return /^t/.test(email);
    })
    .forEach(console.log);
    // "tom@example.com"

上面代码中，先产生一个所有 ``Email`` 地址组成的数组，然后再过滤出以 ``t`` 开头的 ``Email`` 地址。
