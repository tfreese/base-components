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

    private static void validatePosition(final int position) {
        if (!(position == SwingConstants.NORTH_WEST
                || position == SwingConstants.NORTH_EAST
                || position == SwingConstants.SOUTH_WEST
                || position == SwingConstants.SOUTH_EAST)
        ) {
            throw new IllegalArgumentException();
        }
    }

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
        validatePosition(iconPosition);

        setIcon(icon);
    }

    @Override
    public Insets getBorderInsets(final Component c) {
        if (borderInsets == null) {
            borderInsets = switch (iconPosition) {
                case SwingConstants.NORTH_WEST -> new Insets(icon.getIconHeight(), icon.getIconWidth(), 0, 0);
                case SwingConstants.NORTH_EAST -> new Insets(icon.getIconHeight(), 0, 0, icon.getIconWidth());
                case SwingConstants.SOUTH_WEST -> new Insets(0, icon.getIconWidth(), icon.getIconHeight(), 0);
                case SwingConstants.SOUTH_EAST -> new Insets(0, 0, icon.getIconHeight(), icon.getIconWidth());
                default -> new Insets(0, 0, 0, 0);
            };
        }

        return borderInsets;
    }

    public JButton getIconButton() {
        if (iconButton == null) {
            iconButton = new JButton();

            // iconButton.setBorder(BorderFactory.createEmptyBorder());
            iconButton.setBorderPainted(false);
            iconButton.setMargin(new Insets(0, 0, 0, 0));
            iconButton.setFocusPainted(false);
            iconButton.setRequestFocusEnabled(false);
            iconButton.setFocusable(false);
            iconButton.setOpaque(false);
            iconButton.setRolloverEnabled(false);
            iconButton.setText("ff");
            iconButton.setActionCommand("BORDER_ICON");

            iconButton.setMinimumSize(new Dimension(16, 16));
            iconButton.setPreferredSize(new Dimension(16, 16));
            iconButton.setMaximumSize(new Dimension(16, 16));
        }

        return iconButton;
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
        int xPos = 0;
        int yPos = 0;

        switch (iconPosition) {
            case SwingConstants.NORTH_WEST:
                break;

            case SwingConstants.NORTH_EAST:
                xPos = width - icon.getIconWidth();

                break;

            case SwingConstants.SOUTH_WEST:
                yPos = height - icon.getIconHeight();

                break;

            case SwingConstants.SOUTH_EAST:
                xPos = width - icon.getIconWidth();
                yPos = height - icon.getIconHeight();

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
        borderInsets = null;
        getIconButton().setIcon(icon);

        final Dimension dimension = new Dimension(this.icon.getIconWidth(), this.icon.getIconHeight());
        getIconButton().setMinimumSize(dimension);
        getIconButton().setPreferredSize(dimension);
        getIconButton().setMaximumSize(dimension);
        getIconButton().setSize(dimension);
    }
}
