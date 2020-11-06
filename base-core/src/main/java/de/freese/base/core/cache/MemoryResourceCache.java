/**
 * Created: 27.07.2016
 */

package de.freese.base.core.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Cache Implementierung, die Daten im Speicher ablegt.
 *
 * @author Thomas Freese
 */
public class MemoryResourceCache extends AbstractResourceCache
{
    /**
     *
     */
    private final Map<String, byte[]> map;

    /**
     * Erstellt ein neues {@link MemoryResourceCache} Object.
     */
    public MemoryResourceCache()
    {
        super();

        this.map = new TreeMap<>();
    }

    /**
     * @see ResourceCache#clear()
     */
    @Override
    public void clear()
    {
        this.map.clear();
    }

    /**
     * @see de.freese.base.core.cache.ResourceCache#getResource(java.net.URL)
     */
    @Override
    public Optional<InputStream> getResource(final URL url)
    {
        String key = generateKey(url);
        byte[] content = this.map.get(key);

        if (content == null)
        {
            try
            {
                int size = (int) getContentLength(url);

                try (InputStream inputStream = loadInputStream(url);
                     ByteArrayOutputStream baos = new ByteArrayOutputStream(size))
                {
                    byte[] buffer = new byte[4096];
                    // long count = 0;
                    int n = 0;

                    while ((n = inputStream.read(buffer)) != -1)
                    {
                        baos.write(buffer, 0, n);
                        // count += n;
                    }

                    content = baos.toByteArray();
                    this.map.put(key, content);
                }
            }
            catch (RuntimeException ex)
            {
                throw ex;
            }
            catch (final Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }

        if (content == null)
        {
            return Optional.ofNullable(null);
        }

        return Optional.ofNullable(new ByteArrayInputStream(content));
    }
}
