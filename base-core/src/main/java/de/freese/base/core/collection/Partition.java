/**
 * Created: 30.04.2018
 */

package de.freese.base.core.collection;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * Kopie von org.apache.commons.collections4.ListUtils.Partition.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class Partition<T> extends AbstractList<List<T>>
{
    /**
     *
     */
    private final int batchSize;

    /**
     *
     */
    private final List<T> list;

    /**
     * Erstellt ein neues {@link Partition} Object.
     *
     * @param list {@link List}
     * @param batchSize int; Anzahl der Elemente pro Partition
     */
    public Partition(final List<T> list, final int batchSize)
    {

        super();

        this.list = Objects.requireNonNull(list, "list required");

        if (batchSize <= 0)
        {
            throw new IllegalArgumentException("Size must be greater than 0");
        }

        this.batchSize = batchSize;
    }

    /**
     * @see java.util.AbstractList#get(int)
     */
    @Override
    public List<T> get(final int index)
    {
        final int listSize = size();

        if (listSize < 0)
        {
            throw new IllegalArgumentException("negative size: " + listSize);
        }

        if (index < 0)
        {
            throw new IndexOutOfBoundsException("Index " + index + " must not be negative");
        }

        if (index >= listSize)
        {
            throw new IndexOutOfBoundsException("Index " + index + " must be less than size " + listSize);
        }

        final int start = index * this.batchSize;
        final int end = Math.min(start + this.batchSize, this.list.size());

        return this.list.subList(start, end);
    }

    /**
     * @see java.util.AbstractCollection#isEmpty()
     */
    @Override
    public boolean isEmpty()
    {
        return this.list.isEmpty();
    }

    /**
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size()
    {
        return ((this.list.size() + this.batchSize) - 1) / this.batchSize;
    }
}
