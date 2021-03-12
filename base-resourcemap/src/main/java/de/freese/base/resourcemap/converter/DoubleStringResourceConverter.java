package de.freese.base.resourcemap.converter;

/**
 * {@link ResourceConverter} für Doubles.
 *
 * @author Thomas Freese
 */
public class DoubleStringResourceConverter extends AbstractNumberResourceConverter<Double>
{
    /**
     * @see de.freese.base.resourcemap.converter.AbstractNumberResourceConverter#convertString(java.lang.String, int)
     */
    @Override
    protected Double convertString(final String value, final int radix) throws NumberFormatException
    {
        return Double.valueOf(value.replace(",", "."));
    }
}
