package de.freese.base.core.model.tupel;

import java.io.Serial;
import java.util.Objects;

/**
 * Ein 5er-Tupel verknüpft 5 Objekte miteinander.
 *
 * @param <A> Konkreter Typ ValueA
 * @param <B> Konkreter Typ ValueB
 * @param <C> Konkreter Typ ValueC
 * @param <D> Konkreter Typ ValueD
 * @param <E> Konkreter Typ ValueE
 *
 * @author Thomas Freese
 */
public class Tupel5<A, B, C, D, E> extends Tupel4<A, B, C, D>
{
    @Serial
    private static final long serialVersionUID = 8990954871886341438L;

    private E valueE;

    public Tupel5()
    {
        super();
    }

    public Tupel5(final A valueA, final B valueB, final C valueC, final D valueD, final E valueE)
    {
        super(valueA, valueB, valueC, valueD);

        this.valueE = valueE;
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

        Tupel5<?, ?, ?, ?, ?> tupel5 = (Tupel5<?, ?, ?, ?, ?>) o;

        return Objects.equals(valueE, tupel5.valueE);
    }

    public final E getValueE()
    {
        return this.valueE;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), valueE);
    }

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
