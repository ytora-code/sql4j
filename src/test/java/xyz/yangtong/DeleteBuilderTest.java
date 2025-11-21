package xyz.yangtong;

import org.junit.jupiter.api.Test;
import xyz.yangtong.bean.User;
import xyz.yangtong.sql4j.core.SQLHelper;
import xyz.yangtong.sql4j.sql.SqlInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeleteBuilderTest {

    private final SQLHelper sqlHelper = new SQLHelper();

    // 1. 测试DELETE语句：基本删除
    @Test
    public void testDeleteBuilder() {
        SqlInfo sqlInfo = sqlHelper.delete()
                .from(User.class)
                .where(w -> w.eq(User::getId, 1))
                .end();

        // 预期生成的SQL语句
        String expectedSql = "DELETE FROM user WHERE id = ?";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(1, sqlInfo.getOrderedParms().size()); // 一个参数：id
    }

    // 2. 测试没有WHERE子句的DELETE（删除整个表）
    @Test
    public void testDeleteWithoutWhere() {
        SqlInfo sqlInfo = sqlHelper.delete()
                .from(User.class)
                .end();

        // 预期生成的SQL语句（删除整个表）
        String expectedSql = "DELETE FROM user";
        assertEquals(expectedSql, sqlInfo.getSql());
    }

    // 3. 测试多个WHERE条件的DELETE
    @Test
    public void testMultipleWhereConditionsDelete() {
        SqlInfo sqlInfo = sqlHelper.delete()
                .from(User.class)
                .where(w -> w.eq(User::getAge, 18).and(ww -> ww.eq(User::getUserName, "John")))
                .end();

        // 预期生成的SQL语句
        String expectedSql = "DELETE FROM user WHERE age = ? AND (user_name = ?)";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(2, sqlInfo.getOrderedParms().size()); // 两个参数：age 和 user_name
    }
}
