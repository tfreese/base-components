package de.freese.base.resourcemap.converter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @param <T> Type
 *
 * @author Thomas Freese
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
     * @param value String
     *
     * @return {@link URL}
     */
    protected URL getUrl(final String value)
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(value);

        if (url == null)
        {
            cl = getClass().getClassLoader();
            url = cl.getResource(value);
        }

        if (url == null)
        {
            cl = ClassLoader.getSystemClassLoader();
            url = cl.getResource(value);
        }

        return url;
    }

    /**
     * String key is assumed to contain n number substrings separated by commas.<br>
     * Return a list of those integers or null if there are too many, too few, or if a substring can't be parsed.<br>
     * The format of the numbers is specified by Double.valueOf().
     *
     * @param key String
     * @param value String
     * @param n int
     * @param message String
     *
     * @return {@link List}
     *
     * @throws RuntimeException Falls was schiefgeht.
     */
    protected List<Double> parseDoubles(final String key, final String value, final int n, final String message) throws RuntimeException
    {
        String[] splits = value.split(",|;|-|\\s+", n);

        if (splits.length != n)
        {
            throwException(key, value, message);
        }

        List<Double> doubles = new ArrayList<>(n);

        for (String doubleString : splits)
        {
            doubles.add(Double.parseDouble(doubleString));
        }

        return doubles;
    }

    /**
     * @param key String
     * @param value String
     * @param message String
     *
     * @throws RuntimeException Falls was schiefgeht.
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
     *
     * @throws RuntimeException Falls was schiefgeht.
     */
    protected void throwException(final String key, final String value, final Throwable cause) throws RuntimeException
    {
        String msg = String.format("%s = %s", key, value);

        throw new RuntimeException(msg, cause);
    }
}
