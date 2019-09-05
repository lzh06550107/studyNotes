********
表单验证机制详解
********

简介
====
Laravel 提供了几种不同的方法来验证传入应用程序的数据。默认情况下， Laravel 的控制器基类使用 ``ValidatesRequests Trait`` ，它提供了一种方便的方法使用各种强大的验证规则来验证传入的 ``HTTP`` 请求。


快速验证
========
要了解 Laravel 强大的验证功能，让我们看一个验证表单并将错误消息显示回给用户的完整示例。

定义路由
--------
首先，假设我们在 ``routes/web.php`` 文件中定义了以下路由：

.. code-block:: php

    <?php
    Route::get('post/create', 'PostController@create');

    Route::post('post', 'PostController@store');

当然， ``GET`` 路由会显示一个供用户创建一个新的博客帖子的表单，而 ``POST`` 路由会将新的博客文章存储在数据库中。

创建控制器
----------
接下来，让我们看看处理这些路由的简单控制器。 现在我们将 ``store`` 方法留空：

.. code-block:: php

    <?php

    namespace App\Http\Controllers;

    use Illuminate\Http\Request;
    use App\Http\Controllers\Controller;

    class PostController extends Controller
    {
        /**
         * 显示创建博客文章的表单。
         *
         * @return Response
         */
        public function create()
        {
            return view('post.create');
        }

        /**
         * 保存一个新的博客文章。
         *
         * @param  Request  $request
         * @return Response
         */
        public function store(Request $request)
        {
            // 验证并存储博客文章...
        }
    }

编写验证逻辑
------------
现在我们准备开始在 ``store`` 方法中编写逻辑来验证新的博客文章。为此，我们将使用 ``Illuminate\Http\Request`` 对象提供的 ``validate`` 方法 。如果验证通过，你的代码就可以正常的运行。但是如果验证失败，就会抛出异常，并自动将对应的错误响应返回给用户。在典型的 ``HTTP`` 请求的情况下，会生成一个重定向响应，而对于 ``AJAX`` 请求则会发送 ``JSON`` 响应。

让我们接着回到 ``store`` 方法来深入理解 ``validate`` 方法：

.. code-block:: php

    <?php
    /**
     * 保存一篇新的博客文章。
     *
     * @param  Request  $request
     * @return Response
     */
    public function store(Request $request)
    {
        $validatedData = $request->validate([
            'title' => 'required|unique:posts|max:255',
            'body' => 'required',
        ]);

        // 文章内容是符合规则的，存入数据库...
    }

如你所见，我们将所需的验证规则传递至 ``validate`` 方法中。另外再提醒一次，如果验证失败，会自动生成一个对应的响应。如果验证通过，那我们的控制器将会继续正常运行。

第一个验证失败后停止
^^^^^^^^^^^^^^^^^^^^
有时，你希望在某个属性第一次验证失败后停止运行验证规则。为了达到这个目的，附加 ``bail`` 规则到该属性：

.. code-block:: php

    <?php
    $request->validate([
        'title' => 'bail|required|unique:posts|max:255',
        'body' => 'required',
    ]);

在这个例子里，如果 ``title`` 字段没有通过 ``unique`` ，那么不会检查 ``max`` 规则。规则会按照分配的顺序来验证。

关于数组数据的注意事项
^^^^^^^^^^^^^^^^^^^^^^
如果你的 ``HTTP`` 请求包含一个 「嵌套」 参数（即数组），那你可以在验证规则中通过 「点」 语法来指定这些参数。

.. code-block:: php

    <?php
    $request->validate([
        'title' => 'required|unique:posts|max:255',
        'author.name' => 'required',
        'author.description' => 'required',
    ]);

显示验证错误信息
----------------
如果传入的请求参数未通过给定的验证规则呢？正如前面所提到的，Laravel 会自动把用户重定向到之前的位置。另外，所有的验证错误信息会被自动 闪存到 ``session`` 。

重申一次，我们不必在 ``GET`` 路由中将错误消息显式绑定到视图。因为 Lavarel 会检查在 ``Session`` 数据中的错误信息，并自动将其绑定到视图（如果这个视图文件存在）。而其中的变量 ``$errors`` 是 ``Illuminate\Support\MessageBag`` 的一个实例。

.. tip:: ``$errors`` 变量被 ``Web`` 中间件组提供的 ``Illuminate\View\Middleware\ShareErrorsFromSession`` 中间件绑定到视图。 当这个中间件被应用后，在你的视图中就可以获取到 ``$error`` 变量, 可以使一直假定 ``$errors`` 变量存在并且可以安全地使用。

所以，在我们的例子中，当验证失败的时候，用户将会被重定向到控制器的 ``create`` 方法，让我们在视图中显示错误信息：

.. code-block:: php

    <?php
    <!-- /resources/views/post/create.blade.php -->

    <h1>创建文章</h1>

    @if ($errors->any())
        <div class="alert alert-danger">
            <ul>
                @foreach ($errors->all() as $error)
                    <li>{{ $error }}</li>
                @endforeach
            </ul>
        </div>
    @endif

    <!-- 创建文章表单 -->

关于可选字段的注意事项
----------------------
默认情况下，Laravel 在你应用的全局中间件堆栈中包含在 ``App\Http\Kernel`` 类中的 ``TrimStrings`` 和 ``ConvertEmptyStringsToNull`` 中间件。因此，如果你不希望验证程序将 ``null`` 值视为无效的，那就将「可选」的请求字段标记为 ``nullable`` 。

.. code-block:: php

    <?php
    $request->validate([
        'title' => 'required|unique:posts|max:255',
        'body' => 'required',
        'publish_at' => 'nullable|date',
    ]);

在这个例子里，我们指定 ``publish_at`` 字段可以为 ``null`` 或者一个有效的日期格式。如果 ``nullable`` 的修饰词没有被添加到规则定义中，验证器会认为 ``null`` 是一个无效的日期格式。

.. note:: AJAX 请求 & 验证

 在这个例子中，我们使用传统的表单将数据发送到应用程序。但实际情况中，很多程序都会使用 ``AJAX`` 来发送请求。当我们对 ``AJAX`` 的请求中使用 ``validate`` 方法时， Laravel 并不会生成一个重定向响应，而是会生成一个包含所有验证错误信息的 ``JSON`` 响应。这个 ``JSON`` 响应会包含一个 ``HTTP`` 状态码 ``422`` 被发送出去。

表单验证请求
============
创建表单请求
------------
面对更复杂的验证情境中，你可以创建一个「表单请求」来处理更为复杂的逻辑。表单请求是包含验证逻辑的自定义请求类。可使用 ``Artisan`` 命令 ``make:request`` 来创建表单请求类：

.. code-block:: shell

    php artisan make:request StoreBlogPost

新生成的类保存在 ``app/Http/Requests`` 目录下。如果这个目录不存在，运行 ``make:request`` 命令时它会被创建出来。让我们添加一些验证规则到 ``rules`` 方法中：

.. code-block:: php

    <?php
    /**
     * 获取适用于请求的验证规则。
     *
     * @return array
     */
    public function rules()
    {
        return [
            'title' => 'required|unique:posts|max:255',
            'body' => 'required',
        ];
    }

验证规则是如何运行的呢？你所需要做的就是在控制器方法中类型提示传入的请求。在调用控制器方法之前验证传入的表单请求，这意味着你不需要在控制器中写任何验证逻辑：

.. code-block:: php

    <?php
    /**
     * 保存传入的博客文章。
     *
     * @param  StoreBlogPost  $request
     * @return Response
     */
    public function store(StoreBlogPost $request)
    {
        // 传入请求有效...
    }

如果验证失败，就会生成一个让用户返回到先前的位置的重定向响应。这些错误也会被闪存到 ``Session`` 中，以便这些错误都可以在页面中显示出来。如果传入的请求是 ``AJAX`` ，会向用户返回具有 ``422`` 状态代码和验证错误信息的 ``JSON`` 数据的 ``HTTP`` 响应。

添加表单请求后钩子
------------------
如果你想在表单请求「之后」添加钩子，可以使用 ``withValidator`` 方法。这个方法接收一个完整的验证构造器，允许你在验证结果返回之前调用任何方法：

.. code-block:: php

    <?php
    /**
     *  配置验证器实例。
     *
     * @param  \Illuminate\Validation\Validator  $validator
     * @return void
     */
    public function withValidator($validator)
    {
        $validator->after(function ($validator) {
            if ($this->somethingElseIsInvalid()) {
                $validator->errors()->add('field', 'Something is wrong with this field!');
            }
        });
    }

授权表单请求
------------
表单请求类内也包含了 ``authorize`` 方法。在这个方法中，你可以检查经过身份验证的用户确定其是否具有更新给定资源的权限。比方说，你可以判断用户是否拥有更新文章评论的权限：

.. code-block:: php

    <?php
    /**
     * 判断用户是否有权限做出此请求。
     *
     * @return bool
     */
    public function authorize()
    {
        $comment = Comment::find($this->route('comment'));

        return $comment && $this->user()->can('update', $comment);
    }

由于所有的表单请求都是继承了 Laravel 中的请求基类，所以我们可以使用 ``user`` 方法去获取当前认证登录的用户。同时请注意上述例子中对 ``route`` 方法的调用。这个方法允许你在被调用的路由上获取其定义的 ``URI`` 参数，譬如下面例子中的 ``{comment}`` 参数：

.. code-block:: php

    <?php
    Route::post('comment/{comment}');

如果 ``authorize`` 方法返回 ``false`` ，则会自动返回一个包含 ``403`` 状态码的 ``HTTP`` 响应，也不会运行控制器的方法。

如果你打算在应用程序的其它部分也能处理授权逻辑，只需从 ``authorize`` 方法返回 ``true`` ：

.. code-block:: php

    <?php
    /**
     * 判断用户是否有权限进行此请求。
     *
     * @return bool
     */
    public function authorize()
    {
        return true;
    }

自定义错误信息
--------------
你可以通过重写表单请求的 ``messages`` 方法来自定义错误消息。此方法应该如下所示返回属性/规则对数组及其对应错误消息：

.. code-block:: php

    <?php
    /**
     * 获取已定义的验证规则的错误消息。
     *
     * @return array
     */
    public function messages()
    {
        return [
            'title.required' => 'A :attribute is required',
            'body.required'  => 'A :attribute is required',
        ];
    }

错误消息中可以使用属性占位符，占位符代表属性显示格式：

- ``:attribute`` 属性名称全部小写；
- ``:ATTRIBUTE`` 属性名称全部大写；
- ``:Attribute`` 属性名称首字母大写；

自定义字段名称
--------------
你可以通过重写表单请求的 attributes 方法来自定义错误字段名称。

.. code-block:: php

    <?php
    public function attributes()
    {
        return [
            "title" => "anotherTitle",
            "body" => "anotherBody",
        ];
    }

此外，我们还可以在语言文件中自定义错误字段名称。 参考下面的 `在语言文件中指定自定义属性`_

手动创建验证器
==============
如果你不想要使用请求上使用 ``validate`` 方法，你可以通过 validator ``Validator`` facade 手动创建一个验证器实例。用 Facade 上的 ``make`` 方法生成一个新的验证器实例：

.. code-block:: php

    <?php

    namespace App\Http\Controllers;

    use Validator;
    use Illuminate\Http\Request;
    use App\Http\Controllers\Controller;

    class PostController extends Controller
    {
        /**
         * 保存一篇新的博客文章。
         *
         * @param  Request  $request
         * @return Response
         */
        public function store(Request $request)
        {
            $validator = Validator::make($request->all(), [
                'title' => 'required|unique:posts|max:255',
                'body' => 'required',
            ]);

            if ($validator->fails()) {
                return redirect('post/create')
                            ->withErrors($validator)
                            ->withInput();
            }

            // 保存文章...
        }
    }

传给 ``make`` 方法的第一个参数是要验证的数据。第二个参数则是该数据的验证规则。

如果请求没有通过验证，则可以使用 ``withErrors`` 方法把错误消息闪存到 ``Session`` 。使用这个方法进行重定向之后， ``$errors`` 变量会自动与视图中共享，你可以将这些消息显示给用户。 ``withErrors`` 方法接收验证器、 ``MessageBag`` 或 PHP ``array`` 。

自动重定向
----------
如果你想手动创建验证器实例，又想利用请求中 ``validates`` 方法提供的自动重定向，那么你可以在现有的验证器实例上调用 ``validate`` 方法。如果验证失败，用户会自动重定向，如果是 ``AJAX`` 请求，将会返回 ``JSON`` 格式的响应：

.. code-block:: php

    <?php
    Validator::make($request->all(), [
        'title' => 'required|unique:posts|max:255',
        'body' => 'required',
    ])->validate();

命名错误包
----------
如果你一个页面中有多个表单，你可以命名错误信息的 ``MessageBag`` 来检索特定表单的错误消息。只需给 ``withErrors`` 方法传递一个名字作为第二个参数：

.. code-block:: php

    <?php
    return redirect('register')
                ->withErrors($validator, 'login');

然后你能从 ``$errors`` 变量中获取命名的 ``MessageBag`` 实例：

.. code-block:: php

    <?php
    {{ $errors->login->first('email') }}

验证后钩子
----------
验证器还允许你添加在验证完成之后运行的回调函数。以便你进行进一步的验证，甚至是在消息集合中添加更多的错误消息。使用它只需在验证实例上使用 ``after`` 方法：

.. code-block:: php

    <?php
    $validator = Validator::make(...);

    $validator->after(function ($validator) {
        if ($this->somethingElseIsInvalid()) {
            $validator->errors()->add('field', 'Something is wrong with this field!');
        }
    });

    if ($validator->fails()) {
        //
    }

处理错误消息
============
在 ``Validator`` 实例上调用 ``errors`` 方法后，会得到一个 ``Illuminate\Support\MessageBag`` 实例，该实例具有各种方便的处理错误消息的方法。 ``$errors`` 变量是自动提供给所有视图的 ``MessageBag`` 类的一个实例。

查看特定字段的第一个错误消息
----------------------------
如果要查看特定字段的第一个错误消息，可以使用 ``first`` 方法：

.. code-block:: php

    <?php
    $errors = $validator->errors();

    echo $errors->first('email');

查看特定字段的所有错误消息
--------------------------
如果你想以数组的形式获取指定字段的所有错误消息，则可以使用 ``get`` 方法：

.. code-block:: php

    <?php
    foreach ($errors->get('email') as $message) {
        //
    }

如果要验证表单的数组字段，你可以使用 ``*`` 来获取每个数组元素的所有错误消息：

.. code-block:: php

    <?php
    foreach ($errors->get('attachments.*') as $message) {
        //
    }

查看所有字段的错误消息
----------------------
如果你想要得到所有字段的错误消息，可以使用 ``all`` 方法：

.. code-block:: php

    <?php
    foreach ($errors->all() as $message) {
        //
    }

判断特定字段是否含有错误消息
----------------------------
可以使用 ``has`` 方法来检测一个给定的字段是否存在错误消息：

.. code-block:: php

    <?php
    if ($errors->has('email')) {
        //
    }

自定义错误消息
--------------
如果有需要的话，你也可以自定义错误消息取代默认值进行验证。有几种方法可以指定自定义消息。首先，你可以将自定义消息作为第三个参数传递给 ``Validator::make`` 方法：

.. code-block:: php

    <?php
    $messages = [
        'required' => 'The :attribute field is required.',
    ];

    $validator = Validator::make($input, $rules, $messages);

在这个例子中，``:attribute`` 占位符会被验证字段的实际名称取代。除此之外，你还可以在验证消息中使用其它占位符。例如：

.. code-block:: php

    <?php
    $messages = [
        'same'    => 'The :attribute and :other must match.',
        'size'    => 'The :attribute must be exactly :size.',
        'between' => 'The :attribute value :input is not between :min - :max.',
        'in'      => 'The :attribute must be one of the following types: :values',
    ];


为给定属性指定自定义消息
^^^^^^^^^^^^^^^^^^^^^^^^
有时候你可能只想为特定的字段自定义错误消息。只需在属性名称后使用「点」语法来指定验证的规则即可：

.. code-block:: php

    <?php
    $messages = [
        'email.required' => 'We need to know your e-mail address!',
    ];

在语言文件中指定自定义消息
^^^^^^^^^^^^^^^^^^^^^^^^^^
现实中大多数情况下，我们可能不仅仅只是将自定义消息传递给 ``Validator`` ，而是想要会使用不同的语言文件来指定自定义消息。实现它需要在 ``resources/lang/xx/validation.php`` 语言文件中将定制的消息添加到 ``custom`` 数组。

.. code-block:: php

    <?php
    'custom' => [
        'email' => [
            'required' => 'We need to know your e-mail address!',
        ],
    ],

在语言文件中指定自定义属性
^^^^^^^^^^^^^^^^^^^^^^^^^^
如果要使用自定义属性名称替换验证消息的 ``:attribute`` 部分，就在 ``resources/lang/xx/validation.php`` 语言文件的 ``attributes`` 数组中指定自定义名称：

.. code-block:: php

    <?php
    'attributes' => [
        'email' => 'email address',
    ],









