# SQL4J

旨在能够像写 SQL 一样写代码，语法、API方面尽可能跟 SQL 原生语法一致

例如：

```java
sqlHelper.select(User::getUserName).select(User::getAge)
        .from(User.class)
        .leftJoin(Order.class, on -> on.eq(User::getId, Order::getUserId).gt(Order::getOrderAmount, 100.0))
        .where(w -> w.gt(User::getAge, 18))
        .groupBy(User::getUserName, User::getAge)
    	.having(h -> h.gt(Count.of(User::getId), Wrapper.of(1)))
        .orderBy(User::getAge, OrderType.DESC)
        .limit(10)
        .offset(10);
```

等价于下面的 SQL

```sql
SELECT u.user_name, u.age FROM user u LEFT JOIN order o ON u.id = o.user_id AND o.order_amount > ? WHERE u.age > ? GROUP BY u.user_name, u.age HAVING count(u.id) > 1 ORDER BY u.age DESC LIMIT 10 OFFSET 10
```

参数列表如下

```
[100.0, 18]
```

------

## 1 快速入门

### 1.1 实现IConnectionProvider

数据库连接提供者

SQL4J并不知道开发者使用的开发方式（数据库连接池？Spring管理的有事务机制的连接池？），需要开发者实现IConnectionProvider，手动指定数据库连接的获取方式

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

------

### 1.2 创建 SQLHelper 对象

进行 SQL 操作的核心组件，应该保持全局唯一

```java
SQLHelper sqlHelper = new SQLHelper();
```

将上面的 MyConnectionProvider 注册给 SQLHelper

```java
sqlHelper.registerConnectionProvider(new MyConnectionProvider());
```

------

### 1.3 准备数据

先创建BEAN

```java
@Data
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

------

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

------

## 2 数据绑定

所谓数据库绑定，也就是将数据库中的原始数据绑定到Bean对象的过程

这里数据有三种状态

1. 位于数据库中
2. JDBC处理后，此时会得到`原始数据`：List<Map<String, Object>>
3. Bean，也就是将原始数据绑定为Bean对象数组

这里介绍数据从原始数据 -> Bean，也就是List<Map<String, Object>> 变成 List< Bean>

### 2.1 setter

Bean是一个对象，将Map<String, Object>变成一个Bean对象，本质就是字段映射

映射规则：根据原始数据的字段名称，找到Bean里面的setter方法，然后使用字段值作为参数，调用setter方法

比如：

1. Bean里面有一个方法`public void setRealName(String realName)`
2. 将方法名称`setRealName`变成real_name，去Map<String, Object>里面`get("real_name")`
3. 将得到的结果作为参数，调用setRealName方法
4. 由于一般的setter方法，都是进行了字段的赋值操作，所以这里就成功将数据绑定到了字段上

现在我们知道，数据绑定，并不是直接将数据绑定到字段上，而是先调用setter方法，再由setter方法进行间接绑定

特别的，如果Bean的字段类型（本质是看setter方法第一个参数的类型）是枚举，并且原始类型是

* 数字：将数字作为下标，去枚举类型里面找到对应的枚举值
* 字符串：将字符串作为枚举值的名称，去枚举类型里面找到对应的枚举值

------

### 2.2 @Column

有时候Bean的字段名称，可能并不是和数据库的字段名称一致，此时需要使用`@Column`指定数据库的字段名称

```java
 @Column("user_name")
 private UserName s_s_s_s_name;
```

上面代码表示Bean里面的字段名称是s_s_s_s_name，但是数据库的字段名称是user_name

------

### 2.3 @Table

数据库表名称和Bean的类名称的映射规则默认：

* 表名称是下划线格式：sys_user
* Bean的类名称是大驼峰：SysUser

如果表名称和类名称无法四默认规则转换

比如，将数据库的表名称改成`sys_user1`，再次运行 SELECT 程序，会报错：Table 'test.sys_user' doesn't exist

此时需要使用`@Table`

```java
@Table("sys_user1")
@Data
public class SysUser {
    // ...
}
```

再次运行 SELECT 程序，一切正常

------

## 3 Wrapper和SQLFunc

### 3.1 Wrapper

默认情况下，为了避免依赖注入，代码中的参数在被解析成 SQL 时，都是被解析成占位符`?`

比如

```java
sqlHelper.select().from(SysUser.class).where(w -> w.eq(SysUser::getId, 123))
```

会被解析成

```sql
select * from sys_user where id = ?
```

代码中的参数`123`会被有序地放进`参数列表`里面，等到将 SQL 提交给数据库执行时，会从参数列表依次取出每个参数，然后调用setObject(index, param)将参数绑定到 SQL 上



如果想要将参数直接拼接到 SQL 字符串里面，需要额外包一层Wrapper

```java
sqlHelper.select().from(SysUser.class).where(w -> w.eq(SysUser::getId, Wrapper.of(123)))
```

此时则会被解析成

```sql
select * from sys_user where id = 123
```

并且参数列表为空，因为被Wrapper包裹的参数会被直接拼接到 SQL 上，所以不会放进参数列表

------



### 3.2 SQLFunc

有时候，SQL 里面需要用到函数，比如

```sql
select count(id) from sys_user
```

此时代码中应该如何表示`count(id)`这个函数呢？

可不可以使用Wrappers？

不行！

如果Wrapper包裹的是字符串，比如

```java
sqlHelper.select(Wrapper.of("count(id)")).from(SysUser.class)
```

会被解析成

```sql
select 'count(id)' from sys_user
```

可见，count(id)被整体当成字符串处理了

为了处理这种情况，需要引入`SQLFunc`，表示 SQL 函数对象

上面带有函数的 SQL 使用SQLFunc表示如下

```java
// 会被解析成 select count(id) from sys_user
sqlHelper.select(Count.of(SysUser::getId)).from(SysUser.class);
```

其中，Count是一个SQLFunc的内置实现类，表示 SQL 中的函数count

```java
public interface SQLFunc extends SFunction<Object, Object> {
    @Override
    default Object apply(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * 指定别名MAP
     */
    void addAliasRegister(AliasRegister aliasRegister);

    /**
     * 得到函数值（本质是字符串）
     */
    String getValue();
}
```

可见SQLFunc是SFunction的子接口，所以程序中凡是能出现SFunction的地方，都可以使用SQLFunc替代

addAliasRegister抽象函数是 SQL4J 框架内部使用的，开发者无需关心

```java
/**
 * count 函数， count(1)、count(*)、count(id)
 */
public class Count implements SQLFunc {

    private SFunction<?, ?> column;

    private String str;

    private AliasRegister aliasRegister;

    public Count(SFunction<?, ?> column) {
        this.column = column;
    }

    public Count(String str) {
        this.str = str;
    }

    public static <T> Count of(SFunction<T, ?> column) {
        return new Count(column);
    }

    public static Count of(String str) {
        return new Count(str);
    }

    @Override
    public void addAliasRegister(AliasRegister aliasRegister) {
        this.aliasRegister = aliasRegister;
    }

    @Override
    public String getValue() {
        if (column != null) {
            return "count(" + LambdaUtil.parseColumn(column, aliasRegister) + ")";
        } else {
            return "count(" + str + ")";
        }
    }
}
```

从上面代码可以得知，Count本质就是也是做了一个字符串拼接

Count里面的`aliasRegister`是别名注册器，将来一句 SQL 可能涉及很多表，每个表都可以有别名，所有的别名信息都放在aliasRegister里面

aliasRegister由 SQL4J 框架调用`addAliasRegister`方法注入

------



### 3.3 自定义SQLFunc

```java
// length(str)，返回字符串的长度
public class Length implements SQLFunc {
    private AliasRegister aliasRegister;
    
    @Override
    public void addAliasRegister(AliasRegister aliasRegister) {
        this.aliasRegister = aliasRegister;
    }
    
    // 上面的代码是固定的，写死即可
    // 下面代码则需要根据实际情况编写
    
    private final String str;
    
    public Length(String str) {
        this.str = str;
    }
    
    // 方便外界获取Length对象：Length.of(xxx)，外界不使用of直接new一个对象也是等价的
    public static Length of(String str) {
        return new Length(str);
    }
    
    @Override
    public String getValue() {
        int length = str == null ? 0 : str.length();
        // 由于最终得到的SQL是字符串，所以这里也要返回字符串
        return length + "";
    }
}
         
```

最终拼接到 SQL 中的，就是getValue返回的字符串



## 3 类型转换器

将数据从数据库读取到程序中的Bean时，可能需要进行类型转换

比如`sys_user`表的birthday字段是date类型，JDBC处理后得到的类型是 `java.sql.Date`

但是SysUser类的同名字段birthday是LocalDate类型，类型不兼容，无法进行数据绑定

这时需要用到类型转换器，内置了几个类型转换器

1. java.lang.String -> java.lang.Enum
2. java.sql.Date -> java.time.LocalDate
3. java.sql.Time -> java.time.LocalTime
4. java.sql.Timestamp -> java.time.LocalDateTime

由于birthday的JDBC类型是java.sql.Date，而Bean的接收类型是java.time.LocalDate，由于已经存在了对应的内置类型转换器（java.sql.Date -> java.time.LocalDate），所以上面的代码可以直接绑定birthday

------

如果将SysUser类的`id`字段变成String类型，就无法绑定数据了，需要注册一个自定义的类型转换器

注册自定义的类型转换器

1. 实现Caster接口

   id字段的数据库原始类型是bigint，经过JDBC，得到的类型是Long，需要变成String

   ```java
   public class CustomCaster implements Caster<Long, String> {
       // 重写cast，第一个参数是JDBC得到的原始类型，第二个参数是要转换的目标类型
       @Override
       public String cast(Long sourceVal, Class<String> beanName) {
           return sourceVal.toString();
       }
   }
   ```

2. 注册自定义的类型转换器

   ```java
   sqlHelper.registerCaster(new TypePair(Long.class, String.class), new CustomCaster());
   ```

现在再次运行 SELECT 代码，发现id成功绑定



## 4 条件构造器

## 5 拦截器

将 SQL 真正提交数据库执行前后，有两处扩展点

1. 前置拦截器：执行之前会调用所有前置拦截器，如果有一个返回false，则取消提交
2. 后置拦截器：执行之后调用所有后置拦截器，并将后置拦截器的返回值作为该次提交真正的执行结果



拦截器使用方法：

自定义拦截器，实现SqlInterceptor接口

```java
/**
 * 拦截器
 */
public interface SqlInterceptor {
    /**
     * SQL 提交数据库之前进行拦截
     * @param sqlInfo 即将执行的 SQL 信息
     * @return 如果为ture则提交，如果为false则拦截
     */
    Boolean before(SqlInfo sqlInfo);

    /**
     * SQL 提交数据库之后
     * @param sqlInfo 执行的 SQL 信息
     * @return 真正返给客户端的执行结果
     */
    ExecResult after(SqlInfo sqlInfo);
}
```

或者继承SqlInterceptorAdapter类，二选一

```java
public class SqlInterceptorAdapter implements SqlInterceptor {

    @Override
    public Boolean before(SqlInfo sqlInfo) {
        return true;
    }

    @Override
    public ExecResult after(SqlInfo sqlInfo, ExecResult result) {
        return result;
    }
}
```

------

**创建自定义拦截器**

继承SqlInterceptorAdapter

```java
public class MySqlInterceptor extends SqlInterceptorAdapter {

    @Override
    public Boolean before(SqlInfo sqlInfo) {
        System.out.println("前置拦截================");
        return super.before(sqlInfo);
    }

    @Override
    public ExecResult after(SqlInfo sqlInfo, ExecResult execResult) {
        System.out.println("后置拦截================");
        return super.after(sqlInfo, execResult);
    }
}
```

------

**注册自定义拦截器**

```java
sqlHelper.addSqlInterceptor(new MySqlInterceptor());
```



经过如上操作后，再次执行 SELECT，控制台有如下打印内容：

```
 ===>	SELECT id, birthday, user_name, real_name, phone FROM sys_user1 WHERE real_name LIKE ? AND id >= ? LIMIT 10 OFFSET 0
 ===>	[ %杨%(String), 0(Integer) ]
前置拦截================
 <===	{birthday=2025-11-17, phone=112233, user_name=admin, real_name=杨三, id=1}
后置拦截================
```

------

## 6 日志记录

某些节点需要进行日志打印

日志接口

```java
public interface ISqlLogger {

    /**
     * 错误日志
     */
    void error(Object msg);

    /**
     * 警告日志
     */
    void warn(Object msg);

    /**
     * 正常信息日志
     */
    void info(Object msg);

    /**
     * debug日志
     */
    void debug(Object msg);
}
```

内置日志实现类

```java
public class DefaultSqlLogger implements ISqlLogger {
    @Override
    public void error(Object msg) {
        System.err.println(msg);
    }

    @Override
    public void warn(Object msg) {
        System.out.println(msg);
    }

    @Override
    public void info(Object msg) {
        System.out.println(msg);
    }

    @Override
    public void debug(Object msg) {
        System.out.println(msg);
    }
}
```

上面控制台打印的内容，本质就是DefaultSqlLogger的打印结果



如果需要更换日志打印组件，通用需要实现ISqlLogger接口

```java
public class MySqlLogger implements ISqlLogger {
    // 实现略
}
```



注册MySqlLogger

```java
sqlHelper.registerLogger(new MySqlLogger());
```



再次运行程序，控制台的日志就会使用MySqlLogger进行打印