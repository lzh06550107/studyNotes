****
Date
****
- ``ECMAScript`` 中的 ``Date`` 类型是在早期 ``Java`` 中的 ``java.util.Date`` 类基础上构建的。
- ``Date`` 类型使用自 ``UTC`` （Coordinated Universal Time，国际协调时间）1970 年 1 月 1 日午夜（零时）开始经过的毫秒数来保存日期
- ``Date`` 类型保存的日期能够精确到 1970 年 1 月 1 日之前或之后的 285616 年

普通函数的用法
=============
``Date`` 对象可以作为普通函数直接调用，返回一个代表当前时间的字符串。

.. code-block:: js

    Date() // "Tue Dec 01 2015 09:34:43 GMT+0800 (CST)"

注意，即使带有参数， ``Date`` 作为普通函数使用时，返回的还是当前时间。

.. code-block:: js

    Date(2000, 1, 1) // "Tue Dec 01 2015 09:34:43 GMT+0800 (CST)"

面代码说明，无论有没有参数，直接调用 ``Date`` 总是返回当前时间。

构造函数创建对象
===============
使用 ``new`` 操作符和 ``Date`` 构造函数（调用 ``Date`` 构造函数而不传递参数将自动获得当前日期和时间）

.. code-block:: js

    var now = new Date();

在调用 ``Date`` 构造函数而不传递参数的情况下，新创建的对象自动获得当前日期和时间。如果想根据特定的日期和时间创建日期对象，必须传入表示该日期的毫秒数（即从 ``UTC`` 时间1970年1月1日午夜起至该日期止经过的毫秒数）。为了简化这一计算过程， ``ECMAScript`` 提供了两个方法： ``Date.parse()`` 和 ``Date.UTC()`` 。

.. code-block:: js

	// 参数为时间零点开始计算的毫秒数
	new Date(1378218728000)
	// Tue Sep 03 2013 22:32:08 GMT+0800 (CST)

	// 参数为日期字符串
	new Date('January 6, 2013');
	// Sun Jan 06 2013 00:00:00 GMT+0800 (CST)

	// 参数为多个整数，
	// 代表年、月、日、小时、分钟、秒、毫秒
	new Date(2013, 0, 1, 0, 0, 0, 0)
	// Tue Jan 01 2013 00:00:00 GMT+0800 (CST)

关于 ``Date`` 构造函数的参数，有几点说明。

- 第一点，参数可以是负整数，代表1970年元旦之前的时间。

.. code-block:: js

    new Date(-1378218728000) // Fri Apr 30 1926 17:27:52 GMT+0800 (CST)
- 第二点，只要是能被 ``Date.parse()`` 方法解析的字符串，都可以当作参数。

.. code-block:: js

	new Date('2013-2-15')
	new Date('2013/2/15')
	new Date('02/15/2013')
	new Date('2013-FEB-15')
	new Date('FEB, 15, 2013')
	new Date('FEB 15, 2013')
	new Date('Feberuary, 15, 2013')
	new Date('Feberuary 15, 2013')
	new Date('15 Feb 2013')
	new Date('15, Feberuary, 2013')
	// Fri Feb 15 2013 00:00:00 GMT+0800 (CST)

上面多种日期字符串的写法，返回的都是同一个时间。

- 第三，参数为年、月、日等多个整数时，年和月是不能省略的，其他参数都可以省略的。也就是说，这时至少需要两个参数，因为如果只使用“年”这一个参数， ``Date`` 会将其解释为毫秒数。

.. code-block:: js

	new Date(2013) // Thu Jan 01 1970 08:00:02 GMT+0800 (CST)

上面代码中，2013被解释为毫秒数，而不是年份。

.. code-block:: js

	new Date(2013, 0)
	// Tue Jan 01 2013 00:00:00 GMT+0800 (CST)
	new Date(2013, 0, 1)
	// Tue Jan 01 2013 00:00:00 GMT+0800 (CST)
	new Date(2013, 0, 1, 0)
	// Tue Jan 01 2013 00:00:00 GMT+0800 (CST)
	new Date(2013, 0, 1, 0, 0, 0, 0)
	// Tue Jan 01 2013 00:00:00 GMT+0800 (CST)

上面代码中，不管有几个参数，返回的都是2013年1月1日零点。

最后，各个参数的取值范围如下。

- 年：使用四位数年份，比如 ``2000`` 。如果写成两位数或个位数，则加上 ``1900`` ，即 ``10`` 代表 ``1910`` 年。如果是负数，表示公元前。
- 月： ``0`` 表示一月，依次类推， ``11`` 表示 ``12`` 月。
- 日： ``1`` 到 ``31`` 。
- 小时： ``0`` 到 ``23`` 。
- 分钟： ``0`` 到 ``59`` 。
- 秒： ``0`` 到 ``59``
- 毫秒： ``0`` 到 ``999`` 。

注意，月份从 0 开始计算，但是，天数从 1 开始计算。另外，除了日期的默认值为 1 ，小时、分钟、秒钟和毫秒的默认值都是 0 。

这些参数如果超出了正常范围，会被自动折算。比如，如果月设为 15 ，就折算为下一年的 4 月。

.. code-block:: js

	new Date(2013, 15)
	// Tue Apr 01 2014 00:00:00 GMT+0800 (CST)
	new Date(2013, 0, 0)
	// Mon Dec 31 2012 00:00:00 GMT+0800 (CST)

上面代码的第二个例子，日期设为 0 ，就代表上个月的最后一天。

参数还可以使用负数，表示扣去的时间。

.. code-block:: js

	new Date(2013, -1)
	// Sat Dec 01 2012 00:00:00 GMT+0800 (CST)
	new Date(2013, 0, -1)
	// Sun Dec 30 2012 00:00:00 GMT+0800 (CST)

上面代码中，分别对月和日使用了负数，表示从基准日扣去相应的时间。

日期的运算
==========
类型自动转换时， ``Date`` 实例如果转为数值，则等于对应的毫秒数；如果转为字符串，则等于对应的日期字符串。所以，两个日期实例对象进行减法运算时，返回的是它们间隔的毫秒数；进行加法运算时，返回的是两个字符串连接而成的新字符串。

.. code-block:: js

	var d1 = new Date(2000, 2, 1);
	var d2 = new Date(2000, 3, 1);

	d2 - d1
	// 2678400000
	d2 + d1
	// "Sat Apr 01 2000 00:00:00 GMT+0800 (CST)Wed Mar 01 2000 00:00:00 GMT+0800 (CST)"

静态方法
========

Date.now()
-----------
``ECMAScript5`` 添加了 ``Data.now()`` 方法，返回当前时间距离时间零点（1970年1月1日 00:00:00 UTC）的毫秒数，相当于 ``Unix`` 时间戳乘以 1000 。这个方法简化了使用 ``Data`` 对象分析代码的工作。

.. code-block:: js

	var start = Date.now() // 1364026285194

Date.parse()
------------
``Date.parse()`` 方法接收一个表示日期的字符串参数，然后尝试根据这个字符串返回该时间距离时间零点（1970年1月1日 00:00:00）的毫秒数。ECMA-262没有定义 ``Date.parse()`` 应该支持哪种日期格式，因此这个方法的行为因实现而异，而且通常是因地区而异。将地区设置为美国的浏览器通常都接受下列日期格式：

- "月/日/年"，如 ``6/13/2004`` ;
- "英文月名日，年"，如 ``January12，2004`` ;
- "英文星期几英文月名日年时：分：秒时区"，如 ``Tue May 25 2004 00:00:00 GMT-0700`` 。
- 日期字符串应该符合 ``RFC 2822`` 和 ``ISO 8061`` 这两个标准，即 ``YYYY-MM-DDTHH:mm:ss.sssZ`` 格式，其中最后的 ``Z`` 表示时区。

例如，要为2004年5月25日创建一个日期对象，可以使用下面的代码:

.. code-block:: js

	var someDate = new Date(Date.parse("May 25, 2004"));

如果传入 ``Date.parse()`` 方法的字符串不能表示日期，那么它会返回 ``NaN`` 。实际上，如果直接将表示日期的字符串传递给 ``Date`` 构造函数，也会在后台调用 ``Date.parse()`` 。换句话说，下面的代码与前面的例子是等价的：

.. code-block:: js

	var someDate = new Date("May 25, 2004");
	Date.parse('January 26, 2011 13:51:50')
	Date.parse('Mon, 25 Dec 1995 13:30:00 GMT')
	Date.parse('Mon, 25 Dec 1995 13:30:00 +0430')
	Date.parse('2011-10-10')
	Date.parse('2011-10-10T14:48:00')

这行代码将会得到与前面相同的日期对象。


Date.UTC()
==========
``Date.UTC()`` 方法同样也返回该时间距离时间零点（1970年1月1日 00:00:00 UTC）的毫秒数，但它与 ``Date.parse()`` 在构建值时使用不同的信息。 ``Date.UTC()`` 的参数分别是年份、基于0的月份（一月是0，二月是1，以此类推）、月中的哪一天（1到31）、小时数（0到23）、分钟、秒以及毫秒数。在这些参数中，只有前两个参数（年和月）是必需的。如果没有提供月中的天数，则假设天数为1；如果省略其他参数，则统统假设为0。以下是两个使用 ``Date.UTC()`` 方法的例子：

.. code-block:: js

	// 格式
	Date.UTC(year, month[, date[, hrs[, min[, sec[, ms]]]]])

	// 用法
	Date.UTC(2011, 0, 1, 2, 3, 4, 567)
	// 1293847384567

.. code-block:: js

	//GMT时间2000年1月1日午夜零时：
	var y2k = new Date(Date.UTC(2000, 0));
	//GMT时间2005年5月5日下午5:55:55：
	var allFives = new Date(Date.UTC(2005, 4, 5, 17, 55, 55));

如同模仿 ``Date.parse()`` 一样， ``Date`` 构造函数也会模仿 ``Date.UTC()`` ，但有一点明显不同：日期和时间都基于本地时区而非 ``GMT`` 来创建。不过， ``Date`` 构造函数接收的参数仍然与 ``Date.UTC()`` 相同。因此，如果第一个参数是数值， ``Date`` 构造函数就会假设该值是日期中的年份，而第二个参数是月份，以此类推。

.. code-block:: js

	//本地时间2000年1月1日午夜零时：
	var y2k = new Date(2000, 0);
	//本地时间2005年5月5日下午5:55:55：
	var allFives = new Date(2005, 4, 5, 17, 55, 55);

继承的方法
==========
与其他引用类型一样， ``Date`` 类型也重写了 ``toLocaleString()`` 、 ``toString()`` 和 ``valueOf()`` 方法；但这些方法( ``toLocaleString()`` 、 ``toString()`` )回的值与其他类型中的方法不同。

``Date`` 类型的 ``toLocaleString()`` 方法会按照与浏览器设置的地区相适应的格式返回日期和时间。这大致意味着时间格式中会包含 ``AM`` 或 ``PM`` ，但不会包含时区信息（当然，具体的格式会因浏览器而异）。而 ``toString()`` 方法则通常返回带有时区信息的日期和时间，其中时间一般以军用时间（即小时的范围是 0 到 23 ）表示。

.. note:: 这两个方法在不同的浏览器中返回的日期和时间格式可谓大相径庭。事实上， ``toLocaleString()`` 和 ``toString()`` 的这一差别仅在调试代码时比较有用，而在显示日期和时间时没有什么价值。至于 ``Date`` 类型的 ``valueOf()`` 方法，则根本不返回字符串，而是返回日期的毫秒表示。因此，可以方便使用比较操作符（小于或大于）来比较日期值。

Date.prototype.valueOf()
------------------------
``valueOf`` 方法返回实例对象距离时间零点（1970年1月1日00:00:00 UTC）对应的毫秒数，该方法等同于 ``getTime`` 方法。

.. code-block:: js

	var d = new Date();

	d.valueOf() // 1362790014817
	d.getTime() // 1362790014817

Date.prototype.toString()
-------------------------
``toString`` 方法返回一个完整的日期字符串。

.. code-block:: js

	var d = new Date(2013, 0, 1);

	d.toString() // "Tue Jan 01 2013 00:00:00 GMT+0800 (CST)"
	d // "Tue Jan 01 2013 00:00:00 GMT+0800 (CST)"

因为 ``toString`` 是默认的调用方法，所以如果直接读取 ``Date`` 实例，就相当于调用这个方法。

日期格式化方法
=============
``Date`` 类型还有一些专门用于将日期格式化为字符串的方法，这些方法如下。

- ``toDateString()`` ——以特定于实现的格式显示星期几、月、日和年；
- ``toTimeString()`` ——以特定于实现的格式显示时、分、秒和时区；
- ``toLocaleDateString()`` ——以特定于地区的格式显示星期几、月、日和年；
- ``toLocaleTimeString()`` ——以特定于实现的格式显示时、分、秒；
- ``toUTCString()`` ——以特定于实现的格式完整的 ``UTC`` 日期。

Date.prototype.toUTCString()
----------------------------
``toUTCString`` 方法返回对应的 ``UTC`` 时间，也就是比北京时间晚 8 个小时。

.. code-block:: js

	var d = new Date(2013, 0, 1);

	d.toUTCString() // "Mon, 31 Dec 2012 16:00:00 GMT"

Date.prototype.toISOString()
-----------------------------
``toISOString`` 方法返回对应时间的 ISO8601 写法。

.. code-block:: js

	var d = new Date(2013, 0, 1);

	d.toISOString() // "2012-12-31T16:00:00.000Z"

注意， ``toISOString`` 方法返回的总是 ``UTC`` 时区的时间。

Date.prototype.toJSON()
-----------------------
``toJSON`` 方法返回一个符合 ``JSON`` 格式的 ``ISO`` 日期字符串，与 ``toISOString`` 方法的返回结果完全相同。

.. code-block:: js

	var d = new Date(2013, 0, 1);

	d.toJSON() // "2012-12-31T16:00:00.000Z"

Date.prototype.toDateString()
-----------------------------
``toDateString`` 方法返回日期字符串（不含小时、分和秒）。

.. code-block:: js

	var d = new Date(2013, 0, 1);
	d.toDateString() // "Tue Jan 01 2013"

Date.prototype.toTimeString()
-----------------------------
``toTimeString`` 方法返回时间字符串（不含年月日）。

.. code-block:: js

	var d = new Date(2013, 0, 1);
	d.toTimeString() // "00:00:00 GMT+0800 (CST)"

Date.prototype.toLocaleDateString()
-----------------------------------
``toLocaleDateString`` 方法返回一个字符串，代表日期的当地写法（不含小时、分和秒）。

.. code-block:: js

	var d = new Date(2013, 0, 1);

	d.toLocaleDateString()
	// 中文版浏览器为"2013年1月1日"
	// 英文版浏览器为"1/1/2013"

Date.prototype.toLocaleTimeString()
------------------------------------
``toLocaleTimeString`` 方法返回一个字符串，代表时间的当地写法（不含年月日）。

.. code-block:: js

	var d = new Date(2013, 0, 1);

	d.toLocaleTimeString()
	// 中文版浏览器为"上午12:00:00"
	// 英文版浏览器为"12:00:00 AM"

日期/时间组件方法
================
``UTC`` 日期指的是在没有时区偏差的情况下(将日期转换为 ``GMT`` 时间)的日期值。

- ``getTime()`` ：返回表示日期的毫秒数；与 ``valueOf()`` 方法返回的值相同
- ``setTime(毫秒)`` ：以毫秒数设置日期，会改变整个日期
- ``getFullYear()`` ：取得4位数的年份（如2007而非仅07）
- ``getUTCFullYear()`` ：返回 ``UTC`` 日期的4位数年份
- ``setFullYear(年)`` ：设置日期的年份。传入的年份值必须是4位数字（如2007而非仅07）
- ``setUTCFullYear(年)`` ：设置 ``UTC`` 日期的年份。传入的年份值必须是4位数字（如2007而非仅07）
- ``getMonth()`` ：返回日期中的月份，其中0表示一月，11表示十二月
- ``getUTCMonth()`` ：返回 ``UTC`` 日期中的月份，其中0表示一月，11表示十二月
- ``setMonth(月)`` ：设置日期的月份。传入的月份值必须大于0，超过11则增加年份
- ``setUTCMonth(月)`` ：设置 ``UTC`` 日期的月份。传入的月份值必须大于0，超过11则增加年份
- ``getDate()`` ：返回日期月份中的天数（1到31）
- ``getUTCDate()`` ：返回 ``UTC`` 日期月份中的天数（1到31）
- ``setDate(日)`` ：设置日期月份中的天数。如果传入的值超过了该月中应有的天数，则增加月份
- ``setUTCDate(日)`` ：设置 ``UTC`` 日期月份中的天数。如果传入的值超过了该月中应有的天数，则增加月份
- ``getDay()`` ：返回日期中星期的星期几（其中0表示星期日，6表示星期六）
- ``getUTCDay()`` ：返回 ``UTC`` 日期中星期的星期几（其中0表示星期日，6表示星期六）
- ``getHours()`` ：返回日期中的小时数（0到23）
- ``getUTCHours()`` ：返回 ``UTC`` 日期中的小时数（0到23）
- ``setHours(时)`` ：设置日期中的小时数。传入的值超过了23则增加月份中的天数
- ``setUTCHours(时)`` ：设置 ``UTC`` 日期中的小时数。传入的值超过了23则增加月份中的天数
- ``getMinutes()`` ：返回日期中的分钟数（0到59）
- ``getUTCMinutes()`` ：返回 ``UTC`` 日期中的分钟数（0到59）
- ``setMinutes(分)`` ：设置日期中的分钟数。传入的值超过59则增加小时数
- ``setUTCMinutes(分)`` ：设置 ``UTC`` 日期中的分钟数。传入的值超过59则增加小时数
- ``getSeconds()`` ：返回日期中的秒数（0到59）
- ``getUTCSeconds()`` ：返回 ``UTC`` 日期中的秒数（0到59）
- ``setSeconds(秒)`` ：设置日期中的秒数。传入的值超过了59会增加分钟数
- ``setUTCSeconds(秒)`` ：设置 ``UTC`` 日期中的秒数。传入的值超过了59会增加分钟数
- ``getMilliseconds()`` ：返回日期中的毫秒数
- ``getUTCMilliseconds()`` ：返回 ``UTC`` 日期中的毫秒数
- ``setMilliseconds(毫秒)`` ：设置日期中的毫秒数
- ``setUTCMilliseconds(毫秒)`` ：设置 ``UTC`` 日期中的毫秒数
- ``getTimezoneOffset()`` ：返回本地时间与 ``UTC`` 时间相差的分钟数。例如，美国东部标准时间返回 300 。在某地进入夏令时的情况下，这个值会有所变化

get 类方法
----------
``Date`` 对象提供了一系列 ``get*`` 方法，用来获取实例对象某个方面的值。

.. code-block:: js

	var d = new Date('January 6, 2013');

	d.getDate() // 6
	d.getMonth() // 0
	d.getYear() // 113
	d.getFullYear() // 2013
	d.getTimezoneOffset() // -480

上面代码中，最后一行返回 ``-480`` ，即 ``UTC`` 时间减去当前时间，单位是分钟。 ``-480`` 表示 ``UTC`` 比当前时间少 ``480`` 分钟，即当前时区比 ``UTC`` 早 ``8`` 个小时。

下面是一个例子，计算本年度还剩下多少天。

.. code-block:: js

	function leftDays() {
	  var today = new Date();
	  var endYear = new Date(today.getFullYear(), 11, 31, 23, 59, 59, 999);
	  var msPerDay = 24 * 60 * 60 * 1000;
	  return Math.round((endYear.getTime() - today.getTime()) / msPerDay);
	}

上面这些 ``get*`` 方法返回的都是当前时区的时间， ``Date`` 对象还提供了这些方法对应的 ``UTC`` 版本，用来返回 ``UTC`` 时间。

.. code-block:: js

	var d = new Date('January 6, 2013');

	d.getDate() // 6
	d.getUTCDate() // 5

上面代码中，实例对象d表示当前时区（东八时区）的1月6日0点0分0秒，这个时间对于当前时区来说是1月6日，所以 ``getDate`` 方法返回 6 ，对于 ``UTC`` 时区来说是1月5日，所以 ``getUTCDate`` 方法返回 5 。

set 类方法
----------
``Date`` 对象提供了一系列 ``set*`` 方法，用来设置实例对象的各个方面。

- ``setDate(date)`` ：设置实例对象对应的每个月的几号 （1-31） ，返回改变后毫秒时间戳。
- ``setYear(year)`` : 设置距离 1900 年的年数。
- ``setFullYear(year [, month, date])`` ：设置四位年份。
- ``setHours(hour [, min, sec, ms])`` ：设置小时 （0-23） 。
- ``setMilliseconds()`` ：设置毫秒 （0-999） 。
- ``setMinutes(min [, sec, ms])`` ：设置分钟 （0-59） 。
- ``setMonth(month [, date])`` ：设置月份 （0-11） 。
- ``setSeconds(sec [, ms])`` ：设置秒 （0-59） 。
- ``setTime(milliseconds)`` ：设置毫秒时间戳。

这些方法基本是跟 ``get*`` 方法一一对应的，但是没有 ``setDay`` 方法，因为星期几是计算出来的，而不是设置的。另外，需要注意的是，凡是涉及到设置月份，都是从 0 开始算的，即 0 是 1 月， 11 是 12 月。

.. code-block:: js

	var d = new Date ('January 6, 2013');

	d // Sun Jan 06 2013 00:00:00 GMT+0800 (CST)
	d.setDate(9) // 1357660800000
	d // Wed Jan 09 2013 00:00:00 GMT+0800 (CST)

``set*`` 方法的参数都会自动折算。以 ``setDate`` 为例，如果参数超过当月的最大天数，则向下一个月顺延，如果参数是负数，表示从上个月的最后一天开始减去的天数。

.. code-block:: js

	var d1 = new Date('January 6, 2013');

	d1.setDate(32) // 1359648000000
	d1 // Fri Feb 01 2013 00:00:00 GMT+0800 (CST)

	var d2 = new Date ('January 6, 2013');

	d.setDate(-1) // 1356796800000
	d // Sun Dec 30 2012 00:00:00 GMT+0800 (CST)

``set`` 类方法和 ``get`` 类方法，可以结合使用，得到相对时间。

.. code-block:: js

	var d = new Date();

	// 将日期向后推1000天
	d.setDate(d.getDate() + 1000);
	// 将时间设为6小时后
	d.setHours(d.getHours() + 6);
	// 将年份设为去年
	d.setFullYear(d.getFullYear() - 1);

``set*`` 系列方法除了 ``setTime()`` 和 ``setYear()`` ，都有对应的 ``UTC`` 版本，即设置 ``UTC`` 时区的时间。

.. code-block:: js

	var d = new Date('January 6, 2013');
	d.getUTCHours() // 16
	d.setUTCHours(22) // 1357423200000
	d // Sun Jan 06 2013 06:00:00 GMT+0800 (CST)

上面代码中，本地时区（东八时区）的 1 月 6 日 0 点 0 分，是 ``UTC`` 时区的前一天下午 16 点。设为 ``UTC`` 时区的 22 点以后，就变为本地时区的上午 6 点。