*******************
Vuebnb原型，你的第一个vue项目
*******************

在本章中，我们将学习 ``Vue.js`` 的基本功能。 然后，我们将通过构建案例研究项目 ``Vuebnb`` 的原型来将这些知识付诸实践。

本章涵盖以下主题：

- ``Vue.js`` 的安装和基本配置；
- ``Vue.js`` 基本概念，如数据绑定，指令，观察者和生命周期钩子；
- ``Vue`` 的反应系统如何工作；
- 案例研究项目的项目需求；
- 使用 ``Vue.js`` 添加页面内容，包括动态文本，列表和标题图像
- 使用 ``Vue`` 构建图像模态 ``UI`` 功能；

Vuebnb 原型
===========
在本章中，我们将构建一个 ``Vuebnb`` 的原型，它是本书的案例研究项目。 原型只是列表页面，在本章结尾处将如下所示：

.. figure:: ./images/2-1.png

   图2.1 Vuebnb原型

一旦我们在第3章“设置 ``Laravel`` 开发环境”中创建后端，并且第4章“使用 ``Laravel`` 构建 ``Web`` 服务”，我们将把这个原型移植到主项目中。

项目代码
--------
在开始之前，您需要通过从 ``GitHub`` 中克隆代码将代码库下载到您的计算机中。 指令在第1章中的代码库章节中给出， ``Hello Vue - Vue.js`` 简介。

文件夹 ``vuebnb-prototype`` 具有我们现在要构建的原型的项目代码。 进入该文件夹并列出内容：

.. code-block:: shell

    $ cd vuebnb-prototype
    $ ls -la

文件夹内容应如下所示：

.. figure:: ./images/2-2.png

   图2.2 vuebnb-prototype项目文件

.. tip:: 除非另有说明，否则本章中的所有其他终端命令将假定您位于 ``vuebnb-prototype`` 文件夹中。

NPM安装
-------
您现在需要安装此项目中使用的第三方脚本，包括 ``Vue.js`` 本身。 ``NPM`` ``install`` 方法将读取包含的 ``package.json`` 文件并下载所需的模块：

.. code-block:: shell

    $ npm install

您现在会看到一个新的 ``node_modules`` 目录出现在您的项目文件夹中。

Main文件
--------
在IDE中打开 ``vuebnb-prototype`` 目录。请注意，包含以下 ``index.html`` 文件。 它主要由样板代码组成，但也有一些结构标记包含在 ``body`` 标签中。

另请注意，此文件链接到将添加 ``CSS`` 规则的 ``style.css`` ，以及将添加 ``JavaScript`` 的 ``app.js`` 。

index.html

.. code-block:: html

    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="UTF-8">
      <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
      <meta name="viewport" content="width=device-width,initial-scale=1">
      <title>Vuebnb</title>
      <link href="node_modules/open-sans-all/css/open-sans.css" rel="stylesheet">
      <link rel="stylesheet" href="style.css" type="text/css">
    </head>
    <body>
    <div id="toolbar">
      <img class="icon" src="logo.png">
      <h1>vuebnb</h1>
    </div>
    <div id="app">
      <div class="container"></div>
    </div>
    <script src="app.js"></script>
    </body>
    </html>

当前 ``app.js`` 是一个空文件，但是我在 ``style.css`` 中包含了一些 ``CSS`` 规则来启动。

style.css

.. code-block:: css

    body {
      font-family: 'Open Sans', sans-serif;
      color: #484848;
      font-size: 17px;
      margin: 0;
    }

    .container {
      margin: 0 auto;
      padding: 0 12px;
    }

    @media (min-width: 744px) {
      .container {
          width: 696px;
      }
    }

    #toolbar {
      display: flex;
      align-items: center;
      border-bottom: 1px solid #e4e4e4;
      box-shadow: 0 1px 5px rgba(0, 0, 0, 0.1);
    }

    #toolbar .icon {
      height: 34px;
      padding: 16px 12px 16px 24px;
      display: inline-block;
    }

    #toolbar h1 {
      color: #4fc08d;
      display: inline-block;
      font-size: 28px;
      margin: 0;
    }

在浏览器打开
------------
要查看项目，请在 ``Web`` 浏览器中找到 ``index.html`` 文件。 在 ``Chrome`` 中，它就像 ``File|Open File`` 一样简单 。 加载时，除顶部的工具栏外，您会看到大部分为空的页面。

安装Vue.js
==========
现在是时候将 ``Vue.js`` 库添加到我们的项目中。 ``Vue`` 作为我们的 ``NPM`` 安装的一部分下载，所以现在我们可以简单地链接到带有 ``script`` 标签的 ``Vue.js`` 的浏览器版本。

index.html

.. code-block:: html

    <body>
    <div id="toolbar">...</div>
    <div id="app">...</div>
    <script src="node_modules/vue/dist/vue.js"></script>
    <script src="app.js"></script>
    </body>

.. tip:: 在脚本顺序运行时，我们在自定义的 ``app.js`` 脚本之前包含 ``Vue`` 库是非常重要的。

``Vue`` 现在将被注册为全局对象。 我们可以通过浏览器在 ``JavaScript`` 控制台中输入以下内容来测试它：

.. code-block:: shell

    console.log(Vue);

结果如下：

.. figure:: ./images/2-3.png

   图2.3 检查Vue被注册为全局对象

页面内容
========
随着我们的环境设置和启动代码的安装，我们现在准备好开始构建 ``Vuebnb`` 原型的第一步。

让我们在页面中添加一些内容，包括标题图片，标题和关于部分。 我们将在我们的 ``HTML`` 文件中添加结构，并使用 ``Vue.js`` 在我们需要的地方插入正确的内容。

Vue实例
-------
查看我们的 ``app.js`` 文件，现在让我们通过使用带 ``Vue`` 对象的 ``new`` 运算符来创建 ``Vue.js`` 的根实例。

app.js

.. code-block:: js

    var app = new Vue();

当你创建一个 ``Vue`` 实例时，你通常需要传入一个配置对象作为参数。 这个对象是你的项目的自定义数据和函数被定义的地方。

app.js

.. code-block:: js

    var app = new Vue({
      el: '#app'
    });

随着我们的项目的进行，我们将为这个配置对象增加更多，但现在我们刚刚添加了 ``el`` 属性，它告诉 ``Vue`` 将它自己挂载到页面中的位置。

您可以为其分配一个字符串（一个 ``CSS`` 选择器）或一个 ``HTML`` 节点对象。 在我们的例子中，我们使用了 ``#app`` 字符串，它是一个 ``CSS`` 选择器，指向 ``ID`` 为 ``app`` 的元素。

index.html

.. code-block:: html

    <div id="app">
      <!--Mount element-->
    </div>

``Vue`` 支配它所加载的元素和任何子节点。 对于我们迄今为止的项目， ``Vue`` 可以使用 ``header`` 类来操作 ``div`` ，但它不能用 ``ID`` 为 ``toolbar`` 来操作 ``div`` 。 任何放在该 ``div`` 里的东西对于 ``Vue`` 不可见。

index.html

.. code-block:: html

    <body>
    <div id="toolbar">...</div>
    <div id="app">
      <!--Vue only has dominion here-->
      <div class="header">...</header>
      ...
    </div>
    <script src="node_modules/vue/dist/vue.js"></script>
    <script src="app.js"></script>
    </body>

.. tip:: 从现在起，我们将把我们的 ``Mount`` 节点及其子节点作为我们的模板。

数据绑定
--------
``Vue`` 的一个简单任务是将一些 ``JavaScript`` 数据绑定到模板。 让我们在配置对象中创建一个 ``data`` 属性，并为其分配一个对象，该对象包含 ``title:'My apartment'`` 属性。

app.js

.. code-block:: js

    var app = new Vue({
      el: '#app',
      data: {
        title: 'My apartment'
      }
    });

此 ``data`` 对象的任何属性都将在我们的模板中可用。 为了告诉 ``Vue`` 在哪里绑定这些数据，我们可以使用 ``mustache`` 语法，也就是双括号，例如 ``{{myProperty}}`` 。 当 ``Vue`` 实例化时，它会编译模板，用适当的文本替换 ``mustache`` 语法，并更新 ``DOM`` 以反映这一点。 这个过程被称为 **文本插值** ，并在下面的代码块中进行演示。

index.html

.. code-block:: html

    <div id="app">
      <div class="container">
        <div class="heading">
          <h1>{{ title }}</h1>
        </div>
      </div>
    </div>

将会被渲染为：

.. code-block:: html

    <div id="app">
      <div class="container">
        <div class="heading">
          <h1>My apartment</h1>
        </div>
      </div>
    </div>

让我们现在添加更多的数据属性，并增强我们的模板以包含更多的页面结构。

app.js

.. code-block:: js

    var app = new Vue({
      el: '#app',
      data: {
        title: 'My apartment',
        address: '12 My Street, My City, My Country',
        about: 'This is a description of my apartment.'
      }
    });

index.html

.. code-block:: html

    <div class="container">
      <div class="heading">
        <h1>{{ title }}</h1>
        <p>{{ address }}</p>
      </div>
      <hr>
      <div class="about">
        <h3>About this listing</h3>
        <p>{{ about }}</p>
      </div>
    </div>

我们还要添加一些新的 ``CSS`` 规则。

style.css

.. code-block:: css

    .heading {
      margin-bottom: 2em;
    }

    .heading h1 {
      font-size: 32px;
      font-weight: 700;
    }

    .heading p {
      font-size: 15px;
      color: #767676;
    }

    hr {
      border: 0;
      border-top: 1px solid #dce0e0;
    }

    .about {
      margin-top: 2em;
    }

    .about h3 {
      font-size: 22px;
    }

    .about p {
      white-space: pre-wrap;
    }

如果您现在保存并刷新页面，它应该如下所示：

.. figure:: ./images/2-4.png

   图2.4 包含基本数据绑定的列表页面

模拟列表样本
------------
在开发过程中，使用一些模拟数据很好，这样我们就可以看到完成的页面的外观。 出于这个原因，我在项目中包含了 ``sample/data.js`` 。 让我们在我们的文档中加载它，确保它在我们的 ``app.js``文件前面被加载。

index.html

.. code-block:: html

    <body>
    <div id="toolbar">...</div>
    <div id="app">...</div>
    <script src="node_modules/vue/dist/vue.js"></script>
    <script src="sample/data.js"></script>
    <script src="app.js"></script>
    </body>

看看这个文件，你会发现它声明了一个 ``sample`` 对象。 我们现在将在我们的数据配置中使用它。

app.js

.. code-block:: js

    data: {
      title: sample.title,
      address: sample.address,
      about: sample.about
    }

一旦保存并刷新，您将在页面上看到更真实的数据：

.. figure:: ./images/2-5.png

   图2.5 包括模拟列表示例的页面

.. tip:: 以这种方式使用全局变量分割不同的脚本文件不是理想的做法。 尽管如此，我们只会在原型中执行此操作，稍后我们将从服务器中获取此模拟列表样本。


页眉图像
========
如果没有大的光泽图像来显示它，房间清单将是不完整的。 我们现在将包含模拟列表中的标题图像。 将此标记添加到页面。

index.html

.. code-block:: html

    <div id="app">
      <div class="header">
        <div class="header-img"></div>
      </div>
      <div class="container">...</div>
    </div>

style.css文件如下：

.. code-block:: css

    .header {
      height: 320px;
    }

    .header .header-img {
      background-repeat: no-repeat;
      background-size: cover;
      background-position: 50% 50%;
      background-color: #f5f5f5;
      height: 100%;
    }

您可能想知道为什么我们使用 ``div`` 而不是 ``img`` 标签。 为了帮助定位，我们将使用 ``header-img`` 类将我们的图像设置为 ``div`` 的背景。

样式绑定一
----------
要设置背景图片，我们必须将 ``URL`` 作为 ``CSS`` 规则中的属性提供，如下所示：

.. code-block:: css

    .header .header-img {
      background-image: url(...);
    }

显然，我们的标题图像应该针对每个单独的列表，因此我们不想对此 ``CSS`` 规则进行硬编码。 相反，我们可以让 ``Vue`` 将来自数据的 ``URL`` 绑定到我们的模板。

``Vue`` 无法访问我们的 ``CSS`` 样式表，但它可以绑定到内联样式属性：

.. code-block:: html

    <div class="header-img" style="background-image: url(...);"></div>

您可能会认为使用文本插值是这里的解决方案，例如：

.. code-block:: html

    <div class="header-img" style="background-image: {{ headerUrl }}"></div>

但是这不是有效的 ``Vue.js`` 语法。 相反，这是另一个 ``Vue.js`` 中称为指令的功能。 让我们首先探索指令，然后再回来解决这个问题。

指令
----
``Vue`` 的指令是带 ``v-`` 前缀的特殊 ``HTML`` 属性，例如 ``v-if`` ，它提供了一种简单的方法来为我们的模板添加功能。 可以添加到元素的指令的一些示例如下：

- v-if：有条件地呈现元素；
- v-for：根据数组或对象多次渲染元素；
- v-bind：将元素的属性动态绑定到 ``JavaScript`` 表达式；
- v-on：将一个事件监听器关联到该元素；

我们将在整本书中探讨更多内容。

使用
^^^^
就像普通的 ``HTML`` 属性一样，指令通常是名称/值对，形式为 ``name ="value"`` 。 要使用指令，只需将其添加到 ``HTML`` 标记中，就像属性一样，例如：

.. code-block:: html

    <p v-directive="value">

表达式
^^^^^^
如果一个指令需要一个值，它可以是一个表达式。

在 ``JavaScript`` 语言中，表达式是小型的，可计算的语句，可以生成单个值。 表达式可用于任何需要值的地方，例如在 ``if`` 语句的括号中：

.. code-block:: js

    if (expression) {
      ...
    }

表达可以是以下任何一种：

- 一个数学表达式，例如 ``x + 7`` ；
- 比较，例如 ``v <= 7`` ；
- ``Vue data``属性，例如 ``this.myval`` ；

指令和文本插值都接受表达式值：

.. code-block:: html

    <div v-dir="someExpression">{{ firstName + " " + lastName }}</div>

例子：v-if
^^^^^^^^^^
如果它的值是真值表达式， ``v-if`` 将有条件地渲染一个元素。 在以下情况下， ``v-if`` 将根据 ``myval`` 值移除/插入 ``p`` 元素：

.. code-block:: html

    <div id="app">
      <p v-if="myval">Hello Vue</p>
    </div>
    <script>
      var app = new Vue({
        el: '#app',
        data: {
          myval: true
        }
      });
    </script>

将会被渲染为：

.. code-block:: html

    <div id="app">
      <p>Hello Vue</p>
    </div>

如果我们在一个连续的元素中添加 ``v-else`` 指令（一个无值的特殊指令），它会随着 ``myval`` 的改变而被对称地删除/插入：

.. code-block:: html

    <p v-if="myval">Hello Vue</p>
    <p v-else>Goodbye Vue</p>

参数
^^^^
某些指令接收参数，在指令名称后面用冒号表示。 例如，侦听 ``DOM`` 事件的 ``v-on`` 指令需要一个参数来指定应该侦听哪个事件：

.. code-block:: html

    <a v-on:click="doSomething">

除了 ``click`` ，参数可以是 ``mouseenter`` ， ``keypress`` ， ``scroll`` （包括自定义事件）。

样式绑定二
----------
回到我们的页眉图像，我们可以使用带有 ``style`` 参数的 ``v-bind`` 指令将一个值绑定到 ``style`` 属性。

index.html

.. code-block:: html

    <div class="header-img" v-bind:style="headerImageStyle"></div>

``headerImageStyle`` 是一个表达式，用于计算将 ``CSS`` 背景图像设置为正确 ``URL`` 的 ``CSS`` 规则。 这听起来很混乱，但当你看到它的工作时，将会很清楚。

现在让我们创建 ``headerImageStyle`` 作为数据属性。 绑定到样式属性时，可以使用属性和值等效于 ``CSS`` 属性和值的对象。

app.js

.. code-block:: js

    data: {
      ...
      headerImageStyle: {
        'background-image': 'url(sample/header.jpg)'
      }
    },

保存代码，刷新页面，将会显示标题图片：

.. figure:: ./images/2-6.png

   图2.6 包含页眉图像的页面

用您的浏览器开发工具检查页面，注意 ``v-bind`` 指令如何计算的：

.. code-block:: html

    <div class="header-img" style="background-image: url('sample/header.jpg');"></div>

列表部分
========
我们将添加到页面的下一部分内容是设施和价格列表：

.. figure:: ./images/2-7.png

   图2.7 列表部分

如果您查看模拟列表示例，您会看到对象上的设施和价格属性都是数组。

sample/data.js:

.. code-block:: js

    var sample = {
      title: '...',
      address: '...',
      about: '...',
      amenities: [
        {
          title: 'Wireless Internet',
          icon: 'fa-wifi'
        },
        {
          title: 'Pets Allowed',
          icon: 'fa-paw'
        },
        ...
      ],
      prices: [
        {
          title: 'Per night',
          value: '$89'
        },
        {
          title: 'Extra people',
          value: 'No charge'
        },
        ...
      ]
    }

如果我们能够遍历这些数组并将每个项目打印到页面，这不是一件容易的事吗？ 我们可以！ 这是 ``v-for`` 指令的作用。

首先，我们将这些作为数据属性添加到我们的根实例。

app.js:

.. code-block:: js

    data: {
      ...
      amenities: sample.amenities,
      prices: sample.prices
    }

渲染列表
--------
``v-for`` 指令需要 ``item in items`` 形式的特殊表达式，其中 ``items`` 是源数组， ``item`` 是当前数组元素被循环使用的别名。

让我们首先处理 ``amenities`` 数组。 此数组的每个成员都是具有 ``title`` 和 ``icon`` 属性的对象，即：

.. code-block:: js

    { title: 'something', icon: 'something' }

我们将 ``v-for`` 指令添加到模板中，我们分配给它的表达式为 ``amenity in amenities`` 。表达式的别名部分即 ``amenity`` 将在整个循环序列中引用数组中的每个对象，从第一个开始。

index.html:

.. code-block:: html

    <div class="container">
      <div class="heading">...</div>
      <hr>
      <div class="about">...</div>
      <div class="lists">
        <div v-for="amenity in amenities">{{ amenity.title }}</div>
      </div>
    </div>

它将被渲染为：

.. code-block:: html

    <div class="container">
      <div class="heading">...</div>
      <hr>
      <div class="about">...</div>
      <div class="lists">
        <div>Wireless Internet</div>
        <div>Pets Allowed</div>
        <div>TV</div>
        <div>Kitchen</div>
        <div>Breakfast</div>
        <div>Laptop friendly workspace</div>
      </div>
    </div>

Icons
-----
我们 ``amenity`` 对象的第二个属性是图标。 这实际上是一个与 ``Font Awesome`` 图标字体中的图标相关的类。 我们已经将 ``Font Awesome`` 安装为 ``NPM`` 模块，因此将其添加到页面的头部以便现在使用它。

index.html:

.. code-block:: html

    <head>
      ...
      <link rel="stylesheet" href="node_modules/open-sans-all/css/open-sans.css">
      <link rel="stylesheet" href="node_modules/font-awesome/css/font-awesome.css">
      <link rel="stylesheet" href="style.css" type="text/css">
    </head>

现在我们可以在模板中完成我们的 ``amenities`` 部分的结构。

index.html

.. code-block:: html

    <div class="lists">
      <hr>
      <div class="amenities list">
        <div class="title"><strong>Amenities</strong></div>
        <div class="content">
          <div class="list-item" v-for="amenity in amenities">
            <i class="fa fa-lg" v-bind:class="amenity.icon"></i>
            <span>{{ amenity.title }}</span>
          </div>
        </div>
      </div>
    </div>

style.css:

.. code-block:: css

    .list {
      display: flex;
      flex-wrap: nowrap;
      margin: 2em 0;
    }

    .list .title {
      flex: 1 1 25%;
    }

    .list .content {
      flex: 1 1 75%;
      display: flex;
      flex-wrap: wrap;
    }

    .list .list-item {
      flex: 0 0 50%;
      margin-bottom: 16px;
    }

    .list .list-item > i {
      width: 35px;
    }

    @media (max-width: 743px) {
      .list .title {
        flex: 1 1 33%;
      }

      .list .content {
        flex: 1 1 67%;
      }

      .list .list-item {
        flex: 0 0 100%;
      }
    }

Key
---
正如您所预料的那样，由 ``v-for="amenity in amenities"`` 生成的 ``DOM`` 节点被反应性绑定到 ``amenities`` 数组。如果 ``amenities"`` 内容改变， ``Vue`` 会自动重新渲染节点以反映变化。

当使用 ``v-for`` 时，建议您为列表中的每个项目提供一个唯一的 ``key`` 属性。 这使 ``Vue`` 可以针对需要更改的确切 ``DOM`` 节点，从而使 ``DOM`` 更新更加高效。

通常， ``key`` 将是数字 ``ID`` ，例如：

.. code-block:: html

    <div v-for="item in items" v-bind:key="item.id">
      {{ item.title }}
    </div>

对于 ``amenities`` 和价格清单，内容在应用程序的整个生命周期内不会改变，因此我们不需要提供 ``key`` 。 有些波浪线可能会警告您，但在这种情况下，警告可以安全地忽略。

Prices
------
现在让我们将价目表添加到我们的模板中。

index.html:

.. code-block:: html

    <div class="lists">
      <hr>
      <div class="amenities list">...</div>
      <hr>
      <div class="prices list">
        <div class="title">
          <strong>Prices</strong>
        </div>
        <div class="content">
          <div class="list-item" v-for="price in prices">
            {{ price.title }}: <strong>{{ price.value }}</strong>
          </div>
        </div>
      </div>
    </div>

我相信你会同意循环模板比写出每个项目要容易得多。 但是，您可能会注意到这两个列表之间仍然存在一些共同的标记。 在本书后面，我们将利用组件使模板的这部分更加模块化。

显示更多功能
============
在列表部分位于关于部分之后，我们已经遇到了问题。“关于”部分具有任意长度，在我们添加的一些模拟列表中，您会看到此部分很长。

我们不希望它主宰页面并强制用户做很多不受欢迎的滚动查看列表部分，所以我们需要一种方法来隐藏一些文本，如果它太长，但如果他们选择，允许用户查看全文。

让我们添加一个显示 **更多** ``UI`` 功能，该功能将在特定长度后剪裁关于文本，并为用户提供一个按钮来显示隐藏文本：

.. figure:: ./images/2-8.png

   图2.8 显示更多功能

我们将首先向包含 ``about`` 文本插值的 ``p`` 标签添加一个 ``contracted`` 类。 该类的 ``CSS`` 规则将其高度限制为 ``250`` 像素，并隐藏任何溢出元素的文本。

index.html:

.. code-block:: html

    <div class="about">
      <h3>About this listing</h3>
      <p class="contracted">{{ about }}</p>
    </div>

style.css:

.. code-block:: css

    .about p.contracted {
      height: 250px;
      overflow: hidden;
    }

我们还会在 ``p`` 标签后面放一个按钮，用户可以点击将该部分展开为全高。

index.html:

.. code-block:: html

    <div class="about">
      <h3>About this listing</h3>
      <p class="contracted">{{ about }}</p>
      <button class="more">+ More</button>
    </div>

这是需要的 ``CSS`` ，包括一个通用按钮规则，它将为我们在整个项目中添加的所有按钮提供基础样式。

style.css:

.. code-block:: css

    button {
      text-align: center;
      vertical-align: middle;
      user-select: none;
      white-space: nowrap;
      cursor: pointer;
      display: inline-block;
      margin-bottom: 0;
    }

    .about button.more {
      background: transparent;
      border: 0;
      color: #008489;
      padding: 0;
      font-size: 17px;
      font-weight: bold;
    }

    .about button.more:hover,
    .about button.more:focus,
    .about button.more:active {
      text-decoration: underline;
      outline: none;
    }

要做到这一点，我们需要一种方法来在用户单击“更多”按钮时删除 ``contracted`` 类。 似乎很好的指示！

类绑定
-------
我们如何处理这个问题就是动态绑定 ``contracted`` 类。 让我们创建一个 ``contracted`` 的数据属性并将其初始值设置为 ``true`` 。

app.js:

.. code-block:: js

    data: {
      ...
      contracted: true
    }

就像我们的样式绑定一样，我们可以将这个类绑定到一个对象。 在表达式中， ``contracted`` 属性是要绑定的类的名称， ``contracted`` 值是对同名的数据属性的引用，该属性是布尔值。 因此，如果 ``contracted`` 数据属性的计算结果为 ``true`` ，那么该类将被绑定到元素，如果计算结果为 ``false`` ，则不会。

index.html:

.. code-block:: html

    <p v-bind:class="{ contracted: contracted }">{{ about }}</p>

接下来，当页面加载 ``contracted`` 类时被绑定：

.. code-block:: html

    <p class="contracted">...</p>

事件监听
--------
现在我们想要在用户单击“更多”按钮时自动删除 ``contracted`` 类。 为了完成这项工作，我们将使用 ``v-on`` 指令，该指令用 ``click`` 参数监听 ``DOM`` 事件。

``v-on`` 指令的值可以是一个表达式，它将 ``contracted`` 设置为 ``false`` 。

index.html:

.. code-block:: html

    <div class="about">
      <h3>About this listing</h3>
      <p v-bind:class="{ contracted: contracted }">{{ about }}</p>
      <button class="more" v-on:click="contracted = false">+ More</button>
    </div>

反应
----
当我们点击更多按钮时， ``contracted`` 值会改变， ``Vue`` 会立即更新页面以反映这一变化。

``Vue`` 如何知道这样做？ 为了回答这个问题，我们必须首先理解 ``getter`` 和 ``setter`` 的概念。

Getters和setters
^^^^^^^^^^^^^^^^
为 ``JavaScript`` 对象的属性赋值非常简单：

.. code-block:: js

    var myObj = {
      prop: 'Hello'
    }

检索它很简单：

.. code-block:: js

    myObj.prop

这里没有任何窍门。 我想要说明的一点是，我们可以通过使用 ``getter`` 和 ``setter`` 来替换这个对象的正常赋值/检索机制。 这些是特殊的功能，允许自定义逻辑获取或设置属性的值。

当一个属性的值由另一个属性确定时， ``Getter`` 和 ``Setter`` 特别有用。 这是是一个例子：

.. code-block:: js

    var person = {
      firstName: 'Abraham',
      lastName: 'Lincoln',
      get fullName() {
        return this.firstName + ' ' + this.lastName;
      },
      set fullName(name) {
        var words = name.toString().split(' ');
        this.firstName = words[0] || '';
        this.lastName = words[1] || '';
      }
    }

每当我们尝试正常赋值/检索其值时，就会调用 ``fullName`` 属性的 ``get`` 和 ``set`` 函数：

.. code-block:: js

    console.log(person.fullName); // Abraham Lincoln
    person.fullName = 'George Washington';
    console.log(person.firstName); // George
    console.log(person.lastName) // Washington

反应性数据属性
^^^^^^^^^^^^^^
``Vue`` 的另一个初始化步骤是遍历所有的数据属性并为它们分配 ``getter`` 和 ``setter`` 。 如果你看看下面的截图，你可以看到我们当前应用程序中的每个属性如何添加一个get和set函数：

.. figure:: ./images/2-9.png

   图2.9 Getters 和 setters

``Vue`` 添加了这些 ``Getters`` 和 ``setters`` ，使其能够在访问或修改属性时执行依赖关系跟踪和更改通知。 所以，当点击事件改变 ``contracted`` 值时，它的设置方法被触发。  ``set`` 方法会设置新的值，但也会执行一个辅助任务，告诉 ``Vue`` 一个值已经改变，依赖它的页面的任何部分都可能需要重新渲染。

.. tip:: 如果您想了解更多关于 ``Vue`` 的反应系统的信息，请查看文章中的 ``Reactivity In Vue.js`` （及其陷阱）: https://vuejsdevelopers.com/2017/03/05/vue-js-reactivity/

隐藏More按钮
------------
一旦 ``About`` 部分被展开，我们想隐藏 ``More`` 按钮，因为它不再需要。 我们可以使用 ``v-if`` 指令与 ``contracted`` 属性一起实现此目的。

index.html:

.. code-block:: html

    <button v-if="contracted" class="more" v-on:click="contracted = false">
      + More
    </button>

图像模态窗口
============
为了防止我们的页眉图像占据页面，我们裁剪了它并限制了它的高度。 但是如果用户想要看到全部的图像呢？ 一个伟大的允许用户关注单个内容项目的 ``UI`` 设计模式是一个模式窗口。

以下是我们的模式在打开时的样子：

.. figure:: ./images/2-10.png

   图2.10 页眉图片模式

我们的模式将给出标题图像的正确缩放视图，以便用户可以专注于住宿的外观，而不会让页面的其他部分分心。

在本书的后面，我们会将图像传送带插入到模式，以便用户浏览整个房间图像集合！

不过，现在，这里是我们的模态所需的功能：

- 点击标题图像打开模式；
- 冻结主窗口；
- 显示图像；
- 用关闭按钮或退出键关闭模式窗口；

Opening
-------
首先，我们添加一个布尔数据属性，它将表示我们模态的打开或关闭状态。 我们将它初始化为 ``false`` 。

app.js:

.. code-block:: js

    data: {
      ...
      modalOpen: false
    }

我们将这样做，以便点击我们的标题图像将设置模式打开。 我们还会在标题图片的左下角覆盖一个标记为“查看照片”的按钮，以向用户发出一个更强的信号，即他们应该点击以显示图像。

index.html:

.. code-block:: html

    <div
      class="header-img"
      v-bind:style="headerImageStyle"
      v-on:click="modalOpen = true"
    >
      <button class="view-photos">View Photos</button>
    </div>

请注意，通过点击放在包装 ``div`` 上侦听器，无论用户是否因点击按钮或 ``div`` ，都会捕获点击事件。

我们将在头部图像中添加更多的 ``CSS`` ，使光标成为一个指针，让用户知道头部可以被点击，并给头部一个相对位置，这样按钮就可以定位在它的内部。 我们还会添加规则来设置按钮的样式。

style.css:

.. code-block:: css

    .header .header-img {
      ...
      cursor: pointer;
      position: relative;
    }

    button {
      border-radius: 4px;
      border: 1px solid #c4c4c4;
      text-align: center;
      vertical-align: middle;
      font-weight: bold;
      line-height: 1.43;
      user-select: none;
      white-space: nowrap;
      cursor: pointer;
      background: white;
      color: #484848;
      padding: 7px 18px;
      font-size: 14px;
      display: inline-block;
      margin-bottom: 0;
    }

    .header .header-img .view-photos {
      position: absolute;
      bottom: 20px;
      left: 20px;
    }

现在让我们为我们的模态添加标记。 我已经将它放在页面中的其他元素之后，尽管它并不重要，因为模式将不在文档的常规流程中。 通过在下面的 ``CSS`` 中给它一个 ``fixed`` 的位置，我们将它从流中移除。

index.html:

.. code-block:: html

    <div id="app">
      <div class="header">...</div>
      <div class="container">...</div>
      <div id="modal" v-bind:class="{ show : modalOpen }"></div>
    </div>

主模态 ``div`` 将作为其余模态内容的容器，但也可作为将覆盖主窗口内容的背景面板。 为了达到这个目的，我们使用 ``CSS`` 规则来扩展它，使其顶部，右侧，底部和左侧值为 ``0`` ，以完全覆盖视口。我们将 ``z-index`` 设置为较高的数字，以确保模式被堆叠 页面中任何其他元素的前面。

还要注意 ``display`` 最初设置为 ``none`` ，但是我们将一个类名为 ``show`` 动态绑定到模式，使其显示块。 当然，这个类的增加/删除将被绑定到 ``modalOpen`` 的值。

style.css:

.. code-block:: css

    #modal {
      display: none;
      position: fixed;
      top: 0;
      right: 0;
      bottom: 0;
      left: 0;
      z-index: 2000;
    }

    #modal.show {
      display: block;
    }

Window
------
现在让我们为将覆盖在我们的背景面板上的窗口添加标记。 该窗口将具有宽度约束，并将在视口中居中。

index.html:

.. code-block:: html

    <div id="modal" v-bind:class="{ show : modalOpen }">
      <div class="modal-content">
        <img src="sample/header.jpg"/>
      </div>
    </div>

style.css:

.. code-block:: css

    .modal-content {
      height: 100%;
      max-width: 105vh;
      padding-top: 12vh;
      margin: 0 auto;
      position: relative;
    }

    .modal-content img {
      max-width: 100%;
    }

关闭主窗口
----------
当模式打开时，我们希望阻止与主窗口的任何交互，并且明确区分主窗口和子窗口。 我们可以t通过如下做到这一点：

- 调暗主窗口；
- 防止滚动主体；

调暗主窗口
^^^^^^^^^^
当模式打开时，我们可以简单地隐藏主窗口，但如果用户仍然可以知道它们在应用程序的流程中，那么会更好。 为了达到这个目的，我们将把主窗口变暗成半透明面板。

我们可以通过给我们的模式面板一个不透明的黑色背景做到这一点。

style.css:

.. code-block:: css

    #modal {
      ...
      background-color: rgba(0,0,0,0.85);
    }

防止滚动主体
^^^^^^^^^^^^
不过，我们有一个问题。 尽管是全屏显示，我们的模式面板仍然是身体标签的小孩。 这意味着我们仍然可以滚动主窗口！ 我们不希望用户在模式打开时以任何方式与主窗口进行交互，因此我们必须禁用主体上的滚动。

诀窍是将 ``CSS`` ``overflow`` 属性添加到 ``body`` 标签并将其设置为 ``hidden`` 。 这具有剪切任何溢出（即，当前不在视图中的页面的一部分）的效果，并且其余内容将变得不可见。

我们需要动态地添加和删除这个 ``CSS`` 规则，因为我们显然希望能够在模式关闭时滚动页面。 所以，让我们创建一个名为 ``modal-open`` 的类，当模式打开时，我们可以将其应用于 ``body`` 标签。

style.css:

.. code-block:: css

    body.modal-open {
      overflow: hidden;
      position: fixed;
    }

我们可以使用 ``v-bind:class`` 来添加/删除这个类，对吧？ 很不幸的是，不行。 请记住， ``Vue`` 只支配它挂载的元素：

.. code-block:: html

    <body>
      <div id="app">
        <!--This is where Vue has dominion and can modify the page freely-->
      </div>
      <!--Vue is unable to change this part of the page or any ancestors-->
    </body>

如果我们为 ``body`` 标签添加一个指令，它将不会被 ``Vue`` 看到。

Vue的挂载元素
"""""""""""""
如果我们只是将 ``Vue`` 安装在 ``body`` 标签上，会不会解决我们的问题？ 例如：

.. code-block:: js

    new Vue({
      el: 'body'
    });

这是 ``Vue`` 不允许的，如果你尝试它，你会得到这个错误：不要将 ``Vue`` 挂载到 ``<html>`` 或 ``<body>`` - 而是挂载到普通元素。

请记住， ``Vue`` 必须编译模板并替换装载节点。 如果 ``script`` 标记是挂载节点的子节点，就像你经常使用 ``body`` 一样，或者如果你的用户有浏览器插件修改文档的（很多都是），那么当它替换那个节点时，各种各样的问题可能会在页面上出现。

如果您使用 ``ID`` 定义唯一的根元素，则不应出现此类冲突。

监视器
^^^^^^
那么，如果超出了 ``Vue`` 的统治范围，我们怎么能从 ``body`` 中添加/移除类？ 我们必须用浏览器的 ``Web API`` 以旧式的方式来完成它。 当模式打开或关闭时，我们需要运行以下语句：

.. code-block:: js

    // Modal opens
    document.body.classList.add('modal-open');

    // Modal closes
    document.body.classList.remove('modal-closed');

正如所讨论的， ``Vue`` 为每个数据属性添加了反应式 ``getter`` 和 ``setter`` ，这样当数据发生变化时，它就会知道要适当地更新 ``DOM`` 。 ``Vue`` 还允许您编写自定义逻辑，通过名为监视器的功能将其挂钩到反应式数据更改中。

要添加观察者，首先将 ``watch`` 属性添加到您的 ``Vue`` 实例。 为每个属性指定一个对象，其中每个属性都具有声明的数据属性的名称，并且每个值都是一个函数。 该函数有两个参数： **旧值和新值** 。

只要数据属性发生变化， ``Vue`` 就会触发任何已声明的观察者方法：

.. code-block:: js

    var app = new Vue({
      el: '#app'
      data: {
        message: 'Hello world'
      },
      watch: {
        message: function(newVal, oldVal) {
          console.log(oldVal, ', ', newVal);
        }
      }
    });

    setTimeout(function() {
      app.message = 'Goodbye world';
      // Output: "Hello world, Goodbye world";
    }, 2000);

``Vue`` 无法为我们更新 ``body`` 标签，但它可以触发自定义逻辑。 当我们的模式被打开和关闭时，让我们使用观察者来更新 ``body`` 标签。

app.js:

.. code-block:: js

    var app = new Vue({
      data: { ... },
      watch: {
        modalOpen: function() {
          var className = 'modal-open';
          if (this.modalOpen) {
            document.body.classList.add(className);
          } else {
            document.body.classList.remove(className);
          }
        }
      }
    });

现在，当您尝试滚动页面时，您会看到它不会改变！

Closing
-------
用户需要一种方法来关闭他们的模式并返回到主窗口。 我们将在右上角覆盖一个按钮，点击后，表达式将 ``modalOpen`` 设置为 ``false`` 。 我们的包装 ``div`` 上的 ``show`` 类将相应地被删除，这意味着 ``display CSS`` 属性将返回到 ``none`` ，从而从页面中删除模态。

index.html:

.. code-block:: html

    <div id="modal" v-bind:class="{ show : modalOpen }">
      <button v-on:click="modalOpen = false" class="modal-close">
        &times;
      </button>
      <div class="modal-content">
        <img src="sample/header.jpg"/>
      </div>
    </div>

style.css:

.. code-block:: css

    .modal-close {
      position: absolute;
      right: 0;
      top: 0;
      padding: 0px 28px 8px;
      font-size: 4em;
      width: auto;
      height: auto;
      background: transparent;
      border: 0;
      outline: none;
      color: #ffffff;
      z-index: 1000;
      font-weight: 100;
      line-height: 1;
    }

转义键
^^^^^^
为我们的模式设置一个关闭按钮非常方便，但大多数人关闭窗口的本能动作都是 ``Escape`` 键。

``v-on`` 是 ``Vue`` 倾听事件的机制，似乎是这份工作的一个很好的候选人。 在此输入获取焦点时按下任意键后，添加 ``keyup`` 参数将触发处理程序回调：

.. code-block:: html

    <input v-on:keyup="handler">

事件修饰符
^^^^^^^^^^
通过为 ``v-on`` 指令提供修饰符， ``Vue`` 可以轻松地侦听特定的键。 修饰符是用点（.）表示的后缀，例如：

.. code-block:: html

    <input v-on:keyup.enter="handler">

正如您可能猜到的那样， ``.enter`` 修饰符告诉 ``Vue`` 只有在被 ``Enter`` 键事件触发时才调用处理程序。 修饰符使您不必记住特定的 ``key`` 代码，也使您的模板逻辑更加明显。 ``Vue`` 提供了其他各种 ``key`` 修饰符，包括：

- tab；
- delete；
- space；
- esc；

考虑到这一点，似乎我们可以用这个指令关闭我们的模态：

.. code-block:: html

    v-on:keyup.esc="modalOpen = false"

但是，我们关联这个指令的标签是什么？ 不幸的是，除非获取输入焦点，否则 ``key`` 事件将从 ``body`` 元素中发出，正如我们所知，它不在 ``Vue`` 的管辖范围之内！

为了处理这个事件，我们再次诉诸于 ``Web API`` 。

app.js:

.. code-block:: js

    var app = new Vue({
      ...
    });

    document.addEventListener('keyup', function(evt) {
      if (evt.keyCode === 27 && app.modalOpen) {
        app.modalOpen = false;
      }
    });

这有效，但有一个警告（在下一节讨论）。 但 ``Vue`` 可以帮助我们更完善。

生命周期挂钩
^^^^^^^^^^^^
当你的主脚本运行并且你的 ``Vue`` 实例被设置时，它会经过一系列的初始化步骤。 正如我们前面所说的， ``Vue`` 将遍历数据对象并使其成为反应式对象，并编译模板并挂载到 ``DOM`` 。 在生命周期的后期， ``Vue`` 还将进行更新步骤，以后还会进行拆解步骤。

以下是从 http://vuejs.org 获取的生命周期实例的图表。 这些步骤中的许多步骤都涉及我们尚未涉及的概念，但您应该明白：

.. figure:: ./images/2-11.png

    图2.11 Vue.js生命周期图

``Vue`` 允许您通过生命周期钩子在这些不同的步骤执行自定义逻辑，生命周期钩子是配置对象中定义的回调。

例如，我们在这里使用 ``beforeCreate`` 和 ``created`` 的钩子：

.. code-block:: js

    new Vue({
      data: {
        message: 'Hello'
      },
      beforeCreate: function() {
        console.log('beforeCreate message: ' + this.message);
        // "beforeCreate message: undefined"
      },
      created: function() {
        console.log('created: '+ this.message);
        // "created message: Hello"
      },
    });

在调用 ``beforeCreate`` 钩子之后，但在调用创建的钩子之前， ``Vue`` 的 ``data`` 属性还未定义到上下文对象，因此在前者中未定义 ``this.message`` 。

我之前提到的有关 ``Escape`` 键监听器的警告是：虽然不太可能，但如果按 ``Escape`` 键并且在 ``Vue`` 实例对象 ``app`` 变量被赋值之前调用了回调，则 ``app.modalOpen`` 将是未定义的，而不是 ``true`` ，因此我们的 ``if`` 语句会 不像我们预期的那样控制流程。

为了克服这个问题，我们可以在创建的生命周期钩子中设置监听器，它将在 ``Vue`` 实例对象 ``app`` 变量被赋值之后调用。 这给了我们一个保证， ``modalOpen`` 将在回调运行时定义。

app.js:

.. code-block:: js

    function escapeKeyListener(evt) {
      if (evt.keyCode === 27 && app.modalOpen) {
        app.modalOpen = false;
      }
    }

    var app = new Vue({
      data: { ... },
      watch: { ... },
      created: function() {
        document.addEventListener('keyup', escapeKeyListener);
      }
    });

方法
^^^^
``Vue`` 配置对象也有一个方法部分。 方法不是反应性的，所以你可以在 ``Vue`` 配置之外定义它们而没有任何功能上的差异，但是 ``Vue`` 方法的优点是它们在上下文中传递了 ``Vue`` 实例，因此可以方便地访问其他属性和方法。

让我们将我们的 ``escapeKeyListener`` 重构为 ``Vue`` 实例方法。

app.js:

.. code-block:: js

    var app = new Vue({
      data: { ... },
      methods: {
        escapeKeyListener: function(evt) {
          if (evt.keyCode === 27 && this.modalOpen) {
            this.modalOpen = false;
          }
        }
      },
      watch: { ... },
      created: function() {
        document.addEventListener('keyup', this.escapeKeyListener);
      }
    });

代理属性
""""""""
您可能已经注意到我们的 ``escapeKeyListener`` 方法可以引用 ``this.modalOpen`` 。 它不应该是 ``this.data.modalOpen`` ？

当 ``Vue`` 实例被构造时，它将任何数据属性，方法和计算属性代理到实例对象。 这意味着从任何方法中，您都可以引用 ``this.myDataProperty`` ， ``this.myMethod`` 等，而不是 ``this.data.myDataProperty`` 或 ``this.methods.myMethod`` ，如您所想的那样：

.. code-block:: js

    var app = new Vue({
      data: {
        myDataProperty: 'Hello'
      },
      methods: {
        myMethod: function() {
          return this.myDataProperty + ' World';
        }
      }
    });

    console.log(app.myMethod());
    // Output: 'Hello World'

您可以通过在浏览器控制台中打印 ``Vue`` 对象来查看这些代理属性：

.. figure:: ./images/2-12.png

   图2.12 app的Vue实例

现在文本插值的简单性可能更有意义，它们具有 ``Vue`` 实例的上下文，并且由于代理属性，可以像 ``{{myDataProperty}}`` 一样引用。

但是，尽管代理到根可以使语法变得简洁，但结果是您无法命名数据属性，方法或具有相同名称的计算属性！

删除监听器
""""""""""
为了避免任何内存泄漏，我们还应该使用 ``removeEventListener`` 在 ``Vue`` 实例被拆除时摆脱监听器。 我们可以使用 ``destroy`` 钩子并为此调用我们的 ``escapeKeyListener`` 方法。

app.js:

.. code-block:: js

    new Vue({
      data: { ... },
      methods: { ... },
      watch: { ... },
      created: function() { ... },
      destroyed: function () {
        document.removeEventListener('keyup', this.escapeKeyListener);
      }
    });

总结
====
在本章中，我们熟悉了 ``Vue`` 的基本特征，包括安装和基本配置，数据绑定，文本插值，指令，方法，观察者和生命周期钩子。 我们还了解了 ``Vue`` 的内部运作，包括反应系统。

然后，我们利用这些知识设置了一个基本的 ``Vue`` 项目，并为 ``Vuebnb`` 原型创建了包含文本，信息列表，标题图像和 ``UI`` 小部件（如 ``show more`` 按钮和模式窗口）的页面内容。

在下一章中，我们将使用 ``Laravel`` 为 ``Vuebnb`` 设置一个后端，然后让 ``Vue`` 开始短暂休息。