package de.freese.base.core.command;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public abstract class AbstractCommand implements Command
{
    private final Object source;

    protected AbstractCommand(final Object source)
    {
        super();

        this.source = Objects.requireNonNull(source, "source required");
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (!(o instanceof final AbstractCommand that))
        {
            return false;
        }

        return Objects.equals(getSource(), that.getSource());
    }

    /**
     * @see de.freese.base.core.command.Command#getSource()
     */
    @Override
    public Object getSource()
    {
        return this.source;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getSource());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getSource().toString();
    }

    // private void readObject(java.io.ObjectInputStream in)
    // throws IOException, ClassNotFoundException
    // {
    // in.defaultReadObject();
    // this.source = (Serializable) in.readObject();
    // }
    //
    // private void writeObject(java.io.ObjectOutputStream out)
    // throws IOException
    // {
    // out.defaultWriteObject();
    //
    // out.writeObject(_source);
    // }
}
