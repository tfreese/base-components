// Created: 18.09.2014
package de.freese.base.core.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * @author Thomas Freese
 */
public class MemoryResourceCache extends AbstractResourceCache
{
    private final Map<URI, byte[]> map;

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
     * @see de.freese.base.core.cache.ResourceCache#getResource(java.net.URI)
     */
    @Override
    public Optional<InputStream> getResource(final URI uri)
    {
        byte[] content = this.map.get(uri);

        if (content == null)
        {
            try
            {
                //int size = (int) getContentLength(uri);
                int size = 1024;

                try (InputStream inputStream = toInputStream(uri);
                     ByteArrayOutputStream baos = new ByteArrayOutputStream(size))
                {
                    inputStream.transferTo(baos);
                    // byte[] buffer = new byte[4096];
                    // // long count = 0;
                    // int n = 0;
                    //
                    // while ((n = inputStream.read(buffer)) != -1)
                    // {
                    // baos.write(buffer, 0, n);
                    // // count += n;
                    // }

                    content = baos.toByteArray();

                    this.map.put(uri, content);
                }
            }
            catch (final Exception ex)
            {
                getLogger().error(ex.getMessage(), ex);

                return Optional.empty();
            }
        }

        return Optional.of(new ByteArrayInputStream(content));
    }
}
