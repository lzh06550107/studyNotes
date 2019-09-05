*****
Vue入门
*****

.. contents:: 目录
   :depth: 4

``Vue`` （发音 ``view`` ）是为构建交互式用户界面而创建的非常强大的 ``JavaScript`` 库。尽管能够处理大型单页面应用程序，但 ``Vue`` 也非常适合为小型个人使用案例提供框架。它的小文件大小意味着它可以集成到现有的生态系统中，而不会增加大量的代码。

它内置了简单 ``API`` ，与其他竞争对手( ``React`` 和 ``Angular`` )比较起来更容易。虽然它借用了这些库的一些逻辑和方法，但它已经满足了开发人员需要，即更简单的库来构建应用程序。

与 ``React`` 或 ``Angular`` 不同， ``Vue`` 的好处之一就是它生成的干净的 ``HTML`` 输出。其他 ``JavaScript`` 库倾向于将在 ``HTML`` 代码中各处带有额外的属性和 ``class`` ，而 ``Vue`` 将这些移除以产生干净的语义输出。

在本书的第一部分中，我们将构建一个使用 ``JSON`` 字符串作为显示数据的应用程序。然后，我们将着眼于过滤和操作数据，然后再继续研究构建可重用组件，以减少代码中的重复。

在本章中，我们将看看：

- 如何通过包含 ``JavaScript`` 文件来开始使用 ``Vue`` ；
- 如何初始化您的第一个 ``Vue`` 实例和查看数据对象；
- 检查 ``computed`` 的函数和属性；
- 了解 ``Vue`` 方法；

创建工作区
==========
要使用 ``Vue`` ，我们首先需要在我们的 ``HTML`` 中引入 ``Vue`` 库包含并初始化它。 对于本书第一部分中的示例，我们将在单个 ``HTML`` 页面中构建我们的应用程序。 这意味着初始化和控制 ``Vue`` 的 ``JavaScript`` 将被放置在页面的底部。打开你最喜欢的文本编辑器并创建一个新的 ``HTML`` 页面。使用以下模板作为一个起点：

.. code-block:: html

    <!DOCTYPE html>
    <html>
        <head>
            <meta charset="utf-8">
            <title>Vue.js App</title>
        </head>
        <body>
        <div id="app"></div>
        <script src="https://unpkg.com/vue"></script>
        <script type="text/javascript">
            // JS Code here
        </script>
        </body>
    </html>

主要的 ``HTML`` 标签和结构你应该很熟悉。 让我们来看看其他几个方面。

应用空间
========
这里是你的应用程序的容器，并提供了一个 ``Vue`` 工作的画布。所有的 ``Vue`` 代码将被放置在这个标签中。 实际的标签可以是任何 ``HTML`` 元素 -  ``main`` ， ``section`` 等等。 元素的 ``ID`` 可以是任何你想要的名称但必须是唯一的。 这允许您在一个页面上拥有多个 ``Vue`` 实例，或标识哪个 ``Vue`` 实例与哪个 ``Vue`` 代码相关：

.. code-block:: html

    <div id='app'></div>

在教程中，带有 ``ID`` 的这个元素将被称为应用程序空间或视图。 应该注意的是，应用程序的所有 ``HTML`` 和标签以及代码都应放置在此容器中。

.. note:: 虽然您可以将大多数 ``HTML`` 标记用于应用程序空间，但不能在 ``<body>`` 或 ``<HTML>`` 标记上初始化 ``Vue``  - 如果这样做， ``Vue`` 将抛出 ``JavaScript`` 错误并且无法初始化。你将不得不在你的 ``<body>`` 内使用一个元素。

Vue库
=====
对于本书中的示例，我们将使用来自 ``CDN`` （内容交付网络） ``unpkg`` 的托管版本的 ``Vue.js`` 。这确保我们在应用程序中拥有最新版本的 ``Vue`` ，也意味着我们不需要创建和托管其他 ``JavaScript`` 文件。 ``Unpkg`` 是托管流行库的独立网站。 它使您能够快速轻松地将 ``JavaScript`` 包添加到 ``HTML`` 中，而无需亲自下载和托管文件：

.. code-block:: html

    <script src="https://unpkg.com/vue"></script>

在部署代码时，最好从本地文件提供库，而不是依赖 ``CDN`` 。 如果他们发布更新，这可以确保您的实现能够与当前保存的版本一致。 它还会提高应用程序的速度，因为它不需要从另一个服务器请求文件。

库之后的脚本块包含我们将要为我们的 ``Vue`` 应用程序编写所有 ``JavaScript`` 的地方。

初始化Vue并显示第一个消息
=========================
现在我们已经创建了一个模板，我们可以使用以下代码初始化 ``Vue`` 并将其绑定到 ``HTML`` 应用程序空间：

.. code-block:: js

    const app = new Vue().$mount('#app');

此代码创建 ``Vue`` 的新实例，并将其装载到 ``ID`` 值为 ``app`` 的 ``HTML`` 元素上。 如果保存你的文件并在浏览器中打开它，你会注意到什么都没有发生。 然而，在底层，这一行代码已经将 ``div`` 与 ``app`` 变量（它是 ``Vue`` 应用程序的一个实例）相连接。

``Vue`` 本身有许多对象和属性，我们现在可以用它来构建我们的应用程序。 你会遇到的第一个是 ``el`` 属性。 值为 ``HTML ID`` ，该属性告诉 ``Vue`` 它应该绑定哪个元素以及该应用将包含在哪里。 这是安装 ``Vue`` 实例的最常用方法，所有 ``Vue`` 代码都应该在这个元素中发生：

.. code-block:: js

    const app = new Vue({
        el: '#app'
    });

当在实例中未指定 ``el`` 属性时， ``Vue`` 将以未挂载状态初始化(这允许在挂载之前指定要运行的任何函数或方法)来运行和完成。 准备就绪后，您可以独立调用挂载函数。在幕后，当使用 ``el`` 属性时， ``Vue`` 使用 ``$mount`` 函数挂载实例。 如果您想等待特定时机挂载，可以单独调用 ``$mount`` 函数，例如：

.. code-block:: js

    const app = new Vue();
    // When ready to mount app:
    app.$mount('#app');

但是，因为我们在本书中不需要延迟挂载时间的执行，所以我们可以将 ``el`` 元素与 ``Vue`` 实例一起使用。 使用 ``el`` 属性也是安装 ``Vue`` 应用程序的最常见方式。

除了 ``el`` 值， ``Vue`` 还有一个 ``data`` 对象，其中包含我们访问应用程序或应用程序空间所需的任何数据。 在 ``Vue`` 实例中创建一个新的数据对象，并通过执行以下操作为属性赋值：

.. code-block:: js

    const app = new Vue({
        el: '#app',
        data: {
            message: 'Hello!'
        }
    });

在应用程序空间内，我们现在可以访问 ``message`` 变量。 为了在应用中显示数据， ``Vue`` 使用 ``Mustache`` 模板语言输出数据或变量。 这是通过在双括号 ``{{variable}}`` 之间放置变量名来实现的。 对于逻辑语句（如 ``if`` 或 ``foreach`` ）获取 ``HTML`` 属性，本章后面将介绍这些属性。

在应用程序空间内，添加代码以输出字符串：

.. code-block:: html

    <div id="app">
        {{ message }}
    </div>

保存文件，在浏览器中打开它，并且您应该看到您的 ``Hello！`` 串。

如果您没有看到任何输出，请检查 ``JavaScript`` 控制台以查看是否有任何错误。确保远程 ``JavaScript`` 文件加载正确，因为某些浏览器和操作系统需要额外的安全措施，才允许在计算机上最终查看页面时加载某些远程文件。

``data`` 对象可以处理多个键和数据类型。向数据对象添加更多值并查看会发生什么 - 确保在每个值后添加逗号。 数据值是简单的 ``JavaScript`` 表达式 ，也可以处理基本的数学运算 - 尝试添加新的价格键并将值设置为 ``18 + 6`` 以查看会发生什么。或者，尝试添加一个 ``JavaScript`` 数组并将其打印出来：

.. code-block:: js

    const app = new Vue({
        el: '#app',
        data: {
            message: 'Hello!',
            price: 18 + 6,
            details: ['one', 'two', 'three']
        }
    });

在您的应用空间中，您现在可以输出每个值 - ``{{price}}`` 和 ``{{details}}`` 。 我们将在第2章“显示，循环，搜索和过滤数据”中介绍使用和显示列表。

``Vue`` 中的所有数据都是反应性的，可以由用户或应用程序更新。可以通过打开浏览器的 ``JavaScript`` 控制台并自行更新内容来测试。尝试输入 ``app.message ='再见！';``  并按 ``Enter`` 键 - 你的应用程序的内容将会更新。这是因为您直接引用属性 - 第一个 ``app`` 引用了您的 ``const app`` 在您的 ``JavaScript`` 中初始化的 ``app`` 变量。点号表示它的属性，并且 ``message`` 是数据的键。 您也可以更新 ``app.details`` 或 ``price`` 为任何你想要的值！

computed值
==========
``Vue`` 中的 ``data`` 对象非常适合直接存储和检索数据，但是，在您的应用程序中输出数据之前，可能会有时间格式要处理数据。我们可以使用 ``Vue`` 中的 ``computed`` 对象来做到这一点。使用这种技术，我们开始遵循 ``MVVM`` （ ``Model-View-ViewMode`` ）方法。

``MVVM`` 是一种软件体系结构模式，可以将应用程序的各个部分分离为不同的部分。 ``Model`` （或数据）是您的原始数据输入 - 可以来自 ``API`` ，数据库或硬编码数据值。在 ``Vue`` 的上下文中，这通常是我们之前使用的 ``data`` 对象。

``View`` 是您的应用程序的前端。这应该只用于从模型中输出数据，并且除了一些不可避免的 ``if`` 语句之外，不应该包含任何逻辑或数据操作。对于 ``Vue`` 应用程序，这是放在 ``<div id ="app"></div>`` 标记中的所有代码。

``ViewModel`` 是两者之间的桥梁。它允许您在视图输出之前操纵模型中的数据。例如，可以将字符串更改为大写或在价格前缀货币符号，直至从列表中筛选打折产品或计算数组中字段的总值。在 ``Vue`` 中，这是 ``computed`` 对象所在的位置。

``computed`` 对象可以具有所需的任意数量的属性 - 但是，它们必须是函数。这些函数可以利用已经在 ``Vue`` 实例上的数据并返回一个值，该值可以是视图中使用的字符串，数字或数组。

第一步是在我们的 ``Vue`` 应用程序中创建一个 ``computed`` 对象。在这个例子中，我们将使用 ``computed`` 值将字符串转换为小写。

.. code-block:: js

    const app = new Vue({
        el: '#app',
        data: {
            message: 'Hello Vue!'
        },
        computed: {
        }
    });

不要忘记在数据对象的结束括号( ``}`` )后面添加一个逗号( ``，`` )，以便 ``Vue`` 知道需要一个新对象。

下一步是在 ``computed`` 对象内部创建一个函数。开发中最难的部分之一是函数命名 - 确保你的函数的名称是描述性的。由于我们的应用程序非常小，我们将其命名为 ``messageToLower`` ：

.. code-block:: js

    const app = new Vue({
        el: '#app',
        data: {
            message: 'HelLO Vue!'
        },
        computed: {
            messageToLower() {
                return 'hello vue!';
            }
        }
    });

在前面的例子中，我将它设置为返回一个硬编码的字符串，它是消息变量内容的小写版本。 计算函数可以像在视图中使用数据键一样使用它们。 更新视图以输出使用 ``{{messageToLower}}`` 而不是 ``{{message}}`` ，并在浏览器中查看结果。

但是，此代码存在一些问题。首先，如果 ``messageToLower`` 的值被硬编码，我们可以将它添加到另一个数据属性。其次，如果 ``message`` 值发生了变化，小写字母的版本现在会不正确。

在 ``Vue`` 实例中，我们可以使用 ``this`` 变量访问 ``data`` 值和 ``computed`` 值 - 我们将更新函数以使用现有的 ``message`` 值：

.. code-block:: js

    computed: {
        messageToLower() {
            return this.message.toLowerCase();
        }
    }

``messageToLower`` 函数现在引用现有的 ``message`` 变量，并使用本地 ``JavaScript`` 函数将字符串转换为小写。 尝试在应用程序或 ``JavaScript`` 控制台中更新 ``message`` 变量以查看它的更新。

计算函数不仅限于基本功能 - **请记住，它们旨在从视图中移除所有逻辑和操作。**  一个更复杂的例子如下：

.. code-block:: js

    const app = new Vue({
        el: '#app',
        data: {
            price: 25,
            currency: '$',
            salesTax: 16
        },
        computed: {
            cost() {
                // Work out the price of the item including salesTax
                let itemCost = parseFloat(Math.round((this.salesTax / 100) * this.price) + this.price).toFixed(2);
                // Add text before displaying the currency and amount
                let output = 'This item costs ' + this.currency + itemCost;
                // Append to the output variable the price without salesTax
                output += ' (' + this.currency + this.price +  ' excluding salesTax)';
                // Return the output value
                return output;
            }
        }
    });

虽然这看起来似乎很先进，但代码是采取固定价格并计算增加销售税的情况。价格，销售税和货币符号都作为值存储在数据对象中，并在 ``cost()`` 计算函数中访问。 查看输出结果 ``{{cost}}`` ，产生以下结果：

.. code-block:: text

    This item costs $29.00 ($25 excluding sales tax)

如果任何数据由用户或应用程序本身更新，计算函数将重新计算和更新。 这使我们的函数可以根据价格和销售税值进行动态更新。 在浏览器的控制台中尝试以下命令之一：

.. code-block:: js

    app.salesTax = 20
    app.price = 99.99

销售税和价格将立即更新。这是因为计算函数对数据对象和应用程序的其余部分是反应性的。

Methods和可重用函数
===================
在您的 ``Vue`` 应用程序中，您可能希望以一致或重复的方式计算或操作数据，或者运行不需要输出到视图的任务。例如，如果您想计算每个价格的销售税或在分配给一些变量数据之前先从 ``API`` 检索这些数据。

我们需要这样做，而不是每次创建计算函数， ``Vue`` 允许您创建函数或方法。 这些会在您的应用程序中声明并可以从任何地方访问 - 类似于 ``data`` 或 ``computed`` 函数。

将一个方法对象添加到您的 ``Vue`` 应用程序中，并注意对该数据对象的更新：

.. code-block:: js

    const app = new Vue({
        el: '#app',
        data: {
            shirtPrice: 25,
            hatPrice: 10,
            currency: '$',
            salesTax: 16
        },
        methods: {
        }
    });

在数据对象中，价格键被两个价格 - ``shirtPrice`` 和 ``hatPrice`` 取代。 我们将创建一个方法来计算每个价格的销售税。

与为计算对象创建函数类似，创建一个名为 ``calculateSalesTax`` 的方法函数。 该函数需要接受单个参数，这将是价格。 在里面，我们将使用前面例子中的代码来计算销售税。 请记住用参数名称 ``price`` 替换 ``this.price`` ，如下所示：

.. code-block:: js

    methods: {
        calculateSalesTax(price) {
            // Work out the price of the item including sales tax
            return parseFloat(
                Math.round((this.salesTax / 100) * price)  + price).toFixed(2);
        }
    }

保存它不会对我们的应用程序做任何事情 - 我们需要调用该函数。 在你看来，更新输出以使用该函数并传入 ``shirtPrice`` 变量：

.. code-block:: html

    <div id="app">
    {{ calculateSalesTax(shirtPrice) }}
    </div>

保存您的文档并在浏览器中检查结果 - 您期望的是什么？ 接下来的任务是价格前缀货币符号。 我们可以通过添加第二个方法来实现这一点，该方法返回传入函数的参数开头的附加货币符号：

.. code-block:: js

    methods: {
        calculateSalesTax(price) {
            // Work out the price of the item including sales tax
            return parseFloat(
                Math.round((this.salesTax / 100) * price) +
                price).toFixed(2);
        },
        addCurrency(price) {
            return this.currency + price;
        }
    }

在我们看来，然后我们更新我们的输出来利用这两种方法。我们可以将第一个函数 ``calculateSalesTax`` 作为第二个 ``addCurrency`` 函数的参数，而不是分配给变量。 这是因为第一个方法 ``calculateSalesTax`` 接受 ``shirtPrice`` 参数并返回新金额。我们不是将其作为变量保存而是将该变量传递给 ``addCurrency`` 方法，即将函数执行结果直接传递给该函数：

.. code-block:: js

    {{ addCurrency(calculateSalesTax(shirtPrice)) }}

然而，每当我们需要输出价格时，就会开始厌倦不得不调用这两个函数。从这里，我们有两个选择：

- 我们可以创建第三个方法，名为 ``cost()`` - 接受 ``price`` 参数并传递输入给上面的两个函数。
- 创建一个计算函数，如 ``shirtCost`` ，它使用 ``this.shirtPrice`` 代替一个传入的参数。

可选地，我们也可以创建一个名为 ``shirtCost`` 的方法，它和我们的计算函数一样；然而，在这种情况下使用计算函数更好。

**这是因为计算的函数被缓存，而方法函数不是。** 假设我们的方法比现在复杂得多，那么在函数重复调用函数后（例如，如果我们想在多个位置显示价格）可能会对性能产生影响。通过计算函数，只要数据不发生变化，您可以多次调用它，结果被应用程序缓存。如果数据确实发生了变化，则只需重新计算一次，然后重新缓存该结果。为 ``shirtPrice`` 和 ``hatPrice`` 创建一个计算函数，以便可以在视图中使用这两个变量。不要忘记，要在内部调用函数，您必须使用 ``this`` 变量 - 例如 ``this.addCurrency()`` 。使用以下 ``HTML`` 代码作为您的视图的模板：

.. code-block:: html

    <div id="app">
        <p>The shirt costs {{ shirtCost }}</p>
        <p>The hat costs {{ hatCost }}</p>
    </div>

.. code-block:: js

    const app = new Vue({
        el: '#app',
        data: {
            shirtPrice: 25,
            hatPrice: 10,
            currency: '$',
            salesTax: 16
        },
        computed: {
            shirtCost() {
                return this.addCurrency(this.calculateSalesTax(this.shirtPrice))
            },
            hatCost() {
                return this.addCurrency(this.calculateSalesTax(this.hatPrice))
            },
        },
        methods: {
            calculateSalesTax(price) {
                // Work out the price of the item including sales tax
                return parseFloat(
                    Math.round((this.salesTax / 100) * price) + price).toFixed(2);
            },
            addCurrency(price) {
                return this.currency + price;
            }
        }
    });

总结
====
在本章中，我们学习了如何开始使用 ``Vue JavaScript`` 框架。 我们
检查了 ``Vue`` 实例中的 ``data`` , ``computed`` 和 ``methods`` 对象。 我们
介绍了如何在框架内使用每一种，以及它们各自的优势。

