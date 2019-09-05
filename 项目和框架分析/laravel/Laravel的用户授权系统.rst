*******************
Laravel用户授权系统
*******************

.. contents:: 目录
   :depth: 4

简介
====
除了内置开箱即用的 用户认证 服务外， ``Laravel`` 还提供一种更简单的方式来处理用户授权动作。类似用户认证， ``Laravel`` 有 2 种主要方式来实现用户授权： ``gates`` 和策略。

可以把 ``gates`` 和策略类比于路由和控制器。 ``Gates`` 提供了一个简单的、基于闭包的方式来授权认证。策略则和控制器类似，在特定的模型或者资源中通过分组来实现授权认证的逻辑。我们先来看看 ``gates`` ，然后再看策略。

在你的应用中，不要将 ``gates`` 和策略当作相互排斥的方式。大部分应用很可能同时包含 ``gates`` 和策略，并且能很好的工作。 ``Gates`` **大部分应用在模型和资源无关的地方，比如查看管理员的面板。与之相反，策略应该用在特定的模型或者资源中。**


Gates
=====
编写 Gates
----------
``Gates`` 是用来决定用户是否授权执行给定的动作的闭包函数，并且典型的做法是在 ``App\Providers\AuthServiceProvider`` 类中使用 ``Gate`` 门面定义。 ``Gates`` 接受一个用户实例作为第一个参数，并且可以接受可选参数，比如相关的 ``Eloquent`` 模型：

.. code-block:: php

    <?php
    /**
     * 注册任意用户认证、用户授权服务。
     *
     * @return void
     */
    public function boot()
    {
        $this->registerPolicies();

        Gate::define('update-post', function ($user, $post) {
            return $user->id == $post->user_id;
        });
    }

``Gates`` 也可以使用 ``Class@method`` 风格的回调字符串来定义，比如控制器:

.. code-block:: php

    <?php
    /**
     * Register any authentication / authorization services.
     *
     * @return void
     */
    public function boot()
    {
        $this->registerPolicies();

        Gate::define('update-post', 'PostPolicy@update');
    }

资源 Gates
----------
你还可以使用 ``resource`` 方法一次性定义多个 Gate 功能:

.. code-block:: php

    <?php
    Gate::resource('posts', 'PostPolicy');

这与手动编写以下 Gate 定义相同：

.. code-block:: php

    <?php
    Gate::define('posts.view', 'PostPolicy@view');
    Gate::define('posts.create', 'PostPolicy@create');
    Gate::define('posts.update', 'PostPolicy@update');
    Gate::define('posts.delete', 'PostPolicy@delete');

默认情况下将会定义 ``view`` ， ``create`` ， ``update`` ，和 ``delete`` 功能。 通过将数组作为第三个参数传递给 ``resource`` 方法，您可以覆盖或添加新功能到默认的功能。 数组的键定义能力的名称，而值定义方法名称。 例如，以下代码将创建两个新的 ``Gate`` 定义： ``posts.image`` 和 ``posts.photo`` ：

.. code-block:: php

    <?php
    Gate::resource('posts', 'PostPolicy', [
        'image' => 'updateImage',
        'photo' => 'updatePhoto',
    ]);

授权动作
--------
使用 ``gates`` 来授权动作时，应使用 ``allows`` 或 ``denies`` 方法。注意你并不需要传递当前认证通过的用户给这些方法。 ``Laravel``  会自动处理好传入的用户，然后传递给 ``gate`` 闭包函数：

.. code-block:: php

    <?php
    if (Gate::allows('update-post', $post)) {
        // 指定用户可以更新博客...
    }

    if (Gate::denies('update-post', $post)) {
        // 指定用户不能更新博客...
    }

如果需要指定一个特定用户是否可以访问某个动作，可以使用 ``Gate`` 门面中的 ``forUser`` 方法：

.. code-block:: php

    <?php
    if (Gate::forUser($user)->allows('update-post', $post)) {
        // 指定用户可以更新博客...
    }

    if (Gate::forUser($user)->denies('update-post', $post)) {
        // 指定用户不能更新博客...
    }

策略
====

生成策略
--------
**策略是在特定模型或者资源中组织授权逻辑的类。** 例如，如果你的应用是一个博客，会有一个 ``Post`` 模型和一个相应的 ``PostPolicy`` 来授权用户动作，比如创建或者更新博客。

可以使用 ``make:policy artisan`` 命令来生成策略。生成的策略将放置在 ``app/Policies`` 目录。如果在你的应用中不存在这个目录，那么 ``Laravel`` 会自动创建：

.. code-block:: shell

    php artisan make:policy PostPolicy

``make:policy`` 会生成空的策略类。如果希望生成的类包含基本的「CRUD」策略方法， 可以在使用命令时指定 ``--model`` 选项，会自动包含 ``view`` 、 ``create`` 、 ``update`` 和 ``delete`` 动作：

.. code-block:: shell

    php artisan make:policy PostPolicy --model=Post

.. note:: 所有授权策略会通过 ``Laravel`` 服务容器 解析，意指你可以在授权策略的构造器对任何需要的依赖使用类型提示，它们将会被自动注入。

注册策略
--------
一旦该授权策略存在，需要将它进行注册。新的 ``Laravel`` 应用中包含的 ``AuthServiceProvider`` 包含了一个 ``policies`` 属性，可将各种模型对应至管理它们的授权策略。注册一个策略将引导 ``Laravel`` 在授权动作访问指定模型时使用何种策略：

.. code-block:: php

    <?php
    namespace App\Providers;

    use App\Post;
    use App\Policies\PostPolicy;
    use Illuminate\Support\Facades\Gate;
    use Illuminate\Foundation\Support\Providers\AuthServiceProvider as ServiceProvider;

    class AuthServiceProvider extends ServiceProvider
    {
        /**
         * 应用的策略映射。
         *
         * @var array
         */
        protected $policies = [
            Post::class => PostPolicy::class,
        ];

        /**
         * 注册任意用户认证、用户授权服务。
         *
         * @return void
         */
        public function boot()
        {
            $this->registerPolicies(); // 在底层，使用Gate来注册策略

            //
        }
    }

编写策略方法
------------
一旦授权策略被生成且注册，我们就可以为授权的每个动作添加方法。例如，让我们在 ``PostPolicy`` 中定义一个 ``update`` 方法，它会判断指定的 ``User`` 是否可以更新指定的 ``Post`` 实例。

``update`` 方法接受 ``User`` 和 ``Post`` 实例作为参数，并且应当返回 ``true`` 或 ``false`` 来指明用户是否授权更新指定的 ``Post`` 。因此，这个例子中，我们判断用户的 ``id`` 是否和 ``post`` 中的 ``user_id`` 匹配：

.. code-block:: php

    <?php
    namespace App\Policies;

    use App\User;
    use App\Post;

    class PostPolicy
    {
        /**
         * 判断指定博客能否被用户更新。
         *
         * @param  \App\User  $user
         * @param  \App\Post  $post
         * @return bool
         */
        public function update(User $user, Post $post)
        {
            return $user->id === $post->user_id;
        }
    }

你可以继续为此授权策略定义额外的方法，作为各种权限所需要的授权。例如，你可以定义 ``view`` 或 ``delete`` 方法来授权 ``Post`` 的多种行为。也可以为自定义策略方法使用自己喜欢的名字。

不包含模型方法
^^^^^^^^^^^^^^
一些策略方法只接受当前认证通过的用户作为参数而不用传入授权相关的模型实例。最普遍的应用场景就是授权 ``create`` 动作。例如，如果正在创建一篇博客，你可能希望检查一下当前用户是否有权创建博客。

当定义一个不需要传入模型实例的策略方法时，比如 ``create`` 方法，你需要定义这个方法只接受已授权的用户作为参数：

.. code-block:: php

    <?php
    /**
     * 判断指定用户是否可以创建博客。
     *
     * @param  \App\User  $user
     * @return bool
     */
    public function create(User $user) // 不用传入模型实例
    {
        //
    }

策略过滤器
^^^^^^^^^^
对特定用户，你可能希望通过指定的策略授权所有动作。 要达到这个目的，可以在策略中定义一个 ``before`` 方法。 ``before`` 方法会在策略中其它所有方法之前执行，这样提供了一种方式来授权动作而不是指定的策略方法来执行判断。这个功能最常见的场景是授权应用的管理员可以访问所有动作：

.. code-block:: php

    <?php
    public function before($user, $ability)
    {
        if ($user->isSuperAdmin()) {
            return true;
        }
    }

如果你想拒绝用户所有的授权，你应该在 ``before`` 方法中返回 ``false`` 。如果返回的是 ``null`` ，则通过其它的策略方法来决定授权与否。

使用策略授权动作
----------------

通过中间件
^^^^^^^^^^
``Laravel`` 包含一个可以在请求到达路由或控制器之前就进行动作授权的中间件。默认， ``Illuminate\Auth\Middleware\Authorize`` 中间件被指定到 ``App\Http\Kernel`` 类中 ``can`` 键上。我们用一个授权用户更新博客的例子来讲解 ``can`` 中间件的使用：

.. code-block:: php

    <?php
    use App\Post;

    Route::put('/post/{post}', function (Post $post) {
        // 当前用户可以更新博客...
    })->middleware('can:update,post');

在这个例子中，我们传递给 ``can('can' => \Illuminate\Auth\Middleware\Authorize::class)`` 中间件 2 个参数。 **第一个是需要授权的动作的名称，第二个是我们希望传递给策略方法的路由参数。** 这里因为使用了隐式模型绑定，一个 ``Post`` 会被传递给策略方法。**如果用户不被授权访问指定的动作，这个中间件会生成带有 403 状态码的 HTTP 响应。**

.. note:: 这里有个问题，就是中间件只能接收一个权限字符串，且第二个参数可以是路由参数变量名称。

不需要指定模型的动作
""""""""""""""""""""
同样的，一些动作，比如 ``create`` ，并不需要指定模型实例。 **在这种情况下，可传递一个类名给中间件。当授权动作时，这个类名将被用来判断使用哪个策略** ：

.. code-block:: php

    <?php
    Route::post('/post', function () {
        // 当前用户可以创建博客...
    })->middleware('can:create,App\Post'); // 这里需要传入类名

通过Gate门面
^^^^^^^^^^^^

.. code-block:: php

    <?php
    Route::put('posts/{post}', function (App\Post $post) {
       abort_unless(Gate::allows('update', $post), 403); 抛出403响应除非Gate运行更新帖子

       $post->update(request()->input());
    });

我们通过 ``Gate`` 来调用我们策略的更新方法，并将当前通过身份验证的用户以及给定的任务（如果用户未登录， ``Gate`` 将自动拒绝所有查询能力操作）传递它。如果这个能力没有被授予，我们会以403中止。

通过控制器辅助函数
^^^^^^^^^^^^^^^^^^^
除了在 ``User`` 模型中提供辅助方法外， ``Laravel`` 也为所有继承了 ``App\Http\Controllers\Controller`` 基类的控制器提供了一个有用的 ``authorize`` 方法。和 ``can`` 方法类似，这个方法接收需要授权的动作和相关的模型作为参数。如果动作不被授权， ``authorize`` 方法会抛出 ``Illuminate\Auth\Access\AuthorizationException`` 异常，然后被 ``Laravel`` 默认的异常处理器转化为带有 ``403`` 状态码的 ``HTTP`` 响应：

.. code-block:: php

    <?php
    namespace App\Http\Controllers;

    use App\Post;
    use Illuminate\Http\Request;
    use App\Http\Controllers\Controller;

    class PostController extends Controller
    {
        /**
         * 更新指定博客。
         *
         * @param  Request  $request
         * @param  Post  $post
         * @return Response
         */
        public function update(Request $request, Post $post)
        {
            $this->authorize('update', $post); //未被授权则抛出异常

            // 或则
            $this->authorize($post);
            // 当前用户可以更新博客...
        }
    }

.. note:: 在控制器方法中进行授权验证时， ``authorize`` 方法中不用提供第一个权限名称参数，该方法会自定猜测，并传入。



不需要指定模型的动作
""""""""""""""""""""
和之前讨论的一样，一些动作，比如 ``create`` ，并不需要指定模型实例。在这种情况下，可传递一个类名给 ``authorize`` 方法。当授权动作时，这个类名将被用来判断使用哪个策略：

.. code-block:: php

    <?php
    /**
     * 新建博客
     *
     * @param  Request  $request
     * @return Response
     */
    public function create(Request $request)
    {
        $this->authorize('create', Post::class); // 传入类名称

        // 当前用户可以新建博客...
    }

自动能力命名
""""""""""""
当你在控制器中调用 ``authorize`` 方法时，允许你省略能力名称。当没有给方法提供能力名称， ``Laravel`` 会自动从控制器方法中获取能力名称。

在我们样例控制器中，调用 ``$this->authorize($post)`` 将会访问 ``PostPolicy@update`` 策略方法。

.. code-block:: php

    <?php
    class PostController extends Controller
    {
        /**
         * 更新指定博客。
         *
         * @param  Request  $request
         * @param  Post  $post
         * @return Response
         */
        public function update(Request $request, Post $post)
        {
            $this->authorize($post);

            // 当前用户可以更新博客...
        }
        public function create(Request $request)
        {
            $this->authorize(Post::class); // 传入类名称

        }
    }

匹配方法名称的能力名称存在的问题
"""""""""""""""""""""""""""""""""

- 在控制器外部检查能力。控制器方法名称不总是直观反映检查能力名称。例如

  .. code-block:: html

        @foreach ($tasks as $task)
            @if ($user->can('show', $task))
                <a href="{{ url('tasks', $task) }}">View task</a>
            @endif
        @endforeach

  ``show`` 能力名称语义不清晰，应该使用 ``view`` 。

- 重复策略名称。 策略中 ``create`` 和 ``store`` 方法使用相同的检查逻辑，即决定我们是否允许用户随后存储该任务。映射控制器方法名称导致策略存在两个相同逻辑的方法。

  .. code-block:: php

    <?php
    use App\User;

    class TaskPolicy
    {
        public function create(User $user)
        {
            // check if the user can create new tasks
        }

        public function store(User $user)
        {
            // check if the user can create new tasks
        }
    }

  同样的问题存在于 ``edit`` 和 ``update`` 方法。

规范化能力名称
""""""""""""""
在 ``laravel 5.3`` 中，自动能力名称正在升级。取代盲目使用方法名称， ``laravel`` 会根据您要完成的操作选择一个合理的能力名称。

``laravel`` 通过方法和能力之间的简单映射来做到这一点。

.. code-block:: php

    <?php
    [
        'show' => 'view',
        'create' => 'create',
        'store' => 'create',
        'edit' => 'update',
        'update' => 'update',
        'destroy' => 'delete',
    ]

这会自动发生，不用更改控制器中的代码。您现在可以使用 ``$user->can('view'，$task)`` 检查视图，这更直观。

既然控制器并不总是使用与控制器方法匹配的能力名称来调用 ``Gate`` ，对应的策略类应该不再具有这些方法：

.. code-block:: php

    <?php
    use App\Task;

    class TaskController
    {
        public function create()
        {
            $this->authorize(Task::class);

            return view('tasks.create');
        }

        public function store()
        {
            $this->authorize(Task::class);

            Task::create(request()->input());
        }
    }

你只需要一个 ``create()`` 方法即可：

.. code-block:: php

    <?php
    class TaskPolicy
    {
        public function create(User $user)
        {
            // check if the user can create tasks
        }
    }

该 ``create`` 策略方法将被控制器中的 ``create`` 和 ``store`` 调用。

授权一个完整的资源控制器
""""""""""""""""""""""""
在控制器中，存在一个 ``authorizeResource`` 方法，使用该方法可以清空所有控制器中的授权检查方法。它替换了所有位于控制器方法中的 ``authorize`` 方法。

.. code-block:: php

    <?php
    class TaskController
    {
        public function __construct()
        {
            $this->authorizeResource(Post::class);
        }

        // ... all resource methods ...
    }

现在，你不必在控制器器方法中单个调用 ``authorize`` 方法。 ``laravel`` 现在将自动检查任何给定动作的正确能力。

通过用户模型
^^^^^^^^^^^^^
``Laravel`` 应用内置的 ``User`` 模型包含 2 个有用的方法来授权动作： ``can`` 和 ``cant`` 。 ``can``  方法需要指定授权的动作和相关的模型。例如，判定一个用户是否授权更新指定的 ``Post`` 模型：

.. code-block:: php

    <?php
    if ($user->can('update', $post)) {
        //
    }

如果指定模型的策略已被注册， ``can`` 方法会自动调用模型的策略方法并且返回 ``boolean`` 值。如果没有策略注册到这个模型， ``can`` 方法会尝试调用和动作名相匹配的基于闭包的 ``Gate`` 。

不需要指定模型的动作
""""""""""""""""""""
记住，一些动作，比如 ``create`` 并不需要指定模型实例。在这种情况下，可传递一个类名给 ``can`` 方法。当授权动作时，这个类名将被用来判断使用哪个策略：

.. code-block:: php

    <?php
    use App\Post;

    if ($user->can('create', Post::class)) { // 传入类的名称
        // 执行相关策略中的「create」方法...
    }

通过Blade模板
^^^^^^^^^^^^^
当编写 ``Blade`` 模板时，你可能希望页面的指定部分只展示给允许授权访问指定动作的用户。 例如，你可能希望只展示更新表单给有权更新博客的用户。这种情况下，你可以直接使用 ``@can`` 和 ``@cannot`` 指令。

.. code-block:: html

    @can('update', $post)
        <!-- 当前用户可以更新博客 -->
    @elsecan('create', $post)
        <!-- 当前用户可以新建博客 -->
    @endcan

    @cannot('update', $post)
        <!-- 当前用户不可以更新博客 -->
    @elsecannot('create', $post)
        <!-- 当前用户不可以新建博客 -->
    @endcannot

这些指令在编写 ``@if`` 和 ``@unless`` 时提供了方便的缩写。 ``@can`` 和 ``@cannot`` 各自转化为如下对应的语句：

.. code-block:: html

    @if (Auth::user()->can('update', $post))
        <!-- 当前用户可以更新博客 -->
    @endif

    @unless (Auth::user()->can('update', $post))
        <!-- 当前用户不可以更新博客 -->
    @endunless

不需要指定模型的动作
""""""""""""""""""""
和大部分其它的授权方法类似，当动作不需要模型实例时，你可以传递一个类名给 ``@can`` 和 ``@cannot`` 指令：

.. code-block:: html

    @can('create', App\Post::class)
        <!-- 当前用户可以新建博客 -->
    @endcan

    @cannot('create', App\Post::class)
        <!-- 当前用户不可以新建博客 -->
    @endcannot

样例
====
我们设计一个添加咖啡馆的样例，任何认证且具有权限的用户都可以添加咖啡馆。如果没有权限，则增加一个未执行的动作并让管理员审核。

准备用户角色
-----------
在用户迁移表中，为用户添加角色字段。

.. code-block:: php

    public function up()
    {
        Schema::table('users', function( Blueprint $table ){
          $table->integer('permission')->after('id')->default(0);
        });
    }

在id字段后面添加一个 ``permission`` 字段(觉得role字段更好)，它具有如下值：

- 3 – Super Admin
- 2 – Admin
- 1 – Shop Owner
- 0 – General User

用户默认为普通用户，允许管理员和超级管理员提升用户权限。用户类型1比较特殊，因为它们只能将咖啡馆添加到他们拥有的公司，编辑属于他们所拥有的公司的咖啡馆，并删除他们拥有的公司的咖啡馆。

为此，我们需要用户和公司的关系表，使用迁移来创建该多对多关系：

.. code-block:: php

    public function up()
    {
        Schema::create('companies_owners', function( Blueprint $table ){
          $table->integer('user_id')->unsigned();
          $table->foreign('user_id')->references('id')->on('users');
          $table->integer('company_id')->unsigned();
          $table->foreign('company_id')->references('id')->on('companies');
        });
    }

为了在模型上创建这个关系，在 ``app/Models/User.php`` 中增加如下关系：

.. code-block:: php

    public function companiesOwned(){
      return $this->belongsToMany( 'App\Models\Company', 'companies_owners', 'user_id', 'company_id' );
    }

同样在 ``app/Models/Company.php`` 中增加如下关系：

.. code-block:: php

    public function ownedBy(){
        return $this->belongsToMany( 'App\Models\User', 'companies_owners', 'company_id', 'user_id' );
    }

现在一个公司可以拥有多个用户，一个用户可以拥有多个公司。

准备用户权限
------------
如果是普通用户，则可以通过赋权来拥有权限。

首先，利用迁移来创建 ``actions`` 表：

.. code-block:: php

    public function up()
    {
        Schema::create('actions', function( Blueprint $table ){
          $table->increments('id');
          $table->integer('user_id')->unsigned();
          $table->foreign('user_id')->references('id')->on('users');
          $table->integer('company_id')->unsigned()->nullable();
          $table->foreign('company_id')->references('id')->on('companies');
          $table->integer('cafe_id')->unsigned()->nullable();
          $table->foreign('cafe_id')->references('id')->on('cafes');
          $table->integer('status');
          $table->integer('processed_by')->unsigned()->nullable();
          $table->foreign('processed_by')->references('id')->on('users');
          $table->dateTime('processed_on')->nullable();
          $table->string('type');
          $table->text('content');
          $table->timestamps();
        });
    }

- user_id 字段记录执行该动作的用户；
- processed_by 字段记录审核该动作的用户；如果动作已经添加且被授权，则和 user_id 值一样；
- content 字段对于每种请求类型都不同。为审核时的JSON格式数据；
- type 字段包含审核的动作类型。包含 cafe-added, cafe-deleted 和 cafe-updated ；
- status 字段可以是 0,1,2；0表示动作有待处理；1表示动作已经被审核通过；2表示动作被审核拒绝；

我们需要创建 app\Http\Models\Action.php 并添加如下代码：

.. code-block:: php

    namespace App\Models;

    use Illuminate\Database\Eloquent\Model;

    class Action extends Model
    {
        protected $table = 'actions';

      public function cafes(){
        return $this->belongsTo( 'App\Models\Cafe', 'cafe_id', 'id' );
      }

      public function by(){
        return $this->belongsTo( 'App\Models\User', 'user_id', 'id' );
      }

      public function processedBy(){
        return $this->belongsTo( 'App\Models\User', 'processed_by', 'id' );
      }
    }

为了配置所有必须的关系。我们应该为用户模型添加如下关系：

.. code-block:: php

    public function actions(){
      return $this->hasMany( 'App\Models\Action', 'id', 'user_id' );
    }

    public function actionsProcessed(){
      return $this->hasMany( 'App\Models\Action', 'id', 'processed_by' );
    }



创建 Cafe 策略
--------------
使用如下命令来创建一个策略类：

.. code-block:: shell

    php artisan make:policy CafePolicy --model=Cafe

该类针对3个方法4种用户类型进行权限处理。

- 创建 Cafe ：要创建一个咖啡店，你需要是 admin/super admin/你所在公司的咖啡店；
- 编辑 Cafe ：要编辑一个咖啡店，你需要是 admin/super admin/你所在公司的咖啡店；
- 删除 Cafe ：要删除一个咖啡店，你需要是 admin/super admin/你所在公司的咖啡店；

我们不仅仅是禁止没有权限的用户访问，我们还需要为这些访问创建一个动作，以便管理员审核是否授权。

先在 ``app/Providers/AuthServiceProvider.php`` 中注册权限：

.. code-block:: php

    protected $policies = [
        Cafe::class => CafePolicy::class
    ];

    public function boot()
    {
        $this->registerPolicies(); // 在底层，使用Gate来注册策略
    }

编辑我们的策略
-------------
在 ``app/Policies/CafePolicy.php`` 类中添加如下代码：

.. code-block:: php

    /**
     * If a user is an admin or a super admin they can create
     * a cafe.
     *
     * @param \App\Models\User  $user
     * @param \App\Models\Company $company
     */
    public function create( User $user, Company $company ){
      if( $user->permission == 2 || $user->permission == 3 ){
        return true;
      }else if( $company != null && $user->companiesOwned->contains( $company->id ) ){
        return true;
      }else{
        return false;
      }
    }

    /**
     * If a user is an admin or super admin OR they own the cafe
     * company then can edit the cafe.
     *
     * @param \App\Models\User  $user
     * @param \App\Models\Cafe  $cafe
     */
    public function update( User $user, Cafe $cafe ){
      if( $user->permission == 2 || $user->permission == 3 ){
        return true;
      }else if( $user->companiesOwned->contains( $cafe->company_id ) ){
        return true;
      }else{
        return false;
      }
    }

    /**
     * If a user is an admin or super admin OR they own the cafe company
     * then they can delete the cafe.
     *
     * @param \App\Models\User  $user
     * @param \App\Models\Cafe  $cafe
     */
    public function delete( User $user, Cafe $cafe ){
      if( $user->permission == 2 || $user->permission == 3 ){
        return true;
      }else if( $user->companiesOwned->contains( $cafe->company_id ) ){
        return true;
      }else{
        return false;
      }

该方法接受2个参数，第一个参数会被自动注入；第二个参数为我们直接传入的参数。

该方法检查用户是否是管理员或者超级管理员，如果是，则返回 ``true`` ；接下来检查 ``companiesOwned()`` 关系是否包含 ``companyID`` ，如果包含，则允许用户添加咖啡店。最后，如果都不满足，则拒绝执行该动作。

使用授权
--------
可以通过中间件或者Gate门面类帮助方法或者控制器方法或者用户模型方法来使用授权。这里我们使用用户模型方法。

首先，创建 ``app\Http\Controllers\API\CafesController.php`` 类。在这个类中创建 ``public function postNewCafe( StoreCafeRequest $request )`` 方法。该方法通过位于 ``app\Http\Requests\StoreCafeRequest.php`` 中的 ``StoreCafeRequest`` 验证器验证。

如果用户没有权限，则创建一个动作，让管理员或者咖啡店拥有者审核并授权。

首先在控制器中创建 ``public function postNewCafe( StoreCafeRequest $request )`` 方法。

.. code-block:: php

    /*
      Gets company that's adding the cafe
    */
    $companyID = $request->get('company_id');

    /*
      Get the company. If its null, create a new company otherwise
      set to the company that exists.
    */
    $company = Company::where('id', '=', $companyID)->first();
    $company = $company == null ? new Company() : $company;

    if( Auth::user()->can('create', [ Cafe::class, $company ] ) ){
      //..EXISTING CODE
    }else{
      //..CODE TO CREATE ACTION
    }

参考： https://serversideup.net/laravel-gates-and-policies-in-an-api-driven-spa/

Laravel常用的授权包
===================
`角色权限包 <https://laravel-news.com/two-best-roles-permissions-packages>`_

https://github.com/JosephSilber/bouncer

https://github.com/spatie/laravel-permission

https://github.com/santigarcor/laratrust(基于角色的访问控制)
