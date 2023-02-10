package de.freese.base.core.blobstore.memory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import de.freese.base.core.blobstore.AbstractBlobStore;
import de.freese.base.core.blobstore.Blob;
import de.freese.base.core.blobstore.BlobId;

/**
 * @author Thomas Freese
 */
public class MemoryBlobStore extends AbstractBlobStore {
    private final Map<BlobId, byte[]> cache = new HashMap<>();

    @Override
    public OutputStream create(final BlobId id) throws Exception {
        return new ByteArrayOutputStream() {
            @Override
            public void close() throws IOException {
                super.close();

                cache.put(id, toByteArray());
            }
        };
    }

    @Override
    public void create(final BlobId id, final InputStream inputStream) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            inputStream.transferTo(baos);

            baos.flush();

            cache.put(id, baos.toByteArray());
        }
    }

    @Override
    public void delete(final BlobId id) throws Exception {
        cache.remove(id);
    }

    @Override
    public boolean exists(final BlobId id) throws Exception {
        return cache.containsKey(id);
    }

    @Override
    protected Blob doGet(final BlobId id) throws Exception {
        return new MemoryBlob(id, cache.get(id));
    }
}
