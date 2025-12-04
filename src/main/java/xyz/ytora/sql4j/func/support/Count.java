package xyz.ytora.sql4j.func.support;

import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.func.SQLFunc;
import xyz.ytora.sql4j.sql.AliasRegister;
import xyz.ytora.sql4j.util.LambdaUtil;

/**
 * count 函数， count(1)、count(*)、count(id)
 */
public class Count implements SQLFunc {

    private SFunction<?, ?> column;

    private String str;

    private String as;

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
    public SQLFunc as(String as) {
        this.as = as;
        return this;
    }

    @Override
    public String as() {
        return as;
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
