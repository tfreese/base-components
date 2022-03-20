package de.freese.base.core.command;

/**
 * Interface für ein Kommando des Command-Patterns.
 *
 * @author Thomas Freese
 */
public interface Command
{
    /**
     * Ausführen des Kommandos.
     *
     * @throws Exception Falls was schief geht.
     */
    void execute() throws Exception;

    /**
     * Quelle, Ursprung, Inhalt des Kommandos.
     *
     * @return {@link Object}
     */
    Object getSource();
}
