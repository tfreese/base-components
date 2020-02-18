package de.freese.base.swing.filter;

import de.freese.base.core.model.NameProvider;

/**
 * Filter fuer ein {@link NameProvider}.
 *
 * @author Thomas Freese
 */
public class NameProviderFilter extends StringExpressionFilter
{
    /**
     * Creates a new {@link NameProviderFilter} object.
     */
    public NameProviderFilter()
    {
        super(true);
    }

    /**
     * @see de.freese.base.swing.filter.StringExpressionFilter#test(java.lang.Object)
     */
    @Override
    public boolean test(final Object object)
    {
        NameProvider nameProvider = (NameProvider) object;

        return super.test(nameProvider.getName());
    }
}
