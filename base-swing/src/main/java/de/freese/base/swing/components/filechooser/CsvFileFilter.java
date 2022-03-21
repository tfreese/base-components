package de.freese.base.swing.components.filechooser;

/**
 * @author Thomas Freese
 */
public class CsvFileFilter extends GenericFileFilter
{
    /**
     * Erstellt ein neues {@link CsvFileFilter} Object.
     */
    public CsvFileFilter()
    {
        super(true, ".csv");
    }
}
