简介
====
Laravel 的 ``Eloquent ORM`` 提供了漂亮、简洁的 ``ActiveRecord`` 实现来和数据库交互。每个数据库表都有一个对应的「模型」用来与该表交互。你可以通过模型查询数据表中的数据，并将新记录添加到数据表中。

在开始之前，请确保在 ``config/database.php`` 中配置数据库连接。更多关于数据库的配置信息，请查看 文档。

定义模型
========
首先，创建一个 ``Eloquent`` 模型，生成的模型通常放在 ``app`` 目录中，但你可以通过 ``composer.json`` 文件随意地将它们放在可被自动加载的地方。所有的 ``Eloquent`` 模型都继承了 ``Illuminate\Database\Eloquent\Model`` 类。

创建模型实例的最简单方法是使用 Artisan 命令 ``make:model`` ：

.. code-block:: shell

    php artisan make:model User

如果要在生成模型时生成 数据库迁移 ，可以使用 ``--migration`` 或 ``-m`` 选项：

.. code-block:: shell

    php artisan make:model User --migration

    php artisan make:model User -m

Eloquent 模型约定
-----------------
现在，我们来看一个 ``Flight`` 模型类的例子，我们将会用它从 ``flights`` 数据表中检索和存储信息：

.. code-block:: php

    <?php

    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Flight extends Model
    {
        //
    }

数据表名称
^^^^^^^^^^
请注意，我们并没有告诉 ``Eloquent`` ， ``Flight`` 模型该使用哪一个数据表。除非数据表明确地指定了其它名称，否则将使用类的复数形式「蛇形命名」来作为表名。因此，在这种情况下， ``Eloquent`` 会假定 ``Flight`` 模型存储的是 ``flights`` 数据表中的记录。你可以通过在模型上定义 ``table`` 属性，来指定自定义数据表：

.. code-block:: php

    <?php

    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Flight extends Model
    {
        /**
         * 与模型关联的数据表。
         *
         * @var string
         */
        protected $table = 'my_flights';
    }

主键
^^^^^
``Eloquent`` 也会假定每个数据表都有一个名为 ``id`` 的主键字段。你可以定义一个访问权限为 ``protected`` 的 ``$primaryKey`` 属性来覆盖这个约定。

另外， ``Eloquent`` 假定主键是一个递增的整数值，这意味着在默认情况下主键会自动转换为 ``int`` 。如果希望使用非递增或者非数字的主键，则必须在模型上设置 ``public $incrementing = false`` 。如果主键不是一个整数，你应该在模型上设置 ``protected $keyType = string`` 。

时间戳
^^^^^^^
默认情况下， ``Eloquent`` 会默认数据表中存在 ``created_at`` 和 ``updated_at`` 这两个字段。如果你不需要这两个字段，则需要在模型内将 ``$timestamps`` 属性设置为 ``false`` ：


.. code-block:: php

    <?php

    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Flight extends Model
    {
        /**
         * 该模型是否被自动维护时间戳
         *
         * @var bool
         */
        public $timestamps = false;
    }

如果你需要自定义时间戳格式，可在模型内设置 ``$dateFormat`` 属性。这个属性决定了日期属性应如何存储在数据库中，以及模型被序列化成数组或 ``JSON`` 时的格式：

.. code-block:: php

    <?php

    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Flight extends Model
    {
        /**
         * 模型的日期字段的存储格式
         *
         * @var string
         */
        protected $dateFormat = 'U'; // 这是什么意思？？？
    }

如果你需要自定义用于存储时间戳的字段名，可以在模型中通过设置 ``CREATED_AT`` 和 ``UPDATED_AT`` 常量来实现：

.. code-block:: php

    <?php

    class Flight extends Model
    {
        const CREATED_AT = 'creation_date';
        const UPDATED_AT = 'last_update';
    }

数据库连接
^^^^^^^^^^
默认情况下，所有的模型使用应用配置中的默认数据库连接。如果你想要为模型指定不同的连接，可以通过 ``$connection`` 属性来设置：

.. code-block:: php

    <?php

    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Flight extends Model
    {
        /**
         * 数据模型专属的数据库连接
         *
         * @var string
         */
        protected $connection = 'connection-name';
    }

获取模型
========
创建完模型及其 关联数据表 之后，就可以从数据库中获取数据了。将每个 ``Eloquent`` 模型想像成强大的 查询构造器，你可以使用它来流畅的查询与其关联的数据表。例如：

.. code-block:: php

    <?php

    use App\Flight;

    $flights = App\Flight::all();

    foreach ($flights as $flight) {
        echo $flight->name;
    }

添加其他约束
------------
``Eloquent`` 的 ``all`` 方法会返回模型表中的所有结果。由于每个 ``Eloquent`` 模型都可以当作一个 查询构造器 ，因此你还可以在查询中添加约束，然后使用 ``get`` 来获取结果：

.. code-block:: php

    $flights = App\Flight::where('active', 1)
                   ->orderBy('name', 'desc')
                   ->take(10)
                   ->get();

.. tip::  ``Eloquent`` 模型是查询构造器，因此你应当去阅读 查询构造器 提供的所有方法，以便你可以在 ``Eloquent`` 查询中使用。

集合
----
使用 ``Eloquent`` 中的 ``all`` 和 ``get`` 方法可以检索多个结果，并会返回一个 ``Illuminate\Database\Eloquent\Collection`` 实例。 ``Collection`` 类提供了 很多辅助函数 来处理 ``Eloquent`` 结果。

.. code-block:: php

    <?php
    $flights = $flights->reject(function ($flight) { //如果回调返回 true ，就会把对应的项目从集合中移除
        return $flight->cancelled;
    });

你也可以像数组一样简单地来遍历集合：

.. code-block:: php

    <?php
    foreach ($flights as $flight) {
        echo $flight->name;
    }

分块结果
--------
如果你需要处理数千个 ``Eloquent`` 结果，可以使用 ``chunk`` 命令。 ``chunk`` 方法会检索 ``Eloquent`` 模型的『分块』，将它们提供给指定的 ``Closure`` 进行处理。在处理大型结果集时，使用 ``chunk`` 方法可节省内存：

.. code-block:: php

    <?php
    Flight::chunk(200, function ($flights) {
        foreach ($flights as $flight) {
            //
        }
    });

传递到方法的第一个参数是希望每个『分块』接收的数据量。闭包则作为第二个参数传递，它会在每次执行数据库查询传递每个块时被调用。

使用游标
--------
``cursor`` 方法允许你使用游标来遍历数据库数据，该游标只执行一个查询。处理大量数据时，使用 ``cursor`` 方法可以大幅度减少内存的使用量：

.. code-block:: php

    <?php
    foreach (Flight::where('foo', 'bar')->cursor() as $flight) {
        //
    }

检索单个模型／集合
==================
除了从指定的数据表检索所有记录外，你也可以通过 ``find`` 或 ``first`` 方法来检索单条记录。这些方法不是返回一组模型，而是返回一个模型实例：

.. code-block:: php

    <?php
    // 通过主键取回一个模型...
    $flight = App\Flight::find(1);

    // 取回符合查询限制的第一个模型...
    $flight = App\Flight::where('active', 1)->first();

你也可以使用主键数组作为参数调用 ``find`` 方法，它将返回匹配记录的集合：

.. code-block:: php

    <?php
    $flights = App\Flight::find([1, 2, 3]);

『找不到』异常
--------------
如果你希望在找不到模型时抛出异常，可以使用 ``findOrFail`` 以及 ``firstOrFail`` 方法。这些方法会检索查询的第一个结果，如果没有找到相应的结果，就会抛出一个 ``Illuminate\Database\Eloquent\ModelNotFoundException`` ：

.. code-block:: php

    $model = App\Flight::findOrFail(1);

    $model = App\Flight::where('legs', '>', 100)->firstOrFail();

如果没有对异常进行捕获，则会自动返回 ``404`` 响应给用户。也就是说，在使用这些方法时，不需要另外写个检查来返回 ``404`` 响应：

.. code-block:: php

    <?php
    Route::get('/api/flights/{id}', function ($id) {
        return App\Flight::findOrFail($id);
    });

上面的 find 和 first 方法找不到返回什么？？？？？

检索聚合
--------
你还可以使用 查询构造器 提供的 ``count`` 、 ``sum`` 、 ``max`` 以及其它 聚合函数 。这些方法只会返回适当的标量值而不是整个模型实例：

.. code-block:: php

    <?php
    $count = App\Flight::where('active', 1)->count();

    $max = App\Flight::where('active', 1)->max('price');

插入 & 更新模型
===============
插入
----
要向数据库插入一条记录，先创建模型实例，再设置实例属性，然后调用 ``save`` 方法：

.. code-block:: php

    <?php

    namespace App\Http\Controllers;

    use App\Flight;
    use Illuminate\Http\Request;
    use App\Http\Controllers\Controller;

    class FlightController extends Controller
    {
        /**
         * 创建航班实例
         *
         * @param  Request  $request
         * @return Response
         */
        public function store(Request $request)
        {
            // 表单验证

            $flight = new Flight;

            $flight->name = $request->name;

            $flight->save();
        }
    }

这个例子中， ``HTTP`` 请求的参数 ``name`` 赋值给了 ``App\Flight`` 模型实例的 ``name`` 属性。调用 ``save`` 方法，一条记录就会插入数据库。 ``created_at`` 和 ``updated_at`` 时间戳随着 ``save`` 方法的调用，会自动维护，无需手动操作。

更新
----
``save`` 方法也可用于模型更新。更新模型时，需要检索到它，然后设置模型属性，再调用 ``save`` 方法。同样地， ``updated_at`` 时间戳自动更新，无需手动操作：

.. code-block:: php

    <?php
    $flight = App\Flight::find(1);

    $flight->name = 'New Flight Name';

    $flight->save();

批量更新
^^^^^^^^
也可一并更新查询到的多个模型。这个例子中，所有活动的和目的地为 ``San Diego`` 的航班都被更新为延误：

.. code-block:: php

    <?php
    App\Flight::where('active', 1)
              ->where('destination', 'San Diego')
              ->update(['delayed' => 1]);

``update`` 方法接受一个字段为键、更新数据为值的数组。

.. note:: ``Eloquent`` 的批量更新不会触发 ``saved`` 和 ``updated`` 事件。这是因为批量更新时，从不去检索模型。

批量赋值
--------
你也可以使用 ``create`` 方法来保存新模型， 方法会返回模型实例。不过，在使用之前，你需要先在模型上指定 ``fillable`` 或 ``guarded`` 属性，因为所有的 ``Eloquent`` 模型在默认情况下都不能进行批量赋值。

当用户通过 ``HTTP`` 请求传入了一个意外参数，并且该参数更改了数据库中你不需要更改的字段时，就会发生批量赋值漏洞。例如，恶意用户可能会通过 ``HTTP`` 请求发送 ``is_admin`` 参数，然后将其传递给模型的 ``create`` 方法，此操作能让该用户把自己升级为管理者。

所以，在开始之前，你应该定义好哪些模型属性是可以被批量赋值的。你可以使用模型上的 ``$fillable`` 属性来实现。例如，让 ``Flight`` 模型的 ``name`` 属性可以被批量赋值：

.. code-block:: php

    <?php

    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Flight extends Model
    {
        /**
         * 可以被批量赋值的属性。
         *
         * @var array
         */
        protected $fillable = ['name'];
    }

当我们设置好批量赋值的属性，就可以通过 ``create`` 方法插入新数据。 ``create`` 方法将返回已保存的模型实例：

.. code-block:: php

    <?php
    $flight = App\Flight::create(['name' => 'Flight 10']);

如果你已经有一个模型实例，你可以传递数组给 ``fill`` 方法来赋值：

.. code-block:: php

    <?php
    $flight->fill(['name' => 'Flight 22']);

保护属性
^^^^^^^^
``$fillable`` 可以作为批量赋值的『白名单』，你也可以使用 ``$guarded`` 属性来实现。不同的是， ``$guarded`` 属性包含的是不允许被批量赋值的数组。 也就是说， ``$guarded`` 从功能上讲更像是一个『黑名单』。注意，在使用上，只能是 ``$fillable`` 或 ``$guarded`` 二选一。 下面这个例子中，除了 ``price`` 所有的属性都可以被批量赋值：

.. code-block:: php

    <?php

    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Flight extends Model
    {
        /**
         * 不可被批量赋值的属性。
         *
         * @var array
         */
        protected $guarded = ['price'];
    }

将 ``$guarded`` 定义为空数组，则所有属性都可以被批量赋值。

.. code-block:: php

    <?php
    /**
     * 不可被批量赋值的属性。
     *
     * @var array
     */
    protected $guarded = [];

其它创建方法
------------
firstOrCreate/ firstOrNew
^^^^^^^^^^^^^^^^^^^^^^^^^
这里有两个可能被用的别的方法，你可以通过批量分配属性来创建模型： ``firstOrCreate`` 和 ``firstOrNew`` 。 ``firstOrCreate`` 方法将尝试使用给定的列/值来查找数据库。如果在数据库找不到该模型，则会从第一个参数的属性乃至第二个参数的属性中创建一条记录插入到数据库。这里不是很清楚？？？？

``firstOrNew`` 方法像 ``firstOrCreate`` 方法一样将尝试在与给定属性相匹配的数据库中查找记录。不同的是，如果未找到模型，则会返回一个新的模型实例。注意 ``firstOrNew`` 返回的模型尚未被保存到数据库中。你需要手动调用 ``save`` 方法保存：

.. code-block:: php

    <?php
    // 通过 name 查找航班，不存在则创建...
    $flight = App\Flight::firstOrCreate(['name' => 'Flight 10']);

    // 通过 name 查找航班，不存在则使用 name 和 delayed 属性创建...
    $flight = App\Flight::firstOrCreate(
        ['name' => 'Flight 10'], ['delayed' => 1]
    );

    // 通过 name 查找航班，不存在则创建一个实例...
    $flight = App\Flight::firstOrNew(['name' => 'Flight 10']);

    // 通过 name 查找航班，不存在则使用 name 和 delayed 属性创建一个实例...
    $flight = App\Flight::firstOrNew(
        ['name' => 'Flight 10'], ['delayed' => 1]
    );

updateOrCreate
^^^^^^^^^^^^^^
你还可能遇到希望更新现有模型或在不存在的情况下则创建新的模型的情景。 Laravel 提供了 ``updateOrCreate`` 方法仅一个步骤就可以实现。 跟 ``firstOrCreate`` 方法一样，  ``updateOrCreate`` 持久化模型，所以不需要调用 ``save()`` 方法：

.. code-block:: php

    <?php
    // 如果有从奥克兰到圣地亚哥的航班，则价格定为99美元。
    // 如果没匹配到存在的模型，则创建一个。
    $flight = App\Flight::updateOrCreate(
        ['departure' => 'Oakland', 'destination' => 'San Diego'],
        ['price' => 99]
    );

删除模型
========
可以在模型示例上调用 ``delete`` 方法来删除模型：

.. code-block:: php

    <?php
    $flight = App\Flight::find(1);

    $flight->delete();

通过主键删除模型
----------------
在上面的例子中，在调用 ``delete`` 方法之前先从数据库中检索模型。然而，如果你已经知道了这个模型的主键，则可以删除模型而不检索它。此时，可以调用 ``destroy`` 方法：

.. code-block:: php

    <?php
    App\Flight::destroy(1);

    App\Flight::destroy([1, 2, 3]);

    App\Flight::destroy(1, 2, 3);

通过查询删除模型
----------------
当然，你也可以在模型上运行删除语句。在这个例子中，我们将删除所有标记为非活跃的航班。与批量更新一样，批量删除不会为删除的模型启动任何模型事件：

.. code-block:: php

    <?php
    $deletedRows = App\Flight::where('active', 0)->delete();

.. note:: 通过 ``Eloquent`` 执行批量删除语句时，不会触发 ``deleting`` 和 ``deleted`` 模型事件。这是因为在执行删除语句时，从不检索模型示例。

软删除
------
除了真实删除数据库的记录， ``Eloquent`` 也可以「软删除」模型。软删除的模型并不是真的从数据库里删除了。其实是，模型上设置了 ``deleted_at`` 属性并把其值写入数据库。如果 ``deleted_at`` 值非空，代表已软删除这个模型。要开启模型的软删除功能，可以在模型上使用 ``Illuminate\Database\Eloquent\SoftDeletes`` trait 并把 ``deleted_at`` 字段加入 ``$dates`` 属性：

.. code-block:: php

    <?php

    namespace App;

    use Illuminate\Database\Eloquent\Model;
    use Illuminate\Database\Eloquent\SoftDeletes;

    class Flight extends Model
    {
        use SoftDeletes;

        /**
         * 需要转换成日期的属性
         *
         * @var array
         */
        protected $dates = ['deleted_at'];
    }

当然，需要把 ``deleted_at`` 字段加到数据库表。 Laravel 的 表结构构造器 提供了一个辅助方法来生成这个字段：

.. code-block:: php

    <?php
    Schema::table('flights', function ($table) {
        $table->softDeletes();
    });

现在，模型调用 ``delete`` 方法，当前日期时间会写入 ``deleted_at`` 字段。 **同时，查询出来的结果也会自动剔除软删除的模型。**

使用 ``trashed`` 方法验证当前模型是否软删除：

.. code-block:: php

    <?php
    if ($flight->trashed()) {
        //
    }

查询软删除模型
--------------
包括软删除的模型
^^^^^^^^^^^^^^^^
前边已经提到，查询结果会自动剔除软删除模型。然而，查询中可以使用 ``withTrashed`` 方法获取包括软删除在内的模型：

.. code-block:: php

    <?php
    $flights = App\Flight::withTrashed()
                    ->where('account_id', 1)
                    ->get();

withTrashed 方法也可以用在 关联 查询：

.. code-block:: php

    <?php
    $flight->history()->withTrashed()->get();

检索软删除模型
^^^^^^^^^^^^^^
``onlyTrashed`` 方法只检索软删除的模型：

.. code-block:: php

    <?php
    $flights = App\Flight::onlyTrashed()
                    ->where('airline_id', 1)
                    ->get();

恢复软删除模型
^^^^^^^^^^^^^^
有时会对软删除模型进行「撤销」，在软删除模型实例上使用 ``restore`` 方法即可恢复到有效状态：

.. code-block:: php

    <?php
    $flight->restore();

``restore`` 方法可在查询上使用，从而快速恢复多个模型。和其他「批量」操作一样，这个操作不会触发模型的任何事件：

.. code-block:: php

    <?php
    App\Flight::withTrashed()
            ->where('airline_id', 1)
            ->restore();

类似 ``withTrashed`` 方法，关联 也可使用 ``restore`` ：

.. code-block:: php

    <?php
    $flight->history()->restore();

永久删除
^^^^^^^^
要真实删除数据时，使用 ``forceDelete`` 方法即可把软删除模型从数据里永久删除：

.. code-block:: php

    <?php
    // 单个模型实例的永久删除
    $flight->forceDelete();

    // 关联模型的永久删除
    $flight->history()->forceDelete();

查询作用域
==========
全局作用域
----------
全局作用域可以给模型的查询都添加上约束。Laravel 的 软删除 功能就是利用了「未删除」的全局作用域。自定义全局作用域使用起来更方便、简单，同时保证了每一个模型的查询都受到特定约束。

编写全局作用域
^^^^^^^^^^^^^^
编写全局作用域非常简单。定义一个实现 ``Illuminate\Database\Eloquent\Scope`` 接口的类，并实现 ``apply`` 这一方法。 根据需求，在 ``apply`` 加入查询的 ``where`` 约束：

.. code-block:: php

    <?php

    namespace App\Scopes;

    use Illuminate\Database\Eloquent\Scope;
    use Illuminate\Database\Eloquent\Model;
    use Illuminate\Database\Eloquent\Builder;

    class AgeScope implements Scope
    {
        /**
         * 把约束加到 Eloquent 查询构造中.
         *
         * @param  \Illuminate\Database\Eloquent\Builder  $builder
         * @param  \Illuminate\Database\Eloquent\Model  $model
         * @return void
         */
        public function apply(Builder $builder, Model $model)
        {
            $builder->where('age', '>', 200);
        }
    }

.. tip:: 如果需要在 ``select`` 语句里添加字段，应使用 ``addSelect`` 方法，而不是 ``select`` 方法。 这将有效防止无意中替换现有 ``select`` 语句的情况。

应用全局作用域
^^^^^^^^^^^^^^
重写 ``boot`` 方法并使用 ``addGlobalScope`` 方法即可应用全局作用域到模型：

.. code-block:: php

    <?php
    namespace App;

    use App\Scopes\AgeScope;
    use Illuminate\Database\Eloquent\Model;

    class User extends Model
    {
        /**
         * 模型的「启动」方法
         *
         * @return void
         */
        protected static function boot()
        {
            parent::boot();

            static::addGlobalScope(new AgeScope);
        }
    }

添加作用域后， ``User::all()`` 的查询语句如下：

.. code-block:: sql

    select * from `users` where `age` > 200

匿名的全局作用域
^^^^^^^^^^^^^^^^
``Eloquent`` 也可用闭包定义全局作用域，这样就不必要再为一个简单的作用域而编写一个完整的类：

.. code-block:: php

    <?php

    namespace App;

    use Illuminate\Database\Eloquent\Model;
    use Illuminate\Database\Eloquent\Builder;

    class User extends Model
    {
        /**
         * 模型的「启动」方法
         *
         * @return void
         */
        protected static function boot()
        {
            parent::boot();

            static::addGlobalScope('age', function (Builder $builder) { // 第一个参数是命名，可以随便取名
                $builder->where('age', '>', 200);
            });
        }
    }

取消全局作用域
^^^^^^^^^^^^^^
使用 ``withoutGlobalScope`` 方法可以取消当前查询的全局作用域。这个方法接受一个全局作用域的类名作为它唯一的参数：

.. code-block:: php

    <?php
    User::withoutGlobalScope(AgeScope::class)->get();

全局作用域是闭包的话：

.. code-block:: php

    <?php
    User::withoutGlobalScope('age')->get();

``withoutGlobalScopes`` 方法可以取消多个甚至全部的全局作用域：

.. code-block:: php

    <?php
    // 取消全部作用域
    User::withoutGlobalScopes()->get();

    // 取消个别全局作用域
    User::withoutGlobalScopes([
        FirstScope::class, SecondScope::class
    ])->get();

本地范围
--------
本地范围允许定义通用的约束集合以便在应用中复用。 例如， 你可能经常需要获取「受欢迎的」用户。要定义这样一个范围，只需要在对应的 ``Eloquent`` 模型方法前加入 ``scope`` 前缀。

作用域总是返回一个查询构造器实例：

.. code-block:: php

    <?php

    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class User extends Model
    {
        /**
         * 只查询受欢迎的用户.
         *
         * @param \Illuminate\Database\Eloquent\Builder $query
         * @return \Illuminate\Database\Eloquent\Builder
         */
        public function scopePopular($query)
        {
            return $query->where('votes', '>', 100);
        }

        /**
         * 只查询 active 的用户.
         *
         * @param \Illuminate\Database\Eloquent\Builder $query
         * @return \Illuminate\Database\Eloquent\Builder
         */
        public function scopeActive($query)
        {
            return $query->where('active', 1);
        }
    }

利用本地范围
^^^^^^^^^^^^
一旦定义范围。就可以在模型查询的时候调用范围方法。在方法调用时你不需要添加 ``scope`` 前缀。你甚至可以链式调用不同的范围。例如：

.. code-block:: php

    <?php
    $users = App\User::popular()->active()->orderBy('created_at')->get();

动态范围
^^^^^^^^^
有时，你可能希望定义一个可接受参数的范围。这时只需给你的范围添加额外的参数即可。 范围参数应该定义在 ``$query`` 参数后：

.. code-block:: php

    <?php

    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class User extends Model
    {
        /**
         * 查询只包含特定类型的用户.
         *
         * @param \Illuminate\Database\Eloquent\Builder $query
         * @param mixed $type
         * @return \Illuminate\Database\Eloquent\Builder
         */
        public function scopeOfType($query, $type)
        {
            return $query->where('type', $type);
        }
    }

现在，你可以在调用作用域时传递参数：

.. code-block:: php

    <?php
    $users = App\User::ofType('admin')->get();

问题？全局范围是否可以传入参数？？？？

事件
====
``Eloquent`` 模型会触发许多事件，让你在模型的生命周期的多个时间点进行监控： ``retrieved`` ,  ``creating`` , ``created`` , ``updating`` , ``updated`` , ``saving`` , ``saved`` , ``deleting`` , ``deleted`` , ``restoring`` , ``restored`` 。 事件让你在每当有特定模型类进行数据库保存或更新时，执行代码。

- 当从数据库检索现有模型时，会触发 ``retrieved`` 事件。
- 当模型第一次被保存时， ``creating`` 和 ``created`` 事件会被触发。
- 若数据库中已存在此模型，并调用 ``save`` 方法， ``updating`` / ``updated`` 事件会被触发。
- 前面这两种情况下， ``saving`` / ``saved`` 事件都会被触发。

开始前，先在你的 ``Eloquent`` 模型上定义一个 ``$dispatchesEvents`` 属性，将 ``Eloquent`` 模型的生命周期的多个点映射到你的 事件类：

.. code-block:: php

    <?php

    namespace App;

    use App\Events\UserSaved;
    use App\Events\UserDeleted;
    use Illuminate\Notifications\Notifiable;
    use Illuminate\Foundation\Auth\User as Authenticatable;

    class User extends Authenticatable
    {
        use Notifiable;

        /**
         * 此模型的事件映射.
         *
         * @var array
         */
        protected $dispatchesEvents = [
            'saved' => UserSaved::class,
            'deleted' => UserDeleted::class,
        ];
    }

观察者
------
如果你在一个给定模型中监听许多事件，你可以使用观察者将所有的监听添加到一个类。观察者类里的方法名应该反映 ``Eloquent`` 想监听的事件。 每个方法接受 ``model`` 作为唯一参数。 Laravel 不包含观察者的默认目录，所以你可以创建任何你喜欢的目录来存放观察者类：

.. code-block:: php

    <?php

    namespace App\Observers;

    use App\User;

    class UserObserver
    {
        /**
         * 监听创建用户事件.
         *
         * @param  \App\User  $user
         * @return void
         */
        public function created(User $user)
        {
            //
        }

        /**
         * 监听删除用户事件.
         *
         * @param  \App\User  $user
         * @return void
         */
        public function deleting(User $user)
        {
            //
        }
    }

要注册一个观察者，需要用模型中的 ``observe`` 方法去观察。 你可以在你得服务提供者之一的 ``boot`` 方法中注册观察者。在这个例子中，我们将在 ``AppServiceProvider`` 中注册观察者：

.. code-block:: php

    <?php
    namespace App\Providers;

    use App\User;
    use App\Observers\UserObserver;
    use Illuminate\Support\ServiceProvider;

    class AppServiceProvider extends ServiceProvider
    {
        /**
         * 运行所有应用服务.
         *
         * @return void
         */
        public function boot()
        {
            User::observe(UserObserver::class);
        }

        /**
         * 注册服务提供.
         *
         * @return void
         */
        public function register()
        {
            //
        }
    }
