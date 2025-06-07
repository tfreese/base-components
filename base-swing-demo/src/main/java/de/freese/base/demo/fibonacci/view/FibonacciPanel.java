package de.freese.base.demo.fibonacci.view;

import java.awt.GridBagLayout;
import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.freese.base.swing.layout.GbcBuilder;

/**
 * @author Thomas Freese
 */
class FibonacciPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = -2648632168520329957L;

    private JButton buttonComponentBlock;
    private JButton buttonGlassPaneBlock;
    private JLabel label;
    private JLabel labelResult;
    private JTextField textField;

    JButton getButtonComponentBlock() {
        if (buttonComponentBlock == null) {
            buttonComponentBlock = new JButton();
        }

        return buttonComponentBlock;
    }

    JButton getButtonGlassPaneBlock() {
        if (buttonGlassPaneBlock == null) {
            buttonGlassPaneBlock = new JButton();
        }

        return buttonGlassPaneBlock;
    }

    JLabel getLabel() {
        if (label == null) {
            label = new JLabel("#Fibonacci: Wert");
        }

        return label;
    }

    JLabel getLabelResult() {
        if (labelResult == null) {
            labelResult = new JLabel();
        }

        return labelResult;
    }

    JTextField getTextField() {
        if (textField == null) {
            textField = new JTextField();
        }

        return textField;
    }

    void init() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder());

        add(getLabel(), GbcBuilder.of(0, 0));
        add(getTextField(), GbcBuilder.of(1, 0).gridWidth(3).anchorWest().fillHorizontal());
        add(getButtonGlassPaneBlock(), GbcBuilder.of(0, 1).gridWidth(2).fillHorizontal());
        add(getButtonComponentBlock(), GbcBuilder.of(2, 1).gridWidth(2).fillHorizontal());
        add(getLabelResult(), GbcBuilder.of(0, 2).gridWidth(4).anchorCenter().fillVertical());
    }
}
