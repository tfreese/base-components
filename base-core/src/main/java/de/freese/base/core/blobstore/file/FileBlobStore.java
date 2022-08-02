// Created: 18.09.2019
package de.freese.base.core.blobstore.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import de.freese.base.core.blobstore.AbstractBlobStore;
import de.freese.base.core.blobstore.Blob;
import de.freese.base.core.blobstore.BlobId;
import de.freese.base.core.blobstore.BlobStore;

/**
 * {@link BlobStore} Implementierung für eine Datei.
 *
 * @author Thomas Freese
 */
public class FileBlobStore extends AbstractBlobStore
{
    /**
     *
     */
    private final Path basePath;

    /**
     * Erstellt ein neues {@link FileBlobStore} Object.
     *
     * @param basePath {@link Path}
     *
     * @throws IOException Falls was schiefgeht.
     */
    public FileBlobStore(final Path basePath) throws IOException
    {
        super();

        this.basePath = Objects.requireNonNull(basePath, "basePath required");

        // if (!Files.isWritable(this.basePath))
        // {
        // String msg = "Path not writeable: " + uri;
        //
        // getLogger().error(msg);
        // throw new IllegalArgumentException(msg);
        // }

        if (Files.notExists(this.basePath))
        {
            Files.createDirectories(this.basePath);
        }
    }

    @Override
    public OutputStream create(final BlobId id) throws Exception
    {
        Path path = toContentPath(id);

        Files.createDirectories(path.getParent());

        return Files.newOutputStream(path);
    }

    @Override
    public void create(final BlobId id, final InputStream inputStream) throws Exception
    {
        Path path = toContentPath(id);

        Files.createDirectories(path.getParent());

        Files.copy(inputStream, path);
    }

    @Override
    public void delete(final BlobId id) throws Exception
    {
        Path path = toContentPath(id);

        if (Files.exists(path))
        {
            Files.delete(path);
        }
    }

    @Override
    public boolean exists(final BlobId id) throws Exception
    {
        Path path = toContentPath(id);

        return Files.exists(path);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.basePath.toString();
    }

    /**
     * @param id {@link BlobId}
     *
     * @return {@link Path}
     */
    Path toContentPath(final BlobId id)
    {
        URI uri = id.getUri();
        String uriString = uri.toString();

        uriString = uriString.replace(':', '/');
        uriString = uriString.replace('?', '/');
        uriString = uriString.replace('&', '/');
        uriString = uriString.replace(' ', '_');
        uriString = uriString.replace("%20", "_");

        while (uriString.contains("//"))
        {
            uriString = uriString.replace("//", "/");
        }

        return this.basePath.resolve(uriString);

        //        byte[] uriBytes = uriString.getBytes(StandardCharsets.UTF_8);
        //        byte[] digest = getMessageDigest().digest(uriBytes);
        //        String hex = HexFormat.of().withUpperCase().formatHex(uriBytes);
        //
        //        Path path = this.basePath;
        //
        //        // Verzeichnisstruktur innerhalb des Cache-Verzeichnisses aufbauen.
        //        for (int i = 0; i < 3; i++)
        //        {
        //            path = path.resolve(hex.substring(i * 2, (i * 2) + 2));
        //        }
        //
        //        return this.basePath.resolve(hex);
    }

    @Override
    protected Blob doGet(final BlobId id) throws Exception
    {
        return new FileBlob(id, this);
    }
}
