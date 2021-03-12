package de.freese.base.core.save.service;

import java.io.Serializable;
import java.util.Collection;
import de.freese.base.core.save.SaveContext;

/**
 * Interface eines Services der Inserts ausfuehren kann.
 *
 * @author Thomas Freese
 */
public interface InsertService
{
    /**
     * Fuehrt ein Insert auf einem Objekt aus.
     * 
     * @param object {@link Serializable}
     * @param context {@link SaveContext}
     * @throws Exception Falls was schief geht.
     */
    public void insert(Serializable object, SaveContext context) throws Exception;

    /**
     * Fuehrt ein Insert pro vorhandenen Objekt aus
     * 
     * @param toInsert {@link Collection}
     * @param context {@link SaveContext}
     * @throws Exception Falls was schief geht.
     */
    public void insertAll(Collection<? extends Serializable> toInsert, SaveContext context) throws Exception;
}
