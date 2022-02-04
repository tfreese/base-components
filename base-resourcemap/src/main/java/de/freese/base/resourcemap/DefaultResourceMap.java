package de.freese.base.resourcemap;

import de.freese.base.resourcemap.provider.ResourceProvider;

/**
 * Default-{@link ResourceMap} zum laden und verarbeiten lokalisierter Texte.
 *
 * @author Thomas Freese
 */
public class DefaultResourceMap extends AbstractResourceMap
{
    /**
     * Erstellt ein neues {@link DefaultResourceMap} Object.
     *
     * @param bundleName String
     * @param resourceProvider {@link ResourceProvider}
     */
    protected DefaultResourceMap(final String bundleName, final ResourceProvider resourceProvider)
    {
        super(bundleName);

        setResourceProvider(resourceProvider);
    }
}
