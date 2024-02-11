package de.freese.base.reports.jfreechart;

import java.io.Serial;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.jfree.chart.axis.NumberTickUnit;

/**
 * {@link NumberTickUnit} to disable Text for defined Ticks ({@link #setExcludes(Set<String>)}).
 *
 * @author Thomas Freese
 */
public class ExtNumberTickUnit extends NumberTickUnit {
    @Serial
    private static final long serialVersionUID = 8151941607328082952L;

    private Set<String> excludes = Collections.emptySet();

    public ExtNumberTickUnit(final double size) {
        super(size);
    }

    public ExtNumberTickUnit(final double size, final NumberFormat formatter) {
        super(size, formatter);
    }

    public ExtNumberTickUnit(final double size, final NumberFormat formatter, final int minorTickCount) {
        super(size, formatter, minorTickCount);
    }

    public void setExcludes(final Set<String> excludes) {
        this.excludes = Objects.requireNonNull(excludes, "excludes required");
    }

    @Override
    public String valueToString(final double value) {
        String strValue = super.valueToString(value);

        for (String exclude : this.excludes) {
            if (exclude.equals(strValue)) {
                strValue = "";
                break;
            }
        }

        return strValue;
    }
}
