package de.freese.base.core.processor;

/**
 * Processors of the Interceptor/Filter Pattern.
 *
 * @author Thomas Freese
 * @since 10.07.2008
 */
public interface Processor<C> {

    void execute(C context) throws Exception;

    boolean isEnabled();

    void setEnabled(boolean enabled);
}
