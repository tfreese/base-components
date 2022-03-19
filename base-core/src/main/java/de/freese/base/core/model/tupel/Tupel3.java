package de.freese.base.core.model.tupel;

import java.util.Objects;

/**
 * Ein 3er-Tupel verknuepft 3 Objekte miteinander.
 *
 * @param <A> Konkreter Typ ValueA
 * @param <B> Konkreter Typ ValueB
 * @param <C> Konkreter Typ ValueC
 *
 * @author Thomas Freese
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

    @Override
    public boolean equals(final Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        if (!super.equals(o))
        {
            return false;
        }

        Tupel3<?, ?, ?> tupel3 = (Tupel3<?, ?, ?>) o;

        return Objects.equals(valueC, tupel3.valueC);
    }

    /**
     * @return Object
     */
    public final C getValueC()
    {
        return this.valueC;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), valueC);
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
