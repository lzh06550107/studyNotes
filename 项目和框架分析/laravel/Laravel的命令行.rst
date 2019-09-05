======
命令行
======

简介
====
``Artisan`` 是 Laravel 自带的命令行接口，它提供了许多实用的命令来帮助你构建 Laravel 应用。要查看所有可用的 ``Artisan`` 命令的列表，可以使用 ``list`` 命令，该命令位于 ``symfony\console\Command\ListCommand.php`` 文件：

.. code-block:: shell

    php artisan list

每个命令包含了「帮助」界面，它会显示并概述命令的可用参数及选项。只需要在命令前面加上 ``help`` 即可查看命令帮助界面，该命令位于 ``Symfony\Component\Console\Command\HelpCommand`` 文件：

.. code-block:: shell

    php artisan help migrate
    #或者
    php artisan migrate -h

Laravel REPL
-------------
所有 Laravel 应用都包含了 ``Tinker`` ，一个基于 `PsySH <https://github.com/bobthecow/psysh>`_ 包提供支持的 ``REPL`` 。 ``Tinker`` 让你可以在命令行中与你整个的 Laravel 应用进行交互，包括 ``Eloquent ORM`` 、任务、事件等等。运行 ``Artisan`` 命令 ``tinker`` 进入 ``Tinker`` 环境：

.. code-block:: shell

    php artisan tinker

编写命令
========
除 ``Artisan`` 提供的命令外，您还可以构建自己的自定义命令。 命令通常存储在 ``app/Console/Commands`` 目录中；不过，只要您的命令可以由 Composer 加载，您就可以自由选择自己的存储位置。


生成命令
--------
要创建一个新的命令，可以使用 ``Artisan`` 命令 ``make:command`` 。这个命令会在 ``app/Console/Commands`` 目录中创建一个新的命令类。 不必担心应用中不存在这个目录，因为它会在你第一次运行 ``Artisan`` 命令 ``make:command`` 时创建。生成的命令会包括所有命令中默认存在的属性和方法：

.. code-block:: shell

    php artisan make:command SendEmails

命令结构
--------
命令生成后，应先填写类的 ``signature`` 和 ``description`` 属性，这会在使用 ``list`` 命令的时候显示出来。执行命令时会调用 ``handle`` 方法，你可以在这个方法中放置命令逻辑。

.. note:: 为了更好的代码复用，最好保持你的控制台代码轻量并让它们延迟到应用服务中完成。在下面的例子中，请注意，我们注入了一个服务类来完成发送邮件的。

让我们看一个简单的例子。注意，我们可以在 ``Command`` 的构造函数中注入我们需要的任何依赖项。Laravel 服务容器 将会自动注入所有在构造函数中的带类型约束的依赖：

.. code-block:: php

    <?php

    namespace App\Console\Commands;

    use App\User;
    use App\DripEmailer;
    use Illuminate\Console\Command;

    class SendEmails extends Command
    {
        /**
         * 控制台命令 signature 的名称。
         *
         * @var string
         */
        protected $signature = 'email:send {user}';

        /**
         * 控制台命令说明。
         *
         * @var string
         */
        protected $description = 'Send drip e-mails to a user';

        /**
         * 邮件服务的 drip 属性。
         *
         * @var DripEmailer
         */
        protected $drip;

        /**
         * 创建一个新的命令实例。
         *
         * @param  DripEmailer  $drip
         * @return void
         */
        public function __construct(DripEmailer $drip)
        {
            parent::__construct();

            $this->drip = $drip;
        }

        /**
         * 执行控制台命令。
         *
         * @return mixed
         */
        public function handle()
        {
            $this->drip->send(User::find($this->argument('user')));
        }
    }

闭包命令
--------
基于闭包的命令提供一个用类替代定义控制台命令的方法。同样的，路由闭包是控制器的一种替代方法，而命令闭包可以视为命令类的替代方法。在 ``app/Console/Kernel.php`` 文件的 ``commands`` 方法中， Laravel 加载了 ``routes/console.php`` 文件：

.. code-block:: php

    <?php
    /**
     * 注册应用的基于闭包的命令。
     *
     * @return void
     */
    protected function commands()
    {
        require base_path('routes/console.php');
    }

虽然这个文件没有定义 ``HTTP`` 路由，它也将基于控制台的入口点（路由）定义到应用中。在这个文件中，你可以使用 ``Artisan::command`` 方法定义所有基于闭包的路由。 ``command`` 方法接收两个参数：命令签名 和一个接收命令参数和选项的闭包：

.. code-block:: php

    <?php
    Artisan::command('build {project}', function ($project) {
        $this->info("Building {$project}!");
    });

闭包绑定底层的命令实例，因此你可以完全访问通常可以在完整命令类中访问的所有辅助方法。

类型提示依赖
^^^^^^^^^^^^
除了接收命令的参数和选项外，命令闭包也可以使用类型提示从 服务容器 中解析你想要的其他依赖关系：

.. code-block:: php

    <?php
    use App\User;
    use App\DripEmailer;

    Artisan::command('email:send {user}', function (DripEmailer $drip, $user) {
        $drip->send(User::find($user));
    });

闭包命令描述
^^^^^^^^^^^^
当基于命令定义一个闭包的时候，你应当使用 ``describe`` 方法来给命令添加一个描述。这个描述会在你执行 ``php artisan list`` 命令或 ``php artisan help`` 命令时显示。

.. code-block:: php

    <?php
    Artisan::command('build {project}', function ($project) {
        $this->info("Building {$project}!");
    })->describe('Build the project');

定义输入期望
============
在编写控制台命令时，通常是通过参数和选项来收集用户输入。Laravel 可以非常方便地在你的命令里用 ``signature`` 属性来定义你期望用户输入的内容。 ``signature`` 属性允许你使用单一且可读性非常高的、类似路由的语法定义命令的名称、参数和选项。

参数
----
所有用户提供的参数及选项都被包含在花括号中。在下面的例子中，这个命令定义了一个必须的参数： ``user``

.. code-block:: php

    <?php
    /**
     * 控制台命令 signature 的名称。
     *
     * @var string
     */
    protected $signature = 'email:send {user}';

你也可以创建可选参数，并定义参数的默认值：

.. code-block:: php

    <?php
    // 可选参数...
    email:send {user?}

    // 带有默认值的可选参数...
    email:send {user=foo}

选项
----
选项，如参数，是用户输入的另一种格式。当命令行指定选项时，它们以两个连字符( ``--`` )作为前缀。有两种类型的选项：接收值和不接收值。不接收值的选项作为布尔值的「开关」。让我们看一下这种类型的选项的例子：

.. code-block:: php

    <?php
    /**
     * 控制台命令 `signature`　的名称。 
     *
     * @var string
     */
    protected $signature = 'email:send {user} {--queue}';

在这个例子中，可以在调用 ``Artisan`` 时指定 ``--queue`` 开关。如果 ``--queue`` 开关被传递， 该选项的值为 ``true`` ，否则为 ``false`` ：

.. code-block:: shell

    php artisan email:send 1 --queue

带值的选项
^^^^^^^^^^
接下来，我们来看一个带值的选项。如果用户必须为选项指定一个值，需要用一个等号 ``=`` 作为选项名称的后缀：

.. code-block:: php

    <?php
    /**
     * 控制台命令 signature 的名称。
     *
     * @var string
     */
    protected $signature = 'email:send {user} {--queue=}';

在这个例子中， 用户可以传递该选项的值，如下所示：

.. code-block:: shell

    php artisan email:send 1 --queue=default

你可以通过在选项名称后面指定默认值来设定选项的默认值。如果用户没有传递选项值，将使用设定的默认值：

.. code-block:: shell

    email:send {user} {--queue=default}

选项简写
^^^^^^^^
要在定义选项时指定简写，你可以在选项名称前指定它，并且使用 ``|`` 分隔符将简写与完整选项名称分隔开：

.. code-block:: shell

    email:send {user} {--Q|queue}

输入数组
--------
如果你想定义参数或选项你可以使用 ``*`` 符号来期望输入数组。首先，我们先看一个指定数组参数的实例：

.. code-block:: shell

    email:send {user*}

调用此方法时，可以传递 ``user`` 参数给命令行。例如，以下命令会设置 ``user`` 的值为 ``['foo', 'bar']`` ：

.. code-block:: shell

    php artisan email:send foo bar

在定义期望数组输入的选项时，传递给命令的每个选项值都应以选项名称为前缀：

.. code-block:: shell

    email:send {user} {--id=*}

    php artisan email:send --id=1 --id=2

输入说明
--------
你可以通过冒号为参数和选项添加说明。如果你需要一点额外的空间来定义你的命令，可以随意分开在多个行里：

.. code-block:: php

    <?php
    /**
     * 控制台命令和 signature 属性
     *
     * @var string
     */
    protected $signature = 'email:send
                            {user : The ID of the user}
                            {--queue= : Whether the job should be queued}';

I/O 命令
========
检索输入
--------
在执行命令时，显然你需要访问命令的参数和选项的值。此时，你可以使用 ``argument`` 和 ``option`` 方法：

.. code-block:: php

    <?php
    /**
     * 执行控制台命令
     *
     * @return mixed
     */
    public function handle()
    {
        $userId = $this->argument('user');

        //
    }

如果你需要将所有参数以 ``array`` 检索，可以调用 ``arguments`` 方法：

.. code-block:: php

    <?php
    $arguments = $this->arguments();

``option`` 方法可以非常容易的检索参数选项。要将所有选项作为 ``array`` 检索，可以使用 ``options`` 方法：

.. code-block:: php

    <?php
    // 检索一个特定的选项...
    $queueName = $this->option('queue');

    // 检索所有选项...
    $options = $this->options();

如果参数或选项不存在，则返回 ``null`` 。

交互式输入
----------
除了显示输出外，你还可以要求用户在执行命令时提供输入。 ``ask`` 方法将提示用户给定问题，接收他们的输入，然后将用户的输入返回到你的命令：

.. code-block:: php

    <?php
    /**
     * 执行控制台命令。
     *
     * @return mixed
     */
    public function handle()
    {
        $name = $this->ask('What is your name?');
    }

``secret`` 方法和 ``ask`` 方法类似，但用户输入的内容在他们输入控制台时是不可见的。这个方法适用于需要用户输入像密码这样的敏感信息的情况：

.. code-block:: php

    <?php
    $password = $this->secret('What is the password?');

请求确认
^^^^^^^^
如果你要用户提供一些简单的确认信息，你可以使用 ``confirm`` 方法。默认情况下，该方法将返回 ``false`` 。但是，如果用户根据提示输入 ``y`` 或者 ``yes`` 则会返回 ``true`` 。

.. code-block:: php

    <?php
    if ($this->confirm('Do you wish to continue?')) {
        //
    }

自动补全
^^^^^^^^
anticipate 方法可用于为可能的选择提供自动补全功能。不管提示的内容是什么，用户仍然可以选择任何回答：

.. code-block:: php

    <?php
    $name = $this->anticipate('What is your name?', ['Taylor', 'Dayle']);

多重选择
^^^^^^^^
如果你要给用户提供预定义的一组选择，可以使用 ``choice`` 方法。如果用户未选择任何选项，你可以返回设置的默认值：

.. code-block:: php

    <?php
    $name = $this->choice('What is your name?', ['Taylor', 'Dayle'], $defaultIndex);

编写输出
^^^^^^^^
可以使用 ``line`` 、 ``info``  、 ``comment`` 、 ``question`` 和 ``error`` 方法来将输出发送到终端。每个方法都有适当的 ``ANSI`` 颜色来作为表明其目的。例如，如果我们要向用户展示普通信息，通常来说，最好使用 ``info`` 方法，它会在控制台将输出的内容显示为绿色：

.. code-block:: php

    <?php
    /**
     * 执行控制台命令。
     *
     * @return mixed
     */
    public function handle()
    {
        $this->info('Display this on the screen');
    }

显示错误信息， 使用 ``error`` 方法。 错误信息通常显示为红色：

.. code-block:: php

    <?php
    $this->error('Something went wrong!');

如果你想在控制台显示无色的输出，请使用 ``line`` 方法：

.. code-block:: php

    <?php
    $this->line('Display this on the screen');

表格布局
^^^^^^^^^
对于多行列数据的格式化输出， ``table`` 方法处理起来更轻松。基于传入的表头和行数据，它会动态计算宽高：

.. code-block:: php

    <?php
    $headers = ['Name', 'Email'];

    $users = App\User::all(['name', 'email'])->toArray();

    $this->table($headers, $users);

进度条
^^^^^^
对于耗时任务，提示进度非常有必要。使用 ``output`` 对象就可以创建、加载以及停止进度条。首先，定义好任务总步数，然后，在每次任务执行时加载进度条：

.. code-block:: php

    <?php
    $users = App\User::all();

    $bar = $this->output->createProgressBar(count($users));

    foreach ($users as $user) {
        $this->performTask($user);

        $bar->advance();
    }

    $bar->finish();

参阅 `Symfony 进度条组件文档 <https://symfony.com/doc/2.7/components/console/helpers/progressbar.html>`_ 获取更多高级用法。

注册命令
========
``app/Console/Commands`` 目录下的命令都会被注册，这是由于控制台内核的 ``commands`` 方法调用了 ``load`` 。实际上，可随意调用 ``load`` 来扫描其他目录下的 ``Artisan`` 命令：

.. code-block:: php

    <?php
    /**
     * 注册应用的命令
     *
     * @return void
     */
    protected function commands()
    {
        $this->load(__DIR__.'/Commands');
        $this->load(__DIR__.'/MoreCommands');

        // ...
    }

也可以在 ``app/Console/Kernel.php`` 文件的 ``$commands`` 属性中手动注册命令的类名。 ``Artisan`` 启动时，这个属性列出的命令都将由 服务容器 解析并通过 ``Artisan`` 进行注册：

.. code-block:: php

    <?php
    protected $commands = [
        Commands\SendEmails::class
    ];

程序调用命令
============
有时需要在 ``CLI`` 之外执行 ``Artisan`` 命令，例如，在路由或控制器里触发 ``Artisan`` 命令。要实现调用，可以使用 ``Artisan`` 门面的 ``call`` 方法。 ``call`` 方法的第一个参数接受命令名，第二个参数接受数组形式的命令参数。退出码将返回：

.. code-block:: php

    <?php
    Route::get('/foo', function () {
        $exitCode = Artisan::call('email:send', [
            'user' => 1, '--queue' => 'default'
        ]);

        //
    });

``Artisan`` 门面的 ``queue`` 方法可以将 ``Artisan`` 命令队列化，交由 队列工作进程 进行后台处理。使用此方法之前，务必配置好队列以及运行队列监听器：

可以指定 ``Artisan`` 命令派发的连接或任务：

.. code-block:: php

    <?php
    Artisan::queue('email:send', [
        'user' => 1, '--queue' => 'default'
    ])->onConnection('redis')->onQueue('commands');

传递数组值
----------
如果定义了接受数组的选项，可以直接传递数组到该选项：

.. code-block:: php

    <?php
    Route::get('/foo', function () {
        $exitCode = Artisan::call('email:send', [
            'user' => 1, '--id' => [5, 13]
        ]);
    });

传递布尔值
----------
需要指定没有选项值的选项时，例如， ``migrate:refresh`` 命令的 ``--force`` 选项，就可以传入 ``true`` 或 ``false`` ：

.. code-block:: php

    <?php
    $exitCode = Artisan::call('migrate:refresh', [
        '--force' => true,
    ]);

命令的互相调用
--------------
``call`` 方法可以实现调用其它 ``Artisan`` 命令。 ``call`` 方法接受命令名和数组形式的选项：

.. code-block:: php

    <?php
    /**
     * 执行控制台命令
     *
     * @return mixed
     */
    public function handle()
    {
        $this->call('email:send', [
            'user' => 1, '--queue' => 'default'
        ]);

        //
    }

如果要抑制控制台命令的所有输出，可以使用 ``callSilent`` 方法。 ``callSilent`` 的使用方法同 ``call`` ：

.. code-block:: php

    <?php
    $this->callSilent('email:send', [
        'user' => 1, '--queue' => 'default'
    ]);
