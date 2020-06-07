package de.freese.base.resourcemap.converter;

import java.util.function.BiFunction;

/**
 * Konvertiert aus einen String das konkrete Object.
 *
 * @author Thomas Freese
 * @param <T> Konkreter konvertierter Typ
 */
@FunctionalInterface
public interface ResourceConverter<T> extends BiFunction<String, String, T>
{
    /**
     * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
     */
    @Override
    public default T apply(final String t, final String u)
    {
        return convert(t, u);
    }

    /**
     * @param key String
     * @param value String
     * @return @return Object
     */
    public T convert(String key, String value);
}
