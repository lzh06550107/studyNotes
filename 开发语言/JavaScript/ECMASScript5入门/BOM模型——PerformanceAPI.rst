***************
Performance API
***************

``Performance API`` 用于精确度量、控制、增强浏览器的性能表现。这个 ``API`` 为测量网站性能，提供以前没有办法做到的精度。

比如，为了得到脚本运行的准确耗时，需要一个高精度时间戳。传统的做法是使用 ``Date`` 对象的 ``getTime`` 方法。

.. code-block:: js

	var start = new Date().getTime();

	// do something here

	var now = new Date().getTime();
	var latency = now - start;
	console.log("任务运行时间：" + latency);

上面这种做法有两个不足之处。首先， ``getTime`` 方法（以及 ``Date`` 对象的其他方法）都只能精确到毫秒级别（一秒的千分之一），想要得到更小的时间差别就无能为力了；其次，这种写法只能获取代码运行过程中的时间进度，无法知道一些后台事件的时间进度，比如浏览器用了多少时间从服务器加载网页。

为了解决这两个不足之处， ECMAScript 5 引入“高精度时间戳”这个 API ，部署在 ``performance`` 对象上。它的精度可以达到1毫秒的千分之一（1秒的百万分之一），这对于衡量的程序的细微差别，提高程序运行速度很有好处，而且还可以获取后台事件的时间进度。

目前，所有主要浏览器都已经支持 ``performance`` 对象，包括 Chrome 20+、Firefox 15+、IE 10+、Opera 15+ 。

performance.timing对象
======================
``performance`` 对象的 ``timing`` 属性指向一个对象，它包含了各种与浏览器性能有关的时间数据，提供浏览器处理网页各个阶段的耗时。比如， ``performance.timing.navigationStart`` 就是浏览器处理当前网页的启动时间。

.. code-block:: js

	Date.now() - performance.timing.navigationStart // 13260687

上面代码表示距离浏览器开始处理当前网页，已经过了 13260687 毫秒。

下面是另一个例子。

.. code-block:: js

	var t = performance.timing;
	var pageloadtime = t.loadEventStart - t.navigationStart;
	var dns = t.domainLookupEnd - t.domainLookupStart;
	var tcp = t.connectEnd - t.connectStart;
	var ttfb = t.responseStart - t.navigationStart;

上面代码依次得到页面加载的耗时、域名解析的耗时、 ``TCP`` 连接的耗时、读取页面第一个字节之前的耗时。

``performance.timing`` 对象包含以下属性（全部为只读）：

- ``navigationStart`` ：当前浏览器窗口的前一个网页关闭，发生 ``unload`` 事件时的 ``Unix`` 毫秒时间戳。如果没有前一个网页，则等于 ``fetchStart`` 属性。
- ``unloadEventStart`` ：如果前一个网页与当前网页属于同一个域名，则返回前一个网页的 ``unload`` 事件发生时的 ``Unix`` 毫秒时间戳。如果没有前一个网页，或者之前的网页跳转不是在同一个域名内，则返回值为 0 。
- ``unloadEventEnd`` ：如果前一个网页与当前网页属于同一个域名，则返回前一个网页 ``unload`` 事件的回调函数结束时的 ``Unix`` 毫秒时间戳。如果没有前一个网页，或者之前的网页跳转不是在同一个域名内，则返回值为 0 。
- ``redirectStart`` ：返回第一个 ``HTTP`` 跳转开始时的 ``Unix`` 毫秒时间戳。如果没有跳转，或者不是同一个域名内部的跳转，则返回值为 0 。
- ``redirectEnd`` ：返回最后一个 ``HTTP`` 跳转结束时（即跳转回应的最后一个字节接受完成时）的 ``Unix`` 毫秒时间戳。如果没有跳转，或者不是同一个域名内部的跳转，则返回值为 0 。
- ``fetchStart`` ：返回浏览器准备使用 ``HTTP`` 请求读取文档时的 ``Unix`` 毫秒时间戳。该事件在网页查询本地缓存之前发生。
- ``domainLookupStart`` ：返回域名查询开始时的 ``Unix`` 毫秒时间戳。如果使用持久连接，或者信息是从本地缓存获取的，则返回值等同于 ``fetchStart`` 属性的值。
- ``domainLookupEnd`` ：返回域名查询结束时的 ``Unix`` 毫秒时间戳。如果使用持久连接，或者信息是从本地缓存获取的，则返回值等同于 ``fetchStart`` 属性的值。
- ``connectStart`` ：返回 ``HTTP`` 请求开始向服务器发送时的 ``Unix`` 毫秒时间戳。如果使用持久连接（ ``persistent connection`` ），则返回值等同于 ``fetchStart`` 属性的值。
- ``connectEnd`` ：返回浏览器与服务器之间的连接建立时的 ``Unix`` 毫秒时间戳。如果建立的是持久连接，则返回值等同于 ``fetchStart`` 属性的值。连接建立指的是所有握手和认证过程全部结束。
- ``secureConnectionStart`` ：返回浏览器与服务器开始安全链接的握手时的 ``Unix`` 毫秒时间戳。如果当前网页不要求安全连接，则返回 0 。
- ``requestStart`` ：返回浏览器向服务器发出 ``HTTP`` 请求时（或开始读取本地缓存时）的 ``Unix`` 毫秒时间戳。
- ``responseStart`` ：返回浏览器从服务器收到（或从本地缓存读取）第一个字节时的 ``Unix`` 毫秒时间戳。
- ``responseEnd`` ：返回浏览器从服务器收到（或从本地缓存读取）最后一个字节时（如果在此之前 ``HTTP`` 连接已经关闭，则返回关闭时）的 ``Unix`` 毫秒时间戳。
- ``domLoading`` ：返回当前网页 ``DOM`` 结构开始解析时（即 ``Document.readyState`` 属性变为 ``loading`` 、相应的 ``readystatechange`` 事件触发时）的 ``Unix`` 毫秒时间戳。
- ``domInteractive`` ：返回当前网页 ``DOM`` 结构结束解析、开始加载内嵌资源时（即 ``Document.readyState`` 属性变为 ``interactive`` 、相应的 ``readystatechange`` 事件触发时）的 ``Unix`` 毫秒时间戳。
- ``domContentLoadedEventStart`` ：返回当前网页 ``DOMContentLoaded`` 事件发生时（即 ``DOM`` 结构解析完毕、所有脚本开始运行时）的 ``Unix`` 毫秒时间戳。
- ``domContentLoadedEventEnd`` ：返回当前网页所有需要执行的脚本执行完成时的 ``Unix`` 毫秒时间戳。
- ``domComplete`` ：返回当前网页 ``DOM`` 结构生成时（即 ``Document.readyState`` 属性变为 ``complete`` ，以及相应的 ``readystatechange`` 事件发生时）的 ``Unix`` 毫秒时间戳。
- ``loadEventStart`` ：返回当前网页 ``load`` 事件的回调函数开始时的 ``Unix`` 毫秒时间戳。如果该事件还没有发生，返回 0 。
- ``loadEventEnd`` ：返回当前网页 ``load`` 事件的回调函数运行结束时的 ``Unix`` 毫秒时间戳。如果该事件还没有发生，返回 0 。

根据上面这些属性，可以计算出网页加载各个阶段的耗时。比如，网页加载整个过程的耗时的计算方法如下：

.. code-block:: js

	var t = performance.timing;
	var pageLoadTime = t.loadEventEnd - t.navigationStart;

performance.now()
=================
``performance.now`` 方法返回当前网页自从 ``performance.timing.navigationStart`` 到当前时间之间的微秒数（毫秒的千分之一）。也就是说，它的精度可以达到 100 万分之一秒。

.. code-block:: js

	performance.now()
	// 23493457.476999998

	Date.now() - (performance.timing.navigationStart + performance.now())
	// -0.64306640625

上面代码表示， ``performance.timing.navigationStart`` 加上 ``performance.now()`` ，近似等于 ``Date.now()`` ，也就是说， ``Date.now()`` 可以替代 ``performance.now()`` 。但是，前者返回的是毫秒，后者返回的是微秒，所以后者的精度比前者高 1000 倍。

通过两次调用 ``performance.now`` 方法，可以得到间隔的准确时间，用来衡量某种操作的耗时。

.. code-block:: js

	var start = performance.now();
	doTasks();
	var end = performance.now();

	console.log('耗时：' + (end - start) + '微秒。');

performance.mark()
===================
``mark`` 方法用于为相应的视点做标记。

.. code-block:: js

    window.performance.mark('mark_fully_loaded');

``clearMarks`` 方法用于清除标记，如果不加参数，就表示清除所有标记。

.. code-block:: js

	window.peformance.clearMarks('mark_fully_loaded');

	window.performance.clearMarks();

performance.getEntries()
=========================

浏览器获取网页时，会对网页中每一个对象（脚本文件、样式表、图片文件等等）发出一个 ``HTTP`` 请求。 ``performance.getEntries`` 方法以数组形式，返回这些请求的时间统计信息，有多少个请求，返回数组就会有多少个成员。

由于该方法与浏览器处理网页的过程相关，所以只能在浏览器中使用。

.. code-block:: js

	window.performance.getEntries()[0]

	// PerformanceResourceTiming {
	//   responseEnd: 4121.6200000017125,
	//   responseStart: 4120.0690000005125,
	//   requestStart: 3315.355000002455,
	//   ...
	// }

上面代码返回第一个 ``HTTP`` 请求（即网页的 ``HTML`` 源码）的时间统计信息。该信息以一个高精度时间戳的对象形式返回，每个属性的单位是微秒（ ``microsecond`` ），即百万分之一秒。

performance.navigation对象
==========================

除了时间信息， ``performance`` 还可以提供一些用户行为信息，主要都存放在 ``performance.navigation`` 对象上面。

它有两个属性：

performance.navigation.type
----------------------------
该属性返回一个整数值，表示网页的加载来源，可能有以下4种情况：

- ``0`` ：网页通过点击链接、地址栏输入、表单提交、脚本操作等方式加载，相当于常数 ``performance.navigation.TYPE_NAVIGATENEXT`` 。
- ``1`` ：网页通过“重新加载”按钮或者 ``location.reload()`` 方法加载，相当于常数 ``performance.navigation.TYPE_RELOAD`` 。
- ``2`` ：网页通过“前进”或“后退”按钮加载，相当于常数 ``performance.navigation.TYPE_BACK_FORWARD`` 。
- ``255`` ：任何其他来源的加载，相当于常数 ``performance.navigation.TYPE_UNDEFINED`` 。

performance.navigation.redirectCount
------------------------------------
该属性表示当前网页经过了多少次重定向跳转。
