// Created: 15.09.2016
package de.freese.base.core.collection.stream;

import java.util.List;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import de.freese.base.core.collection.stream.spliterator.GenericSplitableSpliterator;

/**
 * Liste mit fester Länge für die Streaming-API.<br>
 * Diese SplitableList kann Backends eines beliebigen DatenTyps aufnahmen, der über die funktionalen Interfaces angesprochen wird.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class GenericSplitableList<T> extends AbstractSplitableList<T>
{
    /**
    *
    */
    private final Object backend;

    /**
     *
     */
    private final BiConsumer<Integer, T> biConsumerSet;

    /**
     *
     */
    private final Function<Integer, T> functionGet;

    /**
    *
    */
    private final IntSupplier supplierSize;

    /**
     * Erzeugt eine neue Instanz von {@link GenericSplitableList}.
     *
     * @param list {@link GenericSplitableList}
     */
    public GenericSplitableList(final List<T> list)
    {
        this(list, 0, list.size());
    }

    /**
     * Erzeugt eine neue Instanz von {@link GenericSplitableList}.
     *
     * @param list {@link List}
     * @param indexStart int; inklusive
     * @param indexEnd int; inklusive
     */
    public GenericSplitableList(final List<T> list, final int indexStart, final int indexEnd)
    {
        this(list, i -> list.get(i), (i, element) -> list.set(i, element), () -> list.size(), indexStart, indexEnd);
    }

    /**
     * Erzeugt eine neue Instanz von {@link GenericSplitableList}.
     *
     * @param backend Object
     * @param functionGet {@link Function}
     * @param biConsumerSet {@link BiConsumer}
     * @param supplierSize {@link IntSupplier}
     * @param indexStart int; inklusive
     * @param indexEnd int; inklusive
     */
    public GenericSplitableList(final Object backend, final Function<Integer, T> functionGet, final BiConsumer<Integer, T> biConsumerSet,
            final IntSupplier supplierSize, final int indexStart, final int indexEnd)
    {
        super(indexStart, Math.min(supplierSize.getAsInt() - 1, indexEnd));

        this.backend = backend;
        this.functionGet = functionGet;
        this.biConsumerSet = biConsumerSet;
        this.supplierSize = supplierSize;
    }

    /**
     * Erzeugt eine neue Instanz von {@link GenericSplitableList}.
     *
     * @param array Object[]
     */
    public GenericSplitableList(final T[] array)
    {
        this(array, 0, array.length);
    }

    /**
     * Erzeugt eine neue Instanz von {@link GenericSplitableList}.
     *
     * @param array Object[]
     * @param indexStart int; inklusive
     * @param indexEnd int; inklusive
     */
    public GenericSplitableList(final T[] array, final int indexStart, final int indexEnd)
    {
        this(array, i -> array[i], (i, element) -> array[i] = element, () -> array.length, indexStart, indexEnd);
    }

    /**
     * @see de.freese.base.core.collection.stream.SplitableList#get(int)
     */
    @Override
    public T get(final int index)
    {
        return this.functionGet.apply(index + getIndexStart());
    }

    /**
     * @return Object
     */
    protected Object getBackend()
    {
        return this.backend;
    }

    /**
     * @see de.freese.base.core.collection.stream.SplitableList#set(int, java.lang.Object)
     */
    @Override
    public void set(final int index, final T object)
    {
        this.biConsumerSet.accept(index + getIndexStart(), object);
    }

    // /**
    // * @see de.freese.base.core.collection.stream.ISplitableList#sort(java.util.Comparator)
    // */
    // @Override
    // public void sort(final Comparator<? super T> comparator)
    // {
    // Collections.sort(getList(), comparator);
    // }

    /**
     * @see java.lang.Iterable#spliterator()
     */
    @Override
    public Spliterator<T> spliterator()
    {
        return new GenericSplitableSpliterator<>(getBackend(), this.functionGet, this.supplierSize, getIndexStart(), getIndexEnd() + 1, 0);
    }

    /**
     * @see de.freese.base.core.collection.stream.SplitableList#subList(int, int)
     */
    @Override
    public SplitableList<T> subList(final int fromIndex, final int toIndex)
    {
        return new GenericSplitableList<>(getBackend(), this.functionGet, this.biConsumerSet, this.supplierSize, fromIndex,
                Math.min(this.supplierSize.getAsInt() - 1, toIndex));
    }
}
