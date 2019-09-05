**********************
PHP 日期、时间和时区处理 API 及组件
**********************

处理日期和时间需要考虑很多事情，例如日期的格式、时区、闰年和天数各异的月份，自己处理太容易出错了，我们应该使用 ``PHP 5.2.0``  引入的 ``DateTime`` 、 ``DateIntervel`` 和 ``DateTimeZone`` 这些类帮助我们创建及处理日期、时间和时区。

设置默认时区
============
首先我们要为 ``PHP`` 中处理日期和时间的函数设置默认时区，如果不设置的话， ``PHP`` 会显示一个 ``E_WARNING`` 消息，设置默认时区有两种方式，可以像下面这样在 ``php.ini`` 中设置：

.. code-block:: ini

    date.timezone = 'Asia/Shanghai';

也可以在运行时使用 ``date_default_timezone_set()`` 函数设置：

.. code-block:: php

    <?php
    date_default_timezone_set('Asia/Shanghai');

DateTime类
==========
``DateTime`` 类提供了一个面向对象接口，用于管理日期和时间，一个 ``DateTime`` 实例表示一个具体的日期和时间， ``DateTime`` 构造方法是创建 ``DateTime`` 新实例最简单的方式：

.. code-block:: php

    <?php
    $datetime = new DateTime();

如果没有参数， ``DateTime`` 类的构造方法创建的是一个表示当前日期和时间的实例。我们可以把一个字符串传入 ``DateTime`` 类的构造方法以便指定日期和时间：

.. code-block:: php

    <?php
    $datetime = new DateTime('2016-06-06 10:00 pm');

.. note:: 传入的字符串参数必须是有效的日期和时间格式（  `参考手册 <http://php.net/manual/zh/datetime.formats.php>`_ ）

理想情况下，我们会指定 ``PHP`` 能理解的日期和时间格式，可是实际情况并不总是如此，有时我们必须处理其它格式或出乎意料的格式，这时我们可以通过 ``DateTime`` 提供的静态方法 ``createFromFormat`` 来使用自定义的格式创建 ``DateTime`` 实例，该方法的第一个参数是表示日期和时间格式的字符串，第二个参数是要使用这种格式的日期和时间字符串：

.. code-block:: php

    <?php
    $datetime = DateTime::createFromFormat('M j, Y H:i:s', 'June 6, 2016 22:00:00');

.. note:: 也许你很眼熟，没错， ``DateTime::createFromFormat`` 和 ``date`` 函数类似。可用的日期时间格式参考 `这里 <http://php.net/manual/zh/datetime.createfromformat.php>`_

DateInterval类
==============
处理 ``DateTime`` 实例之前需要先了解 ``DateInterval`` 类， ``DateInterval`` 实例表示长度固定的时间段（比如两天），或者相对而言的时间段（例如昨天），我们通常使用该类的实例来修改 ``DateTime`` 实例。例如， ``DateTime`` 提供了用于处理 ``DateTime`` 实例的 ``add`` 和 ``sub`` 方法，这两个方法的参数是一个 ``DateInterval`` 实例，表示从 ``DateTime`` 中增加的时间量或减少的时间量。

我们使用构造函数实例化 ``DateInterval`` 实例， ``DateInterval`` 构造函数的参数是一个表示时间间隔约定的字符串，这个时间间隔约定以字母P开头，后面跟着一个整数，最后是一个周期标识符，限定前面的整数。有效周期标识符如下：

- Y（年）
- M（月）
- D（日）
- W（周）
- H（时）
- M（分）
- S（秒）

间隔约定中既可以有时间也可以有日期，如果有时间需要在日期和时间之间加上字母 ``T`` ，例如，间隔约定 ``P2D`` 表示间隔两天，间隔约定 ``P2DT5H2M`` 表示间隔两天五小时两分钟。

下面的实例演示了如何使用 ``add`` 方法将 ``DateTime`` 实例表示的日期和时间向后推移一段时间：

.. code-block:: php

    <?php
    //创建DateTime实例
    $datetime = new DateTime('2016-06-06 22:00:00');

    //创建长度为两天的间隔
    $interval = new DateInterval('P2D');

    //修改DateTime实例
    $datetime->add($interval);
    echo $datetime->format('Y-m-d H:i:s');

我们还可以创建反向的 ``DateInterval`` 实例：

.. code-block:: php

    <?php
    $datetime = new DateTime();
    $interval = DateInterval::createFromDateString('-1 day');
    $period = new DatePeriod($datetime, $interval, 3);
    foreach ($period as $date) {
        echo $date->format('Y-m-d'), PHP_EOL;
    }
    // 以上代码输出为：
    2016-06-06
    2016-06-05
    2016-06-04
    2016-06-03

    //DatePeriod: 迭代处理区间内的日期
    $interval= new DateInterval('P0DT2H');

    $start = new DateTime('2008-08-08',$timezones);
    $end = new DateTime('2008-08-09',$timezones);
    $timeRange = new DatePeriod($start,$interval,$end);

    foreach ($timeRange as $hour) {
        showTimeObj(NULL,$hour);
    }

DateTimeZone类
==============
``PHP`` 使用 ``DateTimeZone`` 类表示时区，我们只需要把有效的时区标识符传递给 ``DateTimeZone`` 类的构造函数：

.. code-block:: php

    <?php
    $timezone = new DateTimeZone('Asia/Shanghai');

创建 ``DateTime`` 实例通常需要使用 ``DateTimeZone`` 实例， ``DateTime`` 类构造方法的第二个参数（可选）就是一个 ``DateTimeZone`` 实例，传入这个参数后， ``DateTime`` 实例的值以及对这个值的所有修改都相对于这个指定的时区，如果不传入则使用的是前面设置的默认时区：

.. code-block:: php

    <?php
    $timezone = new DateTimeZone('Asia/Shanghai');
    $datetime = new DateTime('2016-06-06', $timezone);

实例化之后还可以使用 ``setTimezone`` 方法修改 ``DateTime`` 实例的时区：

.. code-block:: php

    <?php
    $timezone = new DateTimeZone('Asia/Shanghai');
    $datetime = new DateTime('2016-06-06', $timezone);
    $datetime->setTimezone(new DateTimeZone('Asia/Hong_kong'));

DatePeriod类
=============
有时我们需要迭代处理一段时间内反复出现的一系列日期和时间， ``DatePeriod`` 类可以解决这个问题（前面已经用到过）， ``DatePeriod`` 类的构造方法接受三个参数而且都必须提供：

- 一个 ``DateTime`` 实例，表示迭代开始的日期和时间；
- 一个 ``DateInterval`` 实例，表示下一个日期和时间的间隔；
- 一个整数，表示迭代的总次数或者结束 ``DateTime`` 实例；

``DatePeriod`` 是迭代器，每次迭代都会产出一个 ``DateTime`` 实例。 ``DatePeriod`` 的第四个参数是可选的，用于显式指定周期的结束日期和时间，如果迭代时想要排除开始日期和时间，可以把构造方法的最后一个参数设为 ``DatePeriod::EXCLUDE_START_DATE`` 常量：

.. code-block:: php

    <?php
    $datetime = new DateTime();
    $interval = new DateInterval('P2D');
    $period = new DatePeriod($datetime, $interval, 3, DatePeriod::EXCLUDE_START_DATE);
    foreach ($period as $date) {
        echo $date->format('Y-m-d H:i:s'), PHP_EOL;
    }

    // 打印结果是：
    2016-06-08
    2016-06-10
    2016-06-12

nesbot/carbon日期组件
=====================
如果经常需要处理日期和时间，应该使用 ``nesbot/carbon`` 组件（ `github地址 <https://github.com/briannesbitt/Carbon>`_ ）， ``Laravel`` 框架也是使用了这个组件处理日期和时间，该组件集成了常用的日期及时间处理 ``API`` ，其底层正是使用了我们上面提到的几个日期时间处理类实现了各种功能，有兴趣可以去研究下。

请使用手册参考 ``Carbon组件的使用手册``
