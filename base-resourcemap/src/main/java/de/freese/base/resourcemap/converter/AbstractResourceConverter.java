package de.freese.base.resourcemap.converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Basisklasse eines {@link ResourceConverter}s.
 *
 * @author Thomas Freese
 * @param <T> Konkreter konvertierter Typ
 */
public abstract class AbstractResourceConverter<T> implements ResourceConverter<T>
{
    /**
     * Erstellt ein neues {@link AbstractResourceConverter} Object.
     */
    protected AbstractResourceConverter()
    {
        super();

        // ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        //
        // addType((Class<?>) parameterizedType.getActualTypeArguments()[0]);
    }

    /**
     * String key is assumed to contain n number substrings separated by commas. Return a list of those integers or null if there are too many, too few, or if a
     * substring can't be parsed. The format of the numbers is specified by Double.valueOf().
     *
     * @param key String
     * @param value String
     * @param n int
     * @param message String
     * @return {@link List}
     * @throws RuntimeException Falls was schief geht.
     */
    protected List<Double> parseDoubles(final String key, final String value, final int n, final String message) throws RuntimeException
    {
        String[] splits = value.split(",", n + 1);

        if (splits.length != n)
        {
            throwException(key, value, message);
        }

        List<Double> doubles = new ArrayList<>(n);

        for (String doubleString : splits)
        {
            doubles.add(Double.valueOf(doubleString));
        }

        return doubles;
    }

    /**
     * @param key String
     * @param value String
     * @param message String
     * @throws RuntimeException Falls was schief geht.
     */
    protected void throwException(final String key, final String value, final String message) throws RuntimeException
    {
        String msg = String.format("%s = %s: %s", key, value, message);

        throw new RuntimeException(msg);
    }

    /**
     * @param key String
     * @param value String
     * @param cause {@link Throwable}
     * @throws RuntimeException Falls was schief geht.
     */
    protected void throwException(final String key, final String value, final Throwable cause) throws RuntimeException
    {
        String msg = String.format("%s = %s", key, value);

        throw new RuntimeException(msg, cause);
    }
}
