package de.freese.base.resourcemap.converter;

/**
 * {@link ResourceConverter} für Floats.
 *
 * @author Thomas Freese
 */
public class FloatStringResourceConverter extends AbstractNumberResourceConverter<Float>
{
    /**
     * Erstellt ein neues {@link FloatStringResourceConverter} Object.
     */
    public FloatStringResourceConverter()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.converter.AbstractNumberResourceConverter#convertString(java.lang.String, int)
     */
    @Override
    protected Float convertString(final String value, final int radix) throws NumberFormatException
    {
        return Float.valueOf(value.replace(",", "."));
    }
}
