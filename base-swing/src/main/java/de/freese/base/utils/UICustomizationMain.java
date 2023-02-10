// Created: 13.11.22
package de.freese.base.utils;

import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.UIManager;

/**
 * @author Thomas Freese
 */
public final class UICustomizationMain {
    public static void main(final String[] args) throws Exception {
        UICustomization.install(UIManager.getSystemLookAndFeelClassName());

        try (OutputStream os = new FileOutputStream("UIDefaults_" + System.getProperty("java.version") + ".txt")) {
            UICustomization.writeUIDefaults(os);
        }

        // System.err.println("\nAvailable Fonts:");
        // GraphicsEnvironment graphicsEnvironment =
        // GraphicsEnvironment.getLocalGraphicsEnvironment();
        // String[] fontNames = graphicsEnvironment.getAvailableFontFamilyNames();
        //
        // // Iterate the font family names
        // for (String fontName : fontNames)
        // {
        // System.out.println(fontName);
        // }

        // System.err.println("\nAvailable Charsets:");
        // Map<String, Charset> charsets = Charset.availableCharsets();
        //
        // for (Charset charset : charsets.values())
        // {
        // System.out.println(charset);
        // }
    }

    private UICustomizationMain() {
        super();
    }
}
