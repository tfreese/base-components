package de.freese.base.swing.components.text;

import java.awt.Toolkit;
import java.io.Serial;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.core.processor.AbstractProcessor;
import de.freese.base.core.processor.ProcessorChain;

/**
 * Konfigurierbares Text-Dokument, für eine bestimmte Anzahl von Zeichen, nur Zahlen, uppercase für Text etc.
 *
 * @author Thomas Freese
 */
public class ConfigureableDocument extends PlainDocument {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureableDocument.class);

    @Serial
    private static final long serialVersionUID = 4200946186651706734L;

    private static final class DigitProcessor extends AbstractProcessor<DocumentContext> {
        // private static final DecimalFormat FORMATTER = new DecimalFormat("###,###,##0");

        private boolean floatsAllowed;

        DigitProcessor() {
            super();

            setEnabled(false);
        }

        @Override
        public void execute(final DocumentContext context) {
            final String fullText = context.fullText;

            String regex = "[-]?[0-9]+";

            if (areFloatsAllowed() && (".".equals(context.newText) || ",".equals(context.newText))) {
                return;
            }

            if (areFloatsAllowed()) {
                regex += "([,.][0-9]+)?";
            }

            final Pattern pattern = Pattern.compile(regex);

            if (!pattern.matcher(fullText).matches()) {
                throw new IllegalStateException("Text is not number");
            }

            // for (int i = 0; i < newText.length(); i++)
            // {
            // char c = newText.charAt(i);
            //
            // if (!Character.isDigit(c) && (c != '-'))
            // {
            // throw new IllegalStateException("Char " + newText.charAt(i)
            // + " ist not a digit !");
            // }
            //
            // if (!areFloatsAllowed() && ((c == '.') || (c == ',')))
            // {
            // throw new IllegalStateException("Char " + newText.charAt(i)
            // + " ist not a digit !");
            // }
            // }

            // FORMATTER.parse(currentText + newText);
        }

        /**
         * Setzt, ob Zahlen mit Nachkommastellen erlaubt sind.
         */
        public void setFloatsAllowed(final boolean floatsAllowed) {
            this.floatsAllowed = floatsAllowed;
        }

        /**
         * Liefert die Angabe, ob Zahlen mit Nachkommastellen erlaubt sind.
         *
         * @return <code>true</code> wenn ja, sonst <code>false</code>
         */
        private boolean areFloatsAllowed() {
            return floatsAllowed;
        }
    }

    /**
     * @author Thomas Freese
     */
    private static final class DocumentContext {
        private String currentText;
        private String fullText;
        private String newText;

        public String getCurrentText() {
            return currentText;
        }

        public String getFullText() {
            return fullText;
        }

        public String getNewText() {
            return newText;
        }

        public void setCurrentText(final String currentText) {
            this.currentText = currentText;
        }

        public void setFullText(final String fullText) {
            this.fullText = fullText;
        }

        public void setNewText(final String newText) {
            this.newText = newText;
        }
    }

    /**
     * @author Thomas Freese
     */
    private static final class LengthProcessor extends AbstractProcessor<DocumentContext> {
        private int maxLength = Integer.MAX_VALUE;

        LengthProcessor() {
            super();

            setEnabled(false);
        }

        @Override
        public void execute(final DocumentContext context) {
            final String fullText = context.fullText;

            if (fullText.length() > maxLength) {
                throw new IllegalStateException("Max. length of Document reached !");
            }
        }

        public void setMaxLength(final int maxLength) {
            this.maxLength = maxLength;
        }
    }

    /**
     * @author Thomas Freese
     */
    private static final class UpperCaseProcessor extends AbstractProcessor<DocumentContext> {
        UpperCaseProcessor() {
            super();

            setEnabled(false);
        }

        @Override
        public void execute(final DocumentContext context) {
            context.newText = context.newText.toUpperCase();
        }
    }

    private final transient DigitProcessor digitProcessor;
    private final transient LengthProcessor lengthProcessor;
    private final transient ProcessorChain<DocumentContext> processorChain = new ProcessorChain<>();
    private final transient UpperCaseProcessor upperCaseProcessor;

    public ConfigureableDocument() {
        super();

        // Sorgt dafür das \n als Zeilenumbruch verarbeitet wird und nicht als
        // normaler Text.
        putProperty("filterNewlines", Boolean.TRUE);

        // Längenüberprüfung
        lengthProcessor = new LengthProcessor();
        processorChain.addProcessor(lengthProcessor);

        // Zahlenprüfung
        digitProcessor = new DigitProcessor();
        processorChain.addProcessor(digitProcessor);

        // UpperCase
        upperCaseProcessor = new UpperCaseProcessor();
        processorChain.addProcessor(upperCaseProcessor);
    }

    @Override
    public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException {
        if (str == null) {
            return;
        }

        final DocumentContext context = new DocumentContext();
        context.currentText = getText(0, getLength());
        context.newText = str;
        context.fullText = context.currentText + context.newText;

        if (offs < getLength()) {
            // Wenn neuer Text nicht am Ende eingegeben wurde
            final StringBuilder sb = new StringBuilder(context.currentText);
            sb.insert(offs, context.newText);

            context.fullText = sb.toString();
        }

        try {
            processorChain.execute(context);

            super.insertString(offs, context.newText, a);
        }
        catch (Exception ex) {
            handleException(ex);
        }
    }

    public void setFloatAllowed(final boolean floatDigits) {
        digitProcessor.setFloatsAllowed(floatDigits);
    }

    public void setMaxLength(final int maxLength) {
        lengthProcessor.setMaxLength(maxLength);
        lengthProcessor.setEnabled(true);
    }

    public void setOnlyDigits(final boolean onlyDigits) {
        digitProcessor.setEnabled(onlyDigits);
    }

    public void setToUpperCase(final boolean toUpperCase) {
        upperCaseProcessor.setEnabled(toUpperCase);
    }

    protected void handleException(final Exception ex) {
        Toolkit.getDefaultToolkit().beep();
        LOGGER.warn(null, ex);
    }
}
