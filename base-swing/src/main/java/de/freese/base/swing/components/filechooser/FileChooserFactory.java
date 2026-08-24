package de.freese.base.swing.components.filechooser;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Factory für einen {@link JFileChooser}
 *
 * @author Thomas Freese
 */
public final class FileChooserFactory {
    public static JFileChooser createFileChooser(final Iterable<FileFilter> fileFilter) {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setAcceptAllFileFilterUsed(false);

        for (final FileFilter ff : fileFilter) {
            fileChooser.addChoosableFileFilter(ff);
        }

        return fileChooser;
    }

    private FileChooserFactory() {
        super();
    }
}
