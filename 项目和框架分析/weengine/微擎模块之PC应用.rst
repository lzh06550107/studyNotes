********
微擎PC应用
********

微擎webapp是解决PC端前台而设置的，可以在 ``webapp.php`` 文件 ``doPageXXX`` 方法中定义PC版应用前台功能；在 ``site.php`` 文件 ``doWebXXX`` 方法中定义后端管理功能。

规范及约定
=========

- ``Rcdonkey_signup`` 为模块标识，类名的定义遵循 ``模块标识+ModuleWebapp`` 规则；
- 此类必须继承 ``WeModuleWebapp`` 类；
- 所有对外公布的接口函数，必须是以 ``doPage`` 开头；

创建 ``webapp.php`` 文件，这个文件就是PC应用的后端文件。其结构大概如下：

.. code-block:: php

	/**
	 * 接龙报名模块小程序接口定义
	 *
	 * @author lzh
	 */
	defined('IN_IA') or exit('Access Denied');
	class Rcdonkey_signupModuleWebapp extends WeModuleWebapp {
	    public function doPageIndex() {
	    }
	    public function doPageGoodsList() {
	        include $this->template('web/goods/list');
	    }
	}

请求地址解析
===========
PC浏览器通过如下接口来与后端进行交互。

``index.php?i=3&j=3&a=webapp&c=entry&eid=127``

- i：表示统一帐号id；
- j：表示子账号id；
- a：表示动作；
- c：表示控制器；
- eid：表示`` modules_bindings`` 表中入口记录id；

