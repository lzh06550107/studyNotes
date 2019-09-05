===========
用户认证JWT
===========

JWT
===

在介绍 ``JWT`` 之前, 我们首先介绍一下, 传统的服务器端使用 ``session`` 对多用户进行授权的方式. 当然在 ``session`` 之前还有 ``cookie`` 的方式来保存用户的授权信息在客户机(比如浏览器)上, 不过纯 ``cookie`` 的方式过于不安全, 我们就把 ``cookie`` 跟 ``session`` 一起说。

之所以需要授权机制, 是因为 ``http`` 的无状态性(stateless)。

也就是说当一个用户发送一次请求, 请求中附带账户名和密码登录成功之后, 如果这个这个用户再次发送一次请求, 服务器是不能知道这个用户是已经登录过的, 这个时候服务器就还需要用户再次提供授权信息, 也就是用户名和密码.

如果客户端能更少的把自己的身份授权信息在网络上传输, 那么客户端就能更大程度上避免自己的身份信息被泄露.

而 ``session`` 和 ``token`` 都是为了解决此问题出现的.

session 原理概述
----------------
认证流程
^^^^^^^^

1. 当用户使用用户名和密码登录之后，服务器就会生成一个 ``session`` 文件, ``session`` 文件中保存着对这个用户的授权信息，这个文件可以储存在硬盘/内存/数据库中。
2. 同时还要生成一个对应这个 ``session`` 文件的 ``sessionid`` 。通过 ``sessionid`` 就能够找到这个 ``session`` 文件。
3. 然后将 ``sessionid`` 发送给客户端，客户端就将 ``sessionid`` 保存起来，保存的方式有很多种。目前大多情况是通过 ``cookie`` 来保存 ``sessionid`` 。
4. 保存之后，当客户机以后再向服务器发送请求的时候，请求携带上 ``sessionid`` ，这样服务器收到 ``sessionid`` 之后, 自己就会在服务区上查找对应的 ``session`` 文件，如果查找成功。就会得到该用户的授权信息, 从而完成一次授权。

``session`` 的出现解决了一部分的问题，但随着时间的推移和互联网的发展，一些缺陷也随之暴露出来, 比如但不仅限于以下几点

- 随着用户量的增加, 每个用户都需要在服务器上创建一个 ``session`` 文件, 这对服务器造成了压力；
- 对于服务器压力的分流问题, 如果一个用户的 ``session`` 被存储在某台服务器上, 那么当这个用户访问服务器时, 用户就只能在这台服务器上完成授权, 其他的分流服务器无法进行对这种请求进行分流；
- 同样也是 ``session`` 存储的问题, 当我们在一台服务器上成功登录, 如果我们想要另外的一台别的域名的服务器也能让用户不登录就能完成授权, 这个时候就会有很多麻烦；
- CSRF 攻击的防范, 这点目前还没太懂, 不过可以先不做了解；

为了解决此类问题， ``token`` 应运而生了。

Token 原理概述
---------------
认证流程
^^^^^^^^

1. 客户端发送认证信息(一般就是用户名/密码)，向服务器发送请求；
2. 服务器验证客户端的认证信息，验证成功之后，服务器向客户端返回一个加密的 ``token`` (一般情况下就是一个字符串)；
3. 客户端存储( ``cookie`` ， ``session`` ， ``app`` 中都可以存储)这个 ``token`` ， 在之后每次向服务器发送请求时, 都携带上这个 ``token`` ；
4. 服务器验证这个 ``token`` 的合法性，只要验证通过，服务器就认为该请求是一个合法的请求；

JWT 概述
--------
``token`` 只是一种思路，一种解决用户授权问题的思考方式，基于这种思路，针对不同的场景可以有很多种的实现。而在众多的实现中， ``JWT`` (JSON Web Token) 的实现最为流行。

``JWT`` 这个标准提供了一系列如何创建具体 ``token`` 的方法，这些缘故方法和规范可以让我们创建 ``token`` 的过程变得更加合理和效率。

比如, 传统的做法中, 服务器会保存生成的 ``token`` ，当客户端发送来 ``token`` 时，与服务器的进行比对，但是 ``jwt`` 的不需要在服务器保存任何 ``token`` ，而是使用一套加密/解密算法 和 一个密钥 来对用户发来的 ``token`` 进行解密。解密成功后就可以得到这个用户的信息。

这样的做法同时也增加了多服务器时的扩展性，在传统的 ``token`` 验证中, 一旦用户发来 ``token`` , 那么必须要先找到存储这个 ``token`` 的服务器是哪台服务器，然后由那一台服务器进行验证用户身份。而 ``jwt`` 的存在, 只要每一台服务器都知道解密密钥, 那么每一台服务器都可以拥有验证用户身份的能力。

这样一来，服务器就不再保存任何用户授权的信息了，也就解决了 ``session`` 曾出现的问题。

Session   JWT
安全性   得考虑CSRF攻击   无需考虑
存储   需要俩端都存储   客户端存储即可
可控性   服务端可随时修改权限....    只能等待Token过期

什么是JWT
---------
Json web token (JWT), 是为了在网络应用环境间传递声明而执行的一种基于JSON的开放标准（(RFC 7519)。该 ``token`` 被设计为紧凑且安全的，特别适用于分布式站点的单点登录( ``SSO`` )场景。 ``JWT`` 的声明一般被用来在身份提供者和服务提供者间传递被认证的用户身份信息，以便于从资源服务器获取资源，也可以增加一些额外的其它业务逻辑所必须的声明信息，该 ``token`` 也可直接被用于认证，也可被加密。

起源
^^^^
说起 ``JWT`` ，我们应该来谈一谈基于 ``token`` 的认证和传统的 ``session`` 认证的区别。

传统的session认证
""""""""""""""""""
我们知道， ``http`` 协议本身是一种无状态的协议，而这就意味着如果用户向我们的应用提供了用户名和密码来进行用户认证，那么下一次请求时，用户还要再一次进行用户认证才行，因为根据 ``http`` 协议，我们并不能知道是哪个用户发出的请求，所以为了让我们的应用能识别是哪个用户发出的请求，我们只能在服务器存储一份用户登录的信息，这份登录信息会在响应时传递给浏览器，告诉其保存为 ``cookie`` ，以便下次请求时发送给我们的应用，这样我们的应用就能识别请求来自哪个用户了，这就是传统的基于session认证。

但是这种基于 ``session`` 的认证使应用本身很难得到扩展，随着不同客户端用户的增加，独立的服务器已无法承载更多的用户，而这时候基于 ``session`` 认证应用的问题就会暴露出来.

基于 session 认证所显露的问题

- Session: 每个用户经过我们的应用认证之后，我们的应用都要在服务端做一次记录，以方便用户下次请求的鉴别，通常而言 ``session`` 都是保存在内存中，而随着认证用户的增多，服务端的开销会明显增大。
- 扩展性: 用户认证之后，服务端做认证记录，如果认证的记录被保存在内存中的话，这意味着用户下次请求还必须要请求在这台服务器上，这样才能拿到授权的资源，这样在分布式的应用上，相应的限制了负载均衡器的能力。这也意味着限制了应用的扩展能力。
- CSRF: 因为是基于 cookie 来进行用户识别的， ``cookie`` 如果被截获，用户就会很容易受到跨站请求伪造的攻击。

基于token的鉴权机制
"""""""""""""""""""
基于 ``token`` 的鉴权机制类似于 ``http`` 协议也是无状态的，它不需要在服务端去保留用户的认证信息或者会话信息。这就意味着基于 ``token`` 认证机制的应用不需要去考虑用户在哪一台服务器登录了，这就为应用的扩展提供了便利。

流程上是这样的：

1. 用户使用用户名、密码来请求服务器；
2. 服务器进行验证用户的信息；
3. 服务器通过验证发送给用户一个 ``token`` ； 如何验证？？？
4. 客户端存储 ``token`` ，并在每次请求时附送上这个 ``token`` 值；
5. 服务端验证 ``token`` 值，并返回数据；

这个 ``token`` 必须要在每次请求时传递给服务端，它应该保存在请求头里， 另外，服务端要支持 ``CORS`` (跨来源资源共享)策略，一般我们在服务端这么做就可以了 ``Access-Control-Allow-Origin: *`` 。

那么我们现在回到 ``JWT`` 的主题上。

JWT长什么样？
------------
``JWT`` 是由三段信息构成的，将这三段信息文本用 ``.`` 链接一起就构成了 ``JWT`` 字符串。就像这样：

.. code-block:: shell

    eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ

JWT的构成
---------
第一部分我们称它为头部( ``header`` ) ，第二部分我们称其为载荷( ``payload`` ，类似于飞机上承载的物品)，第三部分是签证( ``signature`` )。

header
^^^^^^
``jwt`` 的头部承载两部分信息：

- 声明类型，这里是 ``jwt`` ；
- 声明加密的算法 通常直接使用 ``HMAC SHA256`` ；

完整的头部就像下面这样的 ``JSON`` :

.. code-block:: json

    {
      'typ': 'JWT',
      'alg': 'HS256'
    }

然后将头部进行 ``base64`` 加密（该加密是可以对称解密的)，构成了第一部分。

.. code-block:: shell

    eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9

playload
^^^^^^^^^
载荷就是存放有效信息的地方。这个名字像是特指飞机上承载的货品，这些有效信息包含三个部分

- 标准中注册的声明；
- 公共的声明；
- 私有的声明；

标准中注册的声明 (建议但不强制使用) ：

- iss: jwt签发者
- sub: jwt所面向的用户
- aud: 接收jwt的一方
- exp: jwt的过期时间，这个过期时间必须要大于签发时间
- nbf: 定义在什么时间之前，该jwt都是不可用的.
- iat: jwt的签发时间
- jti: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。

公共的声明：

公共的声明可以添加任何的信息，一般添加用户的相关信息或其他业务需要的必要信息。但不建议添加敏感信息，因为该部分在客户端可解密。

私有的声明：

私有声明是提供者和消费者所共同定义的声明，一般不建议存放敏感信息，因为 ``base64`` 是对称解密的，意味着该部分信息可以归类为明文信息。

定义一个payload:

.. code-block:: json

    {
      "sub": "1234567890",
      "name": "John Doe",
      "admin": true
    }

然后将其进行 ``base64`` 加密，得到 ``Jwt`` 的第二部分。

.. code-block:: shell

    eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9

signature
^^^^^^^^^
``jwt`` 的第三部分是一个签证信息，这个签证信息由三部分组成：

- header (base64后的)
- payload (base64后的)
- secret

这个部分需要 ``base64`` 加密后的 ``header`` 和 ``base64`` 加密后的 ``payload`` 使用 ``.`` 连接组成的字符串，然后通过 ``header`` 中声明的加密方式进行加盐 ``secret`` 组合加密，然后就构成了 ``jwt`` 的第三部分。

.. code-block:: js

    // javascript
    var encodedString = base64UrlEncode(header) + '.' + base64UrlEncode(payload);

    var signature = HMACSHA256(encodedString, 'secret'); // TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ

将这三部分用 ``.`` 连接成一个完整的字符串，构成了最终的 ``jwt`` ：

.. code-block:: shell

    eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ

.. note:: ``secret`` 是保存在服务器端的， ``jwt`` 的签发生成也是在服务器端的， ``secret`` 就是用来进行 ``jwt`` 的签发和 ``jwt`` 的验证，所以，它就是你服务端的私钥，在任何场景都不应该流露出去。一旦客户端得知这个 ``secret`` ，那就意味着客户端是可以自我签发 ``jwt`` 了。

如何应用
---------
一般是在请求头里加入 ``Authorization`` ，并加上 ``Bearer`` 标注：

.. code-block:: js

    fetch('api/user/1', {
      headers: {
        'Authorization': 'Bearer ' + token
      }
    })

服务端会验证 ``token`` ，如果验证通过就会返回相应的资源。整个流程就是这样的:

https://upload-images.jianshu.io/upload_images/1821058-2e28fe6c997a60c9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/700

总结
====

优点
----

- 因为 ``json`` 的通用性，所以 ``JWT`` 是可以进行跨语言支持的，像 ``JAVA`` ， ``JavaScript`` ， ``NodeJS`` ， ``PHP`` 等很多语言都可以使用。
- 因为有了 ``payload`` 部分，所以 ``JWT`` 可以在自身存储一些其他业务逻辑所必要的非敏感信息。
- 便于传输， ``jwt`` 的构成非常简单，字节占用很小，所以它是非常便于传输的。
- 它不需要在服务端保存会话信息，所以它易于应用的扩展。

安全相关
--------

- 不应该在 ``jwt`` 的 ``payload`` 部分存放敏感信息，因为该部分是客户端可解密的部分。
- 保护好 ``secret`` 私钥，该私钥非常重要。
- 如果可以，请使用 ``https`` 协议。


Laravel中认证
=============
说到这里。不妨了解一下 Laravel 中服务器授权方式有哪些.

打开一个 Laravel 项目 ,来到 ``app/Http/Kernel`` 文件中, 我们可以看到有：

.. code-block:: php

    <?php
    protected $routeMiddleware = [
        'auth' => \App\Http\Middleware\Authenticate::class,
        'auth.basic' => \Illuminate\Auth\Middleware\AuthenticateWithBasicAuth::class,
        'guest' => \App\Http\Middleware\RedirectIfAuthenticated::class,
    ];

Laravel 已经我们提供了 3 种授权方式, 并以中间件的方式来提供给我们使用。

auth
----
``auth`` 中间件可以帮我们实现普通的 ``Web`` 页面中的用户密码验证，如果你使用 ``API`` 的方式来提供数据接口，也可以使用 ``token`` 的方式来进行验证。

auth.basic
-----------
也是一种最基础的验证方式，当你请求一个要求授权的 ``url`` 时，需要在这个请求的 ``Header`` 中加入相关的授权信息，比如账号和密码。如果在 ``web`` 请求时加上这个中间件，会在页面中弹出一个输入账号密码的对话框，输入正确的信息后才能继续访问该页面。

guest
------
``guest`` 中间件, 在代码中指向了 ``RedirectIfAuthenticated::class`` 这个类，从类名的定义我们就可以看出，这个中间件的作用就是 如果用户访问了这个中间件下的页面时已经授权成功了， 比如说已经登录过了，那么就会帮我们跳转到某个另外的 ``url`` , 如果没有登录，就继续当前的请求动作。

上面的这些中间件，帮我们管理了一部分的用户授权功能，他们的实现方式大都是通过 Laravel 框架的 ``Auth`` 组件(主要是通过 ``AuthManager`` 这个类)配合 ``Guard`` 类来实现的。

所以按照 Laravel 的思路，我们的授权功能应该还是用中间件的方式来完成。

Laravel 推出了自己的用户认证模块 "Passport", 应该使用吗?
========================================================
在这篇文章完成之前, Laravel 又推出了一个用户认证的高级实现工具, 也就是 Laravel ``Passport`` , 毕竟是 Laravel 家族的产品, 所以刚发布的时候我也想要去使用他, 研究一番之后才发现,  ``Passport`` 并不是适合所有场景的, 下面我们就来对比一下.

这两者都是用来做用户认证的，但是有几点比较重要的不同点。

相比于 ``jwt`` ， ``passport`` 是一个大的多的抽象层，他被设计成一个完全成熟的(同时也要容易配置和使用)  ``Oauth2`` 服务来使用。因为在设计之初,  ``passport`` 的目标就被设计的非常专一，所以在别的使用场景下， ``passport`` 的功能也很难被复用。而且， ``passport`` 内部有一部分使用了 ``jwt`` 来实现认证。

而 ``jwt`` 的设计目的更倾向于，是一个简单的用户认证方式。当然 ``jwt`` 能实现的功能是十分强大的，但他跟 ``Oauth`` 并没有什么关系。

所以一般来说, 你能用 ``jwt`` 完成的需求，用 ``passport`` 一定也能完成，但是并不推荐使用 ``passport`` , 除非你有 ``Oauth`` 相关的需求。如果你真的需要一台支持 ``Oauth`` 的服务器，那么我完全推荐你使用 ``Passport`` ，因为他非常容易配置。

还有一点需要注意, 服务器允许其他 ``Oauth`` 用户登录, 跟服务器自身提供 ``Oauth`` 服务是2个概念。如果你想要的只是类似于 "使用 Google 帐号登录" 这种功能，那么你应该使用 ``Laravel/socialite`` 。服务器自身提供 ``Oauth`` 服务意味着，别的网站的开发者可以使用你提供的用户认证信息来登录到他们的服务器上。

如果不想使用 JWT 和 Passport, Laravel 能快速实现使用 token 的用户认证机制吗?
============================================================================

答案是可以的。 Larave 自身的代码已经实现了大部分功能, 所以我们只要稍作修改, 就能实现基本的 ``token`` 认证了。

步骤如下:

1. 新建一个 ``Laravel 5.5.*`` 的项目；
2. 打开 ``database/migrations/2014_10_12_000000_create_users_table.php`` 这个 ``migration`` 文件，我们需要更改 ``user`` 表的结构；
3. 我们需要为 ``user`` 表添加 ``api_token`` 字段，也就是说我们的 ``token`` 是保存在数据库中的，在合适的位置， 添加一行 ``$table->string('api_token', 60)->unique();`` ；
4. 配置好数据库，通过 ``php artisan migrate`` 命令生成 ``user`` 表；
5. 在 ``user`` 表中，随便添加一条记录，只要保证 ``api_token`` 这个字段设置为 ``123456`` 即可。这样我们就生成了一个用户，等下就可以使用 ``123456`` 这个 ``token`` 值来登录了；
6. 返回到 路由文件 ``routes.php`` ，在里面添加一条测试路由，并将其用 laravel 的中间件保护起来；

   .. code-block:: php

    <?php
    Route::group(['middleware' => 'auth:api'], function () {
      Route::get('/t', function () {
          return 'ok';
      });
    });

   在此处, 使用的是 ``auth`` 中间件， ``:api`` 的意思是说，我们指定了 ``auth`` 中间件的 ``driver`` 为 ``api`` , 而如果不指定的话， ``driver`` 默认使用的是 ``web`` 。

7. 修改 ``app/Http/Middleware/Authenticate.php`` 文件, 精简一下 handle 这个方法, 精简之后成为下面的样子。

   .. code-block:: php

    <?php
    public function handle($request, Closure $next, $guard = null)
    {
      if (Auth::guard($guard)->guest()) {
          return response('没有设置token.', 401);
      }

      return $next($request);
    }

8. 做了以上修改之后，当我们以 ``/t`` 这个 ``url`` 路径向服务器直接发起请求时，服务器就会返回一个 ``401`` 错误，并且会返回一条 ``没有设置token.`` 这样的消息，这也是我们之前在 ``handle()`` 方法中设置的。也就是说 ``/t`` 已经被我们的 ``auth`` 中间件保护起来了。如果想要我们的请求能够正常通过这个中间件，就要提供 ``token`` 。
由于我们之前在 ``user`` 表中添加了一条 ``api_token`` 为 ``123456`` 的数据，所以现在我们再次向服务器请求 ``/t`` ，但是这次我们加入 ``api_token`` ，也就是 ``.../t?api_token=123456``

正常情况下，服务器就会返回 'ok' 了，这也就是说明， ``auth`` 中间件允许这个请求通过。而当我们把 ``123456`` 修改为其他值时，这个请求也是无法通过 ``auth`` 中间件的。

当然，上面只是最简单的实现方式，在实际的应用中会比这个更加复杂。复杂的情况可以使用 `jwt-auth <>`_

Laravel使用JWT认证
==================
我们可以提供使用 ``JWT`` 来轻松的实现我们需要的 ``API`` 级别的用户认证功能。在 Laravel 中使用 ``JWT`` 我们通常使用 `jwt-auth <https://github.com/tymondesigns/jwt-auth>`_

安装
----

``jwt-auth`` 最新版本是 ``1.0.0 rc.1`` 版本，已经支持了 Laravel 5.5 。如果你使用的是 Laravel 5.5 版本，可以使用如下命令安装。根据评论区 @tradzero 兄弟的建议，如果你是 Laravel 5.5 以下版本，也推荐使用最新版本， RC.1 前的版本都存在多用户 ``token`` 认证的安全问题。

.. code-block:: shell

    $ composer require tymon/jwt-auth 1.0.0-rc.1

.. note:: 一定要注意带上版本，否则下载的包不匹配。

配置
----

添加服务提供商
^^^^^^^^^^^^^^^
将下面这行添加至 ``config/app.php`` 文件 ``providers`` 数组中：

.. code-block:: php

    'providers' => [

        ...

        Tymon\JWTAuth\Providers\JWTAuthServiceProvider::class,
    ]

.. note:: 5.5存在自动包门面和服务提供器注册，所以没有必要手动添加。

发布配置文件
^^^^^^^^^^^^
在你的 ``shell`` 中运行如下命令发布 ``jwt-auth`` 的配置文件：

.. code-block:: shell

    php artisan vendor:publish --provider="Tymon\JWTAuth\Providers\LaravelServiceProvider"

此命令会在 ``config`` 目录下生成一个 ``jwt.php`` 配置文件，你可以在此进行自定义配置。

.. note:: 由于前面已经添加了服务提供器，所以上面命令的参数可以省略。

生成密钥
^^^^^^^^
``jwt-auth`` 已经预先定义好了一个 ``Artisan`` 命令方便你生成 ``Secret`` ，你只需要在你的 ``shell`` 中运行如下命令即可：

.. code-block:: shell

    php artisan jwt:secret

此命令会在你的 ``.env`` 文件中新增一行 ``JWT_SECRET=secret`` 。它是用于签署令牌的密钥。

配置 Auth guard
^^^^^^^^^^^^^^^^
在 ``config/auth.php`` 文件中，你需要将 ``guards/driver`` 更新为 ``jwt`` ：

.. code-block:: php

    <?php
    'defaults' => [
        'guard' => 'api',
        'passwords' => 'users',
    ],

    ...

    'guards' => [
        'api' => [
            'driver' => 'jwt',
            'provider' => 'users',
        ],
    ],

.. note:: 只有在使用 Laravel 5.2 及以上版本的情况下才能使用。

更改 Model
----------
如果需要使用 ``jwt-auth`` 作为用户认证，我们需要对我们的 ``User`` 模型进行一点小小的改变，实现一个接口，变更后的 ``User`` 模型如下：

.. code-block:: php

    <?php

    namespace App;

    use Tymon\JWTAuth\Contracts\JWTSubject;
    use Illuminate\Notifications\Notifiable;
    use Illuminate\Foundation\Auth\User as Authenticatable;

    class User extends Authenticatable implements JWTSubject
    {
        use Notifiable;

        // Rest omitted for brevity

        /**
         * Get the identifier that will be stored in the subject claim of the JWT.
         *
         * @return mixed
         */
        public function getJWTIdentifier()
        {
            return $this->getKey();
        }

        /**
         * Return a key value array, containing any custom claims to be added to the JWT.
         *
         * @return array
         */
        public function getJWTCustomClaims()
        {
            return [];
        }
    }

增加一些认证路由
----------------

首先让我们在 ``routes/api.php`` 中添加一些路由，如下所示：

.. code-block:: php

    <?php
    Route::group([
        'prefix' => 'auth'
    ], function () {

        Route::post('login', 'API\AuthController@login');
        Route::post('logout', 'API\AuthController@logout');
        Route::post('refresh', 'API\AuthController@refresh');
        Route::post('me', 'API\AuthController@me');

    });

创建AuthController
------------------
手动或者使用artisan命令创建控制器：

.. code-block:: shell

    php artisan make:controller API/AuthController

.. code-block:: php

    <?php

    namespace App\Http\Controllers\API;

    use Illuminate\Http\Request;
    use App\Http\Controllers\Controller;

    class AuthController extends Controller
    {
        /**
         * Create a new AuthController instance.
         *
         * @return void
         */
        public function __construct()
        {
            $this->middleware('auth:api', ['except' => ['login']]);
        }

        /**
         * Get a JWT via given credentials.
         *
         * @return \Illuminate\Http\JsonResponse
         */
        public function login()
        {
            $credentials = request(['email', 'password']);
            // 注意这里需要指定守护者，非默认的
            if (! $token = auth('api')->attempt($credentials)) {
                return response()->json(['error' => 'Unauthorized'], 401);
            }

            return $this->respondWithToken($token);
        }

        /**
         * Get the authenticated User.
         *
         * @return \Illuminate\Http\JsonResponse
         */
        public function me()
        {
            return response()->json(auth()->user());
        }

        /**
         * Log the user out (Invalidate the token).
         *
         * @return \Illuminate\Http\JsonResponse
         */
        public function logout()
        {
            auth()->logout();

            return response()->json(['message' => 'Successfully logged out']);
        }

        /**
         * Refresh a token.
         *
         * @return \Illuminate\Http\JsonResponse
         */
        public function refresh()
        {
            return $this->respondWithToken(auth()->refresh());
        }

        /**
         * Get the token array structure.
         *
         * @param  string $token
         *
         * @return \Illuminate\Http\JsonResponse
         */
        protected function respondWithToken($token)
        {
            return response()->json([
                'access_token' => $token,
                'token_type' => 'bearer',
                'expires_in' => auth('api')->factory()->getTTL() * 60
            ]);
        }
    }

使用包中间件
============

``jwt-auth`` 包中存在如下的中间件：

- jwt.auth ：认证；
- jwt.check ：检查认证；
- jwt.refresh ： 刷新token；
- jwt.renew ： 认证后立即刷新token；

定义路由
--------

在 ``routes/api.php`` 中，更新给定 ``api`` 路由：

.. code-block:: php

    <?php
    Route::post('login', 'API\AuthController@login');
    Route::middleware('jwt.auth')->group(function(){

        Route::get('logout', 'API\AuthController@logout');
    });

控制器代码如下：

.. code-block:: php

    <?php
    class AuthController extends Controller
    {
        /**
         * API Login, on success return JWT Auth token
         * @param Request $request
         * @return JsonResponse
         */
        public function login(Request $request)
        {
            $credentials = $request->only('email', 'password');
            $rules = [
                'email' => 'required|email',
                'password' => 'required',
            ];
            $validator = Validator::make($credentials, $rules);
            if($validator->fails()) {
                return response()->json([
                    'status' => 'error',
                    'message' => $validator->messages()
                ]);
            }
            try {
                // Attempt to verify the credentials and create a token for the user
                if (! $token = JWTAuth::attempt($credentials)) {
                    return response()->json([
                        'status' => 'error',
                        'message' => 'We can`t find an account with this credentials.'
                    ], 401);
                }
            } catch (JWTException $e) {
                // Something went wrong with JWT Auth.
                return response()->json([
                    'status' => 'error',
                    'message' => 'Failed to login, please try again.'
                ], 500);
            }
            // All good so return the token
            return response()->json([
                'status' => 'success',
                'data'=> [
                    'token' => $token
                    // You can add more details here as per you requirment.
                ]
            ]);
        }
        /**
         * Logout
         * Invalidate the token. User have to relogin to get a new token.
         * @param Request $request 'header'
         */
        public function logout(Request $request)
        {
            // Get JWT Token from the request header key "Authorization"
            $token = $request->header('Authorization');
            // Invalidate the token
            try {
                JWTAuth::invalidate($token);
                return response()->json([
                    'status' => 'success',
                    'message'=> "User successfully logged out."
                ]);
            } catch (JWTException $e) {
                // something went wrong whilst attempting to encode the token
                return response()->json([
                  'status' => 'error',
                  'message' => 'Failed to logout, please try again.'
                ], 500);
            }
        }
    }


参考资料：

http://www.ruanyifeng.com/blog/2018/07/json_web_token-tutorial.html

https://segmentfault.com/a/1190000012606246

https://jwt.io/introduction/

https://jwt-auth.readthedocs.io/en/develop/quick-start/












