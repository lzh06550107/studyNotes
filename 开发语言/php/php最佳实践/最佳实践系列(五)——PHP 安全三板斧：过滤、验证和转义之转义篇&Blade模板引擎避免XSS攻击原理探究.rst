PHP 转义实现
============
把输出渲染成网页或 API 响应时，一定要转义输出，这也是一种防护措施，能避免渲染恶意代码，造成 ``XSS`` 攻击，还能防止应用的用户无意中执行恶意代码。

我们可以使用前面提到的 ``htmlentities`` 函数转义输出，该函数的第二个参数一定要使用 ``ENT_QUOTES`` ，让这个函数转义单引号和双引号，而且，还要在第三个参数中指定合适的字符编码（通常是 ``UTF-8`` ），下面的例子演示了如何在渲染前转义 HTML 输出：

.. code-block:: php

    <?php
    $output = '<p><script>alert(“欢迎来到Laravel学院！")</script></p>';
    echo htmlentities($output, ENT_QUOTES, ‘UTF-8');

如果不转义直接输出，会弹出提示框；转义之后输出变成：

.. code-block:: html

    <p><script>alert("欢迎访问Laravel学院!");</script></p>

现代PHP支持许多模板引擎，这些模板引擎在底层已经为了做好了转义处理，比如现在流行的 `twig/twig <https://packagist.org/packages/twig/twig>`_ 和 `smarty/smarty <https://packagist.org/packages/smarty/smarty>`_ 都会自动转义输出。这种默认处理方式很赞，为 PHP Web 应用提供了有力的安全保障。

Blade 模板引擎避免XSS攻击原理
------------------------------
``Laravel`` 使用的模板引擎是 ``Blade`` ，关于 ``Blade`` 的使用可以参考其官方文档，这里我们简单探讨下 ``Laravel`` 底层如何对输出进行转义处理。

一般我们在 ``Laravel`` 中返回视图内容会这么做：

.. code-block:: php

    return view(’test’, [‘data’=>$data]);

这是一个很简单的例子，意味着我们会在 ``resources/views`` 目录下找到 ``test.blade.php`` 视图文件，然后将 ``$data`` 变量传入其中，并将最终渲染结果作为响应的内容返回给用户。那么这一过程经历了哪些底层源码的处理，如果 ``$data`` 变量中包含脚本代码（如 ``JavaScript`` 脚本），又该怎么去处理呢？接下来我们让来一窥究竟。

首先我们从辅助函数 ``view`` 入手，当然这里我们也可以使用 ``View:make`` ，但是简单起见，我们一般用 ``view`` 函数，该函数定义在 ``Illuminate\Foundation\helpers.php`` 文件中：

.. code-block:: php

    <?php
    function view($view = null, $data = [], $mergeData = [])
    {
        $factory = app(ViewFactory::class);
        if (func_num_args() === 0) {
            return $factory;
        }

        return $factory->make($view, $data, $mergeData);
    }

该函数中的逻辑是从容器中取出视图工厂接口 ``ViewFactory`` 对应的实例 ``$factory`` （该绑定关系在 ``Illuminate\View\ViewServiceProvider`` 的 ``register`` 方法中注册，此外这里还注册了模板引擎解析器 ``EngineResolver`` ，包括 ``PhpEngine`` 和载入 ``BladeCompiler`` 的 ``CompilerEngine`` ，以及视图文件查找器 ``FileViewFinder`` ，一句话，这里注册了视图解析所需的所有服务），如果传入了参数，则调用 ``$factory`` 上的 ``make`` 方法：

.. code-block:: php

    <?php
    public function make($view, $data = [], $mergeData = [])
    {
        if (isset($this->aliases[$view])) {
            $view = $this->aliases[$view];
        }

        $view = $this->normalizeName($view);

        $path = $this->finder->find($view);

        $data = array_merge($mergeData, $this->parseData($data));

        $this->callCreator($view = new View($this, $this->getEngineFromPath($path), $view, $path, $data));

        return $view;
    }

这个方法位于 ``Illuminate\View\Factory`` ，这里所做的事情是获取视图文件的完整路径，合并传入变量， ``$this->getEngineFromPath`` 会通过视图文件后缀获取相应的模板引擎，比如我们使用 ``.blade.php`` 结尾的视图文件则获得到的是 ``CompilerEngine`` （即 ``Blade`` 模板引擎），否则将获取到 ``PhpEngine`` ，然后我们根据相应参数实例化 ``View`` （ ``Illuminate\View\View`` ）对象并返回。需要注意的是 ``View`` 类中重写了 ``__toString`` 方法：

.. code-block:: php

    <?php
    public function __toString()
    {
        return $this->render();
    }

所以当我们打印 ``$view`` 实例的时候，实际上会调用 ``View`` 类的 ``render`` 方法，所以下一步我们理所应当研究 ``render`` 方法做了些什么：

.. code-block:: php

    <?php
    public function render(callable $callback = null)
    {
        try {
            $contents = $this->renderContents();
            $response = isset($callback) ? call_user_func($callback, $this, $contents) : null;

            // Once we have the contents of the view, we will flush the sections if we are
            // done rendering all views so that there is nothing left hanging over when
            // another view gets rendered in the future by the application developer.
            $this->factory->flushSectionsIfDoneRendering();

            return ! is_null($response) ? $response : $contents;
        } catch (Exception $e) {
            $this->factory->flushSections();

            throw $e;
        } catch (Throwable $e) {
            $this->factory->flushSections();

            throw $e;
        }
    }

这里重点是 ``$this->renderContents()`` 方法，我们继续深入研究 ``View`` 类中的 ``renderContents`` 方法：

.. code-block:: php

    <?php
    protected function renderContents()
    {
        // We will keep track of the amount of views being rendered so we can flush
        // the section after the complete rendering operation is done. This will
        // clear out the sections for any separate views that may be rendered.
        $this->factory->incrementRender();
        $this->factory->callComposer($this);

        $contents = $this->getContents();

        // Once we've finished rendering the view, we'll decrement the render count
        // so that each sections get flushed out next time a view is created and
        // no old sections are staying around in the memory of an environment.
        $this->factory->decrementRender();

        return $contents;
    }

我们重点关注 ``$this->getContents()`` 这里，进入 ``getContents`` 方法：

.. code-block:: php

    <?php
    protected function getContents()
    {
        return $this->engine->get($this->path, $this->gatherData());
    }

我们在前面已经提到，这里的 ``$this->engine`` 对应 ``CompilerEngine`` （ ``Illuminate\View\Engines\CompilerEngine`` ），所以我们进入 ``CompilerEngine`` 的 ``get`` 方法：

.. code-block:: php

    <?php
    public function get($path, array $data = [])
    {
        $this->lastCompiled[] = $path;
        // If this given view has expired, which means it has simply been edited since
        // it was last compiled, we will re-compile the views so we can evaluate a
        // fresh copy of the view. We'll pass the compiler the path of the view.
        if ($this->compiler->isExpired($path)) {
            $this->compiler->compile($path);
        }

        $compiled = $this->compiler->getCompiledPath($path);

        // Once we have the path to the compiled file, we will evaluate the paths with
        // typical PHP just like any other templates. We also keep a stack of views
        // which have been rendered for right exception messages to be generated.
        $results = $this->evaluatePath($compiled, $data);

        array_pop($this->lastCompiled);

        return $results;
    }

同样我们在之前提到， ``CompilerEngine`` 使用的 ``compiler`` 是 ``BladeCompiler`` ，所以 ``$this->compiler`` 也就是 ``Blade`` 编译器，我们先看 ``$this->compiler->compile($path);`` 这一行（首次运行或者编译好的视图模板已过期会进这里），进入 ``BladeCompiler`` 的 ``compile`` 方法：

.. code-block:: php

    <?php
    public function compile($path = null)
    {
        if ($path) {
            $this->setPath($path);
        }
        if (! is_null($this->cachePath)) {
            $contents = $this->compileString($this->files->get($this->getPath()));

            $this->files->put($this->getCompiledPath($this->getPath()), $contents);
        }
    }

这里我们做的事情是先编译视图文件内容，然后将编译好的内容存放到视图编译路径（ ``storage\framework\views`` ）下对应的文件（一次编译，多次运行，以提高性能），这里我们重点关注的是 ``$this->compileString`` 方法，该方法中使用了 ``token_get_all`` 函数将视图文件代码分割成多个片段，如果片段是数组的话则循环调用 ``$this->parseToken`` 方法：

.. code-block:: php

    <?php
    protected function parseToken($token)
    {
        list($id, $content) = $token;
        if ($id == T_INLINE_HTML) {
            foreach ($this->compilers as $type) {
                $content = $this->{"compile{$type}"}($content);
            }
        }

        return $content;
    }

来到这里，我们已经很接近真相了，针对 HTML 代码（含 ``Blade`` 指令代码），循环调用 ``compileExtensions`` 、 ``compileStatements`` 、 ``compileComments`` 和 ``compileEchos`` 方法，我们重点关注输出方法 ``compileEchos`` ， ``Blade`` 引擎默认提供了 ``compileRawEchos`` 、 ``compileEscapedEchos`` 和 ``compileRegularEchos`` 三种输出方法，对应的指令分别是 ``{!! !!}`` 、 ``{{{ }}}`` 和 ``{{ }}`` ，顾名思义， ``compileRawEchos`` 对应的是原生输出：

.. code-block:: php

    <?php
    protected function compileRawEchos($value)
    {
        $pattern = sprintf('/(@)?%s\s*(.+?)\s*%s(\r?\n)?/s', $this->rawTags[0], $this->rawTags[1]);
        $callback = function ($matches) {
            $whitespace = empty($matches[3]) ? '' : $matches[3].$matches[3];

            return $matches[1] ? substr($matches[0], 1) : '<?php echo '.$this->compileEchoDefaults($matches[2]).'; ?>'.$whitespace;
        };

        return preg_replace_callback($pattern, $callback, $value);
    }

即 ``Blade`` 视图中以 ``{!! !!}`` 包裹的变量会原生输出 HTML ，如果要显示图片、链接，推荐这种方式。

``{{{}}}`` 对应的 ``CompileEscapedEchos`` ，这个在 Laravel 4.2 及以前版本中用于转义，现在已经替换成了 ``{{}}`` ，即调用 ``compileRegularEchos`` 方法：

.. code-block:: php

    <?php
    protected function compileRegularEchos($value)
    {
        $pattern = sprintf('/(@)?%s\s*(.+?)\s*%s(\r?\n)?/s', $this->contentTags[0], $this->contentTags[1]);
        $callback = function ($matches) {
            $whitespace = empty($matches[3]) ? '' : $matches[3].$matches[3];

            $wrapped = sprintf($this->echoFormat, $this->compileEchoDefaults($matches[2]));

            return $matches[1] ? substr($matches[0], 1) : '<?php echo '.$wrapped.'; ?>'.$whitespace;
        };

        return preg_replace_callback($pattern, $callback, $value);
    }

其中 ``$this->echoFormat`` 对应 ``e(%s)`` ，无独有偶， ``compileEscapedEchos`` 中也用到这个方法：

.. code-block:: php

    <?php
    protected function compileEscapedEchos($value)
    {
        $pattern = sprintf('/(@)?%s\s*(.+?)\s*%s(\r?\n)?/s', $this->escapedTags[0], $this->escapedTags[1]);
        $callback = function ($matches) {
            $whitespace = empty($matches[3]) ? '' : $matches[3].$matches[3];

            return $matches[1] ? $matches[0] : '<?php echo e('.$this->compileEchoDefaults($matches[2]).'); ?>'.$whitespace;
        };

        return preg_replace_callback($pattern, $callback, $value);

    }

辅助函数 ``e()`` 定义在 ``Illuminate\Support\helpers.php`` 中：

.. code-block:: php

    <?php
    function e($value)
    {
        if ($value instanceof Htmlable) {
            return $value->toHtml();
        }
        return htmlentities($value, ENT_QUOTES, 'UTF-8', false);
    }

其作用就是对输入的值进行转义。

经过这样的转义，视图中的 ``{{ $data }}`` 或被编译成 ``<?php echo $data?>`` ，最终如何将 ``$data`` 传入视图输出，我们再回到 ``CompilerEngine`` 的 ``get`` 方法，看这一段：

.. code-block:: php

    <?php
    $results = $this->evaluatePath($compiled, $data);

``evaluatePath`` 中传入了编译后的视图文件路径和传入的变量 ``$data`` ，该方法定义如下：

.. code-block:: php

    <?php
    protected function evaluatePath($__path, $__data)
    {
       $obLevel = ob_get_level();ob_start();

        extract($__data, EXTR_SKIP);

        // We'll evaluate the contents of the view inside a try/catch block so we can
        // flush out any stray output that might get out before an error occurs or
        // an exception is thrown. This prevents any partial views from leaking.
        try {
            include $__path;
        } catch (Exception $e) {
            $this->handleViewException($e, $obLevel);
        } catch (Throwable $e) {
            $this->handleViewException(new FatalThrowableError($e), $obLevel);
        }

        return ltrim(ob_get_clean());
    }

这里面调用了 PHP 系统函数 ``extract`` 将传入变量从数组中导入当前编译后模板中（通过 ``include $__path`` 引入），其作用也就是将编译后视图文件中的变量悉数替换成传入的变量值（通过键名映射）。

好了，这就是 ``Blade`` 视图模板从渲染到输出的基本过程，可以看到我们通过 ``{{}}`` 来转义输出，从而达到避免 ``XSS`` 攻击的目的。