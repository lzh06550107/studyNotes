********************
openssl 下的对称加密和非对称加密
********************

对称加密: 在加密和解密过程中使用相同的密钥，或是两个可以简单地相互推算的密钥的加密算法。

非对称加密: 也称为公开加密，它需要一个密钥对，一个是公钥，一个是私钥，一个负责加密，一个负责解密。

对称加密在性能上要优于非对称加密，但是安全性低于非对称加密。

``PHP 7.1`` 之后的对称加密和非对称加密都需要借助 ``openssl`` 扩展实现。 ``mcrypt`` 库已经被移除。

分组加密算法 ``CBC`` 模式中经常涉及到 ``padding`` 的情况。 ``PKCS7Padding`` 和 ``PKCS5Padding`` 的规则其实是一样的。

例如 ``3DES`` ， ``AES`` ， ``Blowfish`` 这些分组加密算法，要求数据块的大小是一定字节对齐的。

拿 ``AES-128-CBC`` 来说，要求输入是 ``128/8=16`` 字节对齐的，它使用 ``PKCS7Padding`` ，规则是：

1. 计算输入长度为 ``L`` 。
2. 如果 ``L`` 是 ``16`` 倍数， ``padding`` 长度为 ``16`` ， ``padding`` 值为 ``0x10`` 。即在输入后面补齐 ``16`` 字节的 ``0x10`` 。
3. 如果 ``L`` 不是 ``16`` 倍数， ``padding`` 长度为 ``16-L%16`` ， ``padding`` 值为 ``16-L%16`` 。即在输入后面补齐 ``16-L%16`` 的字节，值为 ``16-L%16`` 。

``2`` 和 ``3`` 其实可以根据公式 ``16-L%16`` 写成一条规则，这里只是强调 ``2`` 的特殊性，明确并不是不做补齐。

``PKCS5Padding`` 是专门针对 ``8`` 字节分组的 ``3DES`` 等设计的，规则和上面一样，只是计算时看 ``L`` 是否是 ``8`` 的倍数。

对称加密函数
============

- ``openssl_get_cipher_methods()`` ：返回 ``openssl`` 支持的所有加密方式；
- ``openssl_encrypt($data, $method, $key, $options = 0, $iv = '')`` ：以指定方式 ``method`` 和密钥 ``key`` 加密 ``data`` ，返回 ``false`` 或加密后的数据；

  + ``data`` : 明文
  + ``method`` : 加密算法
  + ``key`` : 密钥
  + ``options`` : 可能值为默认值 ``0`` ，或者 ``OPENSSL_RAW_DATA=1 OPENSSL_ZERO_PADDING=2 OPENSSL_NO_PADDING=3``

    - ``0`` : 自动对明文进行 ``padding`` ，返回的数据经过 ``base64`` 编码；
    - ``1`` : ``OPENSSL_RAW_DATA`` ，自动对明文进行 ``padding`` ，但返回的结果未经过 ``base64`` 编码；
    - ``2`` : ``OPENSSL_ZERO_PADDING`` ，自动对明文进行 ``0`` 填充，返回的结果经过 ``base64`` 编码。但是， ``openssl`` 不推荐 ``0`` 填充的方式，即使选择此项也不会自动进行 ``padding`` ，仍需手动 ``padding`` ；
    - 可以组合 OPENSSL_RAW_DATA|OPENSSL_ZERO_PADDING 来组合设置；

  + ``iv`` : 非空的初始化向量，不使用此项会抛出一个警告；

- ``openssl_decrypt($data, $method, $key, $options = 0, $iv = '')`` : 解密数据；
- ``openssl_cipher_iv_length($method)`` : 获取 ``method`` 要求的初始化向量的长度；
- ``openssl_random_pseudo_bytes($length)`` : 生成指定长度的伪随机字符串，可用于生产初始向量
- ``hash_mac($method, $data, $key, $raw_out)`` : 生成带有密钥的哈希值，可用于对传输信息进行签名

  + ``method`` : 加密算法；
  + ``data`` : 明文；
  + ``key`` : 密钥；
  + ``raw_output`` :

    - ``TRUE`` : 输出原始二进制数据
    - ``FALSE`` : 输出长度固定的小写 ``16`` 进制字符串

$options选项我们使用默认0时：

- 默认选项下加密结果直接返回 ``base64`` 加密串；
- 默认选项下会自动使用 ``PKCS7`` 进行填充。

使用 ``OPENSSL_ZERO_PADDING`` 选项来进行加密，其它保持一样。即将 ``$options`` 的值由 ``0`` 改为 ``OPENSSL_ZERO_PADDING`` 也就是 ``2`` ，结果返回了 ``false`` 。

- ``openssl`` 不推荐补 ``0`` 的方式，所以就算使用了该选项也一样不会自动进行 ``padding`` ，仍然需要手动进行 ``padding`` ；
- 返回结果已经是 ``base64`` 字符串；

使用 ``OPENSSL_NO_PADDING`` 的方式来进行加密，其它保持一样。即将 ``$options`` 的值由 ``0`` 改为 ``OPENSSL_NO_PADDING`` 也就是 ``3`` ，结果返回了 ``false`` 。

- ``openssl`` 是不支持 ``No Padding`` 的，如果选择 ``OPENSSL_NO_PADDING`` ，就得自己手动进行 ``padding`` ；
- ``padding`` 之后返回结果默认也没有进行 ``base64`` 编码，需要手动进行；

使用 ``OPENSSL_RAW_DATA`` 的方式来进行加密，其它保持一样。返回了加密结果，但没有进行 ``base64`` 编码。

- 该方式会自动进行 ``PKCS7`` 方式的填充；
- 返回的结果需要自行进行 ``base64`` ；

主流的对称加密方式有 ``DES`` ，  ``AES`` 。这两种加密方式都属于分组加密， 先将明文分成多个等长的模块 ( block )， 然后进行加密。

DES 加密
--------
``DES`` 加密的密钥长度为 ``64 bit`` ，实际应用中有效使用的是 ``56`` 位，剩余 ``8`` 位作为奇偶校验位。明文按 ``64`` bit ( ``UTF-8`` 下为 ``8`` 个字节长度 ) 进行分组，每 ``64`` 位分成一组 ( 最后一组不足 ``64`` 位的需要填充数据 )，分组后的明文组和密钥按位替代或交换的方法形成密文组。

DES 是对称性加密里面常见一种，全称为 Data Encryption Standard，即数据加密标准，是一种使用密钥加密的块算法。密钥长度是64位(bit)，超过位数密钥被忽略。所谓对称性加密即加密和解密密钥相同，对称性加密一般会按照固定长度，把待加密字符串分成块，不足一整块或者刚好最后有特殊填充字符。

跨语言做 DES 加密解密经常会出现问题，往往是填充方式不对、编码不一致或者加密解密模式没有对应上造成。常见的填充模式有： pkcs5、pkcs7、iso10126、ansix923、zero。加密模式有：DES-ECB、DES-CBC、DES-CTR、DES-OFB、DES-CFB。


.. code-block:: php

	class DES
	{
	    private $method = 'DES-CBC';
	    private $key;

	    public function __construct($key)
	    {
	        // 密钥长度不能超过64bit(UTF-8下为8个字符长度),超过64bit不会影响程序运行,但有效使用的部分只有64bit,多余部分无效,可通过openssl_error_string()查看错误提示
	        $this->key = $key;
	    }

	    public function encrypt($plaintext)
	    {
	        // 生成加密所需的初始化向量, 加密时缺失iv会抛出一个警告
	        $ivlen = openssl_cipher_iv_length($this->method);
	        $iv = openssl_random_pseudo_bytes($ivlen);

	        // 按64bit一组填充明文
	        $plaintext = $this->padding($plaintext);
	        // 加密数据
	        $ciphertext = openssl_encrypt($plaintext, $this->method, $this->key, 1, $iv);
	        // 生成hash
	        $hash = hash_hmac('sha256', $ciphertext, $this->key, false);

	        return base64_encode($iv . $hash . $ciphertext);

	    }

	    public function decrypt($ciphertext)
	    {
	        $ciphertext = base64_decode($ciphertext);
	        // 从密文中获取iv
	        $ivlen = openssl_cipher_iv_length($this->method);
	        $iv = substr($ciphertext, 0, $ivlen);
	        // 从密文中获取hash
	        $hash = substr($ciphertext, $ivlen, 64);
	        // 获取原始密文
	        $ciphertext = substr($ciphertext, $ivlen + 64);
	        // hash校验
	        if(hash_equals($hash, hash_hmac('sha256', $ciphertext, $this->key, false)))
	        {
	            // 解密数据
	            $ciphertext = openssl_decrypt($ciphertext, $this->method, '12345678', 1, $iv) ?? false;
	            // 去除填充数据
	            $plaintext = $ciphertext ? $this->unpadding($ciphertext) : false;

	            return $plaintext;
	        }

	        return '解密失败';
	    }

	    // 按64bit一组填充数据
	    private function padding($plaintext)
	    {
	        $padding = 8 - (strlen($plaintext)%8);
	        $chr = chr($padding);

	        return $plaintext . str_repeat($chr, $padding);
	    }

	    private function unpadding($ciphertext)
	    {
	        $chr = substr($ciphertext, -1);
	        $padding = ord($chr);

	        if($padding > strlen($ciphertext))
	        {
	            return false;
	        }
	        if(strspn($ciphertext, $chr, -1 * $padding, $padding) !== $padding)
	        {
	            return false;
	        }

	        return substr($ciphertext, 0, -1 * $padding);
	    }
	}

	/**
	 * openssl 实现的 DES 加密类，支持各种 PHP 版本
	 */
	class DES
	{
	    /**
	     * @var string $method 加解密方法，可通过 openssl_get_cipher_methods() 获得
	     */
	    protected $method;

	    /**
	     * @var string $key 加解密的密钥
	     */
	    protected $key;

	    /**
	     * @var string $output 输出格式 无、base64、hex
	     */
	    protected $output;

	    /**
	     * @var string $iv 加解密的向量
	     */
	    protected $iv;

	    /**
	     * @var string $options
	     */
	    protected $options;

	    // output 的类型
	    const OUTPUT_NULL = '';
	    const OUTPUT_BASE64 = 'base64';
	    const OUTPUT_HEX = 'hex';


	    /**
	     * DES constructor.
	     * @param string $key
	     * @param string $method
	     *      ECB DES-ECB、DES-EDE3 （为 ECB 模式时，$iv 为空即可）
	     *      CBC DES-CBC、DES-EDE3-CBC、DESX-CBC
	     *      CFB DES-CFB8、DES-EDE3-CFB8
	     *      CTR
	     *      OFB
	     *
	     * @param string $output
	     *      base64、hex
	     *
	     * @param string $iv
	     * @param int $options
	     */
	    public function __construct($key, $method = 'DES-ECB', $output = '', $iv = '', $options = OPENSSL_RAW_DATA | OPENSSL_NO_PADDING) // 不填充，返回原始密文
	    {
	        $this->key = $key;
	        $this->method = $method;
	        $this->output = $output;
	        $this->iv = $iv;
	        $this->options = $options;
	    }

	    /**
	     * 加密
	     *
	     * @param $str
	     * @return string
	     */
	    public function encrypt($str)
	    {
	        $str = $this->pkcsPadding($str, 8);
	        $sign = openssl_encrypt($str, $this->method, $this->key, $this->options, $this->iv);

	        if ($this->output == self::OUTPUT_BASE64) {
	            $sign = base64_encode($sign);
	        } else if ($this->output == self::OUTPUT_HEX) {
	            $sign = bin2hex($sign);
	        }

	        return $sign;
	    }

	    /**
	     * 解密
	     *
	     * @param $encrypted
	     * @return string
	     */
	    public function decrypt($encrypted)
	    {
	        if ($this->output == self::OUTPUT_BASE64) {
	            $encrypted = base64_decode($encrypted);
	        } else if ($this->output == self::OUTPUT_HEX) {
	            $encrypted = hex2bin($encrypted);
	        }

	        $sign = @openssl_decrypt($encrypted, $this->method, $this->key, $this->options, $this->iv);
	        $sign = $this->unPkcsPadding($sign);
	        $sign = rtrim($sign);
	        return $sign;
	    }

	    /**
	     * 填充
	     *
	     * @param $str
	     * @param $blocksize
	     * @return string
	     */
	    private function pkcsPadding($str, $blocksize)
	    {
	        $pad = $blocksize - (strlen($str) % $blocksize);
	        return $str . str_repeat(chr($pad), $pad);
	    }

	    /**
	     * 去填充
	     * 
	     * @param $str
	     * @return string
	     */
	    private function unPkcsPadding($str)
	    {
	        $pad = ord($str{strlen($str) - 1});
	        if ($pad > strlen($str)) {
	            return false;
	        }
	        return substr($str, 0, -1 * $pad);
	    }

	}


	$key = 'key123456';
	$iv = 'iv123456';

	// DES CBC 加解密
	$des = new DES($key, 'DES-CBC', DES::OUTPUT_BASE64, $iv);
	echo $base64Sign = $des->encrypt('Hello DES CBC');
	echo "\n";
	echo $des->decrypt($base64Sign);
	echo "\n";

	// DES ECB 加解密
	$des = new DES($key, 'DES-ECB', DES::OUTPUT_HEX);
	echo $base64Sign = $des->encrypt('Hello DES ECB');
	echo "\n";
	echo $des->decrypt($base64Sign);


AES 加密
--------
``AES`` 加密的分组长度是 ``128`` 位，即每个分组为 ``16`` 个字节 ( 每个字节 ``8`` 位 )。密钥的长度根据加密方式的不同可以是 ``128`` 位， ``192`` 位， ``256`` 位。密钥长度超过指定长度时，超出部分无效。

+---------+-----------------+-----------------+
| AES     | 密钥长度 ( 位 ) | 分组长度 ( 位 ) |
+=========+=================+=================+
| AES-128 | 128             | 128             |
+---------+-----------------+-----------------+
| AES-192 | 192             | 128             |
+---------+-----------------+-----------------+
| AES-256 | 256             | 128             |
+---------+-----------------+-----------------+

.. code-block:: php

	class AES
	{
	    private $key;
	    private $method = 'aes-128-cbc';

	    public function __construct($key)
	    {
	        // 是否启用了openssl扩展
	        extension_loaded('openssl') or die('未启用 OPENSSL 扩展');
	        $this->key = $key;
	    }

	    public function encrypt($plaintext)
	    {
	        if(!in_array($this->method, openssl_get_cipher_methods()))
	        {
	            die('不支持该加密算法!');
	        }
	        $plaintext = $this->padding($plaintext);
	        // 获取加密算法要求的初始化向量的长度
	        $ivlen = openssl_cipher_iv_length($this->method);
	        // 生成对应长度的初始化向量
	        $iv = openssl_random_pseudo_bytes($ivlen);
	        // 加密数据
	        $ciphertext = openssl_encrypt($plaintext, $this->method, $this->key, 1, $iv);
	        $hmac = hash_hmac('sha256', $ciphertext, $this->key, false);

	        return base64_encode($iv . $hmac . $ciphertext);
	    }

	    public function decrypt($ciphertext)
	    {
	        $ciphertext = base64_decode($ciphertext);
	        $ivlen = openssl_cipher_iv_length($this->method);
	        $iv = substr($ciphertext, 0, $ivlen);
	        $hmac = substr($ciphertext, $ivlen, 64);
	        $ciphertext = substr($ciphertext, $ivlen + 64);
	        $verifyHmac = hash_hmac('sha256', $ciphertext, $this->key, false);
	        if(hash_equals($hmac, $verifyHmac))
	        {
	            $plaintext = openssl_decrypt($ciphertext, $this->method, $this->key, 1, $iv)??false;
	            if($plaintext)
	            {
	                $plaintext = $this->unpadding($plaintext);
	                echo $plaintext;
	            }

	            return $plaintext;
	        }else
	        {
	            die('数据被修改!');
	        }
	    }

	    private function padding(string $data) : string
	    {
	        $padding = 16 - (strlen($data) % 16);
	        $chr = chr($padding);
	        return $data . str_repeat($chr, $padding);
	    }

	    private function unpadding($ciphertext)
	    {
	        $chr = substr($ciphertext, -1);
	        $padding = ord($chr);

	        if($padding > strlen($ciphertext))
	        {
	            return false;
	        }

	        if(strspn($ciphertext, $chr, -1 * $padding, $padding) !== $padding)
	        {
	            return false;
	        }

	        return substr($ciphertext, 0, -1 * $padding);
	    }
	}


非对称加密函数
=============

- ``$res = openssl_pkey_new([array $config])`` : 生成一个新的私钥和公钥对。通过配置数组， 可以微调密钥的生成。

  + ``digest_alg`` : 摘要或签名哈希算法；
  + ``private_key_bits`` : 指定生成的私钥的长度；
  + ``private_key_type`` : 指定生成私钥的算法。默认是 ``OPENSSL_KEYTYPE_RSA`` 可指定 ``OPENSSL_KEYTYPE_DSA`` ， ``OPENSSL_KEYTYPE_DH`` ， ``OPENSSL_KEYTYPE_RSA`` ， ``OPENSSL_KEYTYPE_EC`` ；
  + ``config`` : 自定义 ``openssl.conf`` 文件的路径；

- ``openssl_pkey_free($res)`` : 释放由 ``openssl_pkey_new()`` 创建的私钥；
- ``openssl_get_md_methods()`` : 获取可用的摘要算法；
- ``openssl_pkey_export_to_file($res, $outfilename)`` : 将 ``ASCII`` 格式 ( ``PEM`` 编码 ) 的密钥导出到文件中。使用相对路径时，是相对服务器目录，而非当前所在目录；
- ``openssl_pkey_export($res, &$out)`` : 提取 ``PEM`` 格式私钥字符串；
- ``openssl_pkey_get_details($res)`` : 返回包含密钥详情的数组；
- ``openssl_get_privatekey($key)`` : 获取私钥。 ``key`` 是一个 ``PEM`` 格式的文件或一个 ``PEM`` 格式的私钥；
- ``openssl_get_publickey($certificate)`` : 获取公钥。 ``certificate`` 是一个 ``X.509`` 证书资源或一个 ``PEM`` 格式的文件或一个 ``PEM`` 格式的公钥；
- ``openssl_private_encrypt($data, &$crypted, $privKey [, $padding = OPENSSL_PKCS1_PADDING])`` : 使用私钥加密数据, 并保存到 ``crypted`` 。其中填充模式为 ``OPENSSL_PKCS1_PADDING`` 时，如果明文长度不够，加密时会在明文中随机填充数据。为 ``OPENSSL_NO_PADDING`` 时，如果明文长度不够，会在明文的头部填充 ``0`` ；
- ``openssl_private_decrypt($crypted, &$decrypted, $privKey [, $padding])`` : 使用私钥解密数据, 并保存到 ``decrypted`` ；
- ``openssl_public_decrypt($crypted, &$decrypted, $pubKey [, $padding])`` : 使用公钥解密数据, 并保存到 ``decrypted`` ；

- ``openssl_public_encrypt($data, &$crypted, $pubKey [, $padding])`` : 使用公钥加密数据, 并保存到 ``crypted`` ；

  + ``$data`` 为需要加密的数据；
  + ``$crypted`` 为加密后的数据；
  + ``$key`` 为公钥；
  + ``$padding`` 为填充方式，默认为 ``OPENSSL_PKCS1_PADDING`` ，还可以是如下几个值： ``OPENSSL_SSLV23_PADDING`` ， ``OPENSSL_PKCS1_OAEP_PADDING`` ， ``OPENSSL_NO_PADDING。``




使用php函数生成密钥对
--------------------
``openssl`` 模块提供了很多 ``openssl`` 相关的函数，参考手册生成密钥对的方法如下：

.. code-block:: php

	$privateKey = openssl_pkey_new([
	  'private_key_bits' => 2048,  // private key的大小
	  'private_key_type' => OPENSSL_KEYTYPE_RSA,
	]);

	openssl_pkey_export_to_file($privateKey, 'php-private.key');
	$key = openssl_pkey_get_details($privateKey);
	file_put_contents('php-public.key', $key['key']);

	openssl_free_key($privateKey); // 释放资源

要注意， ``openssl key`` 是一种资源类型，在使用完后记得释放资源。

使用 ``openSSL`` 命令生成密钥对：

.. code-block:: shell

	openssl genrsa -out private.pem 1024
	openssl rsa -in private.pem -pubout -out public.pem


非对称加密演示
-------------
``RSA`` 也是一种分组加密方式，但明文的分组长度根据选择的填充方式的不同而不同。 ``rsa`` 加密的明文最大长度 ``117`` 字节，解密要求密文最大长度为 ``128`` 字节。待加密的字节数不能超过密钥的长度值除以 ``8`` 再减去 ``11`` ，而加密后得到密文的字节数，正好是密钥的长度值除以 ``8`` 。

.. code-block:: php

	class RSA
	{
	    private $private_key; // 私钥
	    private $public_key; // 公钥
	    private $private_res; // 私钥资源
	    private $public_res; // 公钥资源

	    public function __construct()
	    {
	        extension_loaded('openssl') or die('未加载 openssl');
	        // 生成新的公钥和私钥对资源
	        $config = [
	            'digest_alg' => 'sha256',
	            'private_key_bits' => 1204,
	            'private_key_type' => OPENSSL_KEYTYPE_RSA
	        ];
	        $res = openssl_pkey_new($config);
	        if(!$res)
	        {
	            die('生成密钥对失败');
	        }

	        // 获取公钥, 生成公钥资源
	        $this->public_key = openssl_pkey_get_details($res)['key'];
	        $this->public_res = openssl_pkey_get_public($this->public_key);

	        // 获取私钥, 生成私钥资源
	        openssl_pkey_export($res, $this->private_key);
	        $this->private_res = openssl_pkey_get_private($this->private_key);

	        openssl_free_key($res);
	    }

	    // 加密
	    public function encrypt($plaintext)
	    {
	        $ciphertext = null;
	        openssl_public_encrypt($plaintext, $ciphertext, $this->public_res);
	        return $ciphertext;
	    }

	    // 解密
	    public function decrypt($ciphertext)
	    {
	        $plaintext = null;
	        openssl_private_decrypt($ciphertext, $plaintext, $this->private_res);
	        return $plaintext;
	    }
	}

在传输重要信息时, 一般会采用对称加密和非对称加密相结合的方式, 而非使用单一加密方式。一般先通过 ``AES`` 加密数据，然后通过 ``RSA`` 加密 ``AES`` 密钥，然后将加密后的密钥和数据一起发送。接收方接收到数据后，先解密 ``AES`` 密钥, 然后使用解密后的密钥解密数据。

流式加密和解密
-------------

使用第一步的 ``php`` 函数生成的公钥对一段明文进行分段(chunk)再分段加密，（实际使用中也可以直接全部文本加密）：

数据加密：

.. code-block:: php

	$plain = 'this data will be encrypted for transform dolot virendrachadr dark';
	echo 'plian text: ' . $plain;
	$plain = gzcompress($plain); // 压缩数据
	$pubkeyStr = file_get_contents('./php-public.key');
	$publicKey = openssl_pkey_get_public($pubkeyStr);

	$p_key = openssl_pkey_get_details($publicKey);
	$chunkSize = ceil($p_key['bits'] / 8) -11; // 这里不知道为什么要-11，后面追加解释

	$output = '';

	while ($plain) {
	  $chunk = substr($plain, 0, $chunkSize);
	  $plain = substr($plain, $chunkSize);

	  $encrypted = '';
	  if ( !openssl_public_encrypt($chunk, $encrypted, $publicKey)) {
	    die("failed to encrypt data");
	  }
	  $output .= $encrypted;
	}
	openssl_free_key($publicKey);
	$output = base64_encode($output);
	echo 'encrypted: ' . ($output);
	file_put_contents('./enc.data', $output);

.. note::  关于什么加密时分片要减去11：2048位密钥加密的数据输出应该是2048bit，也就是256byte。在函数的官方文档中第一个User Notes里提到了：能加密字符串长度的限制，2048位密钥加密的data长度限制为： ``2048/8 - 11`` 了。使用其他大小的密钥时也减去11就可以了。

数据解密：

.. code-block:: php

	$keyStr = file_get_contents('./php-private.key');
	if (!$privateKey = openssl_pkey_get_private($keyStr)) {
	  die('get private key failed');
	}

	$encrypted = file_get_contents('./enc.data');
	echo 'encrypted data: ' . $encrypted;

	$encrypted = base64_decode($encrypted);

	$p_key = openssl_pkey_get_details($privateKey);
	$chunkSize = ceil($p_key['bits'] / 8);
	$output = '';

	while ($encrypted) {
	  $chunk = substr($encrypted, 0, $chunkSize);
	  $encrypted = substr($encrypted, $chunkSize);
	  $decryptd = '';
	  if (!openssl_private_decrypt($chunk, $decryptd, $privateKey)) {
	    die('failed to decrypt data');
	  }
	  $output .= $decryptd;
	}
	openssl_free_key($privateKey);
	$output = gzuncompress($output);
	echo "\ndecrypted data: " . $output;

生成摘要
========

``openssl_digest ( string $data , string $method [, bool $raw_output = false ] )`` 此方法用于对数据进行摘要计算。

- ``$data`` 是需要生成摘要的数据；
- ``$method`` 是生成摘要的算法。摘要支持哪些算法，请参照附录1。
- ``$raw_output`` 表示是否返回原始数据，如果为 ``false`` （默认）的话则返回 ``bin2hex`` 编码后的数据。

事实上摘要的生成可以不使用 ``openssl`` 的这个方法，因为其实生成摘要就是对数据进行 ``hash`` ，我们可以用以下代码取代摘要生成部分：

.. code-block:: php

	if (function_exists(‘hash’)) {
	    $digest = hash($digestAlgo, $data, TRUE);
	} elseif (function_exists(‘mhash’)) {
	    $digest = mhash(constant("MHASH_" . strtoupper($digestAlgo)), $data);
	}
	$digest = bin2hex($digest);

使用 ``hash`` 方法有个好处就是支持的算法要比 ``openssl_digest`` 多很多。具体支持的算法可以调用 ``hash_algos()`` 方法查看。

数字签名
========
使用完全加密的数据进行传输的好处是更加安全，但是计算更加复杂，需要传输的数据也更多，更常用的方式只是对要传输的数据做一个数字签名，在接收端对接收到的数据进行一个签名运算，只要客户端计算的签名和接受的的签名一样就可以认为收到的数据没有被篡改过。即符合数据完整性。

计算签名使用 ``openssl`` 提供的 ``openssl_sign()`` 来生成 **加密数字签名** 注意，数据本身不会被加密，签名验证使用 ``openssl_verify()``

这两个函数的函数签名为：

- bool openssl_sign ( string $data , string &$signature , mixed $priv_key_id [, mixed $signature_alg = OPENSSL_ALGO_SHA1 ] )
- int openssl_verify ( string $data , string $signature , mixed $pub_key_id [, mixed $signature_alg = OPENSSL_ALGO_SHA1 ] )

通过参数比较容易理解函数的使用， ``sign`` 函数：

- 第一个函数是一个字符串，所以对数组，对象等签名需要使用 ``json_encode`` 或者 ``base64_encode`` 等函数编码一下；
- 第二个参数是 ``&$signature`` 就是函数会把对数据 ``$data`` 的签名保存在 ``$signature`` 变量；
- 第三个参数可以是由 ``openssl_get_privatekey()`` 返回的值或者是 ``PEM`` 格式化密钥字符串；
- 第四个参数 ``signature_alg`` 选择签名算法可以是以下之一：

  + OPENSSL_ALGO_SHA1
  + OPENSSL_ALGO_MD5
  + OPENSSL_ALGO_MD4
  + OPENSSL_ALGO_MD2

注意返回值，第一个函数是 ``bool`` 值，第二个是 ``int`` ， ``1`` 表示签名验证通过， ``0`` 表示签名不正确， ``-1`` 表示发生错误。

.. code-block:: php

	$publicKey = file_get_contents('./php-public.key');
	$privateKey = file_get_contents('./php-private.key');

	$data = [
	  'orderId' => 100002,
	  'pay_time' => '2015-09-02 10:10:10'
	];
	$signature = '';
	openssl_sign(json_encode($data), $signature, $privateKey);
	echo 'sign is: ' . base64_encode($signature);

	$verify = openssl_verify(json_encode($data), $signature, $publicKey);

	echo "\nverify result: $verify";

.. note:: 对数据进行签名时要注意一点，一般发送方通过 ``post`` 把数据发送给接收方，在接收方收到的 ``post`` 数据的顺序并不能保证和发送发签名时一样，所以要约定到 ``post`` 数组的键的顺序，一般在签名前进行 ``ksort($data)`` 。当然如果使用 ``raw_post`` 数据( ``php://input`` )那就没关系了。


openssl_cipher_iv_length ( string $method ) : int
--------------------------------------------------
获取密码初始化向量( ``iv`` )长度。成功，返回密码长度，失败返回 ``FALSE`` 。

- ``$method`` ：密码的方法，更多值查看 ``openssl_get_cipher_methods()`` 函数。

.. code-block:: php

	$method = 'AES-128-CBC';
	$ivlen = openssl_cipher_iv_length($method);

	echo $ivlen;



随机数
======
随机数是一种无规律的数，但是真正做到完全无规律也比较困难，所有一般将它称为伪随机数。

openssl_random_pseudo_bytes ( int $length [, bool &$crypto_strong ] ) : string

生成一个伪随机字节串 ``string`` ，字节数由 ``length`` 参数指定。

.. code-block:: php

	for ($i = -1; $i <= 4; $i++) {
	    $bytes = openssl_random_pseudo_bytes($i, $cstrong);
	    $hex   = bin2hex($bytes);

	    echo "Lengths: Bytes: $i and Hex: " . strlen($hex) . PHP_EOL;
	    var_dump($hex);
	    var_dump($cstrong);
	    echo PHP_EOL;
	}















- https://my.oschina.net/sallency/blog/3017339