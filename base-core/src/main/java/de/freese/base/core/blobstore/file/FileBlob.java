package de.freese.base.core.blobstore.file;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import de.freese.base.core.blobstore.AbstractBlob;
import de.freese.base.core.blobstore.Blob;
import de.freese.base.core.blobstore.BlobId;

/**
 * {@link Blob} Implementierung für eine Datei.
 *
 * @author Thomas Freese
 */
final class FileBlob extends AbstractBlob
{
    private final Path absolutePath;

    FileBlob(final BlobId id, final FileBlobStore store)
    {
        super(id);

        this.absolutePath = store.toContentPath(id);
    }

    @Override
    public InputStream getInputStream() throws Exception
    {
        return Files.newInputStream(this.absolutePath);
    }

    @Override
    public long getLength() throws Exception
    {
        return Files.size(this.absolutePath);
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString()
    {
        return this.absolutePath.toString();
    }

}
