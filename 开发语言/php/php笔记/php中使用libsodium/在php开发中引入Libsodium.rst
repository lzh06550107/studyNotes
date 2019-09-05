******************
在php开发中引入Libsodium
******************
本电子书面向没有密码经验的 ``PHP`` 开发人员。为此，我们不会深入到每个 ``libsodium`` 功能使用的低级加密功能的性质。在每章末尾，我们将链接到其他资源，以让感兴趣的读者更深入细节。

Libsodium是什么
===============
``Sodium`` 加密库（ ``libsodium`` ）是一个现代的，易于使用的软件库，用于加密，解密，签名，密码散列等等。

它是一种便携式，可交叉编译的，可安装的，可打包的 ``NaCl`` 分支，具有兼容的 ``API`` 和扩展的 ``API`` ，可进一步提高可用性。

其目标是提供构建更高级别的加密工具所需的所有核心操作。

``Sodium`` 支持各种编译器和操作系统，包括 ``Windows`` （ ``MinGW`` 或 ``Visual Studio`` ， ``x86`` 和 ``x64`` ）， ``iOS`` 和 ``Android`` 。

设计选择强调安全性，“魔术常数”有明确的理由。

尽管强调高安全性，但与 ``NIST`` 标准的大多数实现相比，原始设备更快速。

版本1.0.14于2017年9月21日发布。

PECL Libsodium是什么
--------------------
``PECL Libsodium`` 是指作为 ``PECL`` 包提供的 ``PHP`` 扩展，它为 ``PHP`` 开发人员公开了 ``libsodium API`` 。

这里要记住两件重要的事情：

- 除非您安装 ``libsodium`` ，否则 ``PECL`` 包不起作用。 你需要同时安装这两个。
- 仅仅因为 ``libsodium`` 有一个功能并不意味着它可用（或打算）供 ``PHP`` 开发人员使用。

ext/sodium是什么
----------------
``PHP`` 编程语言版本 ``7.2.0`` 及更新版本内置 ``Sodium`` 扩展（称为 ``ext/sodium`` ）作为核心加密库。 ``PECL`` 中的 ``PHP`` 扩展版本 ``2`` 与 ``PHP 7.2`` 中的 ``ext/sodium`` 兼容。


我们应该安装那个版本的扩展
--------------------------

+-------------------------+----------+---------------+
| PHP版本                 | PECL版本 | Libsodium版本 |
+=========================+==========+===============+
| 7.2或更新               | —        | 1.0.9或更新   |
+-------------------------+----------+---------------+
| 7.0, 7.1                | 2.0.7    | 1.0.9或更新   |
+-------------------------+----------+---------------+
| 5.4, 5.5, 5.6, 7.0, 7.1 | 1.0.6    | 1.0.3或更新   |
+-------------------------+----------+---------------+

我们可以在更老的 ``PHP`` 上使用 ``Libsodium`` 或者不能安装PHP扩展
-----------------------------------------------------------------
您寻找支持 ``PHP 5.2`` 到 ``7.2`` 的 `sodium_compat <https://github.com/paragonie/sodium_compat>`_ ，但该包不支持 ``libsodium`` 的所有功能。特别是，它不提供密码散列算法。

术语和概念
==========
剩下的页面将假设您已阅读这些术语并理解 `基本密码学概念 <https://paragonie.com/blog/2015/08/you-wouldnt-base64-a-password-cryptography-decoded>`_ 。

- **密码** ：专注于安全通信的计算机科学的一个子集。
- **秘钥** ：在密码学中，密钥是决定加密算法输出的一条信息。
- **随机数** ：一个只能使用一次的数字（即，对于给定的密钥或一组密钥）。
- **加密散列函数(哈希)** ：将可变长度数据确定性单向转换为固定大小的输出，散列函数本身不使用密钥。
- **秘密密钥加密(对称加密)** ：两个参与者共享相同密钥的加密算法和协议。
- **公钥加密(非对称加密)** ：加密算法和协议，每个参与者拥有一个私钥和一个相关的公钥。他们公钥共享而私钥永远不会共享。公钥始终与私钥在数学上相关，这样拥有私钥的人可以生成正确的公钥，但相反是不行的。
- **加密** ：通过使用一个或多个密钥来可逆地转换数据，以确保唯一拥有正确密钥的人可以读取给定消息的内容。
- **身份认证** ：保证是拥有秘密认证密钥的人发送了一条消息。
- **数字签名** ：根据消息和私钥计算；允许拥有该消息，签名和公共密钥的任何人验证特定消息是否真实。

安装Libsodium和PHP扩展
=======================
安装 ``Libsodium`` （预编译）
在 ``Debian> = 8`` 和 ``Ubuntu> = 15.04`` 时， ``libsodium`` 可以使用如下命令安装：

.. code-block:: shell

    pt-get install libsodium-dev

如果您运行的是较旧的 ``LTS`` 版本的 ``Ubuntu`` （例如 ``12.04`` ），则可以使用其中一个 ``PPA`` 来安装 ``libsodium`` ：

- https://answers.launchpad.net/~chris-lea/+archive/ubuntu/libsodium
- https://launchpad.net/~anton+/+archive/ubuntu/dnscrypt

例如：

.. code-block:: shell

    # If this doesn't work...
        sudo add-apt-repository ppa:chris-lea/libsodium
    # Run these two lines instead...
        sudo echo "deb http://ppa.launchpad.net/chris-lea/libsodium/ubuntu precise main" >> /etc/apt/sources.list
        sudo echo "deb-src http://ppa.launchpad.net/chris-lea/libsodium/ubuntu precise main" >> /etc/apt/sources.list
    # Finally...
    sudo apt-get update && sudo apt-get install libsodium-dev

在 ``OSX`` 中，可以使用如下命令安装 ``libsodium``

.. code-block:: shell

    brew install libsodium

在 ``Fedora`` 中，可以使用如下命令安装：

.. code-block:: shell

    dnf install libsodium-devel

在 ``RHEL`` 和 ``CentOS`` ，可以从 ``EPEL`` 仓库安装 ``libsodium`` ：

.. code-block:: shell

    yum install libsodium-devel

在Windows中安装Libsodium和PHP扩展
---------------------------------

在 ``Windows`` 中，下载 `合适的libsodium版本 <http://windows.php.net/downloads/pecl/releases/libsodium/1.0.2/>`_ 然后根据如下步骤安装：

1. 复制 ``libsodium.dll`` 到 ``%SYSTEM32%`` 或者 ``php.exe`` 所在的目录下；
2. 复制 ``php_libsodium.dll`` 到 ``PHP`` 扩展目录

   a) 扩展目录可能是 ``ext/`` ；
   b) 可在 ``php.ini`` 中 ``extension_dir`` 指定扩展目录；

3. 在 ``php.ini`` 中添加 ``extension=php_libsodium.dll`` ；

从源码安装Libsodium
-------------------
如果您想要最新版本的 ``libsodium`` ，但它尚未进入操作系统的软件包管理库，最好的选择是从源代码编译它。

在 ``OS X`` 上从源代码安装 ``libsodium`` 很容易；使用 ``brew`` 安装时只需提供 ``--build-from-source`` 标志：

.. code-block:: shell

    brew install --build-from-source libsodium

对于其他操作系统，从源代码构建和安装 ``libsodium`` 需要一些先决条件。

基于 ``Debian`` 的发行版需要安装必要的工具：

.. code-block:: shell

    apt-get install build-essential

基于 ``RHEL`` 的发行版需要安装必要的实用程序：

.. code-block:: shell

    yum groupinstall "Development Tools"

安装必要的实用程序后， ``libsodium`` 可以编译为：

.. code-block:: shell

    # Clone the libsodium source tree
    git clone -b stable https://github.com/jedisct1/libsodium.git
    # Build libsodium, perform any defined tests, install libsodium
    cd libsodium && ./configure && make check && make install

通过PECL安装PHP扩展
--------------------
对于 ``PHP 7.2`` ，你可以跳过这一步。只要确保在安装 ``PHP`` 的操作系统上安装 ``php7.2-sodium`` 软件包。

如果您的系统中没有安装 ``PECL`` 软件包管理器，请确保先执行此操作。 几乎所有 ``PHP`` 支持的操作系统都有在 ``Internet`` 上有安装 ``PECL`` 的指南。

一旦你的系统上安装了 ``libsodium`` ，接下来要做的就是安装 ``PHP`` 扩展。 最简单的方法是安装 ``PECL`` 软件包。

通过运行此命令可以获得 ``ext/sodium`` 。

.. code-block:: shell

    pecl install libsodium

并将以下行添加到您的 ``php.ini`` 文件中：

.. code-block:: shell

    extension=sodium.so

您可以通过运行 ``phpenmod sodium`` 或 ``php5enmod sodium`` 来获得此结果，具体取决于您使用的是哪个 ``web`` 服务器。 确保在安装 ``ext/sodium`` 后重新启动 ``web`` 服务器。

验证你的Libsodium版本
=====================
安装库和 ``PHP`` 扩展之后，制作一个快速测试脚本来验证您是否安装了正确版本的 ``libsodium`` 。

.. code-block:: php

    <?php
    var_dump([
        SODIUM_LIBRARY_MAJOR_VERSION,
        SODIUM_LIBRARY_MINOR_VERSION,
        SODIUM_LIBRARY_VERSION
    ]);

如果您使用的是 ``libsodium 1.0.14`` ，那么在运行此测试脚本时应该会看到如下结果：

.. code-block:: shell

    user@hostname:~/dir$ php version_check.php
    array(2) {
      [0] =>
      int(9)
      [1] =>
      int(6)
      [2] =>
      string(6) "1.0.14"
    }

如果你得到不同的数字，你将无法访问应该在 ``libsodium 1.0.14`` 中的一些功能。如果你需要它们，你需要通过源代码来编译（如上所示）。然后运行 ``pecl`` 卸载 ``libsodium`` 和 ``pecl install libsodium`` 。当你再次运行版本检查 ``PHP`` 脚本时，你应该看到正确的数字。

额外信息
========

- `在 Ubuntu 上安装 PECL 包 <http://askubuntu.com/a/403348/260704>`_ ；
- `官方的 Libsodium 文档 <https://download.libsodium.org/doc>`_ ；
- `在 Github 上 Libsodium 源码 <https://github.com/jedisct1/libsodium>`_ ；
- `在 Github 上 PECL Libsodium 源码 <https://github.com/jedisct1/libsodium-php>`_ ；
