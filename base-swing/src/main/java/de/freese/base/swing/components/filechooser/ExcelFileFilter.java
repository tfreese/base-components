package de.freese.base.swing.components.filechooser;

import java.util.Set;

/**
 * @author Thomas Freese
 */
public class ExcelFileFilter extends GenericFileFilter {
    public ExcelFileFilter() {
        super(true, Set.of(".xls", ".xlsx"));
    }
}
