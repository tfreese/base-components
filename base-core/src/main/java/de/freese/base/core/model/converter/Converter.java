// Created: 19.03.2020
package de.freese.base.core.model.converter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Thomas Freese
 *
 * @param <SOURCE> Typ der Quelle
 * @param <TARGET> Typ des Ziels
 */
public class Converter<SOURCE, TARGET>
{
    /**
    *
    */
    private final Function<SOURCE, TARGET> fromSource;
    /**
    *
    */
    private final Function<TARGET, SOURCE> fromTarget;

    /**
     * @param fromSource {@link Function}
     * @param fromTarget {@link Function}
     */
    public Converter(final Function<SOURCE, TARGET> fromSource, final Function<TARGET, SOURCE> fromTarget)
    {
        super();

        this.fromSource = Objects.requireNonNull(fromSource, "fromSource required");
        this.fromTarget = Objects.requireNonNull(fromTarget, "fromTarget required");
    }

    /**
     * @param source Object
     *
     * @return Object
     */
    public final TARGET convertFromSource(final SOURCE source)
    {
        return this.fromSource.apply(source);
    }

    /**
     * @param target Object
     *
     * @return {@link List}
     */
    public final List<SOURCE> convertFromTarget(final Collection<TARGET> target)
    {
        return target.stream().map(this::convertFromTarget).toList();
    }

    /**
     * @param target Object
     *
     * @return Object
     */
    public final SOURCE convertFromTarget(final TARGET target)
    {
        return this.fromTarget.apply(target);
    }

    /**
     * @param source Object
     *
     * @return {@link List}
     */
    public final List<TARGET> createFromSource(final Collection<SOURCE> source)
    {
        return source.stream().map(this::convertFromSource).toList();
    }
}
