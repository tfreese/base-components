package de.freese.base.resourcemap.converter;

import java.util.function.BiFunction;

/**
 * Converts a String to the required Object.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ResourceConverter<T> extends BiFunction<String, String, T> {
    @Override
    default T apply(final String t, final String u) {
        return convert(t, u);
    }

    T convert(String key, String value);
}
