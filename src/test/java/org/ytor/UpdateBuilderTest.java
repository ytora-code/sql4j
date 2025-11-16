package org.ytor;

import org.junit.jupiter.api.Test;
import org.ytor.bean.User;
import org.ytor.sql4j.core.SQLHelper;
import org.ytor.sql4j.sql.SqlInfo;
import org.ytor.sql4j.sql.update.UpdateBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateBuilderTest {

    private final SQLHelper sqlHelper = new SQLHelper();

    // 1. 测试UPDATE语句：基本更新
    @Test
    public void testUpdateBuilder() {
        SqlInfo sqlInfo = sqlHelper.update(User.class)
                .set(User::getUserName, "John")
                .where(w -> w.eq(User::getId, 1))
                .end();

        // 预期生成的SQL语句
        String expectedSql = "UPDATE user SET user_name = ? WHERE id = ?";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(2, sqlInfo.getOrderedParms().size()); // 两个参数：user_name 和 id
    }

    // 2. 测试多个SET子句
    @Test
    public void testMultipleSetClauses() {
        SqlInfo sqlInfo = sqlHelper.update(User.class)
                .set(User::getUserName, "John")
                .set(User::getUserEmail, "john@example.com")
                .where(w -> w.eq(User::getId, 1))
                .end();

        // 预期生成的SQL语句
        String expectedSql = "UPDATE user SET user_name = ?, user_email = ? WHERE id = ?";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(3, sqlInfo.getOrderedParms().size()); // 三个参数：user_name, user_email 和 id
    }

    // 3. 测试没有WHERE子句的UPDATE（全表更新）
    @Test
    public void testUpdateWithoutWhere() {
        SqlInfo sqlInfo = sqlHelper.update(User.class)
                .set(User::getUserName, "John")
                .end();

        // 预期生成的SQL语句（全表更新）
        String expectedSql = "UPDATE user SET user_name = ?";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(1, sqlInfo.getOrderedParms().size()); // 一个参数：user_name
    }

    // 4. 测试条件表达式中的多个条件
    @Test
    public void testMultipleWhereConditions() {
        SqlInfo sqlInfo = sqlHelper.update(User.class)
                .set(User::getUserName, "John")
                .where(w -> w.eq(User::getAge, 18).and(ww -> ww.gt(User::getId, 10)))
                .end();

        // 预期生成的SQL语句
        String expectedSql = "UPDATE user SET user_name = ? WHERE age = ? AND (id > ?)";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(3, sqlInfo.getOrderedParms().size()); // 三个参数：user_name, age 和 id
    }
}
