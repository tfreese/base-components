// Created: 08.09.2016
package de.freese.base.persistence.jdbc.driver.logging;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.util.ClassUtils;

/**
 * @author Thomas Freese
 */
class LoggingJdbcInvocationHandler implements InvocationHandler {
    private static final Logger LOGGER = LoggingJdbcDriver.LOGGER;

    private final Set<String> logMethods;
    private final Object target;

    LoggingJdbcInvocationHandler(final Object target, final Set<String> logMethods) {
        super();

        this.target = Objects.requireNonNull(target, "target required");
        this.logMethods = Objects.requireNonNull(logMethods, "logMethods required");
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        try {
            final boolean logMethod = this.logMethods.contains(method.getName());

            // if (LOGGER.isDebugEnabled())
            if (logMethod) {
                LOGGER.debug("Invoke {}#{}: {}", this.target.getClass().getSimpleName(), method.getName(), args != null ? Arrays.asList(args) : "[]");
            }

            // long start = System.currentTimeMillis();
            final Object result = method.invoke(this.target, args);
            // long end = System.currentTimeMillis();
            //
            // if (LOGGER.isDebugEnabled())
            // {
            // LOGGER.debug(String.format("Result [%dms] %s#%s: %s", end - start, this.target.getClass().getSimpleName(), method.getName(), result));
            // }

            if (result == null) {
                return null;
            }

            if (method.getReturnType().isInterface()) {
                return Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(), new Class<?>[]{method.getReturnType()},
                        new LoggingJdbcInvocationHandler(result, this.logMethods));
            }

            return result;
        }
        catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }
}
