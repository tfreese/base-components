// Created: 02.02.23
package de.freese.base.resourcemap.converter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.invoke.MethodType;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

/**
 * @author Thomas Freese
 */
public final class ResourceConverters {
    public static ResourceConverters ofDefaults() {
        final ResourceConverters resourceConverters = new ResourceConverters();
        resourceConverters.customize(ResourceConverters::defaultConverters);

        return resourceConverters;
    }

    private static void defaultConverters(final Map<Class<?>, ResourceConverter<?>> converters) {
        converters.put(Boolean.class, new BooleanResourceConverter("true", "on", "yes", "1"));
        converters.put(BufferedImage.class, new ImageResourceConverter());
        converters.put(Byte.class, new ByteResourceConverter());

        converters.put(Color.class, new ColorResourceConverter());

        converters.put(Dimension.class, new DimensionResourceConverter());
        converters.put(Double.class, new DoubleResourceConverter());

        converters.put(EmptyBorder.class, new EmptyBorderResourceConverter());

        converters.put(Float.class, new FloatResourceConverter());
        converters.put(Font.class, new FontResourceConverter());

        converters.put(Icon.class, new IconResourceConverter());
        converters.put(ImageIcon.class, new IconResourceConverter());
        converters.put(Image.class, new ImageResourceConverter());
        converters.put(Integer.class, new IntegerResourceConverter());
        converters.put(Insets.class, new InsetsResourceConverter());

        converters.put(KeyStroke.class, new KeyStrokeResourceConverter());

        converters.put(Long.class, new LongResourceConverter());

        converters.put(Point.class, new PointResourceConverter());

        converters.put(Rectangle.class, new RectangleResourceConverter());

        converters.put(Short.class, new ShortResourceConverter());

        converters.put(URL.class, new UrlResourceConverter());
        converters.put(URI.class, new UriResourceConverter());
    }

    private final Map<Class<?>, ResourceConverter<?>> converters = new HashMap<>();

    private ResourceConverters() {
        super();
    }

    public void customize(final Consumer<Map<Class<?>, ResourceConverter<?>>> converterCustomizer) {
        converterCustomizer.accept(converters);
    }

    @SuppressWarnings("unchecked")
    public <T> ResourceConverter<T> getConverter(final Class<T> type) {
        ResourceConverter<?> resourceConverter = converters.get(type);

        if (resourceConverter == null && type.isPrimitive()) {
            // MethodType..unwrap()
            final Class<T> wrapperType = (Class<T>) MethodType.methodType(type).wrap().returnType();

            resourceConverter = converters.get(wrapperType);
        }

        return (ResourceConverter<T>) resourceConverter;
    }
}
