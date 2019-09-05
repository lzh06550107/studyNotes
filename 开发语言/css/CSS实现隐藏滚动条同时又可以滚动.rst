:date: 2016-01-23
:categories: css
:tags: css

******************
CSS 实现隐藏滚动条同时又可以滚动
******************

移动端页面为了更接近原生的体验，是否可以隐藏滚动条，同时又保证页面可以滚动？

使用原生伪对象选择器
==================

使用 ``overflow:hidden`` 隐藏滚动条，但存在的问题是：页面或元素失去了滚动的特性。由于只需要兼容移动浏览器（Chrome 和 Safari），于是想到了自定义滚动条的伪对象选择器 ``::-webkit-scrollbar`` 。

关于这个选择器的介绍可以参考：

- `Styling Scrollbars <https://webkit.org/blog/363/styling-scrollbars/>`_
- `Custom Scrollbars in WebKit <https://css-tricks.com/custom-scrollbars-in-webkit/>`_

应用如下 CSS 可以隐藏滚动条：

.. code-block:: css

	.element::-webkit-scrollbar {display:none}

使用黑科技实现
=============

如果要兼容 PC 其他浏览器（IE、Firefox 等），国外一位才人 John Kurlak 也研究出了一种办法。在容器外面再嵌套一层 ``overflow:hidden`` 内部内容再限制尺寸和外部嵌套层一样，就变相隐藏了。

.. code-block:: html

	<div class="outer-container">
	     <div class="inner-container">
	        <div class="content">
	            ......
	        </div>
	     </div>
	 </div>

.. code-block:: css

	.outer-container,.content {
	    width: 200px; height: 200px;
	}
	.outer-container {
	    position: relative;
	    overflow: hidden;
	}
	.inner-container {
	    position: absolute; left: 0;
	    overflow-x: hidden;
	    overflow-y: scroll;
	}

	 /* for Chrome */
	.inner-container::-webkit-scrollbar {
	    display: none;
	}





