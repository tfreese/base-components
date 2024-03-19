// Created: 08.02.24
package de.freese.base.mvc.menue;

import java.awt.BorderLayout;
import java.util.concurrent.TimeUnit;

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

        final JLabel label = new JLabel("DemoMenuAndToolbarContext");
        panel.add(label, BorderLayout.CENTER);
        frame.setContentPane(panel);

        frame.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            menuAndToolbarContext.resetDefaults();
            menuAndToolbarContext.setActionListener("FILE", "SAVE", event -> actionFileSave(menuAndToolbarContext, label));
        });
    }

    private static void actionApplicationExit(final JLabel label) {
        System.out.println("Application - Exit");
        label.setText("Application - Exit");

        SwingUtilities.invokeLater(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }

            System.exit(-1);
        });
    }

    private static void actionFileSave(final AbstractMenuAndToolbarContext menuAndToolbarContext, final JLabel label) {
        System.out.println("File - Save");
        label.setText("File - Save");

        menuAndToolbarContext.setState("FILE", "SAVE", ComponentState.VISIBLE_DISABLED);

        menuAndToolbarContext.setActionListener("APPLICATION", "EXIT", event -> actionApplicationExit(label));
    }

    @Override
    public void configure() {
        addMenu(ROOT_NAME, "FILE", node -> node.setTextSupplier(() -> "File"));
        addMenuAndToolbarItem("FILE", "SAVE", node -> {
            node.setTextSupplier(() -> "Save");
            node.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("icons/busy/idle.png")));
        });

        addSeparator(ROOT_NAME);

        addMenu(ROOT_NAME, "APPLICATION", node -> node.setTextSupplier(() -> "Application"));
        addMenuAndToolbarItem("APPLICATION", "EXIT", node -> node.setTextSupplier(() -> "Exit"));
    }
}
