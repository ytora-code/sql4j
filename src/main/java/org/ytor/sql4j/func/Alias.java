package org.ytor.sql4j.func;

import org.ytor.sql4j.sql.AliasRegister;

/**
 * 包装别名
 * <pre />
 * 如果需要手动设置别名，则要使用该类进行包装
 */
public class Alias implements SFunction<Object, Object> {

    private final SFunction<?, ?> column;
    private final String alias;
    private AliasRegister aliasRegister;

    public Alias(SFunction<?, ?> column, String alias) {
        this.column = column;
        this.alias = alias;
    }

    public static <T> Alias of(SFunction<T, ?> column, String alias) {
        return new Alias(column, alias);
    }

    public SFunction<?, ?> getColumn() {
        return column;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public Object apply(Object o) {
        throw new UnsupportedOperationException();
    }

    public void addAliasRegister(AliasRegister aliasRegister) {
        this.aliasRegister = aliasRegister;
    }
}
