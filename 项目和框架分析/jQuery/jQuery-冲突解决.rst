****************************
jQuery中noConflict()机制的冲突解决方法
****************************

许多的 ``JS`` 框架类库都选择使用 ``$`` 符号作为函数或变量名，而且在实际的项目开发中，使用模板语言的话有可能"$"符号即为该模板语言的关键字。例如 ``Veclocity`` 模板语言， ``$`` 是关键字。与 ``jQuery`` 一起使用可能会存在冲突。

``jQuery`` 是使用 ``$`` 符号作为函数或变量名最为典型的一个。在 ``jQuery`` 中, ``$`` 符号只是 ``window.jQuery`` 对象的一个引用，因此即使 ``$`` 被删除， ``jQuery`` 依然能保证整个类库的完整性。

``jQuery`` 的设计充分考虑了多框架之间的引用冲突。我们可以使用 ``jQuery.noConflict`` 方法来轻松实现控制权的转交。

在论述如何解决 ``jQuery`` 冲突之前，我们有必要先对 ``noConflict`` 函数做一个了解，解决冲突的方法就藏在里面。

注意该函数的使用时机：在 ``jQuery`` 导入之后，在其他冲突库使用之前。

jQuery.noConflict();
====================

- 在省缺参数的情况下，运行这个函数将变量 ``$`` 的控制权让渡给第一个实现它的库。在运行完这个函数之后，就只能使用 ``jQuery`` 变量访问 ``jQuery`` 对象(函数不带参数)，例如 ``jQuery("div p")`` 。
- 当参数为 ``true`` 时，执行 ``noConflict`` 则会将 ``$`` 和 ``jQuery`` 对象本身的控制权全部移交给第一个产生他们的库。

.. code-block:: js

	var
		_jQuery = window.jQuery, //其它库对window.jQuery定义

		_$ = window.$; // 其它库对window.$定义

	jQuery.noConflict = function( deep ) {
		if ( window.$ === jQuery ) {
			window.$ = _$; // jQuery转移$符号控制权
		}

		if ( deep && window.jQuery === jQuery ) {
			window.jQuery = _jQuery; // jQuery转移jQuery符号控制权
		}

		return jQuery; // 把jQuery对象赋值给新的变量
	};

容易理解的是， ``jQuery`` 通过两个私有变量映射了 ``window`` 环境下的 ``jQuery`` 和 ``$`` 两个对象，以防止变量被强行覆盖。一旦 ``noConflict`` 方法被调用，则通过 ``_jQuery`` , ``_$`` ,  ``jQuery`` , ``$`` 四者之间的差异，来决定控制权的移交方式。

接下来看看参数设定问题，如果 ``deep`` 没有设置， ``_$`` 覆盖了 ``window.$`` ，此时 ``jQuery`` 的别名 ``$`` 失效了，但是 ``jQuery`` 变量未失效，仍可使用。此时如果有其他库或代码重新定义了 ``$`` 变量的话，其控制权就转交出去了。反之 ``deep`` 设置为 ``true`` 时， ``_jQuery`` 进一步覆盖 ``window.jQuery`` ，此时 ``$`` 和 ``jQuery`` 都将失效。

这种操作的好处是，不管是框架混用还是 ``jQuery`` 多版本共存这种高度冲突的执行环境，由于 ``noConflict`` 的控制权移交机制，以及本身返回违背覆盖的私有变量 ``jQuery`` 对象，完全能够通过变量映射的方式解决冲突。

例子
====

将 $ 引用的对象映射回原始的对象
-----------------------------

.. code-block:: js

	jQuery.noConflict();
	// 使用 jQuery
	jQuery("div p").hide();
	// 使用其他库的 $()
	$("content").style.display = 'none';

恢复使用别名 ``$`` ,然后创建并执行一个闭包函数，在这个函数的作用域中仍然将 ``$`` 作为 ``jQuery`` 的别名来使用。在这个函数中，原来的 ``$`` 对象是无效的。这个函数对于大多数不依赖于其他库的插件都十分有效。

.. code-block:: js

	jQuery.noConflict();
	(function($) {
	  　$(function() {
	    　　　// 使用 $ 作为 jQuery 别名的代码
	　　　　　　//......
	  　});
	})(jQuery);
	// 其他用 $ 作为别名的库的代码
	//......

可以将 jQuery.noConflict() 与简写的 ready 结合，使代码更紧凑
---------------------------------------------------------

.. code-block:: js

	jQuery.noConflict()(function(){
	    // ready回调函数中使用 jQuery 的代码
	});

	... // 其他库使用 $ 做别名的代码

创建一个新的别名用来在接下来的库中使用jQuery对象
---------------------------------------------

.. code-block:: js

	var j = jQuery.noConflict();
	// 基于 jQuery 的代码
	j("div p").hide();
	// 基于其他库的 $() 代码
	$("content").style.display = 'none';

完全将jQuery移到一个新的命名空间
------------------------------

.. code-block:: js

var dom = {};
dom.query = jQuery.noConflict(true);
// 新 jQuery 的代码
dom.query("div p").hide();
// 另一个库 $() 的代码
$("content").style.display = 'none';
// 另一个版本 jQuery 的代码
jQuery("div > p").hide();

