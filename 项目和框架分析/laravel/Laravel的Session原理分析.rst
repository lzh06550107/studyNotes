===============
Session原理分析
===============

前言
====
在网页开发中， ``session`` 具有重要的作用，它可以在多个请求中存储用户的信息，用于识别用户的身份信息。laravel 为用户提供了可读性强的 ``API`` 处理各种自带的 ``Session`` 后台驱动程序。支持诸如比较热门的 ``Memcached`` 、 ``Redis`` 和开箱即用的数据库等常见的后台驱动程序。本文将会在本篇文章中讲述最常见的由 ``File`` 与 ``redis`` 驱动的 ``session`` 源码。

session 服务的注册
==================
与其他功能一样， ``session`` 由自己的服务提供者在 ``container`` 内进行注册：


https://github.com/LeoYang90/laravel-source-analysis/blob/master/Laravel%20Session%E2%80%94%E2%80%94session%20%E7%9A%84%E5%90%AF%E5%8A%A8%E4%B8%8E%E8%BF%90%E8%A1%8C%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90.md









