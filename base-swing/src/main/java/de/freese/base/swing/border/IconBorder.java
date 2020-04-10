package de.freese.base.swing.border;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import de.freese.base.utils.ImageUtils;

/**
 * Setzt ein Icon in eine waehlbare Ecke des Borders.
 *
 * @author Thomas Freese
 */
public class IconBorder implements Border, Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = -9139492820598238887L;

    /**
     *
     */
    private Insets borderInsets = null;

    /**
     *
     */
    private Icon icon = null;

    /**
     *
     */
    private JButton iconButton = new JButton();

    /**
     *
     */
    private final int iconPosition;

    /**
     * Erstellt ein neues {@link IconBorder} Object.
     * 
     * @param icon {@link Icon}
     */
    public IconBorder(final Icon icon)
    {
        this(icon, SwingConstants.NORTH_EAST);
    }

    /**
     * Creates a new {@link IconBorder} object.
     * 
     * @param icon {@link Icon}
     * @param iconPosition int
     */
    public IconBorder(final Icon icon, final int iconPosition)
    {
        super();

        this.iconPosition = iconPosition;
        this.borderInsets = null;
        validatePosition(this.iconPosition);

        setIcon(icon);
    }

    /**
     * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
     */
    @Override
    public Insets getBorderInsets(final Component c)
    {
        if (this.borderInsets == null)
        {
            this.borderInsets = new Insets(0, 0, 0, 0);

            switch (this.iconPosition)
            {
                case SwingConstants.NORTH_WEST:
                    this.borderInsets = new Insets(this.icon.getIconHeight(), this.icon.getIconWidth(), 0, 0);

                    break;

                case SwingConstants.NORTH_EAST:
                    this.borderInsets = new Insets(this.icon.getIconHeight(), 0, 0, this.icon.getIconWidth());

                    break;

                case SwingConstants.SOUTH_WEST:
                    this.borderInsets = new Insets(0, this.icon.getIconWidth(), this.icon.getIconHeight(), 0);

                    break;

                case SwingConstants.SOUTH_EAST:
                    this.borderInsets = new Insets(0, 0, this.icon.getIconHeight(), this.icon.getIconWidth());

                    break;

                default:
                    break;
            }
        }

        return this.borderInsets;
    }

    /**
     * JButton
     * 
     * @return {@link JButton}
     */
    public JButton getIconButton()
    {
        if (this.iconButton == null)
        {
            this.iconButton = new JButton();

            // iconButton.setBorder(BorderFactory.createEmptyBorder());
            this.iconButton.setBorderPainted(false);
            this.iconButton.setMargin(new Insets(0, 0, 0, 0));
            this.iconButton.setFocusPainted(false);
            this.iconButton.setRequestFocusEnabled(false);
            this.iconButton.setFocusable(false);
            this.iconButton.setOpaque(false);
            this.iconButton.setRolloverEnabled(false);
            this.iconButton.setText("ff");
            this.iconButton.setActionCommand("BORDER_ICON");

            this.iconButton.setMinimumSize(new Dimension(16, 16));
            this.iconButton.setPreferredSize(new Dimension(16, 16));
            this.iconButton.setMaximumSize(new Dimension(16, 16));
        }

        return this.iconButton;
    }

    /**
     * @see javax.swing.border.Border#isBorderOpaque()
     */
    @Override
    public boolean isBorderOpaque()
    {
        return false;
    }

    /**
     * @see javax.swing.border.Border#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int)
     */
    @Override
    public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height)
    {
        int xPos = 0;
        int yPos = 0;

        switch (this.iconPosition)
        {
            case SwingConstants.NORTH_WEST:
                break;

            case SwingConstants.NORTH_EAST:
                xPos = width - this.icon.getIconWidth();

                break;

            case SwingConstants.SOUTH_WEST:
                yPos = height - this.icon.getIconHeight();

                break;

            case SwingConstants.SOUTH_EAST:
                xPos = width - this.icon.getIconWidth();
                yPos = height - this.icon.getIconHeight();

                break;

            default:
                break;
        }

        g.translate(xPos, yPos);

        // getIconButton().setBounds(xPos, yPos, getIconButton().getWidth(),
        // getIconButton().getHeight());
        // getIconButton().setBackground(c.getBackground());
        getIconButton().paint(g);

        g.translate(-xPos, -yPos);

        // icon.paintIcon(c, g, xPos, yPos);
    }

    /**
     * Icon
     * 
     * @param icon {@link Icon}
     */
    public void setIcon(final Icon icon)
    {
        this.icon = (icon == null) ? ImageUtils.getMissingIcon() : icon;
        this.borderInsets = null;
        getIconButton().setIcon(this.icon);

        Dimension dimension = new Dimension(this.icon.getIconWidth(), this.icon.getIconHeight());
        getIconButton().setMinimumSize(dimension);
        getIconButton().setPreferredSize(dimension);
        getIconButton().setMaximumSize(dimension);
        getIconButton().setSize(dimension);
    }

    /**
     * Exception, wenn falsche Position.
     * 
     * @param position int
     * @throws IllegalArgumentException Falls was schief geht.
     */
    private void validatePosition(final int position)
    {
        switch (position)
        {
            case SwingConstants.NORTH_WEST:
            case SwingConstants.NORTH_EAST:
            case SwingConstants.SOUTH_WEST:
            case SwingConstants.SOUTH_EAST:
                break;

            default:
                throw new IllegalArgumentException();
        }
    }
}
