// Created: 20.09.2016
package de.freese.base.pool.factory;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * {@link ObjectFactory} für funktionale Interfaces.<br>
 * Als Minimum wird die create-Function benötigt.<br>
 * Andere Methoden werden nur ausgeführt, wenn die entsprechenden Funktionen gesetzt wurden.<br>
 * Die {@link #validate(Object)}-Methode liefert als default true.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class DefaultObjectFactory<T> implements ObjectFactory<T>
{
    /**
    *
    */
    private Consumer<T> activator = null;

    /**
     *
     */
    private final Supplier<T> creator;

    /**
    *
    */
    private Consumer<T> destroyer = null;

    /**
    *
    */
    private Consumer<T> passivator = null;

    /**
    *
    */
    private Function<T, Boolean> validator = null;

    /**
     * Erzeugt eine neue Instanz von {@link DefaultObjectFactory}
     *
     * @param creator {@link Supplier}
     */
    public DefaultObjectFactory(final Supplier<T> creator)
    {
        super();

        this.creator = Objects.requireNonNull(creator, "creator required");
    }

    /**
     * @see de.freese.base.pool.factory.ObjectFactory#activate(java.lang.Object)
     */
    @Override
    public void activate(final T t)
    {
        if (this.activator == null)
        {
            return;
        }

        this.activator.accept(t);
    }

    /**
     * @see de.freese.base.pool.factory.ObjectFactory#create()
     */
    @Override
    public T create()
    {
        return this.creator.get();
    }

    /**
     * @see de.freese.base.pool.factory.ObjectFactory#destroy(java.lang.Object)
     */
    @Override
    public void destroy(final T t)
    {
        if (this.destroyer == null)
        {
            return;
        }

        this.destroyer.accept(t);
    }

    /**
     * @see de.freese.base.pool.factory.ObjectFactory#passivate(java.lang.Object)
     */
    @Override
    public void passivate(final T t)
    {
        if (this.passivator == null)
        {
            return;
        }

        this.passivator.accept(t);
    }

    /**
     * @param activator {@link Consumer}
     */
    public void setActivator(final Consumer<T> activator)
    {
        this.activator = activator;
    }

    /**
     * @param destroyer {@link Consumer}
     */
    public void setDestroyer(final Consumer<T> destroyer)
    {
        this.destroyer = destroyer;
    }

    /**
     * @param passivator {@link Consumer}
     */
    public void setPassivator(final Consumer<T> passivator)
    {
        this.passivator = passivator;
    }

    /**
     * @param validator {@link Function}
     */
    public void setValidator(final Function<T, Boolean> validator)
    {
        this.validator = validator;
    }

    /**
     * @see de.freese.base.pool.factory.ObjectFactory#validate(java.lang.Object)
     */
    @Override
    public boolean validate(final T t)
    {
        if (this.validator == null)
        {
            return true;
        }

        return this.validator.apply(t);
    }
}
