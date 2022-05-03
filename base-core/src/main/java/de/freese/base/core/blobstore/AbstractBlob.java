// Created: 18.09.2019
package de.freese.base.core.blobstore;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic-Implementation of a {@link Blob}s.
 *
 * @author Thomas Freese
 */
public abstract class AbstractBlob implements Blob
{
    /**
     *
     */
    private final BlobId id;
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param id {@link BlobId}
     */
    protected AbstractBlob(final BlobId id)
    {
        super();

        this.id = Objects.requireNonNull(id, "id required");
    }

    @Override
    public final BlobId getId()
    {
        return this.id;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        return getId().toString();
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }
}
