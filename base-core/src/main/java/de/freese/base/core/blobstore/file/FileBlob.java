package de.freese.base.core.blobstore.file;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import de.freese.base.core.blobstore.AbstractBlob;
import de.freese.base.core.blobstore.Blob;
import de.freese.base.core.blobstore.BlobId;
import de.freese.base.core.function.ThrowingConsumer;

/**
 * {@link Blob} Implementierung für eine Datei.
 *
 * @author Thomas Freese
 */
final class FileBlob extends AbstractBlob
{
    /**
     *
     */
    private final Path absolutePath;

    /**
     * Erstellt ein neues {@link FileBlob} Object.
     *
     * @param id {@link BlobId}
     * @param store {@link FileBlobStore}
     */
    FileBlob(final BlobId id, final FileBlobStore store)
    {
        super(id);

        this.absolutePath = store.toContentPath(id);
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        return this.absolutePath.toString();
    }

    @Override
    protected void doConsumeInputStream(final ThrowingConsumer<InputStream, Exception> consumer) throws Exception
    {
        try (InputStream inputStream = Files.newInputStream(this.absolutePath))
        {
            consumer.accept(inputStream);
        }
    }

    /**
     * @see AbstractBlob#doGetLength()
     */
    @Override
    protected long doGetLength() throws Exception
    {
        return Files.size(this.absolutePath);
    }
}
