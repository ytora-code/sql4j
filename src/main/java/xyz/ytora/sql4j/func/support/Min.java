package xyz.ytora.sql4j.func.support;

import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.func.SQLFunc;
import xyz.ytora.sql4j.sql.AliasRegister;
import xyz.ytora.sql4j.util.LambdaUtil;

public class Min implements SQLFunc {

    private SFunction<?, ?> column;

    private String str;

    private String as;

    private AliasRegister aliasRegister;

    public Min(SFunction<?, ?> column) {
        this.column = column;
    }

    public Min(String str) {
        this.str = str;
    }

    public static <T> Min of(SFunction<T, ?> column) {
        return new Min(column);
    }

    public static Min of(String str) {
        return new Min(str);
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
            return "min(" + LambdaUtil.parseColumn(column, aliasRegister) + ")";
        } else {
            return "min(" + str + ")";
        }
    }
}
