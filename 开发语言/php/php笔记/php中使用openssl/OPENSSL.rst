*******
OPENSSL
*******

``OpenSSL`` 扩展包含 ``OpenSSL`` 库中的一部分功能，允许您执行公钥加密。它们允许您签名和验证签名，并允许您加密和解密数据。

公钥密码系统使用一对密钥：一个密钥加密数据，另一个解密它。将其与使用相同密码进行加密和解密的简单加密方案进行比较。使用两把钥匙，所有者可以保留一把私钥，同时让公钥对其他人公开。任何人都可以使用公钥来加密私钥持有者的数据。没有私钥，数据仍然不可读。

导出秘钥
========

这个扩展允许你以几种方式使用秘钥。一种方法是使用由其中一个秘钥读功能生成的资源，例如 ``openssl_get_publickey`` 。或者，您可以提供包含密钥的字符串或包含密钥文件路径的字符串。在这两种情况下，密钥必须采用 ``PEM`` (privacy-enhanced mail，隐私增强邮件)格式。对于需要密码的私钥，您必须指定包含密钥和密码的数组。请务必使用 ``file://`` 开始路径，以便 ``PHP`` 知道它是路径而不是密钥。

openssl_csr_export — 将CSR作为字符串导出
----------------------------------------
``openssl_csr_export()`` 获取证书签名请求( ``csr`` )并通过引用保存其 ``PEM`` 格式的字符串( ``out`` )。

.. code-block:: php

    <?php
    boolean openssl_csr_export(resource csr, string output, boolean terse)

``openssl_csr_export`` 函数将 ``CSR`` (证书签名请求)放入 ``output`` 参数中。可选的 ``terse`` 参数控制输出是否包含额外的，人类可读的注释。它默认为 ``TRUE`` ，这意味着它不包含注释。如果设为 ``FALSE`` ，输出内容将包含附加的人类可读信息。

openssl_csr_export_to_file — 将CSR导出到文件
--------------------------------------------
``openssl_csr_export_to_file()`` 获取证书签名请求( ``csr`` ) 并将之保存在以 ``path`` 路径的 ``PEM`` 格式文件中。

.. code-block:: php

    <?php
    boolean openssl_csr_export_to_file(resource csr, string path, boolean terse)

``openssl_csr_export_to_file`` 函数(代码清单19.13)将 ``CSR`` 写入指定路径的文件中。可选的 ``terse`` 参数控制输出是否包含额外的，人类可读的注释。它默认为 ``TRUE`` ，这意味着它不包含注释。

.. code-block:: php

    <?php
    //setup distinguished name
    $dn = array(
        "countryName"=>"US",
        "stateOrProvinceName"=>"California",
        "organizationName"=>"Example Company, Inc.",
        "commonName"=>"example.com",
        "emailAddress"=>"leon@example.com");

    //setup configuration
    $config = array(
        'private_key_bits'=>1024);

    //make new key
    $privatekey = openssl_pkey_new();
    openssl_pkey_export_to_file($privatekey, 'example.pem',
        'corephp');

    //make certificate signing request
    $csr = openssl_csr_new($dn, $privatekey, $config);
    openssl_csr_export_to_file($csr, 'example.csr', FALSE);

    //make self-signed certificate
    $certificate = openssl_csr_sign($csr, NULL, $privatekey, 45);
    openssl_x509_export_to_file($certificate, 'example.crt');

.. code-block:: php

    <?php
    integer openssl_verify(string data, string signature, resource public_key)

``openssl_verify`` 函数验证签名数据上的签名。如果签名正确返回 ``1`` ，签名错误返回 ``0`` ，内部发生错误则返回 ``-1`` 。


.. code-block:: php

    <?php
    boolean openssl_x509_check_private_key(resource certificate, resource private_key)

``openssl_x509_check_private_key`` 函数检查给定的密钥是否属于给定的证书。

.. code-block:: php

    <?php
    boolean openssl_x509_checkpurpose(resource certificate, integer purpose, array ca, string untrusted)

``openssl_x509_checkpurpose`` 函数检查给定的证书是否可用于给定的目的。它在错误时返回 ``-1`` 。使用下表中的一个常量来指定目的。 ``ca`` 参数应该是一组可信的证书颁发机构。可选的 ``untrusted`` 参数可能是包含不可信证书的文件的路径。

.. code-block:: php

    <?php
    boolean openssl_x509_export(resource certificate, string output, boolean terse)

``openssl_x509_export`` 函数将 ``X.509`` 证书放入 ``output`` 中。可选的 ``terse`` 参数控制输出是否包含额外的，人类可读的注释。它默认为 ``TRUE`` ，这意味着它不包含注释。

.. code-block:: php

    <?php
    boolean openssl_x509_export_to_file(resource certificate, string file, boolean terse)

``openssl_x509_export_to_file`` 函数将 ``X.509`` 证书放入指定的文件中。可选的 ``terse`` 参数控制输出是否包含额外的，人类可读的注释。它默认为 ``TRUE`` ，这意味着它不包含注释。

.. code-block:: php

    <?php
    void openssl_x509_free(resource certificate)

使用此功能释放与证书资源关联的内存。


.. code-block:: php

    <?php
    array openssl_x509_parse(resource certificate, boolean short_names)

``openssl_x509_parse`` 函数返回一个描述给定证书属性的数组。默认情况下， ``PHP`` 使用数组键的短名称。将可选的 ``short_names`` 参数设置为 ``FALSE`` 以使用更长的名称。

.. code-block:: php

    <?php
    resource openssl_x509_read(string certificate)

``openssl_x509_read`` 函数创建一个给定字符串表示的证书资源作为或文件的路径指定的证书资源。


对称加密解密
============
在 ``PHP`` 中， ``openssl_encrypt`` 函数可用于使用散列秘钥来加密纯文本。举个例子吧：

.. code-block:: php

    <?php
    openssl_encrypt($textToEncrypt, $encryptionMethod, $secretKey, $options, $iv);

openssl_encrypt有五个参数：

- $textToEncrypt  - 需要加密的纯文本；
- $encryptionMethod  - 加密方法(使用openssl_get_cipher_methods，可用的方法)；
- $secretKey  - 一个密钥(需要保密)；
- $options  -  OPENSSL_RAW_DATA或OPENSSL_ZERO_PADDING(默认为0)；
- $iV - 初始化矢量。(恰好16位)；

以上将给出给定纯文本的加密文本。当涉及到解密时，您只需要知道 ``$secretKey`` 和 ``$iv`` ，矢量可以从 ``$secretKey`` 派生（或者可以是唯一的）。初始化向量( ``$iv`` )是一个随机数，它确保加密文本是唯一的。了解这个 ``$iv`` 是很重要的。

我用以下来产生 ``$iv`` :

.. code-block:: php

    $bytes = "";
    $last = "";
    while(strlen($bytes) < 48) {
        $last = md5($last . $secretHash, true);
        $bytes.= $last;
    }
    $iv = substr($bytes, 32, 16);

要解密上面创建的密文，我们可以使用 ``openssl_decrypt`` 函数。我们来看一个例子:

.. code-block:: php

    <?php
    openssl_decrypt($cipherText, $encryptionMethod, $secretKey, 0, $iv);

openssl_decrypt有五个参数：

- $cipherText  - 需要解密的加密文本；
- $encryptionMethod  - 加密方法(使用openssl_get_cipher_methods，可用的方法)；
- $secretKey  - 一个密钥(需要保密)；
- $options  -  OPENSSL_RAW_DATA或OPENSSL_ZERO_PADDING(默认为0)；
- $iv  - 一个初始化向量(恰好16位)；

执行上述操作将返回纯文本数据。


非对称加密和解密
================
我们将使用非对称（公钥/私钥）加密。在这种加密中，用户生成一对公钥/私钥，并将公钥发给任何想要发送数据的人。数据的发送者将使用接收者的公钥来加密数据。接收方将使用他自己的私钥解密接收到的数据。使用公钥加密的数据只能使用相应的私钥解密。

您可以加密的数据量和生成的加密数据的大小都由密钥的大小决定。加密数据的大小是密钥中的字节数（向上舍入）。因此，对于1024位密钥，这将是128个字节（1024除以8）。即使您要加密其中包含单个字节的字符串，生成的加密数据仍将是128个字节。可以加密的最大数据量比这少11个字节。所以对于1024位密钥，最多可以加密117个字节。

我们无法一次加密并发送大量数据，所以我们需要将它分成更小的块。在下面的例子中，我们将数据在加密前分成多个块，然后加密它，然后组合加密的数据并发送它。然后接收者将加密的数据分解成块并解密。

生成公钥/私钥
-------------
我们首先需要一对公钥/私钥。下面是一个生成公钥/私钥的示例 ``PHP`` 代码。

.. code-block:: php

    <?php
    $privateKey = openssl_pkey_new(array(
        'private_key_bits' => 2048,      // Size of Key.
        'private_key_type' => OPENSSL_KEYTYPE_RSA,
    ));
    // Save the private key to private.key file. Never share this file with anyone.
    openssl_pkey_export_to_file($privateKey, 'private.key');

    // Generate the public key for the private key
    $a_key = openssl_pkey_get_details($privateKey);
    // Save the public key in public.key file. Send this file to anyone who want to send you the encrypted data.
    file_put_contents('public.key', $a_key['key']);

    // Free the private Key.
    openssl_free_key($privateKey);

上面的代码将生成一对公钥/私钥。切勿与任何人分享私钥。将公钥发给任何会向您发送加密数据的人。

加密数据
--------
以下是可用于加密数据的代码。此代码假定我们已经拥有收件人的公钥。

.. code-block:: php

    <?php
    // Data to be sent
    $plaintext = 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean eleifend vestibulum nunc sit amet mattis. Nulla at volutpat nulla. Pellentesque sodales vel ligula quis consequat. Suspendisse dapibus dolor nec viverra venenatis. Pellentesque blandit vehicula eleifend. Duis eget fermentum velit. Vivamus varius ut dui vel malesuada. Ut adipiscing est non magna posuere ullamcorper. Proin pretium nibh nec elementum tincidunt. Vestibulum leo urna, porttitor et aliquet id, ornare at nibh. Maecenas placerat justo nunc, varius condimentum diam fringilla sed. Donec auctor tellus vitae justo venenatis, sit amet vulputate felis accumsan. Aenean aliquet bibendum magna, ac adipiscing orci venenatis vitae.';

    echo 'Plain text: ' . $plaintext;
    // Compress the data to be sent
    $plaintext = gzcompress($plaintext);

    // Get the public Key of the recipient
    $publicKey = openssl_pkey_get_public('file:///path/to/public.key');
    $a_key = openssl_pkey_get_details($publicKey);

    // Encrypt the data in small chunks and then combine and send it.
    $chunkSize = ceil($a_key['bits'] / 8) - 11;
    $output = '';

    while ($plaintext)
    {
        $chunk = substr($plaintext, 0, $chunkSize);
        $plaintext = substr($plaintext, $chunkSize);
        $encrypted = '';
        if (!openssl_public_encrypt($chunk, $encrypted, $publicKey))
        {
            die('Failed to encrypt data');
        }
        $output .= $encrypted;
    }
    openssl_free_key($publicKey);

    // This is the final encrypted data to be sent to the recipient
    $encrypted = $output;

解密数据
--------
一旦用户使用他的公钥接收到加密数据，用户可以使用他自己的私钥对其解密。以下是解密加密数据的示例代码。

.. code-block:: php

    <?php
    // Get the private Key
    if (!$privateKey = openssl_pkey_get_private('file:///path/to/private.key'))
    {
        die('Private Key failed');
    }
    $a_key = openssl_pkey_get_details($privateKey);

    // Decrypt the data in the small chunks
    $chunkSize = ceil($a_key['bits'] / 8);
    $output = '';

    while ($encrypted)
    {
        $chunk = substr($encrypted, 0, $chunkSize);
        $encrypted = substr($encrypted, $chunkSize);
        $decrypted = '';
        if (!openssl_private_decrypt($chunk, $decrypted, $privateKey))
        {
            die('Failed to decrypt data');
        }
        $output .= $decrypted;
    }
    openssl_free_key($privateKey);

    // Uncompress the unencrypted data.
    $output = gzuncompress($output);

    echo '<br /><br /> Unencrypted Data: ' . $output;

所有OpenSSL的PHP​​函数列表都可以在 `OpenSSL函数 <http://www.php.net/manual/en/ref.openssl.php>`_ 中找到。