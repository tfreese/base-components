package de.freese.base.core.property;

/**
 * Property Objekt.
 *
 * @author Thomas Freese
 */
public class Property
{
    /**
     *
     */
    private String key;
    /**
     *
     */
    private String value;

    /**
     * Liefert das Property als boolean.
     *
     * @return boolean
     */
    public boolean getBoolean()
    {
        return PropertyConverter.getBoolean(getValue());
    }

    /**
     * Liefert das Property als Byte Array.
     *
     * @return byte[]
     */
    public byte[] getByteArray()
    {
        return PropertyConverter.getByteArray(getValue());
    }

    /**
     * Liefert das Property als double.
     *
     * @return double
     */
    public double getDouble()
    {
        return PropertyConverter.getDouble(getValue());
    }

    /**
     * Liefert das Property als double oder Default wenn null oder Konvertierung fehlschlägt.
     *
     * @param defaultValue double
     *
     * @return double
     */
    public double getDouble(final double defaultValue)
    {
        return PropertyConverter.getDouble(getValue(), defaultValue);
    }

    /**
     * Liefert das Property als int.
     *
     * @return int
     */
    public int getInt()
    {
        return PropertyConverter.getInt(getValue());
    }

    /**
     * Liefert das Property als int oder Default wenn null oder Konvertierung fehlschlägt.
     *
     * @param defaultValue int
     *
     * @return int
     */
    public int getInt(final int defaultValue)
    {
        return PropertyConverter.getInt(getValue(), defaultValue);
    }

    /**
     * Liefert den Key.
     *
     * @return String
     */
    public String getKey()
    {
        return this.key;
    }

    /**
     * Liefert das Property als long.
     *
     * @return long
     */
    public long getLong()
    {
        return PropertyConverter.getLong(getValue());
    }

    /**
     * Liefert das Property als long oder Default wenn null oder Konvertierung fehlschlägt.
     *
     * @param defaultValue long
     *
     * @return long
     */
    public long getLong(final long defaultValue)
    {
        return PropertyConverter.getLong(getValue(), defaultValue);
    }

    /**
     * Liefert das Property als Object.
     *
     * @return Object
     */
    public Object getObject()
    {
        return PropertyConverter.getObject(getValue());
    }

    /**
     * Liefert das Property als String.
     *
     * @return String
     */
    public String getString()
    {
        return PropertyConverter.getString(getValue());
    }

    /**
     * Liefert das Property als String oder Default, wenn null.
     *
     * @param defaultValue String
     *
     * @return String
     */
    public String getString(final String defaultValue)
    {
        return PropertyConverter.getString(getValue(), defaultValue);
    }

    /**
     * @return String
     */
    protected String getValue()
    {
        return this.value;
    }

    /**
     * Setzt das Property als boolean.
     *
     * @param value boolean
     */
    public void setBoolean(final boolean value)
    {
        setValue(PropertyConverter.toString(value));
    }

    /**
     * Setzt das Property als Byte Array.
     *
     * @param value byte[]
     */
    public void setByteArray(final byte[] value)
    {
        setValue(PropertyConverter.toString(value));
    }

    /**
     * Setzt das Property als double.
     *
     * @param value double
     */
    public void setDouble(final double value)
    {
        setValue(PropertyConverter.toString(value));
    }

    /**
     * Setzt das Property als int.
     *
     * @param value int
     */
    public void setInt(final int value)
    {
        setValue(PropertyConverter.toString(value));
    }

    /**
     * Setzt den Key.
     *
     * @param value String
     */
    public void setKey(final String value)
    {
        this.key = value;
    }

    /**
     * Setzt das Property als long.
     *
     * @param value long
     */
    public void setLong(final long value)
    {
        setValue(PropertyConverter.toString(value));
    }

    /**
     * Setzt das Property als Object.
     *
     * @param value Object
     */
    public void setObject(final Object value)
    {
        setValue(PropertyConverter.toString(value));
    }

    /**
     * Setzt den String.
     *
     * @param value Stirng
     */
    public void setString(final String value)
    {
        setValue(PropertyConverter.toString(value));
    }

    /**
     * @param value String
     */
    protected void setValue(final String value)
    {
        this.value = value;
    }
}
