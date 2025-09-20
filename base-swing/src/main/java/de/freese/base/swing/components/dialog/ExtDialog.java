package de.freese.base.swing.components.dialog;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.Serial;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;

import de.freese.base.swing.layout.GbcBuilder;

/**
 * Resizeable Dialog, bei dem das ActionHandling der Buttons manuell erfolgen kann, um Validierungen möglich zu machen.<br>
 * Strings als Message werden in einer nicht editierbaren {@link JEditorPane} dargestellt, welche auch HTML aufnehmen kann.
 *
 * @author Thomas Freese
 */
public final class ExtDialog {
    public static ExtDialog of(final ExtDialogConfig config) {
        final ExtDialog extDialog = new ExtDialog();
        extDialog.configure(config);

        return extDialog;
    }

    private static void configureIcon(final JDialog dialog, final ExtDialogConfig config) {
        final GridBagConstraints gbc = GbcBuilder.of(0, 0).insets(null).anchorNorthWest();

        Icon icon = config.getIcon();

        if (icon == null) {
            icon = getIconForType(config.getMessageType());
        }

        if (icon != null) {
            gbc.insets = new Insets(10, 10, 10, 5);
        }

        dialog.add(new JLabel(icon), gbc);
    }

    private static void configureMessage(final JDialog dialog, final ExtDialogConfig config) {
        Component messageComponent = null;

        if (config.getMessage() instanceof String message) {
            final JEditorPane editorPane = new JEditorPane();
            editorPane.setEditable(false);
            editorPane.setOpaque(false);
            editorPane.setContentType("text/html");
            editorPane.setEditorKitForContentType("text/plain", new StyledEditorKit());
            editorPane.setEditorKitForContentType("text/html", new HTMLEditorKit());

            final Font font = UIManager.getFont("OptionPane.messageFont");
            editorPane.setFont(font);

            editorPane.setText(message);

            messageComponent = editorPane;
        }
        else {
            messageComponent = (Component) config.getMessage();
        }

        final GridBagConstraints gbc = GbcBuilder.of(1, 0).insets(10, 10, 10, 10).gridHeight(2).anchorNorthWest();
        dialog.add(messageComponent, gbc);
    }

    private static String getButtonText(final int buttonIndex, final String uiKey, final Locale locale, final String[] options) {
        if (options != null) {
            return options[buttonIndex];
        }

        return UIManager.getString(uiKey, locale);
    }

    /**
     * Returns the icon to use for the passed in type.
     */
    private static Icon getIconForType(final int messageType) {
        if (messageType < 0 || messageType > 3) {
            // -1 = PLAIN_MESSAGE
            // 3 = QUESTION_MESSAGE
            return null;
        }

        final String propertyName = switch (messageType) {
            case JOptionPane.ERROR_MESSAGE -> "OptionPane.errorIcon";
            case JOptionPane.INFORMATION_MESSAGE -> "OptionPane.informationIcon";
            case JOptionPane.WARNING_MESSAGE -> "OptionPane.warningIcon";
            case JOptionPane.QUESTION_MESSAGE -> "OptionPane.questionIcon";
            default -> null;
        };

        if (propertyName != null) {
            return UIManager.getIcon(propertyName);
        }

        return null;
    }

    private static int getMnemonic(final String key, final Locale locale) {
        final String value = (String) UIManager.get(key, locale);

        if (value == null) {
            return 0;
        }

        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException _) {
            // Ignore
        }

        return 0;
    }

    private JButton[] buttons;
    private JDialog dialog;
    private int optionClicked = JOptionPane.CLOSED_OPTION;

    private ExtDialog() {
        super();
    }

    public void dispose() {
        optionClicked = JOptionPane.CLOSED_OPTION;

        dialog.dispose();
    }

    public boolean isCancel() {
        return optionClicked == JOptionPane.CANCEL_OPTION;
    }

    public boolean isClosed() {
        return optionClicked == JOptionPane.CLOSED_OPTION;
    }

    public boolean isNo() {
        return optionClicked == JOptionPane.NO_OPTION;
    }

    public boolean isYesOrOK() {
        return optionClicked == JOptionPane.YES_OPTION; // || optionClicked == JOptionPane.OK_OPTION
    }

    public void setLocationRelativeTo(final Component component) {
        dialog.setLocationRelativeTo(component);
    }

    public void setResizable(final boolean resizable) {
        dialog.setResizable(resizable);
    }

    public void setVisible(final boolean visible) {
        optionClicked = JOptionPane.CLOSED_OPTION;

        dialog.setVisible(visible);
    }

    private void configure(final ExtDialogConfig config) {
        final Window window = (Window) SwingUtilities.getAncestorOfClass(Window.class, config.getOwner());

        dialog = switch (window) {
            case Frame f -> new JDialog(f, config.isModal());
            case Dialog d -> new JDialog(d, config.isModal());
            default -> new JDialog((Frame) null, config.isModal());
        };

        dialog.setLayout(new GridBagLayout());
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // Title
        dialog.setTitle(config.getTitle());

        // Icon
        configureIcon(dialog, config);

        // Message
        configureMessage(dialog, config);

        // Buttons
        configureButtons(config);

        // Listener
        configureListener(dialog, config);

        // Default Buttons
        configureDefaultButtons();

        dialog.pack();
        dialog.setResizable(config.isResizeable());
        dialog.setLocationRelativeTo(null);
    }

    private void configureButtons(final ExtDialogConfig config) {
        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BasicOptionPaneUI.ButtonAreaLayout(true, 10));
        buttonPanel.setMinimumSize(new Dimension(10, 30));

        final Locale locale = dialog.getLocale();
        final int optionType = config.getOptionType();
        final String[] options = config.getOptions();

        if (optionType == JOptionPane.DEFAULT_OPTION) {
            buttons = new JButton[]{new JButton()};

            buttons[0].setText(getButtonText(0, "OptionPane.okButtonText", locale, options));
            buttons[0].setMnemonic(getMnemonic("OptionPane.okButtonMnemonic", locale));
            buttons[0].putClientProperty("option", JOptionPane.OK_OPTION);
            // buttons[0].setIcon((Icon) DefaultLookup
            // .get(optionPane, this, "OptionPane.yesIcon"));
        }
        else if (optionType == JOptionPane.YES_NO_OPTION) {
            buttons = new JButton[]{new JButton(), new JButton()};

            buttons[0].setText(getButtonText(0, "OptionPane.yesButtonText", locale, options));
            buttons[0].setMnemonic(getMnemonic("OptionPane.yesButtonMnemonic", locale));
            buttons[0].putClientProperty("option", JOptionPane.YES_OPTION);

            buttons[1].setText(getButtonText(1, "OptionPane.noButtonText", locale, options));
            buttons[1].setMnemonic(getMnemonic("OptionPane.noButtonMnemonic", locale));
            buttons[1].putClientProperty("option", JOptionPane.NO_OPTION);
        }
        else if (optionType == JOptionPane.YES_NO_CANCEL_OPTION) {
            buttons = new JButton[]{new JButton(), new JButton(), new JButton()};

            buttons[0].setText(getButtonText(0, "OptionPane.yesButtonText", locale, options));
            buttons[0].setMnemonic(getMnemonic("OptionPane.yesButtonMnemonic", locale));
            buttons[0].putClientProperty("option", JOptionPane.YES_OPTION);

            buttons[1].setText(getButtonText(1, "OptionPane.noButtonText", locale, options));
            buttons[1].setMnemonic(getMnemonic("OptionPane.noButtonMnemonic", locale));
            buttons[1].putClientProperty("option", JOptionPane.NO_OPTION);

            buttons[2].setText(getButtonText(2, "OptionPane.cancelButtonText", locale, options));
            buttons[2].setMnemonic(getMnemonic("OptionPane.cancelButtonMnemonic", locale));
            buttons[2].putClientProperty("option", JOptionPane.CANCEL_OPTION);
        }
        else if (optionType == JOptionPane.OK_CANCEL_OPTION) {
            buttons = new JButton[]{new JButton(), new JButton()};

            buttons[0].setText(getButtonText(0, "OptionPane.okButtonText", locale, options));
            buttons[0].setMnemonic(getMnemonic("OptionPane.okButtonMnemonic", locale));
            buttons[0].putClientProperty("option", JOptionPane.OK_OPTION);

            buttons[1].setText(getButtonText(1, "OptionPane.cancelButtonText", locale, options));
            buttons[1].setMnemonic(getMnemonic("OptionPane.cancelButtonMnemonic", locale));
            buttons[1].putClientProperty("option", JOptionPane.CANCEL_OPTION);
        }

        for (JButton button : buttons) {
            buttonPanel.add(button);
        }

        final GridBagConstraints gbc = GbcBuilder.of(0, 2).gridWidth(2).insets(10, 10, 10, 10);
        dialog.add(buttonPanel, gbc);
    }

    /**
     * Erster Button reagiert auf ENTER, letzter Button reagiert auf ESC (wenn vorhanden).
     */
    private void configureDefaultButtons() {
        final JButton firstButton = buttons[0];

        InputMap inputMap = firstButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");

        ActionMap actionMap = firstButton.getActionMap();
        actionMap.put("enter", new AbstractAction() {
            @Serial
            private static final long serialVersionUID = -1121213878070135406L;

            @Override
            public void actionPerformed(final ActionEvent e) {
                firstButton.doClick();
            }
        });

        if (buttons.length > 1) {
            final JButton lastButton = buttons[buttons.length - 1];

            inputMap = lastButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc");

            actionMap = lastButton.getActionMap();
            actionMap.put("esc", new AbstractAction() {
                @Serial
                private static final long serialVersionUID = -1982802474829843159L;

                @Override
                public void actionPerformed(final ActionEvent e) {
                    lastButton.doClick();
                }
            });
        }
    }

    private void configureListener(final JDialog dialog, final ExtDialogConfig config) {
        if (config.getWindowListener() != null) {
            dialog.addWindowListener(config.getWindowListener());
        }
        else {
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        }

        final ActionListener defaultActionListener = event -> {
            final JComponent component = (JComponent) event.getSource();
            final Object option = component.getClientProperty("option");

            ExtDialog.this.optionClicked = (Integer) option;
            dialog.dispose();
        };

        for (int i = 0; i < buttons.length; i++) {
            final ActionListener actionListener = config.getButtonActionListener(i);

            if (actionListener != null) {
                if (actionListener instanceof Action a) {
                    buttons[i].setAction(a);
                }
                else {
                    buttons[i].addActionListener(actionListener);
                }
            }
            else {
                // Default
                buttons[i].addActionListener(defaultActionListener);
            }
        }
    }
}
