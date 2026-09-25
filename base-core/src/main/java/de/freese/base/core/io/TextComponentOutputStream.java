package de.freese.base.core.io;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

/**
 * OutputStream der seinen Inhalt in eine {@link JTextComponent} schreibt, optional auch in einen weiteren Stream.
 *
 * @author Thomas Freese
 */
public class TextComponentOutputStream extends OutputStream {
    // Optional: Obergrenze, um OutOfMemory bei Dauer-Logging zu verhindern.
    private static final int MAX_CHARS = 500_000;

    static void main() {
        final JTextPane textPane = new JTextPane();
        textPane.setEditable(false);

        // final StyledDocument doc = textPane.getStyledDocument();
        final Style stdoutStyle = textPane.addStyle("stdout", null);
        StyleConstants.setForeground(stdoutStyle, Color.BLACK);

        // Zeichen-Attribute definieren.
        // SimpleAttributeSet bold = new SimpleAttributeSet();
        // StyleConstants.setBold(bold, true);
        // StyleConstants.setForeground(bold, Color.RED);
        // StyleConstants.setFontFamily(bold, "Monospaced");

        final Style stderrStyle = textPane.addStyle("stderr", null);
        StyleConstants.setForeground(stderrStyle, Color.RED);

        final PrintStream outStream = new PrintStream(new TextComponentOutputStream(textPane, stdoutStyle), true, StandardCharsets.UTF_8);
        final PrintStream errStream = new PrintStream(new TextComponentOutputStream(textPane, stderrStyle), true, StandardCharsets.UTF_8);

        System.setOut(outStream);
        System.setErr(errStream);

        final JFrame frame = new JFrame();
        frame.setLayout(new GridLayout(3, 1));
        frame.add(new JButton("outStream") {
            @Override
            protected void fireActionPerformed(final java.awt.event.ActionEvent event) {
                System.out.println("Hello World!");
            }
        });
        frame.add(new JButton("errStream") {
            @Override
            protected void fireActionPerformed(final java.awt.event.ActionEvent event) {
                System.err.println("Hello World!");
            }
        });
        frame.add(textPane);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(200, 200);
        frame.setLocationRelativeTo(null);
        // frame.setFont(frame.getFont().deriveFont(24F));
        frame.setVisible(true);
    }

    private final AttributeSet attributes;
    private final StringBuilder buffer = new StringBuilder(256);
    private final JTextComponent textComponent;

    public TextComponentOutputStream(final JTextComponent textComponent) {
        super();

        this.textComponent = Objects.requireNonNull(textComponent, "textComponent required");
        this.attributes = null;
    }

    public TextComponentOutputStream(final JTextPane textPane, final AttributeSet attributes) {
        super();

        this.textComponent = Objects.requireNonNull(textPane, "textPane required");
        this.attributes = Objects.requireNonNull(attributes, "attributes required");
    }

    @Override
    public synchronized void write(final byte[] b, final int off, final int len) {
        final String chunk = new String(b, off, len, StandardCharsets.UTF_8);
        buffer.append(chunk);

        if (chunk.contains("\n")) {
            flushBuffer();
        }
    }

    @Override
    public synchronized void write(final int b) {
        buffer.append((char) b);

        if (b == '\n') {
            flushBuffer();
        }
    }

    private void appendToDocument(final String text) {
        final Document doc = textComponent.getDocument();

        try {
            doc.insertString(doc.getLength(), text, attributes);
        }
        catch (final BadLocationException ex) {
            // Kann bei korrekt implementiertem insertString(getLength(), ...) nicht auftreten.
            throw new IllegalStateException(ex);
        }

        // Größenbegrenzung, um Speicherverbrauch bei Langläufern zu deckeln.
        final int excess = doc.getLength() - MAX_CHARS;

        if (excess > 0) {
            try {
                doc.remove(0, excess);
            }
            catch (BadLocationException _) {
                // Empty
            }
        }

        // Auto-Scroll ans Ende.
        textComponent.setCaretPosition(doc.getLength());
    }

    private void flushBuffer() {
        final String text = buffer.toString();
        buffer.setLength(0);

        // NICHT direkt auf dem aufrufenden Thread ins Document schreiben!
        SwingUtilities.invokeLater(() -> appendToDocument(text));
    }
}
