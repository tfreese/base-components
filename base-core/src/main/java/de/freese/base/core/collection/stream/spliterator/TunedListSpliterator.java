// Created: 15.09.2016
package de.freese.base.core.collection.stream.spliterator;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;

/**
 * Performantere Alternative zum {@link Spliterators}.IteratorSpliterator.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 * @see Spliterators (ArraySpliterator)
 */
public class TunedListSpliterator<T> extends AbstractTunedSpliterator<T>
{
    /**
     *
     */
    private final List<T> list;

    /**
     * Erzeugt eine neue Instanz von {@link TunedListSpliterator}
     *
     * @param list {@link List}
     */
    public TunedListSpliterator(final List<T> list)
    {
        super(0, list.size());

        this.list = list;
    }

    /**
     * Erzeugt eine neue Instanz von {@link TunedListSpliterator}
     *
     * @param list {@link List}
     * @param characteristics int
     */
    public TunedListSpliterator(final List<T> list, final int characteristics)
    {
        this(list, 0, list.size(), characteristics);
    }

    /**
     * Erzeugt eine neue Instanz von {@link TunedListSpliterator}
     *
     * @param list {@link List}
     * @param index int; Inklusive
     * @param endIndex int; Exklusive
     * @param characteristics int
     */
    public TunedListSpliterator(final List<T> list, final int index, final int endIndex, final int characteristics)
    {
        super(index, endIndex, characteristics);

        this.list = list;
    }

    /**
     * @see de.freese.base.core.collection.stream.spliterator.AbstractTunedSpliterator#createSplit(int, int, int)
     */
    @Override
    protected Spliterator<T> createSplit(final int index, final int endIndex, final int characteristics)
    {
        return new TunedListSpliterator<>(this.list, index, endIndex, characteristics);
    }

    /**
     * @see de.freese.base.core.collection.stream.spliterator.AbstractTunedSpliterator#get(int)
     */
    @Override
    protected T get(final int index)
    {
        return this.list.get(index);
    }

    /**
     * @see de.freese.base.core.collection.stream.spliterator.AbstractTunedSpliterator#getMaxSize()
     */
    @Override
    protected int getMaxSize()
    {
        return this.list.size();
    }
}
