// Created: 17.11.2018
package de.freese.base.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * @author Thomas Freese
 */
public final class DigestUtils
{
    public static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    private static final HexFormat HEX_FORMAT = HexFormat.of().withUpperCase();

    public static MessageDigest createMd5Digest()
    {
        return createMessageDigest("MD5");
    }

    /**
     * Erzeugt den {@link MessageDigest} für die Generierung der Prüfsumme.<br>
     * <br>
     * Every implementation of the Java platform is required to support the following standard MessageDigest algorithms:<br>
     * MD5<br>
     * SHA-1<br>
     * SHA-256<br>
     */
    public static MessageDigest createMessageDigest(final String algorithm)
    {
        try
        {
            return MessageDigest.getInstance(algorithm);
        }
        catch (final NoSuchAlgorithmException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public static MessageDigest createSha1Digest()
    {
        return createMessageDigest("SHA-1");
    }

    public static MessageDigest createSha256Digest()
    {
        return createMessageDigest("SHA-256");
    }

    public static byte[] digest(final MessageDigest messageDigest, final byte[] bytes)
    {
        return messageDigest.digest(bytes);
    }

    /**
     * Die Position des {@link ByteBuffer} wird wieder auf den Ursprungs-Wert gesetzt.<br>
     */
    public static void digest(final MessageDigest messageDigest, final ByteBuffer byteBuffer)
    {
        int position = byteBuffer.position();

        messageDigest.update(byteBuffer);

        byteBuffer.position(position);
    }

    /**
     * Der {@link InputStream} wird NICHT geschlossen !
     */
    public static byte[] digest(final MessageDigest messageDigest, final InputStream inputStream) throws IOException
    {
        final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        int bytesRead = -1;

        while ((bytesRead = inputStream.read(buffer)) != -1)
        {
            messageDigest.update(buffer, 0, bytesRead);
        }

        return messageDigest.digest();
    }

    public static byte[] digest(final MessageDigest messageDigest, final Path file) throws IOException
    {
        byte[] bytes = null;

        try (ReadableByteChannel channel = Files.newByteChannel(file, StandardOpenOption.READ))
        {
            final ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE);

            while (channel.read(buffer) != -1)
            {
                buffer.flip();

                messageDigest.update(buffer);

                buffer.clear();
            }

            bytes = messageDigest.digest();
        }

        return bytes;
    }

    public static String digestAsHex(final MessageDigest messageDigest)
    {
        final byte[] digest = messageDigest.digest();

        return HEX_FORMAT.formatHex(digest);
    }

    /**
     * Der {@link InputStream} wird NICHT geschlossen !
     */
    public static String digestAsHex(final MessageDigest messageDigest, final InputStream inputStream) throws IOException
    {
        final byte[] bytes = digest(messageDigest, inputStream);

        return HEX_FORMAT.formatHex(bytes);
    }

    public static String digestAsHex(final MessageDigest messageDigest, final Path file) throws IOException
    {
        final byte[] bytes = digest(messageDigest, file);

        return HEX_FORMAT.formatHex(bytes);
    }

    private DigestUtils()
    {
        super();
    }
}
