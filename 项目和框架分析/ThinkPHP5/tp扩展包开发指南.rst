**************
ThinkPHP扩展开发指南
**************

``ThinkPHP`` 扩展主要包括两种类型：第一种是依赖 ``ThinkPHP`` 的（某个版本），比如某个缓存驱动，某个中间件等等，仅用于 ``ThinkPHP`` 开发的项目；第二种是不依赖 ``ThinkPHP`` 的独立（功能）类库，这个涉及的范畴比较广，并且可以用于任何项目。

遵循规范
========
无论是哪一种类型的扩展，都应该使用 ``Composer`` 包的方式来开发和安装使用（ ``ThinkPHP5`` 的官方扩展全部是基于 ``Composer`` 安装的）。

如果还不清楚如何制作一个 ``Composer`` 包，可以参考这篇： `如何创建一个自己的 Composer 库 <https://juejin.im/entry/57d7c3d2a0bb9f0057f244fc>`_ 。

.. note:: 要注意的是，作为一个规范的 ``Composer`` 包，代码风格必须严格遵循 ``PSR-2`` 规范和使用 ``PSR-4`` 自动加载规范。

独立功能类库没有特别的要求，一般明确 ``PHP`` 的版本要求和其它依赖即可，并选择一个开源协议，然后自己做好测试和添加相关说明（readme.md）或者附上文档地址即可，所以不做特别的说明。

ThinkPHP专用扩展
================
如果是开发 ``ThinkPHP5`` 专属的扩展，最好的办法是参考官方的相关扩展进行，但务必注意，针对不同的 ``ThinkPHP`` 版本你需要不同的实现（通常可以采用分支的形式，并依赖不同的框架版本），主要包括：

缓存驱动
--------
以 ``ThinkPHP5.1`` 版本为例，如果要扩展一个 ``ThinkPHP`` 的缓存驱动，只需要继承系统的缓存驱动基础类 ``think\cache\Driver`` ，并实现下面的抽象方法。

.. code-block:: php

	/**
	 * 判断缓存是否存在
	 * @access public
	 * @param  string $name 缓存变量名
	 * @return bool
	 */
	abstract public function has($name);

	/**
	 * 读取缓存
	 * @access public
	 * @param  string $name 缓存变量名
	 * @param  mixed  $default 默认值
	 * @return mixed
	 */
	abstract public function get($name, $default = false);

	/**
	 * 写入缓存
	 * @access public
	 * @param  string    $name 缓存变量名
	 * @param  mixed     $value  存储数据
	 * @param  int       $expire  有效时间 0为永久
	 * @return boolean
	 */
	abstract public function set($name, $value, $expire = null);

	/**
	 * 自增缓存（针对数值缓存）
	 * @access public
	 * @param  string    $name 缓存变量名
	 * @param  int       $step 步长
	 * @return false|int
	 */
	abstract public function inc($name, $step = 1);

	/**
	 * 自减缓存（针对数值缓存）
	 * @access public
	 * @param  string    $name 缓存变量名
	 * @param  int       $step 步长
	 * @return false|int
	 */
	abstract public function dec($name, $step = 1);

	/**
	 * 删除缓存
	 * @access public
	 * @param  string $name 缓存变量名
	 * @return boolean
	 */
	abstract public function rm($name);

	/**
	 * 清除缓存
	 * @access public
	 * @param  string $tag 标签名
	 * @return boolean
	 */
	abstract public function clear($tag = null);

数据库驱动
---------
要支持某个特殊的数据库（包括 ``NoSQL`` 数据库），可以参考官方的 ``Oracle`` 驱动扩展和 ``MongoDb`` 驱动扩展。

- ``Oracle`` 扩展 ``topthink/think-oracle``
- ``MongoDb`` 扩展 ``topthink/think-mongo``

对于 ``PDO`` 支持的数据库驱动， ``Oracle`` 扩展就是一个典型的例子，你通常只需要实现数据库连接驱动（继承系统的 ``think\db\Connection`` 类）和数据库解析驱动（继承系统的 ``think\db\Builder`` 类）即可。

而对于非 ``PDO`` 支持的数据库， ``Mongo`` 扩展就是一个典型例子，通常你还需要额外实现一个数据库查询驱动类（继承 ``think\db\Query`` 类），并且这种情况下，你的数据库连接驱动和解析驱动可以无需继承系统的基类。

Session驱动
-----------
对于 ``Session`` 驱动其实很简单，你只需要基于 ``SessionHandlerInterface`` 接口实现即可，也就是包含如下方法实现。

.. code-block:: php

	abstract public close ( void ) : bool
	abstract public destroy ( string $session_id ) : bool
	abstract public gc ( int $maxlifetime ) : int
	abstract public open ( string $save_path , string $session_name ) : bool
	abstract public read ( string $session_id ) : string
	abstract public write ( string $session_id , string $session_data ) : bool

日志驱动
--------
``ThinkPHP`` 的日志驱动，其实是一个独立的类库，而且只需要实现一个 ``save`` 方法，可以参考官方的 ``SeasLog`` 驱动实现。

.. code-block:: php

	/**
	 * 日志写入接口
	 * @access public
	 * @param  array    $log    日志信息
	 * @param  bool     $append 是否追加请求信息
	 * @return bool
	 */
	public function save(array $log = [], bool $append = false): bool

Trace驱动
---------
``ThinkPHP`` 的页面 ``Trace`` 驱动也是一个独立的类库，只需要实现一个 ``output`` 方法。

.. code-block:: php

	/**
	 * 调试输出接口
	 * @access public
	 * @param  Response  $response Response对象
	 * @param  array     $log 日志信息
	 * @return bool|string
	 */
	public function output(Response $response, array $log = [])

模板引擎扩展
-----------
``ThinkPHP`` 的模板引擎扩展也是一个独立的类库实现，主要需要实现的方法包括：

.. code-block:: php

	/**
	 * 检测是否存在模板文件
	 * @access public
	 * @param  string $template 模板文件或者模板规则
	 * @return bool
	 */
	abstract public function exists(string $template) : bool;

	/**
	 * 渲染模板文件
	 * @access public
	 * @param  string    $template 模板文件
	 * @param  array     $data 模板变量
	 * @return void
	 */
	abstract public function fetch(string $template, array $data = []) : void;

	/**
	 * 渲染模板内容
	 * @access public
	 * @param  string    $content 模板内容
	 * @param  array     $data 模板变量
	 * @return void
	 */
	abstract public function display(string $content, array $data = []) : void;

	/**
	 * 配置模板引擎
	 * @access private
	 * @param  string|array  $name 参数名
	 * @param  mixed         $value 参数值
	 * @return void
	 */
	abstract public function config($name, $value = null);

具体实现可以参考 ``think-twig`` 扩展和 ``think-blade`` 扩展。

命令行扩展
----------
``ThinkPHP`` 命令行扩展主要是用于扩展命令行的指令，这种方式的扩展可以和某个独立类库同时存在，提供某项功能的命令行指令配合。官方的队列扩展和 ``Swoole`` 扩展等都属于这个范畴，也是用途最广泛的扩展形式。

命令行扩展的指令注册可以使用下面的方式：

.. code-block:: php

	// 注册命令行指令
	\think\Console::addDefaultCommands([
	    // 指令名 => 对应的命令类
	    'swoole'        => '\\think\\swoole\\command\\Swoole',
	    'swoole:server' => '\\think\\swoole\\command\\Server',
	]);

命令类必须继承系统的 ``think\console\Command`` 类，可以参考掌握命令行的表格输出一文中的描述来完成命令行指令的实现。

分页显示驱动
-----------
``ThinkPHP`` 的分页默认使用的是 ``Bootstrap`` 驱动，你也可以自己扩展一个其它的驱动，以满足不同的分页显示需求。

分页驱动必须继承系统的 ``think\Paginator`` 类，具体可以参考内置的 ``think\paginator\driver\Bootstrap`` 类的实现。

行为扩展
--------
``ThinkPHP`` 的行为类可以不需要继承任何的类库，只需要实现一个 ``run`` 方法

.. code-block:: php

    public function run($params);

如果需要使用额外的参数，可以使用依赖注入，具体可以参考官方手册的钩子和行为章节内容。

中间件
------
``ThinkPHP5.1`` 高版本支持中间件，所以你可以通过中间件的方式来扩展功能。中间件无需继承任何的类，只需要实现一个 ``handle`` 方法，

.. code-block:: php

	public function handle($request, \Closure $next)

具体可以参考官方手册的中间件章节内容。

Response扩展
------------
系统内置的 ``Response`` 不一定满足所有的需求，你完全可以扩展一个 ``Response`` 类来实现特殊的需求。

``Response`` 扩展必须继承系统的 ``think\Response`` 类，并且必须实现 ``output`` 方法。

.. code-block:: php

	/**
	 * 处理数据
	 * @access protected
	 * @param  mixed $data 要处理的数据
	 * @return mixed
	 */
	protected function output($data)

关于配置文件
-----------
如果你的 ``ThinkPHP`` 扩展（通常是专属扩展）需要使用独立的配置文件，你可以在扩展中自带一个默认配置，并且在安装扩展的同时复制到框架的配置目录下。

实现方式是修改你的 ``composer.json`` 文件，添加如下定义项：

.. code-block:: php

	// type必须指定为think-extend
	"type": "think-extend",

	"extra": {
	  "think-config": {
	    // 配置文件名 : 默认的配置文件
	    "swoole": "src/config/swoole.php",
	  }
	}

这里是通过 ``topthink/think-installer`` 来安装。

tp相关资源包汇总 https://blog.thinkphp.cn/913360