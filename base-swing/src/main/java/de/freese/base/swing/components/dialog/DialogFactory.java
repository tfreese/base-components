package de.freese.base.swing.components.dialog;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import de.freese.base.resourcemap.ResourceMap;

/**
 * @author Thomas Freese
 */
public final class DialogFactory {
    public static ExtDialog create(final Component parent, final String title, final Object message, final int messageType, final int optionType, final Icon icon, final boolean show) {
        ExtDialogConfig config = new ExtDialogConfig();
        config.setOwner(parent);
        config.setTitle(title);
        config.setMessage(message);
        config.setMessageType(messageType);
        config.setOptionType(optionType);
        config.setIcon(icon);

        ExtDialog dialog = new ExtDialog();
        dialog.configure(config);
        dialog.setVisible(show);

        return dialog;
    }

    public static ExtDialog createFehler(final Component parent, final ResourceMap resourceMap, final Object message, final boolean show) {
        return create(parent, resourceMap.getString("titel.fehler"), message, JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, null, show);
    }

    public static ExtDialog createInArbeit(final Component parent, final ResourceMap resourceMap, final boolean show) {
        return create(parent, resourceMap.getString("titel.info"), resourceMap.getString("titel.in_arbeit"), JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, resourceMap.getIcon("icons/schleimmann.gif"), show);
    }

    public static ExtDialog createInfo(final Component parent, final ResourceMap resourceMap, final Object message, final boolean show) {
        return create(parent, resourceMap.getString("titel.info"), message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, show);
    }

    public static ExtDialog createJaNein(final Component parent, final ResourceMap resourceMap, final Object message, final boolean show) {
        return createJaNein(parent, resourceMap.getString("titel.ja_nein"), message, JOptionPane.QUESTION_MESSAGE, show);
    }

    public static ExtDialog createJaNein(final Component parent, final String title, final Object message, final boolean show) {
        return createJaNein(parent, title, message, JOptionPane.QUESTION_MESSAGE, show);
    }

    public static ExtDialog createJaNein(final Component parent, final String title, final Object message, final int messageType, final boolean show) {
        return create(parent, title, message, messageType, JOptionPane.YES_NO_OPTION, null, show);
    }

    public static ExtDialog createOkAbbrechen(final Component parent, final ResourceMap resourceMap, final Object message, final boolean show) {
        return createOkAbbrechen(parent, resourceMap.getString("titel.ok_abbrechen"), message, show);
    }

    public static ExtDialog createOkAbbrechen(final Component parent, final String title, final Object message, final boolean show) {
        return createOkAbbrechen(parent, title, message, JOptionPane.PLAIN_MESSAGE, show);
    }

    public static ExtDialog createOkAbbrechen(final Component parent, final String title, final Object message, final int messageType, final boolean show) {
        return create(parent, title, message, messageType, JOptionPane.OK_CANCEL_OPTION, null, show);
    }

    public static ExtDialog createWarnung(final Component parent, final ResourceMap resourceMap, final Object message, final boolean show) {
        return create(parent, resourceMap.getString("titel.warnung"), message, JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION, null, show);
    }

    private DialogFactory() {
        super();
    }
}
