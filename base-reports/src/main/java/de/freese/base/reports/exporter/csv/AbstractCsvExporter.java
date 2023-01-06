package de.freese.base.reports.exporter.csv;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import de.freese.base.core.io.WindowsPrintWriter;
import de.freese.base.reports.exporter.AbstractExporter;

/**
 * @author Thomas Freese
 */
public abstract class AbstractCsvExporter<T> extends AbstractExporter<T>
{
    @Override
    public void export(final OutputStream outputStream, final T model) throws Exception
    {
        try (PrintWriter pw = new WindowsPrintWriter(outputStream, true, StandardCharsets.UTF_8))
        {
            export(pw, model);
        }
    }

    public abstract void export(PrintWriter pw, T model) throws Exception;

    public StringBuffer export(final T model) throws Exception
    {
        StringWriter sw = new StringWriter();
        BufferedWriter bw = new BufferedWriter(sw);

        try (PrintWriter pw = new WindowsPrintWriter(bw, true))
        {
            export(pw, model);
        }

        return sw.getBuffer();
    }
}
