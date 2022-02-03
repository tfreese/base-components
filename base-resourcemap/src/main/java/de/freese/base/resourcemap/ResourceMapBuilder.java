// Created: 07.06.2020
package de.freese.base.resourcemap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.freese.base.resourcemap.cache.NoOpResourceMapCache;
import de.freese.base.resourcemap.cache.ResourceMapCache;
import de.freese.base.resourcemap.cache.SimpleResourceMapCache;
import de.freese.base.resourcemap.cache.StaticResourceMapCache;
import de.freese.base.resourcemap.provider.ResourceBundleProvider;
import de.freese.base.resourcemap.provider.ResourceProvider;

/**
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
    private final List<ResourceMap> childs = new ArrayList<>();
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
     * @param child {@link ResourceMap}
     *
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder addChild(final ResourceMap child)
    {
        this.childs.add(child);

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

        DefaultResourceMap resourceMap = new DefaultResourceMap(this.bundleName.trim(), this.resourceProvider);
        resourceMap.setCache(getCache());

        for (ResourceMap child : this.childs)
        {
            resourceMap.addChild(child);
            ((DefaultResourceMap) child).setParent(resourceMap);
        }

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
     * Default: {@link SimpleResourceMapCache}
     *
     * @param cache {@link ResourceMapCache}
     *
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder cache(final ResourceMapCache cache)
    {
        this.cache = Objects.requireNonNull(cache, "cache required");

        return this;
    }

    /**
     * Default: {@link NoOpResourceMapCache}
     *
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder cacheNoOp()
    {
        return cache(NoOpResourceMapCache.getInstance());
    }

    /**
     * Default: {@link StaticResourceMapCache}
     *
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder cacheStatic()
    {
        return cache(StaticResourceMapCache.getInstance());
    }

    /**
     * Child-Builder beendet.
     *
     * @return ResourceMapBuilder
     */
    public ResourceMapBuilder done()
    {
        return this.parentBuilder.addChild(build());
    }

    /**
     * Für Parent: Default = {@link ResourceBundleProvider}<br>
     * Für Childs optional: Default = parent#getResourceProvider
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
        if ((this.cache == null) && (this.parentBuilder != null))
        {
            this.cache = this.parentBuilder.getCache();
        }

        if (this.cache == null)
        {
            return new SimpleResourceMapCache();
        }

        return this.cache;
    }
}
