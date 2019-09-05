*********
Api接口层的规范
*********

一个接口服务，一个文件
====================
对于 ``Api`` 接口层，一种极端做法是，把全部的接口服务都放在同一个文件。显然，这会造就庞然大物，也不会有人这么做（如果真的有，请记得把源代码分享我膜拜一下）。而另一种极端做法是，对于每一个接口服务，都单独放在一个文件中。这样的话，不用担心在修改其他接口服务时，哪怕是在同一个接口类中的不同方法，也不用再担心会影响其他接口服务。

以常见的用户模块的接口服务为例，登录接口层源代码单独放置在 ``Api/User/Login.php`` 这一文件里。

.. code-block:: php

	// Forever$ vim ./Demo/Api/User/Login.php
	<?php
	class Api_User_Login extends PhalApi_Api {
	}

对于用户注册接口服务，则单独放置在 ``Api/User/Register.php`` 这一文件里。

.. code-block:: php

	// Forever$ vim ./Demo/Api/User/Register.php
	<?php
	class Api_User_Register extends PhalApi_Api {
	}

对于用户登录态检测接口服务，则单独放置在 ``Api/User/Check.php`` 这一文件里。

.. code-block:: php

	// Forever$ vim ./Demo/Api/User/Check.php
	<?php
	class Api_User_Check extends PhalApi_Api {
	}

其他接口服务，依此类推。就上面用户模块的三个接口服务而言，对应的文件如下：

.. code-block:: shell

	Forever$ tree ./Demo/Api/
	./Demo/Api/
	└── User
	    ├── Check.php
	    ├── Login.php
	    └── Register.php

更简单请求的形式
===============
如果按照每个接口服务一个文件划分后，接着就会引发一个新的问题：如何为接口服务的类方法命名？为了减轻后端开发人员命名的压力，同时保持高度一致性，可以统一使用相同的类方法名。此方法名应该是简短、有活力、贴切的，例如统一使用 ``go()`` 方法名，那么对于上面的用户模块，那三个接口服务的源代码就会变成这样：

.. code-block:: php

	// Forever$ vim ./Demo/Api/User/Login.php
	class Api_User_Login extends PhalApi_Api {
	    public function go() {
	    }
	}

	// Forever$ vim ./Demo/Api/User/Register.php
	class Api_User_Register extends PhalApi_Api {
	    public function go() {
	    }
	}

	// Forever$ vim ./Demo/Api/User/Check.php
	class Api_User_Check extends PhalApi_Api {
	    public function go() {
	    }
	}

那么，便可以得到默认请求接口服务的 ``URI`` ，但明显可以看到在接口服务名称 ``service`` 参数中，每次请求时都需要重复加上 ``.Go`` 这一后缀，不仅影响美观，而且显得冗余。解决的方法很简单，可以使用前面所学的知识，定制我们自己专属的 ``service`` 格式。同时，结合 ``Nginx`` 的 ``Rewrite`` 规则，可以定制一种更简单的请求方式。先来看下最终的请求效果。

表7-1 请求用户模块接口服务的URI

+----------+---------------------------------+---------------------+
| 接口服务 | 默认的URI                       | 定制后更精简的URI   |
+==========+=================================+=====================+
| 用户登录 | /demo/?service=User_Login.Go    | /demo/user_login    |
+----------+---------------------------------+---------------------+
| 用户注册 | /demo/?service=User_Register.Go | /demo/user_register |
+----------+---------------------------------+---------------------+
| 会话检测 | /demo/?service=User_Check.Go    | /demo/user_check    |
+----------+---------------------------------+---------------------+

可以看到，定制后请求接口服务的 ``URI`` 更为精简，并且也更符合 ``URI`` 的格式，即全部使用小写字母，并用下划线分割。

要达到这样的效果，需要结合 ``Nginx`` 的 ``Rewrite`` 规则和 ``service`` 格式的定制。首先，对于 ``Nginx`` ，需要添加下面这样的 ``Rewrite`` 规则：

.. code-block:: shell

    if (!-e $request_filename) {
        rewrite ^/demo/(.*) /demo/?service=$1;
    }

配置后，记得需要重启 ``Nginx`` 。

接下来，是对 ``service`` 格式的定制。这里需要继承请求量 ``PhalApi_Request`` ，然后重写 ``PhalApi_Request::getService()`` 方法，实现定制化的工作。代码实现如下：

.. code-block:: php

	// Forever$ vim ./Demo/Common/Request.php
	<?php
	class Common_Request extends PhalApi_Request {
	    public function getService() {
	        $service = parent::getService();

	        // 兼容默认格式
	        if (strpos($service, '.')) {
	            return $service;
	        }

	        // 定制后的格式：大写转换 ＋ 后缀
	        $className = preg_replace("/(?:^|_)([a-z])/e", "strtoupper('\\0')", $service);
	        $newService = $className . '.Go';

	        return $newService;
	    }
	}

实现好扩展的子类后，别忘了要在入口文件合适的地方进行注册。如下所示：

.. code-block:: php

	// Forever$ vim ./Public/demo/index.php
	DI()->loader->addDirs(array('Demo'));

	DI()->request = 'Common_Request';

此时，在浏览器使用新定制后的 ``service`` 格式访问，便能达到与默认形式同样的访问效果。


参数规则配置
============
大部分接口服务都会需要接口参数，在一个接口服务，一个文件的情况下，对于参数规则的配置，主要区别在于代码排版方面。不同于之前把全部配置写在一行的做法，在这里更倾向于将配置写成多行，从而提供更统一、更整洁的写法。例如上面登录接口服务 ``User_Login.Go`` 中所需要的登录账号与登录密码这两个参数，写法如下：

.. code-block:: php

	class Api_User_Login extends PhalApi_Api {
	    public function getRules() {
	        return array(
	            'go' => array(
	                'user' => array(
	                    'name' => 'user',
	                    'require' => true,
	                    'min' => '1',
	                    'desc' => '登录账号',
	                ),
	                'pass' => array(
	                    'name' => 'pass',
	                    'require' => true,
	                    'min' => '6',
	                    'desc' => '登录密码',
	                ),
	            ),
	        );
	    }
	    ... ...

并且，对于各个参数规则，通用配置项的顺序，从上到下，分别是：

- 参数名称 name
- 是否必须 require
- 默认值 default
- 最小值 min
- 最大值 max
- 不同类型参数的扩展配置项，如：regex，range，format，ext等
- 数据源 source
- 说明信息 desc


接口实现与返回规范
=================
在实现 ``Api`` 接口层时，通常来说，分为以下这些步骤：

1. 定义返回结果的顶级字段结构
2. 初始化Domain实例并调用
3. 将结果依次进行赋值
4. 返回结果

例如，在实现用户登录时，其代码片段为：

.. code-block:: php

	class Api_User_Login extends PhalApi_Api {
	    public function go() {
	        $rs = array('code' => 1, 'user_id' => 0, 'token' => '', 'tips' => '');

	        $domain = new Domain_User();
	        $userId = $domain->login($this->user, $this->pass);

	        if ($userId <= 0) {
	            $rs['tips'] = '登录失败，用户名或密码错误！';
	            return $rs;
	        }

	        $token = DI()->userLite->generateSession($userId);

	        $rs['code'] = 0;
	        $rs['user_id'] = $userId;
	        $rs['token'] = $token;

	        return $rs;
	    }
	}

为了方便开发人员明确所返回的结构字段，在函数的第一行可定义结果的顶级字段，并使用默认值进行填充。结合上面的代码，可以看到，这里定义了返回结果的字段中有操作码、用户ID、登录凭证和提示信息。

.. code-block:: php

    $rs = array('code' => 1, 'user_id' => 0, 'token' => '', 'tips' => '');

然后初始化了 ``Domain_User`` 领域业务类的实例，并传入登录账号和登录密码尝试进行登录。如果登录失败，则使用卫语句直接失败返回，并进行相应的提示。如果登录成功，则通过 ``User`` 扩展生成一个新的会话凭证。

.. code-block:: php

    $domain = new Domain_User();
    $userId = $domain->login($this->user, $this->pass);
    ... ...
    $token = DI()->userLite->generateSession($userId);

调用领域业务类的实例完成业务功能后，接下来需要把获得的结果以及客户端需要的数据赋值给返回结果变量中。并且，最初定义结果的字段顺序，与实现过程中的获取顺序，以及最后赋值的顺序应该是保持一致的。如这里，依次是赋值了操作码、用户ID和登录凭证。对于不需要进行赋值的结果字段，则可以忽略路过，如这里的 ``tips`` 错误提示字段。

.. code-block:: php

    $rs['code'] = 0;
    $rs['user_id'] = $userId;
    $rs['token'] = $token;

最后一步，非常简单，直接返回结果即可。

虽然 ``PhalApi`` 框架在返回结果的最外层提供了状态码 ``ret`` 字段，但这个字段是技术框架层面的，并且错误时的最外层的 ``msg`` 错误信息字段也是针对技术开发人员，而非面向最终使用用户的。因此，有必要在业务返回结果中再定义自己的业务操作码，以及业务提示文案信息。例如，这里统一定义了业务操作码为 ``code`` ，为 ``0`` 时表示成功，非 ``0`` 时表示失败，并且不同的业务操作码对应不同的业务场景。而业务提示文案信息则用 ``tips`` 字段表示。

注释规范与自动生成文档
=====================
完成 ``Api`` 接口层代码的编写后，还有一个非常关键并且重要的事情，就是添加必要的注释，以便能自动生成对应的在线接口文档，提供给客户端开发同学查看使用。

简单地回顾一下，在接口类中的注释主要有以下三部分。第一部分是接口服务类的文档注释，对应在线接口列表文档的菜单说明。例如：

.. code-block:: php

	<?php
	/**
	 * 用户登录
	 * @author dogstar 20170622
	 */

	class Api_User_Login extends PhalApi_Api {
	        ... ...

第二部分是接口服务方法的注释，这部分包含接口服务的名称以及描述说明。例如：

.. code-block:: php

	class Api_User_Login extends PhalApi_Api {
	    /**
	     * 用户登录接口
	     * @desc 根据账号和密码进行登录，成功后返回凭证
	     */
	    public function go() {
	        ... ...

第三部分，是各返回结果字段的说明，以及错误异常码的说明。例如，添加返回结果字段说明的注释后：

.. code-block:: php

    /**
     * 用户登录接口
     * @desc 根据账号和密码进行登录，成功后返回凭证
     *
     * @return int      code    业务操作码，为0时表示成功，非0时表示登录失败
     * @return int      user_id 用户ID
     * @return string   token   登录凭证
     * @return string   tips    文案提示信息
     */
    public function go() {
        ... ...

补充完整注释后，便可以查看对应自动生成的在线文档。例如，上面登录接口服务的注释，生成的在线文档，效果如下：

.. image:: ./images/ch-7-user-login.jpg

图7-1 自动生成的登录接口服务在线文档

