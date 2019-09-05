============
Eloquent关联
============

简介
====
数据库表通常相互关联。 例如，一篇博客文章可能有许多评论，或者一个订单对应一个下单用户。 ``Eloquent`` 让这些关联的管理和使用变得简单，并支持多种类型的关联：

- 一对一
- 一对多
- 多对多
- 远程一对多
- 多态关联
- 多对多多态关联

定义关联
========
``Eloquent`` 关联在 ``Eloquent`` 模型类中以方法的形式呈现。如同 ``Eloquent`` 模型本身，关联也可以作为强大的 查询语句构造器 使用，提供了强大的链式调用和查询功能。例如，我们可以在 ``posts`` 关联的链式调用中附加一个约束条件：

.. code-block:: php

    <?php
    $user->posts()->where('active', 1)->get();

不过，在深入使用关联之前，让我们先学习如何定义每种关联类型。

一对一
------
一对一关联是最基本的关联关系。例如，一个 ``User`` 模型可能关联一个 ``Phone`` 模型。为了定义这个关联，我们要在 ``User`` 模型中写一个 ``phone`` 方法，在 ``phone`` 方法内部调用 ``hasOne`` 方法并返回其结果：

.. code-block:: php

    <?php
    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class User extends Model
    {
        /**
         * 获取与用户关联的电话号码记录。
         */
        public function phone()
        {
            return $this->hasOne('App\Phone');
        }
    }

``hasOne`` 方法的第一个参数是关联模型的类名。一旦定义了模型关联，我们就可以使用 ``Eloquent`` 动态属性获得相关的记录。动态属性允许你访问关系方法就像访问模型中定义的属性一样：

.. code-block:: php

    <?php
    $phone = User::find(1)->phone;

``Eloquent`` 会基于模型名决定外键名称。在这个列子中， 会自动假设 ``Phone`` 模型有一个 ``user_id`` 的外键。如果你想覆盖这个约定，可以传递第二个参数给 ``has_one`` 方法：

.. code-block:: php

    <?php
    return $this->hasOne('App\Phone', 'foreign_key');

另外， ``Eloquent`` 假设外键的值是与父级 ``id`` （或自定义 ``$primaryKey`` ）列的值相匹配的 。换句话说， ``Eloquent`` 将会在 ``Phone`` 记录的 ``user_id`` 列中查找与用户表的 ``id`` 列相匹配的值。 如果您希望该关联使用 ``id`` 以外的自定义键名，则可以给 ``hasOne`` 方法传递第三个参数：

.. code-block:: php

    <?php
    return $this->hasOne('App\Phone', 'foreign_key', 'local_key');

定义反向关联
^^^^^^^^^^^^
我们已经能从 ``User`` 模型访问到 ``Phone`` 模型了。现在，让我们再在 ``Phone`` 模型上定义一个关联，这个关联能让我们访问到拥有该电话的 ``User`` 模型。我们可以使用与 ``hasOne`` 方法对应的 ``belongsTo`` 方法来定义反向关联：

.. code-block:: php

    <?php
    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Phone extends Model
    {
        /**
         * 获得拥有此电话的用户。
         */
        public function user()
        {
            return $this->belongsTo('App\User');
        }
    }

在上面的例子中， ``Eloquent`` 会尝试匹配 ``Phone`` 模型上的 ``user_id`` 至 ``User`` 模型上的 ``id`` 。它是通过检查关系方法的名称并使用 ``_id`` 作为后缀名来确定默认外键名称的。但是，如果 ``Phone`` 模型的外键不是 ``user_id`` ，那么可以将自定义键名作为第二个参数传递给 ``belongsTo`` 方法：

.. code-block:: php

    <?php
    /**
     * 获得拥有此电话的用户。
     */
    public function user()
    {
        return $this->belongsTo('App\User', 'foreign_key');
    }

如果父级模型没有使用 ``id`` 作为主键，或者是希望用不同的字段来连接子级模型，则可以通过给 ``belongsTo`` 方法传递第三个参数的形式指定父级数据表的自定义键：

.. code-block:: php

    <?php
    /**
     * 获得拥有此电话的用户。
     */
    public function user()
    {
        return $this->belongsTo('App\User', 'foreign_key', 'other_key');
    }

默认模型
^^^^^^^^^
``belongsTo`` 关联允许定义默认模型，这适应于当关联结果返回的是 ``null`` 的情况。这种设计模式通常称为 空对象模式，为您免去了额外的条件判断代码。在下面的例子中， ``user`` 关联如果没有找到文章的作者，就会返回一个空的 ``App\User`` 模型。

.. code-block:: php

    <?php
    /**
     * 获得此文章的作者。
     */
    public function user()
    {
        return $this->belongsTo('App\User')->withDefault();
    }

您也可以通过传递数组或闭包给 ``withDefault`` 方法，已填充默认模型的属性：

.. code-block:: php

    <?php
    /**
     * 获得此文章的作者。
     */
    public function user()
    {
        return $this->belongsTo('App\User')->withDefault([
            'name' => '游客',
        ]);
    }

    /**
     * 获得此文章的作者。
     */
    public function user()
    {
        return $this->belongsTo('App\User')->withDefault(function ($user) {
            $user->name = '游客';
        });
    }

一对多
------
「一对多」关联用于定义单个模型拥有任意数量的其它关联模型。例如，一篇博客文章可能会有无限多条评论。就像其它的 ``Eloquent`` 关联一样，一对多关联的定义也是在 ``Eloquent`` 模型中写一个方法：

.. code-block:: php

    <?php
    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Post extends Model
    {
        /**
         * 获得此博客文章的评论。
         */
        public function comments()
        {
            return $this->hasMany('App\Comment');
        }
    }

记住， ``Eloquent`` 会自动确定 ``Comment`` 模型上正确的外键字段。按照约定， ``Eloquent`` 使用父级模型名的「snake case」形式、加上 ``_id`` 后缀名作为外键字段。对应到上面的场景，就是 ``Eloquent`` 假定 ``Comment`` 模型对应到 ``Post`` 模型上的那个外键字段是 ``post_id`` 。

关联关系定义好后，我们就可以通过访问 ``comments`` 属性获得评论集合。记住，因为 ``Eloquent`` 提供了「动态属性」，所以我们可以像在访问模型中定义的属性一样，访问关联方法：

.. code-block:: php

    <?php
    $comments = App\Post::find(1)->comments;

    foreach ($comments as $comment) {
        //
    }

当然，由于所有的关联还可以作为查询语句构造器使用，因此你可以使用链式调用的方式、在 ``comments`` 方法上添加额外的约束条件：

.. code-block:: php

    <?php
    $comments = App\Post::find(1)->comments()->where('title', 'foo')->first();

和 ``hasOne`` 方法一样，您也可以在使用 ``hasMany`` 方法的时候，通过传递额外参数来覆盖默认使用的外键与本地键。

.. code-block:: php

    <?php
    return $this->hasMany('App\Comment', 'foreign_key');

    return $this->hasMany('App\Comment', 'foreign_key', 'local_key');

一对多（反向）
^^^^^^^^^^^^^
现在，我们已经能获得一篇文章的所有评论，接着再定义一个通过评论获得所属文章的关联。这个关联是 ``hasMany`` 关联的反向关联，在子级模型中使用 ``belongsTo`` 方法定义它：

.. code-block:: php

    <?php
    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Comment extends Model
    {
        /**
         * 获得此评论所属的文章。
         */
        public function post()
        {
            return $this->belongsTo('App\Post');
        }
    }

关联关系定义好后，我们就可以在 ``Comment`` 模型上使用 ``post`` 「动态属性」获得 ``Post`` 模型了。

.. code-block:: php

    <?php
    $comment = App\Comment::find(1);

    echo $comment->post->title;

在上面的例子中， ``Eloquent`` 会尝试用 ``Comment`` 模型的 ``post_id`` 与 ``Post`` 模型的 ``id`` 进行匹配。默认外键名是 ``Eloquent`` 依据关联名、并在关联名后加上 ``_id`` 后缀确定的。当然，如果 ``Comment`` 模型的外键不是 ``post_id`` ，那么可以将自定义键名作为第二个参数传递给 ``belongsTo`` 方法：

.. code-block:: php

    <?php
    /**
     * 获得此评论所属的文章。
     */
    public function post()
    {
        return $this->belongsTo('App\Post', 'foreign_key');
    }

如果父级模型没有使用 ``id`` 作为主键，或者是希望用不同的字段来连接子级模型，则可以通过给 ``belongsTo`` 方法传递第三个参数的形式指定父级数据表的自定义键：

.. code-block:: php

    <?php
    /**
     * Get the post that owns the comment.
     */
    public function post()
    {
        return $this->belongsTo('App\Post', 'foreign_key', 'other_key');
    }

默认模型
^^^^^^^^^
????

多对多
------
多对多关联 比 ``hasOne`` 和 ``hasMany`` 关联稍复杂些。 举一个关联例子，一个用户拥有很多种角色，同时这些角色也被其他用户共享。例如，许多用户都可以有「管理员」这个角色。要定于这种关联，需要用到这三个数据库表： ``users`` 、 ``roles`` 、 和 ``role_user`` 。 ``role_user`` 表 的命名是由关联的两个模型名按照字母顺序而来的，并且包含了 ``user_id`` 和 ``role_id`` 字段。

多对多关联通过写方法定义，在这个方法的内部调用 ``belongsToMany`` 方法并返回其结果。例如，我们在 ``User`` 模型中定义 ``roles`` 方法：

.. code-block:: php

    <?php
    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class User extends Model
    {
        /**
         * 获得用户的角色。
         */
        public function roles()
        {
            return $this->belongsToMany('App\Role');
        }
    }

关联被定义好之后，你就可以通过 ``roles`` 动态属性获取用户的角色了：

.. code-block:: php

    <?php
    $user = App\User::find(1);

    foreach ($user->roles as $role) {
        //
    }

当然，像其它所有的关联类型一样，你可以调用 ``roles`` 方法，利用链式调用对查询语句添加约束条件：

.. code-block:: php

    <?php
    $roles = App\User::find(1)->roles()->orderBy('name')->get();

如前所述， 为了确定关联连接表表名， ``Eloquent`` 会按照字母顺序合并两个关联模型的名称。当然，你也可以不使用这种约定，传参给 ``belongsToMany`` 方法的第二个参数：

.. code-block:: php

    <?php
    return $this->belongsToMany('App\Role', 'role_user');

除了自定义连接表表名外，你还可以通过给 ``belongsToMany`` 方法传递其它参数来自定义连接表的键名。第三个参数是定义此关联的模型在连接表里的外键名，第四个参数是另一个模型在连接表里的外键名：

.. code-block:: php

    <?php
    return $this->belongsToMany('App\Role', 'role_user', 'user_id', 'role_id');

定义反向关联
^^^^^^^^^^^^
定义多对多关联的反向关联，您只要在对方模型里再次调用 ``belongsToMany`` 方法就可以了。让我们接着以用户角色为例，在 ``Role`` 模型中定义一个 ``users`` 方法。

.. code-block:: php

    <?php
    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Role extends Model
    {
        /**
         * 获得此角色下的用户。
         */
        public function users()
        {
            return $this->belongsToMany('App\User');
        }
    }

如你所见，除了引入的模型变为 ``App\User`` 外，其它与在 ``User`` 模型中定义的完全一样。由于我们重用了 ``belongsToMany`` 方法，自定义连接表表名和自定义连接表里的键的字段名称在这里同样适用。

获取中间表字段
^^^^^^^^^^^^^^
您已经学到，多对多关联需要有一个中间表支持， ``Eloquent`` 提供了一些有用的方法来和这张表进行交互。例如，假设我们的 ``User`` 对象关联了许多的 ``Role`` 对象。在获得这些关联对象后，可以使用模型的 ``pivot`` 属性访问中间表数据：

.. code-block:: php

    <?php
    $user = App\User::find(1);

    foreach ($user->roles as $role) {
        echo $role->pivot->created_at;
    }

需要注意的是，我们取得的每个 ``Role`` 模型对象，都会被自动赋予 ``pivot`` 属性，它代表中间表的一个模型对象，能像其它的 ``Eloquent`` 模型一样使用。

默认情况下， ``pivot`` 对象只包含两个关联模型的键。如果中间表里还有额外字段，则必须在定义关联时明确指出：

.. code-block:: php

    <?php
    return $this->belongsToMany('App\Role')->withPivot('column1', 'column2');

如果您想让中间表自动维护 ``created_at`` 和 ``updated_at`` 时间戳，那么在定义关联时加上 ``withTimestamps`` 方法即可。

.. code-block:: php

    <?php
    return $this->belongsToMany('App\Role')->withTimestamps();

自定义 pivot 属性名称
^^^^^^^^^^^^^^^^^^^^^
如前所述，来自中间表的属性可以使用 ``pivot`` 属性在模型上访问。 但是，你可以自由定制此属性的名称，以更好地反映其在应用中的用途。

例如，如果你的应用中包含可能订阅播客的用户，则用户与播客之间可能存在多对多关系。 如果是这种情况，你可能希望将中间表访问器重命名为 ``subscription`` 而不是 ``pivot`` 。 这可以在定义关系时使用 ``as`` 方法完成：

.. code-block:: php

    <?php
    return $this->belongsToMany('App\Podcast')
                    ->as('subscription')
                    ->withTimestamps();

一旦定义完成，你可以使用自定义名称访问中间表数据：

.. code-block:: php

    <?php
    $users = User::with('podcasts')->get();

    foreach ($users->flatMap->podcasts as $podcast) {
        echo $podcast->subscription->created_at;
    }

通过中间表列过滤关系
^^^^^^^^^^^^^^^^^^^^
在定义关系时，你还可以使用 ``wherePivot`` 和 ``wherePivotIn`` 方法来过滤 ``belongsToMany`` 返回的结果：

.. code-block:: php

    <?php
    return $this->belongsToMany('App\Role')->wherePivot('approved', 1);

    return $this->belongsToMany('App\Role')->wherePivotIn('priority', [1, 2]);

定义自定义中间表模型
^^^^^^^^^^^^^^^^^^^^
如果你想定义一个自定义模型来表示关联关系中的中间表，可以在定义关联时调用 ``using`` 方法。所有自定义中间表模型都必须扩展自 ``Illuminate\Database\Eloquent\Relations\Pivot`` 类。例如，我们在写 ``Role`` 模型的关联时，使用自定义中间表模型 ``UserRole`` ：

.. code-block:: php

    <?php
    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Role extends Model
    {
        /**
         * 获得此角色下的用户。
         */
        public function users()
        {
            return $this->belongsToMany('App\User')->using('App\UserRole');
        }
    }

当定义 ``UserRole`` 模型时，我们要扩展 ``Pivot`` 类：

.. code-block:: php

    <?php
    namespace App;

    use Illuminate\Database\Eloquent\Relations\Pivot;

    class UserRole extends Pivot
    {
        //
    }

远程一对多
-----------
「远程一对多」关联提供了方便、简短的方式通过中间的关联来获得远层的关联。例如，一个 ``Country`` 模型可以通过中间的 ``User`` 模型获得多个 ``Post`` 模型。在这个例子中，您可以轻易地收集给定国家的所有博客文章。让我们来看看定义这种关联所需的数据表：

.. code-block:: ini

    countries
        id - integer
        name - string

    users
        id - integer
        country_id - integer
        name - string

    posts
        id - integer
        user_id - integer
        title - string

虽然 ``posts`` 表中不包含 ``country_id`` 字段，但 ``hasManyThrough`` 关联能让我们通过 ``$country->posts`` 访问到一个国家下所有的用户文章。为了完成这个查询， ``Eloquent`` 会先检查中间表 ``users`` 的 ``country_id`` 字段，找到所有匹配的用户 ``ID`` 后，使用这些 ``ID`` ，在 ``posts`` 表中完成查找。

现在，我们已经知道了定义这种关联所需的数据表结构，接下来，让我们在 ``Country`` 模型中定义它：

.. code-block:: php

    <?php
    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Country extends Model
    {
        /**
         * 获得某个国家下所有的用户文章。
         */
        public function posts()
        {
            return $this->hasManyThrough('App\Post', 'App\User');
        }
    }

``hasManyThrough`` 方法的第一个参数是我们最终希望访问的模型名称，而第二个参数是中间模型的名称。

当执行关联查询时，通常会使用 ``Eloquent`` 约定的外键名。如果您想要自定义关联的键，可以通过给 ``hasManyThrough`` 方法传递第三个和第四个参数实现，第三个参数表示中间模型的外键名，第四个参数表示最终模型的外键名。第五个参数表示本地键名，而第六个参数表示中间模型的本地键名：

.. code-block:: php

    <?php
    class Country extends Model
    {
        public function posts()
        {
            return $this->hasManyThrough(
                'App\Post',
                'App\User',
                'country_id', // 用户表外键...
                'user_id', // 文章表外键...
                'id', // 国家表本地键...
                'id' // 用户表本地键...
            );
        }
    }

一对多多态关联
--------------
数据表结构
^^^^^^^^^^^
多态关联允许一个模型在单个关联上属于多个其他模型。例如，想象一下使用您应用的用户可以「评论」文章和视频。使用多态关联，您可以用一个 comments 表同时满足这两个使用场景。让我们来看看构建这种关联所需的数据表结构：

.. code-block:: ini

    posts
        id - integer
        title - string
        body - text

    videos
        id - integer
        title - string
        url - string

    comments
        id - integer
        body - text
        commentable_id - integer
        commentable_type - string

``comments`` 表中有两个需要注意的重要字段 ``commentable_id`` 和 ``commentable_type`` 。 ``commentable_id`` 用来保存文章或者视频的 ``ID`` 值，而 ``commentable_type`` 用来保存所属模型的类名。 ``commentable_type`` 是在我们访问 ``commentable`` 关联时， 让 ``ORM`` 确定所属的模型是哪个「类型」。

模型结构
^^^^^^^^
接下来，我们来看看创建这种关联所需的模型定义：

.. code-block:: php

    <?php
    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Comment extends Model
    {
        /**
         * 获得拥有此评论的模型。
         */
        public function commentable()
        {
            return $this->morphTo();
        }
    }

    class Post extends Model
    {
        /**
         * 获得此文章的所有评论。
         */
        public function comments()
        {
            return $this->morphMany('App\Comment', 'commentable');
        }
    }

    class Video extends Model
    {
        /**
         * 获得此视频的所有评论。
         */
        public function comments()
        {
            return $this->morphMany('App\Comment', 'commentable');
        }
    }

获取多态关联
^^^^^^^^^^^^
一旦您的数据库表准备好、模型定义完成后，就可以通过模型来访问关联了。例如，我们只要简单地使用 ``comments`` 动态属性，就可以获得某篇文章下的所有评论：

.. code-block:: php

    <?php
    $post = App\Post::find(1);

    foreach ($post->comments as $comment) {
        //
    }

您也可以在多态模型上，通过访问调用了 ``morphTo`` 的关联方法获得多态关联的拥有者。在当前场景中，就是 ``Comment`` 模型的 ``commentable`` 方法。所以，我们可以使用动态属性来访问这个方法：

.. code-block:: php

    <?php
    $comment = App\Comment::find(1);

    $commentable = $comment->commentable;

``Comment`` 模型的 ``commentable`` 关联会返回 ``Post`` 或者 ``Video`` 实例，这取决于评论所属的模型类型。

自定义多态关联的类型字段
^^^^^^^^^^^^^^^^^^^^^^^^
默认，Laravel 会使用完全限定类名作为关联模型保存在多态模型上的类型字段值。比如，在上面的例子中， ``Comment`` 属于 ``Post`` 或者 ``Video`` ，那么 ``commentable_type`` 的默认值对应地就是 ``App\Post`` 和 ``App\Video`` 。但是，您可能希望将数据库与程序内部结构解耦。那样的话，你可以定义一个「多态映射表」来指示 ``Eloquent`` 使用每个模型自定义类型字段名而不是类名：

.. code-block:: php

    <?php
    use Illuminate\Database\Eloquent\Relations\Relation;

    Relation::morphMap([
        'posts' => 'App\Post',
        'videos' => 'App\Video',
    ]);

您可以在 ``AppServiceProvider`` 中的 ``boot`` 函数中使用 ``Relation::morphMap`` 方法注册「多态映射表」，或者使用一个独立的服务提供者注册。

多对多多态关联
--------------
数据表结构
^^^^^^^^^^
除了传统的多态关联，您也可以定义「多对多」的多态关联。例如， ``Post`` 模型和 ``Video`` 模型可以共享一个多态关联至 ``Tag`` 模型。 使用多对多多态关联可以让您在文章和视频中共享唯一的标签列表。首先，我们来看看数据表结构：

.. code-block:: ini

    posts
        id - integer
        name - string

    videos
        id - integer
        name - string

    tags
        id - integer
        name - string

    taggables
        tag_id - integer
        taggable_id - integer
        taggable_type - string

模型结构
^^^^^^^^
接下来，我们准备在模型上定义关联关系。 ``Post`` 和 ``Video`` 两个模型都有一个 ``tags`` 方法，方法内部都调用了 ``Eloquent`` 类自身的 ``morphToMany`` 方法：

.. code-block:: php

    <?php
    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Post extends Model
    {
        /**
         * 获得此文章的所有标签。
         */
        public function tags()
        {
            return $this->morphToMany('App\Tag', 'taggable'); //中间表名
        }
    }

定义反向关联
^^^^^^^^^^^^
接下里，在 ``Tag`` 模型中，您应该为每个关联模型定义一个方法。在这个例子里，我们要定义一个 ``posts`` 方法和一个 ``videos`` 方法：

.. code-block:: php

    <?php
    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Tag extends Model
    {
        /**
         * 获得此标签下所有的文章。
         */
        public function posts()
        {
            return $this->morphedByMany('App\Post', 'taggable');//中间表名
        }

        /**
         *  获得此标签下所有的视频。
         */
        public function videos()
        {
            return $this->morphedByMany('App\Video', 'taggable');//中间表名
        }
    }

获取关联
^^^^^^^^^
一旦您的数据库表准备好、模型定义完成后，就可以通过模型来访问关联了。例如，我们只要简单地使用 ``tags`` 动态属性，就可以获得某篇文章下的所有标签：

.. code-block:: php

    <?php
    $post = App\Post::find(1);

    foreach ($post->tags as $tag) {
        //
    }

您也可以在多态模型上，通过访问调用了 ``morphedByMany`` 的关联方法获得多态关联的拥有者。在当前场景中，就是 ``Tag`` 模型上的 ``posts`` 方法和 ``videos`` 方法。所以，我们可以使用动态属性来访问这两个方法：

.. code-block:: php

    <?php
    $tag = App\Tag::find(1);

    foreach ($tag->videos as $video) {
        //
    }

查询关联
========
由于所有类型的关联都通过方法定义，您可以调用这些方法来获取关联实例，而不需要实际运行关联的查询。此外，所有类型的关联都可以作为 查询语句构造器 使用，让你在向数据库执行 ``SQL`` 语句前，使用链式调用的方式添加约束条件。

例如，假设一个博客系统，其中 ``User`` 模型有许多关联的 ``Post`` 模型：

.. code-block:: php

    <?php

    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class User extends Model
    {
        /**
         * 获得此用户所有的文章。
         */
        public function posts()
        {
            return $this->hasMany('App\Post');
        }
    }

您也可以像这样在 ``posts`` 关联上添加额外约束条件：

.. code-block:: php

    <?php
    $user = App\User::find(1);

    $user->posts()->where('active', 1)->get();

您可以在关联上使用任何 查询语句构造器 的方法，所以，欢迎查阅查询语句构造器的相关文档以便了解您可以使用哪些方法。

关联方法 Vs 动态属性
---------------------
如果您不需要给 ``Eloquent`` 关联查询添加额外约束条件，你可以简单的像访问属性一样访问关联。例如，我们刚刚的 ``User`` 和 ``Post`` 模型例子中，我们可以这样访问所有用户的文章：

.. code-block:: php

    <?php
    $user = App\User::find(1);

    foreach ($user->posts as $post) {
        //
    }

动态属性是「懒加载」的，意味着它们的关联数据只在实际被访问时才被加载。因此，开发者经常使用 预加载 提前加载他们之后会用到的关联数据。预加载有效减少了 ``SQL`` 语句请求数，避免了重复执行一个模型关联加载数据、发送 ``SQL`` 请求带来的性能问题。

基于存在的关联查询
------------------
当获取模型记录时，您可能希望根据存在的关联对结果进行限制。例如，您想获得至少有一条评论的所有博客文章。为了实现这个功能，您可以给 ``has`` 或者是 ``orHas`` 方法传递关联名称：

.. code-block:: php

    <?php
    // 获得所有至少有一条评论的文章...
    $posts = App\Post::has('comments')->get();

您也可以指定一个运算符和数目，进一步自定义查询：

.. code-block:: php

    <?php
    // 获得所有有三条或三条以上评论的文章...
    $posts = Post::has('comments', '>=', 3)->get();

也可以使用「点」符号构造嵌套的的 ``has`` 语句。例如，您可以获得所有至少有一条获赞评论的文章：

.. code-block:: php

    <?php
    // 获得所有至少有一条获赞评论的文章...
    $posts = Post::has('comments.votes')->get();

如果您需要更高级的用法，可以使用 ``whereHas`` 和 ``orWhereHas`` 方法在 ``has`` 查询里设置「where」条件。此方法可以让你增加自定义条件至关联约束中，例如对评论内容进行检查：

.. code-block:: php

    <?php
    // 获得所有至少有一条评论内容满足 foo% 条件的文章
    $posts = Post::whereHas('comments', function ($query) {
        $query->where('content', 'like', 'foo%');
    })->get();

基于不存在的关联查询
--------------------
当获取模型记录时，您可能希望根据不存在的关联对结果进行限制。例如，您想获得 没有 任何评论的所有博客文章。为了实现这个功能，您可以给 ``doesntHave`` 或者 ``orDoesntHave`` 方法传递关联名称：

.. code-block:: php

    <?php
    $posts = App\Post::doesntHave('comments')->get();

如果您需要更高级的用法，可以使用 ``whereDoesntHave`` 或者 ``orWhereDoesntHave`` 方法在 ``doesntHave`` 查询里设置「where」条件。此方法可以让你增加自定义条件至关联约束中，例如对评论内容进行检查：

.. code-block:: php

    <?php
    $posts = Post::whereDoesntHave('comments', function ($query) {
        $query->where('content', 'like', 'foo%');
    })->get();

关联数据计数
------------
如果您只想统计结果数而不需要加载实际数据，那么可以使用 ``withCount`` 方法，此方法会在您的结果集模型中添加一个 ``{关联名}_count`` 字段。例如：

.. code-block:: php

    <?php
    $posts = App\Post::withCount('comments')->get();

    foreach ($posts as $post) {
        echo $post->comments_count;
    }

您可以为多个关联数据「计数」，并为其查询添加约束条件：

.. code-block:: php

    <?php
    $posts = Post::withCount(['votes', 'comments' => function ($query) {
        $query->where('content', 'like', 'foo%');
    }])->get();

    echo $posts[0]->votes_count;
    echo $posts[0]->comments_count;

您也可以为关联数据计数结果起别名，允许在同一个关联上多次计数：

.. code-block:: php

    <?php
    $posts = Post::withCount([
        'comments',
        'comments as pending_comments_count' => function ($query) {
            $query->where('approved', false);
        }
    ])->get();

    echo $posts[0]->comments_count;

    echo $posts[0]->pending_comments_count;

预加载
======
当作为属性访问模型关联时，关联的数据是「懒加载」。意味着关联的数据在你第一次访问该属性的时候才会加载。不过，当你查询父模型时， ``Eloquent`` 可以「预加载」关联数据。 预加载避免了 ``N + 1`` 次查询的问题。举例说明一个 ``N + 1`` 查询问题，考虑 ``Book`` 模型跟 ``Author`` 关联的情况：

.. code-block:: php

    <?php
    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Book extends Model
    {
        /**
         * 获取书的作者
         */
        public function author()
        {
            return $this->belongsTo('App\Author');
        }
    }

现在，我们来获取所有书籍和书作者的数据：

.. code-block:: php

    <?php
    $books = App\Book::all();

    foreach ($books as $book) {
        echo $book->author->name;
    }

这个循环将会执行一次从表中获取所有的书籍数据，然后每本书查询一次获取作者数据。所以，如果我们有 ``25`` 本书，这个循环就会执行 ``26`` 次： ``1`` 次获得所有书的数据，另外的 ``25`` 次查询获取每本书的作者数据。

幸好，我们可以使用预加载让查询次数减少到 ``2`` 次。查询时，你可以使用 ``with`` 方法指定哪些关联应该被预加载：

.. code-block:: php

    <?php
    $books = App\Book::with('author')->get();

    foreach ($books as $book) {
        echo $book->author->name;
    }

这个操作，只执行了两次查询：

.. code-block:: php

    <?php
    select * from books

    select * from authors where id in (1, 2, 3, 4, 5, ...)

预加载多个关联
--------------
有时，你可能需要在一次操作中预加载多个不同的关联。只需要给 ``with`` 方法传额外的参数就能实现：

.. code-block:: php

    <?php
    $books = App\Book::with(['author', 'publisher'])->get();

嵌套预加载
----------
预加载嵌套关联，可以使用「点」语法。例如，在一个 ``Eloquent`` 声明中，预加载所有书籍的作者和这些作者的个人联系信息：

.. code-block:: php

    <?php
    $books = App\Book::with('author.contacts')->get();

预加载特定的列
--------------
你可能不是总需要从关联中获取每一列。出于这个原因， ``Eloquent`` 允许你在关联中指定你想要查询的列：

.. code-block:: php

    <?php
    $users = App\Book::with('author:id,name')->get();

.. note:: 使用这个方法时，在你想获取的列中应始终有 ``id`` 列。

约束预加载
----------
有时，在使用预加载时，又需要在预加载上指定额外的查询约束。如下例：

.. code-block:: php

    <?php
    $users = App\User::with(['posts' => function ($query) {
        $query->where('title', 'like', '%first%');
    }])->get();

上例中， ``Eloquent`` 仅预加载 ``title`` 列含有 ``first`` 的帖子。当然，可以调用 查询构造器 的其他方法，进一步自定义预加载操作：

.. code-block:: php

    <?php
    $users = App\User::with(['posts' => function ($query) {
        $query->orderBy('created_at', 'desc');
    }])->get();

延迟预加载
----------
有时，需要在检索出来的模型上进行预加载。这对动态决定是否预加载就非常实用：

.. code-block:: php

    <?php
    $books = App\Book::all();

    if ($someCondition) {
        $books->load('author', 'publisher');
    }

如果需要在预加载上添加额外的查询约束，可以传入一个数组，关联为键，接受查询实例的闭包为值：

.. code-block:: php

    <?php
    $books->load(['author' => function ($query) {
        $query->orderBy('published_date', 'asc');
    }]);

``loadMissing`` 方法可以仅在未加载关联时进行加载：

.. code-block:: php

    <?php
    public function format(Book $book)
    {
        $book->loadMissing('author');

        return [
            'name' => $book->name,
            'author' => $book->author->name
        ];
    }

插入 & 更新关联模型
===================

保存方法
--------
``Eloquent`` 为新模型添加关联提供了便捷的方法。例如，也许你需要添加一个新的 ``Comment`` 到一个 ``Post`` 模型中。你不用在 ``Comment`` 中手动设置 ``post_id`` 属性, 就可以直接使用关联模型的 ``save`` 方法将 ``Comment`` 直接插入：

.. code-block:: php

    <?php
    $comment = new App\Comment(['message' => 'A new comment.']);

    $post = App\Post::find(1);

    $post->comments()->save($comment);

需要注意的是，我们并没有使用动态属性的方式访问 ``comments`` 关联。相反，我们调用 ``comments`` 方法来获得关联实例。 ``save`` 方法将自动添加适当的 ``post_id`` 值到 ``Comment`` 模型中。

如果你需要保存多个关联模型，你可以使用 ``saveMany`` 方法：

.. code-block:: php

    <?php
    $post = App\Post::find(1);

    $post->comments()->saveMany([
        new App\Comment(['message' => 'A new comment.']),
        new App\Comment(['message' => 'Another comment.']),
    ]);

新增方法
--------
除了 ``save`` 和 ``saveMany`` 方法外，你还可以使用 ``create`` 方法。它接受一个属性数组，同时会创建模型并插入到数据库中。 还有， ``save`` 方法和 ``create`` 方法的不同之处在于， ``save`` 方法接受一个完整的 ``Eloquent`` 模型实例，而 ``create`` 则接受普通的 ``PHP`` 数组:

.. code-block:: php

    <?php
    $post = App\Post::find(1);

    $comment = $post->comments()->create([
        'message' => 'A new comment.',
    ]);

.. tip:: 在使用 ``create`` 方法前，请务必确保查看过本文档的 批量赋值 章节。

你还可以使用 ``createMany`` 方法去创建多个关联模型：

.. code-block:: php

    <?php
    $post = App\Post::find(1);

    $post->comments()->createMany([
        [
            'message' => 'A new comment.',
        ],
        [
            'message' => 'Another new comment.',
        ],
    ]);

更新 belongsTo 关联
-------------------
当更新 ``belongsTo`` 关联时，可以使用 ``associate`` 方法。此方法将会在子模型中设置外键：

.. code-block:: php

    <?php
    $account = App\Account::find(10);

    $user->account()->associate($account);

    $user->save();

当移除 ``belongsTo`` 关联时，可以使用 ``dissociate`` 方法。此方法会将关联外键设置为 ``null`` :

.. code-block:: php

    <?php
    $user->account()->dissociate();

    $user->save();

多对多关联
----------

附加 / 分离
^^^^^^^^^^^
``Eloquent`` 也提供了一些额外的辅助方法，使相关模型的使用更加方便。例如，我们假设一个用户可以拥有多个角色，并且每个角色都可以被多个用户共享。给某个用户附加一个角色是通过向中间表插入一条记录实现的，可以使用 ``attach`` 方法完成该操作：

.. code-block:: php

    <?php
    $user = App\User::find(1);

    $user->roles()->attach($roleId);

在将关系附加到模型时，还可以传递一组要插入到中间表中的附加数据：

.. code-block:: php

    <?php
    $user->roles()->attach($roleId, ['expires' => $expires]);

当然，有时也需要移除用户的角色。可以使用 ``detach`` 移除多对多关联记录。 ``detach`` 方法将会移除中间表对应的记录；但是这 ``2`` 个模型都将会保留在数据库中：

.. code-block:: php

    <?php
    // 移除用户的一个角色...
    $user->roles()->detach($roleId);

    // 移除用户的所有角色...
    $user->roles()->detach();

为了方便， ``attach`` 和 ``detach`` 也允许传递一个 ``ID`` 数组：

.. code-block:: php

    <?php
    $user = App\User::find(1);

    $user->roles()->detach([1, 2, 3]);

    $user->roles()->attach([
        1 => ['expires' => $expires],
        2 => ['expires' => $expires]
    ]);

同步关联
^^^^^^^^
你也可以使用 ``sync`` 方法构建多对多关联。 ``sync`` 方法接收一个 ``ID`` 数组以替换中间表的记录。中间表记录中，所有未在 ``ID`` 数组中的记录都将会被移除。所以该操作结束后，只有给出数组的 ``ID`` 会被保留在中间表中：

.. code-block:: php

    <?php
    $user->roles()->sync([1, 2, 3]);

你也可以通过 ``ID`` 传递额外的附加数据到中间表：

.. code-block:: php

    <?php
    $user->roles()->sync([1 => ['expires' => true], 2, 3]);

如果你不想移除现有的 ``ID`` ，可以使用 ``syncWithoutDetaching`` 方法：

.. code-block:: php

    <?php
    $user->roles()->syncWithoutDetaching([1, 2, 3]);

切换关联
^^^^^^^^
多对多关联也提供了 ``toggle`` 方法用于「切换」给定 ``ID`` 数组的附加状态。 如果给定的 ``ID`` 已被附加在中间表中，那么它将会被移除，同样，如果如果给定的 ``ID`` 已被移除，它将会被附加：

.. code-block:: php

    <?php
    $user->roles()->toggle([1, 2, 3]);

在中间表上保存额外的数据
^^^^^^^^^^^^^^^^^^^^^^^^
当处理多对多关联时， ``save`` 方法接收一个额外的数据数组作为第二个参数：

.. code-block:: php

    <?php
    App\User::find(1)->roles()->save($role, ['expires' => $expires]);

更新中间表记录
^^^^^^^^^^^^^^
如果你需要在中间表中更新一条已存在的记录，可以使用 ``updateExistingPivot`` 。此方法接收中间表的外键与要更新的数据数组进行更新：

.. code-block:: php

    <?php
    $user = App\User::find(1);

    $user->roles()->updateExistingPivot($roleId, $attributes);

更新父级时间戳
==============
当一个模型属 ``belongsTo`` 或者 ``belongsToMany`` 另一个模型时， 例如 ``Comment`` 属于 ``Post`` ，有时更新子模型导致更新父模型时间戳非常有用。例如，当 ``Comment`` 模型被更新时，您要自动「触发」父级 ``Post`` 模型的 ``updated_at`` 时间戳的更新。 ``Eloquent`` 让它变得简单。只要在子模型加一个包含关联名称的 ``touches`` 属性即可：

.. code-block:: php

    <?php

    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Comment extends Model
    {
        /**
         * 要触发的所有关联关系。
         *
         * @var array
         */
        protected $touches = ['post'];

        /**
         * 评论所属文章。
         */
        public function post()
        {
            return $this->belongsTo('App\Post');
        }
    }

现在，当你更新一个 ``Comment`` 时，对应父级 ``Post`` 模型的 ``updated_at`` 字段也会被同时更新，使其更方便得知何时让一个 ``Post`` 模型的缓存失效：

.. code-block:: php

    <?php
    $comment = App\Comment::find(1);

    $comment->text = 'Edit to this comment!';

    $comment->save();

