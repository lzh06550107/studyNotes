*************
nginx搭建流媒体服务器
*************

方法一：ffmpeg+nginx
=====================
新的 ``ffmpeg`` 已经支持 ``HLS`` 。

点播
----
生成 ``hls`` 分片:

.. code-block:: shell

    ffmpeg -i <媒体文件> -c:v libx264 -c:a -f hls /usr/local/nginx/html/test.m3u8

直播
----
接收 ``udp`` 流，切片成 ``ts`` 分片（m3u8文件自动更新）放到某指定目录下。

.. code-block:: shell

	# @:1234表示本机1234端口地址
    ffmpeg -i udp://@:1234 -c:v libx264 -c:a -f hls /usr/local/nginx/html/test.m3u8

	ffmpeg -i udp://192.168.1.110:1234 -vcodec copy -acodec aac -ar 44100 -strict -2 -ac 1 -f flv -s 1280x720 -q 10 rtmp://192.168.1.110:1935/hls/test2

在手机浏览器中输入地址 ``http://192.168.1.110/hls/test2.m3u8``

建立web服务器：默认配置就可以。

.. code-block:: shell

	server {
	    listen       80;
	    server_name  localhost;
	    #charset koi8-r;
	    #access_log  logs/host.access.log  main;
	    location / {
	        root   html;
	        index  index.html index.htm;
	    }
	}

1. 启动 ``nginx``
2. 客户端访问 ``http://IP/test.m3u8``
3. 在 ``windows`` 上可以用 ``vlc`` 播放

方案二：nginx-rtmp-module
==========================

点播视频服务器的配置
-------------------
打开配置文件 ``nginx.conf`` ，添加 ``RTMP`` 的配置。

.. code-block:: shell

  worker_processes  1;

  events {
      worker_connections  1024;
  }

  rtmp {                #RTMP服务
      server {
          listen 1935;  #//服务端口
      	chunk_size 4096;   #//数据传输块的大小

      	application vod {
          	play /opt/vide/vod; #//视频文件存放位置。
      	}
      }
  }

  http {
      include       mime.types;
      default_type  application/octet-stream;
      sendfile        on;
      keepalive_timeout  65;
      server {
          listen       80;
          server_name  localhost;
          location / {
              root   html;
              index  index.html index.htm;
          }
          error_page   500 502 503 504  /50x.html;
          location = /50x.html {
              root   html;
          }
      }
  }

在播放器中填写点播的节目地址 ``rtmp://localhost/vod/qq.mp4`` 。当然点播不使用 ``RTMP`` 插件 ``nginx`` 自身也是可以实现点播服务的。那就是配置 ``location`` 部分，由于下面我们要配置直播和回看功能所以选用了 ``RTMP`` 服务。


直播视频服务器的配置
-------------------
接着我们就在点播服务器配置文件的基础之上添加直播服务器的配置。一共2个位置，第一处就是给 ``RTMP`` 服务添加一个 ``application`` 这个名字可以任意起，也可以起多个名字，由于是直播我就叫做它 ``live`` 吧，如果打算弄多个频道的直播就可以 ``live_cctv1`` 、 ``live_cctv2`` 名字任意。第二处就是添加两个 ``location`` 字段，字段的内容请直接看文件吧。

``rtmp`` 传输协议直播流

.. code-block:: shell

	worker_processes  1;

	events {
	    worker_connections  1024;
	}

	rtmp {
	    server {
	        listen 1935;
	    	chunk_size 4096;

		    application vod{
		        play /opt/video/vod;
		    }

		    # RTMP直播流配置
		    application rtmplive{ #第一处添加的直播字段
		        live on;
		        max_connection 1024 #为 rtmp 引擎设置最大连接数
		    }
	    }

	}

	http {
	    include       mime.types;
	    default_type  application/octet-stream;
	    sendfile        on;
	    keepalive_timeout  65;
	    server {
	        listen       80;
	        server_name  localhost;

		    location /stat {     #第二处添加的location字段。
		        rtmp_stat all;
		        rtmp_stat_stylesheet stat.xsl;
		    }

		    location /stat.xsl { #第二处添加的location字段。
		        root /usr/local/nginx/nginx-rtmp-module/;
		    }

	        location / {
	            root   html;
	            index  index.html index.htm;
	        }

	        error_page   500 502 503 504  /50x.html;
	        location = /50x.html {
	            root   html;
	        }
	    }
	}

打开我的服务器地址 ``http://localhost/stat`` 查看推送流状态。播放的地址就是 ``rtmp://localhost/live/test`` 。

可以进一步配置，让服务器支持 ``hls`` 传输协议直播流。

.. code-block:: shell

  rtmp {
      server {
          listen 1935;
          chunk_size 4000;
          #HLS
          # For HLS to work please create a directory in tmpfs (/tmp/app here)
          # for the fragments. The directory contents is served via HTTP (see
          # http{} section in config)
          #
          # Incoming stream must be in H264/AAC. For iPhones use baseline H264
          # profile (see ffmpeg example).
          # This example creates RTMP stream from movie ready for HLS:
          #
          # ffmpeg -loglevel verbose -re -i movie.avi  -vcodec libx264
          #    -vprofile baseline -acodec libmp3lame -ar 44100 -ac 1
          #    -f flv rtmp://localhost:1935/hls/movie
          #
          # If you need to transcode live stream use 'exec' feature.
          # HLS 直播流配置
          application hls {
              live on;
              hls on;
              hls_path /tmp/app;
              hls_fragment 5s;
          }
      }
  }
  http {
      server {
          listen      80;
          location /hls {
              # Serve HLS fragments
              types {
                  application/vnd.apple.mpegurl m3u8;
                  video/mp2t ts;
              }
          	  alias /tmp/app;
          	  # add_header Cache-Control no-cache
          	  expires -1;
          }
      }
  }

ffmpeg推流至nginx
-----------------
``FFmpeg`` 再次登场，通过其对本地视频文件“推流”到已搭建好的 ``Nginx`` 流媒体服务器上。由于在上一步骤中，已经对 ``nginx`` 增加了 ``RTMP`` 和 ``HLS`` 协议的支持，故而借助 ``ffmpeg`` 推流成功后，在 ``nginx`` 服务器上可得到两种视频流： ``RTMP`` 流、 ``HLS`` 流。需要注意的是，不管是哪种流，在推流过程中是 ``RTMP`` 流形式体现的，如下图：

``RTMP`` 流，推流至 ``rtmplive``
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: shell

	ffmpeg -re -i /Users/richyleo/Downloads/warcraft.mp4 -vcodec libx264 -vprofile baseline -acodec aac -ar 44100 -strict -2 -ac 1 -f flv -s 1280x720 -q 10 rtmp://localhost:1935/rtmplive/test

``HLS`` 流，推流至 ``hls``
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: shell

	ffmpeg -re -i /Users/richyleo/Downloads/warcraft.mp4 -vcodec libx264 -vprofile baseline -acodec aac -ar 44100 -strict -2 -ac 1 -f flv -s 1280x720 -q 10 rtmp://localhost:1935/hls/test
	# 后台运行，需要主动注销登录用户 使用 -ss 0:1:30 来跳过指定时间
	nohup ffmpeg -re -i /application/transmission/Downloads/FC2-PPV-785172.mp4 -c copy -f flv  rtmp://localhost:1935/hls/test

其中， ``HLS`` 流表现较明显，在 ``nginx`` 的临时目录下，直观的可看到 ``m3u8`` 索引文件和 ``N`` 多个 ``.ts`` 文件。 ``m3u8`` 列表会实时更新，且会动态更改当前播放索引切片( ``.ts`` )。这种实时更新的机制，不会使得 ``.ts`` 文件长时间存在于 ``Nginx`` 服务器上，且当推流结束之后，该目录下的内容会被全部清除，这样无形中减缓了 ``nginx`` 服务器的压力。另外，也阐释了 ``HLS`` 这种流媒体播放相较 ``RTMP`` 延时较高的原因。

播放 ``rtmp`` 流或 ``hls`` 流
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
最简单的测试，可通过 ``VLC`` 播放器，建立网络任务实现播放。所谓的播放，就是从 ``Nginx`` 服务器取到视频流并播放，也称之为“拉流”。需注意的是， ``HLS`` 是基于 ``HTTP`` 的流媒体传输协议，端口为 ``8080`` ；而 ``RTMP`` 本身即为实时消息传输协议，端口为 ``1935`` 。由此决定了客户端访问直播流的方式：

- ``RTMP`` 流： ``rtmp://localhost:1935/rtmplive/test``
- ``HLS`` 流： ``http://localhost:8080/hls/test.m3u8``

实时回看视频服务器的配置
=======================

ffmpeg 关于hls方面的指令说明
----------------------------

- ``hls`` ：值on｜off  切换hls 
- ``hls_path`` :  设置播放列表(m3u8)和媒体块的位置  
- ``hls_fragment`` : 后面接时间，用来设置每一个块的大小。默认是5秒。只能为整数
- ``hls_playlist_length`` :  设置播放列表的长度，单位是秒
- ``hls_sync`` :  音视频的同步时间
- ``hls_continuous`` :  on|off 设置连续模式，是从停止播放的点开始还是直接跳过
- ``hls_nested`` :  on｜off 默认是off。打开后的作用是每条流自己有一个文件夹
- ``hls_base_url`` : 设置基准URL，对于m3u8中使用相对URL有效
- ``hls_cleanup`` :  on｜off 默认是开着的，是否删除列表中已经没有的媒体块
- ``hls_fragment_naming`` : sequential（使用增长式的整数命名） | timestamp（使用媒体块的时间戳） | system（使用系统时间戳）命名方式
- ``hls_fragment_naming_granularity`` : 如果使用时间戳命名时时间戳的精度
- ``hls_fragment_slicing`` :  plain（媒体块达到需要的duration就换）｜aligned（当到达的数据库块时几个duration） 

https://www.cnblogs.com/tinywan/p/5981197.html

多码率直播
==========
https://www.cnblogs.com/tocy/p/using-ffmpeg-build-hls-live-system.html


nohup ffmpeg -re -i  -c copy -c:a aac -strict -2 -f flv  rtmp://localhost:1935/hls/test
