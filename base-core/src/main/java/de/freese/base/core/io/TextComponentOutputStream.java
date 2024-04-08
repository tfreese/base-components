package de.freese.base.core.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

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
public class TextComponentOutputStream extends FilterOutputStream {

    private final byte[] littleBuffer = new byte[1];

    private JTextComponent textComponent;

    public TextComponentOutputStream(final OutputStream outputStream, final JTextComponent textArea) {
        super(outputStream);

        this.textComponent = textArea;
    }

    @Override
    public void close() throws IOException {
        super.close();

        this.textComponent = null;
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        super.write(b, off, len);

        updateComponent(new String(b, off, len, StandardCharsets.UTF_8));
    }

    @Override
    public void write(final int b) throws IOException {
        this.littleBuffer[0] = (byte) b;

        write(this.littleBuffer);
    }

    private void updateComponent(final String text) {
        final Runnable runnable = () -> {
            final JTextComponent tc = this.textComponent;
            final Document document = tc.getDocument();

            try {
                document.insertString(document.getLength(), text, null);

                // Zum letzten Zeichen springen
                tc.setCaretPosition(document.getLength());

                // Max. 500 Zeilen zulassen
                final int idealSize = 1000;
                final int maxExcess = 500;
                final int excess = document.getLength() - idealSize;

                if (excess >= maxExcess) {
                    try {
                        if (document instanceof AbstractDocument ad) {
                            ad.replace(0, excess, text, null);
                        }
                        else {
                            document.remove(0, excess);
                            document.insertString(0, text, null);
                        }
                    }
                    catch (BadLocationException ex) {
                        throw new IllegalArgumentException(ex.getMessage());
                    }
                }
            }
            catch (Exception ex) {
                // Ignore
            }
        };

        SwingUtilities.invokeLater(runnable);
    }
}
