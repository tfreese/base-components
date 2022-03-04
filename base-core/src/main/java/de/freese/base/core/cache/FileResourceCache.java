// Created: 18.09.2014
package de.freese.base.core.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

        try
        {
            // Falls Verzeichnis nicht vorhanden -> erzeugen.
            if (!Files.exists(cacheDirectory))
            {
                Files.createDirectories(cacheDirectory);
            }
        }
        catch (final IOException ex)
        {
            throw new UncheckedIOException(ex);
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (final Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Erstellt ein neues {@link FileResourceCache} Object im Ordner "java.io.tmpdir/.javacache".
     */
    FileResourceCache()
    {
        this(Paths.get(System.getProperty("java.io.tmpdir"), ".javacache"));
    }

    /**
     * @see ResourceCache#clear()
     */
    @Override
    public void clear()
    {
        try
        {
            // Files.deleteIfExists(directory); // Funktioniert nur, wenn das Verzeichniss leer ist.

            Files.walkFileTree(this.cacheDirectory, new SimpleFileVisitor<>()
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
        catch (final IOException ex)
        {
            throw new UncheckedIOException(ex);
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (final Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see de.freese.base.core.cache.ResourceCache#getResource(java.net.URI)
     */
    @Override
    public Optional<InputStream> getResource(final URI uri)
    {
        String key = generateKey(uri);

        // Verzeichnisstruktur innerhalb des Cache-Verzeichnisses aufbauen.
        Path path = Paths.get(key.substring(0, 2));

        for (int subDir = 1; subDir < 3; subDir++)
        {
            path = path.resolve(key.substring(subDir * 2, (subDir * 2) + 2));
        }

        path = path.resolve(key);

        // Absoluter Path erstellen.
        path = this.cacheDirectory.resolve(path);

        try
        {
            if (!Files.exists(path))
            {
                Files.createDirectories(path.getParent());
            }

            try (InputStream inputStream = toInputStream(uri))
            {
                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            }

            return Optional.of(Files.newInputStream(path, StandardOpenOption.READ));
        }
        catch (final IOException ex)
        {
            throw new UncheckedIOException(ex);
        }
        catch (RuntimeException ex)
        {
            throw ex;
        }
        catch (final Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
}
