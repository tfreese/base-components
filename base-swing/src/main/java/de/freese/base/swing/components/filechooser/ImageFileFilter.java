package de.freese.base.swing.components.filechooser;

import javax.swing.filechooser.FileFilter;

/**
 * {@link FileFilter} für Bild-Dateien.
 *
 * @author Thomas Freese
 */
public class ImageFileFilter extends GenericFileFilter {
    public ImageFileFilter() {
        super(true, ".jpeg", ".jpg", ".gif", ".png", ".bmp");
    }
}
