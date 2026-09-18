package de.freese.base.swing.components.dialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.UIManager;

/**
 * @author Thomas Freese
 * @since 18.09.26
 */
public final class PasswordDialog {
    public static String getAuthToken(final String title) {
        configureSwingFonts(22);

        final JPasswordField passwordField = new JPasswordField();

        // PasswordField without Focus.
        // int choice = JOptionPane.showConfirmDialog(null, passwordField, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        final JOptionPane optionPane = new JOptionPane(passwordField, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {
            @Override
            public void selectInitialValue() {
                passwordField.requestFocus();
            }
        };

        // Components nachträglich vergrößern.
        // applyFontRecursively(optionPane, 22F);

        final JDialog dialog = optionPane.createDialog(null, title);
        dialog.setModal(true);
        dialog.setSize(400, 175);
        dialog.setLocationRelativeTo(null);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        dialog.toFront();

        final Object choice = optionPane.getValue();

        if (Objects.equals(choice, JOptionPane.OK_OPTION)) {
            return String.copyValueOf(passwordField.getPassword());
        }

        return null;
    }

    static void main() {
        final String authToken = getAuthToken("Enter Token");

        System.out.printf("AuthToken: %s%n", authToken);

        System.exit(0);
    }

    /**
     * Recursively enlarges the font of all JComponent children in the given container.
     */
    private static void applyFontRecursively(final Component component, final float fontSize) {
        if (component instanceof final JComponent c) {
            c.setFont(c.getFont().deriveFont(fontSize));
        }

        if (component instanceof final Container container) {
            for (final Component child : container.getComponents()) {
                applyFontRecursively(child, fontSize);
            }
        }
    }

    private static void configureSwingFonts(final int size) {
        final Font font = new Font(Font.SANS_SERIF, Font.PLAIN, size);

        UIManager.put("defaultFont", font);

        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("PasswordField.font", font);
        UIManager.put("FormattedTextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("TextPane.font", font);
        UIManager.put("EditorPane.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("MenuItem.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("RadioButton.font", font);
        UIManager.put("ToolTip.font", font);
    }

    private PasswordDialog() {
        super();
    }
}
