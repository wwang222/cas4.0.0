## cas单点登录

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)

### 基于CAS4.0.0版本开箱即用的单点登录服务器
部署：

1. 一个配置ssl的[tomcat](https://www.baidu.com).
2. 下载源码并导入ide.
3. 修改[deployerConfigContext.xml]()中的数据库连接.
    `
    
        <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
            <property name="driverClassName"><value>com.mysql.jdbc.Driver</value></property>
            <property name="url"><value>jdbc:mysql://127.0.0.1:3306/sso</value></property>
            <property name="username"><value>cas</value></property>
            <property name="password"><value>cas2016</value></property>
        </bean>
    `
4. 修改[AuthenticationAddSaltHandler.java]()中的密码认证规则（默认MD5，为了好测试是将密码匹配注释了的）. 
5. 运行

现在：

1. 基于mysql的自定义登录认证
2. 单客户端登录（同一用户只允许在一个浏览器进行登录操作，换浏览器自动踢出之前登录）（不用可注释）
3. 自定义登出（已获取到用户名与tgt，要做任何操作都随你）

未来：

1. ldap认证
2. 返回参数自带user属性