package de.freese.base.core.model.tupel;

import java.io.Serial;
import java.util.Objects;

/**
 * Ein 4er-Tupel verknüpft 4 Objekte miteinander.
 *
 * @param <A> Konkreter Typ ValueA
 * @param <B> Konkreter Typ ValueB
 * @param <C> Konkreter Typ ValueC
 * @param <D> Konkreter Typ ValueD
 *
 * @author Thomas Freese
 */
public class Tupel4<A, B, C, D> extends Tupel3<A, B, C>
{
    /**
     *
     */
    @Serial
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

        Tupel4<?, ?, ?, ?> tupel4 = (Tupel4<?, ?, ?, ?>) o;

        return Objects.equals(valueD, tupel4.valueD);
    }

    /**
     * @return Object
     */
    public final D getValueD()
    {
        return this.valueD;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), valueD);
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
