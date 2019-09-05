***********************
介绍Vue-Router并加载基于URL的组件
***********************
在接下来的几章和本书的最后一节中，我们将创建一个商店界面。 这家商店将结合我们迄今为止学到的一切，同时推出更多技术，插件和功能。我们将着眼于从 ``CSV`` 文件中检索产品列表，显示它们及其变体，以及通过制造商或标签过滤产品。我们还将着眼于创建产品详情视图，并允许用户在其网上购物车中添加和删除产品和产品变体（如尺寸或颜色）。

所有这些都将通过使用 ``Vue`` 、 ``Vuex`` 和一个新的 ``Vue`` 插件 ``Vue-router`` 来实现。 ``Vue-router`` 用于构建单页面应用程序（ ``SPA`` ），并允许您将组件映射到 ``URL`` 或 ``VueRouter`` 术语，路线和路径。 这是一个非常强大的插件，可处理 ``URL`` 处理所需的大量复杂细节。

本章将包括：

- 初始化 ``Vue`` 路由器及其选项；
- 创建与Vue路由器的链接；
- 制作动态路由以根据 ``URL`` 更新视图；
- 在 ``URL`` 中使用 ``props`` ；
- 嵌套和命名路由；
- 如何以编程方式导航 ``Vue-router`` ；

安装和初始化Vue路由器
=====================
与我们在应用程序中添加 ``Vue`` 和 ``Vuex`` 类似，您可以直接从 ``unpkg`` 包含库，或者前往以下 ``URL`` 并下载本地副本： https://unpkg.com/Vue-router 。 将 JavaScript 添加到新的 HTML 文档，以及 Vue 和您的应用程序的 JavaScript 。 创建一个应用程序容器元素，以及你的视图。 在下面的例子中，我将 ``Vue-router`` JavaScript文件保存为 ``router.js`` ：

.. code-block:: html

    <!DOCTYPE html>
    <html>
    <head>
        <title></title>
    </head>
    <body>
        <div id="app"></div>
        <script type="text/javascript" src="js/vue.js"></script>
        <script type="text/javascript" src="js/router.js"></script>
        <script type="text/javascript" src="js/app.js"></script>
    </body>
    </html>

在您的应用程序 ``JavaScript`` 中初始化一个新的 ``Vue`` 实例：

.. code-block:: js

    new Vue({
        el: '#app'
    });

我们现在准备添加 ``VueRouter`` 并利用其功能。 然而，在我们这样做之前，我们需要创建一些非常简单的组件，我们可以根据 ``URL`` 加载和显示这些组件。由于我们将要使用路由器加载组件，因此我们不需要使用 ``Vue.component`` 注册它们，而是使用与 ``Vue`` 组件相同的属性创建 ``JavaScript`` 对象。

对于第一个练习，我们将创建两个页面 -  ``Home`` 和 ``About`` 页面。 在大多数网站上发现，这些应该可以帮助您了解何时何地正在加载的上下文。 在您的 ``HTML`` 页面中创建两个模板供我们使用：

.. code-block:: html

    <script type="text/x-template" id="homepage">
        <div>
            <h1>Hello &amp; Welcome</h1>
            <p>Welcome to my website. Feel free to browse around.</p>
        </div>
    </script>
    <script type="text/x-template" id="about">
        <div>
            <h1>About Me</h1>
            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus sed metus magna. Vivamus eget est nisi. Phasellus vitae nisi sagittis, ornare dui quis, pharetra leo. Nullam eget tel
            <p>Curabitur et arcu fermentum, viverra lorem ut, pulvinar arcu. Fusce ex massa, vehicula id eros vel, feugiat commodo leo. Etiam in sem rutrum, porttitor velit in, sollicitudin tortor.
        </div>
    </script>

不要忘了将所有内容封装在一个“根”元素中（这里用包装 ``<div>`` 标签表示）。您还需要确保在加载应用程序 ``JavaScript`` 之前声明模板。

我们创建了主页模板，其中包含 ``id`` 为 ``homepage`` 和 ``about`` 的页面。 在JavaScript中创建两个组件引用这两个模板：

.. code-block:: js

    const Home = {
        template: '#homepage'
    };
    const About = {
        template: '#about'
    };

下一步是给路由器一个占位符来渲染视图中的组件。这是通过使用自定义的 ``<router-view>`` HTML元素完成的。使用此元素可以控制内容的渲染位置。 它允许我们在应用视图中拥有一个页眉和页脚，而无需处理凌乱的模板或包含组件本身。

将 ``header`` 、 ``main`` 和 ``footer`` 元素添加到您的应用程序中。 在标题中添加一个 ``logo`` ，并在页脚中给予版权信息; 在主HTML元素中，放置 ``router-view`` 占位符：

.. code-block:: html

    <div id="app">
        <header>
            <div>LOGO</div>
        </header>
        <main>
            <router-view></router-view>
        </main>
        <footer>
            <small>© Myself</small>
        </footer>
    </div>

除了 ``router-view`` 之外，应用视图中的所有内容都是可选的，但它可以让您了解如何将路由器HTML元素放置到网站结构中。

下一阶段是初始化 ``Vue-router`` 并指示 ``Vue`` 使用它。 创建一个 ``VueRouter`` 的新实例并将其添加到 ``Vue`` 实例 - 类似于我们在前一节中添加 ``Vuex`` 的方式：

.. code-block:: js

    const router = new VueRouter();
    new Vue({
        el: '#app',
        router
    });

我们现在需要告诉路由器我们的路由（或路径），以及遇到每个路由时它应该加载哪个组件。在 `` Vue-router`` 实例内创建一个对象，并使用 routes 作为键和数组作为值。该数组需要为每个路由包含一个对象：

.. code-block:: js

    const router = new VueRouter({
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                component: About
            }
        ]
    });

每个路由对象都包含 ``path`` 和 ``component`` 键。 ``path`` 是要加载组件的 ``URL`` 的字符串。 ``Vue-router`` 根据先到先得的原则提供组件。例如，如果有多条路径具有相同的路径，则会使用遇到的第一条路径。确保每条路由都有起始斜杠 - 这告诉路由器它是一个根页面，而不是一个子页面，我们将在后面的章节中介绍子页面。

按下保存并在浏览器中查看您的应用程序。您应该看到 ``Home`` 模板组件的内容。如果您观察 ``URL`` ，您会注意到在页面加载时，散列和正斜杠（ ``＃/`` ）会附加到路径中。这是创建浏览组件和利用地址栏方法的路由器。如果您将其更改为第二条路由 ``＃/about`` ，您将看到 ``About`` 组件的内容。

``Vue-router`` 还能够使用 JavaScript 历史 API 创建更漂亮的 ``URL`` 。例如， ``yourdomain.com/index.html#about`` 将成为 ``yourdomain.com/about`` 。这是通过向 ``VueRouter`` 实例添加 ``mode:'history'`` 来激活的：

.. code-block:: js

    const router = new VueRouter({
        mode: 'history',
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                component: About
            }
        ]
    });

但是，它也需要一些服务器配置来捕获所有请求并将它们重定向到 ``index.html`` 页面，这超出了本书的范围，但在 ``Vue-router`` 文档中有详细介绍。

更改Vue-router的文件夹
======================
可能会出现您想要将 ``Vue`` 应用程序托管在网站的子文件夹中的情况。 在这种情况下，您需要声明项目的基础文件夹，以便 ``Vue-router`` 可以构建并侦听正确的 ``URL`` 。

例如，如果您的应用程序基于 ``/shop/`` 文件夹，则可以使用 ``Vue-router`` 实例上的 ``base`` 参数声明它：

.. code-block:: js

    const router = new VueRouter({
        base: '/shop/',
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                component: About
            }
        ]
    });

这个值需要在开始和结束时都使用斜杠。

除了 ``base`` 之外， ``Vue-router`` 还有其他一些配置选项可供选择，值得熟悉它们，因为它们可能会解决您以后遇到的问题。

链接到不同的路由
================
随着路由器按预期工作，我们现在可以继续在我们的应用中添加链接，允许用户浏览网站。 链接可以通过两种方式实现：我们可以使用传统的 ``<a href="#/about">`` 标记，或者我们可以利用>路由器提供的新HTML元素 ``<router-link to= "/about">`` 。 使用路由器链接元素时，它与 ``<a>`` 标签的作用相同，实际上在浏览器中运行时转换为一样的，但允许更多的定制和与路由器的集成。

强烈建议尽可能使用 ``router-link`` 元素，因为它比标准链路有以下优点：

- 模式更改：第一个优点与路由器的 ``mode`` 相关联。通过使用路由器链接，您可以更改路由器的 ``mode`` ，例如散列到历史记录，而不必更改应用中的每个链接。
- CSS类：使用路由器链接的另一个优点是可以应用于当前正在查看的“树”和页面中活动链接的CSS类。树中的链接是也包含根页面的父页面（例如，任何指向“/”的链接将始终具有活动类）。这是使用路由器的最大好处之一，因为手动添加和删除这些类会需要复杂的编码。这些类可以定制，我们会在稍后做。
- URL参数和命名路由：使用路由器元素的另一个好处是它可以让您使用命名路由并传递 ``URL`` 参数。这进一步允许您为页面的 ``URL`` 提供一个真实的来源，并使用名称和快捷方式来引用路由。本章后面会详细介绍。

将视图中的链接添加到视图中，以便在页面之间导航。在您的网站的 ``<header>`` 中，创建一个包含无序列表的新的 ``<nav>`` 元素。 对于每个页面，添加一个新的带有 ``router-link`` 元素的列表项。将一个 ``to`` 属性添加到链接路径中：

.. code-block:: html

    <nav>
        <ul>
            <li>
                <router-link to="/">Home</router-link>
            </li>
            <li>
                <router-link to="/about">About</router-link>
            </li>
        </ul>
    </nav>

在浏览器中查看应用程序应显示您的两个链接，允许您在两个内容页面之间切换。您还会注意到，通过单击该链接， ``URL`` 也会更新。

如果您查看浏览器的HTML检查器的链接，您会注意到 ``CSS`` 类的更改。 ``Home`` 链接始终有一个 ``router-link-active`` 类 - 这是因为它本身是活动的，或者它有一个活动子节点，例如 ``About`` 页面。 还有另一个 ``CSS`` 类，当您在两个页面之间进行导航时，即添加和删除 ``router-link-exact-active`` 。这只适用于当前活动页面上的链接。

让我们自定义应用于视图的类。在 JavaScript 的路由器的初始化中，向对象添加两个新键 - ``linkActiveClass`` 和 ``linkExactActiveClass`` ：

.. code-block:: js

    const router = new VueRouter({
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                component: About
            }
        ],
        linkActiveClass: 'active',
        linkExactActiveClass: 'current'
    });

这些键应该是不言而喻的，但 ``linkExactActiveClass`` 被应用到当前页面，被查看的页面(配置当链接被精确匹配的时候应该激活的 ``class`` )，而 ``linkActiveClass`` 是当页面或其子项之一被激活时应用的类。

链接到子路由
============
有时您想要链接到子页面。例如 ``/about/meet-the-team`` 。 幸运的是，实现该功能不需要太多的工作。在 ``routes`` 数组中创建一个新对象，指向一个带有模板的新组件：

.. code-block:: js

    const router = new VueRouter({
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                component: About
            },
            {
                path: '/about/meet-the-team',
                component: MeetTheTeam
            }
        ],
        linkActiveClass: 'active',
        linkExactActiveClass: 'current'
    });

导航到此页面时，您会注意到“主页”和“关于”链接都具有 ``active`` 类，但并没有创建我们 ``current`` 类。如果您要在导航中创建到达该页面的链接，则 ``current`` 类将应用于该页面。

带参数的动态路由
================
``Vue`` 路由器可轻松让您拥有动态网址。动态网址允许您在使用相同模板的同时使用相同的组件显示不同的数据。一个例子就是一个商店，所有的分类页面看起来都是一样的，但是根据 ``URL`` 显示不同的数据。另一个例子是产品详细信息页面 - 您不希望为每个产品创建一个组件，相反，您使用一个带有 ``URL`` 参数的组件。

网址参数可以出现在路径的任何位置，并且可以有一个或多个。每个参数都被分配了一个键，因此可以一致地创建和访问它。我们将在第9章使用 ``Vue-Router`` 动态路由加载数据期间更详细地介绍动态路由和参数。现在，我们将构建一个基本示例。

在我们开始创建组件之前，让我们来看看一个新的变量 - ``this.$route`` 。与我们如何使用 ``Vuex`` 访问全局 ``store`` 的方式类似，该变量允许我们访问有关路由， ``URL`` 和参数的大量信息。

在你的 ``Vue`` 实例中，作为一个测试，添加一个 ``mounted()`` 函数。在 ``console.log`` 中，插入 ``this.$route`` 参数：

.. code-block:: js

    new Vue({
        el: '#app',
        router,
        mounted() {
            console.log(this.$route);
        }
    });

如果打开浏览器并查看开发人员工具，则应该看到正在输出的对象。 查看此对象将显示几位信息，例如与当前路径匹配的路径和组件。前往 ``/about`` ``URL`` 会显示关于该对象的不同信息。

我们来创建一个使用这个对象参数的组件。 在您的 ``routes`` 数组中创建一个新对象：

.. code-block:: js

    const router = new VueRouter({
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                component: About
            },
            {
                path: '/user/:name',
                component: User
            }
        ],
        linkActiveClass: 'active',
        linkExactActiveClass: 'current'
    });

您会注意到与此路径不同的是冒号在路径中的 ``name`` 前面。 这告诉 ``Vue-router`` 这部分 ``URL`` 是动态的，但该部分的变量名称是 ``name`` 。

现在创建一个名为 ``User`` 的新组件，并为其创建一个模板。 对于这个例子，我们的模板将是内联的，我们将使用 ES2015 模板语法。这使用反引号，并允许将变量直接传递到模板中，而无需转义它们：

.. code-block:: js

    const User = {
        template: `<h1>Hello {{ $route.params.name }}</h1>`
    };

在模板中输出的变量来自全局路由器实例，并且是参数对象中的 ``name`` 变量。 ``name`` 变量引用 ``routes`` 数组中的路径中的冒号前面的变量。 在组件模板中，我们也可以从 ``$route`` 中省略 ``this`` 变量。

回到您的浏览器并在 ``URL`` 的末尾输入 ``＃/user/sarah`` 。您应该在网页的正文中看到 ``Hello sarah`` 。 查看 JavaScript 浏览器控制台，您应该看到 ``params`` 对象内有一个 ``name:sarah`` 的键/值对：

组件本身也可以使用这个变量。 例如，如果我们想要用户名的首字母大写，我们可以创建一个计算变量，它接受路由参数并对其进行变换：

.. code-block:: js

    const User = {
        template: `<h1>Hello {{ name }}</h1>`,
        computed: {
            name() {
                let name = this.$route.params.name;
                return name.charAt(0).toUpperCase() + name.slice(1);
            }
        }
    };

如果您不熟悉前面的代码正在做什么，它会获取字符串的第一个字符并使其大写。 然后在第一个字符后面（即单词的其余部分）分割字符串并将其附加在大写字母上。

添加这个计算的函数并刷新应用程序将会产生 ``Hello Sarah`` 。

如前所述，路由可以接受任意数量的参数，并可以通过静态或动态变量进行分隔。

将路径更改为以下内容（同时保持组件名称相同）:

.. code-block:: js

    /:name/user/:emotion

这意味着你需要去 ``/sarah/user/happy`` 才能看到用户组件。 但是，您可以访问标题为 ``emotion`` 的新参数，这意味着您可以使用以下模板渲染 ``sarah is happy`` ：

.. code-block:: js

    const User = {
        template: `<h1>{{ name }} is {{ $route.params.emotion }}</h1>`,
        computed: {
            name() {
                let name = this.$route.params.name;
                return name.charAt(0).toUpperCase() + name.slice(1);
            }
        }
    };
    const router = new VueRouter({
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                component: About
            },
            {
                path: '/:name/user/:emotion',
                component: User
            }
        ],
        linkActiveClass: 'active',
        linkExactActiveClass: 'current'
    });

当我们在接下来的几章中构建我们的商店时，动态路线将派上用场，因为我们将在产品和类别中使用它。

GET参数
=======
除了动态路由之外， ``Vue-router`` 还以一种非常简单的方式处理 ``GET`` 参数。 ``GET`` 参数是额外的 ``URL`` 参数，您可以将它们以键/值对形式传递给网页。使用 ``GET`` 参数，第一个参数前面有一个 ``？`` - 这告诉浏览器期望参数。任何其他参数都由一个 ``＆`` 符号分隔。一个例子是：

.. code-block:: html

    example.com/?name=sarah&emotion=happy

这个 ``URL`` 可以让 ``sarah`` 成为 ``name`` 的值，并以 ``emotion`` 的值为 ``happy`` 。 它们通常用于过滤或搜索。

``Vue`` 路由器让开发人员在 ``this.$route`` 变量中 ``query`` 对象获取这些值。 尝试在 ``URL`` 的末尾添加 ``?name=sarah`` 并打开 JavaScript 开发者工具。检查查询对象将显示一个 ``name`` 为键和 ``sarah`` 作为值的对象：

当我们在店铺类别中建立过滤时，我们将使用查询对象。

使用props
=========
尽管直接在组件中使用路由器参数的做法非常好，但它并不是一个好的做法，因为它将组件直接连接到路由。相反，应该使用 ``props`` - 以同样的方式，我们在本书前面的 ``HTML`` 组件中使用过它们。启用并声明后，通过 ``URL`` 传入的参数可以使用，就好像它已通过 ``HTML`` 属性传递一样。

为您的路由组件使用 ``props`` 是将选项和参数传递到路由中是更好方式，因为它具有许多优点。首先，它将组件从一个特定的 ``URL`` 结构中解耦出来 - 如您所见，我们可以将 ``props`` 直接传递给组件本身。它还有助于使您的路由组件更清晰；传入的参数清晰地显示在组件本身中，并且代码在整个组件中都更清晰。

``Props`` 只能使用动态路由 -  ``GET`` 参数仍然可以用前面的技术访问。

使用前面的示例，为 ``name`` 和 ``emotion`` 参数声明 ``props`` 。在使用基于 ``URL`` 的变量使用 ``props`` 时，您需要使用字符串数据类型：

.. code-block:: js

    const User = {
        template: `<h1>{{ name }} is {{ $route.params.emotion }}</h1>`,
        props: {
            name: String,
            emotion: String
        },
        computed: {
            name() {
                let name = this.$route.params.name;
                return name.charAt(0).toUpperCase() + name.slice(1);
            }
        }
    };

我们现在可以通过 ``props`` 和计算的值两次获得 ``this.name`` 两次。但是，因为我们通过 ``props`` 拥有 ``this.name`` 和 ``this.emotion`` ，所以我们可以更新组件以使用这些变量，而不是 ``$route`` 参数。

为避免与 ``prop`` 相冲突，更新计算函数为 ``formattedName()`` 。 我们也可以从函数中移除对变量的引用，因为新变量更具可读性：

.. code-block:: js

    const User = {
        template: `<h1>{{ formattedName }} is {{ this.emotion }}</h1>`,
        props: {
            name: String,
            emotion: String
        },
        computed: {
            formattedName() {
                return this.name.charAt(0).toUpperCase() + this.name.slice(1);
            }
        }
    };

在 ``props`` 工作之前， ``Vue`` 路由器需要在特定路线上声明使用它们。
这是在 ``routes`` 数组中启用的，使用 ``props:true`` 值设置：

.. code-block:: js

    const router = new VueRouter({
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                component: About
            },
            {
                path: '/:name/user/:emotion',
                component: User,
                props: true
            }
        ],
        linkActiveClass: 'active',
        linkExactActiveClass: 'current'
    });

设置prop默认值
==============
路由参数现在可以作为 ``props`` 使用，这使我们可以轻松创建默认值。如果我们想要使参数可选，那么我们需要添加几个 ``if()`` 语句来检查变量的存在。

然而，通过 ``props`` ，我们可以像我们之前所做的那样声明默认值。 添加 ``emotion`` 变量的默认值：

.. code-block:: js

    const User = {
        template: `<h1>{{ formattedName }} is {{ this.emotion }}</h1>`,
        props: {
            name: String,
            emotion: {
                type: String,
                default: 'happy'
            }
        },
        computed: {
            formattedName() {
                return this.name.charAt(0).toUpperCase() + this.name.slice(1);
            }
        }
    };

现在我们可以在我们的路由器中创建一个新的路由器，它使用了没有最终变量的同一个组件。 不要忘记为新路由启用 ``props`` ：

.. code-block:: js

    const router = new VueRouter({
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                component: About
            },
            {
                path: '/:name/user',
                component: User,
                props: true
            },
            {
                path: '/:name/user/:emotion',
                component: User,
                props: true
            }
        ],
        linkActiveClass: 'active',
        linkExactActiveClass: 'current'
    });

现在，通过访问 ``/sarah/user`` ，我们应该看到声明的 ``sarah is happy`` 的文本。

使用静态props
=============
除了配置布尔值以外，路径中的 ``props`` 参数还可以接受带有 ``props`` 列表(多个插值)的对象。这允许您使用相同的组件并根据 ``URL`` 更改其状态，而不需要通过路径传递变量，例如，如果要激活或取消激活部分模板。

.. note:: 当通过 ``URL`` 传递 ``props`` 对象时，它会覆盖整个 ``props`` 对象，这意味着你必须都不或全部声明。道具变量也将优先于动态的，基于 ``URL`` 的变量。

更新新的 ``/:name/user`` 路径以在路径中包含 ``props`` - 从路径中删除 ``:name`` 变量，以使其变为 ``/user`` ：

.. code-block:: js

    const router = new VueRouter({
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                component: About
            },
            {
                path: '/user',
                component: User,
                props: {
                    name: 'Sarah',
                    emotion: 'happy'
                }
            },
            {
                path: '/:name/user/:emotion',
                component: User,
                props: true
            }
        ],
        linkActiveClass: 'active',
        linkExactActiveClass: 'current'
    });

导航到 ``/user`` 应该显示与我们以前相同的句子。在某些情况下，您可能不希望用户共享特定的 ``URL`` 或基于轻松更改的参数更改应用的状态，因此在“幕后”传递道具（不使用 ``URL`` ）非常理想。

嵌套路由
========
嵌套路由与子路由不同，因为它们存在于已经与路由的开始部分匹配的组件内。这使您可以在现有视图中显示不同的内容。

Twitter 就是一个很好的例子。如果您访问 Twitter 用户的个人资料页面，则可以查看他们关注的人员，关注他们的人员以及他们创建的列表。如果您在浏览页面时观察 ``URL`` ，则会发现一个重复出现的模式：用户名跟随各种不同的页面。嵌套路由和子路由之间的区别在于嵌套路由允许您在整个不同的子页面（例如标题栏和侧栏）中保持组件相同。

这样做的好处是，用户可以收藏并分享链接，使页面更易于访问，并且有利于搜索引擎优化。使用简单的切换或标签(tab)框在视图中显示不同的内容，这些优点都不容易实现。

要将 ``Twitter`` 模式用 ``Vue`` 路由重现，它看起来如下所示：

.. code-block:: html

    https://twitter.com/:user/:page

如果我们用前面的路由方法创建它，我们将不得不为每个页面构建组件，这些组件包含模板中侧边栏中的标题和用户信息 - 如果需要更新代码，这会很痛苦！

让我们为我们的 ``About`` 页面制作一些嵌套路由。 我们不会在我们的商店应用程序中使用嵌套路线，但了解 ``Vue`` 路由器的功能很重要。

创建两个新的组件 - ``AboutContact`` ，它将显示联系信息， ``AboutFood`` ，一个将详细描述你喜欢吃的食物的组件。 虽然不是必需的，但在组件名称中保留对父组件的引用（在这种情况下为 ``About`` ）是一个不错的主意 - 当您稍后查看它们时，会将这些组件连接在一起！ 为每个组件提供一些包含固定内容的模板：

.. code-block:: html

    const AboutContact = {
        template: `<div>
            <h2>This is some contact information about me</h2>
            <p>Find me online, in person or on the phone</p>
        </div>`
    };
    const AboutFood = {
        template: `<div>
            <h2>Food</h2>
            <p>I really like chocolate, sweets and apples.</p>
        </div>`
    };

下一步是在你的 ``#about`` 模板中创建占位符，以便嵌套路由进行渲染。该元素与我们以前见过的元素完全相同 - ``<router-view>`` 元素。为了演示它可以放在任何地方，请在模板的两个段落之间添加它：

.. code-block:: html

    <script type="text/x-template" id="about">
        <div>
            <h1>About Me</h1>
            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus sed metus magna. Vivamus eget est nisi. Phasellus vitae nisi sagittis, ornare dui quis, pharetra leo. Nullam eget tel
                <router-view></router-view>
            <p>Curabitur et arcu fermentum, viverra lorem ut, pulvinar arcu. Fusce ex massa, vehicula id eros vel, feugiat commodo leo. Etiam in sem rutrum, porttitor velit in, sollicitudin tortor.
        </div>
    </script>

在浏览器中查看 ``About`` 页面不会渲染任何东西，也不会破坏应用程序。下一步是将这些组件的嵌套路由添加到路由器。 我们不是将它们添加到顶层 ``routes`` 数组中，而是使用 ``children`` 的关键字在 ``/about`` 路由中创建一个数组。该数组的语法是主数组的精确副本 - 也就是一组路由对象。

为每个包含 ``path`` 和 ``component`` 键的 ``routes`` 添加一个对象。 需要注意的是，如果你想把 ``path`` 添加到父节点的末尾，它不应该以 ``/`` 开头。

例如，如果您希望 ``URL`` 是 ``/about/contact`` 来呈现 ``AboutContact`` 组件，则可以使路由组件如下所示：

.. code-block:: js

    const router = new VueRouter({
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                component: About,
                children: [
                    {
                        path: 'contact',
                        component: AboutContact
                    },
                    {
                        path: 'food',
                        component: AboutFood
                    }
                ]
            }
        ],
        linkActiveClass: 'active',
        linkExactActiveClass: 'current'
    });

但是，如果您希望 ``URL`` 简单为 ``/contact`` ，但仍在 ``About`` 组件中呈现 ``AboutContact`` 组件，则可以添加前面的斜杠。 尝试在不使用斜线的情况下查看应用程序，然后添加该应用程序，以查看它的差异。 如果你想要一个子路由器来显示父母加载时没有 ``URL`` 的第二部分，你可以使用一个空的路径 ``path:''`` 。

现在，请不要使用斜杠并添加前面的 ``children`` 数组。转到浏览器并导航到 ``About`` 页面。添加 ``/contact`` 或 ``/food`` 到 ``URL`` 的末尾，并注意新内容会出现在您之前添加到模板中的 ``<router-link>`` 元素的位置。

可以从任何地方为这些组件创建链接，与链接“主页”和“关于”页面的方式相同。您可以将它们添加到 about 模板中，以便它们仅在导航到该页面时显示，或者将它们添加到应用视图中的主导航中。

创建一个404页面
===============
在构建应用程序或网站时，问题和错误都会发生。出于这个原因，有错误页面是一个好主意。最常见的页面将是 404 页面 - 链接不正确或页面移动时显示的消息。404 是未找到页面的提示。

如前所述， ``Vue-router`` 将根据先来先服务原则匹配路由。我们可以通过使用通配符（ ``*`` ）字符作为最后一个路由来使用它。由于通配符匹配每条路由，因此只有与之前路由不匹配的 ``URL`` 才能被该路由捕获。

使用简单模板创建一个名为 ``PageNotFound`` 的新组件，并添加一个使用通配符作为路径的新路由：

.. code-block:: js

    const PageNotFound = {
        template: `<h1>404: Page Not Found</h1>`
    };
    const router = new VueRouter({
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                component: About,
                children: [
                    {
                        path: 'contact',
                        component: AboutContact
                    },
                    {
                        path: 'food',
                        component: AboutFood
                    }
                ]
            },
            {
                path: '*',
                component: PageNotFound
            }
        ],
        linkActiveClass: 'active',
        linkExactActiveClass: 'current'
    });

在浏览器中打开应用程序，然后在 ``URL`` 的末尾输入任何内容（除了 ``about`` ）并按 Enter 键 - 应该显示 404 标题。

.. note:: 虽然这是模拟页面未找到请求，但实际上并未将实际的 HTTP 代码发送到浏览器。如果您在生产中使用 Vue Web 应用程序，建立服务器端错误检查是一个好主意，因此在错误 URL 的情况下，可以正确通知浏览器。

命名组件，路线和视图
====================
在使用 ``Vue-router`` 时，不需要为您的路由和组件添加名称，但这是一个很好的做法，也是一个很好的习惯。

命名组件
--------
具有名称的组件允许您更轻松地调试错误。 在 ``Vue`` 中，当一个组件抛出 JavaScript 错误时，它会给你该组件的名称，而不是列出 ``Anonymous`` 作为组件。

例如，如果您尝试在 ``food`` 组件中输出 ``{{ test }}`` 的变量 - 一个不可用的变量。 默认情况下， JavaScript 控制台错误如下所示：

(图)

请注意堆栈中的两个 ``<Anonymous>`` 组件。

通过给我们的组件添加名称，我们可以轻松确定问题所在。 在以下示例中，名称已添加到 ``About`` 和 ``AboutFood`` 组件中：

(图)

您可以很容易地看到错误出现在 ``<AboutFood>`` 组件中。

给组件添加名称就像向对象添加 ``name`` 的键一样简单，名称就是值。这些名称遵守与我们创建 HTML 元素组件时相同的规则： **不允许有空格，但允许使用连字符和字母。** 为了让我快速识别代码，我选择了将我的组件命名为与定义它的变量名称相同：

.. code-block:: js

    const About = {
        name: 'About',
        template: '#about'
    };
    const AboutFood = {
        name: 'AboutFood',
        template: `<div>
        <h2>Food</h2>
        <p>I really like chocolate, sweets and apples.</p>
        </div>`
    }

命名路由
--------
使用 ``VueRouter`` 时可以命名的另一个对象是路由本身。这使您可以简化路由的位置并更新路径，而无需查找和替换应用中的所有实例。

将 ``name`` 键添加到 ``routes`` ，如以下示例所示：

.. code-block:: js

    const router = new VueRouter({
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                component: About,
                children: [
                    {
                        name: 'contact',
                        path: 'contact',
                        component: AboutContact
                    },
                    {
                        name: 'food',
                        path: 'food',
                        component: AboutFood
                    }
                ]
            },
            {
                path: '*',
                component: PageNotFound
            }
        ],
        linkActiveClass: 'active',
        linkExactActiveClass: 'current'
    });

您现在可以在创建 ``router-link`` 组件时使用该名称，如下所示：

.. code-block:: html

    <router-link :to="{name: 'food'}">Food</router-link>

请注意 ``to`` 属性之前的冒号。这确保内容被解析为对象，而不是文字字符串。使用命名路由的另一个优点是能够将特定属性传递给我们的动态路径。使用本章前面的示例，我们可以通过编程方式构建 ``URL`` ，从路径构建器中抽取数据。这是命名的 ``routes`` 真正使用的地方。假设我们有以下路径：

.. code-block:: json

    { name: 'user', path: '/:name/user/:emotion', component: User }

我们需要将 ``name`` 和 ``emotion`` 变量传递给组件渲染 ``URL`` 。
我们可以像之前那样直接传递给 to  ``URL`` 字符串，或者也可以对 ``to`` 使用以命名路由对象表示法：

.. code-block:: html

    <router-link :to="{name: 'user', params: { name: 'sarah', emotion: 'happy' }}">
        Sarah is Happy
    </router-link>

在浏览器中查看此链接将显示锚链接已正确生成：

.. code-block:: text

    /sarah/user/happy

这使我们可以灵活地使用变量重新排列 ``URL`` ，而无需更新应用程序的其余部分。 如果您想在 ``URL`` 末尾传递参数（例如 ``?name=sarah`` ），则可以将 ``params`` 键更改为 ``query`` ，因为它遵循相同的格式：

.. code-block:: html

    <router-link :to="{name: 'user', query: { name: 'sarah', emotion: 'happy' }}">
        Sarah is Happy
    </router-link>

在路径重新配置为不接受参数的情况下，它将生成以下链接：

.. code-block:: text

    /user?name=sarah&amp;emotion=happy

互换 ``params`` 和 ``query`` 时要小心 - 因为它们会受到您使用 ``path`` 还是 ``name`` 的影响。 使用 ``path`` 时， ``params`` 对象将被忽略，而 ``query`` 则不会。 要使用 ``params`` 对象，您需要使用命名路由。或者，使用 ``$`` 变量将参数传递到 ``path`` 中。

命名视图
--------
``Vue`` 路由器还允许您命名视图，让您将不同的组件传递到应用程序的不同部分。这方面的一个例子可能是一家商店，那里有一个侧边栏和主要内容区域。不同的页面可能以不同的方式使用这些区域。

``About`` 页面可以使用主要内容显示关于内容，而使用侧栏显示联系人详细信息。 但是，商店页面将使用主要内容列出产品，并使用侧边栏显示过滤器。

要做到这一点，创建第二个 router-view 元素作为您的原始的兄弟。保留原始位置，但在第二个位置添加一个 ``name`` 属性，并使用适当的标题：

.. code-block:: main

    <main>
        <router-view></router-view>
    </main>
    <aside>
        <router-view name="sidebar"></router-view>
    </aside>

在路由器实例中声明路由时，我们现在要使用一个新的键， ``components`` ，并删除以前的单一 ``component`` 键。这接受具有视图名称和组件名称的键值对的对象。

建议您将主路由保留未命名，所以您不需要更新每条路由。 如果您决定命名主路由，则需要为您的应用中的每条路线执行下一步。

将 ``About`` 路由更新为使用此新键并将其设置为对象。下一步是告诉代码将要访问的组件。

使用默认值作为键，将 ``About`` 组件设置为值。 这会将 ``About`` 组件的内容放入您的未命名 ``router-view`` 中，即主要视图。这也是使用单数 ``component`` 键的简写：

.. code-block:: js

    const router = new VueRouter({
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                components: {
                    default: About
                }
            },
            {
                path: '*',
                component: PageNotFound
            }
        ],
        linkActiveClass: 'active',
        linkExactActiveClass: 'current'
    });

接下来，添加第二个键值，指定第二个的 ``router-view`` 侧边栏。在 ``/about`` URL 导航到时，命名要填充此区域的组件。为此，我们将使用 ``AboutContact`` 组件：

.. code-block:: js

    const router = new VueRouter({
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                components: {
                    default: About,
                    sidebar: AboutContact
                }
            },
            {
                path: '*',
                component: PageNotFound
            }
        ],
        linkActiveClass: 'active',
        linkExactActiveClass: 'current'
    });

在浏览器中运行应用程序将渲染这两个组件，联系组件的内容显示在边栏中。

以编程方式导航，重定向和添加别名
================================
在构建应用程序时，可能会出现需要使用某种不同导航技术的情况。这些可能以编程方式导航，例如在组件或主 ``Vue`` 实例中，在用户击中特定 ``URL`` 时重定向用户，或用不同的 ``URL`` 加载相同的组件。

以编程方式导航
--------------
您可能想要从代码(一个组件或者动作)更改路径， ``URL`` 或用户流。一个例子可能是在用户添加一个项目后将其发送到购物车。

为此，您需要在路由器实例上使用 ``push()`` 函数。 ``push`` 的值可以是直接 ``URL`` 的字符串，也可以接受对象以传递命名路由或路由参数。 ``push`` 函数的允许内容与 ``router-link`` 元素上的 ``to=""`` 属性完全相同。例如：

.. code-block:: js

    const About = {
        name: 'About',
        template: '#about',
        methods: {
            someAction() {
                /* Some code here */
                // direct user to contact page
                this.$router.push('/contact');
            }
        }
    };

或者，您可以使用参数指向命名路由：

.. code-block:: js

    this.$router.push({name: 'user', params: { name: 'sarah', emotion: 'happy' }});

重定向
------
使用 ``VueRouter`` 重定向相当简单。如果您将 ``/about`` 页面移动到 ``/about-us`` URL，重定向的示例可能是您需要将第一个网址重定向到第二个网址，以防万一任何人共享或标记了您的链接，或者搜索引擎缓存了该网址。

您可能会试图创建一个基本组件，创建它时使用 ``router.push()`` 函数向用户发送到新的 ``URL`` 。

相反，您可以添加路由并在其中指定重定向：

.. code-block:: js

    const router = new VueRouter({
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                redirect: '/about-us'
            },
            {
                path: '/about-us',
                component: About
            },
            {
                path: '*',
                component: PageNotFound
            }
        ],
        linkActiveClass: 'active',
        linkExactActiveClass: 'current'
    });

再一次，重定向键的内容可以是文字字符串或对象 - 很像 ``push()`` 函数。 通过前面的介绍，如果用户访问 ``/about`` ，他们将被重定向到 ``/about-us`` ，并显示 ``About`` 组件。

别名路由
--------
在某些情况下，您希望在两个 ``URL`` 下显示相同的组件。虽然不推荐作为标准做法，但有些边界情况需要这样做。

别名键被添加到现有路由并仅接受路径的字符串。 使用前面的示例，下面将显示 ``About`` 组件，无论用户是访问 ``/about`` 还是 ``/about-us`` ：

.. code-block:: js

    const router = new VueRouter({
        routes: [
            {
                path: '/',
                component: Home
            },
            {
                path: '/about',
                alias: '/about-us',
                component: About,
            },
            {
                path: '*',
                component: PageNotFound
            }
        ],
        linkActiveClass: 'active',
        linkExactActiveClass: 'current'
    });

总结
====
您现在应该熟悉 ``Vue`` 路由器，如何初始化它，可用的选项以及如何创建新的路由 - 无论是静态还是动态。在接下来的几章中，我们将开始创建我们的商店，首先加载一些商店数据并创建产品页面。

