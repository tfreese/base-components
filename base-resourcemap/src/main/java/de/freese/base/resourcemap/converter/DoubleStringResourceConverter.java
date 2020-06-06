package de.freese.base.resourcemap.converter;

/**
 * Resourceconverter fuer Doubles.
 *
 * @author Thomas Freese
 */
public class DoubleStringResourceConverter extends AbstractNumberResourceConverter<Double>
{
    /**
     * Erstellt ein neues {@link DoubleStringResourceConverter} Object.
     */
    public DoubleStringResourceConverter()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.converter.AbstractNumberResourceConverter#parseString(java.lang.String, int)
     */
    @Override
    protected Double parseString(final String key, final int radix) throws NumberFormatException
    {
        return Double.valueOf(key.replaceAll(",", "."));
    }
}
