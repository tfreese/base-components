package de.freese.base.resourcemap.converter;

import java.awt.Dimension;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class DimensionResourceConverter extends AbstractResourceConverter<Dimension> {
    @Override
    public Dimension convert(final String key, final String value) {
        final List<Double> xy = parseDoubles(key, value, 2, "Invalid x,y Dimension string");

        final Dimension dimension = new Dimension();
        dimension.setSize(xy.get(0), xy.get(1));

        return dimension;
    }
}
