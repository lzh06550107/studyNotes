==============
异常和错误处理
==============

介绍
====
当你启动一个新的 Laravel 项目时，错误及异常处理是已为你配置好了的。 ``App\Exceptions\Handler`` 类负责记录应用程序触发的所有异常并呈现给用户。在本文档中，我们将深入探讨这个类。


配置
====
``config/app.php`` 配置文件中的 ``debug`` 选项决定了对于一个错误实际上将显示多少信息给用户。默认情况下，该选项的设置将遵照存储在 ``.env`` 文件中的 ``APP_DEBUG`` 环境变量的值。

对于本地开发，你应该将 ``APP_DEBUG`` 环境变量的值设置为 ``true`` 。在生产环境中，该值应始终为 ``false`` 。如果在生产中将该值设置为 ``true`` ，则可能会将敏感配置值暴露给应用程序的最终用户。

异常处理器
==========
Report 方法
-----------
所有异常都是由 ``App\Exceptions\Handler`` 类处理的。这个类包含两个方法： ``report`` 和 ``render`` 。我们将详细剖析这些方法。 ``report`` 方法用于记录异常或将它们发送给如 ``Bugsnag`` 或 ``Sentry`` 等外部服务。默认情况下， ``report`` 方法将异常传递给记录异常的基类。不过，你可以任何自己喜欢的方式来记录异常。

例如，如果你需要以不同方式报告不同类型的异常，则可以使用 PHP instanceof 比较运算符：

.. code-block:: php

    <?php
    /**
     * Report or log an exception.
     *
     * This is a great spot to send exceptions to Sentry, Bugsnag, etc.
     *
     * @param  \Exception  $exception
     * @return void
     */
    public function report(Exception $exception)
    {
        if ($exception instanceof CustomException) {
            //
        }

        return parent::report($exception);
    }

.. tip:: 不要在 ``report`` 方法中进行太多的 ``instanceof`` 检查，而应考虑使用 可报告异常（reportable exception） 。

Report 辅助函数
^^^^^^^^^^^^^^^
有时你可能需要报告异常，但又不希望终止当前请求的处理。 ``report`` 辅助函数允许你使用异常处理器的 ``report`` 方法在不显示错误页面的情况下快速报告异常：

.. code-block:: php

    <?php
    public function isValid($value)
    {
        try {
            // Validate the value...
        } catch (Exception $e) {
            report($e); // 把异常发送到日志中

            return false;
        }
    }

按类型忽略异常
^^^^^^^^^^^^^^
异常处理器的 ``$dontReport`` 属性包含一组不会被记录的异常类型。例如，由 ``404`` 错误导致的异常以及其他几种类型的错误不会写入日志文件。你可以根据需要添加其他异常类型到此数组中：

.. code-block:: php

    <?php
    /**
     * A list of the exception types that should not be reported.
     *
     * @var array
     */
    protected $dontReport = [
        \Illuminate\Auth\AuthenticationException::class,
        \Illuminate\Auth\Access\AuthorizationException::class,
        \Symfony\Component\HttpKernel\Exception\HttpException::class,
        \Illuminate\Database\Eloquent\ModelNotFoundException::class,
        \Illuminate\Validation\ValidationException::class,
    ];

Render 方法
-----------
``Render`` 方法负责将给定的异常转换为将被发送回浏览器的 ``HTTP`` 响应。默认情况下，异常将传递给为你生成响应的基类。不过，你可以按自己意愿检查异常类型或返回自己的自定义响应：

.. code-block:: php

    <?php
    /**
     * Render an exception into an HTTP response.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Exception  $exception
     * @return \Illuminate\Http\Response
     */
    public function render($request, Exception $exception)
    {
        if ($exception instanceof CustomException) {
            return response()->view('errors.custom', [], 500); // 可以定义视图发送给浏览器
        }

        return parent::render($request, $exception);
    }

Reportable & Renderable 异常
----------------------------
除了在异常处理器的 ``report`` 和 ``render`` 方法中检查异常类型，你还可以直接在自定义异常上定义 ``report`` 和 ``render`` 方法。当定义了这些方法时，它们会被框架自动调用：

.. code-block:: php

    <?php

    namespace App\Exceptions;

    use Exception;

    class RenderException extends Exception
    {
        /**
         * Report the exception.
         *
         * @return void
         */
        public function report()
        {
            //
        }

        /**
         * Render the exception into an HTTP response.
         *
         * @param  \Illuminate\Http\Request
         * @return \Illuminate\Http\Response
         */
        public function render($request)
        {
            return response(...);
        }
    }

HTTP 异常
=========
一些异常用于描述产生自服务器的 ``HTTP`` 错误代码。例如，「页面未找到」错误（404），「未经授权的错误」（401），甚至可以是开发人员引起的 500 错误。 你可以使用 ``abort`` 辅助函数从应用程序的任何地方生成这样的响应：

.. code-block:: php

    <?php
    abort(404); // 使用给定的响应码抛出 HttpException 异常

辅助函数 ``abort`` 将即刻引发一个由异常处理器渲染的异常。你还可选择性地提供响应文本：

.. code-block:: php

    <?php
    abort(403, 'Unauthorized action.');

自定义 HTTP 错误页面
--------------------
Laravel 可以轻松显示各种 ``HTTP`` 状态代码的自定义错误页面。例如，如果你希望自定义 404 HTTP 状态码的错误页面，可以创建一个 ``resources/views/errors/404.blade.php`` 视图文件。该文件将被用于你的应用程序产生的所有 ``404`` 错误。此目录中的视图文件的命名应匹配它们对应的 ``HTTP`` 状态码。由 ``abort`` 函数引发的 ``HttpException`` 实例将作为 ``$exception`` 变量传递给视图：

.. code-block:: php

    <?php
    <h2>{{ $exception->getMessage() }}</h2>

前面抛出异常，就会被注册的异常处理器捕获。
