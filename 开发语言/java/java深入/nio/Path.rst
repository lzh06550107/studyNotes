****
Path
****

Java ``Path`` 接口是Java NIO 2在Java 6和Java 7中收录的Java NIO 2更新的一部分。 Java ``Path`` 接口已添加到 Java 7 中的 Java NIO。 ``Path`` 接口位于 ``java.nio.file`` 包中，所以 Java ``Path`` 接口的完全限定名称是 ``java.nio.file.Path`` 。

Java ``Path`` 实例表示文件系统中的路径。路径可以指向文件或目录。路径可以是绝对路径或相对路径。绝对路径包含从文件系统根目录到其指向的文件或目录的完整路径。相对路径包含相对于某个其他路径的文件或目录的路径。相对路径可能听起来有点混乱。别担心。我将在本 Java NIO 路径教程的后面更详细地解释相对路径。

在某些操作系统中，请勿将文件系统路径与路径环境变量混淆。 ``java.nio.file.Path`` 接口与路径环境变量无关。

在许多方面， ``java.nio.file.Path`` 接口类似于 ``java.io.File`` 类，但存在一些细微差别。但在许多情况下，您可以使用 ``Path`` 接口替换 ``File`` 类的使用。

创建Path实例
============
要使用 ``java.nio.file.Path`` 实例，必须创建 ``Path`` 实例。您可以使用名为 ``Paths.get()`` 的 ``Paths`` 类（ ``java.nio.file.Paths`` ）中的静态方法创建 ``Path`` 实例。这是一个Java ``Paths.get()`` 示例：

.. code-block:: java

	Path path = Paths.get("c:\\data\\myfile.txt");

创建绝对路径Path实例
===================
通过使用绝对文件作为参数调用 ``Paths.get()`` 工厂方法来创建绝对路径。以下是创建表示绝对路径的 ``Path`` 实例的示例：

.. code-block:: java

    Path path = Paths.get("c:\\data\\myfile.txt");

绝对路径是 ``c:\data\myfile.txt`` 。在 Java 字符串中必须使用双 ``\`` 字符，因为 ``\`` 是转义字符，这意味着后面的字符表示字符串中此处的真正位置。通过编写 ``\\`` ，您告诉 Java 编译器将单个 ``\`` 字符写入字符串。

上述路径是 ``Windows`` 文件系统路径。在 ``Unix`` 系统（Linux，MacOS，FreeBSD等）上，上面的绝对路径可能如下所示：

.. code-block:: java

    Path path = Paths.get("/home/jakobjenkov/myfile.txt");

绝对路径现在是 ``/home/jakobjenkov/myfile.txt`` 。如果您在 ``Windows`` 计算机（以 ``/`` 开头的路径）上使用此类路径，则路径将被解释为相对于当前驱动器。例如，路径

.. code-block:: shell

    /home/jakobjenkov/myfile.txt

可以解释为位于C盘上。然后路径将对应于此完整路径：

.. code-block:: shell

    C:/home/jakobjenkov/myfile.txt

创建相对路径Path实例
===================
相对路径是指从一个路径（基本路径）指向目录或文件的路径。通过将基本路径与相对路径组合来导出相对路径的完整路径（绝对路径）。

Java NIO ``Path`` 类也可用于处理相对路径。您可以使用 ``Paths.get(basePath，relativePath)`` 方法创建相对路径。以下是 Java 中的两个相对路径示例：

.. code-block:: java

	Path projects = Paths.get("d:\\data", "projects");
	Path file     = Paths.get("d:\\data", "projects\\a-project\\myfile.txt");

第一个示例创建一个指向路径（目录） ``d:\data\projects`` 的 Java ``Path`` 实例。第二个示例创建一个 ``Path`` 实例，该实例指向路径（文件） ``d:\data\projects\a-project\myfile.txt`` 。

使用相对路径时，可以在路径字符串中使用两个特殊代码。这些代码是

- ``.`` ：表示当前目录。
- ``..`` ：表示上级目录。

如果你这样创建一个相对路径：

.. code-block:: java

	Path currentDir = Paths.get(".");
	System.out.println(currentDir.toAbsolutePath());

然后，Java ``Path`` 实例对应的绝对路径将是执行上述代码的应用程序执行的目录。如果。在路径字符串的中间使用，它只是指路径在该点指向的目录。下面是一个 ``Path`` 示例：

.. code-block:: java

    Path currentDir = Paths.get("d:\\data\\projects\.\a-project");

上面对应的路径是：

.. code-block:: shell

    d:\data\projects\a-project

.. code-block:: java

	String path = "d:\\data\\projects\\a-project\\..\\another-project";
	Path parentDir2 = Paths.get(path);

上面对应的路径是：

.. code-block:: java

    d:\data\projects\another-project

Path.normalize()
=================
``Path`` 接口的 ``normalize()`` 方法可以规范化路径。规范化意味着它会删除所有 ``.`` 和 ``..`` 代码在路径字符串的中间，并解析路径字符串引用的路径。这是一个Java ``Path.normalize()`` 示例：

.. code-block:: java

	String originalPath = "d:\\data\\projects\\a-project\\..\\another-project";

	Path path1 = Paths.get(originalPath);
	System.out.println("path1 = " + path1);

	Path path2 = path1.normalize();
	System.out.println("path2 = " + path2);

此 ``Path`` 示例首先创建一个中间带有 ``..`` 代码的路径字符串。然后，该示例从此路径字符串创建一个 ``Path`` 实例，并将该 ``Path`` 实例打印出来（实际上它打印出 ``Path.toString()`` ）。

然后，该示例在创建的 ``Path`` 实例上调用 ``normalize()`` ，该实例返回一个新的 ``Path`` 实例。然后还打印出这个新的标准化 ``Path`` 实例。

这是从上面的例子打印的输出：

.. code-block:: shell

	path1 = d:\data\projects\a-project\..\another-project
	path2 = d:\data\projects\another-project

如您所见，规范化路径不包含 ``a-project\..`` 部分，因为这是多余的。删除的部分不会为最终的绝对路径添加任何内容。

