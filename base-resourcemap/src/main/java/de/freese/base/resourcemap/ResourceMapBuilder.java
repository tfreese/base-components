/**
 * Created: 07.06.2020
 */

package de.freese.base.resourcemap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import de.freese.base.resourcemap.cache.PerEachResourceMapCache;
import de.freese.base.resourcemap.cache.ResourceMapCache;
import de.freese.base.resourcemap.cache.StaticResourceMapCache;
import de.freese.base.resourcemap.converter.ResourceConverter;
import de.freese.base.resourcemap.provider.ResourceBundleProvider;
import de.freese.base.resourcemap.provider.ResourceProvider;

/**
 * @author Thomas Freese
 */
public final class ResourceMapBuilder
{
    /**
     * @param bundleName String
     * @return {@link ResourceMapBuilder}
     */
    public static ResourceMapBuilder create(final String bundleName)
    {
        return new ResourceMapBuilder(bundleName);
    }

    /**
     *
     */
    private final String bundleName;

    /**
     *
     */
    private ResourceMapCache cache;

    /**
     *
     */
    private ClassLoader classLoader;

    /**
    *
    */
    private ResourceMap parent;

    /**
    *
    */
    private final Map<Class<?>, ResourceConverter<?>> resourceConverters = new HashMap<>();

    /**
     *
     */
    private ResourceProvider resourceProvider;

    /**
     * Erstellt ein neues {@link ResourceMapBuilder} Object.
     *
     * @param bundleName String
     */
    private ResourceMapBuilder(final String bundleName)
    {
        super();

        this.bundleName = Objects.requireNonNull(bundleName, "bundleName required");
    }

    /**
     * @return {@link ResourceMap}
     */
    public ResourceMap build()
    {
        if (this.bundleName.trim().length() == 0)
        {
            throw new IllegalArgumentException("bundleName length = 0");
        }

        // Muss für Parent zwingend gesetzt werden !
        if ((this.classLoader == null) && (this.parent == null))
        {
            this.classLoader = Thread.currentThread().getContextClassLoader();
        }

        if (this.cache == null)
        {
            this.cache = new PerEachResourceMapCache();
        }

        DefaultResourceMap resourceMap = null;

        try
        {
            resourceMap = new DefaultResourceMap(this.bundleName.trim(), this.parent, this.classLoader, this.resourceProvider);

            resourceMap.setCache(this.cache);

            this.resourceConverters.forEach(resourceMap::addResourceConverter);
        }
        catch (Exception ex)
        {
            if (ex instanceof RuntimeException)
            {
                throw (RuntimeException) ex;
            }

            throw new RuntimeException(ex);
        }

        return resourceMap;
    }

    /**
     * Default: {@link PerEachResourceMapCache}
     *
     * @param cache {@link ResourceMapCache}
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder cache(final ResourceMapCache cache)
    {
        this.cache = Objects.requireNonNull(cache, "cache required");

        return this;
    }

    /**
     * Default: {@link StaticResourceMapCache}
     *
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder cacheStatic()
    {
        this.cache = StaticResourceMapCache.getInstance();

        return this;
    }

    /**
     * Für Parent: Default = DefaultResourceMap.class.getClassLoader()<br>
     * Für Childs optional: Default = parent#getClassLoader
     *
     * @param classLoader {@link ClassLoader}
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder classLoader(final ClassLoader classLoader)
    {
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader required");

        return this;
    }

    /**
     * Hinzufügen eines neuen {@link ResourceConverter}s.<br>
     * Die Converter der Parent-ResourceMap vererben sich auf ihre Kinder.
     *
     * @param supportedType Class
     * @param converter {@link ResourceConverter}
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder converter(final Class<?> supportedType, final ResourceConverter<?> converter)
    {
        this.resourceConverters.put(supportedType, converter);

        return this;
    }

    /**
     * Optional
     *
     * @param parent {@link ResourceMap}
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder parent(final ResourceMap parent)
    {
        this.parent = Objects.requireNonNull(parent, "parent required");

        return this;
    }

    /**
     * Für Parent: Default = {@link ResourceBundleProvider}<br>
     * Für Childs optional: Default = parent#getResourceProvider
     *
     * @param resourceProvider {@link ResourceProvider}
     * @return {@link ResourceMapBuilder}
     */
    public ResourceMapBuilder resourceProvider(final ResourceProvider resourceProvider)
    {
        this.resourceProvider = Objects.requireNonNull(resourceProvider, "resourceProvider required");

        return this;
    }
}
