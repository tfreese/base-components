package de.freese.base.swing.components.watermark;

import java.awt.Point;

import javax.swing.ImageIcon;

/**
 * @author Thomas Freese
 */
public interface WatermarkComponent
{
    /**
     * Liefert das Image für den Watermark.
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
     * Setzt das Image für den Watermark.
     *
     * @param watermark {@link ImageIcon}
     */
    void setWatermark(ImageIcon watermark);
}
