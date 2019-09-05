*****************
location配置方法及实例详解
*****************

基本的配置语法
=============

.. code-block:: conf

	location [=|~|~*|^~]  uri  {
		...
	}


location的匹配种类有哪些
=======================

- ``=`` 开头表示精确匹配；
- ``^~`` 开头,注意这不是一个正则表达式，它的目的是优于正则表达式的匹配。如果该 location 是最佳匹配，则不再进行正则表达式检测；
- ``~`` 开头表示区分大小写的正则匹配；
- ``~*`` 开头表示不区分大小写的正则匹配；
- ``/`` 通用匹配, 如果没有其它匹配,任何请求都会匹配到；
- ``!~ && !~*`` ：表示区分大小写不匹配的正则和不区分大小写的不匹配的正则；

精确匹配
--------

.. code-block:: shell

	location = /static/img/file.jpg {
		...
	}

前缀匹配
--------

普通前缀匹配
^^^^^^^^^^^

.. code-block:: shell

	location /static/img/ {
		...
	}

优先前缀匹配
^^^^^^^^^^^

.. code-block:: shell

	location ^~/static/img/ {
		...
	}

正则匹配
-------

区分大小写
^^^^^^^^^

.. code-block:: shell

	location ~ /static/img/.*\.jpg$ {
		...
	}

不区分大小写
^^^^^^^^^^^

.. code-block:: shell

	location ~* /static/img/.*\.jpg$ {
		...
	}

区分大小写取反
^^^^^^^^^^^^^

.. code-block:: shell

	location !~ /static/img/.*\.jpg$ {
		...
	}

不区分大小写取反
^^^^^^^^^^^^^^^

.. code-block:: shell

	location !~* /static/img/.*\.jpg$ {
		...
	}

location搜索顺序
===============

通俗的说也就是：我们可以通过使用不同的前缀，表达不同的含义，对于不同的前缀可以分为两大类： ``普通location`` 和 ``正则location``

你的 ``location`` 前面添加 ``~`` 或者 ``~*`` 且包含正则表达式的则表示为 ``正则Location`` 。其它的为 ``普通location`` ，不能包含正则表达式。

.. code-block:: shell

    优先级：(location =) > (location 完整路径) > (location ^~ 路径) > (location ~,~* 正则顺序) > (location 部分起始路径) > (/)

注意：如果是普通 ``uri`` 匹配，这个时候是没有顺序的，但是正则匹配则是有顺序的，是从上到下依次匹配，一旦有匹配成功，则停止后面的匹配。

``location`` 搜索流程：

1. ``Nginx`` 服务器会首先会检查多个 ``location`` 中是否有普通 ``location`` 匹配，如果有多个匹配，会先记住匹配度最高的那个。

  1) 如果匹配度最高的那个 ``location`` 是精确匹配，则直接返回该匹配结果。

  2) 如果匹配度最高的那个 ``location`` 是优先前缀匹配，则直接返回该匹配结果。

2. 如果都不是，则再检查正则匹配，这里切记正则匹配是有顺序的，从上到下依次匹配，一旦匹配成功，则结束检查，并就会使用这个 ``location`` 块处理此请求。如果正则匹配全部失败，就会使用刚才记录普通 ``location`` 匹配度最高的那个 ``location`` 块处理此请求。



优先级实例
---------

对于请求： http://example.com/static/img/logo.jpg

1. 如果命中精确匹配，例如：

   .. code-block:: shell

	location = /static/img/logo.jpg {

	}

   则优先精确匹配，并终止匹配。

2. 如果命中多个前缀匹配，例如：

   .. code-block:: shell

	location /static/ {

	}

	location /static/img/ {

	}

   则记住最长的前缀匹配，即上例中的 ``/static/img/`` ，并继续匹配

3. 如果最长的前缀匹配是优先前缀匹配，即：

   .. code-block:: shell

	location /static/ {

	}

	location ^~ /static/img/ {

	}

   则命中此最长的优先前缀匹配，并终止匹配

4. 否则，如果命中多个正则匹配，即：

   .. code-block:: shell

	location /static/ {

	}

	location /static/img/ {

	}

	location ~* /static/ {
		//命中
	}

	location ~* /static/img/ {

	}

   则忘记上述第 2 步中的最长前缀匹配，使用第一个命中的正则匹配，即上例中的 ``location ~* /static/`` ，并终止匹配（命中多个正则匹配，优先使用配置文件中出现次序的第一个）

5. 否则，命中上述第 2 步中记住的最长前缀匹配

最佳实践
=======

所以实际使用中，个人觉得至少有三个匹配规则定义，如下：

.. code-block:: shell

	#直接匹配网站根，通过域名访问网站首页比较频繁，使用这个会加速处理，官网如是说。
	#这里是直接转发给后端应用服务器了，也可以是一个静态首页
	# 第一个必选规则
	location = / {
	    proxy_pass http://tomcat:8080/index
	}
	# 第二个必选规则是处理静态文件请求，这是nginx作为http服务器的强项
	# 有两种配置模式，目录匹配或后缀匹配,任选其一或搭配使用
	location ^~ /static/ {
	    root /webroot/static/;
	}
	location ~* \.(gif|jpg|jpeg|png|css|js|ico)$ {
	    root /webroot/res/;
	}
	#第三个规则就是通用规则，用来转发动态请求到后端应用服务器
	#非静态文件请求就默认是动态请求，自己根据实际把握
	#毕竟目前的一些框架的流行，带.php,.jsp后缀的情况很少了
	location / {
	    proxy_pass http://tomcat:8080/
	}



