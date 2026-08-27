package de.freese.base.core.model.builder.request;

/**
 * @author Thomas Freese
 * @since 23.02.2017
 */
@FunctionalInterface
public interface ResultMatcher {
    void match(RequestResult<?> result) throws Exception;
}
