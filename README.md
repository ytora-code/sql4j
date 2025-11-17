# SQL4J

## 1 快速入门

### 1.1 实现IConnectionProvider

数据库连接提供组件，由于SQL4J并不知道开发者使用的框架（数据库连接池？Spring管理的有事务机制的连接池？），所以需要开发者实现IConnectionProvider，来提供数据库连接的提供方式

```java
public interface IConnectionProvider {

    /**
     * 获取数据库连接
     */
    Connection getConnection() throws SQLException;

    /**
     * 关闭数据库连接
     */
    void closeConnection(Connection connection) throws SQLException;

}
```

示例：每次直接通过JDBC创建连接

```java
public class MyConnectionProvider implements IConnectionProvider {

    String url = "jdbc:mysql://localhost:3306/test";
    String user = "root";
    String password = "220600";

    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }
}
```



### 1.2 创建 SQLHelper 对象

进行 SQL 操作的核心组件，应该保持全局唯一

```java
SQLHelper sqlHelper = new SQLHelper();
```

将上面的 MyConnectionProvider 注册给 SQLHelper

```java
sqlHelper.registerConnectionProvider(new MyConnectionProvider());
```



### 1.3 准备数据

先创建BEAN

```java
@Bean
public class SysUser {
    private Long id;
    @Column("user_name")
    private UserName s_s_s_s_name;
    private String realName;
    private String password;
    private String departCode;
    private String phone;
    private LocalDate birthday;
}

public enum UserName {
    admin, guest
}
```

准备一张表

```sql
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL,
  `user_name` varchar(255) NULL,
  `real_name` varchar(255) NULL,
  `password` varchar(255) NULL,
  `depart_code` varchar(255) NULL,
  `phone` varchar(255) NULL,
  `birthday` date NULL,
  PRIMARY KEY (`id`)
);
```

插入数据

```sql
INSERT INTO `test`.`sys_user` (`id`, `user_name`, `real_name`, `password`, `depart_code`, `phone`, `birthday`) VALUES (1, 'admin', '杨三', '123', 'A01', '112233', '2025-11-17');
```



### 1.4 CRUD

SELECT

```java
List<SysUser> beans = sqlHelper.select(SysUser::getId, SysUser::getBirthday, SysUser::getS_s_s_s_name, SysUser::getRealName, SysUser::getPhone)
                .from(SysUser.class)
                .where(w -> w.like(SysUser::getRealName, "%杨%").ge(SysUser::getId, 0))
                .limit(10)
                .offset(0)
                .submit(SysUser.class);
```

submit表示提交数据库执行，里面的参数`SysUser.class`表示使用SysUser来接受返回值

控制台打印如下：

```
 ===>	SELECT id, birthday, user_name, real_name, phone FROM sys_user WHERE real_name LIKE ? AND id >= ? LIMIT 10 OFFSET 0
 ===>	[ %杨%(String), 0(Integer) ]
 <===	{birthday=2025-11-17, phone=112233, user_name=admin, real_name=杨三, id=1}
```

第一行表示即将执行的 SQL，第二行表示占位符参数，下面的行表示查询结果集

这里使用的默认日志打印器 DefaultSqlLogger，开发者可以自定义日志打印器



INSERT

```java
List<Object> result = sqlHelper.insert(SysUser.class)
                .into(SysUser::getId, SysUser::getRealName)
                .value(2, "李四")
                .value(3, "王五")
                .submit();
```

返回值result表示数据库内部产生的主键（自增），如果主键不是数据库产生的（比如手动指定主键），返回值result则为空集合

控制台打印如下：

```
 ===>	INSERT INTO sys_user (id, real_name) VALUES (?, ?), (?, ?)
 ===>	[ 2(Integer), 李四(String), 3(Integer), 王五(String) ]
 <===	 新增行数：2
```

此时查询数据库，发现里面多出来两条数据



UPDATE

```java
Integer count = sqlHelper.update(SysUser.class)
        .set(SysUser::getRealName, "陈6")
        .where(w -> w.eq(SysUser::getId, 3))
        .submit();
```

返回值count表示 UPDATE 操作影响的行数

这句代码表示将`id=3`的那行数据的real_name字段改为“陈6”

控制台打印如下：

```
 ===>	UPDATE sys_user SET real_name = ? WHERE id = ?
 ===>	[ 陈6(String), 3(Integer) ]
 <===	 影响行数1
```



DELETE

```java
Integer count1 = sqlHelper.delete()
        .from(SysUser.class)
        .where(w -> w.gt(SysUser::getId, 1))
        .submit();
```

返回值count表示 DELETE操作影响的行数

这句代码表示删除`id>1`的数据

控制台打印如下：

```
 ===>	DELETE FROM sys_user WHERE id > ?
 ===>	[ 1(Integer) ]
 <===	 影响行数2
```

