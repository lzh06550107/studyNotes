*******************
使用Dropbox API获取文件列表
*******************

在接下来的几章中，我们将构建一个基于 ``Vue`` 的 ``Dropbox`` 浏览器。这个应用程序将采取您的 ``Dropbox API`` 密钥，并允许您导航到文件夹和下载文件。您将学习如何与 ``Vue`` 应用程序中的 ``API`` 交互， ``Vue`` 生命周期挂钩（包括 ``created()`` 方法），最后我们将介绍一个名为 ``Vuex`` 的库来处理应用程序的缓存和状态。该应用将具有可共享的 ``URL`` 并通过 ``＃URL`` 参数检索传入的文件夹的内容。

如果您想让用户访问您的 ``Dropbox`` 的内容而不用提供用户名和密码，这种应用程序将非常有用。不过要注意的是，技术人员用户可能会在代码中找到您的 ``API`` 密钥并滥用它，因此请勿将此代码发布到万维网。

本章将包括：

- 加载和查询 ``Dropbox API`` ；
- 列出 ``Dropbox`` 帐户中的目录和文件；
- 将加载状态添加到您的应用程序；
- 使用 ``Vue`` 动画；

您将需要一个 ``Dropbox`` 帐户来进行下面几章。如果你没有，注册并添加几个虚拟文件和文件夹。``Dropbox`` 的内容不
很重要，但是了解将有助于理解代码。

入门——加载库
============
为您的应用程序创建新的 ``HTML`` 页面以运行。创建网页所需的 ``HTML`` 结构并包含您的应用程序视图封装器：

.. code-block:: html

    <!DOCTYPE html>
    <html>
    <head>
        <title>Dropbox App</title>
    </head>
    <body>
        <div id="app"></div>
    </body>
    </html>

由于我们的应用程序代码会变得非常笨重，请制作一个单独的 ``JavaScript`` 文件并将其包含在文档的底部。您还需要包含 ``Vue`` 和 ``Dropbox API SDK`` 。

和以前一样，您可以引用远程文件或下载的库文件本地副本。 为了速度和兼容性的原因下载本地副本。将您的三个 ``JavaScript`` 文件包含在 ``HTML`` 文件的底部：

.. code-block:: html

    <script src="js/vue.js"></script>
    <script src="js/dropbox.js"></script>
    <script src="js/app.js"></script>

创建 ``app.js`` 并初始化一个新的 ``Vue`` 实例，使用 ``el`` 标签将实例安装到视图中的 ``ID`` 上。

.. code-block:: js

    new Vue({
        el: '#app'
    });

创建一个Dropbox应用然后初始化SDK
================================
在我们与 ``Vue`` 实例交互之前，我们需要通过 ``SDK`` 连接到 ``Dropbox API`` 。这是通过 ``Dropbox`` 自己生成的 ``API`` 密钥完成的，以便跟踪连接到您帐户的内容。

前往 ``Dropbox`` 开发人员区并选择创建您的应用程序。选择 ``Dropbox API`` 并选择受限制的文件夹或完整访问权限。这取决于您的需求，但对于测试，请选择 ``Full Dropbox`` 。为您的应用命名，然后点击创建应用按钮。

为您的应用生成访问令牌。 为此，在查看应用程序详细信息页面时，单击生成的访问令牌下的生成按钮。 这会给你一串长长的数字和字母 - 复制并粘贴到你的编辑器中，并将它作为一个变量存储在 ``JavaScript`` 的顶部。在本书中， ``API`` 密钥将被称为 ``XXXX`` ：

.. code-block:: js

    /**
     * API Access Token
     */
    let accessToken = 'XXXX';

现在我们有了我们的 ``API`` 密钥，我们可以从我们的 ``Dropbox`` 访问文件和文件夹。
初始化 ``API`` 并将您的 ``accessToken`` 变量传递给 ``Dropbox API`` 的 ``accessToken`` 属性：

.. code-block:: js

    /**
     * Dropbox Client
     * @type {Dropbox}
     */
    const dbx = new Dropbox({
        accessToken: accessToken
    });

我们现在可以通过 ``dbx`` 变量访问 ``Dropbox`` 。 我们可以通过连接并输出根路径的内容来验证我们与 ``Dropbox`` 的连接是否正常：

.. code-block:: js

    dbx.filesListFolder({path: ''})
        .then(response => {
            console.log(response.entries);
        })
        .catch(error => {
            console.log(error);
        });

此代码使用 ``JavaScript`` 承诺，这是一种向代码添加动作而不需要回调函数的方式。 如果您不熟悉承诺，请查看 ``Google`` 的此博客文章（ ``https://developers.google.com/web/fundamentals/primers/promises`` ）。

记下第一行，特别是 ``path`` 变量。 这让我们传入一个文件夹路径来列出该目录中的文件和文件夹。 例如，如果您的 ``Dropbox`` 中有一个名为 ``images`` 的文件夹，您可以将参数值更改为 ``/ images`` ，并且返回的文件列表将是该目录中的文件和文件夹。

打开您的 ``JavaScript`` 控制台并检查输出；你应该得到一个包含多个对象的数组 - 一个 ``Dropbox`` 根目录中的每个文件或文件夹。

显示你的数据然后使用Vue获取它
=============================
现在我们可以使用 ``Dropbox API`` 检索数据，现在可以在我们的 ``Vue`` 实例中检索它并显示在我们的视图中。 这个应用程序将完全使用组件构建，因此我们可以利用分组的数据和方法。 这也意味着代码是模块化的，可共享的，便于你集成到其他应用程序。

我们也将利用原生 ``Vue created()`` 函数 - 我们稍后会在触发它的时候介绍它。

创建组件
========
首先，在您的视图中创建您的自定义 ``HTML`` 元素 ``<dropbox-viewer>`` 。在我们的页面底部为 ``HTML`` 布局创建一个 ``<script>`` 模板块：

.. code-block:: html

    <div id="app">
        <dropbox-viewer></dropbox-viewer>
    </div>
    <script type="text/x-template" id="dropbox-viewer-template">
        <h1>Dropbox</h1>
    </script>

在你的 ``app.js`` 文件中初始化你的组件，并指向模板 ``ID`` ：

.. code-block:: js

    Vue.component('dropbox-viewer', {
        template: '#dropbox-viewer-template'
    });

在浏览器中查看应用程序应显示模板中的标题。下一步是将 ``Dropbox API`` 集成到组件中。

检索Dropbox数据
===============
创建一个名为 ``dropbox`` 的新方法。 在那里，移动调用 ``Dropbox`` 类的代码到这里并返回实例。现在，通过调用 ``this.dropbox()`` ，我们可以通过该组件访问 ``Dropbox API`` ：

.. code-block:: js

    Vue.component('dropbox-viewer', {
        template: '#dropbox-viewer-template',
        methods: {
            dropbox() {
                return new Dropbox({
                    accessToken: this.accessToken
                });
            }
        }
    });

我们也将把我们的 ``API`` 密钥集成到组件中。 创建一个返回包含访问令牌的对象的数据函数。更新 ``Dropbox`` 方法以使用本地版本的密钥：

.. code-block:: js

    Vue.component('dropbox-viewer', {
        template: '#dropbox-viewer-template',
        data() {
            return {
                accessToken: 'XXXX'
            }
        },
        methods: {
            dropbox() {
                return new Dropbox({
                    accessToken: this.accessToken
                });
            }
        }
    });

我们现在需要添加组件获取目录列表的能力。为此，我们将创建另一个采用单个参数的方法 - 路径。这将使我们稍后能够根据需要请求不同路径或文件夹的结构。

使用前面提供的代码 - 将 ``dbx`` 变量更改为 ``this.dropbox()`` ：

.. code-block:: js

    getFolderStructure(path) {
        this.dropbox().filesListFolder({path: path})
            .then(response => {
                console.log(response.entries);
            })
            .catch(error => {
                console.log(error);
            });
    }

更新 ``Dropbox filesListFolder`` 函数以接受传入的路径参数，而不是固定值。在浏览器中运行此应用程序将显示 ``Dropbox`` 标题，但不会检索任何文件夹，因为方法尚未被调用。

Vue生命周期钩子
===============
这就是 ``created()`` 函数的来源。 ``Vue`` 实例初始化数据和方法后，会调用 ``created()`` 函数，但尚未在 ``HTML`` 组件上安装实例。在生命周期的不同阶段还有其他几个功能可用； 有关这些的更多信息可以在 ``Alligator.io`` 上阅读。生命周期如下：

.. image:: ./images/lifecycle.png

使用 ``created()`` 函数可以让我们访问方法和数据，同时能够在 ``Vue`` 安装应用程序时启动我们的检索过程。这些不同阶段之间的时间差不多是瞬间的，但每当谈到性能和创建快速应用程序时，每时每刻都在计数。如果我们能够尽早开始任务，在处理数据之前，没有必要等待应用程序完全安装。

在组件上创建 ``created()`` 函数并调用 ``getFolderStructure`` 方法，传递一个空字符串作为路径以获取 ``Dropbox`` 的根目录：

.. code-block:: js

    Vue.component('dropbox-viewer', {
        template: '#dropbox-viewer-template',
        data() {
            return {
                accessToken: 'XXXX'
            }
        },
        methods: {
            ...
        },
        created() {
            this.getFolderStructure('');
        }
    });

现在在您的浏览器中运行应用程序将输出文件夹列表到您的控制台，应该给出与以前相同的结果。

我们现在需要在视图中显示我们的文件列表。为此，我们将在组件中创建一个空数组，并使用 ``Dropbox`` 查询的结果填充它。这有利于 ``Vue`` 在视图中循环这一个变量，即使它没有任何内容。

显示Dropbox数据
===============
在数据对象中创建一个标题为 ``structure`` 的新属性，并给它分配一个空数组。 在文件夹检索的响应函数中，将 ``response.entries`` 分配给 ``this.structure`` 。 保留 ``console.log`` ，因为我们需要检查条目以确定要在模板中输出的内容：

.. code-block:: js

    Vue.component('dropbox-viewer', {
        template: '#dropbox-viewer-template',
        data() {
            return {
                accessToken: 'XXXX',
                structure: []
            }
        },
        methods: {
            dropbox() {
                return new Dropbox({
                    accessToken: this.accessToken
                });
            },
            getFolderStructure(path) {
                this.dropbox().filesListFolder({path: path})
                    .then(response => {
                        console.log(response.entries);
                        this.structure = response.entries;
                    })
                    .catch(error => {
                        console.log(error);
                    });
            }
        },
        created() {
            this.getFolderStructure('');
        }
    });

我们现在可以更新我们的视图以显示 ``Dropbox`` 中的文件夹和文件。 由于结构数组在我们的视图中可用，因此请在结构中使用可重复的 ``<li>`` 循环创建 ``<ul>`` 。

因为我们现在添加第二个元素，且 ``Vue`` 要求模板只有一个包含元素，所以将您的标题和列表包装在 ``<div>`` 中：

.. code-block:: html

    <script type="text/x-template" id="dropbox-viewertemplate">
        <div>
            <h1>Dropbox</h1>
            <ul>
                <li v-for="entry in structure"></li>
            </ul>
        </div>
    </script>

当数组出现在 ``JavaScript`` 控制台中时，在浏览器中查看应用程序将显示许多空白的项目符号。要确定可以显示哪些字段和属性，请在 ``JavaScript`` 控制台中展开数组，然后对每个对象进一步展开。 您应该注意到，每个对象都具有相似属性的集合，并且在文件夹和文件之间有一些不同。

第一个属性 ``.tag`` 帮助我们识别项目是文件还是文件夹。两种类型都具有以下共同特性：

- id:Dropbox唯一的标识符；
- name:文件或文件夹的名称；
- path_display:该项目的完整路径与大小写匹配的文件和文件夹；
- path_lower:和 path_display 一样但都是小写；

带有 ``.tag`` 文件的项目还包含我们要显示的几个字段：

- client_modified：这是将文件添加到 ``Dropbox`` 的日期。
- content_hash：文件的散列，用于标识它与本地还是远程不同复制。 更多内容可以在Dropbox网站上阅读。
- rev：文件版本的唯一标识符。
- server_modified：上次在 ``Dropbox`` 上修改文件的时间。
- size：以字节为单位的文件大小。

首先，我们将显示项目的名称和大小（如果存在）。 更新列表项以显示这些属性：

.. code-block:: html

    <li v-for="entry in structure">
        <strong>{{ entry.name }}</strong>
        <span v-if="entry.size"> - {{ entry.size }}</span>
    </li>

文件更多元信息
==============
为了使我们的文件和文件夹视图更有用，我们可以添加更多丰富的内容和
元数据到文件，如图像。通过在 ``Dropbox API`` 中启用 ``include_media_info`` 选项，可以获得这些详细信息。

返回到 ``getFolderStructure`` 方法并在路径后添加参数。这里有一些新的行：

.. code-block:: js

    getFolderStructure(path) {
        this.dropbox().filesListFolder({
            path: path,
            include_media_info: true
        }).then(response => {
                console.log(response.entries);
                this.structure = response.entries;
            }).catch(error => {
                console.log(error);
            });
    }

检查来自此新 ``API`` 调用的结果将显示视频和图像的 ``media_info`` 键。 展开这些内容会显示更多关于该文件的信息，例如尺寸。 如果您想添加这些信息，则需要在显示信息之前检查 ``media_info`` 对象是否存在：

.. code-block:: html

    <li>
        <strong>{{ f.name }}</strong>
        <span v-if="f.size"> - {{ bytesToSize(f.size) }}</span> -
        <span v-if="f.media_info">
            [{{ f.media_info.metadata.dimensions.width }}px x {{ f.media_info.metadata.dimensions.height }}px]
        </span>
    </li>

尝试从 ``Dropbox`` 中检索数据时更新路径。 例如，如果您有一个名为 ``images`` 的文件夹，请将 ``this.getFolderStructure`` 参数更改为 ``/images`` 。如果您不确定路径是什么，请分析 ``JavaScript`` 控制台中的数据并复制文件夹的 ``path_lower`` 属性的值，例如：

.. code-block:: js

    created() {
        this.getFolderStructure('/images');
    }

格式化文件大小
==============
以纯文本字节输出文件大小对于用户来说可能相当困难。
为了解决这个问题，我们可以添加一个格式化方法来输出更友好的文件大小，例如显示 ``1kb`` 而不是 ``1024`` 。

首先，在数据对象上创建名为 ``byteSize`` 的单位数组的一个新键：

.. code-block:: js

    data() {
        return {
            accessToken: 'XXXX',
            structure: [],
            byteSizes: ['Bytes', 'KB', 'MB', 'GB', 'TB']
        }
    }

这是附加到图上的内容，所以请随意使这些属性为小写或全部单词，例如，兆字节。

接下来，向组件添加一个新方法 ``bytesToSize`` 。 这将需要一个 ``bytes`` 参数，并在最后输出带有单位的格式化字符串：

.. code-block:: js

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

我们可以在视图中使用这个方法：

.. code-block:: html

    <li v-for="entry in structure">
        <strong>{{ entry.name }}</strong>
        <span v-if="entry.size"> - {{ bytesToSize(entry.size) }}</span>
    </li>

增加加载动画
============
本章的最后一步是为我们的应用程序制作一个加载动画。 这将告诉用户应用程序正在加载，如果 ``Dropbox API`` 运行缓慢（或者您需要显示大量数据！）。

这个加载屏幕背后的理论是相当基础的。 我们将默认情况下将加载变量设置为 ``true`` ，然后在加载数据后将其设置为 ``false`` 。根据此变量的结果，我们将利用视图属性显示并隐藏加载文本或动画的元素，并显示加载的数据列表。

在名为 ``isLoading`` 的数据对象中创建一个新的 ``isLoading``  键。默认情况下将此变量设置为 ``true`` ：

.. code-block:: js

    data() {
        return {
            accessToken: 'XXXX',
            structure: [],
            byteSizes: ['Bytes', 'KB', 'MB', 'GB', 'TB'],
            isLoading: true
        }
    }

在组件的 ``getFolderStructure`` 方法中，将 ``isLoading`` 变量设置为 ``false`` 。 在设置结构后，这应该在 ``promise`` 内发生：

.. code-block:: js

    getFolderStructure(path) {
        this.dropbox().filesListFolder({
            path: path,
            include_media_info: true
        })
            .then(response => {
                console.log(response.entries);
                this.structure = response.entries;
                this.isLoading = false; // 注意
            })
            .catch(error => {
                console.log(error);
            });
    }

我们现在可以在我们的视图中使用这个变量来显示和隐藏加载容器。

在包含一些加载文本的无序列表之前创建一个新的 ``<div>`` 。 随意添加 ``CSS`` 动画或 ``GIF`` 动画 - 让用户知道该应用正在检索数据：

.. code-block:: html

    <h1>Dropbox</h1>
    <div>Loading...</div>
    <ul>
        ...

我们现在只需要显示加载 ``div`` ，如果应用程序正在加载，并且数据加载完毕后，将显示列表。 由于这只是 ``DOM`` 的一个变化，我们可以使用 ``v-if`` 指令。为了让您自由地重新排列HTML，请将该属性添加到两者中，而不是使用 ``v-else`` 。

要显示或隐藏，我们只需要检查 ``isLoading`` 变量的状态。 我们可以在列表中添加一个感叹号，如果应用程序未加载则显示：

.. code-block:: html

    <div>
        <h1>Dropbox</h1>
        <div v-if="isLoading">Loading...</div>
        <ul v-if="!isLoading">
            <li v-for="entry in structure">
                <strong>{{ entry.name }}</strong>
                <span v-if="entry.size">- {{ bytesToSize(entry.size) }}</span>
            </li>
        </ul>
    </div>

一旦安装我们的应用程序现在应该显示加载容器，然后收集应用程序数据后应该显示列表。 回顾一下，我们的完整组件代码现在看起来像这样：

.. code-block:: js

    Vue.component('dropbox-viewer', {
        template: '#dropbox-viewer-template',
        data() {
            return {
                accessToken: 'XXXX',
                structure: [],
                byteSizes: ['Bytes', 'KB', 'MB', 'GB', 'TB'],
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
                        console.log(response.entries);
                        this.structure = response.entries;
                        this.isLoading = false;
                    })
                    .catch(error => {
                        console.log(error);
                    });
            },
            bytesToSize(bytes) {
                // Set a default
                let output = '0 Byte';
                // If the bytes are bigger than 0
                if (bytes > 0) {
                    // Divide by 1024 and make an int
                    let i = parseInt(Math.floor(Math.log(bytes)  / Math.log(1024)));
                    // Round to 2 decimal places and select the appropriate unit from the array
                    output = Math.round(bytes / Math.pow(1024,
                        i), 2) + ' ' + this.byteSizes[i];
                }
                return output
            }
        },
        created() {
            this.getFolderStructure('');
        }
    });

动画过渡
========
作为用户的一个不错的改进，我们可以在组件和状态切换之间添加一些过渡。有趣的是， ``Vue`` 包含了一些内置的过渡效果。 使用 ``CSS`` 时，这些转换允许您在插入 ``DOM`` 元素时轻松添加淡入淡出，滑动和其他 ``CSS`` 动画。 有关转换的更多信息可以在 ``Vue`` 文档中找到。

第一步是添加 ``Vue`` 自定义 ``HTML <transition>`` 元素。 使用单独的 ``<transition>`` 元素包装您的加载和列表，并给它一个 ``name``  属性和淡入淡出 ``fade`` 值：

.. code-block:: html

    <script type="text/x-template" id="dropbox-viewertemplate">
        <div>
            <h1>Dropbox</h1>
            <transition name="fade">
                <div v-if="isLoading">Loading...</div>
            </transition>
            <transition name="fade">
                <ul v-if="!isLoading">
                    <li v-for="entry in structure">
                        <strong>{{ entry.name }}</strong>
                        <span v-if="entry.size">- {{ bytesToSize(entry.size) }}</span>
                    </li>
                </ul>
            </transition>
        </div>
    </script>

现在，将以下 ``CSS`` 添加到文档的头部或单独的样式表（如果已经有一个）：

.. code-block:: css

    .fade-enter-active,
    .fade-leave-active {
        transition: opacity .5s
    }
    . fade-enter,
    .fade-leave-to {
        opacity: 0
    }

使用 ``transition`` 元素， ``Vue`` 会根据转换的状态和时间添加和删除各种 ``CSS`` 类。所有这些都以通过属性传入的名称开始，并附加了当前的转换阶段：

在浏览器中尝试应用程序，您应该注意到加载容器淡出并且文件列表淡入。尽管在这个基本示例中，一旦淡入淡出完成，列表就会跳转，这是帮助您理解在 ``Vue`` 中使用转换的示例。

总结
====
在本章中，我们学习了如何创建一个 ``Dropbox`` 查看器，该查看器是一个单页面应用程序，用于列出 ``Dropbox`` 帐户中的文件和文件夹，并允许我们通过更新代码来显示不同的文件夹内容。 我们已经学会了如何为我们的应用程序添加基本的加载状态并使用 ``Vue` 动画进行导航。

在第5章中，浏览文件树并从 ``URL`` 加载文件夹，我们将浏览我们的应用程序文件夹并添加下载链接到我们的文件。
