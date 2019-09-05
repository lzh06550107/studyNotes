****
集合
****

`可移植包下载 <https://github.com/tightenco/collect>`_ 

Collection 类
=============
``Illuminate\Support\Collection`` 提供了一个更具可读性的、更便于处理数组数据的封装。

该 ``Collection`` 类实现了一些 ``PHP`` 和 ``Laravel`` 接口:

- ``ArrayAccess`` :提供对象作为数组访问的接口；
- ``IteratorAggregate`` :创建一个外部迭代器的接口；
- ``JsonSerializable``

其它实现的接口 `参考 <https://laravel.com/api/5.2/Illuminate/Support/Collection.html>`_ 

具体例子看下面的代码。我们使用了 ``collect`` 函数从数组中创建新的集合实例，对其中的每个元素运行 ``strtoupper`` 函数之后再移除所有的空元素：

.. code-block:: php

    <?php
    $collection = collect(['taylor', 'abigail', null])->map(function ($name) {
        return strtoupper($name);
    })->reject(function ($name) {
        return empty($name);
    });

正如你看到的， ``Collection`` 类允许你链式调用其方法，以达到在底层数组上优雅地执行 ``map`` 和 ``reject`` 操作。一般来说，集合是不可改变的，这意味着每个 ``Collection`` 方法都会返回一个全新的 ``Collection`` 实例。

创建一个新的集合
================
使用帮助函数和构造函数
----------------------
可以使用 ``collect()`` 帮助函数从一个数组创建一个集合或者通过实例化 ``Collection`` 来创建。

.. code-block:: php

    <?php
    $newCollection = collect([1, 2, 3, 4, 5]);

    $newCollection = new Collection([1, 2, 3, 4, 5]);

使用wrap
--------
静态 ``wrap`` 方法将可用的给定值包装在集合中：

.. code-block:: php

    <?php
    $collection = Collection::wrap('John Doe');

    $collection->all();

    // ['John Doe']

    $collection = Collection::wrap(['John Doe']);

    $collection->all();

    // ['John Doe']

    $collection = Collection::wrap(collect('John Doe'));

    $collection->all();

    // ['John Doe']

使用times
---------
静态 ``times`` 方法通过回调在给定次数内创建一个新的集合：

.. code-block:: php

    <?php
    $collection = Collection::times(10, function ($number) {
        return $number * 9;
    });

    $collection->all();

    // [9, 18, 27, 36, 45, 54, 63, 72, 81, 90]

Eloquent ORM集合
================
``Laravel Eloquent ORM`` 同样返回集合类型数据。

.. code-block:: php

    <?php
     public function getUsers() {
        $users = User::all(); //返回User对象集合
        dd($users);
    }

创建演示用的样例集合
====================
在接下来的几节中，我将使用用户表数据集和一些自定义集合中的以下数据集进行演示。

.. code-block:: php

    <?php
    Array
    (
        [0] => Array
            (
                [id] => 1
                [name] => Chasity Tillman
                [email] => qleuschke@example.org
                [age] => 51
                [created_at] => 2016-06-07 15:50:50
                [updated_at] => 2016-06-07 15:50:50
            )
        ...
    )

可用的方法
==========
这个文档接下来的内容，我们会探讨 ``Collection`` 类每个可用的方法。记住，所有方法都可以以方法链的形式优雅地操纵数组。而且，几乎所有的方法都会返回新的 ``Collection`` 实例，允许你在必要时保存集合的原始副本。

查找过滤数据
------------
where类
^^^^^^^
where
"""""
你可以通过一个给定键值对，使用 ``where`` 方法从集合中搜索数据。支持链式 ``where()`` 方法。

.. code-block:: php

    <?php
        $users = User::all();
        $user = $users->where('id', 2);
        //Collection of user with an ID of 2

        $user = $users->where('id', 1)
                      ->where('age', '51')
                      ->where('name', 'Chasity Tillman');

        //collection of user with an id of 1, age 51
        //and named Chasity Tillman

        $collection = collect([
            ['product' => 'Desk', 'price' => 200],
            ['product' => 'Chair', 'price' => 100],
            ['product' => 'Bookcase', 'price' => 150],
            ['product' => 'Door', 'price' => 100],
        ]);

        $filtered = $collection->where('price', 100);

        $filtered->all();

        /*
            [
                ['product' => 'Chair', 'price' => 100],
                ['product' => 'Door', 'price' => 100],
            ]
        */

比较数值的时候， ``where`` 方法使用「宽松」比较，意味着具有整数值的字符串将被认为等于相同值的整数。使用 ``whereStrict`` 方法来进行「严格」比较过滤。

whereStrict
"""""""""""
这个方法与 ``where`` 方法一样；但是会以「严格」比较来匹配所有值。

whereIn
"""""""
``whereIn`` 方法通过给定的键和可选值数组来过滤集合：

.. code-block:: php

    <?php
    $collection = collect([
        ['product' => 'Desk', 'price' => 200],
        ['product' => 'Chair', 'price' => 100],
        ['product' => 'Bookcase', 'price' => 150],
        ['product' => 'Door', 'price' => 100],
    ]);

    $filtered = $collection->whereIn('price', [150, 200]);

    $filtered->all();

    /*
        [
            ['product' => 'Bookcase', 'price' => 150],
            ['product' => 'Desk', 'price' => 200],
        ]
    */

``whereIn`` 方法在检查项目值时使用「宽松」比较，意味着具有整数值的字符串将被视为等于相同值的整数。你可以使用 ``whereInStrict`` 做「严格」比较。

whereInStrict
"""""""""""""
此方法的使用和 ``whereIn`` 方法类似，只是使用了「严格」比较来匹配所有值。

whereNotIn
""""""""""
``whereNotIn`` 通过集合中不包含的给定键值对进行：

.. code-block:: php

    <?php
    $collection = collect([
        ['product' => 'Desk', 'price' => 200],
        ['product' => 'Chair', 'price' => 100],
        ['product' => 'Bookcase', 'price' => 150],
        ['product' => 'Door', 'price' => 100],
    ]);

    $filtered = $collection->whereNotIn('price', [150, 200]);

    $filtered->all();

    /*
        [
            ['product' => 'Chair', 'price' => 100],
            ['product' => 'Door', 'price' => 100],
        ]
    */

``whereNotIn`` 方法在检查项目值时使用「宽松」比较，意味着具有整数值的字符串将被视为等于相同值的整数。你可以使用 ``whereNotInStrict`` 做比较 严格 的匹配。

whereNotInStrict
""""""""""""""""
此方法的使用和 ``whereNotIn`` 方法类似，只是使用了「严格」比较来匹配所有值。

first
"""""
``first`` 方法返回集合中通过给定真实测试的第一个元素：

.. code-block:: php

    <?php
    collect([1, 2, 3, 4])->first(function ($value, $key) {
        return $value > 2;
    });

    // 3

你也可以不传入参数使用 ``first`` 方法以获取集合中第一个元素。如果集合是空的，则会返回 ``null`` ：

.. code-block:: php

    <?php
    collect([1, 2, 3, 4])->first();

    // 1

firstWhere
""""""""""


search
^^^^^^^
``search`` 方法搜索给定的值并返回它的键。如果找不到，则返回 ``false`` ：

.. code-block:: php

    <?php
    $collection = collect([2, 4, 6, 8]);

    $collection->search(4);

    // 1

搜索使用「宽松」比较完成，这意味着具有整数值的字符串会被认为等于相同值的整数。要使用「严格」比较，就传入 true 作为该方法的第二个参数：

.. code-block:: php

    <?php
    $collection->search('4', true);

    // false

另外，你可以通过回调来搜索第一个通过真实测试的项目：

.. code-block:: php

    <?php
    $collection->search(function ($item, $key) {
        return $item > 5;
    });

    // 2

filter
^^^^^^
使用 ``filter()`` 方法来过滤集合。需要传入一个回调函数来过滤。

.. code-block:: php

        $users = User::all();
        $youngsters = $users->filter(function ($value, $key) {
            return $value->age < 35;
        });

        $youngsters->all(); // 返回数组
        //list of all users that are below the age of 35

回调函数接受键和值作为参数。

如果没有提供回调函数，集合中所有返回 ``false`` 的元素都会被移除：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, null, false, '', 0, []]);

    $collection->filter()->all();

    // [1, 2, 3]

与 ``filter`` 相反的方法，可以查看 `reject`_ 。

reject
^^^^^^
``reject`` 方法使用指定的回调过滤集合。如果回调返回 ``true`` ，就会把对应的项目从集合中移除：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4]);

    $filtered = $collection->reject(function ($value, $key) {
        return $value > 2;
    });

    $filtered->all();

    // [1, 2]

与 ``reject`` 相反的方法，查看 `filter`_ 。

last
^^^^^
``last`` 方法返回集合中通过给定测试的最后一个元素：

.. code-block:: php

    <?php
    collect([1, 2, 3, 4])->last(function ($value, $key) {
        return $value < 3;
    });

    // 2

你也可以不传入参数调用 ``last`` 方法来获取集合中最后一个元素。如果集合是空的，返回 ``null`` ：

.. code-block:: php

    <?php
    collect([1, 2, 3, 4])->last();

    // 4

判断类
------

contains
^^^^^^^^
``contains`` 方法判断集合是否包含给定的项目：

.. code-block:: php

    <?php
    $collection = collect(['name' => 'Desk', 'price' => 100]);

    $collection->contains('Desk');

    // true

    $collection->contains('New York');

    // false

你也可以用 ``contains`` 方法匹配一对键/值，即判断给定的配对是否存在于集合中：

.. code-block:: php

    <?php
    $collection = collect([
        ['product' => 'Desk', 'price' => 200],
        ['product' => 'Chair', 'price' => 100],
    ]);

    $collection->contains('product', 'Bookcase');

    // false

最后，你也可以传递一个回调到 ``contains`` 方法来执行自己的测试：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5]);

    $collection->contains(function ($value, $key) {
        return $value > 5;
    });

    // false

``contains`` 方法在检查项目值时使用「宽松」比较，意味着具有整数值的字符串将被视为等于相同值的整数。 相反 ``containsStrict`` 方法则是使用「严格」比较进行过滤。


containsStrict
""""""""""""""
此方法和 ``contains`` 方法类似，但是它却是使用了「严格」比较来比较所有值。

has
^^^^
``has`` 方法判断集合中是否存在给定的键：

.. code-block:: php

    <?php
    $collection = collect(['account_id' => 1, 'product' => 'Desk']);

    $collection->has('product');

    // true

every
^^^^^
``every`` 方法可用于验证集合中每一个元素都通过给定的测试：

.. code-block:: php

    <?php
    collect([1, 2, 3, 4])->every(function ($value, $key) {
        return $value > 2;
    });

    // false

isEmpty
^^^^^^^
如果集合是空的， ``isEmpty`` 方法返回 ``true`` ，否则返回 ``false`` ：

.. code-block:: php

    <?php
    collect([])->isEmpty();

    // true

isNotEmpty
^^^^^^^^^^
如果集合不是空的， ``isNotEmpty`` 方法会返回 ``true`` ：否则返回 ``false`` ：

.. code-block:: php

    <?php
    collect([])->isNotEmpty();

    // false

排序类
------

sort
^^^^^
``sort`` 方法对集合进行排序。排序后的集合保留着原数组的键，所以在这个例子中我们使用 ``values`` 方法来把键重置为连续编号的索引。

.. code-block:: php

    <?php
    $collection = collect([5, 3, 1, 2, 4]);

    $sorted = $collection->sort();

    $sorted->values()->all();

    // [1, 2, 3, 4, 5]

如果你有更高级的排序需求，你可以传入回调来用你自己的算法进行排序。请参阅 ``PHP`` 文档的 ``usort`` ，这是集合的 ``sort`` 方法在底层所调用的。

.. note:: 如果要对嵌套数组或对象的集合进行排序，参考 ``sortBy`` 和 ``sortByDesc`` 方法。

sortBy
^^^^^^
``sortBy`` 方法以给定的键对集合进行排序。排序后的集合保留了原数组键，所以在这个例子中，我们使用 ``values`` 方法将键重置为连续编号的索引：

.. code-block:: php

    <?php
    $collection = collect([
        ['name' => 'Desk', 'price' => 200],
        ['name' => 'Chair', 'price' => 100],
        ['name' => 'Bookcase', 'price' => 150],
    ]);

    $sorted = $collection->sortBy('price');

    $sorted->values()->all();

    /*
        [
            ['name' => 'Chair', 'price' => 100],
            ['name' => 'Bookcase', 'price' => 150],
            ['name' => 'Desk', 'price' => 200],
        ]
    */

你还可以传入自己的回调以决定如何对集合的值进行排序：

.. code-block:: php

    <?php
    $collection = collect([
        ['name' => 'Desk', 'colors' => ['Black', 'Mahogany']],
        ['name' => 'Chair', 'colors' => ['Black']],
        ['name' => 'Bookcase', 'colors' => ['Red', 'Beige', 'Brown']],
    ]);

    $sorted = $collection->sortBy(function ($product, $key) {
        return count($product['colors']);
    });

    $sorted->values()->all();

    /*
        [
            ['name' => 'Chair', 'colors' => ['Black']],
            ['name' => 'Desk', 'colors' => ['Black', 'Mahogany']],
            ['name' => 'Bookcase', 'colors' => ['Red', 'Beige', 'Brown']],
        ]
    */

sortByDesc
^^^^^^^^^^^
这个方法与 ``sortBy`` 方法一样，但是会以相反的顺序来对集合进行排序。

排序方法接受一个键或者回调函数作为参数来排序集合。

.. code-block:: php

    <?php
        $users  = User::all();

        $youngestToOldest = $users->sortBy('age');
        $youngestToOldest->all();
        //list of all users from youngest to oldest

        $movies = collect([
            [
                'name' => 'Back To The Future',
                'releases' => [1985, 1989, 1990]
            ],
            [
                'name' => 'Fast and Furious',
                'releases' => [2001, 2003, 2006, 2009, 2011, 2013, 2015, 2017]
            ],
            [
                'name' => 'Speed',
                'releases' => [1994]
            ]
        ]);

        $mostReleases = $movies->sortByDesc(function ($movie, $key) {
            return count($movie['releases']);
        });

        $mostReleases->toArray();
        //list of movies in descending order of most releases.

        dd($mostReleases->values()->toArray());
        /*
            list of movies in descending order of most releases
            but with the key values reset
        */

排序方法为每个值维护键。 虽然这可能对您的应用程序很重要，但可以通过链式 ``values()`` 方法将它们重置为默认的基于零的增量值。

和往常一样，我还引入了一个新的集合方法 ``toArray()`` ，它将集合简单地转换为数组。

reverse
^^^^^^^
``reverse`` 方法倒转集合中项目的顺序：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5]);

    $reversed = $collection->reverse();

    $reversed->all();

    // [5, 4, 3, 2, 1]

重组类
------
拆分集合
^^^^^^^^
chunk
"""""
``chunk`` 方法将集合拆成多个指定大小的小集合：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5, 6, 7]);

    $chunks = $collection->chunk(4);

    $chunks->toArray();

    // [[1, 2, 3, 4], [5, 6, 7]]

partition
""""""""""
``partition`` 方法可以和 ``PHP`` 中的 ``list`` 方法结合使用，来分开满足指定条件的元素以及那些不满足指定条件的元素：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5, 6]);

    list($underThree, $aboveThree) = $collection->partition(function ($i) {
        return $i < 3;
    });

groupBy
"""""""
分组集合有助于组织数据。 ``groupBy`` 方法采用键或回调函数，并根据键值或返回的回调值返回分组集合。

.. code-block:: php

    <?php
        $movies = collect([
            ['name' => 'Back To the Future', 'genre' => 'scifi', 'rating' => 8],
            ['name' => 'The Matrix',  'genre' => 'fantasy', 'rating' => 9],
            ['name' => 'The Croods',  'genre' => 'animation', 'rating' => 8],
            ['name' => 'Zootopia',  'genre' => 'animation', 'rating' => 4],
            ['name' => 'The Jungle Book',  'genre' => 'fantasy', 'rating' => 5],
        ]);

        $genre = $movies->groupBy('genre');
        /*
        [
             "scifi" => [
               ["name" => "Back To the Future", "genre" => "scifi", "rating" => 8,],
             ],
             "fantasy" => [
               ["name" => "The Matrix", "genre" => "fantasy", "rating" => 9,],
               ["name" => "The Jungle Book", "genre" => "fantasy", "rating" => 5, ],
             ],
             "animation" => [
               ["name" => "The Croods", "genre" => "animation", "rating" => 8,],
               ["name" => "Zootopia", "genre" => "animation", "rating" => 4, ],
             ],
        ]
        */

        $rating = $movies->groupBy(function ($movie, $key) {
            return $movie['rating'];
        });

        /*
        [
           8 => [
             ["name" => "Back To the Future", "genre" => "scifi", "rating" => 8,],
             ["name" => "The Croods", "genre" => "animation", "rating" => 8,],
           ],
           9 => [
             ["name" => "The Matrix", "genre" => "fantasy", "rating" => 9,],
           ],
           4 => [
             ["name" => "Zootopia","genre" => "animation", "rating" => 4,],
           ],
           5 => [
             ["name" => "The Jungle Book","genre" => "fantasy","rating" => 5,],
           ],
        ]
       */

迭代集合
^^^^^^^^
each
""""
``each`` 方法将迭代集合中的内容并将其传递到回调函数中：

.. code-block:: php

    <?php
    $collection = $collection->each(function ($item, $key) {
        //
    });

如果你想要中断对内容的迭代，那就从回调中返回 ``false`` ：

.. code-block:: php

    <?php
    $collection = $collection->each(function ($item, $key) {
        if (/* some condition */) {
            return false;
        }
    });

eachSpread
""""""""""
``eachSpread`` 方法迭代集合的项目，将每个嵌套项目值传递给给定的回调：

.. code-block:: php

    <?php
    $collection = collect([['John Doe', 35], ['Jane Doe', 33]]);

    $collection->eachSpread(function ($name, $age) {
        //
    });

您可以通过从回调中返回 ``false`` 来停止迭代项目：

.. code-block:: php

    <?php
    $collection->eachSpread(function ($name, $age) {
        return false;
    });

transform
""""""""""
``transform`` 方法迭代集合并对集合内的每个项目调用给定的回调。而集合的内容也会被回调返回的值取代：

**该方法会修改集合本身的值。**

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5]);
    // 回调函数传入项目和对应的键
    $collection->transform(function ($item, $key) {
        return $item * 2;
    });

    $collection->all();

    // [2, 4, 6, 8, 10]

.. note:: 与大多数集合的方法不同， ``transform`` 会修改集合本身。如果你想创建新的集合，就改用 ``map`` 方法。

map类
"""""
map
+++
``map`` 方法遍历集合并将每一个值传入给定的回调。该回调可以任意修改项目并返回，从而形成新的被修改过项目的集合：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5]);

    $multiplied = $collection->map(function ($item, $key) {
        return $item * 2;
    });

    $multiplied->all();

    // [2, 4, 6, 8, 10]

.. note:: 像其它的集合方法一样， ``map`` 方法会返回一个新的集合实例；它不会修改它所调用的集合。如果你想改变原集合，请使用 ``transform`` 方法。

mapSpread
+++++++++
``mapSpread`` 方法可以迭代集合的项目(就是说，里面的项目是集合)，将每个嵌套项目值传递给给定的回调函数。 该回调可以自由修改该项目并将其返回，从而形成一个新的修改项目集合：

.. code-block:: php

    <?php
    $chunks = $collection->chunk(2);
    // 回调函数的参数是当前块内的元素以及当前块索引值
    $sequence = $chunks->mapSpread(function ($odd, $even) {
        $parms = func_get_args(); // 参数数组
        $numargs  =  func_num_args (); // 参数个数
        $key = func_get_arg($numargs-1); // 获取当前块的索引值
        $chunk = array_slice($parms, 0, $numargs-1);
        echo 'chunk:'.implode(",", $chunk).'，index:'.$key.PHP_EOL;
        return $odd + $even;
    });

    $sequence->all(); // // [1, 5, 9, 13, 17]

mapToDictionary
+++++++++++++++
``mapToDictionary`` 方法通过给定的回调对集合的项目进行分组。 该回调应该返回一个包含单个键/值对的关联数组，从而形成一个新的集合。这个和 ``mapToGroups`` 区别是，该函数返回的集合内部项是数组对象，而不是集合对象。如果需要集合对象请使用 ``mapToGroups`` 函数。

.. code-block:: php

    <?php
    $collection = collect([
        [
            'name' => 'John Doe',
            'department' => 'Sales',
        ],
        [
            'name' => 'Jane Doe',
            'department' => 'Sales',
        ],
        [
            'name' => 'Johnny Doe',
            'department' => 'Marketing',
        ]
    ]);
    // 回调函数传入集合项，和当前集合项的索引
    $grouped = $collection->mapToDictionary(function ($item, $key) {
        return [$item['department'] => $item['name']];
    });

    $grouped->toArray(); // 转换为数组

    /*
        [
            'Sales' => ['John Doe', 'Jane Doe'],
            'Marketing' => ['Johhny Doe'],
        ]
    */

    $item = $grouped->get('Sales') // ['John Doe', 'Jane Doe']

    $item->all(); // 因为这里get()方法返回的对象是数组，不是集合对象，所以不能调用all()方法

mapToGroups
+++++++++++
``mapToGroups`` 方法通过给定的回调对集合的项目进行分组。 该回调应该返回一个包含单个键/值对的关联数组，从而形成一个新的集合：

.. code-block:: php

    <?php
    $collection = collect([
        [
            'name' => 'John Doe',
            'department' => 'Sales',
        ],
        [
            'name' => 'Jane Doe',
            'department' => 'Sales',
        ],
        [
            'name' => 'Johnny Doe',
            'department' => 'Marketing',
        ]
    ]);
    // 回调函数传入集合项，和当前集合项的索引
    $grouped = $collection->mapToGroups(function ($item, $key) {
        return [$item['department'] => $item['name']];
    });

    $grouped->toArray(); // 转换为数组

    /*
        [
            'Sales' => ['John Doe', 'Jane Doe'],
            'Marketing' => ['Johhny Doe'],
        ]
    */

    $grouped->get('Sales')->all(); // 获取指定键的值，并转换集合为数组

    // ['John Doe', 'Jane Doe']

mapWithKeys
+++++++++++
``mapWithKeys`` 方法遍历集合并将每个值传入给定的回调。回调应该返回包含一个键值对的关联数组：

.. code-block:: php

    <?php
    $collection = collect([
        [
            'name' => 'John',
            'department' => 'Sales',
            'email' => 'john@example.com'
        ],
        [
            'name' => 'Jane',
            'department' => 'Marketing',
            'email' => 'jane@example.com'
        ]
    ]);

    // 回调函数接受项目值和项目索引，返回键值对
    $keyed = $collection->mapWithKeys(function ($item, $key) {
        return [$item['email'] => $item['name']];
    });

    $keyed->all();

    /*
        [
            'john@example.com' => 'John',
            'jane@example.com' => 'Jane',
        ]
    */

flatMap
+++++++
``flatMap`` 方法遍历集合并将其中的每个值传递到给定的回调。可以通过回调修改每个值的内容再返回出来，从而形成一个新的被修改过内容的集合。然后你就可以用 ``all()`` 打印修改后的数组：

.. code-block:: php

    <?php
    $collection = collect([
        ['name' => 'Sally'],
        ['school' => 'Arkansas'],
        ['age' => 28]
    ]);

    // 回调函数传入对应的项的值和键
    $flattened = $collection->flatMap(function ($values) {
        return array_map('strtoupper', $values); // 对values中的值进行大写操作
    });

    $flattened->all();

    // ['name' => 'SALLY', 'school' => 'ARKANSAS', 'age' => '28'];

flip
++++
``flip`` 方法将集合中的键和对应的数值进行互换：

.. code-block:: php

    <?php
    $collection = collect(['name' => 'taylor', 'framework' => 'laravel']);

    $flipped = $collection->flip();

    $flipped->all();

    // ['taylor' => 'name', 'laravel' => 'framework']

mapInto
+++++++
``mapInto()`` 方法可以迭代集合，通过将值传递给构造函数来创建给定类的新实例：

.. code-block:: php

    <?php
    class Currency
    {
        /**
         * Create a new currency instance.
         *
         * @param  string  $code
         * @return void
         */
        function __construct(string $code)
        {
            $this->code = $code;
        }
    }

    $collection = collect(['USD', 'EUR', 'GBP']);

    $currencies = $collection->mapInto(Currency::class);

    $currencies->all();

    // [Currency('USD'), Currency('EUR'), Currency('GBP')]

reduce
""""""
``reduce`` 方法将每次迭代的结果传递给下一次迭代直到集合减少为单个值：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3]);

    // 回调函数第一个参数为上一次计算结果，第一次该值为初始化值，第二个参数为当前迭代项
    $total = $collection->reduce(function ($carry, $item) {
        $parms = func_get_args(); // 参数数组
        return $carry + $item;
    }, 10); // 可以传入初始化值

    // 16

pipe
""""
``pipe`` 方法将集合传给给定的回调(作为回调的参数)并返回结果：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3]);

    $piped = $collection->pipe(function ($collection) {
        return $collection->sum();
    });

    // 6

flatten
"""""""
``flatten`` 方法将多维集合转为一维的:

.. code-block:: php

    <?php
    $collection = collect(['name' => 'taylor', 'languages' => ['php', 'javascript']]);

    $flattened = $collection->flatten();

    $flattened->all();

    // ['taylor', 'php', 'javascript'];

你还可以选择性地传入「深度」参数：

.. code-block:: php

    <?php
    $collection = collect([
        'Apple' => [
            ['name' => 'iPhone 6S', 'brand' => 'Apple'],
        ],
        'Samsung' => [
            ['name' => 'Galaxy S7', 'brand' => 'Samsung']
        ],
    ]);

    $products = $collection->flatten(1);

    $products->values()->all();

    /*
        [
            ['name' => 'iPhone 6S', 'brand' => 'Apple'],
            ['name' => 'Galaxy S7', 'brand' => 'Samsung'],
        ]
    */

在这个例子里，调用 ``flatten`` 方法时不传入深度参数的话也会将嵌套数组转成一维的，然后返回 ``['iPhone 6S', 'Apple', 'Galaxy S7', 'Samsung']`` 。传入深度参数能让你限制设置返回数组的层数。

合并集合
^^^^^^^

collapse
""""""""
``collapse`` 方法将多个数组的集合合并成一个数组的集合：

.. code-block:: php

    <?php
    $collection = collect([[1, 2, 3], [4, 5, 6], [7, 8, 9]]);

    $collapsed = $collection->collapse();

    $collapsed->all();

    // [1, 2, 3, 4, 5, 6, 7, 8, 9]

combine
"""""""
``combine`` 方法可以将一个集合的值作为「键」，再将另一个数组或者集合的值作为「值」合并成一个集合：

.. code-block:: php

    <?php
    $collection = collect(['name', 'age']);

    $combined = $collection->combine(['George', 29]);

    $combined->all();

    // ['name' => 'George', 'age' => 29]

concat
""""""
``concat`` 方法将给定的 ``array`` 或集合值附加到集合的末尾：

.. code-block:: php

    <?php
    $collection = collect(['John Doe']);

    $concatenated = $collection->concat(['Jane Doe'])->concat(['name' => 'Johnny Doe']);

    $concatenated->all();

    // ['John Doe', 'Jane Doe', 'Johnny Doe']

merge
"""""
``merge`` 方法将给定数组或集合合并到原集合。如果给定项目中的字符串键与原集合中的字符串键匹配，给定的项目的值将会覆盖原集合中的值：

.. code-block:: php

    <?php
    $collection = collect(['product_id' => 1, 'price' => 100]);

    $merged = $collection->merge(['price' => 200, 'discount' => false]);

    $merged->all();

    // ['product_id' => 1, 'price' => 200, 'discount' => false]

如果给定的项目的键是数字，这些值将被追加到集合的末尾：

.. code-block:: php

    <?php
    $collection = collect(['Desk', 'Chair']);

    $merged = $collection->merge(['Bookcase', 'Door']);

    $merged->all();

    // ['Desk', 'Chair', 'Bookcase', 'Door']

zip
"""
``zip`` 方法将给定数组的值与相应索引处的原集合的值合并在一起：

.. code-block:: php

    <?php
    $collection = collect(['Chair', 'Desk']);

    $zipped = $collection->zip([100, 200]);

    $zipped->all();

    // [['Chair', 100], ['Desk', 200]]

单集合操作
^^^^^^^^^^
when
""""
``when`` 方法当传入的第一个参数为 ``true`` 的时候，将执行给定的回调：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3]);

    $collection->when(true, function ($collection) {
        return $collection->push(4);
    });

    $collection->when(false, function ($collection) {
        return $collection->push(5);
    });

    $collection->all();

    // [1, 2, 3, 4]

和 ``when`` 方法相反，参考 ``unless`` 方法。

unless
""""""
``unless`` 方法当传入的第一个参数不为 ``true`` 的时候，将执行给定的回调：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3]);

    $collection->unless(true, function ($collection) {
        return $collection->push(4);
    });

    $collection->unless(false, function ($collection) {
        return $collection->push(5);
    });

    $collection->all();

    // [1, 2, 3, 5]

和 ``unless`` 方法相反，参考 ``when`` 方法。

forget
""""""
``forget`` 方法通过给定的键来移除掉集合中对应的内容：

.. code-block:: php

    <?php
    $collection = collect(['name' => 'taylor', 'framework' => 'laravel']);

    $collection->forget('name');

    $collection->all();

    // ['framework' => 'laravel']

.. note:: 与大多数集合的方法不同， ``forget`` 不会返回修改过后的新集合；它会直接修改原来的集合。

put
"""
``put`` 方法在集合内设置给定的键值对：

.. code-block:: php

    <?php
    $collection = collect(['product_id' => 1, 'name' => 'Desk']);

    $collection->put('price', 100);

    $collection->all();

    // ['product_id' => 1, 'name' => 'Desk', 'price' => 100]

implode
""""""""
``implode`` 方法合并集合中的项目。其参数取决于集合中项目的类型。如果集合包含数组或对象，你应该传入你希望连接的属性的键，以及你希望放在值之间用来「拼接」的字符串：

.. code-block:: php

    <?php
    $collection = collect([
        ['account_id' => 1, 'product' => 'Desk'],
        ['account_id' => 2, 'product' => 'Chair'],
    ]);

    $collection->implode('product', ', ');

    // Desk, Chair

如果集合包含简单的字符串或数值，只需要传入「拼接」用的字符串作为该方法的唯一参数即可：

.. code-block:: php

    <?php
    collect([1, 2, 3, 4, 5])->implode('-');

    // '1-2-3-4-5'

pad
""""
``pad`` 方法将使用给定的值填充数组，直到数组达到指定的大小。 此方法的行为与 ``array_pad`` PHP 函数功能类似。

要填充到左侧，您应指定负号。如果给定大小的绝对值小于或等于数组的长度，则不会发生填充：

.. code-block:: php

    <?php
    $collection = collect(['A', 'B', 'C']);

    $filtered = $collection->pad(5, 0);

    $filtered->all();

    // ['A', 'B', 'C', 0, 0]

    $filtered = $collection->pad(-5, 0);

    $filtered->all();

    // [0, 0, 'A', 'B', 'C']

队列操作
^^^^^^^^

pop
"""
``pop`` 方法移除并返回集合中的最后一个项目：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5]);

    $collection->pop();

    // 5

    $collection->all();

    // [1, 2, 3, 4]

shift
"""""
``shift`` 方法移除并返回集合的第一个项目：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5]);

    $collection->shift();

    // 1

    $collection->all();

    // [2, 3, 4, 5]

pull
""""
``pull`` 方法把给定键对应的值从集合中移除并返回：

.. code-block:: php

    <?php
    $collection = collect(['product_id' => 'prod-100', 'name' => 'Desk']);

    $collection->pull('name');

    // 'Desk'

    $collection->all();

    // ['product_id' => 'prod-100']

prepend
""""""""
``prepend`` 方法将给定的值添加到集合的开头：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5]);

    $collection->prepend(0);

    $collection->all();

    // [0, 1, 2, 3, 4, 5]

你也可以传递第二个参数来设置新增加项的键：

.. code-block:: php

    <?php
    $collection = collect(['one' => 1, 'two' => 2]);

    $collection->prepend(0, 'zero');

    $collection->all();

    // ['zero' => 0, 'one' => 1, 'two' => 2]

push
""""
``push`` 方法把给定值添加到集合的末尾：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4]);

    $collection->push(5);

    $collection->all();

    // [1, 2, 3, 4, 5]

concat
"""""""
``concat`` 方法将给定的 ``array`` 或集合值附加到集合的末尾：

.. code-block:: php

    <?php
    $collection = collect(['John Doe']);

    $concatenated = $collection->concat(['Jane Doe'])->concat(['name' => 'Johnny Doe']);

    $concatenated->all();

    // ['John Doe', 'Jane Doe', 'Johnny Doe']

Set操作
^^^^^^^
集合类还提供了帮助我们处理数据集的方法。 这意味着我们可以比较两个数据集并基于此执行操作。

union(并集)
""""""""""""
``union()`` 方法用于把数组中的值添加到集合中。 如果集合中已存在一个值，则数组中的值将被忽略。

.. code-block:: php

    <?php
        $coolPeople = collect([
            1 => 'John', 2 => 'James', 3 => 'Jack'
        ]);

        $allCoolPeople = $coolPeople->union([
            4 => 'Sarah', 1 => 'Susan', 5 =>'Seyi'
        ]);
        $allCoolPeople->all();
        /*
        [
            1 => "John", 2 => "James", 3 => "Jack", 4 => "Sarah", 5 => "Seyi",
       ]
       */

intersect(交集)
""""""""""""""""
``intersect()`` 方法删除集合中不在传入数组或集合中的元素。

.. code-block:: php

    <?php
        $coolPeople = collect([
            1 => 'John', 2 => 'James', 3 => 'Jack'
        ]);

        $veryCoolPeople = $coolPeople->intersect(['Sarah', 'John', 'James']);
        $veryCoolPeople->toArray();
        //[1 => "John" 2 => "James"]

您会注意到， ``intersect`` 方法保留返回值的键。

intersectByKeys
+++++++++++++++
``intersectByKeys`` 方法删除原集合中不存在于给定「数组」或集合中的任何键：

.. code-block:: php

    <?php
    $collection = collect([
        'serial' => 'UX301', 'type' => 'screen', 'year' => 2009
    ]);

    $intersect = $collection->intersectByKeys([
        'reference' => 'UX404', 'type' => 'tab', 'year' => 2011
    ]);

    $intersect->all();

    // ['type' => 'screen', 'year' => 2009]

diff(差集)
""""
diff
++++
``diff`` 方法将集合与其它集合或纯 ``PHP`` 数组进行 **值** 的比较，然后返回原集合中存在而给定集合中不存在的值：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5]);

    $diff = $collection->diff([2, 4, 6, 8]);

    $diff->all();

    // [1, 3, 5]

diffAssoc
+++++++++
``diffAssoc`` 该方法与另外一个集合或基于它的 **键和值** 的 ``PHP`` 数组进行比较。这个方法会返回原集合不存在于给定集合中的键值对 ：

.. code-block:: php

    <?php
    $collection = collect([
        'color' => 'orange',
        'type' => 'fruit',
        'remain' => 6
    ]);

    $diff = $collection->diffAssoc([
        'color' => 'yellow',
        'type' => 'fruit',
        'remain' => 3,
        'used' => 6
    ]);

    $diff->all();

    // ['color' => 'orange', 'remain' => 6]

diffKeys
++++++++
``diffKeys`` 方法与另外一个集合或 ``PHP`` 数组的 **键** 进行比较，然后返回原集合中存在而给定的集合中不存在「键」所对应的键值对：

.. code-block:: php

    <?php
    $collection = collect([
        'one' => 10,
        'two' => 20,
        'three' => 30,
        'four' => 40,
        'five' => 50,
    ]);

    $diff = $collection->diffKeys([
        'two' => 2,
        'four' => 4,
        'six' => 6,
        'eight' => 8,
    ]);

    $diff->all();

    // ['one' => 10, 'three' => 30, 'five' => 50]

提取类
------
给定一个数据数组，然后生成一个集合，你可能想要得到它的一部分。 这可能是：

- 前2个记录；
- 后2个记录；
- 除了第2组的所有记录；

集合通过一些方法来帮助我们。

unique
^^^^^^
``unique`` 方法返回集合中所有唯一的项目。返回的集合保留着原数组的键，所以在这个例子中，我们使用 ``values`` 方法来把键重置为连续编号的索引。

.. code-block:: php

    <?php
    $collection = collect([1, 1, 2, 2, 3, 4, 2]);

    $unique = $collection->unique();

    $unique->values()->all();

    // [1, 2, 3, 4]

处理嵌套数组或对象时，你可以指定用来决定唯一性的键：

.. code-block:: php

    <?php
    $collection = collect([
        ['name' => 'iPhone 6', 'brand' => 'Apple', 'type' => 'phone'],
        ['name' => 'iPhone 5', 'brand' => 'Apple', 'type' => 'phone'],
        ['name' => 'Apple Watch', 'brand' => 'Apple', 'type' => 'watch'],
        ['name' => 'Galaxy S6', 'brand' => 'Samsung', 'type' => 'phone'],
        ['name' => 'Galaxy Gear', 'brand' => 'Samsung', 'type' => 'watch'],
    ]);

    $unique = $collection->unique('brand');

    $unique->values()->all();

    /*
        [
            ['name' => 'iPhone 6', 'brand' => 'Apple', 'type' => 'phone'],
            ['name' => 'Galaxy S6', 'brand' => 'Samsung', 'type' => 'phone'],
        ]
    */

你也可以传入自己的回调来确定项目的唯一性：

.. code-block:: php

    <?php
    $unique = $collection->unique(function ($item) {
        return $item['brand'].$item['type'];
    });

    $unique->values()->all();

    /*
        [
            ['name' => 'iPhone 6', 'brand' => 'Apple', 'type' => 'phone'],
            ['name' => 'Apple Watch', 'brand' => 'Apple', 'type' => 'watch'],
            ['name' => 'Galaxy S6', 'brand' => 'Samsung', 'type' => 'phone'],
            ['name' => 'Galaxy Gear', 'brand' => 'Samsung', 'type' => 'watch'],
        ]
    */

在检查项目值时 ``unique`` 方法使用的是「宽松」比较，意味着具有整数值的字符串将被视为等于相同值的整数。使用 ``uniqueStrict`` 可以进行「严格」比较 。

uniqueStrict
"""""""""""""
这个方法的使用和 ``unique`` 方法类似，只是使用了「严格」比较来过滤。

values
^^^^^^
``values`` 方法返回键被重置为连续编号的新集合：

.. code-block:: php

    <?php
    $collection = collect([
        10 => ['product' => 'Desk', 'price' => 200],
        11 => ['product' => 'Desk', 'price' => 200]
    ]);

    $values = $collection->values();

    $values->all();

    /*
        [
            0 => ['product' => 'Desk', 'price' => 200],
            1 => ['product' => 'Desk', 'price' => 200],
        ]
    */

all
^^^^
``all`` 方法返回该集合表示的底层 ``数组`` ：

.. code-block:: php

    <?php
    collect([1, 2, 3])->all();

    // [1, 2, 3]

only
^^^^
``only`` 方法返回集合中给定键的所有项目：

.. code-block:: php

    <?php
    $collection = collect(['product_id' => 1, 'name' => 'Desk', 'price' => 100, 'discount' => false]);

    $filtered = $collection->only(['product_id', 'name']);

    $filtered->all();

    // ['product_id' => 1, 'name' => 'Desk']

与 ``only`` 相反的方法，请查看 `except`_ 。

except
^^^^^^
``except`` 方法返回集合中除了指定键以外的所有项目：

.. code-block:: php

    <?php
    $collection = collect(['product_id' => 1, 'price' => 100, 'discount' => false]);

    $filtered = $collection->except(['price', 'discount']);

    $filtered->all();

    // ['product_id' => 1]

与 ``except`` 相反的方法，请查看 `only`_ 。

pluck
^^^^^^
``pluck`` 方法获取集合中给定键对应的所有值：

.. code-block:: php

    <?php
    $collection = collect([
        ['product_id' => 'prod-100', 'name' => 'Desk'],
        ['product_id' => 'prod-200', 'name' => 'Chair'],
    ]);

    $plucked = $collection->pluck('name');

    $plucked->all();

    // ['Desk', 'Chair']

你也可以通过传入第二个参数来指定生成的集合的键：

.. code-block:: php

    <?php
    $plucked = $collection->pluck('name', 'product_id');

    $plucked->all();

    // ['prod-100' => 'Desk', 'prod-200' => 'Chair']

keys
^^^^
``keys`` 方法返回集合的所有键：

.. code-block:: php

    <?php
    $collection = collect([
        'prod-100' => ['product_id' => 'prod-100', 'name' => 'Desk'],
        'prod-200' => ['product_id' => 'prod-200', 'name' => 'Chair'],
    ]);

    $keys = $collection->keys();

    $keys->all();

    // ['prod-100', 'prod-200']

forPage
^^^^^^^
``forPage`` 方法返回给定页码上显示的项目的新集合。这个方法接受页码作为其第一个参数和每页显示的项目数作为其第二个参数。

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5, 6, 7, 8, 9]);

    $chunk = $collection->forPage(2, 3);

    $chunk->all();

    // [4, 5, 6]

take
^^^^^
``take`` 方法接受一个整数值并返回指定数量的项目。 考虑一个负数， ``take()`` 从集合的末尾返回指定数量的项目。

.. code-block:: php

    <?php
        $list = collect([
            'Albert', 'Ben', 'Charles', 'Dan', 'Eric', 'Xavier', 'Yuri', 'Zane'
        ]);

        //Get the first two names
        $firstTwo = $list->take(2);
        //['Albert', 'Ben']

        //Get the last two names
        $lastTwo = $list->take(-2);
        //['Yuri', 'Zane']

slice
^^^^^
``slice`` 方法返回集合中给定值后面的部分：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5, 6, 7, 8, 9, 10]);

    $slice = $collection->slice(4);

    $slice->all();

    // [5, 6, 7, 8, 9, 10]

如果你想限制返回内容的大小，就将期望的大小作为第二个参数传递给方法：

.. code-block:: php

    <?php
    $slice = $collection->slice(4, 2);

    $slice->all();

    // [5, 6]

默认情况下，返回的内容将会保留原始键。假如你不希望保留原始的键，你可以使用 ``values`` 方法来重新建立索引。

split
^^^^^
``split`` 方法将集合按给定的值(该值为生成的组数)拆分：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5]);

    $groups = $collection->split(3); // 分成3组

    $groups->toArray();

    // [[1, 2], [3, 4], [5]]

chunk
^^^^^
``chunk`` 方法将一个集合分解成大小为n的更小的集合。

.. code-block:: php

    <?php
        $list = collect([
            'Albert', 'Ben', 'Charles', 'Dan', 'Eric', 'Xavier', 'Yuri', 'Zane'
        ]);

        $chunks = $list->chunk(3);
        $chunks->toArray();
        /*
        [
            [1 => "Albert", 2 => "Ben", 3 => "Charles",],
            [3 => "Dan", 4 => "Eric", 5 => "Xavier",],
            [6 => "Yuri", 7 => "Zane",],
        ]
        */

有很多地方可以派上用场。

将数据传递到 ``Blade`` 视图时，可以将其分块以获得n行，例如，将每3个名称合并到一行中。

.. code-block:: html

    @foreach($list->chunk(3) as $names)
        <div class="row">
            @foreach($names as $name)
                {{ $name }}
            @endforeach
        </div>
    @endforeach

您也可以使用 ``collapse()`` 方法将较小的集合组合为较大的集合，从而完成 ``chunk()`` 的相反操作。

splice
^^^^^^
``splice`` 方法删除并返回从给定索引值及之后的内容，原集合也会受到影响：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5]);

    $chunk = $collection->splice(2);

    $chunk->all();

    // [3, 4, 5]

    $collection->all();

    // [1, 2]

你可以传入第二个参数以限制被删除内容的大小：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5]);

    $chunk = $collection->splice(2, 1);

    $chunk->all();

    // [3]

    $collection->all();

    // [1, 2, 4, 5]

此外，你可以传入含有新项目的第三个参数来替换集合中删除的项目：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5]);

    $chunk = $collection->splice(2, 1, [10, 11]);

    $chunk->all();

    // [3]

    $collection->all();

    // [1, 2, 10, 11, 4, 5]

tap
^^^^
``tap`` 方法将集合传递给回调，在特定点「tap」集合。此举能让你对集合中的项目执行某些操作，而不影响集合本身：

.. code-block:: php

    <?php
    collect([2, 4, 3, 1, 5])
        ->sort()
        ->tap(function ($collection) {
            Log::debug('Values after sorting', $collection->values()->toArray());
        })
        ->shift(); // 移除并返回集合的第一个项目

    // 1

nth
^^^
``nth`` 方法创建由每隔 ``n`` 个元素组成一个新的集合：

.. code-block:: php

    <?php
    $collection = collect(['a', 'b', 'c', 'd', 'e', 'f']);

    $collection->nth(4);

    // ['a', 'e']

你也可以选择传入一个偏移位置作为第二个参数

.. code-block:: php

    <?php
    $collection->nth(4, 1);

    // ['b', 'f']

聚合操作类
----------

sum
^^^
``sum`` 方法返回集合内所有项目的总和：

.. code-block:: php

    <?php
    collect([1, 2, 3, 4, 5])->sum();

    // 15

如果集合包含嵌套数组或对象，则应该传入一个键来指定要进行求和的值：

.. code-block:: php

    <?php
    $collection = collect([
        ['name' => 'JavaScript: The Good Parts', 'pages' => 176],
        ['name' => 'JavaScript: The Definitive Guide', 'pages' => 1096],
    ]);

    $collection->sum('pages');

    // 1272

另外，你也可以传入回调来决定要用集合中的哪些值进行求和：

.. code-block:: php

    <?php
    $collection = collect([
        ['name' => 'Chair', 'colors' => ['Black']],
        ['name' => 'Desk', 'colors' => ['Black', 'Mahogany']],
        ['name' => 'Bookcase', 'colors' => ['Red', 'Beige', 'Brown']],
    ]);

    $collection->sum(function ($product) {
        return count($product['colors']);
    });

    // 6

count
^^^^^^
``count`` 方法返回该集合内的项目总数：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4]);

    $collection->count();

    // 4

avg
^^^^
``avg`` 方法返回给定键的 平均值:

.. code-block:: php

    <?php
    $average = collect([['foo' => 10], ['foo' => 10], ['foo' => 20], ['foo' => 40]])->avg('foo');

    // 20

    $average = collect([1, 1, 2, 4])->avg();

    // 2

average
"""""""
``avg`` 方法的别名。

median
^^^^^^
``median`` 方法返回给定键的 中值:

.. code-block:: php

    <?php
    $median = collect([['foo' => 10], ['foo' => 10], ['foo' => 20], ['foo' => 40]])->median('foo');

    // 15

    $median = collect([1, 1, 2, 4])->median();

    // 1.5

mode
^^^^^
``mode`` 方法返回给定键的 众数:

.. code-block:: php

    <?php
    $mode = collect([['foo' => 10], ['foo' => 10], ['foo' => 20], ['foo' => 40]])->mode('foo');

    // [10]

    $mode = collect([1, 1, 2, 4])->mode();

    // [1]

max
^^^^
``max`` 方法返回给定键的最大值：

.. code-block:: php

    <?php
    $max = collect([['foo' => 10], ['foo' => 20]])->max('foo');

    // 20

    $max = collect([1, 2, 3, 4, 5])->max();

    // 5

min
^^^^
``min`` 方法返回给定键的最小值：

.. code-block:: php

    <?php
    $min = collect([['foo' => 10], ['foo' => 20]])->min('foo');

    // 10

    $min = collect([1, 2, 3, 4, 5])->min();

    // 1

random
^^^^^^
``random`` 方法从集合中返回一个随机项：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5]);

    $collection->random();

    // 4 - (retrieved randomly)

你可以选择性传入一个整数到 ``random`` 来指定要获取的随机项的数量。只要你显式传递你希望接收的数量时，则会返回项目的集合：

.. code-block:: php

    <?php
    $random = $collection->random(3);

    $random->all();

    // [2, 4, 5] - (retrieved randomly)

shuffle
^^^^^^^
``shuffle`` 方法随机排序集合中的项目：

.. code-block:: php

    <?php
    $collection = collect([1, 2, 3, 4, 5]);

    $shuffled = $collection->shuffle();

    $shuffled->all();

    // [3, 2, 5, 1, 4] - (generated randomly)

转换为数组
----------
toArray
^^^^^^^
``toArray`` 方法将集合转换成 ``PHP`` 数组。如果集合的值是 ``Eloquent`` 模型，那也会被转换成数组：

.. code-block:: php

    <?php
    $collection = collect(['name' => 'Desk', 'price' => 200]);

    $collection->toArray();

    /*
        [
            ['name' => 'Desk', 'price' => 200],
        ]
    */

.. note:: ``toArray`` 也会将所有集合的嵌套对象转换为数组。如果你想获取原数组，就改用 ``all`` 方法。这样返回的数组中可能包含对象或者集合元素。

all
^^^^
``all`` 方法返回集合表示的底层数组，就是集合中 ``$items`` 属性：

.. code-block:: php

    <?php
    collect([1, 2, 3])->all();

    // [1, 2, 3]

转换为JSON
-----------
toJson
^^^^^^^
``toJson`` 方法将集合转换成 ``JSON`` 字符串：

.. code-block:: php

    <?php
    $collection = collect(['name' => 'Desk', 'price' => 200]);

    $collection->toJson();

    // '{"name":"Desk", "price":200}'

