// Created: 26.01.2018
package de.freese.base.persistence.jdbc.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Objects;

/**
 * {@link InvocationHandler} der die {@link Connection#close()}-Methode nicht ausführt.
 *
 * @author Thomas Freese
 */
public class ConnectionNotClosingInvocationHandler implements InvocationHandler {
    private final Connection target;

    public ConnectionNotClosingInvocationHandler(final Connection target) {
        super();

        this.target = Objects.requireNonNull(target, "target required");
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        switch (method.getName()) {
            case "equals":
                return (proxy == args[0]);
            case "hashCode":
                return System.identityHashCode(proxy);
            case "unwrap":
                if (((Class<?>) args[0]).isInstance(proxy)) {
                    return proxy;
                }
                break;
            case "isWrapperFor":
                if (((Class<?>) args[0]).isInstance(proxy)) {
                    return true;
                }
                break;
            case "close":
                return null;
            case "isClosed":
                return false;
            case "getTargetConnection":
                return this.target;
            default:
                break;
        }

        try {
            return method.invoke(this.target, args);
        }
        catch (final InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }
}
