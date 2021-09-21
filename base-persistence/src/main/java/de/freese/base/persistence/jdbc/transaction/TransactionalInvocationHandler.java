// Created: 11.01.2017
package de.freese.base.persistence.jdbc.transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.transaction.annotation.Transactional;

/**
 * Steuert eine Transaktion auf Methoden-Ebene wenn diese mit {@link Transactional} annotiert ist.<br>
 * Die {@link Connection} wird dabei im {@link ConnectionHolder} abgelegt, wenn diese noch nicht vorhanden ist.<br>
 *
 * <pre>
 * return (Service) Proxy.newProxyInstance(Service.class.getClassLoader(), new Class<?>[]
 * {
 *         Service.class
 * }, new TransactionalInvocationHandler(dataSource, serviceBean));
 * </pre>
 *
 * @author Thomas Freese
 */
public class TransactionalInvocationHandler implements InvocationHandler
{
    /**
     *
     */
    private final Object bean;
    /**
    *
    */
    private final DataSource dataSource;

    /**
     * Erzeugt eine neue Instanz von {@link TransactionalInvocationHandler}
     *
     * @param dataSource {@link DataSource}
     * @param bean Object
     */
    public TransactionalInvocationHandler(final DataSource dataSource, final Object bean)
    {
        super();

        this.dataSource = Objects.requireNonNull(dataSource, "dataSource required");
        this.bean = Objects.requireNonNull(bean, "bean required");
    }

    /**
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
    {
        switch (method.getName())
        {
            case "equals":
                return (proxy == args[0]);
            case "hashCode":
                return System.identityHashCode(proxy);
            default:
                break;
        }

        Method beanMethod = this.bean.getClass().getMethod(method.getName(), method.getParameterTypes());

        if (beanMethod == null)
        {
            throw new RuntimeException("no bean method found: " + method.getName() + " with " + Arrays.toString(method.getParameterTypes()));
        }

        // Transactional transactional = beanMethod.getAnnotation(Transactional.class);
        boolean isTransactional = beanMethod.isAnnotationPresent(Transactional.class);

        if (isTransactional)
        {
            if (ConnectionHolder.isEmpty())
            {
                ConnectionHolder.set(this.dataSource.getConnection());
            }

            ConnectionHolder.beginTX();
        }

        Object result = null;

        try
        {
            result = method.invoke(this.bean, args);

            if (isTransactional)
            {
                ConnectionHolder.commitTX();
            }
        }
        catch (InvocationTargetException ex)
        {
            if (isTransactional)
            {
                ConnectionHolder.rollbackTX();
            }

            throw ex.getTargetException();
        }
        finally
        {
            // Nested-Aufrufe werden nicht unterstützt (Hierachische Transactionen) !
            if (isTransactional)
            {
                ConnectionHolder.close();
            }

            // Remove geht immer, auch wenn nichts drin ist.
            ConnectionHolder.remove();
        }

        return result;
    }
}
