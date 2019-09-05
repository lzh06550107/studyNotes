**********************
Linux下C/C++头文件以及库的搜索路径
**********************

关键点：

1. ``#include <...>`` 不会搜索当前目录；
2. 使用 ``-I`` 参数指定的头文件路径仅次于搜索当前路径；
3. ``gcc -E -v`` 可以输出头文件路径搜索过程；

``C++`` 编译时，教科书中写道: ``#include "headfile.h"`` 优先在当前目录查找头文件； ``#include <headfile.h>`` 从系统默认路径查找头文件。先前以为系统默认路径是环境变量 ``$PATH`` 指定的路径，在系统上一查，傻了眼：

.. code-block:: shell

    -bash-3.2$ echo $PATH
    /usr/local/bin:/bin:/usr/bin:/sbin:/usr/sbin:/usr/X11R6/bin:/usr/Java/j2re1.4.0/bin:/usr/atria/bin:/ccase/bin:/home/devcomp/bin

全是 ``bin`` 目录， ``$PATH`` 是运行可执行文件时的搜索路径，与 ``include`` 头文件的搜索路径无关，可能不少人犯了我这样的错误。


头文件
======

#include "headfile.h"
----------------------
搜索顺序为：

1. 先搜索当前目录；
2. 然后搜索 ``-I`` 指定的目录；
3. 再搜索 ``gcc`` 的环境变量 ``CPLUS_INCLUDE_PATH`` （ ``C`` 程序使用的是 ``C_INCLUDE_PATH`` ）；
4. 最后搜索 ``gcc`` 的内定目录；

.. code-block:: shell

	/usr/include
	/usr/local/include
	/usr/lib/gcc/x86_64-redhat-Linux/4.1.1/include

各目录存在相同文件时，先找到哪个使用哪个。

#include <headfile.h>
----------------------

1. 先搜索 ``-I`` 指定的目录；
2. 然后搜索 ``gcc`` 的环境变量 ``CPLUS_INCLUDE_PATH`` ；
3. 最后搜索 ``gcc`` 的内定目录；

.. code-block:: shell

	/usr/include
	/usr/local/include
	/usr/lib/gcc/x86_64-redhat-linux/4.1.1/include

与上面的相同，各目录存在相同文件时，先找到哪个使用哪个。这里要注意， ``#include<>`` 方式不会搜索当前目录！

这里要说下 ``include`` 的内定目录，它不是由 ``$PATH`` 环境变量指定的，而是由 ``g++`` 的配置 ``prefix`` 指定的(知道它在安装 ``g++`` 时可以指定，不知安装后如何修改的，可能是修改配置文件，需要时再研究下)：

.. code-block:: shell

    -bash-3.2$ g++ -v
	Using built-in specs.
	Target: x86_64-redhat-linux
	Configured with: ../configure --prefix=/usr --mandir=/usr/share/man --infodir=/usr/share/info --enable-shared --enable-threads=posix --enable-checking=release --with-system-zlib --enable-__cxa_atexit --disable-libunwind-exceptions --enable-libgcj-multifile --enable-languages=c,c++,objc,obj-c++,java,fortran,ada --enable-java-awt=gtk --disable-dssi --enable-plugin --with-java-home=/usr/lib/jvm/java-1.4.2-gcj-1.4.2.0/jre --with-cpu=generic --host=x86_64-redhat-linux
	Thread model: posix
	gcc version 4.1.2 20080704 (Red Hat 4.1.2-46)

在安装 g++ 时，指定了 prefix ，那么内定搜索目录就是：

.. code-block:: shell

	Prefix/include
	Prefix/local/include
	Prefix/lib/gcc/--host/--version/include

编译时可以通过 ``-nostdinc++`` 选项屏蔽对内定目录搜索头文件。

库文件
======

编译的时候：

1. ``gcc`` 会去找 ``-L`` ；
2. 再找 ``gcc`` 的环境变量 ``LIBRARY_PATH`` ；
3. 再找内定目录 ``/lib /usr/lib /usr/local/lib`` 这是当初 ``compile gcc`` 时写在程序内的（不可配置的？）

运行时动态库的搜索路径
--------------------
动态库的搜索路径搜索的先后顺序是：

1. 编译目标代码时指定的动态库搜索路径（这是通过 ``gcc``  的参数 ``"-Wl,-rpath"`` 指定。当指定多个动态库搜索路径时，路径之间用冒号 ``:`` 分隔）；
2. 环境变量 ``LD_LIBRARY_PATH`` 指定的动态库搜索路径（当通过该环境变量指定多个动态库搜索路径时，路径之间用冒号 ``:`` 分隔）
3. 配置文件 ``/etc/ld.so.conf`` 中指定的动态库搜索路径；
4. 默认的动态库搜索路径 ``/lib`` ；
5. 默认的动态库搜索路径 ``/usr/lib`` 。
（应注意动态库搜寻路径并不包括当前文件夹，所以当即使可执行文件和其所需的 ``so`` 文件在同一文件夹，也会出现找不到 ``so`` 的问题，类同 ``#include <header_file>`` 不搜索当前目录）



