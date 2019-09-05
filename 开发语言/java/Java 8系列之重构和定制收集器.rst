============================
Java 8系列之重构和定制收集器
============================
前面我们已经了解到了Collector类库中各种收集器的强大，可是，它们也只是能满足常用的场景。既然开放了Collector接口，我们当然可以根据自已意愿去定制，实际操作起来还是比较简单的。

Collectors.joining源码解析
==========================
从前面，我们已经了解到一个Collector是由四部分组成的：

	- Supplier<A> supplier(): 创建新的结果结
	- BiConsumer<A, T> accumulator(): 将元素添加到结果容器
	- BinaryOperator<A> combiner(): 将两个结果容器合并为一个结果容器
	- Function<A, R> finisher(): 对结果容器作相应的变换

我们先Collectors.joining是怎么实现的：
