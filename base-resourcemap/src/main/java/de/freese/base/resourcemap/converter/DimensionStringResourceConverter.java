package de.freese.base.resourcemap.converter;

import java.awt.Dimension;
import java.util.List;

/**
 * {@link ResourceConverter} für {@link Dimension}.
 *
 * @author Thomas Freese
 */
public class DimensionStringResourceConverter extends AbstractResourceConverter<Dimension>
{
    /**
     * Erstellt ein neues {@link DimensionStringResourceConverter} Object.
     */
    public DimensionStringResourceConverter()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public Dimension convert(final String key, final String value)
    {
        List<Double> xy = parseDoubles(key, value, 2, "Invalid x,y Dimension string");

        Dimension dimension = new Dimension();
        dimension.setSize(xy.get(0).doubleValue(), xy.get(1).doubleValue());

        return dimension;
    }
}
