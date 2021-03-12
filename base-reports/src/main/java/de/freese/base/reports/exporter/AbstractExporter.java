/**
 *
 */
package de.freese.base.reports.exporter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.springframework.core.io.ResourceLoader;
import de.freese.base.core.progress.ProgressCallback;

/**
 * Basisklasse eines CSV-Exporters.
 *
 * @author Thomas Freese
 */
public abstract class AbstractExporter implements IExporter
{
    /**
     * Zum laden von Icons, Dateien etc...
     */
    private ResourceLoader resourceLoader;

    /**
     * @see de.freese.base.reports.exporter.IExporter#export(java.lang.String, de.freese.base.core.progress.ProgressCallback, java.lang.Object)
     */
    @Override
    public void export(final String fileName, final ProgressCallback progressCallback, final Object model) throws Exception
    {
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileName)))
        {
            export(outputStream, progressCallback, model);
        }
    }

    /**
     * Zum laden von Icons, Dateien etc...
     *
     * @return {@link ResourceLoader}
     */
    protected ResourceLoader getResourceLoader()
    {
        return this.resourceLoader;
    }

    /**
     * @see de.freese.base.reports.exporter.IExporter#setResourceLoader(org.springframework.core.io.ResourceLoader)
     */
    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }
}
