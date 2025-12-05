package xyz.ytora.sql4j.caster.support;

import xyz.ytora.sql4j.caster.Caster;

/**
 * Long -> LocalDate 转换器
 */
public class LongToStringCaster implements Caster<Long, String> {

    @Override
    public String cast(Long sourceVal) {
        if (sourceVal == null) {
            return null;
        }
        return String.valueOf(sourceVal);
    }

    @Override
    public Class<Long> getSourceType() {
        return Long.class;
    }

    @Override
    public Class<String> getTargetType() {
        return String.class;
    }
}
