package de.freese.base.swing.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.freese.base.core.regex.RegEx;

/**
 * Filtert eine Zeichenkette anhand eines regulären Ausdrucks.
 *
 * @author Thomas Freese
 */
public class StringExpressionFilter extends AbstractTextFieldFilter
{
    private final boolean ignoreCase;

    public StringExpressionFilter(final boolean ignoreCase)
    {
        super();

        this.ignoreCase = ignoreCase;
    }

    /**
     * @see de.freese.base.swing.filter.AbstractFilter#setFilterValue(java.lang.Object)
     */
    @Override
    public void setFilterValue(final Object filterValue)
    {
        if ((filterValue == null) || (filterValue.toString().length() == 0))
        {
            super.setFilterValue(null);
            return;
        }

        Pattern pattern = null;

        try
        {
            String regex = RegEx.getInstance().wildcardToRegEx(filterValue.toString().toLowerCase());
            pattern = Pattern.compile(regex);
        }
        catch (Exception ex)
        {
            getLogger().warn(ex.getMessage());
        }

        super.setFilterValue(pattern);
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

        if (object == null)
        {
            // Kein Object zum Filtern = weg damit
            return false;
        }

        Pattern pattern = (Pattern) getFilterValue();
        String text = object.toString();

        if ("".equals(text) && pattern.pattern().startsWith(EMPTY))
        {
            return true;
        }

        if ((!"".equals(text)) && pattern.pattern().startsWith(NOT_EMPTY))
        {
            return true;
        }

        if (this.ignoreCase)
        {
            Matcher m = pattern.matcher(text.toLowerCase());

            return m.matches();
        }

        Matcher m = pattern.matcher(text);

        return m.matches();
    }
}
