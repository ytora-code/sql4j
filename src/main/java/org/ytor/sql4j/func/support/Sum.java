package org.ytor.sql4j.func.support;

import org.ytor.sql4j.func.SFunction;
import org.ytor.sql4j.func.SQLFunc;
import org.ytor.sql4j.sql.AliasRegister;
import org.ytor.sql4j.util.LambdaUtil;

public class Sum implements SQLFunc {

    private SFunction<?, ?> column;

    private String str;

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
