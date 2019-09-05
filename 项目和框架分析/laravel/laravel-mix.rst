***********
mix使用手册
***********
`参考文档 <https://github.com/JeffreyWay/laravel-mix/blob/master/docs/installation.md>`_ 

基本例子
========
``laravel mix`` 是 ``webpack`` 的一个封装，可以让80％的用例变得简单易用。大多数人会赞同，尽管 ``webpack`` 功能强大，但它的学习曲线陡峭。但是你不必担心这个问题。

看看一个 ``webpack.mix.js`` 基础文件。让我们想象一下，我们只需要 ``javascript`` 和 ``sass`` 编译，则需要如下代码：

.. code-block:: javascript

    let mix = require('laravel-mix');

    mix.sass('src/app.sass', 'dist').js('src/app.js', 'dist');

是不是简单？

1. 编译 ``Sass`` 文件，从 ``./src/app.sass`` 读取，然后编译到 ``./dist/app.css`` ；
2. 打包所有位于 ``./src/app.js`` 脚本(包含任何需要的模块)到 ``./dist/app.js`` ；

使用该配置文件，我们可以使用一下命令触发 ``webpack`` :

.. code-block:: shell

    node_modules/.bin/webpack

在开发过程中，没有必要缩小输出，但是当在生产环境 ``export NODE_ENV=production webpack`` 触发 ``webpack`` 时，会自动执行缩小功能。

如果你更喜欢使用 ``Less`` 编译，你只需要切换 ``mix.sass()`` 为 ``mix.less()`` 。

使用 ``Laravel Mix`` ，你会发现通常的 ``webpack`` 任务变得简单了。

安装
====
虽然 ``laravel mix`` 是针对 ``laravel`` 使用而优化的，但它可以用于任何类型的应用程序。

Laravel 项目
------------
``laravel`` 提供您开始所需的一切。你只需要：

- 安装 ``Laravel`` ；
- 运行 ``npm install`` ；
- 查看 ``webpack.mix.js`` 文件 ；

现在，在命令行中，你可以运行 ``npm run watch`` 来监控改变的文件，然后重新编译。

.. note:: 你不会在你的项目根目录下找到一个 ``webpack.config.js`` 文件。默认情况下，laravel 推迟本库中的配置文件。但是，如果需要对其进行配置，则可以将该文件复制到项目根目录，然后相应地更新您的 ``package.json`` npm脚本： ``cp node_modules/laravel-mix/setup/webpack.config.js ./`` 。

独立项目
--------
首先通过 ``npm`` 或 ``yarn`` 安装 ``laravel mix`` ，然后将示例 ``Mix`` 文件复制到项目根目录。

.. code-block:: shell

    mkdir my-app && cd my-app
    npm init -y
    npm install laravel-mix --save-dev
    cp -r node_modules/laravel-mix/setup/webpack.mix.js ./

你现在应该具有如下目录结构：

- ``node_modules/``
- ``package.json``
- ``webpack.mix.js``

``webpack.mix.js`` 是 ``webpack`` 之上的配置层。你大部分时间都会在这里度过。

转到你的 ``webpack.mix.js`` 文件：

.. code-block:: js

    let mix = require('laravel-mix');

    mix.js('src/app.js', 'dist')
   .sass('src/app.scss', 'dist')
   .setPublicPath('dist');

注意源路径，并创建匹配的目录结构（或者，当然，将它们更改为您喜欢的结构）。你现在都准备好了。通过从命令行运行 ``node_modules/.bin/webpack`` 编译所有内容。你现在应该看到：

- ``dist/app.css``
- ``dist/app.js``
- ``dist/mix-manifest.json``

npm脚本
--------
作为提示，考虑将以下 ``npm`` 脚本添加到 ``package.json`` 文件中，以加快工作流程。 ``laravel`` 安装将已经包含这个。

.. code-block:: js

    "scripts": {
        "dev": "NODE_ENV=development node_modules/webpack/bin/webpack.js --progress --hide-modules --config=node_modules/laravel-mix/setup/webpack.config.js",
        "watch": "NODE_ENV=development node_modules/webpack/bin/webpack.js --watch --progress --hide-modules --config=node_modules/laravel-mix/setup/webpack.config.js",
        "hot": "NODE_ENV=development webpack-dev-server --inline --hot --config=node_modules/laravel-mix/setup/webpack.config.js",
        "production": "NODE_ENV=production node_modules/webpack/bin/webpack.js --progress --hide-modules --config=node_modules/laravel-mix/setup/webpack.config.js"
  }

Laravel工作流
=============
让我们回顾一下您可以从头开始为自己的项目采用的一般工作流程。

1、安装 ``Laravel``
--------------------

.. code-block:: shell

    laravel new my-app

2、安装 ``Node`` 依赖
----------------------

.. code-block:: shell

    npm install

3、查看 ``webpack.min.js``
--------------------------
将此文件视为您的所有前端配置的主基地。

.. code-block:: js

    let mix = require('laravel-mix');

    mix.js('resources/assets/js/app.js', 'public/js')
        .sass('resources/assets/sass/app.scss', 'public/css');

默认情况下，我们启用了 ``javascript ES2017`` +模块捆绑以及 ``sass`` 编译。

4、编译
--------
继续前进并编译这些。

.. code-block:: shell

    node_modules/.bin/webpack

或者，如果你在 ``package.json`` 中有 ``npm`` 脚本，你可以这样做：

.. code-block:: shell

    npm run dev

一旦命令运行结束，你会看到：

- ``./public/js/app.js``
- ``./public/css/app.css``

接下来，让我们开始工作。监控您的 ``JavaScript`` 的更改，运行：

.. code-block:: shell

    npm run watch

``laravel`` 附带 ``./resources/assets/js/components/Example.vue`` 文件。给它一个调整，并等待OS编译已完成的通知！

.. note:: 您也可以使用 ``mix.browsersync('myapp.test')`` 在 ``laravel`` 应用程序中的任何相关文件发生更改时自动重新加载浏览器。

5、更新你的视图
----------------
再次， ``laravel`` 附带一个欢迎页面。我们可以将其用于演示。将其更新为：

.. code-block:: html

    <!DOCTYPE html>
    <html lang="en">
        <head>
            <meta charset="utf-8">
            <title>Laravel</title>

            <link rel="stylesheet" href="{{ mix('css/app.css') }}">
        </head>
        <body>
            <div id="app">
                <example></example>
            </div>

            <script src="{{ mix('js/app.js') }}"></script>
        </body>
    </html>

并在浏览器中重新加载页面。