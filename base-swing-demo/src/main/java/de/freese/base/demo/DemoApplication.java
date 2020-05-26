/**
 * Created: 24.07.2011
 */

package de.freese.base.demo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Paths;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import de.freese.base.mvc.AbstractApplication;
import de.freese.base.mvc.MvcPlugin;
import de.freese.base.mvc.context.storage.LocalStorage;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.resourcemap.provider.ResourceBundleProvider;
import de.freese.base.swing.StatusBar;
import de.freese.base.swing.components.ExtFrame;
import de.freese.base.swing.exception.ReleaseVetoException;

/**
 * Demo Anwendung.
 *
 * @author Thomas Freese
 */
public class DemoApplication extends AbstractApplication
{
    /**
     * WindowListener zum beenden.
     *
     * @author Thomas Freese
     */
    private class MainFrameListener extends WindowAdapter
    {
        /**
         * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
         */
        @Override
        public void windowClosing(final WindowEvent e)
        {
            try
            {
                prepareRelease();
                release();
            }
            catch (ReleaseVetoException ex)
            {
                // getLogger().error(null, ex);
            }
            catch (Exception ex)
            {
                getLogger().error(null, ex);
            }
        }
    }

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        final AbstractApplication application = new DemoApplication();

        SwingUtilities.invokeLater(() -> {
            application.initialize();
            // application.getContext().getMainFrame().setVisible(true);
        });
    }

    /**
     * Erstellt ein neues {@link DemoApplication} Object.
     */
    public DemoApplication()
    {
        super();
    }

    /**
     * @see de.freese.base.mvc.AbstractApplication#getName()
     */
    @Override
    public String getName()
    {
        return "Demo Application";
    }

    /**
     * @see de.freese.base.mvc.AbstractApplication#initContext()
     */
    @Override
    protected void initContext()
    {
        super.initContext();

        LocalStorage localStorage = getContext().getLocalStorage();
        localStorage.setDirectory(Paths.get(System.getProperty("user.home"), ".java-apps", getName().replace(' ', '_')));
    }

    /**
     * @see de.freese.base.mvc.AbstractApplication#initFrameAndGui()
     */
    @Override
    protected void initFrameAndGui()
    {
        getLogger().info("Initialize GUI");

        // Main-Panel
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        StatusBar statusBar = new StatusBar(getContext());
        statusBar.initialize();
        panel.add(statusBar, BorderLayout.SOUTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        panel.add(tabbedPane, BorderLayout.CENTER);

        for (MvcPlugin plugin : getPlugins())
        {
            Component component = plugin.getComponent();

            ResourceMap resourceMap = plugin.getResourceMap();
            // ResourceMap resourceMap = getContext().getResourceMap(plugin.getName());

            tabbedPane.addTab(resourceMap.getString(plugin.getName() + ".title"), component);
        }

        // Main-Frame
        ResourceMap resourceMap = getResourceMapRoot();

        JFrame frame = new ExtFrame();
        getContext().setMainFrame(frame);

        frame.addWindowListener(new MainFrameListener());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setTitle(resourceMap.getString("application.title"));
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);

        try
        {
            getContext().getGuiStateManager().restore(frame, "ApplicationFrame");
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }

        frame.setVisible(true);
    }

    /**
     * @see de.freese.base.mvc.AbstractApplication#initRecourceMap()
     */
    @Override
    protected void initRecourceMap()
    {
        ResourceMap rootMap = ResourceMap.create("bundles/demo", new ResourceBundleProvider());
        getContext().addResourceMap("root", rootMap);

        setResourceMapRoot(rootMap);

        ResourceMap statusbarMap = ResourceMap.create("bundles/statusbar");
        statusbarMap.setParent(rootMap);
        getContext().addResourceMap("statusbar", statusbarMap);
    }
}
