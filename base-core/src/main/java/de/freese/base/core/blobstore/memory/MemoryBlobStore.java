package de.freese.base.core.blobstore.memory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import de.freese.base.core.blobstore.AbstractBlobStore;
import de.freese.base.core.blobstore.Blob;
import de.freese.base.core.blobstore.BlobId;
import de.freese.base.core.function.ThrowingConsumer;

/**
 * @author Thomas Freese
 */
public class MemoryBlobStore extends AbstractBlobStore
{
    /**
     *
     */
    private final Map<BlobId, byte[]> cache = new HashMap<>();

    @Override
    protected void doCreate(final BlobId id, final ThrowingConsumer<OutputStream, Exception> consumer) throws Exception
    {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            consumer.accept(baos);

            baos.flush();

            cache.put(id, baos.toByteArray());
        }
    }

    @Override
    protected void doCreate(final BlobId id, final InputStream inputStream) throws Exception
    {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            inputStream.transferTo(baos);

            baos.flush();

            cache.put(id, baos.toByteArray());
        }
    }

    @Override
    protected void doDelete(final BlobId id) throws Exception
    {
        cache.remove(id);
    }

    @Override
    protected boolean doExists(final BlobId id) throws Exception
    {
        return cache.containsKey(id);
    }

    @Override
    protected Blob doGet(final BlobId id) throws Exception
    {
        return new MemoryBlob(id, cache.get(id));
    }
}
