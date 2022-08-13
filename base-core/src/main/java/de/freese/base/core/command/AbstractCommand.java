package de.freese.base.core.command;

import java.util.Objects;

/**
 * Basisklasse des Command-Patterns.
 *
 * @author Thomas Freese
 */
public abstract class AbstractCommand implements Command
{
    /**
     *
     */
    private final Object source;

    /**
     * Creates a new {@link AbstractCommand} object.
     *
     * @param source {@link Object}
     */
    protected AbstractCommand(final Object source)
    {
        super();

        this.source = Objects.requireNonNull(source, "source required");
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj == null)
        {
            return false;
        }

        if (!(obj instanceof AbstractCommand other))
        {
            return false;
        }

        if (getClass() != obj.getClass())
        {
            return false;
        }

        if (this.source == null)
        {
            return other.source == null;
        }

        return this.source.equals(other.source);
    }

    /**
     * @see de.freese.base.core.command.Command#getSource()
     */
    @Override
    public Object getSource()
    {
        return this.source;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + getClass().hashCode();
        result = (prime * result) + ((this.source == null) ? 0 : this.source.hashCode());

        return result;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getSource().toString();
    }

    // /**
    // * @param in java.io.ObjectInputStream
    // *
    // * @throws IOException Falls was schief geht.
    // * @throws ClassNotFoundException Falls was schief geht.
    // */
    // private void readObject(java.io.ObjectInputStream in)
    // throws IOException, ClassNotFoundException
    // {
    // in.defaultReadObject();
    // this.source = (Serializable) in.readObject();
    // }
    //
    // /**
    // * @param out java.io.ObjectOutputStream
    // *
    // * @throws IOException Falls was schief geht.
    // */
    // private void writeObject(java.io.ObjectOutputStream out)
    // throws IOException
    // {
    // out.defaultWriteObject();
    //
    // out.writeObject(_source);
    // }
}
