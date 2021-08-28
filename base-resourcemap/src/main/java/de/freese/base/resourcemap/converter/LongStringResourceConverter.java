package de.freese.base.resourcemap.converter;

/**
 * {@link ResourceConverter} für Longs.
 *
 * @author Thomas Freese
 */
public class LongStringResourceConverter extends AbstractNumberResourceConverter<Long>
{
    /**
     * @see de.freese.base.resourcemap.converter.AbstractNumberResourceConverter#convertString(java.lang.String, int)
     */
    @Override
    protected Long convertString(final String value, final int radix) throws NumberFormatException
    {
        return (radix == -1) ? Long.valueOf(value) : Long.valueOf(value, radix);
    }
}
