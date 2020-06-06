package de.freese.base.resourcemap.converter;

/**
 * Resourceconverter fuer Floats.
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
     * @see de.freese.base.resourcemap.converter.AbstractNumberResourceConverter#parseString(java.lang.String, int)
     */
    @Override
    protected Float parseString(final String key, final int radix) throws NumberFormatException
    {
        return Float.valueOf(key.replaceAll(",", "."));
    }
}
