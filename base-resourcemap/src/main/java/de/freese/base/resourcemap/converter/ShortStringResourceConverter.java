package de.freese.base.resourcemap.converter;

/**
 * {@link ResourceConverter} für Shorts.
 *
 * @author Thomas Freese
 */
public class ShortStringResourceConverter extends AbstractNumberResourceConverter<Short>
{
    /**
     * @see de.freese.base.resourcemap.converter.AbstractNumberResourceConverter#convertString(java.lang.String, int)
     */
    @Override
    protected Short convertString(final String value, final int radix) throws NumberFormatException
    {
        return (radix == -1) ? Short.decode(value) : Short.valueOf(value, radix);
    }
}
