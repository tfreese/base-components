// Created: 23.02.2017
package de.freese.base.core.model.builder.request;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ResultMatcher
{
    /**
     * @param result {@link RequestResult}
     *
     * @throws Exception Falls was schiefgeht.
     */
    void match(RequestResult result) throws Exception;
}
