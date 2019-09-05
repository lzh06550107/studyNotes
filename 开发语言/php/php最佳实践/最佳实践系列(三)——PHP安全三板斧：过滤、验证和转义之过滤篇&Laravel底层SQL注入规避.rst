我们在开发应用时，一般有个约定：不要信任任何来自不受自己控制的数据源中的数据。例如以下这些外部源：

- $_GET
- $_POST
- $_REQUEST
- $_COOKIE
- $argv
- php://stdin
- php://input
- file_get_contents()
- 远程数据库
- 远程API
- 来自客户端的数据

所有这些外部源都可能是攻击媒介，可能会（有意或无意）把恶意数据注入 PHP 脚本。编写接收用户输入然后渲染输出的 PHP 脚本很容易，可是要安全实现的话，需要下一番功夫。我这里以陈咬金的三板斧为引子，给大家介绍三招：过滤输入、验证数据，以及转义输出。

过滤输入
========
过滤输入是指转义或删除不安全的字符。在数据到达应用的存储层之前，一定要过滤输入数据，这是第一道防线。假如网站的评论框接受 HTML ，用户可以随意在评论中加入恶意的 ``<script>`` 标签，如下所示：

.. code-block:: html

    <p>
    这篇文章很有用！
    </p>
    <script>windows.location.href="http://laravelacademy.org";</script>

如果不过滤这个评论，恶意代码会存入数据库，然后在网页中渲染，当用户访问这个页面时，会重定向到可能不安全的钓鱼网站（这种攻击有一个更专业的称呼： ``XSS`` 攻击）。这个简单示例很好的说明了为什么我们要过滤不受自己控制的输入数据。通常我们要过滤的输入数据包括 HTML 、 SQL 查询以及用户资料等。

HTML
----
我们可以使用 PHP 提供的 ``htmlentities`` 函数过滤 HTML ，该函数会将所有 HTML 标签字符（&、<、>等）转化为对应的 HTML 实体，以便在应用存储层取出后安全渲染。但是有时候我们是允许用户输入某些 HTML 元素的，尤其是输入富文本的时候，比如图片、链接这些，但是 ``htmlentities`` 不能验证 HTML ，检测不出输入字符串的字符集，故而无法实现这样的功能。

.. code-block:: php

    <?php
    $input = "<p><script>alert('Laravel学院');</script></p>"；
    echo htmlentities($input, ENT_QUOTES, 'UTF-8');

``htmlentities`` 的第一个参数表示要处理的 HTML 字符串，第二个参数表示要转义单引号，第三个参数表示输入字符串的字符集编码。

与 ``htmlentities`` 相对的是 ``html_entity_decode`` 方法，该方法会将所有 HTML 实体转化为对应的 HTML 标签。

此外， PHP 还提供了一个类似的内置函数 ``htmlspecialchars`` ，该函数也是用于将 HTML 标签字符转化为 HTML 实体，只是能够转化的字符有限（参考官方文档： http://php.net/manual/zh/function.htmlspecialchars.php ），如果要转化所有字符还是使用 ``htmlentities`` 方法，值得一提的是和 ``htmlentities`` 一样， ``htmlspecialchars`` 也有一个与之相对的方法 ``htmlspecialchars_decode`` 。

如果想要直接将输入字符串中的所有 ``HTML`` 标签去掉，可以使用 ``strip_tags`` 方法。

如果需要更加强大的过滤 HTML 功能，可以使用 ``HTML Purifier`` 库，这是一个很强健且安全的 PHP 库，专门用于使用指定规则过滤 HTML 输入。在 Laravel 中我们可以使用相应的扩展包来实现过滤功能： http://laravelacademy.org/post/3914.html

SQL查询
-------
有时候应用必须根据输入数据构建 ``SQL`` 查询，这些数据可能来自 ``HTTP`` 请求的查询字符串，也可能来自 ``HTTP`` 请求的 ``URI`` 片段，一不小心，就有可能被不怀好意的人利用进行 ``SQL`` 注入攻击（拼接 ``SQL`` 语句对数据库进行破坏或者获取敏感信息）。很多初级的程序员可能会这么写代码：

.. code-block:: php

    $sql = sprintf(
        'UPDATE users SET password = "%s" WHERE id = %s',
        $_POST['password'],
        $_GET['id']
    );

这么做风险很大，比如某个人通过如下方式对 ``HTTP`` 发送请求：

.. code-block:: text

    POST /user?id=1 HTTP/1.1
    Content-Length: 17
    Content-Type: application/x-www-form-urlencoded

    password=abc”;--

这个 ``HTTP`` 请求会把每个用户的密码都设置为 ``abc`` ，因为很多 ``SQL`` 数据库把 ``--`` 视作注释的开头，所以会忽略后续文本。

在 ``SQL`` 查询中一定不能使用未过滤的输入数据，如果要在 ``SQL`` 查询中使用输入数据，一定要使用 ``PDO`` 预处理语句（ ``PDO`` 是 PHP 内置的数据库抽象层，为不同的数据库驱动提供统一接口）， ``PDO`` 预处理语句是 ``PDO`` 提供的一个功能，可以用于过滤外部数据，然后把过滤后的数据嵌入 ``SQL`` 语句，避免出现上述 ``SQL`` 注入问题，此外预处理语句一次编译多次运行，可以有效减少对系统资源的占用，获取更高的执行效率。关于 ``PDO`` 后我们后续还会在数据库部分重点讨论。

值得注意的是，很多现代 PHP 框架都使用了 MVC 架构模式，将数据库的操作封装到了 ``Model`` 层，框架底层已经做好了对 ``SQL`` 注入的规避，只要我们使用模型类提供的方法执行对数据库的操作，基本上可以避免 ``SQL`` 注入风险。

我们以 Laravel 为例看看底层是如何规避 ``SQL`` 注入的，改写上面的 ``update`` 语句，代码会是这样：

.. code-block:: php

    $id = $_GET['id'];
    $password = $_POST['password'];
    User::find($id)->update(['password'=>bcrypt($password)]);

由于模型类底层调用的是是查询构建器的方法，所以最终会调用 ``Builder`` （ ``Illuminate\Database\Query\Builder`` ）的 ``update`` 方法：

.. code-block:: php

    public function update(array $values)
    {
        $bindings = array_values(array_merge($values, $this->getBindings()));

        $sql = $this->grammar->compileUpdate($this, $values);

        return $this->connection->update($sql, $this->cleanBindings($bindings));
    }

这段代码传入参数是要更新的值，然后通过 ``$bindings`` 获得绑定关系，这里我们我们获取到的应该是包含 ``password`` 和 ``updated_at`` （默认更新时间戳）的数组，然后再通过 ``Grammar`` （ ``Illuminate\Database\Query\Grammars\Grammar`` ）类的 ``compileUpdate`` 方法生成预处理 SQL 语句，这里对应的 sql 语句是：

.. code-block:: sql

    update `users` set `password` = ?, `updated_at` = ? where `id` = ?

然后最终将预处理 ``sql`` 语句和对应绑定关系传递给数据库去执行。关于 ``SQL`` 注入我们还会在后续数据库部分继续讨论。

用户资料信息
-------------
如果应用中有用户账户，可能就要处理电子邮件地址、电话号码、邮政编码等资料信息。 PHP 预料到会出现这种情况，因此提供了 ``filter_var`` 和 ``filter_input`` （通过名称获取特定的外部变量，并且可以通过过滤器处理它）函数。这两个函数的参数能使用不同的标志，过滤不同类型的输入： **电子邮件地址、URL编码字符串、整数、浮点数、HTML字符、URL和特定范围的 ASCII 字符** 。

以下示例展示了如何过滤电子邮件地址，删除除字母、数字和 ``!#$%&'*+-/=?^_{|}~@.[]```之外的所有其他字符：

.. code-block:: php

    <?php
    $email = 'yaojinbu@163.com';
    $emailSafe = filter_var($email, FILTER_SANITIZE_EMAIL);

更多 ``filter_var`` 的使用请参考 PHP 官方文档： http://php.net/manual/zh/function.filter-var.php ，相应的内置过滤器请参考： http://php.net/manual/zh/filter.filters.sanitize.php 。

当然， ``filter_var`` 函数还可以用于其它表单提交数据的过滤。






