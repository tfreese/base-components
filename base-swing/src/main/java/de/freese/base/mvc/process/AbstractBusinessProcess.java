package de.freese.base.mvc.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BasisImplementierung eines BusinessProcesses.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractBusinessProcess implements BusinessProcess
{
	/**
	 *
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Erstellt ein neues {@link AbstractBusinessProcess} Object.
	 */
	public AbstractBusinessProcess()
	{
		super();
	}

	/**
	 * @return {@link Logger}
	 */
	protected Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * @see de.freese.base.core.model.Initializeable#initialize()
	 */
	@Override
	public void initialize()
	{
		// Empty
	}

	/**
	 * @see de.freese.base.core.release.ReleasePrepareable#prepareRelease()
	 */
	@Override
	public void prepareRelease() throws IllegalStateException
	{
		// Empty
	}

	/**
	 * @see de.freese.base.mvc.process.BusinessProcess#release()
	 */
	@Override
	public void release()
	{
		// Empty
	}

	/**
	 * @see de.freese.base.mvc.process.BusinessProcess#reload()
	 */
	@Override
	public void reload()
	{
		// Empty
	}
}
