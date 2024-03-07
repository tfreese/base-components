// Created: 17.11.2018
package de.freese.base.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html">Java Security Standard Algorithm Names</a>
 *
 * @author Thomas Freese
 */
public final class DigestUtils {
    public static final int DEFAULT_BUFFER_SIZE = 8 * 1024;
    private static final HexFormat HEX_FORMAT = HexFormat.of().withUpperCase();

    /**
     * MD2<br>
     * MD5<br>
     * SHA-1<br>
     * SHA-224<br>
     * SHA-256<br>
     * SHA-384<br>
     * SHA-512<br>
     */
    public static MessageDigest createMessageDigest(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static MessageDigest createSha1Digest() {
        return createMessageDigest("SHA-1");
    }

    public static MessageDigest createSha512Digest() {
        return createMessageDigest("SHA-512");
    }

    public static DigestInputStream decorateInputStream(final MessageDigest digest, final InputStream inputStream) {
        return new DigestInputStream(inputStream, digest);
    }

    public static DigestOutputStream decorateOutputStream(final MessageDigest digest, final OutputStream outputStream) {
        return new DigestOutputStream(outputStream, digest);
    }

    public static byte[] digest(final MessageDigest digest, final byte[] bytes) {
        return digest.digest(bytes);
    }

    /**
     * Die Position des {@link ByteBuffer} wird wieder auf den Ursprungs-Wert gesetzt.<br>
     */
    public static void digest(final MessageDigest digest, final ByteBuffer byteBuffer) {
        final int position = byteBuffer.position();

        digest.update(byteBuffer);

        byteBuffer.position(position);
    }

    /**
     * Der {@link InputStream} wird NICHT geschlossen !
     */
    public static byte[] digest(final MessageDigest digest, final InputStream inputStream) throws IOException {
        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        int bytesRead = -1;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            digest.update(buffer, 0, bytesRead);
        }

        return digest.digest();
    }

    public static byte[] digest(final MessageDigest digest, final Path file) throws IOException {
        byte[] bytes = null;

        try (ReadableByteChannel channel = Files.newByteChannel(file, StandardOpenOption.READ)) {
            final ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE);

            while (channel.read(buffer) != -1) {
                buffer.flip();

                digest.update(buffer);

                buffer.clear();
            }

            bytes = digest.digest();
        }

        return bytes;
    }

    public static String digestAsHex(final MessageDigest digest) {
        final byte[] bytes = digest.digest();

        return HEX_FORMAT.formatHex(bytes);
    }

    /**
     * Der {@link InputStream} wird NICHT geschlossen !
     */
    public static String digestAsHex(final MessageDigest digest, final InputStream inputStream) throws IOException {
        final byte[] bytes = digest(digest, inputStream);

        return HEX_FORMAT.formatHex(bytes);
    }

    public static String digestAsHex(final MessageDigest digest, final Path file) throws IOException {
        final byte[] bytes = digest(digest, file);

        return HEX_FORMAT.formatHex(bytes);
    }

    private DigestUtils() {
        super();
    }
}
