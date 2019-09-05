********
Stream接口
********

数据读写可以看作是事件模式（ ``Event`` ）的特例，不断发送的数据块好比一个个的事件。读数据是 ``read`` 事件，写数据是 ``write`` 事件，而数据块是事件附带的信息。 ``Node`` 为这类情况提供了一个特殊接口 ``Stream`` 。

概述
====

概念
----
数据流（ ``stream`` ）是处理系统缓存的一种方式。操作系统采用数据块（ ``chunk`` ）的方式读取数据，每收到一次数据，就存入缓存。 ``Node`` 应用程序有两种缓存的处理方式，第一种是等到所有数据接收完毕，一次性从缓存读取，这就是传统的读取文件的方式；第二种是采用“数据流”的方式，收到一块数据，就读取一块，即在数据还没有接收完成时，就开始处理它。

第一种方式先将数据全部读入内存，然后处理，优点是符合直觉，流程非常自然，缺点是如果遇到大文件，要花很长时间，才能进入数据处理的步骤。第二种方式每次只读入数据的一小块，像“流水”一样，每当系统读入了一小块数据，就会触发一个事件，发出“新数据块”的信号。应用程序只要监听这个事件，就能掌握数据读取的进展，做出相应处理，这样就提高了程序的性能。

.. code-block:: js

	var fs = require('fs');

	fs.createReadStream('./data/customers.csv')
	.pipe(process.stdout);

上面代码中， ``fs.createReadStream`` 方法就是以 ``数据流`` 的方式读取文件，这可以在文件还没有读取完的情况下，就输出到标准输出。这显然对大文件的读取非常有利。

``Unix`` 操作系统从很早以前，就有“数据流”这个概念，它是不同进程之间传递数据的一种方式。管道命令（ ``pipe`` ）就起到在不同命令之间，连接数据流的作用。“数据流”相当于把较大的数据拆成很小的部分，一个命令只要部署了数据流接口，就可以把一个流的输出接到另一个流的输入。 ``Node`` 引入了这个概念，通过数据流接口为异步读写数据提供的统一接口，无论是硬盘数据、网络数据，还是内存数据，都可以采用这个接口读写。

**数据流接口最大特点就是通过事件通信** ，具有 ``readable`` 、 ``writable`` 、 ``drain`` 、 ``data`` 、 ``end`` 、 ``close`` 等事件，既可以读取数据，也可以写入数据。读写数据时，每读入（或写入）一段数据，就会触发一次 ``data`` 事件，全部读取（或写入）完毕，触发 ``end`` 事件。如果发生错误，则触发 ``error`` 事件。

一个对象只要部署了数据流接口，就可以从它读取数据，或者写入数据。 ``Node`` 内部很多涉及 ``IO`` 处理的对象，都部署了 ``Stream`` 接口，下面就是其中的一些。

- 文件读写
- ``HTTP`` 请求的读写
- ``TCP`` 连接
- 标准输入输出

可读数据流
==========
``Stream`` 接口分成三类。

- 可读数据流接口，用于对外提供数据。
- 可写数据流接口，用于写入数据。
- 双向数据流接口，用于读取和写入数据，比如 ``Node`` 的 ``tcp sockets`` 、 ``zlib`` 、 ``crypto`` 都部署了这个接口。

“可读数据流”用来产生数据。它表示数据的来源，只要一个对象提供“可读数据流”，就表示你可以从其中读取数据。

.. code-block:: js

	var Readable = require('stream').Readable;

	var rs = new Readable();
	rs.push('beep ');
	rs.push('boop\n');
	rs.push(null);

	rs.pipe(process.stdout);

上面代码产生了一个可写数据流，最后将其写入标准输出。可读数据流的 ``push`` 方法，用来将数据输入缓存。 ``rs.push(null)`` 中的 ``null`` ，用来告诉 ``rs`` ，数据输入完毕。

“可读数据流”有两种状态：流动态和暂停态。处于流动态时，数据会尽快地从数据源导向用户的程序；处于暂停态时，必须显式调用 ``stream.read()`` 等指令，“可读数据流”才会释放数据。刚刚新建的时候，“可读数据流”处于暂停态。

三种方法可以让暂停态转为流动态。

- 添加 ``data`` 事件的监听函数
- 调用 ``resume`` 方法
- 调用 ``pipe`` 方法将数据送往一个可写数据流

如果转为流动态时，没有 ``data`` 事件的监听函数，也没有 ``pipe`` 方法的目的地，那么数据将遗失。

以下两种方法可以让流动态转为暂停态。

- 不存在 ``pipe`` 方法的目的地时，调用 ``pause`` 方法
- 存在 ``pipe`` 方法的目的地时，移除所有 ``data`` 事件的监听函数，并且调用 ``unpipe`` 方法，移除所有 ``pipe`` 方法的目的地

.. note:: 只移除 ``data`` 事件的监听函数，并不会自动引发数据流进入“暂停态”。另外，存在 ``pipe`` 方法的目的地时，调用 ``pause`` 方法，并不能保证数据流总是处于暂停态，一旦那些目的地发出数据请求，数据流有可能会继续提供数据。

每当系统有新的数据，该接口可以监听到 ``data`` 事件，从而回调函数。

.. code-block:: js

	var fs = require('fs');
	var readableStream = fs.createReadStream('file.txt');
	var data = '';

	readableStream.setEncoding('utf8');

	readableStream.on('data', function(chunk) {
	  data+=chunk;
	});

	readableStream.on('end', function() {
	  console.log(data);
	});

上面代码中， ``fs`` 模块的 ``createReadStream`` 方法，是部署了 ``Stream`` 接口的文件读取方法。该方法对指定的文件，返回一个对象。该对象只要监听 ``data`` 事件，回调函数就能读到数据。

除了 ``data`` 事件，监听 ``readable`` 事件，也可以读到数据。

.. code-block:: js

	var fs = require('fs');
	var readableStream = fs.createReadStream('file.txt');
	var data = '';
	var chunk;

	readableStream.setEncoding('utf8');

	readableStream.on('readable', function() {
	  while ((chunk=readableStream.read()) !== null) {
	    data += chunk;
	  }
	});

	readableStream.on('end', function() {
	  console.log(data)
	});

``readable`` 事件表示系统缓冲之中有可读的数据，使用 ``read`` 方法去读出数据。如果没有数据可读， ``read`` 方法会返回 ``null`` 。

“可读数据流”除了 ``read`` 方法，还有以下方法。

- ``Readable.pause()`` ：暂停数据流。已经存在的数据，也不再触发 ``data`` 事件，数据将保留在缓存之中，此时的数据流称为静态数据流。如果对静态数据流再次调用 ``pause`` 方法，数据流将重新开始流动，但是缓存中现有的数据，不会再触发 ``data`` 事件。
- ``Readable.resume()`` ：恢复暂停的数据流。
- ``readable.unpipe()`` ：从管道中移除目的地数据流。如果该方法使用时带有参数，会阻止“可读数据流”进入某个特定的目的地数据流。如果使用时不带有参数，则会移除所有的目的地数据流。

readable 属性
-------------
一个数据流的 ``readable`` 属性返回一个布尔值。如果数据流是一个仍然打开的可读数据流，就返回 ``true`` ，否则返回 ``false`` 。

read()
------
``read`` 方法从系统缓存读取并返回数据。如果读不到数据，则返回 ``null`` 。

该方法可以接受一个整数作为参数，表示所要读取数据的数量，然后会返回该数量的数据。如果读不到足够数量的数据，返回 ``null`` 。如果不提供这个参数，默认返回系统缓存之中的所有数据。

只在“暂停态”时，该方法才有必要手动调用。“流动态”时，该方法是自动调用的，直到系统缓存之中的数据被读光。

.. code-block:: js

	var readable = getReadableStreamSomehow();
	readable.on('readable', function() {
	  var chunk;
	  while (null !== (chunk = readable.read())) {
	    console.log('got %d bytes of data', chunk.length);
	  }
	});

如果该方法返回一个数据块，那么它就触发了 ``data`` 事件。

_read()
-------
可读数据流的 ``_read`` 方法，可以将数据放入可读数据流。

.. code-block:: js

	var Readable = require('stream').Readable;
	var rs = Readable();

	var c = 97;
	rs._read = function () {
	  rs.push(String.fromCharCode(c++));
	  if (c > 'z'.charCodeAt(0)) rs.push(null);
	};

	rs.pipe(process.stdout);

运行结果如下。

.. code-block:: shell

	$ node read1.js
	abcdefghijklmnopqrstuvwxyz

setEncoding()
-------------
调用该方法，会使得数据流返回指定编码的字符串，而不是缓存之中的二进制对象。比如，调用 ``setEncoding('utf8')`` ，数据流会返回 ``UTF-8`` 字符串，调用 ``setEncoding('hex')`` ，数据流会返回 ``16`` 进制的字符串。

``setEncoding`` 的参数是字符串的编码方法，比如 ``utf8`` 、 ``ascii`` 、 ``base64`` 等。

该方法会正确处理多字节的字符，而缓存的方法 ``buf.toString(encoding)`` 不会。所以如果想要从数据流读取字符串，应该总是使用该方法。

.. code-block:: js

	var readable = getReadableStreamSomehow();
	readable.setEncoding('utf8');
	readable.on('data', function(chunk) {
	  assert.equal(typeof chunk, 'string');
	  console.log('got %d characters of string data', chunk.length);
	});

resume()
--------
``resume`` 方法会使得“可读数据流”继续释放 ``data`` 事件，即转为流动态。

.. code-block:: js

	// 新建一个readable数据流
	var readable = getReadableStreamSomehow();
	readable.resume();
	readable.on('end', function(chunk) {
	  console.log('数据流到达尾部，未读取任务数据');
	});

上面代码中，调用 ``resume`` 方法使得数据流进入流动态，只定义 ``end`` 事件的监听函数，不定义 ``data`` 事件的监听函数，表示不从数据流读取任何数据，只监听数据流到达尾部。

pause()
-------
``pause`` 方法使得流动态的数据流，停止释放 ``data`` 事件，转而进入暂停态。任何此时已经可以读到的数据，都将停留在系统缓存。

.. code-block:: js

	// 新建一个readable数据流
	var readable = getReadableStreamSomehow();
	readable.on('data', function(chunk) {
	  console.log('读取%d字节的数据', chunk.length);
	  readable.pause();
	  console.log('接下来的1秒内不读取数据');
	  setTimeout(function() {
	    console.log('数据恢复读取');
	    readable.resume();
	  }, 1000);
	});

isPaused()
----------
该方法返回一个布尔值，表示“可读数据流”被客户端手动暂停（即调用了 ``pause`` 方法），目前还没有调用 ``resume`` 方法。

.. code-block:: js

	var readable = new stream.Readable

	readable.isPaused() // === false
	readable.pause()
	readable.isPaused() // === true
	readable.resume()
	readable.isPaused() // === false

pipe()
-------
``pipe`` 方法是自动传送数据的机制，就像管道一样。它从“可读数据流”读出所有数据，将其写出指定的目的地。整个过程是自动的。

unpipe()
--------


事件
----


继承可读数据流接口
=================

实例： fs 模块的读数据流
-----------------------


可写数据流
==========


writable属性
------------


write()
-------


cork()，uncork()
----------------


setDefaultEncoding()
---------------------

end()
-----


事件
----

pipe 方法
=========


转换数据流
=========


HTTP请求
========


fs模块
======

错误处理
========






