// Created: 24.01.23
package de.freese.base.demo2;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import de.freese.base.mvc2.MenuAndToolbarContext;

/**
 * @author Thomas Freese
 */
public final class Demo2Application
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

        // MenuBar - File
        JMenuItem menuItemExit = new JMenuItem("Exit");
        menuItemExit.setActionCommand("app.file.exit");
        MenuAndToolbarContext.Builder builder = MenuAndToolbarContext.builder()
                .addMenuBarItem(menuBar, new JMenu("File"), menuItemExit);

        // MenuBar - Actions
        JMenu menuActions = new JMenu("Actions");
        JMenu menuVisiblility = new JMenu("Group 1");
        JMenuItem menuItemFileInvisible = new JMenuItem("Item 1");
        menuItemFileInvisible.setActionCommand("app.actions.group.1");
        JMenuItem menuItemFileVisible = new JMenuItem("Item 2");
        menuItemFileVisible.setActionCommand("app.actions.group.2");
        builder = builder
                .addMenuBarItem(menuBar, menuActions, menuVisiblility, menuItemFileInvisible)
                .addMenuBarItem(menuBar, menuActions, menuVisiblility, menuItemFileVisible)
        ;

        // MenuBar - Help
        JMenuItem menuItemHelpAbout = new JMenuItem("About");
        menuItemHelpAbout.setActionCommand("app.help.about");
        menuBar.add(Box.createHorizontalGlue());
        builder = builder.addMenuBarItem(menuBar, new JMenu("Help"), menuItemHelpAbout);

        // ToolBar
        JButton buttonTest = new JButton("Test");
        buttonTest.setBorder(null);
        buttonTest.setActionCommand("app.test");
        builder = builder.addToolBarButton(toolBar, buttonTest);

        JButton buttonChangeController = new JButton("Change Controller");
        buttonChangeController.setBorder(null);
        buttonChangeController.setActionCommand("app.change.controller");
        builder = builder.addToolBarButton(toolBar, buttonChangeController);

        // Gui
        JLabel label = new JLabel();
        frame.add(label, BorderLayout.CENTER);

        MenuAndToolbarContext context = builder.build();
        context.setCurrentController(() ->
        {
            Map<String, ActionListener> map = new HashMap<>();
            map.put("app.file.exit", event -> label.setText("ActionCommand: " + event.getActionCommand()));
            map.put("app.actions.group.1", event -> label.setText("ActionCommand: " + event.getActionCommand()));
            map.put("app.actions.group.2", event -> label.setText("ActionCommand: " + event.getActionCommand()));
            map.put("app.help.about", event -> label.setText("ActionCommand: " + event.getActionCommand()));
            map.put("app.test", event -> label.setText("ActionCommand: " + event.getActionCommand()));

            map.put("app.change.controller", event ->
                    {
                        context.deactivateCurrentController();
                        context.setCurrentController(() -> Map.of("app.file.exit", e -> System.exit(0)));
                    }
            );

            return map;
        });

        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private Demo2Application()
    {
        super();
    }
}
