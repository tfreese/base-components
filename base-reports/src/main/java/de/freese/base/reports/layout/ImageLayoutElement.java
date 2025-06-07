// Created: 15.04.2008
package de.freese.base.reports.layout;

import java.awt.Image;

/**
 * Implementierung eines LayoutElementes für Bilder.
 *
 * @author Thomas Freese
 */
public class ImageLayoutElement extends AbstractLayoutElement {
    private Image image;

    public ImageLayoutElement() {
        super();
    }

    public ImageLayoutElement(final String name) {
        super(name);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(final Image image) {
        this.image = image;
    }
}
