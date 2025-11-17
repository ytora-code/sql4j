package org.ytor.sql4j.caster.support;

import org.ytor.sql4j.caster.Caster;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Date -> LocalDate 转换器
 */
public class DateToLocalDateCaster implements Caster<Date, LocalDate> {

    @Override
    public LocalDate cast(Date sourceVal, Class<LocalDate> targetType) {
        if (sourceVal == null) {
            return null;
        }
        return sourceVal.toLocalDate();
    }
}
