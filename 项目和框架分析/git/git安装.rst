******
Git 安装
******

直接去 `Git官网 <https://git-scm.com/>`_ 下载最新版本，安装完后将 ``安装目录/cmd/git.exe`` 加入到系统环境变量 ``Path`` 中，打开 ``cmd`` 输入 ``git --version`` 命令查看一下是否安装成功。

Git 配置
========
配置全局参数，这里主要配置 用户名、邮箱 以及 SSH 。

.. code-block:: shell

	# 配置用户名
	git config --global user.name "username"
	# 配置邮箱
	git config --global user.email "username@email.com"


.. note:: 注意：（引号内请输入你自己设置的名字，和你自己的邮箱）此用户名和邮箱是git提交代码时用来显示你身份和联系方式的，并不是github用户名和邮箱

git使用ssh密钥
-------------
git支持 ``https`` 和 ``git`` 两种传输协议， github 分享链接时会有两种协议。

git使用 ``https`` 协议，每次 ``pull`` ， ``push`` 都会提示要输入密码，使用 ``git`` 协议，然后使用 ``ssh`` 密钥，这样免去每次都输密码的麻烦。

初次使用 git 的用户要使用 git 协议大概需要三个步骤：

1. 生成密钥对；
2. 设置远程仓库（本文以github为例）上的公钥；
3. 把 ``git`` 的 ``remote url`` 修改为 ``git`` 协议（以上两个步骤初次设置过以后，以后使用都不需要再次设置，此步骤视以后项目的 ``remote url`` 而定，如果以后其他项目的协议为 ``https`` 则需要此步骤）；

生成密钥对
^^^^^^^^^

.. code-block:: shell

    # 生成 ssh，输完后连敲三个回车即可
	ssh-keygen -t rsa

这时候去查看系统盘用户目录下（一般在 ``C:\Users\你的用户名\.ssh`` ）是否有了 ``.ssh`` 文件夹，进入会看到之前生成的 ``id_rsa`` 以及 ``id_rsa.pub`` 。

添加公钥到你的远程仓库（github）
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

1. 查看你生成的公钥

.. code-block:: shell

	$ cat ~/.ssh/id_rsa.pub

	ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC0X6L1zLL4VHuvGb8aJH3ippTozmReSUzgntvk434aJ/v7kOdJ/MTyBlWXFCR+HAo3FXRitBqxiX1nKhXpHAZsMciLq8vR3c8E7CjZN733f5AL8uEYJA+YZevY5UCvEg+umT7PHghKYaJwaCxV7sjYP7Z6V79OMCEAGDNXC26IBMdMgOluQjp6o6j2KAdtRBdCDS/QIU5THQDxJ9lBXjk1fiq9tITo/aXBvjZeD+gH/Apkh/0GbO8VQLiYYmNfqqAHHeXdltORn8N7C9lOa/UW3KM7QdXo6J0GFlBVQeTE/IGqhMS5PMln3 admin@admin-PC

2. 登陆你的github帐户。点击你的头像，然后 Settings -> 左栏点击 SSH and GPG keys -> 点击 New SSH key
3. 然后你复制上面的公钥内容，粘贴进“Key”文本域内。 title域，自己随便起个名字。
4. 点击 Add key。

完成以后，验证下这个key是不是正常工作：

.. code-block:: shell

	$ ssh -T git@github.com

	Attempts to ssh to github

如果，看到：

.. code-block:: shell

    Hi xxx! You've successfully authenticated, but GitHub does not # provide shell access.

恭喜你，你的设置已经成功了。

修改git的remote url
^^^^^^^^^^^^^^^^^^^

使用命令 ``git remote -v`` 查看你当前的 ``remote url``

.. code-block:: shell

	$ git remote -v
	origin https://github.com/someaccount/someproject.git (fetch)
	origin https://github.com/someaccount/someproject.git (push)

如果是以上的结果那么说明此项目是使用 ``https`` 协议进行访问的（如果地址是 ``git`` 开头则表示是 ``git`` 协议）

使用命令 ``git remote set-url`` 来调整你的 ``url`` 。

.. code-block:: shell

    git remote set-url origin git@github.com:someaccount/someproject.git

然后你可以再用命令 ``git remote -v`` 查看一下，url是否已经变成了 ``ssh`` 地址。

然后你就可以愉快的使用 ``git fetch, git pull , git push`` ，再也不用输入烦人的密码了。



`在git中出现中文乱码的解决方案`_ 

.. _在git中出现中文乱码的解决方案: https://blog.csdn.net/Tyro_java/article/details/53439537