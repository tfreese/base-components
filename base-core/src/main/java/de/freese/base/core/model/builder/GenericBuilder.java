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
public class GenericBuilder<T>
{
    /**
     * Beispiel: of(ArrayList::new)
     *
     * @param instantiator {@link Supplier}
     *
     * @return {@link GenericBuilder}
     */
    public static <T> GenericBuilder<T> of(final Supplier<T> instantiator)
    {
        return new GenericBuilder<>(instantiator);
    }

    /**
     *
     */
    private final List<Consumer<T>> instanceModifiers = new ArrayList<>();
    /**
     *
     */
    private final Supplier<T> instantiator;

    /**
     * Erzeugt eine neue Instanz von {@link GenericBuilder}.<br>
     * Beispiel: ArrayList::new
     *
     * @param instantiator {@link Supplier}
     */
    public GenericBuilder(final Supplier<T> instantiator)
    {
        super();

        this.instantiator = Objects.requireNonNull(instantiator, "instantiator required");
    }

    /**
     * Baut das Objekt.
     *
     * @return Object
     */
    public T build()
    {
        T value = getInstantiator().get();

        getInstanceModifiers().forEach(modifier -> modifier.accept(value));
        getInstanceModifiers().clear();

        return value;
    }

    /**
     * Erzeugt das Objekt n-mal.
     *
     * @param n int
     *
     * @return {@link List}
     */
    public List<T> build(final int n)
    {
        List<T> list = new ArrayList<>(n);

        for (int i = 0; i < n; i++)
        {
            T value = getInstantiator().get();
            getInstanceModifiers().forEach(modifier -> modifier.accept(value));

            list.add(value);
        }

        getInstanceModifiers().clear();

        return list;
    }

    /**
     * Beispiel: with(ArrayList::add, "Sample object")
     *
     * @param setter {@link BiConsumer}
     * @param value Object
     *
     * @return {@link GenericBuilder}
     */
    public <U> GenericBuilder<T> with(final BiConsumer<T, U> setter, final U value)
    {
        Consumer<T> c = instance -> setter.accept(instance, value);

        return with(c);
    }

    /**
     * Beispiel: with(list -> list.add("Sample object"))
     *
     * @param setter {@link Consumer}
     *
     * @return {@link GenericBuilder}
     */
    public GenericBuilder<T> with(final Consumer<T> setter)
    {
        getInstanceModifiers().add(setter);

        return this;
    }

    /**
     * @return {@link List}
     */
    protected List<Consumer<T>> getInstanceModifiers()
    {
        return this.instanceModifiers;
    }

    /**
     * @return {@link Supplier}
     */
    protected Supplier<T> getInstantiator()
    {
        return this.instantiator;
    }
}
