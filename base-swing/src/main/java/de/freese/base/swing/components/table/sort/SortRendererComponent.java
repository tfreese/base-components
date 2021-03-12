package de.freese.base.swing.components.table.sort;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Komponente zum Rendern der Sortierung auf einer Tabellenspalten-Überschrift.
 *
 * @author Thomas Freese
 */
public class SortRendererComponent extends JPanel
{
    /**
     *
     */
    private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(0, 0, 0, 2);

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5261935215031937262L;

    /**
     * Label mit dem Sortiericon und der Sortierprioritaet
     */
    private JLabel jLabelIcon;

    /**
     * MainComponent
     */
    private final JComponent mainComponent;

    /**
     * Die Farbe fuer den Prioritaetstext
     */
    private final Color textSortColor;

    /**
     * Erstellt ein neues {@link SortRendererComponent} Objekt.
     *
     * @param mainComponent {@link JComponent}
     * @param sortIcon {@link Icon}
     * @param priority String
     * @param textSortColor {@link Color}
     */
    public SortRendererComponent(final JComponent mainComponent, final Icon sortIcon, final String priority, final Color textSortColor)
    {
        super();

        this.mainComponent = mainComponent;
        this.textSortColor = textSortColor;

        Border mainBorder = mainComponent.getBorder();
        setBorder(BorderFactory.createCompoundBorder(mainBorder, EMPTY_BORDER));
        this.mainComponent.setBorder(null);

        setSortIcon(sortIcon);
        setSortPriority(priority);

        initialize();
    }

    /**
     * Liefert die Hauptkomponente
     *
     * @return {@link Component}
     */
    private Component getMainComponent()
    {
        return this.mainComponent;
    }

    /**
     * Liefert das Label fuer die Sortierung.
     *
     * @return {@link JLabel}
     */
    private JLabel getSortLabel()
    {
        if (this.jLabelIcon == null)
        {
            this.jLabelIcon = new JLabel();
            this.jLabelIcon.setForeground(this.textSortColor);
            this.jLabelIcon.setBackground(UIManager.getColor("TableHeader.background"));

            Font font = this.jLabelIcon.getFont().deriveFont(10F);
            this.jLabelIcon.setFont(font);
            this.jLabelIcon.setIconTextGap(2);
        }

        return this.jLabelIcon;
    }

    /**
     * This method initializes this
     */
    private void initialize()
    {
        setLayout(new BorderLayout(1, 0));
        setForeground(UIManager.getColor("TableHeader.foreground"));
        setBackground(UIManager.getColor("TableHeader.background"));
        this.add(getMainComponent(), BorderLayout.CENTER);
        this.add(getSortLabel(), BorderLayout.LINE_END);
    }

    /**
     * Setzt das Icon, dass die Sortierrichtung angibt.
     *
     * @param icon {@link Icon}
     */
    private void setSortIcon(final Icon icon)
    {
        getSortLabel().setIcon(icon);
    }

    /**
     * Setzt die Prioritaet in der Sortierreihenfolge.
     *
     * @param priority String
     */
    private void setSortPriority(final String priority)
    {
        getSortLabel().setText(priority);
    }
}
