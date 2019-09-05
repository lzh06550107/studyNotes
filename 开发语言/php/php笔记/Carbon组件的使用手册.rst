*************
Carbon组件的使用手册
*************

介绍
====
``Carbon`` 是继承自 ``PHP DateTime`` 类的 ``API`` 扩展，它使得处理日期和时间更加简单。 ``Laravel`` 中默认使用的时间处理类就是 ``Carbon`` 。

.. code-block:: php

    <?php
    namespace Carbon;

    class Carbon extends \DateTime
    {
        // code here
    }

``Carbon`` 具有所有继承自 ``DateTime`` 类的方法。

你需要通过命名空间导入 ``Carbon`` 来使用，而不需每次都提供完整的名称。

.. code-block:: php

    <?php
    use Carbon\Carbon;

要确保时区设置正确。例如，所有比较是基于相同的UTC或者 ``DateTime`` 设置的时区。

.. code-block:: php

    <?php
    $dtToronto = Carbon::create(2012, 1, 1, 0, 0, 0, 'America/Toronto');
    $dtVancouver = Carbon::create(2012, 1, 1, 0, 0, 0, 'America/Vancouver');

    echo $dtVancouver->diffInHours($dtToronto); // 3

实例化
======
创建 ``Carbon`` 实例有很多方法。首先是构造函数。它重载了父构造函数，使用该函数，你需要知道第一个参数的 ``data/time`` 字符串格式。你可能很少使用构造函数，而是使用更方便的静态方法。

.. code-block:: php

    <?php
    $carbon = new Carbon();                  // equivalent to Carbon::now()
    $carbon = new Carbon('first day of January 2008', 'Asia/Shanghai');
    echo get_class($carbon);                 // 'Carbon\Carbon'
    $carbon = Carbon::now(-5); // -5 表示GMT时区偏移

第二个时区参数，可以是 ``DateTimeZone`` 实例、字符串或者对 ``GMT`` 时区偏移的整数。

.. code-block:: php

    <?php
    $now = Carbon::now();

    $nowInLondonTz = Carbon::now(new DateTimeZone('Asia/Shanghai'));

    // or just pass the timezone as a string
    $nowInLondonTz = Carbon::now('Asia/Shanghai');

    // or to create a date with a timezone of +1 to GMT during DST then just pass an integer
    echo Carbon::now(1)->tzName;             // Asia/Shanghai

如果你不想要丑陋的括号而是喜欢流式方法来调用，请使用如下方法：

.. code-block:: php

    <?php
    echo (new Carbon('first day of December 2008'))->addWeeks(2);     // 2008-12-15 00:00:00
    echo Carbon::parse('first day of December 2008')->addWeeks(2);    // 2008-12-15 00:00:00

.. note:: 在 ``PHP5.4`` 之前 ``(new MyClass())->method()`` 触发一个语法错误，如果你使用 ``PHP5.3`` ，你需要创建一个变量，然后调用该方法： ``$date = new Carbon('first day of December 2008'); echo $date->addWeeks(2)``

传递给 ``Carbon::parse`` 或者 ``new Carbon`` 的字符串可以是一个相对时间(next sunday, tomorrow, first day of next month, last year) 或者 绝对时间(first day of December 2008, 2017-01-06)。你可以使用 ``Carbon::hasRelativeKeywords()`` 测试一个字符串将会产生一个相对时间还是绝对时间。

.. code-block:: php

    <?php
    $string = 'first day of next month';
    if (strtotime($string) === false) {
        echo "'$string' is not a valid date/time string.";
    } elseif (Carbon::hasRelativeKeywords($string)) {
        echo "'$string' is a relative valid date/time string, it will returns different dates depending on the current date.";
    } else {
        echo "'$string' is an absolute date/time string, it will always returns the same date.";
    }

伴随 ``now()`` 方法，还存在一些静态实例化帮助方法。注意， ``today(), tomorrow()`` 和 ``yesterday()`` 接受一个时区参数且它们的时间值为 ``00:00:00`` 。

.. code-block:: php

    <?php
    $now = Carbon::now();
    echo $now;                               // 2018-04-17 11:37:07
    $today = Carbon::today();
    echo $today;                             // 2018-04-17 00:00:00
    $tomorrow = Carbon::tomorrow('Asia/Shanghai');
    echo $tomorrow;                          // 2018-04-18 00:00:00
    $yesterday = Carbon::yesterday();
    echo $yesterday;                         // 2018-04-16 00:00:00

下一组静态帮助方法是 ``createXXX()`` 。大部分静态 ``create`` 函数允许你自由提供参数个数，对于没有提供的参数使用默认值。一般默认值为当前日期、时间或者时区。无效的值将会抛出一个 ``InvalidArgumentException`` 异常。该异常包含的消息来自 ``DateTime::getLastErrors()`` 。

.. code-block:: php

    <?php
    Carbon::createFromDate($year, $month, $day, $tz);
    Carbon::createFromTime($hour, $minute, $second, $tz);
    Carbon::createFromTimeString("$hour:$minute:$second", $tz);
    Carbon::create($year, $month, $day, $hour, $minute, $second, $tz);

``createFromDate()`` 默认时间为现在。 ``createFromTime()`` 默认日期为今天。 ``create()`` 任何 ``null`` 参数默认值为当前各自的值。默认值的例外情况是，当指定一个小时参数，但是没有分钟或者秒，默认值将会是0。

.. code-block:: php

    <?php
    $xmasThisYear = Carbon::createFromDate(null, 12, 25);  // Year defaults to current year
    $Y2K = Carbon::create(2000, 1, 1, 0, 0, 0); // equivalent to Carbon::createMidnightDate(2000, 1, 1)
    $alsoY2K = Carbon::create(1999, 12, 31, 24);
    $noonLondonTz = Carbon::createFromTime(12, 0, 0, 'Europe/London');
    $teaTime = Carbon::createFromTimeString('17:00:00', 'Europe/London');

    // A two digit minute could not be found
    try { Carbon::create(1975, 5, 21, 22, -2, 0); } catch(InvalidArgumentException $x) { echo $x->getMessage(); }

创建异常发生在这些负值上，但不会溢出，为了获得溢出异常，请使用 ``createSafe()`` 

.. code-block:: php

    <?php
    echo Carbon::create(2000, 1, 35, 13, 0, 0);
    // 2000-02-04 13:00:00

    try {
        Carbon::createSafe(2000, 1, 35, 13, 0, 0);
    } catch (\Carbon\Exceptions\InvalidDateException $exp) {
        echo $exp->getMessage();
    }
    // day : 35 is not a valid value.

.. note:: 2018-02-29 将会产生异常，但2020-02-29却不会，因为2020是润年。

.. note:: ``Carbon::createSafe(2014, 3, 30, 1, 30, 0, 'Europe/London')`` 自从 PHP5.4 同样会产生异常，因为这个时间的小时根据夏令时跳过了，但是在 PHP5.4之前，这个日期存在，但无效。

.. code-block:: php

    <?php
    Carbon::createFromFormat($format, $time, $tz);

``createFromFormat()`` 是对 ``DateTime::createFromFormat`` 函数的封装。区别是，``$tz`` 参数可以是 ``DateTimeZone`` 实例或者一个时区字符串值。同样地，如果该函数的格式存在错误，将会调用 ``DateTime::getLastErrors()`` 方法，然后使用该错误消息抛出一个 ``InvalidArgumentException`` 。如果你查看上面 ``createXXX()`` 函数的源码，它们都是调用 ``createFromFormat()`` 。

.. code-block:: php

    echo Carbon::createFromFormat('Y-m-d H', '1975-05-21 22')->toDateTimeString(); // 1975-05-21 22:00:00

最后三个创建函数是针对 ``unix timestamps`` 。第一个 ``createFromTimestamp()`` 函数将会创建一个等价于给定时间戳和设置时区的 ``Carbon`` 实例，如果没有传入时区，则默认为当前时区。第二个 ``createFromTimestampUTC()`` 总是使用 ``UTC(GMT)`` 时区。第三个 ``createFromTimestampMs()`` 接受一个以毫秒而不是秒为单位的时间戳。允许接受负的时间戳。

.. code-block:: php

    <?php
    echo Carbon::createFromTimestamp(-1)->toDateTimeString();                                  // 1969-12-31 18:59:59
    echo Carbon::createFromTimestamp(-1, 'Europe/London')->toDateTimeString();                 // 1970-01-01 00:59:59
    echo Carbon::createFromTimestampUTC(-1)->toDateTimeString();                               // 1969-12-31 23:59:59
    echo Carbon::createFromTimestampMs(1)->format('Y-m-d\TH:i:s.uP T');                        // 1969-12-31T19:00:00.001000-05:00 EST
    echo Carbon::createFromTimestampMs(1, 'Europe/London')->format('Y-m-d\TH:i:s.uP T');

你可以通过 ``copy()`` 从一个现存的 ``Carbon`` 实例创建该实例的拷贝。如预期的日期、时间和时区值都复制到新的实例。

.. code-block:: php

    <?php
    $dt = Carbon::now();
    echo $dt->diffInYears($dt->copy()->addYear());  // 1

    // $dt was unchanged and still holds the value of Carbon:now()

您可以在现有 ``Carbon`` 实例上使用 ``nowWithSameTz()`` 现在得到一个现在时间在同一时区的新实例。

.. code-block:: php

    <?php
    $meeting = Carbon::createFromTime(19, 15, 00, 'Africa/Johannesburg');

    // 19:15 in Johannesburg
    echo 'Meeting starts at '.$meeting->format('H:i').' in Johannesburg.';                  // Meeting starts at 19:15 in Johannesburg.
    // now in Johannesburg
    echo "It's ".$meeting->nowWithSameTz()->format('H:i').' right now in Johannesburg.';    // It's 17:37 right now in Johannesburg.

最后，如果你发现继承了另一个库的 ``\DateTime`` 实例，不要害怕！你可以通过一个友好的 ``instance()`` 函数来创建一个 ``Carbon`` 实例。

.. code-block:: php

    <?php
    $dt = new \DateTime('first day of January 2008'); // <== instance from another API
    $carbon = Carbon::instance($dt);
    echo get_class($carbon);                               // 'Carbon\Carbon'
    echo $carbon->toDateTimeString();                      // 2008-01-01 00:00:00

注意微秒。PHP ``DateTime`` 对象允许你设置微秒值，但所有日期函数将会忽略它。从1.12.0开始， ``Carbon`` 现在在实例化或复制操作期间支持微秒，而 ``format()`` 方法默认支持微秒。

.. code-block:: php

    <?php
    $dt = Carbon::parse('1975-05-21 22:23:00.123456');
    echo $dt->micro;                                       // 123456
    echo $dt->copy()->micro;                               // 123456

在 ``PHP7.1`` 之前， ``DateTime`` 微秒没有增加到 ``now`` 实例且之后不能改变。这就意味着：

.. code-block:: php

    <?php
    $date = new DateTime('now');
    echo $date->format('u');
    // display current microtime in PHP >= 7.1 (expect a bug in PHP 7.1.3 only)
    // display 000000 before PHP 7.1

    $date = new DateTime('2001-01-01T00:00:00.123456Z');
    echo $date->format('u');
    // display 123456 in all PHP versions

    $date->modify('00:00:00.987654');
    echo $date->format('u');
    // display 987654 in PHP >= 7.1
    // display 123456 before PHP 7.1

要在 ``Carbon`` 中解决此限制，当在 ``PHP <7.1`` 中调用 ``now`` 会附加微秒，但可以根据需要禁用此功能（对 ``PHP>=7.1`` 不起作用）：

.. code-block:: php

    <?php
    Carbon::useMicrosecondsFallback(false);
    var_dump(Carbon::isMicrosecondsFallbackEnabled()); // false

    echo Carbon::now()->micro; // 0 in PHP < 7.1, microtime in PHP >= 7.1

    Carbon::useMicrosecondsFallback(true); // default value
    var_dump(Carbon::isMicrosecondsFallbackEnabled()); // true

    echo Carbon::now()->micro; // microtime in all PHP version

是否需要循环查找某个日期以查找最早或最晚的日期？ 不知道要将您的初始最大/最小值设置为什么？ 现在有两个助手可以让你的决定变得简单：

.. code-block:: php

    <?php
    echo Carbon::maxValue();                               // '9999-12-31 23:59:59'
    echo Carbon::minValue();                               // '0001-01-01 00:00:00'

最小值和最大值主要取决于系统（32位或64位）。

使用32位操作系统或32位版本的PHP（可以使用 ``PHP_INT_SIZE === 4`` 在PHP中查看它），最小值为 ``0-unix-timestamp (1970-01-01 00:00:00)`` ，最大值是由常量 ``PHP_INT_MAX`` 给出的时间戳。

使用64位OS系统和64位PHP版本时，最小值为 ``01-01-01 00:00:00`` ，最大值为 ``9999-12-31 23:59:59`` 。

本地化
======
不幸的是，基类 ``DateTime`` 没有任何本地化支持。 为了开始本地化支持，添加了 ``formatLocalized($format)`` 方法。 该实现使用当前实例时间戳调用 ``strftime`` 。 如果您首先使用PHP函数 ``setlocale()`` 设置当前语言环境，则返回的字符串将以正确的语言环境格式化。

.. code-block:: php

    <?php
    setlocale(LC_TIME, 'German');
    echo $dt->formatLocalized('%A %d %B %Y');          // Mittwoch 21 Mai 1975
    setlocale(LC_TIME, 'English');
    echo $dt->formatLocalized('%A %d %B %Y');          // Wednesday 21 May 1975
    setlocale(LC_TIME, ''); // reset locale

``diffForHumans()`` 同样被本地化。你可以通过使用静态 ``Carbon::setLocale()`` 函数设置 ``Carbon`` 本地化和使用 ``Carbon::getLocale()`` 获取当前本地化设置。

.. code-block:: php

    <?php
    Carbon::setLocale('de');
    echo Carbon::getLocale();                          // de
    echo Carbon::now()->addYear()->diffForHumans();    // in 1 Jahr

    Carbon::setLocale('en');
    echo Carbon::getLocale();                          // en

一些语言需要 ``utf8`` 编码来打印(主要是不以 ``.UTF8`` 结尾的语言环境包) 在这种情况下，您可以使用静态方法 ``Carbon::setUtf8()`` 将 ``formatLocalized()`` 调用的结果编码到 ``utf8`` 字符集。

.. code-block:: php

    <?php
    setlocale(LC_TIME, 'Spanish');
    $dt = Carbon::create(2016, 01, 06, 00, 00, 00);
    Carbon::setUtf8(false);
    echo $dt->formatLocalized('%A %d %B %Y');          // mi�rcoles 06 enero 2016
    Carbon::setUtf8(true);
    echo $dt->formatLocalized('%A %d %B %Y');          // miércoles 06 enero 2016
    Carbon::setUtf8(false);
    setlocale(LC_TIME, '');

.. note:: 在 ``Linux`` 系统中，如果你翻译存在问题，检查安装在你系统中的 ``locales``。
- ``locale -a`` 列出开启的 ``locales`` ；
- ``sudo locale-gen fr_FR.UTF-8`` 安装一个新的 ``locale`` ；
- ``sudo dpkg-reconfigure locales`` 开启本地所有 ``locale`` ；
- 重启；

您可以通过以下方式自定义现有语言：

.. code-block:: php

    <?php
    Carbon::setLocale('en');
    $translator = Carbon::getTranslator();
    $translator->setMessages('en', array(
        'day' => ':count boring day|:count boring days',
    ));

    $date1 = Carbon::create(2018, 1, 1, 0, 0, 0);
    $date2 = Carbon::create(2018, 1, 4, 4, 0, 0);

    echo $date1->diffForHumans($date2, true, false, 2); // 3 boring days 4 hours

    $translator->resetMessages('en'); // reset language customizations for en language

请注意，只要给定的转换器实现 ``Symfony\Component\Translation\TranslatorInterface`` ，您也可以在 ``Carbon::setTranslator($custom)`` 中使用其它的转换器。

测试协助
========
测试方法允许您设置 ``now`` 方法返回的 ``Carbon`` 实例(真实或模拟)。 提供的实例将在以下情况下返回：

- 调用静态 ``now()`` 方法，例如， ``Carbon::now()`` ；
- 当一个 ``null`` （或空白字符串）被传递给构造函数或 ``parse()`` ，例如， ``Carbon(null)`` ；
- 当字符串 ``now`` 被传递给构造函数或 ``parse()`` ，例如， ``new Carbon('now')`` ；
- 给定的实例也将用作 ``diff`` 方法的默认相对时刻；

.. code-block:: php

    <?php
    $knownDate = Carbon::create(2001, 5, 21, 12);          // create testing date
    Carbon::setTestNow($knownDate);                        // set the mock (of course this could be a real mock object)
    echo Carbon::getTestNow();                             // 2001-05-21 12:00:00
    echo Carbon::now();                                    // 2001-05-21 12:00:00
    echo new Carbon();                                     // 2001-05-21 12:00:00
    echo Carbon::parse();                                  // 2001-05-21 12:00:00
    echo new Carbon('now');                                // 2001-05-21 12:00:00
    echo Carbon::parse('now');                             // 2001-05-21 12:00:00
    echo Carbon::create(2001, 4, 21, 12)->diffForHumans(); // 1 month ago
    var_dump(Carbon::hasTestNow());                        // bool(true)
    Carbon::setTestNow();                                  // clear the mock
    var_dump(Carbon::hasTestNow());                        // bool(false)
    echo Carbon::now();                                    // 2018-04-17 11:37:07

更具意义的完整例子：

.. code-block:: php

    class SeasonalProduct
    {
        protected $price;

        public function __construct($price)
        {
            $this->price = $price;
        }

        public function getPrice() {
            $multiplier = 1;
            if (Carbon::now()->month == 12) {
                $multiplier = 2;
            }

            return $this->price * $multiplier;
        }
    }

    $product = new SeasonalProduct(100);
    Carbon::setTestNow(Carbon::parse('first day of March 2000'));
    echo $product->getPrice();                                             // 100
    Carbon::setTestNow(Carbon::parse('first day of December 2000'));
    echo $product->getPrice();                                             // 200
    Carbon::setTestNow(Carbon::parse('first day of May 2000'));
    echo $product->getPrice();                                             // 100
    Carbon::setTestNow();

根据给定的 ``now`` 实例，相对短语也同样被模拟。

.. code-block:: php

    <?php
    $knownDate = Carbon::create(2001, 5, 21, 12);          // create testing date
    Carbon::setTestNow($knownDate);                        // set the mock
    echo new Carbon('tomorrow');                           // 2001-05-22 00:00:00  ... notice the time !
    echo new Carbon('yesterday');                          // 2001-05-20 00:00:00
    echo new Carbon('next wednesday');                     // 2001-05-23 00:00:00
    echo new Carbon('last friday');                        // 2001-05-18 00:00:00
    echo new Carbon('this thursday');                      // 2001-05-24 00:00:00
    Carbon::setTestNow();                                  // always clear it !

被认为是相对修饰词的单词列表是：

- \+
- \-
- ago
- first
- next
- last
- this
- today
- tomorrow
- yesterday

请注意，类似于 ``next()`` ， ``previous()`` 和 ``modify()`` 方法，这些相对修饰符中的某些会将时间设置为 ``00:00:00`` 。

``Carbon::parse($time，$tz)`` 和 ``new Carbon($time，$tz)`` 都可以将时区作为第二个参数。

.. code-block:: php

    <?php
    echo Carbon::parse('2012-9-5 23:26:11.223', 'Europe/Paris')->timezone->getName(); // Europe/Paris

Getters
=======
``getters`` 是通过 ``PHP`` 的 ``__get()`` 方法实现的。 这使您可以像访问属性而不是函数调用一样访问该值。

.. code-block:: php

    <?php
    $dt = Carbon::parse('2012-9-5 23:26:11.123789');

    // These getters specifically return integers, ie intval()
    var_dump($dt->year);                                         // int(2012)
    var_dump($dt->month);                                        // int(9)
    var_dump($dt->day);                                          // int(5)
    var_dump($dt->hour);                                         // int(23)
    var_dump($dt->minute);                                       // int(26)
    var_dump($dt->second);                                       // int(11)
    var_dump($dt->micro);                                        // int(123789)
    // dayOfWeek returns a number between 0 (sunday) and 6 (saturday)
    var_dump($dt->dayOfWeek);                                    // int(3)
    // dayOfWeekIso returns a number between 1 (monday) and 7 (sunday)
    var_dump($dt->dayOfWeekIso);                                 // int(3)
    var_dump($dt->dayOfYear);                                    // int(248)
    var_dump($dt->weekNumberInMonth);                            // int(2)
    // weekNumberInMonth consider weeks from monday to sunday, so the week 1 will
    // contain 1 day if the month start with a sunday, and up to 7 if it starts with a monday
    var_dump($dt->weekOfMonth);                                  // int(1)
    // weekOfMonth will returns 1 for the 7 first days of the month, then 2 from the 8th to
    // the 14th, 3 from the 15th to the 21st, 4 from 22nd to 28th and 5 above
    var_dump($dt->weekOfYear);                                   // int(36)
    var_dump($dt->daysInMonth);                                  // int(30)
    var_dump($dt->timestamp);                                    // int(1346901971)
    var_dump(Carbon::createFromDate(1975, 5, 21)->age);          // int(42) calculated vs now in the same tz
    var_dump($dt->quarter);                                      // int(3)

    // Returns an int of seconds difference from UTC (+/- sign included)
    var_dump(Carbon::createFromTimestampUTC(0)->offset);         // int(0)
    var_dump(Carbon::createFromTimestamp(0)->offset);            // int(-18000)

    // Returns an int of hours difference from UTC (+/- sign included)
    var_dump(Carbon::createFromTimestamp(0)->offsetHours);       // int(-5)

    // Indicates if day light savings time is on
    var_dump(Carbon::createFromDate(2012, 1, 1)->dst);           // bool(false)
    var_dump(Carbon::createFromDate(2012, 9, 1)->dst);           // bool(true)

    // Indicates if the instance is in the same timezone as the local timezone
    var_dump(Carbon::now()->local);                              // bool(true)
    var_dump(Carbon::now('America/Vancouver')->local);           // bool(false)

    // Indicates if the instance is in the UTC timezone
    var_dump(Carbon::now()->utc);                                // bool(false)
    var_dump(Carbon::now('Europe/London')->utc);                 // bool(false)
    var_dump(Carbon::createFromTimestampUTC(0)->utc);            // bool(true)

    // Gets the DateTimeZone instance
    echo get_class(Carbon::now()->timezone);                     // DateTimeZone
    echo get_class(Carbon::now()->tz);                           // DateTimeZone

    // Gets the DateTimeZone instance name, shortcut for ->timezone->getName()
    echo Carbon::now()->timezoneName;                            // America/Toronto
    echo Carbon::now()->tzName;                                  // America/Toronto

Setters
=======
以下设置器是通过 ``PHP``的 ``__set()`` 方法实现的。 这里需要注意的是，除了明确设置时区之外，所有设置都不会改变实例的时区。 具体而言，设置时间戳不会将相应的时区设置为 ``UTC`` 。

.. code-block:: php

    <?php
    $dt = Carbon::now();

    $dt->year = 1975;
    $dt->month = 13;             // would force year++ and month = 1
    $dt->month = 5;
    $dt->day = 21;
    $dt->hour = 22;
    $dt->minute = 32;
    $dt->second = 5;

    $dt->timestamp = 169957925;  // This will not change the timezone

    // Set the timezone via DateTimeZone instance or string
    $dt->timezone = new DateTimeZone('Europe/London');
    $dt->timezone = 'Europe/London';
    $dt->tz = 'Europe/London';

流式设置
========
对于 ``setter`` 来说，没有参数是可选的，但函数定义中有足够的变体，您不应该需要它们。 这里需要注意的是，除了明确设置时区之外，所有设置都不会改变实例的时区。 具体而言，设置时间戳不会将相应的时区设置为 ``UTC`` 。

.. code-block:: php

    <?php
    $dt = Carbon::now();

    $dt->year(1975)->month(5)->day(21)->hour(22)->minute(32)->second(5)->toDateTimeString();
    $dt->setDate(1975, 5, 21)->setTime(22, 32, 5)->toDateTimeString();
    $dt->setDate(1975, 5, 21)->setTimeFromTimeString('22:32:05')->toDateTimeString();
    $dt->setDateTime(1975, 5, 21, 22, 32, 5)->toDateTimeString();

    $dt->timestamp(169957925)->timezone('Europe/London');

    $dt->tz('America/Toronto')->setTimezone('America/Vancouver');

您还可以将 ``DateTime/Carbon`` 对象来分别设置一个 ``Carbon`` 对象的日期和时间：

.. code-block:: php

    <?php
    $source1 = new Carbon('2010-05-16 22:40:10');

    $dt = new Carbon('2001-01-01 01:01:01');
    $dt->setTimeFrom($source1);

    echo $dt; // 2001-01-01 22:40:10

    $source2 = new DateTime('2013-09-01 09:22:56');

    $dt->setDateFrom($source2);

    echo $dt; // 2013-09-01 22:40:10

isSet
======
``PHP`` 函数 ``__isset()`` 已实现。 这是因为一些外部系统（如 ``Twig`` ）在使用它之前验证了属性的存在。 这是通过使用 ``isset()`` 或 ``empty()`` 方法完成的。 您可以在 ``PHP`` 网站上阅读更多关于这些内容的信息： ``__isset()，isset()，empty()`` 。

.. code-block:: php

    <?php
    var_dump(isset(Carbon::now()->iDoNotExist));       // bool(false)
    var_dump(isset(Carbon::now()->hour));              // bool(true)
    var_dump(empty(Carbon::now()->iDoNotExist));       // bool(true)
    var_dump(empty(Carbon::now()->year));              // bool(false)

字符串格式化
============
所有可用的 ``toXXXString()`` 方法依赖于基类的 ``DateTime::format()`` 。你会注意到 ``__toString()`` 方法被定义了，它允许一个 ``Carbon`` 实例在字符串上下文中被打印为一个漂亮的日期时间字符串。

.. code-block:: php

    <?php
    $dt = Carbon::create(1975, 12, 25, 14, 15, 16);

    var_dump($dt->toDateTimeString() == $dt);          // bool(true) => uses __toString()
    echo $dt->toDateString();                          // 1975-12-25
    echo $dt->toFormattedDateString();                 // Dec 25, 1975
    echo $dt->toTimeString();                          // 14:15:16
    echo $dt->toDateTimeString();                      // 1975-12-25 14:15:16
    echo $dt->toDayDateTimeString();                   // Thu, Dec 25, 1975 2:15 PM

    // ... of course format() is still available
    echo $dt->format('l jS \\of F Y h:i:s A');         // Thursday 25th of December 1975 02:15:16 PM

    // The reverse hasFormat method allows you to test if a string looks like a given format
    var_dump($dt->hasFormat('Thursday 25th December 1975 02:15:16 PM', 'l jS F Y h:i:s A')); // bool(true)

您还可以设置在发生类型转换时使用的默认 ``__toString()`` 格式（默认为 ``Y-m-d H:i:s`` ）。

.. code-block:: php

    <?php
    Carbon::setToStringFormat('jS \o\f F, Y g:i:s a');
    echo $dt;                                          // 25th of December, 1975 2:15:16 pm
    Carbon::resetToStringFormat();
    echo $dt;                                          // 1975-12-25 14:15:16

一般格式化
==========
以下是 ``DateTime`` 类中提供的常用格式的封装。

.. code-block:: php

    <?php
    $dt = Carbon::createFromFormat('Y-m-d H:i:s.u', '2019-02-01 03:45:27.612584');

    // $dt->toAtomString() is the same as $dt->format(DateTime::ATOM);
    echo $dt->toAtomString();        // 2019-02-01T03:45:27-05:00
    echo $dt->toCookieString();      // Friday, 01-Feb-2019 03:45:27 EST

    echo $dt->toIso8601String();     // 2019-02-01T03:45:27-05:00
    // Be aware we chose to use the full-extended format of the ISO 8601 norm
    // Natively, DateTime::ISO8601 format is not compatible with ISO-8601 as it
    // is explained here in the PHP documentation:
    // https://php.net/manual/class.datetime.php#datetime.constants.iso8601
    // We consider it as a PHP mistake and chose not to provide method for this
    // format, but you still can use it this way:
    echo $dt->format(DateTime::ISO8601); // 2019-02-01T03:45:27-0500

    echo $dt->toIso8601ZuluString(); // 2019-02-01T08:45:27Z
    echo $dt->toRfc822String();      // Fri, 01 Feb 19 03:45:27 -0500
    echo $dt->toRfc850String();      // Friday, 01-Feb-19 03:45:27 EST
    echo $dt->toRfc1036String();     // Fri, 01 Feb 19 03:45:27 -0500
    echo $dt->toRfc1123String();     // Fri, 01 Feb 2019 03:45:27 -0500
    echo $dt->toRfc2822String();     // Fri, 01 Feb 2019 03:45:27 -0500
    echo $dt->toRfc3339String();     // 2019-02-01T03:45:27-05:00
    echo $dt->toRfc7231String();     // Fri, 01 Feb 2019 08:45:27 GMT
    echo $dt->toRssString();         // Fri, 01 Feb 2019 03:45:27 -0500
    echo $dt->toW3cString();         // 2019-02-01T03:45:27-05:00

    var_dump($dt->toArray());
    /*
    array(12) {
      ["year"]=>
      int(2019)
      ["month"]=>
      int(2)
      ["day"]=>
      int(1)
      ["dayOfWeek"]=>
      int(5)
      ["dayOfYear"]=>
      int(31)
      ["hour"]=>
      int(3)
      ["minute"]=>
      int(45)
      ["second"]=>
      int(27)
      ["micro"]=>
      int(612584)
      ["timestamp"]=>
      int(1549010727)
      ["formatted"]=>
      string(19) "2019-02-01 03:45:27"
      ["timezone"]=>
      object(DateTimeZone)#31 (2) {
        ["timezone_type"]=>
        int(3)
        ["timezone"]=>
        string(15) "America/Toronto"
      }
    }
    */

比较
====
通过以下函数提供简单的比较。 请记住，比较是在 ``UTC`` 时区完成的，所以事情并非总是如他们所见。

.. code-block:: php

    <?php
    echo Carbon::now()->tzName;                        // America/Toronto
    $first = Carbon::create(2012, 9, 5, 23, 26, 11);
    $second = Carbon::create(2012, 9, 5, 20, 26, 11, 'America/Vancouver');

    echo $first->toDateTimeString();                   // 2012-09-05 23:26:11
    echo $first->tzName;                               // America/Toronto
    echo $second->toDateTimeString();                  // 2012-09-05 20:26:11
    echo $second->tzName;                              // America/Vancouver

    var_dump($first->eq($second));                     // bool(true)
    var_dump($first->ne($second));                     // bool(false)
    var_dump($first->gt($second));                     // bool(false)
    var_dump($first->gte($second));                    // bool(true)
    var_dump($first->lt($second));                     // bool(false)
    var_dump($first->lte($second));                    // bool(true)

    $first->setDateTime(2012, 1, 1, 0, 0, 0);
    $second->setDateTime(2012, 1, 1, 0, 0, 0);         // Remember tz is 'America/Vancouver'

    var_dump($first->eq($second));                     // bool(false)
    var_dump($first->ne($second));                     // bool(true)
    var_dump($first->gt($second));                     // bool(false)
    var_dump($first->gte($second));                    // bool(false)
    var_dump($first->lt($second));                     // bool(true)
    var_dump($first->lte($second));                    // bool(true)

    // All have verbose aliases and PHP equivalent code:

    var_dump($first->eq($second));                     // bool(false)
    var_dump($first->equalTo($second));                // bool(false)
    var_dump($first == $second);                       // bool(false)

    var_dump($first->ne($second));                     // bool(true)
    var_dump($first->notEqualTo($second));             // bool(true)
    var_dump($first != $second);                       // bool(true)

    var_dump($first->gt($second));                     // bool(false)
    var_dump($first->greaterThan($second));            // bool(false)
    var_dump($first > $second);                        // bool(false)

    var_dump($first->gte($second));                    // bool(false)
    var_dump($first->greaterThanOrEqualTo($second));   // bool(false)
    var_dump($first >= $second);                       // bool(false)

    var_dump($first->lt($second));                     // bool(true)
    var_dump($first->lessThan($second));               // bool(true)
    var_dump($first < $second);                        // bool(true)

    var_dump($first->lte($second));                    // bool(true)
    var_dump($first->lessThanOrEqualTo($second));      // bool(true)
    var_dump($first <= $second);                       // bool(true)

这些方法使用由PHP ``$date1 == $date2`` 提供的自然比较，所以它们都将在 ``PHP 7.1`` 之前忽略毫秒/微秒，然后从 ``7.1`` 开始考虑它们。

要确定当前实例是否在两个其他实例之间，可以使用恰当的名称的 ``between()`` 方法。 第三个参数表示是否应该等于比较。 默认值是 ``true`` ，它确定它是在边界之间还是等于边界。

.. code-block:: php

    <?php
    $first = Carbon::create(2012, 9, 5, 1);
    $second = Carbon::create(2012, 9, 5, 5);
    var_dump(Carbon::create(2012, 9, 5, 3)->between($first, $second));          // bool(true)
    var_dump(Carbon::create(2012, 9, 5, 5)->between($first, $second));          // bool(true)
    var_dump(Carbon::create(2012, 9, 5, 5)->between($first, $second, false));   // bool(false)

哇！ 你忘了 ``min()`` 和 ``max()`` 吗？ 不。 通过适当命名的 ``min()`` 和 ``max()`` 方法或 ``minimum()`` 和 ``maximum()`` 别名也可以涵盖这一点。 像往常一样，如果指定了 ``null`` ，则默认参数是 ``now`` 。

.. code-block:: php

    <?php
    $dt1 = Carbon::createMidnightDate(2012, 1, 1);
    $dt2 = Carbon::createMidnightDate(2014, 1, 30);
    echo $dt1->min($dt2);                              // 2012-01-01 00:00:00
    echo $dt1->minimum($dt2);                          // 2012-01-01 00:00:00

    $dt1 = Carbon::createMidnightDate(2012, 1, 1);
    $dt2 = Carbon::createMidnightDate(2014, 1, 30);
    echo $dt1->max($dt2);                              // 2014-01-30 00:00:00
    echo $dt1->maximum($dt2);                          // 2014-01-30 00:00:00

    // now is the default param
    $dt1 = Carbon::createMidnightDate(2000, 1, 1);
    echo $dt1->max();                                  // 2018-04-17 11:37:07
    echo $dt1->maximum();                              // 2018-04-17 11:37:07

    $dt1 = Carbon::createMidnightDate(2010, 4, 1);
    $dt2 = Carbon::createMidnightDate(2010, 3, 28);
    $dt3 = Carbon::createMidnightDate(2010, 4, 16);

    // returns the closest of two date (no matter before or after)
    echo $dt1->closest($dt2, $dt3);                    // 2010-03-28 00:00:00
    echo $dt2->closest($dt1, $dt3);                    // 2010-04-01 00:00:00
    echo $dt3->closest($dt2, $dt1);                    // 2010-04-01 00:00:00

    // returns the farthest of two date (no matter before or after)
    echo $dt1->farthest($dt2, $dt3);                   // 2010-04-16 00:00:00
    echo $dt2->farthest($dt1, $dt3);                   // 2010-04-16 00:00:00
    echo $dt3->farthest($dt2, $dt1);                   // 2010-03-28 00:00:00

为了处理最常用的情况，有一些简单的帮助函数。 对于以某种方式与 ``now()`` （例如 ``isToday()`` ）进行比较的方法， ``now()`` 是在与实例相同的时区中创建的。

.. code-block:: php

    <?php
    $dt = Carbon::now();

    $dt->isWeekday();
    $dt->isWeekend();
    $dt->isMonday();
    $dt->isTuesday();
    $dt->isWednesday();
    $dt->isThursday();
    $dt->isFriday();
    $dt->isSaturday();
    $dt->isSunday();
    $dt->isYesterday();
    $dt->isToday();
    $dt->isTomorrow();
    $dt->isFuture();
    $dt->isPast();
    $dt->isLeapYear();
    $dt->isSameDay(Carbon::now()); // Same day of same month of same year
    $dt->isDayOfWeek(Carbon::SATURDAY); // is a saturday
    $dt->isNextWeek();
    $dt->isLastWeek();
    $dt->isLastOfMonth(); // is the last day of the month
    $dt->isCurrentMonth();
    $dt->isNextMonth();
    $dt->isLastMonth();
    $dt->isSameMonth(Carbon::createFromDate(1987, 4, 23)); // same month no matter the year than the given date
    $dt->isSameMonth(Carbon::createFromDate(1987, 4, 23), true); // same month of the same year than the given date
    $dt->isCurrentYear();
    $dt->isNextYear();
    $dt->isLastYear();
    $dt->isSameYear(Carbon::createFromDate(1987, 4, 23));
    $dt->isLongYear(); // see https://en.wikipedia.org/wiki/ISO_8601#Week_dates
    $dt->isSameAs('w', Carbon::now()); // w is the date of the week, so this will return true if now and $dt has
                                       // the same day of week (both monday or both sunday, etc.)
                                       // you can use any format and combine as much as you want.
    $born = Carbon::createFromDate(1987, 4, 23);
    $noCake = Carbon::createFromDate(2014, 9, 26);
    $yesCake = Carbon::createFromDate(2014, 4, 23);
    $overTheHill = Carbon::now()->subYears(50);
    var_dump($born->isBirthday($noCake));              // bool(false)
    var_dump($born->isBirthday($yesCake));             // bool(true)
    var_dump($overTheHill->isBirthday());              // bool(true) -> default compare it to today!

增加和减少
==========
默认的 ``DateTime`` 提供了几种不同的方法来轻松添加和减少时间。 有 ``modify()`` ， ``add()`` 和 ``sub()`` 。 ``modify()`` 接收神奇的日期/时间格式字符串， ``last day of next month`` ，它解析并应用修改，而 ``add()`` 和 ``sub()`` 使用不太明显的 ``DateInterval`` 类， ``new \DateInterval('P6YT5M')`` 。 希望在几周内没有看到您的代码后，使用这些流畅的函数将会更清晰，更易于阅读。 当然，我不会强制让你选择使用哪个，因为基类函数仍然可用。

.. code-block:: php

    <?php
    $dt = Carbon::create(2012, 1, 31, 0);

    echo $dt->toDateTimeString();            // 2012-01-31 00:00:00

    echo $dt->addCenturies(5);               // 2512-01-31 00:00:00
    echo $dt->addCentury();                  // 2612-01-31 00:00:00
    echo $dt->subCentury();                  // 2512-01-31 00:00:00
    echo $dt->subCenturies(5);               // 2012-01-31 00:00:00

    echo $dt->addYears(5);                   // 2017-01-31 00:00:00
    echo $dt->addYear();                     // 2018-01-31 00:00:00
    echo $dt->subYear();                     // 2017-01-31 00:00:00
    echo $dt->subYears(5);                   // 2012-01-31 00:00:00

    echo $dt->addQuarters(2);                // 2012-07-31 00:00:00
    echo $dt->addQuarter();                  // 2012-10-31 00:00:00
    echo $dt->subQuarter();                  // 2012-07-31 00:00:00
    echo $dt->subQuarters(2);                // 2012-01-31 00:00:00

    echo $dt->addMonths(60);                 // 2017-01-31 00:00:00
    echo $dt->addMonth();                    // 2017-03-03 00:00:00 equivalent of $dt->month($dt->month + 1); so it wraps
    echo $dt->subMonth();                    // 2017-02-03 00:00:00
    echo $dt->subMonths(60);                 // 2012-02-03 00:00:00

    echo $dt->addDays(29);                   // 2012-03-03 00:00:00
    echo $dt->addDay();                      // 2012-03-04 00:00:00
    echo $dt->subDay();                      // 2012-03-03 00:00:00
    echo $dt->subDays(29);                   // 2012-02-03 00:00:00

    echo $dt->addWeekdays(4);                // 2012-02-09 00:00:00
    echo $dt->addWeekday();                  // 2012-02-10 00:00:00
    echo $dt->subWeekday();                  // 2012-02-09 00:00:00
    echo $dt->subWeekdays(4);                // 2012-02-03 00:00:00

    echo $dt->addWeeks(3);                   // 2012-02-24 00:00:00
    echo $dt->addWeek();                     // 2012-03-02 00:00:00
    echo $dt->subWeek();                     // 2012-02-24 00:00:00
    echo $dt->subWeeks(3);                   // 2012-02-03 00:00:00

    echo $dt->addHours(24);                  // 2012-02-04 00:00:00
    echo $dt->addHour();                     // 2012-02-04 01:00:00
    echo $dt->subHour();                     // 2012-02-04 00:00:00
    echo $dt->subHours(24);                  // 2012-02-03 00:00:00

    echo $dt->addMinutes(61);                // 2012-02-03 01:01:00
    echo $dt->addMinute();                   // 2012-02-03 01:02:00
    echo $dt->subMinute();                   // 2012-02-03 01:01:00
    echo $dt->subMinutes(61);                // 2012-02-03 00:00:00

    echo $dt->addSeconds(61);                // 2012-02-03 00:01:01
    echo $dt->addSecond();                   // 2012-02-03 00:01:02
    echo $dt->subSecond();                   // 2012-02-03 00:01:01
    echo $dt->subSeconds(61);                // 2012-02-03 00:00:00

为了好玩，您还可以将负值传递给 ``addXXX()`` ，实际上这就是 ``subXXX()`` 的实现方式。

默认情况下， ``Carbon`` 依赖基础父类 ``PHP DateTime`` 行为。 结果加或减的月份可能溢出，例如：

.. code-block:: php

    <?php
    $dt = Carbon::create(2017, 1, 31, 0);

    echo $dt->copy()->addMonth();            // 2017-03-03 00:00:00
    echo $dt->copy()->subMonths(2);          // 2016-12-01 00:00:00

使用 ``Carbon::useMonthsOverflow(false)`` 方法阻止溢出。

.. code-block:: php

    <?php
    Carbon::useMonthsOverflow(false);

    $dt = Carbon::createMidnightDate(2017, 1, 31);

    echo $dt->copy()->addMonth();            // 2017-02-28 00:00:00
    echo $dt->copy()->subMonths(2);          // 2016-11-30 00:00:00

    // Call the method with true to allow overflow again
    Carbon::resetMonthsOverflow();} // same as Carbon::useMonthsOverflow(true);

``Carbon::shouldOverflowMonths()`` 允许你知道当前溢出是否开启。你同样可以使用 ``->addMonth(s)NoOverflow, ->subMonth(s)NoOverflow, ->addMonth(s)WithOverflow, ->subMonth(s)WithOverflow`` 来无关当前模式来显式使用溢出与否。

.. code-block:: php

    <?php
    Carbon::useMonthsOverflow(false);

    $dt = Carbon::createMidnightDate(2017, 1, 31);

    echo $dt->copy()->addMonthWithOverflow();          // 2017-03-03 00:00:00
    // plural addMonthsWithOverflow() method is also available
    echo $dt->copy()->subMonthsWithOverflow(2);        // 2016-12-01 00:00:00
    // singular subMonthWithOverflow() method is also available
    echo $dt->copy()->addMonthNoOverflow();            // 2017-02-28 00:00:00
    // plural addMonthsNoOverflow() method is also available
    echo $dt->copy()->subMonthsNoOverflow(2);          // 2016-11-30 00:00:00
    // singular subMonthNoOverflow() method is also available

    echo $dt->copy()->addMonth();                      // 2017-02-28 00:00:00
    echo $dt->copy()->subMonths(2);                    // 2016-11-30 00:00:00

    Carbon::useMonthsOverflow(true);

    $dt = Carbon::createMidnightDate(2017, 1, 31);

    echo $dt->copy()->addMonthWithOverflow();          // 2017-03-03 00:00:00
    echo $dt->copy()->subMonthsWithOverflow(2);        // 2016-12-01 00:00:00
    echo $dt->copy()->addMonthNoOverflow();            // 2017-02-28 00:00:00
    echo $dt->copy()->subMonthsNoOverflow(2);          // 2016-11-30 00:00:00

    echo $dt->copy()->addMonth();                      // 2017-03-03 00:00:00
    echo $dt->copy()->subMonths(2);                    // 2016-12-01 00:00:00

    Carbon::resetMonthsOverflow();}

从 1.23.0 版本开始，溢出控制对于years同样可用：

.. code-block:: php

    <?php
    Carbon::useYearsOverflow(false);

    $dt = Carbon::createMidnightDate(2020, 2, 29);

    var_dump(Carbon::shouldOverflowYears());           // bool(false)

    echo $dt->copy()->addYearWithOverflow();           // 2021-03-01 00:00:00
    // plural addYearsWithOverflow() method is also available
    echo $dt->copy()->subYearsWithOverflow(2);         // 2018-03-01 00:00:00
    // singular subYearWithOverflow() method is also available
    echo $dt->copy()->addYearNoOverflow();             // 2021-02-28 00:00:00
    // plural addYearsNoOverflow() method is also available
    echo $dt->copy()->subYearsNoOverflow(2);           // 2018-02-28 00:00:00
    // singular subYearNoOverflow() method is also available

    echo $dt->copy()->addYear();                       // 2021-02-28 00:00:00
    echo $dt->copy()->subYears(2);                     // 2018-02-28 00:00:00

    Carbon::useYearsOverflow(true);

    $dt = Carbon::createMidnightDate(2020, 2, 29);

    var_dump(Carbon::shouldOverflowYears());           // bool(true)

    echo $dt->copy()->addYearWithOverflow();           // 2021-03-01 00:00:00
    echo $dt->copy()->subYearsWithOverflow(2);         // 2018-03-01 00:00:00
    echo $dt->copy()->addYearNoOverflow();             // 2021-02-28 00:00:00
    echo $dt->copy()->subYearsNoOverflow(2);           // 2018-02-28 00:00:00

    echo $dt->copy()->addYear();                       // 2021-03-01 00:00:00
    echo $dt->copy()->subYears(2);                     // 2018-03-01 00:00:00

    Carbon::resetYearsOverflow();}

间隔
=====
由于 ``Carbon`` 扩展了 ``DateTime`` ，它继承了它的方法，比如 ``diff()`` ，它将第二个日期对象作为参数并返回一个 ``DateInterval`` 实例。

我们还提供 ``diffAsCarbonInterval()`` ，类似于 ``diff()`` ，但返回一个 ``CarbonInterval`` 实例。 检查 ``CarbonInterval`` 章节以获取更多信息。 ``Carbon`` 也为每个单元添加 ``diff`` 方法，比如 ``diffInYears()`` ， ``diffInMonths()`` 等等。 ``diffAsCarbonInterval()`` 和 ``diffIn*()`` 方法都可以接受2个可选参数：与之比较的日期（如果缺少，使用现在时间替代）以及绝对布尔选项（默认为 ``true`` ），使得该方法无论哪个日期比另一个更大，都返回绝对值。 如果设置为 ``false`` ，则当方法调用的实例大于比较日期（第一个参数或现在）时，它将返回负值。 请注意， ``diff()`` 原型是不同的：它的第一个参数（日期）是强制性的，第二个参数（绝对选项）默认为 ``false`` 。

这些函数总是返回所请求的指定时间内表示的总差值。 这不同于基类 ``diff()`` 函数，其中 ``122`` 秒的间隔将通过 ``DateInterval`` 实例返回 ``2`` 分钟和 ``2`` 秒。 ``diffInMinutes()`` 函数将简单地返回 ``2`` ，而 ``diffInSeconds()`` 将返回 ``122`` 。所有值都被截断并且不被舍入。 下面的每个函数都有一个默认的第一个参数，它是要比较的 ``Carbon`` 实例，如果您想使用 ``now()`` ，则为 ``null`` 。 第二个参数也是可选的，并且指示如果您希望返回值为绝对值或者如果传入日期小于当前实例，则可能具有 ``-`` （负）符号的相对值。 默认值为 ``true`` ，返回绝对值。

.. code-block:: php

    <?php
    echo Carbon::now('America/Vancouver')->diffInSeconds(Carbon::now('Europe/London')); // 0

    $dtOttawa = Carbon::createMidnightDate(2000, 1, 1, 'America/Toronto');
    $dtVancouver = Carbon::createMidnightDate(2000, 1, 1, 'America/Vancouver');
    echo $dtOttawa->diffInHours($dtVancouver);                             // 3
    echo $dtVancouver->diffInHours($dtOttawa);                             // 3

    echo $dtOttawa->diffInHours($dtVancouver, false);                      // 3
    echo $dtVancouver->diffInHours($dtOttawa, false);                      // -3

    $dt = Carbon::createMidnightDate(2012, 1, 31);
    echo $dt->diffInDays($dt->copy()->addMonth());                         // 31
    echo $dt->diffInDays($dt->copy()->subMonth(), false);                  // -31

    $dt = Carbon::createMidnightDate(2012, 4, 30);
    echo $dt->diffInDays($dt->copy()->addMonth());                         // 30
    echo $dt->diffInDays($dt->copy()->addWeek());                          // 7

    $dt = Carbon::createMidnightDate(2012, 1, 1);
    echo $dt->diffInMinutes($dt->copy()->addSeconds(59));                  // 0
    echo $dt->diffInMinutes($dt->copy()->addSeconds(60));                  // 1
    echo $dt->diffInMinutes($dt->copy()->addSeconds(119));                 // 1
    echo $dt->diffInMinutes($dt->copy()->addSeconds(120));                 // 2

    echo $dt->addSeconds(120)->secondsSinceMidnight();                     // 120

    $interval = $dt->diffAsCarbonInterval($dt->copy()->subYears(3), false);
    echo ($interval->invert ? 'minus ' : 'plus ') . $interval->years;      // minus 3

有关夏令时（ ``DST`` ）的重要说明，默认情况下 ``PHP DateTime`` 不考虑夏令时，这意味着例如像伦敦时间2014年3月30日这样只有23小时的一天将被计为24小时。

.. code-block:: php

    <?php
    $date = new DateTime('2014-03-30 00:00:00', new DateTimeZone('Europe/London')); // DST off
    echo $date->modify('+25 hours')->format('H:i');                   // 01:00 (DST on, 24 hours only have been actually added)

``Carbon`` 也遵循这种行为的 ``增/减/差秒/分钟/小时`` 。 但是我们提供了使用时间戳 ``实时`` 工作的方法：

.. code-block:: php

    <?php
    $date = new Carbon('2014-03-30 00:00:00', 'Europe/London');       // DST off
    echo $date->addRealHours(25)->format('H:i');                      // 02:00 (DST on)
    echo $date->diffInRealHours('2014-03-30 00:00:00');               // 25
    echo $date->diffInHours('2014-03-30 00:00:00');                   // 26
    echo $date->diffInRealMinutes('2014-03-30 00:00:00');             // 1500
    echo $date->diffInMinutes('2014-03-30 00:00:00');                 // 1560
    echo $date->diffInRealSeconds('2014-03-30 00:00:00');             // 90000
    echo $date->diffInSeconds('2014-03-30 00:00:00');                 // 93600
    echo $date->subRealHours(25)->format('H:i');                      // 00:00 (DST off)

``addRealMinutes()`` ， ``subRealMinutes()`` ， ``addRealSeconds()`` ， ``subRealSeconds()`` 及其所有单数快捷方式 ``addRealMinutes()`` ， ``subRealHour()`` ， ``addRealMinute()`` ， ``subRealMinute()`` ， ``addRealSecond()`` ， ``subRealSecond()`` 都可以使用。

还有特殊的过滤函数 ``diffInDaysFiltered()`` ， ``diffInHoursFiltered()`` 和 ``diffFiltered()`` ，可帮助您按天，小时或自定义间隔过滤差异。 例如，计算两个实例之间的周末天数：

.. code-block:: php

    <?php
    $dt = Carbon::create(2014, 1, 1);
    $dt2 = Carbon::create(2014, 12, 31);
    // 以天过滤，判断当天是否是周末，然后统计多少符合条件的天数，就获取间隔周数
    $daysForExtraCoding = $dt->diffInDaysFiltered(function(Carbon $date) {
       return $date->isWeekend(); // 计算两个实例之间的周末天数
    }, $dt2);

    echo $daysForExtraCoding;      // 104

    $dt = Carbon::create(2014, 1, 1)->endOfDay();
    $dt2 = $dt->copy()->startOfDay();
    // 以分钟过滤，判断分钟是否为0，然后统计所有符合条件的分钟，就获取间隔小时
    $littleHandRotations = $dt->diffFiltered(CarbonInterval::minute(), function(Carbon $date) {
       return $date->minute === 0;
    }, $dt2, true); // true as last parameter returns absolute value

    echo $littleHandRotations;     // 24

    $date = Carbon::now()->addSeconds(3666);

    echo $date->diffInSeconds();                       // 3666
    echo $date->diffInMinutes();                       // 61
    echo $date->diffInHours();                         // 1
    echo $date->diffInDays();                          // 0

    $date = Carbon::create(2016, 1, 5, 22, 40, 32);

    echo $date->secondsSinceMidnight();                // 81632
    echo $date->secondsUntilEndOfDay();                // 4767

    $date1 = Carbon::createMidnightDate(2016, 1, 5);
    $date2 = Carbon::createMidnightDate(2017, 3, 15);

    echo $date1->diffInDays($date2);                   // 435
    echo $date1->diffInWeekdays($date2);               // 311
    echo $date1->diffInWeekendDays($date2);            // 124
    echo $date1->diffInWeeks($date2);                  // 62
    echo $date1->diffInMonths($date2);                 // 14
    echo $date1->diffInYears($date2);                  // 1

所有 ``diffIn*`` 过滤器方法接受一个回调过滤器为必须参数和一个日期对象作为可选地第二个参数，如果未提供，则使用 ``now`` 。你可能同样传递 ``true`` 作为第三个参数来获取绝对值。

对于周/周末日的高级处理，请使用以下工具：

.. code-block:: php

    <?php
    echo implode(', ', Carbon::getDays());                       // Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday

    $saturday = new Carbon('first saturday of 2019');
    $sunday = new Carbon('first sunday of 2019');
    $monday = new Carbon('first monday of 2019');

    echo implode(', ', Carbon::getWeekendDays());                // 6, 0
    var_dump($saturday->isWeekend());                            // bool(true)
    var_dump($sunday->isWeekend());                              // bool(true)
    var_dump($monday->isWeekend());                              // bool(false)

    Carbon::setWeekendDays(array(
        Carbon::SUNDAY,
        Carbon::MONDAY,
    ));

    echo implode(', ', Carbon::getWeekendDays());                // 0, 1
    var_dump($saturday->isWeekend());                            // bool(false)
    var_dump($sunday->isWeekend());                              // bool(true)
    var_dump($monday->isWeekend());                              // bool(true)

    Carbon::setWeekendDays(array(
        Carbon::SATURDAY,
        Carbon::SUNDAY,
    ));
    // weekend days and start/end of week or not linked
    Carbon::setWeekStartsAt(Carbon::FRIDAY);
    Carbon::setWeekEndsAt(Carbon::WEDNESDAY); // and it does not need neither to precede the start

    var_dump(Carbon::getWeekStartsAt() === Carbon::FRIDAY);      // bool(true)
    var_dump(Carbon::getWeekEndsAt() === Carbon::WEDNESDAY);     // bool(true)
    echo $saturday->copy()->startOfWeek()->toRfc850String();     // Friday, 06-Apr-18 00:00:00 EDT
    echo $saturday->copy()->endOfWeek()->toRfc850String();       // Wednesday, 11-Apr-18 23:59:59 EDT

    Carbon::setWeekStartsAt(Carbon::MONDAY);
    Carbon::setWeekEndsAt(Carbon::SUNDAY);

    echo $saturday->copy()->startOfWeek()->toRfc850String();     // Monday, 02-Apr-18 00:00:00 EDT
    echo $saturday->copy()->endOfWeek()->toRfc850String();       // Sunday, 08-Apr-18 23:59:59 EDT

人类可读间隔
============
与30天前相比，人类阅读 ``1 month ago`` 更容易。这是大多数日期库中常见的功能，所以我想我也会在这里添加它。 该函数的唯一参数是另一个 ``Carbon`` 实例，如果没有指定，它当然默认为 ``now()`` 。

此方法将在实例和传入的实例的差异值之后添加一个短语。 有4种可能性：

- 将过去的值与现在的默认值进行比较时：

  + 1 hour ago
  + 5 months ago

- 将未来的值与现在的默认值进行比较时：

  + 1 hour from now
  + 5 months from now

- 将过去的值与另一个值进行比较时：

  + 1 hour before
  + 5 months before

- 将未来的值与另一个值进行比较时：

  + 1 hour after
  + 5 months after

您也可以传递 ``true`` 作为第二个参数，从现在开始删除 ``ago`` 修饰符，例如： ``diffForHumans($other，true)`` 。

如果在所使用的本地语言环境中可用，则可以将 ``true`` 作为第三个参数来使用短语法： ``diffForHumans($other，false，true)`` 。

您可以传递一个介于1和6之间的数字作为第四个参数，以获得多个部分的差异（更精确的差异）： ``diffForHumans($other，false，false，4)`` 。

``$other`` 实例可以是 ``DateTime`` ， ``Carbon`` 实例或任何实现 ``DateTimeInterface`` 的对象，如果传递字符串，它将被解析为获取 ``Carbon`` 实例，如果传递 ``null`` ，则将使用 ``Carbon::now()`` 替换。

.. code-block:: php

    <?php
    // The most typical usage is for comments
    // The instance is the date the comment was created and its being compared to default now()
    echo Carbon::now()->subDays(5)->diffForHumans();               // 5 days ago

    echo Carbon::now()->diffForHumans(Carbon::now()->subYear());   // 1 year after

    $dt = Carbon::createFromDate(2011, 8, 1);

    echo $dt->diffForHumans($dt->copy()->addMonth());                        // 1 month before
    echo $dt->diffForHumans($dt->copy()->subMonth());                        // 1 month after

    echo Carbon::now()->addSeconds(5)->diffForHumans();                      // 5 seconds from now

    echo Carbon::now()->subDays(24)->diffForHumans();                        // 3 weeks ago
    echo Carbon::now()->subDays(24)->diffForHumans(null, true);              // 3 weeks

    echo Carbon::parse('2019-08-03')->diffForHumans('2019-08-13');           // 1 week before
    echo Carbon::parse('2000-01-01 00:50:32')->diffForHumans('@946684800');  // 5 hours after

    echo Carbon::create(2018, 2, 26, 4, 29, 43)->diffForHumans(Carbon::create(2016, 6, 21, 0, 0, 0), false, false, 6); // 1 year 8 months 5 days 4 hours 29 minutes 43 seconds after

您也可以在 ``diffForHumans()`` 调用之前使用 ``Carbon::setLocale('fr')`` 更改字符串的语言环境。 请参阅本地化部分了解更多详情。

``diffForHumans()`` 的选项可以通过以下方式启用/禁用：

.. code-block:: php

    <?php
    Carbon::enableHumanDiffOption(Carbon::NO_ZERO_DIFF);
    var_dump((bool) (Carbon::getHumanDiffOptions() & Carbon::NO_ZERO_DIFF)); // bool(true)
    Carbon::disableHumanDiffOption(Carbon::NO_ZERO_DIFF);
    var_dump((bool) (Carbon::getHumanDiffOptions() & Carbon::NO_ZERO_DIFF)); // bool(false)

可用的选项：

- ``Carbon::NO_ZERO_DIFF`` (默认开启)：开启空差异为1秒；
- ``Carbon::JUST_NOW`` (默认关闭)：开启 ``now`` 到 ``now`` 的差异为 ``just now`` ；
- ``Carbon::ONE_DAY_WORDS`` (默认关闭)：开启 ``1 day from now/ago`` 为 ``yesterday/tomorrow`` ；
- ``Carbon::TWO_DAY_WORDS`` (默认关闭)：开启 ``2 days from now/ago`` 为 ``before yesterday/after`` ；

``Carbon::JUST_NOW`` ， ``Carbon::ONE_DAY_WORDS`` 和 ``Carbon::TWO_DAY_WORDS`` 现在仅适用于 ``en`` 和 ``fr`` 语言，其他语言将回退到之前的行为，直到缺少的翻译添加为止。

使用管道操作符一次开启多个选项，例如：

.. code-block:: php

    <?php
    Carbon::ONE_DAY_WORDS | Carbon::TWO_DAY_WORDS

您还可以使用 ``setHumanDiffOptions($options)`` 来禁用所有选项，然后仅激活作为参数传递的选项。

修改器
======
这些方法组对当前实例执行有用的修改。 他们中的大多数都是从他们的名字中自我解释的......或者至少应该是。 您还会注意到 ``startOfXXX()`` ， ``next()`` 和 ``previous()`` 方法将时间设置为 ``00:00:00`` ， ``endOfXXX()`` 方法将时间设置为 ``23:59:59`` 。

唯一稍微不同的是 ``average()`` 函数。 它将你的实例移动到它自己和提供的 ``Carbon`` 参数之间的中间日期。

.. code-block:: php

    <?php
    $dt = Carbon::create(2012, 1, 31, 15, 32, 45);
    echo $dt->startOfMinute();                         // 2012-01-31 15:32:00

    $dt = Carbon::create(2012, 1, 31, 15, 32, 45);
    echo $dt->endOfMinute();                           // 2012-01-31 15:32:59

    $dt = Carbon::create(2012, 1, 31, 15, 32, 45);
    echo $dt->startOfHour();                           // 2012-01-31 15:00:00

    $dt = Carbon::create(2012, 1, 31, 15, 32, 45);
    echo $dt->endOfHour();                             // 2012-01-31 15:59:59

    $dt = Carbon::create(2012, 1, 31, 15, 32, 45);
    echo Carbon::getMidDayAt();                        // 12
    echo $dt->midDay();                                // 2012-01-31 12:00:00
    Carbon::setMidDayAt(13);
    echo Carbon::getMidDayAt();                        // 13
    echo $dt->midDay();                                // 2012-01-31 13:00:00
    Carbon::setMidDayAt(12);

    $dt = Carbon::create(2012, 1, 31, 12, 0, 0);
    echo $dt->startOfDay();                            // 2012-01-31 00:00:00

    $dt = Carbon::create(2012, 1, 31, 12, 0, 0);
    echo $dt->endOfDay();                              // 2012-01-31 23:59:59

    $dt = Carbon::create(2012, 1, 31, 12, 0, 0);
    echo $dt->startOfMonth();                          // 2012-01-01 00:00:00

    $dt = Carbon::create(2012, 1, 31, 12, 0, 0);
    echo $dt->endOfMonth();                            // 2012-01-31 23:59:59

    $dt = Carbon::create(2012, 1, 31, 12, 0, 0);
    echo $dt->startOfYear();                           // 2012-01-01 00:00:00

    $dt = Carbon::create(2012, 1, 31, 12, 0, 0);
    echo $dt->endOfYear();                             // 2012-12-31 23:59:59

    $dt = Carbon::create(2012, 1, 31, 12, 0, 0);
    echo $dt->startOfDecade();                         // 2010-01-01 00:00:00

    $dt = Carbon::create(2012, 1, 31, 12, 0, 0);
    echo $dt->endOfDecade();                           // 2019-12-31 23:59:59

    $dt = Carbon::create(2012, 1, 31, 12, 0, 0);
    echo $dt->startOfCentury();                        // 2001-01-01 00:00:00

    $dt = Carbon::create(2012, 1, 31, 12, 0, 0);
    echo $dt->endOfCentury();                          // 2100-12-31 23:59:59

    $dt = Carbon::create(2012, 1, 31, 12, 0, 0);
    echo $dt->startOfWeek();                           // 2012-01-30 00:00:00
    var_dump($dt->dayOfWeek == Carbon::MONDAY);        // bool(true) : ISO8601 week starts on Monday

    $dt = Carbon::create(2012, 1, 31, 12, 0, 0);
    echo $dt->endOfWeek();                             // 2012-02-05 23:59:59
    var_dump($dt->dayOfWeek == Carbon::SUNDAY);        // bool(true) : ISO8601 week ends on Sunday

    $dt = Carbon::create(2012, 1, 31, 12, 0, 0);
    echo $dt->next(Carbon::WEDNESDAY);                 // 2012-02-01 00:00:00
    var_dump($dt->dayOfWeek == Carbon::WEDNESDAY);     // bool(true)

    $dt = Carbon::create(2012, 1, 1, 12, 0, 0);
    echo $dt->next();                                  // 2012-01-08 00:00:00

    $dt = Carbon::create(2012, 1, 31, 12, 0, 0);
    echo $dt->previous(Carbon::WEDNESDAY);             // 2012-01-25 00:00:00
    var_dump($dt->dayOfWeek == Carbon::WEDNESDAY);     // bool(true)

    $dt = Carbon::create(2012, 1, 1, 12, 0, 0);
    echo $dt->previous();                              // 2011-12-25 00:00:00

    $start = Carbon::create(2014, 1, 1, 0, 0, 0);
    $end = Carbon::create(2014, 1, 30, 0, 0, 0);
    echo $start->average($end);                        // 2014-01-15 12:00:00

    echo Carbon::create(2014, 5, 30, 0, 0, 0)->firstOfMonth();                       // 2014-05-01 00:00:00
    echo Carbon::create(2014, 5, 30, 0, 0, 0)->firstOfMonth(Carbon::MONDAY);         // 2014-05-05 00:00:00
    echo Carbon::create(2014, 5, 30, 0, 0, 0)->lastOfMonth();                        // 2014-05-31 00:00:00
    echo Carbon::create(2014, 5, 30, 0, 0, 0)->lastOfMonth(Carbon::TUESDAY);         // 2014-05-27 00:00:00
    echo Carbon::create(2014, 5, 30, 0, 0, 0)->nthOfMonth(2, Carbon::SATURDAY);      // 2014-05-10 00:00:00

    echo Carbon::create(2014, 5, 30, 0, 0, 0)->firstOfQuarter();                     // 2014-04-01 00:00:00
    echo Carbon::create(2014, 5, 30, 0, 0, 0)->firstOfQuarter(Carbon::MONDAY);       // 2014-04-07 00:00:00
    echo Carbon::create(2014, 5, 30, 0, 0, 0)->lastOfQuarter();                      // 2014-06-30 00:00:00
    echo Carbon::create(2014, 5, 30, 0, 0, 0)->lastOfQuarter(Carbon::TUESDAY);       // 2014-06-24 00:00:00
    echo Carbon::create(2014, 5, 30, 0, 0, 0)->nthOfQuarter(2, Carbon::SATURDAY);    // 2014-04-12 00:00:00
    echo Carbon::create(2014, 5, 30, 0, 0, 0)->startOfQuarter();                     // 2014-04-01 00:00:00
    echo Carbon::create(2014, 5, 30, 0, 0, 0)->endOfQuarter();                       // 2014-06-30 23:59:59

    echo Carbon::create(2014, 5, 30, 0, 0, 0)->firstOfYear();                        // 2014-01-01 00:00:00
    echo Carbon::create(2014, 5, 30, 0, 0, 0)->firstOfYear(Carbon::MONDAY);          // 2014-01-06 00:00:00
    echo Carbon::create(2014, 5, 30, 0, 0, 0)->lastOfYear();                         // 2014-12-31 00:00:00
    echo Carbon::create(2014, 5, 30, 0, 0, 0)->lastOfYear(Carbon::TUESDAY);          // 2014-12-30 00:00:00
    echo Carbon::create(2014, 5, 30, 0, 0, 0)->nthOfYear(2, Carbon::SATURDAY);       // 2014-01-11 00:00:00

    echo Carbon::create(2018, 2, 23, 0, 0, 0)->nextWeekday();                        // 2018-02-26 00:00:00
    echo Carbon::create(2018, 2, 23, 0, 0, 0)->previousWeekday();                    // 2018-02-22 00:00:00
    echo Carbon::create(2018, 2, 21, 0, 0, 0)->nextWeekendDay();                     // 2018-02-24 00:00:00
    echo Carbon::create(2018, 2, 21, 0, 0, 0)->previousWeekendDay();                 // 2018-02-18 00:00:00

常量
====
如下常量定义在 ``Carbon`` 类中。

.. code-block:: php

    <?php
    // These getters specifically return integers, ie intval()
    var_dump(Carbon::SUNDAY);                          // int(0)
    var_dump(Carbon::MONDAY);                          // int(1)
    var_dump(Carbon::TUESDAY);                         // int(2)
    var_dump(Carbon::WEDNESDAY);                       // int(3)
    var_dump(Carbon::THURSDAY);                        // int(4)
    var_dump(Carbon::FRIDAY);                          // int(5)
    var_dump(Carbon::SATURDAY);                        // int(6)

    var_dump(Carbon::YEARS_PER_CENTURY);               // int(100)
    var_dump(Carbon::YEARS_PER_DECADE);                // int(10)
    var_dump(Carbon::MONTHS_PER_YEAR);                 // int(12)
    var_dump(Carbon::WEEKS_PER_YEAR);                  // int(52)
    var_dump(Carbon::DAYS_PER_WEEK);                   // int(7)
    var_dump(Carbon::HOURS_PER_DAY);                   // int(24)
    var_dump(Carbon::MINUTES_PER_HOUR);                // int(60)
    var_dump(Carbon::SECONDS_PER_MINUTE);              // int(60)

.. code-block:: php

    <?php
    $dt = Carbon::createFromDate(2012, 10, 6);
    if ($dt->dayOfWeek === Carbon::SATURDAY) {
        echo 'Place bets on Ottawa Senators Winning!';
    }

系列化
======
``Carbon`` 实例能被系列化。

.. code-block:: php

    <?php
    $dt = Carbon::create(2012, 12, 25, 20, 30, 00, 'Europe/Moscow');

    echo serialize($dt);                                              // O:13:"Carbon\Carbon":3:{s:4:"date";s:26:"2012-12-25 20:30:00.000000";s:13:"timezone_type";i:3;s:8:"timezone";s:13:"Europe/Moscow";}
    // same as:
    echo $dt->serialize();                                            // O:13:"Carbon\Carbon":3:{s:4:"date";s:26:"2012-12-25 20:30:00.000000";s:13:"timezone_type";i:3;s:8:"timezone";s:13:"Europe/Moscow";}

    $dt = 'O:13:"Carbon\Carbon":3:{s:4:"date";s:26:"2012-12-25 20:30:00.000000";s:13:"timezone_type";i:3;s:8:"timezone";s:13:"Europe/Moscow";}';

    echo unserialize($dt)->format('Y-m-d\TH:i:s.uP T');               // 2012-12-25T20:30:00.000000+04:00 MSK
    // same as:
    echo Carbon::fromSerialized($dt)->format('Y-m-d\TH:i:s.uP T');    // 2012-12-25T20:30:00.000000+04:00 MSK

JSON
====
``Carbon`` 实例可以被编码为 JSON或者从 JSON 编码生成实例(该功能从 ``PHP 5.4+`` 开始可用)。

.. code-block:: php

    <?php
    $dt = Carbon::create(2012, 12, 25, 20, 30, 00, 'Europe/Moscow');
    echo json_encode($dt);
    // {"date":"2012-12-25 20:30:00.000000","timezone_type":3,"timezone":"Europe\/Moscow"}

    $json = '{"date":"2012-12-25 20:30:00.000000","timezone_type":3,"timezone":"Europe\/Moscow"}';
    $dt = Carbon::__set_state(json_decode($json, true));
    echo $dt->format('Y-m-d\TH:i:s.uP T');
    // 2012-12-25T20:30:00.000000+04:00 MSK

你可以使用 ``serializeUsing()`` 来定制系列化。

.. code-block:: php

    <?php
    $dt = Carbon::create(2012, 12, 25, 20, 30, 00, 'Europe/Moscow');
    Carbon::serializeUsing(function ($date) {
        return $date->getTimestamp();
    });
    echo json_encode($dt);
    /*
    1356453000
    */

    // Call serializeUsing with null to reset the serializer:
    Carbon::serializeUsing(null);

``jsonSerialize()`` 方法返回通过 ``json_encode`` 变成字符串的中间体，它也可以让你在 ``PHP 5.3`` 中兼容使用。

.. code-block:: php

    <?php
    $dt = Carbon::create(2012, 12, 25, 20, 30, 00, 'Europe/Moscow');
    echo json_encode($dt->jsonSerialize());
    // {"date":"2012-12-25 20:30:00.000000","timezone_type":3,"timezone":"Europe\/Moscow"}
    // This is equivalent to the first json_encode example but works with PHP 5.3.

    // And it can be used separately:
    var_dump($dt->jsonSerialize());
    // array(3) {
      ["date"]=>
      string(26) "2012-12-25 20:30:00.000000"
      ["timezone_type"]=>
      int(3)
      ["timezone"]=>
      string(13) "Europe/Moscow"
    }

宏
===
如果您习惯于使用 ``Laravel`` 和响应或集合等对象，则可能会熟悉宏观概念。 ``Carbon macro()`` 的工作方式与 ``Laravel MacroableTrait`` 完全相同，它将方法名称作为第一个参数，将闭包作为第二个参数。 这使所有 ``Carbon`` 实例（以及 ``Carbon`` 静态方法）上的闭包动作可以作为具有给定名称的方法。

从 ``PHP5.4`` 开始，可以在闭包中使用 ``$this`` 来引用当前实例。为了兼容 ``PHP5.3`` ，我们还为闭包添加了一个 ``$self`` 属性。 例：

.. code-block:: php

    <?php
    Carbon::macro('diffFromYear', function ($year, $self = null) {
        // this chunk is needed to be compatible on both stand-alone Carbon with PHP versions < 5.4 and Laravel
        if (!isset($self) && isset($this)) {
            $self = $this;
        }
        // end of the compatibility chunk

        return $self->diffForHumans(Carbon::create($year, 1, 1, 0, 0, 0), false, false, 3);
    });
    echo Carbon::parse('2020-01-12 12:00:00')->diffFromYear(2019);                 // 1 year 1 week 4 days after

兼容性块允许您确保宏的完全兼容性。 一个关于 ``Illuminate\Support\Carbon`` （ ``Laravel`` 包装类）的宏， ``$self`` 不会被定义，并且正如上面在 ``PHP 5.3`` 中所提到的那样 ``$this`` 这将不会被定义。 所以为了让你的宏可以在任何地方工作，只要粘贴这个 ``if`` 语句测试，如果 ``$this`` 被定义了但没有定义 ``$self`` ，如果是这样的话就拷贝它，然后在函数体中使用 ``$self`` 。

无论您是否忽略了一些可选参数，只要 ``$self`` 具有这个名称并且是最后一个参数：

.. code-block:: php

    <?php
    Carbon::macro('diffFromYear', function ($year, $absolute = false, $short = false, $parts = 1, $self = null) {
        // compatibility chunk
        if (!isset($self) && isset($this)) {
            $self = $this;
        }

        return $self->diffForHumans(Carbon::create($year, 1, 1, 0, 0, 0), $absolute, $short, $parts);
    });

    echo Carbon::parse('2020-01-12 12:00:00')->diffFromYear(2019);                 // 1 year
    echo Carbon::parse('2020-01-12 12:00:00')->diffFromYear(2019, true);           // 1yr
    echo Carbon::parse('2020-01-12 12:00:00')->diffFromYear(2019, true, true);     //
    Notice: Object of class Carbon\Carbon could not be converted to int in C:\Users\bastien.miclo\Perso\CarbonDoc\vendor\nesbot\carbon\src\Carbon\Carbon.php on line 3601
    1yr
    echo Carbon::parse('2020-01-12 12:00:00')->diffFromYear(2019, true, true, 5);  // 1yr 1w 4d 12h

宏也可以在类中分组，并且可以通过 ``mixin()`` 使用。

.. code-block:: php

    <?php
    Class BeerDayCarbonMixin
    {
        public function nextBeerDay()
        {
            return function ($self = null) {
                // compatibility chunk
                if (!isset($self) && isset($this)) {
                    $self = $this;
                }

                return $self->modify('next wednesday');
            };
        }

        public function previousBeerDay()
        {
            return function ($self = null) {
                // compatibility chunk
                if (!isset($self) && isset($this)) {
                    $self = $this;
                }

                return $self->modify('previous wednesday');
            };
        }
    }

    Carbon::mixin(new BeerDayCarbonMixin());

    $date = Carbon::parse('First saturday of December 2018');

    echo $date->previousBeerDay();                                                 // 2018-11-28 00:00:00
    echo $date->nextBeerDay();                                                     // 2018-12-05 00:00:00

您可以使用 ``hasMacro()`` 检查是否可以使用宏（包含 ``mixin`` ）。

.. code-block:: php

    <?php
    var_dump(Carbon::hasMacro('previousBeerDay'));                                 // bool(true)
    var_dump(Carbon::hasMacro('diffFromYear'));                                    // bool(true)
    var_dump(Carbon::hasMacro('dontKnowWhat'));                                    // bool(false)

以下是社区提出的一些有用的宏：

.. code-block:: php

    <?php
    Carbon::macro('isHoliday', function ($self = null) {
        // compatibility chunk
        if (!isset($self) && isset($this)) {
            $self = $this;
        }

        return in_array($self->format('d/m'), [
            '25/12', // Christmas
            '01/01', // New Year
            // ...
        ]);
    });
    var_dump(Carbon::createMidnightDate(2012, 12, 25)->isHoliday());  // bool(true)
    var_dump(Carbon::createMidnightDate(2017, 6, 25)->isHoliday());   // bool(false)
    var_dump(Carbon::createMidnightDate(2021, 1, 1)->isHoliday());    // bool(true)

CarbonInterval
==============
``Carbon`` 类继承 ``DateInterval`` 类中。

.. code-block:: php

    <?php
    class CarbonInterval extends \DateInterval
    {
        // code here
    }

你可以以如下方式创建一个实例：

.. code-block:: php

    <?php
    echo CarbonInterval::year();                           // 1 year
    echo CarbonInterval::months(3);                        // 3 months
    echo CarbonInterval::days(3)->seconds(32);             // 3 days 32 seconds
    echo CarbonInterval::weeks(3);                         // 3 weeks
    echo CarbonInterval::days(23);                         // 3 weeks 2 days
    echo CarbonInterval::create(2, 0, 5, 1, 1, 2, 7);      // 2 years 5 weeks 1 day 1 hour 2 minutes 7 seconds

如果你发现继承了另一个库的 ``\DateInterval`` ，不要害怕！你可以通过友好的 ``instance()`` 函数来创建一个 ``CarbonInterval`` 实例。

.. code-block:: php

    <?php
    $di = new \DateInterval('P1Y2M'); // <== instance from another API
    $ci = CarbonInterval::instance($di);
    echo get_class($ci);                                   // 'Carbon\CarbonInterval'
    echo $ci;                                              // 1 year 2 months

其他助手，但要小心提供的助手来处理周时只是保存了天。 周是根据当前实例的总天数计算的。

.. code-block:: php

    <?php
    echo CarbonInterval::year()->years;                    // 1
    echo CarbonInterval::year()->dayz;                     // 0
    echo CarbonInterval::days(24)->dayz;                   // 24
    echo CarbonInterval::days(24)->daysExcludeWeeks;       // 3
    echo CarbonInterval::weeks(3)->days(14)->weeks;        // 2  <-- days setter overwrites the current value
    echo CarbonInterval::weeks(3)->weeks;                  // 3
    echo CarbonInterval::minutes(3)->weeksAndDays(2, 5);   // 2 weeks 5 days 3 minutes

``CarbonInterval`` 扩展了 ``DateInterval`` ，您可以使用 ``ISO-8601`` 持续时间格式创建：

.. code-block:: php

    <?php
    $ci = CarbonInterval::create('P1Y2M3D');
    $ci = new CarbonInterval('PT0S');

由于使用了 ``fromString()`` 方法，因此可以通过人类友好的字符串创建 ``Carbon`` 间隔。

.. code-block:: php

    <?php
    CarbonInterval::fromString('2 minutes 15 seconds');
    CarbonInterval::fromString('2m 15s'); // or abbreviated

请注意，月份缩写为 ``mo`` 以区分分钟，并且整个语法不区分大小写。

有一个方便的 ``forHumans()`` 方法，它映射为 ``__toString()`` 实现 打印人类可读的时间间隔。

.. code-block:: php

    <?php
    CarbonInterval::setLocale('fr');
    echo CarbonInterval::create(2, 1)->forHumans();        // 2 ans 1 mois
    echo CarbonInterval::hour()->seconds(3);               // 1 heure 3 secondes
    CarbonInterval::setLocale('en');

如您所见，您可以使用 ``CarbonInterval::setLocale('fr')`` 更改字符串的语言环境。


`参考文档 <http://carbon.nesbot.com/docs>`_ 