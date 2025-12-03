package xyz.ytora.sql4j.caster.support;

import xyz.ytora.sql4j.caster.Caster;

import java.sql.Time;
import java.time.LocalTime;

/**
 * Time -> LocalTime 转换器
 */
public class TimeToLocalTimeCaster implements Caster<Time, LocalTime> {

    @Override
    public LocalTime cast(Time sourceVal, Class<LocalTime> targetType) {
        if (sourceVal == null) {
            return null;
        }
        return sourceVal.toLocalTime();
    }
}
