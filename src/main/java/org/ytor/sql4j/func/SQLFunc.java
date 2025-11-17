package org.ytor.sql4j.func;

import org.ytor.sql4j.sql.AliasRegister;

/**
 * SQL 函数
 */
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
