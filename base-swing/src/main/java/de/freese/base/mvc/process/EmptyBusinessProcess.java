package de.freese.base.mvc.process;

/**
 * Leere dummy Implementierung.
 * 
 * @author Thomas Freese
 */
public class EmptyBusinessProcess implements BusinessProcess
{
	/**
	 * Erstellt ein neues {@link EmptyBusinessProcess} Object.
	 */
	public EmptyBusinessProcess()
	{
		super();
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
	 * @see de.freese.base.core.release.Releaseable#release()
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
