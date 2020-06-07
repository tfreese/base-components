package de.freese.base.resourcemap.converter;

import java.awt.Insets;
import java.util.List;

/**
 * {@link ResourceConverter} für {@link Insets}.
 *
 * @author Thomas Freese
 */
public class InsetsStringResourceConverter extends AbstractResourceConverter<Insets>
{
    /**
     * Erstellt ein neues {@link InsetsStringResourceConverter} Object.
     */
    public InsetsStringResourceConverter()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public Insets convert(final String key, final String value)
    {
        List<Double> tlbr = parseDoubles(key, value, 4, "Invalid top,left,bottom,right Insets string");

        return new Insets(tlbr.get(0).intValue(), tlbr.get(1).intValue(), tlbr.get(2).intValue(), tlbr.get(3).intValue());
    }
}
