// Created: 24.07.2011
package de.freese.base.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Paths;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import de.freese.base.demo.example.ExampleController;
import de.freese.base.demo.fibonacci.FibonacciController;
import de.freese.base.demo.nasa.NasaController;
import de.freese.base.mvc.AbstractApplication;
import de.freese.base.mvc.Controller;
import de.freese.base.mvc.ControllerBuilder;
import de.freese.base.mvc.storage.LocalStorage;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.resourcemap.ResourceMapBuilder;
import de.freese.base.resourcemap.provider.ResourceBundleProvider;
import de.freese.base.resourcemap.provider.ResourceProvider;
import de.freese.base.swing.StatusBar;
import de.freese.base.swing.components.frame.ExtFrame;

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

        SwingUtilities.invokeLater(() ->
        {
            application.initialize();
            // application.getContext().getMainFrame().setVisible(true);
        });
    }

    /**
     * WindowListener zum Beenden.
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
                release();
            }
            catch (Exception ex)
            {
                getLogger().error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * @see de.freese.base.mvc.AbstractApplication#getName()
     */
    @Override
    public String getName()
    {
        return "Demo Application";
        // return getContext().getResourceMapRoot().getString("application.title");
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
     * @see de.freese.base.mvc.AbstractApplication#initController()
     */
    @Override
    protected void initController()
    {
        Controller controller = ControllerBuilder.create(getContext()).name("nasa").bundleName("bundles/nasa").clazz(NasaController.class).build();
        controller.initialize();
        getControllers().add(controller);

        controller = ControllerBuilder.create(getContext()).name("fibonacci").bundleName("bundles/fibonacci").clazz(FibonacciController.class).build();
        controller.initialize();
        getControllers().add(controller);

        controller = ControllerBuilder.create(getContext()).name("example").bundleName("bundles/example").clazz(ExampleController.class).build();
        controller.initialize();
        getControllers().add(controller);
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

        for (Controller controller : getControllers())
        {
            Component component = controller.getView().getComponent();

            ResourceMap resourceMap = controller.getResourceMap();

            tabbedPane.addTab(resourceMap.getString(controller.getName() + ".title"), component);
        }

        // Main-Frame
        ResourceMap resourceMap = getContext().getResourceMapRoot();

        JFrame frame = new ExtFrame();
        getContext().setMainFrame(frame);

        frame.addWindowListener(new MainFrameListener());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setTitle(resourceMap.getString("application.title"));
        frame.add(panel);
        // frame.pack();
        frame.setSize(1920, 1080);
        frame.setLocationRelativeTo(null);

        try
        {
            getContext().getGuiStateManager().restore(frame, "ApplicationFrame");
        }
        catch (Exception ex)
        {
            getLogger().error(ex.getMessage(), ex);
        }

        frame.setVisible(true);
    }

    /**
     * @see de.freese.base.mvc.AbstractApplication#initLaF()
     */
    @Override
    protected void initLaF()
    {
        super.initLaF();

        UIManager.getLookAndFeelDefaults().put("ToolTip.background", Color.WHITE);
    }

    /**
     * @see de.freese.base.mvc.AbstractApplication#initRessourceMap()
     */
    @Override
    protected void initRessourceMap()
    {
        ResourceProvider resourceProvider = new ResourceBundleProvider();
        // ResourceProvider resourceProvider = new AbstractDatabaseResourceProvider() {...};

        // @formatter:off
        ResourceMap rootMap = ResourceMapBuilder.create()
            .resourceProvider(resourceProvider)
            .defaultConverters()
            .bundleName("bundles/demo")
            .addChild()
                .bundleName("bundles/statusbar")
                .done()
            .addChild()
                .bundleName("bundles/nasa")
                .done()
            .addChild()
                .bundleName("bundles/fibonacci")
                .done()
            .addChild()
                .bundleName("bundles/example")
                .cacheDisabled()
                .done()
            .build()
            ;
        // @formatter:on

        rootMap.load(Locale.getDefault());

        setResourceMapRoot(rootMap);
    }
}
