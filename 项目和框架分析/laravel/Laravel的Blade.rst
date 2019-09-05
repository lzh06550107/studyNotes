=====
Blade
=====


- {{ $var }} - Echo content
- {{ $var or 'default' }} - Echo content with a default value
- {{{ $var }}} - Echo escaped content
- {{-- Comment --}} - A Blade comment
- @extends('layout') - Extends a template with a layout
- @if(condition) - Starts an if block
- @else - Starts an else block
- @elseif(condition) - Start a elseif block
- @endif - Ends a if block
- @foreach($list as $key => $val) - Starts a foreach block
- @endforeach - Ends a foreach block
- @for($i = 0; $i < 10; $i++) - Starts a for block
- @endfor - Ends a for block
- @while(condition) - Starts a while block
- @endwhile - Ends a while block
- @unless(condition) - Starts an unless block
- @endunless - Ends an unless block
- @include(file) - Includes another template
- @include(file, ['var' => $val,...]) - Includes a template, passing new variables.
- @each('file',$list,'item') - Renders a template on a collection
- @each('file',$list,'item','empty') - Renders a template on a collection or a different template if collection is empty.
- @yield('section') - Yields content of a section.
- @show - Ends section and yields its content
- @lang('message') - Outputs message from translation table
- @choice('message', $count) - Outputs message with language pluralization
- @section('name') - Starts a section
- @stop - Ends section
- @endsection - Ends section
- @append - Ends section and appends it to existing of section of same name
- @overwrite - Ends section, overwriting previous section of same name

简介
====
``Blade`` 是 Laravel 提供的一个简单而又强大的模板引擎。和其他流行的 PHP 模板引擎不同， ``Blade`` 并不限制你在视图中使用原生 PHP 代码。所有 ``Blade`` 视图文件都将被编译成原生的 PHP 代码并缓存起来，除非它被修改，否则不会重新编译，这就意味着 ``Blade`` 基本上不会给你的应用增加任何负担。 ``Blade`` 视图文件使用 ``.blade.php`` 作为文件扩展名，被存放在 ``resources/views`` 目录。

模板继承
========

模板布局的编译指令功能由 ``\Illuminate\View\Compilers\Concerns\CompilesLayouts`` 文件提供。而模板布局的功能函数由 ``\Illuminate\View\Concerns\ManagesLayouts`` 文件提供。

定义布局
--------
``Blade`` 的两个主要优点是 模板继承 和 区块 。为方便开始，让我们先通过一个简单的例子来上手。首先，我们来研究一个「主」页面布局。因为大多数 web 应用会在不同的页面中使用相同的布局方式，因此可以很方便地定义单个 ``Blade`` 布局视图：

.. code-block:: php

    <?php
    <!-- 文件保存于 resources/views/layouts/app.blade.php -->

    <html>
        <head>
            <title>应用程序名称 - @yield('title')</title>
        </head>
        <body>
            @section('sidebar')
                这是主布局的侧边栏。
            @show

            <div class="container">
                @yield('content')
            </div>
        </body>
    </html>

如你所见，该文件包含了典型的 ``HTML`` 语法。不过，请注意 ``@section`` 和 ``@yield`` 命令。顾名思义， ``@section`` 命令定义了视图的一部分内容(继承类可以扩展)，而 ``@yield`` 指令是用来显示指定部分的内容。

现在，我们已经定义好了这个应用程序的布局，接下来，我们定义一个继承此布局的子页面。

继承布局
--------
当定义子视图时，你可以使用 ``Blade`` 提供的 ``@extends`` 命令来为子视图指定应该 「继承」 的布局。 继承 ``Blade`` 布局的视图可使用 ``@section`` 命令将内容注入于布局的 ``@section`` 中。而「主」布局中使用 ``@yield`` 的地方会显示这些子视图中的 ``@section`` 间的内容：

.. code-block:: php

    <?php
    <!-- 文件保存于 resources/views/layouts/child.blade.php -->

    @extends('layouts.app')

    @section('title', 'Page Title') // 直接替换

    @section('sidebar')
        @parent // 扩展

        <p>这将追加到主布局的侧边栏。</p>
    @endsection

    @section('content')
        <p>这是主体内容。</p> // 直接替换
    @endsection

在上面的例子里， ``@section`` 中的 ``sidebar`` 使用 ``@parent`` 命令在「主」布局的 ``@section('sidebar')`` 中增加内容（不是覆盖）。渲染视图时， ``@parent`` 指令会被替换为「主」布局中 ``@section('sidebar')`` 间的内容。

.. tip:: 与上一个示例相反，此侧边栏部分以 ``@endsection`` 而不是 ``@show`` 结尾。 ``@endsection`` 指令只定义一个区块，而 ``@show`` 则是定义并立即生成该区块。

Laravel 框架中的 ``Blade`` 模板引擎，很好用，但是在官方文档中有关 ``Blade`` 的介绍并不详细，有些东西没有写出来，而有些则是没有说清楚。比如，使用中可能会遇到这样的问题：

1. ``@yield`` 和 ``@section`` 都可以预定义可替代的区块，这两者有什么区别呢？
2. ``@section`` 可以用 ``@show`` ， ``@stop`` ， ``@overwrite`` 以及 ``@append`` 来结束，这四者又有什么区别呢？

@yield 与 @section
------------------
首先， ``@yield`` 是不可扩展的，如果你要定义的部分没有默认内容让子模板扩展的，那么用 ``@yield($name, $default)`` 的形式会比较方便，如果你在子模板中并没有指定这个区块的内容，它就会显示默认内容，如果定义了，就会显示你定义的内容。非此即彼。

与之相比， ``@section`` 则既可以被替代，又可以被扩展，这是最大的区别。比如：

.. code-block:: php

    <?php
    {{-- layout.master --}}
    @yield('title','默认标题')

    @section('content')
    默认的内容
    @show

    {{-- home.index --}}
    @extends('layout.master')

    @section('title')
      @parent
      新的标题
    @stop

    @section('content')
      @parent
      扩展的内容
    @stop

上面的例子中，模板用 ``@yield`` 和 ``@section`` 分别定义了一个区块，然后在子模板中去定义内容，由于 ``@yield`` 不能被扩展，所以即使加上了 ``@parent`` 也不起作用，输出的内容只有 ``新的标题`` ，替换了 ``默认标题`` 。因此最终生成的页面只能是 ``默认标题`` 或者 ``新的标题`` ，不能并存。而 ``@section`` 定义的部分，由于使用了 ``@parent`` 关键字，父模板中的内容会被保留，然后再扩展后添加的内容进去，输出的内容会是 ``默认的内容 扩展的内容`` 。

官方网站上的文档中并没有涉及 ``@parent`` 关键字，说的是默认行为是“扩展”，要覆盖需要用 ``@override`` 来结束，这是错的，[github 上的最新文档][docs] 已经做了修正。 ``@section`` 加上 ``@stop`` ，默认是替换（注入），必须用 ``@parent`` 关键字才能扩展。而 ``@override`` 关键字实际上有另外的应用场景。

@show 与 @stop
--------------
接下来再说说与 ``@section`` 对应的结束关键字， ``@show`` ,  ``@stop`` 有什么区别呢？（网上的部分文章，以及一些编辑器插件还会提示 ``@endsection`` , 这个在 4.0 版本中已经被移除，虽然向下兼容，但是不建议使用）。

``@show`` 指的是执行到此处时将该 ``section`` 中的内容输出到页面，而 ``@stop`` 则只是进行内容解析，并且不再处理当前模板中后续对该 ``section`` 的处理，除非用 ``@override`` 覆盖（详见下一部分）。通常来说，在首次定义某个 ``section`` 的时候，应该用 ``@show`` ，而在替换它或者扩展它的时候，不应该用 ``@show`` ，应该用 ``@stop`` 。下面用例子说明：

.. code-block:: php

    <?php
    {{-- layout.master --}}
    <div id="zoneA">
      @section('zoneA')
          AAA
          @show


    </div>

    <div id="zoneB">
      @section('zoneB')
          BBB
          @stop


    </div>
    <div id="zoneC">
      @section('zoneC')
          CCC
          @show

    </div>

    {{-- page.view --}}
    @extends('layout.master')

    @section('zoneA')
    aaa
    @stop

    @section('zoneB')
    bbb
    @stop

    @section('zoneC')
    ccc
    @show

在 ``layout.master`` 中，用 ``@stop`` 来结束 ``zoneB`` ，由于整个模板体系中，没有以 ``@show`` 结束的 ``zoneB`` 的定义，因此这个区块不会被显示。而在 ``page.view`` 中，用 ``@show`` 定义了 ``zoneC`` ，这会在执行到这里时立即显示内容，并按照模板继承机制继续覆盖内容，因此最终显示的内容会是：

.. code-block:: php

    <?php
    ccc // 来自 page.view
    <div class="zoneA">
      aaa
    </div>

    <div class="zoneB">
    </div>
    <div class="zoneC">
      ccc
    </div>

从结果可以看到， ``zoneB`` 的内容丢失，因为没有用 ``@show`` 告诉引擎输出这部分的内容，而 ``zoneC`` 的内容会显示两次，并且还破坏了 ``layout.master`` 的页面结构，因为 ``@show`` 出现了两次。

@append 和 @override
--------------------
刚才说到了， ``@override`` 并不是在子模板中指明内容替换父模板的默认内容，而是另有用途，那么是如何使用呢？这又涉及到一个 ``section`` 在模板中可以多次使用的问题。也即我们所定义的每一个 ``section`` ，在随后的子模板中其实是可以多次出现的。比如：

.. code-block:: php

    <?php
    {{-- master --}}
    <div>
      @yield('content')

    </div>

    {{-- subview --}}
    @extends('master')

    @section('content')
    加一行内容
    @append

    @section('content')
    再加一行内容
    @append

    @section('content')
    加够了，到此为止吧。
    @stop

在上例中，我在父级模板中只定义了一个名为 ``content`` 的 ``section`` ，而在子模板中三次指定了这个 ``section`` 的内容。 这个例子最终的输出是：

.. code-block:: html

    <div>
    加一行内容
    再加一行内容
    加够了，到此为止吧。
    </div>

三次指定的内容都显示出来了，关键就在于 ``@append`` 这个关键字，它表明 ``此处的内容添加到`` ，因此内容会不断扩展。而最后用了`` @stop`` ，表示这个 ``section`` 的处理到此为止。如果在后面继续用 ``@append`` 或者 ``@stop`` 来指定这个 ``section`` 的内容，都不会生效。除非用 ``@override`` 来处理。 ``@override`` 的意思就是 ``覆盖之前的所有定义，以这次的为准`` 。比如：

.. code-block:: php

    <?php
    {{-- master --}}
    <div>
      @yield('content')
        @yield('message')
    </div>
    {{-- master --}}
    <div>
      @section('content')
        加一行内容
        @append
        @section('content')
        再加一行内容
        @append
        @section('content')
        加够了，结束吧
        @stop
        @section('content')
        都不要了，我说的。
        @override
    </div>

这个例子和刚才的类似，只不过最后加了一组定义。最终的输出会是：

.. code-block:: html

    <div>
      都不要了，我说的。
    </div>

所以，在正式的项目中，有时候需要对数据进行遍历输出的，可以使用 ``@append`` ，而如果遍历到了某个数据发现前面的都错了呢？用 ``@override`` 就可以全部推翻。


Components & Slots
==================

视图组件编译指令由 ``\Illuminate\View\Compilers\Concerns\CompilesComponents`` 文件提供；而视图组件的功能函数由 ``\Illuminate\View\Concerns\ManagesComponents`` 文件提供。

``Components`` 和 ``slots`` 类似于布局中的 @section，但其使用方式更容易使人理解。首先，假设我们有一个能在整个应用程序中被重复使用的「警告」组件:

.. code-block:: php

    <?php
    <!-- /resources/views/alert.blade.php -->

    <div class="alert alert-danger">
        {{ $slot }}
    </div>

``{{ $slot }}`` 变量将包含我们希望注入到组件的内容。然后，我们可以使用 ``Blade`` 命令 ``@component`` 来构建这个组件：

.. code-block:: php

    <?php
    @component('alert')
        <strong>Whoops!</strong> Something went wrong!
    @endcomponent

有时为组件定义多个 ``slots`` 是很有帮助的。现在我们要对「警报」组件进行修改，让它可以注入「标题」。通过简单地 「打印」匹配其名称的变量来显示被命名的 ``@slot`` 之间的内容：

.. code-block:: php

    <?php
    <!-- /resources/views/alert.blade.php -->

    <div class="alert alert-danger">
        <div class="alert-title">{{ $title }}</div>

        {{ $slot }}
    </div>

现在，我们可以使用 ``@slot`` 指令注入内容到已命名的 ``slot`` 中，任何没有被 ``@slot`` 指令包裹住的内容将传递给组件中的 ``$slot`` 变量:

.. code-block:: php

    <?php
    @component('alert')
        @slot('title')
            Forbidden
        @endslot

        你没有权限访问这个资源！
    @endcomponent

向组件传递数据
--------------
需要向组件传递数据时，可以给 ``@component`` 指令的第二个参数传入一个数组。数组里的数据将在组件模板以变量的形式生效：

.. code-block:: php

    <?php
    @component('alert', ['foo' => 'bar'])
        ...
    @endcomponent

组件别名(5.5 找不到功能实现代码)
----------------------------
子目录中的 ``Blade`` 组件，使用别名更方便访问。现在，有一个 ``Blade`` 组件存储在 ``resources/views/components/alert.blade.php`` ，可以使用 ``component`` 方法给它起个 ``alert`` 的别名。通常，在 ``AppServiceProvider`` 的 ``boot`` 方法中完成这个操作：

.. code-block:: php

    <?php
    use Illuminate\Support\Facades\Blade;

    Blade::component('components.alert', 'alert');

组件起好别名后，使用指令渲染：

.. code-block:: php

    <?php
    @alert(['type' => 'danger'])
        你无权访问！
    @endalert

也可以不传参数：

.. code-block:: php

    <?php
    @alert
        你无权访问！
    @endalert

显示数据
========
传入 ``Blade`` 视图的数据，通过双花括号包裹来显示。例如，给出如下路由：

.. code-block:: php

    <?php
    Route::get('greeting', function () {
        return view('welcome', ['name' => 'Samantha']);
    });

这样显示 ``name`` 变量的内容：

.. code-block:: php

    <?php
    Hello, {{ $name }}.

当然，不限于显示传入视图的变量内容，还可以输出 PHP 函数结果。实际上，可以在 Blade ``echo`` 语句里放置任何 PHP 代码：

.. code-block:: php

    <?php
    The current UNIX timestamp is {{ time() }}.

.. tip:: Blade ``{{ }}`` 语句会自动调用 PHP 的 ``htmlspecialchars`` 函数防止 ``XSS`` 攻击。

显示未转义数据
--------------
默认情况下，Blade ``{{ }}`` 语句会自动调用 PHP 的 ``htmlspecialchars`` 函数防止 ``XSS`` 攻击。不想转义的话，可以使用以下语法：

.. code-block:: php

    <?php
    Hello, {!! $name !!}.

.. note:: 输出用户提供的数据时，千万要小心。对用户提供的数据，总是要使用双花括号进行显示，防止 ``XSS`` 攻击。

渲染 JSON
---------
有时，为了初始化 JavaScript 变量，需要将传入视图的数组进行 ``JSON`` 化。例如：

.. code-block:: php

    <?php
    <script>
        var app = <?php echo json_encode($array); ?>;
    </script>

然而，可以用 ``@json`` Blade 指令替代手动 ``json_encode`` ：

.. code-block:: php

    <?php
    <script>
        var app = @json($array);
    </script>

HTML 实体转换
-------------
默认情况下，Blade （和 Laravel 的 辅助函数 ``e`` ） 会将 HTML 全部转换。要关闭全部转换，可以在 ``AppServiceProvider`` 的 ``boot`` 方法里调用 ``Blade::withoutDoubleEncoding`` ：

.. code-block:: php

    <?php

    namespace App\Providers;

    use Illuminate\Support\Facades\Blade;
    use Illuminate\Support\ServiceProvider;

    class AppServiceProvider extends ServiceProvider
    {
        /**
         * 引导应用服务.
         *
         * @return void
         */
        public function boot()
        {
            Blade::withoutDoubleEncoding(); // 5.5找不到代码
        }
    }

Blade & JavaScript 框架
-----------------------
由于许多 JavaScript 框架也是用花括号来表示要显示在浏览器的表达式， 可以使用 ``@`` 符告诉 ``Blade`` 渲染引擎保持这个表达式不变。例如：

.. code-block:: php

    <?php
    <h1>Laravel</h1>

    Hello, @{{ name }}.

渲染后， ``Blade`` 引擎会把 ``@`` 符移除，但是 ``{{ name }}`` 表达式保留，从而让 JavaScript 框架去渲染它。

@verbatim 指令
---------------
如果模板中一大部分需要显示 JavaScript 变量，就可以用 ``@verbatim`` 指令包裹住 HTML，这样就不用在每个 Blade ``echo`` 语句前加 ``@`` 符：

.. code-block:: php

    <?php
    @verbatim
        <div class="container">
            Hello, {{ name }}.
        </div>
    @endverbatim

流程控制
========
除了模板继承和数据显示外， ``Blade`` 还为常用的 PHP 流程控制提供了便捷语句，例如条件语句和循环语句。这些语句不但简洁，还与 PHP 语句相似。


条件指令
--------
视图条件编译指令由 ``\Illuminate\View\Compilers\Concerns\CompilesConditionals`` 文件提供。

If 语句
^^^^^^^

使用 ``@if`` 、 ``@elseif`` 、 ``@else`` 和 ``@endif`` 指令构建 ``if`` 语句。这些指令与 PHP 对应，这些指令括号中是php语句：

.. code-block:: php

    <?php
    @if (count($records) === 1)
        我有一条记录！
    @elseif (count($records) > 1)
        我有好几条记录！
    @else
       我没有记录！
    @endif

为方便起见， ``Blade`` 还提供了 ``@unless`` 指令：

.. code-block:: php

    <?php
    @unless (Auth::check())
        未登陆
    @endunless

除了以上述指令， ``@isset`` 和 ``@empty`` 也可能用到，功能与 ``PHP`` 函数对应：

.. code-block:: php

    <?php
    @isset($records)
        // $records 已定义且不为 null
    @endisset

    @empty($records)
        // $records 为空
    @endempty

认证指令
^^^^^^^^^

``@auth`` 和 ``@guest`` 指令用来快速认证当前用户：

.. code-block:: php

    <?php
    @auth
        // 通过认证
    @endauth

    @guest
        // 未通过认证
    @endguest

必要的话，可以在 ``@auth`` 和 ``@guest`` 指令中指定 认证看守器（ ``Guard`` ）：

.. code-block:: php

    <?php
    @auth('admin')
        // 通过认证
    @endauth

    @guest('admin')
        // 未通过认证
    @endguest

Section 指令
^^^^^^^^^^^^
``@hasSection`` 指令检查指定的 ``section`` 区块是否有内容，如果有内容，则输出它的内容：

.. code-block:: php

    <?php
    @hasSection('navigation')
        <div class="pull-right">
            @yield('navigation')
        </div>

        <div class="clearfix"></div>
    @endif

Switch 语句
^^^^^^^^^^^
可以使用 ``@switch`` 、 ``@case`` 、 ``@break`` 、 ``@default`` 和 ``@endswitch`` 指令来构建 ``Switch`` 语句：

.. code-block:: php

    <?php
    @switch($i)
        @case(1)
            First case...
            @break

        @case(2)
            Second case...
            @break

        @default
            Default case...
    @endswitch

循环
----

视图条件编译指令由 ``\Illuminate\View\Compilers\Concerns\CompilesLoops`` 文件提供。

除了条件表达式外， ``Blade`` 也支持 PHP 的循环结构。同样，以下这些指令中的每一个都与其 PHP 对应的函数相同：

.. code-block:: php

    <?php
    @for ($i = 0; $i < 10; $i++)
        目前的值为 {{ $i }}
    @endfor

    @foreach ($users as $user)
        <p>此用户为 {{ $user->id }}</p>
    @endforeach

    @forelse ($users as $user) // 如果要在列表为空时显示不同的消息，请使用forelse-empty-endforeach构造。
        <li>{{ $user->name }}</li>
    @empty
        <p>没有用户</p>
    @endforelse

    @while (true)
        <p>死循环了</p>
    @endwhile

.. tip:: 循环时，你可以使用 循环变量 来获取循环的信息，例如是否在循环中进行第一次或最后一次迭代。

当使用循环时，你也可以结束循环或跳过当前迭代：

.. code-block:: php

    <?php
    @foreach ($users as $user)
        @if ($user->type == 1)
            @continue
        @endif

        <li>{{ $user->name }}</li>

        @if ($user->number == 5)
            @break
        @endif
    @endforeach

你还可以使用一行代码包含指令声明的条件：

.. code-block:: php

    <?php
    @foreach ($users as $user)
        @continue($user->type == 1)

        <li>{{ $user->name }}</li>

        @break($user->number == 5)
    @endforeach


循环变量
--------
循环时，可以在循环内使用 ``$loop`` 变量。这个变量可以提供一些有用的信息，比如当前循环的索引，当前循环是不是首次迭代，又或者当前循环是不是最后一次迭代：

.. code-block:: php

    <?php
    @foreach ($users as $user)
        @if ($loop->first)
            这是第一个迭代。
        @endif

        @if ($loop->last)
            这是最后一个迭代。
        @endif

        <p>This is user {{ $user->id }}</p>
    @endforeach

在一个嵌套的循环中，可以通过使用 ``$loop`` 变量的 ``parent`` 属性来获取父循环中的 ``$loop`` 变量：

.. code-block:: php

    @foreach ($users as $user)
        @foreach ($user->posts as $post)
            @if ($loop->parent->first)
                This is first iteration of the parent loop.
            @endif
        @endforeach
    @endforeach

``$loop`` 变量也包含了其它各种有用的属性：

+------------------+--------------------------------------+
| 属性             | 描述                                 |
+==================+======================================+
| $loop->index     | 当前循环迭代的索引（从0开始）。      |
+------------------+--------------------------------------+
| $loop->iteration | 当前循环迭代 （从1开始）。           |
+------------------+--------------------------------------+
| $loop->remaining | 循环中剩余迭代数量。                 |
+------------------+--------------------------------------+
| $loop->count     | 迭代中的数组项目总数。               |
+------------------+--------------------------------------+
| $loop->first     | 当前迭代是否是循环中的首次迭代。     |
+------------------+--------------------------------------+
| $loop->last      | 当前迭代是否是循环中的最后一次迭代。 |
+------------------+--------------------------------------+
| $loop->depth     | 当前循环的嵌套级别。                 |
+------------------+--------------------------------------+
| $loop->parent    | 在嵌套循环中，父循环的变量。         |
+------------------+--------------------------------------+

注释
----

视图注释编译指令编译指令在 ``\Illuminate\View\Compilers\Concerns\CompilesComments`` 文件提供。

``Blade`` 也能在视图中定义注释。然而，跟 ``HTML`` 的注释不同的， ``Blade`` 注释不会被包含在应用程序返回的 ``HTML`` 内：

.. code-block:: php

    <?php
    {{-- 此注释将不会出现在渲染后的 HTML --}}

PHP
---
在某些情况下，将 ``PHP`` 代码嵌入到视图中很有用。你可以使用 ``Blade`` 的 ``@php`` 指令在模板中执行一段纯 ``PHP`` 代码：

.. code-block:: php

    <?php
    @php
        //
    @endphp

.. tip:: 虽然 ``Blade`` 提供了这个功能，但频繁地使用意味着你的模版被嵌入了太多的逻辑。

引入子视图
==========

引入子视图的编译指令由 ``\Illuminate\View\Compilers\Concerns\CompilesIncludes`` 文件提供。

你可以使用 ``Blade`` 的 ``@include`` 命令来引入一个已存在的视图，所有在父视图的可用变量在被引入的视图中都是可用的。

.. code-block:: php

    <?php
    <div>
        @include('shared.errors')

        <form>
            <!-- 表单内容 -->
        </form>
    </div>

被引入的视图会继承父视图中的所有数据，同时也可以向引入的视图传递额外的数组数据：

.. code-block:: php

    <?php
    @include('view.name', ['some' => 'data'])

当然，如果尝试使用 ``@include`` 去引入一个不存在的视图， Laravel 会抛出错误。如果想引入一个可能存在或可能不存在的视图，就使用 ``@includeIf`` 指令:

.. code-block:: php

    <?php
    @includeIf('view.name', ['some' => 'data'])

如果要根据给定的布尔条件 ``@include`` 视图，可以使用 ``@includeWhen`` 指令：

.. code-block:: php

    <?php
    @includeWhen($boolean, 'view.name', ['some' => 'data'])

要包含来自给定数组视图的第一个视图，可以使用 ``includeFirst`` 指令：

.. code-block:: php

    <?php
    @includeFirst(['custom.admin', 'admin'], ['some' => 'data'])

.. note:: 请避免在 ``Blade`` 视图中使用 ``__DIR__`` 及 ``__FILE__`` 常量，因为它们会引用编译视图时缓存文件的位置。

为集合渲染视图
--------------
你可以使用 ``Blade`` 的 ``@each`` 命令将循环及引入写成一行代码：

.. code-block:: php

    <?php
    @each('view.name', $jobs, 'job')

第一个参数是对数组或集合中的每个元素进行渲染的部分视图。第二个参数是要迭代的数组或集合，而第三个参数是将被分配给视图中当前迭代的变量名称。举个例子，如果你要迭代一个 ``jobs`` 数组，通常会使用子视图中的变量 ``job`` 来获取每个 ``job`` 。当前迭代的 ``key`` 将作为子视图中的 ``key`` 变量。

你也可以传递第四个参数到 ``@each`` 命令。当需要迭代的数组为空时，将会使用这个参数提供的视图来渲染。

.. code-block:: php

    <?php
    @each('view.name', $jobs, 'job', 'view.empty')

.. note:: 通过 ``@each`` 渲染的视图不会从父视图继承变量。 如果子视图需要这些变量，则应该使用 ``@foreach`` 和 ``@include`` 。

请注意，这不是唯一的方法。这个结构大致相当于：

.. code-block:: php

    <?php
    @forelse($this->item->users as $key => $value)
        @include('view.name', array('key' => $key, 'job' => $job)
    @empty
        @include('view.empty')
    @endforelse

堆栈
====

引入堆栈的编译指令由 ``\Illuminate\View\Compilers\Concerns\CompilesStacks`` 文件提供。


``Blade`` 可以被推送到在其他视图或布局中的其他位置渲染的命名堆栈。这在子视图中指定所需的 JavaScript 库时非常有用：

.. code-block:: php

    <?php
    @push('scripts')
        <script src="/example.js"></script>
    @endpush
    @prepend('scripts')
        <script src="/first.js"></script>
    @endprepend

你可以根据需要多次压入堆栈，通过 ``@stack`` 命令中传递堆栈的名称来渲染完整的堆栈内容：

.. code-block:: php

    <?php
    <head>
        <!-- Head Contents -->

        @stack('scripts')
    </head>

服务注入
========

服务注入的编译指令由 ``\Illuminate\View\Compilers\Concerns\CompilesInjections`` 文件提供。

``@inject`` 命令可用于从 Laravel 服务容器 中检索服务。传递给 ``@inject`` 的第一个参数为置放该服务的变量名称，而第二个参数是要解析的服务的类或是接口的名称：

.. code-block:: php

    <?php
    @inject('metrics', 'App\Services\MetricsService')

    <div>
        Monthly Revenue: {{ $metrics->monthlyRevenue() }}.
    </div>

拓展 Blade
==========

底层调用 ``\Illuminate\View\Compilers\BladeCompiler`` 的 ``directive`` 方法。

``Blade`` 甚至允许你使用 ``directive`` 方法来定义自定义指令。当 ``Blade`` 编译器遇到自定义指令时，它将使用指令包含的表达式调用提供的回调。

以下示例创建一个 ``@datetime($var)`` 伪指令，该伪指令格式化给定的 ``DateTime`` 实例 ``$var`` ：

.. code-block:: php

    <?php

    namespace App\Providers;

    use Illuminate\Support\Facades\Blade;
    use Illuminate\Support\ServiceProvider;

    class AppServiceProvider extends ServiceProvider
    {
        /**
         * 执行注册后引导服务。
         *
         * @return void
         */
        public function boot()
        {
            Blade::directive('datetime', function ($expression) {
                return "<?php echo ($expression)->format('m/d/Y H:i'); ?>";
            });
        }

        /**
         * 在容器中注册绑定。
         *
         * @return void
         */
        public function register()
        {
            //
        }
    }

如你所见，我们可以链式调用在指令中传递的任何表达式的 ``format`` 方法。所以，在这个例子里，该 @datetime($datetime) 指令最终生成了以下 PHP 代码：

.. code-block:: php

    <?php echo ($var)->format('m/d/Y H:i'); ?>

.. note:: 更新 ``Blade`` 指令的逻辑后，你需要删除所有已缓存的 ``Blade`` 视图。使用 ``Artisan`` 命令 ``view:clear`` 来清除被缓存的视图。

自定义 If 语句
--------------

底层调用 ``\Illuminate\View\Compilers\BladeCompiler`` 的 ``if`` 方法。

编写自定义指令有时候比定义简单、常见的条件语句更复杂，但是它又非常必要。因此， ``Blade`` 提供了一个 ``Blade::if`` 方法，它能使用闭包来快速定义自定义条件指令。 例如，定义一个自定义条件来检查当前的应用程序环境。我们可以在 ``AppServiceProvider`` 的 ``boot`` 方法中这样做：

.. code-block:: php

    <?php
    use Illuminate\Support\Facades\Blade;

    /**
     * 执行服务的注册后引导。
     *
     * @return void
     */
    public function boot()
    {
        Blade::if('env', function ($environment) {
            return app()->environment($environment);
        });
    }

一旦你定义了自定义条件之后，就可以很轻松地在模板中使用它：

.. code-block:: php

    <?php
    @env('local')
        // 该应用在本地环境中...
    @elseenv('testing')
        // 该应用在测试环境中...
    @else
        // 该应用不在本地或测试环境中...
    @endenv

