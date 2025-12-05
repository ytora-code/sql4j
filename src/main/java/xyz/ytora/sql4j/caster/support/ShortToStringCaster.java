package xyz.ytora.sql4j.caster.support;

import xyz.ytora.sql4j.caster.Caster;

/**
 * Short -> String 转换器
 */
public class ShortToStringCaster implements Caster<Short, String> {

    @Override
    public String cast(Short sourceVal) {
        if (sourceVal == null) {
            return null;
        }
        return String.valueOf(sourceVal);
    }

    @Override
    public Class<Short> getSourceType() {
        return Short.class;
    }

    @Override
    public Class<String> getTargetType() {
        return String.class;
    }
}
