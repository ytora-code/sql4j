package xyz.ytora.sql4j.caster.support;

import xyz.ytora.sql4j.caster.Caster;

/**
 * Byte -> String 转换器
 */
public class ByteToStringCaster implements Caster<Byte, String> {

    @Override
    public String cast(Byte sourceVal) {
        if (sourceVal == null) {
            return null;
        }
        return String.valueOf(sourceVal);
    }

    @Override
    public Class<Byte> getSourceType() {
        return Byte.class;
    }

    @Override
    public Class<String> getTargetType() {
        return String.class;
    }
}
