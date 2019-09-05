*************
配置Laravel开发环境
*************

在本书的前两章中，我们介绍了 ``Vue.js`` 。您现在应该对其基本功能非常满意。 在本章中，我们将准备构建 ``Vuebnb`` 后端，并开始运行 ``Laravel`` 开发环境。

本章涵盖的主题：

- ``Laravel`` 简要介绍；
- 建立 ``Homestead`` 虚拟开发环境；
- 配置 ``Homestead`` 以承载 ``Vuebnb`` ；

Laravel
=======
``Laravel`` 是一个开源的 ``PHP MVC`` 框架，用于构建健壮的 ``Web`` 应用程序。 ``Laravel`` 目前处于 ``5.5`` 版本，是最受欢迎的 ``PHP`` 框架之一，因其优雅的语法和强大的功能而备受赞誉。

``Laravel`` 适用于创建各种基于 ``Web`` 的项目，如下所示：

- 具有用户认证的网站，例如客户门户或社交网络；
- ``Web`` 应用程序，例如图像裁剪器或监视仪表板；
- ``Web`` 服务，例如 ``RESTful APIs`` ；

在本书中，我假设了 ``Laravel`` 的基本知识。 您应该熟悉安装和设置 ``Laravel`` 并熟悉其核心功能，例如路由，视图和中间件。

如果您是 ``Laravel`` 的新手，或者认为您需要复习一下，那么在继续阅读本书之前，您应该花一两个小时阅读 ``Laravel`` 出色的文档： https://laravel.com/docs/5.5/ 。

Laravel和Vue
============
``Laravel`` 可能看起来像一个庞大的框架，因为它包含了构建几乎任何类型的 ``Web`` 应用程序的功能。 在底层， ``Laravel`` 是许多独立模块的集合，其中一些是作为 ``Laravel`` 项目的一部分开发的，另一些是来自第三方作者。 使 ``Laravel`` 更加伟大的部分原因是它对这些组件模块进行了仔细的管理和无缝连接。

自 ``Laravel 5.3`` 版本以来， ``Vue.js`` 一直是 ``Laravel`` 安装中包含的默认前端框架。 和其他值得的选择相比，官方没有理由说明为什么 ``Vue`` 被选择，比如 ``React`` ，但我的猜测是，这是因为 ``Vue`` 和 ``Laravel`` 有着相同的理念：简单性和对开发者体验的强调。

无论是什么原因， ``Vue`` 和 ``Laravel`` 都为开发 ``Web`` 应用程序提供了一个非常强大且灵活的全栈框架。

环境
====
我们将使用 ``Laravel 5.5`` 作为 ``Vuebnb`` 的后端。 此版本的 ``Laravel`` 需要 ``PHP 7`` ，一些个 ``PHP`` 扩展以及以下软件：

- Composer；
- Web服务器，如Apache或Nginx；
- 数据库，如MySQL或MariaDB

.. tip:: 有关 ``Laravel`` 的完整要求列表，请参阅安装指南： https://laravel.com/docs/5.5#installation 。

我强烈建议您使用 ``Homestead`` 开发环境，而不是在您的计算机上手动安装 ``Laravel``，这样不需要预先安装所需的一切。

Homestead
---------
``Laravel Homestead`` 是一个虚拟 ``Web`` 应用程序环境，可在 ``Vagrant`` 和 ``VirtualBox`` 上运行，并可在任何 ``Windows`` ， ``Mac`` 或 ``Linux`` 系统上运行。

使用 ``Homestead`` 可以节省您从零开始设置开发环境的麻烦。 它还将确保您拥有与我正在使用的环境相同的环境，这将使您更轻松地遵循本书。

如果您的计算机上未安装 ``Homestead`` ，请按照 ``Laravel`` 文档中的说明进行操作： https://laravel.com/docs/5.5/homestead 。 使用默认配置选项。

一旦你安装了 ``Homestead`` 并用 ``vagrant up`` 命令启动了 ``Vagrant box`` ，你就可以继续了。

Vuebnb
=======
在第2章“您的第一个 ``Vue.js`` 项目 ``Vuebnb`` 原型开发”中，我们制作了 ``Vuebnb`` 前端的原型。 原型是从我们直接从浏览器加载的单个 ``HTML`` 文件创建的。

现在我们将开始研究整个 ``Vuebnb`` 项目，其中原型将很快成为关键部分。 这个主要项目将是一个带有 ``Web`` 服务器和数据库的完整 ``Laravel`` 安装。

项目代码
--------
如果您还没有，则需要通过从 ``GitHub`` 中克隆它将代码库下载到您的计算机。 指令在第1章中的代码库部分给出， ``Hello Vue - Vue.js`` 简介。

代码库中的 ``vuebnb`` 文件夹包含我们现在要使用的项目代码。

共享文件夹
==========
``Homestead.yaml`` 文件的 ``folders`` 属性列出了您希望在计算机和 ``Homestead`` 环境之间共享的所有文件夹。

确保代码库与 ``Homestead`` 共享，以便我们可以在本章后面的 ``Homestead Web`` 服务器中为 ``Vuebnb`` 提供服务。

~/Homestead/Homestead.yaml:

.. code-block:: yaml

    folders:
        - map: /Users/anthonygore/Projects/Full-Stack-Vue.js-2-and-Laravel-5
          to: /home/vagrant/projects

终端命令
========
除非另有说明，本书中的所有其他终端命令将相对于项目目录，即 ``vuebnb`` 给出。

但是，由于项目目录是在主机和 ``Homestead`` 之间共享的，因此终端命令可以从这些环境中的任何一个运行。

``Homestead`` 使您无需在主机上安装任何软件。 但是，如果你不这样做，在主机环境中许多终端命令可能无法正常工作，或者可能无法正常工作。 例如，如果您的主机没有安装 ``PHP`` ，则无法运行 ``Artisan`` 命令：

.. code-block:: shell

    $ php artisan --version
    -bash: php: command not found

如果您遇到这种情况，您需要首先通过 ``SSH`` 连接 ``Homestead`` ，从 ``Homestead`` 环境中运行这些命令：

.. code-block:: shell

    $ cd ~/Homestead
    $ vagrant ssh

然后，将其更改为 ``OS`` 中的项目目录，并使用相同的终端命令：

.. code-block:: shell

    $ cd ~/projects/vuebnb
    $ php artisan --version
    Laravel Framework 5.5.20

从 ``Homestead`` 运行命令唯一的缺点是由于 ``SSH`` 连接，它们速度较慢。 我会留给你决定你宁愿使用哪一个。

环境变量
========
``Laravel`` 项目需要在 ``.env`` 文件中设置某些环境变量。 通过复制环境文件示例现在创建一个：

.. code-block:: shell

    $ cp .env.example .env

运行以下命令生成应用程序密钥：

.. code-block:: shell

    $ php artisan key:generate

我已经预设了大多数其他相关的环境变量，因此除非您配置了和我 ``Homestead`` 不同的配置，否则不必更改任何内容。

Composer安装
=============
要完成安装过程，我们必须运行 ``composer install`` 来下载所有必需的软件包：

.. code-block:: shell

    $ composer install

数据库
======
我们将使用关系数据库将数据保存在我们的后端应用程序中。 ``Homestead`` 已经安装 ``MySQL`` ; 您只需在 ``.env`` 文件中提供配置，以便在 ``Laravel`` 中使用它。 默认配置将无需进一步更改即可使用。

.env:

.. code-block:: ini

    DB_CONNECTION=mysql
    DB_HOST=192.168.10.10
    DB_PORT=3306
    DB_DATABASE=vuebnb
    DB_USERNAME=homestead
    DB_PASSWORD=secret

无论您为数据库选择什么名称（即 ``DB_DATABASE`` 的值），都要确保将其添加到 ``Homestead.yaml`` 文件中的 ``database`` 数组中。

~/Homestead/Homestead.yaml:

.. code-block:: yaml

    databases:
      ...
      - vuebnb

项目虚拟机
==========
主要的 ``Vuebnb`` 项目现已安装。 让我们让 ``Web`` 服务器在本地开发域 ``vuebnb.test`` 中服务它。

在 ``Homestead`` 配置文件中，将 ``vuebnb.test`` 映射到项目的 ``public`` 文件夹。

~/Homestead/Homestead.yaml:

.. code-block:: yaml

    sites:
      ...
      - map: vuebnb.test
        to: /home/vagrant/vuebnb/public

本地DNS入口
-----------
我们还需要更新计算机的主机文件，以便理解 ``vuebnb.test`` 和 ``Web``` 服务器的 ``IP`` 之间的映射关系。  ``Web`` 服务器位于 ``Homestead`` 盒子中，默认 ``IP`` 为 ``192.168.10.10`` 。

要在 ``Mac`` 上进行配置，请在文本编辑器中打开主机文件 ``/etc/hosts`` 并添加以下条目：

.. code-block:: ini

    192.168.10.10 vuebnb.test

.. tip:: 主机文件通常可以在 ``Windows`` 系统上的 ``C:\Windows\System32\Drivers\etc\hosts`` 中找到。

访问项目
========
完成所有配置后，我们现在可以在 ``Homestead`` 目录内运行 ``vagrant provision`` 以完成设置：

.. code-block:: shell

    $ cd ~/Homestead
    $ vagrant provision
    # The next command will return you to the project directory
    $ cd -

当配置过程完成后，我们应该能够在浏览器浏览到 ``http://vuebnb.test`` 时看到我们的网站正在运行：

总结
====
在这个简短的章节中，我们讨论了开发 ``Laravel`` 项目的要求。 然后，我们安装并配置了 ``Homestead`` 虚拟开发环境，以承载我们的主要项目 ``Vuebnb`` 。

在下一章中，我们将通过构建一个 ``Web`` 服务向 ``Vuebnb`` 前端提供数据，开始我们的主要项目。