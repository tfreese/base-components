// Created: 08.02.24
package de.freese.base.mvc.menue;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * @author Thomas Freese
 */
public class DemoMenuAndToolbarContext extends AbstractMenuAndToolbarContext {
    public static void main(final String[] args) {
        final DemoMenuAndToolbarContext menuAndToolbarContext = new DemoMenuAndToolbarContext();
        menuAndToolbarContext.configure();

        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setTitle("DemoMenuAndToolbarContext");
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);

        frame.setJMenuBar(menuAndToolbarContext.generateMenuBar());

        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(menuAndToolbarContext.generateToolBar(), BorderLayout.NORTH);
        panel.add(new JLabel("DemoMenuAndToolbarContext"), BorderLayout.CENTER);
        frame.setContentPane(panel);

        frame.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            menuAndToolbarContext.resetDefaults();
            menuAndToolbarContext.setActionListener("FILE", "SAVE", event -> System.out.println("FILE.SAFE"));
            menuAndToolbarContext.setActionListener(ROOT_NAME, "EXIT", event -> System.exit(-1));
        });
    }

    @Override
    public void configure() {
        addMenu(ROOT_NAME, "FILE", node -> {
            node.setTextSupplier(() -> "File");

        });
        addMenuAndToolbarItem("FILE", "SAVE", node -> {
            node.setTextSupplier(() -> "Save");
            node.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("icons/busy/idle.png")));
        });

        addSeparator(ROOT_NAME);

        addMenu(ROOT_NAME, "EXPORT", node -> {
            node.setTextSupplier(() -> "Export");
        });
        addMenuAndToolbarItem("EXPORT", "AS_TEXT", node -> {
            node.setTextSupplier(() -> "As Text");
        });

        addToolbarItem(ROOT_NAME, "EXIT", node -> {
            node.setTextSupplier(() -> "Exit");
            node.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("icons/schleimmann.gif")));
        });
        // addMenu(ROOT_NAME, "EXIT", node -> {
        //     node.setTextSupplier(() -> "Exit");
        //     node.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("icons/schleimmann.gif")));
        // });
    }
}
