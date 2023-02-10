package de.freese.base.reports.jfreechart;

import java.io.Serial;
import java.text.NumberFormat;

import org.jfree.chart.axis.NumberTickUnit;

/**
 * {@link NumberTickUnit} die den Text ausblenden kann, je nach angegebenen Excludes ( {@link #setExcludes(String...)}).
 *
 * @author Thomas Freese
 */
public class ExtNumberTickUnit extends NumberTickUnit {
    @Serial
    private static final long serialVersionUID = 8151941607328082952L;

    private String[] excludes;

    public ExtNumberTickUnit(final double size) {
        super(size);
    }

    public ExtNumberTickUnit(final double size, final NumberFormat formatter) {
        super(size, formatter);
    }

    public ExtNumberTickUnit(final double size, final NumberFormat formatter, final int minorTickCount) {
        super(size, formatter, minorTickCount);
    }

    public void setExcludes(final String... excludes) {
        this.excludes = excludes;
    }

    /**
     * @see org.jfree.chart.axis.NumberTickUnit#valueToString(double)
     */
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
