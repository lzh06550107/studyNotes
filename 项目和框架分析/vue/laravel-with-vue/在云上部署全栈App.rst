*****************
在云上部署全栈App
*****************
现在 ``Vuebnb`` 的功能已经完成，最后一步是将其部署到生产环境中。我们将使用两种免费服务， ``Heroku`` 和 ``KeyCDN`` ，与全世界分享 ``Vuebnb`` 。

本章涉及的主题：

- ``Heroku`` 云平台服务简介；
- 将 ``Vuebnb`` 作为免费应用程序部署到 ``Heroku`` ；
- ``CDN`` 如何提高全栈应用程序的性能；
- 将免费 ``CDN`` 与 ``Laravel`` 集成；
- 以生产模式构建资源文件以提高性能和安全性；

Heroku
======
``Heroku`` 是一个用于 ``Web`` 应用程序的云平台服务。它在开发人员中非常受欢迎，因为它提供了在线获取应用程序的简单性和经济性。

``Heroku`` 应用程序可以用各种语言编写，包括 ``PHP`` ， ``JavaScript`` 和 ``Ruby`` 。 除了 ``Web`` 服务器之外， ``Heroku`` 还提供各种附加组件，例如数据库，电子邮件服务和应用程序监视。

``Heroku`` 应用程序可以免费部署，但有一些限制，例如，应用程序将在一段时间不活动后休眠，使其响应缓慢。如果您升级到付费服务，则会解除这些限制。

我们现在将 ``Vuebnb`` 部署到 ``Heroku`` 平台。 第一步是通过访问以下 ``URL`` 来创建帐户： ``https://signup.heroku.com`` 。

CLI
---
使用 ``Heroku`` 最方便的方法是从命令行。请访问以下 ``URL`` 并按照安装步骤操作： ``https://devcenter.heroku.com/articles/heroku-cli`` 。

一旦安装 ``CLI`` 后，从终端登录 ``Heroku`` 。验证您的凭据后，您将能够使用 ``CLI`` 创建和管理您的 ``Heroku`` 应用程序：

.. code-block:: shell

    $ heroku login

    # Enter your Heroku credentials:
    # Email: anthony@vuejsdevelopers.com
    # Password: ************
    # Logged in as anthony@vuejsdevelopers.com

创建一个app
-----------
现在让我们创建一个新的 ``Heroku`` 应用程序。新应用程序需要一个唯一的名称，因此请根据您自己的选择在下面的命令中替换 ``vuebnbapp`` 。 该名称将成为应用程序 ``URL`` 的一部分，因此请确保其简短且令人难忘：

.. code-block:: shell

    $ heroku create vuebnbapp

创建应用程序后，您将获得 ``URL`` ，例如： ``https://vuebnbapp.herokuapp.com/`` 。将它放在浏览器中，您将看到此默认消息：

.. image:: ./images/10-1.png

.. tip:: 新的 ``Heroku`` 应用程序被分配了一个免费域名，例如： ``appname.herokuapp.com`` ，但您也可以使用自己的自定义域名。有关更多信息，请访问 ``Heroku`` 开发中心，网址为 https://devcenter.heroku.com 。

源码
----
要将代码部署到 ``Heroku`` 应用程序，您可以使用 ``Heroku`` 的 ``Git`` 服务器。使用 ``CLI`` 创建应用程序时，会自动将新的远程存储库添加到您的 ``Git`` 项目中。 使用以下命令确认：

.. code-block:: shell

    $ git remote -v

    heroku  https://git.heroku.com/vuebnbapp.git (fetch)
    heroku  https://git.heroku.com/vuebnbapp.git (push)
    origin  git@github.com:fsvwd/vuebnb.git (fetch)
    origin  git@github.com:fsvwd/vuebnb.git (push)

一旦我们完成了应用程序的配置，我们将首先推送。 ``Heroku`` 将使用此代码构建应用程序。

环境变量
--------
``Heroku`` 应用程序有一个短暂的文件系统，只包含最新 ``Git`` 推送的代码。这意味着 ``Vuebnb`` 不会出现 ``.env`` 文件，因为此文件未提交给源代码。

环境变量由 ``Heroku CLI`` 使用 ``heroku config`` 命令设置。让我们从设置应用密钥开始。使用您自己的应用密钥替换以下值：

.. code-block:: shell

    $ heroku config:set APP_KEY=base64:mDZ5lQnC2Hq+M6G2iesFzxRxpr+vKJSl+8bbGs=

创建一个数据库
--------------
我们的生产应用需要一个数据库。 ``Heroku`` 的 ``ClearDB`` 插件提供了一个易于设置和连接的 ``MySQL`` 云数据库。

这个附加组件每个月都可以免费进行有限数量的交易。但是，在添加数据库之前，您需要验证您的 ``Heroku`` 帐户，这意味着即使您使用免费计划，也需要提供信用卡详细信息。

要验证您的 ``Heroku`` 帐户，请访问以下 URL： https://heroku.com/verify 。

完成后，使用以下命令创建一个新的 ``ClearDB`` 数据库：

.. code-block:: shell

    $ heroku addons:create cleardb:ignite

默认字符串长度
^^^^^^^^^^^^^^
在撰写本文时， ``ClearDB`` 使用 ``MySQL`` 版本 ``5.5`` ，而我们的 ``Homestead`` 数据库是 ``MySQL 5.7`` 。 ``MySQL 5.5`` 中的默认字符串长度对于 ``Passport`` 授权密钥来说太短了，因此我们需要在生产应用程序中运行数据库迁移之前在应用服务提供者中手动设置默认字符串长度。

app/Providers/AppServiceProvider.php:

.. code-block:: php

    <?php

    ...

    use Illuminate\Support\Facades\Schema;

    class AppServiceProvider extends ServiceProvider
    {
      ...

      public function boot()
      {
        Schema::defaultStringLength(191);
      }

      ...
    }

配置
^^^^
安装 ``ClearDB`` 附加组件时，会自动设置新的环境变量 ``CLEARDB_DATABASE_URL`` 。 让我们使用 ``heroku config:get`` 命令读取它的值：

.. code-block:: shell

    $ heroku config:get CLEARDB_DATABASE_URL

    # mysql://b221344377ce82c:398z940v@us-cdbr-iron-east-03.cleardb.net/heroku_n0b30ea856af46f?reconnect=true

在 Laravel 项目中，通过设置 ``DB_HOST`` 和 ``DB_DATABASE`` 的值来连接数据库。我们可以从 ``CLEARDB_DATABASE_URL`` 变量中提取这些值，其形式如下：

.. code-block:: shell

    mysql://[DB_USERNAME]:[DB_PASSWORD]@[DB_HOST]/[DB_DATABASE]?reconnect=true

提取值后，在 ``Heroku`` 应用程序中设置适用的环境变量：

.. code-block:: shell

    $ heroku config:set \
    DB_HOST=us-cdbr-iron-east-03.cleardb.net \
    DB_DATABASE=heroku_n0b30ea856af46f \
    DB_USERNAME=b221344377ce82c \
    DB_PASSWORD=398z940v

配置web服务器
-------------
``Heroku`` 的 ``Web`` 服务器配置是通过名为 ``Procfile`` （无文件扩展名）的特殊文件完成的，该文件位于项目目录的根目录中。

我们现在创建该文件：

.. code-block:: shell

    $ touch Procfile

``Procfile`` 的每一行都是一个声明，告诉 ``Heroku`` 如何运行你的应用程序的各个部分。 让我们现在为 ``Vuebnb`` 创建一个 ``Procfile`` 并添加这个单一的声明。

Procfile:

.. code-block:: ini

    web: vendor/bin/heroku-php-apache2 public/

冒号左侧的部分是处理类型。 ``Web`` 处理类型定义在应用程序中发送 ``HTTP`` 请求的位置。右边的部分是运行或启动该过程的命令。我们将到 ``Apache`` 服务器请求路由指向应用程序 ``public`` 目录。

Passport keys
-------------
在第9章“使用 ``Passport`` 添加用户登录和 ``API`` 身份验证”中，我们使用 ``php artisan passport:install`` 命令为 ``Passport`` 创建了加密密钥。这些密钥存储在可以在存储目录中找到的文本文件中。

加密密钥不应受版本控制，因为这会使它们不安全。相反，我们需要在每次部署时重新生成这些密钥。我们可以通过在我们的 composer 文件中添加安装后脚本来完成此操作。

composer.json:

.. code-block:: json

    "scripts": {
      ...
      "post-install-cmd": [
        "Illuminate\\Foundation\\ComposerScripts::postInstall",
        "php artisan optimize",
        "php artisan passport:install"
      ],
    }

部署
----
我们已经完成了所有必要的设置和配置，因此我们现在已准备好部署 ``Vuebnb`` 。确保将任何文件更改提交到 ``Git`` 存储库并推送到 ``Heroku Git`` 服务器的主分支：

.. code-block:: shell

    $ git add --all
    $ git commit -m "Ready for deployment!"
    $ git push heroku master

在推送期间，您将看到类似于以下内容的输出：

.. image:: ./images/10-2.png

.. tip:: 您的 ``Heroku`` 应用程序需要调试有什么问题吗？ ``heroku logs --tail`` 将显示 ``Heroku`` 应用程序的终端输出。 您还可以设置 ``APP_DEBUG = true`` 环境变量来调试 ``Laravel`` 。 但是，当你完成时，请记住将其设置为 ``false`` 。

迁移和填充
----------
部署完成后，我们将迁移表并为数据库设定种子。您可以通过 ``Heroku CLI`` 在生产应用程序上运行 ``Artisan`` 和其他应用程序命令，方法是使用 ``heroku run`` ：

.. code-block:: shell

    $ heroku run php artisan migrate --seed

一旦迁移和播种完成后，我们可以尝试通过浏览器查看应用程序。应该提供该页面，但您会看到这些混合内容错误：

.. image:: ./images/10-3.png

修复这些错误无济于事，因为所引用的文件无论如何都不在服务器上。我们先来讨论这个问题。

服务静态资源
============
由于我们的静态资产（即 ``CSS`` ， ``JavaScript`` 和图像文件）不受版本控制，因此尚未将其部署到我们的 ``Heroku`` 应用服务器。

不过，这是可以的，因为更好的选择是通过 ``CDN`` 为他们服务。在本章的这一部分，我们将使用 ``KeyCDN`` 建立一个帐户，并从那里提供我们的静态资产。

内容分发网络
------------
当服务器收到传入的 ``HTTP`` 请求时，它通常使用以下两种类型的内容之一进行响应：动态或静态。动态内容包括网页或包含特定于该请求的数据的 ``AJAX`` 响应，例如，通过 ``Blade`` 插入用户数据的网页。

静态内容包括在请求之间不会更改的图像， ``JavaScript`` 和 ``CSS`` 文件。使用 ``Web`` 服务器提供静态内容效率很低，因为它不必要地使用服务器资源来简单地返回文件。

内容交付网络( ``CDN`` )是一个服务器网络，通常位于世界各地的不同位置，经过优化，可以比典型的 ``Web`` 服务器更快，更便宜地提供静态资产。

KeyCDN
-------
有许多不同的 ``CDN`` 服务可用，但在本书中我们将使用 ``KeyCDN`` ，因为它提供易于使用的服务和免费使用层。

让我们通过访问此链接并按照说明注册帐户： https://app.keycdn.com/signup 。

创建并确认新的 ``KeyCDN`` 帐户后，请访问以下链接添加新区域。区域只是资源文件的集合； 对于您使用 ``KeyCDN`` 管理的每个网站，您可能都有不同的区域。调用您的新区域 ``vuebnb`` 并确保它是 ``Push`` 区域类型，这将允许我们使用 ``FTP`` 添加文件： https://app.keycdn.com/zones/add 。

使用FTP上传文件
---------------
我们现在将使用 ``FTP`` 将静态资产推送到 ``CDN`` 。您可以使用 ``Filezilla`` 等 ``FTP`` 实用程序来执行此操作，但我已经在项目中添加了一个 ``Node`` 脚本 ``scripts/ftp.js`` ，允许您使用简单的命令执行此操作。

该脚本需要一些 ``NPM`` 包，因此首先安装它们：

.. code-block:: shell

    $ npm i --save-dev dotenv ftp recursive-readdir

环境变量
^^^^^^^^
为了连接到您的 ``KeyCDN`` 帐户， ``FTP`` 脚本需要设置一些环境变量。让我们创建一个名为 ``.env.node`` 的新文件，以使此配置与主 ``Laravel`` 项目分开：

.. code-block:: shell

    $ touch .env.node

用于 ``KeyCDN`` 的 ``FTP URL`` 是 ``ftp.keycdn.com`` 。用户名和密码与您创建帐户的用户名和密码相同，因此请务必替换以下代码中的值。远程目录将与您创建的区域的名称相同。

.env.node:

.. code-block:: ini

    FTP_HOST=ftp.keycdn.com
    FTP_USER=anthonygore
    FTP_PWD=*********
    FTP_REMOTE_DIR=vuebnb
    FTP_SKIP_IMAGES=0

Skipping图片
^^^^^^^^^^^^
我们需要传输到 ``CDN`` 的文件位于 ``public/css`` ， ``public/js`` ， ``public/fonts`` 和 ``public/images`` 目录中。 ``FTP`` 脚本已配置为递归复制这些。

但是，如果将 ``FTP_SKIP_IMAGES`` 环境变量设置为 ``true`` ，则脚本将忽略 ``public/images`` 中的所有文件。在第一次运行脚本后，您将要执行此操作，因为图像不会更改并需要很长时间才能传输。

.env.node:

.. code-block:: ini

    FTP_SKIP_IMAGES=1

你可以在 ``scripts/ftp.js`` 中看到它是如何生效的：

.. code-block:: js

    let folders = [
      'css',
      'js',
      'fonts'
    ];

    if (process.env.FTP_SKIP_IMAGES == 0) {
      folders.push('images');
    }

NPM脚本
^^^^^^^
为了便于使用 ``FTP`` 脚本，请将以下脚本定义添加到 ``package.json`` 文件中。

package.json:

.. code-block:: json

    "ftp-deploy-with-images": "cross-env node ./ftp.js",
    "ftp-deploy": "cross-env FTP_SKIP_IMAGES=1 node ./ftp.js"

生成构建
---------
在运行 ``FTP`` 脚本之前，请务必先使用 ``npm run prod`` 命令构建应用程序以进行生产。 这将运行带有 ``NODE_ENV = production`` 变量集的 ``Webpack`` 构建。

生产构建可确保您的资产针对生产环境进行优化。例如，当 ``Vue.js`` 在生产模式下捆绑时，它将不包含警告和提示，并将禁用 ``Vue Devtools`` 。 您可以从 ``vue.runtime.common.js`` 模块的这个片段中看到这是如何实现的。

node_modules/vue/dist/vue.runtime.common.js:

.. code-block:: js

    /**
     * Show production mode tip message on boot?
     */
    productionTip: process.env.NODE_ENV !== 'production',

    /**
     * Whether to enable devtools
     */
    devtools: process.env.NODE_ENV !== 'production',

``Webpack`` 还将在生产构建期间运行某些仅限生产的插件，以确保您的捆绑文件尽可能小且安全。

运行FTP脚本
-----------
第一次运行 ``FTP`` 脚本时，您需要复制所有文件，包括图像。这需要一些时间，大概 20 到 30 分钟，具体取决于您的 Internet 连接速度：

.. code-block:: shell

    $ npm run prod && npm run ftp-deploy-with-images

传输完成后，上传的文件将在区域 ``URL`` 中可用，例如 http://vuebnb-9c0f.kxcdn.com 。 文件的路径将相对于 ``public`` 文件夹，例如， ``public/css/vue-style.css`` 将在 ``[ZONE_URL]/css/vue-style.css`` 中提供。

测试几个文件以确保传输成功：

.. image:: ./images/10-4.png

后续传输可以使用以下命令跳过图像：

.. code-block:: shell

    $ npm run prod && npm run ftp-deploy

从CDN读取
=========
我们现在希望 ``Vuebnb`` 在生产时从 ``CDN`` 而不是 ``Web`` 服务器加载任何静态资产。 为此，我们将创建自己的 Laravel 辅助方法。

目前，我们使用 ``asset`` 助手引用我们应用中的资产。此帮助程序返回该资产在 ``Web`` 服务器上的位置的完全限定 ``URL`` 。 例如，在我们的应用视图中，我们链接到 JavaScript 包文件，如下所示：

.. code-block:: html

    <script type="text/javascript" src="{{ asset('js/app.js') }}"></script>

我们的新助手，我们称之为 ``cdn`` ，将返回一个指向在 ``CDN`` 上资源的位置的 ``URL`` ：

.. code-block:: html

    <script type="text/javascript" src="{{ cdn('js/app.js') }}"></script>

CDN帮助器
---------
让我们从创建一个名为 ``helpers.php`` 的文件开始。这将声明一个新的 ``cdn`` 方法，目前，除了返回 ``asset`` 助手方法之外，它不会做任何事情。

app/helpers.php:

.. code-block:: php

    <?php

    if (!function_exists('cdn'))
    {
      function cdn($asset)
      {
        return asset($asset);
      }
    }

为确保可以在我们的应用程序中的任何位置使用此帮助程序，我们可以使用 ``Composer`` 的自动加载功能。这使得类或文件可用于所有其他文件，而无需手动 ``include`` 或 ``require`` 它。

composer.json:

.. code-block:: json

    "autoload": {
      "classmap": [ ... ],
      "psr-4": { ... },
      "files": [
        "app/helpers.php"
      ]
    },

每次修改 ``Composer`` 的自动加载声明时，都需要运行 ``dump-autoload`` ：

.. code-block:: shell

    composer dump-autoload

完成后， ``cdn`` 助手将可在我们的应用程序中使用。 让我们用 ``Tinker`` 测试一下：

.. code-block:: shell

    $ php artisan tinker
    >>>> cdn('js/app.js')
    => "http://vuebnb.test/js/app.js"

设置CDN链接
-----------
``cdn helper`` 方法需要知道 ``CDN`` 的 ``URL`` 。让我们设置一个 ``CDN_URL`` 环境变量，该变量将被分配给 ``Vuebnb`` 的区域 ``URL`` ，去除协议前缀。

在我们处理它时，让我们添加另一个变量 ``CDN_BYPASS`` ，它可以用来在本地开发环境中绕过我们不需要的 ``CDN`` 。

.env:

.. code-block:: ini

    CDN_URL=vuebnb-9c0f.kxcdn.com
    CDN_BYPASS=0

现在让我们在 ``app`` 配置文件中注册这些新变量。

config/app.php:

.. code-block:: php

    <?php

    return [
      ...
      // CDN

      'cdn' => [
        'url' => env('CDN_URL'),
        'bypass' => env('CDN_BYPASS', false),
      ],
    ];

现在我们可以完成 ``cdn helper`` 的逻辑了。

app/helpers.php:

.. code-block:: php

    <?php

    use Illuminate\Support\Facades\Config;

    if (!function_exists('cdn'))
    {
      function cdn($asset)
      {
        if (Config::get('app.cdn.bypass') || !Config::get('app.cdn.url')) {
          return asset($asset);
        } else {
          return  "//" . Config::get('app.cdn.url') . '/' . $asset;
        }
      }
    }

如果您已经打开 ``Tinker`` ，请退出并重新输入，并按预期测试更改：

.. code-block:: shell

    >>>> exit
    $ php artisan tinker
    >>>> cdn('js/app.js')
    => "//vuebnb-9c0f.kxcdn.com/js/app.js"

在Laravel使用CDN
----------------
现在让我们用 ``cdn helper`` 替换 ``Laravel`` 文件中 ``asset`` 助手的用法。

app/Http/Controllers/ListingController.php:

.. code-block:: php

    <?php

    ...

    class ListingController extends Controller
    {
      private function get_listing($listing)
      {
        ...
        for($i = 1; $i <=4; $i++) {
          $model['image_' . $i] = cdn(
            'images/' . $listing->id . '/Image_' . $i . '.jpg'
          );
        }
        ...
      }

      ...

      private function get_listing_summaries()
      {
        ...
        $collection->transform(function($listing) {
          $listing->thumb = cdn(
            'images/' . $listing->id . '/Image_1_thumb.jpg'
          );
          return $listing;
        });
        ...
      }

      ...
    }

resources/views/app.blade.php:

.. code-block:: html

    <html>
      <head>
        ...
        <link rel="stylesheet" href="{{ cdn('css/style.css') }}" type="text/css">
        <link rel="stylesheet" href="{{ cdn('css/vue-style.css') }}" type="text/css">
        ...
      </head>
      <body>
        ...
        <script src="{{ cdn('js/app.js') }}"></script>
      </body>
    </html>


在Vue中使用CDN
--------------
在我们的 ``Vue`` 应用程序中，我们也加载了一些静态资产。例如，在工具栏中我们使用徽标。

resources/assets/components/App.vue:

.. code-block:: html

    <img class="icon" src="/images/logo.png">

由于这是一个相对 ``URL`` ，因此默认情况下它将指向 ``Web`` 服务器。如果我们改为使用绝对 ``URL`` ，我们必须对 ``CDN URL`` 进行硬编码，这也不理想。

让我们让 ``Laravel`` 在文档的头部传递 ``CDN URL`` 。我们可以通过简单地使用空字符串调用 ``cdn helper`` 来完成此操作。

resources/views/app.blade.php:

.. code-block:: html

    <head>
      ...
      <script type="text/javascript">
         ...
         window.cdn_url = "{{ cdn('') }}";
       </script>
    </head>

我们现在将使用 ``computed`` 属性来使用此全局值构造绝对 ``URL`` 。

resources/assets/components/App.vue:

.. code-block:: html

    <template>
      ...
      <router-link :to="{ name: 'home' }">
        <img class="icon" :src="logoUrl">
        <h1>vuebnb</h1>
      </router-link>
      ...
    </template>
    <script>
      export default {
        computed: {
          logoUrl() {
            return `${window.cdn_url || ''}images/logo.png`;
          }
        },
        ...
      }
    </script>
    <style>...</style>

我们将在使用灰色徽标的页脚中使用相同的概念。

resources/assets/components/CustomFooter.vue:

.. code-block:: html

    <template>
    ...
      <img class="icon" :src="logoUrl">
    ...
    </template>
    <script>
      export default {
        computed: {
          containerClass() { ... },
          logoUrl() {
            return `${window.cdn_url || ''}images/logo_grey.png`;
          }
        },
      }
    </script>

部署到Heroku
------------
完成后，将任何文件更改提交到 ``Git`` 并再次推送到 ``Heroku`` 以触发新部署。您还需要重建前端资产并将其传输到 ``CDN`` 。

最后，设置 ``CDN`` 环境变量：

.. code-block:: shell

    $ heroku config:set \
    CDN_BYPASS=0 \
    CDN_URL=vuebnb-9c0f.kxcdn.com

终曲
====
您现在已经完成了本书的案例研究项目，一个复杂的全栈 ``Vue.js`` 和 Laravel 应用程序。恭喜！

一定要向你的朋友和同事展示 ``Vuebnb`` ，因为他们毫无疑问会对你的新技能印象深刻。 如果你在推特上发布你的项目链接，我也很感激，所以我也很欣赏你的作品。我的推特手柄是 @anthonygore 。

概括
----

下一步
------

总结
====
在本章中，我们学习了如何将全栈应用程序部署到 ``Heroku`` 云服务器。 为此，我们使用 ``Heroku CLI`` 设置了一个新的 ``Heroku`` 应用程序，然后使用 ``Heroku`` 的 ``Git`` 服务器进行部署。

我们还使用 ``KeyCDN`` 创建了一个 ``CDN`` ，并使用 ``FTP`` 将我们的静态资产部署到 ``CDN`` 。

最后，我们了解了为什么在部署之前以生产模式构建 ``JavaScript`` 资产对于性能和安全性非常重要。

这是本书的最后一章。 感谢您在网络开发之旅中阅读并祝您好运！





















