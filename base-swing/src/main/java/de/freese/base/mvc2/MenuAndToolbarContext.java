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
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Requires @{AbstractButton.setActionCommand} on {@link JMenuItem} and {@link JButton}.
 *
 * @author Thomas Freese
 */
public class MenuAndToolbarContext
{
    private final Map<String, List<Component>> index = new HashMap<>();

    private Controller currentController;

    public MenuAndToolbarContext addMenuItem(JMenu menu1, JMenu menu2, JMenuItem menuItem)
    {
        menuItem.addActionListener(this::delegateEventToCurrentController);

        List<Component> list = index.computeIfAbsent(menuItem.getActionCommand(), key -> new ArrayList<>());
        list.add(menu1);
        list.add(menu2);
        list.add(menuItem);

        return this;
    }

    public MenuAndToolbarContext addMenuItem(JMenu menu, JMenuItem menuItem)
    {
        menuItem.addActionListener(this::delegateEventToCurrentController);

        List<Component> list = index.computeIfAbsent(menuItem.getActionCommand(), key -> new ArrayList<>());
        list.add(menu);
        list.add(menuItem);

        return this;
    }

    public MenuAndToolbarContext addToolbarButton(AbstractButton abstractButton)
    {
        abstractButton.addActionListener(this::delegateEventToCurrentController);

        index.computeIfAbsent(abstractButton.getActionCommand(), key -> new ArrayList<>()).add(abstractButton);

        return this;
    }

    void setActive(Controller controller)
    {
        // TODO Enable Defaults for File/Exit.

        currentController = controller;

        for (String actionCommand : controller.getInterestedActions().keySet())
        {
            index.computeIfAbsent(actionCommand, key -> new ArrayList<>()).forEach(component -> component.setEnabled(true));
        }

        // TODO Enable Default for Help/About.
    }

    void setInactive(Controller controller)
    {
        currentController = null;

        for (String actionCommand : controller.getInterestedActions().keySet())
        {
            index.computeIfAbsent(actionCommand, key -> new ArrayList<>()).forEach(component -> component.setEnabled(false));
        }

        // TODO Enable Defaults for File/Exit and Help/About.
    }

    protected void delegateEventToCurrentController(ActionEvent event)
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
