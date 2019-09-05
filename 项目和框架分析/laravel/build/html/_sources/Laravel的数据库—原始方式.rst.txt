===============
数据库-原始操作
===============

简介
====
Laravel 能使用原生 ``SQL`` 、查询构造器 和 ``Eloquent ORM`` 在各种数据库后台与数据库进行非常简单的交互。当前 Laravel 支持四种数据库:

- MySQL
- Postgres
- SQLite
- SQL Server

配置
====
数据库的配置文件放置在 ``config/database.php`` 文件中，你可以在此定义所有的数据库连接，并指定默认使用的连接。此文件内提供了大部分 Laravel 能支持的数据库配置示例。

默认情况下， Laravel 的示例 环境配置 使用了 Laravel Homestead（这是一种小型虚拟机，能让你很方便地在本地进行 Laravel 的开发）。你可以根据本地数据库的需要修改这个配置。

SQLite 配置
-----------
使用类似 ``touch database/database.sqlite`` 之类命令创建一个新的 ``SQLite`` 数据库之后，可以使用数据库的绝对路径配置 **环境变量** 来指向这个新创建的数据库：

.. code-block:: shell

    DB_CONNECTION=sqlite
    DB_DATABASE=/absolute/path/to/database.sqlite

读和写连接分离
--------------
有时候你希望 ``SELECT`` 语句使用一个数据库连接，而 ``INSERT`` 、 ``UPDATE`` 和 ``DELETE`` 语句使用另一个数据库连接。在 Laravel 中这就像小菜一碟，无论你是使用原生查询，查询构造器 或者 Eloquent ORM 它都能轻松实现。

想了解数据库读写分离如何配置, 让我们看看这个例子：

.. code-block:: php

    <?php

    'mysql' => [
        'read' => [
            'host' => '192.168.1.1',
        ],
        'write' => [
            'host' => '196.168.1.2'
        ],
        'sticky'    => true,
        'driver'    => 'mysql',
        'database'  => 'database',
        'username'  => 'root',
        'password'  => '',
        'charset' => 'utf8mb4',
        'collation' => 'utf8mb4_unicode_ci',
        'prefix'    => '',
    ],

注意在上边的例子中，配置数组中增加了 3 个键： ``read`` ， ``write`` 和 ``sticky`` 。 ``read`` 和 ``write`` 都包含一个键为 ``host`` 的数组。而 ``read`` 和 ``write`` 的其它数据库配置都在键为 ``mysql`` 的主数组中：

如果你希望重写主数组的某个配置项，只需要将它放入 ``read`` 和 ``write`` 的数组中即可。 所以，这个例子中： ``192.168.1.1`` 将用作「读」连接的主机， 而 ``192.168.1.2`` 将作为「写」连接的主机。这两个连接会共享 ``mysql`` 主数组的各项配置，如数据库的凭据，前缀，字符编码 等。

sticky 选项
^^^^^^^^^^^
``sticky`` 是一个 可选 值，它可用于立即读取在当前请求周期内已写入数据库的记录(因为主从复制有延迟)。若 ``sticky`` 被启用，并且当前请求周期内执行过「写」操作，那么任何「读」操作都将使用「写」连接。这样可确保同一周期内写入的数据在同一周期内都可以被立即读取。它是否启用，取决于应用程序的需求。

使用多个数据库连接
------------------
当使用多个数据库连接时，你可以通过 ``DB`` facade 的 ``connection`` 方法访问每一个连接。将 ``name`` 作为 ``connection`` 方法的参数传递，即可使用 ``config/database.php`` 配置文件中对应名称的连接：

.. code-block:: php

    <?php
    $users = DB::connection('foo')->select(...);

你也可以在一个连接的实例上使用 ``getPdo`` 方法 访问底层的 ``PDO`` 实例：

.. code-block:: php

    <?php
    $pdo = DB::connection()->getPdo();

运行原生 SQL 查询
=================
配置好数据库连接后，可以使用 ``DB`` Facade 运行查询。 ``DB`` Facade 为每种类型的查询提供了方法： ``select`` 、 ``update`` 、 ``insert`` 、 ``delete`` 和 ``statement`` 。

运行 Select 查询
----------------
你可以使用 ``DB`` Facade 的 ``select`` 方法来运行基础的查询语句：

.. code-block:: php

    <?php

    namespace App\Http\Controllers;

    use Illuminate\Support\Facades\DB;
    use App\Http\Controllers\Controller;

    class UserController extends Controller
    {
        /**
         * 显示所有应用程序的用户列表
         *
         * @return Response
         */
        public function index()
        {
            $users = DB::select('select * from users where active = ?', [1]);

            return view('user.index', ['users' => $users]);
        }
    }

传递给 ``select`` 方法的第一个参数是一个原生的 ``SQL`` 查询，而第二个参数则是需要绑定到查询中的参数值。 通常，这些值用于约束 ``where`` 语句。参数绑定用于防止 ``SQL`` 注入。

``select`` 方法将始终返回一个数组，数组中的每个结果都是一个 ``StdClass`` 对象，可以像下面这样访问结果值。

.. code-block:: php

    <?php
    foreach ($users as $user) {
        echo $user->name;
    }

使用命名绑定
^^^^^^^^^^^^
除了使用 ``?`` 来表示参数绑定外，你也可以使用命名绑定来执行一个查询：

.. code-block:: php

    <?php
    $results = DB::select('select * from users where id = :id', ['id' => 1]);

运行插入语句
------------
可以使用 ``DB`` Facade 的 ``insert`` 方法来执行 ``insert`` 语句。与 ``select`` 一样，该方法将原生 ``SQL`` 查询作为其第一个参数，并将绑定数据作为第二个参数：

.. code-block:: php

    <?php
    DB::insert('insert into users (id, name) values (?, ?)', [1, 'Dayle']); // 需要测试返回值？？

新增成功则返回 ``true`` 。

运行更新语句
------------
``update`` 方法用于更新数据库中现有的记录。该方法返回受该语句影响的行数：

.. code-block:: php

    <?php
    $affected = DB::update('update users set votes = 100 where name = ?', ['John']);

运行删除语句
------------
``delete`` 方法用于从数据库中删除记录。与 ``update`` 一样，返回受该语句影响的行数：

.. code-block:: php

    <?php
    $deleted = DB::delete('delete from users');

运行普通语句
------------
有些数据库语句不会有任何返回值。对于这些语句，你可以使用 ``DB`` Facade 的 ``statement`` 方法来运行：

.. code-block:: php

    <?php
    DB::statement('drop table users');

执行成功，返回 ``true`` 。

监听查询事件
============
如果你想监控程序执行的每一个 ``SQL`` 查询，你可以使用 ``listen`` 方法。这个方法对于记录查询或调试非常有用。你可以在 服务提供器 中注册你的查询监听器：

.. code-block:: php

    <?php
    namespace App\Providers;

    use Illuminate\Support\Facades\DB;
    use Illuminate\Support\ServiceProvider;

    class AppServiceProvider extends ServiceProvider
    {
        /**
         * 启动应用服务
         *
         * @return void
         */
        public function boot()
        {
            DB::listen(function ($query) {
                // $query->sql
                // $query->bindings
                // $query->time
            });
        }

        /**
         * 注册服务提供器
         *
         * @return void
         */
        public function register()
        {
            //
        }
    }

数据库事务
==========
你可以使用 ``DB`` facade 的 ``transaction`` 方法在数据库事务中运行一组操作。如果在事务 ``Closure`` 中出现一个异常，那么事务将回滚。如果 ``Closure`` 执行成功，事务将自动提交。你不需要担心在使用 ``transaction`` 方法时手动回滚或提交：

.. code-block:: php

    <?php
    DB::transaction(function () {
        DB::table('users')->update(['votes' => 1]);

        DB::table('posts')->delete();
    });

处理死锁
--------
``transaction`` 方法接受一个可选的第二参数，该参数表示事务发生死锁时重试的次数。 一旦这些尝试用尽，就会抛出一个异常：

.. code-block:: php

    <?php
    DB::transaction(function () {
        DB::table('users')->update(['votes' => 1]);

        DB::table('posts')->delete();
    }, 5);

手动使用事务
------------
如果您想要手工开始一个事务，并且对回滚和提交有完全的控制，那么您可以在 ``DB`` facade 上使用 ``beginTransaction`` 方法：

.. code-block:: php

    <?php
    DB::beginTransaction();

你可以使用 ``rollBack`` 方法回滚事务：

.. code-block:: php

    <?php
    DB::rollBack();

最后，你可以使用 ``commit`` 方法提交事务：

.. code-block:: php

    <?php
    DB::commit();

.. tip:: ``DB`` facade 的事务方法同样适用于 查询构造器 和 Eloquent ORM。

.. code-block:: php

    <?php

    DB::beginTransaction(); //开启事务
    try {
        //这里省略了业务逻辑代码
        if($isSuccess){
            DB::commit();   //成功，提交事务
        }
        //思考->如果事务开启不提交会发生什么后果？？？
    } catch(\Illuminate\Database\QueryException $ex) {
        DB::rollback(); //失败，回滚事务
        echo 'error';
    }
    echo 'success';

    DB::beginTransaction(); //事务开始
    try {
           //代码区

        DB::commit(); //提交事务
    } catch(\Illuminate\Database\QueryException $ex) {
        DB::rollback(); //回滚事务

        //异常处理
    }