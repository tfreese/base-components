/**
 * Created 10.07.2008
 */
package de.freese.base.core.processor;

/**
 * Interface eines Processors des Interceptor/Filter Patterns.
 *
 * @author Thomas Freese
 * @param <C> Typ des Kontextobjekts
 */
public interface Processor<C>
{
    /**
     * Ausfuehrung von Anwendungslogik.
     * 
     * @param context Object
     * @throws Exception Falls was schief geht.
     */
    public void execute(C context) throws Exception;

    /**
     * Liefert true, wenn der Processor aktiviert ist, und von der ProcessorChain aufgerufen wird.
     * 
     * @return boolean
     */
    public boolean isEnabled();

    /**
     * True, wenn der Processor von der ProcessorChain aufgerufen werden soll.
     * 
     * @param enabled boolean
     */
    public void setEnabled(boolean enabled);
}
