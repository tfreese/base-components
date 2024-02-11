package de.freese.base.swing.components.filechooser;

import java.util.Set;

/**
 * @author Thomas Freese
 */
public class CsvFileFilter extends GenericFileFilter {
    public CsvFileFilter() {
        super(true, Set.of(".csv"));
    }
}
