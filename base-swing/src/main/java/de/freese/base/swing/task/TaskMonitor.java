package de.freese.base.swing.task;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * Diese Klasse reagiert auf Events des {@link TaskService} und steuert die InputBlocker.
 * 
 * @author Thomas Freese
 */
public final class TaskMonitor
{
	// /**
	// *
	// */
	// private static final Logger LOGGER = LoggerFactory.getLogger(TaskMonitor.class);

	/**
	 * PropertyChangeListener des aktuell ausgefuehrten Tasks (ForegroundTask).
	 * 
	 * @author Thomas Freese
	 */
	private class TaskPCL implements PropertyChangeListener
	{
		/**
		 * Erstellt ein neues {@link TaskPCL} Object.
		 */
		public TaskPCL()
		{
			super();
		}

		/**
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		@Override
		public void propertyChange(final PropertyChangeEvent evt)
		{
			String propertyName = evt.getPropertyName();

			AbstractTask<?, ?> task = (AbstractTask<?, ?>) (evt.getSource());

			if ((task != null) && (task == getForegroundTask()))
			{
				firePropertyChange(evt);

				if ("state".equals(propertyName))
				{
					SwingWorker.StateValue newState = (SwingWorker.StateValue) evt.getNewValue();

					switch (newState)
					{
						case PENDING:
							firePropertyChange(new PropertyChangeEvent(task, "pending",
									Boolean.FALSE, Boolean.TRUE));
							break;
						case STARTED:
							firePropertyChange(new PropertyChangeEvent(task, "started",
									Boolean.FALSE, Boolean.TRUE));
							break;
						case DONE:
							firePropertyChange(new PropertyChangeEvent(task, "done", Boolean.FALSE,
									Boolean.TRUE));
							setForegroundTask(null);
							break;

						default:
							break;
					}
				}
			}
		}
	}

	/**
	 * PropertyChangeListener des {@link TaskService}es.
	 * 
	 * @author Thomas Freese
	 */
	private class TaskServicePCL implements PropertyChangeListener
	{
		/**
		 * Erstellt ein neues {@link TaskServicePCL} Object.
		 */
		public TaskServicePCL()
		{
			super();
		}

		/**
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
		 */
		@Override
		@SuppressWarnings("unchecked")
		public void propertyChange(final PropertyChangeEvent evt)
		{
			String propertyName = evt.getPropertyName();
			// System.out.println("TaskMonitor.TaskServicePCL.propertyChange(): " + propertyName);

			if ("tasks".equals(propertyName))
			{
				List<AbstractTask<?, ?>> oldList = (List<AbstractTask<?, ?>>) evt.getOldValue();
				List<AbstractTask<?, ?>> newList = (List<AbstractTask<?, ?>>) evt.getNewValue();

				updateTasks(oldList, newList);
			}
		}
	}

	/**
	 * 
	 */
	private AbstractTask<?, ?> foregroundTask = null;

	/**
	 * 
	 */
	private final PropertyChangeSupport propertyChangeSupport;

	/**
	 * 
	 */
	private final PropertyChangeListener taskPCL;

	/**
	 * 
	 */
	private final PropertyChangeListener taskServicePCL;

	/**
	 * Erstellt ein neues {@link TaskMonitor} Object.
	 * 
	 * @param taskService {@link TaskService}
	 */
	public TaskMonitor(final TaskService taskService)
	{
		super();

		if (taskService == null)
		{
			throw new NullPointerException("taskService");
		}

		this.taskPCL = new TaskPCL();
		this.taskServicePCL = new TaskServicePCL();
		taskService.addPropertyChangeListener(this.taskServicePCL);

		this.propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
	}

	/**
	 * Hinzufuegen eines PropertyChangeListeners.
	 * 
	 * @param listener {@link PropertyChangeListener}
	 */
	public void addPropertyChangeListener(final PropertyChangeListener listener)
	{
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Feuert ein PropertyChangeEvent.
	 * 
	 * @param event {@link PropertyChangeEvent}
	 */
	private void firePropertyChange(final PropertyChangeEvent event)
	{
		this.propertyChangeSupport.firePropertyChange(event);
	}

	/**
	 * Feuert ein PropertyChangeEvent.
	 * 
	 * @param propertyName String
	 * @param oldValue Object
	 * @param newValue Object
	 */
	private void firePropertyChange(final String propertyName, final Object oldValue,
									final Object newValue)
	{
		this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Liefert den ersten der ausgefuehrenden Tasks oder null.
	 * 
	 * @return {@link AbstractTask}
	 */
	public AbstractTask<?, ?> getForegroundTask()
	{
		return this.foregroundTask;
	}

	/**
	 * Entfernen eines PropertyChangeListeners.
	 * 
	 * @param listener {@link PropertyChangeListener}
	 */
	public void removePropertyChangeListener(final PropertyChangeListener listener)
	{
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Setzt den ersten der ausgefuehrenden Tasks und steuert den InputBlocker.
	 * 
	 * @param foregroundTask {@link AbstractTask}
	 */
	private void setForegroundTask(final AbstractTask<?, ?> foregroundTask)
	{
		if (this.foregroundTask == foregroundTask)
		{
			return;
		}

		AbstractTask<?, ?> oldTask = this.foregroundTask;

		if (oldTask != null)
		{
			oldTask.removePropertyChangeListener(this.taskPCL);

			if (oldTask.getInputBlocker() != null)
			{
				oldTask.getInputBlocker().unblock();
			}
		}

		this.foregroundTask = foregroundTask;
		AbstractTask<?, ?> newTask = this.foregroundTask;

		if (newTask != null)
		{
			newTask.addPropertyChangeListener(this.taskPCL);

			if (newTask.getInputBlocker() != null)
			{
				newTask.getInputBlocker().block();
			}
		}

		firePropertyChange("foregroundTask", oldTask, newTask);
	}

	/**
	 * Verarbeiten der alten und neuen TaskListe.
	 * 
	 * @param oldTasks {@link List}
	 * @param newTasks {@link List}
	 */
	private void updateTasks(final List<AbstractTask<?, ?>> oldTasks,
								final List<AbstractTask<?, ?>> newTasks)
	{
		if (newTasks.isEmpty())
		{
			setForegroundTask(null);
		}
		else
		{
			setForegroundTask(newTasks.get(0));
		}

		// System.out.println(oldTasks + " --- " + newTasks);

		firePropertyChange("tasks", oldTasks, newTasks);
	}
}
