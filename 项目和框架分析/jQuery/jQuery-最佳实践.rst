**********
jQuery最佳实践
**********

使用最新版本的jQuery
===================
``jQuery`` 的版本更新很快，你应该总是使用最新的版本。因为新版本会改进性能，还有很多新功能。

下面就来看看，不同版本的 ``jQuery`` 性能差异有多大。这里是三条最常见的 ``jQuery`` 选择语句：

- $('.elem')
- $('.elem', context)
- context.find('.elem')

我们用 1.4.2 、 1.4.4 、 1.6.2 三个版本的 ``jQuery`` 测试，看看浏览器在 1 秒内能够执行多少次。结果如下：

.. image:: ./images/bg2011080301.png

可以看到， 1.6.2 版本的运行次数，远远超过两个老版本。尤其是第一条语句，性能有数倍的提高。

其他语句的测试，比如 ``.attr("value")`` 和 ``.val()`` ，也是新版本的jQuery表现好于老版本。

用对选择器
==========

在 ``jQuery`` 中，你可以用多种选择器，选择同一个网页元素。每种选择器的性能是不一样的，你应该了解它们的性能差异。

最快的选择器：id选择器和元素标签选择器
-----------------------------------

举例来说，下面的语句性能最佳：

- $('#id')
- $('form')
- $('input')

遇到这些选择器的时候， ``jQuery`` 内部会自动调用浏览器的原生方法（比如 ``getElementById()`` ），所以它们的执行速度快。

较慢的选择器：class选择器
-----------------------

``$('.className')`` 的性能，取决于不同的浏览器。

``Firefox、Safari、Chrome、Opera`` 浏览器，都有原生方法 ``getElementByClassName()`` ，所以速度并不慢。但是， ``IE5-IE8`` 都没有部署这个方法，所以这个选择器在 ``IE`` 中会相当慢。

最慢的选择器：伪类选择器和属性选择器
---------------------------------
先来看例子。找出网页中所有的隐藏元素，就要用到伪类选择器：

- $(':hidden')

属性选择器的例子则是：

- $('[attribute=value]')

这两种语句是最慢的，因为浏览器没有针对它们的原生方法。但是，一些浏览器的新版本，增加了 ``querySelector()`` 和 ``querySelectorAll()`` 方法，因此会使这类选择器的性能有大幅提高。

最后是不同选择器的性能比较图。

.. image:: ./images/bg2011080302.png

可以看到， ``ID`` 选择器遥遥领先，然后是标签选择器，第三是 ``Class`` 选择器，其他选择器都非常慢。

理解子元素和父元素的关系
----------------------
下面六个选择器，都是从父元素中选择子元素。你知道哪个速度最快，哪个速度最慢吗？

- $('.child', $parent)
- $parent.find('.child')
- $parent.children('.child')
- $('#parent > .child')
- $('#parent .child')
- $('.child', $('#parent'))

我们一句句来看。

1. $('.child', $parent)

这条语句的意思是，给定一个 ``DOM`` 对象，然后从中选择一个子元素。 ``jQuery`` 会自动把这条语句转成 ``$.parent.find('child')`` ，这会导致一定的性能损失。它比最快的形式慢了 ``5%-10%`` 。

2. $parent.find('.child')

这条是最快的语句。 ``.find()`` 方法会调用浏览器的原生方法（ ``getElementById`` ， ``getElementByName`` ， ``getElementByTagName`` 等等），所以速度较快。

3. $parent.children('.child')

这条语句在 ``jQuery`` 内部，会使用 ``$.sibling()`` 和 ``javascript`` 的 ``nextSibling()`` 方法，一个个遍历节点。它比最快的形式大约慢 ``50%`` 。

4. $('#parent > .child')

``jQuery`` 内部使用 ``Sizzle`` 引擎，处理各种选择器。 ``Sizzle`` 引擎的选择顺序是从右到左，所以这条语句是先选 ``.child`` ，然后再一个个过滤出父元素 ``#parent`` ，这导致它比最快的形式大约慢 ``70%`` 。

5. $('#parent .child')

这条语句与上一条是同样的情况。但是，上一条只选择直接的子元素，这一条可以于选择多级子元素，所以它的速度更慢，大概比最快的形式慢了 ``77%`` 。

6. $('.child', $('#parent'))

``jQuery`` 内部会将这条语句转成 ``$('#parent').find('.child')`` ，比最快的形式慢了 ``23%`` 。

所以，最佳选择是 ``$parent.find('.child')`` 。而且，由于 ``$parent`` 往往在前面的操作已经生成， ``jQuery`` 会进行缓存，所以进一步加快了执行速度。

不要过度使用jQuery
=================

``jQuery`` 速度再快，也无法与原生的 ``javascript`` 方法相比。所以有原生方法可以使用的场合，尽量避免使用 ``jQuery`` 。

以最简单的选择器为例， ``document.getElementById("foo")`` 要比 ``$("#foo")`` 快 10 多倍。

做好缓存
========
选中某一个网页元素，是开销很大的步骤。所以，使用选择器的次数应该越少越好，并且尽可能缓存选中的结果，便于以后反复使用。

比如，下面这样的写法就是糟糕的写法：

.. code-block:: js

	jQuery('#top').find('p.classA');
	jQuery('#top').find('p.classB');

更好的写法是：

.. code-block:: js

	var cached = jQuery('#top');
	cached.find('p.classA');
    cached.find('p.classB');

使用链式写法
===========
``jQuery`` 的一大特点，就是允许使用链式写法。

.. code-block:: js

    $('div').find('h3').eq(2).html('Hello');

采用链式写法时， ``jQuery`` 自动缓存每一步的结果，因此比非链式写法要快。根据测试，链式写法比（不使用缓存的）非链式写法，大约快了 ``25%`` 。

事件的委托处理（Event Delegation）
================================
``javascript`` 的事件模型，采用"冒泡"模式，也就是说，子元素的事件会逐级向上"冒泡"，成为父元素的事件。

利用这一点，可以大大简化事件的绑定。比如，有一个表格（ ``table`` 元素），里面有 100 个格子（ ``td`` 元素），现在要求在每个格子上面绑定一个点击事件（ ``click`` ），请问是否需要将下面的命令执行 100 次？

.. code-block:: js

	$("td").on("click", function(){
	   $(this).toggleClass("click");
　　  });

回答是不需要，我们只要把这个事件绑定在 ``table`` 元素上面就可以了，因为 ``td`` 元素发生点击事件之后，这个事件会"冒泡"到父元素 ``table`` 上面，从而被监听到。

因此，这个事件只需要在父元素绑定 1 次即可，而不需要在子元素上绑定 100 次，从而大大提高性能。这就叫事件的"委托处理"，也就是子元素"委托"父元素处理这个事件。

.. code-block:: js

	$("table").on("click", "td", function () {
	    $(this).toggleClass("click");
	});

更好的写法，则是把事件绑定在 ``document`` 对象上面。

.. code-block:: js

	$(document).on("click", "td", function () {
	    $(this).toggleClass("click");
	});

如果要取消事件的绑定，就使用 ``off()`` 方法。

.. code-block:: js

    $(document).off("click", "td");

少改动DOM结构
============

1. 改动 ``DOM`` 结构开销很大，因此不要频繁使用 ``.append()`` 、 ``.insertBefore()`` 和 ``.insetAfter()`` 这样的方法。

如果要插入多个元素，就先把它们合并，然后再一次性插入。根据测试，合并插入比不合并插入，快了将近 10 倍。

2. 如果你要对一个 DOM 元素进行大量处理，应该先用 ``.detach()`` 方法，把这个元素从 ``DOM`` 中取出来，处理完毕以后，再重新插回文档。根据测试，使用 ``.detach()`` 方法比不使用时，快了 60% 。

3. 如果你要在 ``DOM`` 元素上储存数据，不要写成下面这样：

.. code-block:: js

	var elem = $('#elem');
	elem.data(key,value);

而要写成

.. code-block:: js

	var elem = $('#elem');
	$.data(elem[0],key,value);

根据测试，后一种写法要比前一种写法，快了将近 10 倍。因为 ``elem.data()`` 方法是定义在 ``jQuery`` 函数的 ``prototype`` 对象上面的，而 ``$.data()`` 方法是定义 ``jQuery`` 函数上面的，调用的时候不从复杂的 ``jQuery`` 对象上调用，所以速度快得多。（此处可以参阅下面第10点。）

4. 插入 ``html`` 代码的时候，浏览器原生的 ``innterHTML()`` 方法比 ``jQuery`` 对象的 ``html()`` 更快。

正确处理循环
===========
循环总是一种比较耗时的操作，如果可以使用复杂的选择器直接选中元素，就不要使用循环，去一个个辨认元素。

``javascript`` 原生循环方法 ``for`` 和 ``while`` ，要比 ``jQuery`` 的 ``.each()`` 方法快，应该优先使用原生方法。

尽量少生成jQuery对象
===================
每当你使用一次选择器（比如 ``$('#id')`` ），就会生成一个 ``jQuery`` 对象。 ``jQuery`` 对象是一个很庞大的对象，带有很多属性和方法，会占用不少资源。所以，尽量少生成 ``jQuery`` 对象。

举例来说，许多 ``jQuery`` 方法都有两个版本，一个是供 ``jQuery`` 对象使用的版本，另一个是供 ``jQuery`` 函数使用的版本。下面两个例子，都是取出一个元素的文本，使用的都是 ``text()`` 方法。

你既可以使用针对 ``jQuery`` 对象的版本：

.. code-block:: js

	var $text = $("#text");
	var $ts = $text.text();

也可以使用针对 ``jQuery`` 函数的版本：

.. code-block:: js

	var $text = $("#text");
	var $ts = $.text($text);

由于后一种针对 ``jQuery`` 函数的版本不通过 ``jQuery`` 对象操作，所以相对开销较小，速度比较快。

选择作用域链最短的方法
=====================
严格地说，这一条原则对所有 ``Javascript`` 编程都适用，而不仅仅针对 ``jQuery`` 。

我们知道， ``Javascript`` 的变量采用链式作用域。读取变量的时候，先在当前作用域寻找该变量，如果找不到，就前往上一层的作用域寻找该变量。这样的设计，使得读取局部变量比读取全局变量快得多。

请看下面两段代码，第一段代码是读取全局变量：

.. code-block:: js

	var a = 0;

	function x() {
	    a += 1;
	}

第二段代码是读取局部变量：

.. code-block:: js

	function y() {
	    var a = 0;
	    a += 1;
	}

第二段代码读取变量 ``a`` 的时候，不用前往上一层作用域，所以要比第一段代码快五六倍。

同理，在调用对象方法的时候， ``closure`` 模式要比 ``prototype`` 模式更快。


``prototype`` 模式：

.. code-block:: js

	var X = function (name) { this.name = name; }

	X.prototype.get_name = function () { return this.name; };

closure模式：

.. code-block:: js

	var Y = function (name) {
	    var y = { name: name };
	    return { 'get_name': function () { return y.name; } };
	};

同样是 ``get_name()`` 方法， ``closure`` 模式更快。

使用Pub/Sub模式管理事件
======================

当发生某个事件后，如果要连续执行多个操作，最好不要写成下面这样：

.. code-block:: js

	function doSomthing() {
	    doSomethingElse();
	    doOneMoreThing();
	}


而要改用事件触发的形式：

.. code-block:: js

	function doSomething{
	    $.trigger("DO_SOMETHING_DONE");
	}

	$(document).on("DO_SOMETHING_DONE", function () {
	    doSomethingElse();
	});


还可以考虑使用 ``deferred`` 对象。

.. code-block:: js

	function doSomething() {
	    var dfd = new $.Deferred();

	    //Do something async, then...
	    //dfd.resolve();

	    return dfd.promise();
	}

	function doSomethingElse() {
	    $.when(doSomething()).then(//The next thing);
	}

