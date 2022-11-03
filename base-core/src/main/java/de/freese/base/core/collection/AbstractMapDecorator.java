package de.freese.base.core.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @param <K> Key-Type
 * @param <V> Value-Type
 *
 * @author Thomas Freese
 * @see <a href="https://github.com/apache/commons-collections/blob/master/src/main/java/org/apache/commons/collections4/map/AbstractMapDecorator.java">https://github.com/apache/commons-collections/blob/master/src/main/java/org/apache/commons/collections4/map/AbstractMapDecorator.java</a>
 */
public abstract class AbstractMapDecorator<K, V> implements Map<K, V>
{
    private final Map<K, V> decoratedMap;

    protected AbstractMapDecorator(Map<K, V> decoratedMap)
    {
        super();

        this.decoratedMap = Objects.requireNonNull(decoratedMap, "decoratedMap required");
    }

    @Override
    public void clear()
    {
        getDecoratedMap().clear();
    }

    @Override
    public boolean containsKey(final Object key)
    {
        return getDecoratedMap().containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value)
    {
        return getDecoratedMap().containsValue(value);
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        return getDecoratedMap().entrySet();
    }

    @Override
    public boolean equals(final Object object)
    {
        if (object == this)
        {
            return true;
        }

        return getDecoratedMap().equals(object);
    }

    @Override
    public V get(final Object key)
    {
        return getDecoratedMap().get(key);
    }

    @Override
    public int hashCode()
    {
        return getDecoratedMap().hashCode();
    }

    @Override
    public boolean isEmpty()
    {
        return getDecoratedMap().isEmpty();
    }

    @Override
    public Set<K> keySet()
    {
        return getDecoratedMap().keySet();
    }

    @Override
    public V put(final K key, final V value)
    {
        return getDecoratedMap().put(key, value);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> mapToCopy)
    {
        getDecoratedMap().putAll(mapToCopy);
    }

    @Override
    public V remove(final Object key)
    {
        return getDecoratedMap().remove(key);
    }

    @Override
    public int size()
    {
        return getDecoratedMap().size();
    }

    @Override
    public String toString()
    {
        return getDecoratedMap().toString();
    }

    @Override
    public Collection<V> values()
    {
        return getDecoratedMap().values();
    }

    protected Map<K, V> getDecoratedMap()
    {
        return this.decoratedMap;
    }
}
