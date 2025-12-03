package xyz.ytora.sql4j.func.support;

import xyz.ytora.sql4j.func.SQLFunc;
import xyz.ytora.sql4j.sql.AliasRegister;

public class Now implements SQLFunc {

    public static Now of() {
        return new Now();
    }

    @Override
    public void addAliasRegister(AliasRegister aliasRegister) {
        // 该方法无需使用aliasRegister，有个空实现就像
    }

    @Override
    public String getValue() {
        return "now()";
    }
}
