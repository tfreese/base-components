package de.freese.base.resourcemap.converter;

/**
 * @author Thomas Freese
 */
public class ByteResourceConverter extends AbstractNumberResourceConverter<Byte> {
    @Override
    protected Byte convertString(final String value, final int radix) throws NumberFormatException {
        return (radix == -1) ? Byte.valueOf(value) : Byte.valueOf(value, radix);
    }
}
