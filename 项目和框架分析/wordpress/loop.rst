====
循环
====
循环是PHP代码，是WordPress用来显示文章的。使用循环，WordPress在当前页面处理每个文章的显示，并根据它与循环标签中的指定条件匹配进行格式化。在循环中，任何HTML或者PHP代码在每一个文章上将会被处理。

当WordPress文档说“该标签必须在循环中”。例如对于具体的模板标签或者插件，每篇文章的都会重复。例如，对于每篇文章默认都重复显示如下信息：

	- 文章标题(the_title());
	- 发布时间(the_time());
	- 分类(the_category());

你可以使用合适的模板标签来显示其它信息或者对于高级用户，可以直接访问$post变量，该变量在循环时，使用当前帖子信息来设置。

使用循环
========
循环放置在index.php和其它用来显示文章信息的任何模板中。请确保在主题模板顶部包header文件模板的调用。如果您在自己的设计中使用循环（而且您自己的设计不是模板），请将WP_USE_THEMES设置为false：

.. code-block:: php

	<?php define( 'WP_USE_THEMES', false ); get_header(); ?>

.. note::

	在wordpress前台入口文件index.php文件中有一个define(‘WP_USE_THEMES’, true); 这个WP_USE_THEMES是个常量，定义wp是否使用主题，默认为true；一般我们也不会去改变这个东西。那么这个东西到底有什么用的，如果设置成false网站打开后就是一片空白，那么设置成flase的应用场景在哪里的？

	其实除了在入口文件定义了这个常量外，在wp执行的流程中，只有template-loader.php使用到了这个常量。template-loader.php文件在wp-includes文件夹当中，主要作用是做模板加载。wp的执行流程可以大概分为三部分：第一步是核心文件的加载，第二步是业务逻辑的执行，第三步是模板的加载渲染，最后输出到浏览器客户端。

	我们可以看到第三步模板的加载渲染调用的就是template-loader.php，在template-loader.php中几乎所有的代码逻辑的执行都是需要判断WP_USE_THEMES的真假的，如果为true才去执行加载模板的逻辑的。

	有时候我们想要使用使用wp的插件加载，url解析，基于ulr的查询（业务逻辑执行），但是不想使用wp的模板加载，也就是说不输出模板数据，只想输出原始的执行数据输出到客户端，比如APi数据的返回，那我们就可以直接设置这个常量为false，这样template-loader.php中的加载模板的代码都不会执行，我们就可以写代码处理原始的数据了。

	其实从大处看，wp也是一个简单的mvc架构，他也包含的url解析，业务的处理以及最后模板的加载。

这里循环开始：

.. code-block:: php

	<?php if ( have_posts() ) : while ( have_posts() ) : the_post(); ?>

在这里结束：

.. code-block:: php

	<?php endwhile; else : ?>
		<p><?php esc_html_e( 'Sorry, no posts matched your criteria.' ); ?></p>
	<?php endif; ?>

下面使用PHP可选的语法来控制结构：

.. code-block:: php

	<?php 
	if ( have_posts() ) {
		while ( have_posts() ) {
			the_post(); 
			//
			// Post Content here
			//
		} // end while
	} // end if
	?>

循环例子
========
根据不同的分类来样式化文章
--------------------------
下面的例子显示每个文章的标题(用作文章的永久链接)，分类和内容。它同样允许分类ID为3的文章分类使用不同的样式。为了达到该目的，in_category()模板标签被使用。仔细阅读下面代码的每一部分做了什么。

.. literalinclude:: loop.php
   :language: php
   :lines: -52

排除某些分类的文章显示
----------------------
下面的例子演示怎么隐藏一个具体的分类或者分类集合被显示。在本例子情况中，来自分类3和8的文章被排除。本例子和前面的例子不同的原因是，本例子改变了查询本身。

.. literalinclude:: loop.php
   :language: php
   :lines: 54-76

.. note::
	如果你为你的主页面使用本例子，你应该为你的分类归档使用不同模板；否则，即使查看分类归档！WordPress将会排除所有在分类3和8中的文章，但是，如果你想要在一个模板文件中使用，你可以通过is_home()标签来决定上面的代码只能在主页面执行：

	.. code-block:: php

		...
		<?php if ( is_home() ) {
			$query = new WP_Query( 'cat=-3,-8' );
		if ( $query->have_posts() ) : while (		$query->have_posts() ) : $query->the_post(); 
		} else {
		...
		?>
		...
	
	还有其它条件标签被用来根据请求页面来控制输出。

多重循环
--------
