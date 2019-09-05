***********
XML-RPC协议规范
***********

``xml-rpc`` 是一套规范及其一系列的实现，允许运行在不同操作系统、不同环境的程序基于 ``internet`` 进行远程过程调用( ``remote procedure call，RPC`` )。这种远程过程调用使用 ``http`` 作为传输协议， ``xml`` 作为传送信息的编码格式。 ``XML-RPC`` 的定义尽可能的保持了简单，但同时能够传送、处理、返回复杂的数据结构。

前言
====
``XML-RPC`` 是工作在 ``internet`` 上的远程过程调用协议。一个 ``XML-RPC`` 消息就是一个请求体为 ``xml`` 的 ``HTTP-POST`` 请求，被调用的方法在服务器端执行并将执行结果以 ``xml`` 格式编码后返回。 过程调用参数可以是标量、数值、字符串、日期等，也可以是复杂的记录或列表结构( ``list structures`` ，即数组类型)。

Request请求样式
===============
``XML-RPC`` 请求的例子：

.. code-block:: http

	POST /RPC2 HTTP/1.0
	User-Agent: Frontier/5.1.2 (WinNT)
	Host: betty.userland.com
	Content-Type: text/xml
	Content-length: 181

	<?xml version="1.0"?>
	<methodCall>
	 <methodName>examples.getStateName</methodName>
	 <params>
	     <param> <value><i4>41</i4></value> </param>
	 </params>
	</methodCall>

请求头部的要求
=============
第一行中， ``URI`` 的格式可以不指定。例如，如果服务只处理 ``XML-RPC`` 调用，它可以为空，或只是一个斜线。然而，如果服务器要处理各种 ``HTTP`` 请求，则我们应让 ``URI`` 能帮助将请求路由到处理这个 ``XML-RPC`` 请求的代码上。 (例子中， ``URI`` 是 ``/RPC2`` , 这告诉服务器将这个请求路由到 ``RPC2`` 应答程序上。)  ``User-Agent`` 和 ``Host`` 必须指定。 ``Content-Type`` 须是 ``text/xml`` 。  ``Content-Length`` 必须指定且必须正确。

有效负载的格式
=============
有效的信息在一个单一的 ``XML`` 结构 ``<methodCall>`` 中。 ``<methodCall>`` 须含有一个 ``<methodName>`` 子项，这是一个字符串，含有要调用的方法(过程)的名字。该字符串只能含有字母(大小写 ``A-Z`` ), 数字( ``0-9`` ), 下划线, 点, 冒号和斜线( ``/`` )。服务器决定如何解释 ``methodName`` 的字符。 例如， ``methodName`` 可能是一个脚本的名字(脚本在接到某个请求时运行)，可能是一个数据库表中某个记录的某个字段，或可能是一个含有目录和文件的结构的文件的路径名。 如果过程调用有参数则 ``<methodCall>`` 必须包含一个 ``<params>`` 子项。 ``<params>`` 可以包含任意数量的 ``<param>`` 子项, 每个 ``<param>`` 含有一个 ``<value>`` 子项.

**Scalar <value>s**

参数值 可以是标量，用类型标签将值包括起来。如果没指定类型，则认为是 ``string`` 类型。类型标签如下表：

========================  ===============================================  ==============================
Tag                       Type                                             Example                       
========================  ===============================================  ==============================
``<i4>or<int>``           four-byte signed integer                         -12                           
``<boolean>``             0 (false) or 1 (true)                            1                             
``<string>``              string                                           hello world                   
``<double>``              double-precision signed floating point number    -12.214                       
``<dateTime.iso8601>``    date/time                                        19980717T14:08:55             
``<base64>``              base64-encoded binary                            eW91IGNhbid0IHJlYWQgdGhpcyE=  
========================  ===============================================  ==============================

如果没有指定类型，默认为字符串。

**<struct>s**

参数值也可以是 ``<struct>`` 类型。一个 ``<struct>`` 可含有几个 ``<member>`` 项，每个 ``<member>`` 含有一个 ``<name>`` 项和一个 ``<value>`` 项。 ``<member>`` 的 ``<value>`` 值可以为任何类型，可为标量类型、 ``<array>`` 甚至 ``<struct>`` (即可以递归)。例子：

.. code-block:: xml

	<struct>
	   <member>
	      <name>lowerBound</name>
	      <value><i4>18</i4></value>
	      </member>
	   <member>
	      <name>upperBound</name>
	      <value><i4>139</i4></value>
	      </member>
	</struct>

``<struct>`` 是可以递归使用的，任何 ``<value>`` 都里还可以 ``<struct>`` 或其他任何类型，包括后面将要说明的 ``<array>`` 。

**<array>s**

值可以个 ``<array>`` 一个 ``<array>`` 简单的有一个 ``<data>`` 元素。 ``<data>`` 可以是任何合法类型。 下面是一个有4个值的 ``array`` ：

.. code-block:: xml

	<array>
	   <data>
	      <value><i4>12</i4></value>
	      <value><string>Egypt</string></value>
	      <value><boolean>0</boolean></value>
	      <value><i4>-31</i4></value>
	   </data>
	</array>

元素没有名字。 你可以混合使用上面列出的几种类型。 ``<arrays>`` 可以递归使用，其值可以是 ``<array>`` 或其他类型，包括上面说明的 ``<strut>`` 。

**Response 应答样式**

下面是一个 ``XML-RPC`` 请求：

.. code-block:: http

	HTTP/1.1 200 OK
	Connection: close
	Content-Length: 158
	Content-Type: text/xml
	Date: Fri, 17 Jul 1998 19:55:08 GMT
	Server: UserLand Frontier/5.1.2-WinNT

	<?xml version="1.0"?>
	<methodResponse>
	   <params>
	      <param>
	         <value><string>South Dakota</string></value>
	      </param>
	   </params>
	</methodResponse>

**Respnse应答格式**

除非底层操作出现错，否则总是返回 ``200 OK`` 。 ``Content-Type`` 是 ``text/xml`` 。必须设置 ``Content-Length`` ，并且必须是正确的值。 响应内容是一个简单的 ``XML`` ，可是是 ``<methodResponse>`` 包含一个 ``<params>`` , ``<params>`` 包含一个 ``<param>`` , ``<param>`` 包含一个 ``<value>`` 。 ``<methodResponse>`` 可能含有一个 ``<fault>`` 标签。 ``<fault>`` 的值为 ``<struct>`` 类型, ``<struct>`` 有两个元素，值为 ``<int>`` 的 ``<faultCode>`` 和值为 ``<string>`` 的 ``<faultString>`` 。 ``<methodResponse>`` 不能既有 ``<fault>`` 又有 ``<params>`` 。 ``XML－RPC`` 调用失败时的应答的例子：

.. code-block:: http

	HTTP/1.1 200 OK
	Connection: close
	Content-Length: 426
	Content-Type: text/xml
	Date: Fri, 17 Jul 1998 19:55:02 GMT
	Server: UserLand Frontier/5.1.2-WinNT

	<?xml version="1.0"?>
	<methodResponse>
	   <fault>
	      <value>
	         <struct>
	            <member>
	               <name>faultCode</name>
	               <value><int>4</int></value>
	            </member>
	            <member>
	               <name>faultString</name>
	               <value><string>Too many parameters.</string></value>
	            </member>
	         </struct>
	      </value>
	   </fault>
	</methodResponse>

各种语言/平台的XML-RPC实现
=========================

几种常用的( `参考 <http://www.xmlrpc.com/directory/1568/implementations>`_ )：

- `PHP: client/server (Keith Devens) <http://keithdevens.com/software/xmlrpc>`_
- `PHPXMLRPC(Useful, Inc.) <http://phpxmlrpc.sourceforge.net/>`_
- `Objective C: client/server (Marcus Müller) <http://www.mulle-kybernetik.com/software/XMLRPC/>`_
- `Cocoa XML-RPC Framework(Client) <http://divisiblebyzero.com/>`_

参考文档：

- `xmlrpc规范 <http://xmlrpc.scripting.com/spec.html>`_