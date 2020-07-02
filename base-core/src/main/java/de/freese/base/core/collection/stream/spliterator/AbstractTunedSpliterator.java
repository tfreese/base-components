// Created: 15.09.2016
package de.freese.base.core.collection.stream.spliterator;

import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

/**
 * Performantere Alternative zum {@link Spliterators}.IteratorSpliterator.<br>
 * Basiert auf {@link Spliterators}.ArraySpliterator, jedoch als abstrakte Implementierung.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 * @see Spliterators (ArraySpliterator)
 */
public abstract class AbstractTunedSpliterator<T> implements Spliterator<T>
{
    /**
     * Element-Größe ab der nicht mehr gesplitted wird.
     */
    private static final int DEFAULT_SPLIT_THRESHOLD = Optional.ofNullable(System.getProperty("split.threshold")).map(Integer::parseInt).orElse(5);

    /**
     *
     */
    private final int characteristics;

    /**
     *
     */
    private final int endIndex;

    /**
     *
     */
    private int index = 0;

    /**
     * Element-Größe ab der nicht mehr gesplitted wird.
     */
    private int splitThreshold = DEFAULT_SPLIT_THRESHOLD;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractTunedSpliterator}
     *
     * @param index int; Inklusive
     * @param endIndex int; Exklusive
     */
    public AbstractTunedSpliterator(final int index, final int endIndex)
    {
        this(index, endIndex, Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.ORDERED | Spliterator.NONNULL);
    }

    /**
     * Erzeugt eine neue Instanz von {@link AbstractTunedSpliterator}
     *
     * @param index int; Inklusive
     * @param endIndex int; Exklusive
     * @param characteristics int
     */
    public AbstractTunedSpliterator(final int index, final int endIndex, final int characteristics)
    {
        super();

        this.index = index;
        this.endIndex = endIndex;
        this.characteristics = characteristics;
    }

    /**
     * @see java.util.Spliterator#characteristics()
     */
    @Override
    public int characteristics()
    {
        return this.characteristics;
    }

    /**
     * Liefert den {@link Spliterator} für den Bereich.
     *
     * @param index int
     * @param endIndex int
     * @param characteristics int
     * @return {@link Spliterator}
     */
    protected abstract Spliterator<T> createSplit(final int index, int endIndex, int characteristics);

    /**
     * @see java.util.Spliterator#estimateSize()
     */
    @Override
    public long estimateSize()
    {
        return (long) this.endIndex - this.index;
    }

    // /**
    // * Zur Performance-Optimierung wird hier nicht die Default-Implementierung verwendet.
    // *
    // * @see java.util.Spliterator#forEachRemaining(java.util.function.Consumer)
    // */
    // @Override
    // public void forEachRemaining(final Consumer<? super T> action)
    // {
    // int i = 0;
    //
    // if ((getMaxSize() >= this.endIndex) && ((i = this.index) >= 0) && (i < (this.index = this.endIndex)))
    // {
    // do
    // {
    // action.accept(get(i));
    // }
    // while (++i < this.endIndex);
    // }
    // }

    /**
     * Liefert das Objekt am Index.
     *
     * @param index int
     * @return Object
     */
    protected abstract T get(int index);

    /**
     * Liefert die Gesamte-/Größte Länge der Liste.
     *
     * @return int
     */
    protected abstract int getMaxSize();

    /**
     * Element-Größe ab der nicht mehr gesplitted wird.
     *
     * @return int
     */
    protected int getSplitThreshold()
    {
        return this.splitThreshold;
    }

    /**
     * Element-Größe ab der nicht mehr gesplitted wird.
     *
     * @param splitThreshold int
     */
    public void setSplitThreshold(final int splitThreshold)
    {
        this.splitThreshold = splitThreshold;
    }

    /**
     * @see java.util.Spliterator#tryAdvance(java.util.function.Consumer)
     */
    @Override
    public boolean tryAdvance(final Consumer<? super T> action)
    {
        if ((this.index >= 0) && (this.index < this.endIndex))
        {
            T element = get(this.index++);
            action.accept(element);

            return true;
        }

        return false;
    }

    /**
     * @see java.util.Spliterator#trySplit()
     */
    @Override
    public Spliterator<T> trySplit()
    {
        // Wenn Rest kleiner als n Elemente -> nicht mehr Splitten.
        if ((this.endIndex - this.index) < getSplitThreshold())
        {
            return null;
        }

        int from = this.index;
        int to = (from + this.endIndex) >>> 1; // (index + fence) / 2)

        if (from >= to)
        {
            return null;
        }

        Spliterator<T> spliterator = createSplit(from, to, this.characteristics);

        this.index = to;

        return spliterator;
    }
}
