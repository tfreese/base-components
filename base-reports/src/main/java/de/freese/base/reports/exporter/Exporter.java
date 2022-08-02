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
     * @param outputStream {@link OutputStream}
     * @param progressCallback {@link ProgressCallback}, optional
     * @param model Object
     *
     * @throws Exception Falls was schiefgeht.
     */
    void export(OutputStream outputStream, ProgressCallback progressCallback, Object model) throws Exception;

    /**
     * Erzeugt das Dokument und schreibt es in die Datei.
     *
     * @param fileName {@link String}
     * @param progressCallback {@link ProgressCallback}, optional
     * @param model Object
     *
     * @throws Exception Falls was schiefgeht.
     */
    void export(String fileName, ProgressCallback progressCallback, Object model) throws Exception;

    /**
     * Zum laden von Icons, Dateien etc...
     *
     * @param resourceLoader {@link ResourceLoader}
     */
    void setResourceLoader(final ResourceLoader resourceLoader);
}
