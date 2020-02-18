package de.freese.base.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

/**
 * Toolkit zum Wandeln von Datentypen in ByteArrays.
 *
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
    public static String HEX_INDEX = "0123456789abcdefABCDEF";

    /**
     * Wandlet das Array in einen HEX-String um.
     *
     * @param bytes byte[]
     * @return String
     */
    public static String bytesToHex(final byte[] bytes)
    {
        StringBuilder sbuf = new StringBuilder(bytes.length * 2);

        for (byte b : bytes)
        {
            // int temp = b & 0xFF;
            //
            // sbuf.append(HEX_CHARS[temp >> 4]);
            // sbuf.append(HEX_CHARS[temp & 0x0F]);

            sbuf.append(HEX_CHARS[(b & 0xF0) >>> 4]);
            sbuf.append(HEX_CHARS[b & 0x0F]);
        }

        return sbuf.toString();
    }

    /**
     * Komprimierung eines ByteArrays.
     *
     * @param bytes byte[]
     * @return byte[]
     * @throws IOException Falls was schief geht.
     */
    public static byte[] compressBytes(final byte[] bytes) throws IOException
    {
        if (bytes == null)
        {
            return null;
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
     * @return byte[]
     * @throws IOException Falls was schief geht.
     */
    public static byte[] decompressBytes(final byte[] bytes) throws IOException
    {
        if (bytes == null)
        {
            return null;
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
     * @return Object
     * @throws SerializationException Falls was schief geht.
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserializeObject(final byte[] bytes)
    {
        if (bytes == null)
        {
            return null;
        }

        Object object = SerializationUtils.deserialize(bytes);

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
     * @return byte[]
     * @throws Exception Falls was schief geht.
     */
    public static byte[] hexToBytes(final CharSequence hexString) throws Exception
    {
        if ((hexString.length() % 2) == 1)
        {
            throw new IllegalArgumentException("Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];

        for (int i = 0; i < hexString.length(); i += 2)
        {
            int firstDigit = Character.digit(hexString.charAt(i), 16);
            int secondDigit = Character.digit(hexString.charAt(i + 1), 16);

            if ((firstDigit < 0) || (secondDigit < 0))
            {
                throw new IllegalArgumentException("Invalid Hexadecimal Character in: " + hexString);
            }

            bytes[i / 2] = (byte) ((firstDigit << 4) + secondDigit);
        }

        return bytes;
    }

    /**
     * Liefert das serialisierte Object.
     *
     * @param object {@link Serializable}
     * @return byte[]
     * @throws SerializationException Falls was schief geht.
     */
    public static byte[] serializeObject(final Serializable object)
    {
        if (object == null)
        {
            return null;
        }

        byte[] bytes = SerializationUtils.serialize(object);

        return bytes;
    }

    /**
     * @param b byte
     * @return boolean
     */
    public static boolean toBoolean(final byte b)
    {
        return b != 0;
    }

    /**
     * Convert a byte[] into an instance of our value class.
     *
     * @param buf byte[]
     * @return boolean[]
     */
    public static boolean[] toBooleanArray(final byte[] buf)
    {
        int n = buf.length;
        boolean[] a = new boolean[n];

        for (int i = 0; i < n; i++)
        {
            a[i] = toBoolean(buf[i]);
        }

        return a;
    }

    /**
     * @param b boolean
     * @return byte[]
     */
    public static byte[] toByteArray(final boolean b)
    {
        byte[] buf = new byte[1];

        if (b)
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
     * @param a boolean[]
     * @return byte[]
     */
    public static byte[] toByteArray(final boolean[] a)
    {
        if (a == null)
        {
            return null;
        }

        int n = a.length;
        byte[] buf = new byte[n];

        for (int i = 0; i < n; i++)
        {
            buf[i] = toByteArray(a[i])[0];
        }

        return buf;
    }

    /**
     * @param c char
     * @return byte[]
     */
    public static byte[] toByteArray(final char c)
    {
        byte[] buf = new byte[2];

        buf[0] = (byte) ((c >>> 8) & 0xFF);
        buf[1] = (byte) (c & 0xFF);

        return buf;
    }

    /**
     * Convert an instance of our value class into a byte[].
     *
     * @param a char[]
     * @return byte[]
     */
    public static byte[] toByteArray(final char[] a)
    {
        if (a == null)
        {
            return null;
        }

        int n = a.length;
        byte[] buf = new byte[n * 2];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = toByteArray(a[i++]);

            for (byte element : tmp)
            {
                buf[j++] = element;
            }
        }

        return buf;
    }

    /**
     * @param value double
     * @return byte[]
     */
    public static byte[] toByteArray(final double value)
    {
        byte[] buf = new byte[8];
        long x = Double.doubleToRawLongBits(value);

        buf[0] = (byte) ((x >>> 56) & 0xFF);
        buf[1] = (byte) ((x >>> 48) & 0xFF);
        buf[2] = (byte) ((x >>> 40) & 0xFF);
        buf[3] = (byte) ((x >>> 32) & 0xFF);
        buf[4] = (byte) ((x >>> 24) & 0xFF);
        buf[5] = (byte) ((x >>> 16) & 0xFF);
        buf[6] = (byte) ((x >>> 8) & 0xFF);
        buf[7] = (byte) (x & 0xFF);

        return buf;
    }

    /**
     * Convert an instance of our value class into a byte[].
     *
     * @param a double[]
     * @return byte[]
     */
    public static byte[] toByteArray(final double[] a)
    {
        if (a == null)
        {
            return null;
        }

        int n = a.length;
        byte[] buf = new byte[n * 8];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = toByteArray(a[i++]);

            for (byte element : tmp)
            {
                buf[j++] = element;
            }
        }

        return buf;
    }

    /**
     * @param f float
     * @return byte[]
     */
    public static byte[] toByteArray(final float f)
    {
        byte[] buf = new byte[4];
        int x = Float.floatToRawIntBits(f);

        buf[0] = (byte) ((x >>> 24) & 0xFF);
        buf[1] = (byte) ((x >>> 16) & 0xFF);
        buf[2] = (byte) ((x >>> 8) & 0xFF);
        buf[3] = (byte) (x & 0xFF);

        return buf;
    }

    /**
     * Convert an instance of our value class into a byte[].
     *
     * @param a float[]
     * @return byte[]
     */
    public static byte[] toByteArray(final float[] a)
    {
        if (a == null)
        {
            return null;
        }

        int n = a.length;
        byte[] buf = new byte[n * 4];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = toByteArray(a[i++]);

            for (byte element : tmp)
            {
                buf[j++] = element;
            }
        }

        return buf;
    }

    /**
     * @param value int
     * @return byte[]
     */
    public static byte[] toByteArray(final int value)
    {
        byte[] buf = new byte[4];

        buf[0] = (byte) ((value >>> 24) & 0xFF);
        buf[1] = (byte) ((value >>> 16) & 0xFF);
        buf[2] = (byte) ((value >>> 8) & 0xFF);
        buf[3] = (byte) (value & 0xFF);

        return buf;
    }

    /**
     * Convert an instance of our value class into a byte[].
     *
     * @param a int[]
     * @return byte[]
     */
    public static byte[] toByteArray(final int[] a)
    {
        if (a == null)
        {
            return null;
        }

        int n = a.length;
        byte[] buf = new byte[n * 4];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = toByteArray(a[i++]);

            for (byte element : tmp)
            {
                buf[j++] = element;
            }
        }

        return buf;
    }

    /**
     * @param value long
     * @return byte[]
     */
    public static byte[] toByteArray(final long value)
    {
        byte[] buf = new byte[8];

        buf[0] = (byte) ((value >>> 56) & 0xFF);
        buf[1] = (byte) ((value >>> 48) & 0xFF);
        buf[2] = (byte) ((value >>> 40) & 0xFF);
        buf[3] = (byte) ((value >>> 32) & 0xFF);
        buf[4] = (byte) ((value >>> 24) & 0xFF);
        buf[5] = (byte) ((value >>> 16) & 0xFF);
        buf[6] = (byte) ((value >>> 8) & 0xFF);
        buf[7] = (byte) (value & 0xFF);

        return buf;
    }

    /**
     * Convert an instance of our value class into a byte[].
     *
     * @param a long[]
     * @return byte[]
     */
    public static byte[] toByteArray(final long[] a)
    {
        if (a == null)
        {
            return null;
        }

        int n = a.length;
        byte[] buf = new byte[n * 8];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = toByteArray(a[i++]);

            for (byte element : tmp)
            {
                buf[j++] = element;
            }
        }

        return buf;
    }

    /**
     * @param value short
     * @return byte[]
     */
    public static byte[] toByteArray(final short value)
    {
        byte[] buf = new byte[2];

        buf[0] = (byte) ((value >>> 8) & 0xFF);
        buf[1] = (byte) (value & 0xFF);

        return buf;
    }

    /**
     * Convert an instance of our value class into a byte[].
     *
     * @param a short[]
     * @return byte[]
     */
    public static byte[] toByteArray(final short[] a)
    {
        if (a == null)
        {
            return null;
        }

        int n = a.length;
        byte[] buf = new byte[n * 2];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = toByteArray(a[i++]);

            for (byte element : tmp)
            {
                buf[j++] = element;
            }
        }

        return buf;
    }

    /**
     * @param value String
     * @return byte[]
     */
    public static byte[] toByteArray(final String value)
    {
        char[] chars = value.toCharArray();
        int size = chars.length;
        byte[] bytes = new byte[size];

        for (int i = 0; i < size;)
        {
            bytes[i] = (byte) chars[i++];
        }

        return bytes;
    }

    /**
     * @param buf byte[]
     * @return char
     */
    public static char toChar(final byte[] buf)
    {
        return (char) (((buf[0] & 0xFF) << 8) + (buf[1] & 0xFF));
    }

    /**
     * Convert a byte[] into an instance of our value class.
     *
     * @param buf byte[]
     * @return char[]
     */
    public static char[] toCharArray(final byte[] buf)
    {
        int n = buf.length / 2;
        char[] a = new char[n];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = new byte[2];

            for (int k = 0; k < tmp.length; k++)
            {
                tmp[k] = buf[j++];
            }

            a[i++] = toChar(tmp);
        }

        return a;
    }

    /**
     * @param buf byte[]
     * @return double
     */
    public static double toDouble(final byte[] buf)
    {
        // @formatter:off
        return Double.longBitsToDouble(
                ((long) (buf[0] & 0xFF) << 56)
                + ((long) (buf[1] & 0xFF) << 48)
                + ((long) (buf[2] & 0xFF) << 40)
                + ((long) (buf[3] & 0xFF) << 32)
                + ((long) (buf[4] & 0xFF) << 24)
                + ((buf[5] & 0xFF) << 16)
                + ((buf[6] & 0xFF) << 8)
                + (buf[7] & 0xFF));
        // @formatter:on
    }

    /**
     * Convert a byte[] into an instance of our value class.
     *
     * @param buf byte[]
     * @return double[]
     */
    public static double[] toDoubleArray(final byte[] buf)
    {
        int n = buf.length / 8;
        double[] a = new double[n];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = new byte[8];

            for (int k = 0; k < tmp.length; k++)
            {
                tmp[k] = buf[j++];
            }

            a[i++] = toDouble(tmp);
        }

        return a;
    }

    /**
     * @param buf byte[]
     * @return float
     */
    public static float toFloat(final byte[] buf)
    {
        // @formatter:off
        return Float.intBitsToFloat(
                ((buf[0] & 0xFF) << 24)
                + ((buf[1] & 0xFF) << 16)
                + ((buf[2] & 0xFF) << 8)
                + (buf[3] & 0xFF));
        // @formatter:on
    }

    /**
     * Convert a byte[] into an instance of our value class.
     *
     * @param buf byte[]
     * @return float[]
     */
    public static float[] toFloatArray(final byte[] buf)
    {
        int n = buf.length / 4;
        float[] a = new float[n];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = new byte[4];

            for (int k = 0; k < tmp.length; k++)
            {
                tmp[k] = buf[j++];
            }

            a[i++] = toFloat(tmp);
        }

        return a;
    }

    /**
     * @param buf byte[]
     * @return int
     */
    public static int toInt(final byte[] buf)
    {
        // @formatter:off
        return ((buf[0] & 0xFF) << 24)
                + ((buf[1] & 0xFF) << 16)
                + ((buf[2] & 0xFF) << 8)
                + (buf[3] & 0xFF);
        // @formatter:on
    }

    /**
     * Convert a byte[] into an instance of our value class.
     *
     * @param buf byte[]
     * @return int[]
     */
    public static int[] toIntArray(final byte[] buf)
    {
        int n = buf.length / 4;
        int[] a = new int[n];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = new byte[4];

            for (int k = 0; k < tmp.length; k++)
            {
                tmp[k] = buf[j++];
            }

            a[i++] = toInt(tmp);
        }

        return a;
    }

    /**
     * @param buf byte[]
     * @return long
     */
    public static long toLong(final byte[] buf)
    {
        // @formatter:off
        return ((long) (buf[0] & 0xFF) << 56)
                + ((long) (buf[1] & 0xFF) << 48)
                + ((long) (buf[2] & 0xFF) << 40)
                + ((long) (buf[3] & 0xFF) << 32)
                + ((long) (buf[4] & 0xFF) << 24)
                + ((buf[5] & 0xFF) << 16)
                + ((buf[6] & 0xFF) << 8)
                + (buf[7] & 0xFF);
        // @formatter:off
    }

    /**
     * Convert a byte[] into an instance of our value class.
     *
     * @param buf byte[]
     * @return long[]
     */
    public static long[] toLongArray(final byte[] buf)
    {
        int n = buf.length / 8;
        long[] a = new long[n];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = new byte[8];

            for (int k = 0; k < tmp.length; k++)
            {
                tmp[k] = buf[j++];
            }

            a[i++] = toLong(tmp);
        }

        return a;
    }

    /**
     * @param buf byte[]
     * @return short
     */
    public static short toShort(final byte[] buf)
    {
        return (short) (((buf[0] & 0xFF) << 8) + (buf[1] & 0xFF));
    }

    /**
     * Convert a byte[] into an instance of our value class.
     *
     * @param buf byte[]
     * @return short[]
     */
    public static short[] toShortArray(final byte[] buf)
    {
        int n = buf.length / 2;
        short[] a = new short[n];
        int i = 0;
        int j = 0;

        while (i < n)
        {
            byte[] tmp = new byte[2];

            for (int k = 0; k < tmp.length; k++)
            {
                tmp[k] = buf[j++];
            }

            a[i++] = toShort(tmp);
        }

        return a;
    }
}
