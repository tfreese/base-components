/**
 * Created: 03.01.2018
 */

package de.freese.base.core.function;

/**
 * Exception-Handler.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ExceptionHandler
{
    /**
     * Behandlung der Exception.
     *
     * @param ex {@link Exception}
     */
    public void handle(Exception ex);
}
