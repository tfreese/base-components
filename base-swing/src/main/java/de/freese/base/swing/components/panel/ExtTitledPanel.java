package de.freese.base.swing.components.panel;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.jdesktop.swingx.JXTitledPanel;

import de.freese.base.utils.UICustomization;

/**
 * TitledPanel fuer Buttons auf der rechten Seite.
 *
 * @author Thomas Freese
 */
public class ExtTitledPanel extends JXTitledPanel
{
    /**
     * Wenn Buttonstatus ausserhalb des TitledPanel geaendert wird, muss er neu gezeichnet werden.
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
     *
     */
    private static final long serialVersionUID = 421436804144877867L;
    /**
     * {@link PropertyChangeListener}
     */
    private final PropertyChangeListener buttonEnabledPropertyChangeListener;
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
     * Fuegt der linken Seite des TitlePanes einen Separator hinzu. (Breite abhaengig vom LaF)
     */
    public void addSeparatorLeft()
    {
        getLeftButtonPanel().add(new JSeparator());
    }

    /**
     * Fuegt der rechten Seite des TitlePanes einen Separator hinzu. (Breite abhaengig vom LaF)
     */
    public void addSeparatorRight()
    {
        getRightButtonPanel().add(new JSeparator());
    }

    /**
     * Hinzufuegen einer Komponente zur linken Seite des TitlePane.
     *
     * @param component {@link JComponent}
     */
    public void addTitleComponentLeft(final JComponent component)
    {
        getLeftButtonPanel().add(component);

        component.addPropertyChangeListener("enabled", this.buttonEnabledPropertyChangeListener);
    }

    /**
     * Hinzufuegen einer Komponente zur rechten Seite des TitlePane.
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
