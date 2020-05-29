/**
 * Created: 23.07.2011
 */

package de.freese.base.mvc;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.mvc.guistate.GuiStateProvider;
import de.freese.base.mvc.guistate.JsonGuiStateProvider;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.utils.ExecutorUtils;
import de.freese.base.utils.UICustomization;

/**
 * Basisklase einer Java Anwendung.
 *
 * @author Thomas Freese
 */
public abstract class AbstractApplication
{
    /**
     *
     */
    private ApplicationContext context = null;

    /**
     *
     */
    private final List<Controller> controller = new ArrayList<>();

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

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
     * Liefert den {@link ApplicationContext}.
     *
     * @return {@link ApplicationContext}
     */
    public ApplicationContext getContext()
    {
        return this.context;
    }

    /**
     * @return {@link List}<Controller>
     */
    protected List<Controller> getController()
    {
        return this.controller;
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
     * @return String
     */
    public abstract String getName();

    /**
     * Initialisiert den {@link ApplicationContext} der Application.
     */
    protected void initContext()
    {
        getLogger().info("Initialize ApplicationContext");

        setContext(new ApplicationContext());

        // GuiStateProvider guiStateProvider = new XMLGuiStateProvider(this.context.getLocalStorage(), this.context.getGuiStateManager());
        GuiStateProvider guiStateProvider = new JsonGuiStateProvider(this.context.getLocalStorage(), this.context.getGuiStateManager());

        getContext().getGuiStateManager().setStateProvider(guiStateProvider);
    }

    /**
     * Definition der Controller.
     */
    protected abstract void initController();

    /**
     * Konfiguration der Gui.
     */
    protected abstract void initFrameAndGui();

    /**
     * Initialisiert das PlugIns.
     */
    public void initialize()
    {
        getLogger().info("Start Application");

        initContext();
        initRecourceMap();
        initLaF();
        initController();
        initFrameAndGui();
        initShutdownHook();
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
     * Liefert die {@link ResourceMap} der Application.
     */
    protected abstract void initRecourceMap();

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
     * Freigeben verwendeter Resourcen.
     */
    public void release()
    {
        getLogger().info("Release");

        int option = JOptionPane.showConfirmDialog(getContext().getMainFrame(), "Really Exit ?");

        if ((option != JOptionPane.YES_OPTION) && (option != JOptionPane.OK_OPTION))
        {
            getLogger().info("Release aborted");

            return;
        }

        try
        {
            getContext().getGuiStateManager().store(getContext().getMainFrame(), "ApplicationFrame");
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }

        for (Controller controller : getController())
        {
            controller.release();
        }

        ExecutorUtils.shutdown(getContext().getExecutorService(), getLogger());

        Runtime.getRuntime().removeShutdownHook(this.shutdownHook);

        System.exit(0);
    }

    /**
     * @param context {@link ApplicationContext}
     */
    protected void setContext(final ApplicationContext context)
    {
        this.context = context;
    }

    /**
     * Setzt die Root-{@link ResourceMap} der Application.
     *
     * @param resourceMapRoot {@link ResourceMap}
     */
    protected void setResourceMapRoot(final ResourceMap resourceMapRoot)
    {
        getContext().setResourceMapRoot(resourceMapRoot);
    }
}
