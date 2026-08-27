package de.freese.base.resourcemap.converter;

import java.util.List;

import javax.swing.border.EmptyBorder;

/**
 * @author Thomas Freese
 */
public class EmptyBorderResourceConverter extends AbstractResourceConverter<EmptyBorder> {
    @Override
    public EmptyBorder convert(final String key, final String value) {
        final List<Double> list = parseDoubles(key, value, 4, "Invalid top,left,bottom,right EmptyBorder string");

        return new EmptyBorder(list.get(0).intValue(), list.get(1).intValue(), list.get(2).intValue(), list.get(3).intValue());
    }
}
