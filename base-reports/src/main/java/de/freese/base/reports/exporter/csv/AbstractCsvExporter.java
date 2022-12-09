package de.freese.base.reports.exporter.csv;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.function.BiConsumer;

import de.freese.base.core.io.WindowsPrintWriter;
import de.freese.base.core.progress.ProgressCallback;
import de.freese.base.reports.exporter.AbstractExporter;

/**
 * Basisklasse eines CSV-Exporters.
 *
 * @author Thomas Freese
 */
public abstract class AbstractCsvExporter<T> extends AbstractExporter<T>
{
    protected AbstractCsvExporter()
    {
        super();
    }

    @Override
    public void export(final OutputStream outputStream, final BiConsumer<Long, Long> progressCallback, final T model) throws Exception
    {
        try (PrintWriter pw = new WindowsPrintWriter(outputStream, true, StandardCharsets.UTF_8))
        {
            export(pw, progressCallback, model);
        }
    }

    /**
     * Erzeugt das Dokument und schreibt es in den {@link PrintWriter}.
     *
     * @param progressCallback {@link ProgressCallback}, optional
     */
    public abstract void export(PrintWriter pw, BiConsumer<Long, Long> progressCallback, Object model) throws Exception;

    /**
     * Erzeugt das Dokument und schreibt es in den {@link StringBuffer}.
     *
     * @param progressCallback {@link ProgressCallback}, optional
     */
    public StringBuffer export(final BiConsumer<Long, Long> progressCallback, final Object model) throws Exception
    {
        StringWriter sw = new StringWriter();
        BufferedWriter bw = new BufferedWriter(sw);

        try (PrintWriter pw = new WindowsPrintWriter(bw, true))
        {
            export(pw, progressCallback, model);
        }

        return sw.getBuffer();
    }
}
