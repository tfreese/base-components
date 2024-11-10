// Created: 29.03.2020
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
public abstract class AbstractIoTest {
    // private static final AtomicInteger COUNTER = new AtomicInteger(0);
    /**
     * Paths.get(System.getProperty("user.dir"), "target")<br>
     * Paths.get(System.getProperty("java.io.tmpdir"), "java")
     */
    protected static final Path PATH_TEST = Paths.get(System.getProperty("java.io.tmpdir"), "java");
    protected static final long SIZE_100_KB = 100 * 1024;
    protected static final long SIZE_10_KB = 10 * 1024;

    /**
     * Löscht das Verzeichnis rekursiv inklusive Dateien und Unterverzeichnisse.
     */
    public static void deleteDirectoryRecursive(final Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }

        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("path is not a directory: " + path);
        }

        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Verzeichnis-Struktur zum Testen löschen.
     */
    @AfterAll
    protected static void afterAll() {
        // Würde auch die Dateien andere IO-Tests löschen.
        // deleteDirectoryRecursive(PATH_TEST);
    }

    @BeforeAll
    protected static void beforeAll() throws IOException {
        if (Files.notExists(PATH_TEST)) {
            Files.createDirectories(PATH_TEST);
        }
    }

    protected Path createFile(final long size) throws IOException {
        // final Path path = PATH_TEST.resolve(getClass().getSimpleName()).resolve("testfile." + COUNTER.incrementAndGet());
        final Path path = PATH_TEST.resolve(getClass().getSimpleName()).resolve(size + "_testfile.bin");

        if (Files.notExists(path)) {
            Files.createDirectories(path.getParent());

            try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw")) {
                raf.setLength(size);
            }
        }

        return path;
    }

    protected Path createFile(final String fileName) throws IOException {
        final Path path = PATH_TEST.resolve(getClass().getSimpleName()).resolve(fileName);

        if (Files.notExists(path)) {
            Files.createDirectories(path.getParent());
        }

        return path;
    }
}
