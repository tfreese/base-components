package de.freese.base.core.collection;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Thomas Freese
 */
public class ArrayIterator<T> implements Iterator<T>
{
    private final Object array;

    private final int endIndex;

    private int position;

    public ArrayIterator(final Object array)
    {
        this(array, 0, Array.getLength(array));
    }

    public ArrayIterator(final Object array, final int startIndex, final int endIndex)
    {
        super();

        this.array = array;
        this.position = startIndex;
        this.endIndex = endIndex;
    }

    /**
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext()
    {
        return this.position < this.endIndex;
    }

    /**
     * @see java.util.Iterator#next()
     */
    @Override
    public T next()
    {
        if (hasNext())
        {
            return (T) Array.get(this.array, this.position++);
        }

        throw new NoSuchElementException("Array index: " + this.position);
    }

    /**
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove()
    {
        // Array.set(this.array, this.position, null);

        throw new UnsupportedOperationException("remove() method is not supported");
    }
}
