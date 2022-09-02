// Created: 19.03.2020
package de.freese.base.core.model.converter;

import java.util.Date;

/**
 * @author Thomas Freese
 */
public class DateToLongConverter extends AbstractConverter<Date, Long>
{
    /**
     * @param time long
     *
     * @return {@link Date}
     */
    private static Date convertToSource(final long time)
    {
        return new Date(time);
    }

    /**
     * @param date {@link Date}
     *
     * @return long
     */
    private static long convertToTarget(final Date date)
    {
        return date.getTime();
    }

    /**
     * Erstellt ein neues {@link DateToLongConverter} Object.
     */
    public DateToLongConverter()
    {
        super(DateToLongConverter::convertToTarget, DateToLongConverter::convertToSource);
    }
}
