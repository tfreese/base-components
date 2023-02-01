// Created: 07.06.2020
package de.freese.base.resourcemap;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import de.freese.base.resourcemap.cache.NoOpResourceCache;
import de.freese.base.resourcemap.cache.ResourceCache;
import de.freese.base.resourcemap.cache.SingleResourceCache;
import de.freese.base.resourcemap.converter.BooleanResourceConverter;
import de.freese.base.resourcemap.converter.ByteResourceConverter;
import de.freese.base.resourcemap.converter.ColorResourceConverter;
import de.freese.base.resourcemap.converter.DimensionResourceConverter;
import de.freese.base.resourcemap.converter.DoubleResourceConverter;
import de.freese.base.resourcemap.converter.EmptyBorderResourceConverter;
import de.freese.base.resourcemap.converter.FloatResourceConverter;
import de.freese.base.resourcemap.converter.FontResourceConverter;
import de.freese.base.resourcemap.converter.IconResourceConverter;
import de.freese.base.resourcemap.converter.ImageResourceConverter;
import de.freese.base.resourcemap.converter.InsetsResourceConverter;
import de.freese.base.resourcemap.converter.IntegerResourceConverter;
import de.freese.base.resourcemap.converter.KeyStrokeResourceConverter;
import de.freese.base.resourcemap.converter.LongResourceConverter;
import de.freese.base.resourcemap.converter.PointResourceConverter;
import de.freese.base.resourcemap.converter.RectangleResourceConverter;
import de.freese.base.resourcemap.converter.ResourceConverter;
import de.freese.base.resourcemap.converter.ShortResourceConverter;
import de.freese.base.resourcemap.converter.UriResourceConverter;
import de.freese.base.resourcemap.converter.UrlResourceConverter;
import de.freese.base.resourcemap.provider.ResourceProvider;

/**
 * Default cache: {@link SingleResourceCache}<br>
 * Configuration Example:
 *
 * <pre>
 * <code>
 * ResourceMap rootMap = ResourceMapBuilder.create()
 *      .resourceProvider(new ResourceBundleProvider())
 *      .defaultConverters()
 *      .cacheDisabled()
 *      .bundleName("parentTest")
 *      .addChild()
 *          .bundleName("bundles/test1")
 *          .addChild()
 *              .bundleName("bundles/test2")
 *              .cacheDisabled()
 *              .done()
 *          .done()
 *      .build();
 * </code>
 * </pre>
 *
 * @author Thomas Freese
 */
public final class ResourceMapBuilder
{
    public static ResourceMapBuilder create()
    {
        return new ResourceMapBuilder(null);
    }

    private final List<ResourceMapBuilder> childBuilders = new ArrayList<>();

    private final ResourceMapBuilder parentBuilder;

    private String bundleName;

    private Map<Class<?>, ResourceConverter<?>> converters;

    private ResourceCache resourceCache;

    private ResourceProvider resourceProvider;

    private ResourceMapBuilder(final ResourceMapBuilder parentBuilder)
    {
        super();

        this.parentBuilder = parentBuilder;
    }

    public ResourceMapBuilder addChild()
    {
        return new ResourceMapBuilder(this);
    }

    public ResourceMap build()
    {
        Objects.requireNonNull(this.bundleName, "bundleName required");

        if (this.bundleName.length() == 0)
        {
            throw new IllegalArgumentException("bundleName is empty");
        }

        DefaultResourceMap resourceMap = new DefaultResourceMap(this.bundleName);
        resourceMap.setResourceProvider(this.resourceProvider);
        resourceMap.setResourceCache(this.resourceCache != null ? this.resourceCache : new SingleResourceCache());
        resourceMap.setConverters(this.converters);

        for (ResourceMapBuilder childBuilder : this.childBuilders)
        {
            ResourceMap child = childBuilder.build();

            resourceMap.addChild((DefaultResourceMap) child);
            ((DefaultResourceMap) child).setParent(resourceMap);
        }

        return resourceMap;
    }

    public ResourceMap buildAndLoad(final Locale locale)
    {
        ResourceMap resourceMap = build();
        resourceMap.load(locale);

        return resourceMap;
    }

    public ResourceMapBuilder bundleName(final String bundleName)
    {
        this.bundleName = Objects.requireNonNull(bundleName, "bundleName required");

        return this;
    }

    public ResourceMapBuilder cache(final ResourceCache resourceCache)
    {
        this.resourceCache = Objects.requireNonNull(resourceCache, "resourceCache required");

        return this;
    }

    public ResourceMapBuilder cacheDisabled()
    {
        return cache(NoOpResourceCache.getInstance());
    }

    public ResourceMapBuilder converter(final Class<?> type, final ResourceConverter<?> converter)
    {
        Objects.requireNonNull(type, "type required");
        Objects.requireNonNull(converter, "converter required");

        if (this.converters == null)
        {
            this.converters = new HashMap<>();
        }

        this.converters.put(type, converter);

        return this;
    }

    /**
     * Optional for Children, the Parent one's will be taken.
     */
    public ResourceMapBuilder defaultConverters()
    {
        converter(Boolean.class, new BooleanResourceConverter("true", "on", "yes", "1"));
        converter(boolean.class, new BooleanResourceConverter("true", "on", "yes", "1"));
        converter(Byte.class, new ByteResourceConverter());
        converter(byte.class, this.converters.get(Byte.class));

        converter(Color.class, new ColorResourceConverter());

        converter(Dimension.class, new DimensionResourceConverter());
        converter(Double.class, new DoubleResourceConverter());
        converter(double.class, this.converters.get(Double.class));

        converter(EmptyBorder.class, new EmptyBorderResourceConverter());

        converter(Float.class, new FloatResourceConverter());
        converter(float.class, this.converters.get(Float.class));
        converter(Font.class, new FontResourceConverter());

        converter(Icon.class, new IconResourceConverter());
        converter(ImageIcon.class, this.converters.get(Icon.class));
        converter(Image.class, new ImageResourceConverter());
        converter(BufferedImage.class, this.converters.get(Image.class));
        converter(Integer.class, new IntegerResourceConverter());
        converter(int.class, this.converters.get(Integer.class));
        converter(Insets.class, new InsetsResourceConverter());

        converter(KeyStroke.class, new KeyStrokeResourceConverter());

        converter(Long.class, new LongResourceConverter());
        converter(long.class, this.converters.get(Long.class));

        converter(Point.class, new PointResourceConverter());

        converter(Rectangle.class, new RectangleResourceConverter());

        converter(Short.class, new ShortResourceConverter());
        converter(short.class, this.converters.get(Short.class));

        converter(URL.class, new UrlResourceConverter());
        converter(URI.class, new UriResourceConverter());

        return this;
    }

    /**
     * Child-Builder ends.
     */
    public ResourceMapBuilder done()
    {
        return this.parentBuilder.addChild(this);
    }

    /**
     * Optional for Children: Default = parent#getResourceProvider
     */
    public ResourceMapBuilder resourceProvider(final ResourceProvider resourceProvider)
    {
        this.resourceProvider = Objects.requireNonNull(resourceProvider, "resourceProvider required");

        return this;
    }

    private ResourceMapBuilder addChild(final ResourceMapBuilder childBuilder)
    {
        this.childBuilders.add(childBuilder);

        return this;
    }
}
