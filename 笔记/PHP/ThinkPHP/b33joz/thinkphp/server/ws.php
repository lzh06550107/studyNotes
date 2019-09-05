<?php
/**
 * ws 优化 基础类库
 * User: singwa
 * Date: 18/3/2
 * Time: 上午12:34
 */

class Ws {
	
    CONST HOST = "0.0.0.0";
    CONST PORT = 8812;
    CONST CHART_PORT=8811;
    CONST live_game_key = 'live_game_key';

    public $ws = null;
    public function __construct() {
        $this->ws = new swoole_websocket_server(self::HOST, self::PORT);
		
		//监听8811 端口
        $this->ws->listen(self::HOST,self::CHART_PORT,SWOOLE_SOCK_TCP);
        $this->ws->set(
            [
  		'enable_static_handler'=>true,
		'document_root'=>"/home/wwwroot/default/b33joz/thinkphp/public/static",
                'worker_num' => 4,
                'task_worker_num' => 4,
            ]
        );
  	$this->ws->on("open", [$this, 'onOpen']);
        $this->ws->on("message", [$this, 'onMessage']);
        $this->ws->on("workerstart",[$this,'onWorkerStart']);
        $this->ws->on("request",[$this,'onRequest']);
        $this->ws->on("task", [$this, 'onTask']);
        $this->ws->on("finish", [$this, 'onFinish']);
        $this->ws->on("close", [$this, 'onClose']);

        $this->ws->start();
    }
    /**
     *
     *
     */
    public function onWorkerStart($server,$worker_id){
   
    	 
    //定义应用目录
    define('APP_PATH',__DIR__.'/../application/');
    //加载框架里面的文件
      require __DIR__.'/../thinkphp/start.php';
 
    
    }
    /**
     *  request 请求回调
     *
     */
     public function onRequest($request,$response){
		 
		 //当有swoole请求数据时把swoole请求转化为ThinkPHP请求让ThinkPHP能读懂请求
		 //注意：swoole结合ThinkPHP会有很多请求上的冲突需要转换，这里只是列出了常用的几项当有请求上出现问题时需要先排除是否为swoole与ThinkPHP的请求冲突
		 //swoole请求还有一个缓存的问题，所以所有的请求不应该经过缓存。ThinkPHP自身有判断请求地址是否有缓存如果有就用缓存的地址，这样swoole里不能正常请求到数据。
		 //解决方法是 打开ThinkPHP核心文件  ./ThinkPHP/library/think/Request.php  找到 function pachinfo() 和 function path()方法分别把 if (is_null($this->pathinfo)) 和 if (is_null($this->path))注释掉
		$_SERVER=[];
        if(isset($request->server)){
	    foreach($request->server as $k=>$v){
		$_SERVER[strtoupper($k)]=$v;
		}
	}

        if(isset($request->header)){
        foreach($request->header as $k=>$v){
           $_SERVER[strtoupper($k)]=$v;
        }
        }
		
        $_GET=[];
        if(isset($request->get)){
       	    foreach($request->get as $k=>$v){
               $_GET[$k]=$v;
        }
       }
        $_FILES=[];
        if(isset($request->files)){
            foreach($request->files as $k=>$v){
               $_FILES[$k]=$v;
         }
       }

       $_POST=[];
       if(isset($request->post)){
          foreach($request->post as $k=>$v){
             $_POST[$k]=$v;
        }
       }
       $_POST['http_server']=$this->ws;
       ob_start();
           //执行应用并响应
       try{
          //执行ThinkPHP方法
          think\Container::get('app',[APP_PATH])->run()->send();

       }catch(\Exception $e){
       echo '有错误！！！！！！！！！！';
       }

       //echo "-action-".request()->action().PHP_EOL;
       $res=ob_get_contents();
       ob_end_clean();
      // $response->cookie("singwa", "xsssss", time() + 1800);
       $response->end($res);
     }
    /**
     * swoole监听ws连接事件
     * @param $ws
     * @param $request
     */
    public function onOpen($ws, $request) {
		//把每一个消息队列的id和 自定义的key保存到redis缓存
		//调用文件路径 application\common\lib\redis\getInstance
       \app\common\lib\redis\Predis::getInstance()->sAdd(self::live_game_key,$request->fd); 
       var_dump($request->fd);
    }

    /**
     * 监听ws消息事件
     * @param $ws
     * @param $frame
     */
    public function onMessage($ws, $frame) {
      //  echo "ser-push-message:{$frame->data}\n";

        $ws->push($frame->fd, "server-push:".date("Y-m-d H:i:s"));
    }

    /**
     * @param $serv
     * @param $taskId
     * @param $workerId
     * @param $data
     */
    public function onTask($serv, $taskId, $workerId, $data) {
    	
	//分发 task 任务机制，让不同的任务 走步同的逻辑
        $obj=new app\common\lib\task\Task;

	$method=$data['method'];

	$flag=$obj->$method($data['data'],$serv);
        return $flag; //告诉worker	
    }

    /**
     * @param $serv
     * @param $taskId
     * @param $data
     */
    public function onFinish($serv, $taskId, $data) {
        echo "taskId:{$taskId}\n";
        echo "finish-data-sucess:{$data}\n";
    }

    /**
     * close
     * @param $ws
     * @param $fd
     */
    public function onClose($ws, $fd) {
       //当用户关闭窗口时把swoole消息队列窗口的id和 自定义的key从redis缓存删除
	   //调用文件路径 application\common\lib\redis\getInstance
     \app\common\lib\redis\Predis::getInstance()->sRem(config('redis.live_game_key'),$fd);
      echo "clientid:{$fd}\n";
    }
}

$obj = new Ws();
