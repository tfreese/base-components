package de.freese.base.swing.components.watermark;

import java.awt.Point;

import javax.swing.ImageIcon;

/**
 * Interface fuer eine Komponente mit Wasserzeichen.
 *
 * @author Thomas Freese
 */
public interface IWatermarkComponent
{
    /**
     * Liefert das Image fuer den Watermark.
     *
     * @return {@link ImageIcon}
     */
    ImageIcon getWatermark();

    /**
     * Setzt die Position des Watermarks.
     *
     * @param position {@link Point}
     */
    void setPosition(Point position);

    /**
     * Setzt das Image fuwr den Watermark.
     *
     * @param watermark {@link ImageIcon}
     */
    void setWatermark(ImageIcon watermark);
}
