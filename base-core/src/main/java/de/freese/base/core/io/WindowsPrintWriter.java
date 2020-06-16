package de.freese.base.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * {@link PrintWriter} mit festcodiertem LineSeparator (println) fuer Windows.
 *
 * @author Thomas Freese
 */
public class WindowsPrintWriter extends PrintWriter
{
    /**
     *
     */
    private static final String LINE_SEPARATOR = "\r\n";

    /**
     *
     */
    private boolean autoFlush = false;

    /**
     * Erstellt ein neues {@link WindowsPrintWriter} Object.
     * 
     * @param file {@link File}
     * @throws FileNotFoundException Falls was schief geht.
     */
    public WindowsPrintWriter(final File file) throws FileNotFoundException
    {
        super(file);
    }

    /**
     * Erstellt ein neues {@link WindowsPrintWriter} Object.
     * 
     * @param file {@link File}
     * @param csn String
     * @throws FileNotFoundException Falls was schief geht.
     * @throws UnsupportedEncodingException Falls was schief geht.
     */
    public WindowsPrintWriter(final File file, final String csn) throws FileNotFoundException, UnsupportedEncodingException
    {
        super(file, csn);
    }

    /**
     * Erstellt ein neues {@link WindowsPrintWriter} Object.
     * 
     * @param out {@link OutputStream}
     */
    public WindowsPrintWriter(final OutputStream out)
    {
        super(out);
    }

    /**
     * Erstellt ein neues {@link WindowsPrintWriter} Object.
     * 
     * @param out {@link OutputStream}
     * @param autoFlush boolean
     */
    public WindowsPrintWriter(final OutputStream out, final boolean autoFlush)
    {
        super(out, autoFlush);

        this.autoFlush = autoFlush;
    }

    /**
     * Erstellt ein neues {@link WindowsPrintWriter} Object.
     * 
     * @param fileName {@link String}
     * @throws FileNotFoundException Falls was schief geht.
     */
    public WindowsPrintWriter(final String fileName) throws FileNotFoundException
    {
        super(fileName);
    }

    /**
     * Erstellt ein neues {@link WindowsPrintWriter} Object.
     * 
     * @param fileName String
     * @param csn String
     * @throws FileNotFoundException Falls was schief geht.
     * @throws UnsupportedEncodingException Falls was schief geht.
     */
    public WindowsPrintWriter(final String fileName, final String csn) throws FileNotFoundException, UnsupportedEncodingException
    {
        super(fileName, csn);
    }

    /**
     * Erstellt ein neues {@link WindowsPrintWriter} Object.
     * 
     * @param out {@link Writer}
     */
    public WindowsPrintWriter(final Writer out)
    {
        super(out);
    }

    /**
     * Erstellt ein neues {@link WindowsPrintWriter} Object.
     * 
     * @param out {@link Writer}
     * @param autoFlush boolean
     */
    public WindowsPrintWriter(final Writer out, final boolean autoFlush)
    {
        super(out, autoFlush);

        this.autoFlush = autoFlush;
    }

    /**
     * Implementierung entspricht PrintWriter.newLine Methode.
     * 
     * @see java.io.PrintWriter#println()
     */
    @Override
    public void println()
    {
        synchronized (this.lock)
        {
            try
            {
                if (this.out == null)
                {
                    throw new IOException("Stream closed");
                }

                this.out.write(LINE_SEPARATOR);

                if (this.autoFlush)
                {
                    this.out.flush();
                }
            }
            catch (InterruptedIOException ex)
            {
                Thread.currentThread().interrupt();
            }
            catch (IOException ex)
            {
                setError();
            }
        }
    }
}
