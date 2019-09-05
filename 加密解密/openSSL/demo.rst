
遵循如下最佳实践：

- 在 CBC 模式下使用 AES256
- 与其他AES实现兼容，但不兼容 mcrypt ，因为 mcrypt 使用 PKCS＃5 而不是 PKCS＃7 。
- 使用 SHA256 从提供的密码生成密钥
- 生成加密数据的 hmac 哈希以进行完整性检查
- 为每条消息生成随机 IV 将 IV（16字节）和散列（32字节）预先添加到密文
- 应该非常安全

``IV`` 是一个公共信息，每个消息都需要随机。哈希确保数据未被篡改。

.. code-block:: php

	function encrypt($plaintext, $password) {
	    $method = "AES-256-CBC";
	    $key = hash('sha256', $password, true);
	    $iv = openssl_random_pseudo_bytes(16);

	    $ciphertext = openssl_encrypt($plaintext, $method, $key, OPENSSL_RAW_DATA, $iv);
	    $hash = hash_hmac('sha256', $ciphertext, $key, true);

	    return $iv . $hash . $ciphertext;
	}

	function decrypt($ivHashCiphertext, $password) {
	    $method = "AES-256-CBC";
	    $iv = substr($ivHashCiphertext, 0, 16);
	    $hash = substr($ivHashCiphertext, 16, 32);
	    $ciphertext = substr($ivHashCiphertext, 48);
	    $key = hash('sha256', $password, true);

	    if (hash_hmac('sha256', $ciphertext, $key, true) !== $hash) return null;

	    return openssl_decrypt($ciphertext, $method, $key, OPENSSL_RAW_DATA, $iv);
	}

	$encrypted = encrypt('Plaintext string.', 'password'); // this yields a binary string

	echo decrypt($encrypted, 'password');
	// decrypt($encrypted, 'wrong password') === null

	////////////////////////分割线/////////////////////
class UnsafeCrypto
{
    const METHOD = 'aes-256-ctr';

    /**
     * Encrypts (but does not authenticate) a message
     * 
     * @param string $message - plaintext message
     * @param string $key - encryption key (raw binary expected)
     * @param boolean $encode - set to TRUE to return a base64-encoded 
     * @return string (raw binary)
     */
    public static function encrypt($message, $key, $encode = false)
    {
        $nonceSize = openssl_cipher_iv_length(self::METHOD);
        $nonce = openssl_random_pseudo_bytes($nonceSize);

        $ciphertext = openssl_encrypt(
            $message,
            self::METHOD,
            $key,
            OPENSSL_RAW_DATA,
            $nonce
        );

        // Now let's pack the IV and the ciphertext together
        // Naively, we can just concatenate
        if ($encode) {
            return base64_encode($nonce.$ciphertext);
        }
        return $nonce.$ciphertext;
    }

    /**
     * Decrypts (but does not verify) a message
     * 
     * @param string $message - ciphertext message
     * @param string $key - encryption key (raw binary expected)
     * @param boolean $encoded - are we expecting an encoded string?
     * @return string
     */
    public static function decrypt($message, $key, $encoded = false)
    {
        if ($encoded) {
            $message = base64_decode($message, true);
            if ($message === false) {
                throw new Exception('Encryption failure');
            }
        }

        $nonceSize = openssl_cipher_iv_length(self::METHOD);
        $nonce = mb_substr($message, 0, $nonceSize, '8bit');
        $ciphertext = mb_substr($message, $nonceSize, null, '8bit');

        $plaintext = openssl_decrypt(
            $ciphertext,
            self::METHOD,
            $key,
            OPENSSL_RAW_DATA,
            $nonce
        );

        return $plaintext;
    }
}

https://stackoverflow.com/questions/9262109/simplest-two-way-encryption-using-php