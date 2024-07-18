// Created: 09.01.2004
package de.freese.base.persistence.exception;

import java.io.Serial;

/**
 * @author Thomas Freese
 */
public class PersistenceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -4679691790131023241L;

    public PersistenceException() {
        super();
    }

    public PersistenceException(final String message) {
        super(message);
    }

    public PersistenceException(final Throwable cause) {
        super(cause);
    }

    public PersistenceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
