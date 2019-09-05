=======================
wordpress启动初始化过程
=======================
WordPress所有的前端页面生成都要经过根目录下的index.php文件（不是主题根目录），这是通过Web服务器的rewrite规则实现的。然后通过index.php文件一步步引导WordPress环境启动，再分析请求URL返回相应数据所组成的前台页面。以下将一步步分析源码文件，以此来了解WordPress整体框架及工作原理。

加载index.php文件
=================

.. literalinclude:: D:\program\phpStudy\WWW\blog\index.php
   :language: php
   :lines: 1,14-17

第一行定义WP_USR_THEMES常量的值为true，是否加载主题文件

.. note::

	在wordpress前台入口文件index.php文件中有一个define(‘WP_USE_THEMES’, true); 这个WP_USE_THEMES是个常量，定义wp是否使用主题，默认为true；一般我们也不会去改变这个东西。那么这个东西到底有什么用的，如果设置成false网站打开后就是一片空白，那么设置成flase的应用场景在哪里的？

	其实除了在入口文件定义了这个常量外，在wp执行的流程中，只有template-loader.php使用到了这个常量。template-loader.php文件在wp-includes文件夹当中，主要作用是做模板加载。wp的执行流程可以大概分为三部分：第一步是核心文件的加载，第二步是业务逻辑的执行，第三步是模板的加载渲染，最后输出到浏览器客户端。

	我们可以看到第三步模板的加载渲染调用的就是template-loader.php，在template-loader.php中几乎所有的代码逻辑的执行都是需要判断WP_USE_THEMES的真假的，如果为true才去执行加载模板的逻辑的。

	有时候我们想要使用使用wp的插件加载，url解析，基于ulr的查询（业务逻辑执行），但是不想使用wp的模板加载，也就是说不输出模板数据，只想输出原始的执行数据输出到客户端，比如APi数据的返回，那我们就可以直接设置这个常量为false，这样template-loader.php中的加载模板的代码都不会执行，我们就可以写代码处理原始的数据了。

	其实从大处看，wp也是一个简单的mvc架构，他也包含的url解析，业务的处理以及最后模板的加载。

第二行包含并运行文件wp-blog-header.php。该文件用于启动WordPress环境及模板。打开wp-blog-header.php文件，代码也非常简单：

.. literalinclude:: D:\program\phpStudy\WWW\blog\wp-blog-header.php
   :language: php
   :lines: 1,8-21

解析1：对$wp_did_header进行赋值，这样如果代码块已经执行过，判断就会失败，代码块就不会再执行。这种做法可以确保wp-blog-header.php文件只执行一次（重复执行的话会出现函数名冲突、变量重置等，WordPress会精神分裂的！）；

解析2：加载WP根目录下wp-load.php文件，执行初始化工作，如初始化常量、环境、加载类库和核心代码等完成WordPress环境启动工作，如加载wp-includes目录下functions.php（函数库）、class-wp.php（类库）、plugin.php（插件）、pomo目录（语言包）、query.php（数据请求）、theme.php（加载主题文件）、post-template.php（文章模板）、comment.php（评论模板）、rewrite.php（URL重写）等等。

解析3：执行wp()函数，执行内容处理工作，如根据用户的请求调用相关函数获取和处理数据，为前端展示准备数据；

解析4：加载根目录绝对路径下wp-includes目录中template-loader.php文件，执行主题应用工作，如根据用户的请求加载主题模板。

WordPress之所以能将用户请求的页面生成出来，都是最后这三行核心代码起的作用。wp-load.php会完成页面生成所需要的所有环境、变量、API等，相当于做了好准备工作；wp()函数根据用户请求的URL从数据库中取出相应的数据内容备用；template-loader.php把已经准备好的内容用主题所设定的样式展现方式给拼接出来。这三项工作完成，就可以将用户请求的页面展现出来了。我们姑且将这三项工作也认定为三个大步骤，以下将重点分析。

初始化执行环境——加载wp-load.php文件
-------------------------------------
该文件初始化常量（如：定义绝对路径、设定功能文件及内容文件路径等）并加载wp-config.php文件（本处不分析wp-config.php文件不存在的情况），部分核心代码如下：

define( 'ABSPATH', dirname(__FILE__) . '/' );

# 定义常量ABSPATH为根目录绝对地址；

require_once( ABSPATH . 'wp-config.php' );

# 加载根目录下wp-config.php文件；

从代码看出，本文件的主要作用就是加载wp-config.php文件，故我们可以抽象的将之看作是wp-load.php初始化时的第一个小步骤，具体如下：

加载wp-config.php文件
+++++++++++++++++++++
该文件主要用于配置MySQL数据库通信信息、设定数据库表名前缀、设定密钥、设置语言及文件绝对路径等，部分核心代码如下（为省事就直接在代码后加#然后解释含义了）：

define('DB_NAME', 'db_name');

# 定义数据库名db_name；

define('DB_USER', 'db_username');

# 定义数据库用户名db_username；

define('DB_PASSWORD', 'db_password');

# 定义数据库密码db_password；

define('DB_HOST', 'db_host_location');

# 定义数据库主机地址，如localhost或其他IP；

define('DB_CHARSET', 'utf8');

# 定义数据表默认文字编码，如utf8；

$table_prefix = 'wp_';

# 定义数据库表前缀，一般默认为wp_；

define('WPLANG', 'zh_CN');

# 定义WordPress语言，中文默认zh_CH，使用的汉化语言文件为/wp-content/languages目录下的zh_CH.mo文件，该文件为二进制，查看具体中文可见zh_CH.po文件；

define('WP_DEBUG', false);

# 设置开发环境DEBUG，默认为false不开启；

require_once(ABSPATH . 'wp-settings.php');

# 加载根目录下wp-settings.php文件；

代码中定义的数据库常量主要用于数据请求时通信数据库，本文件还有个主要作用就是加载了wp-settings.php文件，而该文件相当于启动WordPress环境的总指挥，下面我们就将该文件作为初始化的第二步来分析。

加载wp-settings.php文件
+++++++++++++++++++++++
该文件主要用于创建和定义常见变量、函数和类的库来为WordPress运行做准备，也就是说WordPress运行过程中使用的大多数变量、函数和类等核心代码都是在这个文件中定义的。这个文件相当于一个总控制器，很多常量定义、函数定义等都是在其他文件中完成，而该文件的作用就是执行那些文件或执行在那些文件中已经定义好的函数。
:doc:`wp-setting.php源码分析 </wp-setting.php源码分析>`

内容处理——执行wp()函数
------------------------
在这一阶段，调用wp()函数对数据库内容进行查询，并将查询的内容赋值给一些全局变量，方便在模板中使用模板标签获取相应的数据并展示在前端。该函数源码如下：

.. literalinclude:: D:\program\phpStudy\WWW\blog\wp-includes\functions.php
   :language: php
   :lines: 1,954-960

解析1：调用$wp->main()，即调用对象$wp的main()方法，该对象是class-wp.php文件中WP类实例化得到的，该类主要用于启动WordPress环境，main()方法源码分析详见“WordPress核心类WP内main()方法源码分析”；

解析2：判断$wp_the_query是否设置，若未设置将其赋值为$wp_query，该对象是query.php文件中WP_Query类实例化得到的，该类作用强大，几乎WP所需要的所有数据信息都是由该类得到的，所以内容的准备工作基本都是这段代码来完成的，该类的具体分析见“”；

至此，WP根据请求准备相应数据的工作也已经完成，下面就需要加载模板并把这些数据展现到前台去了。


主题应用——加载template-loader.php文件
---------------------------------------
该文件根据用户URL返回加载相应模板，其源码如下：

.. literalinclude:: D:\program\phpStudy\WWW\blog\wp-includes\template-loader.php
   :language: php

解析1：如果常量WP_USE_THEMES存在且值为真，则判断页面类型同时给$template变量赋相应值；其中，判断页面类型的函数如is_404()位于wp-includes目录下query.php文件，该函数返回对象$wp_query中is_404()方法，若is_404()为false则继续往下判断是否是其他页面；若为true则给$template赋值为get_404_template()，该函数位于wp-includes目录下template.php文件，它返回get_query_template('404')，而该函数将页面类型传入数组$templates并应用调用函数locate_template($templates)且应用过滤器；locate_template()函数根据传入数组在主题中查找到相应的文件然后交给load_template()函数然后使用require加载模板文件，最终将用户需要的页面呈现出来；若template_include过滤钩子上有挂载函数，则对$template进行应用，最终将内容呈现给用户；