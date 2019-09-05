========
验证规则
========

可用验证规则
============
以下是所有可用的验证规则及其功能的清单：

accepted
--------
验证的字段必须为 ``yes`` ， ``on`` ， ``1`` ， ``true`` 。这在确认「服务条款」是否同意时相当有用。

active_url
----------
相当于使用了 ``PHP`` 函数 `dns_get_record <http://php.net/manual/zh/function.dns-get-record.php>`_ (获取指定主机的DNS记录)，验证的字段必须具有有效的 ``A`` 或 ``AAAA`` 记录。

after:date
----------
验证的字段必须是给定日期后的值。这个日期将会通过 PHP 函数 ``strtotime`` 来验证。

.. code-block:: php

    <?php
    'start_date' => 'required|date|after:tomorrow'

您可以指定另一个字段与日期进行比较，而不是传递一个日期字符串用 ``strtotime`` 进行比较：

.. code-block:: php

    <?php
    'finish_date' => 'required|date|after:start_date'

after_or_equal:date
-------------------
验证的字段的值必须等于给定日期或在其之后。更多信息请参见 ``after`` 规则。

alpha
-----
验证的字段必须完全由字母构成。

alpha_dash
----------
验证的字段可能具有字母、数字、破折号（ ``-`` ）以及下划线（ ``_`` ）。

alpha_num
---------
验证的字段必须完全是字母、数字。

array
-----
验证的字段必须是一个 PHP 数组。

before:date
-----------
验证的字段必须是给定日期之前的值。这个日期将会通过 PHP ``strtotime`` 函数来验证。

before_or_equal:date
--------------------
验证的字段的值必须等于给定日期或在其之前。这个日期将会使用 PHP ``strtotime`` 函数来验证。

between:min,max
---------------
验证的字段的大小必须在给定的 ``min`` 和 ``max`` 之间。字符串、数字、数组或是文件大小的计算方式都用 ``size`` 方法。

boolean
-------
验证的字段必须能够被转换为布尔值。可接受的参数为 ``true`` , ``false`` , ``1`` , ``0`` , ``"1"`` , 以及 ``"0"`` 。

confirmed
---------
验证的字段必须和 ``foo_confirmation`` 的字段值一致。例如，如果要验证的字段是 ``password`` ，输入中必须存在匹配的 ``password_confirmation`` 字段。

date
----
验证的字段值必须是通过 PHP 函数 ``strtotime`` 校验的有效日期。

date_equals:date
-----------------
验证的字段必须等于给定的日期。该日期会被传递到 PHP ``strtotime`` 函数。

date_format:format
-------------------
验证的字段必须与给定的格式相匹配。你应该只使用 ``date`` 或 ``date_format`` 中的 其中一个 用于验证，而不应该同时使用两者。

different:field
---------------
验证的字段值必须与字段 ( ``field`` ) 的值不同。

digits:value
------------
验证的字段必须是 数字 ，并且必须具有确切的 值。

digits_between:min,max
----------------------
验证的字段的长度必须在给定的 最小值 和 最大值 之间。

dimensions
----------
验证的文件必须是图片并且图片比例必须符合规则：

.. code-block:: php

    <?php
    'avatar' => 'dimensions:min_width=100,min_height=200'

可用的规则为: ``min_width`` ， ``max_width`` ， ``min_height`` ， ``max_height`` ， ``width`` ， ``height`` ， ``ratio`` 。

``ratio`` 约束应该表示为宽度除以高度。 这可以通过像 ``3/2`` 这样的语句或像 ``1.5`` 这样的 ``float`` 来指定：

.. code-block:: php

    <?php
    'avatar' => 'dimensions:ratio=3/2'

由于此规则需要多个参数，因此你可以 ``Rule::dimensions`` 方法来构造可读性高的规则：

.. code-block:: php

    <?php
    use Illuminate\Validation\Rule;

    Validator::make($data, [
        'avatar' => [
            'required',
            Rule::dimensions()->maxWidth(1000)->maxHeight(500)->ratio(3 / 2),
        ],
    ]);

distinct
--------
验证数组时，指定的字段不能有任何重复值。

.. code-block:: php

    'foo.*.id' => 'distinct'

email
-----
验证的字段必须符合 e-mail 地址格式。

exists:table,column
-------------------
验证的字段必须存在于给定的数据库表中。

Exists 规则的基本用法

.. code-block:: php

    <?php
    'state' => 'exists:states'

指定自定义字段名称

.. code-block:: php

    <?php
    'state' => 'exists:states,abbreviation'

如果你需要指定 ``exists`` 方法用来查询的数据库。你可以通过使用「点」语法将数据库的名称添加到数据表前面来实现这个目的：

.. code-block:: php

    <?php
    'email' => 'exists:connection.staff,email'

如果要自定义验证规则执行的查询，可以使用 ``Rule`` 类来定义规则。在这个例子中，我们使用数组指定验证规则，而不是使用 ``|`` 字符来分隔它们：

.. code-block:: php

    <?php
    use Illuminate\Validation\Rule;

    Validator::make($data, [
        'email' => [
            'required',
            Rule::exists('staff')->where(function ($query) {
                $query->where('account_id', 1);
            }),
        ],
    ]);

file
----
验证的字段必须是成功上传的文件。

filled
------
验证的字段在存在时不能为空。

image
-----
验证的文件必须是一个图像（ jpeg、png、bmp、gif、或 svg ）。

in:foo,bar,...
--------------
验证的字段必须包含在给定的值列表中。因为这个规则通常需要你 ``implode`` 一个数组， ``Rule::in`` 方法可以用来构造规则：

.. code-block:: php

    <?php
    use Illuminate\Validation\Rule;

    Validator::make($data, [
        'zones' => [
            'required',
            Rule::in(['first-zone', 'second-zone']),
        ],
    ]);

in_array:anotherfield
---------------------
验证的字段必须存在于另一个字段 ``anotherfield`` 的值中。

integer
-------
验证的字段必须是整数。

ip
--
验证的字段必须是 ``IP`` 地址。

ipv4
----
验证的字段必须是 ``IPv4`` 地址。

ipv6
----
验证的字段必须是 ``IPv6`` 地址。

json
----
验证的字段必须是有效的 ``JSON`` 字符串。

max:value
---------
验证中的字段必须小于或等于 ``value`` 。字符串、数字、数组或是文件大小的计算方式都用 ``size`` 方法进行评估。

mimetypes:text/plain,...
------------------------
验证的文件必须与给定 ``MIME`` 类型之一匹配：

.. code-block:: php

    <?php
    'video' => 'mimetypes:video/avi,video/mpeg,video/quicktime'

要确定上传文件的 ``MIME`` 类型，会读取文件的内容来判断 ``MIME`` 类型，这可能与客户端提供的 ``MIME`` 类型不同。

mimes:foo,bar,...
-----------------
验证的文件必须具有与列出的其中一个扩展名相对应的 ``MIME`` 类型。

``MIME`` 规则基本用法

.. code-block:: php

    <?php
    'photo' => 'mimes:jpeg,bmp,png'

即使你可能只需要验证指定扩展名，但此规则实际上会验证文件的 ``MIME`` 类型，其通过读取文件的内容以猜测它的 ``MIME`` 类型。

可以在以下链接中找到完整的 ``MIME`` 类型列表及其相应的扩展名：
https://svn.apache.org/repos/asf/httpd/httpd/trunk/docs/conf/mime.types

min:value
----------
验证中的字段必须具有最小值。字符串、数字、数组或是文件大小的计算方式都用 ``size`` 方法进行评估。

nullable
--------
验证的字段可以为 ``null`` 。这在验证基本数据类型时特别有用，例如可以包含空值的字符串和整数。

not_in:foo,bar,...
------------------
验证的字段不能包含在给定的值列表中。 ``Rule::notIn`` 方法可以用来构建规则：

.. code-block:: php

    <?php
    use Illuminate\Validation\Rule;

    Validator::make($data, [
        'toppings' => [
            'required',
            Rule::notIn(['sprinkles', 'cherries']),
        ],
    ]);

numeric
-------
验证的字段必须是数字。

present
-------
验证的字段必须存在于输入数据中，但可以为空。

regex:pattern
-------------
验证的字段必须与给定的正则表达式匹配。

.. note:: 当使用 ``regex`` 规则时，你必须使用数组，而不是使用 ``|`` 分隔符，特别是如果正则表达式包含 ``|`` 字符。

required
--------
验证的字段必须存在于输入数据中，而不是空。如果满足以下条件之一，则字段被视为「空」：

- 值为 ``null`` ；
- 值为空字符串；
- 值为空数组或空 ``Countable`` 对象；
- 值为无路径的上传文件；

required_if:anotherfield,value,...
----------------------------------
如果 ``anotherfield`` 字段等于任一 ``value`` ，验证的字段必须出现且不为空 。

required_unless:anotherfield,value,...
---------------------------------------
如果 ``anotherfield`` 字段不等于任一 ``value`` ，验证的字段必须出现且不为空。

required_with:foo,bar,...
-------------------------
只有在其他任一指定字段出现时，验证的字段才必须出现且不为空。

required_with_all:foo,bar,...
-----------------------------
只有在其他指定字段全部出现时，验证的字段才必须出现且不为空。

required_without:foo,bar,...
----------------------------
只在其他指定任一字段不出现时，验证的字段才必须出现且不为空。

required_without_all:foo,bar,...
--------------------------------
只有在其他指定字段全部不出现时，验证的字段才必须出现且不为空。

same:field
----------
验证的字段必须与给定字段匹配。??

size:value
----------
验证的字段必须具有与给定值匹配的大小。对于字符串， ``value`` 对应字符数。对于数字， ``value`` 对应给定的整数值。对于数组， ``size`` 对应数组的 ``count`` 值。对于文件， ``size`` 对应文件大小（单位kb）。

string
------
验证的字段必须是一个字符串。如果允许这个字段为 ``null`` ，需要给这个字段分配 ``nullable`` 规则。

timezone
--------
验证的字段必须是一个基于 PHP 函数 ``timezone_identifiers_list`` 的有效时区标识。

unique:table,column,except,idColumn
-----------------------------------
验证的字段在给定的数据库表中必须是唯一的。如果没有指定 ``column`` ，将会使用字段本身的名称。

.. note:: 你必须同时在数据库中对指定字段设置唯一索引限制，否则在此版本中并不能达到唯一目的。

指定自定义字段名称：

.. code-block:: php

    <?php
    'email' => 'unique:users,email_address'

自定义数据库连接

有时，你可能需要为验证程序创建的数据库查询设置自定义连接。上面的例子中，将 ``unique:users`` 设置为验证规则，等于使用默认数据库连接来查询数据库。如果要对其进行修改，请使用「点」方法指定连接和表名：

.. code-block:: php

    <?php
    'email' => 'unique:connection.users,email_address'

强迫 Unique 规则忽略指定 ID ：

有时，你可能希望在进行字段唯一性验证时忽略指定 ``ID`` 。例如， 在「更新个人资料」页面会包含用户名、邮箱和地点。这时你会想要验证更新的 ``E-mail`` 值是否唯一。如果用户仅更改了用户名字段而没有改 ``E-mail`` 字段，就不需要抛出验证错误，因为此用户已经是这个 ``E-mail`` 的拥有者了。

使用 ``Rule`` 类定义规则来指示验证器忽略用户的 ``ID`` 。这个例子中通过数组来指定验证规则，而不是使用 ``|`` 字符来分隔：

.. code-block:: php

    <?php
    use Illuminate\Validation\Rule;

    Validator::make($data, [
        'email' => [
            'required',
            Rule::unique('users')->ignore($user->id),
        ],
    ]);

如果你的数据表使用的主键名称不是 ``id`` ，那就在调用 ``ignore`` 方法时指定字段的名称：

.. code-block:: php

    <?php
    'email' => Rule::unique('users')->ignore($user->id, 'user_id')

增加额外的 Where 语句：

你也可以通过 ``where`` 方法指定额外的查询条件。例如， 我们添加 ``account_id`` 为 ``1`` 的约束：

.. code-block:: php

    <?php
    'email' => Rule::unique('users')->where(function ($query) {
        return $query->where('account_id', 1);
    })

url
---
验证的字段必须是有效的 ``URL`` 。

按条件增加规则
==============
存在才验证
----------
在某些情况下，只有在该字段存在于数组中时， 才可以对字段执行验证检查。可通过增加 ``sometimes`` 到规则列表来实现：

.. code-block:: php

    <?php
    $v = Validator::make($data, [
        'email' => 'sometimes|required|email',
    ]);

在上面的例子中， ``email`` 字段只有在 ``$data`` 数组中存在才会被验证。

.. tip:: 如果你尝试验证应该始终存在但可能为空的字段，请查阅 可选字段的注意事项

复杂的条件验证
--------------
有时候你可能需要增加基于更复杂的条件逻辑的验证规则。例如，你可以希望某个指定字段在另一个字段的值超过 ``100`` 时才为必填。或者当某个指定字段存在时，另外两个字段才能具有给定的值。增加这样的验证条件并不难。首先，使用 静态规则 创建一个 ``Validator`` 实例：

.. code-block:: php

    <?php
    $v = Validator::make($data, [
        'email' => 'required|email',
        'games' => 'required|numeric',
    ]);

假设我们有一个专为游戏收藏家所设计的网页应用程序。如果游戏收藏家收藏超过一百款游戏，我们会希望他们来说明下为什么他们会拥有这么多游戏。比如说他们有可能经营了一家游戏分销商店，或者只是为了享受收集的乐趣。为了在特定条件下加入此验证需求，可以在 ``Validator`` 实例中使用 ``sometimes`` 方法。

.. code-block:: php

    <?php
    $v->sometimes('reason', 'required|max:500', function ($input) {
        return $input->games >= 100;
    });

传入 ``sometimes`` 方法的第一个参数是要用来验证的字段名称。第二个参数是我们想使用的验证规则。 闭包 作为第三个参数传入，如果其返回 ``true`` ， 则额外的规则就会被加入。这个方法可以轻松地创建复杂的条件验证。你甚至可以一次对多个字段增加条件验证：

.. code-block:: php

    <?php
    $v->sometimes(['reason', 'cost'], 'required', function ($input) {
        return $input->games >= 100;
    });

.. tip::  传入 闭包 的 ``$input`` 参数是 ``Illuminate\Support\Fluent`` 的一个实例，可用来访问你的输入或文件对象。

验证数组
========
验证表单的输入为数组的字段也不难。你可以使用 「点」方法来验证数组中的属性。例如，如果传入的 HTTP 请求中包含 ``photos[profile]`` 字段， 可以如下验证：

.. code-block:: php

    <?php
    $validator = Validator::make($request->all(), [
        'photos.profile' => 'required|image',
    ]);

你也可以验证数组中的每个元素。例如，要验证指定数组输入字段中的每一个 ``email`` 是唯一的，可以这么做：

.. code-block:: php

    <?php
    $validator = Validator::make($request->all(), [
        'person.*.email' => 'email|unique:users',
        'person.*.first_name' => 'required_with:person.*.last_name',
    ]);

同理，你可以在语言文件定义验证信息时使用 ``*`` 字符，为基于数组的字段使用单个验证消息：

.. code-block:: php

    <?php
    'custom' => [
        'person.*.email' => [
            'unique' => 'Each person must have a unique e-mail address',
        ]
    ],

自定义验证规则
==============
使用规则对象
------------
Laravel 提供了许多有用的验证规则，同时也支持自定义规则。注册自定义验证规则的方法之一，就是使用规则对象。可以使用 ``Artisan`` 命令 ``make:rule`` 来生成新的规则对象。接下来，让我们用这个命令生成一个验证字符串是大写的规则。 Laravel 会将新的规则存放在 ``app/Rules`` 目录中：

.. code-block:: shell

    php artisan make:rule Uppercase

一旦创建了规则，我们就可以定义它的行为。规则对象包含两个方法： ``passes`` 和 ``message`` 。 ``passes`` 方法接收属性值和名称，并根据属性值是否符合规则而返回 ``true`` 或者 ``false`` 。 ``message`` 方法应返回验证失败时应使用的验证错误消息：

.. code-block:: php

    <?php

    namespace App\Rules;

    use Illuminate\Contracts\Validation\Rule;

    class Uppercase implements Rule
    {
        /**
         * 判断验证规则是否通过。
         *
         * @param  string  $attribute
         * @param  mixed  $value
         * @return bool
         */
        public function passes($attribute, $value)
        {
            return strtoupper($value) === $value;
        }

        /**
         * 获取验证错误消息。
         *
         * @return string
         */
        public function message()
        {
            return 'The :attribute must be uppercase.';
        }
    }

当然， 如果你希望从翻译文件中返回一个错误消息，你可以从 ``message`` 方法中调用辅助函数 ``trans`` ：

.. code-block:: php

    <?php
    /**
     * 获取验证错误消息。
     *
     * @return string
     */
    public function message()
    {
        return trans('validation.uppercase');
    }

一旦规则对象被定义好后，你可以通过将规则对象的实例和其他验证规则一起来传递给验证器：

.. code-block:: php

    <?php
    use App\Rules\Uppercase;

    $request->validate([
        'name' => ['required', new Uppercase],
    ]);

使用闭包
--------
如果你在应用程序中只需要一次自定义规则的功能，则可以使用闭包而不是规则对象。闭包接收属性的名称，属性的值以及如果验证失败应该调用的 ``$fail`` 回调：

.. code-block:: php

    <?php
    $validator = Validator::make($request->all(), [
        'title' => [
            'required',
            'max:255',
            function($attribute, $value, $fail) {
                if ($value === 'foo') {
                    return $fail($attribute.' is invalid.'); // 调用传入的回调函数，并传入验证失败的消息
                }
            },
        ],
    ]);

文件 ``\Illuminate\Validation\ClosureValidationRule``

.. code-block:: php

    <?php
    public function passes($attribute, $value)
    {
        $this->failed = false;
        // 这个方式奇怪？？
        $this->callback->__invoke($attribute, $value, function ($message) {
            $this->failed = true;

            $this->message = $message;
        });

        return ! $this->failed;
    }

使用扩展
--------
另外一个注册自定义验证规则的方法，就是使用 ``Validator`` facade 中的 ``extend`` 方法。让我们在 服务容器 中使用这个方法来注册自定义验证规则：

.. code-block:: php

    <?php
    namespace App\Providers;

    use Illuminate\Support\ServiceProvider;
    use Illuminate\Support\Facades\Validator;

    class AppServiceProvider extends ServiceProvider
    {
        /**
         * 引导任何应用服务。
         *
         * @return void
         */
        public function boot()
        {
            Validator::extend('foo', function ($attribute, $value, $parameters, $validator) {
                return $value == 'foo';
            });
        }

        /**
         * 注册服务提供器。
         *
         * @return void
         */
        public function register()
        {
            //
        }
    }

自定义的验证闭包接收四个参数：要被验证的属性名称 ``$attribute`` 、属性的值 ``$value`` 、传入验证规则的参数数组 ``$parameters`` 、及 ``Validator`` 实例。

除了使用闭包，你也可以传入类和方法到 ``extend`` 方法中：

.. code-block:: php

    Validator::extend('foo', 'FooValidator@validate');

定义错误消息
------------
你还需要为自定义规则定义错误信息。你可以使用内联自定义消息数组或者在验证语言文件中添加条目来实现这一功能。消息应该被放到数组的第一位， 而不是在只用于存放属性指定错误信息的 ``custom`` 数组内(不放置在 ``custom`` 数组内，则是针对特定验证规则的错误消息，不针对具体的属性)：

.. code-block:: php

    <?php
    // 内联自定义消息数组
    Validator::extend('foo', 'FooValidator@validate', [
        "foo" => "Your input was invalid!",
        "accepted" => "The :attribute must be accepted.",
        // 其余的验证错误消息...
    ]);

    // resources/lang/xx/validation.php文件内容
    [
      'foo' => "Your input was invalid!",
    ],

当创建一个自定义验证规则时，你可能有时候需要为错误信息定义 **自定义占位符 **。可以通过创建自定义验证器然后调用 ``Validator`` 门面上的 ``replacer`` 方法来进行该自定义占位符的替换。你可以在 服务提供者 的 ``boot`` 方法中执行如下操作：

.. code-block:: php

    <?php
    /**
     * 启动应用服务.
     *
     * @return void
     */
    public function boot()
    {
        Validator::extend(...);

        Validator::replacer('foo', function ($message, $attribute, $rule, $parameters) {
            return str_replace(...);
        });
    }

隐式扩展
--------
默认情况下， 当所要验证的属性不存在或包含由 ``required`` 规则定义的空值时，那么正常的验证规则，包括自定义扩展将不会执行。 例如， ``unique`` 规则将不会检验 ``null`` 值：

.. code-block:: php

    <?php
    $rules = ['name' => 'unique'];

    $input = ['name' => null];

    Validator::make($input, $rules)->passes(); // true

如果要求即使为空时也要验证属性，则必须要暗示属性是必须的，要创建这样一个「隐式」扩展，可以使用 ``Validator::extendImplicit()`` 方法：

.. code-block:: php

    <?php
    Validator::extendImplicit('foo', function ($attribute, $value, $parameters, $validator) {
        return $value == 'foo';
    });

.. note:: 隐式」扩展只 暗示 该属性是必需的。至于它到底是缺失的还是空值这取决于你。


比较
====

- required 验证字段必须出现在输入数据中、不能为空。
- present 验证字段必须出现在输入数据中、能为空。
- filled 验证字段可不在输入数据中。当验证字段出现在输入数据中时，不能为空。
- nullable 验证字段的值可以是 null。
