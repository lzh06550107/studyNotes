***********
接口查询语言与SDK包
***********

用一句话来描述接口请求
=====================
为了统一规范客户端请求调用接口服务的使用，在尽量保证简单易懂的前提下也兼顾使用的流畅性，为此我们专门设计了内部领域特定语言：接口查询语言（Api Structured Query Language）。通过接口查询语言，最后可以用一句话来描述接口请求。

从外部 DSL 的角度来看此接口查询的操作，可总结为创建、初始化、重置、参数设置和请求等操作。

.. code-block:: shell

	create

	withHost host
	withFilter filter
	withParser parser

	reset   #特别注意：重复查询时须重置请求状态

	withService service
	withParams paramName1 paramValue1
	withParams paramName2 paramValue2
	withParams ... ...
	withTimeout timeout

	request

根据此设计理念，任何语言都可实现此接口查询的具体操作。

接口查询语言设计理念与使用示例
============================
接口查询语言的文法是： ``create -> with -> request`` 。所用到的查询文法解释如下。虽然顺序不强制，但通常是从上往下依次操作。

表3-3 接口查询的文法

+-------------+----------------+----------+------------------------------------------+-----------------------------------------------------------------------------------+
| 操作        | 参数           | 是否必须 | 是否可重复调用                           | 作用说明                                                                          |
+=============+================+==========+==========================================+===================================================================================+
| create      | 无             | 必须     | 可以，重复调用时新建一个实例，非单例模式 | 需要先调用此操作创建一个接口实例                                                  |
+-------------+----------------+----------+------------------------------------------+-----------------------------------------------------------------------------------+
| withHost    | 接口域名       | 必须     | 可以，重复时会覆盖                       | 设置接口域名，如：http://api.phalapi.net/                                         |
+-------------+----------------+----------+------------------------------------------+-----------------------------------------------------------------------------------+
| withFilter  | 过滤器         | 可选     | 可以，重复时会覆盖                       | 设置过滤器，与服务器的DI()->filter对应，需要实现PhalApiClientFilter接口           |
+-------------+----------------+----------+------------------------------------------+-----------------------------------------------------------------------------------+
| withParser  | 解析器         | 可选     | 可以，重复时会覆盖                       | 设置结果解析器，仅当不是JSON返回格式时才需要设置，需要实现PhalApiClientParser接口 |
+-------------+----------------+----------+------------------------------------------+-----------------------------------------------------------------------------------+
| reset       | 无             | 通常必须 | 可以                                     | 重复查询时须重置请求状态，包括接口服务名称、接口参数和超时时间                    |
+-------------+----------------+----------+------------------------------------------+-----------------------------------------------------------------------------------+
| withService | 接口服务名称   | 通常必选 | 可以，重复时会覆盖                       | 设置将在调用的接口服务名称，如：Default.Index                                     |
+-------------+----------------+----------+------------------------------------------+-----------------------------------------------------------------------------------+
| withParams  | 接口参数名、值 | 可选     | 可以，累加参数                           | 设置接口参数，此方法是唯一一个可以多次调用并累加参数的操作                        |
+-------------+----------------+----------+------------------------------------------+-----------------------------------------------------------------------------------+
| withTimeout | 超时时间       | 可选     | 可以，重复时会覆盖                       | 设置超时时间，单位毫秒，默认3秒                                                   |
+-------------+----------------+----------+------------------------------------------+-----------------------------------------------------------------------------------+
| request     | 无             | 必选     | 可以，重复发起接口请求                   | 最后执行此操作，发起接口请求                                                      |
+-------------+----------------+----------+------------------------------------------+-----------------------------------------------------------------------------------+

以 ``JAVA`` 客户端为例，先来演示如何调用SDK包调用接口服务。

最简单的调用，也就是默认接口的调用。只需要设置接口系统域名及入口路径，不需要指定接口服务，也不需要添加其他参数。

.. code-block:: java

	PhalApiClientResponse response = PhalApiClient.create()
	       .withHost("http://demo.phalapi.net/")   //接口域名
	       .request();                             //发起请求

通常的调用，需要设置接口服务名称，添加接口参数，并指定超时时间。

.. code-block:: java

	PhalApiClientResponse response = PhalApiClient.create()
	       .withHost("http://demo.phalapi.net/")
	       .withService("Default.Index")          //接口服务
	       .withParams("username", "dogstar")     //接口参数
	       .withTimeout(3000)                     //接口超时
	       .request();

更高级、更复杂的调用，可根据需要再设置过滤器、解析器，以完成定制化扩展的功能。

.. code-block:: java

	PhalApiClientResponse response = PhalApiClient.create()
	       .withHost("http://demo.phalapi.net/")
	       .withService("Default.Index")
	       .withParser(new PhalApiClientParserJson()) //设置JSON解析，默认已经是此解析，这里仅作演示
	       .withParams("username", "dogstar")
	       .withTimeout(3000)
	       .request();

当接口请求超时时，统一返回 ``ret = 408`` 表示接口请求超时。此时可进行接口重试。

需要重试时，可先判断返回的状态码再重新请求。

.. code-block:: java

	PhalApiClient client = PhalApiClient.create()
	     .withHost("http://demo.phalapi.net/")

	PhalApiClientResponse response = client.request();

	if (response.getRet() == 408) {
	     response = client.request(); //请求重试
	}


更好的建议
==========
不支持面向对象的实现方式
----------------------
上面介绍的接口查询的用法是属于基础的用法，其实现与宿主语言有强依赖关系，在不支持面向对象语言中，可以使用函数序列的方式，例如下面面向过程的伪代码示例。

.. code-block:: php

	create();
	withHost('http://demo.phalapi.net/');
	withService('Default.Index');
	withParams('username', 'dogstar');
	withTimeout(3000);
	rs = request();

封装自己的接口实例
-----------------
通常，在一个项目里面我们只需要一个接口实例即可，但此语言没默认使用单例模式，是为了大家更好的自由度。基于此，大家在项目开发时，可以再进行封装：提供一个全局的接口查询单例，并组装基本的接口公共查询属性。即分两步：初始化接口实例，以及接口具体的查询操作。

如第一步先初始化：

.. code-block:: java

	PhalApiClient client = PhalApiClient.create()
	     .withHost("http://demo.phalapi.net/")
	     .withParser(new PhalApiClientParserJson());

第二步进行具体的接口请求：

.. code-block:: java

	PhalApiClientResponse response = client.reset()  //重复查询时须重置
	     .withService("Default.Index")
	     .withParams("username", "dogstar")
	     .withTimeout(3000)
	     .request();

这样，在其他业务场景下就不需要再重复设置这些共同的属性（如过滤器、解析器）或者共同的接口参数。

Java版SDK包的使用说明
====================
虽然上面简单演示了 ``JAVA`` 版 ``SDK`` 包的使用，但为了给实际项目开发提供更详细的参考，这里再补充一下更具体的使用说明。首先，需要将框架目录下的 ``./SDK/JAVA/net`` 目录中的全部代码拷贝到项目，然后便可以开始使用了。

使用说明
--------
首先，我们需要导入 ``SDK`` 包：

.. code-block:: shell

    import net.phalapi.sdk.*;

然后，准备一个子线程调用，并在此线程中实现接口请求：

.. code-block:: java

	/**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // TODO 在这里进行 http request.网络请求相关操作

            PhalApiClient client = PhalApiClient.create()
                        .withHost("http://demo.phalapi.net/");

            PhalApiClientResponse response = client
                        .withService("Default.Index")
                        .withParams("username", "dogstar")
                        .withTimeout(3000)
                        .request();

            String content = "";
            content += "ret=" + response.getRet() + "\n";
            if (response.getRet() == 200) {
                try {
                    JSONObject data = new JSONObject(response.getData());
                    content += "data.title=" + data.getString("title") + "\n";
                    content += "data.content=" + data.getString("content") + "\n";
                    content += "data.version=" + data.getString("version") + "\n";
                } catch (JSONException ex) {

                }
            }
            content += "msg=" + response.getMsg() + "\n";

            Log.v("[PhalApiClientResponse]", content);

            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", content);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

接着，实现线程回调的 ``hander`` ：

.. code-block:: java

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.i("mylog", "请求结果为-->" + val);
            // TODO
            // UI界面的更新等相关操作
        }
    };

最后，在我们需要的地方启动：

.. code-block:: java

    View.OnClickListener mDummyBtnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
            new Thread(networkTask).start();

            // ....
        }
    };

当我们需要再次使用同一个接口实例进行请求时，需要先进行重置，以便清空之前的接口参数，如：

.. code-block:: java

	// 再一次请求
	response = client.reset() //重置
	        .withService("User.GetBaseInfo")
	        .withParams("user_id", "1")
	        .request();

	content = "";
	content += "ret=" + response.getRet() + "\n";
	if (response.getRet() == 200) {
	    try {
	        JSONObject data = new JSONObject(response.getData());
	        JSONObject info = new JSONObject(data.getString("info"));

	        content += "data.info.id=" + info.getString("id") + "\n";
	        content += "data.info.name=" + info.getString("name") + "\n";
	        content += "data.info.from=" + info.getString("from") + "\n";
	    } catch (JSONException ex) {

	    }
	}
	content += "msg=" + response.getMsg() + "\n";

	Log.v("[PhalApiClientResponse]", content);

异常情况下，即 ``ret != 200`` 时，将返回错误的信息，如：

.. code-block:: java

	// 再来试一下异常的请求
	response = client.reset()
	        .withService("Class.Action")
	        .withParams("user_id", "1")
	        .request();

	content = "";
	content += "ret=" + response.getRet() + "\n";
	content += "msg=" + response.getMsg() + "\n";

	Log.v("[PhalApiClientResponse]", content);

运行后，查询 ``log`` ，可以看到：

.. image:: ./images/ch-3-java-sdk.jpg

图3-7 JAVA版SDK包运行后的效果截图

可以注意到，在调试模式时，会有接口请求的链接和返回的结果日记。

.. code-block:: shell

	10-17 07:40:55.268: D/[PhalApiClient requestUrl](1376): http://demo.phalapi.net/?service=User.GetBaseInfo&user_id=1
	10-17 07:40:55.364: D/[PhalApiClient apiResult](1376): {"ret":200,"data":{"code":0,"msg":"","info":{"id":"1","name":"dogstar","from":"oschina"}},"msg":""}

扩展你的过滤器和结果解析器
------------------------

扩展过滤器
^^^^^^^^^^
当服务端接口需要接口签名验证，或者接口参数加密传送，或者压缩传送时，可以实现此过滤器，以便和服务端操持一致。

当需要扩展时，分两步。首先，需要实现过滤器接口：

.. code-block:: java

	class MyFilter implements PhalApiClientFilter {

	        public void filter(String service, Map<String, String> params) {
	            // TODO ...
	        }
	}

然后设置过滤器：

.. code-block:: java

	PhalApiClientResponse response = PhalApiClient.create()
	           .withHost("http://demo.phalapi.net/")
	           .withFilter(new MyFilter())
	           // ...
	           .request();

扩展结果解析器
^^^^^^^^^^^^^
当返回的接口结果不是 ``JSON`` 格式时，可以重新实现此接口。

当需要扩展时，同样分两步。类似过滤器扩展，这里不再赘述。

Ruby版SDK包的使用说明
====================
遵循前面制定的接口查询语言，不同语言的 ``SDK`` 的使用是类似的。为了说明这一点，并且强调接口查询语言的文法，这里再以 ``Ruby`` 版本的 ``SDK`` 为例，进一步简单说明。

当需要使用 ``Ruby`` 版的 ``SDK`` 包时，先将框架目录下的 ``./SDK/Ruby/PhalApiClient`` 目录中的全部代码拷贝到项目。

使用说明
--------
首先，我们需要导入 ``SDK`` 包：

.. code-block:: rb

	# demo.rb
	require_relative './PhalApiClient/phalapi_client'

然后，创建客户端实例，发起接口请求。

.. code-block:: rb

	a_client = PhalApi::Client.create.withHost('http://demo.phalapi.net')
	a_response = a_client.withService('Default.Index').withParams('username', 'dogstar').withTimeout(3000).request()

	puts a_response.ret, a_response.data, a_response.msg

运行后，可以看到：

.. code-block:: shell

	200
	{"title"=>"Hello World!", "content"=>"dogstar您好，欢迎使用PhalApi！", "version"=>"1.2.1", "time"=>1445741092}

当需要重复调用时，需要先进行重置操作 ``reset`` ，如：

.. code-block:: rb

	# 再调用其他接口
	a_response = a_client.reset \
	    .withService("User.GetBaseInfo") \
	    .withParams("user_id", "1") \
	    .request

	puts a_response.ret, a_response.data, a_response.msg

当请求有异常时，返回的 ``ret!= 200`` ，如：

.. code-block:: rb

	# 非法请求
	a_response = a_client.reset.withService('XXXX.noThisMethod').request

	puts a_response.ret, a_response.data, a_response.msg

以上的输出为：

.. code-block:: shell

	400
	非法请求：接口服务XXXX.noThisMethod不存在

扩展你的过滤器和结果解析器
------------------------

扩展过滤器
^^^^^^^^^
当服务端接口需要接口签名验证，或者接口参数加密传送，或者压缩传送时，可以实现此过滤器，以便和服务端操持一致。

当需要扩展时，分两步。首先，需要实现过滤器接口：

.. code-block:: rb

	class MyFilter < PhalApi::ClientFilter
	        def filter(service, *params)
	            #TODO ...
	        end
	}

然后设置过滤器：

.. code-block:: rb

	a_response = PhalApi::Client.create.withHost('http://demo.phalapi.net') \
	       .withFilter(MyFilter.new) \
	       # ... \
	       .request

扩展结果解析器
^^^^^^^^^^^^^
当返回的接口结果不是 ``JSON`` 格式时，可以重新实现此接口。

当需要扩展时，同样分两步。类似过滤器扩展，这里不再赘述。

除了 ``Java`` 和 ``Ruby`` 外，目前已提供的 ``SDK`` 包还有 ``C#`` 版、 ``Golang`` 版、 ``Object-C`` 版、 ``Javascript`` 版、 ``PHP`` 版、 ``Python`` 版等。其他语言的 ``SDK`` 包使用类似，这里不再赘述。
