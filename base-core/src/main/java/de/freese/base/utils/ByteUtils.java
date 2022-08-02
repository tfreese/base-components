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
 * @author Thomas Freese
 */
public final class ByteUtils
{
    /**
     *
     */
    public static final char[] HEX_CHARS =
            {
                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
            };

    /**
     *
     */
    public static final String HEX_INDEX = "0123456789abcdefABCDEF";

    /**
     * Wandelt das Array in einen HEX-String um.
     *
     * @param bytes byte[]
     *
     * @return String
     */
    public static String bytesToHex(final byte[] bytes)
    {
        return HexFormat.of().withUpperCase().formatHex(bytes);
        //        // final int l = bytes.length;
        //        // final char[] out = new char[l << 1];
        //        //
        //        // // two characters form the hex value.
        //        // for (int i = 0, j = 0; i < l; i++) {
        //        // out[j++] = HEX_CHARS[(0xF0 & bytes[i]) >>> 4];
        //        // out[j++] = HEX_CHARS[0x0F & bytes[i]];
        //        // }
        //        //
        //        // return out;
        //
        //        StringBuilder sbuf = new StringBuilder(bytes.length * 2);
        //
        //        for (byte b : bytes)
        //        {
        //            // int temp = b & 0xFF;
        //            //
        //            // sbuf.append(HEX_CHARS[temp >> 4]);
        //            // sbuf.append(HEX_CHARS[temp & 0x0F]);
        //
        //            sbuf.append(HEX_CHARS[(b & 0xF0) >>> 4]);
        //            sbuf.append(HEX_CHARS[b & 0x0F]);
        //        }
        //
        //        return sbuf.toString();
    }

    /**
     * Komprimierung eines ByteArrays.
     *
     * @param bytes byte[]
     *
     * @return byte[]
     *
     * @throws IOException Falls was schiefgeht.
     */
    public static byte[] compressBytes(final byte[] bytes) throws IOException
    {
        if (bytes == null)
        {
            return new byte[0];
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try (GZIPOutputStream zos = new GZIPOutputStream(bos))
        {
            zos.write(bytes);

            zos.flush();
            bos.flush();
        }

        byte[] compressed = bos.toByteArray();

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

        return compressed;
    }

    /**
     * Dekomprimierung eines ByteArrays.
     *
     * @param bytes byte[]
     *
     * @return byte[]
     *
     * @throws IOException Falls was schiefgeht.
     */
    public static byte[] decompressBytes(final byte[] bytes) throws IOException
    {
        if (bytes == null)
        {
            return new byte[0];
        }

        byte[] decompressed = null;

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length))
        {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 GZIPInputStream zis = new GZIPInputStream(bis))
            {
                byte[] buffer = new byte[4096];

                while (true)
                {
                    int length = zis.read(buffer);

                    if (length == -1)
                    {
                        break;
                    }

                    bos.write(buffer, 0, length);
                }

                bos.flush();
            }

            decompressed = bos.toByteArray();
        }

        return decompressed;
    }

    /**
     * Liefert das deserialisierte ByteArray.
     *
     * @param <T> Konkreter Typ des Objektes.
     * @param bytes byte[]
     *
     * @return Object
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserializeObject(final byte[] bytes)
    {
        if (bytes == null)
        {
            return null;
        }

        // Object object = SerializationUtils.deserialize(bytes);
        final Object object;

        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream out = new ObjectInputStream(bais))
        {
            object = out.readObject();
        }
        catch (final IOException ex)
        {
            throw new UncheckedIOException(ex);
        }
        catch (final ClassNotFoundException ex)
        {
            throw new RuntimeException(ex);
        }

        return (T) object;
    }

    /**
     * Generiert eine zufällige negative OID.
     *
     * @return long
     */
    public static long generateTempOID()
    {
        UUID uuid = UUID.randomUUID();

        long oid = (uuid.getMostSignificantBits() >> 32) ^ uuid.getMostSignificantBits();
        oid ^= (uuid.getLeastSignificantBits() >> 32) ^ uuid.getLeastSignificantBits();

        if (oid > 0)
        {
            oid *= -1;
        }

        return oid;
    }

    /**
     * @param hexString {@link CharSequence}
     *
     * @return byte[]
     *
     * @throws Exception Falls was schiefgeht.
     */
    public static byte[] hexToBytes(final CharSequence hexString) throws Exception
    {
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

    /**
     * Liefert das serialisierte Object.
     *
     * @param object {@link Serializable}
     *
     * @return byte[]
     */
    public static byte[] serializeObject(final Serializable object)
    {
        if (object == null)
        {
            return new byte[0];
        }

        // byte[] bytes = SerializationUtils.serialize(object);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(512);

        try (ObjectOutputStream out = new ObjectOutputStream(baos))
        {
            out.writeObject(object);
        }
        catch (final IOException ex)
        {
            throw new UncheckedIOException(ex);
        }

        byte[] bytes = baos.toByteArray();

        return bytes;
    }

    /**
     * @param value byte
     *
     * @return boolean
     */
    public static boolean toBoolean(final byte value)
    {
        return value != 0;
    }

    /**
     * Convert a byte[] into an instance of our value class.
     *
     * @param value byte[]
     *
     * @return boolean[]
     */
    public static boolean[] toBooleanArray(final byte[] value)
    {
        int n = value.length;
        boolean[] a = new boolean[n];

        for (int i = 0; i < n; i++)
        {
            a[i] = toBoolean(value[i]);
        }

        return a;
    }

    /**
     * @param value boolean
     *
     * @return byte[]
     */
    public static byte[] toByteArray(final boolean value)
    {
        byte[] buf = new byte[1];

        if (value)
        {
            buf[0] = (byte) 1;
        }
        else
        {
            buf[0] = (byte) 0;
        }

        return buf;
    }

    /**
     * Convert an instance of our value class into a byte[].
     *
     * @param value boolean[]
     *
     * @return byte[]
     */
    public static byte[] toByteArray(final boolean[] value)
    {
        if (value == null)
        {
            return new byte[0];
        }

        int n = value.length;
        byte[] buf = new byte[n];

        for (int i = 0; i < n; i++)
        {
            buf[i] = toByteArray(value[i])[0];
        }

        return buf;
    }

    /**
     * @param value char
     *
     * @return byte[]
     */
    public static byte[] toByteArray(final char value)
    {
        byte[] buf = new byte[2];

        buf[0] = (byte) ((value >>> 8) & 0xFF);
        buf[1] = (byte) (value & 0xFF);

        return buf;
    }

    /**
     * Convert an instance of our value class into a byte[].
     *
     * @param value char[]
     *
     * @return byte[]
     */
    public static byte[] toByteArray(final char[] value)
    {
        if (value == null)
        {
            return new byte[0];
        }

        int n = value.length;
        byte[] bytes = new byte[n * 2];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = toByteArray(value[i]);
            i++;

            for (byte element : tmp)
            {
                bytes[j] = element;
                j++;
            }
        }

        return bytes;
    }

    /**
     * @param value double
     *
     * @return byte[]
     */
    public static byte[] toByteArray(final double value)
    {
        long longValue = Double.doubleToRawLongBits(value);

        return toByteArray(longValue);
    }

    /**
     * Convert an instance of our value class into a byte[].
     *
     * @param value double[]
     *
     * @return byte[]
     */
    public static byte[] toByteArray(final double[] value)
    {
        if (value == null)
        {
            return new byte[0];
        }

        int n = value.length;
        byte[] bytes = new byte[n * 8];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = toByteArray(value[i]);
            i++;

            for (byte element : tmp)
            {
                bytes[j] = element;
                j++;
            }
        }

        return bytes;
    }

    /**
     * @param value float
     *
     * @return byte[]
     */
    public static byte[] toByteArray(final float value)
    {
        int intValue = Float.floatToRawIntBits(value);

        return toByteArray(intValue);
    }

    /**
     * Convert an instance of our value class into a byte[].
     *
     * @param value float[]
     *
     * @return byte[]
     */
    public static byte[] toByteArray(final float[] value)
    {
        if (value == null)
        {
            return new byte[0];
        }

        int n = value.length;
        byte[] bytes = new byte[n * 4];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = toByteArray(value[i]);
            i++;

            for (byte element : tmp)
            {
                bytes[j] = element;
                j++;
            }
        }

        return bytes;
    }

    /**
     * @param value int
     *
     * @return byte[]
     */
    public static byte[] toByteArray(final int value)
    {
        byte[] bytes = new byte[4];

        bytes[0] = (byte) ((value >>> 24) & 0xFF);
        bytes[1] = (byte) ((value >>> 16) & 0xFF);
        bytes[2] = (byte) ((value >>> 8) & 0xFF);
        bytes[3] = (byte) (value & 0xFF);

        return bytes;
    }

    /**
     * Convert an instance of our value class into a byte[].
     *
     * @param value int[]
     *
     * @return byte[]
     */
    public static byte[] toByteArray(final int[] value)
    {
        if (value == null)
        {
            return new byte[0];
        }

        int n = value.length;
        byte[] bytes = new byte[n * 4];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = toByteArray(value[i]);
            i++;

            for (byte element : tmp)
            {
                bytes[j] = element;
                j++;
            }
        }

        return bytes;
    }

    /**
     * @param value long
     *
     * @return byte[]
     */
    public static byte[] toByteArray(final long value)
    {
        byte[] bytes = new byte[8];

        bytes[0] = (byte) ((value >>> 56) & 0xFF);
        bytes[1] = (byte) ((value >>> 48) & 0xFF);
        bytes[2] = (byte) ((value >>> 40) & 0xFF);
        bytes[3] = (byte) ((value >>> 32) & 0xFF);
        bytes[4] = (byte) ((value >>> 24) & 0xFF);
        bytes[5] = (byte) ((value >>> 16) & 0xFF);
        bytes[6] = (byte) ((value >>> 8) & 0xFF);
        bytes[7] = (byte) (value & 0xFF);

        return bytes;
    }

    /**
     * Convert an instance of our value class into a byte[].
     *
     * @param value long[]
     *
     * @return byte[]
     */
    public static byte[] toByteArray(final long[] value)
    {
        if (value == null)
        {
            return new byte[0];
        }

        int n = value.length;
        byte[] bytes = new byte[n * 8];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = toByteArray(value[i]);
            i++;

            for (byte element : tmp)
            {
                bytes[j] = element;
                j++;
            }
        }

        return bytes;
    }

    /**
     * @param value short
     *
     * @return byte[]
     */
    public static byte[] toByteArray(final short value)
    {
        byte[] bytes = new byte[2];

        bytes[0] = (byte) ((value >>> 8) & 0xFF);
        bytes[1] = (byte) (value & 0xFF);

        return bytes;
    }

    /**
     * Convert an instance of our value class into a byte[].
     *
     * @param value short[]
     *
     * @return byte[]
     */
    public static byte[] toByteArray(final short[] value)
    {
        if (value == null)
        {
            return new byte[0];
        }

        int n = value.length;
        byte[] bytes = new byte[n * 2];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = toByteArray(value[i]);
            i++;

            for (byte element : tmp)
            {
                bytes[j] = element;
                j++;
            }
        }

        return bytes;
    }

    /**
     * @param value String
     *
     * @return byte[]
     */
    public static byte[] toByteArray(final String value)
    {
        char[] chars = value.toCharArray();
        int size = chars.length;
        byte[] bytes = new byte[size];

        for (int i = 0; i < size; i++)
        {
            bytes[i] = (byte) chars[i];
        }

        return bytes;
    }

    /**
     * @param value byte[]
     *
     * @return char
     */
    public static char toChar(final byte[] value)
    {
        return (char) (((value[0] & 0xFF) << 8) + (value[1] & 0xFF));
    }

    /**
     * Convert a byte[] into an instance of our value class.
     *
     * @param value byte[]
     *
     * @return char[]
     */
    public static char[] toCharArray(final byte[] value)
    {
        int n = value.length / 2;
        char[] a = new char[n];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = new byte[2];

            for (int k = 0; k < tmp.length; k++)
            {
                tmp[k] = value[j];
                j++;
            }

            a[i] = toChar(tmp);
            i++;
        }

        return a;
    }

    /**
     * @param value byte[]
     *
     * @return double
     */
    public static double toDouble(final byte[] value)
    {
        long longValue = toLong(value);

        return Double.longBitsToDouble(longValue);
    }

    /**
     * Convert a byte[] into an instance of our value class.
     *
     * @param value byte[]
     *
     * @return double[]
     */
    public static double[] toDoubleArray(final byte[] value)
    {
        int n = value.length / 8;
        double[] a = new double[n];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = new byte[8];

            for (int k = 0; k < tmp.length; k++)
            {
                tmp[k] = value[j];
                j++;
            }

            a[i] = toDouble(tmp);
            i++;
        }

        return a;
    }

    /**
     * @param value byte[]
     *
     * @return float
     */
    public static float toFloat(final byte[] value)
    {
        int intValue = toInt(value);

        return Float.intBitsToFloat(intValue);
    }

    /**
     * Convert a byte[] into an instance of our value class.
     *
     * @param value byte[]
     *
     * @return float[]
     */
    public static float[] toFloatArray(final byte[] value)
    {
        int n = value.length / 4;
        float[] a = new float[n];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = new byte[4];

            for (int k = 0; k < tmp.length; k++)
            {
                tmp[k] = value[j];
                j++;
            }

            a[i] = toFloat(tmp);
            i++;
        }

        return a;
    }

    /**
     * @param value byte[]
     *
     * @return int
     */
    public static int toInt(final byte[] value)
    {
        // @formatter:off
        return ((value[0] & 0xFF) << 24)
                + ((value[1] & 0xFF) << 16)
                + ((value[2] & 0xFF) << 8)
                + (value[3] & 0xFF);
        // @formatter:on
    }

    /**
     * Convert a byte[] into an instance of our value class.
     *
     * @param value byte[]
     *
     * @return int[]
     */
    public static int[] toIntArray(final byte[] value)
    {
        int n = value.length / 4;
        int[] a = new int[n];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = new byte[4];

            for (int k = 0; k < tmp.length; k++)
            {
                tmp[k] = value[j];
                j++;
            }

            a[i] = toInt(tmp);
            i++;
        }

        return a;
    }

    /**
     * @param value byte[]
     *
     * @return long
     */
    public static long toLong(final byte[] value)
    {
        // @formatter:off
        return ((long) (value[0] & 0xFF) << 56)
                + ((long) (value[1] & 0xFF) << 48)
                + ((long) (value[2] & 0xFF) << 40)
                + ((long) (value[3] & 0xFF) << 32)
                + ((long) (value[4] & 0xFF) << 24)
                + ((value[5] & 0xFF) << 16)
                + ((value[6] & 0xFF) << 8)
                + (value[7] & 0xFF);
        // @formatter:off
    }

    /**
     * Convert a byte[] into an instance of our value class.
     *
     * @param value byte[]
     * @return long[]
     */
    public static long[] toLongArray(final byte[] value)
    {
        int n = value.length / 8;
        long[] a = new long[n];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = new byte[8];

            for (int k = 0; k < tmp.length; k++)
            {
                tmp[k] = value[j];
                j++;
            }

            a[i] = toLong(tmp);
            i++;
        }

        return a;
    }

    /**
     * @param value byte[]
     * @return short
     */
    public static short toShort(final byte[] value)
    {
        return (short) (((value[0] & 0xFF) << 8) + (value[1] & 0xFF));
    }

    /**
     * Convert a byte[] into an instance of our value class.
     *
     * @param value byte[]
     * @return short[]
     */
    public static short[] toShortArray(final byte[] value)
    {
        int n = value.length / 2;
        short[] a = new short[n];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = new byte[2];

            for (int k = 0; k < tmp.length; k++)
            {
                tmp[k] = value[j];
                j++;
            }

            a[i] = toShort(tmp);
            i++;
        }

        return a;
    }

    /**
     * Erstellt ein neues {@link ByteUtils} Object.
     */
    private ByteUtils()
    {
        super();
    }
}
