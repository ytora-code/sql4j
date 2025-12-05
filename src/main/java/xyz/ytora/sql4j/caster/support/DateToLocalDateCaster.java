package xyz.ytora.sql4j.caster.support;

import xyz.ytora.sql4j.caster.Caster;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Date -> LocalDate 转换器
 */
public class DateToLocalDateCaster implements Caster<Date, LocalDate> {

    @Override
    public LocalDate cast(Date sourceVal) {
        if (sourceVal == null) {
            return null;
        }
        return sourceVal.toLocalDate();
    }

    @Override
    public Class<Date> getSourceType() {
        return Date.class;
    }

    @Override
    public Class<LocalDate> getTargetType() {
        return LocalDate.class;
    }
}
