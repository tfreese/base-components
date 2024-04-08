// Created: 24.01.2018
package de.freese.base.core.model.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Java Generic Builder Pattern.<br>
 * Beispiel:
 *
 * <pre>
 * <code>
 * List&lt;String&gt; list = GenericBuilder.of(ArrayList&lt;String&gt;::new)
 *      .with(l -&gt; l.add(&quot;A&quot;))
 *      .with(ArrayList::add, &quot;B&quot;)
 *      .build();
 * </code>
 * </pre>
 *
 * @param <T> Typ des zu erzeugenden Objekts.
 *
 * @author Thomas Freese
 */
public class GenericBuilder<T> implements Builder<T> {
    /**
     * Beispiel: of(ArrayList::new)
     */
    public static <T> GenericBuilder<T> of(final Supplier<T> instantiator) {
        return new GenericBuilder<>(instantiator);
    }

    private final List<Consumer<T>> instanceModifiers = new ArrayList<>();
    private final Supplier<T> instantiator;

    /**
     * Erzeugt eine neue Instanz von {@link GenericBuilder}.<br>
     * Beispiel: ArrayList::new
     */
    public GenericBuilder(final Supplier<T> instantiator) {
        super();

        this.instantiator = Objects.requireNonNull(instantiator, "instantiator required");
    }

    @Override
    public T build() {
        final T value = getInstantiator().get();

        getInstanceModifiers().forEach(modifier -> modifier.accept(value));
        getInstanceModifiers().clear();

        return value;
    }

    /**
     * Erzeugt das Objekt n-mal.
     */
    public List<T> build(final int n) {
        final List<T> list = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            final T value = getInstantiator().get();
            getInstanceModifiers().forEach(modifier -> modifier.accept(value));

            list.add(value);
        }

        getInstanceModifiers().clear();

        return list;
    }

    /**
     * Beispiel: with(ArrayList::add, "Sample object")
     */
    public <U> GenericBuilder<T> with(final BiConsumer<T, U> setter, final U value) {
        final Consumer<T> c = instance -> setter.accept(instance, value);

        return with(c);
    }

    /**
     * Beispiel: with(list -> list.add("Sample object"))
     */
    public GenericBuilder<T> with(final Consumer<T> setter) {
        getInstanceModifiers().add(setter);

        return this;
    }

    protected List<Consumer<T>> getInstanceModifiers() {
        return this.instanceModifiers;
    }

    protected Supplier<T> getInstantiator() {
        return this.instantiator;
    }
}
