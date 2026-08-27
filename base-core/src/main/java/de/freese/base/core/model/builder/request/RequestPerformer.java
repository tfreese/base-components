package de.freese.base.core.model.builder.request;

/**
 * Führt den {@link RequestBuilder} aus.
 *
 * @author Thomas Freese
 * @since 23.02.2017
 */
@FunctionalInterface
public interface RequestPerformer {
    void perform(RequestBuilder<?> builder) throws Exception;
}
