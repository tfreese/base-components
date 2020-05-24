/**
 * Created: 08.11.2014
 */
package de.freese.base.core.cache;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;

/**
 * Interface für einen Resource-Cache.<br>
 * Wenn diese nicht vorhanden ist, wird über die {@link URL} nachgeladen.
 *
 * @author Thomas Freese
 */
public interface ResourceCache extends Function<URL, Optional<InputStream>>
{
    /**
     * @see java.util.function.Function#apply(java.lang.Object)
     */
    @Override
    public default Optional<InputStream> apply(final URL url)
    {
        return getResource(url);
    }

    /**
     * Leert den Cache.
     */
    public void clear();

    /**
     * Laden der Resource, wenn nicht vorhanden.
     *
     * @param url String; file://...; http://...
     * @return {@link Optional}
     * @throws MalformedURLException Falls was schief geht.
     */
    public default Optional<InputStream> getResource(final String url) throws MalformedURLException
    {
        return getResource(new URL(url));
    }

    /**
     * Laden der Resource, wenn nicht vorhanden..
     *
     * @param uri {@link URI}
     * @return {@link Optional}
     * @throws MalformedURLException Falls was schief geht.
     */
    public default Optional<InputStream> getResource(final URI uri) throws MalformedURLException
    {
        return getResource(uri.toURL());
    }

    /**
     * Laden der Resource, wenn nicht vorhanden.
     *
     * @param url {@link URL}
     * @return {@link Optional}
     */
    public Optional<InputStream> getResource(final URL url);
}
