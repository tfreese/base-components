// Created: 30.04.2018
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
     *
     * @return boolean
     */
    public static boolean isEmpty(final Collection<?> collection)
    {
        return (collection == null) || collection.isEmpty();
    }

    /**
     * @param collection {@link Collection}
     *
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
