***********
记录日志到syslog
***********

``error_log`` 和 ``access_log`` 指令支持将日志记录到 ``syslog`` 。以下参数将日志配置到 ``syslog`` ：

- ``server=address`` 参数定义 ``syslog`` 服务器的地址。地址可以指定为域名或IP地址，端口可选，也可以指定为带 ``unix:`` 前缀的 UNIX-domain socket 路径。如果未指定端口，则使用 UDP 514 端口。如果一个域名解析为多个 IP 地址，则使用第一个解析的地址。
- ``facility=string`` 参数设置在 RFC 3164 中定义的 ``syslog`` 消息设施。设施可以是 “kern”, “user”, “mail”, “daemon”, “auth”, “intern”, “lpr”, “news”, “uucp”, “clock”, “authpriv”, “ftp”, “ntp”, “audit”, “alert”, “cron”, “local0”..“local7”中的一个。默认设置是“local7”。
- ``severity=string`` 设置 ``access_log`` 的 ``syslog`` 消息的严重性，在 RFC 3164 中有定义。可选的值与 ``error_log`` 指令的第二个参数( ``level`` )相同。默认设置是 ``info`` 。

.. note:: 错误消息的严重程度由 nginx 决定，因此在 error_log 指令中忽略了该参数。

- ``tag=string`` 参数设置 ``syslog`` 消息的标签。默认设置是 ``nginx`` 。 ``nohostname`` 禁止在 ``syslog`` 消息头中添加 ``hostname`` 字段(1.9.7) 。

syslog示例配置:

.. code-block:: shell

	error_log syslog:server=192.168.1.1 debug;

	access_log syslog:server=unix:/var/log/nginx.sock,nohostname;
	access_log syslog:server=[2001:db8::1]:12345,facility=local7,tag=nginx,severity=info combined;


.. note:: 从版本1.7.1开始，就可以记录日志到 syslog 。 记录日志到 syslog 功能作为我们的商业订阅版的一部分，从1.5.3版本开始就可以使用。










