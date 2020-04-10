/**
 * Created: 30.04.2018
 */

package de.freese.base.utils;

import java.util.Collection;

/**
 * Nützliches für Colelctions.
 *
 * @author Thomas Freese
 */
public final class CollectionUtils
{
    /**
     * @param collection {@link Collection}
     * @param sizeOfPartition int
     * @return int
     */
    public static <T> int getNumberOfPartitions(final Collection<T> collection, final int sizeOfPartition)
    {
        if (isEmpty(collection))
        {
            return 0;
        }

        return ((collection.size() + sizeOfPartition) - 1) / sizeOfPartition;
    }

    /**
     * @param collection {@link Collection}
     * @return boolean
     */
    public static boolean isEmpty(final Collection<?> collection)
    {
        return (collection == null) || collection.isEmpty();
    }

    /**
     * @param collection {@link Collection}
     * @return boolean
     */
    public static boolean isNotEmpty(final Collection<?> collection)
    {
        return !isEmpty(collection);
    }

    /**
     * Erstellt ein neues {@link CollectionUtils} Object.
     */
    private CollectionUtils()
    {
        super();
    }
}
