package de.freese.base.core.save;

import java.util.HashMap;
import java.util.Map;

/**
 * Klasse fuer temporaere Daten, welche waehrend des Speichervorgangs benoetigt werden.
 *
 * @author Thomas Freese
 */
public class SaveContext
{
    /**
     *
     */
    private Map<Object, Object> attributes = new HashMap<>();

    /**
     *
     */
    private Map<Class<?>, Map<Long, Long>> primaryKeyMap = new HashMap<>();

    /**
     * Aufraeumen.
     */
    public void clear()
    {
        this.primaryKeyMap.clear();
        this.primaryKeyMap = null;

        this.attributes.clear();
        this.attributes = null;
    }

    /**
     * Liefert das Value fuer den Key oder null.
     *
     * @param key Object
     * @return Object
     */
    public Object getAttribute(final Object key)
    {
        return this.attributes.get(key);
    }

    /**
     * Liefert die Map der temporaeren und konkreten PrimaryKeys einer Klasse.
     *
     * @param clazz Class
     * @return {@link Map}
     */
    private Map<Long, Long> getClazzMap(final Class<?> clazz)
    {
        return this.primaryKeyMap.computeIfAbsent(clazz, key -> new HashMap<>());
    }

    /**
     * Liefert fuer eine Klasse und einer OID den konkreten PrimaryKey.<br>
     * Existiert kein konkreter PrimaryKey wird die OID geliefert.
     *
     * @param clazz Class
     * @param oid Long
     * @return Long
     */
    public long getPrimaryKey(final Class<?> clazz, final Long oid)
    {
        Map<Long, Long> clazzMap = getClazzMap(clazz);

        Long pk = clazzMap.get(oid);

        return pk != null ? pk.longValue() : oid.longValue();
    }

    /**
     * Liefert die Map fuer das Mapping der Temporaeren- zu den DB-PrimaryKeys.
     *
     * @return {@link Map}
     */
    public Map<Class<?>, Map<Long, Long>> getPrimaryKeyMap()
    {
        return this.primaryKeyMap;
    }

    /**
     * Setzt das Value fuer den Key.
     *
     * @param key Object
     * @param value Object
     */
    public void putAttribute(final Object key, final Object value)
    {
        this.attributes.put(key, value);
    }

    /**
     * Setzt fuer eine Klasse und einer temporaeren OID den konkreten PrimaryKey.
     *
     * @param clazz Class
     * @param tempOID Long
     * @param oid long
     */
    public void putPrimaryKey(final Class<?> clazz, final Long tempOID, final long oid)
    {
        Map<Long, Long> clazzMap = getClazzMap(clazz);

        clazzMap.put(tempOID, Long.valueOf(oid));
    }
}
