======================
wp-setting.php源码分析
======================
define( 'WPINC', 'wp-includes' );

# 定义常量WPINC，为后面的路径使用做准备；

require( ABSPATH . WPINC . '/load.php' );

# 加载wp-includes目录（以下不特殊说明则文件目录均为wp-includes）下load.php文件，该文件无执行代码，主要用于定义一些WP可能用到的一些函数，主要函数详解见 :doc:`WordPress初始化核心文件load.php源码分析 </WordPress初始化核心文件load.php源码分析>` 

require( ABSPATH . WPINC . '/default-constants.php' );

# 加载目录下default-constants.php文件，该文件无执行代码，主要用于定义一些WP可能用到的函数，这些函数定义常量如插件目录、主题目录、内存、COOKIE、SSL等，主要函数详解见 :doc:`WordPress初始化核心文件default-constants.php源码分析 </WordPress初始化核心文件default-constants.php源码分析>` 

require_once( ABSPATH . WPINC . '/plugin.php' );

require( ABSPATH . WPINC . '/version.php' );

# 加载version.php文件，该文件无执行代码，主要定义一些跟WordPress版本等相关的常量，包括WP版本、PHP版本、MySQL版本、默认本地语言包等；

wp_initial_constants();

# 执行在default-constants.php文件中定义的该函数完成常量初始化，这些常量如内存、DEBUG、缓存等；

wp_check_php_mysql_versions();

# 执行在load.php中定义的该函数，检查mysql版本；

@ini_set( 'magic_quotes_runtime', 0 );

@ini_set( 'magic_quotes_sybase',  0 );

# 运行时禁用魔术引用，模式引用放在wp-settings.php使用wpdb之后；

date_default_timezone_set( 'UTC' );

# 设置时区；

wp_unregister_GLOBALS();

# 执行在load.php中定义的该函数，关闭全局注册变量；

unset( $wp_filter, $cache_lastcommentmodified );

# 注销这两个变量以免影响WP运行；

wp_fix_server_vars();

# 执行在load.php中定义的该函数，启动时规范$_SERVER；

wp_favicon_request(); 

# 执行在load.php中定义的该函数，检查favico是否丢失；

wp_maintenance(); 

# 执行在load.php中定义的该函数，检查是否处于维护模式；

wp_debug_mode(); 

# 执行在load.php中定义的该函数，检查是否处于DEBUG模式；

缓存？？？？？？

wp_set_lang_dir();

# 执行在load.php中定义的该函数，设置语言包目录；

require( ABSPATH . WPINC . '/compat.php' );

# 加载compat.php文件，该文件无执行代码，提供某些旧PHP版本缺少或默认不包含的函数（用于支持不同版本 PHP 上的兼容和移植）；

require( ABSPATH . WPINC . '/class-wp-list-util.php' );

require( ABSPATH . WPINC . '/functions.php' );

# 加载functions.php文件，该文件无执行代码（除加载option.php文件外），定义WP主要的API（API是一组函数，通常以库的形式存在供用户调用），主要函数详解见“WordPress初始化核心文件functions.php源码分析”；

require( ABSPATH . WPINC . '/class-wp-matchesmapregex.php' );

require( ABSPATH . WPINC . '/option.php' )(移除)

# 加载option.php文件，该文件提供跟option后台及默认选项相关的API，无执行代码，主要函数详解见“WordPress初始化核心文件option.php源码分析”；

require( ABSPATH . WPINC . '/class-wp.php' );

# 定义WP类，无执行代码，定义类WP和WP_MatchesMapRegex，WP类中部分方法功能如设定查询列表、解析查询parse_request()、创建主循环query_posts()、处理404请求、定义main()方法等；其中，main()方法详解见“WordPress核心类WP内main()方法源码分析”；

require( ABSPATH . WPINC . '/class-wp-error.php' );

# 定义WP类，处理各种类型的错误提示；

require( ABSPATH . WPINC . '/plugin.php' )(移除)

# 定义插件API，无执行代码，主要用于创建动作、过滤、挂载函数等，主要函数详解见“WordPress初始化核心文件plugin.php源码分析”；

require( ABSPATH . WPINC . '/pomo/mo.php' );

# 加载本路径下文件translations.php和streams.php并定义语言处理类MO，无执行代码，MO相关文件均用于程序语言翻译本处不再详解；

require_wp_db();

# 执行在load.php中定义的该函数，加载数据库类文件；

wp_set_wpdb_vars()

# 执行在load.php中定义的该函数，设置数据库前缀等；

wp_start_object_cache()

# 执行在load.php中定义的该函数，启动WP缓存等；

require( ABSPATH . WPINC . '/default-filters.php' );

# 加载default-filters.php文件，WP所有的大多数过滤钩子和动作钩子都是通过本文件设置，即添加挂载函数；

// Initialize multisite if enabled.
if ( is_multisite() ) {
	require( ABSPATH . WPINC . '/class-wp-site-query.php' );
	require( ABSPATH . WPINC . '/class-wp-network-query.php' );
	require( ABSPATH . WPINC . '/ms-blogs.php' );
	require( ABSPATH . WPINC . '/ms-settings.php' );
} elseif ( ! defined( 'MULTISITE' ) ) {
	define( 'MULTISITE', false );
}

register_shutdown_function( 'shutdown_action_hook' );

// Stop most of WordPress from being loaded if we just want the basics.
if ( SHORTINIT )
	return false;

require_once( ABSPATH . WPINC . '/l10n.php' );

# 加载l10n.php文件，定义语言翻译API，无执行代码；

require_once( ABSPATH . WPINC . '/class-wp-locale.php' );

require_once( ABSPATH . WPINC .'/class-wp-locale-switcher.php' );

// Run the installer if WordPress is not installed.
wp_not_installed();

require( ABSPATH . WPINC . '/class-wp-walker.php' );

require( ABSPATH . WPINC . '/class-wp-ajax-response.php' );

require( ABSPATH . WPINC . '/formatting.php' );

# 加载formatting.php文件，定义WP大多数用于格式化输出的函数，无执行代码；

require( ABSPATH . WPINC . '/capabilities.php' );

# 加载capabilities.php文件，定义WP角色和权限相当类和函数，无执行代码；

require( ABSPATH . WPINC . '/class-wp-roles.php' );

require( ABSPATH . WPINC . '/class-wp-role.php' );

require( ABSPATH . WPINC . '/class-wp-user.php' );

require( ABSPATH . WPINC . '/class-wp-query.php' );

require( ABSPATH . WPINC . '/query.php' );

# 加载query.php文件，定义WP查询请求API（如文章或页面请求内容、评论等），无执行代码，它定义一个类WP_Query，并定义可以将其实例化的函数query_posts，将其实例化为对象$wp_query，同时定义了很多可以从该对象中取值的API；该类功能强大，它可以用来判断当前页面类型（如文章页、标签页、作者页、首页等）、获取文章内容、获取最新的N篇文章等；该类相对较为重要，该类部分重要属性和方法详解见“WordPress初始化核心文件query.php源码分析”；

require( ABSPATH . WPINC . '/date.php' );

require( ABSPATH . WPINC . '/theme.php' );

# 加载theme.php文件，定义主题、模板和样式表相关函数，无执行代码，如获取主题及样式表、获取主题路径及URL、更换主题、主题样式展示等；

require( ABSPATH . WPINC . '/class-wp-theme.php' );

# 加载class-wp-theme.php文件，定义主题类WP_Theme，无执行代码，该类主要功能如获取样式表、获取模板路径、获取样式表URL等；

require( ABSPATH . WPINC . '/template.php' );

# 定义各类型页面模板API，无执行代码，该文件大多函数名形如get_XXX_template()，该函数会将XXX作为参数传入get_query_template()并返回相应值，这些模板如：404页面、TAG页面、首页、时间分类页等；

require( ABSPATH . WPINC . '/user.php' );

# 定义跟用户相当API及类，如账户密码cookie用户文章数、删除用户等；

require( ABSPATH . WPINC . '/class-wp-user-query.php' );

require( ABSPATH . WPINC . '/class-wp-session-tokens.php' );

require( ABSPATH . WPINC .'/class-wp-user-meta-session-tokens.php' );

require( ABSPATH . WPINC . '/meta.php' );

# 定义一组元数据 API，这些 API 用于获取和操作 WP 中各种对象类型的元数据（Title、Keywords、Description），一个对象的元数据简单的表示为键值对，对象可能包含多个元数据实体，他们有相同的键但不同的值，无执行代码；

require( ABSPATH . WPINC . '/class-wp-meta-query.php' );

require( ABSPATH . WPINC . '/class-wp-metadata-lazyloader.php' );

require( ABSPATH . WPINC . '/general-template.php' );

# 定义常用的模板标签，它们可以在模板中任意使用，无可执行代码，如get_header()、get_footer()、get_sidebar()、wp_title()等；

require( ABSPATH . WPINC . '/link-template.php' );

# 定义大多数WP使用中可能用到的URL，如文章链接、按时间分类页面链接、所有feed相当链接、所有edit时链接、下篇文章链接等等，无可执行代码；

require( ABSPATH . WPINC . '/author-template.php' );

# 定义一组模板中处理作者的函数，如获取文章作者、最后修改人、作者链接、作者所有文章等，无可执行代码；

require( ABSPATH . WPINC . '/post.php' );

# 定义可修改文章信息等的API，无可执行代码，如文章状态、增加文章meta、更换文章meta、删除文章等；

require( ABSPATH . WPINC . '/class-walker-page.php' );

require( ABSPATH . WPINC . '/class-walker-page-dropdown.php' );

require( ABSPATH . WPINC . '/class-wp-post-type.php' );

require( ABSPATH . WPINC . '/class-wp-post.php' );

require( ABSPATH . WPINC . '/post-template.php' );

# 定义一组跟文章缩略图相关API；

require( ABSPATH . WPINC . '/revision.php' );

require( ABSPATH . WPINC . '/post-formats.php' );

require( ABSPATH . WPINC . '/post-thumbnail-template.php' );

require( ABSPATH . WPINC . '/category.php' );

# 定义一组 WP 的目录标签API，无可执行代码，如获取目录ID、获取目录名、获取标签；

require( ABSPATH . WPINC . '/category-template.php' );

# 定义一组目录的模板标签和 API，无可执行代码，如处理目录排序、标签云、目录结构等；

require( ABSPATH . WPINC . '/comment.php' );

# 定义一组跟WP的评论信息相关的 API，无可执行代码，如检查评论必选项、获取评论内容及状态、获取评论总数等；

require( ABSPATH . WPINC . '/comment-template.php' );

# 定义一组评论的模板标签，旨在 Loop 中有用，无可执行代码，如获取评论者及其评论内容、获取评论人email、URL、IP等；

require( ABSPATH . WPINC . '/rewrite.php' );

# 定义一组WP的URL重写的API和类，无执行代码，如标签页URL自定义、首页URL、目录页URL等；

require( ABSPATH . WPINC . '/feed.php' );

# 定义一组 WP 的 Feed API，其中大部分只在 Loop 中使用，无可执行代码；

require( ABSPATH . WPINC . '/bookmark.php' );

# 定义一组 WP 的友情链接/书签 API，无可执行代码；

require( ABSPATH . WPINC . '/cron.php' );

# 定义一组 WP 的 CRON(定时任务) API，用于进行事件调度，无可执行代码；

.. code-block:: php

	if ( ! defined( 'WP_INSTALLING' ) || 'wp-activate.php' === $pagenow ) {
		if ( TEMPLATEPATH !== STYLESHEETPATH && file_exists( STYLESHEETPATH . '/functions.php' ) )
			include( STYLESHEETPATH . '/functions.php' );
		if ( file_exists( TEMPLATEPATH . '/functions.php' ) )
			include( TEMPLATEPATH . '/functions.php' );
	}

# 该段代码用于加载当前主题下functions.php文件，这里我们就解决了最初的问题，“到底WordPress主题下的functions.php文件在什么时候执行”，从代码顺序来看，主题环境加载是在WordPress所有核心文件加载完且初始内容准备完毕后加载；

https://codex.wordpress.org/The_Loop
https://codex.wordpress.org/The_Loop_in_Action
http://www.ecdoer.com/post/wordpress-wp-setting-php.html
https://codex.wordpress.org/Query_Overview
http://humanshell.net/2011/08/wordpress-initialization/