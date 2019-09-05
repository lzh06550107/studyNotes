****************************************
正确加载 Javascript 和 CSS 到 WordPress
****************************************

有两种常用的 ``add_action`` 钩子可以加载 脚本和 CSS 到 WordPress：

- init ：确保始终为您的网站头部加载脚本和 CSS（如果使用 home.php ， index.php 或一个模板文件），以及其他“前端”文章、页面和模板样式。
- wp_enqueue_scripts ：“适当”的钩子方法，并不总是有效的，根据你的 WordPress 设置。


.. code-block:: php

    <?php
    wp_register_script( string $handle, string $src, array $deps = array(), string|bool|null $ver = false, bool $in_footer = false )
    wp_enqueue_script( $handle, $src, $deps, $ver, $in_footer );
    ?>

参数

- $handle（字符串）（必需）脚本名称。小写字符串。默认值：None
- $src（字符串）（可选）WordPress 根目录下的脚本路径示例： ``/wp-includes/js/scriptaculous/scriptaculous.js`` 。该参数只在 WordPress 不了解脚本情况时使用。默认值： None
- $deps（数组）（可选）脚本所依靠的句柄组成的数组；加载该脚本前需要加载的其它脚本。若没有依赖关系，返回 false 。该参数只在 WordPress 不了解脚本情况时使用。默认值： array()
- $ver（字符串）（可选）指明脚本版本号的字符串（若存在版本号）。默认为 false 。该参数可确保即使在启用缓存的状态下，发送给客户端的仍然是正确版本，因此如果版本号可用且对脚本有意义，包含该版本号。默认值：false
- $in_footer（布尔型）（可选）通常情况下脚本会被放置在区块中。如果该函数为 true ，脚本则会出现在区块的最下方。要求主题在适当的位置中包含有 ``wp_footer()`` 钩子。（WordPress新功能）默认值：false

加载外部 jQuery 库和主题自定义的脚本、样式
===========================================
下面这个例子在 ``add_action`` 钩子中使用 ``init`` 。使用 ``init`` 有两个原因，一是因为我们正在注销WordPress 默认的 jQuery 库，然后加载谷歌的 jQuery 库；二是确保在 WordPress 的头部就加载脚本和 CSS 。

使用 ``if(!is_admin())`` 是为了确保这些脚本和 css 只在前端加载，不会再后台管理界面加载。

.. code-block:: php

    /** Google jQuery Library, Custom jQuery and CSS Files */
    function myScripts() {
        wp_register_script( 'google', 'http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.js' );
        wp_register_script( 'default', get_template_directory_uri() . '/jquery.js' );
        wp_register_style( 'default', get_template_directory_uri() . '/style.css' );
        if ( !is_admin() ) { /** Load Scripts and Style on Website Only */
            wp_deregister_script( 'jquery' );
            wp_enqueue_script( 'google' );
            wp_enqueue_script( 'default' );
            wp_enqueue_style( 'default' );
        }
    }
    add_action( 'init', 'myScripts' );

加载WP默认 jQuery 库和主题自定义的脚本、样式
============================================
第3行：使用 ``array('jquery')`` 是为了告诉 ``WordPress`` 这个 ``jquery.js`` 是依赖 WordPress 的 jQuery 库文件，从而使 ``jquery.js`` 在 WordPress jQuery 库文件后加载。

.. code-block:: php

    /** Add Custom jQuery and CSS files to a Theme */
    function myScripts() {
        wp_register_script( 'default', get_template_directory_uri() . '/jquery.js', array('jquery'), '' );
        wp_register_style( 'default', get_template_directory_uri() . '/style.css' );
        if ( !is_admin() ) { /** Load Scripts and Style on Website Only */
            wp_enqueue_script( 'default' );
            wp_enqueue_style( 'default' );
        }
    }
    add_action( 'init', 'myScripts' );

加载 print.css 到你的WordPress主题
==================================
第 3 行：最后的 ``print`` 是媒体屏幕调用，确保 ``print.css`` 在网站的打印机中的文件加载时才加载。

.. code-block:: php

    /** Adding a Print Stylesheet to a Theme */
    function myPrintCss() {
        wp_register_style( 'print', get_template_directory_uri() . '/print.css', '', '', 'print' );
        if ( !is_admin() ) { /** Load Scripts and Style on Website Only */
            wp_enqueue_style( 'print' );
        }
    }
    add_action( 'init', 'myPrintCss' );


使用 wp_enqueue_scripts 替换 init
==================================
如果你要在文章或页面加载唯一的脚本，那就应该使用 ``wp_enqueue_scripts`` 替换 ``init`` 。使用 ``wp_enqueue_scripts`` 仅仅只会在前台加载脚本和 CSS ，不会在后台管理界面加载，所以没必要使用 ``!is_admin()`` 判断。

使用 is_single() 只在文章加载脚本或CSS
--------------------------------------
第 3 行的 ``#`` 替换为文章的 ``ID`` 就可以让脚本和 ``css`` 只加载到那篇文章。当然，如果直接使用 ``is_single()`` （不填ID），就会在所有文章加载脚本和 CSS 。

.. code-block:: php

    /** Adding Scripts To A Unique Post */
    function myScripts() {
        if ( is_single(#) ) { /** Load Scripts and Style on Posts Only */
            /** Add jQuery and/or CSS Enqueue */
        }
    }
    add_action( 'wp_enqueue_scripts', 'myScripts' );

使用 is_page() 只在页面加载脚本或CSS
------------------------------------
第 3 行的 ``#`` 替换为页面的 ``ID`` 就可以让脚本和 ``css`` 只加载到那个页面。当然，如果直接使用 ``is_single()`` （不填ID），就会在所有页面加载脚本和 CSS 。

.. code-block:: php

    /** Adding Scripts To A Unique Page */
    function myScripts() {
        if ( is_page(#) ) { /** Load Scripts and Style on Pages Only */
            /** Add jQuery and/or CSS Enqueue */
        }
    }
    add_action( 'wp_enqueue_scripts', 'myScripts' );

使用 admin_enqueue_scripts 加载脚本到后台
=========================================
这个例子将在整个后台管理界面加载脚本和 CSS 。这个方法不推荐用在插件上，除非插件重建了整个后台管理区。

第 10 行使用 ``admin_enqueue_scripts`` 替换了 ``init`` 或 ``wp_enqueue_scripts``

第 5、6 行，如果你要自定义后台管理区，你可以需要禁用默认的 WordPress CSS调用。

.. code-block:: php

    /** Adding Scripts To The WordPress Admin Area Only */
    function myAdminScripts() {
        wp_register_script( 'default', get_template_directory_uri() . '/jquery.js', array('jquery'), '' );
        wp_enqueue_script( 'default' );
        //wp_deregister_style( 'ie' ); /** removes ie stylesheet */
        //wp_deregister_style( 'colors' ); /** disables default css */
        wp_register_style( 'default', get_template_directory_uri() . '/style.css',  array(), '', 'all' );
        wp_enqueue_style( 'default' );
    }
    add_action( 'admin_enqueue_scripts', 'myAdminScripts' );

加载脚本和CSS到WordPress登录界面
================================
第 6 行：我无法弄清楚如何在在登录页面注册/排序 CSS 文件，所以这行手动添加样式表。

第 10-14行：用来移除 WordPress 默认的样式表。

.. code-block:: php

    /** Adding Scripts To The WordPress Login Page */
    function myLoginScripts() {
        wp_register_script( 'default', get_template_directory_uri() . '/jquery.js', array('jquery'), '' );
        wp_enqueue_script( 'default' );
    ?>
        <link rel='stylesheet' id='default-css' href='<?php echo get_template_directory_uri() . '/style.css';?>' type='text/css' media='all' />
    <?php }
    add_action( 'login_enqueue_scripts', 'myLoginScripts' );
    /** Deregister the login css files */
    function removeScripts() {
        wp_deregister_style( 'wp-admin' );
        wp_deregister_style( 'colors-fresh' );
    }
    add_action( 'login_init', 'removeScripts' );

从WordPress插件加载脚本和CSS
============================
WordPress 插件加载脚本和 CSS 也是常见的。主要的不同之处在于文件的 URL 。主题使用的是 ``get_template_directory_uri`` ，而插件应该用 ``plugins_url`` ，因为文件是从插件目录进行加载的。

从插件加载脚本和CSS

这个例子将在整个网站前端加载脚本和CSS。

.. code-block:: php

    /** Global Plugin Scripts for Outside of Website */
    function pluginScripts() {
        wp_register_script( 'plugin', plugins_url( 'jquery.js' , __FILE__ ), array('jquery'), '' );
        wp_register_style( 'plugin', plugins_url( 'style.css' , __FILE__ ) );
        if ( !is_admin() ) { /** Load Scripts and Style on Website Only */
            wp_enqueue_script( 'plugin' );
            wp_enqueue_style( 'plugin' );
        }
    }
    add_action( 'init', 'pluginScripts' );

从插件加载脚本和CSS到后台管理区
===============================
如果你需要在整个后台管理区加载脚本和 CSS ，就使用 ``admin_enqueue_scripts`` 替换 ``init`` 。

.. code-block:: php

    /** Global Plugin Scripts for The WordPress Admin Area */
    function pluginScripts() {
        wp_register_script( 'plugin', plugins_url( 'jquery1.js' , __FILE__ ), array('jquery'), '' );
        wp_enqueue_script( 'plugin' );
        wp_register_style( 'plugin', plugins_url( 'style1.css' , __FILE__ ) );
        wp_enqueue_style( 'plugin' );
    }
    add_action( 'admin_enqueue_scripts', 'pluginScripts' );

从插件加载脚本和CSS到插件设置页面
=================================
例子只会加载所需的脚本和 CSS 到插件设置页面，不会在管理区的其他页面加载。

第 3 行：自定义 page= 后面的值为你的插件设置页面

.. code-block:: php

    /** Adding Scripts On A Plugins Settings Page */
    function pluginScripts() {
        if ( $_GET['page'] == "plugin_page_name.php" ) {
            wp_register_script( 'plugin', plugins_url( 'jquery.js' , __FILE__ ), array('jquery'), '' );
            wp_enqueue_script( 'plugin' );
            wp_register_style( 'plugin', plugins_url( 'style.css' , __FILE__ ) );
            wp_enqueue_style( 'plugin' );
        }
    }
    add_action( 'admin_enqueue_scripts', 'pluginScripts' );

将 jQuery 库移动到页脚
======================
你不能将 WordPress 默认的 jQuery 库移动到页面底部，但是你可以将自定义的 jQuery 或其他外部jQuery 库（比如 Google 的）移动到底部。不要将 CSS 移动到页面底部。

第 3、4 行：最后的 'true' 告诉 WordPress 在页面底部加载这些脚本。

.. code-block:: php

    /** Moves jQuery to Footer */
    function footerScript() {
            wp_register_script('jquery', ("http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.js"), false, '', true );
            wp_register_script( 'default', get_template_directory_uri() . '/jquery.js', false, '', true );
        if ( !is_admin() ) { /** Load Scripts and Style on Website Only */
            wp_deregister_script( 'jquery' );
            wp_enqueue_script( 'jquery' );
            wp_enqueue_script( 'default' );
        }
    }
    add_action( 'init', 'footerScript' );

根据不用的用户角色和功能加载jQuery和CSS
=======================================
如果你的网站有作者、编辑和其他管理员，你可能需要通过 jQuery 来为他们显示不同的信息。你需要使用 current_user_can 确定登录的用户的 角色和功能 。

下面三个例子中，如果用户已经登录，将在整个网站加载这些脚本和 CSS 。使用 ``!is_admin()`` 包装 ``enqueue_script`` 确保只在前台加载，或者在 ``add_action`` 使用 ``admin_enqueue_scripts`` 就可以确保只在后台管理区加载。

为可以“编辑文章”的管理员加载脚本和CSS
-------------------------------------
只对超级管理员和网站管理员生效

.. code-block:: php

    /** Add CSS & jQuery based on Roles and Capabilities */
    function myScripts() {
        if ( current_user_can('edit_posts') ) {
            /** Add jQuery and/or CSS Enqueue */
        }
    }
    add_action( 'init', 'myScripts' );

为所有登录用户加载脚本和CSS
----------------------------

.. code-block:: php

    /** Admins / Authors / Contributors /  Subscribers */
    function myScripts() {
        if ( current_user_can('read') ) {
            /** Add jQuery and/or CSS Enqueue */
        }
    }
    add_action( 'init', 'myScripts' );

为管理员以外的已登录用户加载脚本和CSS
-------------------------------------

.. code-block:: php

    /** Disable for Super Admins / Admins enable for Authors / Contributors /  Subscribers */
    function myScripts() {
        if ( current_user_can('read') && !current_user_can('edit_users') ) {
            /** Add jQuery and/or CSS Enqueue */
        }
    }
    add_action( 'init', 'myScripts' );

上面的例子如果使用相同的 ``add_action`` ，就可以被合并成一个单一的函数。 换句话说，您可以使用多个 ``if`` 语句在一个函数中分裂了你的脚本和CSS调用，如： ``if_admin`` ``！if_admin``  ， ``is_page`` ， ``is_single`` 和 ``current_user_can`` 的，因为每次使用相同的 ``add_action`` 的 ``init`` 。

正确加载 CSS 到 WordPress
=========================

在 WordPress 中加载 CSS 的错误方式
----------------------------------
多年来，WordPress 已经发展了其代码，以便使它越来越灵活，排队加载 CSS 和 JavaScript 就是在朝这个方向移动。我们的坏习惯已经保持一段时间了，尽管我们知道 WordPress 介绍了排队加载 CSS 和 JavaScript 的方法，我们还是继续添加这类代码到主题的 ``header.php`` 文件：

.. code-block:: php

    <link rel="stylesheet" href="<?php echo get_stylesheet_uri(); ?>">

或者我们添加下面的代码到主题的 ``functions.php`` ，而且认为这个方法更好些：

.. code-block:: php

    <?php
    function add_stylesheet_to_head() {
        echo "<link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>";
    }
    add_action( 'wp_head', 'add_stylesheet_to_head' );
    ?>

在上面的情况下， WordPress 不能确定是否在在页面加载了 CSS 文件。这可能是一个可怕的错误！

如果另一个插件使用相同的 CSS 文件，就无法检查 CSS 文件是否已经被包含在页面中。然后插件第二次加载同一个文件，造成重复的代码。

幸运的是，WordPress 有一个非常简单的解决方案： **注册和排队样式表** 。

在 WordPress 中加载 CSS 的正确方式
----------------------------------
正如我们前面所说， WordPress 已经成长了很多，多年来，我们不得不思考在世界上每一个 WordPress 用户。

除了这些，我们还必须考虑成千上万的 WordPress 插件。但是，不要让这些大的数字吓到你： WordPress 有非常有用的函数，来为我们正确地加载 CSS 样式到 WordPress 。

让我们一起来看看。

注册 CSS 文件
^^^^^^^^^^^^^

如果你要加载 ``CSS`` 样式表，你首先应该使用 ``wp_register_style()`` 函数进行注册：

.. code-block:: php

    <?php
        wp_register_style( $handle, $src, $deps, $ver, $media );
    ?>

- $handle（字符串，必需）是你的样式表唯一名称。其他函数将使用这个 ``handle`` 来排队并打印样式表。
- $src（字符串，必需）指的是样式表的 ``URL`` 。您可以使用函数，如 ``get_template_directory_uri()`` 来获取主题目录中的样式文件。永远不要去想硬编码了！
- $deps （数组，可选）处理相关样式的名称。如果丢失某些其他样式文件将导致你的样式表将无法正常工作，你可以使用该参数设置“依赖关系”。
- $ver （字符串或布尔型，可选）版本号。你可以使用你的主题的版本号或任何一个你想要的。如果您不希望使用一个版本号，将其设置为 ``null`` 。默认为 ``false`` ，这使得 WordPress 的添加自己的版本号。
- $media （字符串，可选）是指 ``CSS`` 的媒体类型，比如 screen 或 handheld 或 print 。如果你不知道是否需要使用这个，那就不使用它。默认为 all 。

以下是 wp_register_style() 函数的一个例子：

.. code-block:: php

    <?php

    // wp_register_style() 示例
    wp_register_style(
        'my-bootstrap-extension', // 名称
        get_template_directory_uri() . '/css/my-bootstrap-extension.css', // 样式表的路径
        array( 'bootstrap-main' ), // 依存的其他样式表
        '1.2', // 版本号
        'screen', // CSS 媒体类型
    );

    ?>

在 WordPress 中，注册样式是“可选的”。如果你的样式不会被其他插件使用，或者你不打算使用任何代码来再次加载它，你可以直接排队插入样式而不需要注册它。继续看看它是如何实现的。

排队 CSS 文件
^^^^^^^^^^^^^
注册我们的风格文件后，我们需要 排队 它，使其准备好在我们主题的 <head> 部分加载。

我们使用 ``wp_enqueue_style()`` 函数来实现：

.. code-block:: php

    <?php
        wp_enqueue_style( $handle, $src, $deps, $ver, $media );
    ?>

该函数的参数和上面的 ``wp_register_style()`` 函数是一样的，就不再重复。

但是，正如我们所说的， ``wp_register_style()`` 函数是不强制使用的，我要告诉你，你可以用两种不同的方式使用 ``wp_enqueue_style()`` ：

.. code-block:: php

    <?php

    // 如果我们之前已经注册过样式
    wp_enqueue_style( 'my-bootstrap-extension' );

    // 如果我们之前没有注册，我们不得不设置 $src 参数！
    wp_enqueue_style(
        'my-bootstrap-extension',
        get_template_directory_uri() . '/css/my-bootstrap-extension.css',
        array( 'bootstrap-main' ),
        null, // 举例不适用版本号
        // ...并且没有指定CSS媒体类型
    );

    ?>

请记住，如果一个插件将要用到你的样式表，或者你打算将在你的主题的不同地方进行加载，你绝对应该先注册。

加载样式到我们的网站
^^^^^^^^^^^^^^^^^^^^
我们不能在主题中随便找个地方使用 ``wp_enqueue_style()`` 函数 – 我们需要使用“动作”钩子。还有我们可以使用各种用途的三个动作钩子：

- wp_enqueue_scripts 用来在网站前台加载脚本和 CSS
- admin_enqueue_scripts 用来在后台加载脚本和 CSS
- login_enqueue_scripts 用来在WP登录页面加载脚本和 CSS

以下是这些钩子的示例：

.. code-block:: php

    <?php

    // 在网站前台加载css
    function mytheme_enqueue_style() {
        wp_enqueue_style( 'mytheme-style', get_stylesheet_uri() );
    }
    add_action( 'wp_enqueue_scripts', 'mytheme_enqueue_style' );

    // 在后台加载css
    function mytheme_enqueue_options_style() {
        wp_enqueue_style( 'mytheme-options-style', get_template_directory_uri() . '/css/admin.css' );
    }
    add_action( 'admin_enqueue_scripts', 'mytheme_enqueue_options_style' );

    // 在登录页面加载css
    function mytheme_enqueue_login_style() {
        wp_enqueue_style( 'mytheme-options-style', get_template_directory_uri() . '/css/login.css' );
    }
    add_action( 'login_enqueue_scripts', 'mytheme_enqueue_login_style' );

    ?>

WordPress 有一个重要的公告： `使用 wp_enqueue_scripts() ，不要用 wp_print_styles() <http://api.viglink.com/api/click?format=go&jsonp=vglnk_jsonp_140417383088318&key=be398d023a2fd4754907632fc9c47398&libId=e303a750-f5cb-4678-bf0b-c3bbb15f2973&loc=http%3A%2F%2Fcode.tutsplus.com%2Ftutorials%2Floading-css-into-wordpress-the-right-way--cms-20402&v=1&out=http%3A%2F%2Fmake.wordpress.org%2Fcore%2F2011%2F12%2F12%2Fuse-wp_enqueue_scripts-not-wp_print_styles-to-enqueue-scripts-and-styles-for-the-frontend%2F&ref=http%3A%2F%2Fxianguo.com%2Fdoing%2F7KTMh&title=Loading%20CSS%20Into%20WordPress%20the%20Right%20Way%20-%20Tuts%2B%20Code%20Tutorial&txt=%22Use%20%3Ccode%3Ewp_enqueue_scripts()%3C%2Fcode%3E%2C%20not%20%3Ccode%3Ewp_print_styles()%3C%2Fcode%3E!%22>`_ ，它会告诉你一个与 WordPress3.3版本可能的不兼容错误。

一些额外的函数
--------------
WordPress 有一些关于 CSS 非常有用的函数：他们允许我们打印内嵌样式，查看样式文件的排队状态，添加元数据以及注销样式。

让我们一起来看看。

添加动态内联样式： wp_add_inline_style()
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
如果你的主题有选项可自定义主题的样式，你可以使用 ``wp_add_inline_style()`` 函数来打印内置的样式：

.. code-block:: php

<?php

function mytheme_custom_styles() {
    wp_enqueue_style( 'custom-style', get_template_directory_uri() . '/css/custom-style.css' );
    // 获取主题可设置值的项
    $bold_headlines = get_theme_mod( 'headline-font-weight' ); // 比方说，它的值是粗体“bold”
    $custom_inline_style = '.headline { font-weight: ' . $bold_headlines . '; }';
    wp_add_inline_style( 'custom-style', $custom_inline_style );
}
add_action( 'wp_enqueue_scripts', 'mytheme_custom_styles' );

?>

方便快捷。但请记住：你必须使用与后面要添加的内联样式样式表相同的 ``hadle`` 名称。

检查样式表的排队状态：wp_style_is()
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
在某些情况下，我们可能需要一个风格的状态信息：是否注册，是否入列，它是打印或等待打印？您可以使用 ``wp_style_is()`` 函数来确定它：

.. code-block:: php

    <?php

    /*
     * wp_style_is( $handle, $state );
     * $handle - name of the stylesheet
     * $state - state of the stylesheet: 'registered', 'enqueued', 'done' or 'to_do'. default: 'enqueued'
     */

    // wp_style_is() 示例
    function bootstrap_styles() {

        if( wp_style_is( 'bootstrap-main' ) {

            // 排队 my-custom-bootstrap-theme 如果 bootstrap-main 已经排队
            wp_enqueue_style( 'my-custom-bootstrap-theme', 'http://url.of/the/custom-theme.css' );

        }

    }
    add_action( 'wp_enqueue_scripts', 'bootstrap_styles' );

    ?>

添加元数据到样式表：wp_style_add_data()
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
``wp_style_add_data()`` 是一个非常棒的函数，它可以让你添加元数据到你的样式中，包括条件注释、RTL的支持和更多！

.. code-block:: php

    <?php

    /*
     * wp_style_add_data( $handle, $key, $value );
     * Possible values for $key and $value:
     * 'conditional' string      Comments for IE 6, lte IE 7 etc.
     * 'rtl'         bool|string To declare an RTL stylesheet.
     * 'suffix'      string      Optional suffix, used in combination with RTL.
     * 'alt'         bool        For rel="alternate stylesheet".
     * 'title'       string      For preferred/alternate stylesheets.
     */

    // wp_style_add_data() 示例
    function mytheme_extra_styles() {
        wp_enqueue_style( 'mytheme-ie', get_template_directory_uri() . '/css/ie.css' );
        wp_style_add_data( 'mytheme-ie', 'conditional', 'lt IE 9' );
        /*
         * alternate usage:
         * $GLOBALS['wp_styles']->add_data( 'mytheme-ie', 'conditional', 'lte IE 9' );
         * wp_style_add_data() is cleaner, though.
         */
    }

    add_action( 'wp_enqueue_scripts', 'mytheme_ie_style' );

    ?>

注销样式文件：wp_deregister_style()
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
如果你需要“注销”样式表（为了使用修改后的版本，例如重新注册），你可以用 ``wp_deregister_style()`` 实现。

.. code-block:: php

    <?php

    function mytheme_load_modified_bootstrap() {
        // 如果 bootstrap-main 之前已注册...
        if( wp_script_is( 'bootstrap-main', 'registered' ) ) {
            // ...取消注册...
            wp_deregister_style( 'bootstrap-main' );
            // ...取而代之，注册我们自定义的 modified bootstrap-main.css...
            wp_register_style( 'bootstrap-main', get_template_directory_uri() . '/css/bootstrap-main-modified.css' );
            // ...然后排队它
            wp_enqueue_style( 'bootstrap-main' );
        }
    }

    add_action( 'wp_enqueue_scripts', 'mytheme_load_modified_bootstrap' );

    ?>

虽然它不是必须的，但是通常你注销了一个样式，就应该重新注册一个样式，否则可能会打乱其他的一些东西。

还有一个类似的函数叫做 ``wp_dequeue_style()`` ，正如它的名字所暗示的一样，用来取消已经排队的样式表。

https://developer.wordpress.org/themes/basics/including-css-javascript/


WordPress 传递PHP数据或字符串到JavaScript
=========================================

将所有静态字符串数据保存在PHP文件中是一个很好的做法。如果你需要在 JavaScript 使用一些数据，在HTML中将你的数据作为 ``data-*`` 属性也是一个好方法。但是在某些特定的情况下，你没有其他选择，只能将字符串直接传递到 JavaScript 代码中。

如果你引用了一个 JavaScript 库，并且你需要在 ``header.php`` 文件中初始化 JavaScript 对象，然后传递数据到它的属性中，那么这篇文章很适合你。本文将教你如何正确地传递 PHP 数据和静态字符串到你的 JavaScript 库。

为什么需要传递数据到JavaScript
------------------------------
让我举例说明一些需要将数据传递到JavaScript 的典型场景。例如，有时我们需要得到这些值转换成JavaScript代码：

- 首页，管理后台，插件，主题或  ajax URL，
- 翻译的字符串，或
- 一个主题或WordPress的选项

传递数据的常用方式
------------------
比方说，我们有一个叫 myLibrary.js 使用 jQuery 的文件，要引入到我们的 WordPress 站点：

.. code-block:: js

    var myLibraryObject;

    (function($) {
        "use strict";

        myLibraryObject = {
            home: '', // populate this later

            pleaseWaitLabel: '', // populate use this later

            someFunction: function() {
                // code which uses the properties above
            }
        }
    });

我们在主题的 ``functions.php`` 通过下面的代码调用它：

.. code-block:: php

    wp_enqueue_script( 'my_js_library', get_template_directory_uri() . '/js/myLibrary.js' );

现在的问题是，我们如何才能填充 ``home`` 和 ``pleaseWaitLabel`` 属性，你可能已经本能地在 ``header.php`` 添加了这些代码来得到所需的数据：

.. code-block:: html

    <script>
    (function( $ ) {
      "use strict";

       $(function() {

         myLibraryObject.home = '<?php echo get_stylesheet_directory_uri() ?>';
         myLibraryObject.pleaseWaitLabel = '<?php _e( 'Please wait...', 'default' ) ?>';

       });
    }(jQuery));
    </script>

这样可以实现我们的目的，但是， WordPress 已经为我们提供了一个更好的和更短的方式来做到这一点。

WordPress的方式
---------------
将数据传递到 JavaScript 的推荐方法是使用 ``wp_localize_script()`` 函数。此功能意味着您要使用 ``wp_enqueue_scripts()`` 来排队脚本后才使用。

.. code-block:: php

    wp_localize_script( $handle, $objectName, $arrayOfValues );

下面是该函数的参数：

- $handle （字符串）（必填） 你所要附加数据的处理脚本，它为前面注册的脚本
- $objectName （字符串）（必填） 将包含数据的js脚本对象名称
- $arrayOfValues 包含名称和要传递给js脚本值的php关联数组

调用此函数之后， ``$objectName`` 变量将在指定的js脚本中可用。

执行 wp_localize_script
-----------------------
让我们使用我们的新数据传递的方法来调整前面的例子。首先，我们排队脚本然后在我们的 ``functions.php`` 使用 ``wp_localize_script`` 调用 ：

.. code-block:: php

    wp_enqueue_script( 'my_js_library', get_template_directory_uri() . '/js/myLibrary.js' );

    $dataToBePassed = array(
        'home'            => get_stylesheet_directory_uri(),
        'pleaseWaitLabel' => __( 'Please wait...', 'default' )
    );
    wp_localize_script( 'my_js_library', 'php_vars', $datatoBePassed );

现在我们的 ``home`` 和 ``pleaseWaitLabel`` 值可以被我们的 jQuery 库内部通过 ``php_vars`` 变量访问。

.. code-block:: js

    var myLibraryObject;

    (function($) {
        "use strict";

        myLibraryObject = {
            home: php_vars.home,

            pleaseWaitLabel: php_vars.pleaseWaitLabel,

            someFunction: function() {
                // code which uses the properties above
            }
        }
    });

注意：

.. code-block:: js

    jQuery(document).ready(function($) {
        // "$"符号在这里才能被识别
    });
    //或者

    (function( $ ){
        // "$"符号在这里才能被识别
    })( jQuery );

WP 的 jQuery 和原版唯一的不同, 就是在最后一行加了 jQuery.noConflict(); 这个 noConflict() 就是为了与其它的 library 兼容性, 如 :Prototype , MooTools 或 YUI 。
