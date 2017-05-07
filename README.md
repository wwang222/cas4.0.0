# cas单点登录
基于CAS4.0.0版本开箱即用的单点登录服务器

1、一个配置ssl的tomcat
2、下载源码并导入ide
3、修改deployerConfigContext.xml中的数据库连接
4、修改AuthenticationAddSaltHandler.java中的密码认证规则（默认MD5，为了好测试是将密码匹配注释了的）
5、运行
