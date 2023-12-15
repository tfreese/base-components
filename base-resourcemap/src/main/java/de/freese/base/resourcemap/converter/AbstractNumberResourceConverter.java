package de.freese.base.resourcemap.converter;

/**
 * @author Thomas Freese
 */
public abstract class AbstractNumberResourceConverter<T extends Number> extends AbstractResourceConverter<T> {
    @Override
    public T convert(final String key, final String value) {
        final String[] splits = value.split("&"); // number ampersand radix
        final int radix = (splits.length == 2) ? Integer.parseInt(splits[1]) : -1;

        return convertString(splits[0], radix);
    }

    protected abstract T convertString(String value, int radix) throws NumberFormatException;
}
