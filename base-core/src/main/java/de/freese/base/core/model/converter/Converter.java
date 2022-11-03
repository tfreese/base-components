package de.freese.base.core.model.converter;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Thomas Freese
 */
public interface Converter<SOURCE, TARGET>
{
    TARGET convertFromSource(final SOURCE source);

    SOURCE convertFromTarget(final TARGET target);

    default List<SOURCE> convertFromTarget(final Iterable<TARGET> target)
    {
        return StreamSupport.stream(target.spliterator(), false).map(this::convertFromTarget).toList();
    }

    default List<TARGET> createFromSource(final Iterable<SOURCE> source)
    {
        return StreamSupport.stream(source.spliterator(), false).map(this::convertFromSource).toList();
    }
}
