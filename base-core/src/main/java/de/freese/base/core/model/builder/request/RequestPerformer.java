// Created: 23.02.2017
package de.freese.base.core.model.builder.request;

/**
 * Führt den {@link RequestBuilder} aus.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface RequestPerformer
{
    /**
     * @param builder {@link RequestBuilder}
     *
     * @throws Exception Falls was schief geht.
     */
    void perform(RequestBuilder builder) throws Exception;
}
