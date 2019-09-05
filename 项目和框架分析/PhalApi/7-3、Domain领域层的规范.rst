************
Domain领域层的规范
************
``PhalApi`` 框架主要的分层模式是 ``ADM`` ，其中最为重要的是 ``Domain`` 领域业务层，它承载着最核心的领域业务的实现，特别对于大型项目，所面临的业务复杂性更高。软件开发的本质就是要尽量降低复杂度，因此有必要在此再对 ``Domain`` 领域业务层进行说明与探讨。系统中的复杂度又往往集中在各对象动态运行时的依赖关系，如果处理不当，就会引起混乱，诱发代码异味。在领域业务中，对这些对象一个很好的划分就是分为实体、值对象和服务。理解这三个概念，对于项目开发是大有禆益的。

实体
====
实体是最为常见的对象，它们主要特点是有存储在数据仓库的数据属性，以及基于这些属性的行为操作，是有状态的。实体与实体之间的区别，不仅表现在数据属性上的不同，还可以表现在唯一标识上的不同。数据仓库的实现机制不限，可以是常用的数据库，也可以是 ``NoSQL`` ，或者是简单的文件。实体与数据仓库是需要进行双向交互的，不仅需要从数据仓库中获取数据，还需要将添加或更新、删除的数据同步到数据仓库。

继续以用户登录为例， ``Domain_User`` 领域业务类可以说是一个实体，因为它需要依赖数据仓库获取用户数据，进而与客户端提供的参数进行比较。以下是实现代码片段：

.. code-block:: php

	// Forever$ vim ./Demo/Domain/User.php
	<?php
	class Domain_User {
	    public function login($user, $pass) {
	        $servicePass = new Domain_Password();
	        $encryptPass = $servicePass->encrypt($pass);

	        $model = new Model_User();
	        return $model->login($user, $encryptPass);
	    }
	}

在后半部分，通过 ``Model`` 层，根据用户名与加密后的密码获取相应的用户 ``ID`` 。

实体类的对象通常是上层 ``Api`` 层的访问入口，在其内部，又会涉及到服务对象与值对象的协同工作。例如上面登录操作中就使用了密码加密服务类 ``Domain_Password`` 。接下来，让我们来了解一下领域业务中的服务。

服务
====
实体是有状态的，而服务是无状态的。服务是一组通用的功能集合，实例化一个服务对象后，便可以使用它进行重复的操作。如上面所说的密码加密服务类，一旦实例化后，它可以对各种密码进行加密操作，并且前面的加密与后面的加密是无任何联系的。

我们先来看下对于 ``Domain_Password`` 的最终使用，再来看下它内部的实现。实例化 ``Domain_Password`` 类的对象很简单，因为它不需要任何参数，然后便可多次进行密码的加密。例如：

.. code-block:: php

	<?php
	$servicePass = new Domain_Password();

	// 对密码“123456”进行加密
	$pass1 = $servicePass->encrypt('123456');

	// 对密码“i_am_18”进行加密
	$pass2 = $servicePass->encrypt('i_am_18');

	// 对密码“who_am_i”进行加密
	$pass3 = $servicePass->encrypt('who_am_i');

服务是不依赖于数据存储的，但它通常会依赖于一些公共的基础设施。这些基础设施可以是 ``PhalApi`` 框架的基础模块，也可以是扩展类库，还可以是自己封装的与业务无关的组件。例如这里就使用了加密技术。因此就不难理解此加密服务的实现细节了。

.. code-block:: php

	// Forever$ vim ./Demo/Domain/Password.php
	<?php
	class Domain_Password {
	    const CRYPT_KEY = '06633f94d3';
	    protected $mcrypt;

	    public function __construct() {
	        $iv = DI()->config->get('sys.crypt.mcrypt_iv');
	        $this->mcrypt = new PhalApi_Crypt_MultiMcrypt($iv);
	    }

	    public function encrypt($pass) {
	        return $this->mcrypt->encrypt($pass, self::CRYPT_KEY);
	    }

	    public function decrypt($encryptPass) {
	        return $this->mcrypt->decrypt($encryptPass, self::CRYPT_KEY);
	    }
	}

虽然服务不需要持久化数据，但会有各自的配置选项，以满足不同场景下的使用。这也是为什么我们在使用扩展类库时需要进行配置的原因。又例如这里，配置了 ``8`` 位的加密向量 ``sys.crypt.mcrypt_iv`` ，此配置位于 ``./Config/sys.php`` 配置文件中，如下所示：

.. code-block:: php

    /**
     * 加密
     */
    'crypt' => array(
        'mcrypt_iv' => '12345678',      //8位
    ),

服务可以是与业务有关的，也可以是与业务无关的。上面的密码加密就是与业务有关的，因为它是专门针对密码进行的加解密操作。而 ``PHPMailer`` 邮件扩展则是与业务无关的，因为它可以用来发送任何类型的业务邮件。当然，也可以在业务无关的服务上，再封装业务有关的服务，以便上面应用更快地重用，并且保证业务的一致性。

值对象
======
在领域业务中，还有一种不可或缺的对象是值对象。尤其在大型系统中，值对象可以说是一种简化设计，减少缺陷的一种有效途径。值对象一个很重要的物质是不可变性，即一旦创建后就不能再修改。所以，在实现值对象时，可以不提供任何 ``setter`` 方法，也不提供任何修改成员属性的方法，只允许在构造函数时进行传参初始化。

值对象可以是基本的数据类型，如整数、布尔值、浮点数，也可以是自定义类类型。关于值对象，鉴于前面已有介绍说明，这里不再赘述。

