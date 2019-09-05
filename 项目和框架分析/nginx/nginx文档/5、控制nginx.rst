*******
控制nginx
*******

``nginx`` 可以通过信号控制。主进程的进程 ``ID`` 默认被写入 ``/usr/local/nginx/logs/nginx.pid`` 文件中。此名称可以在配置时更改，也可以在 ``nginx.conf`` 中使用 ``pid`` 指令指定。主进程支持以下信号:

+-----------+--------------------------------------------------------------------------------------------------+
| TERM, INT | 快速停止                                                                                         |
+===========+==================================================================================================+
| QUIT      | 优雅地停止                                                                                       |
+-----------+--------------------------------------------------------------------------------------------------+
| HUP       | 更改配置，跟上时区的变化(仅适用于FreeBSD和Linux)，使用新配置启动新工作进程，优雅地关闭旧工作进程 |
+-----------+--------------------------------------------------------------------------------------------------+
| USR1      | 重新打开日志文件                                                                                 |
+-----------+--------------------------------------------------------------------------------------------------+
| USR2      | 升级可执行文件                                                                                   |
+-----------+--------------------------------------------------------------------------------------------------+
| WINCH     | 优雅地关闭工作进程                                                                               |
+-----------+--------------------------------------------------------------------------------------------------+

虽然不是必需的，但是也可以用信号来控制每个独立的工作进程。支持的信号有:

+-----------+--------------------------------------+
| TERM, INT | 快速停止                             |
+===========+======================================+
| QUIT      | 优雅地停止                           |
+-----------+--------------------------------------+
| USR1      | 重新打开日志文件                     |
+-----------+--------------------------------------+
| WINCH     | 异常终止调试(需要启用 debug_points ) |
+-----------+--------------------------------------+

改变配置
========
要让 ``nginx`` 重新读取配置文件，应该向主进程发送一个 ``HUP`` 信号。主进程首先检查语法的有效性，然后尝试应用新的配置，即打开日志文件和新的监听套接字。如果失败，则回滚更改并继续使用旧配置。如果成功，它将启动新的工作进程，并向旧的工作进程发送消息，请求它们优雅地关闭。旧工作进程会先关闭监听套接字并继续为之前的客户端提供服务。在为所有旧的客户端提供完服务之后，工作进程才关闭。

让我们通过例子来说明这一点。假设 ``nginx`` 在 ``FreeBSD`` 上运行如下命令：

.. code-block:: shell

    ps axw -o pid,ppid,user,%cpu,vsz,wchan,command | egrep '(nginx|PID)'

会产生以下输出:

.. code-block:: shell

	  PID  PPID USER    %CPU   VSZ WCHAN  COMMAND
	33126     1 root     0.0  1148 pause  nginx: master process /usr/local/nginx/sbin/nginx
	33127 33126 nobody   0.0  1380 kqread nginx: worker process (nginx)
	33128 33126 nobody   0.0  1364 kqread nginx: worker process (nginx)
	33129 33126 nobody   0.0  1364 kqread nginx: worker process (nginx)

如果将 ``HUP`` 命令发送 到主进程，输出为:

.. code-block:: shell

	  PID  PPID USER    %CPU   VSZ WCHAN  COMMAND
	33126     1 root     0.0  1164 pause  nginx: master process /usr/local/nginx/sbin/nginx
	33129 33126 nobody   0.0  1380 kqread nginx: worker process is shutting down (nginx)
	33134 33126 nobody   0.0  1368 kqread nginx: worker process (nginx)
	33135 33126 nobody   0.0  1368 kqread nginx: worker process (nginx)
	33136 33126 nobody   0.0  1368 kqread nginx: worker process (nginx)

其中一个 ``PID`` 为 ``33129`` 的旧工作进程仍然在工作。一段时间后，它会退出:

.. code-block:: shell

	  PID  PPID USER    %CPU   VSZ WCHAN  COMMAND
	33126     1 root     0.0  1164 pause  nginx: master process /usr/local/nginx/sbin/nginx
	33134 33126 nobody   0.0  1368 kqread nginx: worker process (nginx)
	33135 33126 nobody   0.0  1368 kqread nginx: worker process (nginx)
	33136 33126 nobody   0.0  1368 kqread nginx: worker process (nginx)

分割日志文件
===========
要分割日志文件，首先需要重命名它们。之后，发送 ``USR1`` 信号到主进程。然后，主进程将重新打开所有当前打开的日志文件，并将它们分配正在运行工作进程的非特权用户。成功重新打开后，主进程关闭所有打开的文件，并向工作进程发送消息，要求它们重新打开文件。工作进程也打开新文件并立即关闭旧文件。因此，旧文件几乎可以立即用于后期处理，如压缩。

动态升级可执行文件
=================
要升级服务器的可执行文件，首先应该用新的可执行文件替换旧文件。之后，发送 ``USR2`` 信号到主进程。主进程首先将它保存进程 ``ID`` 的文件重命名为一个后缀为 ``.oldbin`` 的新文件，例如 ``/usr/local/nginx/logs/nginx.pid.oldbin`` ，然后启动一个新的可执行文件来依次启动新的工作进程:

.. code-block:: shell

	  PID  PPID USER    %CPU   VSZ WCHAN  COMMAND
	33126     1 root     0.0  1164 pause  nginx: master process /usr/local/nginx/sbin/nginx
	33134 33126 nobody   0.0  1368 kqread nginx: worker process (nginx)
	33135 33126 nobody   0.0  1380 kqread nginx: worker process (nginx)
	33136 33126 nobody   0.0  1368 kqread nginx: worker process (nginx)
	36264 33126 root     0.0  1148 pause  nginx: master process /usr/local/nginx/sbin/nginx
	36265 36264 nobody   0.0  1364 kqread nginx: worker process (nginx)
	36266 36264 nobody   0.0  1364 kqread nginx: worker process (nginx)
	36267 36264 nobody   0.0  1364 kqread nginx: worker process (nginx)

之后，所有的工作进程(旧的和新的)继续接受请求。如果发送 ``WINCH`` 信号到第一个主进程，它将发送消息给它的工作进程，请求它们优雅地关闭，它们将开始退出:

.. code-block:: shell

	  PID  PPID USER    %CPU   VSZ WCHAN  COMMAND
	33126     1 root     0.0  1164 pause  nginx: master process /usr/local/nginx/sbin/nginx
	33135 33126 nobody   0.0  1380 kqread nginx: worker process is shutting down (nginx)
	36264 33126 root     0.0  1148 pause  nginx: master process /usr/local/nginx/sbin/nginx
	36265 36264 nobody   0.0  1364 kqread nginx: worker process (nginx)
	36266 36264 nobody   0.0  1364 kqread nginx: worker process (nginx)
	36267 36264 nobody   0.0  1364 kqread nginx: worker process (nginx)

一段时间后，只有新的 ``worker`` 进程会处理请求:

.. code-block:: shell

	  PID  PPID USER    %CPU   VSZ WCHAN  COMMAND
	33126     1 root     0.0  1164 pause  nginx: master process /usr/local/nginx/sbin/nginx
	36264 33126 root     0.0  1148 pause  nginx: master process /usr/local/nginx/sbin/nginx
	36265 36264 nobody   0.0  1364 kqread nginx: worker process (nginx)
	36266 36264 nobody   0.0  1364 kqread nginx: worker process (nginx)
	36267 36264 nobody   0.0  1364 kqread nginx: worker process (nginx)

应该注意的是，旧的主进程没有关闭它的监听套接字，如果需要，可以使用它来重新启动工作进程。如果由于某种原因，新的可执行文件的没有正常工作，可以采取以下措施之一:

- 将 ``HUP`` 信号发送到旧的主进程。旧的主进程将在不重新读取配置的情况下启动新的工作进程。之后，通过向新的主进程发送 ``QUIT`` 信号，可以优雅地关闭所有新进程。
- 将 ``TERM`` 信号发送到新的主进程。然后，它将向它的工作进程发送一条消息，请求它们立即退出，工作进程几乎都会立即退出。(如果新进程由于某种原因不退出，则可以向它们发送 ``KILL`` 信号，迫使它们退出。)当新的主进程退出时，旧的主进程将自动启动新的工作进程。
如果新的主进程退出，那么旧的主进程会将包含有进程ID的文件的文件名的 ``.oldbin`` 后缀去掉。

如果升级成功，应该发送 ``QUIT`` 信号到旧的主进程，只保留新进程:

.. code-block:: shell

	  PID  PPID USER    %CPU   VSZ WCHAN  COMMAND
	36264     1 root     0.0  1148 pause  nginx: master process /usr/local/nginx/sbin/nginx
	36265 36264 nobody   0.0  1364 kqread nginx: worker process (nginx)
	36266 36264 nobody   0.0  1364 kqread nginx: worker process (nginx)
	36267 36264 nobody   0.0  1364 kqread nginx: worker process (nginx)
