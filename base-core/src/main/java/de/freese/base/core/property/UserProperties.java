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
 * Oberstes Objekt fuer die Verwaltung der {@link UserProperty}.
 *
 * @author Thomas Freese
 */
public class UserProperties
{
    /**
     * Temporaerer Cache fuer schnelleren Zugriff.
     */
    private Map<PropertyType, Map<String, UserProperty>> cache = new HashMap<>();

    /**
     *
     */
    private String userID;

    /**
     * Erstellt ein neues {@link UserProperties} Object.
     */
    public UserProperties()
    {
        super();
    }

    /**
     * Leeren der Properties.
     */
    public void clear()
    {
        this.cache.clear();
    }

    /**
     * Erzeugt ein neues UserProperty fuer einen Key und einen {@link PropertyType}.
     *
     * @param key String
     * @param type {@link PropertyType}
     * @return {@link UserProperty}
     */
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

    /**
     * Liefert den boolean Wert des Propertys fuer einen Key und {@link PropertyType}.
     *
     * @param key String
     * @param type {@link PropertyType}
     * @return boolean; default = false
     */
    public boolean getBoolean(final String key, final PropertyType type)
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            return false;
        }

        return property.getBoolean();
    }

    /**
     * Liefert das ByteArray des Properties fuer einen Key und {@link PropertyType}.
     *
     * @param key String
     * @param type {@link PropertyType}
     * @return byte[]; default = null
     * @throws IOException wenn die Konvertierung fehlschlaegt.
     */
    public byte[] getByteArray(final String key, final PropertyType type) throws IOException
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            return null;
        }

        return property.getByteArray();
    }

    /**
     * Liefert den double Wert des Propertys fuer einen Key und {@link PropertyType}.
     *
     * @param key String
     * @param type {@link PropertyType}
     * @param defaultValue double, wenn Property null oder die Konvertierung fehlschlaegt.
     * @return double
     */
    public double getDouble(final String key, final PropertyType type, final double defaultValue)
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            return defaultValue;
        }

        return property.getDouble(defaultValue);
    }

    /**
     * Liefert den int Wert des Propertys fuer einen Key und {@link PropertyType}.
     *
     * @param key String
     * @param type {@link PropertyType}
     * @param defaultValue int, wenn Property null oder die Konvertierung fehlschlaegt.
     * @return boolean
     */
    public int getInt(final String key, final PropertyType type, final int defaultValue)
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            return defaultValue;
        }

        return property.getInt(defaultValue);
    }

    /**
     * Liefert den long Wert des Propertys fuer einen Key und {@link PropertyType}.
     *
     * @param key String
     * @param type {@link PropertyType}
     * @param defaultValue long, wenn Property null oder die Konvertierung fehlschlaegt.
     * @return long
     */
    public long getLong(final String key, final PropertyType type, final long defaultValue)
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            return defaultValue;
        }

        return property.getLong(defaultValue);
    }

    /**
     * Liefert das Object des Propertys fuer einen Key und {@link PropertyType}.<br>
     * Dieses Object wird aus einem byte[] deserialisiert.
     *
     * @param <T> Konkreter Objecttyp
     * @param key String
     * @param type {@link PropertyType}
     * @return Object; default = null
     * @throws Exception Falls bei der Deserialisierung was schief geht.
     */
    @SuppressWarnings("unchecked")
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
     * Liefert alle {@link UserProperty} des {@link PropertyType} oder alle.
     *
     * @param type {@link PropertyType}; optional
     * @return {@link List}
     */
    public List<UserProperty> getProperties(final PropertyType type)
    {
        List<UserProperty> properties = new ArrayList<>();

        for (Entry<PropertyType, Map<String, UserProperty>> typeCache : this.cache.entrySet())
        {
            if ((type != null) && typeCache.getKey().equals(type))
            {
                properties.addAll(typeCache.getValue().values());
                break;
            }

            properties.addAll(typeCache.getValue().values());
        }

        return properties;
    }

    /**
     * Liefert das {@link UserProperty} fuer den Key und den Typen.
     *
     * @param key String
     * @param type {@link PropertyType}
     * @return {@link UserProperty}; kann auch null sein
     */
    private UserProperty getProperty(final String key, final PropertyType type)
    {
        return this.cache.computeIfAbsent(type, k -> new HashMap<>()).get(key);
    }

    /**
     * Liefert alle Keys des {@link PropertyType}.
     *
     * @param type {@link PropertyType}
     * @return {@link Set}
     */
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

    /**
     * Liefert den String Wert des Properties fuer einen Key und {@link PropertyType}.
     *
     * @param key String
     * @param type {@link PropertyType}
     * @return String
     */
    public String getString(final String key, final PropertyType type)
    {
        UserProperty property = getProperty(key, type);

        if (property == null)
        {
            return null;
        }

        return property.getString();
    }

    /**
     * Markiert das {@link UserProperty} fuer den Key und den {@link PropertyType} als geloescht.
     *
     * @param key String
     * @param type {@link PropertyType}
     */
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
     * Zuruecksetzen der Statusinformationen isNew, isChanged.<br>
     * Alle Properties mit isDeleted werden entfernt.
     */
    public void resetPropertyStates()
    {
        for (Map<String, UserProperty> typeCache : this.cache.values())
        {
            for (Iterator<UserProperty> iteratorProp = typeCache.values().iterator(); iteratorProp.hasNext();)
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

    /**
     * Setzt den boolean Wert des Propertys fuer einen Key und {@link PropertyType}.
     *
     * @param key String
     * @param type PropertyType
     * @param value boolean
     */
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

    /**
     * Setzt das ByteArray des Properties fuer einen Key und {@link PropertyType}.
     *
     * @param key String
     * @param type {@link PropertyType}
     * @param value byte[]
     * @throws Throwable, wenn Konvertierung fehlschlaegt
     */
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

    /**
     * Setzt den long Wert des Propertys fuer einen Key und {@link PropertyType}.
     *
     * @param key String
     * @param type {@link PropertyType}
     * @param value double
     */
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

    /**
     * Setzt den int Wert des Propertys fuer einen Key und {@link PropertyType}.
     *
     * @param key String
     * @param type {@link PropertyType}
     * @param value int
     */
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

    /**
     * Setzt den long Wert des Propertys fuer einen Key und {@link PropertyType}.
     *
     * @param key String
     * @param type {@link PropertyType}
     * @param value long
     */
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

    /**
     * Setzt den Object Wert des Propertys fuer einen Key und {@link PropertyType}.<br>
     * Dieses Object wird als byte[] serialisiert.
     *
     * @param key String
     * @param type {@link PropertyType}
     * @param value Object
     * @throws Throwable, wenn Konvertierung fehlschlaegt
     */
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

    /**
     * Setzt eine neue Liste der {@link UserProperty}.
     *
     * @param properties {@link List}
     */
    public void setProperties(final List<UserProperty> properties)
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

    /**
     * Setzt den String Wert des Properties fuer einen Key und {@link PropertyType}.
     *
     * @param key String
     * @param type {@link PropertyType}
     * @param value String
     */
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

    /**
     * Setzt den gewaehlten SystemName.
     *
     * @param systemName String
     */
    public void setSystemSprache(final String systemName)
    {
        setString("SYSTEM_NAME", PropertyType.VARIABLE, systemName);
    }

    /**
     * @param userID String
     */
    public void setUserID(final String userID)
    {
        this.userID = userID;
    }
}
