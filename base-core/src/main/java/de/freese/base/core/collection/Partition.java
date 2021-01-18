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
    private final List<T> list;

    /**
     *
     */
    private final int sizeOfPartition;

    /**
     * Erstellt ein neues {@link Partition} Object.
     *
     * @param list {@link List}
     * @param sizeOfPartition int; Anzahl der Elemente pro Partition
     */
    public Partition(final List<T> list, final int sizeOfPartition)
    {

        super();

        this.list = Objects.requireNonNull(list, "list required");

        if (sizeOfPartition <= 0)
        {
            throw new IllegalArgumentException("Size must be greater than 0");
        }

        this.sizeOfPartition = sizeOfPartition;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!super.equals(obj))
        {
            return false;
        }

        if (!(obj instanceof Partition))
        {
            return false;
        }

        Partition<?> other = (Partition<?>) obj;

        return Objects.equals(this.list, other.list) && (this.sizeOfPartition == other.sizeOfPartition);
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

        final int start = index * this.sizeOfPartition;
        final int end = Math.min(start + this.sizeOfPartition, this.list.size());

        return this.list.subList(start, end);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();

        result = (prime * result) + Objects.hash(this.list, this.sizeOfPartition);

        return result;
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
        // Math.ceil(
        return ((this.list.size() + this.sizeOfPartition) - 1) / this.sizeOfPartition;
    }
}
