package xyz.yangtong.sql4j.func.support;

import xyz.yangtong.sql4j.func.SFunction;
import xyz.yangtong.sql4j.func.SQLFunc;
import xyz.yangtong.sql4j.sql.AliasRegister;
import xyz.yangtong.sql4j.util.LambdaUtil;

/**
 * count 函数， count(1)、count(*)、count(id)
 */
public class Count implements SQLFunc {

    private SFunction<?, ?> column;

    private String str;

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
