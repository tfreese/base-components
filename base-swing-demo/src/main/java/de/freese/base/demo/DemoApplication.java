// Created: 24.07.2011
package de.freese.base.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.demo.example.ExampleView;
import de.freese.base.demo.fibonacci.view.DefaultFibonacciView;
import de.freese.base.demo.fibonacci.view.FibonacciView;
import de.freese.base.demo.nasa.view.DefaultNasaView;
import de.freese.base.demo.nasa.view.NasaView;
import de.freese.base.mvc.ApplicationContext;
import de.freese.base.mvc.Releasable;
import de.freese.base.mvc.guistate.GuiStateManager;
import de.freese.base.mvc.guistate.XmlGuiStateManager;
import de.freese.base.mvc.storage.LocalStorage;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.resourcemap.ResourceMapBuilder;
import de.freese.base.resourcemap.provider.ResourceBundleProvider;
import de.freese.base.resourcemap.provider.ResourceProvider;
import de.freese.base.swing.StatusBar;
import de.freese.base.swing.components.frame.ExtFrame;
import de.freese.base.swing.exception.DialogSwingExceptionHandler;
import de.freese.base.swing.exception.SwingExceptionHandler;
import de.freese.base.swing.state.GuiStates;
import de.freese.base.swing.task.TaskManager;
import de.freese.base.utils.ExecutorUtils;
import de.freese.base.utils.UICustomization;

/**
 * Demo Anwendung.
 *
 * @author Thomas Freese
 */
public class DemoApplication {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<Releasable> releasables = new ArrayList<>();

    private Thread shutdownHook;

    public void start() {
        final String applicationName = "Demo Application";

        getLogger().info("Starting {}", applicationName);

        final ApplicationContext applicationContext = new ApplicationContext();
        initApplicationContext(applicationContext, applicationName);
        initResourceMap(applicationContext);
        initLookAndFeel(applicationName);
        initShutdownHook(applicationContext);

        createFrame(applicationContext);
    }

    protected void createFrame(final ApplicationContext applicationContext) {
        getLogger().info("Initialize GUI");

        // Main-Panel
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        final StatusBar statusBar = new StatusBar(applicationContext.getResourceMap("bundles/statusbar"), applicationContext.getService(TaskManager.class));
        statusBar.initialize();
        panel.add(statusBar, BorderLayout.SOUTH);

        final JTabbedPane tabbedPane = new JTabbedPane();
        panel.add(tabbedPane, BorderLayout.CENTER);

        final ExampleView exampleView = new ExampleView();
        String tabTitle = applicationContext.getResourceMap("bundles/example").getString("example.title");
        tabbedPane.addTab(tabTitle, exampleView.initComponent(applicationContext).getComponent());

        final FibonacciView fibonacciView = new DefaultFibonacciView();
        releasables.add(fibonacciView);
        tabTitle = applicationContext.getResourceMap("bundles/fibonacci").getString("fibonacci.title");
        tabbedPane.addTab(tabTitle, fibonacciView.initComponent(applicationContext).getComponent());

        final NasaView nasaView = new DefaultNasaView();
        tabTitle = applicationContext.getResourceMap("bundles/nasa").getString("nasa.title");
        tabbedPane.addTab(tabTitle, nasaView.initComponent(applicationContext).getComponent());

        // Main-Frame
        final ResourceMap resourceMap = applicationContext.getResourceMapRoot();

        final JFrame frame = new ExtFrame();
        applicationContext.setMainFrame(frame);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                try {
                    release(applicationContext);
                }
                catch (Exception ex) {
                    getLogger().error(ex.getMessage(), ex);
                }
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setTitle(resourceMap.getString("application.title"));
        frame.add(panel);
        // frame.pack();
        frame.setSize(1920, 1080);
        frame.setLocationRelativeTo(null);

        try {
            applicationContext.getService(GuiStateManager.class).restore(frame, "ApplicationFrame");
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        frame.setVisible(true);
    }

    protected Logger getLogger() {
        return logger;
    }

    protected void initApplicationContext(final ApplicationContext applicationContext, final String applicationName) {
        getLogger().info("Initialize ApplicationContext");

        // Min. 1 Thread, unused Thread will be terminated after 60 seconds.
        final ExecutorService executorService = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
        applicationContext.registerService(ExecutorService.class, executorService);

        final LocalStorage localStorage = new LocalStorage(Paths.get(System.getProperty("user.home"), ".java-apps", applicationName.toLowerCase().replace(" ", "_")));
        applicationContext.registerService(LocalStorage.class, localStorage);

        applicationContext.registerService(GuiStateManager.class, new XmlGuiStateManager(localStorage, GuiStates.ofDefaults()));

        applicationContext.registerService(SwingExceptionHandler.class, new DialogSwingExceptionHandler());

        Clipboard clipboard = null;

        try {
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        }
        catch (Throwable th) {
            clipboard = new Clipboard("sandbox");
        }

        applicationContext.registerService(Clipboard.class, clipboard);

        applicationContext.registerService(TaskManager.class, new TaskManager(executorService));
    }

    protected void initLookAndFeel(final String applicationName) {
        getLogger().info("Initialize LookAndFeel");

        try {
            // PlasticLookAndFeel.setPlasticTheme(new ExperienceBlue());
            // com.jgoodies.looks.plastic.PlasticXPLookAndFeel
            // com.jgoodies.looks.plastic.Plastic3DLookAndFeel
            // com.jgoodies.looks.plastic.PlasticLookAndFeel
            // com.jgoodies.looks.windows.WindowsLookAndFeel
            // com.sun.java.swing.plaf.windows.WindowsLookAndFeel
            UICustomization.install(UIManager.getSystemLookAndFeelClassName());

            // UICustomization.install("javax.swing.plaf.metal.MetalLookAndFeel");
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        UIManager.getLookAndFeelDefaults().put("ToolTip.background", Color.WHITE);

        if (System.getProperty("os.name").contains("Mac OS X")) {
            // When using the Aqua look and feel, this property puts Swing menus in the Mac OS X
            // menu bar. Note that JMenuBars in JDialogs are not moved to the Mac OS X menu bar.
            System.setProperty("apple.laf.useScreenMenuBar", "true");

            // Set the name of the application menu item
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", applicationName);

            System.setProperty("com.apple.mrj.application.growbox.intrudes", "true");
            System.setProperty("com.apple.mrj.application.live-resize", "true");
            System.setProperty("com.apple.macos.smallTabs", "true");

            // By default, the AWT File Dialog lets you choose a file. Under certain circumstances,
            // however, it may be proper for you to choose a directory instead. If that is the case,
            // set this property to allow for directory selection in a file dialog.
            System.setProperty("apple.awt.fileDialogForDirectories", "true");
        }
    }

    protected void initResourceMap(final ApplicationContext applicationContext) {
        getLogger().info("Initialize ResourceMap");

        final ResourceProvider resourceProvider = new ResourceBundleProvider();
        //final  ResourceProvider resourceProvider = new AbstractDatabaseResourceProvider() {...};

        // @formatter:off
        final ResourceMap rootMap = ResourceMapBuilder.create()
            .resourceProvider(resourceProvider)
                //.converter(..., ...)
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

        applicationContext.setResourceMapRoot(rootMap);
    }

    protected void initShutdownHook(final ApplicationContext applicationContext) {
        getLogger().info("Initialize ShutdownHook");

        final Runnable shutdownTask = () -> {
            try {
                release(applicationContext);
            }
            catch (Exception ex) {
                getLogger().error(ex.getMessage(), ex);
            }
        };

        this.shutdownHook = new Thread(shutdownTask, "ShutdownHook");

        Runtime.getRuntime().addShutdownHook(this.shutdownHook);
    }

    private void release(final ApplicationContext applicationContext) {
        getLogger().info("Release");

        final int option = JOptionPane.showConfirmDialog(applicationContext.getMainFrame(), "Really Exit ?", "Exit", JOptionPane.YES_NO_OPTION);

        if ((option != JOptionPane.YES_OPTION) && (option != JOptionPane.OK_OPTION)) {
            getLogger().info("Release aborted");

            return;
        }

        try {
            applicationContext.getService(GuiStateManager.class).store(applicationContext.getMainFrame(), "ApplicationFrame");

            releasables.forEach(Releasable::release);
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }

        ExecutorUtils.shutdown(applicationContext.getService(ExecutorService.class), getLogger());

        Runtime.getRuntime().removeShutdownHook(this.shutdownHook);

        System.exit(0);
    }
}
