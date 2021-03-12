package de.freese.base.core.command;

/**
 * Interface fuer ein Kommando des Command-Patterns.
 *
 * @author Thomas Freese
 */
public interface Command
{
    /**
     * Ausfuehren des Kommandos.
     * 
     * @throws Exception Falls was schief geht.
     */
    public void execute() throws Exception;

    /**
     * Quelle, Ursprung, Inhalt des Kommandos.
     * 
     * @return {@link Object}
     */
    public Object getSource();
}
