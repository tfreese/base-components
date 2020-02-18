package de.freese.base.persistence.exception;

/**
 * Created on 09.01.2004
 *
 * @author Thomas Freese
 */
class PersistenceException extends Exception
{
    /**
     *
     */
    private static final long serialVersionUID = -4679691790131023241L;

    /**
     * Erstellt ein neues {@link PersistenceException} Object.
     */
    public PersistenceException()
    {
        super();
    }

    /**
     * Erstellt ein neues {@link PersistenceException} Object.
     * 
     * @param message String
     */
    public PersistenceException(final String message)
    {
        super(message);
    }

    /**
     * Erstellt ein neues {@link PersistenceException} Object.
     * 
     * @param message String
     * @param cause {@link Throwable}
     */
    public PersistenceException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Erstellt ein neues {@link PersistenceException} Object.
     * 
     * @param cause {@link Throwable}
     */
    public PersistenceException(final Throwable cause)
    {
        super(cause);
    }
}
