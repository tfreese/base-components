// Created: 30.08.23
package de.freese.base.persistence.exception;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class UncheckedSqlException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -543437132026784055L;

    public UncheckedSqlException(final String message, final SQLException cause) {
        super(message, Objects.requireNonNull(cause));
    }

    public UncheckedSqlException(final String message) {
        super(message);
    }

    public UncheckedSqlException(final SQLException cause) {
        super(Objects.requireNonNull(cause));
    }

    @Override
    public SQLException getCause() {
        return (SQLException) super.getCause();
    }

    @Serial
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();

        Throwable cause = super.getCause();

        if (!(cause instanceof SQLException)) {
            throw new InvalidObjectException("Cause must be an SQLException");
        }
    }
}
