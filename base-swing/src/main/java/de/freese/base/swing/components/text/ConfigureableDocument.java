package de.freese.base.swing.components.text;

import java.awt.Toolkit;
import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.core.processor.AbstractProcessor;
import de.freese.base.core.processor.ProcessorChain;

/**
 * Konfigurierbares Textdocument, fuer eine bestimmte Anzahl von Zeichen, nur Zahlen, uppercase fuer Text etc.
 *
 * @author Thomas Freese
 */
public class ConfigureableDocument extends PlainDocument
{
    /**
     * Processor zur Pruefung ob nur Zahlen, ',' oder '.' eingegeben wurden.
     *
     * @author Thomas Freese
     */
    private static class DigitProcessor extends AbstractProcessor<DocumentContext>
    {
        // /**
        // *
        // */
        // private static final DecimalFormat FORMATTER = new DecimalFormat("###,###,##0");

        /**
         * Angabe, Gleitkommazahlen erlaubt sind.
         */
        private boolean floatsAllowed;

        /**
         * Creates a new {@link DigitProcessor} object.
         */
        public DigitProcessor()
        {
            super();

            setEnabled(false);
        }

        /**
         * Liefert die Angabe, ob Zahlen mit Nachkommastellen erlaubt sind.
         *
         * @return <code>true</code> wenn ja, sonst <code>false</code>
         */
        private boolean areFloatsAllowed()
        {
            return this.floatsAllowed;
        }

        /**
         * @param context {@link DocumentContext}
         * @throws Exception Falls was schief geht.
         * @see de.freese.base.core.processor.Processor#execute(java.lang.Object)
         */
        @Override
        public void execute(final DocumentContext context) throws Exception
        {
            String fullText = context.fullText;

            String regex = "[-]?[0-9]+";

            if (areFloatsAllowed() && (".".equals(context.newText) || ",".equals(context.newText)))
            {
                return;
            }

            if (areFloatsAllowed())
            {
                regex += "([,.][0-9]+)?";
            }

            Pattern pattern = Pattern.compile(regex);

            if (!pattern.matcher(fullText).matches())
            {
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
         *
         * @param floatsAllowed boolean
         */
        public void setFloatsAllowed(final boolean floatsAllowed)
        {
            this.floatsAllowed = floatsAllowed;
        }
    }

    /**
     * ProcessorContext des Documents.
     *
     * @author Thomas Freese
     */
    private static class DocumentContext
    {
        /**
         *
         */
        public String currentText;

        /**
         *
         */
        public String fullText;

        /**
         *
         */
        public String newText;
    }

    /**
     * Processor zur Pruefung der Laenge.
     *
     * @author Thomas Freese
     */
    private static class LengthProcessor extends AbstractProcessor<DocumentContext>
    {
        /**
         *
         */
        private int maxLength = Integer.MAX_VALUE;

        /**
         * Creates a new {@link LengthProcessor} object.
         */
        public LengthProcessor()
        {
            super();

            setEnabled(false);
        }

        /**
         * @param context {@link DocumentContext}
         * @throws Exception Falls was schief geht.
         * @see de.freese.base.core.processor.Processor#execute(java.lang.Object)
         */
        @Override
        public void execute(final DocumentContext context) throws Exception
        {
            String fullText = context.fullText;

            if (fullText.length() > this.maxLength)
            {
                throw new IllegalStateException("Max. lenght of Document reached !");
            }
        }

        /**
         * Max. Laenge des Dokumentes.
         *
         * @param maxLength int
         */
        public void setMaxLength(final int maxLength)
        {
            this.maxLength = maxLength;
        }
    }

    /**
     * Processor fuer UpperCase der Texte..
     *
     * @author Thomas Freese
     */
    private static class UpperCaseProcessor extends AbstractProcessor<DocumentContext>
    {
        /**
         * Creates a new {@link UpperCaseProcessor} object.
         */
        public UpperCaseProcessor()
        {
            super();

            setEnabled(false);
        }

        /**
         * @param context {@link DocumentContext}
         * @throws Exception Falls was schief geht.
         * @see de.freese.base.core.processor.Processor#execute(java.lang.Object)
         */
        @Override
        public void execute(final DocumentContext context) throws Exception
        {
            context.newText = context.newText.toUpperCase();
        }
    }

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureableDocument.class);

    /**
     *
     */
    private static final long serialVersionUID = 4200946186651706734L;

    /**
     *
     */
    private final DigitProcessor digitProcessor;

    /**
     *
     */
    private final LengthProcessor lengthProcessor;

    /**
     *
     */
    private final ProcessorChain<DocumentContext> processorChain = new ProcessorChain<>();

    /**
     *
     */
    private final UpperCaseProcessor upperCaseProcessor;

    /**
     * Creates a new {@link ConfigureableDocument} object.
     */
    public ConfigureableDocument()
    {
        super();

        // Sorgt dafuer das \n als Zeilenumbruch verarbeitet wird und nicht als
        // normaler Text.
        putProperty("filterNewlines", Boolean.TRUE);

        // Laengenueberpruefung
        this.lengthProcessor = new LengthProcessor();
        this.processorChain.addProcessor(this.lengthProcessor);

        // Zahlenpruefung
        this.digitProcessor = new DigitProcessor();
        this.processorChain.addProcessor(this.digitProcessor);

        // UpperCase
        this.upperCaseProcessor = new UpperCaseProcessor();
        this.processorChain.addProcessor(this.upperCaseProcessor);
    }

    /**
     * Behandeln von Eingabefehlern.
     *
     * @param ex {@link Exception}
     */
    protected void handleException(final Exception ex)
    {
        Toolkit.getDefaultToolkit().beep();
        LOGGER.warn(null, ex);
    }

    /**
     * @see javax.swing.text.Document#insertString(int, java.lang.String, javax.swing.text.AttributeSet)
     */
    @Override
    public void insertString(final int offs, final String str, final AttributeSet a) throws BadLocationException
    {
        if (str == null)
        {
            return;
        }

        DocumentContext context = new DocumentContext();
        context.currentText = getText(0, getLength());
        context.newText = str;
        context.fullText = context.currentText + context.newText;

        if (offs < getLength())
        {
            // Wenn neuer Text nicht am Ende eingegeben wurde
            StringBuilder sb = new StringBuilder(context.currentText);
            sb.insert(offs, context.newText);

            context.fullText = sb.toString();
        }

        try
        {
            this.processorChain.execute(context);

            super.insertString(offs, context.newText, a);
        }
        catch (Exception ex)
        {
            handleException(ex);
        }
    }

    /**
     * Setz, ob Zahlen mit Nachkommastellen erlaubt sind.
     *
     * @param floatDigits boolean
     */
    public void setFloatAllowed(final boolean floatDigits)
    {
        this.digitProcessor.setFloatsAllowed(floatDigits);
    }

    /**
     * Max. Anzahl von Zeichen.
     *
     * @param maxLength int
     */
    public void setMaxLength(final int maxLength)
    {
        this.lengthProcessor.setMaxLength(maxLength);
        this.lengthProcessor.setEnabled(true);
    }

    /**
     * Nur Zahlen zulassen.
     *
     * @param onlyDigits boolean
     */
    public void setOnlyDigits(final boolean onlyDigits)
    {
        this.digitProcessor.setEnabled(onlyDigits);
    }

    /**
     * Texte in Grossbuchstaben verwandeln.
     *
     * @param toUpperCase boolean
     */
    public void setToUpperCase(final boolean toUpperCase)
    {
        this.upperCaseProcessor.setEnabled(toUpperCase);
    }
}
