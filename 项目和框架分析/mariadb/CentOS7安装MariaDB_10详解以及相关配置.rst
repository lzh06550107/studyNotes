****************************
CentOS 7安装MariaDB 10详解以及相关配置
****************************

添加 MariaDB yum 仓库
=====================
首先在 ``CentOS`` 操作系统中 ``/etc/yum.repos.d/`` 目录下添加 ``MariaDB`` 的 ``YUM`` 配置文件 ``MariaDB.repo`` 文件。

.. code-block:: shell

    vi /etc/yum.repos.d/MariaDB.repo

在该文件中添加以下内容保存：

.. code-block:: shell

	[mariadb]
	name=MariaDB
	baseurl=http://yum.mariadb.org/10.2/centos7-amd64
	gpgkey=https://yum.mariadb.org/RPM-GPG-KEY-MariaDB
	gpgcheck=1

安装 MariaDB
============
通过 ``yum`` 命令轻松安装 ``MariaDB`` 。

.. code-block:: shell

    yum install MariaDB-server MariaDB-client -y

``MariaDB`` 安装完毕后，立即启动数据库服务守护进程。

.. code-block:: shell

    systemctl start mariadb

设置 ``MariaDB`` 在操作系统重启后自动启动服务。

.. code-block:: shell

    systemctl enable mariadb

查看 ``MariaDB`` 服务当前状态。

.. code-block:: shell

    systemctl status mariadb

对 MariaDB 进行安全配置
======================
通过以下命令进行安全配置，根据实际情况用 ``Y/N`` 回复以下问题：设置 ``MariaDB`` 的 ``root`` 账户密码，删除匿名用户，禁用 ``root`` 远程登录，删除测试数据库，重新加载权限表。

.. code-block:: shell

    mysql_secure_installation

本人全都是选择了 ``Y`` ，然后按回车。

在配置完数据库的安全配置后，可以通过以下命令查看版本，确认 ``MariaDB`` 已安装成功。

.. code-block:: shell

    mysql --version

可以通过 ``MariaDB`` 命令行登录，然后对数据库进行 ``sql`` 查询操作。

.. code-block:: shell

    mysql -u root -p

为 ``MariaDB`` 配置远程访问权限

在第三步中如果禁用 ``root`` 远程登录选择 ``Y`` 的话就不能在别的电脑通过 ``navicat`` 等工具连接到数据库，这时就需要给对应的 ``MariaDB`` 账户分配权限，允许使用该账户远程连接到 ``MariaDB`` 。可以输入以下命令查看账号信息：

.. code-block:: shell

    select User, host from mysql.user;

``root`` 账户中的 ``host`` 项是 ``localhost`` 表示该账号只能进行本地登录，我们需要修改权限，输入命令：

.. code-block:: shell

    GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'password' WITH GRANT OPTION;

修改权限。 ``%`` 表示针对所有 ``IP`` ， ``password`` 表示将用这个密码登录 ``root`` 用户，如果想只让某个 ``IP`` 段的主机连接，可以修改为：

.. code-block:: shell

    GRANT ALL PRIVILEGES ON *.* TO 'root'@'192.168.71.%' IDENTIFIED BY 'my-new-password' WITH GRANT OPTION;

最后别忘了：

.. code-block:: shell

    FLUSH PRIVILEGES;

保存更改后，再看看用户账号信息。如果发现相比之前多了一项，它的 ``host`` 项是 ``%`` ，这个时候说明配置成功了，我们可以用该账号进行远程访问了。

CentOS 7 开放防火墙端口
======================
在第四步后如果还是不能远程连上数据库的话应该就是 ``3306`` 端口被防火墙拦截了，这时我们就需要关闭防火墙或者开放防火墙端口。

关闭防火墙：

.. code-block:: shell

	systemctl stop firewalld.service            #停止firewall

	systemctl disable firewalld.service        #禁止firewall开机启动

开放防火墙端口，开启后要重启防火墙：

.. code-block:: shell

	firewall-cmd --zone=public --add-port=3306/tcp --permanent

	firewall-cmd --reload

设置数据库字母大小写不敏感
========================

.. code-block:: shell

    vi /etc/my.cnf.d/server.cnf


	[mysqld]
	lower_case_table_names=1

默认是等于 ``0`` 的，即大小写敏感。改成 ``1`` 就 ``OK`` 了。如果之前已经建了数据库要把之前建立的数据库删除，重建才生效。

设置MariaDB数据库默认编码
========================
``MariaDB`` 的默认编码是 ``latin1`` ，插入中文会乱码，因此需要将编码改为 ``utf8`` 。

1. 登录，使用以下命令查看当前使用的字符集，应该有好几个不是 ``utf8`` 格式；

.. code-block:: shell

    SHOW VARIABLES LIKE 'character%';

2. 修改的配置文件

.. code-block:: shell

    vi /etc/my.cnf.d/client.cnf

	[client]
	default-character-set=utf8

	vi /etc/my.cnf.d/server.cnf

	[mysqld]
	character-set-server=utf8

3. 重启 ``MariaDB`` 配置生效。

.. code-block:: shell

    systemctl restart mariadb

centos6 安装 https://www.cnblogs.com/kevingrace/p/8556239.html
