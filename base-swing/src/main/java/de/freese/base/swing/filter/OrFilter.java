package de.freese.base.swing.filter;

/**
 * Filter zum verknuepfen mehrerer Filter zu einer OR-Bedingung.<br>
 * Dieser Filter reagiert auf die Aenderungen seiner Filter und leitet das Event weiter.
 *
 * @author Thomas Freese
 */
public class OrFilter extends AndFilter
{
    /**
     * @see de.freese.base.swing.filter.FilterCondition#test(java.lang.Object)
     */
    @Override
    public boolean test(final Object object)
    {
        for (FilterCondition filterCondition : getFilters())
        {
            if (filterCondition.test(object))
            {
                return true;
            }
        }

        return false;
    }
}
