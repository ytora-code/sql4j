package xyz.ytora.sql4j;

import org.junit.jupiter.api.Test;
import xyz.ytora.sql4j.bean.Order;
import xyz.ytora.sql4j.bean.User;
import xyz.ytora.sql4j.core.SQLHelper;
import xyz.ytora.sql4j.enums.OrderType;
import xyz.ytora.sql4j.func.support.Concat;
import xyz.ytora.sql4j.func.support.Count;
import xyz.ytora.sql4j.func.support.Raw;
import xyz.ytora.sql4j.sql.SqlInfo;
import xyz.ytora.sql4j.sql.Wrapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SelectBuilderTest {

    private final SQLHelper sqlHelper = new SQLHelper();

    // 1. 测试SELECT语句：基本查询
    @Test
    public void testSelectBuilder() {
        SqlInfo sqlInfo = sqlHelper.distinct().select(User.class)
                .from(User.class)
                .where(w -> w.gt(User::getAge, 18))
                .groupBy(User.class)
                .orderBy(User::getUserName, OrderType.ASC)
                .end();

        // 预期生成的SQL语句
        System.out.println(sqlInfo.getSql());
        //String expectedSql = "SELECT DISTINCT id,age,user_email,user_name FROM user WHERE age > ? ORDER BY user_name ASC";
        //assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(1, sqlInfo.getOrderedParms().size()); // 一个参数：age > 18
    }

    // 2. 测试多个JOIN子句的情况
    @Test
    public void testJoinBuilder() {
        SqlInfo sqlInfo = sqlHelper.select(User::getUserName).select(Order::getOrderAmount)
                .from(User.class)
                .innerJoin(Order.class, on -> on.eq(User::getId, Order::getUserId))
                .where(w -> w.gt(User::getAge, 18))
                .end();


        // 预期生成的SQL语句
        String expectedSql = "SELECT u.user_name, o.order_amount FROM user u INNER JOIN order o ON u.id = o.user_id WHERE u.age > ?";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(1, sqlInfo.getOrderedParms().size()); // 一个参数：age > 18
    }

    // 3. 测试GROUP BY和HAVING的使用
    @Test
    public void testGroupByHavingBuilder() {
        SqlInfo sqlInfo = sqlHelper.select(User::getUserName, User::getAge)
                .from(User.class)
                .groupBy(User::getAge)
                .having(w -> w.gt(User::getAge, 18))
                .end();

        // 预期生成的SQL语句
        String expectedSql = "SELECT user_name, age FROM user GROUP BY age HAVING age > ?";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(1, sqlInfo.getOrderedParms().size()); // 一个参数：age > 18
    }

    //    // 4. 测试LIMIT和OFFSET的使用
    @Test
    public void testLimitOffsetBuilder() {
        SqlInfo sqlInfo = sqlHelper.select(User::getUserName, User::getUserEmail)
                .from(User.class)
                .limit(10)
                .offset(20).end();

        // 预期生成的SQL语句
        String expectedSql = "SELECT user_name, user_email FROM user LIMIT 10 OFFSET 20";
        assertEquals(expectedSql, sqlInfo.getSql());
    }

    // 5. 测试空的SELECT语句，期望返回*
    @Test
    public void testEmptySelectBuilder() {
        SqlInfo sqlInfo = sqlHelper.select()
                .from(User.class).end();
        // 预期生成的SQL语句
        String expectedSql = "SELECT * FROM user";
        assertEquals(expectedSql, sqlInfo.getSql());
    }

    // 6. 测试没有ORDER BY时的SQL
    @Test
    public void testSelectWithoutOrderBy() {
        SqlInfo sqlInfo = sqlHelper.select(User::getUserName, User::getUserEmail)
                .from(User.class)
                .where(w -> w.gt(User::getAge, 18))
                .end();


        // 预期生成的SQL语句（没有ORDER BY）
        String expectedSql = "SELECT user_name, user_email FROM user WHERE age > ?";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(1, sqlInfo.getOrderedParms().size()); // 一个参数：age > 18
    }

    // 7. 测试多个WHERE条件
    @Test
    public void testMultipleWhereConditions() {
        SqlInfo sqlInfo = sqlHelper.select(User::getUserName, User::getUserEmail)
                .from(User.class)
                .where(w -> w.gt(User::getAge, 18).and(ww -> ww.eq(User::getUserName, "John")))
                .end();

        // 预期生成的SQL语句
        String expectedSql = "SELECT user_name, user_email FROM user WHERE age > ? AND (user_name = ?)";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(2, sqlInfo.getOrderedParms().size()); // 两个参数：age > 18 和 user_name LIKE 'John%'
    }

    // 7. 测试函数、包装wrapper
    @Test
    public void testWrapper() {
        SqlInfo sqlInfo = sqlHelper.select(Count.of(User::getId))
                .from(User.class)
                .leftJoin(Order.class, on -> on.eq(User::getId, Order::getUserId))
                .where(w -> w.gt(User::getAge, Wrapper.of(23))
                        .and(ww -> ww.eq(User::getUserName, Wrapper.of("John")))
                        .eq(Concat.of(User::getUserName, User::getId, User::getUserEmail), "21312")
                )
                .groupBy(User::getUserName, User::getUserEmail)
                .having(w -> w.ge(Count.of(User::getUserName), Wrapper.of(100)))
                .end();

        // 预期生成的SQL语句
        String expectedSql = "SELECT count(u.id) FROM user u LEFT JOIN order o ON u.id = o.user_id WHERE u.age > 23 AND (u.user_name = 'John') AND concat(u.user_name, u.id, u.user_email) = ? GROUP BY u.user_name, u.user_email HAVING count(u.user_name) >= 100";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(1, sqlInfo.getOrderedParms().size());
    }

    // 8. 测试复杂JOIN和多个条件
    @Test
    public void testComplexJoinWithMultipleConditions() {
        SqlInfo sqlInfo = sqlHelper.select(User::getUserName).select(Order::getOrderAmount)
                .from(User.class)
                .leftJoin(Order.class, on -> on.eq(User::getId, Order::getUserId).gt(Order::getOrderAmount, 100.0))
                .where(w -> w.gt(User::getAge, 18))
                .end();

        // 预期生成的SQL语句
        String expectedSql = "SELECT u.user_name, o.order_amount FROM user u LEFT JOIN order o ON u.id = o.user_id AND o.order_amount > ? WHERE u.age > ?";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(2, sqlInfo.getOrderedParms().size()); // 两个参数：order_amount > 100 和 age > 18
    }

    // 8. 测试子查询
    @Test
    public void testSubSelect() {
        SqlInfo sqlInfo = sqlHelper.select(User::getUserName).select(Order::getOrderAmount)
                .from(sqlHelper.select(User::getUserName, User::getId, User::getAge).from(User.class).where(w -> w.eq(User::getAge, 1)))
                .where(w -> w.gt(User::getUserName, "zans"))
                .end();

        // 预期生成的SQL语句
        String expectedSql = "SELECT user_name, order_amount FROM (SELECT user_name, id, age FROM user WHERE age = ?) a WHERE user_name > ?";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(2, sqlInfo.getOrderedParms().size());
    }

    // 8. 测试字符串表
    @Test
    public void testStrTable() {
        SqlInfo sqlInfo = sqlHelper.select().from(Order.class, "0111")
                .leftJoin(Order.class, "o222", on -> on.eq(Raw.of("id", "o1Id"), Raw.of("o2Id.id")))
                .end();
        // 预期生成的SQL语句
        // TODO 对应这种自查询，希望可以智能识别不同的表，而不是使用 Raw 手动指定
        String expectedSql = "SELECT * FROM order 0111 LEFT JOIN order o222 ON o1Id.id = o2Id.id";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(0, sqlInfo.getOrderedParms().size());

    }
}
