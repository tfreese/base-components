package de.freese.base.swing.components.panel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.plaf.windows.ExtWindowsClassicTaskPaneUI;

/**
 * Erzeugt eine TaskPane mit angepasster UI für Buttons im Header.
 *
 * @author Thomas Freese
 */
public class ExtTaskPane extends JXTaskPane
{
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -2295264313854376854L;

    /**
     * Creates a new {@link ExtTaskPane} object.
     */
    public ExtTaskPane()
    {
        super();

        setUI(new ExtWindowsClassicTaskPaneUI());
        setScrollOnExpand(false);
        setAnimated(false);
    }

    /**
     * Fügt dem TaskPane einen Separator hinzu.
     */
    public void addSeparator()
    {
        ((ExtWindowsClassicTaskPaneUI) getUI()).addSeparator();
    }

    /**
     * Fügt dem TitlePane einen Separator hinzu.
     *
     * @param dimension {@link Dimension}
     */
    public void addSeparator(final Dimension dimension)
    {
        ((ExtWindowsClassicTaskPaneUI) getUI()).addSeparator(dimension);
    }

    /**
     * Hinzufügen eines Buttons zur TitlePane.
     *
     * @param button JButton
     */
    public void addTitleButton(final JButton button)
    {
        ((ExtWindowsClassicTaskPaneUI) getUI()).addTitleButton(button);
    }

    /**
     * Liefert eine Liste mit allen TaskPanes, welche in dieser TaskPane enthalten sind.
     *
     * @return {@link List}
     */
    public List<JXTaskPane> getChildTaskPanes()
    {
        List<JXTaskPane> childTaskPanes = new ArrayList<>();

        Container container = getContentPane();

        for (int i = 0; i < container.getComponentCount(); i++)
        {
            Component component = container.getComponent(i);

            if (component instanceof JXTaskPane p)
            {
                childTaskPanes.add(p);
            }
        }

        return childTaskPanes;
    }

    // /**
    // * @see org.jdesktop.swingx.JXTaskPane#setAnimated(boolean)
    // */
    // @Override
    // public void setAnimated(final boolean animated)
    // {
    // // Permanent deaktiviert
    // super.setAnimated(false);
    // }
}
