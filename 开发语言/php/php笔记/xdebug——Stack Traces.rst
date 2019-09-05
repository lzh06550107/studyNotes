***
栈追踪
***
当 xdebug 激活时，PHP 一旦要显示通知、警告或错误时，xdebug 就会显示堆栈跟踪信息。它们以何种方式配置，可以根据你的需要来配置。

Xdebug显示堆栈追踪的错误条件(如果在php.ini中将display_errors设置为on)相当保守。这是因为大量的信息会减慢脚本的执行和浏览器中堆栈跟踪本身的渲染速度。但是，可以使堆栈追踪以不同的设置显示更详细的信息。

堆栈中的变量
======
默认情况下，xdebug现在将在其生成的堆栈跟踪中显示变量信息。无论是收集还是显示，变量信息可能需要相当多的资源。然而，在许多情况下，显示变量信息是有用的，这就是为什么xdebug具有设置xdebug.collect_params的原因。下面的脚本，结合使用该设置的不同值的输出将显示在下面的示例中。

https://xdebug.org/docs/

配置参数选项  参数值类型与默认值  参数选项描述
xdebug.auto_trace  boolean类型，默认值=0  是否在脚本运行之前自动调用相关追踪函数。这使得可以追踪在auto_prepend_file中的代码。
xdebug.cli_color   integer类型，默认值=0 该参数自2.2版本开始引入。  如果值=1，当处于CLI模式或连接虚拟控制台时，Xdebug将高亮显示var_dumps()和堆栈追踪输出；在Windows中，这需要安装ANSICON工具。如果值=2，不管是否处于CLI模式或连接虚拟控制台，Xdebug都会高亮显示var_dumps()或堆栈输出；这种情况下，你可能会看到转义后的代码。
xdebug.collect_assignments   boolean类型，默认值=0 该参数自2.1版本开始引入。   用于控制是否为函数跟踪添加变量赋值功能。
xdebug.collect_includes   boolean类型，默认值=1   控制是否在跟踪文件中写入include()、include_once()、require()、require_once()等函数中用到的文件名。
xdebug.collect_params    integer类型，默认值=0  控制在调用函数时，是否收集传递给函数的参数信息。如果参数值过大，这可能会占用大量的内存；不过，在Xdebug 2中不会出现该问题，因为Xdebug 2将相关数据写入磁盘中，而不是占用内存。

该设置具有四个值。对于每个值，显示不同数量的信息。
如果值=0，则不显示任何参数信息。
如果值=1，只显示类型和元素大小信息，例如：string(6)、array(8)。
如果值=2，将显示类型和元素大小，以及全部信息的工具提示。
如果值=3，将显示变量的全部内容。
如果值=4，将显示变量的全部内容和变量名。
如果值=5，PHP系列号变量内容(不带名称)(2.3版本引入)

xdebug.collect_return   boolean类型，默认值=0   控制是否在追踪文件中写入函数调用的返回值。仅仅对于2.3版本以上，计算机格式(xdebug.trace_format=1)追踪文件有效
xdebug.collect_vars  boolean类型，默认值=0  控制是否收集指定作用域中的变量信息。由于需要反向工程PHP的操作码数组，因此Xdebug的分析速度可能比较慢。这个设置不会记录不同的变量具有哪些值，为此请使用xdebug.collect_params。只有在您想使用xdebug_get_declared_vars（）时才需要启用此设置。
xdebug.coverage_enable  boolean类型，默认值=1 该参数自2.2版本开始引入。  控制是否允许通过设置内部结构来启用代码覆盖率功能。如果设置为0，代码覆盖率分析将无法正常工作。
xdebug.default_enable   boolean类型，默认值=1  当发生异常或错误时，是否默认显示堆栈信息。因为这是xdebug的基本功能之一，建议将此设置设置为1。你可以用xdebug_disable()来禁止从代码中显示堆栈跟踪。
xdebug.dump.*   string类型，默认值=Empty   这里的*可以是COOKIE, FILES, GET, POST, REQUEST, SERVER, SESSION中的任意一个。用于指定发生错误时是否显示超全局变量数组中的索引变量信息。比如，你想要显示请求的IP地址和请求方式，可以设置为
xdebug.dump.SERVER=REMOTE_ADD,REQUEST_METHOD
多个索引变量用英文逗号隔开，如果要输出其中的所有变量，可以直接用*，例如：
xdebug.dump.GET=*
xdebug.dump_globals  boolean类型，默认值=1  控制是否显示通过xdebug.dump.*定义的所有超全局变量的信息。
xdebug.dump_once    boolean类型，默认值=1  如果出现多个错误，控制超全局变量信息是在所有错误中显示，还是只在第一个错误中显示。
xdebug.dump_undefined   boolean类型，默认值=1  控制是否显示超全局变量中未定义的值。
xdebug.extended_info    integer类型，默认值=1  是否强制进入PHP解析器的"extended_info"模式，这将允许Xdebug以远程调试器对文件或行添加断点。开启此模式将拖慢脚本的允许速度，该参数只能在php.ini中设置。
xdebug.file_link_format  string类型，默认值=,  自2.2版本开始引入。  用于指定堆栈信息中用到的文件名称的链接样式，这允许IDE通过设置链接协议，直接点击堆栈信息中的文件名称，即可快速打开指定的文件。例如：ZendStudio://%f@%l(%f表示文件路径，%f表示行号)。
xdebug.force_display_errors   integer类型，默认值=0 自2.3版本开始引入。  是否强制显示错误信息。不管php的display_errors是什么设置。
xdebug.force_error_reporting    integer类型，默认值=0 自2.3版本开始引入。  是否强制显示所有错误级别的信息。这个设置是一个位掩码，就像error_reporting。这个位掩码将被逻辑地与由error_reporting表示的位掩码进行比较以揭示哪些错误应该被显示。此设置只能在php.ini中进行，无论应用程序使用ini_set()如何，都可以强制显示某些错误。
xdebug.halt_level   integer类型，默认值=0 自2.3版本开始引入。  指定出现那些错误级别的错误时，中止程序运行。例如：xdebug.halt_level=E_WARNING|E_NOTICE|E_USER_WARNING|E_USER_NOTICE(也仅支持上述4种错误级别)。
xdebug.idekey   string类型，默认值=*complex*    指定传递给DBGp调试器处理程序的IDE Key。控制哪个ide键xdebug应该传递给dbgp调试器处理程序。默认是基于环境设置的。首先查询环境设置dbgp_idekey，然后用户和最后一个用户名。默认设置为找到的第一个环境变量。如果没有找到该设置，则默认为“”。如果这个设置被设置，它总是覆盖环境变量。
xdebug.manual_url   string类型，默认值=http://www.php.net 仅2.2.1以下版本可用  用于指定从函数堆栈和错误信息链接到的帮助手册的基本URL。
xdebug.max_nesting_level    integer类型，默认值=256   指定递归的嵌套层级数。
xdebug.max_stack_frames  integer类型，默认值=-1 在2.3版本引入  控制堆栈跟踪中显示的堆栈帧的数量，包括在PHP错误堆栈跟踪期间的命令行上，以及在浏览器中的HTML跟踪。
xdebug.overload_var_dump    boolean类型，默认值=2 自2.1版本开始引入  当php.ini中的html_error设为1时，Xdebug是否默认使用自身的改进版本来重载var_dump()。默认情况下，当php.ini中html_errors设置为1或者2时，xdebug重载var_dump()和它自己的改进版本来显示变量。如果你不需要，你可以设置这个设置为0，但是直接关闭html_errors不是聪明方法。您也可以使用2作为此设置的值。除了很好的格式化var_dump()输出之外，它还会将文件名和行号添加到输出中。xdebug.file_link_format设置也受到影响。（新的xdebug 2.3）
xdebug.profiler_aggregate  integer类型，默认值=0  当此设置为1时，将为多个请求写入一个分析器文件。人们可以浏览多个页面或重新加载页面以获得所有请求的平均值。该文件将被命名为.cachegrind.aggregate。您将需要移动此文件以获取另一轮聚合数据。
xdebug.profiler_append   integer类型，默认值=0  当多个请求映射到相同文件时，指定是覆盖之前的调试信息文件还是追加内容到该文件中。
xdebug.profiler_enable   integer类型，默认值=0   指定是否启用Xdebug的性能分析，并创建性能信息文件。使xdebug的配置文件在配置文件输出目录中创建文件。这些文件可以通过kcachegrind读取以可视化您的数据。这个设置不能在ini_set()的脚本中设置。如果您想有选择地启用分析器，请将xdebug.profiler_enable_trigger设置为1而不是使用此设置。
xdebug.profiler_enable_trigger  integer类型，默认值=0  当此设置设置为1时，可以使用XDEBUG_PROFILE GET/POST参数触发生成分析器文件，也可以设置使用名称为XDEBUG_PROFILE的cookie。然后，这将将分析器数据写入定义的目录。为了防止分析器为每个请求生成配置文件，您需要将xdebug.profiler_enable设置为0。对触发器本身的访问可以通过xdebug.profiler_enable_trigger_value进行配置。
xdebug.profiler_enable_trigger_value  string类型，默认值为空 在2.3版本引入  这个设置可以用来限制谁可以使用xdebug.profiler_enable_trigger中的xdebug_profile功能。当修改默认值时，cookie，GET或POST参数的值需要与此设置匹配共享密钥集以启动分析器。
xdebug.profiler_output_dir  string类型，默认值=/tmp   指定性能分析信息文件的输出目录。确保运行PHP的用户具有对该目录的写入权限。这个设置不能在ini_set()的脚本中设置。
xdebug.profiler_output_name  string类型，默认值=cachegrind.out.%p   指定性能分析信息文件的名称。此设置确定用于将跟踪转储到的文件的名称。该设置指定格式说明符的格式，非常类似于sprintf()和strftime()。有几种格式说明符可用于格式化文件名。
xdebug.remote_addr_header  string类型，默认值为空 在2.4版本引入  如果xdebug.remote_addr_header被配置为非空字符串，则该值在$SERVER superglobal数组中用作键，以确定使用哪个头来查找用于“连接回”的IP地址或主机名。此设置仅与xdebug.remote_connect_back结合使用，否则将被忽略。
xdebug.remote_autostart  boolean类型，默认值=0  通常您需要使用特定的HTTP GET/POST变量来启动远程调试。当此设置设置为1时，即使GET/POST/COOKIE变量不存在，xdebug也将始终尝试启动远程调试会话并尝试连接到客户端。
xdebug.remote_connect_back  boolean类型，默认值=0 在2.1版本开始引入  

如果启用，xdebug.remote_host设置将被忽略，xdebug将尝试连接到发出http请求的客户端。它检查$_SERVER['HTTP_X_FORWARDED_FOR']和$_SERVER['REMOTE_ADDR']变量来找出使用哪个IP地址。

如果配置了xdebug.remote_addr_header，那么将在 $_SERVER['HTTP_X_FORWARDED_FOR']和$_SERVER['REMOTE_ADDR']变量之前检查具有配置名称的$SERVER变量。

此设置不适用于通过cli进行调试，因为$SERVER头变量在那里不可用。

请注意，没有过滤器可用，任何可以连接到Web服务器的人都可以启动调试会话，即使他们的地址与xdebug.remote_host不匹配。

xdebug.remote_cookie_expire_time  integer类型，默认值3600 在2.1版本引入  此设置可用于通过会话cookie增加（或减少）远程调试会话保持活动的时间。
xdebug.remote_enable    boolean类型，默认值=0  是否开启远程调试。这个开关控制着xdebug是否应该尝试联系正在侦听xdebug.remote_host和xdebug.remote_port设置的主机和端口的调试客户端。如果无法建立连接，则该脚本将继续，就像该设置为0一样。
xdebug.remote_handler   string类型，默认值=dbgp   指定远程调试的处理协议
xdebug.remote_host  string类型，默认值=localhost  指定远程调试的主机名。

选择运行调试客户端的主机，您可以使用主机名称，ip地址或“unix:///path/to/sock”的unix域套接字。如果启用了xdebug.remote_connect_back，则忽略此设置。

xdebug.remote_log   string类型，默认值=    指定远程调试的日志文件名
xdebug.remote_mode   string类型，默认值=req    可以设为req或jit，req表示脚本一开始运行就连接远程客户端，jit表示脚本出错时才连接远程客户端。
xdebug.remote_port   integer类型，默认值=9000   指定远程调试的端口号
xdebug.scream  boolean类型，默认值=0 在版本2.1中引入  如果此设置为1，则xdebug将禁用@(闭合)操作符，以便通知，警告和错误不再隐藏。
xdebug.show_error_trace  integer类型，默认值=0 在2.4版本引入  当这个设置被设置为1时，每当发生错误时，xdebug将显示一个堆栈跟踪 - 即使这个错误实际上被捕获了。
xdebug.show_exception_trace  integer类型，默认值=0  当被设置为1时，无论何时发生异常或错误，xdebug都会显示一个堆栈跟踪 - 即使这个异常或错误实际上被捕获了。
xdebug.show_local_vars  integer类型，默认值=0  当这个设置被设置为非 0时，xdebug在错误情况下生成的堆栈转储也将显示最顶级作用域中的所有变量。注意，这可能会产生大量的信息，因此默认关闭。
xdebug.show_mem_delta  integer类型，默认值=0  当这个设置被设置为非0时，xdebug的生成的人类可读跟踪文件将显示函数调用之间的内存使用情况的差异。如果xdebug被配置为生成计算机可读的跟踪文件，那么他们将始终显示此信息。
xdebug.trace_enable_trigger  boolean类型，默认值=0 在2.2版本开始引入。  当此设置设置为1时，可以使用XDEBUG_TRACE GET/POST参数触发跟踪文件的生成，也可以使用设置名称xdebug_trace的cookie。这会将跟踪数据写入到已定义的目录。为了防止xdebug为每个请求生成跟踪文件，需要将xdebug.auto_trace设置为0。访问触发器本身可以通过xdebug.trace_enable_trigger_value进行配置。
xdebug.trace_enable_trigger_value  string类型，默认值为空 在2.3版本开始引入。  这个设置可以用来限制谁可以使用xdebug.trace_enable_trigger中的xdebug_trace功能。当设置非空值时，cookie，GET或POST参数的值需要将共享密钥集与此设置进行匹配，以便生成跟踪文件。
xdebug.trace_format  integer类型，默认值=0  

跟踪文件的格式。
0：显示一个人类可读的追踪文件，该文件包括：时间、内存使用，memory delta(如果设置启用了xdebug.show_mem_delta)，层级，函数名称，函数参数（如果启用了设置xdebug.collect_params），文件名和行号。
1：写一个计算机可读的格式有两个不同的记录。有不同的记录用于进入一个堆栈帧，和离开一个堆栈帧。下表列出了每种记录类型的字段。字段是制表符分隔的。
2：写一个(简单)html格式的追踪文件。


xdebug.trace_options    integer类型，默认值=0   指定对于之后的请求，追踪文件是追加内容还是覆盖之前内容。当设置为'1'时，跟踪文件将被追加，而不是在随后的请求中被覆盖。
xdebug.trace_output_dir  string类型，默认值=/tmp   指定追踪文件的存放目录
xdebug.trace_output_name  string类型，默认值=trace.%c   指定追踪文件的名称。此设置确定用于将跟踪转储到的文件的名称。该设置指定格式说明符的格式，非常类似于sprintf()和strftime()。有几种格式说明符可用于格式化文件名。“.xt”扩展名总是被自动添加。
xdebug.var_display_max_children  integer类型，默认值=128  用xdebug_var_dump()，xdebug.show_local_vars或函数跟踪显示变量时，控制数组孩子和对象的属性的数量。使用-1作为值，禁用任何限制。此设置对通过远程调试功能发送给客户端的子项数量没有任何影响。
xdebug.var_display_max_data  integer类型，默认值=512  控制使用xdebug_var_dump()，xdebug.show_local_vars或通过函数跟踪显示变量时显示的最大字符串长度。使用-1作为值，禁用任何限制。此设置对通过远程调试功能发送给客户端的子项数量没有任何影响。
xdebug.var_display_max_depth  integer类型，默认值=3  通过xdebug_var_dump()，xdebug.show_local_vars或通过函数跟踪显示变量时，控制数组元素和对象属性的嵌套级别。您可以选择的最大值是1023。您还可以使用-1作为值来选择此最大值。此设置对通过远程调试功能发送给客户端的子项数量没有任何影响。

https://xdebug.org/docs/all_settings