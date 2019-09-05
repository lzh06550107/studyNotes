********
同源政策
********
浏览器安全的基石是“同源政策”（ `same-origin policy <https://en.wikipedia.org/wiki/Same-origin_policy>`_ ）。很多开发者都知道这一点，但了解得不全面。

概述
====

含义
----
1995年，同源政策由 ``Netscape`` 公司引入浏览器。目前，所有浏览器都实行这个政策。

最初，它的含义是指， A 网页设置的 ``Cookie`` ， B 网页不能打开，除非这两个网页“同源”。所谓“同源”指的是”三个相同“。

- 协议相同
- 域名相同
- 端口相同

举例来说， ``http://www.example.com/dir/page.html`` 这个网址，协议是 ``http://`` ，域名是 ``www.example.com`` ，端口是 80 （默认端口可以省略），它的同源情况如下。

- ``http://www.example.com/dir2/other.html`` ：同源
- ``http://example.com/dir/other.html`` ：不同源（域名不同）
- ``http://v2.www.example.com/dir/other.html`` ：不同源（域名不同）
- ``http://www.example.com:81/dir/other.html`` ：不同源（端口不同）
- ``https://www.example.com/dir/page.html`` ：不同源（协议不同）

目的
----
同源政策的目的，是为了保证用户信息的安全，防止恶意的网站窃取数据。

设想这样一种情况： A 网站是一家银行，用户登录以后， A 网站在用户的机器上设置了一个 ``Cookie`` ，包含了一些隐私信息（比如存款总额）。用户离开 A 网站以后，又去访问 B 网站，如果没有同源限制，B 网站可以读取 A 网站的 ``Cookie`` ，那么隐私信息就会泄漏。更可怕的是， ``Cookie`` 往往用来保存用户的登录状态，如果用户没有退出登录，其他网站就可以冒充用户，为所欲为。因为浏览器同时还规定，提交表单不受同源政策的限制。

由此可见，同源政策是必需的，否则 ``Cookie`` 可以共享，互联网就毫无安全可言了。

限制范围
--------
随着互联网的发展，同源政策越来越严格。目前，如果非同源，共有三种行为受到限制。

- 无法读取非同源网页的 ``Cookie`` 、 ``LocalStorage`` 和 ``IndexedDB`` 。
- 无法接触非同源网页的 ``DOM`` 。
- 无法向非同源地址发送 ``AJAX`` 请求（可以发送，但浏览器会拒绝接受响应）。

另外，通过 ``JavaScript`` 脚本可以拿到其他窗口的 ``window`` 对象。如果是非同源的网页，目前允许一个窗口可以接触其他网页的 ``window`` 对象的九个属性和四个方法。

- window.closed
- window.frames
- window.length
- window.location
- window.opener
- window.parent
- window.self
- window.top
- window.window
- window.blur()
- window.close()
- window.focus()
- window.postMessage()

上面的九个属性之中，只有 ``window.location`` 是可读写的，其他八个全部都是只读。

虽然这些限制是必要的，但是有时很不方便，合理的用途也受到影响。下面介绍如何规避上面的限制。

Cookie
=======
``Cookie`` 是服务器写入浏览器的一小段信息，只有同源的网页才能共享。如果两个网页一级域名相同，只是次级域名不同，浏览器允许通过设置 ``document.domain`` 共享 ``Cookie`` 。

举例来说， A 网页的网址是 ``http://w1.example.com/a.html`` ，B 网页的网址是 ``http://w2.example.com/b.html`` ，那么只要设置相同的 ``document.domain`` ，两个网页就可以共享 ``Cookie`` 。因为浏览器通过 ``document.domain`` 属性来检查是否同源。

.. code-block:: js

	// 两个网页都需要设置
	document.domain = 'example.com';

.. note:: A 和 B 两个网页都需要设置 ``document.domain`` 属性，才能达到同源的目的。因为设置 ``document.domain`` 的同时，会把端口重置为 ``null`` ，因此如果只设置一个网页的 ``document.domain`` ，会导致两个网址的端口不同，还是达不到同源的目的。

现在， ``A`` 网页通过脚本设置一个 ``Cookie`` 。

.. code-block:: js

    document.cookie = "test1=hello";

``B`` 网页就可以读到这个 ``Cookie`` 。

.. code-block:: js

    var allCookie = document.cookie;

.. note:: 这种方法只适用于 ``Cookie`` 和 ``iframe`` 窗口， ``LocalStorage`` 和 ``IndexedDB`` 无法通过这种方法，规避同源政策，而要使用下文介绍 ``PostMessage`` API。

另外，服务器也可以在设置 ``Cookie`` 的时候，指定 ``Cookie`` 的所属域名为一级域名，比如 ``.example.com`` 。

.. code-block:: js

    Set-Cookie: key=value; domain=.example.com; path=/

这样的话，二级域名和三级域名不用做任何设置，都可以读取这个 ``Cookie`` 。

iframe
======
``iframe`` 元素可以在当前网页之中，嵌入其他网页。每个 ``iframe`` 元素形成自己的窗口，即有自己的 ``window`` 对象。 ``iframe`` 窗口之中的脚本，可以获得父窗口和子窗口。但是，只有在同源的情况下，父窗口和子窗口才能通信；如果跨域，就无法拿到对方的 ``DOM`` 。

比如，父窗口运行下面的命令，如果 ``iframe`` 窗口不是同源，就会报错。

.. code-block:: js

	document
	.getElementById("myIFrame")
	.contentWindow
	.document
	// Uncaught DOMException: Blocked a frame from accessing a cross-origin frame.

上面命令中，父窗口想获取子窗口的 ``DOM`` ，因为跨域导致报错。

反之亦然，子窗口获取主窗口的 ``DOM`` 也会报错。

.. code-block:: js

    window.parent.document.body // 报错

这种情况不仅适用于 ``iframe`` 窗口，还适用于 ``window.open`` 方法打开的窗口，只要跨域，父窗口与子窗口之间就无法通信。

如果两个窗口一级域名相同，只是二级域名不同，那么设置上一节介绍的 ``document.domain`` 属性，就可以规避同源政策，拿到 ``DOM`` 。

对于完全不同源的网站，目前有两种方法，可以解决跨域窗口的通信问题。

- 片段识别符（fragment identifier）
- 跨文档通信API（Cross-document messaging）

片段识别符
----------
片段标识符（fragment identifier）指的是， ``URL`` 的 ``#`` 号后面的部分，比如 ``http://example.com/x.html#fragment`` 的 ``#fragment`` 。如果只是改变片段标识符，页面不会重新刷新。

父窗口可以把信息，写入子窗口的片段标识符。

.. code-block:: js

	var src = originURL + '#' + data;
	document.getElementById('myIFrame').src = src;

上面代码中，父窗口把所要传递的信息，写入 ``iframe`` 窗口的片段标识符。

子窗口通过监听 ``hashchange`` 事件得到通知。

.. code-block:: js

	window.onhashchange = checkMessage;

	function checkMessage() {
	  var message = window.location.hash;
	  // ...
	}

同样的，子窗口也可以改变父窗口的片段标识符。

.. code-block:: js

    parent.location.href = target + '#' + hash;

window.postMessage()
--------------------
上面的这种方法属于破解， ``HTML5`` 为了解决这个问题，引入了一个全新的 ``API`` ：跨文档通信 ``API`` （Cross-document messaging）。

这个 ``API`` 为 ``window`` 对象新增了一个 ``window.postMessage`` 方法，允许跨窗口通信，不论这两个窗口是否同源。举例来说，父窗口 ``aaa.com`` 向子窗口 ``bbb.com`` 发消息，调用 ``postMessage`` 方法就可以了。

.. code-block:: js

	// 父窗口打开一个子窗口
	var popup = window.open('http://bbb.com', 'title');
	// 父窗口向子窗口发消息
	popup.postMessage('Hello World!', 'http://bbb.com');

``postMessage`` 方法的第一个参数是具体的信息内容，第二个参数是接收消息的窗口的源（origin），即“协议 + 域名 + 端口”。也可以设为 ``*`` ，表示不限制域名，向所有窗口发送。

子窗口向父窗口发送消息的写法类似。

.. code-block:: js

	// 子窗口向父窗口发消息
	window.opener.postMessage('Nice to see you', 'http://aaa.com');

父窗口和子窗口都可以通过 ``message`` 事件，监听对方的消息。

.. code-block:: js

	// 父窗口和子窗口都可以用下面的代码，
	// 监听 message 消息
	window.addEventListener('message', function (e) {
	  console.log(e.data);
	},false);

- ``event.source`` ：发送消息的窗口
- ``event.origin`` : 消息发向的网址
- ``event.data`` : 消息内容

下面的例子是，子窗口通过 ``event.source`` 属性引用父窗口，然后发送消息。

.. code-block:: js

	window.addEventListener('message', receiveMessage);
	function receiveMessage(event) {
	  event.source.postMessage('Nice to see you!', '*');
	}

上面代码有几个地方需要注意。首先， ``receiveMessage`` 函数里面没有过滤信息的来源，任意网址发来的信息都会被处理。其次， ``postMessage`` 方法中指定的目标窗口的网址是一个星号，表示该信息可以向任意网址发送。通常来说，这两种做法是不推荐的，因为不够安全，可能会被恶意利用。

``event.origin`` 属性可以过滤不是发给本窗口的消息。

.. code-block:: js

	window.addEventListener('message', receiveMessage);
	function receiveMessage(event) {
	  if (event.origin !== 'http://aaa.com') return;
	  if (event.data === 'Hello World') {
	    event.source.postMessage('Hello', event.origin);
	  } else {
	    console.log(event.data);
	  }
	}

LocalStorage
------------
通过 ``window.postMessage`` ，读写其他窗口的 ``LocalStorage`` 也成为了可能。

下面是一个例子，主窗口写入 ``iframe`` 子窗口的 ``localStorage`` 。

.. code-block:: js

	window.onmessage = function(e) {
	  if (e.origin !== 'http://bbb.com') {
	    return;
	  }
	  var payload = JSON.parse(e.data);
	  localStorage.setItem(payload.key, JSON.stringify(payload.data));
	};

上面代码中，子窗口将父窗口发来的消息，写入自己的 ``LocalStorage`` 。

父窗口发送消息的代码如下。

.. code-block:: js

	var win = document.getElementsByTagName('iframe')[0].contentWindow;
	var obj = { name: 'Jack' };
	win.postMessage(
	  JSON.stringify({key: 'storage', data: obj}),
	  'http://bbb.com'
	);

加强版的子窗口接收消息的代码如下。

.. code-block:: js

	window.onmessage = function(e) {
	  if (e.origin !== 'http://bbb.com') return;
	  var payload = JSON.parse(e.data);
	  switch (payload.method) {
	    case 'set':
	      localStorage.setItem(payload.key, JSON.stringify(payload.data));
	      break;
	    case 'get':
	      var parent = window.parent;
	      var data = localStorage.getItem(payload.key);
	      parent.postMessage(data, 'http://aaa.com');
	      break;
	    case 'remove':
	      localStorage.removeItem(payload.key);
	      break;
	  }
	};

加强版的父窗口发送消息代码如下。

.. code-block:: js

	var win = document.getElementsByTagName('iframe')[0].contentWindow;
	var obj = { name: 'Jack' };
	// 存入对象
	win.postMessage(
	  JSON.stringify({key: 'storage', method: 'set', data: obj}),
	  'http://bbb.com'
	);
	// 读取对象
	win.postMessage(
	  JSON.stringify({key: 'storage', method: "get"}),
	  "*"
	);
	window.onmessage = function(e) {
	  if (e.origin != 'http://aaa.com') return;
	  console.log(JSON.parse(e.data).name);
	};

AJAX
====
同源政策规定， ``AJAX`` 请求只能发给同源的网址，否则就报错。

除了架设服务器代理（浏览器请求同源服务器，再由后者请求外部服务），有三种方法规避这个限制。

- ``JSONP``
- ``WebSocket``
- ``CORS``

JSONP
------
``JSONP`` 是服务器与客户端跨源通信的常用方法。最大特点就是简单适用，老式浏览器全部支持，服务端改造非常小。

它的基本思想是，网页通过添加一个 ``<script>`` 元素，向服务器请求 ``JSON`` 数据，这种做法不受同源政策限制；服务器收到请求后，将数据放在一个指定名字的回调函数里传回来。

首先，网页动态插入 ``<script>`` 元素，由它向跨源网址发出请求。

.. code-block:: js

	function addScriptTag(src) {
	  var script = document.createElement('script');
	  script.setAttribute("type","text/javascript");
	  script.src = src;
	  document.body.appendChild(script);
	}

	window.onload = function () {
	  addScriptTag('http://example.com/ip?callback=foo');
	}

	function foo(data) {
	  console.log('Your public IP address is: ' + data.ip);
	};

上面代码通过动态添加 ``<script>`` 元素，向服务器 ``example.com`` 发出请求。注意，该请求的查询字符串有一个 ``callback`` 参数，用来指定回调函数的名字，这对于 ``JSONP`` 是必需的。

服务器收到这个请求以后，会将数据放在回调函数的参数位置返回。

.. code-block:: js

	foo({
	  "ip": "8.8.8.8"
	});

由于 ``<script>`` 元素请求的脚本，直接作为代码运行。这时，只要浏览器定义了 ``foo`` 函数，该函数就会立即调用。作为参数的 ``JSON`` 数据被视为 ``JavaScript`` 对象，而不是字符串，因此避免了使用 ``JSON.parse`` 的步骤。

WebSocket
---------
``WebSocket`` 是一种通信协议，使用 ``ws://`` （非加密）和 ``wss://`` （加密）作为协议前缀。该协议不实行同源政策，只要服务器支持，就可以通过它进行跨源通信。

下面是一个例子，浏览器发出的 ``WebSocket`` 请求的头信息（摘自 `维基百科 <https://en.wikipedia.org/wiki/WebSocket>`_ ）。

.. code-block:: shell

	GET /chat HTTP/1.1
	Host: server.example.com
	Upgrade: websocket
	Connection: Upgrade
	Sec-WebSocket-Key: x3JJHMbDL1EzLkh9GBhXDw==
	Sec-WebSocket-Protocol: chat, superchat
	Sec-WebSocket-Version: 13
	Origin: http://example.com

上面代码中，有一个字段是 ``Origin`` ，表示该请求的请求源（origin），即发自哪个域名。

正是因为有了 ``Origin`` 这个字段，所以 ``WebSocket`` 才没有实行同源政策。因为服务器可以根据这个字段，判断是否许可本次通信。如果该域名在白名单内，服务器就会做出如下回应。

.. code-block:: shell

	HTTP/1.1 101 Switching Protocols
	Upgrade: websocket
	Connection: Upgrade
	Sec-WebSocket-Accept: HSmrc0sMlYUkAGmm5OPpG2HaGWk=
	Sec-WebSocket-Protocol: chat

CORS
----

``CORS`` 是跨源资源分享（Cross-Origin Resource Sharing）的缩写。它允许浏览器向跨域的服务器，发出 ``XMLHttpRequest`` 请求，从而克服了 ``AJAX`` 只能同源使用的限制。它是 ``W3C`` 标准，属于跨源 ``AJAX`` 请求的根本解决方法。相比 ``JSONP`` 只能发 ``GET`` 请求， ``CORS`` 允许任何类型的请求。

简介
^^^^
``CORS`` 需要浏览器和服务器同时支持。目前，所有浏览器都支持该功能。

整个 ``CORS`` 通信过程，都是浏览器自动完成，不需要用户参与。对于开发者来说， ``CORS`` 通信与普通的 ``AJAX`` 通信没有差别，代码完全一样。浏览器一旦发现 ``AJAX`` 请求跨域，就会自动添加一些附加的头信息，有时还会多出一次附加的请求，但用户不会有感知。因此，实现 ``CORS`` 通信的关键是服务器。只要服务器实现了 ``CORS`` 接口，就可以跨域通信。

两种请求
^^^^^^^^
``CORS`` 请求分成两类：简单请求（simple request）和非简单请求（not-so-simple request）。

只要同时满足以下两大条件，就属于简单请求。

- 请求方法是以下三种方法之一

  + HEAD
  + GET
  + POST

- ``HTTP`` 的头信息不超出以下几种字段。

  + Accept
  + Accept-Language
  + Content-Language
  + Last-Event-ID
  + Content-Type：只限于三个值 ``application/x-www-form-urlencoded`` 、 ``multipart/form-data`` 、 ``text/plain``

凡是不同时满足上面两个条件，就属于非简单请求。一句话，简单请求就是简单的 ``HTTP`` 方法与简单的 ``HTTP`` 头信息的结合。

这样划分的原因是，表单在历史上一直可以跨域发出请求。简单请求就是表单请求，浏览器沿袭了传统的处理方式，不把行为复杂化，否则开发者可能转而使用表单，规避 ``CORS`` 的限制。对于非简单请求，浏览器会采用新的处理方式。

简单请求
^^^^^^^^

基本流程
""""""""
对于简单请求，浏览器直接发出 ``CORS`` 请求。具体来说，就是在头信息之中，增加一个 ``Origin`` 字段。

下面是一个例子，浏览器发现这次跨域 ``AJAX`` 请求是简单请求，就自动在头信息之中，添加一个 ``Origin`` 字段。

.. code-block:: shell

	GET /cors HTTP/1.1
	Origin: http://api.bob.com
	Host: api.alice.com
	Accept-Language: en-US
	Connection: keep-alive
	User-Agent: Mozilla/5.0...

上面的头信息中， ``Origin`` 字段用来说明，本次请求来自哪个域（协议 + 域名 + 端口）。服务器根据这个值，决定是否同意这次请求。

如果 ``Origin`` 指定的源，不在许可范围内，服务器会返回一个正常的 ``HTTP`` 回应。浏览器发现，这个回应的头信息没有包含 ``Access-Control-Allow-Origin`` 字段（详见下文），就知道出错了，从而抛出一个错误，被 ``XMLHttpRequest`` 的 ``onerror`` 回调函数捕获。注意，这种错误无法通过状态码识别，因为 ``HTTP`` 回应的状态码有可能是 ``200`` 。

如果 ``Origin`` 指定的域名在许可范围内，服务器返回的响应，会多出几个头信息字段。

.. code-block:: shell

	Access-Control-Allow-Origin: http://api.bob.com
	Access-Control-Allow-Credentials: true
	Access-Control-Expose-Headers: FooBar
	Content-Type: text/html; charset=utf-8

上面的头信息之中，有三个与 ``CORS`` 请求相关的字段，都以 ``Access-Control-`` 开头。

1. Access-Control-Allow-Origin

   该字段是必须的。它的值要么是请求时 ``Origin`` 字段的值，要么是一个 ``*`` ，表示接受任意域名的请求。

2. Access-Control-Allow-Credentials

   该字段可选。它的值是一个布尔值，表示是否允许发送 ``Cookie`` 。默认情况下， ``Cookie`` 不包括在 ``CORS`` 请求之中。设为 ``true`` ，即表示服务器明确许可，浏览器可以把 ``Cookie`` 包含在请求中，一起发给服务器。这个值也只能设为 ``true`` ，如果服务器不要浏览器发送 ``Cookie`` ，不发送该字段即可。

3. Access-Control-Expose-Headers

   该字段可选。 ``CORS`` 请求时， ``XMLHttpRequest`` 对象的 ``getResponseHeader()`` 方法只能拿到6个服务器返回的基本字段： ``Cache-Control、Content-Language、Content-Type、Expires、Last-Modified、Pragma`` 。如果想拿到其他字段，就必须在 ``Access-Control-Expose-Headers`` 里面指定。上面的例子指定， ``getResponseHeader('FooBar')`` 可以返回 ``FooBar`` 字段的值。


withCredentials 属性
""""""""""""""""""""
上面说到， ``CORS`` 请求默认不包含 ``Cookie`` 信息（以及 ``HTTP`` 认证信息等），这是为了降低 ``CSRF`` 攻击的风险。但是某些场合，服务器可能需要拿到 ``Cookie`` ，这时需要服务器显式指定 ``Access-Control-Allow-Credentials`` 字段，告诉浏览器可以发送 ``Cookie`` 。

.. code-block:: js

    Access-Control-Allow-Credentials: true

同时，开发者必须在 ``AJAX`` 请求中打开 ``withCredentials`` 属性。

.. code-block:: js

	var xhr = new XMLHttpRequest();
	xhr.withCredentials = true;

否则，即使服务器要求发送 ``Cookie`` ，浏览器也不会发送。或者，服务器要求设置 ``Cookie`` ，浏览器也不会处理。

但是，有的浏览器默认将 ``withCredentials`` 属性设为 ``true`` 。这导致如果省略 ``withCredentials`` 设置，这些浏览器可能还是会一起发送 ``Cookie`` 。这时，可以显式关闭 ``withCredentials`` 。

.. code-block:: js

    xhr.withCredentials = false;

需要注意的是，如果服务器要求浏览器发送 ``Cookie`` ， ``Access-Control-Allow-Origin`` 就不能设为星号，必须指定明确的、与请求网页一致的域名。同时， ``Cookie`` 依然遵循同源政策，只有用服务器域名设置的 ``Cookie`` 才会上传，其他域名的 ``Cookie`` 并不会上传，且（跨域）原网页代码中的 ``document.cookie`` 也无法读取服务器域名下的 ``Cookie`` 。

非简单请求
^^^^^^^^^^

预检请求
""""""""
非简单请求是那种对服务器提出特殊要求的请求，比如请求方法是 ``PUT`` 或 ``DELETE`` ，或者 ``Content-Type`` 字段的类型是 ``application/json`` 。

非简单请求的 ``CORS`` 请求，会在正式通信之前，增加一次 ``HTTP`` 查询请求，称为“预检”请求（preflight）。浏览器先询问服务器，当前网页所在的域名是否在服务器的许可名单之中，以及可以使用哪些 ``HTTP`` 动词和头信息字段。只有得到肯定答复，浏览器才会发出正式的 ``XMLHttpRequest`` 请求，否则就报错。这是为了防止这些新增的请求，对传统的没有 ``CORS`` 支持的服务器形成压力，给服务器一个提前拒绝的机会，这样可以防止服务器大量收到 ``DELETE`` 和 ``PUT`` 请求，这些传统的表单不可能跨域发出的请求。

下面是一段浏览器的 ``JavaScript`` 脚本。

.. code-block:: js

	var url = 'http://api.alice.com/cors';
	var xhr = new XMLHttpRequest();
	xhr.open('PUT', url, true);
	xhr.setRequestHeader('X-Custom-Header', 'value');
	xhr.send();

上面代码中， ``HTTP`` 请求的方法是 ``PUT`` ，并且发送一个自定义头信息 ``X-Custom-Header`` 。

浏览器发现，这是一个非简单请求，就自动发出一个“预检”请求，要求服务器确认可以这样请求。下面是这个“预检”请求的 ``HTTP`` 头信息。

.. code-block:: shell

	OPTIONS /cors HTTP/1.1
	Origin: http://api.bob.com
	Access-Control-Request-Method: PUT
	Access-Control-Request-Headers: X-Custom-Header
	Host: api.alice.com
	Accept-Language: en-US
	Connection: keep-alive
	User-Agent: Mozilla/5.0...

“预检”请求用的请求方法是 ``OPTIONS`` ，表示这个请求是用来询问的。头信息里面，关键字段是 ``Origin`` ，表示请求来自哪个源。

除了 ``Origin`` 字段，“预检”请求的头信息包括两个特殊字段。

- Access-Control-Request-Method

  该字段是必须的，用来列出浏览器的 ``CORS`` 请求会用到哪些 ``HTTP`` 方法，上例是 ``PUT`` 。

- Access-Control-Request-Headers

  该字段是一个逗号分隔的字符串，指定浏览器 ``CORS`` 请求会额外发送的头信息字段，上例是 ``X-Custom-Header`` 。


预检请求的回应
"""""""""""""
服务器收到“预检”请求以后，检查了 ``Origin、Access-Control-Request-Method`` 和 ``Access-Control-Request-Headers`` 字段以后，确认允许跨源请求，就可以做出回应。

.. code-block:: shell

	HTTP/1.1 200 OK
	Date: Mon, 01 Dec 2008 01:15:39 GMT
	Server: Apache/2.0.61 (Unix)
	Access-Control-Allow-Origin: http://api.bob.com
	Access-Control-Allow-Methods: GET, POST, PUT
	Access-Control-Allow-Headers: X-Custom-Header
	Content-Type: text/html; charset=utf-8
	Content-Encoding: gzip
	Content-Length: 0
	Keep-Alive: timeout=2, max=100
	Connection: Keep-Alive
	Content-Type: text/plain

上面的 ``HTTP`` 回应中，关键的是 ``Access-Control-Allow-Origin`` 字段，表示 ``http://api.bob.com`` 可以请求数据。该字段也可以设为星号，表示同意任意跨源请求。

.. code-block:: shell

    Access-Control-Allow-Origin: *

如果服务器否定了“预检”请求，会返回一个正常的 ``HTTP`` 回应，但是没有任何 ``CORS`` 相关的头信息字段，或者明确表示请求不符合条件。

.. code-block:: js

	OPTIONS http://api.bob.com HTTP/1.1
	Status: 200
	Access-Control-Allow-Origin: https://notyourdomain.com
	Access-Control-Allow-Method: POST

上面的服务器回应， ``Access-Control-Allow-Origin`` 字段明确不包括发出请求的 ``http://api.bob.com`` 。

这时，浏览器就会认定，服务器不同意预检请求，因此触发一个错误，被 ``XMLHttpRequest`` 对象的 ``onerror`` 回调函数捕获。控制台会打印出如下的报错信息。

.. code-block:: shell

	XMLHttpRequest cannot load http://api.alice.com.
	Origin http://api.bob.com is not allowed by Access-Control-Allow-Origin.

服务器回应的其他 ``CORS`` 相关字段如下。

.. code-block:: shell

	Access-Control-Allow-Methods: GET, POST, PUT
	Access-Control-Allow-Headers: X-Custom-Header
	Access-Control-Allow-Credentials: true
	Access-Control-Max-Age: 1728000

- Access-Control-Allow-Methods

  该字段必需，它的值是逗号分隔的一个字符串，表明服务器支持的所有跨域请求的方法。注意，返回的是所有支持的方法，而不单是浏览器请求的那个方法。这是为了避免多次“预检”请求。

- Access-Control-Allow-Headers

  如果浏览器请求包括 ``Access-Control-Request-Headers`` 字段，则 ``Access-Control-Allow-Headers`` 字段是必需的。它也是一个逗号分隔的字符串，表明服务器支持的所有头信息字段，不限于浏览器在“预检”中请求的字段。

- Access-Control-Allow-Credentials

  该字段与简单请求时的含义相同。

- Access-Control-Max-Age

  该字段可选，用来指定本次预检请求的有效期，单位为秒。上面结果中，有效期是20天（1728000秒），即允许缓存该条回应1728000秒（即20天），在此期间，不用发出另一条预检请求。

浏览器的正常请求和回应
""""""""""""""""""""
一旦服务器通过了“预检”请求，以后每次浏览器正常的 ``CORS`` 请求，就都跟简单请求一样，会有一个 ``Origin`` 头信息字段。服务器的回应，也都会有一个 ``Access-Control-Allow-Origin`` 头信息字段。

下面是“预检”请求之后，浏览器的正常 ``CORS`` 请求。

.. code-block:: shell

	PUT /cors HTTP/1.1
	Origin: http://api.bob.com
	Host: api.alice.com
	X-Custom-Header: value
	Accept-Language: en-US
	Connection: keep-alive
	User-Agent: Mozilla/5.0...

上面头信息的 ``Origin`` 字段是浏览器自动添加的。

下面是服务器正常的回应。

.. code-block:: shell

	Access-Control-Allow-Origin: http://api.bob.com
	Content-Type: text/html; charset=utf-8

上面头信息中， ``Access-Control-Allow-Origin`` 字段是每次回应都必定包含的。

与 JSONP 的比较
^^^^^^^^^^^^^^^
``CORS`` 与 ``JSONP`` 的使用目的相同，但是比 ``JSONP`` 更强大。 ``JSONP`` 只支持 ``GET`` 请求， ``CORS`` 支持所有类型的 ``HTTP`` 请求。 ``JSONP`` 的优势在于支持老式浏览器，以及可以向不支持 ``CORS`` 的网站请求数据。

