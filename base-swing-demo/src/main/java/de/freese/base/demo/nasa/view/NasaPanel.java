package de.freese.base.demo.nasa.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * @author Thomas Freese
 */
class NasaPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 2482594442090386688L;

    private JButton buttonCancel;
    private JButton buttonNext;
    private JButton buttonPrevious;
    private JLabel labelImage;
    private JLabel labelUri;
    private JScrollPane scrollPane;

    JButton getButtonCancel() {
        if (buttonCancel == null) {
            buttonCancel = new JButton();
        }

        return buttonCancel;
    }

    JButton getButtonNext() {
        if (buttonNext == null) {
            buttonNext = new JButton();
        }

        return buttonNext;
    }

    JButton getButtonPrevious() {
        if (buttonPrevious == null) {
            buttonPrevious = new JButton();
        }

        return buttonPrevious;
    }

    JLabel getLabelImage() {
        if (labelImage == null) {
            labelImage = new JLabel();
            labelImage.setOpaque(true);
            labelImage.setHorizontalAlignment(SwingConstants.CENTER);
            labelImage.setVerticalAlignment(SwingConstants.CENTER);
        }

        return labelImage;
    }

    JLabel getLabelUri() {
        if (labelUri == null) {
            labelUri = new JLabel();
        }

        return labelUri;
    }

    JScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new JScrollPane(getLabelImage());
        }

        return scrollPane;
    }

    void init() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder());

        final JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        final Border border = new EmptyBorder(2, 9, 2, 9); // top, left, bottom, right

        final List<JButton> buttons = new ArrayList<>();
        buttons.add(getButtonPrevious());
        buttons.add(getButtonNext());
        buttons.add(getButtonCancel());

        for (JButton button : buttons) {
            button.setBorder(border);
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setFocusable(false);
            toolBar.add(button);
        }

        toolBar.addSeparator(new Dimension(50, 10));
        toolBar.add(getLabelUri());

        add(toolBar, BorderLayout.NORTH);
        add(getScrollPane(), BorderLayout.CENTER);
    }
}
