package org.ytor;

import org.junit.jupiter.api.Test;
import org.ytor.bean.Order;
import org.ytor.bean.User;
import org.ytor.sql4j.core.SQLHelper;
import org.ytor.sql4j.enums.OrderType;
import org.ytor.sql4j.sql.SqlInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SelectBuilderTest {

    private final SQLHelper sqlHelper = new SQLHelper();

    // 1. 测试SELECT语句：基本查询
    @Test
    public void testSelectBuilder() {
        SqlInfo sqlInfo = sqlHelper.select(User::getUserName, User::getUserEmail)
                .from(User.class)
                .where(w -> w.gt(User::getAge, 18))
                .orderBy(User::getUserName, OrderType.ASC)
                .end();

        // 预期生成的SQL语句
        String expectedSql = "SELECT user_name, user_email FROM user WHERE age > ? ORDER BY user_name ASC";
        assertEquals(expectedSql, sqlInfo.getSql());
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
}
