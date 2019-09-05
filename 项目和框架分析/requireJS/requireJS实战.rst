****************
requireJS 从概念到实战
****************

.. contents:: 目录
   :depth: 4

``requireJS`` 可以很轻易的将一个项目中的 ``JavaScript`` 代码分割成若干个模块( ``module`` )。并且 ``requireJS`` 推荐一个模块就是一个文件，所以，你将获得一些零碎的具有互相依赖关系的 ``JS`` 文件。模块化的好处也浅显意见，那就是大大增强代码的可读性、易维护性、可扩展性、减少全局污染等。

基本概念
========
因为自身设计的不足， ``JavaScript`` 这门语言实际上并没有模块化这种概念与机制，所以想实现如 ``JAVA`` ， ``PHP`` 等一些后台语言的模块化开发，那么我们必须借助 ``requireJS`` 这个前端模拟模块化的插件，虽然我们不需要去了解它的实现原理，但是大致去了解它是如何工作的，我相信这会让我们更容易上手。

- ``requireJS`` 使用 ``head.appendChild()`` 将每一个依赖加载为一个 ``script`` 标签。
- ``requireJS`` 等待所有的依赖加载完毕，计算出模块定义函数正确调用顺序，然后依次调用它们。

requireJS的历史发展
==================
在说 ``JavaScript`` 模块化之前，我们不得不提 ``CommonJS`` （原名叫ServerJs），这个社区可谓大牛云集，他们为 ``NodeJS`` 制定过模块化规范 ``Modules/1.0`` ，并得到了广泛的支持。在为 ``JavaScript`` 定制模块化规范时，讨论的都是在 ``Modules/1.0`` 上进行改进，但是 ``Modules/1.0`` 是专门为服务端制定的规范，所以要想套用在客服端环境的 ``JS`` 上，情况就会有很大的不同，例如，对于服务端加载一个 ``JS`` 文件，其耗费的时间几乎都是可以忽略不计的，因为这些都是基于本地环境，而在客户端浏览器上加载一个文件，都会发送一个 ``HTTP`` 请求，并且还可能会存在跨域的情况，也就是说资源的加载，到执行，是会存在一定的时间消耗与延迟。

所以社区的成员们意识到，要想在浏览器环境中也能模块化开发，则需要对现有规范进行更改，而就在社区讨论制定规范的时候内部发生了比较大的分歧，分裂出了三个主张，渐渐的形成三个不同的派别：

1.Modules/1.x派
----------------
这一波人认为，在现有基础上进行改进即可满足浏览器端的需要，既然浏览器端需要 ``function`` 包装，需要异步加载，那么新增一个方案，能把现有模块转化为适合浏览器端的就行了，有点像“保皇派”。基于这个主张，制定了 `Modules/Transport <http://wiki.commonjs.org/wiki/Modules/Transport>`_ 规范，提出了先通过工具把现有模块转化为符合浏览器上使用的模块，然后再使用的方案。

``browserify`` 就是这样一个工具，可以把 ``nodejs`` 的模块编译成浏览器可用的模块。（ ``Modules/Transport`` 规范晦涩难懂，我也不确定 ``browserify`` 跟它是何关联，有知道的朋友可以讲一下）

目前的最新版是 `Modules/1.1.1 <http://wiki.commonjs.org/wiki/Modules/1.1.1>`_ ，增加了一些 ``require`` 的属性，以及模块内增加 ``module`` 变量来描述模块信息，变动不大。

2.Modules/Async派
------------------
这一波人有点像“革新派”，他们认为浏览器与服务器环境差别太大，不能沿用旧的模块标准。既然浏览器必须异步加载代码，那么模块在定义的时候就必须指明所依赖的模块，然后把本模块的代码写在回调函数里。模块的加载也是通过下载-回调这样的过程来进行，这个思想就是 ``AMD`` 的基础，由于“革新派”与“保皇派”的思想无法达成一致，最终从 ``CommonJs`` 中分裂了出去，独立制定了浏览器端的 ``js`` 模块化规范 `AMD(Asynchronous Module Definition) <https://github.com/amdjs/amdjs-api/wiki/AMD>`_

3.Modules/2.0派
----------------
这一波人有点像“中间派”，既不想丢掉旧的规范，也不想像 ``AMD`` 那样推到重来。他们认为， ``Modules/1.0`` 固然不适合浏览器，但它里面的一些理念还是很好的，（如通过 ``require`` 来声明依赖），新的规范应该兼容这些， ``AMD`` 规范也有它好的地方（例如模块的预先加载以及通过 ``return`` 可以暴漏任意类型的数据，而不是像 ``commonjs`` 那样 ``exports`` 只能为 ``object`` ），也应采纳。最终他们制定了一个 `Modules/Wrappings <http://wiki.commonjs.org/wiki/Modules/Wrappings>`_ 规范，此规范指出了一个模块应该如何“包装”。

实际上这三个流派谁都没有胜过谁，反而是最后的 ``AMD`` , ``CMD`` 规范扎根在这三个流派之上，吸取它们提出的优点不断得到壮大。

总的来说 ``AMD`` , ``CMD`` 都是从 ``commonJS`` 规范中结合浏览器现实情况，并且吸收三大流派的优点而诞生。其中 ``CMD`` 是国内大牛制定的规范，其实现的工具是 ``seaJS`` ，而 ``AMD`` 则是国外大牛制定的，其实现技术则是 ``requireJS`` 。

模块化的优点
===========
既然我们已经详细的了解了“前端模块化”的历史与发展，那么我们也要大致了解模块开发的好处，毕竟这是我们学习的动力。

.. code-block:: shell

	1. 作用域污染
	    小明定义了 var name = 'xiaoming';
	    N ~ 天之后：
	    小王又定义了一个 var name = 'xiaowang';

	2.  防止代码暴漏可被修改：
	    为了解决全局变量的污染，早期的前端的先驱们则是以对象封装的方式来写JS代码：
	    var utils = {
	        'version':'1.3'
	    };
	    然而这种方式不可以避免的是对象中的属性可被直接修改：utils.version = 2.0 。

	3. 维护成本的提升。
	   如果代码毫无模块化可言，那么小明今天写的代码，若干天再让小明自己去看，恐怕也无从下手。


	4. 复用与效率
	   模块与非模块的目的就是为了复用，提高效率


总的来说，前端的模块化就是在眼瞎与手残的过程进行发展的，大致我们可以总结一下几时代：

1. 无序(洪荒时代) ：自由的书写代码。
2. 函数时代 ：将代码关入了笼子之中。
3. 面向对象的方式。
4. 匿名自执行函数：其典型的代表作就是JQ。
5. 伪模块开发（CMD/AMD）
6. 模块化开发（还未诞生的ES6标准）

我们相信未来必将更加光明，但是回顾现在，特别是在国内的市场环境中IE浏览器依然占据半壁江山，所以基于 ES6 的模块特性依然任重道远，因此，在光明还未播撒的时刻，就让我们率先点燃一朵火苗照亮自己，而这朵火苗就是 ———— ``requireJS`` 。

require 实战
============
下面我将化整为零的去讲解 ``requireJS`` 在一个项目的具体使用方式以及需要注意的事项。

引入requireJS
-------------
通过 ``<script>`` 标签，将 ``require.js`` 文件引入到当前的 ``HTML`` 页面中。

.. code-block:: html

	<!DOCTYPE html>
	<html lang="en">
	<head>
	    <meta charset="UTF-8">
	    <title>RequireJS 实战</title>
	</head>
	<body>
	    <script src="js/require.js"></script>
	</body>
	</html>

参数配置
--------
``requireJS`` 常用的方法与命令也就两个，因此 ``requireJS`` 使用起来非常简单。

- require
- define

其中 ``define`` 是用于定义模块，而 ``require`` 是用于载入模块以及载入配置文件。

在 ``requireJS`` 中一个文件就是一个模块，并且文件名就是该模块的 ``ID`` ，其表现则是以 ``key/value`` 的键值对格式， ``key`` 即模块的名称（模块ID），而 ``value`` 则是文件（模块）的地址，因此多个模块便有多个键值对值，这些键值对再加上一些常用的参数，便是 ``require`` 的配置参数，这些配置参数我们通常会单独保存在一个 ``JS`` 文件中，方便以后修改、调用，所以这个文件我们也称之为“配置文件”。

下面是 ``requireJS`` 的基本参数配置：

.. code-block:: html

	//index.html
	<script>
	require.config({
	    baseUrl:'js/',
	    paths:{
	        'jquery':'http://xxxx.xxx.com/js/jquery.min',
	        'index':'index'
	    }
	});

	require(['index']);
	</script>

``require.config()`` 是用于配置参数的核心方法，它接收一个有固定格式与属性的对象作为参数，这个对象便是我们的配置对象。

在配置对象中 ``baseUrl`` 定义了基准目录，它会与 ``paths`` 中模块的地址自动进行拼接，构成该模块的实际地址，并且当配置参数是通过 ``script`` 标签嵌入到 ``html`` 文件中时， ``baseUrl`` 默认的指向路径就是该 ``html`` 文件所处的地址。

``paths`` 属性的值也是一个对象，该对象保存的就是模块 ``key/value`` 值。其中 ``key`` 便是模块的名称与 ``ID`` ，一般使用文件名来命名，而 ``value`` 则是模块的地址，在 ``requireJS`` 中，当模块是一个 ``JS`` 文件时，是可以省略 ``.js`` 的扩展名，比如 ``index.js`` 就可以直接写成 ``index`` 而当定义的模块不需要与 ``baseUrl`` 的值进行拼接时，可以通过 ``/`` 与 ``http://`` 路径值以及 ``.js`` 的形式来绕过 ``baseUrl`` 的设定。当加载纯 ``.js`` 文件(以 ``.js`` 结尾)，不会使用 ``baseUrl`` 。

示例：

.. code-block:: js

	require.config({
	    baseUrl:'js/',
	    paths:{
	        'jquery':'http://xxx.xxxx.com/js/jquery.min',
	        'index':'index'
	    }
	});
	require(['index']);

实际上，除了可以在 ``require.js`` 加载完毕后，通过 ``require.config()`` 方法去配置参数，我们也可以在 ``require.js`` 加载之前，定义一个全局的对象变量 ``require`` 来事先定义配置参数。然后在 ``require.js`` 被浏览器加载完毕后，便会自动继承之前配置的参数。这时候就只能通过声明一个全局的变量来注入配置参数来实现了。

.. code-block:: html

	<script>
	    var require = {
	        baseUrl: 'js/',
	        paths: {
	            'jquery': 'http://xxx.xxxx.com/js/jquery.min',
	            'index': 'index'
	        },
	        deps:[index]
	    };
	</script>
	<script src="js/require.js"></script>

不论是在 ``require.js`` 加载之前定义配置参数，还是之后来定义，这都是看看我们需求而言的，这里我们举例的配置参数都是放入到 ``script`` 标签中，然后嵌入到 ``HTML`` 页面的内嵌方式，在实际使用时，我们更多的则是将该段配置提取出来单独保存在一个文件中，并将其取名为 ``app.js`` ，而这个 ``app.js`` 便是我们后面常说到的配置文件。

另外还有一个“接口文件”的概念， ``requireJS`` 中，所谓接口文件指的便是 ``require.js`` 加载完毕后第一个加载的模块文件。

加载配置文件
-----------
现在我们知道 ``require`` 的配置有两种加载方式，一种是放入到 ``script`` 标签嵌入到 ``html`` 文件中，另一种则是作为配置文件 ``app.js`` 来独立的引入。

独立的引入配置文件也有两种方式，一种是通过 ``script`` 标签加载外部 ``JS`` 文件形式：

.. code-block::  html

	<script src="js/require.js"></script>
	<script src="js/app.js"></script>

另一种方式则是使用 ``require`` 提供的 ``data-main`` 属性，该属性是直接写在引入 ``require.js`` 的 ``script`` 标签上，在 ``require.js`` 加载完毕时，会自动去加载配置文件 ``app.js`` 。

.. code-block:: html

    <script data-main="js/app" src="js/require.js"></script>

通过 ``data-main`` 去加载入口文件，便会使配置对象中的 ``baseUrl`` 属性默认指向地址改为 ``app.js`` 所在的位置，相比之下我更加推荐这种方式，因为它更可能的方便快捷。

当我们的项目足够的庞大时，我也会推荐将入口文件作为一个普通的模块，然后在这个模块中，根据业务的不同再去加载不同的配置文件。

.. code-block:: js

	//define.js
	define(['app1','app2','app3','app4'],function(app1,app2,app3,app4){
	    if(page == 'app1'){
	        require.config(app1);
	    }else if(page == 'app2'){
	        require.config(app2);
	    }else if(page == 'app3'){
	        require.config(app3);
	    }else{
	        require.config(app4);
	    }
	})

当然关于模块的定义和载入我们后面会详细的讲解到，这里只需要有一个概念即可。

定义模块
--------
在我们选择 ``requireJS`` 来模块化开发我们的项目或者页面时，就要明确的知道我们以后所编写的代码或者是某段功能，都是要放在一个个定义好的模块中。

下面是 ``requireJS`` 定义模块的方法格式：

.. code-block:: shell

    define([id,deps,] callback);

- ``ID`` :模块的 ``ID`` ，默认的便是文件名，一般无需使用者自己手动指定。
- ``deps`` :当前模块所以依赖的模块数组，数组的每个数组元素便是模块名或者叫模块 ``ID`` 。
-  ``callback`` :模块的回调方法，用于保存模块具体的功能与代码，而这个回调函数又接收一个或者多个参数，这些参数会与模块数组的每个数组元素一一对应，即每个参数保存的是对应模块返回值。

根据 ``define()`` 使用时参数数量的不同，可以定义以下几种模块类型：

简单的值对
^^^^^^^^^
当所要定义的模块没有任何依赖也不具有任何的功能，只是单纯的返回一组键值对形式的数据时，便可以直接将要返回的数据对象写在 ``define`` 方法中：

.. code-block:: js

	define({
	    'color':'red',
	    'size':'13px',
	    'width':'100px'
	});

这种只为保存数据的模块，我们称之为“值对”模块，实际上值对模块不仅可以用于保存数据，还可以保存我们的配置参数，然后在不同的业务场景下去加载不同的配置参数文件。

示例：

.. code-block:: js

	//app1.js
	define({
	    baseUrl:'music/js/',
	    paths:{
	        msuic:'music',
	        play:'play'
	    }
	});

	//app2.js
	define({
	    baseUrl:'video/js/',
	    paths:{
	        video:'video',
	        play:'play'
	    }
	});

非依赖的函数式定义
^^^^^^^^^^^^^^^^^
如果一个模块没有任何的依赖，只是单纯的执行一些操作，那么便可以直接将函数写在 ``define`` 方法中：

.. code-block:: js

	define(function(require,exports,modules){
	    // do something
	    return {
	    'color':'red',
	    'size':'13px'
	    }
	});

依赖的函数式定义
^^^^^^^^^^^^^^^
这种带有依赖的函数式模块定义，也是我们平时常用到的，这里我们就结合实例，通过上面所举的 ``index`` 模块为例：

.. code-block:: js

	//index.js
	define(['jquery','./utils'], function($) {
	    $(function() {
	        alert($);
	    });
	});

从上面的示例中我们可以看出 ``index`` 模块中，依赖了 ``jquery`` 模块，并且在模块的回调函数中，通过 ``$`` 形参来接收 ``jquery`` 模块返回的值，除了 ``jquery`` 模块， ``index`` 模块还依赖了 ``utils`` 模块，因为该模块没有在配置文件中定义，所以这里以附加路径的形式单独引入进来的。

载入模块
-------
在说载入模块之前，我们先聊聊“模块依赖”。模块与模块之间存在着相互依赖的关系，因此就决定了不同的加载顺序，比如模块 ``A`` 中使用到的一个函数是定义在模块 ``B`` 中的，我们就可以说模块 ``A`` 依赖模块 ``B`` ，同时也说明了在载入模块时，其顺序也是先模块A，再模块 ``B`` 。
在 ``require`` 中，我们可以通过 ``require()`` 方法去载入模块。其使用格式如下：

.. code-block:: js

    require(deps[,callback]);

- ``deps`` :所要载入的模块数组。
- ``callback`` :模块载入后执行的回调方法。

这里就让我们依然使用上述的 ``index`` 模块为例来说明

示例：

.. code-block:: js

	require.config({
        paths:{
            'index':'index'
        }
    });

    require(['index']);

``requireJS`` 通过 ``require([])`` 方法去载入模块，并执行模块中的回调函数，其值是一个数组，数组中的元素便是要载入的模块名称也就是模块 ``ID`` ，这里我们通过 ``require(['index'])`` 方法载入了 ``index`` 这个模块，又因为该模块依赖了 ``jquery`` 模块，所以接着便会继续载入 ``jquery`` 模块，当 ``jquery`` 模块加载完成后，便会将自身的方法传递给形参 ``$`` 最后执行模块的回调方法， ``alert`` 出 ``$`` 参数具体内容。

这里我们可以小小的总结一下，实现模块的载入除了 ``require([],fn)`` 的主动载入方法，通过依赖也可以间接载入对应的模块，但是相比较而言 ``require`` 方式载入模块在使用上更加灵活，它不仅可以只载入模块不执行回调，也可以载入模块然后执行回调，还可以在所定义的模块中，按需载入所需要用到的模块，并且将模块返回的对象或方法中保存在一个变量中，以供使用。

这种按需载入模块，也叫就近依赖模式，它的使用要遵循一定的使用场景:

当模块是非依赖的函数式时，可以直接使用

.. code-block:: js

	define(function(require,exports,modules){
	    var utils = require('utils');
	    utils.sayHellow('hellow World')
	})

当模块是具有依赖的函数式时，只能够以回调的形式处理。

.. code-block:: js

	define(['jquery'], function($) {
	    $(function() { // 为什么要封装一个匿名函数中
	        require(['utils'],function(utils){
	            utils.sayHellow('Hellow World!');
	        });
	    });
	});

当然聪明伶俐的你，一定会想到这样更好的办法：

.. code-block:: js

	define(['jquery','require','exports','modules'], function($,require,exports,modules) {
	    $(function() {
	        //方式一
	        require(['utils'],function(utils){
	            utils.sayHellow('Hellow World!');
	        });
	        //方式二：
	        var utils = require('utils');
	        utils.sayHellow('hellow World')
	    });
	});

模块的返回值
-----------
``require`` 中定义的模块不仅可以返回一个对象作为结果，还可以返回一个函数作为结果。实现模块的返回值主要有两种方法：

return 方式
^^^^^^^^^^^

.. code-block:: js

	// utils.js
	define(function(require,exports,modules){
	    function sayHellow(params){
	        alert(params);
	    }

	    return sayHellow
	});

	// index.js
	define(function(require,exports,modules){
	    var sayHellow = require('utils');
	    sayHellow('hellow World');
	})

如果通过 ``return`` 返回多种结果的情况下：

.. code-block:: js

	// utils.js
	define(function(require,exports,modules){
	    function sayHellow(params){
	        alert(params);
	    }

	    function sayBye(){
	        alert('bye-bye！');
	    }

	    return {
	        'sayHellow':sayHellow,
	        'sayBye':sayBye
	    }
	});

	// index.js
	define(function(require,exports,modules){
	    var utils = require('utils');
	    utils.sayHellow('hellow World');
	})

exports导出
^^^^^^^^^^^

.. code-block:: js

	// utils.js
	define(function(require,exports,modules){
	    function sayHellow(params){
	        alert(params);
	    }
	    exports.sayHellow = sayHellow;
	})

	// index.js
	define(function(require,exports,modules){
	    var utils = require('utils');
	    utils.sayHellow('hellow World');
	});

这里有一个注意的地方，那就是非依赖性的模块，可以直接在模块的回调函数中，加入以下三个参数：

- ``require`` :加载模块时使用。
- ``exports`` :导出模块的返回值。
- ``modules`` :定义模块的相关信息以及参数。

非标准模块定义
-------------
在 ``require.config()`` 方法的配置对象中有一个 ``shim`` 属性，它的值是一个对象，可以用于声明非标准模块的依赖和返回值。

所谓的 “非标准模块” 指的是那些不符合的 ``AMD`` 规范的 ``JS`` 插件。

下面我们先看看基本的 ``shim`` 配置参数：

.. code-block:: js

	require.config({
	    baseUrl:'js/',
	    paths:{
	        'jquery':'http://xxx.xxxx.com/js/jquery.min',
	        'index':'index',
	        'say':'say',
	        'bar':'bar',
	        'tools':'tools'
	    },
	    shim:{
	        'tools':{
	            deps:['bar'],
	            exports:'tool'
	        },
	        'say':{
	            deps:['./a','./b'],
	            init:function(){
	                return {
	                    'sayBye':sayBye,
	                    'sayHellow':sayHellow
	                }
	            }
	        }
	    }
	});

	require(['index']);

这里需要注意的是如果所加载的模块文件是符合 ``AMD`` 规范，比如通过 ``define`` 进行定义的，那么 ``require`` 默认的优先级将是标准的，只有在不符合标准的情况下才会采用 ``shim`` 中定义的参数。

在 ``index`` 模块执行时：

.. code-block:: js

	define(['jquery','tool','say'],function($,tool,say){
	    tool.drag();
	    say.sayHellow();
	    say.sayBye();
	})

上面的示例中，关于 ``shim`` 中有三个重要的属性，它们分别是：

- ``deps`` : 用于声明当前非标准模块所依赖的其它模块，值是一个数组，数组元素是模块的名称或者是ID。
- ``exports`` :用于定义非标准模块的全局变量或者方法。值一般是一个字符串。
- ``init`` :用于初始，处理，非标准模块的全局变量或者是方法，常用于当非标准模块存在多个全局变量以及方法，值是一个函数。

常用参数
-------
在 ``require.config`` 中常用属性设置。



urlArgs
^^^^^^^
``RequireJS`` 获取资源时附加在 ``URL`` 后面的额外的 ``query`` 参数。作为浏览器或服务器未正确配置时的 ``cache bust`` 手段很有用。使用 ``cache bust`` 配置的一个示例：

.. code-block:: js

    urlArgs: "bust=" + (new Date()).getTime()

在开发中这很有用，但请记得在部署到生成环境之前移除它。

scriptType
^^^^^^^^^^^
指定 ``RequireJS`` 将 ``script`` 标签插入 ``document`` 时所用的 ``type=""`` 值。默认为 ``text/javascript`` 。想要启用 ``Firefox`` 的 ``JavaScript 1.8`` 特性，可使用值 ``text/javascript;version=1.8`` 。

waitSeconds
^^^^^^^^^^^
通过该参数可以设置 ``requireJS`` 在加载脚本时的超时时间，它的默认值是 ``7`` ，即如果一个脚本文件加载时长超过 ``7`` 秒钟，便会放弃等待该脚本文件，从而报出 ``timeout`` 超时的错误信息，考虑到国内网络环境不稳定的因素，所以这里我建议设置为 ``0`` 。当然一般不需要去改动它，除非到了你需要的时候。

deps
^^^^
用于声明 ``require.js`` 在加载完成时便会自动加载的模块，值是一个数组，数组元素便是模块名。

callback
^^^^^^^^
当 ``deps`` 中的自动加载模块加载完毕时，触发的回调函数。

config
^^^^^^^
``config`` 属性可以为模块配置额外的参数设定，其使用格式就是以模块名或者模块 ``ID`` 为 ``key`` ，然后具体的参数为 ``value`` 。

.. code-block:: js

	//app.js
	require.config({
	    baseUrl:'js/',
	    paths:{
	        'jquery':'http://xx.xxxx.com/js/jquery.min',
	        'index':'index'
	    },
	    config:{
	        'index':{
	            'size':13,
	            'color':'red'
	        }
	    }
	});

	//index.js
	define(['jquery','module'],function($,module){
	    console.log(module.config().size) // 获取模块的配置参数
	});

这里要引起我们注意的地方就是依赖的 ``module`` 模块，它是一个预先定义好的值，引入该值，在当前模块下便可以调用 ``module`` 对象，从该对象中执行 ``config()`` 方法便可以生成改模块的参数对象。

map
^^^^
[略]，暂时还未弄明白其具体使用方式，后续会继续保持关注，如果你知晓其作用，麻烦你一定要与我联系。

packages
^^^^^^^^^
[略]，暂时还未弄明白其具体使用方式，后续会继续保持关注，如果你知晓其作用，麻烦你一定要与我联系。

rquire 压缩
===========
``RequireJS`` 会将完整项目的 ``JavaScript`` 代码轻易的分割成苦干个模块( ``module`` )，这样，你将获得一些具有互相依赖关系的 ``JavaScript`` 文件。在开发环境中，这种方式可以让我们的代码更具有模块化与易维护性。但是，在生产环境中将所有的 ``JavaScript`` 文件分离，这是一个不好的做法。这会导致很多次请求( ``requests`` )，即使这些文件都很小，也会浪费很多时间。因此我们可以通过合并这些脚本文件压缩文件的大小，以减少请求的次数与资源的体积来达到节省加载时间的目的，所以这里我们就要提到一个关于 ``requireJS`` 延伸，那就是 ``r.js`` 。

``r.js`` 是一个独立的项目，它作用在 ``nodeJS`` 环境中，可以实现对 ``JS`` 代码的合并压缩。

使用 ``r.js`` 要具有以下几个条件：

1. ``r.js`` 源文件
2. ``bulid.js`` (即属于 ``r.js`` 的配置文件)
3. ``nodeJS`` 环境

``r.js`` 可以直接丢在项目的根目录上， ``build.js`` 是 ``r.js`` 的配置文件，由开发者自己新建，与 ``r.js`` 同目录。其一般的目录结构如下:

.. code-block:: shell

	[project]
		/js
		/css
		/images
		index.html
		r.js
		build.js

`r.js 下载 <https://github.com/requirejs/r.js>`_

``nodeJS`` 环境，以及 ``r.js`` 都好办，重要的就是掌握配置文件的使用 ``-- build.js`` ，下面我们就详细的说说它。

.. code-block:: shell

	({
	    //（选填）app的顶级目录。如果指定该参数，说明您的所有文件都在这个目录下面（包括baseUrl和dir都以这个为根目录）。如果不指定，则以baseUrl参数为准
	    appDir: './',

	     // 输出目录。如果不指定，默认会创建一个build目录
	    dir: 'pack',

	     // 模块所在默认相对目录，如果appDir有指定，则baseUrl相对于appDir。
	    baseUrl: 'js/',
	    paths: {
	        'index': 'index',
	        'a': 'a',
	        'b': 'b',
	        'c': 'c'

	    },

	    //过滤规则，匹配到的文件将不会被输出到输出目录去
	    fileExclusionRegExp:   /^(r|build)\.js|.*\.scss$/,

	     /*
	        JS 文件优化方式，目前支持以下几种：
	        uglify: （默认） 使用 UglifyJS 来压缩代码
	        closure: 使用 Google's Closure Compiler 的简单优化模式
	        closure.keepLines: 使用 closure，但保持换行
	        none: 不压缩代码
	    */
	    optimize: 'none',

	   /*
	    允许优化CSS，参数值：
	    “standard”: @import引入并删除注释，删除空格和换行。删除换行在IE可能会出问题，取决于CSS的类型
	    “standard.keepLines”: 和”standard”一样但是会保持换行
	    “none”: 跳过CSS优化
	    “standard.keepComments”: 保持注释，但是去掉换行(r.js 1.0.8+)
	    “standard.keepComments.keepLines”: 保持注释和换行(r.js 1.0.8+)
	    “standard.keepWhitespace”: 和”standard”一样但保持空格
	    */
	    optimizeCss:   '“standard”',


	    // 是否忽略 CSS 资源文件中的 @import 指令
	    cssImportIgnore: null,

	    //参与压缩的主模块，默认情况下会将paths模块中定义的模块都压缩合并到改模块中，通过exclude 可以排除参与压缩的模块，其中模块的地址都是相对于baseUrl的地址。
	    modules: [{
	        name: 'index',
	        exclude: ['c']
	    }],

	    // 包裹模块
	    wrap: true,

	    // 自定义包裹模块，顾名思义就是使用特定内容去包裹modules指定的合并模块内容，如此一来 define/require 就不再是全局变量，在 end 中可以暴露一些全局变量供整个函数使用
	    wrap: {
	         start: "(function() {",
	         end: "}(window));"
	     },

	    removeCombined: false,

	    //如果shim配置在requirejs运行过程中被使用的话，需要在这里重复声明，这样才能将依赖模块正确引入。
	    shim: {}

	     // 载入requireJS 的配置文件，从而使用其中的paths 以及 shim 属性值。通过指定该属性，可以省去我们在bulid.js中重复定义 paths 与 shim属性。
	    mainConfigFile:"js/app.js",
	})

以上环节都准备好了之后，就可以在终端中允许打包压缩命令: ``node r.js -o build.js`` 。

当执行该命令后， ``r.js`` 会将自身所在目录的所有资源连同目录重新拷贝一份到输出目录(dir)中。然后再输出目录进行最后的合并与压缩操作。

其它问题
========

timeout超时问题
---------------
该问题一般是 ``waitSeconds`` 属性值导致，解决的方法有两个，一个是将 ``waitSeconds`` 的值设置更长时间，比如 ``17s`` ，另一个就是将其值设置为 ``0`` ，让其永不超时。

循环依赖问题
-----------

何为循环依赖？
^^^^^^^^^^^^^
如果存在两个模块， ``moduleA`` 与 ``moduleB`` ，如果 ``moduleA`` 依赖 ``moduleB`` ， ``moduleB`` 也依赖了 ``moduleA`` ，并且这中情况下，便是循环依赖。

循环依赖导致的问题！
^^^^^^^^^^^^^^^^^^
如果两个模块循环依赖，并且 ``A`` 中有调用 ``B`` 中的方法，而 ``B`` 中也有调用 ``A`` 中的方法，那么此时， ``A`` 调用 ``B`` 正常，但是 ``B`` 中调用 ``A`` 方法，则会返回 ``undefined`` 异常。

如何解决循环依赖的问题？
^^^^^^^^^^^^^^^^^^^^^^
通过 ``require([],fn)`` 解决

此时在模块 ``B`` 中，我们通过引入 ``require`` 依赖，然后再通过 ``require()`` 方法去载入模块 ``A`` ，并在回调中去执行。

.. code-block:: js

	define(['require','jquery'],function(require,$){

	    function bFunction(){
	        alert('this is b module');
	    }

	    require(['moduleA'],function(m){
	        m() // 执行传递过来方法
	    });

	    return bFunction;
	});

这里要引起我们注意的地方就是依赖的 ``require 模块`` ，它是一个预先定义好的值，引入该值，在当前模块下便可以调用 ``require`` 方法。

通过 ``exports`` 解决

.. code-block:: js

	define(['exports','jquery'],function(exports,$){

	    function bFunction(){
	        exports.aFunction();
	        alert('this is b module');
	    }

	    exports.bFunction = bFunction;
	});

相同的这里依赖的 ``module`` 模块也是一个预先定义好的值，引入该值，在当前模块下便可以调用 ``exports`` 对象设定当前模块的返回值。

而通过 ``exports`` 所解决的循环依赖问题，有一个需要注意的地方，那就是方法的执行必须要放入到当前定义方法的回调中，因为我们不能确定 ``moduleA`` 与 ``moduleB`` 的加载顺序。

CDN回退
-------
如果我们不确定一个模块的加载正确，我们可以在 ``require.config()`` 方法中将模块的地址替换为一个数组，数组的元素，便是同一模块的多个地址。

.. code-block:: js

	requirejs.config({
	    paths: {
	        jquery: [
	            '//cdnjs.cloudflare.com/ajax/libs/jquery/2.0.0/jquery.min.js',
	            'lib/jquery'
	        ]
	    }
	});

定义AMD插件
-----------
有时候我们自己编写的一款插件，我们需要它能够在任何环境中都能起作用，比如在引入 ``requireJS`` 的 ``AMD`` 环境下可以作为符合 ``AMD`` 规范的插件，进行模块式加载调用，而在普通的浏览器环境中也可以正常的执行。

想实现这一功能，其实很简单，只需要按照下例格式去编写插件即可。

.. code-block:: js

	// say.js 基于JQ扩展的插件。

	(function(win, factory) {
	    if ('function' === typeof define && define.amd) {
	        define(['jquery'], function($) {
	            return new factory(win, $)
	        });
	    } else {
	        factory(win, $);
	    }
	}(window, function(win, $) {

	    var say = function(value) {
	        alert(value);
	    }

	    if ('function' === typeof define && define.amd) {
	        return say;
	    } else if ($ && 'function' === typeof $) {
	        $.say = function(v) {
	            return new say(v);
	        }
	    } else {
	        win.say = function(v) {
	            return new say(v);
	        }
	    }

	}));

	// index.js
	define(['say'],function(say){
	    say('hellow requireJS');
	})


关于require的预定义模块
----------------------
关于这个问题，我们上面也有说到，这里就进行一次总结。

我们可以这样理解，对于 ``requireJS`` 来说，除了我们自己使用 ``require.config()`` 定义的模块，它内部也有自己预先定义好的模块，比如： ``require`` ， ``exports`` ， ``modules`` ，在使用时，我们无需在 ``require.config()`` 去中定义，而是可以直接在依赖中引入使用，比如：

.. code-block:: js

	//index.js
	define(['jquery','config','require','exports','module'],function($,config,require,exports,module){
	  $(function(){
	      require.config(config); // 载入配置文件
	      exports.data = 'index Module Return Value' //定义模块的返回值。
	      modules.config().color; // 接受在配置文件中为该模块配置的参数数据。
	  })
	});

关于R.js压缩非本地文件的问题
--------------------------
在 ``r.js`` 中是无法合并压缩远程文件的，它只能操作本地文件，因此这就带来一个问题，当我们进行模块的压缩合并时，若某个模块存在着对远程模块(文件)的依赖时，使用 ``r.js`` 进行操作便会报错，虽然可以将这个远程文件拷贝到本地来解决这一问题，但是如果像一些公用的资源例如JQ插件等，如果让每个项目都在本地放入一个 ``common`` 资源包，这就脱离了我们的实际意义。

.. code-block:: shell

	({
	    paths:{
	        jquery:'http://xxx.com/js/jquery.min'
	    }
	})

此时进行打包的时候在就会报错。但是如果我们不在 ``paths`` 中去声明 ``jquery`` 模块，当打包的时候， ``r.js`` 发现其它模块有依赖 ``jquery`` 的，但是你又没有在 ``build.js`` 中声明，依然会报错阻碍运行。

那么有没有一个好的办法呢？比如虽然声明了 ``jquery`` 模块，但是值却不是远程的文件，本地也不存在该文件，更不会报错。答案是有的，那就是对（不需要参与压缩合并的）远程的资源模块设置值为 ``empty`` :

.. code-block:: shell

    ({ paths:{ jquery:'empty:' } })

或者是在执行命令时，指定参数值为空: ``node r.js -o build.js paths.jquery=empty``

关于R.js - shim功能的说明
------------------------
``R.js`` 用于合并多个模块(多个文件)，以及压缩文件中的 ``JS`` 代码，也就是说在这个合并后的文件中会包含多个 ``define`` 定义的模块，而这个合并后的文件也就是这个页面的入口文件，并且 ``rquire`` 的 ``config`` 配置也会在其中。

模块的合并，对于 ``R.js`` 来言，它会以 ``build.js`` 中 ``paths`` 属性定义的模块为参考，然后到要合并的模块只能中去匹配依赖，匹配到了就合并到当前文件中。如果参与合并的所有模块有某些依赖顺序上的调整，则可以通过 ``shim`` 属性去调整合并时的前后顺序。

.. code-block:: shell

	//build.js
	({
	    'paths':{
	        'a':'a',
	        'b':'b'
	    },
	    'shim':{
	        'a':{
	            'deps':['b']
	        }
	    },
	    wrapShim:true
	})

此时合并到主文件后， ``b`` 模块的内容就会在 ``a`` 模块之前。

.. code-block:: js

	define('b',[],function(){}),
	define('a',[],function(){})

最后强调一点，对于通过 ``exclude`` 属性排除合并的模块，使用 ``shim`` 并不会产生作用，因为它只对合并在一个文件中的模块有效。

关于require加载CSS的问题
-----------------------
``requireJS`` 不仅仅只加载 ``JS`` 文件，实际上它还可以加载 ``CSS`` 样式文件，但是这需要借助一个 ``requireJS`` 插件才能实现。

下载地址： `require-css.js <https://github.com/guybedford/require-css>`_

使用上，有两种方式，一种在配置参数中进行声明：

.. code-block:: js

	var require = {
	    baseUrl:'js/',
	    paths:{
	        'index':'index',
	        'a':'a'
	    },
	    shim:{
	        'a':{
	            deps:['css!../css/a.css']
	        }
	    },
	    deps:['index']
	};

	//index.js
	define(['a']); // 载入模块不执行任何操作。

另一种是直接在模块中进行依赖声明

.. code-block:: js

    define(['css!../css/a.css']);

最后说下我个人对 ``css!../css/index.css`` 的理解吧，首先 ``!`` 是插件与插件参数的分割符号，因此 ``css`` 就是插件的模块名， ``requireJS`` 会先去检查 ``css`` 这个模块是否有在配置文件中声明，如果没有则会默认在 ``baseUrl`` 指向的路径下去载入，而分隔符右边的 ``../css/a.css`` 就是插件要使用的参数，这里便是要载入的 ``css`` 文件地址。

参考文档：

- https://segmentfault.com/a/1190000002401665
- https://segmentfault.com/a/1190000002390643
- https://www.cnblogs.com/zhangjiehui/articles/4275300.html
- https://www.jianshu.com/p/5eb49f5c5196
  






