*****
URL生成
*****

``ThinkPHP`` 支持路由 ``URL`` 地址的统一生成，并且支持所有的路由方式，以及完美解决了路由地址的反转解析，无需再为路由定义和变化而改变 ``URL`` 生成。

.. note:: 如果你开启了路由延迟解析，需要生成路由映射缓存才能支持全部的路由地址的反转解析。

``URL`` 生成使用 ``\think\facade\Url::build()`` 方法或者使用系统提供的助手函数 ``url()`` ，参数一致：

.. code-block:: shell

	Url::build('地址表达式',['参数'],['URL后缀'],['域名'])
	url('地址表达式',['参数'],['URL后缀'],['域名'])

地址表达式和参数
===============
对使用不同的路由地址方式，地址表达式的定义有所区别。参数单独通过第二个参数传入，假设我们定义了一个路由规则如下：

.. code-block:: php

    Route::rule('blog/:id','index/blog/read');

就可以使用下面的方式来生成 ``URL`` 地址：

.. code-block:: php

	Url::build('index/blog/read', 'id=5&name=thinkphp');
	Url::build('index/blog/read', ['id' => 5, 'name' => 'thinkphp']);
	url('index/blog/read', 'id=5&name=thinkphp');
	url('index/blog/read', ['id' => 5, 'name' => 'thinkphp']);

下面我们统一使用第一种方式讲解。

使用模块/控制器/操作生成
----------------------
如果你的路由方式是路由到模块/控制器/操作，那么可以直接写

.. code-block:: php

	// 生成index模块 blog控制器的read操作 URL访问地址
	Url::build('index/blog/read', 'id=5&name=thinkphp');
	// 使用助手函数
	url('index/blog/read', 'id=5&name=thinkphp');

以上方法都会生成下面的 ``URL`` 地址：

.. code-block:: shell

    /index.php/blog/5/name/thinkphp.html


.. note:: 注意，生成方法的第一个参数必须和路由定义的路由地址保持一致，如果写成下面的方式可能无法正确生成URL地址：

.. code-block:: php

	Url::build('blog/read','id=5&name=thinkphp');

如果你的环境支持 ``REWRITE`` ，那么生成的 ``URL`` 地址会变为：

.. code-block:: shell

    /blog/5/name/thinkphp.html

如果你配置了：

.. code-block:: shell

    'url_common_param'=>true

那么生成的URL地址变为：

.. code-block:: shell

    /index.php/blog/5.html?name=thinkphp

不在路由规则里面的变量会直接使用普通 ``URL`` 参数的方式。

.. note:: 需要注意的是，URL地址生成不会检测路由的有效性，只是按照给定的路由地址和参数生成符合条件的路由规则。

使用控制器的方法生成
-------------------
如果你的路由地址是采用控制器的方法，并且路由定义如下：

.. code-block:: php

	// 这里采用配置方式定义路由 动态注册的方式一样有效
	Route::get('blog/:id', '@index/blog/read');

那么可以使用如下方式生成：

.. code-block:: php

	// 生成index模块 blog控制器的read操作 URL访问地址
	Url::build('@index/blog/read', 'id=5');
	// 使用助手函数
	url('@index/blog/read', 'id=5');

那么自动生成的 ``URL`` 地址变为：

.. code-block:: shell

    /index.php/blog/5.html

使用类的方法生成
---------------
如果你的路由地址是路由到类的方法，并且做了如下路由规则定义：

.. code-block:: php

	// 这里采用配置方式定义路由 动态注册的方式一样有效
	Route::rule(['blog','blog/:id'],'\app\index\controller\blog@read');

如果路由地址是到类的方法，需要首先给路由定义命名标识，然后使用标识快速生成 ``URL`` 地址。

那么可以使用如下方式生成：

.. code-block:: php

	// 生成index模块 blog控制器的read操作 URL访问地址
	Url::build('blog?id=5');
	url('blog?id=5');

那么自动生成的 ``URL`` 地址变为：

.. code-block:: shell

    /index.php/blog/5.html

直接使用路由地址
---------------
我们也可以直接使用路由地址来生成 ``URL`` ，例如：

我们定义了路由规则如下：

.. code-block:: php

    Route::get('blog/:id' , 'index/blog/read');

可以使用下面的方式直接使用路由规则生成 ``URL`` 地址：

.. code-block:: php

    Url::build('/blog/5');

那么自动生成的 ``URL`` 地址变为：

.. code-block:: shell

    /index.php/blog/5.html


URL后缀
=======
默认情况下，系统会自动读取 ``url_html_suffix`` 配置参数作为 ``URL`` 后缀（默认为 ``html`` ），如果我们设置了：

.. code-block:: shell

    'url_html_suffix'   => 'shtml'

那么自动生成的 ``URL`` 地址变为：

.. code-block:: shell

    /index.php/blog/5.shtml

如果我们设置了多个 ``URL`` 后缀支持

.. code-block:: shell

    'url_html_suffix'   => 'html|shtml'

则会取第一个后缀来生成 ``URL`` 地址，所以自动生成的 ``URL`` 地址还是：

.. code-block:: shell

    /index.php/blog/5.html

如果你希望指定 ``URL`` 后缀生成，则可以使用：

.. code-block:: php

	Url::build('index/blog/read', 'id=5', 'shtml');
	url('index/blog/read', 'id=5', 'shtml');

域名生成
========

默认生成的 ``URL`` 地址是不带域名的，如果你采用了多域名部署或者希望生成带有域名的 ``URL`` 地址的话，就需要传入第四个参数，该参数有两种用法：


自动生成域名
------------

.. code-block:: php

	Url::build('index/blog/read', 'id=5', 'shtml', true);
	url('index/blog/read', 'id=5', 'shtml', true);

第四个参数传入 ``true`` 的话，表示自动生成域名，如果你开启了 ``url_domain_deploy`` 还会自动识别匹配当前 ``URL`` 规则的域名。

例如，我们注册了域名路由信息如下：

.. code-block:: php

    Route::domain('blog','index/blog');

那么上面的 ``URL`` 地址生成为：

.. code-block:: shell

    http://blog.thinkphp.cn/read/id/5.shtml

指定域名
--------
你也可以显式传入需要生成地址的域名，例如：

.. code-block:: shell

	Url::build('index/blog/read','id=5','shtml','blog');
	url('index/blog/read','id=5','shtml','blog');

或者传入完整的域名

.. code-block:: shell

	Url::build('index/blog/read','id=5','shtml','blog.thinkphp.cn');
	url('index/blog/read','id=5','shtml','blog.thinkphp.cn');

生成的 ``URL`` 地址为：

.. code-block:: shell

    http://blog.thinkphp.cn/read/id/5.shtml

也可以直接在第一个参数里面传入域名，例如：

.. code-block:: php

	Url::build('index/blog/read@blog', 'id=5');
	url('index/blog/read@blog', 'id=5');
	url('index/blog/read@blog.thinkphp.cn', 'id=5');


生成锚点
========
支持生成 ``URL`` 的锚点，可以直接在 ``URL`` 地址参数中使用：

.. code-block:: php

	Url::build('index/blog/read#anchor@blog','id=5');
	url('index/blog/read#anchor@blog','id=5');

.. note:: 锚点和域名一起使用的时候，注意锚点在前面，域名在后面。

生成的 ``URL`` 地址为：

.. code-block:: shell

    http://blog.thinkphp.cn/read/id/5.html#anchor


隐藏或者加上入口文件
==================
有时候我们生成的 ``URL`` 地址可能需要加上 ``index.php`` 或者去掉 ``index.php`` ，大多数时候系统会自动判断，如果发现自动生成的地址有问题，可以直接在调用 ``build`` 方法之前调用 ``root`` 方法，例如加上 ``index.php`` ：

.. code-block:: php

	Url::root('/index.php');
	Url::build('index/blog/read','id=5');

或者隐藏 ``index.php`` ：

.. code-block:: php

	Url::root('/');
	Url::build('index/blog/read','id=5');

.. note:: ``root`` 方法只需要调用一次即可。





