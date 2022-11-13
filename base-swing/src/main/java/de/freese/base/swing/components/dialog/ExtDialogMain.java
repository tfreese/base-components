// Created: 13.11.22
package de.freese.base.swing.components.dialog;

import java.awt.Toolkit;

import javax.swing.JOptionPane;

/**
 * @author Thomas Freese
 */
public final class ExtDialogMain
{
    public static void main(final String[] args)
    {
        ExtDialogConfig config = new ExtDialogConfig();
        config.setTitle("Test");
        config.setMessageType(JOptionPane.QUESTION_MESSAGE);
        config.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        config.setOptions("Bla", "Blub");
        config.setMessage("Beispieltextdddddddddddddddddddddd<br>dddddddddddddddddddddddddddddddd");
        config.setOwner(null);
        // config.setModal(true);
        config.setButtonActionListener(0, event ->
        {
            System.out.println("ExtDialog.main(...).new ActionListener() {...}.actionPerformed()");
            Toolkit.getDefaultToolkit().beep();
        });

        ExtDialog dialog = new ExtDialog();
        dialog.configure(config);
        dialog.setVisible(true);

        System.out.println(dialog.isYesOrOK());
    }
    
    private ExtDialogMain()
    {
        super();
    }
}
