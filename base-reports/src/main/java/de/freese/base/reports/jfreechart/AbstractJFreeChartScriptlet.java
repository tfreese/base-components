package de.freese.base.reports.jfreechart;

import java.text.DecimalFormat;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.Series;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYSeries;

import de.freese.base.core.math.ExtMath;

/**
 * Basisklasse zum Erzeugen eines {@link JFreeChart} Diagramms.
 *
 * @author Thomas Freese
 */
public abstract class AbstractJFreeChartScriptlet<T> {
    private static final double[] DEFAULT_TICKS =
            {0.01D, 0.02D, 0.03D, 0.05D, 0.06D, 0.07D, 0.08D, 0.09D, 0.1D, 0.15D, 0.2D, 0.25D, 0.3D, 0.35D, 0.4D, 0.45D, 0.5D, 0.55D, 0.6D, 0.65D, 0.7D, 0.75D, 0.8D, 0.85D, 0.9D,
                    0.95D, 1D, 1.1D, 1.2D, 1.3D, 1.4D, 1.5D, 1.6D, 1.7D, 1.8D, 1.9D, 2D, 2.5D, 3D, 3.5D, 4D, 4.5D, 5D, 5.5D, 6D, 6.5D, 7D, 7.5D, 8D, 8.5D, 9D, 9.5D, 10D, 11D, 12D,
                    13D, 14D, 15D, 16D, 17D, 18D, 19D, 20D, 25D, 30D, 35D, 40D, 45D, 50D, 55D, 60D, 65D, 70D, 75D, 80D, 85D, 90D, 95D, 100D, 110D, 120D, 130D, 140D, 150D, 200D,
                    250D, 300D, 350D, 400D, 450D, 500D, 550D, 600D, 650D, 700D, 750D, 800D, 850D, 900D, 950D, 1000D, 1100D, 1200D, 1300D, 1400D, 1500D, 2000D, 2500D, 3000D, 3500D,
                    4000D, 4500D, 5000D, 5500D, 6000D, 6500D, 7000D, 7500D, 8000D, 8500D, 9000D, 9500D, 10000D, 15000D, 20000D, 25000D, 30000D, 35000D, 40000D, 45000D, 50000D,
                    55000D, 60000D, 65000D, 70000D, 75000D, 80000D, 85000D, 90000D, 95000D, 100000D, 200000D, 300000D, 400000D, 500000D, 600000D, 700000D, 800000D, 900000D,
                    1000000D, 2000000D, 3000000D, 4000000D, 5000000D, 6000000D, 7000000D, 8000000D, 9000000D, 10000000D};

    /**
     * Adapter für die verschiedenen {@link Plot} Typen.
     *
     * @author Thomas Freese
     */
    private static class PlotAdapter {
        private final Plot plot;

        PlotAdapter(final Plot plot) {
            super();

            this.plot = plot;
        }

        public LegendItemSource getRenderer(final int index) {
            if (plot instanceof XYPlot xyPlot) {
                return xyPlot.getRenderer(index);
            }
            else if (plot instanceof CategoryPlot categoryPlot) {
                return categoryPlot.getRenderer(index);
            }

            throw new UnsupportedOperationException(plot.getClass().getSimpleName());
        }

        public int getRendererCount() {
            if (plot instanceof XYPlot xyPlot) {
                return xyPlot.getRendererCount();
            }
            else if (plot instanceof CategoryPlot categoryPlot) {
                return categoryPlot.getRendererCount();
            }

            throw new UnsupportedOperationException(plot.getClass().getSimpleName());
        }

        public Axis getXAxis() {
            if (plot instanceof XYPlot xyPlot) {
                return xyPlot.getRangeAxis();
            }
            else if (plot instanceof CategoryPlot categoryPlot) {
                return categoryPlot.getDomainAxis();
            }

            throw new UnsupportedOperationException(plot.getClass().getSimpleName());
        }

        public Axis getYAxis() {
            if (plot instanceof XYPlot xyPlot) {
                return xyPlot.getDomainAxis();
            }
            else if (plot instanceof CategoryPlot categoryPlot) {
                return categoryPlot.getRangeAxis();
            }

            throw new UnsupportedOperationException(plot.getClass().getSimpleName());
        }
    }

    private DecimalFormat decimalFormatter;
    private DecimalFormat floatFormatter;

    /**
     * Erzeugt das Diagramm.<br>
     * Für die Dekorierung des Diagramms die {@link #decorateChart(Object, JFreeChart)} Methode aufrufen.
     */
    public abstract JFreeChart createChart(T model);

    /**
     * Berechnet den Schrittwert für eine Skalierung mit Angabe der Anzahl der Schritte und einen möglichen oberen Rand in %.
     *
     * @param upperTickMargin double, % eines Ticks als oberer Rand.
     * @param fractionDigits boolean, Ticks mit Kommastellen ?
     */
    @SuppressWarnings("checkstyle:IllegalCatch")
    protected double calculateTick(final double yMaxValue, final int maxTickUnits, final double upperTickMargin, final boolean fractionDigits) {
        if (upperTickMargin < 0 || upperTickMargin > 1) {
            throw new IllegalArgumentException("upperTickMargin must be between 0 and 1 !");
        }

        // Rein numerisch, sieht aber doof aus...
        // double tick = ExtMath.round(yMaxValue / maxTickUnits, 0);

        // Aktuelle Schrittweite (tick) ermitteln
        double tick = ExtMath.round(yMaxValue / maxTickUnits, 4);
        double defaultTick = DEFAULT_TICKS[DEFAULT_TICKS.length - 1];
        int tickIndex = 0;

        // DefaultTick finden, der grösser als tick ist
        for (; tickIndex < DEFAULT_TICKS.length; tickIndex++) {
            if (DEFAULT_TICKS[tickIndex] >= tick) {
                defaultTick = DEFAULT_TICKS[tickIndex];

                break;
            }
        }

        tick = defaultTick;

        // Wenn yMaxValue zu nah am oberen Tick liegt (< tick * upperTickMargin),
        // den nächst grösseren Tick nehmen
        if (((tick * maxTickUnits) - yMaxValue) < (tick * upperTickMargin)) {
            try {
                tickIndex++;
                tick = DEFAULT_TICKS[tickIndex];
            }
            catch (Throwable _) {
                tick = DEFAULT_TICKS[DEFAULT_TICKS.length - 1];
            }
        }

        if (!fractionDigits) {
            while (ExtMath.hasFractionDigits(tick)) {
                tickIndex++;
                tick = DEFAULT_TICKS[tickIndex];
            }
        }

        return tick;
    }

    /**
     * Initialisiert das Layout des Diagramms.<br>
     * Unterstützte {@link Plot}s:<br>
     * <ul>
     * <li>{@link XYPlot}</li>
     * <li>{@link CategoryPlot}</li>
     * </ul>
     */
    protected void decorateChart(final T model, final JFreeChart chart) {
        // Plot
        final Plot plot = chart.getPlot();
        decoratePlot(model, plot);

        final PlotAdapter plotAdapter = new PlotAdapter(plot);

        // x-Achse
        decorateXAxis(model, plot, plotAdapter.getXAxis());

        // y-Achse
        decorateYAxis(model, plot, plotAdapter.getYAxis());

        // Renderer
        for (int i = 0; i < plotAdapter.getRendererCount(); i++) {
            decorateRenderer(model, plot, i, plotAdapter.getRenderer(i));
        }
    }

    /**
     * Initialisiert das Layout, {@link Dataset} und {@link CategoryItemRenderer} des {@link Plot}.
     */
    protected abstract void decoratePlot(T model, Plot plot);

    /**
     * Initialisiert das Layout der Renderer.
     */
    protected abstract void decorateRenderer(T model, Plot plot, int index, LegendItemSource renderer);

    /**
     * Initialisiert das Layout der x-Achse.
     */
    protected abstract void decorateXAxis(T model, Plot plot, Axis axis);

    /**
     * Initialisiert das Layout der y-Achse.
     */
    protected abstract void decorateYAxis(T model, Plot plot, Axis axis);

    /**
     * 123456.123 wird zu 123.456,1
     */
    protected DecimalFormat getDecimalFormatter() {
        if (decimalFormatter == null) {
            decimalFormatter = new DecimalFormat();

            decimalFormatter.setGroupingSize(3);
            decimalFormatter.setMaximumFractionDigits(0);
            decimalFormatter.setDecimalSeparatorAlwaysShown(false);
        }

        return decimalFormatter;
    }

    /**
     * Findet die höchste Zahl auf der y-Achse eines JFreeChart Datasets heraus.
     */
    protected double getYMaxValue(final CategoryDataset dataSet) {
        double yMax = 0.0D;

        for (int row = 0; row < dataSet.getRowCount(); row++) {
            for (int col = 0; col < dataSet.getColumnCount(); col++) {
                final Number value = dataSet.getValue(row, col);

                if (value != null) {
                    yMax = Math.max(yMax, value.doubleValue());
                }
            }
        }

        return yMax;
    }

    /**
     * Findet die höchste Zahl auf der y-Achse einer JFreeChart Serie heraus.
     */
    protected double getYMaxValue(final Series series) {
        double yMax = 0.0D;

        int count = 0;

        if (series instanceof XYSeries xySeries) {
            count = xySeries.getItemCount();
        }
        else if (series instanceof TimeSeries timeSeries) {
            count = timeSeries.getItemCount();
        }

        for (int i = 0; i < count; i++) {
            Number value = null;

            if (series instanceof XYSeries xySeries) {
                value = xySeries.getY(i);
            }
            else if (series instanceof TimeSeries timeSeries) {
                value = timeSeries.getValue(i);
            }

            if (value != null) {
                yMax = Math.max(yMax, value.doubleValue());
            }
        }

        return yMax;
    }

    /**
     * Normalisiert die Daten für die 1000-er Darstellung.
     */
    protected void normalize(final DefaultCategoryDataset dataSet) {
        final int factor = 1000;

        for (int row = 0; row < dataSet.getRowCount(); row++) {
            final Comparable<?> rowKey = dataSet.getRowKey(row);

            for (int column = 0; column < dataSet.getColumnCount(); column++) {
                final Comparable<?> columnKey = dataSet.getColumnKey(column);

                final Number value = dataSet.getValue(rowKey, columnKey);

                if (value == null) {
                    continue;
                }

                dataSet.setValue(value.doubleValue() / factor, rowKey, columnKey);
            }
        }
    }

    /**
     * Formatiert die y-Achse eines Diagramms.
     *
     * @param fractionDigits boolean, Ticks mit Kommastellen ?
     */
    protected void normalizeYTickUnits(final ValueAxis axis, final double yMaxValue, final int maxTickUnits, final double upperTickMargin, final boolean fractionDigits) {
        if (axis == null) {
            return;
        }

        final double tick = calculateTick(yMaxValue, maxTickUnits, upperTickMargin, fractionDigits);

        axis.setTickMarksVisible(true);
        axis.setRange(new Range(0.0D, tick * maxTickUnits), true, true);

        final TickUnits tickUnits = new TickUnits();
        DecimalFormat formatter = getDecimalFormatter();

        // Wenn Tick Kommastellen hat, Formatter wechseln
        // double tickFloor = Math.floor(tick);

        // if ((tick - tickFloor) != 0.0D)
        if (ExtMath.hasFractionDigits(tick)) {
            formatter = getFloatDecimalFormatter();
        }

        for (int i = 0; i <= maxTickUnits; i++) {
            tickUnits.add(new NumberTickUnit(tick * i, formatter));
        }

        axis.setStandardTickUnits(tickUnits);
    }

    /**
     * 1.234 wird zu 1.2
     */
    private DecimalFormat getFloatDecimalFormatter() {
        if (floatFormatter == null) {
            floatFormatter = new DecimalFormat();
            floatFormatter.setMinimumFractionDigits(1);
            floatFormatter.setDecimalSeparatorAlwaysShown(true);
        }

        return floatFormatter;
    }
}
