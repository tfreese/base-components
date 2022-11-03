package de.freese.base.core.property;

import java.io.Serializable;
import java.util.Base64;

import de.freese.base.utils.ByteUtils;

/**
 * Konvertiert einen Propertywert in konkrete Objekte.
 *
 * @author Thomas Freese
 */
public final class PropertyConverter
{
    public static boolean getBoolean(final String value)
    {
        return Boolean.parseBoolean(value);
    }

    public static byte[] getByteArray(final String value)
    {
        try
        {
            return Base64.getDecoder().decode(value);
        }
        catch (Exception ex)
        {
            RuntimeException rex = new RuntimeException(ex);
            rex.setStackTrace(ex.getStackTrace());

            throw rex;
        }
    }

    public static double getDouble(final String value)
    {
        return Double.parseDouble(value);
    }

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

    public static int getInt(final String value)
    {
        return Integer.parseInt(value);
    }

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

    public static long getLong(final String value)
    {
        return Long.parseLong(value);
    }

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

    public static String getString(final String value)
    {
        return value;
    }

    public static String getString(final String value, final String defaultValue)
    {
        if (value == null)
        {
            return defaultValue;
        }

        return value;
    }

    public static String toString(final boolean value)
    {
        return Boolean.toString(value);
    }

    public static String toString(final byte[] value)
    {
        try
        {
            return Base64.getEncoder().encodeToString(value);
        }
        catch (Exception ex)
        {
            RuntimeException rex = new RuntimeException(ex);
            rex.setStackTrace(ex.getStackTrace());

            throw rex;
        }
    }

    public static String toString(final double value)
    {
        return Double.toString(value);
    }

    public static String toString(final int value)
    {
        return Integer.toString(value);
    }

    public static String toString(final long value)
    {
        return Long.toString(value);
    }

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

    public static String toString(final String value)
    {
        return value;
    }

    private PropertyConverter()
    {
        super();
    }
}
