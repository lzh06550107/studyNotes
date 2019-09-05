******
MISS路由
******

全局MISS路由
============

如果希望在没有匹配到所有的路由规则后执行一条设定的路由，可以注册一个单独的 ``MISS`` 路由：

.. code-block:: php

    Route::miss('public/miss');

.. note:: 一旦设置了 ``MISS`` 路由，相当于开启了强制路由模式

当所有的路由规则都没有匹配到后，会路由到 ``public/miss`` 路由地址。

.. note:: 全局路由其实是针对域名的，只不过默认是针对当前访问的域名，可以在域名路由中单独设置 ``MISS`` 路由

分组MISS路由
============

分组支持独立的 ``MISS`` 路由，例如如下定义：

.. code-block:: php

	Route::group('blog', function () {
	    Route::rule(':id', 'blog/read');
	    Route::rule(':name', 'blog/read');
	    Route::miss('blog/miss');
	})->ext('html')
	    ->pattern(['id' => '\d+', 'name' => '\w+']);




























