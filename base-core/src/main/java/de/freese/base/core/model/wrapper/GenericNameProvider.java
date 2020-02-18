package de.freese.base.core.model.wrapper;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.core.model.NameProvider;

/**
 * Generisches WrapperObjekt fuer ein Objekt mit einem Namen.
 *
 * @author Thomas Freese
 */
public class GenericNameProvider implements Serializable, NameProvider
{
    /**
     * 
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(GenericNameProvider.class);

    /**
     * 
     */
    private static final long serialVersionUID = -1550739143913236326L;

    /**
    *
    */
    private final Function<Object, String> function;

    /**
     * 
     */
    private final Object object;

    /**
     * Erstellt ein neues {@link GenericNameProvider} Objekt.
     * 
     * @param object Object
     * @param function {@link Function}
     */
    public GenericNameProvider(final Object object, final Function<Object, String> function)
    {
        super();

        this.object = Objects.requireNonNull(object, "object required");
        this.function = Objects.requireNonNull(function, "function required");
    }

    /**
     * Erstellt ein neues {@link GenericNameProvider} Objekt.
     * 
     * @param object Object
     * @param methodName String
     */
    public GenericNameProvider(final Object object, final String methodName)
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

            return value != null ? value.toString() : "";
        });
    }

    /**
     * @see de.freese.base.core.model.NameProvider#getName()
     */
    @Override
    public String getName()
    {
        return this.function.apply(this.object);
    }
}