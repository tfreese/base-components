// Created: 18.09.2019
package de.freese.base.core.blobstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic-Implementation of a {@link BlobStore}s.
 *
 * @author Thomas Freese
 */
public abstract class AbstractBlobStore implements BlobStore
{
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Blob get(final BlobId id) throws Exception
    {
        if (!exists(id))
        {
            return null;
        }

        return doGet(id);
    }

    /**
     * @param id {@link BlobId}
     *
     * @return {@link Blob}
     *
     * @throws Exception Falls was schief geht.
     */
    protected abstract Blob doGet(final BlobId id) throws Exception;

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }
}
