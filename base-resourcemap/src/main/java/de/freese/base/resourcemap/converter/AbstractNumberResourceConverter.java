package de.freese.base.resourcemap.converter;

/**
 * Basis {@link ResourceConverter} für Numbers.
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
        String[] splits = value.split("&"); // number ampersand radix
        int radix = (splits.length == 2) ? Integer.parseInt(splits[1]) : -1;

        return convertString(splits[0], radix);
    }

    /**
     * Konvertiert den String in eine Zahl.
     *
     * @param value String
     * @param radix int
     * @return {@link Number}
     * @throws NumberFormatException Falls was schief geht.
     */
    protected abstract T convertString(String value, int radix) throws NumberFormatException;
}
