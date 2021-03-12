package de.freese.base.swing.components.filechooser;

import javax.swing.filechooser.FileFilter;

/**
 * {@link FileFilter} fuer XLS-Dateien.
 *
 * @author Thomas Freese
 */
public class XLSFileFilter extends GenericFileFilter
{
    /**
     * Erstellt ein neues {@link XLSFileFilter} Object.
     */
    public XLSFileFilter()
    {
        super(true, ".xls");
    }
}
