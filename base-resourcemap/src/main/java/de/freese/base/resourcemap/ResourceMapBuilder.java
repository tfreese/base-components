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

import de.freese.base.resourcemap.cache.NoOpResourceMapCache;
import de.freese.base.resourcemap.cache.ResourceMapCache;
import de.freese.base.resourcemap.cache.SingleResourceMapCache;
import de.freese.base.resourcemap.converter.BooleanStringResourceConverter;
import de.freese.base.resourcemap.converter.ByteStringResourceConverter;
import de.freese.base.resourcemap.converter.ColorStringResourceConverter;
import de.freese.base.resourcemap.converter.DimensionStringResourceConverter;
import de.freese.base.resourcemap.converter.DoubleStringResourceConverter;
import de.freese.base.resourcemap.converter.EmptyBorderStringResourceConverter;
import de.freese.base.resourcemap.converter.FloatStringResourceConverter;
import de.freese.base.resourcemap.converter.FontStringResourceConverter;
import de.freese.base.resourcemap.converter.IconStringResourceConverter;
import de.freese.base.resourcemap.converter.ImageStringResourceConverter;
import de.freese.base.resourcemap.converter.InsetsStringResourceConverter;
import de.freese.base.resourcemap.converter.IntegerStringResourceConverter;
import de.freese.base.resourcemap.converter.KeyStrokeStringResourceConverter;
import de.freese.base.resourcemap.converter.LongStringResourceConverter;
import de.freese.base.resourcemap.converter.PointStringResourceConverter;
import de.freese.base.resourcemap.converter.RectangleStringResourceConverter;
import de.freese.base.resourcemap.converter.ResourceConverter;
import de.freese.base.resourcemap.converter.ShortStringResourceConverter;
import de.freese.base.resourcemap.converter.URIStringResourceConverter;
import de.freese.base.resourcemap.converter.URLStringResourceConverter;
import de.freese.base.resourcemap.provider.ResourceProvider;

/**
 * Default cache: {@link SingleResourceMapCache}<br>
 *
 * @author Thomas Freese
 */
public final class ResourceMapBuilder
{
    /**
     * @return {@link ResourceMapBuilder}
     */
    public static ResourceMapBuilder create()
    {
        return new ResourceMapBuilder(null);
    }

    /**
     *
     */
    private String bundleName;
    /**
     *
     */
    private ResourceMapCache cache;
    /**
     *
     */
    private final List<ResourceMapBuilder> childBuilders = new ArrayList<>();
    /**
    *
    */
    private final Map<Class<?>, ResourceConverter<?>> converters = new HashMap<>();
    /**
     *
     */
    private final ResourceMapBuilder parentBuilder;
    /**
     *
     */
    private ResourceProvider resourceProvider;

    /**
     * Erstellt ein neues {@link ResourceMapBuilder} Object.
     *
     * @param parentBuilder {@link ResourceMapBuilder}
     */
    private ResourceMapBuilder(final ResourceMapBuilder parentBuilder)
    {
        super();

        this.parentBuilder = parentBuilder;
    }

    /**
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder addChild()
    {
        return new ResourceMapBuilder(this);
    }

    /**
     * @param childBuilder {@link ResourceMapBuilder}
     *
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder addChild(final ResourceMapBuilder childBuilder)
    {
        this.childBuilders.add(childBuilder);

        return this;
    }

    /**
     * @return {@link ResourceMap}
     */
    public ResourceMap build()
    {
        Objects.requireNonNull(this.bundleName, "bundleName required");

        if (this.bundleName.trim().length() == 0)
        {
            throw new IllegalArgumentException("bundleName is empty");
        }

        DefaultResourceMap resourceMap = new DefaultResourceMap(this.bundleName.trim(), getResourceProvider(), getConverters(), getCache());

        for (ResourceMapBuilder childBuilder : this.childBuilders)
        {
            ResourceMap child = childBuilder.build();

            resourceMap.addChild((DefaultResourceMap) child);
            ((DefaultResourceMap) child).setParent(resourceMap);
        }

        return resourceMap;
    }

    /**
     * @param locale {@link Locale}
     *
     * @return {@link ResourceMap}
     */
    public ResourceMap buildAndLoad(final Locale locale)
    {
        ResourceMap resourceMap = build();
        resourceMap.load(locale);

        return resourceMap;
    }

    /**
     * @param bundleName String
     *
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder bundleName(final String bundleName)
    {
        this.bundleName = bundleName;

        return this;
    }

    /**
     * Disable cacheing.
     *
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder cacheDisabled()
    {
        return cacheObjects(NoOpResourceMapCache.getInstance());
    }

    /**
     * @param cache {@link ResourceMapCache}
     *
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder cacheObjects(final ResourceMapCache cache)
    {
        this.cache = Objects.requireNonNull(cache, "cache required");

        return this;
    }

    /**
     * @param type Class
     * @param converter {@link ResourceConverter}
     *
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder converter(final Class<?> type, final ResourceConverter<?> converter)
    {
        this.converters.put(type, converter);

        return this;
    }

    /**
     * Optional for Childs, the Parent ones will taken.
     *
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder defaultConverters()
    {
        converter(Boolean.class, new BooleanStringResourceConverter("true", "on", "yes", "1"));
        converter(boolean.class, new BooleanStringResourceConverter("true", "on", "yes", "1"));
        converter(Byte.class, new ByteStringResourceConverter());
        converter(byte.class, this.converters.get(Byte.class));

        converter(Color.class, new ColorStringResourceConverter());

        converter(Dimension.class, new DimensionStringResourceConverter());
        converter(Double.class, new DoubleStringResourceConverter());
        converter(double.class, this.converters.get(Double.class));

        converter(EmptyBorder.class, new EmptyBorderStringResourceConverter());

        converter(Float.class, new FloatStringResourceConverter());
        converter(float.class, this.converters.get(Float.class));
        converter(Font.class, new FontStringResourceConverter());

        converter(Icon.class, new IconStringResourceConverter());
        converter(ImageIcon.class, this.converters.get(Icon.class));
        converter(Image.class, new ImageStringResourceConverter());
        converter(BufferedImage.class, this.converters.get(Image.class));
        converter(Integer.class, new IntegerStringResourceConverter());
        converter(int.class, this.converters.get(Integer.class));
        converter(Insets.class, new InsetsStringResourceConverter());

        converter(KeyStroke.class, new KeyStrokeStringResourceConverter());

        converter(Long.class, new LongStringResourceConverter());
        converter(long.class, this.converters.get(Long.class));

        converter(Point.class, new PointStringResourceConverter());

        converter(Rectangle.class, new RectangleStringResourceConverter());

        converter(Short.class, new ShortStringResourceConverter());
        converter(short.class, this.converters.get(Short.class));

        converter(URL.class, new URLStringResourceConverter());
        converter(URI.class, new URIStringResourceConverter());

        return this;
    }

    /**
     * Child-Builder ends.
     *
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder done()
    {
        return this.parentBuilder.addChild(this);
    }

    /**
     * Optional for Childs: Default = parent#getResourceProvider
     *
     * @param resourceProvider {@link ResourceProvider}
     *
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder resourceProvider(final ResourceProvider resourceProvider)
    {
        this.resourceProvider = Objects.requireNonNull(resourceProvider, "resourceProvider required");

        return this;
    }

    /**
     * @return {@link ResourceMapCache}
     */
    protected ResourceMapCache getCache()
    {
        if (this.cache == null)
        {
            return new SingleResourceMapCache();
        }

        return this.cache;
    }

    /**
     * @return {@link ResourceMapCache}
     */
    protected Map<Class<?>, ResourceConverter<?>> getConverters()
    {
        Map<Class<?>, ResourceConverter<?>> map = new HashMap<>();

        if (this.parentBuilder != null)
        {
            // Take the parent ones.
            map.putAll(this.parentBuilder.getConverters());
        }

        // Add the own.
        map.putAll(this.converters);

        return map;
    }

    /**
     * @return {@link ResourceProvider}
     */
    protected ResourceProvider getResourceProvider()
    {
        if ((this.resourceProvider == null) && (this.parentBuilder != null))
        {
            this.resourceProvider = this.parentBuilder.getResourceProvider();
        }

        return this.resourceProvider;
    }
}
