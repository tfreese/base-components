package de.freese.base.core.blobstore.datasource;

import java.io.InputStream;
import java.util.Objects;

import de.freese.base.core.blobstore.AbstractBlob;
import de.freese.base.core.blobstore.BlobId;

/**
 * @author Thomas Freese
 */
class DatasourceBlob extends AbstractBlob {
    private final DatasourceBlobStore blobStore;

    private long length = -1;

    DatasourceBlob(final BlobId id, DatasourceBlobStore blobStore) {
        super(id);

        this.blobStore = Objects.requireNonNull(blobStore, "blobStore required");
    }

    @Override
    public InputStream getInputStream() throws Exception {
        return blobStore.inputStream(getId());
    }

    @Override
    public long getLength() throws Exception {
        if (length < 0) {
            length = blobStore.length(getId());
        }

        return length;
    }
}
