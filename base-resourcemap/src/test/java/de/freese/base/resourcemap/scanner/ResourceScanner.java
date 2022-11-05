package de.freese.base.resourcemap.scanner;

import java.util.Set;

/**
 * Interface für einen Scanner nach bestimmten Ressourcen im ClassPath.
 *
 * @author Thomas Freese
 */
public interface ResourceScanner
{
    /**
     * @param basePath String; Format: app/module/view
     */
    Set<String> scanResources(String basePath);
}
