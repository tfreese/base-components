package de.freese.base.swing.components.filechooser;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.filechooser.FileFilter;

/**
 * @author Thomas Freese
 */
public class GenericFileFilter extends FileFilter implements java.io.FileFilter {
    private final boolean includeDirectories;

    private final Set<String> types;

    /**
     * @param types List;  Filetypes like .csv, .xls, .pdf
     */
    public GenericFileFilter(final boolean includeDirectories, final Set<String> types) {
        super();

        this.types = types;
        this.includeDirectories = includeDirectories;
    }

    @Override
    public boolean accept(final File f) {
        if (this.includeDirectories && f.isDirectory()) {
            return true;
        }

        final String filename = f.getName();

        for (String type : this.types) {
            if (filename.toLowerCase().endsWith(type)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getDescription() {
        return types.stream().map(type -> "*" + type).collect(Collectors.joining(","));
    }
}
