package de.freese.base.core.model.tupel;

import java.util.Objects;

/**
 * Ein 3er-Tupel verknuepft 3 Objekte miteinander.
 *
 * @author Thomas Freese
 *
 * @param <A> Konkreter Typ ValueA
 * @param <B> Konkreter Typ ValueB
 * @param <C> Konkreter Typ ValueC
 */
public class Tupel3<A, B, C> extends Tupel2<A, B>
{
    /**
     *
     */
    private static final long serialVersionUID = -3964125548346645907L;

    /**
     *
     */
    private C valueC;

    /**
     * Erstellt ein neues {@link Tupel3} Object.
     */
    public Tupel3()
    {
        super();
    }

    /**
     * Erstellt ein neues {@link Tupel3} Object.
     *
     * @param valueA Object
     * @param valueB Object
     * @param valueC Object
     */
    public Tupel3(final A valueA, final B valueB, final C valueC)
    {
        super(valueA, valueB);

        this.valueC = valueC;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!super.equals(obj) || !(obj instanceof Tupel3<?, ?, ?> other))
        {
            return false;
        }

        if (!Objects.equals(this.valueC, other.valueC))
        {
            return false;
        }

        return true;
    }

    /**
     * @return Object
     */
    public final C getValueC()
    {
        return this.valueC;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((this.valueC == null) ? 4 : this.valueC.hashCode());

        return result;
    }

    /**
     * @param valueC Object
     */
    public final void setValueC(final C valueC)
    {
        this.valueC = valueC;
    }

    /**
     * @see de.freese.base.core.model.tupel.Tupel2#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("; ");
        sb.append("C=").append(toString(getValueC()));

        return sb.toString();
    }
}
