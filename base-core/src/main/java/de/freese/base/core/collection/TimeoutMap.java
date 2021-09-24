package de.freese.base.core.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * {@link HashMap} Implementierung mit einer Timeout-Funktion der Elemente.<br>
 * Nach Ablauf des Timeouts werden die Elemente beim nächsten Zugriff gelöscht.
 *
 * @author Thomas Freese
 *
 * @param <K> Konkreter Key
 * @param <V> Konkretes Value
 */
public class TimeoutMap<K, V> implements Map<K, V>
{
    /**
    *
    */
    private final Map<K, V> backend;
    /**
     * Millisekunden, Default = 24 Stunden
     */
    private long timeoutInMillis = 1000 * 60 * 60 * 24L;
    /**
     *
     */
    private final Map<K, Long> timestampMap;

    /**
     * Erstellt ein neues {@link TimeoutMap} Object mit einer {@link HashMap} als Backend.
     *
     * @param timeout long
     * @param timeUnit {@link TimeUnit}
     */
    public TimeoutMap(final long timeout, final TimeUnit timeUnit)
    {
        this(new HashMap<>(), timeout, timeUnit);
    }

    /**
     * Erstellt ein neues {@link TimeoutMap} Object.
     *
     * @param backend {@link Map}
     * @param timeout long
     * @param timeUnit {@link TimeUnit}
     */
    public TimeoutMap(final Map<K, V> backend, final long timeout, final TimeUnit timeUnit)
    {
        super();

        this.backend = Objects.requireNonNull(backend, "backend required");
        this.timeoutInMillis = Objects.requireNonNull(timeUnit, "timeUnit required").toMillis(timeout);
        this.timestampMap = new HashMap<>();
    }

    /**
     * @see java.util.Map#clear()
     */
    @Override
    public void clear()
    {
        this.timestampMap.clear();
        this.backend.clear();
    }

    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(final Object key)
    {
        if (isTimedOut(key))
        {
            remove(key);
            return false;
        }

        return this.backend.containsKey(key);
    }

    /**
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(final Object value)
    {
        return entrySet().stream().filter(entry -> entry.getValue() == value).count() > 0;
    }

    /**
     * Es werden nur die Entries geliefert, welche noch nicht abgelaufen sind.<br>
     * Abgelaufene Entries werden gelöscht.
     *
     * @see java.util.Map#entrySet()
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
        // Löschen über Iterator des HashMap.EntrySet funktioniert nicht.
        Set<Map.Entry<K, V>> backendEntrySet = new HashSet<>(this.backend.entrySet());

        for (Iterator<Map.Entry<K, V>> iterator = backendEntrySet.iterator(); iterator.hasNext();)
        {
            Entry<K, V> entry = iterator.next();

            if (isTimedOut(entry.getKey()))
            {
                remove(entry.getKey());
                iterator.remove();
            }
        }

        return backendEntrySet;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        return this.backend.equals(obj);
    }

    /**
     * @see java.util.Map#get(java.lang.Object)
     */
    @Override
    public V get(final Object key)
    {
        if (isTimedOut(key))
        {
            remove(key);
            return null;
        }

        return this.backend.get(key);
    }

    /**
     * Liefert den Timeout.
     *
     * @param timeUnit {@link TimeUnit}
     *
     * @return long
     */
    public long getTimeout(final TimeUnit timeUnit)
    {
        return timeUnit.convert(getTimeoutInMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * Liefert den Timeout in Millisekunden.
     *
     * @return long
     */
    public long getTimeoutInMillis()
    {
        return this.timeoutInMillis;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return this.backend.hashCode();
    }

    /**
     * size = 0 = Anzahl der nicht abgelaufenen Keys.
     *
     * @see java.util.Map#isEmpty()
     */
    @Override
    public boolean isEmpty()
    {
        return size() == 0;
    }

    /**
     * Ein Key ohne Timestamp ist nicht in der Map.<br>
     * Dann soll die normale Implementierung der HashMap greifen.
     *
     * @param key Object
     *
     * @return boolean
     */
    protected boolean isTimedOut(final Object key)
    {
        Long timestamp = this.timestampMap.get(key);

        if (timestamp == null)
        {
            return false;
        }

        return (System.currentTimeMillis() - timestamp) >= getTimeoutInMillis();
    }

    /**
     * Es werden nur die Keys geliefert, welche noch nicht abgelaufen sind.<br>
     * Abgelaufene Keys werden gelöscht.
     *
     * @see java.util.Map#keySet()
     */
    @Override
    public Set<K> keySet()
    {
        // Löschen über Iterator des HashMap.KeySet funktioniert nicht.
        Set<K> backendKeySet = new HashSet<>(this.backend.keySet());

        for (Iterator<K> iterator = backendKeySet.iterator(); iterator.hasNext();)
        {
            K key = iterator.next();

            if (isTimedOut(key))
            {
                remove(key);
                iterator.remove();
            }
        }

        return backendKeySet;
    }

    /**
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public synchronized V put(final K key, final V value)
    {
        this.timestampMap.put(key, System.currentTimeMillis());

        return this.backend.put(key, value);
    }

    /**
     * @see java.util.Map#putAll(java.util.Map)
     */
    @Override
    public void putAll(final Map<? extends K, ? extends V> map)
    {
        for (Entry<? extends K, ? extends V> entry : map.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * @see java.util.Map#remove(java.lang.Object)
     */
    @Override
    public V remove(final Object key)
    {
        this.timestampMap.remove(key);

        return this.backend.remove(key);
    }

    /**
     * Setzt den Timeoput in Millisekunden.
     *
     * @param timeout long
     * @param timeUnit TimeUnit
     */
    public void setTimeout(final long timeout, final TimeUnit timeUnit)
    {
        this.timeoutInMillis = Objects.requireNonNull(timeUnit, "timeUnit required").toMillis(timeout);
    }

    /**
     * size = Anzahl der nicht abgelaufenen Keys.
     *
     * @see java.util.Map#size()
     */
    @Override
    public int size()
    {
        return keySet().size();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.backend.toString();
    }

    /**
     * Alle Values deren Keys nicht abgelaufen sind.
     *
     * @see java.util.Map#values()
     */
    @Override
    public Collection<V> values()
    {
        return entrySet().stream().map(Entry::getValue).collect(Collectors.toList());
    }
}
