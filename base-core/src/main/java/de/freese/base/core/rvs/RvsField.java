package de.freese.base.core.rvs;

import java.util.Objects;
import java.util.function.Function;

/**
 * @author Thomas Freese
 */
public final class RvsField {
    private final Function<String, Object> converter;
    private final int endExclusive;
    private final String name;
    private final int startInclusive;

    public RvsField(final String name, final int startInclusive, final int endExclusive) {
        this(name, startInclusive, endExclusive, value -> value);
    }

    public RvsField(final String name, final int startInclusive, final int endExclusive, final Function<String, Object> converter) {
        super();

        if (startInclusive < 0) {
            throw new IllegalArgumentException("startInclusive < 0");
        }

        if (endExclusive < 0) {
            throw new IllegalArgumentException("endExclusive < 0");
        }

        if (startInclusive > endExclusive) {
            throw new IllegalArgumentException("startInclusive > endExclusive");
        }

        this.name = Objects.requireNonNull(name, "name required");
        this.startInclusive = startInclusive;
        this.endExclusive = endExclusive;
        this.converter = Objects.requireNonNull(converter, "converter required");
    }

    public Function<String, Object> getConverter() {
        return converter;
    }

    public int getEndExclusive() {
        return endExclusive;
    }

    public String getName() {
        return name;
    }

    public int getStartInclusive() {
        return startInclusive;
    }
}
