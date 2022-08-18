package de.freese.base.swing.components.panel;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import de.freese.base.utils.UICustomization;
import org.jdesktop.swingx.JXTitledPanel;

/**
 * TitledPanel für Buttons auf der rechten Seite.
 *
 * @author Thomas Freese
 */
public class ExtTitledPanel extends JXTitledPanel
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 421436804144877867L;

    /**
     * Wenn ButtonStatus ausserhalb des TitledPanel geändert wird, muss er neu gezeichnet werden.
     *
     * @author Thomas Freese
     */
    private class ButtonEnabledPropertyChangeListener implements PropertyChangeListener
    {
        /**
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        @Override
        public void propertyChange(final PropertyChangeEvent evt)
        {
            repaint();
        }
    }
    /**
     * {@link PropertyChangeListener}
     */
    private final transient PropertyChangeListener buttonEnabledPropertyChangeListener;
    /**
     * {@link JPanel} Steuerelemente auf der linken Seite.
     */
    private JPanel leftButtonPanel;
    /**
     * {@link JPanel} Steuerelemente auf der rechten Seite.
     */
    private JPanel rightButtonPanel;

    /**
     * Creates a new {@link ExtTitledPanel} object.
     */
    public ExtTitledPanel()
    {
        super();

        getContentContainer().setLayout(new GridBagLayout());

        setBorder(BorderFactory.createLineBorder(UICustomization.getColorLightGray()));

        this.buttonEnabledPropertyChangeListener = new ButtonEnabledPropertyChangeListener();
    }

    /**
     * Fügt der linken Seite des TitlePanes einen Separator hinzu. (Breite abhängig vom LaF)
     */
    public void addSeparatorLeft()
    {
        getLeftButtonPanel().add(new JSeparator());
    }

    /**
     * Fügt der rechten Seite des TitlePanes einen Separator hinzu. (Breite abhängig vom LaF)
     */
    public void addSeparatorRight()
    {
        getRightButtonPanel().add(new JSeparator());
    }

    /**
     * Hinzufügen einer Komponente zur linken Seite des TitlePane.
     *
     * @param component {@link JComponent}
     */
    public void addTitleComponentLeft(final JComponent component)
    {
        getLeftButtonPanel().add(component);

        component.addPropertyChangeListener("enabled", this.buttonEnabledPropertyChangeListener);
    }

    /**
     * Hinzufügen einer Komponente zur rechten Seite des TitlePane.
     *
     * @param component {@link JComponent}
     */
    public void addTitleComponentRight(final JComponent component)
    {
        getRightButtonPanel().add(component);

        component.addPropertyChangeListener("enabled", this.buttonEnabledPropertyChangeListener);
    }

    /**
     * @return {@link JPanel}
     */
    private JPanel getLeftButtonPanel()
    {
        if (this.leftButtonPanel == null)
        {
            this.leftButtonPanel = new JPanel();
            this.leftButtonPanel.setOpaque(false);
            this.leftButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
            setLeftDecoration(this.leftButtonPanel);
        }

        return this.leftButtonPanel;
    }

    /**
     * @return {@link JPanel}
     */
    private JPanel getRightButtonPanel()
    {
        if (this.rightButtonPanel == null)
        {
            this.rightButtonPanel = new JPanel();
            this.rightButtonPanel.setOpaque(false);
            this.rightButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            setRightDecoration(this.rightButtonPanel);
        }

        return this.rightButtonPanel;
    }
}
