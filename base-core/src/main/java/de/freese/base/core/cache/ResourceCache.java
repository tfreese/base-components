// Created: 08.11.2014
package de.freese.base.core.cache;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * @author Thomas Freese
 */
public interface ResourceCache {
    void clear();

    InputStream getResource(URI uri) throws Exception;

    default InputStream getResource(final URL url) throws Exception {
        return getResource(url.toURI());
    }
}
