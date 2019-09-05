**********************************
php.exe php-cgi.exe php-win.exe的区别
**********************************

php.exe(linux下是php/bin/php)是提供来在命令行（命令行解释器）执行PHP文件的工具，比如你在有文件abc.php，那么你可以在CMD命令提示符下执行命令php.exe abc.php来运行这个PHP文件。

php-cgi.exe(linux下是php/bin/php-cgi)是提供来作为cgi(cgi解释器)使用的，区别是在文件输出所有内容之前，会自动输出一个下面的HTTP头：
X-Powered-By: PHP/5.2.5
Content-type: text/html

php-win.exe也可以执行PHP文件，区别是打开控制台，不显示输出内容。可以用来编写无需显示界面的文件处理、网路服务等程序