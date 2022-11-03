package de.freese.base.core.save.service;

import java.io.Serializable;
import java.util.Collection;

import de.freese.base.core.save.SaveContext;

/**
 * Interface eines Services der Inserts ausführen kann.
 *
 * @author Thomas Freese
 */
public interface InsertService
{
    /**
     * Führt ein Insert auf einem Objekt aus.
     */
    void insert(Serializable object, SaveContext context) throws Exception;

    /**
     * Führt ein Insert pro vorhandenen Objekt aus
     */
    void insertAll(Collection<? extends Serializable> toInsert, SaveContext context) throws Exception;
}
