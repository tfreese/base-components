package de.freese.base.resourcemap.converter;

/**
 * {@link ResourceConverter} für Integers.
 *
 * @author Thomas Freese
 */
public class IntegerStringResourceConverter extends AbstractNumberResourceConverter<Integer>
{
    /**
     * @see de.freese.base.resourcemap.converter.AbstractNumberResourceConverter#convertString(java.lang.String, int)
     */
    @Override
    protected Integer convertString(final String value, final int radix) throws NumberFormatException
    {
        return (radix == -1) ? Integer.decode(value) : Integer.valueOf(value, radix);
    }
}
