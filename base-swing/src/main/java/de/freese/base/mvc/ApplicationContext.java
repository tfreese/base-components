package de.freese.base.mvc;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.HashMap;
import java.util.Map;
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
    /**
     *
     */
    private Clipboard clipboard;
    /**
     *
     */
    private final SwingExceptionHandler exceptionHandler;
    /**
    *
    */
    private final ExecutorService executorService;
    /**
     *
     */
    private final GuiStateManager guiStateManager;
    /**
     *
     */
    private final LocalStorage localStorage;
    /**
    *
    */
    private JFrame mainFrame;
    /**
    *
    */
    private ResourceMap resourceMapRoot;
    /**
     *
     */
    private final Map<String, ResourceMap> resourceMaps = new HashMap<>();
    /**
     *
     */
    private final TaskManager taskManager;
    /**
     *
     */
    private String userID;

    /**
     * Erstellt ein neues {@link ApplicationContext} Object.
     */
    public ApplicationContext()
    {
        // this(Executors.newCachedThreadPool());
        this(new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>()));
    }

    /**
     * Erstellt ein neues {@link ApplicationContext} Object.
     *
     * @param executorService ExecutorService
     */
    public ApplicationContext(final ExecutorService executorService)
    {
        super();

        this.executorService = Objects.requireNonNull(executorService, "executorService required");
        this.taskManager = new TaskManager(executorService);
        this.localStorage = new LocalStorage();
        this.guiStateManager = new GuiStateManager();
        this.exceptionHandler = new DialogExceptionHandler();
    }

    /**
     * Hinzufügen einer ResourceMap.
     *
     * @param name String
     * @param resourceMap {@link ResourceMap}
     */
    public void addResourceMap(final String name, final ResourceMap resourceMap)
    {
        this.resourceMaps.put(name, resourceMap);
    }

    /**
     * @return {@link Clipboard}
     */
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

    /**
     * @return {@link SwingExceptionHandler}
     */
    public SwingExceptionHandler getExceptionHandler()
    {
        return this.exceptionHandler;
    }

    /**
     * @return {@link ExecutorService}
     */
    public ExecutorService getExecutorService()
    {
        return this.executorService;
    }

    /**
     * @return {@link GuiStateManager}
     */
    public GuiStateManager getGuiStateManager()
    {
        return this.guiStateManager;
    }

    /**
     * @return {@link LocalStorage}
     */
    public LocalStorage getLocalStorage()
    {
        return this.localStorage;
    }

    /**
     * @return {@link JFrame}
     */
    public JFrame getMainFrame()
    {
        return this.mainFrame;
    }

    /**
     * Liefert eine ResourceMap.
     *
     * @param name String
     *
     * @return IResourceMap
     */
    public ResourceMap getResourceMap(final String name)
    {
        return this.resourceMaps.get(name);
    }

    /**
     * Liefert die Root-{@link ResourceMap} der Application.
     *
     * @return {@link ResourceMap}
     */
    public ResourceMap getResourceMapRoot()
    {
        return this.resourceMapRoot;
    }

    /**
     * @return {@link TaskManager}
     */
    public TaskManager getTaskManager()
    {
        return this.taskManager;
    }

    /**
     * ID des angemeldeten Users.<br>
     * Wurde keine ID gesetzt wird, der Wert der Systemproperty "user.name" geliefert.
     *
     * @return String
     */
    public String getUserID()
    {
        if (this.userID == null)
        {
            this.userID = System.getProperty("user.name").toUpperCase();
        }

        return this.userID;
    }

    /**
     * @param mainFrame {@link JFrame}
     */
    public void setMainFrame(final JFrame mainFrame)
    {
        this.mainFrame = Objects.requireNonNull(mainFrame, "mainFrame required");
    }

    /**
     * Setzt die Root-{@link ResourceMap} der Application.
     *
     * @param resourceMapRoot {@link ResourceMap}
     */
    void setResourceMapRoot(final ResourceMap resourceMapRoot)
    {
        this.resourceMapRoot = resourceMapRoot;
    }

    /**
     * ID des angemeldeten Users.
     *
     * @param userID String
     */
    public void setUserID(final String userID)
    {
        this.userID = userID;
    }
}
