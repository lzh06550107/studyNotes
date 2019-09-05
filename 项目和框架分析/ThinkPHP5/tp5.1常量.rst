**********
常量和目录
**********


常量
====




目录
====

- public目录：BASE_PATH；

.. code-block:: php

		$this->env->set([
            'think_path'   => $this->thinkPath,
            'root_path'    => $this->rootPath,
            'app_path'     => $this->appPath,
            'config_path'  => $this->configPath,
            'route_path'   => $this->routePath,
            'runtime_path' => $this->runtimePath,
            'extend_path'  => $this->rootPath . 'extend' . DIRECTORY_SEPARATOR,
            'vendor_path'  => $this->rootPath . 'vendor' . DIRECTORY_SEPARATOR,
        ]);

从上面代码可知，要获取项目各个目录，可以通过环境变量或者app实例方法，如：

- think库目录：app('env')->get('think_path') 或者 app()->getThinkPath()；
- 应用根目录：app('env')->get('root_path') 或者 app()->getRootPath()；
- 应用目录：app('env')->get('app_path') 或者 app()->getAppPath()；
- 配置目录：app('env')->get('config_path') 或者 app()->getConfigPath()；
- 路由目录：app('env')->get('route_path') 或者 app()->getRoutePath()；
- 运行时目录：app('env')->get('runtime_path') 或者 app()->getRuntimePath()；
- 扩展目录：app('env')->get('extend_path')
- composer安装包目录：app('env')->get('vendor_path') 
