mbstring 扩展
=============
我们所说的多字节字符指的是不在传统的 128 个 ``ASCII`` 字符集中的字符，比如中文字符。而 PHP 中处理字符串的函数默认假设所有字符都是 8 位字符，占用一个字节，如果使用这些 PHP 原生的字符串处理函数处理包含多字节字符的 ``Unicode`` 字符串，会得到意料之外的错误结果。

为了避免处理多字节字符出错，可以安装 ``mbstring`` 扩展。这个扩展提供了处理多字节字符串的函数，能替代大多数 PHP 原生的处理字符串的函数，例如，可以使用 ``mb_strlen()`` 替代原生的 ``strlen()`` 函数来获取字符串长度。

.. epigraph::

   注：更多多字节字符串处理函数请参考官方文档 http://php.net/manual/zh/ref.mbstring.php

Laravel 底层的字符串处理函数很多也用到 ``mbstring`` 提供的函数对字符串进行处理。详见 ``Illuminate\Support\Str`` 类，这个比较简单，这里就不做赘述了。

字符编码
========
字符编码是打包 ``Unicode`` 数据的方式，以便把数据存储在内存中，或者在服务器和浏览器之间传输。 ``UTF-8`` 是多种可用字符编码中的一种，但是是最流行的一种，所有现代 Web 浏览器都支持。

处理多字节字符串的时候，记住以下几个建议：

- 一定要知道数据的字符编码
- 使用 UTF-8 字符编码存储数据
- 使用 UTF-8 字符编码输出数据

``mbstring`` 扩展不仅能处理 ``Unicode`` 字符串，还能在不同的字符编码之间转换多字节字符串。我们可以使用 ``mb_detect_encoding()`` 方法检测字符编码，然后使用 ``mb_convert_encoding()`` 方法转换字符编码。

输出 UTF-8 数据
===============
处理多字节字符串的时候，需要告知 PHP 使用 ``UTF-8`` 字符编码，我们可以在 ``php.ini`` 中进行全局设置：

.. code-block:: ini

    default_charset = "UTF-8";

很多 PHP 函数都会使用这个默认的字符编码，如 ``htmlentities()`` 、 ``html_entity_decode()`` 和 ``htmlspecialchars()`` ，以及 ``mbstring`` 扩展提供的函数。

在浏览器输出时，我们可以通过 ``header()`` 函数指定输出数据字符编码：

.. code-block:: php

    <?php
    header("Content-Type: application/json;charset=utf-8");

我们还建议在 HTML 文档的头部加上这个 ``meta`` 标签：

.. code-block:: html

    <meta charset="UTF-8"/>
