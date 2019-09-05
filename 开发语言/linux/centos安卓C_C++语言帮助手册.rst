********************
centos 安装C/C++语言帮助手册
********************

安装C语言帮助手册
================
``CentOS`` 系统有可能默认没有安装 ``C`` 语言帮助手册， ``man`` 一个函数时会找不到帮助文件，用下面的命令安装：

.. code-block:: shell

    yum install man-pages.noarch -y      =>安装C语言帮助手册

    man 2 open   =>查看open函数

安装C++语言帮助手册
==================
用下面的命令安装：

.. code-block:: shell

    yum install libstdc++-docs.x86_64 -y      =>安装C++语言帮助手册

    man std::iostream   =>查看std::iostream函数


