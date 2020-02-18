package de.freese.base.core.concurrent;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

/**
 * Zeitgesteuerter {@link AccumulativeRunnable}, der nach einer Zeitspanne die gesammelten Daten
 * ausführt.
 * 
 * @author Thomas Freese
 * @param <T> Konkreter Typ des Objektes.
 */
public abstract class AbstractScheduledAccumulativeRunnable<T> extends AccumulativeRunnable<T>
{
	/**
	 * 
	 */
	private final int delay;

	/**
	 * 
	 */
	private final ScheduledExecutorService scheduledExecutor;

	/**
	 * Erstellt ein neues {@link AbstractScheduledAccumulativeRunnable} Object.
	 * 
	 * @param delay int
	 */
	public AbstractScheduledAccumulativeRunnable(final int delay)
	{
		this(delay, null);
	}

	/**
	 * Erstellt ein neues {@link AbstractScheduledAccumulativeRunnable} Object.
	 * 
	 * @param delay int
	 * @param scheduledExecutor {@link ScheduledExecutorService}
	 */
	public AbstractScheduledAccumulativeRunnable(final int delay,
			final ScheduledExecutorService scheduledExecutor)
	{
		super();

		this.delay = delay;
		this.scheduledExecutor = scheduledExecutor;
	}

	/**
	 * @see de.freese.base.core.concurrent.AccumulativeRunnable#submit()
	 */
	@Override
	protected final void submit()
	{
		if (this.scheduledExecutor != null)
		{
			this.scheduledExecutor.schedule(this, this.delay, TimeUnit.MILLISECONDS);

			// LoggerFactory.getLogger(getClass()).info("ActiveCount: {}",
			// ((ScheduledThreadPoolExecutor) this.scheduledExecutor).getActiveCount());
			// LoggerFactory.getLogger(getClass()).info("PoolSize: {}",
			// ((ScheduledThreadPoolExecutor) this.scheduledExecutor).getPoolSize());
		}
		else
		{
			Timer timer = new Timer(this.delay, this);
			timer.setRepeats(false);
			timer.start();
		}
	}
}
