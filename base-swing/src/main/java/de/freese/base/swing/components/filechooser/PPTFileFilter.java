package de.freese.base.swing.components.filechooser;

import javax.swing.filechooser.FileFilter;

/**
 * {@link FileFilter} fuer PPT-Dateien.
 *
 * @author Thomas Freese
 */
public class PPTFileFilter extends GenericFileFilter
{
    /**
     * Erstellt ein neues {@link PPTFileFilter} Object.
     */
    public PPTFileFilter()
    {
        super(true, ".ppt");
    }
}
