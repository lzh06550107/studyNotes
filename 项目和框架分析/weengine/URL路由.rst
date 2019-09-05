******
URL 路由
******

.. contents:: 目录
   :depth: 4

入口脚本程序获取到到 ``URL`` 中相关的 ``GET`` 参数，解析后进行权限判断，然后调用相应的控制器处理这个请求。该过程就被称为 ``URL`` 路由（routing）。

约定及使用 ``GET`` 参数中的 ``c`` 、 ``a`` 、 ``do`` 为微擎系统的路由参数，应当避免与系统参数冲突，在程序中可以使用 ``$controller`` 、 ``$action`` 、 ``$do`` 来获取对应的路由三个参数。

非模块URL地址路由
================
当传入的 ``URL`` 请求中包含一个名为 ``c`` 、 ``a`` 、 ``do`` (可选) 的 ``GET`` 参数，它即被视为一个路由，例如：

web端(即后端)
-------------

.. code-block:: shell

    http://we7.cc/web/index.php?c=platform&a=menu&

则会路由至 ``/web/source/platform/menu.ctrl.php`` 文件中。

app端(即前端)
-------------

.. code-block:: shell

    http://we7.cc/app/index.php?c=mc&a=home&

则会路由至 ``/app/source/mc/home.ctrl.php`` 文件中。

模块URL地址路由
==============

web端
------

当传入的 ``c`` 值为 ``site`` , ``a`` 值为 ``entry`` 时则是一个模块路由，例如：

.. code-block:: shell

    http://we7.cc/web/index.php?c=site&a=entry&do=themeset&m=we7_demo

则会路由至 ``/addons/we7_demo/site.php`` 文件中的 ``doWebThemeset()`` 方法。

app端
-----
当传入的 ``c`` 值为 ``entry`` 时则是一个模块路由。如果 ``a`` 值为空，默认进入模块的 ``site.php`` ；如果 ``a`` 值不为空( ``a`` 的有效值有： ``aliapp`` 、 ``baiduapp`` 、 ``phoneapp`` 、 ``site`` 、 ``toutiaoapp`` 、 ``webapp`` 、 ``wxapp`` 、 ``xzapp`` )，则进入对应的模块 ``php`` 文件，例如：

.. code-block:: shell

    http://we7.cc/app/index.php?i=1&j=2&c=entry&do=list&m=we7_demo

则会路由至 ``/addons/we7_demo/site.php`` 文件中的 ``doMobileList()`` 方法。

.. code-block:: shell

    http://we7.cc/app/index.php?i=1&j=2&c=entry&a=wxapp&do=list&m=we7_demo

则会路由至 ``/addons/we7_demo/wxapp.php`` 文件中的 ``doPageList()`` 方法。

.. note:: 前端访问还存在一些常用查询参数：

   - ``t`` ：表示多站点中的站点id；
   - ``s`` ：表示样式id；
   - ``i`` ：表示统一帐号id；
   - ``j`` ：表示子账号id；

根据参数创建URL
==============

框架前后端路径创建函数
--------------------

url($segment, $params = array(), $noredirect = false)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
创建一个URL。该函数在前端和后端都可以使用。

- ``$segment`` ：参数是路由的表达式，以斜杠 ``/`` 的方式组织，每个以斜杠分隔的片段都是指向某一控制器（ ``controller`` ）、操作（ ``action`` ）或是行为（ ``do`` ）。
- ``$params`` ：参数则是以数组的形式表示 ``URL`` 中的 ``QueryString`` 。
- ``$noredirect`` ：是否追加微信 ``URL`` 后缀。该参数在前端(App)有效，后端(Web)无效。

实例：

.. code-block:: php

	echo url('site/entry/themeset', array('m' => 'we7_demo'));
	//http://we7.cc/web/index.php?c=site&a=entry&do=themeset&m=we7_demo

	echo url('mc/home');
	//http://we7.cc/app/index.php?c=mc&a=home&

模块前后端路径创建函数
--------------------

在 ``WeBase`` 抽象类中定义如下两个函数，可以在模块中使用：

createMobileUrl($do, $query = array(), $noredirect = true)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
构造手机端(app)当前模块前端页面 ``URL`` 。

- ``$do`` ：要进入的操作名称对应当前模块的 ``doMobileXXX`` 中的 ``Xxx``
- ``$query`` ：附加的查询参数
- ``$noredirect`` ： ``mobile`` 端 ``url`` 是否要附加 ``&wxref=mp.weixin.qq.com#wechat_redirect``

.. code-block:: php

	class We7_demoModuleSite extends WeModuleSite {
	    public function doMobileIndex() {
	        echo $this->createMobileUrl('home');
	    }
	    public function doMobileHome() {
	        //上面doMobileIndex()生成的链接会进入到这里
	    }
	}

createWebUrl($do, $query = array())
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
构造 ``Web`` 即后台当前模块管理页面 ``URL`` 。

- ``$do`` ：要进入的操作名称对应当前模块的 ``doWebXXX`` 中的 ``XXX``
- ``$query`` ：附加的查询参数

.. code-block:: php

	class We7_demoModuleSite extends WeModuleSite {
	    public function doWebIndex() {
	        echo $this->createWebUrl('home');
	    }
	    public function doWebHome() {
	        //上面doWebIndex()生成的链接会进入到这里
	    }
	}



