package de.freese.base.core.model.wrapper;

import java.util.Objects;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.core.model.IdentifierProvider;
import de.freese.base.core.model.NameProvider;

/**
 * Generisches WrapperObjekt fuer ein Objekt.
 *
 * @author Thomas Freese
 */
public class GenericObjectWrapper implements ObjectWrapper
{
    /**
     *
     */
    private final IdentifierProvider identifierProvider;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private final NameProvider nameProvider;

    /**
     *
     */
    private final Object object;

    /**
     * Erstellt ein neues {@link GenericObjectWrapper} Object.
     *
     * @param identifierProvider {@link IdentifierProvider}
     * @param functionName {@link Function}
     */
    public GenericObjectWrapper(final IdentifierProvider identifierProvider, final Function<Object, String> functionName)
    {
        this(identifierProvider, new GenericNameProvider(identifierProvider, functionName), identifierProvider);
    }

    /**
     * Erstellt ein neues {@link GenericObjectWrapper} Object.
     *
     * @param nameProvider {@link NameProvider}
     * @param functionOID {@link Function}
     */
    public GenericObjectWrapper(final NameProvider nameProvider, final Function<Object, Long> functionOID)
    {
        this(nameProvider, nameProvider, new GenericIdentifierProvider(nameProvider, functionOID));
    }

    /**
     * Erstellt ein neues {@link GenericObjectWrapper} Object.<br>
     * Das Object muss vom Typ {@link NameProvider} und {@link IdentifierProvider} sein.
     *
     * @param object Object
     */
    public GenericObjectWrapper(final Object object)
    {
        this(object, (NameProvider) object, (IdentifierProvider) object);
    }

    /**
     * Erstellt ein neues {@link GenericObjectWrapper} Object.
     *
     * @param object Object
     * @param functionName {@link Function}
     * @param functionOID {@link Function}
     */
    public GenericObjectWrapper(final Object object, final Function<Object, String> functionName, final Function<Object, Long> functionOID)
    {
        this(object, new GenericNameProvider(object, functionName), new GenericIdentifierProvider(object, functionOID));
    }

    /**
     * Erstellt ein neues {@link GenericObjectWrapper} Object.
     *
     * @param object Object
     * @param nameProvider {@link NameProvider}
     * @param identifierProvider {@link IdentifierProvider}
     */
    GenericObjectWrapper(final Object object, final NameProvider nameProvider, final IdentifierProvider identifierProvider)
    {
        super();

        this.object = Objects.requireNonNull(object, "object required");
        this.nameProvider = Objects.requireNonNull(nameProvider, "nameProvider required");
        this.identifierProvider = Objects.requireNonNull(identifierProvider, "identifierProvider required");
    }

    /**
     * @see de.freese.base.core.model.NameProvider#getName()
     */
    @Override
    public String getName()
    {
        return getNameProvider().getName();
    }

    /**
     * @see de.freese.base.core.model.wrapper.ObjectWrapper#getObject()
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObject()
    {
        return (T) this.object;
    }

    /**
     * @see de.freese.base.core.model.wrapper.ObjectWrapper#getObjectClass()
     */
    @Override
    public Class<?> getObjectClass()
    {
        Object object = getObject();

        return object.getClass();
    }

    /**
     * @see de.freese.base.core.model.IdentifierProvider#getOid()
     */
    @Override
    public Long getOid()
    {
        return getIdentifierProvider().getOid();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getObjectClass().getSimpleName() + "[" + getName() + "]";
    }

    /**
     * @return {@link IdentifierProvider}
     */
    protected IdentifierProvider getIdentifierProvider()
    {
        return this.identifierProvider;
    }

    /**
     * @return {@link Logger}
     */
    protected final Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @return {@link NameProvider}
     */
    protected NameProvider getNameProvider()
    {
        return this.nameProvider;
    }
}
