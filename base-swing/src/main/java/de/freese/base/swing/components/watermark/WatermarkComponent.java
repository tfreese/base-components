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
     */
    ImageIcon getWatermark();

    /**
     * Setzt die Position des Watermarks.
     */
    void setPosition(Point position);

    /**
     * Setzt das Image für den Watermark.
     */
    void setWatermark(ImageIcon watermark);
}
