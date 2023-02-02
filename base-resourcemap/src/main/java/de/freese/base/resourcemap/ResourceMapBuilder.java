// Created: 07.06.2020
package de.freese.base.resourcemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.freese.base.resourcemap.cache.NoOpResourceCache;
import de.freese.base.resourcemap.cache.ResourceCache;
import de.freese.base.resourcemap.cache.SingleResourceCache;
import de.freese.base.resourcemap.converter.ResourceConverter;
import de.freese.base.resourcemap.converter.ResourceConverters;
import de.freese.base.resourcemap.provider.ResourceProvider;

/**
 * Default cache: {@link SingleResourceCache}<br>
 * Configuration Example:
 *
 * <pre>
 * <code>
 * ResourceMap rootMap = ResourceMapBuilder.create()
 *      .resourceProvider(new ResourceBundleProvider())
 *      .converter(MyClass.class, new MyClassResourceConverter())
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

        ResourceConverters resourceConverters = ResourceConverters.ofDefaults();

        if (converters != null && !converters.isEmpty())
        {
            resourceConverters.customize(map -> map.putAll(converters));
        }

        DefaultResourceMap resourceMap = new DefaultResourceMap(this.bundleName);
        resourceMap.setResourceProvider(this.resourceProvider);
        resourceMap.setResourceCache(this.resourceCache != null ? this.resourceCache : new SingleResourceCache());
        resourceMap.setResourceConverters(resourceConverters);

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
