package xyz.ytora.sql4j.caster.support;

import xyz.ytora.sql4j.caster.Caster;

/**
 * Byte -> Boolean 转换器
 */
public class ByteToBoolCaster implements Caster<Byte, Boolean> {

    @Override
    public Boolean cast(Byte sourceVal) {
        if (sourceVal == null) {
            return null;
        }
        return sourceVal != 0;
    }

    @Override
    public Class<Byte> getSourceType() {
        return Byte.class;
    }

    @Override
    public Class<Boolean> getTargetType() {
        return Boolean.class;
    }
}
