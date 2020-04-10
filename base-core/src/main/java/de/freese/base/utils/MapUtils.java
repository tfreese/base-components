/**
 * Created: 09.04.2020
 */

package de.freese.base.utils;

import java.util.Map;

/**
 * @author Thomas Freese
 */
public final class MapUtils
{
    /**
     * @param map {@link Map}
     * @return boolean
     */
    public static boolean isEmpty(final Map<?, ?> map)
    {
        return (map == null) || map.isEmpty();
    }

    /**
     * @param map {@link Map}
     * @return boolean
     */
    public static boolean isNotEmpty(final Map<?, ?> map)
    {
        return !isEmpty(map);
    }

    /**
     * Erstellt ein neues {@link MapUtils} Object.
     */
    private MapUtils()
    {
        super();
    }
}
