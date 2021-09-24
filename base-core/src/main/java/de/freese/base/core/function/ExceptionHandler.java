// Created: 03.01.2018
package de.freese.base.core.function;

/**
 * Exception-Handler.<br>
 * Exceptions können nicht generisch in einem catch()- Block abgefragt werden !<br>
 * catch (E ex) {...}
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ExceptionHandler// <E extends Exception>
{
    /**
     * Behandlung der Exception.
     *
     * @param ex {@link Exception}
     */
    void handle(Exception ex);
}
