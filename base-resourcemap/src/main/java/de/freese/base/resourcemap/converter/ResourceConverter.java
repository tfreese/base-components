package de.freese.base.resourcemap.converter;

import java.util.function.BiFunction;

/**
 * Converts a String to the required Object.
 *
 * @author Thomas Freese
 *
 * @param <T> Type
 */
@FunctionalInterface
public interface ResourceConverter<T> extends BiFunction<String, String, T>
{
    /**
     * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
     */
    @Override
    default T apply(final String t, final String u)
    {
        return convert(t, u);
    }

    /**
     * @param key String
     * @param value String
     *
     * @return @return Object
     */
    T convert(String key, String value);
}
