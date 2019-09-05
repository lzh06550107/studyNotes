****
Prop
****

https://cn.vuejs.org/v2/guide/components-props.html
https://blog.csdn.net/luzhensmart/article/details/84678333

传递静态或动态 ``Prop`` 静态是直接给属性赋字符串值；动态值，则通过 ``v-bind`` 传入。

动态 ``Prop`` 会在父组件的变量变化时，动态改变 ``Prop`` 传入的值。从而导致子组件的值也改变。而静态传入的值时不会变化的。

Prop 直接在js部分定义即可，不需要像双向数据绑定 ``v-model`` 那样在模板中定义绑定

v-bind="tagProps" {href: linkUrl, target} 这个用法需要研究。


