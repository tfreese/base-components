package de.freese.base.resourcemap.converter;

import java.awt.Rectangle;
import java.util.List;

/**
 * IResourceConverter fuer ein {@link Rectangle}.
 *
 * @author Thomas Freese
 */
public class RectangleStringResourceConverter extends AbstractResourceConverter<Rectangle>
{
    /**
     * Erstellt ein neues {@link RectangleStringResourceConverter} Object.
     */
    public RectangleStringResourceConverter()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public Rectangle convert(final String key, final String value)
    {
        List<Double> xywh = parseDoubles(value, 4, "Invalid x,y,width,height Rectangle string");

        Rectangle rectangle = new Rectangle();
        rectangle.setFrame(xywh.get(0).doubleValue(), xywh.get(1).doubleValue(), xywh.get(2).doubleValue(), xywh.get(3).doubleValue());

        return rectangle;
    }
}
