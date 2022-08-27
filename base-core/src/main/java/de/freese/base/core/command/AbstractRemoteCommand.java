package de.freese.base.core.command;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Basisklasse des Command-Patterns für ein Remote-Kommando.
 *
 * @author Thomas Freese
 */
public abstract class AbstractRemoteCommand extends AbstractCommand implements Serializable
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -4744210429650586724L;
    /**
     *
     */
    private transient Object commandInvoker;
    /**
     *
     */
    private transient Object payload;

    /**
     * Erstellt ein neues {@link AbstractRemoteCommand} Object.
     *
     * @param source {@link Serializable}
     */
    protected AbstractRemoteCommand(final Serializable source)
    {
        super(source);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }
        
        if (!(o instanceof final AbstractRemoteCommand that))
        {
            return false;
        }

        if (!super.equals(o))
        {
            return false;
        }

        return Objects.equals(getCommandInvoker(), that.getCommandInvoker()) && Objects.equals(getPayload(), that.getPayload());
    }

    /**
     * @see de.freese.base.core.command.AbstractCommand#getSource()
     */
    @Override
    public Serializable getSource()
    {
        return (Serializable) super.getSource();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), getCommandInvoker(), getPayload());
    }

    /**
     * Setzt das Objekt für die Verarbeitung des Kommandos.
     *
     * @param commandInvoker Object
     */
    public void setCommandInvoker(final Object commandInvoker)
    {
        this.commandInvoker = commandInvoker;
    }

    /**
     * Setzt das Objekt, welches dem CommandInvoker übergeben werden kann.
     *
     * @param payload Object
     */
    public void setPayload(final Object payload)
    {
        this.payload = payload;
    }

    /**
     * Liefert das Objekt für die Verarbeitung des Kommandos.
     *
     * @return Object
     */
    protected Object getCommandInvoker()
    {
        return this.commandInvoker;
    }

    /**
     * Liefert das Objekt, welches dem CommandInvoker übergeben werden kann.
     *
     * @param <T> Konkreter Typ
     *
     * @return Object
     */
    protected <T> T getPayload()
    {
        return (T) this.payload;
    }
}
