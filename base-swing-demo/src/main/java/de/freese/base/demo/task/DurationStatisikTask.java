package de.freese.base.demo.task;

import de.freese.base.swing.task.AbstractTask;
import de.freese.base.swing.task.DurationStatisikTaskListener;

/**
 * Task der ueber den {@link DurationStatisikTaskListener} seine Ausfuehrungsdauer protokolliert und
 * durch diesen seinen Progresswert zyklisch setzt.
 * 
 * @author Thomas Freese
 */
public class DurationStatisikTask extends AbstractTask<Void, Void>
{
	/**
	 * Erstellt ein neues {@link DurationStatisikTask} Object.
	 */
	public DurationStatisikTask()
	{
		super();

		setTitle("TaskStatistik");
	}

	/**
	 * @see javax.swing.SwingWorker#doInBackground()
	 */
	@Override
	protected Void doInBackground() throws Exception
	{
		getLogger().info("Started");

		for (int i = 0; i < 50; i++)
		{
			Thread.sleep(100);
		}

		getLogger().info("Finished");

		return null;
	}
}
