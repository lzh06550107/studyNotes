========
Session
========

简介
====
由于 ``HTTP`` 驱动的应用程序是无状态的， ``Session`` 提供了一种在多个请求之间存储有关用户的信息的方法。 Laravel 通过同一个可读性强的 ``API`` 处理各种自带的 ``Session`` 后台驱动程序。支持诸如比较热门的 ``Memcached`` ,  ``Redis`` 和数据库。


配置
----
``Session`` 的配置文件存储在 ``config/session.php`` 文件中。请务必查看此文件中对于你可用的选项。默认情况下， Laravel 配置了适用于大多数应用程序的 ``file Session`` 驱动。在生产环境下，你可以考虑使用 ``memcached`` 或 ``redis`` 驱动来实现更出色的 ``Session`` 性能。

``Session driver`` 的配置选项定义了每个请求存储 ``Session`` 数据的位置。 Laravel 自带了几个不错且可开箱即用的驱动：

- ``file`` - 将 ``Session`` 存储在 ``storage/framework/sessions`` 中。
- ``cookie`` - ``Session`` 存储在安全加密的 ``cookie`` 中。
- ``database`` - 将 ``Session`` 存储在数据库中。
- ``memcached`` / ``redis`` - ``Session`` 存储在基于高速缓存的存储系统中。
- ``array`` - ``Sessions`` 存储在 PHP 数组中，但不会被持久化。

.. tip:: 数组驱动一般用于 测试 ，并防止存储在 ``Session`` 中的数据被持久化。

驱动程序先决条件
----------------
数据库
^^^^^^
使用 ``database`` 作为 ``Session`` 驱动时，你需要创建一张包含 ``Session`` 各项数据的表。以下例子是使用 ``Schema`` 建表：

.. code-block:: php

    <?php
    Schema::create('sessions', function ($table) {
        $table->string('id')->unique();
        $table->unsignedInteger('user_id')->nullable();
        $table->string('ip_address', 45)->nullable();
        $table->text('user_agent')->nullable();
        $table->text('payload');
        $table->integer('last_activity');
    });

你可以使用 ``Artisan`` 命令 ``session:table`` 来生成此迁移：

.. code-block:: shell

    php artisan session:table

    php artisan migrate

Redis
^^^^^^
Laravel 在使用 ``Redis`` 作为 ``Session`` 驱动之前，需要通过 ``Composer`` 安装 ``predis/predis`` 扩展包 (~1.0)。然后在 ``database`` 配置文件中配置 ``Redis`` 连接信息。在 ``session`` 配置文件中， ``connection`` 选项可用于指定 ``Session`` 使用哪个 ``Redis`` 连接。

使用 Session
============

获取数据
--------
通过依赖注入
^^^^^^^^^^^^
Laravel 中处理 ``Session`` 数据有两种主要方法：全局辅助函数 ``session`` 和通过一个 ``Request`` 实例。首先，我们来看看通过控制器方法类型提示一个 ``Request`` 实例来访问 ``session`` 。控制器方法依赖项会通过 Laravel 服务容器 实现自动注入:

.. code-block:: php

    <?php
    namespace App\Http\Controllers;

    use Illuminate\Http\Request;
    use App\Http\Controllers\Controller;

    class UserController extends Controller
    {
        /**
         * 展示给定用户的配置文件。
         *
         * @param  Request  $request
         * @param  int  $id
         * @return Response
         */
        public function show(Request $request, $id)
        {
            $value = $request->session()->get('key');

            //
        }
    }

当你从 ``Session`` 获取值时，你还可以传递一个默认值作为 ``get`` 方法的第二个参数。如果 ``Session`` 中不存在指定的键，便会返回这个默认值。若传递一个闭包作为 ``get`` 方法的默认值，并且所请求的键并不存在时， ``get`` 方法将执行闭包并返回其结果：

.. code-block:: php

    <?php
    $value = $request->session()->get('key', 'default');

    $value = $request->session()->get('key', function () {
        return 'default';
    });

全局辅助函数 Session
^^^^^^^^^^^^^^^^^^^^
你也可以使用全局的 PHP 辅助函数 ``session`` 来获取和存储的 ``Session`` 数据。 使用单个字符串类型的值作为参数调用辅助函数 ``session`` 时，它会返回该字该符串对应的 ``Session`` 键的值。当使用一个键值对数组作为参数调用辅助函数 ``session`` 时，传入的键值将会存储在 ``Session`` 中：

.. code-block:: php

    <?php
    Route::get('home', function () {
        // 获取 session 中的一条数据...
        $value = session('key');

        // 指定一个默认值...
        $value = session('key', 'default');

        // 在 Session 中存储一条数据...
        session(['key' => 'value']);
    });

.. tip:: 通过 ``HTTP`` 请求实例操作 ``Session`` 与使用全局辅助函数 ``session`` 两者之间并没有实质上的区别。这两种方法都可以通过所有测试用例中可用的 ``assertSessionHas`` 方法进行 测试。

获取所有的 Session 数据
^^^^^^^^^^^^^^^^^^^^^^^
如果你想要获取所有的 ``Session`` 数据，可以使用 ``all`` 方法：

.. code-block:: php

    <?php
    $data = $request->session()->all();

判断 Session 中是否存在某个值
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
要确定 ``Session`` 中是否存在某个值，可以使用 ``has`` 方法。如果该值存在且不为 ``null`` ，那么 ``has`` 方法会返回 ``true`` ：

.. code-block:: php

    <?php
    if ($request->session()->has('users')) {
        //
    }

要确定 ``Session`` 中是否存在某个值，即使其值为 ``null`` ，也可以使用 ``exists`` 方法。如果值存在，则 ``exists`` 方法返回 ``true`` ：

.. code-block:: php

    <?php
    if ($request->session()->exists('users')) {
        //
    }

存储数据
--------
要存储数据到 ``Session`` ，你可以使用 ``put`` 方法，或者使用辅助函数 ``session`` 。

.. code-block:: php

    <?php
    // 通过请求实例...
    $request->session()->put('key', 'value');

    // 通过全局辅助函数...
    session(['key' => 'value']);

在 Session 数组中保存数据
^^^^^^^^^^^^^^^^^^^^^^^^^^
``push`` 方法可以将一个新的值添加到 ``Session`` 数组内。例如，假设 ``user.teams`` 这个键是包含团队名称的数组，你可以像这样将一个新的值加入到此数组中：

.. code-block:: php

    <?php
    $request->session()->push('user.teams', 'developers');

检索 & 删除一个项目
^^^^^^^^^^^^^^^^^^^
``pull`` 方法可以只用一条语句就从 ``Session`` 中检索并且删除一个项目：

.. code-block:: php

    <?php
    $value = $request->session()->pull('key', 'default');

闪存数据
---------
有时候你仅想在下一个请求之前在 ``Session`` 中存入数据，你可以使用 ``flash`` 方法。使用这个方法保存在 ``Session`` 中的数据，只会保留到下个 ``HTTP`` 请求到来之前，然后就会被删除。闪存数据主要用于短期的状态消息：

.. code-block:: php

    <?php
    $request->session()->flash('status', 'Task was successful!');

如果需要给更多请求保留闪存数据，可以使用 ``reflash`` 方法，这将会所有的闪存数据保留给其它请求。如果只想保留特定的闪存数据，则可以使用 ``keep`` 方法：

.. code-block:: php

    <?php
    $request->session()->reflash();

    $request->session()->keep(['username', 'email']);

删除数据
--------
``forget`` 方法可以从 ``Session`` 内删除一条数据。如果你想删除 ``Session`` 内所有数据，可以使用 ``flush`` 方法：

.. code-block:: php

    <?php
    $request->session()->forget('key');

    $request->session()->flush();

重新生成 Session ID
-------------------
重新生成 ``Session ID`` ，通常是为了防止恶意用户利用 session fixation 对应用进行攻击 (https://blog.csdn.net/youanyyou/article/details/79406499)。

如果使用了内置函数 ``LoginController`` ，Laravel 会自动重新生成身份验证中的 ``Session ID`` 。否则，你需要手动使用 ``regenerate`` 方法重新生成 ``Session ID`` 。

.. code-block:: php

    <?php
    $request->session()->regenerate();

添加自定义 Session 驱动
=======================
实现驱动
--------
你自定义的 ``Session`` 驱动必须实现 ``SessionHandlerInterface`` 接口。这个接口包含了一些我们需要实现的简单方法。下面是一个大概的 ``MongoDB`` 实现流程示例：

.. code-block:: php

    <?php

    namespace App\Extensions;

    class MongoSessionHandler implements \SessionHandlerInterface
    {
        public function open($savePath, $sessionName) {}
        public function close() {}
        public function read($sessionId) {}
        public function write($sessionId, $data) {}
        public function destroy($sessionId) {}
        public function gc($lifetime) {}
    }

.. note::  Laravel 默认没有附带扩展用的目录，你可以把它放在你喜欢的目录内。在下面这个例子中，我们创建了一个 ``Extensions`` 目录放置自定义的 ``MongoHandler`` 扩展。

接口中的这些方法不太容易理解。让我们来快速了解每个方法的作用：

- ``open`` 方法通常用于基于文件的 ``Session`` 存储系统。因为 Laravel 已经附带了一个 ``file`` 的驱动，所以你不需要在该方法中放置任何代码。PHP 要求必需要有这个方法的实现（这只是一个糟糕的接口设计），你只需要把这个方法置空。
- ``close`` 方法跟 ``open`` 方法很相似，通常也可以被忽略。对大多数的驱动而言，此方法不是必须的。
- ``read`` 方法应当返回与给定的 ``$sessionId`` 相匹配的 ``Session`` 数据的字符串格式。在你的自定义的驱动中获取或存储 ``Session`` 数据时，不需要进行任何序列化或其它编码，因为 Laravel 会执行序列化。
- ``write`` 将与 ``$sessionId`` 关联的给定的 ``$data`` 字符串写入到一些持久化存储系统，如 ``MongoDB`` 、 ``Dynamo`` 等。再次重申，你不需要进行任何序列化或其它编码，因为 Laravel 会自动处理这些事情。
- ``destroy`` 方法会从持久化存储中移除与 ``$sessionId`` 相关联的数据。
- ``gc`` 方法能销毁给定的 ``$lifetime`` （UNIX 的时间戳）之前的所有数据。对本身拥有过期机制的系统如 ``Memcached`` 和 ``Redis`` 而言，该方法可以置空。

注册驱动
--------
你的 ``Session`` 驱动实现之后，你还需要在框架中注册该驱动，即将该扩展驱动添加到 Laravel Session 后台。然后在 服务提供者 的 ``boot`` 方法内调用 ``Session Facade`` 的 ``extend`` 方法。之后你就可以从现有的 ``AppServiceProvider`` 或者新创建的提供器中执行此操作。

.. code-block:: php

    <?php
    namespace App\Providers;

    use App\Extensions\MongoSessionHandler;
    use Illuminate\Support\Facades\Session;
    use Illuminate\Support\ServiceProvider;

    class SessionServiceProvider extends ServiceProvider
    {
        /**
         * 执行注册后引导服务。
         *
         * @return void
         */
        public function boot()
        {
            Session::extend('mongo', function ($app) {
                // Return implementation of SessionHandlerInterface...
                return new MongoSessionHandler;
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

一旦 ``Session`` 驱动被注册，则必须在 ``config/session.php`` 的配置文件内使用 ``Mongo`` 驱动。
