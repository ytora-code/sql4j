package xyz.ytora.sql4j.func.support;

import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.func.SQLFunc;
import xyz.ytora.sql4j.sql.AliasRegister;
import xyz.ytora.sql4j.util.LambdaUtil;

public class Avg implements SQLFunc {

    private SFunction<?, ?> column;

    private String str;

    private AliasRegister aliasRegister;

    public Avg(SFunction<?, ?> column) {
        this.column = column;
    }

    public Avg(String str) {
        this.str = str;
    }

    public static <T> Avg of(SFunction<T, ?> column) {
        return new Avg(column);
    }

    public static Avg of(String str) {
        return new Avg(str);
    }

    @Override
    public void addAliasRegister(AliasRegister aliasRegister) {
        this.aliasRegister = aliasRegister;
    }

    @Override
    public String getValue() {
        if (column != null) {
            return "avg(" + LambdaUtil.parseColumn(column, aliasRegister) + ")";
        } else {
            return "avg(" + str + ")";
        }
    }
}
