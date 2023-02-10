// Created: 23.02.2017
package de.freese.base.core.model.builder.request;

/**
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ResultHandler {
    void handle(RequestResult result) throws Exception;
}
