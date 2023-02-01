package de.freese.base.resourcemap.converter;

/**
 * @author Thomas Freese
 */
public abstract class AbstractNumberResourceConverter<T extends Number> extends AbstractResourceConverter<T>
{
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

    protected abstract T convertString(String value, int radix) throws NumberFormatException;
}
