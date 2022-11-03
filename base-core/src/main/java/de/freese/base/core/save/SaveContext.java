package de.freese.base.core.save;

import java.util.HashMap;
import java.util.Map;

/**
 * Klasse für temporäre Daten, welche während des Speichervorgangs benötigt werden.
 *
 * @author Thomas Freese
 */
public class SaveContext
{
    private Map<Object, Object> attributes = new HashMap<>();

    private Map<Class<?>, Map<Long, Long>> primaryKeyMap = new HashMap<>();

    public void clear()
    {
        this.primaryKeyMap.clear();
        this.primaryKeyMap = null;

        this.attributes.clear();
        this.attributes = null;
    }

    public Object getAttribute(final Object key)
    {
        return this.attributes.get(key);
    }

    /**
     * Liefert für eine Klasse und einer OID den konkreten PrimaryKey.<br>
     * Existiert kein konkreter PrimaryKey wird die OID geliefert.
     */
    public long getPrimaryKey(final Class<?> clazz, final Long oid)
    {
        Map<Long, Long> clazzMap = getClazzMap(clazz);

        Long pk = clazzMap.get(oid);

        return pk != null ? pk : oid;
    }

    /**
     * Liefert die Map für das Mapping der Temporären- zu den DB-PrimaryKeys.
     */
    public Map<Class<?>, Map<Long, Long>> getPrimaryKeyMap()
    {
        return this.primaryKeyMap;
    }

    public void putAttribute(final Object key, final Object value)
    {
        this.attributes.put(key, value);
    }

    /**
     * Setzt fär eine Klasse und einer temporären OID den konkreten PrimaryKey.
     */
    public void putPrimaryKey(final Class<?> clazz, final Long tempOID, final long oid)
    {
        Map<Long, Long> clazzMap = getClazzMap(clazz);

        clazzMap.put(tempOID, oid);
    }

    /**
     * Liefert die Map der temporären und konkreten PrimaryKeys einer Klasse.
     */
    private Map<Long, Long> getClazzMap(final Class<?> clazz)
    {
        return this.primaryKeyMap.computeIfAbsent(clazz, key -> new HashMap<>());
    }
}
