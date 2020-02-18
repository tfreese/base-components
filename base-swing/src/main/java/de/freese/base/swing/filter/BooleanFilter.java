package de.freese.base.swing.filter;

/**
 * Filter, die die Uebergebenden {@link Boolean} Objekte einer Tabellenspalte ausfiltert.
 *
 * @author Thomas Freese
 */
public class BooleanFilter extends AbstractFilter
{
    /**
     * Erstellt ein neues {@link BooleanFilter} Objekt.
     */
    public BooleanFilter()
    {
        super();
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

        Boolean value = (Boolean) object;

        return value.equals(getFilterValue());
    }
}
