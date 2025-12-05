package xyz.ytora.sql4j.caster.support;

import xyz.ytora.sql4j.caster.Caster;

/**
 * Long -> Boolean 转换器
 */
public class LongToBoolCaster implements Caster<Long, Boolean> {

    @Override
    public Boolean cast(Long sourceVal) {
        if (sourceVal == null) {
            return null;
        }
        return sourceVal != 0;
    }

    @Override
    public Class<Long> getSourceType() {
        return Long.class;
    }

    @Override
    public Class<Boolean> getTargetType() {
        return Boolean.class;
    }
}
