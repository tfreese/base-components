package de.freese.base.resourcemap.converter;

import java.awt.Dimension;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class DimensionResourceConverter extends AbstractResourceConverter<Dimension> {
    /**
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public Dimension convert(final String key, final String value) {
        List<Double> xy = parseDoubles(key, value, 2, "Invalid x,y Dimension string");

        Dimension dimension = new Dimension();
        dimension.setSize(xy.get(0), xy.get(1));

        return dimension;
    }
}
