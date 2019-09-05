***********
Model数据层的规范
***********
最后到了 ``Model`` 数据层，前面已经多次说到， ``PhalApi`` 框架中的 ``Model`` 层是广义上的数据源层，它的数据不仅仅局限于数据库。特别在大型项目中，其数据来源更为丰富多样，也更为复杂。

小结一下，对于 ``Model`` 层，其数据存储方式可分为以下四大类。

- 关系型数据库
- NoSQL
- 远程系统
- 文件

下面分别进行说明。

常见的关系型数据库对接
=====================
绝大部分的系统都会使用到关系型数据库，例如 MySQL 、 Microsoft SQL Servcer 、 Oracle 、 SQLite 、 PostgreSQL 等。在使用数据库构建大型系统时，有以下几个方面需要注意的。

对于数据量大的表，考虑分表存储
----------------------------
在我职业生涯中，不止一次看到公司因需要进行数据库迁移而大动干戈，小则在夜深人静，用户酣睡时进行迁移操作，大则在迁移前进行动员大会、模拟演练、发布停站升级公告，然后暂停各大系统之间的通讯并按计划严谨进行迁移，连每一个时间点哪个人需要做什么都要细无巨细进行统筹。需要迁移的一个很大原因是，受限于单台服务器的硬盘空间以及数据库本身的存储限制，不能满足业务系统日益膨胀的数据规模。

当然，我也曾非常有幸，在任职过的一家游戏公司中，了解并实践了水平分库分表这一思想。这不仅隔离了数据上的风险，还为可预见的海量数据打下了坚实的基础。因此，在设计之初就避免了日后迁移的维护成本。

需要注意的是，对于数据量大的表，应该考虑分表存储，而不是一定要使用分表存储。进行分表存储一个很大的缺点就是，难以进行数据库表之间的关联查询。如果考虑使用分表策略，那么此数据库表是否不需要关联查询，或者说可以通过其他方式弥补这一点？这些都是需要权衡的点。

在 ``PhalApi`` 进行分表存储时，其实现方式很简单，主要分别：配置、实现和使用这三步。

- 分表配置 首先，在数据库配置文件Config/dbs.php中进行分表的配置。
- 分表实现 其次，在Model实现子类中，重载获取表名方法实现分表的方式。
- 分表使用 最后，便可以使用分表存储了。

让我们通过 ``User`` 扩展类库中的用户会话分表，再来回顾一下这三个步骤。首先，分表配置主要是通过 ``map`` 选项来指定，如下所示，对于用户会话表 ``phalapi_user_session`` ，共分了 10 张表，分别是： phalapi_user_session_0 、 phalapi_user_session_1 、……、 phalapi_user_session_9 ，以及默认缺省表 ``phalapi_user_session`` 。

.. code-block:: php

	return array(
	    'tables' => array(
	        'user_session' => array(
	            'prefix' => 'phalapi_',
	            'key' => 'id',
	            'map' => array(
	                array('db' => 'db_demo'),
	                array('start' => 0, 'end' => 9, 'db' => 'db_demo'),
	            ),
	        ),
	    )
	);

其次，在 ``Model`` 实现子类中，重载 ``PhalApi_Model_NotORM::getTableName($id)`` 方法，指定分表名称。通常做法是，根据参照 ``ID`` 进行求余，然后将余数作为分表的后缀标识。

.. code-block:: php

	class Model_User_UserSession extends PhalApi_Model_NotORM {
	    const TABLE_NUM = 10;

	    protected function getTableName($id) {
	        $tableName = 'user_session';
	        if ($id !== null) {
	            $tableName .= '_' . ($id % self::TABLE_NUM);
	        }
	        return $tableName;
	    }
	    ... ...

最后，便可使用分表存储了。使用的方式又可以分为两大类，当使用全局的访问方式时，可直接手动指定分表名称，如：

.. code-block:: php

	// 使用phalapi_user_session_0分表
	$session0 = DI()->notorm->user_session_0;

	// 使用phalapi_user_session_1分表
	$session1 = DI()->notorm->user_session_1;

	// ... ....

	// 使用phalapi_user_session_9分表
	$session9 = DI()->notorm->user_session_9;

若是直接使用原生 ``SQL`` 拼接时，则还需要手动加上表前缀，例如：

.. code-block:: php

    $sql = 'SELECT count(*) as num FROM phalapi_user_session_0';

当使用局部的访问方式时，对于基本的已封装的 ``CURD`` 操作，可以通过参数 ``ID`` 指定对应的分表，例如：

.. code-block:: php

	$model = new Model_User_UserSession();

	// 查询phalapi_user_session_0分表
	$row = model->get(10);

	// 查询phalapi_user_session_1分表
	$row = model->get(1);

	// ... ....

	// 查询phalapi_user_session_9分表
	$rsow = model->get(9);

若是在 ``Model`` 实现子类内部时，则可以使用 ``PhalApi_Model_NotORM::getORM($id)`` 方法，传入参数 ``ID`` 并获取对应的分表实例。例如：

.. code-block:: php

	class Model_User_UserSession extends PhalApi_Model_NotORM {
	    public function doSth() {
	        // 获取phalapi_user_session_1分表实例
	        $session1 = $this->getORM(1);
	    }
	}

因此在使用时，可根据业务的需要，自行选择合适的方式。值得注意的是，当没有对应的分表时，将会回退使用默认的主表。

对于高并发的查询，使用缓存
------------------------
在大型系统中，应对高并发访问的一个行之有效的方案是使用高效缓存来提高系统的响应时间与高可用性。适合使用缓存的场景可以有：

- 幂等查询
- 允许一定的延时
- 计算成本高或耗时的操作结果

对于大型项目中复杂的数据查询， ``PhalApi`` 提供了一种经验作法。以下这个示例很好地演示了如何使用此经验做法。

首先，实现 ``PhalApi_ModelProxy`` 接口，并实现具体的源数据获取的过程，并指定缓存的键值和缓存时间。

.. code-block:: php

	class ModelProxy_UserBaseInfo extends PhalApi_ModelProxy {
	    protected function doGetData($query) {
	        $model = new Model_User();

	        return $model->getByUserId($query->id);
	    }

	    protected function getKey($query) {
	        return 'userbaseinfo_' . $query->id;
	    }

	    protected function getExpire($query) {
	        return 600;
	    }
	}

随后，通过查询值对象，获取相应的数据。

.. code-block:: php

	class Domain_User {
	    public function getBaseInfo($userId) {
	        $rs = array();
	        $query = new PhalApi_ModelQuery();
	        $query->id = $userId;
	        $modelProxy = new ModelProxy_UserBaseInfo();
	        $rs = $modelProxy->getData($query);
	        return $rs;
	    }
	}

这样，一来可以把每个数据单独实现在职责单一的代理类中，二来可以避免重复对缓存的公共操作。

对于耗时的更新和写入操作，移至后台异步计划任务
------------------------------------------
虽然查询类的数据结果可以缓存，但对于需要写入或者更新的数据，因为要存储到数据库，其做法又会有所差异。当对数据库进行大量的写入和更新操作时，将会导致数据库负载过高，严重时将会阻塞整个系统的响应。一个较好的做法是，先将等插入或更新的数据保存在高效缓存队列中，然后通过后台异步计划任务进行消费。计划任务，不仅适用于用户在与系统进行交互而产生的业务数据，同时也适用于系统基于已有数据而分析产生的二代数据。

``PhalApi`` 框架已经提供了 ``Task`` 计划任务扩展类库，当需要使用到后台计划任务时，这是一个非常值得尝试的类库。

NoSQL阵容
=========
随着非关系型数据库的兴起，出现了越来越多的 ``NoSQL`` ，形成了一个强大的阵容。常用的有键值对存储型，如 ``Memcache/Memcached`` ，和文档存储型，如 ``MongoDB`` 。

在 ``PhalApi`` 中，封装的缓存有： ``PhalApi_Cache_Memcache/PhalApi_Cache_Memcached`` ， ``PhalApi_Cache_Redis`` ，而扩展类库中则有 ``Redis`` 扩展，这些在开发项目都可结合业务的情况，选择使用。

如果需要更强大的 ``NoSQL`` 操作，则可考虑自行封装和延伸。


远程接口的调用
=============
当系统项目还很幼小时，很多事情都是堆积在一起实现的。你可以看到，用户的登录验证，数据库查询，核心基础业务数据的处理，推送功能等都是高度耦合的。随着项目的不断演变以及系统的不断壮大，这些功能会慢慢划分到职责更为明确的模块中，进而渐渐形成一个个相对独立的子系统。从以前直接调用一个类的方法，到调用另外一个子系统的某个接口。再到最后，这些子系统又会独立出来，部署成一个新的系统，肩负着更专注的使命。这时，接口之间的通讯，就从本地通讯变成了分布式的远程通讯。

对于这些接口系统，又可分为两大类，一类是内部系统的接口，另一类是外部第三方开放平台接口。

内部系统接口，是同一个公司或组织内部开发的系统接口，只允许内网内的其他授权系统进行调用。例如内部的推送系统，单点登录系统。第三方开放平台，则很好理解，国外已知的开放平台就有 ``Facebook`` 开放平台、 ``Twitter`` 开放平台 ``API`` 、亚马逊开发者平台等，国内的有微信开放平台、腾讯开放平台、新浪微博开放平台、优酷开放平台等。

不管是与内部系统还是外部第三方开放平台进行对接时，都需要进行远程调用。根据对接的系统，可以有多种调用方式。有些会提供不同语言的 ``SDK`` 开发包，有些则通过 ``curl`` 发起 ``HTTP/HTTPS`` 请求，或者通过 ``socket`` 进行通讯。不管何种方式，在调用远程接口时，应该做好超时设置、重试机制以及日志纪录，保证自身系统的稳定性和可用性。

一切皆文件
=========
在计算机中，对于持久化的存储，其实最终都可归为文件存储。Unix“一切皆文件”这一模型理念，可以说是非常值得借鉴与学习的。在我所参与开发的项目中，就有两个项目就是使用文件进行存储的。至今回想起来，依然让人印象颇为深刻。

其中一个项目是一个对局域网进行流量控制的产品，它有一个运行在特定硬件设备上的管理系统，而在设备之上则是 ``Linux`` 系统。受Unix“一切皆文件”模型的影响，自然而然，底层开发人员在存储数据时也是首选文件，并且只用文件。这不仅遵循了“一切皆文件”理念，而且也为内存和存储空间受限的硬件节省了不必要的开支。

例如，当需要存放各种类型的流量时，它对应的文件数据为：

.. code-block:: shell

	# $ vim /path/to/net_limit.dat
	video 2048 5120
	download 0  10240
	web 5120 5120

上面数据的意思是，第一列为类型名称，第二列为上行最大流量，第三列为下行最大流量，单位均为 ``KB`` 。例如第一行，在线视频类的上行最大流量为 2M ，下行最大流量为 5M ；第二行，下载上行最大流量为 0 ，下行最大流量为 10M ；第三行，网上冲浪上行和下行的最大流量都是 5M 。

当需要获取某个类型对应的流量限制时，则可以这样实现：

.. code-block:: php

	<?php
	class Model_NetLimit {
	    public function get($type) {
	        $rs = array();

	        $shell = 'cat /path/to/net_limit.dat | grep video';
	        $output = shell_exec($shell);

	        if (empty($output)) {
	            return $rs;
	        }

	        $arr = explode(' ', trim($output));
	        $rs['type'] = $arr[0];
	        $rs['up_limit'] = intval($arr[1]);
	        $rs['down_limit'] = intval($arr[2]);

	        return $rs;
	    }
	}

随后，若需要查询视频类的流量限制，则可以：

.. code-block:: php

	$model = new Model_NetLimit();
	$rs = $model->get('video');
	var_dump($rs);

结果会输出：

.. code-block:: shell

	array(3) {
	  'type' =>
	  string(5) "video"
	  'up_limit' =>
	  int(2048)
	  'down_limit' =>
	  int(5120)
	}

使用文件存储的方式简单而实用，结合 ``shell`` 操作，可快速进行相应的查询操作。
