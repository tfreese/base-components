package de.freese.base.core.io;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * OutputStream der seinen Inhalt in eine {@link JTextComponent} schreibt, optional auch in einen weiteren Stream.
 *
 * @author Thomas Freese
 */
public class TextComponentOutputStream extends OutputStream
{
    /**
     *
     */
    private final byte[] littleBuffer = new byte[1];

    /**
     *
     */
    private OutputStream out;

    /**
     *
     */
    private JTextComponent textComponent;

    /**
     * Creates a new {@link TextComponentOutputStream} object.
     *
     * @param textComponent {@link JTextComponent}
     */
    public TextComponentOutputStream(final JTextComponent textComponent)
    {
        super();

        this.textComponent = textComponent;
    }

    /**
     * Creates a new {@link TextComponentOutputStream} object.
     *
     * @param textArea {@link JTextComponent}
     * @param out {@link OutputStream}, Delegate
     */
    public TextComponentOutputStream(final JTextComponent textArea, final OutputStream out)
    {
        this(textArea);

        this.out = out;
    }

    /**
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException
    {
        this.textComponent = null;
        this.out = null;
    }

    /**
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException
    {
        // NOOP
    }

    /**
     * Schreibt den Text in die JTextComponent.
     *
     * @param text String
     */
    private void updateComponent(final String text)
    {
        Runnable runnable = () -> {
            JTextComponent tc = this.textComponent;
            Document document = tc.getDocument();

            try
            {
                document.insertString(document.getLength(), text, null);

                // Zum letzten Zeichen springen
                tc.setCaretPosition(document.getLength());

                // Max. 500 Zeilen zulassen
                int idealSize = 1000;
                int maxExcess = 500;
                int excess = document.getLength() - idealSize;

                if (excess >= maxExcess)
                {
                    try
                    {
                        if (document instanceof AbstractDocument)
                        {
                            ((AbstractDocument) document).replace(0, excess, text, null);
                        }
                        else
                        {
                            document.remove(0, excess);
                            document.insertString(0, text, null);
                        }
                    }
                    catch (BadLocationException ex)
                    {
                        throw new IllegalArgumentException(ex.getMessage());
                    }
                }
            }
            catch (Exception ex)
            {
                // Ignore
            }
        };

        SwingUtilities.invokeLater(runnable);
    }

    /**
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(final byte[] b) throws IOException
    {
        write(b, 0, b.length);
    }

    /**
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException
    {
        String s = new String(b, off, len);

        if (this.out != null)
        {
            this.out.write(b, off, len);
        }

        updateComponent(s);
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(final int b) throws IOException
    {
        this.littleBuffer[0] = (byte) b;

        write(this.littleBuffer);
    }
}
