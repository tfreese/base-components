// Created 10.07.2008
package de.freese.base.core.processor;

/**
 * Processors of the Interceptor/Filter Pattern.
 *
 * @author Thomas Freese
 */
public interface Processor<C> {

    void execute(C context) throws Exception;

    boolean isEnabled();

    void setEnabled(boolean enabled);
}
