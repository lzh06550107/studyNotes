在现代 PHP 特性中，流或许是最出色但使用率最低的。虽然 PHP 4.3 就引入了流，但是很多开发者并不知道流的存在，因为人们很少提及流，而且流的文档也很匮乏。PHP 官方文档对流的解释如下：

.. epigraph::

   流的作用是提供统一的公共函数来处理文件、网络和数据压缩等操作。简单而言，流是具有流式行为的资源对象，也就是说，流可以线性读写，并且可以通过 ``fseek()`` 之类的函数定位到流中的任何位置。

可能看完这段解释后还是云里雾里，我们简化一下，流的作用是在出发地和目的地之间传输数据。出发地和目的地可以是文件、命令行进程、网络连接、ZIP 或 TAR 压缩文件、临时内存、标准输入或输出，或者是通过 PHP 流封装协议实现的任何其他资源。

如果你读写过文件，就用过流；如果你从 ``php://stdin`` 读取过数据，或者把输入写入 ``php://stdout`` ，也用过流。流为 ``PHP`` 的很多 ``IO`` 函数提供了底层实现，如  ``file_get_contents`` 、 ``fopen`` 、 ``fread`` 和 ``fwrite`` 等。PHP 的流函数提供了不同资源的统一接口。

我们可以把流比作管道，把水（资源数据）从一个地方引到另一个地方。在水从出发地到目的地的过程中，我们可以过滤水，可以改变水质，可以添加水，也可以排出水。

流封装协议
==========
流式数据的种类各异，每种类型需要独特的协议，以便读写数据，我们称这些协议为 `流封装协议 <http://php.net/manual/zh/wrappers.php>`_ 。例如，我们可以读写文件系统，可以通过 ``HTTP`` 、 ``HTTPS`` 或 ``SSH`` 与远程 Web 服务器通信，还可以打开并读写 ZIP、RAR 或 PHAR 压缩文件。这些通信方式都包含下述相同的过程：

- 开始通信
- 读取数据
- 写入数据
- 结束通信

虽然过程是一样的，但是读写文件系统中文件的方式与收发 HTTP 消息的方式有所不同， **流封装协议的作用是使用通用的接口封装这种差异。**

**每个流都有一个协议和一个目标。指定协议和目标的方法是使用流标识符： <scheme>://<target> ，其中 <scheme> 是流的封装协议， <target>  是流的数据源。**

http://流封装协议
-----------------
下面使用 ``HTTP`` 流封装协议创建了一个与 Flicker API 通信的 ``PHP`` 流：

.. code-block:: php

    <?php
    $json = file_get_contents(
        'http://api.flickr.com/services/feeds/photos_public.gne?format=json'
    );

不要以为这是普通的网页 ``URL`` ， ``file_get_contents()`` 函数的字符串参数其实是一个流标识符。 ``http`` 协议会让 ``PHP`` 使用 ``HTTP`` 流封装协议，在这个参数中， ``http`` 之后是流的目标。

.. epigraph::

   注：很多 PHP 开发者可能并不知道普通的 ``URL`` 其实是 PHP 流封装协议标识符的伪装。

file://流封装协议
-----------------
我们通常使用 ``file_get_contents()`` 、 ``fopen()`` 、 ``fwrite()`` 和 ``fclose()`` 等函数读写文件系统，因为 PHP 默认使用的流封装协议是 ``file://``，所以我们很少认为这些函数使用的是 PHP 流。下面的示例演示了使用 ``file://`` 流封装协议创建一个读写 ``/etc/hosts`` 文件的流：

.. code-block:: php

    <?php
    $handle = fopen('file:///etc/hosts', 'rb');
    while (feof($handle) !== TRUE) {
        echo fgets($handle);
    }
    fclose($handle);

我们通常会省略掉 ``file://`` 协议，因为这是 PHP 使用的默认值。

php://流封装协议
-----------------
编写命令行脚本的 PHP 开发者会感激 ``php://`` 流封装协议，这个流封装协议的作用是与 PHP 脚本的标准输入、标准输出和标准错误文件描述符通信。我们可以使用 PHP 提供的文件系统函数打开、读取或写入下面四个流：

- ``php://stdin`` ：这是个只读 PHP 流，其中的数据来自标准输入。PHP 脚本可以使用这个流接收命令行传入脚本的信息；
- ``php://stdout`` ：把数据写入当前的输出缓冲区，这个流只能写，无法读或寻址；
- ``php://memory`` ：从系统内存中读取数据，或者把数据写入系统内存。缺点是系统内存有限，所有使用 php://temp 更安全；
- ``php://temp`` ：和 ``php://memory`` 类似，不过，没有可用内存时，PHP 会把数据写入这个临时文件。

其他流封装协议
--------------
PHP 和 PHP 扩展还提供了很多其他流封装协议，例如，与 ZIP 和 TAR 压缩文件、FTP 服务器、数据压缩库、Amazon API、Dropbox API 等通信的流封装协议。需要注意的是，PHP 中的 ``fopen()`` 、 ``fgets()`` 、 ``fputs()`` 、 ``feof()`` 以及 ``fclose()`` 等函数不仅可以用来处理文件系统中的文件，还可以在所有支持这些函数的流封装协议中使用。

.. epigraph::

   注：更多流封装协议，请参考官方网站： http://php.net/manual/zh/wrappers.php

自定义流封装协议
----------------
我们还可以自己编写 PHP 流封装协议。PHP 提供了一个示例 ``StreamWrapper`` 类，演示如何编写自定义的流封装协议，支持部分或全部 PHP 文件系统函数。关于如何编写，具体请参考以下文档：

- http://php.net/manual/zh/class.streamwrapper.php
- http://php.net/manual/zh/stream.streamwrapper.example-1.php

流上下文
========
有些 PHP 流能够接受一系列可选的参数，这些参数叫流上下文，用于定制流的行为。不同的流封装协议使用的流上下文有所不同，流上下文使用 ``stream_context_create()`` 函数创建，这个函数返回的上下文对象可以传入大多数文件系统函数。

例如，你知道可以使用 ``file_get_contents()`` 发送 ``HTTP POST`` 请求吗？使用一个流上下文对象即可实现：

.. code-block:: php

    <?php
    $requestBody = '{"username":"nonfu"}';
    $context = stream_context_create([
        'http' => [
            'method' => 'POST',
            'header' => "Content-Type: application/json;charset=utf-8;\r\nContent-Length: " . mb_strlen($requestBody),
            'content' => $requestBody
        ]
    ]);
    $response = file_get_contents('https://my-api.com/users', false, $context);

流上下文是个关联数组，最外层键是流封装协议的名称，流上下文数组中的值针对不同的流封装协议有所不同，可用的设置参考各个 PHP 流封装协议的文档。

流过滤器
========
目前为止我们讨论了如何打开流，读取流中的数据，以及把数据写入流。不过，PHP 流真正强大的地方在于过滤、转换、添加或删除流中传输的数据，例如，我们可以打开一个流处理 ``Markdown`` 文件，在把文件内容读入内存的过程中自动将其转化为 ``HTML`` 。

.. epigraph::

   注：PHP 所有可用流过滤器请参考官方文档： http://php.net/manual/zh/filters.php 。

若想把过滤器附加到现有的流上，要使用 ``stream_filter_append()`` 函数，下面我们以 ``string.toupper`` 过滤器演示如何把文件中的内容转换成大写字母：

.. code-block:: php

    <?php
    $handle = fopen('test.txt', 'rb');
    stream_filter_append($handle, 'string.toupper');
    while (feof($handle) !== true) {
        echo fgets($handle);
    }
    fclose($handle);

运行该脚本，输出的都是大写字母。

我们还可以使用 ``php://filter`` 流封装协议把过滤器附加到流上，不过，使用这种方式之前必须先打开 ``PHP`` 流：

.. code-block:: php

    <?php
    $handle = fopen('php://filter/read=string.toupper/resource=test.txt', 'rb');
    while (feof($handle) !== true) {
        echo fgets($handle);
    }
    fclose($handle);

这个方式实现效果和 ``stream_filter_append()`` 函数一样，但是相比之下更为繁琐。不过， PHP 的某些文件系统函数在调用后无法附加过滤器，例如 ``file()`` 和 ``fpassthru()``，使用这些函数时只能使用 ``php://filter`` 流封装协议附加流过滤器。

自定义流过滤器
--------------
我们还可以编写自定义的流过滤器。其实，大多数情况下都要使用自定义的流过滤器，自定义的流过滤器是个 PHP 类，继承内置的 ``php_user_filter`` 类（ http://php.net/manual/zh/class.php-user-filter.php ），且必须实现 ``filter()`` 、 ``onCreate()`` 和 ``onClose()`` 方法，最后，必须使用 ``stream_filter_register()`` 函数注册自定义的流过滤器。

.. epigraph::

   注：PHP 流会把数据分成按次序排列的桶，一个桶中盛放的流数据是固定的（如 4096 字节），如果还用管道比喻，就是把水放在一个个水桶中，顺着管道从出发地漂流到目的地，在漂流过程中会经过过滤器，过滤器一次可以接收并处理一个或多个桶，一定时间内过滤器接收到的桶叫做桶队列。桶队列中的每个桶对象都有两个公共属性：data 和 datalen，分别表示桶的内容和长度。

下面我们自定义一个流过滤器 ``DirtyWordsFilter`` ，把流数据读入内存时审查其中的脏字：

.. code-block:: php

    <?php
    class DirtyWordsFilter extends php_user_filter
    {
        /**
         * @param resource $in 流入的桶队列
         * @param resource $out 流出的桶队列
         * @param int $consumed 处理的字节数
         * @param bool $closing 是否是流中最后一个桶队列
         * @return int
         * 接收、处理再转运桶中的流数据，在该方法中，我们迭代桶队列对象，把脏字替换成审查后的值
         */
        public function filter($in, $out, &$consumed, $closing)
        {
            $words = ['grime', 'dirt', 'grease'];
            $wordData = [];
            foreach ($words as $word) {
                $replacement = array_fill(0, mb_strlen($word), '*');
                $wordData[$word] = implode('', $replacement);
            }
            $bad = array_keys($wordData);
            $good = array_values($wordData);

            // 迭代桶队列中的每个桶
            while ($bucket = stream_bucket_make_writeable($in)) {
                // 审查桶对象中的脏字
                $bucket->data = str_replace($bad, $good, $bucket->data);
                // 增加已处理的数据量
                $consumed += $bucket->datalen;
                // 把桶放入流向下游的队列中
                stream_bucket_append($out, $bucket);
            }

            return PSFS_PASS_ON;
        }
    }

然后，我们必须使用 ``stream_filter_register()`` 函数注册这个自定义的 ``DirtyWordsFilter`` 流过滤器：

.. code-block:: php

    <?php
    stream_filter_register('dirty_words_filter', 'DirtyWordsFilter');

第一个参数用于标识这个自定义过滤器的过滤器名，第二个参数是这个自定义过滤器的类名。接下来就可以使用这个自定义的流过滤器了：

.. code-block:: php

    <?php
    $handle = fopen('test.txt', 'rb');
    stream_filter_append($handle, 'dirty_words_filter');
    while (feof($handle) !== true) {
        echo fgets($handle);
    }
    fclose($handle);

修改 test.txt 内容如下：

.. code-block:: txt

    abcdeefghijklmn
    Hello LaravelAcademy!
    grime
    I hate dirty things!

运行上面的自定义过滤器脚本，结果如下：

.. code-block:: txt

    abcdeefghijklmn
    Hello LaravelAcademy!
    *****
    I hate ****y things!

