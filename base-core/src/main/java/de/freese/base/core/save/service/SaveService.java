package de.freese.base.core.save.service;

import java.util.List;
import java.util.Map;
import de.freese.base.core.command.AbstractRemoteCommand;

/**
 * Interface eines Services der Speicher-Operationen ausfuehren kann.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface SaveService
{
    /**
     * Speichern von Datenaenderungen.
     * 
     * @param commands {@link List} aus {@link AbstractRemoteCommand}
     * @return {@link Map}, Map fuer das Mapping der Temporaeren- zu den DB-PrimaryKeys.
     * @throws Exception Falls was schief geht.
     */
    public Map<Long, Long> save(List<AbstractRemoteCommand> commands) throws Exception;
}
