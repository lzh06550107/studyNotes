**********
测试Vue.js应用
**********
在这个致命紧张且需求加速的世界中，为我们的应用程序创建自动化测试变得前所未有的重要。 大多数开发人员忽略的一个重要因素是测试是一项技能，虽然您可能编写感到舒服的解决方案，并不意味着您可以编写出好的单元测试。 当你在这方面有更多的经验时，你会发现自己更经常地写测试，并且想知道你没有他们时是怎么做的！

到本章末尾，我们将介绍以下内容：

- 了解你为什么应该考虑使用自动化测试工具和技术；
- 编写 ``Vue`` 组件的第一个单元测试；
- 编写模拟特定功能的测试；
- 编写依赖于 ``Vue.js`` 事件的测试；
- 使用 ``Wallaby.js`` 实时查看我们的测试结果；

当我们谈论测试我们的 ``Vue`` 项目时，根据上下文，我们可能意味着不同的事情。

为什么测试
==========
自动化测试工具的存在是有原因的。 当我们手动创建测试工作时，您会从经验中了解到，这是一个很长（有时是复杂的）过程，无法获得一致的结果。我们不仅需要手动记住某个特定组件是否工作（或者以其他方式将结果写入某处！），而且它没有变化的弹性。

以下是我多年来在测试提出时听到的一些短语：

“但是，保罗，如果我为我的应用程序编写测试，它将需要三倍的时间！”

“我不知道如何写测试......”

“那不是我的工作！”

......和其他各种各样的。

重点是测试是一种技能，就像开发是一项技能一样。 你可能并不擅长于某一方，但随着时间，练习和毅力的发展，你应该发现自己处于一种测试感觉自然且是软件开发的正常组成部分的位置。

单元测试
========
自动化测试工具在我们每次想要验证我们手动工作时功能如预期那样都能正常工作，并为我们提供了一种方法来运行一条命令，逐一测试我们的断言。这会在报告中显示给我们（或者在我们的编辑器中，我们稍后会看到），这使我们能够重构未按预期工作的代码。

与手动测试相比，通过使用自动化测试工具，我们节省了大量的工作量。

单元测试可以定义为一种测试类型，一次只能测试一个“单元”（功能的最小可测部分）。随着应用程序变大，我们可以自动执行此过程以不断测试我们的功能。此时，您可能希望遵循测试驱动开发实践。

在现代 ``JavaScript`` 测试生态系统中，有各种可用的测试套件。这些测试套件可以被认为是能够编写断言，运行测试，向我们提供覆盖报告等等的应用程序。我们将在我们的项目中使用 ``Jest`` ，因为这是由 ``Facebook`` 创建和维护的一个快速灵活的套件。

让我们创建一个新的演示项目，以便我们熟悉单元测试。我们将使用 ``webpack`` 模板而不是 ``webpack-simple`` 模板，因为这允许我们在默认情况下配置测试：

.. code-block:: shell

    # Create a new Vue project
    $ vue init webpack vue-testing

    ? Project name vue-testing
    ? Project description Various examples of testing Vue.js applications
    ? Author Paul Halliday <hello@paulhalliday.io>
    ? Vue build runtime
    ? Install vue-router? Yes
    ? Use ESLint to lint your code? Yes
    ? Pick an ESLint preset Airbnb
    ? Set up unit tests Yes
    ? Pick a test runner jest
    ? Setup e2e tests with Nightwatch? No
    ? Should we run `npm install` for you after the project has been create
    d? (recommended) npm

    # Navigate to directory
    $ cd vue-testing

    # Run application
    $ npm run dev

我们先来研究一下 ``test/unit/specs`` 目录。 在测试我们的 ``Vue`` 组件时，我们将在该目录放置我们的所有单元/集成测试代码文件。 打开 ``HelloWorld.spec.js`` ，让我们逐行浏览它：

.. code-block:: js

    // Importing Vue and the HelloWorld component
    import Vue from 'vue';
    import HelloWorld from '@/components/HelloWorld';

    // 'describe' is a function used to define the 'suite' of tests (i.e.overall context).
    describe('HelloWorld.vue', () => {

        //'it' is a function that allows us to make assertions (i.e. test true/false)
        it('should render correct contents', () => {
            // Create a sub class of Vue based on our HelloWorld component
            const Constructor = Vue.extend(HelloWorld);

            // Mount the component onto a Vue instance
            const vm = new Constructor().$mount();

            // The h1 with the 'hello' class' text should equal 'Welcome to Your Vue.js App'
            expect(vm.$el.querySelector('.hello h1').textContent).toEqual(
                'Welcome to Your Vue.js App',
            );
        });
    });

然后，我们可以通过在终端中运行 ``npm run unit`` 来运行这些测试（确保您在项目目录中）。 这会告诉我们已经通过了多少次测试以及整体测试代码覆盖率。 这个度量可以用来确定一个应用程序在多么健壮的方式；但是，大多数情况下 它不应该被用作真理。 在下面的截图中，我们可以清楚地看到有多少测试已通过：

.. image:: ./images/11-1.png

配置vue-test-utils
-------------------
为了获得更好的测试体验，建议使用 ``vue-test-utils`` 模块，因为这为我们提供了许多专用于 ``Vue`` 框架的助手和模式。 让我们创建一个基于 ``webpack-simple`` 模板的新项目，并自己集成 ``Jest`` 和 ``vue-test-utils`` 。 在终端中运行以下内容：

.. code-block:: shell

    # Create a new Vue project
    $ vue init webpack-simple vue-test-jest

    # Navigate to directory
    $ cd vue-test-jest

    # Install dependencies
    $ npm install

    # Install Jest and vue-test-utils
    $ npm install jest vue-test-utils --save-dev

    # Run application
    $ npm run dev

然后，我们必须为我们的项目添加一些额外配置，以便我们可以运行我们的测试套件 ``Jest`` 。 这可以在我们项目的 ``package.json`` 中配置。 添加以下内容：

.. code-block:: json

    {
      "scripts": {
        "test": "jest"
      }
    }

这意味着，只要我们想运行我们的测试，我们只需在终端中运行 ``npm run test`` 。 这将在任何匹配 ``* .spec.js`` 名称的文件上运行 ``Jest`` 的本地（项目安装）版本。

接下来，我们需要告诉 ``Jest`` 如何在我们的项目中处理单个文件组件（即 ``* .vue`` 文件）。 这需要 ``vue-jest`` 预处理器。 我们还想在我们的测试中使用 ``ES2015 +`` 语法，所以我们还需要 ``babel-jest`` 预处理器。 我们通过在终端中运行以下代码来安装它们：

.. code-block:: shell

    npm install --save-dev babel-jest vue-jest

然后我们可以在 ``package.json`` 中定义下面的对象：

.. code-block:: json

  "jest": {
    "moduleNameMapper": {
      "^@/(.*)$": "<rootDir>/src/$1"
    },
    "moduleFileExtensions": [
      "js",
      "vue"
    ],
    "transform": {
      "^.+\\.js$": "<rootDir>/node_modules/babel-jest",
      ".*\\.(vue)$": "<rootDir>/node_modules/vue-jest"
    }
  }

这本质上告诉 ``Jest`` 如何处理 ``JavaScript`` 和 ``Vue`` 文件，通过依赖于上下文知道使用哪个预处理器（即， ``babel-jest`` 或 ``vue-jest`` ）。

如果我们告诉 ``Babel`` 仅针对我们当前加载的 ``Node`` 版本的转储特性，我们也可以使我们的测试运行得更快。 让我们给我们的 ``.babelrc`` 文件添加一个单独的测试环境：

.. code-block:: json

    {
      "presets": [["env", { "modules": false }], "stage-3"],
      "env": {
        "test": {
          "presets": [["env", { "targets": { "node": "current" } }]]
        }
      }
    }

现在我们已经添加了适当的配置，让我们开始测试吧！

创建一个TodoList
----------------
现在让我们在 ``src/components`` 文件夹中创建一个 ``TodoList.vue`` 组件。 这是我们将要测试的组件，我们会慢慢添加更多功能：

.. code-block:: html

    <template>
        <div>
            <h1>Todo List</h1>
            <ul>
                <li v-for="todo in todos" v-bind:key="todo.id">
                    {{todo.id}}. {{todo.name}}</li>
            </ul>
        </div>
    </template>

    <script>
        export default {
            data() {
                return {
                    todos: [
                        { id: 1, name: 'Wash the dishes' },
                        { id: 2, name: 'Clean the car' },
                        { id: 3, name: 'Learn about Vue.js' },
                    ],
                };
            },
        };
    </script>

    <style>
        ul,
        li {
            list-style: none;
            margin-left: 0;
            padding-left: 0;
        }
    </style>

正如你所看到的，我们只是有一个简单的应用程序，返回一个带有不同项的待办事项数组。 让我们在 ``src/router/index.js`` 中创建一个路由器，以匹配我们新的 ``TodoList`` 组件并将其显示为根：

.. code-block:: js

    import Vue from 'vue';
    import Router from 'vue-router';
    import TodoList from '../components/TodoList';

    Vue.use(Router);

    export default new Router({
        routes: [
            {
                path: '/',
                name: 'TodoList',
                component: TodoList,
            },
        ],
    });

当我们使用 ``vue-router`` 时，我们也需要安装它。 在终端中运行以下内容：

.. code-block:: shell

    $ npm install vue-router --save-dev

然后，我们可以到 ``main.js`` 中添加路由器：

.. code-block:: js

    import Vue from 'vue'
    import App from './App.vue'
    import router from './router';

    new Vue({
        el: '#app',
        router,
        render: h => h(App)
    })

我现在添加了 ``router-view`` ，并选择从 ``App.vue`` 中删除 ``Vue`` 徽标，所以我们有一个更清楚的用户界面。 以下是 ``App.vue`` 的模板：

.. code-block:: html

    <template>
        <div id="app">
            <router-view/>
        </div>
    </template>

正如我们在浏览器中看到的那样，它显示我们的模板，其中包含 ``TodoList`` 的名称和我们创建的待办事项：

.. image:: ./images/11-2.png

写测试
-------
在 ``src/components`` 文件夹中，创建一个名为 ``__tests__`` 的新文件夹，然后创建一个名为 ``TodoList.spec.js`` 的文件。 ``Jest`` 会自动找到这个文件夹和里面的测试文件。

首先从测试套件中导入我们的组件和 ``mount`` 方法：

.. code-block:: js

    import { mount } from 'vue-test-utils';
    import TodoList from '../TodoList';

``mount`` 方法允许我们单独测试我们的 ``TodoList`` 组件，并使我们能够模拟任何输入 ``props`` ，事件和输出。 接下来，让我们创建一个描述块，用来包含我们的测试套件：

.. code-block:: js

    describe('TodoList.vue', () => {

    });

现在让我们挂载组件并获得对 ``Vue`` 实例的访问权限：

.. code-block:: js

    describe('TodoList.vue', () => {
        // Vue instance can be accessed at wrapper.vm
        const wrapper = mount(TodoList);
    });

接下来，我们需要定义 ``it`` 块来断言用例的结果。 让我们来做我们的第一个期望 - 它应该渲染待办事项列表：

.. code-block:: js

    describe('TodoList.vue', () => {
        const todos = [{ id: 1, name: 'Wash the dishes' }];
        const wrapper = mount(TodoList);

        it('should contain a list of Todo items', () => {
            expect(wrapper.vm.todos).toContainEqual(todos[0]);
        });
    });

我们可以通过在终端中运行 ``$npm run test -- --watchAll`` 来观察我们测试的变化。 或者，我们可以在 ``package.json`` 中创建一个新的脚本，为我们做到这一点：

.. code-block:: js

    "scripts": {
        "test:watch": "jest --watchAll"
    }

现在，如果我们在终端内部运行 ``npm run test:watch`` ，它将监视文件系统的任何更改。

以下是我们的结果：

.. image:: ./images/11-3.png

我们有一个合格的测试！ 然而，现在我们必须自己思考，这个测试是否脆弱？ 在真实世界的应用程序中，默认情况下，我们可能在运行时 ``TodoList`` 中没有项目。

我们需要一种方法在我们独立的测试中设置属性。 这是设置我们自己的 ``Vue`` 选项的能力派上用场的地方！

Vue选项
-------
我们可以在 ``Vue`` 实例上设置自己的选项。让我们使用 ``vue-test-utils`` 在实例上设置我们自己的数据并查看是否在屏幕上显示这些数据：

.. code-block:: js

    describe('TodoList.vue', () => {
        it('should contain a list of Todo items', () => {
            const todos = [{ id: 1, name: 'Wash the dishes' }];
            const wrapper = mount(TodoList, {
                data: { todos },
            });

            // Find the list items on the page
            const liWrapper = wrapper.find('li').text();

            // List items should match the todos item in data
            expect(liWrapper).toBe(todos[0].name);
        });
    });

正如我们所看到的，我们现在正在基于组件中的数据选项对屏幕上呈现的项目进行测试。

我们添加一个 ``TodoItem`` 组件，以便我们可以动态地渲染一个包含 ``todo prop`` 的组件。然后我们可以根据我们的 ``prop`` 来测试这个组件的输出：

.. code-block:: html

    <template>
      <li>{{todo.name}}</li>
    </template>

    <script>
    export default {
      props: ['todo'],
    };
    </script>

然后我们可以将它添加到 ``TodoList`` 组件中：

.. code-block:: html

    <template>
      <div>
        <h1>TodoList</h1>
        <ul>
          <TodoItem v-for="todo in todos" v-bind:key="todo.id"
          :todo="todo">{{todo.name}}</TodoItem>
        </ul>
      </div>
    </template>

    <script>
    import TodoItem from './TodoItem';

    export default {
      components: {
        TodoItem,
      },
      // Omitted
    }

我们的测试仍然按预期通过，因为组件在运行时呈现为 ``li`` 。尽管如此，改变它以查找组件本身可能是一个更好的主意：

.. code-block:: js

    import { mount } from 'vue-test-utils';
    import TodoList from '../TodoList';
    import TodoItem from '../TodoItem';

    describe('TodoList.vue', () => {
        it('should contain a list of Todo items', () => {
            const todos = [{ id: 1, name: 'Wash the dishes' }];
            const wrapper = mount(TodoList, {
                data: { todos },
            });

            // Find the list items on the page
            const liWrapper = wrapper.find(TodoItem).text();

            // List items should match the todos item in data
            expect(liWrapper).toBe(todos[0].name);
        });
    });

我们为我们的 ``TodoItem`` 编写一些测试，并在 ``components/__tests__`` 中创建一个 ``TodoItem.spec.js`` ：

.. code-block:: js

    import { mount } from 'vue-test-utils';
    import TodoItem from '../TodoItem';

    describe('TodoItem.vue', () => {
      it('should display name of the todo item', () => {
        const todo = { id: 1, name: 'Wash the dishes' };
        const wrapper = mount(TodoItem, { propsData: { todo } });

        // Find the list items on the page
        const liWrapper = wrapper.find('li').text();

        // List items should match the todos item in data
        expect(liWrapper).toBe(todo.name);
      });
    });

由于我们基本上使用相同的逻辑，所以我们的测试类似。主要区别在于，我们只有一个 ``todo`` 对象，而不是有一个 ``todos`` 列表。我们使用 ``propsData`` 而不是数据来模拟道具，实质上断言我们可以向这个组件添加属性，并渲染正确的数据。让我们来看看我们的测试是否通过或失败：

.. image:: ./images/11-4.png

增加新功能
----------
让我们采用测试驱动的方法为我们的应用程序添加新功能。我们需要一种方法将新项目添加到我们的 ``todo`` 列表中，所以我们先从编写测试开始。在 ``TodoList.spec.js`` 中，我们将添加另一个 ``it`` 断言，它将一个项目添加到我们的 ``todo`` 列表中：

.. code-block:: js

    it('should add an item to the todo list', () => {
        const wrapper = mount(TodoList);
        const todos = wrapper.vm.todos;
        const newTodos = wrapper.vm.addTodo('Go to work');
        expect(todos.length).toBeLessThan(newTodos.length);
    });

如果我们现在运行我们的测试，我们会得到一个失败的测试！

.. image:: ./images/11-5.png

让我们尽可能地修复我们的错误。我们可以在Vue实例中添加一个名为 ``addTodo`` 的方法：

.. code-block:: js

    export default {
      methods: {
        addTodo(name) {},
      },
      // Omitted
    }

现在这一次我们得到一个新的错误，它声明它不能读取未定义的属性“长度”，基本上说我们没有 ``newTodos`` 数组：

.. image:: ./images/11-6.png

让我们使我们的 ``addTodo`` 函数返回一个数组，它将返回添加新的 ``todo`` 项的 ``todos`` 数组：

.. code-block:: js

    addTodo(name) {
      return [...this.todos, { name }]
    },

运行 ``npm test`` 后我们得到这个输出：

.. image:: ./images/11-7.png

当当！通过测试。

嗯。我确实记得我们所有的 ``todo`` 项都有 ``id`` ，但看起来不再是这种情况。

理想情况下，我们的服务器端数据库应该为我们处理 ``id`` 号码，但现在，我们可以使用 ``uuid`` 包在客户端生成 ``uuid`` 。让我们通过在终端中运行以下代码来安装它：

.. code-block:: shell

    $ npm install uuid

然后，我们可以编写测试用例来添加断言，即列表中的每个项目都有一个 ``id`` 属性：

.. code-block:: js

    it('should add an id to each todo item', () => {
      const wrapper = mount(TodoList);
      const todos = wrapper.vm.todos;
      const newTodos = wrapper.vm.addTodo('Go to work');

      newTodos.map(item => {
        expect(item.id).toBeTruthy();
      });
    });

正如你所看到的，我们终端输出有问题，这是因为我们明显没有一个 ``id`` 属性：

.. image:: ./images/11-8.png

让我们使用我们之前安装的 ``uuid`` 包来实现这个目标：

.. code-block:: js

    import uuid from 'uuid/v4';

    export default {
      methods: {
        addTodo(name) {
          return [...this.todos, { id: uuid(), name }];
        },
      },
      // Omitted
    };

然后，我们通过测试：

.. image:: ./images/11-9.png

从一个失败的测试开始有多个好处：

- 它确保我们的测试实际上正在运行，我们不花时间调试任何东西；
- 我们知道接下来需要实现什么，因为我们是受当前错误消息驱动；

然后，我们可以编写最低限度必要的代码以获得测试通过，并继续重构我们的代码，直到我们对我们的解决方案感到满意为止。在之前的测试中，我们可能写得更少以获得测试通过的结果，但为了简洁，我选择了更小的示例。

点击事件
--------
我们的方法可行，但这不是我们的用户与应用程序交互的方式。让我们看看是否可以使我们的测试考虑到用户输入表单和后续按钮：

.. code-block:: html

    <form @submit.prevent="addTodo(todoName)">
      <input type="text" v-model="todoName">
      <button type="submit">Submit</button>
    </form>

我们也可以对我们的 ``addTodo`` 函数进行一些小改动，确保为 ``this.todos`` 赋予新的 ``todo`` 项目的值：

.. code-block:: js

    addTodo(name) {
        this.todos = [...this.todos, { id: uuid(), name }];
        return this.todos;
    },

重要的是，通过做出这样的改变，我们可以检查我们以前的所有用例，并看到没有任何失败！ ``Hurray``  进行自动化测试！

接下来，让我们创建一个 ``it`` 块 ，我们可以用来断言，只要我们点击提交按钮，就会添加一个项目：

.. code-block:: js

    it('should add an item to the todo list when the button is clicked', () => {
        const wrapper = mount(TodoList);
    })

接下来，我们可以使用包装器的 ``find`` 获取表单元素，这样我们就可以触发一个事件。在我们提交表单时，我们将触发提交事件并将参数传递给我们的 ``submit`` 函数。然后我们可以断言我们的 ``todo`` 列表应该是 ``1`` ：

.. code-block:: js

    it('should add an item to the todo list when the button is clicked', () => {
        const wrapper = mount(TodoList);
        wrapper.find('form').trigger('submit', 'Clean the car');

        const todos = wrapper.vm.todos;

        expect(todos.length).toBe(1);
    })

我们还可以检查在提交表单时是否调用了合适的方法。让我们用 ``jest`` 来做到这一点：

.. code-block:: js

    it('should call addTodo when form is submitted', () => {
        const wrapper = mount(TodoList);
        const spy = jest.spyOn(wrapper.vm, 'addTodo');

        wrapper.find('form').trigger('submit', 'Clean the car');

        expect(wrapper.vm.addTodo).toHaveBeenCalled();
    });

测试事件
--------
我们已经取得了很多进展，但如果我们能够测试组件之间触发的事件，这不会更好吗？让我们通过创建一个 ``TodoInput`` 组件来查看：

.. code-block:: html

    <template>
    <form @submit.prevent="addTodo(todoName)">
        <input type="text" v-model="todoName">
        <button type="submit">Submit</button>
    </form>
    </template>

    <script>
    export default {
        data() {
            return {
                todoName: ''
            }
        },
        methods: {
            addTodo(name) {
                this.$emit('addTodo', name);
            }
        }
    }
    </script>

现在，这个组件中的 ``addTodo`` 方法触发一个事件。让我们在 ``TodoInput.spec.js`` 文件中测试该事件：

.. code-block:: js

    import { mount } from 'vue-test-utils';
    import TodoInput from '../TodoInput';

    describe('TodoInput.vue', () => {
        it('should fire an event named addTodo with todo name', () => {
            const mock = jest.fn()
            const wrapper = mount(TodoInput);

            wrapper.vm.$on('addTodo', mock)
            wrapper.vm.addTodo('Clean the car');

            expect(mock).toBeCalledWith('Clean the car')
        })
    });

我们在这个方法中引入了一个新概念 ``mock`` 。这使我们能够定义我们自己的行为，并随后确定哪个事件被调用。

无论何时触发 ``addTodo`` 事件，都会调用 ``mock`` 函数。这使我们能够看到我们的事件是否被调用，并确保事件带有有效载荷。

我们还可以确保 ``TodoList`` 处理此事件，但首先请确保您已更新 ``TodoList`` 以包含 ``TodoInput`` 表单：

.. code-block:: html

    <template>
        <div>
            <h1>TodoList</h1>

            <TodoInput @addTodo="addTodo($event)"></TodoInput>

            <ul>
                <TodoItem v-for="todo in todos" v-bind:key="todo.id" :todo="todo">{{todo.name}}</TodoItem>
            </ul>
        </div>
    </template>

    <script>
        import uuid from 'uuid/v4';

        import TodoItem from './TodoItem';
        import TodoInput from './TodoInput';

        export default {
            components: {
                TodoItem,
                TodoInput
            },
            data() {
                return {
                    todos: [],
                    todoName: ''
                };
            },
            methods: {
                addTodo(name) {
                    this.todos = [...this.todos, { id: uuid(), name }];
                    return this.todos;
                },
            },
        };
    </script>
    <style>
        ul,
        li {
            list-style: none;
            margin-left: 0;
            padding-left: 0;
        }
    </style>

然后，在我们的 ``TodoList.spec.js`` 中，我们可以通过导入 ``TodoInput`` 开始，然后添加以下内容：

.. code-block:: js

    import TodoInput from '../TodoInput';
    it('should call addTodo when the addTodo event happens', () => {
        const wrapper = mount(TodoList);

        wrapper.vm.addTodo = jest.fn();
        wrapper.find(TodoInput).vm.$emit('addTodo', 'Clean the car');

        expect(wrapper.vm.addTodo).toBeCalledWith('Clean the car');
    })

除此之外，我们还可以确保事件完成它应该做的事情；所以当我们触发事件时，它会向数组添加一个项目，我们正在测试数组长度：

.. code-block:: js

    it('adds an item to the todolist when the addTodo event happens', () => {
        const wrapper = mount(TodoList);
        wrapper.find(TodoInput).vm.$emit('addTodo', 'Clean the car');
        const todos = wrapper.vm.todos;
        expect(todos.length).toBe(1);
    });

使用Wallaby.js获得更好的测试体验
=================================
我们还可以使用 ``Wallaby.js`` 在我们的编辑器中实时查看我们的单元测试结果。这不是一个免费的工具，但您可能会发现它在创建测试驱动的 ``Vue`` 应用程序时很有用。我们首先克隆/下载已经设置好 ``Wallaby`` 的项目。在终端中运行以下内容：

.. code-block:: shell

    # Clone the repository
    $ git clone https://github.com/ChangJoo-Park/vue-wallaby-webpack-template

    # Change directory
    $ cd vue-wallaby-webpack-template

    # Install dependencies
    $ npm install

    # At the time of writing this package is missing eslint-plugin-node
    $ npm install eslint-plugin-node

    # Run in browser
    $ npm run dev

然后，我们可以在我们的编辑器中打开它，并在编辑器中安装 ``Wallaby.js`` 扩展。您可以在 https://wallabyjs.com/download/ 上找到支持的编辑器和说明列表。

我将在 ``Visual Studio Code`` 中安装它，首先在扩展 ``marketplace`` 中搜索 ``Wallaby`` ：

.. image:: ./images/11-10.png

然后，我们可以通过在 ``Mac`` 上按 ``CMD + SHIFT + =`` 或在 ``Windows`` 上按 ``CTRL + SHIFT + =`` 来告诉 ``Wallaby`` 有关该项目的配置文件（ ``wallaby.js`` ）。从下拉列表中，单击选择配置文件，然后键入 ``wallaby.js`` 。这将允许 ``Wallaby`` 和 ``Vue`` 一起工作。

要启动 ``Wallaby`` ，我们可以再次打开配置菜单并选择开始。然后，我们可以导航到 ``tests/unit/specs/Hello.spec.js`` 文件，我们应该在编辑器的行边界看到不同的块(用来指示测试结果)：

.. image:: ./images/11-11.png

当一切都是绿色时，我们知道它已经通过测试了！如果我们改变测试的实施细节怎么办？让我们故意让我们的一个或多个测试失败：

.. image:: ./images/11-12.png

除了'应该呈现正确的内容'块之外，所有东西都保持绿色，这可以在左边看到。这是因为我们现在有一个失败的断言，但更重要的是，我们不必重新运行测试，并且它们会立即显示在我们的编辑器中。在不同的窗口之间不再需要 ``Alt-Tab`` 来观看我们的测试输出！

总结
====
本章让我们了解如何适当测试我们的 ``Vue`` 组件。我们学会了如何遵循以失败为先的方法编写能够推动我们开发决策的测试，以及如何利用 ``Wallaby.js`` 在我们的编辑器中查看测试结果！

在下一章中，我们将学习如何将我们的 ``Vue.js`` 应用程序与现代的渐进式 ``Web`` 应用程序技术（如服务工作者，应用程序清单等）结合起来！

