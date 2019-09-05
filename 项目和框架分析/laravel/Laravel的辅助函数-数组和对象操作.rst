=======================
辅助函数-数组和对象函数
=======================

简介
====
Laravel 包含各种各样的全局「辅助」PHP 函数，框架本身也大量地使用了这些功能；如果你觉得方便，你可以在你的应用中自由的使用它们。

可用方法
========

数组或对象操作
--------------

重构数组
^^^^^^^^

array_wrap：函数将给定的值包装成一个数组
""""""""""""""""""""""""""""""""""""""""
如果给定的值已经是一个数组，则不会被改变：

.. code-block:: php

    <?php
    $string = 'Laravel';

    $array = array_wrap($string);

    // ['Laravel']

如果给定的值是空，则返回一个空数组：

.. code-block:: php

    <?php
    $nothing = null;

    $array = array_wrap($nothing);

    // []

array_collapse：函数将多个单数组合并成一个数组
""""""""""""""""""""""""""""""""""""""""""""""
collapse(崩塌)。

.. code-block:: php

    <?php
    $array = array_collapse([[1, 2, 3], [4, 5, 6], [7, 8, 9]]);

    // [1, 2, 3, 4, 5, 6, 7, 8, 9]

array_divide：函数返回两个数组，一个包含原始数组的健，另一个包含原始数组的值
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    [$keys, $values] = array_divide(['name' => 'Desk']);

    // $keys: ['name']
    // $values: ['Desk']


array_dot：函数将多维数组平铺到一维数组中，该数组使用「点」符号表示深度
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = ['products' => ['desk' => ['price' => 100]]];

    $flattened = array_dot($array);

    // ['products.desk.price' => 100]

array_flatten：函数将多维数组平铺为一维数组
"""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = ['name' => 'Joe', 'languages' => ['PHP', 'Ruby']];

    $flattened = array_flatten($array);

    // ['Joe', 'PHP', 'Ruby']

array_only：函数仅返回给定数组中指定的键/值对
"""""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = ['name' => 'Desk', 'price' => 100, 'orders' => 10];

    $slice = array_only($array, ['name', 'price']);

    // ['name' => 'Desk', 'price' => 100]

array_pluck：函数从数组中检索给定键的所有值
"""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = [
        ['developer' => ['id' => 1, 'name' => 'Taylor']],
        ['developer' => ['id' => 2, 'name' => 'Abigail']],
    ];

    $names = array_pluck($array, 'developer.name');

    // ['Taylor', 'Abigail']

你也可以指定生成的列表的键：

.. code-block:: php

    <?php
    $names = array_pluck($array, 'developer.name', 'developer.id');

    // [1 => 'Taylor', 2 => 'Abigail']

array_where：函数使用给定的闭包来过滤数组
"""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = [100, '200', 300, '400', 500];

    $filtered = array_where($array, function ($value, $key) {
        return is_string($value);
    });

    // [1 => 200, 3 => 400]

判断数组中是否存在值
^^^^^^^^^^^^^^^^^^^^

array_has：函数使用「点」符号检查数组中是否存在给定的项目或项目组
"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = ['product' => ['name' => 'Desk', 'price' => 100]];

    $contains = array_has($array, 'product.name');

    // true

    $contains = array_has($array, ['product.price', 'product.discount']);

    // false


给对象和数组添加值
^^^^^^^^^^^^^^^^^^
array_add：传入键和值来添加到指定的数组中
"""""""""""""""""""""""""""""""""""""""""

如果给定的键不在数组中，那么 ``array_add`` 函数将会把给定的键/值对添加到数组中：

.. code-block:: php

    <?php
    $array = array_add(['name' => 'Desk'], 'price', 100);

    // ['name' => 'Desk', 'price' => 100]

array_prepend：函数将一个项目推到数组的开头
""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = ['one', 'two', 'three', 'four'];

    $array = array_prepend($array, 'zero');

    // ['zero', 'one', 'two', 'three', 'four']

你可以指定用于该值的键：

.. code-block:: php

    <?php
    $array = ['price' => 100];

    $array = array_prepend($array, 'Desk', 'name');

    // ['name' => 'Desk', 'price' => 100]

array_set：函数使用「点」符号在深度嵌套的数组中设置一个值
"""""""""""""""""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = ['products' => ['desk' => ['price' => 100]]];

    array_set($array, 'products.desk.price', 200);

    // ['products' => ['desk' => ['price' => 200]]]

data_fill：函数使用「点」符号在嵌套数组或对象内设置缺少的值
"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $data = ['products' => ['desk' => ['price' => 100]]];

    data_fill($data, 'products.desk.price', 200);

    // ['products' => ['desk' => ['price' => 100]]]

    data_fill($data, 'products.desk.discount', 10);

    // ['products' => ['desk' => ['price' => 100, 'discount' => 10]]]

该函数也接受星号「 ``*`` 」作为通配符，并相应地填写目标：

.. code-block:: php

    <?php
    $data = [
        'products' => [
            ['name' => 'Desk 1', 'price' => 100],
            ['name' => 'Desk 2'],
        ],
    ];

    data_fill($data, 'products.*.price', 200);

    /*
        [
            'products' => [
                ['name' => 'Desk 1', 'price' => 100],
                ['name' => 'Desk 2', 'price' => 200],
            ],
        ]
    */

data_set：函数使用「点」符号在嵌套数组或对象内设置一个值
""""""""""""""""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $data = ['products' => ['desk' => ['price' => 100]]];

    data_set($data, 'products.desk.price', 200);

    // ['products' => ['desk' => ['price' => 200]]]

这个函数也接受通配符「 ``*`` 」，并相应地在目标上设置值：

.. code-block:: php

    <?php
    $data = [
        'products' => [
            ['name' => 'Desk 1', 'price' => 100],
            ['name' => 'Desk 2', 'price' => 150],
        ],
    ];

    data_set($data, 'products.*.price', 200);

    /*
        [
            'products' => [
                ['name' => 'Desk 1', 'price' => 200],
                ['name' => 'Desk 2', 'price' => 200],
            ],
        ]
    */

默认情况下，所有现有的值都会被覆盖。如果你只想设置一个不存在值，你可以传递 ``false`` 作为第三个参数：

.. code-block:: php

    <?php
    $data = ['products' => ['desk' => ['price' => 100]]];

    data_set($data, 'products.desk.price', 200, false);

    // ['products' => ['desk' => ['price' => 100]]]

从对象和数组删除值
^^^^^^^^^^^^^^^^^^

array_except：函数从数组中删除给定的键/值对
"""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = ['name' => 'Desk', 'price' => 100];

    $filtered = array_except($array, ['price']);

    // ['name' => 'Desk']

array_forget：函数使用「点」符号从深度嵌套数组中移除给定的键/值对
"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = ['products' => ['desk' => ['price' => 100]]];

    array_forget($array, 'products.desk');

    // ['products' => []]

从对象和数组获取值
^^^^^^^^^^^^^^^^^^

array_get：函数使用「点」符号从深度嵌套的数组中检索值
"""""""""""""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = ['products' => ['desk' => ['price' => 100]]];

    $price = array_get($array, 'products.desk.price');

    // 100

``array_get`` 函数也接受一个默认值，如果没有找到指定的健，则返回该值：

.. code-block:: php

    <?php
    $discount = array_get($array, 'products.desk.discount', 0);

    // 0

data_get：函数使用「点」符号从嵌套数组或对象中检索值
""""""""""""""""""""""""""""""""""""""""""""""""""""
.. code-block:: php

    <?php
    $data = ['products' => ['desk' => ['price' => 100]]];

    $price = data_get($data, 'products.desk.price');

    // 100

``data_get`` 函数还接受默认值作为第三个参数，如果找不到指定的键，将返回该值：

.. code-block:: php

    <?php
    $discount = data_get($data, 'products.desk.discount', 0);

    // 0

array_pull：函数返回并从数组中删除键/值对
"""""""""""""""""""""""""""""""""""""""""
.. code-block:: php

    <?php
    $array = ['name' => 'Desk', 'price' => 100];

    $name = array_pull($array, 'name');

    // $name: Desk

    // $array: ['price' => 100]

将默认值作为第三个参数传递给该方法。如果键不存在，则返回该值：

.. code-block:: php

    <?php
    $value = array_pull($array, $key, $default);

head：函数返回给定数组中的第一个元素
""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = [100, 200, 300];

    $first = head($array);

    // 100

last：函数返回给定数组中的最后一个元素
"""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = [100, 200, 300];

    $last = last($array);

    // 300

array_first：函数返回数组中第一个通过指定测试的元素
"""""""""""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = [100, 200, 300];

    $first = array_first($array, function ($value, $key) {
        return $value >= 150;
    });

    // 200

将默认值作为第三个参数传递给该方法。如果没有值通过测试，则返回该值：

.. code-block:: php

    <?php
    $first = array_first($array, $callback, $default);

array_last：函数返回数组中最后一个通过指定测试的元素
""""""""""""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = [100, 200, 300, 110];

    $last = array_last($array, function ($value, $key) {
        return $value >= 150;
    });

    // 300

将默认值作为第三个参数传递给该方法。如果没有值通过测试，则返回该值：

.. code-block:: php

    <?php
    $last = array_last($array, $callback, $default);

array_random：函数从数组中返回一个随机值
""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = [1, 2, 3, 4, 5];

    $random = array_random($array);

    // 4 - (retrieved randomly)

你也可以指定要返回的随机数的数量作为第二个可选参数。一旦你指定了第二个参数，即使数量为 1，这个函数也会返回一个数组：

.. code-block:: php

    <?php
    $items = array_random($array, 2);

    // [2, 5] - (retrieved randomly)

给数组排序
^^^^^^^^^^

array_sort：函数按照其值排序数组
""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = ['Desk', 'Table', 'Chair'];

    $sorted = array_sort($array);

    // ['Chair', 'Desk', 'Table']

你也可以按给定的闭包返回的结果对数组进行排序：

.. code-block:: php

    <?php
    $array = [
        ['name' => 'Desk'],
        ['name' => 'Table'],
        ['name' => 'Chair'],
    ];

    $sorted = array_values(array_sort($array, function ($value) {
        return $value['name'];
    }));

    /*
        [
            ['name' => 'Chair'],
            ['name' => 'Desk'],
            ['name' => 'Table'],
        ]
    */

array_sort_recursive：函数使用 sort 函数递归排序数组
""""""""""""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $array = [
        ['Roman', 'Taylor', 'Li'],
        ['PHP', 'Ruby', 'JavaScript'],
    ];

    $sorted = array_sort_recursive($array);

    /*
        [
            ['Li', 'Roman', 'Taylor'],
            ['JavaScript', 'PHP', 'Ruby'],
        ]
    */


