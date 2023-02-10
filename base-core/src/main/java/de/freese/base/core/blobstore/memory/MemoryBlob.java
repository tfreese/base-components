package de.freese.base.core.blobstore.memory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

import de.freese.base.core.blobstore.AbstractBlob;
import de.freese.base.core.blobstore.BlobId;

/**
 * @author Thomas Freese
 */
class MemoryBlob extends AbstractBlob {
    private final byte[] bytes;

    MemoryBlob(final BlobId id, final byte[] bytes) {
        super(id);

        this.bytes = Objects.requireNonNull(bytes, "bytes required");
    }

    @Override
    public InputStream getInputStream() throws Exception {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public long getLength() throws Exception {
        return bytes.length;
    }
}
