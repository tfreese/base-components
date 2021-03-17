/**
 * Created: 09.04.2020
 */

package de.freese.base.utils;

import java.util.List;

/**
 * @author Thomas Freese
 */
public final class ListUtils
{
    /**
     * @param list {@link List}
     * @return boolean
     */
    public static boolean isEmpty(final List<?> list)
    {
        return CollectionUtils.isEmpty(list);
    }

    /**
     * @param list {@link List}
     * @return boolean
     */
    public static boolean isNotEmpty(final List<?> list)
    {
        return CollectionUtils.isNotEmpty(list);
    }

    /**
     * Erstellt ein neues {@link ListUtils} Object.
     */
    private ListUtils()
    {
        super();
    }
}
