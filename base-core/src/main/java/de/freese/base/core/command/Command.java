package de.freese.base.core.command;

/**
 * @author Thomas Freese
 */
public interface Command
{
    void execute() throws Exception;

    Object getSource();
}
