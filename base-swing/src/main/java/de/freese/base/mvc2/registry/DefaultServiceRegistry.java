// Created: 29.01.23
package de.freese.base.mvc2.registry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class DefaultServiceRegistry implements ServiceRegistry
{
    private static final class DefaultServiceRegistryHolder
    {
        private static final DefaultServiceRegistry INSTANCE = new DefaultServiceRegistry();

        private DefaultServiceRegistryHolder()
        {
            super();
        }
    }

    public static DefaultServiceRegistry getInstance()
    {
        return DefaultServiceRegistryHolder.INSTANCE;
    }

    private final Map<Class<?>, Object> registry = new HashMap<>();

    @Override
    public <T> T getService(final Class<T> clazz)
    {
        return clazz.cast(registry.get(clazz));
    }

    @Override
    public <T> void registerService(final Class<T> clazz, final T service)
    {
        registry.put(clazz, service);
    }
}
