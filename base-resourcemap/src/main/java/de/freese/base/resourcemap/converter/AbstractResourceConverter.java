package de.freese.base.resourcemap.converter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Thomas Freese
 */
public abstract class AbstractResourceConverter<T> implements ResourceConverter<T> {
    protected AbstractResourceConverter() {
        super();

        // ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
        //
        // addType((Class<?>) parameterizedType.getActualTypeArguments()[0]);
    }

    protected URL getUrl(final String value) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URL url = cl.getResource(value);

        if (url == null) {
            cl = getClass().getClassLoader();
            url = cl.getResource(value);
        }

        if (url == null) {
            cl = ClassLoader.getSystemClassLoader();
            url = cl.getResource(value);
        }

        return url;
    }

    /**
     * String key is assumed to contain n number substrings separated by commas.<br>
     * Return a list of those integers or null if there are too many, too few, or if a substring can't be parsed.<br>
     * The format of the numbers is specified by Double.valueOf().
     */
    protected List<Double> parseDoubles(final String key, final String value, final int n, final String message) throws RuntimeException {
        final String[] splits = value.split(",|;|-|\\s+", n);

        if (splits.length != n) {
            throwException(key, value, message);
        }

        final List<Double> doubles = new ArrayList<>(n);

        for (String doubleString : splits) {
            doubles.add(Double.parseDouble(doubleString));
        }

        return doubles;
    }

    protected void throwException(final String key, final String value, final String message) throws RuntimeException {
        final String msg = String.format("%s = %s: %s", key, value, message);

        throw new RuntimeException(msg);
    }

    protected void throwException(final String key, final String value, final Throwable cause) throws RuntimeException {
        final String msg = String.format("%s = %s", key, value);

        throw new RuntimeException(msg, cause);
    }
}
