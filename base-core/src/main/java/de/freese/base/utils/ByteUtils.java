package de.freese.base.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.util.HexFormat;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Thomas Freeses
 */
public final class ByteUtils {
    //    static final char[] HEX_CHARS =
    //            {
    //                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    //            };
    //
    //    static final String HEX_INDEX = "0123456789abcdefABCDEF";

    public static String bytesToHex(final byte[] bytes) {
        return HexFormat.of().withUpperCase().formatHex(bytes);
        //
        //        StringBuilder sb = new StringBuilder(bytes.length * 2);
        //
        //        for (byte b : bytes)
        //        {
        //            // int temp = b & 0xFF;
        //            //
        //            // sb.append(HEX_CHARS[temp >> 4]);
        //            // sb.append(HEX_CHARS[temp & 0x0F]);
        //
        //            sb.append(HEX_CHARS[(b & 0xF0) >>> 4]);
        //            sb.append(HEX_CHARS[b & 0x0F]);
        //        }
        //
        //        return sb.toString();
    }

    public static byte[] compressBytes(final byte[] bytes) throws IOException {
        if (bytes == null) {
            return new byte[0];
        }

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream zos = new GZIPOutputStream(bos)) {
            zos.write(bytes);

            zos.flush();
            bos.flush();

            return bos.toByteArray();
        }

        // Deflater compressor = new Deflater();
        // compressor.setLevel(Deflater.BEST_COMPRESSION);
        // compressor.setInput(bytes);
        // compressor.finish();
        //
        // ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
        // byte[] buf = new byte[1024];
        //
        // while (!compressor.finished())
        // {
        // int count = compressor.deflate(buf);
        // bos.write(buf, 0, count);
        // }
        //
        // try
        // {
        // bos.close();
        // }
        // catch (IOException ex)
        // {
        // }
    }

    public static byte[] decompressBytes(final byte[] bytes) throws IOException {
        if (bytes == null) {
            return new byte[0];
        }

        byte[] decompressed = null;

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
             ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             GZIPInputStream zis = new GZIPInputStream(bis)) {
            byte[] buffer = new byte[4096];

            while (true) {
                int length = zis.read(buffer);

                if (length == -1) {
                    break;
                }

                bos.write(buffer, 0, length);
            }

            bos.flush();

            decompressed = bos.toByteArray();
        }

        return decompressed;
    }

    public static <T> T deserializeObject(final Class<T> type, final byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        final Object object;

        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream out = new ObjectInputStream(bais)) {
            object = out.readObject();
        }
        catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
        catch (final ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        return type.cast(object);
    }

    public static long generateTempOID() {
        UUID uuid = UUID.randomUUID();

        long oid = (uuid.getMostSignificantBits() >> 32) ^ uuid.getMostSignificantBits();
        oid ^= (uuid.getLeastSignificantBits() >> 32) ^ uuid.getLeastSignificantBits();

        if (oid > 0) {
            oid *= -1;
        }

        return oid;
    }

    public static byte[] hexToBytes(final CharSequence hexString) throws Exception {
        return HexFormat.of().parseHex(hexString);
        //        if ((hexString.length() % 2) == 1)
        //        {
        //            throw new IllegalArgumentException("Invalid hexadecimal String supplied.");
        //        }
        //
        //        byte[] bytes = new byte[hexString.length() / 2];
        //
        //        for (int i = 0; i < hexString.length(); i += 2)
        //        {
        //            int firstDigit = Character.digit(hexString.charAt(i), 16);
        //            int secondDigit = Character.digit(hexString.charAt(i + 1), 16);
        //
        //            if ((firstDigit < 0) || (secondDigit < 0))
        //            {
        //                throw new IllegalArgumentException("Invalid Hexadecimal Character in: " + hexString);
        //            }
        //
        //            bytes[i / 2] = (byte) ((firstDigit << 4) + secondDigit);
        //        }
        //
        //        return bytes;
    }

    public static byte[] serializeObject(final Serializable object) {
        if (object == null) {
            return new byte[0];
        }

        final ByteArrayOutputStream baos = new ByteArrayOutputStream(512);

        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(object);
        }
        catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }

        return baos.toByteArray();
    }

    public static boolean toBoolean(final byte value) {
        return value != 0;
    }

    public static byte[] toBytes(final boolean value) {
        byte[] bytes = new byte[1];

        if (value) {
            bytes[0] = (byte) 1;
        }

        return bytes;
    }

    public static byte[] toBytes(final char value) {
        byte[] bytes = new byte[2];

        bytes[0] = (byte) (0xFF & (value >> 8));
        bytes[1] = (byte) (0xFF & value);

        return bytes;
    }

    public static byte[] toBytes(final double value) {
        long longValue = Double.doubleToRawLongBits(value);

        return toBytes(longValue);
    }

    public static byte[] toBytes(final float value) {
        int intValue = Float.floatToRawIntBits(value);

        return toBytes(intValue);
    }

    public static byte[] toBytes(final int value) {
        byte[] bytes = new byte[4];

        bytes[0] = (byte) (0xFF & (value >> 24));
        bytes[1] = (byte) (0xFF & (value >> 16));
        bytes[2] = (byte) (0xFF & (value >> 8));
        bytes[3] = (byte) (0xFF & value);

        return bytes;
    }

    public static byte[] toBytes(final long value) {
        byte[] bytes = new byte[8];

        bytes[0] = (byte) (0xFF & (value >> 56));
        bytes[1] = (byte) (0xFF & (value >> 48));
        bytes[2] = (byte) (0xFF & (value >> 40));
        bytes[3] = (byte) (0xFF & (value >> 32));
        bytes[4] = (byte) (0xFF & (value >> 24));
        bytes[5] = (byte) (0xFF & (value >> 16));
        bytes[6] = (byte) (0xFF & (value >> 8));
        bytes[7] = (byte) (0xFF & value);

        return bytes;
    }

    public static byte[] toBytes(final short value) {
        byte[] bytes = new byte[2];

        bytes[0] = (byte) (0xFF & (value >> 8));
        bytes[1] = (byte) (0xFF & value);

        return bytes;
    }

    public static byte[] toBytes(final String value) {
        char[] chars = value.toCharArray();
        int size = chars.length;
        byte[] bytes = new byte[size];

        for (int i = 0; i < size; i++) {
            bytes[i] = (byte) chars[i];
        }

        return bytes;
    }

    public static char toChar(final byte[] value) {
        return (char) (((value[0] & 0xFF) << 8) + (value[1] & 0xFF));
    }

    public static double toDouble(final byte[] value) {
        long longValue = toLong(value);

        return Double.longBitsToDouble(longValue);
    }

    public static float toFloat(final byte[] value) {
        int intValue = toInt(value);

        return Float.intBitsToFloat(intValue);
    }

    public static int toInt(final byte[] value) {
        // @formatter:off
        return ((value[0] & 0xFF) << 24)
                + ((value[1] & 0xFF) << 16)
                + ((value[2] & 0xFF) << 8)
                + (value[3] & 0xFF);
        // @formatter:on
    }

    public static long toLong(final byte[] value) {
        // @formatter:off
        return ((long) (value[0] & 0xFF) << 56)
                + ((long) (value[1] & 0xFF) << 48)
                + ((long) (value[2] & 0xFF) << 40)
                + ((long) (value[3] & 0xFF) << 32)
                + ((long) (value[4] & 0xFF) << 24)
                + ((long) (value[5] & 0xFF) << 16)
                + ((long) (value[6] & 0xFF) << 8)
                + ((long) value[7] & 0xFF);
        // @formatter:on
    }

    public static short toShort(final byte[] value) {
        return (short) (((value[0] & 0xFF) << 8) + (value[1] & 0xFF));
    }

    private ByteUtils() {
        super();
    }
}
