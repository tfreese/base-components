package de.freese.base.core.model.tupel;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class Tupel2<A, B> {
    private A valueA;

    private B valueB;

    public Tupel2() {
        super();
    }

    public Tupel2(final A valueA, final B valueB) {
        super();

        this.valueA = valueA;
        this.valueB = valueB;

        // ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        // Class classA = (Class<A>) type.getActualTypeArguments()[0];
        // Class classB = (Class<B>) type.getActualTypeArguments()[1];
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tupel2<?, ?> tupel2 = (Tupel2<?, ?>) o;

        return Objects.equals(valueA, tupel2.valueA) && Objects.equals(valueB, tupel2.valueB);
    }

    public final A getValueA() {
        return this.valueA;
    }

    public final B getValueB() {
        return this.valueB;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valueA, valueB);
    }

    public final void setValueA(final A valueA) {
        this.valueA = valueA;
    }

    public final void setValueB(final B valueB) {
        this.valueB = valueB;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("A=").append(toString(getValueA()));
        sb.append("; ");
        sb.append("B=").append(toString(getValueB()));

        return sb.toString();
    }

    protected String toString(final Object object) {
        return object == null ? "null" : object.toString();
    }
}
