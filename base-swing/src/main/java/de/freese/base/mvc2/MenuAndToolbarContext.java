// Created: 24.01.23
package de.freese.base.mvc2;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

/**
 * Requires @{AbstractButton.setActionCommand} on {@link JMenuItem} and {@link JButton}.
 *
 * @author Thomas Freese
 */
public final class MenuAndToolbarContext
{
    public static final class Builder
    {
        private final Map<String, List<Component>> index = new HashMap<>();

        private Builder()
        {
            super();
        }

        public Builder addMenuBarItem(JMenuBar menuBar, JMenu menu, JMenuItem menuItem)
        {
            if (!contains(menuBar, menu))
            {
                menuBar.add(menu);
            }

            if (!contains(menu, menuItem))
            {
                menu.add(menuItem);

                List<Component> list = index.computeIfAbsent(menuItem.getActionCommand(), key -> new ArrayList<>());
                list.add(menu);
                list.add(menuItem);
            }

            return this;
        }

        public Builder addMenuBarItem(JMenuBar menuBar, JMenu menu1, JMenu menu2, JMenuItem menuItem)
        {
            if (!contains(menuBar, menu1))
            {
                menuBar.add(menu1);
            }

            if (!contains(menu1, menu2))
            {
                menu1.add(menu2);
            }

            if (!contains(menu2, menuItem))
            {
                menu2.add(menuItem);

                List<Component> list = index.computeIfAbsent(menuItem.getActionCommand(), key -> new ArrayList<>());
                list.add(menu1);
                list.add(menu2);
                list.add(menuItem);
            }

            return this;
        }

        public Builder addToolBarButton(JToolBar toolBar, AbstractButton abstractButton)
        {
            if (!contains(toolBar, abstractButton))
            {
                toolBar.add(abstractButton);

                index.computeIfAbsent(abstractButton.getActionCommand(), key -> new ArrayList<>()).add(abstractButton);
            }

            return this;
        }

        public MenuAndToolbarContext build()
        {
            MenuAndToolbarContext context = new MenuAndToolbarContext(index);

            for (List<Component> components : index.values())
            {
                for (Component component : components)
                {
                    if (component instanceof JMenuItem mi && mi.getActionCommand() != null)
                    {
                        mi.addActionListener(context::delegateEventToCurrentController);
                    }
                    else if (component instanceof AbstractButton b && b.getActionCommand() != null)
                    {
                        b.addActionListener(context::delegateEventToCurrentController);
                    }
                }
            }

            return context;
        }

        private boolean contains(JComponent parent, JComponent child)
        {
            for (int i = 0; i < parent.getComponentCount(); i++)
            {
                // Must same Reference.
                if (child == parent.getComponent(i))
                {
                    return true;
                }
            }

            return false;
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    private final Map<String, List<Component>> index;

    private Controller currentController;

    private MenuAndToolbarContext(Map<String, List<Component>> index)
    {
        super();

        this.index = index;
    }

    public void deactivateCurrentController()
    {
        if (currentController == null)
        {
            return;
        }

        for (String actionCommand : currentController.getInterestedActions().keySet())
        {
            index.computeIfAbsent(actionCommand, key -> new ArrayList<>()).forEach(component -> component.setEnabled(false));
        }

        currentController = null;

        // TODO Enable Defaults for File/Exit and Help/About.
    }

    public void setCurrentController(Controller controller)
    {
        // TODO Enable Defaults for File/Exit.

        currentController = controller;

        for (String actionCommand : controller.getInterestedActions().keySet())
        {
            index.computeIfAbsent(actionCommand, key -> new ArrayList<>()).forEach(component -> component.setEnabled(true));
        }

        // TODO Enable Default for Help/About.
    }

    private void delegateEventToCurrentController(ActionEvent event)
    {
        if (currentController == null)
        {
            return;
        }

        ActionListener actionListener = currentController.getInterestedActions().get(event.getActionCommand());

        if (actionListener == null)
        {
            return;
        }

        actionListener.actionPerformed(event);
    }
}
