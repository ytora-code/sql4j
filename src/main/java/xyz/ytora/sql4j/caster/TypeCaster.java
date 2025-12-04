package xyz.ytora.sql4j.caster;

import xyz.ytora.sql4j.caster.support.DateToLocalDateCaster;
import xyz.ytora.sql4j.caster.support.TimeToLocalTimeCaster;
import xyz.ytora.sql4j.caster.support.TimestampToLocalDateTimeCaster;
import xyz.ytora.ytool.convert.TypePair;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 类型转换器
 */
public class TypeCaster extends TypeCasterRegister {

    {
        register(new TypePair(Date.class, LocalDate.class), new DateToLocalDateCaster());
        register(new TypePair(Time.class, LocalTime.class), new TimeToLocalTimeCaster());
        register(new TypePair(Timestamp.class, LocalDateTime.class), new TimestampToLocalDateTimeCaster());
    }

    /**
     * 将原始值转为目标类型
     * @param sourceVal 原始值
     * @param targetType 目标类型
     * @return 目标值
     * @param <T> 原始类型
     * @param <S> 目标类型
     */
    @SuppressWarnings("unchecked")
    public <T, S> T cast(S sourceVal, Class<T> targetType) {
        if (sourceVal == null) {
            return null;
        }

        // 如果目标类型是枚举
        if (targetType.isEnum()) {
            T[] enumConstants = targetType.getEnumConstants();
            // 如果是数字
            if (sourceVal instanceof Number) {
                int index = ((Number) sourceVal).intValue();
                if (index >= 0 && index < enumConstants.length) {
                    return enumConstants[index];
                }
            }
            // 如果是字符串
            else if (sourceVal.getClass().equals(String.class)) {
                for (T enumConstant : enumConstants) {
                    Enum<?> e = (Enum<?>) enumConstant;
                    if (e.name().equalsIgnoreCase((String) sourceVal)) {
                        return (T) e;
                    }
                }
            }
        }

        // 尝试调用自定义的类型转换器
        TypePair typePair = new TypePair(sourceVal.getClass(), targetType);
        Caster<S, T> caster = getCaster(typePair);
        return caster != null ? caster.cast(sourceVal, targetType) : null;
    }

}
