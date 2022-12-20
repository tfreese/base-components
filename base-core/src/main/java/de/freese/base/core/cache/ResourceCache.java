// Created: 08.11.2014
package de.freese.base.core.cache;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Thomas Freese
 */
public interface ResourceCache extends Function<URL, Optional<InputStream>>
{
    /**
     * @see java.util.function.Function#apply(java.lang.Object)
     */
    @Override
    default Optional<InputStream> apply(final URL url)
    {
        return getResource(url);
    }

    void clear();

    Optional<InputStream> getResource(final URI uri);

    default Optional<InputStream> getResource(final URL url)
    {
        try
        {
            return getResource(url.toURI());
        }
        catch (URISyntaxException ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
