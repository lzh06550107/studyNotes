************************************************************
vue根目录下的index.html中的id="app"与src目录下的App.vue中的id="app"为什么不会冲突
************************************************************

具体描述
========
在 ``vue`` 的项目开发中，我们通过 ``vue-cli`` 生成的项目结构中默认有 ``index.html`` 的文件作为默认的渲染主页文件它的外层 ``div`` 有 ``id="app"`` ，而在项目的 ``src`` 目录中主组件 ``App.vue`` 中它的外层 ``div`` 也有 ``id="app"`` ，两者在渲染时为什么不会冲突？

相关探讨
========
我们先来了解一下 ``vue`` 项目中 ``main.js`` 、 ``App.vue`` 、 ``index.html`` 间的关系

- ``main.js`` 是入口文件，主要作用是初始化 ``vue`` 实例并使用需要的插件;
- ``App.vue`` 是我们的主组件，所有的页面都是在 ``App.vue`` 下进行切换的；也可以认为所有的路由也是 ``App.vue`` 的子组件;
- ``index.html`` 是 ``vue-cli`` 构的项目结构中默认的主渲染页面文件;

我们先来了解一下 ``src/main.js`` 中 ``template`` 的作用

- 一个字符串模板作为 ``Vue`` 实例的标识使用。模板将会替换挂载的元素。挂载元素的内容都将被忽略，除非模板的内容有分发插槽；
- 下面代码中的 ``template: < App/>`` 指代引入的 ``App`` 组件，用来取代 ``index.html`` 中 ``id`` 为 ``app`` 的 ``div`` 层及其内容，反过来说，最初 ``el`` 挂载的 ``id`` 为 ``app`` 的节点指的是 ``index.html`` 的节点而不是 ``app.vue`` 的，只是后来由于 ``template`` 而被主组件 ``App.vue`` 的内容所取代了而已；

.. code-block:: js

	import Vue from 'vue'
	import App from './App'
	import router from './router'

	Vue.config.productionTip = false

	/* eslint-disable no-new */
	new Vue({
	    el: '#app',
	    router,
	    template: '<App/>',
	    components: {
	        App
	    }
	})

结论
====

1. ``index.html`` 中的 ``< div id="app">`` 是指定绑定元素根路径的
2. ``App.vue`` 的 ``< div id="app">`` 则是用于具体注入绑定元素的内容
3. 由于 ``Vue`` 组件必须有个根元素，所以 ``App.vue`` 里面，根元素 ``<div id="app">`` 与外层被注入框架 ``index.html`` 中的 ``<div id="app">`` 是一致的
4. ``index.html`` 中的 ``#app`` 指定绑定目标，而 ``vue`` 文件里的 ``#app`` 提供填充内容，两者在运行时指的是同一个 ``DOM`` 元素

