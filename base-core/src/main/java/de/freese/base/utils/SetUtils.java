// Created: 09.04.2020
package de.freese.base.utils;

import java.util.Set;

/**
 * @author Thomas Freese
 */
public final class SetUtils
{
    /**
     * @param set {@link Set}
     *
     * @return boolean
     */
    public static boolean isEmpty(final Set<?> set)
    {
        return CollectionUtils.isEmpty(set);
    }

    /**
     * @param set {@link Set}
     *
     * @return boolean
     */
    public static boolean isNotEmpty(final Set<?> set)
    {
        return CollectionUtils.isNotEmpty(set);
    }

    /**
     * Erstellt ein neues {@link SetUtils} Object.
     */
    private SetUtils()
    {
        super();
    }
}
