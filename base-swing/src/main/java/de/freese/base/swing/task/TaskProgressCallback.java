/**
 * 
 */
package de.freese.base.swing.task;

import java.util.concurrent.TimeUnit;

import de.freese.base.core.progress.ProgressCallback;

/**
 * Implementierung des {@link ProgressCallback} Interfaces fuer den {@link AbstractTask}.
 * 
 * @author Thomas Freese
 */
public class TaskProgressCallback implements ProgressCallback
{
	/**
	 * 
	 */
	private final AbstractTask<?, ?> task;

	/**
	 * Erstellt ein neues {@link TaskProgressCallback} Objekt.
	 * 
	 * @param task {@link AbstractTask}
	 */
	public TaskProgressCallback(final AbstractTask<?, ?> task)
	{
		super();

		this.task = task;
	}

	/**
	 * @see de.freese.base.core.progress.ProgressCallback#setProgress(float)
	 */
	@Override
	public void setProgress(final float percentage)
	{
		sleep();
		this.task.setProgress(percentage);
	}

	/**
	 * @see de.freese.base.core.progress.ProgressCallback#setProgress(long, long)
	 */
	@Override
	public void setProgress(final long value, final long max)
	{
		sleep();
		this.task.setProgress(value, 0, max);
	}

	/**
	 * Etwas auf die Bremse treten für eine flüssigere ProgressBar.
	 */
	private void sleep()
	{
		try
		{
			TimeUnit.MILLISECONDS.sleep(100);
		}
		catch (Exception ex)
		{
			// NOOP
		}
	}
}
