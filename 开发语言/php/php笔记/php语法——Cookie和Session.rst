会话控制
=======

为什么要使用会话控制
------------------
当我们在浏览网页时，访问每一个 web 页面都需要使用 HTTP协议 实现，但是 HTTP 协议是无状态协议，就是说 HTTP 协议没有一个内建的机制来维护两个事物之间的状态。当一个用户请求一个页面以后，在请求同一个网站上的另外一个页面时， HTTP 协议并不能告诉我们两个请求是来自同一个用户，而不能将这两次访问联系到一起，那利用什么解决呢？利用 Cookie 和 Session 可以解决。

会话控制的思想就是允许服务器跟踪同一个客户端做出的连续请求。这样，我们就可以很容易地做到用户登录的支持，而不是在每浏览一个网页都去重复执行登入的动作。当然，除了使用会话控制在同一个网站中跟踪 Web 用户外，对同一个访问者的请求还可以在多个页面之间为其共享数据。

会话跟踪的方式
-------------
HTTP 是无状态的协议，所以不能维护两个事务之间的状态。但一个用户在请求一个页面以后再请求另一个页面时，还要让服务器知道这是同一个用户。 PHP 系统为了防止这种情况的发生，提供了如下所示三种网页之间传递数据的方法：

- 使用超链接或者 header() 函数等重定向的方式。通过在 URL 的 GET 请求中附加参数的形式，将数据从一个页面转向另一个 PHP 脚本中。也可以使用网页中的各种隐藏表单来存储使用者资料，并将这些信息在提交表单时传递给服务器 PHP 脚本；
- 使用 Cookie 将用户的状态信息，存放在客户端的计算机中，让其他程序通过存取客户端计算机的 Cookie ，来存取目前的使用者资料；
- 相对于 Cookie 还可以使用 Session ，将访问者的状态信息存放于服务器之中，让其他程序能透过服务器中的文件或者数据库，来存取使用者资料；

 注：在三种页面传值方式中， URL 的 GET 和 HTTP POST 方式，主要用来处理参数的传递或是多笔资料的输入，适合于两个脚本之间的简单数据传递。而我们进行会话跟踪一般使用 Cookie 和 Session 技术。

Cookie的管理
-----------

Cookie概述
^^^^^^^^
Cookie 是一种由服务器发送给客户端的片段信息，存储在客户端浏览器的内存或者硬盘上，在客户对该服务器的请求中回发它。

向客户端电脑中设置Cookie
^^^^^^^^^^^^^^^^^^^^^^^
Cookie 的建立十分简单，只要用户的浏览器支持 Cookie 功能，就可以使用 PHP 内建的 setCookie() 函数来新建立一个 Cookie 。 Cookie 是 HTTP 标头的一部分， **因此 setCookie() 函数必须在其它信息被输出到浏览器前调用，所以即使是空格或空行都不行，这和调用 header() 函数的限制类似。** setCookie() 函数的语法格式如下所示：

``bool setcookie(string $name [, string $value = "" [, int $expire = 0 [, string $path = "" [, string $domain = "" [, bool $secure = false [, bool $httponly = false ]]]]]])``

setcookie() 函数定义一个和其余的 HTTP 标头一起发送的 Cookie ，它的所有参数是对应 HTTP 标头 Cookie 资料的属性。虽然 setcookie() 函数的导入参数看起来不少，但除了参数 name 之外，其它都是非必需的，而我们经常使用的只有前三个参数。它的每个参数代表意义如表所示：

+-----------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 参数      | 描述                                                                                                                                                                                                                                     | 示例                                                                                                                                                                                                                                                   |
+===========+==========================================================================================================================================================================================================================================+========================================================================================================================================================================================================================================================+
| $name     | Cookie的识别名称                                                                                                                                                                                                                         | 使用 ``$_COOKIE['cookiename']`` 调用名为 ``cookiename的Cookie``                                                                                                                                                                                        |
+-----------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| $value    | cookie的值，可以为数值或字符串形态，存放在客户端，不要存放敏感数据                                                                                                                                                                       | 假定name是'cookiename'，可以通过 ``$_COOKIE['cookiename']`` 取得其值。                                                                                                                                                                                 |
+-----------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| $expire   | Cookie过期的时间。这是个Unix时间戳，即从Unix纪元开始的秒数。换而言之，通常用time()函数再加上秒数来设定cookie的失效期。或者用 ``mktime()`` 来实现。                                                                                       | ``time()+60*60*24*30`` 将设定cookie 30天后失效。如果未设定，cookie 将会在会话结束后（一般是浏览器关闭）失效。                                                                                                                                          |
+-----------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| $path     | Cookie在服务器端的有效路径。                                                                                                                                                                                                             | 如果该参数设为 '/' 的话，cookie就在整个domain内有效，如果设为'/foo/'，cookie就只在domain下的/foo/目录及其子目录内有效，例如/foo/bar/。默认值为设定 cookie 的当前目录。                                                                                 |
+-----------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| $domain   | 该 cookie 有效的域名。预设为建立此Cookie服务器的网址                                                                                                                                                                                     | 要使 cookie 能在如 ``example.com`` 域名下的所有子域都有效的话，该参数应该设为 '.example.com'。虽然 ``.`` 并不必须的，但加上它会兼容更多的浏览器。如果该参数设为 ``www.example.com`` 的话，就只在 www 子域内有效。细节见Cookie 规范中的 tail matching。 |
+-----------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| $secure   | 指明 cookie 是否仅通过安全的 HTTPS 连接传送。                                                                                                                                                                                            | 当设成 ``TRUE`` 时， ``cookie`` 仅在安全的连接中被设置。默认值为 ``FALSE`` 。                                                                                                                                                                          |
+-----------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| $httponly | 设置成 TRUE，Cookie 仅可通过 HTTP 协议访问。 这意思就是 Cookie 无法通过类似 JavaScript 这样的脚本语言访问。 要有效减少 XSS 攻击时的身份窃取行为，可建议用此设置（虽然不是所有浏览器都支持），不过这个说法经常有争议。 PHP 5.2.0 中添加。 | ``TRUE`` 或 ``FALSE``                                                                                                                                                                                                                                  |
+-----------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

如果只有 ``$name`` 这一个参数，则原有此名称的 ``cookie``  选项将会被删除，也可以使用空字符串来省略此参数。参数 ``$expire`` 和 ``$secure`` 是一个整数，可以使用 ``0`` 来省略此参数，而不是使用空字符串。但 参数 ``$expire`` 是一个正规的 UNIX 时间整数，由 ``time()`` 或者 ``mktime()`` 函数传回。参数 ``$secure`` 指出此 ``Cookie`` 将只有在安全的 ``HTTPS`` 连接时传送。在实际建立Cookie时通常仅使用前三项参数：

``setcookie("username","skygao",time()+60*60*24*7) //7天有效期``

如果访问该脚本就会设置Cookie，并把用户名添加到访问者电脑的Cookie中去。上例表示建立一个识别名称为“username”的Cookie，其内容值为字符串“skygao”，而在客户端有效的存储期限则指定一周。如果其他三个参数也需要使用，可以按如下方式指定：

``setcookie("username","skygao",time()+60*60*24*7,"/test",".example.com",1); //使用全部参数设置``

当最后一个参数设成1时，则Cookie仅在安全的连接中才能被设置。如果需要向客户端设置多个Cookie，可以通过调用多次setCookie()函数实现。但如果两次设置相同的Cookie识别名称，则后设置的Cookie会把值赋给与自己同名的cookie变量，如果原来的值不为空则会被覆盖。

在PHP脚本中读取Cookie的资料内容
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
如果 Cookie 设置成功，客户端就拥有了 Cookie 文件，用来保存 Web 服务器为其设置的用户信息。假设我们在客户端使用了 ``Windows`` 系统去浏览服务器中的脚本， ``Cookie`` 文件会被存放在 `` C:\Documents and Settings\用户名\Cookies`` 文件夹下。 Cookie 是一个以普通文本文件形式记录信息的，虽然直接使用文本编辑器就可以打开浏览，但直接去阅读 Cookie 文件中的信息是没有意义的。而是当客户再次访问该网站时，浏览器会自动把与该站点对应的 Cookie 信息全部发送给服务器。从PHP5之后，任何从客户端发送过来的 Cookie信息，都会被自动保存在 ``$_COOKIE`` 全局数组中，所以在每个PHP脚本中都可以从该数组中读取相应的 Cookie 信息。 ``$_COOKIE`` 全局数组存储所有通过 HTTP 传递的 Cookie 资料内容，并以 Cookie 的识别名称为索引值、内容值为元素。

在设置 Cookie 脚本中，第一次读取它的信息并不会生效，必须刷新或到下一个页面才可以看到 Cookie 值，因为 Cookie 要先被设置到客户端，再次访问时才能被发送过来，这是才能被获取。所以要测试一个 Cookie 是否被成功设定，可以再其到期之前通过另外一个页面来访问其的值。可以简单地使用print_r($_COOKIE)指令来调试现有的Cookies。如下所示：

``print_r($_COOKIE); // 输出Cookie中保存的所有用户信息``

如果使用Cookie中的单个信息，可以在 ``$_COOKIE`` 中通过Cookie标识名称进行访问。如果Cookie中的信息需要批量处理，也可以通过数组遍历的方式对其进行处理。

数组形态的Cookie应用
^^^^^^^^^^^^^^^^^^^
Cookie 也可以利用多维数组的形式，将多个内容值存储在相同 Cookie 名称标识符下。但不能直接使用 ``setCookie()`` 函数将数组变量插入到第二个参数作为Cookie的值，因为 ``setCookie()`` 函数的第二个参数必须传一个字符串的值。如果需要将数组变量设置到Cookie中，可以在 ``setCookie()`` 函数的第一个参数中，通过在Cookie标志名称中指定数组下标的形式设置。如下所示：

.. code-block:: php

    <?php
	setcookie("user[username]", "skygao") // 设置为$_COOKIE["user"]["username"]
	setcookie("user[password]", md5("123456")); // 设置为$_COOKIE["user"]["password"]
	setcookie("user[email]", "skygao@lampbrother.net"); // 设置为$_COOKIE["user"]["email"]
    ?>

在上面一段程序中，建立了一个标识名称为“user”的Cookie，但其中包含了三个数据，这样就形成了Cookie的关联数组形态。设置成功之后，如果需要在PHP脚本中获取其值，同样是使用$_COOKIE超级全局数组。但这时的$_COOKIE数组并不是一维的了，而变成了一个二维数组，一维的下标变成“user”。在下面的PHP脚本中，我们使用foreach()函数遍历上面设置的Cookie：

.. code-block:: php

    <?php
	foreach ($_COOKIE['user'] as $key => $value) { // 遍历$_COOKIE["user"]数组
	    echo $key.":".$value."\n"; // 输出Cookie数组中二维的键值对
	}
    ?>

当然我们也可以设置Cookie为索引数组形态。其实是用Cookie的数组形态，和我们直接在PHP脚本中声明的数组非常相似。区别在于，我们把数组保存到了客户端的电脑中，然后再服务端的每个PHP脚本中都可以使用这个数组。

删除Cookie
^^^^^^^^^^
如果需要删除保存在客户端的Cookie，可以使用两种方法。这两种方法和设置Cookie一样，也是调用 ``setcookie()`` 函数实现删除的动作：第一种方式，省略setcookie()函数的所有参数列，仅导入第一个参数Cookie识别名称参数，来删除指定名称的Cookie资料；第二种方式，利用setcookie()函数，把目标Cookie设定为“已过期”状态。如下所示：

.. code-block:: php

    <?php
	//只指定Cookie识别名称一个参数，即删除客户端中这个指定名称的Cookie资料
	setcookie("Account"); // 第一种方法
	//设置Cookie在当前时间之前已经过期，因此系统会自动删除识别名称为isLogin的Cookie
	setcookie("isLogin","",time()-1); // 第二种方法
    ?>

第一种方法将Cookie的生存时间默认设置为空，则生存期限和浏览器一样，浏览器关闭时Cookie就会被删除。而通过第二种删除Cookie的方法，Cookie的有效期限参数的含义指当超过设定时间时，系统会自动删除客户端的Cookie。

基于Cookie的用户登录模块
^^^^^^^^^^^^^^^^^^^^^^
大部分Web系统软件都会有登录和退出模块，这是为了维护系统的安全性，确保只有通过身份验证的用户才能访问该系统。本例将采用Cookie保存用户登录信息，并在每个PHP脚本中，都可以跟踪登录的用户。用户登录文件login.php中的代码如下，该文件包含登录操作、退出操作和登录表单三部分。代码如下所示:

.. code-block:: php

    <!--login.php-->
	<?php
	header("Content-Type:text/html;charset=utf-8");
	/* 声明一个删除Cookie的函数，调用时清除在客户端设置的所有Cookie */
	function clearCookies() {
	    setCookie('username', '', time()-3600); //删除Cookie中的标识符为username的变量
	    setCookie('isLogin', '', time()-3600); //删除Cookie中的标识符为isLogin的变量
	}
	/* 判断用户是否执行的是登录操作 */
	if($_GET["action"]=="login") {
	    /* 调用时清除在客户端先前设置的所有Cookie */
	    clearCookies();
	    /* 检查用户是否为admin，并且密码是否等于123456 */
	    if($_POST["username"]=="admin" && $_POST["password"]=="123456")	{
	        /* 向Cookie中设置标识符为username，值是表单中提交的，期限为一周 */
	        setCookie('username', $_POST["username"], time()+60*60*24*7);
	        /* 向Cookie中设置标识符为isLogin，用来在其他页面检查用户是否登录 */
	        setCookie('isLogin', '1', time()+60*60*24*7);
	        /* 如果Cookie设置成功则转向网站首页 */
	        header("Location:index.php");
	    }else{
	        die("用户名或密码错误！");
	    }
	    /* 判断用户是否执行的是退出操作	*/
	}else if($_GET["action"]=="logout"){
	    /* 退出时清除在客户端设置的所有Cookie */
	    clearCookies();
	}
	?>
	<!DOCTYPE html>
	<html>
	<head>
	    <meta charset="utf-8" />
	    <title>用户登录</title>
	    <style type="text/css">
	        #login {	/*定义一个ID选择器*/
	            width:300px;	/*定义盒子宽度为300px*/
	            height:250px;	/*定义盒子高度为200px*/
	            position:absolute;	/*使用绝对位置进行定位*/
	            left:50%;	/*左部盒子开始位置是页面宽度的50%*/
	            top:50%;	/*顶部盒子开始位置是页面高度的50%*/
	            margin-left:-150px;	/*左部开始位置再退回盒子宽度的一半*/
	            margin-top:-100px;	/*顶部开始位置再退回盒子高度的一半*/
	            background:#bababa;	/*定义盒子的背景颜色为灰色*/
	        }
	        p { padding:5px 10px; line-height:100%; }
	        input { width:220px; height:35px; line-height:35px; border:1px solid#808080; }
	        input[type="text"]:focus { background:#dde3f9; }
	        input[type="password"]:focus { background:#dde3f9; }
	    </style>
	</head>
	<body>
	<div id="login">
	    <h2>用户登录</h2>
	    <form action="login.php?action=login" method="post">
	        <p>用户名 <input type="text" id="text" name="username" /></p>
	        <p style="letter-spacing:5px;">密码 <input type="password" id="password" name="password" /></p>
	        <p style="text-align:right;"><input style="width:60px; letter-spacing:2px;" type="submit" value="登录" /></p>
	    </form>
	</div>
	</body>
	</html>

在上例中，根据action事件参数判断用户执行的是登录还是退出操作。如果参数action的值为login时，首先调用clearCookies()函数将前一个可以登录的用户注销。再判断从登录表单中提交的用户名和密码是否与指定的相同，如果用户的信息保存在数据库中，可以在连接数据库与注册过的用户时进行匹配。匹配成功后则向Cookie中设置username和isLogin两个选项，既登录成功，并将脚本使用header()函数转向index.php脚本。文件index.php是系统的首页，代码如下所示:

.. code-block:: php

    <!--index.php-->
	<?php
	    header("Content-Type:text/html;charset=utf-8");
	    /* 如果用户没有通过身份验证,页面跳转至登录页面 */
	    if(!(isset($_COOKIE['isLogin']) && $_COOKIE['isLogin'] == '1')) {
	        header("Location:login.php");
	        exit;
	    }
	?>
	<!DOCTYPE html>
	<html>
	<head>
	    <meta charset="utf-8" />
	    <title>网站主页面</title></head>
	<style type="text/css">
	    #login {	/*定义一个ID选择器*/
	        width:300px;	/*定义盒子宽度为300px*/
	        height:250px;	/*定义盒子高度为200px*/
	        position:absolute;	/*使用绝对位置进行定位*/
	        left:50%;	/*左部盒子开始位置是页面宽度的50%*/
	        top:50%;	/*顶部盒子开始位置是页面高度的50%*/
	        margin-left:-150px;	/*左部开始位置再退回盒子宽度的一半*/
	        margin-top:-100px;	/*顶部开始位置再退回盒子高度的一半*/
	        background:#bababa;	/*定义盒子的背景颜色为灰色*/
	    }
	</style>
	<body>
	<div id="login">
	    <?php
	    /* 从Cookie中获取用户名username */
	    echo '您好：'.$_COOKIE["username"];
	    ?>
	    <a href="login.php?action=logout">退出</a>
	    <p>这里显示网页的主体内容</p>
	    <p>美好生活，就从生活导航开始。</p>
	</div>
	</body>
	</html>

在上面的脚本中，在内容显示之前，需要通过Cookie变量进行用户身份判断，若Cookie中的变量isLogin存在并且值为'1',则表明该用户已经通过身份验证登录了系统，并在页面中输出用户名，以及提供一个用户可以退出的操作链接。若Cookie中变量isLogin的值不为'1',页面跳转至登录脚本。因为在开发系统时，每一个操作脚本中都需要进行身份验证，所在可以将身份判断过程写在一个公共脚本中，然后在每个脚本中都去包含它。
直接运行index.php脚本文件时，因为没有登录不允许操作，所以直接转到login.php脚本中执行输出登录表单。如前面程序所示，如果在表单中输入正确的用户名“admin”和密码“123456”，就可以转到index.php脚本中显示首页内容。

Session的管理
-------------
Session 技术与 Cookie 相似，都是用来存储使用者的相关资料。但最大不同之处在于 Cookie 是将资料存放与客户端电脑之中，而 Session 则是将数据存放于服务器系统之下。 Session 的中文意思“会话”，在 Web 系统中，通常是指用户与 Web 系统的对话过程。也就是从用户打开浏览器登录到 Web 系统开始，到关闭浏览器离开 Web 系统的这段时间，在 Session 中注册的变量，在会话期间各个 Web 页面中都可以使用。

Session概述
^^^^^^^^^^^
Session在客户端仅需要保存由服务器为用户创建的一个session标识符，称为Session ID；而在服务器端保存Session变量的值。Session ID是一个既不会重复，又不容易被找到规律的，由32位十六进制数组成的字符串。Session ID会保存在客户端的Cookie里，如果用户阻止Cookie的使用，则可以将Session ID保存在用户浏览器地址栏的URL中。当用户请求Web服务器时，就会把Session ID发送给服务器，再通过Session ID提取保存在服务器中的Session变量。可以把Session中保存的变量，当做是这个用户的全局变量，同一个用户对每个脚本的访问都共享这些变量。

当某个用户向Web服务器发出请求时，服务器首先会检查这个客户端的请求里是否已经包含了一个Session ID。如果包含，说明之前已经为此用户创建过Session，服务器则按该Session ID把Session检索出来使用。如果客户端请求不包含Session ID，则为该用户创建一个Session，并且生成一个与此Session关联的Session ID，在本次响应中被传送给客户端保存。

Session是存放于服务器之中，为了避免对服务器系统造成过大的负荷，因此Session并不像Cookie是一种半永久性的存在。Session会因为下面两种状况而自然消失。

- 第一种情况，当使用者关闭浏览器，失去与服务器之间的连接之后，Session即会自动消失。而当使用者下次登入网站时，再另行配置一个Session使用；
- 第二种情况，Session指定的有效期限到期。一般而言PHP系统中对于Session的生存时间并无定义，也就是说预设值为零。但PHP开发人员可以通过修改php.ini配置文件中有关"session.cookie.lift_time"项目，来设定Session的有效期限，以秒为单位指定了发送到浏览器的Cookie的生命周期。值为0表示“直到关闭浏览器”，默认为0。当系统赋予Session有效期限后，不管浏览器是否开启，Session都会自动消失。

配置Session
^^^^^^^^^^^
在PHP配置文件中，有一组和Session相关的配置选项。通过对一些选项重新设置新值，就可以对Session进行配置，否则使用默认的Session配置。在php.ini文件中和Session有关的，一些有意义选项及其描述如下表：

+-------------------------+-------------------------------------------------------------------+-----------+
| 选项名                  | 描述                                                              | 默认值    |
+=========================+===================================================================+===========+
| session_auto_start      | 自动启动会话，0表示禁用，1表示开启                                | 0         |
+-------------------------+-------------------------------------------------------------------+-----------+
| session_cache_expire    | 为缓存中的会话页设置当前时间，单位分钟                            | 180       |
+-------------------------+-------------------------------------------------------------------+-----------+
| session_cookie_domain   | 指定会话Cookie中的域                                              | none      |
+-------------------------+-------------------------------------------------------------------+-----------+
| session_cookie_lifetime | Cookie中的Session ID在客户机上保存的时间，0表示延续到浏览器关闭时 | 0         |
+-------------------------+-------------------------------------------------------------------+-----------+
| session_cookie_path     | 在会话Cookie中要设置的路径                                        | /         |
+-------------------------+-------------------------------------------------------------------+-----------+
| session_name            | 会话的名称，在客户端用做Cookie的标识名称                          | PHPSESSID |
+-------------------------+-------------------------------------------------------------------+-----------+
| session_use_cookies     | 配置在客户端使用Cookie会话，1表示允许                             | 1         |
+-------------------------+-------------------------------------------------------------------+-----------+

Session的声明与使用
^^^^^^^^^^^^^^^^^^
Session 的设置不同于 Cookie ，必须先启动，在 PHP 中必须调用 session_start() 函数，以便让 PHP 核心程序，将和 Session 相关的内建环境变量预先载入至内存中。 session_start() 函数的语法格式如下所示：

``bool session_start([ array $options = [] ]) //创建Session，开始一个会话，进行Session初始化``

函数 Session_start() 有两个作用，一是开始一个会话，二是返回已经存在的会话。这个函数没有参数，且返回值均为 TRUE 。 **如果你是用基于 Cookie 的 Session ，在使用该函数开启 Session 之前，不能有任何输出的内容。因为基于 Cookie 的 Session 是在开启的时候，调用 session_start() 函数会生成一个唯一的 Session ID，需要保存在客户端电脑的 Cookie 中，和 setCookie() 函数一样，调用之前不能有任何的输出，空格或空行也不行。** 如果已经开启过Session，再次调用Session_start()函数时，不会再创建一个新的Session ID。因为当用户再次访问服务器时，该函数会通过从客户端携带过来的Session ID，返回已经存在的Session。所以在会话期间，同一个用户在访问服务器上任何一个页面时，都是使用同一个Session ID。
如果你不想在每个脚本都是用Session_start()函数来开启Session，可以在php.ini里设置“session.auto_start=1”,则无须每次使用Session之前都要调用session_start()函数。但启用该选项也有一些限制，则不能将对象放入Session中，因为类定义必须在启动Session之前加载以在会话中重建对象。所以不建议使用php.ini中的session.auto_start属性来开启Session。

注册一个会话变量和读取Session
^^^^^^^^^^^^^^^^^^^^^^^^^^^
在PHP中使用Session变量，除了必须要通过session_start()启动之外(即加载会话信息到内存中)，还要经过注册的过程。注册和读取Session变量，都要通过访问$_SESSION数组完成。自PHP4.1.0起，$_SESSION如同$_POST、$_GET或$_COOKIE等一样成为超级全局数组，但必须在调用session_start()函数开启Session之后才能使用。与 ``$HTTP_SESSION_VARS`` 不同,$_SESSION总是具有全局范围，因此不要对$_SESSION使用global关键字。在$_SESSION关联数组中的键名具有和PHP中普通变量名相同的命名规则。注册Session变量代码如下所示：

.. code-block:: php

    <?php
	session_start(); //启动Session的初始化
	$_SESSION["username"] = "sky"; // 注册Session变量，赋值为一个用户的名称
	$_SESSION["uid"] = 1; // 注册Session变量，赋值为一个用户的id
    ?>

执行该脚本后，两个Session变量就会被保存在服务器端的某个文件中。该文件的位置是通过php.ini文件，在session.save_path属性指定的目录下，为这个访问用户单独创建的一个文件，用来保存注册的Session变量。例如, 某个保存 Session 变量的文件名为类似 “ sess_09403850rf7sk39s67 ” 的形式，文件名中包含了 Session ID，所以每个访问用户在服务器中都有自己的保存 Session 变量文件，而且这个文件可以直接使用文本编辑器来打开。该文件的内容结构如下所示：

``变量名|类型:长度:值 //每个变量都适用相同的结构来保存``

上面的实例中 Session 注册了两个变量，如果在服务器中找到为该用户保存 Session 变量的文件，打开后可以看到如下的内容：

``username|s:6:"sky";uid|i:1:"1"; // 保存用户 Session 中注册的两个变量的内容``

注销变量与销毁Session
^^^^^^^^^^^^^^^^^^^^
当使用完一个Session变量后，可以将其删除，当完成一个会话后，也可以将其销毁。如果用户想退出Web系统，就需要为他提供一个注销的功能，把他的所有信息在服务器中销毁。销毁和当前Session有关的所有的资料，可以调用 ``session_destroy()`` 函数结束当前的会话，并清空会话中的所有资源。该函数的语法格式如下所示：

``bool session_destroy(void) //销毁和当前Session有关的所有资料``

相对于session_start()函数，该函数用来关闭Session的运作，如果成功则传回TRUE，销毁Session资料失败则返回false。但该函数并不会释放和当前Session相关的变量，也不会删除保存在客户端Cookie中的Session ID。因为$_SESSION数组和自定义的数组在使用上是相同的，所以我们使用unset()函数来释放在Session中注册的单个变量。如下所示：

.. code-block:: php

    <?php
	unset($_SESSION["username"]); //删除在Session中注册的用户名变量
	unset($_SESSION["password"]); //删除在Session中注册的用户密码变量
    ?>

一定要注意，不要(使用unset($_SESSION)删除整个$_SESSION数组，这样将不能再通过$_SESSION超全局数组注册变量了。但如果想把某个用户在Session中注册的所有变量都删除，可以直接将数组变量$_SESSION赋上一个空数组。如下所示：

``$_SESSION=array(); //将某个用户在Session中注册的变量全部清除``

PHP默认的Session是基于Cookie的，Session ID被服务器存储在客户端的Cookie中，所以在注销Session时也需要清除Cookie中保存的Session ID，而这就必须借助setCookie()函数完成。在Cookie中，保存Session ID的Cookie标识名称就是Session的名称，这个名称是在php.ini中，通过session.name属性指定的值。在PHP脚本中，额可以通过调用session_name()函数获取Session名称。删除保存在客户端Cookie的Session ID，代码如下所示：

.. code-block:: php

    <?php
	if(isset($_COOKIE[session_name()])) { // 判断Cookie中是否保存Session ID
	    setcookie(session_name(), "",time()-3600,'/'); //删除包含Session ID的Cookie
	}
    ?>

通过前面的介绍可以总结出，Session的注销过程共需要4个步骤。在下例中，提供完整的四个步骤代码，运行该脚本就可以关闭Session，并销毁与本次会话有关的所有资源。代码如下所示：

.. code-block:: php

    <?php
	//第一步：开启Session并初始化
	session_start();

	//第二部：删除所有Session的变量，也可以用unset($_SESSION[XXX])逐个删除
	$_SESSION = array();

	//第三部：如果使用基于Cookie的session，使用setCookkie()删除包含Session ID的cookie
	if(isset($_COOKIE[session_name()])) {
	    setCookie(session_name(), "", time()-42000, "/");
	}

	//第四部：最后彻底销毁session
	session_destroy();
    ?>

传递Session ID
^^^^^^^^^^^^^^
使用Session跟踪一个用户，是通过在各个页面之间传递唯一的Session ID，并通过Session ID提取这个用户在服务器中保存的Session变量。常见的Session ID传送方法有以下两种。

- 第一种方法时基于Cookie的方式传递Session ID，这种方法更优化，但由于不总是可用，因为用户在客户端可以屏蔽Cookie;
- 第二种方法则是通过URL参数进行传递，直接将会话ID嵌入到URL中去；

在Session的实现中通常都是采用基于Cookie的方式，客户端保存的Session ID就是一个Cookie。当客户禁用Cookie时，Session ID就不能再在Cookie中保存，也就不能在页面之间传递，此时Session失效。不过PHP5在Linux平台可以自动检查Cookie状态，如果客户端将它禁用，则系统自动把Session ID附加到URL上传送。而使用Windows系统作为Web服务器则无此功能。

通过Cookie传递Session ID
""""""""""""""""""""""""
如果客户端没有禁用Cookie，则在PHP脚本中通过session_start()函数进行初始化后，服务器会自动发送HTTP标头将Session ID保存到客户端电脑的Cookie中。类似于下面的设置方式：

``setCookie(session_name(),session_id(),0,'/')// //虚拟向Cookie中设置Session ID的过程``

- 第一个参数中调用session_name()函数，返回当前Session的名称作为Cookie的标识名称。Session名称的默认值为 ``PHPSESSID`` ，是在php.ini文件中由 ``session.name`` 选项指定的值。也可以在调用 ``session_name()`` 函数时提供参数改变当前Session的名称。
- 第二个参数中调用 ``session_id()`` 函数，返回当前Session ID作为Cookie的值。也可以通过调用 ``session_id()`` 函数时提供参数设定当前Session ID。
- 第三个参数的值0，是通过在php.ini文件中由 ``session.cookie_lifetime`` 选项设置的值。默认值为0，表示SessIon ID将在客户机的Cookie中延续到浏览器关闭。
- 最后一个参数'/'，也是通过PHP配置文件指定的值，在php.ini中由 ``session.cookie_path`` 选项设置的值。默认值为'/'，表示在Cookie中要设置的路径在整个域内都有效。

如果服务器成功将Session ID保存在客户端的Cookie中，当用户再次请求服务器时，就会把Session ID发送回来。所以当在脚本中再次使用 ``session_start()`` 函数时，就会根据Cookie中的Session ID返回已经存在的Session。

通过URL传递Session ID
"""""""""""""""""""""
如果客户浏览器支持Cookie，就把Session ID作为Cookie保存在浏览器中。但如果客户端禁止Cookie的使用，浏览器中就不存在作为Cookie的Session ID，因此在客户请求中不包含Cookie信息。如果调用session_start()函数时，无法从客户端浏览器中取得作为Cookie的Session ID，则又创建了一个新的Session ID，也就无法跟踪客户状态。因此，每次客户请求支持Session的PHP脚本，session_start()函数在开启Session时都会创建一个新的Session，这样就失去了跟踪用户状态的功能

　　如果客户浏览器不支持Cookie，PHP则可以重写客户请求的URL，把Session ID添加到URL信息中。可以手动地在每个超链接的URL中都添加一个Session ID，但工作量比较大，不建议使用这种方式。如下所示：

.. code-block:: php

    <?php
	session_start();
    echo '<a href="demo.php?'.session_name().'='.session_id() .'">链接演示</a>';
    ?>

在使用Linux系统做服务器时，并且选用PHP4.2以后的版本，则在编辑PHP时如果使用了 ``--enable-trans-sid`` 配置选项，和运行时选项 ``session.use_trans_sid`` 都被激活，在客户端禁用Cookie时，相对URL将被自动修改为包含会话ID。如果没有这么配置，或者使用Windows系统作为服务器时，可以使用常量SID。该常量在会话启动时被定义，如果客户端没有发送适当的会话Cookie，则SID的格式为 ``session_name=session_id`` ，否则就为一个空字符串。因此可以无条件地将其嵌入到URL中去。如下所示

.. code-block:: php

    <?php
	session_start();  // 开启Session
	$_SESSION["usemame"]="admin"; // 注册一个Session变量，保存用户名
	echo "Session ID:".session_id()."<br>"; // 在当前页面输出Session ID
	?>
	<a href="test2.php?<?php echo SID ?>">通过URL传递Session ID</a> <!-- 在URL中附加SID -->
    ?>

如果使用Linux系统作为服务器，并配置好相应的选项，就不用手动在每个URL后面附加SID，相对URL将被自动修改为包含Session ID。但要注意，非相对的URL被假定为指向外部站点，因此不能附加SID。因为这可能是个安全隐患，会将SID泄露给不同的服务器。

自定义Session
------------
在系统中使用Session技术跟踪用户时，Session默认的处理方式是使用Web服务器中的文件来记录每个用户的会话信息，通过php.ini中的 ``session_save_path`` 创建会话数据文件的路径。这种默认的处理方式虽然很方便，但也有一些缺陷。例如，登录用户如果非常大，文件操作的I/O开销就会很大，会严重影响系统的执行效率。另外，最主要的是本身的session机制不能跨机，因为对于访问量比较大的系统，通常都是采用多台web服务器进行并发处理，如果每台web服务器都各自独立地处理Session，就不可能达到跟踪用户的目的。这时就需要改变session的处理方式，常见的跨机方法就是通过自定义session的存储方式，可以将session信息使用NFS或SAMBA等共享技术保存到其他服务器中，或使用数据库来保存session信息，最优的方式是使用memcached来进行session存储

无论是用memcached、数据库、还是通过NFS或SAMBA共享session信息，其原理是一样的，都是通过PHP中的 ``session_set_save_handler()`` 函数来改变默认的处理方式，指定回调函数来自定义处理:

``Session_set_save_hander(callback open,callback close,call read,callback write,callback destro,callback gc);``

该函数共需要6个回调函数作为必选参数，分别代表了Session生命周期中的6个过程，用户通过自定义每个函数，来设置Session生命周期中每个环节的信息处理。

回调函数的执行时机如下所示：

+----------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| 回调函数 | 描述                                                                                                                                                                                                                         |
+==========+==============================================================================================================================================================================================================================+
| open     | 运行session_start()时执行，该函数需要声明两个参数，系统自动将php.ini中的session_save_path选项值传递给该函数的第一个参数，将Session名自动传递给第二个参数中，返回true则可以继续向下执行                                       |
+----------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| close    | 该函数不需要参数，在脚本执行完成或调用session_write_close()、session_destroy()时被执行，即在所有session操作完成后被执行。如果不需要处理，则直接返回true即可                                                                  |
+----------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| read     | 在运行session_start()时执行，因为在开启会话时，会read当前session数据并写入$_SESSION变量。需要声明一个参数，系统会自动将Session ID传递给该函数，用于通过Session ID获取对应的用户数据，返回当前用户的会话信息写入$_SESSION变量 |
+----------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| write    | 该函数在脚本结束和对$_SESSION变量赋值数据时执行。需要声明两个参数，分别是Session ID和串行化后Session信息字符串。在对$_SESSION变量赋值时，就可以通过Session ID找到存储的位置，并将信息写入。存储成功可以返回true继续向下执行  |
+----------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| destroy  | 在运行session_destroy()时执行，需要声明一个参数，系统会自动将Session ID传递给该函数，去删除对应的会话信息                                                                                                                    |
+----------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| gc       | 垃圾回收程序启动时执行。需要声明一个参数，系统自动将php.ini中的session_gc_maxlifetime选项值传给该函数，用于删除超过这个时间的Session信息，返回true则可以继续向下执行                                                         |
+----------+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

在运行session_start()时分别执行了open(启动会话)、read(读取session数据至$_SESSION)和gc(清理垃圾)，脚本中所有对$_SESSION的操作均不会调用这些回调函数。在调用session_destroy()函数时，执行destroy销毁当前session(一般是删除相应的记录或文件)，但此回调函数销毁的只是Session的数据，此时如果输出$_SESSION变量，仍然有值，但此值不会再close后被写回去。在调用session_write_close()函数时执行write和close，保存$_SESSION至存储，如果不手工使用此方法，则会在脚本结束时被自动执行。

 [注意]session_set_save_hander()函数必须在php.ini中设置 ``session_save_hander`` 选项的值为"user"时(用户自定义处理器)，才会被系统调用。

.. code-block:: php

    <?php
	    $sess_save_path ="";
	    function open($save_path,$session_name){
	        global $sess_save_path;
	        $sess_save_path = $save_path;
	        return true;
	    }
	    function close(){
	        return true;
	    }
	    function read($id){
	        global $sess_save_path;
	        $sess_file ="{$sess_save_path}/sess_{$id}";
	        return (string) @file_get_contents($sess_file);
	    }
	    function write($id,$sess_data){
	        global $sess_save_path;
	        $sess_file ="{$sess_save_path}/sess_{$id}";
	        if($fp=@fopen($sess_file,"w")){
	            $return = fwrite($fp,$sess_data);
	            fclose($fp);
	            return $return;
	        }else{
	            return false;
	        }
	    }
	    function destroy($id){
	        global $sess_save_path;
	        $sess_file ="{$sess_save_path}/sess_{$id}";
	        return (@unlink($sess_file));
	    }
	    function gc($maxlifetime){
	        global $sess_save_path;
	        foreach(glob("{$sess_save_path}/sess_*") as $filename){
	            if(filemtime($filename) + $maxlifetime <time() ){
	                @unlink($filename);
	            }
	        }
	        return true;
	    }
	    session_set_save_hander(“open","close","read","write","destroy","gc");
	    session_start();
    ?>

数据库处理
^^^^^^^^^
如果网站访问量非常大，需要采用负载均衡技术搭载多台Web服务器协同工作，就需要进行Session同步处理。使用数据库处理Session会比使用NFS及SAMBA更占优势，可以专门建立一个数据库服务器存放Web服务器的Session信息，当用户不管访问集群中的哪个Web服务器，都会去这个专门的数据库，访问自己在服务器端保存的Session信息，以达到Session同步的目的。另外，使用数据库处理Session还可以给我们带来很多好处，比如统计在线人数等。如果mysql也做了集群，每个mysql节点都要有这张表，并且这张Session表的数据要实时同步

　　在使用默认的文件方式处理Session时，有3个比较重要的属性，分别是文件名称、文件内容及文件的修改时间：通过文件名称中包含的Session ID，用户可以找到自己在服务器端的Session文件；通过文件内容用户可以在各个脚本中存取$_session变量；通过文件的修改时间则可以清除所有过期的Session文件。所以使用数据表处理Session信息，也最少要有这三个字段(Session ID、修改时间、Session内容信息)，当然如果考虑更多的情况，例如，用户改变了IP地址，用户切换了浏览器等，还可以再自定义一些其他字段。下面为Session设计的数据表结构包含5个字段，创建保存Session信息表session的SQL语句如下所示：

.. code-block:: sql

    CREATE TABLE session(
	    sid CHAR(32) NOT NULL DEFAULT '',
	    update INT NOT NULL DEFAULT 0,
	    client_ip CHAR(15) NOT NULL DEFAULT '',
	    user_agent CHAR(200) NOT NULL DEFAULT '',
	    data TEXT,
	    PRIMARY KEY(sid)
	);

数据表session创建成功后，再通过自定义的处理方式，将Session信息写入到数据库中:

.. code-block:: php

    <?php
	class DBSession {
	    public static $pdo;             //pdo的对象
	    public static $ctime;           //当前时间
	    public static $maxlifetime;     //最大的生存时间
	    public static $uip;             //用户正在用的ip
	    public static $uagent;          //用户正在用的浏览器

	    //开启和初使化使用的, 参数需要一个路
	    public static function start(PDO $pdo) {

	        self::$pdo = $pdo;
	        self::$ctime = time();
	        self::$maxlifetime = ini_get("session.gc_maxlifetime");
	        self::$uip = !empty($_SERVER['HTTP_CLIENT_IP']) ? $_SERVER['HTTP_CLIENT_IP'] : (!empty($_SERVER['HTTP_X_FORWARDED_FOR']) ? $_SERVER['HTTP_X_FORWARDED_FOR'] : (!empty($_SERVER['REMOTE_ADDR']) ? $_SERVER['REMOTE_ADDR'] : "") );

	        filter_var(self::$uip, FILTER_VALIDATE_IP) && self::$uip = '';
	        self::$uagent = !empty($_SERVER['HTTP_USER_AGENT']) ? $_SERVER['HTTP_USER_AGENT'] : "" ;

	        //注册过程， 让PHP自己处理session时，找这个函数指定的几个周期来完成
	        session_set_save_handler(
	            array(__CLASS__, "open"),
	            array(__CLASS__,"close"),
	            array(__CLASS__, "read"),
	            array(__CLASS__, "write"),
	            array(__CLASS__, "destroy"),
	            array(__CLASS__,"gc"));
	        session_start();  //开启会话
	    }

	    // 开启时， session_start()
	    public static function open($path, $name) {
	        return true;
	    }

	    //关闭
	    public static  function close() {
	        return true;
	    }

	    //读取 echo $_SESSION['username']
	    public static  function read($sid) {
	        $sql = "select * from session where sid = ?";
	        $stmt = self::$pdo -> prepare($sql);
	        $stmt -> execute(array($sid));
	        $result = $stmt -> fetch(PDO::FETCH_ASSOC);
	        //如果还没有会话信息，返回空字符串
	        if(!$result) {
	            return '';
	        }
	        //如果超出时间，销毁session
	        if($result['utime'] + self::$maxlifetime < self::$ctime) {
	            self::destroy($sid);
	            return '';
	        }
	        //如果用户换了ip或换了浏览器
	        if($result['uip'] != self::$uip || $result['uagent'] != self::$uagent) {
	            self::destroy($sid);
	            return '';
	        }
	        return $result['sdata'];

	    }

	    //写入 $_SESSION['username'] = "meizi";
	    public static  function write($sid, $data) {

	        //通过sid获取已经有的数据
	        $sql = "select * from session where sid = ?";
	        $stmt = self::$pdo->prepare($sql);
	        $stmt -> execute(array($sid));
	        $result = $stmt -> fetch(PDO::FETCH_ASSOC);

	        //如果已经获取到了数据，就不插入而更新
	        if($result) {
	            //如果数据和原来的不同才更新
	            if($result['sdata'] != $data || $result['utime']+10 < self::$ctime) {
	                $sql = "update session set sdata = ?, utime = ? where sid=?";
	                $stmt = self::$pdo->prepare($sql);
	                $stmt -> execute(array($data, self::$ctime, $sid));
	            }

	        //如果没有数据，就新插入一条数据
	        } else {

	            if(!empty($data)) {
	                $sql = "insert into session(sid, sdata, utime, uip, uagent) values(?, ?, ?, ?, ?)";
	                $stmt = self::$pdo -> prepare($sql);
	                $stmt -> execute(array($sid, $data, self::$ctime, self::$uip, self::$uagent));
	            }
	        }

	    }

	    //销毁 session_destroy()
	    public static  function destroy($sid) {
	        $sql = "delete from session where sid=?";
	        $stmt = self::$pdo->prepare($sql);
	        return $stmt -> execute(array($sid));
	    }

	    //回收垃圾
	    public static  function gc($maxlifetime) {
	        //    utime < ctime - self::$maxlifetime
	        $sql = "delete from session where utime < ?";
	        $stmt = self::$pdo->prepare($sql);
	        return $stmt -> execute(array(self::$ctime - self::$maxlifetime));
	    }
	}
	//开启
	DBSession::start($pdo);
    ?>

memcached处理
^^^^^^^^^^^^^
用数据库来同步Session会加大数据库的负担，因为数据库本来就是容易产生瓶颈的地方，但如果采用MemCache来处理Session是非常合适的，因为MemCache的缓存机制和Session非常相似。另外，MemCach可以做分布式，能够把Web服务器中的内存组合起来，成为一个”内存池”，不管是哪个服务器产生的Session，都可以放到这个“内存池”中，其他的Web服务器都可以使用。以这种方式来同步Session，不会加大数据库的负担，并且安全性也要比使用Cookie高。把session放到内存里面，读取也要比其他处理方式快很多。

自定义使用memcached处理session信息，和自定义数据库的处理方式相同，但要简单得多，因为MemCache的工作机制和Session技术很相似。

.. code-block:: php

    <?php
	class MemSession {
	    public static $mem;             //pdo的对象
	    public static $maxlifetime;     //最大的生存时间

	    public static function start(Memcache $mem) {
	        self::$mem = $mem;
	        self::$maxlifetime = ini_get("session.gc_maxlifetime");

	        //注册过程， 让PHP自己处理session时，按照这个函数指定的几个周期来完成
	        session_set_save_handler(
	            array(__CLASS__, "open"),
	            array(__CLASS__,"close"),
	            array(__CLASS__, "read"),
	            array(__CLASS__, "write"),
	            array(__CLASS__, "destroy"),
	            array(__CLASS__,"gc"));
	        session_start();  //开启会话
	    }

	    // 开启时，session_start()
	    public static function open($path, $name) {
	        return true;
	    }

	    //关闭
	    public static  function close() {
	        return true;
	    }

	    //读取 echo $_SESSION['username']
	    public static  function read($sid) {
	        $data = self::$mem -> get($sid);
	        if(empty($data)) {
	            return '';
	        }
	        return $data;
	    }

	    //写入
	    public static  function write($sid, $data) {
	        self::$mem -> set($sid, $data, MEMCACHE_COMPRESSED, self::$maxlifetime);
	    }

	    //销毁 session_destroy()
	    public static  function destroy($sid) {
	        self::$mem -> delete($sid, 0);

	    }

	    //回收垃圾
	    public static  function gc($maxlifetime) {
	        return true;
	    }
	}
	//创建对象
	$mem = new Memcache();
	//添加两台memcache服务器
	$mem -> addServer("localhost", 11211);
	$mem -> addServer("192.168.1.3", 11211);
	//开启
	MemSession::start($mem);
    ?>