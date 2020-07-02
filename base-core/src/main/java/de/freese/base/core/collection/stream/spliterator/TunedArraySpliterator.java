// Created: 15.09.2016
package de.freese.base.core.collection.stream.spliterator;

import java.util.Spliterator;
import java.util.Spliterators;

/**
 * Alternative zum Spliterators.ArraySpliterator.<br>
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 * @see Spliterators (ArraySpliterator)
 * @deprecated Default Implementierung Spliterators.ArraySpliterator ist bereits performant.
 */
@Deprecated
public class TunedArraySpliterator<T> extends AbstractTunedSpliterator<T>
{
    /**
     *
     */
    private final T[] array;

    /**
     * Erzeugt eine neue Instanz von {@link TunedArraySpliterator}
     *
     * @param array Object[]
     */
    public TunedArraySpliterator(final T[] array)
    {
        super(0, array.length);

        this.array = array;
    }

    /**
     * Erzeugt eine neue Instanz von {@link TunedArraySpliterator}
     *
     * @param array Object[]
     * @param characteristics int
     */
    public TunedArraySpliterator(final T[] array, final int characteristics)
    {
        this(array, 0, array.length, characteristics);
    }

    /**
     * Erzeugt eine neue Instanz von {@link TunedArraySpliterator}
     *
     * @param array Object[]
     * @param index int; Inklusive
     * @param endIndex int; Exklusivee
     * @param characteristics int
     */
    public TunedArraySpliterator(final T[] array, final int index, final int endIndex, final int characteristics)
    {
        super(index, endIndex, characteristics);

        this.array = array;
    }

    /**
     * @see de.freese.base.core.collection.stream.spliterator.AbstractTunedSpliterator#createSplit(int, int, int)
     */
    @Override
    protected Spliterator<T> createSplit(final int index, final int endIndex, final int characteristics)
    {
        return new TunedArraySpliterator<>(this.array, index, endIndex, characteristics);
    }

    /**
     * @see de.freese.base.core.collection.stream.spliterator.AbstractTunedSpliterator#get(int)
     */
    @Override
    protected T get(final int index)
    {
        return this.array[index];
    }

    /**
     * @see de.freese.base.core.collection.stream.spliterator.AbstractTunedSpliterator#getMaxSize()
     */
    @Override
    protected int getMaxSize()
    {
        return this.array.length;
    }
}
