*****
错误和异常
*****

在 PHP 中，错误和异常可以分为以下 3 个类别：异常，可截获错误，不可截获错误。异常和可截获错误虽然机理不同，但可以当做是同一种处理方式，而不可截获错误是另一种，是一种较为棘手的错误类型。马上将会讲到，PHP7 中的 fatal error 是一种继承自 Throwable 的 Error，是可以被 try catch 住的。通过这一方式 PHP7 解决了这一难题。

值得注意的是，Error 类表现上和 Exception 基本一致，可以像 Exception 异常一样被第一个匹配的 try / catch 块所捕获，如果没有匹配的 catch 块，则调用异常处理函数（事先通过 set_exception_handler() 注册）进行处理。 如果尚未注册异常处理函数，则按照传统方式处理，被报告为一个致命错误（Fatal Error）。但并非继承自 Exception 类（要考虑到和 PHP5 的兼容性），所以不能用 catch (Exception $e) { ... } 来捕获，而需要使用 catch (Error $e) { ... }，当然，也可以使用 set_exception_handler 来捕获。

错误
====

E_DEPRECATED(8192) 运行时通知,启用后将会对在未来版本中可能无法正常工作的代码给出警告。

E_USER_DEPRECATED(16384) 是由用户自己在代码中使用PHP函数 trigger_error() 来产生的

E_NOTICE(8) 运行时通知。表示脚本遇到可能会表现为错误的情况

E_USER_NOTICE(1024) 是用户自己在代码中使用PHP的trigger_error() 函数来产生的通知信息

E_WARNING(2) 运行时警告 (非致命错误)

E_USER_WARNING(512) 用户自己在代码中使用PHP的 trigger_error() 函数来产生的

E_CORE_WARNING(32) PHP初始化启动过程中由PHP引擎核心产生的警告

E_COMPILE_WARNING(128) Zend脚本引擎产生编译时警告

E_ERROR(1) 致命的运行时错误

E_USER_ERROR(256) 用户自己在代码中使用PHP的 trigger_error()函数来产生的

E_CORE_ERROR(16) 在PHP初始化启动过程中由PHP引擎核心产生的致命错误

E_COMPILE_ERROR(64) Zend脚本引擎产生的致命编译时错误

E_PARSE(4) 编译时语法解析错误。解析错误仅仅由分析器产生

E_STRICT(2048) 启用 PHP 对代码的修改建议，以确保代码具有最佳的互操作性和向前兼容性

E_RECOVERABLE_ERROR(4096) 可被捕捉的致命错误。 它表示发生了一个可能非常危险的错误，但是还没有导致PHP引擎处于不稳定的状态。 如果该错误没有被用户自定义句柄捕获 (参见 set_error_handler() )，将成为一个 E_ERROR 　从而脚本会终止运行。

E_ALL(30719) 所有错误和警告信息(手册上说不包含E_STRICT, 经过测试其实是包含E_STRICT的)。


在前述的系统自带的 16 种错误中，有一部分相当重要的错误并不能被 error_handler 捕获

以下级别的错误不能由用户定义的函数来处理： E_ERROR、 E_PARSE、 E_CORE_ERROR、 E_CORE_WARNING、 E_COMPILE_ERROR、E_COMPILE_WARNING，和在调用 set_error_handler() 函数所在文件中产生的大多数 E_STRICT。

这时可以通过注册 register_shutdown_function([__CLASS__, 'appShutdown']) 来获取未处理的错误。

.. note:: 另外一个需要注意的是， error_handler 处理完毕，脚本将会继续执行发生错误的后一行。在某些情况下，你可能希望遇到某些错误可以中断脚本的执行。可以使用die()/exit()结束执行或者直接抛出异常来中断执行。

最佳实践
--------
如果没有做任何配置，PHP 的错误是会直接打印出来的。古老的 PHP 应用也确实有这么做的。但现代应用显然不能这样，现代应用的错误应该遵循一下规则

- 一定要让 PHP 报告错误；
- 在开发环境中要显示错误；
- 在生产环境中不能显示错误；
- 在开发和生产环境中都要记录错误。


异常
====

php7.3中异常类：

.. code-block:: shell

    Error
	   ArithmeticError
	      DivisionByZeroError
	   AssertionError
	   CompileError
	      ParseError
	   TypeError
	      ArgumentCountError
	Exception
	   ClosedGeneratorException
	   DOMException
	   ErrorException
	   IntlException
	   JsonException
	   LogicException
	      BadFunctionCallException
	         BadMethodCallException
	      DomainException
	      InvalidArgumentException
	      LengthException
	      OutOfRangeException
	   PharException
	   ReflectionException
	   RuntimeException
	      OutOfBoundsException
	      OverflowException
	      PDOException
	      RangeException
	      UnderflowException
	      UnexpectedValueException
	   SodiumException

在我们的日常开发中，不可能保证可以 catch 所有的异常，而未被 catch 的异常将以 fatal error 的形式中断脚本的执行并输出错误信息。所以要借助 set_exception_handler，统一处理所有未被 catch 的异常。我们可以像 error_handler 那样，在 exception_handler 中处理 log，将数据库的事务回滚。

前面提到，error_handler 需要在必要的时候手动中断脚本， PHP 文档中给出的一种实践是，在 error_handler 中 throw ErrorException 。注意：在 error_handler 中 throw exception 不会被 exception_handler 截获。


参考：

- https://juejin.im/entry/5987d2ff6fb9a03c314fe732#fn:2
- https://www.cnblogs.com/yjf512/p/5314345.html 日志和错误配置可以参考




