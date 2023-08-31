package de.freese.base.resourcemap.converter;

import java.awt.Rectangle;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class RectangleResourceConverter extends AbstractResourceConverter<Rectangle> {
    @Override
    public Rectangle convert(final String key, final String value) {
        List<Double> xywh = parseDoubles(key, value, 4, "Invalid x,y,width,height Rectangle string");

        Rectangle rectangle = new Rectangle();
        rectangle.setFrame(xywh.get(0), xywh.get(1), xywh.get(2), xywh.get(3));

        return rectangle;
    }
}
