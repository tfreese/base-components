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

/**
 * <a href="https://docs.oracle.com/en/java/javase/11/docs/specs/security/standard-names.html">Java Security Standard Algorithm Names</a>
 *
 * @author Thomas Freese
 */
public final class DigestUtils {
    public static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    public enum Algorithm {
        MD5("MD5"),
        SHA_1("SHA-1"),
        SHA_256("SHA-256"),
        SHA_512("SHA-512"),
        SHA3_256("SHA3-256"),
        SHA3_512("SHA3-512");

        private final String algorithmName;

        Algorithm(final String algorithmName) {
            this.algorithmName = algorithmName;
        }

        public String getAlgorithmName() {
            return algorithmName;
        }
    }

    public static MessageDigest createMessageDigest(final Algorithm algorithm) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(algorithm.getAlgorithmName());
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

    public static String encodeDigest(final MessageDigest digest, final Encoding encoding) {
        final byte[] bytes = digest.digest();

        return CryptoUtils.encode(encoding, bytes);
    }

    private DigestUtils() {
        super();
    }
}
