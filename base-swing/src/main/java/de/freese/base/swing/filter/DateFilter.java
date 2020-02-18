package de.freese.base.swing.filter;

import java.util.Date;

/**
 * Filter fuer {@link Date}s.
 *
 * @author Thomas Freese
 */
public class DateFilter extends AbstractFilter
{
    /**
     *
     */
    private final String dateFormat;

    /**
     * Erstellt ein neues {@link DateFilter} Object.
     *
     * @param dateFormat String; Format: %1$td.%1$tm.%1$tY %1$tH:%1$tM:%1$tS fuer dd.MM.YYYY hh:mm:ss
     */
    public DateFilter(final String dateFormat)
    {
        super();

        this.dateFormat = dateFormat;
    }

    /**
     * @see de.freese.base.swing.filter.FilterCondition#test(java.lang.Object)
     */
    @Override
    public boolean test(final Object object)
    {
        if (getFilterValue() == null)
        {
            return true;
        }

        Date filterDate = (Date) getFilterValue();
        Date date = (Date) object;

        String filterDateString = String.format(this.dateFormat, filterDate);
        String dateString = String.format(this.dateFormat, date);

        if (filterDateString.equals(dateString))
        {
            return true;
        }

        return false;
    }
}
