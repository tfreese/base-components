package de.freese.base.mvc;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import de.freese.base.mvc.guistate.GuiStateManager;
import de.freese.base.mvc.storage.LocalStorage;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.exception.DialogExceptionHandler;
import de.freese.base.swing.exception.SwingExceptionHandler;
import de.freese.base.swing.task.TaskManager;

/**
 * Context für eine oder mehrere Anwendungen.
 *
 * @author Thomas Freese
 */
public class ApplicationContext
{
    private final SwingExceptionHandler exceptionHandler;

    private final ExecutorService executorService;

    private final GuiStateManager guiStateManager;

    private final LocalStorage localStorage;

    private final TaskManager taskManager;

    private Clipboard clipboard;

    private JFrame mainFrame;

    private ResourceMap resourceMapRoot;

    private String userID;

    public ApplicationContext()
    {
        // this(Executors.newCachedThreadPool());
        this(new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>()));
    }

    public ApplicationContext(final ExecutorService executorService)
    {
        super();

        this.executorService = Objects.requireNonNull(executorService, "executorService required");
        this.taskManager = new TaskManager(executorService);
        this.localStorage = new LocalStorage();
        this.guiStateManager = new GuiStateManager();
        this.exceptionHandler = new DialogExceptionHandler();
    }

    public Clipboard getClipboard()
    {
        if (this.clipboard == null)
        {
            try
            {
                this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            }
            catch (Throwable th)
            {
                this.clipboard = new Clipboard("sandbox");
            }
        }

        return this.clipboard;
    }

    public SwingExceptionHandler getExceptionHandler()
    {
        return this.exceptionHandler;
    }

    public ExecutorService getExecutorService()
    {
        return this.executorService;
    }

    public GuiStateManager getGuiStateManager()
    {
        return this.guiStateManager;
    }

    public LocalStorage getLocalStorage()
    {
        return this.localStorage;
    }

    public JFrame getMainFrame()
    {
        return this.mainFrame;
    }

    public ResourceMap getResourceMap(final String name)
    {
        return getResourceMapRoot().getChild(name);
    }

    public ResourceMap getResourceMapRoot()
    {
        return this.resourceMapRoot;
    }

    public TaskManager getTaskManager()
    {
        return this.taskManager;
    }

    /**
     * ID des angemeldeten Users.<br>
     * Wurde keine ID gesetzt wird, der Wert der SystemProperty "user.name" geliefert.
     */
    public String getUserID()
    {
        if (this.userID == null)
        {
            this.userID = System.getProperty("user.name").toUpperCase();
        }

        return this.userID;
    }

    public void setMainFrame(final JFrame mainFrame)
    {
        this.mainFrame = Objects.requireNonNull(mainFrame, "mainFrame required");
    }

    public void setUserID(final String userID)
    {
        this.userID = userID;
    }

    void setResourceMapRoot(final ResourceMap resourceMapRoot)
    {
        this.resourceMapRoot = resourceMapRoot;
    }
}
