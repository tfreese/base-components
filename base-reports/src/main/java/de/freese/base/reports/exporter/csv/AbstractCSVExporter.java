/**
 *
 */
package de.freese.base.reports.exporter.csv;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import de.freese.base.core.io.WindowsPrintWriter;
import de.freese.base.core.progress.ProgressCallback;
import de.freese.base.reports.exporter.AbstractExporter;

/**
 * Basisklasse eines CSV-Exporters.
 *
 * @author Thomas Freese
 */
public abstract class AbstractCSVExporter extends AbstractExporter
{
    /**
     * Erstellt ein neues {@link AbstractCSVExporter} Objekt.
     */
    public AbstractCSVExporter()
    {
        super();
    }

    /**
     * @see de.freese.base.reports.exporter.IExporter#export(java.io.OutputStream, de.freese.base.core.progress.ProgressCallback, java.lang.Object)
     */
    @Override
    public void export(final OutputStream outputStream, final ProgressCallback progressCallback, final Object model) throws Exception
    {
        try (PrintWriter pw = new WindowsPrintWriter(outputStream))
        {
            export(pw, progressCallback, model);
        }
    }

    /**
     * Erzeugt das Dokument und schreibt es in den {@link PrintWriter}.
     * 
     * @param pw {@link PrintWriter}
     * @param progressCallback {@link ProgressCallback}, optional
     * @param model Object
     * @throws Exception Falls was schief geht.
     */
    public abstract void export(PrintWriter pw, ProgressCallback progressCallback, Object model) throws Exception;

    /**
     * Erzeugt das Dokument und schreibt es in den {@link StringBuffer}.
     * 
     * @param progressCallback {@link ProgressCallback}, optional
     * @param model Object
     * @return {@link StringBuffer}
     * @throws Exception Falls was schief geht.
     */
    public StringBuffer export(final ProgressCallback progressCallback, final Object model) throws Exception
    {
        StringWriter sw = new StringWriter();
        BufferedWriter bw = new BufferedWriter(sw);

        try (PrintWriter pw = new WindowsPrintWriter(bw))
        {
            export(pw, progressCallback, model);
        }

        return sw.getBuffer();
    }
}
