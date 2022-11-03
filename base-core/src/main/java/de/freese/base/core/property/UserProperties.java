package de.freese.base.core.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Oberstes Objekt für die Verwaltung der {@link UserProperty}.
 *
 * @author Thomas Freese
 */
public class UserProperties
{
    private final Map<PropertyType, Map<String, UserProperty>> cache = new HashMap<>();

    private String userID;

    public void clear()
    {
        this.cache.clear();
    }

    public boolean getBoolean(final String key, final PropertyType type)
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            return false;
        }

        return property.getBoolean();
    }

    public byte[] getByteArray(final String key, final PropertyType type) throws IOException
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            return null;
        }

        return property.getByteArray();
    }

    public double getDouble(final String key, final PropertyType type, final double defaultValue)
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            return defaultValue;
        }

        return property.getDouble(defaultValue);
    }

    public int getInt(final String key, final PropertyType type, final int defaultValue)
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            return defaultValue;
        }

        return property.getInt(defaultValue);
    }

    public long getLong(final String key, final PropertyType type, final long defaultValue)
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            return defaultValue;
        }

        return property.getLong(defaultValue);
    }

    public <T> T getObject(final String key, final PropertyType type) throws Exception
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            return null;
        }

        return (T) property.getObject();
    }

    /**
     * @param type {@link PropertyType}; optional
     */
    public List<UserProperty> getProperties(final PropertyType type)
    {
        List<UserProperty> properties = new ArrayList<>();

        for (Entry<PropertyType, Map<String, UserProperty>> typeCache : this.cache.entrySet())
        {
            if (typeCache.getKey().equals(type))
            {
                properties.addAll(typeCache.getValue().values());
                break;
            }

            properties.addAll(typeCache.getValue().values());
        }

        return properties;
    }

    public Set<String> getPropertyKeys(final PropertyType type)
    {
        if (type == null)
        {
            return Collections.emptySet();
        }

        Set<String> keys = new HashSet<>();

        for (Entry<PropertyType, Map<String, UserProperty>> typeCache : this.cache.entrySet())
        {
            if (typeCache.getKey().equals(type))
            {
                keys.addAll(typeCache.getValue().keySet());
                break;
            }
        }

        return keys;
    }

    public String getString(final String key, final PropertyType type)
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            return null;
        }

        return property.getString();
    }

    public void markDeleted(final String key, final PropertyType type)
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            return;
        }

        if (property.isCreated())
        {
            this.cache.get(type).remove(key);
        }
        else
        {
            property.setDeleted(true);

            // Inhalt auch leeren
            property.setString(null);
        }
    }

    /**
     * Zurücksetzen der Statusinformationen isNew, isChanged.<br>
     * Alle Properties mit isDeleted werden entfernt.
     */
    public void resetPropertyStates()
    {
        for (Map<String, UserProperty> typeCache : this.cache.values())
        {
            for (Iterator<UserProperty> iteratorProp = typeCache.values().iterator(); iteratorProp.hasNext(); )
            {
                UserProperty property = iteratorProp.next();

                if (property.isDeleted())
                {
                    iteratorProp.remove();
                    continue;
                }

                property.setCreated(false);
                property.setChanged(false);
            }
        }
    }

    public void setBoolean(final String key, final PropertyType type, final boolean value)
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            property = createProperty(key, type);
        }

        property.setBoolean(value);
        property.setChanged(true);
    }

    public void setByteArray(final String key, final PropertyType type, final byte[] value) throws Throwable
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            property = createProperty(key, type);
        }

        property.setByteArray(value);
        property.setChanged(true);
    }

    public void setDouble(final String key, final PropertyType type, final double value)
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            property = createProperty(key, type);
        }

        property.setDouble(value);
        property.setChanged(true);
    }

    public void setInt(final String key, final PropertyType type, final int value)
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            property = createProperty(key, type);
        }

        property.setInt(value);
        property.setChanged(true);
    }

    public void setLong(final String key, final PropertyType type, final long value)
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            property = createProperty(key, type);
        }

        property.setLong(value);
        property.setChanged(true);
    }

    public void setObject(final String key, final PropertyType type, final Object value) throws Throwable
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            property = createProperty(key, type);
        }

        property.setObject(value);
        property.setChanged(true);
    }

    public void setProperties(final Iterable<UserProperty> properties)
    {
        // TypeCache aufbauen
        for (PropertyType type : PropertyType.values())
        {
            this.cache.put(type, new HashMap<>());
        }

        // PropertyCache aufbauen
        for (UserProperty property : properties)
        {
            Map<String, UserProperty> typeCache = this.cache.get(property.getTyp());

            typeCache.put(property.getKey(), property);
        }
    }

    public void setString(final String key, final PropertyType type, final String value)
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            property = createProperty(key, type);
        }

        property.setString(value);
        property.setChanged(true);
    }

    public void setSystemSprache(final String systemName)
    {
        setString("SYSTEM_NAME", PropertyType.VARIABLE, systemName);
    }

    public void setUserID(final String userID)
    {
        this.userID = userID;
    }

    private UserProperty createProperty(final String key, final PropertyType type)
    {
        UserProperty property = new UserProperty();
        property.setUserName(this.userID);
        property.setType(type);
        property.setKey(key);
        property.setCreated(true);

        this.cache.get(type).put(key, property);

        return property;
    }

    private UserProperty getProperty(final String key, final PropertyType type)
    {
        return this.cache.computeIfAbsent(type, k -> new HashMap<>()).get(key);
    }
}
