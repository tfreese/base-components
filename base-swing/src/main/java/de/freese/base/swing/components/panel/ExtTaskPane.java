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
public class ExtTaskPane extends JXTaskPane {
    @Serial
    private static final long serialVersionUID = -2295264313854376854L;

    public ExtTaskPane() {
        super();

        setUI(new ExtWindowsClassicTaskPaneUI());
        setScrollOnExpand(false);
        setAnimated(false);
    }

    public void addSeparator() {
        ((ExtWindowsClassicTaskPaneUI) getUI()).addSeparator();
    }

    public void addSeparator(final Dimension dimension) {
        ((ExtWindowsClassicTaskPaneUI) getUI()).addSeparator(dimension);
    }

    public void addTitleButton(final JButton button) {
        ((ExtWindowsClassicTaskPaneUI) getUI()).addTitleButton(button);
    }

    public List<JXTaskPane> getChildTaskPanes() {
        final List<JXTaskPane> childTaskPanes = new ArrayList<>();

        final Container container = getContentPane();

        for (int i = 0; i < container.getComponentCount(); i++) {
            final Component component = container.getComponent(i);

            if (component instanceof JXTaskPane p) {
                childTaskPanes.add(p);
            }
        }

        return childTaskPanes;
    }

    // @Override
    // public void setAnimated(final boolean animated)
    // {
    // // Permanent deaktiviert
    // super.setAnimated(false);
    // }
}
