// Created: 23.07.2011
package de.freese.base.swing.exception;

import java.io.Serial;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class ReleaseVetoException extends Exception {
    @Serial
    private static final long serialVersionUID = -6827296101261000027L;

    private final transient Object source;

    public ReleaseVetoException(final Object source) {
        super();

        this.source = Objects.requireNonNull(source, "source required");
    }

    public ReleaseVetoException(final Object source, final String message) {
        super(message);

        this.source = Objects.requireNonNull(source, "source required");
    }

    public ReleaseVetoException(final Object source, final String message, final Throwable cause) {
        super(message, cause);

        this.source = Objects.requireNonNull(source, "source required");
    }

    public ReleaseVetoException(final Object source, final Throwable cause) {
        super(cause);

        this.source = Objects.requireNonNull(source, "source required");
    }

    public Object getSource() {
        return source;
    }
}
