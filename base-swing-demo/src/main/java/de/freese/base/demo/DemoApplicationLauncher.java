// Created: 13.11.22
package de.freese.base.demo;

import javax.swing.SwingUtilities;

import de.freese.base.mvc.AbstractApplication;

/**
 * @author Thomas Freese
 */
public final class DemoApplicationLauncher
{
    public static void main(final String[] args)
    {
        final AbstractApplication application = new DemoApplication();

        SwingUtilities.invokeLater(() ->
        {
            application.initialize();
            // application.getContext().getMainFrame().setVisible(true);
        });
    }
    
    private DemoApplicationLauncher()
    {
        super();
    }
}
