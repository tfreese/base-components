/**
 *
 */
package de.freese.base.core.property;

import java.io.Serializable;
import org.apache.commons.codec.binary.Base64;
import de.freese.base.utils.ByteUtils;

/**
 * Konvertiert einen Propertywert in konkrete Objekte.
 *
 * @author Thomas Freese
 */
public final class PropertyConverter
{
    /**
     * Liefert den boolean Wert des Propertys.<br>
     *
     * @param value String
     * @return boolean; default = false
     */
    public static boolean getBoolean(final String value)
    {
        return Boolean.parseBoolean(value);
    }

    /**
     * Liefert ein Base64 codiertes ByteArray des Properties.
     *
     * @param value String
     * @return byte[]
     */
    public static byte[] getByteArray(final String value)
    {
        try
        {
            return Base64.decodeBase64(value);
        }
        catch (Exception ex)
        {
            RuntimeException rex = new RuntimeException(ex);
            rex.setStackTrace(ex.getStackTrace());

            throw rex;
        }
    }

    /**
     * Liefert den double Wert des Propertys.
     *
     * @param value String
     * @return double
     */
    public static double getDouble(final String value)
    {
        return Double.parseDouble(value);
    }

    /**
     * Liefert den double Wert des Propertys.
     *
     * @param value String
     * @param defaultValue double, wenn Konvertierung fehlschlaegt
     * @return double
     */
    public static double getDouble(final String value, final double defaultValue)
    {
        try
        {
            return getDouble(value);
        }
        catch (Exception ex)
        {
            return defaultValue;
        }
    }

    /**
     * Liefert den int Wert des Propertys.
     *
     * @param value String
     * @return boolean
     * @throws NumberFormatException wenn die Konvertierung fehlschlaegt.
     */
    public static int getInt(final String value)
    {
        return Integer.parseInt(value);
    }

    /**
     * Liefert den int Wert des Propertys.
     *
     * @param value String
     * @param defaultValue int, wenn die Konvertierung fehlschlaegt.
     * @return boolean
     */
    public static int getInt(final String value, final int defaultValue)
    {
        try
        {
            return getInt(value);
        }
        catch (Exception ex)
        {
            return defaultValue;
        }
    }

    /**
     * Liefert den long Wert des Propertys.
     *
     * @param value String
     * @return long
     * @throws NumberFormatException wenn die Konvertierung fehlschlaegt.
     */
    public static long getLong(final String value)
    {
        return Long.parseLong(value);
    }

    /**
     * Liefert den long Wert des Propertys.
     *
     * @param value String
     * @param defaultValue long, wenn die Konvertierung fehlschlaegt.
     * @return long
     */
    public static long getLong(final String value, final long defaultValue)
    {
        try
        {
            return getLong(value);
        }
        catch (Exception ex)
        {
            return defaultValue;
        }
    }

    /**
     * Liefert das Object Wert des Propertys.<br>
     * Dieses Object wird aus einem byte[] deserialisiert.
     *
     * @param <T> Konkreter Objekttyp
     * @param value String
     * @return Object
     */
    @SuppressWarnings("unchecked")
    public static <T> T getObject(final String value)
    {
        Object result = null;

        try
        {
            byte[] bytes = getByteArray(value);

            if (bytes != null)
            {
                result = ByteUtils.deserializeObject(bytes);
            }
        }
        catch (Exception ex)
        {
            RuntimeException rex = new RuntimeException(ex);
            rex.setStackTrace(ex.getStackTrace());

            throw rex;
        }

        return (T) result;
    }

    /**
     * Liefert den String Wert des Properties.
     *
     * @param value String
     * @return String
     */
    public static String getString(final String value)
    {
        return value;
    }

    /**
     * Liefert den String Wert des Properties oder defaultValue wenn Wert = null.
     *
     * @param value String
     * @param defaultValue String, wenn Wert = null
     * @return String
     */
    public static String getString(final String value, final String defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }

        return value;
    }

    /**
     * Setzt den boolean Wert des Propertys.
     *
     * @param value boolean
     * @return String
     */
    public static String toString(final boolean value)
    {
        return Boolean.toString(value);
    }

    /**
     * Setzt ein ByteArray und konvertierte es als Base64 String.
     *
     * @param value byte[]
     * @return String
     */
    public static String toString(final byte[] value)
    {
        try
        {
            return Base64.encodeBase64String(value);
        }
        catch (Exception ex)
        {
            RuntimeException rex = new RuntimeException(ex);
            rex.setStackTrace(ex.getStackTrace());

            throw rex;
        }
    }

    /**
     * Setzt den long Wert des Propertys.
     *
     * @param value double
     * @return String
     */
    public static String toString(final double value)
    {
        return Double.toString(value);
    }

    /**
     * Setzt den int Wert des Propertys.
     *
     * @param value int
     * @return String
     */
    public static String toString(final int value)
    {
        return Integer.toString(value);
    }

    /**
     * Setzt den long Wert des Propertys.
     *
     * @param value long
     * @return String
     */
    public static String toString(final long value)
    {
        return Long.toString(value);
    }

    /**
     * Setzt den Object Wert des Propertys.<br>
     * Dieses Object wird als byte[] serialisiert.
     *
     * @param value Object
     * @return String
     */
    public static String toString(final Object value)
    {
        if (value == null)
        {
            return null;
        }

        try
        {
            return toString(ByteUtils.serializeObject((Serializable) value));
        }
        catch (Exception ex)
        {
            RuntimeException rex = new RuntimeException(ex);
            rex.setStackTrace(ex.getStackTrace());

            throw rex;
        }
    }

    /**
     * Setzt den Wert des Propertys.
     *
     * @param value long
     * @return String
     */
    public static String toString(final String value)
    {
        return value;
    }

    /**
     * Erstellt ein neues {@link PropertyConverter} Object.
     */
    private PropertyConverter()
    {
        super();
    }
}
