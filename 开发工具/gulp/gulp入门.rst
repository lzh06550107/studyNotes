******
gulp入门
******
``Gulp`` 是一个使用 ``Node.js`` 构建的流式 ``JavaScript`` 构建系统，它利用流和代码转换配置的功能，可以非常快速有效地自动化，组织和运行开发任务。 通过简单地创建一个小的指令文件， ``Gulp`` 可以执行您能想到的任何开发任务。

``Gulp`` 使用小型单用途插件来修改和处理项目文件。此外，您可以将这些插件链接或管道化为更复杂的操作，并完全控制这些操作的执行顺序。

安装 gulp 命令行工具
===================

1. 安装node；
2. npm install -g grunt-cli；
3. mkdir gulpDemo；cd gulpDemo；
4. npm init 生成package.json文件；

初始化项目
=========

1. gulp init 创建 gulpfile.js 文件；

.. code-block:: js

	var gulp = require('gulp');

	gulp.task('hello', function() {
		console.log('您好！');
	});

	gulp.task('default', ['hello']); // 定义默认命令

创建项目结构：

.. code-block:: shell

	gulp-book/
		- app/
			- css/
				- main.css
				- secondary.css
			- img/
			- js/
				- main.js
				- secondary.js
		- index.html
		- gulpfile.js

index.html

.. code-block:: html

	<!DOCTYPE html>
	<html>
	<head>
		<meta charset="utf-8">
		<title>Gulp Book Project</title>
		<link rel="stylesheet" href="dist/all.css" />
	</head>
	<body>
		<div id="core">
		<div class="box">
			<img src="dist/img/gulp.png" alt="Gulp Logo" class="gulp-logo">
			<h1>Gulp Book Example</h1>
			<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Fugiat atque unde do
			<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Reiciendis dignissim
			<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Quos ut possimus, re
			</div>
		</div>
		<script src="dist/all.js"></script>
	</body>
	</html>

main.css

.. code-block:: css

	/* Variables */
	:root {
		/* Colors */
		--red: #F05D5D;
		--green: #59C946;
		--blue: #6F7AF1;
		--white: #FFFFFF;
		--grey: #EEEEEE;
		--black: #000000;
	}

	body {font:300 16px sans-serif; background:var(--grey);}

secondary.css

.. code-block:: css

	#header {padding:2em; background: var(--blue);}
	#core {width:80%; max-width:900px; margin:0 auto;}
	#footer {padding:2em; background: var(--red);}
	.box {padding:2em; background:var(--white);}
	.gulp-logo {width:125px; margin:0 auto; display:block;}

main.js

.. code-block:: js

	console.log("I'm logging from the main.js file.");

secondary.js

.. code-block:: js

    console.log("I'm logging from the secondary.js file.");

基础
====

gulp 基础
---------
``Gulp`` 从四个主要方法开始： ``.task（）`` ， ``.src（）`` ， ``.watch（）`` 和  ``.dest（）`` 版本 4.x 的发布引入了其他方法，如 ``.series（）`` 和 ``.parallel（）`` 。 除了 ``gulp API`` 方法之外，每个任务还将使用 ``Node.js .pipe（）`` 方法。

这个小方法列表是了解如何开始编写基本任务所需的全部内容。它们各自代表一个特定的目的，并将作为我们的 ``gulpfile`` 的构建块。

task()
^^^^^^
``.task()`` 方法是我们创建任务的基本包装器。 它的语法是 ``task(string，function)`` 。 它需要两个参数 - 表示任务名称的字符串值和一个包含您希望在运行该任务时执行的代码的函数。

src()
^^^^^^
``.src()`` 方法是我们的输入或我们如何获得对我们计划修改的源文件的访问。 它接受单个 ``glob`` 字符串或 ``glob`` 字符串数组作为参数。  ``Globs`` 是一种模式，我们可以用它来使我们的路径更具活力。 使用 ``globs`` 时，我们可以使用通配符将整个文件集与单个字符串匹配，而不是单独列出它们。此方法的语法是 ``src(string || array)``

watch()
^^^^^^^
``.watch()`` 方法用于专门查找文件中的更改。这将允许我们在编码时保持 ``gulp`` 运行，这样我们就不需要在需要处理任务时重新运行 ``gulp`` 。  3.x 和 4.x 版本之间的语法不同。

对于版本 3.x ，语法是 ``watch(string || array，array)`` ，第一个参数是我们要观察的路径 ``/globs`` ，第二个参数是在这些文件更改时需要运行的任务名称数组。

对于版本 4.x ，语法已经改变了一点，以允许两个新方法提供对任务执行顺序的更明确控制。使用 4.x 时，我们将使用 ``series()`` 或 ``parallel()`` 方法，如 ``watch(string || array，gulp.series() || gulp.parallel())`` ，而不是传入一个数组作为第二个参数。

dest()
^^^^^^
``dest()`` 方法用于设置已处理文件的输出目标。通常，这将用于将我们的数据输出到作为库共享的 ``build`` 或由应用程序访问的 ``dist`` 文件夹中。 此方法的语法是 ``dest(string)`` 。

pipe()
^^^^^^
``pipe()`` 方法将允许我们将较小的单用途插件或应用程序组合到 ``pipechain`` 中。这使我们能够完全控制处理文件所需的顺序。此方法的语法是 ``pipe(function)`` 。

parallel() 和 series()
^^^^^^^^^^^^^^^^^^^^^^^
版本 4.x 中添加了并行和串行方法，以便轻松控制您的任务是一次性一起运行还是一个接一个地按顺序运行。如果您的某个任务要求在成功运行之前完成其他任务，这两个方法就很重要。使用这些方法时，参数将是您的任务的字符串名称，以逗号分隔。 这些方法的语法是 ``series(tasks)`` 和 ``parallel(tasks)`` 。

包含模块/插件
------------
编写 ``gulp`` 文件时，您将始终包含要在任务中使用的模块或插件。根据您的需求，这些可以是 ``gulp`` 插件和 ``Node.js`` 模块。 ``Gulp`` 插件是为在 ``gulp`` 内部使用而构建的小型 ``Node.js`` 应用程序，用于提供单一用途的操作，并且可以链接在一起以为您的数据创建复杂的操作。 ``Node.js`` 模块具有更广泛的用途，可以与 ``gulp`` 一起使用或独立使用。

打开 gulpfile.js 文件，添加如下代码：

.. code-block:: js

	// Load Node Modules/Plugins
	var gulp = require('gulp');
	var concat = require('gulp-concat');
	var uglify = require('gulp-uglify');

这是 ``Node.js`` 处理模块化的方式，并且因为 ``gulpfile`` 本质上是一个小型 ``Node.js`` 应用程序，所以它也采用了这种做法。

创建一个任务
-----------

.. code-block:: js

	gulp.task(name, function() {
		return gulp.src(path) // 指定输入路径
		.pipe(plugin) // 管道方法指定要使用插件，注意插件的顺序决定处理顺序
		.pipe(plugin)
		.pipe(gulp.dest(path)); // 指定输出路径
	});

下面是一个完整的配置：

.. code-block:: js

	// Load Node Modules/Plugins
	var gulp = require('gulp');
	var concat = require('gulp-concat');
	var uglify = require('gulp-uglify');
	// Process Styles
	gulp.task('styles', function() {
		return gulp.src('css/*.css')
		.pipe(concat('all.css'))
		.pipe(gulp.dest('dist/'));
	});
	// Process Scripts
	gulp.task('scripts', function() {
		return gulp.src('js/*.js')
		.pipe(concat('all.js'))
		.pipe(uglify())
		.pipe(gulp.dest('dist/'));
	});
	// Watch Files For Changes
	gulp.task('watch', function() {
		gulp.watch('css/*.css', 'styles');
		gulp.watch('js/*.js', 'scripts');
	});
	// Default Task
	gulp.task('default', gulp.parallel('styles', 'scripts', 'watch'));

使用gulp执行任务
----------------
我们的项目将创建单独的任务来处理 ``CSS`` ， ``JavaScript`` 和图像。对于 ``CSS`` ，我们将所有文件合并到一个文件中，然后对其进行预处理以在代码中启用其他功能。对于 ``JavaScript`` ，我们将合并文件，检查代码是否有错误，并将其缩小以减小整体文件大小。对于图像，我们将使用插件来压缩和优化它们，以便我们的项目能够更快，更高效地加载。



复制单个文件
-----------


复制多个文件
-----------


globs
------



多个 globs
----------


排除
----


主任务
------


文件有变化时自动执行任务
----------------------


插件
====

插件
----


编译 Sass：gulp-sass
--------------------


编译 Less：gulp-less
--------------------


创建本地服务器：gulp-connect
---------------------------


实时预览
--------


合并文件：gulp-concat
---------------------


最小化 js 文件：gulp-uglify
--------------------------


重命名文件：gulp-rename
-----------------------


最小化 css 文件：gulp-minify-css
--------------------------------


最小化图像：gulp-imagemin
-------------------------


