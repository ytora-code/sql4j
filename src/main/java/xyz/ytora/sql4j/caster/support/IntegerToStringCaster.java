package xyz.ytora.sql4j.caster.support;

import xyz.ytora.sql4j.caster.Caster;

/**
 * Integer -> String 转换器
 */
public class IntegerToStringCaster implements Caster<Integer, String> {

    @Override
    public String cast(Integer sourceVal) {
        if (sourceVal == null) {
            return null;
        }
        return String.valueOf(sourceVal);
    }

    @Override
    public Class<Integer> getSourceType() {
        return Integer.class;
    }

    @Override
    public Class<String> getTargetType() {
        return String.class;
    }
}
