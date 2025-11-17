package org.ytor.sql4j.caster.support;

import org.ytor.sql4j.caster.Caster;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Timestamp -> LocalDateTime 转换器
 */
public class TimestampToLocalDateTimeCaster implements Caster<Timestamp, LocalDateTime> {

    @Override
    public LocalDateTime cast(Timestamp sourceVal, Class<LocalDateTime> targetType) {
        if (sourceVal == null) {
            return null;
        }
        return sourceVal.toLocalDateTime();
    }
}
