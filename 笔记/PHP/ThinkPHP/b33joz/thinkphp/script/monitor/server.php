<?php
/**
 * 监控服务 ws http 8811
 * Created by PhpStorm.
 * User: baidu
 * Date: 18/4/7
 * Time: 下午10:00
 * 如果想在这个页面使用ThinkPHP框架函数需要像ws.php一样引入 start.php文件和
 * 解析ThinkPHP方法的 think\Container::get('app', [APP_PATH])->run()->send();
 *
 *
 */

class Server {
    const PORT = 8811;

    public function port() {
		//shell 命令
        $shell  =  "netstat -anp 2>/dev/null | grep ". self::PORT . " | grep LISTEN | wc -l";
		//PHP 执行shell命令
        $result = shell_exec($shell);
        if($result != 1) {
            // 发送报警服务 邮件 短信
            /// todo
            echo date("Ymd H:i:s")."error".PHP_EOL;
        } else {
            echo date("Ymd H:i:s")."succss".PHP_EOL;
        }
    }
}

// nohup  swoole 定时器当脚本执行之后每两秒执行一次
// 如果有输出 就把输出内容保存到 /root/swoole.txt  文件
//让这个脚本在后台执行的命令: nohup /home/wwwroot/default/b33joz/thinkphp/script/monitor/server.php >  /root/swoole.txt &
swoole_timer_tick(2000, function($timer_id) {
	 //调用Server 类下的 port() 方法
    (new Server())->port();
    echo "time-start".PHP_EOL;
});
