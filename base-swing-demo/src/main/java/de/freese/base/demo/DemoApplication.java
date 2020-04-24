/**
 * Created: 24.07.2011
 */

package de.freese.base.demo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.nio.file.Paths;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import de.freese.base.mvc.AbstractApplication;
import de.freese.base.mvc.MVCPlugin;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.resourcemap.provider.ResourceBundleProvider;

/**
 * Demo Anwendung.
 *
 * @author Thomas Freese
 */
public class DemoApplication extends AbstractApplication
{
    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        final AbstractApplication application = new DemoApplication();

        SwingUtilities.invokeLater(() -> {
            application.initialize();
            application.getFrame().setVisible(true);
        });
    }

    /**
     *
     */
    private JTabbedPane tabbedPane = null;

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
        return getContext().getResourceMap("root").getString("application.title");
    }

    /**
     * @return {@link JTabbedPane}
     */
    private JTabbedPane getTabbedPane()
    {
        if (this.tabbedPane == null)
        {
            this.tabbedPane = new JTabbedPane();
        }

        return this.tabbedPane;
    }

    /**
     * @see de.freese.base.mvc.AbstractApplication#initPanel()
     */
    @Override
    protected void initPanel()
    {
        super.initPanel();

        getPanel().add(getTabbedPane(), BorderLayout.CENTER);

        for (MVCPlugin plugin : getPlugins())
        {
            Component component = plugin.getComponent();

            ResourceMap resourceMap = plugin.getResourceMap();
            // IResourceMap resourceMap = getContext().getResourceMap(plugin.getName());

            getTabbedPane().addTab(resourceMap.getString(plugin.getName() + ".title"), component);
        }
    }

    /**
     * @see de.freese.base.mvc.AbstractApplication#initRecourceMapRoot()
     */
    @Override
    protected void initRecourceMapRoot()
    {
        ResourceMap rootMap = ResourceMap.create("bundles/demo", new ResourceBundleProvider());
        getContext().addResourceMap("root", rootMap);

        setResourceMapRoot(rootMap);

        ResourceMap statusbarMap = ResourceMap.create("bundles/statusbar");
        statusbarMap.setParent(rootMap);
        getContext().addResourceMap("statusbar", statusbarMap);

        getContext().getLocalStorage().setDirectory(Paths.get(System.getProperty("user.home"), ".java-apps", getName().replace(' ', '_')));
    }
}
