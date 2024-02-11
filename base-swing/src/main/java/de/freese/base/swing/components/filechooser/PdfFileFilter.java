package de.freese.base.swing.components.filechooser;

import java.util.Set;

/**
 * @author Thomas Freese
 */
public class PdfFileFilter extends GenericFileFilter {
    public PdfFileFilter() {
        super(true, Set.of(".pdf"));
    }
}
