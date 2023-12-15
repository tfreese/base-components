package de.freese.base.resourcemap.converter;

import java.awt.Point;
import java.util.List;

/**
 * @author Thomas Freese
 */
public class PointResourceConverter extends AbstractResourceConverter<Point> {
    @Override
    public Point convert(final String key, final String value) {
        final List<Double> xy = parseDoubles(key, value, 2, "Invalid x,y Point string");

        final Point point = new Point();
        point.setLocation(xy.get(0), xy.get(1));

        return point;
    }
}
