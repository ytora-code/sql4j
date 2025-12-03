package xyz.ytora.sql4j.func.support;

import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.func.SQLFunc;
import xyz.ytora.sql4j.sql.AliasRegister;
import xyz.ytora.sql4j.util.LambdaUtil;

public class Max implements SQLFunc {

    private SFunction<?, ?> column;

    private String str;

    private AliasRegister aliasRegister;

    public Max(SFunction<?, ?> column) {
        this.column = column;
    }

    public Max(String str) {
        this.str = str;
    }

    public static <T> Max of(SFunction<T, ?> column) {
        return new Max(column);
    }

    public static Max of(String str) {
        return new Max(str);
    }

    @Override
    public void addAliasRegister(AliasRegister aliasRegister) {
        this.aliasRegister = aliasRegister;
    }

    @Override
    public String getValue() {
        if (column != null) {
            return "max(" + LambdaUtil.parseColumn(column, aliasRegister) + ")";
        } else {
            return "max(" + str + ")";
        }
    }
}
