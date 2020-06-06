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
    // /**
    // *
    // */
    // private final Set<Class<?>> supportedTypes = new HashSet<>();

    /**
     * Erstellt ein neues {@link AbstractResourceConverter} Object.
     */
    public AbstractResourceConverter()
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
     * @param n int
     * @param errorMsg String
     * @return {@link List}
     * @throws ResourceConverterException Falls was schief geht.
     */
    protected List<Double> parseDoubles(final String key, final int n, final String errorMsg) throws ResourceConverterException
    {
        String[] splits = key.split(",", n + 1);

        if (splits.length != n)
        {
            throw new ResourceConverterException(errorMsg, key);
        }

        List<Double> doubles = new ArrayList<>(n);

        for (String doubleString : splits)
        {
            try
            {
                doubles.add(Double.valueOf(doubleString));
            }
            catch (NumberFormatException ex)
            {
                throw new ResourceConverterException(errorMsg, key, ex);
            }
        }

        return doubles;
    }
}
