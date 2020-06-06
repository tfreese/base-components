package de.freese.base.resourcemap.converter;

/**
 * Basis Resourceconverter fuer Numbers.
 *
 * @author Thomas Freese
 * @param <T> Konkreter konvertierter Typ
 */
public abstract class AbstractNumberResourceConverter<T extends Number> extends AbstractResourceConverter<T>
{
    /**
     * Erstellt ein neues {@link AbstractNumberResourceConverter} Object.
     */
    public AbstractNumberResourceConverter()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public T convert(final String key, final String value)
    {
        try
        {
            String[] splits = value.split("&"); // number ampersand radix
            int radix = (splits.length == 2) ? Integer.parseInt(splits[1]) : -1;

            return parseString(splits[0], radix);
        }
        catch (NumberFormatException ex)
        {
            throw new ResourceConverterException("invalid Type", key, ex);
        }
    }

    /**
     * Konvertiert den String in eine Zahl.
     * 
     * @param key String
     * @param radix int
     * @return {@link Number}
     * @throws NumberFormatException Falls was schief geht.
     */
    protected abstract T parseString(String key, int radix) throws NumberFormatException;
}
