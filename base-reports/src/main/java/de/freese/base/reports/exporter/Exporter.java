package de.freese.base.reports.exporter;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;

import de.freese.base.core.progress.ProgressCallback;
import de.freese.base.reports.exporter.csv.AbstractCsvExporter;
import de.freese.base.reports.exporter.pdf.AbstractPdfExporter;
import org.springframework.core.io.ResourceLoader;

/**
 * Interface für einen Exporter.
 *
 * @author Thomas Freese
 * @see AbstractPdfExporter
 * @see AbstractCsvExporter
 */
public interface Exporter<T>
{
    /**
     * Erzeugt das Dokument und schreibt es in den Stream.
     *
     * @param progressCallback {@link ProgressCallback}, optional
     */
    void export(OutputStream outputStream, BiConsumer<Long, Long> progressCallback, T model) throws Exception;

    /**
     * Erzeugt das Dokument und schreibt es in die Datei.
     *
     * @param progressCallback {@link ProgressCallback}, optional
     */
    default void export(Path filePath, BiConsumer<Long, Long> progressCallback, T model) throws Exception
    {
        try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(filePath)))
        {
            export(outputStream, progressCallback, model);
        }
    }

    /**
     * Zum laden von Icons, Dateien etc...
     */
    void setResourceLoader(final ResourceLoader resourceLoader);
}
