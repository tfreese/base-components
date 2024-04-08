package de.freese.base.swing.components.filechooser;

import java.util.Set;

import javax.swing.filechooser.FileFilter;

/**
 * {@link FileFilter} für Bild-Dateien.
 *
 * @author Thomas Freese
 */
public class ImageFileFilter extends GenericFileFilter {
    public ImageFileFilter() {
        super(true, Set.of(".jpeg", ".jpg", ".gif", ".png", ".bmp"));
    }
}
