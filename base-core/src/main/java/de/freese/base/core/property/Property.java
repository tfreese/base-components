package de.freese.base.core.property;

/**
 * Property Objekt.
 *
 * @author Thomas Freese
 */
public class Property
{
    private String key;

    private String value;

    public boolean getBoolean()
    {
        return PropertyConverter.getBoolean(getValue());
    }

    public byte[] getByteArray()
    {
        return PropertyConverter.getByteArray(getValue());
    }

    public double getDouble()
    {
        return PropertyConverter.getDouble(getValue());
    }

    public double getDouble(final double defaultValue)
    {
        return PropertyConverter.getDouble(getValue(), defaultValue);
    }

    public int getInt()
    {
        return PropertyConverter.getInt(getValue());
    }

    public int getInt(final int defaultValue)
    {
        return PropertyConverter.getInt(getValue(), defaultValue);
    }

    public String getKey()
    {
        return this.key;
    }

    public long getLong()
    {
        return PropertyConverter.getLong(getValue());
    }

    public long getLong(final long defaultValue)
    {
        return PropertyConverter.getLong(getValue(), defaultValue);
    }

    public Object getObject()
    {
        return PropertyConverter.getObject(getValue());
    }

    public String getString()
    {
        return PropertyConverter.getString(getValue());
    }

    public String getString(final String defaultValue)
    {
        return PropertyConverter.getString(getValue(), defaultValue);
    }

    public void setBoolean(final boolean value)
    {
        setValue(PropertyConverter.toString(value));
    }

    public void setByteArray(final byte[] value)
    {
        setValue(PropertyConverter.toString(value));
    }

    public void setDouble(final double value)
    {
        setValue(PropertyConverter.toString(value));
    }

    public void setInt(final int value)
    {
        setValue(PropertyConverter.toString(value));
    }

    public void setKey(final String value)
    {
        this.key = value;
    }

    public void setLong(final long value)
    {
        setValue(PropertyConverter.toString(value));
    }

    public void setObject(final Object value)
    {
        setValue(PropertyConverter.toString(value));
    }

    public void setString(final String value)
    {
        setValue(PropertyConverter.toString(value));
    }

    protected String getValue()
    {
        return this.value;
    }

    protected void setValue(final String value)
    {
        this.value = value;
    }
}
