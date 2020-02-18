/**
 * Created: 15.03.2017
 */

package de.freese.base.core.comparator;

import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Ein {@link Comparator}, der nicht sortiert, sondern alles durcheinander bringt.
 * 
 * @author Thomas Freese
 * @param <T> Konkreter Objekt-Typ
 */
public class ShuffleComparator<T> implements Comparator<T>
{
    /**
     *
     */
    private final Random random;

    /**
     * Erstellt ein neues {@link ShuffleComparator} Object.
     */
    public ShuffleComparator()
    {
        this(ThreadLocalRandom.current());
    }

    /**
     * Erstellt ein neues {@link ShuffleComparator} Object.
     *
     * @param random {@link Random}
     */
    public ShuffleComparator(final Random random)
    {
        super();

        this.random = random;
    }

    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(final T o1, final T o2)
    {
        return Integer.compare(valueFor(o1), valueFor(o2));
    }

    /**
     * Liefert den nächsten Random-Wert für das Objekt.
     *
     * @param t Object
     * @return int
     */
    protected int valueFor(final T t)
    {
        return this.random.nextInt();
    }
}
