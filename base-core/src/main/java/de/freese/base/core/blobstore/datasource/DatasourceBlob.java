package de.freese.base.core.blobstore.datasource;

import java.io.InputStream;
import java.util.Objects;

import de.freese.base.core.blobstore.AbstractBlob;
import de.freese.base.core.blobstore.BlobId;
import de.freese.base.core.function.ThrowingConsumer;

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
     *
     */
    private long length = -1;

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
    protected void doConsumeInputStream(final ThrowingConsumer<InputStream, Exception> consumer) throws Exception
    {
        blobStore.readInputStream(getId(), consumer);
    }

    @Override
    protected long doGetLength() throws Exception
    {
        if (length < 0)
        {
            length = blobStore.getLength(getId());
        }

        return length;
    }
}
