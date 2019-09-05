*****************************
浏览文件树和通过URL加载文件夹
*****************************
在第4章“使用 ``Dropbox API`` 获取文件列表”中，我们创建了一个列出指定 ``Dropbox`` 文件夹的文件和文件夹内容的应用程序。我们现在需要让我们的应用程序易于浏览。 这意味着用户将能够单击文件夹名称来导航并列出其中的内容，并使用户能够下载该文件。

在继续之前，请确保您的 ``HTML`` 中包含 ``Vue`` 和 ``Dropbox JavaScript`` 文件。

在本章中，我们将是：

- 为文件和文件夹创建组件；
- 向文件夹组件添加链接以更新目录列表；
- 将下载按钮添加到文件组件；
- 创建一个面包屑组件，以便用户可以轻松导航到树；
- 动态更新浏览器 ``URL`` ，因此如果文件夹作为书签或共享链接，文件夹能被正确的加载；

分离文件和文件夹
================
在我们创建组件之前，我们需要在我们的结构中分离我们的文件和文件夹，以便我们可以轻松识别和显示我们的不同类型。感谢每个项中的 ``.tag`` 属性，我们可以分割我们的文件夹和文件。

首先，我们需要将我们的 ``structure`` 数据属性更新为包含 ``files`` 和 ``folders`` 数组的对象：

.. code-block:: js

    data() {
        return {
            accessToken: 'XXXX',
            structure: {
                files: [],
                folders: []
            },
            byteSizes: ['Bytes', 'KB', 'MB', 'GB', 'TB'],
            isLoading: true
        }
    }

这使我们能够将我们的文件和文件夹附加到不同的数组，这意味着我们可以在我们的视图中以不同的方式显示它们。

下一步是用当前文件夹的数据填充这些数组。以下所有代码都发生在 ``getFolderStructure`` 方法的第一个 ``then()`` 函数中。

创建一个 JavaScript 循环来遍历条目并检查项目的 ``.tag`` 属性。 如果它等于 ``folder`` ，请将其附加到 ``structure.folder`` 数组，否则，将其添加到 ``structure.files`` 数组中：

.. code-block:: js

    getFolderStructure(path) {
        this.dropbox().filesListFolder({
            path: path,
            include_media_info: true
        })
            .then(response => {
                for (let entry of response.entries) {
                    // Check ".tag" prop for type
                    if(entry['.tag'] === 'folder') {
                        this.structure.folders.push(entry);
                    } else {
                        this.structure.files.push(entry);
                    }
                }
                this.isLoading = false;
            })
            .catch(error => {
                console.log(error);
            });
    },

此代码循环显示条目，如同我们在视图中一样，并检查 ``.tag`` 属性。 由于属性本身以 ``.`` 开头，因此我们无法像使用对象样式表示法那样访问属性，例如，对于 ``name`` - ``entry.name`` 。然后，我们使用 JavaScript ``push`` 将条目追加到文件或文件夹数组中。

为了显示这个新数据，我们需要更新视图以循环两种类型的数组。
这是使用 ``<template>`` 标记的完美用例，因为我们希望将这两个数组追加到同一个无序列表中。

更新视图以分别列出两个数组。我们可以从文件夹显示部分删除大小选项，因为它永远不会使用大小属性：

.. code-block:: js

    <ul v-if="!isLoading">
        <template v-for="entry in structure.folders">
            <li>
                <strong>{{entry.name }}</strong>
            </li>
        </template>
        <template v-for="entry in structure.files">
            <li>
                <strong>{{ entry.name }}</strong>
                <span v-if="entry.size">- {{ bytesToSize(entry.size) }}</span>
            </li>
        </template>
    </ul>

现在，我们有机会为这两种类型创建组件。

创建文件和文件夹组件
====================
随着我们的数据类型分离出来，我们可以创建单独的组件来划分数据和方法。 创建一个接受单个属性的 ``folder`` 组件，允许传递 ``folder`` 对象变量。 由于模板非常小，因此不需要基于视图或 ``<script>`` 块的模板；相反，我们可以将它作为字符串传递给组件：

.. code-block:: js

    Vue.component('folder', {
        template: '<li><strong>{{ f.name }}</strong></li>',
        props: {
            f: Object
        },
    });

为了使我们的代码更小，重复性更低，道具被称为 ``f`` 。 这样整理视图并让组件名称决定显示类型，而不必多次重复 ``folder`` 单词。

更新视图以使用文件夹组件，并将 ``entry`` 变量传递给 ``f`` 属性：

.. code-block:: html

    <template v-for="entry in structure.folders">
        <folder :f="entry"></folder>
    </template>

通过创建一个 ``file`` 组件来重复这个过程。在创建 ``file`` 组件时，我们可以从父级 ``dropboxviewer`` 组件移动 ``bytesToSize`` 方法和 ``byteSizes`` 数据属性到本组件中，因为它只会在显示文件时使用：

.. code-block:: js

    Vue.component('file', {
        template: '<li><strong>{{ f.name }}</strong><span v-if="f.size"> - {{ bytesToSize(f.size) }}</span> </li>',
        props: {
            f: Object
        },
        data() {
            return {
                byteSizes: ['Bytes', 'KB', 'MB', 'GB', 'TB']
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
        }
    });

再一次，我们可以使用 ``f`` 作为 ``prop`` 名称来减少重复（以及我们的应用程序的文件大小）。再次更新视图以使用这个新组件：

.. code-block:: html

    <template v-for="entry in structure.files">
        <file :f="entry"></file>
    </template

链接文件夹并更新结构
====================
现在我们已将文件夹和文件分开，我们可以将文件夹名称转换为链接。 这些链接将更新结构以显示所选文件夹的内容。 为此，我们将使用每个文件夹中的 ``path_lower`` 属性来构建链接目标。

为每个文件夹 ``name`` 创建一个动态链接，链接到文件夹的 ``path_lower`` 。 随着我们对 ``Vue`` 越来越熟悉， ``v-bind`` 属性已缩短为冒号符号：

.. code-block:: js

    Vue.component('folder', {
        template: '<li><strong><a :href="f.path_lower">{{ f.name }}</a></strong></li>',
        props: {
            f: Object
        },
    });

我们现在需要为此链接添加 ``click`` 侦听器。 点击后，我们需要触发 ``dropbox-viewer`` 组件上的 ``getFolderStructure`` 方法。 虽然 ``click`` 方法将在每个实例上使用 ``f`` 变量来获取数据，但最好将 ``href`` 属性设置为文件夹 ``URL`` 。

使用我们在本书前面章节中学到的知识，在 ``folder`` 组件上创建一个方法，该方法在触发时将文件夹路径发送到父组件。 ``dropbox-viewer`` 组件还需要一个新的方法来在启动时用给定的参数更新结构。

在 ``folder`` 组件上创建新方法并将 ``click`` 事件添加到文件夹链接。 与 ``v-bind`` 指令一样，我们现在使用以 ``@`` 符号表示的 ``v-on`` 的简写符号：

.. code-block:: js

    Vue.component('folder', {
        template: '<li><strong><a @click.prevent="navigate()" :href="f.path_lower">{{ f.name }}</a></strong></li>',
        props: {
            f: Object
        },
        methods: {
            navigate() {
                this.$emit('path', this.f.path_lower);
            }
        }
    });

除了定义 ``click`` 事件之外，还添加了事件修改器。 在 ``click`` 事件之后使用 ``.prevent`` 将 ``preventDefault`` 添加到链接操作中，这会阻止链接实际上转到指定的 ``URL`` ，而是让 ``click`` 方法处理所有内容。更多的事件修饰符和关于它们的细节可以在 ``Vue`` 中找到文档。

单击时，会触发导航方法，该方法使用 ``path`` 事件发出文件夹的小写路径。

既然我们已经有了我们的 ``click`` 处理程序和变量被发射，我们需要更新视图来触发父 ``Dropbox-viewer`` 组件的方法：

.. code-block:: html

    <template v-for="entry in structure.folders">
        <folder :f="entry" @path="updateStructure"></folder>
    </template>

在 ``Dropbox`` 组件上创建一个与 ``v-on`` 属性值相同的新方法，在本例中为 ``updateStructure`` 。这个方法将有一个参数，这是我们之前发射的路径。 从这里，我们可以使用路径变量来触发原始的 ``getFolderStructure`` 方法：

.. code-block:: js

    updateStructure(path) {
        this.getFolderStructure(path);
    }

在浏览器中查看我们的应用程序现在应该列出文件夹和链接，点击后显示新文件夹的内容。

但是，这样做时会引发一些问题。首先，文件和文件夹被追加到现有列表中，而不是替换它。其次，没有反馈给用户该应用正在加载下一个文件夹。

第一个问题可以通过在追加新结构之前清除文件夹和文件数组来解决。第二个可以通过利用我们在应用程序开始时使用的加载屏幕来解决 - 这会给用户一些反馈。

要解决第一个问题，请在 ``getFolderStructure`` 方法的 ``promise`` 成功函数内创建一个新的 ``structure`` 对象。该对象应该复制数据对象中的 ``structure`` 对象。这应该为文件和文件夹设置空白数组。更新 ``for`` 循环以使用本地结构数组而不是组件的。最后，使用新版本更新组件 ``structure`` 对象，包括更新的文件和文件夹：

.. code-block:: js

    getFolderStructure(path) {
        this.dropbox().filesListFolder({
            path: path,
            include_media_info: true
        }).then(response => {
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
                console.log(error);
            });
    }

由于该方法在应用程序挂载并创建自己的结构对象版本时被调用，因此不需要在 ``data`` 函数中声明数组。更新数据对象且将结构属性初始化为对象：

.. code-block:: js

    data() {
        return {
            accessToken: 'XXXX',
            structure: {},
            isLoading: true
        }
    }

现在运行应用程序将渲染文件列表，当单击新文件夹时将清除和更新文件列表。 为了给用户一些反馈，让他们知道应用程序正在工作，让我们在每次点击后切换加载屏幕。

然而，在我们这样做之前，让我们充分了解延迟来自何处以及哪里最适合触发加载屏幕。

链接上的点击是即时的，这会触发文件夹组件上的导航方法，然后触发 ``Dropbox`` 组件上的 ``updateStructure`` 方法。当应用程序到达 ``getFolderStructure`` 方法内的 ``Dropbox`` 实例上的 ``filesListFolder`` 函数时，延迟就会到来。因为我们可能想在以后触发 ``getFolderStucture`` 方法而不触发加载屏幕，请在 ``updateStructure`` 方法中将 ``isLoading`` 变量设置为 ``true`` ：

.. code-block:: js

    updateStructure(path) {
        this.isLoading = true;
        this.getFolderStructure(path);
    }

使用动画后，在浏览文件夹时，应用程序会在加载屏幕和文件夹结构之间淡出。

创建当前路径的面包屑
====================
在浏览文件夹或任何类型的嵌套结构时，可以使用面包屑总是很好的，这样用户就知道他们在哪里，他们走了多远，还可以轻松地返回到上一个文件夹。我们将为面包屑创建一个组件，因为它将包含各种属性，计算函数和方法。

面包屑组件会将每个文件夹深度列出为文件夹图标的链接。点击链接会将用户直接带到该文件夹 - 即使它有几层。为了达到这个目的，我们需要有一个我们可以循环的链接列表，每个链接都有两个属性 - 一个是文件夹的完整路径，另一个是文件夹名称。

例如，如果我们有 ``/images/holiday/summer/iphone`` 的文件夹结构，我们希望能够点击 ``holiday`` 并导航到 ``/images/holiday`` 。

现在创建你的面包屑组件 - 向模板属性添加一个空的 ``<div>`` ：

.. code-block:: js

    Vue.component('breadcrumb', {
        template: '<div></div>'
    });

将组件添加到您的视图。我们希望 ``breadcrumb`` 在结构列表中淡入淡出，所以我们需要调整 ``HTML`` 以将列表和面包屑组件包装在具有 ``v-if`` 声明的容器中：

.. code-block:: html

    <transition name="fade">
        <div v-if="!isLoading">
            <breadcrumb></breadcrumb>
            <ul>
                <template v-for="entry in structure.folders">
                    <folder :f="entry" @path="updateStructure"> </folder>
                </template>
                <template v-for="entry in structure.files">
                    <file :f="entry"></file>
                </template>
            </ul>
        </div>
    </transition>

我们现在需要为我们提供一个存储当前文件夹路径的变量。 然后我们可以在面包屑组件中操作这个变量。 这将在 ``Dropbox`` 组件上存储和更新，并传递给 ``breadcrumb`` 组件。 在 ``dropbox-viewer`` 组件上创建一个名为 ``path`` 的新属性：

.. code-block:: js

    data() {
        return {
            accessToken: 'XXXXX',
            structure: {},
            isLoading: true,
            path: ''
        }
    }

我们现在需要确保每当从 ``Dropbox API`` 检索结构时都会更新此路径。 在 ``getFolderStructure`` 方法内在 ``isLoading`` 变量被禁用之前执行此操作。 这可确保只有在结构加载完成后以及文件和文件夹显示之前才会更新它：

.. code-block:: js

    getFolderStructure(path) {
        this.dropbox().filesListFolder({
            path: path,
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
                this.path = path;
                this.structure = structure;
                this.isLoading = false;
            })
            .catch(error => {
                console.log(error);
            });
    },

现在我们有一个用当前路径填充的变量，我们可以将它作为 ``prop`` 传递给面包屑组件。 添加一个新的属性到 ``path`` 变量作为值的面包屑：

.. code-block:: html

    <breadcrumb :p="path"></breadcrumb>

更新组件以接受 ``prop`` 作为字符串：

.. code-block:: js

    Vue.component('breadcrumb', {
        template: '<div></div>',
        props: {
            p: String
        }
    });

``p`` 属性现在包含我们所在的完整路径（例如 ``/images/holiday/summer`` ）。 我们想分解这个字符串，以便我们可以识别文件夹名称并为组件渲染构建面包屑。

在组件上创建一个计算对象并创建一个名为 ``folders()`` 的新函数。 这将为我们创建面包屑数组以在模板中循环访问：

.. code-block:: js

    computed: {
        folders() {
        }
    }

我们现在需要设置一些变量供我们使用。 创建一个名为 ``output`` 的新的空数组。 这是我们要建立我们的面包屑的地方。我们还需要一个名为 ``slug`` 的空字符串变量。 ``slug`` 变量指的是 ``URL`` 的一部分，其使用受 ``WordPress`` 的欢迎。 最后一个变量是作为数组创建的路径。正如我们所知道的，每个文件夹都被一个 ``/`` 分开，我们可以使用它来分解或分割字符串到不同的部分：

.. code-block:: js

    computed: {
        folders() {
            let output = [],
                slug = '',
                parts = this.p.split('/');
        }
    }

如果我们要查看 ``Summer""`` 文件夹的部分变量，它将如下所示：

.. code-block:: js

    ['images', 'holiday', 'summer']

我们现在可以遍历数组来创建面包屑。 每个面包屑项目将成为具有单个文件夹 ``name``  的对象，例如 ``holiday`` 或 ``summer`` ，以及 ``slug`` ，前者为 ``/images/holiday/`` 后者为 ``/images/holiday/summer`` 。

每个对象都将被构造并添加到 ``output`` 数组中。 然后我们可以返回供我们的模板使用的 ``output`` ：

.. code-block:: js

    folders() {
        let output = [],
            slug = '',
            parts = this.p.split('/');
        for (let item of parts) {
            slug += item;
            output.push({'name': item, 'path': slug});
            slug += '/';
        }
        return output;
    }

此循环通过执行以下步骤来创建我们的面包屑。对于这个例子，我们假设我们在 ``/images/holiday`` 文件夹中：

1. ``parts`` 现在将成为一个包含三个项目的数组，[''，'images'，'holiday']。如果您分割的字符串从您要分割的项目开始，则会将空项目作为第一项。
2. 在循环开始时，第一个 ``slug`` 变量将等于 ``''`` ，因为它是第一个项目。
3. ``output`` 数组将在最后一个位置添加一个新项目，即 ``{'name':''，'path':''}`` 的对象。
4. ``slug`` 变量然后将 ``/`` 添加到最后。
5. 循环到下一个项目， ``slug`` 变量添加当前项( ``images`` )的名称。
6. 现在输出添加了一个新对象，值为 ``{'name':'images'，'path':'/images'}`` 。
7. 对于最后一个项，另一个 ``/`` 将随着下一个名称， ``holiday`` 一起被添加。
8. ``output`` 获取添加的最后一个对象，值为 ``{'name':'holiday'，'path':'/images/holiday'}`` - 注意路径正在建立，而名称仍然是单个文件夹名称。

我们现在有我们可以在视图中循环的面包屑输出数组。

在我们追加到输出数组之后添加斜杠的原因是， ``API`` 声明要获取 ``Dropbox`` 的根，我们传入一个空字符串，而所有其他路径必须以 ``/`` 开头。

下一步是将面包屑输出到我们的视图中。 由于这个模板很小，我们将使用多行 ``JavaScript`` 表示法。循环遍历 ``folders`` 计算变量中的项目，输出每个项的链接。 不要忘记在所有链接中保留一个包含元素：

.. code-block:: js

    template: '<div>' +
        '<span v-for="f in folders">' +
        '<a :href="f.path">{{ f.name }}</a>' +
        '</span>' +
        '</div>'

在浏览器中渲染这个应用程序应该会显示一个面包屑 - 虽然有点挤在一起并且缺少一个 ``home`` 链接（因为第一个项没有名称）。回到 ``folders`` 函数并添加一条 ``if`` 语句 - 检查该条目是否有名称，如果不是，则添加一个硬编码值：

.. code-block:: js

    folders() {
        let output = [],
            slug = '',
            parts = this.p.split('/');
        console.log(parts);
        for (let item of parts) {
            slug += item;
            output.push({'name': item || 'home', 'path': slug});
            slug += '/';
        }
        return output;
    }

另一种选择是在模板本身中添加 ``if`` 语句：

.. code-block:: js

    template: '<div>' +
        '<span v-for="f in folders">' +
        '<a :href="f.path">{{ f.name || 'Home' }}</a>' +
        '</span>' +
        '</div>'

如果我们想要在文件夹名称之间显示一个分隔符，例如斜杠或 ``>`` 形符号，这可以轻松添加。但是，当我们想在链接之间显示分隔符时，会出现一个小小的障碍，但不会在开始或结束时显示。为了解决这个问题，我们将在循环中利用可用的 ``index`` 关键字。 然后我们将这个与数组长度进行比较，并对元素进行 ``v-if`` 声明。

在循环数组时， ``Vue`` 允许您使用另一个变量。 这是默认情况下的索引（数组中项目的位置）；但是，如果您的数组以键/值方式构造，则索引可能会设置为一个值。 如果是这种情况，您仍然可以通过添加第三个变量来访问索引。 由于我们的数组是一个简单的列表，我们可以轻松使用这个变量：

.. code-block:: js

    template: '<div>' +
        '<span v-for="(f, i) in folders">' +
            '<a :href="f.path">{{ f.name || 'Home' }}</a>' +
            '<span v-if="i !== (folders.length - 1)"> »
            </span>' +
        '</span>' +
    '</div>',

将 ``f`` 变量更新为一对包含 ``f`` 和 ``i`` 的括号，逗号分隔。
``f`` 变量是循环中的当前文件夹，而已创建的变量 ``i`` 是该项目的索引。 请记住，数组索引从 0 开始而不是 1 。

我们添加的分隔符包含在带有 ``v-if`` 属性的 ``span`` 标签中，其中的内容可能看起来很混乱。这会使当前索引与 ``folder`` 数组的长度（有多少项）减 1 相混淆。 -1 是因为索引从 0 开始，而不是 1 ，正如您所期望的那样。如果数字不匹配，则会显示 ``span`` 元素。

我们需要做的最后一件事是让我们的面包屑导航到选定的文件夹。 我们可以通过调整我们为 ``folder`` 组件编写的导航功能来做到这一点。

但是，因为我们的整个组件都是面包屑而不是每个单独的链接，所以我们需要对其进行修改，以便接受参数。

通过将 ``click`` 事件添加到链接开始，传入 ``folder`` 对象：

.. code-block:: js

    template: '<div>' +
        '<span v-for="(f, i) in folders">' +
            '<a @click.prevent="navigate(f)" :href="f.path"> {{ f.name || 'Home' }}</a>' +
            '<i v-if="i !== (folders.length - 1)"> &raquo; </i>' +
        '</span>' +
    '</div>',

接下来，在面包屑组件上创建导航方法，确保接受文件夹参数并发出路径：

.. code-block:: js

    methods: {
        navigate(folder) {
            this.$emit('path', folder.path);
        }
    }

最后一步是在路径被发送时触发父方法。 为此，我们可以在 ``dropbox-viewer`` 组件上使用相同的 ``updateStructure`` 方法：

.. code-block:: html

    <breadcrumb :p="path" @path="updateStructure"></breadcrumb>

我们现在有一个完全可操作的面包屑导航，它允许用户使用文件夹链接浏览文件夹结构并通过面包屑链接备份。

我们的完整的面包屑组件看起来像是：

.. code-block:: js

    Vue.component('breadcrumb', {
        template: '<div>' +
            '<span v-for="(f, i) in folders">' +
                '<a @click.prevent="navigate(f)" :href="f.path">{{ f.name || 'Home' }}</a>' +
                '<i v-if="i !== (folders.length - 1)"> » </i>' +
            '</span>' +
        '</div>',
        props: {
        p: String
        },
        computed: {
            folders() {
                let output = [],
                    slug = '',
                    parts = this.p.split('/');
                console.log(parts);
                for (let item of parts) {
                    slug += item;
                    output.push({'name': item || 'home', 'path': slug});
                    slug += '/';
                }
                return output;
            }
        },
        methods: {
            navigate(folder) {
                this.$emit('path', folder.path);
            }
        }
    });

添加下载文件功能
================
现在我们的用户可以浏览文件夹结构，我们需要添加下载文件的功能。 不幸的是，这并不像访问文件上的链接属性那么简单。 要获得下载链接，我们必须为每个文件查询 ``Dropbox API`` 。

我们将在创建文件组件时查询 ``API`` ，这将异步获取下载链接，并显示一次可用的链接。 在我们做这件事之前，我们需要让 ``Dropbox`` 实例对文件组件可用。

向视图中的文件组件添加一个新属性，并将 ``Dropbox`` 方法作为值传入：

.. code-block:: html

    <file :d="dropbox()" :f="entry"></file>

将 ``d`` 变量作为对象添加到接受组件的 ``props`` 对象中：

.. code-block:: js

    props: {
        f: Object,
        d: Object
    },

我们现在要添加 ``link`` 到数据属性。这应该在默认情况下设置为 ``false`` ，因此我们可以隐藏链接，并且一旦 ``API`` 返回值，我们就会用它填充下载链接。

将 ``created()`` 函数添加到文件组件，并在里面添加 ``API`` 调用：

.. code-block:: js

    created() {
        this.d.filesGetTemporaryLink({path:
            this.f.path_lower}).then(data => {
            this.link = data.link;
        });
    }

这个 ``API`` 方法接受一个对象，类似于 ``filesListFolder`` 函数。 我们正在传递当前文件的路径。数据返回后，我们可以将组件的 ``link`` 属性设置为下载链接。

我们现在可以添加一个下载链接到组件的模板。 添加 ``v-if`` ，只有在检索到下载链接后才显示 ``<a>`` ：

.. code-block:: html

    template: '<li><strong>{{ f.name }}</strong><span vif="f.size"> - {{ bytesToSize(f.size) }}</span><span  v-if="link"> - <a :href="link">Download</a></span></li>'

通过浏览文件，我们现在可以在每个文件旁边看到一个下载链接，其速度取决于您的互联网连接和 ``API`` 速度。添加下载链接的完整文件组件现在看起来如下所示：

.. code-block:: js

    Vue.component('file', {
        template: '<li><strong>{{ f.name }}</strong><span vif="f.size"> - {{ bytesToSize(f.size) }}</span><span v-if="link"> - <a :href="link">Download</a></span></li>',
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
                    let i = parseInt(Math.floor(Math.log(bytes) /
                        Math.log(1024)));
                    // Round to 2 decimal places and select the appropriate unit from the array
                    output = Math.round(bytes / Math.pow(1024, i), 2)  + ' ' + this.byteSizes[i];
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

更新URL哈希并使用它来浏览文件夹
===============================
通过我们的 ``Dropbox`` 网络应用，现在可以通过结构列表和面包屑完全导航，我们现在可以添加和更新浏览器 ``URL`` 以便快速访问和共享文件夹。我们可以通过两种方式做到这一点：我们可以更新散列，例如 ``www.domain.com/#/images/holiday/summer`` ，或者我们可以将所有路径重定向到单个页面，并在URL中没有散列的情况下处理路由。

对于这个应用程序，我们将使用 ``URL`` 中的 ``＃`` 方法。在介绍 ``vue-router`` 时，我们将在本书的第三部分介绍 ``URL`` 路由技术。

在我们获取应用程序以显示 ``URL`` 的相应​​文件夹之前，我们首先需要在导航到新文件夹时获取要更新的 ``URL`` 。我们可以使用本地的 ``window.location.hash`` JavaScript对象来做到这一点。我们希望在用户点击链接后立即更新网址，而不是等待数据加载进行更新。

由于 ``getFolderStructure`` 方法在我们更新结构时被触发，请将代码添加到此函数的顶部。这将意味着 ``URL`` 得到更新，然后调用 ``Dropbox API`` 来更新结构：

.. code-block:: js

    getFolderStructure(path) {
        window.location.hash = path; // 更新浏览器地址
        this.dropbox().filesListFolder({
            path: path,
            include_media_info: true
        }).then(response => {
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
                this.path = path;
                this.structure = structure;
                this.isLoading = false;
            }).catch(error => {
                console.log(error);
            });
    }

您可以浏览您的应用程序，它应该更新 ``URL`` 以包含当前文件夹路径。

但是，当您在文件夹中刷新时会发现 ``URL`` 被重置为通过 ``created()`` 函数中的方法传入的空路径，所以 ``URL`` 重置为哈希后面没有文件夹的情况。

我们可以通过将当前哈希传递给创建的函数中的 ``getFolderStructure`` 来弥补这一点，但是，如果我们这样做，将会有一些检查和错误捕获。

首先，当调用 ``window.location.hash`` 时，你也会得到散列作为字符串的一部分返回，所以我们需要删除它。 其次，如果用户输入错误的路径或文件夹被移动，我们需要处理正确 ``URL`` 的实例。 最后，我们需要让用户在其浏览器中使用后退和前进按钮（或键盘快捷键）。

基于URL显示文件夹
-----------------
当我们的应用程序挂载时，它已经调用了一个函数来请求基础文件夹的结构。在 ``created()`` 函数中，我们已经将该值固定为空的根文件夹，所以我们重新编写这个函数来允许路径被传入。 这使我们能够传递包含散列的URL参数而不是固定字符串来灵活地调整此函数。

更新函数以接受 ``URL`` 的散列，如果没有，则使用原始固定字符串：

.. code-block:: js

    created() {
        let hash = window.location.hash.substring(1);
        this.getFolderStructure(hash || '');
    }

创建一个名为 ``hash`` 的新变量并将 ``window.location.hash`` 赋值给它。 因为我们的应用程序不需要该以 ``#`` 开头的变量，所以使用 ``substring`` 函数从字符串中删除第一个字符。然后，我们可以使用一个逻辑运算符来使用散列变量，或者如果没有，则使用原始固定字符串。

您现在应该可以通过 ``URL`` 更新来浏览您的应用。 如果您随时按下刷新或将该 ``URL`` 复制并粘贴到其他浏览器窗口中，则应加载您所在的文件夹。

显示一个错误消息
----------------
使用我们接受网址的应用，我们需要处理某个人输错网址的情况，或者共享的文件夹已经移动的情况。

由于此错误是边缘情况，因此如果加载数据时出现错误，我们将劫持 ``isLoading`` 参数。在 ``getFolderStructure`` 函数中，我们有一个 ``catch`` 函数作为 ``promise`` 返回，如果API调用发生错误，它将被触发。在这个函数中，将 ``isLoading`` 变量设置为 ``error`` ：

.. code-block:: js

    getFolderStructure(path) {
        window.location.hash = path;
        this.dropbox().filesListFolder({ // 返回promise对象
            path: path,
            include_media_info: true
        }).then(response => {
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
                this.path = path;
                this.structure = structure;
                this.isLoading = false;
            }).catch(error => {
                this.isLoading = 'error';
                console.log(error);
            });
    }

如果我们需要诊断错误文件路径的问题，则使用 ``console.log`` 保留。虽然 ``API`` 可以抛出几个不同的错误，但我们将假定这个应用程序的错误是由于错误的路径。 如果您想在应用程序中处理其他错误，则可以通过其 ``status_code`` 属性识别错误类型。 有关这方面的更多详细信息可以在 ``Dropbox API`` 文档中找到。

更新你的视图来处理这个新的 ``isLoading`` 变量属性。当设置为错误时， ``isLoading`` 变量仍为 ``true`` ，因此在加载元素中添加一个新的 ``v-if`` 来检查加载变量是否设置为 ``error`` ：

.. code-block:: js

    <transition name="fade">
        <div v-if="isLoading">
            <div v-if="isLoading === 'error'">
                <p>There seems to be an issue with the URL entered.
                </p>
                <p><a href="">Go home</a></p>
            </div>
            <div v-else>
                Loading...
            </div>
        </div>
    </transition>

显示isLoading变量的第一个元素被设置为 ``error`` ; 否则，显示加载文本。 在错误文本中，包含一个链接可将用户发送回当前 ``URL`` ，该路径不使用任何 ``URL`` 哈希。 这将“重置”它们返回到文档树的顶部，以便它们可以向下导航。 一个改进可能是打破当前的 ``URL`` ，并建议删除最后一个文件夹的 ``URL`` 。

通过在 ``URL`` 末尾添加不存在的路径并确保显示错误消息来验证错误代码是否正在加载。 请记住，如果 ``Dropbox API`` 引发任何类型的错误，则此消息将显示，您的用户可能会在此错误消息上遇到误报。

在浏览器中使用后退和前进按钮
----------------------------
要在我们的浏览器中使用后退和前进按钮，我们将需要更新我们的代码。目前，当用户从结构或面包屑中点击文件夹时，我们通过在我们的点击处理程序上使用 ``.prevent`` 来阻止浏览器的默认行为。然后，我们在处理该文件夹之前立即更新网址。

但是，如果我们允许应用使用本地行为更新 ``URL`` ，则可以观察哈希 ``URL`` 更新并使用它来检索我们的新结构。 使用这种方法，后退和前进按钮将无需任何进一步干预，因为它们将更新 ``URL`` 哈希。

这也将改善我们的应用程序的可读性，并减少代码的重量，因为我们可以删除链接上的 ``navigate`` 方法和 ``click`` 处理程序。

移除不需要的代码
^^^^^^^^^^^^^^^^
在添加更多代码之前，第一步是从组件中删除不必要的代码。 从面包屑开始，从组件中删除 ``navigate`` 方法，并从模板中的链接中删除 ``\@click.prevent`` 属性。

我们还需要更新每个项目的 ``slug`` 都前缀一个 ``＃`` - 这可确保应用程序不会单击时尝试导航到全新页面。当我们循环浏览文件夹计算函数中的面包屑项时，将对象推送到 ``output`` 数组时，将哈希添加到每个 ``slug`` 前面：

.. code-block:: js

    Vue.component('breadcrumb', {
        template: '<div>' +
            '<span v-for="(f, i) in folders">' +
                '<a :href="f.path">{{ f.name || 'Home' }}</a>' +
                '<i v-if="i !== (folders.length - 1)"> &raquo; </i>' +
            '</span>' +
        '</div>',
        props: {
            p: String
        },
        computed: {
            folders() {
                let output = [],
                    slug = '',
                    parts = this.p.split('/');
                for (let item of parts) {
                    slug += item;
                    output.push({'name': item || 'home', 'path': '#' + slug});
                    slug += '/';
                }
                return output;
            }
        }
    });

我们还可以删除 ``dropbox-viewer-template`` 中面包屑组件的 ``v-on`` 声明。 它应该只将路径作为 ``prop`` 传入：

.. code-block:: html

    <breadcrumb :p="path"></breadcrumb>

我们现在可以为文件夹组件重复相同的模式。从链接中删除 ``@ click.prevent`` 声明并删除 ``navigate`` 方法。

由于在显示文件夹对象之前我们没有循环或编辑文件夹对象，所以我们可以在模板中添加 ``＃`` 。 当我们告诉 ``Vue href`` 被绑定到一个JavaScript对象（带冒号）时，我们需要将这个散列封装在引号中，并使用 JavaScript ``+`` 符号将它与文件夹路径连接。我们已经在单引号和双引号内，所以我们需要通知JavaScript，我们的字面意思是单引号，这是通过在单引号字符前使用反斜线来完成的：

.. code-block:: js

    Vue.component('folder', {
        template: '<li><strong><a :href="\'#\' + f.path_lower">{{ f.name }}</a></strong></li>',
        props: {
            f: Object
        }
    });

我们还可以从视图中的 ``<folder>`` 组件中删除 ``@path`` 属性：

.. code-block:: html

    <template v-for="entry in structure.folders">
        <folder :f="entry"></folder>
    </template>

我们的代码已经看起来更干净，更简洁，文件更小。 在浏览器中查看应用程序将渲染您所在文件夹的结构; 然而，点击链接会更新 ``URL`` 但不会改变显示内容。

使用URL更改更新结构并在实例外设置Vue数据
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
现在我们已经正确地更新了 ``URL`` ，只要哈希值发生变化，我们就可以得到新的结构。 这可以通过 ``JavaScript`` 本地带有 ``onhashchange`` 功能的完成。

我们将创建一个函数，该函数在 ``URL`` 更新的哈希值更新时触发，而这个函数会更新父 ``Vue`` 实例上的路径变量。该变量将作为道具传递给子 ``dropbox-viewer`` 组件。 该组件将会监视变量的变化，并且在更新时它将检索新的结构。

首先，更新父 ``Vue`` 实例以使数据对象具有路径键 - 设置为空字符串属性。 我们也将把我们的 ``Vue`` 实例赋值给 ``app`` 的一个常量变量 - 这允许我们在实例之外设置数据和调用方法：

.. code-block:: js

    const app = new Vue({
        el: '#app',
        data: {
            path: ''
        }
    });

下一步是每次 ``URL`` 更新时更新此数据属性。这是通过使用 ``window.onhashchange`` 完成的，该函数是一个本地 ``JavaScript`` 函数，每当 ``URL`` 中的哈希值发生变化时都会触发该函数。将创建的函数的哈希修饰符复制并粘贴到 ``Dropbox`` 组件上，然后使用该修改器修改哈希并将值存储在 ``Vue`` 实例上。 如果散列不存在，我们将传递一个空字符串给路径变量：

.. code-block:: js

    window.onhashchange = () => {
        let hash = window.location.hash.substring(1);
        app.path = (hash || '');
    }

我们现在需要将此路径变量传递给 ``Dropbox`` 组件。 在视图中添加一个名称为 ``p`` 的 ``prop`` 变量，它的值为 ``path`` 变量：

.. code-block:: html

    <div id="app">
        <dropbox-viewer :p="path"></dropbox-viewer>
    </div>

将 ``prop`` 对象添加到 ``Dropbox`` 组件以接受字符串：

.. code-block:: js

    props: {
        p: String
    },

我们现在要给 ``dropbox-viewer`` 组件添加一个 ``watch`` 函数。 该函数将观察 ``p`` 道具，并在更新时使用修改后的路径调用 ``updateStructure()`` 方法：

.. code-block:: js

    watch: {
        p() {
            this.updateStructure(this.p);
        }
    }

回到浏览器，我们现在应该能够像以前一样浏览我们的 ``Dropbox`` 结构，同时使用文件夹链接和面包屑作为导航。 我们现在应该能够使用后退和前进浏览器按钮，以及任何键盘快捷键，还可以浏览文件夹。

在介绍第6章，使用 ``Vuex`` 缓存当前文件夹结构以及使用 ``vuex`` 引入文件夹缓存到我们的应用程序之前，我们可以对 ``Dropbox`` 组件进行一些优化。

首先，在 ``getFolderStructure`` 函数中，我们可以删除 ``URL`` 哈希被设置为路径的第一行。这是因为点击链接时 ``URL`` 已经更新。 从你的代码中删除这一行：

.. code-block:: js

    window.location.hash = path;

其次， ``Dropbox`` 组件中现在有了 ``this.path`` 变量和 ``p`` 道具的重复。 消除这种情况需要稍微修改一下，因为不允许直接修改路径一样修改道具。 但是，它需要保持同步，以便可以在面包屑中正确渲染。

从 ``Dropbox`` 组件中的数据对象中删除 ``path`` 属性，并从 ``getFolderStructure`` 函数中删除 ``this.path = path`` 行。

接下来，更新 ``prop`` 为 ``path`` ，而不是 ``p`` 。 这也需要更新 ``watch`` 函数来观察 ``path`` 变量而不是 ``p`` 。

更新 ``created`` 方法以仅将 ``this.path`` 用作函数的参数。 ``Dropbox`` 组件现在应该如下所示：

.. code-block:: js

    Vue.component('dropbox-viewer', {
        template: '#dropbox-viewer-template',
        props: {
            path: String
        },
        data() {
            return {
                accessToken: 'XXXX',
                structure: {},
                isLoading: true
            }
        },
        methods: {
            dropbox() {
                return new Dropbox({
                    accessToken: this.accessToken
                });
            },
            getFolderStructure(path) {
                this.dropbox().filesListFolder({
                    path: path,
                    include_media_info: true
                }).then(response => {
                        const structure = {
                            folders: [],
                            files: []
                        }
                        for (let entry of response.entries) {
                            // Check ".tag" prop for type
                            if(entry['.tag'] == 'folder') {
                                structure.folders.push(entry);
                            } else {
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
            updateStructure(path) {
                this.isLoading = true;
                this.getFolderStructure(path);
            }
        },
        created() {
            this.getFolderStructure(this.path);
        },
        watch: {
            path() {
                this.updateStructure(this.path);
            }
        },
    });

更新视图以接受 ``prop`` 作为 ``path`` ：

.. code-block:: html

    <dropbox-viewer :path="path"></dropbox-viewer>

我们现在需要确保父 ``Vue`` 实例在页面加载和散列更改上都有正确的路径。 为了避免重复，我们将用一个方法和一个 ``created`` 函数来扩展我们的Vue实例。

保持路径变量设置为空字符串。创建一个名为 ``updateHash()`` 的新方法，该方法从 ``window`` 哈希中移除第一个字符，然后将该 ``path`` 变量设置为哈希或空字符串。 接下来，创建一个运行 ``updateHash`` 方法的 ``created()`` 函数。

``Vue`` 实例现在看起来像这样：

.. code-block:: js

    const app = new Vue({
        el: '#app',
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

最后，为了消除重复，我们可以在地址栏中的哈希值更改时触发 ``updateHash`` 方法：

.. code-block:: js

    window.onhashchange = () => {
        app.updateHash();
    }

最终代码
========
现在我们的代码完成了您的视图， ``JavaScript`` 文件应该如下所示。
首先，视图应该是这样的：

.. code-block:: html

    <div id="app">
        <dropbox-viewer :path="path"></dropbox-viewer>
    </div>
    <script type="text/x-template" id="dropbox-viewertemplate">
        <div>
            <h1>Dropbox</h1>
            <transition name="fade">
                <div v-if="isLoading">
                    <div v-if="isLoading == 'error'">
                        <p>There seems to be an issue with the URL
                            entered.</p>
                        <p><a href="">Go home</a></p>
                    </div>
                    <div v-else>
                        Loading...
                    </div>
                </div>
            </transition>
            <transition name="fade">
                <div v-if="!isLoading">
                    <breadcrumb :p="path"></breadcrumb>
                    <ul>
                        <template v-for="entry in structure.folders">
                            <folder :f="entry"></folder>
                        </template>
                        <template v-for="entry in structure.files">
                            <file :d="dropbox()" :f="entry"></file>
                        </template>
                    </ul>
                </div>
            </transition>
        </div>
    </script>

随附的 ``JavaScript`` 应用应该如下所示：

.. code-block:: js

    Vue.component('breadcrumb', {
        template: '<div>' +
            '<span v-for="(f, i) in folders">' +
                '<a :href="f.path">{{ f.name || 'Home' }}</a>' +
                '<i v-if="i !== (folders.length - 1)"> &raquo; </i>' +
            '</span>' +
        '</div>',
        props: {
            p: String
        },
        computed: {
            folders() {
                let output = [],
                    slug = '',
                    parts = this.p.split('/');
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
        template: '<li><strong>{{ f.name }}</strong><span v-if="f.size"> - {{ bytesToSize(f.size) }}</span><span v-if="link"> - <a :href="link">Download</a></span></li>',
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
                    output = Math.round(bytes / Math.pow(1024, i), 2)  + ' ' + this.byteSizes[i];
                }
                return output;
            }
        },
        created() {
            this.d.filesGetTemporaryLink({path:
                this.f.path_lower}).then(data => {
                this.link = data.link;
            });
        },
    });

    Vue.component('dropbox-viewer', {
        template: '#dropbox-viewer-template',
        props: {
            path: String
        },
        data() {
            return {
                accessToken: 'XXXX',
                structure: {},
                isLoading: true
            }
        },
        methods: {
            dropbox() {
                return new Dropbox({
                    accessToken: this.accessToken
                });
            },
            getFolderStructure(path) {
                this.dropbox().filesListFolder({
                    path: path,
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
            updateStructure(path) {
                this.isLoading = true;
                this.getFolderStructure(path);
            }
        },
        created() {
            this.getFolderStructure(this.path);
        },
        watch: {
            path() {
                this.updateStructure(this.path);
            }
        },
    });

    const app = new Vue({
        el: '#app',
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

    window.onhashchange = () => {
        app.updateHash();
    }

总结
====
我们现在有一个功能齐全的 ``Dropbox`` 查看器应用程序，其中包含文件夹导航和文件下载链接。 我们可以使用文件夹链接或面包屑导航并使用后退或前进按钮。 我们还可以共享或收藏链接并加载该文件夹的内容。

在第6章缓存当前文件夹结构使用 ``Vuex`` ，我们将通过使用 ``Vuex`` 缓存当前文件夹内容来加快导航过程。
