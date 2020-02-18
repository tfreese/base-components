package de.freese.base.core.model.wrapper;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.core.model.IdentifierProvider;

/**
 * Generisches WrapperObjekt fuer ein Objekt mit einer OID.
 *
 * @author Thomas Freese
 */
public class GenericIdentifierProvider implements Serializable, IdentifierProvider
{
    /**
     *
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(GenericIdentifierProvider.class);

    /**
     *
     */
    private static final long serialVersionUID = 7188426445497671100L;

    /**
     *
     */
    private final Function<Object, Long> function;

    /**
     *
     */
    private final Object object;

    /**
     * Erstellt ein neues {@link GenericIdentifierProvider} Objekt.
     *
     * @param object Object
     * @param function {@link Function}
     */
    public GenericIdentifierProvider(final Object object, final Function<Object, Long> function)
    {
        super();

        this.object = Objects.requireNonNull(object, "object required");
        this.function = Objects.requireNonNull(function, "function required");
    }

    /**
     * Erstellt ein neues {@link GenericIdentifierProvider} Objekt.
     *
     * @param object Object
     * @param methodName String
     */
    public GenericIdentifierProvider(final Object object, final String methodName)
    {
        this(object, obj ->
        {
            Object value = null;

            try
            {
                Method method = obj.getClass().getMethod(methodName, (Class[]) null);
                value = method.invoke(obj, (Object[]) null);
            }
            catch (Exception ex)
            {
                LOGGER.error(null, ex);
            }

            return value != null ? (Long) value : null;
        });
    }

    /**
     * @see de.freese.base.core.model.IdentifierProvider#getOid()
     */
    @Override
    public Long getOid()
    {
        return this.function.apply(this.object);
    }
}