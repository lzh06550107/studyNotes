******************
使用openssl生成自签名CA证书
******************

证书文件生成
============

服务器端
--------

1. 生成服务器端 私钥(key文件);

.. code-block:: shell

    openssl genrsa -des3 -out server.key 1024

运行时会提示输入密码，此密码用于加密 ``key`` 文件(参数 ``des3`` 是加密算法，也可以选用其他安全的算法)，以后每当需读取此文件(通过 ``openssl`` 提供的命令或 ``API`` )都需输入口令。如果不要口令，则去除口令: ``openssl rsa -in server.key -out server.key``

2. 生成服务器端 证书签名请求文件(csr文件);

.. code-block:: shell

	openssl req -new -key server.key -out server.csr

生成 ``Certificate Signing Request（CSR）`` ，生成的 ``csr`` 文件交给 ``CA`` 签名后形成服务端自己的证书。屏幕上将有提示，依照其提示一步一步输入要求的个人信息即可(如: Country ，province ， city ， company 等)。

客户端
------

1. 生成客户端 私钥(key文件);

.. code-block:: shell

	openssl genrsa -des3 -out client.key 1024

2. 生成客户端 证书签名请求文件(csr文件);

.. code-block:: shell

    openssl req -new -key client.key -out client.csr

生成CA证书文件
-------------
``server.csr`` 与 ``client.csr`` 文件必须有 CA 的签名才可形成证书。

1. 首先生成 ``CA`` 的 ``key`` 文件:

.. code-block:: shell

    openssl genrsa -des3 -out ca.key 1024

2. 生成 ``CA`` 自签名证书:

.. code-block:: shell

    openssl req -new -x509 -key ca.key -out ca.crt

可以加证书过期时间选项 "-days 365" 。

利用CA证书进行签名
-----------------

.. code-block:: shell

	openssl ca -in ../server.csr -out ../server.crt -cert ca.crt -keyfile ca.key 
	openssl ca -in ../client.csr -out ../client.crt -cert ca.crt -keyfile ca.key

这两条执行的时候因为没有指定 ``openssl.cnf`` 会报错，不过没关系，我们用默认的 ``/etc/pki/tls/openssl.cnf`` 就可以。

