package de.freese.base.resourcemap.converter;

import java.util.List;

import javax.swing.border.EmptyBorder;

/**
 * @author Thomas Freese
 */
public class EmptyBorderResourceConverter extends AbstractResourceConverter<EmptyBorder> {
    @Override
    public EmptyBorder convert(final String key, final String value) {
        final List<Double> tlbr = parseDoubles(key, value, 4, "Invalid top,left,bottom,right EmptyBorder string");

        return new EmptyBorder(tlbr.get(0).intValue(), tlbr.get(1).intValue(), tlbr.get(2).intValue(), tlbr.get(3).intValue());
    }
}
