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
     *
     * @param object {@link Serializable}
     * @param context {@link SaveContext}
     *
     * @throws Exception Falls was schief geht.
     */
    void insert(Serializable object, SaveContext context) throws Exception;

    /**
     * Führt ein Insert pro vorhandenen Objekt aus
     *
     * @param toInsert {@link Collection}
     * @param context {@link SaveContext}
     *
     * @throws Exception Falls was schief geht.
     */
    void insertAll(Collection<? extends Serializable> toInsert, SaveContext context) throws Exception;
}
