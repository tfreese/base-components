/**
 * Created: 29.03.2020
 */

package de.freese.base.core.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * @author Thomas Freese
 */
public abstract class AbstractIoTest
{
    /**
     * Paths.get(System.getProperty("java.io.tmpdir"), "jsync")<br>
     * Paths.get(System.getProperty("user.dir"), "target")
     */
    protected static final Path PATH_BASE = Paths.get(System.getProperty("java.io.tmpdir"), "java");

    /**
     *
     */
    protected static final Path PATH_FILE_100kB = createFile("smallFile100kB.bin");

    /**
    *
    */
    protected static final Path PATH_FILE_10kB = createFile("smallFile10kB.bin");

    /**
     * Verzeichnis-Struktur zum Testen löschen.
     *
     * @throws Exception Falls was schief geht.
     */
    @AfterAll
    protected static void afterAll() throws Exception
    {
        deleteDirectoryRecursiv(PATH_BASE);
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    @BeforeAll
    protected static void beforeAll() throws IOException
    {
        // Dummy-Datei anlegen.
        Path path = PATH_BASE;
        Path pathFile = PATH_FILE_10kB;

        if (Files.notExists(pathFile))
        {
            Files.createDirectories(path);

            try (RandomAccessFile raf = new RandomAccessFile(pathFile.toFile(), "rw"))
            {
                raf.setLength(1024 * 10);
            }
        }

        path = PATH_BASE;
        pathFile = PATH_FILE_100kB;

        if (Files.notExists(pathFile))
        {
            Files.createDirectories(path);

            try (RandomAccessFile raf = new RandomAccessFile(pathFile.toFile(), "rw"))
            {
                raf.setLength(1024 * 100);
            }
        }
    }

    /**
     * @param fileName String
     * @return {@link Path}
     */
    protected static Path createFile(final String fileName)
    {
        return PATH_BASE.resolve(fileName);
    }

    /**
     * Löscht das Verzeichnis rekursiv inklusive Dateien und Unterverzeichnisse.
     *
     * @param path {@link Path}
     * @throws IOException Falls was schief geht.
     */
    protected static void deleteDirectoryRecursiv(final Path path) throws IOException
    {
        if (!Files.exists(path))
        {
            return;
        }

        if (!Files.isDirectory(path))
        {
            throw new IllegalArgumentException("path is not a dirctory: " + path);
        }

        Files.walkFileTree(path, new SimpleFileVisitor<Path>()
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
}
