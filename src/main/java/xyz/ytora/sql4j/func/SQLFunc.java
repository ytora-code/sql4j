package xyz.ytora.sql4j.func;

import xyz.ytora.sql4j.sql.AliasRegister;

/**
 * SQL 函数
 */
public interface SQLFunc extends SFunction<Object, Object> {
    @Override
    default Object apply(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * 查询字段的as别名
     */
    SQLFunc as(String as);

    /**
     * 获取当前查询字段的as别名
     */
    String as();

    /**
     * 指定别名MAP
     */
    void addAliasRegister(AliasRegister aliasRegister);

    /**
     * 得到函数值（本质是字符串）
     */
    String getValue();
}
