***************************
浅谈Facade层,Service层,DAO层设计原则
***************************

一，Service->DAO，只能在Service中注入DAO。

二，DAO只能操作但表数据，跨表操作放在Service中，Service尽量复用DAO，只有一张表产生的业务放入DAO中。

三，事务操作，放在一个DAO中。

四，如果有更大Service的之间的复杂调用，考虑在service上再加Facade层(Components组件)。

五，多考虑这部分代码放在哪里，多里利用上下分层，增加代码可读性，提高代码复用率。