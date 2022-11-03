package de.freese.base.core.command;

/**
 * Interface für ein Kommando des Command-Patterns.
 *
 * @author Thomas Freese
 */
public interface Command
{
    void execute() throws Exception;

    Object getSource();
}
