********************************
configure-make中的prefix-DESTDIR区别
********************************

只使用 ``./configure --prefix`` 当使用 ``make install`` 时，被安装到 ``prefix`` 为前缀的指定目录。

使用 ``./configure --prefix`` 和 ``make install DESTDIR`` 目标安装目录为 ``DESTDIR/prefix`` 。

只要使用了 ``make install prefix=DIR`` 安装目录为 ``DIR`` 。

注意

- ``./configure -prefix`` 必须是绝对路径
- ``make install prefix`` 也要绝对路径，虽然可以是相对路径，但安装时，因为切换工作目录，导致安装目录错误！