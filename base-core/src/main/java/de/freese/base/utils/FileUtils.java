// Created: 14.04.2020
package de.freese.base.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

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

        final Path parent = path.getParent();
        final String fileName = path.getFileName().toString();
        final Path last = parent.resolve(fileName + ".last");

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
        Objects.requireNonNull(file, "file required");

        deleteDirectoryRecursive(file.toPath());
    }

    public static void deleteDirectoryRecursive(final Path path) throws IOException {
        Objects.requireNonNull(path, "path required");

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

    public static String normalizeFileName(final String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return fileName;
        }

        // digits [0-9]: 48-57
        final Predicate<Character> digitsPredicate = c -> c >= 48 && c <= 57;

        // uppercase letters [A-Z]: 65-90
        final Predicate<Character> upperCaseLettersPredicate = c -> c >= 65 && c <= 90;

        // lowercase letters [a-z]: 97-122
        final Predicate<Character> lowerCaseLettersPredicate = c -> c >= 97 && c <= 122;

        // Specials: '(' 40; ')' 41; '-' 45; '.' 46;  '_' 95
        final Predicate<Character> specialCharsPredicate = c -> c == 40 || c == 41 || c == 45 || c == 46 || c == 95;

        final Predicate<Character> fileNameCompatible = digitsPredicate.or(upperCaseLettersPredicate)
                .or(lowerCaseLettersPredicate)
                .or(specialCharsPredicate);

        return StringUtils.replaceChars(fileName, fileNameCompatible, c -> "_");
    }

    /**
     * @return String, z.B. '___,___ MB'
     */
    public static String toHumanReadableSize(final long size) {
        final int unitIndex = (int) (Math.log10(size) / 3);
        final double unitValue = 1 << (unitIndex * 10);

        // return new DecimalFormat("#,##0.#").format(size / unitValue) + " " + SIZE_UNITS[unitIndex];
        return String.format("%7.3f %s", size / unitValue, SIZE_UNITS[unitIndex]);
    }

    @SuppressWarnings("java:S1162")
    public static void validateZip(final ZipFile zipFile) throws IOException {
        final int THRESHOLD_ENTRIES = 10_000;
        final int THRESHOLD_SIZE = 1_000_000_000; // Entry size: 1 GB
        final double THRESHOLD_RATIO = 10D; // Compression in %

        long totalSizeArchive = 0L;
        int totalEntryArchive = 0;

        for (final Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements(); ) {
            final ZipEntry zipEntry = entries.nextElement();

            totalEntryArchive++;
            long totalSizeEntry = 0L;

            try (InputStream inputStream = zipFile.getInputStream(zipEntry);
                 OutputStream outputStream = OutputStream.nullOutputStream()) {
                totalSizeEntry = inputStream.transferTo(outputStream);
                totalSizeArchive += totalSizeEntry;
            }

            if (totalEntryArchive > THRESHOLD_ENTRIES) {
                throw new ZipException("Too many entries in this archive, can lead to inodes exhaustion of the filesystem: " + totalEntryArchive);
            }

            if (totalSizeArchive > THRESHOLD_SIZE) {
                throw new ZipException("The uncompressed data size is too much for the application resource capacity: " + totalSizeArchive);
            }

            if (!zipEntry.isDirectory()) {
                final double compressionRatio = (double) totalSizeEntry / zipEntry.getCompressedSize();

                if (compressionRatio > THRESHOLD_RATIO) {
                    throw new ZipException("Ratio between compressed and uncompressed data is highly suspicious, looks like a Zip Bomb Attack: "
                            + compressionRatio + "% - " + zipEntry.getName());
                }
            }
        }
    }

    private FileUtils() {
        super();
    }
}
