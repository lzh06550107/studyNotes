==
流
==
Java8引入的改变是为了帮助我们更好的写代码。新的核心库是关键，本章节将了解它们。最重要的核心库的改变是Collection API和新增的streams。流允许我们在一个更高的抽象层次书写集合处理代码。

流接口包含一系列函数，每一个对应你通常在集合上的操作。

Java开发者集合中最常用的方法就是迭代一个集合，然后轮流操作每一个元素。例如，如果我们想要计算来自伦敦的音乐家人数。

.. code-block:: java

	int count = 0;
	for (Artist artist : allArtists) {
		if (artist.isFrom("London")) {
			count++;
		}
	}

但是这种方法有几个问题。它涉及很多样板，即每次要遍历集合时需要编写的代码。这也是很难写一个这个for循环的并行版本。你需要重写每一个以使它们并行运行。

最后，这里的代码不能流利地传达程序员的意图。样板循环结构遮盖意义;为了理解我们必须阅读循环体。对于一个单循环，这样做不是太糟糕，当你有一个很大的代码库，它们成为一个负担（尤其是嵌套的循环）。

for循环实际上是包含的句法糖并隐藏迭代。这是值得花一点时间看底层发生了什么。这个过程的第一步是调用迭代器方法，其中创建一个新的Iterator对象，以控制迭代过程。我们称之为外部迭代。然后，迭代通过显式调用hasNext和next来处理。示例3-2演示了完整的扩展代码。图3-1显示了发生的方法调用的模式。

.. code-block:: java

	int count = 0;
	Iterator<Artist> iterator = allArtists.iterator();
	while(iterator.hasNext()) {
		Artist artist = iterator.next();
		if (artist.isFrom("London")) {
			count++;
		}
	}

外部迭代也有一些负面的问题。首先变得很难抽象出本章稍后将会遇到的不同行为操作。这里是使用for循环来解决你正在做的事情。

另一种方法,内部迭代。第一件注意的事是对stream（）的调用，它调用在前面的例子中类似iterator（）的角色。 而不是返回一个迭代器来控制迭代而是返回内部迭代世界中的等效接口：Stream。

.. code-block:: java

	long count = allArtists.stream().filter(artist -> artist.isFrom("London")).count();

Stream是使用函数方法构建集合复杂操作的工具。

我们可以实际分解该例子为两个简单的操作：

	- 查找所有来自伦敦的艺术家；
	- 计算列表中艺术家个数；

这两个操作都对应于Stream接口上的一个方法。为了找从伦敦来的艺术家，我们过滤了流。在这种情况下，过滤是指“只保留”通过测试的对象“。测试由函数定义，返回true或假，取决于艺术家是否来自伦敦。 该count（）方法计算给定流中的对象数量。

实际上做了什么
==============
在Java中，当您调用方法时，它实际上对应于计算机做某事。例如System.out.println（“Hello World”）; 打印输出到你的终端 Stream上的一些方法有所不同。他们是普通的Java方法，但是返回的Stream对象不是一个新的集合 - 这是一个用于创建新集合的配方。所以想下下面代码的结果：

.. code-block:: java

	allArtists.stream().filter(artist -> artist.isFrom("London"));

它实际上没有做什么，只是调用过滤器构建一个流的菜谱，但是强制使用该菜谱。方法如过滤器建立流配方，但不要强制在最后生成的新值被称为
懒加载。从Stream序列生成最终值的计数方法叫立即加载。

如果我们在过滤器中增加一个打印艺术家的名称的语句。如果运行该代码，当执行时，程序不会打印任何东西。

.. code-block:: java

	allArtists.stream().filter(artist -> {
		System.out.println(artist.getName());
		return artist.isFrom("London");
	});

如果我们在最后一步增加同样的打印语句，我们将会在终端看到输出。

.. code-block:: java

	long count = allArtists.stream().filter(artist -> {
		System.out.println(artist.getName());
		return artist.isFrom("London");
	}).count();

很容易判断一个操作是否是直接操作还是懒操作：查看它返回什么。如果返回一个流，则是懒操作；如果返回另一个值或者不返回值，则是直接操作。通过链接所有懒操作形成一个序列，最后通过一个直接操作来生成你的结果。

整个方法稍微有点类似构建器模式。在构建器模式中，存在系列设置属性或者配置，最后一个单独的调用来构建方法。被创建的对象直到调用发生时才被创建。这意味着我们可以把所有对集合的操作一次迭代完成。

一般的流操作
============
在这一点上，值得回顾一下常见的Stream操作以获得更多的API。因为我们只会覆盖几个重要的例子，我建议看看Javadoc的新API来看看其他可用的函数。

collect(toList())
-----------------
该函数是一个直接操作函数用来从一个流中生成一个列表。

.. code-block:: java

	List<String> collected = Stream.of("a", "b", "c").collect(Collectors.toList());
	assertEquals(Arrays.asList("a", "b", "c"), collected);

注意：因为有很多流函数式懒操作的，你必须在集合操作链的结尾使用一个直接操作函数。

map
---
如果您需要一个将一种类型的值转换为另一种类型的函数，则map可以将此函数应用于值的流，生成另一种新值的流。

你可能会很快注意到多年来你一直在做某种映射操作。假设你正在编写一个把字符串序列转换为大写的Java代码。您将循环列表中的所有值
调用每个元素上的Uppercase。然后，您将会将每个结果值添加到一个新的列表。

.. code-block:: java

	List<String> collected = new ArrayList<>();
	for (String string : asList("a", "b", "hello")) {
		String uppercaseString = string.toUpperCase();
		collected.add(uppercaseString);
	}
	assertEquals(asList("A", "B", "HELLO"), collected);

map是最常用的流操作。上面的等价操作如下：

.. code-block:: java

	List<String> collected = Stream.of("a", "b", "hello").map(string -> string.toUpperCase()).collect(toList());
	assertEquals(asList("A", "B", "HELLO"), collected);

传递给map的函数是一个只能接收一个参数的泛型函数，接收的参数类型和返回类型可以不同。

filter
------
任何时候你通过循环一些数据并检查每个元素，您可能想要考虑使用流新的过滤器方法。

.. code-block:: java

	List<String> beginningWithNumbers = new ArrayList<>();
	for(String value : asList("a", "1abc", "abc1")) {
		//查找以数字开头的字符串
		if (isDigit(value.charAt(0))) {
			beginningWithNumbers.add(value);
		}
	}

	List<String> beginningWithNumbers = Stream.of("a", "1abc", "abc1").filter(value -> isDigit(value.charAt(0))).collect(toList());

传递给filter的函数只能接收一个参数的泛型函数，接收任意类型的参数，返回boolean类型的值。

flatMap
-------
让你使用一个流来取代一个值，然后合并所有的流。如何把值转换为流，如，假设流是一个包含列表的流，则获取流中的列表，然后通过列表来获取流，这样就把流内的值替换为一个流，然后合并所有的流，就可以获取列表中所有的值。

.. code-block:: java

	List<Integer> together = Stream.of(asList(1, 2), asList(3, 4)).flatMap(numbers -> numbers.stream()).collect(toList());

传递给flatMap的函数和Map的一样，只是该函数的返回类型限制为流，不能为其它的值。

max和min
--------

.. code-block:: java

	List<Track> tracks = asList(new Track("Bakai", 524),
	new Track("Violets for Your Furs", 378),
	new Track("Time Was", 451));
	Track shortestTrack = tracks.get(0);
	for (Track track : tracks) {
		if (track.getLength() < shortestTrack.getLength()) {
			shortestTrack = track;
		}
	}

    Track shortestTrack = tracks.stream().min(Comparator.comparing(track -> track.getLength())).get();

当我们比较元素的最大最小值时，我们首先应该考虑将要使用的顺序。当我们查找最短的歌名时，通过歌曲的长度来提供排序。为了通知流我们使用歌曲的长度，我们给它一个比较器。

.. code-block:: java

	Object accumulator = initialValue;
	for(Object element : collection) {
		accumulator = combine(accumulator, element);
	}

一个累加器被放置在循环体内，最后一个累加器的值是我们尝试计算的值。累加器开始值为initialValue，然后通过调用combine把列表的每个元素折叠在一起。

在这种模式的实现之间有所不同的是initialValue和combine函数。在原始示例中，我们使用列表中的第一个元素作为我们的初始值，但不一定是。为了找到最短的值，我们的combie返回当前元素和累加器之间的歌曲名称较短者。

现在我们来看一下这个通用模式如何通过一个流API进行操作。

reduce
------
当你有一个集合，想要生成单个值时，使用聚合操作。前面我们使用count，min和max方法，他们都是聚合操作。

.. code-block:: java

	int count = Stream.of(1, 2, 3).reduce(0, (acc, element) -> acc + element);

组合所有的方法
==============
有了与Stream接口相关的许多不同的操作，有时候寻找你想要的东西还是迷惑。所以让我们通过一个问题一起工作，看看它如何分解成简单的Stream操作。

我们解决的第一个问题是，对于一张专辑，找到每个乐队的国籍
在该专辑上播放每个曲目的艺术家可以是独奏艺术家，也可以是一个乐队。我们假设一个名字以“The”开头的乐队。

首先要注意的是，解决方案不仅仅是任何的简单应用单独的API调用。 它不是像map一样转换值，它不是过滤,也不只是从一个流中得到一个单一的值。我们可以把这个问题分成几部分：

1. 获取一个唱片所有艺术家；
2. 确定乐队；
3. 查找每个乐队的国籍；
4. 把这些值放到一个集合中；

现在，我们很容易填充API：

1. getMusicians方法返回一个包含艺术家的流；
2. 我们使用过滤器获取只是brand的艺术家；
3. 我们使用map来把艺术家转换为国籍；
4. 我们使用collect(toList())把国籍放到一个列表中；

.. code-block:: java

	Set<String> origins = album.getMusicians().filter(artist -> artist.getName().startsWith("The")).map(artist -> artist.getNationality()).collect(toSet());

现在想想是否想要在你的域模型中暴露List和Set对象。也许流工厂将会是一个更好的选择。通过Stream来暴露集合能够更好的封装你的域模型。通过暴露一个Stream，不可能通过使用你的域类来影响你内部工作的List或者Set。

重构遗留代码
============
下面的用来查找歌曲长度超过1分钟的遗留：

.. code-block:: java

	public Set<String> findLongTracks(List<Album> albums) {
		Set<String> trackNames = new HashSet<>();
		for(Album album : albums) {
			for (Track track : album.getTrackList()) {
				if (track.getLength() > 60) {
					String name = track.getName();
					trackNames.add(name);
				}
			}
		}
		return trackNames;
	}

我们要改变的第一件事就是for循环。我们会保留现有的Java编码风格主体，并在Stream上使用forEach方法。这对于中间重构步骤来说可能是一个非常方便的技巧。我们在唱片列表中使用流方法，以获得第一个流。

.. code-block:: java

	public Set<String> findLongTracks(List<Album> albums) {
		Set<String> trackNames = new HashSet<>();
		albums.stream().forEach(album -> {
			album.getTracks().forEach(track -> {
				if (track.getLength() > 60) {
					String name = track.getName();
					trackNames.add(name);
				}
			});
		});
		return trackNames;
	}
	
我们虽然使用流，但是没有发掘它的潜能。那么现在是时候了
我们在我们的编码中引入了更多的流风格。内部forEach调用看起来像
精炼的主要目标。

我们在这里真的做了三件事情：查找歌曲长度超过1分钟的，得到
歌曲的名字，并将歌曲的名字添加到名字集。这意味着我们需要调用三个流操作以完成工作。找到符合标准的曲目听起来像一个过滤器的工作。将曲目转换成歌曲的名字是map的用法。目前我们还要添加歌曲名称到我们的集合，所以我们的终端操作仍将是一个forEach块。

.. code-block:: java

	public Set<String> findLongTracks(List<Album> albums) {
		Set<String> trackNames = new HashSet<>();
		albums.stream().forEach(album -> {
			album.getTracks().filter(track -> track.getLength() > 60)
			.map(track -> track.getName())
			.forEach(name -> trackNames.add(name));
		});
		return trackNames;
	}

现在我们已经用更多的流函数替代了我们的内部循环，但在我们的代码中仍然有问题。我们真的不想有嵌套流操作。

我们需要一个简单而干净的方法调用顺序。我们真正想要做的是找到一种将我们的唱片变成歌曲流的方式。我们知道，只要我们想要转换或替换代码，map操作就可以了。这是比map更为复杂的情况，flatMap对于其输出值也是一个Stream且我们希望它们合并在一起。所以，如果我们替换forEach块，则使用flatMap调用。

.. code-block:: java

	public Set<String> findLongTracks(List<Album> albums) {
		Set<String> trackNames = new HashSet<>();
		albums.stream().flatMap(album -> album.getTracks())
		.filter(track -> track.getLength() > 60)
		.map(track -> track.getName())
		.forEach(name -> trackNames.add(name));
		return trackNames;
	}

看起来好多了，不是吗？ 而不是两个嵌套for循环，在整个操作中我们有一个单一的清楚方法调用执行顺序。但是，这还不够。我们仍然手工创建一个Set，并在结尾添加每个元素。我们真的希望整个计算只是一个Stream调用链。

正如你可以在最后使用collect(toList())来建立一个值列表，您也可以使用collect(toSet())来建立一组值。所以，我们使用collect调用来替换我们最终的forEach调用，我们现在可以删除trackNames变量。

.. code-block:: java

	public Set<String> findLongTracks(List<Album> albums) {
		return albums.stream().flatMap(album -> album.getTracks())
		.filter(track -> track.getLength() > 60)
		.map(track -> track.getName())
		.collect(toSet());
	}

总而言之，我们已经使用了一段旧的代码，并将其重构为流使用的惯用语。

多次流调用
==========

.. code-block:: java

	List<Artist> musicians = album.getMusicians().collect(toList());

	List<Artist> bands = musicians.stream().filter(artist -> artist.getName().startsWith("The")).collect(toList());

	Set<String> origins = bands.stream().map(artist -> artist.getNationality()).collect(toSet());

	Set<String> origins = album.getMusicians()
	.filter(artist -> artist.getName().startsWith("The"))
	.map(artist -> artist.getNationality())
	.collect(toSet());

前面的调用比下面的链式调用更糟糕：

	- 很难阅读，因为样板代码与实际的业务逻辑更糟。
	- 效率较低，因为它需要在中间步骤立即创建新的集合对象。
	- 它使用无意义的垃圾变量(只需要它中间作为中间结果)来混淆您的方法。
	- 使自动并行化操作更加难。

