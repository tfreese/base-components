// Created 10.07.2008
package de.freese.base.core.processor;

/**
 * Interface eines Processors des Interceptor/Filter Patterns.
 *
 * @param <C> Typ des Kontextobjekts
 *
 * @author Thomas Freese
 */
public interface Processor<C>
{
    /**
     * Ausführung von Anwendungslogik.
     *
     * @param context Object
     *
     * @throws Exception Falls was schief geht.
     */
    void execute(C context) throws Exception;

    /**
     * Liefert true, wenn der Processor aktiviert ist, und von der ProcessorChain aufgerufen wird.
     *
     * @return boolean
     */
    boolean isEnabled();

    /**
     * True, wenn der Processor von der ProcessorChain aufgerufen werden soll.
     *
     * @param enabled boolean
     */
    void setEnabled(boolean enabled);
}
