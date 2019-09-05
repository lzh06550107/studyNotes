YUM常用命令
===========

安装软件包
----------

- ``yum install package``
- ``yum localinstall package`` 从本机目录安装软件包
- ``yum groupinstall group`` 安装某个组件的全部软件包

更新软件包
----------

- ``yum update package``
- ``yum check-update`` 列出所有可更新的软件包
- ``yum list updates mysql*`` 查找mysql的更新
- ``yum update`` 更新所有可更新的软件包
- ``yum update mysql*`` 更新所有mysql的软件包
- ``yum groupupdate group`` 更新某个组件的所有软件包
- ``yum list`` 列出所有已安装和仓库中可用的软件包
- ``yum list available`` 列出仓库中所有可用的软件包
- ``yum list updates`` 列出仓库中比当前系统更新的软件包
- ``yum list installed`` 列出已安装的软件包
- ``yum list recent`` 列出新加入仓库的软件包
- ``yum info`` 查询软件包信息

删除软件包
----------

- ``yum remove package``
- ``yum groupremove group`` 删除某个组件的全部软件包

清除软件包
----------

``yum clean packages`` 清除遗留在缓存里的包文件
``yum clean metadata`` 清除遗留在缓存里的元数据
``yum clean headers`` 清除遗留在缓存里的头文件
``yum clean all`` 清除包文件，元数据，头文件

搜索软件包
----------

- ``yum search package``
- ``yum info package`` 查找一个软件包的信息
- ``yum list package`` 列出包含指定信息的软件包
- ``yum list installed`` 列出已安装的软件包
- ``yum list extras`` 列出不是通过软件仓库安装的软件包
- ``yum list *ttp*`` 列出标题包含ttp的软件包
- ``yum list updates`` 列出可以更新的软件包

查找特定文件是由什么软件包提供的
--------------------------------

- ``yum whatprovides filename``

例子:

.. code-block:: shell

    yum whatprovides httpd.conf


可用选项

- ``–disalberepo=lib`` 禁用某个软件仓库
- ``–enalberepo=lib`` 启用某个软件仓库
- ``-C`` 禁用使用本机缓存的元数据

例子:

.. code-block:: shell

    yum –disalberepo=livna|–enalberepo=livna install mplayer
    yum -C info httpd

显示yum仓库
-----------

- ``yum repolist all`` 显示所有仓库
- ``yum repolist enabled`` 显示可用的仓库
- ``yum repolist disabled`` 显示不可用的仓库

包组相关的命令
--------------

- yum groupinstall    # 安装包组
- yum groupupdate     #更新包组
- yum grouplist       #显示包组
- yum groupremove     #移除包组
- yum groupinfo       #查看包组信息

利用yum下载软件包(不安装)的三种方法
===================================

downloadonly插件
----------------
有一个 ``yum`` 的插件叫做 ``downloadonly`` ，顾名思义，就是只下载不安装的意思。

1. 安装插件

.. code-block:: shell

    yum install yum-download

2. 下载

.. code-block:: shell

    yum update httpd -y –downloadonly

这样 ``httpd`` 的 ``rpm`` 就被下载到 ``/var/cache/yum/`` 中去了。

你也可以指定一个目录存放下载的文件

.. code-block:: shell

    yum update httpd -y –downloadonly –downloaddir=/opt

值得注意的是， ``downloadonly`` 插件不但适用于 ``yum update`` ，也适用于 ``yum install`` 。

yum-utils 中的 yumdownloader
----------------------------
``yum-utils`` 包含着一系列的 ``yum`` 的工具，比如 ``debuginfo-install`` ,  ``package-cleanup`` ,  ``repoclosure`` ,  ``repodiff`` ,  ``repo-graph`` ,  ``repomanage`` ,  ``repoquery`` ,  ``repo-rss`` ,  ``reposync`` ,  ``repotrack`` ,  ``verifytree`` ,  ``yum-builddep`` ,  ``yum-complete-transaction`` ,  ``yumdownloader`` ,  ``yum-debug-dump`` 和 ``yum-groups-manager`` 。

1. 安装yum-utils.noarch

yum -y install yum-utils

2. 使用yumdownloader

yumdownloader httpd

呵呵，就这么简单。

利用yum的缓存功能
------------------
用 ``yum`` 安装了某个工具后，我们想要这个工具的包。那 ``yum`` 安装的过程其实就已经把包给下载了，只是没有保持而已。

所以，我们要做的，是将其缓存功能打开。

1. ``vi /etc/yum.conf``  将其中 ``keepcache=0`` 改为 ``keepcache=1`` ，保存退出。
2. ``/etc/init.d/yum-updatesd restart``
3. ``yum install httpd``
4. ``cat /etc/yum.conf |grep cachedir``
   cachedir=/var/cache/yum

5. 跳到上术目录 ``cd cachedir=/var/cache/yum && tree ./``
6. 这个时候的目录树中应该可以找到你需要的安装包了。



