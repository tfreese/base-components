package de.freese.base.resourcemap.converter;

/**
 * {@link ResourceConverter} für Booleans.
 *
 * @author Thomas Freese
 */
public class BooleanStringResourceConverter extends AbstractResourceConverter<Boolean>
{
    /**
     *
     */
    private final String[] trueStrings;

    /**
     * Erstellt ein neues {@link BooleanStringResourceConverter} Object.
     *
     * @param trueStrings String, zb. true, on, yes, 1
     */
    public BooleanStringResourceConverter(final String...trueStrings)
    {
        super();

        this.trueStrings = trueStrings;
    }

    /**
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public Boolean convert(final String key, final String value)
    {
        String v = value.trim();

        for (String trueString : this.trueStrings)
        {
            if (v.equalsIgnoreCase(trueString))
            {
                return Boolean.TRUE;
            }
        }

        return Boolean.FALSE;
    }
}
