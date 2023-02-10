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
        if (this.buttonComponentBlock == null) {
            this.buttonComponentBlock = new JButton();
        }

        return this.buttonComponentBlock;
    }

    JButton getButtonGlassPaneBlock() {
        if (this.buttonGlassPaneBlock == null) {
            this.buttonGlassPaneBlock = new JButton();
        }

        return this.buttonGlassPaneBlock;
    }

    JLabel getLabel() {
        if (this.label == null) {
            this.label = new JLabel("#Fibonacci: Wert");
        }

        return this.label;
    }

    JLabel getLabelResult() {
        if (this.labelResult == null) {
            this.labelResult = new JLabel();
        }

        return this.labelResult;
    }

    JTextField getTextField() {
        if (this.textField == null) {
            this.textField = new JTextField();
        }

        return this.textField;
    }

    void init() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder());

        add(getLabel(), GbcBuilder.of(0, 0));
        add(getTextField(), GbcBuilder.of(1, 0).gridwidth(3).anchorWest().fillHorizontal());
        add(getButtonGlassPaneBlock(), GbcBuilder.of(0, 1).gridwidth(2).fillHorizontal());
        add(getButtonComponentBlock(), GbcBuilder.of(2, 1).gridwidth(2).fillHorizontal());
        add(getLabelResult(), GbcBuilder.of(0, 2).gridwidth(4).anchorCenter().fillVertical());
    }
}
