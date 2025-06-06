package de.freese.base.swing.components.table.sort;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * @author Thomas Freese
 */
public class SortRendererComponent extends JPanel {
    private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(0, 0, 0, 2);

    @Serial
    private static final long serialVersionUID = -5261935215031937262L;

    private final JComponent mainComponent;
    private final Color textSortColor;

    private JLabel jLabelIcon;

    public SortRendererComponent(final JComponent mainComponent, final Icon sortIcon, final String priority, final Color textSortColor) {
        super();

        this.mainComponent = mainComponent;
        this.textSortColor = textSortColor;

        final Border mainBorder = mainComponent.getBorder();
        setBorder(BorderFactory.createCompoundBorder(mainBorder, EMPTY_BORDER));
        this.mainComponent.setBorder(null);

        setSortIcon(sortIcon);
        setSortPriority(priority);

        initialize();
    }

    private Component getMainComponent() {
        return mainComponent;
    }

    private JLabel getSortLabel() {
        if (jLabelIcon == null) {
            jLabelIcon = new JLabel();
            jLabelIcon.setForeground(textSortColor);
            jLabelIcon.setBackground(UIManager.getColor("TableHeader.background"));

            final Font font = jLabelIcon.getFont().deriveFont(10F);
            jLabelIcon.setFont(font);
            jLabelIcon.setIconTextGap(2);
        }

        return jLabelIcon;
    }

    private void initialize() {
        setLayout(new BorderLayout(1, 0));
        setForeground(UIManager.getColor("TableHeader.foreground"));
        setBackground(UIManager.getColor("TableHeader.background"));
        add(getMainComponent(), BorderLayout.CENTER);
        add(getSortLabel(), BorderLayout.LINE_END);
    }

    private void setSortIcon(final Icon icon) {
        getSortLabel().setIcon(icon);
    }

    private void setSortPriority(final String priority) {
        getSortLabel().setText(priority);
    }
}
