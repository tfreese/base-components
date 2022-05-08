// Created: 18.09.2014
package de.freese.base.core.cache;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link ResourceCache}, der die Daten auf der Festplatte ablegt.
 *
 * @author Thomas Freese
 */
public class FileResourceCache extends AbstractResourceCache
{
    /**
     *
     */
    private final Path cacheDirectory;

    /**
     * Erstellt ein neues {@link FileResourceCache} Object.
     *
     * @param cacheDirectory {@link Path}
     */
    public FileResourceCache(final Path cacheDirectory)
    {
        super();

        this.cacheDirectory = Objects.requireNonNull(cacheDirectory, "cacheDirectory required");
    }

    /**
     * @see ResourceCache#clear()
     */
    @Override
    public void clear()
    {
        try
        {
            // Files.deleteIfExists(directory); // Funktioniert nur, wenn das Verzeichnis leer ist.

            Files.walkFileTree(getCacheDirectory(), new SimpleFileVisitor<>()
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
        catch (final Exception ex)
        {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    /**
     * @see de.freese.base.core.cache.ResourceCache#getResource(java.net.URI)
     */
    @Override
    public Optional<InputStream> getResource(final URI uri)
    {
        String key = generateKey(uri);

        Path path = getCacheDirectory();

        // Verzeichnisstruktur innerhalb des Cache-Verzeichnisses aufbauen.
        //        for (int i = 0; i < 3; i++)
        //        {
        //            path = path.resolve(key.substring(i * 2, (i * 2) + 2));
        //        }

        path = path.resolve(key);

        try
        {
            if (!Files.exists(path))
            {
                Files.createDirectories(path.getParent());

                try (InputStream inputStream = toInputStream(uri))
                {
                    Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            return Optional.of(Files.newInputStream(path, StandardOpenOption.READ));
        }
        catch (final Exception ex)
        {
            getLogger().error(ex.getMessage(), ex);

            return Optional.empty();
        }
    }

    /**
     * @return Path
     */
    protected Path getCacheDirectory()
    {
        return this.cacheDirectory;
    }
}
