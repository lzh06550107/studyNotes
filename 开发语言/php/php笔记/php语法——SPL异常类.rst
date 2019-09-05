*********
SPL异常类
*********

标准异常类
=========
SPL提供了一系列的标准异常类，包括逻辑异常(LogicException)和运行时异常(RuntimeException)。逻辑异常下又包括函数调用失败异常(BadFunctionCallException)、数据域异常(DomainException)、参数异常(InvalidArgumentException)、长度异常(LengthException)、超出范围异常(OutOfRangeException)等子类； 运行时异常包括越界异常(OutOfBoundsException)、溢出异常(OverflowException)、范围异常（RangeException,通常这里是指一个算术错误）、下溢异常（UnderflowException,如当从一个空集合中移除一个元素）和不确定值异常(UnexpectedValueException)。

SPL的异常类只是一个壳，他们都是从Exception继承下来的，所有的方法完全继承自Exception类。 如果我们需要在项目中应用SPL的异常类，可以有选择的继承这些类，当有特定的需求需要实现时，可以覆盖这些类继承自Exception类的方法。

Exception是PHP中所有异常的基类， 自从PHP5.1.0开始引入，自此，我们可以以面向对象的方式处理错误。 Exception类的声明如下：

.. code-block:: php

    <?php
		class Exception {
		    /* 属性 */
		    protected string $message ;
		    protected int $code ;
		    protected string $file ;
		    protected int $line ;

		    /* 方法 */
		    public __construct ([ string $message = "" [, int $code = 0 [, Exception $previous = NULL ]]] )
		    final public string getMessage ( void )
		    final public Exception getPrevious ( void )
		    final public int getCode ( void )
		    final public string getFile ( void )
		    final public int getLine ( void )
		    final public array getTrace ( void )
		    final public string getTraceAsString ( void )
		    public string __toString ( void )
		    final private void __clone ( void )
		}
    ?>

其中message表示异常消息内容，code表示异常代码，file表示抛出异常的文件名，line表示抛出异常在该文件中的行号。 下面从 PHP内核的角度说明这些属性及对应的方法。

message表示异常的消息内容，其对应getMessage方法。message是自定义的异常消息，默认为空字符串。 对于PHP内核来说，创建Exception对象时，有无message参数会影响 getMessage方法的返回值， 以及显示异常时是否有with message %s等字样。message成员变量的作用是为了让用户更好的定义说明异常类。

code表示异常代码，其对应getCode方法。和message成员变量一样，code也是用户自定义的内容，默认为0。

file表示抛出异常的文件名，其对应getFile方法，返回值为执行文件的文件名，在PHP内核中存储此文件名的字段为 EG(active_op_array)->filename， 此字段的值在生成一个opcode列表时，PHP的内核会将此前正在编译文件的文件名赋值给opcode的filename属性， 如生成一个函数的op_array，在初始化op_array时，会执行上面所说的赋值操作，这里的赋值是通过编译的全局变量来传递的。 当代码执行时，EG(active_op_array)表示正在执行的opcode列表。

line表示抛出异常在该文件中的行号，其对应getLine方法，返回整数，即EG(opline_ptr)->lineno。 对于每条PHP脚本生成的opcode，在编译时都会执行一次初始化操作， 在这次初始化操作中，PHP内核会将当前正在编译的行号赋值给opcode的lineno属性。 EG(opline_ptr)是PHP内核执行的当前opcode，抛出异常时对应的行号即为此对象的lineno属性。

除了上面四个属性，异常类还包括一个非常重要的内容：异常的追踪信息。 在异常类中，通过getTrace方法可以获取这些信息。此方法的作用相当于PHP的内置函数debug_backtrace。 在代码实现层面他们最终都是调用zend_fetch_debug_backtrace函数。在此函数中通过回溯PHP的调用栈，返回代码追踪信息。 与getTrace方法对应还有一个返回被串化值的方法getTraceAsString，以字符串替代数组返回异常追踪信息。

在构造函数中，从PHP5.3.0增加$previous参数，表示异常链中的前一个异常。 在catch块中可以抛出一个新的异常，并引用原始的异常，为调试提供更多的信息。


逻辑异常(LogicException)
------------------------
编程逻辑中的异常。这种异常应该直接导致代码需要修复。

函数调用失败异常(BadFunctionCallException)
-----------------------------------------
如果callback一个未定义的函数，或者函数参数缺失，则抛出本异常。该异常一般和is_callable()联合使用

.. code-block:: php

    <?php
	function foo($arg) {
	    $func = 'do' . $arg;
	    if (!is_callable($func)) {
	        throw new BadFunctionCallException('Function ' . $func . ' is not callable');
	    }
	}
    ?>

方法调用失败异常(BadMethodCallException)
---------------------------------------
当一个回调方法是一个未定义的方法或缺失一些参数时会抛出该异常。该异常一般和__call()魔术方法联合使用。

数据域异常(DomainException)
--------------------------
如果值不符合已定义的有效数据域，则抛出异常。

参数异常(InvalidArgumentException)
---------------------------------
如果参数不是预期类型，则抛出异常。

长度异常(LengthException)
------------------------
如果长度无效，则抛出异常。

超出范围异常(OutOfRangeException)
--------------------------------
请求非法索引时抛出异常。这表示应在编译时检测到的错误。

RuntimeException(运行时异常)
===========================
如果只能在运行时发现错误，则抛出异常。

越界异常(OutOfBoundsException)
------------------------------
如果值不是有效的键，则抛出异常。这表示在编译时无法检测到的错误。

溢出异常(OverflowException)
--------------------------
将元素添加到满的容器时抛出异常。

范围异常（RangeException,通常这里是指一个算术错误）
-----------------------------------------------
在程序执行期间抛出异常来指示范围错误。通常这意味着除了溢出/溢出之外，还有一个算术错误。这是DomainException的运行时版本。

下溢异常（UnderflowException,如当从一个空集合中移除一个元素）
-------------------------------------------------------
在空容器上执行无效操作（例如移除元素）时抛出异常。

不确定值异常(UnexpectedValueException)
-------------------------------------
如果值与一组值不匹配，则抛出异常。通常情况下，这发生在函数调用另一个函数时，并且期望返回值具有某种类型或值，而不包括算术或缓冲区相关的错误。