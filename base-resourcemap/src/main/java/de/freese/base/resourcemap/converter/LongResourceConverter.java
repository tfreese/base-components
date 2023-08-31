package de.freese.base.resourcemap.converter;

/**
 * @author Thomas Freese
 */
public class LongResourceConverter extends AbstractNumberResourceConverter<Long> {
    @Override
    protected Long convertString(final String value, final int radix) throws NumberFormatException {
        return (radix == -1) ? Long.valueOf(value) : Long.valueOf(value, radix);
    }
}
