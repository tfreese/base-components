package de.freese.base.resourcemap.converter;

/**
 * @author Thomas Freese
 */
public class ShortResourceConverter extends AbstractNumberResourceConverter<Short> {
    @Override
    protected Short convertString(final String value, final int radix) throws NumberFormatException {
        return (radix == -1) ? Short.valueOf(value) : Short.valueOf(value, radix);
    }
}
