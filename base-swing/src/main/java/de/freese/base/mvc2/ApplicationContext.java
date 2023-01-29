package de.freese.base.mvc2;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.swing.JFrame;

import de.freese.base.mvc.guistate.GuiStateManager;
import de.freese.base.mvc.storage.LocalStorage;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.exception.DialogExceptionHandler;
import de.freese.base.swing.exception.SwingExceptionHandler;
import de.freese.base.swing.task.TaskManager;

/**
 * @author Thomas Freese
 */
public final class ApplicationContext
{
    public static final class Builder
    {
        private final Map<Class<?>, Object> map = new HashMap<>();

        private Builder()
        {
            super();
        }

        ApplicationContext build()
        {
            ApplicationContext context = new ApplicationContext();

            // ExecutorService
            ExecutorService executorService = getValue(ExecutorService.class, () -> new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>()));
            context.setExecutorService(executorService);

            // ExceptionHandler
            SwingExceptionHandler exceptionHandler = getValue(SwingExceptionHandler.class, () -> new DialogExceptionHandler());
            context.setExceptionHandler(exceptionHandler);

            // Clipboard
            Clipboard clipboard = getValue(Clipboard.class, () ->
            {
                try
                {
                    return Toolkit.getDefaultToolkit().getSystemClipboard();
                }
                catch (Throwable th)
                {
                    return new Clipboard("sandbox");
                }
            });
            context.setClipboard(clipboard);

            // GuiStateManager
            GuiStateManager guiStateManager = getValue(GuiStateManager.class, () -> new GuiStateManager());
            context.setClipboard(clipboard);

            // LocalStorage
            LocalStorage localStorage = getValue(LocalStorage.class, () -> new LocalStorage());
            context.setLocalStorage(localStorage);

            // TaskManager
            TaskManager taskManager = getValue(TaskManager.class, () -> new TaskManager(executorService));
            context.setTaskManager(taskManager);

            return context;
        }

        Builder clipboard(Clipboard clipboard)
        {
            putValue(Clipboard.class, clipboard);

            return this;
        }

        Builder exceptionHandler(SwingExceptionHandler exceptionHandler)
        {
            putValue(SwingExceptionHandler.class, exceptionHandler);

            return this;
        }

        Builder executorService(ExecutorService executorService)
        {
            putValue(ExecutorService.class, executorService);

            return this;
        }

        Builder guiStateManager(GuiStateManager guiStateManager)
        {
            putValue(GuiStateManager.class, guiStateManager);

            return this;
        }

        Builder localStorage(LocalStorage localStorage)
        {
            putValue(LocalStorage.class, localStorage);

            return this;
        }

        Builder taskManager(TaskManager taskManager)
        {
            putValue(TaskManager.class, taskManager);

            return this;
        }

        private <T> T getValue(Class<T> clazz, Supplier<T> defaultValue)
        {
            return clazz.cast(map.computeIfAbsent(clazz, key -> defaultValue.get()));
        }

        private <T> void putValue(Class<T> clazz, T value)
        {
            map.put(clazz, value);
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    private Clipboard clipboard;
    private SwingExceptionHandler exceptionHandler;
    private ExecutorService executorService;
    private GuiStateManager guiStateManager;
    private LocalStorage localStorage;
    private JFrame mainFrame;
    private ResourceMap resourceMapRoot;
    private TaskManager taskManager;
    private String userID;

    private ApplicationContext()
    {
        super();
    }

    public Clipboard getClipboard()
    {
        return this.clipboard;
    }

    public SwingExceptionHandler getExceptionHandler()
    {
        return exceptionHandler;
    }

    public ExecutorService getExecutorService()
    {
        return executorService;
    }

    public GuiStateManager getGuiStateManager()
    {
        return guiStateManager;
    }

    public LocalStorage getLocalStorage()
    {
        return localStorage;
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
        return taskManager;
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

    private void setClipboard(final Clipboard clipboard)
    {
        this.clipboard = clipboard;
    }

    private void setExceptionHandler(final SwingExceptionHandler exceptionHandler)
    {
        this.exceptionHandler = exceptionHandler;
    }

    private void setExecutorService(final ExecutorService executorService)
    {
        this.executorService = executorService;
    }

    private void setGuiStateManager(final GuiStateManager guiStateManager)
    {
        this.guiStateManager = guiStateManager;
    }

    private void setLocalStorage(final LocalStorage localStorage)
    {
        this.localStorage = localStorage;
    }

    private void setTaskManager(final TaskManager taskManager)
    {
        this.taskManager = taskManager;
    }
}
