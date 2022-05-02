package de.freese.base.core.blobstore.memory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

import de.freese.base.core.blobstore.AbstractBlob;
import de.freese.base.core.blobstore.BlobId;
import de.freese.base.core.function.ThrowingConsumer;

/**
 * @author Thomas Freese
 */
class MemoryBlob extends AbstractBlob
{
    /**
     *
     */
    private final byte[] bytes;

    /**
     * @param id {@link BlobId}
     * @param bytes byte[]
     */
    MemoryBlob(final BlobId id, final byte[] bytes)
    {
        super(id);

        this.bytes = Objects.requireNonNull(bytes, "bytes required");
    }

    @Override
    protected void doConsumeInputStream(final ThrowingConsumer<InputStream, Exception> consumer) throws Exception
    {
        try (InputStream inputStream = new ByteArrayInputStream(bytes))
        {
            consumer.accept(inputStream);
        }
    }

    @Override
    protected long doGetLength() throws Exception
    {
        return bytes.length;
    }
}
