========
文件存储
========

简介
====
Laravel 提供了一个强大的文件系统抽象，这得益于 Frank de Jonge 强大的 `Flysystem <https://github.com/thephpleague/flysystem>`_ 扩展包。 Laravel 文件系统集成为使用本地文件系统、 ``Amazon S3`` 和 ``Rackspace`` 云存储提供了简单易用的驱动程序。更棒的是，由于每个系统的 ``API`` 保持不变，所以在这些存储选项之间切换是非常简单的。

配置
====
文件系统的配置文件位于 ``config/filesystems.php`` 。在这个文件中你可以配置所有「磁盘」。每个磁盘代表特定的存储驱动及存储位置。每种支持的驱动程序的示例配置都包含在配置文件中。因此，只需要修改配置即可反映你的存储偏好和凭据。

当然，你可以根据需要配置多个磁盘，甚至你还可以使多个磁盘共用同一个驱动。

公开磁盘
--------
``public`` 磁盘适用于要公开访问的文件。默认情况下， ``public`` 磁盘使用 ``local`` 驱动，并且将这些文件存储在 ``storage/app/public`` 目录下。为了使它们能通过网络访问，你需要创建 ``public/storage`` 到 ``storage/app/public`` 的符号链接。这种方式能把可公开访问文件都保留在同一个目录下，以便在使用零停机时间部署系统如 `Envoyer <https://envoyer.io/>`_ 的时候，就可以轻松地在不同的部署之间共享这些文件。

你可以使用 ``Artisan`` 命令 ``storage:link`` 来创建符号链接：

.. code-block:: shell

    php artisan storage:link

当然，一旦一个文件被存储并且已经创建了符号链接，你就可以使用辅助函数 ``asset`` 来创建文件的 ``URL`` ：

.. code-block:: php

    <?php
    echo asset('storage/file.txt');

本地磁盘
--------
使用 ``local`` 驱动时，所有文件操作都与你在配置文件中定义的 ``root`` 目录相关。该目录的默认值是 ``storage/app`` 。因此，以下方法会把文件存储在 ``storage/app/file.txt`` 中：

.. code-block:: php

    <?php
    Storage::disk('local')->put('file.txt', 'Contents');

驱动程序先决条件
----------------
Composer 包
^^^^^^^^^^^
在使用 ``S3`` 或 ``Rackspace`` 的驱动之前，你需要通过 ``Composer`` 安装相应的软件包：

- SFTP: league/flysystem-sftp ~1.0
- Amazon S3: league/flysystem-aws-s3-v3 ~1.0
- Rackspace: league/flysystem-rackspace ~1.0

S3 驱动配置
^^^^^^^^^^^
``S3`` 驱动配置信息位于你的 ``config/filesystems.php`` 配置文件中。该文件包含 ``S3`` 驱动程序的示例配置数组。 你可以自由使用你自己的 ``S3`` 配置和凭证修改此阵列。为方便起见，这些环境变量与 ``AWS CLI`` 使用的命名约定相匹配。

FTP 驱动配置
^^^^^^^^^^^^
Laravel 的文件系统集成能很好的支持 ``FTP`` ，不过 ``FTP`` 的配置示例并没有被包含在框架默认的 ``filesystems.php`` 文件中。需要的话可以使用下面的示例配置：

.. code-block:: php

    <?php
    'ftp' => [
        'driver'   => 'ftp',
        'host'     => 'ftp.example.com',
        'username' => 'your-username',
        'password' => 'your-password',

        // Optional FTP Settings...
        // 'port'     => 21,
        // 'root'     => '',
        // 'passive'  => true,
        // 'ssl'      => true,
        // 'timeout'  => 30,
    ],

SFTP 驱动器的配置
^^^^^^^^^^^^^^^^^
Laravel 的 Flysystem 集成包与 ``SFTP`` 协同得非常好；不过，在该框架的默认配置文件 ``filesystems.php`` 中并没有包含示范配置。如果要配置 ``SFTP`` 文件系统，可以使用如下示例配置：

.. code-block:: php

    <?php
    'sftp' => [
        'driver' => 'sftp',
        'host' => 'example.com',
        'username' => 'your-username',
        'password' => 'your-password',

        // 基于认证的 SSH key 设置...
        // 'privateKey' => '/path/to/privateKey',
        // 'password' => 'encryption-password',

        // 可选的 SFTP 设置...
        // 'port' => 22,
        // 'root' => '',
        // 'timeout' => 30,
    ],

Rackspace 驱动器的配置
^^^^^^^^^^^^^^^^^^^^^^
Laravel 的 ``Flysystem`` 集成包与 ``Rackspace`` 协同得非常好；不过，在该框架的默认配置文件 ``filesystems.php`` 中并没有包含示范配置。如果要配置 ``Rackspace`` 文件系统，可以使用如下示例配置：

.. code-block:: php

    <?php
    'rackspace' => [
        'driver'    => 'rackspace',
        'username'  => 'your-username',
        'key'       => 'your-key',
        'container' => 'your-container',
        'endpoint'  => 'https://identity.api.rackspacecloud.com/v2.0/',
        'region'    => 'IAD',
        'url_type'  => 'publicURL',
    ],

获取 Disk 实例
==============
``facade`` 方式 ``Storage`` 可以用于与任何已配置磁盘互操作。例如该 ``facade`` 方式可以使用 ``put`` 方法存储头像到默认磁盘。如果调用 ``facade`` 方式 ``Storage`` 方法，并且一开始没有调用 ``disk`` 方法，那么所调用的方法会自动传递给默认的磁盘：

.. code-block:: php

    <?php
    Storage::put('avatars/1', $fileContents);

如果应用程序要与多个磁盘进行互操作，可使用 ``facade`` 方式 ``Storage`` 中的 ``disk`` 方法，对特定磁盘上的多个文件进行操作：

.. code-block:: php

    <?php
    Storage::disk('s3')->put('avatars/1', $fileContents);

检索文件
========
``get`` 方法可以用于检索文件的内容，此方法返回该文件的原始字符串内容。 切记，所有文件路径的指定都应该相对于为磁盘配置的 ``root`` 目录：

.. code-block:: php

    <?php
    $contents = Storage::get('file.jpg');

``exists`` 方法可以用来判断磁盘上是否存在指定的文件：

.. code-block:: php

    <?php
    $exists = Storage::disk('s3')->exists('file.jpg');

下载文件
--------
``download`` 方法可用于生成一个响应，强制用户的浏览器在给定路径下载文件。 ``download`` 方法接受一个文件名作为方法的第二个参数，它将确定用户下载文件时看到的文件名。 最后，你可以传递一个 ``HTTP`` 头数组作为该方法的第三个参数：

.. code-block:: php

    <?php
    return Storage::download('file.jpg');
    return Storage::download('file.jpg', $name, $headers);

文件 URLs
----------
当使用 ``local`` 或者 ``s3`` 驱动时，你可以使用 ``url`` 方法来获取给定文件的 ``URL`` 。如果你使用的是 ``local`` 驱动，一般只是在给定的路径前面加上 ``/storage`` 并返回一个相对的 ``URL`` 到那个文件。如果使用的是 ``s3`` 驱动，会返回完整的远程 ``URL`` ：

.. code-block:: php

    <?php
    $url = Storage::url('file.jpg');

.. note:: 切记，如果使用的是 ``local`` 驱动，则所有想被公开访问的文件都应该放在 ``storage/app/public`` 目录下。此外，你应该在 ``public/storage`` 创建一个符号链接 来指向 ``storage/app/public`` 目录。

临时 URLs
^^^^^^^^^
对于使用 ``s3`` 驱动来存储的文件，可以使用 ``temporaryUrl`` 方法创建给定文件的临时 ``URL`` 。这个方法接收路径和 ``DateTime`` 实例来指定 ``URL`` 何时过期：

.. code-block:: php

    <?php
    $url = Storage::temporaryUrl(
        'file.jpg', now()->addMinutes(5)
    );

自定义本地 URL 主机
^^^^^^^^^^^^^^^^^^^^
如果要使用 ``local`` 驱动为存储在磁盘上的文件预定义主机，可以向磁盘配置数组添加一个 ``url`` 选项：

.. code-block:: php

    <?php
    'public' => [
        'driver' => 'local',
        'root' => storage_path('app/public'),
        'url' => env('APP_URL').'/storage',
        'visibility' => 'public',
    ],

文件元数据
----------
除了读写文件，Laravel 还可以提供有关文件本身的信息。例如， ``size`` 方法可用来获取文件的大小（以字节为单位）：

.. code-block:: php

    <?php
    $size = Storage::size('file.jpg');

``lastModified`` 方法返回最后一次文件被修改的 ``UNIX`` 时间戳：

.. code-block:: php

    <?php
    $time = Storage::lastModified('file.jpg');

保存文件
========
``put`` 方法可用于将原始文件内容保存到磁盘上。你也可以传递 PHP 的 ``resource`` 给 ``put`` 方法，它将使用文件系统下的底层流支持。强烈建议在处理大文件时使用流：

.. code-block:: php

    <?php
    Storage::put('file.jpg', $contents);

    Storage::put('file.jpg', $resource); //???

自动流式传输
------------
如果你想 Laravel 自动管理将给定文件流式传输到你想要的存储位置，你可以使用 ``putFile`` 或 ``putFileAs`` 方法。这个方法接受 ``Illuminate\HTTP\File`` 或 ``Illuminate\HTTP\UploadedFile`` 实例，并自动将文件流式传输到你想要的位置：

.. code-block:: php

    <?php
    use Illuminate\Http\File;
    use Illuminate\Support\Facades\Storage;

    // 自动为文件名生成唯一的 ID...
    Storage::putFile('photos', new File('/path/to/photo'));

    // 手动指定文件名...
    Storage::putFileAs('photos', new File('/path/to/photo'), 'photo.jpg');

关于 ``putFile`` 方法有些重要的事情要注意。请注意，我们只指定一个目录名，而不是文件名。默认情况下， ``putFile`` 方法将生成唯一的 ``ID`` 作为文件名。该文件的路径将被 ``putFile`` 方法返回，因此可以将路径（包括生成的文件名）存储在数据库中。

``putFile`` 和 ``putFileAs`` 方法也接受一个参数来指定存储文件的「可见性」。如果你将文件存储在诸如 ``S3`` 的云盘上，并且该文件可以公开访问，这是特别有用的：

.. code-block:: php

    <?php
    Storage::putFile('photos', new File('/path/to/photo'), 'public');

文件数据写入
------------
``prepend`` 和 ``append`` 方法可以在文件的头部或尾部写入数据：

.. code-block:: php

    <?php
    Storage::prepend('file.log', 'Prepended Text');

    Storage::append('file.log', 'Appended Text');

复制 & 移动文件
---------------
``copy`` 方法可以复制文件到新地址， ``move`` 方法可以重命名文件或移动文件到新地址：

.. code-block:: php

    <?php
    Storage::copy('old/file.jpg', 'new/file.jpg');

    Storage::move('old/file.jpg', 'new/file.jpg');

文件上传
--------
Web 应用中，常用到文件存储的地方也就是上传头像、照片、文件等。 Lavarel 上传文件实例的 ``store`` 方法可以轻松处理文件上传存储的问题。 ``store`` 方法接受一个目录：

.. code-block:: php

    <?php

    namespace App\Http\Controllers;

    use Illuminate\Http\Request;
    use App\Http\Controllers\Controller;

    class UserAvatarController extends Controller
    {
        /**
         * 更新用户头像
         *
         * @param  Request  $request
         * @return Response
         */
        public function update(Request $request)
        {
            $path = $request->file('avatar')->store('avatars'); //存储到指定的目录

            return $path;
        }
    }

上例需要注意几个问题。这里指定的是目录名，而不是文件名。默认情况下， ``store`` 方法自动生成唯一的 ``ID`` 作为文件名。 ``store`` 方法返回包含文件名的路径，以便后续的数据库存储。

``Storage`` 门面的 ``putFile`` 方法可以达到上面同样的操作效果：

.. code-block:: php

    <?php
    $path = Storage::putFile('avatars', $request->file('avatar'));

自定义文件名
^^^^^^^^^^^^
不想自动生成文件名的话，可以使用 ``storeAs`` 方法，它接受路径、文件名和可选的磁盘三个参数：

.. code-block:: php

    <?php
    $path = $request->file('avatar')->storeAs(
        'avatars', $request->user()->id
    );

``Storage`` 门面的 ``putFileAs`` 方法也可以达到上面的操作效果：

.. code-block:: php

    <?php
    $path = Storage::putFileAs(
        'avatars', $request->file('avatar'), $request->user()->id
    );

自定义磁盘
^^^^^^^^^^
默认情况下， ``store`` 方法使用默认磁盘，如果想使用其它磁盘，可以给它传入磁盘名作为第二个参数：

.. code-block:: php

    <?php
    $path = $request->file('avatar')->store(
        'avatars/'.$request->user()->id, 's3'
    );

文件可见性
----------
Laravel 集成的 ``Flysystem`` 系统，对多平台的文件权限进行了「可见性」抽象。文件可声明为 ``public`` 或 ``private`` 。通常，一个声明成 ``public`` 的文件，意味着对其他人可访问。例如，使用 ``S3`` 驱动时，就可以检索声明为 ``public`` 文件的 ``URL`` 。

``put`` 方法可以设置文件的可见性：

.. code-block:: php

    <?php
    Storage::put('file.jpg', $contents, 'public');

``getVisibility`` 和 ``setVisibility`` 方法可以对现存文件进行可见性的检查和设置：

.. code-block:: php

    <?php
    $visibility = Storage::getVisibility('file.jpg');

    Storage::setVisibility('file.jpg', 'public')

删除文件
========
``delete`` 方法接受单个文件名或数组形式的文件名来删除磁盘上的文件：

.. code-block:: php

    <?php
    Storage::delete('file.jpg');

    Storage::delete(['file.jpg', 'file2.jpg']);

必要的话，可以指定磁盘名来删除其下的文件：

.. code-block:: php

    <?php
    Storage::disk('s3')->delete('folder_path/file_name.jpg');

目录
====

获取目录下所有文件
------------------
``files`` 方法返回指定目录下的所有文件的数组。 ``allFiles`` 方法返回指定目录下包含子目录的所有文件的数组：

.. code-block:: php

    <?php
    $files = Storage::files($directory);

    $files = Storage::allFiles($directory);

获取目录下所有的目录
--------------------
``directories`` 方法返回给定目录下的所有目录的数组。 ``allDirectories`` 方法返回指定目录下的包含子目录的所有目录的数组：

.. code-block:: php

    <?php
    $directories = Storage::directories($directory);

    // 递归
    $directories = Storage::allDirectories($directory);

创建目录
--------
``makeDirectory`` 方法会递归创建目录：

.. code-block:: php

    <?php
    Storage::makeDirectory($directory);

删除目录
--------
``deleteDirectory`` 方法会删除指定目录及其所有文件：

.. code-block:: php

    <?php
    Storage::deleteDirectory($directory);

自定义文件系统
==============
虽然 Laravel 的 文件系统提供了一系列开箱即用的驱动，但是它不限于这些，还提供了其他存储系统的适配器。通过这些适配器，可以在 Lavarel 应用中创建自定义驱动。

为了配置自定义文件系统，现在把社区维护的 ``Dropbox`` 适配器添加到项目中：

.. code-block:: shell

    composer require spatie/flysystem-dropbox

接下来，创建一个名为 ``DropboxServiceProvider`` 的服务提供器。在它的 ``boot`` 方法里，用 ``Storage`` 门面的 ``extend`` 自定义驱动：

.. code-block:: php

    <?php
    namespace App\Providers;

    use Storage;
    use League\Flysystem\Filesystem;
    use Illuminate\Support\ServiceProvider;
    use Spatie\Dropbox\Client as DropboxClient;
    use Spatie\FlysystemDropbox\DropboxAdapter;

    class DropboxServiceProvider extends ServiceProvider
    {
        /**
         * 执行注册后引导服务
         *
         * @return void
         */
        public function boot()
        {
            Storage::extend('dropbox', function ($app, $config) {
                $client = new DropboxClient(
                    $config['authorizationToken']
                );

                return new Filesystem(new DropboxAdapter($client));
            });
        }

        /**
         * 在容器中注册绑定
         *
         * @return void
         */
        public function register()
        {
            //
        }
    }

``extend`` 方法的第一个参数是驱动的名称，第二个参数是个闭包，接受 ``$app`` 和 ``$config`` 两个变量。这个闭包必须返回 ``League\Flysystem\Filesystem`` 的实例。 ``$config`` 变量包含了指定磁盘在 ``config/filesystems.php`` 中的配置。

注册好扩展后， 就可以通过配置 ``config/filesystems.php`` 使用 ``dropbox`` 了。

文件元信息缓存
==============
文件系统 ``I/O`` 很慢，因此 ``Flysystem`` 使用缓存的文件系统元数据来提高性能。

安装适配器缓存装饰器
--------------------
.. code-block:: shell

    composer require league/flysystem-cached-adapter

这个包提供了一个适配器装饰器，它充当缓存代理。 ``CachedAdapter`` （装饰器）缓存除文件内容之外的任何内容。这使缓存小到足以使其有益并涵盖所有文件系统检查操作。 `使用示例 <https://flysystem.thephpleague.com/docs/advanced/caching/>`_ 。

配置缓存
--------
使用flysystem系统的默认缓存驱动
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. code-block:: php

    <?php
    'disks' => [

        'local' => [
            'driver' => 'local',
            'root' => storage_path('app'),
            'cache' => true // 使用flysystem内置的缓存
        ],

    ],

使用应用全局缓存驱动
^^^^^^^^^^^^^^^^^^^^
首先应用全局缓存需要配置。

.. code-block:: php

    <?php
    'disks' => [

        'local' => [
            'driver' => 'local',
            'root' => storage_path('app'),
            'cache' => [
                'store' => env('CACHE_DRIVER', 'file'),
                'prefix' => 'flysystem',
                'expire' => '60' //单位是秒
            ]
        ],

    ],

