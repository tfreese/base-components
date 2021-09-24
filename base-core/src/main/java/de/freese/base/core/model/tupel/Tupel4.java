package de.freese.base.core.model.tupel;

import java.util.Objects;

/**
 * Ein 4er-Tupel verknuepft 4 Objekte miteinander.
 *
 * @author Thomas Freese
 *
 * @param <A> Konkreter Typ ValueA
 * @param <B> Konkreter Typ ValueB
 * @param <C> Konkreter Typ ValueC
 * @param <D> Konkreter Typ ValueD
 */
public class Tupel4<A, B, C, D> extends Tupel3<A, B, C>
{
    /**
     *
     */
    private static final long serialVersionUID = -2117258925171560740L;

    /**
     *
     */
    private D valueD;

    /**
     * Erstellt ein neues {@link Tupel4} Object.
     */
    public Tupel4()
    {
        super();
    }

    /**
     * Erstellt ein neues {@link Tupel4} Object.
     *
     * @param valueA Object
     * @param valueB Object
     * @param valueC Object
     * @param valueD Object
     */
    public Tupel4(final A valueA, final B valueB, final C valueC, final D valueD)
    {
        super(valueA, valueB, valueC);

        this.valueD = valueD;
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

        if (!super.equals(obj) || !(obj instanceof Tupel4<?, ?, ?, ?> other))
        {
            return false;
        }

        if (!Objects.equals(this.valueD, other.valueD))
        {
            return false;
        }

        return true;
    }

    /**
     * @return Object
     */
    public final D getValueD()
    {
        return this.valueD;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((this.valueD == null) ? 8 : this.valueD.hashCode());

        return result;
    }

    /**
     * @param valueD Object
     */
    public final void setValueD(final D valueD)
    {
        this.valueD = valueD;
    }

    /**
     * @see de.freese.base.core.model.tupel.Tupel3#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("; ");
        sb.append("D=").append(toString(getValueD()));

        return sb.toString();
    }
}
