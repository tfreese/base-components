package de.freese.base.swing.components.filechooser;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * {@link FileFilter} für bestimmte DatenTypen.
 *
 * @author Thomas Freese
 */
public class GenericFileFilter extends FileFilter implements java.io.FileFilter {
    private final boolean includeDirectories;

    private final String[] types;

    /**
     * @param types String[] Dateitypen, .csv, .xls usw
     */
    public GenericFileFilter(final boolean includeDirectories, final String... types) {
        super();

        this.types = types;
        this.includeDirectories = includeDirectories;
    }

    @Override
    public boolean accept(final File f) {
        if (this.includeDirectories && f.isDirectory()) {
            return true;
        }

        String filename = f.getName();

        for (String type : this.types) {
            if (filename.toLowerCase().endsWith(type)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < this.types.length; i++) {
            sb.append("*").append(this.types[i]);

            if (i < (this.types.length - 1)) {
                sb.append(",");
            }
        }

        return sb.toString();
    }
}
