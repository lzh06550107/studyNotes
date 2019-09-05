*****************
Bouncer——授权系统
*****************

`github地址 <https://github.com/JosephSilber/bouncer>`_ 

``Bouncer`` 是一个优雅的，框架无关的方式并使用 ``Eloquent`` 模型来管理的任何应用程序的角色和能力的插件。

介绍
====
``Bouncer`` 是一个优雅的，以框架无关的方式使用 ``Eloquent`` 模型来管理任何应用程序的角色和能力。具有表达性和流畅的语法，它尽可能少侵入代码：在需要时使用它，当你不需要时忽略它。

``Bouncer`` 与您在自己的应用程序中硬编码的其它权限很好地协同工作。你的代码总是优先：如果你的代码允许一个动作， ``Bouncer`` 不会干涉。

一旦安装，你可以简单地告诉 ``Bouncer`` 你想在 ``Gate`` 允许什么：

.. code-block:: php

    <?php
    // Give a user the ability to create posts
    Bouncer::allow($user)->to('create', Post::class);

    // Alternatively, do it through a role
    Bouncer::allow('admin')->to('create', Post::class);
    Bouncer::assign('admin')->to($user);

    // You can also grant an ability only to a specific model
    Bouncer::allow($user)->to('edit', $post);

当你在 ``Gate`` 检查能力时，首先会询问 ``Bouncer`` 。如果他看到已经授予当前用户的能力（无论是直接还是通过角色），他都会通过授权。

安装
====

在 ``Laravel`` 应用中安装 ``Bouncer``
--------------------------------------
使用 ``composer`` 安装 ``Bouncer``

.. code-block:: shell

    $ composer require silber/bouncer v1.0.0-rc.1

.. note:: 在 ``Laravel`` 5.5中， 服务提供器和别名会自动注册。如果你正在使用该版本，直接跳到第3步。

1. 在 ``providers`` 数组中增加如下新项：

   .. code-block:: php

    Silber\Bouncer\BouncerServiceProvider::class,

2. 在 ``aliases`` 数组中增加如下新项：

   .. code-block:: php

    'Bouncer' => Silber\Bouncer\BouncerFacade::class,

3. 在用户模型中增加 ``Bouncer`` 的 ``trait``:

   .. code-block:: php

    use Silber\Bouncer\Database\HasRolesAndAbilities;

    class User extends Model
    {
        use HasRolesAndAbilities;
    }

4. 现在，为了运行 ``Bouncer`` 的迁移，首先在 ``migrations`` 中生成迁移文件，可以通过如下命令来生成：

   .. code-block:: shell

    php artisan vendor:publish --tag="bouncer.migrations"

5. 最后，运行迁移：

   .. code-block:: shell

    php artisan migrate


在非 ``Laravel`` 应用中安装 ``Bouncer``
---------------------------------------
1. 使用 ``composer`` 安装 ``Bouncer``

   .. code-block:: shell

    $ composer require silber/bouncer v1.0.0-rc.1

2. 使用 `the Eloquent Capsule component <https://github.com/illuminate/database/blob/master/README.md>`_  配置数据库：

   .. code-block:: php

    <?php
    use Illuminate\Database\Capsule\Manager as Capsule;

    $capsule = new Capsule;

    $capsule->addConnection([/* connection config */]);

    $capsule->setAsGlobal();

   详情请参考 `the Eloquent Capsule 文档 <https://github.com/illuminate/database/blob/master/README.md>`_ 

3. 通过如下方法来运行迁移：

   a) 使用 `vagabond <https://github.com/michaeldyrynda/vagabond>`_ 在 ``Laravel`` 应用之外来运行 ``Laravel`` 迁移；
   b) 可选地，你可以在你的数据库中直接运行原始的SQL语句；

4. 增加 ``Bouncer`` 的 ``trait`` 到你的用户模型中：

   .. code-block:: php

    <?php
    use Illuminate\Database\Eloquent\Model;
    use Silber\Bouncer\Database\HasRolesAndAbilities;

    class User extends Model
    {
        use HasRolesAndAbilities;
    }

5. 创建一个 ``Bouncer`` 实例

   .. code-block:: php

    <?php
    use Silber\Bouncer\Bouncer;

    $bouncer = Bouncer::create();

    // If you are in a request with a current user
    // that you'd wish to check permissions for,
    // pass that user to the "create" method:
    $bouncer = Bouncer::create($user);

   如果在应用中使用依赖注入，你可以在容器中注册 ``Bouncer`` 实例：

   .. code-block:: php

    <?php
    use Silber\Bouncer\Bouncer;
    use Illuminate\Container\Container;

    Container::getInstance()->singleton(Bouncer::class, function () {
        return Bouncer::create();
    });

   现在，你可以注入 ``Bouncer`` 到任何需要的类中。

   ``create`` 方法创建一个具有合理默认值的 ``Bouncer`` 实例。要完全定制它，请使用 ``make`` 方法获取工厂实例。调用工厂的 ``create()`` 来创建 ``Bouncer`` 实例：

   .. code-block:: php

    <?php
    use Silber\Bouncer\Bouncer;

    $bouncer = Bouncer::make()
             ->withCache($customCacheInstance)
             ->create();

6. 设置整个应用程序将哪个模型用作用户模型：

   .. code-block:: php

    <?php
    $bouncer->useUserModel(User::class);

   详情请参考下面的配置部分。

开启缓存
---------
默认情况下，为当前请求缓存 ``Bouncer`` 的查询。为了获得更好的性能，您可能需要启用跨请求缓存。

使用
====
向用户添加角色和能力变得非常简单。你不必事先创建角色或能力。只需传递角色/能力的名称，如果不存在， ``Bouncer`` 会创建它。

.. note:: 下面的例子都使用 ``Bouncer`` 门面。如果您不使用门面，您可以将 ``silber\ bouncer\bouncer的实例注入您的类中。

创建角色和权限
---------------
让我们创建一个 ``admin`` 和赋给它一个 ``ban-users`` 能力：

.. code-block:: php

    <?php
    Bouncer::allow('admin')->to('ban-users');

就这样而已。在幕后， ``Bouncer`` 会为你创造一个 ``Role`` 和 ``Ability`` 模型。

如果你想要给角色/能力增加额外的属性，如，可读的标题，你可以使用 ``Bouncer`` 类的 ``role`` 和 ``ability`` 方法来手动创建它们：

.. code-block:: php

    <?php
    $admin = Bouncer::role()->create([
        'name' => 'admin',
        'title' => 'Administrator',
    ]);

    $ban = Bouncer::ability()->create([
        'name' => 'ban-users',
        'title' => 'Ban users',
    ]);

    Bouncer::allow($admin)->to($ban);

给用户分配角色
--------------
现在将 ``admin`` 角色授予用户，只需告诉 ``Bouncer`` 应该为给定用户分配 ``admin`` 角色：

.. code-block:: php

    <?php
    Bouncer::assign('admin')->to($user);

可选地，你可以直接在用户对象上调用 ``assign`` 方法：

.. code-block:: php

    <?php
    $user->assign('admin');

直接分配权限给用户
-------------------
有时你可能想直接给用户一个能力，而不使用角色：

.. code-block:: php

    <?php
    Bouncer::allow($user)->to('ban-users');

这里也可以直接从用户那里完成相同的操作：

.. code-block:: php

    <?php
    $user->allow('ban-users');

限制能力在具体的模型
--------------------
有时您可能想限制能力在某种特定模型类型。只需将模型名称作为第二个参数传递即可：

.. code-block:: php

    <?php
    Bouncer::allow($user)->to('edit', Post::class);

如果您想限制能力在某个特定模型实例，请改用实际模型：

.. code-block:: php

    <?php
    Bouncer::allow($user)->to('edit', $post);

允许一个用户或者角色拥有一个模型
---------------------------------
使用 ``toOwn`` 方法允许用户管理他们自己的模型：

.. code-block:: php

    <?php
    Bouncer::allow($user)->toOwn(Post::class);

现在，当在 ``Gate`` 检查用户是否可以对给定帖子执行动作时，帖子的 ``user_id`` 将与登录用户的 ``id`` （可以被定制）进行比较。如果他们匹配， ``Gate`` 将允许行动。

上述将授予用户“拥有”模型的​​所有能力。您可以通过调用 ``to`` 方法来限制它的能力：

.. code-block:: php

    <?php
    Bouncer::allow($user)->toOwn(Post::class)->to('view');

    // Or pass it an array of abilities:
    Bouncer::allow($user)->toOwn(Post::class)->to(['view', 'update']);

您还可以允许用户在您的应用程序中拥有所有类型的模型：

.. code-block:: php

    <?php
    Bouncer::allow($user)->toOwnEverything();

    // And to restrict ownership to a given ability
    Bouncer::allow($user)->toOwnEverything()->to('view');

收回一个用户角色
----------------
``Bouncer`` 还可以从用户收回以前分配的角色：

.. code-block:: php

    <?php
    Bouncer::retract('admin')->from($user);

或者直接在用户实例操作：

.. code-block:: php

    <?php
    $user->retract('admin');

移除一个能力
------------
``Bouncer`` 也可以移除先前授予用户的能力：

.. code-block:: php

    <?php
    Bouncer::disallow($user)->to('ban-users');

或者直接在用户实例操作：

.. code-block:: php

    <?php
    $user->disallow('ban-users');

.. note:: 如果用户具有允许他们 ``ban-users`` 能力的 的角色，他们仍然具有该能力。要禁止它，要么从角色中删除角色，要么从用户角色撤回角色。

如果该能力是通过角色获得的，则告诉 ``Bouncer`` 将能力从角色中移除：

.. code-block:: php

    <?php
    Bouncer::disallow('admin')->to('ban-users');

为了移除特定模型类型的能力，请将其名称作为第二个参数传入：

.. code-block:: php

    <?php
    Bouncer::disallow($user)->to('delete', Post::class);

.. warning:: 如果用户有 ``delete`` 能力在特定的 ``$ post`` 实例，则上面的代码不会删除该能力。你将不得不单独移除该能力——通过传递实际的 ``$ post`` 作为第二个参数——如下所示。

要删除特定模型实例的功能，请改用实际模型：

.. code-block:: php

    <?php
    Bouncer::disallow($user)->to('delete', $post);

.. note::  ``disallow`` 方法仅删除先前赋予此用户/角色的能力。如果你想不允许一个更普遍的能力允许的子集，使用禁止方法。

禁用一个能力
------------
``Bouncer`` 也可以让你禁止一个给定的能力，进行更细致的控制。有时您可能希望授予用户/角色涵盖各种操作的能力，但是会限制这些操作的一小部分。

例子如下：

- 您可能允许用户通常查看所有文档，但不应允许查看一个特定高度机密的文档：

  .. code-block:: php

    <?php
    Bouncer::allow($user)->to('view', Document::class);

    Bouncer::forbid($user)->to('view', $classifiedDocument);

- 您可能希望允许 ``superadmins`` 在您的应用中执行所有操作，包括添加/删除用户。那么你可能拥有一个 ``admin`` 角色，除了管理用户之外，他还可以做所有事情。

  .. code-block:: php

    <?php
    Bouncer::allow('superadmin')->everything();

    Bouncer::allow('admin')->everything();
    Bouncer::forbid('admin')->toManage(User::class);

- 您可能希望偶尔禁止用户，取消所有能力。然而，实际上移除他们所有的角色和能力意味着，当禁令被取消时，我们必须弄清楚他们原来的角色和能力是什么。

  使用禁止能力意味着他们可以保留他们现有的所有角色和能力，但仍然没有获得任何授权。我们可以通过创建一个特殊的禁止一切的禁止角色来实现这一点：

  .. code-block:: php
  
      <?php
      Bouncer::forbid('banned')->everything();
      // 然后，我们想要禁止一个用户，可以给它们赋予 banned 角色
      Bouncer::assign('banned')->to($user);
      // 为了移除禁令，我们简单地去除用户的角色
      Bouncer::retract('banned')->from($user);

正如您所看到的， ``Bouncer`` 的禁止能力可以让您对应用中的权限进行精细控制。

去除禁用权限
------------
要移除禁止的能力，请使用 ``unforbid`` 方法：

.. code-block:: php

    <?php
    Bouncer::unforbid($user)->to('view', $classifiedDocument);

.. note:: 这将消除任何以前禁止的能力。如果这个用户/角色授予的不同常规能力尚未允许，它将不会自动允许该能力。

检查用户的角色
--------------
.. note:: 一般来说，你不需要直接检查角色。最好让角色具备某些能力，然后检查这些能力。如果你需要的是非常一般的，你可以创造非常广泛的能力。例如， ``access-dashboard`` 功能总是比直接检查 ``admin`` 或 ``editor`` 角色更好。对于您确实想要检查角色的罕见场合，该功能在此处可用。

``Bouncer`` 可以检查用户是否有特定的角色：

.. code-block:: php

    <?php
    Bouncer::is($user)->a('moderator');

如果您要检查的角色是从元音开始，那么您可能需要使用别名方法：

.. code-block:: php

    <?php
    Bouncer::is($user)->an('admin');

相反，您还可以检查用户是否没有特定的角色：

.. code-block:: php

    <?php
    Bouncer::is($user)->notA('moderator');

    Bouncer::is($user)->notAn('admin');

你可以检查一个用户是否有许多角色之一：

.. code-block:: php

    <?php
    Bouncer::is($user)->a('moderator', 'editor');

您还可以检查用户是否具有所有给定的角色：

.. code-block:: php

    <?php
    Bouncer::is($user)->all('editor', 'moderator');

您还可以检查用户是否没有任何给定的角色：

.. code-block:: php

    <?php
    Bouncer::is($user)->notAn('editor', 'moderator');

这些检查也可以直接在用户上完成：

.. code-block:: php

    <?php
    $user->isAn('admin');
    $user->isA('subscriber');

    $user->isNotAn('admin');
    $user->isNotA('subscriber');

    $user->isAll('editor', 'moderator');

获取一个用户的所有角色
----------------------
您可以直接从用户模型获取用户的所有角色：

.. code-block:: php

    <?php
    $roles = $user->getRoles();

获取一个用户的所有能力
----------------------
您可以直接从用户模型获取用户的所有能力：

.. code-block:: php

    <?php
    $abilities = $user->getAbilities();

这将返回用户能力的集合，包括通过他们的角色授予用户的任何能力。

授权用户
---------
授权用户直接在 ``laravel`` 的 ``Gate`` 或用户模型（ ``$user->can($ability)`` ）上处理。

为了方便起见，``Bouncer`` 类提供了这些直通方法：

.. code-block:: php

    <?php
    Bouncer::can($ability);
    Bouncer::cannot($ability);
    Bouncer::authorize($ability);

它们直接调用了 ``Gate`` 类。

Blade指令
----------
``Bouncer`` 不添加自己的 ``Blade`` 指令。因为 ``Bouncer`` 直接与 ``laravel`` 的 ``Gate`` 合作，只需简单地使用 ``@can`` 指令来检查当前用户的能力：

.. code-block:: html

    @can ('update', $post)
        <a href="{{ route('post.update', $post) }}">Edit Post</a>
    @endcan

由于通常不建议直接检查角色，因此 ``Bouncer`` 无法为此提供单独的指令。如果你仍然坚持检查角色，你可以使用一般的 ``@if`` 指令来做到这一点：

.. code-block:: html

    @if ($user->isAn('admin'))
        //
    @endif

刷新缓存
--------
所有由 ``Bouncer`` 执行的查询都将为当前请求缓存。如果启用交叉请求缓存，则缓存将持续存到不同的请求中。

**当启用交叉缓存且改变用户角色/能力时，由你负责使用如下方法刷新缓存。**

只要你需要，你可以完全刷新 ``Bouncer`` 的缓存：

.. code-block:: php

    <?php
    Bouncer::refresh();

.. note:: 如果缓存标签可用，则使用缓存标记完全刷新所有用户的缓存。并非所有缓存驱动程序都支持此功能请参阅 ``laravel`` 的文档以查看您的驱动程序是否支持缓存标记。如果您的驱动程序不支持缓存标记，则调用刷新可能会稍微慢一点(具体依赖系统中的用户数量)。

或者，您可以仅为特定用户刷新缓存：

.. code-block:: php

    <?php
    Bouncer::refreshFor($user);

备忘单
======

.. code-block:: php

    <?php
    // Adding abilities for users
    Bouncer::allow($user)->to('ban-users');
    Bouncer::allow($user)->to('edit', Post::class);
    Bouncer::allow($user)->to('delete', $post);

    Bouncer::allow($user)->everything();
    Bouncer::allow($user)->toManage(Post::class);
    Bouncer::allow($user)->toManage($post);
    Bouncer::allow($user)->to('view')->everything();

    Bouncer::allow($user)->toOwn(Post::class);
    Bouncer::allow($user)->toOwnEverything();

    // Removing abilities uses the same syntax, e.g.
    Bouncer::disallow($user)->to('delete', $post);
    Bouncer::disallow($user)->toManage(Post::class);
    Bouncer::disallow($user)->toOwn(Post::class);

    // Adding & removing abilities for roles
    Bouncer::allow('admin')->to('ban-users');
    Bouncer::disallow('admin')->to('ban-users');

    // You can also forbid specific abilities with the same syntax...
    Bouncer::forbid($user)->to('delete', $post);

    // And also remove a forbidden ability with the same syntax...
    Bouncer::unforbid($user)->to('delete', $post);

    // Re-sync a user's abilities
    Bouncer::sync($user)->abilities($abilities);

    // Assigning & retracting roles from users
    Bouncer::assign('admin')->to($user);
    Bouncer::retract('admin')->from($user);

    // Re-sync a user's roles
    Bouncer::sync($user)->roles($roles);

    $boolean = Bouncer::can('ban-users');
    $boolean = Bouncer::can('edit', Post::class);
    $boolean = Bouncer::can('delete', $post);

    $boolean = Bouncer::cannot('ban-users');
    $boolean = Bouncer::cannot('edit', Post::class);
    $boolean = Bouncer::cannot('delete', $post);

    $boolean = Bouncer::is($user)->a('subscriber');
    $boolean = Bouncer::is($user)->an('admin');
    $boolean = Bouncer::is($user)->notA('subscriber');
    $boolean = Bouncer::is($user)->notAn('admin');
    $boolean = Bouncer::is($user)->a('moderator', 'editor');
    $boolean = Bouncer::is($user)->all('moderator', 'editor');

    Bouncer::cache();
    Bouncer::dontCache();

    Bouncer::refresh();
    Bouncer::refreshFor($user);

    ///有些功能也可以直接在用户模型上使用：

    $user->allow('ban-users');
    $user->allow('edit', Post::class);
    $user->allow('delete', $post);

    $user->disallow('ban-users');
    $user->disallow('edit', Post::class);
    $user->disallow('delete', $post);

    $user->assign('admin');
    $user->retract('admin');

    $boolean = $user->isAn('admin');
    $boolean = $user->isAn('editor', 'moderator');
    $boolean = $user->isAll('moderator', 'editor');
    $boolean = $user->isNotAn('admin', 'moderator');

    $abilities = $user->getAbilities();