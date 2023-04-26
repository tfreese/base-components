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
import java.util.function.Predicate;

/**
 * @author Thomas Freese
 */
public final class FileUtils {
    private static final String[] SIZE_UNITS = {"B", "KB", "MB", "GB", "TB"};

    /**
     * Copies the File to *.last.
     */
    public static void copyToLast(final Path path) throws IOException {
        Objects.requireNonNull(path, "path required");

        Path parent = path.getParent();
        String fileName = path.getFileName().toString();
        Path last = parent.resolve(fileName + ".last");

        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        if (Files.exists(last)) {
            Files.delete(last);
        }

        if (Files.exists(path)) {
            Files.move(path, last); // StandardCopyOption
        }
    }

    public static void deleteDirectoryRecursive(final File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file required");
        }

        deleteDirectoryRecursive(file.toPath());
    }

    public static void deleteDirectoryRecursive(final Path path) throws IOException {
        if (path == null) {
            throw new NullPointerException("path required");
        }

        if (!Files.exists(path)) {
            return;
        }

        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("path is not a directory: " + path);
        }

        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            /**
             * @see java.nio.file.SimpleFileVisitor#postVisitDirectory(java.lang.Object, java.io.IOException)
             */
            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
                Files.delete(dir);

                return FileVisitResult.CONTINUE;
            }

            /**
             * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
             */
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                Files.delete(file);

                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static String normalizeFileName(final String fileName) {
        if ((fileName == null) || (fileName.strip().length() == 0)) {
            return fileName;
        }

        // digits [0-9]: 48-57
        Predicate<Character> digitsPredicate = c -> c >= 48 && c <= 57;

        // uppercase letters [A-Z]: 65-90
        Predicate<Character> upperCaseLettersPredicate = c -> c >= 65 && c <= 90;

        // lowercase letters [a-z]: 97-122
        Predicate<Character> lowerCaseLettersPredicate = c -> c >= 97 && c <= 122;

        // Specials: '(' 40; ')' 41; '-' 45; '.' 46;  '_' 95
        Predicate<Character> specialCharsPredicate = c -> c == 40 || c == 41 || c == 45 || c == 46 || c == 95;

        // @formatter:off
        Predicate<Character> fileNameCompatible = digitsPredicate.or(upperCaseLettersPredicate)
                .or(lowerCaseLettersPredicate)
                .or(specialCharsPredicate)
                ;
        // @formatter:on

        return StringUtils.replaceChars(fileName, fileNameCompatible, c -> "_");
    }

    /**
     * @return String, z.B. '___,___ MB'
     */
    public static String toHumanReadableSize(final long size) {
        int unitIndex = (int) (Math.log10(size) / 3);
        double unitValue = 1 << (unitIndex * 10);

        // return new DecimalFormat("#,##0.#").format(size / unitValue) + " " + SIZE_UNITS[unitIndex];
        return String.format("%7.3f %s", size / unitValue, SIZE_UNITS[unitIndex]);
    }

    private FileUtils() {
        super();
    }
}
