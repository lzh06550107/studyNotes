*****
微擎App
*****

.. contents:: 目录
   :depth: 4


在继承 ``WeModulePhoneapp`` 类的类中 ``doPageXXX`` 方法定义 ``App`` 应用与后台交互功能；而在 ``site.php`` 文件 ``doWebXXX`` 方法中定义后端管理功能。

规范及约定
=========

- ``Rcdonkey_signup`` 为模块标识，类名的定义遵循 ``模块标识+ModulePhoneapp`` 规则；
- 此类必须继承 ``WeModulePhoneapp`` 类；
- 所有对外公布的接口函数，必须是以 ``doPage`` 开头；

下面文件就是 ``APP`` 应用的后端文件。其结构大概如下：


和 ``webapp.php`` 文件结构类似。在模块根目录创建 ``phoneapp.php`` 文件。然后在文件内部定义如下内容：

.. code-block:: php

	defined('IN_IA') or exit('Access Denied');
	class Rcdonkey_signupModulePhoneapp extends WeModulePhoneapp {

		// 向App提供的接口
		public function doWebDiaoMao() {
			// 代码
			// 这里返回json格式数据
			return $this->result(0, '修改成功', array('id' => $id));
		}
	}

请求地址解析
===========
App端通过如下接口来与后端进行交互。

``index.php?i=3&j=3&a=phoneapp&c=entry&eid=127``

- ``i``：表示统一帐号id；
- ``j``：表示子账号id；
- ``a``：表示动作；
- ``c``：表示控制器；
- ``eid``：表示 ``modules_bindings`` 表中入口记录id；














