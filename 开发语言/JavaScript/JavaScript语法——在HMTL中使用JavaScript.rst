***********************
在HMTL中使用JavaScript
***********************

script元素
========
向HTML页面中插入JavaScript的主要方法，就是使用<script>元素。HTML4.01为<script>定义了下列6个属性。

- async：（异步下载脚本，下载完后立即同步执行）可选。表示应该立即下载脚本，但不妨碍页面中的其它操作，只对外部脚本有效。在XHTML中应该书写为async="async"。异步脚本一定先于页面的load事件执行，但可能会在DOMContentLoaded事件触发前或后执行。两个异步脚本之间执行无序。
- charset：可选。表示通过src属性指定的代码的字符集。由于大多数浏览器会忽略它的值，因此这个属性很少有人用。
- defer：（异步下载脚本，下载完后延迟同步执行）可选。表示脚本可以延迟到文档完全被解析和显示之后再执行。只对外部脚本有效。也就是说脚本可以被延迟到整个页面都解析完毕后再运行。设置该属性后会立即下载脚本但延迟执行。两个延迟脚本之间按定义顺序有序执行。
- language：已废弃。
- src：可选。表示包含要执行代码的外部文件。
- type：可选。可以看成是language的替代属性；表示编写代码使用的脚本语言的内容类型（也称为MIME类型）。不指定时，默认值为text/javascript。

使用<script>元素的方式有两种：直接在页面中嵌入JavaScript代码和包含外部JavaScript文件。

- 在使用<script>元素嵌入JavaScript代码时，只需为<script>指定type属性。然后，向下面这样把javaScript代码直接放在元素内部即可：

  .. code-block:: javascript

    <script type="text/javascript">
        function sayHi(){
            alert("Hi!");
        }
    </script>

  包含在<script>元素内部的JavaScript代码将被从上至下依次解释。就拿前面这个例子来说，解释器会解释一个函数的定义，然后将该定义保存在自己的环境当中。当解释器对<script>元素内部的所有代码求值完毕前，页面中的其余内容都不会被浏览器加载或显示。

  .. note:: 不要在中间包含有 ``</script>`` 字符串，可能会导致解析错误，可以使用 ``<\/script>`` 解决这一问题。

- 如果要通过<script>元素来包含外部JavaScript文件，那么src属性就是必需的。这个属性的值是一个指向外部javascript文件的链接，例如：

  .. code-block:: javascript

    <script type="text/javascript" src="example.js"></script>

  在这个例子中，外部文件example.js将被加载到当前页面中。外部文件只须包含通常要放在开始的<script>和结束的</script>中间的那些javascript代码即可。与解析嵌入式javascript代码一样，在解析外部javascript文件（包括下载该文件）时，页面的处理也会暂时停止。如果是在XHTML文档中，也可以省略前面示例代码中结束的</script>标签，例如：

  .. code-block:: javascript

    <script type="text/javascript" src="example.js" />

  .. note:: 按照惯例，外部javascript文件带有.js扩展名。但这个扩展名不是必需的，因为浏览器不会检查包含javascript的文件的扩展名。这样一来，使用JSP、PHP或其他服务器端语言动态生成javascript代码也就成为了可能。但是，服务器通常还是需要看扩展名决定为响应应用哪种MIME类型。如果不适用.js扩展名，请确保服务器能反应会正确的MIME类型。

总结：

- 在包含外部JavaScript文件时，必须将src属性设置为指向相应文件的URL。而这个文件既可以是当前域服务器中的文件，也可以是其他域服务器中的文件。
- 在不使用defer和async属性的情况下，所有<script>会按照他们在页面中出现的先后顺序依次被解析。只有解析完前面的<script>元素中的代码后，才会开始解析后面<script>元素中的代码。
- 由于浏览器会先解析前面不使用defer的<script>代码，然后再解析后面的内容，所以，应该把<script>元素放在最后，即</body>的前面。
- 使用defer可以让脚本在文档完全呈现之后再执行。延迟脚本总是按照指定他们的顺序执行。
- 使用async属性可以表示当前脚本不必等待其他脚本，也不必阻塞文档呈现。不能保证异步脚本按照它们在页面中的出现的顺序执行。

标签的位置
------------
安装惯例，所有<script>元素都应该放在页面的<head>元素中，例如：

.. code-block:: html

    <html>
        <head>
            <title>Example HTML Page</title>
            <script type="text/javascript" src="example1. js"></script>
            <script type="text/javascript" src="exarnple2. js"></script>
        </head>
        <body>
            <!-- 这里放置内容 -->
        </body>
    </html>

这种做法的目的就是把所有外部文件（包指CSS 文件和JavaScript 文件）的引用都放在相同的地方。可是，在文挡的<head>元素中包含所有JavaScript 文件，意味着必须等到全部JavaScript 代码都被下载、解析和执行完成以后，才能开始呈现页面的内容（浏览器在遇到<body>标签时才开始呈现内容） 。对于那些需要很多JavaScript 代码的页面来说，这无疑会导致浏览器在呈现页面时出现明显的延迟，而延迟期间的浏览器窗口中将是一片空白。为了避免这个问题，现代Web应用程序一般都把全部JavaScript引用放在<body>元索中，放在页面的内容后面。如下例所示：

.. code-block:: html

    <html>
        <head>
            <title>Example HTML Page</title>
        </head>
        <body>
            <!-- 这里放置内容 -->
            <script type="text/javascript" src="example1.js"></script>
            <script type="text/javascript" src="example2.js"></script>
        </body>
    </ html>

这样，在解析包含的JavaScript 代码之前，页面的内容将完全呈现在浏览器中。而用户也会因为浏览器窗口显示空白页面的时间缩短而感到打开页面的速度加快了。

延迟脚本
----------
HTML4.01为<script>标签定义了defer属性。这个属性的用途是表明脚本在执行时不会影响页面的构造。也就是说，脚本会被延迟到整个页面都解析完毕后再运行。因此，在<script>元素中设置defer属性，相当于告诉浏览器立即下载，但延迟执行。

.. code-block:: html

    <!DOCTYPE html>
    <html>
    <head>
        <title>Example HTML Page</title>
        <script type="text/javascript" defer="defer" src="example1.js"></script>
        <script type="text/javascript" defer="defer" src="example2.js"></script>
    </head>
    <body>
    <!— 这里放内容 —>
    </body>
    </html>

在这个例子中，虽然我们把 <script> 元素放在了文档的 <head> 元素中，但其中包含的脚本将延迟到浏览器遇到 </html> 标签后再执行。HTML5规范要求脚本按照它们出现的先后顺序执行，因此第一个延迟脚本会先于第二个延迟脚本执行，而这两个脚本会先于 ``DOMContentLoaded`` 事件执行。在现实当中，延迟脚本并不一定会按照顺序执行，也不一定会在 ``DOMContentLoaded`` 事件触发前执行，因此最好只包含一个延迟脚本。

前面提到过， defer 属性只适用于外部脚本文件。这一点在HTML5中已经明确规定，因此支持HTML5的实现会忽略给嵌入脚本设置的 defer 属性。IE4～IE7还支持
对嵌入脚本的 defer 属性，但IE8及之后版本则完全支持HTML5规定的行为。

IE4、Firefox 3.5、Safari 5和Chrome是最早支持 defer 属性的浏览器。其他浏览器会忽略这个属性，像平常一样处理脚本。为此，把延迟脚本放在页面底部仍然是最佳选择。

.. note:: 在XHTML文档中，要把 defer 属性设置为 defer=“defer” 。

异步脚本
----------
HTML5为<script>元素定义了async属性。这个属性与defer属性类似，都用于改变处理脚本的行为。同样与defer类似，async只适用于外部脚本文件，并告诉浏览器立即下载文件。但与defer不同的是，标记为async的脚本并不保证按照指定它们的先后顺序执行。

.. code-block:: html

    <!DOCTYPE html>
    <html>
        <head>
            <title>HTML js应用</title>
            <meta http-equiv="Content-type" content="text/html; charset=GBK"></meta>
            <script type="text/javascript" async src="example1.js"></script>
            <script type="text/javascript" async src="example2.js"></script>
        </head>
        
        <body>
            <div>
                <h1>好好学习，天天向上</h1>
            </div>
        </body>
    </html>

在以上代码中，第二个脚本文件可能会在第一个脚本文件之前执行。因此，确保两者之间互不依赖非常重要。指定async属性的目的是不让页面等待两个脚本下载。

.. note:: 在XHTML文档中，要把async属性设置为async="async"。

嵌入代码与外部文件
==================
引用外部文件的好处：

- 可维护性：可以在不触及HTML的情况下，集中精力编辑JavaScript代码。
- 可缓存：如果有多个页面都需要使用同一个文件，那么这个文件只需要下载一次。加快页面加载的速度。
- 适应未来：无需使用前面提到的注释hack。

noscript元素
==========
<noscript>元素：可以解决浏览器不支持JavaScript时，页面平稳地退化。

当出现以下情况之一时<noscript>中的内容才会呈现出来，否则永远也不会显示：

- 浏览器不支持JavaScript。
- 浏览器支持脚本，但脚本被禁用。

符合上述任何一个条件，浏览器都会显示<noscript>中的内容。
