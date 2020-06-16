/**
 * Created: 18.09.2014
 */
package de.freese.base.core.cache;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
import java.util.stream.Stream;

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
     * Erstellt ein neues {@link FileResourceCache} Object im Ordner "java.io.tmpdir/.javacache".
     */
    public FileResourceCache()
    {
        // System.getProperty("java.io.tmpdir") + File.separator + ".javacache" + File.separator;
        this(Paths.get(System.getProperty("java.io.tmpdir"), ".javacache"));
    }

    /**
     * Erstellt ein neues {@link FileResourceCache} Object.
     *
     * @param cacheDirectory {@link File}
     */
    public FileResourceCache(final File cacheDirectory)
    {
        this(cacheDirectory.toPath());
    }

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

            // final File file = new File(CACHE_DIR);
            //
            // if (!file.exists())
            // {
            // file.mkdirs();
            // }

            // Files.deleteIfExists(directory); // Funktioniert nur, wenn das Verzeichniss leer ist.
            if (!Files.exists(cacheDirectory))
            {
                Files.createDirectories(cacheDirectory);
            }
        }
        catch (final Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see ResourceCache#clear()
     */
    @Override
    public void clear()
    {
        try
        {
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
        catch (Exception ex)
        {
            // getLogger().error(null, ex);

            if (ex instanceof RuntimeException)
            {
                throw (RuntimeException) ex;
            }

            throw new RuntimeException(ex);
        }
    }

    /**
     * @see de.freese.base.core.cache.ResourceCache#getResource(java.net.URL)
     */
    @Override
    public Optional<InputStream> getResource(final URL url)
    {
        //@formatter:off
        return Stream.of(url)
                .map(this::generateKey)
                .map(this.cacheDirectory::resolve)
                .map(path -> {
                    try
                    {
                        if (!Files.isReadable(path))
                        {
                            try (InputStream inputStream = loadInputStream(url))
                            {
                                Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
                            }
                        }

                        return Files.newInputStream(path, StandardOpenOption.READ);
                    }
                    catch (final Exception ex)
                    {
//                        getLogger().error(null, ex);

                        if (ex instanceof RuntimeException)
                        {
                            throw (RuntimeException) ex;
                        }

                        throw new RuntimeException(ex);
                    }
                }).findFirst();
        //@formatter:on
    }
}
