package xyz.ytora.sql4j.caster.support;

import xyz.ytora.sql4j.caster.Caster;

/**
 * Integer -> Boolean 转换器
 */
public class IntegerToBoolCaster implements Caster<Integer, Boolean> {

    @Override
    public Boolean cast(Integer sourceVal) {
        if (sourceVal == null) {
            return null;
        }
        return sourceVal != 0;
    }

    @Override
    public Class<Integer> getSourceType() {
        return Integer.class;
    }

    @Override
    public Class<Boolean> getTargetType() {
        return Boolean.class;
    }
}
