***********************
AsynchronousFileChannel
***********************

在 Java 7 中， ``AsynchronousFileChannel`` 已添加到 Java NIO 中。 ``AsynchronousFileChannel`` 可以异步读取数据并将数据写入文件。本教程将介绍如何使用 ``AsynchronousFileChannel`` 。

创建 AsynchronousFileChannel 实例
=================================
您可以通过静态方法 ``open()`` 创建 ``AsynchronousFileChannel`` 。以下是创建 ``AsynchronousFileChannel`` 的示例：

.. code-block:: java

	Path path = Paths.get("data/test.xml");

	AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);

``open()`` 方法的第一个参数是指向 ``AsynchronousFileChannel`` 要关联的文件的 ``Path`` 实例。

第二个参数是一个或多个方法选项，它们告诉 ``AsynchronousFileChannel`` 对底层文件执行哪些操作。在这个例子中，我们使用了 ``StandardOpenOption.READ`` ，这意味着该文件将被打开以供阅读。

读数据
======
您可以通过两种方式从 ``AsynchronousFileChannel`` 读取数据。每种读取数据的方法都会调用 ``AsynchronousFileChannel`` 的 ``read()`` 方法之一。以下各节将介绍这两种读取数据的方法。

通过Future对象读数据
-------------------
从 ``AsynchronousFileChannel`` 读取数据的第一种方法是调用返回 ``Future`` 的 ``read()`` 方法。以下是调用 ``read()`` 方法的方式：

.. code-block:: java

    Future<Integer> operation = fileChannel.read(buffer, 0);

此版本的 ``read()`` 方法将 ``ByteBuffer`` 作为第一个参数。从 ``AsynchronousFileChannel`` 读取的数据被读入此 ``ByteBuffer`` 。第二个参数是文件中要开始读取的字节位置。

即使读取操作尚未完成， ``read()`` 方法也会立即返回。您可以通过调用 ``read()`` 方法返回的 ``Future`` 实例的 ``isDone()`` 方法来检查读取操作何时完成。

这是一个更长的示例，展示了如何使用此版本的 ``read()`` 方法：

.. code-block:: java

	AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);

	ByteBuffer buffer = ByteBuffer.allocate(1024);
	long position = 0;

	Future<Integer> operation = fileChannel.read(buffer, position);

	while(!operation.isDone());

	buffer.flip();
	byte[] data = new byte[buffer.limit()];
	buffer.get(data);
	System.out.println(new String(data));
	buffer.clear();

此示例创建一个 ``AsynchronousFileChannel`` ，然后创建一个 ``ByteBuffer`` ，它作为参数传递给 ``read()`` 方法，并且位置为 ``0`` 。在调用 ``read()`` 之后，示例循环，直到返回的 ``Future`` 的 ``isDone()`` 方法返回 ``true`` 。当然，这不是一个非常有效使用 ``CPU`` 的方式 - 但你需要等到读操作完成。

一旦读取操作完成，数据将读入 ``ByteBuffer`` ，然后读入 ``String`` 并打印到 ``System.out`` 。

通过CompletionHandler对象读数据
------------------------------
从 ``AsynchronousFileChannel`` 读取数据的第二种方法是调用以 ``CompletionHandler`` 作为参数的 ``read()`` 方法版本。以下是调用此 ``read()`` 方法的方法：

.. code-block:: java

	fileChannel.read(buffer, position, buffer, new CompletionHandler<Integer, ByteBuffer>() {
	    @Override
	    public void completed(Integer result, ByteBuffer attachment) {
	        System.out.println("result = " + result);

	        attachment.flip();
	        byte[] data = new byte[attachment.limit()];
	        attachment.get(data);
	        System.out.println(new String(data));
	        attachment.clear();
	    }

	    @Override
	    public void failed(Throwable exc, ByteBuffer attachment) {

	    }
	});

一旦读取操作完成，将调用 ``CompletionHandler`` 的 ``completed()`` 方法。传递给 ``completed()`` 方法的参数传递一个 ``Integer`` ，告诉读取了多少字节，以及传递给 ``read()`` 方法的“attachment”。“attachment”是 ``read()`` 方法的第三个参数。在这种情况下，也是 ``ByteBuffer`` ，数据也被读入其中。您可以自由选择要附加的对象。

如果读取操作失败，则将调用 ``CompletionHandler`` 的 ``failed()`` 方法。

写数据
======
就像阅读一样，您可以通过两种方式将数据写入 ``AsynchronousFileChannel`` 。每种写入数据的方式都调用 ``AsynchronousFileChannel`` 的 ``write()`` 方法之一。以下各节将介绍这两种写入数据的方法。

通过Future对象写数据
-------------------
``AsynchronousFileChannel`` 还允许您异步写入数据。这是一个完整的 Java ``AsynchronousFileChannel`` 编写示例：

.. code-block:: java

	Path path = Paths.get("data/test-write.txt");
	AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);

	ByteBuffer buffer = ByteBuffer.allocate(1024);
	long position = 0;

	buffer.put("test data".getBytes());
	buffer.flip();

	Future<Integer> operation = fileChannel.write(buffer, position);
	buffer.clear();

	while(!operation.isDone());

	System.out.println("Write done");

首先，在写入模式下打开 ``AsynchronousFileChannel`` 。然后创建一个 ``ByteBuffer`` 并将一些数据写入其中。然后将 ``ByteBuffer`` 中的数据写入文件。最后，该示例检查返回的 ``Future`` 以查看写入操作何时完成。

请注意，在此代码起作用之前，该文件必须已存在。如果该文件不存在， ``write()`` 方法将抛出 ``java.nio.file.NoSuchFileException`` 。

您可以使用以下代码确保 ``Path`` 指向的文件存在：

.. code-block:: java

	if(!Files.exists(path)){
	    Files.createFile(path);
	}

通过CompletionHandler对象写数据
------------------------------
您还可以使用 ``CompletionHandler`` 将数据写入 ``AsynchronousFileChannel`` ，以告知您何时完成写入而不是 ``Future`` 。以下是使用 ``CompletionHandler`` 将数据写入 ``AsynchronousFileChannel`` 的示例：

.. code-block:: java

	Path path = Paths.get("data/test-write.txt");
	if(!Files.exists(path)){
	    Files.createFile(path);
	}
	AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);

	ByteBuffer buffer = ByteBuffer.allocate(1024);
	long position = 0;

	buffer.put("test data".getBytes());
	buffer.flip();

	fileChannel.write(buffer, position, buffer, new CompletionHandler<Integer, ByteBuffer>() {

	    @Override
	    public void completed(Integer result, ByteBuffer attachment) {
	        System.out.println("bytes written: " + result);
	    }

	    @Override
	    public void failed(Throwable exc, ByteBuffer attachment) {
	        System.out.println("Write failed");
	        exc.printStackTrace();
	    }
	});

当写操作完成时，将调用 ``CompletionHandler`` 的 ``completed()`` 方法。如果由于某种原因写入失败，则会调用 ``failed()`` 方法。

注意 ``ByteBuffer`` 如何用作附件 - 作为传递给 ``CompletionHandler`` 方法的对象。

















