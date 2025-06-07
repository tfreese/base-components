package de.freese.base.core.model.tupel;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class Tupel5<A, B, C, D, E> extends Tupel4<A, B, C, D> {
    private E valueE;

    public Tupel5() {
        super();
    }

    public Tupel5(final A valueA, final B valueB, final C valueC, final D valueD, final E valueE) {
        super(valueA, valueB, valueC, valueD);

        this.valueE = valueE;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        final Tupel5<?, ?, ?, ?, ?> tupel5 = (Tupel5<?, ?, ?, ?, ?>) o;

        return Objects.equals(valueE, tupel5.valueE);
    }

    public final E getValueE() {
        return valueE;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), valueE);
    }

    public final void setValueE(final E valueE) {
        this.valueE = valueE;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("; ");
        sb.append("E=").append(toString(getValueE()));

        return sb.toString();
    }
}
