package de.freese.base.reports.exporter;

import java.io.OutputStream;

import de.freese.base.core.progress.ProgressCallback;
import de.freese.base.reports.exporter.csv.AbstractCSVExporter;
import de.freese.base.reports.exporter.pdf.AbstractPDFExporter;
import org.springframework.core.io.ResourceLoader;

/**
 * Interface für einen Exporter.
 *
 * @author Thomas Freese
 * @see AbstractPDFExporter
 * @see AbstractCSVExporter
 */
public interface Exporter
{
    /**
     * Erzeugt das Dokument und schreibt es in den Stream.
     *
     * @param progressCallback {@link ProgressCallback}, optional
     */
    void export(OutputStream outputStream, ProgressCallback progressCallback, Object model) throws Exception;

    /**
     * Erzeugt das Dokument und schreibt es in die Datei.
     *
     * @param progressCallback {@link ProgressCallback}, optional
     */
    void export(String fileName, ProgressCallback progressCallback, Object model) throws Exception;

    /**
     * Zum laden von Icons, Dateien etc...
     */
    void setResourceLoader(final ResourceLoader resourceLoader);
}
