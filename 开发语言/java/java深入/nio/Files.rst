*****
Files
*****

Java NIO ``Files`` 类（ ``java.nio.file.Files`` ）提供了几种操作文件系统中文件的方法。此 Java NIO ``Files`` 教程将介绍这些方法中最常用的方法。 ``Files`` 类包含许多方法，因此如果您需要一个此处未描述的方法，请检查JavaDoc。

``java.nio.file.Files`` 类通常和 ``java.nio.file.Path`` 实例联合使用，因此在使用 ``Files`` 类之前需要先了解 ``Path`` 类。

Files.exists()
===============
``Files.exists()`` 方法检查文件系统中是否存在给定的 ``Path`` 。

可以创建文件系统中不存在的 ``Path`` 实例。例如，如果您计划创建新目录，则首先要创建相应的 ``Path`` 实例，然后创建该目录。

由于 ``Path`` 实例可能指向或不指向文件系统中存在的路径，因此可以使用 ``Files.exists()`` 方法确定它们是否存在（如果需要检查）。

``Files.exists()`` 例子如下：

.. code-block:: java

	Path path = Paths.get("data/logging.properties");

	boolean pathExists = Files.exists(path, new LinkOption[]{ LinkOption.NOFOLLOW_LINKS});

此示例首先创建一个 ``Path`` 实例，指向我们要检查的路径是否存在。其次，该示例使用 ``Path`` 实例作为第一个参数调用 ``Files.exists()`` 方法。

注意 ``Files.exists()`` 方法的第二个参数。此参数是一个选项数组，它们影响 ``Files.exists()`` 如何确定路径是否存在。在上面的示例中，数组包含 ``LinkOption.NOFOLLOW_LINKS`` ，这意味着 ``Files.exists()`` 方法不应遵循文件系统中的符号链接来确定路径是否存在。

Files.createDirectory()
=======================
``Files.createDirectory()`` 方法从 ``Path`` 实例创建新目录。这是一个 Java ``Files.createDirectory()`` 示例：

.. code-block:: java

	Path path = Paths.get("data/subdir");

	try {
	    Path newDir = Files.createDirectory(path);
	} catch(FileAlreadyExistsException e){
	    // the directory already exists.
	} catch (IOException e) {
	    //something else went wrong
	    e.printStackTrace();
	}

第一行创建表示要创建的目录的 ``Path`` 实例。在 ``try-catch`` 块内部，使用 ``path`` 作为参数调用 ``Files.createDirectory()`` 方法。如果创建目录成功，则返回指向新创建的路径的 ``Path`` 实例。

如果该目录已存在，则抛出 ``java.nio.file.FileAlreadyExistsException`` 。如果出现其他问题，可能会抛出 ``IOException`` 。例如，如果所需的新目录的父目录不存在，则可能抛出 ``IOException`` 。父目录是您要在其中创建新目录的目录。因此，它表示新目录的父目录。

Files.copy()
============
``Files.copy()`` 方法将文件从一个路径复制到另一个路径。这是一个Java NIO ``Files.copy()`` 示例：

.. code-block:: java

	Path sourcePath      = Paths.get("data/logging.properties");
	Path destinationPath = Paths.get("data/logging-copy.properties");

	try {
	    Files.copy(sourcePath, destinationPath);
	} catch(FileAlreadyExistsException e) {
	    //destination file already exists
	} catch (IOException e) {
	    //something else went wrong
	    e.printStackTrace();
	}

首先，该示例创建源和目标 ``Path`` 实例。然后该示例调用 ``Files.copy()`` ，将两个 ``Path`` 实例作为参数传递。这将导致源路径引用的文件被复制到目标路径引用的文件。

如果目标文件已存在，则抛出 ``java.nio.file.FileAlreadyExistsException`` 。如果出现其他问题，将抛出 ``IOException`` 。例如，如果要复制文件的目录不存在，则抛出 ``IOException`` 。

覆盖现有文件
===========
可以强制 ``Files.copy()`` 覆盖现有文件。这是一个示例，说明如何使用 ``Files.copy()`` 覆盖现有文件：

.. code-block:: java

	Path sourcePath      = Paths.get("data/logging.properties");
	Path destinationPath = Paths.get("data/logging-copy.properties");

	try {
	    Files.copy(sourcePath, destinationPath,
	            StandardCopyOption.REPLACE_EXISTING);
	} catch(FileAlreadyExistsException e) {
	    //destination file already exists
	} catch (IOException e) {
	    //something else went wrong
	    e.printStackTrace();
	}

注意 ``Files.copy()`` 方法的第三个参数。如果目标文件已存在，则此参数指示 ``copy()`` 方法覆盖现有文件。

Files.move()
============
Java NIO ``Files`` 类还包含用于将文件从一个路径移动到另一个路径的功能。移动文件与重命名文件相同，除了移动文件可以将文件移动到不同的目录并在同一操作中更改其名称。是的， ``java.io.File`` 类也可以使用其 ``renameTo()`` 方法执行此操作，但现在您也可以在 ``java.nio.file.Files`` 类中使用文件移动功能。

``Files.move()`` 例子如下：

.. code-block:: java

	Path sourcePath      = Paths.get("data/logging-copy.properties");
	Path destinationPath = Paths.get("data/subdir/logging-moved.properties");

	try {
	    Files.move(sourcePath, destinationPath,
	            StandardCopyOption.REPLACE_EXISTING);
	} catch (IOException e) {
	    //moving file failed.
	    e.printStackTrace();
	}

首先创建源路径和目标路径。源路径指向要移动的文件，目标路径指向应将文件移动到的位置。然后调用 ``Files.move()`` 方法。这会导致文件被移动。

注意传递给 ``Files.move()`` 的第三个参数。此参数指示 ``Files.move()`` 方法覆盖目标路径上的任何现有文件。该参数实际上是可选的。

如果移动文件失败， ``Files.move()`` 方法可能会抛出 ``IOException`` 。例如，如果文件已存在于目标路径中，并且您遗漏了 ``StandardCopyOption.REPLACE_EXISTING`` 选项，或者要移动的文件不存在等。

Files.delete()
===============
``Files.delete()`` 方法可以删除文件或目录。这是一个Java ``Files.delete()`` 示例：

.. code-block:: java

	Path path = Paths.get("data/subdir/logging-moved.properties");

	try {
	    Files.delete(path);
	} catch (IOException e) {
	    //deleting file failed
	    e.printStackTrace();
	}

首先，创建指向要删除的文件的路径。其次，调用 ``Files.delete()`` 方法。如果 ``Files.delete()`` 由于某种原因（例如文件或目录不存在）无法删除文件，则抛出 ``IOException`` 。

Files.walkFileTree()
====================
``Files.walkFileTree()`` 方法包含用于递归遍历目录树的功能。 ``walkFileTree()`` 方法将 ``Path`` 实例和 ``FileVisitor`` 作为参数。 ``Path`` 实例指向要遍历的目录。在遍历期间调用 ``FileVisitor`` 。

在我解释遍历的工作原理之前，首先是 ``FileVisitor`` 接口：

.. code-block:: java

	public interface FileVisitor {

	    public FileVisitResult preVisitDirectory(
	        Path dir, BasicFileAttributes attrs) throws IOException;

	    public FileVisitResult visitFile(
	        Path file, BasicFileAttributes attrs) throws IOException;

	    public FileVisitResult visitFileFailed(
	        Path file, IOException exc) throws IOException;

	    public FileVisitResult postVisitDirectory(
	        Path dir, IOException exc) throws IOException {

	}

您必须自己实现 ``FileVisitor`` 接口，并将实现的实例传递给 ``walkFileTree()`` 方法。在目录遍历期间，将在不同时间调用 ``FileVisitor`` 实现的每个方法。如果您不需要挂钩所有这些方法，则可以扩展 ``SimpleFileVisitor`` 类，该类包含 ``FileVisitor`` 接口中所有方法的默认实现。

下面是 walkFileTree() 例子：

.. code-block:: java

	Files.walkFileTree(path, new FileVisitor<Path>() {
	  @Override
	  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
	    System.out.println("pre visit dir:" + dir);
	    return FileVisitResult.CONTINUE;
	  }

	  @Override
	  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	    System.out.println("visit file: " + file);
	    return FileVisitResult.CONTINUE;
	  }

	  @Override
	  public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
	    System.out.println("visit file failed: " + file);
	    return FileVisitResult.CONTINUE;
	  }

	  @Override
	  public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
	    System.out.println("post visit directory: " + dir);
	    return FileVisitResult.CONTINUE;
	  }
	});

在遍历期间，在不同的时间调用 ``FileVisitor`` 实现中的每个方法。

在访问任何目录之前调用 ``preVisitDirectory()`` 方法。访问目录后立即调用 ``postVisitDirectory()`` 方法。

在文件遍历期间访问的每个文件都会调用 ``visitFile()`` 方法。它不是为目录而调用的 - 只是文件。如果访问文件失败，则调用 ``visitFileFailed()`` 方法。例如，如果您没有正确的权限，或者出现其他问题。

四个方法中的每一个都返回一个 ``FileVisitResult`` 枚举实例。 ``FileVisitResult`` 枚举包含以下四个选项：

- CONTINUE
- TERMINATE
- SKIP_SIBLINGS
- SKIP_SUBTREE

通过返回其中一个值，被调用的方法可以决定文件遍历应该如何继续。

- ``CONTINUE`` 表示文件遍历应该正常继续。
- ``TERMINATE`` 表示文件遍历应立即终止。
- ``SKIP_SIBLINGS`` 意味着文件遍历应该继续但不访问此文件或目录的任何兄弟。
- ``SKIP_SUBTREE`` 表示文件遍历应该继续但不访问此目录中的条目。如果从 ``preVisitDirectory()`` 返回，则此值具有该功能。如果从任何其他方法返回，它将被解释为 ``CONTINUE`` 。

搜索文件
========
这是一个 ``walkFileTree()`` 例子 ，它扩展 ``SimpleFileVisitor`` 以查找名为 ``README.txt`` 的文件：

.. code-block:: java

	Path rootPath = Paths.get("data");
	String fileToFind = File.separator + "README.txt";

	try {
	  Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {

	    @Override
	    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	      String fileString = file.toAbsolutePath().toString();
	      //System.out.println("pathString = " + fileString);

	      if(fileString.endsWith(fileToFind)){
	        System.out.println("file found at path: " + file.toAbsolutePath());
	        return FileVisitResult.TERMINATE;
	      }
	      return FileVisitResult.CONTINUE;
	    }
	  });
	} catch(IOException e){
	    e.printStackTrace();
	}

递归删除目录
===========
``Files.walkFileTree()`` 也可用于删除包含其中所有文件和子目录的目录。 ``Files.delete()`` 方法只会删除目录为空的目录。通过遍历所有目录并删除每个目录中的所有文件（在 ``visitFile()`` 内部），然后删除目录本身（在 ``postVisitDirectory()`` 内），您可以删除包含所有子目录和文件的目录。这是一个递归目录删除示例：

.. code-block:: java

	Path rootPath = Paths.get("data/to-delete");

	try {
	  Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
	    @Override
	    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	      System.out.println("delete file: " + file.toString());
	      Files.delete(file);
	      return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
	      Files.delete(dir);
	      System.out.println("delete dir: " + dir.toString());
	      return FileVisitResult.CONTINUE;
	    }
	  });
	} catch(IOException e){
	  e.printStackTrace();
	}

文件类中的其他方法
=================
``java.nio.file.Files`` 类包含许多其他有用的函数，例如用于创建符号链接，确定文件大小，设置文件权限等的函数。有关 ``java.nio.file.Files`` 类的 JavaDoc ，请参阅 JavaDoc 以获取更多信息。这些方法。



