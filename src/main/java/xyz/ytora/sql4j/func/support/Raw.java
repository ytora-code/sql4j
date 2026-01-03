package xyz.ytora.sql4j.func.support;

import xyz.ytora.sql4j.func.SFunction;
import xyz.ytora.sql4j.func.SQLFunc;
import xyz.ytora.sql4j.sql.AliasRegister;
import xyz.ytora.sql4j.util.Sql4jUtil;

/**
 * 不是 SQL 中的函数，该类用于将字符串直接拼接到 SQL 中
 * <pre>
 *     与 Wrapper 区别：Wrapper 会加单引号，Raw直接拼接
 * <pre/>
 */
public class Raw implements SQLFunc {

    private String rawStr;

    private SFunction<?, ?> colFn;

    private AliasRegister aliasRegister;

    private String as;

    public Raw(String rawStr) {
        this.rawStr = rawStr;
    }

    public Raw(SFunction<?, ?> colFn) {
        this.colFn = colFn;
    }

    public static Raw of(String rawStr) {
        return new Raw(rawStr);
    }

    public static Raw of(SFunction<?, ?> colFn) {
        return new Raw(colFn);
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
        if (rawStr != null) {
            return rawStr;
        }
        return Sql4jUtil.parseColumn(colFn, aliasRegister);
    }
}
