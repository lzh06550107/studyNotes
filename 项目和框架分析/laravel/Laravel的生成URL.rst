====
URL
====

简介
====
Laravel 提供了几个辅助函数来为应用程序生成 ``URL`` 。主要用于在模板和 ``API`` 响应中构建 ``URL`` 或者在应用程序的其它部分生成重定向响应。


基础
====

生成基础 URL
------------
辅助函数 ``url`` 可以用于应用的任何一个 ``URL`` 。生成的 ``URL`` 将自动使用当前请求中的方案（ ``HTTP`` 或 ``HTTPS`` ）和主机：

.. code-block:: php

    <?php
    $post = App\Post::find(1);

    echo url("/posts/{$post->id}"); //告诉PHP，括起来的要当成变量处理。

    // http://example.com/posts/1

访问当前 URL
------------
如果没有给辅助函数 ``url`` 提供路径，则会返回一个 ``Illuminate\Routing\UrlGenerator`` 实例，来允许你访问有关当前 ``URL`` 的信息：

.. code-block:: php

    <?php
    // 获取没有查询字符串的当前的 URL ...
    echo url()->current();

    // 获取包含查询字符串的当前的 URL ...
    echo url()->full();

    // 获取上一个请求的完整的 URL...
    echo url()->previous();

上面的这些方法都可以通过 URL facade 访问：

.. code-block:: php

    <?php
    use Illuminate\Support\Facades\URL;

    echo URL::current();

命名路由的 URL
==============
辅助函数 ``route`` 可以用于为指定路由生成 ``URL`` 。命名路由生成的 ``URL`` 方法可以让路由上定义的 ``URL`` 相解耦。因此，就算路由的 ``URL`` 路径有任何更改，都不需要对 ``route`` 函数调用进行任何更改。例如，假设你的应用程序包含以下路由：

.. code-block:: php

    <?php
    Route::get('/post/{post}', function () {
        //
    })->name('post.show');

生成此路由的 ``URL`` ，可以像这样使用辅助函数 ``route`` ：

.. code-block:: php

    <?php
    echo route('post.show', ['post' => 1]);

    // http://example.com/post/1

将 ``Eloquent`` 模型 作为参数值传给 ``route`` 方法，它会自动提取模型的主键来生成 ``URL`` 。

.. code-block:: php

    <?php
    echo route('post.show', ['post' => $post]);

控制器动作的 URL
================
``action`` 功能可以为给定的控制器动作生成 ``URL`` 。这个功能不需要你传递控制器的完整命名空间，但你需要传递相对于命名空间 ``App\Http\Controllers`` 的控制器类名：

.. code-block:: php

    <?php
    $url = action('HomeController@index');

如果控制器方法需要路由参数，那就将它们作为第二个参数传递给 ``action`` 函数：

.. code-block:: php

    <?php
    $url = action('UserController@profile', ['id' => 1]);

默认值
======
对于某些应用程序，你可能希望为某些 ``URL`` 参数的请求范围指定默认值。例如，假设有些路由定义了 ``{locale}`` 参数：

.. code-block:: php

    <?php
    Route::get('/{locale}/posts', function () {
        //
    })->name('post.index');

每次都通过 ``locale`` 来调用辅助函数 ``route`` 也是一件很麻烦的事情。因此，使用 ``URL::defaults`` 方法定义这个参数的默认值，可以让该参数始终存在当前请求中。然后就能从 路由中间件 调用此方法来访问当前请求：

.. code-block:: php

    <?php

    namespace App\Http\Middleware;

    use Closure;
    use Illuminate\Support\Facades\URL;

    class SetDefaultLocaleForUrls
    {
        public function handle($request, Closure $next)
        {
            URL::defaults(['locale' => $request->user()->locale]);

            return $next($request);
        }
    }

一旦设置了 ``locale`` 参数的默认值，您就不再需要通过辅助函数 ``route`` 生成 ``URL`` 时传递它的值。

