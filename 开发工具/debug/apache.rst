********
apache问题
********

apache服务器调试连接总是中断问题
==============================
在php-fcgi.conf文件中插入如下配置。


.. code-block:: conf

	FcgidIOTimeout 3840
	FcgidIdleTimeout 3840
	FcgidConnectTimeout 3600
	FcgidBusyTimeout 3600
	ProcessLifeTime 3600








