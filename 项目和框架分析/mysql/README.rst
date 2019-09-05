https://blog.csdn.net/heirenheiren/article/details/7896546 (分区分表的区别)

https://www.teakki.com/p/57e22ceda16367940da636a0 (mysqld_safe 源码分析)

对于rh打包的mysql安装：


安装完成后：

- 先切换到指定版本mysql，scl enable rh-mysql56 bash 来启动；如果你不知道安装包名称，可以通过 scl -l 来查看安装列表；
- 运行mysql_install_db命令来初始化配置文件和数据库文件；
- 运行systemctl start rh-mysql56-mysqld 来启动mysqld，如果你不知道服务名称，可以通过命令 systemctl list-units | grep mysql 来获取；
- 运行mysql_secure_installation 安全配置向导；

如果想开机自动使用指定版本的数据库，可以通过在