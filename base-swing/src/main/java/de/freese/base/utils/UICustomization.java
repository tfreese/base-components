package de.freese.base.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Objects;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

import org.jdesktop.swingx.painter.MattePainter;

import de.freese.base.swing.fontchange.SwingFontSizeChanger;

/**
 * @author Thomas Freese
 */
public final class UICustomization {
    private static final String COLOR_ALTERNATING = "color.alternating";
    private static final String COLOR_LIGHT_GRAY = "color.lightgray";

    public static Color getColorAlternating() {
        return UIManager.getColor(COLOR_ALTERNATING);
    }

    public static Color getColorLightGray() {
        return UIManager.getColor(COLOR_LIGHT_GRAY);
    }

    /**
     * Default <code>UIManager.getSystemLookAndFeelClassName()</code>
     *
     * @param className String LookAndFeel, optional
     */
    public static void install(final String className) throws Exception {
        UIManager.setLookAndFeel(className);

        installDefaults();
    }

    public static void main(final String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        writeUIDefaults(System.out);
    }

    public static void setDefaultFont(final Font font) {
        for (Entry<Object, Object> entry : UIManager.getDefaults().entrySet()) {
            final Object key = entry.getKey();
            // Object value = entry.getValue();

            final String keyString = key.toString();

            if (keyString.endsWith(".font") || keyString.endsWith(".acceleratorFont")) {
                UIManager.put(key, font);
            }
            // Nicht alle Fonts werden als Font-Objekte geladen
            // if (value instanceof Font) {
            // UIManager.put(key, font);
            // }
        }

        // Ausnahmen
        UIManager.put("JXTitledPanel.titleFont", font.deriveFont(Font.BOLD));

        // UIManager.put("TitledBorder.font", font.deriveFont(Font.BOLD));
        // UIManager.put("Button.font", font);
        // UIManager.put("CheckBox.font", font);
        // UIManager.put("CheckBoxMenuItem.acceleratorFont", font);
        // UIManager.put("CheckBoxMenuItem.font", font);
        // UIManager.put("ColorChooser.font", font);
        // UIManager.put("ComboBox.font", font);
        // UIManager.put("DesktopIcon.font", font);
        // UIManager.put("EditorPane.font", font);
        // UIManager.put("FormattedTextField.font", font);
        // UIManager.put("InternalFrame.titleFont", font);
        // UIManager.put("Label.font", font);
        // UIManager.put("List.font", font);
        // UIManager.put("Menu.acceleratorFont", font);
        // UIManager.put("Menu.font", font);
        // UIManager.put("MenuBar.font", font);
        // UIManager.put("MenuItem.acceleratorFont", font);
        // UIManager.put("MenuItem.font", font);
        // UIManager.put("OptionPane.font", font);
        // UIManager.put("Panel.font", font);
        // UIManager.put("PasswordField.font", font);
        // UIManager.put("PopupMenu.font", font);
        // UIManager.put("ProgressBar.font", font);
        // UIManager.put("RadioButton.font", font);
        // UIManager.put("RadioButtonMenuItem.acceleratorFont", font);
        // UIManager.put("RadioButtonMenuItem.font", font);
        // UIManager.put("ScrollPane.font", font);
        // UIManager.put("Slider.font", font);
        // UIManager.put("Spinner.font", font);
        // UIManager.put("TabbedPane.font", font);
        // UIManager.put("Table.font", font);
        // UIManager.put("TableHeader.font", font);
        // UIManager.put("TextArea.font", font);
        // UIManager.put("TextField.font", font);
        // UIManager.put("TextPane.font", font);
        // UIManager.put("ToggleButton.font", font);
        // UIManager.put("ToolBar.font", font);
        // UIManager.put("ToolTip.font", font);
        // UIManager.put("Tree.font", font);
        // UIManager.put("Viewport.font", font);
        // UIManager.put("Viewport.font", font);
    }

    public static void writeUIDefaults(final OutputStream outputStream) {
        try (Formatter formatter = new Formatter(outputStream, StandardCharsets.UTF_8, Locale.GERMAN)) {
            final UIDefaults uiDefaults = UIManager.getLookAndFeelDefaults();

            uiDefaults.entrySet().stream()
                    .sorted(Comparator.comparing(entry -> entry.getKey().toString()))
                    .forEach(entry -> {
                        final String key = entry.getKey().toString();
                        final String value = Objects.toString(entry.getValue(), "NULL");

                        formatter.format("%1$s \t %2$s %n", key, value);
                    })
            ;

            formatter.flush();
        }
    }

    private static void installDefaults() {
        final UIDefaults defaults = UIManager.getLookAndFeelDefaults();

        UIManager.put("FileChooser.useSystemIcons", Boolean.TRUE);

        setDefaultFont(SwingFontSizeChanger.getInstance().getFont());

        // Platz für die Ausnahmen
        defaults.put(COLOR_LIGHT_GRAY, new Color(215, 215, 215));
        defaults.put(COLOR_ALTERNATING, new Color(215, 215, 215));

        defaults.put("ColorChooserUI", "de.freese.base.swing.ui.ColorChooserUI");
        defaults.put("Table.alternatingBackground", getColorAlternating());
        defaults.put("Table.alternateRowColor", getColorAlternating());
        defaults.put("Tree.alternatingBackground", getColorAlternating());
        defaults.put("List.alternatingBackground", getColorAlternating());
        defaults.put("TreeTableCellRenderer.alternatingBackground", getColorAlternating());

        // Konstanten für SwingX Komponenten
        defaults.put("TaskPane.titleBackgroundGradientStart", Color.WHITE);
        defaults.put("TaskPane.titleBackgroundGradientEnd", Color.LIGHT_GRAY);
        defaults.put("TaskPane.specialTitleBackground", Color.DARK_GRAY);
        defaults.put("TaskPane.specialTitleForeground", Color.WHITE);
        defaults.put("TaskPane.titleForeground", Color.BLACK);
        defaults.put("TaskPaneContainer.useGradient", Boolean.FALSE);
        defaults.put("TaskPaneContainer.backgroundPainter",
                new MattePainter(new GradientPaint(0, 0, UIManager.getColor("Panel.background"), 0, 1, UIManager.getColor("Panel.background")), true));

        defaults.put("JXTitledPanel.titlePainter", new MattePainter(new GradientPaint(0, 0,
                UIManager.getColor("TaskPane.titleBackgroundGradientStart"),
                0, 1,
                UIManager.getColor("TaskPane.titleBackgroundGradientEnd")), true));

        // Wizard
        defaults.put("nb.errorColor", Color.RED);
        // Für Wizard-Hintergrundfarben
        // System.setProperty("WizardDisplayer.default",".wizard.WizardDisplayerImpl");
    }

    private UICustomization() {
        super();
    }
}
