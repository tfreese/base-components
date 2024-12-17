// Created: 13.11.22
package de.freese.base.swing.components.dialog;

import java.awt.Toolkit;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class ExtDialogMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtDialogMain.class);

    public static void main(final String[] args) {
        final ExtDialogConfig config = new ExtDialogConfig();
        config.setTitle("Test");
        config.setMessageType(JOptionPane.QUESTION_MESSAGE);
        config.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        config.setOptions("Bla", "Blub");
        config.setMessage("Beispieltextdddddddddddddddddddddd<br>dddddddddddddddddddddddddddddddd");
        config.setOwner(null);
        // config.setModal(true);
        config.setButtonActionListener(0, event -> {
            LOGGER.info("ExtDialog.main(...).new ActionListener() {...}.actionPerformed()");
            Toolkit.getDefaultToolkit().beep();
        });

        final ExtDialog dialog = ExtDialog.of(config);
        dialog.setVisible(true);

        LOGGER.info("{}", dialog.isYesOrOK());
    }

    private ExtDialogMain() {
        super();
    }
}
