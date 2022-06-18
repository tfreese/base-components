package de.freese.base.core.collection;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * {@link Map} Implementierung mit einer Timeout-Funktion der Elemente.<br>
 * Nach Ablauf des Timeouts werden die Elemente beim nächsten Zugriff gelöscht.
 *
 * @param <K> Key-Type
 * @param <V> Value-Type
 *
 * @author Thomas Freese
 * @see <a href="https://github.com/apache/commons-collections/blob/master/src/main/java/org/apache/commons/collections4/map/PassiveExpiringMap.java">https://github.com/apache/commons-collections/blob/master/src/main/java/org/apache/commons/collections4/map/PassiveExpiringMap.java</a>
 */
public class TimeoutMap<K, V> extends AbstractMapDecorator<K, V>
{
    /**
     * A policy to determine the expiration time for key-value entries.
     *
     * @param <K> Key-Type
     * @param <V> Value-Type
     */
    @FunctionalInterface
    public interface ExpirationPolicy<K, V>
    {
        /**
         * Determine the expiration time for the given key-value entry.
         *
         * @param key the key for the entry.
         * @param value the value for the entry.
         *
         * @return the expiration time value measured in milliseconds. A
         * negative return value indicates the entry never expires.
         */
        long expirationTime(K key, V value);
    }

    /**
     * A {@link ExpirationPolicy} that returns a expiration time that is a constant about of time in the future from the current time.
     *
     * @param <K> Key-Type
     * @param <V> Value-Type
     */
    public static class ConstantTimeToLiveExpirationPolicy<K, V>
            implements ExpirationPolicy<K, V>
    {
        /**
         * the constant time-to-live value measured in milliseconds.
         */
        private final long timeToLiveMillis;

        /**
         * Default constructor. Constructs a policy using a negative
         * time-to-live value that results in entries never expiring.
         */
        public ConstantTimeToLiveExpirationPolicy()
        {
            this(-1L);
        }

        public ConstantTimeToLiveExpirationPolicy(Duration timeToLiveDuration)
        {
            this(Objects.requireNonNull(timeToLiveDuration, "timeToLiveDuration required").toMillis());
        }

        /**
         * Construct a policy with the given time-to-live constant measured in
         * milliseconds. A negative time-to-live value indicates entries never
         * expire. A zero time-to-live value indicates entries expire (nearly)
         * immediately.
         *
         * @param timeToLiveMillis the constant amount of time (in milliseconds)
         * an entry is available before it expires. A negative value
         * results in entries that NEVER expire. A zero value results in
         * entries that ALWAYS expire.
         */
        public ConstantTimeToLiveExpirationPolicy(final long timeToLiveMillis)
        {
            super();

            this.timeToLiveMillis = timeToLiveMillis;
        }

        /**
         * Determine the expiration time for the given key-value entry.
         *
         * @param key the key for the entry (ignored).
         * @param value the value for the entry (ignored).
         *
         * @return if {@link #timeToLiveMillis} &ge; 0, an expiration time of
         * {@link #timeToLiveMillis} +
         * {@link System#currentTimeMillis()} is returned. Otherwise, -1
         * is returned indicating the entry never expires.
         */
        @Override
        public long expirationTime(final K key, final V value)
        {
            if (timeToLiveMillis >= 0L)
            {
                // avoid numerical overflow
                final long nowMillis = now();

                if (nowMillis > Long.MAX_VALUE - timeToLiveMillis)
                {
                    // expiration would be greater than Long.MAX_VALUE never expire
                    return -1;
                }

                // timeToLiveMillis in the future
                return nowMillis + timeToLiveMillis;
            }

            // never expire
            return -1L;
        }
    }

    private static long now()
    {
        return System.currentTimeMillis();
    }

    /**
     *
     */
    private final Map<K, Long> expirationMap;
    /**
     *
     */
    private final ExpirationPolicy<K, V> expirationPolicy;

    /**
     * @param expirationDuration {@link Duration}
     */
    public TimeoutMap(Duration expirationDuration)
    {
        this(expirationDuration, new HashMap<>());
    }

    /**
     * @param expirationDuration {@link Duration}
     * @param decoratedMap {@link Map}
     */
    public TimeoutMap(Duration expirationDuration, Map<K, V> decoratedMap)
    {
        this(new ConstantTimeToLiveExpirationPolicy<>(expirationDuration), decoratedMap);
    }

    /**
     * @param expirationPolicy {@link ExpirationPolicy}
     * @param decoratedMap {@link Map}
     */
    public TimeoutMap(ExpirationPolicy<K, V> expirationPolicy, Map<K, V> decoratedMap)
    {
        super(decoratedMap);

        this.expirationPolicy = Objects.requireNonNull(expirationPolicy, "expirationPolicy required");
        this.expirationMap = new HashMap<>();
    }

    /**
     * @see java.util.Map#clear()
     */
    @Override
    public void clear()
    {
        this.expirationMap.clear();
        super.clear();
    }

    /**
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(final Object key)
    {
        removeIfExpired(key, now());

        return super.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value)
    {
        removeAllExpired(now());

        return super.containsValue(value);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet()
    {
        removeAllExpired(now());

        return super.entrySet();
    }

    @Override
    public V get(final Object key)
    {
        removeIfExpired(key, now());

        return super.get(key);
    }

    @Override
    public boolean isEmpty()
    {
        removeAllExpired(now());

        return super.isEmpty();
    }

    @Override
    public Set<K> keySet()
    {
        removeAllExpired(now());

        return super.keySet();
    }

    @Override
    public V put(final K key, final V value)
    {
        // remove the previous record
        removeIfExpired(key, now());

        // record expiration time of new entry
        final long expirationTime = expirationPolicy.expirationTime(key, value);

        this.expirationMap.put(key, expirationTime);

        return super.put(key, value);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> map)
    {
        map.forEach(this::put);
    }

    @Override
    public V remove(final Object key)
    {
        this.expirationMap.remove(key);

        return super.remove(key);
    }

    @Override
    public int size()
    {
        removeAllExpired(now());

        return super.size();
    }

    @Override
    public Collection<V> values()
    {
        removeAllExpired(now());

        return super.values();
    }

    /**
     * Determines if the given expiration time is less than {@code now}.
     *
     * @param nowMillis the time in milliseconds used to compare against the
     * expiration time.
     * @param expirationTimeObject the expiration time value retrieved from
     * {@link #expirationMap}, can be null.
     *
     * @return {@code true} if {@code expirationTimeObject} is &ge; 0
     * and {@code expirationTimeObject} &lt; {@code now}.
     * {@code false} otherwise.
     */
    private boolean isExpired(final long nowMillis, final Long expirationTimeObject)
    {
        if (expirationTimeObject != null)
        {
            final long expirationTime = expirationTimeObject;

            return expirationTime >= 0 && nowMillis >= expirationTime;
        }

        return false;
    }

    /**
     * Removes all entries in the map whose expiration time is less than
     * {@code now}. The exceptions are entries with negative expiration
     * times; those entries are never removed.
     *
     * @see #isExpired(long, Long)
     */
    private void removeAllExpired(final long nowMillis)
    {
        final Iterator<Map.Entry<K, Long>> iter = expirationMap.entrySet().iterator();

        while (iter.hasNext())
        {
            final Map.Entry<K, Long> expirationEntry = iter.next();

            if (isExpired(nowMillis, expirationEntry.getValue()))
            {
                // remove entry from collection
                super.remove(expirationEntry.getKey());

                // remove entry from expiration map
                iter.remove();
            }
        }
    }

    /**
     * Removes the entry with the given key if the entry's expiration time is
     * less than {@code now}. If the entry has a negative expiration time,
     * the entry is never removed.
     */
    private void removeIfExpired(final Object key, final long nowMillis)
    {
        final Long expirationTimeObject = expirationMap.get(key);

        if (isExpired(nowMillis, expirationTimeObject))
        {
            remove(key);
        }
    }
}
