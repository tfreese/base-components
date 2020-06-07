package de.freese.base.resourcemap.converter;

import java.awt.Point;
import java.util.List;

/**
 * {@link ResourceConverter} für {@link Point}.
 *
 * @author Thomas Freese
 */
public class PointStringResourceConverter extends AbstractResourceConverter<Point>
{
    /**
     * Erstellt ein neues {@link PointStringResourceConverter} Object.
     */
    public PointStringResourceConverter()
    {
        super();
    }

    /**
     * @see de.freese.base.resourcemap.converter.ResourceConverter#convert(java.lang.String, java.lang.String)
     */
    @Override
    public Point convert(final String key, final String value)
    {
        List<Double> xy = parseDoubles(key, value, 2, "Invalid x,y Point string");

        Point point = new Point();
        point.setLocation(xy.get(0).doubleValue(), xy.get(1).doubleValue());

        return point;
    }
}
