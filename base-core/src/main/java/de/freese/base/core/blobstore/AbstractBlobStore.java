// Created: 18.09.2019
package de.freese.base.core.blobstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractBlobStore implements BlobStore {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Blob get(final BlobId id) throws Exception {
        if (!exists(id)) {
            return null;
        }

        return doGet(id);
    }

    protected abstract Blob doGet(BlobId id) throws Exception;

    protected Logger getLogger() {
        return this.logger;
    }
}
