// Created: 19.03.2020
package de.freese.base.core.model.converter;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author Thomas Freese
 */
public abstract class AbstractConverter<SOURCE, TARGET> implements Converter<SOURCE, TARGET> {
    private final Function<SOURCE, TARGET> fromSource;

    private final Function<TARGET, SOURCE> fromTarget;

    protected AbstractConverter(final Function<SOURCE, TARGET> fromSource, final Function<TARGET, SOURCE> fromTarget) {
        super();

        this.fromSource = Objects.requireNonNull(fromSource, "fromSource required");
        this.fromTarget = Objects.requireNonNull(fromTarget, "fromTarget required");
    }

    @Override
    public TARGET convertFromSource(final SOURCE source) {
        return this.fromSource.apply(source);
    }

    @Override
    public SOURCE convertFromTarget(final TARGET target) {
        return this.fromTarget.apply(target);
    }

}
