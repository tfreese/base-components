/**
 * Created: 30.04.2018
 */

package de.freese.base.core.collection;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * Kopie von<br>
 * org.apache.commons.collections4.ListUtils.Partition<br>
 * io.micrometer.core.instrument.util.AbstractPartition<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class Partition<T> extends AbstractList<List<T>>
{
    /**
     * This rounds up on remainder to avoid orphaning.
     *
     * @param <T> Type
     * @param delegate {@link List}
     * @param partitionSize int
     * @return int
     */
    private static <T> int partitionCount(final List<T> delegate, final int partitionSize)
    {
        int result = delegate.size() / partitionSize;

        return (delegate.size() % partitionSize) == 0 ? result : result + 1;

        // return ((this.delegate.size() + this.partitionSize) - 1) / this.partitionSize;
    }

    /**
     *
     */
    private final List<T> delegate;

    /**
     *
     */
    private final int partitionCount;

    /**
     *
     */
    private final int partitionSize;

    /**
     * Erstellt ein neues {@link Partition} Object.
     *
     * @param delegate {@link List}
     * @param partitionSize int; Anzahl der Elemente pro Partition
     */
    public Partition(final List<T> delegate, final int partitionSize)
    {

        super();

        this.delegate = Objects.requireNonNull(delegate, "delegate required");

        if (partitionSize < 1)
        {
            throw new IllegalArgumentException("partitionSize < 1");
        }

        this.partitionSize = partitionSize;
        this.partitionCount = partitionCount(delegate, this.partitionSize);
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

        if (!super.equals(obj) || !(obj instanceof Partition))
        {
            return false;
        }

        Partition<?> other = (Partition<?>) obj;

        return Objects.equals(this.delegate, other.delegate) && (this.partitionSize == other.partitionSize);
    }

    /**
     * @see java.util.AbstractList#get(int)
     */
    @Override
    public List<T> get(final int index)
    {
        final int start = index * this.partitionSize;
        final int end = Math.min(start + this.partitionSize, this.delegate.size());

        return this.delegate.subList(start, end);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();

        return (prime * result) + Objects.hash(this.delegate, this.partitionSize);
    }

    /**
     * @see java.util.AbstractCollection#isEmpty()
     */
    @Override
    public boolean isEmpty()
    {
        return this.delegate.isEmpty();
    }

    /**
     * @see java.util.AbstractCollection#size()
     */
    @Override
    public int size()
    {
        return this.partitionCount;
    }
}
