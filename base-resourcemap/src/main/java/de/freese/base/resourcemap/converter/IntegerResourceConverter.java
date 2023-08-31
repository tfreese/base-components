package de.freese.base.resourcemap.converter;

/**
 * @author Thomas Freese
 */
public class IntegerResourceConverter extends AbstractNumberResourceConverter<Integer> {
    @Override
    protected Integer convertString(final String value, final int radix) throws NumberFormatException {
        return (radix == -1) ? Integer.valueOf(value) : Integer.valueOf(value, radix);
    }
}
