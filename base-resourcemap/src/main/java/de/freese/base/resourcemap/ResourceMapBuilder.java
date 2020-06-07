/**
 * Created: 07.06.2020
 */

package de.freese.base.resourcemap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import de.freese.base.resourcemap.converter.ResourceConverter;
import de.freese.base.resourcemap.provider.ResourceBundleProvider;
import de.freese.base.resourcemap.provider.ResourceProvider;

/**
 * @author Thomas Freese
 */
public class ResourceMapBuilder
{
    /**
     * @param baseName String
     * @return {@link ResourceMapBuilder}
     */
    public static ResourceMapBuilder create(final String baseName)
    {
        return new ResourceMapBuilder(baseName);
    }

    /**
     *
     */
    private final String baseName;

    /**
     *
     */
    private ClassLoader classLoader = null;

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
     * @param baseName String
     */
    private ResourceMapBuilder(final String baseName)
    {
        super();

        this.baseName = Objects.requireNonNull(baseName, "baseName required");
    }

    /**
     * @return {@link ResourceMap}
     */
    public ResourceMap build()
    {
        if (this.baseName.trim().length() == 0)
        {
            throw new IllegalArgumentException("baseName length = 0");
        }

        // Muss für Parent zwingend gesetzt werden !
        if ((this.classLoader == null) && (this.parent == null))
        {
            this.classLoader = DefaultResourceMap.class.getClassLoader();
        }

        DefaultResourceMap resourceMap = null;

        try
        {
            resourceMap = new DefaultResourceMap(this.baseName.trim(), this.parent, this.classLoader, this.resourceProvider);

            if (this.resourceConverters != null)
            {
                this.resourceConverters.forEach(resourceMap::addResourceConverter);
            }
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
