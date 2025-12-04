package xyz.ytora.sql4j.func.support;

import xyz.ytora.sql4j.func.SQLFunc;
import xyz.ytora.sql4j.sql.AliasRegister;

/**
 * 不是 SQL 中的函数，该类用于将字符串直接拼接到 SQL 中
 * <pre>
 *     与 Wrapper 区别：Wrapper 会加单引号，Raw直接拼接
 * <pre/>
 */
public class Raw implements SQLFunc {

    private final String rawStr;

    private String as;

    public Raw(String rawStr) {
        this.rawStr = rawStr;
    }

    public static Raw of(String rawStr) {
        return new Raw(rawStr);
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
        // 该方法无需使用aliasRegister，有个空实现就行
    }

    @Override
    public String getValue() {
        return rawStr;
    }
}
