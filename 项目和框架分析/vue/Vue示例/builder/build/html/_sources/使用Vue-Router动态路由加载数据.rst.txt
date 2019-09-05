********************
使用Vue-Router动态路由加载数据
********************
在第8章介绍 ``Vue-Router`` 和加载基于 ``URL`` 的组件中，我们探讨了 ``Vue-router`` 及其功能。有了这些知识，我们现在可以继续使用 ``Vue`` 开店。在我们跳入代码并开始创建之前，我们应该先计划我们的商店如何运作，我们需要哪些网址以及我们需要制定哪些组件。一旦我们计划了我们的应用程序，我们就可以开始创建产品页面。

在本章中，我们将：

- 概述我们的组件和路由，并创建占位符文件；
- 加载产品 ``CSV`` 文件，处理它并缓存在 ``Vuex`` 中；
- 创建包含图像和产品变体的单独产品页面；

概述并规划您的应用程序
======================
首先，我们来考虑整体应用程序和用户流程。

我们将创建一家没有付款处理网关的商店。商店主页将显示一个手工挑选的产品列表。用户将能够使用类别浏览产品，并使用我们制作的过滤器缩小选择范围。他们将能够选择产品并查看更多详细信息。该产品将有变化（大小，颜色等），并可能有几个产品图像。用户将能够添加变体到他们的购物车。从那里，他们可以继续浏览产品并添加更多内容，或者继续结账，在那里询问他们的姓名和地址，并付款。订单确认屏幕将会显示。

整个商店的应用程序将在 ``Vue`` 中创建并运行在客户端。这不包括支付，用户账户，库存管理或验证所需的任何服务器端代码。

该应用程序将使用 ``Vue-router`` 来处理 ``URL`` 和 ``Vuex`` 以存储产品，购物车内容和用户详细信息。

组件
====
通过概述用户流程，我们需要规划我们需要为我们的商店制定哪些组件，以及他们将被称为什么。这有助于开发应用程序，因为我们清楚地知道需要创建哪些组件。我们也将决定组件名称。遵循 ``Vue`` 风格指南(https://vuejs.org/v2/style-guide/index.html)，我们所有的组件将由两个名称组成。

路由组件
========
以下组件将与 ``Vue-router`` 结合使用以形成我们应用程序的页面：

- 商店主页 - ``HomePage`` : 商店主页将显示由店主策划的产品列表。这将使用预先选择的产品列表来显示。
- 类别页面 - ``CategoryPage`` : 这将列出特定类别的产品。类别列表页面也会有过滤器。
- 产品页面 - ``ProductPage`` : 产品页面将显示产品详细信息，图像和产品的变体。
- 购物车 - ``OrderBasket`` : 在购物车中，用户将能够查看他们添加的产品，删除不需要的商品以及更改每件商品的数量。它也会显示订单的总费用。
- 结账 - ``OrderCheckout`` : 结账将锁定购物车 - 消除了删除和更新产品的能力，并且会有一个表格供用户输入他们的地址。
- 订单确认 - ``OrderConfirmation`` : 订单准备好后，会显示该组件，确认所购买的产品，收货地址和总价。
- 404页 - ``PageNotFound`` : 输入错误的 ``URL`` 时的错误页面。

HTML组件
========
``HTML`` 组件将在页面组件中使用，以帮助减少我们的代码重复布局：

- 列表中的产品 - ``ListProducts`` : 这将在列表视图中查看时显示产品分页列表 - 例如在 ``HomePage`` 或 ``CategoryPage`` 组件中。
- 类别列表 - ``ListCategories`` : 这将创建一个导航类别列表。
- 购买清单 - ``ListPurchases`` : 此组件将出现在购物车，结帐和订单确认页面中。它将以表格形式列出产品 - 详细说明变化，价格和数量。它还将包含购物车中所有产品的总价。
- 过滤 - ``ProductFiltering`` : 在类别页面使用的组件将为用户提供过滤和更新 ``URL`` 的能力，使用我们在第8章 ``Vue-Router`` 和加载基于 ``URL`` 组件中介绍的 ``GET`` 参数。

路径
====
通过概述我们的组件，我们可以规划到我们商店的路径和 ``URL`` ，以及他们将采取的组件或操作。我们还需要考虑错误的 ``URL`` 以及我们是否应该将用户重定向到合适的位置或显示错误消息：

- ``/`` : 首页；
- ``/category/:slug`` : ``CategoryPage`` ，使用 ``:slug`` 唯一标识符来标识展示哪个产品；
- ``/category`` : 这将重定向到 ``/`` ；
- ``/product/:slug`` : ``ProductPage`` - 再次使用 ``:slug`` 来识别产品；
- ``/product`` : 这将重定向到 ``/`` ；
-  ``/basket`` ： ``OrderBasket`` ；
- ``/checkout`` : ``OrderCheckout`` - 如果没有产品，它会将用户重定向到 ``/basket`` ；
- ``/complete`` : ``OrderConfirmation`` - 如果用户不是来自 ``OrderCheckout`` 组件，那么他们将被重定向到 ``/basket`` ；
- ``*`` : ``PageNotFound`` - 这将捕获任何未指定的路由；

随着我们的路由和组件确定，我们可以开始创建我们的应用程序。

创建初始文件
============
使用上一节中概述的应用程序，我们可以为我们的文件结构和组件创建框架。这个应用程序是一个大型的应用程序，我们将每个组件分割为单个文件。这意味着我们的文件更容易管理，我们的应用程序 ``main`` JavaScript文件不会因为增长失去控制。

尽管开发可以接受，但使用这些文件部署应用程序可能会增加您的加载时间，具体取决于您的服务器设置方式。使用传统的 ``HTTP/1.1`` ，浏览器不得不请求并加载每个文件 - 如果有多个文件，这是一个障碍。但是，使用 ``HTTP/2`` ，您可以同时向用户推送多个文件 - 在这种情况下，多个文件可以稍微提高应用程序的性能。

无论您选择在部署中使用哪种方法，都强烈建议您在将代码部署到生产环境时缩小 ``JavaScript`` 。这确保您的代码在提供给用户时尽可能小：

.. image:: ./images/content.png

为每个组件，视图和库(例如 ``Vue`` ， ``Vuex`` 和 ``Vue-router`` )创建一个文件。 然后，为每种类型的文件创建一个文件夹。最后，添加一个 ``app.js`` --这是库被初始化的地方。

您也可以考虑使用 ``vue-cli`` (https://github.com/vuejs/vue-cli)来构建您的应用程序。本书的范围之外，因为我们只涉及使用包含的 JavaScript 文件构建 ``Vue`` 应用程序，因此 ``vue-cli`` 应用程序允许您以更模块化的方式开发您的应用程序，并且一旦开发，就以类似我们一直在开发应用程序的方式部署它。

创建一个 ``index.html`` 并包含您的 JavaScript 文件，确保 ``Vue`` 先加载并且您的应用程序的 JavaScript 保持最后。在我们商店的视图中为您的应用添加容器：

.. code-block:: html

    <!DOCTYPE html>
    <html>
        <head>
            <title>Vue Shop</title>
        </head>
        <body>
            <div id="app"></div>
            <!-- Libraries -->
            <script type="text/javascript" src="js/libs/vue.js"></script>
            <script type="text/javascript" src="js/libs/vuex.js"></script>
            <script type="text/javascript" src="js/libs/router.js"></script>
            <!-- Components -->
            <script src="js/components/ListCategories.js"></script>
            <script src="js/components/ListProducts.js"></script>
            <script src="js/components/ListPurchases.js"></script>
            <script src="js/components/ProductFiltering.js"></script>
            <!-- Views -->
            <script src="js/views/PageNotFound.js"></script>
            <script src="js/views/CategoryPage.js"></script>
            <script src="js/views/HomePage.js"></script>
            <script src="js/views/OrderBasket.js"></script>
            <script src="js/views/OrderCheckout.js"></script>
            <script src="js/views/OrderConfirmation.js"></script>
            <script src="js/views/ProductPage.js"></script>
            <!-- App -->
            <script type="text/javascript" src="js/app.js"></script>
        </body>
    </html>

确保首先加载 ``PageNotFound`` 组件，因为我们将在其他组件中使用它，并将其指定为路由中的 ``404`` 页面。

在每个文件中，通过声明变量或使用 ``Vue.component`` 来初始化组件的类型(这是为什么？为何要区分？)。 对于视图，还要添加一个 ``name`` 属性 - 以帮助稍后进行调试。例如，位于 ``js/components/`` 文件夹中的所有文件应该像下面那样初始化。确保这些组件是小写字母并且用连字符表示：

.. code-block:: js

    Vue.component('list-products', {});

而位于 ``js/views`` 中的路由和视图的组件应如下所示：

.. code-block:: js

    const OrderConfirmation = {
        name: 'OrderConfirmation'
    };

最后一步是初始化我们的 ``Vuex store`` ， ``Vue-router`` 和 ``Vue`` 应用程序。 打开 ``app.js`` 并初始化库：

.. code-block:: js

    const store = new Vuex.Store({});
    const router = new VueRouter({});

    new Vue({
        el: '#app',
        store,
        router
    });

随着 ``Vue`` 组件和路由的准备就绪，我们的 ``store`` ，路由和应用程序已初始化，让我们看看如何设置服务器（如果需要）并加载数据。

服务器设置
==========
在我们的商店中，我们将在加载页面时加载 ``CSV`` 格式产品信息。这将模拟收集销售点系统的来自数据库或 ``API`` 的库存和产品数据。

与本书前面的 ``Dropbox`` 应用类似，我们将加载外部数据并将其保存到 ``Vuex`` 存储中。 然而，我们将要面对的问题是通过 ``JavaScript`` 加载资源时， 浏览器要求所请求文件的协议是通过 ``HTTP`` ， ``HTTPS`` 或 ``CORS`` 请求。

这意味着我们无法使用我们用于 ``Dropbox API`` 的 ``fetch()`` 技术加载本地文件，因为在浏览器中查看我们的应用时，我们正在通过 ``file://`` 协议加载本地资源。 我们可以用几种不同的方式解决这个问题 - 您选择哪一个取决于您的情况。

我们将加载一个 ``CSV`` 文件，并使用两个插件将其转换为可用的 ``JSON`` 对象。 你有三个选择是：

1. 本地存储文件；
2. 使用远程服务器或；
3. 使用本地服务器；

让我们来看看每个选项，每个选项都有其优缺点。

本地存储文件
------------
第一种选择是将 ``CSV`` 转换为适当的 ``JSON`` ，然后将输出保存到文件中。您需要在文件中将其分配给一个变量并在库之前加载该 ``JSON`` 文件。一个示例可能是创建一个data.json并将其分配给一个变量：

.. code-block:: js

    const products = {...}

然后你可以在你的 ``HTML`` 中加载 ``JSON`` 文件：

.. code-block:: js

    <script type="text/javascript" src="data.json"></script>

然后，您可以在 ``app.js`` 中使用 ``products`` 变量。

优点：

- 减少代码中的负载；
- 无需加载处理 ``CSV`` 所需的额外文件；
- 不需要额外的步骤；

缺点：

- 不现实世界模拟；
- 如果您想更新 ``CSV`` 数据，则需要转换，保存并分配给变量；

使用远程服务器
--------------
另一种选择是将文件上传到远程的现有服务器并在那里开发您的应用程序。

优点：

- 模拟加载 ``CSV`` 的真实世界的开发；
- 可以用任何机器在任何地方开发；

缺点：

- 可能会很慢；
- 需要连接到互联网；
- 需要在服务器上设置部署过程或编辑文件；

设置本地服务器
--------------
最后一个选项是在您的机器上设置本地服务器。 有几个小型，轻量级，零配置模块和应用程序，或者还有更大更强大的应用程序。 如果你的机器上安装了 ``npm`` ，建议使用 node ``HTTP`` 服务器。如果没有，还有其他选项可用。

另一种选择是使用更重量级的应用程序，它可以为您提供 ``SQL`` 数据库以及运行 ``PHP`` 应用程序的能力。 一个例子是 ``MAMP`` 或 ``XAMPP`` 。

优点：

- 模拟真实世界加载 ``CSV`` ；
- 快速即时更新；
- 可以离线开发；

缺点：

- 需要安装软件；
- 可能需要一些配置或命令行知识；

我们要选择的选项是最后一个，使用 ``HTTP`` 服务器。我们加载并处理 ``CSV`` ，以便开始创建我们的商店。

加载CSV
=======
为了模拟从商店数据库或销售点收集数据，我们的应用程序将从 ``CSV`` 中加载产品数据。 ``CSV`` 或逗号分隔值是通常用于以数据库样式共享数据的文件格式。想想你如何在 ``excel`` 或数字中列出产品列表： CSV 格式文件的格式。

下一步将需要下载并包含更多的 JavaScript 文件。如果您在服务器设置部分选择了选项 1 - 让您的文件在本地存储在 ``JSON`` 文件中 - 您可以跳过此步骤。

我们将要使用的数据是 ``Shopify`` 的示例商店数据。这些 ``CSV`` 具有广泛的产品类型和不同的数据选择，这将测试我们的 ``Vue`` 技能。 ``Shopify`` 可以从 GitHub 存储库下载(https://github.com/shopifypartners/shopify-product-csvs-and-images)他们的示例数据。下载任何感兴趣的 ``CSV`` 文件，并将其保存在文件系统的 ``data/`` 文件夹中。对于这个应用程序，我将使用 ``bicycles.csv`` 文件。

如果没有大量的编码和处理逗号分隔和引号封装值， JavaScript 无法在本地加载和处理 ``CSV`` 文件。为了节省时间，深入了解如何加载，解析和处理CSV文件，我们将使用一个库来为我们完成繁重的工作。有两个值得注意的库， ``CSV`` 解析器(https://github.com/okfn/csv.js)和 ``d3`` (https://d3js.org/)。 ``CSV`` 解析器只是执行 ``CSV`` 解析，没有别的，而 ``d3`` 有能力生成图表和数据可视化。

值得考虑哪一个最适合你； ``CSV`` 分析器仅为您的应用程序增加了超过 3KB 的大小，而 ``d3`` 大约为 60KB 。除非您预计稍后再添加可视化，否则建议您转到较小的库 - 尤其是在执行相同的功能时。但是，我们将通过两个库的示例。我们希望在应用程序加载时加载我们的产品数据，因此我们的CSV将在组件需要数据时加载并解析。因此，我们将把数据加载到 ``Vue`` 的 ``created()`` 方法中。

用d3加载CSV
-----------
两种插件都以非常相似的方式加载数据，但返回的数据有所不同 - 但是，一旦我们加载了数据，我们就会处理这些数据。

加载 ``d3`` 库 - 如果你想尝试一下，你可以使用托管版本：

.. code-block:: html

    <script src="https://d3js.org/d3.v4.min.js"></script>

使用 ``d3`` ，我们在 ``d3`` 对象上使用一个 ``csv()`` 函数，它接受一个参数 - ``CSV`` 文件的路径。 将 ``created()`` 函数添加到您的 ``Vue`` 实例并初始化 ``CSV`` 加载器：

.. code-block:: js

    new Vue({
        el: '#app',
        store,
        router,
        created() {
            d3.csv('./data/csv-files/bicycles.csv', (error, data) => {
                console.log(data);
            });
        }
    });

.. note:: 请记住文件的路径相对于包含 JavaScript 文件的 HTML 文件 - 在本例中为 index.html 。

在浏览器中打开文件将不会显示任何输出。但是，如果您打开 Javascript 控制台并展开正在输出的对象，则会看到类似如下的内容：

这可以按 键:值 格式为每个产品提供所有可用属性的细分。 这使我们能够使用每个产品上的一致性 ``key`` 访问每个红色 ``value`` 。例如，如果我们想从上面的产品中选择15mm组合扳手(15mm-combo-wrench)，我们可以使用 ``Handle`` 键。关于这个的更多内容将在后面介绍。

使用CSV解析器加载CSV
--------------------
``CSV`` 解析器的工作方式稍有不同，因为它可以接受许多不同的参数，而库包含几种不同的方法和功能。 数据输出也采用不同的格式，提供一个 ``table/CSV`` 格式的结构，并带有一个 ``headers`` 和 ``fields`` 对象：

.. code-block:: js

    new Vue({
        el: '#app',
        store,
        router,
        created() {
            CSV.fetch({url: './data/csv-files/bicycles.csv'}).then(data => {
                console.log(data);
            });
        }
    });

这次查看输出将显示一个非常不同的结构，并且需要将字段的 ``key`` 与 ``headers`` 对象的索引进行匹配。

统一Shopify CSV数据
===================
在我们保存和使用 ``Shopify`` 数据之前，我们需要统一数据并将其操作为更易于管理的状态。如果您检查任一库输出的数据，您会注意到每个变体或产品的附加图像都有一个条目，并且句柄是每个条目之间的链接因子。例如，有大约12个条目用 ``pure-fix-bar-tape`` 句柄，每个都有不同的颜色。理想情况下，我们希望将每个变体分组在同一项目下，并将图像显示为一个产品的列表。

``Shopify CSV`` 数据的另一个问题是字段标题的标点符号和语法不能成为很好的对象键。理想情况下，对象键将像 ``URL`` 别名、小写字母和不包含空格。例如，``Variant Inventory Qty`` 最好应该是 ``variant-inventory-qty`` 。

为了不用手动处理数据和更新键，我们可以使用 ``Vue`` 插件来处理加载库的输出，并返回格式完全符合我们需要的产品对象。该插件是 ``vue-shopify-products`` ，可从 ``unpkg`` 获取：

.. code-block:: html

    https://unpkg.com/vue-shopify-products

下载该库并将其包含到 ``index.html`` 文件中。下一步是告诉 ``Vue`` 使用这个插件 - 在你的 ``app.js`` 文件的顶部，包含以下行：

.. code-block:: js

    Vue.use(ShopifyProducts);

现在在 ``Vue`` 实例上公开了一个新方法 ``$formatProducts()`` ，它允许我们传入 ``CSV`` 加载库的输出并获得更有用的对象集合：

.. code-block:: js

    Vue.use(ShopifyProducts);
    const store = new Vuex.Store({});
    const router = new VueRouter({});
    new Vue({
        el: '#app',
        store,
        router,
        created() {
            CSV.fetch({url: './data/csv-files/bicycles.csv'}).then(data => {
                let products = this.$formatProducts(data);
                console.log(products);
            });
        }
    });

现在检查输出结果，它显示按句柄分组的一个集合，变体和图像作为对象：

随着我们的产品更有效地分组，我们可以根据需要继续进行存储和调用。

存储产品
========
一旦我们检索并格式化了 ``CSV`` 数据，我们就可以将内容缓存在 ``Vuex store`` 中。这将通过一个简单的 ``mutation`` 来完成，该 ``mutation`` 需要一个有效载荷并将其存储而不需要任何修改。

在 ``store`` 中创建一个 ``state`` 和 ``mutations`` 对象。 在 ``state`` 中添加一个 ``products`` 键作为对象，并在 ``mutations`` 对象中创建一个函数，也称为 ``products`` 。 ``mutation`` 应该接受两个参数 - 状态和有效载荷：

.. code-block:: js

    const store = new Vuex.Store({
        state: {
            products: {}
        },
        mutations: {
            products(state, payload) {
            }
        }
    });

将 ``state.products`` 对象更新为 ``payload`` 的内容：

.. code-block:: js

    const store = new Vuex.Store({
        state: {
            products: {}
        },
        mutations: {
            products(state, payload) {
                state.products = payload;
            }
        }
    });

将主 ``Vue`` 实例中的 ``console.log`` 替换为 ``commit`` 函数，调用新的 ``mutation`` 并传入格式化的产品数据：

.. code-block:: js

    new Vue({
        el: '#app',
        store,
        router,
        created() {
            CSV.fetch({url: './data/csv-files/bicycles.csv'}).then(data => {
                let products = this.$formatProducts(data);
                this.store.commit('products', products);
            });
        }
    });

通过直接将 ``$formatProducts`` 函数传递给 ``store commit()`` 函数，而不是将其作为变量存储，可以稍微降低这一点：

.. code-block:: js

    new Vue({
        el: '#app',
        store,
        router,
        created() {
            CSV.fetch({url: './data/csv-files/bicycles.csv'}).then(data => {
                this.$store.commit('products', this.$formatProducts(data));
            });
        }
    });

显示单个产品
============
通过存储我们的数据，我们现在可以开始制作我们的组件并在前端显示内容。我们将首先制作产品视图 - 显示产品详细信息，变体和图像。我们将在第10章“构建电子商务商店 - 浏览产品”中继续创建类别列表页面。

制作产品视图的第一步是创建路由，以允许通过 ``URL`` 显示组件。回顾本章开头的注释，产品组件将被加载到 ``/product/:slug`` 路径中。

在 ``Vue-router`` 中创建 ``routes`` 数组，并指定路径和组件：

.. code-block:: js

    const router = new VueRouter({
        routes: [
            {
                path: '/product/:slug',
                component: ProductPage
            }
        ]
    });

根据前面 ``products`` 对象的布局解释，我们可以开始了解路由和产品如何链接。我们会将产品的句柄传递到 ``URL`` 中。 这将选择具有该句柄的产品并显示数据。这意味着我们不需要明确地将 ``slug`` 与 ``products`` 联系起来。

找不到网页
==========
在创建第一条路由后，我们还应该创建我们的 ``PageNotFound`` 路由，以捕捉任何不存在的 ``URL`` 。 如果没有匹配的产品，我们也可以重定向到此页面。

我们将以与以前不同的方式创建 ``PageNotFound`` 组件。我们将创建一个 /404 路径作为命名路由，而不是将 ``*`` 上的组件。这允许我们根据需要别名和重定向几条不同的路线。

将一个新对象添加到 ``routes`` 数组中，其中 ``/404`` 作为路径， ``PageNotFound`` 组件作为指定组件。为您的路线添加一个名称，以便我们可以根据需要使用，最后添加一个 ``alias`` 属性，其中包含我们的全球搜索路线。

不要忘记将它放在 ``routes`` 数组的最后 - 以捕获之前未指定的路线。添加新路由时，请始终记住将它们放在 ``PageNotFound`` 路由之前：

.. code-block:: js

    const router = new VueRouter({
        routes: [
            {
                path: '/product/:slug',
                component: ProductPage
            },
            {
                path: '/404',
                alias: '*',
                component: PageNotFound
            }
        ]
    });

向您的 ``PageNotFound`` 组件添加一个模板。现在，给它一些基本的内容 - 一旦我们的应用程序的其余部分设置，稍后我们可以改进它：

.. code-block:: js

    const PageNotFound = {
        name: 'PageNotFound',
        template: `<div>
            <h1>404 Page Not Found</h1>
            <p>Head back to the <router-link to="/">home page</router-link> and start again.</p>
        </div>`
    };

请注意在内容中使用路由器链接。我们需要做的最后一件事就是在我们的应用程序中添加 ``<router-view>`` 元素。前往视图，并在 ``#app`` 标签中包含它：

.. code-block:: html

    <div id="app">
        <router-view></router-view>
    </div>

在浏览器中加载应用程序，不要忘记在需要时启动 ``HTTP`` 服务器。 起初，您应该看到您的 ``PageNotFound`` 组件内容。导航到以下产品网址应导致 JavaScript 错误，而不是 404 页面。 这显示路由正确地选择 ``URL`` ，但错误是因为我们的 ``ProductPage`` 组件不包含模板：

.. code-block:: html

    #/product/15mm-combo-wrench

如果您看到 ``PageNotFound`` 组件，请检查您的路由代码，因为这意味着没有拾取 ``ProductPage`` 路由。

选择正确的产品
==============
通过初始路由设置，现在我们可以继续加载所需产品并显示 ``store`` 中的信息。打开 ``views/Product.js`` 并创建一个 ``template`` 键。首先，创建一个显示产品标题的简单 ``<div>`` 容器：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `<div>{{ product.title }}</div>`
    };

在浏览器中查看它会立即抛出一个 JavaScript 错误，因为 Vue 期望 ``product`` 变量是一个对象 - 但目前尚未定义，因为我们尚未声明它。尽管目前这个问题的解决方案似乎相当简单，但我们需要考虑产品尚未定义的情况。

我们的商店应用程序异步加载数据 ``CSV`` 。这意味着应用程序的其他部分在产品加载时不会停止执行。总体而言，这增加了我们的产品在应用程序的速度，我们可以开始操纵和显示列表，而无需等待其他应用程序启动。

因此，用户可能访问产品详细信息页面的可能性很大，无论是共享链接还是搜索结果，都不会加载产品列表。为了防止应用程序尝试显示产品数据而未完全处于已启用状态，请向模板添加条件属性，以在尝试显示其任何属性之前检查产品变量是否存在。

在加载我们的产品数据时，我们可以确保产品变量设置为 ``false`` ，直到所有内容全部加载完毕。将 ``v-if`` 属性添加到模板中包含的元素：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `<div v-if="product">{{ product.title }}</div>`
    };

我们现在可以开始从 ``store`` 加载正确的产品并将其分配给一个变量。

在 ``computed`` 对象里面创建一个 ``product()`` 函数。 其中，创建 ``product`` 的空白变量，然后返回。 现在默认返回 ``false`` ，这意味着我们的模板不会生成 ``<div>`` ：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `<div v-if="product">{{ product.title }}</div>`,
        computed: {
            product() {
                let product;
                return product;
            }
        }
    };

现在选择产品是一个相当简单的过程，多亏我们在产品组件中提供给我们格式良好的产品 ``store`` 和 ``slug`` 变量。存储中的 ``products`` 对象使用句柄作为关键字和产品细节对象作为值来进行格式化。考虑到这一点，我们可以使用方括号格式选择所需的产品。例如：

.. code-block:: js

    products[handle]

使用 ``router params`` 对象，从存储中加载所需的产品，并将其分配给要返回的 ``product`` 变量：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `<div v-if="product">{{ product.title }}</div>`,
        computed: {
            product() {
                let product;
                product = this.$store.state.products[this.$route.params.slug];
                return product;
            }
        }
    };

我们不直接分配 ``product`` 值的原因是我们可以添加一些条件陈述。为确保我们只在 ``store`` 有数据时才加载产品，我们可以添加 ``if()`` 语句以确保产品的对象具有可用的键; 换句话说，有数据加载？

添加一条 ``if`` 语句来检查 ``store`` 产品键长度。 如果它们存在，则将 ``store`` 中的数据分配给要返回的 ``product`` 变量：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `<div v-if="product">{{ product.title }}</div>`,
        computed: {
            product() {
                let product;
                if(Object.keys(this.$store.state.products).length) {
                    product = this.$store.state.products[this.$route.params.slug];
                }
                return product;
            }
        }
    };

现在在浏览器中查看应用程序，一旦数据加载完毕，您将看到产品的标题。这应该只需要一瞬间加载，并应该优雅地被我们的 ``if`` 语句处理。

在继续显示我们所有的产品数据之前，我们需要处理产品中 ``URL`` 的句柄不存在的情况。由于我们的 ``ProductPage`` 路由收集在 ``URL`` 中的 ``/product`` 之后任何内容，因此 ``PageNotFound`` 通配符路径将无法使用 - 因为它是加载数据并确定产品是否存在的 ``ProductPage`` 组件。

找不到缓存产品
==============
为了在产品不可用时显示 ``PageNotFound`` 页面，我们将使用我们的 ``ProductPage`` 组件加载该组件，并有条件地显示它。

为此，我们需要注册组件，以便我们可以在模板中使用它。我们需要注册它，因为我们的 ``PageNotFound`` 组件现在作为一个对象而不是一个 ``Vue`` 组件（例如，当我们使用 ``Vue.component`` 时）。

将一个组件对象添加到您的 ``ProductPage components`` 属性中，该属性包含 ``PageNotFound`` 值：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `<div v-if="product"><h1>{{ product.title }}</h1></div>`,
        components: {
            PageNotFound
        },
        computed: {
            product() {
                let product;
                if(Object.keys(this.$store.state.products).length) {
                    product = this.$store.state.products[this.$route.params.slug];
                }
                return product;
            }
        }
    };

现在这给了我们一个新的 ``HTML`` 元素，以 ``<page-not-found>`` 的形式使用。在现有的 ``<div>`` 之后将此元素添加到您的模板中。由于我们的模板需要一个根元素，因此将它们都包装在一个额外的容器中：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `<div>
            <div v-if="product"><h1>{{ product.title }}</h1></div>
            <page-not-found></page-not-found>
        </div>`,
        components: {
            PageNotFound
        },
        computed: {
            product() {
                let product;
                if(Object.keys(this.$store.state.products).length) {
                    product = this.$store.state.products[this.$route.params.slug];
                }
                return product;
            }
        }
    };

在浏览器中查看此内容将呈现 ``404`` 页面模板，一旦数据加载完毕，产品标题就会显示。我们现在需要更新组件，以便在没有要显示的数据时仅显示 ``PageNotFound`` 组件。我们可以将现有产品变量与 ``v-if`` 属性一起使用，如果为 ``false`` ，则显示错误消息，如下所示：

.. code-block:: html

    <page-not-found v-if="!product"></page-not-found>

但是，这意味着，如果用户访问产品页面时尚未加载产品数据，则在用产品信息替换之前，他们会看到 ``404`` 信息闪现。这不是一个很好的用户体验，所以 我们只有在产品数据已经加载并且没有匹配的项目，才应该显示错误。

为了解决这个问题，我们将创建一个新的变量来确定组件是否显示。在 ``ProductPage`` 组件中创建一个数据函数，该函数返回一个关键字为 ``productNotFound`` 的对象，并设置为 ``false`` 。 将 ``v-if`` 条件添加到 ``<pagenot-found>`` 元素，并检查新的 ``productNotFound`` 变量：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `<div>
            <div v-if="product"><h1>{{ product.title }}</h1></div>
            <page-not-found v-if="productNotFound"></page-not-found>
        </div>`,
        components: {
            PageNotFound
        },
        data() {
            return {
                productNotFound: false
            }
        },
        computed: {
            product() {
                let product;
                if(Object.keys(this.$store.state.products).length) {
                    product = this.$store.state.products[this.$route.params.slug];
                }
                return product;
            }
        }
    };

最后一步是如果产品不存在，则将该变量设置为 ``true`` 。 由于我们只想在数据加载完成后执行此操作，请将代码添加到 ``$store.state.products`` 检查中。 我们已经将数据分配给 ``product`` 变量，因此我们可以添加一个检查来查看此变量是否存在 - 如果不存在，请更改 ``productNotFound`` 变量：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `<div>
            <div v-if="product"><h1>{{ product.title }}</h1></div>
            <page-not-found v-if="productNotFound"></page-not-found>
        </div>`,
        components: {
            PageNotFound
        },
        data() {
            return {
                productNotFound: false
            }
        },
        computed: {
            product() {
                let product;
                if(Object.keys(this.$store.state.products).length) {
                    product = this.$store.state.products[this.$route.params.slug];
                    if(!product) {
                        this.productNotFound = true;
                    }
                }
                return product;
            }
        }
    };

尝试在网址末尾输入错误的字符串 - 您应该看到我们现在熟悉的 ``404`` 错误页面。

显示产品信息
============
通过我们的产品加载，过滤和错误捕获，我们可以继续显示我们产品所需的信息。每个产品都可以包含一个或多个图像，以及一个或多个变体以及两者之间的任意组合 - 因此我们需要确保满足这些场景中的每一个。

要查看可用的数据，请在返回之前添加一个 ``console.log(product)`` ：

.. code-block:: js

    product() {
        let product;
        if(Object.keys(this.$store.state.products).length) {
            product = this.$store.state.products[this.$route.params.slug];
            if(!product) {
                this.productNotFound = true;
            }
        }
        console.log(product);
        return product;
    }

打开 JavaScript 控制台并检查产品对象。熟悉对你可用的键和值。请注意， ``images`` 键是一个数组和一个 ``variations`` 对象(包含一个字符串和另一个数组)。

在我们处理变化和图像之前 - 让我们输出简单的东西。我们需要记住的是，我们输出的每个字段可能并不存在于每个产品上，因此最好在必要时将其包装在条件标签中。

从产品详细信息中输出 ``body`` , ``type`` 和 ``vendor.title`` 。在 vendor.title 和 type 前加上它们描述的内容，但要确保只在产品详细信息中存在该文本时才显示该文本：

.. code-block:: js

    template: `<div>
        <div v-if="product">
            <h1>{{ product.title }}</h1>
            <div class="meta">
                <span>
                    Manufacturer: <strong>{{ product.vendor.title }}</strong>
                </span>
                <span v-if="product.type">
                    Category: <strong>{{ product.type }}</strong>
                </span>
            </div>
            {{ product.body }}
        </div>
        <page-not-found v-if="productNotFound"></page-not-found>
    </div>`,

请注意，我们可以灵活地在类型和供应商中前置添加更多用户友好的名称。一旦我们设置了类别和过滤器，我们就可以将供应商和类型链接到适当的产品列表。

在浏览器中查看这些内容将显示将所有 HTML 标签输出为文本的正文 - 这意味着我们可以在页面上看到它们。如果您回想起我们在书的开头讨论输出类型，我们需要使用 ``v-html`` 来告诉 ``Vue`` 将块作为原始 ``HTML`` 渲染：

.. code-block:: html

    template: `<div>
        <div v-if="product">
            <h1>{{ product.title }}</h1>
            <div class="meta">
                <span>
                    Manufacturer: <strong>{{ product.vendor.title }}</strong>
                </span>
                <span v-if="product.type">
                    Category: <strong>{{ product.type }}</strong>
                </span>
            </div>
            <div v-html="product.body"></div>
        </div>
        <page-not-found v-if="productNotFound"></page-not-found>
    </div>`,

产品图片
========
下一步是输出我们产品的图像。如果您使用的是自行车 ``CSV`` 文件，要测试的好产品是 ``650c-micro-wheelset`` - 导航到该产品，因为它有四个图像。 不要忘记回到原来的产品来检查它是否适用于一个图像。

图像值将始终是一个数组，无论是一个图像还是 100 个，为了显示它们，我们总是需要做一个 ``v-for`` 。 添加一个新的容器并循环显示图像。为每张图片添加一个宽度，以免它占据您的页面。

图像数组包含每个图像的对象。它有一个 ``alt`` 和 ``source`` 键，可以直接输入到你的 ``HTML`` 中。 但是，有些情况下， ``alt`` 值缺失 - 如果是，请插入产品标题来替代：

.. code-block:: html

    template: `<div>
        <div v-if="product">
            <div class="images" v-if="product.images.length">
                <template v-for="img in product.images">
                    <img
                            :src="img.source"
                            :alt="img.alt || product.title"
                            width="100">
                </template>
            </div>
            <h1>{{ product.title }}</h1>
            <div class="meta">
            <span>
                Manufacturer: <strong>{{ product.vendor.title }}</strong>
            </span>
            <span v-if="product.type">
                Category: <strong>{{ product.type }}</strong>
            </span>
            </div>
            <div v-html="product.body"></div>
        </div>
        <page-not-found v-if="productNotFound"></page-not-found>
    </div>`,

随着我们的图像显示，创建一个画廊将是一个很好的补充。商店经常展示一个大图像，在它下面有一组缩略图。点击每个缩略图然后替换主图像，以便用户可以更好地观看更大的图像。让我们重新创建该功能。 如果只有一个图像，我们也需要确保我们不显示缩略图。

我们这样做，通过设置一个图像变量到图像数组中的第一个图像，这是一个将形成大图像的图片。如果数组中有多个图像，我们将显示缩略图。然后，我们将创建一个 ``click`` 方法，用所选图像更新图像变量。

在您的数据对象中创建一个新变量，并在产品加载时使用图像数组中的第一项更新它。 在尝试分配值之前，确保 ``images`` 键实际上是 ``array`` 项目是一种很好的做法：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `<div>
        <div v-if="product">
            <div class="images" v-if="product.images.length">
                <template v-for="img in product.images">
                    <img
                    :src="img.source"
                    :alt="img.alt || product.title"
                    width="100">
                </template>
            </div>
            <h1>{{ product.title }}</h1>
            <div class="meta">
                <span>
                    Manufacturer: <strong>{{ product.vendor.title }}</strong>
                </span>
                <span v-if="product.type">
                    Category: <strong>{{ product.type }}</strong>
                </span>
            </div>
            <div v-html="product.body"></div>
        </div>
        <page-not-found v-if="productNotFound"></page-not-found>
        </div>`,
        components: {
            PageNotFound
        },
        data() {
            return {
                productNotFound: false,
                image: false
            }
        },
        computed: {
            product() {
                let product;
                if(Object.keys(this.$store.state.products).length) {
                    product = this.$store.state.products[this.$route.params.slug];
                    this.image = (product.images.length) ? product.images[0] : false;
                    if(!product) {
                        this.productNotFound = true;
                    }
                }
                console.log(product);
                return product;
            }
        }
    };

接下来，更新模板中现有的图像循环，以便在数组中有多个图像时才显示。另外，在模板中添加第一张图像作为主图像 - 不要忘记检查它是否首先存在：

.. code-block:: js

    template: `<div>
        <div v-if="product">
            <div class="images" v-if="image">
                <div class="main">
                    <img
                            :src="image.source"
                            :alt="image.alt || product.title">
                </div>
                <div class="thumbnails" v-if="product.images.length > 1">
                    <template v-for="img in product.images">
                        <img
                                :src="img.source"
                                :alt="img.alt || product.title"
                                width="100">
                    </template>
                </div>
            </div>
            <h1>{{ product.title }}</h1>
            <div class="meta">
                <span>
                    Manufacturer: <strong>{{ product.vendor.title }}</strong>
                </span>
                <span v-if="product.type">
                    Category: <strong>{{ product.type }}</strong>
                </span>
            </div>
            <div v-html="product.body"></div>
        </div>
        <page-not-found v-if="productNotFound"></page-not-found>
    </div>`,

最后一步是为每个缩略图添加一个点击处理程序，以便在与之交互时更新图像变量。 由于图像本身并不具有 ``cursor:pointer`` CSS 属性，因此可能值得考虑添加此项。

点击处理程序将接受缩略图循环中的每个图像作为参数。点击后，它会简单地通过传递的对象更新 ``image`` 变量：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `<div>
        <div v-if="product">
            <div class="images" v-if="image">
                <div class="main">
                    <img
                            :src="image.source"
                            :alt="image.alt || product.title">
                </div>
                <div class="thumbnails" v-if="product.images.length > 1">
                    <template v-for="img in product.images">
                        <img
                                :src="img.source"
                                :alt="img.alt || product.title"
                                width="100"
                                @click="updateImage(img)">
                    </template>
                </div>
            </div>
            <h1>{{ product.title }}</h1>
            <div class="meta">
                <span>
                    Manufacturer: <strong>{{ product.vendor.title }}</strong>
                </span>
                <span v-if="product.type">
                    Category: <strong>{{ product.type }}</strong>
                </span>
            </div>
            <div v-html="product.body"></div>
        </div>
        <page-not-found v-if="productNotFound"></page-not-found>
    </div>`,
        components: {
            PageNotFound
        },
        data() {
            return {
                productNotFound: false,
                image: false
            }
        },
        computed: {
            product() {
                let product;
                if(Object.keys(this.$store.state.products).length) {
                    product = this.$store.state.products[this.$route.params.slug];
                    this.image = (product.images.length) ? product.images[0] : false;
                    if(!product) {
                        this.productNotFound = true;
                    }
                }
                console.log(product);
                return product;
            }
        },
        methods: {
            updateImage(img) {
                this.image = img;
            }
        }
    };

在您的浏览器中加载产品并尝试点击任何缩略图 - 您应该能够更新主图像。不要忘记在产品上使用代码验证一个图像甚至零图像的情况，以确保用户不会遇到任何错误。

产品变体
========
有了这个特定的数据集，我们的每个产品都至少包含一个变体，但也可以包含多个变体。这通常与图像的数量并行，但并不总是相关的。变化可以是诸如颜色或大小等事物。

在我们的 ``Product`` 对象上，我们有两个键可以帮助我们显示变化。这些是 ``variationTypes`` ，其中列出了变体的名称（如大小和颜色）以及 ``variationProducts`` ，其中包含所有变体。 ``variationProducts`` 对象中的每个产品都有另一个变体对象，它列出了所有可变属性。例如，如果夹克有两种颜色，每种颜色有三种尺寸，则会有六种 ``variationProducts`` (不同的产品)，每种都有两种 ``variant`` 属性。

每个产品至少会包含一个变体，但如果只有一个变体，我们可能需要考虑产品页面的 ``UX`` 。我们将在一个表格和下拉列表中显示我们的产品变体，因此您可以体验创建这两个元素。

显示变体的表
------------
在您的产品模板中创建一个将显示变体的新容器。在这个容器中，我们可以创建一个表来显示产品的不同变化。这将通过 v-for 声明来实现。现在，既然您对功能更加熟悉了，我们可以引入一个新属性。

使用带有key的循环
-----------------
在 ``Vue`` 中使用循环时，建议您使用额外的属性来标识每个项目 ``:key`` 。 这有助于 ``Vue`` 在重新排序，排序或过滤时识别数组的元素。 ``:key`` 使用的一个例子是：

.. code-block:: html

    <div v-for="item in items" :key="item.id">
        {{ item.title }}
    </div>

``key`` 属性应该是项目本身的唯一属性，而不是数组中项目的索引，以帮助 ``Vue`` 识别特定对象。 有关在循环中使用 ``key`` 的更多信息，请参见官方 `Vue <http://vue>`_ 文档。

我们将在显示变体时利用 key 属性，赋值为项目 ``barcode`` (条形码)属性。

表格中显示变体
--------------
向变体容器中添加一个表格元素，并循环遍历 ``items`` 数组。现在，显示 ``tile`` ， ``quantity`` 和 ``price`` 。添加一个附加单元格，其中包含一个按钮，其值为“添加到购物车”。 我们将在第11章“构建电子商务商店 - 添加结帐”中进行配置。不要忘记在价格前添加一个 ``$currency`` 符号，因为它目前只是一个“原始”数字。

注意 - 在模板文字中使用 ``$`` 符号时， JavaScript 会尝试将其与花括号一起解释为 JavaScript 变量。 为了抵消这种情况，用一个反斜杠加上货币 - 这告诉 JavaScript 下一个字符是字面的，不应该以任何其他方式解释：

.. code-block:: js

    template: `<div>
        <div v-if="product">
            <div class="images" v-if="image">
                <div class="main">
                    <img
                            :src="image.source"
                            :alt="image.alt || product.title">
                </div>
                <div class="thumbnails" v-if="product.images.length > 1">
                    <template v-for="img in product.images">
                        <img
                                :src="img.source"
                                :alt="img.alt || product.title"
                                width="100"
                                @click="updateImage(img)">
                    </template>
                </div>
            </div>
            <h1>{{ product.title }}</h1>
            <div class="meta">
                <span>
                    Manufacturer: <strong>{{ product.vendor.title }}</strong>
                </span>
                <span v-if="product.type">
                    Category: <strong>{{ product.type }}</strong>
                </span>
            </div>
            <div class="variations">
                <table>
                    <tr v-for="variation in product.variationProducts" :key="variation.barcode">
                        <td>{{ variation.quantity }}</td>
                        <td>\${{ variation.price }}</td>
                        <td><button>Add to basket</button></td>
                    </tr>
                </table>
            </div>
            <div v-html="product.body"></div>
        </div>
        <page-not-found v-if="productNotFound"></page-not-found>
    </div>`,

尽管我们显示了价格和数量，但我们并未输出变体的实际变体属性（如颜色）。要做到这一点，我们需要用一种方法对我们的变体进行一些处理。

变体对象包含每种变体类型的子对象，每种类型都有一个 ``name`` 和一个 ``value`` 。它们也会在对象内存储一个 ``slug`` 转换的键。请参阅以下屏幕截图了解更多详情：

在表格的开头添加一个新的单元格，将该变体传递给名为 ``variantTitle()`` 的方法：

.. code-block:: html

    <div class="variations">
        <table>
            <tr v-for="variation in product.variationProducts" :key="variation.barcode">
                <td>{{ variantTitle(variation) }}</td>
                <td>{{ variation.quantity }}</td>
                <td>\${{ variation.price }}</td>
                <td><button>Add to basket</button></td>
            </tr>
        </table>
    </div>

在你的 ``methods`` 对象中创建新的方法：

.. code-block:: js

    methods: {
        updateImage(img) {
            this.image = img;
        },
        variantTitle(variation) {
        }
    }

我们现在需要构建一个带有变体标题的字符串，并显示所有可用的选项。为此，我们将构建每个类型的数组，然后将它们连接成一个字符串。

将 ``variants`` 存储为变量并创建一个空数组。 现在我们可以遍历  ``variants`` 对象中可用的键并创建一个字符串输出。如果您决定将 ``HTML`` 添加到字符串中，如以下示例所示，我们需要更新我们的模板以输出 ``HTML`` 而不是原始字符串：

.. code-block:: js

    variantTitle(variation) {
        let variants = variation.variant,
            output = [];
        for(let a in variants) {
            output.push(`<b>${variants[a].name}:</b> ${variants[a].value}`);
        }
    }

我们的输出数组将为每个变体都有一个项目，格式如下：

.. code-block:: js

    ["<b>Color:</b> Alloy", "<b>Size:</b> 49 cm"]

现在我们可以将每一个连接在一起，将数组的输出转换为字符串。 您选择加入的字符，字符串或 ``HTML`` 取决于您。现在，在任一侧使用一个 ` / ` 。或者，您可以使用 ``</td><td>`` 标签创建新的表格单元格。传入到 ``join()`` 函数并使用 ``v-html`` 更新模板：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `<div>
        <div v-if="product">
            <div class="images" v-if="image">
                <div class="main">
                    <img
                            :src="image.source"
                            :alt="image.alt || product.title">
                </div>
                <div class="thumbnails" v-if="product.images.length > 1">
                    <template v-for="img in product.images">
                        <img
                                :src="img.source":alt="img.alt || product.title"
                                width="100"
                                @click="updateImage(img)">
                    </template>
                </div>
            </div>
            <h1>{{ product.title }}</h1>
            <div class="meta">
                <span>
                    Manufacturer: <strong>{{ product.vendor.title }}</strong>
                </span>
                <span v-if="product.type">
                    Category: <strong>{{ product.type }}</strong>
                </span>
            </div>
            <div class="variations">
                <table>
                    <tr v-for="variation in product.variationProducts" :key="variation.barcode">
                        <td v-html="variantTitle(variation)"></td>
                        <td>{{ variation.quantity }}</td>
                        <td>${{ variation.price }}</td>
                        <td><button>Add to basket</button></td>
                    </tr>
                </table>
            </div>
            <div v-html="product.body"></div>
        </div>
        <page-not-found v-if="productNotFound"></page-not-found>
    </div>`,
        components: {
            PageNotFound
        },
        data() {
            return {
                productNotFound: false,
                image: false
            }
        },
        computed: {
            ...
        },
        methods: {
            updateImage(img) {
                this.image = img;
            },
            variantTitle(variation) {
                let variants = variation.variant,
                    output = [];
                for(let a in variants) {
                    output.push(`<b>${variants[a].name}:</b> ${variants[a].value}`);
                }
                return output.join(' / ');
            }
        }
    };

将单击事件附加到添加到购物车按钮，并在组件上创建新方法。该方法将要求传入 ``variation`` 对象，因此可以将对应的变体添加到购物车中。现在，添加一个 JavaScript ``alert()`` 以保证您拥有正确的警报：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `<div>
        <div v-if="product">
            <div class="images" v-if="image">
                <div class="main">
                    <img
                            :src="image.source"
                            :alt="image.alt || product.title">
                </div>
                <div class="thumbnails" v-if="product.images.length > 1">
                    <template v-for="img in product.images">
                        <img
                                :src="img.source"
                                :alt="img.alt || product.title"
                                width="100"
                                @click="updateImage(img)">
                    </template>
                </div>
            </div>
            <h1>{{ product.title }}</h1>
            <div class="meta">
                <span>
                    Manufacturer: <strong>{{ product.vendor.title }}</strong>
                </span>
                <span v-if="product.type">
                    Category: <strong>{{ product.type }}</strong>
                </span>
            </div>
            <div class="variations">
                <table>
                    <tr v-for="variation in product.variationProducts" :key="variation.barcode">
                        <td v-html="variantTitle(variation)"></td>
                        <td>{{ variation.quantity }}</td>
                        <td>${{ variation.price }}</td>
                        <td><button @click="addToBasket(variation)">Add to basket</button></td>
                    </tr>
                </table>
            </div>
            <div v-html="product.body"></div>
        </div>
        <page-not-found v-if="productNotFound"></page-not-found>
    </div>`,
        components: {
            PageNotFound
        },
        data() {
            return {
                productNotFound: false,
                image: false
            }
        },
        computed: {
            product() {
                let product;
                if(Object.keys(this.$store.state.products).length) {
                    product = this.$store.state.products[this.$route.params.slug];
                    this.image = (product.images.length) ? product.images[0] : false;
                    if(!product) {
                        this.productNotFound = true;
                    }
                }
                console.log(product);
                return product;
            }
        },
        methods: {
            updateImage(img) {
                this.image = img;
            },
            variantTitle(variation) {
                let variants = variation.variant,
                    output = [];
                for(let a in variants) {
                    output.push(`<b>${variants[a].name}:</b> ${variants[a].value}`);
                }
                return output.join(' / ');
            },
            addToBasket(variation) {
                alert(`Added to basket: ${this.product.title} - ${this.variantTitle(variation)}`);
            }
        }
    };

注意 ``alert`` 框中使用的模板文字 - 这使我们能够使用 Javascript 变量而不必使用字符串连接技术。 点击添加到购物车按钮现在将生成产品名称和所点击变体信息的弹出列表。

选择框中显示变体
----------------
产品页面上更常见的界面模式是有一个下拉列表或选择框，显示可供您选择的变体。

当使用选择框时，我们将会有一个默认选择的变体或用户已经与之进行交互和选择的变体。因此，当用户更改选择框时，在产品页面上产品的图片将会改变并且显示有关变体的其他信息，包括价格和数量。

我们不会依赖传递给 ``addToBasket`` 方法的变体，因为它将作为产品组件上的对象存在。

将 <table> 元素更新为 <select> ，将 <tr> 更新为 <option> 。 将按钮移动到该元素的外部并从 ``click`` 事件中删除该参数。从 ``variantTitle()`` 方法中删除任何 ``HTML`` 。因为它现在位于选择框内，所以不是必需的：

.. code-block:: html

    <div class="variations">
        <select>
            <option
                    v-for="variation in product.variationProducts"
                    :key="variation.barcode"
                    v-html="variantTitle(variation)"
            ></option>
        </select>
        <button @click="addToBasket()">Add to basket</button>
    </div>

下一步是创建一个可用于组件的新变量。与图像类似，这将通过 ``variationProducts`` 数组的第一项完成，并在选择框更改时进行更新。

在数据对象中创建一个名为 ``variation`` 的新项目。将数据加载到 ``product`` 计算变量中时填充此变量：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `...`,
        components: {
            PageNotFound
        },
        data() {
            return {
                productNotFound: false,
                image: false,
                variation: false
            }
        },
        computed: {
            product() {
                let product;
                if(Object.keys(this.$store.state.products).length) {
                    product = this.$store.state.products[this.$route.params.slug];
                    this.image = (product.images.length) ? product.images[0] : false;
                    this.variation = product.variationProducts[0];
                    if(!product) {
                        this.productNotFound = true;
                    }
                }
                console.log(product);
                return product;
            }
        },
        methods: {
            ...
        }
    };

更新 ``addToBasket`` 方法以使用 ``ProductPage`` 组件的 ``variation`` 变量，而不依赖于参数：

.. code-block:: js

    addToBasket() {
        alert(`Added to basket: ${this.product.title} - ${this.variantTitle(this.variation)}`);
    }

尝试点击添加到购物车按钮 - 它应该添加第一个变体，无论在下拉列表中选择了什么。 要更新变量，我们可以将 ``variations``  变量绑定到选择框 - 以同样的方式，我们在本书开始时进行了文本框过滤。

向 ``select`` 元素添加一个 ``v-model`` 属性。我们还需要告诉 ``Vue`` 当选择时绑定什么到这个变量。 默认情况下，它将执行 ``<option>`` 的内容，该选项是我们自定义的变体标题。但是，我们想要绑定整个 ``variation`` 对象。将一个 ``:value`` 属性添加到 ``<option>`` 元素中：

.. code-block:: html

    <div class="variations">
        <select v-model="variation">
            <option
                    v-for="variation in product.variationProducts"
                    :key="variation.barcode"
                    :value="variation"
                    v-html="variantTitle(variation)"
            ></option>
        </select>
        <button @click="addToBasket()">Add to basket</button>
    </div>

改变选择框并点击添加到购物篮按钮现在会产生正确的变化。这种方法比我们显示表格中的变体提供了更多的灵活性。

它允许我们在产品的其他地方显示变体数据。尝试在产品标题和在 ``meta`` 容器中数量旁边添加价格：

.. code-block:: html

    template: `<div>
        <div v-if="product">
            <div class="images" v-if="image">
                <div class="main">
                    <img
                            :src="image.source"
                            :alt="image.alt || product.title">
                </div>
                <div class="thumbnails" v-if="product.images.length > 1">
                    <template v-for="img in product.images">
                        <img
                                :src="img.source"
                                :alt="img.alt || product.title"
                                width="100"
                                @click="updateImage(img)">
                    </template>
                </div>
            </div>
            <h1>{{ product.title }} - \${{ variation.price }}</h1>
            <div class="meta">
                <span>
                    Manufacturer: <strong>{{ product.vendor.title }}</strong>
                </span>
                <span v-if="product.type">
                    Category: <strong>{{ product.type }}</strong>
                </span>
                <span>
                    Quantity: <strong>{{ variation.quantity }}</strong>
                </span>
            </div>
            <div class="variations">
                <select v-model="variation">
                    <option
                            v-for="variation in product.variationProducts"
                            :key="variation.barcode"
                            :value="variation"
                            v-html="variantTitle(variation)"
                    ></option>
                </select>
                <button @click="addToBasket()">Add to basket</button>
            </div>
            <div v-html="product.body"></div>
        </div>
        <page-not-found v-if="productNotFound"></page-not-found>
    </div>`,

存在两个新属性将在 ``variation`` 变化时更新。如果图像有一个，我们也可以将图像更新为选定的变体。 为此，请将 ``watch`` 对象添加到组件中。 更新时，我们可以检查 ``variation`` 是否有图像，如果是，则使用以下属性更新 ``image`` 变量：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `...`,
        components: {
            ...
        },
        data() {
        ...
        },
        computed: {
            ...
        },
        watch: {
            variation(v) {
                if(v.hasOwnProperty('image')) {
                    this.updateImage(v.image);
                }
            }
        },
        methods: {
            ...
        }
    };

使用 ``watch`` 时，该功能将新项目作为第一个参数传递。而不是引用组件上的那个，我们可以使用它来收集图像信息。

我们可以做的另一个增强功能是如果变体没货，则禁用“添加到购物车”按钮，并在下拉列表中添加备注。 该信息是从变体 ``quantity`` 键中收集的。

检查数量，如果少于一个，则在选择框中显示缺货信息，并使用 ``disabled`` 的 ``HTML`` 属性禁用“添加到购物车”按钮。我们也可以更新按钮的值：

.. code-block:: html

    template: `<div>
        <div v-if="product">
            <div class="images" v-if="image">
                <div class="main">
                    <img
                            :src="image.source"
                            :alt="image.alt || product.title"></div>
                <div class="thumbnails" v-if="product.images.length > 1">
                    <template v-for="img in product.images">
                        <img
                                :src="img.source"
                                :alt="img.alt || product.title"
                                width="100"
                                @click="updateImage(img)">
                    </template>
                </div>
            </div>
            <h1>{{ product.title }} - \${{ variation.price }}</h1>
            <div class="meta">
                <span>
                    Manufacturer: <strong>{{ product.vendor.title }}</strong>
                </span>
                <span v-if="product.type">
                    Category: <strong>{{ product.type }}</strong>
                </span>
                <span>
                    Quantity: <strong>{{ variation.quantity }}</strong>
                </span>
            </div>
            <div class="variations">
                <select v-model="variation">
                    <option
                            v-for="variation in product.variationProducts"
                            :key="variation.barcode"
                            :value="variation"
                            v-html="variantTitle(variation) + ((!variation.quantity) ? ' - out of stock' : '')"
                    ></option>
                </select>
                <button @click="addToBasket()" :disabled="!variation.quantity">
                    {{ (variation.quantity) ? 'Add to basket' : 'Out of stock' }}
                </button>
            </div>
            <div v-html="product.body"></div>
        </div>
        <page-not-found v-if="productNotFound"></page-not-found>
    </div>`,

如果使用 ``bicycles.csv`` 数据集，Keirin Pro Track Frameset 产品（ ``/＃/product/keirin-pro-track-frame`` ）包含多种变体，有些变体没有库存。这使您可以测试缺货功能和图像更改。

我们可以对产品页面做的另一件事是只有当有多个变体时才显示下拉菜单。仅有一种产品的一个例子是 15 毫米组合扳手（ ``＃/product/15mm-combo-wrench`` ）。 在这种情况下，不值得显示 ``<select>`` 框。 由于我们在 ``Product`` 组件加载时设置 ``variation`` 变量，因此我们不依赖选择来初始设置变量。 因此，当只有一个替代产品时，我们可以用 ``v-if=""`` 完全删除选择框。

就像我们对图像所做的那样，检查数组的长度是否超过一个，这次是 ``variationProducts`` 数组：

.. code-block:: html

    <div class="variations">
        <select v-model="variation" v-if="product.variationProducts.length > 1">
            <option
                    v-for="variation in product.variationProducts"
                    :key="variation.barcode"
                    :value="variation"
                    v-html="variantTitle(variation) + ((!variation.quantity) ? ' - out of stock' : '')"
            ></option>
        </select>
        <button @click="addToBasket()" :disabled="!variation.quantity">
            {{ (variation.quantity) ? 'Add to basket' : 'Out of stock' }}
        </button>
    </div>

通过在不需要时删除元素，我们现在可以减少混乱的界面。

切换网址时更新产品详情
======================
在浏览不同的产品 ``URL`` 以检查变体时，您可能已经注意到，点击后退和前进不会更新页面上的产品数据。

这是因为 ``Vue-router`` 实现了在页面之间使用相同的组件，所以不是销毁和创建新的实例，而是重用组件。 不利的一面是数据没有得到更新；我们需要触发一个功能来包含新产品数据。好处是代码更高效。

为了告诉 ``Vue`` 检索新数据，我们需要创建一个 ``watch`` 函数，我们将观察 ``$route`` 变量。当这个更新时，我们可以加载新的数据。

在数据实例中创建一个新的 ``slug`` 变量，并将默认值设置为路由参数。更新 ``product`` 计算函数以使用此变量而不是路由：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `...`,
        components: {
            PageNotFound
        },
        data() {
            return {
                slug: this.$route.params.slug,
                productNotFound: false,
                image: false,
                variation: false
            }
        },
        computed: {
            product() {
                let product;
                if(Object.keys(this.$store.state.products).length) {
                    product = this.$store.state.products[this.slug];
                    this.image = (product.images.length) ? product.images[0] : false;
                    this.variation = product.variationProducts[0];
                    if(!product) {
                        this.productNotFound = true;
                    }
                }
                console.log(product);
                return product;
            }
        },
        watch: {
            ...
        },
        methods: {
            ...
        }
    };

现在我们可以创建一个 ``watch`` 函数，并关注 ``$route`` 变量。当这个变化时，我们可以更新 ``slug`` 变量，这反过来会更新正在显示的数据。

监控路由时，该函数有两个传递给它的参数： ``to`` 和 ``from`` 。 ``to`` 变量包含有关我们将要使用的路由的所有信息，包括参数和使用的组件。 ``from`` 变量包含有关当前路由的所有一切。

通过在路由更改时将 ``slug`` 变量更新为新参数，我们迫使组件重新绘制 ``store`` 中的新数据：

.. code-block:: js

    const ProductPage = {
        name: 'ProductPage',
        template: `<div>
        <div v-if="product">
            <div class="images" v-if="image">
                <div class="main">
                    <img
                            :src="image.source"
                            :alt="image.alt || product.title">
                </div>
                <div class="thumbnails" v-if="product.images.length > 1">
                    <template v-for="img in product.images">
                        <img
                                :src="img.source"
                                :alt="img.alt || product.title"
                                width="100"
                                @click="updateImage(img)">
                    </template>
                </div>
            </div>
            <h1>{{ product.title }} - ${{ variation.price }}</h1>
            <div class="meta">
                <span>
                    Manufacturer: <strong>{{ product.vendor.title }}</strong>
                </span>
                <span v-if="product.type">
                    Category: <strong>{{ product.type }}</strong>
                </span>
                <span>
                    Quantity: <strong>{{ variation.quantity }}</strong>
                </span>
            </div>
            <div class="variations">
                <select v-model="variation" v-if="product.variationProducts.length > 1">
                    <option
                            v-for="variation in product.variationProducts"
                            :key="variation.barcode"
                            :value="variation"
                            v-html="variantTitle(variation) + ((!variation.quantity) ? ' - out of stock' : '')"
                    ></option>
                </select>
                <button @click="addToBasket()" :disabled="!variation.quantity">
                    {{ (variation.quantity) ? 'Add to basket' : 'Out of stock' }}
                </button>
            </div>
            <div v-html="product.body"></div>
        </div>
        <page-not-found v-if="productNotFound"></page-not-found>
    </div>`,
        components: {
            PageNotFound
        },
        data() {
            return {
                slug: this.$route.params.slug,
                productNotFound: false,
                image: false,
                variation: false
            }
        },
        computed: {
            product() {
                let product;
                if(Object.keys(this.$store.state.products).length) {
                    product = this.$store.state.products[this.slug];
                    this.image = (product.images.length) ? product.images[0] : false;
                    this.variation = product.variationProducts[0];
                    if(!product) {
                        this.productNotFound = true;
                    }
                }
                return product;
            }
        },
        watch: {
            variation(v) {
                if(v.hasOwnProperty('image')) {
                    this.updateImage(v.image);
                }
            },
            '$route'(to) { // 这里为何需要引号??
                this.slug = to.params.slug;
            }
        },
        methods: {
            updateImage(img) {
                this.image = img;
            },
            variantTitle(variation) {
                let variants = variation.variant,
                    output = [];
                for(let a in variants) {
                    output.push(`${variants[a].name}: ${variants[a].value}`);
                }
                return output.join(' / ');
            },
            addToBasket() {
                alert(`Added to basket: ${this.product.title} - ${this.variantTitle(this.variation)}`);
            }
        }
    };

通过完成产品页面，我们可以继续为 ``type`` 和 ``vendor`` 变量创建类别列表。 删除代码中的任何 ``console.log()`` 调用，以保持它的清洁。

总结
====
本章覆盖了很多。 我们将产品的 ``CSV`` 文件加载并存储到我们的 ``Vuex`` ``store`` 中。从那里，我们创建了一个产品详细信息页面，该页面使用 ``URL`` 中的动态变量来加载特定产品。我们创建了一个产品详细视图，允许用户查看图像库并从下拉列表中选择一个变体。如果变体具有关联的图像，则更新主图像。

在第10章“构建电子商务商店 - 浏览产品”中，我们将创建一个类别页面，创建过滤和订购功能 - 帮助用户找到他们想要的产品。