package de.freese.base.resourcemap.converter;

import java.awt.Insets;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class InsetsResourceConverter extends AbstractResourceConverter<Insets> {
    @Override
    public Insets convert(final String key, final String value) {
        final List<Double> list = parseDoubles(key, value, 4, "Invalid top,left,bottom,right Insets string");

        return new Insets(list.get(0).intValue(), list.get(1).intValue(), list.get(2).intValue(), list.get(3).intValue());
    }
}
