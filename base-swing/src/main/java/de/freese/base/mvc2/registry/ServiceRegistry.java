// Created: 29.01.23
package de.freese.base.mvc2.registry;

/**
 * @author Thomas Freese
 */
public interface ServiceRegistry
{
    <T> T getService(Class<T> clazz);

    <T> void registerService(Class<T> clazz, T service);
}
