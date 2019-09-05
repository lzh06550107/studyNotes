*********
mysql内存分析
*********
``MySQL`` 的内存大体可以分为 共享内存 和 ``session`` 私有内存两部分

.. image:: ./images/mysql_memory.png



共享内存
========
执行如下命令，即可查询示例的共享内存分配情况：

.. code-block:: sql

	show variables where variable_name in (
	'innodb_buffer_pool_size','innodb_log_buffer_size','innodb_additional_mem_pool_size','key_buffer_size','query_cache_size'
	);

如下是内存规格为 240 M 的 RDS 实例的共享内存分配情况的查询结果：

.. code-block:: shell

	+---------------------------------+-----------------+
	| Variable_name                   | Value           |
	+---------------------------------+-----------------+
	| innodb_additional_mem_pool_size | 2097152         |
	| innodb_buffer_pool_size         | 67108864        |
	| innodb_log_buffer_size          | 1048576         |
	| key_buffer_size                 | 16777216        |
	| query_cache_size                | 0               |
	+---------------------------------+-----------------+
	共返回 5 行记录,花费 342.74 ms.

全局内存消耗（共享内存）相关参数

1. innodb_buffer_pool_size

   使用过 ``Innodb`` 的同学都知道，这块内存是 ``Innodb`` 存储引擎最重要的内存，直接关系到 ``MySQL`` 的读写性能。与 ``MyISAM`` 表只缓存索引，数据寄望于 ``OS`` 系统缓存不同。 ``Innodb`` 一般都会关闭 ``OS`` 的缓存，所有读到数据页和索引都直接存在数据库层的 ``innodb_buffer_pool`` 中的。

   ``InnoDB`` 缓冲池缓存着 ``InnoDB`` 表，索引，及其它辅助缓冲器中的数据。为了实现大容量读取操作的效率，缓冲池被分成可以容纳多行的页。为了缓存管理的效率，缓冲池被实现为页面的链接列表，很少使用的数据使用 ``LRU`` 算法的变体进行页面替换。

   其中主要包含数据页、索引页、undo 页、insert buffer、自适应哈希索引、锁信息以及数据字典等信息。在进行 ``SQL`` 读和写的操作时，首先并不是对物理数据文件操作，而是先对 ``buffer_pool`` 进行操作，然后再通过 ``checkpoint`` 等机制写回数据文件。该空间的优点是可以提升数据库的性能、加快 ``SQL`` 运行速度，缺点是故障恢复速度较慢。

   缓冲池的大小对于系统性能很重要：

   - ``InnoDB`` 使用 ``malloc()`` 方法在服务器启动时为整个缓冲池分配内存，通常，推荐 ``innodb_buffer_pool_size`` 值为系统内存的 ``50％`` 至 ``75％`` 。 ``innodb_buffer_pool_size`` 可以在服务器运行时动态配置。
   - 在具有大量内存的系统上，你可以通过将缓冲池划分为多个缓冲池实例来提高并发性，其 ``innodb_buffer_pool_instances`` 系统变量用来定义缓冲池实例的数量。
   - 缓冲池太小可能会导致过多的交换，因为页面从缓冲池中刷新后仅在短时间内可能再次需要。
   - 缓冲池太大可能会因为内存竞争而导致交换。

2. innodb_additional_mem_pool_size

   主要用于存放 ``MySQL`` 内部的数据结构和 ``Innodb`` 的数据字典，所以大小主要与表的数量有关，表越多值越大。庆幸的是这个值是可变的，如果不够用的话， ``MySQL`` 会向操作系统申请的。该值默认 ``8M`` ， ``AWS`` 所有规格都是统一的 ``2M`` ，由于这个值可以动态申请，所以我觉得 ``2M`` 应该是满足需求的。

3. innodb_log_buffer_size

   这个是 ``redolog`` 的缓冲区，为了提高性能， ``MySQL`` 每次写日志都将日志先写到一个内存 ``Buffer`` 中，然后将 ``Buffer`` 按照 ``innodb_flush_log_at_trx_commit`` 的配置刷到 ``disk`` 上。目前，我们所有实例的 ``innodb_flush_log_at_trx_commit`` 设置为了 ``1`` ，即每次事务提交都会刷新 ``Buffer`` 到磁盘，保证已经提交的事务， ``redo`` 是不会丢的。 ``AWS`` 该值也设的是1（为了保证不丢数据），这个值的大小主要影响到刷磁盘的次数，设置的过小， ``Buffer`` 容易满，就会增加 ``fsync`` 的次数，设置过大，占用内存。该值默认是 ``8M`` ， ``AWS`` 所有规格统一 ``128M`` ，我觉得目前每次提交都会刷 ``buffer`` ，所以除非有大事务的情况，一般 ``buffer`` 不太可能被占满，所以没必要开的很大， ``8M`` 应该是满足需求的。

   该空间不需要太大，因为一般情况下该部分缓存会以较快频率刷新至 redo log（Master Thread 会每秒刷新、事务提交时会刷新、其空间少于 1/2 时同样会刷新）。

   注： innodb_flush_log_trx_commit 参数对 InnoDB Log 的写入性能有非常关键的影响。该参数可以设置为 0 ， 1 ， 2 解释如下：

   - 0 ： log buffer 中的数据将以每秒一次的频率写入到 log file 中，且同时会进行文件系统到磁盘的同步操作，但是每个事务的 commit 并不会触发任何 log buffer 到 log file 的刷新或者文件系统到磁盘的刷新操作；
   - 1 ：在每次事务提交的时候将 log buffer 中的数据都会写入到 log file ，同时也会触发文件系统到磁盘的同步；
   - 2 ：事务提交会触发 log buffer 到 log file 的刷新，但并不会触发磁盘文件系统到磁盘的同步。此外，每秒会有一次文件系统到磁盘同步操作。

   此外， MySQL 文档中还提到，这几种设置中的每秒同步一次的机制，可能并不会完全确保非常准确的每秒就一定会发生同步，还取决于进程调度的问题。实际上， InnoDB 能否真正满足此参数所设置值代表的意义正常 Recovery 还是受到了不同 OS 下文件系统以及磁盘本身的限制，可能有些时候在并没有真正完成磁盘同步的情况下也会告诉 mysqld 已经完成了磁盘同步。

4. key_buffer_size

   ``MyISAM`` 表的 ``key`` 缓存，这个只对 ``MyISAM`` 存储引擎有效，所以对于我们绝大多数使用 ``Innodb`` 的应用，无需关心。

   该部分是 ``MyISAM`` 表的重要缓存区域，所有实例统一为 ``16M`` 。该部分主要存放 ``MyISAM`` 表的键。 ``MyISAM`` 表不同于 ``InnoDB`` 表，其缓存的索引缓存是放在 ``key_buffer`` 中的，而数据缓存则存储于操作系统的内存中。

5. query_cache_size

   ``MySQL`` 对于查询的结果会进行缓存来节省解析 ``SQL`` 、执行 ``SQL`` 的花销， ``query_cache`` 是按照 ``SQL`` 语句的 ``Hash`` 值进行缓存的，同时 ``SQL`` 语句涉及的表发生更新，该缓存就会失效，所以这个缓存对于特定的读多更新少的库比较有用，对于绝大多数更新较多的库可能不是很适用，比较受限于应用场景，所以 ``AWS`` 也把这个缓存给关了。我觉得这个值默认应该关闭，根据需求调整。

   查询缓存是 MySQL 比较独特的一个缓存区域，用来缓存特定 Query 的结果集（Result Set）信息，且共享给所有客户端。通过对 Query 语句进行特定的 Hash 计算之后与结果集对应存放在 Query Cache 中，以提高完全相同的 Query 语句的相应速度。当我们打开 MySQL 的 Query Cache 之后， MySQL 接收到每一个 SELECT 类型的 Query 之后都会首先通过固定的 Hash 算法得到该 Query 的 Hash 值，然后到 Query Cache 中查找是否有对应的 Query Cache。如果有，则直接将 Cache 的结果集返回给客户端。如果没有，再进行后续操作，得到对应的结果集之后将该结果集缓存到 Query Cache 中，再返回给客户端。当任何一个表的数据发生任何变化之后，与该表相关的所有 Query Cache 全部会失效，所以 Query Cache 对变更比较频繁的表并不是非常适用，但对那些变更较少的表是非常合适的，可以极大程度的提高查询效率，如那些静态资源表，配置表等等。为了尽可能高效的利用 Query Cache ， MySQL 针对 Query Cache 设计了多个 query_cache_type 值和两个 Query Hint：SQL_CACHE 和 SQL_NO_CACHE 。当 query_cache_type 设置为 0 （或者 OFF）的时候不使用 Query Cache ，当设置为 1 （或者 ON ）的时候，当且仅当 Query 中使用了 SQL_NO_CACHE 的时候 MySQL 会忽略 Query Cache ，当 query_cache_type 设置为 2 （或者 DEMAND ）的时候，当且仅当 Query 中使用了 SQL_CACHE 提示之后， MySQL 才会针对该 Query 使用 Query Cache 。可以通过 query_cache_size 来设置可以使用的最大内存空间。

6. Thread Cache

   连接线程是 MySQL 为了提高创建连接线程的效率，将部分空闲的连接线程保持在一个缓存区以备新进连接请求的时候使用，这尤其对那些使用短连接的应用程序来说可以极大的提高创建连接的效率。当我们通过 thread_cache_size 设置了连接线程缓存池可以缓存的连接线程的大小之后，可以通过( Connections - Threads_created )/Connections * 100% 计算出连接线程缓存的命中率。注意，这里设置的是可以缓存的连接线程的数目，而不是内存空间的大小。

7. Table Cache

   表缓存区主要用来缓存表文件的文件句柄信息，在 MySQL5.1.3 之前的版本通过 table_cache 参数设置，但从 MySQL5.1.3 开始改为 table_open_cache 来设置其大小。当我们的客户端程序提交 Query 给 MySQL 的时候， MySQL 需要对 Query 所涉及到的每一个表都取得一个表文件句柄信息，如果没有 Table Cache，那么 MySQL 就不得不频繁的进行打开关闭文件操作，无疑会对系统性能产生一定的影响， Table Cache 正是为了解决这一问题而产生的。在有了 Table Cache 之后， MySQL 每次需要获取某个表文件的句柄信息的时候，首先会到 Table Cache 中查找是否存在空闲状态的表文件句柄。如果有，则取出直接使用，没有的话就只能进行打开文件操作获得文件句柄信息。在使用完之后， MySQL 会将该文件句柄信息再放回 Table Cache 池中，以供其他线程使用。注意，这里设置的是可以缓存的表文件句柄信息的数目，而不是内存空间的大小。

8. Table definition Cache

   表定义信息缓存是从 MySQL5.1.3 版本才开始引入的一个新的缓存区，用来存放表定义信息。当我们的 MySQL 中使用了较多的表的时候，此缓存无疑会提高对表定义信息的访问效率。 MySQL 提供了 table_definition_cache 参数给我们设置可以缓存的表的数量。在 MySQL5.1.25 之前的版本中，默认值为 128 ，从 MySQL5.1.25 版本开始，则将默认值调整为 256 了，最大设置值为 524288 。注意，这里设置的是可以缓存的表定义信息的数目，而不是内存空间的大小。

9. Binlog Buffer

   二进制日志缓冲区主要用来缓存由于各种数据变更操做所产生的 Binary Log 信息。为了提高系统的性能， MySQL 并不是每次都是将二进制日志直接写入 Log File ，而是先将信息写入 Binlog Buffer 中，当满足某些特定的条件（如 sync_binlog 参数设置）之后再一次写入 Log File 中。我们可以通过 binlog_cache_size 来设置其可以使用的内存大小，同时通过 max_binlog_cache_size 限制其最大大小（当单个事务过大的时候 MySQL 会申请更多的内存）。当所需内存大于 max_binlog_cache_size 参数设置的时候， MySQL 会报错： "Multi-statement transaction required more than 'max_binlog_cache_size' bytes of storage"。



Session 私有内存
================
上面这些就是 ``MySQL`` 主要的共享内存空间，这些空间是在 ``MySQL`` 启动时就分配的，但是并不是立即使用的。 ``MySQL`` 还有一部分内存是在用户连接请求到达时动态分配的，即每个 ``MySQL`` 连接都单独一个缓存，这部分缓存主要包括：而出现 ``OOM`` 异常的实例都是由于下面各个连接私有的内存造成的。

执行如下命令，查询示例的 ``session`` 私有内存分配情况：

.. code-block:: shell

	show variables where variable_name in (
	'read_buffer_size','read_rnd_buffer_size','sort_buffer_size','join_buffer_size','binlog_cache_size','tmp_table_size'
	);

查询结果如下（如下为测试实例配置）：

.. code-block:: shell

	+-------------------------+-----------------+
	| Variable_name           | Value           |
	+-------------------------+-----------------+
	| binlog_cache_size       | 262144          |
	| join_buffer_size        | 262144          |
	| read_buffer_size        | 262144          |
	| read_rnd_buffer_size    | 262144          |
	| sort_buffer_size        | 262144          |
	| tmp_table_size          | 262144          |
	+-------------------------+-----------------+
	共返回 6 行记录,花费 356.54 ms.

1. read_buffer_size

   每个线程连续扫描时为扫描的每个表分配的缓存区的大小（字节）。如果进行多次连续扫描，可能还需要增加该值。默认值为 ``1311072`` ，只有当查询需要的时候，才分配 ``read_buffer_size`` 指定的全部内存。

   分别存放了对顺序和随机扫描（例如按照排序的顺序访问）的缓存， ``RDS`` 给每个 ``session`` 设置 ``256 K`` 的大小。当 ``thread`` 进行顺序或随机扫描数据时会首先扫描该 ``buffer`` 空间以避免更多的物理读。

   这部分内存主要用于当需要顺序读取数据的时候，如无法使用索引的情况下的全表扫描，全索引扫描等。在这种时候， MySQL 按照数据的存储顺序依次读取数据块，每次读取的数据快首先会暂存在 ``read_buffer_size`` 中，当 ``buffer`` 空间被写满或者全部数据读取结束后，再将 ``buffer`` 中的数据返回给上层调用者，以提高效率。


2. read_rnd_buffer_size

   当以任意顺序读取行时，可以分配随机读取缓冲区，通过该缓冲区读取行，以避免磁盘寻找。 ``read_rnd_buffer_size`` 系统变量决定缓冲器大小。

   和顺序读取相反，当 ``MySQL`` 进行非顺序读取（随机读取）数据块的时候，会利用这个缓冲区暂存读取的数据。如根据索引信息读取表数据，根据排序后的结果集与表进行 ``Join`` 等等。总的来说，就是当数据块的读取需要满足一定的顺序的情况下， ``MySQL`` 就需要产生随机读取，进而使用到 ``read_rnd_buffer_size`` 参数所设置的内存缓冲区。


3. sort_buffer_size

   每一个要做排序的请求，都会分到一个 ``sort_buffer_size`` 大的缓存，用于做 ``order by`` 和 ``group by`` 的排序，如果设置的缓存大小无法满足需要， ``MySQL`` 会将数据写入磁盘来完成排序。因为磁盘操作和内存操作不在一个数量级，所以 ``sort_buffer_size`` 对排序的性能影响很大。由于这部分缓存是即使不用这么大，也会全部分配的，所以对系统内存分配开销是比较大的，如果是希望扩大的话，建议在会话层设置，默认值 ``2M`` ， AWS 也是 ``2M`` 。

4. thread_stack

   默认 ``256K`` ， ``AWS`` 设置为 ``256K`` ， ``MySQL`` 为每个线程分配的堆栈大小，当线程堆栈太小时，这限制了服务器可以处理的 ``SQL`` 语句的复杂性。

   主要用来存放每一个线程自身的标识信息，如线程id，线程运行时基本信息等等，我们可以通过 ``thread_stack`` 参数来设置为每一个线程栈分配多大的内存。

5. join_buffer_size

   每个连接的每次 ``join`` 都分配一个，默认值 ``128K`` ， ``AWS`` 设置为 ``128K`` 。

   ``MySQL`` 仅支持 ``nest loop`` 的 ``join`` 算法， ``RDS`` 设置 ``256 K`` 的大小。处理逻辑是驱动表的一行和非驱动表联合查找，这时就可以将非驱动表放入 ``join_buffer`` ，不需要访问拥有并发保护机制的 ``buffer_pool`` 。

   应用程序经常会出现一些两表（或多表） ``Join`` 的操作需求， ``MySQL`` 在完成某些 ``Join`` 需求的时候（ ``all/index join`` ），为了减少参与 ``Join`` 的 "被驱动表" 的读取次数以提高性能，需要使用到 ``Join Buffer`` 来协助完成 ``Join`` 操作（具体 ``Join`` 实现算法请参考： ``MySQL`` 中的 ``Join`` 基本实现原理）。当 ``Join Buffer`` 太小， ``MySQL`` 不会将该 ``Buffer`` 存入磁盘文件，而是先将 ``Join Buffer`` 中的结果集与需要 ``Join`` 的表进行 ``Join`` 操作，然后清空 ``Join Buffer`` 中的数据，继续将剩余的结果集写入此 ``Buffer`` 中，如此往复。 这势必会造成被驱动表需要被多次读取，成倍增加 ``IO`` 访问，降低效率。


6. binlog_cache_size

   类似于 ``innodb_log_buffer_size`` 缓存事务日志， ``binlog_cache_size`` 缓存 ``Binlog`` ，不同的是这个是每个线程单独一个，主要对于大事务有较大性能提升。默认32K，AWS 32K。

   在一个事务还没有 ``commit`` 之前会先将其日志存储于 ``binlog_cache`` 中，等到事务 ``commit`` 后会将其 ``binlog`` 刷回磁盘上的 ``binlog`` 文件以持久化。

7. tmp_table_size

   默认 ``16M`` ，用户内存临时表的最大值，如果临时表超过该值， ``MySQL`` 就会把临时表转换为一个磁盘上 ``mysiam`` 表。如果用户需要做一些大表的 ``groupby`` 的操作，可能需要较大的该值，由于是与连接相关的，同样建议在会话层设置。

   如果用户在执行事务时遇到类似如下这样的错误，可以考虑增大 ``tmp_table`` 的值。

   .. code-block:: shell

    [Err] 1114 - The table '/home/mysql/data3081/tmp/#sql_6197_2' is full

8. net_buffer_size

   这部分用来存放客户端连接线程的连接信息和返回客户端的结果集。当 ``MySQL`` 开始产生可以返回的结果集，会在通过网络返回给客户端请求线程之前，会先暂存在通过 ``net_buffer_size`` 所设置的缓冲区中，等满足一定大小的时候才开始向客户端发送，以提高网络传输效率。不过， ``net_buffer_size`` 参数所设置的仅仅只是该缓存区的初始化大小， ``MySQL`` 会根据实际需要自行申请更多的内存以满足需求，但最大不会超过 ``max_allowed_packet`` 参数大小。

9. bulk_insert_buffer_size

   当我们使用如 ``insert … values(…),(…),(…)…`` 的方式进行批量插入的时候， ``MySQL`` 会先将提交的数据放如一个缓存空间中，当该缓存空间被写满或者提交完所有数据之后， ``MySQL`` 才会一次性将该缓存空间中的数据写入数据库并清空缓存。此外，当我们进行 ``LOAD DATA INFILE`` 操作来将文本文件中的数据 ``Load`` 进数据库的时候，同样会使用到此缓冲区。


