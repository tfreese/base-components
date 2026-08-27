package de.freese.base.resourcemap.converter;

import java.awt.Rectangle;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class RectangleResourceConverter extends AbstractResourceConverter<Rectangle> {
    @Override
    public Rectangle convert(final String key, final String value) {
        final List<Double> list = parseDoubles(key, value, 4, "Invalid x,y,width,height Rectangle string");

        final Rectangle rectangle = new Rectangle();
        rectangle.setFrame(list.get(0), list.get(1), list.get(2), list.get(3));

        return rectangle;
    }
}
