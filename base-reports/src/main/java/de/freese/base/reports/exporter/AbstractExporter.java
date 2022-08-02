package de.freese.base.reports.exporter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import de.freese.base.core.progress.ProgressCallback;
import org.springframework.core.io.ResourceLoader;

/**
 * Basisklasse eines CSV-Exporters.
 *
 * @author Thomas Freese
 */
public abstract class AbstractExporter implements Exporter
{
    /**
     * Zum laden von Icons, Dateien etc...
     */
    private ResourceLoader resourceLoader;

    /**
     * @see Exporter#export(java.lang.String, de.freese.base.core.progress.ProgressCallback, java.lang.Object)
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
     * @see Exporter#setResourceLoader(org.springframework.core.io.ResourceLoader)
     */
    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
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
}
