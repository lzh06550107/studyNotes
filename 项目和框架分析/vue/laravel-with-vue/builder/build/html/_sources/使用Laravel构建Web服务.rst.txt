****************
使用Laravel构建Web服务
****************

在上一章中，我们获得了 ``Homestead`` 开发环境并开始运行，并开始为主要的 ``Vuebnb`` 项目提供服务。 在本章中，我们将创建一个简单的 ``Web`` 服务，使 ``Vuebnb`` 的房间列表数据准备好并显示在前端。

本章涵盖的主题：

- 使用 ``Laravel`` 创建 ``Web`` 服务；
- 编写数据库迁移和种子文件；
- 创建 ``API`` 端点以公开访问数据；
- 从 ``Laravel`` 提供图像；

Vuebnb房间列表
===============
在第2章，您的第一个 ``Vue.js`` 项目 ``Vuebnb`` 原型开发中，我们构建了前端应用程序的列表页面原型。 不久，我们将删除此页面上的硬编码数据，并将其转换为可显示任何房间列表的模板。

我们不会为用户在本书中创建自己的房间列表添加功能。 相反，我们将使用包含30个不同列表的模拟数据包，每个列表都有自己独特的标题，描述和图像。 我们将为这些列表创建数据库，并根据需要配置 ``Laravel`` 将它们提供给前端。

web服务
========
Web服务是运行在服务器上的应用程序，允许客户端（如浏览器）通过 ``HTTP`` 远程向/从服务器读写数据。

Web服务的接口将是一个或多个 ``API`` 端点，有时通过身份验证进行保护，它将返回 ``XML`` 或 ``JSON`` 负载中的数据：

.. figure:: ./images/4-1.png

   图 4.1. Vuebnb web service

Web服务是 ``Laravel`` 的特色，因此为 ``Vuebnb`` 创建Web服务并不难。 我们将为我们的 ``API`` 端点使用路由，并使用 ``Laravel`` 将无缝地与数据库同步的 ``Eloquent`` 模型来表示列表：

.. figure:: ./images/4-2.png

   图 4.2. Web service architecture

``Laravel`` 还具有内置功能来添加 ``REST`` 等 ``API`` 架构，尽管对于简单用例，我们不需要这些。

模拟数据
========
模拟列表数据位于文件 ``database/data.json`` 中。 该文件包含30个对象的 ``JSON`` 编码数组，每个对象代表不同的列表。 建立了列表页面原型之后，您无疑会认识到这些对象上的许多相同属性，包括标题，地址和说明。

database/data.json:

.. code-block:: json

    [
      {
        "id": 1,
        "title": "Central Downtown Apartment with Amenities",
        "address": "...",
        "about": "...",
        "amenity_wifi": true,
        "amenity_pets_allowed": true,
        "amenity_tv": true,
        "amenity_kitchen": true,
        "amenity_breakfast": true,
        "amenity_laptop": true,
        "price_per_night": "$89"
        "price_extra_people": "No charge",
        "price_weekly_discount": "18%",
        "price_monthly_discount": "50%",
      },
      {
        "id": 2,
        ...
      },
      ...
    ]

每个模拟列表还包括房间的几张图片。 图像并不是真正属于web服务的一部分，但它们将存储在我们的应用程序中的公共文件夹中，以便根据需要提供服务。

图像文件不在项目代码中，但在我们从 ``GitHub`` 下载的代码库中。 我们将在本章的后面将它们复制到我们的项目文件夹中。

数据库
======
我们的 ``Web`` 服务将需要一个数据库表来存储模拟列表数据。 要进行设置，我们需要创建一个模式和迁移。 然后，我们将创建一个播种器，它将加载并解析我们的模拟数据文件并将其插入到数据库中，以供在应用程序中使用。

迁移
====
``migration``` 是一个特殊的类，它包含一组针对数据库运行的操作，例如创建或修改数据库表。 迁移可确保每次创建应用程序的新实例时都能以相同的方式设置数据库，例如在生产环境或团队计算机上进行安装。

要创建新的迁移，请使用 ``make:migration`` ``Artisan CLI`` 命令。 该命令的参数应该是一个蛇式的描述，说明迁移将会做什么：

.. code-block:: shell

    $ php artisan make:migration create_listings_table

您现在将在 ``database/migrations`` 目录中看到您的新迁移。 你会注意到文件名有一个前缀时间戳，比如 ``2017_06_20_133317_create_listings_table.php`` 。 时间戳允许 ``Laravel`` 确定迁移的正确顺序，以防一次需要运行多个迁移。

您的新迁移将声明一个扩展迁移的类。 它覆盖了两种方法： ``up`` ，用于向数据库添加新的表，列或索引; 和 ``down`` ，并用于删除它们。 我们很快就会实施这些方法。

2017_06_20_133317_create_listings_table.php:

.. code-block:: php

    <?php
    use Illuminate\Support\Facades\Schema;
    use Illuminate\Database\Schema\Blueprint;
    use Illuminate\Database\Migrations\Migration;

    class CreateListingsTable extends Migration
    {
      public function up()
      {
        //
      }

      public function down()
      {
        //
      }
    }

模式
----
模式是数据库结构的蓝图。 对于像 ``MySQL`` 这样的关系型数据库，模式会将数据组织到表和列中。 在 ``Laravel`` 中，架构是通过使用 ``Schema`` 外观的 ``create`` 方法来声明的。

现在我们将制定一个表格来保存 ``Vuebnb`` 列表。 表中的列将与我们的模拟列表数据的结构相匹配。 请注意，我们为设施设置了默认的 ``false`` 值 ，并允许价格具有 ``NULL`` 值。 所有其他列都需要一个值。

模式将进入我们的迁移的 ``up`` 方法。 我们还会通过调用 ``Schema::drop`` 来填写 ``down`` 。

2017_06_20_133317_create_listings_table.php:

.. code-block:: php

    <?php
    public function up()
    {
      Schema::create('listings', function (Blueprint $table) {
        $table->primary('id');
        $table->unsignedInteger('id');
        $table->string('title');
        $table->string('address');
        $table->longText('about');

        // Amenities
        $table->boolean('amenity_wifi')->default(false);
        $table->boolean('amenity_pets_allowed')->default(false);
        $table->boolean('amenity_tv')->default(false);
        $table->boolean('amenity_kitchen')->default(false);
        $table->boolean('amenity_breakfast')->default(false);
        $table->boolean('amenity_laptop')->default(false);

        // Prices
        $table->string('price_per_night')->nullable();
        $table->string('price_extra_people')->nullable();
        $table->string('price_weekly_discount')->nullable();
        $table->string('price_monthly_discount')->nullable();
      });
    }

    public function down()
    {
      Schema::drop('listings');
    }

.. tip:: ``Facade`` 是一种面向对象的设计模式，用于为服务容器中的基础类创建静态代理。 外观并不意味着提供任何新的功能; 它的唯一目的是提供一个更加令人难忘且易于阅读的执行共同行动的方式。 把它想象成一个面向对象的帮助函数。

执行
----
现在我们已经设置了新的迁移，让我们用这个 ``Artisan`` 命令来运行它：

.. code-block:: shell

    $ php artisan migrate

你应该在 ``Terminal`` 里看到这样的输出：

.. code-block:: shell

    Migrating: 2017_06_20_133317_create_listings_table
    Migrated:  2017_06_20_133317_create_listings_table

为了确认迁移工作，我们使用 ``Tinker`` 来显示新的表结构。 如果您从未使用过 ``Tinker`` 程序，那么它就是一个 ``REPL`` 工具，它允许您在命令行上与 ``Laravel`` 应用程序进行交互。 当你将一个命令输入 ``Tinker`` 进入程序中时，它将被认为是在你的应用程序代码中的一行。

首先，打开Tinker外壳：

.. code-block:: shell

    $ php artisan tinker

现在输入一个 ``PHP`` 语句进行计算。 让我们使用 ``DB Facade`` 的 ``select`` 方法来运行 ``SQL DESCRIBE`` 查询来显示表结构：

.. code-block:: shell

    >>>> DB::select('DESCRIBE listings;');

输出结果非常详细，所以我不会在这里重现它，但是您应该看到一个包含所有表格细节的对象，从而确认迁移工作正常。

填充模拟列表
============
现在我们有一个数据库表供我们的列表使用，让我们将它与模拟数据结合起来。 要做到这一点，我们必须做到以下几点：

1. 加载 ``database/data.json`` 文件；
2. 解析文件；
3. 将数据插入列表表格中；

创建一个播种器
--------------
``Laravel`` 包含一个我们可以扩展的播种机类，称为播种机。 使用这个 ``Artisan`` 命令来实现它：

.. code-block:: shell

    $ php artisan make:seeder ListingsTableSeeder

当我们运行播种机时， ``run`` 方法中的任何代码都会被执行。

database/ListingsTableSeeder.php:

.. code-block:: php

    <?php
    use Illuminate\Database\Seeder;

    class ListingsTableSeeder extends Seeder
    {
      public function run()
      {
        //
      }
    }

加载模拟数据
^^^^^^^^^^^^
``Laravel`` 提供了一个 ``File`` Facade，它允许我们像 ``File::get($path)`` 一样简单地从磁盘打开文件。 为了获得我们的模拟数据文件的完整路径，我们可以使用 ``base_path()`` 辅助函数，它以字符串的形式返回到我们应用程序目录根目录的路径。

然后使用内置的 ``json_decode`` 方法将此 ``JSON`` 文件转换为 ``PHP`` 数组很简单。 一旦数据是一个数组，它可以直接插入到数据库中，因为表的列名与数组键相同。

database/ListingsTableSeeder.php:

.. code-block:: php

    <?php
    public function run()
    {
      $path = base_path() . '/database/data.json';
      $file = File::get($path);
      $data = json_decode($file, true);
    }

插入数据
^^^^^^^^
为了插入数据，我们将再次使用 ``DB`` 门面。 这次我们将调用 ``table`` 方法，它返回 ``Builder`` 的一个实例。 ``Builder`` 类是一个流式查询生成器，它允许我们通过链式约束来查询数据库，例如 ``DB::table(...)->where(...)->join(...)`` 等等。 让我们使用构建器的 ``insert`` 方法，该方法接受列名和值的数组。

database/seeds/ListingsTableSeeder.php:

.. code-block:: php

    <?php
    public function run()
    {
      $path = base_path() . '/database/data.json';
      $file = File::get($path);
      $data = json_decode($file, true);
      DB::table('listings')->insert($data);
    }

执行播种器
----------
为了执行播种器，我们必须从 ``DatabaseSeeder.php`` 文件调用它，它位于同一目录中。

database/seeds/DatabaseSeeder.php:

.. code-block:: php

    <?php
    use Illuminate\Database\Seeder;

    class DatabaseSeeder extends Seeder
    {
      public function run()
      {
        $this->call(ListingsTableSeeder::class);
      }
    }

完成后，我们可以使用 ``Artisan CLI``执行播种器：

.. code-block:: shell

    $ php artisan db:seed

您应该在终端中看到以下输出：

.. code-block:: shell

    Seeding: ListingsTableSeeder

我们会再次使用 ``Tinker`` 来检查我们的工作。 模拟数据中有30个列表，为了确认种子是否成功，让我们检查数据库中的30行：

.. code-block:: shell

    $ php artisan tinker
    >>>> DB::table('listings')->count();
    # Output: 30

最后，让我们检查表格的第一行，以确保其内容符合我们的预期：

.. code-block:: shell

    >>>> DB::table('listings')->get()->first();

这里是输出：

.. code-block:: shell

    => {#732
     +"id": 1,
     +"title": "Central Downtown Apartment with Amenities",
     +"address": "No. 11, Song-Sho Road, Taipei City, Taiwan 105",
     +"about": "...",
     +"amenity_wifi": 1,
     +"amenity_pets_allowed": 1,
     +"amenity_tv": 1,
     +"amenity_kitchen": 1,
     +"amenity_breakfast": 1,
     +"amenity_laptop": 1,
     +"price_per_night": "$89",
     +"price_extra_people": "No charge",
     +"price_weekly_discount": "18%",
     +"price_monthly_discount": "50%"
    }

如果你的看起来已经准备好，则继续前进！

列表模型
========
我们现在已经成功为我们的列表创建了一个数据库表，并用模拟列表数据对其进行了种子处理。 我们现在如何从 ``Laravel`` 应用程序访问这些数据？

我们看到数据库外观如何让我们直接在数据库上执行查询。 但是 ``Laravel`` 提供了一种通过 ``Eloquent ORM`` 访问数据的更强大的方法。

Eloquent ORM
------------
对象关系映射（ ``Object-Relational Mapping，ORM`` ）是一种用于在不兼容面向对象的编程语言系统中的转换数据的技术。 像 ``MySQL`` 这样的关系数据库只能存储表格中组织的标量值，例如整数和字符串。 不过，我们想在我们的应用中使用丰富的对象，所以我们需要一种强大的转换手段。

``Eloquent`` 是 ``Laravel`` 中使用的 ``ORM`` 实现。 它使用活动记录设计模式，其中模型绑定到单个数据库表，并且模型的实例绑定到单个行。

要使用 ``Eloquent ORM`` 在 ``Laravel`` 中创建模型，只需使用 ``Artisan`` 扩展 ``Illuminate\Database\Eloquent\Model`` 类：

.. code-block:: shell

    $ php artisan make:model Listing

它生成一个新文件。

app/Listing.php:

.. code-block:: php

    <?php
    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Listing extends Model
    {
      //
    }

我们如何告诉 ``ORM`` 要映射到哪个表以及包含哪些列？ 默认情况下， ``Model`` 类使用小写（Listing）的类名（listing）作为要使用的表名。 而且，默认情况下，它使用表中的所有字段。

现在，无论何时我们想要加载我们的列表，我们都可以在我们的应用的任何地方使用这样的代码：

.. code-block:: php

    <?php
    // Load all listings
    $listings = \App\Listing::all();

    // Iterate listings, echo the address
    foreach ($listings as $listing) {
      echo $listing->address . '\n' ;
    }

    /*
     * Output:
     *
     * No. 11, Song-Sho Road, Taipei City, Taiwan 105
     * 110, Taiwan, Taipei City, Xinyi District, Section 5, Xinyi Road, 7
     * No. 51, Hanzhong Street, Wanhua District, Taipei City, Taiwan 108
     * ...
     */

类型转换
--------
``MySQL`` 数据库中的数据类型与 ``PHP`` 中的数据类型不完全一致。 例如， ``ORM`` 如何知道数据库值为 ``0`` 是否为数字 ``0`` ，或 ``false`` 布尔值？

一个 ``Eloquent`` 模型可以被赋予一个 ``$casts`` 属性来声明任何特定属性的数据类型。 ``$casts`` 是一个键/值的数组，其中键是要被转换的属性的名称，该值是我们要转换的数据类型。

对于列表表格，我们将把设施属性设置为布尔值。

app/Listing.php:

.. code-block:: php

    <?php
    namespace App;

    use Illuminate\Database\Eloquent\Model;

    class Listing extends Model
    {
      protected $casts = [
        'amenity_wifi' => 'boolean',
        'amenity_pets_allowed' => 'boolean',
        'amenity_tv' => 'boolean',
        'amenity_kitchen' => 'boolean',
        'amenity_breakfast' => 'boolean',
        'amenity_laptop' => 'boolean'
      ];
    }

现在这些属性将具有正确的类型，使我们的模型更加健壮：

.. code-block:: php

    <?php
    echo gettype($listing->amenity_wifi()); // boolean

闭包处理
========
我们的 ``Web`` 服务的最后一部分是允许客户端应用程序请求列表数据的公共接口。 由于 ``Vuebnb`` 列表页面设计为一次显示一个列表，因此我们至少需要一个端点来检索单个列表。

现在让我们创建一个将任何传入 ``GET`` 请求匹配到 ``/api/listing/{listing}`` 的路由，其中 ``{listing}`` 是一个 ``ID`` 。 我们将把它放在 ``routes/api.php`` 文件中，在这个文件中，路由会自动添加 ``/api/`` 前缀，并且默认情况下会优化中间件以用于 ``Web`` 服务。

我们将使用闭包函数来处理路由。 该函数将有一个 ``$ listing`` 参数，我们将把它作为 ``Listing`` 类的一个实例的类型提示。 ``Laravel`` 的服务容器将把它解析为 ``ID`` 匹配 ``{listing}`` 的实例。

然后，我们可以将模型编码为 ``JSON`` 并将其作为响应返回。

routes/api.php:

.. code-block:: php

    <?php
    use App\Listing;

    Route::get('listing/{listing}', function(Listing $listing) {
      return $listing->toJson();
    });

我们可以使用终端的 ``curl`` 命令来测试这个工作：

.. code-block:: shell

    $ curl http://vuebnb.test/api/listing/1

响应将是 ``ID 1`` 的 ``JSON`` 列表。

控制器处理
==========
随着项目的进展，我们将添加更多路由来检索列表数据。 对此功能使用控制器类来保持关注点分离是最佳做法。 让我们用 ``Artisan CLI`` 创建一个：

.. code-block:: shell

    $ php artisan make:controller ListingController

然后，我们将把路由功能移到一个新的方法 ``get_listing_api`` 中。

app/Http/Controllers/ListingController.php:

.. code-block:: php

    <?php
    namespace App\Http\Controllers;

    use Illuminate\Http\Request;
    use App\Listing;

    class ListingController extends Controller
    {
      public function get_listing_api(Listing $listing)
      {
        return $listing->toJson();
      }
    }

对于 ``Route::get`` 方法，我们可以传递一个字符串作为第二个参数，而不是闭包函数。 该字符串应该采用 ``[controller]@[method]`` 的形式，例如 ``ListingController@get_listing_web`` 。 ``Laravel`` 将在运行时正确解决这个问题。

routes/api.php:

.. code-block:: php

    <?php
    Route::get('/listing/{listing}', 'ListingController@get_listing_api');

图片
====
正如本章开头所述，每个模拟列表都附带了几个房间的图像。 这些图像不在项目代码中，必须从名为 ``images`` 的代码库中的并行目录中复制。

将该目录的内容复制到 ``public/images`` 文件夹中：

.. code-block:: shell

    $ cp -a ../images/. ./public/images

一旦你复制这些文件， ``public/image`` 将有30个子文件夹，每个模拟列表一个。 这些文件夹中的每一个都将包含正好四个主要图像和一个缩略图图像：

.. figure:: ./images/4-4.png

   图 4.4 公共文件夹的图像文件

访问图像
--------
``public`` 目录中的文件可以通过将其相对路径附加到网站 ``URL`` 直接请求。 例如，可以在 ``http://vuebnb.test/css/app.css`` 上请求默认的 ``CSS`` 文件 ``public/css/app.css`` 。

使用 ``public`` 文件夹的好处以及我们将图片放在那里的原因是为了避免创建任何访问它们的逻辑。 前端应用程序可以直接在 ``img`` 标签中调用图像。

.. tip:: 您可能认为我们的 ``Web`` 服务器像这样提供图片是无效的，并且您是对的。 在本书后面，我们将在生产模式下提供来自 ``CDN`` 的图像。

让我们尝试在浏览器中打开一个模拟列表图像来测试此论点： http://vuebnb.test/images/1/Image_1.jpg ：

.. figure:: ./images/4-5.png

   图4.5 模拟列表图像显示在浏览器中

图像链接
--------
``Web`` 服务中每个列表的有效载荷应包含这些新图像的链接，以便客户端应用知道在哪里找到它们。 让我们将图像路径添加到我们的列表 ``API`` 负载中，看起来像这样：

.. code-block:: json

    {
      "id": 1,
      "title": "...",
      "description": "...",
      ...
      "image_1": "http://vuebnb.test/app/image/1/Image_1.jpg",
      "image_2": "http://vuebnb.test/app/image/1/Image_2.jpg",
      "image_3": "http://vuebnb.test/app/image/1/Image_3.jpg",
      "image_4": "http://vuebnb.test/app/image/1/Image_4.jpg"
    }

.. tip:: 直到项目后期才会使用缩略图图像。

为了实现这一点，我们将使用模型的 ``toArray`` 方法来创建模型的数组表示形式。 我们将很容易地添加新的字段。 每个模拟列表都有四个图像，编号为1到4，因此我们可以使用 ``for`` 循环和 ``asset`` 助手来为公用文件夹中的文件生成完全限定的 ``URL`` 。

我们通过调用 ``Response`` 助手来创建一个 ``Response`` 类的实例。 我们使用 ``json`` 方法并传入我们的字段数组，返回结果。

app/Http/Controllers/ListingController.php:

.. code-block:: php

    public function get_listing_api(Listing $listing)
    {
      $model = $listing->toArray();
      for($i = 1; $i <=4; $i++) {
        $model['image_' . $i] = asset(
          'images/' . $listing->id . '/Image_' . $i . '.jpg'
        );
      }
      return response()->json($model);
    }

``/api/listing/{listing}`` 端点现在已准备好供客户端应用程序使用。

总结
====
在本章中，我们与 ``Laravel`` 建立了一个 ``Web`` 服务，使 ``Vuebnb`` 列表数据可公开访问。

这涉及使用迁移和模式设置数据库表，然后使用模拟列表数据为数据库播种。 然后我们使用路由为 ``Web`` 服务创建了一个公共接口。 这将模拟数据作为 ``JSON`` 有效内容返回，其中包括指向我们模拟图像的链接。

在下一章中，我们将介绍 ``Webpack`` 和 ``Laravel Mix`` 构建工具，以建立一个完整的全栈开发环境。 我们将 ``Vuebnb``原型迁移到项目中，并重构它以适应新的工作流程。
