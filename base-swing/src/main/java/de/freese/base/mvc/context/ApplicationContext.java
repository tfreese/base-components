package de.freese.base.mvc.context;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.HashMap;
import java.util.Map;
import de.freese.base.mvc.context.guistate.GuiStateManager;
import de.freese.base.mvc.context.storage.LocalStorage;
import de.freese.base.resourcemap.ResourceMap;
import de.freese.base.swing.exception.DialogExceptionHandler;
import de.freese.base.swing.exception.SwingExceptionHandler;
import de.freese.base.swing.task.TaskMonitor;
import de.freese.base.swing.task.TaskService;

/**
 * Context fuer eine oder mehrere Anwendungen.
 * 
 * @author Thomas Freese
 */
public final class ApplicationContext
{
	// /**
	// * Konstanten fuer Betriebssysteme.
	// *
	// * @author Thomas Freese
	// */
	// public enum OS
	// {
	// /**
	// *
	// */
	// LINUX,
	// /**
	// *
	// */
	// MAC_OS_X,
	// /**
	// *
	// */
	// WINDOWS_7,
	// /**
	// *
	// */
	// WINDOWS_VISTA,
	// /**
	// *
	// */
	// WINDOWS_XP;
	// }
	//
	// /**
	// * Liefert das Betriebssystem.
	// *
	// * @return {@link OS}
	// */
	// public static final OS getOS()
	// {
	// PrivilegedAction<String> doGetOSName = new PrivilegedAction<String>()
	// {
	// /**
	// * @see java.security.PrivilegedAction#run()
	// */
	// @Override
	// public String run()
	// {
	// return System.getProperty("os.name");
	// }
	// };
	//
	// String osName = AccessController.doPrivileged(doGetOSName);
	//
	// if (osName != null)
	// {
	// osName = osName.toLowerCase();
	// }
	//
	// OS os = null;
	//
	// if (osName != null)
	// {
	// for (OS system : OS.values())
	// {
	// String name = system.name().toLowerCase().replaceAll("_", " ");
	//
	// if (name.equals(osName))
	// {
	// os = system;
	// break;
	// }
	// }
	// }
	//
	// if (os == null)
	// {
	// throw new RuntimeException("operating system can not determined");
	// }
	//
	// return os;
	// }

	/**
	 * 
	 */
	private Clipboard clipboard = null;

	/**
	 * 
	 */
	private final SwingExceptionHandler exceptionHandler;

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
	private final Map<String, ResourceMap> resourceMaps = new HashMap<>();

	/**
	 * 
	 */
	private final TaskMonitor taskMonitor;

	/**
	 * 
	 */
	private final TaskService taskService;

	/**
	 * 
	 */
	private String userID = null;

	/**
	 * Erstellt ein neues {@link ApplicationContext} Object.
	 */
	public ApplicationContext()
	{
		super();

		this.taskService = new TaskService();
		this.taskMonitor = new TaskMonitor(this.taskService);
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
	 * Liefert eine ResourceMap.
	 * 
	 * @param name String
	 * @return IResourceMap
	 */
	public ResourceMap getResourceMap(final String name)
	{
		return this.resourceMaps.get(name);
	}

	/**
	 * @return {@link TaskMonitor}
	 */
	public TaskMonitor getTaskMonitor()
	{
		return this.taskMonitor;
	}

	/**
	 * @return {@link TaskService}
	 */
	public TaskService getTaskService()
	{
		return this.taskService;
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
	 * ID des angemeldeten Users.
	 * 
	 * @param userID String
	 */
	public void setUserID(final String userID)
	{
		this.userID = userID;
	}
}
