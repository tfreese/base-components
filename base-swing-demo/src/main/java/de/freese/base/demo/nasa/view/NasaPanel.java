package de.freese.base.demo.nasa.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
 * Panel der NASA-Demo.
 *
 * @author Thomas Freese
 */
public class NasaPanel extends JPanel
{
    /**
     *
     */
    private static final long serialVersionUID = 2482594442090386688L;
    /**
     *
     */
    private JButton buttonCancel;
    /**
     *
     */
    private JButton buttonNext;
    /**
     *
     */
    private JButton buttonPrevious;
    /**
     *
     */
    private JLabel labelImage;
    /**
     *
     */
    private JLabel labelURL;
    /**
     *
     */
    private JScrollPane scrollPane;

    /**
     * @return {@link JButton}
     */
    public JButton getButtonCancel()
    {
        if (this.buttonCancel == null)
        {
            this.buttonCancel = new JButton();
        }

        return this.buttonCancel;
    }

    /**
     * @return {@link JButton}
     */
    public JButton getButtonNext()
    {
        if (this.buttonNext == null)
        {
            this.buttonNext = new JButton();
        }

        return this.buttonNext;
    }

    /**
     * @return {@link JButton}
     */
    public JButton getButtonPrevious()
    {
        if (this.buttonPrevious == null)
        {
            this.buttonPrevious = new JButton();
        }

        return this.buttonPrevious;
    }

    /**
     * @return {@link JLabel}
     */
    public JLabel getLabelImage()
    {
        if (this.labelImage == null)
        {
            this.labelImage = new JLabel();
            this.labelImage.setOpaque(true);
            this.labelImage.setHorizontalAlignment(SwingConstants.CENTER);
            this.labelImage.setVerticalAlignment(SwingConstants.CENTER);
        }

        return this.labelImage;
    }

    /**
     * @return {@link JLabel}
     */
    public JLabel getLabelURL()
    {
        if (this.labelURL == null)
        {
            this.labelURL = new JLabel();
        }

        return this.labelURL;
    }

    /**
     * @return {@link JScrollPane}
     */
    public JScrollPane getScrollPane()
    {
        if (this.scrollPane == null)
        {
            this.scrollPane = new JScrollPane(getLabelImage());
        }

        return this.scrollPane;
    }

    /**
     * Initialisiert die GUI.
     */
    public void initialize()
    {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder());

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        Border border = new EmptyBorder(2, 9, 2, 9); // top, left, bottom, right

        List<JButton> buttons = new ArrayList<>();
        buttons.add(getButtonPrevious());
        buttons.add(getButtonNext());
        buttons.add(getButtonCancel());

        for (JButton button : buttons)
        {
            button.setBorder(border);
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setFocusable(false);
            toolBar.add(button);
        }

        toolBar.addSeparator(new Dimension(50, 10));
        toolBar.add(getLabelURL());

        add(toolBar, BorderLayout.NORTH);
        add(getScrollPane(), BorderLayout.CENTER);
    }
}
