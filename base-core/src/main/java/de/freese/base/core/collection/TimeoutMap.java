package de.freese.base.core.collection;

import java.time.Duration;
import java.time.Instant;
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
 * @author Thomas Freese
 * <a href="https://github.com/apache/commons-collections/blob/master/src/main/java/org/apache/commons/collections4/map/PassiveExpiringMap.java">https://github.com/apache/commons-collections/blob/master/src/main/java/org/apache/commons/collections4/map/PassiveExpiringMap.java</a>
 */
public class TimeoutMap<K, V> extends AbstractMapDecorator<K, V> {

    private static boolean isExpired(final Instant now, final Instant expiration) {
        if (expiration != null) {
            return expiration.isBefore(now);
            //            final long expirationTime = expirationTimeObject;
            //
            //            return expirationTime >= 0L && nowMillis >= expirationTime;
        }

        return false;
    }

    private static Instant now() {
        return Instant.now();
    }

    private final Duration expirationDuration;
    private final Map<K, Instant> expirationMap = new HashMap<>();

    public TimeoutMap(Duration expirationDuration) {
        this(expirationDuration, new HashMap<>());
    }

    public TimeoutMap(Duration expirationDuration, Map<K, V> decoratedMap) {
        super(decoratedMap);

        this.expirationDuration = Objects.requireNonNull(expirationDuration, "expirationDuration required");
    }

    @Override
    public void clear() {
        this.expirationMap.clear();

        super.clear();
    }

    @Override
    public boolean containsKey(final Object key) {
        removeIfExpired(key, now());

        return super.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        removeAllExpired(now());

        return super.containsValue(value);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        removeAllExpired(now());

        return super.entrySet();
    }

    @Override
    public V get(final Object key) {
        removeIfExpired(key, now());

        return super.get(key);
    }

    @Override
    public boolean isEmpty() {
        removeAllExpired(now());

        return super.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        removeAllExpired(now());

        return super.keySet();
    }

    @Override
    public V put(final K key, final V value) {
        final Instant expiration = Instant.now().plus(expirationDuration);

        this.expirationMap.put(key, expiration);

        return super.put(key, value);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        //        map.forEach(this::put);

        final Instant expiration = Instant.now().plus(expirationDuration);

        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            this.expirationMap.put(entry.getKey(), expiration);
            super.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(final Object key) {
        this.expirationMap.remove(key);

        return super.remove(key);
    }

    @Override
    public int size() {
        removeAllExpired(now());

        return super.size();
    }

    @Override
    public Collection<V> values() {
        removeAllExpired(now());

        return super.values();
    }

    /**
     * Removes all entries in the map whose expiration time is less than
     * {@code now}. The exceptions are entries with negative expiration
     * times; those entries are never removed.
     */
    private void removeAllExpired(final Instant now) {
        final Iterator<Map.Entry<K, Instant>> iter = expirationMap.entrySet().iterator();

        while (iter.hasNext()) {
            final Map.Entry<K, Instant> expirationEntry = iter.next();

            if (isExpired(now, expirationEntry.getValue())) {
                // remove entry from decorated map
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
    private void removeIfExpired(final Object key, final Instant now) {
        final Instant expiration = expirationMap.get(key);

        if (isExpired(now, expiration)) {
            remove(key);
        }
    }
}
