***********
Linux文件特殊权限
***********

除了我们前面介绍的 ``rwx`` 权限外， ``Linux`` 中还有另外三种特殊权限： ``SUID`` ， ``SGID`` ， ``SBIT``

SUID，SGID，SBIT 介绍
=====================

``SUID:s`` 出现在文件所有者的 ``x`` 权限上
----------------------------------------

1. ``SUID`` 只能用于二进制可执行文件，对目录无效；
2. 执行者若具有该文件的 ``x`` 权限，则将具有文件所有者的权限；
3. 权限只在文件执行时有效，执行完毕不再拥有所有者权限；

``SGID:s`` 出现在文件所属群组的 ``x`` 权限上
------------------------------------------
``SGID`` 和 ``SUID`` 不同，可以用于目录

1. 使用者若有此目录的 ``x`` ， ``w`` 权限，则可进入和修改此目录；
2. 使用者在此目录下的群组将变成该目录的群组，新建的文件，群组是此目录的群组；

.. code-block:: shell

	# mkdir /opt/test
	# chgrp tom /opt/test
	# chmod g+s /opt/test
	# ls -ld /opt/test
	drwxr-srwx. 2 root tom 6 5月  27 12:37 /opt/test

然后分别用 root 和 bob 用户在该目录下创建文件：

.. code-block:: shell

	# touch /opt/test/test_root
	# ll /opt/test/test_root
	-rw-r--r--. 1 root tom 0 5月  27 12:40 /opt/test/test_root
	$ touch /opt/test/test_bob
	$ ll /opt/test/test_bob
	-rw-rw-r--. 1 bob tom 0 5月  27 12:42 /opt/test/test_bob
	# mkdir /opt/test/root1
	# ls -ld /opt/test/root1
	drwxr-sr-x. 2 root tom 6 5月  27 12:42 /opt/test/root1

``SGID`` 对于文件来说

1. ``SGID`` 只对二进制可执行文件有效
2. 执行者若具有该文件的 ``x`` 权限，则将具有文件所属群组的权限
3. 权限只在文件执行时有效，执行完毕不再拥有所属群组权限

``locate`` 命令属于 ``mlocate`` 包，使用命令前要先更新数据库 ``updatedb`` ，查找速度老快啦，但是 ``locate`` 命令需要实时跟新数据库，否则新创建的文件检索不到，而且 ``/tmp`` 目录下的文件也无法查询：

- ``/usr/bin/locate`` 是可执行的二进制程序，可以赋予 ``SGID``
- 执行用户 ``test`` 对 ``/usr/bin/locate`` 命令拥有执行权限
- 执行 ``/usr/bin/locate`` 命令时，组身份会升级为 ``slocate`` 组，而 ``slocate`` 组对 ``/var/lib/mlocate/mlocate.db`` 数据库拥有 ``r`` 权限，所以普通用户可以使用 ``locate`` 命令查询 ``mlocate`` 数据库
- 命令结束， ``test`` 用户的组身份返回 ``test`` 组


``SBIT:t`` 出现在文件其他用户的 ``x`` 权限上
------------------------------------------

1. 和 ``SUID`` ， ``SGID`` 不同的是，只能用于目录；
2. 普通用户对该目录拥有 ``w`` 和 ``x`` 权限，即普通用户可以在此目录拥有写入权限
3. 如果没有黏着位，因为普通用户拥有 ``w`` 权限，所以可以删除此目录下所有文件，包括其他用户建立的文件。一旦赋予了黏着位，除了 ``root`` 可以删除所以文件，普通用户就算拥有 ``w`` 权限，也只能删除自己建立的文件，但是不能删除其他用户建立的文件。
3. 使用者在该目录下，仅自己与 ``root`` 才有权力删除新建的目录或文件，即 ``t`` 位于目录的 ``other`` 的位置时，这时除了所有者和 ``root`` 之外，其他的用户即使有权限，也无法删除该目录下的文件；

.. code-block:: shell

	[root@www ~]# mkdir /opt/test2
	[root@www ~]# chmod 777 /opt/test2
	[root@www ~]# touch /opt/test2/test
	[root@www ~]# chmod 777 /opt/test2/test
	[tom@www ~]$ rm -rf /opt/test2/test
	[root@www ~]# chmod o+t /opt/test2
	[root@www ~]# touch /opt/test2/test
	[root@www ~]# chmod 777 /opt/test2/test
	[tom@www ~]$ rm -rf /opt/test2/test
	rm: 无法删除"/opt/test2/test": 不允许的操作

为什么要使用特殊权限
===================
举个例子，比如 ``/usr/bin/passwd`` 这个二进制文件。

它的权限是 ``-rwsr-xr-x`` ，我不是所有者，我具有 ``x`` 权限，我执行它时，获得了它的所有者（即 ``root`` ）的权限，所以在该二进制程序执行时，我可以用它来读到我平时是没有权限访问的 ``/etc/shadow`` 文件（ ``-r--------`` ），从而能更改我自己的密码

修改SUID，SGID，SBIT权限
=======================

符号类型修改权限
---------------

- chmod u+s test --为test文件加上suid权限
- chmod g+s test --为test文件加上sgid权限
- chmod o+t test --为test文件加上sbit权限

数字类型修改权限
---------------
我们知道普通文件的 ``rwx`` 权限修改，对于特殊权限，需要在最前面增加一位， ``SUID:4`` ， ``SGID:2`` ， ``SBIT:1``

- chmod 4777 test --test 拥有 SUID 权限，rwsrwxrwx
- chmod 2777 test --test 拥有 SGID 权限，rwxrwsrwx
- chmod 1777 test --test 拥有 SBIT 权限，rwxrwxrwt
- chmod 7777 test --test 有用 SUID SGID SBIT 权限，rwsrwsrwt

文件系统属性权限chattr权限（该权限针对root用户）
=============================================
语法:

# chattr [+-=][选项] 文件名 设置chattr属性

# lsattr –a 文件名 查看chattr属性

-R 递归显示子目录下的文件，-d 查看目录本身

选项：

- i：如果对文件设置 i 属性，那么不允许对文件进行删除、改名，也不能添加和修改数据；如果对目录设置 i 属性，那么只能修改目录下文件的数据，但不允许建立和删除文件。
- a：如果对文件设置了 a 属性，那么只能在文件中增加数据，但是不能删除也不能修改数据；如果对目录设置 a 属性，那么直允许在目录中建立和修改文件，但是不允许删除。


使用 ``lsattr`` 命令查看特殊权限。

特殊权限 ``a`` ，意味着只能增加不能减小，对目录和文件都生效：

.. code-block:: php

	[root@www ~]# touch /tmp/test
	[root@www ~]# lsattr /tmp/test
	---------------- /tmp/test
	[root@www ~]# echo xxxxxxxx > /tmp/test
	[root@www ~]# chattr +a /tmp/test
	[root@www ~]# lsattr /tmp/test
	-----a---------- /tmp/test
	[root@www ~]# echo yyyyyyyyyyy > /tmp/test
	-bash: /tmp/test: 不允许的操作
	[root@www ~]# echo yyyyyyyyyyy >> /tmp/test

特殊权限 ``i`` ，文件和目录的内容不能发生改变：

.. code-block:: php

	[root@www ~]# chattr -a /tmp/test
	[root@www ~]# chattr +i /tmp/test
	[root@www ~]# cat /tmp/test
	xxxxxxxx
	yyyyyyyyyyy
	[root@www ~]# lsattr /tmp/test
	----i----------- /tmp/test
	[root@www ~]# echo hahahhahah >> /tmp/test
	-bash: /tmp/test: 权限不够


系统命令sudo权限
================

- ``root`` 把本来只能超级用户执行的命令赋予普通用户来执行
- ``sudo`` 的操作对象是系统命令

设置方法：

# visudo 或者 # vim /etc/sudoers

root  ALL=(ALL)  ALL

用户名 被管理主机的地址=(可使用的身份) 授权命令(绝对路径)

%test ALL=(ALL)  ALL

组名 被管理主机的地址=(可使用的身份) 授权命令(绝对路径)







