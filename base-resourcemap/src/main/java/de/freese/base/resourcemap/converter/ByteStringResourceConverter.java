package de.freese.base.resourcemap.converter;

/**
 * {@link ResourceConverter} für Bytes.
 *
 * @author Thomas Freese
 */
public class ByteStringResourceConverter extends AbstractNumberResourceConverter<Byte>
{
    /**
     * @see de.freese.base.resourcemap.converter.AbstractNumberResourceConverter#convertString(java.lang.String, int)
     */
    @Override
    protected Byte convertString(final String value, final int radix) throws NumberFormatException
    {
        return (radix == -1) ? Byte.valueOf(value) : Byte.valueOf(value, radix);
    }
}
