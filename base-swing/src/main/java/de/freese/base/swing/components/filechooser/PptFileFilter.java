package de.freese.base.swing.components.filechooser;

import java.util.Set;

/**
 * @author Thomas Freese
 */
public class PptFileFilter extends GenericFileFilter {
    public PptFileFilter() {
        super(true, Set.of(".ppt"));
    }
}
