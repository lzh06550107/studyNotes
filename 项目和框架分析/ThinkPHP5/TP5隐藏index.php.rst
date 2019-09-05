**************
TP5隐藏index.php
**************

首先要开启服务器的 rewrite 模块；

一，找到/public/.htaccess文件，如果你的入口文件已经移动到根目录下，那么你的.htaccess文件也要剪切到根目录下，总之要确保.htaccess跟入口的index.php保持同级。

二，根据你的php环境分别设置.htaccess文件：

Apache：

.. code-block:: ini

    <IfModule mod_rewrite.c>
    Options +FollowSymlinks -Multiviews
    RewriteEngine on
    RewriteCond %{REQUEST_FILENAME} !-d
    RewriteCond %{REQUEST_FILENAME} !-f
    RewriteRule ^(.*)$ index.php/$1 [QSA,PT,L]
    </IfModule>

phpstudy：

.. code-block:: ini

    <IfModule mod_rewrite.c>
    Options +FollowSymlinks -Multiviews
    RewriteEngine on
    RewriteCond %{REQUEST_FILENAME} !-d
    RewriteCond %{REQUEST_FILENAME} !-f
    RewriteRule ^(.*)$ index.php [L,E=PATH_INFO:$1]
    </IfModule>

Nginx（在Nginx.conf中添加）：

.. code-block:: ini

    location / { // …..省略部分代码
        if (!-e $request_filename) {
            rewrite  ^(.*)$  /index.php?s=/$1  last;
            break;
        }
    }