***************
PEAR包命名规范下的自动加载
***************
首先， ``PhalApi`` 的自动加载机制很简单；其次， ``PhalApi`` 不强制只使用一种加载机制。有些框架，单单在类文件的自动加载这一块就弄得非常复杂，以致开发人员需要在使用这些框架的同时添加一些自己的类文件时，往往困难重重，甚至明明用引入了却又不见生效。 而在 ``PhalApi`` ，我们秉承的原则是：简单、统一、规范。

PEAR包命名规范
==============
``PEAR`` 包的类文件路径和类名映射非常简单，如下图：

.. image:: ./images/ch-3-pear-map.png

图3-2 来自Autoloading Standard的截图

出于简单性， ``PhalApi`` 暂时不使用命名空间，所以 ``namespace`` 这一块可省去。可以看出，这里的映射规则是：把类名中的下划线换成目录分割符，并在最后加上“.php”文件后缀，便可得到类对应的文件路径位置。

例如， ``Api_User`` 、 ``Domain_User`` 、 ``Model_User`` 这三个类，分别对应以下路径的文件。

.. code-block:: php

	.
	|-- Api
	|   `-- User.php
	|-- Domain
	|   `-- User.php
	|-- Model
	|   `-- User.php

再举一个稍微复杂的示例，如类 ``Api_Game_User_Equitment`` 对应的文件路径为： ``./Api/Game/User/Equitment.php`` 。需要注意的是，应该严格区分大小写，因为在 ``Linux`` 、 ``Mac`` 等操作系统，文件路径是区分大小写的。

下面是一些错误的示例。

表3-2 错误的类命名

+----------+----------------+------------------------------+
| 类名     | 类文件         | 错误原因                     |
+==========+================+==============================+
| Api_user | ./Api/User.php | 类名user小写，导致无法加载   |
+----------+----------------+------------------------------+
| Api_User | ./Api/user.php | 文件名user小写，导致无法加载 |
+----------+----------------+------------------------------+
| Api_User | ./Api_User.php | 类文件位置错误，导致无法加载 |
+----------+----------------+------------------------------+

挂靠式自动加载
==============
在准备好类和文件后，怎样才能让这些类被框架自动加载呢？这里提供的方式是： **挂靠式自动加载** 。熟悉 ``Linux`` 系统的同学可能很容易明白，还没接触到 ``Linux`` 的同学也是可以很快理解的。这里稍微说明一下。所谓的挂靠就是将项目内的子目录添加到自动加载器。例如我们在入口文件所看到的，添加商城新项目的项目目录，可以：

.. code-block:: php

    DI()->loader->addDirs('Shop');

当有多个目录时，可以传递一个目录数组。

.. code-block:: php

    DI()->loader->addDirs(array('Demo', 'Shop'));

需要注意的是，上面相对路径的都需要放置在应用项目的目录 ``API_ROOT`` 下面，暂时不能添加项目以外的目录。

通过 ``PhalApi_Loader::addDirs($dirs)`` 方式挂靠的路径，都是强制在目录 ``API_ROOT`` 下面。所传递的目录路径都应该是相对路径。在 ``Linux`` 系统上，下面的三种方式是等效的。

.. code-block:: php

	// 路径：API_ROOT/Demo
	DI()->loader->addDirs('Demo');

	// 路径：API_ROOT/./Demo
	DI()->loader->addDirs('./Demo');

	// 路径：API_ROOT/Demo
	DI()->loader->addDirs('/Demo');

如果需要挂靠的目录不在项目目录下，在 ``Linux`` 可以通过软链来解决。

对于单个文件的引入，可以通过 ``PhalApi_Loader::loadFile($filePath)`` 来引入，这里的文件路径可以是相对路径，也可以是绝对路径。注意以下两种写法的区别：

.. code-block:: php

	// 路径：API_ROOT/Demo/Tool.php
	DI()->loader->loadFile('Demo/Tool.php');

	// 路径：/path/to/Demo/Tool.php
	DI()->loader->loadFile('/path/to/Demo/Tool.php');

在添加代码目录后，便可实现该目录下类文件的自动加载。例如通过 ``DI()->loader->addDirs('Shop');`` 添加了 ``Shop`` 项目的源代码目录后，此 ``Shop`` 目录下符合 ``PEAR`` 命名规范的类，都能实现自动加载。

.. code-block:: shell

	$ tree ./Shop/
	./Shop/
	├── Api
	│   ├── Default.php
	│   ├── Goods.php
	│   └── Welcome.php
	├── Common
	│   ├── Crypt
	│   │   └── Base64.php
	│   ├── DB
	│   │   └── MSServer.php
	│   ├── Logger
	│   │   └── DB.php
	│   ├── Request
	│   │   └── WeiXinFilter.php
	│   └── Response
	│       └── XML.php
	├── Domain
	│   └── Goods.php
	├── Model
	│   └── Goods.php

上面是 ``Shop`` 项目下的部分类文件，当使用类 ``Api_Welcome`` 时，会自动加载 ``./Shop/Api/Welcome.php`` 文件；当使用类 ``Common_Response_XML`` 时，会自动加载 ``./Shop/Common/Response/XML.php`` 文件；当使用类 ``Domain_Goods`` 时，则会自动加载 ``./Shop/Domain/Goods.php`` 文件，以此类推。

对于面向过程中的函数，而非类，则可以使用 ``PhalApi_Loader::loadFile($filePath)`` 来手动引入。

初始化文件和入口文件的区别
========================
使用一个类，其过程可归结为三个步骤。

1. 实现该类
2. 自动加载该类
3. 在恰当的地方使用该类

当发现找不到某个类时，应该从这三个步骤分别排查原因。如果尚未实现该类，那么肯定是找不到的，这时可以补充实现。如果已经实现该类但还找不到，则应该检查类名或者类文件路径是否遵循 ``PEAR`` 命名规范。

例如，有一行这样的代码，却提示类 ``Domain_Goods`` 不存在。

.. code-block:: php

    $domain = new Domain_Goods();

导致这种情况的可能以下这几种。

- 未使用目录分割符而导致错误的类文件路径，如：

.. code-block:: php

	// $ vim ./Shop/Domain_Goods.php
	<?php
	class Domain_Goods{ }

- 因小写而导致错误的类文件路径，如：

.. code-block:: php

	// $ vim ./Shop/Domain/goods.php
	<?php
	class Domain_Goods{ }

- 拼写不完整而导致错误的类名，如：

.. code-block:: php

	// $ vim ./Shop/Domain/Goods.php
	<?php
	class Goods{ }

最后如果类名、类文件这些都正确，但仍然还是提示找不到类时，则应该核对第三步，是否在恰当的地方使用该类？恰当的地方是指在添加代码目录之后的调用位置。即在挂靠代码目录前不能使用此目录下的类，而应在挂靠之后使用。用代码示例来表示，则很好理解。例如：

.. code-block:: php

	// 错误：未挂靠Shop目录就使用
	DI()->response = new Common_Response_XML();

	DI()->loader->addDirs('Shop');

正确的用法是在挂靠 ``Shop`` 目录后才使用 ``Shop`` 目录里面的类，即：

.. code-block:: php

	// 正确：先挂靠Shop目录再使用
	DI()->loader->addDirs('Shop');

	DI()->response = new Common_Response_XML();

到这里，大家有没发现一些有趣的规律，或者一种似曾相识的感觉？上面的示例和背后的原理，大家应该很容易理解，当出现 ``Common_Response_XML`` 类未找到时也能很容易明白错误的原因。但当把这些简单的知识点，隐藏于复杂的上下文场景中时，就会容易导致一些令人感到费解的问题。还记得初始化文件 ``./Public/init.php`` 与项目入口文件 ``./Public/shop/index.php`` 吗？还记得为什么有些资源服务需要在初始化文件中注册，而有些则需要在入口文件中注册？为了唤起记忆，这里稍微回顾一下在这两个文件中分别注册的部分资源。

初始化文件中的注册：

.. code-block:: php

	$loader = new PhalApi_Loader(API_ROOT, 'Library');

	// 配置
	DI()->config = new PhalApi_Config_File(API_ROOT . '/Config');

	// 数据操作 - 基于NotORM
	DI()->notorm = new PhalApi_DB_NotORM(DI()->config->get('dbs'), DI()->debug);

Shop项目入口文件中的注册：

.. code-block:: php

	//装载你的接口
	DI()->loader->addDirs('Shop');

	// 微信签名验证服务
	DI()->filter = 'Common_Request_WeiXinFilter';

	// XML返回
	DI()->response = 'Common_Response_XML';

细心的读者可以发现，在初始化文件中，使用的都是框架已经的类，因为框架本身的类会默认全部能自动被加载。而对于 ``Shop`` 项目中的类，则需要在项目入口文件中使用，这是因为只有手动添加了 ``Shop`` 目录后，该目录下的类文件才能被自动加载。如果在初始化文件中，使用了 ``Shop`` 项目中的类，则会导致类找不到，因为那时尚未加载对应的 ``Shop`` 目录。尤其使用的是类名延迟加载方式时，会把问题隐藏得更深而难以排查。

这里的经验法则是，先挂靠再使用，在初始化文件中使用框架提供的类，在项目入口文件中使用项目实现的类。如果确实需要在初始化文件中使用项目实现的类，怎么办呢？解决方案可以有多种，例如可以把这些公共的代码放置在扩展类库下，因为扩展类库目录会在初始化文件中默认添加。另一种方案是在初始化文件一开始也把项目目录添加进去，但这样该目录下的全部类都会被自动加载。最后还可以把这些公共的类统一放置在某一个与项目目录无关的目录下，再在初始化文件中进行添加。

虽然，开发人员会对初始化文件和入口文件这两者感到困惑，增加了学习成本，并且每次都需要先挂靠代码目录才能使用其中的类，也带来了一定的复杂性。但其原理是非常简单的，即需要把要用到的目录添加进来，再使用。而之所以这样设计，是为了更好的扩展性，可以同时开发多个项目，多个模块，多套接口服务。
