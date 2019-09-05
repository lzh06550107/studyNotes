*********************
nginx+php-fpm+php日志配置
*********************

I was able to activate it, if anyone needs, just follow these steps:

1: edit:
sudo vim /usr/local/etc/php-fpm.d/www.conf

2: add to the end of the file:

catch_workers_output = yes

php_flag[display_errors] = on
php_admin_value[error_log] = /var/log/fpm-php.www.log
php_admin_flag[log_errors] = on
3: create the log file, so php-fpm can write on it:(保证进程具有对该文件操作权限)
touch /var/log/fpm-php.www.log && chmod 777 /var/log/fpm-php.www.log

- http://www.nginx.cn/666.html
- https://nbczw8750.iteye.com/blog/2316781



