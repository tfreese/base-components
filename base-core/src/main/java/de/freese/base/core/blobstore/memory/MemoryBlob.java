package de.freese.base.core.blobstore.memory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

import de.freese.base.core.blobstore.AbstractBlob;
import de.freese.base.core.blobstore.BlobId;

/**
 * @author Thomas Freese
 */
class MemoryBlob extends AbstractBlob
{
    /**
     *
     */
    private final byte[] data;

    /**
     * @param id {@link BlobId}
     * @param data byte[]
     */
    MemoryBlob(final BlobId id, final byte[] data)
    {
        super(id);

        this.data = Objects.requireNonNull(data, "data required");
    }

    @Override
    protected InputStream doGetInputStream() throws Exception
    {
        return new ByteArrayInputStream(data);
    }

    @Override
    protected long doGetLength() throws Exception
    {
        return data.length;
    }
}
