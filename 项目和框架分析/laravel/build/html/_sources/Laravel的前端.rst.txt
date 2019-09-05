====
前端
====

简介
====
Laravel 虽然不强制你使用哪个 JavaScript 或 CSS 预处理器，但还是提供了适用多数应用的 ``Bootstrap`` 和 ``Vue`` 来作为起点。默认情况下，Laravel 使用 ``NPM`` 安装这些前端依赖包。

CSS
---
Laravel ``Mix`` 提供了一个简洁、友好的 ``API`` 用于编译 ``SASS`` 或 ``Less`` 。 ``SASS`` 和 ``Less`` 扩展了 ``CSS`` ，添加了变量、 ``mixins`` 等强大特性，使得编写 ``CSS`` 更加轻松。这篇文档将简要讨论 ``CSS`` 大体编译过程，不过，你最好还是查阅完整的 Laravel Mix 文档 获取更多信息。

JavaScript
----------
Laravel 并不强制使用特定 JavaScript 框架或库来构建应用。当然，也可以完全不用 JavaScript 。不过，Laravel 还是提供了一些基本的脚手架，能更容易地使用 ``Vue`` 库编写现代 JavaScript 。 ``Vue`` 提供了一个友好的 ``API`` ，通过组件就可构建强大的 JavaScript 应用。和 CSS 一样，用 Laravel ``Mix`` 就能轻松把多个 JavaScript 组件编译到单个 JavaScript 文件中。

移除前端脚手架
--------------
如果要移除应用的前端脚手架， ``preset Artisan`` 会派上用场。它与选项 ``none`` 配合使用，会移除应用的 ``Bootstrap`` 和 ``Vue`` 脚手架，仅保留一个空 ``SASS`` 文件和几个通用的 JavaScript 库：

.. code-block:: shell

    php artisan preset none

编写 CSS
========
Laravel 的 ``package.json`` 引入了 ``bootstrap`` 包， ``Bootstrap`` 会帮你构建应用的前端雏形。不过，根据需求， ``package.json`` 文件的包可随意添加或删除。构建 Laravel 应用时， ``Bootstrap`` 框架不是必须的，它只是给那些想使用它的人提供了一个好的起点。

在编译 ``CSS`` 之前，须使用 ``Node`` 包管理工具 ( `NPM <https://www.npmjs.org/>`_ ) 安装应用的前端依赖：

.. code-block:: shell

    npm install

运行 ``npm install`` 安装好前端依赖后，就可以使用 Laravel ``Mix`` 将 ``SASS`` 文件编译成原生 ``CSS`` 。 ``npm run dev`` 命令会处理 ``webpack.mix.js`` 文件的指令。通常，编译后的 ``CSS`` 放置在 ``public/css`` 目录下：

.. code-block:: shell

    npm run dev

Laravel 默认的 ``webpack.mix.js`` 会编译 ``SASS`` 文件 ``resources/assets/sass/app.scss`` 。文件 ``app.scss`` 会引入一个包含 ``SASS`` 变量的文件，并加载 ``Bootstrap`` ，这为大多应用提供了一个好的起点。根据需要，文件 ``app.scss`` 可随意修改，甚至通过 配置 Laravel ``Mix`` 使用一个完全不同的预处理器。

编写 JavaScript
===============
项目所有的 JavaScript 依赖都可以在其根目录下的 ``package.json`` 文件中找到。这个文件和 ``composer.json`` 类似，只是一个指定 JavaScript 依赖，一个指定 PHP 依赖。使用 ``Node`` 依赖管理工具 (NPM) 安装依赖：

.. code-block:: shell

    npm install

.. tip:: 默认情况下，Laravel 的 ``package.json`` 文件引入了 ``vue`` 和 ``axios`` 等包来帮你构建 JavaScript 应用。根据需求， ``package.json`` 可随意添加或移除依赖。

安装好依赖后，就可以使用 ``npm run dev`` 命令 编译资源文件 了。 ``Webpack`` 是为现代 JavaScript 而生的模块打包工具。运行 ``npm run dev`` 命令， ``Webpack`` 会执行 ``webpack.mix.js`` 中的指令：

.. code-block:: shell

    npm run dev

默认情况下，Laravel 自带的 ``webpack.mix.js`` 文件会编译 ``SASS`` 和 ``resources/assets/js/app.js`` 文件。 ``app.js`` 文件中，可以注册 ``Vue`` 组件，或使用其他框架进行配置。通常，编译后的 JavaScript 放在 ``public/js`` 目录下。

.. tip:: 文件 ``app.js`` 会加载 ``resources/assets/js/bootstrap.js`` ，这个文件将引导和配置 ``Vue`` ， ``Axios`` ， ``jQuery`` 以及其他的 JavaScript 依赖。如果有额外的 JavaScript 依赖需要配置，也可以在这里进行。

编写 Vue 组件
-------------
新安装的 Laravel 应用在 ``resources/assets/js/components`` 目录包含一个 ``ExampleComponent.vue`` Vue 组件。 ``ExampleComponent.vue`` 是 单文件 ``Vue`` 组件 的实例，定义了自身的 JavaScript 和 HTML 模板。单文件组件为构建 JavaScript 驱动的应用提供了便利。这个实例组件已经在 ``app.js`` 文件中注册：

.. code-block:: js

    Vue.component(
        'example-component',
        require('./components/ExampleComponent.vue')
    );

想要在应用中使用组件，只需把它放在 HTML 模板里即可。例如，运行 ``make:auth`` Artisan 命令生成应用的认证和注册页面后，就可以把它置入 ``home.blade.php`` Blade 模板：

.. code-block:: html

    @extends('layouts.app')

    @section('content')
        <example-component></example-component>
    @endsection

.. tip:: 谨记，每次修改 ``Vue`` 组件，都应运行 ``npm run dev`` 命令。或者，运行 ``npm run watch`` 监视组件的每次修改，进行自动编译。

需要 ``Vue`` 组件更多信息的话，可以阅读 `Vue文档 <https://vuejs.org/guide/>`_ ，它对整个 ``Vue`` 框架进行了充分的综述。

使用 React
----------
如果倾向 ``React`` 构建 JavaScript 应用，在 Laravel 中将 ``Vue`` 脚手架替换为 ``React`` 脚手架也非难事。在任意新安装的 Laravel 应用中，使用带 ``react`` 选项的 ``preset`` 命令即可：

.. code-block:: shell

    php artisan preset react

这个命令将移除 ``Vue`` 脚手架并替换为 ``React`` 脚手架，组件实例也会相应替换。