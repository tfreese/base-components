package de.freese.base.reports.layout;

import javax.swing.SwingConstants;

/**
 * Implementierung eines LayoutElementes für Linien.
 *
 * @author Thomas Freese
 * @since 15.04.2008
 */
public class LineLayoutElement extends AbstractLayoutElement {
    private final int orientation;
    
    private int thickness = 1;

    public LineLayoutElement() {
        this(SwingConstants.HORIZONTAL);
    }

    /**
     * @param orientation <code>SwingConstants</code>: <code>VERTICAL</code>, oder <code>HORIZONTAL</code>
     */
    public LineLayoutElement(final int orientation) {
        super();

        this.orientation = orientation;
    }

    @Override
    public double getHeight() {
        return switch (getOrientation()) {
            case SwingConstants.HORIZONTAL -> getThickness();
            case SwingConstants.VERTICAL -> super.getHeight();
            default -> -1;
        };
    }

    /**
     * @return int; <code>SwingConstants</code>: <code>VERTICAL</code>, oder <code>HORIZONTAL</code>
     */
    public int getOrientation() {
        return orientation;
    }

    public int getThickness() {
        return thickness;
    }

    @Override
    public double getWidth() {
        return switch (getOrientation()) {
            case SwingConstants.HORIZONTAL -> super.getWidth();
            case SwingConstants.VERTICAL -> getThickness();
            default -> -1;
        };
    }

    public void setThickness(final int thickness) {
        this.thickness = thickness;
    }
}
