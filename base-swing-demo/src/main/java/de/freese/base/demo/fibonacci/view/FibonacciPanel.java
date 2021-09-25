package de.freese.base.demo.fibonacci.view;

import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.freese.base.swing.layout.GbcBuilder;

/**
 * Panel für die Fibonacci Demo.
 *
 * @author Thomas Freese
 */
public class FibonacciPanel extends JPanel
{
    /**
     *
     */
    private static final long serialVersionUID = -2648632168520329957L;
    /**
     *
     */
    private JButton buttonComponentBlock;
    /**
     *
     */
    private JButton buttonGlassPaneBlock;
    /**
     *
     */
    private JLabel label;
    /**
     *
     */
    private JLabel labelResult;
    /**
     *
     */
    private JTextField textField;

    /**
     * @return {@link JButton}
     */
    public JButton getButtonComponentBlock()
    {
        if (this.buttonComponentBlock == null)
        {
            this.buttonComponentBlock = new JButton();
        }

        return this.buttonComponentBlock;
    }

    /**
     * @return {@link JButton}
     */
    public JButton getButtonGlassPaneBlock()
    {
        if (this.buttonGlassPaneBlock == null)
        {
            this.buttonGlassPaneBlock = new JButton();
        }

        return this.buttonGlassPaneBlock;
    }

    /**
     * @return {@link JLabel}
     */
    public JLabel getLabel()
    {
        if (this.label == null)
        {
            this.label = new JLabel("#Fibonacci: Wert");
        }

        return this.label;
    }

    /**
     * @return {@link JLabel}
     */
    public JLabel getLabelResult()
    {
        if (this.labelResult == null)
        {
            this.labelResult = new JLabel();
        }

        return this.labelResult;
    }

    /**
     * @return {@link JTextField}
     */
    public JTextField getTextField()
    {
        if (this.textField == null)
        {
            this.textField = new JTextField();
        }

        return this.textField;
    }

    /**
     * Initialisiert die GUI.
     */
    public void initialize()
    {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder());

        add(getLabel(), new GbcBuilder(0, 0));
        add(getTextField(), new GbcBuilder(1, 0).gridwidth(3).anchorWest().fillHorizontal());
        add(getButtonGlassPaneBlock(), new GbcBuilder(0, 1).gridwidth(2).fillHorizontal());
        add(getButtonComponentBlock(), new GbcBuilder(2, 1).gridwidth(2).fillHorizontal());
        add(getLabelResult(), new GbcBuilder(0, 2).gridwidth(4).anchorCenter().fillVertical());
    }
}
