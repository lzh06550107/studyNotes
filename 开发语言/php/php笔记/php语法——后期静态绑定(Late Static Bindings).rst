********************************
PHP 后期静态绑定(Late Static Bindings)
********************************
在PHP5.3中加入了一个新特性：延迟静态绑定，就是把本来在定义阶段固定下来的表达式或变量，改在执行阶段才决定。

准确说，后期静态绑定工作原理是存储了在上一个“非转发调用”（non-forwarding call）的类名。

什么是非转发调用？
==================
- 当进行静态方法调用时，该类名为明确指定的那个（通常在 :: 运算符左侧部分）；
- 当进行非静态方法调用时，即为该对象所属的类。

符合以上条件之一的调用被称为非转发调用。

什么是转发调用？
================
所谓的“转发调用”（forwarding call）指的是通过以下几种方式进行的静态调用：self::，parent::，static:: 以及 forward_static_call()。可用 get_called_class() 函数来得到被调用的方法所在的类名，即获取静态方法调用的类名。

在进行静态调用时未指名类名则是转发调用。

转发与非转发调用本质区别在于是否将调用者的信息传递下去。这个概念只有在使用lsb的时候才有意义。

分析官方文档给的例子
====================

.. code-block:: php

    <?php
    class A {
        public static function foo() {
            static::who();
        }

        public static function who() {
            echo __CLASS__."\n";
        }
    }

    class B extends A {
        public static function test() {
            A::foo();
            parent::foo();
            self::foo();
        }

        public static function who() {
            echo __CLASS__."\n";
        }
    }
    class C extends B {
        public static function who() {
            echo __CLASS__."\n";
        }
    }

    C::test();
    // 输出结果
    A
    C
    C

首先C::test()，进入test方法，A::foo()、parent::foo()、self::foo()这三个方法调用的“上一次非转发调用”存储的类名就是C啦。

通过A::foo()进入foo方法时，“上一次非转发调用”存储的类名就变成A啦。所以static::实际上代表A::。

通过parent::foo()进入foo方法时，“上一次非转发调用”存储的类名还是C。所以static::代表C::。

通过self::foo()进入foo方法时，“上一次非转发调用”存储的类名还是C。所以static::代表C::。

注意这里之所以会触发lsb是因为foo()方法中的static::用法。如果这里的static::who()改为self::who()，则不会出lsb，也就没有“上一次非转发调用”存储的类名这个概念了，实际上就是A::who()