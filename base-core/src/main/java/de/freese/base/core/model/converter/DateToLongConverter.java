// Created: 19.03.2020
package de.freese.base.core.model.converter;

import java.util.Date;

/**
 * @author Thomas Freese
 */
public class DateToLongConverter extends AbstractConverter<Date, Long> {
    private static Date convertToSource(final long time) {
        return new Date(time);
    }

    private static long convertToTarget(final Date date) {
        return date.getTime();
    }

    public DateToLongConverter() {
        super(DateToLongConverter::convertToTarget, DateToLongConverter::convertToSource);
    }
}
