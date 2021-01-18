package de.freese.base.core.model.tupel;

/**
 * Ein 5er-Tupel verknuepft 5 Objekte miteinander.
 *
 * @author Thomas Freese
 * @param <A> Konkreter Typ ValueA
 * @param <B> Konkreter Typ ValueB
 * @param <C> Konkreter Typ ValueC
 * @param <D> Konkreter Typ ValueD
 * @param <E> Konkreter Typ ValueE
 */
public class Tupel5<A, B, C, D, E> extends Tupel4<A, B, C, D>
{
    /**
     *
     */
    private static final long serialVersionUID = 8990954871886341438L;

    /**
     *
     */
    private E valueE;

    /**
     * Erstellt ein neues {@link Tupel5} Object.
     */
    public Tupel5()
    {
        super();
    }

    /**
     * Erstellt ein neues {@link Tupel5} Object.
     *
     * @param valueA Object
     * @param valueB Object
     * @param valueC Object
     * @param valueD Object
     * @param valueE Object
     */
    public Tupel5(final A valueA, final B valueB, final C valueC, final D valueD, final E valueE)
    {
        super(valueA, valueB, valueC, valueD);

        this.valueE = valueE;
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

        if (!super.equals(obj))
        {
            return false;
        }

        if (!(obj instanceof Tupel5<?, ?, ?, ?, ?>))
        {
            return false;
        }

        Tupel5<?, ?, ?, ?, ?> other = (Tupel5<?, ?, ?, ?, ?>) obj;

        if (this.valueE == null)
        {
            if (other.valueE != null)
            {
                return false;
            }
        }
        else if (!this.valueE.equals(other.valueE))
        {
            return false;
        }

        return true;
    }

    /**
     * @return Object
     */
    public final E getValueE()
    {
        return this.valueE;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((this.valueE == null) ? 16 : this.valueE.hashCode());

        return result;
    }

    /**
     * @param valueE Object
     */
    public final void setValueE(final E valueE)
    {
        this.valueE = valueE;
    }

    /**
     * @see de.freese.base.core.model.tupel.Tupel4#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("; ");
        sb.append("E=").append(toString(getValueE()));

        return sb.toString();
    }
}
