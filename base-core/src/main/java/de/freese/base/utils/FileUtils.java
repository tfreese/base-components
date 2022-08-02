// Created: 14.04.2020
package de.freese.base.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public final class FileUtils
{
    /**
     *
     */
    private static final String[] SIZE_UNITS =
            {
                    "B", "KB", "MB", "GB", "TB"
            };

    /**
     * Kopiert die bestehende Datei nach *.last.
     *
     * @param path {@link Path}
     *
     * @throws IOException Falls was schiefgeht.
     */
    public static void copy(final Path path) throws IOException
    {
        if (path == null)
        {
            throw new NullPointerException("path required");
        }

        Path parent = path.getParent();
        String fileName = path.getFileName().toString();
        Path last = parent.resolve(fileName + ".last");

        if (!Files.exists(parent))
        {
            Files.createDirectories(parent);
        }

        if (Files.exists(last))
        {
            Files.delete(last);
        }

        Files.copy(path, last); // StandardCopyOption
    }

    /**
     * Löscht das Verzeichnis rekursiv inklusive Dateien und Unterverzeichnisse.
     *
     * @param file {@link File}
     *
     * @throws IOException Falls was schiefgeht.
     */
    public static void deleteDirectoryRecursive(final File file) throws IOException
    {
        if (file == null)
        {
            throw new NullPointerException("file required");
        }

        deleteDirectoryRecursive(file.toPath());
    }

    /**
     * Löscht das Verzeichnis rekursiv inklusive Dateien und Unterverzeichnisse.
     *
     * @param path {@link Path}
     *
     * @throws IOException Falls was schiefgeht.
     */
    public static void deleteDirectoryRecursive(final Path path) throws IOException
    {
        if (path == null)
        {
            throw new NullPointerException("path required");
        }

        if (!Files.exists(path))
        {
            return;
        }

        if (!Files.isDirectory(path))
        {
            throw new IllegalArgumentException("path is not a directory: " + path);
        }

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

    /**
     * Benennt die bestehende Datei in *.last um.
     *
     * @param path {@link Path}
     *
     * @throws IOException Falls was schiefgeht.
     */
    public static void rename(final Path path) throws IOException
    {
        Objects.requireNonNull(path, "path required");

        Path parent = path.getParent();
        String fileName = path.getFileName().toString();
        Path last = parent.resolve(fileName + ".last");

        if (!Files.exists(parent))
        {
            Files.createDirectories(parent);
        }

        if (Files.exists(last))
        {
            Files.delete(last);
        }

        if (Files.exists(path))
        {
            Files.move(path, last); // StandardCopyOption
        }
    }

    /**
     * Liefert einen gültigen Dateinamen ohne mehrfache Spaces.<br>
     * Sonderzeichen werden ersetzt:<br>
     * <ul>
     * <li>" -> '
     * <li>/ -> _
     * <li>, -> _
     * <li>* -> _
     * <li>' ' -> _
     * </ul>
     *
     * @param fileName String
     *
     * @return String
     */
    public static String rewriteFileName(final String fileName)
    {
        if ((fileName == null) || (fileName.strip().length() == 0))
        {
            return fileName;
        }

        String name = StringUtils.normalizeSpace(fileName);
        name = name.replace('"', '\'');
        name = name.replace('/', '_');
        name = name.replace(',', '_');
        name = name.replace('*', '_');
        name = name.replace(' ', '_');

        // Mehrere Sonderzeichen waren hintereinander.
        name = name.replace("__", "_");
        name = name.replace("__", "_");

        return name;
    }

    /**
     * @param size long
     *
     * @return String, z.B. '___,___ MB'
     */
    public static String toHumanReadableSize(final long size)
    {
        int unitIndex = (int) (Math.log10(size) / 3);
        double unitValue = 1 << (unitIndex * 10);

        // String readableSize = new DecimalFormat("#,##0.#").format(size / unitValue) + " " + SIZE_UNITS[unitIndex];
        String readableSize = String.format("%7.3f %s", size / unitValue, SIZE_UNITS[unitIndex]);

        return readableSize;
    }

    /**
     * Erstellt ein neues {@link FileUtils} Object.
     */
    private FileUtils()
    {
        super();
    }
}
