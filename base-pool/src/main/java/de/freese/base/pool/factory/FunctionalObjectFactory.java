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
public class FunctionalObjectFactory<T> implements ObjectFactory<T>
{
    /**
    *
    */
    private Consumer<T> activateConsumer = null;

    /**
     *
     */
    private final Supplier<T> createSupplier;

    /**
    *
    */
    private Consumer<T> destroyConsumer = null;

    /**
    *
    */
    private Consumer<T> passivateConsumer = null;

    /**
    *
    */
    private Function<T, Boolean> validateFunction = null;

    /**
     * Erzeugt eine neue Instanz von {@link FunctionalObjectFactory}
     *
     * @param createFunction {@link Supplier}
     */
    public FunctionalObjectFactory(final Supplier<T> createFunction)
    {
        super();

        this.createSupplier = Objects.requireNonNull(createFunction, "createFunction required");
    }

    /**
     * @see de.freese.base.pool.factory.ObjectFactory#activate(java.lang.Object)
     */
    @Override
    public void activate(final T t)
    {
        if (this.activateConsumer == null)
        {
            return;
        }

        this.activateConsumer.accept(t);
    }

    /**
     * @see de.freese.base.pool.factory.ObjectFactory#create()
     */
    @Override
    public T create()
    {
        return this.createSupplier.get();
    }

    /**
     * @see de.freese.base.pool.factory.ObjectFactory#destroy(java.lang.Object)
     */
    @Override
    public void destroy(final T t)
    {
        if (this.destroyConsumer == null)
        {
            return;
        }

        this.destroyConsumer.accept(t);
    }

    /**
     * @see de.freese.base.pool.factory.ObjectFactory#passivate(java.lang.Object)
     */
    @Override
    public void passivate(final T t)
    {
        if (this.passivateConsumer == null)
        {
            return;
        }

        this.passivateConsumer.accept(t);
    }

    /**
     * @param activateConsumer {@link Consumer}
     */
    public void setActivateConsumer(final Consumer<T> activateConsumer)
    {
        this.activateConsumer = activateConsumer;
        ;
    }

    /**
     * @param destroyConsumer {@link Consumer}
     */
    public void setDestroyConsumer(final Consumer<T> destroyConsumer)
    {
        this.destroyConsumer = destroyConsumer;
    }

    /**
     * @param passivateConsumer {@link Consumer}
     */
    public void setPassivateConsumer(final Consumer<T> passivateConsumer)
    {
        this.passivateConsumer = passivateConsumer;
    }

    /**
     * @param validateFunction {@link Function}
     */
    public void setValidateFunction(final Function<T, Boolean> validateFunction)
    {
        this.validateFunction = validateFunction;
    }

    /**
     * @see de.freese.base.pool.factory.ObjectFactory#validate(java.lang.Object)
     */
    @Override
    public boolean validate(final T t)
    {
        if (this.validateFunction == null)
        {
            return true;
        }

        return this.validateFunction.apply(t);
    }
}
