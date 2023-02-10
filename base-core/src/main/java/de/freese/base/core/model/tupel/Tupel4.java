package de.freese.base.core.model.tupel;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class Tupel4<A, B, C, D> extends Tupel3<A, B, C> {
    private D valueD;

    public Tupel4() {
        super();
    }

    public Tupel4(final A valueA, final B valueB, final C valueC, final D valueD) {
        super(valueA, valueB, valueC);

        this.valueD = valueD;
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

        Tupel4<?, ?, ?, ?> tupel4 = (Tupel4<?, ?, ?, ?>) o;

        return Objects.equals(valueD, tupel4.valueD);
    }

    public final D getValueD() {
        return this.valueD;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), valueD);
    }

    public final void setValueD(final D valueD) {
        this.valueD = valueD;
    }

    /**
     * @see de.freese.base.core.model.tupel.Tupel3#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("; ");
        sb.append("D=").append(toString(getValueD()));

        return sb.toString();
    }
}
