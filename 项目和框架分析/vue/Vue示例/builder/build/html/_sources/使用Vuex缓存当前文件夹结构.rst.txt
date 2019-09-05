***************
使用Vuex缓存当前文件夹结构
***************

``Vuex`` 是一个专为 ``Vue.js`` 应用程序开发的状态管理模式。它采用集中式存储管理应用的所有组件的状态。在本章中，我们将介绍一个名为 ``Vuex`` 的官方 ``Vue`` 插件。 ``Vuex`` 是一种状态管理模式和库，允许您为所有 ``Vue`` 组件集中存储，而不管它们是子组件还是 ``Vue`` 组件。它使我们能够在整个应用程序中保持数据同步的集中，简单的方式。

本章将包括：

- ``Vuex`` 入门；
- 从 ``Vuex`` 存储和检索数据；
- 将 ``Vuex`` 与我们的 ``Dropbox`` 应用程序集成；
- 如果需要，缓存当前的 ``Dropbox`` 文件夹内容并从存储加载数据

取代在每个组件中要求定制事件和 ``$emit`` 函数，并试图让组件和子组件保持最新状态， ``Vue`` 应用程序的每个部分都可以更新中央存储库，而其他组件可以根据这些信息反应和更新其数据和状态。它还为我们提供了一个存储数据的通用位置，因此，我们可以使用 ``Vuex`` 存储，而不是试图决定将数据对象放在组件，父组件还是 ``Vue`` 实例上是否更具语义。

``Vuex`` 也集成到 ``Vue`` 开发工具中 - 这本书的最后一章，第12章，使用 ``Vue`` 开发工具和测试您的 ``SPA`` 涵盖了这些内容。随着库的集成，它使调试和查看存储的当前和过去的状态变得容易。开发工具反映了状态更改或数据更新，并允许您在存储的每个部分进行观察。

如前所述， ``Vuex`` 是一种状态管理模式，它是 ``Vue`` 应用程序数据的真实来源。例如，跟踪购物车或登录用户对于某些应用程序至关重要，并且如果此数据在组件之间不同步可能会造成严重破坏。在不使用父级来处理交换的情况下，在子组件之间传递数据也是不可能的。 ``Vuex`` 通过处理数据的存储，变异和操作来消除这种复杂性。

.. image:: ./images/vuex.png

对于我们的 ``Dropbox`` 应用程序， ``Vuex`` 存储可用于存储文件夹结构，文件列表和下载链接。 这意味着，如果用户不止一次访问同一文件夹，则无需查询 ``API`` ，因为所有信息都已存储。这将加快文件夹的导航。

包含和初始化Vuex
================
``Vuex`` 库与 ``Vue`` 本身一样。 您可以使用前面提到的 ``unpkg`` 服务（ ``https://unpkg.com/vuex`` ）使用托管版本，也可以从 ``https://github.com/vuejs/vuex`` 下载 ``JavaScript`` 库。

将一个新的 ``<script>`` 块添加到 ``HTML`` 文件的底部。确保 ``Vuex`` 库包含在 ``vue.js`` 库之后但在应用程序脚本之前：

.. code-block:: html

    <script type="text/javascript" src="js/vue.js"></script>
    <script type="text/javascript" src="js/vuex.js"></script>
    <script type="text/javascript" src="js/dropbox.js"></script>
    <script type="text/javascript" src="js/app.js"></script>

如果您正在部署一个包含多个 ``JavaScript`` 文件的应用程序，则值得研究将它们合并并压缩到一个文件或将您的服务器配置为使用 ``HTTP/2`` 推送是否更高效。

使用包含库，我们可以初始化和包含 ``store`` 到我们的应用程序内。 创建一个名为 ``store`` 的新变量并初始化 ``Vuex.Store`` 类，并将其分配给该变量：

.. code-block:: js

    const store = new Vuex.Store({});

在初始化 ``Vuex`` 存储后，我们现在可以使用它的 ``store`` 变量的功能。 使用 ``store`` ，我们可以访问其中的数据并使用 ``mutations`` 更改数据。 有了独立的 ``store`` ，许多 ``Vue`` 实例可以更新同一 ``store`` ; 这在某些情况下可能是需要的，但是，在其他情况下可能是不希望有的副作用。

为了避免这种情况，我们可以将 ``store`` 与特定的 ``Vue`` 实例相关联。 这是通过将 ``store`` 变量传递给我们的 ``Vue`` 类来完成的。 这样做也会将 ``store`` 实例注入到我们所有的子组件中。 虽然我们的应用不是严格要求的，但养成将 ``store`` 与应用相关联的习惯是一种很好的做法：

.. code-block:: js

    const app = new Vue({
        el: '#app',
        store,
        data: {
            path: ''
        },
        methods: {
            updateHash() {
                let hash = window.location.hash.substring(1);
                this.path = (hash || '');
            }
        },
        created() {
            this.updateHash()
        }
    });

通过添加 ``store`` 变量，我们现在可以使用 ``this.$store`` 变量访问我们组件中的 ``store`` 。

使用store
=========
为了帮助我们掌握如何使用 ``store`` ，让我们移动当前存储在父 ``Vue`` 实例上的 ``path`` 变量。在我们开始编写和移动代码之前，有一些短语和单词在使用 ``Vuex`` 存储时会有所不同，我们应该熟悉它们：

- ``state`` : 这是 ``sotre`` 对应的 ``data`` 对象；原始数据存储在该对象内。
- ``getters`` : 这些是 ``computed`` 值的 ``Vuex`` 等价物；该 ``store`` 的函数可以在将其返回以用于组件之前处理原始状态值。
- ``mutations`` : ``Vuex`` 不允许直接在 ``store`` 外修改 ``state`` 对象，这必须通过突变处理程序完成；这些是 ``store`` 的函数，用来更新状态。他们总是把 ``state`` 作为第一个参数。

以上对象直接属于 ``store`` 。但是，更新商店并不像调用 ``store.mutationName()`` 那么简单。相反，我们必须使用新的 ``commit()`` 函数来调用该方法。该函数接受两个参数： **突变的名称和传递给它的数据** 。

虽然起初很难理解，但 ``Vuex store`` 的详细特性使其具有强大的功能。使用来自第1章 ``Vue.js`` 入门的原始示例的存储实例如下所示：

.. code-block:: js

    const store = new Vuex.Store({
        state: {
            message: 'HelLO Vue!'
        },
        getters: {
            message: state => {
                return state.message.toLowerCase();
            }
        },
        mutations: {
            updateMessage(state, msg) {
                state.message = msg;
            }
        }
    });

前面的 ``store`` 示例包含 ``state`` 对象，它是我们的原始数据存储; 一个 ``getters`` 对象，其中包括我们对状态的处理；最后是一个 ``mutations`` 对象，它允许我们更新消息。 请注意 ``message getter`` 和 ``updateMessage`` 突变如何将 ``store`` 状态作为第一个参数。

要使用此 ``store`` ，您可以执行以下操作：

.. code-block:: js

    new Vue({
        el: '#app',
        store,
        computed: {
            message() {
                return this.$store.state.message
            },
            formatted() {
                return this.$store.getters.message
            }
        }
    });

检索消息
========
在 ``{{message}}`` 计算函数中，我们从状态对象中检索了未处理的原始消息，并使用了以下路径：

.. code-block:: js

    this.$store.state.message

这实际上是访问 ``store`` ，然后是 ``state`` 对象，然后是消息
对象键。

类似地， ``{{ formatted }}`` 计算值使用 ``store`` 中的 ``getter`` ，这会小写字符串。这是通过访问 ``getters`` 对象来获取的：

.. code-block:: js

    this.$store.getters.message

更新消息
========
为了更新消息，你需要调用 ``commit`` 函数。它接受方法名称作为第一个参数， ``data`` 作为第二个参数。第二个参数可以是一个简单变量，一个数组或者一个对象。

``store`` 中的 ``updateMessage`` 突变接受单个参数并将消息设置为等于该参数，因此要更新消息，代码将为：

.. code-block::  js

    store.commit('updateMessage', 'VUEX Store');

这可以在应用程序中的任何位置运行，并且会自动更新以前使用的值，因为它们都依赖于相同的 ``store`` 。

现在返回我们的 ``message``  ``getter`` 将返回 ``VUEX Store`` ，因为我们已经更新了状态。 考虑到这一点，让我们更新我们的应用程序，在 ``store`` 中而不是在 ``Vue`` 实例中存储 ``path`` 变量。

使用Vuex存储作为文件夹路径
==========================
为我们的全局 ``Dropbox path`` 变量使用 ``Vue store`` 的第一步是将 ``data`` 对象从 ``Vue`` 实例移动到 ``Store`` ，并将其重命名为 ``state`` ：

.. code-block::  js

    const store = new Vuex.Store({
        state: {
            path: ''
        }
    });

我们还需要创建一个 ``mutation`` 以允许从 ``URL`` 的散列更新 ``path`` 。 将一个 ``mutation`` 对象添加到 ``store`` 并从 ``Vue`` 实例移动 ``updateHash`` 函数 - 不要忘记更新函数以接受 ``store`` 作为第一个参数。 另外，更改方法，以便更新 ``state.path`` 而不是 ``this.path`` ：

.. code-block:: js

    const store = new Vuex.Store({
        state: {
            path: ''
        },
        mutations: {
            updateHash(state) {
                let hash = window.location.hash.substring(1);
                state.path = (hash || '');
            }
        }
    });

通过将 ``path`` 变量和 ``mutation`` 移动到 ``store`` ，它使 ``Vue`` 实例显着缩小，同时删除 ``methods`` 和 ``data`` 对象：

.. code-block:: js

    const app = new Vue({
        el: '#app',
        store,
        created() {
            this.updateHash()
        }
    });

我们现在需要更新我们的应用程序以使用 ``store`` 中的 ``path`` 变量，而不是 ``Vue`` 实例。我们还需要确保我们调用 ``store mutation`` 函数来更新路径变量，而不是 ``Vue`` 实例上的方法。

使用store commit更新路径方法
============================
从 ``Vue`` 实例开始，将 ``this.Updatehash`` 改为 ``store.commit('updateHash')`` 。 不要忘记同样需要在 ``onhashchange`` 函数中更新方法。 第二个函数应该在我们的 ``Vue`` 实例上引用 ``store`` 对象，而不是直接在 ``store`` 中引用。 这是通过访问 ``Vue`` 实例变量 ``app`` ，然后在这个实例中引用 ``Vuex store`` 来完成的。

当引用 ``Vue`` 实例上的 ``Vuex store`` 时， **无论其最初声明的变量名称如何** ，它都将保存在 ``$store`` 的变量下：

.. code-block:: js

    const app = new Vue({
        el: '#app',
        store,
        created() {
            store.commit('updateHash');
        }
    });
    window.onhashchange = () => {
        app.$store.commit('updateHash');
    }

使用path变量
============
我们现在需要更新组件以使用 ``store`` 中的 ``path`` ，而不是通过组件传递的路径。 需要更新 ``breadcrumb`` 和 ``dropbox-viewer`` 以接受这个新变量。 我们也可以从组件中删除不必要的 ``props`` 。

更新breadcrumb组件
------------------
从 ``HTML`` 中删除 ``:p`` prop，留下一个简单的面包屑 ``HTML`` 标签：

.. code-block:: html

    <breadcrumb></breadcrumb>

接下来，从 ``JavaScript`` 文件中的组件中移除 ``props`` 对象。 ``parts`` 变量也需要更新才能使用。使用 ``$store.state.path`` ，而不是 ``this.p`` ：

.. code-block:: js

    Vue.component('breadcrumb', {
        template: '<div>' +
            '<span v-for="(f, i) in folders">' +
                '<a :href="f.path">[F] {{ f.name }}</a>' +
                '<i v-if="i !== (folders.length - 1)"> &raquo; </i>' +
            '</span>' +
        '</div>',
        computed: {
            folders() {
                let output = [],
                    slug = '',
                    parts = this.$store.state.path.split('/');
                for (let item of parts) {
                    slug += item;
                    output.push({'name': item || 'home', 'path': '#' + slug});
                    slug += '/';
                }
                return output;
            }
        }
    });

更新dropbox-viewer组件以使用Vuex
--------------------------------
与 ``breadcrumb`` 组件一样，第一步是从视图中删除 ``HTML prop`` 。 这应该更加简化你的应用程序的视图：

.. code-block:: html

    <div id="app">
        <dropbox-viewer></dropbox-viewer>
    </div>

下一步是清理 ``JavaScript`` ，删除任何不必要的功能参数。 从 ``dropbox-viewer`` 组件中移除 ``props`` 对象。接下来，更新位于 ``getFolderStructure`` 内的 ``filesListFolder`` ``Dropbox`` 方法以使用存储路径，而不是使用路径变量：

.. code-block:: js

    this.dropbox().filesListFolder({
        path: this.$store.state.path,
        include_media_info: true
    })

由于此方法现在使用 ``store`` 而不是函数参数，因此我们可以从方法声明本身除去该变量，并将其从 ``updateStructure`` 方法中移除。例如：

.. code-block:: js

    updateStructure(path) {
        this.isLoading = true;
        this.getFolderStructure(path);
    }

这将成为以下内容：

.. code-block:: js

    updateStructure() {
        this.isLoading = true;
        this.getFolderStructure();
    }

但是，我们仍然需要将路径作为变量存储在此组件上。 这是由于我们的 ``watch`` 方法，它调用 ``updateStructure`` 函数。 **为此，我们需要将我们的路径存储为计算值，而不是固定变量。** 这是因为它可以在 ``store`` 更新时动态更新，而不是组件初始化时的固定值。

使用名为 ``path`` 的方法在 ``dropbox-viewer`` 组件上创建计算对象 - 这应该只是返回 ``store`` 中的 ``path`` ：

.. code-block:: js

    computed: {
        path() {
            return this.$store.state.path;
        }
    }

现在我们把它作为一个本地变量，所以 ``Dropbox`` 中 ``filesListFolder`` 方法可以更新为再一次使用 ``this.path`` 。

新更新的 ``dropbox-viewer`` 组件应该如下所示。 在浏览器中查看应用程序，应该看起来好像没有任何变化 - 但是，应用程序的内部工作现在依赖于新的 ``Vuex`` 存储，而不是存储在 ``Vue`` 实例上的变量：

.. code-block:: js

    Vue.component('dropbox-viewer', {
        template: '#dropbox-viewer-template',
        data() {
            return {
                accessToken: 'XXXX',
                structure: {},
                isLoading: true
            }
        },
        computed: {
            path() { // 相当于定义动态属性变量
                return this.$store.state.path
            }
        },
        methods: {
            dropbox() {
                return new Dropbox({
                    accessToken: this.accessToken
                });
            },
            getFolderStructure() {
                this.dropbox().filesListFolder({
                    path: this.path, // 可以直接引用变量
                    include_media_info: true
                })
                    .then(response => {
                        const structure = {
                            folders: [],
                            files: []
                        }
                        for (let entry of response.entries) {
                            // Check ".tag" prop for type
                            if(entry['.tag'] == 'folder') {
                                structure.folders.push(entry);
                            } else {
                                structure.files.push(entry);
                            }
                        }
                        this.structure = structure;
                        this.isLoading = false;
                    })
                    .catch(error => {
                        this.isLoading = 'error';
                        console.log(error);
                    });
            },
            updateStructure() {
                this.isLoading = true;
                this.getFolderStructure();
            }
        },
        created() {
            this.getFolderStructure();
        },
        watch: {
            path() { // 需要观察path变量
                this.updateStructure();
            }
        },
    });

缓存文件内容
============
现在我们的应用程序中已经有了 ``Vuex`` ，并且正在将它用于路径，我们可以开始着眼于存储当前显示的文件夹的内容，以便如果用户返回到同一位置，则不需要查询 ``API`` 检索结果。我们将通过存储 ``API`` 返回的对象到 ``Vuex`` 存储来完成此操作。

当文件夹被请求时，应用程序将检查数据是否存在于 ``store`` 中。如果是这样， ``API`` 调用将被省略，并从存储装载数据。如果它不存在， ``API`` 将被查询并将结果保存在 ``Vuex``  ``store`` 中。

第一步是将数据处理分解成它自己的方法。这是因为无论数据来自 ``store`` 还是 ``API`` ，文件和文件夹都需要分割。

在 ``dropbox-viewer`` 组件中创建一个名为 ``createFolderStructure()`` 的新方法，并移动该函数调用代码到 ``Dropbox filesListFolder`` 方法返回对象的 ``then()`` 函数内。

现在你的两种方法应该看起来像下面这样，你的应用程序仍然应该像以前一样工作：

.. code-block:: js

    createFolderStructure(response) {
        const structure = {
            folders: [],
            files: []
        }
        for (let entry of response.entries) {
            // Check ".tag" prop for type
            if(entry['.tag'] == 'folder') {
                structure.folders.push(entry);
            } else {
                structure.files.push(entry);
            }
        }
        this.structure = structure;
        this.isLoading = false;
    },
    getFolderStructure() {
        this.dropbox().filesListFolder({
            path: this.path,
            include_media_info: true
        })
            .then(this.createFolderStructure)
            .catch(error => {
                this.isLoading = 'error';
                console.log(error);
            });
    }

使用承若，我们可以使用 ``createFolderStructure`` 作为 ``API`` 回调的操作。

下一步是存储我们正在处理的数据。 为此，我们将利用将对象传递给 ``store`` 的 ``commit`` 函数并将该 ``path`` 用作存储对象中的键。 我们不是嵌套文件结构，而是将信息存储在一个扁平的结构中。 例如，我们浏览了几个文件夹之后，我们的 ``store`` 应该是这样的：

.. code-block:: js

    structure: {
        'images': [{...}],
         'images-holiday': [{...}],
         'images-holiday-summer': [{...}]
    }

将对路径进行多次转换，使其对 ``key`` 友好。它会被小写，任何标点符号都会被删除。 我们还将用连字符替换所有空格和斜杠。

首先，在 ``Vuex`` 存储 ``state`` 对象中创建一个标题为 ``structure`` 的空对象；这是我们要存储数据的地方：

.. code-block:: js

    state: {
        path: '',
        structure: {}
    }

我们现在需要创建一个新的 ``mutation`` ，以便我们在加载数据时存储数据。 在 ``mutations`` 对象内创建一个新函数。 称它为 ``structure`` ； 它需要接受 ``state`` 作为参数，加上一个 ``payload`` 变量，它将成为一个传入的对象：

.. code-block:: js

    structure(state, payload) {}

路径对象将由 ``path`` 变量和 ``API`` 返回的数据组成。 例如：

.. code-block:: js

    {
        path: 'images-holiday',
        data: [{...}]
    }

通过传入此对象，我们可以使用 ``path`` 作为键， ``data`` 作为值。
将数据与路径的关键字一起存储在变量中：

.. code-block:: js

    structure(state, payload) {
        state.structure[payload.path] = payload.data;
    }

我们现在可以在我们的组件中的新 ``createFolderStructure`` 方法的末尾提交这些数据：

.. code-block:: js

    createFolderStructure(response) {
        const structure = {
            folders: [],
            files: []
        }
        for (let entry of response.entries) {
            // Check ".tag" prop for type
            if(entry['.tag'] == 'folder') {
                structure.folders.push(entry);
            } else {
                structure.files.push(entry);
            }
        }
        this.structure = structure;
        this.isLoading = false;
        this.$store.commit('structure', {
            path: this.path,
            data: response
        });
    }

这将在浏览应用程序时存储每个文件夹的数据。 这可以通过在结构变体内添加一个 ``console.log(state.structure)`` 来验证。

虽然这和原来一样工作，但在将它用作对象中的键时 “清理路径” 是一种好的做法。为此，我们将删除任何标点符号，用连字符替换任何空格和斜杠，并将路径更改为小写。

在 ``dropbox-viewer`` 组件上创建一个名为 ``slug`` 的新计算函数。术语 ``slug`` 通常用于``URL`` 消毒。这个函数将运行几个 ``JavaScript replace`` 方法来创建一个安全的对象键：

.. code-block:: js

    slug() {
        return this.path.toLowerCase()
            .replace(/^\/|\/$/g, '')
            .replace(/ /g,'-')
            .replace(/\//g,'-')
            .replace(/[-]+/g, '-')
            .replace(/[^\w-]+/g,'');
    }

``slug`` 功能执行以下操作。 ``/images/iPhone/mom's Birthday - 40th``  将进行如下处理：

- 转换字符串为小写： ``/images/iphone/mom's birthday - 40th`` ；
- 移除路径开始和结尾的斜线： ``images/iphone/mom birthday -
40th`` ；
- 使用连字符替换所有空格： ``images/iphone/mom-birthday---40th`` ；
- 使用连字符替换所有斜线： ``images-iphone-mom-birthday---40th`` ；
- 使用单个连字符替换多个连续连字符： ``images-iphone-mom-birthday-
40th`` ；
- 最后，移除任何的标点符号： ``images-iphone-moms-birthday-40th`` ；

使用现在创建的 ``slug`` ，我们可以在存储数据时使用它作为键：

.. code-block:: js

    this.$store.commit('structure', {
        path: this.slug,
        data: response
    });

通过将我们的文件夹内容缓存到 ``Vuex store`` 中，我们可以添加一个检查来查看 ``store`` 中是否存在数据，如果存在，请从此处加载它。

如果存在从存储中加载数据
------------------------
从 ``store`` 加载我们的数据需要对我们的代码进行一些更改。 第一步是检查 ``store`` 中是否存在结构，如果存在，则加载它。第二步是如果它是新数据，则将数据提交到存储 - 调用现有的 ``createFolderStructure`` 方法将更新结构，但也将数据重新提交到存储。尽管不会对用户造成不利影响，但当您的应用程序增长时，不必要地将数据写入 ``store`` 可能会导致问题。当我们来预缓存的文件夹和文件时，这也将帮助我们解决该问题。


从存储中加载数据
^^^^^^^^^^^^^^^^
由于 ``store`` 是 JavaScript 对象，我们的 ``slug`` 变量是我们组件的一致计算值，因此我们可以使用 ``if`` 语句检查对象键是否存在：

.. code-block:: js

    if(this.$store.state.structure[this.slug]) {
    // The data exists
    }

这使我们可以灵活地使用 ``createFolderStructure`` 方法从 ``store`` 中加载数据（如果存在），如果不存在，则触发 ``Dropbox API`` 调用。

更新 ``getFolderStructure`` 方法以包含 ``if`` 语句，并在数据存在时添加方法调用：

.. code-block:: js

    getFolderStructure() {
        if(this.$store.state.structure[this.slug]) {
            this.createFolderStructure(this.$store.state.structure[this.slug]);
        } else {
            this.dropbox().filesListFolder({
                path: this.path,
                include_media_info: true
            })
                .then(this.createFolderStructure)
                .catch(error => {
                    this.isLoading = 'error';
                    console.log(error);
                });
        }
    }

数据的路径很长，可能导致我们的代码无法读取。为了便于理解，将数据分配给一个变量，该变量允许我们检查它是否存在，并用更干净，更小，更少重复的代码返回数据。 这也意味着如果我们的数据路径发生变化，我们只需更新一行：

.. code-block:: js

    getFolderStructure() {
        let data = this.$store.state.structure[this.slug];
        if(data) {
            this.createFolderStructure(data);
        } else {
            this.dropbox().filesListFolder({
                path: this.path,
                include_media_info: true
            })
                .then(this.createFolderStructure)
                .catch(error => {
                    this.isLoading = 'error';
                    console.log(error);
                });
        }
    }

只存储新数据
^^^^^^^^^^^^
如前所述，当前的 ``createFolderStructure`` 方法既显示结构，也缓存应用程序中的响应，从而即使从缓存中加载数据时也重新保存结构。

创建一个新的方法，一旦数据加载，创建 ``Dropbox API`` 将触发的该方法。 称它为 ``createStructureAndSave`` 。 这应该接受响应变量作为其唯一参数：

.. code-block:: js

    createStructureAndSave(response) {}

现在我们可以将 ``store commit`` 函数从 ``createFolderStructure`` 方法移动到这个新的函数中，同时调用以使用数据激发现有方法：

.. code-block:: js

    createStructureAndSave(response) {
        this.createFolderStructure(response)
        this.$store.commit('structure', {
            path: this.slug,
            data: response
        });
    }

最后，更新 ``Dropbox API`` 函数以调用此方法：

.. code-block:: js

    getFolderStructure() {
        let data = this.$store.state.structure[this.slug];
        if(data) {
            this.createFolderStructure(data);
        } else {
            this.dropbox().filesListFolder({
                path: this.path,
                include_media_info: true
            })
                .then(this.createStructureAndSave)
                .catch(error => {
                    this.isLoading = 'error';
                    console.log(error);
                });
        }
    },

在浏览器中打开您的应用程序并浏览文件夹。当您使用面包屑导航后退时，响应应该更快 - 因为它现在从您创建的缓存中加载，而不是每次查询 ``API`` 。

在第7章“预先缓存其他文件夹和文件用于更快导航”中，我们将讨论预先缓存文件夹以尝试并抢占用户下一步的位置。我们也会考虑缓存这些文件的下载链接。

我们的完整应用程序 ``JavaScript`` 现在应该如下所示：

.. code-block:: js

    Vue.component('breadcrumb', {
        template: '<div>' +
            '<span v-for="(f, i) in folders">' +
                '<a :href="f.path">[F] {{ f.name }}</a>' +
                '<i v-if="i !== (folders.length - 1)"> &raquo; </i>' +
            '</span>' +
        '</div>',
        computed: {
            folders() {
                let output = [],
                    slug = '',
                    parts = this.$store.state.path.split('/');
                for (let item of parts) {
                    slug += item;
                    output.push({'name': item || 'home', 'path': '#' + slug});
                    slug += '/';
                }
                return output;
            }
        }
    });

    Vue.component('folder', {
        template: '<li><strong><a :href="\'#\' + f.path_lower">{{ f.name }}</a></strong></li>',
        props: {
            f: Object
        }
    });

    Vue.component('file', {
        template: '<li><strong>{{ f.name }}</strong><span v-if="f.size"> - {{ bytesToSize(f.size) }}</span> - <a v-if="link" :href="link">Download</a></li>',
        props: {
            f: Object,
            d: Object
        },
        data() {
            return {
                byteSizes: ['Bytes', 'KB', 'MB', 'GB', 'TB'],
                link: false
            }
        },
        methods: {
            bytesToSize(bytes) {
                // Set a default
                let output = '0 Byte';
                // If the bytes are bigger than 0
                if (bytes > 0) {
                    // Divide by 1024 and make an int
                    let i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
                    // Round to 2 decimal places and select the appropriate unit from the array
                    output = Math.round(bytes / Math.pow(1024, i), 2) + ' ' + this.byteSizes[i];
                }
                return output;
            }
        },
        created() {
            this.d.filesGetTemporaryLink({path: this.f.path_lower}).then(data => {
                this.link = data.link;
            });
        },
    });

    Vue.component('dropbox-viewer', {
        template: '#dropbox-viewer-template',
        data() {
            return {
                accessToken: 'XXXX',
                structure: {},
                isLoading: true
            }
        },
        computed: {
            path() {
                return this.$store.state.path
            },
            slug() {
                return this.path.toLowerCase()
                    .replace(/^\/|\/$/g, '')
                    .replace(/ /g,'-')
                    .replace(/\//g,'-')
                    .replace(/[-]+/g, '-')
                    .replace(/[^\w-]+/g,'');
            }
        },
        methods: {
            dropbox() {
                return new Dropbox({
                    accessToken: this.accessToken
                });
            },
            createFolderStructure(response) {
                const structure = {
                    folders: [],
                    files: []
                }
                for (let entry of response.entries) {
                    // Check ".tag" prop for type
                    if(entry['.tag'] == 'folder') {
                        structure.folders.push(entry);
                    } else {
                        structure.files.push(entry);
                    }
                }
                this.structure = structure;
                this.isLoading = false;
            },
            createStructureAndSave(response) {
                this.createFolderStructure(response)
                this.$store.commit('structure', {
                    path: this.slug,
                    data: response
                });
            },
            getFolderStructure() {
                let data = this.$store.state.structure[this.slug];
                if(data) {
                    this.createFolderStructure(data);
                } else {
                    this.dropbox().filesListFolder({
                        path: this.path,
                        include_media_info: true
                    })
                        .then(this.createStructureAndSave)
                        .catch(error => {
                            this.isLoading = 'error';
                            console.log(error);
                        });
                }
            },
            updateStructure() {
                this.isLoading = true;
                this.getFolderStructure();
            }
        },
        created() {
            this.getFolderStructure();
        },
        watch: {
            path() {
                this.updateStructure();
            }
        },
    });

    const store = new Vuex.Store({
        state: {
            path: '',
            structure: {}
        },
        mutations: {
            updateHash(state) {
                let hash = window.location.hash.substring(1);
                state.path = (hash || '');
            },
            structure(state, payload) {
                state.structure[payload.path] = payload.data;
            }
        }
    });

    const app = new Vue({
        el: '#app',
        store,
        created() {
            store.commit('updateHash');
        }
    });

    window.onhashchange = () => {
        app.$store.commit('updateHash');
    }

总结
====
在本章之后，您的应用程序现在应该与 ``Vuex`` 集成并缓存 ``Dropbox`` 文件夹的内容。 ``Dropbox`` 文件夹路径也应该利用 ``store`` 来提高应用程序的效率。 我们也只在需要时才查询 ``API`` 。

在第7章“预先缓存其他文件夹和文件以实现更快导航”中，我们将考虑预先缓存文件夹 - 提前主动查询 ``API`` 以加快应用程序导航和可用性。
