*******
grunt入门
*******

安装grunt并创建项目
==================

1. 安装node；
2. npm install -g grunt-cli；
3. mkdir gruntDemo；cd gruntDemo；
4. npm init 生成package.json文件；


创建人任务
==========

创建任务：task.registerTask/registerTask
---------------------------------------

1. grunt init 创建 Gruntfile.js；
2. 输入下面代码来创建一个任务；

.. code-block:: js

    module.exports = function (grunt) {
    	grunt.registerTask('default', function() {
    		grunt.log.writeln('Hello Grunt!');
    	})
    }

3. 执行任务 grunt default

在任务中使用参数
---------------

.. code-block:: js

    module.exports = function (grunt) {
    	grunt.registerTask('greet', function(name) {
    		grunt.log.writeln('Hello ' + name);
    	})
    }

输入 grunt greet:lzh 来执行任务。

错误提示
--------

.. code-block:: js

    module.exports = function (grunt) {

    	if (name.length < 2) {
    		grunt.warn('名字太短了。');
    	}
    	grunt.registerTask('greet', function(name) {
    		grunt.log.writeln('Hello ' + name);
    	})
    }

输入 grunt greet:l 会触发警告错误，并停止任务执行。

warn 警告错误可以通过 --force 强制执行；但如果是 fatal 致命错误则不行。

链接多个任务
-----------

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.registerTask('greet-1', function() {
    		grunt.log.writeln('Hello');
    	});
    	grunt.registerTask('greet-2', function() {
    		grunt.log.writeln('Hola');
    	});
    	grunt.registerTask('greet-3', function() {
    		grunt.log.writeln('您好！');
    	});

    	grunt.registerTask('greetAll', ['greet-1','greet-2','greet-3']);
    }

输入 grunt greetAll 会执行所有任务。

初始化配置：config.init/initConfig
---------------------------------
给特定任务配置。

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.initConfig({ // 配置
    		greet: {
    			english: 'Hello'
    		}
    	});

    	grunt.registerTask('greet', function() {
    		// 获取配置
    		grunt.log.writeln(grunt.config.get('greet.english'));
    	})
    }


多任务：multiTask
-----------------

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.initConfig({ // 配置
    		greet: {
    			english: 'Hello', // 任务1
    			spanish: 'Hola', // 任务2
    			chinses: '您好' // 任务3
    		}
    	});

    	grunt.registerMultiTask('greet', function() {
    		// 获取配置
    		grunt.log.writeln(this.target + ':' + this.data);
    	})
    }

输入 grunt greet 会执行多个任务。

输入 grunt greet:chinses 会执行多个任务。

文件与目录
==========

创建与删除目录
-------------

.. code-block:: js

	grunt.registerTask('createFoloders', function () {
		grunt.file.mkdir('dist/stylesheets'); // 会递归创建目录
	})

	grunt.registerTask('clean', function () {
		grunt.file.delete('dist'); // 会递归删除目录
	})

读取与写入文件
-------------

.. code-block:: js

	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'); // 读取配置文件内容
	});

	grunt.registerTask('copyright', function() {
		// 读取配置放入模板标签中，然后编译
		var content = grunt.template.process('<%= pkg.name %> 这个项目是由 <%= pkg.author %> 创建的，现在的版本是 <%= pkg.version %>。');

		grunt.file.write('copyright.txt', content); // 写入文件
	});

复制文件：grunt-contrib-copy
----------------------------

1. 安装插件 npm install grunt-contrib-copy --save-dev
2. 配置加载插件

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.loadNpmTasks('grunt-contrib-copy'); // 加载插件

    }

配置要复制文件
-------------

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.loadNpmTasks('grunt-contrib-copy'); // 加载插件

    	grunt.initConfig({
    		copy: { // 插件任务名称为copy
    			html: { // 任务的目标target
    				src: 'index.html',
    				dest: 'dist/'
    			}
    		}
    	});

    }

输入 grunt copy:html 会复制指定文件。

复制多个文件
------------

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.loadNpmTasks('grunt-contrib-copy'); // 加载插件

    	grunt.initConfig({
    		copy: { // 插件任务名称为copy
    			html: { // 任务的目标target
    				src: 'index.html',
    				dest: 'dist/'
    			},
    			style: {
    				src: 'stylesheets/*.css', // 复制多个文件
    				dest: 'dist/css/'
    			},
    			js: {
    				src: 'javascripts/**/*.js', // 复制包含子目录中所有文件
    				dset: 'dist/js/'
    			}
    		}
    	});

    }

监视文件变化：grunt-contrib-watch
---------------------------------
1. 安装插件 npm install grunt-contrib-watch --save-dev
2. 配置加载插件

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.loadNpmTasks('grunt-contrib-watch'); // 加载插件

    }

文件发生变化执行指定的任务
------------------------

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.loadNpmTasks('grunt-contrib-copy'); // 加载插件
    	grunt.loadNpmTasks('grunt-contrib-watch'); // 加载插件

    	grunt.initConfig({
    		watch: {
    			html: {
    				files: ['index.html'], // 要监视的文件
    				tasks: ['copy:html'], // 文件发生变化，则要执行的任务列表
    			}
    		},
    		copy: { // 插件任务名称为copy
    			html: { // 任务的目标target
    				src: 'index.html',
    				dest: 'dist/'
    			},
    			style: {
    				src: 'stylesheets/*.css', // 复制多个文件
    				dest: 'dist/css/'
    			},
    			js: {
    				src: 'javascripts/**/*.js', // 复制包含子目录中所有文件
    				dset: 'dist/js/'
    			}
    		}
    	});

    }

输入 grunt:watch 来开始监控文件修改。

插件
====

创建服务器：grunt-contrib-connect
---------------------------------
用来充当一个静态文件服务器，本身集成了 livereload 功能。

1. 安装插件 npm install grunt-contrib-connect --save-dev
2. 配置加载插件

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.loadNpmTasks('grunt-contrib-connect'); // 加载插件

    }

配置服务器
----------

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.loadNpmTasks('grunt-contrib-copy'); // 加载插件
    	grunt.loadNpmTasks('grunt-contrib-watch'); // 加载插件
    	grunt.loadNpmTasks('grunt-contrib-connect'); // 加载插件

    	grunt.initConfig({
    		connect: {
    			server: {
    				options: {
    					port: 8000,
    					base: 'dist', // 服务器根目录
    				}
    			}
    		},
    		watch: {
    			html: {
    				files: ['index.html'], // 要监视的文件
    				tasks: ['copy:html'], // 文件发生变化，则要执行的任务列表
    			}
    		},
    		copy: { // 插件任务名称为copy
    			html: { // 任务的目标target
    				src: 'index.html',
    				dest: 'dist/'
    			},
    			style: {
    				src: 'stylesheets/*.css', // 复制多个文件
    				dest: 'dist/css/'
    			},
    			js: {
    				src: 'javascripts/**/*.js', // 复制包含子目录中所有文件
    				dset: 'dist/js/'
    			}
    		}
    	});

    }

实时刷新：livereload
--------------------
把 watch 和 connect 任务结合实现实时刷新。

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.loadNpmTasks('grunt-contrib-copy'); // 加载插件
    	grunt.loadNpmTasks('grunt-contrib-watch'); // 加载插件
    	grunt.loadNpmTasks('grunt-contrib-connect'); // 加载插件

    	grunt.initConfig({
    		connect: {
    			server: {
    				options: {
    					port: 8000,
    					base: 'dist', // 服务器根目录
    					livereload: true //开启实时刷新功能
    				}
    			}
    		},
    		watch: {
    			html: {
    				files: ['index.html'], // 要监视的文件
    				tasks: ['copy:html'], // 文件发生变化，则要执行的任务列表
    				optons: {
    					livereload: true // 开启实时刷新功能
    				}
    			}
    		},
    		copy: { // 插件任务名称为copy
    			html: { // 任务的目标target
    				src: 'index.html',
    				dest: 'dist/'
    			},
    			style: {
    				src: 'stylesheets/*.css', // 复制多个文件
    				dest: 'dist/css/'
    			},
    			js: {
    				src: 'javascripts/**/*.js', // 复制包含子目录中所有文件
    				dset: 'dist/js/'
    			}
    		}
    	});

    	grunt.registerTask('serve', ['connect','watch']);

    }

编译sass：grunt-contrib-sass
----------------------------
1. 安装插件 npm install grunt-contrib-sass --save-dev
2. 电脑需要安装 ruby
3. 电脑需要安装 gem install sass
4. 配置加载插件

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.loadNpmTasks('grunt-contrib-sass'); // 加载插件

    	grunt.initConfig({
    		sass: {
    			dist: {
    				options: {
    					style: 'expanded' // 编译后的格式
    				},
    				files: {
    					'dist/stylesheets/style.css':'stylesheets/style.scss'
    				}
    			}
    		}
    	});
    }

输入 grunt sass:dist 执行编译。

编译less：grunt-contrib-less
----------------------------
1. 安装插件 npm install grunt-contrib-less --save-dev
2. 配置加载插件

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.loadNpmTasks('grunt-contrib-less'); // 加载插件

    	grunt.initConfig({
    		less: {
    			dist: {
    				options: {
    					style: 'expanded' // 编译后的格式
    				},
    				files: {
    					'dist/stylesheets/style.css':'stylesheets/style.less'
    				}
    			}
    		}
    	});
    }

输入 grunt less:dist 执行编译。

合并文件：grunt-contrib-concat
------------------------------
1. 安装插件 npm install grunt-contrib-concat --save-dev
2. 配置加载插件

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.loadNpmTasks('grunt-contrib-concat'); // 加载插件

    	grunt.initConfig({
    		concat: {
    			js: {
    				src: ['javascripts/app.js','javascripts/modules/module.js'],
    				dest: 'dist/javascripts/app.js'
    			},
    		}
    	});
    }

输入 grunt concat:js 执行合并。

选项：options
-------------

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.loadNpmTasks('grunt-contrib-concat'); // 加载插件

    	grunt.initConfig({
    		concat: {
    			optons: { // 全局配置

    			},
    			js: {
    				optons: { // 局部配置
    					banner: '/* Grunt course by lzh */\n',
    					footer: '/* The end! */\n'

    				},
    				src: ['javascripts/app.js','javascripts/modules/module.js'],
    				dest: 'dist/javascripts/app.js'
    			},
    		}
    	});
    }


最小化js：grunt-contrib-uglify
------------------------------
1. 安装插件 npm install grunt-contrib-uglify --save-dev
2. 配置加载插件

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.loadNpmTasks('grunt-contrib-uglify'); // 加载插件

    	grunt.initConfig({
    		uglify: {
    			dist: {
    				src: '<%= concat.js.dest %>',
    				dest: 'dist/javascripts/app.min.js'
    			}
    		},
    		concat: {
    			js: {
    				src: ['javascripts/app.js','javascripts/modules/module.js'],
    				dest: 'dist/javascripts/app.js'
    			},
    		}
    	});

    	grunt.registerTask('build', ['concat', 'uglify']);
    }

输入 grunt build 执行合并和最小化。

最小化css：grunt-contrib-cssmin
-------------------------------
1. 安装插件 npm install grunt-contrib-cssmin --save-dev
2. 配置加载插件

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.loadNpmTasks('grunt-contrib-cssmin'); // 加载插件

    	grunt.initConfig({
    		cssmin: {
    			dist: {
    				src: 'dist/stylesheets/style.css',
    				dest: 'dist/stylesheets/style.min.css'
    			}
    		}
    	});
    }

输入 grunt cssmin:dist 执行最小化。

最小化图像：grunt-contrib-imagemin
---------------------------------
我们平时使用Photoshop 切出的图片，都含有一些不需要的信息或者多余的颜色值，这些信息和颜色值，对网页显示并没有用处，反而增加图片大小，Google Pagespeed 建议我们对于JPEG文件，使用jpegtran或jpegoptim（仅适用于Linux；使用--strip-all选项运行）。对于PNG文件，使用OptiPNG或PNGOUT。减小图片大小，就可以减少用户下载的文件大小，加快页面访问速度。

对于不同格式的图片，我们需要用pegtran/jpegoptim/OptiPNG/PNGOUT 的工具，这样对于前端开发费时费力，grunt-contrib-imagemin封装了这些压缩功能；大大方便了我们优化的工作。

1. 安装插件 npm install grunt-contrib-imagemin --save-dev
2. 配置加载插件

.. code-block:: js

    module.exports = function (grunt) {

    	grunt.loadNpmTasks('grunt-contrib-imagemin'); // 加载插件

    	grunt.initConfig({
    		immagemin: {
    			dist: {
    				expand: true,
    				src: 'images/**/*.{png,jpg}',
    				dest: 'dist/'
    			}
    		}
    	});
    }

输入 grunt immagemin:dist 执行最小化。

参考文件：

- https://www.cnblogs.com/koleyang/p/5576989.html
- 官网 https://www.gruntjs.net/
- https://www.cnblogs.com/yexiaochai/p/3594561.html