package de.freese.base.mvc.menue;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 * @since 08.02.2024
 */
public class DemoMenuAndToolbarContext extends AbstractMenuAndToolbarContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoMenuAndToolbarContext.class);

    static void main() {
        configureSwingFonts(22);

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
        LOGGER.info("Application - Exit");
        label.setText("Application - Exit");

        SwingUtilities.invokeLater(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            }
            catch (final InterruptedException ex) {
                // Restore interrupted state.
                Thread.currentThread().interrupt();

                throw new RuntimeException(ex);
            }

            System.exit(-1);
        });
    }

    private static void actionFileSave(final AbstractMenuAndToolbarContext menuAndToolbarContext, final JLabel label) {
        LOGGER.info("File - Save");
        label.setText("File - Save");

        menuAndToolbarContext.setState("FILE", "SAVE", ComponentState.VISIBLE_DISABLED);

        menuAndToolbarContext.setActionListener("APPLICATION", "EXIT", event -> actionApplicationExit(label));
    }

    /**
     * Recursively enlarges the font of all JComponent children in the given container.
     */
    private static void applyFontRecursively(final Component component, final float fontSize) {
        if (component instanceof final JComponent c) {
            c.setFont(c.getFont().deriveFont(fontSize));
        }

        if (component instanceof final Container container) {
            for (final Component child : container.getComponents()) {
                applyFontRecursively(child, fontSize);
            }
        }
    }
    
    private static void configureSwingFonts(final int size) {
        final Font font = new Font(Font.SANS_SERIF, Font.PLAIN, size);

        UIManager.put("defaultFont", font);

        UIManager.put("Button.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("PasswordField.font", font);
        UIManager.put("FormattedTextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("TextPane.font", font);
        UIManager.put("EditorPane.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("MenuItem.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("RadioButton.font", font);
        UIManager.put("ToolTip.font", font);
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
