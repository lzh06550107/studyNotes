*****
验证和消毒
*****

什么是 PHP 过滤器
=================
PHP 过滤器用于验证和过滤来自非安全来源的数据。


为什么使用过滤器
===============
几乎所有 ``web`` 应用程序都依赖外部的输入。这些数据通常来自用户或其他应用程序（比如 ``web`` 服务）。通过使用过滤器，您能够确保应有程序获得正确的输入类型。

您应该始终对外部数据进行过滤！

什么是外部数据？
---------------

- 来自表单的输入数据
- Cookies
- 服务器变量
- 数据库查询结果

过滤器种类
==========
有两种过滤器：


``validating`` 过滤器：

1. 用于验证用户输入。
2. 严格的格式规则。（比如 ``URL`` 或 ``E-Mail`` 验证）
3. 如果成功则返回预期的类型，失败则返回 ``false`` 。

``sanitizing`` 过滤器：

1. 用于允许或禁止字符串中指定的字符。
2. 无数据格式规则。
3. 始终返回修正后的字符串。


过滤器标志
==========

验证过滤器
----------

FILTER_VALIDATE_BOOLEAN
^^^^^^^^^^^^^^^^^^^^^^^^

定义和用法
"""""""""
``FILTER_VALIDATE_BOOLEAN`` 过滤器把值作为布尔选项来验证。

- Name: "boolean"
- ID-number: 258

可能的返回值
""""""""""""

如果是 "1", "true", "on" 以及 "yes"，则返回 true。
如果是 "0", "false", "off", "no" 以及 ""，则返回 false。
否则返回 NULL。


消毒过滤器
----------



其它过滤器
----------





``Validation`` 用于校验数据。例如，在 ``filter_var($variable)`` 中传入 ``FILTER_VALIDATE_EMAIL`` ，会校验 ``$variable`` 是否为合法的 ``email`` 地址。

``Sanitization`` 用于修正数据，不校验数据。例如，在 ``filter_var($variable)`` 中传入 ``FILTER_SANITIZE_EMAIL`` ， ``$variable`` 变量中不符合 ``email`` 地址规则的字符将会被修剪。

.. code-block:: php

	var_dump(filter_var('bob……@example.com', FILTER_VALIDATE_EMAIL)); //false

	var_dump(filter_var('bob……@example.com', FILTER_SANITIZE_EMAIL)); //'bob@example.com'

	var_dump(filter_var('bob@@example.com', FILTER_VALIDATE_EMAIL));  //false

	var_dump(filter_var('bob@@example.com', FILTER_SANITIZE_EMAIL));  //'bob@example.com'


参考文档：

- http://www.w3school.com.cn/php/php_ref_filter.asp