package de.freese.base.resourcemap.scanner;

import java.util.Set;

/**
 * Interface fuer einen Scanner nach bestimmten Resourcen im ClassPath.
 * 
 * @author Thomas Freese
 */
public interface ResourceScanner
{
	/**
	 * @param basePath String; Format: app/module/view
	 * @return {@link Set}
	 */
	public Set<String> scanResources(String basePath);
}
