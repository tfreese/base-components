// Created: 13.11.22
package de.freese.base.demo;

import javax.swing.SwingUtilities;

/**
 * @author Thomas Freese
 */
public final class DemoApplicationLauncher
{
    public static void main(final String[] args)
    {
        final DemoApplication application = new DemoApplication();

        SwingUtilities.invokeLater(() ->
        {
            application.start();
        });
    }

    private DemoApplicationLauncher()
    {
        super();
    }
}
