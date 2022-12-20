package de.freese.base.core.command;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Thomas Freese
 */
public abstract class AbstractRemoteCommand extends AbstractCommand implements Serializable
{
    @Serial
    private static final long serialVersionUID = -4744210429650586724L;

    private transient Object commandInvoker;

    private transient Object payload;

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

    public void setCommandInvoker(final Object commandInvoker)
    {
        this.commandInvoker = commandInvoker;
    }

    public void setPayload(final Object payload)
    {
        this.payload = payload;
    }

    protected Object getCommandInvoker()
    {
        return this.commandInvoker;
    }

    protected <T> T getPayload()
    {
        return (T) this.payload;
    }
}
