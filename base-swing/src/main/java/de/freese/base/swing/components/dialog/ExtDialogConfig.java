package de.freese.base.swing.components.dialog;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JOptionPane;

/**
 * Konfigurationsobjekt für den {@link ExtDialog}.
 *
 * @author Thomas Freese
 */
public class ExtDialogConfig {
    private final Map<Integer, ActionListener> buttonActionListeners = new HashMap<>();

    private Icon icon;

    private Object message = "";
    /**
     * @see JOptionPane#PLAIN_MESSAGE
     * @see JOptionPane#INFORMATION_MESSAGE
     * @see JOptionPane#QUESTION_MESSAGE
     * @see JOptionPane#WARNING_MESSAGE
     * @see JOptionPane#ERROR_MESSAGE
     */
    private int messageType = JOptionPane.PLAIN_MESSAGE;

    private boolean modal = true;
    /**
     * @see JOptionPane#DEFAULT_OPTION
     * @see JOptionPane#YES_NO_OPTION
     * @see JOptionPane#YES_NO_CANCEL_OPTION
     * @see JOptionPane#OK_CANCEL_OPTION
     */
    private int optionType = JOptionPane.OK_OPTION;

    private String[] options;

    private Component owner;

    private boolean resizeable = true;

    private String title = "";

    private WindowListener windowListener;

    public ActionListener getButtonActionListener(final int buttonIndex) {
        return this.buttonActionListeners.get(buttonIndex);
    }

    public Icon getIcon() {
        return this.icon;
    }

    public Object getMessage() {
        return this.message;
    }

    public int getMessageType() {
        return this.messageType;
    }

    public int getOptionType() {
        return this.optionType;
    }

    public String[] getOptions() {
        return this.options;
    }

    public Component getOwner() {
        return this.owner;
    }

    public String getTitle() {
        return this.title;
    }

    public WindowListener getWindowListener() {
        return this.windowListener;
    }

    public boolean isModal() {
        return this.modal;
    }

    public boolean isResizeable() {
        return this.resizeable;
    }

    public void setButtonActionListener(final int buttonIndex, final ActionListener actionListener) {
        this.buttonActionListeners.put(buttonIndex, actionListener);
    }

    public void setIcon(final Icon icon) {
        this.icon = icon;
    }

    public void setMessage(final Object message) {
        this.message = message;
    }

    public void setMessageType(final int messageType) {
        this.messageType = messageType;
    }

    public void setModal(final boolean modal) {
        this.modal = modal;
    }

    public void setOptionType(final int optionType) {
        this.optionType = optionType;
    }

    public void setOptions(final String... options) {
        this.options = options;
    }

    public void setOwner(final Component owner) {
        this.owner = owner;
    }

    public void setResizeable(final boolean resizeable) {
        this.resizeable = resizeable;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setWindowListener(final WindowListener windowListener) {
        this.windowListener = windowListener;
    }
}
