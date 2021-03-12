package de.freese.base.swing.components.filechooser;

import javax.swing.filechooser.FileFilter;

/**
 * {@link FileFilter} fuer PDF-Dateien.
 *
 * @author Thomas Freese
 */
public class CSVFileFilter extends GenericFileFilter
{
    /**
     * Erstellt ein neues {@link CSVFileFilter} Object.
     */
    public CSVFileFilter()
    {
        super(true, ".csv");
    }
}
