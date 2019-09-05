***************
RequireJS和AMD规范
***************

概述
====
先想一想，为什么模块很重要？

因为有了模块，我们就可以更方便地使用别人的代码，想要什么功能，就加载什么模块。

但是，这样做有一个前提，那就是大家必须以同样的方式编写模块，否则你有你的写法，我有我的写法，岂不是乱了套！考虑到 ``Javascript`` 模块现在还没有官方规范，这一点就更重要了。

目前，通行的 ``Javascript`` 模块规范共有两种： `CommonJS <http://wiki.commonjs.org/wiki/Modules/1.1>`_ 和 `AMD <https://github.com/amdjs/amdjs-api/wiki/AMD>`_ 。我主要介绍 ``AMD`` ，但是要先从 ``CommonJS`` 讲起。

CommonJS
--------
``node.js`` 的模块系统，就是参照 ``CommonJS`` 规范实现的。在 ``CommonJS`` 中，有一个全局性方法 ``require()`` ，用于加载模块。假定有一个数学模块 ``math.js`` ，就可以像下面这样加载。

.. code-block:: js

    var math = require('math');

然后，就可以调用模块提供的方法：

.. code-block:: js

	var math = require('math');
	math.add(2,3); // 5

用 ``require()`` 用于加载模块就行了。

AMD
----
有了服务器端模块以后，很自然地，大家就想要客户端模块。而且最好两者能够兼容，一个模块不用修改，在服务器和浏览器都可以运行。

但是，由于一个重大的局限，使得 ``CommonJS`` 规范不适用于浏览器环境。还是上一节的代码，如果在浏览器中运行，会有一个很大的问题，你能看出来吗？

.. code-block:: js

	var math = require('math');
	math.add(2,3); // 5

第二行 ``math.add(2, 3)`` ，在第一行 ``require('math')`` 之后运行，因此必须等 ``math.js`` 加载完成。也就是说，如果加载时间很长，整个应用就会停在那里等。

这对服务器端不是一个问题，因为所有的模块都存放在本地硬盘，可以同步加载完成，等待时间就是硬盘的读取时间。但是，对于浏览器，这却是一个大问题，因为模块都放在服务器端，等待时间取决于网速的快慢，可能要等很长时间，浏览器处于"假死"状态。

因此，浏览器端的模块，不能采用"同步加载"（synchronous），只能采用"异步加载"（asynchronous）。这就是 ``AMD`` 规范诞生的背景。

`AMD <https://github.com/amdjs/amdjs-api/wiki/AMD>`_ 是 "Asynchronous Module Definition" 的缩写，意思就是 "异步模块定义" 。它采用异步方式加载模块，模块的加载不影响它后面语句的运行。所有依赖这个模块的语句，都定义在一个回调函数中，等到加载完成之后，这个回调函数才会运行。

``AMD`` 也采用 ``require()`` 语句加载模块，但是不同于 ``CommonJS`` ，它要求两个参数：

.. code-block:: js

    require([module], callback);

第一个参数 ``[module]`` ，是一个数组，里面的成员就是要加载的模块；第二个参数 ``callback`` ，则是加载成功之后的回调函数。如果将前面的代码改写成 ``AMD`` 形式，就是下面这样：

.. code-block:: js

	require(['math'], function (math) {

	　　　　math.add(2, 3);

	});

``math.add()`` 与 ``math`` 模块加载不是同步的，浏览器不会发生假死。所以很显然， ``AMD`` 比较适合浏览器环境。

目前，主要有两个 ``Javascript`` 库实现了 ``AMD`` 规范： `require.js <http://requirejs.org/>`_  `中文文档 <http://www.requirejs.cn/>`_ 和 `curl.js <https://github.com/cujojs/curl>`_ 。本系列的第三部分，将通过介绍 ``require.js`` ，进一步讲解 ``AMD`` 的用法，以及如何将模块化编程投入实战。

``RequireJS`` 是一个工具库，主要用于客户端的模块管理。它可以让客户端的代码分成一个个模块，实现异步或动态加载，从而提高代码的性能和可维护性。它的模块管理遵守 ``AMD`` 规范（Asynchronous Module Definition）。

``RequireJS`` 的基本思想是，通过 ``define`` 方法，将代码定义为模块；通过 ``require`` 方法，实现代码的模块加载。

首先，将 ``require.js`` 嵌入网页，然后就能在网页中进行模块化编程了。

.. code-block:: html

    <script data-main="scripts/main" src="scripts/require.js"></script>

上面代码的 ``data-main`` 属性不可省略，用于指定主代码所在的脚本文件，在上例中为 ``scripts`` 子目录下的 ``main.js`` 文件。用户自定义的代码就放在这个 ``main.js`` 文件中。

``main.js`` ，我把它称为"主模块"，意思是整个网页的入口代码。它有点像 ``C`` 语言的 ``main()`` 函数，所有代码都从这儿开始运行。

define方法：定义模块
-------------------
``define`` 方法用于定义模块， ``RequireJS`` 要求每个模块放在一个单独的文件里。

按照是否依赖其他模块，可以分成两种情况讨论。第一种情况是定义独立模块，即所定义的模块不依赖其他模块；第二种情况是定义非独立模块，即所定义的模块依赖于其他模块。

独立模块
^^^^^^^^
如果被定义的模块是一个独立模块，不需要依赖任何其他模块，可以直接用 ``define`` 方法生成。

.. code-block:: js

	define({
	    method1: function() {},
	    method2: function() {},
	});

上面代码生成了一个拥有 ``method1`` 、 ``method2`` 两个方法的模块。

另一种等价的写法是，把对象写成一个函数，该函数的返回值就是输出的模块。

.. code-block:: js

	define(function () {
		return {
		    method1: function() {},
			method2: function() {},
	    };
	});

后一种写法的自由度更高一点，可以在函数体内写一些模块初始化代码。

值得指出的是， ``define`` 定义的模块可以返回任何值，不限于对象。

非独立模块
^^^^^^^^^
如果被定义的模块需要依赖其他模块，则 ``define`` 方法必须采用下面的格式。

.. code-block:: js

	define(['module1', 'module2'], function(m1, m2) {
	   ...
	});

``define`` 方法的第一个参数是一个数组，它的成员是当前模块所依赖的模块。比如，['module1', 'module2']表示我们定义的这个新模块依赖于 ``module1`` 模块和 ``module2`` 模块，只有先加载这两个模块，新模块才能正常运行。一般情况下， ``module1`` 模块和 ``module2`` 模块指的是，当前目录下的 ``module1.js`` 文件和 ``module2.js`` 文件，等同于写成 ``['./module1', './module2']`` 。

``define`` 方法的第二个参数是一个函数，当前面数组的所有成员加载成功后，它将被调用。它的参数与数组的成员一一对应，比如 ``function(m1, m2)`` 就表示，这个函数的第一个参数 ``m1`` 对应 ``module1`` 模块，第二个参数 ``m2`` 对应 ``module2`` 模块。这个函数必须返回一个对象，供其他模块调用。

.. code-block:: js

	define(['module1', 'module2'], function(m1, m2) {

	    return {
	        method: function() {
	            m1.methodA();
				m2.methodB();
	        }
	    };

	});

上面代码表示新模块返回一个对象，该对象的 ``method`` 方法就是外部调用的接口， ``menthod`` 方法内部调用了 ``m1`` 模块的 ``methodA`` 方法和 ``m2`` 模块的 ``methodB`` 方法。

需要注意的是，回调函数必须返回一个对象，这个对象就是你定义的模块。

如果依赖的模块很多，参数与模块一一对应的写法非常麻烦。

.. code-block:: js

	define(
	    [       'dep1', 'dep2', 'dep3', 'dep4', 'dep5', 'dep6', 'dep7', 'dep8'],
	    function(dep1,   dep2,   dep3,   dep4,   dep5,   dep6,   dep7,   dep8){
	        ...
	    }
	);

为了避免像上面代码那样繁琐的写法， ``RequireJS`` 提供一种更简单的写法。

.. code-block:: js

	define(
	    function (require) {
	        var dep1 = require('dep1'),
	            dep2 = require('dep2'),
	            dep3 = require('dep3'),
	            dep4 = require('dep4'),
	            dep5 = require('dep5'),
	            dep6 = require('dep6'),
	            dep7 = require('dep7'),
	            dep8 = require('dep8');

	            ...
	    }

	});

下面是一个 ``define`` 实际运用的例子。

.. code-block:: js

	define(['math', 'graph'],
	    function ( math, graph ) {
			return {
	            plot: function(x, y){
	                return graph.drawPie(math.randomGrid(x,y));
	            }
	        }
	    };
	);

上面代码定义的模块依赖 ``math`` 和 ``graph`` 两个库，然后返回一个具有 ``plot`` 接口的对象。

另一个实际的例子是，通过判断浏览器是否为 ``IE`` ，而选择加载 ``zepto`` 或 ``jQuery`` 。

.. code-block:: js

	define(('__proto__' in {} ? ['zepto'] : ['jquery']), function($) {
	    return $;
	});

上面代码定义了一个中间模块，该模块先判断浏览器是否支持 ``__proto__`` 属性（除了 ``IE`` ，其他浏览器都支持），如果返回 ``true`` ，就加载 ``zepto`` 库，否则加载 ``jQuery`` 库。

require方法：调用模块
--------------------
``require`` 方法用于调用模块。它的参数与 ``define`` 方法类似。

.. code-block:: js

	require(['foo', 'bar'], function ( foo, bar ) {
	        foo.doSomething();
	});

上面方法表示加载 ``foo`` 和 ``bar`` 两个模块，当这两个模块都加载成功后，执行一个回调函数。该回调函数就用来完成具体的任务。

``require`` 方法的第一个参数，是一个表示依赖关系的数组。这个数组可以写得很灵活，请看下面的例子。

.. code-block:: js

	require( [ window.JSON ? undefined : 'util/json2' ], function ( JSON ) {
	  JSON = JSON || window.JSON;

	  console.log( JSON.parse( '{ "JSON" : "HERE" }' ) );
	});

上面代码加载 ``JSON`` 模块时，首先判断浏览器是否原生支持 ``JSON`` 对象。如果是的，则将 ``undefined`` 传入回调函数，否则加载 ``util`` 目录下的 ``json2`` 模块。

``require`` 方法也可以用在 ``define`` 方法内部。

.. code-block:: js

	define(function (require) {
	   var otherModule = require('otherModule');
	});

下面的例子显示了如何动态加载模块。

.. code-block:: js

	define(function ( require ) {
	    var isReady = false, foobar;

	    require(['foo', 'bar'], function (foo, bar) {
	        isReady = true;
	        foobar = foo() + bar();
	    });

	    return {
	        isReady: isReady,
	        foobar: foobar
	    };
	});

上面代码所定义的模块，内部加载了 ``foo`` 和 ``bar`` 两个模块，在没有加载完成前， ``isReady`` 属性值为 ``false`` ，加载完成后就变成了 ``true`` 。因此，可以根据 ``isReady`` 属性的值，决定下一步的动作。

下面的例子是模块的输出结果是一个 ``promise`` 对象。

.. code-block:: js

	define(['lib/Deferred'], function( Deferred ){
	    var defer = new Deferred();
	    require(['lib/templates/?index.html','lib/data/?stats'],
	        function( template, data ){
	            defer.resolve({ template: template, data:data });
	        }
	    );
	    return defer.promise();
	});

上面代码的 ``define`` 方法返回一个 ``promise`` 对象，可以在该对象的 ``then`` 方法，指定下一步的动作。

如果服务器端采用 ``JSONP`` 模式，则可以直接在 ``require`` 中调用，方法是指定 ``JSONP`` 的 ``callback`` 参数为 ``define`` 。

.. code-block:: js

	require( [
	    "http://someapi.com/foo?callback=define"
	], function (data) {
	    console.log(data);
	});

``require`` 方法允许添加第三个参数，即错误处理的回调函数。

.. code-block:: js

	require(
	    [ "backbone" ],
	    function ( Backbone ) {
	        return Backbone.View.extend({ /* ... */ });
	    },
	    function (err) {
			// ...
	    }
	);

``require`` 方法的第三个参数，即处理错误的回调函数，接受一个 ``error`` 对象作为参数。

``require`` 对象还允许指定一个全局性的 ``Error`` 事件的监听函数。所有没有被上面的方法捕获的错误，都会被触发这个监听函数。

.. code-block:: js

	requirejs.onError = function (err) {
	    // ...
	};

AMD模式小结
-----------
``define`` 和 ``require`` 这两个定义模块、调用模块的方法，合称为 ``AMD`` 模式。它的模块定义的方法非常清晰，不会污染全局环境，能够清楚地显示依赖关系。

``AMD`` 模式可以用于浏览器环境，并且允许非同步加载模块，也可以根据需要动态加载模块。

配置require.js：config方法
==========================
``require`` 方法本身也是一个对象，它带有一个 ``config`` 方法，用来配置 ``require.js`` 运行参数。 ``config`` 方法接受一个对象作为参数。

.. code-block:: js

	require.config({
	    paths: {
	        jquery: [
	            '//cdnjs.cloudflare.com/ajax/libs/jquery/2.0.0/jquery.min.js',
	            'lib/jquery'
	        ]
	    }
	});

``config`` 方法的参数对象有以下主要成员：

paths
------
``paths`` 参数指定各个模块的位置。这个位置可以是同一个服务器上的相对位置，也可以是外部网址。可以为每个模块定义多个位置，如果第一个位置加载失败，则加载第二个位置，上面的示例就表示如果 ``CDN`` 加载失败，则加载服务器上的备用脚本。需要注意的是，指定本地文件路径时，可以省略文件最后的 ``js`` 后缀名。

.. code-block:: js

	require(["jquery"], function($) {
	    // ...
	});

上面代码加载 ``jquery`` 模块，因为 ``jquery`` 的路径已经在 ``paths`` 参数中定义了，所以就会到事先设定的位置下载。

baseUrl
-------
``baseUrl`` 参数指定本地模块位置的基准目录，即本地模块的路径是相对于哪个目录的。该属性通常由 ``require.js`` 加载时的 ``data-main`` 属性指定。

shim
----
有些库不是 ``AMD`` 兼容的，这时就需要指定 ``shim`` 属性的值。 ``shim`` 可以理解成“垫片”，用来帮助 ``require.js`` 加载非 ``AMD`` 规范的库。

.. code-block:: js

	require.config({
	    paths: {
	        "backbone": "vendor/backbone",
	        "underscore": "vendor/underscore"
	    },
	    shim: {
	        "backbone": {
	            deps: [ "underscore" ],
	            exports: "Backbone"
	        },
	        "underscore": {
	            exports: "_"
	        }
	    }
	});

上面代码中的 ``backbone`` 和 ``underscore`` 就是非 ``AMD`` 规范的库。 ``shim`` 指定它们的依赖关系（ ``backbone`` 依赖于 ``underscore`` ），以及输出符号（ ``backbone`` 为 ``Backbone`` ， ``underscore`` 为 ``_`` ）。

插件
====
``RequireJS`` 允许使用插件，加载各种格式的数据。完整的插件清单可以查看 `官方网站 <https://github.com/jrburke/requirejs/wiki/Plugins>`_ 。

下面是插入文本数据所使用的 ``text`` 插件的例子。

.. code-block:: js

	define([
	    'backbone',
	    'text!templates.html'
	], function( Backbone, template ){
	   // ...
	});

上面代码加载的第一个模块是 ``backbone`` ，第二个模块则是一个文本，用 ``'text!'`` 表示。该文本作为字符串，存放在回调函数的 ``template`` 变量中。

优化器r.js
==========
http://javascript.ruanyifeng.com/tool/requirejs.html
