=================
辅助函数-路径函数
=================

路径操作
========

应用路径操作
------------

app_path：返回 app 目录的完整路径
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
你还可以使用 ``app_path`` 函数来生成相对于 ``app`` 目录的文件完整路径：

.. code-block:: php

    <?php
    $path = app_path();

    $path = app_path('Http/Controllers/Controller.php');

base_path：函数返回项目根目录的完整路径
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
你还可以使用 ``base_path`` 函数生成指定文件相对于项目根目录的完整路径：

.. code-block:: php

    <?php
    $path = base_path();

    $path = base_path('vendor/bin');

config_path：函数返回应用程序 config 目录的完整路径
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
你也可以使用 ``config_path`` 函数来生成应用程序配置目录中给定文件的完整路径：

.. code-block:: php

    <?php
    $path = config_path();

    $path = config_path('app.php');

database_path：函数返回应用程序 database 目录的完整路径
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
你也可以使用 ``database_path`` 函数来生成数据库目录中给定文件的完整路径：

.. code-block:: php

    <?php
    $path = database_path();

    $path = database_path('factories/UserFactory.php');

public_path：函数返回 public 目录的完整路径
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
你也可以使用 ``public_path`` 函数来生成 ``public`` 目录中给定文件的完整路径：

.. code-block:: php

    <?php
    $path = public_path();

    $path = public_path('css/app.css');

resource_path：函数返回 resources 目录的完整路径
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
你也可以使用 ``resource_path`` 函数来生成相对于资源目录的指定文件的完整路径：

.. code-block:: php

    <?php
    $path = resource_path();

    $path = resource_path('assets/sass/app.scss');

storage_path：函数返回 storage 目录的完整路径
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
你也可以使用 ``storage_path`` 来生成相对于储存目录的指定文件的完整路径：

.. code-block:: php

    <?php
    $path = storage_path();

    $path = storage_path('app/file.txt');

样例
^^^^

.. code-block:: php

    <?php
    // web文档根目录D:\phpStudy\WWW
    echo base_path(); // D:\phpStudy\WWW\pzapp
    echo app_path(); // D:\phpStudy\WWW\pzapp\app
    echo public_path(); // D:\phpStudy\WWW\pzapp\public
    echo config_path(); // D:\phpStudy\WWW\pzapp\config
    echo storage_path(); // D:\phpStudy\WWW\pzapp\storage
    echo database_path(); // D:\phpStudy\WWW\pzapp\database

URL访问路径
-----------

生成资源路径
^^^^^^^^^^^^^

asset：函数使用当前请求的协议（ HTTP 或 HTTPS ）为资源文件生成 URL
""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $url = asset('img/photo.jpg');

secure_asset：函数使用 HTTPS 协议为资源文件生成 URL
"""""""""""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $url = secure_asset('img/photo.jpg');

url：函数生成给定路径的标准 URL
"""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $url = url('user/profile');

    $url = url('user/profile', [1]); // http://www.laraveldemo.com.cn/user/profile/1

如果没有提供路径，则返回 ``Illuminate\Routing\UrlGenerator`` 实例：

.. code-block:: php

    <?php
    $current = url()->current();

    $full = url()->full();

    $previous = url()->previous();

secure_url：函数为给定的路径生成一个标准的 HTTPS URL
""""""""""""""""""""""""""""""""""""""""""""""""""""

.. code-block:: php

    <?php
    $url = secure_url('user/profile');

    $url = secure_url('user/profile', [1]); // https://www.laraveldemo.com.cn/user/profile/1


mix：函数获取 版本化 Mix 文件 的路径
""""""""""""""""""""""""""""""""""""

.. code-block:: js

    mix.js('resources/assets/js/app.js', 'public/js')
       .version();
生成版本化文件后，你不会知道确切的文件名。因此，你应该在你的视图中使用 Laravel 的全局辅助函数 ``mix`` 来正确加载名称被哈希后的文件。 laravel自带了 ``laravel-mix`` ，用于对 ``js`` 、 ``css`` 、图片等静态资源进行打包。生成的文件的命名会是： ``app.asjduiik2l1323879dasfydua23.js`` ,  即 ``js`` 原文件名+hash+.js 后缀，因为中间的那个 hash 是随时会变化的，所以在页面引入 js 文件的时候，就不能写死文件的路径，而是使用 ``mix('app.js')`` ，此时 laravel 会自动去匹配当前的 ``app.js`` 对应哪个app+hash+.js的文件（项目 ``public`` 目录下会有一个 ``mix-manifest.json`` , 这里面保存了两者的对应关系，每次打包静态资源的时候都会更新该文件）。

而有些时候我们并不希望静态资源的名称中被加上 ``hash`` 值（大部分情况是独自引入的非 ``nodejs`` 模块的第三方库），这个时候就可以直接使用 ``asset`` 方法，它就是直接简单粗暴地找你给它名称的文件咯。

.. code-block:: php

    <?php
    $url = mix('css/app.css'); // 返回 /css/app.asjduiik2l1323879dasfydua23.css 这是一个相对于域名根的绝对路径
    $url = asset('css/app.css'); // 返回 http://www.laraveldemo.com.cn/css/app.css

.. note:: 即使没有使用版本控制，也可以调用该函数。


生成动作路径
^^^^^^^^^^^^

action：函数为指定的控制器动作生成一个 URL
"""""""""""""""""""""""""""""""""""""""""""
你不需要传递完整的控制器命名空间。只需要传递相对于 ``App\Http\Controllers`` 的命名空间的控制器类名称：

.. code-block:: php

    <?php
    $url = action('HomeController@index');

如果该方法接受路由参数，则可以将它们作为方法的第二个参数传递：

.. code-block:: php

    <?php
    $url = action('UserController@profile', ['id' => 1]);

route：函数为给定的命名路由生成一个 URL
""""""""""""""""""""""""""""""""""""""""
.. code-block:: php

    <?php
    $url = route('routeName');

如果路由接受参数，则可以将它们作为方法的第二个参数传递：

.. code-block:: php

    <?php
    $url = route('routeName', ['id' => 1]);

默认情况下， ``route`` 函数生成的是绝对 ``URL`` 。如果你想生成一个相对 ``URL`` ，你可以传递 ``false`` 作为第三个参数：

.. code-block:: php

    <?php
    $url = route('routeName', ['id' => 1], false);



