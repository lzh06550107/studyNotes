****************
优化你的App和使用组件显示数据
****************

在第2章“显示，循环，搜索和过滤数据”中，我们得到了 ``Vue`` 应用程序显示我们的人员目录，我们可以利用这个机会优化我们的代码并将其分解为组件。这使得代码更易于管理，更易于理解，并且使其他开发人员可以更轻松地理解数据流（或者在几个月后回来查看代码！）。

本章将包括：

- 通过减少重复来优化我们的 ``Vue.js`` 代码，并在逻辑上组织我们的代码；
- 如何创建 ``Vue`` 组件并将它们与 ``Vue`` 一起使用；
- 如何在组件中使用 ``props`` 和 ``slots`` ；
- 利用事件在组件之间传输数据；

优化代码
========
正如我们在解决问题时编写的代码一样，当需要退后一步并查看代码以优化代码时，就会出现一个问题。这可能包括减少变量和方法的数量或创建方法，以减少重复功能。我们目前的 ``Vue`` 应用程序如下所示：

.. code-block:: js

    const app = new Vue({
        el: '#app',
        data: {
            people: [...],
            currency: '$',
            filterField: '',
            filterQuery: '',
            filterUserState: ''
        },
        methods: {
            activeStatus(person) {
                return (person.isActive) ? 'Active' : 'Inactive';
            },
            activeClass(person) {
                return person.isActive ? 'active' : 'inactive';
            },
            balanceClass(person) {
                let balanceLevel = 'success';
                if(person.balance < 2000) {
                    balanceLevel = 'error';
                } else if (person.balance < 3000) {
                    balanceLevel = 'warning';
                }
                let increasing = false,
                    balance = person.balance / 1000;
                if(Math.round(balance) == Math.ceil(balance)) {
                    increasing = 'increasing';
                }
                return [balanceLevel, increasing];
            },
            formatBalance(balance) {
                return this.currency + balance.toFixed(2);
            },
            formatDate(date) {
                let registered = new Date(date);
                return registered.toLocaleString('en-US');
            },
            filterRow(person) {
                let result = true;
                if(this.filterField) {
                    if(this.filterField === 'isActive') {
                        result = (typeof this.filterUserState === 'boolean') ? (this.filterUserState === person.isActive) : true;
                    } else {
                        let query = this.filterQuery,
                            field = person[this.filterField];
                        if(typeof field === 'number') {
                            query.replace(this.currency, '');
                            try {
                                result = eval(field + query);
                            } catch(e) {}
                        } else {
                            field = field.toLowerCase();
                            result = field.includes(query.toLowerCase());
                        }
                    }
                }
                return result;
            },
            isActiveFilterSelected() {
                return (this.filterField === 'isActive');
            }
        }
    });

看看前面的代码，我们可以做出一些改进。这包括：

- 减少过滤器变量的数量并进行逻辑分组；
- 组合格式函数；
- 减少硬编码变量和属性的数量；
- 将方法重新排序为更合理的顺序；

我们将分别介绍这些要点，以便我们有一个干净的基础代码来构建组件。

减少过滤器变量的数量并进行逻辑分组
----------------------------------
过滤当前使用了三个变量， ``filterField`` ， ``filterQuery`` 和 ``filterUserState`` 。目前链接这些变量的唯一方式是名称，而不是拥有它们的对象来系统地链接。 这样做可以避免任何含糊不清的问题，以确定它们是否与相同的组件相关。 在数据对象中，创建一个名为 ``filter`` 的新对象并移动每个变量到其中：

.. code-block:: js

    data: {
        people: [..],
        currency: '$',
        filter: {
                field: '',
                query: '',
                userState: '',
        }
    }

要访问数据，请将 ``filterField`` 的任何引用更新为 ``this.filter.field`` 。 注意额外的点，表示它是过滤器对象的键。不要忘记更新 ``filterQuery`` 和 ``filterUserState`` 引用。例如， ``isActiveFilterSelected`` 方法将变为：

.. code-block:: js

    isActiveFilterSelected() {
        return (this.filter.field === 'isActive');
    }

您还需要更新视图中的 ``v-model`` 和 ``v-show`` 属性 - 每种变量都有出现五次。

在更新过滤变量时，我们可以借此机会删除一个。 通过我们目前的过滤，我们一次只能有一个过滤器处于活动状态。 这意味着在任何时候 ``query`` 和 ``userState`` 变量只能使用一个，这使我们有机会合并这两个变量。 为此，我们需要更新视图和应用程序代码来迎合这一点。

从过滤器数据对象中删除 ``userState`` 变量，并将视图中的任何 ``filter.userState`` 更新为 ``filter.query`` 。 现在在 ``Vue``  JavaScript代码中查找并替换 ``filter.userState`` ，使用 ``filter.query`` 替换它。

在浏览器中查看您的应用程序，它将开始工作，可以按字段过滤用户。 但是，如果按状态筛选，然后切换到其他任何字段，查询输入字段将不会显示。这是因为使用单选按钮将该值设置为布尔值，当试图将查询字段转换为小写字符时，该布尔值不能这样做。为了解决这个问题，我们可以使用原生 ``JavaScript String()`` 函数将 ``filter.query`` 变量中的任何值转换为字符串。这确保了我们的过滤功能可以与任何过滤输入一起工作：

.. code-block:: js

    if(this.filterField === 'isActive') {
        result = (typeof this.filter.query === 'boolean') ? (this.filter.query === person.isActive) : true;
    } else {
        let query = String(this.filter.query),
            field = person[this.filter.field];
        if(typeof field === 'number') {
            query.replace(this.currency, '');
            try {
                result = eval(field + query);
            } catch(e) {}
        } else {
            field = field.toLowerCase();
            result = field.includes(query.toLowerCase());
        }
    }

现在将此代码添加到我们的代码中，可以确保我们的查询数据无论使用什么值都允许用户在字段之间切换以进行过滤。如果选择活动用户并选择单选按钮，则过滤按预期工作，但是，如果您现在切换到电子邮件或其他字段，则输入框会预填充 ``true`` 或 ``false`` 。这会即时过滤，并且通常不会返回任何结果。在两个文本过滤字段之间切换时也会出现这种情况，这不是所需的效果。

我们想要的是，无论何时更新选择框，过滤器查询都应该清除。无论是单选按钮还是输入框，选择一个新字段都应该重置筛选查询，这确保可以开始新的搜索。

这是通过删除选择框和 ``filter.field`` 变量之间的链接并创建我们自己的方法来处理更新来完成的。然后，当选择框更改时，我们触发该方法。此方法将清除 ``query`` 变量并将 ``field`` 变量设置为选择框值。

删除选择框上的 ``v-model`` 属性并添加新的 ``v-on:change`` 属性。我们将传入一个方法名称，每次更新选择框时都会触发该方法名称。

``v-on`` 是一种我们以前没有遇到过的新 ``Vue`` 绑定。它允许您将元素的操作绑定到 ``Vue`` 方法。例如， ``v-on:click`` 是最常用的一种 - 它允许您将点击功能绑定到元素。我们将在本书的下一部分介绍更多内容。 ``v-bind`` 可以缩写为冒号， ``v-on`` 可以缩写为 ``@`` 符号，允许您使用 ``@click =""`` ，例如：

.. code-block:: html

    <select v-on:change="changeFilter($event)"  id="filterField">
        <option value="">Disable filters</option>
        <option value="isActive">Active user</option>
        <option value="name">Name</option>
        <option value="email">Email</option>
        <option value="balance">Balance</option>
        <option value="registered">Date registered</option>
    </select>

此属性在每次更新时触发 ``changeFilter`` 方法并将更改的 ``$event`` 数据传递给它。这个默认的 ``Vue`` 事件对象包含了很多我们可以利用的信息，但是 ``target.value`` 数据是我们之后的关键。

在 ``Vue`` 实例中创建一个接受事件参数并更新 ``query`` 和 ``field`` 变量的新方法。 ``query`` 变量需要清除，因此将其设置为空字符串，而 ``field`` 变量可以设置为选择框的值：

.. code-block:: js

    changeFilter(event) {
        this.filter.query = '';
        this.filter.field = event.target.value;
    }

现在查看您的应用程序应清除过滤器查询的任何内容，同时仍按预期方式运行。

组合格式函数
------------
我们的下一个优化是将 ``Vue`` 实例中的 ``formatBalance`` 和 ``formatDate`` 方法组合起来。 这样，我们就可以扩展我们的格式函数，而不会使用具有类似功能的几种方法来扩展代码。 有两种方法可以处理格式样式函数 - 我们可以自动检测输入的格式，或作为第二个选项传递所需的格式选项。 两者都有其优点和缺点，但我们将查看这两者。

自动检测格式
^^^^^^^^^^^^
在传递给函数时自动检测变量类型对于更简洁的代码非常有用。在你看来，你可以调用函数并传递你想要格式化的参数。 例如：

.. code-block:: html

    {{ format(person.balance) }}

该方法将包含一个 ``switch`` 语句并根据 ``typeof`` 值格式化该变量。 ``switch`` 语句可以计算单个表达式，然后根据输出执行不同的代码。 ``Switch`` 语句可以非常强大，因为它们允许构建子句 - 根据结果使用几个不同的代码位。 可以阅读更多关于 MDN 上的 ``switch`` 语句。

如果您比较相同的表达式，则 ``switch`` 语句是 ``if`` 语句的一个很好的选择。 你也可以为一个代码块提供几种情况，如果以前的情况都不符合，甚至可以包含一个默认值。 作为使用中的一个示例，我们的格式方法可能如下所示：

.. code-block:: js

    format(variable) {
        switch (typeof variable) {
            case 'string':
                // Formatting if the variable is a string
                break;
            case 'number':
                // Number formatting
                break;
            default:
                // Default formatting
                break;
        }
    }

重要的是要注意 ``break;`` 行 。这是每一个 ``switch`` 情况的结束。 如果 ``break`` 被省略，代码将继续并执行以下情况 - 有时是所需的效果。

自动检测变量类型然后格式化是简化代码的好方法。但是，对于我们的应用程序，它不是一个合适的解决方案，因为我们格式化日期， ``typeof`` 输出结果是一个字符串，不能识别为日期类型。

传入第二个变量
^^^^^^^^^^^^^^
上述自动检测的替代方法是将第二个变量传递给 ``format`` 函数。如果我们希望格式化其他字段，这给了我们更大的灵活性和可扩展性。使用第二个变量，我们可以传入一个固定的字符串，该字符串与 ``switch`` 语句中的预选列表相匹配，或者我们可以传递该字段本身。 视图中固定字符串方法的一个例子是：

.. code-block:: html

    {{ format(person.balance, 'currency') }}

如果我们有几个不同的字段需要进行格式化，如 ``balance`` 目前所做的那样，这会很好地工作，但似乎在使用 ``balance`` 键和 ``currency`` 格式时会有轻微的重复。

作为折衷方案，我们将传递 ``person`` 对象作为第一个参数，以便我们可以访问所有数据，并将字段的名称作为第二个参数。 然后我们将使用它来识别所需的格式方法并返回特定的数据。

创建方法
^^^^^^^^
在你的视图中，用传入 ``person`` 变量作为第一个参数，并将字段用引号括起来作为第二个参数的单一格式替换 ``formatDate`` 和 ``formatBalance`` 函数：

.. code-block:: html

    <td v-bind:class="balanceClass(person)">
        {{ format(person, 'balance') }}
    </td>
    <td>
        {{ format(person, 'registered') }}
    </td>

在你的 ``Vue`` 实例中创建一个新的格式方法，它接受两个参数： ``person`` 和 ``key`` 。作为第一步，使用 ``person`` 对象和键变量检索字段：

.. code-block:: js

    format(person, key) {
        let field = person[key],
            output = field.toString().trim();
        return output;
    }

我们还在函数内部创建了第二个变量为 ``output``  - 这是函数结尾处返回的内容，默认情况下设置为该 ``field`` 。这可以确保，如果我们的格式化键与传入的格式键不匹配，则会返回未触动的字段数据 - 但是，我们会将该字段转换为字符串，并从变量中删除任何空格。 现在运行应用程序将返回没有任何格式的字段。

添加一个 ``switch`` 语句，将表达式设置为 ``key`` 。 将两个条件添加到 ``switch`` 语句 - 一个是 ``balance`` ，另一个是 ``registered`` 。 由于我们不希望在与案例不匹配时发生任何事情，所以我们不需要有一个默认语句：

.. code-block:: js

    format(person, key) {
        let field = person[key],
            output = field.toString().trim();
        switch(key) {
            case 'balance':
                break;
            case 'registered':
                break;
        }
        return output;
    }

我们现在只需要将原始格式化函数中的代码复制到各个条件中：

.. code-block:: js

    format(person, key) {
        let field = person[key],
            output = field.toString().trim();
        switch(key) {
            case 'balance':
                output = this.currency + field.toFixed(2);
                break;
            case 'registered':
                let registered = new Date(field);
                output = registered.toLocaleString('en-US');
                break;
        }
        return output;
    }

这种格式功能现在更加灵活。如果需要更多字段（例如处理名称字段），我们可以添加更多 ``switch`` 条件，或者我们可以将新案例添加到现有代码。例如，如果我们的数据包含一个详细记录用户停用( ``deactivated`` )其帐户日期的字段，我们可以很容易地以与注册相同的格式显示它：

.. code-block:: js

    case 'registered':
    case 'deactivated':
        let registered = new Date(field);
        output = registered.toLocaleString('en-US');
        break;

减少硬编码变量和属性的数量并减少冗余
-------------------------------------
当查看Vue JavaScript时，很明显可以通过引入全局变量并在函数中设置更多局部变量以使其更具可读性来优化它。我们也可以使用现有的函数来停止重复编码。

第一个优化是在我们的 ``filterRow()`` 方法中，我们检查 ``filter.field`` 是否处于活动状态。这也在我们用来显示和隐藏单选按钮的 ``isActiveFilterSelected`` 方法中重复出现。更新 ``if`` 语句来代替使用此方法，所以代码如下所示：

.. code-block:: js

    if(this.filter.field === 'isActive') {
        result = (typeof this.filter.query === 'boolean') ?
            (this.filter.query === person.isActive) : true;
    } else {

前面的代码已将 ``this.filter.field ==='isActive'`` 代码移除并替换为 ``isActiveFilterSelected()`` 方法。它现在应该是这样的：

.. code-block:: js

    if(this.isActiveFilterSelected()) {
        result = (typeof this.filter.query === 'boolean') ? (this.filter.query === person.isActive) : true;
    } else {

在 ``filterRow`` 方法中，我们可以通过将 ``query`` 和 ``field``  作为变量存储在方法的开始处来减少代码。 ``result`` 也不是一个正确的关键字，所以让我们将其更改为 ``visible`` 。 首先，在开始时创建并存储两个变量，并将 ``result`` 重命名为 ``visible`` ：

.. code-block:: js

    filterRow(person) {
        let visible = true,
            field = this.filter.field,
            query = this.filter.query;

替换该函数中的所有实例的变量，例如，该方法的第一部分将如下所示：

.. code-block:: js

    if(field) {
        if(this.isActiveFilterSelected()) {
            visible = (typeof query === 'boolean') ? (query === person.isActive) : true;
        } else {
            query = String(query),
                field = person[field];

保存您的文件并在浏览器中打开应用程序，以确保您的优化不会中断功能。

最后一步是将方法重新排列成对您有意义的顺序。随意添加注释以分离出不同的方法类型 - 例如，与 ``CSS`` 类或筛选相关的方法。 我也删除了 ``activeStatus`` 方法，因为我们可以利用我们的 ``format`` 方法来格式化该字段的输出。 优化之后，JavaScript代码现在看起来如下所示：

.. code-block:: js

    const app = new Vue({
        el: '#app',
        data: {
            people: [...],
            currency: '$',
            filter: {
                field: '',
                query: ''
            }
        },
        methods: {
            isActiveFilterSelected() {
                return (this.filter.field === 'isActive');
            },
            /**
             * CSS Classes
             */
            activeClass(person) {
                return person.isActive ? 'active' : 'inactive';
            },
            balanceClass(person) {
                let balanceLevel = 'success';
                if(person.balance < 2000) {
                    balanceLevel = 'error';
                } else if (person.balance < 3000) {
                    balanceLevel = 'warning';
                }
                let increasing = false,
                    balance = person.balance / 1000;
                if(Math.round(balance) == Math.ceil(balance)) {
                    increasing = 'increasing';
                }
                return [balanceLevel, increasing];
            },
            /**
             * Display
             */
            format(person, key) {
                let field = person[key],
                    output = field.toString().trim();
                switch(key) {
                    case 'balance':
                        output = this.currency +
                            field.toFixed(2);
                        break;
                    case 'registered':
                        let registered = new Date(field);
                        output = registered.toLocaleString('en-US');
                        break;
                    case 'isActive':
                        output = (person.isActive) ? 'Active' :
                            'Inactive';
                }
                return output;
            },
            /**
             * Filtering
             */
            changeFilter(event) {
                this.filter.query = '';
                this.filter.field = event.target.value;
            },
            filterRow(person) {
                let visible = true,
                    field = this.filter.field,
                    query = this.filter.query;
                if(field) {
                    if(this.isActiveFilterSelected()) {
                        visible = (typeof query === 'boolean') ?
                            (query === person.isActive) : true;
                    } else {
                        query = String(query),
                            field = person[field];
                        if(typeof field === 'number') {
                            query.replace(this.currency, '');
                            try {
                                visible = eval(field + query);
                            } catch(e) {}
                        } else {
                            field = field.toLowerCase();
                            visible =
                                field.includes(query.toLowerCase());
                        }
                    }
                }
                return visible;
            }
        }
    });

创建Vue组件
===========
现在我们确信我们的代码更加清晰，我们可以继续为应用的各个部分制作 ``Vue`` 组件。暂时搁置代码，并在学习组件时打开一个新文档。

``Vue`` 组件非常强大，是任何 ``Vue`` 应用程序的重要补充。它们允许您创建包含自己的数据，方法和计算值的可重用代码包。

对于我们的应用程序，我们有机会创建两个组件：一个用于每个 ``person`` ，一个用于应用程序的过滤部分。我鼓励您始终考虑将应用程序分解为可能的组件 - 这有助于将您的代码分组到相关的功能中。

组件看起来像迷你 ``Vue`` 实例，因为每个实例都有其自己的数据，方法和计算对象 - 以及我们稍后将介绍的一些特定于组件的特定选项。在创建具有不同页面和小节的应用程序时，组件也非常有用 - 这将在第8章介绍 ``Vue-Router`` 和加载基于 ``URL`` 的组件中介绍。

组件注册后，您将创建一个自定义 ``HTML`` 元素以在您的视图中使用，例如：

.. code-block:: js

    <my-component></my-component>

当命名组件时，可以使用 ``kebab-case`` （连字符）， ``PascalCase`` （没有标点，但每个单词都大写）或 ``camelCase`` （类似于 ``Pascal`` ，但第一个单词不是大写）。 ``Vue`` 组件不受 ``W3C Web`` 组件/自定义元素规则的限制或与 ``W3C Web`` 组件/自定义元素规则相关联，但遵循这种使用 ``kebab-case`` 的惯例是很好的做法。

创建和初始化你的组件
--------------------
``Vue`` 组件是使用 ``Vue.component(tagName，options)`` 语法注册的。 每个组件都必须具有关联的标签名称。在初始化 ``Vue`` 实例之前， ``Vue.component`` 注册必须发生。至少，每个组件应该有一个 ``template`` 属性 - 表示使用组件时应该显示的内容。模板必须始终被一个元素包装；这样自定义 ``HTML`` 标记可以在父容器中被替换。

例如，您不能将以下内容作为您的模板：

.. code-block:: html

    <div>Hello</div><div>Goodbye</div>

如果您确实传递了此格式的模板， ``Vue`` 会在浏览器的 ``JavaScript`` 控制台中发出错误警告您。

用一个简单的固定模板创建一个 ``Vue`` 组件：

.. code-block:: js

    Vue.component('my-component', {
        template: '<div>hello</div>'
    });
    const app = new Vue({
        el: '#app',
        // App options
    });

在声明了这个组件后，它现在会给我们一个 ``<my-component> </ mycomponent>``  HTML标记来在我们的视图中使用。

您也可以在 ``Vue`` 实例中指定组件。 如果你在一个站点上有多个Vue实例并且希望将一个组件包含到另一个中，就可以使用它。要做到这一点，创建你的组件作为一个简单的对象，并在你的 ``Vue`` 实例的 ``components`` 对象中分配 ``tagName`` ：

.. code-block:: js

    let Child = {
        template: '<div>hello</div>'
    }
    const app = new Vue({
        el: '#app',
        // App options
        components: {
            'my-component': Child
        }
    });

但对于我们的应用程序，我们将坚持使用 ``Vue.component()`` 方法来启用组件。

使用你的组件
------------
在你的视图中，增加自定义的 ``HTML`` 组件元素:

.. code-block:: js

    <div id="app">
        <my-component></my-component>
    </div>

在浏览器中查看此内容应该用 ``<div>`` 和 ``hello`` 消息替换 ``<my-component>`` HTML标记。

在某些情况下，自定义 ``HTML`` 标记不会被解析和接受 - 这些情况通常位于 ``<table>`` ， ``<ol>`` ， ``<ul>`` 和 ``<select>`` 元素中。 如果是这种情况，您可以在标准HTML元素上使用 ``is=""`` 属性：

.. code-block:: js

    <ol>
        <li is="my-component"></li>
    </ol>

使用组件数据和方法
------------------
由于 ``Vue`` 组件是 ``Vue`` 应用程序的自包含元素，因此它们都有自己的数据和函数。 这有助于在同一页面上重新使用组件，因为信息是组件实例独有的。 方法和计算函数的声明与您在 ``Vue`` 应用程序上的声明相同， **但数据键应该是返回对象的函数。**

组件的数据对象必须是一个函数。 这样每个组件都有自己的自包含数据，而不是在相同组件的不同实例之间混淆和共享数据。该函数仍然必须像在 ``Vue`` 应用程序中那样返回一个对象。

创建一个名为 ``balance`` 的新组件，为您的组件添加一个数据函数和计算对象，并为模板属性添加一个空的 ``<div>`` ：

.. code-block:: js

    Vue.component('balance', {
        template: '<div></div>',
        data() {
            return {
            }
        },
        computed: {
        }
    });

接下来，在数据对象中增加一个 ``cost:1234`` ，并将该变量添加到您的模板中。将 ``<balance></balance>`` 自定义 ``HTML`` 元素添加到您的视图中，应该会显示您的整数：

.. code-block:: js

    Vue.component('balance', {
        template: '<div>{{ cost }}</div>',
        data() {
            return {
                cost: 1234
            }
        },
        computed: {
        }
    });

和第1章 ``Vue.js`` 入门中的 ``Vue`` 实例一样，在计算对象中添加一个函数，该函数将货币符号附加到整数，并确保有两位小数。 不要忘记将货币符号添加到数据函数中。

更新模板以输出计算值而不是原始 ``cost`` ：

.. code-block:: js

    Vue.component('balance', {
        template: '<div>{{ formattedCost }}</div>',
        data() {
            return {
                cost: 1234,
                currency: '$'
            }
        },
        computed: {
            formattedCost() {
                return this.currency + this.cost.toFixed(2);
            }
        }
    });

这是组件的一个基本示例，但是，组件本身的固定 ``cost`` 受到限制。

传递数据到你的组件——props
-------------------------
将余额作为一个组成部分很好，但如果余额是固定的，那么不是很好。
当您添加通过 ``HTML`` 属性传递参数和属性的功能时，组件真正有用。 在 ``Vue`` 世界中，这些被称为道具( ``props`` )。 道具可以是静态的或可变的。 为了使组件期望这些属性，您需要使用 ``props`` 属性在组件上创建一个数组。 例如，如果我们想制作一个 ``heading`` 组件：

.. code-block:: js

    Vue.component('heading', {
        template: '<h1>{{ text }}</h1>',
        props: ['text']
    });

这个组件在视图中应该这样使用：

.. code-block:: js

    <heading text="Hello!"></heading>

使用道具，我们不需要在数据对象中定义 ``text``  变量，因为在道具数组中定义它自动使其可用于模板。 ``props`` 数组还可以采取更多选项，允许您定义预期的输入类型，是否需要或使用默认值（如果省略）。

为 ``balance`` 组件添加一个 ``prop`` ，以便我们可以将 ``cost`` 作为 ``HTML`` 属性传递。现在您的视图应该如下所示：

.. code-block:: html

    <balance cost="1234"></balance>

现在，我们可以在 ``JavaScript`` 中将 ``cost prop`` 添加到组件中，并从数据函数中删除固定值：

.. code-block:: js

    template: '<div>{{ formattedCost }}</div>',
    props: ['cost'],
    data() {
        return {
            currency: '$'
        }
    },

然而，在我们的浏览器中运行这个功能会在我们的 ``JavaScript`` 控制台中引发错误。这是因为，在本质上，传入的道具被解释为字符串。我们可以用两种方式解决这个问题。我们可以在我们的 ```formatCost()`` 函数中将我们的道具转换为一个数字，或者，我们可以使用 ``v-bind:HTML属性`` 来告诉 ``Vue`` 接受它的输入。

如果你还记得，我们使用这种技术和我们的过滤器来获取 ``true`` 和 ``false`` ，并将它们用作布尔值而不是字符串。 添加 ``v-bind:cost`` 属性：

.. code-block:: html

    <balance v-bind:cost="15234"></balance>

存在额外的步骤可以确保Vue知道需要那种类型的输入数据，且通知组件的用户应该传入何种类型的数据。可以通过组件本身允许你定义数据类型和默认值。

切换 ``props`` 数组为对象，用 ``cost`` 作为键。如果你定义字段类型，你可以使用 ``Vue`` 简写来声明字段类型。这些类型可以是 ``String`` , ``Number`` , ``Boolean`` , ``Function`` , ``Object`` , ``Array`` ,  ``Symbol`` 。由于我们的 ``cost`` 属性应该是一个 ``number`` ，请将其添加为 ``key`` ：

.. code-block:: js

    props: {
        cost: Number
    },

如果没有定义任何东西时，不是抛出错误，而是我们的组件将渲染为 ``$0.00`` 。 我们可以通过将默认值设置为 ``0`` 来实现。要定义默认值，我们需要将我们的 ``prop`` 转换为对象 - 包含值为 ``Number`` 的类型键。 然后我们可以定义一个 ``default`` 键并将该值设置为 0：

.. code-block:: js

    props: {
        cost: {
            type: Number,
            default: 0
        }
    },

在浏览器中渲染组件应显示传递给 ``cost`` 属性的任何值 - 但不传递该值将会产生 ``$0.00`` 。

回顾一下，我们的组件看起来像这样：

.. code-block:: js

    Vue.component('balance', {
        template: '<div>{{ formattedCost }}</div>',
        props: {
            cost: {
                type: Number,
                default: 0
            }
        },
        data() {
            return {
                currency: '$'
            }
        },
        computed: {
            formattedCost() {
                return this.currency + this.cost.toFixed(2);
            }
        }
    });

当我们制作我们的列表应用程序的 ``person`` 组件时，我们应该能够扩展此示例。

传递数据到你的组件——slots
-------------------------
有时您可能需要将 ``HTML`` 块传递给您的组件，这些 ``HTML`` 块未存储在属性中，或者您希望在出现在组件中之前进行格式化。您可以在您的组件中使用插槽，而不是尝试在计算变量预先格式化内容。

插槽就像占位符，允许您在组件的开始和结束标签之间放置内容，并确定它们要显示的位置。

一个完美的例子就是模态窗口。这些标签通常包含多个标签，并且如果您希望在应用程序中多次使用它们，通常会包含大量 ``HTML`` 以进行复制和粘贴。相反，您可以创建一个模式窗口组件并通过一个插槽传递您的 ``HTML`` 。

创建一个标题为 ``modal-window`` 的新组件。这接受一个 ``visible`` 的 ``prop`` ，它接受一个布尔值，默认为 ``false`` 。对于该模板，我们将使用 ``Bootstrap`` 模式中的 ``HTML`` 作为使用插槽的组件可以轻松简化应用程序的一个很好的示例。为了确保组件的样式，请确保在文档中包含 ``bootstrap`` 资产文件：

.. code-block:: js

    Vue.component('modal-window', {
        template: `<div class="modal fade">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close"
                            data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btnprimary">Save changes</button>
                    <button type="button" class="btn btnsecondary" data-dismiss="modal">Close
                    </button>
                </div>
            </div>
        </div>
        </div>`,
        props: {
            visible: {
                type: Boolean,
                default: false
            }
        }
    });

我们将使用 ``visible`` 的 ``prop`` 来确定模态窗口是否打开。将 ``v-show`` 属性添加到接受可见变量的外部容器中：

.. code-block:: js

    Vue.component('modal-window', {
        template: `<div class="modal fade" v-show="visible">
            ...
            </div>`,
        props: {
            visible: {
                type: Boolean,
                default: false
            }
        }
    });

将你的 ``modal-window`` 组件添加到应用程序，指定 ``visible`` 为 ``true`` ，所以我们可以理解和看到发生了什么：

.. code-block:: html

    <modal-window :visible="true"></modal-window>

我们现在需要将一些数据传递给我们的模式框。 在两个标签之间添加标题和一些段落：

.. code-block:: html

    <modal-window :visible="true">
        <h1>Modal Title</h1>
        <p>Lorem ipsum dolor sit amet, consectetur
            adipiscing elit. Suspendisse ut rutrum ante, a
            ultrices felis. Quisque sodales diam non mi
            blandit dapibus. </p>
        <p>Lorem ipsum dolor sit amet, consectetur
            adipiscing elit. Suspendisse ut rutrum ante, a
            ultrices felis. Quisque sodales diam non mi
            blandit dapibus. </p>
    </modal-window>

在浏览器中按刷新将不会执行任何操作，因为我们需要告诉组件如何处理数据。 在您的模板内，在希望显示内容的位置添加 ``<slot></ slot>`` HTML标记。 将它添加到模态体类的 ``div`` 中：

.. code-block:: js

    Vue.component('modal-window', {
        template: `<div class="modal fade">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close"
                            data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <slot></slot>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btnprimary">Save changes</button>
                    <button type="button" class="btn btnsecondary" data-dismiss="modal">Close
                    </button>
                </div>
            </div>
        </div>
        </div>`,
        props: {
            visible: {
                type: Boolean,
                default: false
            }
        }
    });

查看您的应用程序现在将显示您在模态窗口中传入的内容。 已经有了这个新组件，该应用程序看起来更干净。

查看 ``Bootstrap`` HTML，我们可以看到标题，正文和页脚都有空间。 我们可以使用命名插槽来识别这些部分。 这使我们能够将特定内容传递到我们组件的特定区域。

在模态窗口的页眉和页脚中创建两个新的 ``<slot>`` 标签。 给这些新的名称属性，但保留现有的空的：

.. code-block:: html

    <div class="modal fade" vshow="visible">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <slot name="header"></slot>
                    <button type="button" class="close" datadismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <slot></slot>
                </div>
                <div class="modal-footer">
                    <slot name="footer"></slot>
                    <button type="button" class="btn btnprimary">Save changes</button>
                    <button type="button" class="btn btnsecondary" datadismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

在我们的应用程序中，我们现在可以通过在 ``HTML`` 中指定 ``slot`` 属性来指定要传送的内容。这可以使用特定的标签或围绕几个标签的容器。 任何没有插槽属性的HTML也将默认为您的未命名插槽：

.. code-block:: html

    <modal-window :visible="true">
        <h1 slot="header">Modal Title</h1>
        <p>Lorem ipsum dolor sit amet, consectetur
            adipiscing elit. Suspendisse ut rutrum ante, a
            ultrices felis. Quisque sodales diam non mi
            blandit dapibus. </p>
        <p slot="footer">Lorem ipsum dolor sit amet,
            consectetur adipiscing elit. Suspendisse ut
            rutrum ante, a ultrices felis. Quisque sodales
            diam non mi blandit dapibus. </p>
    </modal-window>

我们现在可以指定我们的内容到特定的地方。

您可以使用插槽完成的最后一件事是指定一个默认值。 例如，您可能希望在大多数时候在页脚中显示按钮，但希望能够根据需要替换它们。使用 ``<slot>`` 时，将在标签之间放置任何内容，除非在应用中指定组件时被覆盖。

创建一个标题为 ``buttons`` 的新插槽，并将按钮放置在页脚内。 尝试用其他内容替换它们。

该模板变为：

.. code-block:: html

    <div class="modal fade" vshow="visible">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <slot name="header"></slot>
                    <button type="button" class="close" datadismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <slot></slot>
                </div>
                <div class="modal-footer">
                    <slot name="footer"></slot>
                    <slot name="buttons">
                        <button type="button" class="btn btnprimary">Save changes</button>
                        <button type="button" class="btn btnsecondary" datadismiss="modal">Close</button>
                    </slot>
                </div>
            </div>
        </div>
    </div>

增加HTML：

.. code-block:: html

    <modal-window :visible="true">
        <h1 slot="header">Modal Title</h1>
        <p>Lorem ipsum dolor sit amet, consectetur
            adipiscing elit. Suspendisse ut rutrum ante, a
            ultrices felis. Quisque sodales diam non mi blandit
            dapibus. </p>
        <p slot="footer">Lorem ipsum dolor sit amet,
            consectetur adipiscing elit. Suspendisse ut rutrum
            ante, a ultrices felis. Quisque sodales diam non mi
            blandit dapibus. </p>
        <div slot="buttons">
            <button type="button" class="btn btnprimary">Ok</button>
        </div>
    </modal-window>

虽然我们不会在人员列表应用程序中使用插槽，但最好了解 ``Vue`` 组件的功能。 如果您希望使用这种模式框，则可以将默认情况下的可见性设置为 ``false`` 。 然后，您可以添加一个按钮，该按钮可以将变量从 ``false`` 更改为 ``true`` ，从而显示模式框。

创建一个可重用的组件
====================
组件的美妙之处在于能够在相同的视图中多次使用它们。 这使您能够为该数据拥有一个“真相源”。 我们将为我们的人员列表创建可重复的组件，并为过滤部分创建一个单独的组件。

打开你的人员列表代码，并创建一个名为 ``team-member`` 的新组件。 不要忘记在 ``Vue`` 应用程序初始化之前定义组件。 向组件添加一个 ``prop`` 以允许传递 ``person`` 对象。 出于验证目的，仅指定它可以是对象：

.. code-block:: js

    Vue.component('team-member', {
        props: {
            person: Object
        }
    });

我们现在需要将我们的模板集成到组件中，这是我们视图内部 ``tr`` 中的所有内容。

组件中的模板变量只接受一个没有新行的普通字符串，所以我们需要执行以下操作之一：

- 内嵌我们的 ``HTML`` 模板 - 非常适合小模板，但在这种情况下会牺牲可读性；
- 用 ``+`` 字符串连接添加新行 - 非常适合一行或两行，但会使我们的 ``JavaScript`` 膨胀；
- 创建一个模板块- ``Vue`` 使我们可以选择使用在视图中使用 ``text/x-template`` 语法和 ``ID`` 来定义外部模板；

由于我们的模板非常大，我们将选择第三个选项，在视图末尾声明我们的模板。

在您的应用程序之外的 ``HTML`` 中，创建一个新的脚本块并添加一个 ``type`` 和 ``ID``
属性：

.. code-block:: html

    <script type="text/x-template" id="team-member-template"></script>

然后，我们可以将我们的人员模板移动到该块中，并删除 ``v-for`` 属性 - 我们仍然会在应用程序中使用它：

.. code-block:: html

    <script type="text/x-template" id="team-member-template">
        <tr v-show="filterRow(person)">
            <td>
                {{ person.name }}
            </td>
            <td>
                <a v-bind:href="'mailto:' + person.email">{{ person.email }}</a>
            </td>
            <td v-bind:class="balanceClass(person)">
                {{ format(person, 'balance') }}
            </td>
            <td>
                {{ format(person, 'registered') }}
            </td>
            <td v-bind:class="activeClass(person)">
                {{ format(person, 'isActive') }}
            </td>
        </tr>
    </script>

我们现在需要更新视图以使用 ``team-member`` 组件取代固定代码。 为了使我们的视图更加清晰易懂，我们将使用前面提到的 ``<template>`` HTML属性。创建一个 ``<template>`` 标签并添加我们之前使用的 ``v-for`` 循环。 为了避免混淆，请更新循环 ``person`` 以将每个 ``individual`` 作为变量使用。 它们可以是相同的，但是如果变量，组件和道具具有不同的名称，它会使代码更易于阅读。 将 ``v-for`` 更新为 ``v-for="individual in people"`` ：

.. code-block:: html

    <table>
        <template v-for="individual in people"></template>
    </table>

在视图的 ``template`` 标签内，添加一个新的 ``team-member`` 组件实例，将 ``individual`` 变量传递给 ``person`` prop。不要忘记将 ``v-bind:`` 添加到 ``person``  prop，否则，该组件会将其解释为 ``individual`` 值的固定字符串：

.. code-block:: html

    <table>
        <template v-for="individual in people">
            <team-member v-bind:person="individual"></team-member>
        </template>
    </table>

我们现在需要更新组件，使用我们已经声明的模板和脚本块的 ``ID`` 作为``template`` 属性的值：

.. code-block:: js

    Vue.component('team-member', {
        template: '#team-member-template',
        props: {
            person: Object
        }
    });

在浏览器中查看应用程序将在 ``JavaScript`` 控制台中创建多个错误。 这是因为我们引用了几个不可用的方法 - 因为它们在父 ``Vue`` 实例上，而不是在组件上。 如果要验证组件是否正常工作，请将代码更改为仅输出该人的姓名，然后按刷新：

.. code-block:: html

    <script type="text/x-template" id="team-member-template">
        <tr v-show="filterRow()">
            <td>
                {{ person.name }}
            </td>
        </tr>
    </script>

创建组件方法和计算函数
-----------------------
我们现在需要在子组件上创建 ``Vue`` 实例上创建的方法，以便可以使用它们。我们可以做的一件事是将父母的方法剪切并粘贴到孩子身上，希望他们能够工作；但是，这些方法依赖于父级属性（例如过滤数据），我们也有机会利用计算属性，这些属性缓存数据并可以加速您的应用程序。

现在，从 ``tr`` 元素中删除 ``v-show`` 属性 - 因为这涉及到过滤，并且一旦我们的行正确显示，就会覆盖它。 我们将逐步解决错误并逐个解决它们，以帮助您了解使用 ``Vue`` 解决问题的方法。

CSS类函数
---------
在浏览器中查看应用程序时遇到的第一个错误是：

.. code-block:: text

    Property or method "balanceClass"is not defined

第一个错误是关于我们使用的 ``balanceClass`` 和 ``activeClass`` 函数。 这两个函数都会根据 ``person`` 的数据添加 ``CSS`` 类，一旦组件被渲染，这些数据就不会改变。

因此，我们可以使用 ``Vue`` 中的缓存。将方法移到组件中，但将它们放入新的 ``computed`` 对象中，而不是 ``methods`` 中。

使用组件，每次调用时都会创建一个新实例，所以我们可以依赖通过道具传入的 ``person`` 对象，而不再需要将该 ``person`` 传递给该函数。从函数和视图中删除参数 - 还将对函数内部的 ``person`` 的引用更新为 ``this.person`` ，以引用存储在该组件上的对象：

.. code-block:: js

    computed: {
        /**
         * CSS Classes
         */
        activeClass() {
            return this.person.isActive ? 'active' : 'inactive';
        },
        balanceClass() {
            let balanceLevel = 'success';
            if(this.person.balance < 2000) {
                balanceLevel = 'error';
            } else if (this.person.balance < 3000) {
                balanceLevel = 'warning';
            }
            let increasing = false,
                balance = this.person.balance / 1000;
            if(Math.round(balance) == Math.ceil(balance)) {
                increasing = 'increasing';
            }
            return [balanceLevel, increasing];
        }
    },

我们现在使用这个函数的组件模板部分应该如下所示：

.. code-block:: html

    <td v-bind:class="balanceClass">
        {{ format(person, 'balance') }}
    </td>

格式化值函数
------------
当将 ``format()`` 函数移动到组件用于格式化数据时，我们面临两种选择。我们可以将它像对象一样移动并放入方法对象中，或者我们可以利用 ``Vue`` 缓存和约定并为每个值创建一个计算函数。

我们正在构建这个应用程序的可扩展性，因此建议为每个值创建计算函数 - 它也将有整理我们的模板的优点。 在计算对象中创建名为 ``balance`` ， ``dateRegistered`` 和 ``status`` 的三个函数。 将 ``format`` 函数的相应部分复制到对应的部分，再次将 ``person`` 的引用更新为 ``this.person`` 。

我们使用函数参数检索字段，现在可以修复每个函数中的值。 您还需要为余额函数在数据对象中添加一个带有货币符号的变量。

.. code-block:: js

    data() {
        return {
            currency: '$'
        }
    },

由于 ``team-member`` 组件是唯一使用我们货币符号的地方，因此我们可以将其从 ``Vue`` 应用程序本身中移除。我们也可以从我们的父 ``Vue`` 实例中移除 ``format`` 函数。

总而言之，我们的 ``Vue`` ``team-member`` 组件应该如下所示：

.. code-block:: js

    Vue.component('team-member', {
        template: '#team-member-template',
        props: {
            person: Object
        },
        data() {
            return {
                currency: '$'
            }
        },
        computed: {
            /**
             * CSS Classes
             */
            activeClass() {
                return this.person.isActive ? 'active' : 'inactive';
            },
            balanceClass() {
                let balanceLevel = 'success';
                if(this.person.balance < 2000) {
                    balanceLevel = 'error';
                } else if (this.person.balance < 3000) {
                    balanceLevel = 'warning';
                }
                let increasing = false,
                    balance = this.person.balance / 1000;
                if(Math.round(balance) == Math.ceil(balance))
                {
                    increasing = 'increasing';
                }
                return [balanceLevel, increasing];
            },
            /**
             * Fields
             */
            balance() {
                return this.currency + this.person.balance.toFixed(2);
            },
            dateRegistered() {
                let registered = new
                Date(this.person.registered);
                return registered.toLocaleString('en-US');
            },
            status() {
                return (this.person.isActive) ? 'Active' : 'Inactive';
            }
        }
    });

我们的 ``team-member-template`` 看起来相当简单：

.. code-block:: html

    <script type="text/x-template" id="team-member-template">
        <tr v-show="filterRow()">
            <td>
                {{ person.name }}
            </td>
            <td>
                <a v-bind:href="'mailto:' + person.email">{{ person.email }}</a>
            </td>
            <td v-bind:class="balanceClass">
                {{ balance }}
            </td>
            <td>
                {{ dateRegistered }}
            </td>
            <td v-bind:class="activeClass">
                {{ status }}
            </td>
        </tr>
    </script>

最后，我们的 ``Vue`` 实例看起来要小得多：

.. code-block:: js

    const app = new Vue({
        el: '#app',
        data: {
            people: [...],
            filter: {
                field: '',
                query: ''
            }
        },
        methods: {
            isActiveFilterSelected() {
                return (this.filter.field === 'isActive');
            },
            /**
             * Filtering
             */
            filterRow(person) {
                let visible = true,
                    field = this.filter.field,
                    query = this.filter.query;
                if (field) {
                    if (this.isActiveFilterSelected()) {
                        visible = (typeof query === 'boolean') ? (query === person.isActive) : true;
                    } else {
                        query = String(query),
                            field = person[field];
                        if (typeof field === 'number') {
                            query.replace(this.currency, '');
                            try {
                                visible = eval(field + query);
                            } catch (e) {
                            }
                        } else {
                            field = field.toLowerCase();
                            visible =
                                field.includes(query.toLowerCase())
                        }
                    }
                }
                return visible;
            },
            changeFilter(event) {
                this.filter.query = '';
                this.filter.field = event.target.value;
            }
        }
    });

在浏览器中查看应用程序，我们应该看到我们的人员列表，其中正确的类添加到表格单元格中，并将格式添加到字段中。

使用props让过滤重新工作
-----------------------
将 ``v-show="filterRow()"`` 属性重新添加到模板中包含的 ``tr`` 元素。 由于我们的每个组件实例上具有 ``person`` 缓存，因此我们不再需要将 ``person``  对象传递给方法。刷新页面会给你的 ``JavaScript`` 控制台一个新的错误：

.. code-block:: text

    Property or method "filterRow" is not defined on the instance but referenced during render

这个错误是因为我们的组件具有 ``v-show`` 属性，它根据我们的过滤和属性显示和隐藏，而不是相应的 ``filterRow`` 函数。因为我们没有将它用于其他任何地方，所以我们可以将方法从 ``Vue`` 实例移动到组件，并将其添加到组件方法中。删除 ``person`` 参数并更新使用 ``this.person`` 的方法：

.. code-block:: js

    filterRow() {
        let visible = true,
            field = this.filter.field,
            query = this.filter.query;
        if(field) {
            if(this.isActiveFilterSelected()) {
                visible = (typeof query === 'boolean') ? (query === this.person.isActive) : true;
            } else {
                query = String(query),
                    field = this.person[field];
                if(typeof field === 'number') {
                    query.replace(this.currency, '');
                    try {
                        visible = eval(field + query);
                    } catch(e) {}
                } else {
                    field = field.toLowerCase();
                    visible = field.includes(query.toLowerCase());
                }
            }
        }
        return visible;
    }

在控制台的下一个错误是：

.. code-block:: text

    Cannot read property 'field' of undefined

过滤不起作用的原因是 ``filterRow`` 方法在组件上查找 ``this.filter.field`` 和 ``this.filter.query`` ，而不是它所属的父 ``Vue`` 实例。

.. warning:: 作为一个快速解决方案，您可以使用 ``this.$parent`` 来引用父元素上的数据，但是这不是建议的，应该只在极端情况下使用或者快速传递数据才使用。

为了将数据传递给组件，我们将使用另一个 ``prop`` - 类似于我们将 ``person`` 传递到组件的方式。幸运的是，我们已经将我们的过滤数据分组了，所以我们能够传递这个对象而不是查询或字段的单个属性。在标题为 ``filter``  的组件上创建一个新的道具，并确保只允许一个对象通过：

.. code-block:: js

    props: {
        person: Object,
        filter: Object
    },

然后我们可以将该道具添加到 ``team-member`` 组件中，从而允许我们传递数据：

.. code-block:: html

    <table>
        <template v-for="individual in people">
            <team-member v-bind:person="individual" v-bind:filter="filter"></team-member>
        </template>
    </table>

为了使我们的过滤工作，我们需要传入一个更多的属性 ``isActiveFilterSelected()`` 函数。 创建另一个名为 ``statusFilter`` 的道具，只允许布尔值为该值（因为这是函数等同的值），并传递该函数。 更新 ``filterRow`` 方法以使用此新值。我们的组件现在看起来像：

.. code-block:: js

    Vue.component('team-member', {
        template: '#team-member-template',
        props: {
            person: Object,
            filter: Object,
            statusFilter: Boolean
        },
        data() {
            return {
                currency: '$'
            }
        },
        computed: {
            /**
             * CSS Classes
             */
            activeClass() {
                return this.person.isActive ? 'active' : 'inactive';
            },
            balanceClass() {
                let balanceLevel = 'success';
                if(this.person.balance < 2000) {
                    balanceLevel = 'error';
                } else if (this.person.balance < 3000) {
                    balanceLevel = 'warning';
                }
                let increasing = false,
                    balance = this.person.balance / 1000;
                if(Math.round(balance) == Math.ceil(balance)) {
                    increasing = 'increasing';
                }
                return [balanceLevel, increasing];
            },
            /**
             * Fields
             */
            balance() {
                return this.currency + this.person.balance.toFixed(2);
            },
            dateRegistered() {
                let registered = new Date(this.registered);
                return registered.toLocaleString('en-US');
            },
            status() {
                return output = (this.person.isActive) ? 'Active' : 'Inactive';
            }
        },
        methods: {
            filterRow() {
                let visible = true,
                    field = this.filter.field,
                    query = this.filter.query;
                if(field) {
                    if(this.statusFilter) {
                        visible = (typeof query === 'boolean') ?
                            (query === this.person.isActive) : true;
                    } else {
                        query = String(query),
                            field = this.person[field];
                        if(typeof field === 'number') {
                            query.replace(this.currency, '');
                            try {
                                visible = eval(field + query);
                            } catch(e) {
                            }
                        } else {
                            field = field.toLowerCase();
                            visible = field.includes(query.toLowerCase());
                        }
                    }
                }
                return visible;
            }
        }
    });

现在使用额外道具的视图中的组件现在看起来如下所示。请注意，当用作 ``HTML`` 属性时，骆驼样式命名的道具变成蛇案（连字符）：

.. code-block:: html

    <template v-for="individual in people">
        <team-member v-bind:person="individual" v- bind:filter="filter" v-bind:statusfilter="isActiveFilterSelected()"></teammember>
    </template>

创建一个过滤组件
================
我们现在需要使过滤部分成为自己的组件。 在这种情况下，这不是严格必要的，但这是一种良好的做法，给我们带来了更多挑战。

我们在过滤组件时面临的问题是在 ``filtering`` 组件和 ``team-member`` 组件之间传输过滤器数据的挑战。 ``Vue`` 用自定义事件解决这个问题。 这些允许您将数据从子组件传递（或“发出”）到父组件。

我们将创建一个过滤组件，通过过滤更改，将数据传回父 ``Vue`` 实例。这些数据然后传递给 ``team-member`` 组件进行过滤。

Vue组件绑定自定义事件
---------------------
``Vue`` 组件使用 ``v-on`` 绑定自定义事件：通过事件，父组件中的数据变化由子组件的操作控制。

可以分为3步理解：

1. 在组件模板中按照正常事件机制绑定事件：

.. code-block:: html

    template: '<button v-on:click="increment">{{ counter }}</button>',
　　　　　　
如上， ``v-on:click`` 就是用来监听子组件点击事件的，这就是原生的自带的事件，容易理解。

2. 子组件的事件发生时，在事件监听函数中向父组件“报告”这一事件（使用 ``$emit`` ）：

.. code-block:: js

    methods: {
        increment: function () {
            this.counter += 1;
            this.$emit('increment');
        }
    },
　　　　　　
上面事件函数中的代码 ``this.$emit('increment')`` 的意思就是向父组件报告，自己发生了 ``increment`` 事件。至于发生的事件叫什么名字，可以随意取名，只要在父组件中绑定时名称一致即可。

3. 父组件在使用时，明确在子组件标签上使用 ``v-on`` 来监听子组件传来的事件：

.. code-block:: html

    <button-counter v-on:increment="incrementTotal"></button-counter>

上方的 ``v-on:increment`` 就是绑定的子组件的 ``increment`` 事件。

创建组件
--------
与 ``team-member`` 组件一样，在 ``JavaScript`` 中声明一个新的 ``Vue.component()`` ，引用模板 ``ID`` 为 ``＃filtering-template`` 。在您的视图中创建一个新的 ``<script>`` 模板块并为其提供相同的 ``ID`` 。将视图中的过滤表单替换为 ``<filtering>`` 自定义 ``HTML`` 模板，并将该表单放入您的 ``filtering-template`` 脚本块中。

你的视图应该如下所示：

.. code-block:: html

    <div id="app">
        <filtering></filtering>
        <table>
            <template v-for="individual in people">
                <team-member v-bind:person="individual" vbind:filter="filter" vbind:statusfilter="isActiveFilterSelected()"> </team-member>
            </template>
        </table>
    </div>
    <script type="text/x-template" id="filteringtemplate">
        <form>
            <label for="fiterField">
                Field:
                <select v-on:change="changeFilter($event)" id="filterField">
                    <option value="">Disable filters</option>
                    <option value="isActive">Active user</option>
                    <option value="name">Name</option>
                    <option value="email">Email</option>
                    <option value="balance">Balance</option>
                    <option value="registered">Date
                        registered</option>
                </select>
            </label>
            <label for="filterQuery" v-show="this.filter.field && !isActiveFilterSelected()">
                Query:
                <input type="text" id="filterQuery" vmodel="filter.query">
            </label>
            <span v-show="isActiveFilterSelected()">
    Active:
    <label for="userStateActive">
    Yes:
    <input type="radio" v-bind:value="true" id="userStateActive" v-model="filter.query">
    </label>
    <label for="userStateInactive">
    No:
    <input type="radio" v-bind:value="false"
           id="userStateInactive" v-model="filter.query">
    </label>
    </span>
        </form>
    </script>
    <script type="text/x-template" id="team-membertemplate">
        // Team member template
    </script>

你的 ``JavaScript`` 应该有以下内容：

.. code-block:: js

    Vue.component('filtering', {
        template: '#filtering-template'
    });

解析JavaScript错误
==================
与 ``team-member`` 组件一样，您将在 ``JavaScript`` 控制台中遇到一些错误。这些可以通过从父实例中复制 ``filter`` 数据对象和 ``changeFilter`` 和 ``isActiveFilterSelected`` 方法到 ``filter`` 组件中。 现在我们将它们留在组件和父实例中，但我们稍后将删除重复：

.. code-block:: js

    Vue.component('filtering', {
        template: '#filtering-template',
        data() {
            return {
                filter: {
                    field: '',
                    query: ''
                }
            }
        },
        methods: {
            isActiveFilterSelected() {
                return (this.filter.field === 'isActive');
            },
            changeFilter(event) {
                this.filter.query = '';
                this.filter.field = event.target.value;
            }
        }
    });

运行应用程序将显示过滤器和人员列表，但过滤器不会更新人员列表，因为他们还没有通信。　　　
使用自定义事件来改变过滤器字段
------------------------------
使用自定义事件，您可以使用 ``$on`` 和 ``$emit`` 函数将数据传回给父实例。 对于这个应用程序，我们将过滤数据存储在父 ``Vue`` 实例，并从组件更新它。然后 ``team-member`` 组件可以从 ``Vue`` 实例读取数据并相应地进行过滤。

第一步是利用父 ``Vue`` 实例上的过滤器对象。 从组件中删除数据对象，并通过prop从父对象传入过滤器中 - 就像我们对 ``team-member`` 组件所做的那样：

.. code-block:: html

    <filtering v-bind:filter="filter"></filtering>

我们现在要修改 ``changeFilter`` 函数来发出事件数据，以便父实例可以更新过滤器对象。

从过滤组件中删除现有的 ``changeFilter`` 方法，并创建一个名为 ``change-filter-field`` 的新方法。在这个方法中，我们只需要 ``$emit`` 在下拉菜单中选择的字段的名称。 ``$emit`` 函数有两个参数：一个键和值。 发出 ``change-filter-field`` 的键并传递 ``event.target.value`` 作为数据。 当使用具有多个单词的变量（例如 ``changeFilterField`` ）时，请确保这些事件名称（ ``$emit`` 函数的第一个参数）和 ``HTML`` 属性是连字符连接的：

.. code-block:: js

    changeFilterField(event) {
        this.$emit('change-filter-field', event.target.value);
    }

为了将数据传递给父 ``Vue`` 实例上的 ``changeFilter`` 方法，我们需要在我们的 ``<filtering>`` 元素中添加一个新的属性 。这使用 ``v-on`` 并绑定到自定义事件名称。 然后它将父方法名称作为属性值，用来监听子组件事件。将该属性添加到您的元素中：

.. code-block:: html

    <filtering v-bind:filter="filter" v-on:change-filter-field="changeFilter"></filtering>

该属性告诉 ``Vue`` 在发生更改过滤器字段事件时触发 ``changeFilter`` 方法。 然后我们可以调整我们的方法来接受参数作为值：

.. code-block:: js

    changeFilter(field) {
        this.filter.query = '';
        this.filter.field = field;
    }

然后清除过滤器并更新字段值，然后通过 ``filter`` 道具连接到我们的 ``filtering`` 组件中。

更新过滤查询
------------
为了发出查询字段，我们将使用一个我们之前没有用过的名为 ``watch`` 的新 ``Vue`` 键。 ``watch`` 函数跟踪数据属性，并可以根据输出运行方法。它能够做的另一件事是发出事件。基于这两者，我们的文本字段和单选按钮被设置为更新 ``field.query`` 变量，我们将在此变量上创建一个新的 ``watch`` 函数。

在组件上的方法之后创建一个新的监视对象：

.. code-block:: js

    watch: {
        'filter.query': function() {
        }
    }

键是你想监听的变量。由于我们包含一个点，它需要用引号包装。在此函数中，创建一个新的 ``$emit`` 事件 ``change-filter-query`` ，输出 ``filter.query`` 的值：

.. code-block:: js

    watch: {
        'filter.query': function() {
            this.$emit('change-filter-query',  this.filter.query)
        }
    }

我们现在需要将此方法和自定义事件绑定到视图中的组件，以便它能够将数据传递给父实例。 将属性的值设置为 ``changeQuery`` - 我们将创建一个方法来处理此问题：

.. code-block:: html

    <filtering v-bind:filter="filter" v-on:change-filter-field="changeFilter" v-on:change-filter-query="changeQuery"></filtering>

在父 ``Vue`` 实例上，创建一个名为 ``changeQuery`` 的新方法，该方法根据输入更新 ``filter.query`` 值：

我们的过滤现在再次运行。更新选择框和输入框（或单选按钮）现在将更新我们的人员列表。 我们的 ``Vue`` 实例明显更小，我们的模板和方法包含在各自的组件中。

最后一步是避免重复使用 ``isActiveFilterSelected()`` 方法，因为它仅在 ``team-member`` 组件上使用过一次，但在 ``filtering`` 组件上只使用过几次。从父 ``Vue`` 实例中删除该方法，从 ``team-member`` HTML元素 ``prop`` 中提取值 ( ``this.filter.field`` )，然后用该值的内容替换 ``team-member`` 组件内的 ``filterRow`` 方法中的 ``statusFilter`` 变量。最终的 ``JavaScript`` 如下所示：

.. code-block:: js

    Vue.component('team-member', {
        template: '#team-member-template',
        props: {
            person: Object,
            filter: Object
        },
        data() {
            return {
                currency: '$'
            }
        },
        computed: {
            /**
             * CSS Classes
             */
            activeClass() {
                return this.person.isActive ? 'active' : 'inactive';
            },
            balanceClass() {
                let balanceLevel = 'success';
                if(this.person.balance < 2000) {
                    balanceLevel = 'error';
                } else if (this.person.balance < 3000) {
                    balanceLevel = 'warning';
                }
                let increasing = false,
                    balance = this.person.balance / 1000;
                if(Math.round(balance) == Math.ceil(balance)) {
                    increasing = 'increasing';
                }
                return [balanceLevel, increasing];
            },
            /**
             * Fields
             */
            balance() {
                return this.currency +
                    this.person.balance.toFixed(2);
            },
            dateRegistered() {
                let registered = new Date(this.registered);
                return registered.toLocaleString('en-US');
            },
            status() {
                return output = (this.person.isActive) ?  'Active' : 'Inactive';
            }
        },
        methods: {
            filterRow() {
                let visible = true,
                    field = this.filter.field,
                    query = this.filter.query;
                if(field) {
                    if(this.filter.field === 'isActive') {
                        visible = (typeof query === 'boolean') ? (query === this.person.isActive) : true;
                    } else {
                        query = String(query),
                            field = this.person[field];
                        if(typeof field === 'number') {
                            query.replace(this.currency, '');
                            try {visible = eval(field + query);
                            } catch(e) {}
                        } else {
                            field = field.toLowerCase();
                            visible = field.includes(query.toLowerCase());
                        }
                    }
                }
                return visible;
            } }
    });

    Vue.component('filtering', {
        template: '#filtering-template',
        props: {
            filter: Object
        },
        methods: {
            isActiveFilterSelected() {
                return (this.filter.field === 'isActive');
            },
            changeFilterField(event) {
                this.filter.field = '';
                this.$emit('change-filter-field',  event.target.value);
            },
        },
        watch: {
            'filter.query': function() {
                this.$emit('change-filter-query', this.filter.query)
            }
        }
    });

    const app = new Vue({
        el: '#app',
        data: {
            people: [...],
            filter: {
                field: '',
                query: ''
            }
        },
        methods: {
            changeFilter(field) {
                this.filter.query = '';
                this.filter.field = field;
            },
            changeQuery(query) {
                this.filter.query = query;
            }
        }
    });

现在视图如下：

.. code-block:: html

    <div id="app">
        <filtering v-bind:filter="filter" v-on:changefilter-field="changeFilter"
                   v-on:change-filterquery="changeQuery"></filtering>
        <table>
            <template v-for="individual in people">
                <team-member v-bind:person="individual" vbind:filter="filter"></team-member>
            </template>
        </table>
    </div>

    <script type="text/x-template" id="filtering-template">
        <form>
            <label for="fiterField">
                Field:
                <select v-on:change="changeFilterField($event)" id="filterField">
                    <option value="">Disable filters</option>
                    <option value="isActive">Active user</option>
                    <option value="name">Name</option>
                    <option value="email">Email</option>
                    <option value="balance">Balance</option>
                    <option value="registered">Date  registered</option>
                </select>
            </label>
            <label for="filterQuery" vshow="this.filter.field && !isActiveFilterSelected()">
                Query:
                <input type="text" id="filterQuery" vmodel="filter.query">
            </label>
            <span v-show="isActiveFilterSelected()">
                Active:
                <label for="userStateActive">
                Yes:
                <input type="radio" v-bind:value="true"
                       id="userStateActive" v-model="filter.query">
                </label>
                <label for="userStateInactive">
                No:
                <input type="radio" v-bind:value="false" id="userStateInactive" v-model="filter.query">
                </label>
            </span>
        </form>
    </script>

    <script type="text/x-template" id="team-member-template">
        <tr v-show="filterRow()">
            <td>
                {{ person.name }}
            </td>
            <td>
                <a v-bind:href="'mailto:' + person.email">{{ person.email }}</a>
            </td>
            <td v-bind:class="balanceClass">
                {{ balance }}
            </td>
            <td>
                {{ dateRegistered }}
            </td>
            <td v-bind:class="activeClass">
                {{ status }}
            </td>
        </tr>
    </script>

总结
====
在最近三章中，您已经学习了如何初始化一个新的 ``Vue`` 实例，计算，方法和数据对象的含义，以及如何列出对象中的数据并操作它以正确显示。 您还学习了如何制作组件，以及保持代码清洁和优化的好处。

在本书的下一部分中，我们将介绍 ``Vuex`` ，它可以帮助我们更好地存储和处理存储的数据。
