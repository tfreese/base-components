// Created: 15.09.2016
package de.freese.base.core.collection.stream.spliterator;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import de.freese.base.core.collection.stream.SplitableList;

/**
 * {@link Spliterator} für eine {@link SplitableList}.<br>
 * Performantere Alternative zum {@link Spliterators}.IteratorSpliterator.<br>
 * Dieser Spliterator kann Backends eines beliebigen DatenTyps aufnehmen, der über die funktionalen Interfaces angesprochen wird.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 * @see Spliterators (ArraySpliterator)
 */
public class GenericSplitableSpliterator<T> extends AbstractSplitableSpliterator<T>
{
    /**
     * Zugrunde liegende Struktur.
     */
    private final Object backend;

    /**
     *
     */
    private final Function<Integer, T> functionGet;

    /**
     *
     */
    private final IntSupplier supplierSize;

    /**
     * Erzeugt eine neue Instanz von {@link GenericSplitableSpliterator}.
     *
     * @param list {@link List}
     */
    public GenericSplitableSpliterator(final List<T> list)
    {
        this(list, 0, list.size());
    }

    /**
     * Erzeugt eine neue Instanz von {@link GenericSplitableSpliterator}.
     *
     * @param list {@link List}
     * @param index int; untere Grenze, inklusive
     * @param fence int; obere Grenze, inklusive
     */
    public GenericSplitableSpliterator(final List<T> list, final int index, final int fence)
    {
        this(list, index, fence, 0);
    }

    /**
     * Erzeugt eine neue Instanz von {@link GenericSplitableSpliterator}.
     *
     * @param list {@link List}
     * @param index int; untere Grenze, inklusive
     * @param fence int; obere Grenze, inklusive
     * @param characteristics int
     */
    public GenericSplitableSpliterator(final List<T> list, final int index, final int fence, final int characteristics)
    {
        this(list, i -> list.get(i), () -> list.size(), index, fence, characteristics);
    }

    /**
     * Erzeugt eine neue Instanz von {@link GenericSplitableSpliterator}.<br>
     * Das Backend kann ein beliebiger DatenTyp sein, der über die {@link Function} und {@link Supplier} angesprochen wird.
     *
     * @param backend Object; Beliebiger DatenTyp
     * @param functionGet {@link Function}; Liefert Element am Index, z.B. list.get(i)
     * @param supplierSize {@link IntSupplier}; Liefert die Größe der Struktur, z.B. list.size()
     * @param index int; untere Grenze, inklusive
     * @param fence int; obere Grenze, inklusive
     * @param characteristics int
     */
    public GenericSplitableSpliterator(final Object backend, final Function<Integer, T> functionGet, final IntSupplier supplierSize, final int index,
            final int fence, final int characteristics)
    {
        super(index, fence, characteristics);

        this.backend = backend;
        this.functionGet = functionGet;
        this.supplierSize = supplierSize;
    }

    /**
     * Erzeugt eine neue Instanz von {@link GenericSplitableSpliterator}.
     *
     * @param array Object[]
     */
    public GenericSplitableSpliterator(final T[] array)
    {
        this(array, 0, array.length);
    }

    /**
     * Erzeugt eine neue Instanz von {@link GenericSplitableSpliterator}
     *
     * @param array Object[]
     * @param index int; untere Grenze, inklusive
     * @param fence int; obere Grenze, inklusive
     */
    public GenericSplitableSpliterator(final T[] array, final int index, final int fence)
    {
        this(array, index, fence, 0);
    }

    /**
     * Erzeugt eine neue Instanz von {@link GenericSplitableSpliterator}
     *
     * @param array Object[]
     * @param index int; untere Grenze, inklusive
     * @param fence int; obere Grenze, inklusive
     * @param characteristics int
     */
    public GenericSplitableSpliterator(final T[] array, final int index, final int fence, final int characteristics)
    {
        this(array, i -> array[i], () -> array.length, index, fence, characteristics);
    }

    /**
     * @see de.freese.base.core.collection.stream.spliterator.AbstractSplitableSpliterator#createSplit(int, int, int)
     */
    @Override
    protected Spliterator<T> createSplit(final int index, final int fence, final int characteristics)
    {
        return new GenericSplitableSpliterator<>(this.backend, this.functionGet, this.supplierSize, index, fence, characteristics);
    }

    /**
     * @see de.freese.base.core.collection.stream.spliterator.AbstractSplitableSpliterator#get(int)
     */
    @Override
    protected T get(final int index)
    {
        return this.functionGet.apply(index);
    }

    /**
     * @see de.freese.base.core.collection.stream.spliterator.AbstractSplitableSpliterator#getMaxSize()
     */
    @Override
    protected int getMaxSize()
    {
        return this.supplierSize.getAsInt();
    }
}
