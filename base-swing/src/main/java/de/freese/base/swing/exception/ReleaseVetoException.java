// Created: 23.07.2011
package de.freese.base.swing.exception;

import java.io.Serial;
import java.util.Objects;

/**
 * Exception für ein nicht durchführbaren Release-Vorgang.
 *
 * @author Thomas Freese
 */
public class ReleaseVetoException extends Exception
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -6827296101261000027L;
    /**
     *
     */
    private final transient Object source;

    /**
     * Erstellt ein neues {@link ReleaseVetoException} Object.
     *
     * @param source Object
     */
    public ReleaseVetoException(final Object source)
    {
        super();

        this.source = Objects.requireNonNull(source, "source required");
    }

    /**
     * Erstellt ein neues {@link ReleaseVetoException} Object.
     *
     * @param source Object
     * @param message String
     */
    public ReleaseVetoException(final Object source, final String message)
    {
        super(message);

        this.source = Objects.requireNonNull(source, "source required");
    }

    /**
     * Erstellt ein neues {@link ReleaseVetoException} Object.
     *
     * @param source Object
     * @param message String
     * @param cause {@link Throwable}
     */
    public ReleaseVetoException(final Object source, final String message, final Throwable cause)
    {
        super(message, cause);

        this.source = Objects.requireNonNull(source, "source required");
    }

    /**
     * Erstellt ein neues {@link ReleaseVetoException} Object.
     *
     * @param source Object
     * @param cause {@link Throwable}
     */
    public ReleaseVetoException(final Object source, final Throwable cause)
    {
        super(cause);

        this.source = Objects.requireNonNull(source, "source required");
    }

    /**
     * Liefert die Quelle der Exception.
     *
     * @return Object
     */
    public Object getSource()
    {
        return this.source;
    }
}
