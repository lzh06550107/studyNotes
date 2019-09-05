=============
Blade原理分析
=============


模板引擎一般是要做三件事情：

- 变量值的输出（echo）
- 条件判断和循环（if ... else、for、foreach、while）
- 引入或继承其他文件

视图解析流程
============
Laravel 的 ``View`` 部分是内置了两套输出系统：直接输出和使用 ``Blade`` 引擎“编译”后输出，默认情况下它们通过文件名后缀来选择： ``.blade.php`` 后缀的认为是模板视图文件，其他的 ``.php`` 文件按照 PHP 本身的方式执行。虽然 ``Blade`` 模板文件中也可以随意嵌入 PHP 代码，但如果并没有使用，系统还去进行语法解析和替换也是没有必要的，这样可以提高效率。

在使用 ``View`` 组件输出时，不管是调用 ``helpers`` 中提供的 ``view`` 函数还是使用 ``Facades`` 提供静态接口 ``View::make()`` ，实际上执行的都是 ``Illuminate\View\Factory`` 中的 ``make`` 方法。以此为入口，很容易就能知道视图解析输出的流程：

1. 查找视图文件；
2. 根据文件名后缀从 Container 中取出相应的引擎；
3. 加载视图文件或编译后加载编译后的文件执行，同时将需要解析的数据暴露在视图文件环境中。

``Factory`` 中的一些方法完成了以上第一步的过程，文件查找是调用的 ``FileViewFinder`` ，其中使用了一些 ``Illuminate\Filesystem\Filesystem`` 中的方法，这个类中还有一些方法是跟 ``events`` 相关的，这里就忽略不表了。

在以上步骤中，如果中获取到的视图文件是需要“编译”的，引擎会调用 “Blade 编译器”将原视图进行“编译”并保存在 ``cache`` 目录中然后加载输出。下次调用时如果发现源文件并没有被修改过就不再重新编译而是直接获取缓存文件并输出。

``CompilerEngine`` 调用的编译器是 ``CompilerInterface`` 接口的实现，默认情况下也就只有 ``BladeCompiler`` （如果不知道解析器是如何注入的，你需要去了解 Laravel 的服务容器，这里就不细表）。

Blade 引擎
==========
接下来就是本文的重点： ``Blade`` 是如何“编译”的。我一直给“编译”两个字加引号，因为这显然不是真正意义上的代码编译的过程，只是一些正则替换的过程。

我们知道 Laravel 的模板引擎是很简洁的，使用时并不需要掌握太多东西，基本上只需要知道以下两点：

- ``{{`` 与 ``}}`` 之间是要输出的内容，也有扩展的两个方法 ``{{{ ... }}}`` 和 ``{!! .. !!}`` 分别用于转义输出和不转义输出，5.0 以后的版本中 ``{{ ... }}`` 之间的默认情况下也是转义输出的；
- ``@`` 符号开头的都是指令，包括 PHP 本身有的 ``if else foreach`` 以及扩展的 ``include yield stop`` 等等；

而 ``Blade`` 对于解析的处理实际上是分了四种情况：

- ``Extensions`` -> 扩展部分
- ``Statements`` -> 语句块（就是 ``@`` 开头的指令）
- ``Comments`` -> 注释部分（ ``{{-- ... --}}`` 的写法，解析之后是 PHP 的注释而不是 HTML 的注释）
- ``Echos`` -> 输出

在解析（解析是在 ``cache`` 不存在的情况下）过程中， ``Blade`` 会先使用 ``token_get_all`` 函数获取到视图文件中的被 PHP 解释器认为是 HTML（T_INLINE_HTML）的部分，然后依次进行以上四种情况的解析。

扩展部分是调用用户自定义的编译器解析字符串。BladeCompiler 中提供了的 extend 方法来添加可扩展。

注释部分也很简单，就是将 ``{{-- ... --}}`` 替换成 ``<?php /* ... */ ?php>`` 。


输出部分
--------
输出部分提供了三个方法，分别对应上文提到的三种情况：

- ``compileRawEchos`` -> 输出未经转义的内容 （ ``{!! ... !!}`` ）
- ``compileEscapedEchos`` -> 输出转义之后的内容 （ ``{{{ ... }}}`` ）
- ``compileRegularEchos`` -> 正常输出 （ ``{{ ... }}`` ）

默认情况下经过字符替换之后 ``compileEscapedEchos`` 和 ``compileRegularEchos`` 的函数体其实是完全一样的，在输出的时候都是调用一个 ``e()`` 的辅助函数来输出：

.. code-block:: php

    <?php
    function e($value)
    {
        if ($value instanceof Htmlable) {
            return $value->toHtml();
        }

        return htmlentities($value, ENT_QUOTES, 'UTF-8', false);
    }

这貌似是 5.0 之后的版本才改的，之前的版本里 ``compileRegularEchos`` 执行的是 ``compileRawEchos`` 的行为。不过两个函数还是有一个区别： ``compileRegularEchos`` 的转义函数是可以通过 ``setEchoFormat`` 自定义的（只是默认是 e()），但是 ``compileEscapedEchos`` 不允许自定义。

``echo`` 后的内容也是经过正则替换的：

.. code-block:: php

    <?php
    public function compileEchoDefaults($value)
    {
        return preg_replace('/^(?=\$)(.+?)(?:\s+or\s+)(.+?)$/s', 'isset($1) ? $1 : $2', $value);
    }

从正则表达式中可以看出来输出提供了一个 or 的关键字，$a or $b 的写法会被替换成 isset($a) ? $a : $b。


语句块部分
-----------
语句块部分可以分成三种情况：

- 和 PHP 本身一样的 ``if else foreach`` 以及扩展的 ``unless`` 等流程和循环控制的关键字；
- ``include yield`` 等模板文件引入、内容替换的部分；
- ``lang choice can`` 等涉及到 Laravel 其他组件的功能性关键字；

第一种情况是很简单的替换过程，本身 PHP 为了在 HMTL 和 PHP 混合书写方便就提供了 if foreach 等几个关键字使用冒号和 endif 等关键字代替大括号来控制流程的方法。

第二种情况稍微复杂一点，比如下面的函数：

.. code-block:: php

    <?php
    protected function compileYield($expression)
    {
        return "<?php echo \$__env->yieldContent{$expression}; ?>";
    }

解析之后的语句是调用了一个名为 ``$_env`` 的实例中的方法。这个实例其实就是 ``Illuminate\View\Factory`` 的实例：

Factory 的构造函数：

.. code-block:: php

    <?php
    public function __construct(EngineResolver $engines, ViewFinderInterface $finder, Dispatcher $events)
    {
        ...
        $this->share('__env', $this);
    }

``Illuminate\View\View`` 中：

.. code-block:: php

    <?php
    protected function getContents()
    {
        return $this->engine->get($this->path, $this->gatherData());
    }

    /**
     * Get the data bound to the view instance.
     *
     * @return array
     */
    protected function gatherData()
    {
        $data = array_merge($this->factory->getShared(), $this->data);
        ...
        return $data;
    }

由此也可以看出 ``each yield`` 等指令的实现也是在 ``Factory`` 中，分别对应的是 ``renderEach yieldContent`` 等。

所以文件引入等指令的实现方式就是：在主视图输出的时候，通过注入的 ``$__env`` 来重复调用 ``Factory`` 中的 ``make`` 方法来输出引入的文件。

至于 ``lang`` 等关键字，替换后就是使用 ``app()`` 函数来调用 Laravel 的其他组件。此外 ``Blade`` 还提供了 ``inject`` 关键字来调用任何你想使用的组件。

除了以上这些，你还可以通过 ``directive`` 方法来增加一些自定义指令。

``compileStatements`` 方法中最后进行正则替换的正则表达式看起来比较复杂：

.. code-block:: php

    <?php
    /\B@(\w+)([ \t]*)(\( ( (?>[^()]+) | (?3) )* \))?/x

这是因为正则后面的一部分实现了 `递归模式 <http://php.net/manual/zh/regexp.reference.recursive.php>`_ 来匹配语句块中括号的数量。

后话
====
为了能够在任何地方使用 ``Blade`` ，我把它核心的部分提取了出来，去掉了其他组件的依赖，也不再依赖文件扩展名来选择引擎：

项目地址： https://github.com/XiaoLer/blade

此外也通过这个提取之后的版本做了一个 ``yii2`` 能够使用的版本： https://github.com/XiaoLer/yii2-blade 。在之前尝试的版本中直接使用 Laravel 的 ``View`` 组件并不灵活，现在感觉好多了。