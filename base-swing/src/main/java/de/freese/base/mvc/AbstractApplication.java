/**
 * Created: 23.07.2011
 */

package de.freese.base.mvc;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ServiceLoader;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.core.model.Initializeable;
import de.freese.base.core.model.NameProvider;
import de.freese.base.core.release.ReleasePrepareable;
import de.freese.base.core.release.ReleaseVetoException;
import de.freese.base.core.release.Releaseable;
import de.freese.base.mvc.context.ApplicationContext;
import de.freese.base.mvc.context.guistate.GuiStateProvider;
import de.freese.base.mvc.context.guistate.JsonGuiStateProvider;
import de.freese.base.resourcemap.IResourceMap;
import de.freese.base.swing.StatusBar;
import de.freese.base.swing.components.ExtFrame;
import de.freese.base.utils.UICustomization;

/**
 * Basisklase einer Java Anwendung.
 *
 * @author Thomas Freese
 */
public abstract class AbstractApplication implements NameProvider, Initializeable, Releaseable, ReleasePrepareable
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
     *
     */
    private ApplicationContext context = null;

    /**
     *
     */
    private JFrame frame = null;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private JPanel panel = null;

    /**
     *
     */
    private ServiceLoader<MVCPlugin> plugins;

    /**
     *
     */
    private IResourceMap resourceMapRoot = null;

    /**
     *
     */
    private Thread shutdownHook = null;

    /**
     * Erstellt ein neues {@link AbstractApplication} Object.
     */
    public AbstractApplication()
    {
        super();
    }

    /**
     * Erzeugt den konkreten {@link JFrame}.
     *
     * @return {@link JFrame}
     */
    protected JFrame createFrame()
    {
        return new ExtFrame();
    }

    /**
     * Erzeugt das konkrete {@link JPanel}.
     *
     * @return {@link JPanel}
     */
    protected JPanel createPanel()
    {
        return new JPanel();
    }

    /**
     * Liefert den {@link ApplicationContext}.
     *
     * @return {@link ApplicationContext}
     */
    public ApplicationContext getContext()
    {
        return this.context;
    }

    /**
     * Liefert den {@link JFrame} der Application.
     *
     * @return {@link JFrame}
     */
    public JFrame getFrame()
    {
        if (this.frame == null)
        {
            this.frame = createFrame();
        }

        return this.frame;
    }

    /**
     * @return {@link Logger}
     */
    public Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Liefert den Namen der Application fuer den Titel der View.<br>
     * Dieser Name wird auch fuer den Daten-Ordner im User-Verzeichnis verwendet.
     *
     * @see de.freese.base.core.model.NameProvider#getName()
     */
    @Override
    public abstract String getName();

    /**
     * Liefert das Panel der Application.
     *
     * @return {@link JPanel}
     */
    public JPanel getPanel()
    {
        if (this.panel == null)
        {
            this.panel = createPanel();
        }

        return this.panel;
    }

    /**
     * Liefert den {@link ServiceLoader} fuer die Plugins.
     *
     * @return {@link ServiceLoader}
     */
    public ServiceLoader<MVCPlugin> getPlugins()
    {
        return this.plugins;
    }

    /**
     * Liefert die {@link IResourceMap} der Application.
     *
     * @return {@link IResourceMap}
     */
    public IResourceMap getResourceMapRoot()
    {
        return this.resourceMapRoot;
    }

    /**
     * Initialisiert den {@link ApplicationContext} der Application.
     */
    protected void initContext()
    {
        getLogger().info("Initialize ApplicationContext");

        this.context = new ApplicationContext();

        // GuiStateProvider guiStateProvider = new XMLGuiStateProvider(this.context.getLocalStorage(), this.context.getGuiStateManager());
        GuiStateProvider guiStateProvider = new JsonGuiStateProvider(this.context.getLocalStorage(), this.context.getGuiStateManager());

        this.context.getGuiStateManager().setStateProvider(guiStateProvider);
    }

    /**
     * Initialisiert die View der Application.
     */
    protected void initFrame()
    {
        getLogger().info("Initialize Frame");

        IResourceMap resourceMap = getResourceMapRoot();

        JFrame frame = getFrame();
        frame.addWindowListener(new MainFrameListener());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setTitle(resourceMap.getString("application.title"));
        frame.add(getPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);

        try
        {
            getContext().getGuiStateManager().restore(getFrame(), "ApplicationFrame");
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * @see de.freese.base.core.model.Initializeable#initialize()
     */
    @Override
    public void initialize()
    {
        getLogger().info("Start Application");

        initContext();
        initLaF();
        initLaFSystem();
        initRecourceMapRoot();
        loadPlugins();
        initPlugins();
        initShutdownHook();
        initPanel();
        initFrame();
    }

    /**
     * Initialisiert das LookAndFeel.
     */
    protected void initLaF()
    {
        getLogger().info("Initialize LookAndFeel");

        try
        {
            // PlasticLookAndFeel.setPlasticTheme(new ExperienceBlue());
            // com.jgoodies.looks.plastic.PlasticXPLookAndFeel
            // com.jgoodies.looks.plastic.Plastic3DLookAndFeel
            // com.jgoodies.looks.plastic.PlasticLookAndFeel
            // com.jgoodies.looks.windows.WindowsLookAndFeel
            // com.sun.java.swing.plaf.windows.WindowsLookAndFeel
            UICustomization.install(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * Initialisiert LookAndFeel Besonderheiten fuer bestimmte Betriebssysteme.
     */
    protected void initLaFSystem()
    {
        getLogger().info("Initialize System LookAndFeel");

        if (SystemUtils.IS_OS_MAC_OSX)
        {
            // When using the Aqua look and feel, this property puts Swing menus in the Mac OS X
            // menu bar. Note that JMenuBars in JDialogs are not moved to the Mac OS X menu bar.
            System.setProperty("apple.laf.useScreenMenuBar", "true");

            // Set the name of the application menu item
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", getName());

            System.setProperty("com.apple.mrj.application.growbox.intrudes", "true");
            System.setProperty("com.apple.mrj.application.live-resize", "true");
            System.setProperty("com.apple.macos.smallTabs", "true");

            // By default, the AWT File Dialog lets you choose a file. Under certain circumstances,
            // however, it may be proper for you to choose a directory instead. If that is the case,
            // set this property to allow for directory selection in a file dialog.
            System.setProperty("apple.awt.fileDialogForDirectories", "true");
        }
    }

    /**
     * Konfiguration des Panels.
     */
    protected void initPanel()
    {
        getLogger().info("Initialize Panel");

        JPanel panel = getPanel();
        panel.setLayout(new BorderLayout());

        StatusBar statusBar = new StatusBar(getContext());
        statusBar.initialize();
        panel.add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * Initialisiert die Plugins.
     */
    protected void initPlugins()
    {
        getLogger().info("Initialize Plugins");

        for (MVCPlugin plugin : getPlugins())
        {
            plugin.setApplication(this);
            plugin.initialize();
        }
    }

    /**
     * Liefert die {@link IResourceMap} der Application.
     */
    protected abstract void initRecourceMapRoot();

    /**
     * Initialisiert den ShutdownHook.
     */
    protected void initShutdownHook()
    {
        getLogger().info("Initialize ShutdownHook");

        this.shutdownHook = new Thread()
        {
            /**
             * @see java.lang.Thread#run()
             */
            @Override
            public void run()
            {
                // try
                // {
                // prepareRelease();
                // }
                // catch (Exception ex)
                // {
                // getLogger().error(null, ex);
                // }

                try
                {
                    release();
                }
                catch (Exception ex)
                {
                    getLogger().error(null, ex);
                }
            }
        };

        Runtime.getRuntime().addShutdownHook(this.shutdownHook);
    }

    /**
     * Laden der Plugins.
     *
     * @see ServiceLoader
     */
    protected void loadPlugins()
    {
        getLogger().info("Load Plugins");

        this.plugins = ServiceLoader.load(MVCPlugin.class);
    }

    /**
     * @see de.freese.base.core.release.ReleasePrepareable#prepareRelease()
     */
    @Override
    public void prepareRelease() throws ReleaseVetoException
    {
        getLogger().info("Prepare Release");

        int option = JOptionPane.showConfirmDialog(getFrame(), "Really Exit?");

        if ((option != JOptionPane.YES_OPTION) && (option != JOptionPane.OK_OPTION))
        {
            throw new ReleaseVetoException(this, "no exit");
        }

        for (MVCPlugin plugin : getPlugins())
        {
            plugin.prepareRelease();
        }
    }

    /**
     * @see de.freese.base.core.release.Releaseable#release()
     */
    @Override
    public void release()
    {
        getLogger().info("Release");

        try
        {
            getContext().getGuiStateManager().store(getFrame(), "ApplicationFrame");
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }

        for (MVCPlugin plugin : getPlugins())
        {
            plugin.release();
        }

        Runtime.getRuntime().removeShutdownHook(this.shutdownHook);

        getContext().getTaskService().shutdown();

        System.exit(0);
    }

    /**
     * Setzt die {@link IResourceMap} der Application.
     *
     * @param resourceMapRoot {@link IResourceMap}
     */
    protected void setResourceMapRoot(final IResourceMap resourceMapRoot)
    {
        this.resourceMapRoot = resourceMapRoot;
    }
}
