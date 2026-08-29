package de.freese.base.demo;

import javax.swing.SwingUtilities;

/**
 * @author Thomas Freese
 * @since 13.11.2022
 */
public final class DemoApplicationLauncher {
    static void main() {
        final DemoApplication application = new DemoApplication();

        SwingUtilities.invokeLater(application::start);
    }

    private DemoApplicationLauncher() {
        super();
    }
}
