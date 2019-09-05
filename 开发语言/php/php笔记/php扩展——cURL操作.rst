******
cURL操作
******

简介
=====
PHP支持由Daniel Stenberg创建的libcurl，它允许你连接许多不同类型的服务器，并与许多不同类型的协议通信。 libcurl目前支持http，https，ftp，gopher，telnet，dict，file和ldap协议。 libcurl还支持HTTPS证书，HTTP POST，HTTP PUT，FTP上传（这也可以用PHP的ftp扩展完成），基于HTTP表单的上传，代理，cookie和用户密码验证。

使用条件
========
为了使用PHP的cURL函数，你需要安装libcurl包。 PHP需要libcurl版本7.10.5或更高版本。

为了在Windows环境中启用此模块，libeay32.dll和ssleay32.dll必须存在于您的PATH中。你不需要cURL站点的libcurl.dll。

函数
=====

curl_init()
------------
``resource curl_init ([ string $url = NULL ] )``

初始化新会话并返回一个cURL句柄，以便与curl_setopt()，curl_exec()和curl_close()函数一起使用。

参数：

url：如果提供，CURLOPT_URL选项将被设置为它的值。您可以使用curl_setopt()函数手动设置它。

返回值：

成功时返回cURL句柄，错误时返回FALSE。

curl_setopt()
----------------
``bool curl_setopt ( resource $ch , int $option , mixed $value )``

在给定的cURL会话句柄上设置一个选项。

参数：

- ch：由curl_init()返回的一个cURL句柄。
- option：对CURLOPT_XXX选项进行设置。
- value：选项中设置的值。

返回值：

成功返回TRUE，失败则返回FALSE。

选项列表：

第一类：
对于下面的这些option的可选参数，value应该被设置一个bool类型的值：

=================================  =========================================================================================================================================================================================
选项                                 可选value值
=================================  =========================================================================================================================================================================================
CURLOPT_AUTOREFERER                当根据Location:重定向时，自动设置header中的Referer:信息。
CURLOPT_BINARYTRANSFER             在启用CURLOPT_RETURNTRANSFER的时候，返回原生的（Raw）输出。
CURLOPT_COOKIESESSION              启用时curl会仅仅传递一个session cookie，忽略其他的cookie，默认状况下cURL会将所有的cookie返回给服务端。session cookie是指那些用来判断服务器端的session是否有效而存在的cookie。
CUROPT_CERTINFO                    向安全传输的STDERR输出SSL认证信息。需要CURLOPT_VERBOSE才能起作用。
CUROPT_CONNECT_ONLY                告诉库执行所有需要的代理验证和连接设置，但是没有数据传输。这个选项是为HTTP，SMTP和POP3实现的。
CURLOPT_CRLF                       启用时将Unix的换行符转换成回车换行符。
CURLOPT_DNS_USE_GLOBAL_CACHE       启用时会启用一个全局的DNS缓存，此项不是线程安全的，并且默认启用。
CURLOPT_FAILONERROR                如果返回的HTTP代码大于或等于400，则会失败。默认行为是正常返回页面，忽略代码。
CURLOPT_SSL_FALSESTART             启用TLS错误启动。
CURLOPT_FILETIME                   尝试检索远程文档的修改日期。该信息可以通过 curl_getinfo()获取。
CURLOPT_FOLLOWLOCATION             启用时会将服务器返回放在header中的"Location: "递归的返回给服务器，使用CURLOPT_MAXREDIRS可以限定递归返回的数量。
CURLOPT_FORBID_REUSE               在完成交互以后强迫断开连接，不能重用。
CURLOPT_FRESH_CONNECT              强制获取一个新的连接，而不是缓存中的连接。
CURLOPT_FTP_USE_EPRT               启用时当FTP下载时，使用EPRT (或 LPRT)命令。设置为FALSE时禁用EPRT和LPRT，仅仅使用PORT命令。
CURLOPT_FTP_USE_EPSV               启用时，在FTP传输过程中恢复到PASV模式前首先尝试EPSV命令。设置为FALSE时禁用EPSV命令。
CURLOPT_FTP_CREATE_MISSING_DIRS    当FTP操作遇到当前不存在的路径时创建丢失的目录。
CURLOPT_FTPAPPEND                  启用时追加写入文件而不是覆盖它。
CURLOPT_TCP_NODELAY                禁用TCP的Nagle算法，该算法会尽量减少网络上小数据包的数量。
CURLOPT_FTPASCII                   CURLOPT_TRANSFERTEXT的别名。
CURLOPT_FTPLISTONLY                启用时只列出FTP目录的名字。
CURLOPT_HEADER                     启用时在输出中包含头。
CURLINFO_HEADER_OUT                启用时追踪句柄的请求字符串。
CURLOPT_HTTPGET                    启用时会设置HTTP的method为GET，因为GET是默认是，所以只在被修改的情况下使用。
CURLOPT_HTTPPROXYTUNNEL            启用时会通过HTTP代理隧道来传输。
CURLOPT_NETRC                      在连接建立以后，访问~/.netrc文件获取用户名和密码信息连接远程站点。
CURLOPT_NOBODY                     启用时将不对HTML中的BODY部分进行输出。它请求方法设置为HEAD。
CURLOPT_NOPROGRESS                 启用时关闭curl传输的进度条，此项的默认设置为启用。PHP自动地设置这个选项为TRUE，这个选项仅仅应当在以调试为目的时被改变。
CURLOPT_NOSIGNAL                   启用时忽略所有的curl传递给php进程的信号。在SAPI多线程传输时此项被默认启用。
CURLOPT_PATH_AS_IS                 开启时不处理路径中的点点。（即 ../ ）
CURLOPT_PIPEWAIT                   等待 pipelining/multiplexing。
CURLOPT_POST                       启用时会发送一个常规的POST请求，类型为：application/x-www-form-urlencoded，就像表单提交的一样。
CURLOPT_PUT                        启用时允许HTTP发送文件，必须同时设置CURLOPT_INFILE和CURLOPT_INFILESIZE。
CURLOPT_RETURNTRANSFER             将curl_exec()获取的信息以字符串返回，而不是直接输出。
CURLOPT_SAFE_UPLOAD                TRUE则禁用 @ 前缀在 CURLOPT_POSTFIELDS 中发送文件。 意味着 @ 可以在字段中安全得使用了。 可使用 CURLFile 作为上传的代替。PHP 5.5.0 中添加，默认值 FALSE。 PHP 5.6.0 改默认值为 TRUE。PHP 7 删除了此选项， 必须使用 CURLFile interface 来上传文件。
CURLOPT_SASL_IR                    TRUE 开启，收到首包(first packet)后发送初始的响应(initial response)。cURL 7.31.10 中添加，自 PHP 7.0.7 起有效。
CURLOPT_SSL_ENABLE_ALPN            FALSE 禁用 SSL 握手中的 ALPN (如果 SSL 后端的 libcurl 内建支持) 用于协商到 http2。cURL 7.36.0 中增加， PHP 7.0.7 起有效。
CURLOPT_SSL_ENABLE_NPN             FALSE 禁用 SSL 握手中的 NPN(如果 SSL 后端的 libcurl 内建支持)，用于协商到 http2。
CURLOPT_SSL_VERIFYPEER             禁用后cURL将终止从服务端进行验证。使用CURLOPT_CAINFO选项设置证书使用CURLOPT_CAPATH选项设置证书目录 如果CURLOPT_SSL_VERIFYPEER(默认值为2)被启用，CURLOPT_SSL_VERIFYHOST需要被设置成TRUE否则设置为FALSE。自cURL 7.10开始默认为TRUE。从cURL 7.10开始默认绑定安装。
CURLOPT_SSL_VERIFYSTATUS           TRUE 验证证书状态。cURL 7.41.0 中添加， PHP 7.0.7 起有效。
CURLOPT_TCP_FASTOPEN               TRUE 开启 TCP Fast Open。cURL 7.49.0 中添加， PHP 7.0.7 起有效。
CURLOPT_TFTP_NO_OPTIONS            TRUE 不发送 TFTP 的 options 请求。
CURLOPT_TRANSFERTEXT               启用后对FTP传输使用ASCII模式。对于LDAP，它检索纯文本信息而非HTML。在Windows系统上，系统不会把STDOUT设置成binary模式。
CURLOPT_UNRESTRICTED_AUTH          在使用CURLOPT_FOLLOWLOCATION产生的header中的多个locations中持续追加用户名和密码信息，即使域名已发生改变。
CURLOPT_UPLOAD                     启用后允许文件上传。
CURLOPT_VERBOSE                    启用时输出详细信息，存放在STDERR或指定的CURLOPT_STDERR中。
=================================  =========================================================================================================================================================================================

第二类：

对于下面的这些option的可选参数，value应该被设置一个integer类型的值：

===============================  ===============================================================================================================================================================================================================================================================================================================================================================================================
选项                               可选value值
===============================  ===============================================================================================================================================================================================================================================================================================================================================================================================
CURLOPT_BUFFERSIZE               每次获取的数据中读入缓存的大小，但是不保证这个值每次都会被填满。在cURL 7.10中被加入。
CURLOPT_CLOSEPOLICY              不是CURLCLOSEPOLICY_LEAST_RECENTLY_USED就是CURLCLOSEPOLICY_OLDEST，还存在另外三个CURLCLOSEPOLICY_，但是cURL暂时还不支持。(过时)
CURLOPT_CONNECTTIMEOUT           在发起连接前等待的时间，如果设置为0，则无限等待。
CURLOPT_CONNECTTIMEOUT_MS        尝试连接等待的时间，以毫秒为单位。如果设置为0，则无限等待。在cURL 7.16.2中被加入。从PHP 5.2.3开始可用。
CURLOPT_DNS_CACHE_TIMEOUT        设置在内存中保存DNS信息的时间，默认为120秒。
CURLOPT_EXPECT_100_TIMEOUT_MS    超时预计： 100毫秒内的 continue 响应 默认为 1000 毫秒。
CURLOPT_FTPSSLAUTH               FTP验证方式：CURLFTPAUTH_SSL (首先尝试SSL)，CURLFTPAUTH_TLS (首先尝试TLS)或CURLFTPAUTH_DEFAULT (让cURL自动决定)。在cURL 7.12.2中被加入。
CURLOPT_HEADEROPT                如何处理头。可以是以下一些值：1)CURLHEADER_UNIFIED：在CURLOPT_HTTPHEADER中指定的头将用于对服务器和代理的请求。启用此选项后，CURLOPT_PROXYHEADER将不起任何作用；2)CURLHEADER_SEPARATE：使得CURLOPT_HTTPHEADER头只能发送到服务器而不是代理。代理头文件必须使用CURLOPT_PROXYHEADER来设置才能使用。请注意，如果将非CONNECT请求发送到代理，libcurl将同时发送服务器标头和代理标头。当进行CONNECT时，libcurl将只发送CURLOPT_PROXYHEADER头到代理，然后CURLOPT_HTTPHEADER头只到服务器。从cURL 7.42.1开始，默认为CURLHEADER_SEPARATE，从前是CURLHEADER_UNIFIED。
CURLOPT_HTTP_VERSION             CURL_HTTP_VERSION_NONE (默认值，让cURL自己判断使用哪个版本)，CURL_HTTP_VERSION_1_0 (强制使用 HTTP/1.0)或CURL_HTTP_VERSION_1_1 (强制使用 HTTP/1.1)。
CURLOPT_HTTPAUTH                 使用的HTTP验证方法，可选的值有：CURLAUTH_BASIC、CURLAUTH_DIGEST、CURLAUTH_GSSNEGOTIATE、CURLAUTH_NTLM、CURLAUTH_ANY和CURLAUTH_ANYSAFE。可以使用|位域(或)操作符分隔多个值，cURL让服务器选择一个支持最好的值。CURLAUTH_ANY等价于(CURLAUTH_BASIC | CURLAUTH_DIGEST | CURLAUTH_GSSNEGOTIATE | CURLAUTH_NTLM)。CURLAUTH_ANYSAFE等价于(CURLAUTH_DIGEST | CURLAUTH_GSSNEGOTIATE | CURLAUTH_NTLM)。
CURLOPT_INFILESIZE               设定上传文件的字节大小限制，字节(byte)为单位。请注意，使用此选项不会阻止libcurl发送更多数据，因为发送的内容取决于CURLOPT_READFUNCTION。
CURLOPT_LOW_SPEED_LIMIT          当传输速度小于CURLOPT_LOW_SPEED_LIMIT时(bytes/sec)，PHP会根据CURLOPT_LOW_SPEED_TIME来判断是否因太慢而取消传输。
CURLOPT_LOW_SPEED_TIME           当传输速度小于CURLOPT_LOW_SPEED_LIMIT时(bytes/sec)，PHP会根据CURLOPT_LOW_SPEED_TIME来判断是否因太慢而取消传输。
CURLOPT_MAXCONNECTS              允许的最大持久连接数量，超过是会通过CURLOPT_CLOSEPOLICY决定应该停止哪些连接。
CURLOPT_MAXREDIRS                指定最多的HTTP重定向的数量，这个选项是和CURLOPT_FOLLOWLOCATION一起使用的。
CURLOPT_PORT                     用来指定连接端口。（可选项）
CURLOPT_POSTREDIR                位掩码，1 (301 永久重定向), 2 (302 Found) 和 4 (303 See Other) 设置 CURLOPT_FOLLOWLOCATION 时，什么情况下需要再次 HTTP POST 到重定向网址。
CURLOPT_PROTOCOLS                CURLPROTO_\*的掩码值。如果被启用，掩码值会限定libcurl在传输过程中有哪些可使用的协议。这将允许你在编译libcurl时支持众多协议，但是限制只是用它们中被允许使用的一个子集。默认libcurl将会使用全部它支持的协议。参见CURLOPT_REDIR_PROTOCOLS。可用的协议选项为：CURLPROTO_HTTP、CURLPROTO_HTTPS、CURLPROTO_FTP、CURLPROTO_FTPS、CURLPROTO_SCP、CURLPROTO_SFTP、CURLPROTO_TELNET、CURLPROTO_LDAP、CURLPROTO_LDAPS、CURLPROTO_DICT、CURLPROTO_FILE、CURLPROTO_TFTP、CURLPROTO_ALL。
CURLOPT_PROXYAUTH                HTTP代理连接的验证方式。使用在CURLOPT_HTTPAUTH中的掩码标志来设置相应选项。对于代理验证只有CURLAUTH_BASIC和CURLAUTH_NTLM当前被支持。在cURL 7.10.7中被加入。
CURLOPT_PROXYPORT                代理服务器的端口。端口也可以在CURLOPT_PROXY中进行设置。
CURLOPT_PROXYTYPE                不是CURLPROXY_HTTP (默认值) 就是CURLPROXY_SOCKS4、CURLPROXY_SOCKS5、CURLPROXY_SOCKS4A、CURLPROXY_SOCKS5_HOSTNAME。在cURL 7.10中被加入。
CURLOPT_REDIR_PROTOCOLS          CURLPROTO_\*中的掩码值。如果被启用，掩码值将会限制传输在CURLOPT_FOLLOWLOCATION开启时跟随某个重定向时可使用的协议。这将使你对重定向时限制传输线程使用被允许的协议子集。默认libcurl将会允许除FILE和SCP之外的全部协议。这个和7.19.4预发布版本种无条件地跟随所有支持的协议有一些不同。关于协议常量，请参照CURLOPT_PROTOCOLS。在cURL 7.19.4中被加入。
CURLOPT_RESUME_FROM              在恢复传输时传递一个字节偏移量（用来断点续传）。
CURLOPT_SSL_OPTIONS              设置SSL行为选项，这是以下任何常量的位掩码：1)CURLSSLOPT_ALLOW_BEAST：不会尝试使用SSL3和TLS1.0协议中的安全漏洞的任何变通办法；2)CURLSSLOPT_NO_REVOKE：禁用对存在此类行为的SSL后端的证书吊销检查。
CURLOPT_SSL_VERIFYHOST           1：检查服务器SSL证书中是否存在一个公用名(common name)。译者注：公用名(Common Name)一般来讲就是填写你将要申请SSL证书的域名 (domain)或子域名(sub domain)。2：检查公用名是否存在，并且是否与提供的主机名匹配。
CURLOPT_SSLVERSION               使用的SSL版本(2 或 3)。默认情况下PHP会自己检测这个值，尽管有些情况下需要手动地进行设置。
CURLOPT_STREAM_WEIGHT            设置数字流权重（1到256之间的数字）。
CURLOPT_TIMECONDITION            如果使用CURL_TIMECOND_IFMODSINCE，在CURLOPT_TIMEVALUE指定某个时间内页面被修改，则返回页面，如果在CURLOPT_TIMEVALUE指定时间内没有被修改过，并且CURLOPT_HEADER为true，则返回一个"304 Not Modified"的header；CURL_TIMECOND_IFUNMODSINCE与它相反，默认值为CURL_TIMECOND_IFUNMODSINCE。
CURLOPT_TIMEOUT                  设置cURL允许执行的最长秒数。
CURLOPT_TIMEOUT_MS               设置cURL允许执行的最长毫秒数。在cURL 7.16.2中被加入。从PHP 5.2.3起可使用。
CURLOPT_TIMEVALUE                设置一个被CURLOPT_TIMECONDITION使用的时间戳，在默认状态下使用的是CURL_TIMECOND_IFMODSINCE。
CURLOPT_MAX_RECV_SPEED_LARGE     如果下载速度超过了此速度(以每秒字节数来统计) ，即传输过程中累计的平均数，传输就会降速到这个参数的值。默认不限速。cURL 7.15.5 中添加，PHP 5.4.0 有效。
CURLOPT_MAX_SEND_SPEED_LARGE     如果上传的速度超过了此速度(以每秒字节数来统计)，即传输过程中累计的平均数 ，传输就会降速到这个参数的值。默认不限速。cURL 7.15.5 中添加，PHP 5.4.0 有效。
CURLOPT_SSH_AUTH_TYPES           一个由CURLSSH_AUTH_PUBLICKEY,CURLSSH_AUTH_PASSWORD, CURLSSH_AUTH_HOST, CURLSSH_AUTH_KEYBOARD掩码值。设置为CURLSSH_AUTH_ANY让libcurl随便挑选一个。cURL 7.16.1 中添加。
CURLOPT_IPRESOLVE                允许程序选择想要解析的 IP 地址类别。只有在域名有多种 ip 类别的时候才能用，可以的值有： CURL_IPRESOLVE_WHATEVER、 CURL_IPRESOLVE_V4、 CURL_IPRESOLVE_V6，默认是 CURL_IPRESOLVE_WHATEVER。
CURLOPT_FTP_FILEMETHOD           告诉curl使用哪种方法到达FTP（S）服务器上的文件。可能的值是CURLFTPMETHOD_MULTICWD, CURLFTPMETHOD_NOCWD和CURLFTPMETHOD_SINGLECWD。
===============================  ===============================================================================================================================================================================================================================================================================================================================================================================================

第三类：

对于下面的这些option的可选参数，value应该被设置一个string类型的值：

=================================  ===============================================================================================================================================================================================================================================================================================================================================
选项                                 可选value值
=================================  ===============================================================================================================================================================================================================================================================================================================================================
CURLOPT_CAINFO                     一个保存着1个或多个用来让服务端验证的证书的文件名。这个参数仅仅在和CURLOPT_SSL_VERIFYPEER一起使用时才有意义。可能需要一个绝对路径。
CURLOPT_CAPATH                     一个保存着多个CA证书的目录。这个选项是和CURLOPT_SSL_VERIFYPEER一起使用的。
CURLOPT_COOKIE                     设定HTTP请求中"Cookie:"部分的内容。多个cookie用分号分隔，分号后带一个空格(例如, "fruit=apple; colour=red")。
CURLOPT_COOKIEFILE                 包含cookie数据的文件名，cookie文件的格式可以是Netscape格式，或者只是纯HTTP头部信息存入文件。如果该名称是一个空字符串，则不加载cookie，但仍然启用cookie处理。
CURLOPT_COOKIEJAR                  连接结束(curl_close)后保存cookie信息的文件。
CURLOPT_CUSTOMREQUEST              使用一个自定义的请求信息来代替"GET"或"HEAD"作为HTTP请求。这对于执行"DELETE" 或者其他更隐蔽的HTTP请求有用。有效值如"GET"，"POST"，"CONNECT"等等。也就是说，不要在这里输入整个HTTP请求行。例如输入"GET /index.html HTTP/1.0\r\n\r\n"是不正确的。在确定服务器支持这个自定义请求的方法前不要使用。
CURLOPT_DEFAULT_PROTOCOL           URL不带协议的时候，使用的默认协议。cURL 7.45.0 中添加，自 PHP 7.0.7 起有效。
CURLOPT_DNS_INTERFACE              设置DNS解析器绑定的网络接口名称。必须是接口名称(不是地址)
CURLOPT_DNS_LOCAL_IP4              设置解析器绑定的ipv4地址。
CURLOPT_DNS_LOCAL_IP6              设置解析器绑定的ipv6地址。
CURLOPT_EGDSOCKET                  类似CURLOPT_RANDOM_FILE，除了一个Entropy Gathering Daemon套接字文件名。
CURLOPT_ENCODING                   HTTP请求头中"Accept-Encoding: "的值。它开启了解码响应。支持的编码有"identity"，"deflate"和"gzip"。如果为空字符串""，请求头会发送所有支持的编码类型。在cURL 7.10中被加入。
CURLOPT_FTPPORT                    这个值将被用来获取供FTP"POST"指令所需要的IP地址。"POST"指令告诉远程服务器连接到我们指定的IP地址。这个字符串可以是纯文本的IP地址、主机名、一个网络接口名（UNIX下）或者只是一个'-'来使用系统默认的IP地址。
CURLOPT_INTERFACE                  网络发送接口名，可以是一个接口名、IP地址或者是一个主机名。
CURLOPT_KEYPASSWD                  使用CURLOPT_SSLKEY或CURLOPT_SSH_PRIVATE_KEYFILE私钥所需的密码。
CURLOPT_KRB4LEVEL                  KRB4 (Kerberos 4) 安全级别。下面的任何值都是有效的(从低到高的顺序)："clear"、"safe"、"confidential"、"private"。如果字符串和这些都不匹配，将使用"private"。这个选项设置为NULL时将禁用KRB4 安全认证。目前KRB4 安全认证只能用于FTP传输。
CURLOPT_LOGIN_OPTIONS              可用于设置特定于协议的登录选项，例如通过“AUTH = NTLM”或“AUTH = \*”身份验证机制，并应与CURLOPT_USERNAME选项一起使用。
CURLOPT_PINNEDPUBLICKEY            设置固定公钥。该字符串可以是固定公钥的文件名。预期的文件格式是“PEM”或“DER”。该字符串也可以是以“sha256 //”开始的任意数量的base64编码的sha256哈希，并用“;”分隔。
CURLOPT_POSTFIELDS                 全部数据使用HTTP协议中的"POST"操作来发送。要发送文件，在文件名前面加上@前缀并使用完整路径。文件类型文件名后面的‘;type=mimetype’格式来指定，这个参数可以通过urlencoded后的字符串类似'para1=val1¶2=val2&...'或使用一个以字段名为键值，字段数据为值的数组。如果value是一个数组，Content-Type头将会被设置成multipart/form-data。从PHP 5.2.0开始，如果使用@前缀将文件传递给此选项，则值必须是数组。从PHP 5.5.0开始，@前缀已被弃用，可以使用CURLFile发送文件。通过将CURLOPT_SAFE_UPLOAD选项设置为TRUE，可以禁止@前缀以安全传递以@开头的值。
CURLOPT_PRIVATE                    任何与这个cURL句柄相关联的数据。随后可以使用curl_getinfo()的CURLINFO_PRIVATE选项来检索此数据。cURL对此数据不做任何处理。当使用cURL多重句柄时，这个私有数据通常是识别标准cURL句柄的唯一键。
CURLOPT_PROXY                      HTTP代理通道。
CURLOPT_PROXY_SERVICE_NAME         代理验证服务名称。
CURLOPT_PROXYUSERPWD               一个用来连接到代理的"[username]:[password]"格式的字符串。
CURLOPT_RANDOM_FILE                一个被用来生成SSL随机数种子的文件名。
CURLOPT_RANGE                      以"X-Y"的形式，其中X和Y都是可选项获取数据的范围，以字节计。HTTP传输线程也支持几个这样的重复项中间用逗号分隔如"X-Y,N-M"。
CURLOPT_REFERER                    在HTTP请求头中"Referer: "的内容。
CURLOPT_SERVICE_NAME               身份验证服务名称。
CURLOPT_SSH_HOST_PUBLIC_KEY_MD5    包含32个十六进制数字的字符串。该字符串应该是远程主机公钥的MD5校验和，并且除非md5sums匹配，否则libcurl将拒绝与主机的连接。此选项仅适用于SCP和SFTP传输。
CURLOPT_SSH_PUBLIC_KEYFILE         公钥的文件名。如果不使用，如果设置了HOME环境变量，则libcurl默认为$ HOME / .ssh / id_dsa.pub;如果没有设置HOME，则当前目录中只有“id_dsa.pub”。
CURLOPT_SSH_PRIVATE_KEYFILE        私钥的文件名。如果不使用，如果HOME环境变量已设置，则libcurl默认为$ HOME / .ssh / id_dsa;如果没有设置HOME，则当前目录中只有“id_dsa”。如果该文件受密码保护，请使用CURLOPT_KEYPASSWD设置密码。
CURLOPT_SSL_CIPHER_LIST            一个SSL的加密算法列表。例如RC4-SHA和TLSv1都是可用的加密列表。
CURLOPT_SSLCERT                    一个包含PEM格式证书的文件名。
CURLOPT_SSLCERTPASSWD              使用CURLOPT_SSLCERT证书需要的密码。
CURLOPT_SSLCERTTYPE                证书的类型。支持的格式有"PEM" (默认值), "DER"和"ENG"。在cURL 7.9.3中被加入。
CURLOPT_SSLENGINE                  用来在CURLOPT_SSLKEY中指定的SSL私钥的加密引擎标识符。
CURLOPT_SSLENGINE_DEFAULT          用来做非对称加密操作的用于非对称加密操作的加密引擎的标识符。
CURLOPT_SSLKEY                     包含SSL私钥的文件名。
CURLOPT_SSLKEYPASSWD               在CURLOPT_SSLKEY中指定了的SSL私钥的密码。由于这个选项包含了敏感的密码信息，记得保证这个PHP脚本的安全。
CURLOPT_SSLKEYTYPE                 CURLOPT_SSLKEY中规定的私钥的加密类型，支持的密钥类型为"PEM"(默认值)、"DER"和"ENG"。
CURLOPT_UNIX_SOCKET_PATH           启用使用Unix域套接字作为连接端点并设置该文件的字符串路径。
CURLOPT_URL                        需要获取的URL地址，也可以在 curl_init()函数中设置。
CURLOPT_USERAGENT                  在HTTP请求中包含一个"User-Agent: "头的字符串。
CURLOPT_USERNAME                   用于身份验证的用户名。
CURLOPT_USERPWD                    传递一个连接中需要的用户名和密码，格式为："[username]:[password]"。
CURLOPT_XOAUTH2_BEARER             指定OAuth 2.0访问令牌。
=================================  ===============================================================================================================================================================================================================================================================================================================================================

第四类

对于下面的这些option的可选参数，value应该被设置一个数组：

========================  ===========================================================================================
选项                        可选value值
========================  ===========================================================================================
CURLOPT_CONNECT_TO        连接到特定的主机和端口，而不是URL的主机和端口。接受格式为HOST:PORT:CONNECT-TO-HOST:CONNECT-TO-PORT的字符串数组。
CURLOPT_HTTP200ALIASES    200响应码数组，数组中的响应码被认为是正确的响应，否则被认为是错误的。在cURL 7.10.3中被加入。
CURLOPT_HTTPHEADER        一个用来设置HTTP头字段的数组。使用如下的形式的数组进行设置： array('Content-type: text/plain', 'Content-length: 100')
CURLOPT_POSTQUOTE         在FTP请求执行完成后，在服务器上执行的一组FTP命令。
CURLOPT_PROXYHEADER       要传递给代理的自定义HTTP标头数组。
CURLOPT_QUOTE             一组先于FTP请求的在服务器上执行的FTP命令。
CURLOPT_RESOLVE           为特定主机和端口对提供自定义地址。主机名，端口和IP地址字符串的数组，每个元素用冒号分隔。格式如下：array("example.com:80:127.0.0.1")
========================  ===========================================================================================

对于下面的这些option的可选参数，value应该被设置一个流资源 （例如使用 fopen()）：

=====================  =======================================
选项                     可选value值
=====================  =======================================
CURLOPT_FILE           设置输出文件的位置，值是一个资源类型，默认为STDOUT (浏览器窗口)。
CURLOPT_INFILE         在上传文件的时候需要读取的文件，值是一个资源类型。
CURLOPT_STDERR         设置一个错误输出地址，值是一个资源类型，取代默认的STDERR。
CURLOPT_WRITEHEADER    设置header部分内容的写入的文件地址，值是一个资源类型。
=====================  =======================================

对于下面的这些option的可选参数，value应该被设置为一个回调函数名：

==========================  =========================================================================================================================================================================================================
选项                          可选value值
==========================  =========================================================================================================================================================================================================
CURLOPT_HEADERFUNCTION      设置一个回调函数，这个函数有两个参数，第一个是cURL的资源句柄，第二个是输出的header数据。header数据的输出必须依赖这个函数，返回已写入的数据大小。
CURLOPT_PASSWDFUNCTION      设置一个回调函数，有三个参数，第一个是cURL的资源句柄，第二个是一个密码提示符，第三个参数是密码长度允许的最大值。返回密码的值。
CURLOPT_PROGRESSFUNCTION    设置一个回调函数，有五个参数，第一个是cURL的资源句柄，第二个是在这个传输中预期要下载的字节总数，第三个是到目前为止下载的字节数，第四个是在这个传输中预期要上传的字节总数，以及第五个是到目前为止上传的字节。注意：回调仅在CURLOPT_NOPROGRESS选项设置为FALSE时调用。返回一个非零值来中止传输。在这种情况下，传输将会设置一个CURLE_ABORTED_BY_CALLBACK错误。
CURLOPT_READFUNCTION        拥有三个参数的回调函数，第一个是参数是会话句柄，第二个是通过选项CURLOPT_INFILE提供给cURL的流资源，第三个是要读取的最大数据量。回调函数必须返回一个长度等于或小于所请求的数据量的字符串，通常通过从传递的流资源中读取。它应该返回一个空字符串作为EOF信号。
CURLOPT_WRITEFUNCTION       拥有两个参数的回调函数，第一个是参数是会话句柄，第二个是要写入数据的字符串。数据必须通过回调来保存。它必须返回写入的确切字节数，否则传输将会中止并出现错误。
==========================  =========================================================================================================================================================================================================

其它值

===============  =========================================
选项               可选value值
===============  =========================================
CURLOPT_SHARE    curl_share_init()的结果。使cURL句柄使用共享句柄中的数据。
===============  =========================================

curl_setopt_array()
-----------------------
``bool curl_setopt_array ( resource $ch , array $options )``

为cURL会话设置多个选项。这个函数对于设置大量的cURL选项很有用，不需要重复调​​用curl_setopt()。

参数：

- ch：由curl_init（）返回的一个cURL句柄。
- options：一个数组，指定要设置的选项及其值。这些键应该是有效的curl_setopt()中常量或它们的整数值。

返回值：

如果所有选项均已成功设置，则返回TRUE。如果某个选项无法成功设置，则立即返回FALSE，忽略选项数组中的后面的选项。

curl_reset()
--------------
``void curl_reset ( resource $ch )``

此函数将给定cURL句柄上设置的所有选项重新初始化为默认值。

curl_exec()
--------------
``mixed curl_exec ( resource $ch )``

执行给定的cURL会话。这个函数应该在初始化一个cURL会话之后被调用，并且会话的所有选项都被设置。

参数：

- ch：由curl_init()返回的一个cURL句柄。

返回值：

成功返回TRUE，失败则返回FALSE。但是，如果设置了CURLOPT_RETURNTRANSFER选项，则会在成功时返回结果，在失败时返回FALSE。

.. note:: 此函数可能会返回布尔值FALSE，但也可能会返回一个非布尔值，其值为FALSE。使用===运算符来测试这个函数的返回值。

curl_close()
-------------
``void curl_close ( resource $ch )``

关闭cURL会话并释放所有资源。 cURL句柄ch也被删除。

curl_copy_handle()
----------------------
``resource curl_copy_handle ( resource $ch )``

复制cURL句柄保持相同的选项。

参数：

- ch：由curl_init（）返回的一个cURL句柄。

返回值：

返回一个新的cURL句柄。

curl_escape()
----------------
``string curl_escape ( resource $ch , string $str )``

这个函数的URL根据RFC 3986对给定的字符串进行编码。

参数：

- ch：由curl_init()返回的一个cURL句柄。
- str：要编码的字符串。

返回值：

返回转义字符串或FALSE失败。

curl_unescape()
------------------
``string curl_unescape ( resource $ch , string $str )``

该函数解码给定的URL编码的字符串。

- ch：由curl_init()返回的一个cURL句柄。
- str：要解码的字符串。

返回值：

返回已解码的字符串或在失败时返回FALSE。

.. note:: curl_unescape()不会将"+"符号()解码为空格。 但是urldecode()会。

curl_file_create()
---------------------
这个函数是CURLFile :: __ construct()一个别名。创建一个CURLFile对象。

curl_multi_init()
-------------------
``resource curl_multi_init ( void )``

允许异步处理多个cURL句柄。成功时返回一个cURL多句柄资源，失败时返回FALSE。

curl_multi_setopt()
-----------------------
``bool curl_multi_setopt ( resource $mh , int $option , mixed $value )``

为cURLmulti句柄设置一个选项。

参数：

- ch：由curl_multi_init()返回的一个cURL句柄。
- option：对CURLMOPT\_\*选项进行设置。
- value：选项中设置的值。

返回值：

成功返回TRUE，失败则返回FALSE。

选项和值得表格：

=====================  ======
选项                     选项的值
=====================  ======
CURLMOPT_PIPELINING    未翻译(http://php.net/manual/en/function.curl-multi-setopt.php)
=====================  ======

curl_multi_add_handle()
----------------------------
``int curl_multi_add_handle ( resource $mh , resource $ch )``

将ch句柄添加到多句柄mh中。

参数：

- mh：由curl_multi_init()返回的一个cURL多重句柄。
- ch：由curl_init()返回的一个cURL句柄。

返回值：

成功返回0，或者CURLM_XXX错误代码之一。

curl_multi_remove_handle()
--------------------------------
``int curl_multi_remove_handle ( resource $mh , resource $ch )``

从给定的mh句柄中移除给定的ch句柄。 ch句柄被删除后，在这个句柄上运行curl_exec()也是完全合法的。在使用的时候移除ch句柄将会有效地阻止涉及该句柄的正在进行的转移。

参数：

- mh：由curl_multi_init()返回的一个cURL多重句柄。
- ch：由curl_init()返回的一个cURL句柄。

返回值：

成功返回0，或者CURLM_XXX错误代码之一。

curl_multi_exec()
---------------------
``int curl_multi_exec ( resource $mh , int &$still_running )``

处理堆栈中的每个句柄。这个方法可以被调用是否一个句柄需要读取或写入数据。这个函数完成后，后台的所有句柄始终在运行。是异步操作。

参数：

- mh：由curl_multi_init()返回的一个cURL多重句柄。
- still_running：引用标志来判断操作是否仍在运行。它返回的是当前正在工作的句柄的数量，0表示所有句柄数据接收完毕才结束工作。

返回值：

在cURL(http://php.net/manual/en/curl.constants.php)预定义常量中定义的一个cURL代码。

- CURLM_CALL_MULTI_PERFORM (-1)：这意味着您应该再次调用curl_multi_exec()，因为仍然有可用于处理的数据。
- CURLM_OK (0)：这意味着有更多的数据可用，但还没有到达。
- 其中一个错误代码：CURLM_BAD_HANDLE，CURLM_OUT_OF_MEMORY，CURLM_INTERNAL_ERROR或CURLM_BAD_SOCKET。所有这些都表明我们需要停止处理。

.. note:: 这只会返回有关整个multi堆栈的错误。即使此函数返回CURLM_OK，个别传输仍可能发生问题。

curl_multi_select()
-----------------------
``int curl_multi_select ( resource $mh [, float $timeout = 1.0 ] )``

阻塞直到任何curl_multi连接上有活动为止。

参数：

- mh：由curl_multi_init()返回的一个cURL多重句柄。
- timeout：等待回应时间，以秒为单位。

返回值：

成功时，返回描述符集中包含的描述符的数量。如果任何描述符上没有任何活动，则这可能是0。该函数将在选择失败时返回-1（来自底层的select系统调用）。

.. code-block:: php

    <?php
    // 该循环在整个url请求期间是个死循环，它会轻易导致CPU占用100%。
    do { $n=curl_multi_exec($mh,$active); } while ($active);

    // 修改代码
    do {// 对所有的句柄发出请求，并监控活动句柄的个数
        $mrc = curl_multi_exec($mh,$active);
    } while ($mrc == CURLM_CALL_MULTI_PERFORM);
    /*
    因为$active要等全部url数据接受完毕才变成false，所以这里用到了curl_multi_exec的返回值判断是否还有数据，
    当有数据的时候就不停调用curl_multi_exec，暂时没有数据就进入select阶段，新数据一来就可以被唤醒继续执行。
    这里的好处就是CPU的无谓消耗没有了。
     */
    while ($active and $mrc == CURLM_OK) {
        if (curl_multi_select($mh) != -1) {
            do {
                $mrc = curl_multi_exec($mh, $active);
            } while ($mrc == CURLM_CALL_MULTI_PERFORM);
        }
    }
    ?>

第一个负责清理curl缓冲区。第二个是负责等待更多的信息，然后得到这些信息。这是所谓的阻塞I/O的一个例子。我们阻止程序的其余部分的执行，直到网络I/O完成。虽然这并不是处理网络I/O的最佳方式，但它确实是我们在单线程，同步PHP中唯一的选择。 **由于版本升级，上面代码会造成死循环，需要使用如下代码替代：**

.. code-block:: php

    <?php
    do {
        $mrc = curl_multi_exec($multi, $active);
    } while ($mrc == CURLM_CALL_MULTI_PERFORM);

    while ($active && $mrc == CURLM_OK) {
        //check for results and execute until everything is done

        if (curl_multi_select($multi) == -1) {
            //if it returns -1, wait a bit, but go forward anyways!
            usleep(100);
        }

        //do something with the return values
        while(($info = curl_multi_info_read($multi)) !== false){
            if ($info["result"] == CURLE_OK){
                $content = curl_multi_getcontent($info["handle"]);
                do_something($content);
            }
        }

        do {
            $mrc = curl_multi_exec($multi, $active);
        } while ($mrc == CURLM_CALL_MULTI_PERFORM);
    }
    ?>

curl_multi_info_read()
---------------------------
``array curl_multi_info_read ( resource $mh [, int &$msgs_in_queue = NULL ] )``

询问multi句柄是否有来自单个传输的消息或信息。消息可能包含来自传输的错误代码或传输完成的信息。

重复调用这个函数会每次都返回一个新的结果，直到返回一个FALSE作为没有更多的信号。msgs_in_queue引用参数将包含调用此函数后剩余的消息数。

.. warning:: 调用curl_multi_remove_handle()后，返回的资源指向的数据将无法生存。

参数：

- mh：由curl_multi_init()返回的一个cURL多重句柄。
- msgs_in_queue：仍在队列中的消息数量。

返回值：

成功时，返回消息的关联数组，失败时返回FALSE。

返回数组的内容

========  =====================================
键         值
========  =====================================
msg       CURLMSG_DONE常量。其他返回值目前不可用。
result    其中一个CURLE\_\*常量。如果一切正常，CURLE_OK将是结果。
handle    curl类型的资源表示它所关联的句柄。
========  =====================================

curl_multi_getcontent()
---------------------------
``string curl_multi_getcontent ( resource $ch )``

如果为特定句柄设置了CURLOPT_RETURNTRANSFER选项，则此函数将以字符串的形式返回该cURL句柄的内容。

参数：

- ch：由curl_init()返回的一个cURL句柄。

返回值：

如果设置了CURLOPT_RETURNTRANSFER选项，则会在成功时返回结果。

curl_multi_close()
---------------------
``void curl_multi_close ( resource $mh )``

关闭一组cURL手柄。

curl_share_init()
--------------------
``resource curl_share_init ( void )``

允许在cURL句柄之间共享数据。返回“cURL共享句柄”类型的资源。

curl_share_setopt()
-----------------------
``bool curl_share_setopt ( resource $sh , int $option , string $value )``

在给定的cURL共享句柄上设置一个选项。

参数：

- sh：由curl_share_init()返回的cURL共享句柄。
- option：未翻译 http://php.net/manual/en/function.curl-share-setopt.php
- value：未翻译

返回值：

成功返回TRUE，失败则返回FALSE。

curl_share_close()
---------------------
``void curl_share_close ( resource $sh )``

关闭cURL共享句柄并释放所有资源。

curl_errno()
--------------
``int curl_errno ( resource $ch )``

返回最后一个cURL操作的错误号。

参数：

ch：由curl_init()返回的一个cURL句柄。

返回值：

如果没有发生错误，则返回错误号或0（零）。

curl_error()
--------------
``string curl_error ( resource $ch )``

返回最后一个cURL操作的明文错误消息。

参数：

ch：由curl_init()返回的一个cURL句柄。

返回值：

如果没有发生错误则返回''(空字符串)，否则返回错误消息。

curl_strerror()
-----------------
``string curl_strerror ( int $errornum )``

返回描述给定错误代码的文本错误消息。

参数：

- errornum：其中一个cURL错误代码常量。

返回值：

返回错误描述或NULL表示无效的错误代码。

curl_multi_errno()
----------------------
``int curl_multi_errno ( resource $mh )``

返回一个最后multi curl错误号的整数或在失败时为FALSE。

curl_multi_strerror()
------------------------
``string curl_multi_strerror ( int $errornum )``

返回描述给定CURLM错误代码的文本错误消息。

curl_share_errno()
----------------------
``int curl_share_errno ( resource $sh )``

返回一个包含最后一个共享curl错误号的整数。

curl_share_strerror()
-------------------------
``string curl_share_strerror ( int $errornum )``

返回描述给定错误代码的文本错误消息。

CURLFile
-----------
CURLFile应该被用来上传一个带有CURLOPT_POSTFIELDS的文件。

.. code-block:: php

    <?php
    CURLFile {
        /* Properties */
        public $name ; // 上传文件的名称
        public $mime ; // 文件的MIME类型，默认是application/octet-stream
        public $postname ; // 上传数据中文件的名称（默认为name属性）。
        /* Methods */
        public __construct ( string $filename [, string $mimetype [, string $postname ]] )
        public string getFilename ( void )
        public string getMimeType ( void )
        public string getPostFilename ( void )
        public void setMimeType ( string $mime )
        public void setPostFilename ( string $postname )
        public void __wakeup ( void )
    }
    ?>

``$cfile = curl_file_create('resource/test.png','image/png','testpic');``

``$cfile = new CURLFile('resource/test.png','image/png','testpic');``



使用示例
========

简单函数封装
---------------

.. code-block:: php

    <?php
    // 异步访问远程方法，$urls为远程访问的数组
    function remote($urls) {
        if (!is_array($urls) or count($urls) == 0) {
            return false;
        }
        $curl = $text = array();
        $handle = curl_multi_init();
        foreach($urls as $k => $v) {
            $nurl[$k]= preg_replace('~([^:\/\.]+)~ei', "rawurlencode('\\1')", $v);
            $curl[$k] = curl_init($nurl[$k]);
            curl_setopt($curl[$k], CURLOPT_RETURNTRANSFER, 1);
            curl_setopt($curl[$k], CURLOPT_HEADER, 0);
            curl_multi_add_handle ($handle, $curl[$k]);
        }
        $active = null;
        do {
            $mrc = curl_multi_exec($handle, $active);
        } while ($mrc == CURLM_CALL_MULTI_PERFORM);

        while ($active && $mrc == CURLM_OK) {
            // Wait for activity on any curl-connection
            if (curl_multi_select($handle) == -1) {
                usleep(1);
            }

            // Continue to exec until curl is ready to
            // give us more data
            do {
                $mrc = curl_multi_exec($handle, $active);
            } while ($mrc == CURLM_CALL_MULTI_PERFORM);
        }
        foreach ($curl as $k => $v) {
            if (curl_error($curl[$k]) == "") {
                $text[$k] = (string) curl_multi_getcontent($curl[$k]);
            }
            curl_multi_remove_handle($handle, $curl[$k]);
            curl_close($curl[$k]);
        }
        curl_multi_close($handle);
        return $text;
    }
    //演示例子
    $text = remote(array('http://www.jb51.net/','http://www.baidu.com/'));
    print_r($text);
    ?>

简单类封装
------------
parallelcurl.php(https://github.com/petewarden/ParallelCurl)

.. code-block:: php

    <?php
    /*
     1. 要使用它，首先创建ParallelCurl对象：

     $parallelcurl = new ParallelCurl(10);

     构造函数的第一个参数是允许的同时请求的URL个数。您可以稍后使用setMaxRequests()改变该值
     第二个可选参数是curl_setopt_array()使用的选项数组

     2. 接下来，开始请求URL：

     $parallelcurl->startRequest('http://example.com', 'on_request_done', array('something'));

     第一个参数是请求URL地址，第二个参数是请求完成后回调函数，第三个是传递给回调函数的任意数据
     只要没有超过限制数startRequest调用立即开始。一旦请求完成，将会调用回调函数。可选的第四个参数
    如果你在参数中的那个位置传入一个数组，POST方法将被使用，而数组的内容控制POST参数的内容。

    3. 定义回调函数：

     on_request_done($content, 'http://example.com', $ch, array('something'));

     该函数有四个参数，第一个是URL响应的内容，第二个是请求的URL，第三个是请求的curl句柄，用来查询返回的信息
     第四个关联该请求的用户定义的数据。

    4. 由于您的脚本末尾可能有未完成的请求，您必须调用：

    $parallelcurl->finishAllRequests();

    在你退出之前。如果你不这样做，最终的请求可能是未经处理的！但是由于对象在清除前
    会自动调用，所有也可以省略该步骤。

    */
    class ParallelCurl {
        public $max_requests;
        public $options;
        public $outstanding_requests;
        public $multi_handle;

        public function __construct($in_max_requests = 10, $in_options = array()) {
            $this->max_requests = $in_max_requests;
            $this->options = $in_options;

            $this->outstanding_requests = array();
            $this->multi_handle = curl_multi_init();
        }

        //Ensure all the requests finish nicely
        public function __destruct() {
            $this->finishAllRequests();
        }
        // Sets how many requests can be outstanding at once before we block and wait for one to
        // finish before starting the next one
        public function setMaxRequests($in_max_requests) {
            $this->max_requests = $in_max_requests;
        }

        // Sets the options to pass to curl, using the format of curl_setopt_array()
        public function setOptions($in_options) {
            $this->options = $in_options;
        }
        // Start a fetch from the $url address, calling the $callback function passing the optional
        // $user_data value. The callback should accept 3 arguments, the url, curl handle and user
        // data, eg on_request_done($url, $ch, $user_data);
        public function startRequest($url, $callback, $user_data = array(), $post_fields=null) {
            if( $this->max_requests > 0 )
                $this->waitForOutstandingRequestsToDropBelow($this->max_requests);

            $ch = curl_init();
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
            curl_setopt_array($ch, $this->options);
            curl_setopt($ch, CURLOPT_URL, $url);
            if (isset($post_fields)) {
                curl_setopt($ch, CURLOPT_POST, TRUE);
                curl_setopt($ch, CURLOPT_POSTFIELDS, $post_fields);
            }

            curl_multi_add_handle($this->multi_handle, $ch);

            $ch_array_key = (int)$ch;
            $this->outstanding_requests[$ch_array_key] = array(
                'url' => $url,
                'callback' => $callback,
                'user_data' => $user_data,
            );

            $this->checkForCompletedRequests();
        }

        // You *MUST* call this function at the end of your script. It waits for any running requests
        // to complete, and calls their callback functions
        public function finishAllRequests() {
            $this->waitForOutstandingRequestsToDropBelow(1);
        }
        // Checks to see if any of the outstanding requests have finished
        private function checkForCompletedRequests() {
            $active = null;
            do {
                $mrc = curl_multi_exec($this->multi_handle, $active);
            } while ($mrc == CURLM_CALL_MULTI_PERFORM);

            while ($active && $mrc == CURLM_OK) {
                if (curl_multi_select($this->multi_handle) == -1) {
                    usleep(100);
                }
                do {
                    $mrc = curl_multi_exec($this->multi_handle, $active);
                } while ($mrc == CURLM_CALL_MULTI_PERFORM);
            }

            // Now grab the information about the completed requests
            while ($info = curl_multi_info_read($this->multi_handle)) {

                $ch = $info['handle'];
                $ch_array_key = (int)$ch;

                if (!isset($this->outstanding_requests[$ch_array_key])) {
                    die("Error - handle wasn't found in requests: '$ch' in ".
                        print_r($this->outstanding_requests, true));
                }

                $request = $this->outstanding_requests[$ch_array_key];
                $url = $request['url'];
                $content = curl_multi_getcontent($ch);
                $callback = $request['callback'];
                $user_data = $request['user_data'];
                // 调用回调函数
                call_user_func($callback, $content, $url, $ch, $user_data);

                unset($this->outstanding_requests[$ch_array_key]);

                curl_multi_remove_handle($this->multi_handle, $ch);
            }

        }

        // Blocks until there's less than the specified number of requests outstanding
        private function waitForOutstandingRequestsToDropBelow($max)
        {
            while (1) {
                $this->checkForCompletedRequests();
                if (count($this->outstanding_requests)<$max)
                    break;

                usleep(10000);
            }
        }
    }

    /*
     ParallelCurl 测试脚本，使用不到10个进程来获取100个不同的Google搜索结果
    */
    //require_once('parallelcurl.php');
    define ('SEARCH_URL_PREFIX', 'http://ajax.googleapis.com/ajax/services/search/web?v=1.0&rsz=large&filter=0');
    // This function gets called back for each request that completes
    function on_request_done($content, $url, $ch, $search) {

        $httpcode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        if ($httpcode !== 200) {
            print "Fetch error $httpcode for '$url'\n";
            return;
        }
        $responseobject = json_decode($content, true);
        if (empty($responseobject['responseData']['results'])) {
            print "No results found for '$search'\n";
            return;
        }
        print "********\n";
        print "$search:\n";
        print "********\n";
        $allresponseresults = $responseobject['responseData']['results'];
        foreach ($allresponseresults as $responseresult) {
            $title = $responseresult['title'];
            print "$title\n";
        }
    }
    // The terms to search for on Google
    $terms_list = array(
        "John", "Mary",
        "William", "Anna",
        "James", "Emma",
        "George", "Elizabeth",
        "Charles", "Margaret",
        "Frank", "Minnie",
        "Joseph", "Ida",
        "Henry", "Bertha",
        "Robert", "Clara",
        "Thomas", "Alice",
        "Edward", "Annie",
        "Harry", "Florence",
        "Walter", "Bessie",
        "Arthur", "Grace",
        "Fred", "Ethel",
        "Albert", "Sarah",
        "Samuel", "Ella",
        "Clarence", "Martha",
        "Louis", "Nellie",
        "David", "Mabel",
        "Joe", "Laura",
        "Charlie", "Carrie",
        "Richard", "Cora",
        "Ernest", "Helen",
        "Roy", "Maude",
        "Will", "Lillian",
        "Andrew", "Gertrude",
        "Jesse", "Rose",
        "Oscar", "Edna",
        "Willie", "Pearl",
        "Daniel", "Edith",
        "Benjamin", "Jennie",
        "Carl", "Hattie",
        "Sam", "Mattie",
        "Alfred", "Eva",
        "Earl", "Julia",
        "Peter", "Myrtle",
        "Elmer", "Louise",
        "Frederick", "Lillie",
        "Howard", "Jessie",
        "Lewis", "Frances",
        "Ralph", "Catherine",
        "Herbert", "Lula",
        "Paul", "Lena",
        "Lee", "Marie",
        "Tom", "Ada",
        "Herman", "Josephine",
        "Martin", "Fanny",
        "Jacob", "Lucy",
        "Michael", "Dora",
    );
    if (isset($argv[1])) {
        $max_requests = $argv[1];
    } else {
        $max_requests = 10;
    }
    $curl_options = array(
        CURLOPT_SSL_VERIFYPEER => FALSE,
        CURLOPT_SSL_VERIFYHOST => FALSE,
        CURLOPT_USERAGENT, 'Parallel Curl test script',
    );
    $parallel_curl = new ParallelCurl($max_requests, $curl_options);
    foreach ($terms_list as $terms) {
        $search = '"'.$terms.' is a"';
        $search_url = SEARCH_URL_PREFIX.'&q='.urlencode($terms);
        $parallel_curl->startRequest($search_url, 'on_request_done', $search);
    }
    // This should be called when you need to wait for the requests to finish.
    // This will automatically run on destruct of the ParallelCurl object, so the next line is optional.
    $parallel_curl->finishAllRequests();
    ?>

高级工具类封装
------------------
https://github.com/ares333/php-curl


