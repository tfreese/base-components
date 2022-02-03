// Created: 03.02.2022
package de.freese.base.resourcemap.converter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

/**
 * @author Thomas Freese
 */
public final class ResourceConverters
{
    /**
    *
    */
    private static final Map<Class<?>, ResourceConverter<?>> CONVERTERS = new HashMap<>();

    /**
     *
     */
    static
    {
        addDefaults();
    }

    /**
     * @param type Class
     * @param converter {@link ResourceConverter}
     */
    public static void addConverter(final Class<?> type, final ResourceConverter<?> converter)
    {
        CONVERTERS.put(type, converter);
    }

    /**
     * @param <T> Type
     * @param type Class
     *
     * @return {@link ResourceConverter}
     */
    @SuppressWarnings("unchecked")
    public static <T> ResourceConverter<T> getConverter(final Class<T> type)
    {
        return (ResourceConverter<T>) CONVERTERS.get(type);
    }

    /**
    *
    */
    private static void addDefaults()
    {
        addConverter(Boolean.class, new BooleanStringResourceConverter("true", "on", "yes", "1"));
        addConverter(boolean.class, new BooleanStringResourceConverter("true", "on", "yes", "1"));
        addConverter(Byte.class, new ByteStringResourceConverter());
        addConverter(byte.class, getConverter(Byte.class));

        addConverter(Color.class, new ColorStringResourceConverter());

        addConverter(Dimension.class, new DimensionStringResourceConverter());
        addConverter(Double.class, new DoubleStringResourceConverter());
        addConverter(double.class, getConverter(Double.class));

        addConverter(EmptyBorder.class, new EmptyBorderStringResourceConverter());

        addConverter(Float.class, new FloatStringResourceConverter());
        addConverter(float.class, getConverter(Float.class));
        addConverter(Font.class, new FontStringResourceConverter());

        addConverter(Icon.class, new IconStringResourceConverter());
        addConverter(ImageIcon.class, getConverter(Icon.class));
        addConverter(Image.class, new ImageStringResourceConverter());
        addConverter(BufferedImage.class, getConverter(Image.class));
        addConverter(Integer.class, new IntegerStringResourceConverter());
        addConverter(int.class, getConverter(Integer.class));
        addConverter(Insets.class, new InsetsStringResourceConverter());

        addConverter(KeyStroke.class, new KeyStrokeStringResourceConverter());

        addConverter(Long.class, new LongStringResourceConverter());
        addConverter(long.class, getConverter(Long.class));

        addConverter(Point.class, new PointStringResourceConverter());

        addConverter(Rectangle.class, new RectangleStringResourceConverter());

        addConverter(Short.class, new ShortStringResourceConverter());
        addConverter(short.class, getConverter(Short.class));

        addConverter(URL.class, new URLStringResourceConverter());
        addConverter(URI.class, new URIStringResourceConverter());
    }

    /**
     * Erstellt ein neues {@link ResourceConverters} Object.
     */
    private ResourceConverters()
    {
        super();
    }
}
