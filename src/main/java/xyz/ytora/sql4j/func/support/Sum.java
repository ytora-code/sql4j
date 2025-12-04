package xyz.ytora.sql4j.func.support;

import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.func.SQLFunc;
import xyz.ytora.sql4j.sql.AliasRegister;
import xyz.ytora.sql4j.util.LambdaUtil;

public class Sum implements SQLFunc {

    private SFunction<?, ?> column;

    private String str;

    private String as;

    private AliasRegister aliasRegister;

    public Sum(SFunction<?, ?> column) {
        this.column = column;
    }

    public Sum(String str) {
        this.str = str;
    }

    public static <T> Sum of(SFunction<T, ?> column) {
        return new Sum(column);
    }

    public static Sum of(String str) {
        return new Sum(str);
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
            return "sum(" + LambdaUtil.parseColumn(column, aliasRegister) + ")";
        } else {
            return "sum(" + str + ")";
        }
    }
}
