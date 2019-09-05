===================
辅助函数-字符串函数
===================


字符串操作
==========

.. contents:: 目录
   :depth: 4

字符串生成
----------

str_random：函数生成一个指定长度的随机字符串
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
这个函数数用 PHP 的 ``random_bytes`` 函数：

.. code-block:: php

    <?php
    $random = str_random(40);

Str::orderedUuid：方法高效生成一个可储存在索引数据库列中的 「第一时间」 UUID
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

<?php
    use Illuminate\Support\Str;

    return (string) Str::orderedUuid();

Str::uuid：方法生成一个 UUID（版本 4）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    use Illuminate\Support\Str;

    return (string) Str::uuid();

str_slug：函数根据给定的字符串生成一个 URL 友好的「slug」
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $slug = str_slug('Laravel 5 Framework', '-');

    // laravel-5-framework

字符串修改
----------
str_start：函数将给定值的单个实例添加到字符串（如果它尚未以值开始）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $adjusted = str_start('this/string', '/');

    // /this/string

    $adjusted = str_start('/this/string/', '/');

    // /this/string


str_finish：函数将给定字符串以给定值结尾返回（如果它尚未以给定值结尾）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $adjusted = str_finish('this/string', '/');

    // this/string/

    $adjusted = str_finish('this/string/', '/');

    // this/string/


str_limit：函数按给定的长度截断给定的字符串
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $truncated = str_limit('The quick brown fox jumps over the lazy dog', 20);

    // The quick brown fox...

你也可以传递第三个参数来改变将被追加到最后的字符串：

.. code-block:: php

    <?php
    $truncated = str_limit('The quick brown fox jumps over the lazy dog', 20, ' (...)');

    // The quick brown fox (...)


str_plural：函数将字符串转换为复数形式
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
这个函数目前仅支持英文：

.. code-block:: php

    <?php
    $plural = str_plural('car');

    // cars

    $plural = str_plural('child');

    // children

你可以提供一个整数作为函数的第二个参数来检索字符串的单数或复数形式：

.. code-block:: php

    <?php
    $plural = str_plural('child', 2);

    // children

    $plural = str_plural('child', 1);

    // child


str_singular：函数将字符串转换为单数形式
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
这个函数目前仅支持英文：

.. code-block:: php

    <?php
    $singular = str_singular('cars');

    // car

    $singular = str_singular('children');

    // child


字符串匹配
----------

str_contains：函数判断给定的字符串是否包含给定的值
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $contains = str_contains('This is my name', 'my');

    // true


str_is：函数判断给定的字符串是否匹配给定的模式
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
星号（ ``*`` ）可以用来表示通配符：

.. code-block:: php

    <?php
    $matches = str_is('foo*', 'foobar');

    // true

    $matches = str_is('baz*', 'foobar');

    // false

starts_with：函数判断给定的字符串的开头是否是指定值
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $result = starts_with('This is my name', 'This');

    // true

ends_with：函数判断给定的字符串是否以给定的值结尾
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $result = ends_with('This is my name', 'name');

    // true

字符串替换
----------

preg_replace_array：函数使用数组顺序替换字符串中的给定模式
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $string = 'The event will take place between :start and :end';

    $replaced = preg_replace_array('/:[a-z_]+/', ['8:30', '9:00'], $string);

    // 活动将在 8:30 至 9:00 之间进行

str_replace_array：函数使用数组顺序替换字符串中的给定值
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $string = '该活动将于 ? 至 ? 之间举行';

    $replaced = str_replace_array('?', ['8:30', '9:00'], $string);

    // 该活动将于 8:30 至 9:00 之间举行

str_replace_first：函数替换字符串中给定值的第一个匹配项
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $replaced = str_replace_first('the', 'a', 'the quick brown fox jumps over the lazy dog');

    // a quick brown fox jumps over the lazy dog

str_replace_last：函数替换字符串中最后一次出现的给定值
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $replaced = str_replace_last('the', 'a', 'the quick brown fox jumps over the lazy dog');

    // the quick brown fox jumps over a lazy dog



字符串变换
----------

camel_case：函数将给定的值符传转换为「驼峰命名」
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $converted = camel_case('foo_bar');

    // fooBar

kebab_case：函数将给定的字符串转换为「短横线命名」
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
.. code-block:: php

    <?php
    $converted = kebab_case('fooBar');

    // foo-bar

snake_case：函数将给定的字符串转换为「蛇形命名」
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $converted = snake_case('fooBar');

    // foo_bar

studly_case：函数将给定的字符串转换为「变种驼峰命名」
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $converted = studly_case('foo_bar');

    // FooBar

title_case：函数将给定的字符串转换为「首字母大写」
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $converted = title_case('a nice title uses the correct case');

    // A Nice Title Uses The Correct Case

字符串本地翻译
--------------

``__`` ：函数使用你的 本地化文件来翻译给定的翻译字符串或翻译键
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php

    echo __('Welcome to our application');

    echo __('messages.welcome');

如果指定的翻译字符串或键不存在，则 ``__`` 函数会简单地返回给定的值。所以，按照上面的例子，如果翻译键 ``messages.welcome`` 不存在， ``__`` 方法会将其直接返回。


class_basename：返回给定类删除命名空间的类名
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $class = class_basename('Foo\Bar\Baz');

    // Baz

e：函数将 double_encode 选项设置为 false 来运行 PHP 的 htmlspecialchars 函数
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    echo e('<html>foo</html>');

    // <html>foo</html>

trans：函数使用你的 本地化文件 来翻译给定的翻译字符串或翻译键
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    echo trans('messages.welcome');

如果指定的翻译键不存在，则 ``trans`` 方法会简单地返回给定的键。所以，就上面的例子而言，如果翻译键不存在， ``trans`` 方法会返回 ``messages.welcome`` 。

trans_choice：函数根据词形变化来翻译给定的翻译键

.. code-block:: php

    <?php
    echo trans_choice('messages.notifications', $unreadCount);

如果指定的翻译键不存在， ``trans_choice`` 方法会简单地返回给定的键。所以，按照上面的例子，如果翻译键不存在， ``trans_choice`` 方法会返回 ``messages.notifications`` 。



str_before：函数返回字符串中给定值之前的所有内容
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $slice = str_before('This is my name', 'my name');

    // 'This is '


str_after：函数返回字符串中指定值之后的所有内容
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    $slice = str_after('This is my name', 'This is');

    // ' my name'



你也可以传递一个值的数组来判断给定的字符串是否包含任何值：

.. code-block:: php

    <?php
    $contains = str_contains('This is my name', ['my', 'foo']);

    // true











