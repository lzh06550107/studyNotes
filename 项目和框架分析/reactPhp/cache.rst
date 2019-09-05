*****
cache
*****

介绍
====

``Cache`` 是 ``ReactPHP`` 异步，基于 ``Promise`` 的缓存接口。

缓存组件提供了基于 ``Promise`` 的 ``CacheInterface`` 和内存 ``ArrayCache`` 。允许使用者使用接口时有类型提示，并且第三方可以提供替代实现。

使用
====

CacheInterface
---------------
``CacheInterface`` 描述了这个组件主要的接口。允许使用者使用接口时有类型提示，并且第三方可以提供替代实现。

get()
^^^^^
``get(string $key, mixed $default = null): PromiseInterface`` 此方法从 ``Cache`` 中检索键所对应的缓存值。

在操作成功时此方法会返回键所对应的缓存值，在缓存值不存在或者出现错误时则返回设置的默认值 ``$default`` 。同样的，过期的缓存值（一旦 ``time-to-live`` 过期）会被视为 ``Cache`` 未命中（ ``cache miss`` ）。

.. code-block:: php

	$cache
	    ->get('foo')
	    ->then('var_dump');

本例会获取到键为 ``foo`` 的值并传给 ``var_dump`` 函数。你可以使用 ``promises`` 提供的任意功能。

set()
^^^^^
``set(string $key, mixed $value, ?float $ttl = null): PromiseInterface`` 此方法用于在 ``Cache`` 中存储缓存项。

在操作成功时次方法会返回 ``true`` ，或者在失败的时候返回 ``false`` 。如果 ``Cache`` 不是在本地存储，则在网络传输中会需要一定时间。

可选参数 ``$ttl`` 用来为缓存项设置 ``time-to-live``（有效期，单位为秒）。如果省略了此参数（或者设置为 ``null`` ），只要底层能实现永久存储，这个缓存项会一直存储在 ``Cache`` 中。尝试访问过期的缓存项会导致缓存未命中，另请参阅 ``get()`` 。

.. code-block:: php

    $cache->set('foo', 'bar', 60);

本例最终会把 ``bar`` 赋给 ``foo`` ，如果键已经存在，则它的值会被覆盖。

delete()
^^^^^^^^^
从缓存中删除一个缓存项

在操作成功时次方法会返回 ``true`` ，或者在失败的时候返回 ``false`` 。当 ``Cache`` 中不存在该键的缓存项时也会返回 ``true`` 。如果 ``Cache`` 不是在本地存储，则在网络传输中会需要一定时间。

.. code-block:: php

    $cache->delete('foo');

此示例最终会从 ``Cache`` 中删除键为 ``foo`` 的缓存项 。与 ``set()`` 一样，操作可能不会立即发生，但会返回一个 ``promise`` 以保证该项是否已从 ``Cache`` 中删除。

ArrayCache()
^^^^^^^^^^^^
``ArrayCache`` 提供了 ``CacheInterface`` 的内存实现。

.. code-block:: php

``ArrayCache`` 提供了 ``CacheInterface`` 的内存实现。

.. code-block:: php

	$cache = new ArrayCache();

	$cache->set('foo', 'bar');

它的构造函数接受一个可选的参数 ``int $limit`` 来限制存储在 ``LRU`` 缓存中的最大条目数。 如果向此实例中添加更多的条目，它将自动删除最近最少使用的条目（LRU）。

例如，此代码段将覆盖第一个值，并仅存储最后两个条目：

.. code-block:: php

	$cache = new ArrayCache(2);

	$cache->set('foo', '1');
	$cache->set('bar', '2');
	$cache->set('baz', '3');

常规用例
========
Fallback get （从后备源中 get）
-----------------------------
``Cache`` 的一个常见用例是尝试获取缓存值，如果找不到，则从后备数据源的原始数据中检索它。例如：

.. code-block:: php

	$cache
	    ->get('foo')
	    ->then(function ($result) {
	        if ($result === null) {
	            return getFooFromDb();
	        }

	        return $result;
	    })
	    ->then('var_dump');

首先尝试检索 ``foo`` 的值。 注册了一个回调函数，当结果值为 ``null`` 时，将调用 ``getFooFromDb`` 。 ``getFooFromDb`` 是一个函数（可以是任何 ``PHP Callable`` 类型），如果缓存中不存在该键，则会将调用该函数。

``getFooFromDb`` 可以通过返回一个从数据库（或任何其他数据源）获取实际值的 ``promise`` 来处理丢失的键。结果，该链将正确地返回，并可以在两种情况下提供值。

Fallback get and set （从后备源中 get & set）
--------------------------------------------
例如，为了在使用备源时扩展操作，通常需要在从数据源获取到值后在 ``Cache`` 中缓存该值。

.. code-block:: php

	$cache
	    ->get('foo')
	    ->then(function ($result) {
	        if ($result === null) {
	            return $this->getAndCacheFooFromDb();
	        }

	        return $result;
	    })
	    ->then('var_dump');

	public function getAndCacheFooFromDb()
	{
	    return $this->db
	        ->get('foo')
	        ->then(array($this, 'cacheFooFromDb'));
	}

	public function cacheFooFromDb($foo)
	{
	    $this->cache->set('foo', $foo);

	    return $foo;
	}

如果从数据库中获取值，通过链式操作，则可以轻松有条件地缓存该值。

安装
====
这将安装支持的最新版本：

.. code-block:: php

    $ composer require react/cache:^0.5.0

测试
====
要运行测试套件，首先需要 ``clone`` 此仓库，然后通过Composer安装所有的依赖项：

.. code-block:: php

    $ composer install

要运行测试套件，请切换到项目根目录并运行：

.. code-block:: php

    $ php vendor/bin/phpunit

