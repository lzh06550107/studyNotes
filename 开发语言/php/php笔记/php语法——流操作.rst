******
stream
******

.. contents:: 目录
   :depth: 4

PHP Streams是内置核心操作，它用于统一文件、网络、数据压缩等类文件操作方式，并为这些类文件操作提供一组通用的函数接口。在其最简单的定义中，流是一种表现流式行为的资源对象。也就是说，它可以以线性方式读取或写入，并且可能能够（fseek）到流中的任意位置。

包装器是附加的代码，告诉流如何处理特定的协议/编码。例如，http包装器知道如何将URL转换为远程服务器上的文件的HTTP / 1.0请求。默认情况下，PHP中有许多包装器（见支持的协议和封装协议），另外，可以使用 ``stream_wrapper_register()`` 在PHP脚本中添加额外的自定义包装器，也可以直接对API 接口进行扩展 。因为任何各种各样的包装可能被添加到PHP，他们可以做什么没有限制。要访问当前注册的包装器列表，请使用stream_get_wrappers()。

.. code-block:: php

    <?php
    print_r(stream_get_wrappers());
    /*
    Array
    (
        [0] => php
        [1] => file // 默认包装器
        [2] => glob
        [3] => data
        [4] => http
        [5] => ftp
        [6] => zip
        [7] => compress.zlib
        [8] => compress.bzip2
        [9] => https
        [10] => ftps
        [11] => phar
    )
    */
    ?>

引用一个流格式为： ``scheme：// target``

- scheme：要使用的包装的名称。包括：file，http，https，ftp，ftps，compress.zlib，compress.bz2和php。有关PHP内置包装器的列表，请参阅支持的协议和包装器。如果没有指定包装，则使用函数default（通常是file：//）。
- target：取决于使用的包装。对于文件系统相关的流，这通常是所需文件的路径和文件名。对于网络相关的流，这通常是一个主机名，通常附加一个路径。同样，请参阅支持的协议和包装以获取内置流目标的描述。

PHP还可以通过context和filter对包装类进行修饰和增强。

流过滤器
========
当从数据流读取数据或写入数据流时，过滤器是可以对数据执行操作的最后一段代码。任何数量的过滤器都可以堆叠在流上。可以使用stream_filter_register()在PHP脚本中定义自定义过滤器，也可以在使用流的API参考扩展中定义自定义过滤器。要访问当前注册的过滤器列表，请使用stream_get_filters()。

.. code-block:: php

    <?php
    print_r(stream_get_filters());
    /*
    Array
    (
        [0] => convert.iconv.*
        [1] => mcrypt.*
        [2] => mdecrypt.*
        [3] => string.rot13
        [4] => string.toupper
        [5] => string.tolower
        [6] => string.strip_tags
        [7] => convert.*
        [8] => consumed
        [9] => dechunk
        [10] => zlib.*
        [11] => bzip2.*
    )
    */
    ?>

流上下文
========
上下文是修改或增强流行为的一组参数(所有协议的通用设置)和包装器特定的选项(具体协议的设置)。上下文是使用stream_context_create()创建的，可以传递给大多数文件系统相关的流创建函数（即fopen()，file()，file_get_contents()等）。

可以在调用stream_context_create()时指定选项，或者在以后使用stream_context_set_option()指定选项。在上下文选项和参数章节中可以找到特定包装器选项列表(http://php.net/manual/en/context.php)。

可以使用stream_context_set_params()函数为上下文指定参数。

流错误
======
与任何文件或套接字相关的功能一样，流上的操作可能由于各种正常原因而失败（如，无法连接到远程主机，未找到文件等）。与流相关的调用同样也可能失败，因为所需的流未在正在运行的系统上注册。请参阅stream_get_wrappers()返回的数组，以获取PHP安装支持的流的列表。与大多数PHP内部函数一样，如果发生故障，将会生成一个E_WARNING消息来描述错误的性质。

函数详解
========

stream_context_create()
-----------------------------
``resource stream_context_create ([ array $options [, array $params ]] )``

使用选项中提供的任何选项创建并返回流上下文。

参数：

- options：必须是格式为$ arr ['wrapper'] ['option'] = $ value的关联数组的关联数组。有关可用包装和选项的列表，请参阅上下文选项(http://php.net/manual/en/context.php)。
- params：必须是格式为$ arr ['parameter'] = $ value的关联数组。有关标准流参数的列表，请参阅上下文参数(http://php.net/manual/en/context.params.php)。

返回值：

一个流上下文资源。

stream_context_set_default()
-----------------------------------
设置当文件操作（fopen()，file_get_contents()等等）在没有上下文参数的情况下被调用的默认流上下文。使用与stream_context_create()相同的语法。

参数：

- options：为默认上下文设置的选项。

返回值：

返回默认流上下文。

stream_context_set_option()
----------------------------------
``bool stream_context_set_option ( resource $stream_or_context , array $options )``

在指定的上下文中设置一个选项。值被设置为包装器的选项。

参数：

- stream_or_context：要应用选项的流或上下文资源。
- options：stream_or_context设置的选项。

.. note:: 选项必须是$ arr ['wrapper'] ['option'] = $ value格式的关联数组的关联数组。

返回值：

成功返回TRUE，失败则返回FALSE。

stream_context_set_params()
-----------------------------------
``bool stream_context_set_params ( resource $stream_or_context , array $params )``

在指定的上下文中设置参数。

参数：

- stream_or_context：也应用参数的流或上下文。
- params：要设置的参数数组。

.. note:: params应该是关联数组结构，如：$params ['paramname'] =“paramvalue”;.

返回值：

成功返回TRUE，失败则返回FALSE。

stream_context_get_default()
-----------------------------------
``resource stream_context_get_default ([ array $options ] )``

返回在没有上下文参数的情况下，调用文件操作（fopen()，file_get_contents()等）时使用的默认流上下文。可以使用与stream_context_create()相同的语法来指定默认上下文的选项。该函数和设置默认选项的函数一样，只是它返回上下文资源。

参数：

- options：stream_or_context设置的选项。

.. note:: 选项必须是$arr ['wrapper'] ['option'] = $value格式的关联数组的关联数组。

返回值：

一个流上下文资源。

stream_context_get_options()
-----------------------------------
``array stream_context_get_options ( resource $stream_or_context )``

返回指定流或上下文中的选项数组。

参数：

- stream_or_context：从中获取选项的流或上下文。

stream_context_get_params()
-----------------------------------
``array stream_context_get_params ( resource $stream_or_context )``

从流或上下文中检索参数和选项信息。

参数：

- stream_or_context：从中获取选项的流或上下文。

stream_copy_to_stream()
------------------------------
``int stream_copy_to_stream ( resource $source , resource $dest [, int $maxlength = -1 [, int $offset = 0 ]] )``

从源地址到目的地，从当前位置（或从偏移位置，如果指定）复制最大长度的数据字节。如果未指定maxlength，则将复制源中的所有剩余内容。

参数：

- source：源流；
- dest：目的流；
- maxlength：复制的最大字节；
- offset：从哪里开始复制数据的偏移量；

返回值：

返回复制的字节总数，失败时返回FALSE。

stream_encoding()
---------------------
``bool stream_encoding ( resource $stream [, string $encoding ] )``

为流编码设置字符集。


stream_filter_register()
-----------------------------
``bool stream_filter_register ( string $filtername , string $classname )``

stream_filter_register()允许你在所有其他文件系统函数（如fopen()，fread()等）使用的注册流上实现你自己的过滤器。

参数：

- filtername：要注册的过滤器名称。
- classname：要实现一个过滤器，你需要定义一个类作为php_user_filter的一个扩展，并带有许多成员函数。当对附加过滤器的流执行读/写操作时，PHP将传递数据通过过滤器（以及附加到该流的任何其他过滤器），以便可以根据需要修改数据。您必须完全按照php_user_filter中所述的方式来实现这些方法 - 否则会导致未定义的行为。

返回值：

成功返回TRUE，失败则返回FALSE。如果filtername已经定义，stream_filter_register()将返回FALSE。


stream_filter_append()
---------------------------
``resource stream_filter_append ( resource $stream , string $filtername [, int $read_write [, mixed $params ]] )``

将filtername添加到流中的过滤器列表。

参数：

- stream：目标流；
- filtername：过滤器名称；
- read_write：默认情况下，如果打开文件（即文件模式：r和/或+），则stream_filter_append()将过滤器追加到读取过滤器链。如果打开文件（即文件模式：w，a和/或+），则过滤器也将附加到写入过滤器链中。 STREAM_FILTER_READ，STREAM_FILTER_WRITE和/或STREAM_FILTER_ALL也可以传递给read_write参数来覆盖此行为。
- params：带指定参数的过滤器将会追加到列表的末尾且在流操作的最后调用，如果想在列表的开头增加一个过滤器，使用stream_filter_prepend()。

返回值：

成功返回资源，失败时返回FALSE。在调用stream_filter_remove()期间，该资源可用于引用此过滤器实例。

如果流不是资源或者无法找到filtername，则返回FALSE。

.. note:: 使用自定义（用户）过滤器时为了将所需的用户过滤器注册到filtername，必须首先调用stream_filter_register()。

stream_filter_prepend()
----------------------------
``resource stream_filter_prepend ( resource $stream , string $filtername [, int $read_write [, mixed $params ]] )``

将filtername添加到流中的过滤器列表开头。此过滤器将与指定的参数一起添加到列表的开头，因此将在流操作期间首先被调用。

参数同上个函数。

.. note:: 使用自定义（用户）过滤器时为了将所需的用户过滤器注册到filtername，必须首先调用stream_filter_register()。

stream_filter_remove()
----------------------------
``bool stream_filter_remove ( resource $stream_filter )``

移除先前使用stream_filter_prepend()或stream_filter_append()添加到流中的流过滤器。保留在过滤器内部缓冲区中的任何数据在移除之前将被刷新到下一个过滤器。

参数：

- stream_filter：要移除的流过滤器。

返回值：

成功返回TRUE，失败则返回FALSE。

stream_get_filters()
------------------------
``array stream_get_filters ( void )``

检索正在运行的系统上已注册的过滤器列表。

返回值：

返回包含可用的所有流过滤器名称的索引数组。

stream_wrapper_register()
-------------------------------
``bool stream_wrapper_register ( string $protocol , string $classname [, int $flags = 0 ] )``

允许您实现自己的协议处理程序和流，以便与所有其他文件系统函数（如fopen()，fread()等）一起使用。

参数：

- protocol：将被注册的包装器名称。
- classname：实现协议的类名。
- flags：如果protocol是一个URL协议，应该设置为STREAM_IS_URL。默认为0，本地流。

返回值：

成功返回TRUE，失败则返回FALSE。

如果协议已经有一个处理程序，stream_wrapper_register()将返回FALSE。

stream_wrapper_unregister()
----------------------------------
``bool stream_wrapper_unregister ( string $protocol )``

允许您禁用已经定义的流包装器。一旦封装器被禁用，您可以使用stream_wrapper_register()用用户定义的封装器覆盖它，或者稍后通过stream_wrapper_restore()重新启用封装器。

stream_wrapper_restore()
-------------------------------
``bool stream_wrapper_restore ( string $protocol )``

恢复以前使用stream_wrapper_unregister()注销的内置包装器。

stream_get_wrappers()
---------------------------
``array stream_get_wrappers ( void )``

检索正在运行的系统上可用的注册包装器列表。

返回值：

返回包含正在运行的系统上可用的所有流包装器名称的索引数组。

stream_get_transports()
-----------------------------
``array stream_get_transports ( void )``

返回一个索引数组，其中包含正在运行的系统上可用的所有套接字传输的名称。

stream_get_contents()
---------------------------
``string stream_get_contents ( resource $handle [, int $maxlength = -1 [, int $offset = -1 ]] )``

与file_get_contents()相同，不同之处在于stream_get_contents()在已打开的流资源上运行，并返回字符串中剩余的内容，最大长度为maxlength字节，并从指定的偏移量开始。

参数：

- handle：流资源（例如，从fopen()返回）
- maxlength：读取的最大字节数。默认为-1（读取所有剩余的缓冲区）。
- offset：阅读前寻求指定的偏移量。如果这个数字是负数，则不会发生查找，将从当前位置开始读取数据。

返回值：

返回字符串或失败时返回FALSE。

stream_get_line()
---------------------
``string stream_get_line ( resource $handle , int $length [, string $ending ] )``

从给定的资源句柄中获取一行。

当已经读取指定长度字节时或者当找到由 ending指定的字符串（不包括在返回值中）或EOF（以先到者为准）时读取结束。

这个函数和fgets()几乎是一样的，只是它允许除标准的\\ n，\\ r和\\r \\ n之外的行结束符，并且不返回分隔符本身。

参数：

- handle：一个有效的文件句柄。
- length：从句柄读取的字节数。
- ending：一个可选的字符串分隔符。

返回值：

返回从句柄指向的文件中读取的最多长度字节的字符串。如果发生错误，则返回FALSE。

stream_get_meta_data()
-----------------------------
``array stream_get_meta_data ( resource $stream )``

返回有关现有流的信息。从流/文件指针中检索头/元数据。

参数：

- stream：该流可以是由fopen()，fsockopen()和pfsockopen()创建的任何流。

返回值：

结果数组包含以下项目：

- timed_out：如果数据流在等待最后一次调用fread()或fgets()的数据时超时，则为TRUE。
- blocked：如果数据流处于阻塞IO模式，则为TRUE。请参阅stream_set_blocking()。
- eof：如果数据流已达到文件结尾，则为TRUE。请注意，对于套接字流，即使unread_bytes不为零，该成员也可以为TRUE。要确定是否有更多数据要读取，请使用feof()而不是读取此项目。
- unread_bytes：当前包含在PHP自己的内部缓冲区中的字节数。
- stream_type：描述流的底层实现的标签。
- wrapper_data：封装附加到这个流的特定数据。有关包装器及其包装器数据的更多信息，请参阅支持的协议和包装器(http://php.net/manual/en/wrappers.php)。
- mode：该流所需的访问类型（请参阅fopen()参考 http://php.net/manual/en/function.fopen.php ）
- seekable：是否可以查找当前的流。
- uri：与此流关联的URI /文件名。

stream_is_local()
--------------------
``bool stream_is_local ( mixed $stream_or_url )``

检查流或URL是否是本地的。

参数：

- stream_or_url：要检查的流资源或URL。

返回值：

成功返回TRUE，失败则返回FALSE。

stream_notification_callback()
------------------------------------
``void stream_notification_callback ( int $notification_code , int $severity , string $message , int $message_code , int $bytes_transferred , int $bytes_max )``

通知上下文参数使用的可调用函数，在事件期间调用。

参数：

- notification_code：其中一个STREAM_NOTIFY_ \*通知常量。
- severity：STREAM_NOTIFY_SEVERITY_ \*通知常量之一。
- message：如果事件描述性消息可用则传递。
- message_code：如果事件描述性消息代码使用则传递。此值的含义取决于使用的特定包装。
- bytes_transferred：如果可用，将会填充bytes_transferred。
- bytes_max：如果可用，bytes_max将被填充。

stream_resolve_include_path()
------------------------------------
``string stream_resolve_include_path ( string $filename )``

根据与fopen()/ include相同的规则，对包含路径解析filename。仅仅是解析文件名，不会包含文件内容。

参数：

- filename：要解析的文件名。

返回值：

返回一个已解析的文件的绝对文件名的字符串，或者在失败时返回FALSE。

stream_set_chunk_size()
------------------------------
``int stream_set_chunk_size ( resource $fp , int $chunk_size )``

设置流块大小。

参数：

- fp：要设置的目标流。
- chunk_size：所需的新块大小。

返回值：

返回成功之前的块大小。如果chunk_size小于1或大于PHP_INT_MAX，将返回FALSE。

stream_set_timeout()
-------------------------
``bool stream_set_timeout ( resource $stream , int $seconds [, int $microseconds = 0 ] )``

设置流中的超时值，以秒和微秒之和表示。

当流超时时，由stream_get_meta_data()返回的数组的'timed_out'键被设置为TRUE，尽管没有产生错误/警告。

参数：

- stream：要设置的目标流。
- seconds：超时的秒部分被设置。
- microseconds：要设置的超时的微秒部分。

返回值：

成功返回TRUE，失败则返回FALSE。

.. note:: 此函数不适用于像stream_socket_recvfrom()这样的高级操作，而是适用于具有timeout参数的stream_select()。

stream_set_read_buffer()
-------------------------------
``int stream_set_read_buffer ( resource $stream , int $buffer )``

在给定的流上设置读取文件缓冲。这类似于stream_set_write_buffer()，但是用于读取操作。

参数：

- stream：流资源。
- buffer：要缓冲的字节数。如果缓冲区是0，那么读操作是无缓冲的。这可以确保所有使用fread()的读取在其他进程被允许从输入流读取之前完成。

返回值：

成功时返回0，或者如果请求无法设置，则返回其他值。

stream_set_write_buffer()
--------------------------------
``int stream_set_write_buffer ( resource $stream , int $buffer )``

将给定流上的写入操作的缓冲设置为缓冲区字节。

参数：

- stream：流资源。
- buffer：要缓冲的字节数。如果缓冲区是0，那么读操作是无缓冲的。这可以确保所有使用fwrite()的写入在其他进程被允许从输入流写入之前完成。

返回值：

成功时返回0，或者如果请求无法设置，则返回其他值。

stream_supports_lock()
---------------------------
``bool stream_supports_lock ( resource $stream )``

判断该流是否支持通过flock()进行锁定。

stream_socket_enable_crypto()
-------------------------------------
``mixed stream_socket_enable_crypto ( resource $stream , bool $enable [, int $crypto_type [, resource $session_stream ]] )``

启用或禁用流上的加密。

一旦建立了加密设置，就可以通过在启用参数中传递TRUE或FALSE动态地开启和关闭加密。

参数：

- stream：流资源。
- enable：启用/禁用流上的加密。
- crypto_type：在流上设置加密。有效的方法是：

  + STREAM_CRYPTO_METHOD_SSLv2_CLIENT
  + STREAM_CRYPTO_METHOD_SSLv3_CLIENT
  + STREAM_CRYPTO_METHOD_SSLv23_CLIENT
  + STREAM_CRYPTO_METHOD_ANY_CLIENT
  + STREAM_CRYPTO_METHOD_TLS_CLIENT
  + STREAM_CRYPTO_METHOD_TLSv1_0_CLIENT
  + STREAM_CRYPTO_METHOD_TLSv1_1_CLIENT
  + STREAM_CRYPTO_METHOD_TLSv1_2_CLIENT
  + STREAM_CRYPTO_METHOD_SSLv2_SERVER
  + STREAM_CRYPTO_METHOD_SSLv3_SERVER
  + STREAM_CRYPTO_METHOD_SSLv23_SERVER
  + STREAM_CRYPTO_METHOD_ANY_SERVER
  + STREAM_CRYPTO_METHOD_TLS_SERVER
  + STREAM_CRYPTO_METHOD_TLSv1_0_SERVER
  + STREAM_CRYPTO_METHOD_TLSv1_1_SERVER
  + STREAM_CRYPTO_METHOD_TLSv1_2_SERVER

  如果省略，则将使用流的SSL上下文中的crypto_type上下文选项。
- session_stream：使用session_stream中的设置对流进行播种。

返回值：

如果协商成功，则返回TRUE;如果协商失败，则返回FALSE;如果没有足够的数据，则返回0，并且应该再次尝试（仅适用于非阻塞套接字）。

stream_socket_client()
---------------------------
``resource stream_socket_client ( string $remote_socket [, int &$errno [, string &$errstr [, float $timeout = ini_get("default_socket_timeout") [, int $flags = STREAM_CLIENT_CONNECT [, resource $context ]]]]] )``

初始化到remote_socket指定的目的地的流或数据报连接。创建的套接字类型由使用标准URL格式指定的传输确定：transport：// target。对于诸如TCP和UDP的Internet域套接字（AF_INET），remote_socket参数的目标部分应由主机名或IP地址，后跟冒号和端口号组成。对于Unix域套接字，目标部分应指向文件系统上的套接字文件。

.. note:: 该流将默认在阻塞模式下打开。您可以使用stream_set_blocking()将其切换到非阻塞模式。

参数：

- remote_socket：到套接字要连接的地址。
- errno：如果连接失败，将被设置为系统级错误号。
- errstr：如果连接失败，将被设置为系统级错误消息。
- timeout：connect()系统调用应该超时的秒数。此参数仅适用于未进行异步连接尝试的情况。

  要在套接字上设置读写数据的超时时间，请使用stream_set_timeout()，因为该超时只适用于连接套接字。
- flags：位掩码字段可以设置为连接标志的任意组合。目前，连接标志的选择仅限于STREAM_CLIENT_CONNECT（默认），STREAM_CLIENT_ASYNC_CONNECT和STREAM_CLIENT_PERSISTENT。
- context：使用stream_context_create()创建的有效上下文资源。

返回值：

在成功时返回一个流资源，它可以和其他文件函数（如fgets()，fgetss()，fwrite()，fclose()和feof()）一起使用，失败时为FALSE。

失败时，errno和errstr参数将填充系统级connect()调用中发生的实际系统级错误。如果在errno中返回的值是0并且函数返回FALSE，则表明错误发生在connect()调用之前。这很可能是由于初始化套接字的问题。请注意，errno和errstr参数将始终通过引用传递。

.. note:: 当指定数字IPv6地址（例如fe80 :: 1）时，必须将IP封装在方括号中，例如tcp：// [fe80 :: 1]：80。

stream_socket_server()
----------------------------
``resource stream_socket_server ( string $local_socket [, int &$errno [, string &$errstr [, int $flags = STREAM_SERVER_BIND | STREAM_SERVER_LISTEN [, resource $context ]]]] )``

在指定的local_socket上创建一个流或数据报套接字。

这个函数只创建一个套接字，开始接受连接使用stream_socket_accept()。

参数：

- local_socket：创建的套接字类型由使用标准URL格式指定的传输决定：transport：// target。

  对于诸如TCP和UDP的Internet域套接字（AF_INET），remote_socket参数的target部分应由主机名或IP地址，后跟冒号和端口号组成。对于Unix域套接字，target部分应指向文件系统上的套接字文件。

  根据环境，Unix域套接字可能不可用。可用的传输列表可以使用stream_get_transports()来检索。请参阅支持的套接字传输列表了解传输传输列表(http://php.net/manual/en/transports.php)。
- errno：如果存在可选的errno和errstr参数，则它们将被设置为指示在系统级socket()，bind()和listen()调用中发生的实际系统级错误。如果在errno中返回的值是0并且函数返回FALSE，则表明错误发生在bind()调用之前。这很可能是由于初始化套接字的问题。请注意，errno和errstr参数将始终通过引用传递。
- errstr：errno描述。
- flags：可以设置为套接字创建标志的任意组合的位掩码字段。对于UDP套接字，您必须使用STREAM_SERVER_BIND作为flags参数。
- context：流上下文。

返回值：

返回创建的流，或者错误返回FALSE。

stream_socket_accept()
----------------------------
``resource stream_socket_accept ( resource $server_socket [, float $timeout = ini_get("default_socket_timeout") [, string &$peername ]] )``

在先前由stream_socket_server()创建的套接字上接受连接。

参数：

- server_socket：服务器接受连接的套接字。
- timeout：覆盖套接字接受默认超时时间。时间单位是秒。
- peername：如果包含并可从选定的传输中获得，将被设置为所连接的客户端的名称（地址）。也可以稍后使用stream_socket_get_name()来获取。

返回值：

返回接受的套接字连接的流或失败时返回FALSE。

.. note:: 这个函数不应该与UDP服务器套接字一起使用。相反，使用stream_socket_recvfrom()和stream_socket_sendto()。

stream_socket_get_name()
--------------------------------
``string stream_socket_get_name ( resource $handle , bool $want_peer )``

返回给定套接字连接的本地或远程名称。

参数：

- handle：获取名称的套接字。
- want_peer：如果设置为TRUE，则将返回远程套接字名称，如果设置为FALSE，则将返回本地套接字名称。

返回值：

套接字的名称。

stream_socket_pair()
-------------------------
``array stream_socket_pair ( int $domain , int $type , int $protocol )``

stream_socket_pair()创建一对连接的，不可区分的套接字流。这个功能通常在IPC（进程间通信）中使用。

参数：

- domain：要使用的协议族：STREAM_PF_INET，STREAM_PF_INET6或STREAM_PF_UNIX。
- type：要使用的通信类型：STREAM_SOCK_DGRAM，STREAM_SOCK_RAW，STREAM_SOCK_RDM，STREAM_SOCK_SEQPACKET或STREAM_SOCK_STREAM。
- protocol：要使用的协议：STREAM_IPPROTO_ICMP，STREAM_IPPROTO_IP，STREAM_IPPROTO_RAW，STREAM_IPPROTO_TCP或STREAM_IPPROTO_UDP。

请查阅Streams常量列表以了解每个常量的更多详细信息(http://php.net/manual/en/stream.constants.php)。

返回值：

成功返回两个套接字资源的数组，失败时返回FALSE。

stream_socket_recvfrom()
-------------------------------
``string stream_socket_recvfrom ( resource $socket , int $length [, int $flags = 0 [, string &$address ]] )``

stream_socket_recvfrom()接受来自远程套接字的数据，长度最大为length字节。

参数：

- socket：远程socket。
- length：从套接字接收的字节数。
- flags：标志的值可以是以下的任意组合：

=============  ================================================================
标志             描述
=============  ================================================================
STREAM_OOB     处理OOB（带外）数据。
STREAM_PEEK    从套接字检索数据，但不消耗缓冲区。随后调用fread()或stream_socket_recvfrom()将看到相同的数据。
=============  ================================================================

- address：如果提供地址，则会填入远程套接字的地址。

返回值：

以字符串形式返回读取的数据。

.. note:: 如果接收到的消息比长度参数长，则可能会根据从中接收消息的套接字类型（如UDP），丢弃多余的字节。

.. note:: 在调用基于缓冲区的流函数（如fread()或stream_get_line()）之后，调用基于套接字的流stream_socket_recvfrom()直接从套接字读取数据并绕过流缓冲区。

.. note:: 请注意，stream_socket_recvfrom()将绕过包含TLS / SSL的流包装器。使用fread()从加密流中读取将返回解密的数据，使用stream_socket_recvfrom()会给你原始的加密字节。

stream_socket_sendto()
-----------------------------
``int stream_socket_sendto ( resource $socket , string $data [, int $flags = 0 [, string $address ]] )``

发送消息到套接字，无论是否连接。

参数：

- socket：将数据发送到的套接字。
- data：要发送的数据。
- flags：标志的值可以是以下的任意组合：

============  ==============
标志            描述
============  ==============
STREAM_OOB    处理OOB（带外）数据。
============  ==============

- address：除非在地址中指定了备用地址，否则将使用创建套接字流时指定的地址。如果指定，则必须采用点分四（或[ipv6]）格式。

返回值：

返回的是写入到套接字的数据的字节大小，或者在失败时为-1（这可能是因为非阻塞）

stream_socket_shutdown()
-------------------------------
``bool stream_socket_shutdown ( resource $stream , int $how )``

关闭（部分或者没有）全双工连接。

.. note:: 关联的一个或多个缓冲区可能被清空，也可能不被清空。

参数：

- stream：一个打开的流（例如用stream_socket_client()打开）
- how：以下常量之一：STREAM_SHUT_RD（禁止进一步的接收），STREAM_SHUT_WR（禁止进一步的传输）或STREAM_SHUT_RDWR（禁止进一步的接收和传输）。

返回值：

成功返回TRUE，失败则返回FALSE。

stream_set_blocking()
--------------------------
``bool stream_set_blocking ( resource $stream , bool $mode )``

在流上设置阻塞或非阻塞模式。

此函数适用于支持非阻塞模式的任何流（当前是常规文件和套接字流）。

参数：

- stream：流资源。
- mode：如果mode为FALSE，给定的流将切换到非阻塞模式，如果为TRUE，则切换到阻塞模式。这将影响从流读取的fgets()和fread()等调用。在非阻塞模式下，fgets()调用将始终立即返回，而在阻塞模式下，它将等待数据流到流上可用。

返回值：

成功返回TRUE，失败则返回FALSE。

.. note:: 需要注意的是，stream_set_blocking()和stream_set_timeout()不适用于标准I / O流，如STDIN和STDOUT。

stream_select()
------------------
``int stream_select ( array &$read , array &$write , array &$except , int $tv_sec [, int $tv_usec = 0 ] )``

stream_select()函数接受数组的流并等待它们改变状态。它的操作等同于socket_select()函数的操作，除了socket_select()作用于流。

阻塞方式block，就是进程或是线程执行到这些函数时必须等待某个事件的发生，如果事件没有发生，进程或线程就被阻塞，函数不能立即返回。使用Select就可以完成非阻塞non-block，就是进程或线程执行此函数时不必非要等待事件的发生，一旦执行肯定返回，以返回值的不同来反映函数的执行情况，如果事件发生则与阻塞方式相同，若事件没有发生则返回一个代码来告知事件未发生，而进程或线程继续执行，所以效率较高。select能够监视我们需要监视的文件描述符的变化情况。

参数：

- read：读取数组中的流将被监视以查看字符是否可用于读取（更确切地说，查看读取是否不会被阻塞 - 特别是流资源在文件末尾，在这种情况下一个 fread()将返回一个零长度的字符串）。
- write：写数组中的流将被监视，看是否写没有被阻塞。
- except：该数组中的流将被监视高优先级例外（“带外”）数据到达。

  .. note:: 当stream_select()返回时，数组读，写和例外被修改，以指示哪个流资源实际上改变了状态。

  您不需要将每个数组传递给stream_select()。你可以把它放在外面，用一个空的数组或NULL来代替。另外不要忘记那些数组是通过引用传递的，并且会在stream_select()返回后被修改。

- tv_sec：tv_sec和tv_usec一起构成超时参数，tv_sec指定秒数，而tv_usec是微秒数。超时是stream_select()在返回之前等待的时间量的上限。如果tv_sec和tv_usec都设置为0，则stream_select()不会等待数据，而是立即返回，指示数据流的当前状态。

  如果tv_sec为NULL，则stream_select()可以无限制地阻塞，只有在某个观察到的流上发生事件（或者信号中断系统调用）时才会返回。

  .. note:: 使用超时值0允许您即时轮询流的状态，但是，在循环中使用0超时值不是一个好主意，因为这会导致您的脚本消耗太多的CPU时间。

  指定几秒钟的超时值会更好，但是如果需要同时检查并运行其他代码，则使用至少200000微秒的超时值将有助于减少脚本的CPU使用率。

  请记住，超时值是经过的最长时间;只要请求的流准备好，stream_select()就会返回。

- tv_usec：

返回值：

成功时stream_select()返回修改数组中包含的流资源的数量，如果在发生任何有趣的事情之前超时到期，则可能为零。出现错误返回FALSE并引发警告（如果系统调用被传入信号中断，则可能发生这种情况）。

.. note:: 有些流（比如zlib）不能被这个函数选中。

.. note:: 如果读取/写入数组中返回的流，请注意它们可能没有读取/写入您请求的全部数据量。可能只读/写了一个字节。

对于可读可写分析判断：
^^^^^^^^^^^^^^^^^^
select/poll中的读写事件
"""""""""""""""""""""""""""""""
1. 下列四个条件中的任何一个满足时，套接口准备好多：
    a. 有数据可读，专业的说法是：套接字接收缓冲区中的数据字节数大于等于套接字接收缓冲区低潮限度的当前值。可以使用套接字选项SO_RCVLOWAT来设置低潮限度，对于TCP和UDP套接字，其值缺省为1
    b. 连接的度这一半关闭，也就是说接收了FIN的TCP连接。对这样的套接字的套接字将不阻塞且返回0（即文件结束符）
    c. 套接字是一个监听套接字且已完成的连接数为非0，即连接建立后可读
    d. 有一个套接字错误待处理。对这样的套接字的读操作将不阻塞且返回一个错误（-1），errno则设置成明确的错误条件。这些待处理的错误也可以通过指定套接口选项SO_ERROR调用getsockopt来取得并清除。
2. 下列三个条件中的任一个满足时，套接字准备好写：
    a. 缓冲区可写，专业的说法是：套接字发送缓冲区中的可用字节数大于等于套接字发送缓冲区低潮限度的当前值，且或者套接字已连接或者套接字不要求连接（例如UDP套接字），对于TCP和UDP套接字，其缺省值一半为2048
    b. 连接的写这一半关闭。对这样的套接字的写操作将产生信号SIGPIPE
    c. 有一个套接字错误待处理。

epoll的读写事件
""""""""""""""""""
1. 读事件的发生条件
    a. 正常数据到达
    b. 关闭数据（FIN）到达，即关闭连接
    c. 连接错误数据（reset）到达
    d. 连接到到达时（对于监听套接字）

2. 写事件的发生
    a. 连接建立成功后可写（accept获取的套接字或者客户端建立连接的套接字）
    b. 缓冲区可写

通过上面的分别阐述，epoll的读写事件区分要比select/poll清晰一些

使用例子
=========
客户端
-------

.. code-block:: php

    <?php
    $addr = gethostbyname("www.baidu.com"); // 获取域名ip地址
    $client = stream_socket_client("tcp://$addr:80", $errno, $errorMessage);
    if ($client === false) {
        throw new UnexpectedValueException("Failed to connect: $errorMessage");
    }
    // 客户端发送消息
    fwrite($client, "GET / HTTP/1.0\r\nHost: www.baidu.com\r\nAccept: */*\r\n\r\n");
    // 接收服务端响应的消息
    echo stream_get_contents($client);
    fclose($client);
    ?>

进一步封装函数：

.. code-block:: php

    <?php
    $fp = fsockopen("www.baidu.com", 80, $errno, $errstr, 30);

    if (!$fp) {
        echo "$errstr ($errno)<br />n";
    } else {
        $out = "GET / HTTP/1.1\r\nHost: www.baidu.com\r\nAccept: */*\r\n\r\n";
        ///Send data
        fwrite($fp, $out);

        ///Receive data - in small chunks :)
        while (!feof($fp)) {
            echo fgets($fp, 128);
        }

        fclose($fp);
    }
    ?>

上面的例子创建一个tcp套接字。要创建udp套接字，必须像这样指定套接字流包装器。

``$fp = fsockopen("udp://127.0.0.1", 13, $errno, $errstr);``

服务器端
----------
tcp协议

.. code-block:: php

    <?php
    // 绑定和监听
    $server = stream_socket_server("tcp://127.0.0.1:1337", $errno, $errorMessage);
    if ($server === false) {
        throw new UnexpectedValueException("Could not bind to socket: $errorMessage");
    }
    // 循环接收连接请求
    for (;;) {
        //接收新连接
        $client = @stream_socket_accept($server);
        if ($client) {
            echo 'Connection accepted from '.stream_socket_get_name($client, false) . "n";
            stream_socket_sendto($client, "welecom to this server.\n");
            // 响应给客户端
            if (is_resource($conn)) {
                    stream_copy_to_stream($conn, $conn);
                    fclose($conn);
            }
            //上面两句等价于下面
            if (is_resource($conn)) {
                    while ($buf = fread($conn, 4096)) {
                        fwrite($conn, $buf);
                    }
                    fclose($conn);
            }
        }
    }
    ?>

上面的服务器仅仅是一个简单的echo服务器，该服务器存在问题，一个连接只能处理一次。

处理多个连接多次的服务器：

.. code-block:: php

    <?php
    // open a server on port 4444
    $server = stream_socket_server("tcp://0.0.0.0:4444", $errno, $errorMessage);

    if ($server === false) {
        die("Could not bind to socket: $errorMessage");
    }
    // 存储连接到服务器客户端socket
    $client_socks = array();
    while (true) {
        //每一次循环都把已经连接的客户端和$server的socket加入监听读数组
        $read_socks = $client_socks; // 每次清空数组
        $read_socks[] = $server;

        //start reading and use a large timeout
        if (!stream_select($read_socks, $write, $except, 300000)) {
            die('something went wrong while selecting');
        }

        //如果$server socket状态被修改，则有新的连接
        if (in_array($server, $read_socks)) {
            $new_client = stream_socket_accept($server);

            if ($new_client) {
                //print remote client information, ip and port number
                echo 'Connection accepted from ' . stream_socket_get_name($new_client, true) . "n";
                // 新的客户端socket连接加入数组
                $client_socks[] = $new_client;
                echo "Now there are total " . count($client_socks) . " clients.n";
            }

            // 处理完该改变状态的socket，需要从改变读状态数组移除
            unset($read_socks[array_search($server, $read_socks)]);
        }

        //如果改变读状态数组还有socket，则客户端有数据发送过来，需要处理
        foreach ($read_socks as $sock) {
            $data = fread($sock, 128);
            if (!$data) {
                // 如果客户端关闭连接，则
                unset($client_socks[array_search($sock, $client_socks)]);
                @fclose($sock);
                echo "A client disconnected. Now there are total " . count($client_socks) . " clients.n";
                continue;
            }
            //send the message back to client
            fwrite($sock, $data);
        }
    }
    ?>

udp协议

.. code-block:: php

    <?php
    $port = @$_SERVER['PORT'] ?: 1337;
    $server = stream_socket_server("udp://127.0.0.1:$port", $errno, $errstr, STREAM_SERVER_BIND);
    if (false === $server) {
        # Write error message to STDERR and exit, just like UNIX programs usually do
        fprintf(STDERR, "Error connecting to socket: %d %s\n", $errno, $errstr);
        exit(1);
    }
    printf("Listening on port %d\n", $port);
    for (;;) {
        while ($buf = stream_socket_recvfrom($server, 4096)) {
            echo $buf;
        }
    }
    ?>

非阻塞模式编程

通过tcp服务器接收信息，然后通关两个upd客户端来转发信息。

.. code-block:: php

    <?php
    $client1 = stream_socket_client("udp://127.0.0.1:8005", $errno, $errorMessage);
    $client2 = stream_socket_client("udp://127.0.0.1:8006", $errno, $errorMessage);
    if ($client1 === false) {
        throw new UnexpectedValueException("Failed to connect to server1: $errorMessage");
    }
    if ($client2 === false) {
        throw new UnexpectedValueException("Failed to connect to server2: $errorMessage");
    }
    $server = stream_socket_server("tcp://127.0.0.1:8000", $errno, $errorMessage);
    // 使用select必须使用非阻塞状态？？
    stream_set_blocking($client1, 0);
    stream_set_blocking($client2, 0);
    stream_set_blocking($server, 0);
    if ($server === false) {
        throw new UnexpectedValueException("Could not bind to socket: $errorMessage");
    }
    $connections = [];
    $buffers = [];
    for (;;) {
        $r = array_merge([$server], $connections); // 监听流读状态数组
        $w = [$client1, $client2]; // 监听流写数组
        $e = null;
        if (stream_select($r, $w, $e, 0, 500) > 0) {
            // 检查状态改变的读数组
            foreach ($r as $stream) {
                if ($stream === $server) { //如果存在$server，则是连接请求
                    $conn = stream_socket_accept($server);
                    $connections[] = $conn;
                } else {
                    // 如果是客户端发送数据，则读取发送的数据
                    $buf = fread($stream, 4096);
                    $buffers[(int) $client1] = isset($buffers[(int) $client1]) ? $buffers[(int) $client1] : '';
                    $buffers[(int) $client2] = isset($buffers[(int) $client2]) ? $buffers[(int) $client2] : '';
                    // 不断把客户端的数据转发出去
                    $buffers[(int) $client1] .= $buf;
                    $buffers[(int) $client2] .= $buf;
                    echo $buf;
                }
            }
            // 连接建立则可写事件触发
            foreach ($w as $stream) {
                if (isset($buffers[(int) $stream]) && strlen($buffers[(int) $stream]) > 0) {
                    $key = (int) $stream;
                    // 因为是不阻塞，所以一次可能没有写完
                    $bytesWritten = fwrite($stream, $buffers[$key], 4096);
                    // 需要继续写
                    $buffers[$key] = substr($buffers[$key], $bytesWritten);
                }
            }
        }
        // 重复判断客户端是否关闭
        foreach ($connections as $key => $conn) {
            if (feof($conn)) {
                printf("Client %s closed the connection\n", stream_socket_get_name($conn, true));
                unset($connections[$key]);
                fclose($conn);
            }
        }
    }
    ?>

多用户连接echo服务器

.. code-block:: php

    <?php
    $server = @stream_socket_server('tcp://0.0.0.0:9000', $errno, $errstr);
    stream_set_blocking($server, 0); // 设置为非阻塞
    if (false === $server) {
        fwrite(STDERR, "Error connecting to socket: $errno: $errstr\n");
        exit(1);
    }
    $connections = [];
    $buffers = [];
    for (;;) {
        $readable = $connections;
        array_unshift($readable, $server); //合并数组
        $writable = $connections;
        $except = null;
        if (stream_select($readable, $writable, $except, 0, 500) > 0) {
            // Some streams have data to read
            foreach ((array) $readable as $stream) {
                // When the server is readable this means that a client
                // connection is available. Let's accept the connection and store it
                if ($stream === $server) {
                    $client = @stream_socket_accept($stream, 0, $clientAddress);
                    $key = (int) $client;
                    if (is_resource($client)) {
                        printf("Client %s connected\n", $clientAddress);
                        stream_set_blocking($client, 0); // 设置为非阻塞
                        $connections[$key] = $client; // 记录客户端连接
                    }
                } else {
                    // One of the clients sent data, read it in a client specific buffer
                    $key = (int) $stream;
                    if (!isset($buffers[$key])) {
                        $buffers[$key] = ''; // 初始化
                    }
                    $buffers[$key] .= fread($stream, 4096); // 记录客户端发送的数据
                }
            }
            // Some streams are waiting for data
            foreach ((array) $writable as $stream) {
                $key = (int) $stream;
                // Try to write 4096 bytes, look how many bytes were really written,
                // and subtract the written bytes from this client's buffer
                // 非阻塞模式数据可能没有写完
                if (isset($buffers[$key]) && strlen($buffers[$key]) > 0) {
                    $bytesWritten = fwrite($stream, $buffers[$key], 4096);
                    $buffers[$key] = substr($buffers[$key], $bytesWritten);
                }
            }
            // Out of band data, usually not handled.
            foreach ((array) $except as $stream) {
                // Can't happen, we haven't set $except to anything
            }
        }
        // House keeping
        // Purge connections which were closed by the peer
        foreach ($connections as $key => $conn) {
            if (feof($conn)) {
                printf("Client %s closed the connection\n", stream_socket_get_name($conn, true));
                unset($connections[$key]);
                fclose($conn);
            }
        }
    }
    ?>