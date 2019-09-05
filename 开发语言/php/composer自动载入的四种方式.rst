******************
composer 自动载入的四种方式
******************

对于第三方包的自动加载， ``Composer`` 提供了四种方式的支持，分别是 ``PSR-0`` 和 ``PSR-4`` 的自动加载，生成 ``class-map`` ，和直接包含 ``files`` 的方式。

首先引入 ``autoload.php`` ，在主文件 ``index.php`` 中。

.. code-block:: shell

    require 'vendor/autoload.php';

在 ``composer.json`` 文件中添加以下代码块：

.. code-block:: json

	"autoload": {
	    "psr-4": {
	        "src\\darren\\": "src/",
	        "project\\darren\\": "project"
	        },
	    "files": ["common/Darren.php", "common/Since.php"],
	    "classmamp": [lib]
	}


PSR-4（推荐）
============
在 ``composer.json`` 里是这样进行配置的：

.. code-block:: json

	{
	  "autoload": {
	    "psr-4": {
	      "Foo\\": "src/"
	    }
	  }
	}

执行 ``composer install`` 更新自动加载。照 ``PSR-4`` 的规则，当在 ``index.php`` 中试图 ``new Foo\Bar\Baz`` 这个 ``class`` 时， ``composer`` 会自动去寻找 "src/Bar/Baz.php" 这个文件，如果它存在则进行加载。

PSR-0（不推荐）
==============
在 ``composer.json`` 里是这样进行配置的：

.. code-block:: json

	{
	  "autoload": {
	    "psr-0": {
	      "Foo\\": "src/"
	    }
	  }
	}

执行 ``composer install`` 更新自动加载。注意，照 ``PSR-0`` 的规则，当在 ``index.php`` 中试图 ``new Foo\Bar\Baz`` 这个 ``class`` 时， ``composer`` 会去寻找 "src/Foo/Bar/Baz.php" 这个文件，如果它存在则进行加载。

.. note:: 另外注意 ``PSR-4`` 和 ``PSR-0`` 的配置里， ``Foo\`` 结尾的命名空间分隔符必须加上并且进行转义，以防出现 ``Foo`` 匹配到了 ``FooBar`` 这样的意外发生。

Class-map方式
=============

.. code-block:: json

	{
	  "autoload": {
	    "classmap": ["src/", "lib/", "Something.php"]
	  }
	}

执行 ``composer install`` 更新自动加载。 ``composer`` 会扫描指定目录下以 ``.php`` 或 ``.inc`` 结尾的文件中的 ``class`` ，生成 ``class`` 到指定 ``file path`` 的映射，并加入新生成的 ``vendor/composer/autoload_classmap.php`` 文件中。 例如 ``src/`` 下有一个 ``BaseController`` 类，那么在 ``autoload_classmap.php`` 文件中，就会生成这样的配置：

.. code-block:: shell

    'BaseController' => $baseDir . '/src/BaseController.php'

实例化类的方式这里有两种不同的情况。

- 如果加载的文件有命名空间，直接按命名空间实例化。
- 如果没有命名空间，直接按类名实例化。

Files方式
=========

.. code-block:: json

	{
	  "autoload": {
	    "files": ["src/MyLibrary/functions.php"]
	  }
	}

执行 ``composer install`` 更新自动加载。 ``Files`` 方式，就是手动指定供直接加载的文件。比如说我们有一系列全局的 ``helper functions`` ，可以放到一个 ``helper`` 文件里然后直接进行加载，也就是说，当你用 ``require 'vendor/autoload.php';`` 加载自动加载类时自动将 ``files`` 里的文件加载进来了，你直接使用就行了。

composer install 和 update 的区别
=================================

composer install
----------------

``install`` 命令从当前目录读取 ``composer.json`` 文件，处理了依赖关系，并把其安装到 ``vendor`` 目录下。

.. code-block:: shell

    php composer.phar install

如果当前目录下存在 ``composer.lock`` 文件，它会从此文件读取依赖版本，而不是根据 ``composer.json`` 文件去获取依赖。这确保了该库的每个使用者都能得到相同的依赖版本。

如果没有 ``composer.lock`` 文件， ``composer`` 将在处理完依赖关系后创建它。

composer update
----------------

为了获取依赖的最新版本，并且升级 ``composer.lock`` 文件，你应该使用 ``update`` 命令。

.. code-block:: shell

    php composer.phar update

这将解决项目的所有依赖，并将确切的版本号写入 ``composer.lock`` 。


