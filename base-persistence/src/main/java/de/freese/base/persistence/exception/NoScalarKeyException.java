package de.freese.base.persistence.exception;

import java.io.Serial;

import org.springframework.dao.DataAccessException;

/**
 * Wird geworfen, wenn ein skalarer PrimaryKey erwartet wird, aber ein Komposite-Key verwendet wird.
 *
 * @author Thomas Freese
 */
class NoScalarKeyException extends DataAccessException {
    @Serial
    private static final long serialVersionUID = -8320833456066908985L;

    NoScalarKeyException(final String msg) {
        super(msg);
    }
}
