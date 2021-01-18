package de.freese.base.core.model.tupel;

import java.io.Serializable;

/**
 * Ein 2er-Tupel verknuepft 2 Objekte miteinander.
 *
 * @author Thomas Freese
 * @param <A> Konkreter Typ ValueA
 * @param <B> Konkreter Typ ValueB
 */
public class Tupel2<A, B> implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = -2114823921211413095L;

    /**
     *
     */
    private A valueA;

    /**
     *
     */
    private B valueB;

    /**
     * Erstellt ein neues {@link Tupel2} Object.
     */
    public Tupel2()
    {
        super();
    }

    /**
     * Erstellt ein neues {@link Tupel2} Object.
     *
     * @param valueA Object
     * @param valueB Object
     */
    public Tupel2(final A valueA, final B valueB)
    {
        super();

        this.valueA = valueA;
        this.valueB = valueB;

        // ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        // Class classA = (Class<A>) type.getActualTypeArguments()[0];
        // Class classB = (Class<B>) type.getActualTypeArguments()[1];
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

        if (obj == null)
        {
            return false;
        }

        if (!(obj instanceof Tupel2<?, ?>))
        {
            return false;
        }

        Tupel2<?, ?> other = (Tupel2<?, ?>) obj;

        if (this.valueA == null)
        {
            if (other.valueA != null)
            {
                return false;
            }
        }
        else if (!this.valueA.equals(other.valueA))
        {
            return false;
        }

        if (this.valueB == null)
        {
            if (other.valueB != null)
            {
                return false;
            }
        }
        else if (!this.valueB.equals(other.valueB))
        {
            return false;
        }

        return true;
    }

    /**
     * @return Object
     */
    public final A getValueA()
    {
        return this.valueA;
    }

    /**
     * @return Object
     */
    public final B getValueB()
    {
        return this.valueB;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.valueA == null) ? 1 : this.valueA.hashCode());
        result = (prime * result) + ((this.valueB == null) ? 2 : this.valueB.hashCode());

        return result;
    }

    /**
     * @param valueA Object
     */
    public final void setValueA(final A valueA)
    {
        this.valueA = valueA;
    }

    /**
     * @param valueB Object
     */
    public final void setValueB(final B valueB)
    {
        this.valueB = valueB;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("A=").append(toString(getValueA()));
        sb.append("; ");
        sb.append("B=").append(toString(getValueB()));

        return sb.toString();
    }

    /**
     * Liefert Object.toString oder "null".
     *
     * @param object Object
     * @return String
     */
    protected String toString(final Object object)
    {
        return object == null ? "null" : object.toString();
    }
}
