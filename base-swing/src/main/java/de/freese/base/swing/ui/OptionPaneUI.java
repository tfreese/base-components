package de.freese.base.swing.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.Serial;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;

/**
 * UI für die OptionPane, um auch den Background des ButtonsPanels dem des MessagePanels angleichen zu können.
 *
 * @author Thomas Freese
 */
public class OptionPaneUI extends BasicOptionPaneUI {
    public static ComponentUI createUI(final JComponent component) {
        return new OptionPaneUI();
    }

    /**
     * @see javax.swing.plaf.basic.BasicOptionPaneUI#createButtonArea()
     */
    @Override
    protected Container createButtonArea() {
        Container c = super.createButtonArea();

        if (c instanceof JComponent jc) {
            jc.setOpaque(false);
        }

        return c;
    }

    /**
     * @see javax.swing.plaf.basic.BasicOptionPaneUI#createMessageArea()
     */
    @Override
    protected Container createMessageArea() {
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setBorder(UIManager.getBorder("OptionPane.messageAreaBorder"));
        top.setLayout(new BorderLayout());

        /* Fill the body. */
        JPanel body = new JPanel();
        body.setOpaque(false);

        JPanel realBody = new JPanel();
        realBody.setOpaque(false);

        realBody.setLayout(new BorderLayout());

        if (getIcon() != null) {
            JPanel sep = new JPanel() {
                @Serial
                private static final long serialVersionUID = 1L;

                /**
                 * @see javax.swing.JComponent#getPreferredSize()
                 */
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(15, 1);
                }
            };

            sep.setOpaque(false);
            realBody.add(sep, BorderLayout.BEFORE_LINE_BEGINS);
        }

        realBody.add(body, BorderLayout.CENTER);

        body.setLayout(new GridBagLayout());

        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = cons.gridy = 0;
        cons.gridwidth = GridBagConstraints.REMAINDER;
        cons.gridheight = 1;
        cons.anchor = GridBagConstraints.LINE_START;
        cons.insets = new Insets(0, 0, 3, 0);

        addMessageComponents(body, cons, getMessage(), getMaxCharactersPerLineCount(), false);
        top.add(realBody, BorderLayout.CENTER);

        addIcon(top);

        return top;
    }
}
