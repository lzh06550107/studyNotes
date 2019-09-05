====
Mix
====

简介
====
`Laravel Mix <https://github.com/JeffreyWay/laravel-mix>`_ 提供了简洁且可读性高的 ``API`` ，用于使用几个常见的 ``CSS`` 和 ``JavaScript`` 预处理器为应用定义 ``Webpack`` 构建步骤。可以通过简单链式调用来定义资源的编译。例如：

.. code-block:: js

    mix.js('resources/assets/js/app.js', 'public/js')
       .sass('resources/assets/sass/app.scss', 'public/css');

如果你曾经对于使用 ``Webpack`` 及编译资源感到困惑和不知所措，那么你会爱上 Laravel ``Mix`` 。当然，Laravel 也并没有强迫你一定要使用 ``Mix`` ，你可以自由使用任何你喜欢的资源编译工具，或者不用也行。

安装 & 配置
===========

安装 Node
---------
在开始使用 ``Mix`` 之前，必须先确保你的机器上安装了 ``Node.js`` 和 ``NPM`` 。

.. code-block:: shell

    node -v
    npm -v

默认情况下， Laravel ``Homestead`` 会包含你所需的一切。当然，如果你没有使用 ``Vagrant`` ，就使用简单的图形安装程序从 其下载页面 安装最新版的 ``Node`` 和 ``NPM`` 。

Laravel Mix
-----------
然后就只需要安装 Laravel ``Mix`` 。在新的 Laravel 项目中，你可以在目录结构的根目录中找到一个 ``package.json`` 文件，它包括了运行基本的 ``Mix`` 所需的内容。就如同 ``composer.json`` 文件，只不过它定义的是 Node 的依赖而不是 PHP 。你可以使用以下的命令安装它引用的依赖项：

.. code-block:: shell

    npm install

运行 Mix
=========
``Mix`` 是位于 `Webpack <https://webpack.js.org/>`_ 顶部的配置层，所以要运行 ``Mix`` 任务，只需要执行默认的 Laravel ``package.json`` 文件中包含的一个 ``NPM`` 脚本：

.. code-block:: shell

    // 运行所有 Mix 任务...
    npm run dev

    // 运行所有 Mix 任务并缩小输出..
    npm run production

监控资源文件修改
----------------
``npm run watch`` 会在你的终端里持续运行，监控所有相关的资源文件以便进行更改。 ``Webpack`` 会在检测到文件更改时自动重新编译资源：

.. code-block:: shell

    npm run watch

在某些环境中，当文件更改时， Webpack 不会更新。如果系统出现这种情况，请考虑使用 ``watch-poll`` 命令：

.. code-block:: shell

    npm run watch-poll

使用样式
========
``webpack.mix.js`` 文件是所有资源编译的入口点。可以把它看作是 ``Webpack`` 中的轻量级配置封装清单。 ``Mix`` 任务可以一起被链式调用，以精确定义资源的编译方式。

Less
----
``less`` 方法可以用于将 ``Less`` 编译为 ``CSS`` 。在 ``webpack.mix.js`` 中这样写，可以将 ``app.less`` 编译到 ``public/css/app.css`` 中。

.. code-block:: js

    mix.less('resources/assets/less/app.less', 'public/css');

可以多次调用 ``less`` 方法来编译多个文件:

.. code-block:: js

    mix.less('resources/assets/less/app.less', 'public/css')
       .less('resources/assets/less/admin.less', 'public/css');

如果要自定义编译的 ``CSS`` 的文件名，可以将一个完整的路径作为第二个参数传给 ``less`` 方法:

.. code-block:: js

    mix.less('resources/assets/less/app.less', 'public/stylesheets/styles.css');

如果你需要重写 底层 ``Less``  `插件选项 <https://github.com/webpack-contrib/less-loader#options>`_ ，你可以将一个对象作为第三个参数传到 ``mix.less()`` ：

.. code-block:: js

    mix.less('resources/assets/less/app.less', 'public/css', {
        strictMath: true
    });

Sass
----
``sass`` 方法可以将 ``Sass`` 编译为 ``CSS`` 。用法如下所示：

.. code-block:: js

    mix.sass('resources/assets/sass/app.scss', 'public/css');

跟 ``less`` 方法一样，你可以将多个 ``Sass`` 文件编译到各自的 ``CSS`` 文件中，甚至可以自定义生成的 ``CSS`` 的输出目录：

.. code-block:: js

    mix.sass('resources/assets/sass/app.sass', 'public/css')
       .sass('resources/assets/sass/admin.sass', 'public/css/admin');

另外， ``Node-Sass`` `插件选项 <https://github.com/sass/node-sass#options>`_ 也同样可以作为第三个参数：

.. code-block:: js

    mix.sass('resources/assets/sass/app.sass', 'public/css', {
        precision: 5
    });

Stylus
------
类似于 ``Less`` 和 ``Sass`` ， ``stylus`` 方法可以将 ``Stylus`` 编译为 ``CSS`` ：

.. code-block:: js

    mix.stylus('resources/assets/stylus/app.styl', 'public/css');

你也可以安装其他的 ``Stylus`` 插件，例如 ``Rupture`` 。首先，通过 NPM ( ``npm install rupture`` ) 来安装插件，然后在调用 ``mix.stylus()`` 时引用它：

.. code-block:: js

    mix.stylus('resources/assets/stylus/app.styl', 'public/css', {
        use: [
            require('rupture')()
        ]
    });

PostCSS
-------
Laravel ``Mix`` 自带了一个用来转换 ``CSS`` 的强大工具 ``PostCSS`` 。默认情况下， ``Mix`` 利用了流行的 ``Autoprefixer`` 插件来自动添加所需要的 ``CSS3`` 浏览器引擎前缀。不过，你也可以自由添加任何适合你应用程序的插件。首先，通过 ``NPM`` 安装所需的插件，然后在 ``webpack.mix.js`` 文件中引用它：

.. code-block:: js

    mix.sass('resources/assets/sass/app.scss', 'public/css')
       .options({
            postCss: [
                require('postcss-css-variables')()
            ]
       });

纯 CSS
------
如果你只是想将一些纯 ``CSS`` 样式合并成单个的文件, 你可以使用 ``styles`` 方法。

.. code-block:: js

    mix.styles([
        'public/css/vendor/normalize.css',
        'public/css/vendor/videojs.css'
    ], 'public/css/all.css');

URL 处理
--------
由于 Laravel ``Mix`` 是建立在 ``Webpack`` 之上的，所以了解一些 ``Webpack`` 概念就非常有必要。编译 ``CSS`` 的时候， ``Webpack`` 会重写和优化样式表中对 ``url()`` 的调用。 一开始听起来可能会觉得奇怪，但这确实是一个非常强大的功能。试想一下我们要编译一个包含图片的相对路径的 ``Sass`` 文件:

.. code-block:: css

    .example {
        background: url('../images/example.png');
    }

.. note:: 任何给定 ``url()`` 的绝对路径会被排除在 ``URL`` 重写之外。例如 ``url('/images/thing.png')`` 或者 ``url('http://example.com/images/thing.png')`` 不会被修改。

默认情况下，Laravel ``Mix`` 和 ``Webpack`` 会找到 ``example.png`` ，然后把它复制到你的 ``public/images`` 目录下，然后重写生成的样式中的 ``url()`` 。这样，你编译之后的 ``CSS`` 会变成：

.. code-block:: css

    .example {
      background: url(/images/example.png?d41d8cd98f00b204e9800998ecf8427e);
    }

但如果你想以你喜欢的方式配置现有的文件夹结构，可以禁用 ``url()`` 的重写：

.. code-block:: js

    mix.sass('resources/assets/app/app.scss', 'public/css')
       .options({
          processCssUrls: false
       });

在你的 ``webpack.mix.js`` 文件像上面这样配置之后， ``Mix`` 将不再匹配 ``url()`` 或者将资源复制到你的 ``public`` 目录。换句话说，编译后的 ``CSS`` 会跟原来输入的一样：

.. code-block:: css

    .example {
        background: url("../images/thing.png");
    }

资源映射
--------
默认情况下资源映射是禁用的，可以在 ``webpack.mix.js`` 文件中调用 ``mix.sourceMaps()`` 方法来开启它。尽管它会带来一些编译／性能的成本，但在使用编译资源时，可以为使用浏览器的开发人员工具提供额外的调试信息：

.. code-block:: js

    mix.js('resources/assets/js/app.js', 'public/js')
       .sourceMaps();

使用脚本
========
``Mix`` 提供了一些函数来处理 JavaScript 文件，像是编译 ECMAScript 2015、模块绑定、压缩以及简单地合并纯 JavaScript 文件。更棒的是，这些操作都不需要进行任何自定义的配置：

.. code-block:: js

    mix.js('resources/assets/js/app.js', 'public/js');

仅仅这上面的一行代码，就支持：

- ES 2015 语法
- 模块
- 编译 .vue 文件
- 生产环境压缩代码

提取依赖库
----------
将应用程序特定的 JavaScript 与依赖库捆绑在一起有个潜在的缺点，会使得长期缓存更加困难。例如，即使应用程序使用的依赖库没有被更改，只要有代码被单独更新，都会强制浏览器重新下载所有依赖库。

如果你打算频繁更新应用程序的 JavaScript ，应该考虑将所有的依赖库提取到自己的文件中。如此一来，应用程序代码的更改就不会影响到大型 ``vendor.js`` 文件的缓存。而 ``Mix`` 的 ``extract`` 方法能使之变得轻而易举：

.. code-block:: js

    mix.js('resources/assets/js/app.js', 'public/js')
       .extract(['vue'])

``extract`` 方法接受一个数组参数。这个数组是要提取到 ``vendor.js`` 文件中的所有的依赖库或模块。比如上面的例子中， ``Mix`` 将生成以下文件：

- ``public/js/manifest.js`` : ``Webpack`` 运行清单
- ``public/js/vendor.js`` : 第三方库
- ``public/js/app.js`` : 应用代码

务必按照以下文件顺序加载，以防 JavaScript 报错：

.. code-block:: html

    <script src="/js/manifest.js"></script>
    <script src="/js/vendor.js"></script>
    <script src="/js/app.js"></script>

React
------
``Mix`` 会自动安装 ``React`` 必要的 ``Babel`` 插件。只需替换 ``mix.js()`` 为 ``mix.react()`` ：

.. code-block:: js

    mix.react('resources/assets/js/app.jsx', 'public/js');

``Mix`` 会在后台自动下载包括 ``babel-preset-react`` Babel 插件在内的库。


原生 JS
-------
类似 ``mix.styles()`` 合并样式表文件， ``scripts()`` 方法可以合并压缩任意数量的 JavaScript 文件：

.. code-block:: js

    mix.scripts([
        'public/js/admin.js',
        'public/js/dashboard.js'
    ], 'public/js/all.js');

对那些没有用 ``Webpack`` 编译 JavaScript 的旧项目来说，这个方法非常实用。

.. tip:: ``mix.babel()`` 与 ``mix.scripts()`` 还是有些差别的。在使用上与 ``scripts`` 一致，但是，所有文件都会经过 Babel 编译， 任何 ES2015 语法的代码都会编译成所有浏览器都支持的原生 JavaScript。

自定义 Webpack 配置
===================
Laravel ``Mix`` 在背后引用一个预配置的 ``webpack.config.js`` 文件加速启动与运行。有时，可能有一个特殊的加载器或需要引用的插件，或倾向于 ``Stylus`` 而不是 ``Sass`` ，而不得不去手动修改这个文件。这种情况下，有两个选择：

合并自定义配置
--------------
``Mix`` 提供了一个 ``webpackConfig`` 方法来合并任何 ``Webpack`` 配置以覆盖默认配置。因此你不需要复制和维护 ``webpack.config.js`` 的文件副本。 ``webpackConfig`` 方法接受一个包含任何要应用的 ``Webpack`` `配置项 <https://webpack.js.org/configuration/>`_ 的对象：

.. code-block:: js

    mix.webpackConfig({
        resolve: {
            modules: [
                path.resolve(__dirname, 'vendor/laravel/spark/resources/assets/js')
            ]
        }
    });

自定义配置文件
--------------
如果想完全自定义 ``Webpack`` 配置，就将 ``node_modules/laravel-mix/setup/webpack.config.js`` 文件复制到项目的根目录。然后在 ``package.json`` 文件中将所有 ``--config`` 的值指向新复制的配置文件。采用这种方法进行自定义，如果后续 ``Mix`` 版本有更新时，需要手动合并 ``webpack.config.js`` 并到你的自定义文件中。


复制文件 & 目录
===============
``copy`` 方法用于将文件和目录复制到新位置。当 ``node_modules`` 目录中的特定资源需要被重定位到 ``public`` 文件夹时会很有用。

.. code-block:: js

    mix.copy('node_modules/foo/bar.css', 'public/css/bar.css');

复制目录时， ``copy`` 方法会平面化目录的结构。要维护目录的原始结构，应该使用 ``copyDirectory`` 方法：

.. code-block:: js

    mix.copyDirectory('assets/img', 'public/img');

版本控制／缓存清除
==================
许多的开发者会对其编译的资源文件中加上时间戳或是唯一的令牌作为后缀，以此来强迫浏览器加载全新的资源文件，而不是旧版本的代码副本。你可以使用 ``Mix`` 的 ``version`` 方法处理它们。

``version`` 方法会自动为所有编译文件的文件名附加一个唯一的哈希值，从而实现更方便的缓存清除功能：

.. code-block:: js

    mix.js('resources/assets/js/app.js', 'public/js')
   .version();

生成版本化文件后，你不会知道确切的文件名。因此，你应该在你的视图中使用 Laravel 的全局辅助函数 ``mix`` 来正确加载名称被哈希后的文件。 ``mix`` 函数会自动确定被哈希的文件名称：

.. code-block:: html

    <link rel="stylesheet" href="{{ mix('/css/app.css') }}">

因为在开发中通常是不需要版本化，你可以指示版本控制过程仅在 ``npm run production`` 运行期间进行：

.. code-block:: js

    mix.js('resources/assets/js/app.js', 'public/js');

    if (mix.inProduction()) {
        mix.version();
    }

Browsersync 重新加载
====================
`BrowserSync <https://browsersync.io/>`_ 会自动监控文件修改并将修改注入浏览器而无需手动刷新。 你可以通过调用 ``mix.browserSync()`` 方法启用该支持：

.. code-block:: js

    mix.browserSync('my-domain.test');

    // Or...

    // https://browsersync.io/docs/options
    mix.browserSync({
        proxy: 'my-domain.test'
    });

你可以传递一个字符串（代理）或对象（ ``BrowserSync`` 设置）到该方法。 接下来，使用 ``npm run watch`` 命令来启动 ``Webpack`` 的开发服务器。现在，当你编辑一个 JavaScript 脚本或 PHP 文件时，会看到浏览器会立即刷新以响应你的修改。

环境变量
========
你可以通过在 ``.env`` 文件添加 ``MIX_`` 前缀将环境变量注入 ``Mix`` ：

.. code-block:: ini

    MIX_SENTRY_DSN_PUBLIC=http://example.com

在 ``.env`` 文件中定义号变量之后，可以通过 ``process.env`` 对象进行访问。如果在运行 ``watch`` 任务期间变量值有变动，需要重启任务：

.. code-block:: js

    process.env.MIX_SENTRY_DSN_PUBLIC

通知
====
正常情况下， ``Mix`` 会自动为每个捆绑显示操作系统通知，这可以给你一个及时的反馈：编译成功还是失败。不过，某些场景下你可能希望禁止这些通知，一个典型的例子就是在生产境服务器触发 ``Mix`` 。通知可以通过 ``disableNotifications`` 方法被停用：

.. code-block:: js

    mix.disableNotifications();