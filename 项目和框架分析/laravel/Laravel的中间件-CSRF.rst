==========
中间件CSRF
==========

简介
====
Laravel 可以轻松地保护应用程序免受 跨站点请求伪造 (CSRF) 攻击，跨站点请求伪造是一种恶意攻击，它凭借已通过身份验证的用户身份来运行未经过授权的命令。

Laravel 会自动为每个活跃用户的会话生成一个 ``CSRF`` 「令牌」。该令牌用于验证经过身份验证的用户是否是曾经向应用程序发出过请求的用户。

无论何时，当您在应用程序中定义 ``HTML`` 表单时，都应该在表单中包含一个隐藏的 ``CSRF`` 标记字段，以便 ``CSRF`` 保护中间件可以验证该请求， 您可以使用 ``@csrf`` Blade 指令来生成令牌字段：

.. code-block:: php

    <?php
    <form method="POST" action="/profile">
        @csrf
        ...
    </form>

包含在 ``web`` 中间件组里的 ``VerifyCsrfToken`` 中间件 会自动验证请求里的令牌是否与存储在会话中令牌匹配。

CSRF 令牌 & JavaScript
----------------------
构建由 ``Javascript`` 驱动的应用时，可以很方便地让 Javascript HTTP 函数库在发起每一个请求时自动附上 ``CSRF`` 令牌。默认情况下，  ``resources/assets/js/bootstrap.js`` 文件会用 ``Axios HTTP`` 函数库注册的 ``csrf-token meta`` 标签中的值。如果你不使用这个函数库，你需要手动为你的应用配置此行为。

CSRF 白名单
===========
有时候你可能希望设置一组并不需要 ``CSRF`` 保护的 ``URI`` 。例如，如果你正在使用 ``Stripe`` 处理付款并使用了他们的 ``webhook`` 系统，你会需要从 ``CSRF`` 的保护中排除 ``Stripe Webhook`` 处理程序路由，因为 ``Stripe`` 并不会给你的路由发送 ``CSRF`` 令牌。

你可以把这类路由放到 ``routes/web.php`` 外，因为 ``RouteServiceProvider`` 的 ``web`` 中间件适用于该文件中的所有路由。不过，你也可以通过将这类 ``URI`` 添加到 ``VerifyCsrfToken`` 中间件中的 ``$except`` 属性来排除对这类路由的 ``CSRF`` 保护：

路由放到 ``routes/web.php`` 外，可以在 ``\App\Providers\RouteServiceProvider`` 的 ``map()`` 方法中定义路由。

.. code-block:: php

    <?php
    namespace App\Http\Middleware;

    use Illuminate\Foundation\Http\Middleware\VerifyCsrfToken as Middleware;

    class VerifyCsrfToken extends Middleware
    {
        /**
         * 这些 URI 将免受 CSRF 验证
         *
         * @var array
         */
        protected $except = [
            'stripe/*',
            'http://example.com/foo/bar',
            'http://example.com/foo/*',
        ];
    }

.. tip:: 当 运行测试 时， ``CSRF`` 中间件会自动禁用。

X-CSRF-TOKEN
============
除了检查 ``POST`` 参数中的 ``CSRF`` 令牌外， ``VerifyCsrfToken`` 中间件还会检查 ``X-CSRF-TOKEN`` 请求头。你可以将令牌保存在 HTML ``meta`` 标签中：

.. code-block:: html

    <meta name="csrf-token" content="{{ csrf_token() }}">

然后，一旦创建了 ``meta`` 标记，就可以指示像 ``jQuery`` 的库自动将令牌添加到所有请求的头信息中。这可以为基于 ``AJAX`` 的应用提供简单、方便的 ``CSRF`` 保护：

.. code-block:: js

    $.ajaxSetup({
        headers: {
            'X-CSRF-TOKEN': $('meta[name="csrf-token"]').attr('content')
        }
    });

.. tip::  默认情况下， ``resources/assets/js/bootstrap.js`` 文件会用 ``Axios HTTP`` 函数库注册 ``csrf-token meta`` 标签中的值。如果你不使用这个函数库，则需要为你的应用手动配置此行为。

X-XSRF-TOKEN
============
Laravel 将当前的 ``CSRF`` 令牌存储在一个 ``XSRF-TOKEN cookie`` 中，该 ``cookie`` 包含在框架生成的每个响应中。 您可以使用 ``cookie`` 值来设置 ``X-XSRF-TOKEN`` 请求标头。

这个 ``cookie`` 主要是作为一种方便的方式发送的，因为一些 JavaScript 框架和库，例如 ``Angular`` 和 ``Axios`` ，会自动将它的值放入 ``X-XSRF-TOKEN`` 头中。

.. note:: ``X-CSRF-TOKEN`` 和 ``X-XSRF-TOKEN`` 之间的区别在于第一个使用纯文本值而后者使用加密值，因为 Laravel 中的 ``cookie`` 始终是加密的。如果使用 ``csrf_token()`` 函数提供标记值，则可能需要使用 ``X-CSRF-TOKEN`` 标头。需要设置两个是因为不同的库采用不用的请求头。
















