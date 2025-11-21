package xyz.yangtong.sql4j.func.support;

import xyz.yangtong.sql4j.func.SFunction;
import xyz.yangtong.sql4j.func.SQLFunc;
import xyz.yangtong.sql4j.sql.AliasRegister;
import xyz.yangtong.sql4j.util.LambdaUtil;

public class Min implements SQLFunc {

    private SFunction<?, ?> column;

    private String str;

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
