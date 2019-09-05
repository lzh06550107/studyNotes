=================
数据库-查询构建器
=================

简介
====
Laravel 的数据库查询构造器为创建和运行数据库查询提供了一个方便的接口。它能用来执行应用程序中的大部分数据库操作，且可在所有支持的数据库系统上运行。

Laravel 的查询构造器使用 ``PDO`` 参数绑定来保护您的应用程序免受 ``SQL`` 注入攻击。因此没有必要清理作为绑定传递的字符串。

获取结果
========

从数据表中获取所有行
--------------------
你可以 ``DB`` facade 上使用 ``table`` 方法来开始查询。该 ``table`` 方法为给定的表返回一个查询构造器实例，允许你在查询上链式调用更多的约束，最后使用 ``get`` 方法获取结果：

.. code-block:: php

    <?php

    namespace App\Http\Controllers;

    use Illuminate\Support\Facades\DB;
    use App\Http\Controllers\Controller;

    class UserController extends Controller
    {
        /**
         * 显示所有应用程序用户的列表
         *
         * @return Response
         */
        public function index()
        {
            $users = DB::table('users')->get();

            return view('user.index', ['users' => $users]);
        }
    }

该 ``get`` 方法返回一个包含 ``Illuminate\Support\Collection`` 的结果，其中每个结果都是 PHP ``StdClass`` 对象的一个实例。你可以访问字段作为对象的属性来访问每列的值：

.. code-block:: php

    <?php
    foreach ($users as $user) {
        echo $user->name;
    }

从数据表中获取单行或单行某列的值
---------------------------------
如果你只需要从数据表中检索一行数据，你可以使用 ``first`` 方法。该方法返回一个 ``StdClass`` 对象：

.. code-block:: php

    <?php
    $user = DB::table('users')->where('name', 'John')->first();

    echo $user->name;

如果你甚至不需要整行数据，可以使用 ``value`` 方法从记录中获取单个值。该方法将直接返回第一条记录指定字段的值：

.. code-block:: php

    <?php
    $email = DB::table('users')->where('name', 'John')->value('email');

获取一列的值
------------
如果你想获取包含单列值的集合，你可以使用 ``pluck`` 方法。在下面的例子中，我们将获取角色表中标题的集合：

.. code-block:: php

    <?php
    $titles = DB::table('roles')->pluck('title');

    foreach ($titles as $title) {
        echo $title;
    }

你也可以在返回的集合中指定字段的自定义键值(就是以某列的值作为键，另一列的值作为键值)：

.. code-block:: php

    <?php
    $roles = DB::table('roles')->pluck('title', 'name');

    foreach ($roles as $name => $title) {
        echo $title;
    }

``implode`` 函数可以将多条数据的某一列拼成字符串：

.. code-block:: php

    <?php
    DB::table('users')->where('id', '=', 1)->implode('foo', ',');//'bar,baz'

分块结果
--------
如果你需要处理数千条数据库记录， 可以考虑使用 ``chunk`` 方法。该方法每次只取出一小块结果，并将取出的结果传递给 闭包 处理。这对于编写数千条记录的 ``Artisan`` 命令 而言是非常有用的。例如，一次处理 ``users`` 表中的 ``100`` 条记录：

.. code-block:: php

    <?php
    DB::table('users')->orderBy('id')->chunk(100, function ($users) {
        foreach ($users as $user) {
            //
        }
    });

你可以从 闭包 中返回 ``false`` 来阻止进一步的分块结果：

.. code-block:: php

    <?php
    DB::table('users')->orderBy('id')->chunk(100, function ($users) {
        // Process the records...

        return false;
    });

如果不想按照主键 ``id`` 来进行分块，我们还可以自定义分块主键：

.. code-block:: php

    <?php
    DB::table('users')->orderBy('id')->chunkById(100, function ($users) {
        foreach ($users as $user) {
            //
        }
    }, 'someIdField');

Selects
=======
指定一个 Select 语句
--------------------
当然你可能并不总是希望从数据库表中获取所有列。使用 ``select`` 方法，你可以自定义一个 ``select`` 语句来查询指定的字段：

.. code-block:: php

    <?php
    $users = DB::table('users')->select('name', 'email as user_email')->get();

``distinct`` 允许你强制让查询返回不重复的结果：

.. code-block:: php

    <?php
    $users = DB::table('users')->distinct()->get();

如果你已有一个查询构造器实例，并且希望在现有的 ``select`` 语句中加入一个字段，则可以 ``addSelect`` 方法：

.. code-block:: php

    <?php
    $query = DB::table('users')->select('name');

    $users = $query->addSelect('age')->get();

子查询
------
所谓的 ``select`` 子查询，就是查询的字段来源于其他数据表。对于这种查询，可以分成两部来理解，首先忽略整个 ``select`` 子查询，查出第一个表中的数据，然后根据第一个表的数据执行子查询，

laravel 的 ``selectSub`` 支持闭包函数、 ``queryBuild`` 对象或者原生 ``sql`` 语句，以下是单元测试样例：

.. code-block:: php

    <?php
    $query = DB::table('one')->select(['foo', 'bar'])->where('key', '=', 'val');

    $query->selectSub(function ($query) {
            $query->from('two')->select('baz')->where('subkey', '=', 'subval');
        }, 'sub');

    // 另一种写法
    $query = DB::table('one')->select(['foo', 'bar'])->where('key', '=', 'val');
    $query_sub = DB::table('one')->select('baz')->where('subkey', '=', 'subval');

    $query->selectSub($query_sub, 'sub');

生成的 ``sql`` 语句：

.. code-block:: sql

    select "foo", "bar", (select "baz" from "two" where "subkey" = 'subval') as "sub" from "one" where "key" = 'val'

原生表达式
==========
有时候你可能需要在查询中使用原生表达式。创建一个原生表达式， 你可以使用 ``DB::raw`` 方法：

.. code-block:: php

    <?php
    $users = DB::table('users')
                         ->select(DB::raw('count(*) as user_count, status'))
                         ->where('status', '<>', 1)
                         ->groupBy('status')
                         ->get();

.. note:: 原生表达式将会被当做字符串注入到查询中，因此你应该小心使用，避免创建 ``SQL`` 注入漏洞。

原生方法
--------
可以使用以下的方法代替 ``DB::raw`` 将原生表达式插入查询的各个部分。

selectRaw
^^^^^^^^^
``selectRaw`` 方法可以用来代替 ``select(DB::raw(...))`` 。这个方法的第二个参数接受一个可选的绑定参数数组：

.. code-block:: php

    <?php
    $orders = DB::table('orders')
                    ->selectRaw('price * ? as price_with_tax', [1.0825])
                    ->get();

whereRaw / orWhereRaw
^^^^^^^^^^^^^^^^^^^^^
可以使用 ``whereRaw`` 和 ``orWhereRaw`` 方法将原生的 ``where`` 注入到你的查询中。这些方法接受一个可选的绑定数组作为他们的第二个参数：

.. code-block:: php

    <?php
    $orders = DB::table('orders')
                    ->whereRaw('price > IF(state = "TX", ?, 100)', [200])
                    ->get();

    $users = DB::table('users')
            ->orWhereRaw('id = ? or email = ?', [1, 'foo'])
            ->get();

havingRaw / orHavingRaw
^^^^^^^^^^^^^^^^^^^^^^^
``havingRaw`` 和 ``orHavingRaw`` 方法可用于将原生字符串设置为 ``having`` 语句的值：

.. code-block:: php

    <?php
    $orders = DB::table('orders')
                    ->select('department', DB::raw('SUM(price) as total_sales'))
                    ->groupBy('department')
                    ->havingRaw('SUM(price) > 2500')
                    ->get();

orderByRaw
^^^^^^^^^^
``orderByRaw`` 方法可用于将原生字符串设置为 ``order by`` 子句的值：

.. code-block:: php

    <?php
    $orders = DB::table('orders')
                    ->orderByRaw('updated_at - created_at DESC')
                    ->get();

Joins
======

Inner Join 语句
---------------
查询构造器也可编写 ``join`` 语句。若要执行基本的「内链接」，你可以在查询构造器实例上使用 ``join`` 方法。传递给 ``join`` 方法的第一个参数是你需要连接的表的名称，而其它参数则用来指定连接的字段约束。你还可以在单个查询中连接多个数据表：

.. code-block:: php

    <?php

    DB::table('services')->select('*')->join('translations AS t', 't.item_id', '=', 'services.id');
    // select * from `services` inner join `translations` as `t` on `t`.`item_id` = `services`.`id`

    $users = DB::table('users')
            ->join('contacts', 'users.id', '=', 'contacts.user_id')
            ->join('orders', 'users.id', '=', 'orders.user_id')
            ->select('users.*', 'contacts.phone', 'orders.price')
            ->get();
    // select `users`.*, `contacts`.`phone`, `orders`.`price` from `users` inner join `contacts` on `users`.`id` = `contacts`.`user_id` inner join `orders` on `users`.`id` = `orders`.`user_id`

    DB::table('users')->select('*')->join('contacts', function ($j) {
        $j->on('users.id', '=', 'contacts.id')->orOn('users.name', '=', 'contacts.name');
    })
    // select * from `users` inner join `contacts` on `users`.`id` = `contacts`.`id` or `users`.`name` = `contacts`.`name`

    DB::table('users')->select('*')->from('users')->joinWhere('contacts', 'col1', '=', function ($j) {
            $j->select('users.col2')->from('users')->where('users.id', '=', 'foo');
    })->toSql();
    // select * from `users` inner join `contacts` on `col1` = (select `users`.`col2` from `users` where `users`.`id` = ?)

Left Join 语句
--------------
如果你想使用「左连接」代替「内连接」，使用 ``leftJoin`` 方法。 ``leftJoin`` 方法与 ``join`` 方法用法相同：

.. code-block:: php

    <?php
    $users = DB::table('users')
                ->leftJoin('posts', 'users.id', '=', 'posts.user_id')
                ->get();
    // select * from `users` left join `posts` on `users`.`id` = `posts`.`user_id`

    DB::table('users')->select('*')->leftJoin('contacts', function ($j) {
        $j->on('users.id', '=', 'contacts.id')->where(function ($j) {
            $j->where('contacts.country', '=', 'US')->orWhere('contacts.is_partner', '=', 1);
        });
    })->toSql();
    // select * from `users` left join `contacts` on `users`.`id` = `contacts`.`id` and (`contacts`.`country` = 'US' or `contacts`.`is_partner` = 1)

    DB::table('users')->select('*')->leftJoin('contacts', function ($j) {
        $j->on('users.id', '=', 'contacts.id')->where('contacts.is_active', '=', 1)->orOn(function ($j) {
            $j->orWhere(function ($j) {
                $j->where('contacts.country', '=', 'UK')->orOn('contacts.type', '=', 'users.type');
            })->where(function ($j) {
                $j->where('contacts.country', '=', 'US')->orWhereNull('contacts.is_partner');
            });
        });
    })->toSql();
    // select * from `users` left join `contacts` on `users`.`id` = `contacts`.`id` and `contacts`.`is_active` = ? or ((`contacts`.`country` = ? or `contacts`.`type` = `users`.`type`) and (`contacts`.`country` = ? or `contacts`.`is_partner` is null))

Cross Join 语句
---------------
使用 ``crossJoin`` 方法和你想要交叉连接的表名来做「交叉连接」。交叉连接在第一个表和连接之间生成笛卡尔积：

.. code-block:: php

    <?php
    $users = DB::table('sizes')
                ->crossJoin('colours')
                ->get();

高级 Join 语句
--------------
你可以指定更高级的 ``join`` 语句。比如传递一个 ``闭包`` 作为 ``join`` 方法的第二个参数。此 ``闭包`` 接收一个 ``JoinClause`` 对象，从而在其中指定 ``join`` 语句中指定约束：

.. code-block:: php

    <?php
    DB::table('users')
            ->join('contacts', function ($join) {
                $join->on('users.id', '=', 'contacts.user_id')->orOn(...);
            })
            ->get();

如果你想要在连接上使用「where」风格的语句，可以在连接上使用 ``where`` 和 ``orWhere`` 方法。这些方法会将列和值进行比较而不是列和列进行比较：

.. code-block:: php

    <?php
    DB::table('users')
            ->join('contacts', function ($join) {
                $join->on('users.id', '=', 'contacts.user_id')
                     ->where('contacts.user_id', '>', 5);
            })
            ->get();

Unions
======
查询构造器还提供了将两个查询「联合」起来的快捷方式。比如，你可以先创建一个查询，然后使用 ``union`` 方法将其和第二个查询进行联合：

.. code-block:: php

    <?php
    $first = DB::table('users')
                ->whereNull('first_name');

    $users = DB::table('users')
                ->whereNull('last_name')
                ->union($first)
                ->get();

    $query = DB::table('users')->select('*')->where('id', '=', 1);
    $query->union(DB::table('users')->select('*')->where('id', '=', 2));
    //(select * from `users` where `id` = 1) union (select * from `users` where `id` = 2)

    $query = DB::table('users')->select('*')->where('id', '=', 1);
    $query->union(DB::table('users')->select('*')->where('id', '=', 2));
    $query->union(DB::table('users')->select('*')->where('id', '=', 3));
    //(select * from "users" where "id" = 1) union (select * from "users" where "id" = 2) union (select * from "users" where "id" = 3)

    //union 语句可以与 orderBy 相结合：
    $query = DB::table('users')->select('*')->where('id', '=', 1);
    $query->union(DB::table('users')->select('*')->where('id', '=', 2));
    $query->orderBy('id', 'desc');
    //(select * from `users` where `id` = ?) union (select * from `users` where `id` = ?) order by `id` desc

    //union 语句可以与 limit、offset 相结合：
    $query = DB::table('users')->select('*');
    $query->union(DB::table('users')->select('*'));
    $query->skip(5)->take(10);
    //(select * from `users`) union (select * from `dogs`) limit 10 offset 5

.. tip:: ``unionAll`` 方法也是可用的，并且和 ``union`` 方法用法相同。 ``union`` 因为要进行重复值扫描，所以效率低。如果合并没有刻意要删除重复行，那么就使用 ``unionAll`` 。

Where 语句
==========

简单的 Where 语句
-----------------
使用查询构建器上的 ``where`` 方法可以添加 ``where`` 子句到查询中。调用 ``where`` 最基本的方式需要传递三个参数，第一个参数是列名，第二个参数是任意一个数据库系统支持的运算符，第三个参数是该列要比较的值。

例如，下面是一个要验证「votes」字段的值等于 100 的查询：

.. code-block:: php

    <?php
    $users = DB::table('users')->where('votes', '=', 100)->get();

为了方便，如果你只是简单比较列值和给定数值是否相等，可以将数值直接作为 ``where`` 方法的第二个参数：

.. code-block:: php

    <?php
    $users = DB::table('users')->where('votes', 100)->get();

当然你还可以使用其他运算符来编写 ``where`` 子句：

.. code-block:: php

    <?php
    $users = DB::table('users')
                    ->where('votes', '>=', 100)
                    ->get();

    $users = DB::table('users')
                    ->where('votes', '<>', 100)
                    ->get();

    $users = DB::table('users')
                    ->where('name', 'like', 'T%')
                    ->get();


还可以传递数组到 ``where`` 函数中， **表示 And 条件** ：

.. code-block:: php

    <?php
    $users = DB::table('users')->where([
        ['status', '=', '1'],
        ['subscribed', '<>', '1'],
    ])->get();

生成sql语句：

.. code-block:: sql

    select * from `users` where (`status` = 1 and `subscribed` <> 1)

传递数组和多次调用where有何区别？？

.. code-block:: php

    <?php
    DB::table('users')->where(
            ['status', '=', '1'])->where(
            ['subscribed', '<>', '1'])->toSql(); // 有问题？？

如何表示 Or 条件呢？

.. code-block:: php

    <?php
    $users = DB::table('users')
                        ->where('votes', '>', 100)
                        ->orWhere('name', 'John')
                        ->get();

    DB::table('users')
        ->where('name', '=', 'John')
        ->orWhere(function ($query) {
            $query->where('votes', '>', 100)
                  ->where('title', '<>', 'Admin');
        })->get();

生成的sql语句：

.. code-block:: sql

    select * from users where name = 'John' or (votes > 100 and title <> 'Admin')

where子查询
-----------

.. code-block:: php

    <?php
    DB::table('users')
            ->Where('id', '=', function ($q) {
            $q->select(new Raw('max(id)'))->from('users')->where('email', '=', 'bar');
        })->get();

生成的sql语句：

.. code-block:: sql

    select * from `users` where `id` = (select max(id) from `users` where `email` = 'bar')

whereBetween / whereNotBetween
------------------------------
``whereBetween`` 方法验证字段的值位于两个值之间：

.. code-block:: php

    <?php
    $users = DB::table('users')->whereBetween('votes', [1, 100])->get();

``whereNotBetween`` 方法验证字段的值位于两个值之外：

.. code-block:: php

    <?php
    $users = DB::table('users')
                        ->whereNotBetween('votes', [1, 100])
                        ->get();

whereIn / whereNotIn /orWhereIn / orWhereNotIn
-----------------------------------------------
``whereIn`` 方法验证字段的值在指定的数组内：

.. code-block:: php

    <?php
    $users = DB::table('users')
                        ->whereIn('id', [1, 2, 3])
                        ->get();

    DB::table('users')
                ->whereIn('id', function ($q) {
                    $q->select('id')->from('users')->where('age', '>', 25)->take(3);
                });

``whereNotIn`` 方法验证字段的值 不 在指定的数组内：

.. code-block:: php

    <?php
    $users = DB::table('users')
                        ->whereNotIn('id', [1, 2, 3])
                        ->get();

    DB::table('users')
                ->whereNotIn('id', function ($q) {
                    $q->select('id')->from('users')->where('age', '>', 25)->take(3);
                });

whereNull / whereNotNull / orWhereNull / orWhereNotNull
-------------------------------------------------------
``whereNull`` 方法验证字段的值为 ``NULL`` ：

.. code-block:: php

    <?php
    $users = DB::table('users')
                        ->whereNull('updated_at')
                        ->get();

``whereNotNull`` 方法验证字段的值不为 ``NULL`` ：

.. code-block:: php

    <?php
    $users = DB::table('users')
                        ->whereNotNull('updated_at')
                        ->get();

whereDate / whereMonth / whereDay / whereYear / whereTime
---------------------------------------------------------
``whereDate`` 方法用于比较字段的值和日期：

.. code-block:: php

    <?php
    $users = DB::table('users')
                    ->whereDate('created_at', '2016-12-31')
                    ->get();

``whereMonth`` 方法用于比较字段的值与一年的特定月份：

.. code-block:: php

    <?php
    $users = DB::table('users')
                    ->whereMonth('created_at', '12')
                    ->get();

``whereDay`` 方法用于比较字段的值与一个月的特定日期：

.. code-block:: php

    <?php
    $users = DB::table('users')
                    ->whereDay('created_at', '31')
                    ->get();

``whereYear`` 方法用于比较字段的值与特定年份：

.. code-block:: php

    <?php
    $users = DB::table('users')
                    ->whereYear('created_at', '2016')
                    ->get();

``whereTime`` 用于将字段的值与特定的时间进行比较：

.. code-block:: php

    <?php
    $users = DB::table('users')
                    ->whereTime('created_at', '=', '11:20')
                    ->get();

whereColumn
-----------
``whereColumn`` 方法用于验证两个字段是否相等：

.. code-block:: php

    <?php
    $users = DB::table('users')
                    ->whereColumn('first_name', 'last_name')
                    ->get();

还可以将比较运算符传递给该方法：

.. code-block:: php

    <?php
    $users = DB::table('users')
                    ->whereColumn('updated_at', '>', 'created_at')
                    ->get();

还可以传递多条件数组到 ``whereColumn`` 方法，这些条件通过 ``and`` 运算符连接：

.. code-block:: php

    <?php
    $users = DB::table('users')
                    ->whereColumn([
                        ['first_name', '=', 'last_name'],
                        ['updated_at', '>', 'created_at']
                    ])->get();

生成的sql语句：

.. code-block:: sql

    select * from `users` where (`first_name` = `last_name` and `updated_at` > `created_at`)

参数分组
---------
有时候你需要创建更高级的 ``where`` 子句，例如「where exists」或者嵌套的参数分组。 Laravel 的查询构造器也能够处理这些。下面，让我们看一个在括号中进行分组约束的例子：

.. code-block:: php

    <?php
    DB::table('users')
                ->where('name', '=', 'John')
                ->orWhere(function ($query) {
                    $query->where('votes', '>', 100)
                          ->where('title', '<>', 'Admin');
                })
                ->get();

正如你所看到的，传递 闭包 到 ``orWhere`` 方法构造查询构建器来开始一个约束分组。 该 闭包 接受一个查询构造器实例，上述语句等价于下面的 ``SQL`` ：

.. code-block:: php

    <?php
    select * from users where name = 'John' or (votes > 100 and title <> 'Admin')

Where Exists / whereNotExists / orWhereExists / orWhereNotExists
-----------------------------------------------------------------
``whereExists`` 方法允许你编写 ``where exists SQL`` 语句。 该 ``whereExists`` 方法接受一个 ``Closure`` 参数，该闭包获取一个查询构建器实例从而允许你定义放置在 "exists" 字句中查询：

.. code-block:: php

    <?php
    DB::table('users')
                ->whereExists(function ($query) {
                    $query->select(DB::raw(1))
                          ->from('orders')
                          ->whereRaw('orders.user_id = users.id');
                })
                ->get();

    // select * from users where exists ( select 1 from orders where orders.user_id = users.id)

    DB::table('users')
        ->whereNotExists(function ($query) {
            $query->select(DB::raw(1))
                  ->from('orders')
                  ->whereRaw('orders.user_id = users.id');
        })
        ->get();
    // select * from users where not exists ( select 1 from orders where orders.user_id = users.id)

    DB::table('users')
        ->orWhereExists(function ($query) {
            $query->select(DB::raw(1))
                  ->from('orders')
                  ->whereRaw('orders.user_id = users.id');
        })
        ->get();
    // select * from users or exists ( select 1 from orders where orders.user_id = users.id)

    DB::table('users')
        ->orWhereNotExists(function ($query) {
            $query->select(DB::raw(1))
                  ->from('orders')
                  ->whereRaw('orders.user_id = users.id');
        })
        ->get();
    // select * from users or not exists ( select 1 from orders where orders.user_id = users.id)

``select 1 from`` 查看是否有记录，一般是作条件用的。 ``select 1 from`` 中的 ``1`` 是一常量，查到的所有行的值都是它，但从效率上来说， ``1>anycol>*`` ，因为不用查字典表。

JSON Where 语句
---------------
Laravel 也支持查询 ``JSON`` 类型的字段（仅在对 ``JSON`` 类型支持的数据库上）。目前，本特性仅支持 ``MySQL 5.7+`` 和 ``Postgres`` 数据库。使用 ``->`` 操作符查询 ``JSON`` 数据：

.. code-block:: php

    <?php
    $users = DB::table('users')
                    ->where('options->language', 'en')
                    ->get();

    $users = DB::table('users')
                    ->where('preferences->dining->meal', 'salad')
                    ->get();


Ordering, Grouping, Limit, & Offset
====================================

orderBy
-------
``orderBy`` 方法允许你通过给定字段对结果集进行排序。 ``orderBy`` 的第一个参数应该是你希望排序的字段，第二个参数控制排序的方向，可以是 ``asc`` 或 ``desc`` ：

.. code-block:: php

    <?php
    $users = DB::table('users')
                    ->orderBy('name', 'desc')
                    ->get();

    DB::table('users')->select('*')->orderBy('email')->orderBy('age', 'desc');

    DB::table('users')->select('*')->orderBy('email')->orderByRaw('age desc');

latest / oldest
---------------
``latest`` 和 ``oldest`` 方法允许你通过日期对结果进行排序。默认情况下，结果集根据 ``created_at`` 列进行排序。或者，你可以按照你想要排序的字段作为字段名传入：

.. code-block:: php

    <?php
    $user = DB::table('users')
                    ->latest()
                    ->first();

inRandomOrder
--------------
``inRandomOrder`` 方法可以将查询结果随机排序。例如， 你可以使用这个方法获取一个随机用户：

.. code-block:: php

    <?php
    $randomUser = DB::table('users')
                    ->inRandomOrder()
                    ->first();

groupBy / having
----------------
``groupBy`` 和 ``having`` 方法对查询结果进行分组。 ``having`` 方法的用法与 ``where`` 方法类似： ``HAVING`` 子句可以让我们筛选成组后的各组数据， ``WHERE`` 子句在聚合前先筛选记录．也就是说作用在 ``GROUP BY`` 子句和 ``HAVING`` 子句前；而 ``HAVING`` 子句在聚合后对组记录进行筛选。

.. code-block:: php

    <?php

    DB::select('*')->from('users')->groupBy('email');

    DB::select('*')->from('users')->groupBy('id', 'email');

    DB::select('*')->from('users')->groupBy(['id', 'email']);

    DB::select('*')->from('users')->groupBy(new Raw('DATE(created_at)'));

    $users = DB::table('users')
                    ->groupBy('account_id')
                    ->having('account_id', '>', 100)
                    ->get();

可以将多个参数传递给 ``groupBy`` 方法，按多个字段进行分组：

.. code-block:: php

    <?php
    $users = DB::table('users')
                    ->groupBy('first_name', 'status')
                    ->having('account_id', '>', 100)
                    ->get();

关于 ``having`` 更高级的用法，请查看 ``havingRaw`` 方法。 ``having`` 语句的用法也很简单。大致有 ``having`` 、 ``orHaving`` 、 ``havingRaw`` 、 ``orHavingRaw`` 这几个函数：

.. code-block:: php

    <?php
    DB::select('*')->from('users')->having('email', '>', 1);

    DB::select('*')->from('users')->groupBy('email')->having('email', '>', 1);

    DB::select('*')->from('users')->having('email', 1)->orHaving('email', 2);

    DB::select('*')->from('users')->havingRaw('user_foo < user_bar');

    DB::select('*')->from('users')->having('baz', '=', 1)->orHavingRaw('user_foo < user_bar');

skip / take / limit / offset
----------------------------
想要限定查询返回的结果集的数目， 或者在查询中跳过给定数目的结果，可以使用 ``skip`` 和 ``take`` 方法：

.. code-block:: php

    <?php
    DB::select('*')->from('users')->offset(5)->limit(10);

    DB::select('*')->from('users')->skip(5)->take(10);

    DB::select('*')->from('users')->skip(-5)->take(-10);

    DB::select('*')->from('users')->forPage(5, 10);

或者，你也可以使用 ``limit`` 和 ``offset`` 方法：

.. code-block:: php

    <?php
    $users = DB::table('users')
                    ->offset(10)
                    ->limit(5)
                    ->get();


聚合
====
查询构造器还提供了各种聚合方法，例如 ``count`` ， ``max`` ， ``min`` ， ``avg`` ， 和 ``sum`` 。 你可以在查询后调用任何方法：

.. code-block:: php

    <?php
    $users = DB::table('users')->count();

    $price = DB::table('orders')->max('price');

当然。你也可以将这些方法和其他语句结合起来：

.. code-block:: php

    <?php
    $price = DB::table('orders')
                    ->where('finalized', 1)
                    ->avg('price');

确定记录是否存在
----------------
不要使用 ``count`` 方法来确定是否存在与查询相匹配的记录，应该使用 ``exists`` 和 ``doesntExist`` 方法：

.. code-block:: php

    <?php
    return DB::table('orders')->where('finalized', 1)->exists();

    return DB::table('orders')->where('finalized', 1)->doesntExist();

条件语句
========
有时你可能想要子句只适用于某个情况为真时才执行查询。例如，你可能只想给定值在请求中存在的情况下才应用 ``where`` 语句。你可以通过使用 ``when`` 方法：

.. code-block:: php

    <?php
    $role = $request->input('role');

    $users = DB::table('users')
                    ->when($role, function ($query) use ($role) {
                        return $query->where('role_id', $role);
                    })
                    ->get();

``when`` 方法只有在第一个参数为 ``true`` 的时候才执行给定闭包。 如果第一个参数为 ``false`` ，那么这个闭包将不会被执行。

你可以传递另一个闭包作为 ``when`` 方法的第三个参数。该闭包会在第一个参数为 ``false`` 的情况下执行。为了演示这个特性如何使用，我们来配置一个查询的默认排序：

.. code-block:: php

    <?php
    $sortBy = null;

    $users = DB::table('users')
                    ->when($sortBy, function ($query) use ($sortBy) {
                        return $query->orderBy($sortBy);
                    }, function ($query) {
                        return $query->orderBy('name');
                    })
                    ->get();

``when`` 语句可以根据条件来判断是否执行查询条件， ``unless`` 与 ``when`` 相反，第一个参数是 ``false`` 才会调用闭包函数执行查询， ``tap`` 指定 ``when`` 的第一参数永远为真：

.. code-block:: php

    <?php
    $callback = function ($query, $condition) {
        $this->assertEquals($condition, 'truthy');

        $query->where('id', '=', 1);
    };

    $default = function ($query, $condition) {
        $this->assertEquals($condition, 0);

        $query->where('id', '=', 2);
    };

    DB::select('*')->from('users')->when('truthy', $callback, $default)->where('email', 'foo');

    DB::select('*')->from('users')->tap($callback)->where('email', 'foo');

    DB::select('*')->from('users')->unless('truthy', $callback, $default)->where('email', 'foo');

插入
====
查询构造器还提供了 ``insert`` 方法用于插入记录到数据库中。 ``insert`` 方法接收数组形式的字段名和字段值进行插入操作：

.. code-block:: php

    <?php
    DB::table('users')->insert(
        ['email' => 'john@example.com', 'votes' => 0]
    );

你还可以在 ``insert`` 中传入一个嵌套数组向表中插入多条记录。每个数组代表要插入表中的行：

.. code-block:: php

    <?php
    DB::table('users')->insert([
        ['email' => 'taylor@example.com', 'votes' => 0],
        ['email' => 'dayle@example.com', 'votes' => 0]
    ]);

自增 ID
-------
如果数据表有自增 ``ID`` ，使用 ``insertGetId`` 方法来插入记录并返回 ``ID`` 值：

.. code-block:: php

    <?php
    $id = DB::table('users')->insertGetId(
        ['email' => 'john@example.com', 'votes' => 0]
    );

.. note:: 当使用 ``PostgreSQL`` 时， ``insertGetId`` 方法将默认把 ``id`` 作为自动递增字段的名称。若你要从其他「序列」来获取 ``ID`` ，则可以将字段名称作为第二个参数传递给 ``insertGetId`` 方法。

更新
====
当然，除了插入记录到数据库中，查询构造器也可通过 ``update`` 方法更新已有的记录。  ``update`` 方法和 ``insert`` 方法一样，接受包含要更新的字段及值的数组。 你可以通过 ``where`` 子句对 ``update`` 查询进行约束：

.. code-block:: php

    <?php
    DB::table('users')
                ->where('id', 1)
                ->update(['votes' => 1]);

更新 JSON 字段
--------------
更新 ``JSON`` 字段时，你可以使用 ``->`` 语法访问 ``JSON`` 对象上相应的值，该操作只能用于支持 ``JSON`` 字段类型的数据库：

.. code-block:: php

    <?php
    DB::table('users')
                ->where('id', 1)
                ->update(['options->enabled' => true]);

自增与自减
----------
查询构造器还为给定字段的递增或递减提供了方便的方法。 此方法提供了一个比手动编写 ``update`` 语句更具表达力且更精练的接口。

这两个方法都至少接收一个参数：需要修改的列。第二个参数是可选的，用于控制列递增或递减的量。

.. code-block:: php

    <?php
    DB::table('users')->increment('votes');

    DB::table('users')->increment('votes', 5);

    DB::table('users')->decrement('votes');

    DB::table('users')->decrement('votes', 5);

你也可以在操作过程中指定要更新的字段：

.. code-block:: php

    <?php
    DB::table('users')->increment('votes', 1, ['name' => 'John']);

Deletes
=======
查询构造器也可以使用 ``delete`` 方法从数据表中删除记录。在使用 ``delete`` 前，可添加 ``where`` 子句来约束 ``delete`` 语法：

.. code-block:: php

    <?php
    DB::table('users')->delete();

    DB::table('users')->where('votes', '>', 100)->delete();

如果你需要清空表，你可以使用 ``truncate`` 方法，这将删除所有行，并重置自增 ``ID`` 为零：

.. code-block:: php

    <?php
    DB::table('users')->truncate();

悲观锁
======
查询构造器也包含一些可以帮助你在 ``select`` 语法上实现 「悲观锁定」的函数。若想在查询中实现一个「共享锁」，你可以使用 ``sharedLock`` 方法。共享锁可防止选中的数据列被篡改，直到事务被提交为止 ：

.. code-block:: php

    <?php
    DB::table('users')->where('votes', '>', 100)->sharedLock()->get();

另外，你也可以使用 ``lockForUpdate`` 方法。使用「更新」锁可避免行被其它共享锁修改或选取：

.. code-block:: php

    <?php
    DB::table('users')->where('votes', '>', 100)->lockForUpdate()->get();


