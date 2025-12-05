package xyz.ytora.sql4j.caster;

import xyz.ytora.sql4j.caster.support.*;
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
public class DefaultTypeCaster extends TypeCasterRegister {

    {
        // 注册内置的类型转换器
        register(new TypePair(Date.class, LocalDate.class), new DateToLocalDateCaster());
        register(new TypePair(Time.class, LocalTime.class), new TimeToLocalTimeCaster());
        register(new TypePair(Timestamp.class, LocalDateTime.class), new TimestampToLocalDateTimeCaster());
        register(new TypePair(Long.class, String.class), new LongToStringCaster());
        register(new TypePair(Integer.class, String.class), new IntegerToStringCaster());
        register(new TypePair(Short.class, String.class), new ShortToStringCaster());
        register(new TypePair(Byte.class, String.class), new ByteToStringCaster());
        register(new TypePair(Long.class, Boolean.class), new LongToBoolCaster());
        register(new TypePair(Integer.class, Boolean.class), new IntegerToBoolCaster());
        register(new TypePair(Short.class, Boolean.class), new ShortToBoolCaster());
        register(new TypePair(Byte.class, Boolean.class), new ByteToBoolCaster());
    }

    /**
     * 将原始值转为目标类型
     * 由于读入数据库，JDBC会进行一个基础的转化，将读到的数据库原始数据转为JDBC数据
     * 所以这里的转化通常是： JDBC数据 -> Bean数据
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
        return caster != null ? caster.cast(sourceVal) : null;
    }

}
