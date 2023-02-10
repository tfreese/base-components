package de.freese.base.swing.components.filechooser;

/**
 * @author Thomas Freese
 */
public class CsvFileFilter extends GenericFileFilter {
    public CsvFileFilter() {
        super(true, ".csv");
    }
}
