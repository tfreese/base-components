/**
 *
 */
package de.freese.base.core.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * {@link Iterator} für Arrays.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class ArrayIterator<T> implements Iterator<T>
{
    /**
     * @param array double[]
     * @return {@link Iterator}
     */
    @SuppressWarnings(
    {
            "unchecked", "rawtypes"
    })
    public static final Iterator<Double> of(final double...array)
    {
        return new ArrayIterator(array);
    }

    /**
     * @param array float[]
     * @return {@link Iterator}
     */
    @SuppressWarnings(
    {
            "unchecked", "rawtypes"
    })
    public static final Iterator<Float> of(final float...array)
    {
        return new ArrayIterator(array);
    }

    /**
     * @param array int[]
     * @return {@link Iterator}
     */
    @SuppressWarnings(
    {
            "unchecked", "rawtypes"
    })
    public static final Iterator<Integer> of(final int...array)
    {
        return new ArrayIterator(array);
    }

    /**
     * @param array long[]
     * @return {@link Iterator}
     */
    @SuppressWarnings(
    {
            "unchecked", "rawtypes"
    })
    public static final Iterator<Long> of(final long...array)
    {
        return new ArrayIterator(array);
    }

    /**
     * 
     */
    private final T[] array;

    /**
     * 
     */
    private int index = 0;

    /**
     * Erstellt ein neues {@link ArrayIterator} Objekt.
     * 
     * @param array Object[]
     */
    @SafeVarargs
    private ArrayIterator(final T...array)
    {
        super();

        this.array = array;
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext()
    {
        return (this.index < this.array.length);
    }

    /**
     * @see java.util.Iterator#next()
     */
    @Override
    public T next()
    {
        if (this.index >= this.array.length)
        {
            throw new NoSuchElementException("Array index: " + this.index);
        }

        T object = this.array[this.index];
        this.index++;

        return object;
    }

    /**
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove()
    {
        if (this.index == 0)
        {
            throw new IllegalStateException("Array index: " + this.index);
        }

        this.array[this.index - 1] = null;
    }
}
