package de.freese.base.resourcemap.converter;

import java.awt.Insets;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class InsetsResourceConverter extends AbstractResourceConverter<Insets> {
    @Override
    public Insets convert(final String key, final String value) {
        List<Double> tlbr = parseDoubles(key, value, 4, "Invalid top,left,bottom,right Insets string");

        return new Insets(tlbr.get(0).intValue(), tlbr.get(1).intValue(), tlbr.get(2).intValue(), tlbr.get(3).intValue());
    }
}
