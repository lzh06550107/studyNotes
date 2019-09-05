***********
OpenSSL使用手册
***********

openssl 简介
============
构成部分
--------

- 密码算法库；
- 密钥和证书封装管理功能；
- SSL通信API接口；

用途
----

- 建立 ``RSA`` 、 ``DH`` 、 ``DSA key`` 参数；
- 建立 ``X.509`` 证书、证书签名请求(CSR)和CRLs(证书回收列表)；
- 计算消息摘要；
- 使用各种 Cipher加密/解密；
- ``SSL/TLS`` 客户端以及服务器的测试；
- 处理 ``S/MIME`` 或者加密邮件；

RSA密钥操作
==========
默认情况下， ``openssl`` 输出格式为 ``PKCS#1-PEM``

生成 ``RSA`` 私钥(无加密)

.. code-block:: shell

    openssl genrsa -out rsa_private.key 2048

生成 ``RSA`` 公钥

.. code-block:: shell

    openssl rsa -in rsa_private.key -pubout -out rsa_public.key

生成 ``RSA`` 私钥(使用 ``aes256`` 加密)

.. code-block:: shell

    openssl genrsa -aes256 -passout pass:111111 -out rsa_aes_private.key 2048

其中 ``passout`` 代替 ``shell`` 进行密码输入，否则会提示输入密码；生成加密后的内容如：

.. code-block:: shell

	-----BEGIN RSA PRIVATE KEY-----
	Proc-Type: 4,ENCRYPTED
	DEK-Info: AES-256-CBC,5584D000DDDD53DD5B12AE935F05A007
	Base64 Encoded Data
	-----END RSA PRIVATE KEY-----

此时若生成公钥，需要提供密码:

.. code-block:: shell

	openssl rsa -in rsa_aes_private.key -passin pass:111111 -pubout -out rsa_public.key

其中 ``passout`` 代替 ``shell`` 进行密码输入，否则会提示输入密码；

转换命令
--------
私钥转非加密

.. code-block:: shell

    openssl rsa -in rsa_aes_private.key -passin pass:111111 -out rsa_private.key

私钥转加密

.. code-block:: shell

    openssl rsa -in rsa_private.key -aes256 -passout pass:111111 -out rsa_aes_private.key

私钥PEM转DER

.. code-block:: shell

    openssl rsa -in rsa_private.key -outform der-out rsa_aes_private.der

``-inform`` 和 ``-outform`` 参数制定输入输出格式，由 ``der`` 转 ``pem`` 格式同理

查看私钥明细

.. code-block:: shell

    openssl rsa -in rsa_private.key -noout -text

使用 ``-pubin`` 参数可查看公钥明细

私钥PKCS#1转PKCS#8

.. code-block:: shell

    openssl pkcs8 -topk8 -in rsa_private.key -passout pass:111111 -out pkcs8_private.key

其中 ``-passout`` 指定了密码，输出的 ``pkcs8`` 格式密钥为加密形式， ``pkcs8`` 默认采用 ``des3`` 加密算法，内容如下：

.. code-block:: shell

	-----BEGIN ENCRYPTED PRIVATE KEY-----
	Base64 Encoded Data
	-----END ENCRYPTED PRIVATE KEY-----

使用 ``-nocrypt`` 参数可以输出无加密的 ``pkcs8`` 密钥，如下：

.. code-block:: shell

	-----BEGIN PRIVATE KEY-----
	Base64 Encoded Data
	-----END PRIVATE KEY-----

- https://www.cnblogs.com/littleatp/p/5878763.html


- https://www.cnblogs.com/guogangj/p/4118605.html
