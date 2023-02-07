package de.freese.base.mvc.storage;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

/**
 * Util-Class for local file access.<br>
 * All operations are relative under the storage directory.
 *
 * @author Thomas Freese
 */
public final class LocalStorage
{
    private final Path storageDirectory;

    /**
     * Default: Paths.get(System.getProperty("java.io.tmpdir"), ".java-apps"))
     */
    public LocalStorage()
    {
        this(Paths.get(System.getProperty("java.io.tmpdir"), ".java-apps"));
    }

    public LocalStorage(final Path storageDirectory)
    {
        super();

        this.storageDirectory = Objects.requireNonNull(storageDirectory, "storageDirectory required");
        createDirectories(storageDirectory);
    }

    public void copy(final InputStream src, final OutputStream target) throws IOException
    {
        src.transferTo(target);
    }

    public Path createTemporaryFile(final String prefix, final String suffix) throws IOException
    {
        Path path = Files.createTempFile(getStorageDirectory(), prefix, suffix);

        File file = path.toFile();
        file.deleteOnExit();

        return path;
    }

    public void deleteDirectory(final Path relativePath) throws IOException
    {
        Path path = getAbsolutPath(relativePath);

        Files.walkFileTree(path, new SimpleFileVisitor<>()
        {
            /**
             * @see java.nio.file.SimpleFileVisitor#postVisitDirectory(java.lang.Object, java.io.IOException)
             */
            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException
            {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

            /**
             * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
             */
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException
            {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public boolean deleteFile(final Path relativePath) throws IOException
    {
        Path path = getAbsolutPath(relativePath);

        return Files.deleteIfExists(path);
    }

    public Path getAbsolutPath(final Path relativePath)
    {
        return getStorageDirectory().resolve(relativePath);
    }

    public InputStream getInputStream(final Path relativePath, final OpenOption... options) throws IOException
    {
        Path path = getAbsolutPath(relativePath);

        createDirectories(path.getParent());

        return new BufferedInputStream(Files.newInputStream(path, options));
    }

    public OutputStream getOutputStream(final Path relativePath, final OpenOption... options) throws IOException
    {
        Path path = getAbsolutPath(relativePath);

        createDirectories(path.getParent());

        return new BufferedOutputStream(Files.newOutputStream(path, options));
    }

    public Path getStorageDirectory()
    {
        return this.storageDirectory;
    }

    public void openPath(final Path relativePath) throws IOException
    {
        Desktop.getDesktop().open(getStorageDirectory().resolve(relativePath).toFile());
    }

    public String removeIllegalFileCharacters(final String fileName)
    {
        String internalName = fileName;

        if (internalName.indexOf('/') != -1)
        {
            internalName = internalName.replace("//", "/").replace("/", "-");
        }

        if (internalName.indexOf('\\') != -1)
        {
            internalName = internalName.replace("\\\\", "\\").replace("\\", "-");
        }

        return internalName;
    }

    public void save(final Path relativePath, final byte[] data, final OpenOption... options) throws Exception
    {
        Path path = getAbsolutPath(relativePath);

        createDirectories(path.getParent());

        Files.write(path, data, options);
    }

    private void createDirectories(final Path path)
    {
        if (!Files.exists(path))
        {
            try
            {
                Files.createDirectories(path);
            }
            catch (IOException ex)
            {
                throw new UncheckedIOException(ex);
            }
        }
    }
}
