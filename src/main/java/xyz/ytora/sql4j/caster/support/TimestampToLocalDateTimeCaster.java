package xyz.ytora.sql4j.caster.support;

import xyz.ytora.sql4j.caster.Caster;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Timestamp -> LocalDateTime 转换器
 */
public class TimestampToLocalDateTimeCaster implements Caster<Timestamp, LocalDateTime> {

    @Override
    public LocalDateTime cast(Timestamp sourceVal) {
        if (sourceVal == null) {
            return null;
        }
        return sourceVal.toLocalDateTime();
    }

    @Override
    public Class<Timestamp> getSourceType() {
        return Timestamp.class;
    }

    @Override
    public Class<LocalDateTime> getTargetType() {
        return LocalDateTime.class;
    }
}
