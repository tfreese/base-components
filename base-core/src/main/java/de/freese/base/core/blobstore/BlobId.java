// Created: 18.09.2019
package de.freese.base.core.blobstore;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.util.Objects;

/**
 * Unique ID for a Blob from a {@link BlobStore}.<br>
 * <a href="https://github.com/sonatype/nexus-public/blob/master/components/nexus-blobstore-api">nexus-blobstore-api</a>
 *
 * @author Thomas Freese
 */
public class BlobId implements Serializable, Comparable<BlobId>
{
    @Serial
    private static final long serialVersionUID = -5581749917166864024L;

    private final URI uri;

    public BlobId(final URI uri)
    {
        super();

        this.uri = Objects.requireNonNull(uri, "uri required");
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final BlobId o)
    {
        return this.uri.compareTo(o.uri);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }

        if ((o == null) || (getClass() != o.getClass()))
        {
            return false;
        }

        BlobId blobId = (BlobId) o;

        return this.uri.equals(blobId.uri);
    }

    public URI getUri()
    {
        return this.uri;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return this.uri.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.uri.toString();
    }
}
