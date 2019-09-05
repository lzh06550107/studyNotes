**************
超越HTTP/HTTPS协议
**************

``PhalApi`` 默认使用的是 ``HTTP/HTTPS`` 协议，正如我们所介绍的那样， ``PhalApi`` 还可用于搭建微服务、 ``RESTful`` 接口或 ``Web Services`` 。这一节，我们将一起探讨如何超越 ``HTTP/HTTPS`` 协议，具体如何使用 ``FastRoute`` 扩展构建 ``RESTful API`` ，使用 ``PHPRPC`` 协议，利用 ``SOAP`` 搭建 ``Web Services`` ，以及创建命令行 ``CLI`` 项目。

使用不同的协议，或者采用不同的架构风格，主要是影响接口服务的访问方式，因此我们在切换到其他方案时，只要相应调整接口服务的请求与响应即可。接口服务的核心实现通常无须调整即可共用，但根据不同的使用场景，具体的内部实现需要相应调整。例如命令行 ``CLI`` 项目中不存在 ``COOKIE`` ，所以与 ``COOKIE`` 相关的处理代码会失效或需要移除。

构建RESTful API
===============
``RESTful`` 是一种架构风格，这里不过多介绍什么是 ``RESTful API`` ，而重点在于介绍如何使用 ``FastRoute`` 扩展快速构建 ``RESTful API`` 。

FastRoute扩展的安装
------------------
``FastRoute`` 扩展是基于开源类库 ``FastRoute`` 开发实现的，需要 PHP 5.4.0 及以上版本，通过配置实现自定义路由配置可轻松映射到 ``PhalApi`` 中的 ``service`` 接口服务。

.. note:: FastRoute - Fast request router for PHP：https://github.com/nikic/FastRoute

如前面扩展类库的安装说明，只需要把 ``PhalApi-Library`` 项目中的 ``FastRoute`` 目录拷贝到你项目的 ``Library`` 目录下即可，即：

.. code-block:: shell

    $ cp /path/to/PhalApi-Library/FastRoute/ ./PhalApi/Library/ -R

FastRoute扩展的配置
------------------
随后，在项目配置文件 ``./Config/app.php`` 中追加快速路由配置，这部分可以根据项目的需要进行添加配置。

.. code-block:: php

    /**
     * 扩展类库 - 快速路由配置
     */
    'FastRoute' => array(
         /**
          * 格式：array($method, $routePattern, $handler)
          *
          * @param string/array $method 允许的HTTP请求方法，可以为：GET/POST/HEAD/DELETE 等
          * @param string $routePattern 路由的正则表达式
          * @param string $handler 对应PhalApi中接口服务名称，即：?service=$handler
          */
        'routes' => array(
            array('GET', '/user/get_base_info/{user_id:\d+}', 'User.GetBaseInfo'),
        ),
    ),

其中， ``routes`` 数组中的每一个数组表示一条路由配置，第一个参数是允许的 ``HTTP`` 请求方法，可以为： ``GET/POST/HEAD/DELETE`` 等；第二个参数是路由的正则表达式；第三个参数是对应的接口服务名称，即原来的 ``servcie`` 参数。如上面示例中，配置后访问 ``/user/get_base_info/1`` 等效于原来的 ``/?service=User.GetBaseInfo&user_id=1`` 。

这里为了方便理解，假设前面的 Shop 商城项目中，现在需要添加评论功能，相应地需要提供评论接口服务。显然，评论功能需要发表评论、更新评论、删除评论以及获取评论这些基本功能。对应地，我们可以创建一个新的接口类 ``Api_Comment`` ，并添加对应的成员函数。为简化业务功能，突出 ``RESTful API`` 的构建，假设这里的评论功能很简单，只有评论 ``ID`` 和评论内容这两项。同时，模拟各接口的实现。实现方式很简单，获取相应的参数并简单返回，或者使用一些固定的测试数据。

.. code-block:: php

	// $ vim ./Shop/Api/Comment.php
	<?php
	class Api_Comment extends PhalApi_Api {

	    /**
	     * 获取评论
	     */
	    public function get() {
	        return array('id' => $this->id, 'content' => '模拟获取：评论内容');
	    }

	    /**
	     * 添加评论
	     */
	    public function add() {
	        return array('id' => 1, 'content' => '模拟添加：' . $this->content);
	    }

	    /**
	     * 更新评论
	     */
	    public function update() {
	        return array('id' => $this->id, 'content' => '模拟更新：' . $this->content);
	    }

	    /**
	     * 删除评论
	     */
	    public function delete() {
	        return array('id' => $this->id, 'content' => '模拟删除：评论内容');
	    }
	}

注意，这里只是为了演示而模拟了返回数据。实际项目中，这些返回字段需根据实际情况而定。

上面这四个接口服务所需要的参数配置分别如下：

.. code-block:: php

	// $ vim ./Shop/Api/Comment.php
	class Api_Comment extends PhalApi_Api {

	    public function getRules() {
	        return array(
	            'get' => array(
	                'id' => array('name' => 'id', 'type' => 'int', 'require' => true),
	            ),
	            'add' => array(
	                'content' => array('name' => 'content', 'require' => true),
	            ),
	            'update' => array(
	                'id' => array('name' => 'id', 'type' => 'int', 'require' => true),
	                'content' => array('name' => 'content', 'require' => true),
	            ),
	            'delete' => array(
	                'id' => array('name' => 'id', 'type' => 'int', 'require' => true),
	            ),
	        );
	    }

对于此评论功能，最终希望构建的 ``RESTful API`` 和原来访问方式的映射关系如下：

表3-9 评论RESTful API的映射关系

+----------+-------------------------------+---------------------------+
| 接口服务 | 原来的HTTP/HTTPS访问方式      | 新的RESTful访问方式       |
+==========+===============================+===========================+
| 获取评论 | /shop/?service=Comment.Get    | GET /shop/comment/{id}    |
+----------+-------------------------------+---------------------------+
| 添加评论 | /shop/?service=Comment.Add    | POST /shop/comment        |
+----------+-------------------------------+---------------------------+
| 更新评论 | /shop/?service=Comment.Update | POST /shop/comment/{id}   |
+----------+-------------------------------+---------------------------+
| 删除评论 | /shop/?service=Comment.Delete | DELETE /shop/comment/{id} |
+----------+-------------------------------+---------------------------+

为此，我们需要在项目配置文件 ``./Config/app.php`` 中追加的路由配置为：

.. code-block:: php

	// $ vim ./Config/app.php
    /**
     * 扩展类库 - 快速路由配置
     */
    'FastRoute' => array(
        'routes' => array(
            array('GET', '/shop/comment/{id:\d+}', 'Comment.Get'),
            array('POST', '/shop/comment', 'Comment.Add'),
            array('POST', '/shop/comment/{id:\d+}', 'Comment.Update'),
            array('DELETE', '/shop/comment/{id:\d+}', 'Comment.Delete'),
        ),
    ),

与其他扩展不同，这里除了需要在项目中进行代码配置外，还需要一项很重要的配置，即服务器的配置，需要把不存在的文件路径交给相应的入口文件进行处理。由于我们这里只是对 ``Shop`` 项目采用了 ``RESTful`` ，所以对于以 ``/shop`` 开头且不存在的 ``URI`` ，要交给 ``./Public/shop/index.php`` 文件进行处理。本书使用的是 ``Nginx`` ，所以在 ``Nginx`` 配置文件调整了文件处理的顺序，以及添加了对应的 ``rewrite`` 规则，下面是对应改动与新增的配置。

.. code-block:: shell

    location / {
        try_files $uri $uri/ $uri/index.php;
        #index index.html index.htm index.php;
    }

    if (!-e $request_filename) {
        rewrite ^/shop/(.*)$ /shop/index.php/$1 last;
    }

对于使用 ``Apache`` 或其他服务器的配置也类似，关键是当访问的 ``URI`` 不存在时，需要交由项目的入口文件进行处理。让我们通过示例来逐步分解这一过程，加深对这块的理解。

例如，请求的 ``URL`` 为：

.. code-block:: shell

    http://api.phalapi.net/shop/comment

由于此路径是不存在的，所以会触发 ``rewrite`` 规则，变成了带有 ``index.php`` 的路径。

.. code-block:: shell

    http://api.phalapi.net/shop/index.php/comment

如果到了这一步， ``Nginx`` 服务器还提供 ``404`` ，则需要注意是否配置了 ``index.php`` 处理的方式。例如 ``404`` 时出现这样的 ``error.log`` 错误日记：

.. code-block:: shell

	[error] 2300#0: *9 open() "/path/to/PhalApi/Public/shop/index.php/comment" failed (20: Not a directory), request: "GET /shop/comment HTTP/1.1", host: "api.phalapi.net"

这表示， ``rewrite`` 规则已生效，但未交由 ``/shop/index.php`` 处理，此时可再添加这样的配置。

.. code-block:: shell

    location / {
        try_files $uri $uri/ $uri/index.php;
        #index index.html index.htm index.php;
    }

根据 ``Nginx`` 的说明， ``try_files`` 会先判断 ``$uri`` 这个文件是否存在，再判断 ``$uri/`` 这个目录是否存在，最后重定向到 ``$uri/index.php`` 这个文件。至此，再重新访问上面的 ``URL`` ，便可正常响应了。

.. code-block:: shell

    "GET /shop/comment/1 HTTP/1.1" 200

还有一点补充说明一下。由于本书使用的环境是 PHP 5.3.10 ，而 ``FastRoute`` 需要 PHP 5.4.0 及以上版本。所以针对这一节中 ``FastRoute`` 的演示，我们专门部署了 PHP 7.0.0RC 环境。其他章节若无特殊说明，仍然使用本书约定的版本 PHP 5.3.10 。

FastRoute扩展的注册
------------------
配置好后，接下来就是注册服务。根据情况，可以在初始化文件 ``./Public/init.php`` 注册，也可以在项目入口文件如这里的 ``./Public/shop/index.php`` 注册。而注册 ``FastRoute`` 扩展的具体位置比较关键，应该放置在接口服务响应前，在自定义 ``DI()->request`` 注册后，由于 ``FastRoute`` 扩展会对 ``DI()->request`` 产生副作用，因此在使用时如果不能满足项目需要，可进行相应调整。这里的注册代码是：

.. code-block:: php

	// $ vim ./Public/shop/index.php
	// 显式初始化，并调用分发
	DI()->fastRoute = new FastRoute_Lite();
	DI()->fastRoute->dispatch();

	/** ---------------- 响应接口请求 ---------------- **/
	... ...

FastRoute扩展的使用
------------------
最后，我们可以来体验一下 ``FastRoute`` 扩展所带来的 ``RESTful`` 访问效果。在完成前面的安装、配置、注册和具体的（模拟）业务功能开发的准备工作后，客户端便可以按照新的 ``RESTful`` 风格对接口服务进行访问了。

例如，使用 ``GET`` 方式访问获取评论接口服务，并获取 ``id`` 为 ``1`` 的评论内容。

.. code-block:: shell

	$ curl "http://api.phalapi.net/shop/comment/1"

	{
	  "ret": 200,
	  "data": {
	    "id": 1,
	    "content": "模拟获取：评论内容"
	  },
	  "msg": ""
	}

等效于原来的：

.. code-block:: shell

    $ curl "http://api.phalapi.net/shop/?service=Comment.Get&id=1"

使用 ``POST`` 方式访问评论接口服务，并添加内容为“test”的评论。

.. code-block:: shell

	$ curl -d "content=test" "http://api.phalapi.net/shop/comment"

	{
	    "ret": 200,
	    "data": {
	        "id": 1,
	        "content": "模拟添加：test"
	    },
	    "msg": ""
	}

等效于原来的： ``$ curl -d "content=test" "http://api.phalapi.net/shop/?service=Comment.Add"``

使用 ``PUT`` 方式访问更新评论接口服务，并把 ``id`` 为 ``1`` 的评论内容更新为“新的评论内容”。

.. code-block:: shell

	$ curl -X POST -d "content=新的评论内容" "http://api.phalapi.net/shop/comment/1"

	{
	    "ret": 200,
	    "data": {
	        "id": 1,
	        "content": "模拟更新：新的评论内容"
	    },
	    "msg": ""
	}

等效于原来的：

.. code-block:: shell

    $ curl -X POST -d "id=1&content=新的评论内容" "http://api.phalapi.net/shop/?service=Comment.Update"

使用 ``DELETE`` 方式访问删除评论接口服务，并删除 ``id`` 为 ``1`` 的评论。

.. code-block:: shell

	$ curl -X DELETE "http://api.phalapi.net/shop/comment/1"

	{
	    "ret": 200,
	    "data": {
	        "id": 1,
	        "content": "模拟删除：评论内容"
	    },
	    "msg": ""
	}

等效于原来的：

.. code-block:: shell

    $ curl "http://api.phalapi.net/shop/?service=Comment.Delete&id=1"

一切运行良好！在不修改已有接口服务的前提下，通过新增 ``FastRoute`` 扩展，我们就可以轻松完成了 RESTful API 的构建工作。是不是觉得很有趣？

如果请求的方法未在 ``FastRoute`` 路由规则配置时，会是怎样呢？我们可以尝试一下使用 ``PUT`` 方式访问上面的服务，例如：

.. code-block:: shell

	$ curl -X PUT "http://api.phalapi.net/shop/comment/1"

	{
	    "ret": 405,
	    "data": [],
	    "msg": "快速路由的HTTP请求方法错误，应该为：GET/POST/DELETE"
	}

可以看到，当请求的方法未匹配时，会得到 ``ret = 405`` 的错误返回，并且在提示信息中会注明所允许的访问方式。

即使不使用 ``FastRoute`` 扩展，你也可以使用其他路由类库，或者自定制 ``RESTful`` 映射规则。这里关键的内容是，将 ``RESTful`` 的路由规则，最终转换成原来对应的 ``service`` 访问方式。此外，使用 ``FastRoute`` 扩展，同时会保留原来 ``HTTP/HTTPS`` 协议通过 ``service`` 指定接口服务的访问方式。


使用PHPRPC协议
=============
在需要使用 ``phprpc`` 协议对外提供接口服务时，可以快速利用 ``PHPRPC`` 扩展类库。你会发现，服务端接口服务已有的代码不需要做任何改动，只需要增加此扩展包和添加一个新入口便可完美切换到 ``phprpc`` 协议。

我们一直都建议在项目中恰当地使用设计模式，以便让代码更优雅。要产出优雅的代码，需要在合适的场景采用合适的设计模式，而不是为了“显学”而生硬套用。而更高层的设计原则和工程思想作为指导，能让设计模式发挥更大的作用。比如在设计 ``PhalApi`` 时，我们引入并应用了很多设计原则，有单一职责原则、开放-封闭原则等。因此，在 ``PHPRPC`` 扩展这里我们可以在 ``phprpc`` 的基础上，利用代理模式优雅地扩展实现 ``phprpc`` 协议。

.. note:: phprpc官网：http://www.phprpc.org/

下面将来介绍如何使用 ``PHPRPC`` 扩展类库，通过 ``phprpc`` 协议对外提供接口服务。

PHPRPC扩展的安装
----------------
``PHPRPC`` 扩展的安装和其他扩展一样，从 ``PhalApi-Library`` 扩展库中拷贝 ``PHPRPC`` 到你项目的 ``Library`` 目录下即可。

.. code-block:: shell

    $ cp /path/to/PhalApi-Library/PHPRPC/ ./PhalApi/Library/ -R

到此 ``PHPRPC`` 扩展安装完毕！

PHPRPC扩展的入口
----------------
和其他扩展不同， ``PHPRPC`` 扩展不需要配置，也不需要注册 ``DI`` 服务，但需要单独提供一个使用 ``phprpc`` 协议的访问入口。主要区别是，把原来默认的响应处理 ``PhalApi::reponse()`` 改成 ``PHPRPC`` 扩展的响应处理 ``PHPRPC_Lite::response()`` 。 ``PHPRPC`` 扩展的入口可参考以下实现。

.. code-block:: php

	// $ vim ./Public/shop/phprpc.php
	<?php
	require_once dirname(__FILE__) . '/../init.php';

	// 装载你的接口
	DI()->loader->addDirs('Shop');

	$server = new PHPRPC_Lite();
	$server->response();

和原来的入口文件一样，先加载初始化文件，再装载项目目录，最后使用 ``PHPRPC`` 扩展进行响应。如有其他的入口服务，可在相应的位置进行补充。

至此， ``phprpc`` 协议已准备就绪，可以开始使用了。

通过phprpc协议访问接口服务
------------------------
以 ``Shop`` 项目中的 Hello World 接口服务 ``?service=Welcome.Say`` 为例，演示通过刚才配置的 ``phprpc`` 协议访问此接口服务。

对于提供了 ``phprpc`` 协议的访问入口，如果再使用 ``HTTP/HTTPS`` 协议访问，会看到类似这样的返回。这表示应该更改成通过 ``phprpc`` 协议的访问方式。

.. code-block:: shell

	$ curl "http://api.phalapi.net/shop/phprpc.php?service=Welcome.Say"
	phprpc_functions="YToxOntpOjA7czo4OiJyZXNwb25zZSI7fQ==";

在客户端，根据开发语言可以选择 ``PHPRPC`` 提供的对应的 ``SDK`` 包。这里以 ``PHP`` 版客户端为例，演示如何通过 ``phprpc`` 协议访问接口服务。

.. code-block:: php

	<?php
	require_once '/path/tophprpc/phprpc_client.php';

	$client = new PHPRPC_Client();
	$client->setProxy(NULL);
	$client->setKeyLength(1000);
	$client->setEncryptMode(3);
	$client->setCharset('UTF-8');
	$client->setTimeout(10);

	// 设置phprpc入口链接
	$client->useService('http://api.phalapi.net/shop/phprpc.php');

	// 准备请求的参数
	$params = array('service' => 'Welcome.Say');

	// 请求
	$data = $client->response(json_encode($params));
	var_dump($data);

	if ($data instanceof PHPRPC_Error) {
	    // TODO: 异常处理
	    var_dump($data);
	}

	// 处理返回的数据
	var_dump($data);

注意，最后传递的参数，需要进行一次 ``JSON`` 编码后再传递，以便把全部的参数作为数据包一起发送。

成功请求的情况下，可以看到这样的输出：

.. code-block:: shell

	array(3) {
	  ["ret"]=>
	  int(200)
	  ["data"]=>
	  string(11) "Hello World"
	  ["msg"]=>
	  string(0) ""
	}

失败的情况下，则会返回一个 ``PHPRPC_Error`` 实例。类如当入口链接错误时，返回：

.. code-block:: shell

	object(PHPRPC_Error)#2 (2) {
	  ["Number"]=>
	  int(1)
	  ["Message"]=>
	  string(22) "Illegal PHPRPC server."
	}

为了方便进行 ``phprpc`` 协议下接口服务调用的调试， ``PHPRPC`` 扩展中提供了一个脚本，可用于通过 ``phprpc`` 协议发起接口服务的请求。例如，上面对 Hello World 接口服务的请求，可以：

.. code-block:: shell

	$ ./Library/PHPRPC/check.php "http://api.phalapi.net/shop/phprpc.php" "service=Welcome.Say"
	array(3) {
	  ["ret"]=>
	  int(200)
	  ["data"]=>
	  string(11) "Hello World"
	  ["msg"]=>
	  string(0) ""
	}

输出结果和上面手动编写客户端代码调用的结果一样。

对客户端的调整
-------------
虽然服务端不需要作出太多的改动，但对于客户端来说，需要进行三方面的调整，才能通过 ``phprpc`` 协议调用接口服务，传递参数以及获取返回的结果。

现分说如下。

调用方式的改变
^^^^^^^^^^^^^
首先是客户端调用方式的改变，但值得开心的是， ``phprpc`` 对很多语言都有对应的 ``SDK`` 包支持。具体可以可参考 ``phprpc`` 官网。

POST参数传递方式的改变
^^^^^^^^^^^^^^^^^^^^^
其次是对 ``POST`` 参数传递的改变。考虑到 ``phprpc`` 协议中对 ``POST`` 的数据有一定的复杂性，这里统一作了简化。在前面的示例中也进行了说明，即把全部的参数最后 ``JSON`` 编码传递。对应服务端的解释如下：

.. code-block:: php

    public function response($params = NULL) {
        $paramsArr = json_decode($params, TRUE);
        if ($paramsArr !== FALSE) {
            DI()->request = new PhalApi_Request(array_merge($_GET, $paramsArr));
        }
        ... ...

特此约定：通过第一个参数用 ``JSON`` 格式来传递全部原来需要 ``POST`` 的数据。当 ``POST`` 的数据和 ``GET`` 的数据冲突时，以 ``POST`` 为准。

相应地，当需要传递 ``POST`` 参数时，客户需要这样调整（如 ``PHP`` 下）：

.. code-block:: php

    $client->response(json_encode($params)));

若无此 ``POST`` 参数，则可以忽略不传。

返回结果格式的改变
^^^^^^^^^^^^^^^^^
最后，就是返回结果格式的改变。在 ``phprpc`` 协议下，因为可以更轻松地获取接口返回的源数据，所以这里也同样不再通过字符串流式的序列返回，如原来的 ``JSON`` 或 ``XML`` 格式，而是直接返回接口的源数据 。如前面示例中，返回的是数组类型。这一点，需要特别注意。

利用 SOAP 搭建 Web Services
===========================
当需要使用 ``SOAP`` 时，需要在配置 ``PHP`` 时，通过 ``--enable-soap`` 参数开启 ``SOAP`` 。

SOAP扩展的安装
--------------
从 ``PhalApi-Library`` 扩展库中的 ``SOAP`` 目录拷贝到你项目的 ``Library`` 目录下即可。

.. code-block:: shell

    $ cp /path/to/PhalApi-Library/SOAP/ ./PhalApi/Library/ -R

到此 ``SOAP`` 扩展安装完毕！

SOAP扩展的配置
--------------
需要将以下扩展配置添加到项目配置文件 ``./Config/app.php`` 。

.. code-block:: php

	// $ vim ./Config/app.php
    /**
     * 扩展类库 - SOAP配置
     * @see SoapServer::__construct ( mixed $wsdl [, array $options ] )
     */
    'SOAP' => array(
        'wsdl' => NULL,
        'options' => array(
            'uri' => 'http://api.phalapi.net/shop/soap.php',
            'port' => NULL,
        ),
    ),

其中， ``wsdl`` 配置对应 ``SoapServer`` 构造函数的第一个参数， ``options`` 配置则对应第二个参数，其中的 ``uri`` 须与下面的入口文件路径对应。

SOAP服务端访问入口
-----------------
``SOAP`` 扩展不需要注册 ``DI`` 服务，但需要单独实现访问入口，参考以下实现。

.. code-block:: php

	// $ vim ./Public/shop/soap.php
	<?php
	require_once dirname(__FILE__) . '/../init.php';

	// 装载你的接口
	DI()->loader->addDirs('Shop');

	$server = new SOAP_Lite();
	$server->response();

至此， ``SOAP`` 的服务端已搭建完毕。接下来，客户端便可通过 ``SOAP`` 进行访问了。

SOAP客户端调用
--------------
``SOAP`` 客户端的使用，需要使用 ``SoapClient`` 类，其使用示例如下所示。

.. code-block:: php

	$url = 'http://api.phalapi.net/shop/soap.php';
	$params = array('servcie' => 'Welcome.Say');

	try {
	    $client = new SoapClient(null,
	        array(
	            'location' => $url,
	            'uri'      => $url,
	        )
	    );

	    $data = $client->__soapCall('response', array(json_encode($params)));

	    //处理返回的数据。。。
	    var_dump($data);
	}catch(SoapFault $fault){
	    echo "Error: ".$fault->faultcode.", string: ".$fault->faultstring;
	}

注意，客户端传递的接口参数，最后需要 ``JSON`` 编码后再传递。

SOAP调试脚本
------------
``SOAP`` 扩展提供了一个可以发起 ``SOAP`` 访问的脚本，使用示例如下。

.. code-block:: shell

	$ ./Library/SOAP/check.php http://api.phalapi.net/shop/soap.php "service=Welcome.Say"
	array(3) {
	  ["ret"]=>
	  int(200)
	  ["data"]=>
	  string(11) "Hello World"
	  ["msg"]=>
	  string(0) ""
	}

对客户端的影响
-------------
当使用 ``SOAP`` 访问接口服务时，服务端可以通过使用 ``SOAP`` 扩展快速搭建 ``Web Services`` ，但对于客户端，如同使用 ``PHPRPC`` 协议一样，也要进行三方面的调整。这里简单说明一下。

调用方式的改变
^^^^^^^^^^^^^
首先是客户端调用方式的改变，需要通过 ``SOAP`` 协议进行访问。

POST参数传递方式的改变
^^^^^^^^^^^^^^^^^^^^^
其次是对 ``POST`` 参数传递的改变。和前面的 ``PHPRPC`` 协议一样，客户端需要把全部的参数 ``JSON`` 编码后再传递。当 ``POST`` 的数据和 ``GET`` 的数据冲突时，以 ``POST`` 为准。

相应地，当需要传递 ``POST`` 参数时，客户需要这样调整：

.. code-block:: shell

    $data = $client->__soapCall('response', array(json_encode($params)));

若无此 ``POST`` 参数，则可以忽略不传。

返回结果格式的改变
^^^^^^^^^^^^^^^^^
和 ``PHPRPC`` 协议一样，客户端接收到的是接口服务直接返回的源数据，不再是序列化后返回的字符串。如前面示例中，返回的是数组类型。

创建命令行CLI项目
================
虽然 ``PhalApi`` 专门为接口服务而设计，但若需要使用 ``PhalApi`` 构建命令行 ``CLI`` 项目，也是可以轻松定制实现的。这时需要使用到基于 ``GetOpt`` 的 ``CLI`` 扩展类库，其主要作用是完成命令行参数的解析和处理。

.. note:: GetOpt的Github项目地址为：https://github.com/ulrichsg/getopt-php

CLI扩展类库的安装
----------------
从 ``PhalApi-Library`` 扩展库中的 ``CLI`` 目录拷贝到你项目的 ``Library`` 目录下即可。

.. code-block:: shell

    $ cp /path/to/PhalApi-Library/CLI/ ./PhalApi/Library/ -R

到此CLI扩展安装完毕！

编写命令行入口文件
-----------------
使用 ``CLI`` 扩展构建命令行项目，不需要添加扩展配置，也不需要进行 ``DI`` 服务注册，但需要编写一个单独的 ``CLI`` 入口文件。此 ``CLI`` 入口文件可参考原来项目入口文件的实现，如这里的 ``./Public/shop/index.php`` 。文件路径可根据项目需要放置，通常不建议放置外部可访问的目录 ``Public`` 内，但这为了方便演示，保存到了 ``./Public/shop/cli`` 文件。其代码主要有：

.. code-block:: php

	// $ vim ./Public/shop/cli

	#!/usr/bin/env php
	<?php
	require_once dirname(__FILE__) . '/../init.php';

	// 装载你的接口
	DI()->loader->addDirs('Shop');

	$cli = new CLI_Lite();
	$cli->response();

上面的 ``CLI`` 入口文件，与原来的入口文件类似，先是加载初始化文件，再装载项目目录，最后改用 ``CLI`` 扩展进行响应。

创建好 ``CLI`` 入口文件后，记得需要为其添加执行权限，即：

.. code-block:: shell

    $ chmod +x ./Public/shop/cli

运行和使用
---------
准备好 ``CLI`` 命令行入口后，便试运行一下。

.. code-block:: shell

	$ ./Public/shop/cli
	Usage: ./Public/shop/cli [options] [operands]
	Options:
	  -s, --service <arg>     接口服务
	  -h, --help              查看帮助信息

可以看到，需要使用 ``--service`` 参数传递接口服务名称，或者使用 ``-s`` 缩写形式。

例如，需要调用 Hello World 接口服务，可以：

.. code-block:: shell

	$ ./Public/shop/cli --service Welcome.Say
	{"ret":200,"data":"Hello World","msg":""}

也可以使用缩写的形式：

.. code-block:: shell

	$ ./Public/shop/cli -s Welcome.Say
	{"ret":200,"data":"Hello World","msg":""}

当需要传递更多接口参数时，可以在后面继续添加相应的参数。例如调用商品 ``ID`` 为 ``1`` 的快照信息，需要添加 ``--id 1`` 参数。

.. code-block:: shell

    $ ./Public/shop/cli --service Goods.Snapshot --id 1

又如，调用评论接口更新评论 ``ID`` 为 ``1`` 的内容时，需要添加 ``--id 1`` 和 ``--content "通过CLI 提供的评论内容"`` 这两个参数。

.. code-block:: shell

    $ ./Public/shop/cli --service Comment.Update --id 1 --content "通过CLI 提供的评论内容"

当参数存在空格时，可以像上面这样使用双引号。

再一次，可以看到，接口服务开发好后，通过使用扩展，可轻松切换成其他形式的访问，例如 ``RESTful`` 风格、 ``phprpc`` 协议、 ``CLI`` 命令行等。

获取帮助
--------
命令行项目还有一个颇为有趣功能，那就是常用的帮助信息。当指定接口服务后，若需要查询需要哪些接口参数，可以使用 ``--help`` 查看帮助信息，即查看接口参数说明。

例如，对于获取商品快照信息的 ``Goods.Snapshot`` 服务，若需要查看其接口参数有哪些，可以这样：

.. code-block:: shell

	$ ./Public/shop/cli -s Goods.Snapshot --help
	Usage: ./Public/shop/cli [options] [operands]
	Options:
	  -s, --service <arg>     接口服务
	  -h, --help              查看帮助信息
	  --id <arg>              商品ID

这里的使用说明，会根据配置的接口参数规则自动生成。

让我们来把前面更新评论的接口服务参数规则再完善一下，补充参数说明，并添加一个带有默认值非必须的 ``author`` 参数。

.. code-block:: php

	// $ vim ./Shop/Api/Comment.php
	class Api_Comment extends PhalApi_Api {
	    public function getRules() {
	        return array(
	            'update' => array(
	                'id' => array('name' => 'id', 'type' => 'int', 'require' => true, 'desc' => '评论ID'),
	                'content' => array('name' => 'content', 'require' => true, 'desc' => '待更新的评论内容'),
	                'author' => array('name' => 'author', 'default' => 'nobody', 'desc' => '评论作者'),
	            ),
	            ... ...

再次查看帮助，可以看到相应更新了。

.. code-block:: shell

	$ ./Public/shop/cli -s Comment.Update --help

	Usage: ./Public/shop/cli [options] [operands]
	Options:
	  -s, --service <arg>     接口服务
	  -h, --help              查看帮助信息
	  --id <arg>              评论ID
	  --content <arg>         待更新的评论内容
	  --author [<arg>]        评论作者

小结
====
虽然这里只是介绍了四种超越 ``HTTP/HTTPS`` 协议的方式，但显然还有其他更多的超越方式，比如使用 ``socket`` 进行通信。鉴于篇幅问题，在这不再展开介绍，感兴趣的读者，可以参考上面的方式进行扩展延伸。

这里简单对前面介绍的几种协议方式，进行小结和比较。

表3-10 各种协议/方式的对比

+-------------+--------------------------------------------+------------------------------------+----------------------------+-----------------------------+
| 协议/方式   | 访问方式                                   | 参数传递                           | 结果返回                   | 备注                        |
+=============+============================================+====================================+============================+=============================+
| RESTful API | 依然通过HTTP方式访问，并兼容原来的访问方式 | 增加通过路由规则匹配参数的获取方式 | 保持不变                   | -                           |
+-------------+--------------------------------------------+------------------------------------+----------------------------+-----------------------------+
| PHPRPC协议  | 需要提供新的访问入口，通过phprpc协议访问   | POST参数需要JSON编码后再传递       | 返回源数据，非序列化字符串 | -                           |
+-------------+--------------------------------------------+------------------------------------+----------------------------+-----------------------------+
| SOAP协议    | 需要提供新的访问入口，通过SOAP协议访问     | POST参数需要JSON编码后再传递       | 返回源数据，非序列化字符串 | 需要开启--enable-soap       |
+-------------+--------------------------------------------+------------------------------------+----------------------------+-----------------------------+
| CLI命令行   | 需要提供新的命令行入口                     | 使用命令行参数传递                 | 保持不变                   | 不支持COOKIE、SESSION等操作 |
+-------------+--------------------------------------------+------------------------------------+----------------------------+-----------------------------+

