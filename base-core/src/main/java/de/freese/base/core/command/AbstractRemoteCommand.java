package de.freese.base.core.command;

import java.io.Serializable;

/**
 * Basisklasse des Command-Patterns fuer ein Remotekommando.
 *
 * @author Thomas Freese
 */
public abstract class AbstractRemoteCommand extends AbstractCommand implements Serializable
{
    /**
     * 
     */
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
    public AbstractRemoteCommand(final Serializable source)
    {
        super(source);
    }

    /**
     * Liefert das Objekt fuer die Verarbeitung des Kommandos.
     * 
     * @return Object
     */
    protected Object getCommandInvoker()
    {
        return this.commandInvoker;
    }

    /**
     * Liefert das Objekt, welches dem CommandInvoker uebergeben werden kann.
     * 
     * @param <T> Konkreter Typ
     * @return Object
     */
    @SuppressWarnings("unchecked")
    protected <T> T getPayload()
    {
        return (T) this.payload;
    }

    /**
     * @see de.freese.base.core.command.AbstractCommand#getSource()
     */
    @Override
    public Serializable getSource()
    {
        return (Serializable) super.getSource();
    }

    /**
     * Setzt das Objekt fuer die Verarbeitung des Kommandos.
     * 
     * @param commandInvoker Object
     */
    public void setCommandInvoker(final Object commandInvoker)
    {
        this.commandInvoker = commandInvoker;
    }

    /**
     * Setzt das Objekt, welches dem CommandInvoker uebergeben werden kann.
     * 
     * @param payload Object
     */
    public void setPayload(final Object payload)
    {
        this.payload = payload;
    }
}
