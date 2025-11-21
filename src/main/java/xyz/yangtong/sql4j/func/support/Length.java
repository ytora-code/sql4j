package xyz.yangtong.sql4j.func.support;

import xyz.yangtong.sql4j.func.SQLFunc;
import xyz.yangtong.sql4j.sql.AliasRegister;

/**
 * length(str)，返回字符串的长度
 */
public class Length implements SQLFunc {

    private AliasRegister aliasRegister;

    private final String str;

    public Length(String str) {
        this.str = str;
    }

    @Override
    public void addAliasRegister(AliasRegister aliasRegister) {
        this.aliasRegister = aliasRegister;
    }

    public static Length of(String str) {
        return new Length(str);
    }

    @Override
    public String getValue() {
        return "length(" + str + ")";
    }
}
