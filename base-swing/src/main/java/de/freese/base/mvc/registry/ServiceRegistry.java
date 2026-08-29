package de.freese.base.mvc.registry;

/**
 * @author Thomas Freese
 * @since 29.01.2023
 */
public interface ServiceRegistry {
    <T> T getService(Class<T> clazz);

    <T> void registerService(Class<T> clazz, T service);
}
