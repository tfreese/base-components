/**
 * Created: 01.02.2018
 */
package de.freese.base.core.function.usecase;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Funktionale switch-case Alternative.<br>
 * Beispiel: <code>
 * <pre>
 * Optional&lt;String&gt; result = Switch.match(
 *      Case.matchDefault(() -&gt; "unknown"),
 *      Case.matchCase(() -&gt; i == 0, () -&gt; "Value = 0"),
 *      Case.matchCase(() -&gt; i == 1, () -&gt; "Value = 1"),
 *      Case.matchCase(() -&gt; i == 2, () -&gt; "Value = 2")
 *      );
 * </pre>
 * </code>
 *
 * @param <T> Typ des Rückgabe-Wertes
 *
 * @author Thomas Freese
 */
public final class Switch<T>
{
    /**
     * @param <T> Typ des Rückgabe-Wertes
     *
     * @author Thomas Freese
     * @see Switch
     */
    public static final class Case<T>
    {
        public static <T> Case<T> matchCase(final BooleanSupplier condition, final Supplier<T> value)
        {
            return new Case<>(condition, value);
        }

        public static <T> Case<T> matchDefault(final Supplier<T> value)
        {
            return new Case<>(() -> true, value);
        }

        private final BooleanSupplier condition;

        private final Supplier<T> value;

        private Case(final BooleanSupplier condition, final Supplier<T> value)
        {
            super();

            this.condition = Objects.requireNonNull(condition, "condition required");
            this.value = Objects.requireNonNull(value, "value required");
        }
    }

    /**
     * @param defaultCase {@link Case}; not null
     * @param matchers {@link Case}; not null
     */
    @SafeVarargs
    public static <T> Optional<T> match(final Case<T> defaultCase, final Case<T>... matchers)
    {
        Objects.requireNonNull(defaultCase, "defaultCase required");
        Objects.requireNonNull(matchers, "matchers required");

        //@formatter:off
        T result = Stream.of(matchers)
                .filter(c -> c.condition.getAsBoolean())
                .map(c -> c.value.get())
                .findFirst()
                .orElseGet(defaultCase.value);
        //@formatter:on

        return Optional.ofNullable(result);
    }

    private Switch()
    {
        super();
    }
}
