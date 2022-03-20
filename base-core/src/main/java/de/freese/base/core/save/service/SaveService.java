package de.freese.base.core.save.service;

import java.util.List;
import java.util.Map;

import de.freese.base.core.command.AbstractRemoteCommand;

/**
 * Interface eines Services der Speicher-Operationen ausführen kann.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface SaveService
{
    /**
     * Speichern von Datenänderungen.
     *
     * @param commands {@link List} aus {@link AbstractRemoteCommand}
     *
     * @return {@link Map}, Map für das Mapping der Temporären- zu den DB-PrimaryKeys.
     *
     * @throws Exception Falls was schief geht.
     */
    Map<Long, Long> save(List<AbstractRemoteCommand> commands) throws Exception;
}
