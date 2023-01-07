package de.freese.base.reports.exporter;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Thomas Freese
 */
public interface Exporter<T>
{
    void export(OutputStream outputStream, T model) throws Exception;

    default void export(Path filePath, T model) throws Exception
    {
        try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(filePath)))
        {
            export(outputStream, model);

            outputStream.flush();
        }
    }
}
