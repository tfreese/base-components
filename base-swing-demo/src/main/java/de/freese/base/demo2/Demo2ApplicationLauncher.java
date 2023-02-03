// Created: 13.11.22
package de.freese.base.demo2;

import javax.swing.SwingUtilities;

/**
 * @author Thomas Freese
 */
public final class Demo2ApplicationLauncher
{
    public static void main(final String[] args)
    {
        final Demo2Application application = new Demo2Application();

        SwingUtilities.invokeLater(() ->
        {
            application.start();
        });
    }

    private Demo2ApplicationLauncher()
    {
        super();
    }
}
