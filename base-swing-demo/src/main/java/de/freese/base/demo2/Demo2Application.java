// Created: 24.01.23
package de.freese.base.demo2;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.MenuElement;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 */
public class Demo2Application
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Demo2");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        frame.add(toolBar, BorderLayout.NORTH);

        JLabel label = new JLabel();
        frame.add(label, BorderLayout.CENTER);

        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);

        JMenuItem menuItemExit = new JMenuItem("Exit");
        menuItemExit.setActionCommand("app.file.exit");
        menuItemExit.addActionListener(event -> label.setText("ActionCommand from MenuItem: " + event.getActionCommand()));
        menuFile.add(menuItemExit);

        JMenu menuActions = new JMenu("Actions");
        menuBar.add(menuActions);

        JMenu menuVisiblility = new JMenu("File visiblility");
        menuActions.add(menuVisiblility);

        JMenuItem menuItemFileInvisible = new JMenuItem("Make File invisible");
        menuItemFileInvisible.setActionCommand("app.actions.file.invisible");
        menuItemFileInvisible.addActionListener(event ->
        {
            menuFile.setVisible(false);
            menuItemFileInvisible.setVisible(false);
        });
        menuVisiblility.add(menuItemFileInvisible);

        JMenuItem menuItemFileVisible = new JMenuItem("Make File visible");
        menuItemFileVisible.setActionCommand("app.actions.file.visible");
        menuItemFileVisible.addActionListener(event -> menuFile.setVisible(true));
        menuVisiblility.add(menuItemFileVisible);

        JMenu menuHelp = new JMenu("Help");
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuHelp);

        JMenuItem menuItemHelpAbout = new JMenuItem("About");
        menuItemHelpAbout.setActionCommand("app.help.about");
        menuItemHelpAbout.addActionListener(event -> label.setText("ActionCommand from MenuItem: " + event.getActionCommand()));
        menuHelp.add(menuItemHelpAbout);

        JButton button = new JButton("Test");
        button.setBorder(null);
        button.setActionCommand("app.test");
        button.addActionListener(event -> label.setText("ActionCommand from ToolbarButton: " + event.getActionCommand()));
        toolBar.add(button);

        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        getMenuBarComponents(menuBar);
        getToolBarComponents(toolBar);
    }

    private static List<Component> getMenuBarComponents(JMenuBar menuBar)
    {
        List<Component> list = new ArrayList<>(100);

        for (int i = 0; i < menuBar.getMenuCount(); i++)
        {
            JMenu menu = menuBar.getMenu(i);

            if (menu == null)
            {
                // May be Separator
                continue;
            }

            list.add(menu);

            for (int j = 0; j < menu.getItemCount(); j++)
            {
                JMenuItem menuItem = menu.getItem(j);

                if (menuItem == null)
                {
                    // May be Separator
                    continue;
                }

                list.add(menuItem);

                for (MenuElement menuElement : menuItem.getSubElements())
                {
                    list.add(menuElement.getComponent());

                    for (MenuElement subElement : menuElement.getSubElements())
                    {
                        if (subElement instanceof JMenuItem mi)
                        {
                            list.add(mi);
                        }
                    }
                }
            }
        }

        return list;
    }

    private static List<Component> getToolBarComponents(JToolBar toolBar)
    {
        List<Component> list = new ArrayList<>(100);

        for (int i = 0; i < toolBar.getComponentCount(); i++)
        {
            Component component = toolBar.getComponent(i);

            if (component == null)
            {
                // May be Separator
                continue;
            }

            list.add(component);
        }

        return list;
    }
}
