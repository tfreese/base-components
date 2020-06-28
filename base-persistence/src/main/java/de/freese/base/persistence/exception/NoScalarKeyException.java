package de.freese.base.persistence.exception;

import org.springframework.dao.DataAccessException;

/**
 * Wird geworfen, wenn ein skalarer PrimaryKey erwartet wird, aber ein Komposite-Key verwendet wird.
 *
 * @author Thomas Freese
 */
class NoScalarKeyException extends DataAccessException
{
    /**
     *
     */
    private static final long serialVersionUID = -8320833456066908985L;

    /**
     * Erstellt ein neues {@link NoScalarKeyException} Object.
     *
     * @param msg String
     */
    public NoScalarKeyException(final String msg)
    {
        super(msg);
    }
}
