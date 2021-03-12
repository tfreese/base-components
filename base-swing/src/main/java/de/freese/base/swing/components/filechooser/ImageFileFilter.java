package de.freese.base.swing.components.filechooser;

import javax.swing.filechooser.FileFilter;

/**
 * {@link FileFilter} fuer Bild-Dateien.
 *
 * @author Thomas Freese
 */
public class ImageFileFilter extends GenericFileFilter
{
    /**
     * Erstellt ein neues {@link ImageFileFilter} Object.
     */
    public ImageFileFilter()
    {
        super(true, ".jpeg", ".jpg", ".gif", ".png", ".bmp");
    }
}
