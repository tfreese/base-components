package de.freese.base.core.blobstore.memory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import de.freese.base.core.blobstore.AbstractBlob;
import de.freese.base.core.blobstore.BlobId;

/**
 * @author Thomas Freese
 */
class MemoryBlob extends AbstractBlob {
    private final byte[] bytes;

    MemoryBlob(final BlobId id, final byte[] bytes) {
        super(id);

        this.bytes = bytes;
    }

    @Override
    public InputStream getInputStream() throws Exception {
        if (bytes == null) {
            return InputStream.nullInputStream();
        }

        return new ByteArrayInputStream(bytes);
    }

    @Override
    public long getLength() throws Exception {
        if (bytes == null) {
            return -1L;
        }

        return bytes.length;
    }
}
