**************
PHP获取POST的几种方法
**************

最常见的方法是：$_POST['fieldname'];
===================================

- 说明：只能接收 ``Content-Type: application/x-www-form-urlencoded`` 提交的数据
- 解释：也就是表单 ``POST`` 过来的数据

file_get_contents("php://input");
=================================

- 说明：允许读取 ``POST`` 的原始数据。和 ``$HTTP_RAW_POST_DATA`` 比起来，它给内存带来的压力较小，并且不需要任何特殊的 ``php.ini`` 设置。 ``php://input`` 不能用于 ``enctype="multipart/form-data"`` 。
- 解释：对于未指定 ``Content-Type`` 的 ``POST`` 数据，则可以使用 ``file_get_contents("php://input");`` 来获取原始数据。事实上，用 PHP 接收 ``POST`` 的任何数据都可以使用本方法。而不用考虑 ``Content-Type`` ,包括二进制文件流也可以。所以用方法二是最保险的方法

$GLOBALS['HTTP_RAW_POST_DATA'];
===============================

- 说明：总是产生 ``$HTTP_RAW_POST_DATA``  变量包含有原始的 ``POST`` 数据。此变量仅在碰到未识别 ``MIME`` 类型的数据时产生。 ``$HTTP_RAW_POST_DATA``  对于 ``enctype="multipart/form-data"`` 表单数据不可用。如果 ``post`` 过来的数据不是 PHP 能够识别的，可以用 ``$GLOBALS['HTTP_RAW_POST_DATA']`` 来接收，比如 ``text/xml`` 或者 ``soap`` 等等
- 解释： ``$GLOBALS['HTTP_RAW_POST_DATA']`` 存放的是 ``POST`` 过来的原始数据。 ``$_POST`` 或 $_REQUEST 存放的是 PHP 以 key=>value 的形式格式化以后的数据。 但 $GLOBALS['HTTP_RAW_POST_DATA'] 中是否保存 ``POST`` 过来的数据取决于 ``centent-Type`` 的设置，即 ``POST`` 数据时必须显式示指明 ``Content-Type:application/x-www-form-urlencoded`` ， ``POST`` 的数据才会存放到 ``$GLOBALS['HTTP_RAW_POST_DATA']`` 中

摘自：https://my.oschina.net/u/205403/blog/325595

已发送XML为例：

方法1：（file_get_contents("php://input")）

.. code-block:: php

	$input = file_get_contents("php://input"); //接收POST数据
	$xml = simplexml_load_string($input); //提取POST数据为simplexml对象
	var_dump($xml);

方法2：（$GLOBALS['HTTP_RAW_POST_DATA']）

.. code-block:: php

	$input = $GLOBALS['HTTP_RAW_POST_DATA'];
	libxml_disable_entity_loader(true);
	$xml = simplexml_load_string($input, 'SimpleXMLElement', LIBXML_NOCDATA);
	var_dump($xml);