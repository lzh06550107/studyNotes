********
PHP多进程操作
********

简介
====
我们都知道PHP是单进程执行的，PHP处理多并发主要是依赖服务器或PHP-FPM的多进程及它们进程的复用，但PHP实现多进程也意义重大，尤其是在后台Cli模式下处理大量数据或运行后台DEMON守护进程时，多进程的优势不用多说。

PHP的多线程也曾被人提及，但进程内多线程资源共享和分配的问题难以解决。PHP也有多线程想关的扩展 pthreads ，但据说不太稳定，且要求环境为线程安全，所用不多。

要实现PHP的多进程，我们需要两个扩展 pcntl和 posix，安装方法这里不再赘述。

PHP中的进程控制支持实现了进程创建，程序执行，信号处理和进程终止的Unix风格。不应在Web服务器环境中启用进程控制，如果在Web服务器环境中使用任何进程控制功能，则可能会发生意外的结果。

本文档旨在解释每个过程控制功能的一般用法。有关Unix进程控制的详细信息，建议您参考您的系统文档（包括fork(2)，waitpid(2)和signal(2)）或综合参考（如UNIX环境中的高级编程）。

PCNTL现在使用ticks作为信号处理回调机制，比以前的机制快得多。此更改遵循与使用“用户ticks”相同的语义。您可以使用declare()语句来指定程序中允许发生回调的位置。这样可以最大限度地减少处理异步事件的开销。在过去，无论你的脚本实际上是否使用pcntl，编译启用pcntl的PHP总是会招致这种开销。

有一个调整，所有在PHP 4.3.0之前的pcntl脚本要使它们工作，或者在回调的地方使用declare()，或者在整个脚本中使用declare()新的全局语法。

.. note:: 通过pcntl_XXX系列函数使用多进程功能。注意：pcntl_XXX只能运行在php CLI（命令行）环境下，在web服务器环境下，会出现无法预期的结果，请慎用！

.. note:: 此扩展在Windows平台上不可用。

常用函数
========

pcntl_fork()
--------------
``int pcntl_fork ( void )``

pcntl_fork() 在当前进程当前位置产生分支（子进程）。此函数创建了一个新的子进程后，子进程会继承父进程当前的上下文，和父进程一样从pcntl_fork()函数处继续向下执行，只是获取到的pcntl_fork()的返回值不同，我们便能从判断返回值来区分父进程和子进程，分配父进程和子进程去做不同的逻辑处理。

pcntl_fork()函数成功执行时会在父进程返回子进程的进程id(pid)，因为系统的初始进程init进程的pid为1，后来产生进程的pid都会大于此进程，所以我们可以通过判断pcntl_fork()的返回值大于1来确实当前进程是父进程；

而在子进程中，此函数的返回值会是固定值0，我们也可以通过判断pcntl_fork()的返回值为0来确定子进程；

而pcntl_fork()函数在执行失败时，会在父进程返回-1,当然也不会有子进程产生。

返回值：

成功时，子进程的PID将在父进程的执行线程中返回，并在子进程的线程中返回0。失败时，将在父项的上下文中返回-1，不会创建子进程，并引发PHP错误。

.. code-block:: php

    <?php
    $ppid = posix_getpid();
    $pid = pcntl_fork();
    if ($pid == -1) {
        throw new Exception('fork子进程失败!');
    } elseif ($pid > 0) {
        cli_set_process_title("我是父进程,我的进程id是{$ppid}.");
        $n = 200;
    } else {
        $cpid = posix_getpid();
        cli_set_process_title("我是{$ppid}的子进程,我的进程id是{$cpid}.");
        $n = 100;
    }

    for(; $n > 0; $n--) {
        usleep(100);
        print cli_get_process_title().PHP_EOL;
    }
    ?>

代码说明：

1. 父进程初始化。
2. 父进程调用 ``pcntl_fork()`` ，这是一个系统调用，因此进入内核。
3. 内核根据父进程复制出一个子进程，父进程和子进程的PCB信息相同，用户态代码和数据也相同。因此，子进程现在的状态看起来和父进程一样，做完了初始化，刚调用了 ``pcntl_fork()`` 进入内核，还没有从内核返回。
4. 现在有两个一模一样的进程看起来都调用了 ``pcntl_fork()`` 进入内核等待从内核返回（实际上fork只调用了一次），此外系统中还有很多别的进程也等待从内核返回。是父进程先返回还是子进程先返回，还是这两个进程都等待，先去调度执行别的进程，这都不一定，取决于内核的调度算法。
5. 如果某个时刻父进程被调度执行了，从内核返回后就从 ``pcntl_fork()`` 函数返回，保存在变量pid中的返回值是子进程的id，是一个大于0的整数，因此执下面的elseif分支，然后执行for循环，打印之后终止。
6. 如果某个时刻子进程被调度执行了，从内核返回后就从 ``pcntl_fork()`` 函数返回，保存在变量pid中的返回值是0，因此执行下面的else分支，然后执行for循环，打印。

posix_getpid()
----------------
``int posix_getpid ( void )``

返回当前进程的进程标识符。

返回值：

以整数形式返回标识符。

posix_getppid()
------------------
``int posix_getppid ( void )``

返回当前进程父进程的进程标识符。

返回值：

以整数形式返回标识符。

pcntl_exec()
---------------
``bool pcntl_exec ( string $path [, array $args [, array $envs ]] )``

用给定的参数执行程序。

参数：

- path：路径必须是指向二进制可执行文件或脚本的路径，该脚本的第一行存在指向可执行文件路径。有关更多信息，请参阅系统的man execve(2)页面。
- args：args是传递给程序的参数字符串数组。
- envs：envs是一个字符串数组，它作为环境传递给程序。该数组的格式为name => value，键为环境变量的名称，值为该变量的值。

返回值：

错误时返回FALSE，成功时不返回。

.. code-block:: php

    <?php
    // 获取用户名称
    $username = $_SERVER['argv'][1];
    // 根据用户名称获取用户信息数组
    $user = posix_getpwnam($username);
    // 修改当前进程的组id
    posix_setgid($user['gid']);
    // 修改当前进程的用户id
    posix_setuid($user['uid']);
    // 使用设置的账号执行命令
    pcntl_exec('/path/to/cmd');

    $dir = '/home/vagrant/';
    $cmd = 'ls';
    $option = '-l';
    $pathtobin = '/bin/ls';

    $arg = array($cmd, $option, $dir);

    pcntl_exec($pathtobin, $arg);
    echo '123';    //不会执行到该行
    ?>

posix_getpwnam()
--------------------
``array posix_getpwnam ( string $username )``

返回有关给定用户的信息数组。

参数：

- username：一个字母数字用户名。

返回值：

成功返回一个包含以下元素的数组，否则返回FALSE：


用户信息数组

========  =============================================================================================================================
键         描述
========  =============================================================================================================================
name      名称元素包含用户的用户名。这是一个短的，通常少于16个字符的用户句柄，而不是真实的全名。这应该与调用函数时使用的用户名参数相同，因此是多余的。
passwd    passwd元素以加密格式包含用户的密码。通常，例如在使用“shadow”密码的系统上，将返回星号。
uid       用户的数字形式的用户ID。
gid       用户的组ID。使用函数posix_getgrgid()来解析组名及其成员列表。
gecos     GECOS是一个过时的术语，指的是霍尼韦尔批处理系统上的手指信息字段。然而，这个领域依然存在，其内容已经被POSIX正式化了。该字段包含逗号分隔的列表，其中包含用户的全名，办公室电话，办公室号码和家庭电话号码。在大多数系统上，只有用户的全名可用。
dir       该元素包含用户主目录的绝对路径。
shell     shell元素包含用户默认shell的可执行文件的绝对路径。
========  =============================================================================================================================

.. code-block:: php

    <?php
    $userinfo = posix_getpwnam("vagrant");
    print_r($userinfo);
    /*
    Array
    (
        [name] => vagrant
        [passwd] => x
        [uid] => 1000
        [gid] => 1000
        [gecos] => vagrant,,,
        [dir] => /home/vagrant
        [shell] => /bin/bash
    )
    */
    ?>

posix_setuid()
-------------------
``bool posix_setuid ( int $uid )``

设置当前进程的真实用户标识。这是一个特权函数，需要系统上适当的权限（通常是root）才能执行此功能。

参数：

- uid：用户id;

返回值：

成功返回TRUE，失败则返回FALSE。

posix_setgid()
-----------------
``bool posix_setgid ( int $gid )``

设置当前进程的实际组ID。这是一个特权功能，需要适当的权限（通常是root）才能执行这个功能。函数调用的适当顺序是首先调用posix_setgid()，然后调用posix_setuid()。

.. note:: 如果调用方是超级用户，这也将设置有效的组ID。

参数：

- gid：组id;

返回值：

成功返回TRUE，失败则返回FALSE。


一个进程在终止时会关闭所有文件描述符，释放在用户空间分配的内存，但它的PCB还保留着，内核在其中保存了一些信息：如果是正常终止则保存着退出状态，如果是异常终止则保存着导致该进程终止的信号是哪个。这个进程的父进程可以调用wait或waitpid获取这些信息，然后彻底清除掉这个进程。我们知道一个进程的退出状态可以在Shell中用特殊变量$?查看，因为Shell是它的父进程，当它终止时Shell调用wait或waitpid得到它的退出状态同时彻底清除掉这个进程。

如果一个进程已经终止，但是它的父进程尚未调用wait或waitpid对它进行清理，这时的进程状态称为僵尸（ Zombie） 进程。

pcntl_wait()
--------------
``int pcntl_wait ( int &$status [, int $options = 0 ] )``

wait()函数暂停(阻塞)当前进程的执行，直到孩子退出，或者直到一个终止当前进程或调用信号处理函数的信号被传递。如果一个孩子已经退出通话时间（所谓的“僵尸”进程），该函数立即返回。任何由孩子使用的系统资源都被释放。请参阅系统的等待（2）手册页，了解有关系统如何等待的特定详细信息。

.. note:: 这个函数相当于调用pid为-1 (表示等待所有孩子进程结束)和没有选项的pcntl_waitpid()。

参数：

- status：pcntl_wait()会将状态信息存储在状态参数中，可以使用pcntl_wifexited()，pcntl_wifstopped()，pcntl_wifsignaled()，pcntl_wexitstatus()，pcntl_wtermsig()和pcntl_wstopsig()函数来获取状态信息。
- options：如果系统上有wait3（主要是BSD风格的系统），则可以提供可选的options参数。如果未提供此参数，则将把wati当做系统调用。如果wait3不可用，则为选项提供值将不起作用。选项的值是以下两个常量中的OR组合：

===========  ===================
WNOHANG      如果没有孩子退出，立即返回。
===========  ===================
WUNTRACED    返回已停止的且未报告其状态的孩子。
===========  ===================

返回值：

pcntl_wait()返回退出的子进程的ID，错误时为-1，如果WNOHANG作为选项提供（在wait3-available系统上）并且没有孩子可用，则不阻塞立即返回0。

pcntl_waitpid()
------------------
``int pcntl_waitpid ( int $pid , int &$status [, int $options = 0 ] )``

暂停执行当前进程，直到由pid参数指定的子进程退出，或者直到传递一个信号，该信号的作用是终止当前进程或调用信号处理函数。

如果在pid请求的孩子已经退出通话时间（所谓的“僵尸”进程），该函数立即返回。任何由孩子使用的系统资源都被释放。请参阅系统的waitpid（2）手册页，了解有关系统如何使用waitpid的具体细节。

参数：

- pid：pid的值可以是下列之一：

======  =========================
< -1    等待进程组ID等于pid绝对值的任何子进程。
======  =========================
-1      等待任何孩子进程;这是pcntl_wait()函数表现的行为。
0       等待进程组ID等于调用进程的子进程。
> 0     等待进程ID等于pid值的子进程。
======  =========================

参数：

- status：pcntl_wait()会将状态信息存储在状态参数中，可以使用pcntl_wifexited()，pcntl_wifstopped()，pcntl_wifsignaled()，pcntl_wexitstatus()，pcntl_wtermsig()和pcntl_wstopsig()函数来获取状态信息。
- options：如果系统上有wait3（主要是BSD风格的系统），则可以提供可选的options参数。如果未提供此参数，则将把wati当做系统调用。如果wait3不可用，则为选项提供值将不起作用。选项的值是以下两个常量中的OR组合：

===========  ===================
WNOHANG      如果没有孩子退出，立即返回。
===========  ===================
WUNTRACED    返回已停止的且未报告其状态的孩子。
===========  ===================

返回值：

pcntl_waitpid()返回退出的子进程的ID，错误时为-1，如果WNOHANG作为选项提供（在wait3-available系统上）并且没有孩子可用，则不阻塞立即返回0。

pcntl_wifexited()
---------------------
``bool pcntl_wifexited ( int $status )``

检查子进程状态码是否正常退出。

参数：

- status：状态参数是提供给pcntl_waitpid()后成功调用的状态参数。

返回值：

如果孩子进程状态代码表示正常退出，则返回TRUE，否则返回FALSE。

pcntl_wifstopped()
----------------------
``bool pcntl_wifstopped ( int $status )``

检查子进程当前是否已经停止;这只有在使用WUNTRACED选项对pcntl_waitpid()的调用完成时才有可能。

参数：

- status：状态参数是提供给pcntl_waitpid()后成功调用的状态参数。

返回值：

如果返回的子进程当前已停止，则返回TRUE;否则返回FALSE。

pcntl_wifsignaled()
----------------------
``bool pcntl_wifsignaled ( int $status )``

检查是否由于未捕获的信号而退出子进程。

参数：

- status：状态参数是提供给pcntl_waitpid()后成功调用的状态参数。

返回值：

如果子进程由于未捕获到的信号而退出，则返回TRUE;否则返回FALSE。

pcntl_wexitstatus()
-----------------------
``int pcntl_wexitstatus ( int $status )``

返回已终止子进程的返回码。此函数仅在pcntl_wifexited()返回TRUE时有用。

参数：

- status：状态参数是提供给pcntl_waitpid()后成功调用的状态参数。

返回值：

以整数形式返回返回码。

pcntl_wtermsig()
--------------------
``int pcntl_wtermsig ( int $status )``

返回导致子进程终止的信号的编号。此函数仅在pcntl_wifsignaled()返回TRUE时有用。

参数：

- status：状态参数是提供给pcntl_waitpid()后成功调用的状态参数。

返回值：

以整数形式返回信号编号。

pcntl_wstopsig()
-------------------
``int pcntl_wstopsig ( int $status )``

返回导致孩子停止的信号的数量。这个函数只有在pcntl_wifstopped()返回TRUE时才有用。

参数：

- status：状态参数是提供给pcntl_waitpid()后成功调用的状态参数。

返回值：

返回信号编号。

pcntl_setpriority()
----------------------
``bool pcntl_setpriority ( int $priority [, int $pid = getmypid() [, int $process_identifier = PRIO_PROCESS ]] )``

pcntl_setpriority()设置pid的优先级。

参数：

- priority：优先级通常是-20至20范围内的值。默认优先级为0，而较低的数值会导致更有利的调度。由于系统类型和内核版本的优先级可能不同，请查看您系统的setpriority（2）手册页以获取具体的细节。
- pid：如果未指定，则使用当前进程的PID。
- process_identifier：PRIO_PGRP，PRIO_USER或PRIO_PROCESS之一。

返回值：

成功返回TRUE，失败则返回FALSE。

pcntl_getpriority()
----------------------
``int pcntl_getpriority ([ int $pid = getmypid() [, int $process_identifier = PRIO_PROCESS ]] )``

pcntl_getpriority()获取pid的优先级。由于系统类型和内核版本的优先级可能不同，请参阅系统的getpriority（2）手册页以获取特定的详细信息。

参数：

- pid：如果未指定，则使用当前进程的PID。
- process_identifier：PRIO_PGRP，PRIO_USER或PRIO_PROCESS之一。, 默认 PRIO_PROCESS。其中 PRIO_PGRP 指获取进程组的优先级 , PRIO_USER 指获取用户进程的优先级 , PRIO_PROCESS 指获取特定进程优先级 。

返回值：

pcntl_getpriority()返回进程的优先级，错误返回FALSE。数值越低，调度越有利。

.. note:: 此函数可能会返回布尔值FALSE，但也可能会返回一个非布尔值，其值为FALSE。有关更多信息，请阅读布尔部分。使用===运算符来测试这个函数的返回值。

**产生信号**

pcntl_alarm()
----------------
``int pcntl_alarm ( int $seconds )``

创建一个计时器，在给定的秒数之后，该计时器将发送一个SIGALRM信号给进程。任何对pcntl_alarm()的调用都将取消之前设置的任何警报。

参数：

- seconds：等待的秒数。如果秒数为零，则不会创建新的警报。

返回值：

返回先前计划的警报在交付之前剩余的时间（秒），如果没有先前计划的警报，则返回0。

posix_kill()
-------------
``bool posix_kill ( int $pid , int $sig )``

将信号sig发送到进程标识符为pid的进程。

参数：

- pid：进程标识符。
- sig：其中一个PCNTL信号常量。

返回值：

成功返回TRUE，失败则返回FALSE。

**操作信号**

pcntl_sigprocmask()
-----------------------
``bool pcntl_sigprocmask ( int $how , array $set [, array &$oldset ] )``

pcntl_sigprocmask()函数根据how参数添加，删除或设置阻塞的信号。

参数：

- how：设置pcntl_sigprocmask()的行为。可能的值：

  + SIG_BLOCK：将信号添加到当前被阻止的信号。
  + SIG_UNBLOCK：从当前阻塞的信号中删除信号。
  + SIG_SETMASK：用给定的信号列表替换当前阻塞的信号。

- set：信号列表。
- oldset：oldset参数设置为包含先前阻塞的信号列表的数组。

返回值：

成功返回TRUE，失败则返回FALSE。

**信号处理**

pcntl_signal()
----------------
``bool pcntl_signal ( int $signo , callable|int $handler [, bool $restart_syscalls = true ] )``

pcntl_signal()函数安装一个新的信号处理程序或替换signo指示的信号的当前信号处理程序。

参数：

- signo：信号号码。
- handler：信号处理程序。这可能是函数，将被调用来处理信号，或者是两个全局常量SIG_IGN或SIG_DFL中的任一个，它们将分别表示忽略信号或恢复默认信号处理程序。
- restart_syscalls：指定当信号到达时是否应该使用系统调用重启。

如果给出了回调函数，则必须执行以下签名：

``void handler ( int $signo , mixed $signinfo )``

- signo：正在处理的信号。
- siginfo：如果操作系统支持siginfo_t结构，这将是一个依赖于信号的信号信息数组。

.. note:: 请注意，当您将一个处理程序设置为一个对象方法时，该对象的引用计数会增加，这会使对象一直不能被清除，直到您将处理程序更改为其他值，或脚本结束。

返回值：

成功返回TRUE，失败则返回FALSE。

pcntl_signal_get_handler()
-------------------------------
``int|string pcntl_signal_get_handler ( int $signo )``

pcntl_signal_get_handler()函数将获取指定signo的当前处理程序。

参数：

- signo：信号号码。

返回值：

该函数可能会返回一个引用SIG_DFL或SIG_IGN的整数值。如果您设置自定义处理程序，则返回包含函数名称的字符串值。

pcntl_signal_dispatch()
---------------------------
``bool pcntl_signal_dispatch ( void )``

pcntl_signal()函数仅仅是注册信号和它的处理方法，真正接收到信号并调用其处理方法的是pcntl_signal_dispatch()函数

返回值：

成功返回TRUE，失败则返回FALSE。

.. code-block:: php

    <?php
    function signal_handler($signal) {
        print "Caught SIGALRM\n";
        pcntl_alarm(5);
    }

    pcntl_signal(SIGALRM, "signal_handler", true);
    pcntl_alarm(5);

    for(;;) {
        pcntl_signal_dispatch();
    }
    ?>

pcntl_sigwaitinfo()
----------------------
``int pcntl_sigwaitinfo ( array $set [, array &$siginfo ] )``

pcntl_sigwaitinfo()函数暂停调用脚本的执行，直到set中给出的信号之一被传递。如果其中一个信号已经挂起（例如被pcntl_sigprocmask()阻塞），pcntl_sigwaitinfo()将立即返回。

参数：

- set：要等待的信号数组。
- siginfo：siginfo参数设置为包含有关该信号的信息的数组。

1. 所有信号都有以下三个信息 :

   a) signo: 信号编号
   b) errno: 错误号
   c) code: 信号代码
2. SIGCHLD 信号特有的信息

   a) status: 退出的值或信号
   b) utime: 用户消耗时间
   c) stime: 系统消耗时间
   d) pid: 发送进程 id
   e) uid: 发送进程的真实用户 id
3. SIGILL, SIGFPE, SIGSEGV, SIGBUS 拥有的信息

   a)  addr: 产生故障的内存位置
4. SIGPOLL 特有的信息 :

   a) band: band event, 意义未知
   b) fd: 文件描述符

返回值：

成功时，pcntl_sigwaitinfo()返回一个信号编号。

pcntl_sigtimedwait()
------------------------
``int pcntl_sigtimedwait ( array $set [, array &$siginfo [, int $seconds = 0 [, int $nanoseconds = 0 ]]] )``

pcntl_sigtimedwait()函数的操作方式与pcntl_sigwaitinfo()完全相同，只是它需要两个额外的参数（秒和纳秒），它们使上限来限制挂起脚本的时间。

参数：

- set：要等待的信号数组。
- siginfo：siginfo参数设置为包含有关该信号的信息的数组。
- seconds：以秒为单位的超时。
- nanoseconds：以纳秒为单位超时。

返回值：

成功时，pcntl_sigtimedwait()返回一个信号编号。

posix_setsid()
----------------
``int posix_setsid ( void )``

使当前进程成为会话的领导者。

返回值：

返回会话ID，或者返回错误-1。

PHP多进程实现方式
=================
直接方式
----------

.. code-block:: php

    <?php
    // example of multiple processes
    date_default_timezone_set('Asia/Chongqing');
    echo "parent start, pid ", getmypid(), "\n";
    beep();
    for ($i = 0; $i < 3; ++$i) {
        $pid = pcntl_fork(); // 创建了三个子进程
        if ($pid == -1) {
            die ("cannot fork");
        } else if ($pid > 0) {
            echo "parent continue \n";
            for ($k = 0; $k < 2; ++$k) {
                beep(); // 父进程执行2次输出后，再次创建子进程
            }
        } else if ($pid == 0) {
            echo "child start, pid ", getmypid(), "\n";
            for ($j = 0; $j < 5; ++$j) {
                beep();
            }
            exit; //子进程执行5次输出后退出
        }
    }

    function beep () {
        echo getmypid(), "\t", date('Y-m-d H:i:s', time()), "\n";
        sleep(1);
    }
    ?>

阻塞方式
----------
用直接方式，父进程创建了子进程后，并没有等待子进程结束，而是继续运行。似乎这里看不到有什么问题。如果php脚本并不是运行完后自动结束，而是常驻内存的，就会造成子进程无法回收的问题。也就是僵尸进程。可以通过pcntl_wai()方法等待进程结束，然后回收已经结束的进程。

.. code-block:: php

    <?php
    $pid = pcntl_fork();
    if ($pid == -1){
        ...
    } else if ($pid > 0){
        echo "parent continue \n";
        pcntl_wait($status); // 阻塞，等待子进程结束
        for ($k = 0; $k < 2; ++$k) {
            beep(); // 等子进程结束后父进程执行2次输出后，再次创建子进程
        }
    } else if ($pid == 0){
        ...
    }

    function beep () {
        echo getmypid(), "\t", date('Y-m-d H:i:s', time()), "\n";
        sleep(1);
    }
    ?>

非阻塞方式
------------
阻塞方式失去了多进程的并行性。还有一种方法，既可以回收已经结束的子进程，又可以并行。这就是非阻塞的方式。

.. code-block:: php

    <?php
    // example of multiple processes
    date_default_timezone_set('Asia/Chongqing');
    // 表示每执行一条低级指令，就检查一次信号，如果检测到注册的信号，就调用其信号处理器。
    //declare (ticks = 1); // 5.3以下的版本
    // 注册信号处理函数，在一个进程终止或者停止时，将SIGCHLD信号发送给其父进程
    pcntl_signal(SIGCHLD, "garbage");
    echo "parent start, pid ", getmypid(), "\n";
    beep();
    for ($i = 0; $i < 3; ++$i) {
        $pid = pcntl_fork();
        if ($pid == -1) {
            die ("cannot fork");
        } else if ($pid > 0) {
            echo "parent continue \n";
            for ($k = 0; $k < 2; ++$k) {
                beep();
            }
        } else if ($pid == 0) {
            echo "child start, pid ", getmypid(), "\n";
            for ($j = 0; $j < 5; ++$j) {
                beep();
            }
            exit (0);
        }
    }
    // 父进程不退出，等待所有子进程结束
    while (1) {
        // do something else
        //5.3及以上版本
        pcntl_signal_dispatch();// 接收到信号时，调用注册的signalHandler()
        sleep(5);
    }
    // 信号处理函数
    function garbage ($signal) {
        echo "signel $signal received\n";

        // 等待所有孩子进程，如果没有孩子退出，立即返回
        while (($pid = pcntl_waitpid(-1, $status, WNOHANG)) > 0) {
            echo "\t child end pid $pid , status $status\n";
        }
    }

    function beep () {
        echo getmypid(), "\t", date('Y-m-d H:i:s', time()), "\n";
        sleep(1);
    }
    ?>

windows下多线程
-------------------
windows系统不支持pcntl函数，幸好有curl_multi_exec()这个工具，利用内部的多线程，访问多个链接，每个链接可以作为一个任务。

脚本test1.php

.. code-block:: php

    <?php
    date_default_timezone_set('Asia/Chongqing');
    $tasks = array(
        'http://www.demo.com/demo/test2.php?job=task1',
        'http://www.demo.com/demo/test2.php?job=task2',
        'http://www.demo.com/demo/test2.php?job=task3'
    );
    $mh = curl_multi_init();
    foreach ($tasks as $i => $task) {
        $ch[$i] = curl_init();
        curl_setopt($ch[$i], CURLOPT_URL, $task);
        curl_setopt($ch[$i], CURLOPT_RETURNTRANSFER, 1);
        curl_multi_add_handle($mh, $ch[$i]);
    }
    do {
        $mrc = curl_multi_exec($mh, $active);
    } while ($mrc == CURLM_CALL_MULTI_PERFORM);
    while ($active && $mrc == CURLM_OK) {
        if (curl_multi_select($mh) == -1) {
            //if it returns -1, wait a bit, but go forward anyways!
            usleep(100);
        }

        do {
            $mrc = curl_multi_exec($mh, $active);
        } while ($mrc == CURLM_CALL_MULTI_PERFORM);

    }
    // completed, checkout result
    foreach ($tasks as $j => $task) {
        if (curl_error($ch[$j])) {
            echo "task ${j} [$task ] error ", curl_error($ch[$j]), "\r\n";
        } else {
            echo "task ${j} [$task ] get: \r\n", curl_multi_getcontent($ch[$j]), "\r\n";
        }
    }
    ?>

脚本test2.php

.. code-block:: php

    <?php
    date_default_timezone_set( 'Asia/Chongqing');
    echo "child start, pid ", getmypid(), "\r\n" ;
    for ($i=0; $i<5; ++$i){
        beep();
    }
    exit (0);

    function beep(){
        echo getmypid(), "\t" , date('Y-m-d H:i:s' , time()), "\r\n";
        sleep(1);
    }
    ?>

PHP基于文件锁解决多进程同时读写一个文件问题
========================================
首先PHP是支持进程的而不支持多线程（这个先搞清楚了），如果是对于文件操作，其实你只需要给文件加锁就能解决，不需要其它操作，PHP的flock已经帮你搞定了。

用flock在写文件前先锁上，等写完后解锁，这样就实现了多线程同时读写一个文件避免冲突。大概就是下面这个流程。

.. code-block:: php

    <?php
    /*
    *flock(file,lock,block)
    *file 必需，规定要锁定或释放的已打开的文件
    *lock 必需。规定要使用哪种锁定类型。
    *block 可选。若设置为 1 或 true，则当进行锁定时阻挡其他进程。
    *lock
    *LOCK_SH 要取得共享锁定（读取的程序）
    *LOCK_EX 要取得独占锁定（写入的程序）
    *LOCK_UN 要释放锁定（无论共享或独占）
    *LOCK_NB 如果不希望 flock() 在锁定时堵塞
    */
    if (flock($file, LOCK_EX)) {
        fwrite($file, 'write more words');
        flock($file, LOCK_UN);
    } else {
        // 处理错误逻辑
    }
    fclose($file);
    ?>


守护进程
========
守护进程概述
---------------
守护进程，也就是通常所说的daemon进程，是Linux中的后台服务进程。它是一个生存期较长的进程，通常独立于控制终端并且周期性地 执行某种任务或等待处理某些发生的事件。守护进程常常在系统引导载入时启动，在系统关闭时终止。Linux有很多系统服务，大多数服务都是通过守护进程实 现的。同时，守护进程还能完成许多系统任务，例如，作业规划进程crond、打印进程lqd等（这里的结尾字母d就是daemon的意思）。

由于在Linux中，每一个系统与用户进行交流的界面称为终端，每一个从此终端开始运行的进程都会依附于这个终端，这个终端称为这些进程的 控制终端，当控制终端被关闭时，相应的进程都会自动关闭。但是守护进程却能够突破这种限制，它从被执行开始运转，直到接收到某种信号或者整个系统关闭时才 会退出。如果想让某个进程不因为用户、终端或者其他的变化而受到影响，那么就必须把这个进程变成一个守护进程。可见，守护进程是非常重要的。

编写守护进程
----------------
编写守护进程看似复杂，但实际上也是遵循一个特定的流程，只要将此流程掌握了，就能很方便地编写出自己的守护进程。下面就分4个步骤来讲解怎样创建一个简单的守护进程。在讲解的同时，会配合介绍与创建守护进程相关的几个系统函数，希望读者能很好地掌握。

1. 创建子进程，父进程退出。这是编写守护进程的第一步。由于守护进程是脱离控制终端的，因此，完成第一步后就会在shell终端造成一 种程序已经运行完毕的假象，之后的所有工作都在子进程中完成，而用户在shell终端则可以执行其他的命令，从而在形式上做到与控制终端的脱离。

   到这里，有心的读者可能会问，父进程创建了子进程后退出，此时该子进程不就没有父进程了吗？守护进程中确实会出现这么一个有趣的现象：由于 父进程已经先于子进程退出，就会造成子进程没有父进程，从而变成一个孤儿进程。在Linux中，每当系统发现一个孤儿进程时，就会自动由1号进程（也就是 init进程）收养它，这样，原先的子进程就会变成init进程的子进程。其关键代码如下：

   .. code-block:: php

    <?php
    pid = fork();
    if (pid > 0){
        exit(0); /* 父进程退出 */
    }
    ?>

2. 在子进程中创建新会话。这个步骤是创建守护进程最重要的一步，虽然实现非常简单，但意义却非常重大。在这里使用的是系统函数setsid()，在具体介绍setsid()之前，读者首先要了解两个概念：进程组和会话期。

   进程组。进程组是一个或多个进程的集合。进程组由进程组ID来唯一标识。除了进程号（PID）之外，进程组ID也是一个进程的必备属性。

   每个进程组都有一个组长进程，其组长进程的进程号等于进程组ID，且该进程ID不会因组长进程的退出而受到影响。

   会话期。会话组是一个或多个进程组的集合。通常，一个会话开始于用户登录，终止于用户退出，在此期间该用户运行的所有进程都属于这个会话期。

   接下来就可以具体介绍setsid()的相关内容。

   setsid()函数的作用。setsid()函数用于创建一个新的会话组，并担任该会话组的组长。调用setsid()有以下3个作用：

   - 让进程摆脱原会话的控制。
   - 让进程摆脱原进程组的控制。
   - 让进程摆脱原控制终端的控制。

   那么，在创建守护进程时为什么要调用setsid()函数呢？读者可以回忆一下创建守护进程的第一步， 在那里调用了fork()函数来创建子进程再令父进程退出。由于在调用fork()函数时，子进程全盘复制了父进程的会话期、进程组和控制终端等，虽然父 进程退出了，但原先的会话期、进程组和控制终端等并没有改变，因此，还不是真正意义上的独立。而setsid()函数能够使进程完全独立出来，从而脱离所 有其他进程的控制。

3. 改变当前目录为根目录。这一步也是必要的步骤。使用fork()创建的子进程继承了父进程的当前工作目录。由于在进程运行过程中，当 前目录所在的文件系统（如“/mnt/usb”等）是不能卸载的，这对以后的使用会造成诸多的麻烦（如系统由于某种原因要进入单用户模式）。因此，通常的 做法是让“/”作为守护进程的当前工作目录，这样就可以避免上述问题。当然，如有特殊需要，也可以把当前工作目录换成其他的路径，如/tmp。改变工作目 录的常见函数是chdir()。

4. 重设文件权限掩码。文件权限掩码是指屏蔽掉文件权限中的对应位。例如，有一个文件权限掩码是050，它就屏蔽了文件组拥有者的可读与 可执行权限。由于使用fork()函数新建的子进程继承了父进程的文件权限掩码，这就给该子进程使用文件带来了诸多的麻烦。因此，把文件权限掩码设置为 0，可以大大增强该守护进程的灵活性。设置文件权限掩码的函数是umask()。在这里，通常的使用方法为umask(0)。

5. 关闭文件描述符。同文件权限掩码一样，用fork()函数新建的子进程会从父进程那里继承一些已经打开的文件。这些被打开的文件可能永远不会被守护进程读或写，但它们一样消耗系统资源，而且可能导致所在的文件系统无法被卸载。

.. code-block:: php

    <?php
    /**
     * 需要进行的作业
     */
    function worker () {
        $pid = pcntl_fork();
        if ($pid == -1) {
            exit('fork error');
        }
        if ($pid == 0) {
            for ($i = 0; $i < 50; $i++) {
                file_put_contents("log", "hello {$i}\n", FILE_APPEND);
                sleep(1);
            }
        }
    }

    /**
     * 子进程
     */
    function children () {
        $sid = posix_setsid();
        echo $sid;
        for ($i = 0; $i < 2; $i++) {
            worker();
        }
        sleep(100);
    }

    $pid = pcntl_fork();

    if ($pid == -1) {
        exit('fork error');
    }

    if ($pid == 0) {
        children();
    } else {
        exit('parent exit');
    }
    ?>

上面的代码就是子进程再次fork两个子进程来执行任务。是每隔1s向log里面写入一行文字。

上面的代码中，在children进程我们称之为master进程，下面有两个worker进程。函数里面使用了sleep(100)是为了不让master进程马上就退出，如果该进程退出了，那么这个进程下面的子进程则又会成为孤儿进程。

实际过程中，肯定不能使用sleep()函数不让master进程退出的，需要使用pcntl_wait($status,WUNTRACED);来等待子进程的信号。



http://www.jb51.net/article/84367.htm
http://www.jb51.net/article/71238.htm