package xyz.ytora.sql4j.caster.support;

import xyz.ytora.sql4j.caster.Caster;

/**
 * Short -> Boolean 转换器
 */
public class ShortToBoolCaster implements Caster<Short, Boolean> {

    @Override
    public Boolean cast(Short sourceVal) {
        if (sourceVal == null) {
            return null;
        }
        return sourceVal != 0;
    }

    @Override
    public Class<Short> getSourceType() {
        return Short.class;
    }

    @Override
    public Class<Boolean> getTargetType() {
        return Boolean.class;
    }
}
