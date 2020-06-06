package de.freese.base.resourcemap.converter;

/**
 * Resourceconverter fuer Integers.
 *
 * @author Thomas Freese
 */
public class IntegerStringResourceConverter extends AbstractNumberResourceConverter<Integer>
{
    /**
     * Erstellt ein neues {@link IntegerStringResourceConverter} Object.
     */
    public IntegerStringResourceConverter()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.converter.AbstractNumberResourceConverter#parseString(java.lang.String, int)
     */
    @Override
    protected Integer parseString(final String key, final int radix) throws NumberFormatException
    {
        return (radix == -1) ? Integer.decode(key) : Integer.valueOf(key, radix);
    }
}
