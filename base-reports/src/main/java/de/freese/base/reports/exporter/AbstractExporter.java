package de.freese.base.reports.exporter;

import org.springframework.core.io.ResourceLoader;

/**
 * Basisklasse eines CSV-Exporters.
 *
 * @author Thomas Freese
 */
public abstract class AbstractExporter<T> implements Exporter<T>
{
    private ResourceLoader resourceLoader;

    /**
     * @see Exporter#setResourceLoader(org.springframework.core.io.ResourceLoader)
     */
    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    protected ResourceLoader getResourceLoader()
    {
        return this.resourceLoader;
    }
}
