package xyz.yangtong;

import org.junit.jupiter.api.Test;
import xyz.yangtong.bean.User;
import xyz.yangtong.sql4j.core.SQLHelper;
import xyz.yangtong.sql4j.sql.SqlInfo;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsertBuilderTest {

    private final SQLHelper sqlHelper = new SQLHelper();

    // 1. 测试INSERT语句：基本插入
    @Test
    public void testInsertBuilder() {
        SqlInfo sqlInfo = sqlHelper.insert(User.class)
                .into(User::getUserName, User::getUserEmail)
                .value("John", "john@example.com")
                .end();

        // 预期生成的SQL语句
        String expectedSql = "INSERT INTO user (user_name, user_email) VALUES (?, ?)";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(2, sqlInfo.getOrderedParms().size()); // 两个参数：user_name 和 user_email
    }

    // 2. 测试多个值的插入
    @Test
    public void testMultipleValuesInsert() {
        SqlInfo sqlInfo = sqlHelper.insert(User.class)
                .into(User::getUserName, User::getUserEmail)
                .values(Arrays.asList(
                        Arrays.asList("John", "john@example.com"),
                        Arrays.asList("Jane", "jane@example.com")
                ))
                .end();

        // 预期生成的SQL语句
        String expectedSql = "INSERT INTO user (user_name, user_email) VALUES (?, ?), (?, ?)";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(4, sqlInfo.getOrderedParms().size()); // 四个参数：user_name 和 user_email（两组）
    }

    // 3. 测试插入空字段
    @Test
    public void testInsertWithNullValues() {
        SqlInfo sqlInfo = sqlHelper.insert(User.class)
                .into(User::getUserName, User::getUserEmail)
                .value(null, "john@example.com")
                .end();

        // 预期生成的SQL语句
        String expectedSql = "INSERT INTO user (user_name, user_email) VALUES (?, ?)";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(2, sqlInfo.getOrderedParms().size()); // 一个参数：user_email
    }

    // 4. 测试插入没有字段
    @Test
    public void testEmptyInsert() {
        SqlInfo sqlInfo = sqlHelper.insert(User.class)
                .into()
                .value("John", "john@example.com")
                .end();

        // 预期生成的SQL语句
        String expectedSql = "INSERT INTO user VALUES (?, ?)";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(2, sqlInfo.getOrderedParms().size()); // 两个参数：user_name 和 user_email
    }

    // 4. 测试使用查询结果插入
    @Test
    public void testSubSelect() {
        SqlInfo sqlInfo = sqlHelper.insert(User.class)
                .into()
                .values(sqlHelper.select(User::getUserName, User::getId, User::getAge).from(User.class).where(w -> w.eq(User::getAge, 1)))
                .end();
        System.out.println(sqlInfo);
        // 预期生成的SQL语句
        String expectedSql = "INSERT INTO user (user_name, id, age) (SELECT user_name, id, age FROM user WHERE age = ?)";
        assertEquals(expectedSql, sqlInfo.getSql());
        assertEquals(1, sqlInfo.getOrderedParms().size());
    }
}
