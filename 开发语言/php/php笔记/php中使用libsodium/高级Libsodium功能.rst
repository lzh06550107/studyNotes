*************
高级Libsodium功能
*************
本章中介绍的功能适用于高级开发人员。如果使用不当，某些功能可能会非常危险，因此寻求通用加密解决方案的开发人员不鼓励它们使用。

在决定是否使用某个功能之前，请检查 `快速参考 <https://paragonie.com/blog/2017/06/libsodium-quick-reference-quick-comparison-similar-functions-and-which-one-use>`_ 页面，该页面说明每个函数的作用以及每个函数的使用位置。

要查看旧的 ``API`` 文档， `请点击此处 <https://github.com/paragonie/pecl-libsodium-doc/blob/v1/chapters/08-advanced.md>`_

高级的秘密密钥加密技术
======================

（这个空间保留给CAESAR比赛冠军）

有一个名为 ``CAESAR`` 的密码学竞赛正在进行，它将确定下一代秘密密钥加密认证算法。 ``CAESAR`` 获胜者预计将于2017年12月公布，并应在不久之后在 ``Libsodium`` 上作为 ``crypto_aead()`` 发布。同时，您可以使用 ``crypto_aead_chacha20poly1305`` API。

通过关联数据进行身份验证（秘密密钥）加密 - ChaCha20 + Poly1305
--------------------------------------------------------------
``ChaCha20 + Poly1305`` 是由 ``ChaCha20`` 流密码和 ``Poly1305`` 消息认证码( ``MAC`` )结合的一种应用在互联网安全协议中的认证加密算法。

与 ``crypto_secretbox`` API类似，除了其底层算法是 ``chacha20poly1305`` 而不是 ``xsalsa20poly1305`` 和可选的非保密（非加密）数据可以包含在 ``Poly1305`` 认证标签验证中。

您应该更喜欢 `IETF变体 <https://paragonie.com/book/pecl-libsodium/read/08-advanced.md#crypto-aead-chacha20poly1305-ietf>`_ 。

从 ``Libsodium`` 文档，此操作：

- 使用密钥和随机数加密消息以保密。
- 计算认证标签。该标签用于确保消息以及可选的非机密（非加密）数据未被篡改。

额外数据的典型用例是存储关于消息的协议特定元数据，例如其长度和编码。

所选择的结构使用加密，然后 ``MAC`` ，并且在验证之前，解密或部分解密将永远不会执行。

由于这是一个秘密密钥加密函数，因此您可以生成一个加密密钥，如下所示：

.. code-block:: php

    <?php
    $key = random_bytes(SODIUM_CRYPTO_AEAD_CHACHA20POLY1305_KEYBYTES);


AEAD加密
^^^^^^^^

.. code-block:: php

    <?php
    string sodium_crypto_aead_chacha20poly1305_encrypt(string $confidential_message, string $public_message, string $nonce, string $key)

像 ``crypto_secretbox`` 一样，您不应该重复使用相同的随机数和密钥。

.. code-block:: php

    <?php
    $nonce = random_bytes(SODIUM_CRYPTO_AEAD_CHACHA20POLY1305_NPUBBYTES);
    $ad = 'Additional (public) data';
    $ciphertext = sodium_crypto_aead_chacha20poly1305_encrypt(
        $message,
        $ad,
        $nonce,
        $key
    );

AEAD解密
^^^^^^^^

.. code-block:: php

    <?php
    string sodium_crypto_aead_chacha20poly1305_decrypt(string $confidential_message, string $public_message, string $nonce, string $key)

.. code-block:: php

    <?php
    $decrypted = sodium_crypto_aead_chacha20poly1305_decrypt(
        $ciphertext,
        $ad,
        $nonce,
        $key
    );
    if ($decrypted === false) {
        throw new Exception("Bad ciphertext");
    }

通过关联数据进行身份验证(秘密密钥)加密 - ChaCha20 + Poly1305（IETF变体）
-----------------------------------------------------------------------
``ChaCha20-Poly1305`` 的 ``IETF`` 变体使用 ``96`` 位随机数( ``12`` 字节)而不是 ``64`` 位随机数( ``8`` 字节)。

AEAD加密
^^^^^^^^

.. code-block:: php

    <?php
    string sodium_crypto_aead_chacha20poly1305_ietf_encrypt(string $confidential_message, string $public_message, string $nonce, string $key)

.. code-block:: php

    <?php
    $nonce = random_bytes(SODIUM_CRYPTO_AEAD_CHACHA20POLY1305_IETF_NPUBBYTES);
    $ad = 'Additional (public) data';
    $ciphertext = sodium_crypto_aead_chacha20poly1305_ietf_encrypt(
        $message,
        $ad,
        $nonce,
        $key
    );

AEAD解密
^^^^^^^^

.. code-block:: php

    <?php
    string|bool sodium_crypto_aead_chacha20poly1305_ietf_decrypt(string $confidential_message, string $public_message, string $nonce, string $key)

.. code-block:: php

    <?php
    $decrypted = sodium_crypto_aead_chacha20poly1305_ietf_decrypt(
        $ciphertext,
        $ad,
        $nonce,
        $key
    );
    if ($decrypted === false) {
        throw new Exception("Bad ciphertext");
    }

通过关联数据进行身份验证(秘密密钥)加密 -  XChaCha20 + Poly1305
--------------------------------------------------------------
这是 ``ChaCha20-Poly1305`` 的随机扩展变体，它使用 ``24`` 字节的随机数而不是 ``8`` 字节或 ``12`` 字节的随机数。它遵循 ``IETF`` 兼容性构造。

AEAD加密
^^^^^^^^

.. code-block:: php

    <?php
    string sodium_crypto_aead_xchacha20poly1305_ietf_encrypt(string $confidential_message, string $public_message, string $nonce, string $key)

.. code-block:: php

    <?php
    $nonce = random_bytes(SODIUM_CRYPTO_AEAD_XCHACHA20POLY1305_IETF_NPUBBYTES);
    $ad = 'Additional (public) data';
    $ciphertext = sodium_crypto_aead_xchacha20poly1305_ietf_encrypt(
        $message,
        $ad,
        $nonce,
        $key
    );

AEAD解密
^^^^^^^^

.. code-block:: php

    <?php
    string|bool sodium_crypto_aead_xchacha20poly1305_ietf_decrypt(string $confidential_message, string $public_message, string $nonce, string $key)

.. code-block:: php

    <?php
    $decrypted = sodium_crypto_aead_xchacha20poly1305_ietf_decrypt(
        $ciphertext,
        $ad,
        $nonce,
        $key
    );
    if ($decrypted === false) {
        throw new Exception("Bad ciphertext");
    }

通过关联数据进行身份验证(秘密密钥)加密 -  AES-256 + GCM
-------------------------------------------------------
当被 ``CPU`` 支持时， ``AES-256-GCM`` 是这个库中最快的 ``AEAD`` 密码。当 ``CPU`` 不支持时， **加密/解密功能不可用** 。

.. code-block:: php

    <?php
    bool sodium_crypto_aead_aes256gcm_is_available()

确保在尝试使用它之前检查 ``AES-256-GCM`` 是否可用。

从Libsodium文档，此操作：

- 使用密钥和随机数加密消息以保密。
- 计算认证标签。该标签用于确保消息以及可选的非机密（非加密）数据未被篡改。

额外数据的典型用例是存储关于消息的协议特定元数据，例如其长度和编码。所选择的结构使用加密，然后 ``MAC`` ，并且在验证之前，解密甚至部分解密将永远不会执行。

由于这是一个秘密密钥加密函数，因此您可以生成一个加密密钥，如下所示：

.. code-block:: php

    <?php
    $key = random_bytes(SODIUM_CRYPTO_AEAD_AES256GCM_KEYBYTES);

AEAD加密
^^^^^^^^

.. code-block:: php

    <?php
    string sodium_crypto_aead_aes256gcm_encrypt(string $confidential_message, string $public_message, string $nonce, string $key)

像 ``crypto_secretbox`` 一样，您不应该重复使用相同的随机数和密钥。

.. code-block:: php

    <?php
    if (sodium_crypto_aead_aes256gcm_is_available()) {
        $nonce = random_bytes(SODIUM_CRYPTO_AEAD_AES256GCM_NPUBBYTES);
        $ad = 'Additional (public) data';
        $ciphertext = sodium_crypto_aead_aes256gcm_encrypt(
            $message,
            $ad,
            $nonce,
            $key
        );
    }

AEAD解密
^^^^^^^^

.. code-block:: php

    <?php
    string|bool sodium_crypto_aead_aes256gcm_decrypt(string $confidential_message, string $public_message, string $nonce, string $key)

.. code-block:: php

    <?php
    if (sodium_crypto_aead_aes256gcm_is_available()) {
        $decrypted = sodium_crypto_aead_aes256gcm_decrypt(
            $ciphertext,
            $ad,
            $nonce,
            $key
        );
        if ($decrypted === false) {
            throw new Exception("Bad ciphertext");
        }
    }

密钥加密（不进行身份验证）
-------------------------
在使用这些功能之前，您应该确保您了解与 `未经身份验证的加密相关的风险 <https://paragonie.com/blog/2015/05/using-encryption-and-authentication-correctly>`_ 。

使用流密码加密消息，无需身份验证
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    string sodium_crypto_stream_xor($message, $nonce, $key)

此操作使用密钥和随机数对消息进行加密或解密。但是，密文不包含认证标签，这意味着不可能验证该消息未被篡改。

除非您特别需要未经身份验证的加密，否则 ``sodium_crypto_secretbox()`` 是您应该使用的操作。

.. code-block:: php

    <?php
    $nonce = random_bytes(SODIUM_CRYPTO_STREAM_NONCEBYTES);
    $key = random_bytes(SODIUM_CRYPTO_STREAM_KEYBYTES);

    // This operation is reversible:
    $ciphertext = sodium_crypto_stream_xor('test', $nonce, $key);
    $plaintext = sodium_crypto_stream_xor($ciphertext, $nonce, $key);

来自流密码的伪随机字节
^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    string sodium_crypto_stream(int $length, string $nonce, string $key)

您可以使用 ``crypto_stream`` 生成一串伪随机字节。注意不要对相同的密钥重复使用某个随机数。

.. code-block:: php

    <?php
    $nonce = random_bytes(SODIUM_CRYPTO_STREAM_NONCEBYTES);
    $key = random_bytes(SODIUM_CRYPTO_STREAM_KEYBYTES);

    // Derive $length pseudorandom bytes from the nonce and the key
    $stream = sodium_crypto_stream($length, $nonce, $key);


高级公钥加密技术
================

密封盒（匿名公钥加密）
---------------------
密封盒旨在匿名发送消息给予其公钥的收件人。

只有收件人可以使用他们的私钥解密这些消息。收件人可以验证邮件的完整性，但无法验证发件人的身份。

使用临时密钥对加密消息，其密钥部分在加密过程之后立即销毁。

在不知道用于给定消息的密钥的情况下，发送者以后不能解密它自己的消息。没有额外的数据，一条消息就不能与其发送者的身份相关联。

密封的盒子加密
^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    string sodium_crypto_box_seal(string $message, string $publickey)

这将使用用户的公钥加密消息。

.. code-block:: php

    <?php
    $anonymous_message_to_bob = sodium_crypto_box_seal(
        $message,
        $bob_box_publickey
    );

密封盒解密
^^^^^^^^^^

.. code-block:: php

    <?php
    string|bool sodium_crypto_box_seal_open(string $message, string $recipient_keypair)

使用一个带有密钥和公钥的密钥对来打开密封盒。

.. code-block:: php

    <?php
    $bob_box_kp = sodium_crypto_box_keypair_from_secretkey_and_publickey(
        $bob_box_seceretkey,
        $bob_box_publickey
    );
    $decrypted_message = sodium_crypto_box_seal_open(
        $anonymous_message_to_bob,
        $bob_box_kp
    );

标量乘法（椭圆曲线密码学）
=========================
``Sodium`` 为 ``Curve25519`` 提供了一个 ``API`` ，这是一种适用于各种应用的最先进的 ``Diffie-Hellman`` 函数。

.. code-block:: php

    <?php
    string sodium_crypto_scalarmult(string $key_1, string $key_2)

``crypto_scalarmult``  API允许从您的密钥和其他用户的公钥中获取共享密钥。它还允许从您的密钥中派生您的公钥。

将 ``crypto_sign`` 密钥转换为 ``crypto_box`` 密钥
--------------------------------------------------

.. code-block:: php

    <?php
    string sodium_crypto_sign_ed25519_sk_to_curve25519(string $ed25519sk)

传递一个 ``crypto_sign`` 密钥，获得相应的 ``crypto_box`` 密钥。

.. code-block:: php

    <?php
    string sodium_crypto_sign_ed25519_pk_to_curve25519(string $ed25519pk)

传递一个 ``crypto_sign`` 公钥，得到相应的 ``crypto_box`` 公钥。

从秘钥获得公钥
==============

.. code-block:: php

    <?php
    string sodium_crypto_box_publickey_from_secretkey(string $secretkey)

这非常简单。

.. code-block:: php

    <?php
    $alice_box_publickey = sodium_crypto_box_publickey_from_secretkey(
        $alice_box_secretkey
    );

函数 ``sodium_crypto_scalarmult_base()`` 是 ``sodium_crypto_box_publickey_from_secretkey()`` 的别名。

.. code-block:: php

    <?php
    string sodium_crypto_sign_publickey_from_secretkey(string $secretkey)

和上面一样，只是用 ``crypto_sign`` 替换 ``crypto_box`` :

.. code-block:: php

    <?php
    $alice_sign_publickey = sodium_crypto_sign_publickey_from_secretkey(
        $alice_sign_secretkey
    );

``Elliptic Curve Diffie Hellman`` 密钥交换
==========================================

.. code-block:: php

    <?php
    string sodium_crypto_kx(string $secretkey, string $publickey, string $client_publickey, string $server_publickey)

使用 ``Elliptic Curve Diffie Hellman`` 在 ``Curve25519`` 上计算共享密钥。

.. code-block:: php

    <?php
    // Alice's computer:
    $alice_sharedsecret = sodium_crypto_kx(
        $alice_box_secretkey, $bob_box_publickey,
        $alice_box_publickey, $bob_box_publickey
    );

    // Bob's computer:
    $bob_sharedsecret = sodium_crypto_kx( // 存在问题？？？
        $bob_box_secretkey, $alice_box_publickey,
        $alice_box_publickey, $bob_box_publickey
    );

额外信息
========

- 相关的Libsodium文档页面：

  + `crypto_aead <https://download.libsodium.org/doc/secret-key_cryptography/aead.html>`_
  + `crypto_stream <https://download.libsodium.org/doc/advanced/xsalsa20.html>`_
  + `crypto_box_seal <https://download.libsodium.org/doc/public-key_cryptography/sealed_boxes.html>`_
  + `crypto_scalarmult <https://download.libsodium.org/doc/advanced/scalar_multiplication.html>`_

- `CAESAR:认证加密竞赛：安全性，适用性和稳健性 <http://competitions.cr.yp.to/caesar.html>`_







