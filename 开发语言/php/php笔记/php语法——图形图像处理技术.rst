图形图像处理技术
==============

在PHP中加载GD库
--------------
GD库是一个开发的、动态创建图像的、源代码公开的函数库。目前，GD库支持GIF、PNG、JPEG、WBMP和XBM等多种图像格式，用于对图像的处理。

GD库在PHP5中是默认安装的，但要激活GD库，必须设置php.ini文件，即将文件中的 ``";extension=php_gd2.dll"`` 选项前的分号删除，保存修改后的文件并重新启动Apache服务器即可生效。

在成功加载GD2函数库后，可以通过phpinfo()函数来获取GD2函数库的安装信息，验证GD库是否安装成功。

Jpgraph的安装与装配
------------------
Jpgraph这个强大的绘图组件能根据用户的需要绘制任意图形。用户只需要提供数据，就能自动调用绘图函数，按照输入的数据自动绘制图形。Jpgraph提供了多种方法创建各种统计图，包括折线图、柱形图和饼形图等。因为Jparaph是一个完全使用PHP语言编写的类库，所以它可以应用在任何PHP环境中。

jpgraph的安装
^^^^^^^^^^^^^
pgraph可以从其官方网站下载。注意：jpgraph支持php4.3.1以上和PHP5两种版本的图形库，选择合适的jpgraph版本下载。可以下载这样的版本使用：jpgraph-2.3

其安装步骤非常简单：

- 将压缩包下的全部文件解压到一个文件夹中。如f:\appserv\www\jpgraph;
- 打开PHP的安装目录，编辑php.ini文件并修改其中的include_path参数，在其后增加前面的文件夹名， ``include_path=".;f:\appserv\www\jpgraph"``
- 重新启动apache服务器即可生效。

Jpgraph的配置
^^^^^^^^^^^^^
Jpgraph提供了一个专门用于配置Jpgraph类库的文件jpg-config.inc.php。在使用Jpgraph前，可以通过修改文本文件来完成Jpgraph的配置。

jpg-config.inc.php文件的配置需要修改以下两项：

- 支持中文的配置

  Jpgraph支持的中文标准字体可以通过修改chinese_ttf_font的设置来完成。

  ``Define(‘chinese_ttf_font’,’bkai00mp.ttf’);``
- 默认图片格式的配置

  根据当前PHP环境中支持的图片格式来设置默认的生成图片的格式。Jpgraph默认图片格式的配置可以通过修改DEFAULT_GFORMAT的设置来完成。默认值auto表示jpgraph将依次按照png,gif和jpeg的顺序来检索系统支持的图片格式。

  ``DEFINE(“DEFAULT_GFORMAT”,”auto”);``

 注意：如果用户使用的为jpgraph2.3版本，那么不需要重新进行配置。

PHP中GD库的使用
--------------
在PHP中，通过GD库处理图像的操作，都是先在内存中处理，操作完成以后再以文件流的方式，输出到浏览器或保存在服务器的磁盘中。创建一个图像应该完成如下所示的四个基本步骤。

- 创建画布：所有的绘图设计都需要在一个背景图片上完成，而画布实际上就是在内存中开辟的一块临时区域，用于存储图像的信息。以后的图像操作都将基于这个背景画布，该画布的管理就类似于我们在画画时使用的画布。
- 绘制图像：画布创建完成以后，就可以通过这个画布资源，使用各种画像函数设置图像的颜色、填充画布、画点、线段、各种几何图形，以及向图像中添加文本等。
- 输出图像：完成整个图像的绘制以后，需要将图像以某种格式保存到服务器指定的文件中，或将图像直接输出到浏览器上显示给用户。但在图像输出之前，一定要使用 ``header()`` 函数发送 ``Content-type`` 通知浏览器，这次发送的是图片不是文本。
- 释放资源：图像被输出以后，画布中的内容也不再有用。出于节约系统资源的考虑，需要及时清除画布占用的所有内存资源。

我们先来了解一下一个非常简单的创建图像脚本。在下面的脚本文件image.php中，按前面介绍的绘制图像的四个步骤，使用GD库动态输出一个扇形统计图。代码如下所示：

.. code-block:: php

    <?php
	//创建画布，返回一个资源类型的变量$image，并在内存中开辟一个临时区域
	$image = imagecreatetruecolor(100, 100);   //创建画布大小为100×100

	//设置图像中所需的颜色，相当于在画画时准备的染料盒
	$white = imagecolorallocate($image, 0xFF, 0xFF, 0xFF);          //为图像分配颜色为白色
	$gray = imagecolorallocate($image, 0xC0, 0xC0, 0xC0);           //为图像分配颜色为灰色
	$darkgray = imagecolorallocate($image, 0x90, 0x90, 0x90);       //为图像分配颜色为暗灰色
	$navy = imagecolorallocate($image, 0x00, 0x00, 0x80);           //为图像分配颜色为深蓝色
	$darknavy = imagecolorallocate($image, 0x00, 0x00, 0x50);       //为图像分配颜色为暗深蓝色
	$red = imagecolorallocate($image, 0xFF, 0x00, 0x00);           //为图像分配颜色为红色
	$darkred = imagecolorallocate($image, 0x90, 0x00, 0x00);       //为图像分配颜色为暗红色

	imagefill($image, 0, 0, $white);     //为画布背景填充背景颜色
	//动态制作3D效果
	for ($i = 60; $i > 50; $i--) {                //循环10次画出立体效果
	    imagefilledarc($image, 50, $i, 100, 50, -160, 40, $darknavy, IMG_ARC_PIE);
	    imagefilledarc($image, 50, $i, 100, 50, 40, 75, $darkgray, IMG_ARC_PIE);
	    imagefilledarc($image, 50, $i, 100, 50, 75, 200, $darkred, IMG_ARC_PIE);
	}

	imagefilledarc($image, 50, 50, 100, 50, -160, 40, $navy, IMG_ARC_PIE);      //画一椭圆弧且填充
	imagefilledarc($image, 50, 50, 100, 50, 40, 75, $gray, IMG_ARC_PIE);      //画一椭圆弧且填充
	imagefilledarc($image, 50, 50, 100, 50, 75, 200, $red, IMG_ARC_PIE);      //画一椭圆弧且填充

	imagestring($image, 1, 15, 55,'34.7%', $white);   // 水平地画一行字符串
	imagestring($image, 1, 45, 35, '55.5%', $white);  // 水平地画一行字符串

	//向浏览器中输出一个GIF格式的图片
	header('Content-type:image/png');  // 使用头函数告诉浏览器以图像方式处理以下输出
	imagepng($image);  // 向浏览器输出
	imagedestroy($image);  // 销毁图像释放资源
    ?>

画布管理
--------
使用PHP的GD库处理图像时，必须对画布进行管理。创建画布就是在内存中开辟一块存储区域，以后在PHP中对图像的所有操作都是基于这个画布处理的。常用的画布管理函数如下：

创建画布
^^^^^^^
在PHP中，可以使用 ``imagecrete()`` 和 ``imageCreateTrueColor()`` 两个函数创建指定的画布。这两个函数的作用是一致的，都是建立一个指定大小的画布，他们的原型如下所示：

.. code-block:: php

    <?php
	resource imagecreate(int $x_size,int $y_size) //新建一个基于调色板的图像
	resource imagecreatetruecolor(int $x_size,int $y_size) //新建一个真彩色图像
    ?>

虽然这两个函数都可以创建一个新的画布，但各自能够容纳的颜色的总数是不同的。imageCreate()函数可以创建一个基于普通调色板的图像，通常支持256色。而imageCreateTrueColor()函数可以创建一个真彩色图像，但该函数不能用于GIF文件格式。当画布创建后，返回一个图像标识符，代表了一幅宽度为$x_size和高度为$y_size的空白图像引用句柄。在后续的绘图过程中，都需要使用这个资源类型的句柄。

除了可以使用上面介绍的两个函数，创建一个新的画布资源作为引用句柄使用。也可以通过下面介绍的几个函数，打开服务器或网络文件中已经存在的GIF、JPEG、PNG或WBMP格式图像，返回一个图像标识符，代表了从给定的文件名取得的图像。它们的原型如下所示：

.. code-block:: php

    <?php
	resource imagecreatefromjpeg ( string $filename ) //从JPEG文件或URL新建一图像
	resource imagecreatefrompng ( string $filename ) //从PNG文件或URL新建一图像
	resource imagecreatefromgif ( string $filename ) //从GIF文件或URL新建一图像
	resource imagecreatefromwbmp ( string $filename ) //从WBMP文件或URL新建一图像
    ?>

它们在失败时都会返回一个空字符串，并且输出一条错误信息。不管是新创建的画布还是使用已存在的图像返回图像标识符，都可以获取图像的大小，可以通过调用imagex()和imagey()两个函数实现。代码如下所示：

.. code-block:: php

    <?php
	$img = imagecreatetruecolor(300, 200); // 创建一个300*200的画布
	echo imagesx($img); // 输出画布宽度300
	echo imagesy($img); // 输出画布高度200
    ?>

另外，画布的引用句柄如果不再使用时，一定要将这个资源销毁，释放内存与该图像的存储单元。画布的销毁过程非常简单，调用imagedestroy()函数就可以实现。其语法格式如下所示：

``bool imagedestroy(resource $image) //销毁一图像``

如果该方法调用成功，就会释放与参数image关联的内存。其中参数image是由图像创建函数返回的图像标识符。

设置颜色
^^^^
在使用PHP动态输出美丽图像的同时，也离不开颜色的设置，就像画画时需要使用的调色板一样。设置图像中的颜色，需要调用imageColorAllocate()函数完成。如果在图像中需要设置多种颜色，只要多次调用该函数即可。该函数的原型如下所示：

``int imagecolorallocate(resource $image,int $red,int $green,int $blue)//为一幅图分配颜色``

该函数会返回一个标识符，代表了由给定的RGB成分组成的颜色。参数$red、$green和$blue分别是所需要的颜色的红、绿蓝成分。这些参数是0到255的整数或者十六进制的0×00到0xFF。第1个参数$image是画布图像的句柄，该函数必须调用$image所代表的图像中的颜色。 **但要注意，如果是使用imagecreate()函数建立的画布，则第一次对imagecolorallocate()函数的调用，会给基于调色板的图像填充背景色。** 该函数的使用代码如下所示：

.. code-block:: php

    <?php
	$im = imagecreate(100,100);//为设置颜色函数提供一个画布资源
	//背景设为红色
	$background = imagecolorallocate($m,255,0,0);//第一次调用即为画布设置背景颜色
	//设定一些颜色
	$white = imagecolorallocate($im,255,255,255);//返回由十进制整数设置为白色的标识符
	$black = imagecolorallocate($im,0,0,0);//返回由十进制参数设置为黑色的标识符
	//十六进制方式
	$white = imagecolorallocate($im,0xFF,0xFF,0xFF);//返回由十六进制整数设置为白色的标识符
	$black = imagecolorallocate($im,0x00,0x00,0x00);//返回由十六进制整数设置为黑色的标识符
    ?>

生成图像
^^^^
如果使用GD库中提供的函数动态绘制完成图像以后，就需要输出到浏览器或者将图像保存起来。在PHP中，可以将动态绘制完成的画布，直接生成GIF、JPEG、PNG和WBMP四种图像格式。可以通过调用下面四个函数生成这些格式的图像：

.. code-block:: php

    <?php
	bool imagegif(resource $image[,string $filename])  //以GIF格式将图像输出
	bool imagejpeg(resource $image[,string $filename[,int $quality]])  //以JPEG格式将图像输出
	bool imagepng(resource $image[,string $filename])  //以PNG格式将图像输出
	bool imagewbmp(resource $image[,string $filename[,int $foreground]])  //以WBMP格式将图像输出
    ?>

以上四个函数的使用类似，前两个参数的使用是相同的。第一个参数$image为必选项，是前面介绍的图像引用句柄。如果不为这些函数提供其他参数，访问时则直接将原图像流出，并在浏览器使用中显示动态输出的图像。但一定要在输出之前，使用header()函数发送标头信息，用来通知浏览器使用正确的MIME类型对接收的内容进行解析，让它知道我们发送的是图片而不似乎文本的HTML。以下代码段通过自动检测GD库支持的图像类型，来写出移植性更好的PHP程序。如下所示：

.. code-block:: php

    <?php
	if(function_exists("imagegif")){   //判断生成GIF格式图像的函数是否存在
	    header("Content-type:image/gif");   //发送标头信息设置MIME类型image/gif
	    imagegif($im);   //以GIF格式将图像输出到浏览器
	}elseif(function_exists("imageipeg")){
	    header("Content-type:image/jpeg");
	    imagejpeg($im,"",0.5);
	}elseif(function_exists("imagepng")){
	    header("Content-type:image/png");
	    imagepng($im);
	}elseif(function_exists("imagewbmp")){
	    header("Content-type:image/wbmp");
	    imagewbmp($im);
	}else{
	    die("在PHP服务器中，不支持图像");
	}
    ?>

如果希望将PHP动态绘制的图像保存在本地服务器上，则必须在第二个可选参数中指定一个文件名字符串。这样不仅不会将图像直接输出到浏览器，也不需要使用header()函数发送标头信息。

如果使用imageJPEG()函数生成JPEG格式的图像，还可以通过第三个可选参数$quality指定JPEG格式图像的品质，该参数可以提供的值是从0（最差品质，但文件最小）到100（最高品质，文件也最大）的整数，默认值为75.也可以为函数imageWBMP()提供第三个可选参数$forground，指定图像的前景颜色，默认颜色值为黑色。

绘制图像
^^^^^^^
在PHP中绘制图像的函数非常丰富，包括点、线、各种几何图形等可以想象出来的平面图形，都可以通过PHP中提供的各种画图函数完成。我们在这里介绍一些常用的图像绘制，如果使用我们没有介绍过的函数，可以参考手册实现。另外，这些图形绘制函数都需要使用画布资源，并在画布中的位置通过坐标（原点是在画布左上角的起始位置，以像素为单位，沿着X轴正方向向右延伸，Y轴正方向向下延伸）决定，并且还可以通过函数的最后一个参数，设置每个图形的颜色。画布中的坐标系统如图所示。

.. image:: ./images/cavan.jpg

函数图形区域填充imageFill()
""""""""""""""""""""""""""
通过PHP仅仅绘制出只有边线的几何图形是不够的，还可以使用对应的填充函数，完成图形区域的填充。除了每个图形都有对应的填充函数之外，还可以使用imageFill()函数实现区域填充。该函数的语法格式如下：

``bool imagefill(resource $image,int $x ,int $y,int $color) // 区域填充``

该函数在参数$image代表的图像上，相对于图像左上角(0,0)坐标处，从坐标($x,$y)处用参数$color指定的颜色执行区域填充。 **与坐标($x,$y)点颜色相同且相邻的点都会被填充。** 例如在下面的示例中，将画布的背景设置为红色。代码如下所示：

.. code-block:: php

    <?php
	$im = imagecreatetruecolor(100, 100); //创建100*100大小的画布
	$red = imagecolorallocate($im, 255, 0, 0); //设置一个颜色变量为红色

	imagefill($im, 0, 0, $red); //将背景设为红色

	header('Content-type:image/png'); //通知浏览器这不是文本而是一个图片
	imagepng($im); //生成PNG格式的图片输出给浏览器

	imagedestroy($im); //销毁图像资源，释放画布占用的内存空间
    ?>

绘制点和线imageSetPixel()、imageline()
"""""""""""""""""""""""""""""""""""""
画点和线是绘制图像中最基本的操作，如果灵活使用，可以通过它们绘制出千变万化的图像。在PHP中，使用imageSetPixel()函数在画布中绘制一个单一像素的点，并且可以设置点的颜色。其函数的原型如下所示：

``bool imagesetpixel(resource $image,int $x,int $y,int $color) //画一个单一像素``

该函数在第一个参数$image中提供的画布上，距离圆点分别为$x和$y的坐标位置，绘制一个颜色为$color的一个像素点。理论上使用画点函数便可以画出所需要的所有图形，也可以使用其他的绘图函数。如果需要绘制一条线段，可以使用imageline()函数，其语法格式如下所示：

``bool imageline(resource $image,int $x1,int $y1,int $x2,int $y2,int $color) //画一条线段``

我们都知道两点确定一条线段，所以该函数使用$color颜色在图像$image中，从坐标($x1,$y1)开始到($x2,$y2)坐标结束画一条线段。

绘制矩形imageRectangle()、imageFilledRectangle()
"""""""""""""""""""""""""""""""""""""""""""""""
可以使用imageRectangle()函数绘制矩形，也可以通过imageFilledRectangle()函数绘制一个矩形并填充。这两个函数的语法格式如下所示：

.. code-block:: php

    <?php
	bool imagerectangle(resource $image,int $x1 , int $y1,int $x2,int $y2,int $color) //画一个矩形
	bool imagefilledrectangle(resource image,int $x1 ,int $y1 ,int $x2 ,int $y2,int $color) //画一个矩形并填充
    ?>

这两个函数的行为类似，都是在$image图像中画一个矩形，只不过前者是使用$color参数指定矩形的边线颜色，而后者则是使用这个颜色填充矩形。相对于图像左上角的(0,0)位置，矩形的左上角坐标为($x1,$y1)，右下角坐标为($x2,$y2)。

绘制多边形imagePolygon()、imagefilledpolygon()
""""""""""""""""""""""""""""""""""""""""
可以使用imagePolygon()函数绘制一个多边形，也可以通过imageFilledPolygon()函数绘制一个多边形并填充。这两个函数的语法格式如下：

.. code-block:: php

    <?php
	bool imagepolygon(resource $image,array $points,int $num_points,int $color) //画一个多边形
	bool imagefilledpolygon(resource $image ,array $points,int $num_points,int $color) //画一个多边形并填充
    ?>

这两个函数的行为类似，都是在$image图像中画一个多边形，只不过前者是使用$color参数指定多边形的边线颜色，而后者则是使用这个颜色填充多边形。第二个参数$points是一个PHP数组，包含了多边形的各个顶点坐标。即points[0]=x0,points[1]=y0,points[2]=x1,points[3]=y1,依此类推。第三个参数$num_points是顶点的总数，必须大于3。

绘制椭圆imageEllipse()、imageFilledElipse()
""""""""""""""""""""""""""""""""""""""
可以使用imageEllipse()函数绘制一个椭圆，也可以通过imageFilledEllipse()函数绘制一个椭圆并填充。这两个函数的语法格式如下：

.. code-block:: php

    <?php
	bool imageellipse(resource $image,int $cx,int $cy,int $w,int $h,int $color) //画一个椭圆
	bool imagefilledellipse(resource $image,int $cx,int $cy,int $w,int $h,int $color) //画一个椭圆填充
    ?>

绘制弧线imageArc()
""""""""""""""
前面介绍的3D扇形统计图示例，就是使用绘制填充圆弧的函数实现的。可以使用imageArc()函数绘制一条弧线，以及圆形和椭圆形。这个函数的语法格式如下：

``bool imagearc(resource $image ,int $cx,int $cy,int $w,int $h,int $s,int $e ,int $color) //画椭圆弧``

相对于画布左上角坐标(0,0),该函数以($cx,$cy)坐标为中心，在$image所代表的图像中画一个椭圆弧。其中参数$w和$h分别指定了椭圆的宽度和高度，起始点和结束点以$s和$e参数以角度指定。0º位于三点钟位置，以顺时针方向绘画。如果要绘制一个完整的圆形，首先要将参数$w和$h设置为相等的值，然后将起始角度$s设置为0，结束角度$e指定为360.如果需要绘制填充圆弧，可以查询imageFilledArc()函数使用。

在图像中绘制文字
^^^^^^^^
在图像中显示的文字也需要按坐标位置画上去。在PHP中不仅支持比较多的字体库，而且提供了非常灵活的文字绘制方法。例如，在图中绘制缩放、倾斜、旋转的文字等。可以使用imageString()、imageStringUP()或imageChar()等函数使用的字体文字绘制到图像中。这些函数的原型如下所示：

.. code-block:: php

    <?php
	bool imagestring(resource $image,int $font,int $x ,int $y,string $s,int $color) //水平地画一行字符串
	bool imagestringup(resource $image,int $font,int $x ,int $y,string $s,int $color) //垂直地画一行字符串
	bool imagechar(resource $image,int $font,int $x ,int $y,char $c,int $color) //水平地画一个字符
	bool imagecharup(resource $image,int $font,int $x ,int $y,char $c,int $color) //垂直地画一个字符
    ?>

在上面列出来的四个函数中，前两个函数imageString()和imageStringUP()分别用来向图像中水平和垂直输出一行字符串，而后两个函数imageChar()和imageCharUP()分别用来向图像中水平和垂直输出一个字符。虽然这四个函数有所差异，但调用方式类似。它们都是在$image图像中绘制由第五个参数指定的字符，绘制的位置都是从坐标($x，$y)开始输出。如果是水平地面画一行字符串则是从左向右输出，而垂直地画一行字符串则是从下而上输出。这些函数都可以通过最后一个参数$color给出文字的颜色。第二个参数$font则给出了文字字体标识符，其值为整数1、2、3、4或5，则是使用内置的字体，数字越大则输出的文字尺寸就越大。下面是在一个图像中输出文字的示例：

.. code-block:: php

    <?php
	$im = imagecreate(150, 150);

	$bg = imagecolorallocate($im, 255, 255, 255); //设置画布的背景为白色
	$black = imagecolorallocate($im, 0, 0, 0); //设置一个颜色变量为黑色

	$string = "LAMPBrother"; //在图像中输出的字符

	imagestring($im, 3, 28, 70, $string, $black); //水平的将字符串输出到图像中
	imagestringup($im, 3, 59, 115, $string, $black); //垂直由下而上输到图像中
	for($i=0,$j=strlen($string);$i<strlen($string);$i++,$j--){ //循环单个字符输出到图像中
	    imagechar($im, 3, 10*($i+1),10*($j+2),$string[$i],$black); //向下倾斜输出每个字符
	    imagecharup($im, 3, 10*($i+1),10*($j+2),$string[$i],$black); //向上倾斜输出每个字符
	}

	header('Content-type:image/png');
	imagepng($im);
    ?>

通过上面介绍的四个函数输出内置的字体外，还可以使用imageTtfText()函数，输出一种可以缩放的与设备无关的TrueType字体。TrueType是用数学函数描述字体轮廓外形，即可以用做打印字体，又可以用作屏幕显示，各种操作系统都可以兼容这种字体。由于它是由指令对字形进行描述，因此它与分辨率无关，输出时总是按照打印机的分辨率输出。无论放大或是缩小，字体总是光滑的，不会有锯齿出现。例如在Windows系统中，字体库所在的文件夹C:\WINDOWS\Fonts下，对TrueType字体都有标注，如simsun.ttf为TrueType字体中的“宋体”。imageTtfText()函数的原型如下所示：

``array imagettftext(resource $image,float $size ,float $angle,int $x,int $y,int $color ,string $fontfile,string $text)``

该函数需要多个参数，其中参数$image需要提供一个图像资源。参数$size用来设置字体大小，根据GD库版本不同，应该以像素大小指定（GD1）或点大小（GD2）。参数$angle是角度制表示的角度，0º为从左向右读的文本，更高数值表示逆时针旋转。例如90º表示从下向上读的文本。并由($x,$y)两个参数所表示的坐标，定义了一个字符的基本点，大概是字符的左下角。而这和imagestring()函数有所不同，其($x,$y)坐标定义了第一个字符的左上角。参数$color指定颜色索引。使用负的颜色索引值具有关闭防锯齿的效果。参见$fontfile是想要使用的TrueType字体的路径。根据PHP所使用的GD库的不同，当fontfil没有以“/”开头时则“.ttf”将被加到文件名之后，并且会在库定义字体路径中尝试搜索该文件名。最后一个参数$text指定需要输出的文本字符串，可以包含十进制数字化字符表示（形式为：€）来访问字体中超过位置127的字符。UTF-8编码的字符串可以直接传递。如果字符串中使用的某个字符不被字体支持，一个空心矩形将替换该字符。

imagettftext()函数返回一个含有8个单元的数组，表示了文本外框的四个角，顺序为左下角，右下角，右上角，左上角。这些点是相对于文本的而和角度无关，因此“左上角”指的是以水平方向看文字时其左上角。我们通过在下例中的脚本，生成一个白色的400X30像素的PNG图片，其中有黑色（带灰色阴影）“宋体”字体写的“回忆经典！”代码如下所示：

.. code-block:: php

    <?php
	$im = imagecreatetruecolor(400, 30); //创建400 30像素大小的画布

	$white = imagecolorallocate($im, 255, 255, 255);
	$grey = imagecolorallocate($im, 128, 128, 128);
	$black = imagecolorallocate($im, 0, 0, 0);

	imagefilledrectangle($im, 0, 0, 399, 29, $white); //输出一个使用白色填充的矩形作为背景

	//如果有中文输出，需要将其转码，转换为UTF-8的字符串才可以直接传递
	//$text = iconv("GB2312", "UTF-8", "回忆经典");
	$text = "回忆经典";

	//设定字体，将系统中与simsun.ttc对应的字体复制到当前目录下
	$font = 'C:\Windows\Fonts\simsun.ttc';

	imagettftext($im, 20, 0, 12, 21, $grey, $font, $text); //输出一个灰色的字符串作为阴影
	imagettftext($im, 20, 0, 10, 20, $black, $font, $text); //在阴影上输出一个黑色的字符串

	header("Content-type: image/png");
	imagepng($im);

	imagedestroy($im);
    ?>

图形图像的典型应用
-----------------

创建一个简单的图像
^^^^^^^^^^^^^^^^
使用GD2函数库可以实现对各种图形图像的处理。创建画布是使用GD2函数库来创建图像的第一步，无论创建什么样的图像，首先都需要创建一个画布，其他操作都将在这个画布上完成。在GD2函数库中创建画布，可以通过imagecreate()函数实现。颜色RGB值为(255,66,159)，最后输出一个gif格式的图像。

使用imagecreate()函数创建一个宽200像素、高60像素的画布，并且设置画布背景

.. code-block:: php

    <?php
	$m = imagecreate(200,60);   //创建一个画布大小为200px宽度和60px高度
	$w = imagecolorallocate($m,225,66,159);   //设置画布的背景颜色
	$ima = imagegif($m);  //输出图像
    ?>

使用GD2函数在照片上添加文字
^^^^^^^^^^^^^^^^^^^^^^^^^
PHP中的GD库支持中文，但必须要以UTF-8格式的参数来进行传递，如果使用imagestring()函数直接绘制中文字符串就会显示乱码，这是因为GD2对中文只能接收UTF-8编码格式，并且默认使用了英文的字体，所以只需要将显示的中文字符串进行转码，并设置绘制中文字符使用的字体，即可绘制中文字符。

.. code-block:: php

    <?php
	header("content-type:image/jpeg");       //定义输出图像的类型
	$im=imagecreatefromjpeg("liumiao.jpg");//输入照片
	$textcolor=imagecolorallocate($im,56,73,136);  //设置字体颜色
	$fnt="c:/windows/fonts/simhei.ttf";      //定义字体
	$motto=chr(0xe9).chr(0x95).chr(0xBF);   //定义输出字符串 UTF-8编码字体
	imageTTFText($im,50,50,150,150,$textcolor,$fnt,$motto);   //写入到图片中
	imagegif($im);        //建立gif图形
	imageDestroy($im);   //结束图形，释放内存空间
    ?>

注意：一个汉字占3个字节，所以应该选择编辑汉字的总数乘以3个字节的内容。其中，0(零)x是必定的,im是指照片，50是字体的大小，50是文字的水平方向，150，150是文字的坐标值。应用该方法还可以制作电子相册。

使用图形处理技术生成验证码
^^^^^^^^^^^^^^^^^^^^^^^^
验证码就是将一串随机产生的数字或符号，动态生成一副图片。再在图片中加上一些干扰像素，只要让用户可以通过肉眼识别其中的信息即可。并将其在表单提交时使用，只有审核成功后才能使用某项功能。

很多地方都需要使用验证码，经常出现在用户注册、登陆或者网上发帖子的时候。因为你的Web站有时会碰到客户机恶意攻击，其中一种很常见的攻击手段就是身份欺骗。

1. 设计验证码类

设计一个验证码类ValidationCode。将该类声明在文件ValidationCode.php中，并通过面向对象的特性将一些实现的细节封装在该类中。只要在创建对象时，为构造方法提供三个参数，包括创建验证码图片的宽度、高度及验证码字母个数，就可以成功创建一个验证码类的对象。默认验证码的宽度为60个像素，高度为20个像素，由四个字母组成。该类的声明代码如下所示：

.. code-block:: php

    <?php
	/* 类ValidationCode声明在文件名为Validationcode.php中    */
	/* 通过该类的对象可以动态获取验证码图片，和验证码字符串 */
	class ValidationCode {
	    private $width;         //验证码图片的宽度
	    private $height;        //验证码图片的高度
	    private $codeNum;    //验证码字符的个数
	    private $checkCode;  //验证码字符
	    private $image;          //验证码画布

	    /* 构造方法用来实例化验证码对象，并为一些成员属性初始化 */
	    /* 参数width: 设置验证码图片的宽度，默认宽度值为60像素   */
	    /* 参数height: 设置验证码图片的高度，默认高度值为20像素  */
	    /* 参数codeNum: 设置验证码中字母和数字的个数，默认个数为4个 */
	    function __construct ($width = 60, $height = 20, $codeNum = 4) {
	        $this->width = $width; //为成员属性width初始化
	        $this->height = $height; //为成员属性height初始化
	        $this->codeNum = $codeNum; //为成员属性codeNum初始化
	        $this->checkCode = $this->createCheckCode(); //为成员属性checkCode初始化
	    }

	    function showImage () { //通过访问该方法向浏览器中输出图像
	        $this->getCreateImage(); //调用内部方法创建画布并对其进行初始化
	        $this->outputText(); //向图像中输出随机的字符串
	        $this->setDisturbColor(); //向图像中设置一些干扰像素
	        $this->outputImage(); //生成相应格式的图像并输出
	    }

	    function getCheckCode () { //访问该方法获取随机创建的验证码字符串
	        return $this->checkCode; //返回成员属性$checkCode保存的字符串
	    }

	    private function getCreateImage () { //用来创建图像资源，并初始化背影
	        $this->image = imageCreate($this->width, $this->height);
	        $back = imageColorAllocate($this->image, 255, 255, 255);
	        $border = imageColorAllocate($this->image, 0, 0, 0);
	        imageRectangle($this->image, 0, 0, $this->width - 1, $this->height - 1, $border);
	    }

	    private function createCheckCode ($ascii_number="") { //随机生成用户指定个数的字符串
	        for ($i = 0; $i < $this->codeNum; $i++) {
	            $number = rand(0, 2);
	            switch ($number) {
	                case 0 :
	                    $rand_number = rand(48, 57);
	                    break;    //数字
	                case 1 :
	                    $rand_number = rand(65, 90);
	                    break;    //大写字母
	                case 2 :
	                    $rand_number = rand(97, 122);
	                    break;   //小写字母
	            }
	            $ascii = sprintf("%c", $rand_number);
	            $ascii_number = $ascii_number . $ascii;
	        }
	        return $ascii_number;
	    }

	    private function setDisturbColor () {   //设置干扰像素，向图像中输出不同颜色的100个点
	            for ($i = 0; $i <= 100; $i++) {
	                $color = imagecolorallocate($this->image, rand(0, 255), rand(0, 255), rand(0, 255));
	                imagesetpixel($this->image, rand(1, $this->width - 2), rand(1, $this->height - 2), $color);
	            }
	        }

	    private function outputText () {  //随机颜色、随机摆放、随机字符串向图像中输出
	            for ($i = 0; $i <= $this->codeNum; $i++) {
	                $bg_color = imagecolorallocate($this->image, rand(0, 255), rand(0, 128), rand(0, 255));
	                $x = floor($this->width / $this->codeNum) * $i + 3;
	                $y = rand(0, $this->height - 15);
	                imagechar($this->image, 5, $x, $y, $this ->checkCode[$i], $bg_color);
	              }
	        }

	    private function outputImage () { //自动检测GD支持的图像类型，并输出图像
	        if (imagetypes() & IMG_GIF) { //判断生成GIF格式图像的函数是否存在
	            header("Content-type: image/gif"); //发送标头信息设置MIME类型为image/gif
	            imagegif($this->image); //以GIF格式将图像输出到浏览器
	        } elseif (imagetypes() & IMG_JPG) { //判断生成JPG格式图像的函数是否存在
	            header("Content-type: image/jpeg"); //发送标头信息设置MIME类型为image/jpeg
	            imagejpeg($this->image, "", 0.5); //以JPEN格式将图像输出到浏览器
	        } elseif (imagetypes() & IMG_PNG) { //判断生成PNG格式图像的函数是否存在
	            header("Content-type: image/png"); //发送标头信息设置MIME类型为image/png
	            imagepng($this->image); //以PNG格式将图像输出到浏览器
	        } elseif (imagetypes() & IMG_WBMP) { //判断生成WBMP格式图像的函数是否存在
	            header("Content-type: image/vnd.wap.wbmp");    //发送标头为image/wbmp
	            imagewbmp($this->image); //以WBMP格式将图像输出到浏览器
	        } else { //如果没有支持的图像类型
	            die("PHP不支持图像创建！"); //不输出图像，输出一错误消息，并退出程序
	        }
	    }

	    function __destruct () { //当对象结束之前销毁图像资源释放内存
	        imagedestroy($this->image); //调用GD库中的方法销毁图像资源
	    }
	}
    ?>

2. 应用验证码类的实例对象
在下面的脚本imgcode.php中，包含验证码类ValidationCode所在文件，并创建该类对象。然后，从验证码对象中获取随机生成的验证码图片，发送到浏览器中。接着，又从验证码对象中获取验证码字符串，使用Session技术保留在服务器中。代码如下所示：
输出验证码图片的脚本文件imgcode.php

.. code-block:: php

    <?php
	session_start(); //开启SESSION
	require_once('ValidationCode.php'); //包含验证码所在的类文件
	$image = new ValidationCode(60,20,4); //创建验证码对象，并初始化尺寸
	$image->showImage(); //调用对象中的方法向浏览器发送图片
	$_SESSION['validationcode'] =$image->getCheckCode(); //将对应的字符串存入会话中
    ?>

3. 表单中应用验证码

.. code-block:: php

    <?php
	session_start();
	if(isset($_POST['dosubmit'])) {
	    if(strtoupper($_SESSION['code']) == strtoupper($_POST['code']) ) {
	        echo "输入成功!<br>";
	    }else{
	        echo "输入不对!<br>";
	    }
	}
	?>

	<body>
	<form action="reg.php" method="post">
	    username: <input type="text" name="username"> <br>
	    password: <input type="password" name="password"> <br>
	    code: <input type="text" onkeyup="if(this.value!=this.value.toUpperCase()) this.value=this.value.toUpperCase()" size="6" name="code">
	    <img src="code.php" onclick="this.src='code.php?'+Math.random()" /> <br>
	    <input type="submit" name="dosubmit" value="登 录"> <br>
	</form>
	</body>
    ?>

使用柱形图统计图书月销售量
^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
	include("jpgraph/src/jpgraph.php");
	include("jpgraph/src/jpgraph_bar.php");
	//以上是引用柱形图对象所在的文件
	$datay=array(160,180,203,289,405,408,489,299,166,187,105);  //定义数组
	//创建画布
	$graph=new graph(600,300,"auto");
	$graph->SetScale("textlin");
	$graph->yaxis->scale->SetGrace(20);
	//创建画布阴影
	$graph->SetShadow();
	//设置显示区左，右，上，下距边线的距离，单位为像素
	$graph->img->SetMargin(40,30,30,40);
	//创建一个矩形对象
	$bplot=new BarPlot($datay);
	//创建一个柱形图的颜色
	$bplot->setfillcolor('orange');
	//设置显示数字
	$bplot->value->show();
	//在柱形图中显示格式化的图书销量
	$bplot->value->setformat('%d');
	//将柱形图添加到图像中
	$graph->add($bplot);
	//设置画布背景色为淡蓝色
	$graph->setmargincolor("lightblue");
	//创建标题
	$graph->title->set("2011年PHP图书销量");
	//设置X坐标轴文字
	$a=array("1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月");
	$graph->xaxis->setticklabels($a);
	//设置字体
	$graph->title->setfont(FF_SIMSUN);
	$graph->xaxis->setfont(FF_SIMSUN);
	//输出矩形图表
	$graph->stroke();
    ?>

使用折线图统计图书月销售量
^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
	include("jpgraph/src/jpgraph.php");
	include ("jpgraph/src/jpgraph_line.php");
	//引用折线图LinePlot类文件
	$datay= array(8320,9360,14956,17028,13060,15376,25428,16216,28548,18632,22724,28460); //填充的数据
	$graph = new Graph(600,300,"auto"); //创建画布
	$graph->img->SetMargin(50,40,30,40); //设置统计图所在画布的位置，左边距50、右边距40、上边距30、下边距40，单位为像素
	$graph->img->SetAntiAliasing(); //设置折线的平滑状态
	$graph->SetScale("textlin"); //设置刻度样式
	$graph->SetShadow(); //创建画布阴影
	$graph->title->Set("2007年《PHP5从入门到精通》图书月销售额折线图"); //设置标题
	$graph->title->SetFont(FF_SIMSUN,FS_BOLD); //设置标题字体
	$graph->SetMarginColor("lightblue"); //设置画布的背景颜色为淡蓝色
	$graph->yaxis->title->SetFont(FF_SIMSUN,FS_BOLD); //设置Y轴标题的字体
	$graph->xaxis->SetPos("min");
	$graph->yaxis->HideZeroLabel();
	$graph->ygrid->SetFill(true,'#EFEFEF@0.5','#BBCCFF@0.5');
	$a=array("1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月");  //X轴
	$graph->xaxis->SetTickLabels($a);  //设置X轴
	$graph->xaxis->SetFont(FF_SIMSUN); //设置X坐标轴的字体
	$graph->yscale->SetGrace(20);

	$p1 = new LinePlot($datay); //创建折线图对象
	$p1->mark->SetType(MARK_FILLEDCIRCLE);  //设置数据坐标点为圆形标记
	$p1->mark->SetFillColor("red"); //设置填充的颜色
	$p1->mark->SetWidth(4); //设置圆形标记的直径为4像素
	$p1->SetColor("blue"); //设置折形颜色为蓝色
	$p1->SetCenter(); //在X轴的各坐标点中心位置绘制折线
	$graph->Add($p1); //在统计图上绘制折线
	$graph->Stroke(); //输出图像
    ?>

使用3D饼型图统计各类商品的年销售额比率
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
	include_once("jpgraph/src/jpgraph.php");
	include_once("jpgraph/src/jpgraph_pie.php");
	include_once("jpgraph/src/jpgraph_pie3d.php");
	$data =array(266036,295621,335851,254256,254254,685425);   //定义数组
	$graph = new PieGraph(540,260,'auto');    //创建画布
	$graph->SetShadow();    //设置画布阴影
	$graph->title->Set("应用3D饼形图统计2007年商品的年销售额比率");                    //创建标题
	$graph->title->SetFont(FF_SIMSUN,FS_BOLD);  //设置标题字体
	$graph->legend->SetFont(FF_SIMSUN,FS_NORMAL);   //设置图例字体
	$p1 = new PiePlot3D($data);   //创建3D饼形图对象
	$p1->SetLegends(array("IT数码","家电通讯","家居日用","服装鞋帽","健康美容","食品烟酒"));
	$targ=array("pie3d_csimex1.php?v=1","pie3d_csimex1.php?v=2","pie3d_csimex1.php?v=3",
	    "pie3d_csimex1.php?v=4","pie3d_csimex1.php?v=5","pie3d_csimex1.php?v=6");
	$alts=array("val=%d","val=%d","val=%d","val=%d","val=%d","val=%d");
	$p1->SetCSIMTargets($targ,$alts);
	$p1->SetCenter(0.4,0.5);    //设置饼形图所在画布的位置
	$graph->Add($p1);    //将3D饼图形添加到图像中
	$graph->StrokeCSIM();    //输出图像到浏览器
    ?>