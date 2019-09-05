=================
集成到WordPress中
=================
在本章中，您将学习如何：

	- 将您的HTML文件转换为WordPress的PHP文件;
	- 将您的PHP分解成许多模板，将代码分解成WordPress可以使用的；
	- 使用正确的模板文件显示不同种类的内容；
	- 结合WordPress具体PHP代码，如模板标签和API钩子，进入主题的模板页面，以创建我们的主题函数

WordPress主题基础
=================
到目前为止，您已经明白，WordPress主题内容包含HTML
和CSS，可以持有并样式您的WordPress内容。利用WordPress模板层次结构和主题API，WordPress主题放在一起。主题API基本上只是WordPress具体PHP代码，这使在我们的主题中可以创建和定制循环以及利用各种模板标签和钩子。

模板层次
========
我们已经讨论了一个事实，即WordPress主题由许多包括模板页面的文件类型组成。模板页面有一个结构或层次结构。这意味着如果一个模板页面类型不存在，则WordPress系统将调用下一个可用的模板页面类型。

除了这些模板，主题当然也包括图像，CSS样式表，自定义模板页面和PHP代码文件。从根本上说，在您的WordPress主题中你可以有很多不同的默认页面模板，不包括您的style.css或include模板文件。如果你利用WordPress的为定制post，page，category和tag模板功能，你可以有更多的模板页面。

你可以通过增加如下文件来扩展你的主题：

- 对于静态页面：
	+ 当查看一个静态页面时，page.php优先于index.php；
	+ 当主文章列表被查看时，home.php优先于index.php和page.php；
	+ 当前端页面被查看时，无论是静态页面还是文章列表front-page.php优先于index.php和home.php；
	+ 当通过页面的“管理”面板选择时，自定义模板page-slug或page-ID页面，如page-about.php，将优先于为index.php和page.php；
- 对于单独文章和附件：
	+ 当一个单独文章或附件被查看时，single.php优先于index.php;
	+ 当使用一个自定义文章类型时，single-post_type.php优先于single.php。例如，如果你使用一个product文章类型，你可以设置一个single-product.php来查看单独产品详情。如果你想为自定义文章类型显示不同的信息，这个非常有用；
	+ 当一个附件被查看时，single-attachment.php优先于single.php。同样地，attachment.php优先于single-attachment.php；
	+ 当给定MIME_type附件被查看，一个定制的MIME_type页面优先于attachment.php。例如你可以使用image.php或者video.php来显示附件的这些类型每一种子类型；
- 对于列出文章归档或者搜索结果：
	+ 当一个目录，标签，日期或者作者列表被查看时，archive.php优先于index.php；
	+ 当来自搜索框被查看时，search.php优先于index.php;
	+ 当一个具体分类列表被查看，category.php优先于archive.php;
	+ 当一个分类方法列表被查看，taxonomy.php优先于archive.php;
	+ 一个定制的category-ID页面有限与category.php。为了设置一个分类列表页面，你或者使用分类ID(如，文件名称为category-12.php文件)或者使用分类别名(如，文件名称为category-featured.php)；
	+ 当一个标签列表被查看时，tag.php优先于archive.php；
	+ 一个定制的tag-tagname页面，如tag-reviews.php，优先于tag.php；
	+ 当通过作者列出文章被访问，author.php优先于archive.php；
	+ 当通过给定的日期列出文章被访问，date.php优先于archive.php；
- 最后，当服务器不能发现页面或文章：
	+ 当URL地址发现不存该文件，则404.php优先于其它。你可以使用它来显示一些定制内容，如一个错误消息和搜索框。

.. note::
	页面，文章，附件和定制文章类型是不同的内容：

		- 一个页面是一个静态的不包含在列表中的页面。例如，你可以具有一个静态About页面，或者如果你的站点不是一个博客，而是一个静态Home页面。你可以告诉WordPress是否显示一个静态Home页面或者一个文章列表；
		- 一个文章是一个博客文章或者新闻文章。依赖你开发站点的种类；
		- 一个附件是一个你通过WordPress媒体管理界面上传的文件。WordPress使用附件页面来显示多媒体，如图片，pdf文件和视频。
		- 一个定制文章类型是和博客和新闻文章不同的类型。例如，如果你想要开发一个显示产品的站点，你可以创建自定义一个product文章类型。这将不会在博客中显示，但会在它们自己的列表页中显示。

为什么模板层次结构的具有这样的工作方式？
---------------------------------------
简而言之，WordPress为了强大的灵活性。如果你的主题设计很简单和直截了当（也就是说，你确定你想要所有的循环，帖子和页面看起来完全相同），就像你所说的那样，你可以在技术上把所有一切包含标题，页脚，侧边栏的所有代码放置到单个index.php文件中。

然而，随着你自己的主题开发技能进步，你会发现将主题分割成单独的模板文件可以帮助您利用WordPress拥有的功能，从而让您进行设计
更强大的网站，可以轻松适应许多不同类型的内容，布局，小部件和插件。

构件你的WordPress主题模板文件
=============================
从空白的项目开始
----------------
我们在这里使用的方法包括以下步骤（我们将详细介绍每个步骤）：

	1. 创建一个新的，空的主题目录（但不要删除默认的主题）。
	2. 上传您的模型图像目录以及index.html和style.css模型到上面的目录。
	3. 将一些注释掉的代码添加到WordPress的样式表中。
	4. 将index.html重命名为index.php。
	5. 添加WordPress模板标签的到PHP代码中，特别是blog_info标签和循环，使您的样式样式和一些WordPress内容显示。
	6. 一旦您的主题的WordPress内容加载，您的HTML和CSS工作和正确看，然后你可以把它分开到你的主题相应的模板文件，如header.php，footer.php，sidebar.php等。
	7. 一旦你的主题设计被分离到逻辑模板中，你可以开始使您的主题具有任何特殊的显示要求，如不同的home页面布局，内部页面布局和使用模板标签和API钩子的额外功能。

动手——设置我们主题目录
------------------------
第一步创建包含我们主题的目录。

	1. 创建一个目录作为你主题的名称；
	2. 复制你的HTML/CSS文件和image目录到这个新目录中；
	3. 重新命名你的index.html文件为index.php；
	4. 打开style.css文件然后在顶部增加如下的代码片段：
	 .. code-block:: css

		/*
		Theme Name: Open Source Online Magazine chapter 3
		Theme URI: http://rachelmccollin.co.uk/opensourcemagazine
		Author: Tessa Blakeley Silver / Rachel McCollin
		Author URI:
		Description: Theme to accompany WordPress 3.4 Theme Development
		Beginners Guide
		Version:
		Tags:
		*/
	
	5. 保存你的样式；
	6. 激活该主题，并查看效果；

接下来，为index.php关联样式表。为了让index.php读取你的style.css页面，我们需要在index.php文件中，添加如下两种形式：

.. code-block:: html

	<link rel="stylesheet" href="[stylesheet name]">

或者

.. code-block:: html

	<style>@import "[stylesheet name]"</style>

动作——获取你的CSS样式来显示
-----------------------------
第一个最好，是我们使用的方法。

1. 首先，找到如下代码：

 .. code-block:: html

	<link media="all" rel="stylesheet" type="text/css" href="style.css" />
2. 在href属性中使用如下的 ``bloginfo('stylesheet_url')`` 模板标签：

 .. code-block:: html

	<link media="all" rel="stylesheet" type="text/css" href="<?php bloginfo('stylesheet_url'); ?>" />

理解WordPress模板标签和钩子
===========================
在即将到来的部分中，我们将讨论更多的模板标签和几个API钩子这将有助于我们的模板玩插件。我们来看一下模板的基础知识标签和挂钩。

模板标签
--------
在WordPress系统中具有很多的模板标签具有类似功能的函数。最大区别就是模板标签具有一个内置的返回，以便它们可以自动显示WordPress内容。而函数将会显示内容，除非你使用一个PHP的echo或者print命令来指定其显示。例如：

.. code-block:: php

	<? php the_author_meta( 'name' );?>

具有类似功能的函数如下：

.. code-block:: php

	<? php echo get_author_meta( 'name' );?>

当你不想要内容立即显示，WordPress函数非常有用，对于您在模板的function.php文件中编写的特殊脚本说，我们将在下一章中介绍，或者如果您开始开发定制插件为WordPress添加额外的功能。

钩子
----
钩子是插件API的一部分，是WordPress插件开发者最常用的被用来访问和操作数据，然后在主题中在某些点服务或执行。您的主题确实需要一些准备才能与大多数插件一起工作。

钩子包括action钩子，用于添加内容或函数，并且filter钩子，用于操纵数据输出的方式。

我们将使用的最重要的钩子是wp_head()钩子。这允许激活的插件在WordPress主题的标题文件（例如CSS链接）中写入信息。

循环——WordPress循环
---------------------
在bloginfo模板标签之后，下一个（也许是最重要的）增加到我们主题的WordPress代码是循环。循环本质是你的一个WordPress主题部分。 它按时间顺序显示您的帖子，并允许您使用各种WordPress模板标签和查询包装在您的HTML标记自定义显示属性。

动手——创建一个基础循环
------------------------
我们首先将以下循环代码放This Month标题头下的最宽列中，覆盖样本内容。这个代码是最基本的循环。我们将看到，稍后将需要进行一些调整，以配合我们的主题设计。



