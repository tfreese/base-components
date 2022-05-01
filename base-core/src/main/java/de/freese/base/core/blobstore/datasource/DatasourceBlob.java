package de.freese.base.core.blobstore.datasource;

import java.io.InputStream;
import java.util.Objects;

import de.freese.base.core.blobstore.AbstractBlob;
import de.freese.base.core.blobstore.BlobId;

/**
 * @author Thomas Freese
 */
class DatasourceBlob extends AbstractBlob
{
    /**
     *
     */
    private final DatasourceBlobStore blobStore;

    /**
     * @param id {@link BlobId}
     * @param blobStore {@link DatasourceBlobStore}
     */
    DatasourceBlob(final BlobId id, DatasourceBlobStore blobStore)
    {
        super(id);

        this.blobStore = Objects.requireNonNull(blobStore, "blobStore required");
    }

    @Override
    protected InputStream doGetInputStream() throws Exception
    {
        String sql = "select BLOB from BLOB_STORE where URI = ?";

        // TODO
        return null;
    }

    @Override
    protected long doGetLength() throws Exception
    {
        String sql = "select LENGTH from BLOB_STORE where URI = ?";

        // TODO
        return 0;
    }
}
