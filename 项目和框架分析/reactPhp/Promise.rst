*******
Promise
*******

.. contents:: 目录
   :depth: 4


介绍
====
PHP `CommonJS Promises/A <http://wiki.commonjs.org/wiki/Promises/A>`_ 的轻量级实现。

它还提供了一些其他有用的与 ``promise`` 相关的概念，例如连接多个 ``promise`` 和映射以及聚合 ``promise`` 的集合。

如果您之前从未听说过 ``promises`` ，请先阅读此 `内容 <https://gist.github.com/3889970>`_ 。

概念
====

Deferred
---------
延迟( ``Deferred`` )代表可能尚未完成的计算或工作单元。通常（但不总是），该计算将是异步执行并在将来的某个时刻完成的事情。

Promise
--------
虽然延迟( ``Deferred`` )表示计算本身，但 ``Promise`` 表示该计算的结果。因此，每个延迟的承诺都充当其实际结果的占位符。

API
===

Deferred
---------
延迟表示待决的操作。它有单独的 ``promise`` 和 ``resolver`` 部分。

.. code-block:: php

	$deferred = new React\Promise\Deferred();

	$promise = $deferred->promise();

	$deferred->resolve(mixed $value = null);
	$deferred->reject(mixed $reason = null);
	$deferred->notify(mixed $update = null);

``promise`` 方法返回延迟对象的 ``promise`` 。

``resolve`` 和 ``reject`` 方法控制延迟的状态。

不推荐使用的 ``notify`` 方法用于进度通知。

``Deferred`` 的构造函数接受可选的 ``$canceller`` 参数。

Deferred::promise()
^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    $promise = $deferred->promise();

返回延迟的 ``promise`` ，您可以将其传递给其他人，同时为您自己保留修改其状态的权限。

Deferred::resolve()
^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    $deferred->resolve(mixed $value = null);

解析了由 ``promise()`` 返回的 ``promise`` 。通过调用 ``$onFulfilled``（ ``$promise->then()`` 注册） 上注册的方法并传入 ``$value`` 来通知所有消费者。

如果 ``$value`` 本身是一个 ``promise`` ，那么一旦它被解析， ``promise`` 将转换为该 ``promise`` 的状态。??

Deferred::reject()
^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    $deferred->reject(mixed $reason = null);

拒绝 ``promise()`` 返回的 ``promise`` ，表示延迟计算失败。通过调用 ``$onRejected`` (通过 ``$promise->then()`` 注册)上注册的方法并传入 ``$reason`` 来通知所有消费者。

如果 ``$reason`` 本身是一个 ``promise`` ，那么无论是履行还是拒绝承诺，承诺都将被拒绝。??

Deferred::notify()
^^^^^^^^^^^^^^^^^^^

.. note:: 在v2.6.0中不推荐使用：不推荐使用进度支持，不应再使用它。

.. code-block:: php

    $deferred->notify(mixed $update = null);

触发进度通知，以向消费者指示计算正在朝向其结果进行。通过调用 ``$onProgress`` (通过 ``$promise->then()`` 注册)上注册的方法并传入 ``$update`` 来通知所有消费者。

PromiseInterface
-----------------
``promise`` 接口为所有 ``promise`` 实现提供通用接口。

``promise`` 代表最终结果，即履行(成功)和关联值，或拒绝(失败)和相关原因。

一旦处于履行或拒绝状态，承诺就变得不可改变。它的状态和结果（或错误）都不能被修改。

实现类：

- Promise
- FulfilledPromise
- RejectedPromise
- LazyPromise

PromiseInterface::then()
^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    $transformedPromise = $promise->then(callable $onFulfilled = null, callable $onRejected = null, callable $onProgress = null);

通过将函数应用于 ``promise`` 的履行或拒绝值来转换 ``promise`` 的值。返回转换结果的新 ``promise`` 。

``then()`` 方法使用 ``promise`` 注册新的履行，拒绝和进度处理程序（所有参数都是可选的）：

- 履行承诺后，将调用 ``$onFulfilled`` 并将结果作为第一个参数传递。
- 一旦拒绝承诺并将原因作为第一个参数传递，将调用 ``$onRejected`` 。
- 只要 ``promise`` 的生产者触发进度通知并传递一个参数（无论它想要什么）来指示进度，就会调用 ``$onProgress`` （不建议使用）。

它返回一个新的 ``promise`` ，它将以 ``$onFulfilled`` 或 ``$onRejected`` 的返回值（无论哪个被调用）来完成，或者如果他们抛出异常将通过抛出异常来拒绝。

一个 ``promise`` 对在同一个 ``then()`` 调用中注册的所有处理程序做出以下保证：

1. 只会调用 ``$onFulfilled`` 或 ``$onRejected`` 中的一个，而不是两个。
2. 永远不会多次调用 ``$onFulfilled`` 和 ``$onRejected`` 。
3. 可以多次调用 ``$onProgress`` （不建议使用）。

ExtendedPromiseInterface
-------------------------
``ExtendedPromiseInterface`` 使用有用的快捷方式和实用程序方法扩展 ``PromiseInterface`` ，这些方法不是 ``Promises/A`` 规范的一部分。

实现类：

- Promise
- FulfilledPromise
- RejectedPromise
- LazyPromise

ExtendedPromiseInterface::done()
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    $promise->done(callable $onFulfilled = null, callable $onRejected = null, callable $onProgress = null);

如果承诺履行，则消耗承诺的最终值或处理最终错误。

如果 ``$onFulfilled`` 或 ``$onRejected`` 抛出或返回一个拒绝的承诺，则会导致致命错误。

由于 ``done()`` 的目的是消耗而不是转换，因此 ``done()`` 始终返回 ``null`` 。

ExtendedPromiseInterface::otherwise()
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    $promise->otherwise(callable $onRejected);

为承诺注册拒绝处理程序。这是下面调用的一个快捷方法：

.. code-block:: php

    $promise->then(null, $onRejected);

此外，您可以在 ``$onRejected`` 注册方法中键入提示 ``$reason`` 参数类型以仅捕获特定错误。

.. code-block:: php

	$promise
    ->otherwise(function (\RuntimeException $reason) {
        // Only catch \RuntimeException instances
        // All other types of errors will propagate automatically
    })
    ->otherwise(function ($reason) {
        // Catch other errors
    )};

ExtendedPromiseInterface::always()
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    $newPromise = $promise->always(callable $onFulfilledOrRejected);

允许您在承诺链中执行“清理”类型的任务。

当承诺被履行或拒绝时，都将调用 ``$onFulfilledOrRejected`` 方法，该方法没有参数。

- 如果 ``$promise`` 完成，并且 ``$onFulfilledOrRejected`` 成功返回，则 ``$newPromise`` 将使用与 ``$promise`` 相同的值来解析。
- 如果 ``$promise`` 完成，并且 ``$onFulfilledOrRejected`` 抛出或返回拒绝的 ``promise`` ， ``$newPromise`` 将是带有抛出异常或拒绝原因的拒绝。
- 如果 ``$promise`` 拒绝，并且 ``$onFulfilledOrRejected`` 成功返回，则 ``$newPromise`` 将拒绝，原因与 ``$promise`` 相同。
- 如果 ``$promise`` 拒绝，并且 ``$onFulfilledOrRejected`` 抛出或返回拒绝的 ``promise`` ， ``$newPromise`` 将是带有抛出异常或拒绝原因的拒绝。

``always()`` 的行为与同步 ``finally`` 语句类似。当与 ``otherwise()`` 结合使用时， ``always()`` 允许您编写类似于熟悉的同步 ``catch/finally`` 对的代码。

请考虑以下同步代码：

.. code-block:: php

	try {
	  return doSomething();
	} catch(\Exception $e) {
	    return handleError($e);
	} finally {
	    cleanup();
	}

可以编写类似的异步代码（使用返回 ``promise`` 的 ``doSomething()`` ）：

.. code-block:: php

	return doSomething()
	    ->otherwise('handleError')
	    ->always('cleanup');

ExtendedPromiseInterface::progress()
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. note:: 在v2.6.0中不推荐使用：不推荐使用进度支持，不应再使用它。

.. code-block:: php

    $promise->progress(callable $onProgress);

为 ``promise`` 中的进度更新注册处理程序。这是下面方法的一个快捷方法：

.. code-block:: php

    $promise->then(null, null, $onProgress);

CancellablePromiseInterface
----------------------------
可取消的 ``promise`` 为消费者提供了一种机制，通知创建者他们对操作结果不再感兴趣。

CancellablePromiseInterface::cancel()
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    $promise->cancel();

``cancel()`` 方法通知 ``promise`` 创建者对操作结果没有进一步的兴趣。

一旦承诺得到解决（要么履行要么被拒绝），对承诺调用 ``cancel()`` 就没有效果。

实现类：

- Promise
- FulfilledPromise
- RejectedPromise
- LazyPromise

Promise
--------
创建一个 ``promise`` ，其状态由传递给 ``$resolver`` 的函数控制。

.. code-block:: php

	$resolver = function (callable $resolve, callable $reject, callable $notify) {
	    // Do some work, possibly asynchronously, and then
	    // resolve or reject. You can notify of progress events (deprecated)
	    // along the way if you want/need.

	    $resolve($awesomeResult);
	    // or throw new Exception('Promise rejected');
	    // or $resolve($anotherPromise);
	    // or $reject($nastyError);
	    // or $notify($progressNotification);
	};

	$canceller = function () {
	    // Cancel/abort any running operations like network connections, streams etc.

	    // Reject promise by throwing an exception
	    throw new Exception('Promise cancelled');
	};

	$promise = new React\Promise\Promise($resolver, $canceller);

``promise`` 构造函数接收一个解析器函数和一个可选的 ``canceller`` 函数，它们都接收如下3个参数：

- ``$resolve($value)`` ：密封返回 ``promise`` 命运的主要函数。接受非承诺值或其他承诺。使用非承诺值调用时，使用该值履行承诺。当用另一个承诺调用时，例如 ``$resolve($otherPromise)`` ，则 ``promise`` 的命运将等同于 ``$otherPromise`` 的命运。
- ``$reject($reason)`` ：拒绝 ``promise`` 的函数。建议只抛出异常而不是使用 ``$reject()`` 。
- ``$notify($update)`` ：不推荐使用的函数，用于为 ``promise`` 发出进度事件。

如果 ``resolver`` 或 ``canceller`` 抛出异常，则该拒绝原因将使用该抛出的异常。

``resolver`` 函数将立即被调用， ``canceller`` 函数只有在所有消费者调用 ``promise`` 的 ``cancel()`` 方法时才会被调用。

FulfilledPromise
-----------------
创造一个已经履行的承诺。

.. code-block:: php

    $promise = React\Promise\FulfilledPromise($value);

注意， ``$value`` 不能是一个承诺。建议使用 ``resolve()`` 来创建已解决的 ``promise`` 。

RejectedPromise
----------------
创建一个已被拒绝的承诺。

.. code-block:: php

    $promise = React\Promise\RejectedPromise($reason);

请注意， ``$reason`` 不能是一个承诺。建议使用 ``reject()`` 来创建被拒绝的承诺。

LazyPromise
-----------
创建一个承诺，一旦消费者调用 ``then()`` 方法，它将被 ``$factory`` 懒初始化。

.. code-block:: php

	$factory = function () {
	    $deferred = new React\Promise\Deferred();

	    // Do some heavy stuff here and resolve the deferred once completed

	    return $deferred->promise();
	};

	$promise = new React\Promise\LazyPromise($factory);

	// $factory will only be executed once we call then()
	$promise->then(function ($value) {
	});

函数
----
用于创建，连接，映射和聚合 ``promise`` 集合的有用函数。

处理 ``promise`` 集合的所有函数（如 ``all()`` ，``race()`` ， ``some()`` 等）都支持取消。这意味着，如果对返回的 ``promise`` 调用 ``cancel()`` ，则集合中的所有 ``promise`` 都将被取消。如果集合本身是一个解析为数组的 ``promise`` ，则此 ``promise`` 也会被取消。

resolve()
^^^^^^^^^^

.. code-block:: php

    $promise = React\Promise\resolve(mixed $promiseOrValue);

为提供的 ``$promiseOrValue`` 创建一个 ``promise`` 。

- 如果 ``$promiseOrValue`` 是一个值，它将是返回的 ``promise`` 的解析值。
- 如果 ``$promiseOrValue`` 是一个 ``thenable`` （任何提供 ``then()`` 方法的对象），则返回一个遵循 ``thenable`` 状态的可信承诺。
- 如果 ``$promiseOrValue`` 是一个 ``promise`` ，它将按原样返回。

.. note:: 返回的 ``promise`` 始终是实现 ``ExtendedPromiseInterface`` 的承诺。如果传入仅实现 ``PromiseInterface`` 的自定义承诺，则此承诺将被同化为使用 ``$promiseOrValue`` 后的扩展承诺。

reject()
^^^^^^^^

.. code-block:: php

    $promise = React\Promise\reject(mixed $promiseOrValue);

为提供的 ``$promiseOrValue`` 创建拒绝的承诺。

如果 ``$promiseOrValue`` 是一个值，它将是返回的 ``promise`` 的拒绝值。

如果 ``$promiseOrValue`` 是一个 ``promise`` ，它的完成值将是返回的 ``promise`` 的拒绝值。

这在您需要拒绝承诺而不抛出异常的情况下非常有用。例如，它允许您使用另一个 ``promise`` 的值传播拒绝。

all()
^^^^^

.. code-block:: php

    $promise = React\Promise\all(array|React\Promise\PromiseInterface $promisesOrValues);

返回只有在 ``$promisesOrValues`` 中的所有项都已解析后才会解析的 ``promise`` 。返回的 ``promise`` 的解析值将是一个数组，其中包含 ``$promisesOrValues`` 中每个项的解析值。

race()
^^^^^^^

.. code-block:: php

    $promise = React\Promise\race(array|React\Promise\PromiseInterface $promisesOrValues);

发起竞争性比赛，允许一个赢家。返回一个 ``promise`` ，它返回第一个解析的 ``promise`` 。

如何与any区别？？

any()
^^^^^

.. code-block:: php

    $promise = React\Promise\any(array|React\Promise\PromiseInterface $promisesOrValues);

返回 ``$promisesOrValues`` 中的任何一个项解析时解析的 ``promise`` 。返回的 ``promise`` 的解析值将是触发项的解析值。

如果 ``$promisesOrValues`` 中的所有项目被拒绝，则返回拒绝 promise 。拒绝值将是所有拒绝原因的数组。

如果 ``$promisesOrValues`` 包含 ``0`` 项，则返回带有 ``React\Promise\Exception\LengthException`` 的拒绝 ``promise`` 。

some()
^^^^^^^

.. code-block:: php

    $promise = React\Promise\some(array|React\Promise\PromiseInterface $promisesOrValues, integer $howMany);

返回一个 ``promise`` ，它将在 ``$Many`` 个 ``$promisesOrValues`` 中 的所提供项解析时解析。返回的 ``promise`` 的解析值将是一个长度为 ``$howMany`` 的数组，其中包含触发项的解析值。

如果 ``$howMany`` 项无法解决（即 ``(count($promisesOrValues）- $howMany)+ 1`` 项拒绝），则返回的承诺将拒绝。拒绝值将是 ``(count($promisesOrValues) - $howMany)+ 1`` 拒绝原因的数组。

如果 ``$promisesOrValues`` 包含的项目少于 ``$howMany`` ，则返回带有 ``React\Promise\Exception\LengthException`` 的拒绝 ``promise`` 。

map()
^^^^^^

.. code-block:: php

    $promise = React\Promise\map(array|React\Promise\PromiseInterface $promisesOrValues, callable $mapFunc);

传统的 ``map`` 函数，类似于 ``array_map()`` ，但允许输入包含 ``promise`` 或值， ``$mapFunc`` 可以返回值或 ``promise`` 。

``map`` 函数 ``$mapFunc`` 接收每个项目作为参数，其中 ``item`` 是 ``$promisesOrValues`` 中的 ``promise`` 或值的完全解析值。

reduce()
^^^^^^^^^

.. code-block:: php

    $promise = React\Promise\reduce(array|React\Promise\PromiseInterface $promisesOrValues, callable $reduceFunc , $initialValue = null);

传统的 ``reduce`` 函数，类似于 ``array_reduce()`` ，但输入可能包含 ``promise`` 或值， ``$ reduceFunc`` 可能返回值或 ``promise`` ， ``$initialValue`` 可能是 ``promise`` 或值。

PromisorInterface
-----------------

``React\Promise\PromisorInterface`` 为提供 ``promise`` 的对象提供了一个通用接口。 ``React\Promise\Deferred`` 实现它，但由于它是公共API的一部分，任何人都可以实现它。

例子
====

怎样使用 Deferred
-----------------

.. code-block:: php

	function getAwesomeResultPromise()
	{
	    $deferred = new React\Promise\Deferred();

	    // Execute a Node.js-style function using the callback pattern
	    computeAwesomeResultAsynchronously(function ($error, $result) use ($deferred) {
	        if ($error) {
	            $deferred->reject($error);
	        } else {
	            $deferred->resolve($result);
	        }
	    });

	    // Return the promise
	    return $deferred->promise();
	}

	getAwesomeResultPromise()
	    ->then(
	        function ($value) {
	            // Deferred resolved, do something with $value
	        },
	        function ($reason) {
	            // Deferred rejected, do something with $reason
	        },
	        function ($update) {
	            // Progress notification triggered, do something with $update
	        }
	    );

承诺转发如何工作
---------------
一些简单的例子来说明 ``Promises/A`` 转发的机制是如何工作的。当然，这些示例是设计的，并且在实际使用中，承诺链通常会分布在多个函数调用中，甚至是应用程序体系结构的多个级别。

解析转发
^^^^^^^^
已解决的承诺将解决值转发给下一个承诺。第一个承诺 ( ``$deferred->promise()`` ) 将使用下面传递给 ``$deferred->resolve()`` 的值解析。每次调用 ``then()`` 都会返回一个新的 ``promise`` ，它将使用前一个处理程序的返回值进行解析。这创造了一个承诺“管道”。

.. code-block:: php

	$deferred = new React\Promise\Deferred();

	$deferred->promise()
	    ->then(function ($x) {
	        // $x will be the value passed to $deferred->resolve() below
	        // and returns a *new promise* for $x + 1
	        return $x + 1;
	    })
	    ->then(function ($x) {
	        // $x === 2
	        // This handler receives the return value of the
	        // previous handler.
	        return $x + 1;
	    })
	    ->then(function ($x) {
	        // $x === 3
	        // This handler receives the return value of the
	        // previous handler.
	        return $x + 1;
	    })
	    ->then(function ($x) {
	        // $x === 4
	        // This handler receives the return value of the
	        // previous handler.
	        echo 'Resolve ' . $x;
	    });

	$deferred->resolve(1); // Prints "Resolve 4"

拒绝转发
^^^^^^^
拒绝的 ``promises`` 行为类似，并且与 ``try/catch`` 的工作方式类似：当你捕获异常时，必须重新抛出它才能传播。

类似地，当你处理拒绝的承诺，传播拒绝时，通过返回拒绝的承诺或重新抛出它（因为承诺将抛出的异常转换为拒绝）

.. code-block:: php

	$deferred = new React\Promise\Deferred();

	$deferred->promise()
	    ->then(function ($x) {
	        throw new \Exception($x + 1);
	    })
	    ->otherwise(function (\Exception $x) {
	        // Propagate the rejection
	        throw $x;
	    })
	    ->otherwise(function (\Exception $x) {
	        // Can also propagate by returning another rejection
	        return React\Promise\reject(
	            new \Exception($x->getMessage() + 1)
	        );
	    })
	    ->otherwise(function ($x) {
	        echo 'Reject ' . $x->getMessage(); // 3
	    });

	$deferred->resolve(1);  // Prints "Reject 3"

混合解析和拒绝转发
^^^^^^^^^^^^^^^^^
就像 try/catch 一样，您可以选择是否传播。混合解析和拒绝仍将以可预测的方式转发处理程序结果。

.. code-block:: php

	$deferred = new React\Promise\Deferred();

	$deferred->promise()
	    ->then(function ($x) {
	        return $x + 1;
	    })
	    ->then(function ($x) {
	        throw new \Exception($x + 1);
	    })
	    ->otherwise(function (\Exception $x) {
	        // Handle the rejection, and don't propagate.
	        // This is like catch without a rethrow
	        return $x->getMessage() + 1;
	    })
	    ->then(function ($x) {
	        echo 'Mixed ' . $x; // 4
	    });

	$deferred->resolve(1);  // Prints "Mixed 4"


进度事件转发
^^^^^^^^^^^

.. note:: 在v2.6.0中不推荐使用：不推荐使用进度支持，不应再使用它。

与解析和拒绝处理程序一样，进度处理程序必须返回一个进度事件，以传播到链中的下一个链接。如果不返回任何内容，则会传播 ``null`` 。

同样，与解析和拒绝相同，如果您没有注册进度处理程序，则更新将通过传播。

如果进度处理程序抛出异常，则异常将传播到链中的下一个链接。最好的办法是确保进度处理程序不会抛出异常。

这使您有机会在链中的每个步骤转换进度事件，以便它们对下一步有意义。它还允许您选择不转换它们，并且通过不注册进度处理程序，简单地让它们传播而不转换。

.. code-block:: php

	$deferred = new React\Promise\Deferred();

	$deferred->promise()
	    ->progress(function ($update) {
	        return $update + 1;
	    })
	    ->progress(function ($update) {
	        echo 'Progress ' . $update; // 2
	    });

	$deferred->notify(1);  // Prints "Progress 2"

done()和then()比较
^^^^^^^^^^^^^^^^^^
黄金法则是： **要么返回你的承诺，要么在它上面调用 done() 。**

乍一看， ``then()`` 和 ``done()`` 似乎非常相似。但是，有一些重要的区别。

``then()`` 的目的是转换 ``promise`` 的值，并将转换后的值的新 ``promise`` 传递给代码的其他部分。

``done()`` 的目的是使用 ``promise`` 的值，将转换值的责任转移到代码中。

除了转换值之外， ``then()`` 允许您从中间错误中恢复或传播它们。任何未处理的错误都将被 ``promise`` 机制捕获并用于 ``then()`` 返回拒绝 ``promise`` 。

调用 ``done()`` 会将转换错误的所有责任转移到您的代码中。如果您提供的 ``$onFulfilled`` 或 ``$onRejected`` 回调函数中错误（抛出异常或返回拒绝）没有捕获，它将以无法捕获的方式重新抛出，从而导致致命错误。

.. code-block:: php

	function getJsonResult()
	{
	    return queryApi()
	        ->then(
	            // Transform API results to an object
	            function ($jsonResultString) {
	                return json_decode($jsonResultString);
	            },
	            // Transform API errors to an exception
	            function ($jsonErrorString) {
	                $object = json_decode($jsonErrorString);
	                throw new ApiErrorException($object->errorMessage);
	            }
	        );
	}

	// Here we provide no rejection handler. If the promise returned has been
	// rejected, the ApiErrorException will be thrown
	getJsonResult()
	    ->done(
	        // Consume transformed object
	        function ($jsonResultObject) {
	            // Do something with $jsonResultObject
	        }
	    );

	// Here we provide a rejection handler which will either throw while debugging
	// or log the exception
	getJsonResult()
	    ->done(
	        function ($jsonResultObject) {
	            // Do something with $jsonResultObject
	        },
	        function (ApiErrorException $exception) {
	            if (isDebug()) {
	                throw $exception;
	            } else {
	                logException($exception);
	            }
	        }
	    );

请注意，如果拒绝值不是 ``\Exception`` 的实例，它将被包装在 ``React\Promise\UnhandledRejectionException`` 类型的异常中。

您可以通过调用 ``$exception->getReason()`` 来获取原始拒绝原因。

安装
====

.. code-block:: shell

    $ composer require react/promise:^2.7




