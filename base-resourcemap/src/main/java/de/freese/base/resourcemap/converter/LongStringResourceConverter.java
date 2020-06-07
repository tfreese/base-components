package de.freese.base.resourcemap.converter;

/**
 * {@link ResourceConverter} für Longs.
 *
 * @author Thomas Freese
 */
public class LongStringResourceConverter extends AbstractNumberResourceConverter<Long>
{
    /**
     * Erstellt ein neues {@link LongStringResourceConverter} Object.
     */
    public LongStringResourceConverter()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.converter.AbstractNumberResourceConverter#convertString(java.lang.String, int)
     */
    @Override
    protected Long convertString(final String value, final int radix) throws NumberFormatException
    {
        return (radix == -1) ? Long.decode(value) : Long.valueOf(value, radix);
    }
}
