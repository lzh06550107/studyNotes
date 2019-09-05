***************************
如何使用 FFmpeg 进行视频转码
***************************

标清：320*180

高清：640*360

超清：1280*720

1080p：1920*1080

1080P=1920*1080
720p=1280×720
480p=720X480
360p=480×360
240p=320X240

将 1080p 转成 720p ，宽度自适应

.. code-block:: shell

    ffmpeg -i input.mkv -c copy -c:v libx264 -vf scale=-2:720 output.mkv
    ffmpeg -i test.mp4  -s 480×360  out.mp4

``-vf`` 是 ``-filter:v`` 的简写， ``-filter`` 指定过滤器， ``:v`` 是流选择器，表示对视频流应用过滤器。 ``scale=`` 后面的参数是 ``w:h`` ， ``w`` 和 ``h`` 可以包含一些变量，比如原始的宽高分别为 ``iw`` 和 ``ih`` 。当其中一个是负数时，假设为 ``-n`` ， ``ffmpeg`` 自动使用另一个值按照原始的宽高比(aspect ratio)计算出该值，并且保证计算出来的值能被 ``n`` 整除。

因为 ``scale`` 过滤器是针对未编码的原始数据，所以视频流不能用 ``copy`` ，需要进行重新编码。 ``-c copy -c:v libx264`` 表示视频流使用 ``h.264`` 重新编码，其他流直接 ``copy`` ，顺序不能颠倒。

转码
====

最简单命令如下：

.. code-block:: shell

	ffmpeg -i out.ogv -vcodec h264 out.mp4
	ffmpeg -i out.ogv -vcodec mpeg4 out.mp4
	ffmpeg -i out.ogv -vcodec libxvid out.mp4
	ffmpeg -i out.mp4 -vcodec wmv1 out.wmv
	ffmpeg -i out.mp4 -vcodec wmv2 out.wmv

``-i`` 后面是输入文件名。 ``-vcodec`` 后面是编码格式， ``h264`` 最佳。

附加选项： ``-r`` 指定帧率， ``-s`` 指定分辨率， ``-b`` 指定比特率；于此同时可以对声道进行转码， ``-acodec`` 指定音频编码， ``-ab`` 指定音频比特率， ``-ac`` 指定声道数，例如

.. code-block:: shell

    ffmpeg -i out.ogv -s 640x480 -b 500k -vcodec h264 -r 29.97 -acodec libfaac -ab 48k -ac 2 out.mp4

ffmpeg转码m3u8
--------------
1. 如果视频不为mp4格式，需先将视频转码为mp4，可使用如下命令进行转换

.. code-block:: shell

    ffmpeg -i 本地视频地址 -y -c:v libx264 -strict -2 转换视频.mp4

2. 将mp4格式转换为ts格式

.. code-block:: shell

    ffmpeg -y -i 本地视频.mp4 -vcodec copy -acodec copy -vbsf h264_mp4toannexb 转换视频.ts

3. 将ts文件进行切片

.. code-block:: shell

    ffmpeg -i 本地视频.ts -c copy -map 0 -f segment -segment_list 视频索引.m3u8 -segment_time 5 前缀-%03d.ts

其中 ``segment`` 就是切片， ``-segment_time`` 表示隔几秒进行切一个文件，上面命令是隔 ``5s`` ，你也可以调整成更大的参数。

参考： https://blog.csdn.net/psh18513234633/article/details/79312607

使用ffmpeg对视频进行TS切片
-------------------------

1. ``ffmpeg`` 切片命令，以 ``H264`` 和 ``AAC`` 的形式对视频进行输出

.. code-block:: shell

    ffmpeg -i input.mp4 -c:v libx264 -c:a aac -strict -2 -f hls output.m3u8

2. ``ffmpeg`` 转化成 ``HLS`` 时附带的指令

- hls_time n: 设置每片的长度，默认值为 2 。单位为秒
- hls_list_size n:设置播放列表保存的最多条目，设置为 0 会保存有所片信息，默认值为 5
- hls_wrap n:设置多少片之后开始覆盖，如果设置为 0 则不会覆盖，默认值为 0 。这个选项能够避免在磁盘上存储过多的片，而且能够限制写入磁盘的最多的片的数量
- hls_start_number n:设置播放列表中 sequence number 的值为 number ，默认值为 0

3. 对 ffmpeg 切片指令的使用

.. code-block:: shell

    ffmpeg -i output.mp4 -c:v libx264 -c:a aac -strict -2 -f hls -hls_list_size 0 -hls_time 5 output1.m3u8


剪切
====
用 ``-ss`` 和 ``-t`` 选项， 从第 ``30`` 秒开始，向后截取 ``10`` 秒的视频，并保存：

.. code-block:: shell

	ffmpeg -i input.wmv -ss 00:00:30.0 -c copy -t 00:00:10.0 output.wmv
	ffmpeg -i input.wmv -ss 30 -c copy -t 10 output.wmv

达成相同效果，也可以用 ``-ss`` 和 ``-to`` 选项， 从第 ``30`` 秒截取到第 ``40`` 秒：

.. code-block:: shell

    ffmpeg -i input.wmv -ss 30 -c copy -to 40 output.wmv

值得注意的是， ``ffmpeg`` 为了加速，会使用关键帧技术，所以有时剪切出来的结果在起止时间上未必准确。 通常来说，把 ``-ss`` 选项放在 ``-i`` 之前，会使用关键帧技术； 把 ``-ss`` 选项放在 ``-i`` 之后，则不使用关键帧技术。 如果要使用关键帧技术又要保留时间戳，可以加上 ``-copyts`` 选项：

.. code-block:: shell

    ffmpeg -ss 00:01:00 -i video.mp4 -to 00:02:00 -c copy -copyts cut.mp4

合并
====

把两个视频文件合并成一个。

简单地使用 concat demuxer ，示例：

.. code-block:: shell

	$ cat mylist.txt
	file '/path/to/file1'
	file '/path/to/file2'
	file '/path/to/file3'

	$ ffmpeg -f concat -i mylist.txt -c copy output

更多时候，由于输入文件的多样性，需要转成中间格式再合成：

.. code-block:: shell

	ffmpeg -i input1.avi -qscale:v 1 intermediate1.mpg
	ffmpeg -i input2.avi -qscale:v 1 intermediate2.mpg
	cat intermediate1.mpg intermediate2.mpg > intermediate_all.mpg
	ffmpeg -i intermediate_all.mpg -qscale:v 2 output.avi

调整播放速度
===========
加速四倍：

.. code-block:: shell

    ffmpeg -i TheOrigin.mp4 -vf  "setpts=0.25*PTS" UpTheOrigin.mp4

四倍慢速：

.. code-block:: shell

    ffmpeg -i TheOrigin.mp4 -vf  "setpts=4*PTS" DownTheOrigin.mp4








``FFmpeg`` 是一个使用广泛的开源多媒体编解码器，其一大用途就是进行音频或视频的编码及格式转换。

``FFmpeg`` 并不像它表面看上去那么高端，那么难。只是中文的资料实在太少，才造成了这样的表象。所以，我决定动手开启这个计划，为中文用户提供一个更简单易学的 ``FFmpeg`` 教程。

安装
====

GNU/Linux
----------
得益于 ``GNU/Linux`` 上强大的包管理系统，在 GNU/Linux 中安装 FFmpeg 非常简单。但由于各种发行版采用的包管理器不同，安装时需要执行的命令也不同，以下列举三种最常见的包管理器安装 FFmpeg 时所需的命令，全部需要在 root 账户或者 sudo 后执行：


https://github.com/FiveYellowMice/how-to-convert-videos-with-ffmpeg-zh