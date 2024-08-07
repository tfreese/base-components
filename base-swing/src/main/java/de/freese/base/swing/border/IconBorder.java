package de.freese.base.swing.border;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.Serial;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import de.freese.base.utils.ImageUtils;

/**
 * Setzt ein Icon in eine wählbare Ecke des Borders.
 *
 * @author Thomas Freese
 */
public class IconBorder implements Border, Serializable {
    @Serial
    private static final long serialVersionUID = -9139492820598238887L;

    private final int iconPosition;

    private Insets borderInsets;
    private transient Icon icon;
    private JButton iconButton = new JButton();

    public IconBorder(final Icon icon) {
        this(icon, SwingConstants.NORTH_EAST);
    }

    public IconBorder(final Icon icon, final int iconPosition) {
        super();

        this.iconPosition = iconPosition;
        this.borderInsets = null;
        validatePosition(this.iconPosition);

        setIcon(icon);
    }

    @Override
    public Insets getBorderInsets(final Component c) {
        if (this.borderInsets == null) {
            this.borderInsets = switch (this.iconPosition) {
                case SwingConstants.NORTH_WEST -> new Insets(this.icon.getIconHeight(), this.icon.getIconWidth(), 0, 0);
                case SwingConstants.NORTH_EAST -> new Insets(this.icon.getIconHeight(), 0, 0, this.icon.getIconWidth());
                case SwingConstants.SOUTH_WEST -> new Insets(0, this.icon.getIconWidth(), this.icon.getIconHeight(), 0);
                case SwingConstants.SOUTH_EAST -> new Insets(0, 0, this.icon.getIconHeight(), this.icon.getIconWidth());
                default -> new Insets(0, 0, 0, 0);
            };
        }

        return this.borderInsets;
    }

    public JButton getIconButton() {
        if (this.iconButton == null) {
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

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
        int xPos = 0;
        int yPos = 0;

        switch (this.iconPosition) {
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

    public void setIcon(final Icon icon) {
        this.icon = (icon == null) ? ImageUtils.createMissingIcon() : icon;
        this.borderInsets = null;
        getIconButton().setIcon(this.icon);

        final Dimension dimension = new Dimension(this.icon.getIconWidth(), this.icon.getIconHeight());
        getIconButton().setMinimumSize(dimension);
        getIconButton().setPreferredSize(dimension);
        getIconButton().setMaximumSize(dimension);
        getIconButton().setSize(dimension);
    }

    private void validatePosition(final int position) {
        switch (position) {
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
