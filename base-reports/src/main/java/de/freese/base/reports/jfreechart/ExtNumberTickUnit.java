package de.freese.base.reports.jfreechart;

import java.io.Serial;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.jfree.chart.axis.NumberTickUnit;

/**
 * {@link NumberTickUnit} to disable Text for defined Ticks ({@link #setExcludes(Set)}).
 *
 * @author Thomas Freese
 */
public class ExtNumberTickUnit extends NumberTickUnit {
    @Serial
    private static final long serialVersionUID = 8151941607328082952L;

    private transient Set<String> excludes = Collections.emptySet();

    public ExtNumberTickUnit(final double size) {
        super(size);
    }

    public ExtNumberTickUnit(final double size, final NumberFormat formatter) {
        super(size, formatter);
    }

    public ExtNumberTickUnit(final double size, final NumberFormat formatter, final int minorTickCount) {
        super(size, formatter, minorTickCount);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof final ExtNumberTickUnit that)) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        return Objects.equals(excludes, that.excludes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), excludes);
    }

    public void setExcludes(final Set<String> excludes) {
        this.excludes = Objects.requireNonNull(excludes, "excludes required");
    }

    @Override
    public String valueToString(final double value) {
        String strValue = super.valueToString(value);

        for (String exclude : excludes) {
            if (exclude.equals(strValue)) {
                strValue = "";
                break;
            }
        }

        return strValue;
    }
}
